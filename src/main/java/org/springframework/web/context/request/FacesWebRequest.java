/*     */ package org.springframework.web.context.request;
/*     */ 
/*     */ import java.security.Principal;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import javax.faces.context.ExternalContext;
/*     */ import javax.faces.context.FacesContext;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class FacesWebRequest
/*     */   extends FacesRequestAttributes
/*     */   implements NativeWebRequest
/*     */ {
/*     */   public FacesWebRequest(FacesContext facesContext) {
/*  46 */     super(facesContext);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getNativeRequest() {
/*  52 */     return getExternalContext().getRequest();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getNativeResponse() {
/*  57 */     return getExternalContext().getResponse();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeRequest(@Nullable Class<T> requiredType) {
/*  63 */     if (requiredType != null) {
/*  64 */       Object request = getExternalContext().getRequest();
/*  65 */       if (requiredType.isInstance(request)) {
/*  66 */         return (T)request;
/*     */       }
/*     */     } 
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeResponse(@Nullable Class<T> requiredType) {
/*  75 */     if (requiredType != null) {
/*  76 */       Object response = getExternalContext().getResponse();
/*  77 */       if (requiredType.isInstance(response)) {
/*  78 */         return (T)response;
/*     */       }
/*     */     } 
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getHeader(String headerName) {
/*  88 */     return (String)getExternalContext().getRequestHeaderMap().get(headerName);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String[] getHeaderValues(String headerName) {
/*  94 */     return (String[])getExternalContext().getRequestHeaderValuesMap().get(headerName);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> getHeaderNames() {
/*  99 */     return getExternalContext().getRequestHeaderMap().keySet().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getParameter(String paramName) {
/* 105 */     return (String)getExternalContext().getRequestParameterMap().get(paramName);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> getParameterNames() {
/* 110 */     return getExternalContext().getRequestParameterNames();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String[] getParameterValues(String paramName) {
/* 116 */     return (String[])getExternalContext().getRequestParameterValuesMap().get(paramName);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String[]> getParameterMap() {
/* 121 */     return getExternalContext().getRequestParameterValuesMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public Locale getLocale() {
/* 126 */     return getFacesContext().getExternalContext().getRequestLocale();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContextPath() {
/* 131 */     return getFacesContext().getExternalContext().getRequestContextPath();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getRemoteUser() {
/* 137 */     return getFacesContext().getExternalContext().getRemoteUser();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Principal getUserPrincipal() {
/* 143 */     return getFacesContext().getExternalContext().getUserPrincipal();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUserInRole(String role) {
/* 148 */     return getFacesContext().getExternalContext().isUserInRole(role);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 153 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkNotModified(long lastModifiedTimestamp) {
/* 158 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkNotModified(@Nullable String eTag) {
/* 163 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkNotModified(@Nullable String etag, long lastModifiedTimestamp) {
/* 168 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription(boolean includeClientInfo) {
/* 173 */     ExternalContext externalContext = getExternalContext();
/* 174 */     StringBuilder sb = new StringBuilder();
/* 175 */     sb.append("context=").append(externalContext.getRequestContextPath());
/* 176 */     if (includeClientInfo) {
/* 177 */       Object session = externalContext.getSession(false);
/* 178 */       if (session != null) {
/* 179 */         sb.append(";session=").append(getSessionId());
/*     */       }
/* 181 */       String user = externalContext.getRemoteUser();
/* 182 */       if (StringUtils.hasLength(user)) {
/* 183 */         sb.append(";user=").append(user);
/*     */       }
/*     */     } 
/* 186 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 192 */     return "FacesWebRequest: " + getDescription(true);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/FacesWebRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */