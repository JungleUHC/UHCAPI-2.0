/*    */ package org.springframework.web.filter.reactive;
/*    */ 
/*    */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*    */ import org.springframework.web.server.ServerWebExchange;
/*    */ import org.springframework.web.server.WebFilter;
/*    */ import org.springframework.web.server.WebFilterChain;
/*    */ import org.springframework.web.server.adapter.ForwardedHeaderTransformer;
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
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class ForwardedHeaderFilter
/*    */   extends ForwardedHeaderTransformer
/*    */   implements WebFilter
/*    */ {
/*    */   public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
/* 50 */     ServerHttpRequest request = exchange.getRequest();
/* 51 */     if (hasForwardedHeaders(request)) {
/* 52 */       exchange = exchange.mutate().request(apply(request)).build();
/*    */     }
/* 54 */     return chain.filter(exchange);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/reactive/ForwardedHeaderFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */