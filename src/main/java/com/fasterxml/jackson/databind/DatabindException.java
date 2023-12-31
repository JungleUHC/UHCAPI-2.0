/*    */ package com.fasterxml.jackson.databind;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonLocation;
/*    */ import com.fasterxml.jackson.core.JsonProcessingException;
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
/*    */ public abstract class DatabindException
/*    */   extends JsonProcessingException
/*    */ {
/*    */   private static final long serialVersionUID = 3L;
/*    */   
/*    */   protected DatabindException(String msg, JsonLocation loc, Throwable rootCause) {
/* 22 */     super(msg, loc, rootCause);
/*    */   }
/*    */   
/*    */   protected DatabindException(String msg) {
/* 26 */     super(msg);
/*    */   }
/*    */   
/*    */   protected DatabindException(String msg, JsonLocation loc) {
/* 30 */     this(msg, loc, null);
/*    */   }
/*    */   
/*    */   protected DatabindException(String msg, Throwable rootCause) {
/* 34 */     this(msg, null, rootCause);
/*    */   }
/*    */   
/*    */   public abstract void prependPath(Object paramObject, String paramString);
/*    */   
/*    */   public abstract void prependPath(Object paramObject, int paramInt);
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/DatabindException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */