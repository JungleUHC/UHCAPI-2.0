/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.servlet.Filter;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import org.springframework.context.ConfigurableApplicationContext;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.web.context.WebApplicationContext;
/*     */ import org.springframework.web.context.support.WebApplicationContextUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DelegatingFilterProxy
/*     */   extends GenericFilterBean
/*     */ {
/*     */   @Nullable
/*     */   private String contextAttribute;
/*     */   @Nullable
/*     */   private WebApplicationContext webApplicationContext;
/*     */   @Nullable
/*     */   private String targetBeanName;
/*     */   private boolean targetFilterLifecycle = false;
/*     */   @Nullable
/*     */   private volatile Filter delegate;
/*  99 */   private final Object delegateMonitor = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DelegatingFilterProxy() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DelegatingFilterProxy(Filter delegate) {
/* 122 */     Assert.notNull(delegate, "Delegate Filter must not be null");
/* 123 */     this.delegate = delegate;
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
/*     */   public DelegatingFilterProxy(String targetBeanName) {
/* 139 */     this(targetBeanName, (WebApplicationContext)null);
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
/*     */   public DelegatingFilterProxy(String targetBeanName, @Nullable WebApplicationContext wac) {
/* 162 */     Assert.hasText(targetBeanName, "Target Filter bean name must not be null or empty");
/* 163 */     setTargetBeanName(targetBeanName);
/* 164 */     this.webApplicationContext = wac;
/* 165 */     if (wac != null) {
/* 166 */       setEnvironment(wac.getEnvironment());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContextAttribute(@Nullable String contextAttribute) {
/* 175 */     this.contextAttribute = contextAttribute;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getContextAttribute() {
/* 184 */     return this.contextAttribute;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTargetBeanName(@Nullable String targetBeanName) {
/* 194 */     this.targetBeanName = targetBeanName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String getTargetBeanName() {
/* 202 */     return this.targetBeanName;
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
/*     */   public void setTargetFilterLifecycle(boolean targetFilterLifecycle) {
/* 214 */     this.targetFilterLifecycle = targetFilterLifecycle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isTargetFilterLifecycle() {
/* 222 */     return this.targetFilterLifecycle;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initFilterBean() throws ServletException {
/* 228 */     synchronized (this.delegateMonitor) {
/* 229 */       if (this.delegate == null) {
/*     */         
/* 231 */         if (this.targetBeanName == null) {
/* 232 */           this.targetBeanName = getFilterName();
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 237 */         WebApplicationContext wac = findWebApplicationContext();
/* 238 */         if (wac != null) {
/* 239 */           this.delegate = initDelegate(wac);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/* 250 */     Filter delegateToUse = this.delegate;
/* 251 */     if (delegateToUse == null) {
/* 252 */       synchronized (this.delegateMonitor) {
/* 253 */         delegateToUse = this.delegate;
/* 254 */         if (delegateToUse == null) {
/* 255 */           WebApplicationContext wac = findWebApplicationContext();
/* 256 */           if (wac == null) {
/* 257 */             throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener or DispatcherServlet registered?");
/*     */           }
/*     */           
/* 260 */           delegateToUse = initDelegate(wac);
/*     */         } 
/* 262 */         this.delegate = delegateToUse;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 267 */     invokeDelegate(delegateToUse, request, response, filterChain);
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 272 */     Filter delegateToUse = this.delegate;
/* 273 */     if (delegateToUse != null) {
/* 274 */       destroyDelegate(delegateToUse);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected WebApplicationContext findWebApplicationContext() {
/* 297 */     if (this.webApplicationContext != null) {
/*     */       
/* 299 */       if (this.webApplicationContext instanceof ConfigurableApplicationContext) {
/* 300 */         ConfigurableApplicationContext cac = (ConfigurableApplicationContext)this.webApplicationContext;
/* 301 */         if (!cac.isActive())
/*     */         {
/* 303 */           cac.refresh();
/*     */         }
/*     */       } 
/* 306 */       return this.webApplicationContext;
/*     */     } 
/* 308 */     String attrName = getContextAttribute();
/* 309 */     if (attrName != null) {
/* 310 */       return WebApplicationContextUtils.getWebApplicationContext(getServletContext(), attrName);
/*     */     }
/*     */     
/* 313 */     return WebApplicationContextUtils.findWebApplicationContext(getServletContext());
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
/*     */   protected Filter initDelegate(WebApplicationContext wac) throws ServletException {
/* 332 */     String targetBeanName = getTargetBeanName();
/* 333 */     Assert.state((targetBeanName != null), "No target bean name set");
/* 334 */     Filter delegate = (Filter)wac.getBean(targetBeanName, Filter.class);
/* 335 */     if (isTargetFilterLifecycle()) {
/* 336 */       delegate.init(getFilterConfig());
/*     */     }
/* 338 */     return delegate;
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
/*     */   protected void invokeDelegate(Filter delegate, ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/* 354 */     delegate.doFilter(request, response, filterChain);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void destroyDelegate(Filter delegate) {
/* 365 */     if (isTargetFilterLifecycle())
/* 366 */       delegate.destroy(); 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/DelegatingFilterProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */