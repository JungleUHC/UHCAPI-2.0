/*     */ package com.fasterxml.jackson.core.json.async;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParseException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.StreamReadCapability;
/*     */ import com.fasterxml.jackson.core.base.ParserBase;
/*     */ import com.fasterxml.jackson.core.io.IOContext;
/*     */ import com.fasterxml.jackson.core.json.JsonReadContext;
/*     */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*     */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*     */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Writer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class NonBlockingJsonParserBase
/*     */   extends ParserBase
/*     */ {
/*     */   protected static final int MAJOR_INITIAL = 0;
/*     */   protected static final int MAJOR_ROOT = 1;
/*     */   protected static final int MAJOR_OBJECT_FIELD_FIRST = 2;
/*     */   protected static final int MAJOR_OBJECT_FIELD_NEXT = 3;
/*     */   protected static final int MAJOR_OBJECT_VALUE = 4;
/*     */   protected static final int MAJOR_ARRAY_ELEMENT_FIRST = 5;
/*     */   protected static final int MAJOR_ARRAY_ELEMENT_NEXT = 6;
/*     */   protected static final int MAJOR_CLOSED = 7;
/*     */   protected static final int MINOR_ROOT_BOM = 1;
/*     */   protected static final int MINOR_ROOT_NEED_SEPARATOR = 2;
/*     */   protected static final int MINOR_ROOT_GOT_SEPARATOR = 3;
/*     */   protected static final int MINOR_FIELD_LEADING_WS = 4;
/*     */   protected static final int MINOR_FIELD_LEADING_COMMA = 5;
/*     */   protected static final int MINOR_FIELD_NAME = 7;
/*     */   protected static final int MINOR_FIELD_NAME_ESCAPE = 8;
/*     */   protected static final int MINOR_FIELD_APOS_NAME = 9;
/*     */   protected static final int MINOR_FIELD_UNQUOTED_NAME = 10;
/*     */   protected static final int MINOR_VALUE_LEADING_WS = 12;
/*     */   protected static final int MINOR_VALUE_EXPECTING_COMMA = 13;
/*     */   protected static final int MINOR_VALUE_EXPECTING_COLON = 14;
/*     */   protected static final int MINOR_VALUE_WS_AFTER_COMMA = 15;
/*     */   protected static final int MINOR_VALUE_TOKEN_NULL = 16;
/*     */   protected static final int MINOR_VALUE_TOKEN_TRUE = 17;
/*     */   protected static final int MINOR_VALUE_TOKEN_FALSE = 18;
/*     */   protected static final int MINOR_VALUE_TOKEN_NON_STD = 19;
/*     */   protected static final int MINOR_NUMBER_MINUS = 23;
/*     */   protected static final int MINOR_NUMBER_ZERO = 24;
/*     */   protected static final int MINOR_NUMBER_MINUSZERO = 25;
/*     */   protected static final int MINOR_NUMBER_INTEGER_DIGITS = 26;
/*     */   protected static final int MINOR_NUMBER_FRACTION_DIGITS = 30;
/*     */   protected static final int MINOR_NUMBER_EXPONENT_MARKER = 31;
/*     */   protected static final int MINOR_NUMBER_EXPONENT_DIGITS = 32;
/*     */   protected static final int MINOR_VALUE_STRING = 40;
/*     */   protected static final int MINOR_VALUE_STRING_ESCAPE = 41;
/*     */   protected static final int MINOR_VALUE_STRING_UTF8_2 = 42;
/*     */   protected static final int MINOR_VALUE_STRING_UTF8_3 = 43;
/*     */   protected static final int MINOR_VALUE_STRING_UTF8_4 = 44;
/*     */   protected static final int MINOR_VALUE_APOS_STRING = 45;
/*     */   protected static final int MINOR_VALUE_TOKEN_ERROR = 50;
/*     */   protected static final int MINOR_COMMENT_LEADING_SLASH = 51;
/*     */   protected static final int MINOR_COMMENT_CLOSING_ASTERISK = 52;
/*     */   protected static final int MINOR_COMMENT_C = 53;
/*     */   protected static final int MINOR_COMMENT_CPP = 54;
/*     */   protected static final int MINOR_COMMENT_YAML = 55;
/*     */   protected final ByteQuadsCanonicalizer _symbols;
/* 147 */   protected int[] _quadBuffer = new int[8];
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _quadLength;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _quad1;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _pending32;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _pendingBytes;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _quoted32;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _quotedDigits;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _majorState;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _majorStateAfterValue;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _minorState;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _minorStateAfterSplit;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _endOfInput = false;
/*     */ 
/*     */   
/*     */   protected static final int NON_STD_TOKEN_NAN = 0;
/*     */ 
/*     */   
/*     */   protected static final int NON_STD_TOKEN_INFINITY = 1;
/*     */ 
/*     */   
/*     */   protected static final int NON_STD_TOKEN_PLUS_INFINITY = 2;
/*     */ 
/*     */   
/*     */   protected static final int NON_STD_TOKEN_MINUS_INFINITY = 3;
/*     */ 
/*     */   
/* 206 */   protected static final String[] NON_STD_TOKENS = new String[] { "NaN", "Infinity", "+Infinity", "-Infinity" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 211 */   protected static final double[] NON_STD_TOKEN_VALUES = new double[] { Double.NaN, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _nonStdTokenType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 233 */   protected int _currBufferStart = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 242 */   protected int _currInputRowAlt = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NonBlockingJsonParserBase(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
/* 253 */     super(ctxt, parserFeatures);
/* 254 */     this._symbols = sym;
/* 255 */     this._currToken = null;
/* 256 */     this._majorState = 0;
/* 257 */     this._majorStateAfterValue = 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public ObjectCodec getCodec() {
/* 262 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCodec(ObjectCodec c) {
/* 267 */     throw new UnsupportedOperationException("Can not use ObjectMapper with non-blocking parser");
/*     */   }
/*     */   
/*     */   public boolean canParseAsync() {
/* 271 */     return true;
/*     */   }
/*     */   
/*     */   public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
/* 275 */     return JSON_READ_CAPABILITIES;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ByteQuadsCanonicalizer symbolTableForTests() {
/* 285 */     return this._symbols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int releaseBuffered(OutputStream paramOutputStream) throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _releaseBuffers() throws IOException {
/* 300 */     super._releaseBuffers();
/*     */     
/* 302 */     this._symbols.release();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getInputSource() {
/* 309 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _closeInput() throws IOException {
/* 317 */     this._currBufferStart = 0;
/* 318 */     this._inputEnd = 0;
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
/*     */   public boolean hasTextCharacters() {
/* 330 */     if (this._currToken == JsonToken.VALUE_STRING)
/*     */     {
/* 332 */       return this._textBuffer.hasTextAsCharacters();
/*     */     }
/* 334 */     if (this._currToken == JsonToken.FIELD_NAME)
/*     */     {
/* 336 */       return this._nameCopied;
/*     */     }
/*     */     
/* 339 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonLocation getCurrentLocation() {
/* 345 */     int col = this._inputPtr - this._currInputRowStart + 1;
/*     */     
/* 347 */     int row = Math.max(this._currInputRow, this._currInputRowAlt);
/* 348 */     return new JsonLocation(_contentReference(), this._currInputProcessed + (this._inputPtr - this._currBufferStart), -1L, row, col);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonLocation getTokenLocation() {
/* 356 */     return new JsonLocation(_contentReference(), this._tokenInputTotal, -1L, this._tokenInputRow, this._tokenInputCol);
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
/*     */   public String getText() throws IOException {
/* 375 */     if (this._currToken == JsonToken.VALUE_STRING) {
/* 376 */       return this._textBuffer.contentsAsString();
/*     */     }
/* 378 */     return _getText2(this._currToken);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final String _getText2(JsonToken t) {
/* 383 */     if (t == null) {
/* 384 */       return null;
/*     */     }
/* 386 */     switch (t.id()) {
/*     */       case -1:
/* 388 */         return null;
/*     */       case 5:
/* 390 */         return this._parsingContext.getCurrentName();
/*     */       
/*     */       case 6:
/*     */       case 7:
/*     */       case 8:
/* 395 */         return this._textBuffer.contentsAsString();
/*     */     } 
/* 397 */     return t.asString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getText(Writer writer) throws IOException {
/* 404 */     JsonToken t = this._currToken;
/* 405 */     if (t == JsonToken.VALUE_STRING) {
/* 406 */       return this._textBuffer.contentsToWriter(writer);
/*     */     }
/* 408 */     if (t == JsonToken.FIELD_NAME) {
/* 409 */       String n = this._parsingContext.getCurrentName();
/* 410 */       writer.write(n);
/* 411 */       return n.length();
/*     */     } 
/* 413 */     if (t != null) {
/* 414 */       if (t.isNumeric()) {
/* 415 */         return this._textBuffer.contentsToWriter(writer);
/*     */       }
/* 417 */       if (t == JsonToken.NOT_AVAILABLE) {
/* 418 */         _reportError("Current token not available: can not call this method");
/*     */       }
/* 420 */       char[] ch = t.asCharArray();
/* 421 */       writer.write(ch);
/* 422 */       return ch.length;
/*     */     } 
/* 424 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValueAsString() throws IOException {
/* 433 */     if (this._currToken == JsonToken.VALUE_STRING) {
/* 434 */       return this._textBuffer.contentsAsString();
/*     */     }
/* 436 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 437 */       return getCurrentName();
/*     */     }
/* 439 */     return super.getValueAsString(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValueAsString(String defValue) throws IOException {
/* 446 */     if (this._currToken == JsonToken.VALUE_STRING) {
/* 447 */       return this._textBuffer.contentsAsString();
/*     */     }
/* 449 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 450 */       return getCurrentName();
/*     */     }
/* 452 */     return super.getValueAsString(defValue);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] getTextCharacters() throws IOException {
/* 458 */     if (this._currToken != null) {
/* 459 */       switch (this._currToken.id()) {
/*     */         
/*     */         case 5:
/* 462 */           if (!this._nameCopied) {
/* 463 */             String name = this._parsingContext.getCurrentName();
/* 464 */             int nameLen = name.length();
/* 465 */             if (this._nameCopyBuffer == null) {
/* 466 */               this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/* 467 */             } else if (this._nameCopyBuffer.length < nameLen) {
/* 468 */               this._nameCopyBuffer = new char[nameLen];
/*     */             } 
/* 470 */             name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/* 471 */             this._nameCopied = true;
/*     */           } 
/* 473 */           return this._nameCopyBuffer;
/*     */ 
/*     */         
/*     */         case 6:
/*     */         case 7:
/*     */         case 8:
/* 479 */           return this._textBuffer.getTextBuffer();
/*     */       } 
/*     */       
/* 482 */       return this._currToken.asCharArray();
/*     */     } 
/*     */     
/* 485 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTextLength() throws IOException {
/* 491 */     if (this._currToken != null) {
/* 492 */       switch (this._currToken.id()) {
/*     */         
/*     */         case 5:
/* 495 */           return this._parsingContext.getCurrentName().length();
/*     */         
/*     */         case 6:
/*     */         case 7:
/*     */         case 8:
/* 500 */           return this._textBuffer.size();
/*     */       } 
/*     */       
/* 503 */       return (this._currToken.asCharArray()).length;
/*     */     } 
/*     */     
/* 506 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTextOffset() throws IOException {
/* 513 */     if (this._currToken != null) {
/* 514 */       switch (this._currToken.id()) {
/*     */         case 5:
/* 516 */           return 0;
/*     */         
/*     */         case 6:
/*     */         case 7:
/*     */         case 8:
/* 521 */           return this._textBuffer.getTextOffset();
/*     */       } 
/*     */     
/*     */     }
/* 525 */     return 0;
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
/*     */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/* 537 */     if (this._currToken != JsonToken.VALUE_STRING) {
/* 538 */       _reportError("Current token (%s) not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary", this._currToken);
/*     */     }
/*     */     
/* 541 */     if (this._binaryValue == null) {
/*     */       
/* 543 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/* 544 */       _decodeBase64(getText(), builder, b64variant);
/* 545 */       this._binaryValue = builder.toByteArray();
/*     */     } 
/* 547 */     return this._binaryValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/* 553 */     byte[] b = getBinaryValue(b64variant);
/* 554 */     out.write(b);
/* 555 */     return b.length;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getEmbeddedObject() throws IOException {
/* 561 */     if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 562 */       return this._binaryValue;
/*     */     }
/* 564 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonToken _startArrayScope() throws IOException {
/* 575 */     this._parsingContext = this._parsingContext.createChildArrayContext(-1, -1);
/* 576 */     this._majorState = 5;
/* 577 */     this._majorStateAfterValue = 6;
/* 578 */     return this._currToken = JsonToken.START_ARRAY;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final JsonToken _startObjectScope() throws IOException {
/* 583 */     this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
/* 584 */     this._majorState = 2;
/* 585 */     this._majorStateAfterValue = 3;
/* 586 */     return this._currToken = JsonToken.START_OBJECT;
/*     */   }
/*     */   
/*     */   protected final JsonToken _closeArrayScope() throws IOException {
/*     */     int st;
/* 591 */     if (!this._parsingContext.inArray()) {
/* 592 */       _reportMismatchedEndMarker(93, '}');
/*     */     }
/* 594 */     JsonReadContext ctxt = this._parsingContext.getParent();
/* 595 */     this._parsingContext = ctxt;
/*     */     
/* 597 */     if (ctxt.inObject()) {
/* 598 */       st = 3;
/* 599 */     } else if (ctxt.inArray()) {
/* 600 */       st = 6;
/*     */     } else {
/* 602 */       st = 1;
/*     */     } 
/* 604 */     this._majorState = st;
/* 605 */     this._majorStateAfterValue = st;
/* 606 */     return this._currToken = JsonToken.END_ARRAY;
/*     */   }
/*     */   
/*     */   protected final JsonToken _closeObjectScope() throws IOException {
/*     */     int st;
/* 611 */     if (!this._parsingContext.inObject()) {
/* 612 */       _reportMismatchedEndMarker(125, ']');
/*     */     }
/* 614 */     JsonReadContext ctxt = this._parsingContext.getParent();
/* 615 */     this._parsingContext = ctxt;
/*     */     
/* 617 */     if (ctxt.inObject()) {
/* 618 */       st = 3;
/* 619 */     } else if (ctxt.inArray()) {
/* 620 */       st = 6;
/*     */     } else {
/* 622 */       st = 1;
/*     */     } 
/* 624 */     this._majorState = st;
/* 625 */     this._majorStateAfterValue = st;
/* 626 */     return this._currToken = JsonToken.END_OBJECT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _findName(int q1, int lastQuadBytes) throws JsonParseException {
/* 637 */     q1 = _padLastQuad(q1, lastQuadBytes);
/*     */     
/* 639 */     String name = this._symbols.findName(q1);
/* 640 */     if (name != null) {
/* 641 */       return name;
/*     */     }
/*     */     
/* 644 */     this._quadBuffer[0] = q1;
/* 645 */     return _addName(this._quadBuffer, 1, lastQuadBytes);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final String _findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
/* 650 */     q2 = _padLastQuad(q2, lastQuadBytes);
/*     */     
/* 652 */     String name = this._symbols.findName(q1, q2);
/* 653 */     if (name != null) {
/* 654 */       return name;
/*     */     }
/*     */     
/* 657 */     this._quadBuffer[0] = q1;
/* 658 */     this._quadBuffer[1] = q2;
/* 659 */     return _addName(this._quadBuffer, 2, lastQuadBytes);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final String _findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException {
/* 664 */     q3 = _padLastQuad(q3, lastQuadBytes);
/* 665 */     String name = this._symbols.findName(q1, q2, q3);
/* 666 */     if (name != null) {
/* 667 */       return name;
/*     */     }
/* 669 */     int[] quads = this._quadBuffer;
/* 670 */     quads[0] = q1;
/* 671 */     quads[1] = q2;
/* 672 */     quads[2] = _padLastQuad(q3, lastQuadBytes);
/* 673 */     return _addName(quads, 3, lastQuadBytes);
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
/*     */   protected final String _addName(int[] quads, int qlen, int lastQuadBytes) throws JsonParseException {
/* 687 */     int lastQuad, byteLen = (qlen << 2) - 4 + lastQuadBytes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 696 */     if (lastQuadBytes < 4) {
/* 697 */       lastQuad = quads[qlen - 1];
/*     */       
/* 699 */       quads[qlen - 1] = lastQuad << 4 - lastQuadBytes << 3;
/*     */     } else {
/* 701 */       lastQuad = 0;
/*     */     } 
/*     */ 
/*     */     
/* 705 */     char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 706 */     int cix = 0;
/*     */     
/* 708 */     for (int ix = 0; ix < byteLen; ) {
/* 709 */       int ch = quads[ix >> 2];
/* 710 */       int byteIx = ix & 0x3;
/* 711 */       ch = ch >> 3 - byteIx << 3 & 0xFF;
/* 712 */       ix++;
/*     */       
/* 714 */       if (ch > 127) {
/*     */         int needed;
/* 716 */         if ((ch & 0xE0) == 192) {
/* 717 */           ch &= 0x1F;
/* 718 */           needed = 1;
/* 719 */         } else if ((ch & 0xF0) == 224) {
/* 720 */           ch &= 0xF;
/* 721 */           needed = 2;
/* 722 */         } else if ((ch & 0xF8) == 240) {
/* 723 */           ch &= 0x7;
/* 724 */           needed = 3;
/*     */         } else {
/* 726 */           _reportInvalidInitial(ch);
/* 727 */           needed = ch = 1;
/*     */         } 
/* 729 */         if (ix + needed > byteLen) {
/* 730 */           _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*     */         }
/*     */ 
/*     */         
/* 734 */         int ch2 = quads[ix >> 2];
/* 735 */         byteIx = ix & 0x3;
/* 736 */         ch2 >>= 3 - byteIx << 3;
/* 737 */         ix++;
/*     */         
/* 739 */         if ((ch2 & 0xC0) != 128) {
/* 740 */           _reportInvalidOther(ch2);
/*     */         }
/* 742 */         ch = ch << 6 | ch2 & 0x3F;
/* 743 */         if (needed > 1) {
/* 744 */           ch2 = quads[ix >> 2];
/* 745 */           byteIx = ix & 0x3;
/* 746 */           ch2 >>= 3 - byteIx << 3;
/* 747 */           ix++;
/*     */           
/* 749 */           if ((ch2 & 0xC0) != 128) {
/* 750 */             _reportInvalidOther(ch2);
/*     */           }
/* 752 */           ch = ch << 6 | ch2 & 0x3F;
/* 753 */           if (needed > 2) {
/* 754 */             ch2 = quads[ix >> 2];
/* 755 */             byteIx = ix & 0x3;
/* 756 */             ch2 >>= 3 - byteIx << 3;
/* 757 */             ix++;
/* 758 */             if ((ch2 & 0xC0) != 128) {
/* 759 */               _reportInvalidOther(ch2 & 0xFF);
/*     */             }
/* 761 */             ch = ch << 6 | ch2 & 0x3F;
/*     */           } 
/*     */         } 
/* 764 */         if (needed > 2) {
/* 765 */           ch -= 65536;
/* 766 */           if (cix >= cbuf.length) {
/* 767 */             cbuf = this._textBuffer.expandCurrentSegment();
/*     */           }
/* 769 */           cbuf[cix++] = (char)(55296 + (ch >> 10));
/* 770 */           ch = 0xDC00 | ch & 0x3FF;
/*     */         } 
/*     */       } 
/* 773 */       if (cix >= cbuf.length) {
/* 774 */         cbuf = this._textBuffer.expandCurrentSegment();
/*     */       }
/* 776 */       cbuf[cix++] = (char)ch;
/*     */     } 
/*     */ 
/*     */     
/* 780 */     String baseName = new String(cbuf, 0, cix);
/*     */     
/* 782 */     if (lastQuadBytes < 4) {
/* 783 */       quads[qlen - 1] = lastQuad;
/*     */     }
/* 785 */     return this._symbols.addName(baseName, quads, qlen);
/*     */   }
/*     */ 
/*     */   
/*     */   protected static final int _padLastQuad(int q, int bytes) {
/* 790 */     return (bytes == 4) ? q : (q | -1 << bytes << 3);
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
/*     */   protected final JsonToken _eofAsNextToken() throws IOException {
/* 802 */     this._majorState = 7;
/* 803 */     if (!this._parsingContext.inRoot()) {
/* 804 */       _handleEOF();
/*     */     }
/* 806 */     close();
/* 807 */     return this._currToken = null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final JsonToken _fieldComplete(String name) throws IOException {
/* 812 */     this._majorState = 4;
/* 813 */     this._parsingContext.setCurrentName(name);
/* 814 */     return this._currToken = JsonToken.FIELD_NAME;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final JsonToken _valueComplete(JsonToken t) throws IOException {
/* 819 */     this._majorState = this._majorStateAfterValue;
/* 820 */     this._currToken = t;
/* 821 */     return t;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final JsonToken _valueCompleteInt(int value, String asText) throws IOException {
/* 826 */     this._textBuffer.resetWithString(asText);
/* 827 */     this._intLength = asText.length();
/* 828 */     this._numTypesValid = 1;
/* 829 */     this._numberInt = value;
/* 830 */     this._majorState = this._majorStateAfterValue;
/* 831 */     JsonToken t = JsonToken.VALUE_NUMBER_INT;
/* 832 */     this._currToken = t;
/* 833 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonToken _valueNonStdNumberComplete(int type) throws IOException {
/* 839 */     String tokenStr = NON_STD_TOKENS[type];
/* 840 */     this._textBuffer.resetWithString(tokenStr);
/* 841 */     if (!isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 842 */       _reportError("Non-standard token '%s': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow", tokenStr);
/*     */     }
/*     */     
/* 845 */     this._intLength = 0;
/* 846 */     this._numTypesValid = 8;
/* 847 */     this._numberDouble = NON_STD_TOKEN_VALUES[type];
/* 848 */     this._majorState = this._majorStateAfterValue;
/* 849 */     return this._currToken = JsonToken.VALUE_NUMBER_FLOAT;
/*     */   }
/*     */   
/*     */   protected final String _nonStdToken(int type) {
/* 853 */     return NON_STD_TOKENS[type];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void _updateTokenLocation() {
/* 864 */     this._tokenInputRow = Math.max(this._currInputRow, this._currInputRowAlt);
/* 865 */     int ptr = this._inputPtr;
/* 866 */     this._tokenInputCol = ptr - this._currInputRowStart;
/* 867 */     this._tokenInputTotal = this._currInputProcessed + (ptr - this._currBufferStart);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _reportInvalidChar(int c) throws JsonParseException {
/* 872 */     if (c < 32) {
/* 873 */       _throwInvalidSpace(c);
/*     */     }
/* 875 */     _reportInvalidInitial(c);
/*     */   }
/*     */   
/*     */   protected void _reportInvalidInitial(int mask) throws JsonParseException {
/* 879 */     _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
/*     */   }
/*     */   
/*     */   protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
/* 883 */     this._inputPtr = ptr;
/* 884 */     _reportInvalidOther(mask);
/*     */   }
/*     */   
/*     */   protected void _reportInvalidOther(int mask) throws JsonParseException {
/* 888 */     _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/async/NonBlockingJsonParserBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */