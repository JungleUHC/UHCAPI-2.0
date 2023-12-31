/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.node.ObjectNode;
/*     */ import com.fasterxml.jackson.databind.ser.ContainerSerializer;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public class StdArraySerializers {
/*  25 */   protected static final HashMap<String, JsonSerializer<?>> _arraySerializers = new HashMap<>();
/*     */ 
/*     */   
/*     */   static {
/*  29 */     _arraySerializers.put(boolean[].class.getName(), new BooleanArraySerializer());
/*  30 */     _arraySerializers.put(byte[].class.getName(), new ByteArraySerializer());
/*  31 */     _arraySerializers.put(char[].class.getName(), new CharArraySerializer());
/*  32 */     _arraySerializers.put(short[].class.getName(), new ShortArraySerializer());
/*  33 */     _arraySerializers.put(int[].class.getName(), new IntArraySerializer());
/*  34 */     _arraySerializers.put(long[].class.getName(), new LongArraySerializer());
/*  35 */     _arraySerializers.put(float[].class.getName(), new FloatArraySerializer());
/*  36 */     _arraySerializers.put(double[].class.getName(), new DoubleArraySerializer());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonSerializer<?> findStandardImpl(Class<?> cls) {
/*  46 */     return _arraySerializers.get(cls.getName());
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
/*     */   protected static abstract class TypedPrimitiveArraySerializer<T>
/*     */     extends ArraySerializerBase<T>
/*     */   {
/*     */     protected TypedPrimitiveArraySerializer(Class<T> cls) {
/*  63 */       super(cls);
/*     */     }
/*     */ 
/*     */     
/*     */     protected TypedPrimitiveArraySerializer(TypedPrimitiveArraySerializer<T> src, BeanProperty prop, Boolean unwrapSingle) {
/*  68 */       super(src, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
/*  76 */       return this;
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
/*     */   public static class BooleanArraySerializer
/*     */     extends ArraySerializerBase<boolean[]>
/*     */   {
/*  92 */     private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Boolean.class);
/*     */     public BooleanArraySerializer() {
/*  94 */       super((Class)boolean[].class);
/*     */     }
/*     */     
/*     */     protected BooleanArraySerializer(BooleanArraySerializer src, BeanProperty prop, Boolean unwrapSingle) {
/*  98 */       super(src, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
/* 103 */       return (JsonSerializer<?>)new BooleanArraySerializer(this, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
/* 112 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public JavaType getContentType() {
/* 117 */       return VALUE_TYPE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> getContentSerializer() {
/* 123 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty(SerializerProvider prov, boolean[] value) {
/* 128 */       return (value.length == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasSingleElement(boolean[] value) {
/* 133 */       return (value.length == 1);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final void serialize(boolean[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 139 */       int len = value.length;
/* 140 */       if (len == 1 && _shouldUnwrapSingle(provider)) {
/* 141 */         serializeContents(value, g, provider);
/*     */         return;
/*     */       } 
/* 144 */       g.writeStartArray(value, len);
/* 145 */       serializeContents(value, g, provider);
/* 146 */       g.writeEndArray();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serializeContents(boolean[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 153 */       for (int i = 0, len = value.length; i < len; i++) {
/* 154 */         g.writeBoolean(value[i]);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 161 */       ObjectNode o = createSchemaNode("array", true);
/* 162 */       o.set("items", (JsonNode)createSchemaNode("boolean"));
/* 163 */       return (JsonNode)o;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 170 */       visitArrayFormat(visitor, typeHint, JsonFormatTypes.BOOLEAN);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class ShortArraySerializer
/*     */     extends TypedPrimitiveArraySerializer<short[]>
/*     */   {
/* 179 */     private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(short.class);
/*     */     public ShortArraySerializer() {
/* 181 */       super((Class)short[].class);
/*     */     }
/*     */     public ShortArraySerializer(ShortArraySerializer src, BeanProperty prop, Boolean unwrapSingle) {
/* 184 */       super(src, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
/* 189 */       return (JsonSerializer<?>)new ShortArraySerializer(this, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     public JavaType getContentType() {
/* 194 */       return VALUE_TYPE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> getContentSerializer() {
/* 200 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty(SerializerProvider prov, short[] value) {
/* 205 */       return (value.length == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasSingleElement(short[] value) {
/* 210 */       return (value.length == 1);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final void serialize(short[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 216 */       int len = value.length;
/* 217 */       if (len == 1 && _shouldUnwrapSingle(provider)) {
/* 218 */         serializeContents(value, g, provider);
/*     */         return;
/*     */       } 
/* 221 */       g.writeStartArray(value, len);
/* 222 */       serializeContents(value, g, provider);
/* 223 */       g.writeEndArray();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serializeContents(short[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 230 */       for (int i = 0, len = value.length; i < len; i++) {
/* 231 */         g.writeNumber(value[i]);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 239 */       ObjectNode o = createSchemaNode("array", true);
/* 240 */       return o.set("items", (JsonNode)createSchemaNode("integer"));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 247 */       visitArrayFormat(visitor, typeHint, JsonFormatTypes.INTEGER);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class CharArraySerializer
/*     */     extends StdSerializer<char[]>
/*     */   {
/*     */     public CharArraySerializer() {
/* 261 */       super((Class)char[].class);
/*     */     }
/*     */     
/*     */     public boolean isEmpty(SerializerProvider prov, char[] value) {
/* 265 */       return (value.length == 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serialize(char[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 273 */       if (provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
/* 274 */         g.writeStartArray(value, value.length);
/* 275 */         _writeArrayContents(g, value);
/* 276 */         g.writeEndArray();
/*     */       } else {
/* 278 */         g.writeString(value, 0, value.length);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serializeWithType(char[] value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/*     */       WritableTypeId typeIdDef;
/* 288 */       boolean asArray = provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS);
/*     */       
/* 290 */       if (asArray) {
/* 291 */         typeIdDef = typeSer.writeTypePrefix(g, typeSer
/* 292 */             .typeId(value, JsonToken.START_ARRAY));
/* 293 */         _writeArrayContents(g, value);
/*     */       } else {
/* 295 */         typeIdDef = typeSer.writeTypePrefix(g, typeSer
/* 296 */             .typeId(value, JsonToken.VALUE_STRING));
/* 297 */         g.writeString(value, 0, value.length);
/*     */       } 
/* 299 */       typeSer.writeTypeSuffix(g, typeIdDef);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private final void _writeArrayContents(JsonGenerator g, char[] value) throws IOException {
/* 305 */       for (int i = 0, len = value.length; i < len; i++) {
/* 306 */         g.writeString(value, i, 1);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 313 */       ObjectNode o = createSchemaNode("array", true);
/* 314 */       ObjectNode itemSchema = createSchemaNode("string");
/* 315 */       itemSchema.put("type", "string");
/* 316 */       return o.set("items", (JsonNode)itemSchema);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 323 */       visitArrayFormat(visitor, typeHint, JsonFormatTypes.STRING);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class IntArraySerializer
/*     */     extends ArraySerializerBase<int[]>
/*     */   {
/* 332 */     private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(int.class);
/*     */     public IntArraySerializer() {
/* 334 */       super((Class)int[].class);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected IntArraySerializer(IntArraySerializer src, BeanProperty prop, Boolean unwrapSingle) {
/* 341 */       super(src, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
/* 346 */       return (JsonSerializer<?>)new IntArraySerializer(this, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
/* 355 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public JavaType getContentType() {
/* 360 */       return VALUE_TYPE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> getContentSerializer() {
/* 366 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty(SerializerProvider prov, int[] value) {
/* 371 */       return (value.length == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasSingleElement(int[] value) {
/* 376 */       return (value.length == 1);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final void serialize(int[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 382 */       int len = value.length;
/* 383 */       if (len == 1 && _shouldUnwrapSingle(provider)) {
/* 384 */         serializeContents(value, g, provider);
/*     */         
/*     */         return;
/*     */       } 
/* 388 */       g.writeArray(value, 0, value.length);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serializeContents(int[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 395 */       for (int i = 0, len = value.length; i < len; i++) {
/* 396 */         g.writeNumber(value[i]);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 402 */       return createSchemaNode("array", true).set("items", (JsonNode)createSchemaNode("integer"));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 408 */       visitArrayFormat(visitor, typeHint, JsonFormatTypes.INTEGER);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class LongArraySerializer
/*     */     extends TypedPrimitiveArraySerializer<long[]>
/*     */   {
/* 417 */     private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(long.class);
/*     */     public LongArraySerializer() {
/* 419 */       super((Class)long[].class);
/*     */     }
/*     */     public LongArraySerializer(LongArraySerializer src, BeanProperty prop, Boolean unwrapSingle) {
/* 422 */       super(src, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
/* 427 */       return (JsonSerializer<?>)new LongArraySerializer(this, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     public JavaType getContentType() {
/* 432 */       return VALUE_TYPE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> getContentSerializer() {
/* 438 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty(SerializerProvider prov, long[] value) {
/* 443 */       return (value.length == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasSingleElement(long[] value) {
/* 448 */       return (value.length == 1);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final void serialize(long[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 454 */       int len = value.length;
/* 455 */       if (len == 1 && _shouldUnwrapSingle(provider)) {
/* 456 */         serializeContents(value, g, provider);
/*     */         
/*     */         return;
/*     */       } 
/* 460 */       g.writeArray(value, 0, value.length);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serializeContents(long[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 467 */       for (int i = 0, len = value.length; i < len; i++) {
/* 468 */         g.writeNumber(value[i]);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 475 */       return createSchemaNode("array", true)
/* 476 */         .set("items", (JsonNode)createSchemaNode("number", true));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 483 */       visitArrayFormat(visitor, typeHint, JsonFormatTypes.NUMBER);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class FloatArraySerializer
/*     */     extends TypedPrimitiveArraySerializer<float[]>
/*     */   {
/* 492 */     private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(float.class);
/*     */     
/*     */     public FloatArraySerializer() {
/* 495 */       super((Class)float[].class);
/*     */     }
/*     */     
/*     */     public FloatArraySerializer(FloatArraySerializer src, BeanProperty prop, Boolean unwrapSingle) {
/* 499 */       super(src, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
/* 504 */       return (JsonSerializer<?>)new FloatArraySerializer(this, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     public JavaType getContentType() {
/* 509 */       return VALUE_TYPE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> getContentSerializer() {
/* 515 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty(SerializerProvider prov, float[] value) {
/* 520 */       return (value.length == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasSingleElement(float[] value) {
/* 525 */       return (value.length == 1);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final void serialize(float[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 531 */       int len = value.length;
/* 532 */       if (len == 1 && _shouldUnwrapSingle(provider)) {
/* 533 */         serializeContents(value, g, provider);
/*     */         return;
/*     */       } 
/* 536 */       g.writeStartArray(value, len);
/* 537 */       serializeContents(value, g, provider);
/* 538 */       g.writeEndArray();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void serializeContents(float[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 545 */       for (int i = 0, len = value.length; i < len; i++) {
/* 546 */         g.writeNumber(value[i]);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 552 */       return createSchemaNode("array", true).set("items", (JsonNode)createSchemaNode("number"));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 558 */       visitArrayFormat(visitor, typeHint, JsonFormatTypes.NUMBER);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   public static class DoubleArraySerializer
/*     */     extends ArraySerializerBase<double[]>
/*     */   {
/* 567 */     private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(double.class);
/*     */     public DoubleArraySerializer() {
/* 569 */       super((Class)double[].class);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected DoubleArraySerializer(DoubleArraySerializer src, BeanProperty prop, Boolean unwrapSingle) {
/* 576 */       super(src, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
/* 581 */       return (JsonSerializer<?>)new DoubleArraySerializer(this, prop, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
/* 590 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public JavaType getContentType() {
/* 595 */       return VALUE_TYPE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<?> getContentSerializer() {
/* 601 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty(SerializerProvider prov, double[] value) {
/* 606 */       return (value.length == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasSingleElement(double[] value) {
/* 611 */       return (value.length == 1);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final void serialize(double[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 617 */       int len = value.length;
/* 618 */       if (len == 1 && _shouldUnwrapSingle(provider)) {
/* 619 */         serializeContents(value, g, provider);
/*     */         
/*     */         return;
/*     */       } 
/* 623 */       g.writeArray(value, 0, value.length);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void serializeContents(double[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 629 */       for (int i = 0, len = value.length; i < len; i++) {
/* 630 */         g.writeNumber(value[i]);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 636 */       return createSchemaNode("array", true).set("items", (JsonNode)createSchemaNode("number"));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 643 */       visitArrayFormat(visitor, typeHint, JsonFormatTypes.NUMBER);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/StdArraySerializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */