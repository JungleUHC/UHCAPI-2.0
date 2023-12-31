/*     */ package org.springframework.http;
/*     */ 
/*     */ import java.time.Duration;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
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
/*     */ public final class ResponseCookie
/*     */   extends HttpCookie
/*     */ {
/*     */   private final Duration maxAge;
/*     */   @Nullable
/*     */   private final String domain;
/*     */   @Nullable
/*     */   private final String path;
/*     */   private final boolean secure;
/*     */   private final boolean httpOnly;
/*     */   @Nullable
/*     */   private final String sameSite;
/*     */   
/*     */   private ResponseCookie(String name, String value, Duration maxAge, @Nullable String domain, @Nullable String path, boolean secure, boolean httpOnly, @Nullable String sameSite) {
/*  60 */     super(name, value);
/*  61 */     Assert.notNull(maxAge, "Max age must not be null");
/*     */     
/*  63 */     this.maxAge = maxAge;
/*  64 */     this.domain = domain;
/*  65 */     this.path = path;
/*  66 */     this.secure = secure;
/*  67 */     this.httpOnly = httpOnly;
/*  68 */     this.sameSite = sameSite;
/*     */     
/*  70 */     Rfc6265Utils.validateCookieName(name);
/*  71 */     Rfc6265Utils.validateCookieValue(value);
/*  72 */     Rfc6265Utils.validateDomain(domain);
/*  73 */     Rfc6265Utils.validatePath(path);
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
/*     */   public Duration getMaxAge() {
/*  85 */     return this.maxAge;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getDomain() {
/*  93 */     return this.domain;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getPath() {
/* 101 */     return this.path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 108 */     return this.secure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isHttpOnly() {
/* 116 */     return this.httpOnly;
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
/*     */   public String getSameSite() {
/* 128 */     return this.sameSite;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 134 */     if (this == other) {
/* 135 */       return true;
/*     */     }
/* 137 */     if (!(other instanceof ResponseCookie)) {
/* 138 */       return false;
/*     */     }
/* 140 */     ResponseCookie otherCookie = (ResponseCookie)other;
/* 141 */     return (getName().equalsIgnoreCase(otherCookie.getName()) && 
/* 142 */       ObjectUtils.nullSafeEquals(this.path, otherCookie.getPath()) && 
/* 143 */       ObjectUtils.nullSafeEquals(this.domain, otherCookie.getDomain()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 148 */     int result = super.hashCode();
/* 149 */     result = 31 * result + ObjectUtils.nullSafeHashCode(this.domain);
/* 150 */     result = 31 * result + ObjectUtils.nullSafeHashCode(this.path);
/* 151 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 156 */     StringBuilder sb = new StringBuilder();
/* 157 */     sb.append(getName()).append('=').append(getValue());
/* 158 */     if (StringUtils.hasText(getPath())) {
/* 159 */       sb.append("; Path=").append(getPath());
/*     */     }
/* 161 */     if (StringUtils.hasText(this.domain)) {
/* 162 */       sb.append("; Domain=").append(this.domain);
/*     */     }
/* 164 */     if (!this.maxAge.isNegative()) {
/* 165 */       sb.append("; Max-Age=").append(this.maxAge.getSeconds());
/* 166 */       sb.append("; Expires=");
/* 167 */       long millis = (this.maxAge.getSeconds() > 0L) ? (System.currentTimeMillis() + this.maxAge.toMillis()) : 0L;
/* 168 */       sb.append(HttpHeaders.formatDate(millis));
/*     */     } 
/* 170 */     if (this.secure) {
/* 171 */       sb.append("; Secure");
/*     */     }
/* 173 */     if (this.httpOnly) {
/* 174 */       sb.append("; HttpOnly");
/*     */     }
/* 176 */     if (StringUtils.hasText(this.sameSite)) {
/* 177 */       sb.append("; SameSite=").append(this.sameSite);
/*     */     }
/* 179 */     return sb.toString();
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
/*     */   public static ResponseCookieBuilder from(String name, String value) {
/* 191 */     return from(name, value, false);
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
/*     */   public static ResponseCookieBuilder fromClientResponse(String name, String value) {
/* 205 */     return from(name, value, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static ResponseCookieBuilder from(final String name, final String value, final boolean lenient) {
/* 211 */     return new ResponseCookieBuilder()
/*     */       {
/* 213 */         private Duration maxAge = Duration.ofSeconds(-1L);
/*     */         
/*     */         @Nullable
/*     */         private String domain;
/*     */         
/*     */         @Nullable
/*     */         private String path;
/*     */         
/*     */         private boolean secure;
/*     */         
/*     */         private boolean httpOnly;
/*     */         
/*     */         @Nullable
/*     */         private String sameSite;
/*     */ 
/*     */         
/*     */         public ResponseCookie.ResponseCookieBuilder maxAge(Duration maxAge) {
/* 230 */           this.maxAge = maxAge;
/* 231 */           return this;
/*     */         }
/*     */ 
/*     */         
/*     */         public ResponseCookie.ResponseCookieBuilder maxAge(long maxAgeSeconds) {
/* 236 */           this.maxAge = (maxAgeSeconds >= 0L) ? Duration.ofSeconds(maxAgeSeconds) : Duration.ofSeconds(-1L);
/* 237 */           return this;
/*     */         }
/*     */ 
/*     */         
/*     */         public ResponseCookie.ResponseCookieBuilder domain(String domain) {
/* 242 */           this.domain = initDomain(domain);
/* 243 */           return this;
/*     */         }
/*     */         
/*     */         @Nullable
/*     */         private String initDomain(String domain) {
/* 248 */           if (lenient && StringUtils.hasLength(domain)) {
/* 249 */             String str = domain.trim();
/* 250 */             if (str.startsWith("\"") && str.endsWith("\"") && 
/* 251 */               str.substring(1, str.length() - 1).trim().isEmpty()) {
/* 252 */               return null;
/*     */             }
/*     */           } 
/*     */           
/* 256 */           return domain;
/*     */         }
/*     */ 
/*     */         
/*     */         public ResponseCookie.ResponseCookieBuilder path(String path) {
/* 261 */           this.path = path;
/* 262 */           return this;
/*     */         }
/*     */ 
/*     */         
/*     */         public ResponseCookie.ResponseCookieBuilder secure(boolean secure) {
/* 267 */           this.secure = secure;
/* 268 */           return this;
/*     */         }
/*     */ 
/*     */         
/*     */         public ResponseCookie.ResponseCookieBuilder httpOnly(boolean httpOnly) {
/* 273 */           this.httpOnly = httpOnly;
/* 274 */           return this;
/*     */         }
/*     */ 
/*     */         
/*     */         public ResponseCookie.ResponseCookieBuilder sameSite(@Nullable String sameSite) {
/* 279 */           this.sameSite = sameSite;
/* 280 */           return this;
/*     */         }
/*     */ 
/*     */         
/*     */         public ResponseCookie build() {
/* 285 */           return new ResponseCookie(name, value, this.maxAge, this.domain, this.path, this.secure, this.httpOnly, this.sameSite);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface ResponseCookieBuilder
/*     */   {
/*     */     ResponseCookieBuilder maxAge(Duration param1Duration);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ResponseCookieBuilder maxAge(long param1Long);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ResponseCookieBuilder path(String param1String);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ResponseCookieBuilder domain(String param1String);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ResponseCookieBuilder secure(boolean param1Boolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ResponseCookieBuilder httpOnly(boolean param1Boolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ResponseCookieBuilder sameSite(@Nullable String param1String);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ResponseCookie build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Rfc6265Utils
/*     */   {
/* 352 */     private static final String SEPARATOR_CHARS = new String(new char[] { '(', ')', '<', '>', '@', ',', ';', ':', '\\', '"', '/', '[', ']', '?', '=', '{', '}', ' ' });
/*     */ 
/*     */ 
/*     */     
/*     */     private static final String DOMAIN_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-";
/*     */ 
/*     */ 
/*     */     
/*     */     public static void validateCookieName(String name) {
/* 361 */       for (int i = 0; i < name.length(); i++) {
/* 362 */         char c = name.charAt(i);
/*     */         
/* 364 */         if (c <= '\037' || c == '') {
/* 365 */           throw new IllegalArgumentException(name + ": RFC2616 token cannot have control chars");
/*     */         }
/*     */         
/* 368 */         if (SEPARATOR_CHARS.indexOf(c) >= 0) {
/* 369 */           throw new IllegalArgumentException(name + ": RFC2616 token cannot have separator chars such as '" + c + "'");
/*     */         }
/*     */         
/* 372 */         if (c >= '') {
/* 373 */           throw new IllegalArgumentException(name + ": RFC2616 token can only have US-ASCII: 0x" + 
/* 374 */               Integer.toHexString(c));
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     public static void validateCookieValue(@Nullable String value) {
/* 380 */       if (value == null) {
/*     */         return;
/*     */       }
/* 383 */       int start = 0;
/* 384 */       int end = value.length();
/* 385 */       if (end > 1 && value.charAt(0) == '"' && value.charAt(end - 1) == '"') {
/* 386 */         start = 1;
/* 387 */         end--;
/*     */       } 
/* 389 */       for (int i = start; i < end; i++) {
/* 390 */         char c = value.charAt(i);
/* 391 */         if (c < '!' || c == '"' || c == ',' || c == ';' || c == '\\' || c == '') {
/* 392 */           throw new IllegalArgumentException("RFC2616 cookie value cannot have '" + c + "'");
/*     */         }
/*     */         
/* 395 */         if (c >= '') {
/* 396 */           throw new IllegalArgumentException("RFC2616 cookie value can only have US-ASCII chars: 0x" + 
/* 397 */               Integer.toHexString(c));
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     public static void validateDomain(@Nullable String domain) {
/* 403 */       if (!StringUtils.hasLength(domain)) {
/*     */         return;
/*     */       }
/* 406 */       int char1 = domain.charAt(0);
/* 407 */       int charN = domain.charAt(domain.length() - 1);
/* 408 */       if (char1 == 45 || charN == 46 || charN == 45) {
/* 409 */         throw new IllegalArgumentException("Invalid first/last char in cookie domain: " + domain);
/*     */       }
/* 411 */       for (int i = 0, c = -1; i < domain.length(); i++) {
/* 412 */         int p = c;
/* 413 */         c = domain.charAt(i);
/* 414 */         if ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-".indexOf(c) == -1 || (p == 46 && (c == 46 || c == 45)) || (p == 45 && c == 46)) {
/* 415 */           throw new IllegalArgumentException(domain + ": invalid cookie domain char '" + c + "'");
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     public static void validatePath(@Nullable String path) {
/* 421 */       if (path == null) {
/*     */         return;
/*     */       }
/* 424 */       for (int i = 0; i < path.length(); i++) {
/* 425 */         char c = path.charAt(i);
/* 426 */         if (c < ' ' || c > '~' || c == ';')
/* 427 */           throw new IllegalArgumentException(path + ": Invalid cookie path char '" + c + "'"); 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/ResponseCookie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */