/*     */ package org.springframework.web.multipart.support;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Serializable;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.mail.internet.MimeUtility;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.Part;
/*     */ import org.springframework.http.ContentDisposition;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.FileCopyUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.web.multipart.MaxUploadSizeExceededException;
/*     */ import org.springframework.web.multipart.MultipartException;
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
/*     */ public class StandardMultipartHttpServletRequest
/*     */   extends AbstractMultipartHttpServletRequest
/*     */ {
/*     */   @Nullable
/*     */   private Set<String> multipartParameterNames;
/*     */   
/*     */   public StandardMultipartHttpServletRequest(HttpServletRequest request) throws MultipartException {
/*  72 */     this(request, false);
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
/*     */   public StandardMultipartHttpServletRequest(HttpServletRequest request, boolean lazyParsing) throws MultipartException {
/*  86 */     super(request);
/*  87 */     if (!lazyParsing) {
/*  88 */       parseRequest(request);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseRequest(HttpServletRequest request) {
/*     */     try {
/*  95 */       Collection<Part> parts = request.getParts();
/*  96 */       this.multipartParameterNames = new LinkedHashSet<>(parts.size());
/*  97 */       LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(parts.size());
/*  98 */       for (Part part : parts) {
/*  99 */         String headerValue = part.getHeader("Content-Disposition");
/* 100 */         ContentDisposition disposition = ContentDisposition.parse(headerValue);
/* 101 */         String filename = disposition.getFilename();
/* 102 */         if (filename != null) {
/* 103 */           if (filename.startsWith("=?") && filename.endsWith("?=")) {
/* 104 */             filename = MimeDelegate.decode(filename);
/*     */           }
/* 106 */           linkedMultiValueMap.add(part.getName(), new StandardMultipartFile(part, filename));
/*     */           continue;
/*     */         } 
/* 109 */         this.multipartParameterNames.add(part.getName());
/*     */       } 
/*     */       
/* 112 */       setMultipartFiles((MultiValueMap<String, MultipartFile>)linkedMultiValueMap);
/*     */     }
/* 114 */     catch (Throwable ex) {
/* 115 */       handleParseFailure(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void handleParseFailure(Throwable ex) {
/* 120 */     String msg = ex.getMessage();
/* 121 */     if (msg != null && msg.contains("size") && msg.contains("exceed")) {
/* 122 */       throw new MaxUploadSizeExceededException(-1L, ex);
/*     */     }
/* 124 */     throw new MultipartException("Failed to parse multipart servlet request", ex);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void initializeMultipart() {
/* 129 */     parseRequest(getRequest());
/*     */   }
/*     */ 
/*     */   
/*     */   public Enumeration<String> getParameterNames() {
/* 134 */     if (this.multipartParameterNames == null) {
/* 135 */       initializeMultipart();
/*     */     }
/* 137 */     if (this.multipartParameterNames.isEmpty()) {
/* 138 */       return super.getParameterNames();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 143 */     Set<String> paramNames = new LinkedHashSet<>();
/* 144 */     Enumeration<String> paramEnum = super.getParameterNames();
/* 145 */     while (paramEnum.hasMoreElements()) {
/* 146 */       paramNames.add(paramEnum.nextElement());
/*     */     }
/* 148 */     paramNames.addAll(this.multipartParameterNames);
/* 149 */     return Collections.enumeration(paramNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String[]> getParameterMap() {
/* 154 */     if (this.multipartParameterNames == null) {
/* 155 */       initializeMultipart();
/*     */     }
/* 157 */     if (this.multipartParameterNames.isEmpty()) {
/* 158 */       return super.getParameterMap();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 163 */     Map<String, String[]> paramMap = (Map)new LinkedHashMap<>(super.getParameterMap());
/* 164 */     for (String paramName : this.multipartParameterNames) {
/* 165 */       if (!paramMap.containsKey(paramName)) {
/* 166 */         paramMap.put(paramName, getParameterValues(paramName));
/*     */       }
/*     */     } 
/* 169 */     return paramMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMultipartContentType(String paramOrFileName) {
/*     */     try {
/* 175 */       Part part = getPart(paramOrFileName);
/* 176 */       return (part != null) ? part.getContentType() : null;
/*     */     }
/* 178 */     catch (Throwable ex) {
/* 179 */       throw new MultipartException("Could not access multipart servlet request", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getMultipartHeaders(String paramOrFileName) {
/*     */     try {
/* 186 */       Part part = getPart(paramOrFileName);
/* 187 */       if (part != null) {
/* 188 */         HttpHeaders headers = new HttpHeaders();
/* 189 */         for (String headerName : part.getHeaderNames()) {
/* 190 */           headers.put(headerName, new ArrayList(part.getHeaders(headerName)));
/*     */         }
/* 192 */         return headers;
/*     */       } 
/*     */       
/* 195 */       return null;
/*     */     
/*     */     }
/* 198 */     catch (Throwable ex) {
/* 199 */       throw new MultipartException("Could not access multipart servlet request", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class StandardMultipartFile
/*     */     implements MultipartFile, Serializable
/*     */   {
/*     */     private final Part part;
/*     */ 
/*     */     
/*     */     private final String filename;
/*     */ 
/*     */     
/*     */     public StandardMultipartFile(Part part, String filename) {
/* 215 */       this.part = part;
/* 216 */       this.filename = filename;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getName() {
/* 221 */       return this.part.getName();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getOriginalFilename() {
/* 226 */       return this.filename;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getContentType() {
/* 231 */       return this.part.getContentType();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 236 */       return (this.part.getSize() == 0L);
/*     */     }
/*     */ 
/*     */     
/*     */     public long getSize() {
/* 241 */       return this.part.getSize();
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] getBytes() throws IOException {
/* 246 */       return FileCopyUtils.copyToByteArray(this.part.getInputStream());
/*     */     }
/*     */ 
/*     */     
/*     */     public InputStream getInputStream() throws IOException {
/* 251 */       return this.part.getInputStream();
/*     */     }
/*     */ 
/*     */     
/*     */     public void transferTo(File dest) throws IOException, IllegalStateException {
/* 256 */       this.part.write(dest.getPath());
/* 257 */       if (dest.isAbsolute() && !dest.exists())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 264 */         FileCopyUtils.copy(this.part.getInputStream(), Files.newOutputStream(dest.toPath(), new java.nio.file.OpenOption[0]));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void transferTo(Path dest) throws IOException, IllegalStateException {
/* 270 */       FileCopyUtils.copy(this.part.getInputStream(), Files.newOutputStream(dest, new java.nio.file.OpenOption[0]));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class MimeDelegate
/*     */   {
/*     */     public static String decode(String value) {
/*     */       try {
/* 282 */         return MimeUtility.decodeText(value);
/*     */       }
/* 284 */       catch (UnsupportedEncodingException ex) {
/* 285 */         throw new IllegalStateException(ex);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/support/StandardMultipartHttpServletRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */