/*    */ package org.springframework.web.cors;
/*    */ 
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.springframework.http.HttpMethod;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.ObjectUtils;
/*    */ import org.springframework.web.util.UriComponents;
/*    */ import org.springframework.web.util.UriComponentsBuilder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class CorsUtils
/*    */ {
/*    */   public static boolean isCorsRequest(HttpServletRequest request) {
/* 42 */     String origin = request.getHeader("Origin");
/* 43 */     if (origin == null) {
/* 44 */       return false;
/*    */     }
/* 46 */     UriComponents originUrl = UriComponentsBuilder.fromOriginHeader(origin).build();
/* 47 */     String scheme = request.getScheme();
/* 48 */     String host = request.getServerName();
/* 49 */     int port = request.getServerPort();
/* 50 */     return (!ObjectUtils.nullSafeEquals(scheme, originUrl.getScheme()) || 
/* 51 */       !ObjectUtils.nullSafeEquals(host, originUrl.getHost()) || 
/* 52 */       getPort(scheme, port) != getPort(originUrl.getScheme(), originUrl.getPort()));
/*    */   }
/*    */ 
/*    */   
/*    */   private static int getPort(@Nullable String scheme, int port) {
/* 57 */     if (port == -1) {
/* 58 */       if ("http".equals(scheme) || "ws".equals(scheme)) {
/* 59 */         port = 80;
/*    */       }
/* 61 */       else if ("https".equals(scheme) || "wss".equals(scheme)) {
/* 62 */         port = 443;
/*    */       } 
/*    */     }
/* 65 */     return port;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isPreFlightRequest(HttpServletRequest request) {
/* 73 */     return (HttpMethod.OPTIONS.matches(request.getMethod()) && request
/* 74 */       .getHeader("Origin") != null && request
/* 75 */       .getHeader("Access-Control-Request-Method") != null);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/CorsUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */