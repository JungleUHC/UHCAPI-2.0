/*     */ package org.springframework.remoting.httpinvoker;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Locale;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.NoHttpResponseException;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.Configurable;
/*     */ import org.apache.http.client.methods.HttpPost;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.config.Registry;
/*     */ import org.apache.http.config.RegistryBuilder;
/*     */ import org.apache.http.conn.HttpClientConnectionManager;
/*     */ import org.apache.http.conn.socket.ConnectionSocketFactory;
/*     */ import org.apache.http.conn.socket.PlainConnectionSocketFactory;
/*     */ import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
/*     */ import org.apache.http.entity.ByteArrayEntity;
/*     */ import org.apache.http.impl.client.HttpClientBuilder;
/*     */ import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
/*     */ import org.springframework.context.i18n.LocaleContext;
/*     */ import org.springframework.context.i18n.LocaleContextHolder;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.remoting.support.RemoteInvocationResult;
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
/*     */ @Deprecated
/*     */ public class HttpComponentsHttpInvokerRequestExecutor
/*     */   extends AbstractHttpInvokerRequestExecutor
/*     */ {
/*     */   private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
/*     */   private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
/*     */   private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 60000;
/*     */   private HttpClient httpClient;
/*     */   @Nullable
/*     */   private RequestConfig requestConfig;
/*     */   
/*     */   public HttpComponentsHttpInvokerRequestExecutor() {
/*  86 */     this(createDefaultHttpClient(), RequestConfig.custom()
/*  87 */         .setSocketTimeout(60000).build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpComponentsHttpInvokerRequestExecutor(HttpClient httpClient) {
/*  96 */     this(httpClient, (RequestConfig)null);
/*     */   }
/*     */   
/*     */   private HttpComponentsHttpInvokerRequestExecutor(HttpClient httpClient, @Nullable RequestConfig requestConfig) {
/* 100 */     this.httpClient = httpClient;
/* 101 */     this.requestConfig = requestConfig;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static HttpClient createDefaultHttpClient() {
/* 109 */     Registry<ConnectionSocketFactory> schemeRegistry = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
/*     */     
/* 111 */     PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(schemeRegistry);
/* 112 */     connectionManager.setMaxTotal(100);
/* 113 */     connectionManager.setDefaultMaxPerRoute(5);
/*     */     
/* 115 */     return (HttpClient)HttpClientBuilder.create().setConnectionManager((HttpClientConnectionManager)connectionManager).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHttpClient(HttpClient httpClient) {
/* 123 */     this.httpClient = httpClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpClient getHttpClient() {
/* 130 */     return this.httpClient;
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
/*     */   public void setConnectTimeout(int timeout) {
/* 142 */     Assert.isTrue((timeout >= 0), "Timeout must be a non-negative value");
/* 143 */     this.requestConfig = cloneRequestConfig().setConnectTimeout(timeout).build();
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
/* 156 */     this.requestConfig = cloneRequestConfig().setConnectionRequestTimeout(connectionRequestTimeout).build();
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
/*     */   public void setReadTimeout(int timeout) {
/* 169 */     Assert.isTrue((timeout >= 0), "Timeout must be a non-negative value");
/* 170 */     this.requestConfig = cloneRequestConfig().setSocketTimeout(timeout).build();
/*     */   }
/*     */   
/*     */   private RequestConfig.Builder cloneRequestConfig() {
/* 174 */     return (this.requestConfig != null) ? RequestConfig.copy(this.requestConfig) : RequestConfig.custom();
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
/*     */ 
/*     */   
/*     */   protected RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration config, ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
/* 193 */     HttpPost postMethod = createHttpPost(config);
/* 194 */     setRequestBody(config, postMethod, baos);
/*     */     try {
/* 196 */       HttpResponse response = executeHttpPost(config, getHttpClient(), postMethod);
/* 197 */       validateResponse(config, response);
/* 198 */       InputStream responseBody = getResponseBody(config, response);
/* 199 */       return readRemoteInvocationResult(responseBody, config.getCodebaseUrl());
/*     */     } finally {
/*     */       
/* 202 */       postMethod.releaseConnection();
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
/*     */   protected HttpPost createHttpPost(HttpInvokerClientConfiguration config) throws IOException {
/* 216 */     HttpPost httpPost = new HttpPost(config.getServiceUrl());
/*     */     
/* 218 */     RequestConfig requestConfig = createRequestConfig(config);
/* 219 */     if (requestConfig != null) {
/* 220 */       httpPost.setConfig(requestConfig);
/*     */     }
/*     */     
/* 223 */     LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
/* 224 */     if (localeContext != null) {
/* 225 */       Locale locale = localeContext.getLocale();
/* 226 */       if (locale != null) {
/* 227 */         httpPost.addHeader("Accept-Language", locale.toLanguageTag());
/*     */       }
/*     */     } 
/*     */     
/* 231 */     if (isAcceptGzipEncoding()) {
/* 232 */       httpPost.addHeader("Accept-Encoding", "gzip");
/*     */     }
/*     */     
/* 235 */     return httpPost;
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
/*     */   @Nullable
/*     */   protected RequestConfig createRequestConfig(HttpInvokerClientConfiguration config) {
/* 250 */     HttpClient client = getHttpClient();
/* 251 */     if (client instanceof Configurable) {
/* 252 */       RequestConfig clientRequestConfig = ((Configurable)client).getConfig();
/* 253 */       return mergeRequestConfig(clientRequestConfig);
/*     */     } 
/* 255 */     return this.requestConfig;
/*     */   }
/*     */   
/*     */   private RequestConfig mergeRequestConfig(RequestConfig defaultRequestConfig) {
/* 259 */     if (this.requestConfig == null) {
/* 260 */       return defaultRequestConfig;
/*     */     }
/*     */     
/* 263 */     RequestConfig.Builder builder = RequestConfig.copy(defaultRequestConfig);
/* 264 */     int connectTimeout = this.requestConfig.getConnectTimeout();
/* 265 */     if (connectTimeout >= 0) {
/* 266 */       builder.setConnectTimeout(connectTimeout);
/*     */     }
/* 268 */     int connectionRequestTimeout = this.requestConfig.getConnectionRequestTimeout();
/* 269 */     if (connectionRequestTimeout >= 0) {
/* 270 */       builder.setConnectionRequestTimeout(connectionRequestTimeout);
/*     */     }
/* 272 */     int socketTimeout = this.requestConfig.getSocketTimeout();
/* 273 */     if (socketTimeout >= 0) {
/* 274 */       builder.setSocketTimeout(socketTimeout);
/*     */     }
/* 276 */     return builder.build();
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
/*     */   
/*     */   protected void setRequestBody(HttpInvokerClientConfiguration config, HttpPost httpPost, ByteArrayOutputStream baos) throws IOException {
/* 294 */     ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
/* 295 */     entity.setContentType(getContentType());
/* 296 */     httpPost.setEntity((HttpEntity)entity);
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
/*     */   protected HttpResponse executeHttpPost(HttpInvokerClientConfiguration config, HttpClient httpClient, HttpPost httpPost) throws IOException {
/* 311 */     return httpClient.execute((HttpUriRequest)httpPost);
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
/*     */   protected void validateResponse(HttpInvokerClientConfiguration config, HttpResponse response) throws IOException {
/* 326 */     StatusLine status = response.getStatusLine();
/* 327 */     if (status.getStatusCode() >= 300) {
/* 328 */       throw new NoHttpResponseException("Did not receive successful HTTP response: status code = " + status
/* 329 */           .getStatusCode() + ", status message = [" + status
/* 330 */           .getReasonPhrase() + "]");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected InputStream getResponseBody(HttpInvokerClientConfiguration config, HttpResponse httpResponse) throws IOException {
/* 349 */     if (isGzipResponse(httpResponse)) {
/* 350 */       return new GZIPInputStream(httpResponse.getEntity().getContent());
/*     */     }
/*     */     
/* 353 */     return httpResponse.getEntity().getContent();
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
/*     */   protected boolean isGzipResponse(HttpResponse httpResponse) {
/* 365 */     Header encodingHeader = httpResponse.getFirstHeader("Content-Encoding");
/* 366 */     return (encodingHeader != null && encodingHeader.getValue() != null && encodingHeader
/* 367 */       .getValue().toLowerCase().contains("gzip"));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/httpinvoker/HttpComponentsHttpInvokerRequestExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */