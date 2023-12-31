/*    */ package com.fasterxml.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.SerializationFeature;
/*    */ import com.fasterxml.jackson.databind.SerializerProvider;
/*    */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.fasterxml.jackson.databind.ser.std.ToEmptyObjectSerializer;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ public class UnknownSerializer
/*    */   extends ToEmptyObjectSerializer
/*    */ {
/*    */   public UnknownSerializer() {
/* 16 */     super(Object.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public UnknownSerializer(Class<?> cls) {
/* 21 */     super(cls);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void serialize(Object value, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
/* 28 */     if (ctxt.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)) {
/* 29 */       failForEmpty(ctxt, value);
/*    */     }
/* 31 */     super.serialize(value, gen, ctxt);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer) throws IOException {
/* 38 */     if (ctxt.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)) {
/* 39 */       failForEmpty(ctxt, value);
/*    */     }
/* 41 */     super.serializeWithType(value, gen, ctxt, typeSer);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void failForEmpty(SerializerProvider prov, Object value) throws JsonMappingException {
/* 46 */     prov.reportBadDefinition(handledType(), String.format("No serializer found for class %s and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)", new Object[] { value
/*    */             
/* 48 */             .getClass().getName() }));
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/impl/UnknownSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */