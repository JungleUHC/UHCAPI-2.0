/*     */ package org.springframework.web.bind;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.springframework.beans.MutablePropertyValues;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.validation.BindException;
/*     */ import org.springframework.web.multipart.MultipartFile;
/*     */ import org.springframework.web.multipart.MultipartRequest;
/*     */ import org.springframework.web.multipart.support.StandardServletPartUtils;
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
/*     */ public class ServletRequestDataBinder
/*     */   extends WebDataBinder
/*     */ {
/*     */   public ServletRequestDataBinder(@Nullable Object target) {
/*  74 */     super(target);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletRequestDataBinder(@Nullable Object target, String objectName) {
/*  84 */     super(target, objectName);
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
/*     */   
/*     */   public void bind(ServletRequest request) {
/* 106 */     MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
/* 107 */     MultipartRequest multipartRequest = (MultipartRequest)WebUtils.getNativeRequest(request, MultipartRequest.class);
/* 108 */     if (multipartRequest != null) {
/* 109 */       bindMultipart((Map<String, List<MultipartFile>>)multipartRequest.getMultiFileMap(), mpvs);
/*     */     }
/* 111 */     else if (StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/form-data")) {
/* 112 */       HttpServletRequest httpServletRequest = (HttpServletRequest)WebUtils.getNativeRequest(request, HttpServletRequest.class);
/* 113 */       if (httpServletRequest != null && HttpMethod.POST.matches(httpServletRequest.getMethod())) {
/* 114 */         StandardServletPartUtils.bindParts(httpServletRequest, mpvs, isBindEmptyMultipartFiles());
/*     */       }
/*     */     } 
/* 117 */     addBindValues(mpvs, request);
/* 118 */     doBind(mpvs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeNoCatch() throws ServletRequestBindingException {
/* 138 */     if (getBindingResult().hasErrors())
/* 139 */       throw new ServletRequestBindingException("Errors binding onto object '" + 
/* 140 */           getBindingResult().getObjectName() + "'", new BindException(
/* 141 */             getBindingResult())); 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/ServletRequestDataBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */