/*     */ package org.springframework.web.server.adapter;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.core.NestedExceptionUtils;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.codec.HttpMessageReader;
/*     */ import org.springframework.http.codec.LoggingCodecSupport;
/*     */ import org.springframework.http.codec.ServerCodecConfigurer;
/*     */ import org.springframework.http.server.reactive.HttpHandler;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.server.ServerWebExchange;
/*     */ import org.springframework.web.server.WebHandler;
/*     */ import org.springframework.web.server.handler.WebHandlerDecorator;
/*     */ import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
/*     */ import org.springframework.web.server.i18n.LocaleContextResolver;
/*     */ import org.springframework.web.server.session.DefaultWebSessionManager;
/*     */ import org.springframework.web.server.session.WebSessionManager;
/*     */ import reactor.core.publisher.Mono;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpWebHandlerAdapter
/*     */   extends WebHandlerDecorator
/*     */   implements HttpHandler
/*     */ {
/*     */   private static final String DISCONNECTED_CLIENT_LOG_CATEGORY = "org.springframework.web.server.DisconnectedClient";
/*  73 */   private static final Set<String> DISCONNECTED_CLIENT_EXCEPTIONS = new HashSet<>(
/*  74 */       Arrays.asList(new String[] { "AbortedException", "ClientAbortException", "EOFException", "EofException" }));
/*     */ 
/*     */   
/*  77 */   private static final Log logger = LogFactory.getLog(HttpWebHandlerAdapter.class);
/*     */   
/*  79 */   private static final Log lostClientLogger = LogFactory.getLog("org.springframework.web.server.DisconnectedClient");
/*     */ 
/*     */   
/*  82 */   private WebSessionManager sessionManager = (WebSessionManager)new DefaultWebSessionManager();
/*     */   
/*     */   @Nullable
/*     */   private ServerCodecConfigurer codecConfigurer;
/*     */   
/*  87 */   private LocaleContextResolver localeContextResolver = (LocaleContextResolver)new AcceptHeaderLocaleContextResolver();
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ForwardedHeaderTransformer forwardedHeaderTransformer;
/*     */   
/*     */   @Nullable
/*     */   private ApplicationContext applicationContext;
/*     */   
/*     */   private boolean enableLoggingRequestDetails = false;
/*     */ 
/*     */   
/*     */   public HttpWebHandlerAdapter(WebHandler delegate) {
/* 100 */     super(delegate);
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
/*     */   public void setSessionManager(WebSessionManager sessionManager) {
/* 112 */     Assert.notNull(sessionManager, "WebSessionManager must not be null");
/* 113 */     this.sessionManager = sessionManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSessionManager getSessionManager() {
/* 120 */     return this.sessionManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCodecConfigurer(ServerCodecConfigurer codecConfigurer) {
/* 130 */     Assert.notNull(codecConfigurer, "ServerCodecConfigurer is required");
/* 131 */     this.codecConfigurer = codecConfigurer;
/*     */     
/* 133 */     this.enableLoggingRequestDetails = false;
/* 134 */     this.codecConfigurer.getReaders().stream()
/* 135 */       .filter(LoggingCodecSupport.class::isInstance)
/* 136 */       .forEach(reader -> {
/*     */           if (((LoggingCodecSupport)reader).isEnableLoggingRequestDetails()) {
/*     */             this.enableLoggingRequestDetails = true;
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerCodecConfigurer getCodecConfigurer() {
/* 147 */     if (this.codecConfigurer == null) {
/* 148 */       setCodecConfigurer(ServerCodecConfigurer.create());
/*     */     }
/* 150 */     return this.codecConfigurer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocaleContextResolver(LocaleContextResolver resolver) {
/* 161 */     Assert.notNull(resolver, "LocaleContextResolver is required");
/* 162 */     this.localeContextResolver = resolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LocaleContextResolver getLocaleContextResolver() {
/* 169 */     return this.localeContextResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setForwardedHeaderTransformer(ForwardedHeaderTransformer transformer) {
/* 180 */     Assert.notNull(transformer, "ForwardedHeaderTransformer is required");
/* 181 */     this.forwardedHeaderTransformer = transformer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ForwardedHeaderTransformer getForwardedHeaderTransformer() {
/* 190 */     return this.forwardedHeaderTransformer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setApplicationContext(ApplicationContext applicationContext) {
/* 201 */     this.applicationContext = applicationContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ApplicationContext getApplicationContext() {
/* 210 */     return this.applicationContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 218 */     if (logger.isDebugEnabled()) {
/* 219 */       String value = this.enableLoggingRequestDetails ? "shown which may lead to unsafe logging of potentially sensitive data" : "masked to prevent unsafe logging of potentially sensitive data";
/*     */ 
/*     */       
/* 222 */       logger.debug("enableLoggingRequestDetails='" + this.enableLoggingRequestDetails + "': form data and headers will be " + value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
/* 230 */     if (this.forwardedHeaderTransformer != null) {
/*     */       try {
/* 232 */         request = this.forwardedHeaderTransformer.apply(request);
/*     */       }
/* 234 */       catch (Throwable ex) {
/* 235 */         if (logger.isDebugEnabled()) {
/* 236 */           logger.debug("Failed to apply forwarded headers to " + formatRequest(request), ex);
/*     */         }
/* 238 */         response.setStatusCode(HttpStatus.BAD_REQUEST);
/* 239 */         return response.setComplete();
/*     */       } 
/*     */     }
/* 242 */     ServerWebExchange exchange = createExchange(request, response);
/*     */     
/* 244 */     LogFormatUtils.traceDebug(logger, traceOn -> exchange.getLogPrefix() + formatRequest(exchange.getRequest()) + (traceOn.booleanValue() ? (", headers=" + formatHeaders(exchange.getRequest().getHeaders())) : ""));
/*     */ 
/*     */ 
/*     */     
/* 248 */     return getDelegate().handle(exchange)
/* 249 */       .doOnSuccess(aVoid -> logResponse(exchange))
/* 250 */       .onErrorResume(ex -> handleUnresolvedError(exchange, ex))
/* 251 */       .then(Mono.defer(response::setComplete));
/*     */   }
/*     */   
/*     */   protected ServerWebExchange createExchange(ServerHttpRequest request, ServerHttpResponse response) {
/* 255 */     return new DefaultServerWebExchange(request, response, this.sessionManager, 
/* 256 */         getCodecConfigurer(), getLocaleContextResolver(), this.applicationContext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String formatRequest(ServerHttpRequest request) {
/* 266 */     String rawQuery = request.getURI().getRawQuery();
/* 267 */     String query = StringUtils.hasText(rawQuery) ? ("?" + rawQuery) : "";
/* 268 */     return "HTTP " + request.getMethod() + " \"" + request.getPath() + query + "\"";
/*     */   }
/*     */   
/*     */   private void logResponse(ServerWebExchange exchange) {
/* 272 */     LogFormatUtils.traceDebug(logger, traceOn -> {
/*     */           HttpStatus status = exchange.getResponse().getStatusCode();
/*     */           return exchange.getLogPrefix() + "Completed " + ((status != null) ? (String)status : "200 OK") + (traceOn.booleanValue() ? (", headers=" + formatHeaders(exchange.getResponse().getHeaders())) : "");
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private String formatHeaders(HttpHeaders responseHeaders) {
/* 280 */     return this.enableLoggingRequestDetails ? responseHeaders
/* 281 */       .toString() : (responseHeaders.isEmpty() ? "{}" : "{masked}");
/*     */   }
/*     */   
/*     */   private Mono<Void> handleUnresolvedError(ServerWebExchange exchange, Throwable ex) {
/* 285 */     ServerHttpRequest request = exchange.getRequest();
/* 286 */     ServerHttpResponse response = exchange.getResponse();
/* 287 */     String logPrefix = exchange.getLogPrefix();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 292 */     if (response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)) {
/* 293 */       logger.error(logPrefix + "500 Server Error for " + formatRequest(request), ex);
/* 294 */       return Mono.empty();
/*     */     } 
/* 296 */     if (isDisconnectedClientError(ex)) {
/* 297 */       if (lostClientLogger.isTraceEnabled()) {
/* 298 */         lostClientLogger.trace(logPrefix + "Client went away", ex);
/*     */       }
/* 300 */       else if (lostClientLogger.isDebugEnabled()) {
/* 301 */         lostClientLogger.debug(logPrefix + "Client went away: " + ex + " (stacktrace at TRACE level for '" + "org.springframework.web.server.DisconnectedClient" + "')");
/*     */       } 
/*     */       
/* 304 */       return Mono.empty();
/*     */     } 
/*     */ 
/*     */     
/* 308 */     logger.error(logPrefix + "Error [" + ex + "] for " + formatRequest(request) + ", but ServerHttpResponse already committed (" + response
/* 309 */         .getStatusCode() + ")");
/* 310 */     return Mono.error(ex);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isDisconnectedClientError(Throwable ex) {
/* 315 */     String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
/* 316 */     if (message != null) {
/* 317 */       String text = message.toLowerCase();
/* 318 */       if (text.contains("broken pipe") || text.contains("connection reset by peer")) {
/* 319 */         return true;
/*     */       }
/*     */     } 
/* 322 */     return DISCONNECTED_CLIENT_EXCEPTIONS.contains(ex.getClass().getSimpleName());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/adapter/HttpWebHandlerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */