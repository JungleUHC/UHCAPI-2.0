/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferRecycler
/*     */ {
/*     */   public static final int BYTE_READ_IO_BUFFER = 0;
/*     */   public static final int BYTE_WRITE_ENCODING_BUFFER = 1;
/*     */   public static final int BYTE_WRITE_CONCAT_BUFFER = 2;
/*     */   public static final int BYTE_BASE64_CODEC_BUFFER = 3;
/*     */   public static final int CHAR_TOKEN_BUFFER = 0;
/*     */   public static final int CHAR_CONCAT_BUFFER = 1;
/*     */   public static final int CHAR_TEXT_BUFFER = 2;
/*     */   public static final int CHAR_NAME_COPY_BUFFER = 3;
/*  76 */   private static final int[] BYTE_BUFFER_LENGTHS = new int[] { 8000, 8000, 2000, 2000 };
/*  77 */   private static final int[] CHAR_BUFFER_LENGTHS = new int[] { 4000, 4000, 200, 200 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AtomicReferenceArray<byte[]> _byteBuffers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AtomicReferenceArray<char[]> _charBuffers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferRecycler() {
/*  96 */     this(4, 4);
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
/*     */   protected BufferRecycler(int bbCount, int cbCount) {
/* 109 */     this._byteBuffers = (AtomicReferenceArray)new AtomicReferenceArray<byte>(bbCount);
/* 110 */     this._charBuffers = (AtomicReferenceArray)new AtomicReferenceArray<char>(cbCount);
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
/*     */   public final byte[] allocByteBuffer(int ix) {
/* 125 */     return allocByteBuffer(ix, 0);
/*     */   }
/*     */   
/*     */   public byte[] allocByteBuffer(int ix, int minSize) {
/* 129 */     int DEF_SIZE = byteBufferLength(ix);
/* 130 */     if (minSize < DEF_SIZE) {
/* 131 */       minSize = DEF_SIZE;
/*     */     }
/* 133 */     byte[] buffer = this._byteBuffers.getAndSet(ix, null);
/* 134 */     if (buffer == null || buffer.length < minSize) {
/* 135 */       buffer = balloc(minSize);
/*     */     }
/* 137 */     return buffer;
/*     */   }
/*     */   
/*     */   public void releaseByteBuffer(int ix, byte[] buffer) {
/* 141 */     this._byteBuffers.set(ix, buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final char[] allocCharBuffer(int ix) {
/* 151 */     return allocCharBuffer(ix, 0);
/*     */   }
/*     */   
/*     */   public char[] allocCharBuffer(int ix, int minSize) {
/* 155 */     int DEF_SIZE = charBufferLength(ix);
/* 156 */     if (minSize < DEF_SIZE) {
/* 157 */       minSize = DEF_SIZE;
/*     */     }
/* 159 */     char[] buffer = this._charBuffers.getAndSet(ix, null);
/* 160 */     if (buffer == null || buffer.length < minSize) {
/* 161 */       buffer = calloc(minSize);
/*     */     }
/* 163 */     return buffer;
/*     */   }
/*     */   
/*     */   public void releaseCharBuffer(int ix, char[] buffer) {
/* 167 */     this._charBuffers.set(ix, buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int byteBufferLength(int ix) {
/* 177 */     return BYTE_BUFFER_LENGTHS[ix];
/*     */   }
/*     */   
/*     */   protected int charBufferLength(int ix) {
/* 181 */     return CHAR_BUFFER_LENGTHS[ix];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] balloc(int size) {
/* 190 */     return new byte[size]; } protected char[] calloc(int size) {
/* 191 */     return new char[size];
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/BufferRecycler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */