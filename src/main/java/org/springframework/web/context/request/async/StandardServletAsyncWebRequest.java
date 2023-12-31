/*     */ package org.springframework.web.context.request.async;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.function.Consumer;
/*     */ import javax.servlet.AsyncContext;
/*     */ import javax.servlet.AsyncEvent;
/*     */ import javax.servlet.AsyncListener;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.web.context.request.ServletWebRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StandardServletAsyncWebRequest
/*     */   extends ServletWebRequest
/*     */   implements AsyncWebRequest, AsyncListener
/*     */ {
/*     */   private Long timeout;
/*     */   private AsyncContext asyncContext;
/*  51 */   private AtomicBoolean asyncCompleted = new AtomicBoolean();
/*     */   
/*  53 */   private final List<Runnable> timeoutHandlers = new ArrayList<>();
/*     */   
/*  55 */   private final List<Consumer<Throwable>> exceptionHandlers = new ArrayList<>();
/*     */   
/*  57 */   private final List<Runnable> completionHandlers = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StandardServletAsyncWebRequest(HttpServletRequest request, HttpServletResponse response) {
/*  66 */     super(request, response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTimeout(Long timeout) {
/*  76 */     Assert.state(!isAsyncStarted(), "Cannot change the timeout with concurrent handling in progress");
/*  77 */     this.timeout = timeout;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addTimeoutHandler(Runnable timeoutHandler) {
/*  82 */     this.timeoutHandlers.add(timeoutHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addErrorHandler(Consumer<Throwable> exceptionHandler) {
/*  87 */     this.exceptionHandlers.add(exceptionHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addCompletionHandler(Runnable runnable) {
/*  92 */     this.completionHandlers.add(runnable);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAsyncStarted() {
/*  97 */     return (this.asyncContext != null && getRequest().isAsyncStarted());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAsyncComplete() {
/* 107 */     return this.asyncCompleted.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void startAsync() {
/* 112 */     Assert.state(getRequest().isAsyncSupported(), "Async support must be enabled on a servlet and for all filters involved in async request processing. This is done in Java code using the Servlet API or by adding \"<async-supported>true</async-supported>\" to servlet and filter declarations in web.xml.");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 117 */     Assert.state(!isAsyncComplete(), "Async processing has already completed");
/*     */     
/* 119 */     if (isAsyncStarted()) {
/*     */       return;
/*     */     }
/* 122 */     this.asyncContext = getRequest().startAsync((ServletRequest)getRequest(), (ServletResponse)getResponse());
/* 123 */     this.asyncContext.addListener(this);
/* 124 */     if (this.timeout != null) {
/* 125 */       this.asyncContext.setTimeout(this.timeout.longValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispatch() {
/* 131 */     Assert.notNull(this.asyncContext, "Cannot dispatch without an AsyncContext");
/* 132 */     this.asyncContext.dispatch();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onStartAsync(AsyncEvent event) throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onError(AsyncEvent event) throws IOException {
/* 146 */     this.exceptionHandlers.forEach(consumer -> consumer.accept(event.getThrowable()));
/*     */   }
/*     */ 
/*     */   
/*     */   public void onTimeout(AsyncEvent event) throws IOException {
/* 151 */     this.timeoutHandlers.forEach(Runnable::run);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onComplete(AsyncEvent event) throws IOException {
/* 156 */     this.completionHandlers.forEach(Runnable::run);
/* 157 */     this.asyncContext = null;
/* 158 */     this.asyncCompleted.set(true);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/async/StandardServletAsyncWebRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */