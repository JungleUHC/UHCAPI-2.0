/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.handler.codec.http.cookie.DefaultCookie;
/*     */ import java.net.URI;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.NettyDataBufferFactory;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.ZeroCopyHttpOutputMessage;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.netty.NettyOutbound;
/*     */ import reactor.netty.http.client.HttpClientRequest;
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
/*     */ class ReactorClientHttpRequest
/*     */   extends AbstractClientHttpRequest
/*     */   implements ZeroCopyHttpOutputMessage
/*     */ {
/*     */   private final HttpMethod httpMethod;
/*     */   private final URI uri;
/*     */   private final HttpClientRequest request;
/*     */   private final NettyOutbound outbound;
/*     */   private final NettyDataBufferFactory bufferFactory;
/*     */   
/*     */   public ReactorClientHttpRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound outbound) {
/*  60 */     this.httpMethod = method;
/*  61 */     this.uri = uri;
/*  62 */     this.request = request;
/*  63 */     this.outbound = outbound;
/*  64 */     this.bufferFactory = new NettyDataBufferFactory(outbound.alloc());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpMethod getMethod() {
/*  70 */     return this.httpMethod;
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*  75 */     return this.uri;
/*     */   }
/*     */ 
/*     */   
/*     */   public DataBufferFactory bufferFactory() {
/*  80 */     return (DataBufferFactory)this.bufferFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeRequest() {
/*  86 */     return (T)this.request;
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
/*  91 */     return doCommit(() -> {
/*     */           if (body instanceof Mono) {
/*     */             Mono<ByteBuf> byteBufMono = Mono.from(body).map(NettyDataBufferFactory::toByteBuf);
/*     */             return (Publisher)this.outbound.send((Publisher)byteBufMono).then();
/*     */           } 
/*     */           Flux<ByteBuf> byteBufFlux = Flux.from(body).map(NettyDataBufferFactory::toByteBuf);
/*     */           return (Publisher)this.outbound.send((Publisher)byteBufFlux).then();
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
/* 107 */     Flux flux = Flux.from(body).map(ReactorClientHttpRequest::toByteBufs);
/* 108 */     return doCommit(() -> this.outbound.sendGroups(byteBufs).then());
/*     */   }
/*     */   
/*     */   private static Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
/* 112 */     return (Publisher<ByteBuf>)Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeWith(Path file, long position, long count) {
/* 117 */     return doCommit(() -> this.outbound.sendFile(file, position, count).then());
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> setComplete() {
/* 122 */     return doCommit(this.outbound::then);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyHeaders() {
/* 127 */     getHeaders().forEach((key, value) -> this.request.requestHeaders().set(key, value));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyCookies() {
/* 132 */     getCookies().values().stream().flatMap(Collection::stream)
/* 133 */       .map(cookie -> new DefaultCookie(cookie.getName(), cookie.getValue()))
/* 134 */       .forEach(this.request::addCookie);
/*     */   }
/*     */ 
/*     */   
/*     */   protected HttpHeaders initReadOnlyHeaders() {
/* 139 */     return HttpHeaders.readOnlyHttpHeaders(new NettyHeadersAdapter(this.request.requestHeaders()));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/ReactorClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */