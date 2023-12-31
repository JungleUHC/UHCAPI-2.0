/*    */ package com.fasterxml.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JsonDeserializer;
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
/*    */ public class ErrorThrowingDeserializer
/*    */   extends JsonDeserializer<Object>
/*    */ {
/*    */   private final Error _cause;
/*    */   
/*    */   public ErrorThrowingDeserializer(NoClassDefFoundError cause) {
/* 22 */     this._cause = cause;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 27 */     throw this._cause;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/ErrorThrowingDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */