/*    */ package org.springframework.web.cors.reactive;
/*    */ 
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.web.server.ServerWebExchange;
/*    */ import org.springframework.web.server.WebFilter;
/*    */ import org.springframework.web.server.WebFilterChain;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PreFlightRequestWebFilter
/*    */   implements WebFilter
/*    */ {
/*    */   private final PreFlightRequestHandler handler;
/*    */   
/*    */   public PreFlightRequestWebFilter(PreFlightRequestHandler handler) {
/* 47 */     Assert.notNull(handler, "PreFlightRequestHandler is required");
/* 48 */     this.handler = handler;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
/* 54 */     return CorsUtils.isPreFlightRequest(exchange.getRequest()) ? this.handler
/* 55 */       .handlePreFlight(exchange) : chain.filter(exchange);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/reactive/PreFlightRequestWebFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */