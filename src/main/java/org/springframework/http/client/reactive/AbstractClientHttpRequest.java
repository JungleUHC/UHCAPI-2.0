/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ public abstract class AbstractClientHttpRequest
/*     */   implements ClientHttpRequest
/*     */ {
/*     */   private final HttpHeaders headers;
/*     */   private final MultiValueMap<String, HttpCookie> cookies;
/*     */   
/*     */   private enum State
/*     */   {
/*  52 */     NEW, COMMITTING, COMMITTED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   private final AtomicReference<State> state = new AtomicReference<>(State.NEW);
/*     */   
/*  61 */   private final List<Supplier<? extends Publisher<Void>>> commitActions = new ArrayList<>(4);
/*     */   
/*     */   @Nullable
/*     */   private HttpHeaders readOnlyHeaders;
/*     */ 
/*     */   
/*     */   public AbstractClientHttpRequest() {
/*  68 */     this(new HttpHeaders());
/*     */   }
/*     */   
/*     */   public AbstractClientHttpRequest(HttpHeaders headers) {
/*  72 */     Assert.notNull(headers, "HttpHeaders must not be null");
/*  73 */     this.headers = headers;
/*  74 */     this.cookies = (MultiValueMap<String, HttpCookie>)new LinkedMultiValueMap();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/*  80 */     if (this.readOnlyHeaders != null) {
/*  81 */       return this.readOnlyHeaders;
/*     */     }
/*  83 */     if (State.COMMITTED.equals(this.state.get())) {
/*  84 */       this.readOnlyHeaders = initReadOnlyHeaders();
/*  85 */       return this.readOnlyHeaders;
/*     */     } 
/*     */     
/*  88 */     return this.headers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpHeaders initReadOnlyHeaders() {
/*  99 */     return HttpHeaders.readOnlyHttpHeaders(this.headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, HttpCookie> getCookies() {
/* 104 */     if (State.COMMITTED.equals(this.state.get())) {
/* 105 */       return CollectionUtils.unmodifiableMultiValueMap(this.cookies);
/*     */     }
/* 107 */     return this.cookies;
/*     */   }
/*     */ 
/*     */   
/*     */   public void beforeCommit(Supplier<? extends Mono<Void>> action) {
/* 112 */     Assert.notNull(action, "Action must not be null");
/* 113 */     this.commitActions.add(action);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCommitted() {
/* 118 */     return (this.state.get() != State.NEW);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Mono<Void> doCommit() {
/* 126 */     return doCommit(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Mono<Void> doCommit(@Nullable Supplier<? extends Publisher<Void>> writeAction) {
/* 136 */     if (!this.state.compareAndSet(State.NEW, State.COMMITTING)) {
/* 137 */       return Mono.empty();
/*     */     }
/*     */     
/* 140 */     this.commitActions.add(() -> Mono.fromRunnable(()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 147 */     if (writeAction != null) {
/* 148 */       this.commitActions.add(writeAction);
/*     */     }
/*     */ 
/*     */     
/* 152 */     List<? extends Publisher<Void>> actions = (List<? extends Publisher<Void>>)this.commitActions.stream().map(Supplier::get).collect(Collectors.toList());
/*     */     
/* 154 */     return Flux.concat(actions).then();
/*     */   }
/*     */   
/*     */   protected abstract void applyHeaders();
/*     */   
/*     */   protected abstract void applyCookies();
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/AbstractClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */