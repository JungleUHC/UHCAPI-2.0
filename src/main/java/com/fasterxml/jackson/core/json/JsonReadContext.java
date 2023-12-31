/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParseException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
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
/*     */ public final class JsonReadContext
/*     */   extends JsonStreamContext
/*     */ {
/*     */   protected final JsonReadContext _parent;
/*     */   protected DupDetector _dups;
/*     */   protected JsonReadContext _child;
/*     */   protected String _currentName;
/*     */   protected Object _currentValue;
/*     */   protected int _lineNr;
/*     */   protected int _columnNr;
/*     */   
/*     */   public JsonReadContext(JsonReadContext parent, DupDetector dups, int type, int lineNr, int colNr) {
/*  58 */     this._parent = parent;
/*  59 */     this._dups = dups;
/*  60 */     this._type = type;
/*  61 */     this._lineNr = lineNr;
/*  62 */     this._columnNr = colNr;
/*  63 */     this._index = -1;
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
/*     */   public void reset(int type, int lineNr, int colNr) {
/*  80 */     this._type = type;
/*  81 */     this._index = -1;
/*  82 */     this._lineNr = lineNr;
/*  83 */     this._columnNr = colNr;
/*  84 */     this._currentName = null;
/*  85 */     this._currentValue = null;
/*  86 */     if (this._dups != null) {
/*  87 */       this._dups.reset();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonReadContext withDupDetector(DupDetector dups) {
/*  98 */     this._dups = dups;
/*  99 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getCurrentValue() {
/* 104 */     return this._currentValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentValue(Object v) {
/* 109 */     this._currentValue = v;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonReadContext createRootContext(int lineNr, int colNr, DupDetector dups) {
/* 119 */     return new JsonReadContext(null, dups, 0, lineNr, colNr);
/*     */   }
/*     */   
/*     */   public static JsonReadContext createRootContext(DupDetector dups) {
/* 123 */     return new JsonReadContext(null, dups, 0, 1, 0);
/*     */   }
/*     */   
/*     */   public JsonReadContext createChildArrayContext(int lineNr, int colNr) {
/* 127 */     JsonReadContext ctxt = this._child;
/* 128 */     if (ctxt == null) {
/* 129 */       this
/* 130 */         ._child = ctxt = new JsonReadContext(this, (this._dups == null) ? null : this._dups.child(), 1, lineNr, colNr);
/*     */     } else {
/* 132 */       ctxt.reset(1, lineNr, colNr);
/*     */     } 
/* 134 */     return ctxt;
/*     */   }
/*     */   
/*     */   public JsonReadContext createChildObjectContext(int lineNr, int colNr) {
/* 138 */     JsonReadContext ctxt = this._child;
/* 139 */     if (ctxt == null) {
/* 140 */       this
/* 141 */         ._child = ctxt = new JsonReadContext(this, (this._dups == null) ? null : this._dups.child(), 2, lineNr, colNr);
/* 142 */       return ctxt;
/*     */     } 
/* 144 */     ctxt.reset(2, lineNr, colNr);
/* 145 */     return ctxt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCurrentName() {
/* 154 */     return this._currentName;
/*     */   }
/*     */   public boolean hasCurrentName() {
/* 157 */     return (this._currentName != null);
/*     */   } public JsonReadContext getParent() {
/* 159 */     return this._parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonLocation startLocation(ContentReference srcRef) {
/* 164 */     long totalChars = -1L;
/* 165 */     return new JsonLocation(srcRef, totalChars, this._lineNr, this._columnNr);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonLocation getStartLocation(Object rawSrc) {
/* 171 */     return startLocation(ContentReference.rawReference(rawSrc));
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
/*     */   public JsonReadContext clearAndGetParent() {
/* 193 */     this._currentValue = null;
/*     */     
/* 195 */     return this._parent;
/*     */   }
/*     */   
/*     */   public DupDetector getDupDetector() {
/* 199 */     return this._dups;
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
/*     */   public boolean expectComma() {
/* 213 */     int ix = ++this._index;
/* 214 */     return (this._type != 0 && ix > 0);
/*     */   }
/*     */   
/*     */   public void setCurrentName(String name) throws JsonProcessingException {
/* 218 */     this._currentName = name;
/* 219 */     if (this._dups != null) _checkDup(this._dups, name); 
/*     */   }
/*     */   
/*     */   private void _checkDup(DupDetector dd, String name) throws JsonProcessingException {
/* 223 */     if (dd.isDup(name)) {
/* 224 */       Object src = dd.getSource();
/* 225 */       throw new JsonParseException((src instanceof JsonParser) ? (JsonParser)src : null, "Duplicate field '" + name + "'");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/JsonReadContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */