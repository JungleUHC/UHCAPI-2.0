/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.util.function.Supplier;
/*     */ import org.springframework.beans.factory.config.AutowiredPropertyMarker;
/*     */ import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
/*     */ import org.springframework.beans.factory.config.RuntimeBeanReference;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BeanDefinitionBuilder
/*     */ {
/*     */   private final AbstractBeanDefinition beanDefinition;
/*     */   private int constructorArgIndex;
/*     */   
/*     */   public static BeanDefinitionBuilder genericBeanDefinition() {
/*  45 */     return new BeanDefinitionBuilder(new GenericBeanDefinition());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanDefinitionBuilder genericBeanDefinition(String beanClassName) {
/*  53 */     BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new GenericBeanDefinition());
/*  54 */     builder.beanDefinition.setBeanClassName(beanClassName);
/*  55 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass) {
/*  63 */     BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new GenericBeanDefinition());
/*  64 */     builder.beanDefinition.setBeanClass(beanClass);
/*  65 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> BeanDefinitionBuilder genericBeanDefinition(Class<T> beanClass, Supplier<T> instanceSupplier) {
/*  75 */     BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new GenericBeanDefinition());
/*  76 */     builder.beanDefinition.setBeanClass(beanClass);
/*  77 */     builder.beanDefinition.setInstanceSupplier(instanceSupplier);
/*  78 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName) {
/*  86 */     return rootBeanDefinition(beanClassName, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName, @Nullable String factoryMethodName) {
/*  95 */     BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new RootBeanDefinition());
/*  96 */     builder.beanDefinition.setBeanClassName(beanClassName);
/*  97 */     builder.beanDefinition.setFactoryMethodName(factoryMethodName);
/*  98 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanDefinitionBuilder rootBeanDefinition(Class<?> beanClass) {
/* 106 */     return rootBeanDefinition(beanClass, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanDefinitionBuilder rootBeanDefinition(Class<?> beanClass, @Nullable String factoryMethodName) {
/* 115 */     BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new RootBeanDefinition());
/* 116 */     builder.beanDefinition.setBeanClass(beanClass);
/* 117 */     builder.beanDefinition.setFactoryMethodName(factoryMethodName);
/* 118 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> BeanDefinitionBuilder rootBeanDefinition(ResolvableType beanType, Supplier<T> instanceSupplier) {
/* 128 */     RootBeanDefinition beanDefinition = new RootBeanDefinition();
/* 129 */     beanDefinition.setTargetType(beanType);
/* 130 */     beanDefinition.setInstanceSupplier(instanceSupplier);
/* 131 */     return new BeanDefinitionBuilder(beanDefinition);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> BeanDefinitionBuilder rootBeanDefinition(Class<T> beanClass, Supplier<T> instanceSupplier) {
/* 142 */     return rootBeanDefinition(ResolvableType.forClass(beanClass), instanceSupplier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanDefinitionBuilder childBeanDefinition(String parentName) {
/* 150 */     return new BeanDefinitionBuilder(new ChildBeanDefinition(parentName));
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
/*     */   private BeanDefinitionBuilder(AbstractBeanDefinition beanDefinition) {
/* 169 */     this.beanDefinition = beanDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractBeanDefinition getRawBeanDefinition() {
/* 177 */     return this.beanDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractBeanDefinition getBeanDefinition() {
/* 184 */     this.beanDefinition.validate();
/* 185 */     return this.beanDefinition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setParentName(String parentName) {
/* 193 */     this.beanDefinition.setParentName(parentName);
/* 194 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setFactoryMethod(String factoryMethod) {
/* 202 */     this.beanDefinition.setFactoryMethodName(factoryMethod);
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setFactoryMethodOnBean(String factoryMethod, String factoryBean) {
/* 214 */     this.beanDefinition.setFactoryMethodName(factoryMethod);
/* 215 */     this.beanDefinition.setFactoryBeanName(factoryBean);
/* 216 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder addConstructorArgValue(@Nullable Object value) {
/* 224 */     this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(this.constructorArgIndex++, value);
/*     */     
/* 226 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder addConstructorArgReference(String beanName) {
/* 234 */     this.beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(this.constructorArgIndex++, new RuntimeBeanReference(beanName));
/*     */     
/* 236 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder addPropertyValue(String name, @Nullable Object value) {
/* 243 */     this.beanDefinition.getPropertyValues().add(name, value);
/* 244 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder addPropertyReference(String name, String beanName) {
/* 253 */     this.beanDefinition.getPropertyValues().add(name, new RuntimeBeanReference(beanName));
/* 254 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder addAutowiredProperty(String name) {
/* 264 */     this.beanDefinition.getPropertyValues().add(name, AutowiredPropertyMarker.INSTANCE);
/* 265 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setInitMethodName(@Nullable String methodName) {
/* 272 */     this.beanDefinition.setInitMethodName(methodName);
/* 273 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setDestroyMethodName(@Nullable String methodName) {
/* 280 */     this.beanDefinition.setDestroyMethodName(methodName);
/* 281 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setScope(@Nullable String scope) {
/* 291 */     this.beanDefinition.setScope(scope);
/* 292 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setAbstract(boolean flag) {
/* 299 */     this.beanDefinition.setAbstract(flag);
/* 300 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setLazyInit(boolean lazy) {
/* 307 */     this.beanDefinition.setLazyInit(lazy);
/* 308 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setAutowireMode(int autowireMode) {
/* 315 */     this.beanDefinition.setAutowireMode(autowireMode);
/* 316 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setDependencyCheck(int dependencyCheck) {
/* 323 */     this.beanDefinition.setDependencyCheck(dependencyCheck);
/* 324 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder addDependsOn(String beanName) {
/* 332 */     if (this.beanDefinition.getDependsOn() == null) {
/* 333 */       this.beanDefinition.setDependsOn(new String[] { beanName });
/*     */     } else {
/*     */       
/* 336 */       String[] added = (String[])ObjectUtils.addObjectToArray((Object[])this.beanDefinition.getDependsOn(), beanName);
/* 337 */       this.beanDefinition.setDependsOn(added);
/*     */     } 
/* 339 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setPrimary(boolean primary) {
/* 347 */     this.beanDefinition.setPrimary(primary);
/* 348 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setRole(int role) {
/* 355 */     this.beanDefinition.setRole(role);
/* 356 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder setSynthetic(boolean synthetic) {
/* 365 */     this.beanDefinition.setSynthetic(synthetic);
/* 366 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDefinitionBuilder applyCustomizers(BeanDefinitionCustomizer... customizers) {
/* 374 */     for (BeanDefinitionCustomizer customizer : customizers) {
/* 375 */       customizer.customize(this.beanDefinition);
/*     */     }
/* 377 */     return this;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/BeanDefinitionBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */