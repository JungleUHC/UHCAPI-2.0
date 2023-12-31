/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.util.function.Supplier;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.ResponseCookie;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class ServerHttpResponseDecorator
/*     */   implements ServerHttpResponse
/*     */ {
/*     */   private final ServerHttpResponse delegate;
/*     */   
/*     */   public ServerHttpResponseDecorator(ServerHttpResponse delegate) {
/*  46 */     Assert.notNull(delegate, "Delegate is required");
/*  47 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpResponse getDelegate() {
/*  52 */     return this.delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setStatusCode(@Nullable HttpStatus status) {
/*  60 */     return getDelegate().setStatusCode(status);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpStatus getStatusCode() {
/*  65 */     return getDelegate().getStatusCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setRawStatusCode(@Nullable Integer value) {
/*  70 */     return getDelegate().setRawStatusCode(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getRawStatusCode() {
/*  75 */     return getDelegate().getRawStatusCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/*  80 */     return getDelegate().getHeaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, ResponseCookie> getCookies() {
/*  85 */     return getDelegate().getCookies();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addCookie(ResponseCookie cookie) {
/*  90 */     getDelegate().addCookie(cookie);
/*     */   }
/*     */ 
/*     */   
/*     */   public DataBufferFactory bufferFactory() {
/*  95 */     return getDelegate().bufferFactory();
/*     */   }
/*     */ 
/*     */   
/*     */   public void beforeCommit(Supplier<? extends Mono<Void>> action) {
/* 100 */     getDelegate().beforeCommit(action);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCommitted() {
/* 105 */     return getDelegate().isCommitted();
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
/* 110 */     return getDelegate().writeWith(body);
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
/* 115 */     return getDelegate().writeAndFlushWith(body);
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> setComplete() {
/* 120 */     return getDelegate().setComplete();
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
/*     */   public static <T> T getNativeResponse(ServerHttpResponse response) {
/* 133 */     if (response instanceof AbstractServerHttpResponse) {
/* 134 */       return ((AbstractServerHttpResponse)response).getNativeResponse();
/*     */     }
/* 136 */     if (response instanceof ServerHttpResponseDecorator) {
/* 137 */       return getNativeResponse(((ServerHttpResponseDecorator)response).getDelegate());
/*     */     }
/*     */     
/* 140 */     throw new IllegalArgumentException("Can't find native response in " + response
/* 141 */         .getClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 148 */     return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/ServerHttpResponseDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */