/*    */ package org.springframework.util;
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
/*    */ public class InvalidMimeTypeException
/*    */   extends IllegalArgumentException
/*    */ {
/*    */   private final String mimeType;
/*    */   
/*    */   public InvalidMimeTypeException(String mimeType, String message) {
/* 39 */     super("Invalid mime type \"" + mimeType + "\": " + message);
/* 40 */     this.mimeType = mimeType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMimeType() {
/* 48 */     return this.mimeType;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/InvalidMimeTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */