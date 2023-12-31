/*     */ package org.springframework.web.accept;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.MediaTypeFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ import org.springframework.web.util.UriUtils;
/*     */ import org.springframework.web.util.UrlPathHelper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public class PathExtensionContentNegotiationStrategy
/*     */   extends AbstractMappingContentNegotiationStrategy
/*     */ {
/*  51 */   private UrlPathHelper urlPathHelper = new UrlPathHelper();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathExtensionContentNegotiationStrategy() {
/*  59 */     this((Map<String, MediaType>)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathExtensionContentNegotiationStrategy(@Nullable Map<String, MediaType> mediaTypes) {
/*  66 */     super(mediaTypes);
/*  67 */     setUseRegisteredExtensionsOnly(false);
/*  68 */     setIgnoreUnknownExtensions(true);
/*  69 */     this.urlPathHelper.setUrlDecode(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
/*  79 */     this.urlPathHelper = urlPathHelper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setUseJaf(boolean useJaf) {
/*  89 */     setUseRegisteredExtensionsOnly(!useJaf);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String getMediaTypeKey(NativeWebRequest webRequest) {
/*  95 */     HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
/*  96 */     if (request == null) {
/*  97 */       return null;
/*     */     }
/*     */     
/* 100 */     String path = this.urlPathHelper.getLookupPathForRequest(request);
/* 101 */     String extension = UriUtils.extractFileExtension(path);
/* 102 */     return StringUtils.hasText(extension) ? extension.toLowerCase(Locale.ENGLISH) : null;
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
/*     */   public MediaType getMediaTypeForResource(Resource resource) {
/* 116 */     Assert.notNull(resource, "Resource must not be null");
/* 117 */     MediaType mediaType = null;
/* 118 */     String filename = resource.getFilename();
/* 119 */     String extension = StringUtils.getFilenameExtension(filename);
/* 120 */     if (extension != null) {
/* 121 */       mediaType = lookupMediaType(extension);
/*     */     }
/* 123 */     if (mediaType == null) {
/* 124 */       mediaType = MediaTypeFactory.getMediaType(filename).orElse(null);
/*     */     }
/* 126 */     return mediaType;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/accept/PathExtensionContentNegotiationStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */