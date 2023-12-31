/*     */ package org.springframework.http.codec.json;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonView;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.springframework.core.GenericTypeResolver;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.http.HttpLogging;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.MimeType;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Jackson2CodecSupport
/*     */ {
/*  64 */   public static final String JSON_VIEW_HINT = Jackson2CodecSupport.class.getName() + ".jsonView";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   static final String ACTUAL_TYPE_HINT = Jackson2CodecSupport.class.getName() + ".actualType";
/*     */ 
/*     */   
/*     */   private static final String JSON_VIEW_HINT_ERROR = "@JsonView only supported for write hints with exactly 1 class argument: ";
/*     */   
/*  78 */   private static final List<MimeType> DEFAULT_MIME_TYPES = Collections.unmodifiableList(
/*  79 */       (List)Arrays.asList((Object[])new MediaType[] { MediaType.APPLICATION_JSON, new MediaType("application", "*+json"), MediaType.APPLICATION_NDJSON }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   protected final Log logger = HttpLogging.forLogName(getClass());
/*     */ 
/*     */   
/*     */   private ObjectMapper defaultObjectMapper;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Map<Class<?>, Map<MimeType, ObjectMapper>> objectMapperRegistrations;
/*     */ 
/*     */   
/*     */   private final List<MimeType> mimeTypes;
/*     */ 
/*     */   
/*     */   protected Jackson2CodecSupport(ObjectMapper objectMapper, MimeType... mimeTypes) {
/*  99 */     Assert.notNull(objectMapper, "ObjectMapper must not be null");
/* 100 */     this.defaultObjectMapper = objectMapper;
/* 101 */     this
/* 102 */       .mimeTypes = !ObjectUtils.isEmpty((Object[])mimeTypes) ? Collections.<MimeType>unmodifiableList(Arrays.asList(mimeTypes)) : DEFAULT_MIME_TYPES;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setObjectMapper(ObjectMapper objectMapper) {
/* 112 */     Assert.notNull(objectMapper, "ObjectMapper must not be null");
/* 113 */     this.defaultObjectMapper = objectMapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectMapper getObjectMapper() {
/* 120 */     return this.defaultObjectMapper;
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
/*     */   public void registerObjectMappersForType(Class<?> clazz, Consumer<Map<MimeType, ObjectMapper>> registrar) {
/* 140 */     if (this.objectMapperRegistrations == null) {
/* 141 */       this.objectMapperRegistrations = new LinkedHashMap<>();
/*     */     }
/*     */     
/* 144 */     Map<MimeType, ObjectMapper> registrations = this.objectMapperRegistrations.computeIfAbsent(clazz, c -> new LinkedHashMap<>());
/* 145 */     registrar.accept(registrations);
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
/*     */   public Map<MimeType, ObjectMapper> getObjectMappersForType(Class<?> clazz) {
/* 157 */     for (Map.Entry<Class<?>, Map<MimeType, ObjectMapper>> entry : getObjectMapperRegistrations().entrySet()) {
/* 158 */       if (((Class)entry.getKey()).isAssignableFrom(clazz)) {
/* 159 */         return entry.getValue();
/*     */       }
/*     */     } 
/* 162 */     return Collections.emptyMap();
/*     */   }
/*     */   
/*     */   protected Map<Class<?>, Map<MimeType, ObjectMapper>> getObjectMapperRegistrations() {
/* 166 */     return (this.objectMapperRegistrations != null) ? this.objectMapperRegistrations : Collections.<Class<?>, Map<MimeType, ObjectMapper>>emptyMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<MimeType> getMimeTypes() {
/* 173 */     return this.mimeTypes;
/*     */   }
/*     */   
/*     */   protected List<MimeType> getMimeTypes(ResolvableType elementType) {
/* 177 */     Class<?> elementClass = elementType.toClass();
/* 178 */     List<MimeType> result = null;
/* 179 */     for (Map.Entry<Class<?>, Map<MimeType, ObjectMapper>> entry : getObjectMapperRegistrations().entrySet()) {
/* 180 */       if (((Class)entry.getKey()).isAssignableFrom(elementClass)) {
/* 181 */         result = (result != null) ? result : new ArrayList<>(((Map)entry.getValue()).size());
/* 182 */         result.addAll(((Map)entry.getValue()).keySet());
/*     */       } 
/*     */     } 
/* 185 */     return CollectionUtils.isEmpty(result) ? getMimeTypes() : result;
/*     */   }
/*     */   
/*     */   protected boolean supportsMimeType(@Nullable MimeType mimeType) {
/* 189 */     if (mimeType == null) {
/* 190 */       return true;
/*     */     }
/* 192 */     for (MimeType supportedMimeType : this.mimeTypes) {
/* 193 */       if (supportedMimeType.isCompatibleWith(mimeType)) {
/* 194 */         return true;
/*     */       }
/*     */     } 
/* 197 */     return false;
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
/*     */   protected void logWarningIfNecessary(Type type, @Nullable Throwable cause) {
/* 209 */     if (cause == null) {
/*     */       return;
/*     */     }
/* 212 */     if (this.logger.isDebugEnabled()) {
/* 213 */       String msg = "Failed to evaluate Jackson " + ((type instanceof JavaType) ? "de" : "") + "serialization for type [" + type + "]";
/*     */       
/* 215 */       this.logger.debug(msg, cause);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
/* 220 */     return this.defaultObjectMapper.constructType(GenericTypeResolver.resolveType(type, contextClass));
/*     */   }
/*     */   
/*     */   protected Map<String, Object> getHints(ResolvableType resolvableType) {
/* 224 */     MethodParameter param = getParameter(resolvableType);
/* 225 */     if (param != null) {
/* 226 */       Map<String, Object> hints = null;
/* 227 */       if (resolvableType.hasGenerics()) {
/* 228 */         hints = new HashMap<>(2);
/* 229 */         hints.put(ACTUAL_TYPE_HINT, resolvableType);
/*     */       } 
/* 231 */       JsonView annotation = getAnnotation(param, JsonView.class);
/* 232 */       if (annotation != null) {
/* 233 */         Class<?>[] classes = annotation.value();
/* 234 */         Assert.isTrue((classes.length == 1), "@JsonView only supported for write hints with exactly 1 class argument: " + param);
/* 235 */         hints = (hints != null) ? hints : new HashMap<>(1);
/* 236 */         hints.put(JSON_VIEW_HINT, classes[0]);
/*     */       } 
/* 238 */       if (hints != null) {
/* 239 */         return hints;
/*     */       }
/*     */     } 
/* 242 */     return Hints.none();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected MethodParameter getParameter(ResolvableType type) {
/* 247 */     return (type.getSource() instanceof MethodParameter) ? (MethodParameter)type.getSource() : null;
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
/*     */   protected ObjectMapper selectObjectMapper(ResolvableType targetType, @Nullable MimeType targetMimeType) {
/* 261 */     if (targetMimeType == null || CollectionUtils.isEmpty(this.objectMapperRegistrations)) {
/* 262 */       return this.defaultObjectMapper;
/*     */     }
/* 264 */     Class<?> targetClass = targetType.toClass();
/* 265 */     for (Map.Entry<Class<?>, Map<MimeType, ObjectMapper>> typeEntry : getObjectMapperRegistrations().entrySet()) {
/* 266 */       if (((Class)typeEntry.getKey()).isAssignableFrom(targetClass)) {
/* 267 */         for (Map.Entry<MimeType, ObjectMapper> objectMapperEntry : (Iterable<Map.Entry<MimeType, ObjectMapper>>)((Map)typeEntry.getValue()).entrySet()) {
/* 268 */           if (((MimeType)objectMapperEntry.getKey()).includes(targetMimeType)) {
/* 269 */             return objectMapperEntry.getValue();
/*     */           }
/*     */         } 
/*     */         
/* 273 */         return null;
/*     */       } 
/*     */     } 
/*     */     
/* 277 */     return this.defaultObjectMapper;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected abstract <A extends java.lang.annotation.Annotation> A getAnnotation(MethodParameter paramMethodParameter, Class<A> paramClass);
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/json/Jackson2CodecSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */