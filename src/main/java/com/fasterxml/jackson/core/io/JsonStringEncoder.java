/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*     */ import com.fasterxml.jackson.core.util.TextBuffer;
/*     */ import java.util.Arrays;
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
/*     */ public final class JsonStringEncoder
/*     */ {
/*  24 */   private static final char[] HC = CharTypes.copyHexChars();
/*     */   
/*  26 */   private static final byte[] HB = CharTypes.copyHexBytes();
/*     */ 
/*     */   
/*     */   private static final int SURR1_FIRST = 55296;
/*     */ 
/*     */   
/*     */   private static final int SURR1_LAST = 56319;
/*     */ 
/*     */   
/*     */   private static final int SURR2_FIRST = 56320;
/*     */ 
/*     */   
/*     */   private static final int SURR2_LAST = 57343;
/*     */ 
/*     */   
/*     */   static final int MIN_CHAR_BUFFER_SIZE = 16;
/*     */   
/*     */   static final int MAX_CHAR_BUFFER_SIZE = 32000;
/*     */   
/*     */   static final int MIN_BYTE_BUFFER_SIZE = 24;
/*     */   
/*     */   static final int MAX_BYTE_BUFFER_SIZE = 32000;
/*     */   
/*  49 */   private static final JsonStringEncoder instance = new JsonStringEncoder();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonStringEncoder getInstance() {
/*  60 */     return instance;
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
/*     */   public char[] quoteAsString(String input) {
/*  79 */     int inputLen = input.length();
/*  80 */     char[] outputBuffer = new char[_initialCharBufSize(inputLen)];
/*  81 */     int[] escCodes = CharTypes.get7BitOutputEscapes();
/*  82 */     int escCodeCount = escCodes.length;
/*  83 */     int inPtr = 0;
/*  84 */     TextBuffer textBuffer = null;
/*  85 */     int outPtr = 0;
/*  86 */     char[] qbuf = null;
/*     */ 
/*     */     
/*  89 */     while (inPtr < inputLen) {
/*     */       
/*     */       label35: while (true) {
/*  92 */         char d, c = input.charAt(inPtr);
/*  93 */         if (c < escCodeCount && escCodes[c] != 0)
/*     */         
/*     */         { 
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
/* 109 */           if (qbuf == null) {
/* 110 */             qbuf = _qbuf();
/*     */           }
/* 112 */           d = input.charAt(inPtr++);
/* 113 */           int escCode = escCodes[d];
/*     */ 
/*     */           
/* 116 */           int length = (escCode < 0) ? _appendNumeric(d, qbuf) : _appendNamed(escCode, qbuf);
/*     */           
/* 118 */           if (outPtr + length > outputBuffer.length) {
/* 119 */             int first = outputBuffer.length - outPtr;
/* 120 */             if (first > 0) {
/* 121 */               System.arraycopy(qbuf, 0, outputBuffer, outPtr, first);
/*     */             }
/* 123 */             if (textBuffer == null) {
/* 124 */               textBuffer = TextBuffer.fromInitial(outputBuffer); break label35;
/*     */             } 
/* 126 */             outputBuffer = textBuffer.finishCurrentSegment();
/* 127 */             int second = length - first;
/* 128 */             System.arraycopy(qbuf, first, outputBuffer, 0, second);
/* 129 */             outPtr = second; continue;
/*     */           } 
/* 131 */           System.arraycopy(qbuf, 0, outputBuffer, outPtr, length);
/* 132 */           outPtr += length; continue; }  if (outPtr >= outputBuffer.length) { if (textBuffer == null)
/*     */             textBuffer = TextBuffer.fromInitial(outputBuffer);  outputBuffer = textBuffer.finishCurrentSegment(); outPtr = 0; }  outputBuffer[outPtr++] = d; if (++inPtr >= inputLen)
/*     */           break; 
/*     */       } 
/* 136 */     }  if (textBuffer == null) {
/* 137 */       return Arrays.copyOfRange(outputBuffer, 0, outPtr);
/*     */     }
/* 139 */     textBuffer.setCurrentLength(outPtr);
/* 140 */     return textBuffer.contentsAsArray();
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
/*     */   public char[] quoteAsString(CharSequence input) {
/* 155 */     if (input instanceof String) {
/* 156 */       return quoteAsString((String)input);
/*     */     }
/*     */     
/* 159 */     TextBuffer textBuffer = null;
/*     */     
/* 161 */     int inputLen = input.length();
/* 162 */     char[] outputBuffer = new char[_initialCharBufSize(inputLen)];
/* 163 */     int[] escCodes = CharTypes.get7BitOutputEscapes();
/* 164 */     int escCodeCount = escCodes.length;
/* 165 */     int inPtr = 0;
/* 166 */     int outPtr = 0;
/* 167 */     char[] qbuf = null;
/*     */ 
/*     */     
/* 170 */     while (inPtr < inputLen) {
/*     */       
/*     */       label37: while (true) {
/* 173 */         char d, c = input.charAt(inPtr);
/* 174 */         if (c < escCodeCount && escCodes[c] != 0)
/*     */         
/*     */         { 
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
/* 190 */           if (qbuf == null) {
/* 191 */             qbuf = _qbuf();
/*     */           }
/* 193 */           d = input.charAt(inPtr++);
/* 194 */           int escCode = escCodes[d];
/*     */ 
/*     */           
/* 197 */           int length = (escCode < 0) ? _appendNumeric(d, qbuf) : _appendNamed(escCode, qbuf);
/*     */           
/* 199 */           if (outPtr + length > outputBuffer.length) {
/* 200 */             int first = outputBuffer.length - outPtr;
/* 201 */             if (first > 0) {
/* 202 */               System.arraycopy(qbuf, 0, outputBuffer, outPtr, first);
/*     */             }
/* 204 */             if (textBuffer == null) {
/* 205 */               textBuffer = TextBuffer.fromInitial(outputBuffer); break label37;
/*     */             } 
/* 207 */             outputBuffer = textBuffer.finishCurrentSegment();
/* 208 */             int second = length - first;
/* 209 */             System.arraycopy(qbuf, first, outputBuffer, 0, second);
/* 210 */             outPtr = second; continue;
/*     */           } 
/* 212 */           System.arraycopy(qbuf, 0, outputBuffer, outPtr, length);
/* 213 */           outPtr += length; continue; }  if (outPtr >= outputBuffer.length) { if (textBuffer == null)
/*     */             textBuffer = TextBuffer.fromInitial(outputBuffer);  outputBuffer = textBuffer.finishCurrentSegment(); outPtr = 0; }  outputBuffer[outPtr++] = d; if (++inPtr >= inputLen)
/*     */           break; 
/*     */       } 
/* 217 */     }  if (textBuffer == null) {
/* 218 */       return Arrays.copyOfRange(outputBuffer, 0, outPtr);
/*     */     }
/* 220 */     textBuffer.setCurrentLength(outPtr);
/* 221 */     return textBuffer.contentsAsArray();
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
/*     */   public void quoteAsString(CharSequence input, StringBuilder output) {
/* 236 */     int[] escCodes = CharTypes.get7BitOutputEscapes();
/* 237 */     int escCodeCount = escCodes.length;
/* 238 */     int inPtr = 0;
/* 239 */     int inputLen = input.length();
/* 240 */     char[] qbuf = null;
/*     */ 
/*     */     
/* 243 */     while (inPtr < inputLen) {
/*     */       
/*     */       while (true) {
/* 246 */         char d, c = input.charAt(inPtr);
/* 247 */         if (c < escCodeCount && escCodes[c] != 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 256 */           if (qbuf == null) {
/* 257 */             qbuf = _qbuf();
/*     */           }
/* 259 */           d = input.charAt(inPtr++);
/* 260 */           int escCode = escCodes[d];
/* 261 */           if (escCode < 0);
/*     */           
/* 263 */           int length = _appendNamed(escCode, qbuf);
/* 264 */           output.append(qbuf, 0, length);
/*     */           continue;
/*     */         } 
/*     */         output.append(d);
/*     */         if (++inPtr >= inputLen) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] quoteAsUTF8(String text) {
/* 280 */     int inputPtr = 0;
/* 281 */     int inputEnd = text.length();
/* 282 */     int outputPtr = 0;
/* 283 */     byte[] outputBuffer = new byte[_initialByteBufSize(inputEnd)];
/* 284 */     ByteArrayBuilder bb = null;
/*     */ 
/*     */     
/* 287 */     while (inputPtr < inputEnd)
/* 288 */     { int[] escCodes = CharTypes.get7BitOutputEscapes();
/*     */ 
/*     */       
/*     */       label55: while (true)
/* 292 */       { int ch = text.charAt(inputPtr);
/* 293 */         if (ch > 127 || escCodes[ch] != 0)
/*     */         
/*     */         { 
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
/* 308 */           if (bb == null) {
/* 309 */             bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
/*     */           }
/* 311 */           if (outputPtr >= outputBuffer.length) {
/* 312 */             outputBuffer = bb.finishCurrentSegment();
/* 313 */             outputPtr = 0;
/*     */           } 
/*     */           
/* 316 */           ch = text.charAt(inputPtr++);
/* 317 */           if (ch <= 127) {
/* 318 */             int escape = escCodes[ch];
/*     */             
/* 320 */             outputPtr = _appendByte(ch, escape, bb, outputPtr);
/* 321 */             outputBuffer = bb.getCurrentSegment();
/*     */             continue;
/*     */           } 
/* 324 */           if (ch <= 2047) {
/* 325 */             outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/* 326 */             ch = 0x80 | ch & 0x3F;
/*     */           
/*     */           }
/* 329 */           else if (ch < 55296 || ch > 57343) {
/* 330 */             outputBuffer[outputPtr++] = (byte)(0xE0 | ch >> 12);
/* 331 */             if (outputPtr >= outputBuffer.length) {
/* 332 */               outputBuffer = bb.finishCurrentSegment();
/* 333 */               outputPtr = 0;
/*     */             } 
/* 335 */             outputBuffer[outputPtr++] = (byte)(0x80 | ch >> 6 & 0x3F);
/* 336 */             ch = 0x80 | ch & 0x3F;
/*     */           } else {
/* 338 */             if (ch > 56319) {
/* 339 */               _illegal(ch);
/*     */             }
/*     */             
/* 342 */             if (inputPtr >= inputEnd) {
/* 343 */               _illegal(ch);
/*     */             }
/* 345 */             ch = _convert(ch, text.charAt(inputPtr++));
/* 346 */             if (ch > 1114111) {
/* 347 */               _illegal(ch);
/*     */             }
/* 349 */             outputBuffer[outputPtr++] = (byte)(0xF0 | ch >> 18);
/* 350 */             if (outputPtr >= outputBuffer.length) {
/* 351 */               outputBuffer = bb.finishCurrentSegment();
/* 352 */               outputPtr = 0;
/*     */             } 
/* 354 */             outputBuffer[outputPtr++] = (byte)(0x80 | ch >> 12 & 0x3F);
/* 355 */             if (outputPtr >= outputBuffer.length) {
/* 356 */               outputBuffer = bb.finishCurrentSegment();
/* 357 */               outputPtr = 0;
/*     */             } 
/* 359 */             outputBuffer[outputPtr++] = (byte)(0x80 | ch >> 6 & 0x3F);
/* 360 */             ch = 0x80 | ch & 0x3F;
/*     */           } 
/*     */           
/* 363 */           if (outputPtr >= outputBuffer.length) {
/* 364 */             outputBuffer = bb.finishCurrentSegment();
/* 365 */             outputPtr = 0; break label55;
/*     */           } 
/* 367 */           outputBuffer[outputPtr++] = (byte)ch; continue; }  if (outputPtr >= outputBuffer.length) { if (bb == null)
/*     */             bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);  outputBuffer = bb.finishCurrentSegment(); outputPtr = 0; }  outputBuffer[outputPtr++] = (byte)ch; if (++inputPtr >= inputEnd)
/* 369 */           break;  }  }  if (bb == null) {
/* 370 */       return Arrays.copyOfRange(outputBuffer, 0, outputPtr);
/*     */     }
/* 372 */     return bb.completeAndCoalesce(outputPtr);
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
/*     */   public byte[] encodeAsUTF8(String text) {
/* 386 */     int inputPtr = 0;
/* 387 */     int inputEnd = text.length();
/* 388 */     int outputPtr = 0;
/* 389 */     byte[] outputBuffer = new byte[_initialByteBufSize(inputEnd)];
/* 390 */     int outputEnd = outputBuffer.length;
/* 391 */     ByteArrayBuilder bb = null;
/*     */ 
/*     */     
/* 394 */     label51: while (inputPtr < inputEnd) {
/* 395 */       int c = text.charAt(inputPtr++);
/*     */ 
/*     */       
/* 398 */       while (c <= 127) {
/* 399 */         if (outputPtr >= outputEnd) {
/* 400 */           if (bb == null) {
/* 401 */             bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
/*     */           }
/* 403 */           outputBuffer = bb.finishCurrentSegment();
/* 404 */           outputEnd = outputBuffer.length;
/* 405 */           outputPtr = 0;
/*     */         } 
/* 407 */         outputBuffer[outputPtr++] = (byte)c;
/* 408 */         if (inputPtr >= inputEnd) {
/*     */           break label51;
/*     */         }
/* 411 */         c = text.charAt(inputPtr++);
/*     */       } 
/*     */ 
/*     */       
/* 415 */       if (bb == null) {
/* 416 */         bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
/*     */       }
/* 418 */       if (outputPtr >= outputEnd) {
/* 419 */         outputBuffer = bb.finishCurrentSegment();
/* 420 */         outputEnd = outputBuffer.length;
/* 421 */         outputPtr = 0;
/*     */       } 
/* 423 */       if (c < 2048) {
/* 424 */         outputBuffer[outputPtr++] = (byte)(0xC0 | c >> 6);
/*     */       
/*     */       }
/* 427 */       else if (c < 55296 || c > 57343) {
/* 428 */         outputBuffer[outputPtr++] = (byte)(0xE0 | c >> 12);
/* 429 */         if (outputPtr >= outputEnd) {
/* 430 */           outputBuffer = bb.finishCurrentSegment();
/* 431 */           outputEnd = outputBuffer.length;
/* 432 */           outputPtr = 0;
/*     */         } 
/* 434 */         outputBuffer[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/*     */       } else {
/* 436 */         if (c > 56319) {
/* 437 */           _illegal(c);
/*     */         }
/*     */         
/* 440 */         if (inputPtr >= inputEnd) {
/* 441 */           _illegal(c);
/*     */         }
/* 443 */         c = _convert(c, text.charAt(inputPtr++));
/* 444 */         if (c > 1114111) {
/* 445 */           _illegal(c);
/*     */         }
/* 447 */         outputBuffer[outputPtr++] = (byte)(0xF0 | c >> 18);
/* 448 */         if (outputPtr >= outputEnd) {
/* 449 */           outputBuffer = bb.finishCurrentSegment();
/* 450 */           outputEnd = outputBuffer.length;
/* 451 */           outputPtr = 0;
/*     */         } 
/* 453 */         outputBuffer[outputPtr++] = (byte)(0x80 | c >> 12 & 0x3F);
/* 454 */         if (outputPtr >= outputEnd) {
/* 455 */           outputBuffer = bb.finishCurrentSegment();
/* 456 */           outputEnd = outputBuffer.length;
/* 457 */           outputPtr = 0;
/*     */         } 
/* 459 */         outputBuffer[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/*     */       } 
/*     */       
/* 462 */       if (outputPtr >= outputEnd) {
/* 463 */         outputBuffer = bb.finishCurrentSegment();
/* 464 */         outputEnd = outputBuffer.length;
/* 465 */         outputPtr = 0;
/*     */       } 
/* 467 */       outputBuffer[outputPtr++] = (byte)(0x80 | c & 0x3F);
/*     */     } 
/* 469 */     if (bb == null) {
/* 470 */       return Arrays.copyOfRange(outputBuffer, 0, outputPtr);
/*     */     }
/* 472 */     return bb.completeAndCoalesce(outputPtr);
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
/*     */   public byte[] encodeAsUTF8(CharSequence text) {
/* 487 */     int inputPtr = 0;
/* 488 */     int inputEnd = text.length();
/* 489 */     int outputPtr = 0;
/* 490 */     byte[] outputBuffer = new byte[_initialByteBufSize(inputEnd)];
/* 491 */     int outputEnd = outputBuffer.length;
/* 492 */     ByteArrayBuilder bb = null;
/*     */ 
/*     */     
/* 495 */     label51: while (inputPtr < inputEnd) {
/* 496 */       int c = text.charAt(inputPtr++);
/*     */ 
/*     */       
/* 499 */       while (c <= 127) {
/* 500 */         if (outputPtr >= outputEnd) {
/* 501 */           if (bb == null) {
/* 502 */             bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
/*     */           }
/* 504 */           outputBuffer = bb.finishCurrentSegment();
/* 505 */           outputEnd = outputBuffer.length;
/* 506 */           outputPtr = 0;
/*     */         } 
/* 508 */         outputBuffer[outputPtr++] = (byte)c;
/* 509 */         if (inputPtr >= inputEnd) {
/*     */           break label51;
/*     */         }
/* 512 */         c = text.charAt(inputPtr++);
/*     */       } 
/*     */ 
/*     */       
/* 516 */       if (bb == null) {
/* 517 */         bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
/*     */       }
/* 519 */       if (outputPtr >= outputEnd) {
/* 520 */         outputBuffer = bb.finishCurrentSegment();
/* 521 */         outputEnd = outputBuffer.length;
/* 522 */         outputPtr = 0;
/*     */       } 
/* 524 */       if (c < 2048) {
/* 525 */         outputBuffer[outputPtr++] = (byte)(0xC0 | c >> 6);
/*     */       
/*     */       }
/* 528 */       else if (c < 55296 || c > 57343) {
/* 529 */         outputBuffer[outputPtr++] = (byte)(0xE0 | c >> 12);
/* 530 */         if (outputPtr >= outputEnd) {
/* 531 */           outputBuffer = bb.finishCurrentSegment();
/* 532 */           outputEnd = outputBuffer.length;
/* 533 */           outputPtr = 0;
/*     */         } 
/* 535 */         outputBuffer[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/*     */       } else {
/* 537 */         if (c > 56319) {
/* 538 */           _illegal(c);
/*     */         }
/*     */         
/* 541 */         if (inputPtr >= inputEnd) {
/* 542 */           _illegal(c);
/*     */         }
/* 544 */         c = _convert(c, text.charAt(inputPtr++));
/* 545 */         if (c > 1114111) {
/* 546 */           _illegal(c);
/*     */         }
/* 548 */         outputBuffer[outputPtr++] = (byte)(0xF0 | c >> 18);
/* 549 */         if (outputPtr >= outputEnd) {
/* 550 */           outputBuffer = bb.finishCurrentSegment();
/* 551 */           outputEnd = outputBuffer.length;
/* 552 */           outputPtr = 0;
/*     */         } 
/* 554 */         outputBuffer[outputPtr++] = (byte)(0x80 | c >> 12 & 0x3F);
/* 555 */         if (outputPtr >= outputEnd) {
/* 556 */           outputBuffer = bb.finishCurrentSegment();
/* 557 */           outputEnd = outputBuffer.length;
/* 558 */           outputPtr = 0;
/*     */         } 
/* 560 */         outputBuffer[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
/*     */       } 
/*     */       
/* 563 */       if (outputPtr >= outputEnd) {
/* 564 */         outputBuffer = bb.finishCurrentSegment();
/* 565 */         outputEnd = outputBuffer.length;
/* 566 */         outputPtr = 0;
/*     */       } 
/* 568 */       outputBuffer[outputPtr++] = (byte)(0x80 | c & 0x3F);
/*     */     } 
/* 570 */     if (bb == null) {
/* 571 */       return Arrays.copyOfRange(outputBuffer, 0, outputPtr);
/*     */     }
/* 573 */     return bb.completeAndCoalesce(outputPtr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] _qbuf() {
/* 583 */     char[] qbuf = new char[6];
/* 584 */     qbuf[0] = '\\';
/* 585 */     qbuf[2] = '0';
/* 586 */     qbuf[3] = '0';
/* 587 */     return qbuf;
/*     */   }
/*     */   
/*     */   private int _appendNumeric(int value, char[] qbuf) {
/* 591 */     qbuf[1] = 'u';
/*     */     
/* 593 */     qbuf[4] = HC[value >> 4];
/* 594 */     qbuf[5] = HC[value & 0xF];
/* 595 */     return 6;
/*     */   }
/*     */   
/*     */   private int _appendNamed(int esc, char[] qbuf) {
/* 599 */     qbuf[1] = (char)esc;
/* 600 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   private int _appendByte(int ch, int esc, ByteArrayBuilder bb, int ptr) {
/* 605 */     bb.setCurrentSegmentLength(ptr);
/* 606 */     bb.append(92);
/* 607 */     if (esc < 0) {
/* 608 */       bb.append(117);
/* 609 */       if (ch > 255) {
/* 610 */         int hi = ch >> 8;
/* 611 */         bb.append(HB[hi >> 4]);
/* 612 */         bb.append(HB[hi & 0xF]);
/* 613 */         ch &= 0xFF;
/*     */       } else {
/* 615 */         bb.append(48);
/* 616 */         bb.append(48);
/*     */       } 
/* 618 */       bb.append(HB[ch >> 4]);
/* 619 */       bb.append(HB[ch & 0xF]);
/*     */     } else {
/* 621 */       bb.append((byte)esc);
/*     */     } 
/* 623 */     return bb.getCurrentSegmentLength();
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _convert(int p1, int p2) {
/* 628 */     if (p2 < 56320 || p2 > 57343) {
/* 629 */       throw new IllegalArgumentException("Broken surrogate pair: first char 0x" + Integer.toHexString(p1) + ", second 0x" + Integer.toHexString(p2) + "; illegal combination");
/*     */     }
/* 631 */     return 65536 + (p1 - 55296 << 10) + p2 - 56320;
/*     */   }
/*     */   
/*     */   private static void _illegal(int c) {
/* 635 */     throw new IllegalArgumentException(UTF8Writer.illegalSurrogateDesc(c));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int _initialCharBufSize(int strLen) {
/* 642 */     int estimated = Math.max(16, strLen + 
/* 643 */         Math.min(6 + (strLen >> 3), 1000));
/* 644 */     return Math.min(estimated, 32000);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int _initialByteBufSize(int strLen) {
/* 651 */     int doubled = Math.max(24, strLen + 6 + (strLen >> 1));
/*     */     
/* 653 */     return Math.min(doubled, 32000);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/JsonStringEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */