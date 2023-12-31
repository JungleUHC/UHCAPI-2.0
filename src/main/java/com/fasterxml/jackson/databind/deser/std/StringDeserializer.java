/*    */ package com.fasterxml.jackson.databind.deser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JacksonException;
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.core.JsonToken;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*    */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.fasterxml.jackson.databind.type.LogicalType;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ @JacksonStdImpl
/*    */ public class StringDeserializer
/*    */   extends StdScalarDeserializer<String>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   public static final StringDeserializer instance = new StringDeserializer();
/*    */   public StringDeserializer() {
/* 21 */     super(String.class);
/*    */   }
/*    */   
/*    */   public LogicalType logicalType() {
/* 25 */     return LogicalType.Textual;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCachable() {
/* 30 */     return true;
/*    */   }
/*    */   
/*    */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 34 */     return "";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 40 */     if (p.hasToken(JsonToken.VALUE_STRING)) {
/* 41 */       return p.getText();
/*    */     }
/* 43 */     JsonToken t = p.currentToken();
/*    */     
/* 45 */     if (t == JsonToken.START_ARRAY) {
/* 46 */       return _deserializeFromArray(p, ctxt);
/*    */     }
/*    */     
/* 49 */     if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 50 */       Object ob = p.getEmbeddedObject();
/* 51 */       if (ob == null) {
/* 52 */         return null;
/*    */       }
/* 54 */       if (ob instanceof byte[]) {
/* 55 */         return ctxt.getBase64Variant().encode((byte[])ob, false);
/*    */       }
/*    */       
/* 58 */       return ob.toString();
/*    */     } 
/*    */     
/* 61 */     if (t == JsonToken.START_OBJECT) {
/* 62 */       return ctxt.extractScalarFromObject(p, this, this._valueClass);
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 67 */     if (t.isScalarValue()) {
/* 68 */       String text = p.getValueAsString();
/* 69 */       if (text != null) {
/* 70 */         return text;
/*    */       }
/*    */     } 
/* 73 */     return (String)ctxt.handleUnexpectedToken(this._valueClass, p);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 81 */     return deserialize(p, ctxt);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StringDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */