/*     */ package com.fasterxml.jackson.databind.ser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class StringCollectionSerializer
/*     */   extends StaticListSerializerBase<Collection<String>>
/*     */ {
/*  27 */   public static final StringCollectionSerializer instance = new StringCollectionSerializer();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StringCollectionSerializer() {
/*  36 */     super(Collection.class);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected StringCollectionSerializer(StringCollectionSerializer src, Boolean unwrapSingle) {
/*  42 */     super(src, unwrapSingle);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
/*  47 */     return (JsonSerializer<?>)new StringCollectionSerializer(this, unwrapSingle);
/*     */   }
/*     */   
/*     */   protected JsonNode contentSchema() {
/*  51 */     return (JsonNode)createSchemaNode("string", true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void acceptContentVisitor(JsonArrayFormatVisitor visitor) throws JsonMappingException {
/*  57 */     visitor.itemsFormat(JsonFormatTypes.STRING);
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
/*     */   public void serialize(Collection<String> value, JsonGenerator g, SerializerProvider provider) throws IOException {
/*  70 */     int len = value.size();
/*  71 */     if (len == 1 && ((
/*  72 */       this._unwrapSingle == null && provider
/*  73 */       .isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
/*     */       
/*  75 */       serializeContents(value, g, provider);
/*     */       
/*     */       return;
/*     */     } 
/*  79 */     g.writeStartArray(value, len);
/*  80 */     serializeContents(value, g, provider);
/*  81 */     g.writeEndArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeWithType(Collection<String> value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/*  89 */     WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
/*  90 */         .typeId(value, JsonToken.START_ARRAY));
/*  91 */     g.setCurrentValue(value);
/*  92 */     serializeContents(value, g, provider);
/*  93 */     typeSer.writeTypeSuffix(g, typeIdDef);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final void serializeContents(Collection<String> value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 100 */     int i = 0;
/*     */     
/*     */     try {
/* 103 */       for (String str : value) {
/* 104 */         if (str == null) {
/* 105 */           provider.defaultSerializeNull(g);
/*     */         } else {
/* 107 */           g.writeString(str);
/*     */         } 
/* 109 */         i++;
/*     */       } 
/* 111 */     } catch (Exception e) {
/* 112 */       wrapAndThrow(provider, e, value, i);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/impl/StringCollectionSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */