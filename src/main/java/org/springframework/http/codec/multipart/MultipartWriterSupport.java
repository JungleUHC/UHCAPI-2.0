/*     */ package org.springframework.http.codec.multipart;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.codec.LoggingCodecSupport;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MimeTypeUtils;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ public class MultipartWriterSupport
/*     */   extends LoggingCodecSupport
/*     */ {
/*  47 */   public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
/*     */   
/*     */   private final List<MediaType> supportedMediaTypes;
/*     */   
/*  51 */   private Charset charset = DEFAULT_CHARSET;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MultipartWriterSupport(List<MediaType> supportedMediaTypes) {
/*  58 */     this.supportedMediaTypes = supportedMediaTypes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/*  66 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCharset(Charset charset) {
/*  77 */     Assert.notNull(charset, "Charset must not be null");
/*  78 */     this.charset = charset;
/*     */   }
/*     */   
/*     */   public List<MediaType> getWritableMediaTypes() {
/*  82 */     return this.supportedMediaTypes;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
/*  87 */     if (MultiValueMap.class.isAssignableFrom(elementType.toClass())) {
/*  88 */       if (mediaType == null) {
/*  89 */         return true;
/*     */       }
/*  91 */       for (MediaType supportedMediaType : this.supportedMediaTypes) {
/*  92 */         if (supportedMediaType.isCompatibleWith(mediaType)) {
/*  93 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] generateMultipartBoundary() {
/* 105 */     return MimeTypeUtils.generateMultipartBoundary();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MediaType getMultipartMediaType(@Nullable MediaType mediaType, byte[] boundary) {
/* 114 */     Map<String, String> params = new HashMap<>();
/* 115 */     if (mediaType != null) {
/* 116 */       params.putAll(mediaType.getParameters());
/*     */     }
/* 118 */     params.put("boundary", new String(boundary, StandardCharsets.US_ASCII));
/* 119 */     Charset charset = getCharset();
/* 120 */     if (!charset.equals(StandardCharsets.UTF_8) && 
/* 121 */       !charset.equals(StandardCharsets.US_ASCII)) {
/* 122 */       params.put("charset", charset.name());
/*     */     }
/*     */     
/* 125 */     mediaType = (mediaType != null) ? mediaType : MediaType.MULTIPART_FORM_DATA;
/* 126 */     mediaType = new MediaType(mediaType, params);
/* 127 */     return mediaType;
/*     */   }
/*     */   
/*     */   protected Mono<DataBuffer> generateBoundaryLine(byte[] boundary, DataBufferFactory bufferFactory) {
/* 131 */     return Mono.fromCallable(() -> {
/*     */           DataBuffer buffer = bufferFactory.allocateBuffer(boundary.length + 4);
/*     */           buffer.write((byte)45);
/*     */           buffer.write((byte)45);
/*     */           buffer.write(boundary);
/*     */           buffer.write((byte)13);
/*     */           buffer.write((byte)10);
/*     */           return buffer;
/*     */         });
/*     */   }
/*     */   
/*     */   protected Mono<DataBuffer> generateNewLine(DataBufferFactory bufferFactory) {
/* 143 */     return Mono.fromCallable(() -> {
/*     */           DataBuffer buffer = bufferFactory.allocateBuffer(2);
/*     */           buffer.write((byte)13);
/*     */           buffer.write((byte)10);
/*     */           return buffer;
/*     */         });
/*     */   }
/*     */   
/*     */   protected Mono<DataBuffer> generateLastLine(byte[] boundary, DataBufferFactory bufferFactory) {
/* 152 */     return Mono.fromCallable(() -> {
/*     */           DataBuffer buffer = bufferFactory.allocateBuffer(boundary.length + 6);
/*     */           buffer.write((byte)45);
/*     */           buffer.write((byte)45);
/*     */           buffer.write(boundary);
/*     */           buffer.write((byte)45);
/*     */           buffer.write((byte)45);
/*     */           buffer.write((byte)13);
/*     */           buffer.write((byte)10);
/*     */           return buffer;
/*     */         });
/*     */   }
/*     */   
/*     */   protected Mono<DataBuffer> generatePartHeaders(HttpHeaders headers, DataBufferFactory bufferFactory) {
/* 166 */     return Mono.fromCallable(() -> {
/*     */           DataBuffer buffer = bufferFactory.allocateBuffer();
/*     */           for (Map.Entry<String, List<String>> entry : (Iterable<Map.Entry<String, List<String>>>)headers.entrySet()) {
/*     */             byte[] headerName = ((String)entry.getKey()).getBytes(getCharset());
/*     */             for (String headerValueString : entry.getValue()) {
/*     */               byte[] headerValue = headerValueString.getBytes(getCharset());
/*     */               buffer.write(headerName);
/*     */               buffer.write((byte)58);
/*     */               buffer.write((byte)32);
/*     */               buffer.write(headerValue);
/*     */               buffer.write((byte)13);
/*     */               buffer.write((byte)10);
/*     */             } 
/*     */           } 
/*     */           buffer.write((byte)13);
/*     */           buffer.write((byte)10);
/*     */           return buffer;
/*     */         });
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/MultipartWriterSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */