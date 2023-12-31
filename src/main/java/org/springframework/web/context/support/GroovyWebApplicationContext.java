/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import groovy.lang.GroovyObject;
/*     */ import groovy.lang.GroovySystem;
/*     */ import groovy.lang.MetaClass;
/*     */ import java.io.IOException;
/*     */ import org.springframework.beans.BeanWrapper;
/*     */ import org.springframework.beans.BeanWrapperImpl;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.factory.NoSuchBeanDefinitionException;
/*     */ import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
/*     */ import org.springframework.beans.factory.support.BeanDefinitionRegistry;
/*     */ import org.springframework.beans.factory.support.DefaultListableBeanFactory;
/*     */ import org.springframework.core.env.Environment;
/*     */ import org.springframework.core.io.ResourceLoader;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GroovyWebApplicationContext
/*     */   extends AbstractRefreshableWebApplicationContext
/*     */   implements GroovyObject
/*     */ {
/*     */   public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.groovy";
/*     */   public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";
/*     */   public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".groovy";
/*  82 */   private final BeanWrapper contextWrapper = (BeanWrapper)new BeanWrapperImpl(this);
/*     */   
/*  84 */   private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(getClass());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
/*  96 */     GroovyBeanDefinitionReader beanDefinitionReader = new GroovyBeanDefinitionReader((BeanDefinitionRegistry)beanFactory);
/*     */ 
/*     */ 
/*     */     
/* 100 */     beanDefinitionReader.setEnvironment((Environment)getEnvironment());
/* 101 */     beanDefinitionReader.setResourceLoader((ResourceLoader)this);
/*     */ 
/*     */ 
/*     */     
/* 105 */     initBeanDefinitionReader(beanDefinitionReader);
/* 106 */     loadBeanDefinitions(beanDefinitionReader);
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
/*     */   protected void initBeanDefinitionReader(GroovyBeanDefinitionReader beanDefinitionReader) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void loadBeanDefinitions(GroovyBeanDefinitionReader reader) throws IOException {
/* 131 */     String[] configLocations = getConfigLocations();
/* 132 */     if (configLocations != null) {
/* 133 */       for (String configLocation : configLocations) {
/* 134 */         reader.loadBeanDefinitions(configLocation);
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
/*     */   protected String[] getDefaultConfigLocations() {
/* 146 */     if (getNamespace() != null) {
/* 147 */       return new String[] { "/WEB-INF/" + getNamespace() + ".groovy" };
/*     */     }
/*     */     
/* 150 */     return new String[] { "/WEB-INF/applicationContext.groovy" };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMetaClass(MetaClass metaClass) {
/* 159 */     this.metaClass = metaClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public MetaClass getMetaClass() {
/* 164 */     return this.metaClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object invokeMethod(String name, Object args) {
/* 169 */     return this.metaClass.invokeMethod(this, name, args);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setProperty(String property, Object newValue) {
/* 174 */     this.metaClass.setProperty(this, property, newValue);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getProperty(String property) {
/* 180 */     if (containsBean(property)) {
/* 181 */       return getBean(property);
/*     */     }
/* 183 */     if (this.contextWrapper.isReadableProperty(property)) {
/* 184 */       return this.contextWrapper.getPropertyValue(property);
/*     */     }
/* 186 */     throw new NoSuchBeanDefinitionException(property);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/GroovyWebApplicationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */