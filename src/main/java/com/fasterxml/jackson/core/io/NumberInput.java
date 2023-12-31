/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class NumberInput
/*     */ {
/*     */   public static final String NASTY_SMALL_DOUBLE = "2.2250738585072012e-308";
/*     */   static final long L_BILLION = 1000000000L;
/*  18 */   static final String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
/*  19 */   static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int parseInt(char[] ch, int off, int len) {
/*  37 */     int num = ch[off + len - 1] - 48;
/*     */     
/*  39 */     switch (len) {
/*     */       case 9:
/*  41 */         num += (ch[off++] - 48) * 100000000;
/*     */       case 8:
/*  43 */         num += (ch[off++] - 48) * 10000000;
/*     */       case 7:
/*  45 */         num += (ch[off++] - 48) * 1000000;
/*     */       case 6:
/*  47 */         num += (ch[off++] - 48) * 100000;
/*     */       case 5:
/*  49 */         num += (ch[off++] - 48) * 10000;
/*     */       case 4:
/*  51 */         num += (ch[off++] - 48) * 1000;
/*     */       case 3:
/*  53 */         num += (ch[off++] - 48) * 100;
/*     */       case 2:
/*  55 */         num += (ch[off] - 48) * 10; break;
/*     */     } 
/*  57 */     return num;
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
/*     */   public static int parseInt(String s) {
/*  79 */     char c = s.charAt(0);
/*  80 */     int len = s.length();
/*  81 */     boolean neg = (c == '-');
/*  82 */     int offset = 1;
/*     */ 
/*     */     
/*  85 */     if (neg) {
/*  86 */       if (len == 1 || len > 10) {
/*  87 */         return Integer.parseInt(s);
/*     */       }
/*  89 */       c = s.charAt(offset++);
/*     */     }
/*  91 */     else if (len > 9) {
/*  92 */       return Integer.parseInt(s);
/*     */     } 
/*     */     
/*  95 */     if (c > '9' || c < '0') {
/*  96 */       return Integer.parseInt(s);
/*     */     }
/*  98 */     int num = c - 48;
/*  99 */     if (offset < len) {
/* 100 */       c = s.charAt(offset++);
/* 101 */       if (c > '9' || c < '0') {
/* 102 */         return Integer.parseInt(s);
/*     */       }
/* 104 */       num = num * 10 + c - 48;
/* 105 */       if (offset < len) {
/* 106 */         c = s.charAt(offset++);
/* 107 */         if (c > '9' || c < '0') {
/* 108 */           return Integer.parseInt(s);
/*     */         }
/* 110 */         num = num * 10 + c - 48;
/*     */         
/* 112 */         if (offset < len) {
/*     */           do {
/* 114 */             c = s.charAt(offset++);
/* 115 */             if (c > '9' || c < '0') {
/* 116 */               return Integer.parseInt(s);
/*     */             }
/* 118 */             num = num * 10 + c - 48;
/* 119 */           } while (offset < len);
/*     */         }
/*     */       } 
/*     */     } 
/* 123 */     return neg ? -num : num;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static long parseLong(char[] ch, int off, int len) {
/* 129 */     int len1 = len - 9;
/* 130 */     long val = parseInt(ch, off, len1) * 1000000000L;
/* 131 */     return val + parseInt(ch, off + len1, 9);
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
/*     */   public static long parseLong(String s) {
/* 145 */     int length = s.length();
/* 146 */     if (length <= 9) {
/* 147 */       return parseInt(s);
/*     */     }
/*     */     
/* 150 */     return Long.parseLong(s);
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
/*     */   public static boolean inLongRange(char[] ch, int off, int len, boolean negative) {
/* 171 */     String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
/* 172 */     int cmpLen = cmpStr.length();
/* 173 */     if (len < cmpLen) return true; 
/* 174 */     if (len > cmpLen) return false;
/*     */     
/* 176 */     for (int i = 0; i < cmpLen; i++) {
/* 177 */       int diff = ch[off + i] - cmpStr.charAt(i);
/* 178 */       if (diff != 0) {
/* 179 */         return (diff < 0);
/*     */       }
/*     */     } 
/* 182 */     return true;
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
/*     */   public static boolean inLongRange(String s, boolean negative) {
/* 198 */     String cmp = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
/* 199 */     int cmpLen = cmp.length();
/* 200 */     int alen = s.length();
/* 201 */     if (alen < cmpLen) return true; 
/* 202 */     if (alen > cmpLen) return false;
/*     */ 
/*     */     
/* 205 */     for (int i = 0; i < cmpLen; i++) {
/* 206 */       int diff = s.charAt(i) - cmp.charAt(i);
/* 207 */       if (diff != 0) {
/* 208 */         return (diff < 0);
/*     */       }
/*     */     } 
/* 211 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int parseAsInt(String s, int def) {
/* 216 */     if (s == null) {
/* 217 */       return def;
/*     */     }
/* 219 */     s = s.trim();
/* 220 */     int len = s.length();
/* 221 */     if (len == 0) {
/* 222 */       return def;
/*     */     }
/*     */     
/* 225 */     int i = 0;
/*     */     
/* 227 */     char sign = s.charAt(0);
/* 228 */     if (sign == '+') {
/* 229 */       s = s.substring(1);
/* 230 */       len = s.length();
/* 231 */     } else if (sign == '-') {
/* 232 */       i = 1;
/*     */     } 
/* 234 */     for (; i < len; i++) {
/* 235 */       char c = s.charAt(i);
/*     */       
/* 237 */       if (c > '9' || c < '0') {
/*     */         try {
/* 239 */           return (int)parseDouble(s);
/* 240 */         } catch (NumberFormatException e) {
/* 241 */           return def;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     try {
/* 246 */       return Integer.parseInt(s);
/* 247 */     } catch (NumberFormatException numberFormatException) {
/* 248 */       return def;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static long parseAsLong(String s, long def) {
/* 253 */     if (s == null) {
/* 254 */       return def;
/*     */     }
/* 256 */     s = s.trim();
/* 257 */     int len = s.length();
/* 258 */     if (len == 0) {
/* 259 */       return def;
/*     */     }
/*     */     
/* 262 */     int i = 0;
/*     */     
/* 264 */     char sign = s.charAt(0);
/* 265 */     if (sign == '+') {
/* 266 */       s = s.substring(1);
/* 267 */       len = s.length();
/* 268 */     } else if (sign == '-') {
/* 269 */       i = 1;
/*     */     } 
/* 271 */     for (; i < len; i++) {
/* 272 */       char c = s.charAt(i);
/*     */       
/* 274 */       if (c > '9' || c < '0') {
/*     */         try {
/* 276 */           return (long)parseDouble(s);
/* 277 */         } catch (NumberFormatException e) {
/* 278 */           return def;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     try {
/* 283 */       return Long.parseLong(s);
/* 284 */     } catch (NumberFormatException numberFormatException) {
/* 285 */       return def;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static double parseAsDouble(String s, double def) {
/* 290 */     if (s == null) return def; 
/* 291 */     s = s.trim();
/* 292 */     int len = s.length();
/* 293 */     if (len == 0) {
/* 294 */       return def;
/*     */     }
/*     */     try {
/* 297 */       return parseDouble(s);
/* 298 */     } catch (NumberFormatException numberFormatException) {
/* 299 */       return def;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double parseDouble(String s) throws NumberFormatException {
/* 307 */     if ("2.2250738585072012e-308".equals(s)) {
/* 308 */       return Double.MIN_VALUE;
/*     */     }
/* 310 */     return Double.parseDouble(s);
/*     */   }
/*     */   
/*     */   public static BigDecimal parseBigDecimal(String s) throws NumberFormatException {
/* 314 */     return BigDecimalParser.parse(s);
/*     */   }
/*     */   
/*     */   public static BigDecimal parseBigDecimal(char[] ch, int off, int len) throws NumberFormatException {
/* 318 */     return BigDecimalParser.parse(ch, off, len);
/*     */   }
/*     */   
/*     */   public static BigDecimal parseBigDecimal(char[] ch) throws NumberFormatException {
/* 322 */     return BigDecimalParser.parse(ch);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/NumberInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */