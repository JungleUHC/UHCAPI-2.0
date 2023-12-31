/*    */ package org.springframework.web.filter.reactive;
/*    */ 
/*    */ import java.util.Optional;
/*    */ import org.springframework.web.server.ServerWebExchange;
/*    */ import org.springframework.web.server.WebFilter;
/*    */ import org.springframework.web.server.WebFilterChain;
/*    */ import reactor.core.publisher.Mono;
/*    */ import reactor.util.context.Context;
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
/*    */ public class ServerWebExchangeContextFilter
/*    */   implements WebFilter
/*    */ {
/* 43 */   public static final String EXCHANGE_CONTEXT_ATTRIBUTE = ServerWebExchangeContextFilter.class
/* 44 */     .getName() + ".EXCHANGE_CONTEXT";
/*    */ 
/*    */ 
/*    */   
/*    */   public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
/* 49 */     return chain.filter(exchange)
/* 50 */       .contextWrite(cxt -> cxt.put(EXCHANGE_CONTEXT_ATTRIBUTE, exchange));
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
/*    */   public static Optional<ServerWebExchange> get(Context context) {
/* 62 */     return context.getOrEmpty(EXCHANGE_CONTEXT_ATTRIBUTE);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/reactive/ServerWebExchangeContextFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */