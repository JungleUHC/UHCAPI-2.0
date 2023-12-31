/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import org.springframework.beans.BeanMetadataElement;
/*     */ import org.springframework.beans.Mergeable;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ManagedMap<K, V>
/*     */   extends LinkedHashMap<K, V>
/*     */   implements Mergeable, BeanMetadataElement
/*     */ {
/*     */   @Nullable
/*     */   private Object source;
/*     */   @Nullable
/*     */   private String keyTypeName;
/*     */   @Nullable
/*     */   private String valueTypeName;
/*     */   private boolean mergeEnabled;
/*     */   
/*     */   public ManagedMap() {}
/*     */   
/*     */   public ManagedMap(int initialCapacity) {
/*  56 */     super(initialCapacity);
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
/*     */   @SafeVarargs
/*     */   public static <K, V> ManagedMap<K, V> ofEntries(Map.Entry<? extends K, ? extends V>... entries) {
/*  73 */     ManagedMap<K, V> map = new ManagedMap<>();
/*  74 */     for (Map.Entry<? extends K, ? extends V> entry : entries) {
/*  75 */       map.put(entry.getKey(), entry.getValue());
/*     */     }
/*  77 */     return map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSource(@Nullable Object source) {
/*  85 */     this.source = source;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getSource() {
/*  91 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKeyTypeName(@Nullable String keyTypeName) {
/*  98 */     this.keyTypeName = keyTypeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getKeyTypeName() {
/* 106 */     return this.keyTypeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValueTypeName(@Nullable String valueTypeName) {
/* 113 */     this.valueTypeName = valueTypeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getValueTypeName() {
/* 121 */     return this.valueTypeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMergeEnabled(boolean mergeEnabled) {
/* 129 */     this.mergeEnabled = mergeEnabled;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMergeEnabled() {
/* 134 */     return this.mergeEnabled;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object merge(@Nullable Object parent) {
/* 140 */     if (!this.mergeEnabled) {
/* 141 */       throw new IllegalStateException("Not allowed to merge when the 'mergeEnabled' property is set to 'false'");
/*     */     }
/* 143 */     if (parent == null) {
/* 144 */       return this;
/*     */     }
/* 146 */     if (!(parent instanceof Map)) {
/* 147 */       throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
/*     */     }
/* 149 */     Map<K, V> merged = new ManagedMap();
/* 150 */     merged.putAll((Map<? extends K, ? extends V>)parent);
/* 151 */     merged.putAll(this);
/* 152 */     return merged;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/ManagedMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */