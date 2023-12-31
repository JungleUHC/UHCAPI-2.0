/*     */ package com.fasterxml.jackson.core.exc;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.util.RequestPayload;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class StreamReadException
/*     */   extends JsonProcessingException
/*     */ {
/*     */   static final long serialVersionUID = 2L;
/*     */   protected transient JsonParser _processor;
/*     */   protected RequestPayload _requestPayload;
/*     */   
/*     */   protected StreamReadException(JsonParser p, String msg) {
/*  30 */     super(msg, (p == null) ? null : p.getCurrentLocation());
/*  31 */     this._processor = p;
/*     */   }
/*     */   
/*     */   protected StreamReadException(JsonParser p, String msg, Throwable root) {
/*  35 */     super(msg, (p == null) ? null : p.getCurrentLocation(), root);
/*  36 */     this._processor = p;
/*     */   }
/*     */   
/*     */   protected StreamReadException(JsonParser p, String msg, JsonLocation loc) {
/*  40 */     super(msg, loc, null);
/*  41 */     this._processor = p;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected StreamReadException(JsonParser p, String msg, JsonLocation loc, Throwable rootCause) {
/*  47 */     super(msg, loc, rootCause);
/*  48 */     this._processor = p;
/*     */   }
/*     */   
/*     */   protected StreamReadException(String msg, JsonLocation loc, Throwable rootCause) {
/*  52 */     super(msg, loc, rootCause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser getProcessor() {
/*  81 */     return this._processor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestPayload getRequestPayload() {
/*  91 */     return this._requestPayload;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRequestPayloadAsString() {
/* 101 */     return (this._requestPayload != null) ? this._requestPayload.toString() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/* 109 */     String msg = super.getMessage();
/* 110 */     if (this._requestPayload != null) {
/* 111 */       msg = msg + "\nRequest payload : " + this._requestPayload.toString();
/*     */     }
/* 113 */     return msg;
/*     */   }
/*     */   
/*     */   public abstract StreamReadException withParser(JsonParser paramJsonParser);
/*     */   
/*     */   public abstract StreamReadException withRequestPayload(RequestPayload paramRequestPayload);
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/exc/StreamReadException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */