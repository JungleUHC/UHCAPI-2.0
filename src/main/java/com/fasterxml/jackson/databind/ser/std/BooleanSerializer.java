/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
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
/*     */ @JacksonStdImpl
/*     */ public final class BooleanSerializer
/*     */   extends StdScalarSerializer<Object>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final boolean _forPrimitive;
/*     */   
/*     */   public BooleanSerializer(boolean forPrimitive) {
/*  42 */     super(forPrimitive ? boolean.class : Boolean.class, false);
/*  43 */     this._forPrimitive = forPrimitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
/*  52 */     JsonFormat.Value format = findFormatOverrides(serializers, property, 
/*  53 */         handledType());
/*  54 */     if (format != null) {
/*  55 */       JsonFormat.Shape shape = format.getShape();
/*  56 */       if (shape.isNumeric()) {
/*  57 */         return new AsNumber(this._forPrimitive);
/*     */       }
/*  59 */       if (shape == JsonFormat.Shape.STRING) {
/*  60 */         return new ToStringSerializer(this._handledType);
/*     */       }
/*     */     } 
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
/*  68 */     g.writeBoolean(Boolean.TRUE.equals(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void serializeWithType(Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/*  75 */     g.writeBoolean(Boolean.TRUE.equals(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/*  80 */     return (JsonNode)createSchemaNode("boolean", !this._forPrimitive);
/*     */   }
/*     */ 
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/*  85 */     visitor.expectBooleanFormat(typeHint);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class AsNumber
/*     */     extends StdScalarSerializer<Object>
/*     */     implements ContextualSerializer
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected final boolean _forPrimitive;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public AsNumber(boolean forPrimitive) {
/* 107 */       super(forPrimitive ? boolean.class : Boolean.class, false);
/* 108 */       this._forPrimitive = forPrimitive;
/*     */     }
/*     */ 
/*     */     
/*     */     public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 113 */       g.writeNumber(Boolean.FALSE.equals(value) ? 0 : 1);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final void serializeWithType(Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 124 */       g.writeBoolean(Boolean.TRUE.equals(value));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 131 */       visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
/* 138 */       JsonFormat.Value format = findFormatOverrides(serializers, property, Boolean.class);
/*     */       
/* 140 */       if (format != null) {
/* 141 */         JsonFormat.Shape shape = format.getShape();
/* 142 */         if (!shape.isNumeric()) {
/* 143 */           return new BooleanSerializer(this._forPrimitive);
/*     */         }
/*     */       } 
/* 146 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/BooleanSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */