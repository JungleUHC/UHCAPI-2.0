/*     */ package org.springframework.http.codec.multipart;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentLinkedDeque;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.reactivestreams.Subscription;
/*     */ import org.springframework.core.codec.DecodingException;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferLimitException;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.lang.Nullable;
/*     */ import reactor.core.CoreSubscriber;
/*     */ import reactor.core.publisher.BaseSubscriber;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.FluxSink;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MultipartParser
/*     */   extends BaseSubscriber<DataBuffer>
/*     */ {
/*     */   private static final byte CR = 13;
/*     */   private static final byte LF = 10;
/*  57 */   private static final byte[] CR_LF = new byte[] { 13, 10 };
/*     */   
/*     */   private static final byte HYPHEN = 45;
/*     */   
/*  61 */   private static final byte[] TWO_HYPHENS = new byte[] { 45, 45 };
/*     */   
/*     */   private static final String HEADER_ENTRY_SEPARATOR = "\\r\\n";
/*     */   
/*  65 */   private static final Log logger = LogFactory.getLog(MultipartParser.class);
/*     */   
/*     */   private final AtomicReference<State> state;
/*     */   
/*     */   private final FluxSink<Token> sink;
/*     */   
/*     */   private final byte[] boundary;
/*     */   
/*     */   private final int maxHeadersSize;
/*     */   
/*  75 */   private final AtomicBoolean requestOutstanding = new AtomicBoolean();
/*     */   
/*     */   private final Charset headersCharset;
/*     */ 
/*     */   
/*     */   private MultipartParser(FluxSink<Token> sink, byte[] boundary, int maxHeadersSize, Charset headersCharset) {
/*  81 */     this.sink = sink;
/*  82 */     this.boundary = boundary;
/*  83 */     this.maxHeadersSize = maxHeadersSize;
/*  84 */     this.headersCharset = headersCharset;
/*  85 */     this.state = new AtomicReference<>(new PreambleState());
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
/*     */   public static Flux<Token> parse(Flux<DataBuffer> buffers, byte[] boundary, int maxHeadersSize, Charset headersCharset) {
/*  98 */     return Flux.create(sink -> {
/*     */           MultipartParser parser = new MultipartParser(sink, boundary, maxHeadersSize, headersCharset);
/*     */           sink.onCancel(parser::onSinkCancel);
/*     */           sink.onRequest(());
/*     */           buffers.subscribe((CoreSubscriber)parser);
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public Context currentContext() {
/* 108 */     return this.sink.currentContext();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void hookOnSubscribe(Subscription subscription) {
/* 113 */     requestBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void hookOnNext(DataBuffer value) {
/* 118 */     this.requestOutstanding.set(false);
/* 119 */     ((State)this.state.get()).onNext(value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void hookOnComplete() {
/* 124 */     ((State)this.state.get()).onComplete();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void hookOnError(Throwable throwable) {
/* 129 */     State oldState = this.state.getAndSet(DisposedState.INSTANCE);
/* 130 */     oldState.dispose();
/* 131 */     this.sink.error(throwable);
/*     */   }
/*     */   
/*     */   private void onSinkCancel() {
/* 135 */     State oldState = this.state.getAndSet(DisposedState.INSTANCE);
/* 136 */     oldState.dispose();
/* 137 */     cancel();
/*     */   }
/*     */   
/*     */   boolean changeState(State oldState, State newState, @Nullable DataBuffer remainder) {
/* 141 */     if (this.state.compareAndSet(oldState, newState)) {
/* 142 */       if (logger.isTraceEnabled()) {
/* 143 */         logger.trace("Changed state: " + oldState + " -> " + newState);
/*     */       }
/* 145 */       oldState.dispose();
/* 146 */       if (remainder != null) {
/* 147 */         if (remainder.readableByteCount() > 0) {
/* 148 */           newState.onNext(remainder);
/*     */         } else {
/*     */           
/* 151 */           DataBufferUtils.release(remainder);
/* 152 */           requestBuffer();
/*     */         } 
/*     */       }
/* 155 */       return true;
/*     */     } 
/*     */     
/* 158 */     DataBufferUtils.release(remainder);
/* 159 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   void emitHeaders(HttpHeaders headers) {
/* 164 */     if (logger.isTraceEnabled()) {
/* 165 */       logger.trace("Emitting headers: " + headers);
/*     */     }
/* 167 */     this.sink.next(new HeadersToken(headers));
/*     */   }
/*     */   
/*     */   void emitBody(DataBuffer buffer) {
/* 171 */     if (logger.isTraceEnabled()) {
/* 172 */       logger.trace("Emitting body: " + buffer);
/*     */     }
/* 174 */     this.sink.next(new BodyToken(buffer));
/*     */   }
/*     */   
/*     */   void emitError(Throwable t) {
/* 178 */     cancel();
/* 179 */     this.sink.error(t);
/*     */   }
/*     */   
/*     */   void emitComplete() {
/* 183 */     cancel();
/* 184 */     this.sink.complete();
/*     */   }
/*     */   
/*     */   private void requestBuffer() {
/* 188 */     if (upstream() != null && 
/* 189 */       !this.sink.isCancelled() && this.sink
/* 190 */       .requestedFromDownstream() > 0L && this.requestOutstanding
/* 191 */       .compareAndSet(false, true)) {
/* 192 */       request(1L);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class Token
/*     */   {
/*     */     public abstract HttpHeaders headers();
/*     */ 
/*     */ 
/*     */     
/*     */     public abstract DataBuffer buffer();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class HeadersToken
/*     */     extends Token
/*     */   {
/*     */     private final HttpHeaders headers;
/*     */ 
/*     */     
/*     */     public HeadersToken(HttpHeaders headers) {
/* 216 */       this.headers = headers;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHeaders headers() {
/* 221 */       return this.headers;
/*     */     }
/*     */ 
/*     */     
/*     */     public DataBuffer buffer() {
/* 226 */       throw new IllegalStateException();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class BodyToken
/*     */     extends Token
/*     */   {
/*     */     private final DataBuffer buffer;
/*     */ 
/*     */     
/*     */     public BodyToken(DataBuffer buffer) {
/* 239 */       this.buffer = buffer;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHeaders headers() {
/* 244 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/*     */     
/*     */     public DataBuffer buffer() {
/* 249 */       return this.buffer;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static interface State
/*     */   {
/*     */     void onNext(DataBuffer param1DataBuffer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void onComplete();
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
/*     */   private final class PreambleState
/*     */     implements State
/*     */   {
/*     */     private final DataBufferUtils.Matcher firstBoundary;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PreambleState() {
/* 291 */       this.firstBoundary = DataBufferUtils.matcher(
/* 292 */           MultipartUtils.concat(new byte[][] { MultipartParser.access$000(), MultipartParser.access$100(this$0) }));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onNext(DataBuffer buf) {
/* 302 */       int endIdx = this.firstBoundary.match(buf);
/* 303 */       if (endIdx != -1) {
/* 304 */         if (MultipartParser.logger.isTraceEnabled()) {
/* 305 */           MultipartParser.logger.trace("First boundary found @" + endIdx + " in " + buf);
/*     */         }
/* 307 */         DataBuffer headersBuf = MultipartUtils.sliceFrom(buf, endIdx);
/* 308 */         DataBufferUtils.release(buf);
/*     */         
/* 310 */         MultipartParser.this.changeState(this, new MultipartParser.HeadersState(), headersBuf);
/*     */       } else {
/*     */         
/* 313 */         DataBufferUtils.release(buf);
/* 314 */         MultipartParser.this.requestBuffer();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void onComplete() {
/* 320 */       if (MultipartParser.this.changeState(this, MultipartParser.DisposedState.INSTANCE, (DataBuffer)null)) {
/* 321 */         MultipartParser.this.emitError((Throwable)new DecodingException("Could not find first boundary"));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 327 */       return "PREAMBLE";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final class HeadersState
/*     */     implements State
/*     */   {
/* 340 */     private final DataBufferUtils.Matcher endHeaders = DataBufferUtils.matcher(MultipartUtils.concat(new byte[][] { MultipartParser.access$500(), MultipartParser.access$500() }));
/*     */     
/* 342 */     private final AtomicInteger byteCount = new AtomicInteger();
/*     */     
/* 344 */     private final List<DataBuffer> buffers = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onNext(DataBuffer buf) {
/* 359 */       if (isLastBoundary(buf)) {
/* 360 */         if (MultipartParser.logger.isTraceEnabled()) {
/* 361 */           MultipartParser.logger.trace("Last boundary found in " + buf);
/*     */         }
/*     */         
/* 364 */         if (MultipartParser.this.changeState(this, MultipartParser.DisposedState.INSTANCE, buf)) {
/* 365 */           MultipartParser.this.emitComplete();
/*     */         }
/*     */         return;
/*     */       } 
/* 369 */       int endIdx = this.endHeaders.match(buf);
/* 370 */       if (endIdx != -1) {
/* 371 */         if (MultipartParser.logger.isTraceEnabled()) {
/* 372 */           MultipartParser.logger.trace("End of headers found @" + endIdx + " in " + buf);
/*     */         }
/* 374 */         long count = this.byteCount.addAndGet(endIdx);
/* 375 */         if (belowMaxHeaderSize(count)) {
/* 376 */           DataBuffer headerBuf = MultipartUtils.sliceTo(buf, endIdx);
/* 377 */           this.buffers.add(headerBuf);
/* 378 */           DataBuffer bodyBuf = MultipartUtils.sliceFrom(buf, endIdx);
/* 379 */           DataBufferUtils.release(buf);
/*     */           
/* 381 */           MultipartParser.this.emitHeaders(parseHeaders());
/* 382 */           MultipartParser.this.changeState(this, new MultipartParser.BodyState(), bodyBuf);
/*     */         } 
/*     */       } else {
/*     */         
/* 386 */         long count = this.byteCount.addAndGet(buf.readableByteCount());
/* 387 */         if (belowMaxHeaderSize(count)) {
/* 388 */           this.buffers.add(buf);
/* 389 */           MultipartParser.this.requestBuffer();
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean isLastBoundary(DataBuffer buf) {
/* 399 */       return ((this.buffers.isEmpty() && buf
/* 400 */         .readableByteCount() >= 2 && buf
/* 401 */         .getByte(0) == 45 && buf.getByte(1) == 45) || (this.buffers
/*     */         
/* 403 */         .size() == 1 && ((DataBuffer)this.buffers
/* 404 */         .get(0)).readableByteCount() == 1 && ((DataBuffer)this.buffers
/* 405 */         .get(0)).getByte(0) == 45 && buf
/* 406 */         .readableByteCount() >= 1 && buf
/* 407 */         .getByte(0) == 45));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean belowMaxHeaderSize(long count) {
/* 415 */       if (count <= MultipartParser.this.maxHeadersSize) {
/* 416 */         return true;
/*     */       }
/*     */       
/* 419 */       MultipartParser.this.emitError((Throwable)new DataBufferLimitException("Part headers exceeded the memory usage limit of " + MultipartParser.this
/* 420 */             .maxHeadersSize + " bytes"));
/* 421 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private HttpHeaders parseHeaders() {
/* 431 */       if (this.buffers.isEmpty()) {
/* 432 */         return HttpHeaders.EMPTY;
/*     */       }
/* 434 */       DataBuffer joined = ((DataBuffer)this.buffers.get(0)).factory().join(this.buffers);
/* 435 */       this.buffers.clear();
/* 436 */       String string = joined.toString(MultipartParser.this.headersCharset);
/* 437 */       DataBufferUtils.release(joined);
/* 438 */       String[] lines = string.split("\\r\\n");
/* 439 */       HttpHeaders result = new HttpHeaders();
/* 440 */       for (String line : lines) {
/* 441 */         int idx = line.indexOf(':');
/* 442 */         if (idx != -1) {
/* 443 */           String name = line.substring(0, idx);
/* 444 */           String value = line.substring(idx + 1);
/* 445 */           while (value.startsWith(" ")) {
/* 446 */             value = value.substring(1);
/*     */           }
/* 448 */           result.add(name, value);
/*     */         } 
/*     */       } 
/* 451 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onComplete() {
/* 456 */       if (MultipartParser.this.changeState(this, MultipartParser.DisposedState.INSTANCE, (DataBuffer)null)) {
/* 457 */         MultipartParser.this.emitError((Throwable)new DecodingException("Could not find end of headers"));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void dispose() {
/* 463 */       this.buffers.forEach(DataBufferUtils::release);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 468 */       return "HEADERS";
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private HeadersState() {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final class BodyState
/*     */     implements State
/*     */   {
/*     */     private final DataBufferUtils.Matcher boundary;
/*     */ 
/*     */     
/*     */     private final int boundaryLength;
/*     */     
/* 486 */     private final Deque<DataBuffer> queue = new ConcurrentLinkedDeque<>();
/*     */     
/*     */     public BodyState() {
/* 489 */       byte[] delimiter = MultipartUtils.concat(new byte[][] { MultipartParser.access$500(), MultipartParser.access$000(), MultipartParser.access$100(this$0) });
/* 490 */       this.boundary = DataBufferUtils.matcher(delimiter);
/* 491 */       this.boundaryLength = delimiter.length;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onNext(DataBuffer buffer) {
/* 504 */       int endIdx = this.boundary.match(buffer);
/* 505 */       if (endIdx != -1) {
/* 506 */         if (MultipartParser.logger.isTraceEnabled()) {
/* 507 */           MultipartParser.logger.trace("Boundary found @" + endIdx + " in " + buffer);
/*     */         }
/* 509 */         int len = endIdx - buffer.readPosition() - this.boundaryLength + 1;
/* 510 */         if (len > 0) {
/*     */ 
/*     */           
/* 513 */           DataBuffer body = buffer.retainedSlice(buffer.readPosition(), len);
/* 514 */           enqueue(body);
/* 515 */           flush();
/*     */         }
/* 517 */         else if (len < 0) {
/*     */           DataBuffer prev;
/*     */ 
/*     */           
/* 521 */           while ((prev = this.queue.pollLast()) != null) {
/* 522 */             int prevLen = prev.readableByteCount() + len;
/* 523 */             if (prevLen > 0) {
/*     */               
/* 525 */               DataBuffer body = prev.retainedSlice(prev.readPosition(), prevLen);
/* 526 */               DataBufferUtils.release(prev);
/* 527 */               enqueue(body);
/* 528 */               flush();
/*     */               
/*     */               break;
/*     */             } 
/*     */             
/* 533 */             DataBufferUtils.release(prev);
/* 534 */             len += prev.readableByteCount();
/*     */           }
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 540 */           flush();
/*     */         } 
/*     */         
/* 543 */         DataBuffer remainder = MultipartUtils.sliceFrom(buffer, endIdx);
/* 544 */         DataBufferUtils.release(buffer);
/*     */         
/* 546 */         MultipartParser.this.changeState(this, new MultipartParser.HeadersState(), remainder);
/*     */       } else {
/*     */         
/* 549 */         enqueue(buffer);
/* 550 */         MultipartParser.this.requestBuffer();
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void enqueue(DataBuffer buf) {
/* 561 */       this.queue.add(buf);
/*     */       
/* 563 */       int len = 0;
/* 564 */       Deque<DataBuffer> emit = new ArrayDeque<>();
/* 565 */       for (Iterator<DataBuffer> iterator = this.queue.descendingIterator(); iterator.hasNext(); ) {
/* 566 */         DataBuffer previous = iterator.next();
/* 567 */         if (len > this.boundaryLength) {
/*     */           
/* 569 */           emit.addFirst(previous);
/* 570 */           iterator.remove();
/*     */         } 
/* 572 */         len += previous.readableByteCount();
/*     */       } 
/*     */       
/* 575 */       emit.forEach(MultipartParser.this::emitBody);
/*     */     }
/*     */     
/*     */     private void flush() {
/* 579 */       this.queue.forEach(MultipartParser.this::emitBody);
/* 580 */       this.queue.clear();
/*     */     }
/*     */ 
/*     */     
/*     */     public void onComplete() {
/* 585 */       if (MultipartParser.this.changeState(this, MultipartParser.DisposedState.INSTANCE, (DataBuffer)null)) {
/* 586 */         MultipartParser.this.emitError((Throwable)new DecodingException("Could not find end of body"));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void dispose() {
/* 592 */       this.queue.forEach(DataBufferUtils::release);
/* 593 */       this.queue.clear();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 598 */       return "BODY";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class DisposedState
/*     */     implements State
/*     */   {
/* 609 */     public static final DisposedState INSTANCE = new DisposedState();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onNext(DataBuffer buf) {
/* 616 */       DataBufferUtils.release(buf);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onComplete() {}
/*     */ 
/*     */     
/*     */     public String toString() {
/* 625 */       return "DISPOSED";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/MultipartParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */