/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.apache.hc.client5.http.cookie.Cookie;
/*     */ import org.apache.hc.client5.http.protocol.HttpClientContext;
/*     */ import org.apache.hc.core5.http.HttpMessage;
/*     */ import org.apache.hc.core5.http.HttpResponse;
/*     */ import org.apache.hc.core5.http.Message;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.reactivestreams.Subscription;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.ResponseCookie;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import reactor.core.publisher.Flux;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class HttpComponentsClientHttpResponse
/*     */   implements ClientHttpResponse
/*     */ {
/*     */   private final DataBufferFactory dataBufferFactory;
/*     */   private final Message<HttpResponse, Publisher<ByteBuffer>> message;
/*     */   private final HttpHeaders headers;
/*     */   private final HttpClientContext context;
/*  55 */   private final AtomicBoolean rejectSubscribers = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpComponentsClientHttpResponse(DataBufferFactory dataBufferFactory, Message<HttpResponse, Publisher<ByteBuffer>> message, HttpClientContext context) {
/*  61 */     this.dataBufferFactory = dataBufferFactory;
/*  62 */     this.message = message;
/*  63 */     this.context = context;
/*     */     
/*  65 */     MultiValueMap<String, String> adapter = new HttpComponentsHeadersAdapter((HttpMessage)message.getHead());
/*  66 */     this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpStatus getStatusCode() {
/*  72 */     return HttpStatus.valueOf(((HttpResponse)this.message.getHead()).getCode());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRawStatusCode() {
/*  77 */     return ((HttpResponse)this.message.getHead()).getCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, ResponseCookie> getCookies() {
/*  82 */     LinkedMultiValueMap<String, ResponseCookie> result = new LinkedMultiValueMap();
/*  83 */     this.context.getCookieStore().getCookies().forEach(cookie -> result.add(cookie.getName(), ResponseCookie.fromClientResponse(cookie.getName(), cookie.getValue()).domain(cookie.getDomain()).path(cookie.getPath()).maxAge(getMaxAgeSeconds(cookie)).secure(cookie.isSecure()).httpOnly(cookie.containsAttribute("httponly")).sameSite(cookie.getAttribute("samesite")).build()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     return (MultiValueMap<String, ResponseCookie>)result;
/*     */   }
/*     */   
/*     */   private long getMaxAgeSeconds(Cookie cookie) {
/*  97 */     String maxAgeAttribute = cookie.getAttribute("max-age");
/*  98 */     return (maxAgeAttribute != null) ? Long.parseLong(maxAgeAttribute) : -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public Flux<DataBuffer> getBody() {
/* 103 */     return Flux.from((Publisher)this.message.getBody())
/* 104 */       .doOnSubscribe(s -> {
/*     */           
/*     */           if (!this.rejectSubscribers.compareAndSet(false, true)) {
/*     */             throw new IllegalStateException("The client response body can only be consumed once.");
/*     */           }
/* 109 */         }).map(this.dataBufferFactory::wrap);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/* 114 */     return this.headers;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/HttpComponentsClientHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */