/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*     */ import org.springframework.core.env.Environment;
/*     */ import org.springframework.core.env.EnvironmentCapable;
/*     */ import org.springframework.core.env.StandardEnvironment;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.ResourceLoader;
/*     */ import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
/*     */ import org.springframework.core.io.support.ResourcePatternResolver;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractBeanDefinitionReader
/*     */   implements BeanDefinitionReader, EnvironmentCapable
/*     */ {
/*  52 */   protected final Log logger = LogFactory.getLog(getClass());
/*     */   
/*     */   private final BeanDefinitionRegistry registry;
/*     */   
/*     */   @Nullable
/*     */   private ResourceLoader resourceLoader;
/*     */   
/*     */   @Nullable
/*     */   private ClassLoader beanClassLoader;
/*     */   
/*     */   private Environment environment;
/*     */   
/*  64 */   private BeanNameGenerator beanNameGenerator = DefaultBeanNameGenerator.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
/*  85 */     Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
/*  86 */     this.registry = registry;
/*     */ 
/*     */     
/*  89 */     if (this.registry instanceof ResourceLoader) {
/*  90 */       this.resourceLoader = (ResourceLoader)this.registry;
/*     */     } else {
/*     */       
/*  93 */       this.resourceLoader = (ResourceLoader)new PathMatchingResourcePatternResolver();
/*     */     } 
/*     */ 
/*     */     
/*  97 */     if (this.registry instanceof EnvironmentCapable) {
/*  98 */       this.environment = ((EnvironmentCapable)this.registry).getEnvironment();
/*     */     } else {
/*     */       
/* 101 */       this.environment = (Environment)new StandardEnvironment();
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
/*     */   @Deprecated
/*     */   public final BeanDefinitionRegistry getBeanFactory() {
/* 115 */     return this.registry;
/*     */   }
/*     */ 
/*     */   
/*     */   public final BeanDefinitionRegistry getRegistry() {
/* 120 */     return this.registry;
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
/*     */   public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
/* 135 */     this.resourceLoader = resourceLoader;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ResourceLoader getResourceLoader() {
/* 141 */     return this.resourceLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
/* 152 */     this.beanClassLoader = beanClassLoader;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ClassLoader getBeanClassLoader() {
/* 158 */     return this.beanClassLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnvironment(Environment environment) {
/* 167 */     Assert.notNull(environment, "Environment must not be null");
/* 168 */     this.environment = environment;
/*     */   }
/*     */ 
/*     */   
/*     */   public Environment getEnvironment() {
/* 173 */     return this.environment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
/* 182 */     this.beanNameGenerator = (beanNameGenerator != null) ? beanNameGenerator : DefaultBeanNameGenerator.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanNameGenerator getBeanNameGenerator() {
/* 187 */     return this.beanNameGenerator;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
/* 193 */     Assert.notNull(resources, "Resource array must not be null");
/* 194 */     int count = 0;
/* 195 */     for (Resource resource : resources) {
/* 196 */       count += loadBeanDefinitions(resource);
/*     */     }
/* 198 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
/* 203 */     return loadBeanDefinitions(location, null);
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
/*     */   public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
/* 222 */     ResourceLoader resourceLoader = getResourceLoader();
/* 223 */     if (resourceLoader == null) {
/* 224 */       throw new BeanDefinitionStoreException("Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
/*     */     }
/*     */ 
/*     */     
/* 228 */     if (resourceLoader instanceof ResourcePatternResolver) {
/*     */       
/*     */       try {
/* 231 */         Resource[] resources = ((ResourcePatternResolver)resourceLoader).getResources(location);
/* 232 */         int i = loadBeanDefinitions(resources);
/* 233 */         if (actualResources != null) {
/* 234 */           Collections.addAll(actualResources, resources);
/*     */         }
/* 236 */         if (this.logger.isTraceEnabled()) {
/* 237 */           this.logger.trace("Loaded " + i + " bean definitions from location pattern [" + location + "]");
/*     */         }
/* 239 */         return i;
/*     */       }
/* 241 */       catch (IOException ex) {
/* 242 */         throw new BeanDefinitionStoreException("Could not resolve bean definition resource pattern [" + location + "]", ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 248 */     Resource resource = resourceLoader.getResource(location);
/* 249 */     int count = loadBeanDefinitions(resource);
/* 250 */     if (actualResources != null) {
/* 251 */       actualResources.add(resource);
/*     */     }
/* 253 */     if (this.logger.isTraceEnabled()) {
/* 254 */       this.logger.trace("Loaded " + count + " bean definitions from location [" + location + "]");
/*     */     }
/* 256 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
/* 262 */     Assert.notNull(locations, "Location array must not be null");
/* 263 */     int count = 0;
/* 264 */     for (String location : locations) {
/* 265 */       count += loadBeanDefinitions(location);
/*     */     }
/* 267 */     return count;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/AbstractBeanDefinitionReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */