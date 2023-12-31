/*     */ package com.fasterxml.jackson.core.filter;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TokenFilterContext
/*     */   extends JsonStreamContext
/*     */ {
/*     */   protected final TokenFilterContext _parent;
/*     */   protected TokenFilterContext _child;
/*     */   protected String _currentName;
/*     */   protected TokenFilter _filter;
/*     */   protected boolean _startHandled;
/*     */   protected boolean _needToHandleName;
/*     */   
/*     */   protected TokenFilterContext(int type, TokenFilterContext parent, TokenFilter filter, boolean startHandled) {
/*  72 */     this._type = type;
/*  73 */     this._parent = parent;
/*  74 */     this._filter = filter;
/*  75 */     this._index = -1;
/*  76 */     this._startHandled = startHandled;
/*  77 */     this._needToHandleName = false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected TokenFilterContext reset(int type, TokenFilter filter, boolean startWritten) {
/*  83 */     this._type = type;
/*  84 */     this._filter = filter;
/*  85 */     this._index = -1;
/*  86 */     this._currentName = null;
/*  87 */     this._startHandled = startWritten;
/*  88 */     this._needToHandleName = false;
/*  89 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TokenFilterContext createRootContext(TokenFilter filter) {
/* 100 */     return new TokenFilterContext(0, null, filter, true);
/*     */   }
/*     */   
/*     */   public TokenFilterContext createChildArrayContext(TokenFilter filter, boolean writeStart) {
/* 104 */     TokenFilterContext ctxt = this._child;
/* 105 */     if (ctxt == null) {
/* 106 */       this._child = ctxt = new TokenFilterContext(1, this, filter, writeStart);
/* 107 */       return ctxt;
/*     */     } 
/* 109 */     return ctxt.reset(1, filter, writeStart);
/*     */   }
/*     */   
/*     */   public TokenFilterContext createChildObjectContext(TokenFilter filter, boolean writeStart) {
/* 113 */     TokenFilterContext ctxt = this._child;
/* 114 */     if (ctxt == null) {
/* 115 */       this._child = ctxt = new TokenFilterContext(2, this, filter, writeStart);
/* 116 */       return ctxt;
/*     */     } 
/* 118 */     return ctxt.reset(2, filter, writeStart);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenFilter setFieldName(String name) throws JsonProcessingException {
/* 128 */     this._currentName = name;
/* 129 */     this._needToHandleName = true;
/* 130 */     return this._filter;
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
/*     */   public TokenFilter checkValue(TokenFilter filter) {
/* 143 */     if (this._type == 2) {
/* 144 */       return filter;
/*     */     }
/*     */     
/* 147 */     int ix = ++this._index;
/* 148 */     if (this._type == 1) {
/* 149 */       return filter.includeElement(ix);
/*     */     }
/* 151 */     return filter.includeRootValue(ix);
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
/*     */   public void ensureFieldNameWritten(JsonGenerator gen) throws IOException {
/* 165 */     if (this._needToHandleName) {
/* 166 */       this._needToHandleName = false;
/* 167 */       gen.writeFieldName(this._currentName);
/*     */     } 
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
/*     */   public void writePath(JsonGenerator gen) throws IOException {
/* 182 */     if (this._filter == null || this._filter == TokenFilter.INCLUDE_ALL) {
/*     */       return;
/*     */     }
/* 185 */     if (this._parent != null) {
/* 186 */       this._parent._writePath(gen);
/*     */     }
/* 188 */     if (this._startHandled) {
/*     */       
/* 190 */       if (this._needToHandleName) {
/* 191 */         gen.writeFieldName(this._currentName);
/*     */       }
/*     */     } else {
/* 194 */       this._startHandled = true;
/* 195 */       if (this._type == 2) {
/* 196 */         gen.writeStartObject();
/* 197 */         gen.writeFieldName(this._currentName);
/* 198 */       } else if (this._type == 1) {
/* 199 */         gen.writeStartArray();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void _writePath(JsonGenerator gen) throws IOException {
/* 206 */     if (this._filter == null || this._filter == TokenFilter.INCLUDE_ALL) {
/*     */       return;
/*     */     }
/* 209 */     if (this._parent != null) {
/* 210 */       this._parent._writePath(gen);
/*     */     }
/* 212 */     if (this._startHandled) {
/*     */       
/* 214 */       if (this._needToHandleName) {
/* 215 */         this._needToHandleName = false;
/* 216 */         gen.writeFieldName(this._currentName);
/*     */       } 
/*     */     } else {
/* 219 */       this._startHandled = true;
/* 220 */       if (this._type == 2) {
/* 221 */         gen.writeStartObject();
/* 222 */         if (this._needToHandleName) {
/* 223 */           this._needToHandleName = false;
/* 224 */           gen.writeFieldName(this._currentName);
/*     */         } 
/* 226 */       } else if (this._type == 1) {
/* 227 */         gen.writeStartArray();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public TokenFilterContext closeArray(JsonGenerator gen) throws IOException {
/* 234 */     if (this._startHandled) {
/* 235 */       gen.writeEndArray();
/*     */     }
/* 237 */     if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL) {
/* 238 */       this._filter.filterFinishArray();
/*     */     }
/* 240 */     return this._parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public TokenFilterContext closeObject(JsonGenerator gen) throws IOException {
/* 245 */     if (this._startHandled) {
/* 246 */       gen.writeEndObject();
/*     */     }
/* 248 */     if (this._filter != null && this._filter != TokenFilter.INCLUDE_ALL) {
/* 249 */       this._filter.filterFinishObject();
/*     */     }
/* 251 */     return this._parent;
/*     */   }
/*     */   
/*     */   public void skipParentChecks() {
/* 255 */     this._filter = null;
/* 256 */     for (TokenFilterContext ctxt = this._parent; ctxt != null; ctxt = ctxt._parent) {
/* 257 */       this._parent._filter = null;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getCurrentValue() {
/* 268 */     return null;
/*     */   }
/*     */   
/*     */   public void setCurrentValue(Object v) {}
/*     */   
/* 273 */   public final TokenFilterContext getParent() { return this._parent; } public final String getCurrentName() {
/* 274 */     return this._currentName;
/*     */   } public boolean hasCurrentName() {
/* 276 */     return (this._currentName != null);
/*     */   }
/* 278 */   public TokenFilter getFilter() { return this._filter; } public boolean isStartHandled() {
/* 279 */     return this._startHandled;
/*     */   }
/*     */   public JsonToken nextTokenToRead() {
/* 282 */     if (!this._startHandled) {
/* 283 */       this._startHandled = true;
/* 284 */       if (this._type == 2) {
/* 285 */         return JsonToken.START_OBJECT;
/*     */       }
/*     */       
/* 288 */       return JsonToken.START_ARRAY;
/*     */     } 
/*     */     
/* 291 */     if (this._needToHandleName && this._type == 2) {
/* 292 */       this._needToHandleName = false;
/* 293 */       return JsonToken.FIELD_NAME;
/*     */     } 
/* 295 */     return null;
/*     */   }
/*     */   
/*     */   public TokenFilterContext findChildOf(TokenFilterContext parent) {
/* 299 */     if (this._parent == parent) {
/* 300 */       return this;
/*     */     }
/* 302 */     TokenFilterContext curr = this._parent;
/* 303 */     while (curr != null) {
/* 304 */       TokenFilterContext p = curr._parent;
/* 305 */       if (p == parent) {
/* 306 */         return curr;
/*     */       }
/* 308 */       curr = p;
/*     */     } 
/*     */     
/* 311 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void appendDesc(StringBuilder sb) {
/* 317 */     if (this._parent != null) {
/* 318 */       this._parent.appendDesc(sb);
/*     */     }
/* 320 */     if (this._type == 2) {
/* 321 */       sb.append('{');
/* 322 */       if (this._currentName != null) {
/* 323 */         sb.append('"');
/*     */         
/* 325 */         sb.append(this._currentName);
/* 326 */         sb.append('"');
/*     */       } else {
/* 328 */         sb.append('?');
/*     */       } 
/* 330 */       sb.append('}');
/* 331 */     } else if (this._type == 1) {
/* 332 */       sb.append('[');
/* 333 */       sb.append(getCurrentIndex());
/* 334 */       sb.append(']');
/*     */     } else {
/*     */       
/* 337 */       sb.append("/");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 346 */     StringBuilder sb = new StringBuilder(64);
/* 347 */     appendDesc(sb);
/* 348 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/filter/TokenFilterContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */