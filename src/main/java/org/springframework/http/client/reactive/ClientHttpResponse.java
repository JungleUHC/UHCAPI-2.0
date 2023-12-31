/*    */ package org.springframework.http.client.reactive;
/*    */ 
/*    */ import org.springframework.http.HttpStatus;
/*    */ import org.springframework.http.ReactiveHttpInputMessage;
/*    */ import org.springframework.http.ResponseCookie;
/*    */ import org.springframework.util.MultiValueMap;
/*    */ import org.springframework.util.ObjectUtils;
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
/*    */ public interface ClientHttpResponse
/*    */   extends ReactiveHttpInputMessage
/*    */ {
/*    */   default String getId() {
/* 40 */     return ObjectUtils.getIdentityHexString(this);
/*    */   }
/*    */   
/*    */   HttpStatus getStatusCode();
/*    */   
/*    */   int getRawStatusCode();
/*    */   
/*    */   MultiValueMap<String, ResponseCookie> getCookies();
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/ClientHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */