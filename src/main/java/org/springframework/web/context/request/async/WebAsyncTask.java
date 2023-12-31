/*     */ package org.springframework.web.context.request.async;
/*     */ 
/*     */ import java.util.concurrent.Callable;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.beans.factory.BeanFactoryAware;
/*     */ import org.springframework.core.task.AsyncTaskExecutor;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebAsyncTask<V>
/*     */   implements BeanFactoryAware
/*     */ {
/*     */   private final Callable<V> callable;
/*     */   private Long timeout;
/*     */   private AsyncTaskExecutor executor;
/*     */   private String executorName;
/*     */   private BeanFactory beanFactory;
/*     */   private Callable<V> timeoutCallback;
/*     */   private Callable<V> errorCallback;
/*     */   private Runnable completionCallback;
/*     */   
/*     */   public WebAsyncTask(Callable<V> callable) {
/*  60 */     Assert.notNull(callable, "Callable must not be null");
/*  61 */     this.callable = callable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebAsyncTask(long timeout, Callable<V> callable) {
/*  70 */     this(callable);
/*  71 */     this.timeout = Long.valueOf(timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebAsyncTask(@Nullable Long timeout, String executorName, Callable<V> callable) {
/*  81 */     this(callable);
/*  82 */     Assert.notNull(executorName, "Executor name must not be null");
/*  83 */     this.executorName = executorName;
/*  84 */     this.timeout = timeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebAsyncTask(@Nullable Long timeout, AsyncTaskExecutor executor, Callable<V> callable) {
/*  94 */     this(callable);
/*  95 */     Assert.notNull(executor, "Executor must not be null");
/*  96 */     this.executor = executor;
/*  97 */     this.timeout = timeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Callable<?> getCallable() {
/* 105 */     return this.callable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Long getTimeout() {
/* 113 */     return this.timeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBeanFactory(BeanFactory beanFactory) {
/* 123 */     this.beanFactory = beanFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public AsyncTaskExecutor getExecutor() {
/* 132 */     if (this.executor != null) {
/* 133 */       return this.executor;
/*     */     }
/* 135 */     if (this.executorName != null) {
/* 136 */       Assert.state((this.beanFactory != null), "BeanFactory is required to look up an executor bean by name");
/* 137 */       return (AsyncTaskExecutor)this.beanFactory.getBean(this.executorName, AsyncTaskExecutor.class);
/*     */     } 
/*     */     
/* 140 */     return null;
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
/*     */   public void onTimeout(Callable<V> callback) {
/* 154 */     this.timeoutCallback = callback;
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
/*     */   public void onError(Callable<V> callback) {
/* 168 */     this.errorCallback = callback;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onCompletion(Runnable callback) {
/* 177 */     this.completionCallback = callback;
/*     */   }
/*     */   
/*     */   CallableProcessingInterceptor getInterceptor() {
/* 181 */     return new CallableProcessingInterceptor()
/*     */       {
/*     */         public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
/* 184 */           return (WebAsyncTask.this.timeoutCallback != null) ? WebAsyncTask.this.timeoutCallback.call() : CallableProcessingInterceptor.RESULT_NONE;
/*     */         }
/*     */         
/*     */         public <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) throws Exception {
/* 188 */           return (WebAsyncTask.this.errorCallback != null) ? WebAsyncTask.this.errorCallback.call() : CallableProcessingInterceptor.RESULT_NONE;
/*     */         }
/*     */         
/*     */         public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
/* 192 */           if (WebAsyncTask.this.completionCallback != null)
/* 193 */             WebAsyncTask.this.completionCallback.run(); 
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/async/WebAsyncTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */