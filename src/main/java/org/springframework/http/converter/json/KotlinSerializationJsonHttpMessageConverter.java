/*     */ package org.springframework.http.converter.json;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import kotlinx.serialization.DeserializationStrategy;
/*     */ import kotlinx.serialization.KSerializer;
/*     */ import kotlinx.serialization.SerializationException;
/*     */ import kotlinx.serialization.SerializationStrategy;
/*     */ import kotlinx.serialization.SerializersKt;
/*     */ import kotlinx.serialization.descriptors.PolymorphicKind;
/*     */ import kotlinx.serialization.descriptors.SerialDescriptor;
/*     */ import kotlinx.serialization.json.Json;
/*     */ import org.springframework.core.GenericTypeResolver;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.HttpOutputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
/*     */ import org.springframework.http.converter.HttpMessageNotReadableException;
/*     */ import org.springframework.http.converter.HttpMessageNotWritableException;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ConcurrentReferenceHashMap;
/*     */ import org.springframework.util.StreamUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KotlinSerializationJsonHttpMessageConverter
/*     */   extends AbstractGenericHttpMessageConverter<Object>
/*     */ {
/*  63 */   private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
/*     */   
/*  65 */   private static final Map<Type, KSerializer<Object>> serializerCache = (Map<Type, KSerializer<Object>>)new ConcurrentReferenceHashMap();
/*     */ 
/*     */ 
/*     */   
/*     */   private final Json json;
/*     */ 
/*     */ 
/*     */   
/*     */   public KotlinSerializationJsonHttpMessageConverter() {
/*  74 */     this((Json)Json.Default);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KotlinSerializationJsonHttpMessageConverter(Json json) {
/*  81 */     super(new MediaType[] { MediaType.APPLICATION_JSON, new MediaType("application", "*+json") });
/*  82 */     this.json = json;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean supports(Class<?> clazz) {
/*     */     try {
/*  89 */       serializer(clazz);
/*  90 */       return true;
/*     */     }
/*  92 */     catch (Exception ex) {
/*  93 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
/*     */     try {
/* 100 */       serializer(GenericTypeResolver.resolveType(type, contextClass));
/* 101 */       return canRead(mediaType);
/*     */     }
/* 103 */     catch (Exception ex) {
/* 104 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType) {
/*     */     try {
/* 111 */       serializer((type != null) ? GenericTypeResolver.resolveType(type, clazz) : clazz);
/* 112 */       return canWrite(mediaType);
/*     */     }
/* 114 */     catch (Exception ex) {
/* 115 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 123 */     return decode(serializer(GenericTypeResolver.resolveType(type, contextClass)), inputMessage);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 130 */     return decode(serializer(clazz), inputMessage);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Object decode(KSerializer<Object> serializer, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
/* 136 */     MediaType contentType = inputMessage.getHeaders().getContentType();
/* 137 */     String jsonText = StreamUtils.copyToString(inputMessage.getBody(), getCharsetToUse(contentType));
/*     */     
/*     */     try {
/* 140 */       return this.json.decodeFromString((DeserializationStrategy)serializer, jsonText);
/*     */     }
/* 142 */     catch (SerializationException ex) {
/* 143 */       throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex, inputMessage);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/* 151 */     encode(object, serializer((type != null) ? type : object.getClass()), outputMessage);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void encode(Object object, KSerializer<Object> serializer, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
/*     */     try {
/* 158 */       String json = this.json.encodeToString((SerializationStrategy)serializer, object);
/* 159 */       MediaType contentType = outputMessage.getHeaders().getContentType();
/* 160 */       outputMessage.getBody().write(json.getBytes(getCharsetToUse(contentType)));
/* 161 */       outputMessage.getBody().flush();
/*     */     }
/* 163 */     catch (IOException ex) {
/* 164 */       throw ex;
/*     */     }
/* 166 */     catch (Exception ex) {
/* 167 */       throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Charset getCharsetToUse(@Nullable MediaType contentType) {
/* 172 */     if (contentType != null && contentType.getCharset() != null) {
/* 173 */       return contentType.getCharset();
/*     */     }
/* 175 */     return DEFAULT_CHARSET;
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
/*     */   private KSerializer<Object> serializer(Type type) {
/* 188 */     KSerializer<Object> serializer = serializerCache.get(type);
/* 189 */     if (serializer == null) {
/* 190 */       serializer = SerializersKt.serializer(type);
/* 191 */       if (hasPolymorphism(serializer.getDescriptor(), new HashSet<>())) {
/* 192 */         throw new UnsupportedOperationException("Open polymorphic serialization is not supported yet");
/*     */       }
/* 194 */       serializerCache.put(type, serializer);
/*     */     } 
/* 196 */     return serializer;
/*     */   }
/*     */   
/*     */   private boolean hasPolymorphism(SerialDescriptor descriptor, Set<String> alreadyProcessed) {
/* 200 */     alreadyProcessed.add(descriptor.getSerialName());
/* 201 */     if (descriptor.getKind().equals(PolymorphicKind.OPEN.INSTANCE)) {
/* 202 */       return true;
/*     */     }
/* 204 */     for (int i = 0; i < descriptor.getElementsCount(); i++) {
/* 205 */       SerialDescriptor elementDescriptor = descriptor.getElementDescriptor(i);
/* 206 */       if (!alreadyProcessed.contains(elementDescriptor.getSerialName()) && hasPolymorphism(elementDescriptor, alreadyProcessed)) {
/* 207 */         return true;
/*     */       }
/*     */     } 
/* 210 */     return false;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/json/KotlinSerializationJsonHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */