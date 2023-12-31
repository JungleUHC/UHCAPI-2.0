/*    */ package org.springframework.cglib.core;
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
/*    */ public class CodeGenerationException
/*    */   extends RuntimeException
/*    */ {
/*    */   private Throwable cause;
/*    */   
/*    */   public CodeGenerationException(Throwable cause) {
/* 25 */     super(cause.getClass().getName() + "-->" + cause.getMessage());
/* 26 */     this.cause = cause;
/*    */   }
/*    */   
/*    */   public Throwable getCause() {
/* 30 */     return this.cause;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/cglib/core/CodeGenerationException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */