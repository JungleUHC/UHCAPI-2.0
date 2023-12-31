/*    */ package com.fasterxml.jackson.databind.ser.std;
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.SerializerProvider;
/*    */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
/*    */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
/*    */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ public class ByteBufferSerializer extends StdScalarSerializer<ByteBuffer> {
/*    */   public ByteBufferSerializer() {
/* 16 */     super(ByteBuffer.class);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void serialize(ByteBuffer bbuf, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 22 */     if (bbuf.hasArray()) {
/* 23 */       int pos = bbuf.position();
/* 24 */       gen.writeBinary(bbuf.array(), bbuf.arrayOffset() + pos, bbuf.limit() - pos);
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 29 */     ByteBuffer copy = bbuf.asReadOnlyBuffer();
/* 30 */     if (copy.position() > 0) {
/* 31 */       copy.rewind();
/*    */     }
/* 33 */     ByteBufferBackedInputStream byteBufferBackedInputStream = new ByteBufferBackedInputStream(copy);
/* 34 */     gen.writeBinary((InputStream)byteBufferBackedInputStream, copy.remaining());
/* 35 */     byteBufferBackedInputStream.close();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 43 */     JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
/* 44 */     if (v2 != null)
/* 45 */       v2.itemsFormat(JsonFormatTypes.INTEGER); 
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/ByteBufferSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */