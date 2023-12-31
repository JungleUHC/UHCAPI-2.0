/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Base64Variants
/*     */ {
/*     */   static final String STD_BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
/*  51 */   public static final Base64Variant MIME = new Base64Variant("MIME", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", true, '=', 76);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   public static final Base64Variant MIME_NO_LINEFEEDS = new Base64Variant(MIME, "MIME-NO-LINEFEEDS", 2147483647);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   public static final Base64Variant PEM = new Base64Variant(MIME, "PEM", true, '=', 64);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final Base64Variant MODIFIED_FOR_URL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  90 */     StringBuilder sb = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
/*     */     
/*  92 */     sb.setCharAt(sb.indexOf("+"), '-');
/*  93 */     sb.setCharAt(sb.indexOf("/"), '_');
/*     */     
/*  95 */     MODIFIED_FOR_URL = new Base64Variant("MODIFIED-FOR-URL", sb.toString(), false, false, 2147483647);
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
/*     */   public static Base64Variant getDefaultVariant() {
/* 107 */     return MIME_NO_LINEFEEDS;
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
/*     */   public static Base64Variant valueOf(String name) throws IllegalArgumentException {
/* 123 */     if (MIME._name.equals(name)) {
/* 124 */       return MIME;
/*     */     }
/* 126 */     if (MIME_NO_LINEFEEDS._name.equals(name)) {
/* 127 */       return MIME_NO_LINEFEEDS;
/*     */     }
/* 129 */     if (PEM._name.equals(name)) {
/* 130 */       return PEM;
/*     */     }
/* 132 */     if (MODIFIED_FOR_URL._name.equals(name)) {
/* 133 */       return MODIFIED_FOR_URL;
/*     */     }
/* 135 */     if (name == null) {
/* 136 */       name = "<null>";
/*     */     } else {
/* 138 */       name = "'" + name + "'";
/*     */     } 
/* 140 */     throw new IllegalArgumentException("No Base64Variant with name " + name);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/Base64Variants.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */