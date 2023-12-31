/*     */ package org.springframework.remoting.httpinvoker;
/*     */ 
/*     */ import org.aopalliance.intercept.MethodInterceptor;
/*     */ import org.aopalliance.intercept.MethodInvocation;
/*     */ import org.springframework.aop.support.AopUtils;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.remoting.RemoteAccessException;
/*     */ import org.springframework.remoting.RemoteConnectFailureException;
/*     */ import org.springframework.remoting.RemoteInvocationFailureException;
/*     */ import org.springframework.remoting.support.RemoteInvocation;
/*     */ import org.springframework.remoting.support.RemoteInvocationBasedAccessor;
/*     */ import org.springframework.remoting.support.RemoteInvocationResult;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class HttpInvokerClientInterceptor
/*     */   extends RemoteInvocationBasedAccessor
/*     */   implements MethodInterceptor, HttpInvokerClientConfiguration
/*     */ {
/*     */   @Nullable
/*     */   private String codebaseUrl;
/*     */   @Nullable
/*     */   private HttpInvokerRequestExecutor httpInvokerRequestExecutor;
/*     */   
/*     */   public void setCodebaseUrl(@Nullable String codebaseUrl) {
/*  98 */     this.codebaseUrl = codebaseUrl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getCodebaseUrl() {
/* 107 */     return this.codebaseUrl;
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
/*     */   public void setHttpInvokerRequestExecutor(HttpInvokerRequestExecutor httpInvokerRequestExecutor) {
/* 120 */     this.httpInvokerRequestExecutor = httpInvokerRequestExecutor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpInvokerRequestExecutor getHttpInvokerRequestExecutor() {
/* 129 */     if (this.httpInvokerRequestExecutor == null) {
/* 130 */       SimpleHttpInvokerRequestExecutor executor = new SimpleHttpInvokerRequestExecutor();
/* 131 */       executor.setBeanClassLoader(getBeanClassLoader());
/* 132 */       this.httpInvokerRequestExecutor = executor;
/*     */     } 
/* 134 */     return this.httpInvokerRequestExecutor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 139 */     super.afterPropertiesSet();
/*     */ 
/*     */     
/* 142 */     getHttpInvokerRequestExecutor();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object invoke(MethodInvocation methodInvocation) throws Throwable {
/*     */     RemoteInvocationResult result;
/* 149 */     if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
/* 150 */       return "HTTP invoker proxy for service URL [" + getServiceUrl() + "]";
/*     */     }
/*     */     
/* 153 */     RemoteInvocation invocation = createRemoteInvocation(methodInvocation);
/*     */ 
/*     */     
/*     */     try {
/* 157 */       result = executeRequest(invocation, methodInvocation);
/*     */     }
/* 159 */     catch (Throwable ex) {
/* 160 */       RemoteAccessException rae = convertHttpInvokerAccessException(ex);
/* 161 */       throw (rae != null) ? rae : ex;
/*     */     } 
/*     */     
/*     */     try {
/* 165 */       return recreateRemoteInvocationResult(result);
/*     */     }
/* 167 */     catch (Throwable ex) {
/* 168 */       if (result.hasInvocationTargetException()) {
/* 169 */         throw ex;
/*     */       }
/*     */       
/* 172 */       throw new RemoteInvocationFailureException("Invocation of method [" + methodInvocation.getMethod() + "] failed in HTTP invoker remote service at [" + 
/* 173 */           getServiceUrl() + "]", ex);
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
/*     */   protected RemoteInvocationResult executeRequest(RemoteInvocation invocation, MethodInvocation originalInvocation) throws Exception {
/* 191 */     return executeRequest(invocation);
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
/*     */   protected RemoteInvocationResult executeRequest(RemoteInvocation invocation) throws Exception {
/* 209 */     return getHttpInvokerRequestExecutor().executeRequest(this, invocation);
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
/*     */   protected RemoteAccessException convertHttpInvokerAccessException(Throwable ex) {
/* 221 */     if (ex instanceof java.net.ConnectException) {
/* 222 */       return (RemoteAccessException)new RemoteConnectFailureException("Could not connect to HTTP invoker remote service at [" + 
/* 223 */           getServiceUrl() + "]", ex);
/*     */     }
/*     */     
/* 226 */     if (ex instanceof ClassNotFoundException || ex instanceof NoClassDefFoundError || ex instanceof java.io.InvalidClassException)
/*     */     {
/* 228 */       return new RemoteAccessException("Could not deserialize result from HTTP invoker remote service [" + 
/* 229 */           getServiceUrl() + "]", ex);
/*     */     }
/*     */     
/* 232 */     if (ex instanceof Exception) {
/* 233 */       return new RemoteAccessException("Could not access HTTP invoker remote service at [" + 
/* 234 */           getServiceUrl() + "]", ex);
/*     */     }
/*     */ 
/*     */     
/* 238 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/httpinvoker/HttpInvokerClientInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */