/*    */ package org.springframework.http.client.support;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.charset.Charset;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.http.HttpRequest;
/*    */ import org.springframework.http.client.ClientHttpRequestExecution;
/*    */ import org.springframework.http.client.ClientHttpRequestInterceptor;
/*    */ import org.springframework.http.client.ClientHttpResponse;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BasicAuthenticationInterceptor
/*    */   implements ClientHttpRequestInterceptor
/*    */ {
/*    */   private final String encodedCredentials;
/*    */   
/*    */   public BasicAuthenticationInterceptor(String username, String password) {
/* 54 */     this(username, password, null);
/*    */   }
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
/*    */   public BasicAuthenticationInterceptor(String username, String password, @Nullable Charset charset) {
/* 67 */     this.encodedCredentials = HttpHeaders.encodeBasicAuth(username, password, charset);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
/* 75 */     HttpHeaders headers = request.getHeaders();
/* 76 */     if (!headers.containsKey("Authorization")) {
/* 77 */       headers.setBasicAuth(this.encodedCredentials);
/*    */     }
/* 79 */     return execution.execute(request, body);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/support/BasicAuthenticationInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */