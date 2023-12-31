/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.EnumValues;
/*     */ import java.io.IOException;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ 
/*     */ public abstract class StdKeySerializers {
/*  18 */   protected static final JsonSerializer<Object> DEFAULT_KEY_SERIALIZER = new StdKeySerializer();
/*     */   
/*  20 */   protected static final JsonSerializer<Object> DEFAULT_STRING_SERIALIZER = new StringKeySerializer();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonSerializer<Object> getStdKeySerializer(SerializationConfig config, Class<?> rawKeyType, boolean useDefault) {
/*  38 */     if (rawKeyType == null || rawKeyType == Object.class) {
/*  39 */       return new Dynamic();
/*     */     }
/*  41 */     if (rawKeyType == String.class) {
/*  42 */       return DEFAULT_STRING_SERIALIZER;
/*     */     }
/*  44 */     if (rawKeyType.isPrimitive()) {
/*  45 */       rawKeyType = ClassUtil.wrapperType(rawKeyType);
/*     */     }
/*  47 */     if (rawKeyType == Integer.class) {
/*  48 */       return new Default(5, rawKeyType);
/*     */     }
/*  50 */     if (rawKeyType == Long.class) {
/*  51 */       return new Default(6, rawKeyType);
/*     */     }
/*  53 */     if (rawKeyType.isPrimitive() || Number.class.isAssignableFrom(rawKeyType))
/*     */     {
/*     */       
/*  56 */       return new Default(8, rawKeyType);
/*     */     }
/*  58 */     if (rawKeyType == Class.class) {
/*  59 */       return new Default(3, rawKeyType);
/*     */     }
/*  61 */     if (Date.class.isAssignableFrom(rawKeyType)) {
/*  62 */       return new Default(1, rawKeyType);
/*     */     }
/*  64 */     if (Calendar.class.isAssignableFrom(rawKeyType)) {
/*  65 */       return new Default(2, rawKeyType);
/*     */     }
/*     */     
/*  68 */     if (rawKeyType == UUID.class) {
/*  69 */       return new Default(8, rawKeyType);
/*     */     }
/*  71 */     if (rawKeyType == byte[].class) {
/*  72 */       return new Default(7, rawKeyType);
/*     */     }
/*  74 */     if (useDefault)
/*     */     {
/*  76 */       return new Default(8, rawKeyType);
/*     */     }
/*  78 */     return null;
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
/*     */   public static JsonSerializer<Object> getFallbackKeySerializer(SerializationConfig config, Class<?> rawKeyType) {
/*  91 */     if (rawKeyType != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  98 */       if (rawKeyType == Enum.class) {
/*  99 */         return new Dynamic();
/*     */       }
/*     */ 
/*     */       
/* 103 */       if (ClassUtil.isEnumType(rawKeyType)) {
/* 104 */         return EnumKeySerializer.construct(rawKeyType, 
/* 105 */             EnumValues.constructFromName((MapperConfig)config, rawKeyType));
/*     */       }
/*     */     } 
/*     */     
/* 109 */     return new Default(8, rawKeyType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static JsonSerializer<Object> getDefault() {
/* 117 */     return DEFAULT_KEY_SERIALIZER;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Default
/*     */     extends StdSerializer<Object>
/*     */   {
/*     */     static final int TYPE_DATE = 1;
/*     */ 
/*     */     
/*     */     static final int TYPE_CALENDAR = 2;
/*     */ 
/*     */     
/*     */     static final int TYPE_CLASS = 3;
/*     */     
/*     */     static final int TYPE_ENUM = 4;
/*     */     
/*     */     static final int TYPE_INTEGER = 5;
/*     */     
/*     */     static final int TYPE_LONG = 6;
/*     */     
/*     */     static final int TYPE_BYTE_ARRAY = 7;
/*     */     
/*     */     static final int TYPE_TO_STRING = 8;
/*     */     
/*     */     protected final int _typeId;
/*     */ 
/*     */     
/*     */     public Default(int typeId, Class<?> type) {
/* 147 */       super(type, false);
/* 148 */       this._typeId = typeId;
/*     */     }
/*     */     
/*     */     public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
/*     */       String key, encoded;
/* 153 */       switch (this._typeId) {
/*     */         case 1:
/* 155 */           provider.defaultSerializeDateKey((Date)value, g);
/*     */           return;
/*     */         case 2:
/* 158 */           provider.defaultSerializeDateKey(((Calendar)value).getTimeInMillis(), g);
/*     */           return;
/*     */         case 3:
/* 161 */           g.writeFieldName(((Class)value).getName());
/*     */           return;
/*     */ 
/*     */ 
/*     */         
/*     */         case 4:
/* 167 */           if (provider.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
/* 168 */             key = value.toString();
/*     */           } else {
/* 170 */             Enum<?> e = (Enum)value;
/*     */             
/* 172 */             if (provider.isEnabled(SerializationFeature.WRITE_ENUM_KEYS_USING_INDEX)) {
/* 173 */               key = String.valueOf(e.ordinal());
/*     */             } else {
/* 175 */               key = e.name();
/*     */             } 
/*     */           } 
/* 178 */           g.writeFieldName(key);
/*     */           return;
/*     */         
/*     */         case 5:
/*     */         case 6:
/* 183 */           g.writeFieldId(((Number)value).longValue());
/*     */           return;
/*     */         
/*     */         case 7:
/* 187 */           encoded = provider.getConfig().getBase64Variant().encode((byte[])value);
/* 188 */           g.writeFieldName(encoded);
/*     */           return;
/*     */       } 
/*     */ 
/*     */       
/* 193 */       g.writeFieldName(value.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Dynamic
/*     */     extends StdSerializer<Object>
/*     */   {
/*     */     protected transient PropertySerializerMap _dynamicSerializers;
/*     */ 
/*     */ 
/*     */     
/*     */     public Dynamic() {
/* 208 */       super(String.class, false);
/* 209 */       this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */     }
/*     */ 
/*     */     
/*     */     Object readResolve() {
/* 214 */       this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/* 215 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 222 */       Class<?> cls = value.getClass();
/* 223 */       PropertySerializerMap m = this._dynamicSerializers;
/* 224 */       JsonSerializer<Object> ser = m.serializerFor(cls);
/* 225 */       if (ser == null) {
/* 226 */         ser = _findAndAddDynamic(m, cls, provider);
/*     */       }
/* 228 */       ser.serialize(value, g, provider);
/*     */     }
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 233 */       visitStringFormat(visitor, typeHint);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
/* 240 */       if (type == Object.class) {
/*     */         
/* 242 */         JsonSerializer<Object> ser = new StdKeySerializers.Default(8, type);
/* 243 */         this._dynamicSerializers = map.newWith(type, ser);
/* 244 */         return ser;
/*     */       } 
/*     */ 
/*     */       
/* 248 */       PropertySerializerMap.SerializerAndMapResult result = map.findAndAddKeySerializer(type, provider, null);
/*     */       
/* 250 */       if (map != result.map) {
/* 251 */         this._dynamicSerializers = result.map;
/*     */       }
/* 253 */       return result.serializer;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class StringKeySerializer
/*     */     extends StdSerializer<Object>
/*     */   {
/*     */     public StringKeySerializer() {
/* 262 */       super(String.class, false);
/*     */     }
/*     */     
/*     */     public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 266 */       g.writeFieldName((String)value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class EnumKeySerializer
/*     */     extends StdSerializer<Object>
/*     */   {
/*     */     protected final EnumValues _values;
/*     */ 
/*     */ 
/*     */     
/*     */     protected EnumKeySerializer(Class<?> enumType, EnumValues values) {
/* 280 */       super(enumType, false);
/* 281 */       this._values = values;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public static EnumKeySerializer construct(Class<?> enumType, EnumValues enumValues) {
/* 287 */       return new EnumKeySerializer(enumType, enumValues);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(Object value, JsonGenerator g, SerializerProvider serializers) throws IOException {
/* 294 */       if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
/* 295 */         g.writeFieldName(value.toString());
/*     */         return;
/*     */       } 
/* 298 */       Enum<?> en = (Enum)value;
/*     */       
/* 300 */       if (serializers.isEnabled(SerializationFeature.WRITE_ENUM_KEYS_USING_INDEX)) {
/* 301 */         g.writeFieldName(String.valueOf(en.ordinal()));
/*     */         return;
/*     */       } 
/* 304 */       g.writeFieldName(this._values.serializedValueFor(en));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/StdKeySerializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */