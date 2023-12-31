/*     */ package org.springframework.http.codec.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import com.fasterxml.jackson.databind.ObjectReader;
/*     */ import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
/*     */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.CodecException;
/*     */ import org.springframework.core.codec.DecodingException;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.codec.HttpMessageDecoder;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MimeType;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.core.publisher.SynchronousSink;
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
/*     */ 
/*     */ public abstract class AbstractJackson2Decoder
/*     */   extends Jackson2CodecSupport
/*     */   implements HttpMessageDecoder<Object>
/*     */ {
/*  66 */   private int maxInMemorySize = 262144;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractJackson2Decoder(ObjectMapper mapper, MimeType... mimeTypes) {
/*  73 */     super(mapper, mimeTypes);
/*     */   }
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
/*     */   public void setMaxInMemorySize(int byteCount) {
/*  87 */     this.maxInMemorySize = byteCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxInMemorySize() {
/*  95 */     return this.maxInMemorySize;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
/* 101 */     ObjectMapper mapper = selectObjectMapper(elementType, mimeType);
/* 102 */     if (mapper == null) {
/* 103 */       return false;
/*     */     }
/* 105 */     JavaType javaType = mapper.constructType(elementType.getType());
/*     */     
/* 107 */     if (CharSequence.class.isAssignableFrom(elementType.toClass()) || !supportsMimeType(mimeType)) {
/* 108 */       return false;
/*     */     }
/* 110 */     if (!this.logger.isDebugEnabled()) {
/* 111 */       return mapper.canDeserialize(javaType);
/*     */     }
/*     */     
/* 114 */     AtomicReference<Throwable> causeRef = new AtomicReference<>();
/* 115 */     if (mapper.canDeserialize(javaType, causeRef)) {
/* 116 */       return true;
/*     */     }
/* 118 */     logWarningIfNecessary((Type)javaType, causeRef.get());
/* 119 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<Object> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 127 */     ObjectMapper mapper = selectObjectMapper(elementType, mimeType);
/* 128 */     if (mapper == null) {
/* 129 */       throw new IllegalStateException("No ObjectMapper for " + elementType);
/*     */     }
/*     */     
/* 132 */     boolean forceUseOfBigDecimal = mapper.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
/* 133 */     if (BigDecimal.class.equals(elementType.getType())) {
/* 134 */       forceUseOfBigDecimal = true;
/*     */     }
/*     */     
/* 137 */     Flux<DataBuffer> processed = processInput(input, elementType, mimeType, hints);
/* 138 */     Flux<TokenBuffer> tokens = Jackson2Tokenizer.tokenize(processed, mapper.getFactory(), mapper, true, forceUseOfBigDecimal, 
/* 139 */         getMaxInMemorySize());
/*     */     
/* 141 */     ObjectReader reader = getObjectReader(mapper, elementType, hints);
/*     */     
/* 143 */     return tokens.handle((tokenBuffer, sink) -> {
/*     */           try {
/*     */             Object value = reader.readValue(tokenBuffer.asParser((ObjectCodec)mapper));
/*     */             
/*     */             logValue(value, hints);
/*     */             if (value != null) {
/*     */               sink.next(value);
/*     */             }
/* 151 */           } catch (IOException ex) {
/*     */             sink.error((Throwable)processException(ex));
/*     */           } 
/*     */         });
/*     */   }
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
/*     */   protected Flux<DataBuffer> processInput(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 171 */     return Flux.from(input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Object> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
/* 178 */     return DataBufferUtils.join(input, this.maxInMemorySize)
/* 179 */       .flatMap(dataBuffer -> Mono.justOrEmpty(decode(dataBuffer, elementType, mimeType, hints)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
/* 186 */     ObjectMapper mapper = selectObjectMapper(targetType, mimeType);
/* 187 */     if (mapper == null) {
/* 188 */       throw new IllegalStateException("No ObjectMapper for " + targetType);
/*     */     }
/*     */     
/*     */     try {
/* 192 */       ObjectReader objectReader = getObjectReader(mapper, targetType, hints);
/* 193 */       Object value = objectReader.readValue(dataBuffer.asInputStream());
/* 194 */       logValue(value, hints);
/* 195 */       return value;
/*     */     }
/* 197 */     catch (IOException ex) {
/* 198 */       throw processException(ex);
/*     */     } finally {
/*     */       
/* 201 */       DataBufferUtils.release(dataBuffer);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ObjectReader getObjectReader(ObjectMapper mapper, ResolvableType elementType, @Nullable Map<String, Object> hints) {
/* 208 */     Assert.notNull(elementType, "'elementType' must not be null");
/* 209 */     Class<?> contextClass = getContextClass(elementType);
/* 210 */     if (contextClass == null && hints != null) {
/* 211 */       contextClass = getContextClass((ResolvableType)hints.get(ACTUAL_TYPE_HINT));
/*     */     }
/* 213 */     JavaType javaType = getJavaType(elementType.getType(), contextClass);
/* 214 */     Class<?> jsonView = (hints != null) ? (Class)hints.get(Jackson2CodecSupport.JSON_VIEW_HINT) : null;
/* 215 */     return (jsonView != null) ? mapper
/* 216 */       .readerWithView(jsonView).forType(javaType) : mapper
/* 217 */       .readerFor(javaType);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private Class<?> getContextClass(@Nullable ResolvableType elementType) {
/* 222 */     MethodParameter param = (elementType != null) ? getParameter(elementType) : null;
/* 223 */     return (param != null) ? param.getContainingClass() : null;
/*     */   }
/*     */   
/*     */   private void logValue(@Nullable Object value, @Nullable Map<String, Object> hints) {
/* 227 */     if (!Hints.isLoggingSuppressed(hints)) {
/* 228 */       LogFormatUtils.traceDebug(this.logger, traceOn -> {
/*     */             String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
/*     */             return Hints.getLogPrefix(hints) + "Decoded [" + formatted + "]";
/*     */           });
/*     */     }
/*     */   }
/*     */   
/*     */   private CodecException processException(IOException ex) {
/* 236 */     if (ex instanceof InvalidDefinitionException) {
/* 237 */       JavaType type = ((InvalidDefinitionException)ex).getType();
/* 238 */       return new CodecException("Type definition error: " + type, ex);
/*     */     } 
/* 240 */     if (ex instanceof JsonProcessingException) {
/* 241 */       String originalMessage = ((JsonProcessingException)ex).getOriginalMessage();
/* 242 */       return (CodecException)new DecodingException("JSON decoding error: " + originalMessage, ex);
/*     */     } 
/* 244 */     return (CodecException)new DecodingException("I/O error while parsing input stream", ex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Object> getDecodeHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response) {
/* 254 */     return getHints(actualType);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MimeType> getDecodableMimeTypes() {
/* 259 */     return getMimeTypes();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MimeType> getDecodableMimeTypes(ResolvableType targetType) {
/* 264 */     return getMimeTypes(targetType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected <A extends java.lang.annotation.Annotation> A getAnnotation(MethodParameter parameter, Class<A> annotType) {
/* 271 */     return (A)parameter.getParameterAnnotation(annotType);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/json/AbstractJackson2Decoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */