/*     */ package org.springframework.web.method.annotation;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.servlet.ServletException;
/*     */ import org.springframework.beans.ConversionNotSupportedException;
/*     */ import org.springframework.beans.TypeMismatchException;
/*     */ import org.springframework.beans.factory.config.BeanExpressionContext;
/*     */ import org.springframework.beans.factory.config.BeanExpressionResolver;
/*     */ import org.springframework.beans.factory.config.ConfigurableBeanFactory;
/*     */ import org.springframework.beans.factory.config.Scope;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.web.bind.ServletRequestBindingException;
/*     */ import org.springframework.web.bind.WebDataBinder;
/*     */ import org.springframework.web.bind.support.WebDataBinderFactory;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ import org.springframework.web.context.request.RequestScope;
/*     */ import org.springframework.web.method.support.HandlerMethodArgumentResolver;
/*     */ import org.springframework.web.method.support.ModelAndViewContainer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractNamedValueMethodArgumentResolver
/*     */   implements HandlerMethodArgumentResolver
/*     */ {
/*     */   @Nullable
/*     */   private final ConfigurableBeanFactory configurableBeanFactory;
/*     */   @Nullable
/*     */   private final BeanExpressionContext expressionContext;
/*  73 */   private final Map<MethodParameter, NamedValueInfo> namedValueInfoCache = new ConcurrentHashMap<>(256);
/*     */ 
/*     */   
/*     */   public AbstractNamedValueMethodArgumentResolver() {
/*  77 */     this.configurableBeanFactory = null;
/*  78 */     this.expressionContext = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractNamedValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
/*  88 */     this.configurableBeanFactory = beanFactory;
/*  89 */     this.expressionContext = (beanFactory != null) ? new BeanExpressionContext(beanFactory, (Scope)new RequestScope()) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
/*  99 */     NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);
/* 100 */     MethodParameter nestedParameter = parameter.nestedIfOptional();
/*     */     
/* 102 */     Object resolvedName = resolveEmbeddedValuesAndExpressions(namedValueInfo.name);
/* 103 */     if (resolvedName == null) {
/* 104 */       throw new IllegalArgumentException("Specified name must not resolve to null: [" + namedValueInfo
/* 105 */           .name + "]");
/*     */     }
/*     */     
/* 108 */     Object arg = resolveName(resolvedName.toString(), nestedParameter, webRequest);
/* 109 */     if (arg == null) {
/* 110 */       if (namedValueInfo.defaultValue != null) {
/* 111 */         arg = resolveEmbeddedValuesAndExpressions(namedValueInfo.defaultValue);
/*     */       }
/* 113 */       else if (namedValueInfo.required && !nestedParameter.isOptional()) {
/* 114 */         handleMissingValue(namedValueInfo.name, nestedParameter, webRequest);
/*     */       } 
/* 116 */       arg = handleNullValue(namedValueInfo.name, arg, nestedParameter.getNestedParameterType());
/*     */     }
/* 118 */     else if ("".equals(arg) && namedValueInfo.defaultValue != null) {
/* 119 */       arg = resolveEmbeddedValuesAndExpressions(namedValueInfo.defaultValue);
/*     */     } 
/*     */     
/* 122 */     if (binderFactory != null) {
/* 123 */       WebDataBinder binder = binderFactory.createBinder(webRequest, null, namedValueInfo.name);
/*     */       try {
/* 125 */         arg = binder.convertIfNecessary(arg, parameter.getParameterType(), parameter);
/*     */       }
/* 127 */       catch (ConversionNotSupportedException ex) {
/* 128 */         throw new MethodArgumentConversionNotSupportedException(arg, ex.getRequiredType(), namedValueInfo
/* 129 */             .name, parameter, ex.getCause());
/*     */       }
/* 131 */       catch (TypeMismatchException ex) {
/* 132 */         throw new MethodArgumentTypeMismatchException(arg, ex.getRequiredType(), namedValueInfo
/* 133 */             .name, parameter, ex.getCause());
/*     */       } 
/*     */       
/* 136 */       if (arg == null && namedValueInfo.defaultValue == null && namedValueInfo
/* 137 */         .required && !nestedParameter.isOptional()) {
/* 138 */         handleMissingValueAfterConversion(namedValueInfo.name, nestedParameter, webRequest);
/*     */       }
/*     */     } 
/*     */     
/* 142 */     handleResolvedValue(arg, namedValueInfo.name, parameter, mavContainer, webRequest);
/*     */     
/* 144 */     return arg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
/* 151 */     NamedValueInfo namedValueInfo = this.namedValueInfoCache.get(parameter);
/* 152 */     if (namedValueInfo == null) {
/* 153 */       namedValueInfo = createNamedValueInfo(parameter);
/* 154 */       namedValueInfo = updateNamedValueInfo(parameter, namedValueInfo);
/* 155 */       this.namedValueInfoCache.put(parameter, namedValueInfo);
/*     */     } 
/* 157 */     return namedValueInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract NamedValueInfo createNamedValueInfo(MethodParameter paramMethodParameter);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private NamedValueInfo updateNamedValueInfo(MethodParameter parameter, NamedValueInfo info) {
/* 172 */     String name = info.name;
/* 173 */     if (info.name.isEmpty()) {
/* 174 */       name = parameter.getParameterName();
/* 175 */       if (name == null) {
/* 176 */         throw new IllegalArgumentException("Name for argument of type [" + parameter
/* 177 */             .getNestedParameterType().getName() + "] not specified, and parameter name information not found in class file either.");
/*     */       }
/*     */     } 
/*     */     
/* 181 */     String defaultValue = "\n\t\t\n\t\t\n\n\t\t\t\t\n".equals(info.defaultValue) ? null : info.defaultValue;
/* 182 */     return new NamedValueInfo(name, info.required, defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Object resolveEmbeddedValuesAndExpressions(String value) {
/* 191 */     if (this.configurableBeanFactory == null || this.expressionContext == null) {
/* 192 */       return value;
/*     */     }
/* 194 */     String placeholdersResolved = this.configurableBeanFactory.resolveEmbeddedValue(value);
/* 195 */     BeanExpressionResolver exprResolver = this.configurableBeanFactory.getBeanExpressionResolver();
/* 196 */     if (exprResolver == null) {
/* 197 */       return value;
/*     */     }
/* 199 */     return exprResolver.evaluate(placeholdersResolved, this.expressionContext);
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
/*     */   @Nullable
/*     */   protected abstract Object resolveName(String paramString, MethodParameter paramMethodParameter, NativeWebRequest paramNativeWebRequest) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
/* 226 */     handleMissingValue(name, parameter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
/* 236 */     throw new ServletRequestBindingException("Missing argument '" + name + "' for method parameter of type " + parameter
/* 237 */         .getNestedParameterType().getSimpleName());
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
/*     */   protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
/* 250 */     handleMissingValue(name, parameter, request);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Object handleNullValue(String name, @Nullable Object value, Class<?> paramType) {
/* 258 */     if (value == null) {
/* 259 */       if (boolean.class.equals(paramType)) {
/* 260 */         return Boolean.FALSE;
/*     */       }
/* 262 */       if (paramType.isPrimitive()) {
/* 263 */         throw new IllegalStateException("Optional " + paramType.getSimpleName() + " parameter '" + name + "' is present but cannot be translated into a null value due to being declared as a primitive type. Consider declaring it as object wrapper for the corresponding primitive type.");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 268 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleResolvedValue(@Nullable Object arg, String name, MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static class NamedValueInfo
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */ 
/*     */     
/*     */     private final boolean required;
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private final String defaultValue;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public NamedValueInfo(String name, boolean required, @Nullable String defaultValue) {
/* 297 */       this.name = name;
/* 298 */       this.required = required;
/* 299 */       this.defaultValue = defaultValue;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/annotation/AbstractNamedValueMethodArgumentResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */