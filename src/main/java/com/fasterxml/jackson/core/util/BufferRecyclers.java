/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.JsonStringEncoder;
/*     */ import java.lang.ref.SoftReference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferRecyclers
/*     */ {
/*     */   public static final String SYSTEM_PROPERTY_TRACK_REUSABLE_BUFFERS = "com.fasterxml.jackson.core.util.BufferRecyclers.trackReusableBuffers";
/*     */   private static final ThreadLocalBufferManager _bufferRecyclerTracker;
/*     */   
/*     */   static {
/*  38 */     boolean trackReusableBuffers = false;
/*     */     try {
/*  40 */       trackReusableBuffers = "true".equals(System.getProperty("com.fasterxml.jackson.core.util.BufferRecyclers.trackReusableBuffers"));
/*  41 */     } catch (SecurityException securityException) {}
/*     */     
/*  43 */     _bufferRecyclerTracker = trackReusableBuffers ? ThreadLocalBufferManager.instance() : null;
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
/*  57 */   protected static final ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef = new ThreadLocal<SoftReference<BufferRecycler>>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferRecycler getBufferRecycler() {
/*  67 */     SoftReference<BufferRecycler> ref = _recyclerRef.get();
/*  68 */     BufferRecycler br = (ref == null) ? null : ref.get();
/*     */     
/*  70 */     if (br == null) {
/*  71 */       br = new BufferRecycler();
/*  72 */       if (_bufferRecyclerTracker != null) {
/*  73 */         ref = _bufferRecyclerTracker.wrapAndTrack(br);
/*     */       } else {
/*  75 */         ref = new SoftReference<BufferRecycler>(br);
/*     */       } 
/*  77 */       _recyclerRef.set(ref);
/*     */     } 
/*  79 */     return br;
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
/*     */   public static int releaseBuffers() {
/*  96 */     if (_bufferRecyclerTracker != null) {
/*  97 */       return _bufferRecyclerTracker.releaseBuffers();
/*     */     }
/*  99 */     return -1;
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
/*     */   @Deprecated
/*     */   public static JsonStringEncoder getJsonStringEncoder() {
/* 119 */     return JsonStringEncoder.getInstance();
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
/*     */   @Deprecated
/*     */   public static byte[] encodeAsUTF8(String text) {
/* 134 */     return JsonStringEncoder.getInstance().encodeAsUTF8(text);
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
/*     */   @Deprecated
/*     */   public static char[] quoteAsJsonText(String rawText) {
/* 150 */     return JsonStringEncoder.getInstance().quoteAsString(rawText);
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
/*     */   @Deprecated
/*     */   public static void quoteAsJsonText(CharSequence input, StringBuilder output) {
/* 165 */     JsonStringEncoder.getInstance().quoteAsString(input, output);
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
/*     */   @Deprecated
/*     */   public static byte[] quoteAsJsonUTF8(String rawText) {
/* 181 */     return JsonStringEncoder.getInstance().quoteAsUTF8(rawText);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/BufferRecyclers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */