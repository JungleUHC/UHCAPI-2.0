/*    */ package com.fasterxml.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.SerializerProvider;
/*    */ import com.fasterxml.jackson.databind.ser.std.StdSerializer;
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
/*    */ public class UnsupportedTypeSerializer
/*    */   extends StdSerializer<Object>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final JavaType _type;
/*    */   protected final String _message;
/*    */   
/*    */   public UnsupportedTypeSerializer(JavaType t, String msg) {
/* 28 */     super(Object.class);
/* 29 */     this._type = t;
/* 30 */     this._message = msg;
/*    */   }
/*    */ 
/*    */   
/*    */   public void serialize(Object value, JsonGenerator g, SerializerProvider ctxt) throws IOException {
/* 35 */     ctxt.reportBadDefinition(this._type, this._message);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/impl/UnsupportedTypeSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */