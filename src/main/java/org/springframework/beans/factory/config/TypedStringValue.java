/*     */ package org.springframework.beans.factory.config;
/*     */ 
/*     */ import org.springframework.beans.BeanMetadataElement;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
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
/*     */ public class TypedStringValue
/*     */   implements BeanMetadataElement
/*     */ {
/*     */   @Nullable
/*     */   private String value;
/*     */   @Nullable
/*     */   private volatile Object targetType;
/*     */   @Nullable
/*     */   private Object source;
/*     */   @Nullable
/*     */   private String specifiedTypeName;
/*     */   private volatile boolean dynamic;
/*     */   
/*     */   public TypedStringValue(@Nullable String value) {
/*  60 */     setValue(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypedStringValue(@Nullable String value, Class<?> targetType) {
/*  70 */     setValue(value);
/*  71 */     setTargetType(targetType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypedStringValue(@Nullable String value, String targetTypeName) {
/*  81 */     setValue(value);
/*  82 */     setTargetTypeName(targetTypeName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(@Nullable String value) {
/*  92 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getValue() {
/* 100 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTargetType(Class<?> targetType) {
/* 109 */     Assert.notNull(targetType, "'targetType' must not be null");
/* 110 */     this.targetType = targetType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getTargetType() {
/* 117 */     Object targetTypeValue = this.targetType;
/* 118 */     if (!(targetTypeValue instanceof Class)) {
/* 119 */       throw new IllegalStateException("Typed String value does not carry a resolved target type");
/*     */     }
/* 121 */     return (Class)targetTypeValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTargetTypeName(@Nullable String targetTypeName) {
/* 128 */     this.targetType = targetTypeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getTargetTypeName() {
/* 136 */     Object targetTypeValue = this.targetType;
/* 137 */     if (targetTypeValue instanceof Class) {
/* 138 */       return ((Class)targetTypeValue).getName();
/*     */     }
/*     */     
/* 141 */     return (String)targetTypeValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasTargetType() {
/* 149 */     return this.targetType instanceof Class;
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
/*     */   @Nullable
/*     */   public Class<?> resolveTargetType(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
/* 162 */     String typeName = getTargetTypeName();
/* 163 */     if (typeName == null) {
/* 164 */       return null;
/*     */     }
/* 166 */     Class<?> resolvedClass = ClassUtils.forName(typeName, classLoader);
/* 167 */     this.targetType = resolvedClass;
/* 168 */     return resolvedClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSource(@Nullable Object source) {
/* 177 */     this.source = source;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getSource() {
/* 183 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSpecifiedTypeName(@Nullable String specifiedTypeName) {
/* 190 */     this.specifiedTypeName = specifiedTypeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getSpecifiedTypeName() {
/* 198 */     return this.specifiedTypeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDynamic() {
/* 206 */     this.dynamic = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDynamic() {
/* 213 */     return this.dynamic;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 219 */     if (this == other) {
/* 220 */       return true;
/*     */     }
/* 222 */     if (!(other instanceof TypedStringValue)) {
/* 223 */       return false;
/*     */     }
/* 225 */     TypedStringValue otherValue = (TypedStringValue)other;
/* 226 */     return (ObjectUtils.nullSafeEquals(this.value, otherValue.value) && 
/* 227 */       ObjectUtils.nullSafeEquals(this.targetType, otherValue.targetType));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 232 */     return ObjectUtils.nullSafeHashCode(this.value) * 29 + ObjectUtils.nullSafeHashCode(this.targetType);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 237 */     return "TypedStringValue: value [" + this.value + "], target type [" + this.targetType + "]";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/TypedStringValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */