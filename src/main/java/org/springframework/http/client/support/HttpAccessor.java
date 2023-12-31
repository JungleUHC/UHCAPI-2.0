/*     */ package org.springframework.http.client.support;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.springframework.core.annotation.AnnotationAwareOrderComparator;
/*     */ import org.springframework.http.HttpLogging;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.client.ClientHttpRequest;
/*     */ import org.springframework.http.client.ClientHttpRequestFactory;
/*     */ import org.springframework.http.client.ClientHttpRequestInitializer;
/*     */ import org.springframework.http.client.SimpleClientHttpRequestFactory;
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
/*     */ public abstract class HttpAccessor
/*     */ {
/*  54 */   protected final Log logger = HttpLogging.forLogName(getClass());
/*     */   
/*  56 */   private ClientHttpRequestFactory requestFactory = (ClientHttpRequestFactory)new SimpleClientHttpRequestFactory();
/*     */   
/*  58 */   private final List<ClientHttpRequestInitializer> clientHttpRequestInitializers = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
/*  73 */     Assert.notNull(requestFactory, "ClientHttpRequestFactory must not be null");
/*  74 */     this.requestFactory = requestFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientHttpRequestFactory getRequestFactory() {
/*  81 */     return this.requestFactory;
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
/*     */   public void setClientHttpRequestInitializers(List<ClientHttpRequestInitializer> clientHttpRequestInitializers) {
/*  94 */     if (this.clientHttpRequestInitializers != clientHttpRequestInitializers) {
/*  95 */       this.clientHttpRequestInitializers.clear();
/*  96 */       this.clientHttpRequestInitializers.addAll(clientHttpRequestInitializers);
/*  97 */       AnnotationAwareOrderComparator.sort(this.clientHttpRequestInitializers);
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
/*     */   public List<ClientHttpRequestInitializer> getClientHttpRequestInitializers() {
/* 111 */     return this.clientHttpRequestInitializers;
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
/*     */   protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
/* 124 */     ClientHttpRequest request = getRequestFactory().createRequest(url, method);
/* 125 */     initialize(request);
/* 126 */     if (this.logger.isDebugEnabled()) {
/* 127 */       this.logger.debug("HTTP " + method.name() + " " + url);
/*     */     }
/* 129 */     return request;
/*     */   }
/*     */   
/*     */   private void initialize(ClientHttpRequest request) {
/* 133 */     this.clientHttpRequestInitializers.forEach(initializer -> initializer.initialize(request));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/support/HttpAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */