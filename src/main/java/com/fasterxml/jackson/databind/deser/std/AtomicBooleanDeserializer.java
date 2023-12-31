/*    */ package com.fasterxml.jackson.databind.deser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JacksonException;
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.core.JsonToken;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.type.LogicalType;
/*    */ import java.io.IOException;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ 
/*    */ public class AtomicBooleanDeserializer extends StdScalarDeserializer<AtomicBoolean> {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public AtomicBooleanDeserializer() {
/* 16 */     super(AtomicBoolean.class);
/*    */   }
/*    */   
/*    */   public AtomicBoolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 20 */     JsonToken t = p.currentToken();
/* 21 */     if (t == JsonToken.VALUE_TRUE) {
/* 22 */       return new AtomicBoolean(true);
/*    */     }
/* 24 */     if (t == JsonToken.VALUE_FALSE) {
/* 25 */       return new AtomicBoolean(false);
/*    */     }
/*    */ 
/*    */     
/* 29 */     Boolean b = _parseBoolean(p, ctxt, AtomicBoolean.class);
/* 30 */     return (b == null) ? null : new AtomicBoolean(b.booleanValue());
/*    */   }
/*    */   
/*    */   public LogicalType logicalType() {
/* 34 */     return LogicalType.Boolean;
/*    */   }
/*    */   
/*    */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 38 */     return new AtomicBoolean(false);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/AtomicBooleanDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */