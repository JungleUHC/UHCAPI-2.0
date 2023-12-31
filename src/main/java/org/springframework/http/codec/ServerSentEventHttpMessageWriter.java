/*     */ package org.springframework.http.codec;
/*     */ 
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.time.Duration;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.CodecException;
/*     */ import org.springframework.core.codec.Encoder;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.io.buffer.PooledDataBuffer;
/*     */ import org.springframework.http.HttpLogging;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ReactiveHttpOutputMessage;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MimeType;
/*     */ import org.springframework.util.StringUtils;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
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
/*     */ public class ServerSentEventHttpMessageWriter
/*     */   implements HttpMessageWriter<Object>
/*     */ {
/*  57 */   private static final MediaType DEFAULT_MEDIA_TYPE = new MediaType("text", "event-stream", StandardCharsets.UTF_8);
/*     */   
/*  59 */   private static final List<MediaType> WRITABLE_MEDIA_TYPES = Collections.singletonList(MediaType.TEXT_EVENT_STREAM);
/*     */   
/*  61 */   private static final Log logger = HttpLogging.forLogName(ServerSentEventHttpMessageWriter.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final Encoder<?> encoder;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerSentEventHttpMessageWriter() {
/*  73 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerSentEventHttpMessageWriter(@Nullable Encoder<?> encoder) {
/*  82 */     this.encoder = encoder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Encoder<?> getEncoder() {
/*  91 */     return this.encoder;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> getWritableMediaTypes() {
/*  96 */     return WRITABLE_MEDIA_TYPES;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
/* 102 */     return (mediaType == null || MediaType.TEXT_EVENT_STREAM.includes(mediaType) || ServerSentEvent.class
/* 103 */       .isAssignableFrom(elementType.toClass()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Void> write(Publisher<?> input, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
/* 110 */     mediaType = (mediaType != null && mediaType.getCharset() != null) ? mediaType : DEFAULT_MEDIA_TYPE;
/* 111 */     DataBufferFactory bufferFactory = message.bufferFactory();
/*     */     
/* 113 */     message.getHeaders().setContentType(mediaType);
/* 114 */     return message.writeAndFlushWith((Publisher)encode(input, elementType, mediaType, bufferFactory, hints));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Flux<Publisher<DataBuffer>> encode(Publisher<?> input, ResolvableType elementType, MediaType mediaType, DataBufferFactory factory, Map<String, Object> hints) {
/* 121 */     ResolvableType dataType = ServerSentEvent.class.isAssignableFrom(elementType.toClass()) ? elementType.getGeneric(new int[0]) : elementType;
/*     */     
/* 123 */     return Flux.from(input).map(element -> {
/*     */           Flux<DataBuffer> result;
/*     */           ServerSentEvent<?> sse = (element instanceof ServerSentEvent) ? (ServerSentEvent)element : ServerSentEvent.builder().data(element).build();
/*     */           StringBuilder sb = new StringBuilder();
/*     */           String id = sse.id();
/*     */           String event = sse.event();
/*     */           Duration retry = sse.retry();
/*     */           String comment = sse.comment();
/*     */           Object data = sse.data();
/*     */           if (id != null) {
/*     */             writeField("id", id, sb);
/*     */           }
/*     */           if (event != null) {
/*     */             writeField("event", event, sb);
/*     */           }
/*     */           if (retry != null) {
/*     */             writeField("retry", Long.valueOf(retry.toMillis()), sb);
/*     */           }
/*     */           if (comment != null) {
/*     */             sb.append(':').append(StringUtils.replace(comment, "\n", "\n:")).append('\n');
/*     */           }
/*     */           if (data != null) {
/*     */             sb.append("data:");
/*     */           }
/*     */           if (data == null) {
/*     */             result = Flux.just(encodeText(sb + "\n", mediaType, factory));
/*     */           } else if (data instanceof String) {
/*     */             data = StringUtils.replace((String)data, "\n", "\ndata:");
/*     */             result = Flux.just(encodeText(sb + (String)data + "\n\n", mediaType, factory));
/*     */           } else {
/*     */             result = encodeEvent(sb, data, dataType, mediaType, factory, hints);
/*     */           } 
/*     */           return (Publisher)result.doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
/*     */         });
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
/*     */   
/*     */   private <T> Flux<DataBuffer> encodeEvent(StringBuilder eventContent, T data, ResolvableType dataType, MediaType mediaType, DataBufferFactory factory, Map<String, Object> hints) {
/* 170 */     if (this.encoder == null) {
/* 171 */       throw new CodecException("No SSE encoder configured and the data is not String.");
/*     */     }
/*     */     
/* 174 */     return Flux.defer(() -> {
/*     */           DataBuffer startBuffer = encodeText(eventContent, mediaType, factory);
/*     */           DataBuffer endBuffer = encodeText("\n\n", mediaType, factory);
/*     */           DataBuffer dataBuffer = this.encoder.encodeValue(data, factory, dataType, (MimeType)mediaType, hints);
/*     */           Hints.touchDataBuffer(dataBuffer, hints, logger);
/*     */           return (Publisher)Flux.just((Object[])new DataBuffer[] { startBuffer, dataBuffer, endBuffer });
/*     */         });
/*     */   }
/*     */   
/*     */   private void writeField(String fieldName, Object fieldValue, StringBuilder sb) {
/* 184 */     sb.append(fieldName).append(':').append(fieldValue).append('\n');
/*     */   }
/*     */   
/*     */   private DataBuffer encodeText(CharSequence text, MediaType mediaType, DataBufferFactory bufferFactory) {
/* 188 */     Assert.notNull(mediaType.getCharset(), "Expected MediaType with charset");
/* 189 */     byte[] bytes = text.toString().getBytes(mediaType.getCharset());
/* 190 */     return bufferFactory.wrap(bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Void> write(Publisher<?> input, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
/* 198 */     Map<String, Object> allHints = Hints.merge(hints, 
/* 199 */         getEncodeHints(actualType, elementType, mediaType, request, response));
/*     */     
/* 201 */     return write(input, elementType, mediaType, (ReactiveHttpOutputMessage)response, allHints);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<String, Object> getEncodeHints(ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
/* 207 */     if (this.encoder instanceof HttpMessageEncoder) {
/* 208 */       HttpMessageEncoder<?> encoder = (HttpMessageEncoder)this.encoder;
/* 209 */       return encoder.getEncodeHints(actualType, elementType, mediaType, request, response);
/*     */     } 
/* 211 */     return Hints.none();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/ServerSentEventHttpMessageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */