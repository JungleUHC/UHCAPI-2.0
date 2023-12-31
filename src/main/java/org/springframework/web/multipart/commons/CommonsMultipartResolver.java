/*     */ package org.springframework.web.multipart.commons;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.apache.commons.fileupload.FileItem;
/*     */ import org.apache.commons.fileupload.FileItemFactory;
/*     */ import org.apache.commons.fileupload.FileUpload;
/*     */ import org.apache.commons.fileupload.FileUploadBase;
/*     */ import org.apache.commons.fileupload.FileUploadException;
/*     */ import org.apache.commons.fileupload.RequestContext;
/*     */ import org.apache.commons.fileupload.servlet.ServletFileUpload;
/*     */ import org.apache.commons.fileupload.servlet.ServletRequestContext;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.web.context.ServletContextAware;
/*     */ import org.springframework.web.multipart.MaxUploadSizeExceededException;
/*     */ import org.springframework.web.multipart.MultipartException;
/*     */ import org.springframework.web.multipart.MultipartHttpServletRequest;
/*     */ import org.springframework.web.multipart.MultipartResolver;
/*     */ import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;
/*     */ import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
/*     */ import org.springframework.web.util.WebUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommonsMultipartResolver
/*     */   extends CommonsFileUploadSupport
/*     */   implements MultipartResolver, ServletContextAware
/*     */ {
/*     */   private boolean resolveLazily = false;
/*     */   @Nullable
/*     */   private Set<String> supportedMethods;
/*     */   
/*     */   public CommonsMultipartResolver() {}
/*     */   
/*     */   public CommonsMultipartResolver(ServletContext servletContext) {
/* 106 */     this();
/* 107 */     setServletContext(servletContext);
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
/*     */   public void setResolveLazily(boolean resolveLazily) {
/* 120 */     this.resolveLazily = resolveLazily;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSupportedMethods(String... supportedMethods) {
/* 131 */     this.supportedMethods = new HashSet<>(Arrays.asList(supportedMethods));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
/* 142 */     return (FileUpload)new ServletFileUpload(fileItemFactory);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setServletContext(ServletContext servletContext) {
/* 147 */     if (!isUploadTempDirSpecified()) {
/* 148 */       getFileItemFactory().setRepository(WebUtils.getTempDir(servletContext));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMultipart(HttpServletRequest request) {
/* 155 */     return (this.supportedMethods != null) ? ((this.supportedMethods
/* 156 */       .contains(request.getMethod()) && 
/* 157 */       FileUploadBase.isMultipartContent((RequestContext)new ServletRequestContext(request)))) : 
/* 158 */       ServletFileUpload.isMultipartContent(request);
/*     */   }
/*     */ 
/*     */   
/*     */   public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
/* 163 */     Assert.notNull(request, "Request must not be null");
/* 164 */     if (this.resolveLazily) {
/* 165 */       return (MultipartHttpServletRequest)new DefaultMultipartHttpServletRequest(request)
/*     */         {
/*     */           protected void initializeMultipart() {
/* 168 */             CommonsFileUploadSupport.MultipartParsingResult parsingResult = CommonsMultipartResolver.this.parseRequest(request);
/* 169 */             setMultipartFiles(parsingResult.getMultipartFiles());
/* 170 */             setMultipartParameters(parsingResult.getMultipartParameters());
/* 171 */             setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
/*     */           }
/*     */         };
/*     */     }
/*     */     
/* 176 */     CommonsFileUploadSupport.MultipartParsingResult parsingResult = parseRequest(request);
/* 177 */     return (MultipartHttpServletRequest)new DefaultMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(), parsingResult
/* 178 */         .getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CommonsFileUploadSupport.MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
/* 189 */     String encoding = determineEncoding(request);
/* 190 */     FileUpload fileUpload = prepareFileUpload(encoding);
/*     */     try {
/* 192 */       List<FileItem> fileItems = ((ServletFileUpload)fileUpload).parseRequest(request);
/* 193 */       return parseFileItems(fileItems, encoding);
/*     */     }
/* 195 */     catch (org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException ex) {
/* 196 */       throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
/*     */     }
/* 198 */     catch (org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException ex) {
/* 199 */       throw new MaxUploadSizeExceededException(fileUpload.getFileSizeMax(), ex);
/*     */     }
/* 201 */     catch (FileUploadException ex) {
/* 202 */       throw new MultipartException("Failed to parse multipart servlet request", ex);
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
/*     */ 
/*     */   
/*     */   protected String determineEncoding(HttpServletRequest request) {
/* 217 */     String encoding = request.getCharacterEncoding();
/* 218 */     if (encoding == null) {
/* 219 */       encoding = getDefaultEncoding();
/*     */     }
/* 221 */     return encoding;
/*     */   }
/*     */ 
/*     */   
/*     */   public void cleanupMultipart(MultipartHttpServletRequest request) {
/* 226 */     if (!(request instanceof AbstractMultipartHttpServletRequest) || ((AbstractMultipartHttpServletRequest)request)
/* 227 */       .isResolved())
/*     */       try {
/* 229 */         cleanupFileItems(request.getMultiFileMap());
/*     */       }
/* 231 */       catch (Throwable ex) {
/* 232 */         this.logger.warn("Failed to perform multipart cleanup for servlet request", ex);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/commons/CommonsMultipartResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */