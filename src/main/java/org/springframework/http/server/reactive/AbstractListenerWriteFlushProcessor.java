/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.reactivestreams.Processor;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.reactivestreams.Subscriber;
/*     */ import org.reactivestreams.Subscription;
/*     */ import org.springframework.core.log.LogDelegateFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ public abstract class AbstractListenerWriteFlushProcessor<T>
/*     */   implements Processor<Publisher<? extends T>, Void>
/*     */ {
/*  53 */   protected static final Log rsWriteFlushLogger = LogDelegateFactory.getHiddenLog(AbstractListenerWriteFlushProcessor.class);
/*     */ 
/*     */   
/*  56 */   private final AtomicReference<State> state = new AtomicReference<>(State.UNSUBSCRIBED);
/*     */   
/*     */   @Nullable
/*     */   private Subscription subscription;
/*     */   
/*     */   private volatile boolean sourceCompleted;
/*     */   
/*     */   @Nullable
/*     */   private volatile AbstractListenerWriteProcessor<?> currentWriteProcessor;
/*     */   
/*     */   private final WriteResultPublisher resultPublisher;
/*     */   
/*     */   private final String logPrefix;
/*     */ 
/*     */   
/*     */   public AbstractListenerWriteFlushProcessor() {
/*  72 */     this("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractListenerWriteFlushProcessor(String logPrefix) {
/*  80 */     this.logPrefix = logPrefix;
/*  81 */     this.resultPublisher = new WriteResultPublisher(logPrefix + "[WFP] ", () -> {
/*     */           cancel();
/*     */           State oldState = this.state.getAndSet(State.COMPLETED);
/*     */           if (rsWriteFlushLogger.isTraceEnabled()) {
/*     */             rsWriteFlushLogger.trace(getLogPrefix() + oldState + " -> " + this.state);
/*     */           }
/*     */           AbstractListenerWriteProcessor<?> writeProcessor = this.currentWriteProcessor;
/*     */           if (writeProcessor != null) {
/*     */             writeProcessor.cancelAndSetCompleted();
/*     */           }
/*     */           this.currentWriteProcessor = null;
/*     */         });
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
/*     */   public String getLogPrefix() {
/* 104 */     return this.logPrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void onSubscribe(Subscription subscription) {
/* 112 */     ((State)this.state.get()).onSubscribe(this, subscription);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void onNext(Publisher<? extends T> publisher) {
/* 117 */     if (rsWriteFlushLogger.isTraceEnabled()) {
/* 118 */       rsWriteFlushLogger.trace(getLogPrefix() + "onNext: \"write\" Publisher");
/*     */     }
/* 120 */     ((State)this.state.get()).onNext(this, publisher);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void onError(Throwable ex) {
/* 129 */     State state = this.state.get();
/* 130 */     if (rsWriteFlushLogger.isTraceEnabled()) {
/* 131 */       rsWriteFlushLogger.trace(getLogPrefix() + "onError: " + ex + " [" + state + "]");
/*     */     }
/* 133 */     state.onError(this, ex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void onComplete() {
/* 142 */     State state = this.state.get();
/* 143 */     if (rsWriteFlushLogger.isTraceEnabled()) {
/* 144 */       rsWriteFlushLogger.trace(getLogPrefix() + "onComplete [" + state + "]");
/*     */     }
/* 146 */     state.onComplete(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void onFlushPossible() {
/* 155 */     ((State)this.state.get()).onFlushPossible(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void cancel() {
/* 166 */     if (rsWriteFlushLogger.isTraceEnabled()) {
/* 167 */       rsWriteFlushLogger.trace(getLogPrefix() + "cancel [" + this.state + "]");
/*     */     }
/* 169 */     if (this.subscription != null) {
/* 170 */       this.subscription.cancel();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void subscribe(Subscriber<? super Void> subscriber) {
/* 179 */     this.resultPublisher.subscribe(subscriber);
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
/*     */   protected void flushingFailed(Throwable t) {
/* 216 */     cancel();
/* 217 */     onError(t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean changeState(State oldState, State newState) {
/* 224 */     boolean result = this.state.compareAndSet(oldState, newState);
/* 225 */     if (result && rsWriteFlushLogger.isTraceEnabled()) {
/* 226 */       rsWriteFlushLogger.trace(getLogPrefix() + oldState + " -> " + newState);
/*     */     }
/* 228 */     return result;
/*     */   }
/*     */   
/*     */   private void flushIfPossible() {
/* 232 */     boolean result = isWritePossible();
/* 233 */     if (rsWriteFlushLogger.isTraceEnabled()) {
/* 234 */       rsWriteFlushLogger.trace(getLogPrefix() + "isWritePossible[" + result + "]");
/*     */     }
/* 236 */     if (result) {
/* 237 */       onFlushPossible();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Processor<? super T, Void> createWriteProcessor();
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isWritePossible();
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void flush() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isFlushPending();
/*     */ 
/*     */   
/*     */   private enum State
/*     */   {
/* 260 */     UNSUBSCRIBED
/*     */     {
/*     */       public <T> void onSubscribe(AbstractListenerWriteFlushProcessor<T> processor, Subscription subscription) {
/* 263 */         Assert.notNull(subscription, "Subscription must not be null");
/* 264 */         if (processor.changeState(this, REQUESTED)) {
/* 265 */           processor.subscription = subscription;
/* 266 */           subscription.request(1L);
/*     */         } else {
/*     */           
/* 269 */           super.onSubscribe(processor, subscription);
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
/* 276 */         if (processor.changeState(this, COMPLETED)) {
/* 277 */           processor.resultPublisher.publishComplete();
/*     */         } else {
/*     */           
/* 280 */           ((State)processor.state.get()).onComplete(processor);
/*     */         }
/*     */       
/*     */       }
/*     */     },
/* 285 */     REQUESTED
/*     */     {
/*     */       
/*     */       public <T> void onNext(AbstractListenerWriteFlushProcessor<T> processor, Publisher<? extends T> currentPublisher)
/*     */       {
/* 290 */         if (processor.changeState(this, RECEIVED)) {
/* 291 */           Processor<? super T, Void> writeProcessor = processor.createWriteProcessor();
/* 292 */           processor.currentWriteProcessor = (AbstractListenerWriteProcessor)writeProcessor;
/* 293 */           currentPublisher.subscribe((Subscriber)writeProcessor);
/* 294 */           writeProcessor.subscribe(new WriteResultSubscriber(processor));
/*     */         } 
/*     */       }
/*     */       
/*     */       public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
/* 299 */         if (processor.changeState(this, COMPLETED)) {
/* 300 */           processor.resultPublisher.publishComplete();
/*     */         } else {
/*     */           
/* 303 */           ((State)processor.state.get()).onComplete(processor);
/*     */         }
/*     */       
/*     */       }
/*     */     },
/* 308 */     RECEIVED
/*     */     {
/*     */       public <T> void writeComplete(AbstractListenerWriteFlushProcessor<T> processor) {
/*     */         try {
/* 312 */           processor.flush();
/*     */         }
/* 314 */         catch (Throwable ex) {
/* 315 */           processor.flushingFailed(ex);
/*     */           return;
/*     */         } 
/* 318 */         if (processor.changeState(this, REQUESTED)) {
/* 319 */           if (processor.sourceCompleted) {
/* 320 */             handleSourceCompleted(processor);
/*     */           } else {
/*     */             
/* 323 */             Assert.state((processor.subscription != null), "No subscription");
/* 324 */             processor.subscription.request(1L);
/*     */           } 
/*     */         }
/*     */       }
/*     */       
/*     */       public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
/* 330 */         processor.sourceCompleted = true;
/*     */         
/* 332 */         if (processor.state.get() == State.REQUESTED) {
/* 333 */           handleSourceCompleted(processor);
/*     */         }
/*     */       }
/*     */       
/*     */       private <T> void handleSourceCompleted(AbstractListenerWriteFlushProcessor<T> processor) {
/* 338 */         if (processor.isFlushPending()) {
/*     */           
/* 340 */           processor.changeState(State.REQUESTED, State.FLUSHING);
/* 341 */           processor.flushIfPossible();
/*     */         }
/* 343 */         else if (processor.changeState(State.REQUESTED, State.COMPLETED)) {
/* 344 */           processor.resultPublisher.publishComplete();
/*     */         } else {
/*     */           
/* 347 */           ((State)processor.state.get()).onComplete(processor);
/*     */         }
/*     */       
/*     */       }
/*     */     },
/* 352 */     FLUSHING
/*     */     {
/*     */       public <T> void onFlushPossible(AbstractListenerWriteFlushProcessor<T> processor) {
/*     */         try {
/* 356 */           processor.flush();
/*     */         }
/* 358 */         catch (Throwable ex) {
/* 359 */           processor.flushingFailed(ex);
/*     */           return;
/*     */         } 
/* 362 */         if (processor.changeState(this, COMPLETED)) {
/* 363 */           processor.resultPublisher.publishComplete();
/*     */         } else {
/*     */           
/* 366 */           ((State)processor.state.get()).onComplete(processor);
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {}
/*     */ 
/*     */ 
/*     */       
/*     */       public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {}
/*     */     },
/* 379 */     COMPLETED
/*     */     {
/*     */       public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {}
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public <T> void onError(AbstractListenerWriteFlushProcessor<T> processor, Throwable t) {}
/*     */ 
/*     */ 
/*     */       
/*     */       public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {}
/*     */     };
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> void onSubscribe(AbstractListenerWriteFlushProcessor<T> proc, Subscription subscription) {
/* 396 */       subscription.cancel();
/*     */     }
/*     */     
/*     */     public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
/* 400 */       throw new IllegalStateException(toString());
/*     */     }
/*     */     
/*     */     public <T> void onError(AbstractListenerWriteFlushProcessor<T> processor, Throwable ex) {
/* 404 */       if (processor.changeState(this, COMPLETED)) {
/* 405 */         processor.resultPublisher.publishError(ex);
/*     */       } else {
/*     */         
/* 408 */         ((State)processor.state.get()).onError(processor, ex);
/*     */       } 
/*     */     }
/*     */     
/*     */     public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
/* 413 */       throw new IllegalStateException(toString());
/*     */     }
/*     */     
/*     */     public <T> void writeComplete(AbstractListenerWriteFlushProcessor<T> processor) {
/* 417 */       throw new IllegalStateException(toString());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> void onFlushPossible(AbstractListenerWriteFlushProcessor<T> processor) {}
/*     */ 
/*     */ 
/*     */     
/*     */     private static class WriteResultSubscriber
/*     */       implements Subscriber<Void>
/*     */     {
/*     */       private final AbstractListenerWriteFlushProcessor<?> processor;
/*     */ 
/*     */ 
/*     */       
/*     */       public WriteResultSubscriber(AbstractListenerWriteFlushProcessor<?> processor) {
/* 435 */         this.processor = processor;
/*     */       }
/*     */ 
/*     */       
/*     */       public void onSubscribe(Subscription subscription) {
/* 440 */         subscription.request(Long.MAX_VALUE);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public void onNext(Void aVoid) {}
/*     */ 
/*     */       
/*     */       public void onError(Throwable ex) {
/* 449 */         if (AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.isTraceEnabled()) {
/* 450 */           AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.trace(this.processor
/* 451 */               .getLogPrefix() + "current \"write\" Publisher failed: " + ex);
/*     */         }
/* 453 */         this.processor.currentWriteProcessor = null;
/* 454 */         this.processor.cancel();
/* 455 */         this.processor.onError(ex);
/*     */       }
/*     */ 
/*     */       
/*     */       public void onComplete() {
/* 460 */         if (AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.isTraceEnabled()) {
/* 461 */           AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.trace(this.processor
/* 462 */               .getLogPrefix() + "current \"write\" Publisher completed");
/*     */         }
/* 464 */         this.processor.currentWriteProcessor = null;
/* 465 */         ((AbstractListenerWriteFlushProcessor.State)this.processor.state.get()).writeComplete(this.processor);
/*     */       }
/*     */ 
/*     */       
/*     */       public String toString() {
/* 470 */         return this.processor.getClass().getSimpleName() + "-WriteResultSubscriber";
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/AbstractListenerWriteFlushProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */