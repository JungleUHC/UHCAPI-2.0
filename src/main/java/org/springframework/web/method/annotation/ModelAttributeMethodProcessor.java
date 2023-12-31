/*     */ package org.springframework.web.method.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.Part;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeanInstantiationException;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ import org.springframework.beans.PropertyAccessException;
/*     */ import org.springframework.beans.TypeMismatchException;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.validation.BindException;
/*     */ import org.springframework.validation.BindingResult;
/*     */ import org.springframework.validation.Errors;
/*     */ import org.springframework.validation.SmartValidator;
/*     */ import org.springframework.validation.Validator;
/*     */ import org.springframework.validation.annotation.ValidationAnnotationUtils;
/*     */ import org.springframework.web.bind.WebDataBinder;
/*     */ import org.springframework.web.bind.annotation.ModelAttribute;
/*     */ import org.springframework.web.bind.support.WebDataBinderFactory;
/*     */ import org.springframework.web.bind.support.WebRequestDataBinder;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ import org.springframework.web.context.request.WebRequest;
/*     */ import org.springframework.web.method.support.HandlerMethodArgumentResolver;
/*     */ import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
/*     */ import org.springframework.web.method.support.ModelAndViewContainer;
/*     */ import org.springframework.web.multipart.MultipartFile;
/*     */ import org.springframework.web.multipart.MultipartRequest;
/*     */ import org.springframework.web.multipart.support.StandardServletPartUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ModelAttributeMethodProcessor
/*     */   implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler
/*     */ {
/*  88 */   protected final Log logger = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean annotationNotRequired;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ModelAttributeMethodProcessor(boolean annotationNotRequired) {
/* 100 */     this.annotationNotRequired = annotationNotRequired;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsParameter(MethodParameter parameter) {
/* 111 */     return (parameter.hasParameterAnnotation(ModelAttribute.class) || (this.annotationNotRequired && 
/* 112 */       !BeanUtils.isSimpleProperty(parameter.getParameterType())));
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
/*     */   @Nullable
/*     */   public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
/* 129 */     Assert.state((mavContainer != null), "ModelAttributeMethodProcessor requires ModelAndViewContainer");
/* 130 */     Assert.state((binderFactory != null), "ModelAttributeMethodProcessor requires WebDataBinderFactory");
/*     */     
/* 132 */     String name = ModelFactory.getNameForParameter(parameter);
/* 133 */     ModelAttribute ann = (ModelAttribute)parameter.getParameterAnnotation(ModelAttribute.class);
/* 134 */     if (ann != null) {
/* 135 */       mavContainer.setBinding(name, ann.binding());
/*     */     }
/*     */     
/* 138 */     Object<?> attribute = null;
/* 139 */     BindingResult bindingResult = null;
/*     */     
/* 141 */     if (mavContainer.containsAttribute(name)) {
/* 142 */       attribute = (Object<?>)mavContainer.getModel().get(name);
/*     */     } else {
/*     */ 
/*     */       
/*     */       try {
/* 147 */         attribute = (Object<?>)createAttribute(name, parameter, binderFactory, webRequest);
/*     */       }
/* 149 */       catch (BindException ex) {
/* 150 */         if (isBindExceptionRequired(parameter))
/*     */         {
/* 152 */           throw ex;
/*     */         }
/*     */         
/* 155 */         if (parameter.getParameterType() == Optional.class) {
/* 156 */           attribute = Optional.empty();
/*     */         } else {
/*     */           
/* 159 */           attribute = (Object<?>)ex.getTarget();
/*     */         } 
/* 161 */         bindingResult = ex.getBindingResult();
/*     */       } 
/*     */     } 
/*     */     
/* 165 */     if (bindingResult == null) {
/*     */ 
/*     */       
/* 168 */       WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
/* 169 */       if (binder.getTarget() != null) {
/* 170 */         if (!mavContainer.isBindingDisabled(name)) {
/* 171 */           bindRequestParameters(binder, webRequest);
/*     */         }
/* 173 */         validateIfApplicable(binder, parameter);
/* 174 */         if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
/* 175 */           throw new BindException(binder.getBindingResult());
/*     */         }
/*     */       } 
/*     */       
/* 179 */       if (!parameter.getParameterType().isInstance(attribute)) {
/* 180 */         attribute = (Object<?>)binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
/*     */       }
/* 182 */       bindingResult = binder.getBindingResult();
/*     */     } 
/*     */ 
/*     */     
/* 186 */     Map<String, Object> bindingResultModel = bindingResult.getModel();
/* 187 */     mavContainer.removeAttributes(bindingResultModel);
/* 188 */     mavContainer.addAllAttributes(bindingResultModel);
/*     */     
/* 190 */     return attribute;
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
/*     */   protected Object createAttribute(String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {
/* 216 */     MethodParameter nestedParameter = parameter.nestedIfOptional();
/* 217 */     Class<?> clazz = nestedParameter.getNestedParameterType();
/*     */     
/* 219 */     Constructor<?> ctor = BeanUtils.getResolvableConstructor(clazz);
/* 220 */     Object attribute = constructAttribute(ctor, attributeName, parameter, binderFactory, webRequest);
/* 221 */     if (parameter != nestedParameter) {
/* 222 */       attribute = Optional.of(attribute);
/*     */     }
/* 224 */     return attribute;
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
/*     */   protected Object constructAttribute(Constructor<?> ctor, String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {
/* 245 */     if (ctor.getParameterCount() == 0)
/*     */     {
/* 247 */       return BeanUtils.instantiateClass(ctor, new Object[0]);
/*     */     }
/*     */ 
/*     */     
/* 251 */     String[] paramNames = BeanUtils.getParameterNames(ctor);
/* 252 */     Class<?>[] paramTypes = ctor.getParameterTypes();
/* 253 */     Object[] args = new Object[paramTypes.length];
/* 254 */     WebDataBinder binder = binderFactory.createBinder(webRequest, null, attributeName);
/* 255 */     String fieldDefaultPrefix = binder.getFieldDefaultPrefix();
/* 256 */     String fieldMarkerPrefix = binder.getFieldMarkerPrefix();
/* 257 */     boolean bindingFailure = false;
/* 258 */     Set<String> failedParams = new HashSet<>(4);
/*     */     
/* 260 */     for (int i = 0; i < paramNames.length; i++) {
/* 261 */       String paramName = paramNames[i];
/* 262 */       Class<?> paramType = paramTypes[i];
/* 263 */       Object value = webRequest.getParameterValues(paramName);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 268 */       if (ObjectUtils.isArray(value) && Array.getLength(value) == 1) {
/* 269 */         value = Array.get(value, 0);
/*     */       }
/*     */       
/* 272 */       if (value == null) {
/* 273 */         if (fieldDefaultPrefix != null) {
/* 274 */           value = webRequest.getParameter(fieldDefaultPrefix + paramName);
/*     */         }
/* 276 */         if (value == null) {
/* 277 */           if (fieldMarkerPrefix != null && webRequest.getParameter(fieldMarkerPrefix + paramName) != null) {
/* 278 */             value = binder.getEmptyValue(paramType);
/*     */           } else {
/*     */             
/* 281 */             value = resolveConstructorArgument(paramName, paramType, webRequest);
/*     */           } 
/*     */         }
/*     */       } 
/*     */       
/*     */       try {
/* 287 */         MethodParameter methodParam = new FieldAwareConstructorParameter(ctor, i, paramName);
/* 288 */         if (value == null && methodParam.isOptional()) {
/* 289 */           args[i] = (methodParam.getParameterType() == Optional.class) ? Optional.empty() : null;
/*     */         } else {
/*     */           
/* 292 */           args[i] = binder.convertIfNecessary(value, paramType, methodParam);
/*     */         }
/*     */       
/* 295 */       } catch (TypeMismatchException ex) {
/* 296 */         ex.initPropertyName(paramName);
/* 297 */         args[i] = null;
/* 298 */         failedParams.add(paramName);
/* 299 */         binder.getBindingResult().recordFieldValue(paramName, paramType, value);
/* 300 */         binder.getBindingErrorProcessor().processPropertyAccessException((PropertyAccessException)ex, binder.getBindingResult());
/* 301 */         bindingFailure = true;
/*     */       } 
/*     */     } 
/*     */     
/* 305 */     if (bindingFailure) {
/* 306 */       BindingResult result = binder.getBindingResult();
/* 307 */       for (int j = 0; j < paramNames.length; j++) {
/* 308 */         String paramName = paramNames[j];
/* 309 */         if (!failedParams.contains(paramName)) {
/* 310 */           Object value = args[j];
/* 311 */           result.recordFieldValue(paramName, paramTypes[j], value);
/* 312 */           validateValueIfApplicable(binder, parameter, ctor.getDeclaringClass(), paramName, value);
/*     */         } 
/*     */       } 
/* 315 */       if (!parameter.isOptional()) {
/*     */         try {
/* 317 */           final Object target = BeanUtils.instantiateClass(ctor, args);
/* 318 */           throw new BindException(result)
/*     */             {
/*     */               public Object getTarget() {
/* 321 */                 return target;
/*     */               }
/*     */             };
/*     */         }
/* 325 */         catch (BeanInstantiationException beanInstantiationException) {}
/*     */       }
/*     */ 
/*     */       
/* 329 */       throw new BindException(result);
/*     */     } 
/*     */     
/* 332 */     return BeanUtils.instantiateClass(ctor, args);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
/* 341 */     ((WebRequestDataBinder)binder).bind((WebRequest)request);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object resolveConstructorArgument(String paramName, Class<?> paramType, NativeWebRequest request) throws Exception {
/* 348 */     MultipartRequest multipartRequest = (MultipartRequest)request.getNativeRequest(MultipartRequest.class);
/* 349 */     if (multipartRequest != null) {
/* 350 */       List<MultipartFile> files = multipartRequest.getFiles(paramName);
/* 351 */       if (!files.isEmpty()) {
/* 352 */         return (files.size() == 1) ? files.get(0) : files;
/*     */       }
/*     */     }
/* 355 */     else if (StringUtils.startsWithIgnoreCase(request
/* 356 */         .getHeader("Content-Type"), "multipart/form-data")) {
/* 357 */       HttpServletRequest servletRequest = (HttpServletRequest)request.getNativeRequest(HttpServletRequest.class);
/* 358 */       if (servletRequest != null && HttpMethod.POST.matches(servletRequest.getMethod())) {
/* 359 */         List<Part> parts = StandardServletPartUtils.getParts(servletRequest, paramName);
/* 360 */         if (!parts.isEmpty()) {
/* 361 */           return (parts.size() == 1) ? parts.get(0) : parts;
/*     */         }
/*     */       } 
/*     */     } 
/* 365 */     return null;
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
/*     */   protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
/* 379 */     for (Annotation ann : parameter.getParameterAnnotations()) {
/* 380 */       Object[] validationHints = ValidationAnnotationUtils.determineValidationHints(ann);
/* 381 */       if (validationHints != null) {
/* 382 */         binder.validate(validationHints);
/*     */         break;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void validateValueIfApplicable(WebDataBinder binder, MethodParameter parameter, Class<?> targetType, String fieldName, @Nullable Object value) {
/* 405 */     for (Annotation ann : parameter.getParameterAnnotations()) {
/* 406 */       Object[] validationHints = ValidationAnnotationUtils.determineValidationHints(ann);
/* 407 */       if (validationHints != null) {
/* 408 */         for (Validator validator : binder.getValidators()) {
/* 409 */           if (validator instanceof SmartValidator) {
/*     */             try {
/* 411 */               ((SmartValidator)validator).validateValue(targetType, fieldName, value, (Errors)binder
/* 412 */                   .getBindingResult(), validationHints);
/*     */             }
/* 414 */             catch (IllegalArgumentException illegalArgumentException) {}
/*     */           }
/*     */         } 
/*     */         break;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
/* 433 */     return isBindExceptionRequired(parameter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isBindExceptionRequired(MethodParameter parameter) {
/* 443 */     int i = parameter.getParameterIndex();
/* 444 */     Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
/* 445 */     boolean hasBindingResult = (paramTypes.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]));
/* 446 */     return !hasBindingResult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsReturnType(MethodParameter returnType) {
/* 456 */     return (returnType.hasMethodAnnotation(ModelAttribute.class) || (this.annotationNotRequired && 
/* 457 */       !BeanUtils.isSimpleProperty(returnType.getParameterType())));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
/* 467 */     if (returnValue != null) {
/* 468 */       String name = ModelFactory.getNameForReturnValue(returnValue, returnType);
/* 469 */       mavContainer.addAttribute(name, returnValue);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class FieldAwareConstructorParameter
/*     */     extends MethodParameter
/*     */   {
/*     */     private final String parameterName;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private volatile Annotation[] combinedAnnotations;
/*     */ 
/*     */     
/*     */     public FieldAwareConstructorParameter(Constructor<?> constructor, int parameterIndex, String parameterName) {
/* 486 */       super(constructor, parameterIndex);
/* 487 */       this.parameterName = parameterName;
/*     */     }
/*     */ 
/*     */     
/*     */     public Annotation[] getParameterAnnotations() {
/* 492 */       Annotation[] anns = this.combinedAnnotations;
/* 493 */       if (anns == null) {
/* 494 */         anns = super.getParameterAnnotations();
/*     */         try {
/* 496 */           Field field = getDeclaringClass().getDeclaredField(this.parameterName);
/* 497 */           Annotation[] fieldAnns = field.getAnnotations();
/* 498 */           if (fieldAnns.length > 0) {
/* 499 */             List<Annotation> merged = new ArrayList<>(anns.length + fieldAnns.length);
/* 500 */             merged.addAll(Arrays.asList(anns));
/* 501 */             for (Annotation fieldAnn : fieldAnns) {
/* 502 */               boolean existingType = false;
/* 503 */               for (Annotation ann : anns) {
/* 504 */                 if (ann.annotationType() == fieldAnn.annotationType()) {
/* 505 */                   existingType = true;
/*     */                   break;
/*     */                 } 
/*     */               } 
/* 509 */               if (!existingType) {
/* 510 */                 merged.add(fieldAnn);
/*     */               }
/*     */             } 
/* 513 */             anns = merged.<Annotation>toArray(new Annotation[0]);
/*     */           }
/*     */         
/* 516 */         } catch (NoSuchFieldException|SecurityException noSuchFieldException) {}
/*     */ 
/*     */         
/* 519 */         this.combinedAnnotations = anns;
/*     */       } 
/* 521 */       return anns;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getParameterName() {
/* 526 */       return this.parameterName;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/annotation/ModelAttributeMethodProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */