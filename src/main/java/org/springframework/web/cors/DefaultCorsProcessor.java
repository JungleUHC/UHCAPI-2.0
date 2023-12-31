/*     */ package org.springframework.web.cors;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.server.ServerHttpRequest;
/*     */ import org.springframework.http.server.ServerHttpResponse;
/*     */ import org.springframework.http.server.ServletServerHttpRequest;
/*     */ import org.springframework.http.server.ServletServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
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
/*     */ public class DefaultCorsProcessor
/*     */   implements CorsProcessor
/*     */ {
/*  56 */   private static final Log logger = LogFactory.getLog(DefaultCorsProcessor.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean processRequest(@Nullable CorsConfiguration config, HttpServletRequest request, HttpServletResponse response) throws IOException {
/*  64 */     Collection<String> varyHeaders = response.getHeaders("Vary");
/*  65 */     if (!varyHeaders.contains("Origin")) {
/*  66 */       response.addHeader("Vary", "Origin");
/*     */     }
/*  68 */     if (!varyHeaders.contains("Access-Control-Request-Method")) {
/*  69 */       response.addHeader("Vary", "Access-Control-Request-Method");
/*     */     }
/*  71 */     if (!varyHeaders.contains("Access-Control-Request-Headers")) {
/*  72 */       response.addHeader("Vary", "Access-Control-Request-Headers");
/*     */     }
/*     */     
/*  75 */     if (!CorsUtils.isCorsRequest(request)) {
/*  76 */       return true;
/*     */     }
/*     */     
/*  79 */     if (response.getHeader("Access-Control-Allow-Origin") != null) {
/*  80 */       logger.trace("Skip: response already contains \"Access-Control-Allow-Origin\"");
/*  81 */       return true;
/*     */     } 
/*     */     
/*  84 */     boolean preFlightRequest = CorsUtils.isPreFlightRequest(request);
/*  85 */     if (config == null) {
/*  86 */       if (preFlightRequest) {
/*  87 */         rejectRequest((ServerHttpResponse)new ServletServerHttpResponse(response));
/*  88 */         return false;
/*     */       } 
/*     */       
/*  91 */       return true;
/*     */     } 
/*     */ 
/*     */     
/*  95 */     return handleInternal((ServerHttpRequest)new ServletServerHttpRequest(request), (ServerHttpResponse)new ServletServerHttpResponse(response), config, preFlightRequest);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void rejectRequest(ServerHttpResponse response) throws IOException {
/* 104 */     response.setStatusCode(HttpStatus.FORBIDDEN);
/* 105 */     response.getBody().write("Invalid CORS request".getBytes(StandardCharsets.UTF_8));
/* 106 */     response.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean handleInternal(ServerHttpRequest request, ServerHttpResponse response, CorsConfiguration config, boolean preFlightRequest) throws IOException {
/* 115 */     String requestOrigin = request.getHeaders().getOrigin();
/* 116 */     String allowOrigin = checkOrigin(config, requestOrigin);
/* 117 */     HttpHeaders responseHeaders = response.getHeaders();
/*     */     
/* 119 */     if (allowOrigin == null) {
/* 120 */       logger.debug("Reject: '" + requestOrigin + "' origin is not allowed");
/* 121 */       rejectRequest(response);
/* 122 */       return false;
/*     */     } 
/*     */     
/* 125 */     HttpMethod requestMethod = getMethodToUse(request, preFlightRequest);
/* 126 */     List<HttpMethod> allowMethods = checkMethods(config, requestMethod);
/* 127 */     if (allowMethods == null) {
/* 128 */       logger.debug("Reject: HTTP '" + requestMethod + "' is not allowed");
/* 129 */       rejectRequest(response);
/* 130 */       return false;
/*     */     } 
/*     */     
/* 133 */     List<String> requestHeaders = getHeadersToUse(request, preFlightRequest);
/* 134 */     List<String> allowHeaders = checkHeaders(config, requestHeaders);
/* 135 */     if (preFlightRequest && allowHeaders == null) {
/* 136 */       logger.debug("Reject: headers '" + requestHeaders + "' are not allowed");
/* 137 */       rejectRequest(response);
/* 138 */       return false;
/*     */     } 
/*     */     
/* 141 */     responseHeaders.setAccessControlAllowOrigin(allowOrigin);
/*     */     
/* 143 */     if (preFlightRequest) {
/* 144 */       responseHeaders.setAccessControlAllowMethods(allowMethods);
/*     */     }
/*     */     
/* 147 */     if (preFlightRequest && !allowHeaders.isEmpty()) {
/* 148 */       responseHeaders.setAccessControlAllowHeaders(allowHeaders);
/*     */     }
/*     */     
/* 151 */     if (!CollectionUtils.isEmpty(config.getExposedHeaders())) {
/* 152 */       responseHeaders.setAccessControlExposeHeaders(config.getExposedHeaders());
/*     */     }
/*     */     
/* 155 */     if (Boolean.TRUE.equals(config.getAllowCredentials())) {
/* 156 */       responseHeaders.setAccessControlAllowCredentials(true);
/*     */     }
/*     */     
/* 159 */     if (preFlightRequest && config.getMaxAge() != null) {
/* 160 */       responseHeaders.setAccessControlMaxAge(config.getMaxAge().longValue());
/*     */     }
/*     */     
/* 163 */     response.flush();
/* 164 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String checkOrigin(CorsConfiguration config, @Nullable String requestOrigin) {
/* 174 */     return config.checkOrigin(requestOrigin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected List<HttpMethod> checkMethods(CorsConfiguration config, @Nullable HttpMethod requestMethod) {
/* 184 */     return config.checkHttpMethod(requestMethod);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private HttpMethod getMethodToUse(ServerHttpRequest request, boolean isPreFlight) {
/* 189 */     return isPreFlight ? request.getHeaders().getAccessControlRequestMethod() : request.getMethod();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected List<String> checkHeaders(CorsConfiguration config, List<String> requestHeaders) {
/* 199 */     return config.checkHeaders(requestHeaders);
/*     */   }
/*     */   
/*     */   private List<String> getHeadersToUse(ServerHttpRequest request, boolean isPreFlight) {
/* 203 */     HttpHeaders headers = request.getHeaders();
/* 204 */     return isPreFlight ? headers.getAccessControlRequestHeaders() : new ArrayList<>(headers.keySet());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/DefaultCorsProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */