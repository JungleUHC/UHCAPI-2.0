/*     */ package org.springframework.http;
/*     */ 
/*     */ import java.time.Duration;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CacheControl
/*     */ {
/*     */   @Nullable
/*     */   private Duration maxAge;
/*     */   private boolean noCache = false;
/*     */   private boolean noStore = false;
/*     */   private boolean mustRevalidate = false;
/*     */   private boolean noTransform = false;
/*     */   private boolean cachePublic = false;
/*     */   private boolean cachePrivate = false;
/*     */   private boolean proxyRevalidate = false;
/*     */   @Nullable
/*     */   private Duration staleWhileRevalidate;
/*     */   @Nullable
/*     */   private Duration staleIfError;
/*     */   @Nullable
/*     */   private Duration sMaxAge;
/*     */   
/*     */   public static CacheControl empty() {
/*  96 */     return new CacheControl();
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
/*     */   public static CacheControl maxAge(long maxAge, TimeUnit unit) {
/* 115 */     return maxAge(Duration.ofSeconds(unit.toSeconds(maxAge)));
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
/*     */   public static CacheControl maxAge(Duration maxAge) {
/* 133 */     CacheControl cc = new CacheControl();
/* 134 */     cc.maxAge = maxAge;
/* 135 */     return cc;
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
/*     */   public static CacheControl noCache() {
/* 151 */     CacheControl cc = new CacheControl();
/* 152 */     cc.noCache = true;
/* 153 */     return cc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CacheControl noStore() {
/* 164 */     CacheControl cc = new CacheControl();
/* 165 */     cc.noStore = true;
/* 166 */     return cc;
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
/*     */   public CacheControl mustRevalidate() {
/* 179 */     this.mustRevalidate = true;
/* 180 */     return this;
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
/*     */   public CacheControl noTransform() {
/* 192 */     this.noTransform = true;
/* 193 */     return this;
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
/*     */   public CacheControl cachePublic() {
/* 205 */     this.cachePublic = true;
/* 206 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CacheControl cachePrivate() {
/* 217 */     this.cachePrivate = true;
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CacheControl proxyRevalidate() {
/* 229 */     this.proxyRevalidate = true;
/* 230 */     return this;
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
/*     */   public CacheControl sMaxAge(long sMaxAge, TimeUnit unit) {
/* 244 */     return sMaxAge(Duration.ofSeconds(unit.toSeconds(sMaxAge)));
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
/*     */   public CacheControl sMaxAge(Duration sMaxAge) {
/* 257 */     this.sMaxAge = sMaxAge;
/* 258 */     return this;
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
/*     */   public CacheControl staleWhileRevalidate(long staleWhileRevalidate, TimeUnit unit) {
/* 275 */     return staleWhileRevalidate(Duration.ofSeconds(unit.toSeconds(staleWhileRevalidate)));
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
/*     */   public CacheControl staleWhileRevalidate(Duration staleWhileRevalidate) {
/* 291 */     this.staleWhileRevalidate = staleWhileRevalidate;
/* 292 */     return this;
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
/*     */   public CacheControl staleIfError(long staleIfError, TimeUnit unit) {
/* 306 */     return staleIfError(Duration.ofSeconds(unit.toSeconds(staleIfError)));
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
/*     */   public CacheControl staleIfError(Duration staleIfError) {
/* 319 */     this.staleIfError = staleIfError;
/* 320 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getHeaderValue() {
/* 329 */     String headerValue = toHeaderValue();
/* 330 */     return StringUtils.hasText(headerValue) ? headerValue : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String toHeaderValue() {
/* 338 */     StringBuilder headerValue = new StringBuilder();
/* 339 */     if (this.maxAge != null) {
/* 340 */       appendDirective(headerValue, "max-age=" + this.maxAge.getSeconds());
/*     */     }
/* 342 */     if (this.noCache) {
/* 343 */       appendDirective(headerValue, "no-cache");
/*     */     }
/* 345 */     if (this.noStore) {
/* 346 */       appendDirective(headerValue, "no-store");
/*     */     }
/* 348 */     if (this.mustRevalidate) {
/* 349 */       appendDirective(headerValue, "must-revalidate");
/*     */     }
/* 351 */     if (this.noTransform) {
/* 352 */       appendDirective(headerValue, "no-transform");
/*     */     }
/* 354 */     if (this.cachePublic) {
/* 355 */       appendDirective(headerValue, "public");
/*     */     }
/* 357 */     if (this.cachePrivate) {
/* 358 */       appendDirective(headerValue, "private");
/*     */     }
/* 360 */     if (this.proxyRevalidate) {
/* 361 */       appendDirective(headerValue, "proxy-revalidate");
/*     */     }
/* 363 */     if (this.sMaxAge != null) {
/* 364 */       appendDirective(headerValue, "s-maxage=" + this.sMaxAge.getSeconds());
/*     */     }
/* 366 */     if (this.staleIfError != null) {
/* 367 */       appendDirective(headerValue, "stale-if-error=" + this.staleIfError.getSeconds());
/*     */     }
/* 369 */     if (this.staleWhileRevalidate != null) {
/* 370 */       appendDirective(headerValue, "stale-while-revalidate=" + this.staleWhileRevalidate.getSeconds());
/*     */     }
/* 372 */     return headerValue.toString();
/*     */   }
/*     */   
/*     */   private void appendDirective(StringBuilder builder, String value) {
/* 376 */     if (builder.length() > 0) {
/* 377 */       builder.append(", ");
/*     */     }
/* 379 */     builder.append(value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 385 */     return "CacheControl [" + toHeaderValue() + "]";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/CacheControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */