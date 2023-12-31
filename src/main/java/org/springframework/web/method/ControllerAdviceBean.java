/*     */ package org.springframework.web.method;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.springframework.aop.scope.ScopedProxyUtils;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.beans.factory.BeanFactoryUtils;
/*     */ import org.springframework.beans.factory.ListableBeanFactory;
/*     */ import org.springframework.beans.factory.NoSuchBeanDefinitionException;
/*     */ import org.springframework.beans.factory.config.BeanDefinition;
/*     */ import org.springframework.beans.factory.config.ConfigurableBeanFactory;
/*     */ import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
/*     */ import org.springframework.beans.factory.support.RootBeanDefinition;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.context.ConfigurableApplicationContext;
/*     */ import org.springframework.core.OrderComparator;
/*     */ import org.springframework.core.Ordered;
/*     */ import org.springframework.core.annotation.AnnotatedElementUtils;
/*     */ import org.springframework.core.annotation.OrderUtils;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.web.bind.annotation.ControllerAdvice;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ControllerAdviceBean
/*     */   implements Ordered
/*     */ {
/*     */   private final Object beanOrName;
/*     */   private final boolean isSingleton;
/*     */   @Nullable
/*     */   private Object resolvedBean;
/*     */   @Nullable
/*     */   private final Class<?> beanType;
/*     */   private final HandlerTypePredicate beanTypePredicate;
/*     */   @Nullable
/*     */   private final BeanFactory beanFactory;
/*     */   @Nullable
/*     */   private Integer order;
/*     */   
/*     */   public ControllerAdviceBean(Object bean) {
/*  91 */     Assert.notNull(bean, "Bean must not be null");
/*  92 */     this.beanOrName = bean;
/*  93 */     this.isSingleton = true;
/*  94 */     this.resolvedBean = bean;
/*  95 */     this.beanType = ClassUtils.getUserClass(bean.getClass());
/*  96 */     this.beanTypePredicate = createBeanTypePredicate(this.beanType);
/*  97 */     this.beanFactory = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ControllerAdviceBean(String beanName, BeanFactory beanFactory) {
/* 108 */     this(beanName, beanFactory, null);
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
/*     */   public ControllerAdviceBean(String beanName, BeanFactory beanFactory, @Nullable ControllerAdvice controllerAdvice) {
/* 123 */     Assert.hasText(beanName, "Bean name must contain text");
/* 124 */     Assert.notNull(beanFactory, "BeanFactory must not be null");
/* 125 */     Assert.isTrue(beanFactory.containsBean(beanName), () -> "BeanFactory [" + beanFactory + "] does not contain specified controller advice bean '" + beanName + "'");
/*     */ 
/*     */     
/* 128 */     this.beanOrName = beanName;
/* 129 */     this.isSingleton = beanFactory.isSingleton(beanName);
/* 130 */     this.beanType = getBeanType(beanName, beanFactory);
/* 131 */     this
/* 132 */       .beanTypePredicate = (controllerAdvice != null) ? createBeanTypePredicate(controllerAdvice) : createBeanTypePredicate(this.beanType);
/* 133 */     this.beanFactory = beanFactory;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getOrder() {
/* 160 */     if (this.order == null) {
/* 161 */       String beanName = null;
/* 162 */       Object resolvedBean = null;
/* 163 */       if (this.beanFactory != null && this.beanOrName instanceof String) {
/* 164 */         beanName = (String)this.beanOrName;
/* 165 */         String targetBeanName = ScopedProxyUtils.getTargetBeanName(beanName);
/* 166 */         boolean isScopedProxy = this.beanFactory.containsBean(targetBeanName);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 171 */         if (!isScopedProxy && !ScopedProxyUtils.isScopedTarget(beanName)) {
/* 172 */           resolvedBean = resolveBean();
/*     */         }
/*     */       } else {
/*     */         
/* 176 */         resolvedBean = resolveBean();
/*     */       } 
/*     */       
/* 179 */       if (resolvedBean instanceof Ordered) {
/* 180 */         this.order = Integer.valueOf(((Ordered)resolvedBean).getOrder());
/*     */       } else {
/*     */         
/* 183 */         if (beanName != null && this.beanFactory instanceof ConfigurableBeanFactory) {
/* 184 */           ConfigurableBeanFactory cbf = (ConfigurableBeanFactory)this.beanFactory;
/*     */           try {
/* 186 */             BeanDefinition bd = cbf.getMergedBeanDefinition(beanName);
/* 187 */             if (bd instanceof RootBeanDefinition) {
/* 188 */               Method factoryMethod = ((RootBeanDefinition)bd).getResolvedFactoryMethod();
/* 189 */               if (factoryMethod != null) {
/* 190 */                 this.order = OrderUtils.getOrder(factoryMethod);
/*     */               }
/*     */             }
/*     */           
/* 194 */           } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
/*     */         } 
/*     */ 
/*     */         
/* 198 */         if (this.order == null) {
/* 199 */           if (this.beanType != null) {
/* 200 */             this.order = Integer.valueOf(OrderUtils.getOrder(this.beanType, 2147483647));
/*     */           } else {
/*     */             
/* 203 */             this.order = Integer.valueOf(2147483647);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/* 208 */     return this.order.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Class<?> getBeanType() {
/* 218 */     return this.beanType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object resolveBean() {
/* 229 */     if (this.resolvedBean == null) {
/*     */ 
/*     */       
/* 232 */       Object resolvedBean = obtainBeanFactory().getBean((String)this.beanOrName);
/*     */       
/* 234 */       if (!this.isSingleton) {
/* 235 */         return resolvedBean;
/*     */       }
/* 237 */       this.resolvedBean = resolvedBean;
/*     */     } 
/* 239 */     return this.resolvedBean;
/*     */   }
/*     */   
/*     */   private BeanFactory obtainBeanFactory() {
/* 243 */     Assert.state((this.beanFactory != null), "No BeanFactory set");
/* 244 */     return this.beanFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isApplicableToBeanType(@Nullable Class<?> beanType) {
/* 255 */     return this.beanTypePredicate.test(beanType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 261 */     if (this == other) {
/* 262 */       return true;
/*     */     }
/* 264 */     if (!(other instanceof ControllerAdviceBean)) {
/* 265 */       return false;
/*     */     }
/* 267 */     ControllerAdviceBean otherAdvice = (ControllerAdviceBean)other;
/* 268 */     return (this.beanOrName.equals(otherAdvice.beanOrName) && this.beanFactory == otherAdvice.beanFactory);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 273 */     return this.beanOrName.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 278 */     return this.beanOrName.toString();
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
/*     */   public static List<ControllerAdviceBean> findAnnotatedBeans(ApplicationContext context) {
/*     */     ConfigurableListableBeanFactory configurableListableBeanFactory;
/* 293 */     ApplicationContext applicationContext = context;
/* 294 */     if (context instanceof ConfigurableApplicationContext)
/*     */     {
/* 296 */       configurableListableBeanFactory = ((ConfigurableApplicationContext)context).getBeanFactory();
/*     */     }
/* 298 */     List<ControllerAdviceBean> adviceBeans = new ArrayList<>();
/* 299 */     for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)configurableListableBeanFactory, Object.class)) {
/* 300 */       if (!ScopedProxyUtils.isScopedTarget(name)) {
/* 301 */         ControllerAdvice controllerAdvice = (ControllerAdvice)configurableListableBeanFactory.findAnnotationOnBean(name, ControllerAdvice.class);
/* 302 */         if (controllerAdvice != null)
/*     */         {
/*     */           
/* 305 */           adviceBeans.add(new ControllerAdviceBean(name, (BeanFactory)configurableListableBeanFactory, controllerAdvice));
/*     */         }
/*     */       } 
/*     */     } 
/* 309 */     OrderComparator.sort(adviceBeans);
/* 310 */     return adviceBeans;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static Class<?> getBeanType(String beanName, BeanFactory beanFactory) {
/* 315 */     Class<?> beanType = beanFactory.getType(beanName);
/* 316 */     return (beanType != null) ? ClassUtils.getUserClass(beanType) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static HandlerTypePredicate createBeanTypePredicate(@Nullable Class<?> beanType) {
/* 321 */     ControllerAdvice controllerAdvice = (beanType != null) ? (ControllerAdvice)AnnotatedElementUtils.findMergedAnnotation(beanType, ControllerAdvice.class) : null;
/* 322 */     return createBeanTypePredicate(controllerAdvice);
/*     */   }
/*     */   
/*     */   private static HandlerTypePredicate createBeanTypePredicate(@Nullable ControllerAdvice controllerAdvice) {
/* 326 */     if (controllerAdvice != null) {
/* 327 */       return HandlerTypePredicate.builder()
/* 328 */         .basePackage(controllerAdvice.basePackages())
/* 329 */         .basePackageClass(controllerAdvice.basePackageClasses())
/* 330 */         .assignableType(controllerAdvice.assignableTypes())
/* 331 */         .annotation((Class<? extends Annotation>[])controllerAdvice.annotations())
/* 332 */         .build();
/*     */     }
/* 334 */     return HandlerTypePredicate.forAnyHandlerType();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/ControllerAdviceBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */