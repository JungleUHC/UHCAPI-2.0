/*     */ package org.springframework.beans.factory.config;
/*     */ 
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RuntimeBeanReference
/*     */   implements BeanReference
/*     */ {
/*     */   private final String beanName;
/*     */   @Nullable
/*     */   private final Class<?> beanType;
/*     */   private final boolean toParent;
/*     */   @Nullable
/*     */   private Object source;
/*     */   
/*     */   public RuntimeBeanReference(String beanName) {
/*  50 */     this(beanName, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RuntimeBeanReference(String beanName, boolean toParent) {
/*  61 */     Assert.hasText(beanName, "'beanName' must not be empty");
/*  62 */     this.beanName = beanName;
/*  63 */     this.beanType = null;
/*  64 */     this.toParent = toParent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RuntimeBeanReference(Class<?> beanType) {
/*  73 */     this(beanType, false);
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
/*     */   public RuntimeBeanReference(Class<?> beanType, boolean toParent) {
/*  85 */     Assert.notNull(beanType, "'beanType' must not be empty");
/*  86 */     this.beanName = beanType.getName();
/*  87 */     this.beanType = beanType;
/*  88 */     this.toParent = toParent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBeanName() {
/*  99 */     return this.beanName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Class<?> getBeanType() {
/* 108 */     return this.beanType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isToParent() {
/* 115 */     return this.toParent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSource(@Nullable Object source) {
/* 123 */     this.source = source;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getSource() {
/* 129 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 135 */     if (this == other) {
/* 136 */       return true;
/*     */     }
/* 138 */     if (!(other instanceof RuntimeBeanReference)) {
/* 139 */       return false;
/*     */     }
/* 141 */     RuntimeBeanReference that = (RuntimeBeanReference)other;
/* 142 */     return (this.beanName.equals(that.beanName) && this.beanType == that.beanType && this.toParent == that.toParent);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 148 */     int result = this.beanName.hashCode();
/* 149 */     result = 29 * result + (this.toParent ? 1 : 0);
/* 150 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 155 */     return '<' + getBeanName() + '>';
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/RuntimeBeanReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */