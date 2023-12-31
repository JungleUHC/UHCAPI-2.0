/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Executable;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import java.util.function.Supplier;
/*     */ import org.springframework.beans.MutablePropertyValues;
/*     */ import org.springframework.beans.factory.config.BeanDefinition;
/*     */ import org.springframework.beans.factory.config.BeanDefinitionHolder;
/*     */ import org.springframework.beans.factory.config.ConstructorArgumentValues;
/*     */ import org.springframework.core.ResolvableType;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RootBeanDefinition
/*     */   extends AbstractBeanDefinition
/*     */ {
/*     */   @Nullable
/*     */   private BeanDefinitionHolder decoratedDefinition;
/*     */   @Nullable
/*     */   private AnnotatedElement qualifiedElement;
/*     */   volatile boolean stale;
/*     */   boolean allowCaching = true;
/*     */   boolean isFactoryMethodUnique;
/*     */   @Nullable
/*     */   volatile ResolvableType targetType;
/*     */   @Nullable
/*     */   volatile Class<?> resolvedTargetType;
/*     */   @Nullable
/*     */   volatile Boolean isFactoryBean;
/*     */   @Nullable
/*     */   volatile ResolvableType factoryMethodReturnType;
/*     */   @Nullable
/*     */   volatile Method factoryMethodToIntrospect;
/*     */   @Nullable
/*     */   volatile String resolvedDestroyMethodName;
/*  96 */   final Object constructorArgumentLock = new Object();
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   Executable resolvedConstructorOrFactoryMethod;
/*     */ 
/*     */   
/*     */   boolean constructorArgumentsResolved = false;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   Object[] resolvedConstructorArguments;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   Object[] preparedConstructorArguments;
/*     */ 
/*     */   
/* 114 */   final Object postProcessingLock = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean postProcessed = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   volatile Boolean beforeInstantiationResolved;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Set<Member> externallyManagedConfigMembers;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Set<String> externallyManagedInitMethods;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Set<String> externallyManagedDestroyMethods;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RootBeanDefinition(@Nullable Class<?> beanClass) {
/* 152 */     setBeanClass(beanClass);
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
/*     */   public <T> RootBeanDefinition(@Nullable Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
/* 166 */     setBeanClass(beanClass);
/* 167 */     setInstanceSupplier(instanceSupplier);
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
/*     */   public <T> RootBeanDefinition(@Nullable Class<T> beanClass, String scope, @Nullable Supplier<T> instanceSupplier) {
/* 182 */     setBeanClass(beanClass);
/* 183 */     setScope(scope);
/* 184 */     setInstanceSupplier(instanceSupplier);
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
/*     */   public RootBeanDefinition(@Nullable Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
/* 197 */     setBeanClass(beanClass);
/* 198 */     setAutowireMode(autowireMode);
/* 199 */     if (dependencyCheck && getResolvedAutowireMode() != 3) {
/* 200 */       setDependencyCheck(1);
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
/*     */ 
/*     */   
/*     */   public RootBeanDefinition(@Nullable Class<?> beanClass, @Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
/* 214 */     super(cargs, pvs);
/* 215 */     setBeanClass(beanClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RootBeanDefinition(String beanClassName) {
/* 225 */     setBeanClassName(beanClassName);
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
/*     */   public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
/* 237 */     super(cargs, pvs);
/* 238 */     setBeanClassName(beanClassName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RootBeanDefinition(RootBeanDefinition original) {
/* 247 */     super(original);
/* 248 */     this.decoratedDefinition = original.decoratedDefinition;
/* 249 */     this.qualifiedElement = original.qualifiedElement;
/* 250 */     this.allowCaching = original.allowCaching;
/* 251 */     this.isFactoryMethodUnique = original.isFactoryMethodUnique;
/* 252 */     this.targetType = original.targetType;
/* 253 */     this.factoryMethodToIntrospect = original.factoryMethodToIntrospect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   RootBeanDefinition(BeanDefinition original) {
/* 262 */     super(original);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getParentName() {
/* 268 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setParentName(@Nullable String parentName) {
/* 273 */     if (parentName != null) {
/* 274 */       throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDecoratedDefinition(@Nullable BeanDefinitionHolder decoratedDefinition) {
/* 282 */     this.decoratedDefinition = decoratedDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public BeanDefinitionHolder getDecoratedDefinition() {
/* 290 */     return this.decoratedDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setQualifiedElement(@Nullable AnnotatedElement qualifiedElement) {
/* 301 */     this.qualifiedElement = qualifiedElement;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public AnnotatedElement getQualifiedElement() {
/* 311 */     return this.qualifiedElement;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTargetType(ResolvableType targetType) {
/* 319 */     this.targetType = targetType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTargetType(@Nullable Class<?> targetType) {
/* 327 */     this.targetType = (targetType != null) ? ResolvableType.forClass(targetType) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Class<?> getTargetType() {
/* 337 */     if (this.resolvedTargetType != null) {
/* 338 */       return this.resolvedTargetType;
/*     */     }
/* 340 */     ResolvableType targetType = this.targetType;
/* 341 */     return (targetType != null) ? targetType.resolve() : null;
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
/*     */   public ResolvableType getResolvableType() {
/* 356 */     ResolvableType targetType = this.targetType;
/* 357 */     if (targetType != null) {
/* 358 */       return targetType;
/*     */     }
/* 360 */     ResolvableType returnType = this.factoryMethodReturnType;
/* 361 */     if (returnType != null) {
/* 362 */       return returnType;
/*     */     }
/* 364 */     Method factoryMethod = this.factoryMethodToIntrospect;
/* 365 */     if (factoryMethod != null) {
/* 366 */       return ResolvableType.forMethodReturnType(factoryMethod);
/*     */     }
/* 368 */     return super.getResolvableType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Constructor<?>[] getPreferredConstructors() {
/* 380 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUniqueFactoryMethodName(String name) {
/* 387 */     Assert.hasText(name, "Factory method name must not be empty");
/* 388 */     setFactoryMethodName(name);
/* 389 */     this.isFactoryMethodUnique = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNonUniqueFactoryMethodName(String name) {
/* 397 */     Assert.hasText(name, "Factory method name must not be empty");
/* 398 */     setFactoryMethodName(name);
/* 399 */     this.isFactoryMethodUnique = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFactoryMethod(Method candidate) {
/* 406 */     return candidate.getName().equals(getFactoryMethodName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setResolvedFactoryMethod(@Nullable Method method) {
/* 415 */     this.factoryMethodToIntrospect = method;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Method getResolvedFactoryMethod() {
/* 424 */     return this.factoryMethodToIntrospect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerExternallyManagedConfigMember(Member configMember) {
/* 431 */     synchronized (this.postProcessingLock) {
/* 432 */       if (this.externallyManagedConfigMembers == null) {
/* 433 */         this.externallyManagedConfigMembers = new LinkedHashSet<>(1);
/*     */       }
/* 435 */       this.externallyManagedConfigMembers.add(configMember);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExternallyManagedConfigMember(Member configMember) {
/* 443 */     synchronized (this.postProcessingLock) {
/* 444 */       return (this.externallyManagedConfigMembers != null && this.externallyManagedConfigMembers
/* 445 */         .contains(configMember));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Member> getExternallyManagedConfigMembers() {
/* 454 */     synchronized (this.postProcessingLock) {
/* 455 */       return (this.externallyManagedConfigMembers != null) ? 
/* 456 */         Collections.<Member>unmodifiableSet(new LinkedHashSet<>(this.externallyManagedConfigMembers)) : 
/* 457 */         Collections.<Member>emptySet();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerExternallyManagedInitMethod(String initMethod) {
/* 473 */     synchronized (this.postProcessingLock) {
/* 474 */       if (this.externallyManagedInitMethods == null) {
/* 475 */         this.externallyManagedInitMethods = new LinkedHashSet<>(1);
/*     */       }
/* 477 */       this.externallyManagedInitMethods.add(initMethod);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExternallyManagedInitMethod(String initMethod) {
/* 488 */     synchronized (this.postProcessingLock) {
/* 489 */       return (this.externallyManagedInitMethods != null && this.externallyManagedInitMethods
/* 490 */         .contains(initMethod));
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
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasAnyExternallyManagedInitMethod(String initMethod) {
/* 505 */     synchronized (this.postProcessingLock) {
/* 506 */       if (isExternallyManagedInitMethod(initMethod)) {
/* 507 */         return true;
/*     */       }
/* 509 */       if (this.externallyManagedInitMethods != null) {
/* 510 */         for (String candidate : this.externallyManagedInitMethods) {
/* 511 */           int indexOfDot = candidate.lastIndexOf('.');
/* 512 */           if (indexOfDot >= 0) {
/* 513 */             String methodName = candidate.substring(indexOfDot + 1);
/* 514 */             if (methodName.equals(initMethod)) {
/* 515 */               return true;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/* 520 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getExternallyManagedInitMethods() {
/* 531 */     synchronized (this.postProcessingLock) {
/* 532 */       return (this.externallyManagedInitMethods != null) ? 
/* 533 */         Collections.<String>unmodifiableSet(new LinkedHashSet<>(this.externallyManagedInitMethods)) : 
/* 534 */         Collections.<String>emptySet();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerExternallyManagedDestroyMethod(String destroyMethod) {
/* 550 */     synchronized (this.postProcessingLock) {
/* 551 */       if (this.externallyManagedDestroyMethods == null) {
/* 552 */         this.externallyManagedDestroyMethods = new LinkedHashSet<>(1);
/*     */       }
/* 554 */       this.externallyManagedDestroyMethods.add(destroyMethod);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
/* 565 */     synchronized (this.postProcessingLock) {
/* 566 */       return (this.externallyManagedDestroyMethods != null && this.externallyManagedDestroyMethods
/* 567 */         .contains(destroyMethod));
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
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasAnyExternallyManagedDestroyMethod(String destroyMethod) {
/* 582 */     synchronized (this.postProcessingLock) {
/* 583 */       if (isExternallyManagedDestroyMethod(destroyMethod)) {
/* 584 */         return true;
/*     */       }
/* 586 */       if (this.externallyManagedDestroyMethods != null) {
/* 587 */         for (String candidate : this.externallyManagedDestroyMethods) {
/* 588 */           int indexOfDot = candidate.lastIndexOf('.');
/* 589 */           if (indexOfDot >= 0) {
/* 590 */             String methodName = candidate.substring(indexOfDot + 1);
/* 591 */             if (methodName.equals(destroyMethod)) {
/* 592 */               return true;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/* 597 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getExternallyManagedDestroyMethods() {
/* 608 */     synchronized (this.postProcessingLock) {
/* 609 */       return (this.externallyManagedDestroyMethods != null) ? 
/* 610 */         Collections.<String>unmodifiableSet(new LinkedHashSet<>(this.externallyManagedDestroyMethods)) : 
/* 611 */         Collections.<String>emptySet();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RootBeanDefinition cloneBeanDefinition() {
/* 618 */     return new RootBeanDefinition(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 623 */     return (this == other || (other instanceof RootBeanDefinition && super.equals(other)));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 628 */     return "Root bean: " + super.toString();
/*     */   }
/*     */   
/*     */   public RootBeanDefinition() {}
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/RootBeanDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */