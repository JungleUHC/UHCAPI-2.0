/*    */ package com.fasterxml.jackson.databind.deser.std;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.HashSet;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JdkDeserializers
/*    */ {
/* 17 */   private static final HashSet<String> _classNames = new HashSet<>();
/*    */   
/*    */   static {
/* 20 */     Class<?>[] types = new Class[] { UUID.class, AtomicBoolean.class, AtomicInteger.class, AtomicLong.class, StackTraceElement.class, ByteBuffer.class, Void.class };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 29 */     for (Class<?> cls : types) _classNames.add(cls.getName()); 
/* 30 */     for (Class<?> cls : FromStringDeserializer.types()) _classNames.add(cls.getName());
/*    */   
/*    */   }
/*    */   
/*    */   public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
/* 35 */     if (_classNames.contains(clsName)) {
/* 36 */       JsonDeserializer<?> d = FromStringDeserializer.findDeserializer(rawType);
/* 37 */       if (d != null) {
/* 38 */         return d;
/*    */       }
/* 40 */       if (rawType == UUID.class) {
/* 41 */         return new UUIDDeserializer();
/*    */       }
/* 43 */       if (rawType == StackTraceElement.class) {
/* 44 */         return new StackTraceElementDeserializer();
/*    */       }
/* 46 */       if (rawType == AtomicBoolean.class) {
/* 47 */         return new AtomicBooleanDeserializer();
/*    */       }
/* 49 */       if (rawType == AtomicInteger.class) {
/* 50 */         return new AtomicIntegerDeserializer();
/*    */       }
/* 52 */       if (rawType == AtomicLong.class) {
/* 53 */         return new AtomicLongDeserializer();
/*    */       }
/* 55 */       if (rawType == ByteBuffer.class) {
/* 56 */         return new ByteBufferDeserializer();
/*    */       }
/* 58 */       if (rawType == Void.class) {
/* 59 */         return NullifyingDeserializer.instance;
/*    */       }
/*    */     } 
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean hasDeserializerFor(Class<?> rawType) {
/* 67 */     return _classNames.contains(rawType.getName());
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/JdkDeserializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */