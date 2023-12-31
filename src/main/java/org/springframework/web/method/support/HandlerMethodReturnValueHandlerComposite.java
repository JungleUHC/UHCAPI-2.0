/*     */ package org.springframework.web.method.support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class HandlerMethodReturnValueHandlerComposite
/*     */   implements HandlerMethodReturnValueHandler
/*     */ {
/*  37 */   private final List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HandlerMethodReturnValueHandler> getHandlers() {
/*  44 */     return Collections.unmodifiableList(this.returnValueHandlers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsReturnType(MethodParameter returnType) {
/*  53 */     return (getReturnValueHandler(returnType) != null);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private HandlerMethodReturnValueHandler getReturnValueHandler(MethodParameter returnType) {
/*  58 */     for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
/*  59 */       if (handler.supportsReturnType(returnType)) {
/*  60 */         return handler;
/*     */       }
/*     */     } 
/*  63 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
/*  74 */     HandlerMethodReturnValueHandler handler = selectHandler(returnValue, returnType);
/*  75 */     if (handler == null) {
/*  76 */       throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
/*     */     }
/*  78 */     handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private HandlerMethodReturnValueHandler selectHandler(@Nullable Object value, MethodParameter returnType) {
/*  83 */     boolean isAsyncValue = isAsyncReturnValue(value, returnType);
/*  84 */     for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
/*  85 */       if (isAsyncValue && !(handler instanceof AsyncHandlerMethodReturnValueHandler)) {
/*     */         continue;
/*     */       }
/*  88 */       if (handler.supportsReturnType(returnType)) {
/*  89 */         return handler;
/*     */       }
/*     */     } 
/*  92 */     return null;
/*     */   }
/*     */   
/*     */   private boolean isAsyncReturnValue(@Nullable Object value, MethodParameter returnType) {
/*  96 */     for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
/*  97 */       if (handler instanceof AsyncHandlerMethodReturnValueHandler && ((AsyncHandlerMethodReturnValueHandler)handler)
/*  98 */         .isAsyncReturnValue(value, returnType)) {
/*  99 */         return true;
/*     */       }
/*     */     } 
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HandlerMethodReturnValueHandlerComposite addHandler(HandlerMethodReturnValueHandler handler) {
/* 109 */     this.returnValueHandlers.add(handler);
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HandlerMethodReturnValueHandlerComposite addHandlers(@Nullable List<? extends HandlerMethodReturnValueHandler> handlers) {
/* 119 */     if (handlers != null) {
/* 120 */       this.returnValueHandlers.addAll(handlers);
/*     */     }
/* 122 */     return this;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/support/HandlerMethodReturnValueHandlerComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */