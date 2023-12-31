/*     */ package org.springframework.http;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.time.Instant;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Consumer;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.ObjectUtils;
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
/*     */ public class ResponseEntity<T>
/*     */   extends HttpEntity<T>
/*     */ {
/*     */   private final Object status;
/*     */   
/*     */   public ResponseEntity(HttpStatus status) {
/*  89 */     this((T)null, (MultiValueMap<String, String>)null, status);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResponseEntity(@Nullable T body, HttpStatus status) {
/*  98 */     this(body, (MultiValueMap<String, String>)null, status);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResponseEntity(MultiValueMap<String, String> headers, HttpStatus status) {
/* 107 */     this((T)null, headers, status);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, HttpStatus status) {
/* 117 */     this(body, headers, status);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, int rawStatus) {
/* 128 */     this(body, headers, Integer.valueOf(rawStatus));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, Object status) {
/* 135 */     super(body, headers);
/* 136 */     Assert.notNull(status, "HttpStatus must not be null");
/* 137 */     this.status = status;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpStatus getStatusCode() {
/* 146 */     if (this.status instanceof HttpStatus) {
/* 147 */       return (HttpStatus)this.status;
/*     */     }
/*     */     
/* 150 */     return HttpStatus.valueOf(((Integer)this.status).intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStatusCodeValue() {
/* 160 */     if (this.status instanceof HttpStatus) {
/* 161 */       return ((HttpStatus)this.status).value();
/*     */     }
/*     */     
/* 164 */     return ((Integer)this.status).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 171 */     if (this == other) {
/* 172 */       return true;
/*     */     }
/* 174 */     if (!super.equals(other)) {
/* 175 */       return false;
/*     */     }
/* 177 */     ResponseEntity<?> otherEntity = (ResponseEntity)other;
/* 178 */     return ObjectUtils.nullSafeEquals(this.status, otherEntity.status);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 183 */     return 29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.status);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 188 */     StringBuilder builder = new StringBuilder("<");
/* 189 */     builder.append(this.status);
/* 190 */     if (this.status instanceof HttpStatus) {
/* 191 */       builder.append(' ');
/* 192 */       builder.append(((HttpStatus)this.status).getReasonPhrase());
/*     */     } 
/* 194 */     builder.append(',');
/* 195 */     T body = getBody();
/* 196 */     HttpHeaders headers = getHeaders();
/* 197 */     if (body != null) {
/* 198 */       builder.append(body);
/* 199 */       builder.append(',');
/*     */     } 
/* 201 */     builder.append(headers);
/* 202 */     builder.append('>');
/* 203 */     return builder.toString();
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
/*     */   public static BodyBuilder status(HttpStatus status) {
/* 216 */     Assert.notNull(status, "HttpStatus must not be null");
/* 217 */     return new DefaultBuilder(status);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder status(int status) {
/* 227 */     return new DefaultBuilder(Integer.valueOf(status));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder ok() {
/* 236 */     return status(HttpStatus.OK);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ResponseEntity<T> ok(@Nullable T body) {
/* 247 */     return ok().body(body);
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
/*     */   public static <T> ResponseEntity<T> of(Optional<T> body) {
/* 259 */     Assert.notNull(body, "Body must not be null");
/* 260 */     return body.<ResponseEntity<T>>map(ResponseEntity::ok).orElseGet(() -> notFound().build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder created(URI location) {
/* 271 */     return status(HttpStatus.CREATED).location(location);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder accepted() {
/* 280 */     return status(HttpStatus.ACCEPTED);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> noContent() {
/* 289 */     return status(HttpStatus.NO_CONTENT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder badRequest() {
/* 298 */     return status(HttpStatus.BAD_REQUEST);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> notFound() {
/* 307 */     return status(HttpStatus.NOT_FOUND);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder unprocessableEntity() {
/* 317 */     return status(HttpStatus.UNPROCESSABLE_ENTITY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder internalServerError() {
/* 327 */     return status(HttpStatus.INTERNAL_SERVER_ERROR);
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
/*     */   private static class DefaultBuilder
/*     */     implements BodyBuilder
/*     */   {
/*     */     private final Object statusCode;
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
/* 494 */     private final HttpHeaders headers = new HttpHeaders();
/*     */     
/*     */     public DefaultBuilder(Object statusCode) {
/* 497 */       this.statusCode = statusCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder header(String headerName, String... headerValues) {
/* 502 */       for (String headerValue : headerValues) {
/* 503 */         this.headers.add(headerName, headerValue);
/*     */       }
/* 505 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder headers(@Nullable HttpHeaders headers) {
/* 510 */       if (headers != null) {
/* 511 */         this.headers.putAll((Map<? extends String, ? extends List<String>>)headers);
/*     */       }
/* 513 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder headers(Consumer<HttpHeaders> headersConsumer) {
/* 518 */       headersConsumer.accept(this.headers);
/* 519 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder allow(HttpMethod... allowedMethods) {
/* 524 */       this.headers.setAllow(new LinkedHashSet<>(Arrays.asList(allowedMethods)));
/* 525 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder contentLength(long contentLength) {
/* 530 */       this.headers.setContentLength(contentLength);
/* 531 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder contentType(MediaType contentType) {
/* 536 */       this.headers.setContentType(contentType);
/* 537 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder eTag(String etag) {
/* 542 */       if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
/* 543 */         etag = "\"" + etag;
/*     */       }
/* 545 */       if (!etag.endsWith("\"")) {
/* 546 */         etag = etag + "\"";
/*     */       }
/* 548 */       this.headers.setETag(etag);
/* 549 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder lastModified(ZonedDateTime date) {
/* 554 */       this.headers.setLastModified(date);
/* 555 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder lastModified(Instant date) {
/* 560 */       this.headers.setLastModified(date);
/* 561 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder lastModified(long date) {
/* 566 */       this.headers.setLastModified(date);
/* 567 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder location(URI location) {
/* 572 */       this.headers.setLocation(location);
/* 573 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder cacheControl(CacheControl cacheControl) {
/* 578 */       this.headers.setCacheControl(cacheControl);
/* 579 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ResponseEntity.BodyBuilder varyBy(String... requestHeaders) {
/* 584 */       this.headers.setVary(Arrays.asList(requestHeaders));
/* 585 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> ResponseEntity<T> build() {
/* 590 */       return body(null);
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> ResponseEntity<T> body(@Nullable T body) {
/* 595 */       return new ResponseEntity<>(body, this.headers, this.statusCode);
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface BodyBuilder extends HeadersBuilder<BodyBuilder> {
/*     */     BodyBuilder contentLength(long param1Long);
/*     */     
/*     */     BodyBuilder contentType(MediaType param1MediaType);
/*     */     
/*     */     <T> ResponseEntity<T> body(@Nullable T param1T);
/*     */   }
/*     */   
/*     */   public static interface HeadersBuilder<B extends HeadersBuilder<B>> {
/*     */     B header(String param1String, String... param1VarArgs);
/*     */     
/*     */     B headers(@Nullable HttpHeaders param1HttpHeaders);
/*     */     
/*     */     B headers(Consumer<HttpHeaders> param1Consumer);
/*     */     
/*     */     B allow(HttpMethod... param1VarArgs);
/*     */     
/*     */     B eTag(String param1String);
/*     */     
/*     */     B lastModified(ZonedDateTime param1ZonedDateTime);
/*     */     
/*     */     B lastModified(Instant param1Instant);
/*     */     
/*     */     B lastModified(long param1Long);
/*     */     
/*     */     B location(URI param1URI);
/*     */     
/*     */     B cacheControl(CacheControl param1CacheControl);
/*     */     
/*     */     B varyBy(String... param1VarArgs);
/*     */     
/*     */     <T> ResponseEntity<T> build();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/ResponseEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */