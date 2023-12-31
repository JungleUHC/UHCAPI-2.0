/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.StreamUtils;
/*     */ import org.springframework.util.StringUtils;
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
/*     */ final class SimpleClientHttpResponse
/*     */   extends AbstractClientHttpResponse
/*     */ {
/*     */   private final HttpURLConnection connection;
/*     */   @Nullable
/*     */   private HttpHeaders headers;
/*     */   @Nullable
/*     */   private InputStream responseStream;
/*     */   
/*     */   SimpleClientHttpResponse(HttpURLConnection connection) {
/*  49 */     this.connection = connection;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRawStatusCode() throws IOException {
/*  55 */     return this.connection.getResponseCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStatusText() throws IOException {
/*  60 */     String result = this.connection.getResponseMessage();
/*  61 */     return (result != null) ? result : "";
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/*  66 */     if (this.headers == null) {
/*  67 */       this.headers = new HttpHeaders();
/*     */       
/*  69 */       String name = this.connection.getHeaderFieldKey(0);
/*  70 */       if (StringUtils.hasLength(name)) {
/*  71 */         this.headers.add(name, this.connection.getHeaderField(0));
/*     */       }
/*  73 */       int i = 1;
/*     */       while (true) {
/*  75 */         name = this.connection.getHeaderFieldKey(i);
/*  76 */         if (!StringUtils.hasLength(name)) {
/*     */           break;
/*     */         }
/*  79 */         this.headers.add(name, this.connection.getHeaderField(i));
/*  80 */         i++;
/*     */       } 
/*     */     } 
/*  83 */     return this.headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getBody() throws IOException {
/*  88 */     InputStream errorStream = this.connection.getErrorStream();
/*  89 */     this.responseStream = (errorStream != null) ? errorStream : this.connection.getInputStream();
/*  90 */     return this.responseStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/*  96 */       if (this.responseStream == null) {
/*  97 */         getBody();
/*     */       }
/*  99 */       StreamUtils.drain(this.responseStream);
/* 100 */       this.responseStream.close();
/*     */     }
/* 102 */     catch (Exception exception) {}
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/SimpleClientHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */