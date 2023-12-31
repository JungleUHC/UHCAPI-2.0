/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
/*     */ import org.apache.hc.client5.http.cookie.Cookie;
/*     */ import org.apache.hc.client5.http.cookie.CookieStore;
/*     */ import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
/*     */ import org.apache.hc.client5.http.protocol.HttpClientContext;
/*     */ import org.apache.hc.core5.http.ContentType;
/*     */ import org.apache.hc.core5.http.HttpMessage;
/*     */ import org.apache.hc.core5.http.HttpRequest;
/*     */ import org.apache.hc.core5.http.message.BasicHttpRequest;
/*     */ import org.apache.hc.core5.http.nio.AsyncEntityProducer;
/*     */ import org.apache.hc.core5.http.nio.AsyncRequestProducer;
/*     */ import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
/*     */ import org.apache.hc.core5.reactive.ReactiveEntityProducer;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ class HttpComponentsClientHttpRequest
/*     */   extends AbstractClientHttpRequest
/*     */ {
/*     */   private final HttpRequest httpRequest;
/*     */   private final DataBufferFactory dataBufferFactory;
/*     */   private final HttpClientContext context;
/*     */   @Nullable
/*     */   private Flux<ByteBuffer> byteBufferFlux;
/*  65 */   private transient long contentLength = -1L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpComponentsClientHttpRequest(HttpMethod method, URI uri, HttpClientContext context, DataBufferFactory dataBufferFactory) {
/*  71 */     this.context = context;
/*  72 */     this.httpRequest = (HttpRequest)new BasicHttpRequest(method.name(), uri);
/*  73 */     this.dataBufferFactory = dataBufferFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpMethod getMethod() {
/*  79 */     HttpMethod method = HttpMethod.resolve(this.httpRequest.getMethod());
/*  80 */     Assert.state((method != null), "Method must not be null");
/*  81 */     return method;
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*     */     try {
/*  87 */       return this.httpRequest.getUri();
/*     */     }
/*  89 */     catch (URISyntaxException ex) {
/*  90 */       throw new IllegalArgumentException("Invalid URI syntax: " + ex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public DataBufferFactory bufferFactory() {
/*  96 */     return this.dataBufferFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeRequest() {
/* 102 */     return (T)this.httpRequest;
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
/* 107 */     return doCommit(() -> {
/*     */           this.byteBufferFlux = Flux.from(body).map(DataBuffer::asByteBuffer);
/*     */           return (Publisher)Mono.empty();
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
/* 115 */     return writeWith((Publisher<? extends DataBuffer>)Flux.from(body).flatMap(Function.identity()));
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> setComplete() {
/* 120 */     return doCommit();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyHeaders() {
/* 125 */     HttpHeaders headers = getHeaders();
/*     */     
/* 127 */     headers.entrySet()
/* 128 */       .stream()
/* 129 */       .filter(entry -> !"Content-Length".equals(entry.getKey()))
/* 130 */       .forEach(entry -> ((List)entry.getValue()).forEach(()));
/*     */     
/* 132 */     if (!this.httpRequest.containsHeader("Accept")) {
/* 133 */       this.httpRequest.addHeader("Accept", "*/*");
/*     */     }
/*     */     
/* 136 */     this.contentLength = headers.getContentLength();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyCookies() {
/* 141 */     if (getCookies().isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/* 145 */     CookieStore cookieStore = this.context.getCookieStore();
/*     */     
/* 147 */     getCookies().values()
/* 148 */       .stream()
/* 149 */       .flatMap(Collection::stream)
/* 150 */       .forEach(cookie -> {
/*     */           BasicClientCookie clientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
/*     */           clientCookie.setDomain(getURI().getHost());
/*     */           clientCookie.setPath(getURI().getPath());
/*     */           cookieStore.addCookie((Cookie)clientCookie);
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected HttpHeaders initReadOnlyHeaders() {
/* 160 */     return HttpHeaders.readOnlyHttpHeaders(new HttpComponentsHeadersAdapter((HttpMessage)this.httpRequest));
/*     */   }
/*     */   
/*     */   public AsyncRequestProducer toRequestProducer() {
/* 164 */     ReactiveEntityProducer reactiveEntityProducer = null;
/*     */     
/* 166 */     if (this.byteBufferFlux != null) {
/* 167 */       String contentEncoding = getHeaders().getFirst("Content-Encoding");
/* 168 */       ContentType contentType = null;
/* 169 */       if (getHeaders().getContentType() != null) {
/* 170 */         contentType = ContentType.parse(getHeaders().getContentType().toString());
/*     */       }
/* 172 */       reactiveEntityProducer = new ReactiveEntityProducer((Publisher)this.byteBufferFlux, this.contentLength, contentType, contentEncoding);
/*     */     } 
/*     */ 
/*     */     
/* 176 */     return (AsyncRequestProducer)new BasicRequestProducer(this.httpRequest, (AsyncEntityProducer)reactiveEntityProducer);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/HttpComponentsClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */