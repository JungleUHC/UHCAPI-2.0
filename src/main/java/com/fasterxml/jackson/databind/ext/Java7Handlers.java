/*    */ package com.fasterxml.jackson.databind.ext;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*    */ import com.fasterxml.jackson.databind.JsonSerializer;
/*    */ import com.fasterxml.jackson.databind.util.ClassUtil;
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
/*    */ public abstract class Java7Handlers
/*    */ {
/*    */   private static final Java7Handlers IMPL;
/*    */   
/*    */   static {
/* 21 */     Java7Handlers impl = null;
/*    */     try {
/* 23 */       Class<?> cls = Class.forName("com.fasterxml.jackson.databind.ext.Java7HandlersImpl");
/* 24 */       impl = (Java7Handlers)ClassUtil.createInstance(cls, false);
/* 25 */     } catch (Throwable throwable) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 32 */     IMPL = impl;
/*    */   }
/*    */   
/*    */   public static Java7Handlers instance() {
/* 36 */     return IMPL;
/*    */   }
/*    */   
/*    */   public abstract JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> paramClass);
/*    */   
/*    */   public abstract JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> paramClass);
/*    */   
/*    */   public abstract Class<?> getClassJavaNioFilePath();
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ext/Java7Handlers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */