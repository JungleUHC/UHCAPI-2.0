/*     */ package org.springframework.web.method.annotation;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ import org.springframework.core.Conventions;
/*     */ import org.springframework.core.GenericTypeResolver;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.ui.ModelMap;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.validation.BindingResult;
/*     */ import org.springframework.web.HttpSessionRequiredException;
/*     */ import org.springframework.web.bind.WebDataBinder;
/*     */ import org.springframework.web.bind.annotation.ModelAttribute;
/*     */ import org.springframework.web.bind.support.WebDataBinderFactory;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ import org.springframework.web.context.request.WebRequest;
/*     */ import org.springframework.web.method.HandlerMethod;
/*     */ import org.springframework.web.method.support.InvocableHandlerMethod;
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
/*     */ public final class ModelFactory
/*     */ {
/*  64 */   private static final Log logger = LogFactory.getLog(ModelFactory.class);
/*     */ 
/*     */   
/*  67 */   private final List<ModelMethod> modelMethods = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final WebDataBinderFactory dataBinderFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final SessionAttributesHandler sessionAttributesHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ModelFactory(@Nullable List<InvocableHandlerMethod> handlerMethods, WebDataBinderFactory binderFactory, SessionAttributesHandler attributeHandler) {
/*  83 */     if (handlerMethods != null) {
/*  84 */       for (InvocableHandlerMethod handlerMethod : handlerMethods) {
/*  85 */         this.modelMethods.add(new ModelMethod(handlerMethod));
/*     */       }
/*     */     }
/*  88 */     this.dataBinderFactory = binderFactory;
/*  89 */     this.sessionAttributesHandler = attributeHandler;
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
/*     */   public void initModel(NativeWebRequest request, ModelAndViewContainer container, HandlerMethod handlerMethod) throws Exception {
/* 110 */     Map<String, ?> sessionAttributes = this.sessionAttributesHandler.retrieveAttributes((WebRequest)request);
/* 111 */     container.mergeAttributes(sessionAttributes);
/* 112 */     invokeModelAttributeMethods(request, container);
/*     */     
/* 114 */     for (String name : findSessionAttributeArguments(handlerMethod)) {
/* 115 */       if (!container.containsAttribute(name)) {
/* 116 */         Object value = this.sessionAttributesHandler.retrieveAttribute((WebRequest)request, name);
/* 117 */         if (value == null) {
/* 118 */           throw new HttpSessionRequiredException("Expected session attribute '" + name + "'", name);
/*     */         }
/* 120 */         container.addAttribute(name, value);
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
/*     */   private void invokeModelAttributeMethods(NativeWebRequest request, ModelAndViewContainer container) throws Exception {
/* 132 */     while (!this.modelMethods.isEmpty()) {
/* 133 */       InvocableHandlerMethod modelMethod = getNextModelMethod(container).getHandlerMethod();
/* 134 */       ModelAttribute ann = (ModelAttribute)modelMethod.getMethodAnnotation(ModelAttribute.class);
/* 135 */       Assert.state((ann != null), "No ModelAttribute annotation");
/* 136 */       if (container.containsAttribute(ann.name())) {
/* 137 */         if (!ann.binding()) {
/* 138 */           container.setBindingDisabled(ann.name());
/*     */         }
/*     */         
/*     */         continue;
/*     */       } 
/* 143 */       Object returnValue = modelMethod.invokeForRequest(request, container, new Object[0]);
/* 144 */       if (modelMethod.isVoid()) {
/* 145 */         if (StringUtils.hasText(ann.value()) && 
/* 146 */           logger.isDebugEnabled()) {
/* 147 */           logger.debug("Name in @ModelAttribute is ignored because method returns void: " + modelMethod
/* 148 */               .getShortLogMessage());
/*     */         }
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 154 */       String returnValueName = getNameForReturnValue(returnValue, modelMethod.getReturnType());
/* 155 */       if (!ann.binding()) {
/* 156 */         container.setBindingDisabled(returnValueName);
/*     */       }
/* 158 */       if (!container.containsAttribute(returnValueName)) {
/* 159 */         container.addAttribute(returnValueName, returnValue);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private ModelMethod getNextModelMethod(ModelAndViewContainer container) {
/* 165 */     for (ModelMethod modelMethod1 : this.modelMethods) {
/* 166 */       if (modelMethod1.checkDependencies(container)) {
/* 167 */         this.modelMethods.remove(modelMethod1);
/* 168 */         return modelMethod1;
/*     */       } 
/*     */     } 
/* 171 */     ModelMethod modelMethod = this.modelMethods.get(0);
/* 172 */     this.modelMethods.remove(modelMethod);
/* 173 */     return modelMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<String> findSessionAttributeArguments(HandlerMethod handlerMethod) {
/* 180 */     List<String> result = new ArrayList<>();
/* 181 */     for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
/* 182 */       if (parameter.hasParameterAnnotation(ModelAttribute.class)) {
/* 183 */         String name = getNameForParameter(parameter);
/* 184 */         Class<?> paramType = parameter.getParameterType();
/* 185 */         if (this.sessionAttributesHandler.isHandlerSessionAttribute(name, paramType)) {
/* 186 */           result.add(name);
/*     */         }
/*     */       } 
/*     */     } 
/* 190 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateModel(NativeWebRequest request, ModelAndViewContainer container) throws Exception {
/* 201 */     ModelMap defaultModel = container.getDefaultModel();
/* 202 */     if (container.getSessionStatus().isComplete()) {
/* 203 */       this.sessionAttributesHandler.cleanupAttributes((WebRequest)request);
/*     */     } else {
/*     */       
/* 206 */       this.sessionAttributesHandler.storeAttributes((WebRequest)request, (Map<String, ?>)defaultModel);
/*     */     } 
/* 208 */     if (!container.isRequestHandled() && container.getModel() == defaultModel) {
/* 209 */       updateBindingResult(request, defaultModel);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateBindingResult(NativeWebRequest request, ModelMap model) throws Exception {
/* 217 */     List<String> keyNames = new ArrayList<>(model.keySet());
/* 218 */     for (String name : keyNames) {
/* 219 */       Object value = model.get(name);
/* 220 */       if (value != null && isBindingCandidate(name, value)) {
/* 221 */         String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + name;
/* 222 */         if (!model.containsAttribute(bindingResultKey)) {
/* 223 */           WebDataBinder dataBinder = this.dataBinderFactory.createBinder(request, value, name);
/* 224 */           model.put(bindingResultKey, dataBinder.getBindingResult());
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isBindingCandidate(String attributeName, Object value) {
/* 234 */     if (attributeName.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
/* 235 */       return false;
/*     */     }
/*     */     
/* 238 */     if (this.sessionAttributesHandler.isHandlerSessionAttribute(attributeName, value.getClass())) {
/* 239 */       return true;
/*     */     }
/*     */     
/* 242 */     return (!value.getClass().isArray() && !(value instanceof java.util.Collection) && !(value instanceof Map) && 
/* 243 */       !BeanUtils.isSimpleValueType(value.getClass()));
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
/*     */   public static String getNameForParameter(MethodParameter parameter) {
/* 256 */     ModelAttribute ann = (ModelAttribute)parameter.getParameterAnnotation(ModelAttribute.class);
/* 257 */     String name = (ann != null) ? ann.value() : null;
/* 258 */     return StringUtils.hasText(name) ? name : Conventions.getVariableNameForParameter(parameter);
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
/*     */   public static String getNameForReturnValue(@Nullable Object returnValue, MethodParameter returnType) {
/* 274 */     ModelAttribute ann = (ModelAttribute)returnType.getMethodAnnotation(ModelAttribute.class);
/* 275 */     if (ann != null && StringUtils.hasText(ann.value())) {
/* 276 */       return ann.value();
/*     */     }
/*     */     
/* 279 */     Method method = returnType.getMethod();
/* 280 */     Assert.state((method != null), "No handler method");
/* 281 */     Class<?> containingClass = returnType.getContainingClass();
/* 282 */     Class<?> resolvedType = GenericTypeResolver.resolveReturnType(method, containingClass);
/* 283 */     return Conventions.getVariableNameForReturnType(method, resolvedType, returnValue);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ModelMethod
/*     */   {
/*     */     private final InvocableHandlerMethod handlerMethod;
/*     */     
/* 292 */     private final Set<String> dependencies = new HashSet<>();
/*     */     
/*     */     public ModelMethod(InvocableHandlerMethod handlerMethod) {
/* 295 */       this.handlerMethod = handlerMethod;
/* 296 */       for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
/* 297 */         if (parameter.hasParameterAnnotation(ModelAttribute.class)) {
/* 298 */           this.dependencies.add(ModelFactory.getNameForParameter(parameter));
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     public InvocableHandlerMethod getHandlerMethod() {
/* 304 */       return this.handlerMethod;
/*     */     }
/*     */     
/*     */     public boolean checkDependencies(ModelAndViewContainer mavContainer) {
/* 308 */       for (String name : this.dependencies) {
/* 309 */         if (!mavContainer.containsAttribute(name)) {
/* 310 */           return false;
/*     */         }
/*     */       } 
/* 313 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 318 */       return this.handlerMethod.getMethod().toGenericString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/annotation/ModelFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */