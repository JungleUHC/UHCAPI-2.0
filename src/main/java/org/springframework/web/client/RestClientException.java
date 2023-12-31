/*    */ package org.springframework.web.client;
/*    */ 
/*    */ import org.springframework.core.NestedRuntimeException;
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
/*    */ public class RestClientException
/*    */   extends NestedRuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -4084444984163796577L;
/*    */   
/*    */   public RestClientException(String msg) {
/* 41 */     super(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RestClientException(String msg, Throwable ex) {
/* 51 */     super(msg, ex);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/RestClientException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */