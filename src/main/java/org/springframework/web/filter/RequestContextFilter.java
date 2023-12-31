/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.springframework.context.i18n.LocaleContextHolder;
/*     */ import org.springframework.web.context.request.RequestAttributes;
/*     */ import org.springframework.web.context.request.RequestContextHolder;
/*     */ import org.springframework.web.context.request.ServletRequestAttributes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RequestContextFilter
/*     */   extends OncePerRequestFilter
/*     */ {
/*     */   private boolean threadContextInheritable = false;
/*     */   
/*     */   public void setThreadContextInheritable(boolean threadContextInheritable) {
/*  69 */     this.threadContextInheritable = threadContextInheritable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean shouldNotFilterAsyncDispatch() {
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean shouldNotFilterErrorDispatch() {
/*  88 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*  96 */     ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
/*  97 */     initContextHolders(request, attributes);
/*     */     
/*     */     try {
/* 100 */       filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
/*     */     } finally {
/*     */       
/* 103 */       resetContextHolders();
/* 104 */       if (this.logger.isTraceEnabled()) {
/* 105 */         this.logger.trace("Cleared thread-bound request context: " + request);
/*     */       }
/* 107 */       attributes.requestCompleted();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void initContextHolders(HttpServletRequest request, ServletRequestAttributes requestAttributes) {
/* 112 */     LocaleContextHolder.setLocale(request.getLocale(), this.threadContextInheritable);
/* 113 */     RequestContextHolder.setRequestAttributes((RequestAttributes)requestAttributes, this.threadContextInheritable);
/* 114 */     if (this.logger.isTraceEnabled()) {
/* 115 */       this.logger.trace("Bound request context to thread: " + request);
/*     */     }
/*     */   }
/*     */   
/*     */   private void resetContextHolders() {
/* 120 */     LocaleContextHolder.resetLocaleContext();
/* 121 */     RequestContextHolder.resetRequestAttributes();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/RequestContextFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */