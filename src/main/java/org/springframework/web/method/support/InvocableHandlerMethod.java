/*     */ package org.springframework.web.method.support;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import org.springframework.context.MessageSource;
/*     */ import org.springframework.core.CoroutinesUtils;
/*     */ import org.springframework.core.DefaultParameterNameDiscoverer;
/*     */ import org.springframework.core.KotlinDetector;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.ParameterNameDiscoverer;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.web.bind.support.WebDataBinderFactory;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ import org.springframework.web.method.HandlerMethod;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InvocableHandlerMethod
/*     */   extends HandlerMethod
/*     */ {
/*  49 */   private static final Object[] EMPTY_ARGS = new Object[0];
/*     */ 
/*     */   
/*  52 */   private HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();
/*     */   
/*  54 */   private ParameterNameDiscoverer parameterNameDiscoverer = (ParameterNameDiscoverer)new DefaultParameterNameDiscoverer();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private WebDataBinderFactory dataBinderFactory;
/*     */ 
/*     */ 
/*     */   
/*     */   public InvocableHandlerMethod(HandlerMethod handlerMethod) {
/*  64 */     super(handlerMethod);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InvocableHandlerMethod(Object bean, Method method) {
/*  71 */     super(bean, method);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected InvocableHandlerMethod(Object bean, Method method, @Nullable MessageSource messageSource) {
/*  80 */     super(bean, method, messageSource);
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
/*     */   public InvocableHandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
/*  93 */     super(bean, methodName, parameterTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
/* 102 */     this.resolvers = argumentResolvers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
/* 111 */     this.parameterNameDiscoverer = parameterNameDiscoverer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDataBinderFactory(WebDataBinderFactory dataBinderFactory) {
/* 119 */     this.dataBinderFactory = dataBinderFactory;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
/* 146 */     Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
/* 147 */     if (logger.isTraceEnabled()) {
/* 148 */       logger.trace("Arguments: " + Arrays.toString(args));
/*     */     }
/* 150 */     return doInvoke(args);
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
/*     */   protected Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
/* 162 */     MethodParameter[] parameters = getMethodParameters();
/* 163 */     if (ObjectUtils.isEmpty((Object[])parameters)) {
/* 164 */       return EMPTY_ARGS;
/*     */     }
/*     */     
/* 167 */     Object[] args = new Object[parameters.length];
/* 168 */     for (int i = 0; i < parameters.length; i++) {
/* 169 */       MethodParameter parameter = parameters[i];
/* 170 */       parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
/* 171 */       args[i] = findProvidedArgument(parameter, providedArgs);
/* 172 */       if (args[i] == null) {
/*     */ 
/*     */         
/* 175 */         if (!this.resolvers.supportsParameter(parameter)) {
/* 176 */           throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
/*     */         }
/*     */         try {
/* 179 */           args[i] = this.resolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);
/*     */         }
/* 181 */         catch (Exception ex) {
/*     */           
/* 183 */           if (logger.isDebugEnabled()) {
/* 184 */             String exMsg = ex.getMessage();
/* 185 */             if (exMsg != null && !exMsg.contains(parameter.getExecutable().toGenericString())) {
/* 186 */               logger.debug(formatArgumentError(parameter, exMsg));
/*     */             }
/*     */           } 
/* 189 */           throw ex;
/*     */         } 
/*     */       } 
/* 192 */     }  return args;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Object doInvoke(Object... args) throws Exception {
/* 200 */     Method method = getBridgedMethod();
/*     */     try {
/* 202 */       if (KotlinDetector.isSuspendingFunction(method)) {
/* 203 */         return CoroutinesUtils.invokeSuspendingFunction(method, getBean(), args);
/*     */       }
/* 205 */       return method.invoke(getBean(), args);
/*     */     }
/* 207 */     catch (IllegalArgumentException ex) {
/* 208 */       assertTargetBean(method, getBean(), args);
/* 209 */       String text = (ex.getMessage() != null) ? ex.getMessage() : "Illegal argument";
/* 210 */       throw new IllegalStateException(formatInvokeError(text, args), ex);
/*     */     }
/* 212 */     catch (InvocationTargetException ex) {
/*     */       
/* 214 */       Throwable targetException = ex.getTargetException();
/* 215 */       if (targetException instanceof RuntimeException) {
/* 216 */         throw (RuntimeException)targetException;
/*     */       }
/* 218 */       if (targetException instanceof Error) {
/* 219 */         throw (Error)targetException;
/*     */       }
/* 221 */       if (targetException instanceof Exception) {
/* 222 */         throw (Exception)targetException;
/*     */       }
/*     */       
/* 225 */       throw new IllegalStateException(formatInvokeError("Invocation failure", args), targetException);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/support/InvocableHandlerMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */