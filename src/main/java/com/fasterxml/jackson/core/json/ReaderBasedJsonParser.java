/*      */ package com.fasterxml.jackson.core.json;
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonLocation;
/*      */ import com.fasterxml.jackson.core.JsonParseException;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.SerializableString;
/*      */ import com.fasterxml.jackson.core.StreamReadCapability;
/*      */ import com.fasterxml.jackson.core.base.ParserBase;
/*      */ import com.fasterxml.jackson.core.io.CharTypes;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*      */ import com.fasterxml.jackson.core.util.TextBuffer;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.Writer;
/*      */ 
/*      */ public class ReaderBasedJsonParser extends ParserBase {
/*   23 */   private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
/*      */ 
/*      */   
/*   26 */   private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
/*      */ 
/*      */   
/*   29 */   private static final int FEAT_MASK_NON_NUM_NUMBERS = JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS.getMask();
/*      */ 
/*      */   
/*   32 */   private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
/*   33 */   private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
/*   34 */   private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
/*      */   
/*   36 */   private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
/*   37 */   private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
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
/*      */   protected Reader _reader;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _inputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _bufferRecyclable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectCodec _objectCodec;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final CharsToNameCanonicalizer _symbols;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _hashSeed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _tokenIncomplete;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected long _nameStartOffset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _nameStartRow;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _nameStartCol;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st, char[] inputBuffer, int start, int end, boolean bufferRecyclable) {
/*  145 */     super(ctxt, features);
/*  146 */     this._reader = r;
/*  147 */     this._objectCodec = codec;
/*  148 */     this._inputBuffer = inputBuffer;
/*  149 */     this._inputPtr = start;
/*  150 */     this._inputEnd = end;
/*  151 */     this._currInputRowStart = start;
/*      */     
/*  153 */     this._currInputProcessed = -start;
/*  154 */     this._symbols = st;
/*  155 */     this._hashSeed = st.hashSeed();
/*  156 */     this._bufferRecyclable = bufferRecyclable;
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
/*      */   public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st) {
/*  172 */     super(ctxt, features);
/*  173 */     this._reader = r;
/*  174 */     this._inputBuffer = ctxt.allocTokenBuffer();
/*  175 */     this._inputPtr = 0;
/*  176 */     this._inputEnd = 0;
/*  177 */     this._objectCodec = codec;
/*  178 */     this._symbols = st;
/*  179 */     this._hashSeed = st.hashSeed();
/*  180 */     this._bufferRecyclable = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectCodec getCodec() {
/*  189 */     return this._objectCodec; } public void setCodec(ObjectCodec c) {
/*  190 */     this._objectCodec = c;
/*      */   }
/*      */   
/*      */   public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
/*  194 */     return JSON_READ_CAPABILITIES;
/*      */   }
/*      */ 
/*      */   
/*      */   public int releaseBuffered(Writer w) throws IOException {
/*  199 */     int count = this._inputEnd - this._inputPtr;
/*  200 */     if (count < 1) return 0;
/*      */     
/*  202 */     int origPtr = this._inputPtr;
/*  203 */     this._inputPtr += count;
/*  204 */     w.write(this._inputBuffer, origPtr, count);
/*  205 */     return count;
/*      */   }
/*      */   public Object getInputSource() {
/*  208 */     return this._reader;
/*      */   }
/*      */   @Deprecated
/*      */   protected char getNextChar(String eofMsg) throws IOException {
/*  212 */     return getNextChar(eofMsg, (JsonToken)null);
/*      */   }
/*      */   
/*      */   protected char getNextChar(String eofMsg, JsonToken forToken) throws IOException {
/*  216 */     if (this._inputPtr >= this._inputEnd && 
/*  217 */       !_loadMore()) {
/*  218 */       _reportInvalidEOF(eofMsg, forToken);
/*      */     }
/*      */     
/*  221 */     return this._inputBuffer[this._inputPtr++];
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
/*      */   protected void _closeInput() throws IOException {
/*  233 */     if (this._reader != null) {
/*  234 */       if (this._ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
/*  235 */         this._reader.close();
/*      */       }
/*  237 */       this._reader = null;
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
/*      */   protected void _releaseBuffers() throws IOException {
/*  250 */     super._releaseBuffers();
/*      */     
/*  252 */     this._symbols.release();
/*      */     
/*  254 */     if (this._bufferRecyclable) {
/*  255 */       char[] buf = this._inputBuffer;
/*  256 */       if (buf != null) {
/*  257 */         this._inputBuffer = null;
/*  258 */         this._ioContext.releaseTokenBuffer(buf);
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
/*      */   protected void _loadMoreGuaranteed() throws IOException {
/*  270 */     if (!_loadMore()) _reportInvalidEOF();
/*      */   
/*      */   }
/*      */   
/*      */   protected boolean _loadMore() throws IOException {
/*  275 */     if (this._reader != null) {
/*  276 */       int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
/*  277 */       if (count > 0) {
/*  278 */         int bufSize = this._inputEnd;
/*  279 */         this._currInputProcessed += bufSize;
/*  280 */         this._currInputRowStart -= bufSize;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  285 */         this._nameStartOffset -= bufSize;
/*      */         
/*  287 */         this._inputPtr = 0;
/*  288 */         this._inputEnd = count;
/*      */         
/*  290 */         return true;
/*      */       } 
/*      */       
/*  293 */       _closeInput();
/*      */       
/*  295 */       if (count == 0) {
/*  296 */         throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
/*      */       }
/*      */     } 
/*  299 */     return false;
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
/*      */   public final String getText() throws IOException {
/*  317 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  318 */       if (this._tokenIncomplete) {
/*  319 */         this._tokenIncomplete = false;
/*  320 */         _finishString();
/*      */       } 
/*  322 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  324 */     return _getText2(this._currToken);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getText(Writer writer) throws IOException {
/*  330 */     JsonToken t = this._currToken;
/*  331 */     if (t == JsonToken.VALUE_STRING) {
/*  332 */       if (this._tokenIncomplete) {
/*  333 */         this._tokenIncomplete = false;
/*  334 */         _finishString();
/*      */       } 
/*  336 */       return this._textBuffer.contentsToWriter(writer);
/*      */     } 
/*  338 */     if (t == JsonToken.FIELD_NAME) {
/*  339 */       String n = this._parsingContext.getCurrentName();
/*  340 */       writer.write(n);
/*  341 */       return n.length();
/*      */     } 
/*  343 */     if (t != null) {
/*  344 */       if (t.isNumeric()) {
/*  345 */         return this._textBuffer.contentsToWriter(writer);
/*      */       }
/*  347 */       char[] ch = t.asCharArray();
/*  348 */       writer.write(ch);
/*  349 */       return ch.length;
/*      */     } 
/*  351 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String getValueAsString() throws IOException {
/*  360 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  361 */       if (this._tokenIncomplete) {
/*  362 */         this._tokenIncomplete = false;
/*  363 */         _finishString();
/*      */       } 
/*  365 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  367 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  368 */       return getCurrentName();
/*      */     }
/*  370 */     return super.getValueAsString(null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final String getValueAsString(String defValue) throws IOException {
/*  376 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  377 */       if (this._tokenIncomplete) {
/*  378 */         this._tokenIncomplete = false;
/*  379 */         _finishString();
/*      */       } 
/*  381 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  383 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  384 */       return getCurrentName();
/*      */     }
/*  386 */     return super.getValueAsString(defValue);
/*      */   }
/*      */   
/*      */   protected final String _getText2(JsonToken t) {
/*  390 */     if (t == null) {
/*  391 */       return null;
/*      */     }
/*  393 */     switch (t.id()) {
/*      */       case 5:
/*  395 */         return this._parsingContext.getCurrentName();
/*      */ 
/*      */       
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  401 */         return this._textBuffer.contentsAsString();
/*      */     } 
/*  403 */     return t.asString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final char[] getTextCharacters() throws IOException {
/*  410 */     if (this._currToken != null) {
/*  411 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  413 */           if (!this._nameCopied) {
/*  414 */             String name = this._parsingContext.getCurrentName();
/*  415 */             int nameLen = name.length();
/*  416 */             if (this._nameCopyBuffer == null) {
/*  417 */               this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  418 */             } else if (this._nameCopyBuffer.length < nameLen) {
/*  419 */               this._nameCopyBuffer = new char[nameLen];
/*      */             } 
/*  421 */             name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  422 */             this._nameCopied = true;
/*      */           } 
/*  424 */           return this._nameCopyBuffer;
/*      */         case 6:
/*  426 */           if (this._tokenIncomplete) {
/*  427 */             this._tokenIncomplete = false;
/*  428 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  433 */           return this._textBuffer.getTextBuffer();
/*      */       } 
/*  435 */       return this._currToken.asCharArray();
/*      */     } 
/*      */     
/*  438 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getTextLength() throws IOException {
/*  444 */     if (this._currToken != null) {
/*  445 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  447 */           return this._parsingContext.getCurrentName().length();
/*      */         case 6:
/*  449 */           if (this._tokenIncomplete) {
/*  450 */             this._tokenIncomplete = false;
/*  451 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  456 */           return this._textBuffer.size();
/*      */       } 
/*  458 */       return (this._currToken.asCharArray()).length;
/*      */     } 
/*      */     
/*  461 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getTextOffset() throws IOException {
/*  468 */     if (this._currToken != null) {
/*  469 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  471 */           return 0;
/*      */         case 6:
/*  473 */           if (this._tokenIncomplete) {
/*  474 */             this._tokenIncomplete = false;
/*  475 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  480 */           return this._textBuffer.getTextOffset();
/*      */       } 
/*      */     
/*      */     }
/*  484 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/*  490 */     if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT && this._binaryValue != null) {
/*  491 */       return this._binaryValue;
/*      */     }
/*  493 */     if (this._currToken != JsonToken.VALUE_STRING) {
/*  494 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */     
/*  497 */     if (this._tokenIncomplete) {
/*      */       try {
/*  499 */         this._binaryValue = _decodeBase64(b64variant);
/*  500 */       } catch (IllegalArgumentException iae) {
/*  501 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  506 */       this._tokenIncomplete = false;
/*      */     }
/*  508 */     else if (this._binaryValue == null) {
/*      */       
/*  510 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  511 */       _decodeBase64(getText(), builder, b64variant);
/*  512 */       this._binaryValue = builder.toByteArray();
/*      */     } 
/*      */     
/*  515 */     return this._binaryValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/*  522 */     if (!this._tokenIncomplete || this._currToken != JsonToken.VALUE_STRING) {
/*  523 */       byte[] b = getBinaryValue(b64variant);
/*  524 */       out.write(b);
/*  525 */       return b.length;
/*      */     } 
/*      */     
/*  528 */     byte[] buf = this._ioContext.allocBase64Buffer();
/*      */     try {
/*  530 */       return _readBinary(b64variant, out, buf);
/*      */     } finally {
/*  532 */       this._ioContext.releaseBase64Buffer(buf);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
/*  538 */     int outputPtr = 0;
/*  539 */     int outputEnd = buffer.length - 3;
/*  540 */     int outputCount = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  546 */       if (this._inputPtr >= this._inputEnd) {
/*  547 */         _loadMoreGuaranteed();
/*      */       }
/*  549 */       char ch = this._inputBuffer[this._inputPtr++];
/*  550 */       if (ch > ' ') {
/*  551 */         int bits = b64variant.decodeBase64Char(ch);
/*  552 */         if (bits < 0) {
/*  553 */           if (ch == '"') {
/*      */             break;
/*      */           }
/*  556 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/*  557 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  563 */         if (outputPtr > outputEnd) {
/*  564 */           outputCount += outputPtr;
/*  565 */           out.write(buffer, 0, outputPtr);
/*  566 */           outputPtr = 0;
/*      */         } 
/*      */         
/*  569 */         int decodedData = bits;
/*      */ 
/*      */ 
/*      */         
/*  573 */         if (this._inputPtr >= this._inputEnd) {
/*  574 */           _loadMoreGuaranteed();
/*      */         }
/*  576 */         ch = this._inputBuffer[this._inputPtr++];
/*  577 */         bits = b64variant.decodeBase64Char(ch);
/*  578 */         if (bits < 0) {
/*  579 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/*  581 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/*  584 */         if (this._inputPtr >= this._inputEnd) {
/*  585 */           _loadMoreGuaranteed();
/*      */         }
/*  587 */         ch = this._inputBuffer[this._inputPtr++];
/*  588 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/*  591 */         if (bits < 0) {
/*  592 */           if (bits != -2) {
/*      */             
/*  594 */             if (ch == '"') {
/*  595 */               decodedData >>= 4;
/*  596 */               buffer[outputPtr++] = (byte)decodedData;
/*  597 */               if (b64variant.usesPadding()) {
/*  598 */                 this._inputPtr--;
/*  599 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/*      */               break;
/*      */             } 
/*  603 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/*  605 */           if (bits == -2) {
/*      */             
/*  607 */             if (this._inputPtr >= this._inputEnd) {
/*  608 */               _loadMoreGuaranteed();
/*      */             }
/*  610 */             ch = this._inputBuffer[this._inputPtr++];
/*  611 */             if (!b64variant.usesPaddingChar(ch) && 
/*  612 */               _decodeBase64Escape(b64variant, ch, 3) != -2) {
/*  613 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/*  617 */             decodedData >>= 4;
/*  618 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  623 */         decodedData = decodedData << 6 | bits;
/*      */         
/*  625 */         if (this._inputPtr >= this._inputEnd) {
/*  626 */           _loadMoreGuaranteed();
/*      */         }
/*  628 */         ch = this._inputBuffer[this._inputPtr++];
/*  629 */         bits = b64variant.decodeBase64Char(ch);
/*  630 */         if (bits < 0) {
/*  631 */           if (bits != -2) {
/*      */             
/*  633 */             if (ch == '"') {
/*  634 */               decodedData >>= 2;
/*  635 */               buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  636 */               buffer[outputPtr++] = (byte)decodedData;
/*  637 */               if (b64variant.usesPadding()) {
/*  638 */                 this._inputPtr--;
/*  639 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/*      */               break;
/*      */             } 
/*  643 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/*  645 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  652 */             decodedData >>= 2;
/*  653 */             buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  654 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  659 */         decodedData = decodedData << 6 | bits;
/*  660 */         buffer[outputPtr++] = (byte)(decodedData >> 16);
/*  661 */         buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  662 */         buffer[outputPtr++] = (byte)decodedData;
/*      */       } 
/*  664 */     }  this._tokenIncomplete = false;
/*  665 */     if (outputPtr > 0) {
/*  666 */       outputCount += outputPtr;
/*  667 */       out.write(buffer, 0, outputPtr);
/*      */     } 
/*  669 */     return outputCount;
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
/*      */   public final JsonToken nextToken() throws IOException {
/*      */     JsonToken t;
/*  689 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  690 */       return _nextAfterName();
/*      */     }
/*      */ 
/*      */     
/*  694 */     this._numTypesValid = 0;
/*  695 */     if (this._tokenIncomplete) {
/*  696 */       _skipString();
/*      */     }
/*  698 */     int i = _skipWSOrEnd();
/*  699 */     if (i < 0) {
/*      */ 
/*      */       
/*  702 */       close();
/*  703 */       return this._currToken = null;
/*      */     } 
/*      */     
/*  706 */     this._binaryValue = null;
/*      */ 
/*      */     
/*  709 */     if (i == 93 || i == 125) {
/*  710 */       _closeScope(i);
/*  711 */       return this._currToken;
/*      */     } 
/*      */ 
/*      */     
/*  715 */     if (this._parsingContext.expectComma()) {
/*  716 */       i = _skipComma(i);
/*      */ 
/*      */       
/*  719 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  720 */         i == 93 || i == 125)) {
/*  721 */         _closeScope(i);
/*  722 */         return this._currToken;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  730 */     boolean inObject = this._parsingContext.inObject();
/*  731 */     if (inObject) {
/*      */       
/*  733 */       _updateNameLocation();
/*  734 */       String name = (i == 34) ? _parseName() : _handleOddName(i);
/*  735 */       this._parsingContext.setCurrentName(name);
/*  736 */       this._currToken = JsonToken.FIELD_NAME;
/*  737 */       i = _skipColon();
/*      */     } 
/*  739 */     _updateLocation();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  745 */     switch (i) {
/*      */       case 34:
/*  747 */         this._tokenIncomplete = true;
/*  748 */         t = JsonToken.VALUE_STRING;
/*      */         break;
/*      */       case 91:
/*  751 */         if (!inObject) {
/*  752 */           this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*      */         }
/*  754 */         t = JsonToken.START_ARRAY;
/*      */         break;
/*      */       case 123:
/*  757 */         if (!inObject) {
/*  758 */           this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */         }
/*  760 */         t = JsonToken.START_OBJECT;
/*      */         break;
/*      */ 
/*      */       
/*      */       case 125:
/*  765 */         _reportUnexpectedChar(i, "expected a value");
/*      */       case 116:
/*  767 */         _matchTrue();
/*  768 */         t = JsonToken.VALUE_TRUE;
/*      */         break;
/*      */       case 102:
/*  771 */         _matchFalse();
/*  772 */         t = JsonToken.VALUE_FALSE;
/*      */         break;
/*      */       case 110:
/*  775 */         _matchNull();
/*  776 */         t = JsonToken.VALUE_NULL;
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 45:
/*  784 */         t = _parseNegNumber();
/*      */         break;
/*      */       case 46:
/*  787 */         t = _parseFloatThatStartsWithPeriod();
/*      */         break;
/*      */       case 48:
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*  799 */         t = _parsePosNumber(i);
/*      */         break;
/*      */       default:
/*  802 */         t = _handleOddValue(i);
/*      */         break;
/*      */     } 
/*      */     
/*  806 */     if (inObject) {
/*  807 */       this._nextToken = t;
/*  808 */       return this._currToken;
/*      */     } 
/*  810 */     this._currToken = t;
/*  811 */     return t;
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextAfterName() {
/*  816 */     this._nameCopied = false;
/*  817 */     JsonToken t = this._nextToken;
/*  818 */     this._nextToken = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  823 */     if (t == JsonToken.START_ARRAY) {
/*  824 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  825 */     } else if (t == JsonToken.START_OBJECT) {
/*  826 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     } 
/*  828 */     return this._currToken = t;
/*      */   }
/*      */ 
/*      */   
/*      */   public void finishToken() throws IOException {
/*  833 */     if (this._tokenIncomplete) {
/*  834 */       this._tokenIncomplete = false;
/*  835 */       _finishString();
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
/*      */   public boolean nextFieldName(SerializableString sstr) throws IOException {
/*  851 */     this._numTypesValid = 0;
/*  852 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  853 */       _nextAfterName();
/*  854 */       return false;
/*      */     } 
/*  856 */     if (this._tokenIncomplete) {
/*  857 */       _skipString();
/*      */     }
/*  859 */     int i = _skipWSOrEnd();
/*  860 */     if (i < 0) {
/*  861 */       close();
/*  862 */       this._currToken = null;
/*  863 */       return false;
/*      */     } 
/*  865 */     this._binaryValue = null;
/*      */ 
/*      */     
/*  868 */     if (i == 93 || i == 125) {
/*  869 */       _closeScope(i);
/*  870 */       return false;
/*      */     } 
/*      */     
/*  873 */     if (this._parsingContext.expectComma()) {
/*  874 */       i = _skipComma(i);
/*      */ 
/*      */       
/*  877 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  878 */         i == 93 || i == 125)) {
/*  879 */         _closeScope(i);
/*  880 */         return false;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  885 */     if (!this._parsingContext.inObject()) {
/*  886 */       _updateLocation();
/*  887 */       _nextTokenNotInObject(i);
/*  888 */       return false;
/*      */     } 
/*      */     
/*  891 */     _updateNameLocation();
/*  892 */     if (i == 34) {
/*      */       
/*  894 */       char[] nameChars = sstr.asQuotedChars();
/*  895 */       int len = nameChars.length;
/*      */ 
/*      */       
/*  898 */       if (this._inputPtr + len + 4 < this._inputEnd) {
/*      */         
/*  900 */         int end = this._inputPtr + len;
/*  901 */         if (this._inputBuffer[end] == '"') {
/*  902 */           int offset = 0;
/*  903 */           int ptr = this._inputPtr;
/*      */           while (true) {
/*  905 */             if (ptr == end) {
/*  906 */               this._parsingContext.setCurrentName(sstr.getValue());
/*  907 */               _isNextTokenNameYes(_skipColonFast(ptr + 1));
/*  908 */               return true;
/*      */             } 
/*  910 */             if (nameChars[offset] != this._inputBuffer[ptr]) {
/*      */               break;
/*      */             }
/*  913 */             offset++;
/*  914 */             ptr++;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*  919 */     return _isNextTokenNameMaybe(i, sstr.getValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextFieldName() throws IOException {
/*  927 */     this._numTypesValid = 0;
/*  928 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  929 */       _nextAfterName();
/*  930 */       return null;
/*      */     } 
/*  932 */     if (this._tokenIncomplete) {
/*  933 */       _skipString();
/*      */     }
/*  935 */     int i = _skipWSOrEnd();
/*  936 */     if (i < 0) {
/*  937 */       close();
/*  938 */       this._currToken = null;
/*  939 */       return null;
/*      */     } 
/*  941 */     this._binaryValue = null;
/*  942 */     if (i == 93 || i == 125) {
/*  943 */       _closeScope(i);
/*  944 */       return null;
/*      */     } 
/*  946 */     if (this._parsingContext.expectComma()) {
/*  947 */       i = _skipComma(i);
/*  948 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  949 */         i == 93 || i == 125)) {
/*  950 */         _closeScope(i);
/*  951 */         return null;
/*      */       } 
/*      */     } 
/*      */     
/*  955 */     if (!this._parsingContext.inObject()) {
/*  956 */       _updateLocation();
/*  957 */       _nextTokenNotInObject(i);
/*  958 */       return null;
/*      */     } 
/*      */     
/*  961 */     _updateNameLocation();
/*  962 */     String name = (i == 34) ? _parseName() : _handleOddName(i);
/*  963 */     this._parsingContext.setCurrentName(name);
/*  964 */     this._currToken = JsonToken.FIELD_NAME;
/*  965 */     i = _skipColon();
/*      */     
/*  967 */     _updateLocation();
/*  968 */     if (i == 34) {
/*  969 */       this._tokenIncomplete = true;
/*  970 */       this._nextToken = JsonToken.VALUE_STRING;
/*  971 */       return name;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  978 */     switch (i)
/*      */     { case 45:
/*  980 */         t = _parseNegNumber();
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
/* 1019 */         this._nextToken = t;
/* 1020 */         return name;case 46: t = _parseFloatThatStartsWithPeriod(); this._nextToken = t; return name;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return name;case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return name;case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return name;case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return name;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return name;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return name; }  JsonToken t = _handleOddValue(i); this._nextToken = t; return name;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _isNextTokenNameYes(int i) throws IOException {
/* 1025 */     this._currToken = JsonToken.FIELD_NAME;
/* 1026 */     _updateLocation();
/*      */     
/* 1028 */     switch (i) {
/*      */       case 34:
/* 1030 */         this._tokenIncomplete = true;
/* 1031 */         this._nextToken = JsonToken.VALUE_STRING;
/*      */         return;
/*      */       case 91:
/* 1034 */         this._nextToken = JsonToken.START_ARRAY;
/*      */         return;
/*      */       case 123:
/* 1037 */         this._nextToken = JsonToken.START_OBJECT;
/*      */         return;
/*      */       case 116:
/* 1040 */         _matchToken("true", 1);
/* 1041 */         this._nextToken = JsonToken.VALUE_TRUE;
/*      */         return;
/*      */       case 102:
/* 1044 */         _matchToken("false", 1);
/* 1045 */         this._nextToken = JsonToken.VALUE_FALSE;
/*      */         return;
/*      */       case 110:
/* 1048 */         _matchToken("null", 1);
/* 1049 */         this._nextToken = JsonToken.VALUE_NULL;
/*      */         return;
/*      */       case 45:
/* 1052 */         this._nextToken = _parseNegNumber();
/*      */         return;
/*      */       case 46:
/* 1055 */         this._nextToken = _parseFloatThatStartsWithPeriod();
/*      */         return;
/*      */       case 48:
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/* 1067 */         this._nextToken = _parsePosNumber(i);
/*      */         return;
/*      */     } 
/* 1070 */     this._nextToken = _handleOddValue(i);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _isNextTokenNameMaybe(int i, String nameToMatch) throws IOException {
/* 1076 */     String name = (i == 34) ? _parseName() : _handleOddName(i);
/* 1077 */     this._parsingContext.setCurrentName(name);
/* 1078 */     this._currToken = JsonToken.FIELD_NAME;
/* 1079 */     i = _skipColon();
/* 1080 */     _updateLocation();
/* 1081 */     if (i == 34) {
/* 1082 */       this._tokenIncomplete = true;
/* 1083 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1084 */       return nameToMatch.equals(name);
/*      */     } 
/*      */ 
/*      */     
/* 1088 */     switch (i)
/*      */     { case 45:
/* 1090 */         t = _parseNegNumber();
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
/* 1129 */         this._nextToken = t;
/* 1130 */         return nameToMatch.equals(name);case 46: t = _parseFloatThatStartsWithPeriod(); this._nextToken = t; return nameToMatch.equals(name);case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return nameToMatch.equals(name);case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return nameToMatch.equals(name);case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return nameToMatch.equals(name);case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return nameToMatch.equals(name);case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return nameToMatch.equals(name);case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return nameToMatch.equals(name); }  JsonToken t = _handleOddValue(i); this._nextToken = t; return nameToMatch.equals(name);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextTokenNotInObject(int i) throws IOException {
/* 1135 */     if (i == 34) {
/* 1136 */       this._tokenIncomplete = true;
/* 1137 */       return this._currToken = JsonToken.VALUE_STRING;
/*      */     } 
/* 1139 */     switch (i) {
/*      */       case 91:
/* 1141 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1142 */         return this._currToken = JsonToken.START_ARRAY;
/*      */       case 123:
/* 1144 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/* 1145 */         return this._currToken = JsonToken.START_OBJECT;
/*      */       case 116:
/* 1147 */         _matchToken("true", 1);
/* 1148 */         return this._currToken = JsonToken.VALUE_TRUE;
/*      */       case 102:
/* 1150 */         _matchToken("false", 1);
/* 1151 */         return this._currToken = JsonToken.VALUE_FALSE;
/*      */       case 110:
/* 1153 */         _matchToken("null", 1);
/* 1154 */         return this._currToken = JsonToken.VALUE_NULL;
/*      */       case 45:
/* 1156 */         return this._currToken = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 46:
/* 1162 */         return this._currToken = _parseFloatThatStartsWithPeriod();
/*      */       case 48:
/*      */       case 49:
/*      */       case 50:
/*      */       case 51:
/*      */       case 52:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/* 1173 */         return this._currToken = _parsePosNumber(i);
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
/*      */       case 44:
/* 1185 */         if (!this._parsingContext.inRoot() && (
/* 1186 */           this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/* 1187 */           this._inputPtr--;
/* 1188 */           return this._currToken = JsonToken.VALUE_NULL;
/*      */         } 
/*      */         break;
/*      */     } 
/* 1192 */     return this._currToken = _handleOddValue(i);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final String nextTextValue() throws IOException {
/* 1198 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1199 */       this._nameCopied = false;
/* 1200 */       JsonToken t = this._nextToken;
/* 1201 */       this._nextToken = null;
/* 1202 */       this._currToken = t;
/* 1203 */       if (t == JsonToken.VALUE_STRING) {
/* 1204 */         if (this._tokenIncomplete) {
/* 1205 */           this._tokenIncomplete = false;
/* 1206 */           _finishString();
/*      */         } 
/* 1208 */         return this._textBuffer.contentsAsString();
/*      */       } 
/* 1210 */       if (t == JsonToken.START_ARRAY) {
/* 1211 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1212 */       } else if (t == JsonToken.START_OBJECT) {
/* 1213 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1215 */       return null;
/*      */     } 
/*      */     
/* 1218 */     return (nextToken() == JsonToken.VALUE_STRING) ? getText() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int nextIntValue(int defaultValue) throws IOException {
/* 1225 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1226 */       this._nameCopied = false;
/* 1227 */       JsonToken t = this._nextToken;
/* 1228 */       this._nextToken = null;
/* 1229 */       this._currToken = t;
/* 1230 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1231 */         return getIntValue();
/*      */       }
/* 1233 */       if (t == JsonToken.START_ARRAY) {
/* 1234 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1235 */       } else if (t == JsonToken.START_OBJECT) {
/* 1236 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1238 */       return defaultValue;
/*      */     } 
/*      */     
/* 1241 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getIntValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long nextLongValue(long defaultValue) throws IOException {
/* 1248 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1249 */       this._nameCopied = false;
/* 1250 */       JsonToken t = this._nextToken;
/* 1251 */       this._nextToken = null;
/* 1252 */       this._currToken = t;
/* 1253 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1254 */         return getLongValue();
/*      */       }
/* 1256 */       if (t == JsonToken.START_ARRAY) {
/* 1257 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1258 */       } else if (t == JsonToken.START_OBJECT) {
/* 1259 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1261 */       return defaultValue;
/*      */     } 
/*      */     
/* 1264 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getLongValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Boolean nextBooleanValue() throws IOException {
/* 1271 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1272 */       this._nameCopied = false;
/* 1273 */       JsonToken jsonToken = this._nextToken;
/* 1274 */       this._nextToken = null;
/* 1275 */       this._currToken = jsonToken;
/* 1276 */       if (jsonToken == JsonToken.VALUE_TRUE) {
/* 1277 */         return Boolean.TRUE;
/*      */       }
/* 1279 */       if (jsonToken == JsonToken.VALUE_FALSE) {
/* 1280 */         return Boolean.FALSE;
/*      */       }
/* 1282 */       if (jsonToken == JsonToken.START_ARRAY) {
/* 1283 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1284 */       } else if (jsonToken == JsonToken.START_OBJECT) {
/* 1285 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1287 */       return null;
/*      */     } 
/* 1289 */     JsonToken t = nextToken();
/* 1290 */     if (t != null) {
/* 1291 */       int id = t.id();
/* 1292 */       if (id == 9) return Boolean.TRUE; 
/* 1293 */       if (id == 10) return Boolean.FALSE; 
/*      */     } 
/* 1295 */     return null;
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
/*      */   protected final JsonToken _parseFloatThatStartsWithPeriod() throws IOException {
/* 1308 */     if (!isEnabled(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
/* 1309 */       return _handleOddValue(46);
/*      */     }
/* 1311 */     return _parseFloat(46, this._inputPtr - 1, this._inputPtr, false, 0);
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
/*      */   protected final JsonToken _parsePosNumber(int ch) throws IOException {
/* 1344 */     int ptr = this._inputPtr;
/* 1345 */     int startPtr = ptr - 1;
/* 1346 */     int inputLen = this._inputEnd;
/*      */ 
/*      */     
/* 1349 */     if (ch == 48) {
/* 1350 */       return _parseNumber2(false, startPtr);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1359 */     int intLen = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1364 */       if (ptr >= inputLen) {
/* 1365 */         this._inputPtr = startPtr;
/* 1366 */         return _parseNumber2(false, startPtr);
/*      */       } 
/* 1368 */       ch = this._inputBuffer[ptr++];
/* 1369 */       if (ch < 48 || ch > 57) {
/*      */         break;
/*      */       }
/* 1372 */       intLen++;
/*      */     } 
/* 1374 */     if (ch == 46 || ch == 101 || ch == 69) {
/* 1375 */       this._inputPtr = ptr;
/* 1376 */       return _parseFloat(ch, startPtr, ptr, false, intLen);
/*      */     } 
/*      */ 
/*      */     
/* 1380 */     this._inputPtr = --ptr;
/*      */     
/* 1382 */     if (this._parsingContext.inRoot()) {
/* 1383 */       _verifyRootSpace(ch);
/*      */     }
/* 1385 */     int len = ptr - startPtr;
/* 1386 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/* 1387 */     return resetInt(false, intLen);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _parseFloat(int ch, int startPtr, int ptr, boolean neg, int intLen) throws IOException {
/* 1393 */     int inputLen = this._inputEnd;
/* 1394 */     int fractLen = 0;
/*      */ 
/*      */     
/* 1397 */     if (ch == 46) {
/*      */       
/*      */       while (true) {
/* 1400 */         if (ptr >= inputLen) {
/* 1401 */           return _parseNumber2(neg, startPtr);
/*      */         }
/* 1403 */         ch = this._inputBuffer[ptr++];
/* 1404 */         if (ch < 48 || ch > 57) {
/*      */           break;
/*      */         }
/* 1407 */         fractLen++;
/*      */       } 
/*      */       
/* 1410 */       if (fractLen == 0) {
/* 1411 */         reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
/*      */       }
/*      */     } 
/* 1414 */     int expLen = 0;
/* 1415 */     if (ch == 101 || ch == 69) {
/* 1416 */       if (ptr >= inputLen) {
/* 1417 */         this._inputPtr = startPtr;
/* 1418 */         return _parseNumber2(neg, startPtr);
/*      */       } 
/*      */       
/* 1421 */       ch = this._inputBuffer[ptr++];
/* 1422 */       if (ch == 45 || ch == 43) {
/* 1423 */         if (ptr >= inputLen) {
/* 1424 */           this._inputPtr = startPtr;
/* 1425 */           return _parseNumber2(neg, startPtr);
/*      */         } 
/* 1427 */         ch = this._inputBuffer[ptr++];
/*      */       } 
/* 1429 */       while (ch <= 57 && ch >= 48) {
/* 1430 */         expLen++;
/* 1431 */         if (ptr >= inputLen) {
/* 1432 */           this._inputPtr = startPtr;
/* 1433 */           return _parseNumber2(neg, startPtr);
/*      */         } 
/* 1435 */         ch = this._inputBuffer[ptr++];
/*      */       } 
/*      */       
/* 1438 */       if (expLen == 0) {
/* 1439 */         reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1443 */     this._inputPtr = --ptr;
/*      */     
/* 1445 */     if (this._parsingContext.inRoot()) {
/* 1446 */       _verifyRootSpace(ch);
/*      */     }
/* 1448 */     int len = ptr - startPtr;
/* 1449 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/*      */     
/* 1451 */     return resetFloat(neg, intLen, fractLen, expLen);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final JsonToken _parseNegNumber() throws IOException {
/* 1456 */     int ptr = this._inputPtr;
/* 1457 */     int startPtr = ptr - 1;
/* 1458 */     int inputLen = this._inputEnd;
/*      */     
/* 1460 */     if (ptr >= inputLen) {
/* 1461 */       return _parseNumber2(true, startPtr);
/*      */     }
/* 1463 */     int ch = this._inputBuffer[ptr++];
/*      */     
/* 1465 */     if (ch > 57 || ch < 48) {
/* 1466 */       this._inputPtr = ptr;
/* 1467 */       return _handleInvalidNumberStart(ch, true);
/*      */     } 
/*      */     
/* 1470 */     if (ch == 48) {
/* 1471 */       return _parseNumber2(true, startPtr);
/*      */     }
/* 1473 */     int intLen = 1;
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1478 */       if (ptr >= inputLen) {
/* 1479 */         return _parseNumber2(true, startPtr);
/*      */       }
/* 1481 */       ch = this._inputBuffer[ptr++];
/* 1482 */       if (ch < 48 || ch > 57) {
/*      */         break;
/*      */       }
/* 1485 */       intLen++;
/*      */     } 
/*      */     
/* 1488 */     if (ch == 46 || ch == 101 || ch == 69) {
/* 1489 */       this._inputPtr = ptr;
/* 1490 */       return _parseFloat(ch, startPtr, ptr, true, intLen);
/*      */     } 
/*      */     
/* 1493 */     this._inputPtr = --ptr;
/* 1494 */     if (this._parsingContext.inRoot()) {
/* 1495 */       _verifyRootSpace(ch);
/*      */     }
/* 1497 */     int len = ptr - startPtr;
/* 1498 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/* 1499 */     return resetInt(true, intLen);
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
/*      */   private final JsonToken _parseNumber2(boolean neg, int startPtr) throws IOException {
/* 1520 */     this._inputPtr = neg ? (startPtr + 1) : startPtr;
/* 1521 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1522 */     int outPtr = 0;
/*      */ 
/*      */     
/* 1525 */     if (neg) {
/* 1526 */       outBuf[outPtr++] = '-';
/*      */     }
/*      */ 
/*      */     
/* 1530 */     int intLen = 0;
/*      */     
/* 1532 */     char c = (this._inputPtr < this._inputEnd) ? this._inputBuffer[this._inputPtr++] : getNextChar("No digit following minus sign", JsonToken.VALUE_NUMBER_INT);
/* 1533 */     if (c == '0') {
/* 1534 */       c = _verifyNoLeadingZeroes();
/*      */     }
/* 1536 */     boolean eof = false;
/*      */ 
/*      */ 
/*      */     
/* 1540 */     while (c >= '0' && c <= '9') {
/* 1541 */       intLen++;
/* 1542 */       if (outPtr >= outBuf.length) {
/* 1543 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1544 */         outPtr = 0;
/*      */       } 
/* 1546 */       outBuf[outPtr++] = c;
/* 1547 */       if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */         
/* 1549 */         c = Character.MIN_VALUE;
/* 1550 */         eof = true;
/*      */         break;
/*      */       } 
/* 1553 */       c = this._inputBuffer[this._inputPtr++];
/*      */     } 
/*      */     
/* 1556 */     if (intLen == 0) {
/* 1557 */       return _handleInvalidNumberStart(c, neg);
/*      */     }
/*      */     
/* 1560 */     int fractLen = 0;
/*      */     
/* 1562 */     if (c == '.') {
/* 1563 */       if (outPtr >= outBuf.length) {
/* 1564 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1565 */         outPtr = 0;
/*      */       } 
/* 1567 */       outBuf[outPtr++] = c;
/*      */ 
/*      */       
/*      */       while (true) {
/* 1571 */         if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1572 */           eof = true;
/*      */           break;
/*      */         } 
/* 1575 */         c = this._inputBuffer[this._inputPtr++];
/* 1576 */         if (c < '0' || c > '9') {
/*      */           break;
/*      */         }
/* 1579 */         fractLen++;
/* 1580 */         if (outPtr >= outBuf.length) {
/* 1581 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1582 */           outPtr = 0;
/*      */         } 
/* 1584 */         outBuf[outPtr++] = c;
/*      */       } 
/*      */       
/* 1587 */       if (fractLen == 0) {
/* 1588 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1592 */     int expLen = 0;
/* 1593 */     if (c == 'e' || c == 'E') {
/* 1594 */       if (outPtr >= outBuf.length) {
/* 1595 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1596 */         outPtr = 0;
/*      */       } 
/* 1598 */       outBuf[outPtr++] = c;
/*      */ 
/*      */       
/* 1601 */       c = (this._inputPtr < this._inputEnd) ? this._inputBuffer[this._inputPtr++] : getNextChar("expected a digit for number exponent");
/*      */       
/* 1603 */       if (c == '-' || c == '+') {
/* 1604 */         if (outPtr >= outBuf.length) {
/* 1605 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1606 */           outPtr = 0;
/*      */         } 
/* 1608 */         outBuf[outPtr++] = c;
/*      */ 
/*      */         
/* 1611 */         c = (this._inputPtr < this._inputEnd) ? this._inputBuffer[this._inputPtr++] : getNextChar("expected a digit for number exponent");
/*      */       } 
/*      */ 
/*      */       
/* 1615 */       while (c <= '9' && c >= '0') {
/* 1616 */         expLen++;
/* 1617 */         if (outPtr >= outBuf.length) {
/* 1618 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1619 */           outPtr = 0;
/*      */         } 
/* 1621 */         outBuf[outPtr++] = c;
/* 1622 */         if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1623 */           eof = true;
/*      */           break;
/*      */         } 
/* 1626 */         c = this._inputBuffer[this._inputPtr++];
/*      */       } 
/*      */       
/* 1629 */       if (expLen == 0) {
/* 1630 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1635 */     if (!eof) {
/* 1636 */       this._inputPtr--;
/* 1637 */       if (this._parsingContext.inRoot()) {
/* 1638 */         _verifyRootSpace(c);
/*      */       }
/*      */     } 
/* 1641 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1643 */     return reset(neg, intLen, fractLen, expLen);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final char _verifyNoLeadingZeroes() throws IOException {
/* 1651 */     if (this._inputPtr < this._inputEnd) {
/* 1652 */       char ch = this._inputBuffer[this._inputPtr];
/*      */       
/* 1654 */       if (ch < '0' || ch > '9') {
/* 1655 */         return '0';
/*      */       }
/*      */     } 
/*      */     
/* 1659 */     return _verifyNLZ2();
/*      */   }
/*      */ 
/*      */   
/*      */   private char _verifyNLZ2() throws IOException {
/* 1664 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1665 */       return '0';
/*      */     }
/* 1667 */     char ch = this._inputBuffer[this._inputPtr];
/* 1668 */     if (ch < '0' || ch > '9') {
/* 1669 */       return '0';
/*      */     }
/* 1671 */     if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1672 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/* 1675 */     this._inputPtr++;
/* 1676 */     if (ch == '0') {
/* 1677 */       while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 1678 */         ch = this._inputBuffer[this._inputPtr];
/* 1679 */         if (ch < '0' || ch > '9') {
/* 1680 */           return '0';
/*      */         }
/* 1682 */         this._inputPtr++;
/* 1683 */         if (ch != '0') {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     }
/* 1688 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean negative) throws IOException {
/* 1695 */     if (ch == 73) {
/* 1696 */       if (this._inputPtr >= this._inputEnd && 
/* 1697 */         !_loadMore()) {
/* 1698 */         _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
/*      */       }
/*      */       
/* 1701 */       ch = this._inputBuffer[this._inputPtr++];
/* 1702 */       if (ch == 78) {
/* 1703 */         String match = negative ? "-INF" : "+INF";
/* 1704 */         _matchToken(match, 3);
/* 1705 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 1706 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/* 1708 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 1709 */       } else if (ch == 110) {
/* 1710 */         String match = negative ? "-Infinity" : "+Infinity";
/* 1711 */         _matchToken(match, 3);
/* 1712 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 1713 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/* 1715 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */       } 
/*      */     } 
/* 1718 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 1719 */     return null;
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
/*      */   private final void _verifyRootSpace(int ch) throws IOException {
/* 1737 */     this._inputPtr++;
/* 1738 */     switch (ch) {
/*      */       case 9:
/*      */       case 32:
/*      */         return;
/*      */       case 13:
/* 1743 */         _skipCR();
/*      */         return;
/*      */       case 10:
/* 1746 */         this._currInputRow++;
/* 1747 */         this._currInputRowStart = this._inputPtr;
/*      */         return;
/*      */     } 
/* 1750 */     _reportMissingRootWS(ch);
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
/*      */   protected final String _parseName() throws IOException {
/* 1763 */     int ptr = this._inputPtr;
/* 1764 */     int hash = this._hashSeed;
/* 1765 */     int[] codes = _icLatin1;
/*      */     
/* 1767 */     while (ptr < this._inputEnd) {
/* 1768 */       int ch = this._inputBuffer[ptr];
/* 1769 */       if (ch < codes.length && codes[ch] != 0) {
/* 1770 */         if (ch == 34) {
/* 1771 */           int i = this._inputPtr;
/* 1772 */           this._inputPtr = ptr + 1;
/* 1773 */           return this._symbols.findSymbol(this._inputBuffer, i, ptr - i, hash);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1777 */       hash = hash * 33 + ch;
/* 1778 */       ptr++;
/*      */     } 
/* 1780 */     int start = this._inputPtr;
/* 1781 */     this._inputPtr = ptr;
/* 1782 */     return _parseName2(start, hash, 34);
/*      */   }
/*      */ 
/*      */   
/*      */   private String _parseName2(int startPtr, int hash, int endChar) throws IOException {
/* 1787 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1792 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 1793 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     
/*      */     while (true) {
/* 1796 */       if (this._inputPtr >= this._inputEnd && 
/* 1797 */         !_loadMore()) {
/* 1798 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 1801 */       char c = this._inputBuffer[this._inputPtr++];
/* 1802 */       int i = c;
/* 1803 */       if (i <= 92) {
/* 1804 */         if (i == 92) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1809 */           c = _decodeEscaped();
/* 1810 */         } else if (i <= endChar) {
/* 1811 */           if (i == endChar) {
/*      */             break;
/*      */           }
/* 1814 */           if (i < 32) {
/* 1815 */             _throwUnquotedSpace(i, "name");
/*      */           }
/*      */         } 
/*      */       }
/* 1819 */       hash = hash * 33 + c;
/*      */       
/* 1821 */       outBuf[outPtr++] = c;
/*      */ 
/*      */       
/* 1824 */       if (outPtr >= outBuf.length) {
/* 1825 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1826 */         outPtr = 0;
/*      */       } 
/*      */     } 
/* 1829 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1831 */     TextBuffer tb = this._textBuffer;
/* 1832 */     char[] buf = tb.getTextBuffer();
/* 1833 */     int start = tb.getTextOffset();
/* 1834 */     int len = tb.size();
/* 1835 */     return this._symbols.findSymbol(buf, start, len, hash);
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
/*      */   protected String _handleOddName(int i) throws IOException {
/*      */     boolean firstOk;
/* 1855 */     if (i == 39 && (this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 1856 */       return _parseAposName();
/*      */     }
/*      */     
/* 1859 */     if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
/* 1860 */       _reportUnexpectedChar(i, "was expecting double-quote to start field name");
/*      */     }
/* 1862 */     int[] codes = CharTypes.getInputCodeLatin1JsNames();
/* 1863 */     int maxCode = codes.length;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1868 */     if (i < maxCode) {
/* 1869 */       firstOk = (codes[i] == 0);
/*      */     } else {
/* 1871 */       firstOk = Character.isJavaIdentifierPart((char)i);
/*      */     } 
/* 1873 */     if (!firstOk) {
/* 1874 */       _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/* 1876 */     int ptr = this._inputPtr;
/* 1877 */     int hash = this._hashSeed;
/* 1878 */     int inputLen = this._inputEnd;
/*      */     
/* 1880 */     if (ptr < inputLen) {
/*      */       do {
/* 1882 */         int ch = this._inputBuffer[ptr];
/* 1883 */         if (ch < maxCode) {
/* 1884 */           if (codes[ch] != 0) {
/* 1885 */             int j = this._inputPtr - 1;
/* 1886 */             this._inputPtr = ptr;
/* 1887 */             return this._symbols.findSymbol(this._inputBuffer, j, ptr - j, hash);
/*      */           } 
/* 1889 */         } else if (!Character.isJavaIdentifierPart((char)ch)) {
/* 1890 */           int j = this._inputPtr - 1;
/* 1891 */           this._inputPtr = ptr;
/* 1892 */           return this._symbols.findSymbol(this._inputBuffer, j, ptr - j, hash);
/*      */         } 
/* 1894 */         hash = hash * 33 + ch;
/* 1895 */         ++ptr;
/* 1896 */       } while (ptr < inputLen);
/*      */     }
/* 1898 */     int start = this._inputPtr - 1;
/* 1899 */     this._inputPtr = ptr;
/* 1900 */     return _handleOddName2(start, hash, codes);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _parseAposName() throws IOException {
/* 1906 */     int ptr = this._inputPtr;
/* 1907 */     int hash = this._hashSeed;
/* 1908 */     int inputLen = this._inputEnd;
/*      */     
/* 1910 */     if (ptr < inputLen) {
/* 1911 */       int[] codes = _icLatin1;
/* 1912 */       int maxCode = codes.length;
/*      */       
/*      */       do {
/* 1915 */         int ch = this._inputBuffer[ptr];
/* 1916 */         if (ch == 39) {
/* 1917 */           int i = this._inputPtr;
/* 1918 */           this._inputPtr = ptr + 1;
/* 1919 */           return this._symbols.findSymbol(this._inputBuffer, i, ptr - i, hash);
/*      */         } 
/* 1921 */         if (ch < maxCode && codes[ch] != 0) {
/*      */           break;
/*      */         }
/* 1924 */         hash = hash * 33 + ch;
/* 1925 */         ++ptr;
/* 1926 */       } while (ptr < inputLen);
/*      */     } 
/*      */     
/* 1929 */     int start = this._inputPtr;
/* 1930 */     this._inputPtr = ptr;
/*      */     
/* 1932 */     return _parseName2(start, hash, 39);
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
/*      */   protected JsonToken _handleOddValue(int i) throws IOException {
/* 1949 */     switch (i) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 39:
/* 1956 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 1957 */           return _handleApos();
/*      */         }
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 93:
/* 1965 */         if (!this._parsingContext.inArray()) {
/*      */           break;
/*      */         }
/*      */ 
/*      */       
/*      */       case 44:
/* 1971 */         if (!this._parsingContext.inRoot() && (
/* 1972 */           this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/* 1973 */           this._inputPtr--;
/* 1974 */           return JsonToken.VALUE_NULL;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 78:
/* 1979 */         _matchToken("NaN", 1);
/* 1980 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 1981 */           return resetAsNaN("NaN", Double.NaN);
/*      */         }
/* 1983 */         _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 73:
/* 1986 */         _matchToken("Infinity", 1);
/* 1987 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 1988 */           return resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
/*      */         }
/* 1990 */         _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 43:
/* 1993 */         if (this._inputPtr >= this._inputEnd && 
/* 1994 */           !_loadMore()) {
/* 1995 */           _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
/*      */         }
/*      */         
/* 1998 */         return _handleInvalidNumberStart(this._inputBuffer[this._inputPtr++], false);
/*      */     } 
/*      */     
/* 2001 */     if (Character.isJavaIdentifierStart(i)) {
/* 2002 */       _reportInvalidToken("" + (char)i, _validJsonTokenList());
/*      */     }
/*      */     
/* 2005 */     _reportUnexpectedChar(i, "expected a valid value " + _validJsonValueList());
/* 2006 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _handleApos() throws IOException {
/* 2011 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2012 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     
/*      */     while (true) {
/* 2015 */       if (this._inputPtr >= this._inputEnd && 
/* 2016 */         !_loadMore()) {
/* 2017 */         _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
/*      */       }
/*      */ 
/*      */       
/* 2021 */       char c = this._inputBuffer[this._inputPtr++];
/* 2022 */       int i = c;
/* 2023 */       if (i <= 92) {
/* 2024 */         if (i == 92) {
/*      */ 
/*      */ 
/*      */           
/* 2028 */           c = _decodeEscaped();
/* 2029 */         } else if (i <= 39) {
/* 2030 */           if (i == 39) {
/*      */             break;
/*      */           }
/* 2033 */           if (i < 32) {
/* 2034 */             _throwUnquotedSpace(i, "string value");
/*      */           }
/*      */         } 
/*      */       }
/*      */       
/* 2039 */       if (outPtr >= outBuf.length) {
/* 2040 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2041 */         outPtr = 0;
/*      */       } 
/*      */       
/* 2044 */       outBuf[outPtr++] = c;
/*      */     } 
/* 2046 */     this._textBuffer.setCurrentLength(outPtr);
/* 2047 */     return JsonToken.VALUE_STRING;
/*      */   }
/*      */ 
/*      */   
/*      */   private String _handleOddName2(int startPtr, int hash, int[] codes) throws IOException {
/* 2052 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/* 2053 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 2054 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2055 */     int maxCode = codes.length;
/*      */ 
/*      */     
/* 2058 */     while (this._inputPtr < this._inputEnd || 
/* 2059 */       _loadMore()) {
/*      */ 
/*      */ 
/*      */       
/* 2063 */       char c = this._inputBuffer[this._inputPtr];
/* 2064 */       int i = c;
/* 2065 */       if ((i < maxCode) ? (
/* 2066 */         codes[i] != 0) : 
/*      */ 
/*      */         
/* 2069 */         !Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 2072 */       this._inputPtr++;
/* 2073 */       hash = hash * 33 + i;
/*      */       
/* 2075 */       outBuf[outPtr++] = c;
/*      */ 
/*      */       
/* 2078 */       if (outPtr >= outBuf.length) {
/* 2079 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2080 */         outPtr = 0;
/*      */       } 
/*      */     } 
/* 2083 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 2085 */     TextBuffer tb = this._textBuffer;
/* 2086 */     char[] buf = tb.getTextBuffer();
/* 2087 */     int start = tb.getTextOffset();
/* 2088 */     int len = tb.size();
/*      */     
/* 2090 */     return this._symbols.findSymbol(buf, start, len, hash);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _finishString() throws IOException {
/* 2101 */     int ptr = this._inputPtr;
/* 2102 */     int inputLen = this._inputEnd;
/*      */     
/* 2104 */     if (ptr < inputLen) {
/* 2105 */       int[] codes = _icLatin1;
/* 2106 */       int maxCode = codes.length;
/*      */       
/*      */       do {
/* 2109 */         int ch = this._inputBuffer[ptr];
/* 2110 */         if (ch < maxCode && codes[ch] != 0) {
/* 2111 */           if (ch == 34) {
/* 2112 */             this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/* 2113 */             this._inputPtr = ptr + 1;
/*      */             
/*      */             return;
/*      */           } 
/*      */           break;
/*      */         } 
/* 2119 */         ++ptr;
/* 2120 */       } while (ptr < inputLen);
/*      */     } 
/*      */ 
/*      */     
/* 2124 */     this._textBuffer.resetWithCopy(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/* 2125 */     this._inputPtr = ptr;
/* 2126 */     _finishString2();
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _finishString2() throws IOException {
/* 2131 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 2132 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 2133 */     int[] codes = _icLatin1;
/* 2134 */     int maxCode = codes.length;
/*      */     
/*      */     while (true) {
/* 2137 */       if (this._inputPtr >= this._inputEnd && 
/* 2138 */         !_loadMore()) {
/* 2139 */         _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
/*      */       }
/*      */ 
/*      */       
/* 2143 */       char c = this._inputBuffer[this._inputPtr++];
/* 2144 */       int i = c;
/* 2145 */       if (i < maxCode && codes[i] != 0) {
/* 2146 */         if (i == 34)
/*      */           break; 
/* 2148 */         if (i == 92) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2153 */           c = _decodeEscaped();
/* 2154 */         } else if (i < 32) {
/* 2155 */           _throwUnquotedSpace(i, "string value");
/*      */         } 
/*      */       } 
/*      */       
/* 2159 */       if (outPtr >= outBuf.length) {
/* 2160 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2161 */         outPtr = 0;
/*      */       } 
/*      */       
/* 2164 */       outBuf[outPtr++] = c;
/*      */     } 
/* 2166 */     this._textBuffer.setCurrentLength(outPtr);
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
/*      */   protected final void _skipString() throws IOException {
/* 2179 */     this._tokenIncomplete = false;
/*      */     
/* 2181 */     int inPtr = this._inputPtr;
/* 2182 */     int inLen = this._inputEnd;
/* 2183 */     char[] inBuf = this._inputBuffer;
/*      */     
/*      */     while (true) {
/* 2186 */       if (inPtr >= inLen) {
/* 2187 */         this._inputPtr = inPtr;
/* 2188 */         if (!_loadMore()) {
/* 2189 */           _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
/*      */         }
/*      */         
/* 2192 */         inPtr = this._inputPtr;
/* 2193 */         inLen = this._inputEnd;
/*      */       } 
/* 2195 */       char c = inBuf[inPtr++];
/* 2196 */       int i = c;
/* 2197 */       if (i <= 92) {
/* 2198 */         if (i == 92) {
/*      */ 
/*      */           
/* 2201 */           this._inputPtr = inPtr;
/* 2202 */           _decodeEscaped();
/* 2203 */           inPtr = this._inputPtr;
/* 2204 */           inLen = this._inputEnd; continue;
/* 2205 */         }  if (i <= 34) {
/* 2206 */           if (i == 34) {
/* 2207 */             this._inputPtr = inPtr;
/*      */             break;
/*      */           } 
/* 2210 */           if (i < 32) {
/* 2211 */             this._inputPtr = inPtr;
/* 2212 */             _throwUnquotedSpace(i, "string value");
/*      */           } 
/*      */         } 
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
/*      */   protected final void _skipCR() throws IOException {
/* 2228 */     if ((this._inputPtr < this._inputEnd || _loadMore()) && 
/* 2229 */       this._inputBuffer[this._inputPtr] == '\n') {
/* 2230 */       this._inputPtr++;
/*      */     }
/*      */     
/* 2233 */     this._currInputRow++;
/* 2234 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon() throws IOException {
/* 2239 */     if (this._inputPtr + 4 >= this._inputEnd) {
/* 2240 */       return _skipColon2(false);
/*      */     }
/* 2242 */     char c = this._inputBuffer[this._inputPtr];
/* 2243 */     if (c == ':') {
/* 2244 */       int i = this._inputBuffer[++this._inputPtr];
/* 2245 */       if (i > 32) {
/* 2246 */         if (i == 47 || i == 35) {
/* 2247 */           return _skipColon2(true);
/*      */         }
/* 2249 */         this._inputPtr++;
/* 2250 */         return i;
/*      */       } 
/* 2252 */       if (i == 32 || i == 9) {
/* 2253 */         i = this._inputBuffer[++this._inputPtr];
/* 2254 */         if (i > 32) {
/* 2255 */           if (i == 47 || i == 35) {
/* 2256 */             return _skipColon2(true);
/*      */           }
/* 2258 */           this._inputPtr++;
/* 2259 */           return i;
/*      */         } 
/*      */       } 
/* 2262 */       return _skipColon2(true);
/*      */     } 
/* 2264 */     if (c == ' ' || c == '\t') {
/* 2265 */       c = this._inputBuffer[++this._inputPtr];
/*      */     }
/* 2267 */     if (c == ':') {
/* 2268 */       int i = this._inputBuffer[++this._inputPtr];
/* 2269 */       if (i > 32) {
/* 2270 */         if (i == 47 || i == 35) {
/* 2271 */           return _skipColon2(true);
/*      */         }
/* 2273 */         this._inputPtr++;
/* 2274 */         return i;
/*      */       } 
/* 2276 */       if (i == 32 || i == 9) {
/* 2277 */         i = this._inputBuffer[++this._inputPtr];
/* 2278 */         if (i > 32) {
/* 2279 */           if (i == 47 || i == 35) {
/* 2280 */             return _skipColon2(true);
/*      */           }
/* 2282 */           this._inputPtr++;
/* 2283 */           return i;
/*      */         } 
/*      */       } 
/* 2286 */       return _skipColon2(true);
/*      */     } 
/* 2288 */     return _skipColon2(false);
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon2(boolean gotColon) throws IOException {
/* 2293 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2294 */       int i = this._inputBuffer[this._inputPtr++];
/* 2295 */       if (i > 32) {
/* 2296 */         if (i == 47) {
/* 2297 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 2300 */         if (i == 35 && 
/* 2301 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 2305 */         if (gotColon) {
/* 2306 */           return i;
/*      */         }
/* 2308 */         if (i != 58) {
/* 2309 */           _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */         }
/* 2311 */         gotColon = true;
/*      */         continue;
/*      */       } 
/* 2314 */       if (i < 32) {
/* 2315 */         if (i == 10) {
/* 2316 */           this._currInputRow++;
/* 2317 */           this._currInputRowStart = this._inputPtr; continue;
/* 2318 */         }  if (i == 13) {
/* 2319 */           _skipCR(); continue;
/* 2320 */         }  if (i != 9) {
/* 2321 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2325 */     _reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", null);
/*      */     
/* 2327 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipColonFast(int ptr) throws IOException {
/* 2333 */     int i = this._inputBuffer[ptr++];
/* 2334 */     if (i == 58) {
/* 2335 */       i = this._inputBuffer[ptr++];
/* 2336 */       if (i > 32) {
/* 2337 */         if (i != 47 && i != 35) {
/* 2338 */           this._inputPtr = ptr;
/* 2339 */           return i;
/*      */         } 
/* 2341 */       } else if (i == 32 || i == 9) {
/* 2342 */         i = this._inputBuffer[ptr++];
/* 2343 */         if (i > 32 && 
/* 2344 */           i != 47 && i != 35) {
/* 2345 */           this._inputPtr = ptr;
/* 2346 */           return i;
/*      */         } 
/*      */       } 
/*      */       
/* 2350 */       this._inputPtr = ptr - 1;
/* 2351 */       return _skipColon2(true);
/*      */     } 
/* 2353 */     if (i == 32 || i == 9) {
/* 2354 */       i = this._inputBuffer[ptr++];
/*      */     }
/* 2356 */     boolean gotColon = (i == 58);
/* 2357 */     if (gotColon) {
/* 2358 */       i = this._inputBuffer[ptr++];
/* 2359 */       if (i > 32) {
/* 2360 */         if (i != 47 && i != 35) {
/* 2361 */           this._inputPtr = ptr;
/* 2362 */           return i;
/*      */         } 
/* 2364 */       } else if (i == 32 || i == 9) {
/* 2365 */         i = this._inputBuffer[ptr++];
/* 2366 */         if (i > 32 && 
/* 2367 */           i != 47 && i != 35) {
/* 2368 */           this._inputPtr = ptr;
/* 2369 */           return i;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 2374 */     this._inputPtr = ptr - 1;
/* 2375 */     return _skipColon2(gotColon);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipComma(int i) throws IOException {
/* 2381 */     if (i != 44) {
/* 2382 */       _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */     }
/* 2384 */     while (this._inputPtr < this._inputEnd) {
/* 2385 */       i = this._inputBuffer[this._inputPtr++];
/* 2386 */       if (i > 32) {
/* 2387 */         if (i == 47 || i == 35) {
/* 2388 */           this._inputPtr--;
/* 2389 */           return _skipAfterComma2();
/*      */         } 
/* 2391 */         return i;
/*      */       } 
/* 2393 */       if (i < 32) {
/* 2394 */         if (i == 10) {
/* 2395 */           this._currInputRow++;
/* 2396 */           this._currInputRowStart = this._inputPtr; continue;
/* 2397 */         }  if (i == 13) {
/* 2398 */           _skipCR(); continue;
/* 2399 */         }  if (i != 9) {
/* 2400 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2404 */     return _skipAfterComma2();
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipAfterComma2() throws IOException {
/* 2409 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2410 */       int i = this._inputBuffer[this._inputPtr++];
/* 2411 */       if (i > 32) {
/* 2412 */         if (i == 47) {
/* 2413 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 2416 */         if (i == 35 && 
/* 2417 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 2421 */         return i;
/*      */       } 
/* 2423 */       if (i < 32) {
/* 2424 */         if (i == 10) {
/* 2425 */           this._currInputRow++;
/* 2426 */           this._currInputRowStart = this._inputPtr; continue;
/* 2427 */         }  if (i == 13) {
/* 2428 */           _skipCR(); continue;
/* 2429 */         }  if (i != 9) {
/* 2430 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2434 */     throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipWSOrEnd() throws IOException {
/* 2441 */     if (this._inputPtr >= this._inputEnd && 
/* 2442 */       !_loadMore()) {
/* 2443 */       return _eofAsNextChar();
/*      */     }
/*      */     
/* 2446 */     int i = this._inputBuffer[this._inputPtr++];
/* 2447 */     if (i > 32) {
/* 2448 */       if (i == 47 || i == 35) {
/* 2449 */         this._inputPtr--;
/* 2450 */         return _skipWSOrEnd2();
/*      */       } 
/* 2452 */       return i;
/*      */     } 
/* 2454 */     if (i != 32) {
/* 2455 */       if (i == 10) {
/* 2456 */         this._currInputRow++;
/* 2457 */         this._currInputRowStart = this._inputPtr;
/* 2458 */       } else if (i == 13) {
/* 2459 */         _skipCR();
/* 2460 */       } else if (i != 9) {
/* 2461 */         _throwInvalidSpace(i);
/*      */       } 
/*      */     }
/*      */     
/* 2465 */     while (this._inputPtr < this._inputEnd) {
/* 2466 */       i = this._inputBuffer[this._inputPtr++];
/* 2467 */       if (i > 32) {
/* 2468 */         if (i == 47 || i == 35) {
/* 2469 */           this._inputPtr--;
/* 2470 */           return _skipWSOrEnd2();
/*      */         } 
/* 2472 */         return i;
/*      */       } 
/* 2474 */       if (i != 32) {
/* 2475 */         if (i == 10) {
/* 2476 */           this._currInputRow++;
/* 2477 */           this._currInputRowStart = this._inputPtr; continue;
/* 2478 */         }  if (i == 13) {
/* 2479 */           _skipCR(); continue;
/* 2480 */         }  if (i != 9) {
/* 2481 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 2485 */     return _skipWSOrEnd2();
/*      */   }
/*      */ 
/*      */   
/*      */   private int _skipWSOrEnd2() throws IOException {
/*      */     while (true) {
/* 2491 */       if (this._inputPtr >= this._inputEnd && 
/* 2492 */         !_loadMore()) {
/* 2493 */         return _eofAsNextChar();
/*      */       }
/*      */       
/* 2496 */       int i = this._inputBuffer[this._inputPtr++];
/* 2497 */       if (i > 32) {
/* 2498 */         if (i == 47) {
/* 2499 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 2502 */         if (i == 35 && 
/* 2503 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 2507 */         return i;
/* 2508 */       }  if (i != 32) {
/* 2509 */         if (i == 10) {
/* 2510 */           this._currInputRow++;
/* 2511 */           this._currInputRowStart = this._inputPtr; continue;
/* 2512 */         }  if (i == 13) {
/* 2513 */           _skipCR(); continue;
/* 2514 */         }  if (i != 9) {
/* 2515 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void _skipComment() throws IOException {
/* 2523 */     if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
/* 2524 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     
/* 2527 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 2528 */       _reportInvalidEOF(" in a comment", null);
/*      */     }
/* 2530 */     char c = this._inputBuffer[this._inputPtr++];
/* 2531 */     if (c == '/') {
/* 2532 */       _skipLine();
/* 2533 */     } else if (c == '*') {
/* 2534 */       _skipCComment();
/*      */     } else {
/* 2536 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _skipCComment() throws IOException {
/* 2543 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2544 */       int i = this._inputBuffer[this._inputPtr++];
/* 2545 */       if (i <= 42) {
/* 2546 */         if (i == 42) {
/* 2547 */           if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */             break;
/*      */           }
/* 2550 */           if (this._inputBuffer[this._inputPtr] == '/') {
/* 2551 */             this._inputPtr++;
/*      */             return;
/*      */           } 
/*      */           continue;
/*      */         } 
/* 2556 */         if (i < 32) {
/* 2557 */           if (i == 10) {
/* 2558 */             this._currInputRow++;
/* 2559 */             this._currInputRowStart = this._inputPtr; continue;
/* 2560 */           }  if (i == 13) {
/* 2561 */             _skipCR(); continue;
/* 2562 */           }  if (i != 9) {
/* 2563 */             _throwInvalidSpace(i);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 2568 */     _reportInvalidEOF(" in a comment", null);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean _skipYAMLComment() throws IOException {
/* 2573 */     if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
/* 2574 */       return false;
/*      */     }
/* 2576 */     _skipLine();
/* 2577 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _skipLine() throws IOException {
/* 2583 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2584 */       int i = this._inputBuffer[this._inputPtr++];
/* 2585 */       if (i < 32) {
/* 2586 */         if (i == 10) {
/* 2587 */           this._currInputRow++;
/* 2588 */           this._currInputRowStart = this._inputPtr; break;
/*      */         } 
/* 2590 */         if (i == 13) {
/* 2591 */           _skipCR(); break;
/*      */         } 
/* 2593 */         if (i != 9) {
/* 2594 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _decodeEscaped() throws IOException {
/* 2603 */     if (this._inputPtr >= this._inputEnd && 
/* 2604 */       !_loadMore()) {
/* 2605 */       _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */     }
/*      */     
/* 2608 */     char c = this._inputBuffer[this._inputPtr++];
/*      */     
/* 2610 */     switch (c) {
/*      */       
/*      */       case 'b':
/* 2613 */         return '\b';
/*      */       case 't':
/* 2615 */         return '\t';
/*      */       case 'n':
/* 2617 */         return '\n';
/*      */       case 'f':
/* 2619 */         return '\f';
/*      */       case 'r':
/* 2621 */         return '\r';
/*      */ 
/*      */       
/*      */       case '"':
/*      */       case '/':
/*      */       case '\\':
/* 2627 */         return c;
/*      */       
/*      */       case 'u':
/*      */         break;
/*      */       
/*      */       default:
/* 2633 */         return _handleUnrecognizedCharacterEscape(c);
/*      */     } 
/*      */ 
/*      */     
/* 2637 */     int value = 0;
/* 2638 */     for (int i = 0; i < 4; i++) {
/* 2639 */       if (this._inputPtr >= this._inputEnd && 
/* 2640 */         !_loadMore()) {
/* 2641 */         _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */       }
/*      */       
/* 2644 */       int ch = this._inputBuffer[this._inputPtr++];
/* 2645 */       int digit = CharTypes.charToHex(ch);
/* 2646 */       if (digit < 0) {
/* 2647 */         _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
/*      */       }
/* 2649 */       value = value << 4 | digit;
/*      */     } 
/* 2651 */     return (char)value;
/*      */   }
/*      */   
/*      */   private final void _matchTrue() throws IOException {
/* 2655 */     int ptr = this._inputPtr;
/* 2656 */     if (ptr + 3 < this._inputEnd) {
/* 2657 */       char[] b = this._inputBuffer;
/* 2658 */       if (b[ptr] == 'r' && b[++ptr] == 'u' && b[++ptr] == 'e') {
/* 2659 */         char c = b[++ptr];
/* 2660 */         if (c < '0' || c == ']' || c == '}') {
/* 2661 */           this._inputPtr = ptr;
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2667 */     _matchToken("true", 1);
/*      */   }
/*      */   
/*      */   private final void _matchFalse() throws IOException {
/* 2671 */     int ptr = this._inputPtr;
/* 2672 */     if (ptr + 4 < this._inputEnd) {
/* 2673 */       char[] b = this._inputBuffer;
/* 2674 */       if (b[ptr] == 'a' && b[++ptr] == 'l' && b[++ptr] == 's' && b[++ptr] == 'e') {
/* 2675 */         char c = b[++ptr];
/* 2676 */         if (c < '0' || c == ']' || c == '}') {
/* 2677 */           this._inputPtr = ptr;
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2683 */     _matchToken("false", 1);
/*      */   }
/*      */   
/*      */   private final void _matchNull() throws IOException {
/* 2687 */     int ptr = this._inputPtr;
/* 2688 */     if (ptr + 3 < this._inputEnd) {
/* 2689 */       char[] b = this._inputBuffer;
/* 2690 */       if (b[ptr] == 'u' && b[++ptr] == 'l' && b[++ptr] == 'l') {
/* 2691 */         char c = b[++ptr];
/* 2692 */         if (c < '0' || c == ']' || c == '}') {
/* 2693 */           this._inputPtr = ptr;
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2699 */     _matchToken("null", 1);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _matchToken(String matchStr, int i) throws IOException {
/* 2705 */     int len = matchStr.length();
/* 2706 */     if (this._inputPtr + len >= this._inputEnd) {
/* 2707 */       _matchToken2(matchStr, i);
/*      */       
/*      */       return;
/*      */     } 
/*      */     while (true) {
/* 2712 */       if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/* 2713 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2715 */       this._inputPtr++;
/* 2716 */       if (++i >= len) {
/* 2717 */         int ch = this._inputBuffer[this._inputPtr];
/* 2718 */         if (ch >= 48 && ch != 93 && ch != 125)
/* 2719 */           _checkMatchEnd(matchStr, i, ch); 
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   private final void _matchToken2(String matchStr, int i) throws IOException {
/* 2725 */     int len = matchStr.length();
/*      */     do {
/* 2727 */       if ((this._inputPtr >= this._inputEnd && !_loadMore()) || this._inputBuffer[this._inputPtr] != matchStr
/* 2728 */         .charAt(i)) {
/* 2729 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2731 */       this._inputPtr++;
/* 2732 */     } while (++i < len);
/*      */ 
/*      */     
/* 2735 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */       return;
/*      */     }
/* 2738 */     int ch = this._inputBuffer[this._inputPtr];
/* 2739 */     if (ch >= 48 && ch != 93 && ch != 125) {
/* 2740 */       _checkMatchEnd(matchStr, i, ch);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _checkMatchEnd(String matchStr, int i, int c) throws IOException {
/* 2746 */     char ch = (char)c;
/* 2747 */     if (Character.isJavaIdentifierPart(ch)) {
/* 2748 */       _reportInvalidToken(matchStr.substring(0, i));
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
/*      */   protected byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
/* 2772 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2779 */       if (this._inputPtr >= this._inputEnd) {
/* 2780 */         _loadMoreGuaranteed();
/*      */       }
/* 2782 */       char ch = this._inputBuffer[this._inputPtr++];
/* 2783 */       if (ch > ' ') {
/* 2784 */         int bits = b64variant.decodeBase64Char(ch);
/* 2785 */         if (bits < 0) {
/* 2786 */           if (ch == '"') {
/* 2787 */             return builder.toByteArray();
/*      */           }
/* 2789 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 2790 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/* 2794 */         int decodedData = bits;
/*      */ 
/*      */ 
/*      */         
/* 2798 */         if (this._inputPtr >= this._inputEnd) {
/* 2799 */           _loadMoreGuaranteed();
/*      */         }
/* 2801 */         ch = this._inputBuffer[this._inputPtr++];
/* 2802 */         bits = b64variant.decodeBase64Char(ch);
/* 2803 */         if (bits < 0) {
/* 2804 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/* 2806 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/* 2809 */         if (this._inputPtr >= this._inputEnd) {
/* 2810 */           _loadMoreGuaranteed();
/*      */         }
/* 2812 */         ch = this._inputBuffer[this._inputPtr++];
/* 2813 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/* 2816 */         if (bits < 0) {
/* 2817 */           if (bits != -2) {
/*      */             
/* 2819 */             if (ch == '"') {
/* 2820 */               decodedData >>= 4;
/* 2821 */               builder.append(decodedData);
/* 2822 */               if (b64variant.usesPadding()) {
/* 2823 */                 this._inputPtr--;
/* 2824 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/* 2826 */               return builder.toByteArray();
/*      */             } 
/* 2828 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/* 2830 */           if (bits == -2) {
/*      */             
/* 2832 */             if (this._inputPtr >= this._inputEnd) {
/* 2833 */               _loadMoreGuaranteed();
/*      */             }
/* 2835 */             ch = this._inputBuffer[this._inputPtr++];
/* 2836 */             if (!b64variant.usesPaddingChar(ch) && 
/* 2837 */               _decodeBase64Escape(b64variant, ch, 3) != -2) {
/* 2838 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/* 2842 */             decodedData >>= 4;
/* 2843 */             builder.append(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*      */         
/* 2849 */         decodedData = decodedData << 6 | bits;
/*      */         
/* 2851 */         if (this._inputPtr >= this._inputEnd) {
/* 2852 */           _loadMoreGuaranteed();
/*      */         }
/* 2854 */         ch = this._inputBuffer[this._inputPtr++];
/* 2855 */         bits = b64variant.decodeBase64Char(ch);
/* 2856 */         if (bits < 0) {
/* 2857 */           if (bits != -2) {
/*      */             
/* 2859 */             if (ch == '"') {
/* 2860 */               decodedData >>= 2;
/* 2861 */               builder.appendTwoBytes(decodedData);
/* 2862 */               if (b64variant.usesPadding()) {
/* 2863 */                 this._inputPtr--;
/* 2864 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/* 2866 */               return builder.toByteArray();
/*      */             } 
/* 2868 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/* 2870 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2876 */             decodedData >>= 2;
/* 2877 */             builder.appendTwoBytes(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*      */         
/* 2883 */         decodedData = decodedData << 6 | bits;
/* 2884 */         builder.appendThreeBytes(decodedData);
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
/*      */   public JsonLocation getTokenLocation() {
/* 2897 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 2898 */       long total = this._currInputProcessed + this._nameStartOffset - 1L;
/* 2899 */       return new JsonLocation(_contentReference(), -1L, total, this._nameStartRow, this._nameStartCol);
/*      */     } 
/*      */     
/* 2902 */     return new JsonLocation(_contentReference(), -1L, this._tokenInputTotal - 1L, this._tokenInputRow, this._tokenInputCol);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getCurrentLocation() {
/* 2908 */     int col = this._inputPtr - this._currInputRowStart + 1;
/* 2909 */     return new JsonLocation(_contentReference(), -1L, this._currInputProcessed + this._inputPtr, this._currInputRow, col);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _updateLocation() {
/* 2917 */     int ptr = this._inputPtr;
/* 2918 */     this._tokenInputTotal = this._currInputProcessed + ptr;
/* 2919 */     this._tokenInputRow = this._currInputRow;
/* 2920 */     this._tokenInputCol = ptr - this._currInputRowStart;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _updateNameLocation() {
/* 2926 */     int ptr = this._inputPtr;
/* 2927 */     this._nameStartOffset = ptr;
/* 2928 */     this._nameStartRow = this._currInputRow;
/* 2929 */     this._nameStartCol = ptr - this._currInputRowStart;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart) throws IOException {
/* 2939 */     _reportInvalidToken(matchedPart, _validJsonTokenList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart, String msg) throws IOException {
/* 2948 */     StringBuilder sb = new StringBuilder(matchedPart);
/* 2949 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 2950 */       char c = this._inputBuffer[this._inputPtr];
/* 2951 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 2954 */       this._inputPtr++;
/* 2955 */       sb.append(c);
/* 2956 */       if (sb.length() >= 256) {
/* 2957 */         sb.append("...");
/*      */         break;
/*      */       } 
/*      */     } 
/* 2961 */     _reportError("Unrecognized token '%s': was expecting %s", sb, msg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _closeScope(int i) throws JsonParseException {
/* 2971 */     if (i == 93) {
/* 2972 */       _updateLocation();
/* 2973 */       if (!this._parsingContext.inArray()) {
/* 2974 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/* 2976 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 2977 */       this._currToken = JsonToken.END_ARRAY;
/*      */     } 
/* 2979 */     if (i == 125) {
/* 2980 */       _updateLocation();
/* 2981 */       if (!this._parsingContext.inObject()) {
/* 2982 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/* 2984 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 2985 */       this._currToken = JsonToken.END_OBJECT;
/*      */     } 
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/ReaderBasedJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */