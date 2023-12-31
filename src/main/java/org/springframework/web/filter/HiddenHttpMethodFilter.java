/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletRequestWrapper;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HiddenHttpMethodFilter
/*     */   extends OncePerRequestFilter
/*     */ {
/*  60 */   private static final List<String> ALLOWED_METHODS = Collections.unmodifiableList(Arrays.asList(new String[] { HttpMethod.PUT.name(), HttpMethod.DELETE
/*  61 */           .name(), HttpMethod.PATCH.name() }));
/*     */ 
/*     */   
/*     */   public static final String DEFAULT_METHOD_PARAM = "_method";
/*     */   
/*  66 */   private String methodParam = "_method";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMethodParam(String methodParam) {
/*  74 */     Assert.hasText(methodParam, "'methodParam' must not be empty");
/*  75 */     this.methodParam = methodParam;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*     */     HttpMethodRequestWrapper httpMethodRequestWrapper;
/*  82 */     HttpServletRequest requestToUse = request;
/*     */     
/*  84 */     if ("POST".equals(request.getMethod()) && request.getAttribute("javax.servlet.error.exception") == null) {
/*  85 */       String paramValue = request.getParameter(this.methodParam);
/*  86 */       if (StringUtils.hasLength(paramValue)) {
/*  87 */         String method = paramValue.toUpperCase(Locale.ENGLISH);
/*  88 */         if (ALLOWED_METHODS.contains(method)) {
/*  89 */           httpMethodRequestWrapper = new HttpMethodRequestWrapper(request, method);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  94 */     filterChain.doFilter((ServletRequest)httpMethodRequestWrapper, (ServletResponse)response);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class HttpMethodRequestWrapper
/*     */     extends HttpServletRequestWrapper
/*     */   {
/*     */     private final String method;
/*     */ 
/*     */ 
/*     */     
/*     */     public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
/* 107 */       super(request);
/* 108 */       this.method = method;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getMethod() {
/* 113 */       return this.method;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/HiddenHttpMethodFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */