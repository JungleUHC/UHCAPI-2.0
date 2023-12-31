/*    */ package com.fasterxml.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.core.JsonToken;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
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
/*    */ 
/*    */ public class UnsupportedTypeDeserializer
/*    */   extends StdDeserializer<Object>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final JavaType _type;
/*    */   protected final String _message;
/*    */   
/*    */   public UnsupportedTypeDeserializer(JavaType t, String m) {
/* 33 */     super(t);
/* 34 */     this._type = t;
/* 35 */     this._message = m;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 42 */     if (p.currentToken() == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 43 */       Object value = p.getEmbeddedObject();
/* 44 */       if (value == null || this._type.getRawClass().isAssignableFrom(value.getClass())) {
/* 45 */         return value;
/*    */       }
/*    */     } 
/* 48 */     ctxt.reportBadDefinition(this._type, this._message);
/* 49 */     return null;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/UnsupportedTypeDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */