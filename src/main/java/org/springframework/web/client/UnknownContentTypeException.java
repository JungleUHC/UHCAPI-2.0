/*     */ package org.springframework.web.client;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.springframework.http.HttpHeaders;
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
/*     */ public class UnknownContentTypeException
/*     */   extends RestClientException
/*     */ {
/*     */   private static final long serialVersionUID = 2759516676367274084L;
/*     */   private final Type targetType;
/*     */   private final MediaType contentType;
/*     */   private final int rawStatusCode;
/*     */   private final String statusText;
/*     */   private final byte[] responseBody;
/*     */   private final HttpHeaders responseHeaders;
/*     */   
/*     */   public UnknownContentTypeException(Type targetType, MediaType contentType, int statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody) {
/*  64 */     super("Could not extract response: no suitable HttpMessageConverter found for response type [" + targetType + "] and content type [" + contentType + "]");
/*     */ 
/*     */     
/*  67 */     this.targetType = targetType;
/*  68 */     this.contentType = contentType;
/*  69 */     this.rawStatusCode = statusCode;
/*  70 */     this.statusText = statusText;
/*  71 */     this.responseHeaders = responseHeaders;
/*  72 */     this.responseBody = responseBody;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Type getTargetType() {
/*  80 */     return this.targetType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MediaType getContentType() {
/*  87 */     return this.contentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRawStatusCode() {
/*  94 */     return this.rawStatusCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStatusText() {
/* 101 */     return this.statusText;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public HttpHeaders getResponseHeaders() {
/* 109 */     return this.responseHeaders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getResponseBody() {
/* 116 */     return this.responseBody;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getResponseBodyAsString() {
/* 124 */     return new String(this.responseBody, (this.contentType.getCharset() != null) ? this.contentType
/* 125 */         .getCharset() : StandardCharsets.UTF_8);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/UnknownContentTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */