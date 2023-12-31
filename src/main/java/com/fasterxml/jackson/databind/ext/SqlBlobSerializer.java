/*    */ package com.fasterxml.jackson.databind.ext;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import com.fasterxml.jackson.core.JsonToken;
/*    */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.JsonMappingException;
/*    */ import com.fasterxml.jackson.databind.SerializerProvider;
/*    */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*    */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
/*    */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
/*    */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.sql.Blob;
/*    */ import java.sql.SQLException;
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
/*    */ @JacksonStdImpl
/*    */ public class SqlBlobSerializer
/*    */   extends StdScalarSerializer<Blob>
/*    */ {
/*    */   public SqlBlobSerializer() {
/* 35 */     super(Blob.class);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isEmpty(SerializerProvider provider, Blob value) {
/* 41 */     return (value == null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void serialize(Blob value, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
/* 47 */     _writeValue(value, gen, ctxt);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void serializeWithType(Blob value, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer) throws IOException {
/* 57 */     WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer
/* 58 */         .typeId(value, JsonToken.VALUE_EMBEDDED_OBJECT));
/* 59 */     _writeValue(value, gen, ctxt);
/* 60 */     typeSer.writeTypeSuffix(gen, typeIdDef);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void _writeValue(Blob value, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
/* 66 */     InputStream in = null;
/*    */     try {
/* 68 */       in = value.getBinaryStream();
/* 69 */     } catch (SQLException e) {
/* 70 */       ctxt.reportMappingProblem(e, "Failed to access `java.sql.Blob` value to write as binary value", new Object[0]);
/*    */     } 
/*    */     
/* 73 */     gen.writeBinary(ctxt.getConfig().getBase64Variant(), in, -1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 84 */     JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
/* 85 */     if (v2 != null)
/* 86 */       v2.itemsFormat(JsonFormatTypes.INTEGER); 
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ext/SqlBlobSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */