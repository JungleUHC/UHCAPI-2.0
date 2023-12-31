/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import java.io.File;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.context.support.ApplicationObjectSupport;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.web.context.ServletContextAware;
/*     */ import org.springframework.web.context.WebApplicationContext;
/*     */ import org.springframework.web.util.WebUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class WebApplicationObjectSupport
/*     */   extends ApplicationObjectSupport
/*     */   implements ServletContextAware
/*     */ {
/*     */   @Nullable
/*     */   private ServletContext servletContext;
/*     */   
/*     */   public final void setServletContext(ServletContext servletContext) {
/*  52 */     if (servletContext != this.servletContext) {
/*  53 */       this.servletContext = servletContext;
/*  54 */       initServletContext(servletContext);
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
/*     */   protected boolean isContextRequired() {
/*  69 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initApplicationContext(ApplicationContext context) {
/*  78 */     super.initApplicationContext(context);
/*  79 */     if (this.servletContext == null && context instanceof WebApplicationContext) {
/*  80 */       this.servletContext = ((WebApplicationContext)context).getServletContext();
/*  81 */       if (this.servletContext != null) {
/*  82 */         initServletContext(this.servletContext);
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
/*     */   protected void initServletContext(ServletContext servletContext) {}
/*     */ 
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
/*     */   protected final WebApplicationContext getWebApplicationContext() throws IllegalStateException {
/* 110 */     ApplicationContext ctx = getApplicationContext();
/* 111 */     if (ctx instanceof WebApplicationContext) {
/* 112 */       return (WebApplicationContext)getApplicationContext();
/*     */     }
/* 114 */     if (isContextRequired()) {
/* 115 */       throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run in a WebApplicationContext but in: " + ctx);
/*     */     }
/*     */ 
/*     */     
/* 119 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected final ServletContext getServletContext() throws IllegalStateException {
/* 130 */     if (this.servletContext != null) {
/* 131 */       return this.servletContext;
/*     */     }
/* 133 */     ServletContext servletContext = null;
/* 134 */     WebApplicationContext wac = getWebApplicationContext();
/* 135 */     if (wac != null) {
/* 136 */       servletContext = wac.getServletContext();
/*     */     }
/* 138 */     if (servletContext == null && isContextRequired()) {
/* 139 */       throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run within a ServletContext. Make sure the object is fully configured!");
/*     */     }
/*     */     
/* 142 */     return servletContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final File getTempDir() throws IllegalStateException {
/* 153 */     ServletContext servletContext = getServletContext();
/* 154 */     Assert.state((servletContext != null), "ServletContext is required");
/* 155 */     return WebUtils.getTempDir(servletContext);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/WebApplicationObjectSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */