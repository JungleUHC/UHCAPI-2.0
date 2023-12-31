/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
/*     */ import org.springframework.beans.factory.support.DefaultListableBeanFactory;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.context.support.GenericApplicationContext;
/*     */ import org.springframework.core.env.ConfigurableEnvironment;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.ResourceLoader;
/*     */ import org.springframework.core.io.support.ResourcePatternResolver;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.ui.context.Theme;
/*     */ import org.springframework.ui.context.ThemeSource;
/*     */ import org.springframework.ui.context.support.UiApplicationContextUtils;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.context.ConfigurableWebApplicationContext;
/*     */ import org.springframework.web.context.ConfigurableWebEnvironment;
/*     */ import org.springframework.web.context.ServletContextAware;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GenericWebApplicationContext
/*     */   extends GenericApplicationContext
/*     */   implements ConfigurableWebApplicationContext, ThemeSource
/*     */ {
/*     */   @Nullable
/*     */   private ServletContext servletContext;
/*     */   @Nullable
/*     */   private ThemeSource themeSource;
/*     */   
/*     */   public GenericWebApplicationContext() {}
/*     */   
/*     */   public GenericWebApplicationContext(ServletContext servletContext) {
/* 107 */     this.servletContext = servletContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory) {
/* 118 */     super(beanFactory);
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
/*     */   public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory, ServletContext servletContext) {
/* 130 */     super(beanFactory);
/* 131 */     this.servletContext = servletContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServletContext(@Nullable ServletContext servletContext) {
/* 140 */     this.servletContext = servletContext;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ServletContext getServletContext() {
/* 146 */     return this.servletContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getApplicationName() {
/* 151 */     return (this.servletContext != null) ? this.servletContext.getContextPath() : "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ConfigurableEnvironment createEnvironment() {
/* 159 */     return (ConfigurableEnvironment)new StandardServletEnvironment();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
/* 167 */     if (this.servletContext != null) {
/* 168 */       beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext));
/* 169 */       beanFactory.ignoreDependencyInterface(ServletContextAware.class);
/*     */     } 
/* 171 */     WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
/* 172 */     WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Resource getResourceByPath(String path) {
/* 181 */     Assert.state((this.servletContext != null), "No ServletContext available");
/* 182 */     return (Resource)new ServletContextResource(this.servletContext, path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ResourcePatternResolver getResourcePatternResolver() {
/* 191 */     return (ResourcePatternResolver)new ServletContextResourcePatternResolver((ResourceLoader)this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void onRefresh() {
/* 199 */     this.themeSource = UiApplicationContextUtils.initThemeSource((ApplicationContext)this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initPropertySources() {
/* 208 */     ConfigurableEnvironment env = getEnvironment();
/* 209 */     if (env instanceof ConfigurableWebEnvironment) {
/* 210 */       ((ConfigurableWebEnvironment)env).initPropertySources(this.servletContext, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Theme getTheme(String themeName) {
/* 217 */     Assert.state((this.themeSource != null), "No ThemeSource available");
/* 218 */     return this.themeSource.getTheme(themeName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServletConfig(@Nullable ServletConfig servletConfig) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ServletConfig getServletConfig() {
/* 234 */     throw new UnsupportedOperationException("GenericWebApplicationContext does not support getServletConfig()");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNamespace(@Nullable String namespace) {}
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getNamespace() {
/* 246 */     throw new UnsupportedOperationException("GenericWebApplicationContext does not support getNamespace()");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConfigLocation(String configLocation) {
/* 252 */     if (StringUtils.hasText(configLocation)) {
/* 253 */       throw new UnsupportedOperationException("GenericWebApplicationContext does not support setConfigLocation(). Do you still have a 'contextConfigLocation' init-param set?");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConfigLocations(String... configLocations) {
/* 261 */     if (!ObjectUtils.isEmpty((Object[])configLocations)) {
/* 262 */       throw new UnsupportedOperationException("GenericWebApplicationContext does not support setConfigLocations(). Do you still have a 'contextConfigLocations' init-param set?");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getConfigLocations() {
/* 270 */     throw new UnsupportedOperationException("GenericWebApplicationContext does not support getConfigLocations()");
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/GenericWebApplicationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */