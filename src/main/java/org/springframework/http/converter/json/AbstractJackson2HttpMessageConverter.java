/*     */ package org.springframework.http.converter.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonEncoding;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import com.fasterxml.jackson.core.util.DefaultIndenter;
/*     */ import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import com.fasterxml.jackson.databind.ObjectReader;
/*     */ import com.fasterxml.jackson.databind.ObjectWriter;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
/*     */ import com.fasterxml.jackson.databind.ser.FilterProvider;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Type;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.function.Consumer;
/*     */ import org.springframework.core.GenericTypeResolver;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
/*     */ import org.springframework.http.converter.HttpMessageConversionException;
/*     */ import org.springframework.http.converter.HttpMessageNotReadableException;
/*     */ import org.springframework.http.converter.HttpMessageNotWritableException;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.StreamUtils;
/*     */ import org.springframework.util.TypeUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractJackson2HttpMessageConverter
/*     */   extends AbstractGenericHttpMessageConverter<Object>
/*     */ {
/*  86 */   private static final Map<String, JsonEncoding> ENCODINGS = CollectionUtils.newHashMap((JsonEncoding.values()).length); static {
/*  87 */     for (JsonEncoding encoding : JsonEncoding.values()) {
/*  88 */       ENCODINGS.put(encoding.getJavaName(), encoding);
/*     */     }
/*  90 */     ENCODINGS.put("US-ASCII", JsonEncoding.UTF8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   @Deprecated
/*  99 */   public static final Charset DEFAULT_CHARSET = null;
/*     */ 
/*     */   
/*     */   protected ObjectMapper defaultObjectMapper;
/*     */   
/*     */   @Nullable
/*     */   private Map<Class<?>, Map<MediaType, ObjectMapper>> objectMapperRegistrations;
/*     */   
/*     */   @Nullable
/*     */   private Boolean prettyPrint;
/*     */   
/*     */   @Nullable
/*     */   private PrettyPrinter ssePrettyPrinter;
/*     */ 
/*     */   
/*     */   protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper) {
/* 115 */     this.defaultObjectMapper = objectMapper;
/* 116 */     DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
/* 117 */     prettyPrinter.indentObjectsWith((DefaultPrettyPrinter.Indenter)new DefaultIndenter("  ", "\ndata:"));
/* 118 */     this.ssePrettyPrinter = (PrettyPrinter)prettyPrinter;
/*     */   }
/*     */   
/*     */   protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper, MediaType supportedMediaType) {
/* 122 */     this(objectMapper);
/* 123 */     setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
/*     */   }
/*     */   
/*     */   protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper, MediaType... supportedMediaTypes) {
/* 127 */     this(objectMapper);
/* 128 */     setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
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
/*     */   public void setObjectMapper(ObjectMapper objectMapper) {
/* 145 */     Assert.notNull(objectMapper, "ObjectMapper must not be null");
/* 146 */     this.defaultObjectMapper = objectMapper;
/* 147 */     configurePrettyPrint();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectMapper getObjectMapper() {
/* 154 */     return this.defaultObjectMapper;
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
/*     */   public void registerObjectMappersForType(Class<?> clazz, Consumer<Map<MediaType, ObjectMapper>> registrar) {
/* 174 */     if (this.objectMapperRegistrations == null) {
/* 175 */       this.objectMapperRegistrations = new LinkedHashMap<>();
/*     */     }
/*     */     
/* 178 */     Map<MediaType, ObjectMapper> registrations = this.objectMapperRegistrations.computeIfAbsent(clazz, c -> new LinkedHashMap<>());
/* 179 */     registrar.accept(registrations);
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
/*     */   public Map<MediaType, ObjectMapper> getObjectMappersForType(Class<?> clazz) {
/* 191 */     for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> entry : getObjectMapperRegistrations().entrySet()) {
/* 192 */       if (((Class)entry.getKey()).isAssignableFrom(clazz)) {
/* 193 */         return entry.getValue();
/*     */       }
/*     */     } 
/* 196 */     return Collections.emptyMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
/* 201 */     List<MediaType> result = null;
/* 202 */     for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> entry : getObjectMapperRegistrations().entrySet()) {
/* 203 */       if (((Class)entry.getKey()).isAssignableFrom(clazz)) {
/* 204 */         result = (result != null) ? result : new ArrayList<>(((Map)entry.getValue()).size());
/* 205 */         result.addAll(((Map)entry.getValue()).keySet());
/*     */       } 
/*     */     } 
/* 208 */     return CollectionUtils.isEmpty(result) ? getSupportedMediaTypes() : result;
/*     */   }
/*     */   
/*     */   private Map<Class<?>, Map<MediaType, ObjectMapper>> getObjectMapperRegistrations() {
/* 212 */     return (this.objectMapperRegistrations != null) ? this.objectMapperRegistrations : Collections.<Class<?>, Map<MediaType, ObjectMapper>>emptyMap();
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
/*     */   public void setPrettyPrint(boolean prettyPrint) {
/* 225 */     this.prettyPrint = Boolean.valueOf(prettyPrint);
/* 226 */     configurePrettyPrint();
/*     */   }
/*     */   
/*     */   private void configurePrettyPrint() {
/* 230 */     if (this.prettyPrint != null) {
/* 231 */       this.defaultObjectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint.booleanValue());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
/* 238 */     return canRead(clazz, (Class<?>)null, mediaType);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
/* 243 */     if (!canRead(mediaType)) {
/* 244 */       return false;
/*     */     }
/* 246 */     JavaType javaType = getJavaType(type, contextClass);
/* 247 */     ObjectMapper objectMapper = selectObjectMapper(javaType.getRawClass(), mediaType);
/* 248 */     if (objectMapper == null) {
/* 249 */       return false;
/*     */     }
/* 251 */     AtomicReference<Throwable> causeRef = new AtomicReference<>();
/* 252 */     if (objectMapper.canDeserialize(javaType, causeRef)) {
/* 253 */       return true;
/*     */     }
/* 255 */     logWarningIfNecessary((Type)javaType, causeRef.get());
/* 256 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
/* 261 */     if (!canWrite(mediaType)) {
/* 262 */       return false;
/*     */     }
/* 264 */     if (mediaType != null && mediaType.getCharset() != null) {
/* 265 */       Charset charset = mediaType.getCharset();
/* 266 */       if (!ENCODINGS.containsKey(charset.name())) {
/* 267 */         return false;
/*     */       }
/*     */     } 
/* 270 */     ObjectMapper objectMapper = selectObjectMapper(clazz, mediaType);
/* 271 */     if (objectMapper == null) {
/* 272 */       return false;
/*     */     }
/* 274 */     AtomicReference<Throwable> causeRef = new AtomicReference<>();
/* 275 */     if (objectMapper.canSerialize(clazz, causeRef)) {
/* 276 */       return true;
/*     */     }
/* 278 */     logWarningIfNecessary(clazz, causeRef.get());
/* 279 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ObjectMapper selectObjectMapper(Class<?> targetType, @Nullable MediaType targetMediaType) {
/* 289 */     if (targetMediaType == null || CollectionUtils.isEmpty(this.objectMapperRegistrations)) {
/* 290 */       return this.defaultObjectMapper;
/*     */     }
/* 292 */     for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> typeEntry : getObjectMapperRegistrations().entrySet()) {
/* 293 */       if (((Class)typeEntry.getKey()).isAssignableFrom(targetType)) {
/* 294 */         for (Map.Entry<MediaType, ObjectMapper> objectMapperEntry : (Iterable<Map.Entry<MediaType, ObjectMapper>>)((Map)typeEntry.getValue()).entrySet()) {
/* 295 */           if (((MediaType)objectMapperEntry.getKey()).includes(targetMediaType)) {
/* 296 */             return objectMapperEntry.getValue();
/*     */           }
/*     */         } 
/*     */         
/* 300 */         return null;
/*     */       } 
/*     */     } 
/*     */     
/* 304 */     return this.defaultObjectMapper;
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
/* 316 */     if (cause == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 321 */     boolean debugLevel = (cause instanceof com.fasterxml.jackson.databind.JsonMappingException && cause.getMessage().startsWith("Cannot find"));
/*     */     
/* 323 */     if (debugLevel ? this.logger.isDebugEnabled() : this.logger.isWarnEnabled()) {
/* 324 */       String msg = "Failed to evaluate Jackson " + ((type instanceof JavaType) ? "de" : "") + "serialization for type [" + type + "]";
/*     */       
/* 326 */       if (debugLevel) {
/* 327 */         this.logger.debug(msg, cause);
/*     */       }
/* 329 */       else if (this.logger.isDebugEnabled()) {
/* 330 */         this.logger.warn(msg, cause);
/*     */       } else {
/*     */         
/* 333 */         this.logger.warn(msg + ": " + cause);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 342 */     JavaType javaType = getJavaType(type, contextClass);
/* 343 */     return readJavaType(javaType, inputMessage);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 350 */     JavaType javaType = getJavaType(clazz, (Class<?>)null);
/* 351 */     return readJavaType(javaType, inputMessage);
/*     */   }
/*     */   
/*     */   private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) throws IOException {
/* 355 */     MediaType contentType = inputMessage.getHeaders().getContentType();
/* 356 */     Charset charset = getCharset(contentType);
/*     */     
/* 358 */     ObjectMapper objectMapper = selectObjectMapper(javaType.getRawClass(), contentType);
/* 359 */     Assert.state((objectMapper != null), "No ObjectMapper for " + javaType);
/*     */ 
/*     */ 
/*     */     
/* 363 */     boolean isUnicode = (ENCODINGS.containsKey(charset.name()) || "UTF-16".equals(charset.name()) || "UTF-32".equals(charset.name()));
/*     */     try {
/* 365 */       InputStream inputStream = StreamUtils.nonClosing(inputMessage.getBody());
/* 366 */       if (inputMessage instanceof MappingJacksonInputMessage) {
/* 367 */         Class<?> deserializationView = ((MappingJacksonInputMessage)inputMessage).getDeserializationView();
/* 368 */         if (deserializationView != null) {
/* 369 */           ObjectReader objectReader = objectMapper.readerWithView(deserializationView).forType(javaType);
/* 370 */           if (isUnicode) {
/* 371 */             return objectReader.readValue(inputStream);
/*     */           }
/*     */           
/* 374 */           Reader reader1 = new InputStreamReader(inputStream, charset);
/* 375 */           return objectReader.readValue(reader1);
/*     */         } 
/*     */       } 
/*     */       
/* 379 */       if (isUnicode) {
/* 380 */         return objectMapper.readValue(inputStream, javaType);
/*     */       }
/*     */       
/* 383 */       Reader reader = new InputStreamReader(inputStream, charset);
/* 384 */       return objectMapper.readValue(reader, javaType);
/*     */     
/*     */     }
/* 387 */     catch (InvalidDefinitionException ex) {
/* 388 */       throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
/*     */     }
/* 390 */     catch (JsonProcessingException ex) {
/* 391 */       throw new HttpMessageNotReadableException("JSON parse error: " + ex.getOriginalMessage(), ex, inputMessage);
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
/*     */   protected Charset getCharset(@Nullable MediaType contentType) {
/* 404 */     if (contentType != null && contentType.getCharset() != null) {
/* 405 */       return contentType.getCharset();
/*     */     }
/*     */     
/* 408 */     return StandardCharsets.UTF_8;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/* 416 */     MediaType contentType = outputMessage.getHeaders().getContentType();
/* 417 */     JsonEncoding encoding = getJsonEncoding(contentType);
/*     */ 
/*     */     
/* 420 */     Class<?> clazz = (object instanceof MappingJacksonValue) ? ((MappingJacksonValue)object).getValue().getClass() : object.getClass();
/* 421 */     ObjectMapper objectMapper = selectObjectMapper(clazz, contentType);
/* 422 */     Assert.state((objectMapper != null), "No ObjectMapper for " + clazz.getName());
/*     */     
/* 424 */     OutputStream outputStream = StreamUtils.nonClosing(outputMessage.getBody());
/* 425 */     try (JsonGenerator generator = objectMapper.getFactory().createGenerator(outputStream, encoding)) {
/* 426 */       writePrefix(generator, object);
/*     */       
/* 428 */       Object value = object;
/* 429 */       Class<?> serializationView = null;
/* 430 */       FilterProvider filters = null;
/* 431 */       JavaType javaType = null;
/*     */       
/* 433 */       if (object instanceof MappingJacksonValue) {
/* 434 */         MappingJacksonValue container = (MappingJacksonValue)object;
/* 435 */         value = container.getValue();
/* 436 */         serializationView = container.getSerializationView();
/* 437 */         filters = container.getFilters();
/*     */       } 
/* 439 */       if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
/* 440 */         javaType = getJavaType(type, (Class<?>)null);
/*     */       }
/*     */ 
/*     */       
/* 444 */       ObjectWriter objectWriter = (serializationView != null) ? objectMapper.writerWithView(serializationView) : objectMapper.writer();
/* 445 */       if (filters != null) {
/* 446 */         objectWriter = objectWriter.with(filters);
/*     */       }
/* 448 */       if (javaType != null && javaType.isContainerType()) {
/* 449 */         objectWriter = objectWriter.forType(javaType);
/*     */       }
/* 451 */       SerializationConfig config = objectWriter.getConfig();
/* 452 */       if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) && config
/* 453 */         .isEnabled(SerializationFeature.INDENT_OUTPUT)) {
/* 454 */         objectWriter = objectWriter.with(this.ssePrettyPrinter);
/*     */       }
/* 456 */       objectWriter.writeValue(generator, value);
/*     */       
/* 458 */       writeSuffix(generator, object);
/* 459 */       generator.flush();
/*     */     }
/* 461 */     catch (InvalidDefinitionException ex) {
/* 462 */       throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
/*     */     }
/* 464 */     catch (JsonProcessingException ex) {
/* 465 */       throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getOriginalMessage(), ex);
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
/*     */   protected void writePrefix(JsonGenerator generator, Object object) throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
/* 493 */     return this.defaultObjectMapper.constructType(GenericTypeResolver.resolveType(type, contextClass));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonEncoding getJsonEncoding(@Nullable MediaType contentType) {
/* 502 */     if (contentType != null && contentType.getCharset() != null) {
/* 503 */       Charset charset = contentType.getCharset();
/* 504 */       JsonEncoding encoding = ENCODINGS.get(charset.name());
/* 505 */       if (encoding != null) {
/* 506 */         return encoding;
/*     */       }
/*     */     } 
/* 509 */     return JsonEncoding.UTF8;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected MediaType getDefaultContentType(Object object) throws IOException {
/* 515 */     if (object instanceof MappingJacksonValue) {
/* 516 */       object = ((MappingJacksonValue)object).getValue();
/*     */     }
/* 518 */     return super.getDefaultContentType(object);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Long getContentLength(Object object, @Nullable MediaType contentType) throws IOException {
/* 523 */     if (object instanceof MappingJacksonValue) {
/* 524 */       object = ((MappingJacksonValue)object).getValue();
/*     */     }
/* 526 */     return super.getContentLength(object, contentType);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/json/AbstractJackson2HttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */