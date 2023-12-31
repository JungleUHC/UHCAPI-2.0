/*    */ package org.springframework.http.client;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ public abstract class AbstractClientHttpRequest
/*    */   implements ClientHttpRequest
/*    */ {
/* 35 */   private final HttpHeaders headers = new HttpHeaders();
/*    */ 
/*    */   
/*    */   private boolean executed = false;
/*    */   
/*    */   @Nullable
/*    */   private HttpHeaders readOnlyHeaders;
/*    */ 
/*    */   
/*    */   public final HttpHeaders getHeaders() {
/* 45 */     if (this.readOnlyHeaders != null) {
/* 46 */       return this.readOnlyHeaders;
/*    */     }
/* 48 */     if (this.executed) {
/* 49 */       this.readOnlyHeaders = HttpHeaders.readOnlyHttpHeaders(this.headers);
/* 50 */       return this.readOnlyHeaders;
/*    */     } 
/*    */     
/* 53 */     return this.headers;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public final OutputStream getBody() throws IOException {
/* 59 */     assertNotExecuted();
/* 60 */     return getBodyInternal(this.headers);
/*    */   }
/*    */ 
/*    */   
/*    */   public final ClientHttpResponse execute() throws IOException {
/* 65 */     assertNotExecuted();
/* 66 */     ClientHttpResponse result = executeInternal(this.headers);
/* 67 */     this.executed = true;
/* 68 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void assertNotExecuted() {
/* 76 */     Assert.state(!this.executed, "ClientHttpRequest already executed");
/*    */   }
/*    */   
/*    */   protected abstract OutputStream getBodyInternal(HttpHeaders paramHttpHeaders) throws IOException;
/*    */   
/*    */   protected abstract ClientHttpResponse executeInternal(HttpHeaders paramHttpHeaders) throws IOException;
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/AbstractClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */