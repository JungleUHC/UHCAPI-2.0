/*     */ package org.springframework.web.server.adapter;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.stream.Collectors;
/*     */ import org.springframework.beans.factory.NoSuchBeanDefinitionException;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.http.codec.ServerCodecConfigurer;
/*     */ import org.springframework.http.server.reactive.HttpHandler;
/*     */ import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.web.server.WebExceptionHandler;
/*     */ import org.springframework.web.server.WebFilter;
/*     */ import org.springframework.web.server.WebHandler;
/*     */ import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
/*     */ import org.springframework.web.server.handler.FilteringWebHandler;
/*     */ import org.springframework.web.server.i18n.LocaleContextResolver;
/*     */ import org.springframework.web.server.session.WebSessionManager;
/*     */ import reactor.blockhound.BlockHound;
/*     */ import reactor.blockhound.integration.BlockHoundIntegration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class WebHttpHandlerBuilder
/*     */ {
/*     */   public static final String WEB_HANDLER_BEAN_NAME = "webHandler";
/*     */   public static final String WEB_SESSION_MANAGER_BEAN_NAME = "webSessionManager";
/*     */   public static final String SERVER_CODEC_CONFIGURER_BEAN_NAME = "serverCodecConfigurer";
/*     */   public static final String LOCALE_CONTEXT_RESOLVER_BEAN_NAME = "localeContextResolver";
/*     */   public static final String FORWARDED_HEADER_TRANSFORMER_BEAN_NAME = "forwardedHeaderTransformer";
/*     */   private final WebHandler webHandler;
/*     */   @Nullable
/*     */   private final ApplicationContext applicationContext;
/*  89 */   private final List<WebFilter> filters = new ArrayList<>();
/*     */   
/*  91 */   private final List<WebExceptionHandler> exceptionHandlers = new ArrayList<>();
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Function<HttpHandler, HttpHandler> httpHandlerDecorator;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private WebSessionManager sessionManager;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ServerCodecConfigurer codecConfigurer;
/*     */   
/*     */   @Nullable
/*     */   private LocaleContextResolver localeContextResolver;
/*     */   
/*     */   @Nullable
/*     */   private ForwardedHeaderTransformer forwardedHeaderTransformer;
/*     */ 
/*     */   
/*     */   private WebHttpHandlerBuilder(WebHandler webHandler, @Nullable ApplicationContext applicationContext) {
/* 113 */     Assert.notNull(webHandler, "WebHandler must not be null");
/* 114 */     this.webHandler = webHandler;
/* 115 */     this.applicationContext = applicationContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private WebHttpHandlerBuilder(WebHttpHandlerBuilder other) {
/* 122 */     this.webHandler = other.webHandler;
/* 123 */     this.applicationContext = other.applicationContext;
/* 124 */     this.filters.addAll(other.filters);
/* 125 */     this.exceptionHandlers.addAll(other.exceptionHandlers);
/* 126 */     this.sessionManager = other.sessionManager;
/* 127 */     this.codecConfigurer = other.codecConfigurer;
/* 128 */     this.localeContextResolver = other.localeContextResolver;
/* 129 */     this.forwardedHeaderTransformer = other.forwardedHeaderTransformer;
/* 130 */     this.httpHandlerDecorator = other.httpHandlerDecorator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WebHttpHandlerBuilder webHandler(WebHandler webHandler) {
/* 140 */     return new WebHttpHandlerBuilder(webHandler, null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WebHttpHandlerBuilder applicationContext(ApplicationContext context) {
/* 168 */     WebHttpHandlerBuilder builder = new WebHttpHandlerBuilder((WebHandler)context.getBean("webHandler", WebHandler.class), context);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 173 */     List<WebFilter> webFilters = (List<WebFilter>)context.getBeanProvider(WebFilter.class).orderedStream().collect(Collectors.toList());
/* 174 */     builder.filters(filters -> filters.addAll(webFilters));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 179 */     List<WebExceptionHandler> exceptionHandlers = (List<WebExceptionHandler>)context.getBeanProvider(WebExceptionHandler.class).orderedStream().collect(Collectors.toList());
/* 180 */     builder.exceptionHandlers(handlers -> handlers.addAll(exceptionHandlers));
/*     */     
/* 182 */     context.getBeanProvider(HttpHandlerDecoratorFactory.class)
/* 183 */       .orderedStream()
/* 184 */       .forEach(builder::httpHandlerDecorator);
/*     */     
/*     */     try {
/* 187 */       builder.sessionManager((WebSessionManager)context
/* 188 */           .getBean("webSessionManager", WebSessionManager.class));
/*     */     }
/* 190 */     catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 195 */       builder.codecConfigurer((ServerCodecConfigurer)context
/* 196 */           .getBean("serverCodecConfigurer", ServerCodecConfigurer.class));
/*     */     }
/* 198 */     catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 203 */       builder.localeContextResolver((LocaleContextResolver)context
/* 204 */           .getBean("localeContextResolver", LocaleContextResolver.class));
/*     */     }
/* 206 */     catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 211 */       builder.forwardedHeaderTransformer((ForwardedHeaderTransformer)context
/* 212 */           .getBean("forwardedHeaderTransformer", ForwardedHeaderTransformer.class));
/*     */     }
/* 214 */     catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
/*     */ 
/*     */ 
/*     */     
/* 218 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebHttpHandlerBuilder filter(WebFilter... filters) {
/* 227 */     if (!ObjectUtils.isEmpty((Object[])filters)) {
/* 228 */       this.filters.addAll(Arrays.asList(filters));
/* 229 */       updateFilters();
/*     */     } 
/* 231 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebHttpHandlerBuilder filters(Consumer<List<WebFilter>> consumer) {
/* 239 */     consumer.accept(this.filters);
/* 240 */     updateFilters();
/* 241 */     return this;
/*     */   }
/*     */   
/*     */   private void updateFilters() {
/* 245 */     if (this.filters.isEmpty()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 256 */     List<WebFilter> filtersToUse = (List<WebFilter>)this.filters.stream().peek(filter -> { if (filter instanceof ForwardedHeaderTransformer && this.forwardedHeaderTransformer == null) this.forwardedHeaderTransformer = (ForwardedHeaderTransformer)filter;  }).filter(filter -> !(filter instanceof ForwardedHeaderTransformer)).collect(Collectors.toList());
/*     */     
/* 258 */     this.filters.clear();
/* 259 */     this.filters.addAll(filtersToUse);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebHttpHandlerBuilder exceptionHandler(WebExceptionHandler... handlers) {
/* 267 */     if (!ObjectUtils.isEmpty((Object[])handlers)) {
/* 268 */       this.exceptionHandlers.addAll(Arrays.asList(handlers));
/*     */     }
/* 270 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebHttpHandlerBuilder exceptionHandlers(Consumer<List<WebExceptionHandler>> consumer) {
/* 278 */     consumer.accept(this.exceptionHandlers);
/* 279 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebHttpHandlerBuilder sessionManager(WebSessionManager manager) {
/* 290 */     this.sessionManager = manager;
/* 291 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasSessionManager() {
/* 300 */     return (this.sessionManager != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebHttpHandlerBuilder codecConfigurer(ServerCodecConfigurer codecConfigurer) {
/* 308 */     this.codecConfigurer = codecConfigurer;
/* 309 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasCodecConfigurer() {
/* 319 */     return (this.codecConfigurer != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebHttpHandlerBuilder localeContextResolver(LocaleContextResolver localeContextResolver) {
/* 328 */     this.localeContextResolver = localeContextResolver;
/* 329 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasLocaleContextResolver() {
/* 338 */     return (this.localeContextResolver != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebHttpHandlerBuilder forwardedHeaderTransformer(ForwardedHeaderTransformer transformer) {
/* 348 */     this.forwardedHeaderTransformer = transformer;
/* 349 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasForwardedHeaderTransformer() {
/* 359 */     return (this.forwardedHeaderTransformer != null);
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
/*     */   public WebHttpHandlerBuilder httpHandlerDecorator(Function<HttpHandler, HttpHandler> handlerDecorator) {
/* 373 */     this
/* 374 */       .httpHandlerDecorator = (this.httpHandlerDecorator != null) ? handlerDecorator.<HttpHandler>andThen(this.httpHandlerDecorator) : handlerDecorator;
/* 375 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasHttpHandlerDecorator() {
/* 384 */     return (this.httpHandlerDecorator != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHandler build() {
/* 391 */     FilteringWebHandler filteringWebHandler = new FilteringWebHandler(this.webHandler, this.filters);
/* 392 */     ExceptionHandlingWebHandler exceptionHandlingWebHandler = new ExceptionHandlingWebHandler((WebHandler)filteringWebHandler, this.exceptionHandlers);
/*     */     
/* 394 */     HttpWebHandlerAdapter adapted = new HttpWebHandlerAdapter((WebHandler)exceptionHandlingWebHandler);
/* 395 */     if (this.sessionManager != null) {
/* 396 */       adapted.setSessionManager(this.sessionManager);
/*     */     }
/* 398 */     if (this.codecConfigurer != null) {
/* 399 */       adapted.setCodecConfigurer(this.codecConfigurer);
/*     */     }
/* 401 */     if (this.localeContextResolver != null) {
/* 402 */       adapted.setLocaleContextResolver(this.localeContextResolver);
/*     */     }
/* 404 */     if (this.forwardedHeaderTransformer != null) {
/* 405 */       adapted.setForwardedHeaderTransformer(this.forwardedHeaderTransformer);
/*     */     }
/* 407 */     if (this.applicationContext != null) {
/* 408 */       adapted.setApplicationContext(this.applicationContext);
/*     */     }
/* 410 */     adapted.afterPropertiesSet();
/*     */     
/* 412 */     return (this.httpHandlerDecorator != null) ? this.httpHandlerDecorator.apply(adapted) : adapted;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebHttpHandlerBuilder clone() {
/* 421 */     return new WebHttpHandlerBuilder(this);
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
/*     */   public static class SpringWebBlockHoundIntegration
/*     */     implements BlockHoundIntegration
/*     */   {
/*     */     public void applyTo(BlockHound.Builder builder) {
/* 436 */       builder.allowBlockingCallsInside("org.springframework.http.MediaTypeFactory", "<clinit>");
/* 437 */       builder.allowBlockingCallsInside("org.springframework.web.util.HtmlUtils", "<clinit>");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/adapter/WebHttpHandlerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */