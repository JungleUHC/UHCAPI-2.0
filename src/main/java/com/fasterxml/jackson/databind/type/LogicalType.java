/*     */ package com.fasterxml.jackson.databind.type;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum LogicalType
/*     */ {
/*  20 */   Array,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  26 */   Collection,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  32 */   Map,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   POJO,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   Untyped,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   Integer,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   Float,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   Boolean,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   Enum,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   Textual,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   Binary,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  93 */   DateTime,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  99 */   OtherScalar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LogicalType fromClass(Class<?> raw, LogicalType defaultIfNotRecognized) {
/* 116 */     if (raw.isEnum()) {
/* 117 */       return Enum;
/*     */     }
/* 119 */     if (raw.isArray()) {
/* 120 */       if (raw == byte[].class) {
/* 121 */         return Binary;
/*     */       }
/* 123 */       return Array;
/*     */     } 
/* 125 */     if (Collection.class.isAssignableFrom(raw)) {
/* 126 */       return Collection;
/*     */     }
/* 128 */     if (Map.class.isAssignableFrom(raw)) {
/* 129 */       return Map;
/*     */     }
/* 131 */     if (raw == String.class) {
/* 132 */       return Textual;
/*     */     }
/* 134 */     return defaultIfNotRecognized;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/type/LogicalType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */