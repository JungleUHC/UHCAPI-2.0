/*    */ package com.fasterxml.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*    */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NullsAsEmptyProvider
/*    */   implements NullValueProvider, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final JsonDeserializer<?> _deserializer;
/*    */   
/*    */   public NullsAsEmptyProvider(JsonDeserializer<?> deser) {
/* 20 */     this._deserializer = deser;
/*    */   }
/*    */ 
/*    */   
/*    */   public AccessPattern getNullAccessPattern() {
/* 25 */     return AccessPattern.DYNAMIC;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getNullValue(DeserializationContext ctxt) throws JsonMappingException {
/* 31 */     return this._deserializer.getEmptyValue(ctxt);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/NullsAsEmptyProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */