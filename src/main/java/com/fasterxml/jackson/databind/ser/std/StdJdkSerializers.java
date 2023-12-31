/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.Collection;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class StdJdkSerializers {
/*     */   public static Collection<Map.Entry<Class<?>, Object>> all() {
/*  27 */     HashMap<Class<?>, Object> sers = new HashMap<>();
/*     */ 
/*     */     
/*  30 */     sers.put(URL.class, new ToStringSerializer(URL.class));
/*  31 */     sers.put(URI.class, new ToStringSerializer(URI.class));
/*     */     
/*  33 */     sers.put(Currency.class, new ToStringSerializer(Currency.class));
/*  34 */     sers.put(UUID.class, new UUIDSerializer());
/*  35 */     sers.put(Pattern.class, new ToStringSerializer(Pattern.class));
/*  36 */     sers.put(Locale.class, new ToStringSerializer(Locale.class));
/*     */ 
/*     */     
/*  39 */     sers.put(AtomicBoolean.class, AtomicBooleanSerializer.class);
/*  40 */     sers.put(AtomicInteger.class, AtomicIntegerSerializer.class);
/*  41 */     sers.put(AtomicLong.class, AtomicLongSerializer.class);
/*     */ 
/*     */     
/*  44 */     sers.put(File.class, FileSerializer.class);
/*  45 */     sers.put(Class.class, ClassSerializer.class);
/*     */ 
/*     */     
/*  48 */     sers.put(Void.class, NullSerializer.instance);
/*  49 */     sers.put(void.class, NullSerializer.instance);
/*     */     
/*  51 */     return sers.entrySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class AtomicBooleanSerializer
/*     */     extends StdScalarSerializer<AtomicBoolean>
/*     */   {
/*     */     public AtomicBooleanSerializer() {
/*  63 */       super(AtomicBoolean.class, false);
/*     */     }
/*     */     
/*     */     public void serialize(AtomicBoolean value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*  67 */       gen.writeBoolean(value.get());
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/*  72 */       return (JsonNode)createSchemaNode("boolean", true);
/*     */     }
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/*  77 */       visitor.expectBooleanFormat(typeHint);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class AtomicIntegerSerializer
/*     */     extends StdScalarSerializer<AtomicInteger> {
/*     */     public AtomicIntegerSerializer() {
/*  84 */       super(AtomicInteger.class, false);
/*     */     }
/*     */     
/*     */     public void serialize(AtomicInteger value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*  88 */       gen.writeNumber(value.get());
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/*  93 */       return (JsonNode)createSchemaNode("integer", true);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/*  99 */       visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class AtomicLongSerializer
/*     */     extends StdScalarSerializer<AtomicLong> {
/*     */     public AtomicLongSerializer() {
/* 106 */       super(AtomicLong.class, false);
/*     */     }
/*     */     
/*     */     public void serialize(AtomicLong value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 110 */       gen.writeNumber(value.get());
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 115 */       return (JsonNode)createSchemaNode("integer", true);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 122 */       visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/StdJdkSerializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */