/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeanDefinitionDefaults
/*     */ {
/*     */   @Nullable
/*     */   private Boolean lazyInit;
/*  35 */   private int autowireMode = 0;
/*     */   
/*  37 */   private int dependencyCheck = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String initMethodName;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String destroyMethodName;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLazyInit(boolean lazyInit) {
/*  53 */     this.lazyInit = Boolean.valueOf(lazyInit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLazyInit() {
/*  62 */     return (this.lazyInit != null && this.lazyInit.booleanValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Boolean getLazyInit() {
/*  73 */     return this.lazyInit;
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
/*     */   public void setAutowireMode(int autowireMode) {
/*  86 */     this.autowireMode = autowireMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAutowireMode() {
/*  93 */     return this.autowireMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDependencyCheck(int dependencyCheck) {
/* 103 */     this.dependencyCheck = dependencyCheck;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDependencyCheck() {
/* 110 */     return this.dependencyCheck;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInitMethodName(@Nullable String initMethodName) {
/* 121 */     this.initMethodName = StringUtils.hasText(initMethodName) ? initMethodName : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getInitMethodName() {
/* 129 */     return this.initMethodName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDestroyMethodName(@Nullable String destroyMethodName) {
/* 140 */     this.destroyMethodName = StringUtils.hasText(destroyMethodName) ? destroyMethodName : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getDestroyMethodName() {
/* 148 */     return this.destroyMethodName;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/BeanDefinitionDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */