/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.buffer.ByteBufAllocator;
/*     */ import io.netty.handler.codec.http.cookie.Cookie;
/*     */ import io.netty.handler.codec.http.cookie.DefaultCookie;
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.reactivestreams.Subscription;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.NettyDataBufferFactory;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.ResponseCookie;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.netty.ChannelOperationsId;
/*     */ import reactor.netty.Connection;
/*     */ import reactor.netty.NettyInbound;
/*     */ import reactor.netty.http.client.HttpClientResponse;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ReactorClientHttpResponse
/*     */   implements ClientHttpResponse
/*     */ {
/*  57 */   static final boolean reactorNettyRequestChannelOperationsIdPresent = ClassUtils.isPresent("reactor.netty.ChannelOperationsId", ReactorClientHttpResponse.class
/*  58 */       .getClassLoader());
/*     */ 
/*     */   
/*  61 */   private static final Log logger = LogFactory.getLog(ReactorClientHttpResponse.class);
/*     */ 
/*     */   
/*     */   private final HttpClientResponse response;
/*     */   
/*     */   private final HttpHeaders headers;
/*     */   
/*     */   private final NettyInbound inbound;
/*     */   
/*     */   private final NettyDataBufferFactory bufferFactory;
/*     */   
/*  72 */   private final AtomicInteger state = new AtomicInteger();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReactorClientHttpResponse(HttpClientResponse response, Connection connection) {
/*  81 */     this.response = response;
/*  82 */     MultiValueMap<String, String> adapter = new NettyHeadersAdapter(response.responseHeaders());
/*  83 */     this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
/*  84 */     this.inbound = connection.inbound();
/*  85 */     this.bufferFactory = new NettyDataBufferFactory(connection.outbound().alloc());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ReactorClientHttpResponse(HttpClientResponse response, NettyInbound inbound, ByteBufAllocator alloc) {
/*  94 */     this.response = response;
/*  95 */     MultiValueMap<String, String> adapter = new NettyHeadersAdapter(response.responseHeaders());
/*  96 */     this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
/*  97 */     this.inbound = inbound;
/*  98 */     this.bufferFactory = new NettyDataBufferFactory(alloc);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getId() {
/* 104 */     String id = null;
/* 105 */     if (reactorNettyRequestChannelOperationsIdPresent) {
/* 106 */       id = ChannelOperationsIdHelper.getId(this.response);
/*     */     }
/* 108 */     if (id == null && this.response instanceof Connection) {
/* 109 */       id = ((Connection)this.response).channel().id().asShortText();
/*     */     }
/* 111 */     return (id != null) ? id : ObjectUtils.getIdentityHexString(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public Flux<DataBuffer> getBody() {
/* 116 */     return this.inbound.receive()
/* 117 */       .doOnSubscribe(s -> {
/*     */           if (this.state.compareAndSet(0, 1)) {
/*     */             return;
/*     */           }
/*     */ 
/*     */           
/*     */           if (this.state.get() == 2) {
/*     */             throw new IllegalStateException("The client response body has been released already due to cancellation.");
/*     */           }
/* 126 */         }).map(byteBuf -> {
/*     */           byteBuf.retain();
/*     */           return (DataBuffer)this.bufferFactory.wrap(byteBuf);
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/* 134 */     return this.headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpStatus getStatusCode() {
/* 139 */     return HttpStatus.valueOf(getRawStatusCode());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRawStatusCode() {
/* 144 */     return this.response.status().code();
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, ResponseCookie> getCookies() {
/* 149 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/* 150 */     this.response.cookies().values().stream()
/* 151 */       .flatMap(Collection::stream)
/* 152 */       .forEach(cookie -> result.add(cookie.name(), ResponseCookie.fromClientResponse(cookie.name(), cookie.value()).domain(cookie.domain()).path(cookie.path()).maxAge(cookie.maxAge()).secure(cookie.isSecure()).httpOnly(cookie.isHttpOnly()).sameSite(getSameSite(cookie)).build()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 161 */     return CollectionUtils.unmodifiableMultiValueMap((MultiValueMap)linkedMultiValueMap);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String getSameSite(Cookie cookie) {
/* 166 */     if (cookie instanceof DefaultCookie) {
/* 167 */       DefaultCookie defaultCookie = (DefaultCookie)cookie;
/* 168 */       if (defaultCookie.sameSite() != null) {
/* 169 */         return defaultCookie.sameSite().name();
/*     */       }
/*     */     } 
/* 172 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void releaseAfterCancel(HttpMethod method) {
/* 183 */     if (mayHaveBody(method) && this.state.compareAndSet(0, 2)) {
/* 184 */       if (logger.isDebugEnabled()) {
/* 185 */         logger.debug("[" + getId() + "]Releasing body, not yet subscribed.");
/*     */       }
/* 187 */       this.inbound.receive().doOnNext(byteBuf -> {  }).subscribe(byteBuf -> {
/*     */           
/*     */           }ex -> {
/*     */           
/*     */           });
/* 192 */     }  } private boolean mayHaveBody(HttpMethod method) { int code = getRawStatusCode();
/* 193 */     return ((code < 100 || code >= 200) && code != 204 && code != 205 && 
/* 194 */       !method.equals(HttpMethod.HEAD) && getHeaders().getContentLength() != 0L); }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 199 */     return "ReactorClientHttpResponse{request=[" + this.response
/* 200 */       .method().name() + " " + this.response.uri() + "],status=" + 
/* 201 */       getRawStatusCode() + '}';
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ChannelOperationsIdHelper
/*     */   {
/*     */     @Nullable
/*     */     public static String getId(HttpClientResponse response) {
/* 209 */       if (response instanceof ChannelOperationsId) {
/* 210 */         return ReactorClientHttpResponse.logger.isDebugEnabled() ? ((ChannelOperationsId)response)
/* 211 */           .asLongText() : ((ChannelOperationsId)response)
/* 212 */           .asShortText();
/*     */       }
/* 214 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/ReactorClientHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */