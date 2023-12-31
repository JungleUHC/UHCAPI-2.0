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
/*     */ public enum JsonToken
/*     */ {
/*  31 */   NOT_AVAILABLE(null, -1),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  37 */   START_OBJECT("{", 1),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   END_OBJECT("}", 2),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   START_ARRAY("[", 3),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   END_ARRAY("]", 4),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   FIELD_NAME(null, 5),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   VALUE_EMBEDDED_OBJECT(null, 12),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   VALUE_STRING(null, 6),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  90 */   VALUE_NUMBER_INT(null, 7),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  99 */   VALUE_NUMBER_FLOAT(null, 8),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 105 */   VALUE_TRUE("true", 9),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 111 */   VALUE_FALSE("false", 10),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 117 */   VALUE_NULL("null", 11);
/*     */ 
/*     */   
/*     */   final String _serialized;
/*     */ 
/*     */   
/*     */   final char[] _serializedChars;
/*     */ 
/*     */   
/*     */   final byte[] _serializedBytes;
/*     */ 
/*     */   
/*     */   final int _id;
/*     */   
/*     */   final boolean _isStructStart;
/*     */   
/*     */   final boolean _isStructEnd;
/*     */   
/*     */   final boolean _isNumber;
/*     */   
/*     */   final boolean _isBoolean;
/*     */   
/*     */   final boolean _isScalar;
/*     */ 
/*     */   
/*     */   JsonToken(String token, int id) {
/* 143 */     if (token == null) {
/* 144 */       this._serialized = null;
/* 145 */       this._serializedChars = null;
/* 146 */       this._serializedBytes = null;
/*     */     } else {
/* 148 */       this._serialized = token;
/* 149 */       this._serializedChars = token.toCharArray();
/*     */       
/* 151 */       int len = this._serializedChars.length;
/* 152 */       this._serializedBytes = new byte[len];
/* 153 */       for (int i = 0; i < len; i++) {
/* 154 */         this._serializedBytes[i] = (byte)this._serializedChars[i];
/*     */       }
/*     */     } 
/* 157 */     this._id = id;
/*     */     
/* 159 */     this._isBoolean = (id == 10 || id == 9);
/* 160 */     this._isNumber = (id == 7 || id == 8);
/*     */     
/* 162 */     this._isStructStart = (id == 1 || id == 3);
/* 163 */     this._isStructEnd = (id == 2 || id == 4);
/*     */     
/* 165 */     this._isScalar = (!this._isStructStart && !this._isStructEnd && id != 5 && id != -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int id() {
/* 170 */     return this._id;
/*     */   }
/* 172 */   public final String asString() { return this._serialized; }
/* 173 */   public final char[] asCharArray() { return this._serializedChars; } public final byte[] asByteArray() {
/* 174 */     return this._serializedBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isNumeric() {
/* 180 */     return this._isNumber;
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
/*     */   public final boolean isStructStart() {
/* 193 */     return this._isStructStart;
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
/*     */   public final boolean isStructEnd() {
/* 206 */     return this._isStructEnd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isScalarValue() {
/* 217 */     return this._isScalar;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isBoolean() {
/* 223 */     return this._isBoolean;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */