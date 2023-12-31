/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import javax.servlet.AsyncContext;
/*     */ import javax.servlet.AsyncEvent;
/*     */ import javax.servlet.AsyncListener;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.Servlet;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRegistration;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.reactivestreams.Subscriber;
/*     */ import org.reactivestreams.Subscription;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DefaultDataBufferFactory;
/*     */ import org.springframework.http.HttpLogging;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletHttpHandlerAdapter
/*     */   implements Servlet
/*     */ {
/*  60 */   private static final Log logger = HttpLogging.forLogName(ServletHttpHandlerAdapter.class);
/*     */   
/*     */   private static final int DEFAULT_BUFFER_SIZE = 8192;
/*     */   
/*  64 */   private static final String WRITE_ERROR_ATTRIBUTE_NAME = ServletHttpHandlerAdapter.class.getName() + ".ERROR";
/*     */ 
/*     */   
/*     */   private final HttpHandler httpHandler;
/*     */   
/*  69 */   private int bufferSize = 8192;
/*     */   
/*     */   @Nullable
/*     */   private String servletPath;
/*     */   
/*  74 */   private DataBufferFactory dataBufferFactory = (DataBufferFactory)DefaultDataBufferFactory.sharedInstance;
/*     */ 
/*     */   
/*     */   public ServletHttpHandlerAdapter(HttpHandler httpHandler) {
/*  78 */     Assert.notNull(httpHandler, "HttpHandler must not be null");
/*  79 */     this.httpHandler = httpHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBufferSize(int bufferSize) {
/*  88 */     Assert.isTrue((bufferSize > 0), "Buffer size must be larger than zero");
/*  89 */     this.bufferSize = bufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBufferSize() {
/*  96 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getServletPath() {
/* 108 */     return this.servletPath;
/*     */   }
/*     */   
/*     */   public void setDataBufferFactory(DataBufferFactory dataBufferFactory) {
/* 112 */     Assert.notNull(dataBufferFactory, "DataBufferFactory must not be null");
/* 113 */     this.dataBufferFactory = dataBufferFactory;
/*     */   }
/*     */   
/*     */   public DataBufferFactory getDataBufferFactory() {
/* 117 */     return this.dataBufferFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(ServletConfig config) {
/* 125 */     this.servletPath = getServletPath(config);
/*     */   }
/*     */   
/*     */   private String getServletPath(ServletConfig config) {
/* 129 */     String name = config.getServletName();
/* 130 */     ServletRegistration registration = config.getServletContext().getServletRegistration(name);
/* 131 */     if (registration == null) {
/* 132 */       throw new IllegalStateException("ServletRegistration not found for Servlet '" + name + "'");
/*     */     }
/*     */     
/* 135 */     Collection<String> mappings = registration.getMappings();
/* 136 */     if (mappings.size() == 1) {
/* 137 */       String mapping = mappings.iterator().next();
/* 138 */       if (mapping.equals("/")) {
/* 139 */         return "";
/*     */       }
/* 141 */       if (mapping.endsWith("/*")) {
/* 142 */         String path = mapping.substring(0, mapping.length() - 2);
/* 143 */         if (!path.isEmpty() && logger.isDebugEnabled()) {
/* 144 */           logger.debug("Found servlet mapping prefix '" + path + "' for '" + name + "'");
/*     */         }
/* 146 */         return path;
/*     */       } 
/*     */     } 
/*     */     
/* 150 */     throw new IllegalArgumentException("Expected a single Servlet mapping: either the default Servlet mapping (i.e. '/'), or a path based mapping (e.g. '/*', '/foo/*'). Actual mappings: " + mappings + " for Servlet '" + name + "'");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
/*     */     ServletServerHttpRequest httpRequest;
/*     */     AsyncListener requestListener;
/*     */     String logPrefix;
/* 160 */     if (DispatcherType.ASYNC == request.getDispatcherType()) {
/* 161 */       Throwable ex = (Throwable)request.getAttribute(WRITE_ERROR_ATTRIBUTE_NAME);
/* 162 */       throw new ServletException("Failed to create response content", ex);
/*     */     } 
/*     */ 
/*     */     
/* 166 */     AsyncContext asyncContext = request.startAsync();
/* 167 */     asyncContext.setTimeout(-1L);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 173 */       httpRequest = createRequest((HttpServletRequest)request, asyncContext);
/* 174 */       requestListener = httpRequest.getAsyncListener();
/* 175 */       logPrefix = httpRequest.getLogPrefix();
/*     */     }
/* 177 */     catch (URISyntaxException ex) {
/* 178 */       if (logger.isDebugEnabled()) {
/* 179 */         logger.debug("Failed to get request  URL: " + ex.getMessage());
/*     */       }
/* 181 */       ((HttpServletResponse)response).setStatus(400);
/* 182 */       asyncContext.complete();
/*     */       
/*     */       return;
/*     */     } 
/* 186 */     ServerHttpResponse httpResponse = createResponse((HttpServletResponse)response, asyncContext, httpRequest);
/* 187 */     AsyncListener responseListener = ((ServletServerHttpResponse)httpResponse).getAsyncListener();
/* 188 */     if (httpRequest.getMethod() == HttpMethod.HEAD) {
/* 189 */       httpResponse = new HttpHeadResponseDecorator(httpResponse);
/*     */     }
/*     */     
/* 192 */     AtomicBoolean completionFlag = new AtomicBoolean();
/* 193 */     HandlerResultSubscriber subscriber = new HandlerResultSubscriber(asyncContext, completionFlag, logPrefix);
/*     */     
/* 195 */     asyncContext.addListener(new HttpHandlerAsyncListener(requestListener, responseListener, subscriber, completionFlag, logPrefix));
/*     */ 
/*     */     
/* 198 */     this.httpHandler.handle(httpRequest, httpResponse).subscribe(subscriber);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext context) throws IOException, URISyntaxException {
/* 204 */     Assert.notNull(this.servletPath, "Servlet path is not initialized");
/* 205 */     return new ServletServerHttpRequest(request, context, this.servletPath, 
/* 206 */         getDataBufferFactory(), getBufferSize());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context, ServletServerHttpRequest request) throws IOException {
/* 212 */     return new ServletServerHttpResponse(response, context, getDataBufferFactory(), getBufferSize(), request);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getServletInfo() {
/* 217 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ServletConfig getServletConfig() {
/* 223 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() {}
/*     */ 
/*     */   
/*     */   private static void runIfAsyncNotComplete(AsyncContext asyncContext, AtomicBoolean isCompleted, Runnable task) {
/*     */     try {
/* 233 */       if (asyncContext.getRequest().isAsyncStarted() && isCompleted.compareAndSet(false, true)) {
/* 234 */         task.run();
/*     */       }
/*     */     }
/* 237 */     catch (IllegalStateException illegalStateException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class HttpHandlerAsyncListener
/*     */     implements AsyncListener
/*     */   {
/*     */     private final AsyncListener requestAsyncListener;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final AsyncListener responseAsyncListener;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final Runnable handlerDisposeTask;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final AtomicBoolean completionFlag;
/*     */ 
/*     */ 
/*     */     
/*     */     private final String logPrefix;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public HttpHandlerAsyncListener(AsyncListener requestAsyncListener, AsyncListener responseAsyncListener, Runnable handlerDisposeTask, AtomicBoolean completionFlag, String logPrefix) {
/* 272 */       this.requestAsyncListener = requestAsyncListener;
/* 273 */       this.responseAsyncListener = responseAsyncListener;
/* 274 */       this.handlerDisposeTask = handlerDisposeTask;
/* 275 */       this.completionFlag = completionFlag;
/* 276 */       this.logPrefix = logPrefix;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onTimeout(AsyncEvent event) {
/* 283 */       if (ServletHttpHandlerAdapter.logger.isDebugEnabled()) {
/* 284 */         ServletHttpHandlerAdapter.logger.debug(this.logPrefix + "AsyncEvent onTimeout");
/*     */       }
/* 286 */       delegateTimeout(this.requestAsyncListener, event);
/* 287 */       delegateTimeout(this.responseAsyncListener, event);
/* 288 */       handleTimeoutOrError(event);
/*     */     }
/*     */ 
/*     */     
/*     */     public void onError(AsyncEvent event) {
/* 293 */       Throwable ex = event.getThrowable();
/* 294 */       if (ServletHttpHandlerAdapter.logger.isDebugEnabled()) {
/* 295 */         ServletHttpHandlerAdapter.logger.debug(this.logPrefix + "AsyncEvent onError: " + ((ex != null) ? ex : "<no Throwable>"));
/*     */       }
/* 297 */       delegateError(this.requestAsyncListener, event);
/* 298 */       delegateError(this.responseAsyncListener, event);
/* 299 */       handleTimeoutOrError(event);
/*     */     }
/*     */ 
/*     */     
/*     */     public void onComplete(AsyncEvent event) {
/* 304 */       delegateComplete(this.requestAsyncListener, event);
/* 305 */       delegateComplete(this.responseAsyncListener, event);
/*     */     }
/*     */     
/*     */     private static void delegateTimeout(AsyncListener listener, AsyncEvent event) {
/*     */       try {
/* 310 */         listener.onTimeout(event);
/*     */       }
/* 312 */       catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private static void delegateError(AsyncListener listener, AsyncEvent event) {
/*     */       try {
/* 319 */         listener.onError(event);
/*     */       }
/* 321 */       catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private static void delegateComplete(AsyncListener listener, AsyncEvent event) {
/*     */       try {
/* 328 */         listener.onComplete(event);
/*     */       }
/* 330 */       catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private void handleTimeoutOrError(AsyncEvent event) {
/* 336 */       AsyncContext context = event.getAsyncContext();
/* 337 */       ServletHttpHandlerAdapter.runIfAsyncNotComplete(context, this.completionFlag, () -> {
/*     */             try {
/*     */               this.handlerDisposeTask.run();
/*     */             } finally {
/*     */               context.complete();
/*     */             } 
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onStartAsync(AsyncEvent event) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class HandlerResultSubscriber
/*     */     implements Subscriber<Void>, Runnable
/*     */   {
/*     */     private final AsyncContext asyncContext;
/*     */ 
/*     */     
/*     */     private final AtomicBoolean completionFlag;
/*     */     
/*     */     private final String logPrefix;
/*     */     
/*     */     @Nullable
/*     */     private volatile Subscription subscription;
/*     */ 
/*     */     
/*     */     public HandlerResultSubscriber(AsyncContext asyncContext, AtomicBoolean completionFlag, String logPrefix) {
/* 368 */       this.asyncContext = asyncContext;
/* 369 */       this.completionFlag = completionFlag;
/* 370 */       this.logPrefix = logPrefix;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onSubscribe(Subscription subscription) {
/* 375 */       this.subscription = subscription;
/* 376 */       subscription.request(Long.MAX_VALUE);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onNext(Void aVoid) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void onError(Throwable ex) {
/* 386 */       if (ServletHttpHandlerAdapter.logger.isTraceEnabled()) {
/* 387 */         ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "onError: " + ex);
/*     */       }
/* 389 */       ServletHttpHandlerAdapter.runIfAsyncNotComplete(this.asyncContext, this.completionFlag, () -> {
/*     */             if (this.asyncContext.getResponse().isCommitted()) {
/*     */               ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "Dispatch to container, to raise the error on servlet thread");
/*     */               this.asyncContext.getRequest().setAttribute(ServletHttpHandlerAdapter.WRITE_ERROR_ATTRIBUTE_NAME, ex);
/*     */               this.asyncContext.dispatch();
/*     */             } else {
/*     */               try {
/*     */                 ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "Setting ServletResponse status to 500 Server Error");
/*     */                 this.asyncContext.getResponse().resetBuffer();
/*     */                 ((HttpServletResponse)this.asyncContext.getResponse()).setStatus(500);
/*     */               } finally {
/*     */                 this.asyncContext.complete();
/*     */               } 
/*     */             } 
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onComplete() {
/* 410 */       if (ServletHttpHandlerAdapter.logger.isTraceEnabled()) {
/* 411 */         ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "onComplete");
/*     */       }
/* 413 */       ServletHttpHandlerAdapter.runIfAsyncNotComplete(this.asyncContext, this.completionFlag, this.asyncContext::complete);
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 418 */       Subscription s = this.subscription;
/* 419 */       if (s != null)
/* 420 */         s.cancel(); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/ServletHttpHandlerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */