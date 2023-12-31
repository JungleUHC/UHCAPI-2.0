/*     */ package org.springframework.web.multipart.support;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.springframework.web.context.WebApplicationContext;
/*     */ import org.springframework.web.context.support.WebApplicationContextUtils;
/*     */ import org.springframework.web.filter.OncePerRequestFilter;
/*     */ import org.springframework.web.multipart.MultipartHttpServletRequest;
/*     */ import org.springframework.web.multipart.MultipartResolver;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultipartFilter
/*     */   extends OncePerRequestFilter
/*     */ {
/*     */   public static final String DEFAULT_MULTIPART_RESOLVER_BEAN_NAME = "filterMultipartResolver";
/*  74 */   private final MultipartResolver defaultMultipartResolver = new StandardServletMultipartResolver();
/*     */   
/*  76 */   private String multipartResolverBeanName = "filterMultipartResolver";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMultipartResolverBeanName(String multipartResolverBeanName) {
/*  84 */     this.multipartResolverBeanName = multipartResolverBeanName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getMultipartResolverBeanName() {
/*  92 */     return this.multipartResolverBeanName;
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
/*     */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*     */     MultipartHttpServletRequest multipartHttpServletRequest;
/* 108 */     MultipartResolver multipartResolver = lookupMultipartResolver(request);
/*     */     
/* 110 */     HttpServletRequest processedRequest = request;
/* 111 */     if (multipartResolver.isMultipart(processedRequest)) {
/* 112 */       if (this.logger.isTraceEnabled()) {
/* 113 */         this.logger.trace("Resolving multipart request");
/*     */       }
/* 115 */       multipartHttpServletRequest = multipartResolver.resolveMultipart(processedRequest);
/*     */ 
/*     */     
/*     */     }
/* 119 */     else if (this.logger.isTraceEnabled()) {
/* 120 */       this.logger.trace("Not a multipart request");
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 125 */       filterChain.doFilter((ServletRequest)multipartHttpServletRequest, (ServletResponse)response);
/*     */     } finally {
/*     */       
/* 128 */       if (multipartHttpServletRequest instanceof MultipartHttpServletRequest) {
/* 129 */         multipartResolver.cleanupMultipart(multipartHttpServletRequest);
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
/*     */   protected MultipartResolver lookupMultipartResolver(HttpServletRequest request) {
/* 143 */     return lookupMultipartResolver();
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
/*     */   protected MultipartResolver lookupMultipartResolver() {
/* 155 */     WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
/* 156 */     String beanName = getMultipartResolverBeanName();
/* 157 */     if (wac != null && wac.containsBean(beanName)) {
/* 158 */       if (this.logger.isDebugEnabled()) {
/* 159 */         this.logger.debug("Using MultipartResolver '" + beanName + "' for MultipartFilter");
/*     */       }
/* 161 */       return (MultipartResolver)wac.getBean(beanName, MultipartResolver.class);
/*     */     } 
/*     */     
/* 164 */     return this.defaultMultipartResolver;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/support/MultipartFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */