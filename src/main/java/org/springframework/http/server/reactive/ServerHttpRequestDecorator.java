/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.server.RequestPath;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ 
/*     */ public class ServerHttpRequestDecorator
/*     */   implements ServerHttpRequest
/*     */ {
/*     */   private final ServerHttpRequest delegate;
/*     */   
/*     */   public ServerHttpRequestDecorator(ServerHttpRequest delegate) {
/*  46 */     Assert.notNull(delegate, "Delegate is required");
/*  47 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpRequest getDelegate() {
/*  52 */     return this.delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getId() {
/*  60 */     return getDelegate().getId();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public HttpMethod getMethod() {
/*  66 */     return getDelegate().getMethod();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMethodValue() {
/*  71 */     return getDelegate().getMethodValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*  76 */     return getDelegate().getURI();
/*     */   }
/*     */ 
/*     */   
/*     */   public RequestPath getPath() {
/*  81 */     return getDelegate().getPath();
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, String> getQueryParams() {
/*  86 */     return getDelegate().getQueryParams();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/*  91 */     return getDelegate().getHeaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, HttpCookie> getCookies() {
/*  96 */     return getDelegate().getCookies();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public InetSocketAddress getLocalAddress() {
/* 102 */     return getDelegate().getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public InetSocketAddress getRemoteAddress() {
/* 108 */     return getDelegate().getRemoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SslInfo getSslInfo() {
/* 114 */     return getDelegate().getSslInfo();
/*     */   }
/*     */ 
/*     */   
/*     */   public Flux<DataBuffer> getBody() {
/* 119 */     return getDelegate().getBody();
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
/*     */   public static <T> T getNativeRequest(ServerHttpRequest request) {
/* 132 */     if (request instanceof AbstractServerHttpRequest) {
/* 133 */       return ((AbstractServerHttpRequest)request).getNativeRequest();
/*     */     }
/* 135 */     if (request instanceof ServerHttpRequestDecorator) {
/* 136 */       return getNativeRequest(((ServerHttpRequestDecorator)request).getDelegate());
/*     */     }
/*     */     
/* 139 */     throw new IllegalArgumentException("Can't find native request in " + request
/* 140 */         .getClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 147 */     return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/ServerHttpRequestDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */