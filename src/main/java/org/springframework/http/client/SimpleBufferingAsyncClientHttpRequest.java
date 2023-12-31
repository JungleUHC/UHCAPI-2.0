/*    */ package org.springframework.http.client;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.HttpURLConnection;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
/*    */ import org.springframework.core.task.AsyncListenableTaskExecutor;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.http.HttpMethod;
/*    */ import org.springframework.util.FileCopyUtils;
/*    */ import org.springframework.util.concurrent.ListenableFuture;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ final class SimpleBufferingAsyncClientHttpRequest
/*    */   extends AbstractBufferingAsyncClientHttpRequest
/*    */ {
/*    */   private final HttpURLConnection connection;
/*    */   private final boolean outputStreaming;
/*    */   private final AsyncListenableTaskExecutor taskExecutor;
/*    */   
/*    */   SimpleBufferingAsyncClientHttpRequest(HttpURLConnection connection, boolean outputStreaming, AsyncListenableTaskExecutor taskExecutor) {
/* 53 */     this.connection = connection;
/* 54 */     this.outputStreaming = outputStreaming;
/* 55 */     this.taskExecutor = taskExecutor;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMethodValue() {
/* 61 */     return this.connection.getRequestMethod();
/*    */   }
/*    */ 
/*    */   
/*    */   public URI getURI() {
/*    */     try {
/* 67 */       return this.connection.getURL().toURI();
/*    */     }
/* 69 */     catch (URISyntaxException ex) {
/* 70 */       throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
/* 78 */     return this.taskExecutor.submitListenable(() -> {
/*    */           SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
/*    */           if (getMethod() == HttpMethod.DELETE && bufferedOutput.length == 0)
/*    */             this.connection.setDoOutput(false); 
/*    */           if (this.connection.getDoOutput() && this.outputStreaming)
/*    */             this.connection.setFixedLengthStreamingMode(bufferedOutput.length); 
/*    */           this.connection.connect();
/*    */           if (this.connection.getDoOutput()) {
/*    */             FileCopyUtils.copy(bufferedOutput, this.connection.getOutputStream());
/*    */           } else {
/*    */             this.connection.getResponseCode();
/*    */           } 
/*    */           return new SimpleClientHttpResponse(this.connection);
/*    */         });
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/SimpleBufferingAsyncClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */