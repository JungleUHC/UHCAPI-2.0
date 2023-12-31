/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collection;
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
/*     */ public abstract class StaticListSerializerBase<T extends Collection<?>>
/*     */   extends StdSerializer<T>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   protected final Boolean _unwrapSingle;
/*     */   
/*     */   protected StaticListSerializerBase(Class<?> cls) {
/*  38 */     super(cls, false);
/*  39 */     this._unwrapSingle = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StaticListSerializerBase(StaticListSerializerBase<?> src, Boolean unwrapSingle) {
/*  47 */     super(src);
/*  48 */     this._unwrapSingle = unwrapSingle;
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
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
/*  69 */     JsonSerializer<?> ser = null;
/*  70 */     Boolean unwrapSingle = null;
/*     */     
/*  72 */     if (property != null) {
/*  73 */       AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
/*  74 */       AnnotatedMember m = property.getMember();
/*  75 */       if (m != null) {
/*  76 */         Object serDef = intr.findContentSerializer((Annotated)m);
/*  77 */         if (serDef != null) {
/*  78 */           ser = serializers.serializerInstance((Annotated)m, serDef);
/*     */         }
/*     */       } 
/*     */     } 
/*  82 */     JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
/*  83 */     if (format != null) {
/*  84 */       unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
/*     */     }
/*     */     
/*  87 */     ser = findContextualConvertingSerializer(serializers, property, ser);
/*  88 */     if (ser == null) {
/*  89 */       ser = serializers.findContentValueSerializer(String.class, property);
/*     */     }
/*     */     
/*  92 */     if (isDefaultSerializer(ser)) {
/*  93 */       if (Objects.equals(unwrapSingle, this._unwrapSingle)) {
/*  94 */         return this;
/*     */       }
/*  96 */       return _withResolved(property, unwrapSingle);
/*     */     } 
/*     */ 
/*     */     
/* 100 */     return (JsonSerializer<?>)new CollectionSerializer(serializers.constructType(String.class), true, null, ser);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty(SerializerProvider provider, T value) {
/* 106 */     return (value == null || value.size() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 111 */     return createSchemaNode("array", true).set("items", contentSchema());
/*     */   }
/*     */ 
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 116 */     JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
/* 117 */     if (v2 != null)
/* 118 */       acceptContentVisitor(v2); 
/*     */   }
/*     */   
/*     */   public abstract JsonSerializer<?> _withResolved(BeanProperty paramBeanProperty, Boolean paramBoolean);
/*     */   
/*     */   protected abstract JsonNode contentSchema();
/*     */   
/*     */   protected abstract void acceptContentVisitor(JsonArrayFormatVisitor paramJsonArrayFormatVisitor) throws JsonMappingException;
/*     */   
/*     */   public abstract void serializeWithType(T paramT, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider, TypeSerializer paramTypeSerializer) throws IOException;
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/StaticListSerializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */