/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.reactivestreams.Processor;
/*     */ import org.reactivestreams.Subscriber;
/*     */ import org.reactivestreams.Subscription;
/*     */ import org.springframework.core.log.LogDelegateFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractListenerWriteProcessor<T>
/*     */   implements Processor<T, Void>
/*     */ {
/*  55 */   protected static final Log rsWriteLogger = LogDelegateFactory.getHiddenLog(AbstractListenerWriteProcessor.class);
/*     */ 
/*     */   
/*  58 */   private final AtomicReference<State> state = new AtomicReference<>(State.UNSUBSCRIBED);
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Subscription subscription;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private volatile T currentData;
/*     */ 
/*     */   
/*     */   private volatile boolean sourceCompleted;
/*     */ 
/*     */   
/*     */   private volatile boolean readyToCompleteAfterLastWrite;
/*     */ 
/*     */   
/*     */   private final WriteResultPublisher resultPublisher;
/*     */ 
/*     */   
/*     */   private final String logPrefix;
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractListenerWriteProcessor() {
/*  83 */     this("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractListenerWriteProcessor(String logPrefix) {
/*  93 */     this.resultPublisher = new WriteResultPublisher(logPrefix + "[WP] ", this::cancelAndSetCompleted);
/*  94 */     this.logPrefix = StringUtils.hasText(logPrefix) ? logPrefix : "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLogPrefix() {
/* 103 */     return this.logPrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void onSubscribe(Subscription subscription) {
/* 111 */     ((State)this.state.get()).onSubscribe(this, subscription);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void onNext(T data) {
/* 116 */     if (rsWriteLogger.isTraceEnabled()) {
/* 117 */       rsWriteLogger.trace(getLogPrefix() + "onNext: " + data.getClass().getSimpleName());
/*     */     }
/* 119 */     ((State)this.state.get()).onNext(this, data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void onError(Throwable ex) {
/* 128 */     State state = this.state.get();
/* 129 */     if (rsWriteLogger.isTraceEnabled()) {
/* 130 */       rsWriteLogger.trace(getLogPrefix() + "onError: " + ex + " [" + state + "]");
/*     */     }
/* 132 */     state.onError(this, ex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void onComplete() {
/* 141 */     State state = this.state.get();
/* 142 */     if (rsWriteLogger.isTraceEnabled()) {
/* 143 */       rsWriteLogger.trace(getLogPrefix() + "onComplete [" + state + "]");
/*     */     }
/* 145 */     state.onComplete(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void onWritePossible() {
/* 154 */     State state = this.state.get();
/* 155 */     if (rsWriteLogger.isTraceEnabled()) {
/* 156 */       rsWriteLogger.trace(getLogPrefix() + "onWritePossible [" + state + "]");
/*     */     }
/* 158 */     state.onWritePossible(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cancel() {
/* 169 */     if (rsWriteLogger.isTraceEnabled()) {
/* 170 */       rsWriteLogger.trace(getLogPrefix() + "cancel [" + this.state + "]");
/*     */     }
/* 172 */     if (this.subscription != null) {
/* 173 */       this.subscription.cancel();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void cancelAndSetCompleted() {
/* 183 */     cancel();
/*     */     while (true) {
/* 185 */       State prev = this.state.get();
/* 186 */       if (prev == State.COMPLETED) {
/*     */         break;
/*     */       }
/* 189 */       if (this.state.compareAndSet(prev, State.COMPLETED)) {
/* 190 */         if (rsWriteLogger.isTraceEnabled()) {
/* 191 */           rsWriteLogger.trace(getLogPrefix() + prev + " -> " + this.state);
/*     */         }
/* 193 */         if (prev != State.WRITING) {
/* 194 */           discardCurrentData();
/*     */         }
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void subscribe(Subscriber<? super Void> subscriber) {
/* 205 */     this.resultPublisher.subscribe(subscriber);
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
/*     */   protected void dataReceived(T data) {
/* 223 */     T prev = this.currentData;
/* 224 */     if (prev != null) {
/*     */ 
/*     */ 
/*     */       
/* 228 */       discardData(data);
/* 229 */       cancel();
/* 230 */       onError(new IllegalStateException("Received new data while current not processed yet."));
/*     */     } 
/* 232 */     this.currentData = data;
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
/*     */   @Deprecated
/*     */   protected void writingPaused() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writingComplete() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writingFailed(Throwable ex) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean changeState(State oldState, State newState) {
/* 294 */     boolean result = this.state.compareAndSet(oldState, newState);
/* 295 */     if (result && rsWriteLogger.isTraceEnabled()) {
/* 296 */       rsWriteLogger.trace(getLogPrefix() + oldState + " -> " + newState);
/*     */     }
/* 298 */     return result;
/*     */   }
/*     */   
/*     */   private void changeStateToReceived(State oldState) {
/* 302 */     if (changeState(oldState, State.RECEIVED)) {
/* 303 */       writeIfPossible();
/*     */     }
/*     */   }
/*     */   
/*     */   private void changeStateToComplete(State oldState) {
/* 308 */     if (changeState(oldState, State.COMPLETED)) {
/* 309 */       discardCurrentData();
/* 310 */       writingComplete();
/* 311 */       this.resultPublisher.publishComplete();
/*     */     } else {
/*     */       
/* 314 */       ((State)this.state.get()).onComplete(this);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeIfPossible() {
/* 319 */     boolean result = isWritePossible();
/* 320 */     if (!result && rsWriteLogger.isTraceEnabled()) {
/* 321 */       rsWriteLogger.trace(getLogPrefix() + "isWritePossible false");
/*     */     }
/* 323 */     if (result) {
/* 324 */       onWritePossible();
/*     */     }
/*     */   }
/*     */   
/*     */   private void discardCurrentData() {
/* 329 */     T data = this.currentData;
/* 330 */     this.currentData = null;
/* 331 */     if (data != null) {
/* 332 */       discardData(data);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isDataEmpty(T paramT);
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isWritePossible();
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean write(T paramT) throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void discardData(T paramT);
/*     */ 
/*     */   
/*     */   private enum State
/*     */   {
/* 355 */     UNSUBSCRIBED
/*     */     {
/*     */       public <T> void onSubscribe(AbstractListenerWriteProcessor<T> processor, Subscription subscription) {
/* 358 */         Assert.notNull(subscription, "Subscription must not be null");
/* 359 */         if (processor.changeState(this, REQUESTED)) {
/* 360 */           processor.subscription = subscription;
/* 361 */           subscription.request(1L);
/*     */         } else {
/*     */           
/* 364 */           super.onSubscribe(processor, subscription);
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
/* 371 */         processor.changeStateToComplete(this);
/*     */       }
/*     */     },
/*     */     
/* 375 */     REQUESTED
/*     */     {
/*     */       public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
/* 378 */         if (processor.isDataEmpty(data)) {
/* 379 */           Assert.state((processor.subscription != null), "No subscription");
/* 380 */           processor.subscription.request(1L);
/*     */         } else {
/*     */           
/* 383 */           processor.dataReceived(data);
/* 384 */           processor.changeStateToReceived(this);
/*     */         } 
/*     */       }
/*     */       
/*     */       public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
/* 389 */         processor.readyToCompleteAfterLastWrite = true;
/* 390 */         processor.changeStateToReceived(this);
/*     */       }
/*     */     },
/*     */     
/* 394 */     RECEIVED
/*     */     {
/*     */       public <T> void onWritePossible(AbstractListenerWriteProcessor<T> processor)
/*     */       {
/* 398 */         if (processor.readyToCompleteAfterLastWrite) {
/* 399 */           processor.changeStateToComplete(RECEIVED);
/*     */         }
/* 401 */         else if (processor.changeState(this, WRITING)) {
/* 402 */           T data = processor.currentData;
/* 403 */           Assert.state((data != null), "No data");
/*     */           try {
/* 405 */             if (processor.write(data)) {
/* 406 */               if (processor.changeState(WRITING, REQUESTED)) {
/* 407 */                 processor.currentData = null;
/* 408 */                 if (processor.sourceCompleted) {
/* 409 */                   processor.readyToCompleteAfterLastWrite = true;
/* 410 */                   processor.changeStateToReceived(REQUESTED);
/*     */                 } else {
/*     */                   
/* 413 */                   processor.writingPaused();
/* 414 */                   Assert.state((processor.subscription != null), "No subscription");
/* 415 */                   processor.subscription.request(1L);
/*     */                 } 
/*     */               } 
/*     */             } else {
/*     */               
/* 420 */               processor.changeStateToReceived(WRITING);
/*     */             }
/*     */           
/* 423 */           } catch (IOException ex) {
/* 424 */             processor.writingFailed(ex);
/*     */           } 
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
/* 431 */         processor.sourceCompleted = true;
/*     */         
/* 433 */         if (processor.state.get() == State.REQUESTED) {
/* 434 */           processor.changeStateToComplete(State.REQUESTED);
/*     */         
/*     */         }
/*     */       }
/*     */     },
/* 439 */     WRITING
/*     */     {
/*     */       public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
/* 442 */         processor.sourceCompleted = true;
/*     */         
/* 444 */         if (processor.state.get() == State.REQUESTED) {
/* 445 */           processor.changeStateToComplete(State.REQUESTED);
/*     */         
/*     */         }
/*     */       }
/*     */     },
/* 450 */     COMPLETED
/*     */     {
/*     */       public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {}
/*     */ 
/*     */ 
/*     */       
/*     */       public <T> void onError(AbstractListenerWriteProcessor<T> processor, Throwable ex) {}
/*     */ 
/*     */ 
/*     */       
/*     */       public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {}
/*     */     };
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> void onSubscribe(AbstractListenerWriteProcessor<T> processor, Subscription subscription) {
/* 466 */       subscription.cancel();
/*     */     }
/*     */     
/*     */     public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
/* 470 */       processor.discardData(data);
/* 471 */       processor.cancel();
/* 472 */       processor.onError(new IllegalStateException("Illegal onNext without demand"));
/*     */     }
/*     */     
/*     */     public <T> void onError(AbstractListenerWriteProcessor<T> processor, Throwable ex) {
/* 476 */       if (processor.changeState(this, COMPLETED)) {
/* 477 */         processor.discardCurrentData();
/* 478 */         processor.writingComplete();
/* 479 */         processor.resultPublisher.publishError(ex);
/*     */       } else {
/*     */         
/* 482 */         ((State)processor.state.get()).onError(processor, ex);
/*     */       } 
/*     */     }
/*     */     
/*     */     public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
/* 487 */       throw new IllegalStateException(toString());
/*     */     }
/*     */     
/*     */     public <T> void onWritePossible(AbstractListenerWriteProcessor<T> processor) {}
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/AbstractListenerWriteProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */