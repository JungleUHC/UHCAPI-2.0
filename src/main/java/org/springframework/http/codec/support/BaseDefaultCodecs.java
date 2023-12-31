/*     */ package org.springframework.http.codec.support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
/*     */ import org.springframework.core.SpringProperties;
/*     */ import org.springframework.core.codec.AbstractDataBufferDecoder;
/*     */ import org.springframework.core.codec.ByteArrayDecoder;
/*     */ import org.springframework.core.codec.ByteArrayEncoder;
/*     */ import org.springframework.core.codec.ByteBufferDecoder;
/*     */ import org.springframework.core.codec.ByteBufferEncoder;
/*     */ import org.springframework.core.codec.CharSequenceEncoder;
/*     */ import org.springframework.core.codec.DataBufferDecoder;
/*     */ import org.springframework.core.codec.DataBufferEncoder;
/*     */ import org.springframework.core.codec.Decoder;
/*     */ import org.springframework.core.codec.Encoder;
/*     */ import org.springframework.core.codec.NettyByteBufDecoder;
/*     */ import org.springframework.core.codec.NettyByteBufEncoder;
/*     */ import org.springframework.core.codec.ResourceDecoder;
/*     */ import org.springframework.core.codec.StringDecoder;
/*     */ import org.springframework.http.codec.CodecConfigurer;
/*     */ import org.springframework.http.codec.DecoderHttpMessageReader;
/*     */ import org.springframework.http.codec.EncoderHttpMessageWriter;
/*     */ import org.springframework.http.codec.FormHttpMessageReader;
/*     */ import org.springframework.http.codec.FormHttpMessageWriter;
/*     */ import org.springframework.http.codec.HttpMessageReader;
/*     */ import org.springframework.http.codec.HttpMessageWriter;
/*     */ import org.springframework.http.codec.ResourceHttpMessageReader;
/*     */ import org.springframework.http.codec.ResourceHttpMessageWriter;
/*     */ import org.springframework.http.codec.ServerSentEventHttpMessageReader;
/*     */ import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
/*     */ import org.springframework.http.codec.json.AbstractJackson2Decoder;
/*     */ import org.springframework.http.codec.json.Jackson2JsonDecoder;
/*     */ import org.springframework.http.codec.json.Jackson2JsonEncoder;
/*     */ import org.springframework.http.codec.json.Jackson2SmileDecoder;
/*     */ import org.springframework.http.codec.json.Jackson2SmileEncoder;
/*     */ import org.springframework.http.codec.json.KotlinSerializationJsonDecoder;
/*     */ import org.springframework.http.codec.json.KotlinSerializationJsonEncoder;
/*     */ import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
/*     */ import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
/*     */ import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
/*     */ import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
/*     */ import org.springframework.http.codec.protobuf.ProtobufDecoder;
/*     */ import org.springframework.http.codec.protobuf.ProtobufEncoder;
/*     */ import org.springframework.http.codec.protobuf.ProtobufHttpMessageWriter;
/*     */ import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
/*     */ import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.ObjectUtils;
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
/*     */ class BaseDefaultCodecs
/*     */   implements CodecConfigurer.DefaultCodecs, CodecConfigurer.DefaultCodecConfig
/*     */ {
/*  85 */   private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
/*     */   
/*     */   static final boolean jackson2Present;
/*     */   private static final boolean jackson2SmilePresent;
/*     */   private static final boolean jaxb2Present;
/*     */   private static final boolean protobufPresent;
/*     */   static final boolean synchronossMultipartPresent;
/*     */   static final boolean nettyByteBufPresent;
/*     */   static final boolean kotlinSerializationJsonPresent;
/*     */   @Nullable
/*     */   private Decoder<?> jackson2JsonDecoder;
/*     */   @Nullable
/*     */   private Encoder<?> jackson2JsonEncoder;
/*     */   @Nullable
/*     */   private Encoder<?> jackson2SmileEncoder;
/*     */   
/*     */   static {
/* 102 */     ClassLoader classLoader = BaseCodecConfigurer.class.getClassLoader();
/*     */     
/* 104 */     jackson2Present = (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader));
/* 105 */     jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
/* 106 */     jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", classLoader);
/* 107 */     protobufPresent = ClassUtils.isPresent("com.google.protobuf.Message", classLoader);
/* 108 */     synchronossMultipartPresent = ClassUtils.isPresent("org.synchronoss.cloud.nio.multipart.NioMultipartParser", classLoader);
/* 109 */     nettyByteBufPresent = ClassUtils.isPresent("io.netty.buffer.ByteBuf", classLoader);
/* 110 */     kotlinSerializationJsonPresent = ClassUtils.isPresent("kotlinx.serialization.json.Json", classLoader);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Decoder<?> jackson2SmileDecoder;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Decoder<?> protobufDecoder;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Encoder<?> protobufEncoder;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Decoder<?> jaxb2Decoder;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Encoder<?> jaxb2Encoder;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Decoder<?> kotlinSerializationJsonDecoder;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Encoder<?> kotlinSerializationJsonEncoder;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Consumer<Object> codecConsumer;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Integer maxInMemorySize;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Boolean enableLoggingRequestDetails;
/*     */ 
/*     */   
/*     */   private boolean registerDefaults = true;
/*     */ 
/*     */   
/* 158 */   private final List<HttpMessageReader<?>> typedReaders = new ArrayList<>();
/*     */   
/* 160 */   private final List<HttpMessageReader<?>> objectReaders = new ArrayList<>();
/*     */   
/* 162 */   private final List<HttpMessageWriter<?>> typedWriters = new ArrayList<>();
/*     */   
/* 164 */   private final List<HttpMessageWriter<?>> objectWriters = new ArrayList<>();
/*     */ 
/*     */   
/*     */   BaseDefaultCodecs() {
/* 168 */     initReaders();
/* 169 */     initWriters();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initReaders() {
/* 177 */     initTypedReaders();
/* 178 */     initObjectReaders();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initWriters() {
/* 186 */     initTypedWriters();
/* 187 */     initObjectWriters();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BaseDefaultCodecs(BaseDefaultCodecs other) {
/* 194 */     this.jackson2JsonDecoder = other.jackson2JsonDecoder;
/* 195 */     this.jackson2JsonEncoder = other.jackson2JsonEncoder;
/* 196 */     this.jackson2SmileDecoder = other.jackson2SmileDecoder;
/* 197 */     this.jackson2SmileEncoder = other.jackson2SmileEncoder;
/* 198 */     this.protobufDecoder = other.protobufDecoder;
/* 199 */     this.protobufEncoder = other.protobufEncoder;
/* 200 */     this.jaxb2Decoder = other.jaxb2Decoder;
/* 201 */     this.jaxb2Encoder = other.jaxb2Encoder;
/* 202 */     this.kotlinSerializationJsonDecoder = other.kotlinSerializationJsonDecoder;
/* 203 */     this.kotlinSerializationJsonEncoder = other.kotlinSerializationJsonEncoder;
/* 204 */     this.codecConsumer = other.codecConsumer;
/* 205 */     this.maxInMemorySize = other.maxInMemorySize;
/* 206 */     this.enableLoggingRequestDetails = other.enableLoggingRequestDetails;
/* 207 */     this.registerDefaults = other.registerDefaults;
/* 208 */     this.typedReaders.addAll(other.typedReaders);
/* 209 */     this.objectReaders.addAll(other.objectReaders);
/* 210 */     this.typedWriters.addAll(other.typedWriters);
/* 211 */     this.objectWriters.addAll(other.objectWriters);
/*     */   }
/*     */ 
/*     */   
/*     */   public void jackson2JsonDecoder(Decoder<?> decoder) {
/* 216 */     this.jackson2JsonDecoder = decoder;
/* 217 */     initObjectReaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public void jackson2JsonEncoder(Encoder<?> encoder) {
/* 222 */     this.jackson2JsonEncoder = encoder;
/* 223 */     initObjectWriters();
/* 224 */     initTypedWriters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void jackson2SmileDecoder(Decoder<?> decoder) {
/* 229 */     this.jackson2SmileDecoder = decoder;
/* 230 */     initObjectReaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public void jackson2SmileEncoder(Encoder<?> encoder) {
/* 235 */     this.jackson2SmileEncoder = encoder;
/* 236 */     initObjectWriters();
/* 237 */     initTypedWriters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void protobufDecoder(Decoder<?> decoder) {
/* 242 */     this.protobufDecoder = decoder;
/* 243 */     initTypedReaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public void protobufEncoder(Encoder<?> encoder) {
/* 248 */     this.protobufEncoder = encoder;
/* 249 */     initTypedWriters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void jaxb2Decoder(Decoder<?> decoder) {
/* 254 */     this.jaxb2Decoder = decoder;
/* 255 */     initObjectReaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public void jaxb2Encoder(Encoder<?> encoder) {
/* 260 */     this.jaxb2Encoder = encoder;
/* 261 */     initObjectWriters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void kotlinSerializationJsonDecoder(Decoder<?> decoder) {
/* 266 */     this.kotlinSerializationJsonDecoder = decoder;
/* 267 */     initObjectReaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public void kotlinSerializationJsonEncoder(Encoder<?> encoder) {
/* 272 */     this.kotlinSerializationJsonEncoder = encoder;
/* 273 */     initObjectWriters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void configureDefaultCodec(Consumer<Object> codecConsumer) {
/* 278 */     this
/* 279 */       .codecConsumer = (this.codecConsumer != null) ? this.codecConsumer.andThen(codecConsumer) : codecConsumer;
/* 280 */     initReaders();
/* 281 */     initWriters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void maxInMemorySize(int byteCount) {
/* 286 */     if (!ObjectUtils.nullSafeEquals(this.maxInMemorySize, Integer.valueOf(byteCount))) {
/* 287 */       this.maxInMemorySize = Integer.valueOf(byteCount);
/* 288 */       initReaders();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Integer maxInMemorySize() {
/* 295 */     return this.maxInMemorySize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void enableLoggingRequestDetails(boolean enable) {
/* 300 */     if (!ObjectUtils.nullSafeEquals(this.enableLoggingRequestDetails, Boolean.valueOf(enable))) {
/* 301 */       this.enableLoggingRequestDetails = Boolean.valueOf(enable);
/* 302 */       initReaders();
/* 303 */       initWriters();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Boolean isEnableLoggingRequestDetails() {
/* 310 */     return this.enableLoggingRequestDetails;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void registerDefaults(boolean registerDefaults) {
/* 317 */     if (this.registerDefaults != registerDefaults) {
/* 318 */       this.registerDefaults = registerDefaults;
/* 319 */       initReaders();
/* 320 */       initWriters();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final List<HttpMessageReader<?>> getTypedReaders() {
/* 329 */     return this.typedReaders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initTypedReaders() {
/* 337 */     this.typedReaders.clear();
/* 338 */     if (!this.registerDefaults) {
/*     */       return;
/*     */     }
/* 341 */     addCodec(this.typedReaders, new DecoderHttpMessageReader((Decoder)new ByteArrayDecoder()));
/* 342 */     addCodec(this.typedReaders, new DecoderHttpMessageReader((Decoder)new ByteBufferDecoder()));
/* 343 */     addCodec(this.typedReaders, new DecoderHttpMessageReader((Decoder)new DataBufferDecoder()));
/* 344 */     if (nettyByteBufPresent) {
/* 345 */       addCodec(this.typedReaders, new DecoderHttpMessageReader((Decoder)new NettyByteBufDecoder()));
/*     */     }
/* 347 */     addCodec(this.typedReaders, new ResourceHttpMessageReader(new ResourceDecoder()));
/* 348 */     addCodec(this.typedReaders, new DecoderHttpMessageReader((Decoder)StringDecoder.textPlainOnly()));
/* 349 */     if (protobufPresent) {
/* 350 */       addCodec(this.typedReaders, new DecoderHttpMessageReader((this.protobufDecoder != null) ? this.protobufDecoder : (Decoder)new ProtobufDecoder()));
/*     */     }
/*     */     
/* 353 */     addCodec(this.typedReaders, new FormHttpMessageReader());
/*     */ 
/*     */     
/* 356 */     extendTypedReaders(this.typedReaders);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected <T> void addCodec(List<T> codecs, T codec) {
/* 364 */     initCodec(codec);
/* 365 */     codecs.add(codec);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initCodec(@Nullable Object codec) {
/* 375 */     if (codec instanceof DecoderHttpMessageReader) {
/* 376 */       codec = ((DecoderHttpMessageReader)codec).getDecoder();
/*     */     }
/* 378 */     else if (codec instanceof EncoderHttpMessageWriter) {
/* 379 */       codec = ((EncoderHttpMessageWriter)codec).getEncoder();
/*     */     } 
/*     */     
/* 382 */     if (codec == null) {
/*     */       return;
/*     */     }
/*     */     
/* 386 */     Integer size = this.maxInMemorySize;
/* 387 */     if (size != null) {
/* 388 */       if (codec instanceof AbstractDataBufferDecoder) {
/* 389 */         ((AbstractDataBufferDecoder)codec).setMaxInMemorySize(size.intValue());
/*     */       }
/* 391 */       if (protobufPresent && 
/* 392 */         codec instanceof ProtobufDecoder) {
/* 393 */         ((ProtobufDecoder)codec).setMaxMessageSize(size.intValue());
/*     */       }
/*     */       
/* 396 */       if (kotlinSerializationJsonPresent && 
/* 397 */         codec instanceof KotlinSerializationJsonDecoder) {
/* 398 */         ((KotlinSerializationJsonDecoder)codec).setMaxInMemorySize(size.intValue());
/*     */       }
/*     */       
/* 401 */       if (jackson2Present && 
/* 402 */         codec instanceof AbstractJackson2Decoder) {
/* 403 */         ((AbstractJackson2Decoder)codec).setMaxInMemorySize(size.intValue());
/*     */       }
/*     */       
/* 406 */       if (jaxb2Present && !shouldIgnoreXml && 
/* 407 */         codec instanceof Jaxb2XmlDecoder) {
/* 408 */         ((Jaxb2XmlDecoder)codec).setMaxInMemorySize(size.intValue());
/*     */       }
/*     */       
/* 411 */       if (codec instanceof FormHttpMessageReader) {
/* 412 */         ((FormHttpMessageReader)codec).setMaxInMemorySize(size.intValue());
/*     */       }
/* 414 */       if (codec instanceof ServerSentEventHttpMessageReader) {
/* 415 */         ((ServerSentEventHttpMessageReader)codec).setMaxInMemorySize(size.intValue());
/*     */       }
/* 417 */       if (codec instanceof DefaultPartHttpMessageReader) {
/* 418 */         ((DefaultPartHttpMessageReader)codec).setMaxInMemorySize(size.intValue());
/*     */       }
/* 420 */       if (synchronossMultipartPresent && 
/* 421 */         codec instanceof SynchronossPartHttpMessageReader) {
/* 422 */         ((SynchronossPartHttpMessageReader)codec).setMaxInMemorySize(size.intValue());
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 427 */     Boolean enable = this.enableLoggingRequestDetails;
/* 428 */     if (enable != null) {
/* 429 */       if (codec instanceof FormHttpMessageReader) {
/* 430 */         ((FormHttpMessageReader)codec).setEnableLoggingRequestDetails(enable.booleanValue());
/*     */       }
/* 432 */       if (codec instanceof MultipartHttpMessageReader) {
/* 433 */         ((MultipartHttpMessageReader)codec).setEnableLoggingRequestDetails(enable.booleanValue());
/*     */       }
/* 435 */       if (codec instanceof DefaultPartHttpMessageReader) {
/* 436 */         ((DefaultPartHttpMessageReader)codec).setEnableLoggingRequestDetails(enable.booleanValue());
/*     */       }
/* 438 */       if (synchronossMultipartPresent && 
/* 439 */         codec instanceof SynchronossPartHttpMessageReader) {
/* 440 */         ((SynchronossPartHttpMessageReader)codec).setEnableLoggingRequestDetails(enable.booleanValue());
/*     */       }
/*     */       
/* 443 */       if (codec instanceof FormHttpMessageWriter) {
/* 444 */         ((FormHttpMessageWriter)codec).setEnableLoggingRequestDetails(enable.booleanValue());
/*     */       }
/* 446 */       if (codec instanceof MultipartHttpMessageWriter) {
/* 447 */         ((MultipartHttpMessageWriter)codec).setEnableLoggingRequestDetails(enable.booleanValue());
/*     */       }
/*     */     } 
/*     */     
/* 451 */     if (this.codecConsumer != null) {
/* 452 */       this.codecConsumer.accept(codec);
/*     */     }
/*     */ 
/*     */     
/* 456 */     if (codec instanceof MultipartHttpMessageReader) {
/* 457 */       initCodec(((MultipartHttpMessageReader)codec).getPartReader());
/*     */     }
/* 459 */     else if (codec instanceof MultipartHttpMessageWriter) {
/* 460 */       initCodec(((MultipartHttpMessageWriter)codec).getFormWriter());
/*     */     }
/* 462 */     else if (codec instanceof ServerSentEventHttpMessageReader) {
/* 463 */       initCodec(((ServerSentEventHttpMessageReader)codec).getDecoder());
/*     */     }
/* 465 */     else if (codec instanceof ServerSentEventHttpMessageWriter) {
/* 466 */       initCodec(((ServerSentEventHttpMessageWriter)codec).getEncoder());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final List<HttpMessageReader<?>> getObjectReaders() {
/* 480 */     return this.objectReaders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initObjectReaders() {
/* 488 */     this.objectReaders.clear();
/* 489 */     if (!this.registerDefaults) {
/*     */       return;
/*     */     }
/* 492 */     if (kotlinSerializationJsonPresent) {
/* 493 */       addCodec(this.objectReaders, new DecoderHttpMessageReader(getKotlinSerializationJsonDecoder()));
/*     */     }
/* 495 */     if (jackson2Present) {
/* 496 */       addCodec(this.objectReaders, new DecoderHttpMessageReader(getJackson2JsonDecoder()));
/*     */     }
/* 498 */     if (jackson2SmilePresent) {
/* 499 */       addCodec(this.objectReaders, new DecoderHttpMessageReader((this.jackson2SmileDecoder != null) ? this.jackson2SmileDecoder : (Decoder)new Jackson2SmileDecoder()));
/*     */     }
/*     */     
/* 502 */     if (jaxb2Present && !shouldIgnoreXml) {
/* 503 */       addCodec(this.objectReaders, new DecoderHttpMessageReader((this.jaxb2Decoder != null) ? this.jaxb2Decoder : (Decoder)new Jaxb2XmlDecoder()));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 508 */     extendObjectReaders(this.objectReaders);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final List<HttpMessageReader<?>> getCatchAllReaders() {
/* 521 */     if (!this.registerDefaults) {
/* 522 */       return Collections.emptyList();
/*     */     }
/* 524 */     List<HttpMessageReader<?>> readers = new ArrayList<>();
/* 525 */     addCodec(readers, new DecoderHttpMessageReader((Decoder)StringDecoder.allMimeTypes()));
/* 526 */     return readers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final List<HttpMessageWriter<?>> getTypedWriters() {
/* 534 */     return this.typedWriters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initTypedWriters() {
/* 542 */     this.typedWriters.clear();
/* 543 */     if (!this.registerDefaults) {
/*     */       return;
/*     */     }
/* 546 */     this.typedWriters.addAll(getBaseTypedWriters());
/* 547 */     extendTypedWriters(this.typedWriters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final List<HttpMessageWriter<?>> getBaseTypedWriters() {
/* 554 */     if (!this.registerDefaults) {
/* 555 */       return Collections.emptyList();
/*     */     }
/* 557 */     List<HttpMessageWriter<?>> writers = new ArrayList<>();
/* 558 */     addCodec(writers, new EncoderHttpMessageWriter((Encoder)new ByteArrayEncoder()));
/* 559 */     addCodec(writers, new EncoderHttpMessageWriter((Encoder)new ByteBufferEncoder()));
/* 560 */     addCodec(writers, new EncoderHttpMessageWriter((Encoder)new DataBufferEncoder()));
/* 561 */     if (nettyByteBufPresent) {
/* 562 */       addCodec(writers, new EncoderHttpMessageWriter((Encoder)new NettyByteBufEncoder()));
/*     */     }
/* 564 */     addCodec(writers, new ResourceHttpMessageWriter());
/* 565 */     addCodec(writers, new EncoderHttpMessageWriter((Encoder)CharSequenceEncoder.textPlainOnly()));
/* 566 */     if (protobufPresent) {
/* 567 */       addCodec(writers, new ProtobufHttpMessageWriter((this.protobufEncoder != null) ? this.protobufEncoder : (Encoder)new ProtobufEncoder()));
/*     */     }
/*     */     
/* 570 */     return writers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final List<HttpMessageWriter<?>> getObjectWriters() {
/* 583 */     return this.objectWriters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initObjectWriters() {
/* 591 */     this.objectWriters.clear();
/* 592 */     if (!this.registerDefaults) {
/*     */       return;
/*     */     }
/* 595 */     this.objectWriters.addAll(getBaseObjectWriters());
/* 596 */     extendObjectWriters(this.objectWriters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final List<HttpMessageWriter<?>> getBaseObjectWriters() {
/* 603 */     List<HttpMessageWriter<?>> writers = new ArrayList<>();
/* 604 */     if (kotlinSerializationJsonPresent) {
/* 605 */       addCodec(writers, new EncoderHttpMessageWriter(getKotlinSerializationJsonEncoder()));
/*     */     }
/* 607 */     if (jackson2Present) {
/* 608 */       addCodec(writers, new EncoderHttpMessageWriter(getJackson2JsonEncoder()));
/*     */     }
/* 610 */     if (jackson2SmilePresent) {
/* 611 */       addCodec(writers, new EncoderHttpMessageWriter((this.jackson2SmileEncoder != null) ? this.jackson2SmileEncoder : (Encoder)new Jackson2SmileEncoder()));
/*     */     }
/*     */     
/* 614 */     if (jaxb2Present && !shouldIgnoreXml) {
/* 615 */       addCodec(writers, new EncoderHttpMessageWriter((this.jaxb2Encoder != null) ? this.jaxb2Encoder : (Encoder)new Jaxb2XmlEncoder()));
/*     */     }
/*     */     
/* 618 */     return writers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   List<HttpMessageWriter<?>> getCatchAllWriters() {
/* 631 */     if (!this.registerDefaults) {
/* 632 */       return Collections.emptyList();
/*     */     }
/* 634 */     List<HttpMessageWriter<?>> result = new ArrayList<>();
/* 635 */     result.add(new EncoderHttpMessageWriter((Encoder)CharSequenceEncoder.allMimeTypes()));
/* 636 */     return result;
/*     */   }
/*     */   
/*     */   void applyDefaultConfig(BaseCodecConfigurer.DefaultCustomCodecs customCodecs) {
/* 640 */     applyDefaultConfig(customCodecs.getTypedReaders());
/* 641 */     applyDefaultConfig(customCodecs.getObjectReaders());
/* 642 */     applyDefaultConfig(customCodecs.getTypedWriters());
/* 643 */     applyDefaultConfig(customCodecs.getObjectWriters());
/* 644 */     customCodecs.getDefaultConfigConsumers().forEach(consumer -> consumer.accept(this));
/*     */   }
/*     */   
/*     */   private void applyDefaultConfig(Map<?, Boolean> readers) {
/* 648 */     readers.entrySet().stream()
/* 649 */       .filter(Map.Entry::getValue)
/* 650 */       .map(Map.Entry::getKey)
/* 651 */       .forEach(this::initCodec);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Decoder<?> getJackson2JsonDecoder() {
/* 658 */     if (this.jackson2JsonDecoder == null) {
/* 659 */       this.jackson2JsonDecoder = (Decoder<?>)new Jackson2JsonDecoder();
/*     */     }
/* 661 */     return this.jackson2JsonDecoder;
/*     */   }
/*     */   
/*     */   protected Encoder<?> getJackson2JsonEncoder() {
/* 665 */     if (this.jackson2JsonEncoder == null) {
/* 666 */       this.jackson2JsonEncoder = (Encoder<?>)new Jackson2JsonEncoder();
/*     */     }
/* 668 */     return this.jackson2JsonEncoder;
/*     */   }
/*     */   
/*     */   protected Decoder<?> getKotlinSerializationJsonDecoder() {
/* 672 */     if (this.kotlinSerializationJsonDecoder == null) {
/* 673 */       this.kotlinSerializationJsonDecoder = (Decoder<?>)new KotlinSerializationJsonDecoder();
/*     */     }
/* 675 */     return this.kotlinSerializationJsonDecoder;
/*     */   }
/*     */   
/*     */   protected Encoder<?> getKotlinSerializationJsonEncoder() {
/* 679 */     if (this.kotlinSerializationJsonEncoder == null) {
/* 680 */       this.kotlinSerializationJsonEncoder = (Encoder<?>)new KotlinSerializationJsonEncoder();
/*     */     }
/* 682 */     return this.kotlinSerializationJsonEncoder;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/support/BaseDefaultCodecs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */