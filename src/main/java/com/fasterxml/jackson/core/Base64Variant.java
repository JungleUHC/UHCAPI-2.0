/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*     */ import java.io.Serializable;
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
/*     */ public final class Base64Variant
/*     */   implements Serializable
/*     */ {
/*     */   private static final int INT_SPACE = 32;
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected static final char PADDING_CHAR_NONE = '\000';
/*     */   public static final int BASE64_VALUE_INVALID = -1;
/*     */   public static final int BASE64_VALUE_PADDING = -2;
/*     */   
/*     */   public enum PaddingReadBehaviour
/*     */   {
/*  32 */     PADDING_FORBIDDEN,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  38 */     PADDING_REQUIRED,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  45 */     PADDING_ALLOWED;
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
/*  82 */   private final transient int[] _asciiToBase64 = new int[128];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   private final transient char[] _base64ToAsciiC = new char[64];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   private final transient byte[] _base64ToAsciiB = new byte[64];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final String _name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final char _paddingChar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int _maxLineLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean _writePadding;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final PaddingReadBehaviour _paddingReadBehaviour;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64Variant(String name, String base64Alphabet, boolean writePadding, char paddingChar, int maxLineLength) {
/* 149 */     this._name = name;
/* 150 */     this._writePadding = writePadding;
/* 151 */     this._paddingChar = paddingChar;
/* 152 */     this._maxLineLength = maxLineLength;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 157 */     int alphaLen = base64Alphabet.length();
/* 158 */     if (alphaLen != 64) {
/* 159 */       throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + alphaLen + ")");
/*     */     }
/*     */ 
/*     */     
/* 163 */     base64Alphabet.getChars(0, alphaLen, this._base64ToAsciiC, 0);
/* 164 */     Arrays.fill(this._asciiToBase64, -1);
/* 165 */     for (int i = 0; i < alphaLen; i++) {
/* 166 */       char alpha = this._base64ToAsciiC[i];
/* 167 */       this._base64ToAsciiB[i] = (byte)alpha;
/* 168 */       this._asciiToBase64[alpha] = i;
/*     */     } 
/*     */ 
/*     */     
/* 172 */     if (writePadding) {
/* 173 */       this._asciiToBase64[paddingChar] = -2;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 178 */     this._paddingReadBehaviour = writePadding ? PaddingReadBehaviour.PADDING_REQUIRED : PaddingReadBehaviour.PADDING_FORBIDDEN;
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
/*     */   public Base64Variant(Base64Variant base, String name, int maxLineLength) {
/* 196 */     this(base, name, base._writePadding, base._paddingChar, maxLineLength);
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
/*     */   public Base64Variant(Base64Variant base, String name, boolean writePadding, char paddingChar, int maxLineLength) {
/* 214 */     this(base, name, writePadding, paddingChar, base._paddingReadBehaviour, maxLineLength);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Base64Variant(Base64Variant base, String name, boolean writePadding, char paddingChar, PaddingReadBehaviour paddingReadBehaviour, int maxLineLength) {
/* 220 */     this._name = name;
/* 221 */     byte[] srcB = base._base64ToAsciiB;
/* 222 */     System.arraycopy(srcB, 0, this._base64ToAsciiB, 0, srcB.length);
/* 223 */     char[] srcC = base._base64ToAsciiC;
/* 224 */     System.arraycopy(srcC, 0, this._base64ToAsciiC, 0, srcC.length);
/* 225 */     int[] srcV = base._asciiToBase64;
/* 226 */     System.arraycopy(srcV, 0, this._asciiToBase64, 0, srcV.length);
/*     */     
/* 228 */     this._writePadding = writePadding;
/* 229 */     this._paddingChar = paddingChar;
/* 230 */     this._maxLineLength = maxLineLength;
/* 231 */     this._paddingReadBehaviour = paddingReadBehaviour;
/*     */   }
/*     */   
/*     */   private Base64Variant(Base64Variant base, PaddingReadBehaviour paddingReadBehaviour) {
/* 235 */     this(base, base._name, base._writePadding, base._paddingChar, paddingReadBehaviour, base._maxLineLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64Variant withPaddingAllowed() {
/* 244 */     return withReadPadding(PaddingReadBehaviour.PADDING_ALLOWED);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64Variant withPaddingRequired() {
/* 252 */     return withReadPadding(PaddingReadBehaviour.PADDING_REQUIRED);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64Variant withPaddingForbidden() {
/* 260 */     return withReadPadding(PaddingReadBehaviour.PADDING_FORBIDDEN);
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
/*     */   public Base64Variant withReadPadding(PaddingReadBehaviour readPadding) {
/* 272 */     return (readPadding == this._paddingReadBehaviour) ? this : new Base64Variant(this, readPadding);
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
/*     */   public Base64Variant withWritePadding(boolean writePadding) {
/* 284 */     return (writePadding == this._writePadding) ? this : new Base64Variant(this, this._name, writePadding, this._paddingChar, this._maxLineLength);
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
/*     */   protected Object readResolve() {
/* 299 */     Base64Variant base = Base64Variants.valueOf(this._name);
/* 300 */     if (this._writePadding != base._writePadding || this._paddingChar != base._paddingChar || this._paddingReadBehaviour != base._paddingReadBehaviour || this._maxLineLength != base._maxLineLength || this._writePadding != base._writePadding)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 306 */       return new Base64Variant(base, this._name, this._writePadding, this._paddingChar, this._paddingReadBehaviour, this._maxLineLength);
/*     */     }
/*     */     
/* 309 */     return base;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 318 */     return this._name;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean usesPadding() {
/* 324 */     return this._writePadding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean requiresPaddingOnRead() {
/* 332 */     return (this._paddingReadBehaviour == PaddingReadBehaviour.PADDING_REQUIRED);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean acceptsPaddingOnRead() {
/* 341 */     return (this._paddingReadBehaviour != PaddingReadBehaviour.PADDING_FORBIDDEN);
/*     */   }
/*     */   
/* 344 */   public boolean usesPaddingChar(char c) { return (c == this._paddingChar); } public boolean usesPaddingChar(int ch) {
/* 345 */     return (ch == this._paddingChar);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PaddingReadBehaviour paddingReadBehaviour() {
/* 353 */     return this._paddingReadBehaviour;
/*     */   }
/* 355 */   public char getPaddingChar() { return this._paddingChar; } public byte getPaddingByte() {
/* 356 */     return (byte)this._paddingChar;
/*     */   } public int getMaxLineLength() {
/* 358 */     return this._maxLineLength;
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
/*     */   public int decodeBase64Char(char c) {
/* 373 */     int ch = c;
/* 374 */     return (ch <= 127) ? this._asciiToBase64[ch] : -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int decodeBase64Char(int ch) {
/* 379 */     return (ch <= 127) ? this._asciiToBase64[ch] : -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int decodeBase64Byte(byte b) {
/* 384 */     int ch = b;
/*     */     
/* 386 */     if (ch < 0) {
/* 387 */       return -1;
/*     */     }
/* 389 */     return this._asciiToBase64[ch];
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
/*     */   public char encodeBase64BitsAsChar(int value) {
/* 402 */     return this._base64ToAsciiC[value];
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
/*     */   public int encodeBase64Chunk(int b24, char[] buffer, int outPtr) {
/* 419 */     buffer[outPtr++] = this._base64ToAsciiC[b24 >> 18 & 0x3F];
/* 420 */     buffer[outPtr++] = this._base64ToAsciiC[b24 >> 12 & 0x3F];
/* 421 */     buffer[outPtr++] = this._base64ToAsciiC[b24 >> 6 & 0x3F];
/* 422 */     buffer[outPtr++] = this._base64ToAsciiC[b24 & 0x3F];
/* 423 */     return outPtr;
/*     */   }
/*     */ 
/*     */   
/*     */   public void encodeBase64Chunk(StringBuilder sb, int b24) {
/* 428 */     sb.append(this._base64ToAsciiC[b24 >> 18 & 0x3F]);
/* 429 */     sb.append(this._base64ToAsciiC[b24 >> 12 & 0x3F]);
/* 430 */     sb.append(this._base64ToAsciiC[b24 >> 6 & 0x3F]);
/* 431 */     sb.append(this._base64ToAsciiC[b24 & 0x3F]);
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
/*     */   public int encodeBase64Partial(int bits, int outputBytes, char[] buffer, int outPtr) {
/* 449 */     buffer[outPtr++] = this._base64ToAsciiC[bits >> 18 & 0x3F];
/* 450 */     buffer[outPtr++] = this._base64ToAsciiC[bits >> 12 & 0x3F];
/* 451 */     if (usesPadding()) {
/* 452 */       buffer[outPtr++] = (outputBytes == 2) ? this._base64ToAsciiC[bits >> 6 & 0x3F] : this._paddingChar;
/*     */       
/* 454 */       buffer[outPtr++] = this._paddingChar;
/*     */     }
/* 456 */     else if (outputBytes == 2) {
/* 457 */       buffer[outPtr++] = this._base64ToAsciiC[bits >> 6 & 0x3F];
/*     */     } 
/*     */     
/* 460 */     return outPtr;
/*     */   }
/*     */ 
/*     */   
/*     */   public void encodeBase64Partial(StringBuilder sb, int bits, int outputBytes) {
/* 465 */     sb.append(this._base64ToAsciiC[bits >> 18 & 0x3F]);
/* 466 */     sb.append(this._base64ToAsciiC[bits >> 12 & 0x3F]);
/* 467 */     if (usesPadding()) {
/* 468 */       sb.append((outputBytes == 2) ? this._base64ToAsciiC[bits >> 6 & 0x3F] : this._paddingChar);
/*     */       
/* 470 */       sb.append(this._paddingChar);
/*     */     }
/* 472 */     else if (outputBytes == 2) {
/* 473 */       sb.append(this._base64ToAsciiC[bits >> 6 & 0x3F]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte encodeBase64BitsAsByte(int value) {
/* 481 */     return this._base64ToAsciiB[value];
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
/*     */   public int encodeBase64Chunk(int b24, byte[] buffer, int outPtr) {
/* 496 */     buffer[outPtr++] = this._base64ToAsciiB[b24 >> 18 & 0x3F];
/* 497 */     buffer[outPtr++] = this._base64ToAsciiB[b24 >> 12 & 0x3F];
/* 498 */     buffer[outPtr++] = this._base64ToAsciiB[b24 >> 6 & 0x3F];
/* 499 */     buffer[outPtr++] = this._base64ToAsciiB[b24 & 0x3F];
/* 500 */     return outPtr;
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
/*     */   public int encodeBase64Partial(int bits, int outputBytes, byte[] buffer, int outPtr) {
/* 518 */     buffer[outPtr++] = this._base64ToAsciiB[bits >> 18 & 0x3F];
/* 519 */     buffer[outPtr++] = this._base64ToAsciiB[bits >> 12 & 0x3F];
/* 520 */     if (usesPadding()) {
/* 521 */       byte pb = (byte)this._paddingChar;
/* 522 */       buffer[outPtr++] = (outputBytes == 2) ? this._base64ToAsciiB[bits >> 6 & 0x3F] : pb;
/*     */       
/* 524 */       buffer[outPtr++] = pb;
/*     */     }
/* 526 */     else if (outputBytes == 2) {
/* 527 */       buffer[outPtr++] = this._base64ToAsciiB[bits >> 6 & 0x3F];
/*     */     } 
/*     */     
/* 530 */     return outPtr;
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
/*     */   public String encode(byte[] input) {
/* 550 */     return encode(input, false);
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
/*     */   public String encode(byte[] input, boolean addQuotes) {
/* 567 */     int inputEnd = input.length;
/* 568 */     StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
/* 569 */     if (addQuotes) {
/* 570 */       sb.append('"');
/*     */     }
/*     */     
/* 573 */     int chunksBeforeLF = getMaxLineLength() >> 2;
/*     */ 
/*     */     
/* 576 */     int inputPtr = 0;
/* 577 */     int safeInputEnd = inputEnd - 3;
/*     */     
/* 579 */     while (inputPtr <= safeInputEnd) {
/*     */       
/* 581 */       int b24 = input[inputPtr++] << 8;
/* 582 */       b24 |= input[inputPtr++] & 0xFF;
/* 583 */       b24 = b24 << 8 | input[inputPtr++] & 0xFF;
/* 584 */       encodeBase64Chunk(sb, b24);
/* 585 */       if (--chunksBeforeLF <= 0) {
/*     */         
/* 587 */         sb.append('\\');
/* 588 */         sb.append('n');
/* 589 */         chunksBeforeLF = getMaxLineLength() >> 2;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 594 */     int inputLeft = inputEnd - inputPtr;
/* 595 */     if (inputLeft > 0) {
/* 596 */       int b24 = input[inputPtr++] << 16;
/* 597 */       if (inputLeft == 2) {
/* 598 */         b24 |= (input[inputPtr++] & 0xFF) << 8;
/*     */       }
/* 600 */       encodeBase64Partial(sb, b24, inputLeft);
/*     */     } 
/*     */     
/* 603 */     if (addQuotes) {
/* 604 */       sb.append('"');
/*     */     }
/* 606 */     return sb.toString();
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
/*     */   public String encode(byte[] input, boolean addQuotes, String linefeed) {
/* 624 */     int inputEnd = input.length;
/* 625 */     StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
/* 626 */     if (addQuotes) {
/* 627 */       sb.append('"');
/*     */     }
/*     */     
/* 630 */     int chunksBeforeLF = getMaxLineLength() >> 2;
/*     */     
/* 632 */     int inputPtr = 0;
/* 633 */     int safeInputEnd = inputEnd - 3;
/*     */     
/* 635 */     while (inputPtr <= safeInputEnd) {
/* 636 */       int b24 = input[inputPtr++] << 8;
/* 637 */       b24 |= input[inputPtr++] & 0xFF;
/* 638 */       b24 = b24 << 8 | input[inputPtr++] & 0xFF;
/* 639 */       encodeBase64Chunk(sb, b24);
/* 640 */       if (--chunksBeforeLF <= 0) {
/* 641 */         sb.append(linefeed);
/* 642 */         chunksBeforeLF = getMaxLineLength() >> 2;
/*     */       } 
/*     */     } 
/* 645 */     int inputLeft = inputEnd - inputPtr;
/* 646 */     if (inputLeft > 0) {
/* 647 */       int b24 = input[inputPtr++] << 16;
/* 648 */       if (inputLeft == 2) {
/* 649 */         b24 |= (input[inputPtr++] & 0xFF) << 8;
/*     */       }
/* 651 */       encodeBase64Partial(sb, b24, inputLeft);
/*     */     } 
/*     */     
/* 654 */     if (addQuotes) {
/* 655 */       sb.append('"');
/*     */     }
/* 657 */     return sb.toString();
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
/*     */   public byte[] decode(String input) throws IllegalArgumentException {
/* 675 */     ByteArrayBuilder b = new ByteArrayBuilder();
/* 676 */     decode(input, b);
/* 677 */     return b.toByteArray();
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
/*     */   public void decode(String str, ByteArrayBuilder builder) throws IllegalArgumentException {
/* 698 */     int ptr = 0;
/* 699 */     int len = str.length();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 706 */     while (ptr < len) {
/*     */ 
/*     */       
/* 709 */       char ch = str.charAt(ptr++);
/* 710 */       if (ch > ' ') {
/* 711 */         int bits = decodeBase64Char(ch);
/* 712 */         if (bits < 0) {
/* 713 */           _reportInvalidBase64(ch, 0, null);
/*     */         }
/* 715 */         int decodedData = bits;
/*     */         
/* 717 */         if (ptr >= len) {
/* 718 */           _reportBase64EOF();
/*     */         }
/* 720 */         ch = str.charAt(ptr++);
/* 721 */         bits = decodeBase64Char(ch);
/* 722 */         if (bits < 0) {
/* 723 */           _reportInvalidBase64(ch, 1, null);
/*     */         }
/* 725 */         decodedData = decodedData << 6 | bits;
/*     */         
/* 727 */         if (ptr >= len) {
/*     */           
/* 729 */           if (!requiresPaddingOnRead()) {
/* 730 */             decodedData >>= 4;
/* 731 */             builder.append(decodedData);
/*     */             break;
/*     */           } 
/* 734 */           _reportBase64EOF();
/*     */         } 
/* 736 */         ch = str.charAt(ptr++);
/* 737 */         bits = decodeBase64Char(ch);
/*     */ 
/*     */         
/* 740 */         if (bits < 0) {
/* 741 */           if (bits != -2) {
/* 742 */             _reportInvalidBase64(ch, 2, null);
/*     */           }
/* 744 */           if (!acceptsPaddingOnRead()) {
/* 745 */             _reportBase64UnexpectedPadding();
/*     */           }
/*     */           
/* 748 */           if (ptr >= len) {
/* 749 */             _reportBase64EOF();
/*     */           }
/* 751 */           ch = str.charAt(ptr++);
/* 752 */           if (!usesPaddingChar(ch)) {
/* 753 */             _reportInvalidBase64(ch, 3, "expected padding character '" + getPaddingChar() + "'");
/*     */           }
/*     */           
/* 756 */           decodedData >>= 4;
/* 757 */           builder.append(decodedData);
/*     */           
/*     */           continue;
/*     */         } 
/* 761 */         decodedData = decodedData << 6 | bits;
/*     */         
/* 763 */         if (ptr >= len) {
/*     */           
/* 765 */           if (!requiresPaddingOnRead()) {
/* 766 */             decodedData >>= 2;
/* 767 */             builder.appendTwoBytes(decodedData);
/*     */             break;
/*     */           } 
/* 770 */           _reportBase64EOF();
/*     */         } 
/* 772 */         ch = str.charAt(ptr++);
/* 773 */         bits = decodeBase64Char(ch);
/* 774 */         if (bits < 0) {
/* 775 */           if (bits != -2) {
/* 776 */             _reportInvalidBase64(ch, 3, null);
/*     */           }
/* 778 */           if (!acceptsPaddingOnRead()) {
/* 779 */             _reportBase64UnexpectedPadding();
/*     */           }
/* 781 */           decodedData >>= 2;
/* 782 */           builder.appendTwoBytes(decodedData);
/*     */           continue;
/*     */         } 
/* 785 */         decodedData = decodedData << 6 | bits;
/* 786 */         builder.appendThreeBytes(decodedData);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 798 */     return this._name;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 804 */     if (o == this) return true; 
/* 805 */     if (o == null || o.getClass() != getClass()) return false;
/*     */     
/* 807 */     Base64Variant other = (Base64Variant)o;
/* 808 */     return (other._paddingChar == this._paddingChar && other._maxLineLength == this._maxLineLength && other._writePadding == this._writePadding && other._paddingReadBehaviour == this._paddingReadBehaviour && this._name
/*     */ 
/*     */ 
/*     */       
/* 812 */       .equals(other._name));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 818 */     return this._name.hashCode();
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
/*     */   protected void _reportInvalidBase64(char ch, int bindex, String msg) throws IllegalArgumentException {
/*     */     String base;
/* 837 */     if (ch <= ' ') {
/* 838 */       base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units";
/* 839 */     } else if (usesPaddingChar(ch)) {
/* 840 */       base = "Unexpected padding character ('" + getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
/* 841 */     } else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
/*     */       
/* 843 */       base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*     */     } else {
/* 845 */       base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*     */     } 
/* 847 */     if (msg != null) {
/* 848 */       base = base + ": " + msg;
/*     */     }
/* 850 */     throw new IllegalArgumentException(base);
/*     */   }
/*     */   
/*     */   protected void _reportBase64EOF() throws IllegalArgumentException {
/* 854 */     throw new IllegalArgumentException(missingPaddingMessage());
/*     */   }
/*     */   
/*     */   protected void _reportBase64UnexpectedPadding() throws IllegalArgumentException {
/* 858 */     throw new IllegalArgumentException(unexpectedPaddingMessage());
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
/*     */   protected String unexpectedPaddingMessage() {
/* 870 */     return String.format("Unexpected end of base64-encoded String: base64 variant '%s' expects no padding at the end while decoding. This Base64Variant might have been incorrectly configured", new Object[] {
/* 871 */           getName()
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String missingPaddingMessage() {
/* 883 */     return String.format("Unexpected end of base64-encoded String: base64 variant '%s' expects padding (one or more '%c' characters) at the end. This Base64Variant might have been incorrectly configured", new Object[] {
/* 884 */           getName(), Character.valueOf(getPaddingChar())
/*     */         });
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/Base64Variant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */