/*      */ package org.springframework.beans.factory.support;
/*      */ 
/*      */ import java.beans.PropertyEditor;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import java.util.function.Predicate;
/*      */ import java.util.function.UnaryOperator;
/*      */ import org.springframework.beans.BeanUtils;
/*      */ import org.springframework.beans.BeanWrapper;
/*      */ import org.springframework.beans.BeansException;
/*      */ import org.springframework.beans.PropertyEditorRegistrar;
/*      */ import org.springframework.beans.PropertyEditorRegistry;
/*      */ import org.springframework.beans.PropertyEditorRegistrySupport;
/*      */ import org.springframework.beans.SimpleTypeConverter;
/*      */ import org.springframework.beans.TypeConverter;
/*      */ import org.springframework.beans.TypeMismatchException;
/*      */ import org.springframework.beans.factory.BeanCreationException;
/*      */ import org.springframework.beans.factory.BeanCurrentlyInCreationException;
/*      */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*      */ import org.springframework.beans.factory.BeanFactory;
/*      */ import org.springframework.beans.factory.BeanFactoryUtils;
/*      */ import org.springframework.beans.factory.BeanIsAbstractException;
/*      */ import org.springframework.beans.factory.BeanIsNotAFactoryException;
/*      */ import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
/*      */ import org.springframework.beans.factory.CannotLoadBeanClassException;
/*      */ import org.springframework.beans.factory.FactoryBean;
/*      */ import org.springframework.beans.factory.NoSuchBeanDefinitionException;
/*      */ import org.springframework.beans.factory.SmartFactoryBean;
/*      */ import org.springframework.beans.factory.config.BeanDefinition;
/*      */ import org.springframework.beans.factory.config.BeanDefinitionHolder;
/*      */ import org.springframework.beans.factory.config.BeanExpressionContext;
/*      */ import org.springframework.beans.factory.config.BeanExpressionResolver;
/*      */ import org.springframework.beans.factory.config.BeanPostProcessor;
/*      */ import org.springframework.beans.factory.config.ConfigurableBeanFactory;
/*      */ import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
/*      */ import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
/*      */ import org.springframework.beans.factory.config.Scope;
/*      */ import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
/*      */ import org.springframework.core.AttributeAccessor;
/*      */ import org.springframework.core.DecoratingClassLoader;
/*      */ import org.springframework.core.NamedThreadLocal;
/*      */ import org.springframework.core.ResolvableType;
/*      */ import org.springframework.core.convert.ConversionService;
/*      */ import org.springframework.core.log.LogMessage;
/*      */ import org.springframework.core.metrics.ApplicationStartup;
/*      */ import org.springframework.core.metrics.StartupStep;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
/*      */ import org.springframework.util.ClassUtils;
/*      */ import org.springframework.util.ObjectUtils;
/*      */ import org.springframework.util.StringUtils;
/*      */ import org.springframework.util.StringValueResolver;
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
/*      */ public abstract class AbstractBeanFactory
/*      */   extends FactoryBeanRegistrySupport
/*      */   implements ConfigurableBeanFactory
/*      */ {
/*      */   @Nullable
/*      */   private BeanFactory parentBeanFactory;
/*      */   @Nullable
/*  129 */   private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private ClassLoader tempClassLoader;
/*      */ 
/*      */   
/*      */   private boolean cacheBeanMetadata = true;
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private BeanExpressionResolver beanExpressionResolver;
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private ConversionService conversionService;
/*      */ 
/*      */   
/*  147 */   private final Set<PropertyEditorRegistrar> propertyEditorRegistrars = new LinkedHashSet<>(4);
/*      */ 
/*      */   
/*  150 */   private final Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap<>(4);
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private TypeConverter typeConverter;
/*      */ 
/*      */   
/*  157 */   private final List<StringValueResolver> embeddedValueResolvers = new CopyOnWriteArrayList<>();
/*      */ 
/*      */   
/*  160 */   private final List<BeanPostProcessor> beanPostProcessors = new BeanPostProcessorCacheAwareList();
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private volatile BeanPostProcessorCache beanPostProcessorCache;
/*      */ 
/*      */   
/*  167 */   private final Map<String, Scope> scopes = new LinkedHashMap<>(8);
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private SecurityContextProvider securityContextProvider;
/*      */ 
/*      */   
/*  174 */   private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);
/*      */ 
/*      */   
/*  177 */   private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));
/*      */ 
/*      */   
/*  180 */   private final ThreadLocal<Object> prototypesCurrentlyInCreation = (ThreadLocal<Object>)new NamedThreadLocal("Prototype beans currently in creation");
/*      */ 
/*      */ 
/*      */   
/*  184 */   private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;
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
/*      */   public AbstractBeanFactory(@Nullable BeanFactory parentBeanFactory) {
/*  198 */     this.parentBeanFactory = parentBeanFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getBean(String name) throws BeansException {
/*  208 */     return doGetBean(name, (Class<?>)null, (Object[])null, false);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
/*  213 */     return doGetBean(name, requiredType, (Object[])null, false);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getBean(String name, Object... args) throws BeansException {
/*  218 */     return doGetBean(name, (Class<?>)null, args, false);
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
/*      */   public <T> T getBean(String name, @Nullable Class<T> requiredType, @Nullable Object... args) throws BeansException {
/*  233 */     return doGetBean(name, requiredType, args, false);
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
/*      */   protected <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly) throws BeansException {
/*      */     Object beanInstance;
/*  252 */     String beanName = transformedBeanName(name);
/*      */ 
/*      */ 
/*      */     
/*  256 */     Object sharedInstance = getSingleton(beanName);
/*  257 */     if (sharedInstance != null && args == null) {
/*  258 */       if (this.logger.isTraceEnabled()) {
/*  259 */         if (isSingletonCurrentlyInCreation(beanName)) {
/*  260 */           this.logger.trace("Returning eagerly cached instance of singleton bean '" + beanName + "' that is not fully initialized yet - a consequence of a circular reference");
/*      */         }
/*      */         else {
/*      */           
/*  264 */           this.logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
/*      */         } 
/*      */       }
/*  267 */       beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, (RootBeanDefinition)null);
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  273 */       if (isPrototypeCurrentlyInCreation(beanName)) {
/*  274 */         throw new BeanCurrentlyInCreationException(beanName);
/*      */       }
/*      */ 
/*      */       
/*  278 */       BeanFactory parentBeanFactory = getParentBeanFactory();
/*  279 */       if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
/*      */         
/*  281 */         String nameToLookup = originalBeanName(name);
/*  282 */         if (parentBeanFactory instanceof AbstractBeanFactory) {
/*  283 */           return ((AbstractBeanFactory)parentBeanFactory).doGetBean(nameToLookup, requiredType, args, typeCheckOnly);
/*      */         }
/*      */         
/*  286 */         if (args != null)
/*      */         {
/*  288 */           return (T)parentBeanFactory.getBean(nameToLookup, args);
/*      */         }
/*  290 */         if (requiredType != null)
/*      */         {
/*  292 */           return (T)parentBeanFactory.getBean(nameToLookup, requiredType);
/*      */         }
/*      */         
/*  295 */         return (T)parentBeanFactory.getBean(nameToLookup);
/*      */       } 
/*      */ 
/*      */       
/*  299 */       if (!typeCheckOnly) {
/*  300 */         markBeanAsCreated(beanName);
/*      */       }
/*      */ 
/*      */       
/*  304 */       StartupStep beanCreation = this.applicationStartup.start("spring.beans.instantiate").tag("beanName", name);
/*      */       try {
/*  306 */         if (requiredType != null) {
/*  307 */           beanCreation.tag("beanType", requiredType::toString);
/*      */         }
/*  309 */         RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
/*  310 */         checkMergedBeanDefinition(mbd, beanName, args);
/*      */ 
/*      */         
/*  313 */         String[] dependsOn = mbd.getDependsOn();
/*  314 */         if (dependsOn != null) {
/*  315 */           for (String dep : dependsOn) {
/*  316 */             if (isDependent(beanName, dep)) {
/*  317 */               throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
/*      */             }
/*      */             
/*  320 */             registerDependentBean(dep, beanName);
/*      */             try {
/*  322 */               getBean(dep);
/*      */             }
/*  324 */             catch (NoSuchBeanDefinitionException ex) {
/*  325 */               throw new BeanCreationException(mbd.getResourceDescription(), beanName, "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
/*      */             } 
/*      */           } 
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  332 */         if (mbd.isSingleton()) {
/*  333 */           sharedInstance = getSingleton(beanName, () -> {
/*      */                 
/*      */                 try {
/*      */                   return createBean(beanName, mbd, args);
/*  337 */                 } catch (BeansException ex) {
/*      */                   destroySingleton(beanName);
/*      */ 
/*      */                   
/*      */                   throw ex;
/*      */                 } 
/*      */               });
/*      */           
/*  345 */           beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
/*      */         
/*      */         }
/*  348 */         else if (mbd.isPrototype()) {
/*      */           
/*  350 */           Object prototypeInstance = null;
/*      */           try {
/*  352 */             beforePrototypeCreation(beanName);
/*  353 */             prototypeInstance = createBean(beanName, mbd, args);
/*      */           } finally {
/*      */             
/*  356 */             afterPrototypeCreation(beanName);
/*      */           } 
/*  358 */           beanInstance = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
/*      */         }
/*      */         else {
/*      */           
/*  362 */           String scopeName = mbd.getScope();
/*  363 */           if (!StringUtils.hasLength(scopeName)) {
/*  364 */             throw new IllegalStateException("No scope name defined for bean '" + beanName + "'");
/*      */           }
/*  366 */           Scope scope = this.scopes.get(scopeName);
/*  367 */           if (scope == null) {
/*  368 */             throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
/*      */           }
/*      */           try {
/*  371 */             Object scopedInstance = scope.get(beanName, () -> {
/*      */                   beforePrototypeCreation(beanName);
/*      */                   
/*      */                   try {
/*      */                     return createBean(beanName, mbd, args);
/*      */                   } finally {
/*      */                     afterPrototypeCreation(beanName);
/*      */                   } 
/*      */                 });
/*  380 */             beanInstance = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
/*      */           }
/*  382 */           catch (IllegalStateException ex) {
/*  383 */             throw new ScopeNotActiveException(beanName, scopeName, ex);
/*      */           }
/*      */         
/*      */         } 
/*  387 */       } catch (BeansException ex) {
/*  388 */         beanCreation.tag("exception", ex.getClass().toString());
/*  389 */         beanCreation.tag("message", String.valueOf(ex.getMessage()));
/*  390 */         cleanupAfterBeanCreationFailure(beanName);
/*  391 */         throw ex;
/*      */       } finally {
/*      */         
/*  394 */         beanCreation.end();
/*      */       } 
/*      */     } 
/*      */     
/*  398 */     return adaptBeanInstance(name, beanInstance, requiredType);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   <T> T adaptBeanInstance(String name, Object bean, @Nullable Class<?> requiredType) {
/*  404 */     if (requiredType != null && !requiredType.isInstance(bean)) {
/*      */       try {
/*  406 */         Object convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
/*  407 */         if (convertedBean == null) {
/*  408 */           throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
/*      */         }
/*  410 */         return (T)convertedBean;
/*      */       }
/*  412 */       catch (TypeMismatchException ex) {
/*  413 */         if (this.logger.isTraceEnabled()) {
/*  414 */           this.logger.trace("Failed to convert bean '" + name + "' to required type '" + 
/*  415 */               ClassUtils.getQualifiedName(requiredType) + "'", (Throwable)ex);
/*      */         }
/*  417 */         throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
/*      */       } 
/*      */     }
/*  420 */     return (T)bean;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsBean(String name) {
/*  425 */     String beanName = transformedBeanName(name);
/*  426 */     if (containsSingleton(beanName) || containsBeanDefinition(beanName)) {
/*  427 */       return (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(name));
/*      */     }
/*      */     
/*  430 */     BeanFactory parentBeanFactory = getParentBeanFactory();
/*  431 */     return (parentBeanFactory != null && parentBeanFactory.containsBean(originalBeanName(name)));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
/*  436 */     String beanName = transformedBeanName(name);
/*      */     
/*  438 */     Object beanInstance = getSingleton(beanName, false);
/*  439 */     if (beanInstance != null) {
/*  440 */       if (beanInstance instanceof FactoryBean) {
/*  441 */         return (BeanFactoryUtils.isFactoryDereference(name) || ((FactoryBean)beanInstance).isSingleton());
/*      */       }
/*      */       
/*  444 */       return !BeanFactoryUtils.isFactoryDereference(name);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  449 */     BeanFactory parentBeanFactory = getParentBeanFactory();
/*  450 */     if (parentBeanFactory != null && !containsBeanDefinition(beanName))
/*      */     {
/*  452 */       return parentBeanFactory.isSingleton(originalBeanName(name));
/*      */     }
/*      */     
/*  455 */     RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
/*      */ 
/*      */     
/*  458 */     if (mbd.isSingleton()) {
/*  459 */       if (isFactoryBean(beanName, mbd)) {
/*  460 */         if (BeanFactoryUtils.isFactoryDereference(name)) {
/*  461 */           return true;
/*      */         }
/*  463 */         FactoryBean<?> factoryBean = (FactoryBean)getBean("&" + beanName);
/*  464 */         return factoryBean.isSingleton();
/*      */       } 
/*      */       
/*  467 */       return !BeanFactoryUtils.isFactoryDereference(name);
/*      */     } 
/*      */ 
/*      */     
/*  471 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
/*  477 */     String beanName = transformedBeanName(name);
/*      */     
/*  479 */     BeanFactory parentBeanFactory = getParentBeanFactory();
/*  480 */     if (parentBeanFactory != null && !containsBeanDefinition(beanName))
/*      */     {
/*  482 */       return parentBeanFactory.isPrototype(originalBeanName(name));
/*      */     }
/*      */     
/*  485 */     RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
/*  486 */     if (mbd.isPrototype())
/*      */     {
/*  488 */       return (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(beanName, mbd));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  493 */     if (BeanFactoryUtils.isFactoryDereference(name)) {
/*  494 */       return false;
/*      */     }
/*  496 */     if (isFactoryBean(beanName, mbd)) {
/*  497 */       FactoryBean<?> fb = (FactoryBean)getBean("&" + beanName);
/*  498 */       if (System.getSecurityManager() != null) {
/*  499 */         return ((Boolean)AccessController.<Boolean>doPrivileged(() -> Boolean.valueOf(
/*      */               
/*  501 */               ((fb instanceof SmartFactoryBean && ((SmartFactoryBean)fb).isPrototype()) || !fb.isSingleton())), 
/*      */             
/*  503 */             getAccessControlContext())).booleanValue();
/*      */       }
/*      */       
/*  506 */       return ((fb instanceof SmartFactoryBean && ((SmartFactoryBean)fb).isPrototype()) || 
/*  507 */         !fb.isSingleton());
/*      */     } 
/*      */ 
/*      */     
/*  511 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
/*  517 */     return isTypeMatch(name, typeToMatch, true);
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
/*      */   protected boolean isTypeMatch(String name, ResolvableType typeToMatch, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
/*  537 */     String beanName = transformedBeanName(name);
/*  538 */     boolean isFactoryDereference = BeanFactoryUtils.isFactoryDereference(name);
/*      */ 
/*      */     
/*  541 */     Object beanInstance = getSingleton(beanName, false);
/*  542 */     if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
/*  543 */       if (beanInstance instanceof FactoryBean) {
/*  544 */         if (!isFactoryDereference) {
/*  545 */           Class<?> type = getTypeForFactoryBean((FactoryBean)beanInstance);
/*  546 */           return (type != null && typeToMatch.isAssignableFrom(type));
/*      */         } 
/*      */         
/*  549 */         return typeToMatch.isInstance(beanInstance);
/*      */       } 
/*      */       
/*  552 */       if (!isFactoryDereference) {
/*  553 */         if (typeToMatch.isInstance(beanInstance))
/*      */         {
/*  555 */           return true;
/*      */         }
/*  557 */         if (typeToMatch.hasGenerics() && containsBeanDefinition(beanName)) {
/*      */           
/*  559 */           RootBeanDefinition rootBeanDefinition = getMergedLocalBeanDefinition(beanName);
/*  560 */           Class<?> targetType = rootBeanDefinition.getTargetType();
/*  561 */           if (targetType != null && targetType != ClassUtils.getUserClass(beanInstance)) {
/*      */             
/*  563 */             Class<?> clazz = typeToMatch.resolve();
/*  564 */             if (clazz != null && !clazz.isInstance(beanInstance)) {
/*  565 */               return false;
/*      */             }
/*  567 */             if (typeToMatch.isAssignableFrom(targetType)) {
/*  568 */               return true;
/*      */             }
/*      */           } 
/*  571 */           ResolvableType resolvableType = rootBeanDefinition.targetType;
/*  572 */           if (resolvableType == null) {
/*  573 */             resolvableType = rootBeanDefinition.factoryMethodReturnType;
/*      */           }
/*  575 */           return (resolvableType != null && typeToMatch.isAssignableFrom(resolvableType));
/*      */         } 
/*      */       } 
/*  578 */       return false;
/*      */     } 
/*  580 */     if (containsSingleton(beanName) && !containsBeanDefinition(beanName))
/*      */     {
/*  582 */       return false;
/*      */     }
/*      */ 
/*      */     
/*  586 */     BeanFactory parentBeanFactory = getParentBeanFactory();
/*  587 */     if (parentBeanFactory != null && !containsBeanDefinition(beanName))
/*      */     {
/*  589 */       return parentBeanFactory.isTypeMatch(originalBeanName(name), typeToMatch);
/*      */     }
/*      */ 
/*      */     
/*  593 */     RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
/*  594 */     BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
/*      */ 
/*      */     
/*  597 */     Class<?> classToMatch = typeToMatch.resolve();
/*  598 */     if (classToMatch == null) {
/*  599 */       classToMatch = FactoryBean.class;
/*      */     }
/*  601 */     (new Class[1])[0] = classToMatch; (new Class[2])[0] = FactoryBean.class; (new Class[2])[1] = classToMatch; Class<?>[] typesToMatch = (FactoryBean.class == classToMatch) ? new Class[1] : new Class[2];
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  606 */     Class<?> predictedType = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  611 */     if (!isFactoryDereference && dbd != null && isFactoryBean(beanName, mbd))
/*      */     {
/*      */       
/*  614 */       if (!mbd.isLazyInit() || allowFactoryBeanInit) {
/*  615 */         RootBeanDefinition tbd = getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
/*  616 */         Class<?> targetType = predictBeanType(dbd.getBeanName(), tbd, typesToMatch);
/*  617 */         if (targetType != null && !FactoryBean.class.isAssignableFrom(targetType)) {
/*  618 */           predictedType = targetType;
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  624 */     if (predictedType == null) {
/*  625 */       predictedType = predictBeanType(beanName, mbd, typesToMatch);
/*  626 */       if (predictedType == null) {
/*  627 */         return false;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  632 */     ResolvableType beanType = null;
/*      */ 
/*      */     
/*  635 */     if (FactoryBean.class.isAssignableFrom(predictedType)) {
/*  636 */       if (beanInstance == null && !isFactoryDereference) {
/*  637 */         beanType = getTypeForFactoryBean(beanName, mbd, allowFactoryBeanInit);
/*  638 */         predictedType = beanType.resolve();
/*  639 */         if (predictedType == null) {
/*  640 */           return false;
/*      */         }
/*      */       }
/*      */     
/*  644 */     } else if (isFactoryDereference) {
/*      */ 
/*      */ 
/*      */       
/*  648 */       predictedType = predictBeanType(beanName, mbd, new Class[] { FactoryBean.class });
/*  649 */       if (predictedType == null || !FactoryBean.class.isAssignableFrom(predictedType)) {
/*  650 */         return false;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  656 */     if (beanType == null) {
/*  657 */       ResolvableType definedType = mbd.targetType;
/*  658 */       if (definedType == null) {
/*  659 */         definedType = mbd.factoryMethodReturnType;
/*      */       }
/*  661 */       if (definedType != null && definedType.resolve() == predictedType) {
/*  662 */         beanType = definedType;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  667 */     if (beanType != null) {
/*  668 */       return typeToMatch.isAssignableFrom(beanType);
/*      */     }
/*      */ 
/*      */     
/*  672 */     return typeToMatch.isAssignableFrom(predictedType);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
/*  677 */     return isTypeMatch(name, ResolvableType.forRawClass(typeToMatch));
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
/*  683 */     return getType(name, true);
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
/*  689 */     String beanName = transformedBeanName(name);
/*      */ 
/*      */     
/*  692 */     Object beanInstance = getSingleton(beanName, false);
/*  693 */     if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
/*  694 */       if (beanInstance instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
/*  695 */         return getTypeForFactoryBean((FactoryBean)beanInstance);
/*      */       }
/*      */       
/*  698 */       return beanInstance.getClass();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  703 */     BeanFactory parentBeanFactory = getParentBeanFactory();
/*  704 */     if (parentBeanFactory != null && !containsBeanDefinition(beanName))
/*      */     {
/*  706 */       return parentBeanFactory.getType(originalBeanName(name));
/*      */     }
/*      */     
/*  709 */     RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
/*      */ 
/*      */ 
/*      */     
/*  713 */     BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
/*  714 */     if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
/*  715 */       RootBeanDefinition tbd = getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
/*  716 */       Class<?> targetClass = predictBeanType(dbd.getBeanName(), tbd, new Class[0]);
/*  717 */       if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
/*  718 */         return targetClass;
/*      */       }
/*      */     } 
/*      */     
/*  722 */     Class<?> beanClass = predictBeanType(beanName, mbd, new Class[0]);
/*      */ 
/*      */     
/*  725 */     if (beanClass != null && FactoryBean.class.isAssignableFrom(beanClass)) {
/*  726 */       if (!BeanFactoryUtils.isFactoryDereference(name))
/*      */       {
/*  728 */         return getTypeForFactoryBean(beanName, mbd, allowFactoryBeanInit).resolve();
/*      */       }
/*      */       
/*  731 */       return beanClass;
/*      */     } 
/*      */ 
/*      */     
/*  735 */     return !BeanFactoryUtils.isFactoryDereference(name) ? beanClass : null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getAliases(String name) {
/*  741 */     String beanName = transformedBeanName(name);
/*  742 */     List<String> aliases = new ArrayList<>();
/*  743 */     boolean factoryPrefix = name.startsWith("&");
/*  744 */     String fullBeanName = beanName;
/*  745 */     if (factoryPrefix) {
/*  746 */       fullBeanName = "&" + beanName;
/*      */     }
/*  748 */     if (!fullBeanName.equals(name)) {
/*  749 */       aliases.add(fullBeanName);
/*      */     }
/*  751 */     String[] retrievedAliases = super.getAliases(beanName);
/*  752 */     String prefix = factoryPrefix ? "&" : "";
/*  753 */     for (String retrievedAlias : retrievedAliases) {
/*  754 */       String alias = prefix + retrievedAlias;
/*  755 */       if (!alias.equals(name)) {
/*  756 */         aliases.add(alias);
/*      */       }
/*      */     } 
/*  759 */     if (!containsSingleton(beanName) && !containsBeanDefinition(beanName)) {
/*  760 */       BeanFactory parentBeanFactory = getParentBeanFactory();
/*  761 */       if (parentBeanFactory != null) {
/*  762 */         aliases.addAll(Arrays.asList(parentBeanFactory.getAliases(fullBeanName)));
/*      */       }
/*      */     } 
/*  765 */     return StringUtils.toStringArray(aliases);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public BeanFactory getParentBeanFactory() {
/*  776 */     return this.parentBeanFactory;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsLocalBean(String name) {
/*  781 */     String beanName = transformedBeanName(name);
/*  782 */     return ((containsSingleton(beanName) || containsBeanDefinition(beanName)) && (
/*  783 */       !BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(beanName)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setParentBeanFactory(@Nullable BeanFactory parentBeanFactory) {
/*  793 */     if (this.parentBeanFactory != null && this.parentBeanFactory != parentBeanFactory) {
/*  794 */       throw new IllegalStateException("Already associated with parent BeanFactory: " + this.parentBeanFactory);
/*      */     }
/*  796 */     if (this == parentBeanFactory) {
/*  797 */       throw new IllegalStateException("Cannot set parent bean factory to self");
/*      */     }
/*  799 */     this.parentBeanFactory = parentBeanFactory;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
/*  804 */     this.beanClassLoader = (beanClassLoader != null) ? beanClassLoader : ClassUtils.getDefaultClassLoader();
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public ClassLoader getBeanClassLoader() {
/*  810 */     return this.beanClassLoader;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTempClassLoader(@Nullable ClassLoader tempClassLoader) {
/*  815 */     this.tempClassLoader = tempClassLoader;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public ClassLoader getTempClassLoader() {
/*  821 */     return this.tempClassLoader;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCacheBeanMetadata(boolean cacheBeanMetadata) {
/*  826 */     this.cacheBeanMetadata = cacheBeanMetadata;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isCacheBeanMetadata() {
/*  831 */     return this.cacheBeanMetadata;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver) {
/*  836 */     this.beanExpressionResolver = resolver;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public BeanExpressionResolver getBeanExpressionResolver() {
/*  842 */     return this.beanExpressionResolver;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setConversionService(@Nullable ConversionService conversionService) {
/*  847 */     this.conversionService = conversionService;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public ConversionService getConversionService() {
/*  853 */     return this.conversionService;
/*      */   }
/*      */ 
/*      */   
/*      */   public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
/*  858 */     Assert.notNull(registrar, "PropertyEditorRegistrar must not be null");
/*  859 */     this.propertyEditorRegistrars.add(registrar);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
/*  866 */     return this.propertyEditorRegistrars;
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass) {
/*  871 */     Assert.notNull(requiredType, "Required type must not be null");
/*  872 */     Assert.notNull(propertyEditorClass, "PropertyEditor class must not be null");
/*  873 */     this.customEditors.put(requiredType, propertyEditorClass);
/*      */   }
/*      */ 
/*      */   
/*      */   public void copyRegisteredEditorsTo(PropertyEditorRegistry registry) {
/*  878 */     registerCustomEditors(registry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<Class<?>, Class<? extends PropertyEditor>> getCustomEditors() {
/*  885 */     return this.customEditors;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTypeConverter(TypeConverter typeConverter) {
/*  890 */     this.typeConverter = typeConverter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected TypeConverter getCustomTypeConverter() {
/*  899 */     return this.typeConverter;
/*      */   }
/*      */ 
/*      */   
/*      */   public TypeConverter getTypeConverter() {
/*  904 */     TypeConverter customConverter = getCustomTypeConverter();
/*  905 */     if (customConverter != null) {
/*  906 */       return customConverter;
/*      */     }
/*      */ 
/*      */     
/*  910 */     SimpleTypeConverter typeConverter = new SimpleTypeConverter();
/*  911 */     typeConverter.setConversionService(getConversionService());
/*  912 */     registerCustomEditors((PropertyEditorRegistry)typeConverter);
/*  913 */     return (TypeConverter)typeConverter;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
/*  919 */     Assert.notNull(valueResolver, "StringValueResolver must not be null");
/*  920 */     this.embeddedValueResolvers.add(valueResolver);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasEmbeddedValueResolver() {
/*  925 */     return !this.embeddedValueResolvers.isEmpty();
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String resolveEmbeddedValue(@Nullable String value) {
/*  931 */     if (value == null) {
/*  932 */       return null;
/*      */     }
/*  934 */     String result = value;
/*  935 */     for (StringValueResolver resolver : this.embeddedValueResolvers) {
/*  936 */       result = resolver.resolveStringValue(result);
/*  937 */       if (result == null) {
/*  938 */         return null;
/*      */       }
/*      */     } 
/*  941 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
/*  946 */     Assert.notNull(beanPostProcessor, "BeanPostProcessor must not be null");
/*      */     
/*  948 */     this.beanPostProcessors.remove(beanPostProcessor);
/*      */     
/*  950 */     this.beanPostProcessors.add(beanPostProcessor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors) {
/*  960 */     this.beanPostProcessors.removeAll(beanPostProcessors);
/*  961 */     this.beanPostProcessors.addAll(beanPostProcessors);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getBeanPostProcessorCount() {
/*  966 */     return this.beanPostProcessors.size();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<BeanPostProcessor> getBeanPostProcessors() {
/*  974 */     return this.beanPostProcessors;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   BeanPostProcessorCache getBeanPostProcessorCache() {
/*  983 */     BeanPostProcessorCache bpCache = this.beanPostProcessorCache;
/*  984 */     if (bpCache == null) {
/*  985 */       bpCache = new BeanPostProcessorCache();
/*  986 */       for (BeanPostProcessor bp : this.beanPostProcessors) {
/*  987 */         if (bp instanceof InstantiationAwareBeanPostProcessor) {
/*  988 */           bpCache.instantiationAware.add((InstantiationAwareBeanPostProcessor)bp);
/*  989 */           if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
/*  990 */             bpCache.smartInstantiationAware.add((SmartInstantiationAwareBeanPostProcessor)bp);
/*      */           }
/*      */         } 
/*  993 */         if (bp instanceof DestructionAwareBeanPostProcessor) {
/*  994 */           bpCache.destructionAware.add((DestructionAwareBeanPostProcessor)bp);
/*      */         }
/*  996 */         if (bp instanceof MergedBeanDefinitionPostProcessor) {
/*  997 */           bpCache.mergedDefinition.add((MergedBeanDefinitionPostProcessor)bp);
/*      */         }
/*      */       } 
/* 1000 */       this.beanPostProcessorCache = bpCache;
/*      */     } 
/* 1002 */     return bpCache;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean hasInstantiationAwareBeanPostProcessors() {
/* 1012 */     return !(getBeanPostProcessorCache()).instantiationAware.isEmpty();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean hasDestructionAwareBeanPostProcessors() {
/* 1022 */     return !(getBeanPostProcessorCache()).destructionAware.isEmpty();
/*      */   }
/*      */ 
/*      */   
/*      */   public void registerScope(String scopeName, Scope scope) {
/* 1027 */     Assert.notNull(scopeName, "Scope identifier must not be null");
/* 1028 */     Assert.notNull(scope, "Scope must not be null");
/* 1029 */     if ("singleton".equals(scopeName) || "prototype".equals(scopeName)) {
/* 1030 */       throw new IllegalArgumentException("Cannot replace existing scopes 'singleton' and 'prototype'");
/*      */     }
/* 1032 */     Scope previous = this.scopes.put(scopeName, scope);
/* 1033 */     if (previous != null && previous != scope) {
/* 1034 */       if (this.logger.isDebugEnabled()) {
/* 1035 */         this.logger.debug("Replacing scope '" + scopeName + "' from [" + previous + "] to [" + scope + "]");
/*      */       
/*      */       }
/*      */     }
/* 1039 */     else if (this.logger.isTraceEnabled()) {
/* 1040 */       this.logger.trace("Registering scope '" + scopeName + "' with implementation [" + scope + "]");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getRegisteredScopeNames() {
/* 1047 */     return StringUtils.toStringArray(this.scopes.keySet());
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Scope getRegisteredScope(String scopeName) {
/* 1053 */     Assert.notNull(scopeName, "Scope identifier must not be null");
/* 1054 */     return this.scopes.get(scopeName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSecurityContextProvider(SecurityContextProvider securityProvider) {
/* 1063 */     this.securityContextProvider = securityProvider;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setApplicationStartup(ApplicationStartup applicationStartup) {
/* 1068 */     Assert.notNull(applicationStartup, "applicationStartup should not be null");
/* 1069 */     this.applicationStartup = applicationStartup;
/*      */   }
/*      */ 
/*      */   
/*      */   public ApplicationStartup getApplicationStartup() {
/* 1074 */     return this.applicationStartup;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AccessControlContext getAccessControlContext() {
/* 1083 */     return (this.securityContextProvider != null) ? this.securityContextProvider
/* 1084 */       .getAccessControlContext() : 
/* 1085 */       AccessController.getContext();
/*      */   }
/*      */ 
/*      */   
/*      */   public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
/* 1090 */     Assert.notNull(otherFactory, "BeanFactory must not be null");
/* 1091 */     setBeanClassLoader(otherFactory.getBeanClassLoader());
/* 1092 */     setCacheBeanMetadata(otherFactory.isCacheBeanMetadata());
/* 1093 */     setBeanExpressionResolver(otherFactory.getBeanExpressionResolver());
/* 1094 */     setConversionService(otherFactory.getConversionService());
/* 1095 */     if (otherFactory instanceof AbstractBeanFactory) {
/* 1096 */       AbstractBeanFactory otherAbstractFactory = (AbstractBeanFactory)otherFactory;
/* 1097 */       this.propertyEditorRegistrars.addAll(otherAbstractFactory.propertyEditorRegistrars);
/* 1098 */       this.customEditors.putAll(otherAbstractFactory.customEditors);
/* 1099 */       this.typeConverter = otherAbstractFactory.typeConverter;
/* 1100 */       this.beanPostProcessors.addAll(otherAbstractFactory.beanPostProcessors);
/* 1101 */       this.scopes.putAll(otherAbstractFactory.scopes);
/* 1102 */       this.securityContextProvider = otherAbstractFactory.securityContextProvider;
/*      */     } else {
/*      */       
/* 1105 */       setTypeConverter(otherFactory.getTypeConverter());
/* 1106 */       String[] otherScopeNames = otherFactory.getRegisteredScopeNames();
/* 1107 */       for (String scopeName : otherScopeNames) {
/* 1108 */         this.scopes.put(scopeName, otherFactory.getRegisteredScope(scopeName));
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
/*      */   public BeanDefinition getMergedBeanDefinition(String name) throws BeansException {
/* 1126 */     String beanName = transformedBeanName(name);
/*      */     
/* 1128 */     if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
/* 1129 */       return ((ConfigurableBeanFactory)getParentBeanFactory()).getMergedBeanDefinition(beanName);
/*      */     }
/*      */     
/* 1132 */     return getMergedLocalBeanDefinition(beanName);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
/* 1137 */     String beanName = transformedBeanName(name);
/* 1138 */     Object beanInstance = getSingleton(beanName, false);
/* 1139 */     if (beanInstance != null) {
/* 1140 */       return beanInstance instanceof FactoryBean;
/*      */     }
/*      */     
/* 1143 */     if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory)
/*      */     {
/* 1145 */       return ((ConfigurableBeanFactory)getParentBeanFactory()).isFactoryBean(name);
/*      */     }
/* 1147 */     return isFactoryBean(beanName, getMergedLocalBeanDefinition(beanName));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isActuallyInCreation(String beanName) {
/* 1152 */     return (isSingletonCurrentlyInCreation(beanName) || isPrototypeCurrentlyInCreation(beanName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isPrototypeCurrentlyInCreation(String beanName) {
/* 1161 */     Object curVal = this.prototypesCurrentlyInCreation.get();
/* 1162 */     return (curVal != null && (curVal
/* 1163 */       .equals(beanName) || (curVal instanceof Set && ((Set)curVal).contains(beanName))));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void beforePrototypeCreation(String beanName) {
/* 1174 */     Object curVal = this.prototypesCurrentlyInCreation.get();
/* 1175 */     if (curVal == null) {
/* 1176 */       this.prototypesCurrentlyInCreation.set(beanName);
/*      */     }
/* 1178 */     else if (curVal instanceof String) {
/* 1179 */       Set<String> beanNameSet = new HashSet<>(2);
/* 1180 */       beanNameSet.add((String)curVal);
/* 1181 */       beanNameSet.add(beanName);
/* 1182 */       this.prototypesCurrentlyInCreation.set(beanNameSet);
/*      */     } else {
/*      */       
/* 1185 */       Set<String> beanNameSet = (Set<String>)curVal;
/* 1186 */       beanNameSet.add(beanName);
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
/*      */   protected void afterPrototypeCreation(String beanName) {
/* 1198 */     Object curVal = this.prototypesCurrentlyInCreation.get();
/* 1199 */     if (curVal instanceof String) {
/* 1200 */       this.prototypesCurrentlyInCreation.remove();
/*      */     }
/* 1202 */     else if (curVal instanceof Set) {
/* 1203 */       Set<String> beanNameSet = (Set<String>)curVal;
/* 1204 */       beanNameSet.remove(beanName);
/* 1205 */       if (beanNameSet.isEmpty()) {
/* 1206 */         this.prototypesCurrentlyInCreation.remove();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void destroyBean(String beanName, Object beanInstance) {
/* 1213 */     destroyBean(beanName, beanInstance, getMergedLocalBeanDefinition(beanName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void destroyBean(String beanName, Object bean, RootBeanDefinition mbd) {
/* 1224 */     (new DisposableBeanAdapter(bean, beanName, mbd, 
/* 1225 */         (getBeanPostProcessorCache()).destructionAware, getAccessControlContext())).destroy();
/*      */   }
/*      */ 
/*      */   
/*      */   public void destroyScopedBean(String beanName) {
/* 1230 */     RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
/* 1231 */     if (mbd.isSingleton() || mbd.isPrototype()) {
/* 1232 */       throw new IllegalArgumentException("Bean name '" + beanName + "' does not correspond to an object in a mutable scope");
/*      */     }
/*      */     
/* 1235 */     String scopeName = mbd.getScope();
/* 1236 */     Scope scope = this.scopes.get(scopeName);
/* 1237 */     if (scope == null) {
/* 1238 */       throw new IllegalStateException("No Scope SPI registered for scope name '" + scopeName + "'");
/*      */     }
/* 1240 */     Object bean = scope.remove(beanName);
/* 1241 */     if (bean != null) {
/* 1242 */       destroyBean(beanName, bean, mbd);
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
/*      */   protected String transformedBeanName(String name) {
/* 1258 */     return canonicalName(BeanFactoryUtils.transformedBeanName(name));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String originalBeanName(String name) {
/* 1267 */     String beanName = transformedBeanName(name);
/* 1268 */     if (name.startsWith("&")) {
/* 1269 */       beanName = "&" + beanName;
/*      */     }
/* 1271 */     return beanName;
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
/*      */   protected void initBeanWrapper(BeanWrapper bw) {
/* 1283 */     bw.setConversionService(getConversionService());
/* 1284 */     registerCustomEditors((PropertyEditorRegistry)bw);
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
/*      */   protected void registerCustomEditors(PropertyEditorRegistry registry) {
/* 1296 */     if (registry instanceof PropertyEditorRegistrySupport) {
/* 1297 */       ((PropertyEditorRegistrySupport)registry).useConfigValueEditors();
/*      */     }
/* 1299 */     if (!this.propertyEditorRegistrars.isEmpty()) {
/* 1300 */       for (PropertyEditorRegistrar registrar : this.propertyEditorRegistrars) {
/*      */         try {
/* 1302 */           registrar.registerCustomEditors(registry);
/*      */         }
/* 1304 */         catch (BeanCreationException ex) {
/* 1305 */           Throwable rootCause = ex.getMostSpecificCause();
/* 1306 */           if (rootCause instanceof BeanCurrentlyInCreationException) {
/* 1307 */             BeanCreationException bce = (BeanCreationException)rootCause;
/* 1308 */             String bceBeanName = bce.getBeanName();
/* 1309 */             if (bceBeanName != null && isCurrentlyInCreation(bceBeanName)) {
/* 1310 */               if (this.logger.isDebugEnabled()) {
/* 1311 */                 this.logger.debug("PropertyEditorRegistrar [" + registrar.getClass().getName() + "] failed because it tried to obtain currently created bean '" + ex
/*      */                     
/* 1313 */                     .getBeanName() + "': " + ex.getMessage());
/*      */               }
/* 1315 */               onSuppressedException((Exception)ex);
/*      */               continue;
/*      */             } 
/*      */           } 
/* 1319 */           throw ex;
/*      */         } 
/*      */       } 
/*      */     }
/* 1323 */     if (!this.customEditors.isEmpty()) {
/* 1324 */       this.customEditors.forEach((requiredType, editorClass) -> registry.registerCustomEditor(requiredType, (PropertyEditor)BeanUtils.instantiateClass(editorClass)));
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
/*      */   protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
/* 1340 */     RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
/* 1341 */     if (mbd != null && !mbd.stale) {
/* 1342 */       return mbd;
/*      */     }
/* 1344 */     return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
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
/*      */   protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd) throws BeanDefinitionStoreException {
/* 1358 */     return getMergedBeanDefinition(beanName, bd, (BeanDefinition)null);
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
/*      */   protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd) throws BeanDefinitionStoreException {
/* 1375 */     synchronized (this.mergedBeanDefinitions) {
/* 1376 */       RootBeanDefinition mbd = null;
/* 1377 */       RootBeanDefinition previous = null;
/*      */ 
/*      */       
/* 1380 */       if (containingBd == null) {
/* 1381 */         mbd = this.mergedBeanDefinitions.get(beanName);
/*      */       }
/*      */       
/* 1384 */       if (mbd == null || mbd.stale) {
/* 1385 */         previous = mbd;
/* 1386 */         if (bd.getParentName() == null) {
/*      */           
/* 1388 */           if (bd instanceof RootBeanDefinition) {
/* 1389 */             mbd = ((RootBeanDefinition)bd).cloneBeanDefinition();
/*      */           } else {
/*      */             
/* 1392 */             mbd = new RootBeanDefinition(bd);
/*      */           } 
/*      */         } else {
/*      */           BeanDefinition pbd;
/*      */ 
/*      */           
/*      */           try {
/* 1399 */             String parentBeanName = transformedBeanName(bd.getParentName());
/* 1400 */             if (!beanName.equals(parentBeanName)) {
/* 1401 */               pbd = getMergedBeanDefinition(parentBeanName);
/*      */             } else {
/*      */               
/* 1404 */               BeanFactory parent = getParentBeanFactory();
/* 1405 */               if (parent instanceof ConfigurableBeanFactory) {
/* 1406 */                 pbd = ((ConfigurableBeanFactory)parent).getMergedBeanDefinition(parentBeanName);
/*      */               } else {
/*      */                 
/* 1409 */                 throw new NoSuchBeanDefinitionException(parentBeanName, "Parent name '" + parentBeanName + "' is equal to bean name '" + beanName + "': cannot be resolved without a ConfigurableBeanFactory parent");
/*      */               }
/*      */             
/*      */             }
/*      */           
/*      */           }
/* 1415 */           catch (NoSuchBeanDefinitionException ex) {
/* 1416 */             throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName, "Could not resolve parent bean definition '" + bd
/* 1417 */                 .getParentName() + "'", ex);
/*      */           } 
/*      */           
/* 1420 */           mbd = new RootBeanDefinition(pbd);
/* 1421 */           mbd.overrideFrom(bd);
/*      */         } 
/*      */ 
/*      */         
/* 1425 */         if (!StringUtils.hasLength(mbd.getScope())) {
/* 1426 */           mbd.setScope("singleton");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1433 */         if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
/* 1434 */           mbd.setScope(containingBd.getScope());
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 1439 */         if (containingBd == null && isCacheBeanMetadata()) {
/* 1440 */           this.mergedBeanDefinitions.put(beanName, mbd);
/*      */         }
/*      */       } 
/* 1443 */       if (previous != null) {
/* 1444 */         copyRelevantMergedBeanDefinitionCaches(previous, mbd);
/*      */       }
/* 1446 */       return mbd;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void copyRelevantMergedBeanDefinitionCaches(RootBeanDefinition previous, RootBeanDefinition mbd) {
/* 1451 */     if (ObjectUtils.nullSafeEquals(mbd.getBeanClassName(), previous.getBeanClassName()) && 
/* 1452 */       ObjectUtils.nullSafeEquals(mbd.getFactoryBeanName(), previous.getFactoryBeanName()) && 
/* 1453 */       ObjectUtils.nullSafeEquals(mbd.getFactoryMethodName(), previous.getFactoryMethodName())) {
/* 1454 */       ResolvableType targetType = mbd.targetType;
/* 1455 */       ResolvableType previousTargetType = previous.targetType;
/* 1456 */       if (targetType == null || targetType.equals(previousTargetType)) {
/* 1457 */         mbd.targetType = previousTargetType;
/* 1458 */         mbd.isFactoryBean = previous.isFactoryBean;
/* 1459 */         mbd.resolvedTargetType = previous.resolvedTargetType;
/* 1460 */         mbd.factoryMethodReturnType = previous.factoryMethodReturnType;
/* 1461 */         mbd.factoryMethodToIntrospect = previous.factoryMethodToIntrospect;
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
/*      */   protected void checkMergedBeanDefinition(RootBeanDefinition mbd, String beanName, @Nullable Object[] args) throws BeanDefinitionStoreException {
/* 1477 */     if (mbd.isAbstract()) {
/* 1478 */       throw new BeanIsAbstractException(beanName);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void clearMergedBeanDefinition(String beanName) {
/* 1488 */     RootBeanDefinition bd = this.mergedBeanDefinitions.get(beanName);
/* 1489 */     if (bd != null) {
/* 1490 */       bd.stale = true;
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
/*      */   public void clearMetadataCache() {
/* 1503 */     this.mergedBeanDefinitions.forEach((beanName, bd) -> {
/*      */           if (!isBeanEligibleForMetadataCaching(beanName)) {
/*      */             bd.stale = true;
/*      */           }
/*      */         });
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
/*      */   protected Class<?> resolveBeanClass(RootBeanDefinition mbd, String beanName, Class<?>... typesToMatch) throws CannotLoadBeanClassException {
/*      */     try {
/* 1526 */       if (mbd.hasBeanClass()) {
/* 1527 */         return mbd.getBeanClass();
/*      */       }
/* 1529 */       if (System.getSecurityManager() != null) {
/* 1530 */         return AccessController.<Class<?>>doPrivileged(() -> doResolveBeanClass(mbd, typesToMatch), 
/* 1531 */             getAccessControlContext());
/*      */       }
/*      */       
/* 1534 */       return doResolveBeanClass(mbd, typesToMatch);
/*      */     
/*      */     }
/* 1537 */     catch (PrivilegedActionException pae) {
/* 1538 */       ClassNotFoundException ex = (ClassNotFoundException)pae.getException();
/* 1539 */       throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
/*      */     }
/* 1541 */     catch (ClassNotFoundException ex) {
/* 1542 */       throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
/*      */     }
/* 1544 */     catch (LinkageError err) {
/* 1545 */       throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), err);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?>... typesToMatch) throws ClassNotFoundException {
/* 1553 */     ClassLoader beanClassLoader = getBeanClassLoader();
/* 1554 */     ClassLoader dynamicLoader = beanClassLoader;
/* 1555 */     boolean freshResolve = false;
/*      */     
/* 1557 */     if (!ObjectUtils.isEmpty((Object[])typesToMatch)) {
/*      */ 
/*      */       
/* 1560 */       ClassLoader tempClassLoader = getTempClassLoader();
/* 1561 */       if (tempClassLoader != null) {
/* 1562 */         dynamicLoader = tempClassLoader;
/* 1563 */         freshResolve = true;
/* 1564 */         if (tempClassLoader instanceof DecoratingClassLoader) {
/* 1565 */           DecoratingClassLoader dcl = (DecoratingClassLoader)tempClassLoader;
/* 1566 */           for (Class<?> typeToMatch : typesToMatch) {
/* 1567 */             dcl.excludeClass(typeToMatch.getName());
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1573 */     String className = mbd.getBeanClassName();
/* 1574 */     if (className != null) {
/* 1575 */       Object evaluated = evaluateBeanDefinitionString(className, mbd);
/* 1576 */       if (!className.equals(evaluated)) {
/*      */         
/* 1578 */         if (evaluated instanceof Class) {
/* 1579 */           return (Class)evaluated;
/*      */         }
/* 1581 */         if (evaluated instanceof String) {
/* 1582 */           className = (String)evaluated;
/* 1583 */           freshResolve = true;
/*      */         } else {
/*      */           
/* 1586 */           throw new IllegalStateException("Invalid class name expression result: " + evaluated);
/*      */         } 
/*      */       } 
/* 1589 */       if (freshResolve) {
/*      */ 
/*      */         
/* 1592 */         if (dynamicLoader != null) {
/*      */           try {
/* 1594 */             return dynamicLoader.loadClass(className);
/*      */           }
/* 1596 */           catch (ClassNotFoundException ex) {
/* 1597 */             if (this.logger.isTraceEnabled()) {
/* 1598 */               this.logger.trace("Could not load class [" + className + "] from " + dynamicLoader + ": " + ex);
/*      */             }
/*      */           } 
/*      */         }
/* 1602 */         return ClassUtils.forName(className, dynamicLoader);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1607 */     return mbd.resolveBeanClass(beanClassLoader);
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
/*      */   protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
/* 1620 */     if (this.beanExpressionResolver == null) {
/* 1621 */       return value;
/*      */     }
/*      */     
/* 1624 */     Scope scope = null;
/* 1625 */     if (beanDefinition != null) {
/* 1626 */       String scopeName = beanDefinition.getScope();
/* 1627 */       if (scopeName != null) {
/* 1628 */         scope = getRegisteredScope(scopeName);
/*      */       }
/*      */     } 
/* 1631 */     return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
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
/*      */   @Nullable
/*      */   protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
/* 1652 */     Class<?> targetType = mbd.getTargetType();
/* 1653 */     if (targetType != null) {
/* 1654 */       return targetType;
/*      */     }
/* 1656 */     if (mbd.getFactoryMethodName() != null) {
/* 1657 */       return null;
/*      */     }
/* 1659 */     return resolveBeanClass(mbd, beanName, typesToMatch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
/* 1668 */     Boolean result = mbd.isFactoryBean;
/* 1669 */     if (result == null) {
/* 1670 */       Class<?> beanType = predictBeanType(beanName, mbd, new Class[] { FactoryBean.class });
/* 1671 */       result = Boolean.valueOf((beanType != null && FactoryBean.class.isAssignableFrom(beanType)));
/* 1672 */       mbd.isFactoryBean = result;
/*      */     } 
/* 1674 */     return result.booleanValue();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ResolvableType getTypeForFactoryBean(String beanName, RootBeanDefinition mbd, boolean allowInit) {
/* 1702 */     ResolvableType result = getTypeForFactoryBeanFromAttributes((AttributeAccessor)mbd);
/* 1703 */     if (result != ResolvableType.NONE) {
/* 1704 */       return result;
/*      */     }
/*      */     
/* 1707 */     if (allowInit && mbd.isSingleton()) {
/*      */       try {
/* 1709 */         FactoryBean<?> factoryBean = doGetBean("&" + beanName, FactoryBean.class, (Object[])null, true);
/* 1710 */         Class<?> objectType = getTypeForFactoryBean(factoryBean);
/* 1711 */         return (objectType != null) ? ResolvableType.forClass(objectType) : ResolvableType.NONE;
/*      */       }
/* 1713 */       catch (BeanCreationException ex) {
/* 1714 */         if (ex.contains(BeanCurrentlyInCreationException.class)) {
/* 1715 */           this.logger.trace(LogMessage.format("Bean currently in creation on FactoryBean type check: %s", ex));
/*      */         }
/* 1717 */         else if (mbd.isLazyInit()) {
/* 1718 */           this.logger.trace(LogMessage.format("Bean creation exception on lazy FactoryBean type check: %s", ex));
/*      */         } else {
/*      */           
/* 1721 */           this.logger.debug(LogMessage.format("Bean creation exception on eager FactoryBean type check: %s", ex));
/*      */         } 
/* 1723 */         onSuppressedException((Exception)ex);
/*      */       } 
/*      */     }
/* 1726 */     return ResolvableType.NONE;
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
/*      */   ResolvableType getTypeForFactoryBeanFromAttributes(AttributeAccessor attributes) {
/* 1738 */     Object attribute = attributes.getAttribute("factoryBeanObjectType");
/* 1739 */     if (attribute instanceof ResolvableType) {
/* 1740 */       return (ResolvableType)attribute;
/*      */     }
/* 1742 */     if (attribute instanceof Class) {
/* 1743 */       return ResolvableType.forClass((Class)attribute);
/*      */     }
/* 1745 */     return ResolvableType.NONE;
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
/*      */   @Nullable
/*      */   @Deprecated
/*      */   protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
/* 1767 */     return getTypeForFactoryBean(beanName, mbd, true).resolve();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void markBeanAsCreated(String beanName) {
/* 1777 */     if (!this.alreadyCreated.contains(beanName)) {
/* 1778 */       synchronized (this.mergedBeanDefinitions) {
/* 1779 */         if (!this.alreadyCreated.contains(beanName)) {
/*      */ 
/*      */           
/* 1782 */           clearMergedBeanDefinition(beanName);
/* 1783 */           this.alreadyCreated.add(beanName);
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void cleanupAfterBeanCreationFailure(String beanName) {
/* 1794 */     synchronized (this.mergedBeanDefinitions) {
/* 1795 */       this.alreadyCreated.remove(beanName);
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
/*      */   protected boolean isBeanEligibleForMetadataCaching(String beanName) {
/* 1807 */     return this.alreadyCreated.contains(beanName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean removeSingletonIfCreatedForTypeCheckOnly(String beanName) {
/* 1817 */     if (!this.alreadyCreated.contains(beanName)) {
/* 1818 */       removeSingleton(beanName);
/* 1819 */       return true;
/*      */     } 
/*      */     
/* 1822 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean hasBeanCreationStarted() {
/* 1833 */     return !this.alreadyCreated.isEmpty();
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
/*      */   protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {
/* 1849 */     if (BeanFactoryUtils.isFactoryDereference(name)) {
/* 1850 */       if (beanInstance instanceof NullBean) {
/* 1851 */         return beanInstance;
/*      */       }
/* 1853 */       if (!(beanInstance instanceof FactoryBean)) {
/* 1854 */         throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
/*      */       }
/* 1856 */       if (mbd != null) {
/* 1857 */         mbd.isFactoryBean = Boolean.valueOf(true);
/*      */       }
/* 1859 */       return beanInstance;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1865 */     if (!(beanInstance instanceof FactoryBean)) {
/* 1866 */       return beanInstance;
/*      */     }
/*      */     
/* 1869 */     Object object = null;
/* 1870 */     if (mbd != null) {
/* 1871 */       mbd.isFactoryBean = Boolean.valueOf(true);
/*      */     } else {
/*      */       
/* 1874 */       object = getCachedObjectForFactoryBean(beanName);
/*      */     } 
/* 1876 */     if (object == null) {
/*      */       
/* 1878 */       FactoryBean<?> factory = (FactoryBean)beanInstance;
/*      */       
/* 1880 */       if (mbd == null && containsBeanDefinition(beanName)) {
/* 1881 */         mbd = getMergedLocalBeanDefinition(beanName);
/*      */       }
/* 1883 */       boolean synthetic = (mbd != null && mbd.isSynthetic());
/* 1884 */       object = getObjectFromFactoryBean(factory, beanName, !synthetic);
/*      */     } 
/* 1886 */     return object;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBeanNameInUse(String beanName) {
/* 1896 */     return (isAlias(beanName) || containsLocalBean(beanName) || hasDependentBean(beanName));
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
/*      */   protected boolean requiresDestruction(Object bean, RootBeanDefinition mbd) {
/* 1910 */     return (bean.getClass() != NullBean.class && (DisposableBeanAdapter.hasDestroyMethod(bean, mbd) || (
/* 1911 */       hasDestructionAwareBeanPostProcessors() && DisposableBeanAdapter.hasApplicableProcessors(bean, 
/* 1912 */         (getBeanPostProcessorCache()).destructionAware))));
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
/*      */   protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
/* 1928 */     AccessControlContext acc = (System.getSecurityManager() != null) ? getAccessControlContext() : null;
/* 1929 */     if (!mbd.isPrototype() && requiresDestruction(bean, mbd)) {
/* 1930 */       if (mbd.isSingleton()) {
/*      */ 
/*      */ 
/*      */         
/* 1934 */         registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, mbd, 
/* 1935 */               (getBeanPostProcessorCache()).destructionAware, acc));
/*      */       }
/*      */       else {
/*      */         
/* 1939 */         Scope scope = this.scopes.get(mbd.getScope());
/* 1940 */         if (scope == null) {
/* 1941 */           throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
/*      */         }
/* 1943 */         scope.registerDestructionCallback(beanName, new DisposableBeanAdapter(bean, beanName, mbd, 
/* 1944 */               (getBeanPostProcessorCache()).destructionAware, acc));
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
/*      */   public AbstractBeanFactory() {}
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
/*      */   protected abstract boolean containsBeanDefinition(String paramString);
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
/*      */   protected abstract BeanDefinition getBeanDefinition(String paramString) throws BeansException;
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
/*      */   protected abstract Object createBean(String paramString, RootBeanDefinition paramRootBeanDefinition, @Nullable Object[] paramArrayOfObject) throws BeanCreationException;
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
/*      */   private class BeanPostProcessorCacheAwareList
/*      */     extends CopyOnWriteArrayList<BeanPostProcessor>
/*      */   {
/*      */     private BeanPostProcessorCacheAwareList() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BeanPostProcessor set(int index, BeanPostProcessor element) {
/* 2016 */       BeanPostProcessor result = super.set(index, element);
/* 2017 */       AbstractBeanFactory.this.beanPostProcessorCache = null;
/* 2018 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean add(BeanPostProcessor o) {
/* 2023 */       boolean success = super.add(o);
/* 2024 */       AbstractBeanFactory.this.beanPostProcessorCache = null;
/* 2025 */       return success;
/*      */     }
/*      */ 
/*      */     
/*      */     public void add(int index, BeanPostProcessor element) {
/* 2030 */       super.add(index, element);
/* 2031 */       AbstractBeanFactory.this.beanPostProcessorCache = null;
/*      */     }
/*      */ 
/*      */     
/*      */     public BeanPostProcessor remove(int index) {
/* 2036 */       BeanPostProcessor result = super.remove(index);
/* 2037 */       AbstractBeanFactory.this.beanPostProcessorCache = null;
/* 2038 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean remove(Object o) {
/* 2043 */       boolean success = super.remove(o);
/* 2044 */       if (success) {
/* 2045 */         AbstractBeanFactory.this.beanPostProcessorCache = null;
/*      */       }
/* 2047 */       return success;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean removeAll(Collection<?> c) {
/* 2052 */       boolean success = super.removeAll(c);
/* 2053 */       if (success) {
/* 2054 */         AbstractBeanFactory.this.beanPostProcessorCache = null;
/*      */       }
/* 2056 */       return success;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean retainAll(Collection<?> c) {
/* 2061 */       boolean success = super.retainAll(c);
/* 2062 */       if (success) {
/* 2063 */         AbstractBeanFactory.this.beanPostProcessorCache = null;
/*      */       }
/* 2065 */       return success;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean addAll(Collection<? extends BeanPostProcessor> c) {
/* 2070 */       boolean success = super.addAll(c);
/* 2071 */       if (success) {
/* 2072 */         AbstractBeanFactory.this.beanPostProcessorCache = null;
/*      */       }
/* 2074 */       return success;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean addAll(int index, Collection<? extends BeanPostProcessor> c) {
/* 2079 */       boolean success = super.addAll(index, c);
/* 2080 */       if (success) {
/* 2081 */         AbstractBeanFactory.this.beanPostProcessorCache = null;
/*      */       }
/* 2083 */       return success;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean removeIf(Predicate<? super BeanPostProcessor> filter) {
/* 2088 */       boolean success = super.removeIf(filter);
/* 2089 */       if (success) {
/* 2090 */         AbstractBeanFactory.this.beanPostProcessorCache = null;
/*      */       }
/* 2092 */       return success;
/*      */     }
/*      */ 
/*      */     
/*      */     public void replaceAll(UnaryOperator<BeanPostProcessor> operator) {
/* 2097 */       super.replaceAll(operator);
/* 2098 */       AbstractBeanFactory.this.beanPostProcessorCache = null;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class BeanPostProcessorCache
/*      */   {
/* 2110 */     final List<InstantiationAwareBeanPostProcessor> instantiationAware = new ArrayList<>();
/*      */     
/* 2112 */     final List<SmartInstantiationAwareBeanPostProcessor> smartInstantiationAware = new ArrayList<>();
/*      */     
/* 2114 */     final List<DestructionAwareBeanPostProcessor> destructionAware = new ArrayList<>();
/*      */     
/* 2116 */     final List<MergedBeanDefinitionPostProcessor> mergedDefinition = new ArrayList<>();
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/AbstractBeanFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */