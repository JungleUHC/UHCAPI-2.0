/*    */ package org.springframework.web.server.handler;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.springframework.http.HttpMethod;
/*    */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*    */ import org.springframework.util.StringUtils;
/*    */ import org.springframework.web.server.ServerWebExchange;
/*    */ import org.springframework.web.server.WebExceptionHandler;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExceptionHandlingWebHandler
/*    */   extends WebHandlerDecorator
/*    */ {
/*    */   private final List<WebExceptionHandler> exceptionHandlers;
/*    */   
/*    */   public ExceptionHandlingWebHandler(WebHandler delegate, List<WebExceptionHandler> handlers) {
/* 50 */     super(delegate);
/* 51 */     List<WebExceptionHandler> handlersToUse = new ArrayList<>();
/* 52 */     handlersToUse.add(new CheckpointInsertingHandler());
/* 53 */     handlersToUse.addAll(handlers);
/* 54 */     this.exceptionHandlers = Collections.unmodifiableList(handlersToUse);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<WebExceptionHandler> getExceptionHandlers() {
/* 62 */     return this.exceptionHandlers;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Mono<Void> handle(ServerWebExchange exchange) {
/*    */     Mono<Void> completion;
/*    */     try {
/* 70 */       completion = super.handle(exchange);
/*    */     }
/* 72 */     catch (Throwable ex) {
/* 73 */       completion = Mono.error(ex);
/*    */     } 
/*    */     
/* 76 */     for (WebExceptionHandler handler : this.exceptionHandlers) {
/* 77 */       completion = completion.onErrorResume(ex -> handler.handle(exchange, ex));
/*    */     }
/* 79 */     return completion;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static class CheckpointInsertingHandler
/*    */     implements WebExceptionHandler
/*    */   {
/*    */     private CheckpointInsertingHandler() {}
/*    */ 
/*    */ 
/*    */     
/*    */     public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
/* 93 */       ServerHttpRequest request = exchange.getRequest();
/* 94 */       String rawQuery = request.getURI().getRawQuery();
/* 95 */       String query = StringUtils.hasText(rawQuery) ? ("?" + rawQuery) : "";
/* 96 */       HttpMethod httpMethod = request.getMethod();
/* 97 */       String description = "HTTP " + httpMethod + " \"" + request.getPath() + query + "\"";
/* 98 */       return Mono.error(ex).checkpoint(description + " [ExceptionHandlingWebHandler]");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/handler/ExceptionHandlingWebHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */