/*     */ package com.fasterxml.jackson.core.base;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.JsonParseException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.exc.InputCoercionException;
/*     */ import com.fasterxml.jackson.core.io.JsonEOFException;
/*     */ import com.fasterxml.jackson.core.io.NumberInput;
/*     */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*     */ import com.fasterxml.jackson.core.util.VersionUtil;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ParserMinimalBase
/*     */   extends JsonParser
/*     */ {
/*     */   protected static final int INT_TAB = 9;
/*     */   protected static final int INT_LF = 10;
/*     */   protected static final int INT_CR = 13;
/*     */   protected static final int INT_SPACE = 32;
/*     */   protected static final int INT_LBRACKET = 91;
/*     */   protected static final int INT_RBRACKET = 93;
/*     */   protected static final int INT_LCURLY = 123;
/*     */   protected static final int INT_RCURLY = 125;
/*     */   protected static final int INT_QUOTE = 34;
/*     */   protected static final int INT_APOS = 39;
/*     */   protected static final int INT_BACKSLASH = 92;
/*     */   protected static final int INT_SLASH = 47;
/*     */   protected static final int INT_ASTERISK = 42;
/*     */   protected static final int INT_COLON = 58;
/*     */   protected static final int INT_COMMA = 44;
/*     */   protected static final int INT_HASH = 35;
/*     */   protected static final int INT_0 = 48;
/*     */   protected static final int INT_9 = 57;
/*     */   protected static final int INT_MINUS = 45;
/*     */   protected static final int INT_PLUS = 43;
/*     */   protected static final int INT_PERIOD = 46;
/*     */   protected static final int INT_e = 101;
/*     */   protected static final int INT_E = 69;
/*     */   protected static final char CHAR_NULL = '\000';
/*  62 */   protected static final byte[] NO_BYTES = new byte[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   protected static final int[] NO_INTS = new int[0];
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_UNKNOWN = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_INT = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_LONG = 2;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_BIGINT = 4;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_DOUBLE = 8;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int NR_BIGDECIMAL = 16;
/*     */ 
/*     */   
/*     */   protected static final int NR_FLOAT = 32;
/*     */ 
/*     */   
/*  97 */   protected static final BigInteger BI_MIN_INT = BigInteger.valueOf(-2147483648L);
/*  98 */   protected static final BigInteger BI_MAX_INT = BigInteger.valueOf(2147483647L);
/*     */   
/* 100 */   protected static final BigInteger BI_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
/* 101 */   protected static final BigInteger BI_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
/*     */   
/* 103 */   protected static final BigDecimal BD_MIN_LONG = new BigDecimal(BI_MIN_LONG);
/* 104 */   protected static final BigDecimal BD_MAX_LONG = new BigDecimal(BI_MAX_LONG);
/*     */   
/* 106 */   protected static final BigDecimal BD_MIN_INT = new BigDecimal(BI_MIN_INT);
/* 107 */   protected static final BigDecimal BD_MAX_INT = new BigDecimal(BI_MAX_INT);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final long MIN_INT_L = -2147483648L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final long MAX_INT_L = 2147483647L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final double MIN_LONG_D = -9.223372036854776E18D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final double MAX_LONG_D = 9.223372036854776E18D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final double MIN_INT_D = -2.147483648E9D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final double MAX_INT_D = 2.147483647E9D;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int MAX_ERROR_TOKEN_LENGTH = 256;
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonToken _currToken;
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonToken _lastClearedToken;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ParserMinimalBase() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ParserMinimalBase(int features) {
/* 160 */     super(features);
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
/*     */   public abstract JsonToken nextToken() throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken currentToken() {
/* 186 */     return this._currToken;
/*     */   } public int currentTokenId() {
/* 188 */     JsonToken t = this._currToken;
/* 189 */     return (t == null) ? 0 : t.id();
/*     */   }
/*     */   public JsonToken getCurrentToken() {
/* 192 */     return this._currToken;
/*     */   }
/*     */   @Deprecated
/*     */   public int getCurrentTokenId() {
/* 196 */     JsonToken t = this._currToken;
/* 197 */     return (t == null) ? 0 : t.id();
/*     */   }
/*     */   public boolean hasCurrentToken() {
/* 200 */     return (this._currToken != null);
/*     */   } public boolean hasTokenId(int id) {
/* 202 */     JsonToken t = this._currToken;
/* 203 */     if (t == null) {
/* 204 */       return (0 == id);
/*     */     }
/* 206 */     return (t.id() == id);
/*     */   }
/*     */   
/*     */   public boolean hasToken(JsonToken t) {
/* 210 */     return (this._currToken == t);
/*     */   }
/*     */   
/* 213 */   public boolean isExpectedStartArrayToken() { return (this._currToken == JsonToken.START_ARRAY); }
/* 214 */   public boolean isExpectedStartObjectToken() { return (this._currToken == JsonToken.START_OBJECT); } public boolean isExpectedNumberIntToken() {
/* 215 */     return (this._currToken == JsonToken.VALUE_NUMBER_INT);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken nextValue() throws IOException {
/* 221 */     JsonToken t = nextToken();
/* 222 */     if (t == JsonToken.FIELD_NAME) {
/* 223 */       t = nextToken();
/*     */     }
/* 225 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser skipChildren() throws IOException {
/* 231 */     if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY)
/*     */     {
/* 233 */       return this;
/*     */     }
/* 235 */     int open = 1;
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 240 */       JsonToken t = nextToken();
/* 241 */       if (t == null) {
/* 242 */         _handleEOF();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 247 */         return this;
/*     */       } 
/* 249 */       if (t.isStructStart()) {
/* 250 */         open++; continue;
/* 251 */       }  if (t.isStructEnd()) {
/* 252 */         if (--open == 0)
/* 253 */           return this; 
/*     */         continue;
/*     */       } 
/* 256 */       if (t == JsonToken.NOT_AVAILABLE)
/*     */       {
/*     */         
/* 259 */         _reportError("Not enough content available for `skipChildren()`: non-blocking parser? (%s)", 
/* 260 */             getClass().getName());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void _handleEOF() throws JsonParseException;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getCurrentName() throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void close() throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isClosed();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonStreamContext getParsingContext();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearCurrentToken() {
/* 294 */     if (this._currToken != null) {
/* 295 */       this._lastClearedToken = this._currToken;
/* 296 */       this._currToken = null;
/*     */     } 
/*     */   }
/*     */   public JsonToken getLastClearedToken() {
/* 300 */     return this._lastClearedToken;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void overrideCurrentName(String paramString);
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getText() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract char[] getTextCharacters() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean hasTextCharacters();
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getTextLength() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getTextOffset() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract byte[] getBinaryValue(Base64Variant paramBase64Variant) throws IOException;
/*     */ 
/*     */   
/*     */   public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
/* 333 */     JsonToken t = this._currToken;
/* 334 */     if (t != null) {
/* 335 */       String str; Object value; switch (t.id()) {
/*     */         case 6:
/* 337 */           str = getText().trim();
/* 338 */           if ("true".equals(str)) {
/* 339 */             return true;
/*     */           }
/* 341 */           if ("false".equals(str)) {
/* 342 */             return false;
/*     */           }
/* 344 */           if (_hasTextualNull(str)) {
/* 345 */             return false;
/*     */           }
/*     */           break;
/*     */         case 7:
/* 349 */           return (getIntValue() != 0);
/*     */         case 9:
/* 351 */           return true;
/*     */         case 10:
/*     */         case 11:
/* 354 */           return false;
/*     */         case 12:
/* 356 */           value = getEmbeddedObject();
/* 357 */           if (value instanceof Boolean) {
/* 358 */             return ((Boolean)value).booleanValue();
/*     */           }
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 364 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValueAsInt() throws IOException {
/* 370 */     JsonToken t = this._currToken;
/* 371 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 372 */       return getIntValue();
/*     */     }
/* 374 */     return getValueAsInt(0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValueAsInt(int defaultValue) throws IOException {
/* 380 */     JsonToken t = this._currToken;
/* 381 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 382 */       return getIntValue();
/*     */     }
/* 384 */     if (t != null) {
/* 385 */       String str; Object value; switch (t.id()) {
/*     */         case 6:
/* 387 */           str = getText();
/* 388 */           if (_hasTextualNull(str)) {
/* 389 */             return 0;
/*     */           }
/* 391 */           return NumberInput.parseAsInt(str, defaultValue);
/*     */         case 9:
/* 393 */           return 1;
/*     */         case 10:
/* 395 */           return 0;
/*     */         case 11:
/* 397 */           return 0;
/*     */         case 12:
/* 399 */           value = getEmbeddedObject();
/* 400 */           if (value instanceof Number)
/* 401 */             return ((Number)value).intValue(); 
/*     */           break;
/*     */       } 
/*     */     } 
/* 405 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getValueAsLong() throws IOException {
/* 411 */     JsonToken t = this._currToken;
/* 412 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 413 */       return getLongValue();
/*     */     }
/* 415 */     return getValueAsLong(0L);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getValueAsLong(long defaultValue) throws IOException {
/* 421 */     JsonToken t = this._currToken;
/* 422 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 423 */       return getLongValue();
/*     */     }
/* 425 */     if (t != null) {
/* 426 */       String str; Object value; switch (t.id()) {
/*     */         case 6:
/* 428 */           str = getText();
/* 429 */           if (_hasTextualNull(str)) {
/* 430 */             return 0L;
/*     */           }
/* 432 */           return NumberInput.parseAsLong(str, defaultValue);
/*     */         case 9:
/* 434 */           return 1L;
/*     */         case 10:
/*     */         case 11:
/* 437 */           return 0L;
/*     */         case 12:
/* 439 */           value = getEmbeddedObject();
/* 440 */           if (value instanceof Number)
/* 441 */             return ((Number)value).longValue(); 
/*     */           break;
/*     */       } 
/*     */     } 
/* 445 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getValueAsDouble(double defaultValue) throws IOException {
/* 451 */     JsonToken t = this._currToken;
/* 452 */     if (t != null) {
/* 453 */       String str; Object value; switch (t.id()) {
/*     */         case 6:
/* 455 */           str = getText();
/* 456 */           if (_hasTextualNull(str)) {
/* 457 */             return 0.0D;
/*     */           }
/* 459 */           return NumberInput.parseAsDouble(str, defaultValue);
/*     */         case 7:
/*     */         case 8:
/* 462 */           return getDoubleValue();
/*     */         case 9:
/* 464 */           return 1.0D;
/*     */         case 10:
/*     */         case 11:
/* 467 */           return 0.0D;
/*     */         case 12:
/* 469 */           value = getEmbeddedObject();
/* 470 */           if (value instanceof Number)
/* 471 */             return ((Number)value).doubleValue(); 
/*     */           break;
/*     */       } 
/*     */     } 
/* 475 */     return defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValueAsString() throws IOException {
/* 481 */     return getValueAsString((String)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValueAsString(String defaultValue) throws IOException {
/* 486 */     if (this._currToken == JsonToken.VALUE_STRING) {
/* 487 */       return getText();
/*     */     }
/* 489 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 490 */       return getCurrentName();
/*     */     }
/* 492 */     if (this._currToken == null || this._currToken == JsonToken.VALUE_NULL || !this._currToken.isScalarValue()) {
/* 493 */       return defaultValue;
/*     */     }
/* 495 */     return getText();
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
/*     */   protected void _decodeBase64(String str, ByteArrayBuilder builder, Base64Variant b64variant) throws IOException {
/*     */     try {
/* 518 */       b64variant.decode(str, builder);
/* 519 */     } catch (IllegalArgumentException e) {
/* 520 */       _reportError(e.getMessage());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _hasTextualNull(String value) {
/* 544 */     return "null".equals(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportUnexpectedNumberChar(int ch, String comment) throws JsonParseException {
/* 553 */     String msg = String.format("Unexpected character (%s) in numeric value", new Object[] { _getCharDesc(ch) });
/* 554 */     if (comment != null) {
/* 555 */       msg = msg + ": " + comment;
/*     */     }
/* 557 */     _reportError(msg);
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
/*     */   protected void reportInvalidNumber(String msg) throws JsonParseException {
/* 571 */     _reportError("Invalid numeric value: " + msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportOverflowInt() throws IOException {
/* 582 */     reportOverflowInt(getText());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportOverflowInt(String numDesc) throws IOException {
/* 587 */     reportOverflowInt(numDesc, currentToken());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportOverflowInt(String numDesc, JsonToken inputType) throws IOException {
/* 592 */     _reportInputCoercion(String.format("Numeric value (%s) out of range of int (%d - %s)", new Object[] {
/* 593 */             _longIntegerDesc(numDesc), Integer.valueOf(-2147483648), Integer.valueOf(2147483647)
/*     */           }), inputType, int.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportOverflowLong() throws IOException {
/* 605 */     reportOverflowLong(getText());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportOverflowLong(String numDesc) throws IOException {
/* 610 */     reportOverflowLong(numDesc, currentToken());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportOverflowLong(String numDesc, JsonToken inputType) throws IOException {
/* 615 */     _reportInputCoercion(String.format("Numeric value (%s) out of range of long (%d - %s)", new Object[] {
/* 616 */             _longIntegerDesc(numDesc), Long.valueOf(Long.MIN_VALUE), Long.valueOf(Long.MAX_VALUE)
/*     */           }), inputType, long.class);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _reportInputCoercion(String msg, JsonToken inputType, Class<?> targetType) throws InputCoercionException {
/* 623 */     throw new InputCoercionException(this, msg, inputType, targetType);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String _longIntegerDesc(String rawNum) {
/* 628 */     int rawLen = rawNum.length();
/* 629 */     if (rawLen < 1000) {
/* 630 */       return rawNum;
/*     */     }
/* 632 */     if (rawNum.startsWith("-")) {
/* 633 */       rawLen--;
/*     */     }
/* 635 */     return String.format("[Integer with %d digits]", new Object[] { Integer.valueOf(rawLen) });
/*     */   }
/*     */ 
/*     */   
/*     */   protected String _longNumberDesc(String rawNum) {
/* 640 */     int rawLen = rawNum.length();
/* 641 */     if (rawLen < 1000) {
/* 642 */       return rawNum;
/*     */     }
/* 644 */     if (rawNum.startsWith("-")) {
/* 645 */       rawLen--;
/*     */     }
/* 647 */     return String.format("[number with %d characters]", new Object[] { Integer.valueOf(rawLen) });
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _reportUnexpectedChar(int ch, String comment) throws JsonParseException {
/* 652 */     if (ch < 0) {
/* 653 */       _reportInvalidEOF();
/*     */     }
/* 655 */     String msg = String.format("Unexpected character (%s)", new Object[] { _getCharDesc(ch) });
/* 656 */     if (comment != null) {
/* 657 */       msg = msg + ": " + comment;
/*     */     }
/* 659 */     _reportError(msg);
/*     */   }
/*     */   
/*     */   protected void _reportInvalidEOF() throws JsonParseException {
/* 663 */     _reportInvalidEOF(" in " + this._currToken, this._currToken);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _reportInvalidEOFInValue(JsonToken type) throws JsonParseException {
/*     */     String msg;
/* 669 */     if (type == JsonToken.VALUE_STRING) {
/* 670 */       msg = " in a String value";
/* 671 */     } else if (type == JsonToken.VALUE_NUMBER_INT || type == JsonToken.VALUE_NUMBER_FLOAT) {
/*     */       
/* 673 */       msg = " in a Number value";
/*     */     } else {
/* 675 */       msg = " in a value";
/*     */     } 
/* 677 */     _reportInvalidEOF(msg, type);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _reportInvalidEOF(String msg, JsonToken currToken) throws JsonParseException {
/* 682 */     throw new JsonEOFException(this, currToken, "Unexpected end-of-input" + msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void _reportInvalidEOFInValue() throws JsonParseException {
/* 692 */     _reportInvalidEOF(" in a value");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void _reportInvalidEOF(String msg) throws JsonParseException {
/* 703 */     throw new JsonEOFException(this, null, "Unexpected end-of-input" + msg);
/*     */   }
/*     */   
/*     */   protected void _reportMissingRootWS(int ch) throws JsonParseException {
/* 707 */     _reportUnexpectedChar(ch, "Expected space separating root-level values");
/*     */   }
/*     */   
/*     */   protected void _throwInvalidSpace(int i) throws JsonParseException {
/* 711 */     char c = (char)i;
/* 712 */     String msg = "Illegal character (" + _getCharDesc(c) + "): only regular white space (\\r, \\n, \\t) is allowed between tokens";
/* 713 */     _reportError(msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String _getCharDesc(int ch) {
/* 724 */     char c = (char)ch;
/* 725 */     if (Character.isISOControl(c)) {
/* 726 */       return "(CTRL-CHAR, code " + ch + ")";
/*     */     }
/* 728 */     if (ch > 255) {
/* 729 */       return "'" + c + "' (code " + ch + " / 0x" + Integer.toHexString(ch) + ")";
/*     */     }
/* 731 */     return "'" + c + "' (code " + ch + ")";
/*     */   }
/*     */   
/*     */   protected final void _reportError(String msg) throws JsonParseException {
/* 735 */     throw _constructError(msg);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void _reportError(String msg, Object arg) throws JsonParseException {
/* 740 */     throw _constructError(String.format(msg, new Object[] { arg }));
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void _reportError(String msg, Object arg1, Object arg2) throws JsonParseException {
/* 745 */     throw _constructError(String.format(msg, new Object[] { arg1, arg2 }));
/*     */   }
/*     */   
/*     */   protected final void _wrapError(String msg, Throwable t) throws JsonParseException {
/* 749 */     throw _constructError(msg, t);
/*     */   }
/*     */   
/*     */   protected final void _throwInternal() {
/* 753 */     VersionUtil.throwInternal();
/*     */   }
/*     */   
/*     */   protected final JsonParseException _constructError(String msg, Throwable t) {
/* 757 */     return new JsonParseException(this, msg, t);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected static byte[] _asciiBytes(String str) {
/* 762 */     byte[] b = new byte[str.length()];
/* 763 */     for (int i = 0, len = str.length(); i < len; i++) {
/* 764 */       b[i] = (byte)str.charAt(i);
/*     */     }
/* 766 */     return b;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected static String _ascii(byte[] b) {
/*     */     try {
/* 772 */       return new String(b, "US-ASCII");
/* 773 */     } catch (IOException e) {
/* 774 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/base/ParserMinimalBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */