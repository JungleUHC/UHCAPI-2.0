/*      */ package com.fasterxml.jackson.core.json.async;
/*      */ 
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.async.ByteArrayFeeder;
/*      */ import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;
/*      */ import com.fasterxml.jackson.core.io.CharTypes;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.json.JsonReadFeature;
/*      */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.VersionUtil;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class NonBlockingJsonParser
/*      */   extends NonBlockingJsonParserBase
/*      */   implements ByteArrayFeeder
/*      */ {
/*   26 */   private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
/*      */   
/*   28 */   private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
/*      */   
/*   30 */   private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
/*   31 */   private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
/*   32 */   private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
/*   33 */   private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
/*   34 */   private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
/*      */ 
/*      */   
/*   37 */   private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
/*      */ 
/*      */ 
/*      */   
/*   41 */   protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
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
/*   52 */   protected byte[] _inputBuffer = NO_BYTES;
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
/*      */   protected int _origBufferLen;
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
/*      */   public NonBlockingJsonParser(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
/*   75 */     super(ctxt, parserFeatures, sym);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteArrayFeeder getNonBlockingInputFeeder() {
/*   86 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean needMoreInput() {
/*   91 */     return (this._inputPtr >= this._inputEnd && !this._endOfInput);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void feedInput(byte[] buf, int start, int end) throws IOException {
/*   98 */     if (this._inputPtr < this._inputEnd) {
/*   99 */       _reportError("Still have %d undecoded bytes, should not call 'feedInput'", Integer.valueOf(this._inputEnd - this._inputPtr));
/*      */     }
/*  101 */     if (end < start) {
/*  102 */       _reportError("Input end (%d) may not be before start (%d)", Integer.valueOf(end), Integer.valueOf(start));
/*      */     }
/*      */     
/*  105 */     if (this._endOfInput) {
/*  106 */       _reportError("Already closed, can not feed more input");
/*      */     }
/*      */     
/*  109 */     this._currInputProcessed += this._origBufferLen;
/*      */ 
/*      */     
/*  112 */     this._currInputRowStart = start - this._inputEnd - this._currInputRowStart;
/*      */ 
/*      */     
/*  115 */     this._currBufferStart = start;
/*  116 */     this._inputBuffer = buf;
/*  117 */     this._inputPtr = start;
/*  118 */     this._inputEnd = end;
/*  119 */     this._origBufferLen = end - start;
/*      */   }
/*      */ 
/*      */   
/*      */   public void endOfInput() {
/*  124 */     this._endOfInput = true;
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
/*      */   public int releaseBuffered(OutputStream out) throws IOException {
/*  146 */     int avail = this._inputEnd - this._inputPtr;
/*  147 */     if (avail > 0) {
/*  148 */       out.write(this._inputBuffer, this._inputPtr, avail);
/*      */     }
/*  150 */     return avail;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _decodeEscaped() throws IOException {
/*  157 */     VersionUtil.throwInternal();
/*  158 */     return ' ';
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
/*      */   public JsonToken nextToken() throws IOException {
/*  172 */     if (this._inputPtr >= this._inputEnd) {
/*  173 */       if (this._closed) {
/*  174 */         return null;
/*      */       }
/*      */       
/*  177 */       if (this._endOfInput) {
/*      */ 
/*      */         
/*  180 */         if (this._currToken == JsonToken.NOT_AVAILABLE) {
/*  181 */           return _finishTokenWithEOF();
/*      */         }
/*  183 */         return _eofAsNextToken();
/*      */       } 
/*  185 */       return JsonToken.NOT_AVAILABLE;
/*      */     } 
/*      */     
/*  188 */     if (this._currToken == JsonToken.NOT_AVAILABLE) {
/*  189 */       return _finishToken();
/*      */     }
/*      */ 
/*      */     
/*  193 */     this._numTypesValid = 0;
/*  194 */     this._tokenInputTotal = this._currInputProcessed + this._inputPtr;
/*      */     
/*  196 */     this._binaryValue = null;
/*  197 */     int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     
/*  199 */     switch (this._majorState) {
/*      */       case 0:
/*  201 */         return _startDocument(ch);
/*      */       
/*      */       case 1:
/*  204 */         return _startValue(ch);
/*      */       
/*      */       case 2:
/*  207 */         return _startFieldName(ch);
/*      */       case 3:
/*  209 */         return _startFieldNameAfterComma(ch);
/*      */       
/*      */       case 4:
/*  212 */         return _startValueExpectColon(ch);
/*      */       
/*      */       case 5:
/*  215 */         return _startValue(ch);
/*      */       
/*      */       case 6:
/*  218 */         return _startValueExpectComma(ch);
/*      */     } 
/*      */ 
/*      */     
/*  222 */     VersionUtil.throwInternal();
/*  223 */     return null;
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
/*      */   protected final JsonToken _finishToken() throws IOException {
/*      */     int c;
/*  238 */     switch (this._minorState) {
/*      */       case 1:
/*  240 */         return _finishBOM(this._pending32);
/*      */       case 4:
/*  242 */         return _startFieldName(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 5:
/*  244 */         return _startFieldNameAfterComma(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */ 
/*      */       
/*      */       case 7:
/*  248 */         return _parseEscapedName(this._quadLength, this._pending32, this._pendingBytes);
/*      */       case 8:
/*  250 */         return _finishFieldWithEscape();
/*      */       case 9:
/*  252 */         return _finishAposName(this._quadLength, this._pending32, this._pendingBytes);
/*      */       case 10:
/*  254 */         return _finishUnquotedName(this._quadLength, this._pending32, this._pendingBytes);
/*      */ 
/*      */ 
/*      */       
/*      */       case 12:
/*  259 */         return _startValue(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 15:
/*  261 */         return _startValueAfterComma(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 13:
/*  263 */         return _startValueExpectComma(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 14:
/*  265 */         return _startValueExpectColon(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       
/*      */       case 16:
/*  268 */         return _finishKeywordToken("null", this._pending32, JsonToken.VALUE_NULL);
/*      */       case 17:
/*  270 */         return _finishKeywordToken("true", this._pending32, JsonToken.VALUE_TRUE);
/*      */       case 18:
/*  272 */         return _finishKeywordToken("false", this._pending32, JsonToken.VALUE_FALSE);
/*      */       case 19:
/*  274 */         return _finishNonStdToken(this._nonStdTokenType, this._pending32);
/*      */       
/*      */       case 23:
/*  277 */         return _finishNumberMinus(this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 24:
/*  279 */         return _finishNumberLeadingZeroes();
/*      */       case 25:
/*  281 */         return _finishNumberLeadingNegZeroes();
/*      */       case 26:
/*  283 */         return _finishNumberIntegralPart(this._textBuffer.getBufferWithoutReset(), this._textBuffer
/*  284 */             .getCurrentSegmentSize());
/*      */       case 30:
/*  286 */         return _finishFloatFraction();
/*      */       case 31:
/*  288 */         return _finishFloatExponent(true, this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       case 32:
/*  290 */         return _finishFloatExponent(false, this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */       
/*      */       case 40:
/*  293 */         return _finishRegularString();
/*      */       case 42:
/*  295 */         this._textBuffer.append((char)_decodeUTF8_2(this._pending32, this._inputBuffer[this._inputPtr++]));
/*  296 */         if (this._minorStateAfterSplit == 45) {
/*  297 */           return _finishAposString();
/*      */         }
/*  299 */         return _finishRegularString();
/*      */       case 43:
/*  301 */         if (!_decodeSplitUTF8_3(this._pending32, this._pendingBytes, this._inputBuffer[this._inputPtr++])) {
/*  302 */           return JsonToken.NOT_AVAILABLE;
/*      */         }
/*  304 */         if (this._minorStateAfterSplit == 45) {
/*  305 */           return _finishAposString();
/*      */         }
/*  307 */         return _finishRegularString();
/*      */       case 44:
/*  309 */         if (!_decodeSplitUTF8_4(this._pending32, this._pendingBytes, this._inputBuffer[this._inputPtr++])) {
/*  310 */           return JsonToken.NOT_AVAILABLE;
/*      */         }
/*  312 */         if (this._minorStateAfterSplit == 45) {
/*  313 */           return _finishAposString();
/*      */         }
/*  315 */         return _finishRegularString();
/*      */ 
/*      */       
/*      */       case 41:
/*  319 */         c = _decodeSplitEscaped(this._quoted32, this._quotedDigits);
/*  320 */         if (c < 0) {
/*  321 */           return JsonToken.NOT_AVAILABLE;
/*      */         }
/*  323 */         this._textBuffer.append((char)c);
/*      */         
/*  325 */         if (this._minorStateAfterSplit == 45) {
/*  326 */           return _finishAposString();
/*      */         }
/*  328 */         return _finishRegularString();
/*      */       
/*      */       case 45:
/*  331 */         return _finishAposString();
/*      */       
/*      */       case 50:
/*  334 */         return _finishErrorToken();
/*      */ 
/*      */ 
/*      */       
/*      */       case 51:
/*  339 */         return _startSlashComment(this._pending32);
/*      */       case 52:
/*  341 */         return _finishCComment(this._pending32, true);
/*      */       case 53:
/*  343 */         return _finishCComment(this._pending32, false);
/*      */       case 54:
/*  345 */         return _finishCppComment(this._pending32);
/*      */       case 55:
/*  347 */         return _finishHashComment(this._pending32);
/*      */     } 
/*  349 */     VersionUtil.throwInternal();
/*  350 */     return null;
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
/*      */   protected final JsonToken _finishTokenWithEOF() throws IOException {
/*      */     int len;
/*  366 */     JsonToken t = this._currToken;
/*  367 */     switch (this._minorState) {
/*      */       case 3:
/*  369 */         return _eofAsNextToken();
/*      */       case 12:
/*  371 */         return _eofAsNextToken();
/*      */ 
/*      */       
/*      */       case 16:
/*  375 */         return _finishKeywordTokenWithEOF("null", this._pending32, JsonToken.VALUE_NULL);
/*      */       case 17:
/*  377 */         return _finishKeywordTokenWithEOF("true", this._pending32, JsonToken.VALUE_TRUE);
/*      */       case 18:
/*  379 */         return _finishKeywordTokenWithEOF("false", this._pending32, JsonToken.VALUE_FALSE);
/*      */       case 19:
/*  381 */         return _finishNonStdTokenWithEOF(this._nonStdTokenType, this._pending32);
/*      */       case 50:
/*  383 */         return _finishErrorTokenWithEOF();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 24:
/*      */       case 25:
/*  390 */         return _valueCompleteInt(0, "0");
/*      */ 
/*      */       
/*      */       case 26:
/*  394 */         len = this._textBuffer.getCurrentSegmentSize();
/*  395 */         if (this._numberNegative) {
/*  396 */           len--;
/*      */         }
/*  398 */         this._intLength = len;
/*      */         
/*  400 */         return _valueComplete(JsonToken.VALUE_NUMBER_INT);
/*      */       
/*      */       case 30:
/*  403 */         this._expLength = 0;
/*      */       
/*      */       case 32:
/*  406 */         return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
/*      */       
/*      */       case 31:
/*  409 */         _reportInvalidEOF(": was expecting fraction after exponent marker", JsonToken.VALUE_NUMBER_FLOAT);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 52:
/*      */       case 53:
/*  417 */         _reportInvalidEOF(": was expecting closing '*/' for comment", JsonToken.NOT_AVAILABLE);
/*      */ 
/*      */       
/*      */       case 54:
/*      */       case 55:
/*  422 */         return _eofAsNextToken();
/*      */     } 
/*      */ 
/*      */     
/*  426 */     _reportInvalidEOF(": was expecting rest of token (internal state: " + this._minorState + ")", this._currToken);
/*  427 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startDocument(int ch) throws IOException {
/*  438 */     ch &= 0xFF;
/*      */ 
/*      */     
/*  441 */     if (ch == 239 && this._minorState != 1) {
/*  442 */       return _finishBOM(1);
/*      */     }
/*      */ 
/*      */     
/*  446 */     while (ch <= 32) {
/*  447 */       if (ch != 32) {
/*  448 */         if (ch == 10) {
/*  449 */           this._currInputRow++;
/*  450 */           this._currInputRowStart = this._inputPtr;
/*  451 */         } else if (ch == 13) {
/*  452 */           this._currInputRowAlt++;
/*  453 */           this._currInputRowStart = this._inputPtr;
/*  454 */         } else if (ch != 9) {
/*  455 */           _throwInvalidSpace(ch);
/*      */         } 
/*      */       }
/*  458 */       if (this._inputPtr >= this._inputEnd) {
/*  459 */         this._minorState = 3;
/*  460 */         if (this._closed) {
/*  461 */           return null;
/*      */         }
/*      */         
/*  464 */         if (this._endOfInput) {
/*  465 */           return _eofAsNextToken();
/*      */         }
/*  467 */         return JsonToken.NOT_AVAILABLE;
/*      */       } 
/*  469 */       ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     } 
/*  471 */     return _startValue(ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _finishBOM(int bytesHandled) throws IOException {
/*  480 */     while (this._inputPtr < this._inputEnd) {
/*  481 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  482 */       switch (bytesHandled) {
/*      */ 
/*      */         
/*      */         case 3:
/*  486 */           this._currInputProcessed -= 3L;
/*  487 */           return _startDocument(ch);
/*      */         case 2:
/*  489 */           if (ch != 191) {
/*  490 */             _reportError("Unexpected byte 0x%02x following 0xEF 0xBB; should get 0xBF as third byte of UTF-8 BOM", Integer.valueOf(ch));
/*      */           }
/*      */           break;
/*      */         case 1:
/*  494 */           if (ch != 187) {
/*  495 */             _reportError("Unexpected byte 0x%02x following 0xEF; should get 0xBB as second byte UTF-8 BOM", Integer.valueOf(ch));
/*      */           }
/*      */           break;
/*      */       } 
/*  499 */       bytesHandled++;
/*      */     } 
/*  501 */     this._pending32 = bytesHandled;
/*  502 */     this._minorState = 1;
/*  503 */     return this._currToken = JsonToken.NOT_AVAILABLE;
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
/*      */   private final JsonToken _startFieldName(int ch) throws IOException {
/*  519 */     if (ch <= 32) {
/*  520 */       ch = _skipWS(ch);
/*  521 */       if (ch <= 0) {
/*  522 */         this._minorState = 4;
/*  523 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  526 */     _updateTokenLocation();
/*  527 */     if (ch != 34) {
/*  528 */       if (ch == 125) {
/*  529 */         return _closeObjectScope();
/*      */       }
/*  531 */       return _handleOddName(ch);
/*      */     } 
/*      */     
/*  534 */     if (this._inputPtr + 13 <= this._inputEnd) {
/*  535 */       String n = _fastParseName();
/*  536 */       if (n != null) {
/*  537 */         return _fieldComplete(n);
/*      */       }
/*      */     } 
/*  540 */     return _parseEscapedName(0, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startFieldNameAfterComma(int ch) throws IOException {
/*  546 */     if (ch <= 32) {
/*  547 */       ch = _skipWS(ch);
/*  548 */       if (ch <= 0) {
/*  549 */         this._minorState = 5;
/*  550 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  553 */     if (ch != 44) {
/*  554 */       if (ch == 125) {
/*  555 */         return _closeObjectScope();
/*      */       }
/*  557 */       if (ch == 35) {
/*  558 */         return _finishHashComment(5);
/*      */       }
/*  560 */       if (ch == 47) {
/*  561 */         return _startSlashComment(5);
/*      */       }
/*  563 */       _reportUnexpectedChar(ch, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */     } 
/*  565 */     int ptr = this._inputPtr;
/*  566 */     if (ptr >= this._inputEnd) {
/*  567 */       this._minorState = 4;
/*  568 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*  570 */     ch = this._inputBuffer[ptr];
/*  571 */     this._inputPtr = ptr + 1;
/*  572 */     if (ch <= 32) {
/*  573 */       ch = _skipWS(ch);
/*  574 */       if (ch <= 0) {
/*  575 */         this._minorState = 4;
/*  576 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  579 */     _updateTokenLocation();
/*  580 */     if (ch != 34) {
/*  581 */       if (ch == 125 && (
/*  582 */         this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  583 */         return _closeObjectScope();
/*      */       }
/*      */       
/*  586 */       return _handleOddName(ch);
/*      */     } 
/*      */     
/*  589 */     if (this._inputPtr + 13 <= this._inputEnd) {
/*  590 */       String n = _fastParseName();
/*  591 */       if (n != null) {
/*  592 */         return _fieldComplete(n);
/*      */       }
/*      */     } 
/*  595 */     return _parseEscapedName(0, 0, 0);
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
/*      */   private final JsonToken _startValue(int ch) throws IOException {
/*  612 */     if (ch <= 32) {
/*  613 */       ch = _skipWS(ch);
/*  614 */       if (ch <= 0) {
/*  615 */         this._minorState = 12;
/*  616 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  619 */     _updateTokenLocation();
/*      */     
/*  621 */     this._parsingContext.expectComma();
/*      */     
/*  623 */     if (ch == 34) {
/*  624 */       return _startString();
/*      */     }
/*  626 */     switch (ch) {
/*      */       case 35:
/*  628 */         return _finishHashComment(12);
/*      */       case 45:
/*  630 */         return _startNegativeNumber();
/*      */       case 47:
/*  632 */         return _startSlashComment(12);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 46:
/*  639 */         if (isEnabled(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
/*  640 */           return _startFloatThatStartsWithPeriod();
/*      */         }
/*      */         break;
/*      */       
/*      */       case 48:
/*  645 */         return _startNumberLeadingZero();
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  655 */         return _startPositiveNumber(ch);
/*      */       case 102:
/*  657 */         return _startFalseToken();
/*      */       case 110:
/*  659 */         return _startNullToken();
/*      */       case 116:
/*  661 */         return _startTrueToken();
/*      */       case 91:
/*  663 */         return _startArrayScope();
/*      */       case 93:
/*  665 */         return _closeArrayScope();
/*      */       case 123:
/*  667 */         return _startObjectScope();
/*      */       case 125:
/*  669 */         return _closeObjectScope();
/*      */     } 
/*      */     
/*  672 */     return _startUnexpectedValue(false, ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startValueExpectComma(int ch) throws IOException {
/*  680 */     if (ch <= 32) {
/*  681 */       ch = _skipWS(ch);
/*  682 */       if (ch <= 0) {
/*  683 */         this._minorState = 13;
/*  684 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  687 */     if (ch != 44) {
/*  688 */       if (ch == 93) {
/*  689 */         return _closeArrayScope();
/*      */       }
/*  691 */       if (ch == 125) {
/*  692 */         return _closeObjectScope();
/*      */       }
/*  694 */       if (ch == 47) {
/*  695 */         return _startSlashComment(13);
/*      */       }
/*  697 */       if (ch == 35) {
/*  698 */         return _finishHashComment(13);
/*      */       }
/*  700 */       _reportUnexpectedChar(ch, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */     } 
/*      */ 
/*      */     
/*  704 */     this._parsingContext.expectComma();
/*      */     
/*  706 */     int ptr = this._inputPtr;
/*  707 */     if (ptr >= this._inputEnd) {
/*  708 */       this._minorState = 15;
/*  709 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*  711 */     ch = this._inputBuffer[ptr];
/*  712 */     this._inputPtr = ptr + 1;
/*  713 */     if (ch <= 32) {
/*  714 */       ch = _skipWS(ch);
/*  715 */       if (ch <= 0) {
/*  716 */         this._minorState = 15;
/*  717 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  720 */     _updateTokenLocation();
/*  721 */     if (ch == 34) {
/*  722 */       return _startString();
/*      */     }
/*  724 */     switch (ch) {
/*      */       case 35:
/*  726 */         return _finishHashComment(15);
/*      */       case 45:
/*  728 */         return _startNegativeNumber();
/*      */       case 47:
/*  730 */         return _startSlashComment(15);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 48:
/*  736 */         return _startNumberLeadingZero();
/*      */       case 49: case 50: case 51: case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  743 */         return _startPositiveNumber(ch);
/*      */       case 102:
/*  745 */         return _startFalseToken();
/*      */       case 110:
/*  747 */         return _startNullToken();
/*      */       case 116:
/*  749 */         return _startTrueToken();
/*      */       case 91:
/*  751 */         return _startArrayScope();
/*      */       
/*      */       case 93:
/*  754 */         if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  755 */           return _closeArrayScope();
/*      */         }
/*      */         break;
/*      */       case 123:
/*  759 */         return _startObjectScope();
/*      */       
/*      */       case 125:
/*  762 */         if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  763 */           return _closeObjectScope();
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/*  768 */     return _startUnexpectedValue(true, ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startValueExpectColon(int ch) throws IOException {
/*  777 */     if (ch <= 32) {
/*  778 */       ch = _skipWS(ch);
/*  779 */       if (ch <= 0) {
/*  780 */         this._minorState = 14;
/*  781 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  784 */     if (ch != 58) {
/*  785 */       if (ch == 47) {
/*  786 */         return _startSlashComment(14);
/*      */       }
/*  788 */       if (ch == 35) {
/*  789 */         return _finishHashComment(14);
/*      */       }
/*      */       
/*  792 */       _reportUnexpectedChar(ch, "was expecting a colon to separate field name and value");
/*      */     } 
/*  794 */     int ptr = this._inputPtr;
/*  795 */     if (ptr >= this._inputEnd) {
/*  796 */       this._minorState = 12;
/*  797 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*  799 */     ch = this._inputBuffer[ptr];
/*  800 */     this._inputPtr = ptr + 1;
/*  801 */     if (ch <= 32) {
/*  802 */       ch = _skipWS(ch);
/*  803 */       if (ch <= 0) {
/*  804 */         this._minorState = 12;
/*  805 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  808 */     _updateTokenLocation();
/*  809 */     if (ch == 34) {
/*  810 */       return _startString();
/*      */     }
/*  812 */     switch (ch) {
/*      */       case 35:
/*  814 */         return _finishHashComment(12);
/*      */       case 45:
/*  816 */         return _startNegativeNumber();
/*      */       case 47:
/*  818 */         return _startSlashComment(12);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 48:
/*  824 */         return _startNumberLeadingZero();
/*      */       case 49: case 50: case 51: case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  831 */         return _startPositiveNumber(ch);
/*      */       case 102:
/*  833 */         return _startFalseToken();
/*      */       case 110:
/*  835 */         return _startNullToken();
/*      */       case 116:
/*  837 */         return _startTrueToken();
/*      */       case 91:
/*  839 */         return _startArrayScope();
/*      */       case 123:
/*  841 */         return _startObjectScope();
/*      */     } 
/*      */     
/*  844 */     return _startUnexpectedValue(false, ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startValueAfterComma(int ch) throws IOException {
/*  851 */     if (ch <= 32) {
/*  852 */       ch = _skipWS(ch);
/*  853 */       if (ch <= 0) {
/*  854 */         this._minorState = 15;
/*  855 */         return this._currToken;
/*      */       } 
/*      */     } 
/*  858 */     _updateTokenLocation();
/*  859 */     if (ch == 34) {
/*  860 */       return _startString();
/*      */     }
/*  862 */     switch (ch) {
/*      */       case 35:
/*  864 */         return _finishHashComment(15);
/*      */       case 45:
/*  866 */         return _startNegativeNumber();
/*      */       case 47:
/*  868 */         return _startSlashComment(15);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 48:
/*  874 */         return _startNumberLeadingZero();
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  884 */         return _startPositiveNumber(ch);
/*      */       case 102:
/*  886 */         return _startFalseToken();
/*      */       case 110:
/*  888 */         return _startNullToken();
/*      */       case 116:
/*  890 */         return _startTrueToken();
/*      */       case 91:
/*  892 */         return _startArrayScope();
/*      */       
/*      */       case 93:
/*  895 */         if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  896 */           return _closeArrayScope();
/*      */         }
/*      */         break;
/*      */       case 123:
/*  900 */         return _startObjectScope();
/*      */       
/*      */       case 125:
/*  903 */         if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
/*  904 */           return _closeObjectScope();
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/*  909 */     return _startUnexpectedValue(true, ch);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startUnexpectedValue(boolean leadingComma, int ch) throws IOException {
/*  914 */     switch (ch) {
/*      */       case 93:
/*  916 */         if (!this._parsingContext.inArray()) {
/*      */           break;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 44:
/*  925 */         if (!this._parsingContext.inRoot() && (
/*  926 */           this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/*  927 */           this._inputPtr--;
/*  928 */           return _valueComplete(JsonToken.VALUE_NULL);
/*      */         } 
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 39:
/*  937 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/*  938 */           return _startAposString();
/*      */         }
/*      */         break;
/*      */       case 43:
/*  942 */         return _finishNonStdToken(2, 1);
/*      */       case 78:
/*  944 */         return _finishNonStdToken(0, 1);
/*      */       case 73:
/*  946 */         return _finishNonStdToken(1, 1);
/*      */     } 
/*      */     
/*  949 */     _reportUnexpectedChar(ch, "expected a valid value " + _validJsonValueList());
/*  950 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipWS(int ch) throws IOException {
/*      */     while (true) {
/*  962 */       if (ch != 32) {
/*  963 */         if (ch == 10) {
/*  964 */           this._currInputRow++;
/*  965 */           this._currInputRowStart = this._inputPtr;
/*  966 */         } else if (ch == 13) {
/*  967 */           this._currInputRowAlt++;
/*  968 */           this._currInputRowStart = this._inputPtr;
/*  969 */         } else if (ch != 9) {
/*  970 */           _throwInvalidSpace(ch);
/*      */         } 
/*      */       }
/*  973 */       if (this._inputPtr >= this._inputEnd) {
/*  974 */         this._currToken = JsonToken.NOT_AVAILABLE;
/*  975 */         return 0;
/*      */       } 
/*  977 */       ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  978 */       if (ch > 32)
/*  979 */         return ch; 
/*      */     } 
/*      */   }
/*      */   
/*      */   private final JsonToken _startSlashComment(int fromMinorState) throws IOException {
/*  984 */     if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
/*  985 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */ 
/*      */     
/*  989 */     if (this._inputPtr >= this._inputEnd) {
/*  990 */       this._pending32 = fromMinorState;
/*  991 */       this._minorState = 51;
/*  992 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*  994 */     int ch = this._inputBuffer[this._inputPtr++];
/*  995 */     if (ch == 42) {
/*  996 */       return _finishCComment(fromMinorState, false);
/*      */     }
/*  998 */     if (ch == 47) {
/*  999 */       return _finishCppComment(fromMinorState);
/*      */     }
/* 1001 */     _reportUnexpectedChar(ch & 0xFF, "was expecting either '*' or '/' for a comment");
/* 1002 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _finishHashComment(int fromMinorState) throws IOException {
/* 1008 */     if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
/* 1009 */       _reportUnexpectedChar(35, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_YAML_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     while (true) {
/* 1012 */       if (this._inputPtr >= this._inputEnd) {
/* 1013 */         this._minorState = 55;
/* 1014 */         this._pending32 = fromMinorState;
/* 1015 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1017 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1018 */       if (ch < 32) {
/* 1019 */         if (ch == 10) {
/* 1020 */           this._currInputRow++;
/* 1021 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 1023 */         if (ch == 13) {
/* 1024 */           this._currInputRowAlt++;
/* 1025 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 1027 */         if (ch != 9) {
/* 1028 */           _throwInvalidSpace(ch);
/*      */         }
/*      */       } 
/*      */     } 
/* 1032 */     return _startAfterComment(fromMinorState);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _finishCppComment(int fromMinorState) throws IOException {
/*      */     while (true) {
/* 1038 */       if (this._inputPtr >= this._inputEnd) {
/* 1039 */         this._minorState = 54;
/* 1040 */         this._pending32 = fromMinorState;
/* 1041 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1043 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1044 */       if (ch < 32) {
/* 1045 */         if (ch == 10) {
/* 1046 */           this._currInputRow++;
/* 1047 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 1049 */         if (ch == 13) {
/* 1050 */           this._currInputRowAlt++;
/* 1051 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 1053 */         if (ch != 9) {
/* 1054 */           _throwInvalidSpace(ch);
/*      */         }
/*      */       } 
/*      */     } 
/* 1058 */     return _startAfterComment(fromMinorState);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _finishCComment(int fromMinorState, boolean gotStar) throws IOException {
/*      */     while (true) {
/* 1064 */       if (this._inputPtr >= this._inputEnd) {
/* 1065 */         this._minorState = gotStar ? 52 : 53;
/* 1066 */         this._pending32 = fromMinorState;
/* 1067 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1069 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1070 */       if (ch < 32)
/* 1071 */       { if (ch == 10) {
/* 1072 */           this._currInputRow++;
/* 1073 */           this._currInputRowStart = this._inputPtr;
/* 1074 */         } else if (ch == 13) {
/* 1075 */           this._currInputRowAlt++;
/* 1076 */           this._currInputRowStart = this._inputPtr;
/* 1077 */         } else if (ch != 9) {
/* 1078 */           _throwInvalidSpace(ch);
/*      */         }  }
/* 1080 */       else { if (ch == 42) {
/* 1081 */           gotStar = true; continue;
/*      */         } 
/* 1083 */         if (ch == 47 && 
/* 1084 */           gotStar) {
/*      */           break;
/*      */         } }
/*      */       
/* 1088 */       gotStar = false;
/*      */     } 
/* 1090 */     return _startAfterComment(fromMinorState);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _startAfterComment(int fromMinorState) throws IOException {
/* 1096 */     if (this._inputPtr >= this._inputEnd) {
/* 1097 */       this._minorState = fromMinorState;
/* 1098 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/* 1100 */     int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1101 */     switch (fromMinorState) {
/*      */       case 4:
/* 1103 */         return _startFieldName(ch);
/*      */       case 5:
/* 1105 */         return _startFieldNameAfterComma(ch);
/*      */       case 12:
/* 1107 */         return _startValue(ch);
/*      */       case 13:
/* 1109 */         return _startValueExpectComma(ch);
/*      */       case 14:
/* 1111 */         return _startValueExpectColon(ch);
/*      */       case 15:
/* 1113 */         return _startValueAfterComma(ch);
/*      */     } 
/*      */     
/* 1116 */     VersionUtil.throwInternal();
/* 1117 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _startFalseToken() throws IOException {
/* 1128 */     int ptr = this._inputPtr;
/* 1129 */     if (ptr + 4 < this._inputEnd) {
/* 1130 */       byte[] buf = this._inputBuffer;
/* 1131 */       if (buf[ptr++] == 97 && buf[ptr++] == 108 && buf[ptr++] == 115 && buf[ptr++] == 101) {
/*      */ 
/*      */ 
/*      */         
/* 1135 */         int ch = buf[ptr] & 0xFF;
/* 1136 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1137 */           this._inputPtr = ptr;
/* 1138 */           return _valueComplete(JsonToken.VALUE_FALSE);
/*      */         } 
/*      */       } 
/*      */     } 
/* 1142 */     this._minorState = 18;
/* 1143 */     return _finishKeywordToken("false", 1, JsonToken.VALUE_FALSE);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startTrueToken() throws IOException {
/* 1148 */     int ptr = this._inputPtr;
/* 1149 */     if (ptr + 3 < this._inputEnd) {
/* 1150 */       byte[] buf = this._inputBuffer;
/* 1151 */       if (buf[ptr++] == 114 && buf[ptr++] == 117 && buf[ptr++] == 101) {
/*      */ 
/*      */         
/* 1154 */         int ch = buf[ptr] & 0xFF;
/* 1155 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1156 */           this._inputPtr = ptr;
/* 1157 */           return _valueComplete(JsonToken.VALUE_TRUE);
/*      */         } 
/*      */       } 
/*      */     } 
/* 1161 */     this._minorState = 17;
/* 1162 */     return _finishKeywordToken("true", 1, JsonToken.VALUE_TRUE);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startNullToken() throws IOException {
/* 1167 */     int ptr = this._inputPtr;
/* 1168 */     if (ptr + 3 < this._inputEnd) {
/* 1169 */       byte[] buf = this._inputBuffer;
/* 1170 */       if (buf[ptr++] == 117 && buf[ptr++] == 108 && buf[ptr++] == 108) {
/*      */ 
/*      */         
/* 1173 */         int ch = buf[ptr] & 0xFF;
/* 1174 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1175 */           this._inputPtr = ptr;
/* 1176 */           return _valueComplete(JsonToken.VALUE_NULL);
/*      */         } 
/*      */       } 
/*      */     } 
/* 1180 */     this._minorState = 16;
/* 1181 */     return _finishKeywordToken("null", 1, JsonToken.VALUE_NULL);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishKeywordToken(String expToken, int matched, JsonToken result) throws IOException {
/* 1187 */     int end = expToken.length();
/*      */     
/*      */     while (true) {
/* 1190 */       if (this._inputPtr >= this._inputEnd) {
/* 1191 */         this._pending32 = matched;
/* 1192 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1194 */       int ch = this._inputBuffer[this._inputPtr];
/* 1195 */       if (matched == end) {
/* 1196 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1197 */           return _valueComplete(result);
/*      */         }
/*      */         break;
/*      */       } 
/* 1201 */       if (ch != expToken.charAt(matched)) {
/*      */         break;
/*      */       }
/* 1204 */       matched++;
/* 1205 */       this._inputPtr++;
/*      */     } 
/* 1207 */     this._minorState = 50;
/* 1208 */     this._textBuffer.resetWithCopy(expToken, 0, matched);
/* 1209 */     return _finishErrorToken();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishKeywordTokenWithEOF(String expToken, int matched, JsonToken result) throws IOException {
/* 1215 */     if (matched == expToken.length()) {
/* 1216 */       return this._currToken = result;
/*      */     }
/* 1218 */     this._textBuffer.resetWithCopy(expToken, 0, matched);
/* 1219 */     return _finishErrorTokenWithEOF();
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNonStdToken(int type, int matched) throws IOException {
/* 1224 */     String expToken = _nonStdToken(type);
/* 1225 */     int end = expToken.length();
/*      */     
/*      */     while (true) {
/* 1228 */       if (this._inputPtr >= this._inputEnd) {
/* 1229 */         this._nonStdTokenType = type;
/* 1230 */         this._pending32 = matched;
/* 1231 */         this._minorState = 19;
/* 1232 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1234 */       int ch = this._inputBuffer[this._inputPtr];
/* 1235 */       if (matched == end) {
/* 1236 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 1237 */           return _valueNonStdNumberComplete(type);
/*      */         }
/*      */         break;
/*      */       } 
/* 1241 */       if (ch != expToken.charAt(matched)) {
/*      */         break;
/*      */       }
/* 1244 */       matched++;
/* 1245 */       this._inputPtr++;
/*      */     } 
/* 1247 */     this._minorState = 50;
/* 1248 */     this._textBuffer.resetWithCopy(expToken, 0, matched);
/* 1249 */     return _finishErrorToken();
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNonStdTokenWithEOF(int type, int matched) throws IOException {
/* 1254 */     String expToken = _nonStdToken(type);
/* 1255 */     if (matched == expToken.length()) {
/* 1256 */       return _valueNonStdNumberComplete(type);
/*      */     }
/* 1258 */     this._textBuffer.resetWithCopy(expToken, 0, matched);
/* 1259 */     return _finishErrorTokenWithEOF();
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishErrorToken() throws IOException {
/* 1264 */     while (this._inputPtr < this._inputEnd) {
/* 1265 */       int i = this._inputBuffer[this._inputPtr++];
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1270 */       char ch = (char)i;
/* 1271 */       if (Character.isJavaIdentifierPart(ch)) {
/*      */ 
/*      */         
/* 1274 */         this._textBuffer.append(ch);
/* 1275 */         if (this._textBuffer.size() < 256) {
/*      */           continue;
/*      */         }
/*      */       } 
/* 1279 */       return _reportErrorToken(this._textBuffer.contentsAsString());
/*      */     } 
/* 1281 */     return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishErrorTokenWithEOF() throws IOException {
/* 1286 */     return _reportErrorToken(this._textBuffer.contentsAsString());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _reportErrorToken(String actualToken) throws IOException {
/* 1292 */     _reportError("Unrecognized token '%s': was expecting %s", this._textBuffer.contentsAsString(), 
/* 1293 */         _validJsonTokenList());
/* 1294 */     return JsonToken.NOT_AVAILABLE;
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
/*      */   protected JsonToken _startFloatThatStartsWithPeriod() throws IOException {
/* 1306 */     this._numberNegative = false;
/* 1307 */     this._intLength = 0;
/* 1308 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1309 */     return _startFloat(outBuf, 0, 46);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startPositiveNumber(int ch) throws IOException {
/* 1314 */     this._numberNegative = false;
/* 1315 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1316 */     outBuf[0] = (char)ch;
/*      */     
/* 1318 */     if (this._inputPtr >= this._inputEnd) {
/* 1319 */       this._minorState = 26;
/* 1320 */       this._textBuffer.setCurrentLength(1);
/* 1321 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*      */     
/* 1324 */     int outPtr = 1;
/*      */     
/* 1326 */     ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     while (true) {
/* 1328 */       if (ch < 48) {
/* 1329 */         if (ch == 46) {
/* 1330 */           this._intLength = outPtr;
/* 1331 */           this._inputPtr++;
/* 1332 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1336 */       if (ch > 57) {
/* 1337 */         if (ch == 101 || ch == 69) {
/* 1338 */           this._intLength = outPtr;
/* 1339 */           this._inputPtr++;
/* 1340 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1344 */       if (outPtr >= outBuf.length)
/*      */       {
/*      */         
/* 1347 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1349 */       outBuf[outPtr++] = (char)ch;
/* 1350 */       if (++this._inputPtr >= this._inputEnd) {
/* 1351 */         this._minorState = 26;
/* 1352 */         this._textBuffer.setCurrentLength(outPtr);
/* 1353 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1355 */       ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     } 
/* 1357 */     this._intLength = outPtr;
/* 1358 */     this._textBuffer.setCurrentLength(outPtr);
/* 1359 */     return _valueComplete(JsonToken.VALUE_NUMBER_INT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startNegativeNumber() throws IOException {
/* 1364 */     this._numberNegative = true;
/* 1365 */     if (this._inputPtr >= this._inputEnd) {
/* 1366 */       this._minorState = 23;
/* 1367 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/* 1369 */     int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1370 */     if (ch <= 48) {
/* 1371 */       if (ch == 48) {
/* 1372 */         return _finishNumberLeadingNegZeroes();
/*      */       }
/*      */       
/* 1375 */       reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 1376 */     } else if (ch > 57) {
/* 1377 */       if (ch == 73) {
/* 1378 */         return _finishNonStdToken(3, 2);
/*      */       }
/* 1380 */       reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/*      */     } 
/* 1382 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1383 */     outBuf[0] = '-';
/* 1384 */     outBuf[1] = (char)ch;
/* 1385 */     if (this._inputPtr >= this._inputEnd) {
/* 1386 */       this._minorState = 26;
/* 1387 */       this._textBuffer.setCurrentLength(2);
/* 1388 */       this._intLength = 1;
/* 1389 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/* 1391 */     ch = this._inputBuffer[this._inputPtr];
/* 1392 */     int outPtr = 2;
/*      */     
/*      */     while (true) {
/* 1395 */       if (ch < 48) {
/* 1396 */         if (ch == 46) {
/* 1397 */           this._intLength = outPtr - 1;
/* 1398 */           this._inputPtr++;
/* 1399 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1403 */       if (ch > 57) {
/* 1404 */         if (ch == 101 || ch == 69) {
/* 1405 */           this._intLength = outPtr - 1;
/* 1406 */           this._inputPtr++;
/* 1407 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1411 */       if (outPtr >= outBuf.length)
/*      */       {
/* 1413 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1415 */       outBuf[outPtr++] = (char)ch;
/* 1416 */       if (++this._inputPtr >= this._inputEnd) {
/* 1417 */         this._minorState = 26;
/* 1418 */         this._textBuffer.setCurrentLength(outPtr);
/* 1419 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1421 */       ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     } 
/* 1423 */     this._intLength = outPtr - 1;
/* 1424 */     this._textBuffer.setCurrentLength(outPtr);
/* 1425 */     return _valueComplete(JsonToken.VALUE_NUMBER_INT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startNumberLeadingZero() throws IOException {
/* 1430 */     int ptr = this._inputPtr;
/* 1431 */     if (ptr >= this._inputEnd) {
/* 1432 */       this._minorState = 24;
/* 1433 */       return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1440 */     int ch = this._inputBuffer[ptr++] & 0xFF;
/*      */     
/* 1442 */     if (ch < 48) {
/* 1443 */       if (ch == 46) {
/* 1444 */         this._inputPtr = ptr;
/* 1445 */         this._intLength = 1;
/* 1446 */         char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1447 */         outBuf[0] = '0';
/* 1448 */         return _startFloat(outBuf, 1, ch);
/*      */       } 
/* 1450 */     } else if (ch > 57) {
/* 1451 */       if (ch == 101 || ch == 69) {
/* 1452 */         this._inputPtr = ptr;
/* 1453 */         this._intLength = 1;
/* 1454 */         char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1455 */         outBuf[0] = '0';
/* 1456 */         return _startFloat(outBuf, 1, ch);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1461 */       if (ch != 93 && ch != 125) {
/* 1462 */         reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 1467 */       return _finishNumberLeadingZeroes();
/*      */     } 
/*      */     
/* 1470 */     return _valueCompleteInt(0, "0");
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNumberMinus(int ch) throws IOException {
/* 1475 */     if (ch <= 48) {
/* 1476 */       if (ch == 48) {
/* 1477 */         return _finishNumberLeadingNegZeroes();
/*      */       }
/* 1479 */       reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 1480 */     } else if (ch > 57) {
/* 1481 */       if (ch == 73) {
/* 1482 */         return _finishNonStdToken(3, 2);
/*      */       }
/* 1484 */       reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/*      */     } 
/* 1486 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1487 */     outBuf[0] = '-';
/* 1488 */     outBuf[1] = (char)ch;
/* 1489 */     this._intLength = 1;
/* 1490 */     return _finishNumberIntegralPart(outBuf, 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNumberLeadingZeroes() throws IOException {
/*      */     while (true) {
/* 1498 */       if (this._inputPtr >= this._inputEnd) {
/* 1499 */         this._minorState = 24;
/* 1500 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1502 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1503 */       if (ch < 48) {
/* 1504 */         if (ch == 46) {
/* 1505 */           char[] arrayOfChar = this._textBuffer.emptyAndGetCurrentSegment();
/* 1506 */           arrayOfChar[0] = '0';
/* 1507 */           this._intLength = 1;
/* 1508 */           return _startFloat(arrayOfChar, 1, ch);
/*      */         }  break;
/* 1510 */       }  if (ch > 57) {
/* 1511 */         if (ch == 101 || ch == 69) {
/* 1512 */           char[] arrayOfChar = this._textBuffer.emptyAndGetCurrentSegment();
/* 1513 */           arrayOfChar[0] = '0';
/* 1514 */           this._intLength = 1;
/* 1515 */           return _startFloat(arrayOfChar, 1, ch);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1520 */         if (ch != 93 && ch != 125) {
/* 1521 */           reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
/*      */         }
/*      */         
/*      */         break;
/*      */       } 
/*      */       
/* 1527 */       if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1528 */         reportInvalidNumber("Leading zeroes not allowed");
/*      */       }
/* 1530 */       if (ch == 48) {
/*      */         continue;
/*      */       }
/* 1533 */       char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */       
/* 1535 */       outBuf[0] = (char)ch;
/* 1536 */       this._intLength = 1;
/* 1537 */       return _finishNumberIntegralPart(outBuf, 1);
/*      */     } 
/* 1539 */     this._inputPtr--;
/* 1540 */     return _valueCompleteInt(0, "0");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNumberLeadingNegZeroes() throws IOException {
/*      */     while (true) {
/* 1549 */       if (this._inputPtr >= this._inputEnd) {
/* 1550 */         this._minorState = 25;
/* 1551 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1553 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1554 */       if (ch < 48) {
/* 1555 */         if (ch == 46) {
/* 1556 */           char[] arrayOfChar = this._textBuffer.emptyAndGetCurrentSegment();
/* 1557 */           arrayOfChar[0] = '-';
/* 1558 */           arrayOfChar[1] = '0';
/* 1559 */           this._intLength = 1;
/* 1560 */           return _startFloat(arrayOfChar, 2, ch);
/*      */         }  break;
/* 1562 */       }  if (ch > 57) {
/* 1563 */         if (ch == 101 || ch == 69) {
/* 1564 */           char[] arrayOfChar = this._textBuffer.emptyAndGetCurrentSegment();
/* 1565 */           arrayOfChar[0] = '-';
/* 1566 */           arrayOfChar[1] = '0';
/* 1567 */           this._intLength = 1;
/* 1568 */           return _startFloat(arrayOfChar, 2, ch);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1573 */         if (ch != 93 && ch != 125) {
/* 1574 */           reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
/*      */         }
/*      */         
/*      */         break;
/*      */       } 
/*      */       
/* 1580 */       if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1581 */         reportInvalidNumber("Leading zeroes not allowed");
/*      */       }
/* 1583 */       if (ch == 48) {
/*      */         continue;
/*      */       }
/* 1586 */       char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */       
/* 1588 */       outBuf[0] = '-';
/* 1589 */       outBuf[1] = (char)ch;
/* 1590 */       this._intLength = 1;
/* 1591 */       return _finishNumberIntegralPart(outBuf, 2);
/*      */     } 
/* 1593 */     this._inputPtr--;
/* 1594 */     return _valueCompleteInt(0, "0");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _finishNumberIntegralPart(char[] outBuf, int outPtr) throws IOException {
/* 1600 */     int negMod = this._numberNegative ? -1 : 0;
/*      */     
/*      */     while (true) {
/* 1603 */       if (this._inputPtr >= this._inputEnd) {
/* 1604 */         this._minorState = 26;
/* 1605 */         this._textBuffer.setCurrentLength(outPtr);
/* 1606 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1608 */       int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 1609 */       if (ch < 48) {
/* 1610 */         if (ch == 46) {
/* 1611 */           this._intLength = outPtr + negMod;
/* 1612 */           this._inputPtr++;
/* 1613 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1617 */       if (ch > 57) {
/* 1618 */         if (ch == 101 || ch == 69) {
/* 1619 */           this._intLength = outPtr + negMod;
/* 1620 */           this._inputPtr++;
/* 1621 */           return _startFloat(outBuf, outPtr, ch);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1625 */       this._inputPtr++;
/* 1626 */       if (outPtr >= outBuf.length)
/*      */       {
/*      */         
/* 1629 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1631 */       outBuf[outPtr++] = (char)ch;
/*      */     } 
/* 1633 */     this._intLength = outPtr + negMod;
/* 1634 */     this._textBuffer.setCurrentLength(outPtr);
/* 1635 */     return _valueComplete(JsonToken.VALUE_NUMBER_INT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _startFloat(char[] outBuf, int outPtr, int ch) throws IOException {
/* 1640 */     int fractLen = 0;
/* 1641 */     if (ch == 46) {
/* 1642 */       if (outPtr >= outBuf.length) {
/* 1643 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1645 */       outBuf[outPtr++] = '.';
/*      */       while (true) {
/* 1647 */         if (this._inputPtr >= this._inputEnd) {
/* 1648 */           this._textBuffer.setCurrentLength(outPtr);
/* 1649 */           this._minorState = 30;
/* 1650 */           this._fractLength = fractLen;
/* 1651 */           return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */         } 
/* 1653 */         ch = this._inputBuffer[this._inputPtr++];
/* 1654 */         if (ch < 48 || ch > 57) {
/* 1655 */           ch &= 0xFF;
/*      */           
/* 1657 */           if (fractLen == 0) {
/* 1658 */             reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
/*      */           }
/*      */           break;
/*      */         } 
/* 1662 */         if (outPtr >= outBuf.length) {
/* 1663 */           outBuf = this._textBuffer.expandCurrentSegment();
/*      */         }
/* 1665 */         outBuf[outPtr++] = (char)ch;
/* 1666 */         fractLen++;
/*      */       } 
/*      */     } 
/* 1669 */     this._fractLength = fractLen;
/* 1670 */     int expLen = 0;
/* 1671 */     if (ch == 101 || ch == 69) {
/* 1672 */       if (outPtr >= outBuf.length) {
/* 1673 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1675 */       outBuf[outPtr++] = (char)ch;
/* 1676 */       if (this._inputPtr >= this._inputEnd) {
/* 1677 */         this._textBuffer.setCurrentLength(outPtr);
/* 1678 */         this._minorState = 31;
/* 1679 */         this._expLength = 0;
/* 1680 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1682 */       ch = this._inputBuffer[this._inputPtr++];
/* 1683 */       if (ch == 45 || ch == 43) {
/* 1684 */         if (outPtr >= outBuf.length) {
/* 1685 */           outBuf = this._textBuffer.expandCurrentSegment();
/*      */         }
/* 1687 */         outBuf[outPtr++] = (char)ch;
/* 1688 */         if (this._inputPtr >= this._inputEnd) {
/* 1689 */           this._textBuffer.setCurrentLength(outPtr);
/* 1690 */           this._minorState = 32;
/* 1691 */           this._expLength = 0;
/* 1692 */           return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */         } 
/* 1694 */         ch = this._inputBuffer[this._inputPtr++];
/*      */       } 
/* 1696 */       while (ch >= 48 && ch <= 57) {
/* 1697 */         expLen++;
/* 1698 */         if (outPtr >= outBuf.length) {
/* 1699 */           outBuf = this._textBuffer.expandCurrentSegment();
/*      */         }
/* 1701 */         outBuf[outPtr++] = (char)ch;
/* 1702 */         if (this._inputPtr >= this._inputEnd) {
/* 1703 */           this._textBuffer.setCurrentLength(outPtr);
/* 1704 */           this._minorState = 32;
/* 1705 */           this._expLength = expLen;
/* 1706 */           return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */         } 
/* 1708 */         ch = this._inputBuffer[this._inputPtr++];
/*      */       } 
/*      */       
/* 1711 */       ch &= 0xFF;
/* 1712 */       if (expLen == 0) {
/* 1713 */         reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1717 */     this._inputPtr--;
/* 1718 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1720 */     this._expLength = expLen;
/* 1721 */     return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishFloatFraction() throws IOException {
/* 1726 */     int fractLen = this._fractLength;
/* 1727 */     char[] outBuf = this._textBuffer.getBufferWithoutReset();
/* 1728 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     
/*      */     int ch;
/*      */     
/* 1732 */     while ((ch = this._inputBuffer[this._inputPtr++]) >= 48 && ch <= 57) {
/* 1733 */       fractLen++;
/* 1734 */       if (outPtr >= outBuf.length) {
/* 1735 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1737 */       outBuf[outPtr++] = (char)ch;
/* 1738 */       if (this._inputPtr >= this._inputEnd) {
/* 1739 */         this._textBuffer.setCurrentLength(outPtr);
/* 1740 */         this._fractLength = fractLen;
/* 1741 */         return JsonToken.NOT_AVAILABLE;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1747 */     if (fractLen == 0) {
/* 1748 */       reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
/*      */     }
/* 1750 */     this._fractLength = fractLen;
/* 1751 */     this._textBuffer.setCurrentLength(outPtr);
/*      */ 
/*      */     
/* 1754 */     if (ch == 101 || ch == 69) {
/* 1755 */       this._textBuffer.append((char)ch);
/* 1756 */       this._expLength = 0;
/* 1757 */       if (this._inputPtr >= this._inputEnd) {
/* 1758 */         this._minorState = 31;
/* 1759 */         return JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1761 */       this._minorState = 32;
/* 1762 */       return _finishFloatExponent(true, this._inputBuffer[this._inputPtr++] & 0xFF);
/*      */     } 
/*      */ 
/*      */     
/* 1766 */     this._inputPtr--;
/* 1767 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1769 */     this._expLength = 0;
/* 1770 */     return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _finishFloatExponent(boolean checkSign, int ch) throws IOException {
/* 1775 */     if (checkSign) {
/* 1776 */       this._minorState = 32;
/* 1777 */       if (ch == 45 || ch == 43) {
/* 1778 */         this._textBuffer.append((char)ch);
/* 1779 */         if (this._inputPtr >= this._inputEnd) {
/* 1780 */           this._minorState = 32;
/* 1781 */           this._expLength = 0;
/* 1782 */           return JsonToken.NOT_AVAILABLE;
/*      */         } 
/* 1784 */         ch = this._inputBuffer[this._inputPtr++];
/*      */       } 
/*      */     } 
/*      */     
/* 1788 */     char[] outBuf = this._textBuffer.getBufferWithoutReset();
/* 1789 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 1790 */     int expLen = this._expLength;
/*      */     
/* 1792 */     while (ch >= 48 && ch <= 57) {
/* 1793 */       expLen++;
/* 1794 */       if (outPtr >= outBuf.length) {
/* 1795 */         outBuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1797 */       outBuf[outPtr++] = (char)ch;
/* 1798 */       if (this._inputPtr >= this._inputEnd) {
/* 1799 */         this._textBuffer.setCurrentLength(outPtr);
/* 1800 */         this._expLength = expLen;
/* 1801 */         return JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1803 */       ch = this._inputBuffer[this._inputPtr++];
/*      */     } 
/*      */     
/* 1806 */     ch &= 0xFF;
/* 1807 */     if (expLen == 0) {
/* 1808 */       reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
/*      */     }
/*      */     
/* 1811 */     this._inputPtr--;
/* 1812 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1814 */     this._expLength = expLen;
/* 1815 */     return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
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
/*      */   private final String _fastParseName() throws IOException {
/* 1831 */     byte[] input = this._inputBuffer;
/* 1832 */     int[] codes = _icLatin1;
/* 1833 */     int ptr = this._inputPtr;
/*      */     
/* 1835 */     int q0 = input[ptr++] & 0xFF;
/* 1836 */     if (codes[q0] == 0) {
/* 1837 */       int i = input[ptr++] & 0xFF;
/* 1838 */       if (codes[i] == 0) {
/* 1839 */         int q = q0 << 8 | i;
/* 1840 */         i = input[ptr++] & 0xFF;
/* 1841 */         if (codes[i] == 0) {
/* 1842 */           q = q << 8 | i;
/* 1843 */           i = input[ptr++] & 0xFF;
/* 1844 */           if (codes[i] == 0) {
/* 1845 */             q = q << 8 | i;
/* 1846 */             i = input[ptr++] & 0xFF;
/* 1847 */             if (codes[i] == 0) {
/* 1848 */               this._quad1 = q;
/* 1849 */               return _parseMediumName(ptr, i);
/*      */             } 
/* 1851 */             if (i == 34) {
/* 1852 */               this._inputPtr = ptr;
/* 1853 */               return _findName(q, 4);
/*      */             } 
/* 1855 */             return null;
/*      */           } 
/* 1857 */           if (i == 34) {
/* 1858 */             this._inputPtr = ptr;
/* 1859 */             return _findName(q, 3);
/*      */           } 
/* 1861 */           return null;
/*      */         } 
/* 1863 */         if (i == 34) {
/* 1864 */           this._inputPtr = ptr;
/* 1865 */           return _findName(q, 2);
/*      */         } 
/* 1867 */         return null;
/*      */       } 
/* 1869 */       if (i == 34) {
/* 1870 */         this._inputPtr = ptr;
/* 1871 */         return _findName(q0, 1);
/*      */       } 
/* 1873 */       return null;
/*      */     } 
/* 1875 */     if (q0 == 34) {
/* 1876 */       this._inputPtr = ptr;
/* 1877 */       return "";
/*      */     } 
/* 1879 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseMediumName(int ptr, int q2) throws IOException {
/* 1884 */     byte[] input = this._inputBuffer;
/* 1885 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1888 */     int i = input[ptr++] & 0xFF;
/* 1889 */     if (codes[i] == 0) {
/* 1890 */       q2 = q2 << 8 | i;
/* 1891 */       i = input[ptr++] & 0xFF;
/* 1892 */       if (codes[i] == 0) {
/* 1893 */         q2 = q2 << 8 | i;
/* 1894 */         i = input[ptr++] & 0xFF;
/* 1895 */         if (codes[i] == 0) {
/* 1896 */           q2 = q2 << 8 | i;
/* 1897 */           i = input[ptr++] & 0xFF;
/* 1898 */           if (codes[i] == 0) {
/* 1899 */             return _parseMediumName2(ptr, i, q2);
/*      */           }
/* 1901 */           if (i == 34) {
/* 1902 */             this._inputPtr = ptr;
/* 1903 */             return _findName(this._quad1, q2, 4);
/*      */           } 
/* 1905 */           return null;
/*      */         } 
/* 1907 */         if (i == 34) {
/* 1908 */           this._inputPtr = ptr;
/* 1909 */           return _findName(this._quad1, q2, 3);
/*      */         } 
/* 1911 */         return null;
/*      */       } 
/* 1913 */       if (i == 34) {
/* 1914 */         this._inputPtr = ptr;
/* 1915 */         return _findName(this._quad1, q2, 2);
/*      */       } 
/* 1917 */       return null;
/*      */     } 
/* 1919 */     if (i == 34) {
/* 1920 */       this._inputPtr = ptr;
/* 1921 */       return _findName(this._quad1, q2, 1);
/*      */     } 
/* 1923 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseMediumName2(int ptr, int q3, int q2) throws IOException {
/* 1928 */     byte[] input = this._inputBuffer;
/* 1929 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1932 */     int i = input[ptr++] & 0xFF;
/* 1933 */     if (codes[i] != 0) {
/* 1934 */       if (i == 34) {
/* 1935 */         this._inputPtr = ptr;
/* 1936 */         return _findName(this._quad1, q2, q3, 1);
/*      */       } 
/* 1938 */       return null;
/*      */     } 
/* 1940 */     q3 = q3 << 8 | i;
/* 1941 */     i = input[ptr++] & 0xFF;
/* 1942 */     if (codes[i] != 0) {
/* 1943 */       if (i == 34) {
/* 1944 */         this._inputPtr = ptr;
/* 1945 */         return _findName(this._quad1, q2, q3, 2);
/*      */       } 
/* 1947 */       return null;
/*      */     } 
/* 1949 */     q3 = q3 << 8 | i;
/* 1950 */     i = input[ptr++] & 0xFF;
/* 1951 */     if (codes[i] != 0) {
/* 1952 */       if (i == 34) {
/* 1953 */         this._inputPtr = ptr;
/* 1954 */         return _findName(this._quad1, q2, q3, 3);
/*      */       } 
/* 1956 */       return null;
/*      */     } 
/* 1958 */     q3 = q3 << 8 | i;
/* 1959 */     i = input[ptr++] & 0xFF;
/* 1960 */     if (i == 34) {
/* 1961 */       this._inputPtr = ptr;
/* 1962 */       return _findName(this._quad1, q2, q3, 4);
/*      */     } 
/*      */     
/* 1965 */     return null;
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
/*      */   private final JsonToken _parseEscapedName(int qlen, int currQuad, int currQuadBytes) throws IOException {
/* 1981 */     int[] quads = this._quadBuffer;
/* 1982 */     int[] codes = _icLatin1;
/*      */     
/*      */     while (true) {
/* 1985 */       if (this._inputPtr >= this._inputEnd) {
/* 1986 */         this._quadLength = qlen;
/* 1987 */         this._pending32 = currQuad;
/* 1988 */         this._pendingBytes = currQuadBytes;
/* 1989 */         this._minorState = 7;
/* 1990 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 1992 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1993 */       if (codes[ch] == 0) {
/* 1994 */         if (currQuadBytes < 4) {
/* 1995 */           currQuadBytes++;
/* 1996 */           currQuad = currQuad << 8 | ch;
/*      */           continue;
/*      */         } 
/* 1999 */         if (qlen >= quads.length) {
/* 2000 */           this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */         }
/* 2002 */         quads[qlen++] = currQuad;
/* 2003 */         currQuad = ch;
/* 2004 */         currQuadBytes = 1;
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2009 */       if (ch == 34) {
/*      */         break;
/*      */       }
/*      */       
/* 2013 */       if (ch != 92) {
/*      */         
/* 2015 */         _throwUnquotedSpace(ch, "name");
/*      */       } else {
/*      */         
/* 2018 */         ch = _decodeCharEscape();
/* 2019 */         if (ch < 0) {
/* 2020 */           this._minorState = 8;
/* 2021 */           this._minorStateAfterSplit = 7;
/* 2022 */           this._quadLength = qlen;
/* 2023 */           this._pending32 = currQuad;
/* 2024 */           this._pendingBytes = currQuadBytes;
/* 2025 */           return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2032 */       if (qlen >= quads.length) {
/* 2033 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2035 */       if (ch > 127) {
/*      */         
/* 2037 */         if (currQuadBytes >= 4) {
/* 2038 */           quads[qlen++] = currQuad;
/* 2039 */           currQuad = 0;
/* 2040 */           currQuadBytes = 0;
/*      */         } 
/* 2042 */         if (ch < 2048) {
/* 2043 */           currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2044 */           currQuadBytes++;
/*      */         } else {
/*      */           
/* 2047 */           currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 2048 */           currQuadBytes++;
/*      */           
/* 2050 */           if (currQuadBytes >= 4) {
/* 2051 */             quads[qlen++] = currQuad;
/* 2052 */             currQuad = 0;
/* 2053 */             currQuadBytes = 0;
/*      */           } 
/* 2055 */           currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2056 */           currQuadBytes++;
/*      */         } 
/*      */         
/* 2059 */         ch = 0x80 | ch & 0x3F;
/*      */       } 
/* 2061 */       if (currQuadBytes < 4) {
/* 2062 */         currQuadBytes++;
/* 2063 */         currQuad = currQuad << 8 | ch;
/*      */         continue;
/*      */       } 
/* 2066 */       quads[qlen++] = currQuad;
/* 2067 */       currQuad = ch;
/* 2068 */       currQuadBytes = 1;
/*      */     } 
/*      */     
/* 2071 */     if (currQuadBytes > 0) {
/* 2072 */       if (qlen >= quads.length) {
/* 2073 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2075 */       quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
/* 2076 */     } else if (qlen == 0) {
/* 2077 */       return _fieldComplete("");
/*      */     } 
/* 2079 */     String name = this._symbols.findName(quads, qlen);
/* 2080 */     if (name == null) {
/* 2081 */       name = _addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2083 */     return _fieldComplete(name);
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
/*      */   private JsonToken _handleOddName(int ch) throws IOException {
/* 2095 */     switch (ch) {
/*      */ 
/*      */       
/*      */       case 35:
/* 2099 */         if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) != 0) {
/* 2100 */           return _finishHashComment(4);
/*      */         }
/*      */         break;
/*      */       case 47:
/* 2104 */         return _startSlashComment(4);
/*      */       case 39:
/* 2106 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 2107 */           return _finishAposName(0, 0, 0);
/*      */         }
/*      */         break;
/*      */       case 93:
/* 2111 */         return _closeArrayScope();
/*      */     } 
/*      */     
/* 2114 */     if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
/*      */ 
/*      */       
/* 2117 */       char c = (char)ch;
/* 2118 */       _reportUnexpectedChar(c, "was expecting double-quote to start field name");
/*      */     } 
/*      */ 
/*      */     
/* 2122 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */     
/* 2124 */     if (codes[ch] != 0) {
/* 2125 */       _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/*      */     
/* 2128 */     return _finishUnquotedName(0, ch, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private JsonToken _finishUnquotedName(int qlen, int currQuad, int currQuadBytes) throws IOException {
/* 2139 */     int[] quads = this._quadBuffer;
/* 2140 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2145 */       if (this._inputPtr >= this._inputEnd) {
/* 2146 */         this._quadLength = qlen;
/* 2147 */         this._pending32 = currQuad;
/* 2148 */         this._pendingBytes = currQuadBytes;
/* 2149 */         this._minorState = 10;
/* 2150 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 2152 */       int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2153 */       if (codes[ch] != 0) {
/*      */         break;
/*      */       }
/* 2156 */       this._inputPtr++;
/*      */       
/* 2158 */       if (currQuadBytes < 4) {
/* 2159 */         currQuadBytes++;
/* 2160 */         currQuad = currQuad << 8 | ch; continue;
/*      */       } 
/* 2162 */       if (qlen >= quads.length) {
/* 2163 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2165 */       quads[qlen++] = currQuad;
/* 2166 */       currQuad = ch;
/* 2167 */       currQuadBytes = 1;
/*      */     } 
/*      */ 
/*      */     
/* 2171 */     if (currQuadBytes > 0) {
/* 2172 */       if (qlen >= quads.length) {
/* 2173 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2175 */       quads[qlen++] = currQuad;
/*      */     } 
/* 2177 */     String name = this._symbols.findName(quads, qlen);
/* 2178 */     if (name == null) {
/* 2179 */       name = _addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2181 */     return _fieldComplete(name);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private JsonToken _finishAposName(int qlen, int currQuad, int currQuadBytes) throws IOException {
/* 2187 */     int[] quads = this._quadBuffer;
/* 2188 */     int[] codes = _icLatin1;
/*      */     
/*      */     while (true) {
/* 2191 */       if (this._inputPtr >= this._inputEnd) {
/* 2192 */         this._quadLength = qlen;
/* 2193 */         this._pending32 = currQuad;
/* 2194 */         this._pendingBytes = currQuadBytes;
/* 2195 */         this._minorState = 9;
/* 2196 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 2198 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 2199 */       if (ch == 39) {
/*      */         break;
/*      */       }
/*      */       
/* 2203 */       if (ch != 34 && codes[ch] != 0) {
/* 2204 */         if (ch != 92) {
/*      */           
/* 2206 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 2209 */           ch = _decodeCharEscape();
/* 2210 */           if (ch < 0) {
/* 2211 */             this._minorState = 8;
/* 2212 */             this._minorStateAfterSplit = 9;
/* 2213 */             this._quadLength = qlen;
/* 2214 */             this._pending32 = currQuad;
/* 2215 */             this._pendingBytes = currQuadBytes;
/* 2216 */             return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */           } 
/*      */         } 
/* 2219 */         if (ch > 127) {
/*      */           
/* 2221 */           if (currQuadBytes >= 4) {
/* 2222 */             if (qlen >= quads.length) {
/* 2223 */               this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */             }
/* 2225 */             quads[qlen++] = currQuad;
/* 2226 */             currQuad = 0;
/* 2227 */             currQuadBytes = 0;
/*      */           } 
/* 2229 */           if (ch < 2048) {
/* 2230 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2231 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 2234 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 2235 */             currQuadBytes++;
/*      */             
/* 2237 */             if (currQuadBytes >= 4) {
/* 2238 */               if (qlen >= quads.length) {
/* 2239 */                 this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */               }
/* 2241 */               quads[qlen++] = currQuad;
/* 2242 */               currQuad = 0;
/* 2243 */               currQuadBytes = 0;
/*      */             } 
/* 2245 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2246 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 2249 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 2253 */       if (currQuadBytes < 4) {
/* 2254 */         currQuadBytes++;
/* 2255 */         currQuad = currQuad << 8 | ch; continue;
/*      */       } 
/* 2257 */       if (qlen >= quads.length) {
/* 2258 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2260 */       quads[qlen++] = currQuad;
/* 2261 */       currQuad = ch;
/* 2262 */       currQuadBytes = 1;
/*      */     } 
/*      */ 
/*      */     
/* 2266 */     if (currQuadBytes > 0) {
/* 2267 */       if (qlen >= quads.length) {
/* 2268 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2270 */       quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
/* 2271 */     } else if (qlen == 0) {
/* 2272 */       return _fieldComplete("");
/*      */     } 
/* 2274 */     String name = this._symbols.findName(quads, qlen);
/* 2275 */     if (name == null) {
/* 2276 */       name = _addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2278 */     return _fieldComplete(name);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonToken _finishFieldWithEscape() throws IOException {
/* 2284 */     int ch = _decodeSplitEscaped(this._quoted32, this._quotedDigits);
/* 2285 */     if (ch < 0) {
/* 2286 */       this._minorState = 8;
/* 2287 */       return JsonToken.NOT_AVAILABLE;
/*      */     } 
/* 2289 */     if (this._quadLength >= this._quadBuffer.length) {
/* 2290 */       this._quadBuffer = growArrayBy(this._quadBuffer, 32);
/*      */     }
/* 2292 */     int currQuad = this._pending32;
/* 2293 */     int currQuadBytes = this._pendingBytes;
/* 2294 */     if (ch > 127) {
/*      */       
/* 2296 */       if (currQuadBytes >= 4) {
/* 2297 */         this._quadBuffer[this._quadLength++] = currQuad;
/* 2298 */         currQuad = 0;
/* 2299 */         currQuadBytes = 0;
/*      */       } 
/* 2301 */       if (ch < 2048) {
/* 2302 */         currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2303 */         currQuadBytes++;
/*      */       } else {
/*      */         
/* 2306 */         currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/*      */         
/* 2308 */         if (++currQuadBytes >= 4) {
/* 2309 */           this._quadBuffer[this._quadLength++] = currQuad;
/* 2310 */           currQuad = 0;
/* 2311 */           currQuadBytes = 0;
/*      */         } 
/* 2313 */         currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2314 */         currQuadBytes++;
/*      */       } 
/*      */       
/* 2317 */       ch = 0x80 | ch & 0x3F;
/*      */     } 
/* 2319 */     if (currQuadBytes < 4) {
/* 2320 */       currQuadBytes++;
/* 2321 */       currQuad = currQuad << 8 | ch;
/*      */     } else {
/* 2323 */       this._quadBuffer[this._quadLength++] = currQuad;
/* 2324 */       currQuad = ch;
/* 2325 */       currQuadBytes = 1;
/*      */     } 
/* 2327 */     if (this._minorStateAfterSplit == 9) {
/* 2328 */       return _finishAposName(this._quadLength, currQuad, currQuadBytes);
/*      */     }
/* 2330 */     return _parseEscapedName(this._quadLength, currQuad, currQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private int _decodeSplitEscaped(int value, int bytesRead) throws IOException {
/* 2335 */     if (this._inputPtr >= this._inputEnd) {
/* 2336 */       this._quoted32 = value;
/* 2337 */       this._quotedDigits = bytesRead;
/* 2338 */       return -1;
/*      */     } 
/* 2340 */     int c = this._inputBuffer[this._inputPtr++];
/* 2341 */     if (bytesRead == -1) {
/* 2342 */       char ch; switch (c) {
/*      */         
/*      */         case 98:
/* 2345 */           return 8;
/*      */         case 116:
/* 2347 */           return 9;
/*      */         case 110:
/* 2349 */           return 10;
/*      */         case 102:
/* 2351 */           return 12;
/*      */         case 114:
/* 2353 */           return 13;
/*      */ 
/*      */         
/*      */         case 34:
/*      */         case 47:
/*      */         case 92:
/* 2359 */           return c;
/*      */ 
/*      */ 
/*      */         
/*      */         case 117:
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 2368 */           ch = (char)c;
/* 2369 */           return _handleUnrecognizedCharacterEscape(ch);
/*      */       } 
/*      */       
/* 2372 */       if (this._inputPtr >= this._inputEnd) {
/* 2373 */         this._quotedDigits = 0;
/* 2374 */         this._quoted32 = 0;
/* 2375 */         return -1;
/*      */       } 
/* 2377 */       c = this._inputBuffer[this._inputPtr++];
/* 2378 */       bytesRead = 0;
/*      */     } 
/* 2380 */     c &= 0xFF;
/*      */     while (true) {
/* 2382 */       int digit = CharTypes.charToHex(c);
/* 2383 */       if (digit < 0) {
/* 2384 */         _reportUnexpectedChar(c & 0xFF, "expected a hex-digit for character escape sequence");
/*      */       }
/* 2386 */       value = value << 4 | digit;
/* 2387 */       if (++bytesRead == 4) {
/* 2388 */         return value;
/*      */       }
/* 2390 */       if (this._inputPtr >= this._inputEnd) {
/* 2391 */         this._quotedDigits = bytesRead;
/* 2392 */         this._quoted32 = value;
/* 2393 */         return -1;
/*      */       } 
/* 2395 */       c = this._inputBuffer[this._inputPtr++] & 0xFF;
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
/*      */   protected JsonToken _startString() throws IOException {
/* 2407 */     int ptr = this._inputPtr;
/* 2408 */     int outPtr = 0;
/* 2409 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2410 */     int[] codes = _icUTF8;
/*      */     
/* 2412 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2413 */     byte[] inputBuffer = this._inputBuffer;
/* 2414 */     while (ptr < max) {
/* 2415 */       int c = inputBuffer[ptr] & 0xFF;
/* 2416 */       if (codes[c] != 0) {
/* 2417 */         if (c == 34) {
/* 2418 */           this._inputPtr = ptr + 1;
/* 2419 */           this._textBuffer.setCurrentLength(outPtr);
/* 2420 */           return _valueComplete(JsonToken.VALUE_STRING);
/*      */         } 
/*      */         break;
/*      */       } 
/* 2424 */       ptr++;
/* 2425 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 2427 */     this._textBuffer.setCurrentLength(outPtr);
/* 2428 */     this._inputPtr = ptr;
/* 2429 */     return _finishRegularString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _finishRegularString() throws IOException {
/* 2437 */     int[] codes = _icUTF8;
/* 2438 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/* 2440 */     char[] outBuf = this._textBuffer.getBufferWithoutReset();
/* 2441 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2442 */     int ptr = this._inputPtr;
/* 2443 */     int safeEnd = this._inputEnd - 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2450 */       if (ptr >= this._inputEnd) {
/* 2451 */         this._inputPtr = ptr;
/* 2452 */         this._minorState = 40;
/* 2453 */         this._textBuffer.setCurrentLength(outPtr);
/* 2454 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 2456 */       if (outPtr >= outBuf.length) {
/* 2457 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2458 */         outPtr = 0;
/*      */       } 
/* 2460 */       int max = Math.min(this._inputEnd, ptr + outBuf.length - outPtr);
/* 2461 */       while (ptr < max) {
/* 2462 */         int c = inputBuffer[ptr++] & 0xFF;
/* 2463 */         if (codes[c] != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2470 */           if (c == 34) {
/* 2471 */             this._inputPtr = ptr;
/* 2472 */             this._textBuffer.setCurrentLength(outPtr);
/* 2473 */             return _valueComplete(JsonToken.VALUE_STRING);
/*      */           } 
/*      */           
/* 2476 */           if (ptr >= safeEnd) {
/* 2477 */             this._inputPtr = ptr;
/* 2478 */             this._textBuffer.setCurrentLength(outPtr);
/* 2479 */             if (!_decodeSplitMultiByte(c, codes[c], (ptr < this._inputEnd))) {
/* 2480 */               this._minorStateAfterSplit = 40;
/* 2481 */               return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */             } 
/* 2483 */             outBuf = this._textBuffer.getBufferWithoutReset();
/* 2484 */             outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2485 */             ptr = this._inputPtr;
/*      */             
/*      */             continue;
/*      */           } 
/* 2489 */           switch (codes[c]) {
/*      */             case 1:
/* 2491 */               this._inputPtr = ptr;
/* 2492 */               c = _decodeFastCharEscape();
/* 2493 */               ptr = this._inputPtr;
/*      */               break;
/*      */             case 2:
/* 2496 */               c = _decodeUTF8_2(c, this._inputBuffer[ptr++]);
/*      */               break;
/*      */             case 3:
/* 2499 */               c = _decodeUTF8_3(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
/*      */               break;
/*      */             case 4:
/* 2502 */               c = _decodeUTF8_4(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
/*      */ 
/*      */               
/* 2505 */               outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2506 */               if (outPtr >= outBuf.length) {
/* 2507 */                 outBuf = this._textBuffer.finishCurrentSegment();
/* 2508 */                 outPtr = 0;
/*      */               } 
/* 2510 */               c = 0xDC00 | c & 0x3FF;
/*      */               break;
/*      */             
/*      */             default:
/* 2514 */               if (c < 32) {
/*      */                 
/* 2516 */                 _throwUnquotedSpace(c, "string value");
/*      */                 break;
/*      */               } 
/* 2519 */               _reportInvalidChar(c);
/*      */               break;
/*      */           } 
/*      */           
/* 2523 */           if (outPtr >= outBuf.length) {
/* 2524 */             outBuf = this._textBuffer.finishCurrentSegment();
/* 2525 */             outPtr = 0;
/*      */           } 
/*      */           
/* 2528 */           outBuf[outPtr++] = (char)c;
/*      */           continue;
/*      */         } 
/*      */         outBuf[outPtr++] = (char)c;
/*      */       } 
/*      */     }  } protected JsonToken _startAposString() throws IOException {
/* 2534 */     int ptr = this._inputPtr;
/* 2535 */     int outPtr = 0;
/* 2536 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2537 */     int[] codes = _icUTF8;
/*      */     
/* 2539 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2540 */     byte[] inputBuffer = this._inputBuffer;
/* 2541 */     while (ptr < max) {
/* 2542 */       int c = inputBuffer[ptr] & 0xFF;
/* 2543 */       if (c == 39) {
/* 2544 */         this._inputPtr = ptr + 1;
/* 2545 */         this._textBuffer.setCurrentLength(outPtr);
/* 2546 */         return _valueComplete(JsonToken.VALUE_STRING);
/*      */       } 
/*      */       
/* 2549 */       if (codes[c] != 0) {
/*      */         break;
/*      */       }
/* 2552 */       ptr++;
/* 2553 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 2555 */     this._textBuffer.setCurrentLength(outPtr);
/* 2556 */     this._inputPtr = ptr;
/* 2557 */     return _finishAposString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _finishAposString() throws IOException {
/* 2563 */     int[] codes = _icUTF8;
/* 2564 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/* 2566 */     char[] outBuf = this._textBuffer.getBufferWithoutReset();
/* 2567 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2568 */     int ptr = this._inputPtr;
/* 2569 */     int safeEnd = this._inputEnd - 5;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2575 */       if (ptr >= this._inputEnd) {
/* 2576 */         this._inputPtr = ptr;
/* 2577 */         this._minorState = 45;
/* 2578 */         this._textBuffer.setCurrentLength(outPtr);
/* 2579 */         return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */       } 
/* 2581 */       if (outPtr >= outBuf.length) {
/* 2582 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2583 */         outPtr = 0;
/*      */       } 
/* 2585 */       int max = Math.min(this._inputEnd, ptr + outBuf.length - outPtr);
/* 2586 */       while (ptr < max) {
/* 2587 */         int c = inputBuffer[ptr++] & 0xFF;
/* 2588 */         if (codes[c] != 0 && c != 34) {
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
/* 2601 */           if (ptr >= safeEnd) {
/* 2602 */             this._inputPtr = ptr;
/* 2603 */             this._textBuffer.setCurrentLength(outPtr);
/* 2604 */             if (!_decodeSplitMultiByte(c, codes[c], (ptr < this._inputEnd))) {
/* 2605 */               this._minorStateAfterSplit = 45;
/* 2606 */               return this._currToken = JsonToken.NOT_AVAILABLE;
/*      */             } 
/* 2608 */             outBuf = this._textBuffer.getBufferWithoutReset();
/* 2609 */             outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2610 */             ptr = this._inputPtr;
/*      */             
/*      */             continue;
/*      */           } 
/* 2614 */           switch (codes[c]) {
/*      */             case 1:
/* 2616 */               this._inputPtr = ptr;
/* 2617 */               c = _decodeFastCharEscape();
/* 2618 */               ptr = this._inputPtr;
/*      */               break;
/*      */             case 2:
/* 2621 */               c = _decodeUTF8_2(c, this._inputBuffer[ptr++]);
/*      */               break;
/*      */             case 3:
/* 2624 */               c = _decodeUTF8_3(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
/*      */               break;
/*      */             case 4:
/* 2627 */               c = _decodeUTF8_4(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
/*      */ 
/*      */               
/* 2630 */               outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2631 */               if (outPtr >= outBuf.length) {
/* 2632 */                 outBuf = this._textBuffer.finishCurrentSegment();
/* 2633 */                 outPtr = 0;
/*      */               } 
/* 2635 */               c = 0xDC00 | c & 0x3FF;
/*      */               break;
/*      */             
/*      */             default:
/* 2639 */               if (c < 32) {
/*      */                 
/* 2641 */                 _throwUnquotedSpace(c, "string value");
/*      */                 break;
/*      */               } 
/* 2644 */               _reportInvalidChar(c);
/*      */               break;
/*      */           } 
/*      */           
/* 2648 */           if (outPtr >= outBuf.length) {
/* 2649 */             outBuf = this._textBuffer.finishCurrentSegment();
/* 2650 */             outPtr = 0;
/*      */           } 
/*      */           
/* 2653 */           outBuf[outPtr++] = (char)c; continue;
/*      */         }  if (c == 39) {
/*      */           this._inputPtr = ptr; this._textBuffer.setCurrentLength(outPtr);
/*      */           return _valueComplete(JsonToken.VALUE_STRING);
/*      */         } 
/*      */         outBuf[outPtr++] = (char)c;
/*      */       } 
/* 2660 */     }  } private final boolean _decodeSplitMultiByte(int c, int type, boolean gotNext) throws IOException { switch (type) {
/*      */       case 1:
/* 2662 */         c = _decodeSplitEscaped(0, -1);
/* 2663 */         if (c < 0) {
/* 2664 */           this._minorState = 41;
/* 2665 */           return false;
/*      */         } 
/* 2667 */         this._textBuffer.append((char)c);
/* 2668 */         return true;
/*      */       case 2:
/* 2670 */         if (gotNext) {
/*      */           
/* 2672 */           c = _decodeUTF8_2(c, this._inputBuffer[this._inputPtr++]);
/* 2673 */           this._textBuffer.append((char)c);
/* 2674 */           return true;
/*      */         } 
/* 2676 */         this._minorState = 42;
/* 2677 */         this._pending32 = c;
/* 2678 */         return false;
/*      */       case 3:
/* 2680 */         c &= 0xF;
/* 2681 */         if (gotNext) {
/* 2682 */           return _decodeSplitUTF8_3(c, 1, this._inputBuffer[this._inputPtr++]);
/*      */         }
/* 2684 */         this._minorState = 43;
/* 2685 */         this._pending32 = c;
/* 2686 */         this._pendingBytes = 1;
/* 2687 */         return false;
/*      */       case 4:
/* 2689 */         c &= 0x7;
/* 2690 */         if (gotNext) {
/* 2691 */           return _decodeSplitUTF8_4(c, 1, this._inputBuffer[this._inputPtr++]);
/*      */         }
/* 2693 */         this._pending32 = c;
/* 2694 */         this._pendingBytes = 1;
/* 2695 */         this._minorState = 44;
/* 2696 */         return false;
/*      */     } 
/* 2698 */     if (c < 32) {
/*      */       
/* 2700 */       _throwUnquotedSpace(c, "string value");
/*      */     } else {
/*      */       
/* 2703 */       _reportInvalidChar(c);
/*      */     } 
/* 2705 */     this._textBuffer.append((char)c);
/* 2706 */     return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean _decodeSplitUTF8_3(int prev, int prevCount, int next) throws IOException {
/* 2713 */     if (prevCount == 1) {
/* 2714 */       if ((next & 0xC0) != 128) {
/* 2715 */         _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */       }
/* 2717 */       prev = prev << 6 | next & 0x3F;
/* 2718 */       if (this._inputPtr >= this._inputEnd) {
/* 2719 */         this._minorState = 43;
/* 2720 */         this._pending32 = prev;
/* 2721 */         this._pendingBytes = 2;
/* 2722 */         return false;
/*      */       } 
/* 2724 */       next = this._inputBuffer[this._inputPtr++];
/*      */     } 
/* 2726 */     if ((next & 0xC0) != 128) {
/* 2727 */       _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */     }
/* 2729 */     this._textBuffer.append((char)(prev << 6 | next & 0x3F));
/* 2730 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean _decodeSplitUTF8_4(int prev, int prevCount, int next) throws IOException {
/* 2738 */     if (prevCount == 1) {
/* 2739 */       if ((next & 0xC0) != 128) {
/* 2740 */         _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */       }
/* 2742 */       prev = prev << 6 | next & 0x3F;
/* 2743 */       if (this._inputPtr >= this._inputEnd) {
/* 2744 */         this._minorState = 44;
/* 2745 */         this._pending32 = prev;
/* 2746 */         this._pendingBytes = 2;
/* 2747 */         return false;
/*      */       } 
/* 2749 */       prevCount = 2;
/* 2750 */       next = this._inputBuffer[this._inputPtr++];
/*      */     } 
/* 2752 */     if (prevCount == 2) {
/* 2753 */       if ((next & 0xC0) != 128) {
/* 2754 */         _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */       }
/* 2756 */       prev = prev << 6 | next & 0x3F;
/* 2757 */       if (this._inputPtr >= this._inputEnd) {
/* 2758 */         this._minorState = 44;
/* 2759 */         this._pending32 = prev;
/* 2760 */         this._pendingBytes = 3;
/* 2761 */         return false;
/*      */       } 
/* 2763 */       next = this._inputBuffer[this._inputPtr++];
/*      */     } 
/* 2765 */     if ((next & 0xC0) != 128) {
/* 2766 */       _reportInvalidOther(next & 0xFF, this._inputPtr);
/*      */     }
/* 2768 */     int c = (prev << 6 | next & 0x3F) - 65536;
/*      */     
/* 2770 */     this._textBuffer.append((char)(0xD800 | c >> 10));
/* 2771 */     c = 0xDC00 | c & 0x3FF;
/*      */     
/* 2773 */     this._textBuffer.append((char)c);
/* 2774 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeCharEscape() throws IOException {
/* 2785 */     int left = this._inputEnd - this._inputPtr;
/* 2786 */     if (left < 5) {
/* 2787 */       return _decodeSplitEscaped(0, -1);
/*      */     }
/* 2789 */     return _decodeFastCharEscape();
/*      */   }
/*      */   
/*      */   private final int _decodeFastCharEscape() throws IOException {
/*      */     char c1;
/* 2794 */     int c = this._inputBuffer[this._inputPtr++];
/* 2795 */     switch (c) {
/*      */       
/*      */       case 98:
/* 2798 */         return 8;
/*      */       case 116:
/* 2800 */         return 9;
/*      */       case 110:
/* 2802 */         return 10;
/*      */       case 102:
/* 2804 */         return 12;
/*      */       case 114:
/* 2806 */         return 13;
/*      */ 
/*      */       
/*      */       case 34:
/*      */       case 47:
/*      */       case 92:
/* 2812 */         return (char)c;
/*      */ 
/*      */ 
/*      */       
/*      */       case 117:
/*      */         break;
/*      */ 
/*      */       
/*      */       default:
/* 2821 */         c1 = (char)c;
/* 2822 */         return _handleUnrecognizedCharacterEscape(c1);
/*      */     } 
/*      */ 
/*      */     
/* 2826 */     int ch = this._inputBuffer[this._inputPtr++];
/* 2827 */     int digit = CharTypes.charToHex(ch);
/* 2828 */     int result = digit;
/*      */     
/* 2830 */     if (digit >= 0) {
/* 2831 */       ch = this._inputBuffer[this._inputPtr++];
/* 2832 */       digit = CharTypes.charToHex(ch);
/* 2833 */       if (digit >= 0) {
/* 2834 */         result = result << 4 | digit;
/* 2835 */         ch = this._inputBuffer[this._inputPtr++];
/* 2836 */         digit = CharTypes.charToHex(ch);
/* 2837 */         if (digit >= 0) {
/* 2838 */           result = result << 4 | digit;
/* 2839 */           ch = this._inputBuffer[this._inputPtr++];
/* 2840 */           digit = CharTypes.charToHex(ch);
/* 2841 */           if (digit >= 0) {
/* 2842 */             return result << 4 | digit;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 2847 */     _reportUnexpectedChar(ch & 0xFF, "expected a hex-digit for character escape sequence");
/* 2848 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUTF8_2(int c, int d) throws IOException {
/* 2859 */     if ((d & 0xC0) != 128) {
/* 2860 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2862 */     return (c & 0x1F) << 6 | d & 0x3F;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _decodeUTF8_3(int c, int d, int e) throws IOException {
/* 2867 */     c &= 0xF;
/* 2868 */     if ((d & 0xC0) != 128) {
/* 2869 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2871 */     c = c << 6 | d & 0x3F;
/* 2872 */     if ((e & 0xC0) != 128) {
/* 2873 */       _reportInvalidOther(e & 0xFF, this._inputPtr);
/*      */     }
/* 2875 */     return c << 6 | e & 0x3F;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUTF8_4(int c, int d, int e, int f) throws IOException {
/* 2882 */     if ((d & 0xC0) != 128) {
/* 2883 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2885 */     c = (c & 0x7) << 6 | d & 0x3F;
/* 2886 */     if ((e & 0xC0) != 128) {
/* 2887 */       _reportInvalidOther(e & 0xFF, this._inputPtr);
/*      */     }
/* 2889 */     c = c << 6 | e & 0x3F;
/* 2890 */     if ((f & 0xC0) != 128) {
/* 2891 */       _reportInvalidOther(f & 0xFF, this._inputPtr);
/*      */     }
/* 2893 */     return (c << 6 | f & 0x3F) - 65536;
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/async/NonBlockingJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */