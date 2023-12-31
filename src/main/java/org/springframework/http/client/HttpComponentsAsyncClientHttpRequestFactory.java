/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.Configurable;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.impl.client.CloseableHttpClient;
/*     */ import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
/*     */ import org.apache.http.impl.nio.client.HttpAsyncClients;
/*     */ import org.apache.http.nio.client.HttpAsyncClient;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.http.HttpMethod;
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
/*     */ @Deprecated
/*     */ public class HttpComponentsAsyncClientHttpRequestFactory
/*     */   extends HttpComponentsClientHttpRequestFactory
/*     */   implements AsyncClientHttpRequestFactory, InitializingBean
/*     */ {
/*     */   private HttpAsyncClient asyncClient;
/*     */   
/*     */   public HttpComponentsAsyncClientHttpRequestFactory() {
/*  63 */     this.asyncClient = (HttpAsyncClient)HttpAsyncClients.createSystem();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpComponentsAsyncClientHttpRequestFactory(HttpAsyncClient asyncClient) {
/*  74 */     this.asyncClient = asyncClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpComponentsAsyncClientHttpRequestFactory(CloseableHttpAsyncClient asyncClient) {
/*  84 */     this.asyncClient = (HttpAsyncClient)asyncClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpComponentsAsyncClientHttpRequestFactory(HttpClient httpClient, HttpAsyncClient asyncClient) {
/*  95 */     super(httpClient);
/*  96 */     this.asyncClient = asyncClient;
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
/*     */   public HttpComponentsAsyncClientHttpRequestFactory(CloseableHttpClient httpClient, CloseableHttpAsyncClient asyncClient) {
/* 108 */     super((HttpClient)httpClient);
/* 109 */     this.asyncClient = (HttpAsyncClient)asyncClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAsyncClient(HttpAsyncClient asyncClient) {
/* 120 */     Assert.notNull(asyncClient, "HttpAsyncClient must not be null");
/* 121 */     this.asyncClient = asyncClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpAsyncClient getAsyncClient() {
/* 131 */     return this.asyncClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setHttpAsyncClient(CloseableHttpAsyncClient asyncClient) {
/* 141 */     this.asyncClient = (HttpAsyncClient)asyncClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public CloseableHttpAsyncClient getHttpAsyncClient() {
/* 151 */     Assert.state(this.asyncClient instanceof CloseableHttpAsyncClient, "No CloseableHttpAsyncClient - use getAsyncClient() instead");
/*     */     
/* 153 */     return (CloseableHttpAsyncClient)this.asyncClient;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 159 */     startAsyncClient();
/*     */   }
/*     */   
/*     */   private HttpAsyncClient startAsyncClient() {
/* 163 */     HttpAsyncClient client = getAsyncClient();
/* 164 */     if (client instanceof CloseableHttpAsyncClient) {
/*     */       
/* 166 */       CloseableHttpAsyncClient closeableAsyncClient = (CloseableHttpAsyncClient)client;
/* 167 */       if (!closeableAsyncClient.isRunning()) {
/* 168 */         closeableAsyncClient.start();
/*     */       }
/*     */     } 
/* 171 */     return client;
/*     */   }
/*     */   
/*     */   public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
/*     */     HttpClientContext httpClientContext;
/* 176 */     HttpAsyncClient client = startAsyncClient();
/*     */     
/* 178 */     HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
/* 179 */     postProcessHttpRequest(httpRequest);
/* 180 */     HttpContext context = createHttpContext(httpMethod, uri);
/* 181 */     if (context == null) {
/* 182 */       httpClientContext = HttpClientContext.create();
/*     */     }
/*     */ 
/*     */     
/* 186 */     if (httpClientContext.getAttribute("http.request-config") == null) {
/*     */       
/* 188 */       RequestConfig config = null;
/* 189 */       if (httpRequest instanceof Configurable) {
/* 190 */         config = ((Configurable)httpRequest).getConfig();
/*     */       }
/* 192 */       if (config == null) {
/* 193 */         config = createRequestConfig(client);
/*     */       }
/* 195 */       if (config != null) {
/* 196 */         httpClientContext.setAttribute("http.request-config", config);
/*     */       }
/*     */     } 
/*     */     
/* 200 */     return new HttpComponentsAsyncClientHttpRequest(client, httpRequest, (HttpContext)httpClientContext);
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() throws Exception {
/*     */     try {
/* 206 */       super.destroy();
/*     */     } finally {
/*     */       
/* 209 */       HttpAsyncClient asyncClient = getAsyncClient();
/* 210 */       if (asyncClient instanceof Closeable)
/* 211 */         ((Closeable)asyncClient).close(); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/HttpComponentsAsyncClientHttpRequestFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */