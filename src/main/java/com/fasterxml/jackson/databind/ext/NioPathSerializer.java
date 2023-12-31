/*    */ package com.fasterxml.jackson.databind.ext;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import com.fasterxml.jackson.core.JsonToken;
/*    */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*    */ import com.fasterxml.jackson.databind.SerializerProvider;
/*    */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NioPathSerializer
/*    */   extends StdScalarSerializer<Path>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public NioPathSerializer() {
/* 22 */     super(Path.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public void serialize(Path value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
/* 27 */     gen.writeString(value.toUri().toString());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void serializeWithType(Path value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 37 */     WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
/* 38 */         .typeId(value, Path.class, JsonToken.VALUE_STRING));
/* 39 */     serialize(value, g, provider);
/* 40 */     typeSer.writeTypeSuffix(g, typeIdDef);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ext/NioPathSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */