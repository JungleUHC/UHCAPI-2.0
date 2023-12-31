/*     */ package org.springframework.web.bind.support;
/*     */ 
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.springframework.beans.MutablePropertyValues;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.validation.BindException;
/*     */ import org.springframework.web.bind.WebDataBinder;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ import org.springframework.web.context.request.WebRequest;
/*     */ import org.springframework.web.multipart.MultipartRequest;
/*     */ import org.springframework.web.multipart.support.StandardServletPartUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebRequestDataBinder
/*     */   extends WebDataBinder
/*     */ {
/*     */   public WebRequestDataBinder(@Nullable Object target) {
/*  78 */     super(target);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebRequestDataBinder(@Nullable Object target, String objectName) {
/*  88 */     super(target, objectName);
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
/*     */   
/*     */   public void bind(WebRequest request) {
/* 111 */     MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
/* 112 */     if (request instanceof NativeWebRequest) {
/* 113 */       NativeWebRequest nativeRequest = (NativeWebRequest)request;
/* 114 */       MultipartRequest multipartRequest = (MultipartRequest)nativeRequest.getNativeRequest(MultipartRequest.class);
/* 115 */       if (multipartRequest != null) {
/* 116 */         bindMultipart((Map)multipartRequest.getMultiFileMap(), mpvs);
/*     */       }
/* 118 */       else if (StringUtils.startsWithIgnoreCase(request
/* 119 */           .getHeader("Content-Type"), "multipart/form-data")) {
/* 120 */         HttpServletRequest servletRequest = (HttpServletRequest)nativeRequest.getNativeRequest(HttpServletRequest.class);
/* 121 */         if (servletRequest != null && HttpMethod.POST.matches(servletRequest.getMethod())) {
/* 122 */           StandardServletPartUtils.bindParts(servletRequest, mpvs, isBindEmptyMultipartFiles());
/*     */         }
/*     */       } 
/*     */     } 
/* 126 */     doBind(mpvs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeNoCatch() throws BindException {
/* 136 */     if (getBindingResult().hasErrors())
/* 137 */       throw new BindException(getBindingResult()); 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/support/WebRequestDataBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */