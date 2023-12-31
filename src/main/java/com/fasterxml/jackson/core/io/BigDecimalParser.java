/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import java.math.BigDecimal;
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
/*     */ 
/*     */ public final class BigDecimalParser
/*     */ {
/*     */   private final char[] chars;
/*     */   
/*     */   BigDecimalParser(char[] chars) {
/*  27 */     this.chars = chars;
/*     */   }
/*     */   
/*     */   public static BigDecimal parse(String valueStr) {
/*  31 */     return parse(valueStr.toCharArray());
/*     */   }
/*     */   
/*     */   public static BigDecimal parse(char[] chars, int off, int len) {
/*  35 */     if (off > 0 || len != chars.length) {
/*  36 */       chars = Arrays.copyOfRange(chars, off, off + len);
/*     */     }
/*  38 */     return parse(chars);
/*     */   }
/*     */   
/*     */   public static BigDecimal parse(char[] chars) {
/*  42 */     int len = chars.length;
/*     */     try {
/*  44 */       if (len < 500) {
/*  45 */         return new BigDecimal(chars);
/*     */       }
/*  47 */       return (new BigDecimalParser(chars)).parseBigDecimal(len / 10);
/*  48 */     } catch (NumberFormatException e) {
/*  49 */       String desc = e.getMessage();
/*     */       
/*  51 */       if (desc == null) {
/*  52 */         desc = "Not a valid number representation";
/*     */       }
/*  54 */       throw new NumberFormatException("Value \"" + new String(chars) + "\" can not be represented as `java.math.BigDecimal`, reason: " + desc);
/*     */     } 
/*     */   }
/*     */   private BigDecimal parseBigDecimal(int splitLen) {
/*     */     int numEndIdx;
/*     */     BigDecimal res;
/*  60 */     boolean numHasSign = false;
/*  61 */     boolean expHasSign = false;
/*  62 */     boolean neg = false;
/*  63 */     int numIdx = 0;
/*  64 */     int expIdx = -1;
/*  65 */     int dotIdx = -1;
/*  66 */     int scale = 0;
/*  67 */     int len = this.chars.length;
/*     */     
/*  69 */     for (int i = 0; i < len; i++) {
/*  70 */       char c = this.chars[i];
/*  71 */       switch (c) {
/*     */         case '+':
/*  73 */           if (expIdx >= 0) {
/*  74 */             if (expHasSign) {
/*  75 */               throw new NumberFormatException("Multiple signs in exponent");
/*     */             }
/*  77 */             expHasSign = true; break;
/*     */           } 
/*  79 */           if (numHasSign) {
/*  80 */             throw new NumberFormatException("Multiple signs in number");
/*     */           }
/*  82 */           numHasSign = true;
/*  83 */           numIdx = i + 1;
/*     */           break;
/*     */         
/*     */         case '-':
/*  87 */           if (expIdx >= 0) {
/*  88 */             if (expHasSign) {
/*  89 */               throw new NumberFormatException("Multiple signs in exponent");
/*     */             }
/*  91 */             expHasSign = true; break;
/*     */           } 
/*  93 */           if (numHasSign) {
/*  94 */             throw new NumberFormatException("Multiple signs in number");
/*     */           }
/*  96 */           numHasSign = true;
/*  97 */           neg = true;
/*  98 */           numIdx = i + 1;
/*     */           break;
/*     */         
/*     */         case 'E':
/*     */         case 'e':
/* 103 */           if (expIdx >= 0) {
/* 104 */             throw new NumberFormatException("Multiple exponent markers");
/*     */           }
/* 106 */           expIdx = i;
/*     */           break;
/*     */         case '.':
/* 109 */           if (dotIdx >= 0) {
/* 110 */             throw new NumberFormatException("Multiple decimal points");
/*     */           }
/* 112 */           dotIdx = i;
/*     */           break;
/*     */         default:
/* 115 */           if (dotIdx >= 0 && expIdx == -1) {
/* 116 */             scale++;
/*     */           }
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 122 */     int exp = 0;
/* 123 */     if (expIdx >= 0) {
/* 124 */       numEndIdx = expIdx;
/* 125 */       String expStr = new String(this.chars, expIdx + 1, len - expIdx - 1);
/* 126 */       exp = Integer.parseInt(expStr);
/* 127 */       scale = adjustScale(scale, exp);
/*     */     } else {
/* 129 */       numEndIdx = len;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 134 */     if (dotIdx >= 0) {
/* 135 */       int leftLen = dotIdx - numIdx;
/* 136 */       BigDecimal left = toBigDecimalRec(numIdx, leftLen, exp, splitLen);
/*     */       
/* 138 */       int rightLen = numEndIdx - dotIdx - 1;
/* 139 */       BigDecimal right = toBigDecimalRec(dotIdx + 1, rightLen, exp - rightLen, splitLen);
/*     */       
/* 141 */       res = left.add(right);
/*     */     } else {
/* 143 */       res = toBigDecimalRec(numIdx, numEndIdx - numIdx, exp, splitLen);
/*     */     } 
/*     */     
/* 146 */     if (scale != 0) {
/* 147 */       res = res.setScale(scale);
/*     */     }
/*     */     
/* 150 */     if (neg) {
/* 151 */       res = res.negate();
/*     */     }
/*     */     
/* 154 */     return res;
/*     */   }
/*     */   
/*     */   private int adjustScale(int scale, long exp) {
/* 158 */     long adjScale = scale - exp;
/* 159 */     if (adjScale > 2147483647L || adjScale < -2147483648L) {
/* 160 */       throw new NumberFormatException("Scale out of range: " + adjScale + " while adjusting scale " + scale + " to exponent " + exp);
/*     */     }
/*     */ 
/*     */     
/* 164 */     return (int)adjScale;
/*     */   }
/*     */   
/*     */   private BigDecimal toBigDecimalRec(int off, int len, int scale, int splitLen) {
/* 168 */     if (len > splitLen) {
/* 169 */       int mid = len / 2;
/* 170 */       BigDecimal left = toBigDecimalRec(off, mid, scale + len - mid, splitLen);
/* 171 */       BigDecimal right = toBigDecimalRec(off + mid, len - mid, scale, splitLen);
/*     */       
/* 173 */       return left.add(right);
/*     */     } 
/*     */     
/* 176 */     return (len == 0) ? BigDecimal.ZERO : (new BigDecimal(this.chars, off, len)).movePointRight(scale);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/BigDecimalParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */