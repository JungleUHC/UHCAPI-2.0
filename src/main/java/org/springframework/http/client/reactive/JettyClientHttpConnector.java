/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import org.eclipse.jetty.client.HttpClient;
/*     */ import org.eclipse.jetty.client.api.Request;
/*     */ import org.eclipse.jetty.reactive.client.ContentChunk;
/*     */ import org.eclipse.jetty.reactive.client.ReactiveResponse;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DefaultDataBufferFactory;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JettyClientHttpConnector
/*     */   implements ClientHttpConnector
/*     */ {
/*     */   private final HttpClient httpClient;
/*  47 */   private DataBufferFactory bufferFactory = (DataBufferFactory)DefaultDataBufferFactory.sharedInstance;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JettyClientHttpConnector() {
/*  54 */     this(new HttpClient());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JettyClientHttpConnector(HttpClient httpClient) {
/*  61 */     this(httpClient, (JettyResourceFactory)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JettyClientHttpConnector(HttpClient httpClient, @Nullable JettyResourceFactory resourceFactory) {
/*  72 */     Assert.notNull(httpClient, "HttpClient is required");
/*  73 */     if (resourceFactory != null) {
/*  74 */       httpClient.setExecutor(resourceFactory.getExecutor());
/*  75 */       httpClient.setByteBufferPool(resourceFactory.getByteBufferPool());
/*  76 */       httpClient.setScheduler(resourceFactory.getScheduler());
/*     */     } 
/*  78 */     this.httpClient = httpClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JettyClientHttpConnector(JettyResourceFactory resourceFactory, @Nullable Consumer<HttpClient> customizer) {
/*  90 */     this(new HttpClient(), resourceFactory);
/*  91 */     if (customizer != null) {
/*  92 */       customizer.accept(this.httpClient);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBufferFactory(DataBufferFactory bufferFactory) {
/* 101 */     this.bufferFactory = bufferFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
/* 109 */     if (!uri.isAbsolute()) {
/* 110 */       return Mono.error(new IllegalArgumentException("URI is not absolute: " + uri));
/*     */     }
/*     */     
/* 113 */     if (!this.httpClient.isStarted()) {
/*     */       try {
/* 115 */         this.httpClient.start();
/*     */       }
/* 117 */       catch (Exception ex) {
/* 118 */         return Mono.error(ex);
/*     */       } 
/*     */     }
/*     */     
/* 122 */     Request jettyRequest = this.httpClient.newRequest(uri).method(method.toString());
/* 123 */     JettyClientHttpRequest request = new JettyClientHttpRequest(jettyRequest, this.bufferFactory);
/*     */     
/* 125 */     return ((Mono)requestCallback.apply(request)).then(execute(request));
/*     */   }
/*     */   
/*     */   private Mono<ClientHttpResponse> execute(JettyClientHttpRequest request) {
/* 129 */     return Mono.fromDirect(request.toReactiveRequest()
/* 130 */         .response((reactiveResponse, chunkPublisher) -> {
/*     */             Flux<DataBuffer> content = Flux.from(chunkPublisher).map(this::toDataBuffer);
/*     */             return (Publisher)Mono.just(new JettyClientHttpResponse(reactiveResponse, (Publisher<DataBuffer>)content));
/*     */           }));
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
/*     */   private DataBuffer toDataBuffer(ContentChunk chunk) {
/* 145 */     DataBuffer buffer = this.bufferFactory.allocateBuffer(chunk.buffer.capacity());
/* 146 */     buffer.write(new ByteBuffer[] { chunk.buffer });
/* 147 */     chunk.callback.succeeded();
/* 148 */     return buffer;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/JettyClientHttpConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */