/*     */ package org.springframework.web.multipart.support;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletRequestWrapper;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.web.multipart.MultipartFile;
/*     */ import org.springframework.web.multipart.MultipartHttpServletRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractMultipartHttpServletRequest
/*     */   extends HttpServletRequestWrapper
/*     */   implements MultipartHttpServletRequest
/*     */ {
/*     */   @Nullable
/*     */   private MultiValueMap<String, MultipartFile> multipartFiles;
/*     */   
/*     */   protected AbstractMultipartHttpServletRequest(HttpServletRequest request) {
/*  56 */     super(request);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServletRequest getRequest() {
/*  62 */     return (HttpServletRequest)super.getRequest();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpMethod getRequestMethod() {
/*  67 */     return HttpMethod.resolve(getRequest().getMethod());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getRequestHeaders() {
/*  72 */     HttpHeaders headers = new HttpHeaders();
/*  73 */     Enumeration<String> headerNames = getHeaderNames();
/*  74 */     while (headerNames.hasMoreElements()) {
/*  75 */       String headerName = headerNames.nextElement();
/*  76 */       headers.put(headerName, Collections.list(getHeaders(headerName)));
/*     */     } 
/*  78 */     return headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> getFileNames() {
/*  83 */     return getMultipartFiles().keySet().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public MultipartFile getFile(String name) {
/*  88 */     return (MultipartFile)getMultipartFiles().getFirst(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MultipartFile> getFiles(String name) {
/*  93 */     List<MultipartFile> multipartFiles = (List<MultipartFile>)getMultipartFiles().get(name);
/*  94 */     if (multipartFiles != null) {
/*  95 */       return multipartFiles;
/*     */     }
/*     */     
/*  98 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, MultipartFile> getFileMap() {
/* 104 */     return getMultipartFiles().toSingleValueMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, MultipartFile> getMultiFileMap() {
/* 109 */     return getMultipartFiles();
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
/*     */   public boolean isResolved() {
/* 121 */     return (this.multipartFiles != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void setMultipartFiles(MultiValueMap<String, MultipartFile> multipartFiles) {
/* 130 */     this
/* 131 */       .multipartFiles = (MultiValueMap<String, MultipartFile>)new LinkedMultiValueMap(Collections.unmodifiableMap((Map<? extends String, ? extends MultipartFile>)multipartFiles));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MultiValueMap<String, MultipartFile> getMultipartFiles() {
/* 140 */     if (this.multipartFiles == null) {
/* 141 */       initializeMultipart();
/*     */     }
/* 143 */     return this.multipartFiles;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initializeMultipart() {
/* 151 */     throw new IllegalStateException("Multipart request not initialized");
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/support/AbstractMultipartHttpServletRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */