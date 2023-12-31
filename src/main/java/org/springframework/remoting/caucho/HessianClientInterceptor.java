/*     */ package org.springframework.remoting.caucho;
/*     */ 
/*     */ import com.caucho.hessian.client.HessianConnectionFactory;
/*     */ import com.caucho.hessian.client.HessianProxyFactory;
/*     */ import com.caucho.hessian.io.SerializerFactory;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.UndeclaredThrowableException;
/*     */ import java.net.MalformedURLException;
/*     */ import org.aopalliance.intercept.MethodInterceptor;
/*     */ import org.aopalliance.intercept.MethodInvocation;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.remoting.RemoteAccessException;
/*     */ import org.springframework.remoting.RemoteConnectFailureException;
/*     */ import org.springframework.remoting.RemoteLookupFailureException;
/*     */ import org.springframework.remoting.RemoteProxyFailureException;
/*     */ import org.springframework.remoting.support.UrlBasedRemoteAccessor;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class HessianClientInterceptor
/*     */   extends UrlBasedRemoteAccessor
/*     */   implements MethodInterceptor
/*     */ {
/*  71 */   private HessianProxyFactory proxyFactory = new HessianProxyFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Object hessianProxy;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProxyFactory(@Nullable HessianProxyFactory proxyFactory) {
/*  84 */     this.proxyFactory = (proxyFactory != null) ? proxyFactory : new HessianProxyFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSerializerFactory(SerializerFactory serializerFactory) {
/*  94 */     this.proxyFactory.setSerializerFactory(serializerFactory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSendCollectionType(boolean sendCollectionType) {
/* 102 */     this.proxyFactory.getSerializerFactory().setSendCollectionType(sendCollectionType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowNonSerializable(boolean allowNonSerializable) {
/* 110 */     this.proxyFactory.getSerializerFactory().setAllowNonSerializable(allowNonSerializable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOverloadEnabled(boolean overloadEnabled) {
/* 119 */     this.proxyFactory.setOverloadEnabled(overloadEnabled);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUsername(String username) {
/* 129 */     this.proxyFactory.setUser(username);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPassword(String password) {
/* 139 */     this.proxyFactory.setPassword(password);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDebug(boolean debug) {
/* 148 */     this.proxyFactory.setDebug(debug);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setChunkedPost(boolean chunkedPost) {
/* 156 */     this.proxyFactory.setChunkedPost(chunkedPost);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConnectionFactory(HessianConnectionFactory connectionFactory) {
/* 163 */     this.proxyFactory.setConnectionFactory(connectionFactory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConnectTimeout(long timeout) {
/* 171 */     this.proxyFactory.setConnectTimeout(timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadTimeout(long timeout) {
/* 179 */     this.proxyFactory.setReadTimeout(timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHessian2(boolean hessian2) {
/* 188 */     this.proxyFactory.setHessian2Request(hessian2);
/* 189 */     this.proxyFactory.setHessian2Reply(hessian2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHessian2Request(boolean hessian2) {
/* 198 */     this.proxyFactory.setHessian2Request(hessian2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHessian2Reply(boolean hessian2) {
/* 207 */     this.proxyFactory.setHessian2Reply(hessian2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 213 */     super.afterPropertiesSet();
/* 214 */     prepare();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prepare() throws RemoteLookupFailureException {
/*     */     try {
/* 223 */       this.hessianProxy = createHessianProxy(this.proxyFactory);
/*     */     }
/* 225 */     catch (MalformedURLException ex) {
/* 226 */       throw new RemoteLookupFailureException("Service URL [" + getServiceUrl() + "] is invalid", ex);
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
/*     */   protected Object createHessianProxy(HessianProxyFactory proxyFactory) throws MalformedURLException {
/* 238 */     Assert.notNull(getServiceInterface(), "'serviceInterface' is required");
/* 239 */     return proxyFactory.create(getServiceInterface(), getServiceUrl(), getBeanClassLoader());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object invoke(MethodInvocation invocation) throws Throwable {
/* 246 */     if (this.hessianProxy == null) {
/* 247 */       throw new IllegalStateException("HessianClientInterceptor is not properly initialized - invoke 'prepare' before attempting any operations");
/*     */     }
/*     */ 
/*     */     
/* 251 */     ClassLoader originalClassLoader = overrideThreadContextClassLoader();
/*     */     try {
/* 253 */       return invocation.getMethod().invoke(this.hessianProxy, invocation.getArguments());
/*     */     }
/* 255 */     catch (InvocationTargetException ex) {
/* 256 */       Throwable targetEx = ex.getTargetException();
/*     */       
/* 258 */       if (targetEx instanceof InvocationTargetException) {
/* 259 */         targetEx = ((InvocationTargetException)targetEx).getTargetException();
/*     */       }
/* 261 */       if (targetEx instanceof com.caucho.hessian.client.HessianConnectionException) {
/* 262 */         throw convertHessianAccessException(targetEx);
/*     */       }
/* 264 */       if (targetEx instanceof com.caucho.hessian.HessianException || targetEx instanceof com.caucho.hessian.client.HessianRuntimeException) {
/* 265 */         Throwable cause = targetEx.getCause();
/* 266 */         throw convertHessianAccessException((cause != null) ? cause : targetEx);
/*     */       } 
/* 268 */       if (targetEx instanceof UndeclaredThrowableException) {
/* 269 */         UndeclaredThrowableException utex = (UndeclaredThrowableException)targetEx;
/* 270 */         throw convertHessianAccessException(utex.getUndeclaredThrowable());
/*     */       } 
/*     */       
/* 273 */       throw targetEx;
/*     */     
/*     */     }
/* 276 */     catch (Throwable ex) {
/* 277 */       throw new RemoteProxyFailureException("Failed to invoke Hessian proxy for remote service [" + 
/* 278 */           getServiceUrl() + "]", ex);
/*     */     } finally {
/*     */       
/* 281 */       resetThreadContextClassLoader(originalClassLoader);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected RemoteAccessException convertHessianAccessException(Throwable ex) {
/* 292 */     if (ex instanceof com.caucho.hessian.client.HessianConnectionException || ex instanceof java.net.ConnectException) {
/* 293 */       return (RemoteAccessException)new RemoteConnectFailureException("Cannot connect to Hessian remote service at [" + 
/* 294 */           getServiceUrl() + "]", ex);
/*     */     }
/*     */     
/* 297 */     return new RemoteAccessException("Cannot access Hessian remote service at [" + 
/* 298 */         getServiceUrl() + "]", ex);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/caucho/HessianClientInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */