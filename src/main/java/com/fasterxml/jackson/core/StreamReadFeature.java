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
/*     */ public enum StreamReadFeature
/*     */   implements JacksonFeature
/*     */ {
/*  31 */   AUTO_CLOSE_SOURCE(JsonParser.Feature.AUTO_CLOSE_SOURCE),
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
/*  49 */   STRICT_DUPLICATE_DETECTION(JsonParser.Feature.STRICT_DUPLICATE_DETECTION),
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
/*  71 */   IGNORE_UNDEFINED(JsonParser.Feature.IGNORE_UNDEFINED),
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
/*  92 */   INCLUDE_SOURCE_IN_LOCATION(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
/*     */ 
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
/*     */   private final JsonParser.Feature _mappedFeature;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   StreamReadFeature(JsonParser.Feature mapTo) {
/* 111 */     this._mappedFeature = mapTo;
/* 112 */     this._mask = mapTo.getMask();
/* 113 */     this._defaultState = mapTo.enabledByDefault();
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
/* 124 */     int flags = 0;
/* 125 */     for (StreamReadFeature f : values()) {
/* 126 */       if (f.enabledByDefault()) {
/* 127 */         flags |= f.getMask();
/*     */       }
/*     */     } 
/* 130 */     return flags;
/*     */   }
/*     */   
/*     */   public boolean enabledByDefault() {
/* 134 */     return this._defaultState;
/*     */   } public boolean enabledIn(int flags) {
/* 136 */     return ((flags & this._mask) != 0);
/*     */   } public int getMask() {
/* 138 */     return this._mask;
/*     */   } public JsonParser.Feature mappedFeature() {
/* 140 */     return this._mappedFeature;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/StreamReadFeature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */