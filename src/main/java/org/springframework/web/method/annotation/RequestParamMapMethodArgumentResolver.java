/*     */ package org.springframework.web.method.annotation;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.Part;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.bind.annotation.RequestParam;
/*     */ import org.springframework.web.bind.support.WebDataBinderFactory;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ import org.springframework.web.method.support.HandlerMethodArgumentResolver;
/*     */ import org.springframework.web.method.support.ModelAndViewContainer;
/*     */ import org.springframework.web.multipart.MultipartFile;
/*     */ import org.springframework.web.multipart.MultipartRequest;
/*     */ import org.springframework.web.multipart.support.MultipartResolutionDelegate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RequestParamMapMethodArgumentResolver
/*     */   implements HandlerMethodArgumentResolver
/*     */ {
/*     */   public boolean supportsParameter(MethodParameter parameter) {
/*  66 */     RequestParam requestParam = (RequestParam)parameter.getParameterAnnotation(RequestParam.class);
/*  67 */     return (requestParam != null && Map.class.isAssignableFrom(parameter.getParameterType()) && 
/*  68 */       !StringUtils.hasText(requestParam.name()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
/*  75 */     ResolvableType resolvableType = ResolvableType.forMethodParameter(parameter);
/*     */     
/*  77 */     if (MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
/*     */       
/*  79 */       Class<?> clazz = resolvableType.as(MultiValueMap.class).getGeneric(new int[] { 1 }).resolve();
/*  80 */       if (clazz == MultipartFile.class) {
/*  81 */         MultipartRequest multipartRequest = MultipartResolutionDelegate.resolveMultipartRequest(webRequest);
/*  82 */         return (multipartRequest != null) ? multipartRequest.getMultiFileMap() : new LinkedMultiValueMap(0);
/*     */       } 
/*  84 */       if (clazz == Part.class) {
/*  85 */         HttpServletRequest servletRequest = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
/*  86 */         if (servletRequest != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
/*  87 */           Collection<Part> parts = servletRequest.getParts();
/*  88 */           LinkedMultiValueMap<String, Part> linkedMultiValueMap1 = new LinkedMultiValueMap(parts.size());
/*  89 */           for (Part part : parts) {
/*  90 */             linkedMultiValueMap1.add(part.getName(), part);
/*     */           }
/*  92 */           return linkedMultiValueMap1;
/*     */         } 
/*  94 */         return new LinkedMultiValueMap(0);
/*     */       } 
/*     */       
/*  97 */       Map<String, String[]> map = webRequest.getParameterMap();
/*  98 */       LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(map.size());
/*  99 */       map.forEach((key, values) -> {
/*     */             for (String value : values) {
/*     */               result.add(key, value);
/*     */             }
/*     */           });
/* 104 */       return linkedMultiValueMap;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     Class<?> valueType = resolvableType.asMap().getGeneric(new int[] { 1 }).resolve();
/* 111 */     if (valueType == MultipartFile.class) {
/* 112 */       MultipartRequest multipartRequest = MultipartResolutionDelegate.resolveMultipartRequest(webRequest);
/* 113 */       return (multipartRequest != null) ? multipartRequest.getFileMap() : new LinkedHashMap<>(0);
/*     */     } 
/* 115 */     if (valueType == Part.class) {
/* 116 */       HttpServletRequest servletRequest = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
/* 117 */       if (servletRequest != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
/* 118 */         Collection<Part> parts = servletRequest.getParts();
/* 119 */         LinkedHashMap<String, Part> linkedHashMap = CollectionUtils.newLinkedHashMap(parts.size());
/* 120 */         for (Part part : parts) {
/* 121 */           if (!linkedHashMap.containsKey(part.getName())) {
/* 122 */             linkedHashMap.put(part.getName(), part);
/*     */           }
/*     */         } 
/* 125 */         return linkedHashMap;
/*     */       } 
/* 127 */       return new LinkedHashMap<>(0);
/*     */     } 
/*     */     
/* 130 */     Map<String, String[]> parameterMap = webRequest.getParameterMap();
/* 131 */     Map<String, String> result = CollectionUtils.newLinkedHashMap(parameterMap.size());
/* 132 */     parameterMap.forEach((key, values) -> {
/*     */           if (values.length > 0) {
/*     */             result.put(key, values[0]);
/*     */           }
/*     */         });
/* 137 */     return result;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/annotation/RequestParamMapMethodArgumentResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */