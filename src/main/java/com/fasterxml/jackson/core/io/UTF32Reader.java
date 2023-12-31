/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import java.io.CharConversionException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
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
/*     */ public class UTF32Reader
/*     */   extends Reader
/*     */ {
/*     */   protected static final int LAST_VALID_UNICODE_CHAR = 1114111;
/*     */   protected static final char NC = '\000';
/*     */   protected final IOContext _context;
/*     */   protected InputStream _in;
/*     */   protected byte[] _buffer;
/*     */   protected int _ptr;
/*     */   protected int _length;
/*     */   protected final boolean _bigEndian;
/*  37 */   protected char _surrogate = Character.MIN_VALUE;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _charCount;
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _byteCount;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _managedBuffers;
/*     */ 
/*     */ 
/*     */   
/*     */   protected char[] _tmpBuf;
/*     */ 
/*     */ 
/*     */   
/*     */   public UTF32Reader(IOContext ctxt, InputStream in, byte[] buf, int ptr, int len, boolean isBigEndian) {
/*  58 */     this._context = ctxt;
/*  59 */     this._in = in;
/*  60 */     this._buffer = buf;
/*  61 */     this._ptr = ptr;
/*  62 */     this._length = len;
/*  63 */     this._bigEndian = isBigEndian;
/*  64 */     this._managedBuffers = (in != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  75 */     InputStream in = this._in;
/*     */     
/*  77 */     if (in != null) {
/*  78 */       this._in = null;
/*  79 */       freeBuffers();
/*  80 */       in.close();
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
/*     */   public int read() throws IOException {
/*  93 */     if (this._tmpBuf == null) {
/*  94 */       this._tmpBuf = new char[1];
/*     */     }
/*  96 */     if (read(this._tmpBuf, 0, 1) < 1) {
/*  97 */       return -1;
/*     */     }
/*  99 */     return this._tmpBuf[0];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(char[] cbuf, int start, int len) throws IOException {
/* 106 */     if (this._buffer == null) return -1; 
/* 107 */     if (len < 1) return len;
/*     */     
/* 109 */     if (start < 0 || start + len > cbuf.length) {
/* 110 */       reportBounds(cbuf, start, len);
/*     */     }
/*     */     
/* 113 */     int outPtr = start;
/* 114 */     int outEnd = len + start;
/*     */ 
/*     */     
/* 117 */     if (this._surrogate != '\000') {
/* 118 */       cbuf[outPtr++] = this._surrogate;
/* 119 */       this._surrogate = Character.MIN_VALUE;
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 124 */       int left = this._length - this._ptr;
/* 125 */       if (left < 4 && 
/* 126 */         !loadMore(left)) {
/*     */         
/* 128 */         if (left == 0) {
/* 129 */           return -1;
/*     */         }
/* 131 */         reportUnexpectedEOF(this._length - this._ptr, 4);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 137 */     int lastValidInputStart = this._length - 4;
/*     */ 
/*     */     
/* 140 */     while (outPtr < outEnd && this._ptr <= lastValidInputStart) {
/* 141 */       int hi, lo, ptr = this._ptr;
/*     */ 
/*     */       
/* 144 */       if (this._bigEndian) {
/* 145 */         hi = this._buffer[ptr] << 8 | this._buffer[ptr + 1] & 0xFF;
/* 146 */         lo = (this._buffer[ptr + 2] & 0xFF) << 8 | this._buffer[ptr + 3] & 0xFF;
/*     */       } else {
/* 148 */         lo = this._buffer[ptr] & 0xFF | (this._buffer[ptr + 1] & 0xFF) << 8;
/* 149 */         hi = this._buffer[ptr + 2] & 0xFF | this._buffer[ptr + 3] << 8;
/*     */       } 
/* 151 */       this._ptr += 4;
/*     */ 
/*     */ 
/*     */       
/* 155 */       if (hi != 0) {
/* 156 */         hi &= 0xFFFF;
/* 157 */         int ch = hi - 1 << 16 | lo;
/* 158 */         if (hi > 16) {
/* 159 */           reportInvalid(ch, outPtr - start, 
/* 160 */               String.format(" (above 0x%08x)", new Object[] { Integer.valueOf(1114111) }));
/*     */         }
/* 162 */         cbuf[outPtr++] = (char)(55296 + (ch >> 10));
/*     */         
/* 164 */         lo = 0xDC00 | ch & 0x3FF;
/*     */         
/* 166 */         if (outPtr >= outEnd) {
/* 167 */           this._surrogate = (char)ch;
/*     */           break;
/*     */         } 
/*     */       } 
/* 171 */       cbuf[outPtr++] = (char)lo;
/*     */     } 
/* 173 */     int actualLen = outPtr - start;
/* 174 */     this._charCount += actualLen;
/* 175 */     return actualLen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
/* 185 */     int bytePos = this._byteCount + gotBytes, charPos = this._charCount;
/*     */     
/* 187 */     throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
/*     */   }
/*     */   
/*     */   private void reportInvalid(int value, int offset, String msg) throws IOException {
/* 191 */     int bytePos = this._byteCount + this._ptr - 1, charPos = this._charCount + offset;
/*     */     
/* 193 */     throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
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
/*     */   private boolean loadMore(int available) throws IOException {
/* 208 */     if (this._in == null || this._buffer == null) {
/* 209 */       return false;
/*     */     }
/*     */     
/* 212 */     this._byteCount += this._length - available;
/*     */ 
/*     */     
/* 215 */     if (available > 0) {
/* 216 */       if (this._ptr > 0) {
/* 217 */         System.arraycopy(this._buffer, this._ptr, this._buffer, 0, available);
/* 218 */         this._ptr = 0;
/*     */       } 
/* 220 */       this._length = available;
/*     */     }
/*     */     else {
/*     */       
/* 224 */       this._ptr = 0;
/* 225 */       int count = this._in.read(this._buffer);
/* 226 */       if (count < 1) {
/* 227 */         this._length = 0;
/* 228 */         if (count < 0) {
/* 229 */           if (this._managedBuffers) {
/* 230 */             freeBuffers();
/*     */           }
/* 232 */           return false;
/*     */         } 
/*     */         
/* 235 */         reportStrangeStream();
/*     */       } 
/* 237 */       this._length = count;
/*     */     } 
/*     */ 
/*     */     
/* 241 */     while (this._length < 4) {
/* 242 */       int count = this._in.read(this._buffer, this._length, this._buffer.length - this._length);
/* 243 */       if (count < 1) {
/* 244 */         if (count < 0) {
/* 245 */           if (this._managedBuffers) {
/* 246 */             freeBuffers();
/*     */           }
/* 248 */           reportUnexpectedEOF(this._length, 4);
/*     */         } 
/*     */         
/* 251 */         reportStrangeStream();
/*     */       } 
/* 253 */       this._length += count;
/*     */     } 
/* 255 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void freeBuffers() {
/* 264 */     byte[] buf = this._buffer;
/* 265 */     if (buf != null) {
/* 266 */       this._buffer = null;
/* 267 */       if (this._context != null) {
/* 268 */         this._context.releaseReadIOBuffer(buf);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void reportBounds(char[] cbuf, int start, int len) throws IOException {
/* 274 */     throw new ArrayIndexOutOfBoundsException(String.format("read(buf,%d,%d), cbuf[%d]", new Object[] {
/*     */             
/* 276 */             Integer.valueOf(start), Integer.valueOf(len), Integer.valueOf(cbuf.length) }));
/*     */   }
/*     */   
/*     */   private void reportStrangeStream() throws IOException {
/* 280 */     throw new IOException("Strange I/O stream, returned 0 bytes on read");
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/UTF32Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */