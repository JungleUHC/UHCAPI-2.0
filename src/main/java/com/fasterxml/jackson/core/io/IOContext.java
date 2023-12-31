/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonEncoding;
/*     */ import com.fasterxml.jackson.core.util.BufferRecycler;
/*     */ import com.fasterxml.jackson.core.util.TextBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IOContext
/*     */ {
/*     */   protected final ContentReference _contentReference;
/*     */   @Deprecated
/*     */   protected final Object _sourceRef;
/*     */   protected JsonEncoding _encoding;
/*     */   protected final boolean _managedResource;
/*     */   protected final BufferRecycler _bufferRecycler;
/*     */   protected byte[] _readIOBuffer;
/*     */   protected byte[] _writeEncodingBuffer;
/*     */   protected byte[] _base64Buffer;
/*     */   protected char[] _tokenCBuffer;
/*     */   protected char[] _concatCBuffer;
/*     */   protected char[] _nameCopyBuffer;
/*     */   
/*     */   public IOContext(BufferRecycler br, ContentReference contentRef, boolean managedResource) {
/* 120 */     this._bufferRecycler = br;
/* 121 */     this._contentReference = contentRef;
/* 122 */     this._sourceRef = contentRef.getRawContent();
/* 123 */     this._managedResource = managedResource;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public IOContext(BufferRecycler br, Object rawContent, boolean managedResource) {
/* 128 */     this(br, ContentReference.rawReference(rawContent), managedResource);
/*     */   }
/*     */   
/*     */   public void setEncoding(JsonEncoding enc) {
/* 132 */     this._encoding = enc;
/*     */   }
/*     */   
/*     */   public IOContext withEncoding(JsonEncoding enc) {
/* 136 */     this._encoding = enc;
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonEncoding getEncoding() {
/* 146 */     return this._encoding; } public boolean isResourceManaged() {
/* 147 */     return this._managedResource;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContentReference contentReference() {
/* 158 */     return this._contentReference;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Object getSourceReference() {
/* 166 */     return this._sourceRef;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TextBuffer constructTextBuffer() {
/* 175 */     return new TextBuffer(this._bufferRecycler);
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
/*     */   public byte[] allocReadIOBuffer() {
/* 187 */     _verifyAlloc(this._readIOBuffer);
/* 188 */     return this._readIOBuffer = this._bufferRecycler.allocByteBuffer(0);
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
/*     */   public byte[] allocReadIOBuffer(int minSize) {
/* 202 */     _verifyAlloc(this._readIOBuffer);
/* 203 */     return this._readIOBuffer = this._bufferRecycler.allocByteBuffer(0, minSize);
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
/*     */   public byte[] allocWriteEncodingBuffer() {
/* 215 */     _verifyAlloc(this._writeEncodingBuffer);
/* 216 */     return this._writeEncodingBuffer = this._bufferRecycler.allocByteBuffer(1);
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
/*     */   public byte[] allocWriteEncodingBuffer(int minSize) {
/* 230 */     _verifyAlloc(this._writeEncodingBuffer);
/* 231 */     return this._writeEncodingBuffer = this._bufferRecycler.allocByteBuffer(1, minSize);
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
/*     */   public byte[] allocBase64Buffer() {
/* 243 */     _verifyAlloc(this._base64Buffer);
/* 244 */     return this._base64Buffer = this._bufferRecycler.allocByteBuffer(3);
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
/*     */   public byte[] allocBase64Buffer(int minSize) {
/* 258 */     _verifyAlloc(this._base64Buffer);
/* 259 */     return this._base64Buffer = this._bufferRecycler.allocByteBuffer(3, minSize);
/*     */   }
/*     */   
/*     */   public char[] allocTokenBuffer() {
/* 263 */     _verifyAlloc(this._tokenCBuffer);
/* 264 */     return this._tokenCBuffer = this._bufferRecycler.allocCharBuffer(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public char[] allocTokenBuffer(int minSize) {
/* 269 */     _verifyAlloc(this._tokenCBuffer);
/* 270 */     return this._tokenCBuffer = this._bufferRecycler.allocCharBuffer(0, minSize);
/*     */   }
/*     */   
/*     */   public char[] allocConcatBuffer() {
/* 274 */     _verifyAlloc(this._concatCBuffer);
/* 275 */     return this._concatCBuffer = this._bufferRecycler.allocCharBuffer(1);
/*     */   }
/*     */   
/*     */   public char[] allocNameCopyBuffer(int minSize) {
/* 279 */     _verifyAlloc(this._nameCopyBuffer);
/* 280 */     return this._nameCopyBuffer = this._bufferRecycler.allocCharBuffer(3, minSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void releaseReadIOBuffer(byte[] buf) {
/* 290 */     if (buf != null) {
/*     */ 
/*     */       
/* 293 */       _verifyRelease(buf, this._readIOBuffer);
/* 294 */       this._readIOBuffer = null;
/* 295 */       this._bufferRecycler.releaseByteBuffer(0, buf);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void releaseWriteEncodingBuffer(byte[] buf) {
/* 300 */     if (buf != null) {
/*     */ 
/*     */       
/* 303 */       _verifyRelease(buf, this._writeEncodingBuffer);
/* 304 */       this._writeEncodingBuffer = null;
/* 305 */       this._bufferRecycler.releaseByteBuffer(1, buf);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void releaseBase64Buffer(byte[] buf) {
/* 310 */     if (buf != null) {
/* 311 */       _verifyRelease(buf, this._base64Buffer);
/* 312 */       this._base64Buffer = null;
/* 313 */       this._bufferRecycler.releaseByteBuffer(3, buf);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void releaseTokenBuffer(char[] buf) {
/* 318 */     if (buf != null) {
/* 319 */       _verifyRelease(buf, this._tokenCBuffer);
/* 320 */       this._tokenCBuffer = null;
/* 321 */       this._bufferRecycler.releaseCharBuffer(0, buf);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void releaseConcatBuffer(char[] buf) {
/* 326 */     if (buf != null) {
/*     */       
/* 328 */       _verifyRelease(buf, this._concatCBuffer);
/* 329 */       this._concatCBuffer = null;
/* 330 */       this._bufferRecycler.releaseCharBuffer(1, buf);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void releaseNameCopyBuffer(char[] buf) {
/* 335 */     if (buf != null) {
/*     */       
/* 337 */       _verifyRelease(buf, this._nameCopyBuffer);
/* 338 */       this._nameCopyBuffer = null;
/* 339 */       this._bufferRecycler.releaseCharBuffer(3, buf);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void _verifyAlloc(Object buffer) {
/* 350 */     if (buffer != null) throw new IllegalStateException("Trying to call same allocXxx() method second time");
/*     */   
/*     */   }
/*     */   
/*     */   protected final void _verifyRelease(byte[] toRelease, byte[] src) {
/* 355 */     if (toRelease != src && toRelease.length < src.length) throw wrongBuf();
/*     */   
/*     */   }
/*     */   
/*     */   protected final void _verifyRelease(char[] toRelease, char[] src) {
/* 360 */     if (toRelease != src && toRelease.length < src.length) throw wrongBuf();
/*     */   
/*     */   }
/*     */   
/*     */   private IllegalArgumentException wrongBuf() {
/* 365 */     return new IllegalArgumentException("Trying to release buffer smaller than original");
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/io/IOContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */