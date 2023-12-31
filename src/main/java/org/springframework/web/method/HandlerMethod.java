/*     */ package org.springframework.web.method;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.StringJoiner;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.IntStream;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.factory.BeanFactory;
/*     */ import org.springframework.context.MessageSource;
/*     */ import org.springframework.context.i18n.LocaleContextHolder;
/*     */ import org.springframework.core.BridgeMethodResolver;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.annotation.AnnotatedElementUtils;
/*     */ import org.springframework.core.annotation.SynthesizingMethodParameter;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.lang.NonNull;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.bind.annotation.ResponseStatus;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HandlerMethod
/*     */ {
/*  69 */   protected static final Log logger = LogFactory.getLog(HandlerMethod.class);
/*     */ 
/*     */   
/*     */   private final Object bean;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final BeanFactory beanFactory;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final MessageSource messageSource;
/*     */   
/*     */   private final Class<?> beanType;
/*     */   
/*     */   private final Method method;
/*     */   
/*     */   private final Method bridgedMethod;
/*     */   
/*     */   private final MethodParameter[] parameters;
/*     */   
/*     */   @Nullable
/*     */   private HttpStatus responseStatus;
/*     */   
/*     */   @Nullable
/*     */   private String responseStatusReason;
/*     */   
/*     */   @Nullable
/*     */   private HandlerMethod resolvedFromHandlerMethod;
/*     */   
/*     */   @Nullable
/*     */   private volatile List<Annotation[][]> interfaceParameterAnnotations;
/*     */   
/*     */   private final String description;
/*     */ 
/*     */   
/*     */   public HandlerMethod(Object bean, Method method) {
/* 106 */     this(bean, method, (MessageSource)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HandlerMethod(Object bean, Method method, @Nullable MessageSource messageSource) {
/* 115 */     Assert.notNull(bean, "Bean is required");
/* 116 */     Assert.notNull(method, "Method is required");
/* 117 */     this.bean = bean;
/* 118 */     this.beanFactory = null;
/* 119 */     this.messageSource = messageSource;
/* 120 */     this.beanType = ClassUtils.getUserClass(bean);
/* 121 */     this.method = method;
/* 122 */     this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
/* 123 */     ReflectionUtils.makeAccessible(this.bridgedMethod);
/* 124 */     this.parameters = initMethodParameters();
/* 125 */     evaluateResponseStatus();
/* 126 */     this.description = initDescription(this.beanType, this.method);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
/* 134 */     Assert.notNull(bean, "Bean is required");
/* 135 */     Assert.notNull(methodName, "Method name is required");
/* 136 */     this.bean = bean;
/* 137 */     this.beanFactory = null;
/* 138 */     this.messageSource = null;
/* 139 */     this.beanType = ClassUtils.getUserClass(bean);
/* 140 */     this.method = bean.getClass().getMethod(methodName, parameterTypes);
/* 141 */     this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(this.method);
/* 142 */     ReflectionUtils.makeAccessible(this.bridgedMethod);
/* 143 */     this.parameters = initMethodParameters();
/* 144 */     evaluateResponseStatus();
/* 145 */     this.description = initDescription(this.beanType, this.method);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HandlerMethod(String beanName, BeanFactory beanFactory, Method method) {
/* 154 */     this(beanName, beanFactory, null, method);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HandlerMethod(String beanName, BeanFactory beanFactory, @Nullable MessageSource messageSource, Method method) {
/* 165 */     Assert.hasText(beanName, "Bean name is required");
/* 166 */     Assert.notNull(beanFactory, "BeanFactory is required");
/* 167 */     Assert.notNull(method, "Method is required");
/* 168 */     this.bean = beanName;
/* 169 */     this.beanFactory = beanFactory;
/* 170 */     this.messageSource = messageSource;
/* 171 */     Class<?> beanType = beanFactory.getType(beanName);
/* 172 */     if (beanType == null) {
/* 173 */       throw new IllegalStateException("Cannot resolve bean type for bean with name '" + beanName + "'");
/*     */     }
/* 175 */     this.beanType = ClassUtils.getUserClass(beanType);
/* 176 */     this.method = method;
/* 177 */     this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
/* 178 */     ReflectionUtils.makeAccessible(this.bridgedMethod);
/* 179 */     this.parameters = initMethodParameters();
/* 180 */     evaluateResponseStatus();
/* 181 */     this.description = initDescription(this.beanType, this.method);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HandlerMethod(HandlerMethod handlerMethod) {
/* 188 */     Assert.notNull(handlerMethod, "HandlerMethod is required");
/* 189 */     this.bean = handlerMethod.bean;
/* 190 */     this.beanFactory = handlerMethod.beanFactory;
/* 191 */     this.messageSource = handlerMethod.messageSource;
/* 192 */     this.beanType = handlerMethod.beanType;
/* 193 */     this.method = handlerMethod.method;
/* 194 */     this.bridgedMethod = handlerMethod.bridgedMethod;
/* 195 */     this.parameters = handlerMethod.parameters;
/* 196 */     this.responseStatus = handlerMethod.responseStatus;
/* 197 */     this.responseStatusReason = handlerMethod.responseStatusReason;
/* 198 */     this.description = handlerMethod.description;
/* 199 */     this.resolvedFromHandlerMethod = handlerMethod.resolvedFromHandlerMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HandlerMethod(HandlerMethod handlerMethod, Object handler) {
/* 206 */     Assert.notNull(handlerMethod, "HandlerMethod is required");
/* 207 */     Assert.notNull(handler, "Handler object is required");
/* 208 */     this.bean = handler;
/* 209 */     this.beanFactory = handlerMethod.beanFactory;
/* 210 */     this.messageSource = handlerMethod.messageSource;
/* 211 */     this.beanType = handlerMethod.beanType;
/* 212 */     this.method = handlerMethod.method;
/* 213 */     this.bridgedMethod = handlerMethod.bridgedMethod;
/* 214 */     this.parameters = handlerMethod.parameters;
/* 215 */     this.responseStatus = handlerMethod.responseStatus;
/* 216 */     this.responseStatusReason = handlerMethod.responseStatusReason;
/* 217 */     this.resolvedFromHandlerMethod = handlerMethod;
/* 218 */     this.description = handlerMethod.description;
/*     */   }
/*     */   
/*     */   private MethodParameter[] initMethodParameters() {
/* 222 */     int count = this.bridgedMethod.getParameterCount();
/* 223 */     MethodParameter[] result = new MethodParameter[count];
/* 224 */     for (int i = 0; i < count; i++) {
/* 225 */       result[i] = (MethodParameter)new HandlerMethodParameter(i);
/*     */     }
/* 227 */     return result;
/*     */   }
/*     */   
/*     */   private void evaluateResponseStatus() {
/* 231 */     ResponseStatus annotation = getMethodAnnotation(ResponseStatus.class);
/* 232 */     if (annotation == null) {
/* 233 */       annotation = (ResponseStatus)AnnotatedElementUtils.findMergedAnnotation(getBeanType(), ResponseStatus.class);
/*     */     }
/* 235 */     if (annotation != null) {
/* 236 */       String reason = annotation.reason();
/*     */       
/* 238 */       String resolvedReason = (StringUtils.hasText(reason) && this.messageSource != null) ? this.messageSource.getMessage(reason, null, reason, LocaleContextHolder.getLocale()) : reason;
/*     */ 
/*     */       
/* 241 */       this.responseStatus = annotation.code();
/* 242 */       this.responseStatusReason = resolvedReason;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String initDescription(Class<?> beanType, Method method) {
/* 247 */     StringJoiner joiner = new StringJoiner(", ", "(", ")");
/* 248 */     for (Class<?> paramType : method.getParameterTypes()) {
/* 249 */       joiner.add(paramType.getSimpleName());
/*     */     }
/* 251 */     return beanType.getName() + "#" + method.getName() + joiner.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getBean() {
/* 259 */     return this.bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Method getMethod() {
/* 266 */     return this.method;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getBeanType() {
/* 275 */     return this.beanType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Method getBridgedMethod() {
/* 283 */     return this.bridgedMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MethodParameter[] getMethodParameters() {
/* 290 */     return this.parameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected HttpStatus getResponseStatus() {
/* 300 */     return this.responseStatus;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String getResponseStatusReason() {
/* 310 */     return this.responseStatusReason;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MethodParameter getReturnType() {
/* 317 */     return (MethodParameter)new HandlerMethodParameter(-1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MethodParameter getReturnValueType(@Nullable Object returnValue) {
/* 324 */     return (MethodParameter)new ReturnValueMethodParameter(returnValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isVoid() {
/* 331 */     return void.class.equals(getReturnType().getParameterType());
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
/*     */   public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
/* 345 */     return (A)AnnotatedElementUtils.findMergedAnnotation(this.method, annotationType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
/* 355 */     return AnnotatedElementUtils.hasAnnotation(this.method, annotationType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public HandlerMethod getResolvedFromHandlerMethod() {
/* 364 */     return this.resolvedFromHandlerMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HandlerMethod createWithResolvedBean() {
/* 372 */     Object handler = this.bean;
/* 373 */     if (this.bean instanceof String) {
/* 374 */       Assert.state((this.beanFactory != null), "Cannot resolve bean name without BeanFactory");
/* 375 */       String beanName = (String)this.bean;
/* 376 */       handler = this.beanFactory.getBean(beanName);
/*     */     } 
/* 378 */     return new HandlerMethod(this, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortLogMessage() {
/* 386 */     return getBeanType().getName() + "#" + this.method.getName() + "[" + this.method
/* 387 */       .getParameterCount() + " args]";
/*     */   }
/*     */ 
/*     */   
/*     */   private List<Annotation[][]> getInterfaceParameterAnnotations() {
/* 392 */     List<Annotation[][]> parameterAnnotations = this.interfaceParameterAnnotations;
/* 393 */     if (parameterAnnotations == null) {
/* 394 */       parameterAnnotations = (List)new ArrayList<>();
/* 395 */       for (Class<?> ifc : (Iterable<Class<?>>)ClassUtils.getAllInterfacesForClassAsSet(this.method.getDeclaringClass())) {
/* 396 */         for (Method candidate : ifc.getMethods()) {
/* 397 */           if (isOverrideFor(candidate)) {
/* 398 */             parameterAnnotations.add(candidate.getParameterAnnotations());
/*     */           }
/*     */         } 
/*     */       } 
/* 402 */       this.interfaceParameterAnnotations = parameterAnnotations;
/*     */     } 
/* 404 */     return parameterAnnotations;
/*     */   }
/*     */   
/*     */   private boolean isOverrideFor(Method candidate) {
/* 408 */     if (!candidate.getName().equals(this.method.getName()) || candidate
/* 409 */       .getParameterCount() != this.method.getParameterCount()) {
/* 410 */       return false;
/*     */     }
/* 412 */     Class<?>[] paramTypes = this.method.getParameterTypes();
/* 413 */     if (Arrays.equals((Object[])candidate.getParameterTypes(), (Object[])paramTypes)) {
/* 414 */       return true;
/*     */     }
/* 416 */     for (int i = 0; i < paramTypes.length; i++) {
/* 417 */       if (paramTypes[i] != 
/* 418 */         ResolvableType.forMethodParameter(candidate, i, this.method.getDeclaringClass()).resolve()) {
/* 419 */         return false;
/*     */       }
/*     */     } 
/* 422 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 428 */     if (this == other) {
/* 429 */       return true;
/*     */     }
/* 431 */     if (!(other instanceof HandlerMethod)) {
/* 432 */       return false;
/*     */     }
/* 434 */     HandlerMethod otherMethod = (HandlerMethod)other;
/* 435 */     return (this.bean.equals(otherMethod.bean) && this.method.equals(otherMethod.method));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 440 */     return this.bean.hashCode() * 31 + this.method.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 445 */     return this.description;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected static Object findProvidedArgument(MethodParameter parameter, @Nullable Object... providedArgs) {
/* 453 */     if (!ObjectUtils.isEmpty(providedArgs)) {
/* 454 */       for (Object providedArg : providedArgs) {
/* 455 */         if (parameter.getParameterType().isInstance(providedArg)) {
/* 456 */           return providedArg;
/*     */         }
/*     */       } 
/*     */     }
/* 460 */     return null;
/*     */   }
/*     */   
/*     */   protected static String formatArgumentError(MethodParameter param, String message) {
/* 464 */     return "Could not resolve parameter [" + param.getParameterIndex() + "] in " + param
/* 465 */       .getExecutable().toGenericString() + (StringUtils.hasText(message) ? (": " + message) : "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void assertTargetBean(Method method, Object targetBean, Object[] args) {
/* 476 */     Class<?> methodDeclaringClass = method.getDeclaringClass();
/* 477 */     Class<?> targetBeanClass = targetBean.getClass();
/* 478 */     if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
/*     */ 
/*     */       
/* 481 */       String text = "The mapped handler method class '" + methodDeclaringClass.getName() + "' is not an instance of the actual controller bean class '" + targetBeanClass.getName() + "'. If the controller requires proxying (e.g. due to @Transactional), please use class-based proxying.";
/*     */       
/* 483 */       throw new IllegalStateException(formatInvokeError(text, args));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String formatInvokeError(String text, Object[] args) {
/* 492 */     String formattedArgs = IntStream.range(0, args.length).<CharSequence>mapToObj(i -> (args[i] != null) ? ("[" + i + "] [type=" + args[i].getClass().getName() + "] [value=" + args[i] + "]") : ("[" + i + "] [null]")).collect(Collectors.joining(",\n", " ", " "));
/* 493 */     return text + "\nController [" + 
/* 494 */       getBeanType().getName() + "]\nMethod [" + 
/* 495 */       getBridgedMethod().toGenericString() + "] with argument values:\n" + formattedArgs;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected class HandlerMethodParameter
/*     */     extends SynthesizingMethodParameter
/*     */   {
/*     */     @Nullable
/*     */     private volatile Annotation[] combinedAnnotations;
/*     */ 
/*     */ 
/*     */     
/*     */     public HandlerMethodParameter(int index) {
/* 509 */       super(HandlerMethod.this.bridgedMethod, index);
/*     */     }
/*     */     
/*     */     protected HandlerMethodParameter(HandlerMethodParameter original) {
/* 513 */       super(original);
/*     */     }
/*     */ 
/*     */     
/*     */     @NonNull
/*     */     public Method getMethod() {
/* 519 */       return HandlerMethod.this.bridgedMethod;
/*     */     }
/*     */ 
/*     */     
/*     */     public Class<?> getContainingClass() {
/* 524 */       return HandlerMethod.this.getBeanType();
/*     */     }
/*     */ 
/*     */     
/*     */     public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
/* 529 */       return HandlerMethod.this.getMethodAnnotation(annotationType);
/*     */     }
/*     */ 
/*     */     
/*     */     public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
/* 534 */       return HandlerMethod.this.hasMethodAnnotation(annotationType);
/*     */     }
/*     */ 
/*     */     
/*     */     public Annotation[] getParameterAnnotations() {
/* 539 */       Annotation[] anns = this.combinedAnnotations;
/* 540 */       if (anns == null) {
/* 541 */         anns = super.getParameterAnnotations();
/* 542 */         int index = getParameterIndex();
/* 543 */         if (index >= 0) {
/* 544 */           for (Annotation[][] ifcAnns : HandlerMethod.this.getInterfaceParameterAnnotations()) {
/* 545 */             if (index < ifcAnns.length) {
/* 546 */               Annotation[] paramAnns = ifcAnns[index];
/* 547 */               if (paramAnns.length > 0) {
/* 548 */                 List<Annotation> merged = new ArrayList<>(anns.length + paramAnns.length);
/* 549 */                 merged.addAll(Arrays.asList(anns));
/* 550 */                 for (Annotation paramAnn : paramAnns) {
/* 551 */                   boolean existingType = false;
/* 552 */                   for (Annotation ann : anns) {
/* 553 */                     if (ann.annotationType() == paramAnn.annotationType()) {
/* 554 */                       existingType = true;
/*     */                       break;
/*     */                     } 
/*     */                   } 
/* 558 */                   if (!existingType) {
/* 559 */                     merged.add(adaptAnnotation(paramAnn));
/*     */                   }
/*     */                 } 
/* 562 */                 anns = merged.<Annotation>toArray(new Annotation[0]);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         }
/* 567 */         this.combinedAnnotations = anns;
/*     */       } 
/* 569 */       return anns;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerMethodParameter clone() {
/* 574 */       return new HandlerMethodParameter(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class ReturnValueMethodParameter
/*     */     extends HandlerMethodParameter
/*     */   {
/*     */     @Nullable
/*     */     private final Object returnValue;
/*     */ 
/*     */     
/*     */     public ReturnValueMethodParameter(Object returnValue) {
/* 588 */       super(-1);
/* 589 */       this.returnValue = returnValue;
/*     */     }
/*     */     
/*     */     protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
/* 593 */       super(original);
/* 594 */       this.returnValue = original.returnValue;
/*     */     }
/*     */ 
/*     */     
/*     */     public Class<?> getParameterType() {
/* 599 */       return (this.returnValue != null) ? this.returnValue.getClass() : super.getParameterType();
/*     */     }
/*     */ 
/*     */     
/*     */     public ReturnValueMethodParameter clone() {
/* 604 */       return new ReturnValueMethodParameter(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/HandlerMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */