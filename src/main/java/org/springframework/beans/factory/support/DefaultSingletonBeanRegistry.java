/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.springframework.beans.factory.BeanCreationException;
/*     */ import org.springframework.beans.factory.BeanCreationNotAllowedException;
/*     */ import org.springframework.beans.factory.BeanCurrentlyInCreationException;
/*     */ import org.springframework.beans.factory.DisposableBean;
/*     */ import org.springframework.beans.factory.ObjectFactory;
/*     */ import org.springframework.beans.factory.config.SingletonBeanRegistry;
/*     */ import org.springframework.core.SimpleAliasRegistry;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ public class DefaultSingletonBeanRegistry
/*     */   extends SimpleAliasRegistry
/*     */   implements SingletonBeanRegistry
/*     */ {
/*     */   private static final int SUPPRESSED_EXCEPTIONS_LIMIT = 100;
/*  78 */   private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
/*     */ 
/*     */   
/*  81 */   private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
/*     */ 
/*     */   
/*  84 */   private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
/*     */ 
/*     */   
/*  87 */   private final Set<String> registeredSingletons = new LinkedHashSet<>(256);
/*     */ 
/*     */ 
/*     */   
/*  91 */   private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
/*     */ 
/*     */ 
/*     */   
/*  95 */   private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Set<Exception> suppressedExceptions;
/*     */ 
/*     */   
/*     */   private boolean singletonsCurrentlyInDestruction = false;
/*     */ 
/*     */   
/* 105 */   private final Map<String, Object> disposableBeans = new LinkedHashMap<>();
/*     */ 
/*     */   
/* 108 */   private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<>(16);
/*     */ 
/*     */   
/* 111 */   private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);
/*     */ 
/*     */   
/* 114 */   private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
/* 119 */     Assert.notNull(beanName, "Bean name must not be null");
/* 120 */     Assert.notNull(singletonObject, "Singleton object must not be null");
/* 121 */     synchronized (this.singletonObjects) {
/* 122 */       Object oldObject = this.singletonObjects.get(beanName);
/* 123 */       if (oldObject != null) {
/* 124 */         throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
/*     */       }
/*     */       
/* 127 */       addSingleton(beanName, singletonObject);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addSingleton(String beanName, Object singletonObject) {
/* 138 */     synchronized (this.singletonObjects) {
/* 139 */       this.singletonObjects.put(beanName, singletonObject);
/* 140 */       this.singletonFactories.remove(beanName);
/* 141 */       this.earlySingletonObjects.remove(beanName);
/* 142 */       this.registeredSingletons.add(beanName);
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
/*     */   protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
/* 155 */     Assert.notNull(singletonFactory, "Singleton factory must not be null");
/* 156 */     synchronized (this.singletonObjects) {
/* 157 */       if (!this.singletonObjects.containsKey(beanName)) {
/* 158 */         this.singletonFactories.put(beanName, singletonFactory);
/* 159 */         this.earlySingletonObjects.remove(beanName);
/* 160 */         this.registeredSingletons.add(beanName);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getSingleton(String beanName) {
/* 168 */     return getSingleton(beanName, true);
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
/*     */   @Nullable
/*     */   protected Object getSingleton(String beanName, boolean allowEarlyReference) {
/* 182 */     Object singletonObject = this.singletonObjects.get(beanName);
/* 183 */     if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
/* 184 */       singletonObject = this.earlySingletonObjects.get(beanName);
/* 185 */       if (singletonObject == null && allowEarlyReference) {
/* 186 */         synchronized (this.singletonObjects) {
/*     */           
/* 188 */           singletonObject = this.singletonObjects.get(beanName);
/* 189 */           if (singletonObject == null) {
/* 190 */             singletonObject = this.earlySingletonObjects.get(beanName);
/* 191 */             if (singletonObject == null) {
/* 192 */               ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
/* 193 */               if (singletonFactory != null) {
/* 194 */                 singletonObject = singletonFactory.getObject();
/* 195 */                 this.earlySingletonObjects.put(beanName, singletonObject);
/* 196 */                 this.singletonFactories.remove(beanName);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 203 */     return singletonObject;
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
/*     */   public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
/* 215 */     Assert.notNull(beanName, "Bean name must not be null");
/* 216 */     synchronized (this.singletonObjects) {
/* 217 */       Object singletonObject = this.singletonObjects.get(beanName);
/* 218 */       if (singletonObject == null) {
/* 219 */         if (this.singletonsCurrentlyInDestruction) {
/* 220 */           throw new BeanCreationNotAllowedException(beanName, "Singleton bean creation not allowed while singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)");
/*     */         }
/*     */ 
/*     */         
/* 224 */         if (this.logger.isDebugEnabled()) {
/* 225 */           this.logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
/*     */         }
/* 227 */         beforeSingletonCreation(beanName);
/* 228 */         boolean newSingleton = false;
/* 229 */         boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
/* 230 */         if (recordSuppressedExceptions) {
/* 231 */           this.suppressedExceptions = new LinkedHashSet<>();
/*     */         }
/*     */         try {
/* 234 */           singletonObject = singletonFactory.getObject();
/* 235 */           newSingleton = true;
/*     */         }
/* 237 */         catch (IllegalStateException ex) {
/*     */ 
/*     */           
/* 240 */           singletonObject = this.singletonObjects.get(beanName);
/* 241 */           if (singletonObject == null) {
/* 242 */             throw ex;
/*     */           }
/*     */         }
/* 245 */         catch (BeanCreationException ex) {
/* 246 */           if (recordSuppressedExceptions) {
/* 247 */             for (Exception suppressedException : this.suppressedExceptions) {
/* 248 */               ex.addRelatedCause(suppressedException);
/*     */             }
/*     */           }
/* 251 */           throw ex;
/*     */         } finally {
/*     */           
/* 254 */           if (recordSuppressedExceptions) {
/* 255 */             this.suppressedExceptions = null;
/*     */           }
/* 257 */           afterSingletonCreation(beanName);
/*     */         } 
/* 259 */         if (newSingleton) {
/* 260 */           addSingleton(beanName, singletonObject);
/*     */         }
/*     */       } 
/* 263 */       return singletonObject;
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
/*     */   protected void onSuppressedException(Exception ex) {
/* 277 */     synchronized (this.singletonObjects) {
/* 278 */       if (this.suppressedExceptions != null && this.suppressedExceptions.size() < 100) {
/* 279 */         this.suppressedExceptions.add(ex);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void removeSingleton(String beanName) {
/* 291 */     synchronized (this.singletonObjects) {
/* 292 */       this.singletonObjects.remove(beanName);
/* 293 */       this.singletonFactories.remove(beanName);
/* 294 */       this.earlySingletonObjects.remove(beanName);
/* 295 */       this.registeredSingletons.remove(beanName);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsSingleton(String beanName) {
/* 301 */     return this.singletonObjects.containsKey(beanName);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getSingletonNames() {
/* 306 */     synchronized (this.singletonObjects) {
/* 307 */       return StringUtils.toStringArray(this.registeredSingletons);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSingletonCount() {
/* 313 */     synchronized (this.singletonObjects) {
/* 314 */       return this.registeredSingletons.size();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentlyInCreation(String beanName, boolean inCreation) {
/* 320 */     Assert.notNull(beanName, "Bean name must not be null");
/* 321 */     if (!inCreation) {
/* 322 */       this.inCreationCheckExclusions.add(beanName);
/*     */     } else {
/*     */       
/* 325 */       this.inCreationCheckExclusions.remove(beanName);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isCurrentlyInCreation(String beanName) {
/* 330 */     Assert.notNull(beanName, "Bean name must not be null");
/* 331 */     return (!this.inCreationCheckExclusions.contains(beanName) && isActuallyInCreation(beanName));
/*     */   }
/*     */   
/*     */   protected boolean isActuallyInCreation(String beanName) {
/* 335 */     return isSingletonCurrentlyInCreation(beanName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSingletonCurrentlyInCreation(String beanName) {
/* 344 */     return this.singletonsCurrentlyInCreation.contains(beanName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void beforeSingletonCreation(String beanName) {
/* 354 */     if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
/* 355 */       throw new BeanCurrentlyInCreationException(beanName);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void afterSingletonCreation(String beanName) {
/* 366 */     if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
/* 367 */       throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
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
/*     */   public void registerDisposableBean(String beanName, DisposableBean bean) {
/* 382 */     synchronized (this.disposableBeans) {
/* 383 */       this.disposableBeans.put(beanName, bean);
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
/*     */   public void registerContainedBean(String containedBeanName, String containingBeanName) {
/* 397 */     synchronized (this.containedBeanMap) {
/*     */       
/* 399 */       Set<String> containedBeans = this.containedBeanMap.computeIfAbsent(containingBeanName, k -> new LinkedHashSet(8));
/* 400 */       if (!containedBeans.add(containedBeanName)) {
/*     */         return;
/*     */       }
/*     */     } 
/* 404 */     registerDependentBean(containedBeanName, containingBeanName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerDependentBean(String beanName, String dependentBeanName) {
/* 414 */     String canonicalName = canonicalName(beanName);
/*     */     
/* 416 */     synchronized (this.dependentBeanMap) {
/*     */       
/* 418 */       Set<String> dependentBeans = this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet(8));
/* 419 */       if (!dependentBeans.add(dependentBeanName)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/* 424 */     synchronized (this.dependenciesForBeanMap) {
/*     */       
/* 426 */       Set<String> dependenciesForBean = this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet(8));
/* 427 */       dependenciesForBean.add(canonicalName);
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
/*     */   protected boolean isDependent(String beanName, String dependentBeanName) {
/* 439 */     synchronized (this.dependentBeanMap) {
/* 440 */       return isDependent(beanName, dependentBeanName, (Set<String>)null);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
/* 445 */     if (alreadySeen != null && alreadySeen.contains(beanName)) {
/* 446 */       return false;
/*     */     }
/* 448 */     String canonicalName = canonicalName(beanName);
/* 449 */     Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
/* 450 */     if (dependentBeans == null) {
/* 451 */       return false;
/*     */     }
/* 453 */     if (dependentBeans.contains(dependentBeanName)) {
/* 454 */       return true;
/*     */     }
/* 456 */     for (String transitiveDependency : dependentBeans) {
/* 457 */       if (alreadySeen == null) {
/* 458 */         alreadySeen = new HashSet<>();
/*     */       }
/* 460 */       alreadySeen.add(beanName);
/* 461 */       if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
/* 462 */         return true;
/*     */       }
/*     */     } 
/* 465 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean hasDependentBean(String beanName) {
/* 473 */     return this.dependentBeanMap.containsKey(beanName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getDependentBeans(String beanName) {
/* 482 */     Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
/* 483 */     if (dependentBeans == null) {
/* 484 */       return new String[0];
/*     */     }
/* 486 */     synchronized (this.dependentBeanMap) {
/* 487 */       return StringUtils.toStringArray(dependentBeans);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getDependenciesForBean(String beanName) {
/* 498 */     Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
/* 499 */     if (dependenciesForBean == null) {
/* 500 */       return new String[0];
/*     */     }
/* 502 */     synchronized (this.dependenciesForBeanMap) {
/* 503 */       return StringUtils.toStringArray(dependenciesForBean);
/*     */     } 
/*     */   }
/*     */   public void destroySingletons() {
/*     */     String[] disposableBeanNames;
/* 508 */     if (this.logger.isTraceEnabled()) {
/* 509 */       this.logger.trace("Destroying singletons in " + this);
/*     */     }
/* 511 */     synchronized (this.singletonObjects) {
/* 512 */       this.singletonsCurrentlyInDestruction = true;
/*     */     } 
/*     */ 
/*     */     
/* 516 */     synchronized (this.disposableBeans) {
/* 517 */       disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
/*     */     } 
/* 519 */     for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
/* 520 */       destroySingleton(disposableBeanNames[i]);
/*     */     }
/*     */     
/* 523 */     this.containedBeanMap.clear();
/* 524 */     this.dependentBeanMap.clear();
/* 525 */     this.dependenciesForBeanMap.clear();
/*     */     
/* 527 */     clearSingletonCache();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void clearSingletonCache() {
/* 535 */     synchronized (this.singletonObjects) {
/* 536 */       this.singletonObjects.clear();
/* 537 */       this.singletonFactories.clear();
/* 538 */       this.earlySingletonObjects.clear();
/* 539 */       this.registeredSingletons.clear();
/* 540 */       this.singletonsCurrentlyInDestruction = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroySingleton(String beanName) {
/*     */     DisposableBean disposableBean;
/* 552 */     removeSingleton(beanName);
/*     */ 
/*     */ 
/*     */     
/* 556 */     synchronized (this.disposableBeans) {
/* 557 */       disposableBean = (DisposableBean)this.disposableBeans.remove(beanName);
/*     */     } 
/* 559 */     destroyBean(beanName, disposableBean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
/*     */     Set<String> dependencies, containedBeans;
/* 571 */     synchronized (this.dependentBeanMap) {
/*     */       
/* 573 */       dependencies = this.dependentBeanMap.remove(beanName);
/*     */     } 
/* 575 */     if (dependencies != null) {
/* 576 */       if (this.logger.isTraceEnabled()) {
/* 577 */         this.logger.trace("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
/*     */       }
/* 579 */       for (String dependentBeanName : dependencies) {
/* 580 */         destroySingleton(dependentBeanName);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 585 */     if (bean != null) {
/*     */       try {
/* 587 */         bean.destroy();
/*     */       }
/* 589 */       catch (Throwable ex) {
/* 590 */         if (this.logger.isWarnEnabled()) {
/* 591 */           this.logger.warn("Destruction of bean with name '" + beanName + "' threw an exception", ex);
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 598 */     synchronized (this.containedBeanMap) {
/*     */       
/* 600 */       containedBeans = this.containedBeanMap.remove(beanName);
/*     */     } 
/* 602 */     if (containedBeans != null) {
/* 603 */       for (String containedBeanName : containedBeans) {
/* 604 */         destroySingleton(containedBeanName);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 609 */     synchronized (this.dependentBeanMap) {
/* 610 */       for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext(); ) {
/* 611 */         Map.Entry<String, Set<String>> entry = it.next();
/* 612 */         Set<String> dependenciesToClean = entry.getValue();
/* 613 */         dependenciesToClean.remove(beanName);
/* 614 */         if (dependenciesToClean.isEmpty()) {
/* 615 */           it.remove();
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 621 */     this.dependenciesForBeanMap.remove(beanName);
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
/*     */   public final Object getSingletonMutex() {
/* 633 */     return this.singletonObjects;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */