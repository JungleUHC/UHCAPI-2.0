/*    */ package org.springframework.http.codec.multipart;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.Channel;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import org.springframework.core.io.buffer.DataBuffer;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.http.MediaType;
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
/*    */ abstract class MultipartUtils
/*    */ {
/*    */   public static Charset charset(HttpHeaders headers) {
/* 40 */     MediaType contentType = headers.getContentType();
/* 41 */     if (contentType != null) {
/* 42 */       Charset charset = contentType.getCharset();
/* 43 */       if (charset != null) {
/* 44 */         return charset;
/*    */       }
/*    */     } 
/* 47 */     return StandardCharsets.UTF_8;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static byte[] concat(byte[]... byteArrays) {
/* 54 */     int len = 0;
/* 55 */     for (byte[] byteArray : byteArrays) {
/* 56 */       len += byteArray.length;
/*    */     }
/* 58 */     byte[] result = new byte[len];
/* 59 */     len = 0;
/* 60 */     for (byte[] byteArray : byteArrays) {
/* 61 */       System.arraycopy(byteArray, 0, result, len, byteArray.length);
/* 62 */       len += byteArray.length;
/*    */     } 
/* 64 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static DataBuffer sliceTo(DataBuffer buf, int idx) {
/* 71 */     int pos = buf.readPosition();
/* 72 */     int len = idx - pos + 1;
/* 73 */     return buf.retainedSlice(pos, len);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static DataBuffer sliceFrom(DataBuffer buf, int idx) {
/* 80 */     int len = buf.writePosition() - idx - 1;
/* 81 */     return buf.retainedSlice(idx + 1, len);
/*    */   }
/*    */   
/*    */   public static void closeChannel(Channel channel) {
/*    */     try {
/* 86 */       if (channel.isOpen()) {
/* 87 */         channel.close();
/*    */       }
/*    */     }
/* 90 */     catch (IOException iOException) {}
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/MultipartUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */