/*     */ package org.springframework.web.multipart.support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.Part;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ import org.springframework.web.multipart.MultipartFile;
/*     */ import org.springframework.web.multipart.MultipartHttpServletRequest;
/*     */ import org.springframework.web.multipart.MultipartRequest;
/*     */ import org.springframework.web.util.WebUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MultipartResolutionDelegate
/*     */ {
/*  47 */   public static final Object UNRESOLVABLE = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static MultipartRequest resolveMultipartRequest(NativeWebRequest webRequest) {
/*  56 */     MultipartRequest multipartRequest = (MultipartRequest)webRequest.getNativeRequest(MultipartRequest.class);
/*  57 */     if (multipartRequest != null) {
/*  58 */       return multipartRequest;
/*     */     }
/*  60 */     HttpServletRequest servletRequest = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
/*  61 */     if (servletRequest != null && isMultipartContent(servletRequest)) {
/*  62 */       return (MultipartRequest)new StandardMultipartHttpServletRequest(servletRequest);
/*     */     }
/*  64 */     return null;
/*     */   }
/*     */   
/*     */   public static boolean isMultipartRequest(HttpServletRequest request) {
/*  68 */     return (WebUtils.getNativeRequest((ServletRequest)request, MultipartHttpServletRequest.class) != null || 
/*  69 */       isMultipartContent(request));
/*     */   }
/*     */   
/*     */   private static boolean isMultipartContent(HttpServletRequest request) {
/*  73 */     String contentType = request.getContentType();
/*  74 */     return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
/*     */   }
/*     */   
/*     */   static MultipartHttpServletRequest asMultipartHttpServletRequest(HttpServletRequest request) {
/*  78 */     MultipartHttpServletRequest unwrapped = (MultipartHttpServletRequest)WebUtils.getNativeRequest((ServletRequest)request, MultipartHttpServletRequest.class);
/*  79 */     if (unwrapped != null) {
/*  80 */       return unwrapped;
/*     */     }
/*  82 */     return new StandardMultipartHttpServletRequest(request);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isMultipartArgument(MethodParameter parameter) {
/*  87 */     Class<?> paramType = parameter.getNestedParameterType();
/*  88 */     return (MultipartFile.class == paramType || 
/*  89 */       isMultipartFileCollection(parameter) || isMultipartFileArray(parameter) || Part.class == paramType || 
/*  90 */       isPartCollection(parameter) || isPartArray(parameter));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Object resolveMultipartArgument(String name, MethodParameter parameter, HttpServletRequest request) throws Exception {
/*  98 */     MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)WebUtils.getNativeRequest((ServletRequest)request, MultipartHttpServletRequest.class);
/*  99 */     boolean isMultipart = (multipartRequest != null || isMultipartContent(request));
/*     */     
/* 101 */     if (MultipartFile.class == parameter.getNestedParameterType()) {
/* 102 */       if (!isMultipart) {
/* 103 */         return null;
/*     */       }
/* 105 */       if (multipartRequest == null) {
/* 106 */         multipartRequest = new StandardMultipartHttpServletRequest(request);
/*     */       }
/* 108 */       return multipartRequest.getFile(name);
/*     */     } 
/* 110 */     if (isMultipartFileCollection(parameter)) {
/* 111 */       if (!isMultipart) {
/* 112 */         return null;
/*     */       }
/* 114 */       if (multipartRequest == null) {
/* 115 */         multipartRequest = new StandardMultipartHttpServletRequest(request);
/*     */       }
/* 117 */       List<MultipartFile> files = multipartRequest.getFiles(name);
/* 118 */       return !files.isEmpty() ? files : null;
/*     */     } 
/* 120 */     if (isMultipartFileArray(parameter)) {
/* 121 */       if (!isMultipart) {
/* 122 */         return null;
/*     */       }
/* 124 */       if (multipartRequest == null) {
/* 125 */         multipartRequest = new StandardMultipartHttpServletRequest(request);
/*     */       }
/* 127 */       List<MultipartFile> files = multipartRequest.getFiles(name);
/* 128 */       return !files.isEmpty() ? files.<MultipartFile>toArray(new MultipartFile[0]) : null;
/*     */     } 
/* 130 */     if (Part.class == parameter.getNestedParameterType()) {
/* 131 */       if (!isMultipart) {
/* 132 */         return null;
/*     */       }
/* 134 */       return request.getPart(name);
/*     */     } 
/* 136 */     if (isPartCollection(parameter)) {
/* 137 */       if (!isMultipart) {
/* 138 */         return null;
/*     */       }
/* 140 */       List<Part> parts = resolvePartList(request, name);
/* 141 */       return !parts.isEmpty() ? parts : null;
/*     */     } 
/* 143 */     if (isPartArray(parameter)) {
/* 144 */       if (!isMultipart) {
/* 145 */         return null;
/*     */       }
/* 147 */       List<Part> parts = resolvePartList(request, name);
/* 148 */       return !parts.isEmpty() ? parts.<Part>toArray(new Part[0]) : null;
/*     */     } 
/*     */     
/* 151 */     return UNRESOLVABLE;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isMultipartFileCollection(MethodParameter methodParam) {
/* 156 */     return (MultipartFile.class == getCollectionParameterType(methodParam));
/*     */   }
/*     */   
/*     */   private static boolean isMultipartFileArray(MethodParameter methodParam) {
/* 160 */     return (MultipartFile.class == methodParam.getNestedParameterType().getComponentType());
/*     */   }
/*     */   
/*     */   private static boolean isPartCollection(MethodParameter methodParam) {
/* 164 */     return (Part.class == getCollectionParameterType(methodParam));
/*     */   }
/*     */   
/*     */   private static boolean isPartArray(MethodParameter methodParam) {
/* 168 */     return (Part.class == methodParam.getNestedParameterType().getComponentType());
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static Class<?> getCollectionParameterType(MethodParameter methodParam) {
/* 173 */     Class<?> paramType = methodParam.getNestedParameterType();
/* 174 */     if (Collection.class == paramType || List.class.isAssignableFrom(paramType)) {
/* 175 */       Class<?> valueType = ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric(new int[0]);
/* 176 */       if (valueType != null) {
/* 177 */         return valueType;
/*     */       }
/*     */     } 
/* 180 */     return null;
/*     */   }
/*     */   
/*     */   private static List<Part> resolvePartList(HttpServletRequest request, String name) throws Exception {
/* 184 */     Collection<Part> parts = request.getParts();
/* 185 */     List<Part> result = new ArrayList<>(parts.size());
/* 186 */     for (Part part : parts) {
/* 187 */       if (part.getName().equals(name)) {
/* 188 */         result.add(part);
/*     */       }
/*     */     } 
/* 191 */     return result;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/support/MultipartResolutionDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */