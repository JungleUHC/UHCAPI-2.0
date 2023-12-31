/*     */ package org.springframework.web.multipart.support;
/*     */ 
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.Part;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.multipart.MultipartException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StandardServletMultipartResolver
/*     */   implements MultipartResolver
/*     */ {
/*     */   private boolean resolveLazily = false;
/*     */   private boolean strictServletCompliance = false;
/*     */   
/*     */   public void setResolveLazily(boolean resolveLazily) {
/*  89 */     this.resolveLazily = resolveLazily;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStrictServletCompliance(boolean strictServletCompliance) {
/* 110 */     this.strictServletCompliance = strictServletCompliance;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMultipart(HttpServletRequest request) {
/* 116 */     return StringUtils.startsWithIgnoreCase(request.getContentType(), this.strictServletCompliance ? "multipart/form-data" : "multipart/");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
/* 122 */     return new StandardMultipartHttpServletRequest(request, this.resolveLazily);
/*     */   }
/*     */ 
/*     */   
/*     */   public void cleanupMultipart(MultipartHttpServletRequest request) {
/* 127 */     if (!(request instanceof AbstractMultipartHttpServletRequest) || ((AbstractMultipartHttpServletRequest)request)
/* 128 */       .isResolved())
/*     */       
/*     */       try {
/*     */         
/* 132 */         for (Part part : request.getParts()) {
/* 133 */           if (request.getFile(part.getName()) != null) {
/* 134 */             part.delete();
/*     */           }
/*     */         }
/*     */       
/* 138 */       } catch (Throwable ex) {
/* 139 */         LogFactory.getLog(getClass()).warn("Failed to perform cleanup of multipart items", ex);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/support/StandardServletMultipartResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */