/*     */ package org.springframework.web.cors.reactive;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.web.cors.CorsConfiguration;
/*     */ import org.springframework.web.server.ServerWebExchange;
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
/*     */ public class DefaultCorsProcessor
/*     */   implements CorsProcessor
/*     */ {
/*  51 */   private static final Log logger = LogFactory.getLog(DefaultCorsProcessor.class);
/*     */   
/*  53 */   private static final List<String> VARY_HEADERS = Arrays.asList(new String[] { "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean process(@Nullable CorsConfiguration config, ServerWebExchange exchange) {
/*  60 */     ServerHttpRequest request = exchange.getRequest();
/*  61 */     ServerHttpResponse response = exchange.getResponse();
/*  62 */     HttpHeaders responseHeaders = response.getHeaders();
/*     */     
/*  64 */     List<String> varyHeaders = responseHeaders.get("Vary");
/*  65 */     if (varyHeaders == null) {
/*  66 */       responseHeaders.addAll("Vary", VARY_HEADERS);
/*     */     } else {
/*     */       
/*  69 */       for (String header : VARY_HEADERS) {
/*  70 */         if (!varyHeaders.contains(header)) {
/*  71 */           responseHeaders.add("Vary", header);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  76 */     if (!CorsUtils.isCorsRequest(request)) {
/*  77 */       return true;
/*     */     }
/*     */     
/*  80 */     if (responseHeaders.getFirst("Access-Control-Allow-Origin") != null) {
/*  81 */       logger.trace("Skip: response already contains \"Access-Control-Allow-Origin\"");
/*  82 */       return true;
/*     */     } 
/*     */     
/*  85 */     boolean preFlightRequest = CorsUtils.isPreFlightRequest(request);
/*  86 */     if (config == null) {
/*  87 */       if (preFlightRequest) {
/*  88 */         rejectRequest(response);
/*  89 */         return false;
/*     */       } 
/*     */       
/*  92 */       return true;
/*     */     } 
/*     */ 
/*     */     
/*  96 */     return handleInternal(exchange, config, preFlightRequest);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void rejectRequest(ServerHttpResponse response) {
/* 103 */     response.setStatusCode(HttpStatus.FORBIDDEN);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean handleInternal(ServerWebExchange exchange, CorsConfiguration config, boolean preFlightRequest) {
/* 112 */     ServerHttpRequest request = exchange.getRequest();
/* 113 */     ServerHttpResponse response = exchange.getResponse();
/* 114 */     HttpHeaders responseHeaders = response.getHeaders();
/*     */     
/* 116 */     String requestOrigin = request.getHeaders().getOrigin();
/* 117 */     String allowOrigin = checkOrigin(config, requestOrigin);
/* 118 */     if (allowOrigin == null) {
/* 119 */       logger.debug("Reject: '" + requestOrigin + "' origin is not allowed");
/* 120 */       rejectRequest(response);
/* 121 */       return false;
/*     */     } 
/*     */     
/* 124 */     HttpMethod requestMethod = getMethodToUse(request, preFlightRequest);
/* 125 */     List<HttpMethod> allowMethods = checkMethods(config, requestMethod);
/* 126 */     if (allowMethods == null) {
/* 127 */       logger.debug("Reject: HTTP '" + requestMethod + "' is not allowed");
/* 128 */       rejectRequest(response);
/* 129 */       return false;
/*     */     } 
/*     */     
/* 132 */     List<String> requestHeaders = getHeadersToUse(request, preFlightRequest);
/* 133 */     List<String> allowHeaders = checkHeaders(config, requestHeaders);
/* 134 */     if (preFlightRequest && allowHeaders == null) {
/* 135 */       logger.debug("Reject: headers '" + requestHeaders + "' are not allowed");
/* 136 */       rejectRequest(response);
/* 137 */       return false;
/*     */     } 
/*     */     
/* 140 */     responseHeaders.setAccessControlAllowOrigin(allowOrigin);
/*     */     
/* 142 */     if (preFlightRequest) {
/* 143 */       responseHeaders.setAccessControlAllowMethods(allowMethods);
/*     */     }
/*     */     
/* 146 */     if (preFlightRequest && !allowHeaders.isEmpty()) {
/* 147 */       responseHeaders.setAccessControlAllowHeaders(allowHeaders);
/*     */     }
/*     */     
/* 150 */     if (!CollectionUtils.isEmpty(config.getExposedHeaders())) {
/* 151 */       responseHeaders.setAccessControlExposeHeaders(config.getExposedHeaders());
/*     */     }
/*     */     
/* 154 */     if (Boolean.TRUE.equals(config.getAllowCredentials())) {
/* 155 */       responseHeaders.setAccessControlAllowCredentials(true);
/*     */     }
/*     */     
/* 158 */     if (preFlightRequest && config.getMaxAge() != null) {
/* 159 */       responseHeaders.setAccessControlMaxAge(config.getMaxAge().longValue());
/*     */     }
/*     */     
/* 162 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String checkOrigin(CorsConfiguration config, @Nullable String requestOrigin) {
/* 172 */     return config.checkOrigin(requestOrigin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected List<HttpMethod> checkMethods(CorsConfiguration config, @Nullable HttpMethod requestMethod) {
/* 182 */     return config.checkHttpMethod(requestMethod);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private HttpMethod getMethodToUse(ServerHttpRequest request, boolean isPreFlight) {
/* 187 */     return isPreFlight ? request.getHeaders().getAccessControlRequestMethod() : request.getMethod();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected List<String> checkHeaders(CorsConfiguration config, List<String> requestHeaders) {
/* 198 */     return config.checkHeaders(requestHeaders);
/*     */   }
/*     */   
/*     */   private List<String> getHeadersToUse(ServerHttpRequest request, boolean isPreFlight) {
/* 202 */     HttpHeaders headers = request.getHeaders();
/* 203 */     return isPreFlight ? headers.getAccessControlRequestHeaders() : new ArrayList<>(headers.keySet());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/reactive/DefaultCorsProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */