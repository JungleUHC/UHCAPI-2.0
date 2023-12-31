/*     */ package org.springframework.http;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.time.Instant;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class RequestEntity<T>
/*     */   extends HttpEntity<T>
/*     */ {
/*     */   @Nullable
/*     */   private final HttpMethod method;
/*     */   @Nullable
/*     */   private final URI url;
/*     */   @Nullable
/*     */   private final Type type;
/*     */   
/*     */   public RequestEntity(HttpMethod method, URI url) {
/*  84 */     this((T)null, (MultiValueMap<String, String>)null, method, url, (Type)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestEntity(@Nullable T body, HttpMethod method, URI url) {
/*  94 */     this(body, (MultiValueMap<String, String>)null, method, url, (Type)null);
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
/*     */   public RequestEntity(@Nullable T body, HttpMethod method, URI url, Type type) {
/* 106 */     this(body, (MultiValueMap<String, String>)null, method, url, type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestEntity(MultiValueMap<String, String> headers, HttpMethod method, URI url) {
/* 116 */     this((T)null, headers, method, url, (Type)null);
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
/*     */   public RequestEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, @Nullable HttpMethod method, URI url) {
/* 129 */     this(body, headers, method, url, (Type)null);
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
/*     */   public RequestEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, @Nullable HttpMethod method, @Nullable URI url, @Nullable Type type) {
/* 144 */     super(body, headers);
/* 145 */     this.method = method;
/* 146 */     this.url = url;
/* 147 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public HttpMethod getMethod() {
/* 157 */     return this.method;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getUrl() {
/* 164 */     if (this.url == null) {
/* 165 */       throw new UnsupportedOperationException();
/*     */     }
/* 167 */     return this.url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Type getType() {
/* 178 */     if (this.type == null) {
/* 179 */       T body = getBody();
/* 180 */       if (body != null) {
/* 181 */         return body.getClass();
/*     */       }
/*     */     } 
/* 184 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 190 */     if (this == other) {
/* 191 */       return true;
/*     */     }
/* 193 */     if (!super.equals(other)) {
/* 194 */       return false;
/*     */     }
/* 196 */     RequestEntity<?> otherEntity = (RequestEntity)other;
/* 197 */     return (ObjectUtils.nullSafeEquals(this.method, otherEntity.method) && 
/* 198 */       ObjectUtils.nullSafeEquals(this.url, otherEntity.url));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 203 */     int hashCode = super.hashCode();
/* 204 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.method);
/* 205 */     hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.url);
/* 206 */     return hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 211 */     return format(getMethod(), getUrl().toString(), getBody(), getHeaders());
/*     */   }
/*     */   
/*     */   static <T> String format(@Nullable HttpMethod httpMethod, String url, @Nullable T body, HttpHeaders headers) {
/* 215 */     StringBuilder builder = new StringBuilder("<");
/* 216 */     builder.append(httpMethod);
/* 217 */     builder.append(' ');
/* 218 */     builder.append(url);
/* 219 */     builder.append(',');
/* 220 */     if (body != null) {
/* 221 */       builder.append(body);
/* 222 */       builder.append(',');
/*     */     } 
/* 224 */     builder.append(headers);
/* 225 */     builder.append('>');
/* 226 */     return builder.toString();
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
/*     */   public static BodyBuilder method(HttpMethod method, URI url) {
/* 239 */     return new DefaultBodyBuilder(method, url);
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
/*     */   public static BodyBuilder method(HttpMethod method, String uriTemplate, Object... uriVariables) {
/* 251 */     return new DefaultBodyBuilder(method, uriTemplate, uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder method(HttpMethod method, String uriTemplate, Map<String, ?> uriVariables) {
/* 262 */     return new DefaultBodyBuilder(method, uriTemplate, uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> get(URI url) {
/* 272 */     return method(HttpMethod.GET, url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> get(String uriTemplate, Object... uriVariables) {
/* 283 */     return method(HttpMethod.GET, uriTemplate, uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> head(URI url) {
/* 292 */     return method(HttpMethod.HEAD, url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> head(String uriTemplate, Object... uriVariables) {
/* 303 */     return method(HttpMethod.HEAD, uriTemplate, uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder post(URI url) {
/* 312 */     return method(HttpMethod.POST, url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder post(String uriTemplate, Object... uriVariables) {
/* 323 */     return method(HttpMethod.POST, uriTemplate, uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder put(URI url) {
/* 332 */     return method(HttpMethod.PUT, url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder put(String uriTemplate, Object... uriVariables) {
/* 343 */     return method(HttpMethod.PUT, uriTemplate, uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder patch(URI url) {
/* 352 */     return method(HttpMethod.PATCH, url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BodyBuilder patch(String uriTemplate, Object... uriVariables) {
/* 363 */     return method(HttpMethod.PATCH, uriTemplate, uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> delete(URI url) {
/* 372 */     return method(HttpMethod.DELETE, url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> delete(String uriTemplate, Object... uriVariables) {
/* 383 */     return method(HttpMethod.DELETE, uriTemplate, uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> options(URI url) {
/* 392 */     return method(HttpMethod.OPTIONS, url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HeadersBuilder<?> options(String uriTemplate, Object... uriVariables) {
/* 403 */     return method(HttpMethod.OPTIONS, uriTemplate, uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface HeadersBuilder<B extends HeadersBuilder<B>>
/*     */   {
/*     */     B header(String param1String, String... param1VarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     B headers(@Nullable HttpHeaders param1HttpHeaders);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     B headers(Consumer<HttpHeaders> param1Consumer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     B accept(MediaType... param1VarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     B acceptCharset(Charset... param1VarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     B ifModifiedSince(ZonedDateTime param1ZonedDateTime);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     B ifModifiedSince(Instant param1Instant);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     B ifModifiedSince(long param1Long);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     B ifNoneMatch(String... param1VarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     RequestEntity<Void> build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface BodyBuilder
/*     */     extends HeadersBuilder<BodyBuilder>
/*     */   {
/*     */     BodyBuilder contentLength(long param1Long);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     BodyBuilder contentType(MediaType param1MediaType);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     <T> RequestEntity<T> body(T param1T);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     <T> RequestEntity<T> body(T param1T, Type param1Type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class DefaultBodyBuilder
/*     */     implements BodyBuilder
/*     */   {
/*     */     private final HttpMethod method;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 541 */     private final HttpHeaders headers = new HttpHeaders();
/*     */     
/*     */     @Nullable
/*     */     private final URI uri;
/*     */     
/*     */     @Nullable
/*     */     private final String uriTemplate;
/*     */     
/*     */     @Nullable
/*     */     private final Object[] uriVarsArray;
/*     */     
/*     */     @Nullable
/*     */     private final Map<String, ?> uriVarsMap;
/*     */     
/*     */     DefaultBodyBuilder(HttpMethod method, URI url) {
/* 556 */       this.method = method;
/* 557 */       this.uri = url;
/* 558 */       this.uriTemplate = null;
/* 559 */       this.uriVarsArray = null;
/* 560 */       this.uriVarsMap = null;
/*     */     }
/*     */     
/*     */     DefaultBodyBuilder(HttpMethod method, String uriTemplate, Object... uriVars) {
/* 564 */       this.method = method;
/* 565 */       this.uri = null;
/* 566 */       this.uriTemplate = uriTemplate;
/* 567 */       this.uriVarsArray = uriVars;
/* 568 */       this.uriVarsMap = null;
/*     */     }
/*     */     
/*     */     DefaultBodyBuilder(HttpMethod method, String uriTemplate, Map<String, ?> uriVars) {
/* 572 */       this.method = method;
/* 573 */       this.uri = null;
/* 574 */       this.uriTemplate = uriTemplate;
/* 575 */       this.uriVarsArray = null;
/* 576 */       this.uriVarsMap = uriVars;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder header(String headerName, String... headerValues) {
/* 581 */       for (String headerValue : headerValues) {
/* 582 */         this.headers.add(headerName, headerValue);
/*     */       }
/* 584 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder headers(@Nullable HttpHeaders headers) {
/* 589 */       if (headers != null) {
/* 590 */         this.headers.putAll((Map<? extends String, ? extends List<String>>)headers);
/*     */       }
/* 592 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder headers(Consumer<HttpHeaders> headersConsumer) {
/* 597 */       headersConsumer.accept(this.headers);
/* 598 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder accept(MediaType... acceptableMediaTypes) {
/* 603 */       this.headers.setAccept(Arrays.asList(acceptableMediaTypes));
/* 604 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder acceptCharset(Charset... acceptableCharsets) {
/* 609 */       this.headers.setAcceptCharset(Arrays.asList(acceptableCharsets));
/* 610 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder contentLength(long contentLength) {
/* 615 */       this.headers.setContentLength(contentLength);
/* 616 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder contentType(MediaType contentType) {
/* 621 */       this.headers.setContentType(contentType);
/* 622 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder ifModifiedSince(ZonedDateTime ifModifiedSince) {
/* 627 */       this.headers.setIfModifiedSince(ifModifiedSince);
/* 628 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder ifModifiedSince(Instant ifModifiedSince) {
/* 633 */       this.headers.setIfModifiedSince(ifModifiedSince);
/* 634 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder ifModifiedSince(long ifModifiedSince) {
/* 639 */       this.headers.setIfModifiedSince(ifModifiedSince);
/* 640 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity.BodyBuilder ifNoneMatch(String... ifNoneMatches) {
/* 645 */       this.headers.setIfNoneMatch(Arrays.asList(ifNoneMatches));
/* 646 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestEntity<Void> build() {
/* 651 */       return buildInternal(null, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> RequestEntity<T> body(T body) {
/* 656 */       return buildInternal(body, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> RequestEntity<T> body(T body, Type type) {
/* 661 */       return buildInternal(body, type);
/*     */     }
/*     */     
/*     */     private <T> RequestEntity<T> buildInternal(@Nullable T body, @Nullable Type type) {
/* 665 */       if (this.uri != null) {
/* 666 */         return new RequestEntity<>(body, this.headers, this.method, this.uri, type);
/*     */       }
/* 668 */       if (this.uriTemplate != null) {
/* 669 */         return new RequestEntity.UriTemplateRequestEntity<>(body, this.headers, this.method, type, this.uriTemplate, this.uriVarsArray, this.uriVarsMap);
/*     */       }
/*     */ 
/*     */       
/* 673 */       throw new IllegalStateException("Neither URI nor URI template");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class UriTemplateRequestEntity<T>
/*     */     extends RequestEntity<T>
/*     */   {
/*     */     private final String uriTemplate;
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private final Object[] uriVarsArray;
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private final Map<String, ?> uriVarsMap;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     UriTemplateRequestEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, @Nullable HttpMethod method, @Nullable Type type, String uriTemplate, @Nullable Object[] uriVarsArray, @Nullable Map<String, ?> uriVarsMap) {
/* 699 */       super(body, headers, method, (URI)null, type);
/* 700 */       this.uriTemplate = uriTemplate;
/* 701 */       this.uriVarsArray = uriVarsArray;
/* 702 */       this.uriVarsMap = uriVarsMap;
/*     */     }
/*     */     
/*     */     public String getUriTemplate() {
/* 706 */       return this.uriTemplate;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public Object[] getVars() {
/* 711 */       return this.uriVarsArray;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public Map<String, ?> getVarsMap() {
/* 716 */       return this.uriVarsMap;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 721 */       if (this == other) {
/* 722 */         return true;
/*     */       }
/* 724 */       if (!super.equals(other)) {
/* 725 */         return false;
/*     */       }
/* 727 */       UriTemplateRequestEntity<?> otherEntity = (UriTemplateRequestEntity)other;
/* 728 */       return (ObjectUtils.nullSafeEquals(this.uriTemplate, otherEntity.uriTemplate) && 
/* 729 */         ObjectUtils.nullSafeEquals(this.uriVarsArray, otherEntity.uriVarsArray) && 
/* 730 */         ObjectUtils.nullSafeEquals(this.uriVarsMap, otherEntity.uriVarsMap));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 735 */       return 29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.uriTemplate);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 740 */       return format(getMethod(), getUriTemplate(), getBody(), getHeaders());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/RequestEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */