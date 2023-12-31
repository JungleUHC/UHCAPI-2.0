/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.reactivestreams.Subscriber;
/*     */ import org.reactivestreams.Subscription;
/*     */ import org.springframework.core.log.LogDelegateFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import reactor.core.publisher.Operators;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractListenerReadPublisher<T>
/*     */   implements Publisher<T>
/*     */ {
/*  57 */   protected static Log rsReadLogger = LogDelegateFactory.getHiddenLog(AbstractListenerReadPublisher.class);
/*     */ 
/*     */   
/*  60 */   private final AtomicReference<State> state = new AtomicReference<>(State.UNSUBSCRIBED);
/*     */ 
/*     */   
/*     */   private volatile long demand;
/*     */ 
/*     */   
/*  66 */   private static final AtomicLongFieldUpdater<AbstractListenerReadPublisher> DEMAND_FIELD_UPDATER = AtomicLongFieldUpdater.newUpdater(AbstractListenerReadPublisher.class, "demand");
/*     */   
/*     */   @Nullable
/*     */   private volatile Subscriber<? super T> subscriber;
/*     */   
/*     */   private volatile boolean completionPending;
/*     */   
/*     */   @Nullable
/*     */   private volatile Throwable errorPending;
/*     */   
/*     */   private final String logPrefix;
/*     */ 
/*     */   
/*     */   public AbstractListenerReadPublisher() {
/*  80 */     this("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractListenerReadPublisher(String logPrefix) {
/*  88 */     this.logPrefix = logPrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLogPrefix() {
/*  97 */     return this.logPrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void subscribe(Subscriber<? super T> subscriber) {
/* 105 */     ((State)this.state.get()).subscribe(this, subscriber);
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
/*     */   public final void onDataAvailable() {
/* 117 */     rsReadLogger.trace(getLogPrefix() + "onDataAvailable");
/* 118 */     ((State)this.state.get()).onDataAvailable(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onAllDataRead() {
/* 126 */     State state = this.state.get();
/* 127 */     if (rsReadLogger.isTraceEnabled()) {
/* 128 */       rsReadLogger.trace(getLogPrefix() + "onAllDataRead [" + state + "]");
/*     */     }
/* 130 */     state.onAllDataRead(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void onError(Throwable ex) {
/* 137 */     State state = this.state.get();
/* 138 */     if (rsReadLogger.isTraceEnabled()) {
/* 139 */       rsReadLogger.trace(getLogPrefix() + "onError: " + ex + " [" + state + "]");
/*     */     }
/* 141 */     state.onError(this, ex);
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
/*     */   private boolean readAndPublish() throws IOException {
/*     */     long r;
/* 189 */     while ((r = this.demand) > 0L && this.state.get() != State.COMPLETED) {
/* 190 */       T data = read();
/* 191 */       if (data != null) {
/* 192 */         if (r != Long.MAX_VALUE) {
/* 193 */           DEMAND_FIELD_UPDATER.addAndGet(this, -1L);
/*     */         }
/* 195 */         Subscriber<? super T> subscriber = this.subscriber;
/* 196 */         Assert.state((subscriber != null), "No subscriber");
/* 197 */         if (rsReadLogger.isTraceEnabled()) {
/* 198 */           rsReadLogger.trace(getLogPrefix() + "Publishing " + data.getClass().getSimpleName());
/*     */         }
/* 200 */         subscriber.onNext(data);
/*     */         continue;
/*     */       } 
/* 203 */       if (rsReadLogger.isTraceEnabled()) {
/* 204 */         rsReadLogger.trace(getLogPrefix() + "No more to read");
/*     */       }
/* 206 */       return true;
/*     */     } 
/*     */     
/* 209 */     return false;
/*     */   }
/*     */   
/*     */   private boolean changeState(State oldState, State newState) {
/* 213 */     boolean result = this.state.compareAndSet(oldState, newState);
/* 214 */     if (result && rsReadLogger.isTraceEnabled()) {
/* 215 */       rsReadLogger.trace(getLogPrefix() + oldState + " -> " + newState);
/*     */     }
/* 217 */     return result;
/*     */   }
/*     */   
/*     */   private void changeToDemandState(State oldState) {
/* 221 */     if (changeState(oldState, State.DEMAND))
/*     */     {
/*     */ 
/*     */       
/* 225 */       if (oldState != State.READING) {
/* 226 */         checkOnDataAvailable();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean handlePendingCompletionOrError() {
/* 232 */     State state = this.state.get();
/* 233 */     if (state == State.DEMAND || state == State.NO_DEMAND) {
/* 234 */       if (this.completionPending) {
/* 235 */         rsReadLogger.trace(getLogPrefix() + "Processing pending completion");
/* 236 */         ((State)this.state.get()).onAllDataRead(this);
/* 237 */         return true;
/*     */       } 
/* 239 */       Throwable ex = this.errorPending;
/* 240 */       if (ex != null) {
/* 241 */         if (rsReadLogger.isTraceEnabled()) {
/* 242 */           rsReadLogger.trace(getLogPrefix() + "Processing pending completion with error: " + ex);
/*     */         }
/* 244 */         ((State)this.state.get()).onError(this, ex);
/* 245 */         return true;
/*     */       } 
/*     */     } 
/* 248 */     return false;
/*     */   } protected abstract void checkOnDataAvailable(); @Nullable
/*     */   protected abstract T read() throws IOException;
/*     */   private Subscription createSubscription() {
/* 252 */     return new ReadSubscription();
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract void readingPaused();
/*     */   
/*     */   protected abstract void discardData();
/*     */   
/*     */   private final class ReadSubscription
/*     */     implements Subscription
/*     */   {
/*     */     public final void request(long n) {
/* 264 */       if (AbstractListenerReadPublisher.rsReadLogger.isTraceEnabled()) {
/* 265 */         AbstractListenerReadPublisher.rsReadLogger.trace(AbstractListenerReadPublisher.this.getLogPrefix() + "request " + ((n != Long.MAX_VALUE) ? Long.valueOf(n) : "Long.MAX_VALUE"));
/*     */       }
/* 267 */       ((AbstractListenerReadPublisher.State)AbstractListenerReadPublisher.this.state.get()).request(AbstractListenerReadPublisher.this, n);
/*     */     }
/*     */     private ReadSubscription() {}
/*     */     
/*     */     public final void cancel() {
/* 272 */       AbstractListenerReadPublisher.State state = AbstractListenerReadPublisher.this.state.get();
/* 273 */       if (AbstractListenerReadPublisher.rsReadLogger.isTraceEnabled()) {
/* 274 */         AbstractListenerReadPublisher.rsReadLogger.trace(AbstractListenerReadPublisher.this.getLogPrefix() + "cancel [" + state + "]");
/*     */       }
/* 276 */       state.cancel(AbstractListenerReadPublisher.this);
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private enum State
/*     */   {
/* 301 */     UNSUBSCRIBED
/*     */     {
/*     */       <T> void subscribe(AbstractListenerReadPublisher<T> publisher, Subscriber<? super T> subscriber) {
/* 304 */         Assert.notNull(publisher, "Publisher must not be null");
/* 305 */         Assert.notNull(subscriber, "Subscriber must not be null");
/* 306 */         if (publisher.changeState(this, SUBSCRIBING)) {
/* 307 */           Subscription subscription = publisher.createSubscription();
/* 308 */           publisher.subscriber = subscriber;
/* 309 */           subscriber.onSubscribe(subscription);
/* 310 */           publisher.changeState(SUBSCRIBING, NO_DEMAND);
/* 311 */           publisher.handlePendingCompletionOrError();
/*     */         } else {
/*     */           
/* 314 */           throw new IllegalStateException("Failed to transition to SUBSCRIBING, subscriber: " + subscriber);
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
/* 321 */         publisher.completionPending = true;
/* 322 */         publisher.handlePendingCompletionOrError();
/*     */       }
/*     */ 
/*     */       
/*     */       <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
/* 327 */         publisher.errorPending = ex;
/* 328 */         publisher.handlePendingCompletionOrError();
/*     */       }
/*     */     },
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 336 */     SUBSCRIBING
/*     */     {
/*     */       <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
/* 339 */         if (Operators.validate(n)) {
/* 340 */           Operators.addCap(AbstractListenerReadPublisher.DEMAND_FIELD_UPDATER, publisher, n);
/* 341 */           publisher.changeToDemandState(this);
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
/* 347 */         publisher.completionPending = true;
/* 348 */         publisher.handlePendingCompletionOrError();
/*     */       }
/*     */ 
/*     */       
/*     */       <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
/* 353 */         publisher.errorPending = ex;
/* 354 */         publisher.handlePendingCompletionOrError();
/*     */       }
/*     */     },
/*     */     
/* 358 */     NO_DEMAND
/*     */     {
/*     */       <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
/* 361 */         if (Operators.validate(n)) {
/* 362 */           Operators.addCap(AbstractListenerReadPublisher.DEMAND_FIELD_UPDATER, publisher, n);
/* 363 */           publisher.changeToDemandState(this);
/*     */         }
/*     */       
/*     */       }
/*     */     },
/* 368 */     DEMAND
/*     */     {
/*     */       <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
/* 371 */         if (Operators.validate(n)) {
/* 372 */           Operators.addCap(AbstractListenerReadPublisher.DEMAND_FIELD_UPDATER, publisher, n);
/*     */           
/* 374 */           publisher.changeToDemandState(NO_DEMAND);
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       <T> void onDataAvailable(AbstractListenerReadPublisher<T> publisher) {
/* 380 */         if (publisher.changeState(this, READING)) {
/*     */           try {
/* 382 */             boolean demandAvailable = publisher.readAndPublish();
/* 383 */             if (demandAvailable) {
/* 384 */               publisher.changeToDemandState(READING);
/* 385 */               publisher.handlePendingCompletionOrError();
/*     */             } else {
/*     */               
/* 388 */               publisher.readingPaused();
/* 389 */               if (publisher.changeState(READING, NO_DEMAND) && 
/* 390 */                 !publisher.handlePendingCompletionOrError())
/*     */               {
/* 392 */                 long r = publisher.demand;
/* 393 */                 if (r > 0L) {
/* 394 */                   publisher.changeToDemandState(NO_DEMAND);
/*     */                 }
/*     */               }
/*     */             
/*     */             }
/*     */           
/* 400 */           } catch (IOException ex) {
/* 401 */             publisher.onError(ex);
/*     */           
/*     */           }
/*     */         
/*     */         }
/*     */       }
/*     */     },
/* 408 */     READING
/*     */     {
/*     */       <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
/* 411 */         if (Operators.validate(n)) {
/* 412 */           Operators.addCap(AbstractListenerReadPublisher.DEMAND_FIELD_UPDATER, publisher, n);
/*     */           
/* 414 */           publisher.changeToDemandState(NO_DEMAND);
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
/* 420 */         publisher.completionPending = true;
/* 421 */         publisher.handlePendingCompletionOrError();
/*     */       }
/*     */ 
/*     */       
/*     */       <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
/* 426 */         publisher.errorPending = ex;
/* 427 */         publisher.handlePendingCompletionOrError();
/*     */       }
/*     */     },
/*     */     
/* 431 */     COMPLETED
/*     */     {
/*     */       <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {}
/*     */ 
/*     */ 
/*     */       
/*     */       <T> void cancel(AbstractListenerReadPublisher<T> publisher) {}
/*     */ 
/*     */ 
/*     */       
/*     */       <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {}
/*     */ 
/*     */ 
/*     */       
/*     */       <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable t) {}
/*     */     };
/*     */ 
/*     */ 
/*     */     
/*     */     <T> void subscribe(AbstractListenerReadPublisher<T> publisher, Subscriber<? super T> subscriber) {
/* 451 */       throw new IllegalStateException(toString());
/*     */     }
/*     */     
/*     */     <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
/* 455 */       throw new IllegalStateException(toString());
/*     */     }
/*     */     
/*     */     <T> void cancel(AbstractListenerReadPublisher<T> publisher) {
/* 459 */       if (publisher.changeState(this, COMPLETED)) {
/* 460 */         publisher.discardData();
/*     */       } else {
/*     */         
/* 463 */         ((State)publisher.state.get()).cancel(publisher);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     <T> void onDataAvailable(AbstractListenerReadPublisher<T> publisher) {}
/*     */ 
/*     */     
/*     */     <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
/* 472 */       if (publisher.changeState(this, COMPLETED)) {
/* 473 */         Subscriber<? super T> s = publisher.subscriber;
/* 474 */         if (s != null) {
/* 475 */           s.onComplete();
/*     */         }
/*     */       } else {
/*     */         
/* 479 */         ((State)publisher.state.get()).onAllDataRead(publisher);
/*     */       } 
/*     */     }
/*     */     
/*     */     <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable t) {
/* 484 */       if (publisher.changeState(this, COMPLETED)) {
/* 485 */         publisher.discardData();
/* 486 */         Subscriber<? super T> s = publisher.subscriber;
/* 487 */         if (s != null) {
/* 488 */           s.onError(t);
/*     */         }
/*     */       } else {
/*     */         
/* 492 */         ((State)publisher.state.get()).onError(publisher, t);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/AbstractListenerReadPublisher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */