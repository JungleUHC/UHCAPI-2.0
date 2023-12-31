/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public final class CharTypes
/*     */ {
/*   7 */   protected static final char[] HC = "0123456789ABCDEF".toCharArray(); protected static final byte[] HB; protected static final int[] sInputCodes;
/*     */   
/*     */   static {
/*  10 */     int len = HC.length;
/*  11 */     HB = new byte[len]; int k;
/*  12 */     for (k = 0; k < len; k++) {
/*  13 */       HB[k] = (byte)HC[k];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  27 */     int[] arrayOfInt1 = new int[256];
/*     */     
/*  29 */     for (k = 0; k < 32; k++) {
/*  30 */       arrayOfInt1[k] = -1;
/*     */     }
/*     */     
/*  33 */     arrayOfInt1[34] = 1;
/*  34 */     arrayOfInt1[92] = 1;
/*  35 */     sInputCodes = arrayOfInt1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  44 */     arrayOfInt1 = new int[sInputCodes.length];
/*  45 */     System.arraycopy(sInputCodes, 0, arrayOfInt1, 0, arrayOfInt1.length);
/*  46 */     for (int c = 128; c < 256; c++) {
/*     */       int code;
/*     */ 
/*     */       
/*  50 */       if ((c & 0xE0) == 192) {
/*  51 */         code = 2;
/*  52 */       } else if ((c & 0xF0) == 224) {
/*  53 */         code = 3;
/*  54 */       } else if ((c & 0xF8) == 240) {
/*     */         
/*  56 */         code = 4;
/*     */       } else {
/*     */         
/*  59 */         code = -1;
/*     */       } 
/*  61 */       arrayOfInt1[c] = code;
/*     */     } 
/*  63 */     sInputCodesUTF8 = arrayOfInt1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     arrayOfInt1 = new int[256];
/*     */     
/*  76 */     Arrays.fill(arrayOfInt1, -1);
/*     */     int j;
/*  78 */     for (j = 33; j < 256; j++) {
/*  79 */       if (Character.isJavaIdentifierPart((char)j)) {
/*  80 */         arrayOfInt1[j] = 0;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  86 */     arrayOfInt1[64] = 0;
/*  87 */     arrayOfInt1[35] = 0;
/*  88 */     arrayOfInt1[42] = 0;
/*  89 */     arrayOfInt1[45] = 0;
/*  90 */     arrayOfInt1[43] = 0;
/*  91 */     sInputCodesJsNames = arrayOfInt1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     arrayOfInt1 = new int[256];
/*     */     
/* 103 */     System.arraycopy(sInputCodesJsNames, 0, arrayOfInt1, 0, arrayOfInt1.length);
/* 104 */     Arrays.fill(arrayOfInt1, 128, 128, 0);
/* 105 */     sInputCodesUtf8JsNames = arrayOfInt1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     int[] buf = new int[256];
/*     */     
/* 116 */     System.arraycopy(sInputCodesUTF8, 128, buf, 128, 128);
/*     */ 
/*     */     
/* 119 */     Arrays.fill(buf, 0, 32, -1);
/* 120 */     buf[9] = 0;
/* 121 */     buf[10] = 10;
/* 122 */     buf[13] = 13;
/* 123 */     buf[42] = 42;
/* 124 */     sInputCodesComment = buf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 135 */     buf = new int[256];
/* 136 */     System.arraycopy(sInputCodesUTF8, 128, buf, 128, 128);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 141 */     Arrays.fill(buf, 0, 32, -1);
/* 142 */     buf[32] = 1;
/* 143 */     buf[9] = 1;
/* 144 */     buf[10] = 10;
/* 145 */     buf[13] = 13;
/* 146 */     buf[47] = 47;
/* 147 */     buf[35] = 35;
/* 148 */     sInputCodesWS = buf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 157 */     int[] table = new int[128];
/*     */     
/* 159 */     for (j = 0; j < 32; j++)
/*     */     {
/* 161 */       table[j] = -1;
/*     */     }
/*     */     
/* 164 */     table[34] = 34;
/* 165 */     table[92] = 92;
/*     */     
/* 167 */     table[8] = 98;
/* 168 */     table[9] = 116;
/* 169 */     table[12] = 102;
/* 170 */     table[10] = 110;
/* 171 */     table[13] = 114;
/* 172 */     sOutputEscapes128 = table;
/*     */   }
/*     */ 
/*     */   
/*     */   protected static final int[] sInputCodesUTF8;
/*     */   protected static final int[] sInputCodesJsNames;
/*     */   protected static final int[] sInputCodesUtf8JsNames;
/*     */   protected static final int[] sInputCodesComment;
/*     */   protected static final int[] sInputCodesWS;
/*     */   protected static final int[] sOutputEscapes128;
/* 182 */   protected static final int[] sHexValues = new int[256];
/*     */   static {
/* 184 */     Arrays.fill(sHexValues, -1); int i;
/* 185 */     for (i = 0; i < 10; i++) {
/* 186 */       sHexValues[48 + i] = i;
/*     */     }
/* 188 */     for (i = 0; i < 6; i++) {
/* 189 */       sHexValues[97 + i] = 10 + i;
/* 190 */       sHexValues[65 + i] = 10 + i;
/*     */     } 
/*     */   }
/*     */   
/* 194 */   public static int[] getInputCodeLatin1() { return sInputCodes; } public static int[] getInputCodeUtf8() {
/* 195 */     return sInputCodesUTF8;
/*     */   }
/* 197 */   public static int[] getInputCodeLatin1JsNames() { return sInputCodesJsNames; } public static int[] getInputCodeUtf8JsNames() {
/* 198 */     return sInputCodesUtf8JsNames;
/*     */   }
/* 200 */   public static int[] getInputCodeComment() { return sInputCodesComment; } public static int[] getInputCodeWS() {
/* 201 */     return sInputCodesWS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] get7BitOutputEscapes() {
/* 212 */     return sOutputEscapes128;
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
/*     */   public static int[] get7BitOutputEscapes(int quoteChar) {
/* 226 */     if (quoteChar == 34) {
/* 227 */       return sOutputEscapes128;
/*     */     }
/* 229 */     return AltEscapes.instance.escapesFor(quoteChar);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int charToHex(int ch) {
/* 236 */     return sHexValues[ch & 0xFF];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static char hexToChar(int ch) {
/* 242 */     return HC[ch];
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
/*     */   public static void appendQuoted(StringBuilder sb, String content) {
/* 256 */     int[] escCodes = sOutputEscapes128;
/* 257 */     int escLen = escCodes.length;
/* 258 */     for (int i = 0, len = content.length(); i < len; i++) {
/* 259 */       char c = content.charAt(i);
/* 260 */       if (c >= escLen || escCodes[c] == 0) {
/* 261 */         sb.append(c);
/*     */       } else {
/*     */         
/* 264 */         sb.append('\\');
/* 265 */         int escCode = escCodes[c];
/* 266 */         if (escCode < 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 275 */           sb.append('u');
/* 276 */           sb.append('0');
/* 277 */           sb.append('0');
/* 278 */           int value = c;
/* 279 */           sb.append(HC[value >> 4]);
/* 280 */           sb.append(HC[value & 0xF]);
/*     */         } else {
/* 282 */           sb.append((char)escCode);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public static char[] copyHexChars() {
/* 288 */     return (char[])HC.clone();
/*     */   }
/*     */   
/*     */   public static byte[] copyHexBytes() {
/* 292 */     return (byte[])HB.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class AltEscapes
/*     */   {
/* 303 */     public static final AltEscapes instance = new AltEscapes();
/*     */     
/* 305 */     private int[][] _altEscapes = new int[128][];
/*     */     
/*     */     public int[] escapesFor(int quoteChar) {
/* 308 */       int[] esc = this._altEscapes[quoteChar];
/* 309 */       if (esc == null) {
/* 310 */         esc = Arrays.copyOf(CharTypes.sOutputEscapes128, 128);
/*     */         
/* 312 */         if (esc[quoteChar] == 0) {
/* 313 */           esc[quoteChar] = -1;
/*     */         }
/* 315 */         this._altEscapes[quoteChar] = esc;
/*     */       } 
/* 317 */       return esc;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/CharTypes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */