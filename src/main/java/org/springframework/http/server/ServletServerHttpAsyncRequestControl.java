/*     */ package org.springframework.http.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import javax.servlet.AsyncContext;
/*     */ import javax.servlet.AsyncEvent;
/*     */ import javax.servlet.AsyncListener;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
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
/*     */ public class ServletServerHttpAsyncRequestControl
/*     */   implements ServerHttpAsyncRequestControl, AsyncListener
/*     */ {
/*     */   private static final long NO_TIMEOUT_VALUE = -9223372036854775808L;
/*     */   private final ServletServerHttpRequest request;
/*     */   private final ServletServerHttpResponse response;
/*     */   @Nullable
/*     */   private AsyncContext asyncContext;
/*  49 */   private AtomicBoolean asyncCompleted = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletServerHttpAsyncRequestControl(ServletServerHttpRequest request, ServletServerHttpResponse response) {
/*  58 */     Assert.notNull(request, "request is required");
/*  59 */     Assert.notNull(response, "response is required");
/*     */     
/*  61 */     Assert.isTrue(request.getServletRequest().isAsyncSupported(), "Async support must be enabled on a servlet and for all filters involved in async request processing. This is done in Java code using the Servlet API or by adding \"<async-supported>true</async-supported>\" to servlet and filter declarations in web.xml. Also you must use a Servlet 3.0+ container");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  67 */     this.request = request;
/*  68 */     this.response = response;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStarted() {
/*  74 */     return (this.asyncContext != null && this.request.getServletRequest().isAsyncStarted());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCompleted() {
/*  79 */     return this.asyncCompleted.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void start() {
/*  84 */     start(Long.MIN_VALUE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void start(long timeout) {
/*  89 */     Assert.state(!isCompleted(), "Async processing has already completed");
/*  90 */     if (isStarted()) {
/*     */       return;
/*     */     }
/*     */     
/*  94 */     HttpServletRequest servletRequest = this.request.getServletRequest();
/*  95 */     HttpServletResponse servletResponse = this.response.getServletResponse();
/*     */     
/*  97 */     this.asyncContext = servletRequest.startAsync((ServletRequest)servletRequest, (ServletResponse)servletResponse);
/*  98 */     this.asyncContext.addListener(this);
/*     */     
/* 100 */     if (timeout != Long.MIN_VALUE) {
/* 101 */       this.asyncContext.setTimeout(timeout);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void complete() {
/* 107 */     if (this.asyncContext != null && isStarted() && !isCompleted()) {
/* 108 */       this.asyncContext.complete();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onComplete(AsyncEvent event) throws IOException {
/* 119 */     this.asyncContext = null;
/* 120 */     this.asyncCompleted.set(true);
/*     */   }
/*     */   
/*     */   public void onStartAsync(AsyncEvent event) throws IOException {}
/*     */   
/*     */   public void onError(AsyncEvent event) throws IOException {}
/*     */   
/*     */   public void onTimeout(AsyncEvent event) throws IOException {}
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/ServletServerHttpAsyncRequestControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */