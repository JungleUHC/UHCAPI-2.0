/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import org.springframework.beans.factory.support.BeanDefinitionRegistry;
/*     */ import org.springframework.beans.factory.support.BeanNameGenerator;
/*     */ import org.springframework.beans.factory.support.DefaultListableBeanFactory;
/*     */ import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
/*     */ import org.springframework.context.annotation.AnnotationConfigRegistry;
/*     */ import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
/*     */ import org.springframework.context.annotation.ScopeMetadataResolver;
/*     */ import org.springframework.core.env.Environment;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AnnotationConfigWebApplicationContext
/*     */   extends AbstractRefreshableWebApplicationContext
/*     */   implements AnnotationConfigRegistry
/*     */ {
/*     */   @Nullable
/*     */   private BeanNameGenerator beanNameGenerator;
/*     */   @Nullable
/*     */   private ScopeMetadataResolver scopeMetadataResolver;
/* 108 */   private final Set<Class<?>> componentClasses = new LinkedHashSet<>();
/*     */   
/* 110 */   private final Set<String> basePackages = new LinkedHashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
/* 121 */     this.beanNameGenerator = beanNameGenerator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected BeanNameGenerator getBeanNameGenerator() {
/* 130 */     return this.beanNameGenerator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
/* 141 */     this.scopeMetadataResolver = scopeMetadataResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected ScopeMetadataResolver getScopeMetadataResolver() {
/* 150 */     return this.scopeMetadataResolver;
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
/*     */   public void register(Class<?>... componentClasses) {
/* 167 */     Assert.notEmpty((Object[])componentClasses, "At least one component class must be specified");
/* 168 */     Collections.addAll(this.componentClasses, componentClasses);
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
/*     */   public void scan(String... basePackages) {
/* 183 */     Assert.notEmpty((Object[])basePackages, "At least one base package must be specified");
/* 184 */     Collections.addAll(this.basePackages, basePackages);
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
/*     */   
/*     */   protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
/* 212 */     AnnotatedBeanDefinitionReader reader = getAnnotatedBeanDefinitionReader(beanFactory);
/* 213 */     ClassPathBeanDefinitionScanner scanner = getClassPathBeanDefinitionScanner(beanFactory);
/*     */     
/* 215 */     BeanNameGenerator beanNameGenerator = getBeanNameGenerator();
/* 216 */     if (beanNameGenerator != null) {
/* 217 */       reader.setBeanNameGenerator(beanNameGenerator);
/* 218 */       scanner.setBeanNameGenerator(beanNameGenerator);
/* 219 */       beanFactory.registerSingleton("org.springframework.context.annotation.internalConfigurationBeanNameGenerator", beanNameGenerator);
/*     */     } 
/*     */     
/* 222 */     ScopeMetadataResolver scopeMetadataResolver = getScopeMetadataResolver();
/* 223 */     if (scopeMetadataResolver != null) {
/* 224 */       reader.setScopeMetadataResolver(scopeMetadataResolver);
/* 225 */       scanner.setScopeMetadataResolver(scopeMetadataResolver);
/*     */     } 
/*     */     
/* 228 */     if (!this.componentClasses.isEmpty()) {
/* 229 */       if (this.logger.isDebugEnabled()) {
/* 230 */         this.logger.debug("Registering component classes: [" + 
/* 231 */             StringUtils.collectionToCommaDelimitedString(this.componentClasses) + "]");
/*     */       }
/* 233 */       reader.register(ClassUtils.toClassArray(this.componentClasses));
/*     */     } 
/*     */     
/* 236 */     if (!this.basePackages.isEmpty()) {
/* 237 */       if (this.logger.isDebugEnabled()) {
/* 238 */         this.logger.debug("Scanning base packages: [" + 
/* 239 */             StringUtils.collectionToCommaDelimitedString(this.basePackages) + "]");
/*     */       }
/* 241 */       scanner.scan(StringUtils.toStringArray(this.basePackages));
/*     */     } 
/*     */     
/* 244 */     String[] configLocations = getConfigLocations();
/* 245 */     if (configLocations != null) {
/* 246 */       for (String configLocation : configLocations) {
/*     */         try {
/* 248 */           Class<?> clazz = ClassUtils.forName(configLocation, getClassLoader());
/* 249 */           if (this.logger.isTraceEnabled()) {
/* 250 */             this.logger.trace("Registering [" + configLocation + "]");
/*     */           }
/* 252 */           reader.register(new Class[] { clazz });
/*     */         }
/* 254 */         catch (ClassNotFoundException ex) {
/* 255 */           if (this.logger.isTraceEnabled()) {
/* 256 */             this.logger.trace("Could not load class for config location [" + configLocation + "] - trying package scan. " + ex);
/*     */           }
/*     */           
/* 259 */           int count = scanner.scan(new String[] { configLocation });
/* 260 */           if (count == 0 && this.logger.isDebugEnabled()) {
/* 261 */             this.logger.debug("No component classes found for specified class/package [" + configLocation + "]");
/*     */           }
/*     */         } 
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
/*     */   protected AnnotatedBeanDefinitionReader getAnnotatedBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
/* 280 */     return new AnnotatedBeanDefinitionReader((BeanDefinitionRegistry)beanFactory, (Environment)getEnvironment());
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
/*     */   protected ClassPathBeanDefinitionScanner getClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
/* 294 */     return new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry)beanFactory, true, (Environment)getEnvironment());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/AnnotationConfigWebApplicationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */