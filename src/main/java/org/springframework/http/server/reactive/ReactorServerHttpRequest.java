/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import io.netty.handler.codec.http.cookie.Cookie;
/*     */ import io.netty.handler.ssl.SslHandler;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.NettyDataBufferFactory;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpLogging;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.netty.ChannelOperationsId;
/*     */ import reactor.netty.Connection;
/*     */ import reactor.netty.http.server.HttpServerRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ReactorServerHttpRequest
/*     */   extends AbstractServerHttpRequest
/*     */ {
/*  55 */   static final boolean reactorNettyRequestChannelOperationsIdPresent = ClassUtils.isPresent("reactor.netty.ChannelOperationsId", ReactorServerHttpRequest.class
/*  56 */       .getClassLoader());
/*     */   
/*  58 */   private static final Log logger = HttpLogging.forLogName(ReactorServerHttpRequest.class);
/*     */ 
/*     */   
/*  61 */   private static final AtomicLong logPrefixIndex = new AtomicLong();
/*     */ 
/*     */   
/*     */   private final HttpServerRequest request;
/*     */ 
/*     */   
/*     */   private final NettyDataBufferFactory bufferFactory;
/*     */ 
/*     */ 
/*     */   
/*     */   public ReactorServerHttpRequest(HttpServerRequest request, NettyDataBufferFactory bufferFactory) throws URISyntaxException {
/*  72 */     super(initUri(request), "", new NettyHeadersAdapter(request.requestHeaders()));
/*  73 */     Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
/*  74 */     this.request = request;
/*  75 */     this.bufferFactory = bufferFactory;
/*     */   }
/*     */   
/*     */   private static URI initUri(HttpServerRequest request) throws URISyntaxException {
/*  79 */     Assert.notNull(request, "HttpServerRequest must not be null");
/*  80 */     return new URI(resolveBaseUrl(request) + resolveRequestUri(request));
/*     */   }
/*     */   
/*     */   private static URI resolveBaseUrl(HttpServerRequest request) throws URISyntaxException {
/*  84 */     String scheme = getScheme(request);
/*  85 */     String header = request.requestHeaders().get((CharSequence)HttpHeaderNames.HOST);
/*  86 */     if (header != null) {
/*     */       int portIndex;
/*  88 */       if (header.startsWith("[")) {
/*  89 */         portIndex = header.indexOf(':', header.indexOf(']'));
/*     */       } else {
/*     */         
/*  92 */         portIndex = header.indexOf(':');
/*     */       } 
/*  94 */       if (portIndex != -1) {
/*     */         try {
/*  96 */           return new URI(scheme, null, header.substring(0, portIndex), 
/*  97 */               Integer.parseInt(header.substring(portIndex + 1)), null, null, null);
/*     */         }
/*  99 */         catch (NumberFormatException ex) {
/* 100 */           throw new URISyntaxException(header, "Unable to parse port", portIndex);
/*     */         } 
/*     */       }
/*     */       
/* 104 */       return new URI(scheme, header, null, null);
/*     */     } 
/*     */ 
/*     */     
/* 108 */     InetSocketAddress localAddress = request.hostAddress();
/* 109 */     Assert.state((localAddress != null), "No host address available");
/* 110 */     return new URI(scheme, null, localAddress.getHostString(), localAddress
/* 111 */         .getPort(), null, null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   private static String getScheme(HttpServerRequest request) {
/* 116 */     return request.scheme();
/*     */   }
/*     */   
/*     */   private static String resolveRequestUri(HttpServerRequest request) {
/* 120 */     String uri = request.uri();
/* 121 */     for (int i = 0; i < uri.length(); i++) {
/* 122 */       char c = uri.charAt(i);
/* 123 */       if (c == '/' || c == '?' || c == '#') {
/*     */         break;
/*     */       }
/* 126 */       if (c == ':' && i + 2 < uri.length() && 
/* 127 */         uri.charAt(i + 1) == '/' && uri.charAt(i + 2) == '/') {
/* 128 */         for (int j = i + 3; j < uri.length(); j++) {
/* 129 */           c = uri.charAt(j);
/* 130 */           if (c == '/' || c == '?' || c == '#') {
/* 131 */             return uri.substring(j);
/*     */           }
/*     */         } 
/* 134 */         return "";
/*     */       } 
/*     */     } 
/*     */     
/* 138 */     return uri;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMethodValue() {
/* 144 */     return this.request.method().name();
/*     */   }
/*     */ 
/*     */   
/*     */   protected MultiValueMap<String, HttpCookie> initCookies() {
/* 149 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/* 150 */     for (CharSequence name : this.request.cookies().keySet()) {
/* 151 */       for (Cookie cookie : this.request.cookies().get(name)) {
/* 152 */         HttpCookie httpCookie = new HttpCookie(name.toString(), cookie.value());
/* 153 */         linkedMultiValueMap.add(name.toString(), httpCookie);
/*     */       } 
/*     */     } 
/* 156 */     return (MultiValueMap<String, HttpCookie>)linkedMultiValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public InetSocketAddress getLocalAddress() {
/* 162 */     return this.request.hostAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public InetSocketAddress getRemoteAddress() {
/* 168 */     return this.request.remoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected SslInfo initSslInfo() {
/* 174 */     Channel channel = ((Connection)this.request).channel();
/* 175 */     SslHandler sslHandler = (SslHandler)channel.pipeline().get(SslHandler.class);
/* 176 */     if (sslHandler == null && channel.parent() != null) {
/* 177 */       sslHandler = (SslHandler)channel.parent().pipeline().get(SslHandler.class);
/*     */     }
/* 179 */     if (sslHandler != null) {
/* 180 */       SSLSession session = sslHandler.engine().getSession();
/* 181 */       return new DefaultSslInfo(session);
/*     */     } 
/* 183 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Flux<DataBuffer> getBody() {
/* 188 */     return this.request.receive().retain().map(this.bufferFactory::wrap);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeRequest() {
/* 194 */     return (T)this.request;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String initId() {
/* 200 */     if (this.request instanceof Connection) {
/* 201 */       return ((Connection)this.request).channel().id().asShortText() + "-" + logPrefixIndex
/* 202 */         .incrementAndGet();
/*     */     }
/* 204 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String initLogPrefix() {
/* 209 */     if (reactorNettyRequestChannelOperationsIdPresent) {
/* 210 */       String id = ChannelOperationsIdHelper.getId(this.request);
/* 211 */       if (id != null) {
/* 212 */         return id;
/*     */       }
/*     */     } 
/* 215 */     if (this.request instanceof Connection) {
/* 216 */       return ((Connection)this.request).channel().id().asShortText() + "-" + logPrefixIndex
/* 217 */         .incrementAndGet();
/*     */     }
/* 219 */     return getId();
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ChannelOperationsIdHelper
/*     */   {
/*     */     @Nullable
/*     */     public static String getId(HttpServerRequest request) {
/* 227 */       if (request instanceof ChannelOperationsId) {
/* 228 */         return ReactorServerHttpRequest.logger.isDebugEnabled() ? ((ChannelOperationsId)request)
/* 229 */           .asLongText() : ((ChannelOperationsId)request)
/* 230 */           .asShortText();
/*     */       }
/* 232 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/ReactorServerHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */