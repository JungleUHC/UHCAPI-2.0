/*     */ package com.fasterxml.jackson.core;
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
/*     */ public class JsonProcessingException
/*     */   extends JacksonException
/*     */ {
/*     */   private static final long serialVersionUID = 123L;
/*     */   protected JsonLocation _location;
/*     */   
/*     */   protected JsonProcessingException(String msg, JsonLocation loc, Throwable rootCause) {
/*  25 */     super(msg, rootCause);
/*  26 */     this._location = loc;
/*     */   }
/*     */   
/*     */   protected JsonProcessingException(String msg) {
/*  30 */     super(msg);
/*     */   }
/*     */   
/*     */   protected JsonProcessingException(String msg, JsonLocation loc) {
/*  34 */     this(msg, loc, null);
/*     */   }
/*     */   
/*     */   protected JsonProcessingException(String msg, Throwable rootCause) {
/*  38 */     this(msg, null, rootCause);
/*     */   }
/*     */   
/*     */   protected JsonProcessingException(Throwable rootCause) {
/*  42 */     this(null, null, rootCause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonLocation getLocation() {
/*  52 */     return this._location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearLocation() {
/*  61 */     this._location = null;
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
/*     */   public String getOriginalMessage() {
/*  73 */     return super.getMessage();
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
/*     */   public Object getProcessor() {
/*  90 */     return null;
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
/*     */   protected String getMessageSuffix() {
/* 105 */     return null;
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
/*     */   public String getMessage() {
/* 120 */     String msg = super.getMessage();
/* 121 */     if (msg == null) {
/* 122 */       msg = "N/A";
/*     */     }
/* 124 */     JsonLocation loc = getLocation();
/* 125 */     String suffix = getMessageSuffix();
/*     */     
/* 127 */     if (loc != null || suffix != null) {
/* 128 */       StringBuilder sb = new StringBuilder(100);
/* 129 */       sb.append(msg);
/* 130 */       if (suffix != null) {
/* 131 */         sb.append(suffix);
/*     */       }
/* 133 */       if (loc != null) {
/* 134 */         sb.append('\n');
/* 135 */         sb.append(" at ");
/* 136 */         sb.append(loc.toString());
/*     */       } 
/* 138 */       msg = sb.toString();
/*     */     } 
/* 140 */     return msg;
/*     */   }
/*     */   public String toString() {
/* 143 */     return getClass().getName() + ": " + getMessage();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonProcessingException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */