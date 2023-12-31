/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.net.HttpCookie;
/*     */ import java.net.URI;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.function.Function;
/*     */ import org.eclipse.jetty.client.api.Request;
/*     */ import org.eclipse.jetty.reactive.client.ContentChunk;
/*     */ import org.eclipse.jetty.reactive.client.ReactiveRequest;
/*     */ import org.eclipse.jetty.util.Callback;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.io.buffer.PooledDataBuffer;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.MediaType;
/*     */ import reactor.core.publisher.Flux;
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
/*     */ class JettyClientHttpRequest
/*     */   extends AbstractClientHttpRequest
/*     */ {
/*     */   private final Request jettyRequest;
/*     */   private final DataBufferFactory bufferFactory;
/*     */   private final ReactiveRequest.Builder builder;
/*     */   
/*     */   public JettyClientHttpRequest(Request jettyRequest, DataBufferFactory bufferFactory) {
/*  59 */     this.jettyRequest = jettyRequest;
/*  60 */     this.bufferFactory = bufferFactory;
/*  61 */     this.builder = ReactiveRequest.newBuilder(this.jettyRequest).abortOnCancel(true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpMethod getMethod() {
/*  67 */     return HttpMethod.valueOf(this.jettyRequest.getMethod());
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*  72 */     return this.jettyRequest.getURI();
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> setComplete() {
/*  77 */     return doCommit();
/*     */   }
/*     */ 
/*     */   
/*     */   public DataBufferFactory bufferFactory() {
/*  82 */     return this.bufferFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeRequest() {
/*  88 */     return (T)this.jettyRequest;
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
/*  93 */     return Mono.create(sink -> {
/*     */           ReactiveRequest.Content content = (ReactiveRequest.Content)Flux.from(body).map(()).as(());
/*     */ 
/*     */           
/*     */           this.builder.content(content);
/*     */           
/*     */           sink.success();
/* 100 */         }).then(doCommit());
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
/* 105 */     return writeWith((Publisher<? extends DataBuffer>)Flux.from(body)
/* 106 */         .flatMap(Function.identity())
/* 107 */         .doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release));
/*     */   }
/*     */   
/*     */   private String getContentType() {
/* 111 */     MediaType contentType = getHeaders().getContentType();
/* 112 */     return (contentType != null) ? contentType.toString() : "application/octet-stream";
/*     */   }
/*     */   
/*     */   private ContentChunk toContentChunk(final DataBuffer buffer, final MonoSink<Void> sink) {
/* 116 */     return new ContentChunk(buffer.asByteBuffer(), new Callback()
/*     */         {
/*     */           public void succeeded() {
/* 119 */             DataBufferUtils.release(buffer);
/*     */           }
/*     */           
/*     */           public void failed(Throwable t) {
/* 123 */             DataBufferUtils.release(buffer);
/* 124 */             sink.error(t);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyCookies() {
/* 131 */     getCookies().values().stream().flatMap(Collection::stream)
/* 132 */       .map(cookie -> new HttpCookie(cookie.getName(), cookie.getValue()))
/* 133 */       .forEach(this.jettyRequest::cookie);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyHeaders() {
/* 138 */     HttpHeaders headers = getHeaders();
/* 139 */     headers.forEach((key, value) -> value.forEach(()));
/* 140 */     if (!headers.containsKey("Accept")) {
/* 141 */       this.jettyRequest.header("Accept", "*/*");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected HttpHeaders initReadOnlyHeaders() {
/* 147 */     return HttpHeaders.readOnlyHttpHeaders(new JettyHeadersAdapter(this.jettyRequest.getHeaders()));
/*     */   }
/*     */   
/*     */   public ReactiveRequest toReactiveRequest() {
/* 151 */     return this.builder.build();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/JettyClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */