/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import org.springframework.beans.factory.config.BeanDefinition;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GenericBeanDefinition
/*     */   extends AbstractBeanDefinition
/*     */ {
/*     */   @Nullable
/*     */   private String parentName;
/*     */   
/*     */   public GenericBeanDefinition() {}
/*     */   
/*     */   public GenericBeanDefinition(BeanDefinition original) {
/*  65 */     super(original);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParentName(@Nullable String parentName) {
/*  71 */     this.parentName = parentName;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getParentName() {
/*  77 */     return this.parentName;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractBeanDefinition cloneBeanDefinition() {
/*  83 */     return new GenericBeanDefinition(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/*  88 */     if (this == other) {
/*  89 */       return true;
/*     */     }
/*  91 */     if (!(other instanceof GenericBeanDefinition)) {
/*  92 */       return false;
/*     */     }
/*  94 */     GenericBeanDefinition that = (GenericBeanDefinition)other;
/*  95 */     return (ObjectUtils.nullSafeEquals(this.parentName, that.parentName) && super.equals(other));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 100 */     if (this.parentName != null) {
/* 101 */       return "Generic bean with parent '" + this.parentName + "': " + super.toString();
/*     */     }
/* 103 */     return "Generic bean: " + super.toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/GenericBeanDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */