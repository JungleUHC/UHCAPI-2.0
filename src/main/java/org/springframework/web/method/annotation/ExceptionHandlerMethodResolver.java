/*     */ package org.springframework.web.method.annotation;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.springframework.core.ExceptionDepthComparator;
/*     */ import org.springframework.core.MethodIntrospector;
/*     */ import org.springframework.core.annotation.AnnotatedElementUtils;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ConcurrentReferenceHashMap;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ import org.springframework.web.bind.annotation.ExceptionHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExceptionHandlerMethodResolver
/*     */ {
/*     */   public static final ReflectionUtils.MethodFilter EXCEPTION_HANDLER_METHODS;
/*     */   private static final Method NO_MATCHING_EXCEPTION_HANDLER_METHOD;
/*     */   
/*     */   static {
/*  50 */     EXCEPTION_HANDLER_METHODS = (method -> AnnotatedElementUtils.hasAnnotation(method, ExceptionHandler.class));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  58 */       NO_MATCHING_EXCEPTION_HANDLER_METHOD = ExceptionHandlerMethodResolver.class.getDeclaredMethod("noMatchingExceptionHandler", new Class[0]);
/*     */     }
/*  60 */     catch (NoSuchMethodException ex) {
/*  61 */       throw new IllegalStateException("Expected method not found: " + ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*  66 */   private final Map<Class<? extends Throwable>, Method> mappedMethods = new HashMap<>(16);
/*     */   
/*  68 */   private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = (Map<Class<? extends Throwable>, Method>)new ConcurrentReferenceHashMap(16);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExceptionHandlerMethodResolver(Class<?> handlerType) {
/*  76 */     for (Method method : MethodIntrospector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS)) {
/*  77 */       for (Class<? extends Throwable> exceptionType : detectExceptionMappings(method)) {
/*  78 */         addExceptionMapping(exceptionType, method);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
/*  90 */     List<Class<? extends Throwable>> result = new ArrayList<>();
/*  91 */     detectAnnotationExceptionMappings(method, result);
/*  92 */     if (result.isEmpty()) {
/*  93 */       for (Class<?> paramType : method.getParameterTypes()) {
/*  94 */         if (Throwable.class.isAssignableFrom(paramType)) {
/*  95 */           result.add(paramType);
/*     */         }
/*     */       } 
/*     */     }
/*  99 */     if (result.isEmpty()) {
/* 100 */       throw new IllegalStateException("No exception types mapped to " + method);
/*     */     }
/* 102 */     return result;
/*     */   }
/*     */   
/*     */   private void detectAnnotationExceptionMappings(Method method, List<Class<? extends Throwable>> result) {
/* 106 */     ExceptionHandler ann = (ExceptionHandler)AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class);
/* 107 */     Assert.state((ann != null), "No ExceptionHandler annotation");
/* 108 */     result.addAll(Arrays.asList((Class<? extends Throwable>[])ann.value()));
/*     */   }
/*     */   
/*     */   private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
/* 112 */     Method oldMethod = this.mappedMethods.put(exceptionType, method);
/* 113 */     if (oldMethod != null && !oldMethod.equals(method)) {
/* 114 */       throw new IllegalStateException("Ambiguous @ExceptionHandler method mapped for [" + exceptionType + "]: {" + oldMethod + ", " + method + "}");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasExceptionMappings() {
/* 123 */     return !this.mappedMethods.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Method resolveMethod(Exception exception) {
/* 134 */     return resolveMethodByThrowable(exception);
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
/*     */   public Method resolveMethodByThrowable(Throwable exception) {
/* 146 */     Method method = resolveMethodByExceptionType((Class)exception.getClass());
/* 147 */     if (method == null) {
/* 148 */       Throwable cause = exception.getCause();
/* 149 */       if (cause != null) {
/* 150 */         method = resolveMethodByThrowable(cause);
/*     */       }
/*     */     } 
/* 153 */     return method;
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
/*     */   public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
/* 165 */     Method method = this.exceptionLookupCache.get(exceptionType);
/* 166 */     if (method == null) {
/* 167 */       method = getMappedMethod(exceptionType);
/* 168 */       this.exceptionLookupCache.put(exceptionType, method);
/*     */     } 
/* 170 */     return (method != NO_MATCHING_EXCEPTION_HANDLER_METHOD) ? method : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
/* 178 */     List<Class<? extends Throwable>> matches = new ArrayList<>();
/* 179 */     for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
/* 180 */       if (mappedException.isAssignableFrom(exceptionType)) {
/* 181 */         matches.add(mappedException);
/*     */       }
/*     */     } 
/* 184 */     if (!matches.isEmpty()) {
/* 185 */       if (matches.size() > 1) {
/* 186 */         matches.sort((Comparator<? super Class<? extends Throwable>>)new ExceptionDepthComparator(exceptionType));
/*     */       }
/* 188 */       return this.mappedMethods.get(matches.get(0));
/*     */     } 
/*     */     
/* 191 */     return NO_MATCHING_EXCEPTION_HANDLER_METHOD;
/*     */   }
/*     */   
/*     */   private void noMatchingExceptionHandler() {}
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/annotation/ExceptionHandlerMethodResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */