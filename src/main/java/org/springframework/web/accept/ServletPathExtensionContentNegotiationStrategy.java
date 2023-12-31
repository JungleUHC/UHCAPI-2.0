/*     */ package org.springframework.web.accept;
/*     */ 
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.HttpMediaTypeNotAcceptableException;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class ServletPathExtensionContentNegotiationStrategy
/*     */   extends PathExtensionContentNegotiationStrategy
/*     */ {
/*     */   private final ServletContext servletContext;
/*     */   
/*     */   public ServletPathExtensionContentNegotiationStrategy(ServletContext context) {
/*  54 */     this(context, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletPathExtensionContentNegotiationStrategy(ServletContext servletContext, @Nullable Map<String, MediaType> mediaTypes) {
/*  63 */     super(mediaTypes);
/*  64 */     Assert.notNull(servletContext, "ServletContext is required");
/*  65 */     this.servletContext = servletContext;
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
/*     */   @Nullable
/*     */   protected MediaType handleNoMatch(NativeWebRequest webRequest, String extension) throws HttpMediaTypeNotAcceptableException {
/*  79 */     MediaType mediaType = null;
/*  80 */     String mimeType = this.servletContext.getMimeType("file." + extension);
/*  81 */     if (StringUtils.hasText(mimeType)) {
/*  82 */       mediaType = MediaType.parseMediaType(mimeType);
/*     */     }
/*  84 */     if (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
/*  85 */       MediaType superMediaType = super.handleNoMatch(webRequest, extension);
/*  86 */       if (superMediaType != null) {
/*  87 */         mediaType = superMediaType;
/*     */       }
/*     */     } 
/*  90 */     return mediaType;
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
/*     */   public MediaType getMediaTypeForResource(Resource resource) {
/* 103 */     MediaType mediaType = null;
/* 104 */     String mimeType = this.servletContext.getMimeType(resource.getFilename());
/* 105 */     if (StringUtils.hasText(mimeType)) {
/* 106 */       mediaType = MediaType.parseMediaType(mimeType);
/*     */     }
/* 108 */     if (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
/* 109 */       MediaType superMediaType = super.getMediaTypeForResource(resource);
/* 110 */       if (superMediaType != null) {
/* 111 */         mediaType = superMediaType;
/*     */       }
/*     */     } 
/* 114 */     return mediaType;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/accept/ServletPathExtensionContentNegotiationStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */