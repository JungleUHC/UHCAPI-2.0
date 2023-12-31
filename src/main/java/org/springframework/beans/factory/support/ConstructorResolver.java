/*      */ package org.springframework.beans.factory.support;
/*      */ 
/*      */ import java.beans.ConstructorProperties;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Executable;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.security.AccessController;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Deque;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.springframework.beans.BeanWrapper;
/*      */ import org.springframework.beans.BeanWrapperImpl;
/*      */ import org.springframework.beans.BeansException;
/*      */ import org.springframework.beans.TypeConverter;
/*      */ import org.springframework.beans.TypeMismatchException;
/*      */ import org.springframework.beans.factory.BeanCreationException;
/*      */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*      */ import org.springframework.beans.factory.BeanFactory;
/*      */ import org.springframework.beans.factory.InjectionPoint;
/*      */ import org.springframework.beans.factory.NoSuchBeanDefinitionException;
/*      */ import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
/*      */ import org.springframework.beans.factory.UnsatisfiedDependencyException;
/*      */ import org.springframework.beans.factory.config.ConstructorArgumentValues;
/*      */ import org.springframework.beans.factory.config.DependencyDescriptor;
/*      */ import org.springframework.core.CollectionFactory;
/*      */ import org.springframework.core.MethodParameter;
/*      */ import org.springframework.core.NamedThreadLocal;
/*      */ import org.springframework.core.ParameterNameDiscoverer;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
/*      */ import org.springframework.util.ClassUtils;
/*      */ import org.springframework.util.MethodInvoker;
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
/*      */ class ConstructorResolver
/*      */ {
/*   86 */   private static final Object[] EMPTY_ARGS = new Object[0];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   92 */   private static final Object autowiredArgumentMarker = new Object();
/*      */   
/*   94 */   private static final NamedThreadLocal<InjectionPoint> currentInjectionPoint = new NamedThreadLocal("Current injection point");
/*      */ 
/*      */ 
/*      */   
/*      */   private final AbstractAutowireCapableBeanFactory beanFactory;
/*      */ 
/*      */ 
/*      */   
/*      */   private final Log logger;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory) {
/*  108 */     this.beanFactory = beanFactory;
/*  109 */     this.logger = beanFactory.getLogger();
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
/*      */   public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] chosenCtors, @Nullable Object[] explicitArgs) {
/*  130 */     BeanWrapperImpl bw = new BeanWrapperImpl();
/*  131 */     this.beanFactory.initBeanWrapper((BeanWrapper)bw);
/*      */     
/*  133 */     Constructor<?> constructorToUse = null;
/*  134 */     ArgumentsHolder argsHolderToUse = null;
/*  135 */     Object[] argsToUse = null;
/*      */     
/*  137 */     if (explicitArgs != null) {
/*  138 */       argsToUse = explicitArgs;
/*      */     } else {
/*      */       
/*  141 */       Object[] argsToResolve = null;
/*  142 */       synchronized (mbd.constructorArgumentLock) {
/*  143 */         constructorToUse = (Constructor)mbd.resolvedConstructorOrFactoryMethod;
/*  144 */         if (constructorToUse != null && mbd.constructorArgumentsResolved) {
/*      */           
/*  146 */           argsToUse = mbd.resolvedConstructorArguments;
/*  147 */           if (argsToUse == null) {
/*  148 */             argsToResolve = mbd.preparedConstructorArguments;
/*      */           }
/*      */         } 
/*      */       } 
/*  152 */       if (argsToResolve != null) {
/*  153 */         argsToUse = resolvePreparedArguments(beanName, mbd, (BeanWrapper)bw, constructorToUse, argsToResolve);
/*      */       }
/*      */     } 
/*      */     
/*  157 */     if (constructorToUse == null || argsToUse == null) {
/*      */       int minNrOfArgs;
/*  159 */       Constructor<?>[] candidates = chosenCtors;
/*  160 */       if (candidates == null) {
/*  161 */         Class<?> beanClass = mbd.getBeanClass();
/*      */         
/*      */         try {
/*  164 */           candidates = mbd.isNonPublicAccessAllowed() ? beanClass.getDeclaredConstructors() : beanClass.getConstructors();
/*      */         }
/*  166 */         catch (Throwable ex) {
/*  167 */           throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Resolution of declared constructors on bean Class [" + beanClass
/*  168 */               .getName() + "] from ClassLoader [" + beanClass
/*  169 */               .getClassLoader() + "] failed", ex);
/*      */         } 
/*      */       } 
/*      */       
/*  173 */       if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
/*  174 */         Constructor<?> uniqueCandidate = candidates[0];
/*  175 */         if (uniqueCandidate.getParameterCount() == 0) {
/*  176 */           synchronized (mbd.constructorArgumentLock) {
/*  177 */             mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
/*  178 */             mbd.constructorArgumentsResolved = true;
/*  179 */             mbd.resolvedConstructorArguments = EMPTY_ARGS;
/*      */           } 
/*  181 */           bw.setBeanInstance(instantiate(beanName, mbd, uniqueCandidate, EMPTY_ARGS));
/*  182 */           return (BeanWrapper)bw;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  188 */       boolean autowiring = (chosenCtors != null || mbd.getResolvedAutowireMode() == 3);
/*  189 */       ConstructorArgumentValues resolvedValues = null;
/*      */ 
/*      */       
/*  192 */       if (explicitArgs != null) {
/*  193 */         minNrOfArgs = explicitArgs.length;
/*      */       } else {
/*      */         
/*  196 */         ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
/*  197 */         resolvedValues = new ConstructorArgumentValues();
/*  198 */         minNrOfArgs = resolveConstructorArguments(beanName, mbd, (BeanWrapper)bw, cargs, resolvedValues);
/*      */       } 
/*      */       
/*  201 */       AutowireUtils.sortConstructors(candidates);
/*  202 */       int minTypeDiffWeight = Integer.MAX_VALUE;
/*  203 */       Set<Constructor<?>> ambiguousConstructors = null;
/*  204 */       Deque<UnsatisfiedDependencyException> causes = null;
/*      */       
/*  206 */       for (Constructor<?> candidate : candidates) {
/*  207 */         ArgumentsHolder argsHolder; int parameterCount = candidate.getParameterCount();
/*      */         
/*  209 */         if (constructorToUse != null && argsToUse != null && argsToUse.length > parameterCount) {
/*      */           break;
/*      */         }
/*      */ 
/*      */         
/*  214 */         if (parameterCount < minNrOfArgs) {
/*      */           continue;
/*      */         }
/*      */ 
/*      */         
/*  219 */         Class<?>[] paramTypes = candidate.getParameterTypes();
/*  220 */         if (resolvedValues != null) {
/*      */           try {
/*  222 */             String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, parameterCount);
/*  223 */             if (paramNames == null) {
/*  224 */               ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
/*  225 */               if (pnd != null) {
/*  226 */                 paramNames = pnd.getParameterNames(candidate);
/*      */               }
/*      */             } 
/*  229 */             argsHolder = createArgumentArray(beanName, mbd, resolvedValues, (BeanWrapper)bw, paramTypes, paramNames, 
/*  230 */                 getUserDeclaredConstructor(candidate), autowiring, (candidates.length == 1));
/*      */           }
/*  232 */           catch (UnsatisfiedDependencyException ex) {
/*  233 */             if (this.logger.isTraceEnabled()) {
/*  234 */               this.logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex);
/*      */             }
/*      */             
/*  237 */             if (causes == null) {
/*  238 */               causes = new ArrayDeque<>(1);
/*      */             }
/*  240 */             causes.add(ex);
/*      */           }
/*      */         
/*      */         }
/*      */         else {
/*      */           
/*  246 */           if (parameterCount != explicitArgs.length) {
/*      */             continue;
/*      */           }
/*  249 */           argsHolder = new ArgumentsHolder(explicitArgs);
/*      */         } 
/*      */ 
/*      */         
/*  253 */         int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
/*      */         
/*  255 */         if (typeDiffWeight < minTypeDiffWeight) {
/*  256 */           constructorToUse = candidate;
/*  257 */           argsHolderToUse = argsHolder;
/*  258 */           argsToUse = argsHolder.arguments;
/*  259 */           minTypeDiffWeight = typeDiffWeight;
/*  260 */           ambiguousConstructors = null;
/*      */         }
/*  262 */         else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
/*  263 */           if (ambiguousConstructors == null) {
/*  264 */             ambiguousConstructors = new LinkedHashSet<>();
/*  265 */             ambiguousConstructors.add(constructorToUse);
/*      */           } 
/*  267 */           ambiguousConstructors.add(candidate);
/*      */         } 
/*      */         continue;
/*      */       } 
/*  271 */       if (constructorToUse == null) {
/*  272 */         if (causes != null) {
/*  273 */           UnsatisfiedDependencyException ex = causes.removeLast();
/*  274 */           for (Exception cause : causes) {
/*  275 */             this.beanFactory.onSuppressedException(cause);
/*      */           }
/*  277 */           throw ex;
/*      */         } 
/*  279 */         throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Could not resolve matching constructor on bean class [" + mbd
/*  280 */             .getBeanClassName() + "] (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
/*      */       } 
/*      */       
/*  283 */       if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
/*  284 */         throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous constructor matches found on bean class [" + mbd
/*  285 */             .getBeanClassName() + "] (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousConstructors);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  290 */       if (explicitArgs == null && argsHolderToUse != null) {
/*  291 */         argsHolderToUse.storeCache(mbd, constructorToUse);
/*      */       }
/*      */     } 
/*      */     
/*  295 */     Assert.state((argsToUse != null), "Unresolved constructor arguments");
/*  296 */     bw.setBeanInstance(instantiate(beanName, mbd, constructorToUse, argsToUse));
/*  297 */     return (BeanWrapper)bw;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Object instantiate(String beanName, RootBeanDefinition mbd, Constructor<?> constructorToUse, Object[] argsToUse) {
/*      */     try {
/*  304 */       InstantiationStrategy strategy = this.beanFactory.getInstantiationStrategy();
/*  305 */       if (System.getSecurityManager() != null) {
/*  306 */         return AccessController.doPrivileged(() -> strategy.instantiate(mbd, beanName, (BeanFactory)this.beanFactory, constructorToUse, argsToUse), this.beanFactory
/*      */             
/*  308 */             .getAccessControlContext());
/*      */       }
/*      */       
/*  311 */       return strategy.instantiate(mbd, beanName, (BeanFactory)this.beanFactory, constructorToUse, argsToUse);
/*      */     
/*      */     }
/*  314 */     catch (Throwable ex) {
/*  315 */       throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean instantiation via constructor failed", ex);
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
/*      */   public void resolveFactoryMethodIfPossible(RootBeanDefinition mbd) {
/*      */     boolean isStatic;
/*  328 */     if (mbd.getFactoryBeanName() != null) {
/*  329 */       factoryClass = this.beanFactory.getType(mbd.getFactoryBeanName());
/*  330 */       isStatic = false;
/*      */     } else {
/*      */       
/*  333 */       factoryClass = mbd.getBeanClass();
/*  334 */       isStatic = true;
/*      */     } 
/*  336 */     Assert.state((factoryClass != null), "Unresolvable factory class");
/*  337 */     Class<?> factoryClass = ClassUtils.getUserClass(factoryClass);
/*      */     
/*  339 */     Method[] candidates = getCandidateMethods(factoryClass, mbd);
/*  340 */     Method uniqueCandidate = null;
/*  341 */     for (Method candidate : candidates) {
/*  342 */       if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
/*  343 */         if (uniqueCandidate == null) {
/*  344 */           uniqueCandidate = candidate;
/*      */         }
/*  346 */         else if (isParamMismatch(uniqueCandidate, candidate)) {
/*  347 */           uniqueCandidate = null;
/*      */           break;
/*      */         } 
/*      */       }
/*      */     } 
/*  352 */     mbd.factoryMethodToIntrospect = uniqueCandidate;
/*      */   }
/*      */   
/*      */   private boolean isParamMismatch(Method uniqueCandidate, Method candidate) {
/*  356 */     int uniqueCandidateParameterCount = uniqueCandidate.getParameterCount();
/*  357 */     int candidateParameterCount = candidate.getParameterCount();
/*  358 */     return (uniqueCandidateParameterCount != candidateParameterCount || 
/*  359 */       !Arrays.equals((Object[])uniqueCandidate.getParameterTypes(), (Object[])candidate.getParameterTypes()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Method[] getCandidateMethods(Class<?> factoryClass, RootBeanDefinition mbd) {
/*  368 */     if (System.getSecurityManager() != null) {
/*  369 */       return AccessController.<Method[]>doPrivileged(() -> mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  374 */     return mbd.isNonPublicAccessAllowed() ? 
/*  375 */       ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods();
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
/*      */   public BeanWrapper instantiateUsingFactoryMethod(String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {
/*      */     Object factoryBean;
/*      */     Class<?> factoryClass;
/*      */     boolean isStatic;
/*  397 */     BeanWrapperImpl bw = new BeanWrapperImpl();
/*  398 */     this.beanFactory.initBeanWrapper((BeanWrapper)bw);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  404 */     String factoryBeanName = mbd.getFactoryBeanName();
/*  405 */     if (factoryBeanName != null) {
/*  406 */       if (factoryBeanName.equals(beanName)) {
/*  407 */         throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
/*      */       }
/*      */       
/*  410 */       factoryBean = this.beanFactory.getBean(factoryBeanName);
/*  411 */       if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
/*  412 */         throw new ImplicitlyAppearedSingletonException();
/*      */       }
/*  414 */       this.beanFactory.registerDependentBean(factoryBeanName, beanName);
/*  415 */       factoryClass = factoryBean.getClass();
/*  416 */       isStatic = false;
/*      */     }
/*      */     else {
/*      */       
/*  420 */       if (!mbd.hasBeanClass()) {
/*  421 */         throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "bean definition declares neither a bean class nor a factory-bean reference");
/*      */       }
/*      */       
/*  424 */       factoryBean = null;
/*  425 */       factoryClass = mbd.getBeanClass();
/*  426 */       isStatic = true;
/*      */     } 
/*      */     
/*  429 */     Method factoryMethodToUse = null;
/*  430 */     ArgumentsHolder argsHolderToUse = null;
/*  431 */     Object[] argsToUse = null;
/*      */     
/*  433 */     if (explicitArgs != null) {
/*  434 */       argsToUse = explicitArgs;
/*      */     } else {
/*      */       
/*  437 */       Object[] argsToResolve = null;
/*  438 */       synchronized (mbd.constructorArgumentLock) {
/*  439 */         factoryMethodToUse = (Method)mbd.resolvedConstructorOrFactoryMethod;
/*  440 */         if (factoryMethodToUse != null && mbd.constructorArgumentsResolved) {
/*      */           
/*  442 */           argsToUse = mbd.resolvedConstructorArguments;
/*  443 */           if (argsToUse == null) {
/*  444 */             argsToResolve = mbd.preparedConstructorArguments;
/*      */           }
/*      */         } 
/*      */       } 
/*  448 */       if (argsToResolve != null) {
/*  449 */         argsToUse = resolvePreparedArguments(beanName, mbd, (BeanWrapper)bw, factoryMethodToUse, argsToResolve);
/*      */       }
/*      */     } 
/*      */     
/*  453 */     if (factoryMethodToUse == null || argsToUse == null) {
/*      */       int minNrOfArgs;
/*      */       
/*  456 */       factoryClass = ClassUtils.getUserClass(factoryClass);
/*      */       
/*  458 */       List<Method> candidates = null;
/*  459 */       if (mbd.isFactoryMethodUnique) {
/*  460 */         if (factoryMethodToUse == null) {
/*  461 */           factoryMethodToUse = mbd.getResolvedFactoryMethod();
/*      */         }
/*  463 */         if (factoryMethodToUse != null) {
/*  464 */           candidates = Collections.singletonList(factoryMethodToUse);
/*      */         }
/*      */       } 
/*  467 */       if (candidates == null) {
/*  468 */         candidates = new ArrayList<>();
/*  469 */         Method[] rawCandidates = getCandidateMethods(factoryClass, mbd);
/*  470 */         for (Method candidate : rawCandidates) {
/*  471 */           if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
/*  472 */             candidates.add(candidate);
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/*  477 */       if (candidates.size() == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
/*  478 */         Method uniqueCandidate = candidates.get(0);
/*  479 */         if (uniqueCandidate.getParameterCount() == 0) {
/*  480 */           mbd.factoryMethodToIntrospect = uniqueCandidate;
/*  481 */           synchronized (mbd.constructorArgumentLock) {
/*  482 */             mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
/*  483 */             mbd.constructorArgumentsResolved = true;
/*  484 */             mbd.resolvedConstructorArguments = EMPTY_ARGS;
/*      */           } 
/*  486 */           bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, uniqueCandidate, EMPTY_ARGS));
/*  487 */           return (BeanWrapper)bw;
/*      */         } 
/*      */       } 
/*      */       
/*  491 */       if (candidates.size() > 1) {
/*  492 */         candidates.sort(AutowireUtils.EXECUTABLE_COMPARATOR);
/*      */       }
/*      */       
/*  495 */       ConstructorArgumentValues resolvedValues = null;
/*  496 */       boolean autowiring = (mbd.getResolvedAutowireMode() == 3);
/*  497 */       int minTypeDiffWeight = Integer.MAX_VALUE;
/*  498 */       Set<Method> ambiguousFactoryMethods = null;
/*      */ 
/*      */       
/*  501 */       if (explicitArgs != null) {
/*  502 */         minNrOfArgs = explicitArgs.length;
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  507 */       else if (mbd.hasConstructorArgumentValues()) {
/*  508 */         ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
/*  509 */         resolvedValues = new ConstructorArgumentValues();
/*  510 */         minNrOfArgs = resolveConstructorArguments(beanName, mbd, (BeanWrapper)bw, cargs, resolvedValues);
/*      */       } else {
/*      */         
/*  513 */         minNrOfArgs = 0;
/*      */       } 
/*      */ 
/*      */       
/*  517 */       Deque<UnsatisfiedDependencyException> causes = null;
/*      */       
/*  519 */       for (Method candidate : candidates) {
/*  520 */         int parameterCount = candidate.getParameterCount();
/*      */         
/*  522 */         if (parameterCount >= minNrOfArgs) {
/*      */           ArgumentsHolder argsHolder;
/*      */           
/*  525 */           Class<?>[] paramTypes = candidate.getParameterTypes();
/*  526 */           if (explicitArgs != null) {
/*      */             
/*  528 */             if (paramTypes.length != explicitArgs.length) {
/*      */               continue;
/*      */             }
/*  531 */             argsHolder = new ArgumentsHolder(explicitArgs);
/*      */           } else {
/*      */ 
/*      */             
/*      */             try {
/*  536 */               String[] paramNames = null;
/*  537 */               ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
/*  538 */               if (pnd != null) {
/*  539 */                 paramNames = pnd.getParameterNames(candidate);
/*      */               }
/*  541 */               argsHolder = createArgumentArray(beanName, mbd, resolvedValues, (BeanWrapper)bw, paramTypes, paramNames, candidate, autowiring, 
/*  542 */                   (candidates.size() == 1));
/*      */             }
/*  544 */             catch (UnsatisfiedDependencyException ex) {
/*  545 */               if (this.logger.isTraceEnabled()) {
/*  546 */                 this.logger.trace("Ignoring factory method [" + candidate + "] of bean '" + beanName + "': " + ex);
/*      */               }
/*      */               
/*  549 */               if (causes == null) {
/*  550 */                 causes = new ArrayDeque<>(1);
/*      */               }
/*  552 */               causes.add(ex);
/*      */               
/*      */               continue;
/*      */             } 
/*      */           } 
/*      */           
/*  558 */           int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
/*      */           
/*  560 */           if (typeDiffWeight < minTypeDiffWeight) {
/*  561 */             factoryMethodToUse = candidate;
/*  562 */             argsHolderToUse = argsHolder;
/*  563 */             argsToUse = argsHolder.arguments;
/*  564 */             minTypeDiffWeight = typeDiffWeight;
/*  565 */             ambiguousFactoryMethods = null;
/*      */ 
/*      */             
/*      */             continue;
/*      */           } 
/*      */ 
/*      */           
/*  572 */           if (factoryMethodToUse != null && typeDiffWeight == minTypeDiffWeight && 
/*  573 */             !mbd.isLenientConstructorResolution() && paramTypes.length == factoryMethodToUse
/*  574 */             .getParameterCount() && 
/*  575 */             !Arrays.equals((Object[])paramTypes, (Object[])factoryMethodToUse.getParameterTypes())) {
/*  576 */             if (ambiguousFactoryMethods == null) {
/*  577 */               ambiguousFactoryMethods = new LinkedHashSet<>();
/*  578 */               ambiguousFactoryMethods.add(factoryMethodToUse);
/*      */             } 
/*  580 */             ambiguousFactoryMethods.add(candidate);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/*  585 */       if (factoryMethodToUse == null || argsToUse == null) {
/*  586 */         if (causes != null) {
/*  587 */           UnsatisfiedDependencyException ex = causes.removeLast();
/*  588 */           for (Exception cause : causes) {
/*  589 */             this.beanFactory.onSuppressedException(cause);
/*      */           }
/*  591 */           throw ex;
/*      */         } 
/*  593 */         List<String> argTypes = new ArrayList<>(minNrOfArgs);
/*  594 */         if (explicitArgs != null) {
/*  595 */           for (Object arg : explicitArgs) {
/*  596 */             argTypes.add((arg != null) ? arg.getClass().getSimpleName() : "null");
/*      */           }
/*      */         }
/*  599 */         else if (resolvedValues != null) {
/*  600 */           Set<ConstructorArgumentValues.ValueHolder> valueHolders = new LinkedHashSet<>(resolvedValues.getArgumentCount());
/*  601 */           valueHolders.addAll(resolvedValues.getIndexedArgumentValues().values());
/*  602 */           valueHolders.addAll(resolvedValues.getGenericArgumentValues());
/*  603 */           for (ConstructorArgumentValues.ValueHolder value : valueHolders) {
/*      */             
/*  605 */             String argType = (value.getType() != null) ? ClassUtils.getShortName(value.getType()) : ((value.getValue() != null) ? value.getValue().getClass().getSimpleName() : "null");
/*  606 */             argTypes.add(argType);
/*      */           } 
/*      */         } 
/*  609 */         String argDesc = StringUtils.collectionToCommaDelimitedString(argTypes);
/*  610 */         throw new BeanCreationException(mbd.getResourceDescription(), beanName, "No matching factory method found on class [" + factoryClass
/*  611 */             .getName() + "]: " + (
/*  612 */             (mbd.getFactoryBeanName() != null) ? ("factory bean '" + mbd
/*  613 */             .getFactoryBeanName() + "'; ") : "") + "factory method '" + mbd
/*  614 */             .getFactoryMethodName() + "(" + argDesc + ")'. Check that a method with the specified name " + ((minNrOfArgs > 0) ? "and arguments " : "") + "exists and that it is " + (isStatic ? "static" : "non-static") + ".");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  620 */       if (void.class == factoryMethodToUse.getReturnType()) {
/*  621 */         throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid factory method '" + mbd
/*  622 */             .getFactoryMethodName() + "' on class [" + factoryClass
/*  623 */             .getName() + "]: needs to have a non-void return type!");
/*      */       }
/*  625 */       if (ambiguousFactoryMethods != null) {
/*  626 */         throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous factory method matches found on class [" + factoryClass
/*  627 */             .getName() + "] (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousFactoryMethods);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  632 */       if (explicitArgs == null && argsHolderToUse != null) {
/*  633 */         mbd.factoryMethodToIntrospect = factoryMethodToUse;
/*  634 */         argsHolderToUse.storeCache(mbd, factoryMethodToUse);
/*      */       } 
/*      */     } 
/*      */     
/*  638 */     bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, factoryMethodToUse, argsToUse));
/*  639 */     return (BeanWrapper)bw;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Object instantiate(String beanName, RootBeanDefinition mbd, @Nullable Object factoryBean, Method factoryMethod, Object[] args) {
/*      */     try {
/*  646 */       if (System.getSecurityManager() != null) {
/*  647 */         return AccessController.doPrivileged(() -> this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, (BeanFactory)this.beanFactory, factoryBean, factoryMethod, args), this.beanFactory
/*      */ 
/*      */             
/*  650 */             .getAccessControlContext());
/*      */       }
/*      */       
/*  653 */       return this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, (BeanFactory)this.beanFactory, factoryBean, factoryMethod, args);
/*      */ 
/*      */     
/*      */     }
/*  657 */     catch (Throwable ex) {
/*  658 */       throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean instantiation via factory method failed", ex);
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
/*      */   private int resolveConstructorArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, ConstructorArgumentValues cargs, ConstructorArgumentValues resolvedValues) {
/*  671 */     TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
/*  672 */     TypeConverter converter = (customConverter != null) ? customConverter : (TypeConverter)bw;
/*  673 */     BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
/*      */ 
/*      */     
/*  676 */     int minNrOfArgs = cargs.getArgumentCount();
/*      */     
/*  678 */     for (Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry : (Iterable<Map.Entry<Integer, ConstructorArgumentValues.ValueHolder>>)cargs.getIndexedArgumentValues().entrySet()) {
/*  679 */       int index = ((Integer)entry.getKey()).intValue();
/*  680 */       if (index < 0) {
/*  681 */         throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid constructor argument index: " + index);
/*      */       }
/*      */       
/*  684 */       if (index + 1 > minNrOfArgs) {
/*  685 */         minNrOfArgs = index + 1;
/*      */       }
/*  687 */       ConstructorArgumentValues.ValueHolder valueHolder = entry.getValue();
/*  688 */       if (valueHolder.isConverted()) {
/*  689 */         resolvedValues.addIndexedArgumentValue(index, valueHolder);
/*      */         
/*      */         continue;
/*      */       } 
/*  693 */       Object resolvedValue = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
/*      */       
/*  695 */       ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
/*  696 */       resolvedValueHolder.setSource(valueHolder);
/*  697 */       resolvedValues.addIndexedArgumentValue(index, resolvedValueHolder);
/*      */     } 
/*      */ 
/*      */     
/*  701 */     for (ConstructorArgumentValues.ValueHolder valueHolder : cargs.getGenericArgumentValues()) {
/*  702 */       if (valueHolder.isConverted()) {
/*  703 */         resolvedValues.addGenericArgumentValue(valueHolder);
/*      */         
/*      */         continue;
/*      */       } 
/*  707 */       Object resolvedValue = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
/*      */       
/*  709 */       ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
/*  710 */       resolvedValueHolder.setSource(valueHolder);
/*  711 */       resolvedValues.addGenericArgumentValue(resolvedValueHolder);
/*      */     } 
/*      */ 
/*      */     
/*  715 */     return minNrOfArgs;
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
/*      */   private ArgumentsHolder createArgumentArray(String beanName, RootBeanDefinition mbd, @Nullable ConstructorArgumentValues resolvedValues, BeanWrapper bw, Class<?>[] paramTypes, @Nullable String[] paramNames, Executable executable, boolean autowiring, boolean fallback) throws UnsatisfiedDependencyException {
/*  727 */     TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
/*  728 */     TypeConverter converter = (customConverter != null) ? customConverter : (TypeConverter)bw;
/*      */     
/*  730 */     ArgumentsHolder args = new ArgumentsHolder(paramTypes.length);
/*  731 */     Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(paramTypes.length);
/*  732 */     Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
/*      */     
/*  734 */     for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
/*  735 */       Class<?> paramType = paramTypes[paramIndex];
/*  736 */       String paramName = (paramNames != null) ? paramNames[paramIndex] : "";
/*      */       
/*  738 */       ConstructorArgumentValues.ValueHolder valueHolder = null;
/*  739 */       if (resolvedValues != null) {
/*  740 */         valueHolder = resolvedValues.getArgumentValue(paramIndex, paramType, paramName, usedValueHolders);
/*      */ 
/*      */ 
/*      */         
/*  744 */         if (valueHolder == null && (!autowiring || paramTypes.length == resolvedValues.getArgumentCount())) {
/*  745 */           valueHolder = resolvedValues.getGenericArgumentValue(null, null, usedValueHolders);
/*      */         }
/*      */       } 
/*  748 */       if (valueHolder != null) {
/*      */         Object convertedValue;
/*      */         
/*  751 */         usedValueHolders.add(valueHolder);
/*  752 */         Object originalValue = valueHolder.getValue();
/*      */         
/*  754 */         if (valueHolder.isConverted()) {
/*  755 */           convertedValue = valueHolder.getConvertedValue();
/*  756 */           args.preparedArguments[paramIndex] = convertedValue;
/*      */         } else {
/*      */           
/*  759 */           MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
/*      */           try {
/*  761 */             convertedValue = converter.convertIfNecessary(originalValue, paramType, methodParam);
/*      */           }
/*  763 */           catch (TypeMismatchException ex) {
/*  764 */             throw new UnsatisfiedDependencyException(mbd
/*  765 */                 .getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + 
/*      */                 
/*  767 */                 ObjectUtils.nullSafeClassName(valueHolder.getValue()) + "] to required type [" + paramType
/*  768 */                 .getName() + "]: " + ex.getMessage());
/*      */           } 
/*  770 */           Object sourceHolder = valueHolder.getSource();
/*  771 */           if (sourceHolder instanceof ConstructorArgumentValues.ValueHolder) {
/*  772 */             Object sourceValue = ((ConstructorArgumentValues.ValueHolder)sourceHolder).getValue();
/*  773 */             args.resolveNecessary = true;
/*  774 */             args.preparedArguments[paramIndex] = sourceValue;
/*      */           } 
/*      */         } 
/*  777 */         args.arguments[paramIndex] = convertedValue;
/*  778 */         args.rawArguments[paramIndex] = originalValue;
/*      */       } else {
/*      */         
/*  781 */         MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
/*      */ 
/*      */         
/*  784 */         if (!autowiring) {
/*  785 */           throw new UnsatisfiedDependencyException(mbd
/*  786 */               .getResourceDescription(), beanName, new InjectionPoint(methodParam), "Ambiguous argument values for parameter of type [" + paramType
/*  787 */               .getName() + "] - did you specify the correct bean references as arguments?");
/*      */         }
/*      */         
/*      */         try {
/*  791 */           Object autowiredArgument = resolveAutowiredArgument(methodParam, beanName, autowiredBeanNames, converter, fallback);
/*      */           
/*  793 */           args.rawArguments[paramIndex] = autowiredArgument;
/*  794 */           args.arguments[paramIndex] = autowiredArgument;
/*  795 */           args.preparedArguments[paramIndex] = autowiredArgumentMarker;
/*  796 */           args.resolveNecessary = true;
/*      */         }
/*  798 */         catch (BeansException ex) {
/*  799 */           Object convertedValue; throw new UnsatisfiedDependencyException(mbd
/*  800 */               .getResourceDescription(), beanName, new InjectionPoint(methodParam), convertedValue);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  805 */     for (String autowiredBeanName : autowiredBeanNames) {
/*  806 */       this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
/*  807 */       if (this.logger.isDebugEnabled()) {
/*  808 */         this.logger.debug("Autowiring by type from bean name '" + beanName + "' via " + ((executable instanceof Constructor) ? "constructor" : "factory method") + " to bean named '" + autowiredBeanName + "'");
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  814 */     return args;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, Executable executable, Object[] argsToResolve) {
/*  823 */     TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
/*  824 */     TypeConverter converter = (customConverter != null) ? customConverter : (TypeConverter)bw;
/*  825 */     BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
/*      */     
/*  827 */     Class<?>[] paramTypes = executable.getParameterTypes();
/*      */     
/*  829 */     Object[] resolvedArgs = new Object[argsToResolve.length];
/*  830 */     for (int argIndex = 0; argIndex < argsToResolve.length; argIndex++) {
/*  831 */       Object argValue = argsToResolve[argIndex];
/*  832 */       MethodParameter methodParam = MethodParameter.forExecutable(executable, argIndex);
/*  833 */       if (argValue == autowiredArgumentMarker) {
/*  834 */         argValue = resolveAutowiredArgument(methodParam, beanName, null, converter, true);
/*      */       }
/*  836 */       else if (argValue instanceof org.springframework.beans.BeanMetadataElement) {
/*  837 */         argValue = valueResolver.resolveValueIfNecessary("constructor argument", argValue);
/*      */       }
/*  839 */       else if (argValue instanceof String) {
/*  840 */         argValue = this.beanFactory.evaluateBeanDefinitionString((String)argValue, mbd);
/*      */       } 
/*  842 */       Class<?> paramType = paramTypes[argIndex];
/*      */       try {
/*  844 */         resolvedArgs[argIndex] = converter.convertIfNecessary(argValue, paramType, methodParam);
/*      */       }
/*  846 */       catch (TypeMismatchException ex) {
/*  847 */         throw new UnsatisfiedDependencyException(mbd
/*  848 */             .getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + 
/*  849 */             ObjectUtils.nullSafeClassName(argValue) + "] to required type [" + paramType
/*  850 */             .getName() + "]: " + ex.getMessage());
/*      */       } 
/*      */     } 
/*  853 */     return resolvedArgs;
/*      */   }
/*      */   
/*      */   protected Constructor<?> getUserDeclaredConstructor(Constructor<?> constructor) {
/*  857 */     Class<?> declaringClass = constructor.getDeclaringClass();
/*  858 */     Class<?> userClass = ClassUtils.getUserClass(declaringClass);
/*  859 */     if (userClass != declaringClass) {
/*      */       try {
/*  861 */         return userClass.getDeclaredConstructor(constructor.getParameterTypes());
/*      */       }
/*  863 */       catch (NoSuchMethodException noSuchMethodException) {}
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  868 */     return constructor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected Object resolveAutowiredArgument(MethodParameter param, String beanName, @Nullable Set<String> autowiredBeanNames, TypeConverter typeConverter, boolean fallback) {
/*  878 */     Class<?> paramType = param.getParameterType();
/*  879 */     if (InjectionPoint.class.isAssignableFrom(paramType)) {
/*  880 */       InjectionPoint injectionPoint = (InjectionPoint)currentInjectionPoint.get();
/*  881 */       if (injectionPoint == null) {
/*  882 */         throw new IllegalStateException("No current InjectionPoint available for " + param);
/*      */       }
/*  884 */       return injectionPoint;
/*      */     } 
/*      */     try {
/*  887 */       return this.beanFactory.resolveDependency(new DependencyDescriptor(param, true), beanName, autowiredBeanNames, typeConverter);
/*      */     
/*      */     }
/*  890 */     catch (NoUniqueBeanDefinitionException ex) {
/*  891 */       throw ex;
/*      */     }
/*  893 */     catch (NoSuchBeanDefinitionException ex) {
/*  894 */       if (fallback) {
/*      */ 
/*      */         
/*  897 */         if (paramType.isArray()) {
/*  898 */           return Array.newInstance(paramType.getComponentType(), 0);
/*      */         }
/*  900 */         if (CollectionFactory.isApproximableCollectionType(paramType)) {
/*  901 */           return CollectionFactory.createCollection(paramType, 0);
/*      */         }
/*  903 */         if (CollectionFactory.isApproximableMapType(paramType)) {
/*  904 */           return CollectionFactory.createMap(paramType, 0);
/*      */         }
/*      */       } 
/*  907 */       throw ex;
/*      */     } 
/*      */   }
/*      */   
/*      */   static InjectionPoint setCurrentInjectionPoint(@Nullable InjectionPoint injectionPoint) {
/*  912 */     InjectionPoint old = (InjectionPoint)currentInjectionPoint.get();
/*  913 */     if (injectionPoint != null) {
/*  914 */       currentInjectionPoint.set(injectionPoint);
/*      */     } else {
/*      */       
/*  917 */       currentInjectionPoint.remove();
/*      */     } 
/*  919 */     return old;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class ArgumentsHolder
/*      */   {
/*      */     public final Object[] rawArguments;
/*      */ 
/*      */     
/*      */     public final Object[] arguments;
/*      */     
/*      */     public final Object[] preparedArguments;
/*      */     
/*      */     public boolean resolveNecessary = false;
/*      */ 
/*      */     
/*      */     public ArgumentsHolder(int size) {
/*  937 */       this.rawArguments = new Object[size];
/*  938 */       this.arguments = new Object[size];
/*  939 */       this.preparedArguments = new Object[size];
/*      */     }
/*      */     
/*      */     public ArgumentsHolder(Object[] args) {
/*  943 */       this.rawArguments = args;
/*  944 */       this.arguments = args;
/*  945 */       this.preparedArguments = args;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
/*  953 */       int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
/*  954 */       int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
/*  955 */       return Math.min(rawTypeDiffWeight, typeDiffWeight);
/*      */     }
/*      */     public int getAssignabilityWeight(Class<?>[] paramTypes) {
/*      */       int i;
/*  959 */       for (i = 0; i < paramTypes.length; i++) {
/*  960 */         if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
/*  961 */           return Integer.MAX_VALUE;
/*      */         }
/*      */       } 
/*  964 */       for (i = 0; i < paramTypes.length; i++) {
/*  965 */         if (!ClassUtils.isAssignableValue(paramTypes[i], this.rawArguments[i])) {
/*  966 */           return 2147483135;
/*      */         }
/*      */       } 
/*  969 */       return 2147482623;
/*      */     }
/*      */     
/*      */     public void storeCache(RootBeanDefinition mbd, Executable constructorOrFactoryMethod) {
/*  973 */       synchronized (mbd.constructorArgumentLock) {
/*  974 */         mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
/*  975 */         mbd.constructorArgumentsResolved = true;
/*  976 */         if (this.resolveNecessary) {
/*  977 */           mbd.preparedConstructorArguments = this.preparedArguments;
/*      */         } else {
/*      */           
/*  980 */           mbd.resolvedConstructorArguments = this.arguments;
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class ConstructorPropertiesChecker
/*      */   {
/*      */     @Nullable
/*      */     public static String[] evaluate(Constructor<?> candidate, int paramCount) {
/*  994 */       ConstructorProperties cp = candidate.<ConstructorProperties>getAnnotation(ConstructorProperties.class);
/*  995 */       if (cp != null) {
/*  996 */         String[] names = cp.value();
/*  997 */         if (names.length != paramCount) {
/*  998 */           throw new IllegalStateException("Constructor annotated with @ConstructorProperties but not corresponding to actual number of parameters (" + paramCount + "): " + candidate);
/*      */         }
/*      */         
/* 1001 */         return names;
/*      */       } 
/*      */       
/* 1004 */       return null;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/ConstructorResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */