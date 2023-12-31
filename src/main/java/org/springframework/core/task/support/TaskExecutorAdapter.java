/*     */ package org.springframework.core.task.support;
/*     */ 
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.FutureTask;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import org.springframework.core.task.AsyncListenableTaskExecutor;
/*     */ import org.springframework.core.task.TaskDecorator;
/*     */ import org.springframework.core.task.TaskRejectedException;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ public class TaskExecutorAdapter
/*     */   implements AsyncListenableTaskExecutor
/*     */ {
/*     */   private final Executor concurrentExecutor;
/*     */   @Nullable
/*     */   private TaskDecorator taskDecorator;
/*     */   
/*     */   public TaskExecutorAdapter(Executor concurrentExecutor) {
/*  60 */     Assert.notNull(concurrentExecutor, "Executor must not be null");
/*  61 */     this.concurrentExecutor = concurrentExecutor;
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
/*     */   public final void setTaskDecorator(TaskDecorator taskDecorator) {
/*  81 */     this.taskDecorator = taskDecorator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(Runnable task) {
/*     */     try {
/*  92 */       doExecute(this.concurrentExecutor, this.taskDecorator, task);
/*     */     }
/*  94 */     catch (RejectedExecutionException ex) {
/*  95 */       throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void execute(Runnable task, long startTimeout) {
/* 103 */     execute(task);
/*     */   }
/*     */ 
/*     */   
/*     */   public Future<?> submit(Runnable task) {
/*     */     try {
/* 109 */       if (this.taskDecorator == null && this.concurrentExecutor instanceof ExecutorService) {
/* 110 */         return ((ExecutorService)this.concurrentExecutor).submit(task);
/*     */       }
/*     */       
/* 113 */       FutureTask<Object> future = new FutureTask(task, null);
/* 114 */       doExecute(this.concurrentExecutor, this.taskDecorator, future);
/* 115 */       return future;
/*     */     
/*     */     }
/* 118 */     catch (RejectedExecutionException ex) {
/* 119 */       throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Future<T> submit(Callable<T> task) {
/*     */     try {
/* 127 */       if (this.taskDecorator == null && this.concurrentExecutor instanceof ExecutorService) {
/* 128 */         return ((ExecutorService)this.concurrentExecutor).submit(task);
/*     */       }
/*     */       
/* 131 */       FutureTask<T> future = new FutureTask<>(task);
/* 132 */       doExecute(this.concurrentExecutor, this.taskDecorator, future);
/* 133 */       return future;
/*     */     
/*     */     }
/* 136 */     catch (RejectedExecutionException ex) {
/* 137 */       throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ListenableFuture<?> submitListenable(Runnable task) {
/*     */     try {
/* 145 */       ListenableFutureTask<Object> future = new ListenableFutureTask(task, null);
/* 146 */       doExecute(this.concurrentExecutor, this.taskDecorator, (Runnable)future);
/* 147 */       return (ListenableFuture<?>)future;
/*     */     }
/* 149 */     catch (RejectedExecutionException ex) {
/* 150 */       throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
/*     */     try {
/* 158 */       ListenableFutureTask<T> future = new ListenableFutureTask(task);
/* 159 */       doExecute(this.concurrentExecutor, this.taskDecorator, (Runnable)future);
/* 160 */       return (ListenableFuture<T>)future;
/*     */     }
/* 162 */     catch (RejectedExecutionException ex) {
/* 163 */       throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
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
/*     */   protected void doExecute(Executor concurrentExecutor, @Nullable TaskDecorator taskDecorator, Runnable runnable) throws RejectedExecutionException {
/* 181 */     concurrentExecutor.execute((taskDecorator != null) ? taskDecorator.decorate(runnable) : runnable);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/task/support/TaskExecutorAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */