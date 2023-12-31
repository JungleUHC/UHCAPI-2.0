/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JacksonFeatureSet<F extends JacksonFeature>
/*     */ {
/*     */   protected int _enabled;
/*     */   
/*     */   protected JacksonFeatureSet(int bitmask) {
/*  23 */     this._enabled = bitmask;
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
/*     */   public static <F extends JacksonFeature> JacksonFeatureSet<F> fromDefaults(F[] allFeatures) {
/*  39 */     if (allFeatures.length > 31) {
/*  40 */       String desc = allFeatures[0].getClass().getName();
/*  41 */       throw new IllegalArgumentException(String.format("Can not use type `%s` with JacksonFeatureSet: too many entries (%d > 31)", new Object[] { desc, 
/*     */               
/*  43 */               Integer.valueOf(allFeatures.length) }));
/*     */     } 
/*     */     
/*  46 */     int flags = 0;
/*  47 */     for (F f : allFeatures) {
/*  48 */       if (f.enabledByDefault()) {
/*  49 */         flags |= f.getMask();
/*     */       }
/*     */     } 
/*  52 */     return new JacksonFeatureSet<F>(flags);
/*     */   }
/*     */   
/*     */   public static <F extends JacksonFeature> JacksonFeatureSet<F> fromBitmask(int bitmask) {
/*  56 */     return new JacksonFeatureSet<F>(bitmask);
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
/*     */   public JacksonFeatureSet<F> with(F feature) {
/*  69 */     int newMask = this._enabled | feature.getMask();
/*  70 */     return (newMask == this._enabled) ? this : new JacksonFeatureSet(newMask);
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
/*     */   public JacksonFeatureSet<F> without(F feature) {
/*  83 */     int newMask = this._enabled & (feature.getMask() ^ 0xFFFFFFFF);
/*  84 */     return (newMask == this._enabled) ? this : new JacksonFeatureSet(newMask);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEnabled(F feature) {
/*  95 */     return ((feature.getMask() & this._enabled) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int asBitmask() {
/* 104 */     return this._enabled;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/JacksonFeatureSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */