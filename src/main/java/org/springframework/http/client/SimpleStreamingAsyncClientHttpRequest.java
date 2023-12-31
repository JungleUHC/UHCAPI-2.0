/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import org.springframework.core.task.AsyncListenableTaskExecutor;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.StreamUtils;
/*     */ import org.springframework.util.concurrent.ListenableFuture;
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
/*     */ @Deprecated
/*     */ final class SimpleStreamingAsyncClientHttpRequest
/*     */   extends AbstractAsyncClientHttpRequest
/*     */ {
/*     */   private final HttpURLConnection connection;
/*     */   private final int chunkSize;
/*     */   @Nullable
/*     */   private OutputStream body;
/*     */   private final boolean outputStreaming;
/*     */   private final AsyncListenableTaskExecutor taskExecutor;
/*     */   
/*     */   SimpleStreamingAsyncClientHttpRequest(HttpURLConnection connection, int chunkSize, boolean outputStreaming, AsyncListenableTaskExecutor taskExecutor) {
/*  61 */     this.connection = connection;
/*  62 */     this.chunkSize = chunkSize;
/*  63 */     this.outputStreaming = outputStreaming;
/*  64 */     this.taskExecutor = taskExecutor;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMethodValue() {
/*  70 */     return this.connection.getRequestMethod();
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*     */     try {
/*  76 */       return this.connection.getURL().toURI();
/*     */     }
/*  78 */     catch (URISyntaxException ex) {
/*  79 */       throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex
/*  80 */           .getMessage(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
/*  86 */     if (this.body == null) {
/*  87 */       if (this.outputStreaming) {
/*  88 */         long contentLength = headers.getContentLength();
/*  89 */         if (contentLength >= 0L) {
/*  90 */           this.connection.setFixedLengthStreamingMode(contentLength);
/*     */         } else {
/*     */           
/*  93 */           this.connection.setChunkedStreamingMode(this.chunkSize);
/*     */         } 
/*     */       } 
/*  96 */       SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
/*  97 */       this.connection.connect();
/*  98 */       this.body = this.connection.getOutputStream();
/*     */     } 
/* 100 */     return StreamUtils.nonClosing(this.body);
/*     */   }
/*     */ 
/*     */   
/*     */   protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers) throws IOException {
/* 105 */     return this.taskExecutor.submitListenable(() -> {
/*     */           try {
/*     */             if (this.body != null) {
/*     */               this.body.close();
/*     */             } else {
/*     */               SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
/*     */ 
/*     */               
/*     */               this.connection.connect();
/*     */               
/*     */               this.connection.getResponseCode();
/*     */             } 
/* 117 */           } catch (IOException iOException) {}
/*     */           return new SimpleClientHttpResponse(this.connection);
/*     */         });
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/SimpleStreamingAsyncClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */