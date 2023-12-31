/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.concurrent.FutureCallback;
/*     */ import org.apache.http.nio.client.HttpAsyncClient;
/*     */ import org.apache.http.nio.entity.NByteArrayEntity;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.util.concurrent.FailureCallback;
/*     */ import org.springframework.util.concurrent.FutureAdapter;
/*     */ import org.springframework.util.concurrent.ListenableFuture;
/*     */ import org.springframework.util.concurrent.ListenableFutureCallback;
/*     */ import org.springframework.util.concurrent.ListenableFutureCallbackRegistry;
/*     */ import org.springframework.util.concurrent.SuccessCallback;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ final class HttpComponentsAsyncClientHttpRequest
/*     */   extends AbstractBufferingAsyncClientHttpRequest
/*     */ {
/*     */   private final HttpAsyncClient httpClient;
/*     */   private final HttpUriRequest httpRequest;
/*     */   private final HttpContext httpContext;
/*     */   
/*     */   HttpComponentsAsyncClientHttpRequest(HttpAsyncClient client, HttpUriRequest request, HttpContext context) {
/*  65 */     this.httpClient = client;
/*  66 */     this.httpRequest = request;
/*  67 */     this.httpContext = context;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMethodValue() {
/*  73 */     return this.httpRequest.getMethod();
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*  78 */     return this.httpRequest.getURI();
/*     */   }
/*     */   
/*     */   HttpContext getHttpContext() {
/*  82 */     return this.httpContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
/*  89 */     HttpComponentsClientHttpRequest.addHeaders(this.httpRequest, headers);
/*     */     
/*  91 */     if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
/*  92 */       HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest)this.httpRequest;
/*  93 */       NByteArrayEntity nByteArrayEntity = new NByteArrayEntity(bufferedOutput);
/*  94 */       entityEnclosingRequest.setEntity((HttpEntity)nByteArrayEntity);
/*     */     } 
/*     */     
/*  97 */     HttpResponseFutureCallback callback = new HttpResponseFutureCallback(this.httpRequest);
/*  98 */     Future<HttpResponse> futureResponse = this.httpClient.execute(this.httpRequest, this.httpContext, callback);
/*  99 */     return new ClientHttpResponseFuture(futureResponse, callback);
/*     */   }
/*     */ 
/*     */   
/*     */   private static class HttpResponseFutureCallback
/*     */     implements FutureCallback<HttpResponse>
/*     */   {
/*     */     private final HttpUriRequest request;
/* 107 */     private final ListenableFutureCallbackRegistry<ClientHttpResponse> callbacks = new ListenableFutureCallbackRegistry();
/*     */ 
/*     */     
/*     */     public HttpResponseFutureCallback(HttpUriRequest request) {
/* 111 */       this.request = request;
/*     */     }
/*     */     
/*     */     public void addCallback(ListenableFutureCallback<? super ClientHttpResponse> callback) {
/* 115 */       this.callbacks.addCallback(callback);
/*     */     }
/*     */     
/*     */     public void addSuccessCallback(SuccessCallback<? super ClientHttpResponse> callback) {
/* 119 */       this.callbacks.addSuccessCallback(callback);
/*     */     }
/*     */     
/*     */     public void addFailureCallback(FailureCallback callback) {
/* 123 */       this.callbacks.addFailureCallback(callback);
/*     */     }
/*     */ 
/*     */     
/*     */     public void completed(HttpResponse result) {
/* 128 */       this.callbacks.success(new HttpComponentsAsyncClientHttpResponse(result));
/*     */     }
/*     */ 
/*     */     
/*     */     public void failed(Exception ex) {
/* 133 */       this.callbacks.failure(ex);
/*     */     }
/*     */ 
/*     */     
/*     */     public void cancelled() {
/* 138 */       this.request.abort();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ClientHttpResponseFuture
/*     */     extends FutureAdapter<ClientHttpResponse, HttpResponse>
/*     */     implements ListenableFuture<ClientHttpResponse>
/*     */   {
/*     */     private final HttpComponentsAsyncClientHttpRequest.HttpResponseFutureCallback callback;
/*     */     
/*     */     public ClientHttpResponseFuture(Future<HttpResponse> response, HttpComponentsAsyncClientHttpRequest.HttpResponseFutureCallback callback) {
/* 149 */       super(response);
/* 150 */       this.callback = callback;
/*     */     }
/*     */ 
/*     */     
/*     */     protected ClientHttpResponse adapt(HttpResponse response) {
/* 155 */       return new HttpComponentsAsyncClientHttpResponse(response);
/*     */     }
/*     */ 
/*     */     
/*     */     public void addCallback(ListenableFutureCallback<? super ClientHttpResponse> callback) {
/* 160 */       this.callback.addCallback(callback);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void addCallback(SuccessCallback<? super ClientHttpResponse> successCallback, FailureCallback failureCallback) {
/* 167 */       this.callback.addSuccessCallback(successCallback);
/* 168 */       this.callback.addFailureCallback(failureCallback);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/HttpComponentsAsyncClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */