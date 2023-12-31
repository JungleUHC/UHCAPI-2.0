/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
/*     */ import com.fasterxml.jackson.databind.node.ArrayNode;
/*     */ import com.fasterxml.jackson.databind.node.ObjectNode;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import com.fasterxml.jackson.databind.util.EnumValues;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public class EnumSerializer
/*     */   extends StdScalarSerializer<Enum<?>>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final EnumValues _values;
/*     */   protected final Boolean _serializeAsIndex;
/*     */   
/*     */   public EnumSerializer(EnumValues v, Boolean serializeAsIndex) {
/*  60 */     super(v.getEnumClass(), false);
/*  61 */     this._values = v;
/*  62 */     this._serializeAsIndex = serializeAsIndex;
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
/*     */   public static EnumSerializer construct(Class<?> enumClass, SerializationConfig config, BeanDescription beanDesc, JsonFormat.Value format) {
/*  79 */     EnumValues v = EnumValues.constructFromName((MapperConfig)config, enumClass);
/*  80 */     Boolean serializeAsIndex = _isShapeWrittenUsingIndex(enumClass, format, true, (Boolean)null);
/*  81 */     return new EnumSerializer(v, serializeAsIndex);
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
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
/*  93 */     JsonFormat.Value format = findFormatOverrides(serializers, property, 
/*  94 */         handledType());
/*  95 */     if (format != null) {
/*  96 */       Class<?> type = handledType();
/*  97 */       Boolean serializeAsIndex = _isShapeWrittenUsingIndex(type, format, false, this._serializeAsIndex);
/*     */       
/*  99 */       if (!Objects.equals(serializeAsIndex, this._serializeAsIndex)) {
/* 100 */         return new EnumSerializer(this._values, serializeAsIndex);
/*     */       }
/*     */     } 
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EnumValues getEnumValues() {
/* 112 */     return this._values;
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
/*     */   public final void serialize(Enum<?> en, JsonGenerator gen, SerializerProvider serializers) throws IOException {
/* 124 */     if (_serializeAsIndex(serializers)) {
/* 125 */       gen.writeNumber(en.ordinal());
/*     */       
/*     */       return;
/*     */     } 
/* 129 */     if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
/* 130 */       gen.writeString(en.toString());
/*     */       return;
/*     */     } 
/* 133 */     gen.writeString(this._values.serializedValueFor(en));
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
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 145 */     if (_serializeAsIndex(provider)) {
/* 146 */       return (JsonNode)createSchemaNode("integer", true);
/*     */     }
/* 148 */     ObjectNode objectNode = createSchemaNode("string", true);
/* 149 */     if (typeHint != null) {
/* 150 */       JavaType type = provider.constructType(typeHint);
/* 151 */       if (type.isEnumType()) {
/* 152 */         ArrayNode enumNode = objectNode.putArray("enum");
/* 153 */         for (SerializableString value : this._values.values()) {
/* 154 */           enumNode.add(value.getValue());
/*     */         }
/*     */       } 
/*     */     } 
/* 158 */     return (JsonNode)objectNode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 165 */     SerializerProvider serializers = visitor.getProvider();
/* 166 */     if (_serializeAsIndex(serializers)) {
/* 167 */       visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
/*     */       return;
/*     */     } 
/* 170 */     JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
/* 171 */     if (stringVisitor != null) {
/* 172 */       Set<String> enums = new LinkedHashSet<>();
/*     */ 
/*     */       
/* 175 */       if (serializers != null && serializers
/* 176 */         .isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
/* 177 */         for (Enum<?> e : (Iterable<Enum<?>>)this._values.enums()) {
/* 178 */           enums.add(e.toString());
/*     */         }
/*     */       } else {
/*     */         
/* 182 */         for (SerializableString value : this._values.values()) {
/* 183 */           enums.add(value.getValue());
/*     */         }
/*     */       } 
/* 186 */       stringVisitor.enumTypes(enums);
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
/*     */   protected final boolean _serializeAsIndex(SerializerProvider serializers) {
/* 198 */     if (this._serializeAsIndex != null) {
/* 199 */       return this._serializeAsIndex.booleanValue();
/*     */     }
/* 201 */     return serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static Boolean _isShapeWrittenUsingIndex(Class<?> enumClass, JsonFormat.Value format, boolean fromClass, Boolean defaultValue) {
/* 212 */     JsonFormat.Shape shape = (format == null) ? null : format.getShape();
/* 213 */     if (shape == null) {
/* 214 */       return defaultValue;
/*     */     }
/*     */     
/* 217 */     if (shape == JsonFormat.Shape.ANY || shape == JsonFormat.Shape.SCALAR) {
/* 218 */       return defaultValue;
/*     */     }
/*     */     
/* 221 */     if (shape == JsonFormat.Shape.STRING || shape == JsonFormat.Shape.NATURAL) {
/* 222 */       return Boolean.FALSE;
/*     */     }
/*     */     
/* 225 */     if (shape.isNumeric() || shape == JsonFormat.Shape.ARRAY) {
/* 226 */       return Boolean.TRUE;
/*     */     }
/*     */     
/* 229 */     throw new IllegalArgumentException(String.format("Unsupported serialization shape (%s) for Enum %s, not supported as %s annotation", new Object[] { shape, enumClass
/*     */             
/* 231 */             .getName(), fromClass ? "class" : "property" }));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/EnumSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */