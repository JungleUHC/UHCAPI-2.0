/*    */ package org.springframework.http;
/*    */ 
/*    */ import java.net.URI;
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
/*    */ public interface HttpRequest
/*    */   extends HttpMessage
/*    */ {
/*    */   @Nullable
/*    */   default HttpMethod getMethod() {
/* 41 */     return HttpMethod.resolve(getMethodValue());
/*    */   }
/*    */   
/*    */   String getMethodValue();
/*    */   
/*    */   URI getURI();
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/HttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */