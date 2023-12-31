/*     */ package org.springframework.beans.factory.support;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.MutablePropertyValues;
/*     */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*     */ import org.springframework.beans.factory.CannotLoadBeanClassException;
/*     */ import org.springframework.beans.factory.config.ConstructorArgumentValues;
/*     */ import org.springframework.beans.factory.config.RuntimeBeanReference;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.support.EncodedResource;
/*     */ import org.springframework.core.io.support.ResourcePropertiesPersister;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.PropertiesPersister;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class PropertiesBeanDefinitionReader
/*     */   extends AbstractBeanDefinitionReader
/*     */ {
/*     */   public static final String TRUE_VALUE = "true";
/*     */   public static final String SEPARATOR = ".";
/*     */   public static final String CLASS_KEY = "(class)";
/*     */   public static final String PARENT_KEY = "(parent)";
/*     */   public static final String SCOPE_KEY = "(scope)";
/*     */   public static final String SINGLETON_KEY = "(singleton)";
/*     */   public static final String ABSTRACT_KEY = "(abstract)";
/*     */   public static final String LAZY_INIT_KEY = "(lazy-init)";
/*     */   public static final String REF_SUFFIX = "(ref)";
/*     */   public static final String REF_PREFIX = "*";
/*     */   public static final String CONSTRUCTOR_ARG_PREFIX = "$";
/*     */   @Nullable
/*     */   private String defaultParentBean;
/* 151 */   private PropertiesPersister propertiesPersister = (PropertiesPersister)ResourcePropertiesPersister.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertiesBeanDefinitionReader(BeanDefinitionRegistry registry) {
/* 160 */     super(registry);
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
/*     */   public void setDefaultParentBean(@Nullable String defaultParentBean) {
/* 177 */     this.defaultParentBean = defaultParentBean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getDefaultParentBean() {
/* 185 */     return this.defaultParentBean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPropertiesPersister(@Nullable PropertiesPersister propertiesPersister) {
/* 194 */     this.propertiesPersister = (propertiesPersister != null) ? propertiesPersister : (PropertiesPersister)ResourcePropertiesPersister.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertiesPersister getPropertiesPersister() {
/* 202 */     return this.propertiesPersister;
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
/*     */   public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
/* 216 */     return loadBeanDefinitions(new EncodedResource(resource), (String)null);
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
/*     */   public int loadBeanDefinitions(Resource resource, @Nullable String prefix) throws BeanDefinitionStoreException {
/* 228 */     return loadBeanDefinitions(new EncodedResource(resource), prefix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
/* 239 */     return loadBeanDefinitions(encodedResource, (String)null);
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
/*     */   public int loadBeanDefinitions(EncodedResource encodedResource, @Nullable String prefix) throws BeanDefinitionStoreException {
/* 254 */     if (this.logger.isTraceEnabled()) {
/* 255 */       this.logger.trace("Loading properties bean definitions from " + encodedResource);
/*     */     }
/*     */     
/* 258 */     Properties props = new Properties();
/*     */     try {
/* 260 */       try (InputStream is = encodedResource.getResource().getInputStream()) {
/* 261 */         if (encodedResource.getEncoding() != null) {
/* 262 */           getPropertiesPersister().load(props, new InputStreamReader(is, encodedResource.getEncoding()));
/*     */         } else {
/*     */           
/* 265 */           getPropertiesPersister().load(props, is);
/*     */         } 
/*     */       } 
/*     */       
/* 269 */       int count = registerBeanDefinitions(props, prefix, encodedResource.getResource().getDescription());
/* 270 */       if (this.logger.isDebugEnabled()) {
/* 271 */         this.logger.debug("Loaded " + count + " bean definitions from " + encodedResource);
/*     */       }
/* 273 */       return count;
/*     */     }
/* 275 */     catch (IOException ex) {
/* 276 */       throw new BeanDefinitionStoreException("Could not parse properties from " + encodedResource.getResource(), ex);
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
/*     */   public int registerBeanDefinitions(ResourceBundle rb) throws BeanDefinitionStoreException {
/* 289 */     return registerBeanDefinitions(rb, (String)null);
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
/*     */   public int registerBeanDefinitions(ResourceBundle rb, @Nullable String prefix) throws BeanDefinitionStoreException {
/* 304 */     Map<String, Object> map = new HashMap<>();
/* 305 */     Enumeration<String> keys = rb.getKeys();
/* 306 */     while (keys.hasMoreElements()) {
/* 307 */       String key = keys.nextElement();
/* 308 */       map.put(key, rb.getObject(key));
/*     */     } 
/* 310 */     return registerBeanDefinitions(map, prefix);
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
/*     */   public int registerBeanDefinitions(Map<?, ?> map) throws BeansException {
/* 325 */     return registerBeanDefinitions(map, (String)null);
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
/*     */   public int registerBeanDefinitions(Map<?, ?> map, @Nullable String prefix) throws BeansException {
/* 340 */     return registerBeanDefinitions(map, prefix, "Map " + map);
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
/*     */   public int registerBeanDefinitions(Map<?, ?> map, @Nullable String prefix, String resourceDescription) throws BeansException {
/* 360 */     if (prefix == null) {
/* 361 */       prefix = "";
/*     */     }
/* 363 */     int beanCount = 0;
/*     */     
/* 365 */     for (Object key : map.keySet()) {
/* 366 */       if (!(key instanceof String)) {
/* 367 */         throw new IllegalArgumentException("Illegal key [" + key + "]: only Strings allowed");
/*     */       }
/* 369 */       String keyString = (String)key;
/* 370 */       if (keyString.startsWith(prefix)) {
/*     */         int sepIdx;
/* 372 */         String nameAndProperty = keyString.substring(prefix.length());
/*     */ 
/*     */         
/* 375 */         int propKeyIdx = nameAndProperty.indexOf("[");
/* 376 */         if (propKeyIdx != -1) {
/* 377 */           sepIdx = nameAndProperty.lastIndexOf(".", propKeyIdx);
/*     */         } else {
/*     */           
/* 380 */           sepIdx = nameAndProperty.lastIndexOf(".");
/*     */         } 
/* 382 */         if (sepIdx != -1) {
/* 383 */           String beanName = nameAndProperty.substring(0, sepIdx);
/* 384 */           if (this.logger.isTraceEnabled()) {
/* 385 */             this.logger.trace("Found bean name '" + beanName + "'");
/*     */           }
/* 387 */           if (!getRegistry().containsBeanDefinition(beanName)) {
/*     */             
/* 389 */             registerBeanDefinition(beanName, map, prefix + beanName, resourceDescription);
/* 390 */             beanCount++;
/*     */           } 
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 396 */         if (this.logger.isDebugEnabled()) {
/* 397 */           this.logger.debug("Invalid bean name and property [" + nameAndProperty + "]");
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 403 */     return beanCount;
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
/*     */   protected void registerBeanDefinition(String beanName, Map<?, ?> map, String prefix, String resourceDescription) throws BeansException {
/* 419 */     String className = null;
/* 420 */     String parent = null;
/* 421 */     String scope = "singleton";
/* 422 */     boolean isAbstract = false;
/* 423 */     boolean lazyInit = false;
/*     */     
/* 425 */     ConstructorArgumentValues cas = new ConstructorArgumentValues();
/* 426 */     MutablePropertyValues pvs = new MutablePropertyValues();
/*     */     
/* 428 */     String prefixWithSep = prefix + ".";
/* 429 */     int beginIndex = prefixWithSep.length();
/*     */     
/* 431 */     for (Map.Entry<?, ?> entry : map.entrySet()) {
/* 432 */       String key = StringUtils.trimWhitespace((String)entry.getKey());
/* 433 */       if (key.startsWith(prefixWithSep)) {
/* 434 */         String property = key.substring(beginIndex);
/* 435 */         if ("(class)".equals(property)) {
/* 436 */           className = StringUtils.trimWhitespace((String)entry.getValue()); continue;
/*     */         } 
/* 438 */         if ("(parent)".equals(property)) {
/* 439 */           parent = StringUtils.trimWhitespace((String)entry.getValue()); continue;
/*     */         } 
/* 441 */         if ("(abstract)".equals(property)) {
/* 442 */           String val = StringUtils.trimWhitespace((String)entry.getValue());
/* 443 */           isAbstract = "true".equals(val); continue;
/*     */         } 
/* 445 */         if ("(scope)".equals(property)) {
/*     */           
/* 447 */           scope = StringUtils.trimWhitespace((String)entry.getValue()); continue;
/*     */         } 
/* 449 */         if ("(singleton)".equals(property)) {
/*     */           
/* 451 */           String val = StringUtils.trimWhitespace((String)entry.getValue());
/* 452 */           scope = (!StringUtils.hasLength(val) || "true".equals(val)) ? "singleton" : "prototype";
/*     */           continue;
/*     */         } 
/* 455 */         if ("(lazy-init)".equals(property)) {
/* 456 */           String val = StringUtils.trimWhitespace((String)entry.getValue());
/* 457 */           lazyInit = "true".equals(val); continue;
/*     */         } 
/* 459 */         if (property.startsWith("$")) {
/* 460 */           if (property.endsWith("(ref)")) {
/* 461 */             int i = Integer.parseInt(property.substring(1, property.length() - "(ref)".length()));
/* 462 */             cas.addIndexedArgumentValue(i, new RuntimeBeanReference(entry.getValue().toString()));
/*     */             continue;
/*     */           } 
/* 465 */           int index = Integer.parseInt(property.substring(1));
/* 466 */           cas.addIndexedArgumentValue(index, readValue(entry));
/*     */           continue;
/*     */         } 
/* 469 */         if (property.endsWith("(ref)")) {
/*     */ 
/*     */           
/* 472 */           property = property.substring(0, property.length() - "(ref)".length());
/* 473 */           String ref = StringUtils.trimWhitespace((String)entry.getValue());
/*     */ 
/*     */ 
/*     */           
/* 477 */           Object val = new RuntimeBeanReference(ref);
/* 478 */           pvs.add(property, val);
/*     */           
/*     */           continue;
/*     */         } 
/* 482 */         pvs.add(property, readValue(entry));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 487 */     if (this.logger.isTraceEnabled()) {
/* 488 */       this.logger.trace("Registering bean definition for bean name '" + beanName + "' with " + pvs);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 494 */     if (parent == null && className == null && !beanName.equals(this.defaultParentBean)) {
/* 495 */       parent = this.defaultParentBean;
/*     */     }
/*     */     
/*     */     try {
/* 499 */       AbstractBeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(parent, className, 
/* 500 */           getBeanClassLoader());
/* 501 */       bd.setScope(scope);
/* 502 */       bd.setAbstract(isAbstract);
/* 503 */       bd.setLazyInit(lazyInit);
/* 504 */       bd.setConstructorArgumentValues(cas);
/* 505 */       bd.setPropertyValues(pvs);
/* 506 */       getRegistry().registerBeanDefinition(beanName, bd);
/*     */     }
/* 508 */     catch (ClassNotFoundException ex) {
/* 509 */       throw new CannotLoadBeanClassException(resourceDescription, beanName, className, ex);
/*     */     }
/* 511 */     catch (LinkageError err) {
/* 512 */       throw new CannotLoadBeanClassException(resourceDescription, beanName, className, err);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object readValue(Map.Entry<?, ?> entry) {
/* 521 */     Object val = entry.getValue();
/* 522 */     if (val instanceof String) {
/* 523 */       String strVal = (String)val;
/*     */       
/* 525 */       if (strVal.startsWith("*")) {
/*     */         
/* 527 */         String targetName = strVal.substring(1);
/* 528 */         if (targetName.startsWith("*")) {
/*     */           
/* 530 */           val = targetName;
/*     */         } else {
/*     */           
/* 533 */           val = new RuntimeBeanReference(targetName);
/*     */         } 
/*     */       } 
/*     */     } 
/* 537 */     return val;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/PropertiesBeanDefinitionReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */