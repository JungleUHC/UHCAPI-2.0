/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import io.netty.handler.codec.http.HttpMethod;
/*     */ import java.net.URI;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.function.Function;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.util.Assert;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.netty.Connection;
/*     */ import reactor.netty.NettyOutbound;
/*     */ import reactor.netty.http.client.HttpClient;
/*     */ import reactor.netty.http.client.HttpClientRequest;
/*     */ import reactor.netty.http.client.HttpClientResponse;
/*     */ import reactor.netty.resources.ConnectionProvider;
/*     */ import reactor.netty.resources.LoopResources;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReactorClientHttpConnector
/*     */   implements ClientHttpConnector
/*     */ {
/*     */   private static final Function<HttpClient, HttpClient> defaultInitializer;
/*     */   private final HttpClient httpClient;
/*     */   
/*     */   static {
/*  43 */     defaultInitializer = (client -> client.compress(true));
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
/*     */   public ReactorClientHttpConnector() {
/*  56 */     this.httpClient = defaultInitializer.apply(HttpClient.create());
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
/*     */   
/*     */   public ReactorClientHttpConnector(ReactorResourceFactory factory, Function<HttpClient, HttpClient> mapper) {
/*  76 */     ConnectionProvider provider = factory.getConnectionProvider();
/*  77 */     Assert.notNull(provider, "No ConnectionProvider: is ReactorResourceFactory not initialized yet?");
/*  78 */     this
/*  79 */       .httpClient = defaultInitializer.<HttpClient>andThen(mapper).<HttpClient>andThen(applyLoopResources(factory)).apply(HttpClient.create(provider));
/*     */   }
/*     */   
/*     */   private static Function<HttpClient, HttpClient> applyLoopResources(ReactorResourceFactory factory) {
/*  83 */     return httpClient -> {
/*     */         LoopResources resources = factory.getLoopResources();
/*     */         Assert.notNull(resources, "No LoopResources: is ReactorResourceFactory not initialized yet?");
/*     */         return (HttpClient)httpClient.runOn(resources);
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReactorClientHttpConnector(HttpClient httpClient) {
/*  97 */     Assert.notNull(httpClient, "HttpClient is required");
/*  98 */     this.httpClient = httpClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
/* 106 */     AtomicReference<ReactorClientHttpResponse> responseRef = new AtomicReference<>();
/*     */     
/* 108 */     return ((HttpClient.RequestSender)this.httpClient
/* 109 */       .request(HttpMethod.valueOf(method.name()))
/* 110 */       .uri(uri.toString()))
/* 111 */       .send((request, outbound) -> (Publisher)requestCallback.apply(adaptRequest(method, uri, request, outbound)))
/* 112 */       .responseConnection((response, connection) -> {
/*     */           responseRef.set(new ReactorClientHttpResponse(response, connection));
/*     */           
/*     */           return (Publisher)Mono.just(responseRef.get());
/* 116 */         }).next()
/* 117 */       .doOnCancel(() -> {
/*     */           ReactorClientHttpResponse response = responseRef.get();
/*     */           if (response != null) {
/*     */             response.releaseAfterCancel(method);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ReactorClientHttpRequest adaptRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound nettyOutbound) {
/* 128 */     return new ReactorClientHttpRequest(method, uri, request, nettyOutbound);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/ReactorClientHttpConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */