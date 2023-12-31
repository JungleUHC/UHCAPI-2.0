/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ import org.springframework.beans.factory.DisposableBean;
/*     */ import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.CollectionUtils;
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
/*     */ class DisposableBeanAdapter
/*     */   implements DisposableBean, Runnable, Serializable
/*     */ {
/*     */   private static final String DESTROY_METHOD_NAME = "destroy";
/*     */   private static final String CLOSE_METHOD_NAME = "close";
/*     */   private static final String SHUTDOWN_METHOD_NAME = "shutdown";
/*  71 */   private static final Log logger = LogFactory.getLog(DisposableBeanAdapter.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private final Object bean;
/*     */ 
/*     */   
/*     */   private final String beanName;
/*     */ 
/*     */   
/*     */   private final boolean nonPublicAccessAllowed;
/*     */ 
/*     */   
/*     */   private final boolean invokeDisposableBean;
/*     */ 
/*     */   
/*     */   private boolean invokeAutoCloseable;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String destroyMethodName;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private transient Method destroyMethod;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final List<DestructionAwareBeanPostProcessor> beanPostProcessors;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final AccessControlContext acc;
/*     */ 
/*     */ 
/*     */   
/*     */   public DisposableBeanAdapter(Object bean, String beanName, RootBeanDefinition beanDefinition, List<DestructionAwareBeanPostProcessor> postProcessors, @Nullable AccessControlContext acc) {
/* 108 */     Assert.notNull(bean, "Disposable bean must not be null");
/* 109 */     this.bean = bean;
/* 110 */     this.beanName = beanName;
/* 111 */     this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
/* 112 */     this
/* 113 */       .invokeDisposableBean = (bean instanceof DisposableBean && !beanDefinition.hasAnyExternallyManagedDestroyMethod("destroy"));
/*     */     
/* 115 */     String destroyMethodName = inferDestroyMethodIfNecessary(bean, beanDefinition);
/* 116 */     if (destroyMethodName != null && (!this.invokeDisposableBean || 
/* 117 */       !"destroy".equals(destroyMethodName)) && 
/* 118 */       !beanDefinition.hasAnyExternallyManagedDestroyMethod(destroyMethodName)) {
/*     */       
/* 120 */       this.invokeAutoCloseable = (bean instanceof AutoCloseable && "close".equals(destroyMethodName));
/* 121 */       if (!this.invokeAutoCloseable) {
/* 122 */         this.destroyMethodName = destroyMethodName;
/* 123 */         Method destroyMethod = determineDestroyMethod(destroyMethodName);
/* 124 */         if (destroyMethod == null) {
/* 125 */           if (beanDefinition.isEnforceDestroyMethod()) {
/* 126 */             throw new BeanDefinitionValidationException("Could not find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
/*     */           }
/*     */         }
/*     */         else {
/*     */           
/* 131 */           if (destroyMethod.getParameterCount() > 0) {
/* 132 */             Class<?>[] paramTypes = destroyMethod.getParameterTypes();
/* 133 */             if (paramTypes.length > 1) {
/* 134 */               throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has more than one parameter - not supported as destroy method");
/*     */             }
/*     */             
/* 137 */             if (paramTypes.length == 1 && boolean.class != paramTypes[0]) {
/* 138 */               throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has a non-boolean parameter - not supported as destroy method");
/*     */             }
/*     */           } 
/*     */           
/* 142 */           destroyMethod = ClassUtils.getInterfaceMethodIfPossible(destroyMethod, bean.getClass());
/*     */         } 
/* 144 */         this.destroyMethod = destroyMethod;
/*     */       } 
/*     */     } 
/*     */     
/* 148 */     this.beanPostProcessors = filterPostProcessors(postProcessors, bean);
/* 149 */     this.acc = acc;
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
/*     */   public DisposableBeanAdapter(Object bean, List<DestructionAwareBeanPostProcessor> postProcessors, AccessControlContext acc) {
/* 161 */     Assert.notNull(bean, "Disposable bean must not be null");
/* 162 */     this.bean = bean;
/* 163 */     this.beanName = bean.getClass().getName();
/* 164 */     this.nonPublicAccessAllowed = true;
/* 165 */     this.invokeDisposableBean = this.bean instanceof DisposableBean;
/* 166 */     this.beanPostProcessors = filterPostProcessors(postProcessors, bean);
/* 167 */     this.acc = acc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DisposableBeanAdapter(Object bean, String beanName, boolean nonPublicAccessAllowed, boolean invokeDisposableBean, boolean invokeAutoCloseable, @Nullable String destroyMethodName, @Nullable List<DestructionAwareBeanPostProcessor> postProcessors) {
/* 177 */     this.bean = bean;
/* 178 */     this.beanName = beanName;
/* 179 */     this.nonPublicAccessAllowed = nonPublicAccessAllowed;
/* 180 */     this.invokeDisposableBean = invokeDisposableBean;
/* 181 */     this.invokeAutoCloseable = invokeAutoCloseable;
/* 182 */     this.destroyMethodName = destroyMethodName;
/* 183 */     this.beanPostProcessors = postProcessors;
/* 184 */     this.acc = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 190 */     destroy();
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 195 */     if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
/* 196 */       for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
/* 197 */         processor.postProcessBeforeDestruction(this.bean, this.beanName);
/*     */       }
/*     */     }
/*     */     
/* 201 */     if (this.invokeDisposableBean) {
/* 202 */       if (logger.isTraceEnabled()) {
/* 203 */         logger.trace("Invoking destroy() on bean with name '" + this.beanName + "'");
/*     */       }
/*     */       try {
/* 206 */         if (System.getSecurityManager() != null) {
/* 207 */           AccessController.doPrivileged(() -> { ((DisposableBean)this.bean).destroy(); return null; }this.acc);
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 213 */           ((DisposableBean)this.bean).destroy();
/*     */         }
/*     */       
/* 216 */       } catch (Throwable ex) {
/* 217 */         String msg = "Invocation of destroy method failed on bean with name '" + this.beanName + "'";
/* 218 */         if (logger.isDebugEnabled()) {
/* 219 */           logger.warn(msg, ex);
/*     */         } else {
/*     */           
/* 222 */           logger.warn(msg + ": " + ex);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 227 */     if (this.invokeAutoCloseable) {
/* 228 */       if (logger.isTraceEnabled()) {
/* 229 */         logger.trace("Invoking close() on bean with name '" + this.beanName + "'");
/*     */       }
/*     */       try {
/* 232 */         if (System.getSecurityManager() != null) {
/* 233 */           AccessController.doPrivileged(() -> { ((AutoCloseable)this.bean).close(); return null; }this.acc);
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 239 */           ((AutoCloseable)this.bean).close();
/*     */         }
/*     */       
/* 242 */       } catch (Throwable ex) {
/* 243 */         String msg = "Invocation of close method failed on bean with name '" + this.beanName + "'";
/* 244 */         if (logger.isDebugEnabled()) {
/* 245 */           logger.warn(msg, ex);
/*     */         } else {
/*     */           
/* 248 */           logger.warn(msg + ": " + ex);
/*     */         }
/*     */       
/*     */       } 
/* 252 */     } else if (this.destroyMethod != null) {
/* 253 */       invokeCustomDestroyMethod(this.destroyMethod);
/*     */     }
/* 255 */     else if (this.destroyMethodName != null) {
/* 256 */       Method destroyMethod = determineDestroyMethod(this.destroyMethodName);
/* 257 */       if (destroyMethod != null) {
/* 258 */         invokeCustomDestroyMethod(ClassUtils.getInterfaceMethodIfPossible(destroyMethod, this.bean.getClass()));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Method determineDestroyMethod(String name) {
/*     */     try {
/* 267 */       if (System.getSecurityManager() != null) {
/* 268 */         return AccessController.<Method>doPrivileged(() -> findDestroyMethod(name));
/*     */       }
/*     */       
/* 271 */       return findDestroyMethod(name);
/*     */     
/*     */     }
/* 274 */     catch (IllegalArgumentException ex) {
/* 275 */       throw new BeanDefinitionValidationException("Could not find unique destroy method on bean with name '" + this.beanName + ": " + ex
/* 276 */           .getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private Method findDestroyMethod(String name) {
/* 282 */     return this.nonPublicAccessAllowed ? 
/* 283 */       BeanUtils.findMethodWithMinimalParameters(this.bean.getClass(), name) : 
/* 284 */       BeanUtils.findMethodWithMinimalParameters(this.bean.getClass().getMethods(), name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeCustomDestroyMethod(Method destroyMethod) {
/* 294 */     int paramCount = destroyMethod.getParameterCount();
/* 295 */     Object[] args = new Object[paramCount];
/* 296 */     if (paramCount == 1) {
/* 297 */       args[0] = Boolean.TRUE;
/*     */     }
/* 299 */     if (logger.isTraceEnabled()) {
/* 300 */       logger.trace("Invoking custom destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'");
/*     */     }
/*     */     
/*     */     try {
/* 304 */       if (System.getSecurityManager() != null) {
/* 305 */         AccessController.doPrivileged(() -> {
/*     */               ReflectionUtils.makeAccessible(destroyMethod);
/*     */               return null;
/*     */             });
/*     */         try {
/* 310 */           AccessController.doPrivileged(() -> destroyMethod.invoke(this.bean, args), this.acc);
/*     */         
/*     */         }
/* 313 */         catch (PrivilegedActionException pax) {
/* 314 */           throw (InvocationTargetException)pax.getException();
/*     */         } 
/*     */       } else {
/*     */         
/* 318 */         ReflectionUtils.makeAccessible(destroyMethod);
/* 319 */         destroyMethod.invoke(this.bean, args);
/*     */       }
/*     */     
/* 322 */     } catch (InvocationTargetException ex) {
/* 323 */       String msg = "Custom destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "' threw an exception";
/*     */       
/* 325 */       if (logger.isDebugEnabled()) {
/* 326 */         logger.warn(msg, ex.getTargetException());
/*     */       } else {
/*     */         
/* 329 */         logger.warn(msg + ": " + ex.getTargetException());
/*     */       }
/*     */     
/* 332 */     } catch (Throwable ex) {
/* 333 */       logger.warn("Failed to invoke custom destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object writeReplace() {
/* 344 */     List<DestructionAwareBeanPostProcessor> serializablePostProcessors = null;
/* 345 */     if (this.beanPostProcessors != null) {
/* 346 */       serializablePostProcessors = new ArrayList<>();
/* 347 */       for (DestructionAwareBeanPostProcessor postProcessor : this.beanPostProcessors) {
/* 348 */         if (postProcessor instanceof Serializable) {
/* 349 */           serializablePostProcessors.add(postProcessor);
/*     */         }
/*     */       } 
/*     */     } 
/* 353 */     return new DisposableBeanAdapter(this.bean, this.beanName, this.nonPublicAccessAllowed, this.invokeDisposableBean, this.invokeAutoCloseable, this.destroyMethodName, serializablePostProcessors);
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
/*     */   public static boolean hasDestroyMethod(Object bean, RootBeanDefinition beanDefinition) {
/* 365 */     return (bean instanceof DisposableBean || inferDestroyMethodIfNecessary(bean, beanDefinition) != null);
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
/*     */   @Nullable
/*     */   private static String inferDestroyMethodIfNecessary(Object bean, RootBeanDefinition beanDefinition) {
/* 384 */     String destroyMethodName = beanDefinition.resolvedDestroyMethodName;
/* 385 */     if (destroyMethodName == null) {
/* 386 */       destroyMethodName = beanDefinition.getDestroyMethodName();
/* 387 */       boolean autoCloseable = bean instanceof AutoCloseable;
/* 388 */       if ("(inferred)".equals(destroyMethodName) || (destroyMethodName == null && autoCloseable)) {
/*     */ 
/*     */ 
/*     */         
/* 392 */         destroyMethodName = null;
/* 393 */         if (!(bean instanceof DisposableBean)) {
/* 394 */           if (autoCloseable) {
/* 395 */             destroyMethodName = "close";
/*     */           } else {
/*     */             
/*     */             try {
/* 399 */               destroyMethodName = bean.getClass().getMethod("close", new Class[0]).getName();
/*     */             }
/* 401 */             catch (NoSuchMethodException ex) {
/*     */               try {
/* 403 */                 destroyMethodName = bean.getClass().getMethod("shutdown", new Class[0]).getName();
/*     */               }
/* 405 */               catch (NoSuchMethodException noSuchMethodException) {}
/*     */             } 
/*     */           } 
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 412 */       beanDefinition.resolvedDestroyMethodName = (destroyMethodName != null) ? destroyMethodName : "";
/*     */     } 
/* 414 */     return StringUtils.hasLength(destroyMethodName) ? destroyMethodName : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasApplicableProcessors(Object bean, List<DestructionAwareBeanPostProcessor> postProcessors) {
/* 423 */     if (!CollectionUtils.isEmpty(postProcessors)) {
/* 424 */       for (DestructionAwareBeanPostProcessor processor : postProcessors) {
/* 425 */         if (processor.requiresDestruction(bean)) {
/* 426 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 430 */     return false;
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
/*     */   private static List<DestructionAwareBeanPostProcessor> filterPostProcessors(List<DestructionAwareBeanPostProcessor> processors, Object bean) {
/* 442 */     List<DestructionAwareBeanPostProcessor> filteredPostProcessors = null;
/* 443 */     if (!CollectionUtils.isEmpty(processors)) {
/* 444 */       filteredPostProcessors = new ArrayList<>(processors.size());
/* 445 */       for (DestructionAwareBeanPostProcessor processor : processors) {
/* 446 */         if (processor.requiresDestruction(bean)) {
/* 447 */           filteredPostProcessors.add(processor);
/*     */         }
/*     */       } 
/*     */     } 
/* 451 */     return filteredPostProcessors;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/DisposableBeanAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */