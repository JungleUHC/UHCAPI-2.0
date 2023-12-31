/*     */ package org.springframework.http.codec;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.AbstractEncoder;
/*     */ import org.springframework.core.codec.Encoder;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.io.buffer.PooledDataBuffer;
/*     */ import org.springframework.http.HttpLogging;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ReactiveHttpOutputMessage;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MimeType;
/*     */ import org.springframework.util.StringUtils;
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
/*     */ public class EncoderHttpMessageWriter<T>
/*     */   implements HttpMessageWriter<T>
/*     */ {
/*  60 */   private static final Log logger = HttpLogging.forLogName(EncoderHttpMessageWriter.class);
/*     */ 
/*     */   
/*     */   private final Encoder<T> encoder;
/*     */ 
/*     */   
/*     */   private final List<MediaType> mediaTypes;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final MediaType defaultMediaType;
/*     */ 
/*     */ 
/*     */   
/*     */   public EncoderHttpMessageWriter(Encoder<T> encoder) {
/*  75 */     Assert.notNull(encoder, "Encoder is required");
/*  76 */     initLogger(encoder);
/*  77 */     this.encoder = encoder;
/*  78 */     this.mediaTypes = MediaType.asMediaTypes(encoder.getEncodableMimeTypes());
/*  79 */     this.defaultMediaType = initDefaultMediaType(this.mediaTypes);
/*     */   }
/*     */   
/*     */   private static void initLogger(Encoder<?> encoder) {
/*  83 */     if (encoder instanceof AbstractEncoder && encoder
/*  84 */       .getClass().getName().startsWith("org.springframework.core.codec")) {
/*  85 */       Log logger = HttpLogging.forLog(((AbstractEncoder)encoder).getLogger());
/*  86 */       ((AbstractEncoder)encoder).setLogger(logger);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static MediaType initDefaultMediaType(List<MediaType> mediaTypes) {
/*  92 */     return mediaTypes.stream().filter(MimeType::isConcrete).findFirst().orElse(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Encoder<T> getEncoder() {
/* 100 */     return this.encoder;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getWritableMediaTypes() {
/* 105 */     return this.mediaTypes;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getWritableMediaTypes(ResolvableType elementType) {
/* 110 */     return MediaType.asMediaTypes(getEncoder().getEncodableMimeTypes(elementType));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
/* 115 */     return this.encoder.canEncode(elementType, (MimeType)mediaType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
/* 122 */     MediaType contentType = updateContentType(message, mediaType);
/*     */     
/* 124 */     Flux<DataBuffer> body = this.encoder.encode(inputStream, message
/* 125 */         .bufferFactory(), elementType, (MimeType)contentType, hints);
/*     */     
/* 127 */     if (inputStream instanceof Mono) {
/* 128 */       return body
/* 129 */         .singleOrEmpty()
/* 130 */         .switchIfEmpty(Mono.defer(() -> {
/*     */               message.getHeaders().setContentLength(0L);
/*     */               
/*     */               return message.setComplete().then(Mono.empty());
/* 134 */             })).flatMap(buffer -> {
/*     */             Hints.touchDataBuffer(buffer, hints, logger);
/*     */             
/*     */             message.getHeaders().setContentLength(buffer.readableByteCount());
/*     */             
/*     */             return message.writeWith((Publisher)Mono.just(buffer).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release));
/* 140 */           }).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
/*     */     }
/*     */     
/* 143 */     if (isStreamingMediaType(contentType)) {
/* 144 */       return message.writeAndFlushWith((Publisher)body.map(buffer -> {
/*     */               Hints.touchDataBuffer(buffer, hints, logger);
/*     */               
/*     */               return Mono.just(buffer).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
/*     */             }));
/*     */     }
/* 150 */     if (logger.isDebugEnabled()) {
/* 151 */       body = body.doOnNext(buffer -> Hints.touchDataBuffer(buffer, hints, logger));
/*     */     }
/* 153 */     return message.writeWith((Publisher)body);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private MediaType updateContentType(ReactiveHttpOutputMessage message, @Nullable MediaType mediaType) {
/* 158 */     MediaType result = message.getHeaders().getContentType();
/* 159 */     if (result != null) {
/* 160 */       return result;
/*     */     }
/* 162 */     MediaType fallback = this.defaultMediaType;
/* 163 */     result = useFallback(mediaType, fallback) ? fallback : mediaType;
/* 164 */     if (result != null) {
/* 165 */       result = addDefaultCharset(result, fallback);
/* 166 */       message.getHeaders().setContentType(result);
/*     */     } 
/* 168 */     return result;
/*     */   }
/*     */   
/*     */   private static boolean useFallback(@Nullable MediaType main, @Nullable MediaType fallback) {
/* 172 */     return (main == null || !main.isConcrete() || (main
/* 173 */       .equals(MediaType.APPLICATION_OCTET_STREAM) && fallback != null));
/*     */   }
/*     */   
/*     */   private static MediaType addDefaultCharset(MediaType main, @Nullable MediaType defaultType) {
/* 177 */     if (main.getCharset() == null && defaultType != null && defaultType.getCharset() != null) {
/* 178 */       return new MediaType(main, defaultType.getCharset());
/*     */     }
/* 180 */     return main;
/*     */   }
/*     */   
/*     */   private boolean isStreamingMediaType(@Nullable MediaType mediaType) {
/* 184 */     if (mediaType == null || !(this.encoder instanceof HttpMessageEncoder)) {
/* 185 */       return false;
/*     */     }
/* 187 */     for (MediaType streamingMediaType : ((HttpMessageEncoder)this.encoder).getStreamingMediaTypes()) {
/* 188 */       if (mediaType.isCompatibleWith(streamingMediaType) && matchParameters(mediaType, streamingMediaType)) {
/* 189 */         return true;
/*     */       }
/*     */     } 
/* 192 */     return false;
/*     */   }
/*     */   
/*     */   private boolean matchParameters(MediaType streamingMediaType, MediaType mediaType) {
/* 196 */     for (String name : streamingMediaType.getParameters().keySet()) {
/* 197 */       String s1 = streamingMediaType.getParameter(name);
/* 198 */       String s2 = mediaType.getParameter(name);
/* 199 */       if (StringUtils.hasText(s1) && StringUtils.hasText(s2) && !s1.equalsIgnoreCase(s2)) {
/* 200 */         return false;
/*     */       }
/*     */     } 
/* 203 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
/* 214 */     Map<String, Object> allHints = Hints.merge(hints, 
/* 215 */         getWriteHints(actualType, elementType, mediaType, request, response));
/*     */     
/* 217 */     return write(inputStream, elementType, mediaType, (ReactiveHttpOutputMessage)response, allHints);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, Object> getWriteHints(ResolvableType streamType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
/* 228 */     if (this.encoder instanceof HttpMessageEncoder) {
/* 229 */       HttpMessageEncoder<?> encoder = (HttpMessageEncoder)this.encoder;
/* 230 */       return encoder.getEncodeHints(streamType, elementType, mediaType, request, response);
/*     */     } 
/* 232 */     return Hints.none();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/EncoderHttpMessageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */