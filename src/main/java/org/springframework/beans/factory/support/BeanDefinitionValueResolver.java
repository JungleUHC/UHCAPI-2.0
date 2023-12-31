/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.TypeConverter;
/*     */ import org.springframework.beans.factory.BeanCreationException;
/*     */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.beans.factory.FactoryBean;
/*     */ import org.springframework.beans.factory.config.BeanDefinition;
/*     */ import org.springframework.beans.factory.config.BeanDefinitionHolder;
/*     */ import org.springframework.beans.factory.config.DependencyDescriptor;
/*     */ import org.springframework.beans.factory.config.NamedBeanHolder;
/*     */ import org.springframework.beans.factory.config.RuntimeBeanNameReference;
/*     */ import org.springframework.beans.factory.config.RuntimeBeanReference;
/*     */ import org.springframework.beans.factory.config.TypedStringValue;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.CollectionUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ class BeanDefinitionValueResolver
/*     */ {
/*     */   private final AbstractAutowireCapableBeanFactory beanFactory;
/*     */   private final String beanName;
/*     */   private final BeanDefinition beanDefinition;
/*     */   private final TypeConverter typeConverter;
/*     */   
/*     */   public BeanDefinitionValueResolver(AbstractAutowireCapableBeanFactory beanFactory, String beanName, BeanDefinition beanDefinition, TypeConverter typeConverter) {
/*  82 */     this.beanFactory = beanFactory;
/*  83 */     this.beanName = beanName;
/*  84 */     this.beanDefinition = beanDefinition;
/*  85 */     this.typeConverter = typeConverter;
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
/*     */   @Nullable
/*     */   public Object resolveValueIfNecessary(Object argName, @Nullable Object value) {
/* 111 */     if (value instanceof RuntimeBeanReference) {
/* 112 */       RuntimeBeanReference ref = (RuntimeBeanReference)value;
/* 113 */       return resolveReference(argName, ref);
/*     */     } 
/* 115 */     if (value instanceof RuntimeBeanNameReference) {
/* 116 */       String refName = ((RuntimeBeanNameReference)value).getBeanName();
/* 117 */       refName = String.valueOf(doEvaluate(refName));
/* 118 */       if (!this.beanFactory.containsBean(refName)) {
/* 119 */         throw new BeanDefinitionStoreException("Invalid bean name '" + refName + "' in bean reference for " + argName);
/*     */       }
/*     */       
/* 122 */       return refName;
/*     */     } 
/* 124 */     if (value instanceof BeanDefinitionHolder) {
/*     */       
/* 126 */       BeanDefinitionHolder bdHolder = (BeanDefinitionHolder)value;
/* 127 */       return resolveInnerBean(argName, bdHolder.getBeanName(), bdHolder.getBeanDefinition());
/*     */     } 
/* 129 */     if (value instanceof BeanDefinition) {
/*     */       
/* 131 */       BeanDefinition bd = (BeanDefinition)value;
/*     */       
/* 133 */       String innerBeanName = "(inner bean)#" + ObjectUtils.getIdentityHexString(bd);
/* 134 */       return resolveInnerBean(argName, innerBeanName, bd);
/*     */     } 
/* 136 */     if (value instanceof DependencyDescriptor) {
/* 137 */       Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
/* 138 */       Object result = this.beanFactory.resolveDependency((DependencyDescriptor)value, this.beanName, autowiredBeanNames, this.typeConverter);
/*     */       
/* 140 */       for (String autowiredBeanName : autowiredBeanNames) {
/* 141 */         if (this.beanFactory.containsBean(autowiredBeanName)) {
/* 142 */           this.beanFactory.registerDependentBean(autowiredBeanName, this.beanName);
/*     */         }
/*     */       } 
/* 145 */       return result;
/*     */     } 
/* 147 */     if (value instanceof ManagedArray) {
/*     */       
/* 149 */       ManagedArray array = (ManagedArray)value;
/* 150 */       Class<?> elementType = array.resolvedElementType;
/* 151 */       if (elementType == null) {
/* 152 */         String elementTypeName = array.getElementTypeName();
/* 153 */         if (StringUtils.hasText(elementTypeName)) {
/*     */           try {
/* 155 */             elementType = ClassUtils.forName(elementTypeName, this.beanFactory.getBeanClassLoader());
/* 156 */             array.resolvedElementType = elementType;
/*     */           }
/* 158 */           catch (Throwable ex) {
/*     */             
/* 160 */             throw new BeanCreationException(this.beanDefinition
/* 161 */                 .getResourceDescription(), this.beanName, "Error resolving array type for " + argName, ex);
/*     */           }
/*     */         
/*     */         } else {
/*     */           
/* 166 */           elementType = Object.class;
/*     */         } 
/*     */       } 
/* 169 */       return resolveManagedArray(argName, (List)value, elementType);
/*     */     } 
/* 171 */     if (value instanceof ManagedList)
/*     */     {
/* 173 */       return resolveManagedList(argName, (List)value);
/*     */     }
/* 175 */     if (value instanceof ManagedSet)
/*     */     {
/* 177 */       return resolveManagedSet(argName, (Set)value);
/*     */     }
/* 179 */     if (value instanceof ManagedMap)
/*     */     {
/* 181 */       return resolveManagedMap(argName, (Map<?, ?>)value);
/*     */     }
/* 183 */     if (value instanceof ManagedProperties) {
/* 184 */       Properties original = (Properties)value;
/* 185 */       Properties copy = new Properties();
/* 186 */       original.forEach((propKey, propValue) -> {
/*     */             if (propKey instanceof TypedStringValue) {
/*     */               propKey = evaluate((TypedStringValue)propKey);
/*     */             }
/*     */             
/*     */             if (propValue instanceof TypedStringValue) {
/*     */               propValue = evaluate((TypedStringValue)propValue);
/*     */             }
/*     */             
/*     */             if (propKey == null || propValue == null) {
/*     */               throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error converting Properties key/value pair for " + argName + ": resolved to null");
/*     */             }
/*     */             copy.put(propKey, propValue);
/*     */           });
/* 200 */       return copy;
/*     */     } 
/* 202 */     if (value instanceof TypedStringValue) {
/*     */       
/* 204 */       TypedStringValue typedStringValue = (TypedStringValue)value;
/* 205 */       Object valueObject = evaluate(typedStringValue);
/*     */       try {
/* 207 */         Class<?> resolvedTargetType = resolveTargetType(typedStringValue);
/* 208 */         if (resolvedTargetType != null) {
/* 209 */           return this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType);
/*     */         }
/*     */         
/* 212 */         return valueObject;
/*     */       
/*     */       }
/* 215 */       catch (Throwable ex) {
/*     */         
/* 217 */         throw new BeanCreationException(this.beanDefinition
/* 218 */             .getResourceDescription(), this.beanName, "Error converting typed String value for " + argName, ex);
/*     */       } 
/*     */     } 
/*     */     
/* 222 */     if (value instanceof NullBean) {
/* 223 */       return null;
/*     */     }
/*     */     
/* 226 */     return evaluate(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Object evaluate(TypedStringValue value) {
/* 237 */     Object result = doEvaluate(value.getValue());
/* 238 */     if (!ObjectUtils.nullSafeEquals(result, value.getValue())) {
/* 239 */       value.setDynamic();
/*     */     }
/* 241 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Object evaluate(@Nullable Object value) {
/* 251 */     if (value instanceof String) {
/* 252 */       return doEvaluate((String)value);
/*     */     }
/* 254 */     if (value instanceof String[]) {
/* 255 */       String[] values = (String[])value;
/* 256 */       boolean actuallyResolved = false;
/* 257 */       Object[] resolvedValues = new Object[values.length];
/* 258 */       for (int i = 0; i < values.length; i++) {
/* 259 */         String originalValue = values[i];
/* 260 */         Object resolvedValue = doEvaluate(originalValue);
/* 261 */         if (resolvedValue != originalValue) {
/* 262 */           actuallyResolved = true;
/*     */         }
/* 264 */         resolvedValues[i] = resolvedValue;
/*     */       } 
/* 266 */       return actuallyResolved ? resolvedValues : values;
/*     */     } 
/*     */     
/* 269 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Object doEvaluate(@Nullable String value) {
/* 280 */     return this.beanFactory.evaluateBeanDefinitionString(value, this.beanDefinition);
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
/*     */   protected Class<?> resolveTargetType(TypedStringValue value) throws ClassNotFoundException {
/* 292 */     if (value.hasTargetType()) {
/* 293 */       return value.getTargetType();
/*     */     }
/* 295 */     return value.resolveTargetType(this.beanFactory.getBeanClassLoader());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Object resolveReference(Object argName, RuntimeBeanReference ref) {
/*     */     try {
/*     */       Object bean;
/* 305 */       Class<?> beanType = ref.getBeanType();
/* 306 */       if (ref.isToParent()) {
/* 307 */         BeanFactory parent = this.beanFactory.getParentBeanFactory();
/* 308 */         if (parent == null) {
/* 309 */           throw new BeanCreationException(this.beanDefinition
/* 310 */               .getResourceDescription(), this.beanName, "Cannot resolve reference to bean " + ref + " in parent factory: no parent factory available");
/*     */         }
/*     */ 
/*     */         
/* 314 */         if (beanType != null) {
/* 315 */           bean = parent.getBean(beanType);
/*     */         } else {
/*     */           
/* 318 */           bean = parent.getBean(String.valueOf(doEvaluate(ref.getBeanName())));
/*     */         } 
/*     */       } else {
/*     */         String resolvedName;
/*     */         
/* 323 */         if (beanType != null) {
/* 324 */           NamedBeanHolder<?> namedBean = this.beanFactory.resolveNamedBean(beanType);
/* 325 */           bean = namedBean.getBeanInstance();
/* 326 */           resolvedName = namedBean.getBeanName();
/*     */         } else {
/*     */           
/* 329 */           resolvedName = String.valueOf(doEvaluate(ref.getBeanName()));
/* 330 */           bean = this.beanFactory.getBean(resolvedName);
/*     */         } 
/* 332 */         this.beanFactory.registerDependentBean(resolvedName, this.beanName);
/*     */       } 
/* 334 */       if (bean instanceof NullBean) {
/* 335 */         bean = null;
/*     */       }
/* 337 */       return bean;
/*     */     }
/* 339 */     catch (BeansException ex) {
/* 340 */       throw new BeanCreationException(this.beanDefinition
/* 341 */           .getResourceDescription(), this.beanName, "Cannot resolve reference to bean '" + ref
/* 342 */           .getBeanName() + "' while setting " + argName, ex);
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
/*     */   private Object resolveInnerBean(Object argName, String innerBeanName, BeanDefinition innerBd) {
/* 355 */     RootBeanDefinition mbd = null;
/*     */     try {
/* 357 */       mbd = this.beanFactory.getMergedBeanDefinition(innerBeanName, innerBd, this.beanDefinition);
/*     */ 
/*     */       
/* 360 */       String actualInnerBeanName = innerBeanName;
/* 361 */       if (mbd.isSingleton()) {
/* 362 */         actualInnerBeanName = adaptInnerBeanName(innerBeanName);
/*     */       }
/* 364 */       this.beanFactory.registerContainedBean(actualInnerBeanName, this.beanName);
/*     */       
/* 366 */       String[] dependsOn = mbd.getDependsOn();
/* 367 */       if (dependsOn != null) {
/* 368 */         for (String dependsOnBean : dependsOn) {
/* 369 */           this.beanFactory.registerDependentBean(dependsOnBean, actualInnerBeanName);
/* 370 */           this.beanFactory.getBean(dependsOnBean);
/*     */         } 
/*     */       }
/*     */       
/* 374 */       Object innerBean = this.beanFactory.createBean(actualInnerBeanName, mbd, (Object[])null);
/* 375 */       if (innerBean instanceof FactoryBean) {
/* 376 */         boolean synthetic = mbd.isSynthetic();
/* 377 */         innerBean = this.beanFactory.getObjectFromFactoryBean((FactoryBean)innerBean, actualInnerBeanName, !synthetic);
/*     */       } 
/*     */       
/* 380 */       if (innerBean instanceof NullBean) {
/* 381 */         innerBean = null;
/*     */       }
/* 383 */       return innerBean;
/*     */     }
/* 385 */     catch (BeansException ex) {
/* 386 */       throw new BeanCreationException(this.beanDefinition
/* 387 */           .getResourceDescription(), this.beanName, "Cannot create inner bean '" + innerBeanName + "' " + ((mbd != null && mbd
/*     */           
/* 389 */           .getBeanClassName() != null) ? ("of type [" + mbd.getBeanClassName() + "] ") : "") + "while setting " + argName, ex);
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
/*     */   private String adaptInnerBeanName(String innerBeanName) {
/* 401 */     String actualInnerBeanName = innerBeanName;
/* 402 */     int counter = 0;
/* 403 */     String prefix = innerBeanName + "#";
/* 404 */     while (this.beanFactory.isBeanNameInUse(actualInnerBeanName)) {
/* 405 */       counter++;
/* 406 */       actualInnerBeanName = prefix + counter;
/*     */     } 
/* 408 */     return actualInnerBeanName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object resolveManagedArray(Object argName, List<?> ml, Class<?> elementType) {
/* 415 */     Object resolved = Array.newInstance(elementType, ml.size());
/* 416 */     for (int i = 0; i < ml.size(); i++) {
/* 417 */       Array.set(resolved, i, resolveValueIfNecessary(new KeyedArgName(argName, Integer.valueOf(i)), ml.get(i)));
/*     */     }
/* 419 */     return resolved;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<?> resolveManagedList(Object argName, List<?> ml) {
/* 426 */     List<Object> resolved = new ArrayList(ml.size());
/* 427 */     for (int i = 0; i < ml.size(); i++) {
/* 428 */       resolved.add(resolveValueIfNecessary(new KeyedArgName(argName, Integer.valueOf(i)), ml.get(i)));
/*     */     }
/* 430 */     return resolved;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Set<?> resolveManagedSet(Object argName, Set<?> ms) {
/* 437 */     Set<Object> resolved = new LinkedHashSet(ms.size());
/* 438 */     int i = 0;
/* 439 */     for (Object m : ms) {
/* 440 */       resolved.add(resolveValueIfNecessary(new KeyedArgName(argName, Integer.valueOf(i)), m));
/* 441 */       i++;
/*     */     } 
/* 443 */     return resolved;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<?, ?> resolveManagedMap(Object argName, Map<?, ?> mm) {
/* 450 */     Map<Object, Object> resolved = CollectionUtils.newLinkedHashMap(mm.size());
/* 451 */     mm.forEach((key, value) -> {
/*     */           Object resolvedKey = resolveValueIfNecessary(argName, key);
/*     */           Object resolvedValue = resolveValueIfNecessary(new KeyedArgName(argName, key), value);
/*     */           resolved.put(resolvedKey, resolvedValue);
/*     */         });
/* 456 */     return resolved;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class KeyedArgName
/*     */   {
/*     */     private final Object argName;
/*     */ 
/*     */     
/*     */     private final Object key;
/*     */ 
/*     */     
/*     */     public KeyedArgName(Object argName, Object key) {
/* 470 */       this.argName = argName;
/* 471 */       this.key = key;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 476 */       return this.argName + " with key " + "[" + this.key + "]";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/BeanDefinitionValueResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */