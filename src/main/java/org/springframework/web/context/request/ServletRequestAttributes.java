/*     */ package org.springframework.web.context.request;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.NumberUtils;
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
/*     */ public class ServletRequestAttributes
/*     */   extends AbstractRequestAttributes
/*     */ {
/*  51 */   public static final String DESTRUCTION_CALLBACK_NAME_PREFIX = ServletRequestAttributes.class
/*  52 */     .getName() + ".DESTRUCTION_CALLBACK.";
/*     */   
/*  54 */   protected static final Set<Class<?>> immutableValueTypes = new HashSet<>(16);
/*     */   
/*     */   static {
/*  57 */     immutableValueTypes.addAll(NumberUtils.STANDARD_NUMBER_TYPES);
/*  58 */     immutableValueTypes.add(Boolean.class);
/*  59 */     immutableValueTypes.add(Character.class);
/*  60 */     immutableValueTypes.add(String.class);
/*     */   }
/*     */ 
/*     */   
/*     */   private final HttpServletRequest request;
/*     */   
/*     */   @Nullable
/*     */   private HttpServletResponse response;
/*     */   
/*     */   @Nullable
/*     */   private volatile HttpSession session;
/*     */   
/*  72 */   private final Map<String, Object> sessionAttributesToUpdate = new ConcurrentHashMap<>(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletRequestAttributes(HttpServletRequest request) {
/*  80 */     Assert.notNull(request, "Request must not be null");
/*  81 */     this.request = request;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response) {
/*  90 */     this(request);
/*  91 */     this.response = response;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final HttpServletRequest getRequest() {
/*  99 */     return this.request;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public final HttpServletResponse getResponse() {
/* 107 */     return this.response;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected final HttpSession getSession(boolean allowCreate) {
/* 116 */     if (isRequestActive()) {
/* 117 */       HttpSession httpSession = this.request.getSession(allowCreate);
/* 118 */       this.session = httpSession;
/* 119 */       return httpSession;
/*     */     } 
/*     */ 
/*     */     
/* 123 */     HttpSession session = this.session;
/* 124 */     if (session == null) {
/* 125 */       if (allowCreate) {
/* 126 */         throw new IllegalStateException("No session found and request already completed - cannot create new session!");
/*     */       }
/*     */ 
/*     */       
/* 130 */       session = this.request.getSession(false);
/* 131 */       this.session = session;
/*     */     } 
/*     */     
/* 134 */     return session;
/*     */   }
/*     */ 
/*     */   
/*     */   private HttpSession obtainSession() {
/* 139 */     HttpSession session = getSession(true);
/* 140 */     Assert.state((session != null), "No HttpSession");
/* 141 */     return session;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getAttribute(String name, int scope) {
/* 147 */     if (scope == 0) {
/* 148 */       if (!isRequestActive()) {
/* 149 */         throw new IllegalStateException("Cannot ask for request attribute - request is not active anymore!");
/*     */       }
/*     */       
/* 152 */       return this.request.getAttribute(name);
/*     */     } 
/*     */     
/* 155 */     HttpSession session = getSession(false);
/* 156 */     if (session != null) {
/*     */       try {
/* 158 */         Object value = session.getAttribute(name);
/* 159 */         if (value != null) {
/* 160 */           this.sessionAttributesToUpdate.put(name, value);
/*     */         }
/* 162 */         return value;
/*     */       }
/* 164 */       catch (IllegalStateException illegalStateException) {}
/*     */     }
/*     */ 
/*     */     
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAttribute(String name, Object value, int scope) {
/* 174 */     if (scope == 0) {
/* 175 */       if (!isRequestActive()) {
/* 176 */         throw new IllegalStateException("Cannot set request attribute - request is not active anymore!");
/*     */       }
/*     */       
/* 179 */       this.request.setAttribute(name, value);
/*     */     } else {
/*     */       
/* 182 */       HttpSession session = obtainSession();
/* 183 */       this.sessionAttributesToUpdate.remove(name);
/* 184 */       session.setAttribute(name, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeAttribute(String name, int scope) {
/* 190 */     if (scope == 0) {
/* 191 */       if (isRequestActive()) {
/* 192 */         removeRequestDestructionCallback(name);
/* 193 */         this.request.removeAttribute(name);
/*     */       } 
/*     */     } else {
/*     */       
/* 197 */       HttpSession session = getSession(false);
/* 198 */       if (session != null) {
/* 199 */         this.sessionAttributesToUpdate.remove(name);
/*     */         try {
/* 201 */           session.removeAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name);
/* 202 */           session.removeAttribute(name);
/*     */         }
/* 204 */         catch (IllegalStateException illegalStateException) {}
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getAttributeNames(int scope) {
/* 213 */     if (scope == 0) {
/* 214 */       if (!isRequestActive()) {
/* 215 */         throw new IllegalStateException("Cannot ask for request attributes - request is not active anymore!");
/*     */       }
/*     */       
/* 218 */       return StringUtils.toStringArray(this.request.getAttributeNames());
/*     */     } 
/*     */     
/* 221 */     HttpSession session = getSession(false);
/* 222 */     if (session != null) {
/*     */       try {
/* 224 */         return StringUtils.toStringArray(session.getAttributeNames());
/*     */       }
/* 226 */       catch (IllegalStateException illegalStateException) {}
/*     */     }
/*     */ 
/*     */     
/* 230 */     return new String[0];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerDestructionCallback(String name, Runnable callback, int scope) {
/* 236 */     if (scope == 0) {
/* 237 */       registerRequestDestructionCallback(name, callback);
/*     */     } else {
/*     */       
/* 240 */       registerSessionDestructionCallback(name, callback);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object resolveReference(String key) {
/* 246 */     if ("request".equals(key)) {
/* 247 */       return this.request;
/*     */     }
/* 249 */     if ("session".equals(key)) {
/* 250 */       return getSession(true);
/*     */     }
/*     */     
/* 253 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSessionId() {
/* 259 */     return obtainSession().getId();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getSessionMutex() {
/* 264 */     return WebUtils.getSessionMutex(obtainSession());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void updateAccessedSessionAttributes() {
/* 274 */     if (!this.sessionAttributesToUpdate.isEmpty()) {
/*     */       
/* 276 */       HttpSession session = getSession(false);
/* 277 */       if (session != null) {
/*     */         try {
/* 279 */           for (Map.Entry<String, Object> entry : this.sessionAttributesToUpdate.entrySet()) {
/* 280 */             String name = entry.getKey();
/* 281 */             Object newValue = entry.getValue();
/* 282 */             Object oldValue = session.getAttribute(name);
/* 283 */             if (oldValue == newValue && !isImmutableSessionAttribute(name, newValue)) {
/* 284 */               session.setAttribute(name, newValue);
/*     */             }
/*     */           }
/*     */         
/* 288 */         } catch (IllegalStateException illegalStateException) {}
/*     */       }
/*     */ 
/*     */       
/* 292 */       this.sessionAttributesToUpdate.clear();
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
/*     */ 
/*     */   
/*     */   protected boolean isImmutableSessionAttribute(String name, @Nullable Object value) {
/* 309 */     return (value == null || immutableValueTypes.contains(value.getClass()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void registerSessionDestructionCallback(String name, Runnable callback) {
/* 320 */     HttpSession session = obtainSession();
/* 321 */     session.setAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name, new DestructionCallbackBindingListener(callback));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 328 */     return this.request.toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/ServletRequestAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */