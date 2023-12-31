/*     */ package org.springframework.http.codec.multipart;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.function.Supplier;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import reactor.core.publisher.Mono;
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
/*     */ abstract class FileStorage
/*     */ {
/*  38 */   private static final Log logger = LogFactory.getLog(FileStorage.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FileStorage fromPath(Path path) throws IOException {
/*  55 */     if (!Files.exists(path, new java.nio.file.LinkOption[0])) {
/*  56 */       Files.createDirectory(path, (FileAttribute<?>[])new FileAttribute[0]);
/*     */     }
/*  58 */     return new PathFileStorage(path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FileStorage tempDirectory(Supplier<Scheduler> scheduler) {
/*  66 */     return new TempFileStorage(scheduler);
/*     */   }
/*     */   
/*     */   public abstract Mono<Path> directory();
/*     */   
/*     */   private static final class PathFileStorage extends FileStorage {
/*     */     private final Mono<Path> directory;
/*     */     
/*     */     public PathFileStorage(Path directory) {
/*  75 */       this.directory = Mono.just(directory);
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Path> directory() {
/*  80 */       return this.directory;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class TempFileStorage
/*     */     extends FileStorage
/*     */   {
/*     */     private static final String IDENTIFIER = "spring-multipart-";
/*     */     
/*     */     private final Supplier<Scheduler> scheduler;
/*  91 */     private volatile Mono<Path> directory = tempDirectory();
/*     */ 
/*     */     
/*     */     public TempFileStorage(Supplier<Scheduler> scheduler) {
/*  95 */       this.scheduler = scheduler;
/*     */     }
/*     */ 
/*     */     
/*     */     public Mono<Path> directory() {
/* 100 */       return this.directory
/* 101 */         .flatMap(this::createNewDirectoryIfDeleted)
/* 102 */         .subscribeOn(this.scheduler.get());
/*     */     }
/*     */     
/*     */     private Mono<Path> createNewDirectoryIfDeleted(Path directory) {
/* 106 */       if (!Files.exists(directory, new java.nio.file.LinkOption[0])) {
/*     */         
/* 108 */         Mono<Path> newDirectory = tempDirectory();
/* 109 */         this.directory = newDirectory;
/* 110 */         return newDirectory;
/*     */       } 
/*     */       
/* 113 */       return Mono.just(directory);
/*     */     }
/*     */ 
/*     */     
/*     */     private static Mono<Path> tempDirectory() {
/* 118 */       return Mono.fromCallable(() -> {
/*     */             Path directory = Files.createTempDirectory("spring-multipart-", (FileAttribute<?>[])new FileAttribute[0]);
/*     */             if (FileStorage.logger.isDebugEnabled()) {
/*     */               FileStorage.logger.debug("Created temporary storage directory: " + directory);
/*     */             }
/*     */             return directory;
/* 124 */           }).cache();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/multipart/FileStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */