/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import javax.servlet.ServletContext;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.web.context.ContextLoader;
/*     */ import org.springframework.web.context.WebApplicationContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SpringBeanAutowiringSupport
/*     */ {
/*  58 */   private static final Log logger = LogFactory.getLog(SpringBeanAutowiringSupport.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SpringBeanAutowiringSupport() {
/*  68 */     processInjectionBasedOnCurrentContext(this);
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
/*     */   public static void processInjectionBasedOnCurrentContext(Object target) {
/*  80 */     Assert.notNull(target, "Target object must not be null");
/*  81 */     WebApplicationContext cc = ContextLoader.getCurrentWebApplicationContext();
/*  82 */     if (cc != null) {
/*  83 */       AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
/*  84 */       bpp.setBeanFactory((BeanFactory)cc.getAutowireCapableBeanFactory());
/*  85 */       bpp.processInjection(target);
/*     */     
/*     */     }
/*  88 */     else if (logger.isWarnEnabled()) {
/*  89 */       logger.warn("Current WebApplicationContext is not available for processing of " + 
/*  90 */           ClassUtils.getShortName(target.getClass()) + ": Make sure this class gets constructed in a Spring web application after the Spring WebApplicationContext has been initialized. Proceeding without injection.");
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
/*     */   public static void processInjectionBasedOnServletContext(Object target, ServletContext servletContext) {
/* 107 */     Assert.notNull(target, "Target object must not be null");
/* 108 */     WebApplicationContext cc = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
/* 109 */     AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
/* 110 */     bpp.setBeanFactory((BeanFactory)cc.getAutowireCapableBeanFactory());
/* 111 */     bpp.processInjection(target);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/SpringBeanAutowiringSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */