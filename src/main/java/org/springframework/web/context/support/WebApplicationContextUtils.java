/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.faces.context.ExternalContext;
/*     */ import javax.faces.context.FacesContext;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.springframework.beans.BeansException;
/*     */ import org.springframework.beans.factory.ObjectFactory;
/*     */ import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
/*     */ import org.springframework.beans.factory.config.Scope;
/*     */ import org.springframework.core.env.MutablePropertySources;
/*     */ import org.springframework.core.env.PropertySource;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.web.context.WebApplicationContext;
/*     */ import org.springframework.web.context.request.RequestAttributes;
/*     */ import org.springframework.web.context.request.RequestContextHolder;
/*     */ import org.springframework.web.context.request.RequestScope;
/*     */ import org.springframework.web.context.request.ServletRequestAttributes;
/*     */ import org.springframework.web.context.request.ServletWebRequest;
/*     */ import org.springframework.web.context.request.SessionScope;
/*     */ import org.springframework.web.context.request.WebRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class WebApplicationContextUtils
/*     */ {
/*  69 */   private static final boolean jsfPresent = ClassUtils.isPresent("javax.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WebApplicationContext getRequiredWebApplicationContext(ServletContext sc) throws IllegalStateException {
/*  83 */     WebApplicationContext wac = getWebApplicationContext(sc);
/*  84 */     if (wac == null) {
/*  85 */       throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
/*     */     }
/*  87 */     return wac;
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
/*     */   @Nullable
/*     */   public static WebApplicationContext getWebApplicationContext(ServletContext sc) {
/* 101 */     return getWebApplicationContext(sc, WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static WebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
/* 112 */     Assert.notNull(sc, "ServletContext must not be null");
/* 113 */     Object attr = sc.getAttribute(attrName);
/* 114 */     if (attr == null) {
/* 115 */       return null;
/*     */     }
/* 117 */     if (attr instanceof RuntimeException) {
/* 118 */       throw (RuntimeException)attr;
/*     */     }
/* 120 */     if (attr instanceof Error) {
/* 121 */       throw (Error)attr;
/*     */     }
/* 123 */     if (attr instanceof Exception) {
/* 124 */       throw new IllegalStateException((Exception)attr);
/*     */     }
/* 126 */     if (!(attr instanceof WebApplicationContext)) {
/* 127 */       throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
/*     */     }
/* 129 */     return (WebApplicationContext)attr;
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
/*     */   @Nullable
/*     */   public static WebApplicationContext findWebApplicationContext(ServletContext sc) {
/* 149 */     WebApplicationContext wac = getWebApplicationContext(sc);
/* 150 */     if (wac == null) {
/* 151 */       Enumeration<String> attrNames = sc.getAttributeNames();
/* 152 */       while (attrNames.hasMoreElements()) {
/* 153 */         String attrName = attrNames.nextElement();
/* 154 */         Object attrValue = sc.getAttribute(attrName);
/* 155 */         if (attrValue instanceof WebApplicationContext) {
/* 156 */           if (wac != null) {
/* 157 */             throw new IllegalStateException("No unique WebApplicationContext found: more than one DispatcherServlet registered with publishContext=true?");
/*     */           }
/*     */           
/* 160 */           wac = (WebApplicationContext)attrValue;
/*     */         } 
/*     */       } 
/*     */     } 
/* 164 */     return wac;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory) {
/* 174 */     registerWebApplicationScopes(beanFactory, null);
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
/*     */   public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory, @Nullable ServletContext sc) {
/* 186 */     beanFactory.registerScope("request", (Scope)new RequestScope());
/* 187 */     beanFactory.registerScope("session", (Scope)new SessionScope());
/* 188 */     if (sc != null) {
/* 189 */       ServletContextScope appScope = new ServletContextScope(sc);
/* 190 */       beanFactory.registerScope("application", appScope);
/*     */       
/* 192 */       sc.setAttribute(ServletContextScope.class.getName(), appScope);
/*     */     } 
/*     */     
/* 195 */     beanFactory.registerResolvableDependency(ServletRequest.class, new RequestObjectFactory());
/* 196 */     beanFactory.registerResolvableDependency(ServletResponse.class, new ResponseObjectFactory());
/* 197 */     beanFactory.registerResolvableDependency(HttpSession.class, new SessionObjectFactory());
/* 198 */     beanFactory.registerResolvableDependency(WebRequest.class, new WebRequestObjectFactory());
/* 199 */     if (jsfPresent) {
/* 200 */       FacesDependencyRegistrar.registerFacesDependencies(beanFactory);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, @Nullable ServletContext sc) {
/* 211 */     registerEnvironmentBeans(bf, sc, null);
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
/*     */   public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, @Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
/* 224 */     if (servletContext != null && !bf.containsBean("servletContext")) {
/* 225 */       bf.registerSingleton("servletContext", servletContext);
/*     */     }
/*     */     
/* 228 */     if (servletConfig != null && !bf.containsBean("servletConfig")) {
/* 229 */       bf.registerSingleton("servletConfig", servletConfig);
/*     */     }
/*     */     
/* 232 */     if (!bf.containsBean("contextParameters")) {
/* 233 */       Map<String, String> parameterMap = new HashMap<>();
/* 234 */       if (servletContext != null) {
/* 235 */         Enumeration<?> paramNameEnum = servletContext.getInitParameterNames();
/* 236 */         while (paramNameEnum.hasMoreElements()) {
/* 237 */           String paramName = (String)paramNameEnum.nextElement();
/* 238 */           parameterMap.put(paramName, servletContext.getInitParameter(paramName));
/*     */         } 
/*     */       } 
/* 241 */       if (servletConfig != null) {
/* 242 */         Enumeration<?> paramNameEnum = servletConfig.getInitParameterNames();
/* 243 */         while (paramNameEnum.hasMoreElements()) {
/* 244 */           String paramName = (String)paramNameEnum.nextElement();
/* 245 */           parameterMap.put(paramName, servletConfig.getInitParameter(paramName));
/*     */         } 
/*     */       } 
/* 248 */       bf.registerSingleton("contextParameters", 
/* 249 */           Collections.unmodifiableMap(parameterMap));
/*     */     } 
/*     */     
/* 252 */     if (!bf.containsBean("contextAttributes")) {
/* 253 */       Map<String, Object> attributeMap = new HashMap<>();
/* 254 */       if (servletContext != null) {
/* 255 */         Enumeration<?> attrNameEnum = servletContext.getAttributeNames();
/* 256 */         while (attrNameEnum.hasMoreElements()) {
/* 257 */           String attrName = (String)attrNameEnum.nextElement();
/* 258 */           attributeMap.put(attrName, servletContext.getAttribute(attrName));
/*     */         } 
/*     */       } 
/* 261 */       bf.registerSingleton("contextAttributes", 
/* 262 */           Collections.unmodifiableMap(attributeMap));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void initServletPropertySources(MutablePropertySources propertySources, ServletContext servletContext) {
/* 273 */     initServletPropertySources(propertySources, servletContext, null);
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
/*     */   
/*     */   public static void initServletPropertySources(MutablePropertySources sources, @Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
/* 297 */     Assert.notNull(sources, "'propertySources' must not be null");
/* 298 */     String name = "servletContextInitParams";
/* 299 */     if (servletContext != null && sources.get(name) instanceof PropertySource.StubPropertySource) {
/* 300 */       sources.replace(name, (PropertySource)new ServletContextPropertySource(name, servletContext));
/*     */     }
/* 302 */     name = "servletConfigInitParams";
/* 303 */     if (servletConfig != null && sources.get(name) instanceof PropertySource.StubPropertySource) {
/* 304 */       sources.replace(name, (PropertySource)new ServletConfigPropertySource(name, servletConfig));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ServletRequestAttributes currentRequestAttributes() {
/* 313 */     RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
/* 314 */     if (!(requestAttr instanceof ServletRequestAttributes)) {
/* 315 */       throw new IllegalStateException("Current request is not a servlet request");
/*     */     }
/* 317 */     return (ServletRequestAttributes)requestAttr;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class RequestObjectFactory
/*     */     implements ObjectFactory<ServletRequest>, Serializable
/*     */   {
/*     */     private RequestObjectFactory() {}
/*     */ 
/*     */     
/*     */     public ServletRequest getObject() {
/* 329 */       return (ServletRequest)WebApplicationContextUtils.currentRequestAttributes().getRequest();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 334 */       return "Current HttpServletRequest";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ResponseObjectFactory
/*     */     implements ObjectFactory<ServletResponse>, Serializable
/*     */   {
/*     */     private ResponseObjectFactory() {}
/*     */ 
/*     */     
/*     */     public ServletResponse getObject() {
/* 347 */       HttpServletResponse httpServletResponse = WebApplicationContextUtils.currentRequestAttributes().getResponse();
/* 348 */       if (httpServletResponse == null) {
/* 349 */         throw new IllegalStateException("Current servlet response not available - consider using RequestContextFilter instead of RequestContextListener");
/*     */       }
/*     */       
/* 352 */       return (ServletResponse)httpServletResponse;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 357 */       return "Current HttpServletResponse";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class SessionObjectFactory
/*     */     implements ObjectFactory<HttpSession>, Serializable
/*     */   {
/*     */     private SessionObjectFactory() {}
/*     */ 
/*     */     
/*     */     public HttpSession getObject() {
/* 370 */       return WebApplicationContextUtils.currentRequestAttributes().getRequest().getSession();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 375 */       return "Current HttpSession";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class WebRequestObjectFactory
/*     */     implements ObjectFactory<WebRequest>, Serializable
/*     */   {
/*     */     private WebRequestObjectFactory() {}
/*     */ 
/*     */     
/*     */     public WebRequest getObject() {
/* 388 */       ServletRequestAttributes requestAttr = WebApplicationContextUtils.currentRequestAttributes();
/* 389 */       return (WebRequest)new ServletWebRequest(requestAttr.getRequest(), requestAttr.getResponse());
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 394 */       return "Current ServletWebRequest";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class FacesDependencyRegistrar
/*     */   {
/*     */     public static void registerFacesDependencies(ConfigurableListableBeanFactory beanFactory) {
/* 405 */       beanFactory.registerResolvableDependency(FacesContext.class, new ObjectFactory<FacesContext>()
/*     */           {
/*     */             public FacesContext getObject() {
/* 408 */               return FacesContext.getCurrentInstance();
/*     */             }
/*     */             
/*     */             public String toString() {
/* 412 */               return "Current JSF FacesContext";
/*     */             }
/*     */           });
/* 415 */       beanFactory.registerResolvableDependency(ExternalContext.class, new ObjectFactory<ExternalContext>()
/*     */           {
/*     */             public ExternalContext getObject() {
/* 418 */               return FacesContext.getCurrentInstance().getExternalContext();
/*     */             }
/*     */             
/*     */             public String toString() {
/* 422 */               return "Current JSF ExternalContext";
/*     */             }
/*     */           });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/WebApplicationContextUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */