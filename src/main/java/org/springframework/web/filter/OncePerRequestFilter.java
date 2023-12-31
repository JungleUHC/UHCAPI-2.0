/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.springframework.web.context.request.async.WebAsyncUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class OncePerRequestFilter
/*     */   extends GenericFilterBean
/*     */ {
/*     */   public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";
/*     */   
/*     */   public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*  91 */     if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
/*  92 */       throw new ServletException("OncePerRequestFilter just supports HTTP requests");
/*     */     }
/*  94 */     HttpServletRequest httpRequest = (HttpServletRequest)request;
/*  95 */     HttpServletResponse httpResponse = (HttpServletResponse)response;
/*     */     
/*  97 */     String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
/*  98 */     boolean hasAlreadyFilteredAttribute = (request.getAttribute(alreadyFilteredAttributeName) != null);
/*     */     
/* 100 */     if (skipDispatch(httpRequest) || shouldNotFilter(httpRequest)) {
/*     */       
/* 102 */       filterChain.doFilter(request, response);
/*     */     }
/* 104 */     else if (hasAlreadyFilteredAttribute) {
/* 105 */       if (DispatcherType.ERROR.equals(request.getDispatcherType())) {
/* 106 */         doFilterNestedErrorDispatch(httpRequest, httpResponse, filterChain);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 111 */       filterChain.doFilter(request, response);
/*     */     }
/*     */     else {
/*     */       
/* 115 */       request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);
/*     */       try {
/* 117 */         doFilterInternal(httpRequest, httpResponse, filterChain);
/*     */       }
/*     */       finally {
/*     */         
/* 121 */         request.removeAttribute(alreadyFilteredAttributeName);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean skipDispatch(HttpServletRequest request) {
/* 127 */     if (isAsyncDispatch(request) && shouldNotFilterAsyncDispatch()) {
/* 128 */       return true;
/*     */     }
/* 130 */     if (request.getAttribute("javax.servlet.error.request_uri") != null && shouldNotFilterErrorDispatch()) {
/* 131 */       return true;
/*     */     }
/* 133 */     return false;
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
/*     */   protected boolean isAsyncDispatch(HttpServletRequest request) {
/* 146 */     return DispatcherType.ASYNC.equals(request.getDispatcherType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isAsyncStarted(HttpServletRequest request) {
/* 157 */     return WebAsyncUtils.getAsyncManager((ServletRequest)request).isConcurrentHandlingStarted();
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
/*     */   protected String getAlreadyFilteredAttributeName() {
/* 170 */     String name = getFilterName();
/* 171 */     if (name == null) {
/* 172 */       name = getClass().getName();
/*     */     }
/* 174 */     return name + ".FILTERED";
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
/*     */   protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
/* 186 */     return false;
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
/*     */   protected boolean shouldNotFilterAsyncDispatch() {
/* 207 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean shouldNotFilterErrorDispatch() {
/* 218 */     return true;
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
/*     */   protected abstract void doFilterInternal(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, FilterChain paramFilterChain) throws ServletException, IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/* 249 */     filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/OncePerRequestFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */