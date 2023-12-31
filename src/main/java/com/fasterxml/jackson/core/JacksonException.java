/*    */ package com.fasterxml.jackson.core;
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
/*    */ public abstract class JacksonException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 123L;
/*    */   
/*    */   protected JacksonException(String msg) {
/* 18 */     super(msg);
/*    */   }
/*    */   
/*    */   protected JacksonException(Throwable t) {
/* 22 */     super(t);
/*    */   }
/*    */   
/*    */   protected JacksonException(String msg, Throwable rootCause) {
/* 26 */     super(msg, rootCause);
/*    */   }
/*    */   
/*    */   public abstract JsonLocation getLocation();
/*    */   
/*    */   public abstract String getOriginalMessage();
/*    */   
/*    */   public abstract Object getProcessor();
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JacksonException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */