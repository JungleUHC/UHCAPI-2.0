/*      */ package org.springframework.beans.factory.support;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.NotSerializableException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectStreamException;
/*      */ import java.io.Serializable;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Method;
/*      */ import java.security.AccessController;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Comparator;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.function.Predicate;
/*      */ import java.util.stream.Stream;
/*      */ import javax.inject.Provider;
/*      */ import org.springframework.beans.BeansException;
/*      */ import org.springframework.beans.FatalBeanException;
/*      */ import org.springframework.beans.TypeConverter;
/*      */ import org.springframework.beans.factory.BeanCreationException;
/*      */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*      */ import org.springframework.beans.factory.BeanFactory;
/*      */ import org.springframework.beans.factory.BeanFactoryAware;
/*      */ import org.springframework.beans.factory.BeanFactoryUtils;
/*      */ import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
/*      */ import org.springframework.beans.factory.CannotLoadBeanClassException;
/*      */ import org.springframework.beans.factory.FactoryBean;
/*      */ import org.springframework.beans.factory.InjectionPoint;
/*      */ import org.springframework.beans.factory.ListableBeanFactory;
/*      */ import org.springframework.beans.factory.NoSuchBeanDefinitionException;
/*      */ import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
/*      */ import org.springframework.beans.factory.ObjectFactory;
/*      */ import org.springframework.beans.factory.ObjectProvider;
/*      */ import org.springframework.beans.factory.SmartFactoryBean;
/*      */ import org.springframework.beans.factory.SmartInitializingSingleton;
/*      */ import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
/*      */ import org.springframework.beans.factory.config.BeanDefinition;
/*      */ import org.springframework.beans.factory.config.BeanDefinitionHolder;
/*      */ import org.springframework.beans.factory.config.ConfigurableBeanFactory;
/*      */ import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
/*      */ import org.springframework.beans.factory.config.DependencyDescriptor;
/*      */ import org.springframework.beans.factory.config.NamedBeanHolder;
/*      */ import org.springframework.core.OrderComparator;
/*      */ import org.springframework.core.ResolvableType;
/*      */ import org.springframework.core.annotation.MergedAnnotation;
/*      */ import org.springframework.core.annotation.MergedAnnotations;
/*      */ import org.springframework.core.log.LogMessage;
/*      */ import org.springframework.core.metrics.StartupStep;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
/*      */ import org.springframework.util.ClassUtils;
/*      */ import org.springframework.util.CollectionUtils;
/*      */ import org.springframework.util.CompositeIterator;
/*      */ import org.springframework.util.ObjectUtils;
/*      */ import org.springframework.util.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DefaultListableBeanFactory
/*      */   extends AbstractAutowireCapableBeanFactory
/*      */   implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable
/*      */ {
/*      */   @Nullable
/*      */   private static Class<?> javaxInjectProviderClass;
/*      */   
/*      */   static {
/*      */     try {
/*  130 */       javaxInjectProviderClass = ClassUtils.forName("javax.inject.Provider", DefaultListableBeanFactory.class.getClassLoader());
/*      */     }
/*  132 */     catch (ClassNotFoundException ex) {
/*      */       
/*  134 */       javaxInjectProviderClass = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*  140 */   private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories = new ConcurrentHashMap<>(8);
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private String serializationId;
/*      */ 
/*      */   
/*      */   private boolean allowBeanDefinitionOverriding = true;
/*      */ 
/*      */   
/*      */   private boolean allowEagerClassLoading = true;
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private Comparator<Object> dependencyComparator;
/*      */ 
/*      */   
/*  158 */   private AutowireCandidateResolver autowireCandidateResolver = SimpleAutowireCandidateResolver.INSTANCE;
/*      */ 
/*      */   
/*  161 */   private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);
/*      */ 
/*      */   
/*  164 */   private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
/*      */ 
/*      */   
/*  167 */   private final Map<String, BeanDefinitionHolder> mergedBeanDefinitionHolders = new ConcurrentHashMap<>(256);
/*      */ 
/*      */   
/*  170 */   private final Map<Class<?>, String[]> allBeanNamesByType = (Map)new ConcurrentHashMap<>(64);
/*      */ 
/*      */   
/*  173 */   private final Map<Class<?>, String[]> singletonBeanNamesByType = (Map)new ConcurrentHashMap<>(64);
/*      */ 
/*      */   
/*  176 */   private volatile List<String> beanDefinitionNames = new ArrayList<>(256);
/*      */ 
/*      */   
/*  179 */   private volatile Set<String> manualSingletonNames = new LinkedHashSet<>(16);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private volatile String[] frozenBeanDefinitionNames;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile boolean configurationFrozen;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DefaultListableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
/*  201 */     super(parentBeanFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSerializationId(@Nullable String serializationId) {
/*  210 */     if (serializationId != null) {
/*  211 */       serializableFactories.put(serializationId, new WeakReference<>(this));
/*      */     }
/*  213 */     else if (this.serializationId != null) {
/*  214 */       serializableFactories.remove(this.serializationId);
/*      */     } 
/*  216 */     this.serializationId = serializationId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getSerializationId() {
/*  226 */     return this.serializationId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
/*  237 */     this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAllowBeanDefinitionOverriding() {
/*  246 */     return this.allowBeanDefinitionOverriding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAllowEagerClassLoading(boolean allowEagerClassLoading) {
/*  260 */     this.allowEagerClassLoading = allowEagerClassLoading;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAllowEagerClassLoading() {
/*  269 */     return this.allowEagerClassLoading;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDependencyComparator(@Nullable Comparator<Object> dependencyComparator) {
/*  279 */     this.dependencyComparator = dependencyComparator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Comparator<Object> getDependencyComparator() {
/*  288 */     return this.dependencyComparator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutowireCandidateResolver(AutowireCandidateResolver autowireCandidateResolver) {
/*  297 */     Assert.notNull(autowireCandidateResolver, "AutowireCandidateResolver must not be null");
/*  298 */     if (autowireCandidateResolver instanceof BeanFactoryAware) {
/*  299 */       if (System.getSecurityManager() != null) {
/*  300 */         AccessController.doPrivileged(() -> {
/*      */               ((BeanFactoryAware)autowireCandidateResolver).setBeanFactory((BeanFactory)this);
/*      */               return null;
/*  303 */             }getAccessControlContext());
/*      */       } else {
/*      */         
/*  306 */         ((BeanFactoryAware)autowireCandidateResolver).setBeanFactory((BeanFactory)this);
/*      */       } 
/*      */     }
/*  309 */     this.autowireCandidateResolver = autowireCandidateResolver;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AutowireCandidateResolver getAutowireCandidateResolver() {
/*  316 */     return this.autowireCandidateResolver;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
/*  322 */     super.copyConfigurationFrom(otherFactory);
/*  323 */     if (otherFactory instanceof DefaultListableBeanFactory) {
/*  324 */       DefaultListableBeanFactory otherListableFactory = (DefaultListableBeanFactory)otherFactory;
/*  325 */       this.allowBeanDefinitionOverriding = otherListableFactory.allowBeanDefinitionOverriding;
/*  326 */       this.allowEagerClassLoading = otherListableFactory.allowEagerClassLoading;
/*  327 */       this.dependencyComparator = otherListableFactory.dependencyComparator;
/*      */       
/*  329 */       setAutowireCandidateResolver(otherListableFactory.getAutowireCandidateResolver().cloneIfNecessary());
/*      */       
/*  331 */       this.resolvableDependencies.putAll(otherListableFactory.resolvableDependencies);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T getBean(Class<T> requiredType) throws BeansException {
/*  342 */     return getBean(requiredType, (Object[])null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T getBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
/*  348 */     Assert.notNull(requiredType, "Required type must not be null");
/*  349 */     Object resolved = resolveBean(ResolvableType.forRawClass(requiredType), args, false);
/*  350 */     if (resolved == null) {
/*  351 */       throw new NoSuchBeanDefinitionException(requiredType);
/*      */     }
/*  353 */     return (T)resolved;
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
/*  358 */     Assert.notNull(requiredType, "Required type must not be null");
/*  359 */     return getBeanProvider(ResolvableType.forRawClass(requiredType), true);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
/*  364 */     return getBeanProvider(requiredType, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsBeanDefinition(String beanName) {
/*  374 */     Assert.notNull(beanName, "Bean name must not be null");
/*  375 */     return this.beanDefinitionMap.containsKey(beanName);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBeanDefinitionCount() {
/*  380 */     return this.beanDefinitionMap.size();
/*      */   }
/*      */ 
/*      */   
/*      */   public String[] getBeanDefinitionNames() {
/*  385 */     String[] frozenNames = this.frozenBeanDefinitionNames;
/*  386 */     if (frozenNames != null) {
/*  387 */       return (String[])frozenNames.clone();
/*      */     }
/*      */     
/*  390 */     return StringUtils.toStringArray(this.beanDefinitionNames);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
/*  396 */     Assert.notNull(requiredType, "Required type must not be null");
/*  397 */     return getBeanProvider(ResolvableType.forRawClass(requiredType), allowEagerInit);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType, final boolean allowEagerInit) {
/*  402 */     return new BeanObjectProvider<T>()
/*      */       {
/*      */         public T getObject() throws BeansException {
/*  405 */           T resolved = DefaultListableBeanFactory.this.resolveBean(requiredType, (Object[])null, false);
/*  406 */           if (resolved == null) {
/*  407 */             throw new NoSuchBeanDefinitionException(requiredType);
/*      */           }
/*  409 */           return resolved;
/*      */         }
/*      */         
/*      */         public T getObject(Object... args) throws BeansException {
/*  413 */           T resolved = DefaultListableBeanFactory.this.resolveBean(requiredType, args, false);
/*  414 */           if (resolved == null) {
/*  415 */             throw new NoSuchBeanDefinitionException(requiredType);
/*      */           }
/*  417 */           return resolved;
/*      */         }
/*      */         
/*      */         @Nullable
/*      */         public T getIfAvailable() throws BeansException {
/*      */           try {
/*  423 */             return DefaultListableBeanFactory.this.resolveBean(requiredType, (Object[])null, false);
/*      */           }
/*  425 */           catch (ScopeNotActiveException ex) {
/*      */             
/*  427 */             return null;
/*      */           } 
/*      */         }
/*      */         
/*      */         public void ifAvailable(Consumer<T> dependencyConsumer) throws BeansException {
/*  432 */           T dependency = getIfAvailable();
/*  433 */           if (dependency != null) {
/*      */             try {
/*  435 */               dependencyConsumer.accept(dependency);
/*      */             }
/*  437 */             catch (ScopeNotActiveException scopeNotActiveException) {}
/*      */           }
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*      */         @Nullable
/*      */         public T getIfUnique() throws BeansException {
/*      */           try {
/*  446 */             return DefaultListableBeanFactory.this.resolveBean(requiredType, (Object[])null, true);
/*      */           }
/*  448 */           catch (ScopeNotActiveException ex) {
/*      */             
/*  450 */             return null;
/*      */           } 
/*      */         }
/*      */         
/*      */         public void ifUnique(Consumer<T> dependencyConsumer) throws BeansException {
/*  455 */           T dependency = getIfUnique();
/*  456 */           if (dependency != null) {
/*      */             try {
/*  458 */               dependencyConsumer.accept(dependency);
/*      */             }
/*  460 */             catch (ScopeNotActiveException scopeNotActiveException) {}
/*      */           }
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         public Stream<T> stream() {
/*  468 */           return Arrays.<String>stream(DefaultListableBeanFactory.this.getBeanNamesForTypedStream(requiredType, allowEagerInit))
/*  469 */             .map(name -> DefaultListableBeanFactory.this.getBean(name))
/*  470 */             .filter(bean -> !(bean instanceof NullBean));
/*      */         }
/*      */ 
/*      */         
/*      */         public Stream<T> orderedStream() {
/*  475 */           String[] beanNames = DefaultListableBeanFactory.this.getBeanNamesForTypedStream(requiredType, allowEagerInit);
/*  476 */           if (beanNames.length == 0) {
/*  477 */             return Stream.empty();
/*      */           }
/*  479 */           Map<String, T> matchingBeans = CollectionUtils.newLinkedHashMap(beanNames.length);
/*  480 */           for (String beanName : beanNames) {
/*  481 */             Object beanInstance = DefaultListableBeanFactory.this.getBean(beanName);
/*  482 */             if (!(beanInstance instanceof NullBean)) {
/*  483 */               matchingBeans.put(beanName, (T)beanInstance);
/*      */             }
/*      */           } 
/*  486 */           Stream<T> stream = matchingBeans.values().stream();
/*  487 */           return stream.sorted((Comparator)DefaultListableBeanFactory.this.adaptOrderComparator(matchingBeans));
/*      */         }
/*      */       };
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   private <T> T resolveBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) {
/*  494 */     NamedBeanHolder<T> namedBean = resolveNamedBean(requiredType, args, nonUniqueAsNull);
/*  495 */     if (namedBean != null) {
/*  496 */       return (T)namedBean.getBeanInstance();
/*      */     }
/*  498 */     BeanFactory parent = getParentBeanFactory();
/*  499 */     if (parent instanceof DefaultListableBeanFactory) {
/*  500 */       return ((DefaultListableBeanFactory)parent).resolveBean(requiredType, args, nonUniqueAsNull);
/*      */     }
/*  502 */     if (parent != null) {
/*  503 */       ObjectProvider<T> parentProvider = parent.getBeanProvider(requiredType);
/*  504 */       if (args != null) {
/*  505 */         return (T)parentProvider.getObject(args);
/*      */       }
/*      */       
/*  508 */       return nonUniqueAsNull ? (T)parentProvider.getIfUnique() : (T)parentProvider.getIfAvailable();
/*      */     } 
/*      */     
/*  511 */     return null;
/*      */   }
/*      */   
/*      */   private String[] getBeanNamesForTypedStream(ResolvableType requiredType, boolean allowEagerInit) {
/*  515 */     return BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)this, requiredType, true, allowEagerInit);
/*      */   }
/*      */ 
/*      */   
/*      */   public String[] getBeanNamesForType(ResolvableType type) {
/*  520 */     return getBeanNamesForType(type, true, true);
/*      */   }
/*      */ 
/*      */   
/*      */   public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
/*  525 */     Class<?> resolved = type.resolve();
/*  526 */     if (resolved != null && !type.hasGenerics()) {
/*  527 */       return getBeanNamesForType(resolved, includeNonSingletons, allowEagerInit);
/*      */     }
/*      */     
/*  530 */     return doGetBeanNamesForType(type, includeNonSingletons, allowEagerInit);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getBeanNamesForType(@Nullable Class<?> type) {
/*  536 */     return getBeanNamesForType(type, true, true);
/*      */   }
/*      */ 
/*      */   
/*      */   public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
/*  541 */     if (!isConfigurationFrozen() || type == null || !allowEagerInit) {
/*  542 */       return doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, allowEagerInit);
/*      */     }
/*  544 */     Map<Class<?>, String[]> cache = includeNonSingletons ? this.allBeanNamesByType : this.singletonBeanNamesByType;
/*      */     
/*  546 */     String[] resolvedBeanNames = cache.get(type);
/*  547 */     if (resolvedBeanNames != null) {
/*  548 */       return resolvedBeanNames;
/*      */     }
/*  550 */     resolvedBeanNames = doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, true);
/*  551 */     if (ClassUtils.isCacheSafe(type, getBeanClassLoader())) {
/*  552 */       cache.put(type, resolvedBeanNames);
/*      */     }
/*  554 */     return resolvedBeanNames;
/*      */   }
/*      */   
/*      */   private String[] doGetBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
/*  558 */     List<String> result = new ArrayList<>();
/*      */ 
/*      */     
/*  561 */     for (String beanName : this.beanDefinitionNames) {
/*      */       
/*  563 */       if (!isAlias(beanName)) {
/*      */         try {
/*  565 */           RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
/*      */           
/*  567 */           if (!mbd.isAbstract() && (allowEagerInit || ((mbd
/*  568 */             .hasBeanClass() || !mbd.isLazyInit() || isAllowEagerClassLoading()) && 
/*  569 */             !requiresEagerInitForType(mbd.getFactoryBeanName())))) {
/*  570 */             boolean isFactoryBean = isFactoryBean(beanName, mbd);
/*  571 */             BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
/*  572 */             boolean matchFound = false;
/*  573 */             boolean allowFactoryBeanInit = (allowEagerInit || containsSingleton(beanName));
/*  574 */             boolean isNonLazyDecorated = (dbd != null && !mbd.isLazyInit());
/*  575 */             if (!isFactoryBean) {
/*  576 */               if (includeNonSingletons || isSingleton(beanName, mbd, dbd)) {
/*  577 */                 matchFound = isTypeMatch(beanName, type, allowFactoryBeanInit);
/*      */               }
/*      */             } else {
/*      */               
/*  581 */               if (includeNonSingletons || isNonLazyDecorated || (allowFactoryBeanInit && 
/*  582 */                 isSingleton(beanName, mbd, dbd))) {
/*  583 */                 matchFound = isTypeMatch(beanName, type, allowFactoryBeanInit);
/*      */               }
/*  585 */               if (!matchFound) {
/*      */                 
/*  587 */                 beanName = "&" + beanName;
/*  588 */                 matchFound = isTypeMatch(beanName, type, allowFactoryBeanInit);
/*      */               } 
/*      */             } 
/*  591 */             if (matchFound) {
/*  592 */               result.add(beanName);
/*      */             }
/*      */           }
/*      */         
/*  596 */         } catch (CannotLoadBeanClassException|BeanDefinitionStoreException ex) {
/*  597 */           if (allowEagerInit) {
/*  598 */             throw ex;
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*  603 */           LogMessage message = (ex instanceof CannotLoadBeanClassException) ? LogMessage.format("Ignoring bean class loading failure for bean '%s'", beanName) : LogMessage.format("Ignoring unresolvable metadata in bean definition '%s'", beanName);
/*  604 */           this.logger.trace(message, (Throwable)ex);
/*      */           
/*  606 */           onSuppressedException((Exception)ex);
/*      */         }
/*  608 */         catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  615 */     for (String beanName : this.manualSingletonNames) {
/*      */       
/*      */       try {
/*  618 */         if (isFactoryBean(beanName)) {
/*  619 */           if ((includeNonSingletons || isSingleton(beanName)) && isTypeMatch(beanName, type)) {
/*  620 */             result.add(beanName);
/*      */             
/*      */             continue;
/*      */           } 
/*      */           
/*  625 */           beanName = "&" + beanName;
/*      */         } 
/*      */         
/*  628 */         if (isTypeMatch(beanName, type)) {
/*  629 */           result.add(beanName);
/*      */         }
/*      */       }
/*  632 */       catch (NoSuchBeanDefinitionException ex) {
/*      */         
/*  634 */         this.logger.trace(LogMessage.format("Failed to check manually registered singleton with name '%s'", beanName), (Throwable)ex);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  639 */     return StringUtils.toStringArray(result);
/*      */   }
/*      */   
/*      */   private boolean isSingleton(String beanName, RootBeanDefinition mbd, @Nullable BeanDefinitionHolder dbd) {
/*  643 */     return (dbd != null) ? mbd.isSingleton() : isSingleton(beanName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean requiresEagerInitForType(@Nullable String factoryBeanName) {
/*  654 */     return (factoryBeanName != null && isFactoryBean(factoryBeanName) && !containsSingleton(factoryBeanName));
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
/*  659 */     return getBeansOfType(type, true, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
/*  667 */     String[] beanNames = getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
/*  668 */     Map<String, T> result = CollectionUtils.newLinkedHashMap(beanNames.length);
/*  669 */     for (String beanName : beanNames) {
/*      */       try {
/*  671 */         Object beanInstance = getBean(beanName);
/*  672 */         if (!(beanInstance instanceof NullBean)) {
/*  673 */           result.put(beanName, (T)beanInstance);
/*      */         }
/*      */       }
/*  676 */       catch (BeanCreationException ex) {
/*  677 */         Throwable rootCause = ex.getMostSpecificCause();
/*  678 */         if (rootCause instanceof org.springframework.beans.factory.BeanCurrentlyInCreationException)
/*  679 */         { BeanCreationException bce = (BeanCreationException)rootCause;
/*  680 */           String exBeanName = bce.getBeanName();
/*  681 */           if (exBeanName != null && isCurrentlyInCreation(exBeanName))
/*  682 */           { if (this.logger.isTraceEnabled()) {
/*  683 */               this.logger.trace("Ignoring match to currently created bean '" + exBeanName + "': " + ex
/*  684 */                   .getMessage());
/*      */             }
/*  686 */             onSuppressedException((Exception)ex);
/*      */              }
/*      */           
/*      */           else
/*      */           
/*      */           { 
/*  692 */             throw ex; }  } else { throw ex; }
/*      */       
/*      */       } 
/*  695 */     }  return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
/*  700 */     List<String> result = new ArrayList<>();
/*  701 */     for (String beanName : this.beanDefinitionNames) {
/*  702 */       BeanDefinition bd = this.beanDefinitionMap.get(beanName);
/*  703 */       if (bd != null && !bd.isAbstract() && findAnnotationOnBean(beanName, annotationType) != null) {
/*  704 */         result.add(beanName);
/*      */       }
/*      */     } 
/*  707 */     for (String beanName : this.manualSingletonNames) {
/*  708 */       if (!result.contains(beanName) && findAnnotationOnBean(beanName, annotationType) != null) {
/*  709 */         result.add(beanName);
/*      */       }
/*      */     } 
/*  712 */     return StringUtils.toStringArray(result);
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
/*  717 */     String[] beanNames = getBeanNamesForAnnotation(annotationType);
/*  718 */     Map<String, Object> result = CollectionUtils.newLinkedHashMap(beanNames.length);
/*  719 */     for (String beanName : beanNames) {
/*  720 */       Object beanInstance = getBean(beanName);
/*  721 */       if (!(beanInstance instanceof NullBean)) {
/*  722 */         result.put(beanName, beanInstance);
/*      */       }
/*      */     } 
/*  725 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
/*  733 */     return findAnnotationOnBean(beanName, annotationType, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
/*  742 */     return (A)findMergedAnnotationOnBean(beanName, annotationType, allowFactoryBeanInit)
/*  743 */       .synthesize(MergedAnnotation::isPresent).orElse(null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private <A extends Annotation> MergedAnnotation<A> findMergedAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) {
/*  749 */     Class<?> beanType = getType(beanName, allowFactoryBeanInit);
/*  750 */     if (beanType != null) {
/*      */       
/*  752 */       MergedAnnotation<A> annotation = MergedAnnotations.from(beanType, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType);
/*  753 */       if (annotation.isPresent()) {
/*  754 */         return annotation;
/*      */       }
/*      */     } 
/*  757 */     if (containsBeanDefinition(beanName)) {
/*  758 */       RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
/*      */       
/*  760 */       if (bd.hasBeanClass()) {
/*  761 */         Class<?> beanClass = bd.getBeanClass();
/*  762 */         if (beanClass != beanType) {
/*      */           
/*  764 */           MergedAnnotation<A> annotation = MergedAnnotations.from(beanClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType);
/*  765 */           if (annotation.isPresent()) {
/*  766 */             return annotation;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/*  771 */       Method factoryMethod = bd.getResolvedFactoryMethod();
/*  772 */       if (factoryMethod != null) {
/*      */         
/*  774 */         MergedAnnotation<A> annotation = MergedAnnotations.from(factoryMethod, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType);
/*  775 */         if (annotation.isPresent()) {
/*  776 */           return annotation;
/*      */         }
/*      */       } 
/*      */     } 
/*  780 */     return MergedAnnotation.missing();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue) {
/*  790 */     Assert.notNull(dependencyType, "Dependency type must not be null");
/*  791 */     if (autowiredValue != null) {
/*  792 */       if (!(autowiredValue instanceof ObjectFactory) && !dependencyType.isInstance(autowiredValue)) {
/*  793 */         throw new IllegalArgumentException("Value [" + autowiredValue + "] does not implement specified dependency type [" + dependencyType
/*  794 */             .getName() + "]");
/*      */       }
/*  796 */       this.resolvableDependencies.put(dependencyType, autowiredValue);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException {
/*  804 */     return isAutowireCandidate(beanName, descriptor, getAutowireCandidateResolver());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {
/*  819 */     String bdName = BeanFactoryUtils.transformedBeanName(beanName);
/*  820 */     if (containsBeanDefinition(bdName)) {
/*  821 */       return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(bdName), descriptor, resolver);
/*      */     }
/*  823 */     if (containsSingleton(beanName)) {
/*  824 */       return isAutowireCandidate(beanName, new RootBeanDefinition(getType(beanName)), descriptor, resolver);
/*      */     }
/*      */     
/*  827 */     BeanFactory parent = getParentBeanFactory();
/*  828 */     if (parent instanceof DefaultListableBeanFactory)
/*      */     {
/*  830 */       return ((DefaultListableBeanFactory)parent).isAutowireCandidate(beanName, descriptor, resolver);
/*      */     }
/*  832 */     if (parent instanceof ConfigurableListableBeanFactory)
/*      */     {
/*  834 */       return ((ConfigurableListableBeanFactory)parent).isAutowireCandidate(beanName, descriptor);
/*      */     }
/*      */     
/*  837 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) {
/*  853 */     String bdName = BeanFactoryUtils.transformedBeanName(beanName);
/*  854 */     resolveBeanClass(mbd, bdName, new Class[0]);
/*  855 */     if (mbd.isFactoryMethodUnique && mbd.factoryMethodToIntrospect == null) {
/*  856 */       (new ConstructorResolver(this)).resolveFactoryMethodIfPossible(mbd);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  861 */     BeanDefinitionHolder holder = beanName.equals(bdName) ? this.mergedBeanDefinitionHolders.computeIfAbsent(beanName, key -> new BeanDefinitionHolder(mbd, beanName, getAliases(bdName))) : new BeanDefinitionHolder(mbd, beanName, getAliases(bdName));
/*  862 */     return resolver.isAutowireCandidate(holder, descriptor);
/*      */   }
/*      */ 
/*      */   
/*      */   public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
/*  867 */     BeanDefinition bd = this.beanDefinitionMap.get(beanName);
/*  868 */     if (bd == null) {
/*  869 */       if (this.logger.isTraceEnabled()) {
/*  870 */         this.logger.trace("No bean named '" + beanName + "' found in " + this);
/*      */       }
/*  872 */       throw new NoSuchBeanDefinitionException(beanName);
/*      */     } 
/*  874 */     return bd;
/*      */   }
/*      */ 
/*      */   
/*      */   public Iterator<String> getBeanNamesIterator() {
/*  879 */     CompositeIterator<String> iterator = new CompositeIterator();
/*  880 */     iterator.add(this.beanDefinitionNames.iterator());
/*  881 */     iterator.add(this.manualSingletonNames.iterator());
/*  882 */     return (Iterator<String>)iterator;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void clearMergedBeanDefinition(String beanName) {
/*  887 */     super.clearMergedBeanDefinition(beanName);
/*  888 */     this.mergedBeanDefinitionHolders.remove(beanName);
/*      */   }
/*      */ 
/*      */   
/*      */   public void clearMetadataCache() {
/*  893 */     super.clearMetadataCache();
/*  894 */     this.mergedBeanDefinitionHolders.clear();
/*  895 */     clearByTypeCache();
/*      */   }
/*      */ 
/*      */   
/*      */   public void freezeConfiguration() {
/*  900 */     this.configurationFrozen = true;
/*  901 */     this.frozenBeanDefinitionNames = StringUtils.toStringArray(this.beanDefinitionNames);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isConfigurationFrozen() {
/*  906 */     return this.configurationFrozen;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isBeanEligibleForMetadataCaching(String beanName) {
/*  916 */     return (this.configurationFrozen || super.isBeanEligibleForMetadataCaching(beanName));
/*      */   }
/*      */ 
/*      */   
/*      */   public void preInstantiateSingletons() throws BeansException {
/*  921 */     if (this.logger.isTraceEnabled()) {
/*  922 */       this.logger.trace("Pre-instantiating singletons in " + this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  927 */     List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);
/*      */ 
/*      */     
/*  930 */     for (String beanName : beanNames) {
/*  931 */       RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
/*  932 */       if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
/*  933 */         if (isFactoryBean(beanName)) {
/*  934 */           Object bean = getBean("&" + beanName);
/*  935 */           if (bean instanceof FactoryBean) {
/*  936 */             boolean isEagerInit; FactoryBean<?> factory = (FactoryBean)bean;
/*      */             
/*  938 */             if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
/*  939 */               isEagerInit = ((Boolean)AccessController.<Boolean>doPrivileged((SmartFactoryBean)factory::isEagerInit, 
/*      */                   
/*  941 */                   getAccessControlContext())).booleanValue();
/*      */             }
/*      */             else {
/*      */               
/*  945 */               isEagerInit = (factory instanceof SmartFactoryBean && ((SmartFactoryBean)factory).isEagerInit());
/*      */             } 
/*  947 */             if (isEagerInit) {
/*  948 */               getBean(beanName);
/*      */             }
/*      */           } 
/*      */           continue;
/*      */         } 
/*  953 */         getBean(beanName);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  959 */     for (String beanName : beanNames) {
/*  960 */       Object singletonInstance = getSingleton(beanName);
/*  961 */       if (singletonInstance instanceof SmartInitializingSingleton) {
/*      */         
/*  963 */         StartupStep smartInitialize = getApplicationStartup().start("spring.beans.smart-initialize").tag("beanName", beanName);
/*  964 */         SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton)singletonInstance;
/*  965 */         if (System.getSecurityManager() != null) {
/*  966 */           AccessController.doPrivileged(() -> {
/*      */                 smartSingleton.afterSingletonsInstantiated();
/*      */                 return null;
/*  969 */               }getAccessControlContext());
/*      */         } else {
/*      */           
/*  972 */           smartSingleton.afterSingletonsInstantiated();
/*      */         } 
/*  974 */         smartInitialize.end();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
/*  988 */     Assert.hasText(beanName, "Bean name must not be empty");
/*  989 */     Assert.notNull(beanDefinition, "BeanDefinition must not be null");
/*      */     
/*  991 */     if (beanDefinition instanceof AbstractBeanDefinition) {
/*      */       try {
/*  993 */         ((AbstractBeanDefinition)beanDefinition).validate();
/*      */       }
/*  995 */       catch (BeanDefinitionValidationException ex) {
/*  996 */         throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName, "Validation of bean definition failed", ex);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1001 */     BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
/* 1002 */     if (existingDefinition != null) {
/* 1003 */       if (!isAllowBeanDefinitionOverriding()) {
/* 1004 */         throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
/*      */       }
/* 1006 */       if (existingDefinition.getRole() < beanDefinition.getRole()) {
/*      */         
/* 1008 */         if (this.logger.isInfoEnabled()) {
/* 1009 */           this.logger.info("Overriding user-defined bean definition for bean '" + beanName + "' with a framework-generated bean definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
/*      */         
/*      */         }
/*      */       
/*      */       }
/* 1014 */       else if (!beanDefinition.equals(existingDefinition)) {
/* 1015 */         if (this.logger.isDebugEnabled()) {
/* 1016 */           this.logger.debug("Overriding bean definition for bean '" + beanName + "' with a different definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
/*      */ 
/*      */         
/*      */         }
/*      */       
/*      */       }
/* 1022 */       else if (this.logger.isTraceEnabled()) {
/* 1023 */         this.logger.trace("Overriding bean definition for bean '" + beanName + "' with an equivalent definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1028 */       this.beanDefinitionMap.put(beanName, beanDefinition);
/*      */     } else {
/*      */       
/* 1031 */       if (hasBeanCreationStarted()) {
/*      */         
/* 1033 */         synchronized (this.beanDefinitionMap) {
/* 1034 */           this.beanDefinitionMap.put(beanName, beanDefinition);
/* 1035 */           List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
/* 1036 */           updatedDefinitions.addAll(this.beanDefinitionNames);
/* 1037 */           updatedDefinitions.add(beanName);
/* 1038 */           this.beanDefinitionNames = updatedDefinitions;
/* 1039 */           removeManualSingletonName(beanName);
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1044 */         this.beanDefinitionMap.put(beanName, beanDefinition);
/* 1045 */         this.beanDefinitionNames.add(beanName);
/* 1046 */         removeManualSingletonName(beanName);
/*      */       } 
/* 1048 */       this.frozenBeanDefinitionNames = null;
/*      */     } 
/*      */     
/* 1051 */     if (existingDefinition != null || containsSingleton(beanName)) {
/* 1052 */       resetBeanDefinition(beanName);
/*      */     }
/* 1054 */     else if (isConfigurationFrozen()) {
/* 1055 */       clearByTypeCache();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
/* 1061 */     Assert.hasText(beanName, "'beanName' must not be empty");
/*      */     
/* 1063 */     BeanDefinition bd = this.beanDefinitionMap.remove(beanName);
/* 1064 */     if (bd == null) {
/* 1065 */       if (this.logger.isTraceEnabled()) {
/* 1066 */         this.logger.trace("No bean named '" + beanName + "' found in " + this);
/*      */       }
/* 1068 */       throw new NoSuchBeanDefinitionException(beanName);
/*      */     } 
/*      */     
/* 1071 */     if (hasBeanCreationStarted()) {
/*      */       
/* 1073 */       synchronized (this.beanDefinitionMap) {
/* 1074 */         List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames);
/* 1075 */         updatedDefinitions.remove(beanName);
/* 1076 */         this.beanDefinitionNames = updatedDefinitions;
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/* 1081 */       this.beanDefinitionNames.remove(beanName);
/*      */     } 
/* 1083 */     this.frozenBeanDefinitionNames = null;
/*      */     
/* 1085 */     resetBeanDefinition(beanName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void resetBeanDefinition(String beanName) {
/* 1101 */     clearMergedBeanDefinition(beanName);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1106 */     destroySingleton(beanName);
/*      */ 
/*      */     
/* 1109 */     for (MergedBeanDefinitionPostProcessor processor : (getBeanPostProcessorCache()).mergedDefinition) {
/* 1110 */       processor.resetBeanDefinition(beanName);
/*      */     }
/*      */ 
/*      */     
/* 1114 */     for (String bdName : this.beanDefinitionNames) {
/* 1115 */       if (!beanName.equals(bdName)) {
/* 1116 */         BeanDefinition bd = this.beanDefinitionMap.get(bdName);
/*      */         
/* 1118 */         if (bd != null && beanName.equals(bd.getParentName())) {
/* 1119 */           resetBeanDefinition(bdName);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean allowAliasOverriding() {
/* 1130 */     return isAllowBeanDefinitionOverriding();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkForAliasCircle(String name, String alias) {
/* 1138 */     super.checkForAliasCircle(name, alias);
/* 1139 */     if (!isAllowBeanDefinitionOverriding() && containsBeanDefinition(alias)) {
/* 1140 */       throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" + name + "': Alias would override bean definition '" + alias + "'");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
/* 1147 */     super.registerSingleton(beanName, singletonObject);
/* 1148 */     updateManualSingletonNames(set -> set.add(beanName), set -> !this.beanDefinitionMap.containsKey(beanName));
/* 1149 */     clearByTypeCache();
/*      */   }
/*      */ 
/*      */   
/*      */   public void destroySingletons() {
/* 1154 */     super.destroySingletons();
/* 1155 */     updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
/* 1156 */     clearByTypeCache();
/*      */   }
/*      */ 
/*      */   
/*      */   public void destroySingleton(String beanName) {
/* 1161 */     super.destroySingleton(beanName);
/* 1162 */     removeManualSingletonName(beanName);
/* 1163 */     clearByTypeCache();
/*      */   }
/*      */   
/*      */   private void removeManualSingletonName(String beanName) {
/* 1167 */     updateManualSingletonNames(set -> set.remove(beanName), set -> set.contains(beanName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void updateManualSingletonNames(Consumer<Set<String>> action, Predicate<Set<String>> condition) {
/* 1177 */     if (hasBeanCreationStarted()) {
/*      */       
/* 1179 */       synchronized (this.beanDefinitionMap) {
/* 1180 */         if (condition.test(this.manualSingletonNames)) {
/* 1181 */           Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
/* 1182 */           action.accept(updatedSingletons);
/* 1183 */           this.manualSingletonNames = updatedSingletons;
/*      */         }
/*      */       
/*      */       }
/*      */     
/*      */     }
/* 1189 */     else if (condition.test(this.manualSingletonNames)) {
/* 1190 */       action.accept(this.manualSingletonNames);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void clearByTypeCache() {
/* 1199 */     this.allBeanNamesByType.clear();
/* 1200 */     this.singletonBeanNamesByType.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
/* 1210 */     Assert.notNull(requiredType, "Required type must not be null");
/* 1211 */     NamedBeanHolder<T> namedBean = resolveNamedBean(ResolvableType.forRawClass(requiredType), (Object[])null, false);
/* 1212 */     if (namedBean != null) {
/* 1213 */       return namedBean;
/*      */     }
/* 1215 */     BeanFactory parent = getParentBeanFactory();
/* 1216 */     if (parent instanceof AutowireCapableBeanFactory) {
/* 1217 */       return ((AutowireCapableBeanFactory)parent).resolveNamedBean(requiredType);
/*      */     }
/* 1219 */     throw new NoSuchBeanDefinitionException(requiredType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private <T> NamedBeanHolder<T> resolveNamedBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) throws BeansException {
/* 1227 */     Assert.notNull(requiredType, "Required type must not be null");
/* 1228 */     String[] candidateNames = getBeanNamesForType(requiredType);
/*      */     
/* 1230 */     if (candidateNames.length > 1) {
/* 1231 */       List<String> autowireCandidates = new ArrayList<>(candidateNames.length);
/* 1232 */       for (String beanName : candidateNames) {
/* 1233 */         if (!containsBeanDefinition(beanName) || getBeanDefinition(beanName).isAutowireCandidate()) {
/* 1234 */           autowireCandidates.add(beanName);
/*      */         }
/*      */       } 
/* 1237 */       if (!autowireCandidates.isEmpty()) {
/* 1238 */         candidateNames = StringUtils.toStringArray(autowireCandidates);
/*      */       }
/*      */     } 
/*      */     
/* 1242 */     if (candidateNames.length == 1) {
/* 1243 */       return resolveNamedBean(candidateNames[0], requiredType, args);
/*      */     }
/* 1245 */     if (candidateNames.length > 1) {
/* 1246 */       Map<String, Object> candidates = CollectionUtils.newLinkedHashMap(candidateNames.length);
/* 1247 */       for (String beanName : candidateNames) {
/* 1248 */         if (containsSingleton(beanName) && args == null) {
/* 1249 */           Object beanInstance = getBean(beanName);
/* 1250 */           candidates.put(beanName, (beanInstance instanceof NullBean) ? null : beanInstance);
/*      */         } else {
/*      */           
/* 1253 */           candidates.put(beanName, getType(beanName));
/*      */         } 
/*      */       } 
/* 1256 */       String candidateName = determinePrimaryCandidate(candidates, requiredType.toClass());
/* 1257 */       if (candidateName == null) {
/* 1258 */         candidateName = determineHighestPriorityCandidate(candidates, requiredType.toClass());
/*      */       }
/* 1260 */       if (candidateName != null) {
/* 1261 */         Object beanInstance = candidates.get(candidateName);
/* 1262 */         if (beanInstance == null) {
/* 1263 */           return null;
/*      */         }
/* 1265 */         if (beanInstance instanceof Class) {
/* 1266 */           return resolveNamedBean(candidateName, requiredType, args);
/*      */         }
/* 1268 */         return new NamedBeanHolder(candidateName, beanInstance);
/*      */       } 
/* 1270 */       if (!nonUniqueAsNull) {
/* 1271 */         throw new NoUniqueBeanDefinitionException(requiredType, candidates.keySet());
/*      */       }
/*      */     } 
/*      */     
/* 1275 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private <T> NamedBeanHolder<T> resolveNamedBean(String beanName, ResolvableType requiredType, @Nullable Object[] args) throws BeansException {
/* 1282 */     Object bean = getBean(beanName, (Class<?>)null, args);
/* 1283 */     if (bean instanceof NullBean) {
/* 1284 */       return null;
/*      */     }
/* 1286 */     return new NamedBeanHolder(beanName, adaptBeanInstance(beanName, bean, requiredType.toClass()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
/* 1294 */     descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
/* 1295 */     if (Optional.class == descriptor.getDependencyType()) {
/* 1296 */       return createOptionalDependency(descriptor, requestingBeanName, new Object[0]);
/*      */     }
/* 1298 */     if (ObjectFactory.class == descriptor.getDependencyType() || ObjectProvider.class == descriptor
/* 1299 */       .getDependencyType()) {
/* 1300 */       return new DependencyObjectProvider(descriptor, requestingBeanName);
/*      */     }
/* 1302 */     if (javaxInjectProviderClass == descriptor.getDependencyType()) {
/* 1303 */       return (new Jsr330Factory()).createDependencyProvider(descriptor, requestingBeanName);
/*      */     }
/*      */     
/* 1306 */     Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(descriptor, requestingBeanName);
/*      */     
/* 1308 */     if (result == null) {
/* 1309 */       result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
/*      */     }
/* 1311 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
/* 1319 */     InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint((InjectionPoint)descriptor);
/*      */     try {
/* 1321 */       Object autowiredBeanName, instanceCandidate, shortcut = descriptor.resolveShortcut((BeanFactory)this);
/* 1322 */       if (shortcut != null) {
/* 1323 */         return shortcut;
/*      */       }
/*      */       
/* 1326 */       Class<?> type = descriptor.getDependencyType();
/* 1327 */       Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);
/* 1328 */       if (value != null) {
/* 1329 */         if (value instanceof String) {
/* 1330 */           String strVal = resolveEmbeddedValue((String)value);
/*      */           
/* 1332 */           BeanDefinition bd = (beanName != null && containsBean(beanName)) ? getMergedBeanDefinition(beanName) : null;
/* 1333 */           value = evaluateBeanDefinitionString(strVal, bd);
/*      */         } 
/* 1335 */         TypeConverter converter = (typeConverter != null) ? typeConverter : getTypeConverter();
/*      */         try {
/* 1337 */           return converter.convertIfNecessary(value, type, descriptor.getTypeDescriptor());
/*      */         }
/* 1339 */         catch (UnsupportedOperationException ex) {
/*      */ 
/*      */ 
/*      */           
/* 1343 */           autowiredBeanName = (descriptor.getField() != null) ? converter.convertIfNecessary(value, type, descriptor.getField()) : converter.convertIfNecessary(value, type, descriptor.getMethodParameter());
/*      */           return autowiredBeanName;
/*      */         } 
/*      */       } 
/* 1347 */       Object multipleBeans = resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
/* 1348 */       if (multipleBeans != null) {
/* 1349 */         return multipleBeans;
/*      */       }
/*      */       
/* 1352 */       Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
/* 1353 */       if (matchingBeans.isEmpty()) {
/* 1354 */         if (isRequired(descriptor)) {
/* 1355 */           raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
/*      */         }
/* 1357 */         autowiredBeanName = null; return autowiredBeanName;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1363 */       if (matchingBeans.size() > 1) {
/* 1364 */         autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
/* 1365 */         if (autowiredBeanName == null) {
/* 1366 */           if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
/* 1367 */             return descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1373 */           return null;
/*      */         } 
/*      */         
/* 1376 */         instanceCandidate = matchingBeans.get(autowiredBeanName);
/*      */       }
/*      */       else {
/*      */         
/* 1380 */         Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
/* 1381 */         autowiredBeanName = entry.getKey();
/* 1382 */         instanceCandidate = entry.getValue();
/*      */       } 
/*      */       
/* 1385 */       if (autowiredBeanNames != null) {
/* 1386 */         autowiredBeanNames.add(autowiredBeanName);
/*      */       }
/* 1388 */       if (instanceCandidate instanceof Class) {
/* 1389 */         instanceCandidate = descriptor.resolveCandidate((String)autowiredBeanName, type, (BeanFactory)this);
/*      */       }
/* 1391 */       Object result = instanceCandidate;
/* 1392 */       if (result instanceof NullBean) {
/* 1393 */         if (isRequired(descriptor)) {
/* 1394 */           raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
/*      */         }
/* 1396 */         result = null;
/*      */       } 
/* 1398 */       if (!ClassUtils.isAssignableValue(type, result)) {
/* 1399 */         throw new BeanNotOfRequiredTypeException(autowiredBeanName, type, instanceCandidate.getClass());
/*      */       }
/* 1401 */       return result;
/*      */     } finally {
/*      */       
/* 1404 */       ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private Object resolveMultipleBeans(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {
/* 1412 */     Class<?> type = descriptor.getDependencyType();
/*      */     
/* 1414 */     if (descriptor instanceof StreamDependencyDescriptor) {
/* 1415 */       Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
/* 1416 */       if (autowiredBeanNames != null) {
/* 1417 */         autowiredBeanNames.addAll(matchingBeans.keySet());
/*      */       }
/*      */ 
/*      */       
/* 1421 */       Stream<Object> stream = matchingBeans.keySet().stream().map(name -> descriptor.resolveCandidate(name, type, (BeanFactory)this)).filter(bean -> !(bean instanceof NullBean));
/* 1422 */       if (((StreamDependencyDescriptor)descriptor).isOrdered()) {
/* 1423 */         stream = stream.sorted(adaptOrderComparator(matchingBeans));
/*      */       }
/* 1425 */       return stream;
/*      */     } 
/* 1427 */     if (type.isArray()) {
/* 1428 */       Class<?> componentType = type.getComponentType();
/* 1429 */       ResolvableType resolvableType = descriptor.getResolvableType();
/* 1430 */       Class<?> resolvedArrayType = resolvableType.resolve(type);
/* 1431 */       if (resolvedArrayType != type) {
/* 1432 */         componentType = resolvableType.getComponentType().resolve();
/*      */       }
/* 1434 */       if (componentType == null) {
/* 1435 */         return null;
/*      */       }
/* 1437 */       Map<String, Object> matchingBeans = findAutowireCandidates(beanName, componentType, new MultiElementDescriptor(descriptor));
/*      */       
/* 1439 */       if (matchingBeans.isEmpty()) {
/* 1440 */         return null;
/*      */       }
/* 1442 */       if (autowiredBeanNames != null) {
/* 1443 */         autowiredBeanNames.addAll(matchingBeans.keySet());
/*      */       }
/* 1445 */       TypeConverter converter = (typeConverter != null) ? typeConverter : getTypeConverter();
/* 1446 */       Object result = converter.convertIfNecessary(matchingBeans.values(), resolvedArrayType);
/* 1447 */       if (result instanceof Object[]) {
/* 1448 */         Comparator<Object> comparator = adaptDependencyComparator(matchingBeans);
/* 1449 */         if (comparator != null) {
/* 1450 */           Arrays.sort((Object[])result, comparator);
/*      */         }
/*      */       } 
/* 1453 */       return result;
/*      */     } 
/* 1455 */     if (Collection.class.isAssignableFrom(type) && type.isInterface()) {
/* 1456 */       Class<?> elementType = descriptor.getResolvableType().asCollection().resolveGeneric(new int[0]);
/* 1457 */       if (elementType == null) {
/* 1458 */         return null;
/*      */       }
/* 1460 */       Map<String, Object> matchingBeans = findAutowireCandidates(beanName, elementType, new MultiElementDescriptor(descriptor));
/*      */       
/* 1462 */       if (matchingBeans.isEmpty()) {
/* 1463 */         return null;
/*      */       }
/* 1465 */       if (autowiredBeanNames != null) {
/* 1466 */         autowiredBeanNames.addAll(matchingBeans.keySet());
/*      */       }
/* 1468 */       TypeConverter converter = (typeConverter != null) ? typeConverter : getTypeConverter();
/* 1469 */       Object result = converter.convertIfNecessary(matchingBeans.values(), type);
/* 1470 */       if (result instanceof List && (
/* 1471 */         (List)result).size() > 1) {
/* 1472 */         Comparator<Object> comparator = adaptDependencyComparator(matchingBeans);
/* 1473 */         if (comparator != null) {
/* 1474 */           ((List<Object>)result).sort(comparator);
/*      */         }
/*      */       } 
/*      */       
/* 1478 */       return result;
/*      */     } 
/* 1480 */     if (Map.class == type) {
/* 1481 */       ResolvableType mapType = descriptor.getResolvableType().asMap();
/* 1482 */       Class<?> keyType = mapType.resolveGeneric(new int[] { 0 });
/* 1483 */       if (String.class != keyType) {
/* 1484 */         return null;
/*      */       }
/* 1486 */       Class<?> valueType = mapType.resolveGeneric(new int[] { 1 });
/* 1487 */       if (valueType == null) {
/* 1488 */         return null;
/*      */       }
/* 1490 */       Map<String, Object> matchingBeans = findAutowireCandidates(beanName, valueType, new MultiElementDescriptor(descriptor));
/*      */       
/* 1492 */       if (matchingBeans.isEmpty()) {
/* 1493 */         return null;
/*      */       }
/* 1495 */       if (autowiredBeanNames != null) {
/* 1496 */         autowiredBeanNames.addAll(matchingBeans.keySet());
/*      */       }
/* 1498 */       return matchingBeans;
/*      */     } 
/*      */     
/* 1501 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isRequired(DependencyDescriptor descriptor) {
/* 1506 */     return getAutowireCandidateResolver().isRequired(descriptor);
/*      */   }
/*      */   
/*      */   private boolean indicatesMultipleBeans(Class<?> type) {
/* 1510 */     return (type.isArray() || (type.isInterface() && (Collection.class
/* 1511 */       .isAssignableFrom(type) || Map.class.isAssignableFrom(type))));
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   private Comparator<Object> adaptDependencyComparator(Map<String, ?> matchingBeans) {
/* 1516 */     Comparator<Object> comparator = getDependencyComparator();
/* 1517 */     if (comparator instanceof OrderComparator) {
/* 1518 */       return ((OrderComparator)comparator).withSourceProvider(
/* 1519 */           createFactoryAwareOrderSourceProvider(matchingBeans));
/*      */     }
/*      */     
/* 1522 */     return comparator;
/*      */   }
/*      */ 
/*      */   
/*      */   private Comparator<Object> adaptOrderComparator(Map<String, ?> matchingBeans) {
/* 1527 */     Comparator<Object> dependencyComparator = getDependencyComparator();
/* 1528 */     OrderComparator comparator = (dependencyComparator instanceof OrderComparator) ? (OrderComparator)dependencyComparator : OrderComparator.INSTANCE;
/*      */     
/* 1530 */     return comparator.withSourceProvider(createFactoryAwareOrderSourceProvider(matchingBeans));
/*      */   }
/*      */   
/*      */   private OrderComparator.OrderSourceProvider createFactoryAwareOrderSourceProvider(Map<String, ?> beans) {
/* 1534 */     IdentityHashMap<Object, String> instancesToBeanNames = new IdentityHashMap<>();
/* 1535 */     beans.forEach((beanName, instance) -> (String)instancesToBeanNames.put(instance, beanName));
/* 1536 */     return new FactoryAwareOrderSourceProvider(instancesToBeanNames);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Map<String, Object> findAutowireCandidates(@Nullable String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {
/* 1555 */     String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)this, requiredType, true, descriptor
/* 1556 */         .isEager());
/* 1557 */     Map<String, Object> result = CollectionUtils.newLinkedHashMap(candidateNames.length);
/* 1558 */     for (Map.Entry<Class<?>, Object> classObjectEntry : this.resolvableDependencies.entrySet()) {
/* 1559 */       Class<?> autowiringType = classObjectEntry.getKey();
/* 1560 */       if (autowiringType.isAssignableFrom(requiredType)) {
/* 1561 */         Object autowiringValue = classObjectEntry.getValue();
/* 1562 */         autowiringValue = AutowireUtils.resolveAutowiringValue(autowiringValue, requiredType);
/* 1563 */         if (requiredType.isInstance(autowiringValue)) {
/* 1564 */           result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1569 */     for (String candidate : candidateNames) {
/* 1570 */       if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, descriptor)) {
/* 1571 */         addCandidateEntry(result, candidate, descriptor, requiredType);
/*      */       }
/*      */     } 
/* 1574 */     if (result.isEmpty()) {
/* 1575 */       boolean multiple = indicatesMultipleBeans(requiredType);
/*      */       
/* 1577 */       DependencyDescriptor fallbackDescriptor = descriptor.forFallbackMatch();
/* 1578 */       for (String candidate : candidateNames) {
/* 1579 */         if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, fallbackDescriptor) && (!multiple || 
/* 1580 */           getAutowireCandidateResolver().hasQualifier(descriptor))) {
/* 1581 */           addCandidateEntry(result, candidate, descriptor, requiredType);
/*      */         }
/*      */       } 
/* 1584 */       if (result.isEmpty() && !multiple)
/*      */       {
/*      */         
/* 1587 */         for (String candidate : candidateNames) {
/* 1588 */           if (isSelfReference(beanName, candidate) && (!(descriptor instanceof MultiElementDescriptor) || 
/* 1589 */             !beanName.equals(candidate)) && 
/* 1590 */             isAutowireCandidate(candidate, fallbackDescriptor)) {
/* 1591 */             addCandidateEntry(result, candidate, descriptor, requiredType);
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/* 1596 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addCandidateEntry(Map<String, Object> candidates, String candidateName, DependencyDescriptor descriptor, Class<?> requiredType) {
/* 1606 */     if (descriptor instanceof MultiElementDescriptor) {
/* 1607 */       Object beanInstance = descriptor.resolveCandidate(candidateName, requiredType, (BeanFactory)this);
/* 1608 */       if (!(beanInstance instanceof NullBean)) {
/* 1609 */         candidates.put(candidateName, beanInstance);
/*      */       }
/*      */     }
/* 1612 */     else if (containsSingleton(candidateName) || (descriptor instanceof StreamDependencyDescriptor && ((StreamDependencyDescriptor)descriptor)
/* 1613 */       .isOrdered())) {
/* 1614 */       Object beanInstance = descriptor.resolveCandidate(candidateName, requiredType, (BeanFactory)this);
/* 1615 */       candidates.put(candidateName, (beanInstance instanceof NullBean) ? null : beanInstance);
/*      */     } else {
/*      */       
/* 1618 */       candidates.put(candidateName, getType(candidateName));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected String determineAutowireCandidate(Map<String, Object> candidates, DependencyDescriptor descriptor) {
/* 1632 */     Class<?> requiredType = descriptor.getDependencyType();
/* 1633 */     String primaryCandidate = determinePrimaryCandidate(candidates, requiredType);
/* 1634 */     if (primaryCandidate != null) {
/* 1635 */       return primaryCandidate;
/*      */     }
/* 1637 */     String priorityCandidate = determineHighestPriorityCandidate(candidates, requiredType);
/* 1638 */     if (priorityCandidate != null) {
/* 1639 */       return priorityCandidate;
/*      */     }
/*      */     
/* 1642 */     for (Map.Entry<String, Object> entry : candidates.entrySet()) {
/* 1643 */       String candidateName = entry.getKey();
/* 1644 */       Object beanInstance = entry.getValue();
/* 1645 */       if ((beanInstance != null && this.resolvableDependencies.containsValue(beanInstance)) || 
/* 1646 */         matchesBeanName(candidateName, descriptor.getDependencyName())) {
/* 1647 */         return candidateName;
/*      */       }
/*      */     } 
/* 1650 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected String determinePrimaryCandidate(Map<String, Object> candidates, Class<?> requiredType) {
/* 1663 */     String primaryBeanName = null;
/* 1664 */     for (Map.Entry<String, Object> entry : candidates.entrySet()) {
/* 1665 */       String candidateBeanName = entry.getKey();
/* 1666 */       Object beanInstance = entry.getValue();
/* 1667 */       if (isPrimary(candidateBeanName, beanInstance)) {
/* 1668 */         if (primaryBeanName != null) {
/* 1669 */           boolean candidateLocal = containsBeanDefinition(candidateBeanName);
/* 1670 */           boolean primaryLocal = containsBeanDefinition(primaryBeanName);
/* 1671 */           if (candidateLocal && primaryLocal) {
/* 1672 */             throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "more than one 'primary' bean found among candidates: " + candidates
/* 1673 */                 .keySet());
/*      */           }
/* 1675 */           if (candidateLocal) {
/* 1676 */             primaryBeanName = candidateBeanName;
/*      */           }
/*      */           continue;
/*      */         } 
/* 1680 */         primaryBeanName = candidateBeanName;
/*      */       } 
/*      */     } 
/*      */     
/* 1684 */     return primaryBeanName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected String determineHighestPriorityCandidate(Map<String, Object> candidates, Class<?> requiredType) {
/* 1701 */     String highestPriorityBeanName = null;
/* 1702 */     Integer highestPriority = null;
/* 1703 */     for (Map.Entry<String, Object> entry : candidates.entrySet()) {
/* 1704 */       String candidateBeanName = entry.getKey();
/* 1705 */       Object beanInstance = entry.getValue();
/* 1706 */       if (beanInstance != null) {
/* 1707 */         Integer candidatePriority = getPriority(beanInstance);
/* 1708 */         if (candidatePriority != null) {
/* 1709 */           if (highestPriorityBeanName != null) {
/* 1710 */             if (candidatePriority.equals(highestPriority)) {
/* 1711 */               throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "Multiple beans found with the same priority ('" + highestPriority + "') among candidates: " + candidates
/*      */                   
/* 1713 */                   .keySet());
/*      */             }
/* 1715 */             if (candidatePriority.intValue() < highestPriority.intValue()) {
/* 1716 */               highestPriorityBeanName = candidateBeanName;
/* 1717 */               highestPriority = candidatePriority;
/*      */             } 
/*      */             continue;
/*      */           } 
/* 1721 */           highestPriorityBeanName = candidateBeanName;
/* 1722 */           highestPriority = candidatePriority;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1727 */     return highestPriorityBeanName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isPrimary(String beanName, Object beanInstance) {
/* 1738 */     String transformedBeanName = transformedBeanName(beanName);
/* 1739 */     if (containsBeanDefinition(transformedBeanName)) {
/* 1740 */       return getMergedLocalBeanDefinition(transformedBeanName).isPrimary();
/*      */     }
/* 1742 */     BeanFactory parent = getParentBeanFactory();
/* 1743 */     return (parent instanceof DefaultListableBeanFactory && ((DefaultListableBeanFactory)parent)
/* 1744 */       .isPrimary(transformedBeanName, beanInstance));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected Integer getPriority(Object beanInstance) {
/* 1761 */     Comparator<Object> comparator = getDependencyComparator();
/* 1762 */     if (comparator instanceof OrderComparator) {
/* 1763 */       return ((OrderComparator)comparator).getPriority(beanInstance);
/*      */     }
/* 1765 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean matchesBeanName(String beanName, @Nullable String candidateName) {
/* 1773 */     return (candidateName != null && (candidateName
/* 1774 */       .equals(beanName) || ObjectUtils.containsElement((Object[])getAliases(beanName), candidateName)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isSelfReference(@Nullable String beanName, @Nullable String candidateName) {
/* 1783 */     return (beanName != null && candidateName != null && (beanName
/* 1784 */       .equals(candidateName) || (containsBeanDefinition(candidateName) && beanName
/* 1785 */       .equals(getMergedLocalBeanDefinition(candidateName).getFactoryBeanName()))));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void raiseNoMatchingBeanFound(Class<?> type, ResolvableType resolvableType, DependencyDescriptor descriptor) throws BeansException {
/* 1795 */     checkBeanNotOfRequiredType(type, descriptor);
/*      */     
/* 1797 */     throw new NoSuchBeanDefinitionException(resolvableType, "expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: " + 
/*      */         
/* 1799 */         ObjectUtils.nullSafeToString(descriptor.getAnnotations()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkBeanNotOfRequiredType(Class<?> type, DependencyDescriptor descriptor) {
/* 1807 */     for (String beanName : this.beanDefinitionNames) {
/*      */       try {
/* 1809 */         RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
/* 1810 */         Class<?> targetType = mbd.getTargetType();
/* 1811 */         if (targetType != null && type.isAssignableFrom(targetType) && 
/* 1812 */           isAutowireCandidate(beanName, mbd, descriptor, getAutowireCandidateResolver()))
/*      */         {
/* 1814 */           Object beanInstance = getSingleton(beanName, false);
/*      */           
/* 1816 */           Class<?> beanType = (beanInstance != null && beanInstance.getClass() != NullBean.class) ? beanInstance.getClass() : predictBeanType(beanName, mbd, new Class[0]);
/* 1817 */           if (beanType != null && !type.isAssignableFrom(beanType)) {
/* 1818 */             throw new BeanNotOfRequiredTypeException(beanName, type, beanType);
/*      */           }
/*      */         }
/*      */       
/* 1822 */       } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1827 */     BeanFactory parent = getParentBeanFactory();
/* 1828 */     if (parent instanceof DefaultListableBeanFactory) {
/* 1829 */       ((DefaultListableBeanFactory)parent).checkBeanNotOfRequiredType(type, descriptor);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Optional<?> createOptionalDependency(DependencyDescriptor descriptor, @Nullable String beanName, Object... args) {
/* 1839 */     DependencyDescriptor descriptorToUse = new NestedDependencyDescriptor(descriptor)
/*      */       {
/*      */         public boolean isRequired() {
/* 1842 */           return false;
/*      */         }
/*      */         
/*      */         public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
/* 1846 */           return !ObjectUtils.isEmpty(args) ? beanFactory.getBean(beanName, args) : super
/* 1847 */             .resolveCandidate(beanName, requiredType, beanFactory);
/*      */         }
/*      */       };
/* 1850 */     Object result = doResolveDependency(descriptorToUse, beanName, (Set<String>)null, (TypeConverter)null);
/* 1851 */     return (result instanceof Optional) ? (Optional)result : Optional.ofNullable(result);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1857 */     StringBuilder sb = new StringBuilder(ObjectUtils.identityToString(this));
/* 1858 */     sb.append(": defining beans [");
/* 1859 */     sb.append(StringUtils.collectionToCommaDelimitedString(this.beanDefinitionNames));
/* 1860 */     sb.append("]; ");
/* 1861 */     BeanFactory parent = getParentBeanFactory();
/* 1862 */     if (parent == null) {
/* 1863 */       sb.append("root of factory hierarchy");
/*      */     } else {
/*      */       
/* 1866 */       sb.append("parent: ").append(ObjectUtils.identityToString(parent));
/*      */     } 
/* 1868 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
/* 1877 */     throw new NotSerializableException("DefaultListableBeanFactory itself is not deserializable - just a SerializedBeanFactoryReference is");
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object writeReplace() throws ObjectStreamException {
/* 1882 */     if (this.serializationId != null) {
/* 1883 */       return new SerializedBeanFactoryReference(this.serializationId);
/*      */     }
/*      */     
/* 1886 */     throw new NotSerializableException("DefaultListableBeanFactory has no serialization id");
/*      */   }
/*      */ 
/*      */   
/*      */   public DefaultListableBeanFactory() {}
/*      */ 
/*      */   
/*      */   private static class SerializedBeanFactoryReference
/*      */     implements Serializable
/*      */   {
/*      */     private final String id;
/*      */ 
/*      */     
/*      */     public SerializedBeanFactoryReference(String id) {
/* 1900 */       this.id = id;
/*      */     }
/*      */     
/*      */     private Object readResolve() {
/* 1904 */       Reference<?> ref = (Reference)DefaultListableBeanFactory.serializableFactories.get(this.id);
/* 1905 */       if (ref != null) {
/* 1906 */         Object result = ref.get();
/* 1907 */         if (result != null) {
/* 1908 */           return result;
/*      */         }
/*      */       } 
/*      */       
/* 1912 */       DefaultListableBeanFactory dummyFactory = new DefaultListableBeanFactory();
/* 1913 */       dummyFactory.serializationId = this.id;
/* 1914 */       return dummyFactory;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class NestedDependencyDescriptor
/*      */     extends DependencyDescriptor
/*      */   {
/*      */     public NestedDependencyDescriptor(DependencyDescriptor original) {
/* 1925 */       super(original);
/* 1926 */       increaseNestingLevel();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class MultiElementDescriptor
/*      */     extends NestedDependencyDescriptor
/*      */   {
/*      */     public MultiElementDescriptor(DependencyDescriptor original) {
/* 1937 */       super(original);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class StreamDependencyDescriptor
/*      */     extends DependencyDescriptor
/*      */   {
/*      */     private final boolean ordered;
/*      */ 
/*      */     
/*      */     public StreamDependencyDescriptor(DependencyDescriptor original, boolean ordered) {
/* 1950 */       super(original);
/* 1951 */       this.ordered = ordered;
/*      */     }
/*      */     
/*      */     public boolean isOrdered() {
/* 1955 */       return this.ordered;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static interface BeanObjectProvider<T>
/*      */     extends ObjectProvider<T>, Serializable {}
/*      */ 
/*      */   
/*      */   private class DependencyObjectProvider
/*      */     implements BeanObjectProvider<Object>
/*      */   {
/*      */     private final DependencyDescriptor descriptor;
/*      */     
/*      */     private final boolean optional;
/*      */     
/*      */     @Nullable
/*      */     private final String beanName;
/*      */ 
/*      */     
/*      */     public DependencyObjectProvider(@Nullable DependencyDescriptor descriptor, String beanName) {
/* 1977 */       this.descriptor = new DefaultListableBeanFactory.NestedDependencyDescriptor(descriptor);
/* 1978 */       this.optional = (this.descriptor.getDependencyType() == Optional.class);
/* 1979 */       this.beanName = beanName;
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getObject() throws BeansException {
/* 1984 */       if (this.optional) {
/* 1985 */         return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
/*      */       }
/*      */       
/* 1988 */       Object result = DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, (Set<String>)null, (TypeConverter)null);
/* 1989 */       if (result == null) {
/* 1990 */         throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
/*      */       }
/* 1992 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getObject(Object... args) throws BeansException {
/* 1998 */       if (this.optional) {
/* 1999 */         return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, args);
/*      */       }
/*      */       
/* 2002 */       DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor)
/*      */         {
/*      */           public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
/* 2005 */             return beanFactory.getBean(beanName, args);
/*      */           }
/*      */         };
/* 2008 */       Object result = DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, (Set<String>)null, (TypeConverter)null);
/* 2009 */       if (result == null) {
/* 2010 */         throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
/*      */       }
/* 2012 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public Object getIfAvailable() throws BeansException {
/*      */       try {
/* 2020 */         if (this.optional) {
/* 2021 */           return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
/*      */         }
/*      */         
/* 2024 */         DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor)
/*      */           {
/*      */             public boolean isRequired() {
/* 2027 */               return false;
/*      */             }
/*      */           };
/* 2030 */         return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, (Set<String>)null, (TypeConverter)null);
/*      */       
/*      */       }
/* 2033 */       catch (ScopeNotActiveException ex) {
/*      */         
/* 2035 */         return null;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void ifAvailable(Consumer<Object> dependencyConsumer) throws BeansException {
/* 2041 */       Object dependency = getIfAvailable();
/* 2042 */       if (dependency != null) {
/*      */         try {
/* 2044 */           dependencyConsumer.accept(dependency);
/*      */         }
/* 2046 */         catch (ScopeNotActiveException scopeNotActiveException) {}
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public Object getIfUnique() throws BeansException {
/* 2055 */       DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor)
/*      */         {
/*      */           public boolean isRequired() {
/* 2058 */             return false;
/*      */           }
/*      */ 
/*      */           
/*      */           @Nullable
/*      */           public Object resolveNotUnique(ResolvableType type, Map<String, Object> matchingBeans) {
/* 2064 */             return null;
/*      */           }
/*      */         };
/*      */       try {
/* 2068 */         if (this.optional) {
/* 2069 */           return DefaultListableBeanFactory.this.createOptionalDependency(descriptorToUse, this.beanName, new Object[0]);
/*      */         }
/*      */         
/* 2072 */         return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, (Set<String>)null, (TypeConverter)null);
/*      */       
/*      */       }
/* 2075 */       catch (ScopeNotActiveException ex) {
/*      */         
/* 2077 */         return null;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void ifUnique(Consumer<Object> dependencyConsumer) throws BeansException {
/* 2083 */       Object dependency = getIfUnique();
/* 2084 */       if (dependency != null) {
/*      */         try {
/* 2086 */           dependencyConsumer.accept(dependency);
/*      */         }
/* 2088 */         catch (ScopeNotActiveException scopeNotActiveException) {}
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     protected Object getValue() throws BeansException {
/* 2096 */       if (this.optional) {
/* 2097 */         return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
/*      */       }
/*      */       
/* 2100 */       return DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, (Set<String>)null, (TypeConverter)null);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Stream<Object> stream() {
/* 2106 */       return resolveStream(false);
/*      */     }
/*      */ 
/*      */     
/*      */     public Stream<Object> orderedStream() {
/* 2111 */       return resolveStream(true);
/*      */     }
/*      */ 
/*      */     
/*      */     private Stream<Object> resolveStream(boolean ordered) {
/* 2116 */       DependencyDescriptor descriptorToUse = new DefaultListableBeanFactory.StreamDependencyDescriptor(this.descriptor, ordered);
/* 2117 */       Object result = DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, (Set<String>)null, (TypeConverter)null);
/* 2118 */       return (result instanceof Stream) ? (Stream<Object>)result : Stream.<Object>of(result);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private class Jsr330Factory
/*      */     implements Serializable
/*      */   {
/*      */     private Jsr330Factory() {}
/*      */ 
/*      */     
/*      */     public Object createDependencyProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
/* 2131 */       return new Jsr330Provider(descriptor, beanName);
/*      */     }
/*      */     
/*      */     private class Jsr330Provider
/*      */       extends DefaultListableBeanFactory.DependencyObjectProvider implements Provider<Object> {
/*      */       public Jsr330Provider(@Nullable DependencyDescriptor descriptor, String beanName) {
/* 2137 */         super(descriptor, beanName);
/*      */       }
/*      */ 
/*      */       
/*      */       @Nullable
/*      */       public Object get() throws BeansException {
/* 2143 */         return getValue();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private class FactoryAwareOrderSourceProvider
/*      */     implements OrderComparator.OrderSourceProvider
/*      */   {
/*      */     private final Map<Object, String> instancesToBeanNames;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FactoryAwareOrderSourceProvider(Map<Object, String> instancesToBeanNames) {
/* 2161 */       this.instancesToBeanNames = instancesToBeanNames;
/*      */     }
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public Object getOrderSource(Object obj) {
/* 2167 */       String beanName = this.instancesToBeanNames.get(obj);
/* 2168 */       if (beanName == null || !DefaultListableBeanFactory.this.containsBeanDefinition(beanName)) {
/* 2169 */         return null;
/*      */       }
/* 2171 */       RootBeanDefinition beanDefinition = DefaultListableBeanFactory.this.getMergedLocalBeanDefinition(beanName);
/* 2172 */       List<Object> sources = new ArrayList(2);
/* 2173 */       Method factoryMethod = beanDefinition.getResolvedFactoryMethod();
/* 2174 */       if (factoryMethod != null) {
/* 2175 */         sources.add(factoryMethod);
/*      */       }
/* 2177 */       Class<?> targetType = beanDefinition.getTargetType();
/* 2178 */       if (targetType != null && targetType != obj.getClass()) {
/* 2179 */         sources.add(targetType);
/*      */       }
/* 2181 */       return sources.toArray();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/DefaultListableBeanFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */