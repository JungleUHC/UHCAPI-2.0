/*     */ package org.springframework.http.codec.multipart;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.DecodingException;
/*     */ import org.springframework.http.HttpMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ReactiveHttpInputMessage;
/*     */ import org.springframework.http.codec.HttpMessageReader;
/*     */ import org.springframework.http.codec.LoggingCodecSupport;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.core.scheduler.Scheduler;
/*     */ import reactor.core.scheduler.Schedulers;
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
/*     */ public class DefaultPartHttpMessageReader
/*     */   extends LoggingCodecSupport
/*     */   implements HttpMessageReader<Part>
/*     */ {
/*  64 */   private int maxInMemorySize = 262144;
/*     */   
/*  66 */   private int maxHeadersSize = 10240;
/*     */   
/*  68 */   private long maxDiskUsagePerPart = -1L;
/*     */   
/*  70 */   private int maxParts = -1;
/*     */   
/*     */   private boolean streaming;
/*     */   
/*  74 */   private Scheduler blockingOperationScheduler = Schedulers.boundedElastic();
/*     */   
/*  76 */   private FileStorage fileStorage = FileStorage.tempDirectory(this::getBlockingOperationScheduler);
/*     */   
/*  78 */   private Charset headersCharset = StandardCharsets.UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxHeadersSize(int byteCount) {
/*  87 */     this.maxHeadersSize = byteCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxInMemorySize() {
/*  94 */     return this.maxInMemorySize;
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
/*     */   public void setMaxInMemorySize(int maxInMemorySize) {
/* 111 */     this.maxInMemorySize = maxInMemorySize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxDiskUsagePerPart(long maxDiskUsagePerPart) {
/* 122 */     this.maxDiskUsagePerPart = maxDiskUsagePerPart;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxParts(int maxParts) {
/* 130 */     this.maxParts = maxParts;
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
/*     */   public void setFileStorageDirectory(Path fileStorageDirectory) throws IOException {
/* 145 */     Assert.notNull(fileStorageDirectory, "FileStorageDirectory must not be null");
/* 146 */     this.fileStorage = FileStorage.fromPath(fileStorageDirectory);
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
/*     */   public void setBlockingOperationScheduler(Scheduler blockingOperationScheduler) {
/* 160 */     Assert.notNull(blockingOperationScheduler, "FileCreationScheduler must not be null");
/* 161 */     this.blockingOperationScheduler = blockingOperationScheduler;
/*     */   }
/*     */   
/*     */   private Scheduler getBlockingOperationScheduler() {
/* 165 */     return this.blockingOperationScheduler;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStreaming(boolean streaming) {
/* 186 */     this.streaming = streaming;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeadersCharset(Charset headersCharset) {
/* 197 */     Assert.notNull(headersCharset, "HeadersCharset must not be null");
/* 198 */     this.headersCharset = headersCharset;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getReadableMediaTypes() {
/* 203 */     return Collections.singletonList(MediaType.MULTIPART_FORM_DATA);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
/* 208 */     return (Part.class.equals(elementType.toClass()) && (mediaType == null || MediaType.MULTIPART_FORM_DATA
/* 209 */       .isCompatibleWith(mediaType)));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Part> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 215 */     return Mono.error(new UnsupportedOperationException("Cannot read multipart request body into single Part"));
/*     */   }
/*     */ 
/*     */   
/*     */   public Flux<Part> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 220 */     return Flux.defer(() -> {
/*     */           byte[] boundary = boundary((HttpMessage)message);
/*     */           if (boundary == null) {
/*     */             return (Publisher)Flux.error((Throwable)new DecodingException("No multipart boundary found in Content-Type: \"" + message.getHeaders().getContentType() + "\""));
/*     */           }
/*     */           Flux<MultipartParser.Token> tokens = MultipartParser.parse(message.getBody(), boundary, this.maxHeadersSize, this.headersCharset);
/*     */           return (Publisher)PartGenerator.createParts(tokens, this.maxParts, this.maxInMemorySize, this.maxDiskUsagePerPart, this.streaming, this.fileStorage.directory(), this.blockingOperationScheduler);
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private byte[] boundary(HttpMessage message) {
/* 236 */     MediaType contentType = message.getHeaders().getContentType();
/* 237 */     if (contentType != null) {
/* 238 */       String boundary = contentType.getParameter("boundary");
/* 239 */       if (boundary != null) {
/* 240 */         int len = boundary.length();
/* 241 */         if (len > 2 && boundary.charAt(0) == '"' && boundary.charAt(len - 1) == '"') {
/* 242 */           boundary = boundary.substring(1, len - 1);
/*     */         }
/* 244 */         return boundary.getBytes(this.headersCharset);
/*     */       } 
/*     */     } 
/* 247 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/DefaultPartHttpMessageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */