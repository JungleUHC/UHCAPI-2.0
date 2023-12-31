/*     */ package org.springframework.http.server.reactive;
/*     */ 
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
/*     */ class WriteResultPublisher
/*     */   implements Publisher<Void>
/*     */ {
/*  48 */   private static final Log rsWriteResultLogger = LogDelegateFactory.getHiddenLog(WriteResultPublisher.class);
/*     */ 
/*     */   
/*  51 */   private final AtomicReference<State> state = new AtomicReference<>(State.UNSUBSCRIBED);
/*     */   
/*     */   private final Runnable cancelTask;
/*     */   
/*     */   @Nullable
/*     */   private volatile Subscriber<? super Void> subscriber;
/*     */   
/*     */   private volatile boolean completedBeforeSubscribed;
/*     */   
/*     */   @Nullable
/*     */   private volatile Throwable errorBeforeSubscribed;
/*     */   
/*     */   private final String logPrefix;
/*     */ 
/*     */   
/*     */   public WriteResultPublisher(String logPrefix, Runnable cancelTask) {
/*  67 */     this.cancelTask = cancelTask;
/*  68 */     this.logPrefix = logPrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final void subscribe(Subscriber<? super Void> subscriber) {
/*  74 */     if (rsWriteResultLogger.isTraceEnabled()) {
/*  75 */       rsWriteResultLogger.trace(this.logPrefix + "got subscriber " + subscriber);
/*     */     }
/*  77 */     ((State)this.state.get()).subscribe(this, subscriber);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void publishComplete() {
/*  84 */     State state = this.state.get();
/*  85 */     if (rsWriteResultLogger.isTraceEnabled()) {
/*  86 */       rsWriteResultLogger.trace(this.logPrefix + "completed [" + state + "]");
/*     */     }
/*  88 */     state.publishComplete(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void publishError(Throwable t) {
/*  95 */     State state = this.state.get();
/*  96 */     if (rsWriteResultLogger.isTraceEnabled()) {
/*  97 */       rsWriteResultLogger.trace(this.logPrefix + "failed: " + t + " [" + state + "]");
/*     */     }
/*  99 */     state.publishError(this, t);
/*     */   }
/*     */   
/*     */   private boolean changeState(State oldState, State newState) {
/* 103 */     return this.state.compareAndSet(oldState, newState);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class WriteResultSubscription
/*     */     implements Subscription
/*     */   {
/*     */     private final WriteResultPublisher publisher;
/*     */ 
/*     */ 
/*     */     
/*     */     public WriteResultSubscription(WriteResultPublisher publisher) {
/* 116 */       this.publisher = publisher;
/*     */     }
/*     */ 
/*     */     
/*     */     public final void request(long n) {
/* 121 */       if (WriteResultPublisher.rsWriteResultLogger.isTraceEnabled()) {
/* 122 */         WriteResultPublisher.rsWriteResultLogger.trace(this.publisher.logPrefix + "request " + ((n != Long.MAX_VALUE) ? 
/* 123 */             Long.valueOf(n) : "Long.MAX_VALUE"));
/*     */       }
/* 125 */       getState().request(this.publisher, n);
/*     */     }
/*     */ 
/*     */     
/*     */     public final void cancel() {
/* 130 */       WriteResultPublisher.State state = getState();
/* 131 */       if (WriteResultPublisher.rsWriteResultLogger.isTraceEnabled()) {
/* 132 */         WriteResultPublisher.rsWriteResultLogger.trace(this.publisher.logPrefix + "cancel [" + state + "]");
/*     */       }
/* 134 */       state.cancel(this.publisher);
/*     */     }
/*     */     
/*     */     private WriteResultPublisher.State getState() {
/* 138 */       return this.publisher.state.get();
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
/*     */   private enum State
/*     */   {
/* 160 */     UNSUBSCRIBED
/*     */     {
/*     */       void subscribe(WriteResultPublisher publisher, Subscriber<? super Void> subscriber) {
/* 163 */         Assert.notNull(subscriber, "Subscriber must not be null");
/* 164 */         if (publisher.changeState(this, SUBSCRIBING)) {
/* 165 */           Subscription subscription = new WriteResultPublisher.WriteResultSubscription(publisher);
/* 166 */           publisher.subscriber = subscriber;
/* 167 */           subscriber.onSubscribe(subscription);
/* 168 */           publisher.changeState(SUBSCRIBING, SUBSCRIBED);
/*     */           
/* 170 */           if (publisher.completedBeforeSubscribed) {
/* 171 */             ((State)publisher.state.get()).publishComplete(publisher);
/*     */           }
/* 173 */           Throwable ex = publisher.errorBeforeSubscribed;
/* 174 */           if (ex != null) {
/* 175 */             ((State)publisher.state.get()).publishError(publisher, ex);
/*     */           }
/*     */         } else {
/*     */           
/* 179 */           throw new IllegalStateException(toString());
/*     */         } 
/*     */       }
/*     */       
/*     */       void publishComplete(WriteResultPublisher publisher) {
/* 184 */         publisher.completedBeforeSubscribed = true;
/* 185 */         if (State.SUBSCRIBED == publisher.state.get()) {
/* 186 */           ((State)publisher.state.get()).publishComplete(publisher);
/*     */         }
/*     */       }
/*     */       
/*     */       void publishError(WriteResultPublisher publisher, Throwable ex) {
/* 191 */         publisher.errorBeforeSubscribed = ex;
/* 192 */         if (State.SUBSCRIBED == publisher.state.get()) {
/* 193 */           ((State)publisher.state.get()).publishError(publisher, ex);
/*     */         
/*     */         }
/*     */       }
/*     */     },
/* 198 */     SUBSCRIBING
/*     */     {
/*     */       void request(WriteResultPublisher publisher, long n) {
/* 201 */         Operators.validate(n);
/*     */       }
/*     */       
/*     */       void publishComplete(WriteResultPublisher publisher) {
/* 205 */         publisher.completedBeforeSubscribed = true;
/* 206 */         if (State.SUBSCRIBED == publisher.state.get()) {
/* 207 */           ((State)publisher.state.get()).publishComplete(publisher);
/*     */         }
/*     */       }
/*     */       
/*     */       void publishError(WriteResultPublisher publisher, Throwable ex) {
/* 212 */         publisher.errorBeforeSubscribed = ex;
/* 213 */         if (State.SUBSCRIBED == publisher.state.get()) {
/* 214 */           ((State)publisher.state.get()).publishError(publisher, ex);
/*     */         
/*     */         }
/*     */       }
/*     */     },
/* 219 */     SUBSCRIBED
/*     */     {
/*     */       void request(WriteResultPublisher publisher, long n) {
/* 222 */         Operators.validate(n);
/*     */       }
/*     */     },
/*     */     
/* 226 */     COMPLETED
/*     */     {
/*     */       void request(WriteResultPublisher publisher, long n) {}
/*     */ 
/*     */ 
/*     */       
/*     */       void cancel(WriteResultPublisher publisher) {}
/*     */ 
/*     */ 
/*     */       
/*     */       void publishComplete(WriteResultPublisher publisher) {}
/*     */ 
/*     */ 
/*     */       
/*     */       void publishError(WriteResultPublisher publisher, Throwable t) {}
/*     */     };
/*     */ 
/*     */ 
/*     */     
/*     */     void subscribe(WriteResultPublisher publisher, Subscriber<? super Void> subscriber) {
/* 246 */       throw new IllegalStateException(toString());
/*     */     }
/*     */     
/*     */     void request(WriteResultPublisher publisher, long n) {
/* 250 */       throw new IllegalStateException(toString());
/*     */     }
/*     */     
/*     */     void cancel(WriteResultPublisher publisher) {
/* 254 */       if (publisher.changeState(this, COMPLETED)) {
/* 255 */         publisher.cancelTask.run();
/*     */       } else {
/*     */         
/* 258 */         ((State)publisher.state.get()).cancel(publisher);
/*     */       } 
/*     */     }
/*     */     
/*     */     void publishComplete(WriteResultPublisher publisher) {
/* 263 */       if (publisher.changeState(this, COMPLETED)) {
/* 264 */         Subscriber<? super Void> s = publisher.subscriber;
/* 265 */         Assert.state((s != null), "No subscriber");
/* 266 */         s.onComplete();
/*     */       } else {
/*     */         
/* 269 */         ((State)publisher.state.get()).publishComplete(publisher);
/*     */       } 
/*     */     }
/*     */     
/*     */     void publishError(WriteResultPublisher publisher, Throwable t) {
/* 274 */       if (publisher.changeState(this, COMPLETED)) {
/* 275 */         Subscriber<? super Void> s = publisher.subscriber;
/* 276 */         Assert.state((s != null), "No subscriber");
/* 277 */         s.onError(t);
/*     */       } else {
/*     */         
/* 280 */         ((State)publisher.state.get()).publishError(publisher, t);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/WriteResultPublisher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */