/*     */ package org.springframework.http.codec.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonEncoding;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import com.fasterxml.jackson.databind.ObjectWriter;
/*     */ import com.fasterxml.jackson.databind.SequenceWriter;
/*     */ import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
/*     */ import com.fasterxml.jackson.databind.ser.FilterProvider;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.CodecException;
/*     */ import org.springframework.core.codec.EncodingException;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.codec.HttpMessageEncoder;
/*     */ import org.springframework.http.converter.json.MappingJacksonValue;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.MimeType;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractJackson2Encoder
/*     */   extends Jackson2CodecSupport
/*     */   implements HttpMessageEncoder<Object>
/*     */ {
/*  71 */   private static final byte[] NEWLINE_SEPARATOR = new byte[] { 10 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   private static final Map<String, JsonEncoding> ENCODINGS = CollectionUtils.newHashMap((JsonEncoding.values()).length); static {
/*  77 */     for (JsonEncoding encoding : JsonEncoding.values()) {
/*  78 */       ENCODINGS.put(encoding.getJavaName(), encoding);
/*     */     }
/*  80 */     ENCODINGS.put("US-ASCII", JsonEncoding.UTF8);
/*     */   }
/*     */ 
/*     */   
/*  84 */   private final List<MediaType> streamingMediaTypes = new ArrayList<>(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractJackson2Encoder(ObjectMapper mapper, MimeType... mimeTypes) {
/*  91 */     super(mapper, mimeTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStreamingMediaTypes(List<MediaType> mediaTypes) {
/* 100 */     this.streamingMediaTypes.clear();
/* 101 */     this.streamingMediaTypes.addAll(mediaTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
/* 107 */     if (!supportsMimeType(mimeType)) {
/* 108 */       return false;
/*     */     }
/* 110 */     if (mimeType != null && mimeType.getCharset() != null) {
/* 111 */       Charset charset = mimeType.getCharset();
/* 112 */       if (!ENCODINGS.containsKey(charset.name())) {
/* 113 */         return false;
/*     */       }
/*     */     } 
/* 116 */     ObjectMapper mapper = selectObjectMapper(elementType, mimeType);
/* 117 */     if (mapper == null) {
/* 118 */       return false;
/*     */     }
/* 120 */     Class<?> clazz = elementType.toClass();
/* 121 */     if (String.class.isAssignableFrom(elementType.resolve(clazz))) {
/* 122 */       return false;
/*     */     }
/* 124 */     if (Object.class == clazz) {
/* 125 */       return true;
/*     */     }
/* 127 */     if (!this.logger.isDebugEnabled()) {
/* 128 */       return mapper.canSerialize(clazz);
/*     */     }
/*     */     
/* 131 */     AtomicReference<Throwable> causeRef = new AtomicReference<>();
/* 132 */     if (mapper.canSerialize(clazz, causeRef)) {
/* 133 */       return true;
/*     */     }
/* 135 */     logWarningIfNecessary(clazz, causeRef.get());
/* 136 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 144 */     Assert.notNull(inputStream, "'inputStream' must not be null");
/* 145 */     Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
/* 146 */     Assert.notNull(elementType, "'elementType' must not be null");
/*     */     
/* 148 */     if (inputStream instanceof Mono) {
/* 149 */       return Mono.from(inputStream)
/* 150 */         .map(value -> encodeValue(value, bufferFactory, elementType, mimeType, hints))
/* 151 */         .flux();
/*     */     }
/*     */     
/* 154 */     byte[] separator = getStreamingMediaTypeSeparator(mimeType);
/* 155 */     if (separator != null) {
/*     */       try {
/* 157 */         ObjectMapper mapper = selectObjectMapper(elementType, mimeType);
/* 158 */         if (mapper == null) {
/* 159 */           throw new IllegalStateException("No ObjectMapper for " + elementType);
/*     */         }
/* 161 */         ObjectWriter writer = createObjectWriter(mapper, elementType, mimeType, (Class<?>)null, hints);
/* 162 */         ByteArrayBuilder byteBuilder = new ByteArrayBuilder(writer.getFactory()._getBufferRecycler());
/* 163 */         JsonEncoding encoding = getJsonEncoding(mimeType);
/* 164 */         JsonGenerator generator = mapper.getFactory().createGenerator((OutputStream)byteBuilder, encoding);
/* 165 */         SequenceWriter sequenceWriter = writer.writeValues(generator);
/*     */         
/* 167 */         return Flux.from(inputStream)
/* 168 */           .map(value -> encodeStreamingValue(value, bufferFactory, hints, sequenceWriter, byteBuilder, separator))
/*     */           
/* 170 */           .doAfterTerminate(() -> {
/*     */               try {
/*     */                 byteBuilder.release();
/*     */                 
/*     */                 generator.close();
/* 175 */               } catch (IOException ex) {
/*     */                 
/*     */                 this.logger.error("Could not close Encoder resources", ex);
/*     */               } 
/*     */             });
/* 180 */       } catch (IOException ex) {
/* 181 */         return Flux.error(ex);
/*     */       } 
/*     */     }
/*     */     
/* 185 */     ResolvableType listType = ResolvableType.forClassWithGenerics(List.class, new ResolvableType[] { elementType });
/* 186 */     return Flux.from(inputStream)
/* 187 */       .collectList()
/* 188 */       .map(list -> encodeValue(list, bufferFactory, listType, mimeType, hints))
/* 189 */       .flux();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 199 */     Class<?> jsonView = null;
/* 200 */     FilterProvider filters = null;
/* 201 */     if (value instanceof MappingJacksonValue) {
/* 202 */       MappingJacksonValue container = (MappingJacksonValue)value;
/* 203 */       value = container.getValue();
/* 204 */       valueType = ResolvableType.forInstance(value);
/* 205 */       jsonView = container.getSerializationView();
/* 206 */       filters = container.getFilters();
/*     */     } 
/*     */     
/* 209 */     ObjectMapper mapper = selectObjectMapper(valueType, mimeType);
/* 210 */     if (mapper == null) {
/* 211 */       throw new IllegalStateException("No ObjectMapper for " + valueType);
/*     */     }
/*     */     
/* 214 */     ObjectWriter writer = createObjectWriter(mapper, valueType, mimeType, jsonView, hints);
/* 215 */     if (filters != null) {
/* 216 */       writer = writer.with(filters);
/*     */     }
/*     */     
/* 219 */     ByteArrayBuilder byteBuilder = new ByteArrayBuilder(writer.getFactory()._getBufferRecycler());
/*     */     try {
/* 221 */       JsonEncoding encoding = getJsonEncoding(mimeType);
/*     */       
/* 223 */       logValue(hints, value);
/*     */       
/* 225 */       try (JsonGenerator generator = mapper.getFactory().createGenerator((OutputStream)byteBuilder, encoding)) {
/* 226 */         writer.writeValue(generator, value);
/* 227 */         generator.flush();
/*     */       }
/* 229 */       catch (InvalidDefinitionException ex) {
/* 230 */         throw new CodecException("Type definition error: " + ex.getType(), ex);
/*     */       }
/* 232 */       catch (JsonProcessingException ex) {
/* 233 */         throw new EncodingException("JSON encoding error: " + ex.getOriginalMessage(), ex);
/*     */       }
/* 235 */       catch (IOException ex) {
/* 236 */         throw new IllegalStateException("Unexpected I/O error while writing to byte array builder", ex);
/*     */       } 
/*     */       
/* 239 */       byte[] bytes = byteBuilder.toByteArray();
/* 240 */       DataBuffer buffer = bufferFactory.allocateBuffer(bytes.length);
/* 241 */       buffer.write(bytes);
/* 242 */       Hints.touchDataBuffer(buffer, hints, this.logger);
/*     */       
/* 244 */       return buffer;
/*     */     } finally {
/*     */       
/* 247 */       byteBuilder.release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private DataBuffer encodeStreamingValue(Object value, DataBufferFactory bufferFactory, @Nullable Map<String, Object> hints, SequenceWriter sequenceWriter, ByteArrayBuilder byteArrayBuilder, byte[] separator) {
/*     */     int offset, length;
/* 254 */     logValue(hints, value);
/*     */     
/*     */     try {
/* 257 */       sequenceWriter.write(value);
/* 258 */       sequenceWriter.flush();
/*     */     }
/* 260 */     catch (InvalidDefinitionException ex) {
/* 261 */       throw new CodecException("Type definition error: " + ex.getType(), ex);
/*     */     }
/* 263 */     catch (JsonProcessingException ex) {
/* 264 */       throw new EncodingException("JSON encoding error: " + ex.getOriginalMessage(), ex);
/*     */     }
/* 266 */     catch (IOException ex) {
/* 267 */       throw new IllegalStateException("Unexpected I/O error while writing to byte array builder", ex);
/*     */     } 
/*     */     
/* 270 */     byte[] bytes = byteArrayBuilder.toByteArray();
/* 271 */     byteArrayBuilder.reset();
/*     */ 
/*     */ 
/*     */     
/* 275 */     if (bytes.length > 0 && bytes[0] == 32) {
/*     */       
/* 277 */       offset = 1;
/* 278 */       length = bytes.length - 1;
/*     */     } else {
/*     */       
/* 281 */       offset = 0;
/* 282 */       length = bytes.length;
/*     */     } 
/* 284 */     DataBuffer buffer = bufferFactory.allocateBuffer(length + separator.length);
/* 285 */     buffer.write(bytes, offset, length);
/* 286 */     buffer.write(separator);
/* 287 */     Hints.touchDataBuffer(buffer, hints, this.logger);
/*     */     
/* 289 */     return buffer;
/*     */   }
/*     */   
/*     */   private void logValue(@Nullable Map<String, Object> hints, Object value) {
/* 293 */     if (!Hints.isLoggingSuppressed(hints)) {
/* 294 */       LogFormatUtils.traceDebug(this.logger, traceOn -> {
/*     */             String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
/*     */             return Hints.getLogPrefix(hints) + "Encoding [" + formatted + "]";
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ObjectWriter createObjectWriter(ObjectMapper mapper, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Class<?> jsonView, @Nullable Map<String, Object> hints) {
/* 305 */     JavaType javaType = getJavaType(valueType.getType(), null);
/* 306 */     if (jsonView == null && hints != null) {
/* 307 */       jsonView = (Class)hints.get(Jackson2CodecSupport.JSON_VIEW_HINT);
/*     */     }
/* 309 */     ObjectWriter writer = (jsonView != null) ? mapper.writerWithView(jsonView) : mapper.writer();
/* 310 */     if (javaType.isContainerType()) {
/* 311 */       writer = writer.forType(javaType);
/*     */     }
/* 313 */     return customizeWriter(writer, mimeType, valueType, hints);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable MimeType mimeType, ResolvableType elementType, @Nullable Map<String, Object> hints) {
/* 319 */     return writer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected byte[] getStreamingMediaTypeSeparator(@Nullable MimeType mimeType) {
/* 331 */     for (MediaType streamingMediaType : this.streamingMediaTypes) {
/* 332 */       if (streamingMediaType.isCompatibleWith(mimeType)) {
/* 333 */         return NEWLINE_SEPARATOR;
/*     */       }
/*     */     } 
/* 336 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonEncoding getJsonEncoding(@Nullable MimeType mimeType) {
/* 346 */     if (mimeType != null && mimeType.getCharset() != null) {
/* 347 */       Charset charset = mimeType.getCharset();
/* 348 */       JsonEncoding result = ENCODINGS.get(charset.name());
/* 349 */       if (result != null) {
/* 350 */         return result;
/*     */       }
/*     */     } 
/* 353 */     return JsonEncoding.UTF8;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<MimeType> getEncodableMimeTypes() {
/* 361 */     return getMimeTypes();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MimeType> getEncodableMimeTypes(ResolvableType elementType) {
/* 366 */     return getMimeTypes(elementType);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getStreamingMediaTypes() {
/* 371 */     return Collections.unmodifiableList(this.streamingMediaTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Object> getEncodeHints(@Nullable ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
/* 378 */     return (actualType != null) ? getHints(actualType) : Hints.none();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected <A extends java.lang.annotation.Annotation> A getAnnotation(MethodParameter parameter, Class<A> annotType) {
/* 386 */     return (A)parameter.getMethodAnnotation(annotType);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/json/AbstractJackson2Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */