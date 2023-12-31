/*    */ package com.fasterxml.jackson.databind.deser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JacksonException;
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.type.LogicalType;
/*    */ import java.io.IOException;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ public class AtomicIntegerDeserializer
/*    */   extends StdScalarDeserializer<AtomicInteger>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public AtomicIntegerDeserializer() {
/* 17 */     super(AtomicInteger.class);
/*    */   }
/*    */   
/*    */   public AtomicInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 21 */     if (p.isExpectedNumberIntToken()) {
/* 22 */       return new AtomicInteger(p.getIntValue());
/*    */     }
/*    */ 
/*    */     
/* 26 */     Integer I = _parseInteger(p, ctxt, AtomicInteger.class);
/* 27 */     return (I == null) ? null : new AtomicInteger(I.intValue());
/*    */   }
/*    */   
/*    */   public LogicalType logicalType() {
/* 31 */     return LogicalType.Integer;
/*    */   }
/*    */   
/*    */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 35 */     return new AtomicInteger();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/AtomicIntegerDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */