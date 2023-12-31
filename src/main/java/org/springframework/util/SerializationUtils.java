/*    */ package org.springframework.util;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class SerializationUtils
/*    */ {
/*    */   @Nullable
/*    */   public static byte[] serialize(@Nullable Object object) {
/* 51 */     if (object == null) {
/* 52 */       return null;
/*    */     }
/* 54 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
/* 55 */     try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
/* 56 */       oos.writeObject(object);
/* 57 */       oos.flush();
/*    */     }
/* 59 */     catch (IOException ex) {
/* 60 */       throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
/*    */     } 
/* 62 */     return baos.toByteArray();
/*    */   }
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
/*    */   @Nullable
/*    */   public static Object deserialize(@Nullable byte[] bytes) {
/* 78 */     if (bytes == null) {
/* 79 */       return null;
/*    */     }
/* 81 */     try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
/* 82 */       return ois.readObject();
/*    */     }
/* 84 */     catch (IOException ex) {
/* 85 */       throw new IllegalArgumentException("Failed to deserialize object", ex);
/*    */     }
/* 87 */     catch (ClassNotFoundException ex) {
/* 88 */       throw new IllegalStateException("Failed to deserialize object type", ex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/SerializationUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */