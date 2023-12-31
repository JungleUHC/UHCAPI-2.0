/*     */ package org.springframework.web.server.handler;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.web.server.ResponseStatusException;
/*     */ import org.springframework.web.server.ServerWebExchange;
/*     */ import org.springframework.web.server.WebExceptionHandler;
/*     */ import reactor.core.publisher.Mono;
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
/*     */ public class ResponseStatusExceptionHandler
/*     */   implements WebExceptionHandler
/*     */ {
/*  45 */   private static final Log logger = LogFactory.getLog(ResponseStatusExceptionHandler.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Log warnLogger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWarnLogCategory(String loggerName) {
/*  61 */     this.warnLogger = LogFactory.getLog(loggerName);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
/*  67 */     if (!updateResponse(exchange.getResponse(), ex)) {
/*  68 */       return Mono.error(ex);
/*     */     }
/*     */ 
/*     */     
/*  72 */     String logPrefix = exchange.getLogPrefix();
/*  73 */     if (this.warnLogger != null && this.warnLogger.isWarnEnabled()) {
/*  74 */       this.warnLogger.warn(logPrefix + formatError(ex, exchange.getRequest()));
/*     */     }
/*  76 */     else if (logger.isDebugEnabled()) {
/*  77 */       logger.debug(logPrefix + formatError(ex, exchange.getRequest()));
/*     */     } 
/*     */     
/*  80 */     return exchange.getResponse().setComplete();
/*     */   }
/*     */ 
/*     */   
/*     */   private String formatError(Throwable ex, ServerHttpRequest request) {
/*  85 */     String className = ex.getClass().getSimpleName();
/*  86 */     String message = LogFormatUtils.formatValue(ex.getMessage(), -1, true);
/*  87 */     String path = request.getURI().getRawPath();
/*  88 */     return "Resolved [" + className + ": " + message + "] for HTTP " + request.getMethod() + " " + path;
/*     */   }
/*     */   
/*     */   private boolean updateResponse(ServerHttpResponse response, Throwable ex) {
/*  92 */     boolean result = false;
/*  93 */     HttpStatus httpStatus = determineStatus(ex);
/*  94 */     int code = (httpStatus != null) ? httpStatus.value() : determineRawStatusCode(ex);
/*  95 */     if (code != -1) {
/*  96 */       if (response.setRawStatusCode(Integer.valueOf(code))) {
/*  97 */         if (ex instanceof ResponseStatusException) {
/*  98 */           ((ResponseStatusException)ex).getResponseHeaders()
/*  99 */             .forEach((name, values) -> values.forEach(()));
/*     */         }
/*     */         
/* 102 */         result = true;
/*     */       } 
/*     */     } else {
/*     */       
/* 106 */       Throwable cause = ex.getCause();
/* 107 */       if (cause != null) {
/* 108 */         result = updateResponse(response, cause);
/*     */       }
/*     */     } 
/* 111 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   @Deprecated
/*     */   protected HttpStatus determineStatus(Throwable ex) {
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int determineRawStatusCode(Throwable ex) {
/* 135 */     if (ex instanceof ResponseStatusException) {
/* 136 */       return ((ResponseStatusException)ex).getRawStatusCode();
/*     */     }
/* 138 */     return -1;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/handler/ResponseStatusExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */