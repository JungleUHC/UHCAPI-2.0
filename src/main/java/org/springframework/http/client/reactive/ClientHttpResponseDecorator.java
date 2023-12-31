/*    */ package org.springframework.http.client.reactive;
/*    */ 
/*    */ import org.springframework.core.io.buffer.DataBuffer;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.http.HttpStatus;
/*    */ import org.springframework.http.ResponseCookie;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.util.MultiValueMap;
/*    */ import reactor.core.publisher.Flux;
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
/*    */ public class ClientHttpResponseDecorator
/*    */   implements ClientHttpResponse
/*    */ {
/*    */   private final ClientHttpResponse delegate;
/*    */   
/*    */   public ClientHttpResponseDecorator(ClientHttpResponse delegate) {
/* 41 */     Assert.notNull(delegate, "Delegate is required");
/* 42 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */   
/*    */   public ClientHttpResponse getDelegate() {
/* 47 */     return this.delegate;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getId() {
/* 55 */     return this.delegate.getId();
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpStatus getStatusCode() {
/* 60 */     return this.delegate.getStatusCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getRawStatusCode() {
/* 65 */     return this.delegate.getRawStatusCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpHeaders getHeaders() {
/* 70 */     return this.delegate.getHeaders();
/*    */   }
/*    */ 
/*    */   
/*    */   public MultiValueMap<String, ResponseCookie> getCookies() {
/* 75 */     return this.delegate.getCookies();
/*    */   }
/*    */ 
/*    */   
/*    */   public Flux<DataBuffer> getBody() {
/* 80 */     return this.delegate.getBody();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 85 */     return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/ClientHttpResponseDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */