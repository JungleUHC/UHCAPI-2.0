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
/*      */ import java.io.Reader;
/*      */ import java.io.Writer;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ 
/*      */ public class WriterBasedJsonGenerator extends JsonGeneratorImpl {
/*      */   protected static final int SHORT_WRITE = 32;
/*   22 */   protected static final char[] HEX_CHARS = CharTypes.copyHexChars();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Writer _writer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char _quoteChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _outputBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _outputHead;
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
/*      */   protected int _outputEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _entityBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SerializableString _currentEscape;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] _copyBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public WriterBasedJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w) {
/*   96 */     this(ctxt, features, codec, w, '"');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public WriterBasedJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w, char quoteChar) {
/*  105 */     super(ctxt, features, codec);
/*  106 */     this._writer = w;
/*  107 */     this._outputBuffer = ctxt.allocConcatBuffer();
/*  108 */     this._outputEnd = this._outputBuffer.length;
/*  109 */     this._quoteChar = quoteChar;
/*  110 */     if (quoteChar != '"') {
/*  111 */       this._outputEscapes = CharTypes.get7BitOutputEscapes(quoteChar);
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
/*      */   public Object getOutputTarget() {
/*  123 */     return this._writer;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOutputBuffered() {
/*  129 */     int len = this._outputTail - this._outputHead;
/*  130 */     return Math.max(0, len);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean canWriteFormattedNumbers() {
/*  135 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldName(String name) throws IOException {
/*  146 */     int status = this._writeContext.writeFieldName(name);
/*  147 */     if (status == 4) {
/*  148 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  150 */     _writeFieldName(name, (status == 1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldName(SerializableString name) throws IOException {
/*  157 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  158 */     if (status == 4) {
/*  159 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  161 */     _writeFieldName(name, (status == 1));
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _writeFieldName(String name, boolean commaBefore) throws IOException {
/*  166 */     if (this._cfgPrettyPrinter != null) {
/*  167 */       _writePPFieldName(name, commaBefore);
/*      */       
/*      */       return;
/*      */     } 
/*  171 */     if (this._outputTail + 1 >= this._outputEnd) {
/*  172 */       _flushBuffer();
/*      */     }
/*  174 */     if (commaBefore) {
/*  175 */       this._outputBuffer[this._outputTail++] = ',';
/*      */     }
/*      */     
/*  178 */     if (this._cfgUnqNames) {
/*  179 */       _writeString(name);
/*      */       
/*      */       return;
/*      */     } 
/*  183 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  185 */     _writeString(name);
/*      */     
/*  187 */     if (this._outputTail >= this._outputEnd) {
/*  188 */       _flushBuffer();
/*      */     }
/*  190 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _writeFieldName(SerializableString name, boolean commaBefore) throws IOException {
/*  195 */     if (this._cfgPrettyPrinter != null) {
/*  196 */       _writePPFieldName(name, commaBefore);
/*      */       
/*      */       return;
/*      */     } 
/*  200 */     if (this._outputTail + 1 >= this._outputEnd) {
/*  201 */       _flushBuffer();
/*      */     }
/*  203 */     if (commaBefore) {
/*  204 */       this._outputBuffer[this._outputTail++] = ',';
/*      */     }
/*      */     
/*  207 */     if (this._cfgUnqNames) {
/*  208 */       char[] ch = name.asQuotedChars();
/*  209 */       writeRaw(ch, 0, ch.length);
/*      */       
/*      */       return;
/*      */     } 
/*  213 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */ 
/*      */     
/*  216 */     int len = name.appendQuoted(this._outputBuffer, this._outputTail);
/*  217 */     if (len < 0) {
/*  218 */       _writeFieldNameTail(name);
/*      */       return;
/*      */     } 
/*  221 */     this._outputTail += len;
/*  222 */     if (this._outputTail >= this._outputEnd) {
/*  223 */       _flushBuffer();
/*      */     }
/*  225 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _writeFieldNameTail(SerializableString name) throws IOException {
/*  230 */     char[] quoted = name.asQuotedChars();
/*  231 */     writeRaw(quoted, 0, quoted.length);
/*  232 */     if (this._outputTail >= this._outputEnd) {
/*  233 */       _flushBuffer();
/*      */     }
/*  235 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
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
/*      */   public void writeStartArray() throws IOException {
/*  247 */     _verifyValueWrite("start an array");
/*  248 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  249 */     if (this._cfgPrettyPrinter != null) {
/*  250 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  252 */       if (this._outputTail >= this._outputEnd) {
/*  253 */         _flushBuffer();
/*      */       }
/*  255 */       this._outputBuffer[this._outputTail++] = '[';
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(Object currentValue) throws IOException {
/*  262 */     _verifyValueWrite("start an array");
/*  263 */     this._writeContext = this._writeContext.createChildArrayContext(currentValue);
/*  264 */     if (this._cfgPrettyPrinter != null) {
/*  265 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  267 */       if (this._outputTail >= this._outputEnd) {
/*  268 */         _flushBuffer();
/*      */       }
/*  270 */       this._outputBuffer[this._outputTail++] = '[';
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartArray(Object currentValue, int size) throws IOException {
/*  277 */     _verifyValueWrite("start an array");
/*  278 */     this._writeContext = this._writeContext.createChildArrayContext(currentValue);
/*  279 */     if (this._cfgPrettyPrinter != null) {
/*  280 */       this._cfgPrettyPrinter.writeStartArray((JsonGenerator)this);
/*      */     } else {
/*  282 */       if (this._outputTail >= this._outputEnd) {
/*  283 */         _flushBuffer();
/*      */       }
/*  285 */       this._outputBuffer[this._outputTail++] = '[';
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeEndArray() throws IOException {
/*  292 */     if (!this._writeContext.inArray()) {
/*  293 */       _reportError("Current context not Array but " + this._writeContext.typeDesc());
/*      */     }
/*  295 */     if (this._cfgPrettyPrinter != null) {
/*  296 */       this._cfgPrettyPrinter.writeEndArray((JsonGenerator)this, this._writeContext.getEntryCount());
/*      */     } else {
/*  298 */       if (this._outputTail >= this._outputEnd) {
/*  299 */         _flushBuffer();
/*      */       }
/*  301 */       this._outputBuffer[this._outputTail++] = ']';
/*      */     } 
/*  303 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject() throws IOException {
/*  309 */     _verifyValueWrite("start an object");
/*  310 */     this._writeContext = this._writeContext.createChildObjectContext();
/*  311 */     if (this._cfgPrettyPrinter != null) {
/*  312 */       this._cfgPrettyPrinter.writeStartObject((JsonGenerator)this);
/*      */     } else {
/*  314 */       if (this._outputTail >= this._outputEnd) {
/*  315 */         _flushBuffer();
/*      */       }
/*  317 */       this._outputBuffer[this._outputTail++] = '{';
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue) throws IOException {
/*  324 */     _verifyValueWrite("start an object");
/*  325 */     JsonWriteContext ctxt = this._writeContext.createChildObjectContext(forValue);
/*  326 */     this._writeContext = ctxt;
/*  327 */     if (this._cfgPrettyPrinter != null) {
/*  328 */       this._cfgPrettyPrinter.writeStartObject((JsonGenerator)this);
/*      */     } else {
/*  330 */       if (this._outputTail >= this._outputEnd) {
/*  331 */         _flushBuffer();
/*      */       }
/*  333 */       this._outputBuffer[this._outputTail++] = '{';
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeEndObject() throws IOException {
/*  340 */     if (!this._writeContext.inObject()) {
/*  341 */       _reportError("Current context not Object but " + this._writeContext.typeDesc());
/*      */     }
/*  343 */     if (this._cfgPrettyPrinter != null) {
/*  344 */       this._cfgPrettyPrinter.writeEndObject((JsonGenerator)this, this._writeContext.getEntryCount());
/*      */     } else {
/*  346 */       if (this._outputTail >= this._outputEnd) {
/*  347 */         _flushBuffer();
/*      */       }
/*  349 */       this._outputBuffer[this._outputTail++] = '}';
/*      */     } 
/*  351 */     this._writeContext = this._writeContext.clearAndGetParent();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _writePPFieldName(String name, boolean commaBefore) throws IOException {
/*  358 */     if (commaBefore) {
/*  359 */       this._cfgPrettyPrinter.writeObjectEntrySeparator((JsonGenerator)this);
/*      */     } else {
/*  361 */       this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*      */     } 
/*      */     
/*  364 */     if (this._cfgUnqNames) {
/*  365 */       _writeString(name);
/*      */     } else {
/*  367 */       if (this._outputTail >= this._outputEnd) {
/*  368 */         _flushBuffer();
/*      */       }
/*  370 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  371 */       _writeString(name);
/*  372 */       if (this._outputTail >= this._outputEnd) {
/*  373 */         _flushBuffer();
/*      */       }
/*  375 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _writePPFieldName(SerializableString name, boolean commaBefore) throws IOException {
/*  381 */     if (commaBefore) {
/*  382 */       this._cfgPrettyPrinter.writeObjectEntrySeparator((JsonGenerator)this);
/*      */     } else {
/*  384 */       this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*      */     } 
/*  386 */     char[] quoted = name.asQuotedChars();
/*  387 */     if (this._cfgUnqNames) {
/*  388 */       writeRaw(quoted, 0, quoted.length);
/*      */     } else {
/*  390 */       if (this._outputTail >= this._outputEnd) {
/*  391 */         _flushBuffer();
/*      */       }
/*  393 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  394 */       writeRaw(quoted, 0, quoted.length);
/*  395 */       if (this._outputTail >= this._outputEnd) {
/*  396 */         _flushBuffer();
/*      */       }
/*  398 */       this._outputBuffer[this._outputTail++] = this._quoteChar;
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
/*  411 */     _verifyValueWrite("write a string");
/*  412 */     if (text == null) {
/*  413 */       _writeNull();
/*      */       return;
/*      */     } 
/*  416 */     if (this._outputTail >= this._outputEnd) {
/*  417 */       _flushBuffer();
/*      */     }
/*  419 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  420 */     _writeString(text);
/*      */     
/*  422 */     if (this._outputTail >= this._outputEnd) {
/*  423 */       _flushBuffer();
/*      */     }
/*  425 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(Reader reader, int len) throws IOException {
/*  431 */     _verifyValueWrite("write a string");
/*  432 */     if (reader == null) {
/*  433 */       _reportError("null reader");
/*      */       return;
/*      */     } 
/*  436 */     int toRead = (len >= 0) ? len : Integer.MAX_VALUE;
/*      */     
/*  438 */     if (this._outputTail >= this._outputEnd) {
/*  439 */       _flushBuffer();
/*      */     }
/*  441 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  443 */     char[] buf = _allocateCopyBuffer();
/*  444 */     while (toRead > 0) {
/*  445 */       int toReadNow = Math.min(toRead, buf.length);
/*  446 */       int numRead = reader.read(buf, 0, toReadNow);
/*  447 */       if (numRead <= 0) {
/*      */         break;
/*      */       }
/*  450 */       _writeString(buf, 0, numRead);
/*  451 */       toRead -= numRead;
/*      */     } 
/*      */     
/*  454 */     if (this._outputTail >= this._outputEnd) {
/*  455 */       _flushBuffer();
/*      */     }
/*  457 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */     
/*  459 */     if (toRead > 0 && len >= 0) {
/*  460 */       _reportError("Didn't read enough from reader");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(char[] text, int offset, int len) throws IOException {
/*  467 */     _verifyValueWrite("write a string");
/*  468 */     if (this._outputTail >= this._outputEnd) {
/*  469 */       _flushBuffer();
/*      */     }
/*  471 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  472 */     _writeString(text, offset, len);
/*      */     
/*  474 */     if (this._outputTail >= this._outputEnd) {
/*  475 */       _flushBuffer();
/*      */     }
/*  477 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(SerializableString sstr) throws IOException {
/*  483 */     _verifyValueWrite("write a string");
/*  484 */     if (this._outputTail >= this._outputEnd) {
/*  485 */       _flushBuffer();
/*      */     }
/*  487 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  488 */     int len = sstr.appendQuoted(this._outputBuffer, this._outputTail);
/*  489 */     if (len < 0) {
/*  490 */       _writeString2(sstr);
/*      */       return;
/*      */     } 
/*  493 */     this._outputTail += len;
/*  494 */     if (this._outputTail >= this._outputEnd) {
/*  495 */       _flushBuffer();
/*      */     }
/*  497 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeString2(SerializableString sstr) throws IOException {
/*  503 */     char[] text = sstr.asQuotedChars();
/*  504 */     int len = text.length;
/*  505 */     if (len < 32) {
/*  506 */       int room = this._outputEnd - this._outputTail;
/*  507 */       if (len > room) {
/*  508 */         _flushBuffer();
/*      */       }
/*  510 */       System.arraycopy(text, 0, this._outputBuffer, this._outputTail, len);
/*  511 */       this._outputTail += len;
/*      */     } else {
/*  513 */       _flushBuffer();
/*  514 */       this._writer.write(text, 0, len);
/*      */     } 
/*  516 */     if (this._outputTail >= this._outputEnd) {
/*  517 */       _flushBuffer();
/*      */     }
/*  519 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/*  525 */     _reportUnsupportedOperation();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
/*  531 */     _reportUnsupportedOperation();
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
/*      */   public void writeRaw(String text) throws IOException {
/*  544 */     int len = text.length();
/*  545 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  547 */     if (room == 0) {
/*  548 */       _flushBuffer();
/*  549 */       room = this._outputEnd - this._outputTail;
/*      */     } 
/*      */     
/*  552 */     if (room >= len) {
/*  553 */       text.getChars(0, len, this._outputBuffer, this._outputTail);
/*  554 */       this._outputTail += len;
/*      */     } else {
/*  556 */       writeRawLong(text);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(String text, int start, int len) throws IOException {
/*  564 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  566 */     if (room < len) {
/*  567 */       _flushBuffer();
/*  568 */       room = this._outputEnd - this._outputTail;
/*      */     } 
/*      */     
/*  571 */     if (room >= len) {
/*  572 */       text.getChars(start, start + len, this._outputBuffer, this._outputTail);
/*  573 */       this._outputTail += len;
/*      */     } else {
/*  575 */       writeRawLong(text.substring(start, start + len));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(SerializableString text) throws IOException {
/*  582 */     int len = text.appendUnquoted(this._outputBuffer, this._outputTail);
/*  583 */     if (len < 0) {
/*  584 */       writeRaw(text.getValue());
/*      */       return;
/*      */     } 
/*  587 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(char[] text, int offset, int len) throws IOException {
/*  594 */     if (len < 32) {
/*  595 */       int room = this._outputEnd - this._outputTail;
/*  596 */       if (len > room) {
/*  597 */         _flushBuffer();
/*      */       }
/*  599 */       System.arraycopy(text, offset, this._outputBuffer, this._outputTail, len);
/*  600 */       this._outputTail += len;
/*      */       
/*      */       return;
/*      */     } 
/*  604 */     _flushBuffer();
/*  605 */     this._writer.write(text, offset, len);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(char c) throws IOException {
/*  611 */     if (this._outputTail >= this._outputEnd) {
/*  612 */       _flushBuffer();
/*      */     }
/*  614 */     this._outputBuffer[this._outputTail++] = c;
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeRawLong(String text) throws IOException {
/*  619 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  621 */     text.getChars(0, room, this._outputBuffer, this._outputTail);
/*  622 */     this._outputTail += room;
/*  623 */     _flushBuffer();
/*  624 */     int offset = room;
/*  625 */     int len = text.length() - room;
/*      */     
/*  627 */     while (len > this._outputEnd) {
/*  628 */       int amount = this._outputEnd;
/*  629 */       text.getChars(offset, offset + amount, this._outputBuffer, 0);
/*  630 */       this._outputHead = 0;
/*  631 */       this._outputTail = amount;
/*  632 */       _flushBuffer();
/*  633 */       offset += amount;
/*  634 */       len -= amount;
/*      */     } 
/*      */     
/*  637 */     text.getChars(offset, offset + len, this._outputBuffer, 0);
/*  638 */     this._outputHead = 0;
/*  639 */     this._outputTail = len;
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
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
/*  652 */     _verifyValueWrite("write a binary value");
/*      */     
/*  654 */     if (this._outputTail >= this._outputEnd) {
/*  655 */       _flushBuffer();
/*      */     }
/*  657 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  658 */     _writeBinary(b64variant, data, offset, offset + len);
/*      */     
/*  660 */     if (this._outputTail >= this._outputEnd) {
/*  661 */       _flushBuffer();
/*      */     }
/*  663 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException, JsonGenerationException {
/*      */     int bytes;
/*  671 */     _verifyValueWrite("write a binary value");
/*      */     
/*  673 */     if (this._outputTail >= this._outputEnd) {
/*  674 */       _flushBuffer();
/*      */     }
/*  676 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  677 */     byte[] encodingBuffer = this._ioContext.allocBase64Buffer();
/*      */     
/*      */     try {
/*  680 */       if (dataLength < 0) {
/*  681 */         bytes = _writeBinary(b64variant, data, encodingBuffer);
/*      */       } else {
/*  683 */         int missing = _writeBinary(b64variant, data, encodingBuffer, dataLength);
/*  684 */         if (missing > 0) {
/*  685 */           _reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
/*      */         }
/*  687 */         bytes = dataLength;
/*      */       } 
/*      */     } finally {
/*  690 */       this._ioContext.releaseBase64Buffer(encodingBuffer);
/*      */     } 
/*      */     
/*  693 */     if (this._outputTail >= this._outputEnd) {
/*  694 */       _flushBuffer();
/*      */     }
/*  696 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  697 */     return bytes;
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
/*  709 */     _verifyValueWrite("write a number");
/*  710 */     if (this._cfgNumbersAsStrings) {
/*  711 */       _writeQuotedShort(s);
/*      */       
/*      */       return;
/*      */     } 
/*  715 */     if (this._outputTail + 6 >= this._outputEnd) {
/*  716 */       _flushBuffer();
/*      */     }
/*  718 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private void _writeQuotedShort(short s) throws IOException {
/*  722 */     if (this._outputTail + 8 >= this._outputEnd) {
/*  723 */       _flushBuffer();
/*      */     }
/*  725 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  726 */     this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*  727 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(int i) throws IOException {
/*  733 */     _verifyValueWrite("write a number");
/*  734 */     if (this._cfgNumbersAsStrings) {
/*  735 */       _writeQuotedInt(i);
/*      */       
/*      */       return;
/*      */     } 
/*  739 */     if (this._outputTail + 11 >= this._outputEnd) {
/*  740 */       _flushBuffer();
/*      */     }
/*  742 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private void _writeQuotedInt(int i) throws IOException {
/*  746 */     if (this._outputTail + 13 >= this._outputEnd) {
/*  747 */       _flushBuffer();
/*      */     }
/*  749 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  750 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*  751 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(long l) throws IOException {
/*  757 */     _verifyValueWrite("write a number");
/*  758 */     if (this._cfgNumbersAsStrings) {
/*  759 */       _writeQuotedLong(l);
/*      */       return;
/*      */     } 
/*  762 */     if (this._outputTail + 21 >= this._outputEnd)
/*      */     {
/*  764 */       _flushBuffer();
/*      */     }
/*  766 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private void _writeQuotedLong(long l) throws IOException {
/*  770 */     if (this._outputTail + 23 >= this._outputEnd) {
/*  771 */       _flushBuffer();
/*      */     }
/*  773 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  774 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*  775 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigInteger value) throws IOException {
/*  783 */     _verifyValueWrite("write a number");
/*  784 */     if (value == null) {
/*  785 */       _writeNull();
/*  786 */     } else if (this._cfgNumbersAsStrings) {
/*  787 */       _writeQuotedRaw(value.toString());
/*      */     } else {
/*  789 */       writeRaw(value.toString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(double d) throws IOException {
/*  797 */     if (this._cfgNumbersAsStrings || (
/*  798 */       NumberOutput.notFinite(d) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
/*  799 */       writeString(String.valueOf(d));
/*      */       
/*      */       return;
/*      */     } 
/*  803 */     _verifyValueWrite("write a number");
/*  804 */     writeRaw(String.valueOf(d));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(float f) throws IOException {
/*  811 */     if (this._cfgNumbersAsStrings || (
/*  812 */       NumberOutput.notFinite(f) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
/*  813 */       writeString(String.valueOf(f));
/*      */       
/*      */       return;
/*      */     } 
/*  817 */     _verifyValueWrite("write a number");
/*  818 */     writeRaw(String.valueOf(f));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(BigDecimal value) throws IOException {
/*  825 */     _verifyValueWrite("write a number");
/*  826 */     if (value == null) {
/*  827 */       _writeNull();
/*  828 */     } else if (this._cfgNumbersAsStrings) {
/*  829 */       _writeQuotedRaw(_asString(value));
/*      */     } else {
/*  831 */       writeRaw(_asString(value));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(String encodedValue) throws IOException {
/*  838 */     _verifyValueWrite("write a number");
/*  839 */     if (encodedValue == null) {
/*  840 */       _writeNull();
/*  841 */     } else if (this._cfgNumbersAsStrings) {
/*  842 */       _writeQuotedRaw(encodedValue);
/*      */     } else {
/*  844 */       writeRaw(encodedValue);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNumber(char[] encodedValueBuffer, int offset, int length) throws IOException {
/*  850 */     _verifyValueWrite("write a number");
/*  851 */     if (this._cfgNumbersAsStrings) {
/*  852 */       _writeQuotedRaw(encodedValueBuffer, offset, length);
/*      */     } else {
/*  854 */       writeRaw(encodedValueBuffer, offset, length);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void _writeQuotedRaw(String value) throws IOException {
/*  860 */     if (this._outputTail >= this._outputEnd) {
/*  861 */       _flushBuffer();
/*      */     }
/*  863 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  864 */     writeRaw(value);
/*  865 */     if (this._outputTail >= this._outputEnd) {
/*  866 */       _flushBuffer();
/*      */     }
/*  868 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */   
/*      */   private void _writeQuotedRaw(char[] text, int offset, int length) throws IOException {
/*  873 */     if (this._outputTail >= this._outputEnd) {
/*  874 */       _flushBuffer();
/*      */     }
/*  876 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*  877 */     writeRaw(text, offset, length);
/*  878 */     if (this._outputTail >= this._outputEnd) {
/*  879 */       _flushBuffer();
/*      */     }
/*  881 */     this._outputBuffer[this._outputTail++] = this._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBoolean(boolean state) throws IOException {
/*  887 */     _verifyValueWrite("write a boolean value");
/*  888 */     if (this._outputTail + 5 >= this._outputEnd) {
/*  889 */       _flushBuffer();
/*      */     }
/*  891 */     int ptr = this._outputTail;
/*  892 */     char[] buf = this._outputBuffer;
/*  893 */     if (state) {
/*  894 */       buf[ptr] = 't';
/*  895 */       buf[++ptr] = 'r';
/*  896 */       buf[++ptr] = 'u';
/*  897 */       buf[++ptr] = 'e';
/*      */     } else {
/*  899 */       buf[ptr] = 'f';
/*  900 */       buf[++ptr] = 'a';
/*  901 */       buf[++ptr] = 'l';
/*  902 */       buf[++ptr] = 's';
/*  903 */       buf[++ptr] = 'e';
/*      */     } 
/*  905 */     this._outputTail = ptr + 1;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNull() throws IOException {
/*  910 */     _verifyValueWrite("write a null");
/*  911 */     _writeNull();
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
/*      */     char c;
/*  923 */     int status = this._writeContext.writeValue();
/*  924 */     if (this._cfgPrettyPrinter != null) {
/*      */       
/*  926 */       _verifyPrettyValueWrite(typeMsg, status);
/*      */       
/*      */       return;
/*      */     } 
/*  930 */     switch (status) {
/*      */       default:
/*      */         return;
/*      */       
/*      */       case 1:
/*  935 */         c = ',';
/*      */         break;
/*      */       case 2:
/*  938 */         c = ':';
/*      */         break;
/*      */       case 3:
/*  941 */         if (this._rootValueSeparator != null) {
/*  942 */           writeRaw(this._rootValueSeparator.getValue());
/*      */         }
/*      */         return;
/*      */       case 5:
/*  946 */         _reportCantWriteValueExpectName(typeMsg);
/*      */         return;
/*      */     } 
/*  949 */     if (this._outputTail >= this._outputEnd) {
/*  950 */       _flushBuffer();
/*      */     }
/*  952 */     this._outputBuffer[this._outputTail++] = c;
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
/*  964 */     _flushBuffer();
/*  965 */     if (this._writer != null && 
/*  966 */       isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/*  967 */       this._writer.flush();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/*  975 */     super.close();
/*      */ 
/*      */ 
/*      */     
/*  979 */     if (this._outputBuffer != null && 
/*  980 */       isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
/*      */       while (true) {
/*  982 */         JsonStreamContext ctxt = getOutputContext();
/*  983 */         if (ctxt.inArray()) {
/*  984 */           writeEndArray(); continue;
/*  985 */         }  if (ctxt.inObject()) {
/*  986 */           writeEndObject();
/*      */           continue;
/*      */         } 
/*      */         break;
/*      */       } 
/*      */     }
/*  992 */     _flushBuffer();
/*  993 */     this._outputHead = 0;
/*  994 */     this._outputTail = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1002 */     if (this._writer != null) {
/* 1003 */       if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
/* 1004 */         this._writer.close();
/* 1005 */       } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/*      */         
/* 1007 */         this._writer.flush();
/*      */       } 
/*      */     }
/*      */     
/* 1011 */     _releaseBuffers();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _releaseBuffers() {
/* 1017 */     char[] buf = this._outputBuffer;
/* 1018 */     if (buf != null) {
/* 1019 */       this._outputBuffer = null;
/* 1020 */       this._ioContext.releaseConcatBuffer(buf);
/*      */     } 
/* 1022 */     buf = this._copyBuffer;
/* 1023 */     if (buf != null) {
/* 1024 */       this._copyBuffer = null;
/* 1025 */       this._ioContext.releaseNameCopyBuffer(buf);
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
/*      */   private void _writeString(String text) throws IOException {
/* 1042 */     int len = text.length();
/* 1043 */     if (len > this._outputEnd) {
/* 1044 */       _writeLongString(text);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1050 */     if (this._outputTail + len > this._outputEnd) {
/* 1051 */       _flushBuffer();
/*      */     }
/* 1053 */     text.getChars(0, len, this._outputBuffer, this._outputTail);
/*      */     
/* 1055 */     if (this._characterEscapes != null) {
/* 1056 */       _writeStringCustom(len);
/* 1057 */     } else if (this._maximumNonEscapedChar != 0) {
/* 1058 */       _writeStringASCII(len, this._maximumNonEscapedChar);
/*      */     } else {
/* 1060 */       _writeString2(len);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeString2(int len) throws IOException {
/* 1067 */     int end = this._outputTail + len;
/* 1068 */     int[] escCodes = this._outputEscapes;
/* 1069 */     int escLen = escCodes.length;
/*      */ 
/*      */     
/* 1072 */     while (this._outputTail < end) {
/*      */       
/*      */       label17: while (true) {
/*      */         
/* 1076 */         char c = this._outputBuffer[this._outputTail];
/* 1077 */         if (c < escLen && escCodes[c] != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1089 */           int flushLen = this._outputTail - this._outputHead;
/* 1090 */           if (flushLen > 0) {
/* 1091 */             this._writer.write(this._outputBuffer, this._outputHead, flushLen);
/*      */             
/*      */             break label17;
/*      */           } 
/*      */           
/* 1096 */           char c1 = this._outputBuffer[this._outputTail++];
/* 1097 */           _prependOrWriteCharacterEscape(c1, escCodes[c1]);
/*      */           continue;
/*      */         } 
/*      */         if (++this._outputTail >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void _writeLongString(String text) throws IOException {
/* 1108 */     _flushBuffer();
/*      */ 
/*      */     
/* 1111 */     int textLen = text.length();
/* 1112 */     int offset = 0;
/*      */     do {
/* 1114 */       int max = this._outputEnd;
/* 1115 */       int segmentLen = (offset + max > textLen) ? (textLen - offset) : max;
/*      */       
/* 1117 */       text.getChars(offset, offset + segmentLen, this._outputBuffer, 0);
/* 1118 */       if (this._characterEscapes != null) {
/* 1119 */         _writeSegmentCustom(segmentLen);
/* 1120 */       } else if (this._maximumNonEscapedChar != 0) {
/* 1121 */         _writeSegmentASCII(segmentLen, this._maximumNonEscapedChar);
/*      */       } else {
/* 1123 */         _writeSegment(segmentLen);
/*      */       } 
/* 1125 */       offset += segmentLen;
/* 1126 */     } while (offset < textLen);
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
/*      */   private void _writeSegment(int end) throws IOException {
/* 1140 */     int[] escCodes = this._outputEscapes;
/* 1141 */     int escLen = escCodes.length;
/*      */     
/* 1143 */     int ptr = 0;
/* 1144 */     int start = ptr;
/*      */ 
/*      */     
/* 1147 */     while (ptr < end) {
/*      */       char c;
/*      */       
/*      */       do {
/* 1151 */         c = this._outputBuffer[ptr];
/* 1152 */         if (c < escLen && escCodes[c] != 0) {
/*      */           break;
/*      */         }
/* 1155 */       } while (++ptr < end);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1164 */       int flushLen = ptr - start;
/* 1165 */       if (flushLen > 0) {
/* 1166 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1167 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/* 1171 */       ptr++;
/*      */       
/* 1173 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCodes[c]);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeString(char[] text, int offset, int len) throws IOException {
/* 1183 */     if (this._characterEscapes != null) {
/* 1184 */       _writeStringCustom(text, offset, len);
/*      */       return;
/*      */     } 
/* 1187 */     if (this._maximumNonEscapedChar != 0) {
/* 1188 */       _writeStringASCII(text, offset, len, this._maximumNonEscapedChar);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/* 1195 */     len += offset;
/* 1196 */     int[] escCodes = this._outputEscapes;
/* 1197 */     int escLen = escCodes.length;
/* 1198 */     while (offset < len) {
/* 1199 */       int start = offset;
/*      */       
/*      */       do {
/* 1202 */         char c1 = text[offset];
/* 1203 */         if (c1 < escLen && escCodes[c1] != 0) {
/*      */           break;
/*      */         }
/* 1206 */       } while (++offset < len);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1212 */       int newAmount = offset - start;
/* 1213 */       if (newAmount < 32) {
/*      */         
/* 1215 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1216 */           _flushBuffer();
/*      */         }
/* 1218 */         if (newAmount > 0) {
/* 1219 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1220 */           this._outputTail += newAmount;
/*      */         } 
/*      */       } else {
/* 1223 */         _flushBuffer();
/* 1224 */         this._writer.write(text, start, newAmount);
/*      */       } 
/*      */       
/* 1227 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1231 */       char c = text[offset++];
/* 1232 */       _appendCharacterEscape(c, escCodes[c]);
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
/*      */   private void _writeStringASCII(int len, int maxNonEscaped) throws IOException, JsonGenerationException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield _outputTail : I
/*      */     //   4: iload_1
/*      */     //   5: iadd
/*      */     //   6: istore_3
/*      */     //   7: aload_0
/*      */     //   8: getfield _outputEscapes : [I
/*      */     //   11: astore #4
/*      */     //   13: aload #4
/*      */     //   15: arraylength
/*      */     //   16: iload_2
/*      */     //   17: iconst_1
/*      */     //   18: iadd
/*      */     //   19: invokestatic min : (II)I
/*      */     //   22: istore #5
/*      */     //   24: iconst_0
/*      */     //   25: istore #6
/*      */     //   27: aload_0
/*      */     //   28: getfield _outputTail : I
/*      */     //   31: iload_3
/*      */     //   32: if_icmpge -> 152
/*      */     //   35: aload_0
/*      */     //   36: getfield _outputBuffer : [C
/*      */     //   39: aload_0
/*      */     //   40: getfield _outputTail : I
/*      */     //   43: caload
/*      */     //   44: istore #7
/*      */     //   46: iload #7
/*      */     //   48: iload #5
/*      */     //   50: if_icmpge -> 68
/*      */     //   53: aload #4
/*      */     //   55: iload #7
/*      */     //   57: iaload
/*      */     //   58: istore #6
/*      */     //   60: iload #6
/*      */     //   62: ifeq -> 80
/*      */     //   65: goto -> 98
/*      */     //   68: iload #7
/*      */     //   70: iload_2
/*      */     //   71: if_icmple -> 80
/*      */     //   74: iconst_m1
/*      */     //   75: istore #6
/*      */     //   77: goto -> 98
/*      */     //   80: aload_0
/*      */     //   81: dup
/*      */     //   82: getfield _outputTail : I
/*      */     //   85: iconst_1
/*      */     //   86: iadd
/*      */     //   87: dup_x1
/*      */     //   88: putfield _outputTail : I
/*      */     //   91: iload_3
/*      */     //   92: if_icmplt -> 35
/*      */     //   95: goto -> 152
/*      */     //   98: aload_0
/*      */     //   99: getfield _outputTail : I
/*      */     //   102: aload_0
/*      */     //   103: getfield _outputHead : I
/*      */     //   106: isub
/*      */     //   107: istore #8
/*      */     //   109: iload #8
/*      */     //   111: ifle -> 131
/*      */     //   114: aload_0
/*      */     //   115: getfield _writer : Ljava/io/Writer;
/*      */     //   118: aload_0
/*      */     //   119: getfield _outputBuffer : [C
/*      */     //   122: aload_0
/*      */     //   123: getfield _outputHead : I
/*      */     //   126: iload #8
/*      */     //   128: invokevirtual write : ([CII)V
/*      */     //   131: aload_0
/*      */     //   132: dup
/*      */     //   133: getfield _outputTail : I
/*      */     //   136: iconst_1
/*      */     //   137: iadd
/*      */     //   138: putfield _outputTail : I
/*      */     //   141: aload_0
/*      */     //   142: iload #7
/*      */     //   144: iload #6
/*      */     //   146: invokespecial _prependOrWriteCharacterEscape : (CI)V
/*      */     //   149: goto -> 27
/*      */     //   152: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #1250	-> 0
/*      */     //   #1251	-> 7
/*      */     //   #1252	-> 13
/*      */     //   #1253	-> 24
/*      */     //   #1256	-> 27
/*      */     //   #1261	-> 35
/*      */     //   #1262	-> 46
/*      */     //   #1263	-> 53
/*      */     //   #1264	-> 60
/*      */     //   #1265	-> 65
/*      */     //   #1267	-> 68
/*      */     //   #1268	-> 74
/*      */     //   #1269	-> 77
/*      */     //   #1271	-> 80
/*      */     //   #1272	-> 95
/*      */     //   #1275	-> 98
/*      */     //   #1276	-> 109
/*      */     //   #1277	-> 114
/*      */     //   #1279	-> 131
/*      */     //   #1280	-> 141
/*      */     //   #1281	-> 149
/*      */     //   #1282	-> 152
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   46	103	7	c	C
/*      */     //   109	40	8	flushLen	I
/*      */     //   0	153	0	this	Lcom/fasterxml/jackson/core/json/WriterBasedJsonGenerator;
/*      */     //   0	153	1	len	I
/*      */     //   0	153	2	maxNonEscaped	I
/*      */     //   7	146	3	end	I
/*      */     //   13	140	4	escCodes	[I
/*      */     //   24	129	5	escLimit	I
/*      */     //   27	126	6	escCode	I
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
/*      */   private void _writeSegmentASCII(int end, int maxNonEscaped) throws IOException, JsonGenerationException {
/* 1287 */     int[] escCodes = this._outputEscapes;
/* 1288 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/*      */     
/* 1290 */     int ptr = 0;
/* 1291 */     int escCode = 0;
/* 1292 */     int start = ptr;
/*      */ 
/*      */     
/* 1295 */     while (ptr < end) {
/*      */       char c;
/*      */       
/*      */       do {
/* 1299 */         c = this._outputBuffer[ptr];
/* 1300 */         if (c < escLimit) {
/* 1301 */           escCode = escCodes[c];
/* 1302 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1305 */         } else if (c > maxNonEscaped) {
/* 1306 */           escCode = -1;
/*      */           break;
/*      */         } 
/* 1309 */       } while (++ptr < end);
/*      */ 
/*      */ 
/*      */       
/* 1313 */       int flushLen = ptr - start;
/* 1314 */       if (flushLen > 0) {
/* 1315 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1316 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/* 1320 */       ptr++;
/* 1321 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeStringASCII(char[] text, int offset, int len, int maxNonEscaped) throws IOException, JsonGenerationException {
/* 1329 */     len += offset;
/* 1330 */     int[] escCodes = this._outputEscapes;
/* 1331 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/*      */     
/* 1333 */     int escCode = 0;
/*      */     
/* 1335 */     while (offset < len) {
/* 1336 */       char c; int start = offset;
/*      */ 
/*      */       
/*      */       do {
/* 1340 */         c = text[offset];
/* 1341 */         if (c < escLimit) {
/* 1342 */           escCode = escCodes[c];
/* 1343 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1346 */         } else if (c > maxNonEscaped) {
/* 1347 */           escCode = -1;
/*      */           break;
/*      */         } 
/* 1350 */       } while (++offset < len);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1356 */       int newAmount = offset - start;
/* 1357 */       if (newAmount < 32) {
/*      */         
/* 1359 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1360 */           _flushBuffer();
/*      */         }
/* 1362 */         if (newAmount > 0) {
/* 1363 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1364 */           this._outputTail += newAmount;
/*      */         } 
/*      */       } else {
/* 1367 */         _flushBuffer();
/* 1368 */         this._writer.write(text, start, newAmount);
/*      */       } 
/*      */       
/* 1371 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1375 */       offset++;
/* 1376 */       _appendCharacterEscape(c, escCode);
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
/*      */   private void _writeStringCustom(int len) throws IOException, JsonGenerationException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield _outputTail : I
/*      */     //   4: iload_1
/*      */     //   5: iadd
/*      */     //   6: istore_2
/*      */     //   7: aload_0
/*      */     //   8: getfield _outputEscapes : [I
/*      */     //   11: astore_3
/*      */     //   12: aload_0
/*      */     //   13: getfield _maximumNonEscapedChar : I
/*      */     //   16: iconst_1
/*      */     //   17: if_icmpge -> 25
/*      */     //   20: ldc 65535
/*      */     //   22: goto -> 29
/*      */     //   25: aload_0
/*      */     //   26: getfield _maximumNonEscapedChar : I
/*      */     //   29: istore #4
/*      */     //   31: aload_3
/*      */     //   32: arraylength
/*      */     //   33: iload #4
/*      */     //   35: iconst_1
/*      */     //   36: iadd
/*      */     //   37: invokestatic min : (II)I
/*      */     //   40: istore #5
/*      */     //   42: iconst_0
/*      */     //   43: istore #6
/*      */     //   45: aload_0
/*      */     //   46: getfield _characterEscapes : Lcom/fasterxml/jackson/core/io/CharacterEscapes;
/*      */     //   49: astore #7
/*      */     //   51: aload_0
/*      */     //   52: getfield _outputTail : I
/*      */     //   55: iload_2
/*      */     //   56: if_icmpge -> 198
/*      */     //   59: aload_0
/*      */     //   60: getfield _outputBuffer : [C
/*      */     //   63: aload_0
/*      */     //   64: getfield _outputTail : I
/*      */     //   67: caload
/*      */     //   68: istore #8
/*      */     //   70: iload #8
/*      */     //   72: iload #5
/*      */     //   74: if_icmpge -> 91
/*      */     //   77: aload_3
/*      */     //   78: iload #8
/*      */     //   80: iaload
/*      */     //   81: istore #6
/*      */     //   83: iload #6
/*      */     //   85: ifeq -> 126
/*      */     //   88: goto -> 144
/*      */     //   91: iload #8
/*      */     //   93: iload #4
/*      */     //   95: if_icmple -> 104
/*      */     //   98: iconst_m1
/*      */     //   99: istore #6
/*      */     //   101: goto -> 144
/*      */     //   104: aload_0
/*      */     //   105: aload #7
/*      */     //   107: iload #8
/*      */     //   109: invokevirtual getEscapeSequence : (I)Lcom/fasterxml/jackson/core/SerializableString;
/*      */     //   112: dup_x1
/*      */     //   113: putfield _currentEscape : Lcom/fasterxml/jackson/core/SerializableString;
/*      */     //   116: ifnull -> 126
/*      */     //   119: bipush #-2
/*      */     //   121: istore #6
/*      */     //   123: goto -> 144
/*      */     //   126: aload_0
/*      */     //   127: dup
/*      */     //   128: getfield _outputTail : I
/*      */     //   131: iconst_1
/*      */     //   132: iadd
/*      */     //   133: dup_x1
/*      */     //   134: putfield _outputTail : I
/*      */     //   137: iload_2
/*      */     //   138: if_icmplt -> 59
/*      */     //   141: goto -> 198
/*      */     //   144: aload_0
/*      */     //   145: getfield _outputTail : I
/*      */     //   148: aload_0
/*      */     //   149: getfield _outputHead : I
/*      */     //   152: isub
/*      */     //   153: istore #9
/*      */     //   155: iload #9
/*      */     //   157: ifle -> 177
/*      */     //   160: aload_0
/*      */     //   161: getfield _writer : Ljava/io/Writer;
/*      */     //   164: aload_0
/*      */     //   165: getfield _outputBuffer : [C
/*      */     //   168: aload_0
/*      */     //   169: getfield _outputHead : I
/*      */     //   172: iload #9
/*      */     //   174: invokevirtual write : ([CII)V
/*      */     //   177: aload_0
/*      */     //   178: dup
/*      */     //   179: getfield _outputTail : I
/*      */     //   182: iconst_1
/*      */     //   183: iadd
/*      */     //   184: putfield _outputTail : I
/*      */     //   187: aload_0
/*      */     //   188: iload #8
/*      */     //   190: iload #6
/*      */     //   192: invokespecial _prependOrWriteCharacterEscape : (CI)V
/*      */     //   195: goto -> 51
/*      */     //   198: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #1394	-> 0
/*      */     //   #1395	-> 7
/*      */     //   #1396	-> 12
/*      */     //   #1397	-> 31
/*      */     //   #1398	-> 42
/*      */     //   #1399	-> 45
/*      */     //   #1402	-> 51
/*      */     //   #1407	-> 59
/*      */     //   #1408	-> 70
/*      */     //   #1409	-> 77
/*      */     //   #1410	-> 83
/*      */     //   #1411	-> 88
/*      */     //   #1413	-> 91
/*      */     //   #1414	-> 98
/*      */     //   #1415	-> 101
/*      */     //   #1417	-> 104
/*      */     //   #1418	-> 119
/*      */     //   #1419	-> 123
/*      */     //   #1422	-> 126
/*      */     //   #1423	-> 141
/*      */     //   #1426	-> 144
/*      */     //   #1427	-> 155
/*      */     //   #1428	-> 160
/*      */     //   #1430	-> 177
/*      */     //   #1431	-> 187
/*      */     //   #1432	-> 195
/*      */     //   #1433	-> 198
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   70	125	8	c	C
/*      */     //   155	40	9	flushLen	I
/*      */     //   0	199	0	this	Lcom/fasterxml/jackson/core/json/WriterBasedJsonGenerator;
/*      */     //   0	199	1	len	I
/*      */     //   7	192	2	end	I
/*      */     //   12	187	3	escCodes	[I
/*      */     //   31	168	4	maxNonEscaped	I
/*      */     //   42	157	5	escLimit	I
/*      */     //   45	154	6	escCode	I
/*      */     //   51	148	7	customEscapes	Lcom/fasterxml/jackson/core/io/CharacterEscapes;
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
/*      */   private void _writeSegmentCustom(int end) throws IOException, JsonGenerationException {
/* 1438 */     int[] escCodes = this._outputEscapes;
/* 1439 */     int maxNonEscaped = (this._maximumNonEscapedChar < 1) ? 65535 : this._maximumNonEscapedChar;
/* 1440 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1441 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1443 */     int ptr = 0;
/* 1444 */     int escCode = 0;
/* 1445 */     int start = ptr;
/*      */ 
/*      */     
/* 1448 */     while (ptr < end) {
/*      */       char c;
/*      */       
/*      */       do {
/* 1452 */         c = this._outputBuffer[ptr];
/* 1453 */         if (c < escLimit) {
/* 1454 */           escCode = escCodes[c];
/* 1455 */           if (escCode != 0)
/*      */             break; 
/*      */         } else {
/* 1458 */           if (c > maxNonEscaped) {
/* 1459 */             escCode = -1;
/*      */             break;
/*      */           } 
/* 1462 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1463 */             escCode = -2;
/*      */             break;
/*      */           } 
/*      */         } 
/* 1467 */       } while (++ptr < end);
/*      */ 
/*      */ 
/*      */       
/* 1471 */       int flushLen = ptr - start;
/* 1472 */       if (flushLen > 0) {
/* 1473 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1474 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       } 
/* 1478 */       ptr++;
/* 1479 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void _writeStringCustom(char[] text, int offset, int len) throws IOException, JsonGenerationException {
/* 1486 */     len += offset;
/* 1487 */     int[] escCodes = this._outputEscapes;
/* 1488 */     int maxNonEscaped = (this._maximumNonEscapedChar < 1) ? 65535 : this._maximumNonEscapedChar;
/* 1489 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1490 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1492 */     int escCode = 0;
/*      */     
/* 1494 */     while (offset < len) {
/* 1495 */       char c; int start = offset;
/*      */ 
/*      */       
/*      */       do {
/* 1499 */         c = text[offset];
/* 1500 */         if (c < escLimit) {
/* 1501 */           escCode = escCodes[c];
/* 1502 */           if (escCode != 0)
/*      */             break; 
/*      */         } else {
/* 1505 */           if (c > maxNonEscaped) {
/* 1506 */             escCode = -1;
/*      */             break;
/*      */           } 
/* 1509 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1510 */             escCode = -2;
/*      */             break;
/*      */           } 
/*      */         } 
/* 1514 */       } while (++offset < len);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1520 */       int newAmount = offset - start;
/* 1521 */       if (newAmount < 32) {
/*      */         
/* 1523 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1524 */           _flushBuffer();
/*      */         }
/* 1526 */         if (newAmount > 0) {
/* 1527 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1528 */           this._outputTail += newAmount;
/*      */         } 
/*      */       } else {
/* 1531 */         _flushBuffer();
/* 1532 */         this._writer.write(text, start, newAmount);
/*      */       } 
/*      */       
/* 1535 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1539 */       offset++;
/* 1540 */       _appendCharacterEscape(c, escCode);
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
/*      */   protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd) throws IOException, JsonGenerationException {
/* 1554 */     int safeInputEnd = inputEnd - 3;
/*      */     
/* 1556 */     int safeOutputEnd = this._outputEnd - 6;
/* 1557 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */ 
/*      */     
/* 1560 */     while (inputPtr <= safeInputEnd) {
/* 1561 */       if (this._outputTail > safeOutputEnd) {
/* 1562 */         _flushBuffer();
/*      */       }
/*      */       
/* 1565 */       int b24 = input[inputPtr++] << 8;
/* 1566 */       b24 |= input[inputPtr++] & 0xFF;
/* 1567 */       b24 = b24 << 8 | input[inputPtr++] & 0xFF;
/* 1568 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1569 */       if (--chunksBeforeLF <= 0) {
/*      */         
/* 1571 */         this._outputBuffer[this._outputTail++] = '\\';
/* 1572 */         this._outputBuffer[this._outputTail++] = 'n';
/* 1573 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1578 */     int inputLeft = inputEnd - inputPtr;
/* 1579 */     if (inputLeft > 0) {
/* 1580 */       if (this._outputTail > safeOutputEnd) {
/* 1581 */         _flushBuffer();
/*      */       }
/* 1583 */       int b24 = input[inputPtr++] << 16;
/* 1584 */       if (inputLeft == 2) {
/* 1585 */         b24 |= (input[inputPtr++] & 0xFF) << 8;
/*      */       }
/* 1587 */       this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft) throws IOException, JsonGenerationException {
/* 1596 */     int inputPtr = 0;
/* 1597 */     int inputEnd = 0;
/* 1598 */     int lastFullOffset = -3;
/*      */ 
/*      */     
/* 1601 */     int safeOutputEnd = this._outputEnd - 6;
/* 1602 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/* 1604 */     while (bytesLeft > 2) {
/* 1605 */       if (inputPtr > lastFullOffset) {
/* 1606 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1607 */         inputPtr = 0;
/* 1608 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1611 */         lastFullOffset = inputEnd - 3;
/*      */       } 
/* 1613 */       if (this._outputTail > safeOutputEnd) {
/* 1614 */         _flushBuffer();
/*      */       }
/* 1616 */       int b24 = readBuffer[inputPtr++] << 8;
/* 1617 */       b24 |= readBuffer[inputPtr++] & 0xFF;
/* 1618 */       b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/* 1619 */       bytesLeft -= 3;
/* 1620 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1621 */       if (--chunksBeforeLF <= 0) {
/* 1622 */         this._outputBuffer[this._outputTail++] = '\\';
/* 1623 */         this._outputBuffer[this._outputTail++] = 'n';
/* 1624 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1629 */     if (bytesLeft > 0) {
/* 1630 */       inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/* 1631 */       inputPtr = 0;
/* 1632 */       if (inputEnd > 0) {
/* 1633 */         int amount; if (this._outputTail > safeOutputEnd) {
/* 1634 */           _flushBuffer();
/*      */         }
/* 1636 */         int b24 = readBuffer[inputPtr++] << 16;
/*      */         
/* 1638 */         if (inputPtr < inputEnd) {
/* 1639 */           b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1640 */           amount = 2;
/*      */         } else {
/* 1642 */           amount = 1;
/*      */         } 
/* 1644 */         this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/* 1645 */         bytesLeft -= amount;
/*      */       } 
/*      */     } 
/* 1648 */     return bytesLeft;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer) throws IOException, JsonGenerationException {
/* 1656 */     int inputPtr = 0;
/* 1657 */     int inputEnd = 0;
/* 1658 */     int lastFullOffset = -3;
/* 1659 */     int bytesDone = 0;
/*      */ 
/*      */     
/* 1662 */     int safeOutputEnd = this._outputEnd - 6;
/* 1663 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */ 
/*      */     
/*      */     while (true) {
/* 1667 */       if (inputPtr > lastFullOffset) {
/* 1668 */         inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
/* 1669 */         inputPtr = 0;
/* 1670 */         if (inputEnd < 3) {
/*      */           break;
/*      */         }
/* 1673 */         lastFullOffset = inputEnd - 3;
/*      */       } 
/* 1675 */       if (this._outputTail > safeOutputEnd) {
/* 1676 */         _flushBuffer();
/*      */       }
/*      */       
/* 1679 */       int b24 = readBuffer[inputPtr++] << 8;
/* 1680 */       b24 |= readBuffer[inputPtr++] & 0xFF;
/* 1681 */       b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/* 1682 */       bytesDone += 3;
/* 1683 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1684 */       if (--chunksBeforeLF <= 0) {
/* 1685 */         this._outputBuffer[this._outputTail++] = '\\';
/* 1686 */         this._outputBuffer[this._outputTail++] = 'n';
/* 1687 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1692 */     if (inputPtr < inputEnd) {
/* 1693 */       if (this._outputTail > safeOutputEnd) {
/* 1694 */         _flushBuffer();
/*      */       }
/* 1696 */       int b24 = readBuffer[inputPtr++] << 16;
/* 1697 */       int amount = 1;
/* 1698 */       if (inputPtr < inputEnd) {
/* 1699 */         b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/* 1700 */         amount = 2;
/*      */       } 
/* 1702 */       bytesDone += amount;
/* 1703 */       this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/*      */     } 
/* 1705 */     return bytesDone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws IOException {
/* 1713 */     int i = 0;
/* 1714 */     while (inputPtr < inputEnd) {
/* 1715 */       readBuffer[i++] = readBuffer[inputPtr++];
/*      */     }
/* 1717 */     inputPtr = 0;
/* 1718 */     inputEnd = i;
/* 1719 */     maxRead = Math.min(maxRead, readBuffer.length);
/*      */     
/*      */     do {
/* 1722 */       int length = maxRead - inputEnd;
/* 1723 */       if (length == 0) {
/*      */         break;
/*      */       }
/* 1726 */       int count = in.read(readBuffer, inputEnd, length);
/* 1727 */       if (count < 0) {
/* 1728 */         return inputEnd;
/*      */       }
/* 1730 */       inputEnd += count;
/* 1731 */     } while (inputEnd < 3);
/* 1732 */     return inputEnd;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeNull() throws IOException {
/* 1743 */     if (this._outputTail + 4 >= this._outputEnd) {
/* 1744 */       _flushBuffer();
/*      */     }
/* 1746 */     int ptr = this._outputTail;
/* 1747 */     char[] buf = this._outputBuffer;
/* 1748 */     buf[ptr] = 'n';
/* 1749 */     buf[++ptr] = 'u';
/* 1750 */     buf[++ptr] = 'l';
/* 1751 */     buf[++ptr] = 'l';
/* 1752 */     this._outputTail = ptr + 1;
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
/*      */   private void _prependOrWriteCharacterEscape(char ch, int escCode) throws IOException, JsonGenerationException {
/*      */     String escape;
/* 1769 */     if (escCode >= 0) {
/* 1770 */       if (this._outputTail >= 2) {
/* 1771 */         int ptr = this._outputTail - 2;
/* 1772 */         this._outputHead = ptr;
/* 1773 */         this._outputBuffer[ptr++] = '\\';
/* 1774 */         this._outputBuffer[ptr] = (char)escCode;
/*      */         
/*      */         return;
/*      */       } 
/* 1778 */       char[] buf = this._entityBuffer;
/* 1779 */       if (buf == null) {
/* 1780 */         buf = _allocateEntityBuffer();
/*      */       }
/* 1782 */       this._outputHead = this._outputTail;
/* 1783 */       buf[1] = (char)escCode;
/* 1784 */       this._writer.write(buf, 0, 2);
/*      */       return;
/*      */     } 
/* 1787 */     if (escCode != -2) {
/* 1788 */       if (this._outputTail >= 6) {
/* 1789 */         char[] arrayOfChar = this._outputBuffer;
/* 1790 */         int ptr = this._outputTail - 6;
/* 1791 */         this._outputHead = ptr;
/* 1792 */         arrayOfChar[ptr] = '\\';
/* 1793 */         arrayOfChar[++ptr] = 'u';
/*      */         
/* 1795 */         if (ch > 'ÿ') {
/* 1796 */           int hi = ch >> 8 & 0xFF;
/* 1797 */           arrayOfChar[++ptr] = HEX_CHARS[hi >> 4];
/* 1798 */           arrayOfChar[++ptr] = HEX_CHARS[hi & 0xF];
/* 1799 */           ch = (char)(ch & 0xFF);
/*      */         } else {
/* 1801 */           arrayOfChar[++ptr] = '0';
/* 1802 */           arrayOfChar[++ptr] = '0';
/*      */         } 
/* 1804 */         arrayOfChar[++ptr] = HEX_CHARS[ch >> 4];
/* 1805 */         arrayOfChar[++ptr] = HEX_CHARS[ch & 0xF];
/*      */         
/*      */         return;
/*      */       } 
/* 1809 */       char[] buf = this._entityBuffer;
/* 1810 */       if (buf == null) {
/* 1811 */         buf = _allocateEntityBuffer();
/*      */       }
/* 1813 */       this._outputHead = this._outputTail;
/* 1814 */       if (ch > 'ÿ') {
/* 1815 */         int hi = ch >> 8 & 0xFF;
/* 1816 */         int lo = ch & 0xFF;
/* 1817 */         buf[10] = HEX_CHARS[hi >> 4];
/* 1818 */         buf[11] = HEX_CHARS[hi & 0xF];
/* 1819 */         buf[12] = HEX_CHARS[lo >> 4];
/* 1820 */         buf[13] = HEX_CHARS[lo & 0xF];
/* 1821 */         this._writer.write(buf, 8, 6);
/*      */       } else {
/* 1823 */         buf[6] = HEX_CHARS[ch >> 4];
/* 1824 */         buf[7] = HEX_CHARS[ch & 0xF];
/* 1825 */         this._writer.write(buf, 2, 6);
/*      */       } 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1831 */     if (this._currentEscape == null) {
/* 1832 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1834 */       escape = this._currentEscape.getValue();
/* 1835 */       this._currentEscape = null;
/*      */     } 
/* 1837 */     int len = escape.length();
/* 1838 */     if (this._outputTail >= len) {
/* 1839 */       int ptr = this._outputTail - len;
/* 1840 */       this._outputHead = ptr;
/* 1841 */       escape.getChars(0, len, this._outputBuffer, ptr);
/*      */       
/*      */       return;
/*      */     } 
/* 1845 */     this._outputHead = this._outputTail;
/* 1846 */     this._writer.write(escape);
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
/*      */   private int _prependOrWriteCharacterEscape(char[] buffer, int ptr, int end, char ch, int escCode) throws IOException, JsonGenerationException {
/*      */     String escape;
/* 1860 */     if (escCode >= 0) {
/* 1861 */       if (ptr > 1 && ptr < end) {
/* 1862 */         ptr -= 2;
/* 1863 */         buffer[ptr] = '\\';
/* 1864 */         buffer[ptr + 1] = (char)escCode;
/*      */       } else {
/* 1866 */         char[] ent = this._entityBuffer;
/* 1867 */         if (ent == null) {
/* 1868 */           ent = _allocateEntityBuffer();
/*      */         }
/* 1870 */         ent[1] = (char)escCode;
/* 1871 */         this._writer.write(ent, 0, 2);
/*      */       } 
/* 1873 */       return ptr;
/*      */     } 
/* 1875 */     if (escCode != -2) {
/* 1876 */       if (ptr > 5 && ptr < end) {
/* 1877 */         ptr -= 6;
/* 1878 */         buffer[ptr++] = '\\';
/* 1879 */         buffer[ptr++] = 'u';
/*      */         
/* 1881 */         if (ch > 'ÿ') {
/* 1882 */           int hi = ch >> 8 & 0xFF;
/* 1883 */           buffer[ptr++] = HEX_CHARS[hi >> 4];
/* 1884 */           buffer[ptr++] = HEX_CHARS[hi & 0xF];
/* 1885 */           ch = (char)(ch & 0xFF);
/*      */         } else {
/* 1887 */           buffer[ptr++] = '0';
/* 1888 */           buffer[ptr++] = '0';
/*      */         } 
/* 1890 */         buffer[ptr++] = HEX_CHARS[ch >> 4];
/* 1891 */         buffer[ptr] = HEX_CHARS[ch & 0xF];
/* 1892 */         ptr -= 5;
/*      */       } else {
/*      */         
/* 1895 */         char[] ent = this._entityBuffer;
/* 1896 */         if (ent == null) {
/* 1897 */           ent = _allocateEntityBuffer();
/*      */         }
/* 1899 */         this._outputHead = this._outputTail;
/* 1900 */         if (ch > 'ÿ') {
/* 1901 */           int hi = ch >> 8 & 0xFF;
/* 1902 */           int lo = ch & 0xFF;
/* 1903 */           ent[10] = HEX_CHARS[hi >> 4];
/* 1904 */           ent[11] = HEX_CHARS[hi & 0xF];
/* 1905 */           ent[12] = HEX_CHARS[lo >> 4];
/* 1906 */           ent[13] = HEX_CHARS[lo & 0xF];
/* 1907 */           this._writer.write(ent, 8, 6);
/*      */         } else {
/* 1909 */           ent[6] = HEX_CHARS[ch >> 4];
/* 1910 */           ent[7] = HEX_CHARS[ch & 0xF];
/* 1911 */           this._writer.write(ent, 2, 6);
/*      */         } 
/*      */       } 
/* 1914 */       return ptr;
/*      */     } 
/*      */     
/* 1917 */     if (this._currentEscape == null) {
/* 1918 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1920 */       escape = this._currentEscape.getValue();
/* 1921 */       this._currentEscape = null;
/*      */     } 
/* 1923 */     int len = escape.length();
/* 1924 */     if (ptr >= len && ptr < end) {
/* 1925 */       ptr -= len;
/* 1926 */       escape.getChars(0, len, buffer, ptr);
/*      */     } else {
/* 1928 */       this._writer.write(escape);
/*      */     } 
/* 1930 */     return ptr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _appendCharacterEscape(char ch, int escCode) throws IOException, JsonGenerationException {
/*      */     String escape;
/* 1940 */     if (escCode >= 0) {
/* 1941 */       if (this._outputTail + 2 > this._outputEnd) {
/* 1942 */         _flushBuffer();
/*      */       }
/* 1944 */       this._outputBuffer[this._outputTail++] = '\\';
/* 1945 */       this._outputBuffer[this._outputTail++] = (char)escCode;
/*      */       return;
/*      */     } 
/* 1948 */     if (escCode != -2) {
/* 1949 */       if (this._outputTail + 5 >= this._outputEnd) {
/* 1950 */         _flushBuffer();
/*      */       }
/* 1952 */       int ptr = this._outputTail;
/* 1953 */       char[] buf = this._outputBuffer;
/* 1954 */       buf[ptr++] = '\\';
/* 1955 */       buf[ptr++] = 'u';
/*      */       
/* 1957 */       if (ch > 'ÿ') {
/* 1958 */         int hi = ch >> 8 & 0xFF;
/* 1959 */         buf[ptr++] = HEX_CHARS[hi >> 4];
/* 1960 */         buf[ptr++] = HEX_CHARS[hi & 0xF];
/* 1961 */         ch = (char)(ch & 0xFF);
/*      */       } else {
/* 1963 */         buf[ptr++] = '0';
/* 1964 */         buf[ptr++] = '0';
/*      */       } 
/* 1966 */       buf[ptr++] = HEX_CHARS[ch >> 4];
/* 1967 */       buf[ptr++] = HEX_CHARS[ch & 0xF];
/* 1968 */       this._outputTail = ptr;
/*      */       
/*      */       return;
/*      */     } 
/* 1972 */     if (this._currentEscape == null) {
/* 1973 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1975 */       escape = this._currentEscape.getValue();
/* 1976 */       this._currentEscape = null;
/*      */     } 
/* 1978 */     int len = escape.length();
/* 1979 */     if (this._outputTail + len > this._outputEnd) {
/* 1980 */       _flushBuffer();
/* 1981 */       if (len > this._outputEnd) {
/* 1982 */         this._writer.write(escape);
/*      */         return;
/*      */       } 
/*      */     } 
/* 1986 */     escape.getChars(0, len, this._outputBuffer, this._outputTail);
/* 1987 */     this._outputTail += len;
/*      */   }
/*      */ 
/*      */   
/*      */   private char[] _allocateEntityBuffer() {
/* 1992 */     char[] buf = new char[14];
/*      */     
/* 1994 */     buf[0] = '\\';
/*      */     
/* 1996 */     buf[2] = '\\';
/* 1997 */     buf[3] = 'u';
/* 1998 */     buf[4] = '0';
/* 1999 */     buf[5] = '0';
/*      */     
/* 2001 */     buf[8] = '\\';
/* 2002 */     buf[9] = 'u';
/* 2003 */     this._entityBuffer = buf;
/* 2004 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private char[] _allocateCopyBuffer() {
/* 2011 */     if (this._copyBuffer == null) {
/* 2012 */       this._copyBuffer = this._ioContext.allocNameCopyBuffer(2000);
/*      */     }
/* 2014 */     return this._copyBuffer;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _flushBuffer() throws IOException {
/* 2019 */     int len = this._outputTail - this._outputHead;
/* 2020 */     if (len > 0) {
/* 2021 */       int offset = this._outputHead;
/* 2022 */       this._outputTail = this._outputHead = 0;
/* 2023 */       this._writer.write(this._outputBuffer, offset, len);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/WriterBasedJsonGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */