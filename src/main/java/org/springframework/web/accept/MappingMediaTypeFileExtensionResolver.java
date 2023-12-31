/*     */ package org.springframework.web.accept;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MappingMediaTypeFileExtensionResolver
/*     */   implements MediaTypeFileExtensionResolver
/*     */ {
/*  46 */   private final ConcurrentMap<String, MediaType> mediaTypes = new ConcurrentHashMap<>(64);
/*     */   
/*  48 */   private final ConcurrentMap<MediaType, List<String>> fileExtensions = new ConcurrentHashMap<>(64);
/*     */   
/*  50 */   private final List<String> allFileExtensions = new CopyOnWriteArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MappingMediaTypeFileExtensionResolver(@Nullable Map<String, MediaType> mediaTypes) {
/*  57 */     if (mediaTypes != null) {
/*  58 */       Set<String> allFileExtensions = new HashSet<>(mediaTypes.size());
/*  59 */       mediaTypes.forEach((extension, mediaType) -> {
/*     */             String lowerCaseExtension = extension.toLowerCase(Locale.ENGLISH);
/*     */             this.mediaTypes.put(lowerCaseExtension, mediaType);
/*     */             addFileExtension(mediaType, lowerCaseExtension);
/*     */             allFileExtensions.add(lowerCaseExtension);
/*     */           });
/*  65 */       this.allFileExtensions.addAll(allFileExtensions);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, MediaType> getMediaTypes() {
/*  71 */     return this.mediaTypes;
/*     */   }
/*     */   
/*     */   protected List<MediaType> getAllMediaTypes() {
/*  75 */     return new ArrayList<>(this.mediaTypes.values());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addMapping(String extension, MediaType mediaType) {
/*  82 */     MediaType previous = this.mediaTypes.putIfAbsent(extension, mediaType);
/*  83 */     if (previous == null) {
/*  84 */       addFileExtension(mediaType, extension);
/*  85 */       this.allFileExtensions.add(extension);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addFileExtension(MediaType mediaType, String extension) {
/*  90 */     ((List<String>)this.fileExtensions.computeIfAbsent(mediaType, key -> new CopyOnWriteArrayList()))
/*  91 */       .add(extension);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> resolveFileExtensions(MediaType mediaType) {
/*  97 */     List<String> fileExtensions = this.fileExtensions.get(mediaType);
/*  98 */     return (fileExtensions != null) ? fileExtensions : Collections.<String>emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getAllFileExtensions() {
/* 103 */     return Collections.unmodifiableList(this.allFileExtensions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected MediaType lookupMediaType(String extension) {
/* 112 */     return this.mediaTypes.get(extension.toLowerCase(Locale.ENGLISH));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/accept/MappingMediaTypeFileExtensionResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */