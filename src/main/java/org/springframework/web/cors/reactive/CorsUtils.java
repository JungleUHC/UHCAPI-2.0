/*     */ package org.springframework.web.cors.reactive;
/*     */ 
/*     */ import java.net.URI;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.web.util.UriComponents;
/*     */ import org.springframework.web.util.UriComponentsBuilder;
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
/*     */ public abstract class CorsUtils
/*     */ {
/*     */   public static boolean isCorsRequest(ServerHttpRequest request) {
/*  44 */     return (request.getHeaders().containsKey("Origin") && !isSameOrigin(request));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isPreFlightRequest(ServerHttpRequest request) {
/*  52 */     HttpHeaders headers = request.getHeaders();
/*  53 */     return (request.getMethod() == HttpMethod.OPTIONS && headers
/*  54 */       .containsKey("Origin") && headers
/*  55 */       .containsKey("Access-Control-Request-Method"));
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
/*     */   @Deprecated
/*     */   public static boolean isSameOrigin(ServerHttpRequest request) {
/*  72 */     String origin = request.getHeaders().getOrigin();
/*  73 */     if (origin == null) {
/*  74 */       return true;
/*     */     }
/*     */     
/*  77 */     URI uri = request.getURI();
/*  78 */     String actualScheme = uri.getScheme();
/*  79 */     String actualHost = uri.getHost();
/*  80 */     int actualPort = getPort(uri.getScheme(), uri.getPort());
/*  81 */     Assert.notNull(actualScheme, "Actual request scheme must not be null");
/*  82 */     Assert.notNull(actualHost, "Actual request host must not be null");
/*  83 */     Assert.isTrue((actualPort != -1), "Actual request port must not be undefined");
/*     */     
/*  85 */     UriComponents originUrl = UriComponentsBuilder.fromOriginHeader(origin).build();
/*  86 */     return (actualScheme.equals(originUrl.getScheme()) && actualHost
/*  87 */       .equals(originUrl.getHost()) && actualPort == 
/*  88 */       getPort(originUrl.getScheme(), originUrl.getPort()));
/*     */   }
/*     */   
/*     */   private static int getPort(@Nullable String scheme, int port) {
/*  92 */     if (port == -1) {
/*  93 */       if ("http".equals(scheme) || "ws".equals(scheme)) {
/*  94 */         port = 80;
/*     */       }
/*  96 */       else if ("https".equals(scheme) || "wss".equals(scheme)) {
/*  97 */         port = 443;
/*     */       } 
/*     */     }
/* 100 */     return port;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/reactive/CorsUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */