/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.util.function.BiFunction;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.Configurable;
/*     */ import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
/*     */ import org.apache.http.client.methods.HttpGet;
/*     */ import org.apache.http.client.methods.HttpHead;
/*     */ import org.apache.http.client.methods.HttpOptions;
/*     */ import org.apache.http.client.methods.HttpPatch;
/*     */ import org.apache.http.client.methods.HttpPost;
/*     */ import org.apache.http.client.methods.HttpPut;
/*     */ import org.apache.http.client.methods.HttpTrace;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.impl.client.HttpClients;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.springframework.beans.factory.DisposableBean;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ public class HttpComponentsClientHttpRequestFactory
/*     */   implements ClientHttpRequestFactory, DisposableBean
/*     */ {
/*     */   private HttpClient httpClient;
/*     */   @Nullable
/*     */   private RequestConfig requestConfig;
/*     */   private boolean bufferRequestBody = true;
/*     */   @Nullable
/*     */   private BiFunction<HttpMethod, URI, HttpContext> httpContextFactory;
/*     */   
/*     */   public HttpComponentsClientHttpRequestFactory() {
/*  79 */     this.httpClient = (HttpClient)HttpClients.createSystem();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
/*  88 */     this.httpClient = httpClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHttpClient(HttpClient httpClient) {
/*  97 */     Assert.notNull(httpClient, "HttpClient must not be null");
/*  98 */     this.httpClient = httpClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpClient getHttpClient() {
/* 106 */     return this.httpClient;
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
/*     */   
/*     */   public void setConnectTimeout(int timeout) {
/* 123 */     Assert.isTrue((timeout >= 0), "Timeout must be a non-negative value");
/* 124 */     this.requestConfig = requestConfigBuilder().setConnectTimeout(timeout).build();
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
/*     */   public void setConnectionRequestTimeout(int connectionRequestTimeout) {
/* 137 */     this
/* 138 */       .requestConfig = requestConfigBuilder().setConnectionRequestTimeout(connectionRequestTimeout).build();
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
/*     */   public void setReadTimeout(int timeout) {
/* 150 */     Assert.isTrue((timeout >= 0), "Timeout must be a non-negative value");
/* 151 */     this.requestConfig = requestConfigBuilder().setSocketTimeout(timeout).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBufferRequestBody(boolean bufferRequestBody) {
/* 161 */     this.bufferRequestBody = bufferRequestBody;
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
/*     */   public void setHttpContextFactory(BiFunction<HttpMethod, URI, HttpContext> httpContextFactory) {
/* 175 */     this.httpContextFactory = httpContextFactory;
/*     */   }
/*     */   
/*     */   public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
/*     */     HttpClientContext httpClientContext;
/* 180 */     HttpClient client = getHttpClient();
/*     */     
/* 182 */     HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
/* 183 */     postProcessHttpRequest(httpRequest);
/* 184 */     HttpContext context = createHttpContext(httpMethod, uri);
/* 185 */     if (context == null) {
/* 186 */       httpClientContext = HttpClientContext.create();
/*     */     }
/*     */ 
/*     */     
/* 190 */     if (httpClientContext.getAttribute("http.request-config") == null) {
/*     */       
/* 192 */       RequestConfig config = null;
/* 193 */       if (httpRequest instanceof Configurable) {
/* 194 */         config = ((Configurable)httpRequest).getConfig();
/*     */       }
/* 196 */       if (config == null) {
/* 197 */         config = createRequestConfig(client);
/*     */       }
/* 199 */       if (config != null) {
/* 200 */         httpClientContext.setAttribute("http.request-config", config);
/*     */       }
/*     */     } 
/*     */     
/* 204 */     if (this.bufferRequestBody) {
/* 205 */       return new HttpComponentsClientHttpRequest(client, httpRequest, (HttpContext)httpClientContext);
/*     */     }
/*     */     
/* 208 */     return new HttpComponentsStreamingClientHttpRequest(client, httpRequest, (HttpContext)httpClientContext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private RequestConfig.Builder requestConfigBuilder() {
/* 218 */     return (this.requestConfig != null) ? RequestConfig.copy(this.requestConfig) : RequestConfig.custom();
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
/*     */   @Nullable
/*     */   protected RequestConfig createRequestConfig(Object client) {
/* 234 */     if (client instanceof Configurable) {
/* 235 */       RequestConfig clientRequestConfig = ((Configurable)client).getConfig();
/* 236 */       return mergeRequestConfig(clientRequestConfig);
/*     */     } 
/* 238 */     return this.requestConfig;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected RequestConfig mergeRequestConfig(RequestConfig clientConfig) {
/* 249 */     if (this.requestConfig == null) {
/* 250 */       return clientConfig;
/*     */     }
/*     */     
/* 253 */     RequestConfig.Builder builder = RequestConfig.copy(clientConfig);
/* 254 */     int connectTimeout = this.requestConfig.getConnectTimeout();
/* 255 */     if (connectTimeout >= 0) {
/* 256 */       builder.setConnectTimeout(connectTimeout);
/*     */     }
/* 258 */     int connectionRequestTimeout = this.requestConfig.getConnectionRequestTimeout();
/* 259 */     if (connectionRequestTimeout >= 0) {
/* 260 */       builder.setConnectionRequestTimeout(connectionRequestTimeout);
/*     */     }
/* 262 */     int socketTimeout = this.requestConfig.getSocketTimeout();
/* 263 */     if (socketTimeout >= 0) {
/* 264 */       builder.setSocketTimeout(socketTimeout);
/*     */     }
/* 266 */     return builder.build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
/* 276 */     switch (httpMethod) {
/*     */       case GET:
/* 278 */         return (HttpUriRequest)new HttpGet(uri);
/*     */       case HEAD:
/* 280 */         return (HttpUriRequest)new HttpHead(uri);
/*     */       case POST:
/* 282 */         return (HttpUriRequest)new HttpPost(uri);
/*     */       case PUT:
/* 284 */         return (HttpUriRequest)new HttpPut(uri);
/*     */       case PATCH:
/* 286 */         return (HttpUriRequest)new HttpPatch(uri);
/*     */       case DELETE:
/* 288 */         return (HttpUriRequest)new HttpDelete(uri);
/*     */       case OPTIONS:
/* 290 */         return (HttpUriRequest)new HttpOptions(uri);
/*     */       case TRACE:
/* 292 */         return (HttpUriRequest)new HttpTrace(uri);
/*     */     } 
/* 294 */     throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
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
/*     */   protected void postProcessHttpRequest(HttpUriRequest request) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
/* 316 */     return (this.httpContextFactory != null) ? this.httpContextFactory.apply(httpMethod, uri) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() throws Exception {
/* 327 */     HttpClient httpClient = getHttpClient();
/* 328 */     if (httpClient instanceof Closeable) {
/* 329 */       ((Closeable)httpClient).close();
/*     */     }
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
/*     */   private static class HttpDelete
/*     */     extends HttpEntityEnclosingRequestBase
/*     */   {
/*     */     public HttpDelete(URI uri) {
/* 346 */       setURI(uri);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getMethod() {
/* 351 */       return "DELETE";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/HttpComponentsClientHttpRequestFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */