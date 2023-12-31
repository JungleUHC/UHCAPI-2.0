/*     */ package org.springframework.beans.factory.annotation;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.factory.BeanCreationException;
/*     */ import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
/*     */ import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
/*     */ import org.springframework.beans.factory.support.RootBeanDefinition;
/*     */ import org.springframework.core.PriorityOrdered;
/*     */ import org.springframework.core.annotation.AnnotationUtils;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InitDestroyAnnotationBeanPostProcessor
/*     */   implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor, PriorityOrdered, Serializable
/*     */ {
/*  83 */   private final transient LifecycleMetadata emptyLifecycleMetadata = new LifecycleMetadata(Object.class, 
/*  84 */       Collections.emptyList(), Collections.emptyList())
/*     */     {
/*     */       public void checkConfigMembers(RootBeanDefinition beanDefinition) {}
/*     */ 
/*     */       
/*     */       public void invokeInitMethods(Object target, String beanName) {}
/*     */ 
/*     */       
/*     */       public void invokeDestroyMethods(Object target, String beanName) {}
/*     */ 
/*     */       
/*     */       public boolean hasDestroyMethods() {
/*  96 */         return false;
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 101 */   protected transient Log logger = LogFactory.getLog(getClass());
/*     */   
/*     */   @Nullable
/*     */   private Class<? extends Annotation> initAnnotationType;
/*     */   
/*     */   @Nullable
/*     */   private Class<? extends Annotation> destroyAnnotationType;
/*     */   
/* 109 */   private int order = Integer.MAX_VALUE;
/*     */   @Nullable
/* 111 */   private final transient Map<Class<?>, LifecycleMetadata> lifecycleMetadataCache = new ConcurrentHashMap<>(256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInitAnnotationType(Class<? extends Annotation> initAnnotationType) {
/* 123 */     this.initAnnotationType = initAnnotationType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDestroyAnnotationType(Class<? extends Annotation> destroyAnnotationType) {
/* 134 */     this.destroyAnnotationType = destroyAnnotationType;
/*     */   }
/*     */   
/*     */   public void setOrder(int order) {
/* 138 */     this.order = order;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOrder() {
/* 143 */     return this.order;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
/* 149 */     LifecycleMetadata metadata = findLifecycleMetadata(beanType);
/* 150 */     metadata.checkConfigMembers(beanDefinition);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
/* 155 */     LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
/*     */     try {
/* 157 */       metadata.invokeInitMethods(bean, beanName);
/*     */     }
/* 159 */     catch (InvocationTargetException ex) {
/* 160 */       throw new BeanCreationException(beanName, "Invocation of init method failed", ex.getTargetException());
/*     */     }
/* 162 */     catch (Throwable ex) {
/* 163 */       throw new BeanCreationException(beanName, "Failed to invoke init method", ex);
/*     */     } 
/* 165 */     return bean;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
/* 170 */     return bean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
/* 175 */     LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
/*     */     try {
/* 177 */       metadata.invokeDestroyMethods(bean, beanName);
/*     */     }
/* 179 */     catch (InvocationTargetException ex) {
/* 180 */       String msg = "Destroy method on bean with name '" + beanName + "' threw an exception";
/* 181 */       if (this.logger.isDebugEnabled()) {
/* 182 */         this.logger.warn(msg, ex.getTargetException());
/*     */       } else {
/*     */         
/* 185 */         this.logger.warn(msg + ": " + ex.getTargetException());
/*     */       }
/*     */     
/* 188 */     } catch (Throwable ex) {
/* 189 */       this.logger.warn("Failed to invoke destroy method on bean with name '" + beanName + "'", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean requiresDestruction(Object bean) {
/* 195 */     return findLifecycleMetadata(bean.getClass()).hasDestroyMethods();
/*     */   }
/*     */ 
/*     */   
/*     */   private LifecycleMetadata findLifecycleMetadata(Class<?> clazz) {
/* 200 */     if (this.lifecycleMetadataCache == null)
/*     */     {
/* 202 */       return buildLifecycleMetadata(clazz);
/*     */     }
/*     */     
/* 205 */     LifecycleMetadata metadata = this.lifecycleMetadataCache.get(clazz);
/* 206 */     if (metadata == null) {
/* 207 */       synchronized (this.lifecycleMetadataCache) {
/* 208 */         metadata = this.lifecycleMetadataCache.get(clazz);
/* 209 */         if (metadata == null) {
/* 210 */           metadata = buildLifecycleMetadata(clazz);
/* 211 */           this.lifecycleMetadataCache.put(clazz, metadata);
/*     */         } 
/* 213 */         return metadata;
/*     */       } 
/*     */     }
/* 216 */     return metadata;
/*     */   }
/*     */   
/*     */   private LifecycleMetadata buildLifecycleMetadata(Class<?> clazz) {
/* 220 */     if (!AnnotationUtils.isCandidateClass(clazz, Arrays.asList((Class<?>[][])new Class[] { this.initAnnotationType, this.destroyAnnotationType }))) {
/* 221 */       return this.emptyLifecycleMetadata;
/*     */     }
/*     */     
/* 224 */     List<LifecycleElement> initMethods = new ArrayList<>();
/* 225 */     List<LifecycleElement> destroyMethods = new ArrayList<>();
/* 226 */     Class<?> targetClass = clazz;
/*     */     
/*     */     do {
/* 229 */       List<LifecycleElement> currInitMethods = new ArrayList<>();
/* 230 */       List<LifecycleElement> currDestroyMethods = new ArrayList<>();
/*     */       
/* 232 */       ReflectionUtils.doWithLocalMethods(targetClass, method -> {
/*     */             if (this.initAnnotationType != null && method.isAnnotationPresent(this.initAnnotationType)) {
/*     */               LifecycleElement element = new LifecycleElement(method);
/*     */               
/*     */               currInitMethods.add(element);
/*     */               if (this.logger.isTraceEnabled()) {
/*     */                 this.logger.trace("Found init method on class [" + clazz.getName() + "]: " + method);
/*     */               }
/*     */             } 
/*     */             if (this.destroyAnnotationType != null && method.isAnnotationPresent(this.destroyAnnotationType)) {
/*     */               currDestroyMethods.add(new LifecycleElement(method));
/*     */               if (this.logger.isTraceEnabled()) {
/*     */                 this.logger.trace("Found destroy method on class [" + clazz.getName() + "]: " + method);
/*     */               }
/*     */             } 
/*     */           });
/* 248 */       initMethods.addAll(0, currInitMethods);
/* 249 */       destroyMethods.addAll(currDestroyMethods);
/* 250 */       targetClass = targetClass.getSuperclass();
/*     */     }
/* 252 */     while (targetClass != null && targetClass != Object.class);
/*     */     
/* 254 */     return (initMethods.isEmpty() && destroyMethods.isEmpty()) ? this.emptyLifecycleMetadata : new LifecycleMetadata(clazz, initMethods, destroyMethods);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
/* 265 */     ois.defaultReadObject();
/*     */ 
/*     */     
/* 268 */     this.logger = LogFactory.getLog(getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class LifecycleMetadata
/*     */   {
/*     */     private final Class<?> targetClass;
/*     */ 
/*     */     
/*     */     private final Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> initMethods;
/*     */ 
/*     */     
/*     */     private final Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> destroyMethods;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private volatile Set<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> checkedInitMethods;
/*     */     
/*     */     @Nullable
/*     */     private volatile Set<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> checkedDestroyMethods;
/*     */ 
/*     */     
/*     */     public LifecycleMetadata(Class<?> targetClass, Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> initMethods, Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> destroyMethods) {
/* 292 */       this.targetClass = targetClass;
/* 293 */       this.initMethods = initMethods;
/* 294 */       this.destroyMethods = destroyMethods;
/*     */     }
/*     */     
/*     */     public void checkConfigMembers(RootBeanDefinition beanDefinition) {
/* 298 */       Set<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> checkedInitMethods = new LinkedHashSet<>(this.initMethods.size());
/* 299 */       for (InitDestroyAnnotationBeanPostProcessor.LifecycleElement element : this.initMethods) {
/* 300 */         String methodIdentifier = element.getIdentifier();
/* 301 */         if (!beanDefinition.isExternallyManagedInitMethod(methodIdentifier)) {
/* 302 */           beanDefinition.registerExternallyManagedInitMethod(methodIdentifier);
/* 303 */           checkedInitMethods.add(element);
/* 304 */           if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
/* 305 */             InitDestroyAnnotationBeanPostProcessor.this.logger.trace("Registered init method on class [" + this.targetClass.getName() + "]: " + methodIdentifier);
/*     */           }
/*     */         } 
/*     */       } 
/* 309 */       Set<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> checkedDestroyMethods = new LinkedHashSet<>(this.destroyMethods.size());
/* 310 */       for (InitDestroyAnnotationBeanPostProcessor.LifecycleElement element : this.destroyMethods) {
/* 311 */         String methodIdentifier = element.getIdentifier();
/* 312 */         if (!beanDefinition.isExternallyManagedDestroyMethod(methodIdentifier)) {
/* 313 */           beanDefinition.registerExternallyManagedDestroyMethod(methodIdentifier);
/* 314 */           checkedDestroyMethods.add(element);
/* 315 */           if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
/* 316 */             InitDestroyAnnotationBeanPostProcessor.this.logger.trace("Registered destroy method on class [" + this.targetClass.getName() + "]: " + methodIdentifier);
/*     */           }
/*     */         } 
/*     */       } 
/* 320 */       this.checkedInitMethods = checkedInitMethods;
/* 321 */       this.checkedDestroyMethods = checkedDestroyMethods;
/*     */     }
/*     */     
/*     */     public void invokeInitMethods(Object target, String beanName) throws Throwable {
/* 325 */       Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> checkedInitMethods = this.checkedInitMethods;
/* 326 */       Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> initMethodsToIterate = (checkedInitMethods != null) ? checkedInitMethods : this.initMethods;
/*     */       
/* 328 */       if (!initMethodsToIterate.isEmpty()) {
/* 329 */         for (InitDestroyAnnotationBeanPostProcessor.LifecycleElement element : initMethodsToIterate) {
/* 330 */           if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
/* 331 */             InitDestroyAnnotationBeanPostProcessor.this.logger.trace("Invoking init method on bean '" + beanName + "': " + element.getMethod());
/*     */           }
/* 333 */           element.invoke(target);
/*     */         } 
/*     */       }
/*     */     }
/*     */     
/*     */     public void invokeDestroyMethods(Object target, String beanName) throws Throwable {
/* 339 */       Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> checkedDestroyMethods = this.checkedDestroyMethods;
/* 340 */       Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> destroyMethodsToUse = (checkedDestroyMethods != null) ? checkedDestroyMethods : this.destroyMethods;
/*     */       
/* 342 */       if (!destroyMethodsToUse.isEmpty()) {
/* 343 */         for (InitDestroyAnnotationBeanPostProcessor.LifecycleElement element : destroyMethodsToUse) {
/* 344 */           if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
/* 345 */             InitDestroyAnnotationBeanPostProcessor.this.logger.trace("Invoking destroy method on bean '" + beanName + "': " + element.getMethod());
/*     */           }
/* 347 */           element.invoke(target);
/*     */         } 
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean hasDestroyMethods() {
/* 353 */       Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> checkedDestroyMethods = this.checkedDestroyMethods;
/* 354 */       Collection<InitDestroyAnnotationBeanPostProcessor.LifecycleElement> destroyMethodsToUse = (checkedDestroyMethods != null) ? checkedDestroyMethods : this.destroyMethods;
/*     */       
/* 356 */       return !destroyMethodsToUse.isEmpty();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class LifecycleElement
/*     */   {
/*     */     private final Method method;
/*     */ 
/*     */     
/*     */     private final String identifier;
/*     */ 
/*     */     
/*     */     public LifecycleElement(Method method) {
/* 371 */       if (method.getParameterCount() != 0) {
/* 372 */         throw new IllegalStateException("Lifecycle method annotation requires a no-arg method: " + method);
/*     */       }
/* 374 */       this.method = method;
/* 375 */       this
/* 376 */         .identifier = Modifier.isPrivate(method.getModifiers()) ? ClassUtils.getQualifiedMethodName(method) : method.getName();
/*     */     }
/*     */     
/*     */     public Method getMethod() {
/* 380 */       return this.method;
/*     */     }
/*     */     
/*     */     public String getIdentifier() {
/* 384 */       return this.identifier;
/*     */     }
/*     */     
/*     */     public void invoke(Object target) throws Throwable {
/* 388 */       ReflectionUtils.makeAccessible(this.method);
/* 389 */       this.method.invoke(target, (Object[])null);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 394 */       if (this == other) {
/* 395 */         return true;
/*     */       }
/* 397 */       if (!(other instanceof LifecycleElement)) {
/* 398 */         return false;
/*     */       }
/* 400 */       LifecycleElement otherElement = (LifecycleElement)other;
/* 401 */       return this.identifier.equals(otherElement.identifier);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 406 */       return this.identifier.hashCode();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/annotation/InitDestroyAnnotationBeanPostProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */