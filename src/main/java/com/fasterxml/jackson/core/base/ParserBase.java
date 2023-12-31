/*      */ package com.fasterxml.jackson.core.base;
/*      */ 
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonLocation;
/*      */ import com.fasterxml.jackson.core.JsonParseException;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonProcessingException;
/*      */ import com.fasterxml.jackson.core.JsonStreamContext;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.StreamReadCapability;
/*      */ import com.fasterxml.jackson.core.Version;
/*      */ import com.fasterxml.jackson.core.io.ContentReference;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.io.NumberInput;
/*      */ import com.fasterxml.jackson.core.json.DupDetector;
/*      */ import com.fasterxml.jackson.core.json.JsonReadContext;
/*      */ import com.fasterxml.jackson.core.json.PackageVersion;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*      */ import com.fasterxml.jackson.core.util.TextBuffer;
/*      */ import java.io.IOException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.Arrays;
/*      */ 
/*      */ public abstract class ParserBase
/*      */   extends ParserMinimalBase {
/*   28 */   protected static final JacksonFeatureSet<StreamReadCapability> JSON_READ_CAPABILITIES = DEFAULT_READ_CAPABILITIES;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final IOContext _ioContext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _closed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _inputPtr;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _inputEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long _currInputProcessed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   84 */   protected int _currInputRow = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _currInputRowStart;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long _tokenInputTotal;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  113 */   protected int _tokenInputRow = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _tokenInputCol;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonReadContext _parsingContext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _nextToken;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final TextBuffer _textBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _nameCopyBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _nameCopied;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ByteArrayBuilder _byteArrayBuilder;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] _binaryValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  188 */   protected int _numTypesValid = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _numberInt;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long _numberLong;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double _numberDouble;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BigInteger _numberBigInt;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BigDecimal _numberBigDecimal;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _numberNegative;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _intLength;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _fractLength;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _expLength;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ParserBase(IOContext ctxt, int features) {
/*  239 */     super(features);
/*  240 */     this._ioContext = ctxt;
/*  241 */     this._textBuffer = ctxt.constructTextBuffer();
/*      */     
/*  243 */     DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
/*  244 */     this._parsingContext = JsonReadContext.createRootContext(dups);
/*      */   }
/*      */   public Version version() {
/*  247 */     return PackageVersion.VERSION;
/*      */   }
/*      */   
/*      */   public Object getCurrentValue() {
/*  251 */     return this._parsingContext.getCurrentValue();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCurrentValue(Object v) {
/*  256 */     this._parsingContext.setCurrentValue(v);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser enable(JsonParser.Feature f) {
/*  267 */     this._features |= f.getMask();
/*  268 */     if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION && 
/*  269 */       this._parsingContext.getDupDetector() == null) {
/*  270 */       this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector(this));
/*      */     }
/*      */     
/*  273 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonParser disable(JsonParser.Feature f) {
/*  278 */     this._features &= f.getMask() ^ 0xFFFFFFFF;
/*  279 */     if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION) {
/*  280 */       this._parsingContext = this._parsingContext.withDupDetector(null);
/*      */     }
/*  282 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonParser setFeatureMask(int newMask) {
/*  288 */     int changes = this._features ^ newMask;
/*  289 */     if (changes != 0) {
/*  290 */       this._features = newMask;
/*  291 */       _checkStdFeatureChanges(newMask, changes);
/*      */     } 
/*  293 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonParser overrideStdFeatures(int values, int mask) {
/*  298 */     int oldState = this._features;
/*  299 */     int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/*  300 */     int changed = oldState ^ newState;
/*  301 */     if (changed != 0) {
/*  302 */       this._features = newState;
/*  303 */       _checkStdFeatureChanges(newState, changed);
/*      */     } 
/*  305 */     return this;
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
/*      */   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
/*  319 */     int f = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.getMask();
/*      */     
/*  321 */     if ((changedFeatures & f) != 0 && (
/*  322 */       newFeatureFlags & f) != 0) {
/*  323 */       if (this._parsingContext.getDupDetector() == null) {
/*  324 */         this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector(this));
/*      */       } else {
/*  326 */         this._parsingContext = this._parsingContext.withDupDetector(null);
/*      */       } 
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
/*      */   public String getCurrentName() throws IOException {
/*  344 */     if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/*  345 */       JsonReadContext parent = this._parsingContext.getParent();
/*  346 */       if (parent != null) {
/*  347 */         return parent.getCurrentName();
/*      */       }
/*      */     } 
/*  350 */     return this._parsingContext.getCurrentName();
/*      */   }
/*      */ 
/*      */   
/*      */   public void overrideCurrentName(String name) {
/*  355 */     JsonReadContext ctxt = this._parsingContext;
/*  356 */     if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/*  357 */       ctxt = ctxt.getParent();
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/*  362 */       ctxt.setCurrentName(name);
/*  363 */     } catch (IOException e) {
/*  364 */       throw new IllegalStateException(e);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void close() throws IOException {
/*  369 */     if (!this._closed) {
/*      */       
/*  371 */       this._inputPtr = Math.max(this._inputPtr, this._inputEnd);
/*  372 */       this._closed = true;
/*      */       try {
/*  374 */         _closeInput();
/*      */       }
/*      */       finally {
/*      */         
/*  378 */         _releaseBuffers();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*  383 */   public boolean isClosed() { return this._closed; } public JsonReadContext getParsingContext() {
/*  384 */     return this._parsingContext;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getTokenLocation() {
/*  393 */     return new JsonLocation(_contentReference(), -1L, 
/*  394 */         getTokenCharacterOffset(), 
/*  395 */         getTokenLineNr(), 
/*  396 */         getTokenColumnNr());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getCurrentLocation() {
/*  405 */     int col = this._inputPtr - this._currInputRowStart + 1;
/*  406 */     return new JsonLocation(_contentReference(), -1L, this._currInputProcessed + this._inputPtr, this._currInputRow, col);
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
/*      */   public boolean hasTextCharacters() {
/*  419 */     if (this._currToken == JsonToken.VALUE_STRING) return true; 
/*  420 */     if (this._currToken == JsonToken.FIELD_NAME) return this._nameCopied; 
/*  421 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant variant) throws IOException {
/*  428 */     if (this._binaryValue == null) {
/*  429 */       if (this._currToken != JsonToken.VALUE_STRING) {
/*  430 */         _reportError("Current token (" + this._currToken + ") not VALUE_STRING, can not access as binary");
/*      */       }
/*  432 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  433 */       _decodeBase64(getText(), builder, variant);
/*  434 */       this._binaryValue = builder.toByteArray();
/*      */     } 
/*  436 */     return this._binaryValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getTokenCharacterOffset() {
/*  445 */     return this._tokenInputTotal; } public int getTokenLineNr() {
/*  446 */     return this._tokenInputRow;
/*      */   }
/*      */   public int getTokenColumnNr() {
/*  449 */     int col = this._tokenInputCol;
/*  450 */     return (col < 0) ? col : (col + 1);
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
/*      */   protected void _releaseBuffers() throws IOException {
/*  477 */     this._textBuffer.releaseBuffers();
/*  478 */     char[] buf = this._nameCopyBuffer;
/*  479 */     if (buf != null) {
/*  480 */       this._nameCopyBuffer = null;
/*  481 */       this._ioContext.releaseNameCopyBuffer(buf);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _handleEOF() throws JsonParseException {
/*  492 */     if (!this._parsingContext.inRoot()) {
/*  493 */       String marker = this._parsingContext.inArray() ? "Array" : "Object";
/*  494 */       _reportInvalidEOF(String.format(": expected close marker for %s (start marker at %s)", new Object[] { marker, this._parsingContext
/*      */ 
/*      */               
/*  497 */               .startLocation(_contentReference()) }), (JsonToken)null);
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
/*      */   protected final int _eofAsNextChar() throws JsonParseException {
/*  511 */     _handleEOF();
/*  512 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteArrayBuilder _getByteArrayBuilder() {
/*  523 */     if (this._byteArrayBuilder == null) {
/*  524 */       this._byteArrayBuilder = new ByteArrayBuilder();
/*      */     } else {
/*  526 */       this._byteArrayBuilder.reset();
/*      */     } 
/*  528 */     return this._byteArrayBuilder;
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
/*      */   protected final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen) {
/*  541 */     if (fractLen < 1 && expLen < 1) {
/*  542 */       return resetInt(negative, intLen);
/*      */     }
/*  544 */     return resetFloat(negative, intLen, fractLen, expLen);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final JsonToken resetInt(boolean negative, int intLen) {
/*  549 */     this._numberNegative = negative;
/*  550 */     this._intLength = intLen;
/*  551 */     this._fractLength = 0;
/*  552 */     this._expLength = 0;
/*  553 */     this._numTypesValid = 0;
/*  554 */     return JsonToken.VALUE_NUMBER_INT;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final JsonToken resetFloat(boolean negative, int intLen, int fractLen, int expLen) {
/*  559 */     this._numberNegative = negative;
/*  560 */     this._intLength = intLen;
/*  561 */     this._fractLength = fractLen;
/*  562 */     this._expLength = expLen;
/*  563 */     this._numTypesValid = 0;
/*  564 */     return JsonToken.VALUE_NUMBER_FLOAT;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final JsonToken resetAsNaN(String valueStr, double value) {
/*  569 */     this._textBuffer.resetWithString(valueStr);
/*  570 */     this._numberDouble = value;
/*  571 */     this._numTypesValid = 8;
/*  572 */     return JsonToken.VALUE_NUMBER_FLOAT;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isNaN() {
/*  577 */     if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT && (
/*  578 */       this._numTypesValid & 0x8) != 0) {
/*      */       
/*  580 */       double d = this._numberDouble;
/*  581 */       return (Double.isNaN(d) || Double.isInfinite(d));
/*      */     } 
/*      */     
/*  584 */     return false;
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
/*      */   public Number getNumberValue() throws IOException {
/*  596 */     if (this._numTypesValid == 0) {
/*  597 */       _parseNumericValue(0);
/*      */     }
/*      */     
/*  600 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  601 */       if ((this._numTypesValid & 0x1) != 0) {
/*  602 */         return Integer.valueOf(this._numberInt);
/*      */       }
/*  604 */       if ((this._numTypesValid & 0x2) != 0) {
/*  605 */         return Long.valueOf(this._numberLong);
/*      */       }
/*  607 */       if ((this._numTypesValid & 0x4) != 0) {
/*  608 */         return this._numberBigInt;
/*      */       }
/*  610 */       _throwInternal();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  615 */     if ((this._numTypesValid & 0x10) != 0) {
/*  616 */       return this._numberBigDecimal;
/*      */     }
/*  618 */     if ((this._numTypesValid & 0x8) == 0) {
/*  619 */       _throwInternal();
/*      */     }
/*  621 */     return Double.valueOf(this._numberDouble);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Number getNumberValueExact() throws IOException {
/*  628 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  629 */       if (this._numTypesValid == 0) {
/*  630 */         _parseNumericValue(0);
/*      */       }
/*  632 */       if ((this._numTypesValid & 0x1) != 0) {
/*  633 */         return Integer.valueOf(this._numberInt);
/*      */       }
/*  635 */       if ((this._numTypesValid & 0x2) != 0) {
/*  636 */         return Long.valueOf(this._numberLong);
/*      */       }
/*  638 */       if ((this._numTypesValid & 0x4) != 0) {
/*  639 */         return this._numberBigInt;
/*      */       }
/*  641 */       _throwInternal();
/*      */     } 
/*      */     
/*  644 */     if (this._numTypesValid == 0) {
/*  645 */       _parseNumericValue(16);
/*      */     }
/*  647 */     if ((this._numTypesValid & 0x10) != 0) {
/*  648 */       return this._numberBigDecimal;
/*      */     }
/*  650 */     if ((this._numTypesValid & 0x8) == 0) {
/*  651 */       _throwInternal();
/*      */     }
/*  653 */     return Double.valueOf(this._numberDouble);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser.NumberType getNumberType() throws IOException {
/*  659 */     if (this._numTypesValid == 0) {
/*  660 */       _parseNumericValue(0);
/*      */     }
/*  662 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  663 */       if ((this._numTypesValid & 0x1) != 0) {
/*  664 */         return JsonParser.NumberType.INT;
/*      */       }
/*  666 */       if ((this._numTypesValid & 0x2) != 0) {
/*  667 */         return JsonParser.NumberType.LONG;
/*      */       }
/*  669 */       return JsonParser.NumberType.BIG_INTEGER;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  678 */     if ((this._numTypesValid & 0x10) != 0) {
/*  679 */       return JsonParser.NumberType.BIG_DECIMAL;
/*      */     }
/*  681 */     return JsonParser.NumberType.DOUBLE;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getIntValue() throws IOException {
/*  687 */     if ((this._numTypesValid & 0x1) == 0) {
/*  688 */       if (this._numTypesValid == 0) {
/*  689 */         return _parseIntValue();
/*      */       }
/*  691 */       if ((this._numTypesValid & 0x1) == 0) {
/*  692 */         convertNumberToInt();
/*      */       }
/*      */     } 
/*  695 */     return this._numberInt;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLongValue() throws IOException {
/*  701 */     if ((this._numTypesValid & 0x2) == 0) {
/*  702 */       if (this._numTypesValid == 0) {
/*  703 */         _parseNumericValue(2);
/*      */       }
/*  705 */       if ((this._numTypesValid & 0x2) == 0) {
/*  706 */         convertNumberToLong();
/*      */       }
/*      */     } 
/*  709 */     return this._numberLong;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public BigInteger getBigIntegerValue() throws IOException {
/*  715 */     if ((this._numTypesValid & 0x4) == 0) {
/*  716 */       if (this._numTypesValid == 0) {
/*  717 */         _parseNumericValue(4);
/*      */       }
/*  719 */       if ((this._numTypesValid & 0x4) == 0) {
/*  720 */         convertNumberToBigInteger();
/*      */       }
/*      */     } 
/*  723 */     return this._numberBigInt;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public float getFloatValue() throws IOException {
/*  729 */     double value = getDoubleValue();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  738 */     return (float)value;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public double getDoubleValue() throws IOException {
/*  744 */     if ((this._numTypesValid & 0x8) == 0) {
/*  745 */       if (this._numTypesValid == 0) {
/*  746 */         _parseNumericValue(8);
/*      */       }
/*  748 */       if ((this._numTypesValid & 0x8) == 0) {
/*  749 */         convertNumberToDouble();
/*      */       }
/*      */     } 
/*  752 */     return this._numberDouble;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getDecimalValue() throws IOException {
/*  758 */     if ((this._numTypesValid & 0x10) == 0) {
/*  759 */       if (this._numTypesValid == 0) {
/*  760 */         _parseNumericValue(16);
/*      */       }
/*  762 */       if ((this._numTypesValid & 0x10) == 0) {
/*  763 */         convertNumberToBigDecimal();
/*      */       }
/*      */     } 
/*  766 */     return this._numberBigDecimal;
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
/*      */   protected void _parseNumericValue(int expType) throws IOException {
/*  792 */     if (this._closed) {
/*  793 */       _reportError("Internal error: _parseNumericValue called when parser instance closed");
/*      */     }
/*      */ 
/*      */     
/*  797 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  798 */       int len = this._intLength;
/*      */       
/*  800 */       if (len <= 9) {
/*  801 */         int i = this._textBuffer.contentsAsInt(this._numberNegative);
/*  802 */         this._numberInt = i;
/*  803 */         this._numTypesValid = 1;
/*      */         return;
/*      */       } 
/*  806 */       if (len <= 18) {
/*  807 */         long l = this._textBuffer.contentsAsLong(this._numberNegative);
/*      */         
/*  809 */         if (len == 10) {
/*  810 */           if (this._numberNegative) {
/*  811 */             if (l >= -2147483648L) {
/*  812 */               this._numberInt = (int)l;
/*  813 */               this._numTypesValid = 1;
/*      */               
/*      */               return;
/*      */             } 
/*  817 */           } else if (l <= 2147483647L) {
/*  818 */             this._numberInt = (int)l;
/*  819 */             this._numTypesValid = 1;
/*      */             
/*      */             return;
/*      */           } 
/*      */         }
/*  824 */         this._numberLong = l;
/*  825 */         this._numTypesValid = 2;
/*      */         return;
/*      */       } 
/*  828 */       _parseSlowInt(expType);
/*      */       return;
/*      */     } 
/*  831 */     if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
/*  832 */       _parseSlowFloat(expType);
/*      */       return;
/*      */     } 
/*  835 */     _reportError("Current token (%s) not numeric, can not use numeric value accessors", this._currToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _parseIntValue() throws IOException {
/*  844 */     if (this._closed) {
/*  845 */       _reportError("Internal error: _parseNumericValue called when parser instance closed");
/*      */     }
/*      */     
/*  848 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT && 
/*  849 */       this._intLength <= 9) {
/*  850 */       int i = this._textBuffer.contentsAsInt(this._numberNegative);
/*  851 */       this._numberInt = i;
/*  852 */       this._numTypesValid = 1;
/*  853 */       return i;
/*      */     } 
/*      */ 
/*      */     
/*  857 */     _parseNumericValue(1);
/*  858 */     if ((this._numTypesValid & 0x1) == 0) {
/*  859 */       convertNumberToInt();
/*      */     }
/*  861 */     return this._numberInt;
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
/*      */   private void _parseSlowFloat(int expType) throws IOException {
/*      */     try {
/*  874 */       if (expType == 16) {
/*  875 */         this._numberBigDecimal = this._textBuffer.contentsAsDecimal();
/*  876 */         this._numTypesValid = 16;
/*      */       } else {
/*      */         
/*  879 */         this._numberDouble = this._textBuffer.contentsAsDouble();
/*  880 */         this._numTypesValid = 8;
/*      */       } 
/*  882 */     } catch (NumberFormatException nex) {
/*      */       
/*  884 */       _wrapError("Malformed numeric value (" + _longNumberDesc(this._textBuffer.contentsAsString()) + ")", nex);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void _parseSlowInt(int expType) throws IOException {
/*  890 */     String numStr = this._textBuffer.contentsAsString();
/*      */     try {
/*  892 */       int len = this._intLength;
/*  893 */       char[] buf = this._textBuffer.getTextBuffer();
/*  894 */       int offset = this._textBuffer.getTextOffset();
/*  895 */       if (this._numberNegative) {
/*  896 */         offset++;
/*      */       }
/*      */       
/*  899 */       if (NumberInput.inLongRange(buf, offset, len, this._numberNegative)) {
/*      */         
/*  901 */         this._numberLong = Long.parseLong(numStr);
/*  902 */         this._numTypesValid = 2;
/*      */       } else {
/*      */         
/*  905 */         if (expType == 1 || expType == 2) {
/*  906 */           _reportTooLongIntegral(expType, numStr);
/*      */         }
/*  908 */         if (expType == 8 || expType == 32) {
/*  909 */           this._numberDouble = NumberInput.parseDouble(numStr);
/*  910 */           this._numTypesValid = 8;
/*      */         } else {
/*      */           
/*  913 */           this._numberBigInt = new BigInteger(numStr);
/*  914 */           this._numTypesValid = 4;
/*      */         } 
/*      */       } 
/*  917 */     } catch (NumberFormatException nex) {
/*      */       
/*  919 */       _wrapError("Malformed numeric value (" + _longNumberDesc(numStr) + ")", nex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportTooLongIntegral(int expType, String rawNum) throws IOException {
/*  926 */     if (expType == 1) {
/*  927 */       reportOverflowInt(rawNum);
/*      */     } else {
/*  929 */       reportOverflowLong(rawNum);
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
/*      */   protected void convertNumberToInt() throws IOException {
/*  942 */     if ((this._numTypesValid & 0x2) != 0) {
/*      */       
/*  944 */       int result = (int)this._numberLong;
/*  945 */       if (result != this._numberLong) {
/*  946 */         reportOverflowInt(getText(), currentToken());
/*      */       }
/*  948 */       this._numberInt = result;
/*  949 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  950 */       if (BI_MIN_INT.compareTo(this._numberBigInt) > 0 || BI_MAX_INT
/*  951 */         .compareTo(this._numberBigInt) < 0) {
/*  952 */         reportOverflowInt();
/*      */       }
/*  954 */       this._numberInt = this._numberBigInt.intValue();
/*  955 */     } else if ((this._numTypesValid & 0x8) != 0) {
/*      */       
/*  957 */       if (this._numberDouble < -2.147483648E9D || this._numberDouble > 2.147483647E9D) {
/*  958 */         reportOverflowInt();
/*      */       }
/*  960 */       this._numberInt = (int)this._numberDouble;
/*  961 */     } else if ((this._numTypesValid & 0x10) != 0) {
/*  962 */       if (BD_MIN_INT.compareTo(this._numberBigDecimal) > 0 || BD_MAX_INT
/*  963 */         .compareTo(this._numberBigDecimal) < 0) {
/*  964 */         reportOverflowInt();
/*      */       }
/*  966 */       this._numberInt = this._numberBigDecimal.intValue();
/*      */     } else {
/*  968 */       _throwInternal();
/*      */     } 
/*  970 */     this._numTypesValid |= 0x1;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void convertNumberToLong() throws IOException {
/*  975 */     if ((this._numTypesValid & 0x1) != 0) {
/*  976 */       this._numberLong = this._numberInt;
/*  977 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  978 */       if (BI_MIN_LONG.compareTo(this._numberBigInt) > 0 || BI_MAX_LONG
/*  979 */         .compareTo(this._numberBigInt) < 0) {
/*  980 */         reportOverflowLong();
/*      */       }
/*  982 */       this._numberLong = this._numberBigInt.longValue();
/*  983 */     } else if ((this._numTypesValid & 0x8) != 0) {
/*      */       
/*  985 */       if (this._numberDouble < -9.223372036854776E18D || this._numberDouble > 9.223372036854776E18D) {
/*  986 */         reportOverflowLong();
/*      */       }
/*  988 */       this._numberLong = (long)this._numberDouble;
/*  989 */     } else if ((this._numTypesValid & 0x10) != 0) {
/*  990 */       if (BD_MIN_LONG.compareTo(this._numberBigDecimal) > 0 || BD_MAX_LONG
/*  991 */         .compareTo(this._numberBigDecimal) < 0) {
/*  992 */         reportOverflowLong();
/*      */       }
/*  994 */       this._numberLong = this._numberBigDecimal.longValue();
/*      */     } else {
/*  996 */       _throwInternal();
/*      */     } 
/*  998 */     this._numTypesValid |= 0x2;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void convertNumberToBigInteger() throws IOException {
/* 1003 */     if ((this._numTypesValid & 0x10) != 0) {
/*      */       
/* 1005 */       this._numberBigInt = this._numberBigDecimal.toBigInteger();
/* 1006 */     } else if ((this._numTypesValid & 0x2) != 0) {
/* 1007 */       this._numberBigInt = BigInteger.valueOf(this._numberLong);
/* 1008 */     } else if ((this._numTypesValid & 0x1) != 0) {
/* 1009 */       this._numberBigInt = BigInteger.valueOf(this._numberInt);
/* 1010 */     } else if ((this._numTypesValid & 0x8) != 0) {
/* 1011 */       this._numberBigInt = BigDecimal.valueOf(this._numberDouble).toBigInteger();
/*      */     } else {
/* 1013 */       _throwInternal();
/*      */     } 
/* 1015 */     this._numTypesValid |= 0x4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void convertNumberToDouble() throws IOException {
/* 1026 */     if ((this._numTypesValid & 0x10) != 0) {
/* 1027 */       this._numberDouble = this._numberBigDecimal.doubleValue();
/* 1028 */     } else if ((this._numTypesValid & 0x4) != 0) {
/* 1029 */       this._numberDouble = this._numberBigInt.doubleValue();
/* 1030 */     } else if ((this._numTypesValid & 0x2) != 0) {
/* 1031 */       this._numberDouble = this._numberLong;
/* 1032 */     } else if ((this._numTypesValid & 0x1) != 0) {
/* 1033 */       this._numberDouble = this._numberInt;
/*      */     } else {
/* 1035 */       _throwInternal();
/*      */     } 
/* 1037 */     this._numTypesValid |= 0x8;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void convertNumberToBigDecimal() throws IOException {
/* 1048 */     if ((this._numTypesValid & 0x8) != 0) {
/*      */ 
/*      */ 
/*      */       
/* 1052 */       this._numberBigDecimal = NumberInput.parseBigDecimal(getText());
/* 1053 */     } else if ((this._numTypesValid & 0x4) != 0) {
/* 1054 */       this._numberBigDecimal = new BigDecimal(this._numberBigInt);
/* 1055 */     } else if ((this._numTypesValid & 0x2) != 0) {
/* 1056 */       this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
/* 1057 */     } else if ((this._numTypesValid & 0x1) != 0) {
/* 1058 */       this._numberBigDecimal = BigDecimal.valueOf(this._numberInt);
/*      */     } else {
/* 1060 */       _throwInternal();
/*      */     } 
/* 1062 */     this._numTypesValid |= 0x10;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportMismatchedEndMarker(int actCh, char expCh) throws JsonParseException {
/* 1072 */     JsonReadContext ctxt = getParsingContext();
/* 1073 */     _reportError(String.format("Unexpected close marker '%s': expected '%c' (for %s starting at %s)", new Object[] {
/*      */             
/* 1075 */             Character.valueOf((char)actCh), Character.valueOf(expCh), ctxt.typeDesc(), ctxt
/* 1076 */             .startLocation(_contentReference())
/*      */           }));
/*      */   }
/*      */ 
/*      */   
/*      */   protected char _handleUnrecognizedCharacterEscape(char ch) throws JsonProcessingException {
/* 1082 */     if (isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
/* 1083 */       return ch;
/*      */     }
/*      */     
/* 1086 */     if (ch == '\'' && isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
/* 1087 */       return ch;
/*      */     }
/* 1089 */     _reportError("Unrecognized character escape " + _getCharDesc(ch));
/* 1090 */     return ch;
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
/*      */   protected void _throwUnquotedSpace(int i, String ctxtDesc) throws JsonParseException {
/* 1107 */     if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS) || i > 32) {
/* 1108 */       char c = (char)i;
/* 1109 */       String msg = "Illegal unquoted character (" + _getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
/* 1110 */       _reportError(msg);
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
/*      */   protected String _validJsonTokenList() throws IOException {
/* 1124 */     return _validJsonValueList();
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
/*      */   protected String _validJsonValueList() throws IOException {
/* 1138 */     if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 1139 */       return "(JSON String, Number (or 'NaN'/'INF'/'+INF'), Array, Object or token 'null', 'true' or 'false')";
/*      */     }
/* 1141 */     return "(JSON String, Number, Array, Object or token 'null', 'true' or 'false')";
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
/*      */   protected char _decodeEscaped() throws IOException {
/* 1160 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index) throws IOException {
/* 1166 */     if (ch != 92) {
/* 1167 */       throw reportInvalidBase64Char(b64variant, ch, index);
/*      */     }
/* 1169 */     int unescaped = _decodeEscaped();
/*      */     
/* 1171 */     if (unescaped <= 32 && 
/* 1172 */       index == 0) {
/* 1173 */       return -1;
/*      */     }
/*      */ 
/*      */     
/* 1177 */     int bits = b64variant.decodeBase64Char(unescaped);
/* 1178 */     if (bits < 0 && 
/* 1179 */       bits != -2) {
/* 1180 */       throw reportInvalidBase64Char(b64variant, unescaped, index);
/*      */     }
/*      */     
/* 1183 */     return bits;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final int _decodeBase64Escape(Base64Variant b64variant, char ch, int index) throws IOException {
/* 1188 */     if (ch != '\\') {
/* 1189 */       throw reportInvalidBase64Char(b64variant, ch, index);
/*      */     }
/* 1191 */     char unescaped = _decodeEscaped();
/*      */     
/* 1193 */     if (unescaped <= ' ' && 
/* 1194 */       index == 0) {
/* 1195 */       return -1;
/*      */     }
/*      */ 
/*      */     
/* 1199 */     int bits = b64variant.decodeBase64Char(unescaped);
/* 1200 */     if (bits < 0)
/*      */     {
/* 1202 */       if (bits != -2 || index < 2) {
/* 1203 */         throw reportInvalidBase64Char(b64variant, unescaped, index);
/*      */       }
/*      */     }
/* 1206 */     return bits;
/*      */   }
/*      */   
/*      */   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex) throws IllegalArgumentException {
/* 1210 */     return reportInvalidBase64Char(b64variant, ch, bindex, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex, String msg) throws IllegalArgumentException {
/*      */     String base;
/* 1219 */     if (ch <= 32) {
/* 1220 */       base = String.format("Illegal white space character (code 0x%s) as character #%d of 4-char base64 unit: can only used between units", new Object[] {
/* 1221 */             Integer.toHexString(ch), Integer.valueOf(bindex + 1) });
/* 1222 */     } else if (b64variant.usesPaddingChar(ch)) {
/* 1223 */       base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
/* 1224 */     } else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
/*      */       
/* 1226 */       base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*      */     } else {
/* 1228 */       base = "Illegal character '" + (char)ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*      */     } 
/* 1230 */     if (msg != null) {
/* 1231 */       base = base + ": " + msg;
/*      */     }
/* 1233 */     return new IllegalArgumentException(base);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _handleBase64MissingPadding(Base64Variant b64variant) throws IOException {
/* 1239 */     _reportError(b64variant.missingPaddingMessage());
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
/*      */   @Deprecated
/*      */   protected Object _getSourceReference() {
/* 1255 */     if (JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features)) {
/* 1256 */       return this._ioContext.contentReference().getRawContent();
/*      */     }
/* 1258 */     return null;
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
/*      */   protected ContentReference _contentReference() {
/* 1270 */     if (JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features)) {
/* 1271 */       return this._ioContext.contentReference();
/*      */     }
/* 1273 */     return ContentReference.unknown();
/*      */   }
/*      */ 
/*      */   
/*      */   protected static int[] growArrayBy(int[] arr, int more) {
/* 1278 */     if (arr == null) {
/* 1279 */       return new int[more];
/*      */     }
/* 1281 */     return Arrays.copyOf(arr, arr.length + more);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void loadMoreGuaranteed() throws IOException {
/* 1293 */     if (!loadMore()) _reportInvalidEOF(); 
/*      */   }
/*      */   @Deprecated
/*      */   protected boolean loadMore() throws IOException {
/* 1297 */     return false;
/*      */   }
/*      */   
/*      */   protected void _finishString() throws IOException {}
/*      */   
/*      */   protected abstract void _closeInput() throws IOException;
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/base/ParserBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */