/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeanInstantiationException;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.beans.factory.config.ConfigurableBeanFactory;
/*     */ import org.springframework.cglib.core.ClassLoaderAwareGeneratorStrategy;
/*     */ import org.springframework.cglib.core.GeneratorStrategy;
/*     */ import org.springframework.cglib.core.NamingPolicy;
/*     */ import org.springframework.cglib.core.SpringNamingPolicy;
/*     */ import org.springframework.cglib.proxy.Callback;
/*     */ import org.springframework.cglib.proxy.CallbackFilter;
/*     */ import org.springframework.cglib.proxy.Enhancer;
/*     */ import org.springframework.cglib.proxy.Factory;
/*     */ import org.springframework.cglib.proxy.MethodInterceptor;
/*     */ import org.springframework.cglib.proxy.MethodProxy;
/*     */ import org.springframework.cglib.proxy.NoOp;
/*     */ import org.springframework.core.ResolvableType;
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
/*     */ public class CglibSubclassingInstantiationStrategy
/*     */   extends SimpleInstantiationStrategy
/*     */ {
/*     */   private static final int PASSTHROUGH = 0;
/*     */   private static final int LOOKUP_OVERRIDE = 1;
/*     */   private static final int METHOD_REPLACER = 2;
/*     */   
/*     */   protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
/*  77 */     return instantiateWithMethodInjection(bd, beanName, owner, null, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, @Nullable Constructor<?> ctor, Object... args) {
/*  85 */     return (new CglibSubclassCreator(bd, owner)).instantiate(ctor, args);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class CglibSubclassCreator
/*     */   {
/*  95 */     private static final Class<?>[] CALLBACK_TYPES = new Class[] { NoOp.class, CglibSubclassingInstantiationStrategy.LookupOverrideMethodInterceptor.class, CglibSubclassingInstantiationStrategy.ReplaceOverrideMethodInterceptor.class };
/*     */     
/*     */     private final RootBeanDefinition beanDefinition;
/*     */     
/*     */     private final BeanFactory owner;
/*     */ 
/*     */     
/*     */     CglibSubclassCreator(RootBeanDefinition beanDefinition, BeanFactory owner) {
/* 103 */       this.beanDefinition = beanDefinition;
/* 104 */       this.owner = owner;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object instantiate(@Nullable Constructor<?> ctor, Object... args) {
/*     */       Object instance;
/* 117 */       Class<?> subclass = createEnhancedSubclass(this.beanDefinition);
/*     */       
/* 119 */       if (ctor == null) {
/* 120 */         instance = BeanUtils.instantiateClass(subclass);
/*     */       } else {
/*     */         
/*     */         try {
/* 124 */           Constructor<?> enhancedSubclassConstructor = subclass.getConstructor(ctor.getParameterTypes());
/* 125 */           instance = enhancedSubclassConstructor.newInstance(args);
/*     */         }
/* 127 */         catch (Exception ex) {
/* 128 */           throw new BeanInstantiationException(this.beanDefinition.getBeanClass(), "Failed to invoke constructor for CGLIB enhanced subclass [" + subclass
/* 129 */               .getName() + "]", ex);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 134 */       Factory factory = (Factory)instance;
/* 135 */       factory.setCallbacks(new Callback[] { (Callback)NoOp.INSTANCE, (Callback)new CglibSubclassingInstantiationStrategy.LookupOverrideMethodInterceptor(this.beanDefinition, this.owner), (Callback)new CglibSubclassingInstantiationStrategy.ReplaceOverrideMethodInterceptor(this.beanDefinition, this.owner) });
/*     */ 
/*     */       
/* 138 */       return instance;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Class<?> createEnhancedSubclass(RootBeanDefinition beanDefinition) {
/* 146 */       Enhancer enhancer = new Enhancer();
/* 147 */       enhancer.setSuperclass(beanDefinition.getBeanClass());
/* 148 */       enhancer.setNamingPolicy((NamingPolicy)SpringNamingPolicy.INSTANCE);
/* 149 */       if (this.owner instanceof ConfigurableBeanFactory) {
/* 150 */         ClassLoader cl = ((ConfigurableBeanFactory)this.owner).getBeanClassLoader();
/* 151 */         enhancer.setStrategy((GeneratorStrategy)new ClassLoaderAwareGeneratorStrategy(cl));
/*     */       } 
/* 153 */       enhancer.setCallbackFilter(new CglibSubclassingInstantiationStrategy.MethodOverrideCallbackFilter(beanDefinition));
/* 154 */       enhancer.setCallbackTypes(CALLBACK_TYPES);
/* 155 */       return enhancer.createClass();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class CglibIdentitySupport
/*     */   {
/*     */     private final RootBeanDefinition beanDefinition;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CglibIdentitySupport(RootBeanDefinition beanDefinition) {
/* 170 */       this.beanDefinition = beanDefinition;
/*     */     }
/*     */     
/*     */     public RootBeanDefinition getBeanDefinition() {
/* 174 */       return this.beanDefinition;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 179 */       return (other != null && getClass() == other.getClass() && this.beanDefinition
/* 180 */         .equals(((CglibIdentitySupport)other).beanDefinition));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 185 */       return this.beanDefinition.hashCode();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class MethodOverrideCallbackFilter
/*     */     extends CglibIdentitySupport
/*     */     implements CallbackFilter
/*     */   {
/* 195 */     private static final Log logger = LogFactory.getLog(MethodOverrideCallbackFilter.class);
/*     */     
/*     */     public MethodOverrideCallbackFilter(RootBeanDefinition beanDefinition) {
/* 198 */       super(beanDefinition);
/*     */     }
/*     */ 
/*     */     
/*     */     public int accept(Method method) {
/* 203 */       MethodOverride methodOverride = getBeanDefinition().getMethodOverrides().getOverride(method);
/* 204 */       if (logger.isTraceEnabled()) {
/* 205 */         logger.trace("MethodOverride for " + method + ": " + methodOverride);
/*     */       }
/* 207 */       if (methodOverride == null) {
/* 208 */         return 0;
/*     */       }
/* 210 */       if (methodOverride instanceof LookupOverride) {
/* 211 */         return 1;
/*     */       }
/* 213 */       if (methodOverride instanceof ReplaceOverride) {
/* 214 */         return 2;
/*     */       }
/* 216 */       throw new UnsupportedOperationException("Unexpected MethodOverride subclass: " + methodOverride
/* 217 */           .getClass().getName());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class LookupOverrideMethodInterceptor
/*     */     extends CglibIdentitySupport
/*     */     implements MethodInterceptor
/*     */   {
/*     */     private final BeanFactory owner;
/*     */ 
/*     */     
/*     */     public LookupOverrideMethodInterceptor(RootBeanDefinition beanDefinition, BeanFactory owner) {
/* 231 */       super(beanDefinition);
/* 232 */       this.owner = owner;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
/* 238 */       LookupOverride lo = (LookupOverride)getBeanDefinition().getMethodOverrides().getOverride(method);
/* 239 */       Assert.state((lo != null), "LookupOverride not found");
/* 240 */       Object[] argsToUse = (args.length > 0) ? args : null;
/* 241 */       if (StringUtils.hasText(lo.getBeanName())) {
/*     */         
/* 243 */         Object bean = (argsToUse != null) ? this.owner.getBean(lo.getBeanName(), argsToUse) : this.owner.getBean(lo.getBeanName());
/*     */         
/* 245 */         return bean.equals(null) ? null : bean;
/*     */       } 
/*     */ 
/*     */       
/* 249 */       ResolvableType genericReturnType = ResolvableType.forMethodReturnType(method);
/* 250 */       return (argsToUse != null) ? this.owner.getBeanProvider(genericReturnType).getObject(argsToUse) : this.owner
/* 251 */         .getBeanProvider(genericReturnType).getObject();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ReplaceOverrideMethodInterceptor
/*     */     extends CglibIdentitySupport
/*     */     implements MethodInterceptor
/*     */   {
/*     */     private final BeanFactory owner;
/*     */ 
/*     */ 
/*     */     
/*     */     public ReplaceOverrideMethodInterceptor(RootBeanDefinition beanDefinition, BeanFactory owner) {
/* 266 */       super(beanDefinition);
/* 267 */       this.owner = owner;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object intercept(Object obj, Method method, Object[] args, MethodProxy mp) throws Throwable {
/* 272 */       ReplaceOverride ro = (ReplaceOverride)getBeanDefinition().getMethodOverrides().getOverride(method);
/* 273 */       Assert.state((ro != null), "ReplaceOverride not found");
/*     */       
/* 275 */       MethodReplacer mr = (MethodReplacer)this.owner.getBean(ro.getMethodReplacerBeanName(), MethodReplacer.class);
/* 276 */       return mr.reimplement(obj, method, args);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */