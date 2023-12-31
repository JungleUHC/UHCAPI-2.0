/*     */ package org.springframework.web.method.annotation;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.Part;
/*     */ import org.springframework.beans.BeanUtils;
/*     */ import org.springframework.beans.factory.config.ConfigurableBeanFactory;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.convert.ConversionService;
/*     */ import org.springframework.core.convert.TypeDescriptor;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.bind.MissingServletRequestParameterException;
/*     */ import org.springframework.web.bind.annotation.RequestParam;
/*     */ import org.springframework.web.bind.annotation.RequestPart;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ import org.springframework.web.method.support.UriComponentsContributor;
/*     */ import org.springframework.web.multipart.MultipartException;
/*     */ import org.springframework.web.multipart.MultipartFile;
/*     */ import org.springframework.web.multipart.MultipartRequest;
/*     */ import org.springframework.web.multipart.support.MissingServletRequestPartException;
/*     */ import org.springframework.web.multipart.support.MultipartResolutionDelegate;
/*     */ import org.springframework.web.util.UriComponentsBuilder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RequestParamMethodArgumentResolver
/*     */   extends AbstractNamedValueMethodArgumentResolver
/*     */   implements UriComponentsContributor
/*     */ {
/*  81 */   private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean useDefaultResolution;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestParamMethodArgumentResolver(boolean useDefaultResolution) {
/*  94 */     this.useDefaultResolution = useDefaultResolution;
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
/*     */   public RequestParamMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory, boolean useDefaultResolution) {
/* 110 */     super(beanFactory);
/* 111 */     this.useDefaultResolution = useDefaultResolution;
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
/*     */   public boolean supportsParameter(MethodParameter parameter) {
/* 128 */     if (parameter.hasParameterAnnotation(RequestParam.class)) {
/* 129 */       if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
/* 130 */         RequestParam requestParam = (RequestParam)parameter.getParameterAnnotation(RequestParam.class);
/* 131 */         return (requestParam != null && StringUtils.hasText(requestParam.name()));
/*     */       } 
/*     */       
/* 134 */       return true;
/*     */     } 
/*     */ 
/*     */     
/* 138 */     if (parameter.hasParameterAnnotation(RequestPart.class)) {
/* 139 */       return false;
/*     */     }
/* 141 */     parameter = parameter.nestedIfOptional();
/* 142 */     if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
/* 143 */       return true;
/*     */     }
/* 145 */     if (this.useDefaultResolution) {
/* 146 */       return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
/*     */     }
/*     */     
/* 149 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
/* 156 */     RequestParam ann = (RequestParam)parameter.getParameterAnnotation(RequestParam.class);
/* 157 */     return (ann != null) ? new RequestParamNamedValueInfo(ann) : new RequestParamNamedValueInfo();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
/* 163 */     HttpServletRequest servletRequest = (HttpServletRequest)request.getNativeRequest(HttpServletRequest.class);
/*     */     
/* 165 */     if (servletRequest != null) {
/* 166 */       Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
/* 167 */       if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
/* 168 */         return mpArg;
/*     */       }
/*     */     } 
/*     */     
/* 172 */     Object arg = null;
/* 173 */     MultipartRequest multipartRequest = (MultipartRequest)request.getNativeRequest(MultipartRequest.class);
/* 174 */     if (multipartRequest != null) {
/* 175 */       List<MultipartFile> files = multipartRequest.getFiles(name);
/* 176 */       if (!files.isEmpty()) {
/* 177 */         arg = (files.size() == 1) ? files.get(0) : files;
/*     */       }
/*     */     } 
/* 180 */     if (arg == null) {
/* 181 */       String[] paramValues = request.getParameterValues(name);
/* 182 */       if (paramValues != null) {
/* 183 */         arg = (paramValues.length == 1) ? paramValues[0] : paramValues;
/*     */       }
/*     */     } 
/* 186 */     return arg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
/* 193 */     handleMissingValueInternal(name, parameter, request, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
/* 200 */     handleMissingValueInternal(name, parameter, request, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleMissingValueInternal(String name, MethodParameter parameter, NativeWebRequest request, boolean missingAfterConversion) throws Exception {
/* 207 */     HttpServletRequest servletRequest = (HttpServletRequest)request.getNativeRequest(HttpServletRequest.class);
/* 208 */     if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
/* 209 */       if (servletRequest == null || !MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
/* 210 */         throw new MultipartException("Current request is not a multipart request");
/*     */       }
/*     */       
/* 213 */       throw new MissingServletRequestPartException(name);
/*     */     } 
/*     */ 
/*     */     
/* 217 */     throw new MissingServletRequestParameterException(name, parameter
/* 218 */         .getNestedParameterType().getSimpleName(), missingAfterConversion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void contributeMethodArgument(MethodParameter parameter, @Nullable Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
/* 226 */     Class<?> paramType = parameter.getNestedParameterType();
/* 227 */     if (Map.class.isAssignableFrom(paramType) || MultipartFile.class == paramType || Part.class == paramType) {
/*     */       return;
/*     */     }
/*     */     
/* 231 */     RequestParam requestParam = (RequestParam)parameter.getParameterAnnotation(RequestParam.class);
/*     */     
/* 233 */     String name = (requestParam != null && StringUtils.hasLength(requestParam.name())) ? requestParam.name() : parameter.getParameterName();
/* 234 */     Assert.state((name != null), "Unresolvable parameter name");
/*     */     
/* 236 */     parameter = parameter.nestedIfOptional();
/* 237 */     if (value instanceof Optional) {
/* 238 */       value = ((Optional)value).orElse(null);
/*     */     }
/*     */     
/* 241 */     if (value == null) {
/* 242 */       if (requestParam != null && (
/* 243 */         !requestParam.required() || !requestParam.defaultValue().equals("\n\t\t\n\t\t\n\n\t\t\t\t\n"))) {
/*     */         return;
/*     */       }
/* 246 */       builder.queryParam(name, new Object[0]);
/*     */     }
/* 248 */     else if (value instanceof java.util.Collection) {
/* 249 */       for (Object element : value) {
/* 250 */         element = formatUriValue(conversionService, TypeDescriptor.nested(parameter, 1), element);
/* 251 */         builder.queryParam(name, new Object[] { element });
/*     */       } 
/*     */     } else {
/*     */       
/* 255 */       builder.queryParam(name, new Object[] { formatUriValue(conversionService, new TypeDescriptor(parameter), value) });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String formatUriValue(@Nullable ConversionService cs, @Nullable TypeDescriptor sourceType, @Nullable Object value) {
/* 263 */     if (value == null) {
/* 264 */       return null;
/*     */     }
/* 266 */     if (value instanceof String) {
/* 267 */       return (String)value;
/*     */     }
/* 269 */     if (cs != null) {
/* 270 */       return (String)cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
/*     */     }
/*     */     
/* 273 */     return value.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private static class RequestParamNamedValueInfo
/*     */     extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo
/*     */   {
/*     */     public RequestParamNamedValueInfo() {
/* 281 */       super("", false, "\n\t\t\n\t\t\n\n\t\t\t\t\n");
/*     */     }
/*     */     
/*     */     public RequestParamNamedValueInfo(RequestParam annotation) {
/* 285 */       super(annotation.name(), annotation.required(), annotation.defaultValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/annotation/RequestParamMethodArgumentResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */