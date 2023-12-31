/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonEncoding;
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.format.InputAccessor;
/*     */ import com.fasterxml.jackson.core.format.MatchStrength;
/*     */ import com.fasterxml.jackson.core.io.IOContext;
/*     */ import com.fasterxml.jackson.core.io.MergedStream;
/*     */ import com.fasterxml.jackson.core.io.UTF32Reader;
/*     */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*     */ import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.CharConversionException;
/*     */ import java.io.DataInput;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
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
/*     */ public final class ByteSourceJsonBootstrapper
/*     */ {
/*     */   public static final byte UTF8_BOM_1 = -17;
/*     */   public static final byte UTF8_BOM_2 = -69;
/*     */   public static final byte UTF8_BOM_3 = -65;
/*     */   private final IOContext _context;
/*     */   private final InputStream _in;
/*     */   private final byte[] _inputBuffer;
/*     */   private int _inputPtr;
/*     */   private int _inputEnd;
/*     */   private final boolean _bufferRecyclable;
/*     */   private boolean _bigEndian = true;
/*     */   private int _bytesPerChar;
/*     */   
/*     */   public ByteSourceJsonBootstrapper(IOContext ctxt, InputStream in) {
/*  88 */     this._context = ctxt;
/*  89 */     this._in = in;
/*  90 */     this._inputBuffer = ctxt.allocReadIOBuffer();
/*  91 */     this._inputEnd = this._inputPtr = 0;
/*     */     
/*  93 */     this._bufferRecyclable = true;
/*     */   }
/*     */   
/*     */   public ByteSourceJsonBootstrapper(IOContext ctxt, byte[] inputBuffer, int inputStart, int inputLen) {
/*  97 */     this._context = ctxt;
/*  98 */     this._in = null;
/*  99 */     this._inputBuffer = inputBuffer;
/* 100 */     this._inputPtr = inputStart;
/* 101 */     this._inputEnd = inputStart + inputLen;
/*     */ 
/*     */     
/* 104 */     this._bufferRecyclable = false;
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
/*     */   public JsonEncoding detectEncoding() throws IOException {
/*     */     JsonEncoding enc;
/* 124 */     boolean foundEncoding = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     if (ensureLoaded(4)) {
/* 134 */       int quad = this._inputBuffer[this._inputPtr] << 24 | (this._inputBuffer[this._inputPtr + 1] & 0xFF) << 16 | (this._inputBuffer[this._inputPtr + 2] & 0xFF) << 8 | this._inputBuffer[this._inputPtr + 3] & 0xFF;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 139 */       if (handleBOM(quad)) {
/* 140 */         foundEncoding = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 148 */       else if (checkUTF32(quad)) {
/* 149 */         foundEncoding = true;
/* 150 */       } else if (checkUTF16(quad >>> 16)) {
/* 151 */         foundEncoding = true;
/*     */       }
/*     */     
/* 154 */     } else if (ensureLoaded(2)) {
/* 155 */       int i16 = (this._inputBuffer[this._inputPtr] & 0xFF) << 8 | this._inputBuffer[this._inputPtr + 1] & 0xFF;
/*     */       
/* 157 */       if (checkUTF16(i16)) {
/* 158 */         foundEncoding = true;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 165 */     if (!foundEncoding)
/* 166 */     { enc = JsonEncoding.UTF8; }
/*     */     else
/* 168 */     { switch (this._bytesPerChar) { case 1:
/* 169 */           enc = JsonEncoding.UTF8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 178 */           this._context.setEncoding(enc);
/* 179 */           return enc;case 2: enc = this._bigEndian ? JsonEncoding.UTF16_BE : JsonEncoding.UTF16_LE; this._context.setEncoding(enc); return enc;case 4: enc = this._bigEndian ? JsonEncoding.UTF32_BE : JsonEncoding.UTF32_LE; this._context.setEncoding(enc); return enc; }  throw new RuntimeException("Internal error"); }  this._context.setEncoding(enc); return enc;
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
/*     */   public static int skipUTF8BOM(DataInput input) throws IOException {
/* 196 */     int b = input.readUnsignedByte();
/* 197 */     if (b != 239) {
/* 198 */       return b;
/*     */     }
/*     */ 
/*     */     
/* 202 */     b = input.readUnsignedByte();
/* 203 */     if (b != 187) {
/* 204 */       throw new IOException("Unexpected byte 0x" + Integer.toHexString(b) + " following 0xEF; should get 0xBB as part of UTF-8 BOM");
/*     */     }
/*     */     
/* 207 */     b = input.readUnsignedByte();
/* 208 */     if (b != 191) {
/* 209 */       throw new IOException("Unexpected byte 0x" + Integer.toHexString(b) + " following 0xEF 0xBB; should get 0xBF as part of UTF-8 BOM");
/*     */     }
/*     */     
/* 212 */     return input.readUnsignedByte();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader constructReader() throws IOException {
/*     */     InputStream in;
/*     */     MergedStream mergedStream;
/* 224 */     JsonEncoding enc = this._context.getEncoding();
/* 225 */     switch (enc.bits()) {
/*     */ 
/*     */       
/*     */       case 8:
/*     */       case 16:
/* 230 */         in = this._in;
/*     */         
/* 232 */         if (in == null) {
/* 233 */           in = new ByteArrayInputStream(this._inputBuffer, this._inputPtr, this._inputEnd);
/*     */ 
/*     */ 
/*     */         
/*     */         }
/* 238 */         else if (this._inputPtr < this._inputEnd) {
/* 239 */           mergedStream = new MergedStream(this._context, in, this._inputBuffer, this._inputPtr, this._inputEnd);
/*     */         } 
/*     */         
/* 242 */         return new InputStreamReader((InputStream)mergedStream, enc.getJavaName());
/*     */       
/*     */       case 32:
/* 245 */         return (Reader)new UTF32Reader(this._context, this._in, this._inputBuffer, this._inputPtr, this._inputEnd, this._context
/* 246 */             .getEncoding().isBigEndian());
/*     */     } 
/* 248 */     throw new RuntimeException("Internal error");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser constructParser(int parserFeatures, ObjectCodec codec, ByteQuadsCanonicalizer rootByteSymbols, CharsToNameCanonicalizer rootCharSymbols, int factoryFeatures) throws IOException {
/* 255 */     int prevInputPtr = this._inputPtr;
/* 256 */     JsonEncoding enc = detectEncoding();
/* 257 */     int bytesProcessed = this._inputPtr - prevInputPtr;
/*     */     
/* 259 */     if (enc == JsonEncoding.UTF8)
/*     */     {
/*     */ 
/*     */       
/* 263 */       if (JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(factoryFeatures)) {
/* 264 */         ByteQuadsCanonicalizer can = rootByteSymbols.makeChild(factoryFeatures);
/* 265 */         return (JsonParser)new UTF8StreamJsonParser(this._context, parserFeatures, this._in, codec, can, this._inputBuffer, this._inputPtr, this._inputEnd, bytesProcessed, this._bufferRecyclable);
/*     */       } 
/*     */     }
/*     */     
/* 269 */     return (JsonParser)new ReaderBasedJsonParser(this._context, parserFeatures, constructReader(), codec, rootCharSymbols
/* 270 */         .makeChild(factoryFeatures));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
/* 296 */     if (!acc.hasMoreBytes()) {
/* 297 */       return MatchStrength.INCONCLUSIVE;
/*     */     }
/* 299 */     byte b = acc.nextByte();
/*     */     
/* 301 */     if (b == -17) {
/* 302 */       if (!acc.hasMoreBytes()) {
/* 303 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 305 */       if (acc.nextByte() != -69) {
/* 306 */         return MatchStrength.NO_MATCH;
/*     */       }
/* 308 */       if (!acc.hasMoreBytes()) {
/* 309 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 311 */       if (acc.nextByte() != -65) {
/* 312 */         return MatchStrength.NO_MATCH;
/*     */       }
/* 314 */       if (!acc.hasMoreBytes()) {
/* 315 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 317 */       b = acc.nextByte();
/*     */     } 
/*     */     
/* 320 */     int ch = skipSpace(acc, b);
/* 321 */     if (ch < 0) {
/* 322 */       return MatchStrength.INCONCLUSIVE;
/*     */     }
/*     */     
/* 325 */     if (ch == 123) {
/*     */       
/* 327 */       ch = skipSpace(acc);
/* 328 */       if (ch < 0) {
/* 329 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 331 */       if (ch == 34 || ch == 125) {
/* 332 */         return MatchStrength.SOLID_MATCH;
/*     */       }
/*     */       
/* 335 */       return MatchStrength.NO_MATCH;
/*     */     } 
/*     */ 
/*     */     
/* 339 */     if (ch == 91) {
/* 340 */       ch = skipSpace(acc);
/* 341 */       if (ch < 0) {
/* 342 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/*     */       
/* 345 */       if (ch == 93 || ch == 91) {
/* 346 */         return MatchStrength.SOLID_MATCH;
/*     */       }
/* 348 */       return MatchStrength.SOLID_MATCH;
/*     */     } 
/*     */     
/* 351 */     MatchStrength strength = MatchStrength.WEAK_MATCH;
/*     */ 
/*     */     
/* 354 */     if (ch == 34) {
/* 355 */       return strength;
/*     */     }
/* 357 */     if (ch <= 57 && ch >= 48) {
/* 358 */       return strength;
/*     */     }
/* 360 */     if (ch == 45) {
/* 361 */       ch = skipSpace(acc);
/* 362 */       if (ch < 0) {
/* 363 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 365 */       return (ch <= 57 && ch >= 48) ? strength : MatchStrength.NO_MATCH;
/*     */     } 
/*     */     
/* 368 */     if (ch == 110) {
/* 369 */       return tryMatch(acc, "ull", strength);
/*     */     }
/* 371 */     if (ch == 116) {
/* 372 */       return tryMatch(acc, "rue", strength);
/*     */     }
/* 374 */     if (ch == 102) {
/* 375 */       return tryMatch(acc, "alse", strength);
/*     */     }
/* 377 */     return MatchStrength.NO_MATCH;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static MatchStrength tryMatch(InputAccessor acc, String matchStr, MatchStrength fullMatchStrength) throws IOException {
/* 383 */     for (int i = 0, len = matchStr.length(); i < len; i++) {
/* 384 */       if (!acc.hasMoreBytes()) {
/* 385 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 387 */       if (acc.nextByte() != matchStr.charAt(i)) {
/* 388 */         return MatchStrength.NO_MATCH;
/*     */       }
/*     */     } 
/* 391 */     return fullMatchStrength;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int skipSpace(InputAccessor acc) throws IOException {
/* 396 */     if (!acc.hasMoreBytes()) {
/* 397 */       return -1;
/*     */     }
/* 399 */     return skipSpace(acc, acc.nextByte());
/*     */   }
/*     */ 
/*     */   
/*     */   private static int skipSpace(InputAccessor acc, byte b) throws IOException {
/*     */     while (true) {
/* 405 */       int ch = b & 0xFF;
/* 406 */       if (ch != 32 && ch != 13 && ch != 10 && ch != 9) {
/* 407 */         return ch;
/*     */       }
/* 409 */       if (!acc.hasMoreBytes()) {
/* 410 */         return -1;
/*     */       }
/* 412 */       b = acc.nextByte();
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
/*     */   private boolean handleBOM(int quad) throws IOException {
/* 431 */     switch (quad) {
/*     */       case 65279:
/* 433 */         this._bigEndian = true;
/* 434 */         this._inputPtr += 4;
/* 435 */         this._bytesPerChar = 4;
/* 436 */         return true;
/*     */       case -131072:
/* 438 */         this._inputPtr += 4;
/* 439 */         this._bytesPerChar = 4;
/* 440 */         this._bigEndian = false;
/* 441 */         return true;
/*     */       case 65534:
/* 443 */         reportWeirdUCS4("2143");
/*     */         break;
/*     */       case -16842752:
/* 446 */         reportWeirdUCS4("3412");
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 451 */     int msw = quad >>> 16;
/* 452 */     if (msw == 65279) {
/* 453 */       this._inputPtr += 2;
/* 454 */       this._bytesPerChar = 2;
/* 455 */       this._bigEndian = true;
/* 456 */       return true;
/*     */     } 
/* 458 */     if (msw == 65534) {
/* 459 */       this._inputPtr += 2;
/* 460 */       this._bytesPerChar = 2;
/* 461 */       this._bigEndian = false;
/* 462 */       return true;
/*     */     } 
/*     */     
/* 465 */     if (quad >>> 8 == 15711167) {
/* 466 */       this._inputPtr += 3;
/* 467 */       this._bytesPerChar = 1;
/* 468 */       this._bigEndian = true;
/* 469 */       return true;
/*     */     } 
/* 471 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean checkUTF32(int quad) throws IOException {
/* 479 */     if (quad >> 8 == 0) {
/* 480 */       this._bigEndian = true;
/* 481 */     } else if ((quad & 0xFFFFFF) == 0) {
/* 482 */       this._bigEndian = false;
/* 483 */     } else if ((quad & 0xFF00FFFF) == 0) {
/* 484 */       reportWeirdUCS4("3412");
/* 485 */     } else if ((quad & 0xFFFF00FF) == 0) {
/* 486 */       reportWeirdUCS4("2143");
/*     */     } else {
/*     */       
/* 489 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 493 */     this._bytesPerChar = 4;
/* 494 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean checkUTF16(int i16) {
/* 499 */     if ((i16 & 0xFF00) == 0) {
/* 500 */       this._bigEndian = true;
/* 501 */     } else if ((i16 & 0xFF) == 0) {
/* 502 */       this._bigEndian = false;
/*     */     } else {
/* 504 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 508 */     this._bytesPerChar = 2;
/* 509 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void reportWeirdUCS4(String type) throws IOException {
/* 519 */     throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
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
/*     */   protected boolean ensureLoaded(int minimum) throws IOException {
/* 532 */     int gotten = this._inputEnd - this._inputPtr;
/* 533 */     while (gotten < minimum) {
/*     */       int count;
/*     */       
/* 536 */       if (this._in == null) {
/* 537 */         count = -1;
/*     */       } else {
/* 539 */         count = this._in.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
/*     */       } 
/* 541 */       if (count < 1) {
/* 542 */         return false;
/*     */       }
/* 544 */       this._inputEnd += count;
/* 545 */       gotten += count;
/*     */     } 
/* 547 */     return true;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/ByteSourceJsonBootstrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */