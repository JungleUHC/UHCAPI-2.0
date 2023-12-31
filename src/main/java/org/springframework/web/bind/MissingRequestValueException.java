/*    */ package org.springframework.web.bind;
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
/*    */ public class MissingRequestValueException
/*    */   extends ServletRequestBindingException
/*    */ {
/*    */   private final boolean missingAfterConversion;
/*    */   
/*    */   public MissingRequestValueException(String msg) {
/* 34 */     this(msg, false);
/*    */   }
/*    */   
/*    */   public MissingRequestValueException(String msg, boolean missingAfterConversion) {
/* 38 */     super(msg);
/* 39 */     this.missingAfterConversion = missingAfterConversion;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isMissingAfterConversion() {
/* 48 */     return this.missingAfterConversion;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/MissingRequestValueException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */