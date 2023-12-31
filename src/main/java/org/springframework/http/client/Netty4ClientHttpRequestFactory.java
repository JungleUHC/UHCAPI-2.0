/*     */ package org.springframework.http.client;
/*     */ 
/*     */ import io.netty.bootstrap.Bootstrap;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.ChannelInitializer;
/*     */ import io.netty.channel.ChannelPipeline;
/*     */ import io.netty.channel.EventLoopGroup;
/*     */ import io.netty.channel.nio.NioEventLoopGroup;
/*     */ import io.netty.channel.socket.SocketChannel;
/*     */ import io.netty.channel.socket.SocketChannelConfig;
/*     */ import io.netty.channel.socket.nio.NioSocketChannel;
/*     */ import io.netty.handler.codec.http.HttpClientCodec;
/*     */ import io.netty.handler.codec.http.HttpObjectAggregator;
/*     */ import io.netty.handler.ssl.SslContext;
/*     */ import io.netty.handler.ssl.SslContextBuilder;
/*     */ import io.netty.handler.timeout.ReadTimeoutHandler;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.net.ssl.SSLException;
/*     */ import org.springframework.beans.factory.DisposableBean;
/*     */ import org.springframework.beans.factory.InitializingBean;
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
/*     */ public class Netty4ClientHttpRequestFactory
/*     */   implements ClientHttpRequestFactory, AsyncClientHttpRequestFactory, InitializingBean, DisposableBean
/*     */ {
/*     */   public static final int DEFAULT_MAX_RESPONSE_SIZE = 10485760;
/*     */   private final EventLoopGroup eventLoopGroup;
/*     */   private final boolean defaultEventLoopGroup;
/*  79 */   private int maxResponseSize = 10485760;
/*     */   
/*     */   @Nullable
/*     */   private SslContext sslContext;
/*     */   
/*  84 */   private int connectTimeout = -1;
/*     */   
/*  86 */   private int readTimeout = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private volatile Bootstrap bootstrap;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Netty4ClientHttpRequestFactory() {
/*  97 */     int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
/*  98 */     this.eventLoopGroup = (EventLoopGroup)new NioEventLoopGroup(ioWorkerCount);
/*  99 */     this.defaultEventLoopGroup = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Netty4ClientHttpRequestFactory(EventLoopGroup eventLoopGroup) {
/* 110 */     Assert.notNull(eventLoopGroup, "EventLoopGroup must not be null");
/* 111 */     this.eventLoopGroup = eventLoopGroup;
/* 112 */     this.defaultEventLoopGroup = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxResponseSize(int maxResponseSize) {
/* 123 */     this.maxResponseSize = maxResponseSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSslContext(SslContext sslContext) {
/* 132 */     this.sslContext = sslContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConnectTimeout(int connectTimeout) {
/* 141 */     this.connectTimeout = connectTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadTimeout(int readTimeout) {
/* 150 */     this.readTimeout = readTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 156 */     if (this.sslContext == null) {
/* 157 */       this.sslContext = getDefaultClientSslContext();
/*     */     }
/*     */   }
/*     */   
/*     */   private SslContext getDefaultClientSslContext() {
/*     */     try {
/* 163 */       return SslContextBuilder.forClient().build();
/*     */     }
/* 165 */     catch (SSLException ex) {
/* 166 */       throw new IllegalStateException("Could not create default client SslContext", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
/* 173 */     return createRequestInternal(uri, httpMethod);
/*     */   }
/*     */ 
/*     */   
/*     */   public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
/* 178 */     return createRequestInternal(uri, httpMethod);
/*     */   }
/*     */   
/*     */   private Netty4ClientHttpRequest createRequestInternal(URI uri, HttpMethod httpMethod) {
/* 182 */     return new Netty4ClientHttpRequest(getBootstrap(uri), uri, httpMethod);
/*     */   }
/*     */   
/*     */   private Bootstrap getBootstrap(URI uri) {
/* 186 */     boolean isSecure = (uri.getPort() == 443 || "https".equalsIgnoreCase(uri.getScheme()));
/* 187 */     if (isSecure) {
/* 188 */       return buildBootstrap(uri, true);
/*     */     }
/*     */     
/* 191 */     Bootstrap bootstrap = this.bootstrap;
/* 192 */     if (bootstrap == null) {
/* 193 */       bootstrap = buildBootstrap(uri, false);
/* 194 */       this.bootstrap = bootstrap;
/*     */     } 
/* 196 */     return bootstrap;
/*     */   }
/*     */ 
/*     */   
/*     */   private Bootstrap buildBootstrap(final URI uri, final boolean isSecure) {
/* 201 */     Bootstrap bootstrap = new Bootstrap();
/* 202 */     ((Bootstrap)((Bootstrap)bootstrap.group(this.eventLoopGroup)).channel(NioSocketChannel.class))
/* 203 */       .handler((ChannelHandler)new ChannelInitializer<SocketChannel>()
/*     */         {
/*     */           protected void initChannel(SocketChannel channel) throws Exception {
/* 206 */             Netty4ClientHttpRequestFactory.this.configureChannel(channel.config());
/* 207 */             ChannelPipeline pipeline = channel.pipeline();
/* 208 */             if (isSecure) {
/* 209 */               Assert.notNull(Netty4ClientHttpRequestFactory.this.sslContext, "sslContext should not be null");
/* 210 */               pipeline.addLast(new ChannelHandler[] { (ChannelHandler)Netty4ClientHttpRequestFactory.access$000(this.this$0).newHandler(channel.alloc(), this.val$uri.getHost(), this.val$uri.getPort()) });
/*     */             } 
/* 212 */             pipeline.addLast(new ChannelHandler[] { (ChannelHandler)new HttpClientCodec() });
/* 213 */             pipeline.addLast(new ChannelHandler[] { (ChannelHandler)new HttpObjectAggregator(Netty4ClientHttpRequestFactory.access$100(this.this$0)) });
/* 214 */             if (Netty4ClientHttpRequestFactory.this.readTimeout > 0) {
/* 215 */               pipeline.addLast(new ChannelHandler[] { (ChannelHandler)new ReadTimeoutHandler(Netty4ClientHttpRequestFactory.access$200(this.this$0), TimeUnit.MILLISECONDS) });
/*     */             }
/*     */           }
/*     */         });
/*     */     
/* 220 */     return bootstrap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void configureChannel(SocketChannelConfig config) {
/* 229 */     if (this.connectTimeout >= 0) {
/* 230 */       config.setConnectTimeoutMillis(this.connectTimeout);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() throws InterruptedException {
/* 237 */     if (this.defaultEventLoopGroup)
/*     */     {
/* 239 */       this.eventLoopGroup.shutdownGracefully().sync();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/Netty4ClientHttpRequestFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */