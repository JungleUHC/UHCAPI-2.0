/*      */ package com.fasterxml.jackson.core.json;
/*      */ 
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonLocation;
/*      */ import com.fasterxml.jackson.core.JsonParseException;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.StreamReadCapability;
/*      */ import com.fasterxml.jackson.core.base.ParserBase;
/*      */ import com.fasterxml.jackson.core.io.CharTypes;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*      */ import java.io.DataInput;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Writer;
/*      */ import java.util.Arrays;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UTF8DataInputJsonParser
/*      */   extends ParserBase
/*      */ {
/*      */   static final byte BYTE_LF = 10;
/*   44 */   private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
/*      */   
/*   46 */   private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
/*      */   
/*   48 */   private static final int FEAT_MASK_NON_NUM_NUMBERS = JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS.getMask();
/*      */   
/*   50 */   private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
/*   51 */   private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
/*   52 */   private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
/*   53 */   private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
/*   54 */   private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
/*      */ 
/*      */   
/*   57 */   private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
/*      */ 
/*      */ 
/*      */   
/*   61 */   protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
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
/*   90 */   protected int[] _quadBuffer = new int[16];
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
/*      */   private int _quad1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DataInput _inputData;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  116 */   protected int _nextByte = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UTF8DataInputJsonParser(IOContext ctxt, int features, DataInput inputData, ObjectCodec codec, ByteQuadsCanonicalizer sym, int firstByte) {
/*  128 */     super(ctxt, features);
/*  129 */     this._objectCodec = codec;
/*  130 */     this._symbols = sym;
/*  131 */     this._inputData = inputData;
/*  132 */     this._nextByte = firstByte;
/*      */   }
/*      */ 
/*      */   
/*      */   public ObjectCodec getCodec() {
/*  137 */     return this._objectCodec;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCodec(ObjectCodec c) {
/*  142 */     this._objectCodec = c;
/*      */   }
/*      */ 
/*      */   
/*      */   public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
/*  147 */     return JSON_READ_CAPABILITIES;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int releaseBuffered(OutputStream out) throws IOException {
/*  158 */     return 0;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getInputSource() {
/*  163 */     return this._inputData;
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
/*      */   protected void _closeInput() throws IOException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _releaseBuffers() throws IOException {
/*  184 */     super._releaseBuffers();
/*      */     
/*  186 */     this._symbols.release();
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
/*  198 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  199 */       if (this._tokenIncomplete) {
/*  200 */         this._tokenIncomplete = false;
/*  201 */         return _finishAndReturnString();
/*      */       } 
/*  203 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  205 */     return _getText2(this._currToken);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getText(Writer writer) throws IOException {
/*  211 */     JsonToken t = this._currToken;
/*  212 */     if (t == JsonToken.VALUE_STRING) {
/*  213 */       if (this._tokenIncomplete) {
/*  214 */         this._tokenIncomplete = false;
/*  215 */         _finishString();
/*      */       } 
/*  217 */       return this._textBuffer.contentsToWriter(writer);
/*      */     } 
/*  219 */     if (t == JsonToken.FIELD_NAME) {
/*  220 */       String n = this._parsingContext.getCurrentName();
/*  221 */       writer.write(n);
/*  222 */       return n.length();
/*      */     } 
/*  224 */     if (t != null) {
/*  225 */       if (t.isNumeric()) {
/*  226 */         return this._textBuffer.contentsToWriter(writer);
/*      */       }
/*  228 */       char[] ch = t.asCharArray();
/*  229 */       writer.write(ch);
/*  230 */       return ch.length;
/*      */     } 
/*  232 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getValueAsString() throws IOException {
/*  239 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  240 */       if (this._tokenIncomplete) {
/*  241 */         this._tokenIncomplete = false;
/*  242 */         return _finishAndReturnString();
/*      */       } 
/*  244 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  246 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  247 */       return getCurrentName();
/*      */     }
/*  249 */     return super.getValueAsString(null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getValueAsString(String defValue) throws IOException {
/*  255 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  256 */       if (this._tokenIncomplete) {
/*  257 */         this._tokenIncomplete = false;
/*  258 */         return _finishAndReturnString();
/*      */       } 
/*  260 */       return this._textBuffer.contentsAsString();
/*      */     } 
/*  262 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  263 */       return getCurrentName();
/*      */     }
/*  265 */     return super.getValueAsString(defValue);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getValueAsInt() throws IOException {
/*  271 */     JsonToken t = this._currToken;
/*  272 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/*      */       
/*  274 */       if ((this._numTypesValid & 0x1) == 0) {
/*  275 */         if (this._numTypesValid == 0) {
/*  276 */           return _parseIntValue();
/*      */         }
/*  278 */         if ((this._numTypesValid & 0x1) == 0) {
/*  279 */           convertNumberToInt();
/*      */         }
/*      */       } 
/*  282 */       return this._numberInt;
/*      */     } 
/*  284 */     return super.getValueAsInt(0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getValueAsInt(int defValue) throws IOException {
/*  290 */     JsonToken t = this._currToken;
/*  291 */     if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/*      */       
/*  293 */       if ((this._numTypesValid & 0x1) == 0) {
/*  294 */         if (this._numTypesValid == 0) {
/*  295 */           return _parseIntValue();
/*      */         }
/*  297 */         if ((this._numTypesValid & 0x1) == 0) {
/*  298 */           convertNumberToInt();
/*      */         }
/*      */       } 
/*  301 */       return this._numberInt;
/*      */     } 
/*  303 */     return super.getValueAsInt(defValue);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final String _getText2(JsonToken t) {
/*  308 */     if (t == null) {
/*  309 */       return null;
/*      */     }
/*  311 */     switch (t.id()) {
/*      */       case 5:
/*  313 */         return this._parsingContext.getCurrentName();
/*      */ 
/*      */       
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  319 */         return this._textBuffer.contentsAsString();
/*      */     } 
/*  321 */     return t.asString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public char[] getTextCharacters() throws IOException {
/*  328 */     if (this._currToken != null) {
/*  329 */       switch (this._currToken.id()) {
/*      */         
/*      */         case 5:
/*  332 */           if (!this._nameCopied) {
/*  333 */             String name = this._parsingContext.getCurrentName();
/*  334 */             int nameLen = name.length();
/*  335 */             if (this._nameCopyBuffer == null) {
/*  336 */               this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  337 */             } else if (this._nameCopyBuffer.length < nameLen) {
/*  338 */               this._nameCopyBuffer = new char[nameLen];
/*      */             } 
/*  340 */             name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  341 */             this._nameCopied = true;
/*      */           } 
/*  343 */           return this._nameCopyBuffer;
/*      */         
/*      */         case 6:
/*  346 */           if (this._tokenIncomplete) {
/*  347 */             this._tokenIncomplete = false;
/*  348 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  353 */           return this._textBuffer.getTextBuffer();
/*      */       } 
/*      */       
/*  356 */       return this._currToken.asCharArray();
/*      */     } 
/*      */     
/*  359 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTextLength() throws IOException {
/*  365 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  366 */       if (this._tokenIncomplete) {
/*  367 */         this._tokenIncomplete = false;
/*  368 */         _finishString();
/*      */       } 
/*  370 */       return this._textBuffer.size();
/*      */     } 
/*  372 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  373 */       return this._parsingContext.getCurrentName().length();
/*      */     }
/*  375 */     if (this._currToken != null) {
/*  376 */       if (this._currToken.isNumeric()) {
/*  377 */         return this._textBuffer.size();
/*      */       }
/*  379 */       return (this._currToken.asCharArray()).length;
/*      */     } 
/*  381 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTextOffset() throws IOException {
/*  388 */     if (this._currToken != null) {
/*  389 */       switch (this._currToken.id()) {
/*      */         case 5:
/*  391 */           return 0;
/*      */         case 6:
/*  393 */           if (this._tokenIncomplete) {
/*  394 */             this._tokenIncomplete = false;
/*  395 */             _finishString();
/*      */           } 
/*      */         
/*      */         case 7:
/*      */         case 8:
/*  400 */           return this._textBuffer.getTextOffset();
/*      */       } 
/*      */     
/*      */     }
/*  404 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/*  410 */     if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null))
/*      */     {
/*  412 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  417 */     if (this._tokenIncomplete) {
/*      */       try {
/*  419 */         this._binaryValue = _decodeBase64(b64variant);
/*  420 */       } catch (IllegalArgumentException iae) {
/*  421 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  426 */       this._tokenIncomplete = false;
/*      */     }
/*  428 */     else if (this._binaryValue == null) {
/*      */       
/*  430 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  431 */       _decodeBase64(getText(), builder, b64variant);
/*  432 */       this._binaryValue = builder.toByteArray();
/*      */     } 
/*      */     
/*  435 */     return this._binaryValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/*  442 */     if (!this._tokenIncomplete || this._currToken != JsonToken.VALUE_STRING) {
/*  443 */       byte[] b = getBinaryValue(b64variant);
/*  444 */       out.write(b);
/*  445 */       return b.length;
/*      */     } 
/*      */     
/*  448 */     byte[] buf = this._ioContext.allocBase64Buffer();
/*      */     try {
/*  450 */       return _readBinary(b64variant, out, buf);
/*      */     } finally {
/*  452 */       this._ioContext.releaseBase64Buffer(buf);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
/*  459 */     int outputPtr = 0;
/*  460 */     int outputEnd = buffer.length - 3;
/*  461 */     int outputCount = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  467 */       int ch = this._inputData.readUnsignedByte();
/*  468 */       if (ch > 32) {
/*  469 */         int bits = b64variant.decodeBase64Char(ch);
/*  470 */         if (bits < 0) {
/*  471 */           if (ch == 34) {
/*      */             break;
/*      */           }
/*  474 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/*  475 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  481 */         if (outputPtr > outputEnd) {
/*  482 */           outputCount += outputPtr;
/*  483 */           out.write(buffer, 0, outputPtr);
/*  484 */           outputPtr = 0;
/*      */         } 
/*      */         
/*  487 */         int decodedData = bits;
/*      */ 
/*      */         
/*  490 */         ch = this._inputData.readUnsignedByte();
/*  491 */         bits = b64variant.decodeBase64Char(ch);
/*  492 */         if (bits < 0) {
/*  493 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/*  495 */         decodedData = decodedData << 6 | bits;
/*      */ 
/*      */         
/*  498 */         ch = this._inputData.readUnsignedByte();
/*  499 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/*  502 */         if (bits < 0) {
/*  503 */           if (bits != -2) {
/*      */             
/*  505 */             if (ch == 34) {
/*  506 */               decodedData >>= 4;
/*  507 */               buffer[outputPtr++] = (byte)decodedData;
/*  508 */               if (b64variant.usesPadding()) {
/*  509 */                 _handleBase64MissingPadding(b64variant);
/*      */               }
/*      */               break;
/*      */             } 
/*  513 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/*  515 */           if (bits == -2) {
/*      */             
/*  517 */             ch = this._inputData.readUnsignedByte();
/*  518 */             if (!b64variant.usesPaddingChar(ch) && (
/*  519 */               ch != 92 || 
/*  520 */               _decodeBase64Escape(b64variant, ch, 3) != -2)) {
/*  521 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/*  525 */             decodedData >>= 4;
/*  526 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  531 */         decodedData = decodedData << 6 | bits;
/*      */         
/*  533 */         ch = this._inputData.readUnsignedByte();
/*  534 */         bits = b64variant.decodeBase64Char(ch);
/*  535 */         if (bits < 0) {
/*  536 */           if (bits != -2) {
/*      */             
/*  538 */             if (ch == 34) {
/*  539 */               decodedData >>= 2;
/*  540 */               buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  541 */               buffer[outputPtr++] = (byte)decodedData;
/*  542 */               if (b64variant.usesPadding()) {
/*  543 */                 _handleBase64MissingPadding(b64variant);
/*      */               }
/*      */               break;
/*      */             } 
/*  547 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/*  549 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  556 */             decodedData >>= 2;
/*  557 */             buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  558 */             buffer[outputPtr++] = (byte)decodedData;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*  563 */         decodedData = decodedData << 6 | bits;
/*  564 */         buffer[outputPtr++] = (byte)(decodedData >> 16);
/*  565 */         buffer[outputPtr++] = (byte)(decodedData >> 8);
/*  566 */         buffer[outputPtr++] = (byte)decodedData;
/*      */       } 
/*  568 */     }  this._tokenIncomplete = false;
/*  569 */     if (outputPtr > 0) {
/*  570 */       outputCount += outputPtr;
/*  571 */       out.write(buffer, 0, outputPtr);
/*      */     } 
/*  573 */     return outputCount;
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
/*      */   public JsonToken nextToken() throws IOException {
/*  589 */     if (this._closed) {
/*  590 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  596 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  597 */       return _nextAfterName();
/*      */     }
/*      */ 
/*      */     
/*  601 */     this._numTypesValid = 0;
/*  602 */     if (this._tokenIncomplete) {
/*  603 */       _skipString();
/*      */     }
/*  605 */     int i = _skipWSOrEnd();
/*  606 */     if (i < 0) {
/*      */       
/*  608 */       close();
/*  609 */       return this._currToken = null;
/*      */     } 
/*      */     
/*  612 */     this._binaryValue = null;
/*  613 */     this._tokenInputRow = this._currInputRow;
/*      */ 
/*      */     
/*  616 */     if (i == 93 || i == 125) {
/*  617 */       _closeScope(i);
/*  618 */       return this._currToken;
/*      */     } 
/*      */ 
/*      */     
/*  622 */     if (this._parsingContext.expectComma()) {
/*  623 */       if (i != 44) {
/*  624 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  626 */       i = _skipWS();
/*      */ 
/*      */       
/*  629 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  630 */         i == 93 || i == 125)) {
/*  631 */         _closeScope(i);
/*  632 */         return this._currToken;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  641 */     if (!this._parsingContext.inObject()) {
/*  642 */       return _nextTokenNotInObject(i);
/*      */     }
/*      */     
/*  645 */     String n = _parseName(i);
/*  646 */     this._parsingContext.setCurrentName(n);
/*  647 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/*  649 */     i = _skipColon();
/*      */ 
/*      */     
/*  652 */     if (i == 34) {
/*  653 */       this._tokenIncomplete = true;
/*  654 */       this._nextToken = JsonToken.VALUE_STRING;
/*  655 */       return this._currToken;
/*      */     } 
/*      */ 
/*      */     
/*  659 */     switch (i)
/*      */     { case 45:
/*  661 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  704 */         this._nextToken = t;
/*  705 */         return this._currToken;case 46: t = _parseFloatThatStartsWithPeriod(); this._nextToken = t; return this._currToken;case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return this._currToken;case 102: _matchToken("false", 1); t = JsonToken.VALUE_FALSE; this._nextToken = t; return this._currToken;case 110: _matchToken("null", 1); t = JsonToken.VALUE_NULL; this._nextToken = t; return this._currToken;case 116: _matchToken("true", 1); t = JsonToken.VALUE_TRUE; this._nextToken = t; return this._currToken;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return this._currToken;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return this._currToken; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return this._currToken;
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextTokenNotInObject(int i) throws IOException {
/*  710 */     if (i == 34) {
/*  711 */       this._tokenIncomplete = true;
/*  712 */       return this._currToken = JsonToken.VALUE_STRING;
/*      */     } 
/*  714 */     switch (i) {
/*      */       case 91:
/*  716 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  717 */         return this._currToken = JsonToken.START_ARRAY;
/*      */       case 123:
/*  719 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*  720 */         return this._currToken = JsonToken.START_OBJECT;
/*      */       case 116:
/*  722 */         _matchToken("true", 1);
/*  723 */         return this._currToken = JsonToken.VALUE_TRUE;
/*      */       case 102:
/*  725 */         _matchToken("false", 1);
/*  726 */         return this._currToken = JsonToken.VALUE_FALSE;
/*      */       case 110:
/*  728 */         _matchToken("null", 1);
/*  729 */         return this._currToken = JsonToken.VALUE_NULL;
/*      */       case 45:
/*  731 */         return this._currToken = _parseNegNumber();
/*      */ 
/*      */ 
/*      */       
/*      */       case 46:
/*  736 */         return this._currToken = _parseFloatThatStartsWithPeriod();
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
/*  747 */         return this._currToken = _parsePosNumber(i);
/*      */     } 
/*  749 */     return this._currToken = _handleUnexpectedValue(i);
/*      */   }
/*      */ 
/*      */   
/*      */   private final JsonToken _nextAfterName() {
/*  754 */     this._nameCopied = false;
/*  755 */     JsonToken t = this._nextToken;
/*  756 */     this._nextToken = null;
/*      */ 
/*      */     
/*  759 */     if (t == JsonToken.START_ARRAY) {
/*  760 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  761 */     } else if (t == JsonToken.START_OBJECT) {
/*  762 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     } 
/*  764 */     return this._currToken = t;
/*      */   }
/*      */ 
/*      */   
/*      */   public void finishToken() throws IOException {
/*  769 */     if (this._tokenIncomplete) {
/*  770 */       this._tokenIncomplete = false;
/*  771 */       _finishString();
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
/*      */   public String nextFieldName() throws IOException {
/*  789 */     this._numTypesValid = 0;
/*  790 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  791 */       _nextAfterName();
/*  792 */       return null;
/*      */     } 
/*  794 */     if (this._tokenIncomplete) {
/*  795 */       _skipString();
/*      */     }
/*  797 */     int i = _skipWS();
/*  798 */     this._binaryValue = null;
/*  799 */     this._tokenInputRow = this._currInputRow;
/*      */     
/*  801 */     if (i == 93 || i == 125) {
/*  802 */       _closeScope(i);
/*  803 */       return null;
/*      */     } 
/*      */ 
/*      */     
/*  807 */     if (this._parsingContext.expectComma()) {
/*  808 */       if (i != 44) {
/*  809 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
/*      */       }
/*  811 */       i = _skipWS();
/*      */ 
/*      */       
/*  814 */       if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/*  815 */         i == 93 || i == 125)) {
/*  816 */         _closeScope(i);
/*  817 */         return null;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  822 */     if (!this._parsingContext.inObject()) {
/*  823 */       _nextTokenNotInObject(i);
/*  824 */       return null;
/*      */     } 
/*      */     
/*  827 */     String nameStr = _parseName(i);
/*  828 */     this._parsingContext.setCurrentName(nameStr);
/*  829 */     this._currToken = JsonToken.FIELD_NAME;
/*      */     
/*  831 */     i = _skipColon();
/*  832 */     if (i == 34) {
/*  833 */       this._tokenIncomplete = true;
/*  834 */       this._nextToken = JsonToken.VALUE_STRING;
/*  835 */       return nameStr;
/*      */     } 
/*      */     
/*  838 */     switch (i)
/*      */     { case 45:
/*  840 */         t = _parseNegNumber();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  878 */         this._nextToken = t;
/*  879 */         return nameStr;case 46: t = _parseFloatThatStartsWithPeriod();case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: t = _parsePosNumber(i); this._nextToken = t; return nameStr;case 102: _matchToken("false", 1); t = JsonToken.VALUE_FALSE; this._nextToken = t; return nameStr;case 110: _matchToken("null", 1); t = JsonToken.VALUE_NULL; this._nextToken = t; return nameStr;case 116: _matchToken("true", 1); t = JsonToken.VALUE_TRUE; this._nextToken = t; return nameStr;case 91: t = JsonToken.START_ARRAY; this._nextToken = t; return nameStr;case 123: t = JsonToken.START_OBJECT; this._nextToken = t; return nameStr; }  JsonToken t = _handleUnexpectedValue(i); this._nextToken = t; return nameStr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextTextValue() throws IOException {
/*  886 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  887 */       this._nameCopied = false;
/*  888 */       JsonToken t = this._nextToken;
/*  889 */       this._nextToken = null;
/*  890 */       this._currToken = t;
/*  891 */       if (t == JsonToken.VALUE_STRING) {
/*  892 */         if (this._tokenIncomplete) {
/*  893 */           this._tokenIncomplete = false;
/*  894 */           return _finishAndReturnString();
/*      */         } 
/*  896 */         return this._textBuffer.contentsAsString();
/*      */       } 
/*  898 */       if (t == JsonToken.START_ARRAY) {
/*  899 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  900 */       } else if (t == JsonToken.START_OBJECT) {
/*  901 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/*  903 */       return null;
/*      */     } 
/*  905 */     return (nextToken() == JsonToken.VALUE_STRING) ? getText() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int nextIntValue(int defaultValue) throws IOException {
/*  912 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  913 */       this._nameCopied = false;
/*  914 */       JsonToken t = this._nextToken;
/*  915 */       this._nextToken = null;
/*  916 */       this._currToken = t;
/*  917 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/*  918 */         return getIntValue();
/*      */       }
/*  920 */       if (t == JsonToken.START_ARRAY) {
/*  921 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  922 */       } else if (t == JsonToken.START_OBJECT) {
/*  923 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/*  925 */       return defaultValue;
/*      */     } 
/*  927 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getIntValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long nextLongValue(long defaultValue) throws IOException {
/*  934 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  935 */       this._nameCopied = false;
/*  936 */       JsonToken t = this._nextToken;
/*  937 */       this._nextToken = null;
/*  938 */       this._currToken = t;
/*  939 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/*  940 */         return getLongValue();
/*      */       }
/*  942 */       if (t == JsonToken.START_ARRAY) {
/*  943 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  944 */       } else if (t == JsonToken.START_OBJECT) {
/*  945 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/*  947 */       return defaultValue;
/*      */     } 
/*  949 */     return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getLongValue() : defaultValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Boolean nextBooleanValue() throws IOException {
/*  956 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  957 */       this._nameCopied = false;
/*  958 */       JsonToken jsonToken = this._nextToken;
/*  959 */       this._nextToken = null;
/*  960 */       this._currToken = jsonToken;
/*  961 */       if (jsonToken == JsonToken.VALUE_TRUE) {
/*  962 */         return Boolean.TRUE;
/*      */       }
/*  964 */       if (jsonToken == JsonToken.VALUE_FALSE) {
/*  965 */         return Boolean.FALSE;
/*      */       }
/*  967 */       if (jsonToken == JsonToken.START_ARRAY) {
/*  968 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  969 */       } else if (jsonToken == JsonToken.START_OBJECT) {
/*  970 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       } 
/*  972 */       return null;
/*      */     } 
/*      */     
/*  975 */     JsonToken t = nextToken();
/*  976 */     if (t == JsonToken.VALUE_TRUE) {
/*  977 */       return Boolean.TRUE;
/*      */     }
/*  979 */     if (t == JsonToken.VALUE_FALSE) {
/*  980 */       return Boolean.FALSE;
/*      */     }
/*  982 */     return null;
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
/*  995 */     if (!isEnabled(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
/*  996 */       return _handleUnexpectedValue(46);
/*      */     }
/*  998 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*  999 */     return _parseFloat(outBuf, 0, 46, false, 0);
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
/*      */   protected JsonToken _parsePosNumber(int c) throws IOException {
/*      */     int outPtr;
/* 1027 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1032 */     if (c == 48) {
/* 1033 */       c = _handleLeadingZeroes();
/* 1034 */       if (c <= 57 && c >= 48) {
/* 1035 */         outPtr = 0;
/*      */       } else {
/* 1037 */         outBuf[0] = '0';
/* 1038 */         outPtr = 1;
/*      */       } 
/*      */     } else {
/* 1041 */       outBuf[0] = (char)c;
/* 1042 */       c = this._inputData.readUnsignedByte();
/* 1043 */       outPtr = 1;
/*      */     } 
/* 1045 */     int intLen = outPtr;
/*      */ 
/*      */     
/* 1048 */     while (c <= 57 && c >= 48) {
/* 1049 */       intLen++;
/* 1050 */       if (outPtr >= outBuf.length) {
/* 1051 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1052 */         outPtr = 0;
/*      */       } 
/* 1054 */       outBuf[outPtr++] = (char)c;
/* 1055 */       c = this._inputData.readUnsignedByte();
/*      */     } 
/* 1057 */     if (c == 46 || c == 101 || c == 69) {
/* 1058 */       return _parseFloat(outBuf, outPtr, c, false, intLen);
/*      */     }
/* 1060 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1062 */     if (this._parsingContext.inRoot()) {
/* 1063 */       _verifyRootSpace();
/*      */     } else {
/* 1065 */       this._nextByte = c;
/*      */     } 
/*      */     
/* 1068 */     return resetInt(false, intLen);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _parseNegNumber() throws IOException {
/* 1073 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1074 */     int outPtr = 0;
/*      */ 
/*      */     
/* 1077 */     outBuf[outPtr++] = '-';
/* 1078 */     int c = this._inputData.readUnsignedByte();
/* 1079 */     outBuf[outPtr++] = (char)c;
/*      */     
/* 1081 */     if (c <= 48) {
/*      */       
/* 1083 */       if (c == 48) {
/* 1084 */         c = _handleLeadingZeroes();
/*      */       } else {
/* 1086 */         return _handleInvalidNumberStart(c, true);
/*      */       } 
/*      */     } else {
/* 1089 */       if (c > 57) {
/* 1090 */         return _handleInvalidNumberStart(c, true);
/*      */       }
/* 1092 */       c = this._inputData.readUnsignedByte();
/*      */     } 
/*      */     
/* 1095 */     int intLen = 1;
/*      */ 
/*      */     
/* 1098 */     while (c <= 57 && c >= 48) {
/* 1099 */       intLen++;
/* 1100 */       outBuf[outPtr++] = (char)c;
/* 1101 */       c = this._inputData.readUnsignedByte();
/*      */     } 
/* 1103 */     if (c == 46 || c == 101 || c == 69) {
/* 1104 */       return _parseFloat(outBuf, outPtr, c, true, intLen);
/*      */     }
/* 1106 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1108 */     this._nextByte = c;
/* 1109 */     if (this._parsingContext.inRoot()) {
/* 1110 */       _verifyRootSpace();
/*      */     }
/*      */     
/* 1113 */     return resetInt(true, intLen);
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
/*      */   private final int _handleLeadingZeroes() throws IOException {
/* 1128 */     int ch = this._inputData.readUnsignedByte();
/*      */     
/* 1130 */     if (ch < 48 || ch > 57) {
/* 1131 */       return ch;
/*      */     }
/*      */     
/* 1134 */     if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
/* 1135 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/* 1138 */     while (ch == 48) {
/* 1139 */       ch = this._inputData.readUnsignedByte();
/*      */     }
/* 1141 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException {
/* 1147 */     int fractLen = 0;
/*      */ 
/*      */     
/* 1150 */     if (c == 46) {
/* 1151 */       outBuf[outPtr++] = (char)c;
/*      */ 
/*      */       
/*      */       while (true) {
/* 1155 */         c = this._inputData.readUnsignedByte();
/* 1156 */         if (c < 48 || c > 57) {
/*      */           break;
/*      */         }
/* 1159 */         fractLen++;
/* 1160 */         if (outPtr >= outBuf.length) {
/* 1161 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1162 */           outPtr = 0;
/*      */         } 
/* 1164 */         outBuf[outPtr++] = (char)c;
/*      */       } 
/*      */       
/* 1167 */       if (fractLen == 0) {
/* 1168 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     } 
/*      */     
/* 1172 */     int expLen = 0;
/* 1173 */     if (c == 101 || c == 69) {
/* 1174 */       if (outPtr >= outBuf.length) {
/* 1175 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1176 */         outPtr = 0;
/*      */       } 
/* 1178 */       outBuf[outPtr++] = (char)c;
/* 1179 */       c = this._inputData.readUnsignedByte();
/*      */       
/* 1181 */       if (c == 45 || c == 43) {
/* 1182 */         if (outPtr >= outBuf.length) {
/* 1183 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1184 */           outPtr = 0;
/*      */         } 
/* 1186 */         outBuf[outPtr++] = (char)c;
/* 1187 */         c = this._inputData.readUnsignedByte();
/*      */       } 
/* 1189 */       while (c <= 57 && c >= 48) {
/* 1190 */         expLen++;
/* 1191 */         if (outPtr >= outBuf.length) {
/* 1192 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1193 */           outPtr = 0;
/*      */         } 
/* 1195 */         outBuf[outPtr++] = (char)c;
/* 1196 */         c = this._inputData.readUnsignedByte();
/*      */       } 
/*      */       
/* 1199 */       if (expLen == 0) {
/* 1200 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1206 */     this._nextByte = c;
/* 1207 */     if (this._parsingContext.inRoot()) {
/* 1208 */       _verifyRootSpace();
/*      */     }
/* 1210 */     this._textBuffer.setCurrentLength(outPtr);
/*      */ 
/*      */     
/* 1213 */     return resetFloat(negative, integerPartLength, fractLen, expLen);
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
/*      */   private final void _verifyRootSpace() throws IOException {
/* 1226 */     int ch = this._nextByte;
/* 1227 */     if (ch <= 32) {
/* 1228 */       this._nextByte = -1;
/* 1229 */       if (ch == 13 || ch == 10) {
/* 1230 */         this._currInputRow++;
/*      */       }
/*      */       return;
/*      */     } 
/* 1234 */     _reportMissingRootWS(ch);
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
/* 1245 */     if (i != 34) {
/* 1246 */       return _handleOddName(i);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1254 */     int[] codes = _icLatin1;
/*      */     
/* 1256 */     int q = this._inputData.readUnsignedByte();
/*      */     
/* 1258 */     if (codes[q] == 0) {
/* 1259 */       i = this._inputData.readUnsignedByte();
/* 1260 */       if (codes[i] == 0) {
/* 1261 */         q = q << 8 | i;
/* 1262 */         i = this._inputData.readUnsignedByte();
/* 1263 */         if (codes[i] == 0) {
/* 1264 */           q = q << 8 | i;
/* 1265 */           i = this._inputData.readUnsignedByte();
/* 1266 */           if (codes[i] == 0) {
/* 1267 */             q = q << 8 | i;
/* 1268 */             i = this._inputData.readUnsignedByte();
/* 1269 */             if (codes[i] == 0) {
/* 1270 */               this._quad1 = q;
/* 1271 */               return _parseMediumName(i);
/*      */             } 
/* 1273 */             if (i == 34) {
/* 1274 */               return findName(q, 4);
/*      */             }
/* 1276 */             return parseName(q, i, 4);
/*      */           } 
/* 1278 */           if (i == 34) {
/* 1279 */             return findName(q, 3);
/*      */           }
/* 1281 */           return parseName(q, i, 3);
/*      */         } 
/* 1283 */         if (i == 34) {
/* 1284 */           return findName(q, 2);
/*      */         }
/* 1286 */         return parseName(q, i, 2);
/*      */       } 
/* 1288 */       if (i == 34) {
/* 1289 */         return findName(q, 1);
/*      */       }
/* 1291 */       return parseName(q, i, 1);
/*      */     } 
/* 1293 */     if (q == 34) {
/* 1294 */       return "";
/*      */     }
/* 1296 */     return parseName(0, q, 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseMediumName(int q2) throws IOException {
/* 1301 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1304 */     int i = this._inputData.readUnsignedByte();
/* 1305 */     if (codes[i] != 0) {
/* 1306 */       if (i == 34) {
/* 1307 */         return findName(this._quad1, q2, 1);
/*      */       }
/* 1309 */       return parseName(this._quad1, q2, i, 1);
/*      */     } 
/* 1311 */     q2 = q2 << 8 | i;
/* 1312 */     i = this._inputData.readUnsignedByte();
/* 1313 */     if (codes[i] != 0) {
/* 1314 */       if (i == 34) {
/* 1315 */         return findName(this._quad1, q2, 2);
/*      */       }
/* 1317 */       return parseName(this._quad1, q2, i, 2);
/*      */     } 
/* 1319 */     q2 = q2 << 8 | i;
/* 1320 */     i = this._inputData.readUnsignedByte();
/* 1321 */     if (codes[i] != 0) {
/* 1322 */       if (i == 34) {
/* 1323 */         return findName(this._quad1, q2, 3);
/*      */       }
/* 1325 */       return parseName(this._quad1, q2, i, 3);
/*      */     } 
/* 1327 */     q2 = q2 << 8 | i;
/* 1328 */     i = this._inputData.readUnsignedByte();
/* 1329 */     if (codes[i] != 0) {
/* 1330 */       if (i == 34) {
/* 1331 */         return findName(this._quad1, q2, 4);
/*      */       }
/* 1333 */       return parseName(this._quad1, q2, i, 4);
/*      */     } 
/* 1335 */     return _parseMediumName2(i, q2);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseMediumName2(int q3, int q2) throws IOException {
/* 1340 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1343 */     int i = this._inputData.readUnsignedByte();
/* 1344 */     if (codes[i] != 0) {
/* 1345 */       if (i == 34) {
/* 1346 */         return findName(this._quad1, q2, q3, 1);
/*      */       }
/* 1348 */       return parseName(this._quad1, q2, q3, i, 1);
/*      */     } 
/* 1350 */     q3 = q3 << 8 | i;
/* 1351 */     i = this._inputData.readUnsignedByte();
/* 1352 */     if (codes[i] != 0) {
/* 1353 */       if (i == 34) {
/* 1354 */         return findName(this._quad1, q2, q3, 2);
/*      */       }
/* 1356 */       return parseName(this._quad1, q2, q3, i, 2);
/*      */     } 
/* 1358 */     q3 = q3 << 8 | i;
/* 1359 */     i = this._inputData.readUnsignedByte();
/* 1360 */     if (codes[i] != 0) {
/* 1361 */       if (i == 34) {
/* 1362 */         return findName(this._quad1, q2, q3, 3);
/*      */       }
/* 1364 */       return parseName(this._quad1, q2, q3, i, 3);
/*      */     } 
/* 1366 */     q3 = q3 << 8 | i;
/* 1367 */     i = this._inputData.readUnsignedByte();
/* 1368 */     if (codes[i] != 0) {
/* 1369 */       if (i == 34) {
/* 1370 */         return findName(this._quad1, q2, q3, 4);
/*      */       }
/* 1372 */       return parseName(this._quad1, q2, q3, i, 4);
/*      */     } 
/* 1374 */     return _parseLongName(i, q2, q3);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String _parseLongName(int q, int q2, int q3) throws IOException {
/* 1379 */     this._quadBuffer[0] = this._quad1;
/* 1380 */     this._quadBuffer[1] = q2;
/* 1381 */     this._quadBuffer[2] = q3;
/*      */ 
/*      */     
/* 1384 */     int[] codes = _icLatin1;
/* 1385 */     int qlen = 3;
/*      */     
/*      */     while (true) {
/* 1388 */       int i = this._inputData.readUnsignedByte();
/* 1389 */       if (codes[i] != 0) {
/* 1390 */         if (i == 34) {
/* 1391 */           return findName(this._quadBuffer, qlen, q, 1);
/*      */         }
/* 1393 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 1);
/*      */       } 
/*      */       
/* 1396 */       q = q << 8 | i;
/* 1397 */       i = this._inputData.readUnsignedByte();
/* 1398 */       if (codes[i] != 0) {
/* 1399 */         if (i == 34) {
/* 1400 */           return findName(this._quadBuffer, qlen, q, 2);
/*      */         }
/* 1402 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 2);
/*      */       } 
/*      */       
/* 1405 */       q = q << 8 | i;
/* 1406 */       i = this._inputData.readUnsignedByte();
/* 1407 */       if (codes[i] != 0) {
/* 1408 */         if (i == 34) {
/* 1409 */           return findName(this._quadBuffer, qlen, q, 3);
/*      */         }
/* 1411 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 3);
/*      */       } 
/*      */       
/* 1414 */       q = q << 8 | i;
/* 1415 */       i = this._inputData.readUnsignedByte();
/* 1416 */       if (codes[i] != 0) {
/* 1417 */         if (i == 34) {
/* 1418 */           return findName(this._quadBuffer, qlen, q, 4);
/*      */         }
/* 1420 */         return parseEscapedName(this._quadBuffer, qlen, q, i, 4);
/*      */       } 
/*      */ 
/*      */       
/* 1424 */       if (qlen >= this._quadBuffer.length) {
/* 1425 */         this._quadBuffer = _growArrayBy(this._quadBuffer, qlen);
/*      */       }
/* 1427 */       this._quadBuffer[qlen++] = q;
/* 1428 */       q = i;
/*      */     } 
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int ch, int lastQuadBytes) throws IOException {
/* 1433 */     return parseEscapedName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int ch, int lastQuadBytes) throws IOException {
/* 1437 */     this._quadBuffer[0] = q1;
/* 1438 */     return parseEscapedName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final String parseName(int q1, int q2, int q3, int ch, int lastQuadBytes) throws IOException {
/* 1442 */     this._quadBuffer[0] = q1;
/* 1443 */     this._quadBuffer[1] = q2;
/* 1444 */     return parseEscapedName(this._quadBuffer, 2, q3, ch, lastQuadBytes);
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
/*      */   protected final String parseEscapedName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes) throws IOException {
/* 1460 */     int[] codes = _icLatin1;
/*      */     
/*      */     while (true) {
/* 1463 */       if (codes[ch] != 0) {
/* 1464 */         if (ch == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 1468 */         if (ch != 92) {
/*      */           
/* 1470 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 1473 */           ch = _decodeEscaped();
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1480 */         if (ch > 127) {
/*      */           
/* 1482 */           if (currQuadBytes >= 4) {
/* 1483 */             if (qlen >= quads.length) {
/* 1484 */               this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */             }
/* 1486 */             quads[qlen++] = currQuad;
/* 1487 */             currQuad = 0;
/* 1488 */             currQuadBytes = 0;
/*      */           } 
/* 1490 */           if (ch < 2048) {
/* 1491 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 1492 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 1495 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 1496 */             currQuadBytes++;
/*      */             
/* 1498 */             if (currQuadBytes >= 4) {
/* 1499 */               if (qlen >= quads.length) {
/* 1500 */                 this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */               }
/* 1502 */               quads[qlen++] = currQuad;
/* 1503 */               currQuad = 0;
/* 1504 */               currQuadBytes = 0;
/*      */             } 
/* 1506 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 1507 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 1510 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 1514 */       if (currQuadBytes < 4) {
/* 1515 */         currQuadBytes++;
/* 1516 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1518 */         if (qlen >= quads.length) {
/* 1519 */           this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */         }
/* 1521 */         quads[qlen++] = currQuad;
/* 1522 */         currQuad = ch;
/* 1523 */         currQuadBytes = 1;
/*      */       } 
/* 1525 */       ch = this._inputData.readUnsignedByte();
/*      */     } 
/*      */     
/* 1528 */     if (currQuadBytes > 0) {
/* 1529 */       if (qlen >= quads.length) {
/* 1530 */         this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */       }
/* 1532 */       quads[qlen++] = pad(currQuad, currQuadBytes);
/*      */     } 
/* 1534 */     String name = this._symbols.findName(quads, qlen);
/* 1535 */     if (name == null) {
/* 1536 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 1538 */     return name;
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
/*      */   protected String _handleOddName(int ch) throws IOException {
/* 1556 */     if (ch == 39 && (this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 1557 */       return _parseAposName();
/*      */     }
/* 1559 */     if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
/* 1560 */       char c = (char)_decodeCharForError(ch);
/* 1561 */       _reportUnexpectedChar(c, "was expecting double-quote to start field name");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1567 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */     
/* 1569 */     if (codes[ch] != 0) {
/* 1570 */       _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1577 */     int[] quads = this._quadBuffer;
/* 1578 */     int qlen = 0;
/* 1579 */     int currQuad = 0;
/* 1580 */     int currQuadBytes = 0;
/*      */ 
/*      */     
/*      */     do {
/* 1584 */       if (currQuadBytes < 4) {
/* 1585 */         currQuadBytes++;
/* 1586 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1588 */         if (qlen >= quads.length) {
/* 1589 */           this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */         }
/* 1591 */         quads[qlen++] = currQuad;
/* 1592 */         currQuad = ch;
/* 1593 */         currQuadBytes = 1;
/*      */       } 
/* 1595 */       ch = this._inputData.readUnsignedByte();
/* 1596 */     } while (codes[ch] == 0);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1601 */     this._nextByte = ch;
/* 1602 */     if (currQuadBytes > 0) {
/* 1603 */       if (qlen >= quads.length) {
/* 1604 */         this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */       }
/* 1606 */       quads[qlen++] = currQuad;
/*      */     } 
/* 1608 */     String name = this._symbols.findName(quads, qlen);
/* 1609 */     if (name == null) {
/* 1610 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 1612 */     return name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _parseAposName() throws IOException {
/* 1623 */     int ch = this._inputData.readUnsignedByte();
/* 1624 */     if (ch == 39) {
/* 1625 */       return "";
/*      */     }
/* 1627 */     int[] quads = this._quadBuffer;
/* 1628 */     int qlen = 0;
/* 1629 */     int currQuad = 0;
/* 1630 */     int currQuadBytes = 0;
/*      */ 
/*      */ 
/*      */     
/* 1634 */     int[] codes = _icLatin1;
/*      */ 
/*      */     
/* 1637 */     while (ch != 39) {
/*      */ 
/*      */ 
/*      */       
/* 1641 */       if (ch != 34 && codes[ch] != 0) {
/* 1642 */         if (ch != 92) {
/*      */ 
/*      */           
/* 1645 */           _throwUnquotedSpace(ch, "name");
/*      */         } else {
/*      */           
/* 1648 */           ch = _decodeEscaped();
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1654 */         if (ch > 127) {
/*      */           
/* 1656 */           if (currQuadBytes >= 4) {
/* 1657 */             if (qlen >= quads.length) {
/* 1658 */               this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */             }
/* 1660 */             quads[qlen++] = currQuad;
/* 1661 */             currQuad = 0;
/* 1662 */             currQuadBytes = 0;
/*      */           } 
/* 1664 */           if (ch < 2048) {
/* 1665 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 1666 */             currQuadBytes++;
/*      */           } else {
/*      */             
/* 1669 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 1670 */             currQuadBytes++;
/*      */             
/* 1672 */             if (currQuadBytes >= 4) {
/* 1673 */               if (qlen >= quads.length) {
/* 1674 */                 this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */               }
/* 1676 */               quads[qlen++] = currQuad;
/* 1677 */               currQuad = 0;
/* 1678 */               currQuadBytes = 0;
/*      */             } 
/* 1680 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 1681 */             currQuadBytes++;
/*      */           } 
/*      */           
/* 1684 */           ch = 0x80 | ch & 0x3F;
/*      */         } 
/*      */       } 
/*      */       
/* 1688 */       if (currQuadBytes < 4) {
/* 1689 */         currQuadBytes++;
/* 1690 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1692 */         if (qlen >= quads.length) {
/* 1693 */           this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */         }
/* 1695 */         quads[qlen++] = currQuad;
/* 1696 */         currQuad = ch;
/* 1697 */         currQuadBytes = 1;
/*      */       } 
/* 1699 */       ch = this._inputData.readUnsignedByte();
/*      */     } 
/*      */     
/* 1702 */     if (currQuadBytes > 0) {
/* 1703 */       if (qlen >= quads.length) {
/* 1704 */         this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */       }
/* 1706 */       quads[qlen++] = pad(currQuad, currQuadBytes);
/*      */     } 
/* 1708 */     String name = this._symbols.findName(quads, qlen);
/* 1709 */     if (name == null) {
/* 1710 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 1712 */     return name;
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
/* 1723 */     q1 = pad(q1, lastQuadBytes);
/*      */     
/* 1725 */     String name = this._symbols.findName(q1);
/* 1726 */     if (name != null) {
/* 1727 */       return name;
/*      */     }
/*      */     
/* 1730 */     this._quadBuffer[0] = q1;
/* 1731 */     return addName(this._quadBuffer, 1, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
/* 1736 */     q2 = pad(q2, lastQuadBytes);
/*      */     
/* 1738 */     String name = this._symbols.findName(q1, q2);
/* 1739 */     if (name != null) {
/* 1740 */       return name;
/*      */     }
/*      */     
/* 1743 */     this._quadBuffer[0] = q1;
/* 1744 */     this._quadBuffer[1] = q2;
/* 1745 */     return addName(this._quadBuffer, 2, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException {
/* 1750 */     q3 = pad(q3, lastQuadBytes);
/* 1751 */     String name = this._symbols.findName(q1, q2, q3);
/* 1752 */     if (name != null) {
/* 1753 */       return name;
/*      */     }
/* 1755 */     int[] quads = this._quadBuffer;
/* 1756 */     quads[0] = q1;
/* 1757 */     quads[1] = q2;
/* 1758 */     quads[2] = pad(q3, lastQuadBytes);
/* 1759 */     return addName(quads, 3, lastQuadBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
/* 1764 */     if (qlen >= quads.length) {
/* 1765 */       this._quadBuffer = quads = _growArrayBy(quads, quads.length);
/*      */     }
/* 1767 */     quads[qlen++] = pad(lastQuad, lastQuadBytes);
/* 1768 */     String name = this._symbols.findName(quads, qlen);
/* 1769 */     if (name == null) {
/* 1770 */       return addName(quads, qlen, lastQuadBytes);
/*      */     }
/* 1772 */     return name;
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
/*      */   private final String addName(int[] quads, int qlen, int lastQuadBytes) throws JsonParseException {
/* 1788 */     int lastQuad, byteLen = (qlen << 2) - 4 + lastQuadBytes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1797 */     if (lastQuadBytes < 4) {
/* 1798 */       lastQuad = quads[qlen - 1];
/*      */       
/* 1800 */       quads[qlen - 1] = lastQuad << 4 - lastQuadBytes << 3;
/*      */     } else {
/* 1802 */       lastQuad = 0;
/*      */     } 
/*      */ 
/*      */     
/* 1806 */     char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1807 */     int cix = 0;
/*      */     
/* 1809 */     for (int ix = 0; ix < byteLen; ) {
/* 1810 */       int ch = quads[ix >> 2];
/* 1811 */       int byteIx = ix & 0x3;
/* 1812 */       ch = ch >> 3 - byteIx << 3 & 0xFF;
/* 1813 */       ix++;
/*      */       
/* 1815 */       if (ch > 127) {
/*      */         int needed;
/* 1817 */         if ((ch & 0xE0) == 192) {
/* 1818 */           ch &= 0x1F;
/* 1819 */           needed = 1;
/* 1820 */         } else if ((ch & 0xF0) == 224) {
/* 1821 */           ch &= 0xF;
/* 1822 */           needed = 2;
/* 1823 */         } else if ((ch & 0xF8) == 240) {
/* 1824 */           ch &= 0x7;
/* 1825 */           needed = 3;
/*      */         } else {
/* 1827 */           _reportInvalidInitial(ch);
/* 1828 */           needed = ch = 1;
/*      */         } 
/* 1830 */         if (ix + needed > byteLen) {
/* 1831 */           _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
/*      */         }
/*      */ 
/*      */         
/* 1835 */         int ch2 = quads[ix >> 2];
/* 1836 */         byteIx = ix & 0x3;
/* 1837 */         ch2 >>= 3 - byteIx << 3;
/* 1838 */         ix++;
/*      */         
/* 1840 */         if ((ch2 & 0xC0) != 128) {
/* 1841 */           _reportInvalidOther(ch2);
/*      */         }
/* 1843 */         ch = ch << 6 | ch2 & 0x3F;
/* 1844 */         if (needed > 1) {
/* 1845 */           ch2 = quads[ix >> 2];
/* 1846 */           byteIx = ix & 0x3;
/* 1847 */           ch2 >>= 3 - byteIx << 3;
/* 1848 */           ix++;
/*      */           
/* 1850 */           if ((ch2 & 0xC0) != 128) {
/* 1851 */             _reportInvalidOther(ch2);
/*      */           }
/* 1853 */           ch = ch << 6 | ch2 & 0x3F;
/* 1854 */           if (needed > 2) {
/* 1855 */             ch2 = quads[ix >> 2];
/* 1856 */             byteIx = ix & 0x3;
/* 1857 */             ch2 >>= 3 - byteIx << 3;
/* 1858 */             ix++;
/* 1859 */             if ((ch2 & 0xC0) != 128) {
/* 1860 */               _reportInvalidOther(ch2 & 0xFF);
/*      */             }
/* 1862 */             ch = ch << 6 | ch2 & 0x3F;
/*      */           } 
/*      */         } 
/* 1865 */         if (needed > 2) {
/* 1866 */           ch -= 65536;
/* 1867 */           if (cix >= cbuf.length) {
/* 1868 */             cbuf = this._textBuffer.expandCurrentSegment();
/*      */           }
/* 1870 */           cbuf[cix++] = (char)(55296 + (ch >> 10));
/* 1871 */           ch = 0xDC00 | ch & 0x3FF;
/*      */         } 
/*      */       } 
/* 1874 */       if (cix >= cbuf.length) {
/* 1875 */         cbuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1877 */       cbuf[cix++] = (char)ch;
/*      */     } 
/*      */ 
/*      */     
/* 1881 */     String baseName = new String(cbuf, 0, cix);
/*      */     
/* 1883 */     if (lastQuadBytes < 4) {
/* 1884 */       quads[qlen - 1] = lastQuad;
/*      */     }
/* 1886 */     return this._symbols.addName(baseName, quads, qlen);
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
/*      */   protected void _finishString() throws IOException {
/* 1898 */     int outPtr = 0;
/* 1899 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1900 */     int[] codes = _icUTF8;
/* 1901 */     int outEnd = outBuf.length;
/*      */     
/*      */     while (true) {
/* 1904 */       int c = this._inputData.readUnsignedByte();
/* 1905 */       if (codes[c] != 0) {
/* 1906 */         if (c == 34) {
/* 1907 */           this._textBuffer.setCurrentLength(outPtr);
/*      */           return;
/*      */         } 
/* 1910 */         _finishString2(outBuf, outPtr, c);
/*      */         return;
/*      */       } 
/* 1913 */       outBuf[outPtr++] = (char)c;
/* 1914 */       if (outPtr >= outEnd) {
/* 1915 */         _finishString2(outBuf, outPtr, this._inputData.readUnsignedByte());
/*      */         return;
/*      */       } 
/*      */     } 
/*      */   } private String _finishAndReturnString() throws IOException {
/* 1920 */     int outPtr = 0;
/* 1921 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1922 */     int[] codes = _icUTF8;
/* 1923 */     int outEnd = outBuf.length;
/*      */     
/*      */     while (true) {
/* 1926 */       int c = this._inputData.readUnsignedByte();
/* 1927 */       if (codes[c] != 0) {
/* 1928 */         if (c == 34) {
/* 1929 */           return this._textBuffer.setCurrentAndReturn(outPtr);
/*      */         }
/* 1931 */         _finishString2(outBuf, outPtr, c);
/* 1932 */         return this._textBuffer.contentsAsString();
/*      */       } 
/* 1934 */       outBuf[outPtr++] = (char)c;
/* 1935 */       if (outPtr >= outEnd) {
/* 1936 */         _finishString2(outBuf, outPtr, this._inputData.readUnsignedByte());
/* 1937 */         return this._textBuffer.contentsAsString();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _finishString2(char[] outBuf, int outPtr, int c) throws IOException {
/* 1944 */     int[] codes = _icUTF8;
/* 1945 */     int outEnd = outBuf.length;
/*      */ 
/*      */     
/* 1948 */     for (;; c = this._inputData.readUnsignedByte()) {
/*      */       
/* 1950 */       while (codes[c] == 0) {
/* 1951 */         if (outPtr >= outEnd) {
/* 1952 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1953 */           outPtr = 0;
/* 1954 */           outEnd = outBuf.length;
/*      */         } 
/* 1956 */         outBuf[outPtr++] = (char)c;
/* 1957 */         c = this._inputData.readUnsignedByte();
/*      */       } 
/*      */       
/* 1960 */       if (c == 34) {
/*      */         break;
/*      */       }
/* 1963 */       switch (codes[c]) {
/*      */         case 1:
/* 1965 */           c = _decodeEscaped();
/*      */           break;
/*      */         case 2:
/* 1968 */           c = _decodeUtf8_2(c);
/*      */           break;
/*      */         case 3:
/* 1971 */           c = _decodeUtf8_3(c);
/*      */           break;
/*      */         case 4:
/* 1974 */           c = _decodeUtf8_4(c);
/*      */           
/* 1976 */           if (outPtr >= outBuf.length) {
/* 1977 */             outBuf = this._textBuffer.finishCurrentSegment();
/* 1978 */             outPtr = 0;
/* 1979 */             outEnd = outBuf.length;
/*      */           } 
/* 1981 */           outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 1982 */           c = 0xDC00 | c & 0x3FF;
/*      */           break;
/*      */         
/*      */         default:
/* 1986 */           if (c < 32) {
/* 1987 */             _throwUnquotedSpace(c, "string value");
/*      */             break;
/*      */           } 
/* 1990 */           _reportInvalidChar(c);
/*      */           break;
/*      */       } 
/*      */       
/* 1994 */       if (outPtr >= outBuf.length) {
/* 1995 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1996 */         outPtr = 0;
/* 1997 */         outEnd = outBuf.length;
/*      */       } 
/*      */       
/* 2000 */       outBuf[outPtr++] = (char)c;
/*      */     } 
/* 2002 */     this._textBuffer.setCurrentLength(outPtr);
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
/* 2015 */     this._tokenIncomplete = false;
/*      */ 
/*      */     
/* 2018 */     int[] codes = _icUTF8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2026 */       int c = this._inputData.readUnsignedByte();
/* 2027 */       if (codes[c] != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2032 */         if (c == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 2036 */         switch (codes[c]) {
/*      */           case 1:
/* 2038 */             _decodeEscaped();
/*      */             continue;
/*      */           case 2:
/* 2041 */             _skipUtf8_2();
/*      */             continue;
/*      */           case 3:
/* 2044 */             _skipUtf8_3();
/*      */             continue;
/*      */           case 4:
/* 2047 */             _skipUtf8_4();
/*      */             continue;
/*      */         } 
/* 2050 */         if (c < 32) {
/* 2051 */           _throwUnquotedSpace(c, "string value");
/*      */           continue;
/*      */         } 
/* 2054 */         _reportInvalidChar(c);
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
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _handleUnexpectedValue(int c) throws IOException {
/* 2075 */     switch (c) {
/*      */       case 93:
/* 2077 */         if (!this._parsingContext.inArray()) {
/*      */           break;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 44:
/* 2086 */         if (!this._parsingContext.inRoot() && (
/* 2087 */           this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
/*      */           
/* 2089 */           this._nextByte = c;
/* 2090 */           return JsonToken.VALUE_NULL;
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 125:
/* 2097 */         _reportUnexpectedChar(c, "expected a value");
/*      */       case 39:
/* 2099 */         if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
/* 2100 */           return _handleApos();
/*      */         }
/*      */         break;
/*      */       case 78:
/* 2104 */         _matchToken("NaN", 1);
/* 2105 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2106 */           return resetAsNaN("NaN", Double.NaN);
/*      */         }
/* 2108 */         _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 73:
/* 2111 */         _matchToken("Infinity", 1);
/* 2112 */         if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2113 */           return resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
/*      */         }
/* 2115 */         _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */         break;
/*      */       case 43:
/* 2118 */         return _handleInvalidNumberStart(this._inputData.readUnsignedByte(), false);
/*      */     } 
/*      */     
/* 2121 */     if (Character.isJavaIdentifierStart(c)) {
/* 2122 */       _reportInvalidToken(c, "" + (char)c, _validJsonTokenList());
/*      */     }
/*      */     
/* 2125 */     _reportUnexpectedChar(c, "expected a valid value " + _validJsonValueList());
/* 2126 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonToken _handleApos() throws IOException {
/* 2131 */     int c = 0;
/*      */     
/* 2133 */     int outPtr = 0;
/* 2134 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */ 
/*      */     
/* 2137 */     int[] codes = _icUTF8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     label36: while (true) {
/* 2144 */       int outEnd = outBuf.length;
/* 2145 */       if (outPtr >= outBuf.length) {
/* 2146 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2147 */         outPtr = 0;
/* 2148 */         outEnd = outBuf.length;
/*      */       } 
/*      */       while (true)
/* 2151 */       { c = this._inputData.readUnsignedByte();
/* 2152 */         if (c == 39) {
/*      */           break;
/*      */         }
/* 2155 */         if (codes[c] != 0 && c != 34)
/*      */         
/*      */         { 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2164 */           switch (codes[c]) {
/*      */             case 1:
/* 2166 */               c = _decodeEscaped();
/*      */               break;
/*      */             case 2:
/* 2169 */               c = _decodeUtf8_2(c);
/*      */               break;
/*      */             case 3:
/* 2172 */               c = _decodeUtf8_3(c);
/*      */               break;
/*      */             case 4:
/* 2175 */               c = _decodeUtf8_4(c);
/*      */               
/* 2177 */               outBuf[outPtr++] = (char)(0xD800 | c >> 10);
/* 2178 */               if (outPtr >= outBuf.length) {
/* 2179 */                 outBuf = this._textBuffer.finishCurrentSegment();
/* 2180 */                 outPtr = 0;
/*      */               } 
/* 2182 */               c = 0xDC00 | c & 0x3FF;
/*      */               break;
/*      */             
/*      */             default:
/* 2186 */               if (c < 32) {
/* 2187 */                 _throwUnquotedSpace(c, "string value");
/*      */               }
/*      */               
/* 2190 */               _reportInvalidChar(c);
/*      */               break;
/*      */           } 
/* 2193 */           if (outPtr >= outBuf.length) {
/* 2194 */             outBuf = this._textBuffer.finishCurrentSegment();
/* 2195 */             outPtr = 0;
/*      */           } 
/*      */           
/* 2198 */           outBuf[outPtr++] = (char)c; continue; }  outBuf[outPtr++] = (char)c; if (outPtr >= outEnd)
/*      */           continue label36;  }  break;
/* 2200 */     }  this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 2202 */     return JsonToken.VALUE_STRING;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean neg) throws IOException {
/* 2212 */     while (ch == 73) {
/* 2213 */       String match; ch = this._inputData.readUnsignedByte();
/*      */       
/* 2215 */       if (ch == 78) {
/* 2216 */         match = neg ? "-INF" : "+INF";
/* 2217 */       } else if (ch == 110) {
/* 2218 */         match = neg ? "-Infinity" : "+Infinity";
/*      */       } else {
/*      */         break;
/*      */       } 
/* 2222 */       _matchToken(match, 3);
/* 2223 */       if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
/* 2224 */         return resetAsNaN(match, neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */       }
/* 2226 */       _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */     } 
/* 2228 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 2229 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _matchToken(String matchStr, int i) throws IOException {
/* 2234 */     int len = matchStr.length();
/*      */     do {
/* 2236 */       int j = this._inputData.readUnsignedByte();
/* 2237 */       if (j == matchStr.charAt(i))
/* 2238 */         continue;  _reportInvalidToken(j, matchStr.substring(0, i));
/*      */     }
/* 2240 */     while (++i < len);
/*      */     
/* 2242 */     int ch = this._inputData.readUnsignedByte();
/* 2243 */     if (ch >= 48 && ch != 93 && ch != 125) {
/* 2244 */       _checkMatchEnd(matchStr, i, ch);
/*      */     }
/* 2246 */     this._nextByte = ch;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _checkMatchEnd(String matchStr, int i, int ch) throws IOException {
/* 2251 */     char c = (char)_decodeCharForError(ch);
/* 2252 */     if (Character.isJavaIdentifierPart(c)) {
/* 2253 */       _reportInvalidToken(c, matchStr.substring(0, i));
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
/* 2265 */     int i = this._nextByte;
/* 2266 */     if (i < 0) {
/* 2267 */       i = this._inputData.readUnsignedByte();
/*      */     } else {
/* 2269 */       this._nextByte = -1;
/*      */     } 
/*      */     while (true) {
/* 2272 */       if (i > 32) {
/* 2273 */         if (i == 47 || i == 35) {
/* 2274 */           return _skipWSComment(i);
/*      */         }
/* 2276 */         return i;
/*      */       } 
/*      */ 
/*      */       
/* 2280 */       if (i == 13 || i == 10) {
/* 2281 */         this._currInputRow++;
/*      */       }
/*      */       
/* 2284 */       i = this._inputData.readUnsignedByte();
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
/*      */   private final int _skipWSOrEnd() throws IOException {
/* 2296 */     int i = this._nextByte;
/* 2297 */     if (i < 0) {
/*      */       try {
/* 2299 */         i = this._inputData.readUnsignedByte();
/* 2300 */       } catch (EOFException e) {
/* 2301 */         return _eofAsNextChar();
/*      */       } 
/*      */     } else {
/* 2304 */       this._nextByte = -1;
/*      */     } 
/*      */     while (true) {
/* 2307 */       if (i > 32) {
/* 2308 */         if (i == 47 || i == 35) {
/* 2309 */           return _skipWSComment(i);
/*      */         }
/* 2311 */         return i;
/*      */       } 
/*      */ 
/*      */       
/* 2315 */       if (i == 13 || i == 10) {
/* 2316 */         this._currInputRow++;
/*      */       }
/*      */       
/*      */       try {
/* 2320 */         i = this._inputData.readUnsignedByte();
/* 2321 */       } catch (EOFException e) {
/* 2322 */         return _eofAsNextChar();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipWSComment(int i) throws IOException {
/*      */     while (true) {
/* 2330 */       if (i > 32) {
/* 2331 */         if (i == 47) {
/* 2332 */           _skipComment();
/* 2333 */         } else if (i == 35) {
/* 2334 */           if (!_skipYAMLComment()) {
/* 2335 */             return i;
/*      */           }
/*      */         } else {
/* 2338 */           return i;
/*      */         
/*      */         }
/*      */       
/*      */       }
/* 2343 */       else if (i == 13 || i == 10) {
/* 2344 */         this._currInputRow++;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2352 */       i = this._inputData.readUnsignedByte();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon() throws IOException {
/* 2358 */     int i = this._nextByte;
/* 2359 */     if (i < 0) {
/* 2360 */       i = this._inputData.readUnsignedByte();
/*      */     } else {
/* 2362 */       this._nextByte = -1;
/*      */     } 
/*      */     
/* 2365 */     if (i == 58) {
/* 2366 */       i = this._inputData.readUnsignedByte();
/* 2367 */       if (i > 32) {
/* 2368 */         if (i == 47 || i == 35) {
/* 2369 */           return _skipColon2(i, true);
/*      */         }
/* 2371 */         return i;
/*      */       } 
/* 2373 */       if (i == 32 || i == 9) {
/* 2374 */         i = this._inputData.readUnsignedByte();
/* 2375 */         if (i > 32) {
/* 2376 */           if (i == 47 || i == 35) {
/* 2377 */             return _skipColon2(i, true);
/*      */           }
/* 2379 */           return i;
/*      */         } 
/*      */       } 
/* 2382 */       return _skipColon2(i, true);
/*      */     } 
/* 2384 */     if (i == 32 || i == 9) {
/* 2385 */       i = this._inputData.readUnsignedByte();
/*      */     }
/* 2387 */     if (i == 58) {
/* 2388 */       i = this._inputData.readUnsignedByte();
/* 2389 */       if (i > 32) {
/* 2390 */         if (i == 47 || i == 35) {
/* 2391 */           return _skipColon2(i, true);
/*      */         }
/* 2393 */         return i;
/*      */       } 
/* 2395 */       if (i == 32 || i == 9) {
/* 2396 */         i = this._inputData.readUnsignedByte();
/* 2397 */         if (i > 32) {
/* 2398 */           if (i == 47 || i == 35) {
/* 2399 */             return _skipColon2(i, true);
/*      */           }
/* 2401 */           return i;
/*      */         } 
/*      */       } 
/* 2404 */       return _skipColon2(i, true);
/*      */     } 
/* 2406 */     return _skipColon2(i, false);
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _skipColon2(int i, boolean gotColon) throws IOException {
/* 2411 */     for (;; i = this._inputData.readUnsignedByte()) {
/* 2412 */       if (i > 32) {
/* 2413 */         if (i == 47) {
/* 2414 */           _skipComment();
/*      */         
/*      */         }
/* 2417 */         else if (i != 35 || 
/* 2418 */           !_skipYAMLComment()) {
/*      */ 
/*      */ 
/*      */           
/* 2422 */           if (gotColon) {
/* 2423 */             return i;
/*      */           }
/* 2425 */           if (i != 58) {
/* 2426 */             _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */           }
/* 2428 */           gotColon = true;
/*      */         }
/*      */       
/*      */       }
/* 2432 */       else if (i == 13 || i == 10) {
/* 2433 */         this._currInputRow++;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipComment() throws IOException {
/* 2441 */     if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
/* 2442 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/* 2444 */     int c = this._inputData.readUnsignedByte();
/* 2445 */     if (c == 47) {
/* 2446 */       _skipLine();
/* 2447 */     } else if (c == 42) {
/* 2448 */       _skipCComment();
/*      */     } else {
/* 2450 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipCComment() throws IOException {
/* 2457 */     int[] codes = CharTypes.getInputCodeComment();
/* 2458 */     int i = this._inputData.readUnsignedByte();
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2463 */       int code = codes[i];
/* 2464 */       if (code != 0)
/* 2465 */         switch (code) {
/*      */           case 42:
/* 2467 */             i = this._inputData.readUnsignedByte();
/* 2468 */             if (i == 47) {
/*      */               return;
/*      */             }
/*      */             continue;
/*      */           case 10:
/*      */           case 13:
/* 2474 */             this._currInputRow++;
/*      */             break;
/*      */           case 2:
/* 2477 */             _skipUtf8_2();
/*      */             break;
/*      */           case 3:
/* 2480 */             _skipUtf8_3();
/*      */             break;
/*      */           case 4:
/* 2483 */             _skipUtf8_4();
/*      */             break;
/*      */           
/*      */           default:
/* 2487 */             _reportInvalidChar(i);
/*      */             break;
/*      */         }  
/* 2490 */       i = this._inputData.readUnsignedByte();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final boolean _skipYAMLComment() throws IOException {
/* 2496 */     if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
/* 2497 */       return false;
/*      */     }
/* 2499 */     _skipLine();
/* 2500 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipLine() throws IOException {
/* 2510 */     int[] codes = CharTypes.getInputCodeComment();
/*      */     while (true) {
/* 2512 */       int i = this._inputData.readUnsignedByte();
/* 2513 */       int code = codes[i];
/* 2514 */       if (code != 0) {
/* 2515 */         switch (code) {
/*      */           case 10:
/*      */           case 13:
/* 2518 */             this._currInputRow++;
/*      */             return;
/*      */           case 42:
/*      */             continue;
/*      */           case 2:
/* 2523 */             _skipUtf8_2();
/*      */             continue;
/*      */           case 3:
/* 2526 */             _skipUtf8_3();
/*      */             continue;
/*      */           case 4:
/* 2529 */             _skipUtf8_4();
/*      */             continue;
/*      */         } 
/* 2532 */         if (code < 0)
/*      */         {
/* 2534 */           _reportInvalidChar(i);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _decodeEscaped() throws IOException {
/* 2544 */     int c = this._inputData.readUnsignedByte();
/*      */     
/* 2546 */     switch (c) {
/*      */       
/*      */       case 98:
/* 2549 */         return '\b';
/*      */       case 116:
/* 2551 */         return '\t';
/*      */       case 110:
/* 2553 */         return '\n';
/*      */       case 102:
/* 2555 */         return '\f';
/*      */       case 114:
/* 2557 */         return '\r';
/*      */ 
/*      */       
/*      */       case 34:
/*      */       case 47:
/*      */       case 92:
/* 2563 */         return (char)c;
/*      */       
/*      */       case 117:
/*      */         break;
/*      */       
/*      */       default:
/* 2569 */         return _handleUnrecognizedCharacterEscape((char)_decodeCharForError(c));
/*      */     } 
/*      */ 
/*      */     
/* 2573 */     int value = 0;
/* 2574 */     for (int i = 0; i < 4; i++) {
/* 2575 */       int ch = this._inputData.readUnsignedByte();
/* 2576 */       int digit = CharTypes.charToHex(ch);
/* 2577 */       if (digit < 0) {
/* 2578 */         _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
/*      */       }
/* 2580 */       value = value << 4 | digit;
/*      */     } 
/* 2582 */     return (char)value;
/*      */   }
/*      */ 
/*      */   
/*      */   protected int _decodeCharForError(int firstByte) throws IOException {
/* 2587 */     int c = firstByte & 0xFF;
/* 2588 */     if (c > 127) {
/*      */       int needed;
/*      */ 
/*      */       
/* 2592 */       if ((c & 0xE0) == 192) {
/* 2593 */         c &= 0x1F;
/* 2594 */         needed = 1;
/* 2595 */       } else if ((c & 0xF0) == 224) {
/* 2596 */         c &= 0xF;
/* 2597 */         needed = 2;
/* 2598 */       } else if ((c & 0xF8) == 240) {
/*      */         
/* 2600 */         c &= 0x7;
/* 2601 */         needed = 3;
/*      */       } else {
/* 2603 */         _reportInvalidInitial(c & 0xFF);
/* 2604 */         needed = 1;
/*      */       } 
/*      */       
/* 2607 */       int d = this._inputData.readUnsignedByte();
/* 2608 */       if ((d & 0xC0) != 128) {
/* 2609 */         _reportInvalidOther(d & 0xFF);
/*      */       }
/* 2611 */       c = c << 6 | d & 0x3F;
/*      */       
/* 2613 */       if (needed > 1) {
/* 2614 */         d = this._inputData.readUnsignedByte();
/* 2615 */         if ((d & 0xC0) != 128) {
/* 2616 */           _reportInvalidOther(d & 0xFF);
/*      */         }
/* 2618 */         c = c << 6 | d & 0x3F;
/* 2619 */         if (needed > 2) {
/* 2620 */           d = this._inputData.readUnsignedByte();
/* 2621 */           if ((d & 0xC0) != 128) {
/* 2622 */             _reportInvalidOther(d & 0xFF);
/*      */           }
/* 2624 */           c = c << 6 | d & 0x3F;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2628 */     return c;
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
/* 2639 */     int d = this._inputData.readUnsignedByte();
/* 2640 */     if ((d & 0xC0) != 128) {
/* 2641 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2643 */     return (c & 0x1F) << 6 | d & 0x3F;
/*      */   }
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_3(int c1) throws IOException {
/* 2648 */     c1 &= 0xF;
/* 2649 */     int d = this._inputData.readUnsignedByte();
/* 2650 */     if ((d & 0xC0) != 128) {
/* 2651 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2653 */     int c = c1 << 6 | d & 0x3F;
/* 2654 */     d = this._inputData.readUnsignedByte();
/* 2655 */     if ((d & 0xC0) != 128) {
/* 2656 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2658 */     c = c << 6 | d & 0x3F;
/* 2659 */     return c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _decodeUtf8_4(int c) throws IOException {
/* 2668 */     int d = this._inputData.readUnsignedByte();
/* 2669 */     if ((d & 0xC0) != 128) {
/* 2670 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2672 */     c = (c & 0x7) << 6 | d & 0x3F;
/* 2673 */     d = this._inputData.readUnsignedByte();
/* 2674 */     if ((d & 0xC0) != 128) {
/* 2675 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2677 */     c = c << 6 | d & 0x3F;
/* 2678 */     d = this._inputData.readUnsignedByte();
/* 2679 */     if ((d & 0xC0) != 128) {
/* 2680 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2686 */     return (c << 6 | d & 0x3F) - 65536;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_2() throws IOException {
/* 2691 */     int c = this._inputData.readUnsignedByte();
/* 2692 */     if ((c & 0xC0) != 128) {
/* 2693 */       _reportInvalidOther(c & 0xFF);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_3() throws IOException {
/* 2703 */     int c = this._inputData.readUnsignedByte();
/* 2704 */     if ((c & 0xC0) != 128) {
/* 2705 */       _reportInvalidOther(c & 0xFF);
/*      */     }
/* 2707 */     c = this._inputData.readUnsignedByte();
/* 2708 */     if ((c & 0xC0) != 128) {
/* 2709 */       _reportInvalidOther(c & 0xFF);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _skipUtf8_4() throws IOException {
/* 2715 */     int d = this._inputData.readUnsignedByte();
/* 2716 */     if ((d & 0xC0) != 128) {
/* 2717 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2719 */     d = this._inputData.readUnsignedByte();
/* 2720 */     if ((d & 0xC0) != 128) {
/* 2721 */       _reportInvalidOther(d & 0xFF);
/*      */     }
/* 2723 */     d = this._inputData.readUnsignedByte();
/* 2724 */     if ((d & 0xC0) != 128) {
/* 2725 */       _reportInvalidOther(d & 0xFF);
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
/*      */   protected void _reportInvalidToken(int ch, String matchedPart) throws IOException {
/* 2737 */     _reportInvalidToken(ch, matchedPart, _validJsonTokenList());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidToken(int ch, String matchedPart, String msg) throws IOException {
/* 2743 */     StringBuilder sb = new StringBuilder(matchedPart);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2750 */       char c = (char)_decodeCharForError(ch);
/* 2751 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 2754 */       sb.append(c);
/* 2755 */       ch = this._inputData.readUnsignedByte();
/*      */     } 
/* 2757 */     _reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidChar(int c) throws JsonParseException {
/* 2764 */     if (c < 32) {
/* 2765 */       _throwInvalidSpace(c);
/*      */     }
/* 2767 */     _reportInvalidInitial(c);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportInvalidInitial(int mask) throws JsonParseException {
/* 2773 */     _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _reportInvalidOther(int mask) throws JsonParseException {
/* 2779 */     _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */ 
/*      */   
/*      */   private static int[] _growArrayBy(int[] arr, int more) {
/* 2784 */     if (arr == null) {
/* 2785 */       return new int[more];
/*      */     }
/* 2787 */     return Arrays.copyOf(arr, arr.length + more);
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
/* 2810 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2817 */       int ch = this._inputData.readUnsignedByte();
/* 2818 */       if (ch > 32) {
/* 2819 */         int bits = b64variant.decodeBase64Char(ch);
/* 2820 */         if (bits < 0) {
/* 2821 */           if (ch == 34) {
/* 2822 */             return builder.toByteArray();
/*      */           }
/* 2824 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 2825 */           if (bits < 0) {
/*      */             continue;
/*      */           }
/*      */         } 
/* 2829 */         int decodedData = bits;
/*      */ 
/*      */         
/* 2832 */         ch = this._inputData.readUnsignedByte();
/* 2833 */         bits = b64variant.decodeBase64Char(ch);
/* 2834 */         if (bits < 0) {
/* 2835 */           bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */         }
/* 2837 */         decodedData = decodedData << 6 | bits;
/*      */         
/* 2839 */         ch = this._inputData.readUnsignedByte();
/* 2840 */         bits = b64variant.decodeBase64Char(ch);
/*      */ 
/*      */         
/* 2843 */         if (bits < 0) {
/* 2844 */           if (bits != -2) {
/*      */             
/* 2846 */             if (ch == 34) {
/* 2847 */               decodedData >>= 4;
/* 2848 */               builder.append(decodedData);
/* 2849 */               if (b64variant.usesPadding()) {
/* 2850 */                 _handleBase64MissingPadding(b64variant);
/*      */               }
/* 2852 */               return builder.toByteArray();
/*      */             } 
/* 2854 */             bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */           } 
/* 2856 */           if (bits == -2) {
/* 2857 */             ch = this._inputData.readUnsignedByte();
/* 2858 */             if (!b64variant.usesPaddingChar(ch) && (
/* 2859 */               ch != 92 || 
/* 2860 */               _decodeBase64Escape(b64variant, ch, 3) != -2)) {
/* 2861 */               throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */             }
/*      */ 
/*      */             
/* 2865 */             decodedData >>= 4;
/* 2866 */             builder.append(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/* 2871 */         decodedData = decodedData << 6 | bits;
/*      */         
/* 2873 */         ch = this._inputData.readUnsignedByte();
/* 2874 */         bits = b64variant.decodeBase64Char(ch);
/* 2875 */         if (bits < 0) {
/* 2876 */           if (bits != -2) {
/*      */             
/* 2878 */             if (ch == 34) {
/* 2879 */               decodedData >>= 2;
/* 2880 */               builder.appendTwoBytes(decodedData);
/* 2881 */               if (b64variant.usesPadding()) {
/* 2882 */                 _handleBase64MissingPadding(b64variant);
/*      */               }
/* 2884 */               return builder.toByteArray();
/*      */             } 
/* 2886 */             bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */           } 
/* 2888 */           if (bits == -2) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2895 */             decodedData >>= 2;
/* 2896 */             builder.appendTwoBytes(decodedData);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/* 2901 */         decodedData = decodedData << 6 | bits;
/* 2902 */         builder.appendThreeBytes(decodedData);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getTokenLocation() {
/* 2924 */     return new JsonLocation(_contentReference(), -1L, -1L, this._tokenInputRow, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonLocation getCurrentLocation() {
/* 2930 */     int col = -1;
/* 2931 */     return new JsonLocation(_contentReference(), -1L, -1L, this._currInputRow, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _closeScope(int i) throws JsonParseException {
/* 2942 */     if (i == 93) {
/* 2943 */       if (!this._parsingContext.inArray()) {
/* 2944 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/* 2946 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 2947 */       this._currToken = JsonToken.END_ARRAY;
/*      */     } 
/* 2949 */     if (i == 125) {
/* 2950 */       if (!this._parsingContext.inObject()) {
/* 2951 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/* 2953 */       this._parsingContext = this._parsingContext.clearAndGetParent();
/* 2954 */       this._currToken = JsonToken.END_OBJECT;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int pad(int q, int bytes) {
/* 2962 */     return (bytes == 4) ? q : (q | -1 << bytes << 3);
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/UTF8DataInputJsonParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */