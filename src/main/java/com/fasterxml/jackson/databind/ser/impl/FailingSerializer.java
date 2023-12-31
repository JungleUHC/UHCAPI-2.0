/*    */ package com.fasterxml.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
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
/*    */ public class FailingSerializer
/*    */   extends StdSerializer<Object>
/*    */ {
/*    */   protected final String _msg;
/*    */   
/*    */   public FailingSerializer(String msg) {
/* 25 */     super(Object.class);
/* 26 */     this._msg = msg;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void serialize(Object value, JsonGenerator g, SerializerProvider ctxt) throws IOException {
/* 32 */     ctxt.reportMappingProblem(this._msg, new Object[0]);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/impl/FailingSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */