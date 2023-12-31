/*     */ package com.fasterxml.jackson.databind.module;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.Deserializers;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.ArrayType;
/*     */ import com.fasterxml.jackson.databind.type.ClassKey;
/*     */ import com.fasterxml.jackson.databind.type.CollectionLikeType;
/*     */ import com.fasterxml.jackson.databind.type.CollectionType;
/*     */ import com.fasterxml.jackson.databind.type.MapLikeType;
/*     */ import com.fasterxml.jackson.databind.type.MapType;
/*     */ import com.fasterxml.jackson.databind.type.ReferenceType;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class SimpleDeserializers
/*     */   extends Deserializers.Base implements Serializable {
/*     */   private static final long serialVersionUID = 1L;
/*  26 */   protected HashMap<ClassKey, JsonDeserializer<?>> _classMappings = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _hasEnumDeserializer = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleDeserializers() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleDeserializers(Map<Class<?>, JsonDeserializer<?>> desers) {
/*  47 */     addDeserializers(desers);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void addDeserializer(Class<T> forClass, JsonDeserializer<? extends T> deser) {
/*  52 */     ClassKey key = new ClassKey(forClass);
/*  53 */     if (this._classMappings == null) {
/*  54 */       this._classMappings = new HashMap<>();
/*     */     }
/*  56 */     this._classMappings.put(key, deser);
/*     */     
/*  58 */     if (forClass == Enum.class) {
/*  59 */       this._hasEnumDeserializer = true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDeserializers(Map<Class<?>, JsonDeserializer<?>> desers) {
/*  69 */     for (Map.Entry<Class<?>, JsonDeserializer<?>> entry : desers.entrySet()) {
/*  70 */       Class<?> cls = entry.getKey();
/*     */       
/*  72 */       JsonDeserializer<Object> deser = (JsonDeserializer<Object>)entry.getValue();
/*  73 */       addDeserializer(cls, deser);
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
/*     */   public JsonDeserializer<?> findArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*  89 */     return _find((JavaType)type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*  97 */     return _find(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 107 */     return _find((JavaType)type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 117 */     return _find((JavaType)type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/* 125 */     if (this._classMappings == null) {
/* 126 */       return null;
/*     */     }
/* 128 */     JsonDeserializer<?> deser = this._classMappings.get(new ClassKey(type));
/* 129 */     if (deser == null)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 134 */       if (this._hasEnumDeserializer && type.isEnum()) {
/* 135 */         deser = this._classMappings.get(new ClassKey(Enum.class));
/*     */       }
/*     */     }
/* 138 */     return deser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> findTreeNodeDeserializer(Class<? extends JsonNode> nodeType, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/* 146 */     if (this._classMappings == null) {
/* 147 */       return null;
/*     */     }
/* 149 */     return this._classMappings.get(new ClassKey(nodeType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
/* 159 */     return _find((JavaType)refType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 170 */     return _find((JavaType)type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 181 */     return _find((JavaType)type);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
/* 187 */     return (this._classMappings != null && this._classMappings
/* 188 */       .containsKey(new ClassKey(valueType)));
/*     */   }
/*     */   
/*     */   private final JsonDeserializer<?> _find(JavaType type) {
/* 192 */     if (this._classMappings == null) {
/* 193 */       return null;
/*     */     }
/* 195 */     return this._classMappings.get(new ClassKey(type.getRawClass()));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/module/SimpleDeserializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */