/*     */ package org.springframework.web.server;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UnsupportedMediaTypeStatusException
/*     */   extends ResponseStatusException
/*     */ {
/*     */   @Nullable
/*     */   private final MediaType contentType;
/*     */   private final List<MediaType> supportedMediaTypes;
/*     */   @Nullable
/*     */   private final ResolvableType bodyType;
/*     */   @Nullable
/*     */   private final HttpMethod method;
/*     */   
/*     */   public UnsupportedMediaTypeStatusException(@Nullable String reason) {
/*  55 */     super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason);
/*  56 */     this.contentType = null;
/*  57 */     this.supportedMediaTypes = Collections.emptyList();
/*  58 */     this.bodyType = null;
/*  59 */     this.method = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes) {
/*  66 */     this(contentType, supportedTypes, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable ResolvableType bodyType) {
/*  75 */     this(contentType, supportedTypes, bodyType, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable HttpMethod method) {
/*  84 */     this(contentType, supportedTypes, null, method);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable ResolvableType bodyType, @Nullable HttpMethod method) {
/*  94 */     super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, initReason(contentType, bodyType));
/*  95 */     this.contentType = contentType;
/*  96 */     this.supportedMediaTypes = Collections.unmodifiableList(supportedTypes);
/*  97 */     this.bodyType = bodyType;
/*  98 */     this.method = method;
/*     */   }
/*     */   
/*     */   private static String initReason(@Nullable MediaType contentType, @Nullable ResolvableType bodyType) {
/* 102 */     return "Content type '" + ((contentType != null) ? (String)contentType : "") + "' not supported" + ((bodyType != null) ? (" for bodyType=" + bodyType
/* 103 */       .toString()) : "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public MediaType getContentType() {
/* 113 */     return this.contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<MediaType> getSupportedMediaTypes() {
/* 121 */     return this.supportedMediaTypes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ResolvableType getBodyType() {
/* 133 */     return this.bodyType;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getResponseHeaders() {
/* 138 */     if (HttpMethod.PATCH != this.method || CollectionUtils.isEmpty(this.supportedMediaTypes)) {
/* 139 */       return HttpHeaders.EMPTY;
/*     */     }
/* 141 */     HttpHeaders headers = new HttpHeaders();
/* 142 */     headers.setAcceptPatch(this.supportedMediaTypes);
/* 143 */     return headers;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/UnsupportedMediaTypeStatusException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */