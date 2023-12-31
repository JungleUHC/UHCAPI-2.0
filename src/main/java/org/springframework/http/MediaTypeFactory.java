/*     */ package org.springframework.http;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Optional;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ public final class MediaTypeFactory
/*     */ {
/*     */   private static final String MIME_TYPES_FILE_NAME = "/org/springframework/http/mime.types";
/*  48 */   private static final MultiValueMap<String, MediaType> fileExtensionToMediaTypes = parseMimeTypes();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static MultiValueMap<String, MediaType> parseMimeTypes() {
/*  68 */     InputStream is = MediaTypeFactory.class.getResourceAsStream("/org/springframework/http/mime.types");
/*  69 */     Assert.state((is != null), "/org/springframework/http/mime.types not found in classpath");
/*  70 */     try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII))) {
/*  71 */       LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/*     */       String line;
/*  73 */       while ((line = reader.readLine()) != null) {
/*  74 */         if (line.isEmpty() || line.charAt(0) == '#') {
/*     */           continue;
/*     */         }
/*  77 */         String[] tokens = StringUtils.tokenizeToStringArray(line, " \t\n\r\f");
/*  78 */         MediaType mediaType = MediaType.parseMediaType(tokens[0]);
/*  79 */         for (int i = 1; i < tokens.length; i++) {
/*  80 */           String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
/*  81 */           linkedMultiValueMap.add(fileExtension, mediaType);
/*     */         } 
/*     */       } 
/*  84 */       return (MultiValueMap<String, MediaType>)linkedMultiValueMap;
/*     */     }
/*  86 */     catch (IOException ex) {
/*  87 */       throw new IllegalStateException("Could not read /org/springframework/http/mime.types", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Optional<MediaType> getMediaType(@Nullable Resource resource) {
/*  97 */     return Optional.<Resource>ofNullable(resource)
/*  98 */       .map(Resource::getFilename)
/*  99 */       .flatMap(MediaTypeFactory::getMediaType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Optional<MediaType> getMediaType(@Nullable String filename) {
/* 108 */     return getMediaTypes(filename).stream().findFirst();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<MediaType> getMediaTypes(@Nullable String filename) {
/* 117 */     List<MediaType> mediaTypes = null;
/* 118 */     String ext = StringUtils.getFilenameExtension(filename);
/* 119 */     if (ext != null) {
/* 120 */       mediaTypes = (List<MediaType>)fileExtensionToMediaTypes.get(ext.toLowerCase(Locale.ENGLISH));
/*     */     }
/* 122 */     return (mediaTypes != null) ? mediaTypes : Collections.<MediaType>emptyList();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/MediaTypeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */