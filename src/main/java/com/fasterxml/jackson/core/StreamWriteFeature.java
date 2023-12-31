/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.util.JacksonFeature;
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
/*     */ public enum StreamWriteFeature
/*     */   implements JacksonFeature
/*     */ {
/*  33 */   AUTO_CLOSE_TARGET(JsonGenerator.Feature.AUTO_CLOSE_TARGET),
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
/*  46 */   AUTO_CLOSE_CONTENT(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT),
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
/*  59 */   FLUSH_PASSED_TO_STREAM(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM),
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
/*  75 */   WRITE_BIGDECIMAL_AS_PLAIN(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN),
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
/*  92 */   STRICT_DUPLICATE_DETECTION(JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION),
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
/* 112 */   IGNORE_UNKNOWN(JsonGenerator.Feature.IGNORE_UNKNOWN);
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean _defaultState;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int _mask;
/*     */ 
/*     */ 
/*     */   
/*     */   private final JsonGenerator.Feature _mappedFeature;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   StreamWriteFeature(JsonGenerator.Feature mappedTo) {
/* 130 */     this._mappedFeature = mappedTo;
/* 131 */     this._mask = mappedTo.getMask();
/* 132 */     this._defaultState = mappedTo.enabledByDefault();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int collectDefaults() {
/* 143 */     int flags = 0;
/* 144 */     for (StreamWriteFeature f : values()) {
/* 145 */       if (f.enabledByDefault()) {
/* 146 */         flags |= f.getMask();
/*     */       }
/*     */     } 
/* 149 */     return flags;
/*     */   }
/*     */   
/*     */   public boolean enabledByDefault() {
/* 153 */     return this._defaultState;
/*     */   } public boolean enabledIn(int flags) {
/* 155 */     return ((flags & this._mask) != 0);
/*     */   } public int getMask() {
/* 157 */     return this._mask;
/*     */   } public JsonGenerator.Feature mappedFeature() {
/* 159 */     return this._mappedFeature;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/StreamWriteFeature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */