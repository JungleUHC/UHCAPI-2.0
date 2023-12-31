/*     */ package org.springframework.web.cors;
/*     */ 
/*     */ import java.time.Duration;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.stream.Collectors;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
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
/*     */ public class CorsConfiguration
/*     */ {
/*     */   public static final String ALL = "*";
/*  60 */   private static final List<String> ALL_LIST = Collections.singletonList("*");
/*     */   
/*  62 */   private static final OriginPattern ALL_PATTERN = new OriginPattern("*");
/*     */   
/*  64 */   private static final List<OriginPattern> ALL_PATTERN_LIST = Collections.singletonList(ALL_PATTERN);
/*     */   
/*  66 */   private static final List<String> DEFAULT_PERMIT_ALL = Collections.singletonList("*");
/*     */   
/*  68 */   private static final List<HttpMethod> DEFAULT_METHODS = Collections.unmodifiableList(
/*  69 */       Arrays.asList(new HttpMethod[] { HttpMethod.GET, HttpMethod.HEAD }));
/*     */   
/*  71 */   private static final List<String> DEFAULT_PERMIT_METHODS = Collections.unmodifiableList(
/*  72 */       Arrays.asList(new String[] { HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name() }));
/*     */   
/*     */   @Nullable
/*     */   private List<String> allowedOrigins;
/*     */   
/*     */   @Nullable
/*     */   private List<OriginPattern> allowedOriginPatterns;
/*     */   
/*     */   @Nullable
/*     */   private List<String> allowedMethods;
/*     */   
/*     */   @Nullable
/*  84 */   private List<HttpMethod> resolvedMethods = DEFAULT_METHODS;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private List<String> allowedHeaders;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private List<String> exposedHeaders;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Boolean allowCredentials;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Long maxAge;
/*     */ 
/*     */ 
/*     */   
/*     */   public CorsConfiguration() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public CorsConfiguration(CorsConfiguration other) {
/* 113 */     this.allowedOrigins = other.allowedOrigins;
/* 114 */     this.allowedOriginPatterns = other.allowedOriginPatterns;
/* 115 */     this.allowedMethods = other.allowedMethods;
/* 116 */     this.resolvedMethods = other.resolvedMethods;
/* 117 */     this.allowedHeaders = other.allowedHeaders;
/* 118 */     this.exposedHeaders = other.exposedHeaders;
/* 119 */     this.allowCredentials = other.allowCredentials;
/* 120 */     this.maxAge = other.maxAge;
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
/*     */   public void setAllowedOrigins(@Nullable List<String> origins) {
/* 140 */     this
/* 141 */       .allowedOrigins = (origins == null) ? null : (List<String>)origins.stream().filter(Objects::nonNull).map(this::trimTrailingSlash).collect(Collectors.toList());
/*     */   }
/*     */   
/*     */   private String trimTrailingSlash(String origin) {
/* 145 */     return origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> getAllowedOrigins() {
/* 153 */     return this.allowedOrigins;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAllowedOrigin(@Nullable String origin) {
/* 160 */     if (origin == null) {
/*     */       return;
/*     */     }
/* 163 */     if (this.allowedOrigins == null) {
/* 164 */       this.allowedOrigins = new ArrayList<>(4);
/*     */     }
/* 166 */     else if (this.allowedOrigins == DEFAULT_PERMIT_ALL && CollectionUtils.isEmpty(this.allowedOriginPatterns)) {
/* 167 */       setAllowedOrigins(DEFAULT_PERMIT_ALL);
/*     */     } 
/* 169 */     origin = trimTrailingSlash(origin);
/* 170 */     this.allowedOrigins.add(origin);
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
/*     */   public CorsConfiguration setAllowedOriginPatterns(@Nullable List<String> allowedOriginPatterns) {
/* 194 */     if (allowedOriginPatterns == null) {
/* 195 */       this.allowedOriginPatterns = null;
/*     */     } else {
/*     */       
/* 198 */       this.allowedOriginPatterns = new ArrayList<>(allowedOriginPatterns.size());
/* 199 */       for (String patternValue : allowedOriginPatterns) {
/* 200 */         addAllowedOriginPattern(patternValue);
/*     */       }
/*     */     } 
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> getAllowedOriginPatterns() {
/* 212 */     if (this.allowedOriginPatterns == null) {
/* 213 */       return null;
/*     */     }
/* 215 */     return (List<String>)this.allowedOriginPatterns.stream()
/* 216 */       .map(OriginPattern::getDeclaredPattern)
/* 217 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAllowedOriginPattern(@Nullable String originPattern) {
/* 225 */     if (originPattern == null) {
/*     */       return;
/*     */     }
/* 228 */     if (this.allowedOriginPatterns == null) {
/* 229 */       this.allowedOriginPatterns = new ArrayList<>(4);
/*     */     }
/* 231 */     originPattern = trimTrailingSlash(originPattern);
/* 232 */     this.allowedOriginPatterns.add(new OriginPattern(originPattern));
/* 233 */     if (this.allowedOrigins == DEFAULT_PERMIT_ALL) {
/* 234 */       this.allowedOrigins = null;
/*     */     }
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
/*     */   public void setAllowedMethods(@Nullable List<String> allowedMethods) {
/* 253 */     this.allowedMethods = (allowedMethods != null) ? new ArrayList<>(allowedMethods) : null;
/* 254 */     if (!CollectionUtils.isEmpty(allowedMethods)) {
/* 255 */       this.resolvedMethods = new ArrayList<>(allowedMethods.size());
/* 256 */       for (String method : allowedMethods) {
/* 257 */         if ("*".equals(method)) {
/* 258 */           this.resolvedMethods = null;
/*     */           break;
/*     */         } 
/* 261 */         this.resolvedMethods.add(HttpMethod.resolve(method));
/*     */       } 
/*     */     } else {
/*     */       
/* 265 */       this.resolvedMethods = DEFAULT_METHODS;
/*     */     } 
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
/*     */   public List<String> getAllowedMethods() {
/* 278 */     return this.allowedMethods;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAllowedMethod(HttpMethod method) {
/* 285 */     addAllowedMethod(method.name());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAllowedMethod(String method) {
/* 292 */     if (StringUtils.hasText(method)) {
/* 293 */       if (this.allowedMethods == null) {
/* 294 */         this.allowedMethods = new ArrayList<>(4);
/* 295 */         this.resolvedMethods = new ArrayList<>(4);
/*     */       }
/* 297 */       else if (this.allowedMethods == DEFAULT_PERMIT_METHODS) {
/* 298 */         setAllowedMethods(DEFAULT_PERMIT_METHODS);
/*     */       } 
/* 300 */       this.allowedMethods.add(method);
/* 301 */       if ("*".equals(method)) {
/* 302 */         this.resolvedMethods = null;
/*     */       }
/* 304 */       else if (this.resolvedMethods != null) {
/* 305 */         this.resolvedMethods.add(HttpMethod.resolve(method));
/*     */       } 
/*     */     } 
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
/*     */   public void setAllowedHeaders(@Nullable List<String> allowedHeaders) {
/* 321 */     this.allowedHeaders = (allowedHeaders != null) ? new ArrayList<>(allowedHeaders) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> getAllowedHeaders() {
/* 331 */     return this.allowedHeaders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAllowedHeader(String allowedHeader) {
/* 338 */     if (this.allowedHeaders == null) {
/* 339 */       this.allowedHeaders = new ArrayList<>(4);
/*     */     }
/* 341 */     else if (this.allowedHeaders == DEFAULT_PERMIT_ALL) {
/* 342 */       setAllowedHeaders(DEFAULT_PERMIT_ALL);
/*     */     } 
/* 344 */     this.allowedHeaders.add(allowedHeader);
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
/*     */   public void setExposedHeaders(@Nullable List<String> exposedHeaders) {
/* 357 */     this.exposedHeaders = (exposedHeaders != null) ? new ArrayList<>(exposedHeaders) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public List<String> getExposedHeaders() {
/* 367 */     return this.exposedHeaders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addExposedHeader(String exposedHeader) {
/* 376 */     if (this.exposedHeaders == null) {
/* 377 */       this.exposedHeaders = new ArrayList<>(4);
/*     */     }
/* 379 */     this.exposedHeaders.add(exposedHeader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowCredentials(@Nullable Boolean allowCredentials) {
/* 387 */     this.allowCredentials = allowCredentials;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Boolean getAllowCredentials() {
/* 396 */     return this.allowCredentials;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxAge(Duration maxAge) {
/* 406 */     this.maxAge = Long.valueOf(maxAge.getSeconds());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxAge(@Nullable Long maxAge) {
/* 415 */     this.maxAge = maxAge;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Long getMaxAge() {
/* 424 */     return this.maxAge;
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
/*     */   public CorsConfiguration applyPermitDefaultValues() {
/* 444 */     if (this.allowedOrigins == null && this.allowedOriginPatterns == null) {
/* 445 */       this.allowedOrigins = DEFAULT_PERMIT_ALL;
/*     */     }
/* 447 */     if (this.allowedMethods == null) {
/* 448 */       this.allowedMethods = DEFAULT_PERMIT_METHODS;
/* 449 */       this
/* 450 */         .resolvedMethods = (List<HttpMethod>)DEFAULT_PERMIT_METHODS.stream().map(HttpMethod::resolve).collect(Collectors.toList());
/*     */     } 
/* 452 */     if (this.allowedHeaders == null) {
/* 453 */       this.allowedHeaders = DEFAULT_PERMIT_ALL;
/*     */     }
/* 455 */     if (this.maxAge == null) {
/* 456 */       this.maxAge = Long.valueOf(1800L);
/*     */     }
/* 458 */     return this;
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
/*     */   public void validateAllowCredentials() {
/* 470 */     if (this.allowCredentials == Boolean.TRUE && this.allowedOrigins != null && this.allowedOrigins
/* 471 */       .contains("*"))
/*     */     {
/* 473 */       throw new IllegalArgumentException("When allowCredentials is true, allowedOrigins cannot contain the special value \"*\" since that cannot be set on the \"Access-Control-Allow-Origin\" response header. To allow credentials to a set of origins, list them explicitly or consider using \"allowedOriginPatterns\" instead.");
/*     */     }
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
/*     */   public CorsConfiguration combine(@Nullable CorsConfiguration other) {
/* 499 */     if (other == null) {
/* 500 */       return this;
/*     */     }
/*     */     
/* 503 */     CorsConfiguration config = new CorsConfiguration(this);
/* 504 */     List<String> origins = combine(getAllowedOrigins(), other.getAllowedOrigins());
/* 505 */     List<OriginPattern> patterns = combinePatterns(this.allowedOriginPatterns, other.allowedOriginPatterns);
/* 506 */     config.allowedOrigins = (origins == DEFAULT_PERMIT_ALL && !CollectionUtils.isEmpty(patterns)) ? null : origins;
/* 507 */     config.allowedOriginPatterns = patterns;
/* 508 */     config.setAllowedMethods(combine(getAllowedMethods(), other.getAllowedMethods()));
/* 509 */     config.setAllowedHeaders(combine(getAllowedHeaders(), other.getAllowedHeaders()));
/* 510 */     config.setExposedHeaders(combine(getExposedHeaders(), other.getExposedHeaders()));
/* 511 */     Boolean allowCredentials = other.getAllowCredentials();
/* 512 */     if (allowCredentials != null) {
/* 513 */       config.setAllowCredentials(allowCredentials);
/*     */     }
/* 515 */     Long maxAge = other.getMaxAge();
/* 516 */     if (maxAge != null) {
/* 517 */       config.setMaxAge(maxAge);
/*     */     }
/* 519 */     return config;
/*     */   }
/*     */   
/*     */   private List<String> combine(@Nullable List<String> source, @Nullable List<String> other) {
/* 523 */     if (other == null) {
/* 524 */       return (source != null) ? source : Collections.<String>emptyList();
/*     */     }
/* 526 */     if (source == null) {
/* 527 */       return other;
/*     */     }
/* 529 */     if (source == DEFAULT_PERMIT_ALL || source == DEFAULT_PERMIT_METHODS) {
/* 530 */       return other;
/*     */     }
/* 532 */     if (other == DEFAULT_PERMIT_ALL || other == DEFAULT_PERMIT_METHODS) {
/* 533 */       return source;
/*     */     }
/* 535 */     if (source.contains("*") || other.contains("*")) {
/* 536 */       return ALL_LIST;
/*     */     }
/* 538 */     Set<String> combined = new LinkedHashSet<>(source.size() + other.size());
/* 539 */     combined.addAll(source);
/* 540 */     combined.addAll(other);
/* 541 */     return new ArrayList<>(combined);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private List<OriginPattern> combinePatterns(@Nullable List<OriginPattern> source, @Nullable List<OriginPattern> other) {
/* 547 */     if (other == null) {
/* 548 */       return (source != null) ? source : Collections.<OriginPattern>emptyList();
/*     */     }
/* 550 */     if (source == null) {
/* 551 */       return other;
/*     */     }
/* 553 */     if (source.contains(ALL_PATTERN) || other.contains(ALL_PATTERN)) {
/* 554 */       return ALL_PATTERN_LIST;
/*     */     }
/* 556 */     Set<OriginPattern> combined = new LinkedHashSet<>(source.size() + other.size());
/* 557 */     combined.addAll(source);
/* 558 */     combined.addAll(other);
/* 559 */     return new ArrayList<>(combined);
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
/*     */   public String checkOrigin(@Nullable String origin) {
/* 571 */     if (!StringUtils.hasText(origin)) {
/* 572 */       return null;
/*     */     }
/* 574 */     String originToCheck = trimTrailingSlash(origin);
/* 575 */     if (!ObjectUtils.isEmpty(this.allowedOrigins)) {
/* 576 */       if (this.allowedOrigins.contains("*")) {
/* 577 */         validateAllowCredentials();
/* 578 */         return "*";
/*     */       } 
/* 580 */       for (String allowedOrigin : this.allowedOrigins) {
/* 581 */         if (originToCheck.equalsIgnoreCase(allowedOrigin)) {
/* 582 */           return origin;
/*     */         }
/*     */       } 
/*     */     } 
/* 586 */     if (!ObjectUtils.isEmpty(this.allowedOriginPatterns)) {
/* 587 */       for (OriginPattern p : this.allowedOriginPatterns) {
/* 588 */         if (p.getDeclaredPattern().equals("*") || p.getPattern().matcher(originToCheck).matches()) {
/* 589 */           return origin;
/*     */         }
/*     */       } 
/*     */     }
/* 593 */     return null;
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
/*     */   @Nullable
/*     */   public List<HttpMethod> checkHttpMethod(@Nullable HttpMethod requestMethod) {
/* 606 */     if (requestMethod == null) {
/* 607 */       return null;
/*     */     }
/* 609 */     if (this.resolvedMethods == null) {
/* 610 */       return Collections.singletonList(requestMethod);
/*     */     }
/* 612 */     return this.resolvedMethods.contains(requestMethod) ? this.resolvedMethods : null;
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
/*     */   @Nullable
/*     */   public List<String> checkHeaders(@Nullable List<String> requestHeaders) {
/* 625 */     if (requestHeaders == null) {
/* 626 */       return null;
/*     */     }
/* 628 */     if (requestHeaders.isEmpty()) {
/* 629 */       return Collections.emptyList();
/*     */     }
/* 631 */     if (ObjectUtils.isEmpty(this.allowedHeaders)) {
/* 632 */       return null;
/*     */     }
/*     */     
/* 635 */     boolean allowAnyHeader = this.allowedHeaders.contains("*");
/* 636 */     List<String> result = new ArrayList<>(requestHeaders.size());
/* 637 */     for (String requestHeader : requestHeaders) {
/* 638 */       if (StringUtils.hasText(requestHeader)) {
/* 639 */         requestHeader = requestHeader.trim();
/* 640 */         if (allowAnyHeader) {
/* 641 */           result.add(requestHeader);
/*     */           continue;
/*     */         } 
/* 644 */         for (String allowedHeader : this.allowedHeaders) {
/* 645 */           if (requestHeader.equalsIgnoreCase(allowedHeader)) {
/* 646 */             result.add(requestHeader);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 653 */     return result.isEmpty() ? null : result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class OriginPattern
/*     */   {
/* 663 */     private static final Pattern PORTS_PATTERN = Pattern.compile("(.*):\\[(\\*|\\d+(,\\d+)*)]");
/*     */     
/*     */     private final String declaredPattern;
/*     */     
/*     */     private final Pattern pattern;
/*     */     
/*     */     OriginPattern(String declaredPattern) {
/* 670 */       this.declaredPattern = declaredPattern;
/* 671 */       this.pattern = initPattern(declaredPattern);
/*     */     }
/*     */     
/*     */     private static Pattern initPattern(String patternValue) {
/* 675 */       String portList = null;
/* 676 */       Matcher matcher = PORTS_PATTERN.matcher(patternValue);
/* 677 */       if (matcher.matches()) {
/* 678 */         patternValue = matcher.group(1);
/* 679 */         portList = matcher.group(2);
/*     */       } 
/*     */       
/* 682 */       patternValue = "\\Q" + patternValue + "\\E";
/* 683 */       patternValue = patternValue.replace("*", "\\E.*\\Q");
/*     */       
/* 685 */       if (portList != null) {
/* 686 */         patternValue = patternValue + (portList.equals("*") ? "(:\\d+)?" : (":(" + portList.replace(',', '|') + ")"));
/*     */       }
/*     */       
/* 689 */       return Pattern.compile(patternValue);
/*     */     }
/*     */     
/*     */     public String getDeclaredPattern() {
/* 693 */       return this.declaredPattern;
/*     */     }
/*     */     
/*     */     public Pattern getPattern() {
/* 697 */       return this.pattern;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/* 702 */       if (this == other) {
/* 703 */         return true;
/*     */       }
/* 705 */       if (other == null || !getClass().equals(other.getClass())) {
/* 706 */         return false;
/*     */       }
/* 708 */       return ObjectUtils.nullSafeEquals(this.declaredPattern, ((OriginPattern)other).declaredPattern);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 714 */       return this.declaredPattern.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 719 */       return this.declaredPattern;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/CorsConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */