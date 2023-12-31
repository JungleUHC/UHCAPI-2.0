/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
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
/*     */ public class LookupOverride
/*     */   extends MethodOverride
/*     */ {
/*     */   @Nullable
/*     */   private final String beanName;
/*     */   @Nullable
/*     */   private Method method;
/*     */   
/*     */   public LookupOverride(String methodName, @Nullable String beanName) {
/*  58 */     super(methodName);
/*  59 */     this.beanName = beanName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LookupOverride(Method method, @Nullable String beanName) {
/*  69 */     super(method.getName());
/*  70 */     this.method = method;
/*  71 */     this.beanName = beanName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getBeanName() {
/*  80 */     return this.beanName;
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
/*     */   public boolean matches(Method method) {
/*  93 */     if (this.method != null) {
/*  94 */       return method.equals(this.method);
/*     */     }
/*     */     
/*  97 */     return (method.getName().equals(getMethodName()) && (!isOverloaded() || 
/*  98 */       Modifier.isAbstract(method.getModifiers()) || method.getParameterCount() == 0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 105 */     if (!(other instanceof LookupOverride) || !super.equals(other)) {
/* 106 */       return false;
/*     */     }
/* 108 */     LookupOverride that = (LookupOverride)other;
/* 109 */     return (ObjectUtils.nullSafeEquals(this.method, that.method) && 
/* 110 */       ObjectUtils.nullSafeEquals(this.beanName, that.beanName));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 115 */     return 29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.beanName);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 120 */     return "LookupOverride for method '" + getMethodName() + "'";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/LookupOverride.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */