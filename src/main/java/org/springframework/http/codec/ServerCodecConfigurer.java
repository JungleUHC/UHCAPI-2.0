/*    */ package org.springframework.http.codec;
/*    */ 
/*    */ import org.springframework.core.codec.Encoder;
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
/*    */ public interface ServerCodecConfigurer
/*    */   extends CodecConfigurer
/*    */ {
/*    */   static ServerCodecConfigurer create() {
/* 76 */     return CodecConfigurerFactory.<ServerCodecConfigurer>create(ServerCodecConfigurer.class);
/*    */   }
/*    */   
/*    */   ServerCodecConfigurer clone();
/*    */   
/*    */   ServerDefaultCodecs defaultCodecs();
/*    */   
/*    */   public static interface ServerDefaultCodecs extends CodecConfigurer.DefaultCodecs {
/*    */     void multipartReader(HttpMessageReader<?> param1HttpMessageReader);
/*    */     
/*    */     void serverSentEventEncoder(Encoder<?> param1Encoder);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/ServerCodecConfigurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */