/*    */ package com.fasterxml.jackson.core.exc;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
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
/*    */ public abstract class StreamWriteException
/*    */   extends JsonProcessingException
/*    */ {
/*    */   private static final long serialVersionUID = 2L;
/*    */   protected transient JsonGenerator _processor;
/*    */   
/*    */   protected StreamWriteException(Throwable rootCause, JsonGenerator g) {
/* 21 */     super(rootCause);
/* 22 */     this._processor = g;
/*    */   }
/*    */   
/*    */   protected StreamWriteException(String msg, JsonGenerator g) {
/* 26 */     super(msg, (JsonLocation)null);
/* 27 */     this._processor = g;
/*    */   }
/*    */   
/*    */   protected StreamWriteException(String msg, Throwable rootCause, JsonGenerator g) {
/* 31 */     super(msg, null, rootCause);
/* 32 */     this._processor = g;
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
/*    */   public JsonGenerator getProcessor() {
/* 46 */     return this._processor;
/*    */   }
/*    */   
/*    */   public abstract StreamWriteException withGenerator(JsonGenerator paramJsonGenerator);
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/exc/StreamWriteException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */