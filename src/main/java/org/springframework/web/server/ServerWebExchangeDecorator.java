/*     */ package org.springframework.web.server;
/*     */ 
/*     */ import java.time.Instant;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
/*     */ import org.springframework.context.ApplicationContext;
/*     */ import org.springframework.context.i18n.LocaleContext;
/*     */ import org.springframework.http.codec.multipart.Part;
/*     */ import org.springframework.http.server.reactive.ServerHttpRequest;
/*     */ import org.springframework.http.server.reactive.ServerHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerWebExchangeDecorator
/*     */   implements ServerWebExchange
/*     */ {
/*     */   private final ServerWebExchange delegate;
/*     */   
/*     */   protected ServerWebExchangeDecorator(ServerWebExchange delegate) {
/*  55 */     Assert.notNull(delegate, "ServerWebExchange 'delegate' is required.");
/*  56 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerWebExchange getDelegate() {
/*  61 */     return this.delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerHttpRequest getRequest() {
/*  68 */     return getDelegate().getRequest();
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerHttpResponse getResponse() {
/*  73 */     return getDelegate().getResponse();
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, Object> getAttributes() {
/*  78 */     return getDelegate().getAttributes();
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<WebSession> getSession() {
/*  83 */     return getDelegate().getSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends java.security.Principal> Mono<T> getPrincipal() {
/*  88 */     return getDelegate().getPrincipal();
/*     */   }
/*     */ 
/*     */   
/*     */   public LocaleContext getLocaleContext() {
/*  93 */     return getDelegate().getLocaleContext();
/*     */   }
/*     */ 
/*     */   
/*     */   public ApplicationContext getApplicationContext() {
/*  98 */     return getDelegate().getApplicationContext();
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<MultiValueMap<String, String>> getFormData() {
/* 103 */     return getDelegate().getFormData();
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<MultiValueMap<String, Part>> getMultipartData() {
/* 108 */     return getDelegate().getMultipartData();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNotModified() {
/* 113 */     return getDelegate().isNotModified();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkNotModified(Instant lastModified) {
/* 118 */     return getDelegate().checkNotModified(lastModified);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkNotModified(String etag) {
/* 123 */     return getDelegate().checkNotModified(etag);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkNotModified(@Nullable String etag, Instant lastModified) {
/* 128 */     return getDelegate().checkNotModified(etag, lastModified);
/*     */   }
/*     */ 
/*     */   
/*     */   public String transformUrl(String url) {
/* 133 */     return getDelegate().transformUrl(url);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addUrlTransformer(Function<String, String> transformer) {
/* 138 */     getDelegate().addUrlTransformer(transformer);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLogPrefix() {
/* 143 */     return getDelegate().getLogPrefix();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 148 */     return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/ServerWebExchangeDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */