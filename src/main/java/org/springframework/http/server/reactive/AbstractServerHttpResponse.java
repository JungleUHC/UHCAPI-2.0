/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.function.Supplier;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.reactivestreams.Subscription;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.io.buffer.PooledDataBuffer;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.ResponseCookie;
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
/*     */ public abstract class AbstractServerHttpResponse
/*     */   implements ServerHttpResponse
/*     */ {
/*     */   private final DataBufferFactory dataBufferFactory;
/*     */   @Nullable
/*     */   private Integer statusCode;
/*     */   private final HttpHeaders headers;
/*     */   private final MultiValueMap<String, ResponseCookie> cookies;
/*     */   
/*     */   private enum State
/*     */   {
/*  59 */     NEW, COMMITTING, COMMIT_ACTION_FAILED, COMMITTED;
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
/*     */   
/*  71 */   private final AtomicReference<State> state = new AtomicReference<>(State.NEW);
/*     */   
/*  73 */   private final List<Supplier<? extends Mono<Void>>> commitActions = new ArrayList<>(4);
/*     */   
/*     */   @Nullable
/*     */   private HttpHeaders readOnlyHeaders;
/*     */ 
/*     */   
/*     */   public AbstractServerHttpResponse(DataBufferFactory dataBufferFactory) {
/*  80 */     this(dataBufferFactory, new HttpHeaders());
/*     */   }
/*     */   
/*     */   public AbstractServerHttpResponse(DataBufferFactory dataBufferFactory, HttpHeaders headers) {
/*  84 */     Assert.notNull(dataBufferFactory, "DataBufferFactory must not be null");
/*  85 */     Assert.notNull(headers, "HttpHeaders must not be null");
/*  86 */     this.dataBufferFactory = dataBufferFactory;
/*  87 */     this.headers = headers;
/*  88 */     this.cookies = (MultiValueMap<String, ResponseCookie>)new LinkedMultiValueMap();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final DataBufferFactory bufferFactory() {
/*  94 */     return this.dataBufferFactory;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setStatusCode(@Nullable HttpStatus status) {
/*  99 */     if (this.state.get() == State.COMMITTED) {
/* 100 */       return false;
/*     */     }
/*     */     
/* 103 */     this.statusCode = (status != null) ? Integer.valueOf(status.value()) : null;
/* 104 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public HttpStatus getStatusCode() {
/* 111 */     return (this.statusCode != null) ? HttpStatus.resolve(this.statusCode.intValue()) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setRawStatusCode(@Nullable Integer statusCode) {
/* 116 */     if (this.state.get() == State.COMMITTED) {
/* 117 */       return false;
/*     */     }
/*     */     
/* 120 */     this.statusCode = statusCode;
/* 121 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Integer getRawStatusCode() {
/* 128 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setStatusCodeValue(@Nullable Integer statusCode) {
/* 139 */     if (this.state.get() != State.COMMITTED) {
/* 140 */       this.statusCode = statusCode;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   @Deprecated
/*     */   public Integer getStatusCodeValue() {
/* 153 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/* 158 */     if (this.readOnlyHeaders != null) {
/* 159 */       return this.readOnlyHeaders;
/*     */     }
/* 161 */     if (this.state.get() == State.COMMITTED) {
/* 162 */       this.readOnlyHeaders = HttpHeaders.readOnlyHttpHeaders(this.headers);
/* 163 */       return this.readOnlyHeaders;
/*     */     } 
/*     */     
/* 166 */     return this.headers;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, ResponseCookie> getCookies() {
/* 172 */     return (this.state.get() == State.COMMITTED) ? 
/* 173 */       CollectionUtils.unmodifiableMultiValueMap(this.cookies) : this.cookies;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addCookie(ResponseCookie cookie) {
/* 178 */     Assert.notNull(cookie, "ResponseCookie must not be null");
/*     */     
/* 180 */     if (this.state.get() == State.COMMITTED) {
/* 181 */       throw new IllegalStateException("Can't add the cookie " + cookie + "because the HTTP response has already been committed");
/*     */     }
/*     */ 
/*     */     
/* 185 */     getCookies().add(cookie.getName(), cookie);
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
/*     */ 
/*     */   
/*     */   public void beforeCommit(Supplier<? extends Mono<Void>> action) {
/* 199 */     this.commitActions.add(action);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCommitted() {
/* 204 */     State state = this.state.get();
/* 205 */     return (state != State.NEW && state != State.COMMIT_ACTION_FAILED);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
/* 213 */     if (body instanceof Mono) {
/* 214 */       return ((Mono)body)
/* 215 */         .flatMap(buffer -> {
/*     */             touchDataBuffer(buffer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             AtomicBoolean subscribed = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             return doCommit(()).doOnError(()).doOnCancel(());
/* 236 */           }).doOnError(t -> getHeaders().clearContentHeaders())
/* 237 */         .doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
/*     */     }
/*     */     
/* 240 */     return (new ChannelSendOperator(body, inner -> doCommit(())))
/* 241 */       .doOnError(t -> getHeaders().clearContentHeaders());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
/* 247 */     return (new ChannelSendOperator(body, inner -> doCommit(())))
/* 248 */       .doOnError(t -> getHeaders().clearContentHeaders());
/*     */   }
/*     */ 
/*     */   
/*     */   public Mono<Void> setComplete() {
/* 253 */     return !isCommitted() ? doCommit(null) : Mono.empty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Mono<Void> doCommit() {
/* 261 */     return doCommit(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Mono<Void> doCommit(@Nullable Supplier<? extends Mono<Void>> writeAction) {
/* 271 */     Flux<Void> allActions = Flux.empty();
/* 272 */     if (this.state.compareAndSet(State.NEW, State.COMMITTING)) {
/* 273 */       if (!this.commitActions.isEmpty())
/*     */       {
/* 275 */         allActions = Flux.concat((Publisher)Flux.fromIterable(this.commitActions).map(Supplier::get)).doOnError(ex -> {
/*     */               
/*     */               if (this.state.compareAndSet(State.COMMITTING, State.COMMIT_ACTION_FAILED)) {
/*     */                 getHeaders().clearContentHeaders();
/*     */               }
/*     */             });
/*     */       }
/* 282 */     } else if (!this.state.compareAndSet(State.COMMIT_ACTION_FAILED, State.COMMITTING)) {
/*     */ 
/*     */ 
/*     */       
/* 286 */       return Mono.empty();
/*     */     } 
/*     */     
/* 289 */     allActions = allActions.concatWith((Publisher)Mono.fromRunnable(() -> {
/*     */             applyStatusCode();
/*     */             
/*     */             applyHeaders();
/*     */             applyCookies();
/*     */             this.state.set(State.COMMITTED);
/*     */           }));
/* 296 */     if (writeAction != null) {
/* 297 */       allActions = allActions.concatWith((Publisher)writeAction.get());
/*     */     }
/*     */     
/* 300 */     return allActions.then();
/*     */   }
/*     */   
/*     */   protected void touchDataBuffer(DataBuffer buffer) {}
/*     */   
/*     */   public abstract <T> T getNativeResponse();
/*     */   
/*     */   protected abstract Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> paramPublisher);
/*     */   
/*     */   protected abstract Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> paramPublisher);
/*     */   
/*     */   protected abstract void applyStatusCode();
/*     */   
/*     */   protected abstract void applyHeaders();
/*     */   
/*     */   protected abstract void applyCookies();
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/AbstractServerHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */