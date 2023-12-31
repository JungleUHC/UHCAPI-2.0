/*     */ package org.springframework.web.context;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.context.ApplicationContextException;
/*     */ import org.springframework.context.ApplicationContextInitializer;
/*     */ import org.springframework.context.ConfigurableApplicationContext;
/*     */ import org.springframework.core.GenericTypeResolver;
/*     */ import org.springframework.core.annotation.AnnotationAwareOrderComparator;
/*     */ import org.springframework.core.env.ConfigurableEnvironment;
/*     */ import org.springframework.core.io.ClassPathResource;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.support.PropertiesLoaderUtils;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.ObjectUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContextLoader
/*     */ {
/*     */   public static final String CONTEXT_ID_PARAM = "contextId";
/*     */   public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
/*     */   public static final String CONTEXT_CLASS_PARAM = "contextClass";
/*     */   public static final String CONTEXT_INITIALIZER_CLASSES_PARAM = "contextInitializerClasses";
/*     */   public static final String GLOBAL_INITIALIZER_CLASSES_PARAM = "globalInitializerClasses";
/*     */   private static final String INIT_PARAM_DELIMITERS = ",; \t\n";
/*     */   private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";
/*     */   private static final Properties defaultStrategies;
/*     */   
/*     */   static {
/*     */     try {
/* 143 */       ClassPathResource resource = new ClassPathResource("ContextLoader.properties", ContextLoader.class);
/* 144 */       defaultStrategies = PropertiesLoaderUtils.loadProperties((Resource)resource);
/*     */     }
/* 146 */     catch (IOException ex) {
/* 147 */       throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 155 */   private static final Map<ClassLoader, WebApplicationContext> currentContextPerThread = new ConcurrentHashMap<>(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static volatile WebApplicationContext currentContext;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private WebApplicationContext context;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 173 */   private final List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContextLoader(WebApplicationContext context) {
/* 229 */     this.context = context;
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
/*     */   public void setContextInitializers(@Nullable ApplicationContextInitializer<?>... initializers) {
/* 242 */     if (initializers != null) {
/* 243 */       for (ApplicationContextInitializer<?> initializer : initializers) {
/* 244 */         this.contextInitializers.add(initializer);
/*     */       }
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
/*     */   public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
/* 262 */     if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
/* 263 */       throw new IllegalStateException("Cannot initialize context because there is already a root application context present - check whether you have multiple ContextLoader* definitions in your web.xml!");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 268 */     servletContext.log("Initializing Spring root WebApplicationContext");
/* 269 */     Log logger = LogFactory.getLog(ContextLoader.class);
/* 270 */     if (logger.isInfoEnabled()) {
/* 271 */       logger.info("Root WebApplicationContext: initialization started");
/*     */     }
/* 273 */     long startTime = System.currentTimeMillis();
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 278 */       if (this.context == null) {
/* 279 */         this.context = createWebApplicationContext(servletContext);
/*     */       }
/* 281 */       if (this.context instanceof ConfigurableWebApplicationContext) {
/* 282 */         ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext)this.context;
/* 283 */         if (!cwac.isActive()) {
/*     */ 
/*     */           
/* 286 */           if (cwac.getParent() == null) {
/*     */ 
/*     */             
/* 289 */             ApplicationContext parent = loadParentContext(servletContext);
/* 290 */             cwac.setParent(parent);
/*     */           } 
/* 292 */           configureAndRefreshWebApplicationContext(cwac, servletContext);
/*     */         } 
/*     */       } 
/* 295 */       servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
/*     */       
/* 297 */       ClassLoader ccl = Thread.currentThread().getContextClassLoader();
/* 298 */       if (ccl == ContextLoader.class.getClassLoader()) {
/* 299 */         currentContext = this.context;
/*     */       }
/* 301 */       else if (ccl != null) {
/* 302 */         currentContextPerThread.put(ccl, this.context);
/*     */       } 
/*     */       
/* 305 */       if (logger.isInfoEnabled()) {
/* 306 */         long elapsedTime = System.currentTimeMillis() - startTime;
/* 307 */         logger.info("Root WebApplicationContext initialized in " + elapsedTime + " ms");
/*     */       } 
/*     */       
/* 310 */       return this.context;
/*     */     }
/* 312 */     catch (RuntimeException|Error ex) {
/* 313 */       logger.error("Context initialization failed", ex);
/* 314 */       servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
/* 315 */       throw ex;
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
/*     */   protected WebApplicationContext createWebApplicationContext(ServletContext sc) {
/* 332 */     Class<?> contextClass = determineContextClass(sc);
/* 333 */     if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
/* 334 */       throw new ApplicationContextException("Custom context class [" + contextClass.getName() + "] is not of type [" + ConfigurableWebApplicationContext.class
/* 335 */           .getName() + "]");
/*     */     }
/* 337 */     return (ConfigurableWebApplicationContext)BeanUtils.instantiateClass(contextClass);
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
/*     */   protected Class<?> determineContextClass(ServletContext servletContext) {
/* 349 */     String contextClassName = servletContext.getInitParameter("contextClass");
/* 350 */     if (contextClassName != null) {
/*     */       try {
/* 352 */         return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
/*     */       }
/* 354 */       catch (ClassNotFoundException ex) {
/* 355 */         throw new ApplicationContextException("Failed to load custom context class [" + contextClassName + "]", ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 360 */     contextClassName = defaultStrategies.getProperty(WebApplicationContext.class.getName());
/*     */     try {
/* 362 */       return ClassUtils.forName(contextClassName, ContextLoader.class.getClassLoader());
/*     */     }
/* 364 */     catch (ClassNotFoundException ex) {
/* 365 */       throw new ApplicationContextException("Failed to load default context class [" + contextClassName + "]", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac, ServletContext sc) {
/* 372 */     if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
/*     */ 
/*     */       
/* 375 */       String idParam = sc.getInitParameter("contextId");
/* 376 */       if (idParam != null) {
/* 377 */         wac.setId(idParam);
/*     */       }
/*     */       else {
/*     */         
/* 381 */         wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + 
/* 382 */             ObjectUtils.getDisplayString(sc.getContextPath()));
/*     */       } 
/*     */     } 
/*     */     
/* 386 */     wac.setServletContext(sc);
/* 387 */     String configLocationParam = sc.getInitParameter("contextConfigLocation");
/* 388 */     if (configLocationParam != null) {
/* 389 */       wac.setConfigLocation(configLocationParam);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 395 */     ConfigurableEnvironment env = wac.getEnvironment();
/* 396 */     if (env instanceof ConfigurableWebEnvironment) {
/* 397 */       ((ConfigurableWebEnvironment)env).initPropertySources(sc, null);
/*     */     }
/*     */     
/* 400 */     customizeContext(sc, wac);
/* 401 */     wac.refresh();
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
/*     */   protected void customizeContext(ServletContext sc, ConfigurableWebApplicationContext wac) {
/* 423 */     List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> initializerClasses = determineContextInitializerClasses(sc);
/*     */     
/* 425 */     for (Class<ApplicationContextInitializer<ConfigurableApplicationContext>> initializerClass : initializerClasses) {
/*     */       
/* 427 */       Class<?> initializerContextClass = GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
/* 428 */       if (initializerContextClass != null && !initializerContextClass.isInstance(wac)) {
/* 429 */         throw new ApplicationContextException(String.format("Could not apply context initializer [%s] since its generic parameter [%s] is not assignable from the type of application context used by this context loader: [%s]", new Object[] { initializerClass
/*     */ 
/*     */                 
/* 432 */                 .getName(), initializerContextClass.getName(), wac
/* 433 */                 .getClass().getName() }));
/*     */       }
/* 435 */       this.contextInitializers.add(BeanUtils.instantiateClass(initializerClass));
/*     */     } 
/*     */     
/* 438 */     AnnotationAwareOrderComparator.sort(this.contextInitializers);
/* 439 */     for (ApplicationContextInitializer<ConfigurableApplicationContext> initializer : this.contextInitializers) {
/* 440 */       initializer.initialize(wac);
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
/*     */   protected List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> determineContextInitializerClasses(ServletContext servletContext) {
/* 453 */     List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> classes = new ArrayList<>();
/*     */ 
/*     */     
/* 456 */     String globalClassNames = servletContext.getInitParameter("globalInitializerClasses");
/* 457 */     if (globalClassNames != null) {
/* 458 */       for (String className : StringUtils.tokenizeToStringArray(globalClassNames, ",; \t\n")) {
/* 459 */         classes.add(loadInitializerClass(className));
/*     */       }
/*     */     }
/*     */     
/* 463 */     String localClassNames = servletContext.getInitParameter("contextInitializerClasses");
/* 464 */     if (localClassNames != null) {
/* 465 */       for (String className : StringUtils.tokenizeToStringArray(localClassNames, ",; \t\n")) {
/* 466 */         classes.add(loadInitializerClass(className));
/*     */       }
/*     */     }
/*     */     
/* 470 */     return classes;
/*     */   }
/*     */ 
/*     */   
/*     */   private Class<ApplicationContextInitializer<ConfigurableApplicationContext>> loadInitializerClass(String className) {
/*     */     try {
/* 476 */       Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
/* 477 */       if (!ApplicationContextInitializer.class.isAssignableFrom(clazz)) {
/* 478 */         throw new ApplicationContextException("Initializer class does not implement ApplicationContextInitializer interface: " + clazz);
/*     */       }
/*     */       
/* 481 */       return (Class)clazz;
/*     */     }
/* 483 */     catch (ClassNotFoundException ex) {
/* 484 */       throw new ApplicationContextException("Failed to load context initializer class [" + className + "]", ex);
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
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected ApplicationContext loadParentContext(ServletContext servletContext) {
/* 504 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeWebApplicationContext(ServletContext servletContext) {
/* 514 */     servletContext.log("Closing Spring root WebApplicationContext");
/*     */     try {
/* 516 */       if (this.context instanceof ConfigurableWebApplicationContext) {
/* 517 */         ((ConfigurableWebApplicationContext)this.context).close();
/*     */       }
/*     */     } finally {
/*     */       
/* 521 */       ClassLoader ccl = Thread.currentThread().getContextClassLoader();
/* 522 */       if (ccl == ContextLoader.class.getClassLoader()) {
/* 523 */         currentContext = null;
/*     */       }
/* 525 */       else if (ccl != null) {
/* 526 */         currentContextPerThread.remove(ccl);
/*     */       } 
/* 528 */       servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
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
/*     */   @Nullable
/*     */   public static WebApplicationContext getCurrentWebApplicationContext() {
/* 543 */     ClassLoader ccl = Thread.currentThread().getContextClassLoader();
/* 544 */     if (ccl != null) {
/* 545 */       WebApplicationContext ccpt = currentContextPerThread.get(ccl);
/* 546 */       if (ccpt != null) {
/* 547 */         return ccpt;
/*     */       }
/*     */     } 
/* 550 */     return currentContext;
/*     */   }
/*     */   
/*     */   public ContextLoader() {}
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/ContextLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */