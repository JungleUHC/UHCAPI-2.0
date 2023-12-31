/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.CharTypes;
/*     */ import com.fasterxml.jackson.core.io.ContentReference;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JsonStreamContext
/*     */ {
/*     */   public static final int TYPE_ROOT = 0;
/*     */   public static final int TYPE_ARRAY = 1;
/*     */   public static final int TYPE_OBJECT = 2;
/*     */   protected int _type;
/*     */   protected int _index;
/*     */   
/*     */   protected JsonStreamContext() {}
/*     */   
/*     */   protected JsonStreamContext(JsonStreamContext base) {
/*  72 */     this._type = base._type;
/*  73 */     this._index = base._index;
/*     */   }
/*     */ 
/*     */   
/*     */   protected JsonStreamContext(int type, int index) {
/*  78 */     this._type = type;
/*  79 */     this._index = index;
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
/*     */   public abstract JsonStreamContext getParent();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean inArray() {
/* 102 */     return (this._type == 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean inRoot() {
/* 111 */     return (this._type == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean inObject() {
/* 119 */     return (this._type == 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final String getTypeDesc() {
/* 128 */     switch (this._type) { case 0:
/* 129 */         return "ROOT";
/* 130 */       case 1: return "ARRAY";
/* 131 */       case 2: return "OBJECT"; }
/*     */     
/* 133 */     return "?";
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
/*     */   public String typeDesc() {
/* 146 */     switch (this._type) { case 0:
/* 147 */         return "root";
/* 148 */       case 1: return "Array";
/* 149 */       case 2: return "Object"; }
/*     */     
/* 151 */     return "?";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getEntryCount() {
/* 157 */     return this._index + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getCurrentIndex() {
/* 162 */     return (this._index < 0) ? 0 : this._index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasCurrentIndex() {
/* 173 */     return (this._index >= 0);
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
/*     */   public boolean hasPathSegment() {
/* 195 */     if (this._type == 2)
/* 196 */       return hasCurrentName(); 
/* 197 */     if (this._type == 1) {
/* 198 */       return hasCurrentIndex();
/*     */     }
/* 200 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getCurrentName();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasCurrentName() {
/* 218 */     return (getCurrentName() != null);
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
/*     */   public Object getCurrentValue() {
/* 235 */     return null;
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
/*     */   public void setCurrentValue(Object v) {}
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
/*     */   public JsonPointer pathAsPointer() {
/* 259 */     return JsonPointer.forPath(this, false);
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
/*     */   public JsonPointer pathAsPointer(boolean includeRoot) {
/* 274 */     return JsonPointer.forPath(this, includeRoot);
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
/*     */   public JsonLocation startLocation(ContentReference srcRef) {
/* 292 */     return JsonLocation.NA;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonLocation getStartLocation(Object srcRef) {
/* 304 */     return JsonLocation.NA;
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
/*     */   public String toString() {
/* 318 */     StringBuilder sb = new StringBuilder(64);
/* 319 */     switch (this._type)
/*     */     { case 0:
/* 321 */         sb.append("/");
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
/* 342 */         return sb.toString();case 1: sb.append('['); sb.append(getCurrentIndex()); sb.append(']'); return sb.toString(); }  sb.append('{'); String currentName = getCurrentName(); if (currentName != null) { sb.append('"'); CharTypes.appendQuoted(sb, currentName); sb.append('"'); } else { sb.append('?'); }  sb.append('}'); return sb.toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonStreamContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */