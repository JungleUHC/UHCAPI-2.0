/*    */ package com.fasterxml.jackson.databind.util;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.JavaType;
/*    */ import com.fasterxml.jackson.databind.type.TypeFactory;
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
/*    */ public abstract class StdConverter<IN, OUT>
/*    */   implements Converter<IN, OUT>
/*    */ {
/*    */   public abstract OUT convert(IN paramIN);
/*    */   
/*    */   public JavaType getInputType(TypeFactory typeFactory) {
/* 27 */     return _findConverterType(typeFactory).containedType(0);
/*    */   }
/*    */ 
/*    */   
/*    */   public JavaType getOutputType(TypeFactory typeFactory) {
/* 32 */     return _findConverterType(typeFactory).containedType(1);
/*    */   }
/*    */   
/*    */   protected JavaType _findConverterType(TypeFactory tf) {
/* 36 */     JavaType thisType = tf.constructType(getClass());
/* 37 */     JavaType convType = thisType.findSuperType(Converter.class);
/* 38 */     if (convType == null || convType.containedTypeCount() < 2) {
/* 39 */       throw new IllegalStateException("Cannot find OUT type parameter for Converter of type " + getClass().getName());
/*    */     }
/* 41 */     return convType;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/StdConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */