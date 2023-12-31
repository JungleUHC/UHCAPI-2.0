/*     */ package org.springframework.beans.factory.config;
/*     */ 
/*     */ import java.util.Properties;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.core.Constants;
/*     */ import org.springframework.core.SpringProperties;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.PropertyPlaceholderHelper;
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
/*     */ @Deprecated
/*     */ public class PropertyPlaceholderConfigurer
/*     */   extends PlaceholderConfigurerSupport
/*     */ {
/*     */   public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;
/*     */   public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;
/*     */   public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
/*  75 */   private static final Constants constants = new Constants(PropertyPlaceholderConfigurer.class);
/*     */   
/*  77 */   private int systemPropertiesMode = 1;
/*     */ 
/*     */   
/*  80 */   private boolean searchSystemEnvironment = !SpringProperties.getFlag("spring.getenv.ignore");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
/*  90 */     this.systemPropertiesMode = constants.asNumber(constantName).intValue();
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
/*     */   public void setSystemPropertiesMode(int systemPropertiesMode) {
/* 106 */     this.systemPropertiesMode = systemPropertiesMode;
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
/*     */   public void setSearchSystemEnvironment(boolean searchSystemEnvironment) {
/* 123 */     this.searchSystemEnvironment = searchSystemEnvironment;
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
/*     */   @Nullable
/*     */   protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
/* 144 */     String propVal = null;
/* 145 */     if (systemPropertiesMode == 2) {
/* 146 */       propVal = resolveSystemProperty(placeholder);
/*     */     }
/* 148 */     if (propVal == null) {
/* 149 */       propVal = resolvePlaceholder(placeholder, props);
/*     */     }
/* 151 */     if (propVal == null && systemPropertiesMode == 1) {
/* 152 */       propVal = resolveSystemProperty(placeholder);
/*     */     }
/* 154 */     return propVal;
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
/*     */   @Nullable
/*     */   protected String resolvePlaceholder(String placeholder, Properties props) {
/* 172 */     return props.getProperty(placeholder);
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
/*     */   @Nullable
/*     */   protected String resolveSystemProperty(String key) {
/*     */     try {
/* 187 */       String value = System.getProperty(key);
/* 188 */       if (value == null && this.searchSystemEnvironment) {
/* 189 */         value = System.getenv(key);
/*     */       }
/* 191 */       return value;
/*     */     }
/* 193 */     catch (Throwable ex) {
/* 194 */       if (this.logger.isDebugEnabled()) {
/* 195 */         this.logger.debug("Could not access system property '" + key + "': " + ex);
/*     */       }
/* 197 */       return null;
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
/*     */   protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
/* 210 */     StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
/* 211 */     doProcessProperties(beanFactoryToProcess, valueResolver);
/*     */   }
/*     */ 
/*     */   
/*     */   private class PlaceholderResolvingStringValueResolver
/*     */     implements StringValueResolver
/*     */   {
/*     */     private final PropertyPlaceholderHelper helper;
/*     */     private final PropertyPlaceholderHelper.PlaceholderResolver resolver;
/*     */     
/*     */     public PlaceholderResolvingStringValueResolver(Properties props) {
/* 222 */       this.helper = new PropertyPlaceholderHelper(PropertyPlaceholderConfigurer.this.placeholderPrefix, PropertyPlaceholderConfigurer.this.placeholderSuffix, PropertyPlaceholderConfigurer.this.valueSeparator, PropertyPlaceholderConfigurer.this.ignoreUnresolvablePlaceholders);
/*     */       
/* 224 */       this.resolver = new PropertyPlaceholderConfigurer.PropertyPlaceholderConfigurerResolver(props);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String resolveStringValue(String strVal) throws BeansException {
/* 230 */       String resolved = this.helper.replacePlaceholders(strVal, this.resolver);
/* 231 */       if (PropertyPlaceholderConfigurer.this.trimValues) {
/* 232 */         resolved = resolved.trim();
/*     */       }
/* 234 */       return resolved.equals(PropertyPlaceholderConfigurer.this.nullValue) ? null : resolved;
/*     */     }
/*     */   }
/*     */   
/*     */   private final class PropertyPlaceholderConfigurerResolver
/*     */     implements PropertyPlaceholderHelper.PlaceholderResolver
/*     */   {
/*     */     private final Properties props;
/*     */     
/*     */     private PropertyPlaceholderConfigurerResolver(Properties props) {
/* 244 */       this.props = props;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String resolvePlaceholder(String placeholderName) {
/* 250 */       return PropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName, this.props, PropertyPlaceholderConfigurer.this
/* 251 */           .systemPropertiesMode);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/PropertyPlaceholderConfigurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */