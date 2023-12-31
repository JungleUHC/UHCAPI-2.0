/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerationException;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonWriteContext
/*     */   extends JsonStreamContext
/*     */ {
/*     */   public static final int STATUS_OK_AS_IS = 0;
/*     */   public static final int STATUS_OK_AFTER_COMMA = 1;
/*     */   public static final int STATUS_OK_AFTER_COLON = 2;
/*     */   public static final int STATUS_OK_AFTER_SPACE = 3;
/*     */   public static final int STATUS_EXPECT_VALUE = 4;
/*     */   public static final int STATUS_EXPECT_NAME = 5;
/*     */   protected final JsonWriteContext _parent;
/*     */   protected DupDetector _dups;
/*     */   protected JsonWriteContext _child;
/*     */   protected String _currentName;
/*     */   protected Object _currentValue;
/*     */   protected boolean _gotName;
/*     */   
/*     */   protected JsonWriteContext(int type, JsonWriteContext parent, DupDetector dups) {
/*  70 */     this._type = type;
/*  71 */     this._parent = parent;
/*  72 */     this._dups = dups;
/*  73 */     this._index = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonWriteContext(int type, JsonWriteContext parent, DupDetector dups, Object currValue) {
/*  80 */     this._type = type;
/*  81 */     this._parent = parent;
/*  82 */     this._dups = dups;
/*  83 */     this._index = -1;
/*  84 */     this._currentValue = currValue;
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
/*     */   public JsonWriteContext reset(int type) {
/* 101 */     this._type = type;
/* 102 */     this._index = -1;
/* 103 */     this._currentName = null;
/* 104 */     this._gotName = false;
/* 105 */     this._currentValue = null;
/* 106 */     if (this._dups != null) this._dups.reset(); 
/* 107 */     return this;
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
/*     */   public JsonWriteContext reset(int type, Object currValue) {
/* 127 */     this._type = type;
/* 128 */     this._index = -1;
/* 129 */     this._currentName = null;
/* 130 */     this._gotName = false;
/* 131 */     this._currentValue = currValue;
/* 132 */     if (this._dups != null) this._dups.reset(); 
/* 133 */     return this;
/*     */   }
/*     */   
/*     */   public JsonWriteContext withDupDetector(DupDetector dups) {
/* 137 */     this._dups = dups;
/* 138 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getCurrentValue() {
/* 143 */     return this._currentValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentValue(Object v) {
/* 148 */     this._currentValue = v;
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
/*     */   @Deprecated
/*     */   public static JsonWriteContext createRootContext() {
/* 163 */     return createRootContext((DupDetector)null);
/*     */   }
/*     */   public static JsonWriteContext createRootContext(DupDetector dd) {
/* 166 */     return new JsonWriteContext(0, null, dd);
/*     */   }
/*     */   
/*     */   public JsonWriteContext createChildArrayContext() {
/* 170 */     JsonWriteContext ctxt = this._child;
/* 171 */     if (ctxt == null) {
/* 172 */       this
/* 173 */         ._child = ctxt = new JsonWriteContext(1, this, (this._dups == null) ? null : this._dups.child());
/* 174 */       return ctxt;
/*     */     } 
/* 176 */     return ctxt.reset(1);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonWriteContext createChildArrayContext(Object currValue) {
/* 181 */     JsonWriteContext ctxt = this._child;
/* 182 */     if (ctxt == null) {
/* 183 */       this
/* 184 */         ._child = ctxt = new JsonWriteContext(1, this, (this._dups == null) ? null : this._dups.child(), currValue);
/* 185 */       return ctxt;
/*     */     } 
/* 187 */     return ctxt.reset(1, currValue);
/*     */   }
/*     */   
/*     */   public JsonWriteContext createChildObjectContext() {
/* 191 */     JsonWriteContext ctxt = this._child;
/* 192 */     if (ctxt == null) {
/* 193 */       this
/* 194 */         ._child = ctxt = new JsonWriteContext(2, this, (this._dups == null) ? null : this._dups.child());
/* 195 */       return ctxt;
/*     */     } 
/* 197 */     return ctxt.reset(2);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonWriteContext createChildObjectContext(Object currValue) {
/* 202 */     JsonWriteContext ctxt = this._child;
/* 203 */     if (ctxt == null) {
/* 204 */       this
/* 205 */         ._child = ctxt = new JsonWriteContext(2, this, (this._dups == null) ? null : this._dups.child(), currValue);
/* 206 */       return ctxt;
/*     */     } 
/* 208 */     return ctxt.reset(2, currValue);
/*     */   }
/*     */   
/* 211 */   public final JsonWriteContext getParent() { return this._parent; } public final String getCurrentName() {
/* 212 */     return this._currentName;
/*     */   } public boolean hasCurrentName() {
/* 214 */     return (this._currentName != null);
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
/*     */   public JsonWriteContext clearAndGetParent() {
/* 229 */     this._currentValue = null;
/*     */     
/* 231 */     return this._parent;
/*     */   }
/*     */   
/*     */   public DupDetector getDupDetector() {
/* 235 */     return this._dups;
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
/*     */   public int writeFieldName(String name) throws JsonProcessingException {
/* 248 */     if (this._type != 2 || this._gotName) {
/* 249 */       return 4;
/*     */     }
/* 251 */     this._gotName = true;
/* 252 */     this._currentName = name;
/* 253 */     if (this._dups != null) _checkDup(this._dups, name); 
/* 254 */     return (this._index < 0) ? 0 : 1;
/*     */   }
/*     */   
/*     */   private final void _checkDup(DupDetector dd, String name) throws JsonProcessingException {
/* 258 */     if (dd.isDup(name)) {
/* 259 */       Object src = dd.getSource();
/* 260 */       throw new JsonGenerationException("Duplicate field '" + name + "'", (src instanceof JsonGenerator) ? (JsonGenerator)src : null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int writeValue() {
/* 267 */     if (this._type == 2) {
/* 268 */       if (!this._gotName) {
/* 269 */         return 5;
/*     */       }
/* 271 */       this._gotName = false;
/* 272 */       this._index++;
/* 273 */       return 2;
/*     */     } 
/*     */ 
/*     */     
/* 277 */     if (this._type == 1) {
/* 278 */       int ix = this._index;
/* 279 */       this._index++;
/* 280 */       return (ix < 0) ? 0 : 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 285 */     this._index++;
/* 286 */     return (this._index == 0) ? 0 : 3;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/JsonWriteContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */