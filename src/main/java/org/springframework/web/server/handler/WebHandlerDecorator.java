/*    */ package org.springframework.web.server.handler;
/*    */ 
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.web.server.ServerWebExchange;
/*    */ import org.springframework.web.server.WebHandler;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WebHandlerDecorator
/*    */   implements WebHandler
/*    */ {
/*    */   private final WebHandler delegate;
/*    */   
/*    */   public WebHandlerDecorator(WebHandler delegate) {
/* 41 */     Assert.notNull(delegate, "'delegate' must not be null");
/* 42 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WebHandler getDelegate() {
/* 50 */     return this.delegate;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Mono<Void> handle(ServerWebExchange exchange) {
/* 56 */     return this.delegate.handle(exchange);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     return getClass().getSimpleName() + " [delegate=" + this.delegate + "]";
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/handler/WebHandlerDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */