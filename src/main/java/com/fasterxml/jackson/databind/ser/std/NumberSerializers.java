/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.io.NumberOutput;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
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
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ public class NumberSerializers
/*     */ {
/*     */   public static void addAll(Map<String, JsonSerializer<?>> allDeserializers) {
/*  28 */     allDeserializers.put(Integer.class.getName(), new IntegerSerializer(Integer.class));
/*  29 */     allDeserializers.put(int.class.getName(), new IntegerSerializer(int.class));
/*  30 */     allDeserializers.put(Long.class.getName(), new LongSerializer(Long.class));
/*  31 */     allDeserializers.put(long.class.getName(), new LongSerializer(long.class));
/*     */     
/*  33 */     allDeserializers.put(Byte.class.getName(), IntLikeSerializer.instance);
/*  34 */     allDeserializers.put(byte.class.getName(), IntLikeSerializer.instance);
/*  35 */     allDeserializers.put(Short.class.getName(), ShortSerializer.instance);
/*  36 */     allDeserializers.put(short.class.getName(), ShortSerializer.instance);
/*     */ 
/*     */     
/*  39 */     allDeserializers.put(Double.class.getName(), new DoubleSerializer(Double.class));
/*  40 */     allDeserializers.put(double.class.getName(), new DoubleSerializer(double.class));
/*  41 */     allDeserializers.put(Float.class.getName(), FloatSerializer.instance);
/*  42 */     allDeserializers.put(float.class.getName(), FloatSerializer.instance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class Base<T>
/*     */     extends StdScalarSerializer<T>
/*     */     implements ContextualSerializer
/*     */   {
/*     */     protected final JsonParser.NumberType _numberType;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected final String _schemaType;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected final boolean _isInt;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Base(Class<?> cls, JsonParser.NumberType numberType, String schemaType) {
/*  70 */       super(cls, false);
/*  71 */       this._numberType = numberType;
/*  72 */       this._schemaType = schemaType;
/*  73 */       this._isInt = (numberType == JsonParser.NumberType.INT || numberType == JsonParser.NumberType.LONG || numberType == JsonParser.NumberType.BIG_INTEGER);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/*  80 */       return (JsonNode)createSchemaNode(this._schemaType, true);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/*  87 */       if (this._isInt) {
/*  88 */         visitIntFormat(visitor, typeHint, this._numberType);
/*     */       } else {
/*  90 */         visitFloatFormat(visitor, typeHint, this._numberType);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
/*  98 */       JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
/*  99 */       if (format != null) {
/* 100 */         switch (format.getShape()) {
/*     */           case STRING:
/* 102 */             if (handledType() == BigDecimal.class) {
/* 103 */               return NumberSerializer.bigDecimalAsStringSerializer();
/*     */             }
/* 105 */             return ToStringSerializer.instance;
/*     */         } 
/*     */       
/*     */       }
/* 109 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class ShortSerializer
/*     */     extends Base<Object>
/*     */   {
/* 121 */     static final ShortSerializer instance = new ShortSerializer();
/*     */     
/*     */     public ShortSerializer() {
/* 124 */       super(Short.class, JsonParser.NumberType.INT, "number");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 130 */       gen.writeNumber(((Short)value).shortValue());
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
/*     */   @JacksonStdImpl
/*     */   public static class IntegerSerializer
/*     */     extends Base<Object>
/*     */   {
/*     */     public IntegerSerializer(Class<?> type) {
/* 147 */       super(type, JsonParser.NumberType.INT, "integer");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 153 */       gen.writeNumber(((Integer)value).intValue());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 162 */       serialize(value, gen, provider);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class IntLikeSerializer
/*     */     extends Base<Object>
/*     */   {
/* 173 */     static final IntLikeSerializer instance = new IntLikeSerializer();
/*     */     
/*     */     public IntLikeSerializer() {
/* 176 */       super(Number.class, JsonParser.NumberType.INT, "integer");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 182 */       gen.writeNumber(((Number)value).intValue());
/*     */     }
/*     */   }
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class LongSerializer extends Base<Object> {
/*     */     public LongSerializer(Class<?> cls) {
/* 189 */       super(cls, JsonParser.NumberType.LONG, "number");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 195 */       gen.writeNumber(((Long)value).longValue());
/*     */     }
/*     */   }
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class FloatSerializer extends Base<Object> {
/* 201 */     static final FloatSerializer instance = new FloatSerializer();
/*     */     
/*     */     public FloatSerializer() {
/* 204 */       super(Float.class, JsonParser.NumberType.FLOAT, "number");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 210 */       gen.writeNumber(((Float)value).floatValue());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class DoubleSerializer
/*     */     extends Base<Object>
/*     */   {
/*     */     public DoubleSerializer(Class<?> cls) {
/* 224 */       super(cls, JsonParser.NumberType.DOUBLE, "number");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 231 */       gen.writeNumber(((Double)value).doubleValue());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serializeWithType(Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 241 */       Double d = (Double)value;
/* 242 */       if (NumberOutput.notFinite(d.doubleValue())) {
/* 243 */         WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
/*     */             
/* 245 */             .typeId(value, JsonToken.VALUE_NUMBER_FLOAT));
/* 246 */         g.writeNumber(d.doubleValue());
/* 247 */         typeSer.writeTypeSuffix(g, typeIdDef);
/*     */       } else {
/* 249 */         g.writeNumber(d.doubleValue());
/*     */       } 
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public static boolean notFinite(double value) {
/* 255 */       return NumberOutput.notFinite(value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/NumberSerializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */