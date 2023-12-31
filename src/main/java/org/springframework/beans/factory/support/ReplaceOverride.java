/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ public class ReplaceOverride
/*     */   extends MethodOverride
/*     */ {
/*     */   private final String methodReplacerBeanName;
/*  42 */   private final List<String> typeIdentifiers = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReplaceOverride(String methodName, String methodReplacerBeanName) {
/*  51 */     super(methodName);
/*  52 */     Assert.notNull(methodReplacerBeanName, "Method replacer bean name must not be null");
/*  53 */     this.methodReplacerBeanName = methodReplacerBeanName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMethodReplacerBeanName() {
/*  61 */     return this.methodReplacerBeanName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTypeIdentifier(String identifier) {
/*  70 */     this.typeIdentifiers.add(identifier);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(Method method) {
/*  76 */     if (!method.getName().equals(getMethodName())) {
/*  77 */       return false;
/*     */     }
/*  79 */     if (!isOverloaded())
/*     */     {
/*  81 */       return true;
/*     */     }
/*     */     
/*  84 */     if (this.typeIdentifiers.size() != method.getParameterCount()) {
/*  85 */       return false;
/*     */     }
/*  87 */     Class<?>[] parameterTypes = method.getParameterTypes();
/*  88 */     for (int i = 0; i < this.typeIdentifiers.size(); i++) {
/*  89 */       String identifier = this.typeIdentifiers.get(i);
/*  90 */       if (!parameterTypes[i].getName().contains(identifier)) {
/*  91 */         return false;
/*     */       }
/*     */     } 
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 100 */     if (!(other instanceof ReplaceOverride) || !super.equals(other)) {
/* 101 */       return false;
/*     */     }
/* 103 */     ReplaceOverride that = (ReplaceOverride)other;
/* 104 */     return (ObjectUtils.nullSafeEquals(this.methodReplacerBeanName, that.methodReplacerBeanName) && 
/* 105 */       ObjectUtils.nullSafeEquals(this.typeIdentifiers, that.typeIdentifiers));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 110 */     int hashCode = super.hashCode();
/* 111 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.methodReplacerBeanName);
/* 112 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.typeIdentifiers);
/* 113 */     return hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 118 */     return "Replace override for method '" + getMethodName() + "'";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/ReplaceOverride.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */