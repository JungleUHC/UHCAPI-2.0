/*    */ package org.springframework.http.codec;
/*    */ 
/*    */ import org.springframework.core.codec.Decoder;
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
/*    */ public interface ClientCodecConfigurer
/*    */   extends CodecConfigurer
/*    */ {
/*    */   static ClientCodecConfigurer create() {
/* 77 */     return CodecConfigurerFactory.<ClientCodecConfigurer>create(ClientCodecConfigurer.class);
/*    */   }
/*    */   
/*    */   ClientCodecConfigurer clone();
/*    */   
/*    */   ClientDefaultCodecs defaultCodecs();
/*    */   
/*    */   public static interface MultipartCodecs {
/*    */     MultipartCodecs encoder(Encoder<?> param1Encoder);
/*    */     
/*    */     MultipartCodecs writer(HttpMessageWriter<?> param1HttpMessageWriter);
/*    */   }
/*    */   
/*    */   public static interface ClientDefaultCodecs extends CodecConfigurer.DefaultCodecs {
/*    */     ClientCodecConfigurer.MultipartCodecs multipartCodecs();
/*    */     
/*    */     void serverSentEventDecoder(Decoder<?> param1Decoder);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/ClientCodecConfigurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */