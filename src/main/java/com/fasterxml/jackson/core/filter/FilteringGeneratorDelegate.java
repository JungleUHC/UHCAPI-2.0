/*      */ package com.fasterxml.jackson.core.filter;
/*      */ 
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.JsonStreamContext;
/*      */ import com.fasterxml.jackson.core.SerializableString;
/*      */ import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FilteringGeneratorDelegate
/*      */   extends JsonGeneratorDelegate
/*      */ {
/*      */   protected TokenFilter rootFilter;
/*      */   protected boolean _allowMultipleMatches;
/*      */   protected TokenFilter.Inclusion _inclusion;
/*      */   protected TokenFilterContext _filterContext;
/*      */   protected TokenFilter _itemFilter;
/*      */   protected int _matchCount;
/*      */   
/*      */   @Deprecated
/*      */   public FilteringGeneratorDelegate(JsonGenerator d, TokenFilter f, boolean includePath, boolean allowMultipleMatches) {
/*   97 */     this(d, f, includePath ? TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH : TokenFilter.Inclusion.ONLY_INCLUDE_ALL, allowMultipleMatches);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FilteringGeneratorDelegate(JsonGenerator d, TokenFilter f, TokenFilter.Inclusion inclusion, boolean allowMultipleMatches) {
/*  112 */     super(d, false);
/*  113 */     this.rootFilter = f;
/*      */     
/*  115 */     this._itemFilter = f;
/*  116 */     this._filterContext = TokenFilterContext.createRootContext(f);
/*  117 */     this._inclusion = inclusion;
/*  118 */     this._allowMultipleMatches = allowMultipleMatches;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TokenFilter getFilter() {
/*  127 */     return this.rootFilter;
/*      */   }
/*      */   public JsonStreamContext getFilterContext() {
/*  130 */     return this._filterContext;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMatchCount() {
/*  140 */     return this._matchCount;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonStreamContext getOutputContext() {
/*  155 */     return this._filterContext;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray() throws IOException {
/*  168 */     if (this._itemFilter == null) {
/*  169 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/*      */       return;
/*      */     } 
/*  172 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  173 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  174 */       this.delegate.writeStartArray();
/*      */       
/*      */       return;
/*      */     } 
/*  178 */     this._itemFilter = this._filterContext.checkValue(this._itemFilter);
/*  179 */     if (this._itemFilter == null) {
/*  180 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/*      */       return;
/*      */     } 
/*  183 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  184 */       this._itemFilter = this._itemFilter.filterStartArray();
/*      */     }
/*  186 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  187 */       _checkParentPath();
/*  188 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  189 */       this.delegate.writeStartArray();
/*  190 */     } else if (this._itemFilter != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/*  191 */       _checkParentPath(false);
/*  192 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  193 */       this.delegate.writeStartArray();
/*      */     } else {
/*  195 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(int size) throws IOException {
/*  203 */     if (this._itemFilter == null) {
/*  204 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/*      */       return;
/*      */     } 
/*  207 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  208 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  209 */       this.delegate.writeStartArray(size);
/*      */       return;
/*      */     } 
/*  212 */     this._itemFilter = this._filterContext.checkValue(this._itemFilter);
/*  213 */     if (this._itemFilter == null) {
/*  214 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/*      */       return;
/*      */     } 
/*  217 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  218 */       this._itemFilter = this._itemFilter.filterStartArray();
/*      */     }
/*  220 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  221 */       _checkParentPath();
/*  222 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  223 */       this.delegate.writeStartArray(size);
/*  224 */     } else if (this._itemFilter != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/*  225 */       _checkParentPath(false);
/*  226 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  227 */       this.delegate.writeStartArray(size);
/*      */     } else {
/*  229 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(Object forValue) throws IOException {
/*  236 */     if (this._itemFilter == null) {
/*  237 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/*      */       return;
/*      */     } 
/*  240 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  241 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  242 */       this.delegate.writeStartArray(forValue);
/*      */       return;
/*      */     } 
/*  245 */     this._itemFilter = this._filterContext.checkValue(this._itemFilter);
/*  246 */     if (this._itemFilter == null) {
/*  247 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/*      */       return;
/*      */     } 
/*  250 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  251 */       this._itemFilter = this._itemFilter.filterStartArray();
/*      */     }
/*  253 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  254 */       _checkParentPath();
/*  255 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  256 */       this.delegate.writeStartArray(forValue);
/*      */     } else {
/*  258 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(Object forValue, int size) throws IOException {
/*  265 */     if (this._itemFilter == null) {
/*  266 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/*      */       return;
/*      */     } 
/*  269 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  270 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  271 */       this.delegate.writeStartArray(forValue, size);
/*      */       return;
/*      */     } 
/*  274 */     this._itemFilter = this._filterContext.checkValue(this._itemFilter);
/*  275 */     if (this._itemFilter == null) {
/*  276 */       this._filterContext = this._filterContext.createChildArrayContext(null, false);
/*      */       return;
/*      */     } 
/*  279 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  280 */       this._itemFilter = this._itemFilter.filterStartArray();
/*      */     }
/*  282 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  283 */       _checkParentPath();
/*  284 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
/*  285 */       this.delegate.writeStartArray(forValue, size);
/*      */     } else {
/*  287 */       this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeEndArray() throws IOException {
/*  294 */     this._filterContext = this._filterContext.closeArray(this.delegate);
/*      */     
/*  296 */     if (this._filterContext != null) {
/*  297 */       this._itemFilter = this._filterContext.getFilter();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject() throws IOException {
/*  304 */     if (this._itemFilter == null) {
/*  305 */       this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, false);
/*      */       return;
/*      */     } 
/*  308 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  309 */       this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, true);
/*  310 */       this.delegate.writeStartObject();
/*      */       
/*      */       return;
/*      */     } 
/*  314 */     TokenFilter f = this._filterContext.checkValue(this._itemFilter);
/*  315 */     if (f == null) {
/*      */       return;
/*      */     }
/*      */     
/*  319 */     if (f != TokenFilter.INCLUDE_ALL) {
/*  320 */       f = f.filterStartObject();
/*      */     }
/*  322 */     if (f == TokenFilter.INCLUDE_ALL) {
/*  323 */       _checkParentPath();
/*  324 */       this._filterContext = this._filterContext.createChildObjectContext(f, true);
/*  325 */       this.delegate.writeStartObject();
/*  326 */     } else if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/*  327 */       _checkParentPath(false);
/*  328 */       this._filterContext = this._filterContext.createChildObjectContext(f, true);
/*  329 */       this.delegate.writeStartObject();
/*      */     } else {
/*  331 */       this._filterContext = this._filterContext.createChildObjectContext(f, false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue) throws IOException {
/*  338 */     if (this._itemFilter == null) {
/*  339 */       this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, false);
/*      */       return;
/*      */     } 
/*  342 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  343 */       this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, true);
/*  344 */       this.delegate.writeStartObject(forValue);
/*      */       
/*      */       return;
/*      */     } 
/*  348 */     TokenFilter f = this._filterContext.checkValue(this._itemFilter);
/*  349 */     if (f == null) {
/*      */       return;
/*      */     }
/*      */     
/*  353 */     if (f != TokenFilter.INCLUDE_ALL) {
/*  354 */       f = f.filterStartObject();
/*      */     }
/*  356 */     if (f == TokenFilter.INCLUDE_ALL) {
/*  357 */       _checkParentPath();
/*  358 */       this._filterContext = this._filterContext.createChildObjectContext(f, true);
/*  359 */       this.delegate.writeStartObject(forValue);
/*  360 */     } else if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/*  361 */       _checkParentPath(false);
/*  362 */       this._filterContext = this._filterContext.createChildObjectContext(f, true);
/*  363 */       this.delegate.writeStartObject(forValue);
/*      */     } else {
/*  365 */       this._filterContext = this._filterContext.createChildObjectContext(f, false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue, int size) throws IOException {
/*  372 */     if (this._itemFilter == null) {
/*  373 */       this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, false);
/*      */       return;
/*      */     } 
/*  376 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/*  377 */       this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, true);
/*  378 */       this.delegate.writeStartObject(forValue, size);
/*      */       
/*      */       return;
/*      */     } 
/*  382 */     TokenFilter f = this._filterContext.checkValue(this._itemFilter);
/*  383 */     if (f == null) {
/*      */       return;
/*      */     }
/*      */     
/*  387 */     if (f != TokenFilter.INCLUDE_ALL) {
/*  388 */       f = f.filterStartObject();
/*      */     }
/*  390 */     if (f == TokenFilter.INCLUDE_ALL) {
/*  391 */       _checkParentPath();
/*  392 */       this._filterContext = this._filterContext.createChildObjectContext(f, true);
/*  393 */       this.delegate.writeStartObject(forValue, size);
/*      */     } else {
/*  395 */       this._filterContext = this._filterContext.createChildObjectContext(f, false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeEndObject() throws IOException {
/*  402 */     this._filterContext = this._filterContext.closeObject(this.delegate);
/*  403 */     if (this._filterContext != null) {
/*  404 */       this._itemFilter = this._filterContext.getFilter();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldName(String name) throws IOException {
/*  411 */     TokenFilter state = this._filterContext.setFieldName(name);
/*  412 */     if (state == null) {
/*  413 */       this._itemFilter = null;
/*      */       return;
/*      */     } 
/*  416 */     if (state == TokenFilter.INCLUDE_ALL) {
/*  417 */       this._itemFilter = state;
/*  418 */       this.delegate.writeFieldName(name);
/*      */       return;
/*      */     } 
/*  421 */     state = state.includeProperty(name);
/*  422 */     this._itemFilter = state;
/*  423 */     if (state == TokenFilter.INCLUDE_ALL) {
/*  424 */       _checkPropertyParentPath();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldName(SerializableString name) throws IOException {
/*  431 */     TokenFilter state = this._filterContext.setFieldName(name.getValue());
/*  432 */     if (state == null) {
/*  433 */       this._itemFilter = null;
/*      */       return;
/*      */     } 
/*  436 */     if (state == TokenFilter.INCLUDE_ALL) {
/*  437 */       this._itemFilter = state;
/*  438 */       this.delegate.writeFieldName(name);
/*      */       return;
/*      */     } 
/*  441 */     state = state.includeProperty(name.getValue());
/*  442 */     this._itemFilter = state;
/*  443 */     if (state == TokenFilter.INCLUDE_ALL) {
/*  444 */       _checkPropertyParentPath();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldId(long id) throws IOException {
/*  451 */     writeFieldName(Long.toString(id));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(String value) throws IOException {
/*  463 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  466 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  467 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  468 */       if (state == null) {
/*      */         return;
/*      */       }
/*  471 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  472 */         !state.includeString(value)) {
/*      */         return;
/*      */       }
/*      */       
/*  476 */       _checkParentPath();
/*      */     } 
/*  478 */     this.delegate.writeString(value);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(char[] text, int offset, int len) throws IOException {
/*  484 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  487 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  488 */       String value = new String(text, offset, len);
/*  489 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  490 */       if (state == null) {
/*      */         return;
/*      */       }
/*  493 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  494 */         !state.includeString(value)) {
/*      */         return;
/*      */       }
/*      */       
/*  498 */       _checkParentPath();
/*      */     } 
/*  500 */     this.delegate.writeString(text, offset, len);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(SerializableString value) throws IOException {
/*  506 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  509 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  510 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  511 */       if (state == null) {
/*      */         return;
/*      */       }
/*  514 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  515 */         !state.includeString(value.getValue())) {
/*      */         return;
/*      */       }
/*      */       
/*  519 */       _checkParentPath();
/*      */     } 
/*  521 */     this.delegate.writeString(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeString(Reader reader, int len) throws IOException {
/*  526 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  529 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  530 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  531 */       if (state == null) {
/*      */         return;
/*      */       }
/*  534 */       if (state != TokenFilter.INCLUDE_ALL)
/*      */       {
/*      */         
/*  537 */         if (!state.includeString(reader, len)) {
/*      */           return;
/*      */         }
/*      */       }
/*  541 */       _checkParentPath();
/*      */     } 
/*  543 */     this.delegate.writeString(reader, len);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/*  549 */     if (_checkRawValueWrite()) {
/*  550 */       this.delegate.writeRawUTF8String(text, offset, length);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
/*  558 */     if (_checkRawValueWrite()) {
/*  559 */       this.delegate.writeUTF8String(text, offset, length);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(String text) throws IOException {
/*  572 */     if (_checkRawValueWrite()) {
/*  573 */       this.delegate.writeRaw(text);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(String text, int offset, int len) throws IOException {
/*  580 */     if (_checkRawValueWrite()) {
/*  581 */       this.delegate.writeRaw(text, offset, len);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(SerializableString text) throws IOException {
/*  588 */     if (_checkRawValueWrite()) {
/*  589 */       this.delegate.writeRaw(text);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(char[] text, int offset, int len) throws IOException {
/*  596 */     if (_checkRawValueWrite()) {
/*  597 */       this.delegate.writeRaw(text, offset, len);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(char c) throws IOException {
/*  604 */     if (_checkRawValueWrite()) {
/*  605 */       this.delegate.writeRaw(c);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawValue(String text) throws IOException {
/*  612 */     if (_checkRawValueWrite()) {
/*  613 */       this.delegate.writeRawValue(text);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawValue(String text, int offset, int len) throws IOException {
/*  620 */     if (_checkRawValueWrite()) {
/*  621 */       this.delegate.writeRawValue(text, offset, len);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawValue(char[] text, int offset, int len) throws IOException {
/*  628 */     if (_checkRawValueWrite()) {
/*  629 */       this.delegate.writeRawValue(text, offset, len);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
/*  636 */     if (_checkBinaryWrite()) {
/*  637 */       this.delegate.writeBinary(b64variant, data, offset, len);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
/*  644 */     if (_checkBinaryWrite()) {
/*  645 */       return this.delegate.writeBinary(b64variant, data, dataLength);
/*      */     }
/*  647 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(short v) throws IOException {
/*  659 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  662 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  663 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  664 */       if (state == null) {
/*      */         return;
/*      */       }
/*  667 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  668 */         !state.includeNumber(v)) {
/*      */         return;
/*      */       }
/*      */       
/*  672 */       _checkParentPath();
/*      */     } 
/*  674 */     this.delegate.writeNumber(v);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(int v) throws IOException {
/*  680 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  683 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  684 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  685 */       if (state == null) {
/*      */         return;
/*      */       }
/*  688 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  689 */         !state.includeNumber(v)) {
/*      */         return;
/*      */       }
/*      */       
/*  693 */       _checkParentPath();
/*      */     } 
/*  695 */     this.delegate.writeNumber(v);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(long v) throws IOException {
/*  701 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  704 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  705 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  706 */       if (state == null) {
/*      */         return;
/*      */       }
/*  709 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  710 */         !state.includeNumber(v)) {
/*      */         return;
/*      */       }
/*      */       
/*  714 */       _checkParentPath();
/*      */     } 
/*  716 */     this.delegate.writeNumber(v);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigInteger v) throws IOException {
/*  722 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  725 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  726 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  727 */       if (state == null) {
/*      */         return;
/*      */       }
/*  730 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  731 */         !state.includeNumber(v)) {
/*      */         return;
/*      */       }
/*      */       
/*  735 */       _checkParentPath();
/*      */     } 
/*  737 */     this.delegate.writeNumber(v);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(double v) throws IOException {
/*  743 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  746 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  747 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  748 */       if (state == null) {
/*      */         return;
/*      */       }
/*  751 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  752 */         !state.includeNumber(v)) {
/*      */         return;
/*      */       }
/*      */       
/*  756 */       _checkParentPath();
/*      */     } 
/*  758 */     this.delegate.writeNumber(v);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(float v) throws IOException {
/*  764 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  767 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  768 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  769 */       if (state == null) {
/*      */         return;
/*      */       }
/*  772 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  773 */         !state.includeNumber(v)) {
/*      */         return;
/*      */       }
/*      */       
/*  777 */       _checkParentPath();
/*      */     } 
/*  779 */     this.delegate.writeNumber(v);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigDecimal v) throws IOException {
/*  785 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  788 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  789 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  790 */       if (state == null) {
/*      */         return;
/*      */       }
/*  793 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  794 */         !state.includeNumber(v)) {
/*      */         return;
/*      */       }
/*      */       
/*  798 */       _checkParentPath();
/*      */     } 
/*  800 */     this.delegate.writeNumber(v);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(String encodedValue) throws IOException, UnsupportedOperationException {
/*  806 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  809 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  810 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  811 */       if (state == null) {
/*      */         return;
/*      */       }
/*  814 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  815 */         !state.includeRawValue()) {
/*      */         return;
/*      */       }
/*      */       
/*  819 */       _checkParentPath();
/*      */     } 
/*  821 */     this.delegate.writeNumber(encodedValue);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(char[] encodedValueBuffer, int offset, int length) throws IOException, UnsupportedOperationException {
/*  827 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  830 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  831 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  832 */       if (state == null) {
/*      */         return;
/*      */       }
/*  835 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  836 */         !state.includeRawValue()) {
/*      */         return;
/*      */       }
/*      */       
/*  840 */       _checkParentPath();
/*      */     } 
/*  842 */     this.delegate.writeNumber(encodedValueBuffer, offset, length);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBoolean(boolean v) throws IOException {
/*  848 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  851 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  852 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  853 */       if (state == null) {
/*      */         return;
/*      */       }
/*  856 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  857 */         !state.includeBoolean(v)) {
/*      */         return;
/*      */       }
/*      */       
/*  861 */       _checkParentPath();
/*      */     } 
/*  863 */     this.delegate.writeBoolean(v);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNull() throws IOException {
/*  869 */     if (this._itemFilter == null) {
/*      */       return;
/*      */     }
/*  872 */     if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
/*  873 */       TokenFilter state = this._filterContext.checkValue(this._itemFilter);
/*  874 */       if (state == null) {
/*      */         return;
/*      */       }
/*  877 */       if (state != TokenFilter.INCLUDE_ALL && 
/*  878 */         !state.includeNull()) {
/*      */         return;
/*      */       }
/*      */       
/*  882 */       _checkParentPath();
/*      */     } 
/*  884 */     this.delegate.writeNull();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeOmittedField(String fieldName) throws IOException {
/*  896 */     if (this._itemFilter != null) {
/*  897 */       this.delegate.writeOmittedField(fieldName);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeObjectId(Object id) throws IOException {
/*  912 */     if (this._itemFilter != null) {
/*  913 */       this.delegate.writeObjectId(id);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeObjectRef(Object id) throws IOException {
/*  919 */     if (this._itemFilter != null) {
/*  920 */       this.delegate.writeObjectRef(id);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeTypeId(Object id) throws IOException {
/*  926 */     if (this._itemFilter != null) {
/*  927 */       this.delegate.writeTypeId(id);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _checkParentPath() throws IOException {
/* 1006 */     _checkParentPath(true);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _checkParentPath(boolean isMatch) throws IOException {
/* 1011 */     if (isMatch) {
/* 1012 */       this._matchCount++;
/*      */     }
/*      */     
/* 1015 */     if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
/* 1016 */       this._filterContext.writePath(this.delegate);
/* 1017 */     } else if (this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/*      */       
/* 1019 */       this._filterContext.ensureFieldNameWritten(this.delegate);
/*      */     } 
/*      */     
/* 1022 */     if (isMatch && !this._allowMultipleMatches)
/*      */     {
/* 1024 */       this._filterContext.skipParentChecks();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _checkPropertyParentPath() throws IOException {
/* 1037 */     this._matchCount++;
/* 1038 */     if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
/* 1039 */       this._filterContext.writePath(this.delegate);
/* 1040 */     } else if (this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
/*      */       
/* 1042 */       this._filterContext.ensureFieldNameWritten(this.delegate);
/*      */     } 
/*      */ 
/*      */     
/* 1046 */     if (!this._allowMultipleMatches)
/*      */     {
/* 1048 */       this._filterContext.skipParentChecks();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected boolean _checkBinaryWrite() throws IOException {
/* 1054 */     if (this._itemFilter == null) {
/* 1055 */       return false;
/*      */     }
/* 1057 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 1058 */       return true;
/*      */     }
/* 1060 */     if (this._itemFilter.includeBinary()) {
/* 1061 */       _checkParentPath();
/* 1062 */       return true;
/*      */     } 
/* 1064 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   protected boolean _checkRawValueWrite() throws IOException {
/* 1069 */     if (this._itemFilter == null) {
/* 1070 */       return false;
/*      */     }
/* 1072 */     if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 1073 */       return true;
/*      */     }
/* 1075 */     if (this._itemFilter.includeRawValue()) {
/* 1076 */       _checkParentPath();
/* 1077 */       return true;
/*      */     } 
/* 1079 */     return false;
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/filter/FilteringGeneratorDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */