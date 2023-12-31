/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.channel.ChannelId;
/*     */ import java.nio.file.Path;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.io.buffer.NettyDataBufferFactory;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.ResponseCookie;
/*     */ import org.springframework.http.ZeroCopyHttpOutputMessage;
/*     */ import org.springframework.util.Assert;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.netty.ChannelOperationsId;
/*     */ import reactor.netty.Connection;
/*     */ import reactor.netty.http.server.HttpServerResponse;
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
/*     */ class ReactorServerHttpResponse
/*     */   extends AbstractServerHttpResponse
/*     */   implements ZeroCopyHttpOutputMessage
/*     */ {
/*  51 */   private static final Log logger = LogFactory.getLog(ReactorServerHttpResponse.class);
/*     */ 
/*     */   
/*     */   private final HttpServerResponse response;
/*     */ 
/*     */   
/*     */   public ReactorServerHttpResponse(HttpServerResponse response, DataBufferFactory bufferFactory) {
/*  58 */     super(bufferFactory, new HttpHeaders(new NettyHeadersAdapter(response.responseHeaders())));
/*  59 */     Assert.notNull(response, "HttpServerResponse must not be null");
/*  60 */     this.response = response;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeResponse() {
/*  67 */     return (T)this.response;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpStatus getStatusCode() {
/*  72 */     HttpStatus status = super.getStatusCode();
/*  73 */     return (status != null) ? status : HttpStatus.resolve(this.response.status().code());
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getRawStatusCode() {
/*  78 */     Integer status = super.getRawStatusCode();
/*  79 */     return Integer.valueOf((status != null) ? status.intValue() : this.response.status().code());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyStatusCode() {
/*  84 */     Integer status = super.getRawStatusCode();
/*  85 */     if (status != null) {
/*  86 */       this.response.status(status.intValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> publisher) {
/*  92 */     return this.response.send(toByteBufs(publisher)).then();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> publisher) {
/*  97 */     return this.response.sendGroups((Publisher)Flux.from(publisher).map(this::toByteBufs)).then();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyHeaders() {}
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyCookies() {
/* 108 */     for (List<ResponseCookie> cookies : (Iterable<List<ResponseCookie>>)getCookies().values()) {
/* 109 */       for (ResponseCookie cookie : cookies) {
/* 110 */         this.response.addHeader("Set-Cookie", cookie.toString());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> writeWith(Path file, long position, long count) {
/* 117 */     return doCommit(() -> this.response.sendFile(file, position, count).then());
/*     */   }
/*     */   
/*     */   private Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
/* 121 */     return (dataBuffers instanceof Mono) ? 
/* 122 */       (Publisher<ByteBuf>)Mono.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf) : 
/* 123 */       (Publisher<ByteBuf>)Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void touchDataBuffer(DataBuffer buffer) {
/* 128 */     if (logger.isDebugEnabled()) {
/* 129 */       if (ReactorServerHttpRequest.reactorNettyRequestChannelOperationsIdPresent && 
/* 130 */         ChannelOperationsIdHelper.touch(buffer, this.response)) {
/*     */         return;
/*     */       }
/*     */       
/* 134 */       this.response.withConnection(connection -> {
/*     */             ChannelId id = connection.channel().id();
/*     */             DataBufferUtils.touch(buffer, "Channel id: " + id.asShortText());
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ChannelOperationsIdHelper
/*     */   {
/*     */     public static boolean touch(DataBuffer dataBuffer, HttpServerResponse response) {
/* 145 */       if (response instanceof ChannelOperationsId) {
/* 146 */         String id = ((ChannelOperationsId)response).asLongText();
/* 147 */         DataBufferUtils.touch(dataBuffer, "Channel id: " + id);
/* 148 */         return true;
/*     */       } 
/* 150 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/ReactorServerHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */