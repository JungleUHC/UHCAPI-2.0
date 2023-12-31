/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContainerSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import java.io.IOException;
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
/*     */ @JacksonStdImpl
/*     */ public class ObjectArraySerializer
/*     */   extends ArraySerializerBase<Object[]>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   protected final boolean _staticTyping;
/*     */   protected final JavaType _elementType;
/*     */   protected final TypeSerializer _valueTypeSerializer;
/*     */   protected JsonSerializer<Object> _elementSerializer;
/*     */   protected PropertySerializerMap _dynamicSerializers;
/*     */   
/*     */   public ObjectArraySerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> elementSerializer) {
/*  65 */     super(Object[].class);
/*  66 */     this._elementType = elemType;
/*  67 */     this._staticTyping = staticTyping;
/*  68 */     this._valueTypeSerializer = vts;
/*  69 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*  70 */     this._elementSerializer = elementSerializer;
/*     */   }
/*     */ 
/*     */   
/*     */   public ObjectArraySerializer(ObjectArraySerializer src, TypeSerializer vts) {
/*  75 */     super(src);
/*  76 */     this._elementType = src._elementType;
/*  77 */     this._valueTypeSerializer = vts;
/*  78 */     this._staticTyping = src._staticTyping;
/*     */ 
/*     */     
/*  81 */     this._dynamicSerializers = src._dynamicSerializers;
/*  82 */     this._elementSerializer = src._elementSerializer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectArraySerializer(ObjectArraySerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
/*  90 */     super(src, property, unwrapSingle);
/*  91 */     this._elementType = src._elementType;
/*  92 */     this._valueTypeSerializer = vts;
/*  93 */     this._staticTyping = src._staticTyping;
/*     */     
/*  95 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*  96 */     this._elementSerializer = (JsonSerializer)elementSerializer;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
/* 101 */     return (JsonSerializer<?>)new ObjectArraySerializer(this, prop, this._valueTypeSerializer, this._elementSerializer, unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
/* 107 */     return new ObjectArraySerializer(this._elementType, this._staticTyping, vts, this._elementSerializer);
/*     */   }
/*     */ 
/*     */   
/*     */   public ObjectArraySerializer withResolved(BeanProperty prop, TypeSerializer vts, JsonSerializer<?> ser, Boolean unwrapSingle) {
/* 112 */     if (this._property == prop && ser == this._elementSerializer && this._valueTypeSerializer == vts && 
/* 113 */       Objects.equals(this._unwrapSingle, unwrapSingle)) {
/* 114 */       return this;
/*     */     }
/* 116 */     return new ObjectArraySerializer(this, prop, vts, ser, unwrapSingle);
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
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
/* 130 */     TypeSerializer vts = this._valueTypeSerializer;
/* 131 */     if (vts != null) {
/* 132 */       vts = vts.forProperty(property);
/*     */     }
/* 134 */     JsonSerializer<?> ser = null;
/* 135 */     Boolean unwrapSingle = null;
/*     */ 
/*     */     
/* 138 */     if (property != null) {
/* 139 */       AnnotatedMember m = property.getMember();
/* 140 */       AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
/* 141 */       if (m != null) {
/* 142 */         Object serDef = intr.findContentSerializer((Annotated)m);
/* 143 */         if (serDef != null) {
/* 144 */           ser = serializers.serializerInstance((Annotated)m, serDef);
/*     */         }
/*     */       } 
/*     */     } 
/* 148 */     JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
/* 149 */     if (format != null) {
/* 150 */       unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
/*     */     }
/* 152 */     if (ser == null) {
/* 153 */       ser = this._elementSerializer;
/*     */     }
/*     */     
/* 156 */     ser = findContextualConvertingSerializer(serializers, property, ser);
/* 157 */     if (ser == null)
/*     */     {
/*     */       
/* 160 */       if (this._elementType != null && 
/* 161 */         this._staticTyping && !this._elementType.isJavaLangObject()) {
/* 162 */         ser = serializers.findContentValueSerializer(this._elementType, property);
/*     */       }
/*     */     }
/*     */     
/* 166 */     return (JsonSerializer<?>)withResolved(property, vts, ser, unwrapSingle);
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
/* 177 */     return this._elementType;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> getContentSerializer() {
/* 182 */     return this._elementSerializer;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty(SerializerProvider prov, Object[] value) {
/* 187 */     return (value.length == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasSingleElement(Object[] value) {
/* 192 */     return (value.length == 1);
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
/*     */   public final void serialize(Object[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 204 */     int len = value.length;
/* 205 */     if (len == 1 && ((
/* 206 */       this._unwrapSingle == null && provider
/* 207 */       .isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
/*     */       
/* 209 */       serializeContents(value, gen, provider);
/*     */       
/*     */       return;
/*     */     } 
/* 213 */     gen.writeStartArray(value, len);
/* 214 */     serializeContents(value, gen, provider);
/* 215 */     gen.writeEndArray();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeContents(Object[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 221 */     int len = value.length;
/* 222 */     if (len == 0) {
/*     */       return;
/*     */     }
/* 225 */     if (this._elementSerializer != null) {
/* 226 */       serializeContentsUsing(value, gen, provider, this._elementSerializer);
/*     */       return;
/*     */     } 
/* 229 */     if (this._valueTypeSerializer != null) {
/* 230 */       serializeTypedContents(value, gen, provider);
/*     */       return;
/*     */     } 
/* 233 */     int i = 0;
/* 234 */     Object elem = null;
/*     */     try {
/* 236 */       PropertySerializerMap serializers = this._dynamicSerializers;
/* 237 */       for (; i < len; i++) {
/* 238 */         elem = value[i];
/* 239 */         if (elem == null)
/* 240 */         { provider.defaultSerializeNull(gen); }
/*     */         else
/*     */         
/* 243 */         { Class<?> cc = elem.getClass();
/* 244 */           JsonSerializer<Object> serializer = serializers.serializerFor(cc);
/* 245 */           if (serializer == null) {
/* 246 */             if (this._elementType.hasGenericTypes()) {
/* 247 */               serializer = _findAndAddDynamic(serializers, provider
/* 248 */                   .constructSpecializedType(this._elementType, cc), provider);
/*     */             } else {
/* 250 */               serializer = _findAndAddDynamic(serializers, cc, provider);
/*     */             } 
/*     */           }
/* 253 */           serializer.serialize(elem, gen, provider); } 
/*     */       } 
/* 255 */     } catch (Exception e) {
/* 256 */       wrapAndThrow(provider, e, elem, i);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeContentsUsing(Object[] value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException {
/* 263 */     int len = value.length;
/* 264 */     TypeSerializer typeSer = this._valueTypeSerializer;
/*     */     
/* 266 */     int i = 0;
/* 267 */     Object elem = null;
/*     */     try {
/* 269 */       for (; i < len; i++) {
/* 270 */         elem = value[i];
/* 271 */         if (elem == null) {
/* 272 */           provider.defaultSerializeNull(jgen);
/*     */         
/*     */         }
/* 275 */         else if (typeSer == null) {
/* 276 */           ser.serialize(elem, jgen, provider);
/*     */         } else {
/* 278 */           ser.serializeWithType(elem, jgen, provider, typeSer);
/*     */         } 
/*     */       } 
/* 281 */     } catch (Exception e) {
/* 282 */       wrapAndThrow(provider, e, elem, i);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void serializeTypedContents(Object[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
/* 288 */     int len = value.length;
/* 289 */     TypeSerializer typeSer = this._valueTypeSerializer;
/* 290 */     int i = 0;
/* 291 */     Object elem = null;
/*     */     try {
/* 293 */       PropertySerializerMap serializers = this._dynamicSerializers;
/* 294 */       for (; i < len; i++) {
/* 295 */         elem = value[i];
/* 296 */         if (elem == null)
/* 297 */         { provider.defaultSerializeNull(jgen); }
/*     */         else
/*     */         
/* 300 */         { Class<?> cc = elem.getClass();
/* 301 */           JsonSerializer<Object> serializer = serializers.serializerFor(cc);
/* 302 */           if (serializer == null) {
/* 303 */             serializer = _findAndAddDynamic(serializers, cc, provider);
/*     */           }
/* 305 */           serializer.serializeWithType(elem, jgen, provider, typeSer); } 
/*     */       } 
/* 307 */     } catch (Exception e) {
/* 308 */       wrapAndThrow(provider, e, elem, i);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 316 */     JsonArrayFormatVisitor arrayVisitor = visitor.expectArrayFormat(typeHint);
/* 317 */     if (arrayVisitor != null) {
/* 318 */       JavaType contentType = this._elementType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 329 */       JsonSerializer<?> valueSer = this._elementSerializer;
/* 330 */       if (valueSer == null) {
/* 331 */         valueSer = visitor.getProvider().findContentValueSerializer(contentType, this._property);
/*     */       }
/* 333 */       arrayVisitor.itemsFormat((JsonFormatVisitable)valueSer, contentType);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
/* 340 */     PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
/*     */     
/* 342 */     if (map != result.map) {
/* 343 */       this._dynamicSerializers = result.map;
/*     */     }
/* 345 */     return result.serializer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider) throws JsonMappingException {
/* 351 */     PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
/*     */     
/* 353 */     if (map != result.map) {
/* 354 */       this._dynamicSerializers = result.map;
/*     */     }
/* 356 */     return result.serializer;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/ObjectArraySerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */