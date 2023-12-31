/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.Converter;
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
/*     */ public class StdDelegatingSerializer
/*     */   extends StdSerializer<Object>
/*     */   implements ContextualSerializer, ResolvableSerializer, JsonFormatVisitable, SchemaAware
/*     */ {
/*     */   protected final Converter<Object, ?> _converter;
/*     */   protected final JavaType _delegateType;
/*     */   protected final JsonSerializer<Object> _delegateSerializer;
/*     */   
/*     */   public StdDelegatingSerializer(Converter<?, ?> converter) {
/*  55 */     super(Object.class);
/*  56 */     this._converter = (Converter)converter;
/*  57 */     this._delegateType = null;
/*  58 */     this._delegateSerializer = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> StdDelegatingSerializer(Class<T> cls, Converter<T, ?> converter) {
/*  64 */     super(cls, false);
/*  65 */     this._converter = (Converter)converter;
/*  66 */     this._delegateType = null;
/*  67 */     this._delegateSerializer = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdDelegatingSerializer(Converter<Object, ?> converter, JavaType delegateType, JsonSerializer<?> delegateSerializer) {
/*  74 */     super(delegateType);
/*  75 */     this._converter = converter;
/*  76 */     this._delegateType = delegateType;
/*  77 */     this._delegateSerializer = (JsonSerializer)delegateSerializer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StdDelegatingSerializer withDelegate(Converter<Object, ?> converter, JavaType delegateType, JsonSerializer<?> delegateSerializer) {
/*  87 */     ClassUtil.verifyMustOverride(StdDelegatingSerializer.class, this, "withDelegate");
/*  88 */     return new StdDelegatingSerializer(converter, delegateType, delegateSerializer);
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
/*     */   public void resolve(SerializerProvider provider) throws JsonMappingException {
/* 100 */     if (this._delegateSerializer != null && this._delegateSerializer instanceof ResolvableSerializer)
/*     */     {
/* 102 */       ((ResolvableSerializer)this._delegateSerializer).resolve(provider);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/* 110 */     JsonSerializer<?> delSer = this._delegateSerializer;
/* 111 */     JavaType delegateType = this._delegateType;
/*     */     
/* 113 */     if (delSer == null) {
/*     */       
/* 115 */       if (delegateType == null) {
/* 116 */         delegateType = this._converter.getOutputType(provider.getTypeFactory());
/*     */       }
/*     */ 
/*     */       
/* 120 */       if (!delegateType.isJavaLangObject()) {
/* 121 */         delSer = provider.findValueSerializer(delegateType);
/*     */       }
/*     */     } 
/* 124 */     if (delSer instanceof ContextualSerializer) {
/* 125 */       delSer = provider.handleSecondaryContextualization(delSer, property);
/*     */     }
/* 127 */     if (delSer == this._delegateSerializer && delegateType == this._delegateType) {
/* 128 */       return this;
/*     */     }
/* 130 */     return withDelegate(this._converter, delegateType, delSer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Converter<Object, ?> getConverter() {
/* 140 */     return this._converter;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> getDelegatee() {
/* 145 */     return this._delegateSerializer;
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
/*     */   public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 157 */     Object delegateValue = convertValue(value);
/*     */     
/* 159 */     if (delegateValue == null) {
/* 160 */       provider.defaultSerializeNull(gen);
/*     */       
/*     */       return;
/*     */     } 
/* 164 */     JsonSerializer<Object> ser = this._delegateSerializer;
/* 165 */     if (ser == null) {
/* 166 */       ser = _findSerializer(delegateValue, provider);
/*     */     }
/* 168 */     ser.serialize(delegateValue, gen, provider);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 177 */     Object delegateValue = convertValue(value);
/* 178 */     JsonSerializer<Object> ser = this._delegateSerializer;
/* 179 */     if (ser == null) {
/* 180 */       ser = _findSerializer(value, provider);
/*     */     }
/* 182 */     ser.serializeWithType(delegateValue, gen, provider, typeSer);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty(SerializerProvider prov, Object value) {
/* 188 */     Object delegateValue = convertValue(value);
/* 189 */     if (delegateValue == null) {
/* 190 */       return true;
/*     */     }
/* 192 */     if (this._delegateSerializer == null) {
/* 193 */       return (value == null);
/*     */     }
/* 195 */     return this._delegateSerializer.isEmpty(prov, delegateValue);
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
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
/* 208 */     if (this._delegateSerializer instanceof SchemaAware) {
/* 209 */       return ((SchemaAware)this._delegateSerializer).getSchema(provider, typeHint);
/*     */     }
/* 211 */     return super.getSchema(provider, typeHint);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint, boolean isOptional) throws JsonMappingException {
/* 218 */     if (this._delegateSerializer instanceof SchemaAware) {
/* 219 */       return ((SchemaAware)this._delegateSerializer).getSchema(provider, typeHint, isOptional);
/*     */     }
/* 221 */     return super.getSchema(provider, typeHint);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 232 */     if (this._delegateSerializer != null) {
/* 233 */       this._delegateSerializer.acceptJsonFormatVisitor(visitor, typeHint);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object convertValue(Object value) {
/* 255 */     return this._converter.convert(value);
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
/*     */   protected JsonSerializer<Object> _findSerializer(Object value, SerializerProvider serializers) throws JsonMappingException {
/* 270 */     return serializers.findValueSerializer(value.getClass());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/StdDelegatingSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */