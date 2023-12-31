/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.exc.StreamReadException;
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
/*     */ public class JsonParseException
/*     */   extends StreamReadException
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   
/*     */   @Deprecated
/*     */   public JsonParseException(String msg, JsonLocation loc) {
/*  23 */     super(msg, loc, null);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonParseException(String msg, JsonLocation loc, Throwable root) {
/*  28 */     super(msg, loc, root);
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
/*     */   public JsonParseException(JsonParser p, String msg) {
/*  42 */     super(p, msg);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonParseException(JsonParser p, String msg, Throwable root) {
/*  47 */     super(p, msg, root);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonParseException(JsonParser p, String msg, JsonLocation loc) {
/*  52 */     super(p, msg, loc);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonParseException(JsonParser p, String msg, JsonLocation loc, Throwable root) {
/*  57 */     super(msg, loc, root);
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
/*     */   public JsonParseException withParser(JsonParser p) {
/*  74 */     this._processor = p;
/*  75 */     return this;
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
/*     */   public JsonParseException withRequestPayload(RequestPayload payload) {
/*  92 */     this._requestPayload = payload;
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser getProcessor() {
/*  99 */     return super.getProcessor();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestPayload getRequestPayload() {
/* 105 */     return super.getRequestPayload();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRequestPayloadAsString() {
/* 111 */     return super.getRequestPayloadAsString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/* 117 */     return super.getMessage();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonParseException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */