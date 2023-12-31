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
/*     */ import org.springframework.util.CollectionUtils;
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
/*     */ public class FormContentFilter
/*     */   extends OncePerRequestFilter
/*     */ {
/*  59 */   private static final List<String> HTTP_METHODS = Arrays.asList(new String[] { "PUT", "PATCH", "DELETE" });
/*     */   
/*  61 */   private FormHttpMessageConverter formConverter = (FormHttpMessageConverter)new AllEncompassingFormHttpMessageConverter();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFormConverter(FormHttpMessageConverter converter) {
/*  69 */     Assert.notNull(converter, "FormHttpMessageConverter is required");
/*  70 */     this.formConverter = converter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCharset(Charset charset) {
/*  79 */     this.formConverter.setCharset(charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*  88 */     MultiValueMap<String, String> params = parseIfNecessary(request);
/*  89 */     if (!CollectionUtils.isEmpty((Map)params)) {
/*  90 */       filterChain.doFilter((ServletRequest)new FormContentRequestWrapper(request, params), (ServletResponse)response);
/*     */     } else {
/*     */       
/*  93 */       filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private MultiValueMap<String, String> parseIfNecessary(final HttpServletRequest request) throws IOException {
/*  99 */     if (!shouldParse(request)) {
/* 100 */       return null;
/*     */     }
/*     */     
/* 103 */     ServletServerHttpRequest servletServerHttpRequest = new ServletServerHttpRequest(request)
/*     */       {
/*     */         public InputStream getBody() throws IOException {
/* 106 */           return (InputStream)request.getInputStream();
/*     */         }
/*     */       };
/* 109 */     return this.formConverter.read(null, (HttpInputMessage)servletServerHttpRequest);
/*     */   }
/*     */   
/*     */   private boolean shouldParse(HttpServletRequest request) {
/* 113 */     String contentType = request.getContentType();
/* 114 */     String method = request.getMethod();
/* 115 */     if (StringUtils.hasLength(contentType) && HTTP_METHODS.contains(method)) {
/*     */       try {
/* 117 */         MediaType mediaType = MediaType.parseMediaType(contentType);
/* 118 */         return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
/*     */       }
/* 120 */       catch (IllegalArgumentException illegalArgumentException) {}
/*     */     }
/*     */     
/* 123 */     return false;
/*     */   }
/*     */   
/*     */   private static class FormContentRequestWrapper
/*     */     extends HttpServletRequestWrapper
/*     */   {
/*     */     private MultiValueMap<String, String> formParams;
/*     */     
/*     */     public FormContentRequestWrapper(HttpServletRequest request, MultiValueMap<String, String> params) {
/* 132 */       super(request);
/* 133 */       this.formParams = params;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String getParameter(String name) {
/* 139 */       String queryStringValue = super.getParameter(name);
/* 140 */       String formValue = (String)this.formParams.getFirst(name);
/* 141 */       return (queryStringValue != null) ? queryStringValue : formValue;
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, String[]> getParameterMap() {
/* 146 */       Map<String, String[]> result = (Map)new LinkedHashMap<>();
/* 147 */       Enumeration<String> names = getParameterNames();
/* 148 */       while (names.hasMoreElements()) {
/* 149 */         String name = names.nextElement();
/* 150 */         result.put(name, getParameterValues(name));
/*     */       } 
/* 152 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Enumeration<String> getParameterNames() {
/* 157 */       Set<String> names = new LinkedHashSet<>();
/* 158 */       names.addAll(Collections.list(super.getParameterNames()));
/* 159 */       names.addAll(this.formParams.keySet());
/* 160 */       return Collections.enumeration(names);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String[] getParameterValues(String name) {
/* 166 */       String[] parameterValues = super.getParameterValues(name);
/* 167 */       List<String> formParam = (List<String>)this.formParams.get(name);
/* 168 */       if (formParam == null) {
/* 169 */         return parameterValues;
/*     */       }
/* 171 */       if (parameterValues == null || getQueryString() == null) {
/* 172 */         return StringUtils.toStringArray(formParam);
/*     */       }
/*     */       
/* 175 */       List<String> result = new ArrayList<>(parameterValues.length + formParam.size());
/* 176 */       result.addAll(Arrays.asList(parameterValues));
/* 177 */       result.addAll(formParam);
/* 178 */       return StringUtils.toStringArray(result);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/FormContentFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */