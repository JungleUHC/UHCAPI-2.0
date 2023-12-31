/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ public final class NumberOutput
/*     */ {
/*   5 */   private static int MILLION = 1000000;
/*   6 */   private static int BILLION = 1000000000;
/*   7 */   private static long BILLION_L = 1000000000L;
/*     */   
/*   9 */   private static long MIN_INT_AS_LONG = -2147483648L;
/*  10 */   private static long MAX_INT_AS_LONG = 2147483647L;
/*     */   
/*  12 */   static final String SMALLEST_INT = String.valueOf(-2147483648);
/*  13 */   static final String SMALLEST_LONG = String.valueOf(Long.MIN_VALUE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  21 */   private static final int[] TRIPLET_TO_CHARS = new int[1000];
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  26 */     int fullIx = 0;
/*  27 */     for (int i1 = 0; i1 < 10; i1++) {
/*  28 */       for (int i2 = 0; i2 < 10; i2++) {
/*  29 */         for (int i3 = 0; i3 < 10; i3++) {
/*  30 */           int enc = i1 + 48 << 16 | i2 + 48 << 8 | i3 + 48;
/*     */ 
/*     */           
/*  33 */           TRIPLET_TO_CHARS[fullIx++] = enc;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*  39 */   private static final String[] sSmallIntStrs = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
/*     */ 
/*     */   
/*  42 */   private static final String[] sSmallIntStrs2 = new String[] { "-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10" };
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
/*     */   public static int outputInt(int v, char[] b, int off) {
/*  67 */     if (v < 0) {
/*  68 */       if (v == Integer.MIN_VALUE)
/*     */       {
/*     */         
/*  71 */         return _outputSmallestI(b, off);
/*     */       }
/*  73 */       b[off++] = '-';
/*  74 */       v = -v;
/*     */     } 
/*     */     
/*  77 */     if (v < MILLION) {
/*  78 */       if (v < 1000) {
/*  79 */         if (v < 10) {
/*  80 */           b[off] = (char)(48 + v);
/*  81 */           return off + 1;
/*     */         } 
/*  83 */         return _leading3(v, b, off);
/*     */       } 
/*  85 */       int i = v / 1000;
/*  86 */       v -= i * 1000;
/*  87 */       off = _leading3(i, b, off);
/*  88 */       off = _full3(v, b, off);
/*  89 */       return off;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  97 */     if (v >= BILLION) {
/*  98 */       v -= BILLION;
/*  99 */       if (v >= BILLION) {
/* 100 */         v -= BILLION;
/* 101 */         b[off++] = '2';
/*     */       } else {
/* 103 */         b[off++] = '1';
/*     */       } 
/* 105 */       return _outputFullBillion(v, b, off);
/*     */     } 
/* 107 */     int newValue = v / 1000;
/* 108 */     int ones = v - newValue * 1000;
/* 109 */     v = newValue;
/* 110 */     newValue /= 1000;
/* 111 */     int thousands = v - newValue * 1000;
/*     */     
/* 113 */     off = _leading3(newValue, b, off);
/* 114 */     off = _full3(thousands, b, off);
/* 115 */     return _full3(ones, b, off);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int outputInt(int v, byte[] b, int off) {
/* 120 */     if (v < 0) {
/* 121 */       if (v == Integer.MIN_VALUE) {
/* 122 */         return _outputSmallestI(b, off);
/*     */       }
/* 124 */       b[off++] = 45;
/* 125 */       v = -v;
/*     */     } 
/*     */     
/* 128 */     if (v < MILLION) {
/* 129 */       if (v < 1000) {
/* 130 */         if (v < 10) {
/* 131 */           b[off++] = (byte)(48 + v);
/*     */         } else {
/* 133 */           off = _leading3(v, b, off);
/*     */         } 
/*     */       } else {
/* 136 */         int i = v / 1000;
/* 137 */         v -= i * 1000;
/* 138 */         off = _leading3(i, b, off);
/* 139 */         off = _full3(v, b, off);
/*     */       } 
/* 141 */       return off;
/*     */     } 
/* 143 */     if (v >= BILLION) {
/* 144 */       v -= BILLION;
/* 145 */       if (v >= BILLION) {
/* 146 */         v -= BILLION;
/* 147 */         b[off++] = 50;
/*     */       } else {
/* 149 */         b[off++] = 49;
/*     */       } 
/* 151 */       return _outputFullBillion(v, b, off);
/*     */     } 
/* 153 */     int newValue = v / 1000;
/* 154 */     int ones = v - newValue * 1000;
/* 155 */     v = newValue;
/* 156 */     newValue /= 1000;
/* 157 */     int thousands = v - newValue * 1000;
/* 158 */     off = _leading3(newValue, b, off);
/* 159 */     off = _full3(thousands, b, off);
/* 160 */     return _full3(ones, b, off);
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
/*     */   public static int outputLong(long v, char[] b, int off) {
/* 179 */     if (v < 0L) {
/* 180 */       if (v > MIN_INT_AS_LONG) {
/* 181 */         return outputInt((int)v, b, off);
/*     */       }
/* 183 */       if (v == Long.MIN_VALUE) {
/* 184 */         return _outputSmallestL(b, off);
/*     */       }
/* 186 */       b[off++] = '-';
/* 187 */       v = -v;
/*     */     }
/* 189 */     else if (v <= MAX_INT_AS_LONG) {
/* 190 */       return outputInt((int)v, b, off);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 195 */     long upper = v / BILLION_L;
/* 196 */     v -= upper * BILLION_L;
/*     */ 
/*     */     
/* 199 */     if (upper < BILLION_L) {
/* 200 */       off = _outputUptoBillion((int)upper, b, off);
/*     */     } else {
/*     */       
/* 203 */       long hi = upper / BILLION_L;
/* 204 */       upper -= hi * BILLION_L;
/* 205 */       off = _leading3((int)hi, b, off);
/* 206 */       off = _outputFullBillion((int)upper, b, off);
/*     */     } 
/* 208 */     return _outputFullBillion((int)v, b, off);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int outputLong(long v, byte[] b, int off) {
/* 213 */     if (v < 0L) {
/* 214 */       if (v > MIN_INT_AS_LONG) {
/* 215 */         return outputInt((int)v, b, off);
/*     */       }
/* 217 */       if (v == Long.MIN_VALUE) {
/* 218 */         return _outputSmallestL(b, off);
/*     */       }
/* 220 */       b[off++] = 45;
/* 221 */       v = -v;
/*     */     }
/* 223 */     else if (v <= MAX_INT_AS_LONG) {
/* 224 */       return outputInt((int)v, b, off);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 229 */     long upper = v / BILLION_L;
/* 230 */     v -= upper * BILLION_L;
/*     */ 
/*     */     
/* 233 */     if (upper < BILLION_L) {
/* 234 */       off = _outputUptoBillion((int)upper, b, off);
/*     */     } else {
/*     */       
/* 237 */       long hi = upper / BILLION_L;
/* 238 */       upper -= hi * BILLION_L;
/* 239 */       off = _leading3((int)hi, b, off);
/* 240 */       off = _outputFullBillion((int)upper, b, off);
/*     */     } 
/* 242 */     return _outputFullBillion((int)v, b, off);
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
/*     */   public static String toString(int v) {
/* 257 */     if (v < sSmallIntStrs.length) {
/* 258 */       if (v >= 0) {
/* 259 */         return sSmallIntStrs[v];
/*     */       }
/* 261 */       int v2 = -v - 1;
/* 262 */       if (v2 < sSmallIntStrs2.length) {
/* 263 */         return sSmallIntStrs2[v2];
/*     */       }
/*     */     } 
/* 266 */     return Integer.toString(v);
/*     */   }
/*     */   
/*     */   public static String toString(long v) {
/* 270 */     if (v <= 2147483647L && v >= -2147483648L) {
/* 271 */       return toString((int)v);
/*     */     }
/* 273 */     return Long.toString(v);
/*     */   }
/*     */   
/*     */   public static String toString(double v) {
/* 277 */     return Double.toString(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String toString(float v) {
/* 282 */     return Float.toString(v);
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
/*     */   public static boolean notFinite(double value) {
/* 303 */     return (Double.isNaN(value) || Double.isInfinite(value));
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
/*     */   public static boolean notFinite(float value) {
/* 318 */     return (Float.isNaN(value) || Float.isInfinite(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int _outputUptoBillion(int v, char[] b, int off) {
/* 329 */     if (v < MILLION) {
/* 330 */       if (v < 1000) {
/* 331 */         return _leading3(v, b, off);
/*     */       }
/* 333 */       int i = v / 1000;
/* 334 */       int j = v - i * 1000;
/* 335 */       return _outputUptoMillion(b, off, i, j);
/*     */     } 
/* 337 */     int thousands = v / 1000;
/* 338 */     int ones = v - thousands * 1000;
/* 339 */     int millions = thousands / 1000;
/* 340 */     thousands -= millions * 1000;
/*     */     
/* 342 */     off = _leading3(millions, b, off);
/*     */     
/* 344 */     int enc = TRIPLET_TO_CHARS[thousands];
/* 345 */     b[off++] = (char)(enc >> 16);
/* 346 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 347 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 349 */     enc = TRIPLET_TO_CHARS[ones];
/* 350 */     b[off++] = (char)(enc >> 16);
/* 351 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 352 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 354 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputFullBillion(int v, char[] b, int off) {
/* 359 */     int thousands = v / 1000;
/* 360 */     int ones = v - thousands * 1000;
/* 361 */     int millions = thousands / 1000;
/*     */     
/* 363 */     int enc = TRIPLET_TO_CHARS[millions];
/* 364 */     b[off++] = (char)(enc >> 16);
/* 365 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 366 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 368 */     thousands -= millions * 1000;
/* 369 */     enc = TRIPLET_TO_CHARS[thousands];
/* 370 */     b[off++] = (char)(enc >> 16);
/* 371 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 372 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 374 */     enc = TRIPLET_TO_CHARS[ones];
/* 375 */     b[off++] = (char)(enc >> 16);
/* 376 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 377 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 379 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputUptoBillion(int v, byte[] b, int off) {
/* 384 */     if (v < MILLION) {
/* 385 */       if (v < 1000) {
/* 386 */         return _leading3(v, b, off);
/*     */       }
/* 388 */       int i = v / 1000;
/* 389 */       int j = v - i * 1000;
/* 390 */       return _outputUptoMillion(b, off, i, j);
/*     */     } 
/* 392 */     int thousands = v / 1000;
/* 393 */     int ones = v - thousands * 1000;
/* 394 */     int millions = thousands / 1000;
/* 395 */     thousands -= millions * 1000;
/*     */     
/* 397 */     off = _leading3(millions, b, off);
/*     */     
/* 399 */     int enc = TRIPLET_TO_CHARS[thousands];
/* 400 */     b[off++] = (byte)(enc >> 16);
/* 401 */     b[off++] = (byte)(enc >> 8);
/* 402 */     b[off++] = (byte)enc;
/*     */     
/* 404 */     enc = TRIPLET_TO_CHARS[ones];
/* 405 */     b[off++] = (byte)(enc >> 16);
/* 406 */     b[off++] = (byte)(enc >> 8);
/* 407 */     b[off++] = (byte)enc;
/*     */     
/* 409 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputFullBillion(int v, byte[] b, int off) {
/* 414 */     int thousands = v / 1000;
/* 415 */     int ones = v - thousands * 1000;
/* 416 */     int millions = thousands / 1000;
/* 417 */     thousands -= millions * 1000;
/*     */     
/* 419 */     int enc = TRIPLET_TO_CHARS[millions];
/* 420 */     b[off++] = (byte)(enc >> 16);
/* 421 */     b[off++] = (byte)(enc >> 8);
/* 422 */     b[off++] = (byte)enc;
/*     */     
/* 424 */     enc = TRIPLET_TO_CHARS[thousands];
/* 425 */     b[off++] = (byte)(enc >> 16);
/* 426 */     b[off++] = (byte)(enc >> 8);
/* 427 */     b[off++] = (byte)enc;
/*     */     
/* 429 */     enc = TRIPLET_TO_CHARS[ones];
/* 430 */     b[off++] = (byte)(enc >> 16);
/* 431 */     b[off++] = (byte)(enc >> 8);
/* 432 */     b[off++] = (byte)enc;
/*     */     
/* 434 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputUptoMillion(char[] b, int off, int thousands, int ones) {
/* 439 */     int enc = TRIPLET_TO_CHARS[thousands];
/* 440 */     if (thousands > 9) {
/* 441 */       if (thousands > 99) {
/* 442 */         b[off++] = (char)(enc >> 16);
/*     */       }
/* 444 */       b[off++] = (char)(enc >> 8 & 0x7F);
/*     */     } 
/* 446 */     b[off++] = (char)(enc & 0x7F);
/*     */     
/* 448 */     enc = TRIPLET_TO_CHARS[ones];
/* 449 */     b[off++] = (char)(enc >> 16);
/* 450 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 451 */     b[off++] = (char)(enc & 0x7F);
/* 452 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputUptoMillion(byte[] b, int off, int thousands, int ones) {
/* 457 */     int enc = TRIPLET_TO_CHARS[thousands];
/* 458 */     if (thousands > 9) {
/* 459 */       if (thousands > 99) {
/* 460 */         b[off++] = (byte)(enc >> 16);
/*     */       }
/* 462 */       b[off++] = (byte)(enc >> 8);
/*     */     } 
/* 464 */     b[off++] = (byte)enc;
/*     */     
/* 466 */     enc = TRIPLET_TO_CHARS[ones];
/* 467 */     b[off++] = (byte)(enc >> 16);
/* 468 */     b[off++] = (byte)(enc >> 8);
/* 469 */     b[off++] = (byte)enc;
/* 470 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _leading3(int t, char[] b, int off) {
/* 475 */     int enc = TRIPLET_TO_CHARS[t];
/* 476 */     if (t > 9) {
/* 477 */       if (t > 99) {
/* 478 */         b[off++] = (char)(enc >> 16);
/*     */       }
/* 480 */       b[off++] = (char)(enc >> 8 & 0x7F);
/*     */     } 
/* 482 */     b[off++] = (char)(enc & 0x7F);
/* 483 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _leading3(int t, byte[] b, int off) {
/* 488 */     int enc = TRIPLET_TO_CHARS[t];
/* 489 */     if (t > 9) {
/* 490 */       if (t > 99) {
/* 491 */         b[off++] = (byte)(enc >> 16);
/*     */       }
/* 493 */       b[off++] = (byte)(enc >> 8);
/*     */     } 
/* 495 */     b[off++] = (byte)enc;
/* 496 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _full3(int t, char[] b, int off) {
/* 501 */     int enc = TRIPLET_TO_CHARS[t];
/* 502 */     b[off++] = (char)(enc >> 16);
/* 503 */     b[off++] = (char)(enc >> 8 & 0x7F);
/* 504 */     b[off++] = (char)(enc & 0x7F);
/* 505 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _full3(int t, byte[] b, int off) {
/* 510 */     int enc = TRIPLET_TO_CHARS[t];
/* 511 */     b[off++] = (byte)(enc >> 16);
/* 512 */     b[off++] = (byte)(enc >> 8);
/* 513 */     b[off++] = (byte)enc;
/* 514 */     return off;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int _outputSmallestL(char[] b, int off) {
/* 521 */     int len = SMALLEST_LONG.length();
/* 522 */     SMALLEST_LONG.getChars(0, len, b, off);
/* 523 */     return off + len;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputSmallestL(byte[] b, int off) {
/* 528 */     int len = SMALLEST_LONG.length();
/* 529 */     for (int i = 0; i < len; i++) {
/* 530 */       b[off++] = (byte)SMALLEST_LONG.charAt(i);
/*     */     }
/* 532 */     return off;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputSmallestI(char[] b, int off) {
/* 537 */     int len = SMALLEST_INT.length();
/* 538 */     SMALLEST_INT.getChars(0, len, b, off);
/* 539 */     return off + len;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int _outputSmallestI(byte[] b, int off) {
/* 544 */     int len = SMALLEST_INT.length();
/* 545 */     for (int i = 0; i < len; i++) {
/* 546 */       b[off++] = (byte)SMALLEST_INT.charAt(i);
/*     */     }
/* 548 */     return off;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/NumberOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */