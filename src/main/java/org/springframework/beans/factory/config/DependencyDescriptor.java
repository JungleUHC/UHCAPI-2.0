/*     */ package org.springframework.beans.factory.config;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import kotlin.reflect.KProperty;
/*     */ import kotlin.reflect.jvm.ReflectJvmMapping;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.beans.factory.InjectionPoint;
/*     */ import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
/*     */ import org.springframework.core.KotlinDetector;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.ParameterNameDiscoverer;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.convert.TypeDescriptor;
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
/*     */ public class DependencyDescriptor
/*     */   extends InjectionPoint
/*     */   implements Serializable
/*     */ {
/*     */   private final Class<?> declaringClass;
/*     */   @Nullable
/*     */   private String methodName;
/*     */   @Nullable
/*     */   private Class<?>[] parameterTypes;
/*     */   private int parameterIndex;
/*     */   @Nullable
/*     */   private String fieldName;
/*     */   private final boolean required;
/*     */   private final boolean eager;
/*  72 */   private int nestingLevel = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Class<?> containingClass;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private volatile transient ResolvableType resolvableType;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private volatile transient TypeDescriptor typeDescriptor;
/*     */ 
/*     */ 
/*     */   
/*     */   public DependencyDescriptor(MethodParameter methodParameter, boolean required) {
/*  91 */     this(methodParameter, required, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DependencyDescriptor(MethodParameter methodParameter, boolean required, boolean eager) {
/* 102 */     super(methodParameter);
/*     */     
/* 104 */     this.declaringClass = methodParameter.getDeclaringClass();
/* 105 */     if (methodParameter.getMethod() != null) {
/* 106 */       this.methodName = methodParameter.getMethod().getName();
/*     */     }
/* 108 */     this.parameterTypes = methodParameter.getExecutable().getParameterTypes();
/* 109 */     this.parameterIndex = methodParameter.getParameterIndex();
/* 110 */     this.containingClass = methodParameter.getContainingClass();
/* 111 */     this.required = required;
/* 112 */     this.eager = eager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DependencyDescriptor(Field field, boolean required) {
/* 122 */     this(field, required, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DependencyDescriptor(Field field, boolean required, boolean eager) {
/* 133 */     super(field);
/*     */     
/* 135 */     this.declaringClass = field.getDeclaringClass();
/* 136 */     this.fieldName = field.getName();
/* 137 */     this.required = required;
/* 138 */     this.eager = eager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DependencyDescriptor(DependencyDescriptor original) {
/* 146 */     super(original);
/*     */     
/* 148 */     this.declaringClass = original.declaringClass;
/* 149 */     this.methodName = original.methodName;
/* 150 */     this.parameterTypes = original.parameterTypes;
/* 151 */     this.parameterIndex = original.parameterIndex;
/* 152 */     this.fieldName = original.fieldName;
/* 153 */     this.containingClass = original.containingClass;
/* 154 */     this.required = original.required;
/* 155 */     this.eager = original.eager;
/* 156 */     this.nestingLevel = original.nestingLevel;
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
/*     */   public boolean isRequired() {
/* 168 */     if (!this.required) {
/* 169 */       return false;
/*     */     }
/*     */     
/* 172 */     if (this.field != null) {
/* 173 */       return (this.field.getType() != Optional.class && !hasNullableAnnotation() && (
/* 174 */         !KotlinDetector.isKotlinReflectPresent() || 
/* 175 */         !KotlinDetector.isKotlinType(this.field.getDeclaringClass()) || 
/* 176 */         !KotlinDelegate.isNullable(this.field)));
/*     */     }
/*     */     
/* 179 */     return !obtainMethodParameter().isOptional();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasNullableAnnotation() {
/* 189 */     for (Annotation ann : getAnnotations()) {
/* 190 */       if ("Nullable".equals(ann.annotationType().getSimpleName())) {
/* 191 */         return true;
/*     */       }
/*     */     } 
/* 194 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEager() {
/* 202 */     return this.eager;
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
/*     */   
/*     */   @Nullable
/*     */   public Object resolveNotUnique(ResolvableType type, Map<String, Object> matchingBeans) throws BeansException {
/* 220 */     throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
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
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public Object resolveNotUnique(Class<?> type, Map<String, Object> matchingBeans) throws BeansException {
/* 240 */     throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
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
/*     */   @Nullable
/*     */   public Object resolveShortcut(BeanFactory beanFactory) throws BeansException {
/* 257 */     return null;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) throws BeansException {
/* 276 */     return beanFactory.getBean(beanName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increaseNestingLevel() {
/* 284 */     this.nestingLevel++;
/* 285 */     this.resolvableType = null;
/* 286 */     if (this.methodParameter != null) {
/* 287 */       this.methodParameter = this.methodParameter.nested();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContainingClass(Class<?> containingClass) {
/* 298 */     this.containingClass = containingClass;
/* 299 */     this.resolvableType = null;
/* 300 */     if (this.methodParameter != null) {
/* 301 */       this.methodParameter = this.methodParameter.withContainingClass(containingClass);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResolvableType getResolvableType() {
/* 310 */     ResolvableType resolvableType = this.resolvableType;
/* 311 */     if (resolvableType == null) {
/*     */ 
/*     */       
/* 314 */       resolvableType = (this.field != null) ? ResolvableType.forField(this.field, this.nestingLevel, this.containingClass) : ResolvableType.forMethodParameter(obtainMethodParameter());
/* 315 */       this.resolvableType = resolvableType;
/*     */     } 
/* 317 */     return resolvableType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeDescriptor getTypeDescriptor() {
/* 325 */     TypeDescriptor typeDescriptor = this.typeDescriptor;
/* 326 */     if (typeDescriptor == null) {
/*     */ 
/*     */       
/* 329 */       typeDescriptor = (this.field != null) ? new TypeDescriptor(getResolvableType(), getDependencyType(), getAnnotations()) : new TypeDescriptor(obtainMethodParameter());
/* 330 */       this.typeDescriptor = typeDescriptor;
/*     */     } 
/* 332 */     return typeDescriptor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean fallbackMatchAllowed() {
/* 343 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DependencyDescriptor forFallbackMatch() {
/* 352 */     return new DependencyDescriptor(this)
/*     */       {
/*     */         public boolean fallbackMatchAllowed() {
/* 355 */           return true;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void initParameterNameDiscovery(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
/* 367 */     if (this.methodParameter != null) {
/* 368 */       this.methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getDependencyName() {
/* 378 */     return (this.field != null) ? this.field.getName() : obtainMethodParameter().getParameterName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getDependencyType() {
/* 386 */     if (this.field != null) {
/* 387 */       if (this.nestingLevel > 1) {
/* 388 */         Type type = this.field.getGenericType();
/* 389 */         for (int i = 2; i <= this.nestingLevel; i++) {
/* 390 */           if (type instanceof ParameterizedType) {
/* 391 */             Type[] args = ((ParameterizedType)type).getActualTypeArguments();
/* 392 */             type = args[args.length - 1];
/*     */           } 
/*     */         } 
/* 395 */         if (type instanceof Class) {
/* 396 */           return (Class)type;
/*     */         }
/* 398 */         if (type instanceof ParameterizedType) {
/* 399 */           Type arg = ((ParameterizedType)type).getRawType();
/* 400 */           if (arg instanceof Class) {
/* 401 */             return (Class)arg;
/*     */           }
/*     */         } 
/* 404 */         return Object.class;
/*     */       } 
/*     */       
/* 407 */       return this.field.getType();
/*     */     } 
/*     */ 
/*     */     
/* 411 */     return obtainMethodParameter().getNestedParameterType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 418 */     if (this == other) {
/* 419 */       return true;
/*     */     }
/* 421 */     if (!super.equals(other)) {
/* 422 */       return false;
/*     */     }
/* 424 */     DependencyDescriptor otherDesc = (DependencyDescriptor)other;
/* 425 */     return (this.required == otherDesc.required && this.eager == otherDesc.eager && this.nestingLevel == otherDesc.nestingLevel && this.containingClass == otherDesc.containingClass);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 431 */     return 31 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.containingClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
/* 441 */     ois.defaultReadObject();
/*     */ 
/*     */     
/*     */     try {
/* 445 */       if (this.fieldName != null) {
/* 446 */         this.field = this.declaringClass.getDeclaredField(this.fieldName);
/*     */       } else {
/*     */         
/* 449 */         if (this.methodName != null) {
/* 450 */           this
/* 451 */             .methodParameter = new MethodParameter(this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
/*     */         } else {
/*     */           
/* 454 */           this
/* 455 */             .methodParameter = new MethodParameter(this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
/*     */         } 
/* 457 */         for (int i = 1; i < this.nestingLevel; i++) {
/* 458 */           this.methodParameter = this.methodParameter.nested();
/*     */         }
/*     */       }
/*     */     
/* 462 */     } catch (Throwable ex) {
/* 463 */       throw new IllegalStateException("Could not find original class structure", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class KotlinDelegate
/*     */   {
/*     */     public static boolean isNullable(Field field) {
/* 477 */       KProperty<?> property = ReflectJvmMapping.getKotlinProperty(field);
/* 478 */       return (property != null && property.getReturnType().isMarkedNullable());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/DependencyDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */