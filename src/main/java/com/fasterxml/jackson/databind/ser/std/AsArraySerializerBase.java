/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
/*     */ import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.node.ObjectNode;
/*     */ import com.fasterxml.jackson.databind.ser.ContainerSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AsArraySerializerBase<T>
/*     */   extends ContainerSerializer<T>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   protected final JavaType _elementType;
/*     */   protected final BeanProperty _property;
/*     */   protected final boolean _staticTyping;
/*     */   protected final Boolean _unwrapSingle;
/*     */   protected final TypeSerializer _valueTypeSerializer;
/*     */   protected final JsonSerializer<Object> _elementSerializer;
/*     */   protected PropertySerializerMap _dynamicSerializers;
/*     */   
/*     */   protected AsArraySerializerBase(Class<?> cls, JavaType et, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> elementSerializer) {
/*  81 */     this(cls, et, staticTyping, vts, (BeanProperty)null, elementSerializer, (Boolean)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected AsArraySerializerBase(Class<?> cls, JavaType et, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> elementSerializer) {
/*  92 */     this(cls, et, staticTyping, vts, property, elementSerializer, (Boolean)null);
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
/*     */   protected AsArraySerializerBase(Class<?> cls, JavaType elementType, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
/* 106 */     super(cls, false);
/* 107 */     this._elementType = elementType;
/*     */     
/* 109 */     this._staticTyping = (staticTyping || (elementType != null && elementType.isFinal()));
/* 110 */     this._valueTypeSerializer = vts;
/* 111 */     this._property = property;
/* 112 */     this._elementSerializer = (JsonSerializer)elementSerializer;
/* 113 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/* 114 */     this._unwrapSingle = unwrapSingle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AsArraySerializerBase(AsArraySerializerBase<?> src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
/* 122 */     super(src);
/* 123 */     this._elementType = src._elementType;
/* 124 */     this._staticTyping = src._staticTyping;
/* 125 */     this._valueTypeSerializer = vts;
/* 126 */     this._property = property;
/* 127 */     this._elementSerializer = (JsonSerializer)elementSerializer;
/*     */     
/* 129 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/* 130 */     this._unwrapSingle = unwrapSingle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected AsArraySerializerBase(AsArraySerializerBase<?> src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer) {
/* 140 */     this(src, property, vts, elementSerializer, src._unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final AsArraySerializerBase<T> withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer) {
/* 149 */     return withResolved(property, vts, elementSerializer, this._unwrapSingle);
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
/*     */   public abstract AsArraySerializerBase<T> withResolved(BeanProperty paramBeanProperty, TypeSerializer paramTypeSerializer, JsonSerializer<?> paramJsonSerializer, Boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
/* 176 */     TypeSerializer typeSer = this._valueTypeSerializer;
/* 177 */     if (typeSer != null) {
/* 178 */       typeSer = typeSer.forProperty(property);
/*     */     }
/* 180 */     JsonSerializer<?> ser = null;
/* 181 */     Boolean unwrapSingle = null;
/*     */ 
/*     */     
/* 184 */     if (property != null) {
/* 185 */       AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
/* 186 */       AnnotatedMember m = property.getMember();
/* 187 */       if (m != null) {
/* 188 */         Object serDef = intr.findContentSerializer((Annotated)m);
/* 189 */         if (serDef != null) {
/* 190 */           ser = serializers.serializerInstance((Annotated)m, serDef);
/*     */         }
/*     */       } 
/*     */     } 
/* 194 */     JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
/* 195 */     if (format != null) {
/* 196 */       unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
/*     */     }
/* 198 */     if (ser == null) {
/* 199 */       ser = this._elementSerializer;
/*     */     }
/*     */     
/* 202 */     ser = findContextualConvertingSerializer(serializers, property, ser);
/* 203 */     if (ser == null)
/*     */     {
/*     */       
/* 206 */       if (this._elementType != null && 
/* 207 */         this._staticTyping && !this._elementType.isJavaLangObject()) {
/* 208 */         ser = serializers.findContentValueSerializer(this._elementType, property);
/*     */       }
/*     */     }
/*     */     
/* 212 */     if (ser != this._elementSerializer || property != this._property || this._valueTypeSerializer != typeSer || 
/*     */ 
/*     */       
/* 215 */       !Objects.equals(this._unwrapSingle, unwrapSingle)) {
/* 216 */       return (JsonSerializer<?>)withResolved(property, typeSer, ser, unwrapSingle);
/*     */     }
/* 218 */     return (JsonSerializer<?>)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType getContentType() {
/* 229 */     return this._elementType;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> getContentSerializer() {
/* 234 */     return this._elementSerializer;
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
/*     */   public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 249 */     if (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) && 
/* 250 */       hasSingleElement(value)) {
/* 251 */       serializeContents(value, gen, provider);
/*     */       return;
/*     */     } 
/* 254 */     gen.writeStartArray(value);
/* 255 */     serializeContents(value, gen, provider);
/* 256 */     gen.writeEndArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeWithType(T value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 263 */     WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
/* 264 */         .typeId(value, JsonToken.START_ARRAY));
/*     */     
/* 266 */     g.setCurrentValue(value);
/* 267 */     serializeContents(value, g, provider);
/* 268 */     typeSer.writeTypeSuffix(g, typeIdDef);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void serializeContents(T paramT, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider) throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
/* 279 */     ObjectNode o = createSchemaNode("array", true);
/* 280 */     if (this._elementSerializer != null) {
/* 281 */       JsonNode schemaNode = null;
/* 282 */       if (this._elementSerializer instanceof SchemaAware) {
/* 283 */         schemaNode = ((SchemaAware)this._elementSerializer).getSchema(provider, null);
/*     */       }
/* 285 */       if (schemaNode == null) {
/* 286 */         schemaNode = JsonSchema.getDefaultSchemaNode();
/*     */       }
/* 288 */       o.set("items", schemaNode);
/*     */     } 
/* 290 */     return (JsonNode)o;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 297 */     JsonSerializer<?> valueSer = this._elementSerializer;
/* 298 */     if (valueSer == null)
/*     */     {
/*     */       
/* 301 */       if (this._elementType != null) {
/* 302 */         valueSer = visitor.getProvider().findContentValueSerializer(this._elementType, this._property);
/*     */       }
/*     */     }
/* 305 */     visitArrayFormat(visitor, typeHint, valueSer, this._elementType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
/* 311 */     PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
/*     */     
/* 313 */     if (map != result.map) {
/* 314 */       this._dynamicSerializers = result.map;
/*     */     }
/* 316 */     return result.serializer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider) throws JsonMappingException {
/* 322 */     PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
/* 323 */     if (map != result.map) {
/* 324 */       this._dynamicSerializers = result.map;
/*     */     }
/* 326 */     return result.serializer;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/AsArraySerializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */