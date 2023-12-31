/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.CookieImpl;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import org.reactivestreams.Processor;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.ResponseCookie;
/*     */ import org.springframework.http.ZeroCopyHttpOutputMessage;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.core.publisher.MonoSink;
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
/*     */ class UndertowServerHttpResponse
/*     */   extends AbstractListenerServerHttpResponse
/*     */   implements ZeroCopyHttpOutputMessage
/*     */ {
/*     */   private final HttpServerExchange exchange;
/*     */   private final UndertowServerHttpRequest request;
/*     */   @Nullable
/*     */   private StreamSinkChannel responseChannel;
/*     */   
/*     */   UndertowServerHttpResponse(HttpServerExchange exchange, DataBufferFactory bufferFactory, UndertowServerHttpRequest request) {
/*  65 */     super(bufferFactory, createHeaders(exchange));
/*  66 */     Assert.notNull(exchange, "HttpServerExchange must not be null");
/*  67 */     this.exchange = exchange;
/*  68 */     this.request = request;
/*     */   }
/*     */   
/*     */   private static HttpHeaders createHeaders(HttpServerExchange exchange) {
/*  72 */     UndertowHeadersAdapter headersMap = new UndertowHeadersAdapter(exchange.getResponseHeaders());
/*  73 */     return new HttpHeaders(headersMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeResponse() {
/*  80 */     return (T)this.exchange;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpStatus getStatusCode() {
/*  85 */     HttpStatus status = super.getStatusCode();
/*  86 */     return (status != null) ? status : HttpStatus.resolve(this.exchange.getStatusCode());
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getRawStatusCode() {
/*  91 */     Integer status = super.getRawStatusCode();
/*  92 */     return Integer.valueOf((status != null) ? status.intValue() : this.exchange.getStatusCode());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyStatusCode() {
/*  97 */     Integer status = super.getRawStatusCode();
/*  98 */     if (status != null) {
/*  99 */       this.exchange.setStatusCode(status.intValue());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyHeaders() {}
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyCookies() {
/* 110 */     for (String name : getCookies().keySet()) {
/* 111 */       for (ResponseCookie httpCookie : getCookies().get(name)) {
/* 112 */         CookieImpl cookieImpl = new CookieImpl(name, httpCookie.getValue());
/* 113 */         if (!httpCookie.getMaxAge().isNegative()) {
/* 114 */           cookieImpl.setMaxAge(Integer.valueOf((int)httpCookie.getMaxAge().getSeconds()));
/*     */         }
/* 116 */         if (httpCookie.getDomain() != null) {
/* 117 */           cookieImpl.setDomain(httpCookie.getDomain());
/*     */         }
/* 119 */         if (httpCookie.getPath() != null) {
/* 120 */           cookieImpl.setPath(httpCookie.getPath());
/*     */         }
/* 122 */         cookieImpl.setSecure(httpCookie.isSecure());
/* 123 */         cookieImpl.setHttpOnly(httpCookie.isHttpOnly());
/* 124 */         cookieImpl.setSameSiteMode(httpCookie.getSameSite());
/*     */         
/* 126 */         this.exchange.getResponseCookies().putIfAbsent(name, cookieImpl);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeWith(Path file, long position, long count) {
/* 133 */     return doCommit(() -> Mono.create(()));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor() {
/* 155 */     return new ResponseBodyFlushProcessor();
/*     */   }
/*     */   
/*     */   private ResponseBodyProcessor createBodyProcessor() {
/* 159 */     if (this.responseChannel == null) {
/* 160 */       this.responseChannel = this.exchange.getResponseChannel();
/*     */     }
/* 162 */     return new ResponseBodyProcessor(this.responseChannel);
/*     */   }
/*     */ 
/*     */   
/*     */   private class ResponseBodyProcessor
/*     */     extends AbstractListenerWriteProcessor<DataBuffer>
/*     */   {
/*     */     private final StreamSinkChannel channel;
/*     */     
/*     */     @Nullable
/*     */     private volatile ByteBuffer byteBuffer;
/*     */     
/*     */     private volatile boolean writePossible;
/*     */ 
/*     */     
/*     */     public ResponseBodyProcessor(StreamSinkChannel channel) {
/* 178 */       super(UndertowServerHttpResponse.this.request.getLogPrefix());
/* 179 */       Assert.notNull(channel, "StreamSinkChannel must not be null");
/* 180 */       this.channel = channel;
/* 181 */       this.channel.getWriteSetter().set(c -> {
/*     */             this.writePossible = true;
/*     */             onWritePossible();
/*     */           });
/* 185 */       this.channel.suspendWrites();
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isWritePossible() {
/* 190 */       this.channel.resumeWrites();
/* 191 */       return this.writePossible;
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean write(DataBuffer dataBuffer) throws IOException {
/* 196 */       ByteBuffer buffer = this.byteBuffer;
/* 197 */       if (buffer == null) {
/* 198 */         return false;
/*     */       }
/*     */ 
/*     */       
/* 202 */       this.writePossible = false;
/*     */ 
/*     */       
/* 205 */       int total = buffer.remaining();
/* 206 */       int written = writeByteBuffer(buffer);
/*     */       
/* 208 */       if (rsWriteLogger.isTraceEnabled()) {
/* 209 */         rsWriteLogger.trace(getLogPrefix() + "Wrote " + written + " of " + total + " bytes");
/*     */       }
/* 211 */       if (written != total) {
/* 212 */         return false;
/*     */       }
/*     */ 
/*     */       
/* 216 */       this.writePossible = true;
/*     */       
/* 218 */       DataBufferUtils.release(dataBuffer);
/* 219 */       this.byteBuffer = null;
/* 220 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     private int writeByteBuffer(ByteBuffer byteBuffer) throws IOException {
/* 225 */       int written, totalWritten = 0;
/*     */       do {
/* 227 */         written = this.channel.write(byteBuffer);
/* 228 */         totalWritten += written;
/*     */       }
/* 230 */       while (byteBuffer.hasRemaining() && written > 0);
/* 231 */       return totalWritten;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void dataReceived(DataBuffer dataBuffer) {
/* 236 */       super.dataReceived(dataBuffer);
/* 237 */       this.byteBuffer = dataBuffer.asByteBuffer();
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isDataEmpty(DataBuffer dataBuffer) {
/* 242 */       return (dataBuffer.readableByteCount() == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void writingComplete() {
/* 247 */       this.channel.getWriteSetter().set(null);
/* 248 */       this.channel.resumeWrites();
/*     */     }
/*     */ 
/*     */     
/*     */     protected void writingFailed(Throwable ex) {
/* 253 */       cancel();
/* 254 */       onError(ex);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void discardData(DataBuffer dataBuffer) {
/* 259 */       DataBufferUtils.release(dataBuffer);
/*     */     }
/*     */   }
/*     */   
/*     */   private class ResponseBodyFlushProcessor
/*     */     extends AbstractListenerWriteFlushProcessor<DataBuffer>
/*     */   {
/*     */     public ResponseBodyFlushProcessor() {
/* 267 */       super(UndertowServerHttpResponse.this.request.getLogPrefix());
/*     */     }
/*     */ 
/*     */     
/*     */     protected Processor<? super DataBuffer, Void> createWriteProcessor() {
/* 272 */       return UndertowServerHttpResponse.this.createBodyProcessor();
/*     */     }
/*     */ 
/*     */     
/*     */     protected void flush() throws IOException {
/* 277 */       StreamSinkChannel channel = UndertowServerHttpResponse.this.responseChannel;
/* 278 */       if (channel != null) {
/* 279 */         if (rsWriteFlushLogger.isTraceEnabled()) {
/* 280 */           rsWriteFlushLogger.trace(getLogPrefix() + "flush");
/*     */         }
/* 282 */         channel.flush();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isWritePossible() {
/* 288 */       StreamSinkChannel channel = UndertowServerHttpResponse.this.responseChannel;
/* 289 */       if (channel != null) {
/*     */         
/* 291 */         channel.resumeWrites();
/* 292 */         return true;
/*     */       } 
/* 294 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isFlushPending() {
/* 299 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class TransferBodyListener
/*     */   {
/*     */     private final FileChannel source;
/*     */     
/*     */     private final MonoSink<Void> sink;
/*     */     
/*     */     private long position;
/*     */     
/*     */     private long count;
/*     */ 
/*     */     
/*     */     public TransferBodyListener(FileChannel source, long position, long count, MonoSink<Void> sink) {
/* 316 */       this.source = source;
/* 317 */       this.sink = sink;
/* 318 */       this.position = position;
/* 319 */       this.count = count;
/*     */     }
/*     */     
/*     */     public void transfer(StreamSinkChannel destination) {
/*     */       try {
/* 324 */         while (this.count > 0L) {
/* 325 */           long len = destination.transferFrom(this.source, this.position, this.count);
/* 326 */           if (len != 0L) {
/* 327 */             this.position += len;
/* 328 */             this.count -= len;
/*     */             continue;
/*     */           } 
/* 331 */           destination.resumeWrites();
/*     */           
/*     */           return;
/*     */         } 
/* 335 */         this.sink.success();
/*     */       }
/* 337 */       catch (IOException ex) {
/* 338 */         this.sink.error(ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void closeSource() {
/*     */       try {
/* 345 */         this.source.close();
/*     */       }
/* 347 */       catch (IOException iOException) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/UndertowServerHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */