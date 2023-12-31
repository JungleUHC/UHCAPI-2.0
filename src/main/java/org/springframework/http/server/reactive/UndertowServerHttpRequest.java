/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import reactor.core.publisher.Flux;
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
/*     */ class UndertowServerHttpRequest
/*     */   extends AbstractServerHttpRequest
/*     */ {
/*  54 */   private static final AtomicLong logPrefixIndex = new AtomicLong();
/*     */ 
/*     */   
/*     */   private final HttpServerExchange exchange;
/*     */ 
/*     */   
/*     */   private final RequestBodyPublisher body;
/*     */ 
/*     */ 
/*     */   
/*     */   public UndertowServerHttpRequest(HttpServerExchange exchange, DataBufferFactory bufferFactory) throws URISyntaxException {
/*  65 */     super(initUri(exchange), "", new UndertowHeadersAdapter(exchange.getRequestHeaders()));
/*  66 */     this.exchange = exchange;
/*  67 */     this.body = new RequestBodyPublisher(exchange, bufferFactory);
/*  68 */     this.body.registerListeners(exchange);
/*     */   }
/*     */   
/*     */   private static URI initUri(HttpServerExchange exchange) throws URISyntaxException {
/*  72 */     Assert.notNull(exchange, "HttpServerExchange is required");
/*  73 */     String requestURL = exchange.getRequestURL();
/*  74 */     String query = exchange.getQueryString();
/*  75 */     String requestUriAndQuery = StringUtils.hasLength(query) ? (requestURL + "?" + query) : requestURL;
/*  76 */     return new URI(requestUriAndQuery);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMethodValue() {
/*  81 */     return this.exchange.getRequestMethod().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected MultiValueMap<String, HttpCookie> initCookies() {
/*  87 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/*     */     
/*  89 */     for (String name : this.exchange.getRequestCookies().keySet()) {
/*  90 */       Cookie cookie = (Cookie)this.exchange.getRequestCookies().get(name);
/*  91 */       HttpCookie httpCookie = new HttpCookie(name, cookie.getValue());
/*  92 */       linkedMultiValueMap.add(name, httpCookie);
/*     */     } 
/*  94 */     return (MultiValueMap<String, HttpCookie>)linkedMultiValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public InetSocketAddress getLocalAddress() {
/* 100 */     return this.exchange.getDestinationAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public InetSocketAddress getRemoteAddress() {
/* 106 */     return this.exchange.getSourceAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected SslInfo initSslInfo() {
/* 112 */     SSLSession session = this.exchange.getConnection().getSslSession();
/* 113 */     if (session != null) {
/* 114 */       return new DefaultSslInfo(session);
/*     */     }
/* 116 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Flux<DataBuffer> getBody() {
/* 121 */     return Flux.from(this.body);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeRequest() {
/* 127 */     return (T)this.exchange;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String initId() {
/* 132 */     return ObjectUtils.getIdentityHexString(this.exchange.getConnection()) + "-" + logPrefixIndex
/* 133 */       .incrementAndGet();
/*     */   }
/*     */ 
/*     */   
/*     */   private class RequestBodyPublisher
/*     */     extends AbstractListenerReadPublisher<DataBuffer>
/*     */   {
/*     */     private final StreamSourceChannel channel;
/*     */     
/*     */     private final DataBufferFactory bufferFactory;
/*     */     private final ByteBufferPool byteBufferPool;
/*     */     
/*     */     public RequestBodyPublisher(HttpServerExchange exchange, DataBufferFactory bufferFactory) {
/* 146 */       super(UndertowServerHttpRequest.this.getLogPrefix());
/* 147 */       this.channel = exchange.getRequestChannel();
/* 148 */       this.bufferFactory = bufferFactory;
/* 149 */       this.byteBufferPool = exchange.getConnection().getByteBufferPool();
/*     */     }
/*     */     
/*     */     private void registerListeners(HttpServerExchange exchange) {
/* 153 */       exchange.addExchangeCompleteListener((ex, next) -> {
/*     */             onAllDataRead();
/*     */             next.proceed();
/*     */           });
/* 157 */       this.channel.getReadSetter().set(c -> onDataAvailable());
/* 158 */       this.channel.getCloseSetter().set(c -> onAllDataRead());
/* 159 */       this.channel.resumeReads();
/*     */     }
/*     */ 
/*     */     
/*     */     protected void checkOnDataAvailable() {
/* 164 */       this.channel.resumeReads();
/*     */       
/* 166 */       onDataAvailable();
/*     */     }
/*     */ 
/*     */     
/*     */     protected void readingPaused() {
/* 171 */       this.channel.suspendReads();
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     protected DataBuffer read() throws IOException {
/* 177 */       PooledByteBuffer pooledByteBuffer = this.byteBufferPool.allocate();
/*     */       try {
/* 179 */         ByteBuffer byteBuffer = pooledByteBuffer.getBuffer();
/* 180 */         int read = this.channel.read(byteBuffer);
/*     */         
/* 182 */         if (rsReadLogger.isTraceEnabled()) {
/* 183 */           rsReadLogger.trace(getLogPrefix() + "Read " + read + ((read != -1) ? " bytes" : ""));
/*     */         }
/*     */         
/* 186 */         if (read > 0) {
/* 187 */           byteBuffer.flip();
/* 188 */           DataBuffer dataBuffer = this.bufferFactory.allocateBuffer(read);
/* 189 */           dataBuffer.write(new ByteBuffer[] { byteBuffer });
/* 190 */           return dataBuffer;
/*     */         } 
/* 192 */         if (read == -1) {
/* 193 */           onAllDataRead();
/*     */         }
/* 195 */         return null;
/*     */       } finally {
/*     */         
/* 198 */         pooledByteBuffer.close();
/*     */       } 
/*     */     }
/*     */     
/*     */     protected void discardData() {}
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/UndertowServerHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */