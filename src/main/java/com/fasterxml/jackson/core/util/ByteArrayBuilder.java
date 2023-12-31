/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ByteArrayBuilder
/*     */   extends OutputStream
/*     */ {
/*  31 */   public static final byte[] NO_BYTES = new byte[0];
/*     */ 
/*     */   
/*     */   private static final int INITIAL_BLOCK_SIZE = 500;
/*     */ 
/*     */   
/*     */   private static final int MAX_BLOCK_SIZE = 131072;
/*     */ 
/*     */   
/*     */   static final int DEFAULT_BLOCK_ARRAY_SIZE = 40;
/*     */   
/*     */   private final BufferRecycler _bufferRecycler;
/*     */   
/*  44 */   private final LinkedList<byte[]> _pastBlocks = (LinkedList)new LinkedList<byte>();
/*     */   
/*     */   private int _pastLen;
/*     */   private byte[] _currBlock;
/*     */   private int _currBlockPtr;
/*     */   
/*     */   public ByteArrayBuilder() {
/*  51 */     this((BufferRecycler)null); }
/*  52 */   public ByteArrayBuilder(BufferRecycler br) { this(br, 500); } public ByteArrayBuilder(int firstBlockSize) {
/*  53 */     this(null, firstBlockSize);
/*     */   }
/*     */   public ByteArrayBuilder(BufferRecycler br, int firstBlockSize) {
/*  56 */     this._bufferRecycler = br;
/*     */ 
/*     */     
/*  59 */     if (firstBlockSize > 131072) {
/*  60 */       firstBlockSize = 131072;
/*     */     }
/*  62 */     this._currBlock = (br == null) ? new byte[firstBlockSize] : br.allocByteBuffer(2);
/*     */   }
/*     */   
/*     */   private ByteArrayBuilder(BufferRecycler br, byte[] initialBlock, int initialLen) {
/*  66 */     this._bufferRecycler = null;
/*  67 */     this._currBlock = initialBlock;
/*  68 */     this._currBlockPtr = initialLen;
/*     */   }
/*     */   
/*     */   public static ByteArrayBuilder fromInitial(byte[] initialBlock, int length) {
/*  72 */     return new ByteArrayBuilder(null, initialBlock, length);
/*     */   }
/*     */   
/*     */   public void reset() {
/*  76 */     this._pastLen = 0;
/*  77 */     this._currBlockPtr = 0;
/*     */     
/*  79 */     if (!this._pastBlocks.isEmpty()) {
/*  80 */       this._pastBlocks.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/*  90 */     return this._pastLen + this._currBlockPtr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/*  99 */     reset();
/* 100 */     if (this._bufferRecycler != null && this._currBlock != null) {
/* 101 */       this._bufferRecycler.releaseByteBuffer(2, this._currBlock);
/* 102 */       this._currBlock = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void append(int i) {
/* 107 */     if (this._currBlockPtr >= this._currBlock.length) {
/* 108 */       _allocMore();
/*     */     }
/* 110 */     this._currBlock[this._currBlockPtr++] = (byte)i;
/*     */   }
/*     */   
/*     */   public void appendTwoBytes(int b16) {
/* 114 */     if (this._currBlockPtr + 1 < this._currBlock.length) {
/* 115 */       this._currBlock[this._currBlockPtr++] = (byte)(b16 >> 8);
/* 116 */       this._currBlock[this._currBlockPtr++] = (byte)b16;
/*     */     } else {
/* 118 */       append(b16 >> 8);
/* 119 */       append(b16);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void appendThreeBytes(int b24) {
/* 124 */     if (this._currBlockPtr + 2 < this._currBlock.length) {
/* 125 */       this._currBlock[this._currBlockPtr++] = (byte)(b24 >> 16);
/* 126 */       this._currBlock[this._currBlockPtr++] = (byte)(b24 >> 8);
/* 127 */       this._currBlock[this._currBlockPtr++] = (byte)b24;
/*     */     } else {
/* 129 */       append(b24 >> 16);
/* 130 */       append(b24 >> 8);
/* 131 */       append(b24);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void appendFourBytes(int b32) {
/* 137 */     if (this._currBlockPtr + 3 < this._currBlock.length) {
/* 138 */       this._currBlock[this._currBlockPtr++] = (byte)(b32 >> 24);
/* 139 */       this._currBlock[this._currBlockPtr++] = (byte)(b32 >> 16);
/* 140 */       this._currBlock[this._currBlockPtr++] = (byte)(b32 >> 8);
/* 141 */       this._currBlock[this._currBlockPtr++] = (byte)b32;
/*     */     } else {
/* 143 */       append(b32 >> 24);
/* 144 */       append(b32 >> 16);
/* 145 */       append(b32 >> 8);
/* 146 */       append(b32);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] toByteArray() {
/* 158 */     int totalLen = this._pastLen + this._currBlockPtr;
/*     */     
/* 160 */     if (totalLen == 0) {
/* 161 */       return NO_BYTES;
/*     */     }
/* 163 */     byte[] result = new byte[totalLen];
/* 164 */     int offset = 0;
/*     */     
/* 166 */     for (byte[] block : this._pastBlocks) {
/* 167 */       int len = block.length;
/* 168 */       System.arraycopy(block, 0, result, offset, len);
/* 169 */       offset += len;
/*     */     } 
/* 171 */     System.arraycopy(this._currBlock, 0, result, offset, this._currBlockPtr);
/* 172 */     offset += this._currBlockPtr;
/* 173 */     if (offset != totalLen) {
/* 174 */       throw new RuntimeException("Internal error: total len assumed to be " + totalLen + ", copied " + offset + " bytes");
/*     */     }
/*     */     
/* 177 */     if (!this._pastBlocks.isEmpty()) {
/* 178 */       reset();
/*     */     }
/* 180 */     return result;
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
/*     */   public byte[] resetAndGetFirstSegment() {
/* 196 */     reset();
/* 197 */     return this._currBlock;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] finishCurrentSegment() {
/* 208 */     _allocMore();
/* 209 */     return this._currBlock;
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
/*     */   public byte[] completeAndCoalesce(int lastBlockLength) {
/* 222 */     this._currBlockPtr = lastBlockLength;
/* 223 */     return toByteArray();
/*     */   }
/*     */   
/* 226 */   public byte[] getCurrentSegment() { return this._currBlock; }
/* 227 */   public void setCurrentSegmentLength(int len) { this._currBlockPtr = len; } public int getCurrentSegmentLength() {
/* 228 */     return this._currBlockPtr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b) {
/* 238 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) {
/*     */     while (true) {
/* 245 */       int max = this._currBlock.length - this._currBlockPtr;
/* 246 */       int toCopy = Math.min(max, len);
/* 247 */       if (toCopy > 0) {
/* 248 */         System.arraycopy(b, off, this._currBlock, this._currBlockPtr, toCopy);
/* 249 */         off += toCopy;
/* 250 */         this._currBlockPtr += toCopy;
/* 251 */         len -= toCopy;
/*     */       } 
/* 253 */       if (len <= 0)
/* 254 */         break;  _allocMore();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) {
/* 260 */     append(b);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() {}
/*     */ 
/*     */ 
/*     */   
/*     */   private void _allocMore() {
/* 274 */     int newPastLen = this._pastLen + this._currBlock.length;
/*     */ 
/*     */ 
/*     */     
/* 278 */     if (newPastLen < 0) {
/* 279 */       throw new IllegalStateException("Maximum Java array size (2GB) exceeded by `ByteArrayBuilder`");
/*     */     }
/*     */     
/* 282 */     this._pastLen = newPastLen;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 290 */     int newSize = Math.max(this._pastLen >> 1, 1000);
/*     */     
/* 292 */     if (newSize > 131072) {
/* 293 */       newSize = 131072;
/*     */     }
/* 295 */     this._pastBlocks.add(this._currBlock);
/* 296 */     this._currBlock = new byte[newSize];
/* 297 */     this._currBlockPtr = 0;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/ByteArrayBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */