/*      */ package org.springframework.web.util;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.StringJoiner;
/*      */ import java.util.function.BiFunction;
/*      */ import java.util.function.UnaryOperator;
/*      */ import org.springframework.lang.NonNull;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
/*      */ import org.springframework.util.CollectionUtils;
/*      */ import org.springframework.util.LinkedMultiValueMap;
/*      */ import org.springframework.util.MultiValueMap;
/*      */ import org.springframework.util.ObjectUtils;
/*      */ import org.springframework.util.StreamUtils;
/*      */ import org.springframework.util.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class HierarchicalUriComponents
/*      */   extends UriComponents
/*      */ {
/*      */   private static final char PATH_DELIMITER = '/';
/*   57 */   private static final String PATH_DELIMITER_STRING = String.valueOf('/');
/*      */ 
/*      */   
/*   60 */   private static final MultiValueMap<String, String> EMPTY_QUERY_PARAMS = CollectionUtils.unmodifiableMultiValueMap((MultiValueMap)new LinkedMultiValueMap());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   66 */   static final PathComponent NULL_PATH_COMPONENT = new PathComponent()
/*      */     {
/*      */       public String getPath() {
/*   69 */         return "";
/*      */       }
/*      */       
/*      */       public List<String> getPathSegments() {
/*   73 */         return Collections.emptyList();
/*      */       }
/*      */       
/*      */       public HierarchicalUriComponents.PathComponent encode(BiFunction<String, HierarchicalUriComponents.Type, String> encoder) {
/*   77 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public void verify() {}
/*      */       
/*      */       public HierarchicalUriComponents.PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
/*   84 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {}
/*      */       
/*      */       public boolean equals(@Nullable Object other) {
/*   91 */         return (this == other);
/*      */       }
/*      */       
/*      */       public int hashCode() {
/*   95 */         return getClass().hashCode();
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private final String userInfo;
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private final String host;
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private final String port;
/*      */ 
/*      */ 
/*      */   
/*      */   private final PathComponent path;
/*      */ 
/*      */ 
/*      */   
/*      */   private final MultiValueMap<String, String> queryParams;
/*      */ 
/*      */ 
/*      */   
/*      */   private final EncodeState encodeState;
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private UnaryOperator<String> variableEncoder;
/*      */ 
/*      */ 
/*      */   
/*      */   HierarchicalUriComponents(@Nullable String scheme, @Nullable String fragment, @Nullable String userInfo, @Nullable String host, @Nullable String port, @Nullable PathComponent path, @Nullable MultiValueMap<String, String> query, boolean encoded) {
/*  134 */     super(scheme, fragment);
/*      */     
/*  136 */     this.userInfo = userInfo;
/*  137 */     this.host = host;
/*  138 */     this.port = port;
/*  139 */     this.path = (path != null) ? path : NULL_PATH_COMPONENT;
/*  140 */     this.queryParams = (query != null) ? CollectionUtils.unmodifiableMultiValueMap(query) : EMPTY_QUERY_PARAMS;
/*  141 */     this.encodeState = encoded ? EncodeState.FULLY_ENCODED : EncodeState.RAW;
/*      */ 
/*      */     
/*  144 */     if (encoded) {
/*  145 */       verify();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private HierarchicalUriComponents(@Nullable String scheme, @Nullable String fragment, @Nullable String userInfo, @Nullable String host, @Nullable String port, PathComponent path, MultiValueMap<String, String> queryParams, EncodeState encodeState, @Nullable UnaryOperator<String> variableEncoder) {
/*  154 */     super(scheme, fragment);
/*      */     
/*  156 */     this.userInfo = userInfo;
/*  157 */     this.host = host;
/*  158 */     this.port = port;
/*  159 */     this.path = path;
/*  160 */     this.queryParams = queryParams;
/*  161 */     this.encodeState = encodeState;
/*  162 */     this.variableEncoder = variableEncoder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getSchemeSpecificPart() {
/*  171 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getUserInfo() {
/*  177 */     return this.userInfo;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getHost() {
/*  183 */     return this.host;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getPort() {
/*  188 */     if (this.port == null) {
/*  189 */       return -1;
/*      */     }
/*  191 */     if (this.port.contains("{")) {
/*  192 */       throw new IllegalStateException("The port contains a URI variable but has not been expanded yet: " + this.port);
/*      */     }
/*      */     
/*  195 */     return Integer.parseInt(this.port);
/*      */   }
/*      */ 
/*      */   
/*      */   @NonNull
/*      */   public String getPath() {
/*  201 */     return this.path.getPath();
/*      */   }
/*      */ 
/*      */   
/*      */   public List<String> getPathSegments() {
/*  206 */     return this.path.getPathSegments();
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getQuery() {
/*  212 */     if (!this.queryParams.isEmpty()) {
/*  213 */       StringBuilder queryBuilder = new StringBuilder();
/*  214 */       this.queryParams.forEach((name, values) -> {
/*      */             if (CollectionUtils.isEmpty(values)) {
/*      */               if (queryBuilder.length() != 0) {
/*      */                 queryBuilder.append('&');
/*      */               }
/*      */               
/*      */               queryBuilder.append(name);
/*      */             } else {
/*      */               for (Object value : values) {
/*      */                 if (queryBuilder.length() != 0) {
/*      */                   queryBuilder.append('&');
/*      */                 }
/*      */                 queryBuilder.append(name);
/*      */                 if (value != null) {
/*      */                   queryBuilder.append('=').append(value.toString());
/*      */                 }
/*      */               } 
/*      */             } 
/*      */           });
/*  233 */       return queryBuilder.toString();
/*      */     } 
/*      */     
/*  236 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MultiValueMap<String, String> getQueryParams() {
/*  245 */     return this.queryParams;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   HierarchicalUriComponents encodeTemplate(Charset charset) {
/*  257 */     if (this.encodeState.isEncoded()) {
/*  258 */       return this;
/*      */     }
/*      */ 
/*      */     
/*  262 */     this.variableEncoder = (value -> encodeUriComponent(value, charset, Type.URI));
/*      */     
/*  264 */     UriTemplateEncoder encoder = new UriTemplateEncoder(charset);
/*  265 */     String schemeTo = (getScheme() != null) ? encoder.apply(getScheme(), Type.SCHEME) : null;
/*  266 */     String fragmentTo = (getFragment() != null) ? encoder.apply(getFragment(), Type.FRAGMENT) : null;
/*  267 */     String userInfoTo = (getUserInfo() != null) ? encoder.apply(getUserInfo(), Type.USER_INFO) : null;
/*  268 */     String hostTo = (getHost() != null) ? encoder.apply(getHost(), getHostType()) : null;
/*  269 */     PathComponent pathTo = this.path.encode(encoder);
/*  270 */     MultiValueMap<String, String> queryParamsTo = encodeQueryParams(encoder);
/*      */     
/*  272 */     return new HierarchicalUriComponents(schemeTo, fragmentTo, userInfoTo, hostTo, this.port, pathTo, queryParamsTo, EncodeState.TEMPLATE_ENCODED, this.variableEncoder);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public HierarchicalUriComponents encode(Charset charset) {
/*  278 */     if (this.encodeState.isEncoded()) {
/*  279 */       return this;
/*      */     }
/*  281 */     String scheme = getScheme();
/*  282 */     String fragment = getFragment();
/*  283 */     String schemeTo = (scheme != null) ? encodeUriComponent(scheme, charset, Type.SCHEME) : null;
/*  284 */     String fragmentTo = (fragment != null) ? encodeUriComponent(fragment, charset, Type.FRAGMENT) : null;
/*  285 */     String userInfoTo = (this.userInfo != null) ? encodeUriComponent(this.userInfo, charset, Type.USER_INFO) : null;
/*  286 */     String hostTo = (this.host != null) ? encodeUriComponent(this.host, charset, getHostType()) : null;
/*  287 */     BiFunction<String, Type, String> encoder = (s, type) -> encodeUriComponent(s, charset, type);
/*  288 */     PathComponent pathTo = this.path.encode(encoder);
/*  289 */     MultiValueMap<String, String> queryParamsTo = encodeQueryParams(encoder);
/*      */     
/*  291 */     return new HierarchicalUriComponents(schemeTo, fragmentTo, userInfoTo, hostTo, this.port, pathTo, queryParamsTo, EncodeState.FULLY_ENCODED, null);
/*      */   }
/*      */ 
/*      */   
/*      */   private MultiValueMap<String, String> encodeQueryParams(BiFunction<String, Type, String> encoder) {
/*  296 */     int size = this.queryParams.size();
/*  297 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(size);
/*  298 */     this.queryParams.forEach((key, values) -> {
/*      */           String name = encoder.apply(key, Type.QUERY_PARAM);
/*      */           List<String> encodedValues = new ArrayList<>(values.size());
/*      */           for (String value : values) {
/*      */             encodedValues.add((value != null) ? encoder.apply(value, Type.QUERY_PARAM) : null);
/*      */           }
/*      */           result.put(name, encodedValues);
/*      */         });
/*  306 */     return CollectionUtils.unmodifiableMultiValueMap((MultiValueMap)linkedMultiValueMap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String encodeUriComponent(String source, String encoding, Type type) {
/*  319 */     return encodeUriComponent(source, Charset.forName(encoding), type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String encodeUriComponent(String source, Charset charset, Type type) {
/*  332 */     if (!StringUtils.hasLength(source)) {
/*  333 */       return source;
/*      */     }
/*  335 */     Assert.notNull(charset, "Charset must not be null");
/*  336 */     Assert.notNull(type, "Type must not be null");
/*      */     
/*  338 */     byte[] bytes = source.getBytes(charset);
/*  339 */     boolean original = true;
/*  340 */     for (byte b : bytes) {
/*  341 */       if (!type.isAllowed(b)) {
/*  342 */         original = false;
/*      */         break;
/*      */       } 
/*      */     } 
/*  346 */     if (original) {
/*  347 */       return source;
/*      */     }
/*      */     
/*  350 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
/*  351 */     for (byte b : bytes) {
/*  352 */       if (type.isAllowed(b)) {
/*  353 */         baos.write(b);
/*      */       } else {
/*      */         
/*  356 */         baos.write(37);
/*  357 */         char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
/*  358 */         char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
/*  359 */         baos.write(hex1);
/*  360 */         baos.write(hex2);
/*      */       } 
/*      */     } 
/*  363 */     return StreamUtils.copyToString(baos, charset);
/*      */   }
/*      */   
/*      */   private Type getHostType() {
/*  367 */     return (this.host != null && this.host.startsWith("[")) ? Type.HOST_IPV6 : Type.HOST_IPV4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void verify() {
/*  377 */     verifyUriComponent(getScheme(), Type.SCHEME);
/*  378 */     verifyUriComponent(this.userInfo, Type.USER_INFO);
/*  379 */     verifyUriComponent(this.host, getHostType());
/*  380 */     this.path.verify();
/*  381 */     this.queryParams.forEach((key, values) -> {
/*      */           verifyUriComponent(key, Type.QUERY_PARAM);
/*      */           for (String value : values) {
/*      */             verifyUriComponent(value, Type.QUERY_PARAM);
/*      */           }
/*      */         });
/*  387 */     verifyUriComponent(getFragment(), Type.FRAGMENT);
/*      */   }
/*      */   
/*      */   private static void verifyUriComponent(@Nullable String source, Type type) {
/*  391 */     if (source == null) {
/*      */       return;
/*      */     }
/*  394 */     int length = source.length();
/*  395 */     for (int i = 0; i < length; i++) {
/*  396 */       char ch = source.charAt(i);
/*  397 */       if (ch == '%') {
/*  398 */         if (i + 2 < length) {
/*  399 */           char hex1 = source.charAt(i + 1);
/*  400 */           char hex2 = source.charAt(i + 2);
/*  401 */           int u = Character.digit(hex1, 16);
/*  402 */           int l = Character.digit(hex2, 16);
/*  403 */           if (u == -1 || l == -1) {
/*  404 */             throw new IllegalArgumentException("Invalid encoded sequence \"" + source
/*  405 */                 .substring(i) + "\"");
/*      */           }
/*  407 */           i += 2;
/*      */         } else {
/*      */           
/*  410 */           throw new IllegalArgumentException("Invalid encoded sequence \"" + source
/*  411 */               .substring(i) + "\"");
/*      */         }
/*      */       
/*  414 */       } else if (!type.isAllowed(ch)) {
/*  415 */         throw new IllegalArgumentException("Invalid character '" + ch + "' for " + type
/*  416 */             .name() + " in \"" + source + "\"");
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected HierarchicalUriComponents expandInternal(UriComponents.UriTemplateVariables uriVariables) {
/*  426 */     Assert.state(!this.encodeState.equals(EncodeState.FULLY_ENCODED), "URI components already encoded, and could not possibly contain '{' or '}'.");
/*      */ 
/*      */ 
/*      */     
/*  430 */     String schemeTo = expandUriComponent(getScheme(), uriVariables, this.variableEncoder);
/*  431 */     String userInfoTo = expandUriComponent(this.userInfo, uriVariables, this.variableEncoder);
/*  432 */     String hostTo = expandUriComponent(this.host, uriVariables, this.variableEncoder);
/*  433 */     String portTo = expandUriComponent(this.port, uriVariables, this.variableEncoder);
/*  434 */     PathComponent pathTo = this.path.expand(uriVariables, this.variableEncoder);
/*  435 */     MultiValueMap<String, String> queryParamsTo = expandQueryParams(uriVariables);
/*  436 */     String fragmentTo = expandUriComponent(getFragment(), uriVariables, this.variableEncoder);
/*      */     
/*  438 */     return new HierarchicalUriComponents(schemeTo, fragmentTo, userInfoTo, hostTo, portTo, pathTo, queryParamsTo, this.encodeState, this.variableEncoder);
/*      */   }
/*      */ 
/*      */   
/*      */   private MultiValueMap<String, String> expandQueryParams(UriComponents.UriTemplateVariables variables) {
/*  443 */     int size = this.queryParams.size();
/*  444 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(size);
/*  445 */     UriComponents.UriTemplateVariables queryVariables = new QueryUriTemplateVariables(variables);
/*  446 */     this.queryParams.forEach((key, values) -> {
/*      */           String name = expandUriComponent(key, queryVariables, this.variableEncoder);
/*      */           List<String> expandedValues = new ArrayList<>(values.size());
/*      */           for (String value : values) {
/*      */             expandedValues.add(expandUriComponent(value, queryVariables, this.variableEncoder));
/*      */           }
/*      */           result.put(name, expandedValues);
/*      */         });
/*  454 */     return CollectionUtils.unmodifiableMultiValueMap((MultiValueMap)linkedMultiValueMap);
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponents normalize() {
/*  459 */     String normalizedPath = StringUtils.cleanPath(getPath());
/*  460 */     FullPathComponent path = new FullPathComponent(normalizedPath);
/*  461 */     return new HierarchicalUriComponents(getScheme(), getFragment(), this.userInfo, this.host, this.port, path, this.queryParams, this.encodeState, this.variableEncoder);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toUriString() {
/*  470 */     StringBuilder uriBuilder = new StringBuilder();
/*  471 */     if (getScheme() != null) {
/*  472 */       uriBuilder.append(getScheme()).append(':');
/*      */     }
/*  474 */     if (this.userInfo != null || this.host != null) {
/*  475 */       uriBuilder.append("//");
/*  476 */       if (this.userInfo != null) {
/*  477 */         uriBuilder.append(this.userInfo).append('@');
/*      */       }
/*  479 */       if (this.host != null) {
/*  480 */         uriBuilder.append(this.host);
/*      */       }
/*  482 */       if (getPort() != -1) {
/*  483 */         uriBuilder.append(':').append(this.port);
/*      */       }
/*      */     } 
/*  486 */     String path = getPath();
/*  487 */     if (StringUtils.hasLength(path)) {
/*  488 */       if (uriBuilder.length() != 0 && path.charAt(0) != '/') {
/*  489 */         uriBuilder.append('/');
/*      */       }
/*  491 */       uriBuilder.append(path);
/*      */     } 
/*  493 */     String query = getQuery();
/*  494 */     if (query != null) {
/*  495 */       uriBuilder.append('?').append(query);
/*      */     }
/*  497 */     if (getFragment() != null) {
/*  498 */       uriBuilder.append('#').append(getFragment());
/*      */     }
/*  500 */     return uriBuilder.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public URI toUri() {
/*      */     try {
/*  506 */       if (this.encodeState.isEncoded()) {
/*  507 */         return new URI(toUriString());
/*      */       }
/*      */       
/*  510 */       String path = getPath();
/*  511 */       if (StringUtils.hasLength(path) && path.charAt(0) != '/')
/*      */       {
/*  513 */         if (getScheme() != null || getUserInfo() != null || getHost() != null || getPort() != -1) {
/*  514 */           path = '/' + path;
/*      */         }
/*      */       }
/*  517 */       return new URI(getScheme(), getUserInfo(), getHost(), getPort(), path, getQuery(), getFragment());
/*      */     
/*      */     }
/*  520 */     catch (URISyntaxException ex) {
/*  521 */       throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
/*  527 */     if (getScheme() != null) {
/*  528 */       builder.scheme(getScheme());
/*      */     }
/*  530 */     if (getUserInfo() != null) {
/*  531 */       builder.userInfo(getUserInfo());
/*      */     }
/*  533 */     if (getHost() != null) {
/*  534 */       builder.host(getHost());
/*      */     }
/*      */     
/*  537 */     if (this.port != null) {
/*  538 */       builder.port(this.port);
/*      */     }
/*  540 */     this.path.copyToUriComponentsBuilder(builder);
/*  541 */     if (!getQueryParams().isEmpty()) {
/*  542 */       builder.queryParams(getQueryParams());
/*      */     }
/*  544 */     if (getFragment() != null) {
/*  545 */       builder.fragment(getFragment());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(@Nullable Object other) {
/*  552 */     if (this == other) {
/*  553 */       return true;
/*      */     }
/*  555 */     if (!(other instanceof HierarchicalUriComponents)) {
/*  556 */       return false;
/*      */     }
/*  558 */     HierarchicalUriComponents otherComp = (HierarchicalUriComponents)other;
/*  559 */     return (ObjectUtils.nullSafeEquals(getScheme(), otherComp.getScheme()) && 
/*  560 */       ObjectUtils.nullSafeEquals(getUserInfo(), otherComp.getUserInfo()) && 
/*  561 */       ObjectUtils.nullSafeEquals(getHost(), otherComp.getHost()) && 
/*  562 */       getPort() == otherComp.getPort() && this.path
/*  563 */       .equals(otherComp.path) && this.queryParams
/*  564 */       .equals(otherComp.queryParams) && 
/*  565 */       ObjectUtils.nullSafeEquals(getFragment(), otherComp.getFragment()));
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  570 */     int result = ObjectUtils.nullSafeHashCode(getScheme());
/*  571 */     result = 31 * result + ObjectUtils.nullSafeHashCode(this.userInfo);
/*  572 */     result = 31 * result + ObjectUtils.nullSafeHashCode(this.host);
/*  573 */     result = 31 * result + ObjectUtils.nullSafeHashCode(this.port);
/*  574 */     result = 31 * result + this.path.hashCode();
/*  575 */     result = 31 * result + this.queryParams.hashCode();
/*  576 */     result = 31 * result + ObjectUtils.nullSafeHashCode(getFragment());
/*  577 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   enum Type
/*      */   {
/*  590 */     SCHEME
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  593 */         return (isAlpha(c) || isDigit(c) || 43 == c || 45 == c || 46 == c);
/*      */       }
/*      */     },
/*  596 */     AUTHORITY
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  599 */         return (isUnreserved(c) || isSubDelimiter(c) || 58 == c || 64 == c);
/*      */       }
/*      */     },
/*  602 */     USER_INFO
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  605 */         return (isUnreserved(c) || isSubDelimiter(c) || 58 == c);
/*      */       }
/*      */     },
/*  608 */     HOST_IPV4
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  611 */         return (isUnreserved(c) || isSubDelimiter(c));
/*      */       }
/*      */     },
/*  614 */     HOST_IPV6
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  617 */         return (isUnreserved(c) || isSubDelimiter(c) || 91 == c || 93 == c || 58 == c);
/*      */       }
/*      */     },
/*  620 */     PORT
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  623 */         return isDigit(c);
/*      */       }
/*      */     },
/*  626 */     PATH
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  629 */         return (isPchar(c) || 47 == c);
/*      */       }
/*      */     },
/*  632 */     PATH_SEGMENT
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  635 */         return isPchar(c);
/*      */       }
/*      */     },
/*  638 */     QUERY
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  641 */         return (isPchar(c) || 47 == c || 63 == c);
/*      */       }
/*      */     },
/*  644 */     QUERY_PARAM
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  647 */         if (61 == c || 38 == c) {
/*  648 */           return false;
/*      */         }
/*      */         
/*  651 */         return (isPchar(c) || 47 == c || 63 == c);
/*      */       }
/*      */     },
/*      */     
/*  655 */     FRAGMENT
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  658 */         return (isPchar(c) || 47 == c || 63 == c);
/*      */       }
/*      */     },
/*  661 */     URI
/*      */     {
/*      */       public boolean isAllowed(int c) {
/*  664 */         return isUnreserved(c);
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean isAlpha(int c) {
/*  679 */       return ((c >= 97 && c <= 122) || (c >= 65 && c <= 90));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean isDigit(int c) {
/*  687 */       return (c >= 48 && c <= 57);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean isGenericDelimiter(int c) {
/*  695 */       return (58 == c || 47 == c || 63 == c || 35 == c || 91 == c || 93 == c || 64 == c);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean isSubDelimiter(int c) {
/*  703 */       return (33 == c || 36 == c || 38 == c || 39 == c || 40 == c || 41 == c || 42 == c || 43 == c || 44 == c || 59 == c || 61 == c);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean isReserved(int c) {
/*  712 */       return (isGenericDelimiter(c) || isSubDelimiter(c));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean isUnreserved(int c) {
/*  720 */       return (isAlpha(c) || isDigit(c) || 45 == c || 46 == c || 95 == c || 126 == c);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean isPchar(int c) {
/*  728 */       return (isUnreserved(c) || isSubDelimiter(c) || 58 == c || 64 == c);
/*      */     }
/*      */ 
/*      */     
/*      */     public abstract boolean isAllowed(int param1Int);
/*      */   }
/*      */ 
/*      */   
/*      */   private enum EncodeState
/*      */   {
/*  738 */     RAW,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  744 */     FULLY_ENCODED,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  751 */     TEMPLATE_ENCODED;
/*      */ 
/*      */     
/*      */     public boolean isEncoded() {
/*  755 */       return (equals(FULLY_ENCODED) || equals(TEMPLATE_ENCODED));
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static class UriTemplateEncoder
/*      */     implements BiFunction<String, Type, String>
/*      */   {
/*      */     private final Charset charset;
/*  764 */     private final StringBuilder currentLiteral = new StringBuilder();
/*      */     
/*  766 */     private final StringBuilder currentVariable = new StringBuilder();
/*      */     
/*  768 */     private final StringBuilder output = new StringBuilder();
/*      */     
/*      */     private boolean variableWithNameAndRegex;
/*      */     
/*      */     public UriTemplateEncoder(Charset charset) {
/*  773 */       this.charset = charset;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String apply(String source, HierarchicalUriComponents.Type type) {
/*  779 */       if (isUriVariable(source)) {
/*  780 */         return source;
/*      */       }
/*      */       
/*  783 */       if (source.indexOf('{') == -1) {
/*  784 */         return HierarchicalUriComponents.encodeUriComponent(source, this.charset, type);
/*      */       }
/*  786 */       int level = 0;
/*  787 */       clear(this.currentLiteral);
/*  788 */       clear(this.currentVariable);
/*  789 */       clear(this.output);
/*  790 */       for (int i = 0; i < source.length(); i++) {
/*  791 */         char c = source.charAt(i);
/*  792 */         if (c == ':' && level == 1) {
/*  793 */           this.variableWithNameAndRegex = true;
/*      */         }
/*      */         
/*  796 */         level++;
/*  797 */         if (c == '{' && level == 1) {
/*  798 */           append(this.currentLiteral, true, type);
/*      */         }
/*      */         
/*  801 */         if (c == '}' && level > 0) {
/*  802 */           level--;
/*  803 */           this.currentVariable.append('}');
/*  804 */           if (level == 0) {
/*  805 */             boolean encode = !isUriVariable(this.currentVariable);
/*  806 */             append(this.currentVariable, encode, type);
/*      */           }
/*  808 */           else if (!this.variableWithNameAndRegex) {
/*  809 */             append(this.currentVariable, true, type);
/*  810 */             level = 0;
/*      */           }
/*      */         
/*  813 */         } else if (level > 0) {
/*  814 */           this.currentVariable.append(c);
/*      */         } else {
/*      */           
/*  817 */           this.currentLiteral.append(c);
/*      */         } 
/*      */       } 
/*  820 */       if (level > 0) {
/*  821 */         this.currentLiteral.append(this.currentVariable);
/*      */       }
/*  823 */       append(this.currentLiteral, true, type);
/*  824 */       return this.output.toString();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean isUriVariable(CharSequence source) {
/*  834 */       if (source.length() < 2 || source.charAt(0) != '{' || source.charAt(source.length() - 1) != '}') {
/*  835 */         return false;
/*      */       }
/*  837 */       boolean hasText = false;
/*  838 */       for (int i = 1; i < source.length() - 1; i++) {
/*  839 */         char c = source.charAt(i);
/*  840 */         if (c == ':' && i > 1) {
/*  841 */           return true;
/*      */         }
/*  843 */         if (c == '{' || c == '}') {
/*  844 */           return false;
/*      */         }
/*  846 */         hasText = (hasText || !Character.isWhitespace(c));
/*      */       } 
/*  848 */       return hasText;
/*      */     }
/*      */     
/*      */     private void append(StringBuilder sb, boolean encode, HierarchicalUriComponents.Type type) {
/*  852 */       this.output.append(encode ? HierarchicalUriComponents.encodeUriComponent(sb.toString(), this.charset, type) : sb);
/*  853 */       clear(sb);
/*  854 */       this.variableWithNameAndRegex = false;
/*      */     }
/*      */     
/*      */     private void clear(StringBuilder sb) {
/*  858 */       sb.delete(0, sb.length());
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static interface PathComponent
/*      */     extends Serializable
/*      */   {
/*      */     String getPath();
/*      */ 
/*      */     
/*      */     List<String> getPathSegments();
/*      */ 
/*      */     
/*      */     PathComponent encode(BiFunction<String, HierarchicalUriComponents.Type, String> param1BiFunction);
/*      */ 
/*      */     
/*      */     void verify();
/*      */ 
/*      */     
/*      */     PathComponent expand(UriComponents.UriTemplateVariables param1UriTemplateVariables, @Nullable UnaryOperator<String> param1UnaryOperator);
/*      */ 
/*      */     
/*      */     void copyToUriComponentsBuilder(UriComponentsBuilder param1UriComponentsBuilder);
/*      */   }
/*      */   
/*      */   static final class FullPathComponent
/*      */     implements PathComponent
/*      */   {
/*      */     private final String path;
/*      */     
/*      */     public FullPathComponent(@Nullable String path) {
/*  890 */       this.path = (path != null) ? path : "";
/*      */     }
/*      */ 
/*      */     
/*      */     public String getPath() {
/*  895 */       return this.path;
/*      */     }
/*      */ 
/*      */     
/*      */     public List<String> getPathSegments() {
/*  900 */       String[] segments = StringUtils.tokenizeToStringArray(getPath(), HierarchicalUriComponents.PATH_DELIMITER_STRING);
/*  901 */       return Collections.unmodifiableList(Arrays.asList(segments));
/*      */     }
/*      */ 
/*      */     
/*      */     public HierarchicalUriComponents.PathComponent encode(BiFunction<String, HierarchicalUriComponents.Type, String> encoder) {
/*  906 */       String encodedPath = encoder.apply(getPath(), HierarchicalUriComponents.Type.PATH);
/*  907 */       return new FullPathComponent(encodedPath);
/*      */     }
/*      */ 
/*      */     
/*      */     public void verify() {
/*  912 */       HierarchicalUriComponents.verifyUriComponent(getPath(), HierarchicalUriComponents.Type.PATH);
/*      */     }
/*      */ 
/*      */     
/*      */     public HierarchicalUriComponents.PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
/*  917 */       String expandedPath = UriComponents.expandUriComponent(getPath(), uriVariables, encoder);
/*  918 */       return new FullPathComponent(expandedPath);
/*      */     }
/*      */ 
/*      */     
/*      */     public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
/*  923 */       builder.path(getPath());
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(@Nullable Object other) {
/*  928 */       return (this == other || (other instanceof FullPathComponent && 
/*  929 */         getPath().equals(((FullPathComponent)other).getPath())));
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  934 */       return getPath().hashCode();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static final class PathSegmentComponent
/*      */     implements PathComponent
/*      */   {
/*      */     private final List<String> pathSegments;
/*      */ 
/*      */     
/*      */     public PathSegmentComponent(List<String> pathSegments) {
/*  947 */       Assert.notNull(pathSegments, "List must not be null");
/*  948 */       this.pathSegments = Collections.unmodifiableList(new ArrayList<>(pathSegments));
/*      */     }
/*      */ 
/*      */     
/*      */     public String getPath() {
/*  953 */       String delimiter = HierarchicalUriComponents.PATH_DELIMITER_STRING;
/*  954 */       StringJoiner pathBuilder = new StringJoiner(delimiter, delimiter, "");
/*  955 */       for (String pathSegment : this.pathSegments) {
/*  956 */         pathBuilder.add(pathSegment);
/*      */       }
/*  958 */       return pathBuilder.toString();
/*      */     }
/*      */ 
/*      */     
/*      */     public List<String> getPathSegments() {
/*  963 */       return this.pathSegments;
/*      */     }
/*      */ 
/*      */     
/*      */     public HierarchicalUriComponents.PathComponent encode(BiFunction<String, HierarchicalUriComponents.Type, String> encoder) {
/*  968 */       List<String> pathSegments = getPathSegments();
/*  969 */       List<String> encodedPathSegments = new ArrayList<>(pathSegments.size());
/*  970 */       for (String pathSegment : pathSegments) {
/*  971 */         String encodedPathSegment = encoder.apply(pathSegment, HierarchicalUriComponents.Type.PATH_SEGMENT);
/*  972 */         encodedPathSegments.add(encodedPathSegment);
/*      */       } 
/*  974 */       return new PathSegmentComponent(encodedPathSegments);
/*      */     }
/*      */ 
/*      */     
/*      */     public void verify() {
/*  979 */       for (String pathSegment : getPathSegments()) {
/*  980 */         HierarchicalUriComponents.verifyUriComponent(pathSegment, HierarchicalUriComponents.Type.PATH_SEGMENT);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public HierarchicalUriComponents.PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
/*  986 */       List<String> pathSegments = getPathSegments();
/*  987 */       List<String> expandedPathSegments = new ArrayList<>(pathSegments.size());
/*  988 */       for (String pathSegment : pathSegments) {
/*  989 */         String expandedPathSegment = UriComponents.expandUriComponent(pathSegment, uriVariables, encoder);
/*  990 */         expandedPathSegments.add(expandedPathSegment);
/*      */       } 
/*  992 */       return new PathSegmentComponent(expandedPathSegments);
/*      */     }
/*      */ 
/*      */     
/*      */     public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
/*  997 */       builder.pathSegment(StringUtils.toStringArray(getPathSegments()));
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(@Nullable Object other) {
/* 1002 */       return (this == other || (other instanceof PathSegmentComponent && 
/* 1003 */         getPathSegments().equals(((PathSegmentComponent)other).getPathSegments())));
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1008 */       return getPathSegments().hashCode();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static final class PathComponentComposite
/*      */     implements PathComponent
/*      */   {
/*      */     private final List<HierarchicalUriComponents.PathComponent> pathComponents;
/*      */ 
/*      */     
/*      */     public PathComponentComposite(List<HierarchicalUriComponents.PathComponent> pathComponents) {
/* 1021 */       Assert.notNull(pathComponents, "PathComponent List must not be null");
/* 1022 */       this.pathComponents = pathComponents;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getPath() {
/* 1027 */       StringBuilder pathBuilder = new StringBuilder();
/* 1028 */       for (HierarchicalUriComponents.PathComponent pathComponent : this.pathComponents) {
/* 1029 */         pathBuilder.append(pathComponent.getPath());
/*      */       }
/* 1031 */       return pathBuilder.toString();
/*      */     }
/*      */ 
/*      */     
/*      */     public List<String> getPathSegments() {
/* 1036 */       List<String> result = new ArrayList<>();
/* 1037 */       for (HierarchicalUriComponents.PathComponent pathComponent : this.pathComponents) {
/* 1038 */         result.addAll(pathComponent.getPathSegments());
/*      */       }
/* 1040 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     public HierarchicalUriComponents.PathComponent encode(BiFunction<String, HierarchicalUriComponents.Type, String> encoder) {
/* 1045 */       List<HierarchicalUriComponents.PathComponent> encodedComponents = new ArrayList<>(this.pathComponents.size());
/* 1046 */       for (HierarchicalUriComponents.PathComponent pathComponent : this.pathComponents) {
/* 1047 */         encodedComponents.add(pathComponent.encode(encoder));
/*      */       }
/* 1049 */       return new PathComponentComposite(encodedComponents);
/*      */     }
/*      */ 
/*      */     
/*      */     public void verify() {
/* 1054 */       for (HierarchicalUriComponents.PathComponent pathComponent : this.pathComponents) {
/* 1055 */         pathComponent.verify();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public HierarchicalUriComponents.PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
/* 1061 */       List<HierarchicalUriComponents.PathComponent> expandedComponents = new ArrayList<>(this.pathComponents.size());
/* 1062 */       for (HierarchicalUriComponents.PathComponent pathComponent : this.pathComponents) {
/* 1063 */         expandedComponents.add(pathComponent.expand(uriVariables, encoder));
/*      */       }
/* 1065 */       return new PathComponentComposite(expandedComponents);
/*      */     }
/*      */ 
/*      */     
/*      */     public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
/* 1070 */       for (HierarchicalUriComponents.PathComponent pathComponent : this.pathComponents) {
/* 1071 */         pathComponent.copyToUriComponentsBuilder(builder);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static class QueryUriTemplateVariables
/*      */     implements UriComponents.UriTemplateVariables
/*      */   {
/*      */     private final UriComponents.UriTemplateVariables delegate;
/*      */     
/*      */     public QueryUriTemplateVariables(UriComponents.UriTemplateVariables delegate) {
/* 1082 */       this.delegate = delegate;
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getValue(@Nullable String name) {
/* 1087 */       Object value = this.delegate.getValue(name);
/* 1088 */       if (ObjectUtils.isArray(value)) {
/* 1089 */         value = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(value));
/*      */       }
/* 1091 */       return value;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/HierarchicalUriComponents.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */