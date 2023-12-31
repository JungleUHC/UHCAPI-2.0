/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URI;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.HttpRequest;
/*     */ import org.springframework.http.StreamingHttpOutputMessage;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StreamUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class InterceptingClientHttpRequest
/*     */   extends AbstractBufferingClientHttpRequest
/*     */ {
/*     */   private final ClientHttpRequestFactory requestFactory;
/*     */   private final List<ClientHttpRequestInterceptor> interceptors;
/*     */   private HttpMethod method;
/*     */   private URI uri;
/*     */   
/*     */   protected InterceptingClientHttpRequest(ClientHttpRequestFactory requestFactory, List<ClientHttpRequestInterceptor> interceptors, URI uri, HttpMethod method) {
/*  52 */     this.requestFactory = requestFactory;
/*  53 */     this.interceptors = interceptors;
/*  54 */     this.method = method;
/*  55 */     this.uri = uri;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpMethod getMethod() {
/*  61 */     return this.method;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMethodValue() {
/*  66 */     return this.method.name();
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*  71 */     return this.uri;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
/*  76 */     InterceptingRequestExecution requestExecution = new InterceptingRequestExecution();
/*  77 */     return requestExecution.execute(this, bufferedOutput);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class InterceptingRequestExecution
/*     */     implements ClientHttpRequestExecution
/*     */   {
/*  86 */     private final Iterator<ClientHttpRequestInterceptor> iterator = InterceptingClientHttpRequest.this.interceptors.iterator();
/*     */ 
/*     */ 
/*     */     
/*     */     public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
/*  91 */       if (this.iterator.hasNext()) {
/*  92 */         ClientHttpRequestInterceptor nextInterceptor = this.iterator.next();
/*  93 */         return nextInterceptor.intercept(request, body, this);
/*     */       } 
/*     */       
/*  96 */       HttpMethod method = request.getMethod();
/*  97 */       Assert.state((method != null), "No standard HTTP method");
/*  98 */       ClientHttpRequest delegate = InterceptingClientHttpRequest.this.requestFactory.createRequest(request.getURI(), method);
/*  99 */       request.getHeaders().forEach((key, value) -> delegate.getHeaders().addAll(key, value));
/* 100 */       if (body.length > 0) {
/* 101 */         if (delegate instanceof StreamingHttpOutputMessage) {
/* 102 */           StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)delegate;
/* 103 */           streamingOutputMessage.setBody(outputStream -> StreamUtils.copy(body, outputStream));
/*     */         } else {
/*     */           
/* 106 */           StreamUtils.copy(body, delegate.getBody());
/*     */         } 
/*     */       }
/* 109 */       return delegate.execute();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/InterceptingClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */