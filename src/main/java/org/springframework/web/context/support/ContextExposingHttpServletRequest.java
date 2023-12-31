/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletRequestWrapper;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ public class ContextExposingHttpServletRequest
/*     */   extends HttpServletRequestWrapper
/*     */ {
/*     */   private final WebApplicationContext webApplicationContext;
/*     */   @Nullable
/*     */   private final Set<String> exposedContextBeanNames;
/*     */   @Nullable
/*     */   private Set<String> explicitAttributes;
/*     */   
/*     */   public ContextExposingHttpServletRequest(HttpServletRequest originalRequest, WebApplicationContext context) {
/*  54 */     this(originalRequest, context, null);
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
/*     */   public ContextExposingHttpServletRequest(HttpServletRequest originalRequest, WebApplicationContext context, @Nullable Set<String> exposedContextBeanNames) {
/*  68 */     super(originalRequest);
/*  69 */     Assert.notNull(context, "WebApplicationContext must not be null");
/*  70 */     this.webApplicationContext = context;
/*  71 */     this.exposedContextBeanNames = exposedContextBeanNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final WebApplicationContext getWebApplicationContext() {
/*  79 */     return this.webApplicationContext;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getAttribute(String name) {
/*  86 */     if ((this.explicitAttributes == null || !this.explicitAttributes.contains(name)) && (this.exposedContextBeanNames == null || this.exposedContextBeanNames
/*  87 */       .contains(name)) && this.webApplicationContext
/*  88 */       .containsBean(name)) {
/*  89 */       return this.webApplicationContext.getBean(name);
/*     */     }
/*     */     
/*  92 */     return super.getAttribute(name);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAttribute(String name, Object value) {
/*  98 */     super.setAttribute(name, value);
/*  99 */     if (this.explicitAttributes == null) {
/* 100 */       this.explicitAttributes = new HashSet<>(8);
/*     */     }
/* 102 */     this.explicitAttributes.add(name);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/ContextExposingHttpServletRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */