/*     */ package org.springframework.web.multipart.support;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.Part;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.server.ServletServerHttpRequest;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.web.multipart.MultipartException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RequestPartServletServerHttpRequest
/*     */   extends ServletServerHttpRequest
/*     */ {
/*     */   private final MultipartHttpServletRequest multipartRequest;
/*     */   private final String requestPartName;
/*     */   private final HttpHeaders multipartHeaders;
/*     */   
/*     */   public RequestPartServletServerHttpRequest(HttpServletRequest request, String requestPartName) throws MissingServletRequestPartException {
/*  66 */     super(request);
/*     */     
/*  68 */     this.multipartRequest = MultipartResolutionDelegate.asMultipartHttpServletRequest(request);
/*  69 */     this.requestPartName = requestPartName;
/*     */     
/*  71 */     HttpHeaders multipartHeaders = this.multipartRequest.getMultipartHeaders(requestPartName);
/*  72 */     if (multipartHeaders == null) {
/*  73 */       throw new MissingServletRequestPartException(requestPartName);
/*     */     }
/*  75 */     this.multipartHeaders = multipartHeaders;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/*  81 */     return this.multipartHeaders;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getBody() throws IOException {
/*  87 */     boolean servletParts = this.multipartRequest instanceof StandardMultipartHttpServletRequest;
/*  88 */     if (servletParts) {
/*  89 */       Part part = retrieveServletPart();
/*  90 */       if (part != null) {
/*  91 */         return part.getInputStream();
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  96 */     MultipartFile file = this.multipartRequest.getFile(this.requestPartName);
/*  97 */     if (file != null) {
/*  98 */       return file.getInputStream();
/*     */     }
/* 100 */     String paramValue = this.multipartRequest.getParameter(this.requestPartName);
/* 101 */     if (paramValue != null) {
/* 102 */       return new ByteArrayInputStream(paramValue.getBytes(determineCharset()));
/*     */     }
/*     */ 
/*     */     
/* 106 */     if (!servletParts) {
/* 107 */       Part part = retrieveServletPart();
/* 108 */       if (part != null) {
/* 109 */         return part.getInputStream();
/*     */       }
/*     */     } 
/*     */     
/* 113 */     throw new IllegalStateException("No body available for request part '" + this.requestPartName + "'");
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private Part retrieveServletPart() {
/*     */     try {
/* 119 */       return this.multipartRequest.getPart(this.requestPartName);
/*     */     }
/* 121 */     catch (Exception ex) {
/* 122 */       throw new MultipartException("Failed to retrieve request part '" + this.requestPartName + "'", ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Charset determineCharset() {
/* 127 */     MediaType contentType = getHeaders().getContentType();
/* 128 */     if (contentType != null) {
/* 129 */       Charset charset = contentType.getCharset();
/* 130 */       if (charset != null) {
/* 131 */         return charset;
/*     */       }
/*     */     } 
/* 134 */     String encoding = this.multipartRequest.getCharacterEncoding();
/* 135 */     return (encoding != null) ? Charset.forName(encoding) : FORM_CHARSET;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/support/RequestPartServletServerHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */