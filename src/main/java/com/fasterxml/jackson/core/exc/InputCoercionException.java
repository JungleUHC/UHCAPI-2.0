/*    */ package com.fasterxml.jackson.core.exc;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.core.JsonToken;
/*    */ import com.fasterxml.jackson.core.util.RequestPayload;
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
/*    */ public class InputCoercionException
/*    */   extends StreamReadException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final JsonToken _inputType;
/*    */   protected final Class<?> _targetType;
/*    */   
/*    */   public InputCoercionException(JsonParser p, String msg, JsonToken inputType, Class<?> targetType) {
/* 40 */     super(p, msg);
/* 41 */     this._inputType = inputType;
/* 42 */     this._targetType = targetType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InputCoercionException withParser(JsonParser p) {
/* 53 */     this._processor = p;
/* 54 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputCoercionException withRequestPayload(RequestPayload p) {
/* 59 */     this._requestPayload = p;
/* 60 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JsonToken getInputType() {
/* 70 */     return this._inputType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<?> getTargetType() {
/* 80 */     return this._targetType;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/exc/InputCoercionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */