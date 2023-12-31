/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Stream;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.factory.BeanCreationException;
/*     */ import org.springframework.beans.factory.BeanFactoryUtils;
/*     */ import org.springframework.beans.factory.BeanIsNotAFactoryException;
/*     */ import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
/*     */ import org.springframework.beans.factory.FactoryBean;
/*     */ import org.springframework.beans.factory.ListableBeanFactory;
/*     */ import org.springframework.beans.factory.NoSuchBeanDefinitionException;
/*     */ import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
/*     */ import org.springframework.beans.factory.ObjectProvider;
/*     */ import org.springframework.beans.factory.SmartFactoryBean;
/*     */ import org.springframework.core.OrderComparator;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.annotation.AnnotatedElementUtils;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
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
/*     */ public class StaticListableBeanFactory
/*     */   implements ListableBeanFactory
/*     */ {
/*     */   private final Map<String, Object> beans;
/*     */   
/*     */   public StaticListableBeanFactory() {
/*  78 */     this.beans = new LinkedHashMap<>();
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
/*     */   public StaticListableBeanFactory(Map<String, Object> beans) {
/*  92 */     Assert.notNull(beans, "Beans Map must not be null");
/*  93 */     this.beans = beans;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addBean(String name, Object bean) {
/* 104 */     this.beans.put(name, bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getBean(String name) throws BeansException {
/* 114 */     String beanName = BeanFactoryUtils.transformedBeanName(name);
/* 115 */     Object bean = this.beans.get(beanName);
/*     */     
/* 117 */     if (bean == null) {
/* 118 */       throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + 
/* 119 */           StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 124 */     if (BeanFactoryUtils.isFactoryDereference(name) && !(bean instanceof FactoryBean)) {
/* 125 */       throw new BeanIsNotAFactoryException(beanName, bean.getClass());
/*     */     }
/*     */     
/* 128 */     if (bean instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
/*     */       try {
/* 130 */         Object exposedObject = ((FactoryBean)bean).getObject();
/* 131 */         if (exposedObject == null) {
/* 132 */           throw new BeanCreationException(beanName, "FactoryBean exposed null object");
/*     */         }
/* 134 */         return exposedObject;
/*     */       }
/* 136 */       catch (Exception ex) {
/* 137 */         throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
/*     */       } 
/*     */     }
/*     */     
/* 141 */     return bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getBean(String name, @Nullable Class<T> requiredType) throws BeansException {
/* 148 */     Object bean = getBean(name);
/* 149 */     if (requiredType != null && !requiredType.isInstance(bean)) {
/* 150 */       throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
/*     */     }
/* 152 */     return (T)bean;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getBean(String name, Object... args) throws BeansException {
/* 157 */     if (!ObjectUtils.isEmpty(args)) {
/* 158 */       throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
/*     */     }
/*     */     
/* 161 */     return getBean(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getBean(Class<T> requiredType) throws BeansException {
/* 166 */     String[] beanNames = getBeanNamesForType(requiredType);
/* 167 */     if (beanNames.length == 1) {
/* 168 */       return getBean(beanNames[0], requiredType);
/*     */     }
/* 170 */     if (beanNames.length > 1) {
/* 171 */       throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
/*     */     }
/*     */     
/* 174 */     throw new NoSuchBeanDefinitionException(requiredType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
/* 180 */     if (!ObjectUtils.isEmpty(args)) {
/* 181 */       throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
/*     */     }
/*     */     
/* 184 */     return getBean(requiredType);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) throws BeansException {
/* 189 */     return getBeanProvider(ResolvableType.forRawClass(requiredType), true);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
/* 194 */     return getBeanProvider(requiredType, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsBean(String name) {
/* 199 */     return this.beans.containsKey(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
/* 204 */     Object bean = getBean(name);
/*     */     
/* 206 */     if (bean instanceof FactoryBean) {
/* 207 */       return ((FactoryBean)bean).isSingleton();
/*     */     }
/* 209 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
/* 214 */     Object bean = getBean(name);
/*     */     
/* 216 */     return ((bean instanceof SmartFactoryBean && ((SmartFactoryBean)bean).isPrototype()) || (bean instanceof FactoryBean && 
/* 217 */       !((FactoryBean)bean).isSingleton()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
/* 222 */     Class<?> type = getType(name);
/* 223 */     return (type != null && typeToMatch.isAssignableFrom(type));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTypeMatch(String name, @Nullable Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
/* 228 */     Class<?> type = getType(name);
/* 229 */     return (typeToMatch == null || (type != null && typeToMatch.isAssignableFrom(type)));
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
/* 234 */     return getType(name, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
/* 239 */     String beanName = BeanFactoryUtils.transformedBeanName(name);
/*     */     
/* 241 */     Object bean = this.beans.get(beanName);
/* 242 */     if (bean == null) {
/* 243 */       throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + 
/* 244 */           StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
/*     */     }
/*     */     
/* 247 */     if (bean instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name))
/*     */     {
/* 249 */       return ((FactoryBean)bean).getObjectType();
/*     */     }
/* 251 */     return bean.getClass();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getAliases(String name) {
/* 256 */     return new String[0];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsBeanDefinition(String name) {
/* 266 */     return this.beans.containsKey(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBeanDefinitionCount() {
/* 271 */     return this.beans.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getBeanDefinitionNames() {
/* 276 */     return StringUtils.toStringArray(this.beans.keySet());
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
/* 281 */     return getBeanProvider(ResolvableType.forRawClass(requiredType), allowEagerInit);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType, boolean allowEagerInit) {
/* 287 */     return new ObjectProvider<T>()
/*     */       {
/*     */         public T getObject() throws BeansException {
/* 290 */           String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
/* 291 */           if (beanNames.length == 1) {
/* 292 */             return (T)StaticListableBeanFactory.this.getBean(beanNames[0], new Object[] { this.val$requiredType });
/*     */           }
/* 294 */           if (beanNames.length > 1) {
/* 295 */             throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
/*     */           }
/*     */           
/* 298 */           throw new NoSuchBeanDefinitionException(requiredType);
/*     */         }
/*     */ 
/*     */         
/*     */         public T getObject(Object... args) throws BeansException {
/* 303 */           String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
/* 304 */           if (beanNames.length == 1) {
/* 305 */             return (T)StaticListableBeanFactory.this.getBean(beanNames[0], args);
/*     */           }
/* 307 */           if (beanNames.length > 1) {
/* 308 */             throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
/*     */           }
/*     */           
/* 311 */           throw new NoSuchBeanDefinitionException(requiredType);
/*     */         }
/*     */ 
/*     */         
/*     */         @Nullable
/*     */         public T getIfAvailable() throws BeansException {
/* 317 */           String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
/* 318 */           if (beanNames.length == 1) {
/* 319 */             return (T)StaticListableBeanFactory.this.getBean(beanNames[0]);
/*     */           }
/* 321 */           if (beanNames.length > 1) {
/* 322 */             throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
/*     */           }
/*     */           
/* 325 */           return null;
/*     */         }
/*     */ 
/*     */         
/*     */         @Nullable
/*     */         public T getIfUnique() throws BeansException {
/* 331 */           String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
/* 332 */           if (beanNames.length == 1) {
/* 333 */             return (T)StaticListableBeanFactory.this.getBean(beanNames[0]);
/*     */           }
/*     */           
/* 336 */           return null;
/*     */         }
/*     */ 
/*     */         
/*     */         public Stream<T> stream() {
/* 341 */           return Arrays.<String>stream(StaticListableBeanFactory.this.getBeanNamesForType(requiredType)).map(name -> StaticListableBeanFactory.this.getBean(name));
/*     */         }
/*     */         
/*     */         public Stream<T> orderedStream() {
/* 345 */           return stream().sorted((Comparator<? super T>)OrderComparator.INSTANCE);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getBeanNamesForType(@Nullable ResolvableType type) {
/* 352 */     return getBeanNamesForType(type, true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getBeanNamesForType(@Nullable ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
/* 359 */     Class<?> resolved = (type != null) ? type.resolve() : null;
/* 360 */     boolean isFactoryType = (resolved != null && FactoryBean.class.isAssignableFrom(resolved));
/* 361 */     List<String> matches = new ArrayList<>();
/*     */     
/* 363 */     for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
/* 364 */       String beanName = entry.getKey();
/* 365 */       Object beanInstance = entry.getValue();
/* 366 */       if (beanInstance instanceof FactoryBean && !isFactoryType) {
/* 367 */         FactoryBean<?> factoryBean = (FactoryBean)beanInstance;
/* 368 */         Class<?> objectType = factoryBean.getObjectType();
/* 369 */         if ((includeNonSingletons || factoryBean.isSingleton()) && objectType != null && (type == null || type
/* 370 */           .isAssignableFrom(objectType))) {
/* 371 */           matches.add(beanName);
/*     */         }
/*     */         continue;
/*     */       } 
/* 375 */       if (type == null || type.isInstance(beanInstance)) {
/* 376 */         matches.add(beanName);
/*     */       }
/*     */     } 
/*     */     
/* 380 */     return StringUtils.toStringArray(matches);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getBeanNamesForType(@Nullable Class<?> type) {
/* 385 */     return getBeanNamesForType(ResolvableType.forClass(type));
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
/* 390 */     return getBeanNamesForType(ResolvableType.forClass(type), includeNonSingletons, allowEagerInit);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
/* 395 */     return getBeansOfType(type, true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
/* 403 */     boolean isFactoryType = (type != null && FactoryBean.class.isAssignableFrom(type));
/* 404 */     Map<String, T> matches = new LinkedHashMap<>();
/*     */     
/* 406 */     for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
/* 407 */       String beanName = entry.getKey();
/* 408 */       Object beanInstance = entry.getValue();
/*     */       
/* 410 */       if (beanInstance instanceof FactoryBean && !isFactoryType) {
/*     */         
/* 412 */         FactoryBean<?> factory = (FactoryBean)beanInstance;
/* 413 */         Class<?> objectType = factory.getObjectType();
/* 414 */         if ((includeNonSingletons || factory.isSingleton()) && objectType != null && (type == null || type
/* 415 */           .isAssignableFrom(objectType))) {
/* 416 */           matches.put(beanName, getBean(beanName, type));
/*     */         }
/*     */         continue;
/*     */       } 
/* 420 */       if (type == null || type.isInstance(beanInstance)) {
/*     */ 
/*     */         
/* 423 */         if (isFactoryType) {
/* 424 */           beanName = "&" + beanName;
/*     */         }
/* 426 */         matches.put(beanName, (T)beanInstance);
/*     */       } 
/*     */     } 
/*     */     
/* 430 */     return matches;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
/* 435 */     List<String> results = new ArrayList<>();
/* 436 */     for (String beanName : this.beans.keySet()) {
/* 437 */       if (findAnnotationOnBean(beanName, annotationType) != null) {
/* 438 */         results.add(beanName);
/*     */       }
/*     */     } 
/* 441 */     return StringUtils.toStringArray(results);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
/* 448 */     Map<String, Object> results = new LinkedHashMap<>();
/* 449 */     for (String beanName : this.beans.keySet()) {
/* 450 */       if (findAnnotationOnBean(beanName, annotationType) != null) {
/* 451 */         results.put(beanName, getBean(beanName));
/*     */       }
/*     */     } 
/* 454 */     return results;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
/* 462 */     return findAnnotationOnBean(beanName, annotationType, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
/* 471 */     Class<?> beanType = getType(beanName, allowFactoryBeanInit);
/* 472 */     return (beanType != null) ? (A)AnnotatedElementUtils.findMergedAnnotation(beanType, annotationType) : null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/StaticListableBeanFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */