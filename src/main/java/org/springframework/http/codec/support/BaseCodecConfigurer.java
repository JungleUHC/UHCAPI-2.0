/*     */ package org.springframework.http.codec.support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.Decoder;
/*     */ import org.springframework.core.codec.Encoder;
/*     */ import org.springframework.http.codec.CodecConfigurer;
/*     */ import org.springframework.http.codec.DecoderHttpMessageReader;
/*     */ import org.springframework.http.codec.EncoderHttpMessageWriter;
/*     */ import org.springframework.http.codec.HttpMessageReader;
/*     */ import org.springframework.http.codec.HttpMessageWriter;
/*     */ import org.springframework.util.Assert;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class BaseCodecConfigurer
/*     */   implements CodecConfigurer
/*     */ {
/*     */   protected final BaseDefaultCodecs defaultCodecs;
/*     */   protected final DefaultCustomCodecs customCodecs;
/*     */   
/*     */   BaseCodecConfigurer(BaseDefaultCodecs defaultCodecs) {
/*  55 */     Assert.notNull(defaultCodecs, "'defaultCodecs' is required");
/*  56 */     this.defaultCodecs = defaultCodecs;
/*  57 */     this.customCodecs = new DefaultCustomCodecs();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BaseCodecConfigurer(BaseCodecConfigurer other) {
/*  65 */     this.defaultCodecs = other.cloneDefaultCodecs();
/*  66 */     this.customCodecs = new DefaultCustomCodecs(other.customCodecs);
/*     */   }
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
/*     */   public CodecConfigurer.DefaultCodecs defaultCodecs() {
/*  79 */     return this.defaultCodecs;
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerDefaults(boolean shouldRegister) {
/*  84 */     this.defaultCodecs.registerDefaults(shouldRegister);
/*     */   }
/*     */ 
/*     */   
/*     */   public CodecConfigurer.CustomCodecs customCodecs() {
/*  89 */     return this.customCodecs;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HttpMessageReader<?>> getReaders() {
/*  94 */     this.defaultCodecs.applyDefaultConfig(this.customCodecs);
/*     */     
/*  96 */     List<HttpMessageReader<?>> result = new ArrayList<>();
/*  97 */     result.addAll(this.customCodecs.getTypedReaders().keySet());
/*  98 */     result.addAll(this.defaultCodecs.getTypedReaders());
/*  99 */     result.addAll(this.customCodecs.getObjectReaders().keySet());
/* 100 */     result.addAll(this.defaultCodecs.getObjectReaders());
/* 101 */     result.addAll(this.defaultCodecs.getCatchAllReaders());
/* 102 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HttpMessageWriter<?>> getWriters() {
/* 107 */     this.defaultCodecs.applyDefaultConfig(this.customCodecs);
/*     */     
/* 109 */     List<HttpMessageWriter<?>> result = new ArrayList<>();
/* 110 */     result.addAll(this.customCodecs.getTypedWriters().keySet());
/* 111 */     result.addAll(this.defaultCodecs.getTypedWriters());
/* 112 */     result.addAll(this.customCodecs.getObjectWriters().keySet());
/* 113 */     result.addAll(this.defaultCodecs.getObjectWriters());
/* 114 */     result.addAll(this.defaultCodecs.getCatchAllWriters());
/* 115 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract BaseDefaultCodecs cloneDefaultCodecs();
/*     */ 
/*     */   
/*     */   public abstract CodecConfigurer clone();
/*     */   
/*     */   protected static final class DefaultCustomCodecs
/*     */     implements CodecConfigurer.CustomCodecs
/*     */   {
/* 127 */     private final Map<HttpMessageReader<?>, Boolean> typedReaders = new LinkedHashMap<>(4);
/*     */     
/* 129 */     private final Map<HttpMessageWriter<?>, Boolean> typedWriters = new LinkedHashMap<>(4);
/*     */     
/* 131 */     private final Map<HttpMessageReader<?>, Boolean> objectReaders = new LinkedHashMap<>(4);
/*     */     
/* 133 */     private final Map<HttpMessageWriter<?>, Boolean> objectWriters = new LinkedHashMap<>(4);
/*     */     
/* 135 */     private final List<Consumer<CodecConfigurer.DefaultCodecConfig>> defaultConfigConsumers = new ArrayList<>(4);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     DefaultCustomCodecs() {}
/*     */ 
/*     */ 
/*     */     
/*     */     DefaultCustomCodecs(DefaultCustomCodecs other) {
/* 145 */       this.typedReaders.putAll(other.typedReaders);
/* 146 */       this.typedWriters.putAll(other.typedWriters);
/* 147 */       this.objectReaders.putAll(other.objectReaders);
/* 148 */       this.objectWriters.putAll(other.objectWriters);
/*     */     }
/*     */ 
/*     */     
/*     */     public void register(Object codec) {
/* 153 */       addCodec(codec, false);
/*     */     }
/*     */ 
/*     */     
/*     */     public void registerWithDefaultConfig(Object codec) {
/* 158 */       addCodec(codec, true);
/*     */     }
/*     */ 
/*     */     
/*     */     public void registerWithDefaultConfig(Object codec, Consumer<CodecConfigurer.DefaultCodecConfig> configConsumer) {
/* 163 */       addCodec(codec, false);
/* 164 */       this.defaultConfigConsumers.add(configConsumer);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void decoder(Decoder<?> decoder) {
/* 170 */       addCodec(decoder, false);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void encoder(Encoder<?> encoder) {
/* 176 */       addCodec(encoder, false);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void reader(HttpMessageReader<?> reader) {
/* 182 */       addCodec(reader, false);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void writer(HttpMessageWriter<?> writer) {
/* 188 */       addCodec(writer, false);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void withDefaultCodecConfig(Consumer<CodecConfigurer.DefaultCodecConfig> codecsConfigConsumer) {
/* 194 */       this.defaultConfigConsumers.add(codecsConfigConsumer);
/*     */     }
/*     */ 
/*     */     
/*     */     private void addCodec(Object codec, boolean applyDefaultConfig) {
/* 199 */       if (codec instanceof Decoder) {
/* 200 */         codec = new DecoderHttpMessageReader((Decoder)codec);
/*     */       }
/* 202 */       else if (codec instanceof Encoder) {
/* 203 */         codec = new EncoderHttpMessageWriter((Encoder)codec);
/*     */       } 
/*     */       
/* 206 */       if (codec instanceof HttpMessageReader) {
/* 207 */         HttpMessageReader<?> reader = (HttpMessageReader)codec;
/* 208 */         boolean canReadToObject = reader.canRead(ResolvableType.forClass(Object.class), null);
/* 209 */         (canReadToObject ? this.objectReaders : this.typedReaders).put(reader, Boolean.valueOf(applyDefaultConfig));
/*     */       }
/* 211 */       else if (codec instanceof HttpMessageWriter) {
/* 212 */         HttpMessageWriter<?> writer = (HttpMessageWriter)codec;
/* 213 */         boolean canWriteObject = writer.canWrite(ResolvableType.forClass(Object.class), null);
/* 214 */         (canWriteObject ? this.objectWriters : this.typedWriters).put(writer, Boolean.valueOf(applyDefaultConfig));
/*     */       } else {
/*     */         
/* 217 */         throw new IllegalArgumentException("Unexpected codec type: " + codec.getClass().getName());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     Map<HttpMessageReader<?>, Boolean> getTypedReaders() {
/* 224 */       return this.typedReaders;
/*     */     }
/*     */     
/*     */     Map<HttpMessageWriter<?>, Boolean> getTypedWriters() {
/* 228 */       return this.typedWriters;
/*     */     }
/*     */     
/*     */     Map<HttpMessageReader<?>, Boolean> getObjectReaders() {
/* 232 */       return this.objectReaders;
/*     */     }
/*     */     
/*     */     Map<HttpMessageWriter<?>, Boolean> getObjectWriters() {
/* 236 */       return this.objectWriters;
/*     */     }
/*     */     
/*     */     List<Consumer<CodecConfigurer.DefaultCodecConfig>> getDefaultConfigConsumers() {
/* 240 */       return this.defaultConfigConsumers;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/support/BaseCodecConfigurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */