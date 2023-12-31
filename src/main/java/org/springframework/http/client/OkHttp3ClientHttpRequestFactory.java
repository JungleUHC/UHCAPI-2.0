/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import okhttp3.Cache;
/*     */ import okhttp3.MediaType;
/*     */ import okhttp3.OkHttpClient;
/*     */ import okhttp3.Request;
/*     */ import okhttp3.RequestBody;
/*     */ import okhttp3.internal.http.HttpMethod;
/*     */ import org.springframework.beans.factory.DisposableBean;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StringUtils;
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
/*     */ public class OkHttp3ClientHttpRequestFactory
/*     */   implements ClientHttpRequestFactory, AsyncClientHttpRequestFactory, DisposableBean
/*     */ {
/*     */   private OkHttpClient client;
/*     */   private final boolean defaultClient;
/*     */   
/*     */   public OkHttp3ClientHttpRequestFactory() {
/*  58 */     this.client = new OkHttpClient();
/*  59 */     this.defaultClient = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OkHttp3ClientHttpRequestFactory(OkHttpClient client) {
/*  67 */     Assert.notNull(client, "OkHttpClient must not be null");
/*  68 */     this.client = client;
/*  69 */     this.defaultClient = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadTimeout(int readTimeout) {
/*  78 */     this
/*     */       
/*  80 */       .client = this.client.newBuilder().readTimeout(readTimeout, TimeUnit.MILLISECONDS).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteTimeout(int writeTimeout) {
/*  88 */     this
/*     */       
/*  90 */       .client = this.client.newBuilder().writeTimeout(writeTimeout, TimeUnit.MILLISECONDS).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConnectTimeout(int connectTimeout) {
/*  98 */     this
/*     */       
/* 100 */       .client = this.client.newBuilder().connectTimeout(connectTimeout, TimeUnit.MILLISECONDS).build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
/* 106 */     return new OkHttp3ClientHttpRequest(this.client, uri, httpMethod);
/*     */   }
/*     */ 
/*     */   
/*     */   public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) {
/* 111 */     return new OkHttp3AsyncClientHttpRequest(this.client, uri, httpMethod);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() throws IOException {
/* 117 */     if (this.defaultClient) {
/*     */       
/* 119 */       Cache cache = this.client.cache();
/* 120 */       if (cache != null) {
/* 121 */         cache.close();
/*     */       }
/* 123 */       this.client.dispatcher().executorService().shutdown();
/* 124 */       this.client.connectionPool().evictAll();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Request buildRequest(HttpHeaders headers, byte[] content, URI uri, HttpMethod method) throws MalformedURLException {
/* 132 */     MediaType contentType = getContentType(headers);
/*     */ 
/*     */     
/* 135 */     RequestBody body = (content.length > 0 || HttpMethod.requiresRequestBody(method.name())) ? RequestBody.create(contentType, content) : null;
/*     */     
/* 137 */     Request.Builder builder = (new Request.Builder()).url(uri.toURL()).method(method.name(), body);
/* 138 */     headers.forEach((headerName, headerValues) -> {
/*     */           for (String headerValue : headerValues) {
/*     */             builder.addHeader(headerName, headerValue);
/*     */           }
/*     */         });
/* 143 */     return builder.build();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static MediaType getContentType(HttpHeaders headers) {
/* 148 */     String rawContentType = headers.getFirst("Content-Type");
/* 149 */     return StringUtils.hasText(rawContentType) ? MediaType.parse(rawContentType) : null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/OkHttp3ClientHttpRequestFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */