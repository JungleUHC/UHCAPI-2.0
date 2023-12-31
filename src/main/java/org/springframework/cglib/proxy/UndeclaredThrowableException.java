/*    */ package org.springframework.cglib.proxy;
/*    */ 
/*    */ import org.springframework.cglib.core.CodeGenerationException;
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
/*    */ public class UndeclaredThrowableException
/*    */   extends CodeGenerationException
/*    */ {
/*    */   public UndeclaredThrowableException(Throwable t) {
/* 30 */     super(t);
/*    */   }
/*    */   
/*    */   public Throwable getUndeclaredThrowable() {
/* 34 */     return getCause();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/cglib/proxy/UndeclaredThrowableException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */