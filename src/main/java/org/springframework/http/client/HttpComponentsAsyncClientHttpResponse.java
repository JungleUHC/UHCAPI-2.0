/*    */ package org.springframework.http.client;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.apache.http.Header;
/*    */ import org.apache.http.HttpEntity;
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.StreamUtils;
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
/*    */ 
/*    */ @Deprecated
/*    */ final class HttpComponentsAsyncClientHttpResponse
/*    */   extends AbstractClientHttpResponse
/*    */ {
/*    */   private final HttpResponse httpResponse;
/*    */   @Nullable
/*    */   private HttpHeaders headers;
/*    */   
/*    */   HttpComponentsAsyncClientHttpResponse(HttpResponse httpResponse) {
/* 53 */     this.httpResponse = httpResponse;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getRawStatusCode() throws IOException {
/* 59 */     return this.httpResponse.getStatusLine().getStatusCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getStatusText() throws IOException {
/* 64 */     return this.httpResponse.getStatusLine().getReasonPhrase();
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpHeaders getHeaders() {
/* 69 */     if (this.headers == null) {
/* 70 */       this.headers = new HttpHeaders();
/* 71 */       for (Header header : this.httpResponse.getAllHeaders()) {
/* 72 */         this.headers.add(header.getName(), header.getValue());
/*    */       }
/*    */     } 
/* 75 */     return this.headers;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getBody() throws IOException {
/* 80 */     HttpEntity entity = this.httpResponse.getEntity();
/* 81 */     return (entity != null) ? entity.getContent() : StreamUtils.emptyInput();
/*    */   }
/*    */   
/*    */   public void close() {}
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/HttpComponentsAsyncClientHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */