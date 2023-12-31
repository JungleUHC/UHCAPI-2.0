/*    */ package com.fasterxml.jackson.databind.deser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JacksonException;
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.type.LogicalType;
/*    */ import java.io.IOException;
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ 
/*    */ public class AtomicLongDeserializer
/*    */   extends StdScalarDeserializer<AtomicLong>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public AtomicLongDeserializer() {
/* 17 */     super(AtomicLong.class);
/*    */   }
/*    */   
/*    */   public AtomicLong deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 21 */     if (p.isExpectedNumberIntToken()) {
/* 22 */       return new AtomicLong(p.getLongValue());
/*    */     }
/*    */ 
/*    */     
/* 26 */     Long L = _parseLong(p, ctxt, AtomicLong.class);
/* 27 */     return (L == null) ? null : new AtomicLong(L.intValue());
/*    */   }
/*    */   
/*    */   public LogicalType logicalType() {
/* 31 */     return LogicalType.Integer;
/*    */   }
/*    */   
/*    */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 35 */     return new AtomicLong();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/AtomicLongDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */