/*    */ package com.fasterxml.jackson.core;
/*    */ 
/*    */ import com.fasterxml.jackson.core.exc.StreamWriteException;
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
/*    */ public class JsonGenerationException
/*    */   extends StreamWriteException
/*    */ {
/*    */   private static final long serialVersionUID = 123L;
/*    */   
/*    */   @Deprecated
/*    */   public JsonGenerationException(Throwable rootCause) {
/* 22 */     super(rootCause, null);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public JsonGenerationException(String msg) {
/* 27 */     super(msg, null);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public JsonGenerationException(String msg, Throwable rootCause) {
/* 32 */     super(msg, rootCause, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public JsonGenerationException(Throwable rootCause, JsonGenerator g) {
/* 37 */     super(rootCause, g);
/*    */   }
/*    */ 
/*    */   
/*    */   public JsonGenerationException(String msg, JsonGenerator g) {
/* 42 */     super(msg, g);
/* 43 */     this._processor = g;
/*    */   }
/*    */ 
/*    */   
/*    */   public JsonGenerationException(String msg, Throwable rootCause, JsonGenerator g) {
/* 48 */     super(msg, rootCause, g);
/* 49 */     this._processor = g;
/*    */   }
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
/*    */   public JsonGenerationException withGenerator(JsonGenerator g) {
/* 64 */     this._processor = g;
/* 65 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public JsonGenerator getProcessor() {
/* 70 */     return this._processor;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonGenerationException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */