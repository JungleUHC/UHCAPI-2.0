/*     */ package org.springframework.http.codec;
/*     */ 
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.time.Duration;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.CodecException;
/*     */ import org.springframework.core.codec.Decoder;
/*     */ import org.springframework.core.codec.StringDecoder;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferLimitException;
/*     */ import org.springframework.core.io.buffer.DefaultDataBuffer;
/*     */ import org.springframework.core.io.buffer.DefaultDataBufferFactory;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ReactiveHttpInputMessage;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.MimeType;
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
/*     */ public class ServerSentEventHttpMessageReader
/*     */   implements HttpMessageReader<Object>
/*     */ {
/*  50 */   private static final ResolvableType STRING_TYPE = ResolvableType.forClass(String.class);
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final Decoder<?> decoder;
/*     */   
/*  56 */   private final StringDecoder lineDecoder = StringDecoder.textPlainOnly();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerSentEventHttpMessageReader() {
/*  64 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerSentEventHttpMessageReader(@Nullable Decoder<?> decoder) {
/*  72 */     this.decoder = decoder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Decoder<?> getDecoder() {
/*  81 */     return this.decoder;
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
/*     */   public void setMaxInMemorySize(int byteCount) {
/*  95 */     this.lineDecoder.setMaxInMemorySize(byteCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxInMemorySize() {
/* 103 */     return this.lineDecoder.getMaxInMemorySize();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<MediaType> getReadableMediaTypes() {
/* 109 */     return Collections.singletonList(MediaType.TEXT_EVENT_STREAM);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
/* 114 */     return (MediaType.TEXT_EVENT_STREAM.includes(mediaType) || isServerSentEvent(elementType));
/*     */   }
/*     */   
/*     */   private boolean isServerSentEvent(ResolvableType elementType) {
/* 118 */     return ServerSentEvent.class.isAssignableFrom(elementType.toClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<Object> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 126 */     LimitTracker limitTracker = new LimitTracker();
/*     */     
/* 128 */     boolean shouldWrap = isServerSentEvent(elementType);
/* 129 */     ResolvableType valueType = shouldWrap ? elementType.getGeneric(new int[0]) : elementType;
/*     */     
/* 131 */     return this.lineDecoder.decode((Publisher)message.getBody(), STRING_TYPE, null, hints)
/* 132 */       .doOnNext(limitTracker::afterLineParsed)
/* 133 */       .bufferUntil(String::isEmpty)
/* 134 */       .concatMap(lines -> {
/*     */           Object event = buildEvent(lines, valueType, shouldWrap, hints);
/*     */           return (event != null) ? (Publisher)Mono.just(event) : (Publisher)Mono.empty();
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Object buildEvent(List<String> lines, ResolvableType valueType, boolean shouldWrap, Map<String, Object> hints) {
/* 144 */     ServerSentEvent.Builder<Object> sseBuilder = shouldWrap ? ServerSentEvent.<Object>builder() : null;
/* 145 */     StringBuilder data = null;
/* 146 */     StringBuilder comment = null;
/*     */     
/* 148 */     for (String line : lines) {
/* 149 */       if (line.startsWith("data:")) {
/* 150 */         int length = line.length();
/* 151 */         if (length > 5) {
/* 152 */           int index = (line.charAt(5) != ' ') ? 5 : 6;
/* 153 */           if (length > index) {
/* 154 */             data = (data != null) ? data : new StringBuilder();
/* 155 */             data.append(line, index, line.length());
/* 156 */             data.append('\n');
/*     */           } 
/*     */         }  continue;
/*     */       } 
/* 160 */       if (shouldWrap) {
/* 161 */         if (line.startsWith("id:")) {
/* 162 */           sseBuilder.id(line.substring(3).trim()); continue;
/*     */         } 
/* 164 */         if (line.startsWith("event:")) {
/* 165 */           sseBuilder.event(line.substring(6).trim()); continue;
/*     */         } 
/* 167 */         if (line.startsWith("retry:")) {
/* 168 */           sseBuilder.retry(Duration.ofMillis(Long.parseLong(line.substring(6).trim()))); continue;
/*     */         } 
/* 170 */         if (line.startsWith(":")) {
/* 171 */           comment = (comment != null) ? comment : new StringBuilder();
/* 172 */           comment.append(line.substring(1).trim()).append('\n');
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 177 */     Object decodedData = (data != null) ? decodeData(data, valueType, hints) : null;
/*     */     
/* 179 */     if (shouldWrap) {
/* 180 */       if (comment != null) {
/* 181 */         sseBuilder.comment(comment.substring(0, comment.length() - 1));
/*     */       }
/* 183 */       if (decodedData != null) {
/* 184 */         sseBuilder.data(decodedData);
/*     */       }
/* 186 */       return sseBuilder.build();
/*     */     } 
/*     */     
/* 189 */     return decodedData;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Object decodeData(StringBuilder data, ResolvableType dataType, Map<String, Object> hints) {
/* 195 */     if (String.class == dataType.resolve()) {
/* 196 */       return data.substring(0, data.length() - 1);
/*     */     }
/* 198 */     if (this.decoder == null) {
/* 199 */       throw new CodecException("No SSE decoder configured and the data is not String.");
/*     */     }
/* 201 */     byte[] bytes = data.toString().getBytes(StandardCharsets.UTF_8);
/* 202 */     DefaultDataBuffer defaultDataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(bytes);
/* 203 */     return this.decoder.decode((DataBuffer)defaultDataBuffer, dataType, (MimeType)MediaType.TEXT_EVENT_STREAM, hints);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Object> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 213 */     if (elementType.resolve() == String.class) {
/* 214 */       Flux<DataBuffer> body = message.getBody();
/* 215 */       return this.lineDecoder.decodeToMono((Publisher)body, elementType, null, null).cast(Object.class);
/*     */     } 
/*     */     
/* 218 */     return Mono.error(new UnsupportedOperationException("ServerSentEventHttpMessageReader only supports reading stream of events as a Flux"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class LimitTracker
/*     */   {
/* 225 */     private int accumulated = 0;
/*     */     
/*     */     public void afterLineParsed(String line) {
/* 228 */       if (ServerSentEventHttpMessageReader.this.getMaxInMemorySize() < 0) {
/*     */         return;
/*     */       }
/* 231 */       if (line.isEmpty()) {
/* 232 */         this.accumulated = 0;
/*     */       }
/* 234 */       if (line.length() > Integer.MAX_VALUE - this.accumulated) {
/* 235 */         raiseLimitException();
/*     */       } else {
/*     */         
/* 238 */         this.accumulated += line.length();
/* 239 */         if (this.accumulated > ServerSentEventHttpMessageReader.this.getMaxInMemorySize()) {
/* 240 */           raiseLimitException();
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private void raiseLimitException() {
/* 247 */       throw new DataBufferLimitException("Exceeded limit on max bytes to buffer : " + ServerSentEventHttpMessageReader.this.getMaxInMemorySize());
/*     */     }
/*     */     
/*     */     private LimitTracker() {}
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/ServerSentEventHttpMessageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */