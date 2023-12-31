/*    */ package org.springframework.http.server.reactive;
/*    */ 
/*    */ import org.springframework.http.HttpStatus;
/*    */ import org.springframework.http.ReactiveHttpOutputMessage;
/*    */ import org.springframework.http.ResponseCookie;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.MultiValueMap;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface ServerHttpResponse
/*    */   extends ReactiveHttpOutputMessage
/*    */ {
/*    */   boolean setStatusCode(@Nullable HttpStatus paramHttpStatus);
/*    */   
/*    */   @Nullable
/*    */   HttpStatus getStatusCode();
/*    */   
/*    */   default boolean setRawStatusCode(@Nullable Integer value) {
/* 62 */     if (value == null) {
/* 63 */       return setStatusCode(null);
/*    */     }
/*    */     
/* 66 */     HttpStatus httpStatus = HttpStatus.resolve(value.intValue());
/* 67 */     if (httpStatus == null) {
/* 68 */       throw new IllegalStateException("Unresolvable HttpStatus for general ServerHttpResponse: " + value);
/*    */     }
/*    */     
/* 71 */     return setStatusCode(httpStatus);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   default Integer getRawStatusCode() {
/* 83 */     HttpStatus httpStatus = getStatusCode();
/* 84 */     return (httpStatus != null) ? Integer.valueOf(httpStatus.value()) : null;
/*    */   }
/*    */   
/*    */   MultiValueMap<String, ResponseCookie> getCookies();
/*    */   
/*    */   void addCookie(ResponseCookie paramResponseCookie);
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/ServerHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */