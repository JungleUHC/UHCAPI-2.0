/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Function;
/*     */ import org.apache.hc.client5.http.cookie.BasicCookieStore;
/*     */ import org.apache.hc.client5.http.cookie.CookieStore;
/*     */ import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
/*     */ import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
/*     */ import org.apache.hc.client5.http.protocol.HttpClientContext;
/*     */ import org.apache.hc.core5.concurrent.FutureCallback;
/*     */ import org.apache.hc.core5.http.HttpResponse;
/*     */ import org.apache.hc.core5.http.HttpStreamResetException;
/*     */ import org.apache.hc.core5.http.Message;
/*     */ import org.apache.hc.core5.http.nio.AsyncRequestProducer;
/*     */ import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
/*     */ import org.apache.hc.core5.http.protocol.HttpContext;
/*     */ import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DefaultDataBufferFactory;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.util.Assert;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.core.publisher.MonoSink;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpComponentsClientHttpConnector
/*     */   implements ClientHttpConnector, Closeable
/*     */ {
/*     */   private final CloseableHttpAsyncClient client;
/*     */   private final BiFunction<HttpMethod, URI, ? extends HttpClientContext> contextProvider;
/*  59 */   private DataBufferFactory dataBufferFactory = (DataBufferFactory)DefaultDataBufferFactory.sharedInstance;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpComponentsClientHttpConnector() {
/*  66 */     this(HttpAsyncClients.createDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpComponentsClientHttpConnector(CloseableHttpAsyncClient client) {
/*  74 */     this(client, (method, uri) -> HttpClientContext.create());
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
/*     */   public HttpComponentsClientHttpConnector(CloseableHttpAsyncClient client, BiFunction<HttpMethod, URI, ? extends HttpClientContext> contextProvider) {
/*  87 */     Assert.notNull(client, "Client must not be null");
/*  88 */     Assert.notNull(contextProvider, "ContextProvider must not be null");
/*     */     
/*  90 */     this.contextProvider = contextProvider;
/*  91 */     this.client = client;
/*  92 */     this.client.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBufferFactory(DataBufferFactory bufferFactory) {
/* 100 */     this.dataBufferFactory = bufferFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
/* 108 */     HttpClientContext context = this.contextProvider.apply(method, uri);
/*     */     
/* 110 */     if (context.getCookieStore() == null) {
/* 111 */       context.setCookieStore((CookieStore)new BasicCookieStore());
/*     */     }
/*     */     
/* 114 */     HttpComponentsClientHttpRequest request = new HttpComponentsClientHttpRequest(method, uri, context, this.dataBufferFactory);
/*     */ 
/*     */     
/* 117 */     return ((Mono)requestCallback.apply(request)).then(Mono.defer(() -> execute(request, context)));
/*     */   }
/*     */   
/*     */   private Mono<ClientHttpResponse> execute(HttpComponentsClientHttpRequest request, HttpClientContext context) {
/* 121 */     AsyncRequestProducer requestProducer = request.toRequestProducer();
/*     */     
/* 123 */     return Mono.create(sink -> {
/*     */           ReactiveResponseConsumer reactiveResponseConsumer = new ReactiveResponseConsumer(new MonoFutureCallbackAdapter(sink, this.dataBufferFactory, context));
/*     */           this.client.execute(requestProducer, (AsyncResponseConsumer)reactiveResponseConsumer, (HttpContext)context, null);
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 133 */     this.client.close();
/*     */   }
/*     */ 
/*     */   
/*     */   private static class MonoFutureCallbackAdapter
/*     */     implements FutureCallback<Message<HttpResponse, Publisher<ByteBuffer>>>
/*     */   {
/*     */     private final MonoSink<ClientHttpResponse> sink;
/*     */     
/*     */     private final DataBufferFactory dataBufferFactory;
/*     */     
/*     */     private final HttpClientContext context;
/*     */     
/*     */     public MonoFutureCallbackAdapter(MonoSink<ClientHttpResponse> sink, DataBufferFactory dataBufferFactory, HttpClientContext context) {
/* 147 */       this.sink = sink;
/* 148 */       this.dataBufferFactory = dataBufferFactory;
/* 149 */       this.context = context;
/*     */     }
/*     */ 
/*     */     
/*     */     public void completed(Message<HttpResponse, Publisher<ByteBuffer>> result) {
/* 154 */       HttpComponentsClientHttpResponse response = new HttpComponentsClientHttpResponse(this.dataBufferFactory, result, this.context);
/*     */       
/* 156 */       this.sink.success(response);
/*     */     }
/*     */ 
/*     */     
/*     */     public void failed(Exception ex) {
/* 161 */       Throwable t = ex;
/* 162 */       if (t instanceof HttpStreamResetException) {
/* 163 */         HttpStreamResetException httpStreamResetException = (HttpStreamResetException)ex;
/* 164 */         t = httpStreamResetException.getCause();
/*     */       } 
/* 166 */       this.sink.error(t);
/*     */     }
/*     */     
/*     */     public void cancelled() {}
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/HttpComponentsClientHttpConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */