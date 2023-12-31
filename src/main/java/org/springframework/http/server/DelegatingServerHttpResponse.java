/*    */ package org.springframework.http.server;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.http.HttpStatus;
/*    */ import org.springframework.util.Assert;
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
/*    */ public class DelegatingServerHttpResponse
/*    */   implements ServerHttpResponse
/*    */ {
/*    */   private final ServerHttpResponse delegate;
/*    */   
/*    */   public DelegatingServerHttpResponse(ServerHttpResponse delegate) {
/* 42 */     Assert.notNull(delegate, "Delegate must not be null");
/* 43 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServerHttpResponse getDelegate() {
/* 51 */     return this.delegate;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setStatusCode(HttpStatus status) {
/* 56 */     this.delegate.setStatusCode(status);
/*    */   }
/*    */ 
/*    */   
/*    */   public void flush() throws IOException {
/* 61 */     this.delegate.flush();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 66 */     this.delegate.close();
/*    */   }
/*    */ 
/*    */   
/*    */   public OutputStream getBody() throws IOException {
/* 71 */     return this.delegate.getBody();
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpHeaders getHeaders() {
/* 76 */     return this.delegate.getHeaders();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/DelegatingServerHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */