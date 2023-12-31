/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.util.function.Consumer;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.HttpRequest;
/*     */ import org.springframework.http.ReactiveHttpInputMessage;
/*     */ import org.springframework.http.server.RequestPath;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ public interface ServerHttpRequest
/*     */   extends HttpRequest, ReactiveHttpInputMessage
/*     */ {
/*     */   String getId();
/*     */   
/*     */   RequestPath getPath();
/*     */   
/*     */   MultiValueMap<String, String> getQueryParams();
/*     */   
/*     */   MultiValueMap<String, HttpCookie> getCookies();
/*     */   
/*     */   @Nullable
/*     */   default InetSocketAddress getLocalAddress() {
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   default InetSocketAddress getRemoteAddress() {
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   default SslInfo getSslInfo() {
/*  97 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Builder mutate() {
/* 106 */     return new DefaultServerHttpRequestBuilder(this);
/*     */   }
/*     */   
/*     */   public static interface Builder {
/*     */     Builder method(HttpMethod param1HttpMethod);
/*     */     
/*     */     Builder uri(URI param1URI);
/*     */     
/*     */     Builder path(String param1String);
/*     */     
/*     */     Builder contextPath(String param1String);
/*     */     
/*     */     Builder header(String param1String, String... param1VarArgs);
/*     */     
/*     */     Builder headers(Consumer<HttpHeaders> param1Consumer);
/*     */     
/*     */     Builder sslInfo(SslInfo param1SslInfo);
/*     */     
/*     */     Builder remoteAddress(InetSocketAddress param1InetSocketAddress);
/*     */     
/*     */     ServerHttpRequest build();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/ServerHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */