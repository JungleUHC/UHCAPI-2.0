/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
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
/*     */ public final class UTF8Writer
/*     */   extends Writer
/*     */ {
/*     */   static final int SURR1_FIRST = 55296;
/*     */   static final int SURR1_LAST = 56319;
/*     */   static final int SURR2_FIRST = 56320;
/*     */   static final int SURR2_LAST = 57343;
/*     */   private final IOContext _context;
/*     */   private OutputStream _out;
/*     */   private byte[] _outBuffer;
/*     */   private final int _outBufferEnd;
/*     */   private int _outPtr;
/*     */   private int _surrogate;
/*     */   
/*     */   public UTF8Writer(IOContext ctxt, OutputStream out) {
/*  31 */     this._context = ctxt;
/*  32 */     this._out = out;
/*     */     
/*  34 */     this._outBuffer = ctxt.allocWriteEncodingBuffer();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  39 */     this._outBufferEnd = this._outBuffer.length - 4;
/*  40 */     this._outPtr = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Writer append(char c) throws IOException {
/*  47 */     write(c);
/*  48 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  55 */     if (this._out != null) {
/*  56 */       if (this._outPtr > 0) {
/*  57 */         this._out.write(this._outBuffer, 0, this._outPtr);
/*  58 */         this._outPtr = 0;
/*     */       } 
/*  60 */       OutputStream out = this._out;
/*  61 */       this._out = null;
/*     */       
/*  63 */       byte[] buf = this._outBuffer;
/*  64 */       if (buf != null) {
/*  65 */         this._outBuffer = null;
/*  66 */         this._context.releaseWriteEncodingBuffer(buf);
/*     */       } 
/*     */       
/*  69 */       out.close();
/*     */ 
/*     */ 
/*     */       
/*  73 */       int code = this._surrogate;
/*  74 */       this._surrogate = 0;
/*  75 */       if (code > 0) {
/*  76 */         illegalSurrogate(code);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/*  85 */     if (this._out != null) {
/*  86 */       if (this._outPtr > 0) {
/*  87 */         this._out.write(this._outBuffer, 0, this._outPtr);
/*  88 */         this._outPtr = 0;
/*     */       } 
/*  90 */       this._out.flush();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(char[] cbuf) throws IOException {
/*  98 */     write(cbuf, 0, cbuf.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(char[] cbuf, int off, int len) throws IOException {
/* 105 */     if (len < 2) {
/* 106 */       if (len == 1) {
/* 107 */         write(cbuf[off]);
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 113 */     if (this._surrogate > 0) {
/* 114 */       char second = cbuf[off++];
/* 115 */       len--;
/* 116 */       write(convertSurrogate(second));
/*     */     } 
/*     */ 
/*     */     
/* 120 */     int outPtr = this._outPtr;
/* 121 */     byte[] outBuf = this._outBuffer;
/* 122 */     int outBufLast = this._outBufferEnd;
/*     */ 
/*     */     
/* 125 */     len += off;
/*     */ 
/*     */     
/* 128 */     label45: while (off < len) {
/*     */ 
/*     */ 
/*     */       
/* 132 */       if (outPtr >= outBufLast) {
/* 133 */         this._out.write(outBuf, 0, outPtr);
/* 134 */         outPtr = 0;
/*     */       } 
/*     */       
/* 137 */       int c = cbuf[off++];
/*     */       
/* 139 */       if (c < 128) {
/* 140 */         outBuf[outPtr++] = (byte)c;
/*     */         
/* 142 */         int maxInCount = len - off;
/* 143 */         int maxOutCount = outBufLast - outPtr;
/*     */         
/* 145 */         if (maxInCount > maxOutCount) {
/* 146 */           maxInCount = maxOutCount;
/*     */         }
/* 148 */         maxInCount += off;
/*     */ 
/*     */         
/* 151 */         while (off < maxInCount) {
/*     */ 
/*     */           
/* 154 */           c = cbuf[off++];
/* 155 */           if (c >= 128) {
/*     */             continue label45;
/*     */           }
/* 158 */           outBuf[outPtr++] = (byte)c;
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/* 163 */       if (c < 2048) {
/* 164 */         outBuf[outPtr++] = (byte)(0xC0 | c >> 6);
/* 165 */         outBuf[outPtr++] = (byte)(0x80 | c & 0x3F);
/*     */         continue;
/*     */       } 
/* 168 */       if (c < 55296 || c > 57343) {
/* 169 */         outBuf[outPtr++] = (byte)(0xE0 | c >> 12);
/* 170 */         outBuf[outPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/* 171 */         outBuf[outPtr++] = (byte)(0x80 | c & 0x3F);
/*     */         
/*     */         continue;
/*     */       } 
/* 175 */       if (c > 56319) {
/* 176 */         this._outPtr = outPtr;
/* 177 */         illegalSurrogate(c);
/*     */       } 
/* 179 */       this._surrogate = c;
/*     */       
/* 181 */       if (off >= len) {
/*     */         break;
/*     */       }
/* 184 */       c = convertSurrogate(cbuf[off++]);
/* 185 */       if (c > 1114111) {
/* 186 */         this._outPtr = outPtr;
/* 187 */         illegalSurrogate(c);
/*     */       } 
/* 189 */       outBuf[outPtr++] = (byte)(0xF0 | c >> 18);
/* 190 */       outBuf[outPtr++] = (byte)(0x80 | c >> 12 & 0x3F);
/* 191 */       outBuf[outPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/* 192 */       outBuf[outPtr++] = (byte)(0x80 | c & 0x3F);
/*     */     } 
/*     */     
/* 195 */     this._outPtr = outPtr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int c) throws IOException {
/* 202 */     if (this._surrogate > 0) {
/* 203 */       c = convertSurrogate(c);
/*     */     }
/* 205 */     else if (c >= 55296 && c <= 57343) {
/*     */       
/* 207 */       if (c > 56319) {
/* 208 */         illegalSurrogate(c);
/*     */       }
/*     */       
/* 211 */       this._surrogate = c;
/*     */       
/*     */       return;
/*     */     } 
/* 215 */     if (this._outPtr >= this._outBufferEnd) {
/* 216 */       this._out.write(this._outBuffer, 0, this._outPtr);
/* 217 */       this._outPtr = 0;
/*     */     } 
/*     */     
/* 220 */     if (c < 128) {
/* 221 */       this._outBuffer[this._outPtr++] = (byte)c;
/*     */     } else {
/* 223 */       int ptr = this._outPtr;
/* 224 */       if (c < 2048) {
/* 225 */         this._outBuffer[ptr++] = (byte)(0xC0 | c >> 6);
/* 226 */         this._outBuffer[ptr++] = (byte)(0x80 | c & 0x3F);
/* 227 */       } else if (c <= 65535) {
/* 228 */         this._outBuffer[ptr++] = (byte)(0xE0 | c >> 12);
/* 229 */         this._outBuffer[ptr++] = (byte)(0x80 | c >> 6 & 0x3F);
/* 230 */         this._outBuffer[ptr++] = (byte)(0x80 | c & 0x3F);
/*     */       } else {
/* 232 */         if (c > 1114111) {
/* 233 */           illegalSurrogate(c);
/*     */         }
/* 235 */         this._outBuffer[ptr++] = (byte)(0xF0 | c >> 18);
/* 236 */         this._outBuffer[ptr++] = (byte)(0x80 | c >> 12 & 0x3F);
/* 237 */         this._outBuffer[ptr++] = (byte)(0x80 | c >> 6 & 0x3F);
/* 238 */         this._outBuffer[ptr++] = (byte)(0x80 | c & 0x3F);
/*     */       } 
/* 240 */       this._outPtr = ptr;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(String str) throws IOException {
/* 247 */     write(str, 0, str.length());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(String str, int off, int len) throws IOException {
/* 253 */     if (len < 2) {
/* 254 */       if (len == 1) {
/* 255 */         write(str.charAt(off));
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 261 */     if (this._surrogate > 0) {
/* 262 */       char second = str.charAt(off++);
/* 263 */       len--;
/* 264 */       write(convertSurrogate(second));
/*     */     } 
/*     */ 
/*     */     
/* 268 */     int outPtr = this._outPtr;
/* 269 */     byte[] outBuf = this._outBuffer;
/* 270 */     int outBufLast = this._outBufferEnd;
/*     */ 
/*     */     
/* 273 */     len += off;
/*     */ 
/*     */     
/* 276 */     label45: while (off < len) {
/*     */ 
/*     */ 
/*     */       
/* 280 */       if (outPtr >= outBufLast) {
/* 281 */         this._out.write(outBuf, 0, outPtr);
/* 282 */         outPtr = 0;
/*     */       } 
/*     */       
/* 285 */       int c = str.charAt(off++);
/*     */       
/* 287 */       if (c < 128) {
/* 288 */         outBuf[outPtr++] = (byte)c;
/*     */         
/* 290 */         int maxInCount = len - off;
/* 291 */         int maxOutCount = outBufLast - outPtr;
/*     */         
/* 293 */         if (maxInCount > maxOutCount) {
/* 294 */           maxInCount = maxOutCount;
/*     */         }
/* 296 */         maxInCount += off;
/*     */ 
/*     */         
/* 299 */         while (off < maxInCount) {
/*     */ 
/*     */           
/* 302 */           c = str.charAt(off++);
/* 303 */           if (c >= 128) {
/*     */             continue label45;
/*     */           }
/* 306 */           outBuf[outPtr++] = (byte)c;
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/* 311 */       if (c < 2048) {
/* 312 */         outBuf[outPtr++] = (byte)(0xC0 | c >> 6);
/* 313 */         outBuf[outPtr++] = (byte)(0x80 | c & 0x3F);
/*     */         continue;
/*     */       } 
/* 316 */       if (c < 55296 || c > 57343) {
/* 317 */         outBuf[outPtr++] = (byte)(0xE0 | c >> 12);
/* 318 */         outBuf[outPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/* 319 */         outBuf[outPtr++] = (byte)(0x80 | c & 0x3F);
/*     */         
/*     */         continue;
/*     */       } 
/* 323 */       if (c > 56319) {
/* 324 */         this._outPtr = outPtr;
/* 325 */         illegalSurrogate(c);
/*     */       } 
/* 327 */       this._surrogate = c;
/*     */       
/* 329 */       if (off >= len) {
/*     */         break;
/*     */       }
/* 332 */       c = convertSurrogate(str.charAt(off++));
/* 333 */       if (c > 1114111) {
/* 334 */         this._outPtr = outPtr;
/* 335 */         illegalSurrogate(c);
/*     */       } 
/* 337 */       outBuf[outPtr++] = (byte)(0xF0 | c >> 18);
/* 338 */       outBuf[outPtr++] = (byte)(0x80 | c >> 12 & 0x3F);
/* 339 */       outBuf[outPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/* 340 */       outBuf[outPtr++] = (byte)(0x80 | c & 0x3F);
/*     */     } 
/*     */     
/* 343 */     this._outPtr = outPtr;
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
/*     */   protected int convertSurrogate(int secondPart) throws IOException {
/* 364 */     int firstPart = this._surrogate;
/* 365 */     this._surrogate = 0;
/*     */ 
/*     */     
/* 368 */     if (secondPart < 56320 || secondPart > 57343) {
/* 369 */       throw new IOException("Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination");
/*     */     }
/* 371 */     return 65536 + (firstPart - 55296 << 10) + secondPart - 56320;
/*     */   }
/*     */   
/*     */   protected static void illegalSurrogate(int code) throws IOException {
/* 375 */     throw new IOException(illegalSurrogateDesc(code));
/*     */   }
/*     */ 
/*     */   
/*     */   protected static String illegalSurrogateDesc(int code) {
/* 380 */     if (code > 1114111) {
/* 381 */       return "Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 4627";
/*     */     }
/* 383 */     if (code >= 55296) {
/* 384 */       if (code <= 56319) {
/* 385 */         return "Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")";
/*     */       }
/* 387 */       return "Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")";
/*     */     } 
/*     */     
/* 390 */     return "Illegal character point (0x" + Integer.toHexString(code) + ") to output";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/UTF8Writer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */