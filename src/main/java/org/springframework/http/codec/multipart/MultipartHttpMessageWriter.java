/*     */ package org.springframework.http.codec.multipart;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.function.Supplier;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.ResolvableTypeProvider;
/*     */ import org.springframework.core.codec.CharSequenceEncoder;
/*     */ import org.springframework.core.codec.CodecException;
/*     */ import org.springframework.core.codec.Encoder;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.io.buffer.PooledDataBuffer;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.HttpEntity;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ReactiveHttpOutputMessage;
/*     */ import org.springframework.http.codec.EncoderHttpMessageWriter;
/*     */ import org.springframework.http.codec.FormHttpMessageWriter;
/*     */ import org.springframework.http.codec.HttpMessageWriter;
/*     */ import org.springframework.http.codec.ResourceHttpMessageWriter;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ public class MultipartHttpMessageWriter
/*     */   extends MultipartWriterSupport
/*     */   implements HttpMessageWriter<MultiValueMap<String, ?>>
/*     */ {
/*  80 */   private static final Map<String, Object> DEFAULT_HINTS = Hints.from(Hints.SUPPRESS_LOGGING_HINT, Boolean.valueOf(true));
/*     */ 
/*     */ 
/*     */   
/*     */   private final List<HttpMessageWriter<?>> partWriters;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final HttpMessageWriter<MultiValueMap<String, String>> formWriter;
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipartHttpMessageWriter() {
/*  93 */     this(Arrays.asList((HttpMessageWriter<?>[])new HttpMessageWriter[] { (HttpMessageWriter)new EncoderHttpMessageWriter(
/*  94 */               (Encoder)CharSequenceEncoder.textPlainOnly()), (HttpMessageWriter)new ResourceHttpMessageWriter() }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipartHttpMessageWriter(List<HttpMessageWriter<?>> partWriters) {
/* 103 */     this(partWriters, (HttpMessageWriter<MultiValueMap<String, String>>)new FormHttpMessageWriter());
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
/*     */   public MultipartHttpMessageWriter(List<HttpMessageWriter<?>> partWriters, @Nullable HttpMessageWriter<MultiValueMap<String, String>> formWriter) {
/* 116 */     super(initMediaTypes(formWriter));
/* 117 */     this.partWriters = partWriters;
/* 118 */     this.formWriter = formWriter;
/*     */   }
/*     */   
/*     */   private static List<MediaType> initMediaTypes(@Nullable HttpMessageWriter<?> formWriter) {
/* 122 */     List<MediaType> result = new ArrayList<>(MultipartHttpMessageReader.MIME_TYPES);
/* 123 */     if (formWriter != null) {
/* 124 */       result.addAll(formWriter.getWritableMediaTypes());
/*     */     }
/* 126 */     return Collections.unmodifiableList(result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HttpMessageWriter<?>> getPartWriters() {
/* 135 */     return Collections.unmodifiableList(this.partWriters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public HttpMessageWriter<MultiValueMap<String, String>> getFormWriter() {
/* 145 */     return this.formWriter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Void> write(Publisher<? extends MultiValueMap<String, ?>> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage outputMessage, Map<String, Object> hints) {
/* 155 */     return Mono.from(inputStream)
/* 156 */       .flatMap(map -> {
/*     */           if (this.formWriter == null || isMultipart(map, mediaType)) {
/*     */             return writeMultipart(map, outputMessage, mediaType, hints);
/*     */           }
/*     */           Mono<MultiValueMap<String, String>> input = Mono.just(map);
/*     */           return this.formWriter.write((Publisher)input, elementType, mediaType, outputMessage, hints);
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isMultipart(MultiValueMap<String, ?> map, @Nullable MediaType contentType) {
/* 169 */     if (contentType != null) {
/* 170 */       return contentType.getType().equalsIgnoreCase("multipart");
/*     */     }
/* 172 */     for (List<?> values : (Iterable<List<?>>)map.values()) {
/* 173 */       for (Object value : values) {
/* 174 */         if (value != null && !(value instanceof String)) {
/* 175 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 179 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Mono<Void> writeMultipart(MultiValueMap<String, ?> map, ReactiveHttpOutputMessage outputMessage, @Nullable MediaType mediaType, Map<String, Object> hints) {
/* 185 */     byte[] boundary = generateMultipartBoundary();
/*     */     
/* 187 */     mediaType = getMultipartMediaType(mediaType, boundary);
/* 188 */     outputMessage.getHeaders().setContentType(mediaType);
/*     */     
/* 190 */     LogFormatUtils.traceDebug(this.logger, traceOn -> Hints.getLogPrefix(hints) + "Encoding " + (isEnableLoggingRequestDetails() ? LogFormatUtils.formatValue(map, !traceOn.booleanValue()) : ("parts " + map.keySet() + " (content masked)")));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 195 */     DataBufferFactory bufferFactory = outputMessage.bufferFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 200 */     Flux<DataBuffer> body = Flux.fromIterable(map.entrySet()).concatMap(entry -> encodePartValues(boundary, (String)entry.getKey(), (List)entry.getValue(), bufferFactory)).concatWith((Publisher)generateLastLine(boundary, bufferFactory)).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
/*     */     
/* 202 */     if (this.logger.isDebugEnabled()) {
/* 203 */       body = body.doOnNext(buffer -> Hints.touchDataBuffer(buffer, hints, this.logger));
/*     */     }
/*     */     
/* 206 */     return outputMessage.writeWith((Publisher)body);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Flux<DataBuffer> encodePartValues(byte[] boundary, String name, List<?> values, DataBufferFactory bufferFactory) {
/* 212 */     return Flux.fromIterable(values)
/* 213 */       .concatMap(value -> encodePart(boundary, name, value, bufferFactory));
/*     */   }
/*     */   private <T> Flux<DataBuffer> encodePart(byte[] boundary, String name, T value, DataBufferFactory factory) {
/*     */     T body;
/*     */     Mono mono;
/* 218 */     MultipartHttpOutputMessage message = new MultipartHttpOutputMessage(factory);
/* 219 */     HttpHeaders headers = message.getHeaders();
/*     */ 
/*     */     
/* 222 */     ResolvableType resolvableType = null;
/* 223 */     if (value instanceof HttpEntity) {
/* 224 */       HttpEntity<T> httpEntity = (HttpEntity<T>)value;
/* 225 */       headers.putAll((Map)httpEntity.getHeaders());
/* 226 */       body = (T)httpEntity.getBody();
/* 227 */       Assert.state((body != null), "MultipartHttpMessageWriter only supports HttpEntity with body");
/* 228 */       if (httpEntity instanceof ResolvableTypeProvider) {
/* 229 */         resolvableType = ((ResolvableTypeProvider)httpEntity).getResolvableType();
/*     */       }
/*     */     } else {
/*     */       
/* 233 */       body = value;
/*     */     } 
/* 235 */     if (resolvableType == null) {
/* 236 */       resolvableType = ResolvableType.forClass(body.getClass());
/*     */     }
/*     */     
/* 239 */     if (!headers.containsKey("Content-Disposition")) {
/* 240 */       if (body instanceof Resource) {
/* 241 */         headers.setContentDispositionFormData(name, ((Resource)body).getFilename());
/*     */       }
/* 243 */       else if (resolvableType.resolve() == Resource.class) {
/* 244 */         mono = Mono.from((Publisher)body).doOnNext(o -> headers.setContentDispositionFormData(name, ((Resource)o).getFilename()));
/*     */       }
/*     */       else {
/*     */         
/* 248 */         headers.setContentDispositionFormData(name, null);
/*     */       } 
/*     */     }
/*     */     
/* 252 */     MediaType contentType = headers.getContentType();
/*     */     
/* 254 */     ResolvableType finalBodyType = resolvableType;
/*     */ 
/*     */     
/* 257 */     Optional<HttpMessageWriter<?>> writer = this.partWriters.stream().filter(partWriter -> partWriter.canWrite(finalBodyType, contentType)).findFirst();
/*     */     
/* 259 */     if (!writer.isPresent()) {
/* 260 */       return Flux.error((Throwable)new CodecException("No suitable writer found for part: " + name));
/*     */     }
/*     */ 
/*     */     
/* 264 */     Publisher<T> bodyPublisher = (mono instanceof Publisher) ? (Publisher<T>)mono : (Publisher<T>)Mono.just(mono);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 270 */     Mono<Void> partContentReady = ((HttpMessageWriter)writer.get()).write(bodyPublisher, resolvableType, contentType, message, DEFAULT_HINTS);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 275 */     Flux<DataBuffer> partContent = partContentReady.thenMany((Publisher)Flux.defer(message::getBody));
/*     */     
/* 277 */     return Flux.concat(new Publisher[] { (Publisher)
/* 278 */           generateBoundaryLine(boundary, factory), (Publisher)partContent, (Publisher)
/*     */           
/* 280 */           generateNewLine(factory) });
/*     */   }
/*     */ 
/*     */   
/*     */   private class MultipartHttpOutputMessage
/*     */     implements ReactiveHttpOutputMessage
/*     */   {
/*     */     private final DataBufferFactory bufferFactory;
/* 288 */     private final HttpHeaders headers = new HttpHeaders();
/*     */     
/* 290 */     private final AtomicBoolean committed = new AtomicBoolean();
/*     */     
/*     */     @Nullable
/*     */     private Flux<DataBuffer> body;
/*     */     
/*     */     public MultipartHttpOutputMessage(DataBufferFactory bufferFactory) {
/* 296 */       this.bufferFactory = bufferFactory;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHeaders getHeaders() {
/* 301 */       return (this.body != null) ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
/*     */     }
/*     */ 
/*     */     
/*     */     public DataBufferFactory bufferFactory() {
/* 306 */       return this.bufferFactory;
/*     */     }
/*     */ 
/*     */     
/*     */     public void beforeCommit(Supplier<? extends Mono<Void>> action) {
/* 311 */       this.committed.set(true);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isCommitted() {
/* 316 */       return this.committed.get();
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
/* 321 */       if (this.body != null) {
/* 322 */         return Mono.error(new IllegalStateException("Multiple calls to writeWith() not supported"));
/*     */       }
/* 324 */       this.body = MultipartHttpMessageWriter.this.generatePartHeaders(this.headers, this.bufferFactory).concatWith(body);
/*     */ 
/*     */       
/* 327 */       return Mono.empty();
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
/* 332 */       return Mono.error(new UnsupportedOperationException());
/*     */     }
/*     */     
/*     */     public Flux<DataBuffer> getBody() {
/* 336 */       return (this.body != null) ? this.body : 
/* 337 */         Flux.error(new IllegalStateException("Body has not been written yet"));
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> setComplete() {
/* 342 */       return Mono.error(new UnsupportedOperationException());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/MultipartHttpMessageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */