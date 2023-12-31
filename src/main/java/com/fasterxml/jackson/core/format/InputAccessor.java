/*     */ package com.fasterxml.jackson.core.format;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface InputAccessor
/*     */ {
/*     */   boolean hasMoreBytes() throws IOException;
/*     */   
/*     */   byte nextByte() throws IOException;
/*     */   
/*     */   void reset();
/*     */   
/*     */   public static class Std
/*     */     implements InputAccessor
/*     */   {
/*     */     protected final InputStream _in;
/*     */     protected final byte[] _buffer;
/*     */     protected final int _bufferedStart;
/*     */     protected int _bufferedEnd;
/*     */     protected int _ptr;
/*     */     
/*     */     public Std(InputStream in, byte[] buffer) {
/*  72 */       this._in = in;
/*  73 */       this._buffer = buffer;
/*  74 */       this._bufferedStart = 0;
/*  75 */       this._ptr = 0;
/*  76 */       this._bufferedEnd = 0;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Std(byte[] inputDocument) {
/*  82 */       this(inputDocument, 0, inputDocument.length);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Std(byte[] inputDocument, int start, int len) {
/*  89 */       this._in = null;
/*  90 */       this._buffer = inputDocument;
/*  91 */       this._ptr = start;
/*  92 */       this._bufferedStart = start;
/*  93 */       this._bufferedEnd = start + len;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasMoreBytes() throws IOException {
/*  99 */       if (this._ptr < this._bufferedEnd) {
/* 100 */         return true;
/*     */       }
/* 102 */       if (this._in == null) {
/* 103 */         return false;
/*     */       }
/* 105 */       int amount = this._buffer.length - this._ptr;
/* 106 */       if (amount < 1) {
/* 107 */         return false;
/*     */       }
/* 109 */       int count = this._in.read(this._buffer, this._ptr, amount);
/* 110 */       if (count <= 0) {
/* 111 */         return false;
/*     */       }
/* 113 */       this._bufferedEnd += count;
/* 114 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public byte nextByte() throws IOException {
/* 121 */       if (this._ptr >= this._bufferedEnd && 
/* 122 */         !hasMoreBytes()) {
/* 123 */         throw new EOFException("Failed auto-detect: could not read more than " + this._ptr + " bytes (max buffer size: " + this._buffer.length + ")");
/*     */       }
/*     */       
/* 126 */       return this._buffer[this._ptr++];
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset() {
/* 131 */       this._ptr = this._bufferedStart;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DataFormatMatcher createMatcher(JsonFactory match, MatchStrength matchStrength) {
/* 142 */       return new DataFormatMatcher(this._in, this._buffer, this._bufferedStart, this._bufferedEnd - this._bufferedStart, match, matchStrength);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/format/InputAccessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */