/*    */ package com.fasterxml.jackson.databind.ser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import com.fasterxml.jackson.core.JsonToken;
/*    */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.JsonNode;
/*    */ import com.fasterxml.jackson.databind.SerializerProvider;
/*    */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*    */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @JacksonStdImpl
/*    */ public class ToEmptyObjectSerializer
/*    */   extends StdSerializer<Object>
/*    */ {
/*    */   protected ToEmptyObjectSerializer(Class<?> raw) {
/* 32 */     super(raw, false);
/*    */   }
/*    */   
/*    */   public ToEmptyObjectSerializer(JavaType type) {
/* 36 */     super(type);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void serialize(Object value, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
/* 42 */     gen.writeStartObject(value, 0);
/* 43 */     gen.writeEndObject();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer) throws IOException {
/* 50 */     WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer
/* 51 */         .typeId(value, JsonToken.START_OBJECT));
/* 52 */     typeSer.writeTypeSuffix(gen, typeIdDef);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty(SerializerProvider provider, Object value) {
/* 57 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
/* 63 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 71 */     visitor.expectObjectFormat(typeHint);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/ToEmptyObjectSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */