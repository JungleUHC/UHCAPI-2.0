/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.util.function.Supplier;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ 
/*     */ public class ClientHttpRequestDecorator
/*     */   implements ClientHttpRequest
/*     */ {
/*     */   private final ClientHttpRequest delegate;
/*     */   
/*     */   public ClientHttpRequestDecorator(ClientHttpRequest delegate) {
/*  46 */     Assert.notNull(delegate, "Delegate is required");
/*  47 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientHttpRequest getDelegate() {
/*  52 */     return this.delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpMethod getMethod() {
/*  60 */     return this.delegate.getMethod();
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*  65 */     return this.delegate.getURI();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/*  70 */     return this.delegate.getHeaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, HttpCookie> getCookies() {
/*  75 */     return this.delegate.getCookies();
/*     */   }
/*     */ 
/*     */   
/*     */   public DataBufferFactory bufferFactory() {
/*  80 */     return this.delegate.bufferFactory();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getNativeRequest() {
/*  85 */     return this.delegate.getNativeRequest();
/*     */   }
/*     */ 
/*     */   
/*     */   public void beforeCommit(Supplier<? extends Mono<Void>> action) {
/*  90 */     this.delegate.beforeCommit(action);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCommitted() {
/*  95 */     return this.delegate.isCommitted();
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
/* 100 */     return this.delegate.writeWith(body);
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
/* 105 */     return this.delegate.writeAndFlushWith(body);
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> setComplete() {
/* 110 */     return this.delegate.setComplete();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 116 */     return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/ClientHttpRequestDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */