/*     */ package org.springframework.web.client;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HttpStatusCodeException
/*     */   extends RestClientResponseException
/*     */ {
/*     */   private static final long serialVersionUID = 5696801857651587810L;
/*     */   private final HttpStatus statusCode;
/*     */   
/*     */   protected HttpStatusCodeException(HttpStatus statusCode) {
/*  47 */     this(statusCode, statusCode.name(), null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpStatusCodeException(HttpStatus statusCode, String statusText) {
/*  56 */     this(statusCode, statusText, null, null, null);
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
/*     */   protected HttpStatusCodeException(HttpStatus statusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
/*  70 */     this(statusCode, statusText, null, responseBody, responseCharset);
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
/*     */   
/*     */   protected HttpStatusCodeException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
/*  86 */     this(getMessage(statusCode, statusText), statusCode, statusText, responseHeaders, responseBody, responseCharset);
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpStatusCodeException(String message, HttpStatus statusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
/* 104 */     super(message, statusCode.value(), statusText, responseHeaders, responseBody, responseCharset);
/* 105 */     this.statusCode = statusCode;
/*     */   }
/*     */   
/*     */   private static String getMessage(HttpStatus statusCode, String statusText) {
/* 109 */     if (!StringUtils.hasLength(statusText)) {
/* 110 */       statusText = statusCode.getReasonPhrase();
/*     */     }
/* 112 */     return statusCode.value() + " " + statusText;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpStatus getStatusCode() {
/* 119 */     return this.statusCode;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/HttpStatusCodeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */