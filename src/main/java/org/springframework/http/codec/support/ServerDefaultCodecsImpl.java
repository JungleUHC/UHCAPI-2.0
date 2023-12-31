/*    */ package org.springframework.http.codec.support;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.springframework.core.codec.Encoder;
/*    */ import org.springframework.http.codec.HttpMessageReader;
/*    */ import org.springframework.http.codec.HttpMessageWriter;
/*    */ import org.springframework.http.codec.ServerCodecConfigurer;
/*    */ import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
/*    */ import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
/*    */ import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
/*    */ import org.springframework.http.codec.multipart.PartHttpMessageWriter;
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
/*    */ class ServerDefaultCodecsImpl
/*    */   extends BaseDefaultCodecs
/*    */   implements ServerCodecConfigurer.ServerDefaultCodecs
/*    */ {
/*    */   @Nullable
/*    */   private HttpMessageReader<?> multipartReader;
/*    */   @Nullable
/*    */   private Encoder<?> sseEncoder;
/*    */   
/*    */   ServerDefaultCodecsImpl() {}
/*    */   
/*    */   ServerDefaultCodecsImpl(ServerDefaultCodecsImpl other) {
/* 49 */     super(other);
/* 50 */     this.multipartReader = other.multipartReader;
/* 51 */     this.sseEncoder = other.sseEncoder;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void multipartReader(HttpMessageReader<?> reader) {
/* 57 */     this.multipartReader = reader;
/* 58 */     initTypedReaders();
/*    */   }
/*    */ 
/*    */   
/*    */   public void serverSentEventEncoder(Encoder<?> encoder) {
/* 63 */     this.sseEncoder = encoder;
/* 64 */     initObjectWriters();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
/* 70 */     if (this.multipartReader != null) {
/* 71 */       addCodec(typedReaders, this.multipartReader);
/*    */       return;
/*    */     } 
/* 74 */     DefaultPartHttpMessageReader partReader = new DefaultPartHttpMessageReader();
/* 75 */     addCodec(typedReaders, partReader);
/* 76 */     addCodec(typedReaders, new MultipartHttpMessageReader((HttpMessageReader)partReader));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
/* 81 */     addCodec(typedWriters, new PartHttpMessageWriter());
/*    */   }
/*    */ 
/*    */   
/*    */   protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
/* 86 */     objectWriters.add(new ServerSentEventHttpMessageWriter(getSseEncoder()));
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   private Encoder<?> getSseEncoder() {
/* 91 */     return (this.sseEncoder != null) ? this.sseEncoder : (jackson2Present ? 
/* 92 */       getJackson2JsonEncoder() : (kotlinSerializationJsonPresent ? 
/* 93 */       getKotlinSerializationJsonEncoder() : null));
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/support/ServerDefaultCodecsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */