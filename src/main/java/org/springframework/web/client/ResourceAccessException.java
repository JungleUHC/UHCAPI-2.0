/*    */ package org.springframework.web.client;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ public class ResourceAccessException
/*    */   extends RestClientException
/*    */ {
/*    */   private static final long serialVersionUID = -8513182514355844870L;
/*    */   
/*    */   public ResourceAccessException(String msg) {
/* 37 */     super(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ResourceAccessException(String msg, IOException ex) {
/* 46 */     super(msg, ex);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/ResourceAccessException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */