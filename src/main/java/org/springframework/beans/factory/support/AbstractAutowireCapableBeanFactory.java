/*      */ package org.springframework.beans.factory.support;
/*      */ 
/*      */ import java.beans.PropertyDescriptor;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.function.Supplier;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.springframework.beans.BeanUtils;
/*      */ import org.springframework.beans.BeanWrapper;
/*      */ import org.springframework.beans.BeanWrapperImpl;
/*      */ import org.springframework.beans.BeansException;
/*      */ import org.springframework.beans.MutablePropertyValues;
/*      */ import org.springframework.beans.PropertyAccessorUtils;
/*      */ import org.springframework.beans.PropertyValue;
/*      */ import org.springframework.beans.PropertyValues;
/*      */ import org.springframework.beans.TypeConverter;
/*      */ import org.springframework.beans.factory.BeanClassLoaderAware;
/*      */ import org.springframework.beans.factory.BeanCreationException;
/*      */ import org.springframework.beans.factory.BeanCurrentlyInCreationException;
/*      */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*      */ import org.springframework.beans.factory.BeanFactory;
/*      */ import org.springframework.beans.factory.BeanFactoryAware;
/*      */ import org.springframework.beans.factory.BeanNameAware;
/*      */ import org.springframework.beans.factory.FactoryBean;
/*      */ import org.springframework.beans.factory.InitializingBean;
/*      */ import org.springframework.beans.factory.InjectionPoint;
/*      */ import org.springframework.beans.factory.UnsatisfiedDependencyException;
/*      */ import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
/*      */ import org.springframework.beans.factory.config.AutowiredPropertyMarker;
/*      */ import org.springframework.beans.factory.config.BeanDefinition;
/*      */ import org.springframework.beans.factory.config.BeanPostProcessor;
/*      */ import org.springframework.beans.factory.config.ConfigurableBeanFactory;
/*      */ import org.springframework.beans.factory.config.ConstructorArgumentValues;
/*      */ import org.springframework.beans.factory.config.DependencyDescriptor;
/*      */ import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
/*      */ import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
/*      */ import org.springframework.beans.factory.config.TypedStringValue;
/*      */ import org.springframework.core.AttributeAccessor;
/*      */ import org.springframework.core.DefaultParameterNameDiscoverer;
/*      */ import org.springframework.core.MethodParameter;
/*      */ import org.springframework.core.NamedThreadLocal;
/*      */ import org.springframework.core.NativeDetector;
/*      */ import org.springframework.core.ParameterNameDiscoverer;
/*      */ import org.springframework.core.ResolvableType;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
/*      */ import org.springframework.util.ClassUtils;
/*      */ import org.springframework.util.ObjectUtils;
/*      */ import org.springframework.util.ReflectionUtils;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class AbstractAutowireCapableBeanFactory
/*      */   extends AbstractBeanFactory
/*      */   implements AutowireCapableBeanFactory
/*      */ {
/*      */   private InstantiationStrategy instantiationStrategy;
/*      */   @Nullable
/*  131 */   private ParameterNameDiscoverer parameterNameDiscoverer = (ParameterNameDiscoverer)new DefaultParameterNameDiscoverer();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean allowCircularReferences = true;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean allowRawInjectionDespiteWrapping = false;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  147 */   private final Set<Class<?>> ignoredDependencyTypes = new HashSet<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  153 */   private final Set<Class<?>> ignoredDependencyInterfaces = new HashSet<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  159 */   private final NamedThreadLocal<String> currentlyCreatedBean = new NamedThreadLocal("Currently created bean");
/*      */ 
/*      */   
/*  162 */   private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();
/*      */ 
/*      */   
/*  165 */   private final ConcurrentMap<Class<?>, Method[]> factoryMethodCandidateCache = (ConcurrentMap)new ConcurrentHashMap<>();
/*      */ 
/*      */   
/*  168 */   private final ConcurrentMap<Class<?>, PropertyDescriptor[]> filteredPropertyDescriptorsCache = (ConcurrentMap)new ConcurrentHashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AbstractAutowireCapableBeanFactory() {
/*  177 */     ignoreDependencyInterface(BeanNameAware.class);
/*  178 */     ignoreDependencyInterface(BeanFactoryAware.class);
/*  179 */     ignoreDependencyInterface(BeanClassLoaderAware.class);
/*  180 */     if (NativeDetector.inNativeImage()) {
/*  181 */       this.instantiationStrategy = new SimpleInstantiationStrategy();
/*      */     } else {
/*      */       
/*  184 */       this.instantiationStrategy = new CglibSubclassingInstantiationStrategy();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AbstractAutowireCapableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
/*  193 */     this();
/*  194 */     setParentBeanFactory(parentBeanFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
/*  204 */     this.instantiationStrategy = instantiationStrategy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected InstantiationStrategy getInstantiationStrategy() {
/*  211 */     return this.instantiationStrategy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setParameterNameDiscoverer(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
/*  220 */     this.parameterNameDiscoverer = parameterNameDiscoverer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected ParameterNameDiscoverer getParameterNameDiscoverer() {
/*  229 */     return this.parameterNameDiscoverer;
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
/*      */   public void setAllowCircularReferences(boolean allowCircularReferences) {
/*  246 */     this.allowCircularReferences = allowCircularReferences;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAllowCircularReferences() {
/*  255 */     return this.allowCircularReferences;
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
/*      */   public void setAllowRawInjectionDespiteWrapping(boolean allowRawInjectionDespiteWrapping) {
/*  273 */     this.allowRawInjectionDespiteWrapping = allowRawInjectionDespiteWrapping;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAllowRawInjectionDespiteWrapping() {
/*  282 */     return this.allowRawInjectionDespiteWrapping;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void ignoreDependencyType(Class<?> type) {
/*  290 */     this.ignoredDependencyTypes.add(type);
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
/*      */   public void ignoreDependencyInterface(Class<?> ifc) {
/*  304 */     this.ignoredDependencyInterfaces.add(ifc);
/*      */   }
/*      */ 
/*      */   
/*      */   public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
/*  309 */     super.copyConfigurationFrom(otherFactory);
/*  310 */     if (otherFactory instanceof AbstractAutowireCapableBeanFactory) {
/*  311 */       AbstractAutowireCapableBeanFactory otherAutowireFactory = (AbstractAutowireCapableBeanFactory)otherFactory;
/*      */       
/*  313 */       this.instantiationStrategy = otherAutowireFactory.instantiationStrategy;
/*  314 */       this.allowCircularReferences = otherAutowireFactory.allowCircularReferences;
/*  315 */       this.ignoredDependencyTypes.addAll(otherAutowireFactory.ignoredDependencyTypes);
/*  316 */       this.ignoredDependencyInterfaces.addAll(otherAutowireFactory.ignoredDependencyInterfaces);
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
/*      */   public <T> T createBean(Class<T> beanClass) throws BeansException {
/*  329 */     RootBeanDefinition bd = new RootBeanDefinition(beanClass);
/*  330 */     bd.setScope("prototype");
/*  331 */     bd.allowCaching = ClassUtils.isCacheSafe(beanClass, getBeanClassLoader());
/*  332 */     return (T)createBean(beanClass.getName(), bd, (Object[])null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void autowireBean(Object existingBean) {
/*  338 */     RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean));
/*  339 */     bd.setScope("prototype");
/*  340 */     bd.allowCaching = ClassUtils.isCacheSafe(bd.getBeanClass(), getBeanClassLoader());
/*  341 */     BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(existingBean);
/*  342 */     initBeanWrapper((BeanWrapper)beanWrapperImpl);
/*  343 */     populateBean(bd.getBeanClass().getName(), bd, (BeanWrapper)beanWrapperImpl);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object configureBean(Object existingBean, String beanName) throws BeansException {
/*  348 */     markBeanAsCreated(beanName);
/*  349 */     BeanDefinition mbd = getMergedBeanDefinition(beanName);
/*  350 */     RootBeanDefinition bd = null;
/*  351 */     if (mbd instanceof RootBeanDefinition) {
/*  352 */       RootBeanDefinition rbd = (RootBeanDefinition)mbd;
/*  353 */       bd = rbd.isPrototype() ? rbd : rbd.cloneBeanDefinition();
/*      */     } 
/*  355 */     if (bd == null) {
/*  356 */       bd = new RootBeanDefinition(mbd);
/*      */     }
/*  358 */     if (!bd.isPrototype()) {
/*  359 */       bd.setScope("prototype");
/*  360 */       bd.allowCaching = ClassUtils.isCacheSafe(ClassUtils.getUserClass(existingBean), getBeanClassLoader());
/*      */     } 
/*  362 */     BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(existingBean);
/*  363 */     initBeanWrapper((BeanWrapper)beanWrapperImpl);
/*  364 */     populateBean(beanName, bd, (BeanWrapper)beanWrapperImpl);
/*  365 */     return initializeBean(beanName, existingBean, bd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
/*  376 */     RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
/*  377 */     bd.setScope("prototype");
/*  378 */     return createBean(beanClass.getName(), bd, (Object[])null);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
/*      */     Object bean;
/*  384 */     RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
/*  385 */     bd.setScope("prototype");
/*  386 */     if (bd.getResolvedAutowireMode() == 3) {
/*  387 */       return autowireConstructor(beanClass.getName(), bd, (Constructor<?>[])null, (Object[])null).getWrappedInstance();
/*      */     }
/*      */ 
/*      */     
/*  391 */     if (System.getSecurityManager() != null) {
/*  392 */       bean = AccessController.doPrivileged(() -> getInstantiationStrategy().instantiate(bd, null, (BeanFactory)this), 
/*      */           
/*  394 */           getAccessControlContext());
/*      */     } else {
/*      */       
/*  397 */       bean = getInstantiationStrategy().instantiate(bd, null, (BeanFactory)this);
/*      */     } 
/*  399 */     populateBean(beanClass.getName(), bd, (BeanWrapper)new BeanWrapperImpl(bean));
/*  400 */     return bean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
/*  408 */     if (autowireMode == 3) {
/*  409 */       throw new IllegalArgumentException("AUTOWIRE_CONSTRUCTOR not supported for existing bean instance");
/*      */     }
/*      */ 
/*      */     
/*  413 */     RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean), autowireMode, dependencyCheck);
/*  414 */     bd.setScope("prototype");
/*  415 */     BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(existingBean);
/*  416 */     initBeanWrapper((BeanWrapper)beanWrapperImpl);
/*  417 */     populateBean(bd.getBeanClass().getName(), bd, (BeanWrapper)beanWrapperImpl);
/*      */   }
/*      */ 
/*      */   
/*      */   public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
/*  422 */     markBeanAsCreated(beanName);
/*  423 */     BeanDefinition bd = getMergedBeanDefinition(beanName);
/*  424 */     BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(existingBean);
/*  425 */     initBeanWrapper((BeanWrapper)beanWrapperImpl);
/*  426 */     applyPropertyValues(beanName, bd, (BeanWrapper)beanWrapperImpl, (PropertyValues)bd.getPropertyValues());
/*      */   }
/*      */ 
/*      */   
/*      */   public Object initializeBean(Object existingBean, String beanName) {
/*  431 */     return initializeBean(beanName, existingBean, (RootBeanDefinition)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
/*  438 */     Object result = existingBean;
/*  439 */     for (BeanPostProcessor processor : getBeanPostProcessors()) {
/*  440 */       Object current = processor.postProcessBeforeInitialization(result, beanName);
/*  441 */       if (current == null) {
/*  442 */         return result;
/*      */       }
/*  444 */       result = current;
/*      */     } 
/*  446 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
/*  453 */     Object result = existingBean;
/*  454 */     for (BeanPostProcessor processor : getBeanPostProcessors()) {
/*  455 */       Object current = processor.postProcessAfterInitialization(result, beanName);
/*  456 */       if (current == null) {
/*  457 */         return result;
/*      */       }
/*  459 */       result = current;
/*      */     } 
/*  461 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public void destroyBean(Object existingBean) {
/*  466 */     (new DisposableBeanAdapter(existingBean, 
/*  467 */         (getBeanPostProcessorCache()).destructionAware, getAccessControlContext())).destroy();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object resolveBeanByName(String name, DependencyDescriptor descriptor) {
/*  477 */     InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint((InjectionPoint)descriptor);
/*      */     try {
/*  479 */       return getBean(name, descriptor.getDependencyType());
/*      */     } finally {
/*      */       
/*  482 */       ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException {
/*  489 */     return resolveDependency(descriptor, requestingBeanName, null, null);
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
/*      */   protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
/*  506 */     if (this.logger.isTraceEnabled()) {
/*  507 */       this.logger.trace("Creating instance of bean '" + beanName + "'");
/*      */     }
/*  509 */     RootBeanDefinition mbdToUse = mbd;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  514 */     Class<?> resolvedClass = resolveBeanClass(mbd, beanName, new Class[0]);
/*  515 */     if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
/*  516 */       mbdToUse = new RootBeanDefinition(mbd);
/*  517 */       mbdToUse.setBeanClass(resolvedClass);
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*  522 */       mbdToUse.prepareMethodOverrides();
/*      */     }
/*  524 */     catch (BeanDefinitionValidationException ex) {
/*  525 */       throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(), beanName, "Validation of method overrides failed", ex);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  531 */       Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
/*  532 */       if (bean != null) {
/*  533 */         return bean;
/*      */       }
/*      */     }
/*  536 */     catch (Throwable ex) {
/*  537 */       throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "BeanPostProcessor before instantiation of bean failed", ex);
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*  542 */       Object beanInstance = doCreateBean(beanName, mbdToUse, args);
/*  543 */       if (this.logger.isTraceEnabled()) {
/*  544 */         this.logger.trace("Finished creating instance of bean '" + beanName + "'");
/*      */       }
/*  546 */       return beanInstance;
/*      */     }
/*  548 */     catch (BeanCreationException|ImplicitlyAppearedSingletonException ex) {
/*      */ 
/*      */       
/*  551 */       throw ex;
/*      */     }
/*  553 */     catch (Throwable ex) {
/*  554 */       throw new BeanCreationException(mbdToUse
/*  555 */           .getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
/*  577 */     BeanWrapper instanceWrapper = null;
/*  578 */     if (mbd.isSingleton()) {
/*  579 */       instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
/*      */     }
/*  581 */     if (instanceWrapper == null) {
/*  582 */       instanceWrapper = createBeanInstance(beanName, mbd, args);
/*      */     }
/*  584 */     Object bean = instanceWrapper.getWrappedInstance();
/*  585 */     Class<?> beanType = instanceWrapper.getWrappedClass();
/*  586 */     if (beanType != NullBean.class) {
/*  587 */       mbd.resolvedTargetType = beanType;
/*      */     }
/*      */ 
/*      */     
/*  591 */     synchronized (mbd.postProcessingLock) {
/*  592 */       if (!mbd.postProcessed) {
/*      */         try {
/*  594 */           applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
/*      */         }
/*  596 */         catch (Throwable ex) {
/*  597 */           throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Post-processing of merged bean definition failed", ex);
/*      */         } 
/*      */         
/*  600 */         mbd.postProcessed = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  607 */     boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences && isSingletonCurrentlyInCreation(beanName));
/*  608 */     if (earlySingletonExposure) {
/*  609 */       if (this.logger.isTraceEnabled()) {
/*  610 */         this.logger.trace("Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references");
/*      */       }
/*      */       
/*  613 */       addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
/*      */     } 
/*      */ 
/*      */     
/*  617 */     Object exposedObject = bean;
/*      */     try {
/*  619 */       populateBean(beanName, mbd, instanceWrapper);
/*  620 */       exposedObject = initializeBean(beanName, exposedObject, mbd);
/*      */     }
/*  622 */     catch (Throwable ex) {
/*  623 */       if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException)ex).getBeanName())) {
/*  624 */         throw (BeanCreationException)ex;
/*      */       }
/*      */       
/*  627 */       throw new BeanCreationException(mbd
/*  628 */           .getResourceDescription(), beanName, "Initialization of bean failed", ex);
/*      */     } 
/*      */ 
/*      */     
/*  632 */     if (earlySingletonExposure) {
/*  633 */       Object earlySingletonReference = getSingleton(beanName, false);
/*  634 */       if (earlySingletonReference != null) {
/*  635 */         if (exposedObject == bean) {
/*  636 */           exposedObject = earlySingletonReference;
/*      */         }
/*  638 */         else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
/*  639 */           String[] dependentBeans = getDependentBeans(beanName);
/*  640 */           Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
/*  641 */           for (String dependentBean : dependentBeans) {
/*  642 */             if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
/*  643 */               actualDependentBeans.add(dependentBean);
/*      */             }
/*      */           } 
/*  646 */           if (!actualDependentBeans.isEmpty()) {
/*  647 */             throw new BeanCurrentlyInCreationException(beanName, "Bean with name '" + beanName + "' has been injected into other beans [" + 
/*      */                 
/*  649 */                 StringUtils.collectionToCommaDelimitedString(actualDependentBeans) + "] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  661 */       registerDisposableBeanIfNecessary(beanName, bean, mbd);
/*      */     }
/*  663 */     catch (BeanDefinitionValidationException ex) {
/*  664 */       throw new BeanCreationException(mbd
/*  665 */           .getResourceDescription(), beanName, "Invalid destruction signature", ex);
/*      */     } 
/*      */     
/*  668 */     return exposedObject;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
/*  674 */     Class<?> targetType = determineTargetType(beanName, mbd, typesToMatch);
/*      */ 
/*      */     
/*  677 */     if (targetType != null && !mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
/*  678 */       boolean matchingOnlyFactoryBean = (typesToMatch.length == 1 && typesToMatch[0] == FactoryBean.class);
/*  679 */       for (SmartInstantiationAwareBeanPostProcessor bp : (getBeanPostProcessorCache()).smartInstantiationAware) {
/*  680 */         Class<?> predicted = bp.predictBeanType(targetType, beanName);
/*  681 */         if (predicted != null && (!matchingOnlyFactoryBean || FactoryBean.class
/*  682 */           .isAssignableFrom(predicted))) {
/*  683 */           return predicted;
/*      */         }
/*      */       } 
/*      */     } 
/*  687 */     return targetType;
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
/*      */   protected Class<?> determineTargetType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
/*  700 */     Class<?> targetType = mbd.getTargetType();
/*  701 */     if (targetType == null) {
/*      */ 
/*      */       
/*  704 */       targetType = (mbd.getFactoryMethodName() != null) ? getTypeForFactoryMethod(beanName, mbd, typesToMatch) : resolveBeanClass(mbd, beanName, typesToMatch);
/*  705 */       if (ObjectUtils.isEmpty((Object[])typesToMatch) || getTempClassLoader() == null) {
/*  706 */         mbd.resolvedTargetType = targetType;
/*      */       }
/*      */     } 
/*  709 */     return targetType;
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
/*      */   @Nullable
/*      */   protected Class<?> getTypeForFactoryMethod(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
/*  728 */     ResolvableType cachedReturnType = mbd.factoryMethodReturnType;
/*  729 */     if (cachedReturnType != null) {
/*  730 */       return cachedReturnType.resolve();
/*      */     }
/*      */     
/*  733 */     Class<?> commonType = null;
/*  734 */     Method uniqueCandidate = mbd.factoryMethodToIntrospect;
/*      */     
/*  736 */     if (uniqueCandidate == null) {
/*      */       
/*  738 */       boolean isStatic = true;
/*      */       
/*  740 */       String factoryBeanName = mbd.getFactoryBeanName();
/*  741 */       if (factoryBeanName != null) {
/*  742 */         if (factoryBeanName.equals(beanName)) {
/*  743 */           throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
/*      */         }
/*      */ 
/*      */         
/*  747 */         factoryClass = getType(factoryBeanName);
/*  748 */         isStatic = false;
/*      */       }
/*      */       else {
/*      */         
/*  752 */         factoryClass = resolveBeanClass(mbd, beanName, typesToMatch);
/*      */       } 
/*      */       
/*  755 */       if (factoryClass == null) {
/*  756 */         return null;
/*      */       }
/*  758 */       Class<?> factoryClass = ClassUtils.getUserClass(factoryClass);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  763 */       int minNrOfArgs = mbd.hasConstructorArgumentValues() ? mbd.getConstructorArgumentValues().getArgumentCount() : 0;
/*  764 */       Method[] candidates = this.factoryMethodCandidateCache.computeIfAbsent(factoryClass, clazz -> ReflectionUtils.getUniqueDeclaredMethods(clazz, ReflectionUtils.USER_DECLARED_METHODS));
/*      */ 
/*      */       
/*  767 */       for (Method candidate : candidates) {
/*  768 */         if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate) && candidate
/*  769 */           .getParameterCount() >= minNrOfArgs)
/*      */         {
/*  771 */           if ((candidate.getTypeParameters()).length > 0) {
/*      */             
/*      */             try {
/*  774 */               Class<?>[] paramTypes = candidate.getParameterTypes();
/*  775 */               String[] paramNames = null;
/*  776 */               ParameterNameDiscoverer pnd = getParameterNameDiscoverer();
/*  777 */               if (pnd != null) {
/*  778 */                 paramNames = pnd.getParameterNames(candidate);
/*      */               }
/*  780 */               ConstructorArgumentValues cav = mbd.getConstructorArgumentValues();
/*  781 */               Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(paramTypes.length);
/*  782 */               Object[] args = new Object[paramTypes.length];
/*  783 */               for (int i = 0; i < args.length; i++) {
/*  784 */                 ConstructorArgumentValues.ValueHolder valueHolder = cav.getArgumentValue(i, paramTypes[i], (paramNames != null) ? paramNames[i] : null, usedValueHolders);
/*      */                 
/*  786 */                 if (valueHolder == null) {
/*  787 */                   valueHolder = cav.getGenericArgumentValue(null, null, usedValueHolders);
/*      */                 }
/*  789 */                 if (valueHolder != null) {
/*  790 */                   args[i] = valueHolder.getValue();
/*  791 */                   usedValueHolders.add(valueHolder);
/*      */                 } 
/*      */               } 
/*  794 */               Class<?> returnType = AutowireUtils.resolveReturnTypeForFactoryMethod(candidate, args, 
/*  795 */                   getBeanClassLoader());
/*  796 */               uniqueCandidate = (commonType == null && returnType == candidate.getReturnType()) ? candidate : null;
/*      */               
/*  798 */               commonType = ClassUtils.determineCommonAncestor(returnType, commonType);
/*  799 */               if (commonType == null)
/*      */               {
/*  801 */                 return null;
/*      */               }
/*      */             }
/*  804 */             catch (Throwable ex) {
/*  805 */               if (this.logger.isDebugEnabled()) {
/*  806 */                 this.logger.debug("Failed to resolve generic return type for factory method: " + ex);
/*      */               }
/*      */             } 
/*      */           } else {
/*      */             
/*  811 */             uniqueCandidate = (commonType == null) ? candidate : null;
/*  812 */             commonType = ClassUtils.determineCommonAncestor(candidate.getReturnType(), commonType);
/*  813 */             if (commonType == null)
/*      */             {
/*  815 */               return null;
/*      */             }
/*      */           } 
/*      */         }
/*      */       } 
/*      */       
/*  821 */       mbd.factoryMethodToIntrospect = uniqueCandidate;
/*  822 */       if (commonType == null) {
/*  823 */         return null;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  830 */     cachedReturnType = (uniqueCandidate != null) ? ResolvableType.forMethodReturnType(uniqueCandidate) : ResolvableType.forClass(commonType);
/*  831 */     mbd.factoryMethodReturnType = cachedReturnType;
/*  832 */     return cachedReturnType.resolve();
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
/*      */   protected ResolvableType getTypeForFactoryBean(String beanName, RootBeanDefinition mbd, boolean allowInit) {
/*  850 */     ResolvableType result = getTypeForFactoryBeanFromAttributes((AttributeAccessor)mbd);
/*  851 */     if (result != ResolvableType.NONE) {
/*  852 */       return result;
/*      */     }
/*      */ 
/*      */     
/*  856 */     ResolvableType beanType = mbd.hasBeanClass() ? ResolvableType.forClass(mbd.getBeanClass()) : ResolvableType.NONE;
/*      */ 
/*      */     
/*  859 */     if (mbd.getInstanceSupplier() != null) {
/*  860 */       result = getFactoryBeanGeneric(mbd.targetType);
/*  861 */       if (result.resolve() != null) {
/*  862 */         return result;
/*      */       }
/*  864 */       result = getFactoryBeanGeneric(beanType);
/*  865 */       if (result.resolve() != null) {
/*  866 */         return result;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  871 */     String factoryBeanName = mbd.getFactoryBeanName();
/*  872 */     String factoryMethodName = mbd.getFactoryMethodName();
/*      */ 
/*      */     
/*  875 */     if (factoryBeanName != null) {
/*  876 */       if (factoryMethodName != null) {
/*      */         Class<?> factoryBeanClass;
/*      */         
/*  879 */         BeanDefinition factoryBeanDefinition = getBeanDefinition(factoryBeanName);
/*      */         
/*  881 */         if (factoryBeanDefinition instanceof AbstractBeanDefinition && ((AbstractBeanDefinition)factoryBeanDefinition)
/*  882 */           .hasBeanClass()) {
/*  883 */           factoryBeanClass = ((AbstractBeanDefinition)factoryBeanDefinition).getBeanClass();
/*      */         } else {
/*      */           
/*  886 */           RootBeanDefinition fbmbd = getMergedBeanDefinition(factoryBeanName, factoryBeanDefinition);
/*  887 */           factoryBeanClass = determineTargetType(factoryBeanName, fbmbd, new Class[0]);
/*      */         } 
/*  889 */         if (factoryBeanClass != null) {
/*  890 */           result = getTypeForFactoryBeanFromMethod(factoryBeanClass, factoryMethodName);
/*  891 */           if (result.resolve() != null) {
/*  892 */             return result;
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  899 */       if (!isBeanEligibleForMetadataCaching(factoryBeanName)) {
/*  900 */         return ResolvableType.NONE;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  905 */     if (allowInit) {
/*      */ 
/*      */       
/*  908 */       FactoryBean<?> factoryBean = mbd.isSingleton() ? getSingletonFactoryBeanForTypeCheck(beanName, mbd) : getNonSingletonFactoryBeanForTypeCheck(beanName, mbd);
/*  909 */       if (factoryBean != null) {
/*      */         
/*  911 */         Class<?> type = getTypeForFactoryBean(factoryBean);
/*  912 */         if (type != null) {
/*  913 */           return ResolvableType.forClass(type);
/*      */         }
/*      */ 
/*      */         
/*  917 */         return super.getTypeForFactoryBean(beanName, mbd, true);
/*      */       } 
/*      */     } 
/*      */     
/*  921 */     if (factoryBeanName == null && mbd.hasBeanClass() && factoryMethodName != null)
/*      */     {
/*      */       
/*  924 */       return getTypeForFactoryBeanFromMethod(mbd.getBeanClass(), factoryMethodName);
/*      */     }
/*  926 */     result = getFactoryBeanGeneric(beanType);
/*  927 */     if (result.resolve() != null) {
/*  928 */       return result;
/*      */     }
/*  930 */     return ResolvableType.NONE;
/*      */   }
/*      */   
/*      */   private ResolvableType getFactoryBeanGeneric(@Nullable ResolvableType type) {
/*  934 */     if (type == null) {
/*  935 */       return ResolvableType.NONE;
/*      */     }
/*  937 */     return type.as(FactoryBean.class).getGeneric(new int[0]);
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
/*      */   private ResolvableType getTypeForFactoryBeanFromMethod(Class<?> beanClass, String factoryMethodName) {
/*  949 */     Class<?> factoryBeanClass = ClassUtils.getUserClass(beanClass);
/*  950 */     FactoryBeanMethodTypeFinder finder = new FactoryBeanMethodTypeFinder(factoryMethodName);
/*  951 */     ReflectionUtils.doWithMethods(factoryBeanClass, finder, ReflectionUtils.USER_DECLARED_METHODS);
/*  952 */     return finder.getResult();
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
/*      */   @Deprecated
/*      */   @Nullable
/*      */   protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
/*  970 */     return getTypeForFactoryBean(beanName, mbd, true).resolve();
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
/*      */   protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
/*  982 */     Object exposedObject = bean;
/*  983 */     if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
/*  984 */       for (SmartInstantiationAwareBeanPostProcessor bp : (getBeanPostProcessorCache()).smartInstantiationAware) {
/*  985 */         exposedObject = bp.getEarlyBeanReference(exposedObject, beanName);
/*      */       }
/*      */     }
/*  988 */     return exposedObject;
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
/*      */   @Nullable
/*      */   private FactoryBean<?> getSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
/* 1006 */     synchronized (getSingletonMutex()) {
/* 1007 */       Object instance; BeanWrapper bw = this.factoryBeanInstanceCache.get(beanName);
/* 1008 */       if (bw != null) {
/* 1009 */         return (FactoryBean)bw.getWrappedInstance();
/*      */       }
/* 1011 */       Object beanInstance = getSingleton(beanName, false);
/* 1012 */       if (beanInstance instanceof FactoryBean) {
/* 1013 */         return (FactoryBean)beanInstance;
/*      */       }
/* 1015 */       if (isSingletonCurrentlyInCreation(beanName) || (mbd
/* 1016 */         .getFactoryBeanName() != null && isSingletonCurrentlyInCreation(mbd.getFactoryBeanName()))) {
/* 1017 */         return null;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 1023 */         beforeSingletonCreation(beanName);
/*      */         
/* 1025 */         instance = resolveBeforeInstantiation(beanName, mbd);
/* 1026 */         if (instance == null) {
/* 1027 */           bw = createBeanInstance(beanName, mbd, (Object[])null);
/* 1028 */           instance = bw.getWrappedInstance();
/*      */         }
/*      */       
/* 1031 */       } catch (UnsatisfiedDependencyException ex) {
/*      */         
/* 1033 */         throw ex;
/*      */       }
/* 1035 */       catch (BeanCreationException ex) {
/*      */ 
/*      */         
/* 1038 */         if (ex.contains(LinkageError.class)) {
/* 1039 */           throw ex;
/*      */         }
/*      */         
/* 1042 */         if (this.logger.isDebugEnabled()) {
/* 1043 */           this.logger.debug("Bean creation exception on singleton FactoryBean type check: " + ex);
/*      */         }
/* 1045 */         onSuppressedException((Exception)ex);
/* 1046 */         return null;
/*      */       }
/*      */       finally {
/*      */         
/* 1050 */         afterSingletonCreation(beanName);
/*      */       } 
/*      */       
/* 1053 */       FactoryBean<?> fb = getFactoryBean(beanName, instance);
/* 1054 */       if (bw != null) {
/* 1055 */         this.factoryBeanInstanceCache.put(beanName, bw);
/*      */       }
/* 1057 */       return fb;
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
/*      */   @Nullable
/*      */   private FactoryBean<?> getNonSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
/*      */     Object instance;
/* 1071 */     if (isPrototypeCurrentlyInCreation(beanName)) {
/* 1072 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1078 */       beforePrototypeCreation(beanName);
/*      */       
/* 1080 */       instance = resolveBeforeInstantiation(beanName, mbd);
/* 1081 */       if (instance == null) {
/* 1082 */         BeanWrapper bw = createBeanInstance(beanName, mbd, (Object[])null);
/* 1083 */         instance = bw.getWrappedInstance();
/*      */       }
/*      */     
/* 1086 */     } catch (UnsatisfiedDependencyException ex) {
/*      */       
/* 1088 */       throw ex;
/*      */     }
/* 1090 */     catch (BeanCreationException ex) {
/*      */       
/* 1092 */       if (this.logger.isDebugEnabled()) {
/* 1093 */         this.logger.debug("Bean creation exception on non-singleton FactoryBean type check: " + ex);
/*      */       }
/* 1095 */       onSuppressedException((Exception)ex);
/* 1096 */       return null;
/*      */     }
/*      */     finally {
/*      */       
/* 1100 */       afterPrototypeCreation(beanName);
/*      */     } 
/*      */     
/* 1103 */     return getFactoryBean(beanName, instance);
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
/*      */   protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
/* 1115 */     for (MergedBeanDefinitionPostProcessor processor : (getBeanPostProcessorCache()).mergedDefinition) {
/* 1116 */       processor.postProcessMergedBeanDefinition(mbd, beanType, beanName);
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
/*      */   @Nullable
/*      */   protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
/* 1129 */     Object bean = null;
/* 1130 */     if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
/*      */       
/* 1132 */       if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
/* 1133 */         Class<?> targetType = determineTargetType(beanName, mbd, new Class[0]);
/* 1134 */         if (targetType != null) {
/* 1135 */           bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
/* 1136 */           if (bean != null) {
/* 1137 */             bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
/*      */           }
/*      */         } 
/*      */       } 
/* 1141 */       mbd.beforeInstantiationResolved = Boolean.valueOf((bean != null));
/*      */     } 
/* 1143 */     return bean;
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
/*      */   @Nullable
/*      */   protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
/* 1159 */     for (InstantiationAwareBeanPostProcessor bp : (getBeanPostProcessorCache()).instantiationAware) {
/* 1160 */       Object result = bp.postProcessBeforeInstantiation(beanClass, beanName);
/* 1161 */       if (result != null) {
/* 1162 */         return result;
/*      */       }
/*      */     } 
/* 1165 */     return null;
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
/*      */   protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
/* 1182 */     Class<?> beanClass = resolveBeanClass(mbd, beanName, new Class[0]);
/*      */     
/* 1184 */     if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
/* 1185 */       throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean class isn't public, and non-public access not allowed: " + beanClass
/* 1186 */           .getName());
/*      */     }
/*      */     
/* 1189 */     Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
/* 1190 */     if (instanceSupplier != null) {
/* 1191 */       return obtainFromSupplier(instanceSupplier, beanName);
/*      */     }
/*      */     
/* 1194 */     if (mbd.getFactoryMethodName() != null) {
/* 1195 */       return instantiateUsingFactoryMethod(beanName, mbd, args);
/*      */     }
/*      */ 
/*      */     
/* 1199 */     boolean resolved = false;
/* 1200 */     boolean autowireNecessary = false;
/* 1201 */     if (args == null) {
/* 1202 */       synchronized (mbd.constructorArgumentLock) {
/* 1203 */         if (mbd.resolvedConstructorOrFactoryMethod != null) {
/* 1204 */           resolved = true;
/* 1205 */           autowireNecessary = mbd.constructorArgumentsResolved;
/*      */         } 
/*      */       } 
/*      */     }
/* 1209 */     if (resolved) {
/* 1210 */       if (autowireNecessary) {
/* 1211 */         return autowireConstructor(beanName, mbd, (Constructor<?>[])null, (Object[])null);
/*      */       }
/*      */       
/* 1214 */       return instantiateBean(beanName, mbd);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1219 */     Constructor[] arrayOfConstructor = (Constructor[])determineConstructorsFromBeanPostProcessors(beanClass, beanName);
/* 1220 */     if (arrayOfConstructor != null || mbd.getResolvedAutowireMode() == 3 || mbd
/* 1221 */       .hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
/* 1222 */       return autowireConstructor(beanName, mbd, (Constructor<?>[])arrayOfConstructor, args);
/*      */     }
/*      */ 
/*      */     
/* 1226 */     arrayOfConstructor = (Constructor[])mbd.getPreferredConstructors();
/* 1227 */     if (arrayOfConstructor != null) {
/* 1228 */       return autowireConstructor(beanName, mbd, (Constructor<?>[])arrayOfConstructor, (Object[])null);
/*      */     }
/*      */ 
/*      */     
/* 1232 */     return instantiateBean(beanName, mbd);
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
/*      */   protected BeanWrapper obtainFromSupplier(Supplier<?> instanceSupplier, String beanName) {
/*      */     Object instance;
/* 1246 */     String outerBean = (String)this.currentlyCreatedBean.get();
/* 1247 */     this.currentlyCreatedBean.set(beanName);
/*      */     try {
/* 1249 */       instance = instanceSupplier.get();
/*      */     } finally {
/*      */       
/* 1252 */       if (outerBean != null) {
/* 1253 */         this.currentlyCreatedBean.set(outerBean);
/*      */       } else {
/*      */         
/* 1256 */         this.currentlyCreatedBean.remove();
/*      */       } 
/*      */     } 
/*      */     
/* 1260 */     if (instance == null) {
/* 1261 */       instance = new NullBean();
/*      */     }
/* 1263 */     BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(instance);
/* 1264 */     initBeanWrapper((BeanWrapper)beanWrapperImpl);
/* 1265 */     return (BeanWrapper)beanWrapperImpl;
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
/*      */   protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {
/* 1279 */     String currentlyCreatedBean = (String)this.currentlyCreatedBean.get();
/* 1280 */     if (currentlyCreatedBean != null) {
/* 1281 */       registerDependentBean(beanName, currentlyCreatedBean);
/*      */     }
/*      */     
/* 1284 */     return super.getObjectForBeanInstance(beanInstance, name, beanName, mbd);
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
/*      */   @Nullable
/*      */   protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName) throws BeansException {
/* 1300 */     if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {
/* 1301 */       for (SmartInstantiationAwareBeanPostProcessor bp : (getBeanPostProcessorCache()).smartInstantiationAware) {
/* 1302 */         Constructor[] arrayOfConstructor = bp.determineCandidateConstructors(beanClass, beanName);
/* 1303 */         if (arrayOfConstructor != null) {
/* 1304 */           return (Constructor<?>[])arrayOfConstructor;
/*      */         }
/*      */       } 
/*      */     }
/* 1308 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
/*      */     try {
/*      */       Object beanInstance;
/* 1320 */       if (System.getSecurityManager() != null) {
/* 1321 */         beanInstance = AccessController.doPrivileged(() -> getInstantiationStrategy().instantiate(mbd, beanName, (BeanFactory)this), 
/*      */             
/* 1323 */             getAccessControlContext());
/*      */       } else {
/*      */         
/* 1326 */         beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, (BeanFactory)this);
/*      */       } 
/* 1328 */       BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(beanInstance);
/* 1329 */       initBeanWrapper((BeanWrapper)beanWrapperImpl);
/* 1330 */       return (BeanWrapper)beanWrapperImpl;
/*      */     }
/* 1332 */     catch (Throwable ex) {
/* 1333 */       throw new BeanCreationException(mbd
/* 1334 */           .getResourceDescription(), beanName, "Instantiation of bean failed", ex);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeanWrapper instantiateUsingFactoryMethod(String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {
/* 1352 */     return (new ConstructorResolver(this)).instantiateUsingFactoryMethod(beanName, mbd, explicitArgs);
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
/*      */   
/*      */   protected BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {
/* 1372 */     return (new ConstructorResolver(this)).autowireConstructor(beanName, mbd, ctors, explicitArgs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
/*      */     PropertyValues propertyValues;
/* 1384 */     if (bw == null) {
/* 1385 */       if (mbd.hasPropertyValues()) {
/* 1386 */         throw new BeanCreationException(mbd
/* 1387 */             .getResourceDescription(), beanName, "Cannot apply property values to null instance");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1398 */     if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
/* 1399 */       for (InstantiationAwareBeanPostProcessor bp : (getBeanPostProcessorCache()).instantiationAware) {
/* 1400 */         if (!bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
/*      */           return;
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/* 1406 */     MutablePropertyValues mutablePropertyValues = mbd.hasPropertyValues() ? mbd.getPropertyValues() : null;
/*      */     
/* 1408 */     int resolvedAutowireMode = mbd.getResolvedAutowireMode();
/* 1409 */     if (resolvedAutowireMode == 1 || resolvedAutowireMode == 2) {
/* 1410 */       MutablePropertyValues newPvs = new MutablePropertyValues((PropertyValues)mutablePropertyValues);
/*      */       
/* 1412 */       if (resolvedAutowireMode == 1) {
/* 1413 */         autowireByName(beanName, mbd, bw, newPvs);
/*      */       }
/*      */       
/* 1416 */       if (resolvedAutowireMode == 2) {
/* 1417 */         autowireByType(beanName, mbd, bw, newPvs);
/*      */       }
/* 1419 */       mutablePropertyValues = newPvs;
/*      */     } 
/*      */     
/* 1422 */     boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
/* 1423 */     boolean needsDepCheck = (mbd.getDependencyCheck() != 0);
/*      */     
/* 1425 */     PropertyDescriptor[] filteredPds = null;
/* 1426 */     if (hasInstAwareBpps) {
/* 1427 */       if (mutablePropertyValues == null) {
/* 1428 */         mutablePropertyValues = mbd.getPropertyValues();
/*      */       }
/* 1430 */       for (InstantiationAwareBeanPostProcessor bp : (getBeanPostProcessorCache()).instantiationAware) {
/* 1431 */         PropertyValues pvsToUse = bp.postProcessProperties((PropertyValues)mutablePropertyValues, bw.getWrappedInstance(), beanName);
/* 1432 */         if (pvsToUse == null) {
/* 1433 */           if (filteredPds == null) {
/* 1434 */             filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
/*      */           }
/* 1436 */           pvsToUse = bp.postProcessPropertyValues((PropertyValues)mutablePropertyValues, filteredPds, bw.getWrappedInstance(), beanName);
/* 1437 */           if (pvsToUse == null) {
/*      */             return;
/*      */           }
/*      */         } 
/* 1441 */         propertyValues = pvsToUse;
/*      */       } 
/*      */     } 
/* 1444 */     if (needsDepCheck) {
/* 1445 */       if (filteredPds == null) {
/* 1446 */         filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
/*      */       }
/* 1448 */       checkDependencies(beanName, mbd, filteredPds, propertyValues);
/*      */     } 
/*      */     
/* 1451 */     if (propertyValues != null) {
/* 1452 */       applyPropertyValues(beanName, mbd, bw, propertyValues);
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
/*      */ 
/*      */ 
/*      */   
/*      */   protected void autowireByName(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
/* 1468 */     String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
/* 1469 */     for (String propertyName : propertyNames) {
/* 1470 */       if (containsBean(propertyName)) {
/* 1471 */         Object bean = getBean(propertyName);
/* 1472 */         pvs.add(propertyName, bean);
/* 1473 */         registerDependentBean(propertyName, beanName);
/* 1474 */         if (this.logger.isTraceEnabled()) {
/* 1475 */           this.logger.trace("Added autowiring by name from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + propertyName + "'");
/*      */         
/*      */         }
/*      */       
/*      */       }
/* 1480 */       else if (this.logger.isTraceEnabled()) {
/* 1481 */         this.logger.trace("Not autowiring property '" + propertyName + "' of bean '" + beanName + "' by name: no matching bean found");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void autowireByType(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
/*      */     BeanWrapper beanWrapper;
/* 1502 */     TypeConverter converter = getCustomTypeConverter();
/* 1503 */     if (converter == null) {
/* 1504 */       beanWrapper = bw;
/*      */     }
/*      */     
/* 1507 */     Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
/* 1508 */     String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
/* 1509 */     for (String propertyName : propertyNames) {
/*      */       try {
/* 1511 */         PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
/*      */ 
/*      */         
/* 1514 */         if (Object.class != pd.getPropertyType()) {
/* 1515 */           MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
/*      */           
/* 1517 */           boolean eager = !(bw.getWrappedInstance() instanceof org.springframework.core.PriorityOrdered);
/* 1518 */           DependencyDescriptor desc = new AutowireByTypeDependencyDescriptor(methodParam, eager);
/* 1519 */           Object autowiredArgument = resolveDependency(desc, beanName, autowiredBeanNames, (TypeConverter)beanWrapper);
/* 1520 */           if (autowiredArgument != null) {
/* 1521 */             pvs.add(propertyName, autowiredArgument);
/*      */           }
/* 1523 */           for (String autowiredBeanName : autowiredBeanNames) {
/* 1524 */             registerDependentBean(autowiredBeanName, beanName);
/* 1525 */             if (this.logger.isTraceEnabled()) {
/* 1526 */               this.logger.trace("Autowiring by type from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + autowiredBeanName + "'");
/*      */             }
/*      */           } 
/*      */           
/* 1530 */           autowiredBeanNames.clear();
/*      */         }
/*      */       
/* 1533 */       } catch (BeansException ex) {
/* 1534 */         throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, propertyName, ex);
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
/*      */ 
/*      */   
/*      */   protected String[] unsatisfiedNonSimpleProperties(AbstractBeanDefinition mbd, BeanWrapper bw) {
/* 1550 */     Set<String> result = new TreeSet<>();
/* 1551 */     MutablePropertyValues mutablePropertyValues = mbd.getPropertyValues();
/* 1552 */     PropertyDescriptor[] pds = bw.getPropertyDescriptors();
/* 1553 */     for (PropertyDescriptor pd : pds) {
/* 1554 */       if (pd.getWriteMethod() != null && !isExcludedFromDependencyCheck(pd) && !mutablePropertyValues.contains(pd.getName()) && 
/* 1555 */         !BeanUtils.isSimpleProperty(pd.getPropertyType())) {
/* 1556 */         result.add(pd.getName());
/*      */       }
/*      */     } 
/* 1559 */     return StringUtils.toStringArray(result);
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
/*      */   protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw, boolean cache) {
/* 1572 */     PropertyDescriptor[] filtered = this.filteredPropertyDescriptorsCache.get(bw.getWrappedClass());
/* 1573 */     if (filtered == null) {
/* 1574 */       filtered = filterPropertyDescriptorsForDependencyCheck(bw);
/* 1575 */       if (cache) {
/*      */         
/* 1577 */         PropertyDescriptor[] existing = this.filteredPropertyDescriptorsCache.putIfAbsent(bw.getWrappedClass(), filtered);
/* 1578 */         if (existing != null) {
/* 1579 */           filtered = existing;
/*      */         }
/*      */       } 
/*      */     } 
/* 1583 */     return filtered;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw) {
/* 1594 */     List<PropertyDescriptor> pds = new ArrayList<>(Arrays.asList(bw.getPropertyDescriptors()));
/* 1595 */     pds.removeIf(this::isExcludedFromDependencyCheck);
/* 1596 */     return pds.<PropertyDescriptor>toArray(new PropertyDescriptor[0]);
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
/*      */   protected boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
/* 1610 */     return (AutowireUtils.isExcludedFromDependencyCheck(pd) || this.ignoredDependencyTypes
/* 1611 */       .contains(pd.getPropertyType()) || 
/* 1612 */       AutowireUtils.isSetterDefinedInInterface(pd, this.ignoredDependencyInterfaces));
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
/*      */   protected void checkDependencies(String beanName, AbstractBeanDefinition mbd, PropertyDescriptor[] pds, @Nullable PropertyValues pvs) throws UnsatisfiedDependencyException {
/* 1629 */     int dependencyCheck = mbd.getDependencyCheck();
/* 1630 */     for (PropertyDescriptor pd : pds) {
/* 1631 */       if (pd.getWriteMethod() != null && (pvs == null || !pvs.contains(pd.getName()))) {
/* 1632 */         boolean isSimple = BeanUtils.isSimpleProperty(pd.getPropertyType());
/* 1633 */         boolean unsatisfied = (dependencyCheck == 3 || (isSimple && dependencyCheck == 2) || (!isSimple && dependencyCheck == 1));
/*      */ 
/*      */         
/* 1636 */         if (unsatisfied) {
/* 1637 */           throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, pd.getName(), "Set this property value or disable dependency checking for this bean.");
/*      */         }
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
/*      */   protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
/*      */     List<PropertyValue> original;
/*      */     BeanWrapper beanWrapper;
/* 1654 */     if (pvs.isEmpty()) {
/*      */       return;
/*      */     }
/*      */     
/* 1658 */     if (System.getSecurityManager() != null && bw instanceof BeanWrapperImpl) {
/* 1659 */       ((BeanWrapperImpl)bw).setSecurityContext(getAccessControlContext());
/*      */     }
/*      */     
/* 1662 */     MutablePropertyValues mpvs = null;
/*      */ 
/*      */     
/* 1665 */     if (pvs instanceof MutablePropertyValues) {
/* 1666 */       mpvs = (MutablePropertyValues)pvs;
/* 1667 */       if (mpvs.isConverted()) {
/*      */         
/*      */         try {
/* 1670 */           bw.setPropertyValues((PropertyValues)mpvs);
/*      */           
/*      */           return;
/* 1673 */         } catch (BeansException ex) {
/* 1674 */           throw new BeanCreationException(mbd
/* 1675 */               .getResourceDescription(), beanName, "Error setting property values", ex);
/*      */         } 
/*      */       }
/* 1678 */       original = mpvs.getPropertyValueList();
/*      */     } else {
/*      */       
/* 1681 */       original = Arrays.asList(pvs.getPropertyValues());
/*      */     } 
/*      */     
/* 1684 */     TypeConverter converter = getCustomTypeConverter();
/* 1685 */     if (converter == null) {
/* 1686 */       beanWrapper = bw;
/*      */     }
/* 1688 */     BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, (TypeConverter)beanWrapper);
/*      */ 
/*      */     
/* 1691 */     List<PropertyValue> deepCopy = new ArrayList<>(original.size());
/* 1692 */     boolean resolveNecessary = false;
/* 1693 */     for (PropertyValue pv : original) {
/* 1694 */       if (pv.isConverted()) {
/* 1695 */         deepCopy.add(pv);
/*      */         continue;
/*      */       } 
/* 1698 */       String propertyName = pv.getName();
/* 1699 */       Object originalValue = pv.getValue();
/* 1700 */       if (originalValue == AutowiredPropertyMarker.INSTANCE) {
/* 1701 */         Method writeMethod = bw.getPropertyDescriptor(propertyName).getWriteMethod();
/* 1702 */         if (writeMethod == null) {
/* 1703 */           throw new IllegalArgumentException("Autowire marker for property without write method: " + pv);
/*      */         }
/* 1705 */         originalValue = new DependencyDescriptor(new MethodParameter(writeMethod, 0), true);
/*      */       } 
/* 1707 */       Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
/* 1708 */       Object convertedValue = resolvedValue;
/*      */       
/* 1710 */       boolean convertible = (bw.isWritableProperty(propertyName) && !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName));
/* 1711 */       if (convertible) {
/* 1712 */         convertedValue = convertForProperty(resolvedValue, propertyName, bw, (TypeConverter)beanWrapper);
/*      */       }
/*      */ 
/*      */       
/* 1716 */       if (resolvedValue == originalValue) {
/* 1717 */         if (convertible) {
/* 1718 */           pv.setConvertedValue(convertedValue);
/*      */         }
/* 1720 */         deepCopy.add(pv); continue;
/*      */       } 
/* 1722 */       if (convertible && originalValue instanceof TypedStringValue && 
/* 1723 */         !((TypedStringValue)originalValue).isDynamic() && !(convertedValue instanceof java.util.Collection) && 
/* 1724 */         !ObjectUtils.isArray(convertedValue)) {
/* 1725 */         pv.setConvertedValue(convertedValue);
/* 1726 */         deepCopy.add(pv);
/*      */         continue;
/*      */       } 
/* 1729 */       resolveNecessary = true;
/* 1730 */       deepCopy.add(new PropertyValue(pv, convertedValue));
/*      */     } 
/*      */ 
/*      */     
/* 1734 */     if (mpvs != null && !resolveNecessary) {
/* 1735 */       mpvs.setConverted();
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 1740 */       bw.setPropertyValues((PropertyValues)new MutablePropertyValues(deepCopy));
/*      */     }
/* 1742 */     catch (BeansException ex) {
/* 1743 */       throw new BeanCreationException(mbd
/* 1744 */           .getResourceDescription(), beanName, "Error setting property values", ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private Object convertForProperty(@Nullable Object value, String propertyName, BeanWrapper bw, TypeConverter converter) {
/* 1755 */     if (converter instanceof BeanWrapperImpl) {
/* 1756 */       return ((BeanWrapperImpl)converter).convertForProperty(value, propertyName);
/*      */     }
/*      */     
/* 1759 */     PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
/* 1760 */     MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
/* 1761 */     return converter.convertIfNecessary(value, pd.getPropertyType(), methodParam);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
/* 1784 */     if (System.getSecurityManager() != null) {
/* 1785 */       AccessController.doPrivileged(() -> {
/*      */             invokeAwareMethods(beanName, bean);
/*      */             return null;
/* 1788 */           }getAccessControlContext());
/*      */     } else {
/*      */       
/* 1791 */       invokeAwareMethods(beanName, bean);
/*      */     } 
/*      */     
/* 1794 */     Object wrappedBean = bean;
/* 1795 */     if (mbd == null || !mbd.isSynthetic()) {
/* 1796 */       wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
/*      */     }
/*      */     
/*      */     try {
/* 1800 */       invokeInitMethods(beanName, wrappedBean, mbd);
/*      */     }
/* 1802 */     catch (Throwable ex) {
/* 1803 */       throw new BeanCreationException((mbd != null) ? mbd
/* 1804 */           .getResourceDescription() : null, beanName, "Invocation of init method failed", ex);
/*      */     } 
/*      */     
/* 1807 */     if (mbd == null || !mbd.isSynthetic()) {
/* 1808 */       wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
/*      */     }
/*      */     
/* 1811 */     return wrappedBean;
/*      */   }
/*      */   
/*      */   private void invokeAwareMethods(String beanName, Object bean) {
/* 1815 */     if (bean instanceof org.springframework.beans.factory.Aware) {
/* 1816 */       if (bean instanceof BeanNameAware) {
/* 1817 */         ((BeanNameAware)bean).setBeanName(beanName);
/*      */       }
/* 1819 */       if (bean instanceof BeanClassLoaderAware) {
/* 1820 */         ClassLoader bcl = getBeanClassLoader();
/* 1821 */         if (bcl != null) {
/* 1822 */           ((BeanClassLoaderAware)bean).setBeanClassLoader(bcl);
/*      */         }
/*      */       } 
/* 1825 */       if (bean instanceof BeanFactoryAware) {
/* 1826 */         ((BeanFactoryAware)bean).setBeanFactory((BeanFactory)this);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd) throws Throwable {
/* 1846 */     boolean isInitializingBean = bean instanceof InitializingBean;
/* 1847 */     if (isInitializingBean && (mbd == null || !mbd.hasAnyExternallyManagedInitMethod("afterPropertiesSet"))) {
/* 1848 */       if (this.logger.isTraceEnabled()) {
/* 1849 */         this.logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
/*      */       }
/* 1851 */       if (System.getSecurityManager() != null) {
/*      */         try {
/* 1853 */           AccessController.doPrivileged(() -> {
/*      */                 ((InitializingBean)bean).afterPropertiesSet();
/*      */                 return null;
/* 1856 */               }getAccessControlContext());
/*      */         }
/* 1858 */         catch (PrivilegedActionException pae) {
/* 1859 */           throw pae.getException();
/*      */         } 
/*      */       } else {
/*      */         
/* 1863 */         ((InitializingBean)bean).afterPropertiesSet();
/*      */       } 
/*      */     } 
/*      */     
/* 1867 */     if (mbd != null && bean.getClass() != NullBean.class) {
/* 1868 */       String initMethodName = mbd.getInitMethodName();
/* 1869 */       if (StringUtils.hasLength(initMethodName) && (!isInitializingBean || 
/* 1870 */         !"afterPropertiesSet".equals(initMethodName)) && 
/* 1871 */         !mbd.hasAnyExternallyManagedInitMethod(initMethodName)) {
/* 1872 */         invokeCustomInitMethod(beanName, bean, mbd);
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
/*      */   
/*      */   protected void invokeCustomInitMethod(String beanName, Object bean, RootBeanDefinition mbd) throws Throwable {
/* 1887 */     String initMethodName = mbd.getInitMethodName();
/* 1888 */     Assert.state((initMethodName != null), "No init method set");
/*      */ 
/*      */     
/* 1891 */     Method initMethod = mbd.isNonPublicAccessAllowed() ? BeanUtils.findMethod(bean.getClass(), initMethodName, new Class[0]) : ClassUtils.getMethodIfAvailable(bean.getClass(), initMethodName, new Class[0]);
/*      */     
/* 1893 */     if (initMethod == null) {
/* 1894 */       if (mbd.isEnforceInitMethod()) {
/* 1895 */         throw new BeanDefinitionValidationException("Could not find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
/*      */       }
/*      */ 
/*      */       
/* 1899 */       if (this.logger.isTraceEnabled()) {
/* 1900 */         this.logger.trace("No default init method named '" + initMethodName + "' found on bean with name '" + beanName + "'");
/*      */       }
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/* 1908 */     if (this.logger.isTraceEnabled()) {
/* 1909 */       this.logger.trace("Invoking init method  '" + initMethodName + "' on bean with name '" + beanName + "'");
/*      */     }
/* 1911 */     Method methodToInvoke = ClassUtils.getInterfaceMethodIfPossible(initMethod, bean.getClass());
/*      */     
/* 1913 */     if (System.getSecurityManager() != null) {
/* 1914 */       AccessController.doPrivileged(() -> {
/*      */             ReflectionUtils.makeAccessible(methodToInvoke);
/*      */             return null;
/*      */           });
/*      */       try {
/* 1919 */         AccessController.doPrivileged(() -> methodToInvoke.invoke(bean, new Object[0]), 
/* 1920 */             getAccessControlContext());
/*      */       }
/* 1922 */       catch (PrivilegedActionException pae) {
/* 1923 */         InvocationTargetException ex = (InvocationTargetException)pae.getException();
/* 1924 */         throw ex.getTargetException();
/*      */       } 
/*      */     } else {
/*      */       
/*      */       try {
/* 1929 */         ReflectionUtils.makeAccessible(methodToInvoke);
/* 1930 */         methodToInvoke.invoke(bean, new Object[0]);
/*      */       }
/* 1932 */       catch (InvocationTargetException ex) {
/* 1933 */         throw ex.getTargetException();
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
/*      */   protected Object postProcessObjectFromFactoryBean(Object object, String beanName) {
/* 1947 */     return applyBeanPostProcessorsAfterInitialization(object, beanName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void removeSingleton(String beanName) {
/* 1955 */     synchronized (getSingletonMutex()) {
/* 1956 */       super.removeSingleton(beanName);
/* 1957 */       this.factoryBeanInstanceCache.remove(beanName);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void clearSingletonCache() {
/* 1966 */     synchronized (getSingletonMutex()) {
/* 1967 */       super.clearSingletonCache();
/* 1968 */       this.factoryBeanInstanceCache.clear();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Log getLogger() {
/* 1977 */     return this.logger;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class AutowireByTypeDependencyDescriptor
/*      */     extends DependencyDescriptor
/*      */   {
/*      */     public AutowireByTypeDependencyDescriptor(MethodParameter methodParameter, boolean eager) {
/* 1989 */       super(methodParameter, false, eager);
/*      */     }
/*      */ 
/*      */     
/*      */     public String getDependencyName() {
/* 1994 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class FactoryBeanMethodTypeFinder
/*      */     implements ReflectionUtils.MethodCallback
/*      */   {
/*      */     private final String factoryMethodName;
/*      */ 
/*      */     
/* 2006 */     private ResolvableType result = ResolvableType.NONE;
/*      */     
/*      */     FactoryBeanMethodTypeFinder(String factoryMethodName) {
/* 2009 */       this.factoryMethodName = factoryMethodName;
/*      */     }
/*      */ 
/*      */     
/*      */     public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
/* 2014 */       if (isFactoryBeanMethod(method)) {
/* 2015 */         ResolvableType returnType = ResolvableType.forMethodReturnType(method);
/* 2016 */         ResolvableType candidate = returnType.as(FactoryBean.class).getGeneric(new int[0]);
/* 2017 */         if (this.result == ResolvableType.NONE) {
/* 2018 */           this.result = candidate;
/*      */         } else {
/*      */           
/* 2021 */           Class<?> resolvedResult = this.result.resolve();
/* 2022 */           Class<?> commonAncestor = ClassUtils.determineCommonAncestor(candidate.resolve(), resolvedResult);
/* 2023 */           if (!ObjectUtils.nullSafeEquals(resolvedResult, commonAncestor)) {
/* 2024 */             this.result = ResolvableType.forClass(commonAncestor);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*      */     private boolean isFactoryBeanMethod(Method method) {
/* 2031 */       return (method.getName().equals(this.factoryMethodName) && FactoryBean.class
/* 2032 */         .isAssignableFrom(method.getReturnType()));
/*      */     }
/*      */     
/*      */     ResolvableType getResult() {
/* 2036 */       Class<?> resolved = this.result.resolve();
/* 2037 */       boolean foundResult = (resolved != null && resolved != Object.class);
/* 2038 */       return foundResult ? this.result : ResolvableType.NONE;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */