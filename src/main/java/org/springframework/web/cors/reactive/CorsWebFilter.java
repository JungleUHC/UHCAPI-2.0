/*    */ package org.springframework.web.cors.reactive;
/*    */ 
/*    */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.web.cors.CorsConfiguration;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CorsWebFilter
/*    */   implements WebFilter
/*    */ {
/*    */   private final CorsConfigurationSource configSource;
/*    */   private final CorsProcessor processor;
/*    */   
/*    */   public CorsWebFilter(CorsConfigurationSource configSource) {
/* 57 */     this(configSource, new DefaultCorsProcessor());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CorsWebFilter(CorsConfigurationSource configSource, CorsProcessor processor) {
/* 68 */     Assert.notNull(configSource, "CorsConfigurationSource must not be null");
/* 69 */     Assert.notNull(processor, "CorsProcessor must not be null");
/* 70 */     this.configSource = configSource;
/* 71 */     this.processor = processor;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
/* 77 */     ServerHttpRequest request = exchange.getRequest();
/* 78 */     CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(exchange);
/* 79 */     boolean isValid = this.processor.process(corsConfiguration, exchange);
/* 80 */     if (!isValid || CorsUtils.isPreFlightRequest(request)) {
/* 81 */       return Mono.empty();
/*    */     }
/* 83 */     return chain.filter(exchange);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/reactive/CorsWebFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */