/*      */ package com.fasterxml.jackson.core.json;
/*      */ 
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonGenerationException;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.JsonStreamContext;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.SerializableString;
/*      */ import com.fasterxml.jackson.core.io.CharTypes;
/*      */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.io.NumberOutput;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ 
/*      */ public class UTF8JsonGenerator
/*      */   extends JsonGeneratorImpl {
/*      */   private static final byte BYTE_u = 117;
/*      */   private static final byte BYTE_0 = 48;
/*      */   private static final byte BYTE_LBRACKET = 91;
/*      */   private static final byte BYTE_RBRACKET = 93;
/*      */   private static final byte BYTE_LCURLY = 123;
/*      */   private static final byte BYTE_RCURLY = 125;
/*      */   private static final byte BYTE_BACKSLASH = 92;
/*      */   private static final byte BYTE_COMMA = 44;
/*      */   private static final byte BYTE_COLON = 58;
/*      */   private static final int MAX_BYTES_TO_BUFFER = 512;
/*   32 */   private static final byte[] HEX_CHARS = CharTypes.copyHexBytes();
/*      */   
/*   34 */   private static final byte[] NULL_BYTES = new byte[] { 110, 117, 108, 108 };
/*   35 */   private static final byte[] TRUE_BYTES = new byte[] { 116, 114, 117, 101 };
/*   36 */   private static final byte[] FALSE_BYTES = new byte[] { 102, 97, 108, 115, 101 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final OutputStream _outputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte _quoteChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] _outputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _outputTail;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _outputEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _outputMaxContiguous;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _charBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _charBufferLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected byte[] _entityBuffer;
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
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, char quoteChar) {
/*  121 */     super(ctxt, features, codec);
/*  122 */     this._outputStream = out;
/*  123 */     this._quoteChar = (byte)quoteChar;
/*  124 */     if (quoteChar != '"') {
/*  125 */       this._outputEscapes = CharTypes.get7BitOutputEscapes(quoteChar);
/*      */     }
/*      */     
/*  128 */     this._bufferRecyclable = true;
/*  129 */     this._outputBuffer = ctxt.allocWriteEncodingBuffer();
/*  130 */     this._outputEnd = this._outputBuffer.length;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  136 */     this._outputMaxContiguous = this._outputEnd >> 3;
/*  137 */     this._charBuffer = ctxt.allocConcatBuffer();
/*  138 */     this._charBufferLength = this._charBuffer.length;
/*      */ 
/*      */     
/*  141 */     if (isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
/*  142 */       setHighestNonEscapedChar(127);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, char quoteChar, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable) {
/*  152 */     super(ctxt, features, codec);
/*  153 */     this._outputStream = out;
/*  154 */     this._quoteChar = (byte)quoteChar;
/*  155 */     if (quoteChar != '"') {
/*  156 */       this._outputEscapes = CharTypes.get7BitOutputEscapes(quoteChar);
/*      */     }
/*      */     
/*  159 */     this._bufferRecyclable = bufferRecyclable;
/*  160 */     this._outputTail = outputOffset;
/*  161 */     this._outputBuffer = outputBuffer;
/*  162 */     this._outputEnd = this._outputBuffer.length;
/*      */     
/*  164 */     this._outputMaxContiguous = this._outputEnd >> 3;
/*  165 */     this._charBuffer = ctxt.allocConcatBuffer();
/*  166 */     this._charBufferLength = this._charBuffer.length;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out) {
/*  172 */     this(ctxt, features, codec, out, '"');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable) {
/*  180 */     this(ctxt, features, codec, out, '"', outputBuffer, outputOffset, bufferRecyclable);
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
/*      */   public Object getOutputTarget() {
/*  192 */     return this._outputStream;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOutputBuffered() {
/*  198 */     return this._outputTail;
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
/*      */   public void writeFieldName(String name) throws IOException {
/*  210 */     if (this._cfgPrettyPrinter != null) {
/*  211 */       _writePPFieldName(name);
/*      */       return;
/*      */     } 
/*  214 */     int status = this._writeContext.writeFieldName(name);
/*  215 */     if (status == 4) {
/*  216 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  218 */     if (status == 1) {
/*  219 */       if (this._outputTail >= this._outputEnd) {
/*  220 */         _flushBuffer();
/*      */       }
/*  222 */       this._outputBuffer[this._outputTail++] = 44;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  227 */     if (this._cfgUnqNames) {
/*  228 */       _writeStringSegments(name, false);
/*      */       return;
/*      */     } 
/*  231 */     int len = name.length();
/*      */     
/*  233 */     if (len > this._charBufferLength) {
/*  234 */       _writeStringSegments(name, true);
/*      */       return;
/*      */     } 
/*  237 */     if (this._outputTail >= this._outputEnd) {
/*  238 */       _flushBuffer();
/*      */     }
/*  240 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  242 */     if (len <= this._outputMaxContiguous) {
/*  243 */       if (this._outputTail + len > this._outputEnd) {
/*  244 */         _flushBuffer();
/*      */       }
/*  246 */       _writeStringSegment(name, 0, len);
/*      */     } else {
/*  248 */       _writeStringSegments(name, 0, len);
/*      */     } 
/*      */     
/*  251 */     if (this._outputTail >= this._outputEnd) {
/*  252 */       _flushBuffer();
/*      */     }
/*  254 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldName(SerializableString name) throws IOException {
/*  260 */     if (this._cfgPrettyPrinter != null) {
/*  261 */       _writePPFieldName(name);
/*      */       return;
/*      */     } 
/*  264 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  265 */     if (status == 4) {
/*  266 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  268 */     if (status == 1) {
/*  269 */       if (this._outputTail >= this._outputEnd) {
/*  270 */         _flushBuffer();
/*      */       }
/*  272 */       this._outputBuffer[this._outputTail++] = 44;
/*      */     } 
/*  274 */     if (this._cfgUnqNames) {
/*  275 */       _writeUnq(name);
/*      */       return;
/*      */     } 
/*  278 */     if (this._outputTail >= this._outputEnd) {
/*  279 */       _flushBuffer();
/*      */     }
/*  281 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  282 */     int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  283 */     if (len < 0) {
/*  284 */       _writeBytes(name.asQuotedUTF8());
/*      */     } else {
/*  286 */       this._outputTail += len;
/*      */     } 
/*  288 */     if (this._outputTail >= this._outputEnd) {
/*  289 */       _flushBuffer();
/*      */     }
/*  291 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */   
/*      */   private final void _writeUnq(SerializableString name) throws IOException {
/*  295 */     int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  296 */     if (len < 0) {
/*  297 */       _writeBytes(name.asQuotedUTF8());
/*      */     } else {
/*  299 */       this._outputTail += len;
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
/*      */   public final void writeStartArray() throws IOException {
/*  312 */     _verifyValueWrite("start an array");
/*  313 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  314 */     if (this._cfgPrettyPrinter != null) {
/*  315 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  317 */       if (this._outputTail >= this._outputEnd) {
/*  318 */         _flushBuffer();
/*      */       }
/*  320 */       this._outputBuffer[this._outputTail++] = 91;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeStartArray(Object currentValue) throws IOException {
/*  327 */     _verifyValueWrite("start an array");
/*  328 */     this._writeContext = this._writeContext.createChildArrayContext(currentValue);
/*  329 */     if (this._cfgPrettyPrinter != null) {
/*  330 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  332 */       if (this._outputTail >= this._outputEnd) {
/*  333 */         _flushBuffer();
/*      */       }
/*  335 */       this._outputBuffer[this._outputTail++] = 91;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(Object currentValue, int size) throws IOException {
/*  342 */     _verifyValueWrite("start an array");
/*  343 */     this._writeContext = this._writeContext.createChildArrayContext(currentValue);
/*  344 */     if (this._cfgPrettyPrinter != null) {
/*  345 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  347 */       if (this._outputTail >= this._outputEnd) {
/*  348 */         _flushBuffer();
/*      */       }
/*  350 */       this._outputBuffer[this._outputTail++] = 91;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeEndArray() throws IOException {
/*  357 */     if (!this._writeContext.inArray()) {
/*  358 */       _reportError("Current context not Array but " + this._writeContext.typeDesc());
/*      */     }
/*  360 */     if (this._cfgPrettyPrinter != null) {
/*  361 */       this._cfgPrettyPrinter.writeEndArray((JsonGenerator)this, this._writeContext.getEntryCount());
/*      */     } else {
/*  363 */       if (this._outputTail >= this._outputEnd) {
/*  364 */         _flushBuffer();
/*      */       }
/*  366 */       this._outputBuffer[this._outputTail++] = 93;
/*      */     } 
/*  368 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeStartObject() throws IOException {
/*  374 */     _verifyValueWrite("start an object");
/*  375 */     this._writeContext = this._writeContext.createChildObjectContext();
/*  376 */     if (this._cfgPrettyPrinter != null) {
/*  377 */       this._cfgPrettyPrinter.writeStartObject((JsonGenerator)this);
/*      */     } else {
/*  379 */       if (this._outputTail >= this._outputEnd) {
/*  380 */         _flushBuffer();
/*      */       }
/*  382 */       this._outputBuffer[this._outputTail++] = 123;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue) throws IOException {
/*  389 */     _verifyValueWrite("start an object");
/*  390 */     JsonWriteContext ctxt = this._writeContext.createChildObjectContext(forValue);
/*  391 */     this._writeContext = ctxt;
/*  392 */     if (this._cfgPrettyPrinter != null) {
/*  393 */       this._cfgPrettyPrinter.writeStartObject((JsonGenerator)this);
/*      */     } else {
/*  395 */       if (this._outputTail >= this._outputEnd) {
/*  396 */         _flushBuffer();
/*      */       }
/*  398 */       this._outputBuffer[this._outputTail++] = 123;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeEndObject() throws IOException {
/*  405 */     if (!this._writeContext.inObject()) {
/*  406 */       _reportError("Current context not Object but " + this._writeContext.typeDesc());
/*      */     }
/*  408 */     if (this._cfgPrettyPrinter != null) {
/*  409 */       this._cfgPrettyPrinter.writeEndObject((JsonGenerator)this, this._writeContext.getEntryCount());
/*      */     } else {
/*  411 */       if (this._outputTail >= this._outputEnd) {
/*  412 */         _flushBuffer();
/*      */       }
/*  414 */       this._outputBuffer[this._outputTail++] = 125;
/*      */     } 
/*  416 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _writePPFieldName(String name) throws IOException {
/*  423 */     int status = this._writeContext.writeFieldName(name);
/*  424 */     if (status == 4) {
/*  425 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  427 */     if (status == 1) {
/*  428 */       this._cfgPrettyPrinter.writeObjectEntrySeparator((JsonGenerator)this);
/*      */     } else {
/*  430 */       this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*      */     } 
/*  432 */     if (this._cfgUnqNames) {
/*  433 */       _writeStringSegments(name, false);
/*      */       return;
/*      */     } 
/*  436 */     int len = name.length();
/*  437 */     if (len > this._charBufferLength) {
/*  438 */       _writeStringSegments(name, true);
/*      */       return;
/*      */     } 
/*  441 */     if (this._outputTail >= this._outputEnd) {
/*  442 */       _flushBuffer();
/*      */     }
/*  444 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  445 */     name.getChars(0, len, this._charBuffer, 0);
/*      */     
/*  447 */     if (len <= this._outputMaxContiguous) {
/*  448 */       if (this._outputTail + len > this._outputEnd) {
/*  449 */         _flushBuffer();
/*      */       }
/*  451 */       _writeStringSegment(this._charBuffer, 0, len);
/*      */     } else {
/*  453 */       _writeStringSegments(this._charBuffer, 0, len);
/*      */     } 
/*  455 */     if (this._outputTail >= this._outputEnd) {
/*  456 */       _flushBuffer();
/*      */     }
/*  458 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _writePPFieldName(SerializableString name) throws IOException {
/*  463 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  464 */     if (status == 4) {
/*  465 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  467 */     if (status == 1) {
/*  468 */       this._cfgPrettyPrinter.writeObjectEntrySeparator((JsonGenerator)this);
/*      */     } else {
/*  470 */       this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*      */     } 
/*      */     
/*  473 */     boolean addQuotes = !this._cfgUnqNames;
/*  474 */     if (addQuotes) {
/*  475 */       if (this._outputTail >= this._outputEnd) {
/*  476 */         _flushBuffer();
/*      */       }
/*  478 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     } 
/*  480 */     int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  481 */     if (len < 0) {
/*  482 */       _writeBytes(name.asQuotedUTF8());
/*      */     } else {
/*  484 */       this._outputTail += len;
/*      */     } 
/*  486 */     if (addQuotes) {
/*  487 */       if (this._outputTail >= this._outputEnd) {
/*  488 */         _flushBuffer();
/*      */       }
/*  490 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
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
/*      */   public void writeString(String text) throws IOException {
/*  503 */     _verifyValueWrite("write a string");
/*  504 */     if (text == null) {
/*  505 */       _writeNull();
/*      */       
/*      */       return;
/*      */     } 
/*  509 */     int len = text.length();
/*  510 */     if (len > this._outputMaxContiguous) {
/*  511 */       _writeStringSegments(text, true);
/*      */       return;
/*      */     } 
/*  514 */     if (this._outputTail + len >= this._outputEnd) {
/*  515 */       _flushBuffer();
/*      */     }
/*  517 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  518 */     _writeStringSegment(text, 0, len);
/*  519 */     if (this._outputTail >= this._outputEnd) {
/*  520 */       _flushBuffer();
/*      */     }
/*  522 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeString(Reader reader, int len) throws IOException {
/*  527 */     _verifyValueWrite("write a string");
/*  528 */     if (reader == null) {
/*  529 */       _reportError("null reader");
/*      */       
/*      */       return;
/*      */     } 
/*  533 */     int toRead = (len >= 0) ? len : Integer.MAX_VALUE;
/*  534 */     char[] buf = this._charBuffer;
/*      */ 
/*      */     
/*  537 */     if (this._outputTail >= this._outputEnd) {
/*  538 */       _flushBuffer();
/*      */     }
/*  540 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */ 
/*      */     
/*  543 */     while (toRead > 0) {
/*  544 */       int toReadNow = Math.min(toRead, buf.length);
/*  545 */       int numRead = reader.read(buf, 0, toReadNow);
/*  546 */       if (numRead <= 0) {
/*      */         break;
/*      */       }
/*  549 */       if (this._outputTail + len >= this._outputEnd) {
/*  550 */         _flushBuffer();
/*      */       }
/*  552 */       _writeStringSegments(buf, 0, numRead);
/*      */       
/*  554 */       toRead -= numRead;
/*      */     } 
/*      */ 
/*      */     
/*  558 */     if (this._outputTail >= this._outputEnd) {
/*  559 */       _flushBuffer();
/*      */     }
/*  561 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  563 */     if (toRead > 0 && len >= 0) {
/*  564 */       _reportError("Didn't read enough from reader");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(char[] text, int offset, int len) throws IOException {
/*  571 */     _verifyValueWrite("write a string");
/*  572 */     if (this._outputTail >= this._outputEnd) {
/*  573 */       _flushBuffer();
/*      */     }
/*  575 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  577 */     if (len <= this._outputMaxContiguous) {
/*  578 */       if (this._outputTail + len > this._outputEnd) {
/*  579 */         _flushBuffer();
/*      */       }
/*  581 */       _writeStringSegment(text, offset, len);
/*      */     } else {
/*  583 */       _writeStringSegments(text, offset, len);
/*      */     } 
/*      */     
/*  586 */     if (this._outputTail >= this._outputEnd) {
/*  587 */       _flushBuffer();
/*      */     }
/*  589 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeString(SerializableString text) throws IOException {
/*  595 */     _verifyValueWrite("write a string");
/*  596 */     if (this._outputTail >= this._outputEnd) {
/*  597 */       _flushBuffer();
/*      */     }
/*  599 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  600 */     int len = text.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/*  601 */     if (len < 0) {
/*  602 */       _writeBytes(text.asQuotedUTF8());
/*      */     } else {
/*  604 */       this._outputTail += len;
/*      */     } 
/*  606 */     if (this._outputTail >= this._outputEnd) {
/*  607 */       _flushBuffer();
/*      */     }
/*  609 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/*  615 */     _verifyValueWrite("write a string");
/*  616 */     if (this._outputTail >= this._outputEnd) {
/*  617 */       _flushBuffer();
/*      */     }
/*  619 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  620 */     _writeBytes(text, offset, length);
/*  621 */     if (this._outputTail >= this._outputEnd) {
/*  622 */       _flushBuffer();
/*      */     }
/*  624 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeUTF8String(byte[] text, int offset, int len) throws IOException {
/*  630 */     _verifyValueWrite("write a string");
/*  631 */     if (this._outputTail >= this._outputEnd) {
/*  632 */       _flushBuffer();
/*      */     }
/*  634 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  636 */     if (len <= this._outputMaxContiguous) {
/*  637 */       _writeUTF8Segment(text, offset, len);
/*      */     } else {
/*  639 */       _writeUTF8Segments(text, offset, len);
/*      */     } 
/*  641 */     if (this._outputTail >= this._outputEnd) {
/*  642 */       _flushBuffer();
/*      */     }
/*  644 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(String text) throws IOException {
/*  655 */     int len = text.length();
/*  656 */     char[] buf = this._charBuffer;
/*  657 */     if (len <= buf.length) {
/*  658 */       text.getChars(0, len, buf, 0);
/*  659 */       writeRaw(buf, 0, len);
/*      */     } else {
/*  661 */       writeRaw(text, 0, len);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(String text, int offset, int len) throws IOException {
/*  668 */     char[] buf = this._charBuffer;
/*  669 */     int cbufLen = buf.length;
/*      */ 
/*      */     
/*  672 */     if (len <= cbufLen) {
/*  673 */       text.getChars(offset, offset + len, buf, 0);
/*  674 */       writeRaw(buf, 0, len);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/*  681 */     int maxChunk = Math.min(cbufLen, (this._outputEnd >> 2) + (this._outputEnd >> 4));
/*      */     
/*  683 */     int maxBytes = maxChunk * 3;
/*      */     
/*  685 */     while (len > 0) {
/*  686 */       int len2 = Math.min(maxChunk, len);
/*  687 */       text.getChars(offset, offset + len2, buf, 0);
/*  688 */       if (this._outputTail + maxBytes > this._outputEnd) {
/*  689 */         _flushBuffer();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  697 */       if (len2 > 1) {
/*  698 */         char ch = buf[len2 - 1];
/*  699 */         if (ch >= '?' && ch <= '?') {
/*  700 */           len2--;
/*      */         }
/*      */       } 
/*  703 */       _writeRawSegment(buf, 0, len2);
/*  704 */       offset += len2;
/*  705 */       len -= len2;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(SerializableString text) throws IOException {
/*  712 */     int len = text.appendUnquotedUTF8(this._outputBuffer, this._outputTail);
/*  713 */     if (len < 0) {
/*  714 */       _writeBytes(text.asUnquotedUTF8());
/*      */     } else {
/*  716 */       this._outputTail += len;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawValue(SerializableString text) throws IOException {
/*  723 */     _verifyValueWrite("write a raw (unencoded) value");
/*  724 */     int len = text.appendUnquotedUTF8(this._outputBuffer, this._outputTail);
/*  725 */     if (len < 0) {
/*  726 */       _writeBytes(text.asUnquotedUTF8());
/*      */     } else {
/*  728 */       this._outputTail += len;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeRaw(char[] cbuf, int offset, int len) throws IOException {
/*  738 */     int len3 = len + len + len;
/*  739 */     if (this._outputTail + len3 > this._outputEnd) {
/*      */       
/*  741 */       if (this._outputEnd < len3) {
/*  742 */         _writeSegmentedRaw(cbuf, offset, len);
/*      */         
/*      */         return;
/*      */       } 
/*  746 */       _flushBuffer();
/*      */     } 
/*      */     
/*  749 */     len += offset;
/*      */ 
/*      */ 
/*      */     
/*  753 */     while (offset < len) {
/*      */       
/*      */       while (true) {
/*  756 */         int ch = cbuf[offset];
/*  757 */         if (ch > 127) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  765 */           ch = cbuf[offset++];
/*  766 */           if (ch < 2048) {
/*  767 */             this._outputBuffer[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/*  768 */             this._outputBuffer[this._outputTail++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */           } 
/*  770 */           offset = _outputRawMultiByteChar(ch, cbuf, offset, len);
/*      */           continue;
/*      */         } 
/*      */         this._outputBuffer[this._outputTail++] = (byte)ch;
/*      */         if (++offset >= len)
/*      */           break; 
/*      */       } 
/*      */     }  } public void writeRaw(char ch) throws IOException {
/*  778 */     if (this._outputTail + 3 >= this._outputEnd) {
/*  779 */       _flushBuffer();
/*      */     }
/*  781 */     byte[] bbuf = this._outputBuffer;
/*  782 */     if (ch <= '') {
/*  783 */       bbuf[this._outputTail++] = (byte)ch;
/*  784 */     } else if (ch < 'ࠀ') {
/*  785 */       bbuf[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/*  786 */       bbuf[this._outputTail++] = (byte)(0x80 | ch & 0x3F);
/*      */     } else {
/*  788 */       _outputRawMultiByteChar(ch, (char[])null, 0, 0);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeSegmentedRaw(char[] cbuf, int offset, int len) throws IOException {
/*  798 */     int end = this._outputEnd;
/*  799 */     byte[] bbuf = this._outputBuffer;
/*  800 */     int inputEnd = offset + len;
/*      */ 
/*      */     
/*  803 */     while (offset < inputEnd) {
/*      */       
/*      */       while (true) {
/*  806 */         int ch = cbuf[offset];
/*  807 */         if (ch >= 128) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  819 */           if (this._outputTail + 3 >= this._outputEnd) {
/*  820 */             _flushBuffer();
/*      */           }
/*  822 */           ch = cbuf[offset++];
/*  823 */           if (ch < 2048) {
/*  824 */             bbuf[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/*  825 */             bbuf[this._outputTail++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */           } 
/*  827 */           offset = _outputRawMultiByteChar(ch, cbuf, offset, inputEnd);
/*      */           continue;
/*      */         } 
/*      */         if (this._outputTail >= end) {
/*      */           _flushBuffer();
/*      */         }
/*      */         bbuf[this._outputTail++] = (byte)ch;
/*      */         if (++offset >= inputEnd) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeRawSegment(char[] cbuf, int offset, int end) throws IOException {
/*  844 */     while (offset < end) {
/*      */       
/*      */       while (true) {
/*  847 */         int ch = cbuf[offset];
/*  848 */         if (ch > 127) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  856 */           ch = cbuf[offset++];
/*  857 */           if (ch < 2048) {
/*  858 */             this._outputBuffer[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/*  859 */             this._outputBuffer[this._outputTail++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */           } 
/*  861 */           offset = _outputRawMultiByteChar(ch, cbuf, offset, end);
/*      */           continue;
/*      */         } 
/*      */         this._outputBuffer[this._outputTail++] = (byte)ch;
/*      */         if (++offset >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
/*  877 */     _verifyValueWrite("write a binary value");
/*      */     
/*  879 */     if (this._outputTail >= this._outputEnd) {
/*  880 */       _flushBuffer();
/*      */     }
/*  882 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  883 */     _writeBinary(b64variant, data, offset, offset + len);
/*      */     
/*  885 */     if (this._outputTail >= this._outputEnd) {
/*  886 */       _flushBuffer();
/*      */     }
/*  888 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException, JsonGenerationException {
/*      */     int bytes;
/*  896 */     _verifyValueWrite("write a binary value");
/*      */     
/*  898 */     if (this._outputTail >= this._outputEnd) {
/*  899 */       _flushBuffer();
/*      */     }
/*  901 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  902 */     byte[] encodingBuffer = this._ioContext.allocBase64Buffer();
/*      */     
/*      */     try {
/*  905 */       if (dataLength < 0) {
/*  906 */         bytes = _writeBinary(b64variant, data, encodingBuffer);
/*      */       } else {
/*  908 */         int missing = _writeBinary(b64variant, data, encodingBuffer, dataLength);
/*  909 */         if (missing > 0) {
/*  910 */           _reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
/*      */         }
/*  912 */         bytes = dataLength;
/*      */       } 
/*      */     } finally {
/*  915 */       this._ioContext.releaseBase64Buffer(encodingBuffer);
/*      */     } 
/*      */     
/*  918 */     if (this._outputTail >= this._outputEnd) {
/*  919 */       _flushBuffer();
/*      */     }
/*  921 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  922 */     return bytes;
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
/*      */   public void writeNumber(short s) throws IOException {
/*  934 */     _verifyValueWrite("write a number");
/*      */     
/*  936 */     if (this._outputTail + 6 >= this._outputEnd) {
/*  937 */       _flushBuffer();
/*      */     }
/*  939 */     if (this._cfgNumbersAsStrings) {
/*  940 */       _writeQuotedShort(s);
/*      */       return;
/*      */     } 
/*  943 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private final void _writeQuotedShort(short s) throws IOException {
/*  947 */     if (this._outputTail + 8 >= this._outputEnd) {
/*  948 */       _flushBuffer();
/*      */     }
/*  950 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  951 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*  952 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(int i) throws IOException {
/*  958 */     _verifyValueWrite("write a number");
/*      */     
/*  960 */     if (this._outputTail + 11 >= this._outputEnd) {
/*  961 */       _flushBuffer();
/*      */     }
/*  963 */     if (this._cfgNumbersAsStrings) {
/*  964 */       _writeQuotedInt(i);
/*      */       return;
/*      */     } 
/*  967 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeQuotedInt(int i) throws IOException {
/*  972 */     if (this._outputTail + 13 >= this._outputEnd) {
/*  973 */       _flushBuffer();
/*      */     }
/*  975 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  976 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*  977 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(long l) throws IOException {
/*  983 */     _verifyValueWrite("write a number");
/*  984 */     if (this._cfgNumbersAsStrings) {
/*  985 */       _writeQuotedLong(l);
/*      */       return;
/*      */     } 
/*  988 */     if (this._outputTail + 21 >= this._outputEnd)
/*      */     {
/*  990 */       _flushBuffer();
/*      */     }
/*  992 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeQuotedLong(long l) throws IOException {
/*  997 */     if (this._outputTail + 23 >= this._outputEnd) {
/*  998 */       _flushBuffer();
/*      */     }
/* 1000 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/* 1001 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/* 1002 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigInteger value) throws IOException {
/* 1008 */     _verifyValueWrite("write a number");
/* 1009 */     if (value == null) {
/* 1010 */       _writeNull();
/* 1011 */     } else if (this._cfgNumbersAsStrings) {
/* 1012 */       _writeQuotedRaw(value.toString());
/*      */     } else {
/* 1014 */       writeRaw(value.toString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(double d) throws IOException {
/* 1022 */     if (this._cfgNumbersAsStrings || (
/* 1023 */       NumberOutput.notFinite(d) && JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS
/* 1024 */       .enabledIn(this._features))) {
/* 1025 */       writeString(String.valueOf(d));
/*      */       
/*      */       return;
/*      */     } 
/* 1029 */     _verifyValueWrite("write a number");
/* 1030 */     writeRaw(String.valueOf(d));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(float f) throws IOException {
/* 1037 */     if (this._cfgNumbersAsStrings || (
/* 1038 */       NumberOutput.notFinite(f) && JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS
/* 1039 */       .enabledIn(this._features))) {
/* 1040 */       writeString(String.valueOf(f));
/*      */       
/*      */       return;
/*      */     } 
/* 1044 */     _verifyValueWrite("write a number");
/* 1045 */     writeRaw(String.valueOf(f));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigDecimal value) throws IOException {
/* 1052 */     _verifyValueWrite("write a number");
/* 1053 */     if (value == null) {
/* 1054 */       _writeNull();
/* 1055 */     } else if (this._cfgNumbersAsStrings) {
/* 1056 */       _writeQuotedRaw(_asString(value));
/*      */     } else {
/* 1058 */       writeRaw(_asString(value));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(String encodedValue) throws IOException {
/* 1065 */     _verifyValueWrite("write a number");
/* 1066 */     if (encodedValue == null) {
/* 1067 */       _writeNull();
/* 1068 */     } else if (this._cfgNumbersAsStrings) {
/* 1069 */       _writeQuotedRaw(encodedValue);
/*      */     } else {
/* 1071 */       writeRaw(encodedValue);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNumber(char[] encodedValueBuffer, int offset, int length) throws IOException {
/* 1077 */     _verifyValueWrite("write a number");
/* 1078 */     if (this._cfgNumbersAsStrings) {
/* 1079 */       _writeQuotedRaw(encodedValueBuffer, offset, length);
/*      */     } else {
/* 1081 */       writeRaw(encodedValueBuffer, offset, length);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeQuotedRaw(String value) throws IOException {
/* 1087 */     if (this._outputTail >= this._outputEnd) {
/* 1088 */       _flushBuffer();
/*      */     }
/* 1090 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/* 1091 */     writeRaw(value);
/* 1092 */     if (this._outputTail >= this._outputEnd) {
/* 1093 */       _flushBuffer();
/*      */     }
/* 1095 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   private void _writeQuotedRaw(char[] text, int offset, int length) throws IOException {
/* 1100 */     if (this._outputTail >= this._outputEnd) {
/* 1101 */       _flushBuffer();
/*      */     }
/* 1103 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/* 1104 */     writeRaw(text, offset, length);
/* 1105 */     if (this._outputTail >= this._outputEnd) {
/* 1106 */       _flushBuffer();
/*      */     }
/* 1108 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBoolean(boolean state) throws IOException {
/* 1114 */     _verifyValueWrite("write a boolean value");
/* 1115 */     if (this._outputTail + 5 >= this._outputEnd) {
/* 1116 */       _flushBuffer();
/*      */     }
/* 1118 */     byte[] keyword = state ? TRUE_BYTES : FALSE_BYTES;
/* 1119 */     int len = keyword.length;
/* 1120 */     System.arraycopy(keyword, 0, this._outputBuffer, this._outputTail, len);
/* 1121 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNull() throws IOException {
/* 1127 */     _verifyValueWrite("write a null");
/* 1128 */     _writeNull();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _verifyValueWrite(String typeMsg) throws IOException {
/*      */     byte b;
/* 1140 */     int status = this._writeContext.writeValue();
/* 1141 */     if (this._cfgPrettyPrinter != null) {
/*      */       
/* 1143 */       _verifyPrettyValueWrite(typeMsg, status);
/*      */       
/*      */       return;
/*      */     } 
/* 1147 */     switch (status) {
/*      */       default:
/*      */         return;
/*      */       
/*      */       case 1:
/* 1152 */         b = 44;
/*      */         break;
/*      */       case 2:
/* 1155 */         b = 58;
/*      */         break;
/*      */       case 3:
/* 1158 */         if (this._rootValueSeparator != null) {
/* 1159 */           byte[] raw = this._rootValueSeparator.asUnquotedUTF8();
/* 1160 */           if (raw.length > 0) {
/* 1161 */             _writeBytes(raw);
/*      */           }
/*      */         } 
/*      */         return;
/*      */       case 5:
/* 1166 */         _reportCantWriteValueExpectName(typeMsg);
/*      */         return;
/*      */     } 
/* 1169 */     if (this._outputTail >= this._outputEnd) {
/* 1170 */       _flushBuffer();
/*      */     }
/* 1172 */     this._outputBuffer[this._outputTail++] = b;
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
/*      */   public void flush() throws IOException {
/* 1184 */     _flushBuffer();
/* 1185 */     if (this._outputStream != null && 
/* 1186 */       isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/* 1187 */       this._outputStream.flush();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/* 1195 */     super.close();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1201 */     if (this._outputBuffer != null && 
/* 1202 */       isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
/*      */       while (true) {
/* 1204 */         JsonStreamContext ctxt = getOutputContext();
/* 1205 */         if (ctxt.inArray()) {
/* 1206 */           writeEndArray(); continue;
/* 1207 */         }  if (ctxt.inObject()) {
/* 1208 */           writeEndObject();
/*      */           continue;
/*      */         } 
/*      */         break;
/*      */       } 
/*      */     }
/* 1214 */     _flushBuffer();
/* 1215 */     this._outputTail = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1223 */     if (this._outputStream != null) {
/* 1224 */       if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
/* 1225 */         this._outputStream.close();
/* 1226 */       } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/*      */         
/* 1228 */         this._outputStream.flush();
/*      */       } 
/*      */     }
/*      */     
/* 1232 */     _releaseBuffers();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _releaseBuffers() {
/* 1238 */     byte[] buf = this._outputBuffer;
/* 1239 */     if (buf != null && this._bufferRecyclable) {
/* 1240 */       this._outputBuffer = null;
/* 1241 */       this._ioContext.releaseWriteEncodingBuffer(buf);
/*      */     } 
/* 1243 */     char[] cbuf = this._charBuffer;
/* 1244 */     if (cbuf != null) {
/* 1245 */       this._charBuffer = null;
/* 1246 */       this._ioContext.releaseConcatBuffer(cbuf);
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
/*      */   private final void _writeBytes(byte[] bytes) throws IOException {
/* 1258 */     int len = bytes.length;
/* 1259 */     if (this._outputTail + len > this._outputEnd) {
/* 1260 */       _flushBuffer();
/*      */       
/* 1262 */       if (len > 512) {
/* 1263 */         this._outputStream.write(bytes, 0, len);
/*      */         return;
/*      */       } 
/*      */     } 
/* 1267 */     System.arraycopy(bytes, 0, this._outputBuffer, this._outputTail, len);
/* 1268 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeBytes(byte[] bytes, int offset, int len) throws IOException {
/* 1273 */     if (this._outputTail + len > this._outputEnd) {
/* 1274 */       _flushBuffer();
/*      */       
/* 1276 */       if (len > 512) {
/* 1277 */         this._outputStream.write(bytes, offset, len);
/*      */         return;
/*      */       } 
/*      */     } 
/* 1281 */     System.arraycopy(bytes, offset, this._outputBuffer, this._outputTail, len);
/* 1282 */     this._outputTail += len;
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
/*      */   private final void _writeStringSegments(String text, boolean addQuotes) throws IOException {
/* 1300 */     if (addQuotes) {
/* 1301 */       if (this._outputTail >= this._outputEnd) {
/* 1302 */         _flushBuffer();
/*      */       }
/* 1304 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     } 
/*      */     
/* 1307 */     int left = text.length();
/* 1308 */     int offset = 0;
/*      */     
/* 1310 */     while (left > 0) {
/* 1311 */       int len = Math.min(this._outputMaxContiguous, left);
/* 1312 */       if (this._outputTail + len > this._outputEnd) {
/* 1313 */         _flushBuffer();
/*      */       }
/* 1315 */       _writeStringSegment(text, offset, len);
/* 1316 */       offset += len;
/* 1317 */       left -= len;
/*      */     } 
/*      */     
/* 1320 */     if (addQuotes) {
/* 1321 */       if (this._outputTail >= this._outputEnd) {
/* 1322 */         _flushBuffer();
/*      */       }
/* 1324 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
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
/*      */   private final void _writeStringSegments(char[] cbuf, int offset, int totalLen) throws IOException {
/*      */     do {
/* 1337 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1338 */       if (this._outputTail + len > this._outputEnd) {
/* 1339 */         _flushBuffer();
/*      */       }
/* 1341 */       _writeStringSegment(cbuf, offset, len);
/* 1342 */       offset += len;
/* 1343 */       totalLen -= len;
/* 1344 */     } while (totalLen > 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeStringSegments(String text, int offset, int totalLen) throws IOException {
/*      */     do {
/* 1350 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1351 */       if (this._outputTail + len > this._outputEnd) {
/* 1352 */         _flushBuffer();
/*      */       }
/* 1354 */       _writeStringSegment(text, offset, len);
/* 1355 */       offset += len;
/* 1356 */       totalLen -= len;
/* 1357 */     } while (totalLen > 0);
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
/*      */   private final void _writeStringSegment(char[] cbuf, int offset, int len) throws IOException {
/* 1380 */     len += offset;
/*      */     
/* 1382 */     int outputPtr = this._outputTail;
/* 1383 */     byte[] outputBuffer = this._outputBuffer;
/* 1384 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1386 */     while (offset < len) {
/* 1387 */       int ch = cbuf[offset];
/*      */       
/* 1389 */       if (ch > 127 || escCodes[ch] != 0) {
/*      */         break;
/*      */       }
/* 1392 */       outputBuffer[outputPtr++] = (byte)ch;
/* 1393 */       offset++;
/*      */     } 
/* 1395 */     this._outputTail = outputPtr;
/* 1396 */     if (offset < len) {
/* 1397 */       if (this._characterEscapes != null) {
/* 1398 */         _writeCustomStringSegment2(cbuf, offset, len);
/* 1399 */       } else if (this._maximumNonEscapedChar == 0) {
/* 1400 */         _writeStringSegment2(cbuf, offset, len);
/*      */       } else {
/* 1402 */         _writeStringSegmentASCII2(cbuf, offset, len);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeStringSegment(String text, int offset, int len) throws IOException {
/* 1412 */     len += offset;
/*      */     
/* 1414 */     int outputPtr = this._outputTail;
/* 1415 */     byte[] outputBuffer = this._outputBuffer;
/* 1416 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1418 */     while (offset < len) {
/* 1419 */       int ch = text.charAt(offset);
/*      */       
/* 1421 */       if (ch > 127 || escCodes[ch] != 0) {
/*      */         break;
/*      */       }
/* 1424 */       outputBuffer[outputPtr++] = (byte)ch;
/* 1425 */       offset++;
/*      */     } 
/* 1427 */     this._outputTail = outputPtr;
/* 1428 */     if (offset < len) {
/* 1429 */       if (this._characterEscapes != null) {
/* 1430 */         _writeCustomStringSegment2(text, offset, len);
/* 1431 */       } else if (this._maximumNonEscapedChar == 0) {
/* 1432 */         _writeStringSegment2(text, offset, len);
/*      */       } else {
/* 1434 */         _writeStringSegmentASCII2(text, offset, len);
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
/*      */   private final void _writeStringSegment2(char[] cbuf, int offset, int end) throws IOException {
/* 1446 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1447 */       _flushBuffer();
/*      */     }
/*      */     
/* 1450 */     int outputPtr = this._outputTail;
/*      */     
/* 1452 */     byte[] outputBuffer = this._outputBuffer;
/* 1453 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1455 */     while (offset < end) {
/* 1456 */       int ch = cbuf[offset++];
/* 1457 */       if (ch <= 127) {
/* 1458 */         if (escCodes[ch] == 0) {
/* 1459 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1462 */         int escape = escCodes[ch];
/* 1463 */         if (escape > 0) {
/* 1464 */           outputBuffer[outputPtr++] = 92;
/* 1465 */           outputBuffer[outputPtr++] = (byte)escape;
/*      */           continue;
/*      */         } 
/* 1468 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1472 */       if (ch <= 2047) {
/* 1473 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1474 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1476 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1479 */     this._outputTail = outputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeStringSegment2(String text, int offset, int end) throws IOException {
/* 1484 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1485 */       _flushBuffer();
/*      */     }
/*      */     
/* 1488 */     int outputPtr = this._outputTail;
/*      */     
/* 1490 */     byte[] outputBuffer = this._outputBuffer;
/* 1491 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1493 */     while (offset < end) {
/* 1494 */       int ch = text.charAt(offset++);
/* 1495 */       if (ch <= 127) {
/* 1496 */         if (escCodes[ch] == 0) {
/* 1497 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1500 */         int escape = escCodes[ch];
/* 1501 */         if (escape > 0) {
/* 1502 */           outputBuffer[outputPtr++] = 92;
/* 1503 */           outputBuffer[outputPtr++] = (byte)escape;
/*      */           continue;
/*      */         } 
/* 1506 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1510 */       if (ch <= 2047) {
/* 1511 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1512 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1514 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1517 */     this._outputTail = outputPtr;
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
/*      */   private final void _writeStringSegmentASCII2(char[] cbuf, int offset, int end) throws IOException {
/* 1534 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1535 */       _flushBuffer();
/*      */     }
/* 1537 */     int outputPtr = this._outputTail;
/*      */     
/* 1539 */     byte[] outputBuffer = this._outputBuffer;
/* 1540 */     int[] escCodes = this._outputEscapes;
/* 1541 */     int maxUnescaped = this._maximumNonEscapedChar;
/*      */     
/* 1543 */     while (offset < end) {
/* 1544 */       int ch = cbuf[offset++];
/* 1545 */       if (ch <= 127) {
/* 1546 */         if (escCodes[ch] == 0) {
/* 1547 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1550 */         int escape = escCodes[ch];
/* 1551 */         if (escape > 0) {
/* 1552 */           outputBuffer[outputPtr++] = 92;
/* 1553 */           outputBuffer[outputPtr++] = (byte)escape;
/*      */           continue;
/*      */         } 
/* 1556 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1560 */       if (ch > maxUnescaped) {
/* 1561 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         continue;
/*      */       } 
/* 1564 */       if (ch <= 2047) {
/* 1565 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1566 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1568 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1571 */     this._outputTail = outputPtr;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeStringSegmentASCII2(String text, int offset, int end) throws IOException {
/* 1577 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1578 */       _flushBuffer();
/*      */     }
/*      */     
/* 1581 */     int outputPtr = this._outputTail;
/*      */     
/* 1583 */     byte[] outputBuffer = this._outputBuffer;
/* 1584 */     int[] escCodes = this._outputEscapes;
/* 1585 */     int maxUnescaped = this._maximumNonEscapedChar;
/*      */     
/* 1587 */     while (offset < end) {
/* 1588 */       int ch = text.charAt(offset++);
/* 1589 */       if (ch <= 127) {
/* 1590 */         if (escCodes[ch] == 0) {
/* 1591 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1594 */         int escape = escCodes[ch];
/* 1595 */         if (escape > 0) {
/* 1596 */           outputBuffer[outputPtr++] = 92;
/* 1597 */           outputBuffer[outputPtr++] = (byte)escape;
/*      */           continue;
/*      */         } 
/* 1600 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1604 */       if (ch > maxUnescaped) {
/* 1605 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         continue;
/*      */       } 
/* 1608 */       if (ch <= 2047) {
/* 1609 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1610 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1612 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1615 */     this._outputTail = outputPtr;
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
/*      */   private final void _writeCustomStringSegment2(char[] cbuf, int offset, int end) throws IOException {
/* 1632 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1633 */       _flushBuffer();
/*      */     }
/* 1635 */     int outputPtr = this._outputTail;
/*      */     
/* 1637 */     byte[] outputBuffer = this._outputBuffer;
/* 1638 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1640 */     int maxUnescaped = (this._maximumNonEscapedChar <= 0) ? 65535 : this._maximumNonEscapedChar;
/* 1641 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1643 */     while (offset < end) {
/* 1644 */       int ch = cbuf[offset++];
/* 1645 */       if (ch <= 127) {
/* 1646 */         if (escCodes[ch] == 0) {
/* 1647 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1650 */         int escape = escCodes[ch];
/* 1651 */         if (escape > 0) {
/* 1652 */           outputBuffer[outputPtr++] = 92;
/* 1653 */           outputBuffer[outputPtr++] = (byte)escape; continue;
/* 1654 */         }  if (escape == -2) {
/* 1655 */           SerializableString serializableString = customEscapes.getEscapeSequence(ch);
/* 1656 */           if (serializableString == null) {
/* 1657 */             _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + 
/* 1658 */                 Integer.toHexString(ch) + ", although was supposed to have one");
/*      */           }
/* 1660 */           outputPtr = _writeCustomEscape(outputBuffer, outputPtr, serializableString, end - offset);
/*      */           continue;
/*      */         } 
/* 1663 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1667 */       if (ch > maxUnescaped) {
/* 1668 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         continue;
/*      */       } 
/* 1671 */       SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1672 */       if (esc != null) {
/* 1673 */         outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */         continue;
/*      */       } 
/* 1676 */       if (ch <= 2047) {
/* 1677 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1678 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1680 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1683 */     this._outputTail = outputPtr;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeCustomStringSegment2(String text, int offset, int end) throws IOException {
/* 1689 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1690 */       _flushBuffer();
/*      */     }
/* 1692 */     int outputPtr = this._outputTail;
/*      */     
/* 1694 */     byte[] outputBuffer = this._outputBuffer;
/* 1695 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1697 */     int maxUnescaped = (this._maximumNonEscapedChar <= 0) ? 65535 : this._maximumNonEscapedChar;
/* 1698 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1700 */     while (offset < end) {
/* 1701 */       int ch = text.charAt(offset++);
/* 1702 */       if (ch <= 127) {
/* 1703 */         if (escCodes[ch] == 0) {
/* 1704 */           outputBuffer[outputPtr++] = (byte)ch;
/*      */           continue;
/*      */         } 
/* 1707 */         int escape = escCodes[ch];
/* 1708 */         if (escape > 0) {
/* 1709 */           outputBuffer[outputPtr++] = 92;
/* 1710 */           outputBuffer[outputPtr++] = (byte)escape; continue;
/* 1711 */         }  if (escape == -2) {
/* 1712 */           SerializableString serializableString = customEscapes.getEscapeSequence(ch);
/* 1713 */           if (serializableString == null) {
/* 1714 */             _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + 
/* 1715 */                 Integer.toHexString(ch) + ", although was supposed to have one");
/*      */           }
/* 1717 */           outputPtr = _writeCustomEscape(outputBuffer, outputPtr, serializableString, end - offset);
/*      */           continue;
/*      */         } 
/* 1720 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         
/*      */         continue;
/*      */       } 
/* 1724 */       if (ch > maxUnescaped) {
/* 1725 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         continue;
/*      */       } 
/* 1728 */       SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1729 */       if (esc != null) {
/* 1730 */         outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */         continue;
/*      */       } 
/* 1733 */       if (ch <= 2047) {
/* 1734 */         outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 1735 */         outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F); continue;
/*      */       } 
/* 1737 */       outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */     } 
/*      */     
/* 1740 */     this._outputTail = outputPtr;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _writeCustomEscape(byte[] outputBuffer, int outputPtr, SerializableString esc, int remainingChars) throws IOException, JsonGenerationException {
/* 1746 */     byte[] raw = esc.asUnquotedUTF8();
/* 1747 */     int len = raw.length;
/* 1748 */     if (len > 6) {
/* 1749 */       return _handleLongCustomEscape(outputBuffer, outputPtr, this._outputEnd, raw, remainingChars);
/*      */     }
/*      */     
/* 1752 */     System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/* 1753 */     return outputPtr + len;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _handleLongCustomEscape(byte[] outputBuffer, int outputPtr, int outputEnd, byte[] raw, int remainingChars) throws IOException, JsonGenerationException {
/* 1760 */     int len = raw.length;
/* 1761 */     if (outputPtr + len > outputEnd) {
/* 1762 */       this._outputTail = outputPtr;
/* 1763 */       _flushBuffer();
/* 1764 */       outputPtr = this._outputTail;
/* 1765 */       if (len > outputBuffer.length) {
/* 1766 */         this._outputStream.write(raw, 0, len);
/* 1767 */         return outputPtr;
/*      */       } 
/*      */     } 
/* 1770 */     System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/* 1771 */     outputPtr += len;
/*      */     
/* 1773 */     if (outputPtr + 6 * remainingChars > outputEnd) {
/* 1774 */       this._outputTail = outputPtr;
/* 1775 */       _flushBuffer();
/* 1776 */       return this._outputTail;
/*      */     } 
/* 1778 */     return outputPtr;
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
/*      */   private final void _writeUTF8Segments(byte[] utf8, int offset, int totalLen) throws IOException, JsonGenerationException {
/*      */     do {
/* 1796 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1797 */       _writeUTF8Segment(utf8, offset, len);
/* 1798 */       offset += len;
/* 1799 */       totalLen -= len;
/* 1800 */     } while (totalLen > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeUTF8Segment(byte[] utf8, int offset, int len) throws IOException, JsonGenerationException {
/* 1807 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1809 */     for (int ptr = offset, end = offset + len; ptr < end; ) {
/*      */       
/* 1811 */       int ch = utf8[ptr++];
/* 1812 */       if (ch >= 0 && escCodes[ch] != 0) {
/* 1813 */         _writeUTF8Segment2(utf8, offset, len);
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */     
/* 1819 */     if (this._outputTail + len > this._outputEnd) {
/* 1820 */       _flushBuffer();
/*      */     }
/* 1822 */     System.arraycopy(utf8, offset, this._outputBuffer, this._outputTail, len);
/* 1823 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeUTF8Segment2(byte[] utf8, int offset, int len) throws IOException, JsonGenerationException {
/* 1829 */     int outputPtr = this._outputTail;
/*      */ 
/*      */     
/* 1832 */     if (outputPtr + len * 6 > this._outputEnd) {
/* 1833 */       _flushBuffer();
/* 1834 */       outputPtr = this._outputTail;
/*      */     } 
/*      */     
/* 1837 */     byte[] outputBuffer = this._outputBuffer;
/* 1838 */     int[] escCodes = this._outputEscapes;
/* 1839 */     len += offset;
/*      */     
/* 1841 */     while (offset < len) {
/* 1842 */       byte b = utf8[offset++];
/* 1843 */       int ch = b;
/* 1844 */       if (ch < 0 || escCodes[ch] == 0) {
/* 1845 */         outputBuffer[outputPtr++] = b;
/*      */         continue;
/*      */       } 
/* 1848 */       int escape = escCodes[ch];
/* 1849 */       if (escape > 0) {
/* 1850 */         outputBuffer[outputPtr++] = 92;
/* 1851 */         outputBuffer[outputPtr++] = (byte)escape;
/*      */         continue;
/*      */       } 
/* 1854 */       outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */     } 
/*      */     
/* 1857 */     this._outputTail = outputPtr;
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
/*      */   protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd) throws IOException, JsonGenerationException {
/* 1871 */     int safeInputEnd = inputEnd - 3;
/*      */     
/* 1873 */     int safeOutputEnd = this._outputEnd - 6;
/* 1874 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */ 
/*      */     
/* 1877 */     while (inputPtr <= safeInputEnd) {
/* 1878 */       if (this._outputTail > safeOutputEnd) {
/* 1879 */         _flushBuffer();
/*      */       }
/*      */       
/* 1882 */       int b24 = input[inputPtr++] << 8;
/* 1883 */       b24 |= input[inputPtr++] & 0xFF;
/* 1884 */       b24 = b24 << 8 | input[inputPtr++] & 0xFF;
/* 1885 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1886 */       if (--chunksBeforeLF <= 0) {
/*      */         
/* 1888 */         this._outputBuffer[this._outputTail++] = 92;
/* 1889 */         this._outputBuffer[this._outputTail++] = 110;
/* 1890 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1895 */     int inputLeft = inputEnd - inputPtr;
/* 1896 */     if (inputLeft > 0) {
/* 1897 */       if (this._outputTail > safeOutputEnd) {
/* 1898 */         _flushBuffer();
/*      */       }
/* 1900 */       int b24 = input[inputPtr++] << 16;
/* 1901 */       if (inputLeft == 2) {
/* 1902 */         b24 |= (input[inputPtr++] & 0xFF) << 8;
/*      */       }
/* 1904 */       this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft) throws IOException, JsonGenerationException {
/* 1913 */     int inputPtr = 0;
/* 1914 */     int inputEnd = 0;
/* 1915 */     int lastFullOffset = -3;
/*      */ 
/*      */     
/* 1918 */     int safeOutputEnd = this._outputEnd - 6;
/* 1919 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/* 1921 */     while (bytesLeft > 2) {
/* 1922 */       if (inputPtr > lastFullOffset) {
/* 1923 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1924 */         inputPtr = 0;
/* 1925 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1928 */         lastFullOffset = inputEnd - 3;
/*      */       } 
/* 1930 */       if (this._outputTail > safeOutputEnd) {
/* 1931 */         _flushBuffer();
/*      */       }
/* 1933 */       int b24 = readBuffer[inputPtr++] << 8;
/* 1934 */       b24 |= readBuffer[inputPtr++] & 0xFF;
/* 1935 */       b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/* 1936 */       bytesLeft -= 3;
/* 1937 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1938 */       if (--chunksBeforeLF <= 0) {
/* 1939 */         this._outputBuffer[this._outputTail++] = 92;
/* 1940 */         this._outputBuffer[this._outputTail++] = 110;
/* 1941 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1946 */     if (bytesLeft > 0) {
/* 1947 */       inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1948 */       inputPtr = 0;
/* 1949 */       if (inputEnd > 0) {
/* 1950 */         int amount; if (this._outputTail > safeOutputEnd) {
/* 1951 */           _flushBuffer();
/*      */         }
/* 1953 */         int b24 = readBuffer[inputPtr++] << 16;
/*      */         
/* 1955 */         if (inputPtr < inputEnd) {
/* 1956 */           b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1957 */           amount = 2;
/*      */         } else {
/* 1959 */           amount = 1;
/*      */         } 
/* 1961 */         this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/* 1962 */         bytesLeft -= amount;
/*      */       } 
/*      */     } 
/* 1965 */     return bytesLeft;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer) throws IOException, JsonGenerationException {
/* 1973 */     int inputPtr = 0;
/* 1974 */     int inputEnd = 0;
/* 1975 */     int lastFullOffset = -3;
/* 1976 */     int bytesDone = 0;
/*      */ 
/*      */     
/* 1979 */     int safeOutputEnd = this._outputEnd - 6;
/* 1980 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */ 
/*      */     
/*      */     while (true) {
/* 1984 */       if (inputPtr > lastFullOffset) {
/* 1985 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
/* 1986 */         inputPtr = 0;
/* 1987 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1990 */         lastFullOffset = inputEnd - 3;
/*      */       } 
/* 1992 */       if (this._outputTail > safeOutputEnd) {
/* 1993 */         _flushBuffer();
/*      */       }
/*      */       
/* 1996 */       int b24 = readBuffer[inputPtr++] << 8;
/* 1997 */       b24 |= readBuffer[inputPtr++] & 0xFF;
/* 1998 */       b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/* 1999 */       bytesDone += 3;
/* 2000 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 2001 */       if (--chunksBeforeLF <= 0) {
/* 2002 */         this._outputBuffer[this._outputTail++] = 92;
/* 2003 */         this._outputBuffer[this._outputTail++] = 110;
/* 2004 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 2009 */     if (inputPtr < inputEnd) {
/* 2010 */       if (this._outputTail > safeOutputEnd) {
/* 2011 */         _flushBuffer();
/*      */       }
/* 2013 */       int b24 = readBuffer[inputPtr++] << 16;
/* 2014 */       int amount = 1;
/* 2015 */       if (inputPtr < inputEnd) {
/* 2016 */         b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 2017 */         amount = 2;
/*      */       } 
/* 2019 */       bytesDone += amount;
/* 2020 */       this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/*      */     } 
/* 2022 */     return bytesDone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws IOException {
/* 2030 */     int i = 0;
/* 2031 */     while (inputPtr < inputEnd) {
/* 2032 */       readBuffer[i++] = readBuffer[inputPtr++];
/*      */     }
/* 2034 */     inputPtr = 0;
/* 2035 */     inputEnd = i;
/* 2036 */     maxRead = Math.min(maxRead, readBuffer.length);
/*      */     
/*      */     do {
/* 2039 */       int length = maxRead - inputEnd;
/* 2040 */       if (length == 0) {
/*      */         break;
/*      */       }
/* 2043 */       int count = in.read(readBuffer, inputEnd, length);
/* 2044 */       if (count < 0) {
/* 2045 */         return inputEnd;
/*      */       }
/* 2047 */       inputEnd += count;
/* 2048 */     } while (inputEnd < 3);
/* 2049 */     return inputEnd;
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
/*      */   private final int _outputRawMultiByteChar(int ch, char[] cbuf, int inputOffset, int inputEnd) throws IOException {
/* 2067 */     if (ch >= 55296 && 
/* 2068 */       ch <= 57343) {
/*      */       
/* 2070 */       if (inputOffset >= inputEnd || cbuf == null) {
/* 2071 */         _reportError(String.format("Split surrogate on writeRaw() input (last character): first character 0x%4x", new Object[] {
/* 2072 */                 Integer.valueOf(ch) }));
/*      */       } else {
/* 2074 */         _outputSurrogates(ch, cbuf[inputOffset]);
/*      */       } 
/* 2076 */       return inputOffset + 1;
/*      */     } 
/*      */     
/* 2079 */     byte[] bbuf = this._outputBuffer;
/* 2080 */     bbuf[this._outputTail++] = (byte)(0xE0 | ch >> 12);
/* 2081 */     bbuf[this._outputTail++] = (byte)(0x80 | ch >> 6 & 0x3F);
/* 2082 */     bbuf[this._outputTail++] = (byte)(0x80 | ch & 0x3F);
/* 2083 */     return inputOffset;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _outputSurrogates(int surr1, int surr2) throws IOException {
/* 2088 */     int c = _decodeSurrogate(surr1, surr2);
/* 2089 */     if (this._outputTail + 4 > this._outputEnd) {
/* 2090 */       _flushBuffer();
/*      */     }
/* 2092 */     byte[] bbuf = this._outputBuffer;
/* 2093 */     bbuf[this._outputTail++] = (byte)(0xF0 | c >> 18);
/* 2094 */     bbuf[this._outputTail++] = (byte)(0x80 | c >> 12 & 0x3F);
/* 2095 */     bbuf[this._outputTail++] = (byte)(0x80 | c >> 6 & 0x3F);
/* 2096 */     bbuf[this._outputTail++] = (byte)(0x80 | c & 0x3F);
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
/*      */   private final int _outputMultiByteChar(int ch, int outputPtr) throws IOException {
/* 2110 */     byte[] bbuf = this._outputBuffer;
/* 2111 */     if (ch >= 55296 && ch <= 57343) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2116 */       bbuf[outputPtr++] = 92;
/* 2117 */       bbuf[outputPtr++] = 117;
/*      */       
/* 2119 */       bbuf[outputPtr++] = HEX_CHARS[ch >> 12 & 0xF];
/* 2120 */       bbuf[outputPtr++] = HEX_CHARS[ch >> 8 & 0xF];
/* 2121 */       bbuf[outputPtr++] = HEX_CHARS[ch >> 4 & 0xF];
/* 2122 */       bbuf[outputPtr++] = HEX_CHARS[ch & 0xF];
/*      */     } else {
/*      */       
/* 2125 */       bbuf[outputPtr++] = (byte)(0xE0 | ch >> 12);
/* 2126 */       bbuf[outputPtr++] = (byte)(0x80 | ch >> 6 & 0x3F);
/* 2127 */       bbuf[outputPtr++] = (byte)(0x80 | ch & 0x3F);
/*      */     } 
/* 2129 */     return outputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeNull() throws IOException {
/* 2134 */     if (this._outputTail + 4 >= this._outputEnd) {
/* 2135 */       _flushBuffer();
/*      */     }
/* 2137 */     System.arraycopy(NULL_BYTES, 0, this._outputBuffer, this._outputTail, 4);
/* 2138 */     this._outputTail += 4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int _writeGenericEscape(int charToEscape, int outputPtr) throws IOException {
/* 2148 */     byte[] bbuf = this._outputBuffer;
/* 2149 */     bbuf[outputPtr++] = 92;
/* 2150 */     bbuf[outputPtr++] = 117;
/* 2151 */     if (charToEscape > 255) {
/* 2152 */       int hi = charToEscape >> 8 & 0xFF;
/* 2153 */       bbuf[outputPtr++] = HEX_CHARS[hi >> 4];
/* 2154 */       bbuf[outputPtr++] = HEX_CHARS[hi & 0xF];
/* 2155 */       charToEscape &= 0xFF;
/*      */     } else {
/* 2157 */       bbuf[outputPtr++] = 48;
/* 2158 */       bbuf[outputPtr++] = 48;
/*      */     } 
/*      */     
/* 2161 */     bbuf[outputPtr++] = HEX_CHARS[charToEscape >> 4];
/* 2162 */     bbuf[outputPtr++] = HEX_CHARS[charToEscape & 0xF];
/* 2163 */     return outputPtr;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _flushBuffer() throws IOException {
/* 2168 */     int len = this._outputTail;
/* 2169 */     if (len > 0) {
/* 2170 */       this._outputTail = 0;
/* 2171 */       this._outputStream.write(this._outputBuffer, 0, len);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/UTF8JsonGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */