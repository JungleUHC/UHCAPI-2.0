/*    */ package com.fasterxml.jackson.databind.deser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.fasterxml.jackson.databind.type.LogicalType;
/*    */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ public abstract class StdScalarDeserializer<T>
/*    */   extends StdDeserializer<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected StdScalarDeserializer(Class<?> vc) {
/* 19 */     super(vc); } protected StdScalarDeserializer(JavaType valueType) {
/* 20 */     super(valueType);
/*    */   }
/*    */   protected StdScalarDeserializer(StdScalarDeserializer<?> src) {
/* 23 */     super(src);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LogicalType logicalType() {
/* 33 */     return LogicalType.OtherScalar;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 42 */     return Boolean.FALSE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public AccessPattern getNullAccessPattern() {
/* 48 */     return AccessPattern.ALWAYS_NULL;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AccessPattern getEmptyAccessPattern() {
/* 55 */     return AccessPattern.CONSTANT;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 66 */     return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T deserialize(JsonParser p, DeserializationContext ctxt, T intoValue) throws IOException {
/* 76 */     ctxt.handleBadMerge(this);
/*    */     
/* 78 */     return (T)deserialize(p, ctxt);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StdScalarDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */