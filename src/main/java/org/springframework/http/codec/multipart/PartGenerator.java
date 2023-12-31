/*     */ package org.springframework.http.codec.multipart;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.UncheckedIOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.reactivestreams.Subscription;
/*     */ import org.springframework.core.codec.DecodingException;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferLimitException;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.io.buffer.DefaultDataBufferFactory;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.util.FastByteArrayOutputStream;
/*     */ import org.springframework.util.MimeType;
/*     */ import reactor.core.CoreSubscriber;
/*     */ import reactor.core.publisher.BaseSubscriber;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.FluxSink;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.core.scheduler.Scheduler;
/*     */ import reactor.util.context.Context;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class PartGenerator
/*     */   extends BaseSubscriber<MultipartParser.Token>
/*     */ {
/*  65 */   private static final Log logger = LogFactory.getLog(PartGenerator.class);
/*     */   
/*  67 */   private final AtomicReference<State> state = new AtomicReference<>(new InitialState());
/*     */   
/*  69 */   private final AtomicInteger partCount = new AtomicInteger();
/*     */   
/*  71 */   private final AtomicBoolean requestOutstanding = new AtomicBoolean();
/*     */ 
/*     */   
/*     */   private final FluxSink<Part> sink;
/*     */ 
/*     */   
/*     */   private final int maxParts;
/*     */   
/*     */   private final boolean streaming;
/*     */   
/*     */   private final int maxInMemorySize;
/*     */   
/*     */   private final long maxDiskUsagePerPart;
/*     */   
/*     */   private final Mono<Path> fileStorageDirectory;
/*     */   
/*     */   private final Scheduler blockingOperationScheduler;
/*     */ 
/*     */   
/*     */   private PartGenerator(FluxSink<Part> sink, int maxParts, int maxInMemorySize, long maxDiskUsagePerPart, boolean streaming, Mono<Path> fileStorageDirectory, Scheduler blockingOperationScheduler) {
/*  91 */     this.sink = sink;
/*  92 */     this.maxParts = maxParts;
/*  93 */     this.maxInMemorySize = maxInMemorySize;
/*  94 */     this.maxDiskUsagePerPart = maxDiskUsagePerPart;
/*  95 */     this.streaming = streaming;
/*  96 */     this.fileStorageDirectory = fileStorageDirectory;
/*  97 */     this.blockingOperationScheduler = blockingOperationScheduler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Flux<Part> createParts(Flux<MultipartParser.Token> tokens, int maxParts, int maxInMemorySize, long maxDiskUsagePerPart, boolean streaming, Mono<Path> fileStorageDirectory, Scheduler blockingOperationScheduler) {
/* 107 */     return Flux.create(sink -> {
/*     */           PartGenerator generator = new PartGenerator(sink, maxParts, maxInMemorySize, maxDiskUsagePerPart, streaming, fileStorageDirectory, blockingOperationScheduler);
/*     */           sink.onCancel(generator::onSinkCancel);
/*     */           sink.onRequest(());
/*     */           tokens.subscribe((CoreSubscriber)generator);
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Context currentContext() {
/* 119 */     return this.sink.currentContext();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void hookOnSubscribe(Subscription subscription) {
/* 124 */     requestToken();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void hookOnNext(MultipartParser.Token token) {
/* 129 */     this.requestOutstanding.set(false);
/* 130 */     State state = this.state.get();
/* 131 */     if (token instanceof MultipartParser.HeadersToken) {
/*     */       
/* 133 */       state.partComplete(false);
/*     */       
/* 135 */       if (tooManyParts()) {
/*     */         return;
/*     */       }
/*     */       
/* 139 */       newPart(state, token.headers());
/*     */     } else {
/*     */       
/* 142 */       state.body(token.buffer());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void newPart(State currentState, HttpHeaders headers) {
/* 147 */     if (isFormField(headers)) {
/* 148 */       changeStateInternal(new FormFieldState(headers));
/* 149 */       requestToken();
/*     */     }
/* 151 */     else if (!this.streaming) {
/* 152 */       changeStateInternal(new InMemoryState(headers));
/* 153 */       requestToken();
/*     */     } else {
/*     */       
/* 156 */       Flux<DataBuffer> streamingContent = Flux.create(contentSink -> {
/*     */             State newState = new StreamingState(contentSink);
/*     */             if (changeState(currentState, newState)) {
/*     */               contentSink.onRequest(());
/*     */               requestToken();
/*     */             } 
/*     */           });
/* 163 */       emitPart(DefaultParts.part(headers, streamingContent));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void hookOnComplete() {
/* 169 */     ((State)this.state.get()).partComplete(true);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void hookOnError(Throwable throwable) {
/* 174 */     ((State)this.state.get()).error(throwable);
/* 175 */     changeStateInternal(DisposedState.INSTANCE);
/* 176 */     this.sink.error(throwable);
/*     */   }
/*     */   
/*     */   private void onSinkCancel() {
/* 180 */     changeStateInternal(DisposedState.INSTANCE);
/* 181 */     cancel();
/*     */   }
/*     */   
/*     */   boolean changeState(State oldState, State newState) {
/* 185 */     if (this.state.compareAndSet(oldState, newState)) {
/* 186 */       if (logger.isTraceEnabled()) {
/* 187 */         logger.trace("Changed state: " + oldState + " -> " + newState);
/*     */       }
/* 189 */       oldState.dispose();
/* 190 */       return true;
/*     */     } 
/*     */     
/* 193 */     logger.warn("Could not switch from " + oldState + " to " + newState + "; current state:" + this.state
/*     */         
/* 195 */         .get());
/* 196 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void changeStateInternal(State newState) {
/* 201 */     if (this.state.get() == DisposedState.INSTANCE) {
/*     */       return;
/*     */     }
/* 204 */     State oldState = this.state.getAndSet(newState);
/* 205 */     if (logger.isTraceEnabled()) {
/* 206 */       logger.trace("Changed state: " + oldState + " -> " + newState);
/*     */     }
/* 208 */     oldState.dispose();
/*     */   }
/*     */   
/*     */   void emitPart(Part part) {
/* 212 */     if (logger.isTraceEnabled()) {
/* 213 */       logger.trace("Emitting: " + part);
/*     */     }
/* 215 */     this.sink.next(part);
/*     */   }
/*     */   
/*     */   void emitComplete() {
/* 219 */     this.sink.complete();
/*     */   }
/*     */ 
/*     */   
/*     */   void emitError(Throwable t) {
/* 224 */     cancel();
/* 225 */     this.sink.error(t);
/*     */   }
/*     */   
/*     */   void requestToken() {
/* 229 */     if (upstream() != null && 
/* 230 */       !this.sink.isCancelled() && this.sink
/* 231 */       .requestedFromDownstream() > 0L && this.requestOutstanding
/* 232 */       .compareAndSet(false, true)) {
/* 233 */       request(1L);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean tooManyParts() {
/* 238 */     int count = this.partCount.incrementAndGet();
/* 239 */     if (this.maxParts > 0 && count > this.maxParts) {
/* 240 */       emitError((Throwable)new DecodingException("Too many parts (" + count + "/" + this.maxParts + " allowed)"));
/* 241 */       return true;
/*     */     } 
/*     */     
/* 244 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isFormField(HttpHeaders headers) {
/* 249 */     MediaType contentType = headers.getContentType();
/* 250 */     return ((contentType == null || MediaType.TEXT_PLAIN.equalsTypeAndSubtype((MimeType)contentType)) && headers
/* 251 */       .getContentDisposition().getFilename() == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static interface State
/*     */   {
/*     */     void body(DataBuffer param1DataBuffer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void partComplete(boolean param1Boolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default void error(Throwable throwable) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default void dispose() {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final class InitialState
/*     */     implements State
/*     */   {
/*     */     private InitialState() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void body(DataBuffer dataBuffer) {
/* 312 */       DataBufferUtils.release(dataBuffer);
/* 313 */       PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
/*     */     }
/*     */ 
/*     */     
/*     */     public void partComplete(boolean finalPart) {
/* 318 */       if (finalPart) {
/* 319 */         PartGenerator.this.emitComplete();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 325 */       return "INITIAL";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final class FormFieldState
/*     */     implements State
/*     */   {
/* 336 */     private final FastByteArrayOutputStream value = new FastByteArrayOutputStream();
/*     */     
/*     */     private final HttpHeaders headers;
/*     */     
/*     */     public FormFieldState(HttpHeaders headers) {
/* 341 */       this.headers = headers;
/*     */     }
/*     */ 
/*     */     
/*     */     public void body(DataBuffer dataBuffer) {
/* 346 */       int size = this.value.size() + dataBuffer.readableByteCount();
/* 347 */       if (PartGenerator.this.maxInMemorySize == -1 || size < PartGenerator.this
/* 348 */         .maxInMemorySize) {
/* 349 */         store(dataBuffer);
/* 350 */         PartGenerator.this.requestToken();
/*     */       } else {
/*     */         
/* 353 */         DataBufferUtils.release(dataBuffer);
/* 354 */         PartGenerator.this.emitError((Throwable)new DataBufferLimitException("Form field value exceeded the memory usage limit of " + PartGenerator.this
/* 355 */               .maxInMemorySize + " bytes"));
/*     */       } 
/*     */     }
/*     */     
/*     */     private void store(DataBuffer dataBuffer) {
/*     */       try {
/* 361 */         byte[] bytes = new byte[dataBuffer.readableByteCount()];
/* 362 */         dataBuffer.read(bytes);
/* 363 */         this.value.write(bytes);
/*     */       }
/* 365 */       catch (IOException ex) {
/* 366 */         PartGenerator.this.emitError(ex);
/*     */       } finally {
/*     */         
/* 369 */         DataBufferUtils.release(dataBuffer);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void partComplete(boolean finalPart) {
/* 375 */       byte[] bytes = this.value.toByteArrayUnsafe();
/* 376 */       String value = new String(bytes, MultipartUtils.charset(this.headers));
/* 377 */       PartGenerator.this.emitPart(DefaultParts.formFieldPart(this.headers, value));
/* 378 */       if (finalPart) {
/* 379 */         PartGenerator.this.emitComplete();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 385 */       return "FORM-FIELD";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final class StreamingState
/*     */     implements State
/*     */   {
/*     */     private final FluxSink<DataBuffer> bodySink;
/*     */ 
/*     */ 
/*     */     
/*     */     public StreamingState(FluxSink<DataBuffer> bodySink) {
/* 400 */       this.bodySink = bodySink;
/*     */     }
/*     */ 
/*     */     
/*     */     public void body(DataBuffer dataBuffer) {
/* 405 */       if (!this.bodySink.isCancelled()) {
/* 406 */         this.bodySink.next(dataBuffer);
/* 407 */         if (this.bodySink.requestedFromDownstream() > 0L) {
/* 408 */           PartGenerator.this.requestToken();
/*     */         }
/*     */       } else {
/*     */         
/* 412 */         DataBufferUtils.release(dataBuffer);
/*     */ 
/*     */         
/* 415 */         PartGenerator.this.requestToken();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void partComplete(boolean finalPart) {
/* 421 */       if (!this.bodySink.isCancelled()) {
/* 422 */         this.bodySink.complete();
/*     */       }
/* 424 */       if (finalPart) {
/* 425 */         PartGenerator.this.emitComplete();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void error(Throwable throwable) {
/* 431 */       if (!this.bodySink.isCancelled()) {
/* 432 */         this.bodySink.error(throwable);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 438 */       return "STREAMING";
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
/*     */   private final class InMemoryState
/*     */     implements State
/*     */   {
/* 453 */     private final AtomicLong byteCount = new AtomicLong();
/*     */     
/* 455 */     private final Queue<DataBuffer> content = new ConcurrentLinkedQueue<>();
/*     */     
/*     */     private final HttpHeaders headers;
/*     */     
/*     */     private volatile boolean releaseOnDispose = true;
/*     */ 
/*     */     
/*     */     public InMemoryState(HttpHeaders headers) {
/* 463 */       this.headers = headers;
/*     */     }
/*     */ 
/*     */     
/*     */     public void body(DataBuffer dataBuffer) {
/* 468 */       long prevCount = this.byteCount.get();
/* 469 */       long count = this.byteCount.addAndGet(dataBuffer.readableByteCount());
/* 470 */       if (PartGenerator.this.maxInMemorySize == -1 || count <= PartGenerator.this
/* 471 */         .maxInMemorySize) {
/* 472 */         storeBuffer(dataBuffer);
/*     */       }
/* 474 */       else if (prevCount <= PartGenerator.this.maxInMemorySize) {
/* 475 */         switchToFile(dataBuffer, count);
/*     */       } else {
/*     */         
/* 478 */         DataBufferUtils.release(dataBuffer);
/* 479 */         PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
/*     */       } 
/*     */     }
/*     */     
/*     */     private void storeBuffer(DataBuffer dataBuffer) {
/* 484 */       this.content.add(dataBuffer);
/* 485 */       PartGenerator.this.requestToken();
/*     */     }
/*     */     
/*     */     private void switchToFile(DataBuffer current, long byteCount) {
/* 489 */       List<DataBuffer> content = new ArrayList<>(this.content);
/* 490 */       content.add(current);
/* 491 */       this.releaseOnDispose = false;
/*     */       
/* 493 */       PartGenerator.CreateFileState newState = new PartGenerator.CreateFileState(this.headers, content, byteCount);
/* 494 */       if (PartGenerator.this.changeState(this, newState)) {
/* 495 */         newState.createFile();
/*     */       } else {
/*     */         
/* 498 */         content.forEach(DataBufferUtils::release);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void partComplete(boolean finalPart) {
/* 504 */       emitMemoryPart();
/* 505 */       if (finalPart) {
/* 506 */         PartGenerator.this.emitComplete();
/*     */       }
/*     */     }
/*     */     
/*     */     private void emitMemoryPart() {
/* 511 */       byte[] bytes = new byte[(int)this.byteCount.get()];
/* 512 */       int idx = 0;
/* 513 */       for (DataBuffer buffer : this.content) {
/* 514 */         int len = buffer.readableByteCount();
/* 515 */         buffer.read(bytes, idx, len);
/* 516 */         idx += len;
/* 517 */         DataBufferUtils.release(buffer);
/*     */       } 
/* 519 */       this.content.clear();
/* 520 */       Flux<DataBuffer> content = Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(bytes));
/* 521 */       PartGenerator.this.emitPart(DefaultParts.part(this.headers, content));
/*     */     }
/*     */ 
/*     */     
/*     */     public void dispose() {
/* 526 */       if (this.releaseOnDispose) {
/* 527 */         this.content.forEach(DataBufferUtils::release);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 533 */       return "IN-MEMORY";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final class CreateFileState
/*     */     implements State
/*     */   {
/*     */     private final HttpHeaders headers;
/*     */ 
/*     */     
/*     */     private final Collection<DataBuffer> content;
/*     */ 
/*     */     
/*     */     private final long byteCount;
/*     */ 
/*     */     
/*     */     private volatile boolean completed;
/*     */ 
/*     */     
/*     */     private volatile boolean finalPart;
/*     */ 
/*     */     
/*     */     private volatile boolean releaseOnDispose = true;
/*     */ 
/*     */     
/*     */     public CreateFileState(HttpHeaders headers, Collection<DataBuffer> content, long byteCount) {
/* 561 */       this.headers = headers;
/* 562 */       this.content = content;
/* 563 */       this.byteCount = byteCount;
/*     */     }
/*     */ 
/*     */     
/*     */     public void body(DataBuffer dataBuffer) {
/* 568 */       DataBufferUtils.release(dataBuffer);
/* 569 */       PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
/*     */     }
/*     */ 
/*     */     
/*     */     public void partComplete(boolean finalPart) {
/* 574 */       this.completed = true;
/* 575 */       this.finalPart = finalPart;
/*     */     }
/*     */     
/*     */     public void createFile() {
/* 579 */       PartGenerator.this.fileStorageDirectory
/* 580 */         .map(this::createFileState)
/* 581 */         .subscribeOn(PartGenerator.this.blockingOperationScheduler)
/* 582 */         .subscribe(this::fileCreated, PartGenerator.this::emitError);
/*     */     }
/*     */     
/*     */     private PartGenerator.WritingFileState createFileState(Path directory) {
/*     */       try {
/* 587 */         Path tempFile = Files.createTempFile(directory, null, ".multipart", (FileAttribute<?>[])new FileAttribute[0]);
/* 588 */         if (PartGenerator.logger.isTraceEnabled()) {
/* 589 */           PartGenerator.logger.trace("Storing multipart data in file " + tempFile);
/*     */         }
/* 591 */         WritableByteChannel channel = Files.newByteChannel(tempFile, new OpenOption[] { StandardOpenOption.WRITE });
/* 592 */         return new PartGenerator.WritingFileState(this, tempFile, channel);
/*     */       }
/* 594 */       catch (IOException ex) {
/* 595 */         throw new UncheckedIOException("Could not create temp file in " + directory, ex);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void fileCreated(PartGenerator.WritingFileState newState) {
/* 600 */       this.releaseOnDispose = false;
/*     */       
/* 602 */       if (PartGenerator.this.changeState(this, newState)) {
/*     */         
/* 604 */         newState.writeBuffers(this.content);
/*     */         
/* 606 */         if (this.completed) {
/* 607 */           newState.partComplete(this.finalPart);
/*     */         }
/*     */       } else {
/*     */         
/* 611 */         MultipartUtils.closeChannel(newState.channel);
/* 612 */         this.content.forEach(DataBufferUtils::release);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void dispose() {
/* 618 */       if (this.releaseOnDispose) {
/* 619 */         this.content.forEach(DataBufferUtils::release);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 625 */       return "CREATE-FILE";
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private final class IdleFileState
/*     */     implements State
/*     */   {
/*     */     private final HttpHeaders headers;
/*     */     
/*     */     private final Path file;
/*     */     
/*     */     private final WritableByteChannel channel;
/*     */     
/*     */     private final AtomicLong byteCount;
/*     */     
/*     */     private volatile boolean closeOnDispose = true;
/*     */ 
/*     */     
/*     */     public IdleFileState(PartGenerator.WritingFileState state) {
/* 645 */       this.headers = state.headers;
/* 646 */       this.file = state.file;
/* 647 */       this.channel = state.channel;
/* 648 */       this.byteCount = state.byteCount;
/*     */     }
/*     */ 
/*     */     
/*     */     public void body(DataBuffer dataBuffer) {
/* 653 */       long count = this.byteCount.addAndGet(dataBuffer.readableByteCount());
/* 654 */       if (PartGenerator.this.maxDiskUsagePerPart == -1L || count <= PartGenerator.this.maxDiskUsagePerPart) {
/*     */         
/* 656 */         this.closeOnDispose = false;
/* 657 */         PartGenerator.WritingFileState newState = new PartGenerator.WritingFileState(this);
/* 658 */         if (PartGenerator.this.changeState(this, newState)) {
/* 659 */           newState.writeBuffer(dataBuffer);
/*     */         } else {
/*     */           
/* 662 */           MultipartUtils.closeChannel(this.channel);
/* 663 */           DataBufferUtils.release(dataBuffer);
/*     */         } 
/*     */       } else {
/*     */         
/* 667 */         DataBufferUtils.release(dataBuffer);
/* 668 */         PartGenerator.this.emitError((Throwable)new DataBufferLimitException("Part exceeded the disk usage limit of " + PartGenerator.this
/* 669 */               .maxDiskUsagePerPart + " bytes"));
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void partComplete(boolean finalPart) {
/* 676 */       MultipartUtils.closeChannel(this.channel);
/* 677 */       PartGenerator.this.emitPart(DefaultParts.part(this.headers, this.file, PartGenerator.this.blockingOperationScheduler));
/* 678 */       if (finalPart) {
/* 679 */         PartGenerator.this.emitComplete();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void dispose() {
/* 685 */       if (this.closeOnDispose) {
/* 686 */         MultipartUtils.closeChannel(this.channel);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 693 */       return "IDLE-FILE";
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private final class WritingFileState
/*     */     implements State
/*     */   {
/*     */     private final HttpHeaders headers;
/*     */     
/*     */     private final Path file;
/*     */     
/*     */     private final WritableByteChannel channel;
/*     */     
/*     */     private final AtomicLong byteCount;
/*     */     
/*     */     private volatile boolean completed;
/*     */     
/*     */     private volatile boolean finalPart;
/*     */ 
/*     */     
/*     */     public WritingFileState(PartGenerator.CreateFileState state, Path file, WritableByteChannel channel) {
/* 715 */       this.headers = state.headers;
/* 716 */       this.file = file;
/* 717 */       this.channel = channel;
/* 718 */       this.byteCount = new AtomicLong(state.byteCount);
/*     */     }
/*     */     
/*     */     public WritingFileState(PartGenerator.IdleFileState state) {
/* 722 */       this.headers = state.headers;
/* 723 */       this.file = state.file;
/* 724 */       this.channel = state.channel;
/* 725 */       this.byteCount = state.byteCount;
/*     */     }
/*     */ 
/*     */     
/*     */     public void body(DataBuffer dataBuffer) {
/* 730 */       DataBufferUtils.release(dataBuffer);
/* 731 */       PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
/*     */     }
/*     */ 
/*     */     
/*     */     public void partComplete(boolean finalPart) {
/* 736 */       this.completed = true;
/* 737 */       this.finalPart = finalPart;
/*     */     }
/*     */     
/*     */     public void writeBuffer(DataBuffer dataBuffer) {
/* 741 */       Mono.just(dataBuffer)
/* 742 */         .flatMap(this::writeInternal)
/* 743 */         .subscribeOn(PartGenerator.this.blockingOperationScheduler)
/* 744 */         .subscribe(null, PartGenerator.this::emitError, this::writeComplete);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void writeBuffers(Iterable<DataBuffer> dataBuffers) {
/* 750 */       Flux.fromIterable(dataBuffers)
/* 751 */         .concatMap(this::writeInternal)
/* 752 */         .then()
/* 753 */         .subscribeOn(PartGenerator.this.blockingOperationScheduler)
/* 754 */         .subscribe(null, PartGenerator.this::emitError, this::writeComplete);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private void writeComplete() {
/* 760 */       PartGenerator.IdleFileState newState = new PartGenerator.IdleFileState(this);
/* 761 */       if (this.completed) {
/* 762 */         newState.partComplete(this.finalPart);
/*     */       }
/* 764 */       else if (PartGenerator.this.changeState(this, newState)) {
/* 765 */         PartGenerator.this.requestToken();
/*     */       } else {
/*     */         
/* 768 */         MultipartUtils.closeChannel(this.channel);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private Mono<Void> writeInternal(DataBuffer dataBuffer) {
/*     */       try {
/* 775 */         ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
/* 776 */         while (byteBuffer.hasRemaining()) {
/* 777 */           this.channel.write(byteBuffer);
/*     */         }
/* 779 */         return Mono.empty();
/*     */       }
/* 781 */       catch (IOException ex) {
/* 782 */         return Mono.error(ex);
/*     */       } finally {
/*     */         
/* 785 */         DataBufferUtils.release(dataBuffer);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 791 */       return "WRITE-FILE";
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class DisposedState
/*     */     implements State
/*     */   {
/* 798 */     public static final DisposedState INSTANCE = new DisposedState();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void body(DataBuffer dataBuffer) {
/* 805 */       DataBufferUtils.release(dataBuffer);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void partComplete(boolean finalPart) {}
/*     */ 
/*     */     
/*     */     public String toString() {
/* 814 */       return "DISPOSED";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/PartGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */