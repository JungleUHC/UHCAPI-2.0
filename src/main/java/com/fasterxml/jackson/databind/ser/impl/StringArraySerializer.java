/*     */ package com.fasterxml.jackson.databind.ser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContainerSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.std.ArraySerializerBase;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class StringArraySerializer
/*     */   extends ArraySerializerBase<String[]>
/*     */   implements ContextualSerializer
/*     */ {
/*  35 */   private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(String.class);
/*     */   
/*  37 */   public static final StringArraySerializer instance = new StringArraySerializer();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonSerializer<Object> _elementSerializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StringArraySerializer() {
/*  52 */     super(String[].class);
/*  53 */     this._elementSerializer = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public StringArraySerializer(StringArraySerializer src, BeanProperty prop, JsonSerializer<?> ser, Boolean unwrapSingle) {
/*  59 */     super(src, prop, unwrapSingle);
/*  60 */     this._elementSerializer = (JsonSerializer)ser;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
/*  65 */     return (JsonSerializer<?>)new StringArraySerializer(this, prop, this._elementSerializer, unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
/*  74 */     return (ContainerSerializer<?>)this;
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
/*     */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/*  92 */     JsonSerializer<?> ser = null;
/*     */ 
/*     */     
/*  95 */     if (property != null) {
/*  96 */       AnnotationIntrospector ai = provider.getAnnotationIntrospector();
/*  97 */       AnnotatedMember m = property.getMember();
/*  98 */       if (m != null) {
/*  99 */         Object serDef = ai.findContentSerializer((Annotated)m);
/* 100 */         if (serDef != null) {
/* 101 */           ser = provider.serializerInstance((Annotated)m, serDef);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 107 */     Boolean unwrapSingle = findFormatFeature(provider, property, String[].class, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
/*     */     
/* 109 */     if (ser == null) {
/* 110 */       ser = this._elementSerializer;
/*     */     }
/*     */     
/* 113 */     ser = findContextualConvertingSerializer(provider, property, ser);
/* 114 */     if (ser == null) {
/* 115 */       ser = provider.findContentValueSerializer(String.class, property);
/*     */     }
/*     */     
/* 118 */     if (isDefaultSerializer(ser)) {
/* 119 */       ser = null;
/*     */     }
/*     */     
/* 122 */     if (ser == this._elementSerializer && Objects.equals(unwrapSingle, this._unwrapSingle)) {
/* 123 */       return (JsonSerializer<?>)this;
/*     */     }
/* 125 */     return (JsonSerializer<?>)new StringArraySerializer(this, property, ser, unwrapSingle);
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
/* 136 */     return VALUE_TYPE;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> getContentSerializer() {
/* 141 */     return this._elementSerializer;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty(SerializerProvider prov, String[] value) {
/* 146 */     return (value.length == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasSingleElement(String[] value) {
/* 151 */     return (value.length == 1);
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
/*     */   public final void serialize(String[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 164 */     int len = value.length;
/* 165 */     if (len == 1 && ((
/* 166 */       this._unwrapSingle == null && provider
/* 167 */       .isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
/*     */       
/* 169 */       serializeContents(value, gen, provider);
/*     */       
/*     */       return;
/*     */     } 
/* 173 */     gen.writeStartArray(value, len);
/* 174 */     serializeContents(value, gen, provider);
/* 175 */     gen.writeEndArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeContents(String[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 182 */     int len = value.length;
/* 183 */     if (len == 0) {
/*     */       return;
/*     */     }
/* 186 */     if (this._elementSerializer != null) {
/* 187 */       serializeContentsSlow(value, gen, provider, this._elementSerializer);
/*     */       return;
/*     */     } 
/* 190 */     for (int i = 0; i < len; i++) {
/* 191 */       String str = value[i];
/* 192 */       if (str == null) {
/* 193 */         gen.writeNull();
/*     */       } else {
/* 195 */         gen.writeString(value[i]);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void serializeContentsSlow(String[] value, JsonGenerator gen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException {
/* 203 */     for (int i = 0, len = value.length; i < len; i++) {
/* 204 */       String str = value[i];
/* 205 */       if (str == null) {
/* 206 */         provider.defaultSerializeNull(gen);
/*     */       } else {
/* 208 */         ser.serialize(value[i], gen, provider);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 215 */     return createSchemaNode("array", true).set("items", (JsonNode)createSchemaNode("string"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 221 */     visitArrayFormat(visitor, typeHint, JsonFormatTypes.STRING);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/impl/StringArraySerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */