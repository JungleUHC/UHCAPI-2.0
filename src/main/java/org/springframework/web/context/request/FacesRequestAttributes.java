/*     */ package org.springframework.web.context.request;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import javax.faces.context.ExternalContext;
/*     */ import javax.faces.context.FacesContext;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ import org.springframework.util.StringUtils;
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
/*     */ public class FacesRequestAttributes
/*     */   implements RequestAttributes
/*     */ {
/*  57 */   private static final Log logger = LogFactory.getLog(FacesRequestAttributes.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final FacesContext facesContext;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FacesRequestAttributes(FacesContext facesContext) {
/*  68 */     Assert.notNull(facesContext, "FacesContext must not be null");
/*  69 */     this.facesContext = facesContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final FacesContext getFacesContext() {
/*  77 */     return this.facesContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ExternalContext getExternalContext() {
/*  85 */     return getFacesContext().getExternalContext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, Object> getAttributeMap(int scope) {
/*  96 */     if (scope == 0) {
/*  97 */       return getExternalContext().getRequestMap();
/*     */     }
/*     */     
/* 100 */     return getExternalContext().getSessionMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getAttribute(String name, int scope) {
/* 107 */     return getAttributeMap(scope).get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttribute(String name, Object value, int scope) {
/* 112 */     getAttributeMap(scope).put(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeAttribute(String name, int scope) {
/* 117 */     getAttributeMap(scope).remove(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getAttributeNames(int scope) {
/* 122 */     return StringUtils.toStringArray(getAttributeMap(scope).keySet());
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerDestructionCallback(String name, Runnable callback, int scope) {
/* 127 */     if (logger.isWarnEnabled()) {
/* 128 */       logger.warn("Could not register destruction callback [" + callback + "] for attribute '" + name + "' because FacesRequestAttributes does not support such callbacks");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object resolveReference(String key) {
/* 135 */     if ("request".equals(key)) {
/* 136 */       return getExternalContext().getRequest();
/*     */     }
/* 138 */     if ("session".equals(key)) {
/* 139 */       return getExternalContext().getSession(true);
/*     */     }
/* 141 */     if ("application".equals(key)) {
/* 142 */       return getExternalContext().getContext();
/*     */     }
/* 144 */     if ("requestScope".equals(key)) {
/* 145 */       return getExternalContext().getRequestMap();
/*     */     }
/* 147 */     if ("sessionScope".equals(key)) {
/* 148 */       return getExternalContext().getSessionMap();
/*     */     }
/* 150 */     if ("applicationScope".equals(key)) {
/* 151 */       return getExternalContext().getApplicationMap();
/*     */     }
/* 153 */     if ("facesContext".equals(key)) {
/* 154 */       return getFacesContext();
/*     */     }
/* 156 */     if ("cookie".equals(key)) {
/* 157 */       return getExternalContext().getRequestCookieMap();
/*     */     }
/* 159 */     if ("header".equals(key)) {
/* 160 */       return getExternalContext().getRequestHeaderMap();
/*     */     }
/* 162 */     if ("headerValues".equals(key)) {
/* 163 */       return getExternalContext().getRequestHeaderValuesMap();
/*     */     }
/* 165 */     if ("param".equals(key)) {
/* 166 */       return getExternalContext().getRequestParameterMap();
/*     */     }
/* 168 */     if ("paramValues".equals(key)) {
/* 169 */       return getExternalContext().getRequestParameterValuesMap();
/*     */     }
/* 171 */     if ("initParam".equals(key)) {
/* 172 */       return getExternalContext().getInitParameterMap();
/*     */     }
/* 174 */     if ("view".equals(key)) {
/* 175 */       return getFacesContext().getViewRoot();
/*     */     }
/* 177 */     if ("viewScope".equals(key)) {
/* 178 */       return getFacesContext().getViewRoot().getViewMap();
/*     */     }
/* 180 */     if ("flash".equals(key)) {
/* 181 */       return getExternalContext().getFlash();
/*     */     }
/* 183 */     if ("resource".equals(key)) {
/* 184 */       return getFacesContext().getApplication().getResourceHandler();
/*     */     }
/*     */     
/* 187 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSessionId() {
/* 193 */     Object session = getExternalContext().getSession(true);
/*     */     
/*     */     try {
/* 196 */       Method getIdMethod = session.getClass().getMethod("getId", new Class[0]);
/* 197 */       return String.valueOf(ReflectionUtils.invokeMethod(getIdMethod, session));
/*     */     }
/* 199 */     catch (NoSuchMethodException ex) {
/* 200 */       throw new IllegalStateException("Session object [" + session + "] does not have a getId() method");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getSessionMutex() {
/* 207 */     ExternalContext externalContext = getExternalContext();
/* 208 */     Object session = externalContext.getSession(true);
/* 209 */     Object mutex = externalContext.getSessionMap().get(WebUtils.SESSION_MUTEX_ATTRIBUTE);
/* 210 */     if (mutex == null) {
/* 211 */       mutex = (session != null) ? session : externalContext;
/*     */     }
/* 213 */     return mutex;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/FacesRequestAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */