/*     */ package com.fasterxml.jackson.databind.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.io.ContentReference;
/*     */ import com.fasterxml.jackson.core.json.JsonReadContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TokenBufferReadContext
/*     */   extends JsonStreamContext
/*     */ {
/*     */   protected final JsonStreamContext _parent;
/*     */   protected final JsonLocation _startLocation;
/*     */   protected String _currentName;
/*     */   protected Object _currentValue;
/*     */   
/*     */   protected TokenBufferReadContext(JsonStreamContext base, ContentReference srcRef) {
/*  39 */     super(base);
/*  40 */     this._parent = base.getParent();
/*  41 */     this._currentName = base.getCurrentName();
/*  42 */     this._currentValue = base.getCurrentValue();
/*  43 */     if (base instanceof JsonReadContext) {
/*  44 */       JsonReadContext rc = (JsonReadContext)base;
/*  45 */       this._startLocation = rc.startLocation(srcRef);
/*     */     } else {
/*  47 */       this._startLocation = JsonLocation.NA;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected TokenBufferReadContext(JsonStreamContext base, Object srcRef) {
/*  53 */     this(base, (srcRef instanceof ContentReference) ? (ContentReference)srcRef : 
/*     */         
/*  55 */         ContentReference.rawReference(srcRef));
/*     */   }
/*     */   
/*     */   protected TokenBufferReadContext(JsonStreamContext base, JsonLocation startLoc) {
/*  59 */     super(base);
/*  60 */     this._parent = base.getParent();
/*  61 */     this._currentName = base.getCurrentName();
/*  62 */     this._currentValue = base.getCurrentValue();
/*  63 */     this._startLocation = startLoc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TokenBufferReadContext() {
/*  71 */     super(0, -1);
/*  72 */     this._parent = null;
/*  73 */     this._startLocation = JsonLocation.NA;
/*     */   }
/*     */   
/*     */   protected TokenBufferReadContext(TokenBufferReadContext parent, int type, int index) {
/*  77 */     super(type, index);
/*  78 */     this._parent = parent;
/*  79 */     this._startLocation = parent._startLocation;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getCurrentValue() {
/*  84 */     return this._currentValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentValue(Object v) {
/*  89 */     this._currentValue = v;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TokenBufferReadContext createRootContext(JsonStreamContext origContext) {
/* 100 */     if (origContext == null) {
/* 101 */       return new TokenBufferReadContext();
/*     */     }
/* 103 */     return new TokenBufferReadContext(origContext, ContentReference.unknown());
/*     */   }
/*     */ 
/*     */   
/*     */   public TokenBufferReadContext createChildArrayContext() {
/* 108 */     this._index++;
/* 109 */     return new TokenBufferReadContext(this, 1, -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public TokenBufferReadContext createChildObjectContext() {
/* 114 */     this._index++;
/* 115 */     return new TokenBufferReadContext(this, 2, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenBufferReadContext parentOrCopy() {
/* 126 */     if (this._parent instanceof TokenBufferReadContext) {
/* 127 */       return (TokenBufferReadContext)this._parent;
/*     */     }
/* 129 */     if (this._parent == null) {
/* 130 */       return new TokenBufferReadContext();
/*     */     }
/* 132 */     return new TokenBufferReadContext(this._parent, this._startLocation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCurrentName() {
/* 141 */     return this._currentName;
/*     */   }
/*     */   public boolean hasCurrentName() {
/* 144 */     return (this._currentName != null);
/*     */   } public JsonStreamContext getParent() {
/* 146 */     return this._parent;
/*     */   }
/*     */   public void setCurrentName(String name) throws JsonProcessingException {
/* 149 */     this._currentName = name;
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
/*     */   public void updateForValue() {
/* 162 */     this._index++;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/TokenBufferReadContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */