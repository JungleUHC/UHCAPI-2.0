/*     */ package org.springframework.http.codec.multipart;
/*     */ 
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.concurrent.Callable;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.core.io.buffer.DefaultDataBufferFactory;
/*     */ import org.springframework.http.ContentDisposition;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.util.Assert;
/*     */ import reactor.core.publisher.Flux;
/*     */ import reactor.core.publisher.Mono;
/*     */ import reactor.core.publisher.MonoSink;
/*     */ import reactor.core.scheduler.Scheduler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class DefaultParts
/*     */ {
/*     */   public static FormFieldPart formFieldPart(HttpHeaders headers, String value) {
/*  51 */     Assert.notNull(headers, "Headers must not be null");
/*  52 */     Assert.notNull(value, "Value must not be null");
/*     */     
/*  54 */     return new DefaultFormFieldPart(headers, value);
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
/*     */   public static Part part(HttpHeaders headers, Flux<DataBuffer> dataBuffers) {
/*  67 */     Assert.notNull(headers, "Headers must not be null");
/*  68 */     Assert.notNull(dataBuffers, "DataBuffers must not be null");
/*     */     
/*  70 */     return partInternal(headers, new FluxContent(dataBuffers));
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
/*     */   public static Part part(HttpHeaders headers, Path file, Scheduler scheduler) {
/*  83 */     Assert.notNull(headers, "Headers must not be null");
/*  84 */     Assert.notNull(file, "File must not be null");
/*  85 */     Assert.notNull(scheduler, "Scheduler must not be null");
/*     */     
/*  87 */     return partInternal(headers, new FileContent(file, scheduler));
/*     */   }
/*     */ 
/*     */   
/*     */   private static Part partInternal(HttpHeaders headers, Content content) {
/*  92 */     String filename = headers.getContentDisposition().getFilename();
/*  93 */     if (filename != null) {
/*  94 */       return new DefaultFilePart(headers, content);
/*     */     }
/*     */     
/*  97 */     return new DefaultPart(headers, content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static abstract class AbstractPart
/*     */     implements Part
/*     */   {
/*     */     private final HttpHeaders headers;
/*     */ 
/*     */ 
/*     */     
/*     */     protected AbstractPart(HttpHeaders headers) {
/* 111 */       Assert.notNull(headers, "HttpHeaders is required");
/* 112 */       this.headers = headers;
/*     */     }
/*     */ 
/*     */     
/*     */     public String name() {
/* 117 */       String name = headers().getContentDisposition().getName();
/* 118 */       Assert.state((name != null), "No name available");
/* 119 */       return name;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public HttpHeaders headers() {
/* 125 */       return this.headers;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class DefaultFormFieldPart
/*     */     extends AbstractPart
/*     */     implements FormFieldPart
/*     */   {
/*     */     private final String value;
/*     */ 
/*     */     
/*     */     public DefaultFormFieldPart(HttpHeaders headers, String value) {
/* 138 */       super(headers);
/* 139 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public Flux<DataBuffer> content() {
/* 144 */       return Flux.defer(() -> {
/*     */             byte[] bytes = this.value.getBytes(MultipartUtils.charset(headers()));
/*     */             return (Publisher)Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(bytes));
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public String value() {
/* 152 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 157 */       String name = headers().getContentDisposition().getName();
/* 158 */       if (name != null) {
/* 159 */         return "DefaultFormFieldPart{" + name() + "}";
/*     */       }
/*     */       
/* 162 */       return "DefaultFormFieldPart";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class DefaultPart
/*     */     extends AbstractPart
/*     */   {
/*     */     protected final DefaultParts.Content content;
/*     */ 
/*     */ 
/*     */     
/*     */     public DefaultPart(HttpHeaders headers, DefaultParts.Content content) {
/* 177 */       super(headers);
/* 178 */       this.content = content;
/*     */     }
/*     */ 
/*     */     
/*     */     public Flux<DataBuffer> content() {
/* 183 */       return this.content.content();
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> delete() {
/* 188 */       return this.content.delete();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 193 */       String name = headers().getContentDisposition().getName();
/* 194 */       if (name != null) {
/* 195 */         return "DefaultPart{" + name + "}";
/*     */       }
/*     */       
/* 198 */       return "DefaultPart";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class DefaultFilePart
/*     */     extends DefaultPart
/*     */     implements FilePart
/*     */   {
/*     */     public DefaultFilePart(HttpHeaders headers, DefaultParts.Content content) {
/* 211 */       super(headers, content);
/*     */     }
/*     */ 
/*     */     
/*     */     public String filename() {
/* 216 */       String filename = headers().getContentDisposition().getFilename();
/* 217 */       Assert.state((filename != null), "No filename found");
/* 218 */       return filename;
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> transferTo(Path dest) {
/* 223 */       return this.content.transferTo(dest);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 228 */       ContentDisposition contentDisposition = headers().getContentDisposition();
/* 229 */       String name = contentDisposition.getName();
/* 230 */       String filename = contentDisposition.getFilename();
/* 231 */       if (name != null) {
/* 232 */         return "DefaultFilePart{" + name + " (" + filename + ")}";
/*     */       }
/*     */       
/* 235 */       return "DefaultFilePart{(" + filename + ")}";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static interface Content
/*     */   {
/*     */     Flux<DataBuffer> content();
/*     */ 
/*     */ 
/*     */     
/*     */     Mono<Void> transferTo(Path param1Path);
/*     */ 
/*     */ 
/*     */     
/*     */     Mono<Void> delete();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class FluxContent
/*     */     implements Content
/*     */   {
/*     */     private final Flux<DataBuffer> content;
/*     */ 
/*     */ 
/*     */     
/*     */     public FluxContent(Flux<DataBuffer> content) {
/* 264 */       this.content = content;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Flux<DataBuffer> content() {
/* 270 */       return this.content;
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> transferTo(Path dest) {
/* 275 */       return DataBufferUtils.write((Publisher)this.content, dest, new OpenOption[0]);
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> delete() {
/* 280 */       return Mono.empty();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class FileContent
/*     */     implements Content
/*     */   {
/*     */     private final Path file;
/*     */ 
/*     */     
/*     */     private final Scheduler scheduler;
/*     */ 
/*     */ 
/*     */     
/*     */     public FileContent(Path file, Scheduler scheduler) {
/* 297 */       this.file = file;
/* 298 */       this.scheduler = scheduler;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Flux<DataBuffer> content() {
/* 304 */       return DataBufferUtils.readByteChannel(() -> Files.newByteChannel(this.file, new OpenOption[] { StandardOpenOption.READ }), (DataBufferFactory)DefaultDataBufferFactory.sharedInstance, 1024)
/*     */ 
/*     */         
/* 307 */         .subscribeOn(this.scheduler);
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> transferTo(Path dest) {
/* 312 */       return blockingOperation(() -> Files.copy(this.file, dest, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING }));
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Void> delete() {
/* 317 */       return blockingOperation(() -> {
/*     */             Files.delete(this.file);
/*     */             return null;
/*     */           });
/*     */     }
/*     */     
/*     */     private Mono<Void> blockingOperation(Callable<?> callable) {
/* 324 */       return Mono.create(sink -> {
/*     */             try {
/*     */               callable.call();
/*     */               
/*     */               sink.success();
/* 329 */             } catch (Exception ex) {
/*     */               
/*     */               sink.error(ex);
/*     */             } 
/* 333 */           }).subscribeOn(this.scheduler);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/DefaultParts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */