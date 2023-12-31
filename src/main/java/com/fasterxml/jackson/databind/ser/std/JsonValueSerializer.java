/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
/*     */ import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class JsonValueSerializer
/*     */   extends StdSerializer<Object>
/*     */   implements ContextualSerializer, JsonFormatVisitable, SchemaAware
/*     */ {
/*     */   protected final AnnotatedMember _accessor;
/*     */   protected final TypeSerializer _valueTypeSerializer;
/*     */   protected final JsonSerializer<Object> _valueSerializer;
/*     */   protected final BeanProperty _property;
/*     */   protected final JavaType _valueType;
/*     */   protected final boolean _forceTypeInformation;
/*     */   protected transient PropertySerializerMap _dynamicSerializers;
/*     */   
/*     */   public JsonValueSerializer(AnnotatedMember accessor, TypeSerializer vts, JsonSerializer<?> ser) {
/* 101 */     super(accessor.getType());
/* 102 */     this._accessor = accessor;
/* 103 */     this._valueType = accessor.getType();
/* 104 */     this._valueTypeSerializer = vts;
/* 105 */     this._valueSerializer = (JsonSerializer)ser;
/* 106 */     this._property = null;
/* 107 */     this._forceTypeInformation = true;
/* 108 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonValueSerializer(AnnotatedMember accessor, JsonSerializer<?> ser) {
/* 116 */     this(accessor, (TypeSerializer)null, ser);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonValueSerializer(JsonValueSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> ser, boolean forceTypeInfo) {
/* 124 */     super(_notNullClass(src.handledType()));
/* 125 */     this._accessor = src._accessor;
/* 126 */     this._valueType = src._valueType;
/* 127 */     this._valueTypeSerializer = vts;
/* 128 */     this._valueSerializer = (JsonSerializer)ser;
/* 129 */     this._property = property;
/* 130 */     this._forceTypeInformation = forceTypeInfo;
/* 131 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */   }
/*     */ 
/*     */   
/*     */   private static final Class<Object> _notNullClass(Class<?> cls) {
/* 136 */     return (cls == null) ? Object.class : (Class)cls;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonValueSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> ser, boolean forceTypeInfo) {
/* 142 */     if (this._property == property && this._valueTypeSerializer == vts && this._valueSerializer == ser && forceTypeInfo == this._forceTypeInformation)
/*     */     {
/*     */       
/* 145 */       return this;
/*     */     }
/* 147 */     return new JsonValueSerializer(this, property, vts, ser, forceTypeInfo);
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
/*     */   public boolean isEmpty(SerializerProvider ctxt, Object bean) {
/* 160 */     Object referenced = this._accessor.getValue(bean);
/* 161 */     if (referenced == null) {
/* 162 */       return true;
/*     */     }
/* 164 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 165 */     if (ser == null) {
/*     */       try {
/* 167 */         ser = _findDynamicSerializer(ctxt, referenced.getClass());
/* 168 */       } catch (JsonMappingException e) {
/* 169 */         throw new RuntimeJsonMappingException(e);
/*     */       } 
/*     */     }
/* 172 */     return ser.isEmpty(ctxt, referenced);
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
/*     */   public JsonSerializer<?> createContextual(SerializerProvider ctxt, BeanProperty property) throws JsonMappingException {
/* 190 */     TypeSerializer typeSer = this._valueTypeSerializer;
/* 191 */     if (typeSer != null) {
/* 192 */       typeSer = typeSer.forProperty(property);
/*     */     }
/* 194 */     JsonSerializer<?> ser = this._valueSerializer;
/* 195 */     if (ser == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 200 */       if (ctxt.isEnabled(MapperFeature.USE_STATIC_TYPING) || this._valueType.isFinal()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 206 */         ser = ctxt.findPrimaryPropertySerializer(this._valueType, property);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 211 */         boolean forceTypeInformation = isNaturalTypeWithStdHandling(this._valueType.getRawClass(), ser);
/* 212 */         return withResolved(property, typeSer, ser, forceTypeInformation);
/*     */       } 
/*     */       
/* 215 */       if (property != this._property) {
/* 216 */         return withResolved(property, typeSer, ser, this._forceTypeInformation);
/*     */       }
/*     */     } else {
/*     */       
/* 220 */       ser = ctxt.handlePrimaryContextualization(ser, property);
/* 221 */       return withResolved(property, typeSer, ser, this._forceTypeInformation);
/*     */     } 
/* 223 */     return this;
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
/*     */   public void serialize(Object bean, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
/*     */     Object value;
/*     */     try {
/* 237 */       value = this._accessor.getValue(bean);
/* 238 */     } catch (Exception e) {
/* 239 */       value = null;
/* 240 */       wrapAndThrow(ctxt, e, bean, this._accessor.getName() + "()");
/*     */     } 
/*     */     
/* 243 */     if (value == null) {
/* 244 */       ctxt.defaultSerializeNull(gen);
/*     */     } else {
/* 246 */       JsonSerializer<Object> ser = this._valueSerializer;
/* 247 */       if (ser == null) {
/* 248 */         ser = _findDynamicSerializer(ctxt, value.getClass());
/*     */       }
/* 250 */       if (this._valueTypeSerializer != null) {
/* 251 */         ser.serializeWithType(value, gen, ctxt, this._valueTypeSerializer);
/*     */       } else {
/* 253 */         ser.serialize(value, gen, ctxt);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer0) throws IOException {
/*     */     Object value;
/*     */     try {
/* 265 */       value = this._accessor.getValue(bean);
/* 266 */     } catch (Exception e) {
/* 267 */       value = null;
/* 268 */       wrapAndThrow(ctxt, e, bean, this._accessor.getName() + "()");
/*     */     } 
/*     */ 
/*     */     
/* 272 */     if (value == null) {
/* 273 */       ctxt.defaultSerializeNull(gen);
/*     */       return;
/*     */     } 
/* 276 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 277 */     if (ser == null) {
/* 278 */       ser = _findDynamicSerializer(ctxt, value.getClass());
/*     */ 
/*     */     
/*     */     }
/* 282 */     else if (this._forceTypeInformation) {
/*     */       
/* 284 */       WritableTypeId typeIdDef = typeSer0.writeTypePrefix(gen, typeSer0
/* 285 */           .typeId(bean, JsonToken.VALUE_STRING));
/* 286 */       ser.serialize(value, gen, ctxt);
/* 287 */       typeSer0.writeTypeSuffix(gen, typeIdDef);
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 295 */     TypeSerializerRerouter rr = new TypeSerializerRerouter(typeSer0, bean);
/* 296 */     ser.serializeWithType(value, gen, ctxt, rr);
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
/*     */   public JsonNode getSchema(SerializerProvider ctxt, Type typeHint) throws JsonMappingException {
/* 310 */     if (this._valueSerializer instanceof SchemaAware) {
/* 311 */       return ((SchemaAware)this._valueSerializer).getSchema(ctxt, null);
/*     */     }
/* 313 */     return JsonSchema.getDefaultSchemaNode();
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
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 329 */     Class<?> declaring = this._accessor.getDeclaringClass();
/* 330 */     if (declaring != null && ClassUtil.isEnumType(declaring) && 
/* 331 */       _acceptJsonFormatVisitorForEnum(visitor, typeHint, declaring)) {
/*     */       return;
/*     */     }
/*     */     
/* 335 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 336 */     if (ser == null) {
/* 337 */       ser = visitor.getProvider().findTypedValueSerializer(this._valueType, false, this._property);
/* 338 */       if (ser == null) {
/* 339 */         visitor.expectAnyFormat(typeHint);
/*     */         return;
/*     */       } 
/*     */     } 
/* 343 */     ser.acceptJsonFormatVisitor(visitor, this._valueType);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _acceptJsonFormatVisitorForEnum(JsonFormatVisitorWrapper visitor, JavaType typeHint, Class<?> enumType) throws JsonMappingException {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: aload_2
/*     */     //   2: invokeinterface expectStringFormat : (Lcom/fasterxml/jackson/databind/JavaType;)Lcom/fasterxml/jackson/databind/jsonFormatVisitors/JsonStringFormatVisitor;
/*     */     //   7: astore #4
/*     */     //   9: aload #4
/*     */     //   11: ifnull -> 160
/*     */     //   14: new java/util/LinkedHashSet
/*     */     //   17: dup
/*     */     //   18: invokespecial <init> : ()V
/*     */     //   21: astore #5
/*     */     //   23: aload_3
/*     */     //   24: invokevirtual getEnumConstants : ()[Ljava/lang/Object;
/*     */     //   27: astore #6
/*     */     //   29: aload #6
/*     */     //   31: arraylength
/*     */     //   32: istore #7
/*     */     //   34: iconst_0
/*     */     //   35: istore #8
/*     */     //   37: iload #8
/*     */     //   39: iload #7
/*     */     //   41: if_icmpge -> 151
/*     */     //   44: aload #6
/*     */     //   46: iload #8
/*     */     //   48: aaload
/*     */     //   49: astore #9
/*     */     //   51: aload #5
/*     */     //   53: aload_0
/*     */     //   54: getfield _accessor : Lcom/fasterxml/jackson/databind/introspect/AnnotatedMember;
/*     */     //   57: aload #9
/*     */     //   59: invokevirtual getValue : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   62: invokestatic valueOf : (Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   65: invokeinterface add : (Ljava/lang/Object;)Z
/*     */     //   70: pop
/*     */     //   71: goto -> 145
/*     */     //   74: astore #10
/*     */     //   76: aload #10
/*     */     //   78: astore #11
/*     */     //   80: aload #11
/*     */     //   82: instanceof java/lang/reflect/InvocationTargetException
/*     */     //   85: ifeq -> 106
/*     */     //   88: aload #11
/*     */     //   90: invokevirtual getCause : ()Ljava/lang/Throwable;
/*     */     //   93: ifnull -> 106
/*     */     //   96: aload #11
/*     */     //   98: invokevirtual getCause : ()Ljava/lang/Throwable;
/*     */     //   101: astore #11
/*     */     //   103: goto -> 80
/*     */     //   106: aload #11
/*     */     //   108: invokestatic throwIfError : (Ljava/lang/Throwable;)Ljava/lang/Throwable;
/*     */     //   111: pop
/*     */     //   112: aload #11
/*     */     //   114: aload #9
/*     */     //   116: new java/lang/StringBuilder
/*     */     //   119: dup
/*     */     //   120: invokespecial <init> : ()V
/*     */     //   123: aload_0
/*     */     //   124: getfield _accessor : Lcom/fasterxml/jackson/databind/introspect/AnnotatedMember;
/*     */     //   127: invokevirtual getName : ()Ljava/lang/String;
/*     */     //   130: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   133: ldc '()'
/*     */     //   135: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   138: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   141: invokestatic wrapWithPath : (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;)Lcom/fasterxml/jackson/databind/JsonMappingException;
/*     */     //   144: athrow
/*     */     //   145: iinc #8, 1
/*     */     //   148: goto -> 37
/*     */     //   151: aload #4
/*     */     //   153: aload #5
/*     */     //   155: invokeinterface enumTypes : (Ljava/util/Set;)V
/*     */     //   160: iconst_1
/*     */     //   161: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #360	-> 0
/*     */     //   #361	-> 9
/*     */     //   #362	-> 14
/*     */     //   #363	-> 23
/*     */     //   #368	-> 51
/*     */     //   #376	-> 71
/*     */     //   #369	-> 74
/*     */     //   #370	-> 76
/*     */     //   #371	-> 80
/*     */     //   #372	-> 96
/*     */     //   #374	-> 106
/*     */     //   #375	-> 112
/*     */     //   #363	-> 145
/*     */     //   #378	-> 151
/*     */     //   #380	-> 160
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   80	65	11	t	Ljava/lang/Throwable;
/*     */     //   76	69	10	e	Ljava/lang/Exception;
/*     */     //   51	94	9	en	Ljava/lang/Object;
/*     */     //   23	137	5	enums	Ljava/util/Set;
/*     */     //   0	162	0	this	Lcom/fasterxml/jackson/databind/ser/std/JsonValueSerializer;
/*     */     //   0	162	1	visitor	Lcom/fasterxml/jackson/databind/jsonFormatVisitors/JsonFormatVisitorWrapper;
/*     */     //   0	162	2	typeHint	Lcom/fasterxml/jackson/databind/JavaType;
/*     */     //   0	162	3	enumType	Ljava/lang/Class;
/*     */     //   9	153	4	stringVisitor	Lcom/fasterxml/jackson/databind/jsonFormatVisitors/JsonStringFormatVisitor;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   23	137	5	enums	Ljava/util/Set<Ljava/lang/String;>;
/*     */     //   0	162	3	enumType	Ljava/lang/Class<*>;
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   51	71	74	java/lang/Exception
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isNaturalTypeWithStdHandling(Class<?> rawType, JsonSerializer<?> ser) {
/* 392 */     if (rawType.isPrimitive()) {
/* 393 */       if (rawType != int.class && rawType != boolean.class && rawType != double.class) {
/* 394 */         return false;
/*     */       }
/*     */     }
/* 397 */     else if (rawType != String.class && rawType != Integer.class && rawType != Boolean.class && rawType != Double.class) {
/*     */       
/* 399 */       return false;
/*     */     } 
/*     */     
/* 402 */     return isDefaultSerializer(ser);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonSerializer<Object> _findDynamicSerializer(SerializerProvider ctxt, Class<?> valueClass) throws JsonMappingException {
/* 409 */     JsonSerializer<Object> serializer = this._dynamicSerializers.serializerFor(valueClass);
/* 410 */     if (serializer == null) {
/* 411 */       if (this._valueType.hasGenericTypes()) {
/* 412 */         JavaType fullType = ctxt.constructSpecializedType(this._valueType, valueClass);
/* 413 */         serializer = ctxt.findPrimaryPropertySerializer(fullType, this._property);
/* 414 */         PropertySerializerMap.SerializerAndMapResult result = this._dynamicSerializers.addSerializer(fullType, serializer);
/* 415 */         this._dynamicSerializers = result.map;
/*     */       } else {
/* 417 */         serializer = ctxt.findPrimaryPropertySerializer(valueClass, this._property);
/* 418 */         PropertySerializerMap.SerializerAndMapResult result = this._dynamicSerializers.addSerializer(valueClass, serializer);
/* 419 */         this._dynamicSerializers = result.map;
/*     */       } 
/*     */     }
/* 422 */     return serializer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 452 */     return "(@JsonValue serializer for method " + this._accessor.getDeclaringClass() + "#" + this._accessor.getName() + ")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class TypeSerializerRerouter
/*     */     extends TypeSerializer
/*     */   {
/*     */     protected final TypeSerializer _typeSerializer;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected final Object _forObject;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TypeSerializerRerouter(TypeSerializer ts, Object ob) {
/* 473 */       this._typeSerializer = ts;
/* 474 */       this._forObject = ob;
/*     */     }
/*     */ 
/*     */     
/*     */     public TypeSerializer forProperty(BeanProperty prop) {
/* 479 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonTypeInfo.As getTypeInclusion() {
/* 484 */       return this._typeSerializer.getTypeInclusion();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getPropertyName() {
/* 489 */       return this._typeSerializer.getPropertyName();
/*     */     }
/*     */ 
/*     */     
/*     */     public TypeIdResolver getTypeIdResolver() {
/* 494 */       return this._typeSerializer.getTypeIdResolver();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId typeId) throws IOException {
/* 503 */       typeId.forValue = this._forObject;
/* 504 */       return this._typeSerializer.writeTypePrefix(g, typeId);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId typeId) throws IOException {
/* 511 */       return this._typeSerializer.writeTypeSuffix(g, typeId);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeTypePrefixForScalar(Object value, JsonGenerator gen) throws IOException {
/* 519 */       this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeTypePrefixForObject(Object value, JsonGenerator gen) throws IOException {
/* 525 */       this._typeSerializer.writeTypePrefixForObject(this._forObject, gen);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeTypePrefixForArray(Object value, JsonGenerator gen) throws IOException {
/* 531 */       this._typeSerializer.writeTypePrefixForArray(this._forObject, gen);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeTypeSuffixForScalar(Object value, JsonGenerator gen) throws IOException {
/* 537 */       this._typeSerializer.writeTypeSuffixForScalar(this._forObject, gen);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeTypeSuffixForObject(Object value, JsonGenerator gen) throws IOException {
/* 543 */       this._typeSerializer.writeTypeSuffixForObject(this._forObject, gen);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeTypeSuffixForArray(Object value, JsonGenerator gen) throws IOException {
/* 549 */       this._typeSerializer.writeTypeSuffixForArray(this._forObject, gen);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeTypePrefixForScalar(Object value, JsonGenerator gen, Class<?> type) throws IOException {
/* 555 */       this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen, type);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeTypePrefixForObject(Object value, JsonGenerator gen, Class<?> type) throws IOException {
/* 561 */       this._typeSerializer.writeTypePrefixForObject(this._forObject, gen, type);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeTypePrefixForArray(Object value, JsonGenerator gen, Class<?> type) throws IOException {
/* 567 */       this._typeSerializer.writeTypePrefixForArray(this._forObject, gen, type);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeCustomTypePrefixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException {
/* 580 */       this._typeSerializer.writeCustomTypePrefixForScalar(this._forObject, gen, typeId);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeCustomTypePrefixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
/* 586 */       this._typeSerializer.writeCustomTypePrefixForObject(this._forObject, gen, typeId);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeCustomTypePrefixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
/* 592 */       this._typeSerializer.writeCustomTypePrefixForArray(this._forObject, gen, typeId);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException {
/* 598 */       this._typeSerializer.writeCustomTypeSuffixForScalar(this._forObject, gen, typeId);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeCustomTypeSuffixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
/* 604 */       this._typeSerializer.writeCustomTypeSuffixForObject(this._forObject, gen, typeId);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public void writeCustomTypeSuffixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
/* 610 */       this._typeSerializer.writeCustomTypeSuffixForArray(this._forObject, gen, typeId);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/JsonValueSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */