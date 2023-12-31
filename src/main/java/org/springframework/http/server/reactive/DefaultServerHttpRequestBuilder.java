/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.Arrays;
/*     */ import java.util.function.Consumer;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.StringUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class DefaultServerHttpRequestBuilder
/*     */   implements ServerHttpRequest.Builder
/*     */ {
/*     */   private URI uri;
/*     */   private HttpHeaders headers;
/*     */   private String httpMethodValue;
/*     */   @Nullable
/*     */   private String uriPath;
/*     */   @Nullable
/*     */   private String contextPath;
/*     */   @Nullable
/*     */   private SslInfo sslInfo;
/*     */   @Nullable
/*     */   private InetSocketAddress remoteAddress;
/*     */   private Flux<DataBuffer> body;
/*     */   private final ServerHttpRequest originalRequest;
/*     */   
/*     */   public DefaultServerHttpRequestBuilder(ServerHttpRequest original) {
/*  70 */     Assert.notNull(original, "ServerHttpRequest is required");
/*     */     
/*  72 */     this.uri = original.getURI();
/*  73 */     this.headers = HttpHeaders.writableHttpHeaders(original.getHeaders());
/*  74 */     this.httpMethodValue = original.getMethodValue();
/*  75 */     this.contextPath = original.getPath().contextPath().value();
/*  76 */     this.remoteAddress = original.getRemoteAddress();
/*  77 */     this.body = original.getBody();
/*  78 */     this.originalRequest = original;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerHttpRequest.Builder method(HttpMethod httpMethod) {
/*  84 */     this.httpMethodValue = httpMethod.name();
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpRequest.Builder uri(URI uri) {
/*  90 */     this.uri = uri;
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpRequest.Builder path(String path) {
/*  96 */     Assert.isTrue(path.startsWith("/"), "The path does not have a leading slash.");
/*  97 */     this.uriPath = path;
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpRequest.Builder contextPath(String contextPath) {
/* 103 */     this.contextPath = contextPath;
/* 104 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpRequest.Builder header(String headerName, String... headerValues) {
/* 109 */     this.headers.put(headerName, Arrays.asList(headerValues));
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpRequest.Builder headers(Consumer<HttpHeaders> headersConsumer) {
/* 115 */     Assert.notNull(headersConsumer, "'headersConsumer' must not be null");
/* 116 */     headersConsumer.accept(this.headers);
/* 117 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpRequest.Builder sslInfo(SslInfo sslInfo) {
/* 122 */     this.sslInfo = sslInfo;
/* 123 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpRequest.Builder remoteAddress(InetSocketAddress remoteAddress) {
/* 128 */     this.remoteAddress = remoteAddress;
/* 129 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpRequest build() {
/* 134 */     return new MutatedServerHttpRequest(getUriToUse(), this.contextPath, this.httpMethodValue, this.sslInfo, this.remoteAddress, this.headers, this.body, this.originalRequest);
/*     */   }
/*     */ 
/*     */   
/*     */   private URI getUriToUse() {
/* 139 */     if (this.uriPath == null) {
/* 140 */       return this.uri;
/*     */     }
/*     */     
/* 143 */     StringBuilder uriBuilder = new StringBuilder();
/* 144 */     if (this.uri.getScheme() != null) {
/* 145 */       uriBuilder.append(this.uri.getScheme()).append(':');
/*     */     }
/* 147 */     if (this.uri.getRawUserInfo() != null || this.uri.getHost() != null) {
/* 148 */       uriBuilder.append("//");
/* 149 */       if (this.uri.getRawUserInfo() != null) {
/* 150 */         uriBuilder.append(this.uri.getRawUserInfo()).append('@');
/*     */       }
/* 152 */       if (this.uri.getHost() != null) {
/* 153 */         uriBuilder.append(this.uri.getHost());
/*     */       }
/* 155 */       if (this.uri.getPort() != -1) {
/* 156 */         uriBuilder.append(':').append(this.uri.getPort());
/*     */       }
/*     */     } 
/* 159 */     if (StringUtils.hasLength(this.uriPath)) {
/* 160 */       uriBuilder.append(this.uriPath);
/*     */     }
/* 162 */     if (this.uri.getRawQuery() != null) {
/* 163 */       uriBuilder.append('?').append(this.uri.getRawQuery());
/*     */     }
/* 165 */     if (this.uri.getRawFragment() != null) {
/* 166 */       uriBuilder.append('#').append(this.uri.getRawFragment());
/*     */     }
/*     */     try {
/* 169 */       return new URI(uriBuilder.toString());
/*     */     }
/* 171 */     catch (URISyntaxException ex) {
/* 172 */       throw new IllegalStateException("Invalid URI path: \"" + this.uriPath + "\"", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class MutatedServerHttpRequest
/*     */     extends AbstractServerHttpRequest
/*     */   {
/*     */     private final String methodValue;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private final SslInfo sslInfo;
/*     */     
/*     */     @Nullable
/*     */     private InetSocketAddress remoteAddress;
/*     */     
/*     */     private final Flux<DataBuffer> body;
/*     */     
/*     */     private final ServerHttpRequest originalRequest;
/*     */ 
/*     */     
/*     */     public MutatedServerHttpRequest(URI uri, @Nullable String contextPath, String methodValue, @Nullable SslInfo sslInfo, @Nullable InetSocketAddress remoteAddress, HttpHeaders headers, Flux<DataBuffer> body, ServerHttpRequest originalRequest) {
/* 196 */       super(uri, contextPath, headers);
/* 197 */       this.methodValue = methodValue;
/* 198 */       this.remoteAddress = (remoteAddress != null) ? remoteAddress : originalRequest.getRemoteAddress();
/* 199 */       this.sslInfo = (sslInfo != null) ? sslInfo : originalRequest.getSslInfo();
/* 200 */       this.body = body;
/* 201 */       this.originalRequest = originalRequest;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getMethodValue() {
/* 206 */       return this.methodValue;
/*     */     }
/*     */ 
/*     */     
/*     */     protected MultiValueMap<String, HttpCookie> initCookies() {
/* 211 */       return this.originalRequest.getCookies();
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public InetSocketAddress getLocalAddress() {
/* 217 */       return this.originalRequest.getLocalAddress();
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public InetSocketAddress getRemoteAddress() {
/* 223 */       return this.remoteAddress;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     protected SslInfo initSslInfo() {
/* 229 */       return this.sslInfo;
/*     */     }
/*     */ 
/*     */     
/*     */     public Flux<DataBuffer> getBody() {
/* 234 */       return this.body;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> T getNativeRequest() {
/* 240 */       return ServerHttpRequestDecorator.getNativeRequest(this.originalRequest);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getId() {
/* 245 */       return this.originalRequest.getId();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/DefaultServerHttpRequestBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */