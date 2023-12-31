/*    */ package com.fasterxml.jackson.databind.deser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.JsonNode;
/*    */ import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
/*    */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class StdNodeBasedDeserializer<T>
/*    */   extends StdDeserializer<T>
/*    */   implements ResolvableDeserializer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected JsonDeserializer<Object> _treeDeserializer;
/*    */   
/*    */   protected StdNodeBasedDeserializer(JavaType targetType) {
/* 35 */     super(targetType);
/*    */   }
/*    */   
/*    */   protected StdNodeBasedDeserializer(Class<T> targetType) {
/* 39 */     super(targetType);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected StdNodeBasedDeserializer(StdNodeBasedDeserializer<?> src) {
/* 47 */     super(src);
/* 48 */     this._treeDeserializer = src._treeDeserializer;
/*    */   }
/*    */ 
/*    */   
/*    */   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
/* 53 */     this._treeDeserializer = ctxt.findRootValueDeserializer(ctxt.constructType(JsonNode.class));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract T convert(JsonNode paramJsonNode, DeserializationContext paramDeserializationContext) throws IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 72 */     JsonNode n = (JsonNode)this._treeDeserializer.deserialize(jp, ctxt);
/* 73 */     return convert(n, ctxt);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer td) throws IOException {
/* 83 */     JsonNode n = (JsonNode)this._treeDeserializer.deserializeWithType(jp, ctxt, td);
/* 84 */     return convert(n, ctxt);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StdNodeBasedDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */