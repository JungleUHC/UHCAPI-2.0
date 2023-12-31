/*     */ package org.springframework.http.codec.support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import org.springframework.core.codec.Decoder;
/*     */ import org.springframework.core.codec.Encoder;
/*     */ import org.springframework.http.codec.ClientCodecConfigurer;
/*     */ import org.springframework.http.codec.EncoderHttpMessageWriter;
/*     */ import org.springframework.http.codec.FormHttpMessageWriter;
/*     */ import org.springframework.http.codec.HttpMessageReader;
/*     */ import org.springframework.http.codec.HttpMessageWriter;
/*     */ import org.springframework.http.codec.ServerSentEventHttpMessageReader;
/*     */ import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ClientDefaultCodecsImpl
/*     */   extends BaseDefaultCodecs
/*     */   implements ClientCodecConfigurer.ClientDefaultCodecs
/*     */ {
/*     */   @Nullable
/*     */   private DefaultMultipartCodecs multipartCodecs;
/*     */   @Nullable
/*     */   private Decoder<?> sseDecoder;
/*     */   @Nullable
/*     */   private Supplier<List<HttpMessageWriter<?>>> partWritersSupplier;
/*     */   
/*     */   ClientDefaultCodecsImpl() {}
/*     */   
/*     */   ClientDefaultCodecsImpl(ClientDefaultCodecsImpl other) {
/*  56 */     super(other);
/*  57 */     this.multipartCodecs = (other.multipartCodecs != null) ? new DefaultMultipartCodecs(other.multipartCodecs) : null;
/*     */     
/*  59 */     this.sseDecoder = other.sseDecoder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setPartWritersSupplier(Supplier<List<HttpMessageWriter<?>>> supplier) {
/*  70 */     this.partWritersSupplier = supplier;
/*  71 */     initTypedWriters();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientCodecConfigurer.MultipartCodecs multipartCodecs() {
/*  77 */     if (this.multipartCodecs == null) {
/*  78 */       this.multipartCodecs = new DefaultMultipartCodecs();
/*     */     }
/*  80 */     return this.multipartCodecs;
/*     */   }
/*     */ 
/*     */   
/*     */   public void serverSentEventDecoder(Decoder<?> decoder) {
/*  85 */     this.sseDecoder = decoder;
/*  86 */     initObjectReaders();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
/*  94 */     Decoder<?> decoder = (this.sseDecoder != null) ? this.sseDecoder : (jackson2Present ? getJackson2JsonDecoder() : (kotlinSerializationJsonPresent ? getKotlinSerializationJsonDecoder() : null));
/*     */ 
/*     */     
/*  97 */     addCodec(objectReaders, new ServerSentEventHttpMessageReader(decoder));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
/* 102 */     addCodec(typedWriters, new MultipartHttpMessageWriter(getPartWriters(), (HttpMessageWriter)new FormHttpMessageWriter()));
/*     */   }
/*     */   
/*     */   private List<HttpMessageWriter<?>> getPartWriters() {
/* 106 */     if (this.multipartCodecs != null) {
/* 107 */       return this.multipartCodecs.getWriters();
/*     */     }
/* 109 */     if (this.partWritersSupplier != null) {
/* 110 */       return this.partWritersSupplier.get();
/*     */     }
/*     */     
/* 113 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class DefaultMultipartCodecs
/*     */     implements ClientCodecConfigurer.MultipartCodecs
/*     */   {
/* 123 */     private final List<HttpMessageWriter<?>> writers = new ArrayList<>();
/*     */ 
/*     */     
/*     */     DefaultMultipartCodecs() {}
/*     */ 
/*     */     
/*     */     DefaultMultipartCodecs(DefaultMultipartCodecs other) {
/* 130 */       this.writers.addAll(other.writers);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ClientCodecConfigurer.MultipartCodecs encoder(Encoder<?> encoder) {
/* 136 */       writer((HttpMessageWriter<?>)new EncoderHttpMessageWriter(encoder));
/* 137 */       ClientDefaultCodecsImpl.this.initTypedWriters();
/* 138 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ClientCodecConfigurer.MultipartCodecs writer(HttpMessageWriter<?> writer) {
/* 143 */       this.writers.add(writer);
/* 144 */       ClientDefaultCodecsImpl.this.initTypedWriters();
/* 145 */       return this;
/*     */     }
/*     */     
/*     */     List<HttpMessageWriter<?>> getWriters() {
/* 149 */       return this.writers;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/support/ClientDefaultCodecsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */