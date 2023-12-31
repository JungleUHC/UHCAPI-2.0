/*    */ package org.springframework.http.server.reactive;
/*    */ 
/*    */ import org.reactivestreams.Publisher;
/*    */ import org.springframework.core.io.buffer.DataBuffer;
/*    */ import org.springframework.core.io.buffer.DataBufferUtils;
/*    */ import reactor.core.publisher.Flux;
/*    */ import reactor.core.publisher.Mono;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpHeadResponseDecorator
/*    */   extends ServerHttpResponseDecorator
/*    */ {
/*    */   public HttpHeadResponseDecorator(ServerHttpResponse delegate) {
/* 37 */     super(delegate);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
/* 49 */     if (shouldSetContentLength() && body instanceof Mono) {
/* 50 */       return ((Mono)body)
/* 51 */         .doOnSuccess(buffer -> {
/*    */             if (buffer != null) {
/*    */               getHeaders().setContentLength(buffer.readableByteCount());
/*    */ 
/*    */               
/*    */               DataBufferUtils.release(buffer);
/*    */             } else {
/*    */               getHeaders().setContentLength(0L);
/*    */             } 
/* 60 */           }).then();
/*    */     }
/*    */     
/* 63 */     return Flux.from(body)
/* 64 */       .doOnNext(DataBufferUtils::release)
/* 65 */       .then();
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean shouldSetContentLength() {
/* 70 */     return (getHeaders().getFirst("Content-Length") == null && 
/* 71 */       getHeaders().getFirst("Transfer-Encoding") == null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
/* 83 */     return setComplete();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/HttpHeadResponseDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */