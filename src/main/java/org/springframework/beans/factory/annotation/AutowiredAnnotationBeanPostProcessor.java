/*     */ package org.springframework.beans.factory.annotation;
/*     */ 
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.PropertyValues;
/*     */ import org.springframework.beans.TypeConverter;
/*     */ import org.springframework.beans.factory.BeanCreationException;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.beans.factory.BeanFactoryAware;
/*     */ import org.springframework.beans.factory.BeanFactoryUtils;
/*     */ import org.springframework.beans.factory.InjectionPoint;
/*     */ import org.springframework.beans.factory.ListableBeanFactory;
/*     */ import org.springframework.beans.factory.NoSuchBeanDefinitionException;
/*     */ import org.springframework.beans.factory.UnsatisfiedDependencyException;
/*     */ import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
/*     */ import org.springframework.beans.factory.config.DependencyDescriptor;
/*     */ import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
/*     */ import org.springframework.beans.factory.support.LookupOverride;
/*     */ import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
/*     */ import org.springframework.beans.factory.support.MethodOverride;
/*     */ import org.springframework.beans.factory.support.RootBeanDefinition;
/*     */ import org.springframework.core.BridgeMethodResolver;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.PriorityOrdered;
/*     */ import org.springframework.core.annotation.AnnotationAttributes;
/*     */ import org.springframework.core.annotation.AnnotationUtils;
/*     */ import org.springframework.core.annotation.MergedAnnotation;
/*     */ import org.springframework.core.annotation.MergedAnnotations;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.ReflectionUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AutowiredAnnotationBeanPostProcessor
/*     */   implements SmartInstantiationAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware
/*     */ {
/* 134 */   protected final Log logger = LogFactory.getLog(getClass());
/*     */   
/* 136 */   private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);
/*     */   
/* 138 */   private String requiredParameterName = "required";
/*     */   
/*     */   private boolean requiredParameterValue = true;
/*     */   
/* 142 */   private int order = 2147483645;
/*     */   
/*     */   @Nullable
/*     */   private ConfigurableListableBeanFactory beanFactory;
/*     */   
/* 147 */   private final Set<String> lookupMethodsChecked = Collections.newSetFromMap(new ConcurrentHashMap<>(256));
/*     */   
/* 149 */   private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache = (Map)new ConcurrentHashMap<>(256);
/*     */   
/* 151 */   private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AutowiredAnnotationBeanPostProcessor() {
/* 162 */     this.autowiredAnnotationTypes.add(Autowired.class);
/* 163 */     this.autowiredAnnotationTypes.add(Value.class);
/*     */     try {
/* 165 */       this.autowiredAnnotationTypes.add(
/* 166 */           ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
/* 167 */       this.logger.trace("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
/*     */     }
/* 169 */     catch (ClassNotFoundException classNotFoundException) {}
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
/*     */   public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationType) {
/* 186 */     Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
/* 187 */     this.autowiredAnnotationTypes.clear();
/* 188 */     this.autowiredAnnotationTypes.add(autowiredAnnotationType);
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
/*     */   public void setAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
/* 202 */     Assert.notEmpty(autowiredAnnotationTypes, "'autowiredAnnotationTypes' must not be empty");
/* 203 */     this.autowiredAnnotationTypes.clear();
/* 204 */     this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRequiredParameterName(String requiredParameterName) {
/* 212 */     this.requiredParameterName = requiredParameterName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRequiredParameterValue(boolean requiredParameterValue) {
/* 222 */     this.requiredParameterValue = requiredParameterValue;
/*     */   }
/*     */   
/*     */   public void setOrder(int order) {
/* 226 */     this.order = order;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOrder() {
/* 231 */     return this.order;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBeanFactory(BeanFactory beanFactory) {
/* 236 */     if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
/* 237 */       throw new IllegalArgumentException("AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
/*     */     }
/*     */     
/* 240 */     this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
/* 246 */     InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
/* 247 */     metadata.checkConfigMembers(beanDefinition);
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetBeanDefinition(String beanName) {
/* 252 */     this.lookupMethodsChecked.remove(beanName);
/* 253 */     this.injectionMetadataCache.remove(beanName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeanCreationException {
/* 262 */     if (!this.lookupMethodsChecked.contains(beanName)) {
/* 263 */       if (AnnotationUtils.isCandidateClass(beanClass, Lookup.class)) {
/*     */         try {
/* 265 */           Class<?> targetClass = beanClass;
/*     */           do {
/* 267 */             ReflectionUtils.doWithLocalMethods(targetClass, method -> {
/*     */                   Lookup lookup = method.<Lookup>getAnnotation(Lookup.class);
/*     */                   
/*     */                   if (lookup != null) {
/*     */                     Assert.state((this.beanFactory != null), "No BeanFactory available");
/*     */                     
/*     */                     LookupOverride override = new LookupOverride(method, lookup.value());
/*     */                     try {
/*     */                       RootBeanDefinition mbd = (RootBeanDefinition)this.beanFactory.getMergedBeanDefinition(beanName);
/*     */                       mbd.getMethodOverrides().addOverride((MethodOverride)override);
/* 277 */                     } catch (NoSuchBeanDefinitionException ex) {
/*     */                       throw new BeanCreationException(beanName, "Cannot apply @Lookup to beans without corresponding bean definition");
/*     */                     } 
/*     */                   } 
/*     */                 });
/*     */             
/* 283 */             targetClass = targetClass.getSuperclass();
/*     */           }
/* 285 */           while (targetClass != null && targetClass != Object.class);
/*     */         
/*     */         }
/* 288 */         catch (IllegalStateException ex) {
/* 289 */           throw new BeanCreationException(beanName, "Lookup method resolution failed", ex);
/*     */         } 
/*     */       }
/* 292 */       this.lookupMethodsChecked.add(beanName);
/*     */     } 
/*     */ 
/*     */     
/* 296 */     Constructor[] arrayOfConstructor = (Constructor[])this.candidateConstructorsCache.get(beanClass);
/* 297 */     if (arrayOfConstructor == null)
/*     */     {
/* 299 */       synchronized (this.candidateConstructorsCache) {
/* 300 */         arrayOfConstructor = (Constructor[])this.candidateConstructorsCache.get(beanClass);
/* 301 */         if (arrayOfConstructor == null) {
/*     */           Constructor[] arrayOfConstructor1;
/*     */           try {
/* 304 */             arrayOfConstructor1 = (Constructor[])beanClass.getDeclaredConstructors();
/*     */           }
/* 306 */           catch (Throwable ex) {
/* 307 */             throw new BeanCreationException(beanName, "Resolution of declared constructors on bean Class [" + beanClass
/* 308 */                 .getName() + "] from ClassLoader [" + beanClass
/* 309 */                 .getClassLoader() + "] failed", ex);
/*     */           } 
/* 311 */           List<Constructor<?>> candidates = new ArrayList<>(arrayOfConstructor1.length);
/* 312 */           Constructor<?> requiredConstructor = null;
/* 313 */           Constructor<?> defaultConstructor = null;
/* 314 */           Constructor<?> primaryConstructor = BeanUtils.findPrimaryConstructor(beanClass);
/* 315 */           int nonSyntheticConstructors = 0;
/* 316 */           for (Constructor<?> candidate : arrayOfConstructor1) {
/* 317 */             if (!candidate.isSynthetic()) {
/* 318 */               nonSyntheticConstructors++;
/*     */             }
/* 320 */             else if (primaryConstructor != null) {
/*     */               continue;
/*     */             } 
/* 323 */             MergedAnnotation<?> ann = findAutowiredAnnotation(candidate);
/* 324 */             if (ann == null) {
/* 325 */               Class<?> userClass = ClassUtils.getUserClass(beanClass);
/* 326 */               if (userClass != beanClass) {
/*     */                 
/*     */                 try {
/* 329 */                   Constructor<?> superCtor = userClass.getDeclaredConstructor(candidate.getParameterTypes());
/* 330 */                   ann = findAutowiredAnnotation(superCtor);
/*     */                 }
/* 332 */                 catch (NoSuchMethodException noSuchMethodException) {}
/*     */               }
/*     */             } 
/*     */ 
/*     */             
/* 337 */             if (ann != null) {
/* 338 */               if (requiredConstructor != null) {
/* 339 */                 throw new BeanCreationException(beanName, "Invalid autowire-marked constructor: " + candidate + ". Found constructor with 'required' Autowired annotation already: " + requiredConstructor);
/*     */               }
/*     */ 
/*     */ 
/*     */               
/* 344 */               boolean required = determineRequiredStatus(ann);
/* 345 */               if (required) {
/* 346 */                 if (!candidates.isEmpty()) {
/* 347 */                   throw new BeanCreationException(beanName, "Invalid autowire-marked constructors: " + candidates + ". Found constructor with 'required' Autowired annotation: " + candidate);
/*     */                 }
/*     */ 
/*     */ 
/*     */                 
/* 352 */                 requiredConstructor = candidate;
/*     */               } 
/* 354 */               candidates.add(candidate);
/*     */             }
/* 356 */             else if (candidate.getParameterCount() == 0) {
/* 357 */               defaultConstructor = candidate;
/*     */             }  continue;
/*     */           } 
/* 360 */           if (!candidates.isEmpty()) {
/*     */             
/* 362 */             if (requiredConstructor == null) {
/* 363 */               if (defaultConstructor != null) {
/* 364 */                 candidates.add(defaultConstructor);
/*     */               }
/* 366 */               else if (candidates.size() == 1 && this.logger.isInfoEnabled()) {
/* 367 */                 this.logger.info("Inconsistent constructor declaration on bean with name '" + beanName + "': single autowire-marked constructor flagged as optional - this constructor is effectively required since there is no default constructor to fall back to: " + candidates
/*     */ 
/*     */                     
/* 370 */                     .get(0));
/*     */               } 
/*     */             }
/* 373 */             arrayOfConstructor = candidates.<Constructor>toArray(new Constructor[0]);
/*     */           }
/* 375 */           else if (arrayOfConstructor1.length == 1 && arrayOfConstructor1[0].getParameterCount() > 0) {
/* 376 */             arrayOfConstructor = new Constructor[] { arrayOfConstructor1[0] };
/*     */           }
/* 378 */           else if (nonSyntheticConstructors == 2 && primaryConstructor != null && defaultConstructor != null && 
/* 379 */             !primaryConstructor.equals(defaultConstructor)) {
/* 380 */             arrayOfConstructor = new Constructor[] { primaryConstructor, defaultConstructor };
/*     */           }
/* 382 */           else if (nonSyntheticConstructors == 1 && primaryConstructor != null) {
/* 383 */             arrayOfConstructor = new Constructor[] { primaryConstructor };
/*     */           } else {
/*     */             
/* 386 */             arrayOfConstructor = new Constructor[0];
/*     */           } 
/* 388 */           this.candidateConstructorsCache.put(beanClass, arrayOfConstructor);
/*     */         } 
/*     */       } 
/*     */     }
/* 392 */     return (arrayOfConstructor.length > 0) ? (Constructor<?>[])arrayOfConstructor : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
/* 397 */     InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
/*     */     try {
/* 399 */       metadata.inject(bean, beanName, pvs);
/*     */     }
/* 401 */     catch (BeanCreationException ex) {
/* 402 */       throw ex;
/*     */     }
/* 404 */     catch (Throwable ex) {
/* 405 */       throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
/*     */     } 
/* 407 */     return pvs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) {
/* 415 */     return postProcessProperties(pvs, bean, beanName);
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
/*     */   public void processInjection(Object bean) throws BeanCreationException {
/* 427 */     Class<?> clazz = bean.getClass();
/* 428 */     InjectionMetadata metadata = findAutowiringMetadata(clazz.getName(), clazz, null);
/*     */     try {
/* 430 */       metadata.inject(bean, null, null);
/*     */     }
/* 432 */     catch (BeanCreationException ex) {
/* 433 */       throw ex;
/*     */     }
/* 435 */     catch (Throwable ex) {
/* 436 */       throw new BeanCreationException("Injection of autowired dependencies failed for class [" + clazz + "]", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
/* 444 */     String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
/*     */     
/* 446 */     InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
/* 447 */     if (InjectionMetadata.needsRefresh(metadata, clazz)) {
/* 448 */       synchronized (this.injectionMetadataCache) {
/* 449 */         metadata = this.injectionMetadataCache.get(cacheKey);
/* 450 */         if (InjectionMetadata.needsRefresh(metadata, clazz)) {
/* 451 */           if (metadata != null) {
/* 452 */             metadata.clear(pvs);
/*     */           }
/* 454 */           metadata = buildAutowiringMetadata(clazz);
/* 455 */           this.injectionMetadataCache.put(cacheKey, metadata);
/*     */         } 
/*     */       } 
/*     */     }
/* 459 */     return metadata;
/*     */   }
/*     */   
/*     */   private InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
/* 463 */     if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
/* 464 */       return InjectionMetadata.EMPTY;
/*     */     }
/*     */     
/* 467 */     List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
/* 468 */     Class<?> targetClass = clazz;
/*     */     
/*     */     do {
/* 471 */       List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();
/*     */       
/* 473 */       ReflectionUtils.doWithLocalFields(targetClass, field -> {
/*     */             MergedAnnotation<?> ann = findAutowiredAnnotation(field);
/*     */             
/*     */             if (ann != null) {
/*     */               if (Modifier.isStatic(field.getModifiers())) {
/*     */                 if (this.logger.isInfoEnabled()) {
/*     */                   this.logger.info("Autowired annotation is not supported on static fields: " + field);
/*     */                 }
/*     */                 return;
/*     */               } 
/*     */               boolean required = determineRequiredStatus(ann);
/*     */               currElements.add(new AutowiredFieldElement(field, required));
/*     */             } 
/*     */           });
/* 487 */       ReflectionUtils.doWithLocalMethods(targetClass, method -> {
/*     */             Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
/*     */             
/*     */             if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
/*     */               return;
/*     */             }
/*     */             
/*     */             MergedAnnotation<?> ann = findAutowiredAnnotation(bridgedMethod);
/*     */             
/*     */             if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
/*     */               if (Modifier.isStatic(method.getModifiers())) {
/*     */                 if (this.logger.isInfoEnabled()) {
/*     */                   this.logger.info("Autowired annotation is not supported on static methods: " + method);
/*     */                 }
/*     */                 
/*     */                 return;
/*     */               } 
/*     */               if (method.getParameterCount() == 0 && this.logger.isInfoEnabled()) {
/*     */                 this.logger.info("Autowired annotation should only be used on methods with parameters: " + method);
/*     */               }
/*     */               boolean required = determineRequiredStatus(ann);
/*     */               PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
/*     */               currElements.add(new AutowiredMethodElement(method, required, pd));
/*     */             } 
/*     */           });
/* 512 */       elements.addAll(0, currElements);
/* 513 */       targetClass = targetClass.getSuperclass();
/*     */     }
/* 515 */     while (targetClass != null && targetClass != Object.class);
/*     */     
/* 517 */     return InjectionMetadata.forElements(elements, clazz);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private MergedAnnotation<?> findAutowiredAnnotation(AccessibleObject ao) {
/* 522 */     MergedAnnotations annotations = MergedAnnotations.from(ao);
/* 523 */     for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
/* 524 */       MergedAnnotation<?> annotation = annotations.get(type);
/* 525 */       if (annotation.isPresent()) {
/* 526 */         return annotation;
/*     */       }
/*     */     } 
/* 529 */     return null;
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
/*     */   protected boolean determineRequiredStatus(MergedAnnotation<?> ann) {
/* 542 */     return determineRequiredStatus((AnnotationAttributes)ann.asMap(mergedAnnotation -> new AnnotationAttributes(mergedAnnotation.getType()), new MergedAnnotation.Adapt[0]));
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
/*     */   @Deprecated
/*     */   protected boolean determineRequiredStatus(AnnotationAttributes ann) {
/* 557 */     return (!ann.containsKey(this.requiredParameterName) || this.requiredParameterValue == ann
/* 558 */       .getBoolean(this.requiredParameterName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected <T> Map<String, T> findAutowireCandidates(Class<T> type) throws BeansException {
/* 568 */     if (this.beanFactory == null) {
/* 569 */       throw new IllegalStateException("No BeanFactory configured - override the getBeanOfType method or specify the 'beanFactory' property");
/*     */     }
/*     */     
/* 572 */     return BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory)this.beanFactory, type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void registerDependentBeans(@Nullable String beanName, Set<String> autowiredBeanNames) {
/* 579 */     if (beanName != null) {
/* 580 */       for (String autowiredBeanName : autowiredBeanNames) {
/* 581 */         if (this.beanFactory != null && this.beanFactory.containsBean(autowiredBeanName)) {
/* 582 */           this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
/*     */         }
/* 584 */         if (this.logger.isTraceEnabled()) {
/* 585 */           this.logger.trace("Autowiring by type from bean name '" + beanName + "' to bean named '" + autowiredBeanName + "'");
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Object resolvedCachedArgument(@Nullable String beanName, @Nullable Object cachedArgument) {
/* 597 */     if (cachedArgument instanceof DependencyDescriptor) {
/* 598 */       DependencyDescriptor descriptor = (DependencyDescriptor)cachedArgument;
/* 599 */       Assert.state((this.beanFactory != null), "No BeanFactory available");
/* 600 */       return this.beanFactory.resolveDependency(descriptor, beanName, null, null);
/*     */     } 
/*     */     
/* 603 */     return cachedArgument;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class AutowiredFieldElement
/*     */     extends InjectionMetadata.InjectedElement
/*     */   {
/*     */     private final boolean required;
/*     */ 
/*     */     
/*     */     private volatile boolean cached;
/*     */     
/*     */     @Nullable
/*     */     private volatile Object cachedFieldValue;
/*     */ 
/*     */     
/*     */     public AutowiredFieldElement(Field field, boolean required) {
/* 621 */       super(field, null);
/* 622 */       this.required = required;
/*     */     }
/*     */     
/*     */     protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
/*     */       Object value;
/* 627 */       Field field = (Field)this.member;
/*     */       
/* 629 */       if (this.cached) {
/*     */         try {
/* 631 */           value = AutowiredAnnotationBeanPostProcessor.this.resolvedCachedArgument(beanName, this.cachedFieldValue);
/*     */         }
/* 633 */         catch (NoSuchBeanDefinitionException ex) {
/*     */           
/* 635 */           value = resolveFieldValue(field, bean, beanName);
/*     */         } 
/*     */       } else {
/*     */         
/* 639 */         value = resolveFieldValue(field, bean, beanName);
/*     */       } 
/* 641 */       if (value != null) {
/* 642 */         ReflectionUtils.makeAccessible(field);
/* 643 */         field.set(bean, value);
/*     */       } 
/*     */     }
/*     */     @Nullable
/*     */     private Object resolveFieldValue(Field field, Object bean, @Nullable String beanName) {
/*     */       Object value;
/* 649 */       DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
/* 650 */       desc.setContainingClass(bean.getClass());
/* 651 */       Set<String> autowiredBeanNames = new LinkedHashSet<>(1);
/* 652 */       Assert.state((AutowiredAnnotationBeanPostProcessor.this.beanFactory != null), "No BeanFactory available");
/* 653 */       TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();
/*     */       
/*     */       try {
/* 656 */         value = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
/*     */       }
/* 658 */       catch (BeansException ex) {
/* 659 */         throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
/*     */       } 
/* 661 */       synchronized (this) {
/* 662 */         if (!this.cached) {
/* 663 */           Object cachedFieldValue = null;
/* 664 */           if (value != null || this.required) {
/* 665 */             cachedFieldValue = desc;
/* 666 */             AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeanNames);
/* 667 */             if (autowiredBeanNames.size() == 1) {
/* 668 */               String autowiredBeanName = autowiredBeanNames.iterator().next();
/* 669 */               if (AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) && AutowiredAnnotationBeanPostProcessor.this
/* 670 */                 .beanFactory.isTypeMatch(autowiredBeanName, field.getType()))
/*     */               {
/* 672 */                 cachedFieldValue = new AutowiredAnnotationBeanPostProcessor.ShortcutDependencyDescriptor(desc, autowiredBeanName, field.getType());
/*     */               }
/*     */             } 
/*     */           } 
/* 676 */           this.cachedFieldValue = cachedFieldValue;
/* 677 */           this.cached = true;
/*     */         } 
/*     */       } 
/* 680 */       return value;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class AutowiredMethodElement
/*     */     extends InjectionMetadata.InjectedElement
/*     */   {
/*     */     private final boolean required;
/*     */     
/*     */     private volatile boolean cached;
/*     */     
/*     */     @Nullable
/*     */     private volatile Object[] cachedMethodArguments;
/*     */ 
/*     */     
/*     */     public AutowiredMethodElement(Method method, @Nullable boolean required, PropertyDescriptor pd) {
/* 698 */       super(method, pd);
/* 699 */       this.required = required;
/*     */     }
/*     */     
/*     */     protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
/*     */       Object[] arguments;
/* 704 */       if (checkPropertySkipping(pvs)) {
/*     */         return;
/*     */       }
/* 707 */       Method method = (Method)this.member;
/*     */       
/* 709 */       if (this.cached) {
/*     */         try {
/* 711 */           arguments = resolveCachedArguments(beanName);
/*     */         }
/* 713 */         catch (NoSuchBeanDefinitionException ex) {
/*     */           
/* 715 */           arguments = resolveMethodArguments(method, bean, beanName);
/*     */         } 
/*     */       } else {
/*     */         
/* 719 */         arguments = resolveMethodArguments(method, bean, beanName);
/*     */       } 
/* 721 */       if (arguments != null) {
/*     */         try {
/* 723 */           ReflectionUtils.makeAccessible(method);
/* 724 */           method.invoke(bean, arguments);
/*     */         }
/* 726 */         catch (InvocationTargetException ex) {
/* 727 */           throw ex.getTargetException();
/*     */         } 
/*     */       }
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private Object[] resolveCachedArguments(@Nullable String beanName) {
/* 734 */       Object[] cachedMethodArguments = this.cachedMethodArguments;
/* 735 */       if (cachedMethodArguments == null) {
/* 736 */         return null;
/*     */       }
/* 738 */       Object[] arguments = new Object[cachedMethodArguments.length];
/* 739 */       for (int i = 0; i < arguments.length; i++) {
/* 740 */         arguments[i] = AutowiredAnnotationBeanPostProcessor.this.resolvedCachedArgument(beanName, cachedMethodArguments[i]);
/*     */       }
/* 742 */       return arguments;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private Object[] resolveMethodArguments(Method method, Object bean, @Nullable String beanName) {
/* 747 */       int argumentCount = method.getParameterCount();
/* 748 */       Object[] arguments = new Object[argumentCount];
/* 749 */       DependencyDescriptor[] descriptors = new DependencyDescriptor[argumentCount];
/* 750 */       Set<String> autowiredBeans = new LinkedHashSet<>(argumentCount);
/* 751 */       Assert.state((AutowiredAnnotationBeanPostProcessor.this.beanFactory != null), "No BeanFactory available");
/* 752 */       TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();
/* 753 */       for (int i = 0; i < arguments.length; i++) {
/* 754 */         MethodParameter methodParam = new MethodParameter(method, i);
/* 755 */         DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
/* 756 */         currDesc.setContainingClass(bean.getClass());
/* 757 */         descriptors[i] = currDesc;
/*     */         try {
/* 759 */           Object arg = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(currDesc, beanName, autowiredBeans, typeConverter);
/* 760 */           if (arg == null && !this.required) {
/* 761 */             arguments = null;
/*     */             break;
/*     */           } 
/* 764 */           arguments[i] = arg;
/*     */         }
/* 766 */         catch (BeansException ex) {
/* 767 */           throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(methodParam), ex);
/*     */         } 
/*     */       } 
/* 770 */       synchronized (this) {
/* 771 */         if (!this.cached) {
/* 772 */           if (arguments != null) {
/* 773 */             DependencyDescriptor[] cachedMethodArguments = Arrays.<DependencyDescriptor>copyOf(descriptors, arguments.length);
/* 774 */             AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeans);
/* 775 */             if (autowiredBeans.size() == argumentCount) {
/* 776 */               Iterator<String> it = autowiredBeans.iterator();
/* 777 */               Class<?>[] paramTypes = method.getParameterTypes();
/* 778 */               for (int j = 0; j < paramTypes.length; j++) {
/* 779 */                 String autowiredBeanName = it.next();
/* 780 */                 if (AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) && AutowiredAnnotationBeanPostProcessor.this
/* 781 */                   .beanFactory.isTypeMatch(autowiredBeanName, paramTypes[j])) {
/* 782 */                   cachedMethodArguments[j] = new AutowiredAnnotationBeanPostProcessor.ShortcutDependencyDescriptor(descriptors[j], autowiredBeanName, paramTypes[j]);
/*     */                 }
/*     */               } 
/*     */             } 
/*     */             
/* 787 */             this.cachedMethodArguments = (Object[])cachedMethodArguments;
/*     */           } else {
/*     */             
/* 790 */             this.cachedMethodArguments = null;
/*     */           } 
/* 792 */           this.cached = true;
/*     */         } 
/*     */       } 
/* 795 */       return arguments;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ShortcutDependencyDescriptor
/*     */     extends DependencyDescriptor
/*     */   {
/*     */     private final String shortcut;
/*     */ 
/*     */     
/*     */     private final Class<?> requiredType;
/*     */ 
/*     */     
/*     */     public ShortcutDependencyDescriptor(DependencyDescriptor original, String shortcut, Class<?> requiredType) {
/* 811 */       super(original);
/* 812 */       this.shortcut = shortcut;
/* 813 */       this.requiredType = requiredType;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object resolveShortcut(BeanFactory beanFactory) {
/* 818 */       return beanFactory.getBean(this.shortcut, this.requiredType);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */