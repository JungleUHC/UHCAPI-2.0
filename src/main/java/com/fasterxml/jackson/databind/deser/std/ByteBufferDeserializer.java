/*    */ package com.fasterxml.jackson.databind.deser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JacksonException;
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.databind.DeserializationContext;
/*    */ import com.fasterxml.jackson.databind.type.LogicalType;
/*    */ import com.fasterxml.jackson.databind.util.ByteBufferBackedOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ public class ByteBufferDeserializer
/*    */   extends StdScalarDeserializer<ByteBuffer> {
/*    */   protected ByteBufferDeserializer() {
/* 15 */     super(ByteBuffer.class);
/*    */   }
/*    */   private static final long serialVersionUID = 1L;
/*    */   public LogicalType logicalType() {
/* 19 */     return LogicalType.Binary;
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBuffer deserialize(JsonParser parser, DeserializationContext cx) throws IOException {
/* 24 */     byte[] b = parser.getBinaryValue();
/* 25 */     return ByteBuffer.wrap(b);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ByteBuffer deserialize(JsonParser jp, DeserializationContext ctxt, ByteBuffer intoValue) throws IOException {
/* 31 */     ByteBufferBackedOutputStream byteBufferBackedOutputStream = new ByteBufferBackedOutputStream(intoValue);
/* 32 */     jp.readBinaryValue(ctxt.getBase64Variant(), (OutputStream)byteBufferBackedOutputStream);
/* 33 */     byteBufferBackedOutputStream.close();
/* 34 */     return intoValue;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/ByteBufferDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */