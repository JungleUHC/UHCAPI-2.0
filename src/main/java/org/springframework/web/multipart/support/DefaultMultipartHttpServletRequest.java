/*     */ package org.springframework.web.multipart.support;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.web.multipart.MultipartFile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultMultipartHttpServletRequest
/*     */   extends AbstractMultipartHttpServletRequest
/*     */ {
/*     */   private static final String CONTENT_TYPE = "Content-Type";
/*     */   @Nullable
/*     */   private Map<String, String[]> multipartParameters;
/*     */   @Nullable
/*     */   private Map<String, String> multipartParameterContentTypes;
/*     */   
/*     */   public DefaultMultipartHttpServletRequest(HttpServletRequest request, MultiValueMap<String, MultipartFile> mpFiles, Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {
/*  67 */     super(request);
/*  68 */     setMultipartFiles(mpFiles);
/*  69 */     setMultipartParameters(mpParams);
/*  70 */     setMultipartParameterContentTypes(mpParamContentTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultMultipartHttpServletRequest(HttpServletRequest request) {
/*  78 */     super(request);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getParameter(String name) {
/*  85 */     String[] values = getMultipartParameters().get(name);
/*  86 */     if (values != null) {
/*  87 */       return (values.length > 0) ? values[0] : null;
/*     */     }
/*  89 */     return super.getParameter(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getParameterValues(String name) {
/*  94 */     String[] parameterValues = super.getParameterValues(name);
/*  95 */     String[] mpValues = getMultipartParameters().get(name);
/*  96 */     if (mpValues == null) {
/*  97 */       return parameterValues;
/*     */     }
/*  99 */     if (parameterValues == null || getQueryString() == null) {
/* 100 */       return mpValues;
/*     */     }
/*     */     
/* 103 */     String[] result = new String[mpValues.length + parameterValues.length];
/* 104 */     System.arraycopy(mpValues, 0, result, 0, mpValues.length);
/* 105 */     System.arraycopy(parameterValues, 0, result, mpValues.length, parameterValues.length);
/* 106 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration<String> getParameterNames() {
/* 112 */     Map<String, String[]> multipartParameters = getMultipartParameters();
/* 113 */     if (multipartParameters.isEmpty()) {
/* 114 */       return super.getParameterNames();
/*     */     }
/*     */     
/* 117 */     Set<String> paramNames = new LinkedHashSet<>();
/* 118 */     paramNames.addAll(Collections.list(super.getParameterNames()));
/* 119 */     paramNames.addAll(multipartParameters.keySet());
/* 120 */     return Collections.enumeration(paramNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String[]> getParameterMap() {
/* 125 */     Map<String, String[]> result = (Map)new LinkedHashMap<>();
/* 126 */     Enumeration<String> names = getParameterNames();
/* 127 */     while (names.hasMoreElements()) {
/* 128 */       String name = names.nextElement();
/* 129 */       result.put(name, getParameterValues(name));
/*     */     } 
/* 131 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMultipartContentType(String paramOrFileName) {
/* 136 */     MultipartFile file = getFile(paramOrFileName);
/* 137 */     if (file != null) {
/* 138 */       return file.getContentType();
/*     */     }
/*     */     
/* 141 */     return getMultipartParameterContentTypes().get(paramOrFileName);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHeaders getMultipartHeaders(String paramOrFileName) {
/* 147 */     String contentType = getMultipartContentType(paramOrFileName);
/* 148 */     if (contentType != null) {
/* 149 */       HttpHeaders headers = new HttpHeaders();
/* 150 */       headers.add("Content-Type", contentType);
/* 151 */       return headers;
/*     */     } 
/*     */     
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void setMultipartParameters(Map<String, String[]> multipartParameters) {
/* 164 */     this.multipartParameters = multipartParameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, String[]> getMultipartParameters() {
/* 173 */     if (this.multipartParameters == null) {
/* 174 */       initializeMultipart();
/*     */     }
/* 176 */     return this.multipartParameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void setMultipartParameterContentTypes(Map<String, String> multipartParameterContentTypes) {
/* 184 */     this.multipartParameterContentTypes = multipartParameterContentTypes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, String> getMultipartParameterContentTypes() {
/* 193 */     if (this.multipartParameterContentTypes == null) {
/* 194 */       initializeMultipart();
/*     */     }
/* 196 */     return this.multipartParameterContentTypes;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/support/DefaultMultipartHttpServletRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */