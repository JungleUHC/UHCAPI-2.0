/*     */ package org.springframework.beans.factory.config;
/*     */ 
/*     */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.beans.factory.BeanFactoryAware;
/*     */ import org.springframework.beans.factory.BeanNameAware;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.StringValueResolver;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class PlaceholderConfigurerSupport
/*     */   extends PropertyResourceConfigurer
/*     */   implements BeanNameAware, BeanFactoryAware
/*     */ {
/*     */   public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
/*     */   public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
/*     */   public static final String DEFAULT_VALUE_SEPARATOR = ":";
/* 105 */   protected String placeholderPrefix = "${";
/*     */ 
/*     */   
/* 108 */   protected String placeholderSuffix = "}";
/*     */   
/*     */   @Nullable
/* 111 */   protected String valueSeparator = ":";
/*     */ 
/*     */   
/*     */   protected boolean trimValues = false;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String nullValue;
/*     */ 
/*     */   
/*     */   protected boolean ignoreUnresolvablePlaceholders = false;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String beanName;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private BeanFactory beanFactory;
/*     */ 
/*     */   
/*     */   public void setPlaceholderPrefix(String placeholderPrefix) {
/* 133 */     this.placeholderPrefix = placeholderPrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPlaceholderSuffix(String placeholderSuffix) {
/* 141 */     this.placeholderSuffix = placeholderSuffix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValueSeparator(@Nullable String valueSeparator) {
/* 151 */     this.valueSeparator = valueSeparator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrimValues(boolean trimValues) {
/* 161 */     this.trimValues = trimValues;
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
/*     */   public void setNullValue(String nullValue) {
/* 174 */     this.nullValue = nullValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
/* 185 */     this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
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
/*     */   public void setBeanName(String beanName) {
/* 198 */     this.beanName = beanName;
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
/*     */   public void setBeanFactory(BeanFactory beanFactory) {
/* 211 */     this.beanFactory = beanFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess, StringValueResolver valueResolver) {
/* 218 */     BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);
/*     */     
/* 220 */     String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
/* 221 */     for (String curName : beanNames) {
/*     */ 
/*     */       
/* 224 */       if (!curName.equals(this.beanName) || !beanFactoryToProcess.equals(this.beanFactory)) {
/* 225 */         BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
/*     */         try {
/* 227 */           visitor.visitBeanDefinition(bd);
/*     */         }
/* 229 */         catch (Exception ex) {
/* 230 */           throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 236 */     beanFactoryToProcess.resolveAliases(valueResolver);
/*     */ 
/*     */     
/* 239 */     beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/PlaceholderConfigurerSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */