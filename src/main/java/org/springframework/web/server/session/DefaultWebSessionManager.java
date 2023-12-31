/*     */ package org.springframework.web.server.session;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.web.server.ServerWebExchange;
/*     */ import org.springframework.web.server.WebSession;
/*     */ import reactor.core.publisher.Flux;
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
/*     */ public class DefaultWebSessionManager
/*     */   implements WebSessionManager
/*     */ {
/*  41 */   private static final Log logger = LogFactory.getLog(DefaultWebSessionManager.class);
/*     */ 
/*     */   
/*  44 */   private WebSessionIdResolver sessionIdResolver = new CookieWebSessionIdResolver();
/*     */   
/*  46 */   private WebSessionStore sessionStore = new InMemoryWebSessionStore();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSessionIdResolver(WebSessionIdResolver sessionIdResolver) {
/*  55 */     Assert.notNull(sessionIdResolver, "WebSessionIdResolver is required");
/*  56 */     this.sessionIdResolver = sessionIdResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSessionIdResolver getSessionIdResolver() {
/*  63 */     return this.sessionIdResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSessionStore(WebSessionStore sessionStore) {
/*  72 */     Assert.notNull(sessionStore, "WebSessionStore is required");
/*  73 */     this.sessionStore = sessionStore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSessionStore getSessionStore() {
/*  80 */     return this.sessionStore;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Mono<WebSession> getSession(ServerWebExchange exchange) {
/*  86 */     return Mono.defer(() -> retrieveSession(exchange).switchIfEmpty(createWebSession()).doOnNext(()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Mono<WebSession> createWebSession() {
/*  92 */     Mono<WebSession> session = this.sessionStore.createWebSession();
/*  93 */     if (logger.isDebugEnabled()) {
/*  94 */       session = session.doOnNext(s -> logger.debug("Created new WebSession."));
/*     */     }
/*  96 */     return session;
/*     */   }
/*     */   
/*     */   private Mono<WebSession> retrieveSession(ServerWebExchange exchange) {
/* 100 */     return Flux.fromIterable(getSessionIdResolver().resolveSessionIds(exchange))
/* 101 */       .concatMap(this.sessionStore::retrieveSession)
/* 102 */       .next();
/*     */   }
/*     */   
/*     */   private Mono<Void> save(ServerWebExchange exchange, WebSession session) {
/* 106 */     List<String> ids = getSessionIdResolver().resolveSessionIds(exchange);
/*     */     
/* 108 */     if (!session.isStarted() || session.isExpired()) {
/* 109 */       if (!ids.isEmpty()) {
/*     */         
/* 111 */         if (logger.isDebugEnabled()) {
/* 112 */           logger.debug("WebSession expired or has been invalidated");
/*     */         }
/* 114 */         this.sessionIdResolver.expireSession(exchange);
/*     */       } 
/* 116 */       return Mono.empty();
/*     */     } 
/*     */     
/* 119 */     if (ids.isEmpty() || !session.getId().equals(ids.get(0))) {
/* 120 */       this.sessionIdResolver.setSessionId(exchange, session.getId());
/*     */     }
/*     */     
/* 123 */     return session.save();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/session/DefaultWebSessionManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */