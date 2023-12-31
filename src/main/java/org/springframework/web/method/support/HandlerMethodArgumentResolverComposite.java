/*     */ package org.springframework.web.method.support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.web.bind.support.WebDataBinderFactory;
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
/*     */ public class HandlerMethodArgumentResolverComposite
/*     */   implements HandlerMethodArgumentResolver
/*     */ {
/*  41 */   private final List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
/*     */   
/*  43 */   private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache = new ConcurrentHashMap<>(256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HandlerMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver resolver) {
/*  51 */     this.argumentResolvers.add(resolver);
/*  52 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HandlerMethodArgumentResolverComposite addResolvers(@Nullable HandlerMethodArgumentResolver... resolvers) {
/*  62 */     if (resolvers != null) {
/*  63 */       Collections.addAll(this.argumentResolvers, resolvers);
/*     */     }
/*  65 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HandlerMethodArgumentResolverComposite addResolvers(@Nullable List<? extends HandlerMethodArgumentResolver> resolvers) {
/*  74 */     if (resolvers != null) {
/*  75 */       this.argumentResolvers.addAll(resolvers);
/*     */     }
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HandlerMethodArgumentResolver> getResolvers() {
/*  84 */     return Collections.unmodifiableList(this.argumentResolvers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  92 */     this.argumentResolvers.clear();
/*  93 */     this.argumentResolverCache.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsParameter(MethodParameter parameter) {
/* 103 */     return (getArgumentResolver(parameter) != null);
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
/*     */   @Nullable
/*     */   public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
/* 117 */     HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
/* 118 */     if (resolver == null) {
/* 119 */       throw new IllegalArgumentException("Unsupported parameter type [" + parameter
/* 120 */           .getParameterType().getName() + "]. supportsParameter should be called first.");
/*     */     }
/* 122 */     return resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
/* 131 */     HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
/* 132 */     if (result == null) {
/* 133 */       for (HandlerMethodArgumentResolver resolver : this.argumentResolvers) {
/* 134 */         if (resolver.supportsParameter(parameter)) {
/* 135 */           result = resolver;
/* 136 */           this.argumentResolverCache.put(parameter, result);
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/* 141 */     return result;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/support/HandlerMethodArgumentResolverComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */