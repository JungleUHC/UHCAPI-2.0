/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.beans.PropertyEditor;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.servlet.Filter;
/*     */ import javax.servlet.FilterConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeanWrapper;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.MutablePropertyValues;
/*     */ import org.springframework.beans.PropertyAccessorFactory;
/*     */ import org.springframework.beans.PropertyValue;
/*     */ import org.springframework.beans.PropertyValues;
/*     */ import org.springframework.beans.factory.BeanNameAware;
/*     */ import org.springframework.beans.factory.DisposableBean;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.context.EnvironmentAware;
/*     */ import org.springframework.core.env.Environment;
/*     */ import org.springframework.core.env.EnvironmentCapable;
/*     */ import org.springframework.core.env.PropertyResolver;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.ResourceEditor;
/*     */ import org.springframework.core.io.ResourceLoader;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.context.ServletContextAware;
/*     */ import org.springframework.web.context.support.ServletContextResourceLoader;
/*     */ import org.springframework.web.context.support.StandardServletEnvironment;
/*     */ import org.springframework.web.util.NestedServletException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class GenericFilterBean
/*     */   implements Filter, BeanNameAware, EnvironmentAware, EnvironmentCapable, ServletContextAware, InitializingBean, DisposableBean
/*     */ {
/*  86 */   protected final Log logger = LogFactory.getLog(getClass());
/*     */   
/*     */   @Nullable
/*     */   private String beanName;
/*     */   
/*     */   @Nullable
/*     */   private Environment environment;
/*     */   
/*     */   @Nullable
/*     */   private ServletContext servletContext;
/*     */   
/*     */   @Nullable
/*     */   private FilterConfig filterConfig;
/*     */   
/* 100 */   private final Set<String> requiredProperties = new HashSet<>(4);
/*     */ 
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
/* 112 */     this.beanName = beanName;
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
/*     */   public void setEnvironment(Environment environment) {
/* 125 */     this.environment = environment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Environment getEnvironment() {
/* 136 */     if (this.environment == null) {
/* 137 */       this.environment = createEnvironment();
/*     */     }
/* 139 */     return this.environment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Environment createEnvironment() {
/* 149 */     return (Environment)new StandardServletEnvironment();
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
/*     */   public void setServletContext(ServletContext servletContext) {
/* 161 */     this.servletContext = servletContext;
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
/*     */   public void afterPropertiesSet() throws ServletException {
/* 174 */     initFilterBean();
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
/*     */   public void destroy() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void addRequiredProperty(String property) {
/* 198 */     this.requiredProperties.add(property);
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
/*     */   public final void init(FilterConfig filterConfig) throws ServletException {
/* 212 */     Assert.notNull(filterConfig, "FilterConfig must not be null");
/*     */     
/* 214 */     this.filterConfig = filterConfig;
/*     */ 
/*     */     
/* 217 */     FilterConfigPropertyValues filterConfigPropertyValues = new FilterConfigPropertyValues(filterConfig, this.requiredProperties);
/* 218 */     if (!filterConfigPropertyValues.isEmpty()) {
/*     */       try {
/* 220 */         StandardServletEnvironment standardServletEnvironment; BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
/* 221 */         ServletContextResourceLoader servletContextResourceLoader = new ServletContextResourceLoader(filterConfig.getServletContext());
/* 222 */         Environment env = this.environment;
/* 223 */         if (env == null) {
/* 224 */           standardServletEnvironment = new StandardServletEnvironment();
/*     */         }
/* 226 */         bw.registerCustomEditor(Resource.class, (PropertyEditor)new ResourceEditor((ResourceLoader)servletContextResourceLoader, (PropertyResolver)standardServletEnvironment));
/* 227 */         initBeanWrapper(bw);
/* 228 */         bw.setPropertyValues((PropertyValues)filterConfigPropertyValues, true);
/*     */       }
/* 230 */       catch (BeansException ex) {
/*     */         
/* 232 */         String msg = "Failed to set bean properties on filter '" + filterConfig.getFilterName() + "': " + ex.getMessage();
/* 233 */         this.logger.error(msg, (Throwable)ex);
/* 234 */         throw new NestedServletException(msg, ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 239 */     initFilterBean();
/*     */     
/* 241 */     if (this.logger.isDebugEnabled()) {
/* 242 */       this.logger.debug("Filter '" + filterConfig.getFilterName() + "' configured for use");
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
/*     */   protected void initBeanWrapper(BeanWrapper bw) throws BeansException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initFilterBean() throws ServletException {}
/*     */ 
/*     */ 
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
/*     */   public FilterConfig getFilterConfig() {
/* 282 */     return this.filterConfig;
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
/*     */   @Nullable
/*     */   protected String getFilterName() {
/* 298 */     return (this.filterConfig != null) ? this.filterConfig.getFilterName() : this.beanName;
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
/*     */   protected ServletContext getServletContext() {
/* 314 */     if (this.filterConfig != null) {
/* 315 */       return this.filterConfig.getServletContext();
/*     */     }
/* 317 */     if (this.servletContext != null) {
/* 318 */       return this.servletContext;
/*     */     }
/*     */     
/* 321 */     throw new IllegalStateException("No ServletContext");
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
/*     */   private static class FilterConfigPropertyValues
/*     */     extends MutablePropertyValues
/*     */   {
/*     */     public FilterConfigPropertyValues(FilterConfig config, Set<String> requiredProperties) throws ServletException {
/* 342 */       Set<String> missingProps = !CollectionUtils.isEmpty(requiredProperties) ? new HashSet<>(requiredProperties) : null;
/*     */ 
/*     */       
/* 345 */       Enumeration<String> paramNames = config.getInitParameterNames();
/* 346 */       while (paramNames.hasMoreElements()) {
/* 347 */         String property = paramNames.nextElement();
/* 348 */         Object value = config.getInitParameter(property);
/* 349 */         addPropertyValue(new PropertyValue(property, value));
/* 350 */         if (missingProps != null) {
/* 351 */           missingProps.remove(property);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 356 */       if (!CollectionUtils.isEmpty(missingProps))
/* 357 */         throw new ServletException("Initialization from FilterConfig for filter '" + config
/* 358 */             .getFilterName() + "' failed; the following required properties were missing: " + 
/*     */             
/* 360 */             StringUtils.collectionToDelimitedString(missingProps, ", ")); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/GenericFilterBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */