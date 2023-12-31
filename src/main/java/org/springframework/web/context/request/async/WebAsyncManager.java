/*     */ package org.springframework.web.context.request.async;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.core.task.AsyncTaskExecutor;
/*     */ import org.springframework.core.task.SimpleAsyncTaskExecutor;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class WebAsyncManager
/*     */ {
/*  64 */   private static final Object RESULT_NONE = new Object();
/*     */   
/*  66 */   private static final AsyncTaskExecutor DEFAULT_TASK_EXECUTOR = (AsyncTaskExecutor)new SimpleAsyncTaskExecutor(WebAsyncManager.class
/*  67 */       .getSimpleName());
/*     */   
/*  69 */   private static final Log logger = LogFactory.getLog(WebAsyncManager.class);
/*     */   
/*  71 */   private static final CallableProcessingInterceptor timeoutCallableInterceptor = new TimeoutCallableProcessingInterceptor();
/*     */ 
/*     */   
/*  74 */   private static final DeferredResultProcessingInterceptor timeoutDeferredResultInterceptor = new TimeoutDeferredResultProcessingInterceptor();
/*     */ 
/*     */   
/*  77 */   private static Boolean taskExecutorWarning = Boolean.valueOf(true);
/*     */ 
/*     */   
/*     */   private AsyncWebRequest asyncWebRequest;
/*     */   
/*  82 */   private AsyncTaskExecutor taskExecutor = DEFAULT_TASK_EXECUTOR;
/*     */   
/*  84 */   private volatile Object concurrentResult = RESULT_NONE;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile Object[] concurrentResultContext;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean errorHandlingInProgress;
/*     */ 
/*     */   
/*  95 */   private final Map<Object, CallableProcessingInterceptor> callableInterceptors = new LinkedHashMap<>();
/*     */   
/*  97 */   private final Map<Object, DeferredResultProcessingInterceptor> deferredResultInterceptors = new LinkedHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAsyncWebRequest(AsyncWebRequest asyncWebRequest) {
/* 119 */     Assert.notNull(asyncWebRequest, "AsyncWebRequest must not be null");
/* 120 */     this.asyncWebRequest = asyncWebRequest;
/* 121 */     this.asyncWebRequest.addCompletionHandler(() -> asyncWebRequest.removeAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE, 0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
/* 131 */     this.taskExecutor = taskExecutor;
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
/*     */   public boolean isConcurrentHandlingStarted() {
/* 143 */     return (this.asyncWebRequest != null && this.asyncWebRequest.isAsyncStarted());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasConcurrentResult() {
/* 150 */     return (this.concurrentResult != RESULT_NONE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getConcurrentResult() {
/* 160 */     return this.concurrentResult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] getConcurrentResultContext() {
/* 169 */     return this.concurrentResultContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public CallableProcessingInterceptor getCallableInterceptor(Object key) {
/* 179 */     return this.callableInterceptors.get(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public DeferredResultProcessingInterceptor getDeferredResultInterceptor(Object key) {
/* 189 */     return this.deferredResultInterceptors.get(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerCallableInterceptor(Object key, CallableProcessingInterceptor interceptor) {
/* 198 */     Assert.notNull(key, "Key is required");
/* 199 */     Assert.notNull(interceptor, "CallableProcessingInterceptor  is required");
/* 200 */     this.callableInterceptors.put(key, interceptor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerCallableInterceptors(CallableProcessingInterceptor... interceptors) {
/* 209 */     Assert.notNull(interceptors, "A CallableProcessingInterceptor is required");
/* 210 */     for (CallableProcessingInterceptor interceptor : interceptors) {
/* 211 */       String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
/* 212 */       this.callableInterceptors.put(key, interceptor);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerDeferredResultInterceptor(Object key, DeferredResultProcessingInterceptor interceptor) {
/* 222 */     Assert.notNull(key, "Key is required");
/* 223 */     Assert.notNull(interceptor, "DeferredResultProcessingInterceptor is required");
/* 224 */     this.deferredResultInterceptors.put(key, interceptor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerDeferredResultInterceptors(DeferredResultProcessingInterceptor... interceptors) {
/* 233 */     Assert.notNull(interceptors, "A DeferredResultProcessingInterceptor is required");
/* 234 */     for (DeferredResultProcessingInterceptor interceptor : interceptors) {
/* 235 */       String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
/* 236 */       this.deferredResultInterceptors.put(key, interceptor);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearConcurrentResult() {
/* 245 */     synchronized (this) {
/* 246 */       this.concurrentResult = RESULT_NONE;
/* 247 */       this.concurrentResultContext = null;
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
/*     */   public void startCallableProcessing(Callable<?> callable, Object... processingContext) throws Exception {
/* 266 */     Assert.notNull(callable, "Callable must not be null");
/* 267 */     startCallableProcessing(new WebAsyncTask(callable), processingContext);
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
/*     */   public void startCallableProcessing(WebAsyncTask<?> webAsyncTask, Object... processingContext) throws Exception {
/* 282 */     Assert.notNull(webAsyncTask, "WebAsyncTask must not be null");
/* 283 */     Assert.state((this.asyncWebRequest != null), "AsyncWebRequest must not be null");
/*     */     
/* 285 */     Long timeout = webAsyncTask.getTimeout();
/* 286 */     if (timeout != null) {
/* 287 */       this.asyncWebRequest.setTimeout(timeout);
/*     */     }
/*     */     
/* 290 */     AsyncTaskExecutor executor = webAsyncTask.getExecutor();
/* 291 */     if (executor != null) {
/* 292 */       this.taskExecutor = executor;
/*     */     } else {
/*     */       
/* 295 */       logExecutorWarning();
/*     */     } 
/*     */     
/* 298 */     List<CallableProcessingInterceptor> interceptors = new ArrayList<>();
/* 299 */     interceptors.add(webAsyncTask.getInterceptor());
/* 300 */     interceptors.addAll(this.callableInterceptors.values());
/* 301 */     interceptors.add(timeoutCallableInterceptor);
/*     */     
/* 303 */     Callable<?> callable = webAsyncTask.getCallable();
/* 304 */     CallableInterceptorChain interceptorChain = new CallableInterceptorChain(interceptors);
/*     */     
/* 306 */     this.asyncWebRequest.addTimeoutHandler(() -> {
/*     */           if (logger.isDebugEnabled()) {
/*     */             logger.debug("Async request timeout for " + formatRequestUri());
/*     */           }
/*     */           
/*     */           Object result = interceptorChain.triggerAfterTimeout(this.asyncWebRequest, callable);
/*     */           if (result != CallableProcessingInterceptor.RESULT_NONE) {
/*     */             setConcurrentResultAndDispatch(result);
/*     */           }
/*     */         });
/* 316 */     this.asyncWebRequest.addErrorHandler(ex -> {
/*     */           if (!this.errorHandlingInProgress) {
/*     */             if (logger.isDebugEnabled()) {
/*     */               logger.debug("Async request error for " + formatRequestUri() + ": " + ex);
/*     */             }
/*     */             
/*     */             Object result = interceptorChain.triggerAfterError(this.asyncWebRequest, callable, ex);
/*     */             result = (result != CallableProcessingInterceptor.RESULT_NONE) ? result : ex;
/*     */             setConcurrentResultAndDispatch(result);
/*     */           } 
/*     */         });
/* 327 */     this.asyncWebRequest.addCompletionHandler(() -> interceptorChain.triggerAfterCompletion(this.asyncWebRequest, callable));
/*     */ 
/*     */     
/* 330 */     interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, callable);
/* 331 */     startAsyncProcessing(processingContext);
/*     */     try {
/* 333 */       Future<?> future = this.taskExecutor.submit(() -> {
/*     */             Object result = null;
/*     */             
/*     */             try {
/*     */               interceptorChain.applyPreProcess(this.asyncWebRequest, callable);
/*     */               result = callable.call();
/* 339 */             } catch (Throwable ex) {
/*     */               result = ex;
/*     */             } finally {
/*     */               result = interceptorChain.applyPostProcess(this.asyncWebRequest, callable, result);
/*     */             } 
/*     */             
/*     */             setConcurrentResultAndDispatch(result);
/*     */           });
/* 347 */       interceptorChain.setTaskFuture(future);
/*     */     }
/* 349 */     catch (RejectedExecutionException ex) {
/* 350 */       Object result = interceptorChain.applyPostProcess(this.asyncWebRequest, callable, ex);
/* 351 */       setConcurrentResultAndDispatch(result);
/* 352 */       throw ex;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void logExecutorWarning() {
/* 357 */     if (taskExecutorWarning.booleanValue() && logger.isWarnEnabled()) {
/* 358 */       synchronized (DEFAULT_TASK_EXECUTOR) {
/* 359 */         AsyncTaskExecutor executor = this.taskExecutor;
/* 360 */         if (taskExecutorWarning.booleanValue() && (executor instanceof SimpleAsyncTaskExecutor || executor instanceof org.springframework.core.task.SyncTaskExecutor)) {
/*     */           
/* 362 */           String executorTypeName = executor.getClass().getSimpleName();
/* 363 */           logger.warn("\n!!!\nAn Executor is required to handle java.util.concurrent.Callable return values.\nPlease, configure a TaskExecutor in the MVC config under \"async support\".\nThe " + executorTypeName + " currently in use is not suitable under load.\n-------------------------------\nRequest URI: '" + 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 368 */               formatRequestUri() + "'\n!!!");
/*     */           
/* 370 */           taskExecutorWarning = Boolean.valueOf(false);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private String formatRequestUri() {
/* 377 */     HttpServletRequest request = (HttpServletRequest)this.asyncWebRequest.getNativeRequest(HttpServletRequest.class);
/* 378 */     return (request != null) ? request.getRequestURI() : "servlet container";
/*     */   }
/*     */   
/*     */   private void setConcurrentResultAndDispatch(Object result) {
/* 382 */     synchronized (this) {
/* 383 */       if (this.concurrentResult != RESULT_NONE) {
/*     */         return;
/*     */       }
/* 386 */       this.concurrentResult = result;
/* 387 */       this.errorHandlingInProgress = result instanceof Throwable;
/*     */     } 
/*     */     
/* 390 */     if (this.asyncWebRequest.isAsyncComplete()) {
/* 391 */       if (logger.isDebugEnabled()) {
/* 392 */         logger.debug("Async result set but request already complete: " + formatRequestUri());
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/* 397 */     if (logger.isDebugEnabled()) {
/* 398 */       boolean isError = result instanceof Throwable;
/* 399 */       logger.debug("Async " + (isError ? "error" : "result set") + ", dispatch to " + formatRequestUri());
/*     */     } 
/* 401 */     this.asyncWebRequest.dispatch();
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
/*     */   public void startDeferredResultProcessing(DeferredResult<?> deferredResult, Object... processingContext) throws Exception {
/* 421 */     Assert.notNull(deferredResult, "DeferredResult must not be null");
/* 422 */     Assert.state((this.asyncWebRequest != null), "AsyncWebRequest must not be null");
/*     */     
/* 424 */     Long timeout = deferredResult.getTimeoutValue();
/* 425 */     if (timeout != null) {
/* 426 */       this.asyncWebRequest.setTimeout(timeout);
/*     */     }
/*     */     
/* 429 */     List<DeferredResultProcessingInterceptor> interceptors = new ArrayList<>();
/* 430 */     interceptors.add(deferredResult.getInterceptor());
/* 431 */     interceptors.addAll(this.deferredResultInterceptors.values());
/* 432 */     interceptors.add(timeoutDeferredResultInterceptor);
/*     */     
/* 434 */     DeferredResultInterceptorChain interceptorChain = new DeferredResultInterceptorChain(interceptors);
/*     */     
/* 436 */     this.asyncWebRequest.addTimeoutHandler(() -> {
/*     */           
/*     */           try {
/*     */             interceptorChain.triggerAfterTimeout(this.asyncWebRequest, deferredResult);
/* 440 */           } catch (Throwable ex) {
/*     */             setConcurrentResultAndDispatch(ex);
/*     */           } 
/*     */         });
/*     */     
/* 445 */     this.asyncWebRequest.addErrorHandler(ex -> {
/*     */           if (!this.errorHandlingInProgress) {
/*     */             try {
/*     */               if (!interceptorChain.triggerAfterError(this.asyncWebRequest, deferredResult, ex)) {
/*     */                 return;
/*     */               }
/*     */               
/*     */               deferredResult.setErrorResult(ex);
/* 453 */             } catch (Throwable interceptorEx) {
/*     */               setConcurrentResultAndDispatch(interceptorEx);
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 459 */     this.asyncWebRequest.addCompletionHandler(() -> interceptorChain.triggerAfterCompletion(this.asyncWebRequest, deferredResult));
/*     */ 
/*     */     
/* 462 */     interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, deferredResult);
/* 463 */     startAsyncProcessing(processingContext);
/*     */     
/*     */     try {
/* 466 */       interceptorChain.applyPreProcess(this.asyncWebRequest, deferredResult);
/* 467 */       deferredResult.setResultHandler(result -> {
/*     */             result = interceptorChain.applyPostProcess(this.asyncWebRequest, deferredResult, result);
/*     */             
/*     */             setConcurrentResultAndDispatch(result);
/*     */           });
/* 472 */     } catch (Throwable ex) {
/* 473 */       setConcurrentResultAndDispatch(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void startAsyncProcessing(Object[] processingContext) {
/* 478 */     synchronized (this) {
/* 479 */       this.concurrentResult = RESULT_NONE;
/* 480 */       this.concurrentResultContext = processingContext;
/* 481 */       this.errorHandlingInProgress = false;
/*     */     } 
/* 483 */     this.asyncWebRequest.startAsync();
/*     */     
/* 485 */     if (logger.isDebugEnabled())
/* 486 */       logger.debug("Started async request"); 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/async/WebAsyncManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */