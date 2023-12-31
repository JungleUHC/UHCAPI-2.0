/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletRequestWrapper;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.converter.FormHttpMessageConverter;
/*     */ import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
/*     */ import org.springframework.http.server.ServletServerHttpRequest;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ @Deprecated
/*     */ public class HttpPutFormContentFilter
/*     */   extends OncePerRequestFilter
/*     */ {
/*  68 */   private FormHttpMessageConverter formConverter = (FormHttpMessageConverter)new AllEncompassingFormHttpMessageConverter();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFormConverter(FormHttpMessageConverter converter) {
/*  76 */     Assert.notNull(converter, "FormHttpMessageConverter is required.");
/*  77 */     this.formConverter = converter;
/*     */   }
/*     */   
/*     */   public FormHttpMessageConverter getFormConverter() {
/*  81 */     return this.formConverter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCharset(Charset charset) {
/*  90 */     this.formConverter.setCharset(charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFilterInternal(final HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*  98 */     if (("PUT".equals(request.getMethod()) || "PATCH".equals(request.getMethod())) && isFormContentType(request)) {
/*  99 */       ServletServerHttpRequest servletServerHttpRequest = new ServletServerHttpRequest(request)
/*     */         {
/*     */           public InputStream getBody() throws IOException {
/* 102 */             return (InputStream)request.getInputStream();
/*     */           }
/*     */         };
/* 105 */       MultiValueMap<String, String> formParameters = this.formConverter.read(null, (HttpInputMessage)servletServerHttpRequest);
/* 106 */       if (!formParameters.isEmpty()) {
/* 107 */         HttpPutFormContentRequestWrapper httpPutFormContentRequestWrapper = new HttpPutFormContentRequestWrapper(request, formParameters);
/* 108 */         filterChain.doFilter((ServletRequest)httpPutFormContentRequestWrapper, (ServletResponse)response);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 113 */     filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
/*     */   }
/*     */   
/*     */   private boolean isFormContentType(HttpServletRequest request) {
/* 117 */     String contentType = request.getContentType();
/* 118 */     if (contentType != null) {
/*     */       try {
/* 120 */         MediaType mediaType = MediaType.parseMediaType(contentType);
/* 121 */         return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
/*     */       }
/* 123 */       catch (IllegalArgumentException ex) {
/* 124 */         return false;
/*     */       } 
/*     */     }
/*     */     
/* 128 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class HttpPutFormContentRequestWrapper
/*     */     extends HttpServletRequestWrapper
/*     */   {
/*     */     private MultiValueMap<String, String> formParameters;
/*     */     
/*     */     public HttpPutFormContentRequestWrapper(HttpServletRequest request, MultiValueMap<String, String> parameters) {
/* 138 */       super(request);
/* 139 */       this.formParameters = parameters;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String getParameter(String name) {
/* 145 */       String queryStringValue = super.getParameter(name);
/* 146 */       String formValue = (String)this.formParameters.getFirst(name);
/* 147 */       return (queryStringValue != null) ? queryStringValue : formValue;
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, String[]> getParameterMap() {
/* 152 */       Map<String, String[]> result = (Map)new LinkedHashMap<>();
/* 153 */       Enumeration<String> names = getParameterNames();
/* 154 */       while (names.hasMoreElements()) {
/* 155 */         String name = names.nextElement();
/* 156 */         result.put(name, getParameterValues(name));
/*     */       } 
/* 158 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Enumeration<String> getParameterNames() {
/* 163 */       Set<String> names = new LinkedHashSet<>();
/* 164 */       names.addAll(Collections.list(super.getParameterNames()));
/* 165 */       names.addAll(this.formParameters.keySet());
/* 166 */       return Collections.enumeration(names);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String[] getParameterValues(String name) {
/* 172 */       String[] parameterValues = super.getParameterValues(name);
/* 173 */       List<String> formParam = (List<String>)this.formParameters.get(name);
/* 174 */       if (formParam == null) {
/* 175 */         return parameterValues;
/*     */       }
/* 177 */       if (parameterValues == null || getQueryString() == null) {
/* 178 */         return StringUtils.toStringArray(formParam);
/*     */       }
/*     */       
/* 181 */       List<String> result = new ArrayList<>(parameterValues.length + formParam.size());
/* 182 */       result.addAll(Arrays.asList(parameterValues));
/* 183 */       result.addAll(formParam);
/* 184 */       return StringUtils.toStringArray(result);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/HttpPutFormContentFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */