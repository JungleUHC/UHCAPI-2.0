/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.factory.BeanCreationException;
/*     */ import org.springframework.beans.factory.BeanCurrentlyInCreationException;
/*     */ import org.springframework.beans.factory.FactoryBean;
/*     */ import org.springframework.beans.factory.FactoryBeanNotInitializedException;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class FactoryBeanRegistrySupport
/*     */   extends DefaultSingletonBeanRegistry
/*     */ {
/*  47 */   private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Class<?> getTypeForFactoryBean(FactoryBean<?> factoryBean) {
/*     */     try {
/*  59 */       if (System.getSecurityManager() != null) {
/*  60 */         return AccessController.<Class<?>>doPrivileged(factoryBean::getObjectType, 
/*  61 */             getAccessControlContext());
/*     */       }
/*     */       
/*  64 */       return factoryBean.getObjectType();
/*     */     
/*     */     }
/*  67 */     catch (Throwable ex) {
/*     */       
/*  69 */       this.logger.info("FactoryBean threw exception from getObjectType, despite the contract saying that it should return null if the type of its object cannot be determined yet", ex);
/*     */       
/*  71 */       return null;
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
/*     */   @Nullable
/*     */   protected Object getCachedObjectForFactoryBean(String beanName) {
/*  84 */     return this.factoryBeanObjectCache.get(beanName);
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
/*     */   protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
/*  97 */     if (factory.isSingleton() && containsSingleton(beanName)) {
/*  98 */       synchronized (getSingletonMutex()) {
/*  99 */         Object object1 = this.factoryBeanObjectCache.get(beanName);
/* 100 */         if (object1 == null) {
/* 101 */           object1 = doGetObjectFromFactoryBean(factory, beanName);
/*     */ 
/*     */           
/* 104 */           Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
/* 105 */           if (alreadyThere != null) {
/* 106 */             object1 = alreadyThere;
/*     */           } else {
/*     */             
/* 109 */             if (shouldPostProcess) {
/* 110 */               if (isSingletonCurrentlyInCreation(beanName))
/*     */               {
/* 112 */                 return object1;
/*     */               }
/* 114 */               beforeSingletonCreation(beanName);
/*     */               try {
/* 116 */                 object1 = postProcessObjectFromFactoryBean(object1, beanName);
/*     */               }
/* 118 */               catch (Throwable ex) {
/* 119 */                 throw new BeanCreationException(beanName, "Post-processing of FactoryBean's singleton object failed", ex);
/*     */               }
/*     */               finally {
/*     */                 
/* 123 */                 afterSingletonCreation(beanName);
/*     */               } 
/*     */             } 
/* 126 */             if (containsSingleton(beanName)) {
/* 127 */               this.factoryBeanObjectCache.put(beanName, object1);
/*     */             }
/*     */           } 
/*     */         } 
/* 131 */         return object1;
/*     */       } 
/*     */     }
/*     */     
/* 135 */     Object object = doGetObjectFromFactoryBean(factory, beanName);
/* 136 */     if (shouldPostProcess) {
/*     */       try {
/* 138 */         object = postProcessObjectFromFactoryBean(object, beanName);
/*     */       }
/* 140 */       catch (Throwable ex) {
/* 141 */         throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
/*     */       } 
/*     */     }
/* 144 */     return object;
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
/*     */   private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws BeanCreationException {
/*     */     Object object;
/*     */     try {
/* 159 */       if (System.getSecurityManager() != null) {
/* 160 */         AccessControlContext acc = getAccessControlContext();
/*     */         try {
/* 162 */           object = AccessController.doPrivileged(factory::getObject, acc);
/*     */         }
/* 164 */         catch (PrivilegedActionException pae) {
/* 165 */           throw pae.getException();
/*     */         } 
/*     */       } else {
/*     */         
/* 169 */         object = factory.getObject();
/*     */       }
/*     */     
/* 172 */     } catch (FactoryBeanNotInitializedException ex) {
/* 173 */       throw new BeanCurrentlyInCreationException(beanName, ex.toString());
/*     */     }
/* 175 */     catch (Throwable ex) {
/* 176 */       throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 181 */     if (object == null) {
/* 182 */       if (isSingletonCurrentlyInCreation(beanName)) {
/* 183 */         throw new BeanCurrentlyInCreationException(beanName, "FactoryBean which is currently in creation returned null from getObject");
/*     */       }
/*     */       
/* 186 */       object = new NullBean();
/*     */     } 
/* 188 */     return object;
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
/*     */   protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
/* 202 */     return object;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
/* 213 */     if (!(beanInstance instanceof FactoryBean)) {
/* 214 */       throw new BeanCreationException(beanName, "Bean instance of type [" + beanInstance
/* 215 */           .getClass() + "] is not a FactoryBean");
/*     */     }
/* 217 */     return (FactoryBean)beanInstance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void removeSingleton(String beanName) {
/* 225 */     synchronized (getSingletonMutex()) {
/* 226 */       super.removeSingleton(beanName);
/* 227 */       this.factoryBeanObjectCache.remove(beanName);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void clearSingletonCache() {
/* 236 */     synchronized (getSingletonMutex()) {
/* 237 */       super.clearSingletonCache();
/* 238 */       this.factoryBeanObjectCache.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AccessControlContext getAccessControlContext() {
/* 249 */     return AccessController.getContext();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/FactoryBeanRegistrySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */