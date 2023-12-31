/*     */ package org.springframework.beans;
/*     */ 
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.core.BridgeMethodResolver;
/*     */ import org.springframework.core.GenericTypeResolver;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.ObjectUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class GenericTypeAwarePropertyDescriptor
/*     */   extends PropertyDescriptor
/*     */ {
/*     */   private final Class<?> beanClass;
/*     */   @Nullable
/*     */   private final Method readMethod;
/*     */   @Nullable
/*     */   private final Method writeMethod;
/*     */   @Nullable
/*     */   private volatile Set<Method> ambiguousWriteMethods;
/*     */   @Nullable
/*     */   private MethodParameter writeMethodParameter;
/*     */   @Nullable
/*     */   private Class<?> propertyType;
/*     */   @Nullable
/*     */   private final Class<?> propertyEditorClass;
/*     */   
/*     */   public GenericTypeAwarePropertyDescriptor(Class<?> beanClass, String propertyName, @Nullable Method readMethod, @Nullable Method writeMethod, @Nullable Class<?> propertyEditorClass) throws IntrospectionException {
/*  71 */     super(propertyName, (Method)null, (Method)null);
/*  72 */     this.beanClass = beanClass;
/*     */     
/*  74 */     Method readMethodToUse = (readMethod != null) ? BridgeMethodResolver.findBridgedMethod(readMethod) : null;
/*  75 */     Method writeMethodToUse = (writeMethod != null) ? BridgeMethodResolver.findBridgedMethod(writeMethod) : null;
/*  76 */     if (writeMethodToUse == null && readMethodToUse != null) {
/*     */ 
/*     */ 
/*     */       
/*  80 */       Method candidate = ClassUtils.getMethodIfAvailable(this.beanClass, "set" + 
/*  81 */           StringUtils.capitalize(getName()), (Class[])null);
/*  82 */       if (candidate != null && candidate.getParameterCount() == 1) {
/*  83 */         writeMethodToUse = candidate;
/*     */       }
/*     */     } 
/*  86 */     this.readMethod = readMethodToUse;
/*  87 */     this.writeMethod = writeMethodToUse;
/*     */     
/*  89 */     if (this.writeMethod != null) {
/*  90 */       if (this.readMethod == null) {
/*     */ 
/*     */ 
/*     */         
/*  94 */         Set<Method> ambiguousCandidates = new HashSet<>();
/*  95 */         for (Method method : beanClass.getMethods()) {
/*  96 */           if (method.getName().equals(writeMethodToUse.getName()) && 
/*  97 */             !method.equals(writeMethodToUse) && !method.isBridge() && method
/*  98 */             .getParameterCount() == writeMethodToUse.getParameterCount()) {
/*  99 */             ambiguousCandidates.add(method);
/*     */           }
/*     */         } 
/* 102 */         if (!ambiguousCandidates.isEmpty()) {
/* 103 */           this.ambiguousWriteMethods = ambiguousCandidates;
/*     */         }
/*     */       } 
/* 106 */       this.writeMethodParameter = (new MethodParameter(this.writeMethod, 0)).withContainingClass(this.beanClass);
/*     */     } 
/*     */     
/* 109 */     if (this.readMethod != null) {
/* 110 */       this.propertyType = GenericTypeResolver.resolveReturnType(this.readMethod, this.beanClass);
/*     */     }
/* 112 */     else if (this.writeMethodParameter != null) {
/* 113 */       this.propertyType = this.writeMethodParameter.getParameterType();
/*     */     } 
/*     */     
/* 116 */     this.propertyEditorClass = propertyEditorClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getBeanClass() {
/* 121 */     return this.beanClass;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Method getReadMethod() {
/* 127 */     return this.readMethod;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Method getWriteMethod() {
/* 133 */     return this.writeMethod;
/*     */   }
/*     */   
/*     */   public Method getWriteMethodForActualAccess() {
/* 137 */     Assert.state((this.writeMethod != null), "No write method available");
/* 138 */     Set<Method> ambiguousCandidates = this.ambiguousWriteMethods;
/* 139 */     if (ambiguousCandidates != null) {
/* 140 */       this.ambiguousWriteMethods = null;
/* 141 */       LogFactory.getLog(GenericTypeAwarePropertyDescriptor.class).debug("Non-unique JavaBean property '" + 
/* 142 */           getName() + "' being accessed! Ambiguous write methods found next to actually used [" + this.writeMethod + "]: " + ambiguousCandidates);
/*     */     } 
/*     */     
/* 145 */     return this.writeMethod;
/*     */   }
/*     */   
/*     */   public MethodParameter getWriteMethodParameter() {
/* 149 */     Assert.state((this.writeMethodParameter != null), "No write method available");
/* 150 */     return this.writeMethodParameter;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Class<?> getPropertyType() {
/* 156 */     return this.propertyType;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Class<?> getPropertyEditorClass() {
/* 162 */     return this.propertyEditorClass;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 168 */     if (this == other) {
/* 169 */       return true;
/*     */     }
/* 171 */     if (!(other instanceof GenericTypeAwarePropertyDescriptor)) {
/* 172 */       return false;
/*     */     }
/* 174 */     GenericTypeAwarePropertyDescriptor otherPd = (GenericTypeAwarePropertyDescriptor)other;
/* 175 */     return (getBeanClass().equals(otherPd.getBeanClass()) && PropertyDescriptorUtils.equals(this, otherPd));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 180 */     int hashCode = getBeanClass().hashCode();
/* 181 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(getReadMethod());
/* 182 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(getWriteMethod());
/* 183 */     return hashCode;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/GenericTypeAwarePropertyDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */