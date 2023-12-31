/*     */ package org.springframework.http.codec;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.AbstractDecoder;
/*     */ import org.springframework.core.codec.Decoder;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.http.HttpLogging;
/*     */ import org.springframework.http.HttpMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ReactiveHttpInputMessage;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DecoderHttpMessageReader<T>
/*     */   implements HttpMessageReader<T>
/*     */ {
/*     */   private final Decoder<T> decoder;
/*     */   private final List<MediaType> mediaTypes;
/*     */   
/*     */   public DecoderHttpMessageReader(Decoder<T> decoder) {
/*  63 */     Assert.notNull(decoder, "Decoder is required");
/*  64 */     initLogger(decoder);
/*  65 */     this.decoder = decoder;
/*  66 */     this.mediaTypes = MediaType.asMediaTypes(decoder.getDecodableMimeTypes());
/*     */   }
/*     */   
/*     */   private static void initLogger(Decoder<?> decoder) {
/*  70 */     if (decoder instanceof AbstractDecoder && decoder
/*  71 */       .getClass().getName().startsWith("org.springframework.core.codec")) {
/*  72 */       Log logger = HttpLogging.forLog(((AbstractDecoder)decoder).getLogger());
/*  73 */       ((AbstractDecoder)decoder).setLogger(logger);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Decoder<T> getDecoder() {
/*  82 */     return this.decoder;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getReadableMediaTypes() {
/*  87 */     return this.mediaTypes;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getReadableMediaTypes(ResolvableType elementType) {
/*  92 */     return MediaType.asMediaTypes(this.decoder.getDecodableMimeTypes(elementType));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
/*  97 */     return this.decoder.canDecode(elementType, (MimeType)mediaType);
/*     */   }
/*     */ 
/*     */   
/*     */   public Flux<T> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 102 */     MediaType contentType = getContentType((HttpMessage)message);
/* 103 */     Map<String, Object> allHints = Hints.merge(hints, getReadHints(elementType, message));
/* 104 */     return this.decoder.decode((Publisher)message.getBody(), elementType, (MimeType)contentType, allHints);
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<T> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 109 */     MediaType contentType = getContentType((HttpMessage)message);
/* 110 */     Map<String, Object> allHints = Hints.merge(hints, getReadHints(elementType, message));
/* 111 */     return this.decoder.decodeToMono((Publisher)message.getBody(), elementType, (MimeType)contentType, allHints);
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
/*     */   protected MediaType getContentType(HttpMessage inputMessage) {
/* 123 */     MediaType contentType = inputMessage.getHeaders().getContentType();
/* 124 */     return (contentType != null) ? contentType : MediaType.APPLICATION_OCTET_STREAM;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, Object> getReadHints(ResolvableType elementType, ReactiveHttpInputMessage message) {
/* 132 */     return Hints.none();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<T> read(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
/* 142 */     Map<String, Object> allHints = Hints.merge(hints, 
/* 143 */         getReadHints(actualType, elementType, request, response));
/*     */     
/* 145 */     return read(elementType, (ReactiveHttpInputMessage)request, allHints);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<T> readMono(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
/* 152 */     Map<String, Object> allHints = Hints.merge(hints, 
/* 153 */         getReadHints(actualType, elementType, request, response));
/*     */     
/* 155 */     return readMono(elementType, (ReactiveHttpInputMessage)request, allHints);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, Object> getReadHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response) {
/* 166 */     if (this.decoder instanceof HttpMessageDecoder) {
/* 167 */       HttpMessageDecoder<?> decoder = (HttpMessageDecoder)this.decoder;
/* 168 */       return decoder.getDecodeHints(actualType, elementType, request, response);
/*     */     } 
/* 170 */     return Hints.none();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/DecoderHttpMessageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */