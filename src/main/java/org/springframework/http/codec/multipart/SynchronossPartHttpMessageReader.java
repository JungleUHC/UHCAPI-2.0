/*     */ package org.springframework.http.codec.multipart;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.function.Consumer;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.core.codec.DecodingException;
/*     */ import org.springframework.core.codec.Hints;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferLimitException;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.io.buffer.DefaultDataBufferFactory;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ReactiveHttpInputMessage;
/*     */ import org.springframework.http.codec.HttpMessageReader;
/*     */ import org.springframework.http.codec.LoggingCodecSupport;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.synchronoss.cloud.nio.multipart.DefaultPartBodyStreamStorageFactory;
/*     */ import org.synchronoss.cloud.nio.multipart.Multipart;
/*     */ import org.synchronoss.cloud.nio.multipart.MultipartContext;
/*     */ import org.synchronoss.cloud.nio.multipart.MultipartUtils;
/*     */ import org.synchronoss.cloud.nio.multipart.NioMultipartParser;
/*     */ import org.synchronoss.cloud.nio.multipart.NioMultipartParserListener;
/*     */ import org.synchronoss.cloud.nio.multipart.PartBodyStreamStorageFactory;
/*     */ import org.synchronoss.cloud.nio.stream.storage.NameAwarePurgableFileInputStream;
/*     */ import org.synchronoss.cloud.nio.stream.storage.StreamStorage;
/*     */ import reactor.core.CoreSubscriber;
/*     */ import reactor.core.publisher.BaseSubscriber;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.FluxSink;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.core.publisher.SignalType;
/*     */ import reactor.core.scheduler.Schedulers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SynchronossPartHttpMessageReader
/*     */   extends LoggingCodecSupport
/*     */   implements HttpMessageReader<Part>
/*     */ {
/*     */   private static final String FILE_STORAGE_DIRECTORY_PREFIX = "synchronoss-file-upload-";
/*  89 */   private int maxInMemorySize = 262144;
/*     */   
/*  91 */   private long maxDiskUsagePerPart = -1L;
/*     */   
/*  93 */   private int maxParts = -1;
/*     */   
/*  95 */   private final AtomicReference<Path> fileStorageDirectory = new AtomicReference<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxInMemorySize(int byteCount) {
/* 112 */     this.maxInMemorySize = byteCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxInMemorySize() {
/* 120 */     return this.maxInMemorySize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxDiskUsagePerPart(long maxDiskUsagePerPart) {
/* 130 */     this.maxDiskUsagePerPart = maxDiskUsagePerPart;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxDiskUsagePerPart() {
/* 138 */     return this.maxDiskUsagePerPart;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxParts(int maxParts) {
/* 146 */     this.maxParts = maxParts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxParts() {
/* 154 */     return this.maxParts;
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
/*     */   public void setFileStorageDirectory(Path fileStorageDirectory) throws IOException {
/* 166 */     Assert.notNull(fileStorageDirectory, "FileStorageDirectory must not be null");
/* 167 */     if (!Files.exists(fileStorageDirectory, new java.nio.file.LinkOption[0])) {
/* 168 */       Files.createDirectory(fileStorageDirectory, (FileAttribute<?>[])new FileAttribute[0]);
/*     */     }
/* 170 */     this.fileStorageDirectory.set(fileStorageDirectory);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<MediaType> getReadableMediaTypes() {
/* 176 */     return MultipartHttpMessageReader.MIME_TYPES;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
/* 181 */     if (Part.class.equals(elementType.toClass())) {
/* 182 */       if (mediaType == null) {
/* 183 */         return true;
/*     */       }
/* 185 */       for (MediaType supportedMediaType : getReadableMediaTypes()) {
/* 186 */         if (supportedMediaType.isCompatibleWith(mediaType)) {
/* 187 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 191 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Flux<Part> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 196 */     return getFileStorageDirectory().flatMapMany(directory -> Flux.create(new SynchronossPartGenerator(message, directory)).doOnNext(()));
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
/*     */   public Mono<Part> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
/* 210 */     return Mono.error(new UnsupportedOperationException("Cannot read multipart request body into single Part"));
/*     */   }
/*     */   
/*     */   private Mono<Path> getFileStorageDirectory() {
/* 214 */     return Mono.defer(() -> {
/*     */           Path directory = this.fileStorageDirectory.get();
/*     */           return (directory != null) ? Mono.just(directory) : Mono.fromCallable(()).subscribeOn(Schedulers.boundedElastic());
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
/*     */ 
/*     */ 
/*     */   
/*     */   private class SynchronossPartGenerator
/*     */     extends BaseSubscriber<DataBuffer>
/*     */     implements Consumer<FluxSink<Part>>
/*     */   {
/*     */     private final ReactiveHttpInputMessage inputMessage;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 247 */     private final SynchronossPartHttpMessageReader.LimitedPartBodyStreamStorageFactory storageFactory = new SynchronossPartHttpMessageReader.LimitedPartBodyStreamStorageFactory();
/*     */     
/*     */     private final Path fileStorageDirectory;
/*     */     
/*     */     @Nullable
/*     */     private NioMultipartParserListener listener;
/*     */     
/*     */     @Nullable
/*     */     private NioMultipartParser parser;
/*     */     
/*     */     public SynchronossPartGenerator(ReactiveHttpInputMessage inputMessage, Path fileStorageDirectory) {
/* 258 */       this.inputMessage = inputMessage;
/* 259 */       this.fileStorageDirectory = fileStorageDirectory;
/*     */     }
/*     */ 
/*     */     
/*     */     public void accept(FluxSink<Part> sink) {
/* 264 */       HttpHeaders headers = this.inputMessage.getHeaders();
/* 265 */       MediaType mediaType = headers.getContentType();
/* 266 */       Assert.state((mediaType != null), "No content type set");
/*     */       
/* 268 */       int length = getContentLength(headers);
/* 269 */       Charset charset = Optional.<Charset>ofNullable(mediaType.getCharset()).orElse(StandardCharsets.UTF_8);
/* 270 */       MultipartContext context = new MultipartContext(mediaType.toString(), length, charset.name());
/*     */       
/* 272 */       this.listener = new SynchronossPartHttpMessageReader.FluxSinkAdapterListener(sink, context, this.storageFactory);
/*     */       
/* 274 */       this
/*     */ 
/*     */ 
/*     */         
/* 278 */         .parser = Multipart.multipart(context).saveTemporaryFilesTo(this.fileStorageDirectory.toString()).usePartBodyStreamStorageFactory(this.storageFactory).forNIO(this.listener);
/*     */       
/* 280 */       this.inputMessage.getBody().subscribe((CoreSubscriber)this);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void hookOnNext(DataBuffer buffer) {
/* 285 */       Assert.state((this.parser != null && this.listener != null), "Not initialized yet");
/*     */       
/* 287 */       int size = buffer.readableByteCount();
/* 288 */       this.storageFactory.increaseByteCount(size);
/* 289 */       byte[] resultBytes = new byte[size];
/* 290 */       buffer.read(resultBytes);
/*     */       
/*     */       try {
/* 293 */         this.parser.write(resultBytes);
/*     */       }
/* 295 */       catch (IOException ex) {
/* 296 */         cancel();
/* 297 */         int index = this.storageFactory.getCurrentPartIndex();
/* 298 */         this.listener.onError("Parser error for part [" + index + "]", ex);
/*     */       } finally {
/*     */         
/* 301 */         DataBufferUtils.release(buffer);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected void hookOnError(Throwable ex) {
/* 307 */       if (this.listener != null) {
/* 308 */         int index = this.storageFactory.getCurrentPartIndex();
/* 309 */         this.listener.onError("Failure while parsing part[" + index + "]", ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected void hookOnComplete() {
/* 315 */       if (this.listener != null) {
/* 316 */         this.listener.onAllPartsFinished();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected void hookFinally(SignalType type) {
/*     */       try {
/* 323 */         if (this.parser != null) {
/* 324 */           this.parser.close();
/*     */         }
/*     */       }
/* 327 */       catch (IOException iOException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int getContentLength(HttpHeaders headers) {
/* 334 */       long length = headers.getContentLength();
/* 335 */       return ((int)length == length) ? (int)length : -1;
/*     */     }
/*     */   }
/*     */   
/*     */   private class LimitedPartBodyStreamStorageFactory
/*     */     implements PartBodyStreamStorageFactory
/*     */   {
/* 342 */     private final PartBodyStreamStorageFactory storageFactory = (SynchronossPartHttpMessageReader.this.maxInMemorySize > 0) ? (PartBodyStreamStorageFactory)new DefaultPartBodyStreamStorageFactory(SynchronossPartHttpMessageReader.this
/* 343 */         .maxInMemorySize) : (PartBodyStreamStorageFactory)new DefaultPartBodyStreamStorageFactory();
/*     */ 
/*     */     
/* 346 */     private int index = 1;
/*     */     
/*     */     private boolean isFilePart;
/*     */     
/*     */     private long partSize;
/*     */     
/*     */     public int getCurrentPartIndex() {
/* 353 */       return this.index;
/*     */     }
/*     */ 
/*     */     
/*     */     public StreamStorage newStreamStorageForPartBody(Map<String, List<String>> headers, int index) {
/* 358 */       this.index = index;
/* 359 */       this.isFilePart = (MultipartUtils.getFileName(headers) != null);
/* 360 */       this.partSize = 0L;
/* 361 */       if (SynchronossPartHttpMessageReader.this.maxParts > 0 && index > SynchronossPartHttpMessageReader.this.maxParts) {
/* 362 */         throw new DecodingException("Too many parts: Part[" + index + "] but maxParts=" + SynchronossPartHttpMessageReader.this.maxParts);
/*     */       }
/* 364 */       return this.storageFactory.newStreamStorageForPartBody(headers, index);
/*     */     }
/*     */     
/*     */     public void increaseByteCount(long byteCount) {
/* 368 */       this.partSize += byteCount;
/* 369 */       if (SynchronossPartHttpMessageReader.this.maxInMemorySize > 0 && !this.isFilePart && this.partSize >= SynchronossPartHttpMessageReader.this.maxInMemorySize) {
/* 370 */         throw new DataBufferLimitException("Part[" + this.index + "] exceeded the in-memory limit of " + SynchronossPartHttpMessageReader.this
/* 371 */             .maxInMemorySize + " bytes");
/*     */       }
/* 373 */       if (SynchronossPartHttpMessageReader.this.maxDiskUsagePerPart > 0L && this.isFilePart && this.partSize > SynchronossPartHttpMessageReader.this.maxDiskUsagePerPart) {
/* 374 */         throw new DecodingException("Part[" + this.index + "] exceeded the disk usage limit of " + SynchronossPartHttpMessageReader.this
/* 375 */             .maxDiskUsagePerPart + " bytes");
/*     */       }
/*     */     }
/*     */     
/*     */     public void partFinished() {
/* 380 */       this.index++;
/* 381 */       this.isFilePart = false;
/* 382 */       this.partSize = 0L;
/*     */     }
/*     */ 
/*     */     
/*     */     private LimitedPartBodyStreamStorageFactory() {}
/*     */   }
/*     */ 
/*     */   
/*     */   private static class FluxSinkAdapterListener
/*     */     implements NioMultipartParserListener
/*     */   {
/*     */     private final FluxSink<Part> sink;
/*     */     
/*     */     private final MultipartContext context;
/*     */     
/*     */     private final SynchronossPartHttpMessageReader.LimitedPartBodyStreamStorageFactory storageFactory;
/* 398 */     private final AtomicInteger terminated = new AtomicInteger();
/*     */ 
/*     */ 
/*     */     
/*     */     FluxSinkAdapterListener(FluxSink<Part> sink, MultipartContext context, SynchronossPartHttpMessageReader.LimitedPartBodyStreamStorageFactory factory) {
/* 403 */       this.sink = sink;
/* 404 */       this.context = context;
/* 405 */       this.storageFactory = factory;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onPartFinished(StreamStorage storage, Map<String, List<String>> headers) {
/* 410 */       HttpHeaders httpHeaders = new HttpHeaders();
/* 411 */       httpHeaders.putAll(headers);
/* 412 */       this.storageFactory.partFinished();
/* 413 */       this.sink.next(createPart(storage, httpHeaders));
/*     */     }
/*     */     
/*     */     private Part createPart(StreamStorage storage, HttpHeaders httpHeaders) {
/* 417 */       String filename = MultipartUtils.getFileName((Map)httpHeaders);
/* 418 */       if (filename != null) {
/* 419 */         return new SynchronossPartHttpMessageReader.SynchronossFilePart(httpHeaders, filename, storage);
/*     */       }
/* 421 */       if (MultipartUtils.isFormField((Map)httpHeaders, this.context)) {
/* 422 */         String value = MultipartUtils.readFormParameterValue(storage, (Map)httpHeaders);
/* 423 */         return new SynchronossPartHttpMessageReader.SynchronossFormFieldPart(httpHeaders, value);
/*     */       } 
/*     */       
/* 426 */       return new SynchronossPartHttpMessageReader.SynchronossPart(httpHeaders, storage);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onError(String message, Throwable cause) {
/* 432 */       if (this.terminated.getAndIncrement() == 0) {
/* 433 */         this.sink.error((Throwable)new DecodingException(message, cause));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void onAllPartsFinished() {
/* 439 */       if (this.terminated.getAndIncrement() == 0) {
/* 440 */         this.sink.complete();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void onNestedPartStarted(Map<String, List<String>> headersFromParentPart) {}
/*     */ 
/*     */     
/*     */     public void onNestedPartFinished() {}
/*     */   }
/*     */ 
/*     */   
/*     */   private static abstract class AbstractSynchronossPart
/*     */     implements Part
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private final HttpHeaders headers;
/*     */ 
/*     */     
/*     */     AbstractSynchronossPart(HttpHeaders headers) {
/* 461 */       Assert.notNull(headers, "HttpHeaders is required");
/* 462 */       this.name = MultipartUtils.getFieldName((Map)headers);
/* 463 */       this.headers = headers;
/*     */     }
/*     */ 
/*     */     
/*     */     public String name() {
/* 468 */       return this.name;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHeaders headers() {
/* 473 */       return this.headers;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 478 */       return "Part '" + this.name + "', headers=" + this.headers;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SynchronossPart
/*     */     extends AbstractSynchronossPart
/*     */   {
/*     */     private final StreamStorage storage;
/*     */     
/*     */     SynchronossPart(HttpHeaders headers, StreamStorage storage) {
/* 488 */       super(headers);
/* 489 */       Assert.notNull(storage, "StreamStorage is required");
/* 490 */       this.storage = storage;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Flux<DataBuffer> content() {
/* 496 */       return DataBufferUtils.readInputStream(
/* 497 */           getStorage()::getInputStream, (DataBufferFactory)DefaultDataBufferFactory.sharedInstance, 4096);
/*     */     }
/*     */     
/*     */     protected StreamStorage getStorage() {
/* 501 */       return this.storage;
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> delete() {
/* 506 */       return Mono.fromRunnable(() -> {
/*     */             File file = getFile();
/*     */             if (file != null) {
/*     */               file.delete();
/*     */             }
/*     */           });
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private File getFile() {
/* 516 */       InputStream inputStream = null;
/*     */       try {
/* 518 */         inputStream = getStorage().getInputStream();
/* 519 */         if (inputStream instanceof NameAwarePurgableFileInputStream) {
/* 520 */           NameAwarePurgableFileInputStream stream = (NameAwarePurgableFileInputStream)inputStream;
/* 521 */           return stream.getFile();
/*     */         } 
/*     */       } finally {
/*     */         
/* 525 */         if (inputStream != null) {
/*     */           try {
/* 527 */             inputStream.close();
/*     */           }
/* 529 */           catch (IOException iOException) {}
/*     */         }
/*     */       } 
/*     */       
/* 533 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SynchronossFilePart
/*     */     extends SynchronossPart
/*     */     implements FilePart {
/* 540 */     private static final OpenOption[] FILE_CHANNEL_OPTIONS = new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE };
/*     */     
/*     */     private final String filename;
/*     */ 
/*     */     
/*     */     SynchronossFilePart(HttpHeaders headers, String filename, StreamStorage storage) {
/* 546 */       super(headers, storage);
/* 547 */       this.filename = filename;
/*     */     }
/*     */ 
/*     */     
/*     */     public String filename() {
/* 552 */       return this.filename;
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> transferTo(Path dest) {
/* 557 */       ReadableByteChannel input = null;
/* 558 */       FileChannel output = null;
/*     */       try {
/* 560 */         input = Channels.newChannel(getStorage().getInputStream());
/* 561 */         output = FileChannel.open(dest, FILE_CHANNEL_OPTIONS);
/* 562 */         long size = (input instanceof FileChannel) ? ((FileChannel)input).size() : Long.MAX_VALUE;
/* 563 */         long totalWritten = 0L;
/* 564 */         while (totalWritten < size) {
/* 565 */           long written = output.transferFrom(input, totalWritten, size - totalWritten);
/* 566 */           if (written <= 0L) {
/*     */             break;
/*     */           }
/* 569 */           totalWritten += written;
/*     */         }
/*     */       
/* 572 */       } catch (IOException ex) {
/* 573 */         return Mono.error(ex);
/*     */       } finally {
/*     */         
/* 576 */         if (input != null) {
/*     */           try {
/* 578 */             input.close();
/*     */           }
/* 580 */           catch (IOException iOException) {}
/*     */         }
/*     */         
/* 583 */         if (output != null) {
/*     */           try {
/* 585 */             output.close();
/*     */           }
/* 587 */           catch (IOException iOException) {}
/*     */         }
/*     */       } 
/*     */       
/* 591 */       return Mono.empty();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 596 */       return "Part '" + name() + "', filename='" + this.filename + "'";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SynchronossFormFieldPart
/*     */     extends AbstractSynchronossPart
/*     */     implements FormFieldPart {
/*     */     private final String content;
/*     */     
/*     */     SynchronossFormFieldPart(HttpHeaders headers, String content) {
/* 606 */       super(headers);
/* 607 */       this.content = content;
/*     */     }
/*     */ 
/*     */     
/*     */     public String value() {
/* 612 */       return this.content;
/*     */     }
/*     */ 
/*     */     
/*     */     public Flux<DataBuffer> content() {
/* 617 */       byte[] bytes = this.content.getBytes(getCharset());
/* 618 */       return Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(bytes));
/*     */     }
/*     */     
/*     */     private Charset getCharset() {
/* 622 */       String name = MultipartUtils.getCharEncoding((Map)headers());
/* 623 */       return (name != null) ? Charset.forName(name) : StandardCharsets.UTF_8;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 628 */       return "Part '" + name() + "=" + this.content + "'";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/SynchronossPartHttpMessageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */