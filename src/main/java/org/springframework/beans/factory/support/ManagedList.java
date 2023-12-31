/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
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
/*     */ public class ManagedList<E>
/*     */   extends ArrayList<E>
/*     */   implements Mergeable, BeanMetadataElement
/*     */ {
/*     */   @Nullable
/*     */   private Object source;
/*     */   @Nullable
/*     */   private String elementTypeName;
/*     */   private boolean mergeEnabled;
/*     */   
/*     */   public ManagedList() {}
/*     */   
/*     */   public ManagedList(int initialCapacity) {
/*  53 */     super(initialCapacity);
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
/*     */   public static <E> ManagedList<E> of(E... elements) {
/*  66 */     ManagedList<E> list = new ManagedList<>();
/*  67 */     list.addAll(Arrays.asList(elements));
/*  68 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSource(@Nullable Object source) {
/*  76 */     this.source = source;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getSource() {
/*  82 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setElementTypeName(String elementTypeName) {
/*  89 */     this.elementTypeName = elementTypeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getElementTypeName() {
/*  97 */     return this.elementTypeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMergeEnabled(boolean mergeEnabled) {
/* 105 */     this.mergeEnabled = mergeEnabled;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMergeEnabled() {
/* 110 */     return this.mergeEnabled;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<E> merge(@Nullable Object parent) {
/* 116 */     if (!this.mergeEnabled) {
/* 117 */       throw new IllegalStateException("Not allowed to merge when the 'mergeEnabled' property is set to 'false'");
/*     */     }
/* 119 */     if (parent == null) {
/* 120 */       return this;
/*     */     }
/* 122 */     if (!(parent instanceof List)) {
/* 123 */       throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
/*     */     }
/* 125 */     List<E> merged = new ManagedList();
/* 126 */     merged.addAll((List)parent);
/* 127 */     merged.addAll(this);
/* 128 */     return merged;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/ManagedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */