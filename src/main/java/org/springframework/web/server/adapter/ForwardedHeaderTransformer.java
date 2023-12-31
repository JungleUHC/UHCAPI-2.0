/*     */ package org.springframework.web.server.adapter;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.util.Collections;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.LinkedCaseInsensitiveMap;
/*     */ import org.springframework.util.StringUtils;
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
/*     */ public class ForwardedHeaderTransformer
/*     */   implements Function<ServerHttpRequest, ServerHttpRequest>
/*     */ {
/*  60 */   static final Set<String> FORWARDED_HEADER_NAMES = Collections.newSetFromMap((Map<String, Boolean>)new LinkedCaseInsensitiveMap(10, Locale.ENGLISH));
/*     */   
/*     */   static {
/*  63 */     FORWARDED_HEADER_NAMES.add("Forwarded");
/*  64 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
/*  65 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
/*  66 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
/*  67 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
/*  68 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Ssl");
/*  69 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-For");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean removeOnly;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRemoveOnly(boolean removeOnly) {
/*  82 */     this.removeOnly = removeOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRemoveOnly() {
/*  90 */     return this.removeOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerHttpRequest apply(ServerHttpRequest request) {
/* 100 */     if (hasForwardedHeaders(request)) {
/* 101 */       ServerHttpRequest.Builder builder = request.mutate();
/* 102 */       if (!this.removeOnly) {
/* 103 */         URI uri = UriComponentsBuilder.fromHttpRequest((HttpRequest)request).build(true).toUri();
/* 104 */         builder.uri(uri);
/* 105 */         String prefix = getForwardedPrefix(request);
/* 106 */         if (prefix != null) {
/* 107 */           builder.path(prefix + uri.getRawPath());
/* 108 */           builder.contextPath(prefix);
/*     */         } 
/* 110 */         InetSocketAddress remoteAddress = request.getRemoteAddress();
/* 111 */         remoteAddress = UriComponentsBuilder.parseForwardedFor((HttpRequest)request, remoteAddress);
/* 112 */         if (remoteAddress != null) {
/* 113 */           builder.remoteAddress(remoteAddress);
/*     */         }
/*     */       } 
/* 116 */       removeForwardedHeaders(builder);
/* 117 */       request = builder.build();
/*     */     } 
/* 119 */     return request;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean hasForwardedHeaders(ServerHttpRequest request) {
/* 127 */     HttpHeaders headers = request.getHeaders();
/* 128 */     for (String headerName : FORWARDED_HEADER_NAMES) {
/* 129 */       if (headers.containsKey(headerName)) {
/* 130 */         return true;
/*     */       }
/*     */     } 
/* 133 */     return false;
/*     */   }
/*     */   
/*     */   private void removeForwardedHeaders(ServerHttpRequest.Builder builder) {
/* 137 */     builder.headers(map -> FORWARDED_HEADER_NAMES.forEach(map::remove));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static String getForwardedPrefix(ServerHttpRequest request) {
/* 143 */     HttpHeaders headers = request.getHeaders();
/* 144 */     String header = headers.getFirst("X-Forwarded-Prefix");
/* 145 */     if (header == null) {
/* 146 */       return null;
/*     */     }
/* 148 */     StringBuilder prefix = new StringBuilder(header.length());
/* 149 */     String[] rawPrefixes = StringUtils.tokenizeToStringArray(header, ",");
/* 150 */     for (String rawPrefix : rawPrefixes) {
/* 151 */       int endIndex = rawPrefix.length();
/* 152 */       while (endIndex > 1 && rawPrefix.charAt(endIndex - 1) == '/') {
/* 153 */         endIndex--;
/*     */       }
/* 155 */       prefix.append((endIndex != rawPrefix.length()) ? rawPrefix.substring(0, endIndex) : rawPrefix);
/*     */     } 
/* 157 */     return prefix.toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/adapter/ForwardedHeaderTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */