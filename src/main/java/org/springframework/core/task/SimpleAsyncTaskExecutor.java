/*     */ package org.springframework.core.task;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.FutureTask;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ConcurrencyThrottleSupport;
/*     */ import org.springframework.util.CustomizableThreadCreator;
/*     */ import org.springframework.util.concurrent.ListenableFuture;
/*     */ import org.springframework.util.concurrent.ListenableFutureTask;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleAsyncTaskExecutor
/*     */   extends CustomizableThreadCreator
/*     */   implements AsyncListenableTaskExecutor, Serializable
/*     */ {
/*     */   public static final int UNBOUNDED_CONCURRENCY = -1;
/*     */   public static final int NO_CONCURRENCY = 0;
/*  68 */   private final ConcurrencyThrottleAdapter concurrencyThrottle = new ConcurrencyThrottleAdapter();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ThreadFactory threadFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private TaskDecorator taskDecorator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleAsyncTaskExecutor(String threadNamePrefix) {
/*  89 */     super(threadNamePrefix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleAsyncTaskExecutor(ThreadFactory threadFactory) {
/*  97 */     this.threadFactory = threadFactory;
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
/*     */   public void setThreadFactory(@Nullable ThreadFactory threadFactory) {
/* 110 */     this.threadFactory = threadFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public final ThreadFactory getThreadFactory() {
/* 118 */     return this.threadFactory;
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
/*     */   public final void setTaskDecorator(TaskDecorator taskDecorator) {
/* 137 */     this.taskDecorator = taskDecorator;
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
/*     */   public void setConcurrencyLimit(int concurrencyLimit) {
/* 151 */     this.concurrencyThrottle.setConcurrencyLimit(concurrencyLimit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getConcurrencyLimit() {
/* 158 */     return this.concurrencyThrottle.getConcurrencyLimit();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isThrottleActive() {
/* 168 */     return this.concurrencyThrottle.isThrottleActive();
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
/*     */   public void execute(Runnable task) {
/* 180 */     execute(task, Long.MAX_VALUE);
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
/*     */   @Deprecated
/*     */   public void execute(Runnable task, long startTimeout) {
/* 195 */     Assert.notNull(task, "Runnable must not be null");
/* 196 */     Runnable taskToUse = (this.taskDecorator != null) ? this.taskDecorator.decorate(task) : task;
/* 197 */     if (isThrottleActive() && startTimeout > 0L) {
/* 198 */       this.concurrencyThrottle.beforeAccess();
/* 199 */       doExecute(new ConcurrencyThrottlingRunnable(taskToUse));
/*     */     } else {
/*     */       
/* 202 */       doExecute(taskToUse);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Future<?> submit(Runnable task) {
/* 209 */     FutureTask<Object> future = new FutureTask(task, null);
/* 210 */     execute(future, Long.MAX_VALUE);
/* 211 */     return future;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Future<T> submit(Callable<T> task) {
/* 217 */     FutureTask<T> future = new FutureTask<>(task);
/* 218 */     execute(future, Long.MAX_VALUE);
/* 219 */     return future;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ListenableFuture<?> submitListenable(Runnable task) {
/* 225 */     ListenableFutureTask<Object> future = new ListenableFutureTask(task, null);
/* 226 */     execute((Runnable)future, Long.MAX_VALUE);
/* 227 */     return (ListenableFuture<?>)future;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
/* 233 */     ListenableFutureTask<T> future = new ListenableFutureTask(task);
/* 234 */     execute((Runnable)future, Long.MAX_VALUE);
/* 235 */     return (ListenableFuture<T>)future;
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
/*     */   protected void doExecute(Runnable task) {
/* 247 */     Thread thread = (this.threadFactory != null) ? this.threadFactory.newThread(task) : createThread(task);
/* 248 */     thread.start();
/*     */   }
/*     */ 
/*     */   
/*     */   public SimpleAsyncTaskExecutor() {}
/*     */ 
/*     */   
/*     */   private static class ConcurrencyThrottleAdapter
/*     */     extends ConcurrencyThrottleSupport
/*     */   {
/*     */     private ConcurrencyThrottleAdapter() {}
/*     */     
/*     */     protected void beforeAccess() {
/* 261 */       super.beforeAccess();
/*     */     }
/*     */ 
/*     */     
/*     */     protected void afterAccess() {
/* 266 */       super.afterAccess();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class ConcurrencyThrottlingRunnable
/*     */     implements Runnable
/*     */   {
/*     */     private final Runnable target;
/*     */ 
/*     */ 
/*     */     
/*     */     public ConcurrencyThrottlingRunnable(Runnable target) {
/* 280 */       this.target = target;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/*     */       try {
/* 286 */         this.target.run();
/*     */       } finally {
/*     */         
/* 289 */         SimpleAsyncTaskExecutor.this.concurrencyThrottle.afterAccess();
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/task/SimpleAsyncTaskExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */