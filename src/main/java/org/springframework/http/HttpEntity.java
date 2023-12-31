/*     */ package org.springframework.http;
/*     */ 
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
/*     */ public class HttpEntity<T>
/*     */ {
/*  63 */   public static final HttpEntity<?> EMPTY = new HttpEntity();
/*     */ 
/*     */ 
/*     */   
/*     */   private final HttpHeaders headers;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final T body;
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpEntity() {
/*  76 */     this(null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpEntity(T body) {
/*  84 */     this(body, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpEntity(MultiValueMap<String, String> headers) {
/*  92 */     this(null, headers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers) {
/* 101 */     this.body = body;
/* 102 */     this.headers = HttpHeaders.readOnlyHttpHeaders((headers != null) ? headers : new HttpHeaders());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/* 110 */     return this.headers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public T getBody() {
/* 118 */     return this.body;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasBody() {
/* 125 */     return (this.body != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 131 */     if (this == other) {
/* 132 */       return true;
/*     */     }
/* 134 */     if (other == null || other.getClass() != getClass()) {
/* 135 */       return false;
/*     */     }
/* 137 */     HttpEntity<?> otherEntity = (HttpEntity)other;
/* 138 */     return (ObjectUtils.nullSafeEquals(this.headers, otherEntity.headers) && 
/* 139 */       ObjectUtils.nullSafeEquals(this.body, otherEntity.body));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 144 */     return ObjectUtils.nullSafeHashCode(this.headers) * 29 + ObjectUtils.nullSafeHashCode(this.body);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 149 */     StringBuilder builder = new StringBuilder("<");
/* 150 */     if (this.body != null) {
/* 151 */       builder.append(this.body);
/* 152 */       builder.append(',');
/*     */     } 
/* 154 */     builder.append(this.headers);
/* 155 */     builder.append('>');
/* 156 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/HttpEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */