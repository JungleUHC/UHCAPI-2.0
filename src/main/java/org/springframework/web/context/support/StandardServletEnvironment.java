/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.springframework.core.env.MutablePropertySources;
/*     */ import org.springframework.core.env.PropertySource;
/*     */ import org.springframework.core.env.StandardEnvironment;
/*     */ import org.springframework.jndi.JndiLocatorDelegate;
/*     */ import org.springframework.jndi.JndiPropertySource;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.web.context.ConfigurableWebEnvironment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StandardServletEnvironment
/*     */   extends StandardEnvironment
/*     */   implements ConfigurableWebEnvironment
/*     */ {
/*     */   public static final String SERVLET_CONTEXT_PROPERTY_SOURCE_NAME = "servletContextInitParams";
/*     */   public static final String SERVLET_CONFIG_PROPERTY_SOURCE_NAME = "servletConfigInitParams";
/*     */   public static final String JNDI_PROPERTY_SOURCE_NAME = "jndiProperties";
/*  60 */   private static final boolean jndiPresent = ClassUtils.isPresent("javax.naming.InitialContext", StandardServletEnvironment.class
/*  61 */       .getClassLoader());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StandardServletEnvironment() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StandardServletEnvironment(MutablePropertySources propertySources) {
/*  76 */     super(propertySources);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void customizePropertySources(MutablePropertySources propertySources) {
/* 108 */     propertySources.addLast((PropertySource)new PropertySource.StubPropertySource("servletConfigInitParams"));
/* 109 */     propertySources.addLast((PropertySource)new PropertySource.StubPropertySource("servletContextInitParams"));
/* 110 */     if (jndiPresent && JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable()) {
/* 111 */       propertySources.addLast((PropertySource)new JndiPropertySource("jndiProperties"));
/*     */     }
/* 113 */     super.customizePropertySources(propertySources);
/*     */   }
/*     */ 
/*     */   
/*     */   public void initPropertySources(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
/* 118 */     WebApplicationContextUtils.initServletPropertySources(getPropertySources(), servletContext, servletConfig);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/StandardServletEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */