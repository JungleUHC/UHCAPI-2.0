/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.Proxy;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import org.springframework.core.task.AsyncListenableTaskExecutor;
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
/*     */ public class SimpleClientHttpRequestFactory
/*     */   implements ClientHttpRequestFactory, AsyncClientHttpRequestFactory
/*     */ {
/*     */   private static final int DEFAULT_CHUNK_SIZE = 4096;
/*     */   @Nullable
/*     */   private Proxy proxy;
/*     */   private boolean bufferRequestBody = true;
/*  51 */   private int chunkSize = 4096;
/*     */   
/*  53 */   private int connectTimeout = -1;
/*     */   
/*  55 */   private int readTimeout = -1;
/*     */ 
/*     */   
/*     */   private boolean outputStreaming = true;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private AsyncListenableTaskExecutor taskExecutor;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProxy(Proxy proxy) {
/*  67 */     this.proxy = proxy;
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
/*     */   public void setBufferRequestBody(boolean bufferRequestBody) {
/*  84 */     this.bufferRequestBody = bufferRequestBody;
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
/*     */   public void setChunkSize(int chunkSize) {
/*  97 */     this.chunkSize = chunkSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConnectTimeout(int connectTimeout) {
/* 107 */     this.connectTimeout = connectTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadTimeout(int readTimeout) {
/* 117 */     this.readTimeout = readTimeout;
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
/*     */   public void setOutputStreaming(boolean outputStreaming) {
/* 130 */     this.outputStreaming = outputStreaming;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTaskExecutor(AsyncListenableTaskExecutor taskExecutor) {
/* 139 */     this.taskExecutor = taskExecutor;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
/* 145 */     HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
/* 146 */     prepareConnection(connection, httpMethod.name());
/*     */     
/* 148 */     if (this.bufferRequestBody) {
/* 149 */       return new SimpleBufferingClientHttpRequest(connection, this.outputStreaming);
/*     */     }
/*     */     
/* 152 */     return new SimpleStreamingClientHttpRequest(connection, this.chunkSize, this.outputStreaming);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
/* 162 */     Assert.state((this.taskExecutor != null), "Asynchronous execution requires TaskExecutor to be set");
/*     */     
/* 164 */     HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
/* 165 */     prepareConnection(connection, httpMethod.name());
/*     */     
/* 167 */     if (this.bufferRequestBody) {
/* 168 */       return new SimpleBufferingAsyncClientHttpRequest(connection, this.outputStreaming, this.taskExecutor);
/*     */     }
/*     */ 
/*     */     
/* 172 */     return new SimpleStreamingAsyncClientHttpRequest(connection, this.chunkSize, this.outputStreaming, this.taskExecutor);
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
/*     */   protected HttpURLConnection openConnection(URL url, @Nullable Proxy proxy) throws IOException {
/* 187 */     URLConnection urlConnection = (proxy != null) ? url.openConnection(proxy) : url.openConnection();
/* 188 */     if (!(urlConnection instanceof HttpURLConnection)) {
/* 189 */       throw new IllegalStateException("HttpURLConnection required for [" + url + "] but got: " + urlConnection);
/*     */     }
/*     */     
/* 192 */     return (HttpURLConnection)urlConnection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
/* 203 */     if (this.connectTimeout >= 0) {
/* 204 */       connection.setConnectTimeout(this.connectTimeout);
/*     */     }
/* 206 */     if (this.readTimeout >= 0) {
/* 207 */       connection.setReadTimeout(this.readTimeout);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 212 */     boolean mayWrite = ("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod) || "DELETE".equals(httpMethod));
/*     */     
/* 214 */     connection.setDoInput(true);
/* 215 */     connection.setInstanceFollowRedirects("GET".equals(httpMethod));
/* 216 */     connection.setDoOutput(mayWrite);
/* 217 */     connection.setRequestMethod(httpMethod);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/SimpleClientHttpRequestFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */