/*    */ package org.springframework.http.server.reactive;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ import org.reactivestreams.Processor;
/*    */ import org.reactivestreams.Publisher;
/*    */ import org.reactivestreams.Subscriber;
/*    */ import org.springframework.core.io.buffer.DataBuffer;
/*    */ import org.springframework.core.io.buffer.DataBufferFactory;
/*    */ import org.springframework.http.HttpHeaders;
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
/*    */ public abstract class AbstractListenerServerHttpResponse
/*    */   extends AbstractServerHttpResponse
/*    */ {
/* 38 */   private final AtomicBoolean writeCalled = new AtomicBoolean();
/*    */ 
/*    */   
/*    */   public AbstractListenerServerHttpResponse(DataBufferFactory bufferFactory) {
/* 42 */     super(bufferFactory);
/*    */   }
/*    */   
/*    */   public AbstractListenerServerHttpResponse(DataBufferFactory bufferFactory, HttpHeaders headers) {
/* 46 */     super(bufferFactory, headers);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected final Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body) {
/* 52 */     return writeAndFlushWithInternal((Publisher<? extends Publisher<? extends DataBuffer>>)Mono.just(body));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected final Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body) {
/* 59 */     if (!this.writeCalled.compareAndSet(false, true)) {
/* 60 */       return Mono.error(new IllegalStateException("writeWith() or writeAndFlushWith() has already been called"));
/*    */     }
/*    */     
/* 63 */     Processor<? super Publisher<? extends DataBuffer>, Void> processor = createBodyFlushProcessor();
/* 64 */     return Mono.from(subscriber -> {
/*    */           body.subscribe((Subscriber)processor);
/*    */           processor.subscribe(subscriber);
/*    */         });
/*    */   }
/*    */   
/*    */   protected abstract Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor();
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/AbstractListenerServerHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */