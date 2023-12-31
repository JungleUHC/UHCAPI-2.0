/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*     */ import org.springframework.beans.factory.config.BeanDefinition;
/*     */ import org.springframework.beans.factory.config.BeanDefinitionHolder;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
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
/*     */ public abstract class BeanDefinitionReaderUtils
/*     */ {
/*     */   public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";
/*     */   
/*     */   public static AbstractBeanDefinition createBeanDefinition(@Nullable String parentName, @Nullable String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException {
/*  60 */     GenericBeanDefinition bd = new GenericBeanDefinition();
/*  61 */     bd.setParentName(parentName);
/*  62 */     if (className != null) {
/*  63 */       if (classLoader != null) {
/*  64 */         bd.setBeanClass(ClassUtils.forName(className, classLoader));
/*     */       } else {
/*     */         
/*  67 */         bd.setBeanClassName(className);
/*     */       } 
/*     */     }
/*  70 */     return bd;
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
/*     */   public static String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
/*  87 */     return generateBeanName(beanDefinition, registry, false);
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
/*     */   public static String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean) throws BeanDefinitionStoreException {
/* 107 */     String generatedBeanName = definition.getBeanClassName();
/* 108 */     if (generatedBeanName == null) {
/* 109 */       if (definition.getParentName() != null) {
/* 110 */         generatedBeanName = definition.getParentName() + "$child";
/*     */       }
/* 112 */       else if (definition.getFactoryBeanName() != null) {
/* 113 */         generatedBeanName = definition.getFactoryBeanName() + "$created";
/*     */       } 
/*     */     }
/* 116 */     if (!StringUtils.hasText(generatedBeanName)) {
/* 117 */       throw new BeanDefinitionStoreException("Unnamed bean definition specifies neither 'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
/*     */     }
/*     */ 
/*     */     
/* 121 */     if (isInnerBean)
/*     */     {
/* 123 */       return generatedBeanName + "#" + ObjectUtils.getIdentityHexString(definition);
/*     */     }
/*     */ 
/*     */     
/* 127 */     return uniqueBeanName(generatedBeanName, registry);
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
/*     */   public static String uniqueBeanName(String beanName, BeanDefinitionRegistry registry) {
/* 140 */     String id = beanName;
/* 141 */     int counter = -1;
/*     */ 
/*     */     
/* 144 */     String prefix = beanName + "#";
/* 145 */     while (counter == -1 || registry.containsBeanDefinition(id)) {
/* 146 */       counter++;
/* 147 */       id = prefix + counter;
/*     */     } 
/* 149 */     return id;
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
/*     */   public static void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
/* 163 */     String beanName = definitionHolder.getBeanName();
/* 164 */     registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
/*     */ 
/*     */     
/* 167 */     String[] aliases = definitionHolder.getAliases();
/* 168 */     if (aliases != null) {
/* 169 */       for (String alias : aliases) {
/* 170 */         registry.registerAlias(beanName, alias);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String registerWithGeneratedName(AbstractBeanDefinition definition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
/* 188 */     String generatedName = generateBeanName(definition, registry, false);
/* 189 */     registry.registerBeanDefinition(generatedName, definition);
/* 190 */     return generatedName;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/BeanDefinitionReaderUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */