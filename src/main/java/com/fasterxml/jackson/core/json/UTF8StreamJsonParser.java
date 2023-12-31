/*      */ package com.fasterxml.jackson.core.json;
/*      */ 
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
/*      */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Writer;
/*      */ 
/*      */ public class UTF8StreamJsonParser extends ParserBase {
/*      */   static final byte BYTE_LF = 10;
/*   24 */   private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
/*      */   
/*   26 */   private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
/*      */   
/*   28 */   private static final int FEAT_MASK_NON_NUM_NUMBERS = JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS.getMask();
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
/*      */   protected ObjectCodec _objectCodec;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final ByteQuadsCanonicalizer _symbols;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   70 */   protected int[] _quadBuffer = new int[16];
/*      */ 
/*      */ 
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
/*      */   
/*      */   private int _quad1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _nameStartOffset;
/*      */ 
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
/*      */   protected InputStream _inputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] _inputBuffer;
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
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public UTF8StreamJsonParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym, byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
/*  164 */     this(ctxt, features, in, codec, sym, inputBuffer, start, end, 0, bufferRecyclable);
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
/*      */   public UTF8StreamJsonParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym, byte[] inputBuffer, int start, int end, int bytesPreProcessed, boolean bufferRecyclable) {
/*  190 */     super(ctxt, features);
/*  191 */     this._inputStream = in;
/*  192 */     this._objectCodec = codec;
/*  193 */     this._symbols = sym;
/*  194 */     this._inputBuffer = inputBuffer;
/*  195 */     this._inputPtr = start;
/*  196 */     this._inputEnd = end;
/*  197 */     this._currInputRowStart = start - bytesPreProcessed;
/*      */     
/*  199 */     this._currInputProcessed = (-start + bytesPreProcessed);
/*  200 */     this._bufferRecyclable = bufferRecyclable;
/*      */   }
/*      */ 
/*      */   
/*      */   public ObjectCodec getCodec() {
/*  205 */     return this._objectCodec;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCodec(ObjectCodec c) {
/*  210 */     this._objectCodec = c;
/*      */   }
/*      */ 
/*      */   
/*      */   public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
/*  215 */     return JSON_READ_CAPABILITIES;
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
/*      */   public int releaseBuffered(OutputStream out) throws IOException {
/*  227 */     int count = this._inputEnd - this._inputPtr;
/*  228 */     if (count < 1) {
/*  229 */       return 0;
/*      */     }
/*      */     
/*  232 */     int origPtr = this._inputPtr;
/*  233 */     this._inputPtr += count;
/*  234 */     out.write(this._inputBuffer, origPtr, count);
/*  235 */     return count;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getInputSource() {
/*  240 */     return this._inputStream;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _loadMore() throws IOException {
/*  251 */     if (this._inputStream != null) {
/*  252 */       int space = this._inputBuffer.length;
/*  253 */       if (space == 0) {
/*  254 */         return false;
/*      */       }
/*      */       
/*  257 */       int count = this._inputStream.read(this._inputBuffer, 0, space);
/*  258 */       if (count > 0) {
/*  259 */         int bufSize = this._inputEnd;
/*      */         
/*  261 */         this._currInputProcessed += bufSize;
/*  262 */         this._currInputRowStart -= bufSize;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  267 */         this._nameStartOffset -= bufSize;
/*      */         
/*  269 */         this._inputPtr = 0;
/*  270 */         this._inputEnd = count;
/*      */         
/*  272 */         return true;
/*      */       } 
/*      */       
/*  275 */       _closeInput();
/*      */       
/*  277 */       if (count == 0) {
/*  278 */         throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
/*      */       }
/*      */     } 
/*  281 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _closeInput() throws IOException {
/*  289 */     if (this._inputStream != null) {
/*  290 */       if (this._ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
/*  291 */         this._inputStream.close();
/*      */       }
/*  293 */       this._inputStream = null;
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
/*  306 */     super._releaseBuffers();
/*      */     
/*  308 */     this._symbols.release();
/*  309 */     if (this._bufferRecyclable) {
/*  310 */       byte[] buf = this._inputBuffer;
/*  311 */       if (buf != null)
/*      */       {
/*      */         
/*  314 */         if (buf != NO_BYTES) {
/*  315 */           this._inputBuffer = NO_BYTES;
/*  316 */           this._ioContext.releaseReadIOBuffer(buf);
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
/*      */   public String getText() throws IOException {
/*  331 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  332 */       if (this._tokenIncomplete) {
/*  333 */         this._tokenIncomplete = false;
/*  334 */         return _finishAndReturnString();
/*      */       } 
/*  336 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  338 */     return _getText2(this._currToken);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getText(Writer writer) throws IOException {
/*  344 */     JsonToken t = this._currToken;
/*  345 */     if (t == JsonToken.VALUE_STRING) {
/*  346 */       if (this._tokenIncomplete) {
/*  347 */         this._tokenIncomplete = false;
/*  348 */         _finishString();
/*      */       } 
/*  350 */       return this._textBuffer.contentsToWriter(writer);
/*      */     } 
/*  352 */     if (t == JsonToken.FIELD_NAME) {
/*  353 */       String n = this._parsingContext.getCurrentName();
/*  354 */       writer.write(n);
/*  355 */       return n.length();
/*      */     } 
/*  357 */     if (t != null) {
/*  358 */       if (t.isNumeric()) {
/*  359 */         return this._textBuffer.contentsToWriter(writer);
/*      */       }
/*  361 */       char[] ch = t.asCharArray();
/*  362 */       writer.write(ch);
/*  363 */       return ch.length;
/*      */     } 
/*  365 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getValueAsString() throws IOException {
/*  374 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  375 */       if (this._tokenIncomplete) {
/*  376 */         this._tokenIncomplete = false;
/*  377 */         return _finishAndReturnString();
/*      */       } 
/*  379 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  381 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  382 */       return getCurrentName();
/*      */     }
/*  384 */     return super.getValueAsString(null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getValueAsString(String defValue) throws IOException {
/*  391 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  392 */       if (this._tokenIncomplete) {
/*  393 */         this._tokenIncomplete = false;
/*  394 */         return _finishAndReturnString();
/*      */       } 
/*  396 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  398 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  399 */       return getCurrentName();
/*      */     }
/*  401 */     return super.getValueAsString(defValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getValueAsInt() throws IOException {
/*  408 */     JsonToken t = this._currToken;
/*  409 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/*      */       
/*  411 */       if ((this._numTypesValid & 0x1) == 0) {
/*  412 */         if (this._numTypesValid == 0) {
/*  413 */           return _parseIntValue();
/*      */         }
/*  415 */         if ((this._numTypesValid & 0x1) == 0) {
/*  416 */           convertNumberToInt();
/*      */         }
/*      */       } 
/*  419 */       return this._numberInt;
/*      */     } 
/*  421 */     return super.getValueAsInt(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getValueAsInt(int defValue) throws IOException {
/*  428 */     JsonToken t = this._currToken;
/*  429 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/*      */       
/*  431 */       if ((this._numTypesValid & 0x1) == 0) {
/*  432 */         if (this._numTypesValid == 0) {
/*  433 */           return _parseIntValue();
/*      */         }
/*  435 */         if ((this._numTypesValid & 0x1) == 0) {
/*  436 */           convertNumberToInt();
/*      */         }
/*      */       } 
/*  439 */       return this._numberInt;
/*      */     } 
/*  441 */     return super.getValueAsInt(defValue);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final String _getText2(JsonToken t) {
/*  446 */     if (t == null) {
/*  447 */       return null;
/*      */     }
/*  449 */     switch (t.id()) {
/*      */       case 5:
/*  451 */         return this._parsingContext.getCurrentName();
/*      */ 
/*      */       
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  457 */         return this._textBuffer.contentsAsString();
/*      */     } 
/*  459 */     return t.asString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public char[] getTextCharacters() throws IOException {
/*  466 */     if (this._currToken != null) {
/*  467 */       switch (this._currToken.id()) {
/*      */         
/*      */         case 5:
/*  470 */           if (!this._nameCopied) {
/*  471 */             String name = this._parsingContext.getCurrentName();
/*  472 */             int nameLen = name.length();
/*  473 */             if (this._nameCopyBuffer == null) {
/*  474 */               this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  475 */             } else if (this._nameCopyBuffer.length < nameLen) {
/*  476 */               this._nameCopyBuffer = new char[nameLen];
/*      */             } 
/*  478 */             name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  479 */             this._nameCopied = true;
/*      */           } 
/*  481 */           return this._nameCopyBuffer;
/*      */         
/*      */         case 6:
/*  484 */           if (this._tokenIncomplete) {
/*  485 */             this._tokenIncomplete = false;
/*  486 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  491 */           return this._textBuffer.getTextBuffer();
/*      */       } 
/*      */       
/*  494 */       return this._currToken.asCharArray();
/*      */     } 
/*      */     
/*  497 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTextLength() throws IOException {
/*  503 */     if (this._currToken != null) {
/*  504 */       switch (this._currToken.id()) {
/*      */         
/*      */         case 5:
/*  507 */           return this._parsingContext.getCurrentName().length();
/*      */         case 6:
/*  509 */           if (this._tokenIncomplete) {
/*  510 */             this._tokenIncomplete = false;
/*  511 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  516 */           return this._textBuffer.size();
/*      */       } 
/*      */       
/*  519 */       return (this._currToken.asCharArray()).length;
/*      */     } 
/*      */     
/*  522 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTextOffset() throws IOException {
/*  529 */     if (this._currToken != null) {
/*  530 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  532 */           return 0;
/*      */         case 6:
/*  534 */           if (this._tokenIncomplete) {
/*  535 */             this._tokenIncomplete = false;
/*  536 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  541 */           return this._textBuffer.getTextOffset();
/*      */       } 
/*      */     
/*      */     }
/*  545 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/*  551 */     if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null))
/*      */     {
/*  553 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */     
/*  556 */     if (this._tokenIncomplete) {
/*      */       try {
/*  558 */         this._binaryValue = _decodeBase64(b64variant);
/*  559 */       } catch (IllegalArgumentException iae) {
/*  560 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       } 
/*      */       
/*  563 */       this._tokenIncomplete = false;
/*      */     }
/*  565 */     else if (this._binaryValue == null) {
/*      */       
/*  567 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  568 */       _decodeBase64(getText(), builder, b64variant);
/*  569 */       this._binaryValue = builder.toByteArray();
/*      */     } 
/*      */     
/*  572 */     return this._binaryValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/*  579 */     if (!this._tokenIncomplete || this._currToken != JsonToken.VALUE_STRING) {
/*  580 */       byte[] b = getBinaryValue(b64variant);
/*  581 */       out.write(b);
/*  582 */       return b.length;
/*      */     } 
/*      */     
/*  585 */     byte[] buf = this._ioContext.allocBase64Buffer();
/*      */     try {
/*  587 */       return _readBinary(b64variant, out, buf);
/*      */     } finally {
/*  589 */       this._ioContext.releaseBase64Buffer(buf);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
/*  596 */     int outputPtr = 0;
/*  597 */     int outputEnd = buffer.length - 3;
/*  598 */     int outputCount = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  604 */       if (this._inputPtr >= this._inputEnd) {
/*  605 */         _loadMoreGuaranteed();
/*      */       }
/*  607 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  608 */       if (ch > 32) {
/*  609 */         int bits = b64variant.decodeBase64Char(ch);
/*  610 */         if (bits < 0) {
/*  611 */           if (ch == 34) {
/*      */             break;
/*      */           }
/*  614 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/*  615 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  621 */         if (outputPtr > outputEnd) {
/*  622 */           outputCount += outputPtr;
/*  623 */           out.write(buffer, 0, outputPtr);
/*  624 */           outputPtr = 0;
/*      */         } 
/*      */         
/*  627 */         int decodedData = bits;
/*      */ 
/*      */ 
/*      */         
/*  631 */         if (this._inputPtr >= this._inputEnd) {
/*  632 */           _loadMoreGuaranteed();
/*      */         }
/*  634 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  635 */         bits = b64variant.decodeBase64Char(ch);
/*  636 */         if (bits < 0) {
/*  637 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/*  639 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/*  642 */         if (this._inputPtr >= this._inputEnd) {
/*  643 */           _loadMoreGuaranteed();
/*      */         }
/*  645 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  646 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/*  649 */         if (bits < 0) {
/*  650 */           if (bits != -2) {
/*      */             
/*  652 */             if (ch == 34) {
/*  653 */               decodedData >>= 4;
/*  654 */               buffer[outputPtr++] = (byte)decodedData;
/*  655 */               if (b64variant.usesPadding()) {
/*  656 */                 this._inputPtr--;
/*  657 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/*      */               break;
/*      */             } 
/*  661 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/*  663 */           if (bits == -2) {
/*      */             
/*  665 */             if (this._inputPtr >= this._inputEnd) {
/*  666 */               _loadMoreGuaranteed();
/*      */             }
/*  668 */             ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  669 */             if (!b64variant.usesPaddingChar(ch) && 
/*  670 */               _decodeBase64Escape(b64variant, ch, 3) != -2) {
/*  671 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/*  675 */             decodedData >>= 4;
/*  676 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  681 */         decodedData = decodedData << 6 | bits;
/*      */         
/*  683 */         if (this._inputPtr >= this._inputEnd) {
/*  684 */           _loadMoreGuaranteed();
/*      */         }
/*  686 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*  687 */         bits = b64variant.decodeBase64Char(ch);
/*  688 */         if (bits < 0) {
/*  689 */           if (bits != -2) {
/*      */             
/*  691 */             if (ch == 34) {
/*  692 */               decodedData >>= 2;
/*  693 */               buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  694 */               buffer[outputPtr++] = (byte)decodedData;
/*  695 */               if (b64variant.usesPadding()) {
/*  696 */                 this._inputPtr--;
/*  697 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/*      */               break;
/*      */             } 
/*  701 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/*  703 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  710 */             decodedData >>= 2;
/*  711 */             buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  712 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  717 */         decodedData = decodedData << 6 | bits;
/*  718 */         buffer[outputPtr++] = (byte)(decodedData >> 16);
/*  719 */         buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  720 */         buffer[outputPtr++] = (byte)decodedData;
/*      */       } 
/*  722 */     }  this._tokenIncomplete = false;
/*  723 */     if (outputPtr > 0) {
/*  724 */       outputCount += outputPtr;
/*  725 */       out.write(buffer, 0, outputPtr);
/*      */     } 
/*  727 */     return outputCount;
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
/*      */   public JsonToken nextToken() throws IOException {
/*  747 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  748 */       return _nextAfterName();
/*      */     }
/*      */ 
/*      */     
/*  752 */     this._numTypesValid = 0;
/*  753 */     if (this._tokenIncomplete) {
/*  754 */       _skipString();
/*      */     }
/*  756 */     int i = _skipWSOrEnd();
/*  757 */     if (i < 0) {
/*      */       
/*  759 */       close();
/*  760 */       return this._currToken = null;
/*      */     } 
/*      */     
/*  763 */     this._binaryValue = null;
/*      */ 
/*      */     
/*  766 */     if (i == 93) {
/*  767 */       _closeArrayScope();
/*  768 */       return this._currToken = JsonToken.END_ARRAY;
/*      */     } 
/*  770 */     if (i == 125) {
/*  771 */       _closeObjectScope();
/*  772 */       return this._currToken = JsonToken.END_OBJECT;
/*      */     } 
/*      */ 
/*      */     
/*  776 */     if (this._parsingContext.expectComma()) {
/*  777 */       if (i != 44) {
/*  778 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  780 */       i = _skipWS();
/*      */       
/*  782 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  783 */         i == 93 || i == 125)) {
/*  784 */         return _closeScope(i);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  792 */     if (!this._parsingContext.inObject()) {
/*  793 */       _updateLocation();
/*  794 */       return _nextTokenNotInObject(i);
/*      */     } 
/*      */     
/*  797 */     _updateNameLocation();
/*  798 */     String n = _parseName(i);
/*  799 */     this._parsingContext.setCurrentName(n);
/*  800 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/*  802 */     i = _skipColon();
/*  803 */     _updateLocation();
/*      */ 
/*      */     
/*  806 */     if (i == 34) {
/*  807 */       this._tokenIncomplete = true;
/*  808 */       this._nextToken = JsonToken.VALUE_STRING;
/*  809 */       return this._currToken;
/*      */     } 
/*      */ 
/*      */     
/*  813 */     switch (i)
/*      */     { case 45:
/*  815 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  857 */         this._nextToken = t;
/*  858 */         return this._currToken;case 46: t = _parseFloatThatStartsWithPeriod(); this._nextToken = t; return this._currToken;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return this._currToken;case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return this._currToken;case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return this._currToken;case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return this._currToken;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return this._currToken;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return this._currToken; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return this._currToken;
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextTokenNotInObject(int i) throws IOException {
/*  863 */     if (i == 34) {
/*  864 */       this._tokenIncomplete = true;
/*  865 */       return this._currToken = JsonToken.VALUE_STRING;
/*      */     } 
/*  867 */     switch (i) {
/*      */       case 91:
/*  869 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  870 */         return this._currToken = JsonToken.START_ARRAY;
/*      */       case 123:
/*  872 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*  873 */         return this._currToken = JsonToken.START_OBJECT;
/*      */       case 116:
/*  875 */         _matchTrue();
/*  876 */         return this._currToken = JsonToken.VALUE_TRUE;
/*      */       case 102:
/*  878 */         _matchFalse();
/*  879 */         return this._currToken = JsonToken.VALUE_FALSE;
/*      */       case 110:
/*  881 */         _matchNull();
/*  882 */         return this._currToken = JsonToken.VALUE_NULL;
/*      */       case 45:
/*  884 */         return this._currToken = _parseNegNumber();
/*      */ 
/*      */ 
/*      */       
/*      */       case 46:
/*  889 */         return this._currToken = _parseFloatThatStartsWithPeriod();
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
/*  900 */         return this._currToken = _parsePosNumber(i);
/*      */     } 
/*  902 */     return this._currToken = _handleUnexpectedValue(i);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextAfterName() {
/*  907 */     this._nameCopied = false;
/*  908 */     JsonToken t = this._nextToken;
/*  909 */     this._nextToken = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  914 */     if (t == JsonToken.START_ARRAY) {
/*  915 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  916 */     } else if (t == JsonToken.START_OBJECT) {
/*  917 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     } 
/*  919 */     return this._currToken = t;
/*      */   }
/*      */ 
/*      */   
/*      */   public void finishToken() throws IOException {
/*  924 */     if (this._tokenIncomplete) {
/*  925 */       this._tokenIncomplete = false;
/*  926 */       _finishString();
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
/*      */   public boolean nextFieldName(SerializableString str) throws IOException {
/*  940 */     this._numTypesValid = 0;
/*  941 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  942 */       _nextAfterName();
/*  943 */       return false;
/*      */     } 
/*  945 */     if (this._tokenIncomplete) {
/*  946 */       _skipString();
/*      */     }
/*  948 */     int i = _skipWSOrEnd();
/*  949 */     if (i < 0) {
/*  950 */       close();
/*  951 */       this._currToken = null;
/*  952 */       return false;
/*      */     } 
/*  954 */     this._binaryValue = null;
/*      */ 
/*      */     
/*  957 */     if (i == 93) {
/*  958 */       _closeArrayScope();
/*  959 */       this._currToken = JsonToken.END_ARRAY;
/*  960 */       return false;
/*      */     } 
/*  962 */     if (i == 125) {
/*  963 */       _closeObjectScope();
/*  964 */       this._currToken = JsonToken.END_OBJECT;
/*  965 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  969 */     if (this._parsingContext.expectComma()) {
/*  970 */       if (i != 44) {
/*  971 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  973 */       i = _skipWS();
/*      */ 
/*      */       
/*  976 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  977 */         i == 93 || i == 125)) {
/*  978 */         _closeScope(i);
/*  979 */         return false;
/*      */       } 
/*      */     } 
/*      */     
/*  983 */     if (!this._parsingContext.inObject()) {
/*  984 */       _updateLocation();
/*  985 */       _nextTokenNotInObject(i);
/*  986 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  990 */     _updateNameLocation();
/*  991 */     if (i == 34) {
/*      */       
/*  993 */       byte[] nameBytes = str.asQuotedUTF8();
/*  994 */       int len = nameBytes.length;
/*      */ 
/*      */       
/*  997 */       if (this._inputPtr + len + 4 < this._inputEnd) {
/*      */         
/*  999 */         int end = this._inputPtr + len;
/* 1000 */         if (this._inputBuffer[end] == 34) {
/* 1001 */           int offset = 0;
/* 1002 */           int ptr = this._inputPtr;
/*      */           while (true) {
/* 1004 */             if (ptr == end) {
/* 1005 */               this._parsingContext.setCurrentName(str.getValue());
/* 1006 */               i = _skipColonFast(ptr + 1);
/* 1007 */               _isNextTokenNameYes(i);
/* 1008 */               return true;
/*      */             } 
/* 1010 */             if (nameBytes[offset] != this._inputBuffer[ptr]) {
/*      */               break;
/*      */             }
/* 1013 */             offset++;
/* 1014 */             ptr++;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 1019 */     return _isNextTokenNameMaybe(i, str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextFieldName() throws IOException {
/* 1026 */     this._numTypesValid = 0;
/* 1027 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1028 */       _nextAfterName();
/* 1029 */       return null;
/*      */     } 
/* 1031 */     if (this._tokenIncomplete) {
/* 1032 */       _skipString();
/*      */     }
/* 1034 */     int i = _skipWSOrEnd();
/* 1035 */     if (i < 0) {
/* 1036 */       close();
/* 1037 */       this._currToken = null;
/* 1038 */       return null;
/*      */     } 
/* 1040 */     this._binaryValue = null;
/*      */     
/* 1042 */     if (i == 93) {
/* 1043 */       _closeArrayScope();
/* 1044 */       this._currToken = JsonToken.END_ARRAY;
/* 1045 */       return null;
/*      */     } 
/* 1047 */     if (i == 125) {
/* 1048 */       _closeObjectScope();
/* 1049 */       this._currToken = JsonToken.END_OBJECT;
/* 1050 */       return null;
/*      */     } 
/*      */ 
/*      */     
/* 1054 */     if (this._parsingContext.expectComma()) {
/* 1055 */       if (i != 44) {
/* 1056 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/* 1058 */       i = _skipWS();
/*      */       
/* 1060 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/* 1061 */         i == 93 || i == 125)) {
/* 1062 */         _closeScope(i);
/* 1063 */         return null;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1068 */     if (!this._parsingContext.inObject()) {
/* 1069 */       _updateLocation();
/* 1070 */       _nextTokenNotInObject(i);
/* 1071 */       return null;
/*      */     } 
/*      */     
/* 1074 */     _updateNameLocation();
/* 1075 */     String nameStr = _parseName(i);
/* 1076 */     this._parsingContext.setCurrentName(nameStr);
/* 1077 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/* 1079 */     i = _skipColon();
/* 1080 */     _updateLocation();
/* 1081 */     if (i == 34) {
/* 1082 */       this._tokenIncomplete = true;
/* 1083 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1084 */       return nameStr;
/*      */     } 
/*      */     
/* 1087 */     switch (i)
/*      */     { case 45:
/* 1089 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1128 */         this._nextToken = t;
/* 1129 */         return nameStr;case 46: t = _parseFloatThatStartsWithPeriod(); this._nextToken = t; return nameStr;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return nameStr;case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return nameStr;case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return nameStr;case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return nameStr;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return nameStr;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return nameStr; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return nameStr;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipColonFast(int ptr) throws IOException {
/* 1135 */     int i = this._inputBuffer[ptr++];
/* 1136 */     if (i == 58) {
/* 1137 */       i = this._inputBuffer[ptr++];
/* 1138 */       if (i > 32) {
/* 1139 */         if (i != 47 && i != 35) {
/* 1140 */           this._inputPtr = ptr;
/* 1141 */           return i;
/*      */         } 
/* 1143 */       } else if (i == 32 || i == 9) {
/* 1144 */         i = this._inputBuffer[ptr++];
/* 1145 */         if (i > 32 && 
/* 1146 */           i != 47 && i != 35) {
/* 1147 */           this._inputPtr = ptr;
/* 1148 */           return i;
/*      */         } 
/*      */       } 
/*      */       
/* 1152 */       this._inputPtr = ptr - 1;
/* 1153 */       return _skipColon2(true);
/*      */     } 
/* 1155 */     if (i == 32 || i == 9) {
/* 1156 */       i = this._inputBuffer[ptr++];
/*      */     }
/* 1158 */     if (i == 58) {
/* 1159 */       i = this._inputBuffer[ptr++];
/* 1160 */       if (i > 32) {
/* 1161 */         if (i != 47 && i != 35) {
/* 1162 */           this._inputPtr = ptr;
/* 1163 */           return i;
/*      */         } 
/* 1165 */       } else if (i == 32 || i == 9) {
/* 1166 */         i = this._inputBuffer[ptr++];
/* 1167 */         if (i > 32 && 
/* 1168 */           i != 47 && i != 35) {
/* 1169 */           this._inputPtr = ptr;
/* 1170 */           return i;
/*      */         } 
/*      */       } 
/*      */       
/* 1174 */       this._inputPtr = ptr - 1;
/* 1175 */       return _skipColon2(true);
/*      */     } 
/* 1177 */     this._inputPtr = ptr - 1;
/* 1178 */     return _skipColon2(false);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _isNextTokenNameYes(int i) throws IOException {
/* 1183 */     this._currToken = JsonToken.FIELD_NAME;
/* 1184 */     _updateLocation();
/*      */     
/* 1186 */     switch (i) {
/*      */       case 34:
/* 1188 */         this._tokenIncomplete = true;
/* 1189 */         this._nextToken = JsonToken.VALUE_STRING;
/*      */         return;
/*      */       case 91:
/* 1192 */         this._nextToken = JsonToken.START_ARRAY;
/*      */         return;
/*      */       case 123:
/* 1195 */         this._nextToken = JsonToken.START_OBJECT;
/*      */         return;
/*      */       case 116:
/* 1198 */         _matchTrue();
/* 1199 */         this._nextToken = JsonToken.VALUE_TRUE;
/*      */         return;
/*      */       case 102:
/* 1202 */         _matchFalse();
/* 1203 */         this._nextToken = JsonToken.VALUE_FALSE;
/*      */         return;
/*      */       case 110:
/* 1206 */         _matchNull();
/* 1207 */         this._nextToken = JsonToken.VALUE_NULL;
/*      */         return;
/*      */       case 45:
/* 1210 */         this._nextToken = _parseNegNumber();
/*      */         return;
/*      */       case 46:
/* 1213 */         this._nextToken = _parseFloatThatStartsWithPeriod();
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
/* 1225 */         this._nextToken = _parsePosNumber(i);
/*      */         return;
/*      */     } 
/* 1228 */     this._nextToken = _handleUnexpectedValue(i);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean _isNextTokenNameMaybe(int i, SerializableString str) throws IOException {
/* 1235 */     String n = _parseName(i);
/* 1236 */     this._parsingContext.setCurrentName(n);
/* 1237 */     boolean match = n.equals(str.getValue());
/* 1238 */     this._currToken = JsonToken.FIELD_NAME;
/* 1239 */     i = _skipColon();
/* 1240 */     _updateLocation();
/*      */ 
/*      */     
/* 1243 */     if (i == 34) {
/* 1244 */       this._tokenIncomplete = true;
/* 1245 */       this._nextToken = JsonToken.VALUE_STRING;
/* 1246 */       return match;
/*      */     } 
/*      */ 
/*      */     
/* 1250 */     switch (i)
/*      */     { case 91:
/* 1252 */         t = JsonToken.START_ARRAY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1290 */         this._nextToken = t;
/* 1291 */         return match;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return match;case 116: _matchTrue(); t = JsonToken.VALUE_TRUE; this._nextToken = t; return match;case 102: _matchFalse(); t = JsonToken.VALUE_FALSE; this._nextToken = t; return match;case 110: _matchNull(); t = JsonToken.VALUE_NULL; this._nextToken = t; return match;case 45: t = _parseNegNumber(); this._nextToken = t; return match;case 46: t = _parseFloatThatStartsWithPeriod(); this._nextToken = t; return match;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return match; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return match;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextTextValue() throws IOException {
/* 1298 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1299 */       this._nameCopied = false;
/* 1300 */       JsonToken t = this._nextToken;
/* 1301 */       this._nextToken = null;
/* 1302 */       this._currToken = t;
/* 1303 */       if (t == JsonToken.VALUE_STRING) {
/* 1304 */         if (this._tokenIncomplete) {
/* 1305 */           this._tokenIncomplete = false;
/* 1306 */           return _finishAndReturnString();
/*      */         } 
/* 1308 */         return this._textBuffer.contentsAsString();
/*      */       } 
/* 1310 */       if (t == JsonToken.START_ARRAY) {
/* 1311 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1312 */       } else if (t == JsonToken.START_OBJECT) {
/* 1313 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1315 */       return null;
/*      */     } 
/*      */     
/* 1318 */     return (nextToken() == JsonToken.VALUE_STRING) ? getText() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int nextIntValue(int defaultValue) throws IOException {
/* 1325 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1326 */       this._nameCopied = false;
/* 1327 */       JsonToken t = this._nextToken;
/* 1328 */       this._nextToken = null;
/* 1329 */       this._currToken = t;
/* 1330 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1331 */         return getIntValue();
/*      */       }
/* 1333 */       if (t == JsonToken.START_ARRAY) {
/* 1334 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1335 */       } else if (t == JsonToken.START_OBJECT) {
/* 1336 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1338 */       return defaultValue;
/*      */     } 
/*      */     
/* 1341 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getIntValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long nextLongValue(long defaultValue) throws IOException {
/* 1348 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1349 */       this._nameCopied = false;
/* 1350 */       JsonToken t = this._nextToken;
/* 1351 */       this._nextToken = null;
/* 1352 */       this._currToken = t;
/* 1353 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 1354 */         return getLongValue();
/*      */       }
/* 1356 */       if (t == JsonToken.START_ARRAY) {
/* 1357 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1358 */       } else if (t == JsonToken.START_OBJECT) {
/* 1359 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1361 */       return defaultValue;
/*      */     } 
/*      */     
/* 1364 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getLongValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Boolean nextBooleanValue() throws IOException {
/* 1371 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 1372 */       this._nameCopied = false;
/* 1373 */       JsonToken jsonToken = this._nextToken;
/* 1374 */       this._nextToken = null;
/* 1375 */       this._currToken = jsonToken;
/* 1376 */       if (jsonToken == JsonToken.VALUE_TRUE) {
/* 1377 */         return Boolean.TRUE;
/*      */       }
/* 1379 */       if (jsonToken == JsonToken.VALUE_FALSE) {
/* 1380 */         return Boolean.FALSE;
/*      */       }
/* 1382 */       if (jsonToken == JsonToken.START_ARRAY) {
/* 1383 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 1384 */       } else if (jsonToken == JsonToken.START_OBJECT) {
/* 1385 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/* 1387 */       return null;
/*      */     } 
/*      */     
/* 1390 */     JsonToken t = nextToken();
/* 1391 */     if (t == JsonToken.VALUE_TRUE) {
/* 1392 */       return Boolean.TRUE;
/*      */     }
/* 1394 */     if (t == JsonToken.VALUE_FALSE) {
/* 1395 */       return Boolean.FALSE;
/*      */     }
/* 1397 */     return null;
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
/* 1410 */     if (!isEnabled(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
/* 1411 */       return _handleUnexpectedValue(46);
/*      */     }
/* 1413 */     return _parseFloat(this._textBuffer.emptyAndGetCurrentSegment(), 0, 46, false, 0);
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
/*      */   protected JsonToken _parsePosNumber(int c) throws IOException {
/* 1442 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */     
/* 1444 */     if (c == 48) {
/* 1445 */       c = _verifyNoLeadingZeroes();
/*      */     }
/*      */     
/* 1448 */     outBuf[0] = (char)c;
/* 1449 */     int intLen = 1;
/* 1450 */     int outPtr = 1;
/*      */ 
/*      */     
/* 1453 */     int end = Math.min(this._inputEnd, this._inputPtr + outBuf.length - 1);
/*      */     
/*      */     while (true) {
/* 1456 */       if (this._inputPtr >= end) {
/* 1457 */         return _parseNumber2(outBuf, outPtr, false, intLen);
/*      */       }
/* 1459 */       c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1460 */       if (c < 48 || c > 57) {
/*      */         break;
/*      */       }
/* 1463 */       intLen++;
/* 1464 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 1466 */     if (c == 46 || c == 101 || c == 69) {
/* 1467 */       return _parseFloat(outBuf, outPtr, c, false, intLen);
/*      */     }
/* 1469 */     this._inputPtr--;
/* 1470 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1472 */     if (this._parsingContext.inRoot()) {
/* 1473 */       _verifyRootSpace(c);
/*      */     }
/*      */     
/* 1476 */     return resetInt(false, intLen);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _parseNegNumber() throws IOException {
/* 1481 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1482 */     int outPtr = 0;
/*      */ 
/*      */     
/* 1485 */     outBuf[outPtr++] = '-';
/*      */     
/* 1487 */     if (this._inputPtr >= this._inputEnd) {
/* 1488 */       _loadMoreGuaranteed();
/*      */     }
/* 1490 */     int c = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     
/* 1492 */     if (c <= 48) {
/*      */       
/* 1494 */       if (c != 48) {
/* 1495 */         return _handleInvalidNumberStart(c, true);
/*      */       }
/* 1497 */       c = _verifyNoLeadingZeroes();
/* 1498 */     } else if (c > 57) {
/* 1499 */       return _handleInvalidNumberStart(c, true);
/*      */     } 
/*      */ 
/*      */     
/* 1503 */     outBuf[outPtr++] = (char)c;
/* 1504 */     int intLen = 1;
/*      */ 
/*      */ 
/*      */     
/* 1508 */     int end = Math.min(this._inputEnd, this._inputPtr + outBuf.length - outPtr);
/*      */     
/*      */     while (true) {
/* 1511 */       if (this._inputPtr >= end)
/*      */       {
/* 1513 */         return _parseNumber2(outBuf, outPtr, true, intLen);
/*      */       }
/* 1515 */       c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1516 */       if (c < 48 || c > 57) {
/*      */         break;
/*      */       }
/* 1519 */       intLen++;
/* 1520 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 1522 */     if (c == 46 || c == 101 || c == 69) {
/* 1523 */       return _parseFloat(outBuf, outPtr, c, true, intLen);
/*      */     }
/*      */     
/* 1526 */     this._inputPtr--;
/* 1527 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1529 */     if (this._parsingContext.inRoot()) {
/* 1530 */       _verifyRootSpace(c);
/*      */     }
/*      */ 
/*      */     
/* 1534 */     return resetInt(true, intLen);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _parseNumber2(char[] outBuf, int outPtr, boolean negative, int intPartLength) throws IOException {
/*      */     while (true) {
/* 1544 */       if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1545 */         this._textBuffer.setCurrentLength(outPtr);
/* 1546 */         return resetInt(negative, intPartLength);
/*      */       } 
/* 1548 */       int c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1549 */       if (c > 57 || c < 48) {
/* 1550 */         if (c == 46 || c == 101 || c == 69) {
/* 1551 */           return _parseFloat(outBuf, outPtr, c, negative, intPartLength);
/*      */         }
/*      */         break;
/*      */       } 
/* 1555 */       if (outPtr >= outBuf.length) {
/* 1556 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1557 */         outPtr = 0;
/*      */       } 
/* 1559 */       outBuf[outPtr++] = (char)c;
/* 1560 */       intPartLength++;
/*      */     } 
/* 1562 */     this._inputPtr--;
/* 1563 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1565 */     if (this._parsingContext.inRoot()) {
/* 1566 */       _verifyRootSpace(this._inputBuffer[this._inputPtr] & 0xFF);
/*      */     }
/*      */ 
/*      */     
/* 1570 */     return resetInt(negative, intPartLength);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _verifyNoLeadingZeroes() throws IOException {
/* 1579 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1580 */       return 48;
/*      */     }
/* 1582 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     
/* 1584 */     if (ch < 48 || ch > 57) {
/* 1585 */       return 48;
/*      */     }
/*      */     
/* 1588 */     if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1589 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/* 1592 */     this._inputPtr++;
/* 1593 */     if (ch == 48) {
/* 1594 */       while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 1595 */         ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 1596 */         if (ch < 48 || ch > 57) {
/* 1597 */           return 48;
/*      */         }
/* 1599 */         this._inputPtr++;
/* 1600 */         if (ch != 48) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     }
/* 1605 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException {
/* 1611 */     int fractLen = 0;
/* 1612 */     boolean eof = false;
/*      */ 
/*      */     
/* 1615 */     if (c == 46) {
/* 1616 */       if (outPtr >= outBuf.length) {
/* 1617 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1618 */         outPtr = 0;
/*      */       } 
/* 1620 */       outBuf[outPtr++] = (char)c;
/*      */ 
/*      */       
/*      */       while (true) {
/* 1624 */         if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1625 */           eof = true;
/*      */           break;
/*      */         } 
/* 1628 */         c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1629 */         if (c < 48 || c > 57) {
/*      */           break;
/*      */         }
/* 1632 */         fractLen++;
/* 1633 */         if (outPtr >= outBuf.length) {
/* 1634 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1635 */           outPtr = 0;
/*      */         } 
/* 1637 */         outBuf[outPtr++] = (char)c;
/*      */       } 
/*      */       
/* 1640 */       if (fractLen == 0) {
/* 1641 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1645 */     int expLen = 0;
/* 1646 */     if (c == 101 || c == 69) {
/* 1647 */       if (outPtr >= outBuf.length) {
/* 1648 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1649 */         outPtr = 0;
/*      */       } 
/* 1651 */       outBuf[outPtr++] = (char)c;
/*      */       
/* 1653 */       if (this._inputPtr >= this._inputEnd) {
/* 1654 */         _loadMoreGuaranteed();
/*      */       }
/* 1656 */       c = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */       
/* 1658 */       if (c == 45 || c == 43) {
/* 1659 */         if (outPtr >= outBuf.length) {
/* 1660 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1661 */           outPtr = 0;
/*      */         } 
/* 1663 */         outBuf[outPtr++] = (char)c;
/*      */         
/* 1665 */         if (this._inputPtr >= this._inputEnd) {
/* 1666 */           _loadMoreGuaranteed();
/*      */         }
/* 1668 */         c = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */       } 
/*      */ 
/*      */       
/* 1672 */       while (c >= 48 && c <= 57) {
/* 1673 */         expLen++;
/* 1674 */         if (outPtr >= outBuf.length) {
/* 1675 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1676 */           outPtr = 0;
/*      */         } 
/* 1678 */         outBuf[outPtr++] = (char)c;
/* 1679 */         if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 1680 */           eof = true;
/*      */           break;
/*      */         } 
/* 1683 */         c = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */       } 
/*      */       
/* 1686 */       if (expLen == 0) {
/* 1687 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1692 */     if (!eof) {
/* 1693 */       this._inputPtr--;
/*      */       
/* 1695 */       if (this._parsingContext.inRoot()) {
/* 1696 */         _verifyRootSpace(c);
/*      */       }
/*      */     } 
/* 1699 */     this._textBuffer.setCurrentLength(outPtr);
/*      */ 
/*      */     
/* 1702 */     return resetFloat(negative, integerPartLength, fractLen, expLen);
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
/* 1720 */     this._inputPtr++;
/*      */     
/* 1722 */     switch (ch) {
/*      */       case 9:
/*      */       case 32:
/*      */         return;
/*      */       case 13:
/* 1727 */         _skipCR();
/*      */         return;
/*      */       case 10:
/* 1730 */         this._currInputRow++;
/* 1731 */         this._currInputRowStart = this._inputPtr;
/*      */         return;
/*      */     } 
/* 1734 */     _reportMissingRootWS(ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final String _parseName(int i) throws IOException {
/* 1745 */     if (i != 34) {
/* 1746 */       return _handleOddName(i);
/*      */     }
/*      */     
/* 1749 */     if (this._inputPtr + 13 > this._inputEnd) {
/* 1750 */       return slowParseName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1759 */     byte[] input = this._inputBuffer;
/* 1760 */     int[] codes = _icLatin1;
/*      */     
/* 1762 */     int q = input[this._inputPtr++] & 0xFF;
/*      */     
/* 1764 */     if (codes[q] == 0) {
/* 1765 */       i = input[this._inputPtr++] & 0xFF;
/* 1766 */       if (codes[i] == 0) {
/* 1767 */         q = q << 8 | i;
/* 1768 */         i = input[this._inputPtr++] & 0xFF;
/* 1769 */         if (codes[i] == 0) {
/* 1770 */           q = q << 8 | i;
/* 1771 */           i = input[this._inputPtr++] & 0xFF;
/* 1772 */           if (codes[i] == 0) {
/* 1773 */             q = q << 8 | i;
/* 1774 */             i = input[this._inputPtr++] & 0xFF;
/* 1775 */             if (codes[i] == 0) {
/* 1776 */               this._quad1 = q;
/* 1777 */               return parseMediumName(i);
/*      */             } 
/* 1779 */             if (i == 34) {
/* 1780 */               return findName(q, 4);
/*      */             }
/* 1782 */             return parseName(q, i, 4);
/*      */           } 
/* 1784 */           if (i == 34) {
/* 1785 */             return findName(q, 3);
/*      */           }
/* 1787 */           return parseName(q, i, 3);
/*      */         } 
/* 1789 */         if (i == 34) {
/* 1790 */           return findName(q, 2);
/*      */         }
/* 1792 */         return parseName(q, i, 2);
/*      */       } 
/* 1794 */       if (i == 34) {
/* 1795 */         return findName(q, 1);
/*      */       }
/* 1797 */       return parseName(q, i, 1);
/*      */     } 
/* 1799 */     if (q == 34) {
/* 1800 */       return "";
/*      */     }
/* 1802 */     return parseName(0, q, 0);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final String parseMediumName(int q2) throws IOException {
/* 1807 */     byte[] input = this._inputBuffer;
/* 1808 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1811 */     int i = input[this._inputPtr++] & 0xFF;
/* 1812 */     if (codes[i] != 0) {
/* 1813 */       if (i == 34) {
/* 1814 */         return findName(this._quad1, q2, 1);
/*      */       }
/* 1816 */       return parseName(this._quad1, q2, i, 1);
/*      */     } 
/* 1818 */     q2 = q2 << 8 | i;
/* 1819 */     i = input[this._inputPtr++] & 0xFF;
/* 1820 */     if (codes[i] != 0) {
/* 1821 */       if (i == 34) {
/* 1822 */         return findName(this._quad1, q2, 2);
/*      */       }
/* 1824 */       return parseName(this._quad1, q2, i, 2);
/*      */     } 
/* 1826 */     q2 = q2 << 8 | i;
/* 1827 */     i = input[this._inputPtr++] & 0xFF;
/* 1828 */     if (codes[i] != 0) {
/* 1829 */       if (i == 34) {
/* 1830 */         return findName(this._quad1, q2, 3);
/*      */       }
/* 1832 */       return parseName(this._quad1, q2, i, 3);
/*      */     } 
/* 1834 */     q2 = q2 << 8 | i;
/* 1835 */     i = input[this._inputPtr++] & 0xFF;
/* 1836 */     if (codes[i] != 0) {
/* 1837 */       if (i == 34) {
/* 1838 */         return findName(this._quad1, q2, 4);
/*      */       }
/* 1840 */       return parseName(this._quad1, q2, i, 4);
/*      */     } 
/* 1842 */     return parseMediumName2(i, q2);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final String parseMediumName2(int q3, int q2) throws IOException {
/* 1848 */     byte[] input = this._inputBuffer;
/* 1849 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1852 */     int i = input[this._inputPtr++] & 0xFF;
/* 1853 */     if (codes[i] != 0) {
/* 1854 */       if (i == 34) {
/* 1855 */         return findName(this._quad1, q2, q3, 1);
/*      */       }
/* 1857 */       return parseName(this._quad1, q2, q3, i, 1);
/*      */     } 
/* 1859 */     q3 = q3 << 8 | i;
/* 1860 */     i = input[this._inputPtr++] & 0xFF;
/* 1861 */     if (codes[i] != 0) {
/* 1862 */       if (i == 34) {
/* 1863 */         return findName(this._quad1, q2, q3, 2);
/*      */       }
/* 1865 */       return parseName(this._quad1, q2, q3, i, 2);
/*      */     } 
/* 1867 */     q3 = q3 << 8 | i;
/* 1868 */     i = input[this._inputPtr++] & 0xFF;
/* 1869 */     if (codes[i] != 0) {
/* 1870 */       if (i == 34) {
/* 1871 */         return findName(this._quad1, q2, q3, 3);
/*      */       }
/* 1873 */       return parseName(this._quad1, q2, q3, i, 3);
/*      */     } 
/* 1875 */     q3 = q3 << 8 | i;
/* 1876 */     i = input[this._inputPtr++] & 0xFF;
/* 1877 */     if (codes[i] != 0) {
/* 1878 */       if (i == 34) {
/* 1879 */         return findName(this._quad1, q2, q3, 4);
/*      */       }
/* 1881 */       return parseName(this._quad1, q2, q3, i, 4);
/*      */     } 
/* 1883 */     return parseLongName(i, q2, q3);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final String parseLongName(int q, int q2, int q3) throws IOException {
/* 1888 */     this._quadBuffer[0] = this._quad1;
/* 1889 */     this._quadBuffer[1] = q2;
/* 1890 */     this._quadBuffer[2] = q3;
/*      */ 
/*      */     
/* 1893 */     byte[] input = this._inputBuffer;
/* 1894 */     int[] codes = _icLatin1;
/* 1895 */     int qlen = 3;
/*      */     
/* 1897 */     while (this._inputPtr + 4 <= this._inputEnd) {
/* 1898 */       int i = input[this._inputPtr++] & 0xFF;
/* 1899 */       if (codes[i] != 0) {
/* 1900 */         if (i == 34) {
/* 1901 */           return findName(this._quadBuffer, qlen, q, 1);
/*      */         }
/* 1903 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 1);
/*      */       } 
/*      */       
/* 1906 */       q = q << 8 | i;
/* 1907 */       i = input[this._inputPtr++] & 0xFF;
/* 1908 */       if (codes[i] != 0) {
/* 1909 */         if (i == 34) {
/* 1910 */           return findName(this._quadBuffer, qlen, q, 2);
/*      */         }
/* 1912 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 2);
/*      */       } 
/*      */       
/* 1915 */       q = q << 8 | i;
/* 1916 */       i = input[this._inputPtr++] & 0xFF;
/* 1917 */       if (codes[i] != 0) {
/* 1918 */         if (i == 34) {
/* 1919 */           return findName(this._quadBuffer, qlen, q, 3);
/*      */         }
/* 1921 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 3);
/*      */       } 
/*      */       
/* 1924 */       q = q << 8 | i;
/* 1925 */       i = input[this._inputPtr++] & 0xFF;
/* 1926 */       if (codes[i] != 0) {
/* 1927 */         if (i == 34) {
/* 1928 */           return findName(this._quadBuffer, qlen, q, 4);
/*      */         }
/* 1930 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 4);
/*      */       } 
/*      */ 
/*      */       
/* 1934 */       if (qlen >= this._quadBuffer.length) {
/* 1935 */         this._quadBuffer = growArrayBy(this._quadBuffer, qlen);
/*      */       }
/* 1937 */       this._quadBuffer[qlen++] = q;
/* 1938 */       q = i;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1945 */     return parseEscapedName(this._quadBuffer, qlen, 0, q, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String slowParseName() throws IOException {
/* 1953 */     if (this._inputPtr >= this._inputEnd && 
/* 1954 */       !_loadMore()) {
/* 1955 */       _reportInvalidEOF(": was expecting closing '\"' for name", JsonToken.FIELD_NAME);
/*      */     }
/*      */     
/* 1958 */     int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 1959 */     if (i == 34) {
/* 1960 */       return "";
/*      */     }
/* 1962 */     return parseEscapedName(this._quadBuffer, 0, 0, i, 0);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int ch, int lastQuadBytes) throws IOException {
/* 1966 */     return parseEscapedName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int ch, int lastQuadBytes) throws IOException {
/* 1970 */     this._quadBuffer[0] = q1;
/* 1971 */     return parseEscapedName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int q3, int ch, int lastQuadBytes) throws IOException {
/* 1975 */     this._quadBuffer[0] = q1;
/* 1976 */     this._quadBuffer[1] = q2;
/* 1977 */     return parseEscapedName(this._quadBuffer, 2, q3, ch, lastQuadBytes);
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
/*      */   protected final String parseEscapedName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes) throws IOException {
/* 1990 */     int[] codes = _icLatin1;
/*      */     
/*      */     while (true) {
/* 1993 */       if (codes[ch] != 0) {
/* 1994 */         if (ch == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 1998 */         if (ch != 92) {
/*      */           
/* 2000 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 2003 */           ch = _decodeEscaped();
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 2008 */         if (ch > 127) {
/*      */           
/* 2010 */           if (currQuadBytes >= 4) {
/* 2011 */             if (qlen >= quads.length) {
/* 2012 */               this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */             }
/* 2014 */             quads[qlen++] = currQuad;
/* 2015 */             currQuad = 0;
/* 2016 */             currQuadBytes = 0;
/*      */           } 
/* 2018 */           if (ch < 2048) {
/* 2019 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2020 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 2023 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 2024 */             currQuadBytes++;
/*      */             
/* 2026 */             if (currQuadBytes >= 4) {
/* 2027 */               if (qlen >= quads.length) {
/* 2028 */                 this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */               }
/* 2030 */               quads[qlen++] = currQuad;
/* 2031 */               currQuad = 0;
/* 2032 */               currQuadBytes = 0;
/*      */             } 
/* 2034 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2035 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 2038 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 2042 */       if (currQuadBytes < 4) {
/* 2043 */         currQuadBytes++;
/* 2044 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 2046 */         if (qlen >= quads.length) {
/* 2047 */           this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */         }
/* 2049 */         quads[qlen++] = currQuad;
/* 2050 */         currQuad = ch;
/* 2051 */         currQuadBytes = 1;
/*      */       } 
/* 2053 */       if (this._inputPtr >= this._inputEnd && 
/* 2054 */         !_loadMore()) {
/* 2055 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 2058 */       ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     } 
/*      */     
/* 2061 */     if (currQuadBytes > 0) {
/* 2062 */       if (qlen >= quads.length) {
/* 2063 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2065 */       quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
/*      */     } 
/* 2067 */     String name = this._symbols.findName(quads, qlen);
/* 2068 */     if (name == null) {
/* 2069 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2071 */     return name;
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
/*      */   protected String _handleOddName(int ch) throws IOException {
/* 2090 */     if (ch == 39 && (this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 2091 */       return _parseAposName();
/*      */     }
/*      */     
/* 2094 */     if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
/* 2095 */       char c = (char)_decodeCharForError(ch);
/* 2096 */       _reportUnexpectedChar(c, "was expecting double-quote to start field name");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2102 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */     
/* 2104 */     if (codes[ch] != 0) {
/* 2105 */       _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2111 */     int[] quads = this._quadBuffer;
/* 2112 */     int qlen = 0;
/* 2113 */     int currQuad = 0;
/* 2114 */     int currQuadBytes = 0;
/*      */ 
/*      */     
/*      */     while (true) {
/* 2118 */       if (currQuadBytes < 4) {
/* 2119 */         currQuadBytes++;
/* 2120 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 2122 */         if (qlen >= quads.length) {
/* 2123 */           this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */         }
/* 2125 */         quads[qlen++] = currQuad;
/* 2126 */         currQuad = ch;
/* 2127 */         currQuadBytes = 1;
/*      */       } 
/* 2129 */       if (this._inputPtr >= this._inputEnd && 
/* 2130 */         !_loadMore()) {
/* 2131 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 2134 */       ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2135 */       if (codes[ch] != 0) {
/*      */         break;
/*      */       }
/* 2138 */       this._inputPtr++;
/*      */     } 
/*      */     
/* 2141 */     if (currQuadBytes > 0) {
/* 2142 */       if (qlen >= quads.length) {
/* 2143 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2145 */       quads[qlen++] = currQuad;
/*      */     } 
/* 2147 */     String name = this._symbols.findName(quads, qlen);
/* 2148 */     if (name == null) {
/* 2149 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2151 */     return name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _parseAposName() throws IOException {
/* 2160 */     if (this._inputPtr >= this._inputEnd && 
/* 2161 */       !_loadMore()) {
/* 2162 */       _reportInvalidEOF(": was expecting closing ''' for field name", JsonToken.FIELD_NAME);
/*      */     }
/*      */     
/* 2165 */     int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 2166 */     if (ch == 39) {
/* 2167 */       return "";
/*      */     }
/* 2169 */     int[] quads = this._quadBuffer;
/* 2170 */     int qlen = 0;
/* 2171 */     int currQuad = 0;
/* 2172 */     int currQuadBytes = 0;
/*      */ 
/*      */ 
/*      */     
/* 2176 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 2179 */     while (ch != 39) {
/*      */ 
/*      */ 
/*      */       
/* 2183 */       if (codes[ch] != 0 && ch != 34) {
/* 2184 */         if (ch != 92) {
/*      */ 
/*      */           
/* 2187 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 2190 */           ch = _decodeEscaped();
/*      */         } 
/*      */         
/* 2193 */         if (ch > 127) {
/*      */           
/* 2195 */           if (currQuadBytes >= 4) {
/* 2196 */             if (qlen >= quads.length) {
/* 2197 */               this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */             }
/* 2199 */             quads[qlen++] = currQuad;
/* 2200 */             currQuad = 0;
/* 2201 */             currQuadBytes = 0;
/*      */           } 
/* 2203 */           if (ch < 2048) {
/* 2204 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 2205 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 2208 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 2209 */             currQuadBytes++;
/*      */             
/* 2211 */             if (currQuadBytes >= 4) {
/* 2212 */               if (qlen >= quads.length) {
/* 2213 */                 this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */               }
/* 2215 */               quads[qlen++] = currQuad;
/* 2216 */               currQuad = 0;
/* 2217 */               currQuadBytes = 0;
/*      */             } 
/* 2219 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 2220 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 2223 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 2227 */       if (currQuadBytes < 4) {
/* 2228 */         currQuadBytes++;
/* 2229 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 2231 */         if (qlen >= quads.length) {
/* 2232 */           this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */         }
/* 2234 */         quads[qlen++] = currQuad;
/* 2235 */         currQuad = ch;
/* 2236 */         currQuadBytes = 1;
/*      */       } 
/* 2238 */       if (this._inputPtr >= this._inputEnd && 
/* 2239 */         !_loadMore()) {
/* 2240 */         _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */       }
/*      */       
/* 2243 */       ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */     } 
/*      */     
/* 2246 */     if (currQuadBytes > 0) {
/* 2247 */       if (qlen >= quads.length) {
/* 2248 */         this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */       }
/* 2250 */       quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
/*      */     } 
/* 2252 */     String name = this._symbols.findName(quads, qlen);
/* 2253 */     if (name == null) {
/* 2254 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 2256 */     return name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int lastQuadBytes) throws JsonParseException {
/* 2267 */     q1 = _padLastQuad(q1, lastQuadBytes);
/*      */     
/* 2269 */     String name = this._symbols.findName(q1);
/* 2270 */     if (name != null) {
/* 2271 */       return name;
/*      */     }
/*      */     
/* 2274 */     this._quadBuffer[0] = q1;
/* 2275 */     return addName(this._quadBuffer, 1, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
/* 2280 */     q2 = _padLastQuad(q2, lastQuadBytes);
/*      */     
/* 2282 */     String name = this._symbols.findName(q1, q2);
/* 2283 */     if (name != null) {
/* 2284 */       return name;
/*      */     }
/*      */     
/* 2287 */     this._quadBuffer[0] = q1;
/* 2288 */     this._quadBuffer[1] = q2;
/* 2289 */     return addName(this._quadBuffer, 2, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException {
/* 2294 */     q3 = _padLastQuad(q3, lastQuadBytes);
/* 2295 */     String name = this._symbols.findName(q1, q2, q3);
/* 2296 */     if (name != null) {
/* 2297 */       return name;
/*      */     }
/* 2299 */     int[] quads = this._quadBuffer;
/* 2300 */     quads[0] = q1;
/* 2301 */     quads[1] = q2;
/* 2302 */     quads[2] = _padLastQuad(q3, lastQuadBytes);
/* 2303 */     return addName(quads, 3, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
/* 2308 */     if (qlen >= quads.length) {
/* 2309 */       this._quadBuffer = quads = growArrayBy(quads, quads.length);
/*      */     }
/* 2311 */     quads[qlen++] = _padLastQuad(lastQuad, lastQuadBytes);
/* 2312 */     String name = this._symbols.findName(quads, qlen);
/* 2313 */     if (name == null) {
/* 2314 */       return addName(quads, qlen, lastQuadBytes);
/*      */     }
/* 2316 */     return name;
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
/*      */   private final String addName(int[] quads, int qlen, int lastQuadBytes) throws JsonParseException {
/* 2331 */     int lastQuad, byteLen = (qlen << 2) - 4 + lastQuadBytes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2340 */     if (lastQuadBytes < 4) {
/* 2341 */       lastQuad = quads[qlen - 1];
/*      */       
/* 2343 */       quads[qlen - 1] = lastQuad << 4 - lastQuadBytes << 3;
/*      */     } else {
/* 2345 */       lastQuad = 0;
/*      */     } 
/*      */ 
/*      */     
/* 2349 */     char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2350 */     int cix = 0;
/*      */     
/* 2352 */     for (int ix = 0; ix < byteLen; ) {
/* 2353 */       int ch = quads[ix >> 2];
/* 2354 */       int byteIx = ix & 0x3;
/* 2355 */       ch = ch >> 3 - byteIx << 3 & 0xFF;
/* 2356 */       ix++;
/*      */       
/* 2358 */       if (ch > 127) {
/*      */         int needed;
/* 2360 */         if ((ch & 0xE0) == 192) {
/* 2361 */           ch &= 0x1F;
/* 2362 */           needed = 1;
/* 2363 */         } else if ((ch & 0xF0) == 224) {
/* 2364 */           ch &= 0xF;
/* 2365 */           needed = 2;
/* 2366 */         } else if ((ch & 0xF8) == 240) {
/* 2367 */           ch &= 0x7;
/* 2368 */           needed = 3;
/*      */         } else {
/* 2370 */           _reportInvalidInitial(ch);
/* 2371 */           needed = ch = 1;
/*      */         } 
/* 2373 */         if (ix + needed > byteLen) {
/* 2374 */           _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */         }
/*      */ 
/*      */         
/* 2378 */         int ch2 = quads[ix >> 2];
/* 2379 */         byteIx = ix & 0x3;
/* 2380 */         ch2 >>= 3 - byteIx << 3;
/* 2381 */         ix++;
/*      */         
/* 2383 */         if ((ch2 & 0xC0) != 128) {
/* 2384 */           _reportInvalidOther(ch2);
/*      */         }
/* 2386 */         ch = ch << 6 | ch2 & 0x3F;
/* 2387 */         if (needed > 1) {
/* 2388 */           ch2 = quads[ix >> 2];
/* 2389 */           byteIx = ix & 0x3;
/* 2390 */           ch2 >>= 3 - byteIx << 3;
/* 2391 */           ix++;
/*      */           
/* 2393 */           if ((ch2 & 0xC0) != 128) {
/* 2394 */             _reportInvalidOther(ch2);
/*      */           }
/* 2396 */           ch = ch << 6 | ch2 & 0x3F;
/* 2397 */           if (needed > 2) {
/* 2398 */             ch2 = quads[ix >> 2];
/* 2399 */             byteIx = ix & 0x3;
/* 2400 */             ch2 >>= 3 - byteIx << 3;
/* 2401 */             ix++;
/* 2402 */             if ((ch2 & 0xC0) != 128) {
/* 2403 */               _reportInvalidOther(ch2 & 0xFF);
/*      */             }
/* 2405 */             ch = ch << 6 | ch2 & 0x3F;
/*      */           } 
/*      */         } 
/* 2408 */         if (needed > 2) {
/* 2409 */           ch -= 65536;
/* 2410 */           if (cix >= cbuf.length) {
/* 2411 */             cbuf = this._textBuffer.expandCurrentSegment();
/*      */           }
/* 2413 */           cbuf[cix++] = (char)(55296 + (ch >> 10));
/* 2414 */           ch = 0xDC00 | ch & 0x3FF;
/*      */         } 
/*      */       } 
/* 2417 */       if (cix >= cbuf.length) {
/* 2418 */         cbuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 2420 */       cbuf[cix++] = (char)ch;
/*      */     } 
/*      */ 
/*      */     
/* 2424 */     String baseName = new String(cbuf, 0, cix);
/*      */     
/* 2426 */     if (lastQuadBytes < 4) {
/* 2427 */       quads[qlen - 1] = lastQuad;
/*      */     }
/* 2429 */     return this._symbols.addName(baseName, quads, qlen);
/*      */   }
/*      */ 
/*      */   
/*      */   private static final int _padLastQuad(int q, int bytes) {
/* 2434 */     return (bytes == 4) ? q : (q | -1 << bytes << 3);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _loadMoreGuaranteed() throws IOException {
/* 2444 */     if (!_loadMore()) _reportInvalidEOF();
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _finishString() throws IOException {
/* 2451 */     int ptr = this._inputPtr;
/* 2452 */     if (ptr >= this._inputEnd) {
/* 2453 */       _loadMoreGuaranteed();
/* 2454 */       ptr = this._inputPtr;
/*      */     } 
/* 2456 */     int outPtr = 0;
/* 2457 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2458 */     int[] codes = _icUTF8;
/*      */     
/* 2460 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2461 */     byte[] inputBuffer = this._inputBuffer;
/* 2462 */     while (ptr < max) {
/* 2463 */       int c = inputBuffer[ptr] & 0xFF;
/* 2464 */       if (codes[c] != 0) {
/* 2465 */         if (c == 34) {
/* 2466 */           this._inputPtr = ptr + 1;
/* 2467 */           this._textBuffer.setCurrentLength(outPtr);
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       } 
/* 2472 */       ptr++;
/* 2473 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 2475 */     this._inputPtr = ptr;
/* 2476 */     _finishString2(outBuf, outPtr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _finishAndReturnString() throws IOException {
/* 2483 */     int ptr = this._inputPtr;
/* 2484 */     if (ptr >= this._inputEnd) {
/* 2485 */       _loadMoreGuaranteed();
/* 2486 */       ptr = this._inputPtr;
/*      */     } 
/* 2488 */     int outPtr = 0;
/* 2489 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 2490 */     int[] codes = _icUTF8;
/*      */     
/* 2492 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 2493 */     byte[] inputBuffer = this._inputBuffer;
/* 2494 */     while (ptr < max) {
/* 2495 */       int c = inputBuffer[ptr] & 0xFF;
/* 2496 */       if (codes[c] != 0) {
/* 2497 */         if (c == 34) {
/* 2498 */           this._inputPtr = ptr + 1;
/* 2499 */           return this._textBuffer.setCurrentAndReturn(outPtr);
/*      */         } 
/*      */         break;
/*      */       } 
/* 2503 */       ptr++;
/* 2504 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 2506 */     this._inputPtr = ptr;
/* 2507 */     _finishString2(outBuf, outPtr);
/* 2508 */     return this._textBuffer.contentsAsString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _finishString2(char[] outBuf, int outPtr) throws IOException {
/* 2517 */     int[] codes = _icUTF8;
/* 2518 */     byte[] inputBuffer = this._inputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2525 */       int ptr = this._inputPtr;
/* 2526 */       if (ptr >= this._inputEnd) {
/* 2527 */         _loadMoreGuaranteed();
/* 2528 */         ptr = this._inputPtr;
/*      */       } 
/* 2530 */       if (outPtr >= outBuf.length) {
/* 2531 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2532 */         outPtr = 0;
/*      */       } 
/* 2534 */       int max = Math.min(this._inputEnd, ptr + outBuf.length - outPtr);
/* 2535 */       while (ptr < max) {
/* 2536 */         int c = inputBuffer[ptr++] & 0xFF;
/* 2537 */         if (codes[c] != 0) {
/* 2538 */           this._inputPtr = ptr;
/*      */         } else {
/*      */           
/* 2541 */           outBuf[outPtr++] = (char)c;
/*      */           
/*      */           continue;
/*      */         } 
/*      */         
/* 2546 */         if (c == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 2550 */         switch (codes[c]) {
/*      */           case 1:
/* 2552 */             c = _decodeEscaped();
/*      */             break;
/*      */           case 2:
/* 2555 */             c = _decodeUtf8_2(c);
/*      */             break;
/*      */           case 3:
/* 2558 */             if (this._inputEnd - this._inputPtr >= 2) {
/* 2559 */               c = _decodeUtf8_3fast(c); break;
/*      */             } 
/* 2561 */             c = _decodeUtf8_3(c);
/*      */             break;
/*      */           
/*      */           case 4:
/* 2565 */             c = _decodeUtf8_4(c);
/*      */             
/* 2567 */             outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2568 */             if (outPtr >= outBuf.length) {
/* 2569 */               outBuf = this._textBuffer.finishCurrentSegment();
/* 2570 */               outPtr = 0;
/*      */             } 
/* 2572 */             c = 0xDC00 | c & 0x3FF;
/*      */             break;
/*      */           
/*      */           default:
/* 2576 */             if (c < 32) {
/*      */               
/* 2578 */               _throwUnquotedSpace(c, "string value");
/*      */               break;
/*      */             } 
/* 2581 */             _reportInvalidChar(c);
/*      */             break;
/*      */         } 
/*      */         
/* 2585 */         if (outPtr >= outBuf.length) {
/* 2586 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 2587 */           outPtr = 0;
/*      */         } 
/*      */         
/* 2590 */         outBuf[outPtr++] = (char)c;
/*      */       }  this._inputPtr = ptr;
/* 2592 */     }  this._textBuffer.setCurrentLength(outPtr);
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
/*      */   protected void _skipString() throws IOException {
/* 2605 */     this._tokenIncomplete = false;
/*      */ 
/*      */     
/* 2608 */     int[] codes = _icUTF8;
/* 2609 */     byte[] inputBuffer = this._inputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2617 */       int ptr = this._inputPtr;
/* 2618 */       int max = this._inputEnd;
/* 2619 */       if (ptr >= max) {
/* 2620 */         _loadMoreGuaranteed();
/* 2621 */         ptr = this._inputPtr;
/* 2622 */         max = this._inputEnd;
/*      */       } 
/* 2624 */       while (ptr < max) {
/* 2625 */         int c = inputBuffer[ptr++] & 0xFF;
/* 2626 */         if (codes[c] != 0) {
/* 2627 */           this._inputPtr = ptr;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2634 */           if (c != 34) {
/*      */ 
/*      */ 
/*      */             
/* 2638 */             switch (codes[c]) {
/*      */               case 1:
/* 2640 */                 _decodeEscaped();
/*      */                 continue;
/*      */               case 2:
/* 2643 */                 _skipUtf8_2();
/*      */                 continue;
/*      */               case 3:
/* 2646 */                 _skipUtf8_3();
/*      */                 continue;
/*      */               case 4:
/* 2649 */                 _skipUtf8_4(c);
/*      */                 continue;
/*      */             } 
/* 2652 */             if (c < 32) {
/* 2653 */               _throwUnquotedSpace(c, "string value");
/*      */               continue;
/*      */             } 
/* 2656 */             _reportInvalidChar(c);
/*      */             continue;
/*      */           } 
/*      */           return;
/*      */         } 
/*      */       } 
/*      */       this._inputPtr = ptr;
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
/*      */   protected JsonToken _handleUnexpectedValue(int c) throws IOException {
/* 2676 */     switch (c) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 93:
/* 2685 */         if (!this._parsingContext.inArray()) {
/*      */           break;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 44:
/* 2694 */         if (!this._parsingContext.inRoot() && (
/* 2695 */           this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/* 2696 */           this._inputPtr--;
/* 2697 */           return JsonToken.VALUE_NULL;
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 125:
/* 2704 */         _reportUnexpectedChar(c, "expected a value");
/*      */       case 39:
/* 2706 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 2707 */           return _handleApos();
/*      */         }
/*      */         break;
/*      */       case 78:
/* 2711 */         _matchToken("NaN", 1);
/* 2712 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2713 */           return resetAsNaN("NaN", Double.NaN);
/*      */         }
/* 2715 */         _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 73:
/* 2718 */         _matchToken("Infinity", 1);
/* 2719 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2720 */           return resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
/*      */         }
/* 2722 */         _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 43:
/* 2725 */         if (this._inputPtr >= this._inputEnd && 
/* 2726 */           !_loadMore()) {
/* 2727 */           _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
/*      */         }
/*      */         
/* 2730 */         return _handleInvalidNumberStart(this._inputBuffer[this._inputPtr++] & 0xFF, false);
/*      */     } 
/*      */     
/* 2733 */     if (Character.isJavaIdentifierStart(c)) {
/* 2734 */       _reportInvalidToken("" + (char)c, _validJsonTokenList());
/*      */     }
/*      */     
/* 2737 */     _reportUnexpectedChar(c, "expected a valid value " + _validJsonValueList());
/* 2738 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _handleApos() throws IOException {
/* 2743 */     int c = 0;
/*      */     
/* 2745 */     int outPtr = 0;
/* 2746 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */ 
/*      */     
/* 2749 */     int[] codes = _icUTF8;
/* 2750 */     byte[] inputBuffer = this._inputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true)
/* 2757 */     { if (this._inputPtr >= this._inputEnd) {
/* 2758 */         _loadMoreGuaranteed();
/*      */       }
/* 2760 */       if (outPtr >= outBuf.length) {
/* 2761 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2762 */         outPtr = 0;
/*      */       } 
/* 2764 */       int max = this._inputEnd;
/*      */       
/* 2766 */       int max2 = this._inputPtr + outBuf.length - outPtr;
/* 2767 */       if (max2 < max) {
/* 2768 */         max = max2;
/*      */       }
/*      */       
/* 2771 */       while (this._inputPtr < max)
/* 2772 */       { c = inputBuffer[this._inputPtr++] & 0xFF;
/* 2773 */         if (c != 39) {
/*      */ 
/*      */           
/* 2776 */           if (codes[c] != 0 && c != 34) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2786 */             switch (codes[c]) {
/*      */               case 1:
/* 2788 */                 c = _decodeEscaped();
/*      */                 break;
/*      */               case 2:
/* 2791 */                 c = _decodeUtf8_2(c);
/*      */                 break;
/*      */               case 3:
/* 2794 */                 if (this._inputEnd - this._inputPtr >= 2) {
/* 2795 */                   c = _decodeUtf8_3fast(c); break;
/*      */                 } 
/* 2797 */                 c = _decodeUtf8_3(c);
/*      */                 break;
/*      */               
/*      */               case 4:
/* 2801 */                 c = _decodeUtf8_4(c);
/*      */                 
/* 2803 */                 outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2804 */                 if (outPtr >= outBuf.length) {
/* 2805 */                   outBuf = this._textBuffer.finishCurrentSegment();
/* 2806 */                   outPtr = 0;
/*      */                 } 
/* 2808 */                 c = 0xDC00 | c & 0x3FF;
/*      */                 break;
/*      */               
/*      */               default:
/* 2812 */                 if (c < 32) {
/* 2813 */                   _throwUnquotedSpace(c, "string value");
/*      */                 }
/*      */                 
/* 2816 */                 _reportInvalidChar(c);
/*      */                 break;
/*      */             } 
/* 2819 */             if (outPtr >= outBuf.length) {
/* 2820 */               outBuf = this._textBuffer.finishCurrentSegment();
/* 2821 */               outPtr = 0;
/*      */             } 
/*      */             
/* 2824 */             outBuf[outPtr++] = (char)c; continue;
/*      */           }  outBuf[outPtr++] = (char)c; continue;
/* 2826 */         }  this._textBuffer.setCurrentLength(outPtr);
/*      */         
/* 2828 */         return JsonToken.VALUE_STRING; }  }  this._textBuffer.setCurrentLength(outPtr); return JsonToken.VALUE_STRING;
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
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean neg) throws IOException {
/* 2841 */     while (ch == 73) {
/* 2842 */       String match; if (this._inputPtr >= this._inputEnd && 
/* 2843 */         !_loadMore()) {
/* 2844 */         _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_FLOAT);
/*      */       }
/*      */       
/* 2847 */       ch = this._inputBuffer[this._inputPtr++];
/*      */       
/* 2849 */       if (ch == 78) {
/* 2850 */         match = neg ? "-INF" : "+INF";
/* 2851 */       } else if (ch == 110) {
/* 2852 */         match = neg ? "-Infinity" : "+Infinity";
/*      */       } else {
/*      */         break;
/*      */       } 
/* 2856 */       _matchToken(match, 3);
/* 2857 */       if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2858 */         return resetAsNaN(match, neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */       }
/* 2860 */       _reportError("Non-standard token '%s': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow", match);
/*      */     } 
/*      */     
/* 2863 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 2864 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _matchTrue() throws IOException {
/* 2870 */     int ptr = this._inputPtr;
/* 2871 */     if (ptr + 3 < this._inputEnd) {
/* 2872 */       byte[] buf = this._inputBuffer;
/* 2873 */       if (buf[ptr++] == 114 && buf[ptr++] == 117 && buf[ptr++] == 101) {
/*      */ 
/*      */         
/* 2876 */         int ch = buf[ptr] & 0xFF;
/* 2877 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 2878 */           this._inputPtr = ptr;
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2883 */     _matchToken2("true", 1);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _matchFalse() throws IOException {
/* 2888 */     int ptr = this._inputPtr;
/* 2889 */     if (ptr + 4 < this._inputEnd) {
/* 2890 */       byte[] buf = this._inputBuffer;
/* 2891 */       if (buf[ptr++] == 97 && buf[ptr++] == 108 && buf[ptr++] == 115 && buf[ptr++] == 101) {
/*      */ 
/*      */ 
/*      */         
/* 2895 */         int ch = buf[ptr] & 0xFF;
/* 2896 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 2897 */           this._inputPtr = ptr;
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2902 */     _matchToken2("false", 1);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _matchNull() throws IOException {
/* 2907 */     int ptr = this._inputPtr;
/* 2908 */     if (ptr + 3 < this._inputEnd) {
/* 2909 */       byte[] buf = this._inputBuffer;
/* 2910 */       if (buf[ptr++] == 117 && buf[ptr++] == 108 && buf[ptr++] == 108) {
/*      */ 
/*      */         
/* 2913 */         int ch = buf[ptr] & 0xFF;
/* 2914 */         if (ch < 48 || ch == 93 || ch == 125) {
/* 2915 */           this._inputPtr = ptr;
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2920 */     _matchToken2("null", 1);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _matchToken(String matchStr, int i) throws IOException {
/* 2925 */     int len = matchStr.length();
/* 2926 */     if (this._inputPtr + len >= this._inputEnd) {
/* 2927 */       _matchToken2(matchStr, i);
/*      */       return;
/*      */     } 
/*      */     do {
/* 2931 */       if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/* 2932 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2934 */       this._inputPtr++;
/* 2935 */     } while (++i < len);
/*      */     
/* 2937 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2938 */     if (ch >= 48 && ch != 93 && ch != 125) {
/* 2939 */       _checkMatchEnd(matchStr, i, ch);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _matchToken2(String matchStr, int i) throws IOException {
/* 2945 */     int len = matchStr.length();
/*      */     do {
/* 2947 */       if ((this._inputPtr >= this._inputEnd && !_loadMore()) || this._inputBuffer[this._inputPtr] != matchStr
/* 2948 */         .charAt(i)) {
/* 2949 */         _reportInvalidToken(matchStr.substring(0, i));
/*      */       }
/* 2951 */       this._inputPtr++;
/* 2952 */     } while (++i < len);
/*      */ 
/*      */     
/* 2955 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */       return;
/*      */     }
/* 2958 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2959 */     if (ch >= 48 && ch != 93 && ch != 125) {
/* 2960 */       _checkMatchEnd(matchStr, i, ch);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _checkMatchEnd(String matchStr, int i, int ch) throws IOException {
/* 2966 */     char c = (char)_decodeCharForError(ch);
/* 2967 */     if (Character.isJavaIdentifierPart(c)) {
/* 2968 */       _reportInvalidToken(matchStr.substring(0, i));
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
/*      */   private final int _skipWS() throws IOException {
/* 2980 */     while (this._inputPtr < this._inputEnd) {
/* 2981 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 2982 */       if (i > 32) {
/* 2983 */         if (i == 47 || i == 35) {
/* 2984 */           this._inputPtr--;
/* 2985 */           return _skipWS2();
/*      */         } 
/* 2987 */         return i;
/*      */       } 
/* 2989 */       if (i != 32) {
/* 2990 */         if (i == 10) {
/* 2991 */           this._currInputRow++;
/* 2992 */           this._currInputRowStart = this._inputPtr; continue;
/* 2993 */         }  if (i == 13) {
/* 2994 */           _skipCR(); continue;
/* 2995 */         }  if (i != 9) {
/* 2996 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 3000 */     return _skipWS2();
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipWS2() throws IOException {
/* 3005 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3006 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3007 */       if (i > 32) {
/* 3008 */         if (i == 47) {
/* 3009 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 3012 */         if (i == 35 && 
/* 3013 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 3017 */         return i;
/*      */       } 
/* 3019 */       if (i != 32) {
/* 3020 */         if (i == 10) {
/* 3021 */           this._currInputRow++;
/* 3022 */           this._currInputRowStart = this._inputPtr; continue;
/* 3023 */         }  if (i == 13) {
/* 3024 */           _skipCR(); continue;
/* 3025 */         }  if (i != 9) {
/* 3026 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 3030 */     throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _skipWSOrEnd() throws IOException {
/* 3037 */     if (this._inputPtr >= this._inputEnd && 
/* 3038 */       !_loadMore()) {
/* 3039 */       return _eofAsNextChar();
/*      */     }
/*      */     
/* 3042 */     int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3043 */     if (i > 32) {
/* 3044 */       if (i == 47 || i == 35) {
/* 3045 */         this._inputPtr--;
/* 3046 */         return _skipWSOrEnd2();
/*      */       } 
/* 3048 */       return i;
/*      */     } 
/* 3050 */     if (i != 32) {
/* 3051 */       if (i == 10) {
/* 3052 */         this._currInputRow++;
/* 3053 */         this._currInputRowStart = this._inputPtr;
/* 3054 */       } else if (i == 13) {
/* 3055 */         _skipCR();
/* 3056 */       } else if (i != 9) {
/* 3057 */         _throwInvalidSpace(i);
/*      */       } 
/*      */     }
/*      */     
/* 3061 */     while (this._inputPtr < this._inputEnd) {
/* 3062 */       i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3063 */       if (i > 32) {
/* 3064 */         if (i == 47 || i == 35) {
/* 3065 */           this._inputPtr--;
/* 3066 */           return _skipWSOrEnd2();
/*      */         } 
/* 3068 */         return i;
/*      */       } 
/* 3070 */       if (i != 32) {
/* 3071 */         if (i == 10) {
/* 3072 */           this._currInputRow++;
/* 3073 */           this._currInputRowStart = this._inputPtr; continue;
/* 3074 */         }  if (i == 13) {
/* 3075 */           _skipCR(); continue;
/* 3076 */         }  if (i != 9) {
/* 3077 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 3081 */     return _skipWSOrEnd2();
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipWSOrEnd2() throws IOException {
/* 3086 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3087 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3088 */       if (i > 32) {
/* 3089 */         if (i == 47) {
/* 3090 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 3093 */         if (i == 35 && 
/* 3094 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 3098 */         return i;
/* 3099 */       }  if (i != 32) {
/* 3100 */         if (i == 10) {
/* 3101 */           this._currInputRow++;
/* 3102 */           this._currInputRowStart = this._inputPtr; continue;
/* 3103 */         }  if (i == 13) {
/* 3104 */           _skipCR(); continue;
/* 3105 */         }  if (i != 9) {
/* 3106 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 3111 */     return _eofAsNextChar();
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon() throws IOException {
/* 3116 */     if (this._inputPtr + 4 >= this._inputEnd) {
/* 3117 */       return _skipColon2(false);
/*      */     }
/*      */     
/* 3120 */     int i = this._inputBuffer[this._inputPtr];
/* 3121 */     if (i == 58) {
/* 3122 */       i = this._inputBuffer[++this._inputPtr];
/* 3123 */       if (i > 32) {
/* 3124 */         if (i == 47 || i == 35) {
/* 3125 */           return _skipColon2(true);
/*      */         }
/* 3127 */         this._inputPtr++;
/* 3128 */         return i;
/*      */       } 
/* 3130 */       if (i == 32 || i == 9) {
/* 3131 */         i = this._inputBuffer[++this._inputPtr];
/* 3132 */         if (i > 32) {
/* 3133 */           if (i == 47 || i == 35) {
/* 3134 */             return _skipColon2(true);
/*      */           }
/* 3136 */           this._inputPtr++;
/* 3137 */           return i;
/*      */         } 
/*      */       } 
/* 3140 */       return _skipColon2(true);
/*      */     } 
/* 3142 */     if (i == 32 || i == 9) {
/* 3143 */       i = this._inputBuffer[++this._inputPtr];
/*      */     }
/* 3145 */     if (i == 58) {
/* 3146 */       i = this._inputBuffer[++this._inputPtr];
/* 3147 */       if (i > 32) {
/* 3148 */         if (i == 47 || i == 35) {
/* 3149 */           return _skipColon2(true);
/*      */         }
/* 3151 */         this._inputPtr++;
/* 3152 */         return i;
/*      */       } 
/* 3154 */       if (i == 32 || i == 9) {
/* 3155 */         i = this._inputBuffer[++this._inputPtr];
/* 3156 */         if (i > 32) {
/* 3157 */           if (i == 47 || i == 35) {
/* 3158 */             return _skipColon2(true);
/*      */           }
/* 3160 */           this._inputPtr++;
/* 3161 */           return i;
/*      */         } 
/*      */       } 
/* 3164 */       return _skipColon2(true);
/*      */     } 
/* 3166 */     return _skipColon2(false);
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon2(boolean gotColon) throws IOException {
/* 3171 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3172 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */       
/* 3174 */       if (i > 32) {
/* 3175 */         if (i == 47) {
/* 3176 */           _skipComment();
/*      */           continue;
/*      */         } 
/* 3179 */         if (i == 35 && 
/* 3180 */           _skipYAMLComment()) {
/*      */           continue;
/*      */         }
/*      */         
/* 3184 */         if (gotColon) {
/* 3185 */           return i;
/*      */         }
/* 3187 */         if (i != 58) {
/* 3188 */           _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */         }
/* 3190 */         gotColon = true; continue;
/* 3191 */       }  if (i != 32) {
/* 3192 */         if (i == 10) {
/* 3193 */           this._currInputRow++;
/* 3194 */           this._currInputRowStart = this._inputPtr; continue;
/* 3195 */         }  if (i == 13) {
/* 3196 */           _skipCR(); continue;
/* 3197 */         }  if (i != 9) {
/* 3198 */           _throwInvalidSpace(i);
/*      */         }
/*      */       } 
/*      */     } 
/* 3202 */     _reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", null);
/*      */     
/* 3204 */     return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipComment() throws IOException {
/* 3209 */     if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
/* 3210 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     
/* 3213 */     if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/* 3214 */       _reportInvalidEOF(" in a comment", null);
/*      */     }
/* 3216 */     int c = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3217 */     if (c == 47) {
/* 3218 */       _skipLine();
/* 3219 */     } else if (c == 42) {
/* 3220 */       _skipCComment();
/*      */     } else {
/* 3222 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipCComment() throws IOException {
/* 3229 */     int[] codes = CharTypes.getInputCodeComment();
/*      */ 
/*      */ 
/*      */     
/* 3233 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3234 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3235 */       int code = codes[i];
/* 3236 */       if (code != 0) {
/* 3237 */         switch (code) {
/*      */           case 42:
/* 3239 */             if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*      */               break;
/*      */             }
/* 3242 */             if (this._inputBuffer[this._inputPtr] == 47) {
/* 3243 */               this._inputPtr++;
/*      */               return;
/*      */             } 
/*      */             continue;
/*      */           case 10:
/* 3248 */             this._currInputRow++;
/* 3249 */             this._currInputRowStart = this._inputPtr;
/*      */             continue;
/*      */           case 13:
/* 3252 */             _skipCR();
/*      */             continue;
/*      */           case 2:
/* 3255 */             _skipUtf8_2();
/*      */             continue;
/*      */           case 3:
/* 3258 */             _skipUtf8_3();
/*      */             continue;
/*      */           case 4:
/* 3261 */             _skipUtf8_4(i);
/*      */             continue;
/*      */         } 
/*      */         
/* 3265 */         _reportInvalidChar(i);
/*      */       } 
/*      */     } 
/*      */     
/* 3269 */     _reportInvalidEOF(" in a comment", null);
/*      */   }
/*      */ 
/*      */   
/*      */   private final boolean _skipYAMLComment() throws IOException {
/* 3274 */     if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
/* 3275 */       return false;
/*      */     }
/* 3277 */     _skipLine();
/* 3278 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipLine() throws IOException {
/* 3286 */     int[] codes = CharTypes.getInputCodeComment();
/* 3287 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3288 */       int i = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3289 */       int code = codes[i];
/* 3290 */       if (code != 0) {
/* 3291 */         switch (code) {
/*      */           case 10:
/* 3293 */             this._currInputRow++;
/* 3294 */             this._currInputRowStart = this._inputPtr;
/*      */             return;
/*      */           case 13:
/* 3297 */             _skipCR();
/*      */             return;
/*      */           case 42:
/*      */             continue;
/*      */           case 2:
/* 3302 */             _skipUtf8_2();
/*      */             continue;
/*      */           case 3:
/* 3305 */             _skipUtf8_3();
/*      */             continue;
/*      */           case 4:
/* 3308 */             _skipUtf8_4(i);
/*      */             continue;
/*      */         } 
/* 3311 */         if (code < 0)
/*      */         {
/* 3313 */           _reportInvalidChar(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _decodeEscaped() throws IOException {
/* 3323 */     if (this._inputPtr >= this._inputEnd && 
/* 3324 */       !_loadMore()) {
/* 3325 */       _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */     }
/*      */     
/* 3328 */     int c = this._inputBuffer[this._inputPtr++];
/*      */     
/* 3330 */     switch (c) {
/*      */       
/*      */       case 98:
/* 3333 */         return '\b';
/*      */       case 116:
/* 3335 */         return '\t';
/*      */       case 110:
/* 3337 */         return '\n';
/*      */       case 102:
/* 3339 */         return '\f';
/*      */       case 114:
/* 3341 */         return '\r';
/*      */ 
/*      */       
/*      */       case 34:
/*      */       case 47:
/*      */       case 92:
/* 3347 */         return (char)c;
/*      */       
/*      */       case 117:
/*      */         break;
/*      */       
/*      */       default:
/* 3353 */         return _handleUnrecognizedCharacterEscape((char)_decodeCharForError(c));
/*      */     } 
/*      */ 
/*      */     
/* 3357 */     int value = 0;
/* 3358 */     for (int i = 0; i < 4; i++) {
/* 3359 */       if (this._inputPtr >= this._inputEnd && 
/* 3360 */         !_loadMore()) {
/* 3361 */         _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
/*      */       }
/*      */       
/* 3364 */       int ch = this._inputBuffer[this._inputPtr++];
/* 3365 */       int digit = CharTypes.charToHex(ch);
/* 3366 */       if (digit < 0) {
/* 3367 */         _reportUnexpectedChar(ch & 0xFF, "expected a hex-digit for character escape sequence");
/*      */       }
/* 3369 */       value = value << 4 | digit;
/*      */     } 
/* 3371 */     return (char)value;
/*      */   }
/*      */ 
/*      */   
/*      */   protected int _decodeCharForError(int firstByte) throws IOException {
/* 3376 */     int c = firstByte & 0xFF;
/* 3377 */     if (c > 127) {
/*      */       int needed;
/*      */ 
/*      */       
/* 3381 */       if ((c & 0xE0) == 192) {
/* 3382 */         c &= 0x1F;
/* 3383 */         needed = 1;
/* 3384 */       } else if ((c & 0xF0) == 224) {
/* 3385 */         c &= 0xF;
/* 3386 */         needed = 2;
/* 3387 */       } else if ((c & 0xF8) == 240) {
/*      */         
/* 3389 */         c &= 0x7;
/* 3390 */         needed = 3;
/*      */       } else {
/* 3392 */         _reportInvalidInitial(c & 0xFF);
/* 3393 */         needed = 1;
/*      */       } 
/*      */       
/* 3396 */       int d = nextByte();
/* 3397 */       if ((d & 0xC0) != 128) {
/* 3398 */         _reportInvalidOther(d & 0xFF);
/*      */       }
/* 3400 */       c = c << 6 | d & 0x3F;
/*      */       
/* 3402 */       if (needed > 1) {
/* 3403 */         d = nextByte();
/* 3404 */         if ((d & 0xC0) != 128) {
/* 3405 */           _reportInvalidOther(d & 0xFF);
/*      */         }
/* 3407 */         c = c << 6 | d & 0x3F;
/* 3408 */         if (needed > 2) {
/* 3409 */           d = nextByte();
/* 3410 */           if ((d & 0xC0) != 128) {
/* 3411 */             _reportInvalidOther(d & 0xFF);
/*      */           }
/* 3413 */           c = c << 6 | d & 0x3F;
/*      */         } 
/*      */       } 
/*      */     } 
/* 3417 */     return c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_2(int c) throws IOException {
/* 3428 */     if (this._inputPtr >= this._inputEnd) {
/* 3429 */       _loadMoreGuaranteed();
/*      */     }
/* 3431 */     int d = this._inputBuffer[this._inputPtr++];
/* 3432 */     if ((d & 0xC0) != 128) {
/* 3433 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3435 */     return (c & 0x1F) << 6 | d & 0x3F;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_3(int c1) throws IOException {
/* 3440 */     if (this._inputPtr >= this._inputEnd) {
/* 3441 */       _loadMoreGuaranteed();
/*      */     }
/* 3443 */     c1 &= 0xF;
/* 3444 */     int d = this._inputBuffer[this._inputPtr++];
/* 3445 */     if ((d & 0xC0) != 128) {
/* 3446 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3448 */     int c = c1 << 6 | d & 0x3F;
/* 3449 */     if (this._inputPtr >= this._inputEnd) {
/* 3450 */       _loadMoreGuaranteed();
/*      */     }
/* 3452 */     d = this._inputBuffer[this._inputPtr++];
/* 3453 */     if ((d & 0xC0) != 128) {
/* 3454 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3456 */     c = c << 6 | d & 0x3F;
/* 3457 */     return c;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_3fast(int c1) throws IOException {
/* 3462 */     c1 &= 0xF;
/* 3463 */     int d = this._inputBuffer[this._inputPtr++];
/* 3464 */     if ((d & 0xC0) != 128) {
/* 3465 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3467 */     int c = c1 << 6 | d & 0x3F;
/* 3468 */     d = this._inputBuffer[this._inputPtr++];
/* 3469 */     if ((d & 0xC0) != 128) {
/* 3470 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3472 */     c = c << 6 | d & 0x3F;
/* 3473 */     return c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_4(int c) throws IOException {
/* 3480 */     if (this._inputPtr >= this._inputEnd) {
/* 3481 */       _loadMoreGuaranteed();
/*      */     }
/* 3483 */     int d = this._inputBuffer[this._inputPtr++];
/* 3484 */     if ((d & 0xC0) != 128) {
/* 3485 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3487 */     c = (c & 0x7) << 6 | d & 0x3F;
/*      */     
/* 3489 */     if (this._inputPtr >= this._inputEnd) {
/* 3490 */       _loadMoreGuaranteed();
/*      */     }
/* 3492 */     d = this._inputBuffer[this._inputPtr++];
/* 3493 */     if ((d & 0xC0) != 128) {
/* 3494 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3496 */     c = c << 6 | d & 0x3F;
/* 3497 */     if (this._inputPtr >= this._inputEnd) {
/* 3498 */       _loadMoreGuaranteed();
/*      */     }
/* 3500 */     d = this._inputBuffer[this._inputPtr++];
/* 3501 */     if ((d & 0xC0) != 128) {
/* 3502 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3508 */     return (c << 6 | d & 0x3F) - 65536;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_2() throws IOException {
/* 3513 */     if (this._inputPtr >= this._inputEnd) {
/* 3514 */       _loadMoreGuaranteed();
/*      */     }
/* 3516 */     int c = this._inputBuffer[this._inputPtr++];
/* 3517 */     if ((c & 0xC0) != 128) {
/* 3518 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_3() throws IOException {
/* 3527 */     if (this._inputPtr >= this._inputEnd) {
/* 3528 */       _loadMoreGuaranteed();
/*      */     }
/*      */     
/* 3531 */     int c = this._inputBuffer[this._inputPtr++];
/* 3532 */     if ((c & 0xC0) != 128) {
/* 3533 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/* 3535 */     if (this._inputPtr >= this._inputEnd) {
/* 3536 */       _loadMoreGuaranteed();
/*      */     }
/* 3538 */     c = this._inputBuffer[this._inputPtr++];
/* 3539 */     if ((c & 0xC0) != 128) {
/* 3540 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_4(int c) throws IOException {
/* 3546 */     if (this._inputPtr >= this._inputEnd) {
/* 3547 */       _loadMoreGuaranteed();
/*      */     }
/* 3549 */     int d = this._inputBuffer[this._inputPtr++];
/* 3550 */     if ((d & 0xC0) != 128) {
/* 3551 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3553 */     if (this._inputPtr >= this._inputEnd) {
/* 3554 */       _loadMoreGuaranteed();
/*      */     }
/* 3556 */     d = this._inputBuffer[this._inputPtr++];
/* 3557 */     if ((d & 0xC0) != 128) {
/* 3558 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 3560 */     if (this._inputPtr >= this._inputEnd) {
/* 3561 */       _loadMoreGuaranteed();
/*      */     }
/* 3563 */     d = this._inputBuffer[this._inputPtr++];
/* 3564 */     if ((d & 0xC0) != 128) {
/* 3565 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
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
/*      */   protected final void _skipCR() throws IOException {
/* 3579 */     if ((this._inputPtr < this._inputEnd || _loadMore()) && 
/* 3580 */       this._inputBuffer[this._inputPtr] == 10) {
/* 3581 */       this._inputPtr++;
/*      */     }
/*      */     
/* 3584 */     this._currInputRow++;
/* 3585 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   private int nextByte() throws IOException {
/* 3590 */     if (this._inputPtr >= this._inputEnd) {
/* 3591 */       _loadMoreGuaranteed();
/*      */     }
/* 3593 */     return this._inputBuffer[this._inputPtr++] & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart, int ptr) throws IOException {
/* 3603 */     this._inputPtr = ptr;
/* 3604 */     _reportInvalidToken(matchedPart, _validJsonTokenList());
/*      */   }
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart) throws IOException {
/* 3608 */     _reportInvalidToken(matchedPart, _validJsonTokenList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart, String msg) throws IOException {
/* 3617 */     StringBuilder sb = new StringBuilder(matchedPart);
/* 3618 */     while (this._inputPtr < this._inputEnd || _loadMore()) {
/* 3619 */       int i = this._inputBuffer[this._inputPtr++];
/* 3620 */       char c = (char)_decodeCharForError(i);
/* 3621 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3629 */       sb.append(c);
/* 3630 */       if (sb.length() >= 256) {
/* 3631 */         sb.append("...");
/*      */         break;
/*      */       } 
/*      */     } 
/* 3635 */     _reportError("Unrecognized token '%s': was expecting %s", sb, msg);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidChar(int c) throws JsonParseException {
/* 3641 */     if (c < 32) {
/* 3642 */       _throwInvalidSpace(c);
/*      */     }
/* 3644 */     _reportInvalidInitial(c);
/*      */   }
/*      */   
/*      */   protected void _reportInvalidInitial(int mask) throws JsonParseException {
/* 3648 */     _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */   
/*      */   protected void _reportInvalidOther(int mask) throws JsonParseException {
/* 3652 */     _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
/* 3658 */     this._inputPtr = ptr;
/* 3659 */     _reportInvalidOther(mask);
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
/*      */   protected final byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
/* 3682 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 3688 */       if (this._inputPtr >= this._inputEnd) {
/* 3689 */         _loadMoreGuaranteed();
/*      */       }
/* 3691 */       int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3692 */       if (ch > 32) {
/* 3693 */         int bits = b64variant.decodeBase64Char(ch);
/* 3694 */         if (bits < 0) {
/* 3695 */           if (ch == 34) {
/* 3696 */             return builder.toByteArray();
/*      */           }
/* 3698 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 3699 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/* 3703 */         int decodedData = bits;
/*      */ 
/*      */ 
/*      */         
/* 3707 */         if (this._inputPtr >= this._inputEnd) {
/* 3708 */           _loadMoreGuaranteed();
/*      */         }
/* 3710 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3711 */         bits = b64variant.decodeBase64Char(ch);
/* 3712 */         if (bits < 0) {
/* 3713 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/* 3715 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/* 3718 */         if (this._inputPtr >= this._inputEnd) {
/* 3719 */           _loadMoreGuaranteed();
/*      */         }
/* 3721 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3722 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/* 3725 */         if (bits < 0) {
/* 3726 */           if (bits != -2) {
/*      */             
/* 3728 */             if (ch == 34) {
/* 3729 */               decodedData >>= 4;
/* 3730 */               builder.append(decodedData);
/* 3731 */               if (b64variant.usesPadding()) {
/* 3732 */                 this._inputPtr--;
/* 3733 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/* 3735 */               return builder.toByteArray();
/*      */             } 
/* 3737 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/* 3739 */           if (bits == -2) {
/*      */             
/* 3741 */             if (this._inputPtr >= this._inputEnd) {
/* 3742 */               _loadMoreGuaranteed();
/*      */             }
/* 3744 */             ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3745 */             if (!b64variant.usesPaddingChar(ch) && 
/* 3746 */               _decodeBase64Escape(b64variant, ch, 3) != -2) {
/* 3747 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/* 3751 */             decodedData >>= 4;
/* 3752 */             builder.append(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/* 3757 */         decodedData = decodedData << 6 | bits;
/*      */         
/* 3759 */         if (this._inputPtr >= this._inputEnd) {
/* 3760 */           _loadMoreGuaranteed();
/*      */         }
/* 3762 */         ch = this._inputBuffer[this._inputPtr++] & 0xFF;
/* 3763 */         bits = b64variant.decodeBase64Char(ch);
/* 3764 */         if (bits < 0) {
/* 3765 */           if (bits != -2) {
/*      */             
/* 3767 */             if (ch == 34) {
/* 3768 */               decodedData >>= 2;
/* 3769 */               builder.appendTwoBytes(decodedData);
/* 3770 */               if (b64variant.usesPadding()) {
/* 3771 */                 this._inputPtr--;
/* 3772 */                 _handleBase64MissingPadding(b64variant);
/*      */               } 
/* 3774 */               return builder.toByteArray();
/*      */             } 
/* 3776 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/* 3778 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */             
/* 3782 */             decodedData >>= 2;
/* 3783 */             builder.appendTwoBytes(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/* 3788 */         decodedData = decodedData << 6 | bits;
/* 3789 */         builder.appendThreeBytes(decodedData);
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
/*      */   public JsonLocation getTokenLocation() {
/* 3803 */     if (this._currToken == JsonToken.FIELD_NAME) {
/* 3804 */       long total = this._currInputProcessed + (this._nameStartOffset - 1);
/* 3805 */       return new JsonLocation(_contentReference(), total, -1L, this._nameStartRow, this._nameStartCol);
/*      */     } 
/*      */     
/* 3808 */     return new JsonLocation(_contentReference(), this._tokenInputTotal - 1L, -1L, this._tokenInputRow, this._tokenInputCol);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getCurrentLocation() {
/* 3816 */     int col = this._inputPtr - this._currInputRowStart + 1;
/* 3817 */     return new JsonLocation(_contentReference(), this._currInputProcessed + this._inputPtr, -1L, this._currInputRow, col);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _updateLocation() {
/* 3825 */     this._tokenInputRow = this._currInputRow;
/* 3826 */     int ptr = this._inputPtr;
/* 3827 */     this._tokenInputTotal = this._currInputProcessed + ptr;
/* 3828 */     this._tokenInputCol = ptr - this._currInputRowStart;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _updateNameLocation() {
/* 3834 */     this._nameStartRow = this._currInputRow;
/* 3835 */     int ptr = this._inputPtr;
/* 3836 */     this._nameStartOffset = ptr;
/* 3837 */     this._nameStartCol = ptr - this._currInputRowStart;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _closeScope(int i) throws JsonParseException {
/* 3847 */     if (i == 125) {
/* 3848 */       _closeObjectScope();
/* 3849 */       return this._currToken = JsonToken.END_OBJECT;
/*      */     } 
/* 3851 */     _closeArrayScope();
/* 3852 */     return this._currToken = JsonToken.END_ARRAY;
/*      */   }
/*      */   
/*      */   private final void _closeArrayScope() throws JsonParseException {
/* 3856 */     _updateLocation();
/* 3857 */     if (!this._parsingContext.inArray()) {
/* 3858 */       _reportMismatchedEndMarker(93, '}');
/*      */     }
/* 3860 */     this._parsingContext = this._parsingContext.clearAndGetParent();
/*      */   }
/*      */   
/*      */   private final void _closeObjectScope() throws JsonParseException {
/* 3864 */     _updateLocation();
/* 3865 */     if (!this._parsingContext.inObject()) {
/* 3866 */       _reportMismatchedEndMarker(125, ']');
/*      */     }
/* 3868 */     this._parsingContext = this._parsingContext.clearAndGetParent();
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/UTF8StreamJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */