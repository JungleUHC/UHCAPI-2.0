/*     */ package org.springframework.web.server;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import org.springframework.core.NestedExceptionUtils;
/*     */ import org.springframework.core.NestedRuntimeException;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResponseStatusException
/*     */   extends NestedRuntimeException
/*     */ {
/*     */   private final int status;
/*     */   @Nullable
/*     */   private final String reason;
/*     */   
/*     */   public ResponseStatusException(HttpStatus status) {
/*  50 */     this(status, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResponseStatusException(HttpStatus status, @Nullable String reason) {
/*  60 */     super("");
/*  61 */     Assert.notNull(status, "HttpStatus is required");
/*  62 */     this.status = status.value();
/*  63 */     this.reason = reason;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResponseStatusException(HttpStatus status, @Nullable String reason, @Nullable Throwable cause) {
/*  74 */     super(null, cause);
/*  75 */     Assert.notNull(status, "HttpStatus is required");
/*  76 */     this.status = status.value();
/*  77 */     this.reason = reason;
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
/*     */   public ResponseStatusException(int rawStatusCode, @Nullable String reason, @Nullable Throwable cause) {
/*  89 */     super(null, cause);
/*  90 */     this.status = rawStatusCode;
/*  91 */     this.reason = reason;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpStatus getStatus() {
/* 102 */     return HttpStatus.valueOf(this.status);
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
/*     */   public int getRawStatusCode() {
/* 114 */     return this.status;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Map<String, String> getHeaders() {
/* 126 */     return Collections.emptyMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHeaders getResponseHeaders() {
/* 136 */     Map<String, String> headers = getHeaders();
/* 137 */     if (headers.isEmpty()) {
/* 138 */       return HttpHeaders.EMPTY;
/*     */     }
/* 140 */     HttpHeaders result = new HttpHeaders();
/* 141 */     getHeaders().forEach(result::add);
/* 142 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getReason() {
/* 150 */     return this.reason;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/* 156 */     HttpStatus code = HttpStatus.resolve(this.status);
/* 157 */     String msg = ((code != null) ? (String)code : (String)Integer.valueOf(this.status)) + ((this.reason != null) ? (" \"" + this.reason + "\"") : "");
/* 158 */     return NestedExceptionUtils.buildMessage(msg, getCause());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/ResponseStatusException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */