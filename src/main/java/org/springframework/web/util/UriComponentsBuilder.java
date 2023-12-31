/*      */ package org.springframework.web.util;
/*      */ 
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URI;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Deque;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.springframework.http.HttpHeaders;
/*      */ import org.springframework.http.HttpRequest;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
/*      */ import org.springframework.util.CollectionUtils;
/*      */ import org.springframework.util.LinkedMultiValueMap;
/*      */ import org.springframework.util.MultiValueMap;
/*      */ import org.springframework.util.ObjectUtils;
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
/*      */ public class UriComponentsBuilder
/*      */   implements UriBuilder, Cloneable
/*      */ {
/*   74 */   private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
/*      */ 
/*      */   
/*      */   private static final String SCHEME_PATTERN = "([^:/?#]+):";
/*      */   
/*      */   private static final String HTTP_PATTERN = "(?i)(http|https):";
/*      */   
/*      */   private static final String USERINFO_PATTERN = "([^@\\[/?#]*)";
/*      */   
/*      */   private static final String HOST_IPV4_PATTERN = "[^\\[/?#:]*";
/*      */   
/*      */   private static final String HOST_IPV6_PATTERN = "\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]";
/*      */   
/*      */   private static final String HOST_PATTERN = "(\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]|[^\\[/?#:]*)";
/*      */   
/*      */   private static final String PORT_PATTERN = "(\\{[^}]+\\}?|[^/?#]*)";
/*      */   
/*      */   private static final String PATH_PATTERN = "([^?#]*)";
/*      */   
/*      */   private static final String QUERY_PATTERN = "([^#]*)";
/*      */   
/*      */   private static final String LAST_PATTERN = "(.*)";
/*      */   
/*   97 */   private static final Pattern URI_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//(([^@\\[/?#]*)@)?(\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]|[^\\[/?#:]*)(:(\\{[^}]+\\}?|[^/?#]*))?)?([^?#]*)(\\?([^#]*))?(#(.*))?");
/*      */ 
/*      */ 
/*      */   
/*  101 */   private static final Pattern HTTP_URL_PATTERN = Pattern.compile("^(?i)(http|https):(//(([^@\\[/?#]*)@)?(\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]|[^\\[/?#:]*)(:(\\{[^}]+\\}?|[^/?#]*))?)?([^?#]*)(\\?([^#]*))?(#(.*))?");
/*      */ 
/*      */   
/*      */   private static final String FORWARDED_VALUE = "\"?([^;,\"]+)\"?";
/*      */ 
/*      */   
/*  107 */   private static final Pattern FORWARDED_HOST_PATTERN = Pattern.compile("(?i:host)=\"?([^;,\"]+)\"?");
/*      */   
/*  109 */   private static final Pattern FORWARDED_PROTO_PATTERN = Pattern.compile("(?i:proto)=\"?([^;,\"]+)\"?");
/*      */   
/*  111 */   private static final Pattern FORWARDED_FOR_PATTERN = Pattern.compile("(?i:for)=\"?([^;,\"]+)\"?");
/*      */   
/*  113 */   private static final Object[] EMPTY_VALUES = new Object[0];
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private String scheme;
/*      */   
/*      */   @Nullable
/*      */   private String ssp;
/*      */   
/*      */   @Nullable
/*      */   private String userInfo;
/*      */   
/*      */   @Nullable
/*      */   private String host;
/*      */   
/*      */   @Nullable
/*      */   private String port;
/*      */   
/*      */   private CompositePathComponentBuilder pathBuilder;
/*      */   
/*  133 */   private final MultiValueMap<String, String> queryParams = (MultiValueMap<String, String>)new LinkedMultiValueMap();
/*      */   
/*      */   @Nullable
/*      */   private String fragment;
/*      */   
/*  138 */   private final Map<String, Object> uriVariables = new HashMap<>(4);
/*      */   
/*      */   private boolean encodeTemplate;
/*      */   
/*  142 */   private Charset charset = StandardCharsets.UTF_8;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected UriComponentsBuilder() {
/*  152 */     this.pathBuilder = new CompositePathComponentBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected UriComponentsBuilder(UriComponentsBuilder other) {
/*  161 */     this.scheme = other.scheme;
/*  162 */     this.ssp = other.ssp;
/*  163 */     this.userInfo = other.userInfo;
/*  164 */     this.host = other.host;
/*  165 */     this.port = other.port;
/*  166 */     this.pathBuilder = other.pathBuilder.cloneBuilder();
/*  167 */     this.uriVariables.putAll(other.uriVariables);
/*  168 */     this.queryParams.addAll(other.queryParams);
/*  169 */     this.fragment = other.fragment;
/*  170 */     this.encodeTemplate = other.encodeTemplate;
/*  171 */     this.charset = other.charset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static UriComponentsBuilder newInstance() {
/*  182 */     return new UriComponentsBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static UriComponentsBuilder fromPath(String path) {
/*  191 */     UriComponentsBuilder builder = new UriComponentsBuilder();
/*  192 */     builder.path(path);
/*  193 */     return builder;
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
/*      */ 
/*      */   
/*      */   public static UriComponentsBuilder fromUri(URI uri) {
/*  208 */     UriComponentsBuilder builder = new UriComponentsBuilder();
/*  209 */     builder.uri(uri);
/*  210 */     return builder;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static UriComponentsBuilder fromUriString(String uri) {
/*  228 */     Assert.notNull(uri, "URI must not be null");
/*  229 */     Matcher matcher = URI_PATTERN.matcher(uri);
/*  230 */     if (matcher.matches()) {
/*  231 */       UriComponentsBuilder builder = new UriComponentsBuilder();
/*  232 */       String scheme = matcher.group(2);
/*  233 */       String userInfo = matcher.group(5);
/*  234 */       String host = matcher.group(6);
/*  235 */       String port = matcher.group(8);
/*  236 */       String path = matcher.group(9);
/*  237 */       String query = matcher.group(11);
/*  238 */       String fragment = matcher.group(13);
/*  239 */       boolean opaque = false;
/*  240 */       if (StringUtils.hasLength(scheme)) {
/*  241 */         String rest = uri.substring(scheme.length());
/*  242 */         if (!rest.startsWith(":/")) {
/*  243 */           opaque = true;
/*      */         }
/*      */       } 
/*  246 */       builder.scheme(scheme);
/*  247 */       if (opaque) {
/*  248 */         String ssp = uri.substring(scheme.length() + 1);
/*  249 */         if (StringUtils.hasLength(fragment)) {
/*  250 */           ssp = ssp.substring(0, ssp.length() - fragment.length() + 1);
/*      */         }
/*  252 */         builder.schemeSpecificPart(ssp);
/*      */       } else {
/*      */         
/*  255 */         if (StringUtils.hasLength(scheme) && scheme.startsWith("http") && !StringUtils.hasLength(host)) {
/*  256 */           throw new IllegalArgumentException("[" + uri + "] is not a valid HTTP URL");
/*      */         }
/*  258 */         builder.userInfo(userInfo);
/*  259 */         builder.host(host);
/*  260 */         if (StringUtils.hasLength(port)) {
/*  261 */           builder.port(port);
/*      */         }
/*  263 */         builder.path(path);
/*  264 */         builder.query(query);
/*      */       } 
/*  266 */       if (StringUtils.hasText(fragment)) {
/*  267 */         builder.fragment(fragment);
/*      */       }
/*  269 */       return builder;
/*      */     } 
/*      */     
/*  272 */     throw new IllegalArgumentException("[" + uri + "] is not a valid URI");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static UriComponentsBuilder fromHttpUrl(String httpUrl) {
/*  291 */     Assert.notNull(httpUrl, "HTTP URL must not be null");
/*  292 */     Matcher matcher = HTTP_URL_PATTERN.matcher(httpUrl);
/*  293 */     if (matcher.matches()) {
/*  294 */       UriComponentsBuilder builder = new UriComponentsBuilder();
/*  295 */       String scheme = matcher.group(1);
/*  296 */       builder.scheme((scheme != null) ? scheme.toLowerCase() : null);
/*  297 */       builder.userInfo(matcher.group(4));
/*  298 */       String host = matcher.group(5);
/*  299 */       if (StringUtils.hasLength(scheme) && !StringUtils.hasLength(host)) {
/*  300 */         throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
/*      */       }
/*  302 */       builder.host(host);
/*  303 */       String port = matcher.group(7);
/*  304 */       if (StringUtils.hasLength(port)) {
/*  305 */         builder.port(port);
/*      */       }
/*  307 */       builder.path(matcher.group(8));
/*  308 */       builder.query(matcher.group(10));
/*  309 */       String fragment = matcher.group(12);
/*  310 */       if (StringUtils.hasText(fragment)) {
/*  311 */         builder.fragment(fragment);
/*      */       }
/*  313 */       return builder;
/*      */     } 
/*      */     
/*  316 */     throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
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
/*      */ 
/*      */ 
/*      */   
/*      */   public static UriComponentsBuilder fromHttpRequest(HttpRequest request) {
/*  332 */     return fromUri(request.getURI()).adaptFromForwardedHeaders(request.getHeaders());
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public static InetSocketAddress parseForwardedFor(HttpRequest request, @Nullable InetSocketAddress remoteAddress) {
/*  350 */     int port = (remoteAddress != null) ? remoteAddress.getPort() : ("https".equals(request.getURI().getScheme()) ? 443 : 80);
/*      */     
/*  352 */     String forwardedHeader = request.getHeaders().getFirst("Forwarded");
/*  353 */     if (StringUtils.hasText(forwardedHeader)) {
/*  354 */       String forwardedToUse = StringUtils.tokenizeToStringArray(forwardedHeader, ",")[0];
/*  355 */       Matcher matcher = FORWARDED_FOR_PATTERN.matcher(forwardedToUse);
/*  356 */       if (matcher.find()) {
/*  357 */         String value = matcher.group(1).trim();
/*  358 */         String host = value;
/*  359 */         int portSeparatorIdx = value.lastIndexOf(':');
/*  360 */         int squareBracketIdx = value.lastIndexOf(']');
/*  361 */         if (portSeparatorIdx > squareBracketIdx) {
/*  362 */           if (squareBracketIdx == -1 && value.indexOf(':') != portSeparatorIdx) {
/*  363 */             throw new IllegalArgumentException("Invalid IPv4 address: " + value);
/*      */           }
/*  365 */           host = value.substring(0, portSeparatorIdx);
/*      */           try {
/*  367 */             port = Integer.parseInt(value.substring(portSeparatorIdx + 1));
/*      */           }
/*  369 */           catch (NumberFormatException ex) {
/*  370 */             throw new IllegalArgumentException("Failed to parse a port from \"forwarded\"-type header value: " + value);
/*      */           } 
/*      */         } 
/*      */         
/*  374 */         return InetSocketAddress.createUnresolved(host, port);
/*      */       } 
/*      */     } 
/*      */     
/*  378 */     String forHeader = request.getHeaders().getFirst("X-Forwarded-For");
/*  379 */     if (StringUtils.hasText(forHeader)) {
/*  380 */       String host = StringUtils.tokenizeToStringArray(forHeader, ",")[0];
/*  381 */       return InetSocketAddress.createUnresolved(host, port);
/*      */     } 
/*      */     
/*  384 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static UriComponentsBuilder fromOriginHeader(String origin) {
/*  392 */     Matcher matcher = URI_PATTERN.matcher(origin);
/*  393 */     if (matcher.matches()) {
/*  394 */       UriComponentsBuilder builder = new UriComponentsBuilder();
/*  395 */       String scheme = matcher.group(2);
/*  396 */       String host = matcher.group(6);
/*  397 */       String port = matcher.group(8);
/*  398 */       if (StringUtils.hasLength(scheme)) {
/*  399 */         builder.scheme(scheme);
/*      */       }
/*  401 */       builder.host(host);
/*  402 */       if (StringUtils.hasLength(port)) {
/*  403 */         builder.port(port);
/*      */       }
/*  405 */       return builder;
/*      */     } 
/*      */     
/*  408 */     throw new IllegalArgumentException("[" + origin + "] is not a valid \"Origin\" header value");
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
/*      */   public final UriComponentsBuilder encode() {
/*  437 */     return encode(StandardCharsets.UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder encode(Charset charset) {
/*  446 */     this.encodeTemplate = true;
/*  447 */     this.charset = charset;
/*  448 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UriComponents build() {
/*  459 */     return build(false);
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
/*      */   public UriComponents build(boolean encoded) {
/*  472 */     return buildInternal(encoded ? EncodingHint.FULLY_ENCODED : (this.encodeTemplate ? EncodingHint.ENCODE_TEMPLATE : EncodingHint.NONE));
/*      */   }
/*      */ 
/*      */   
/*      */   private UriComponents buildInternal(EncodingHint hint) {
/*      */     UriComponents result;
/*  478 */     if (this.ssp != null) {
/*  479 */       result = new OpaqueUriComponents(this.scheme, this.ssp, this.fragment);
/*      */     }
/*      */     else {
/*      */       
/*  483 */       HierarchicalUriComponents uric = new HierarchicalUriComponents(this.scheme, this.fragment, this.userInfo, this.host, this.port, this.pathBuilder.build(), this.queryParams, (hint == EncodingHint.FULLY_ENCODED));
/*      */       
/*  485 */       result = (hint == EncodingHint.ENCODE_TEMPLATE) ? uric.encodeTemplate(this.charset) : uric;
/*      */     } 
/*  487 */     if (!this.uriVariables.isEmpty()) {
/*  488 */       result = result.expand(name -> this.uriVariables.getOrDefault(name, UriComponents.UriTemplateVariables.SKIP_VALUE));
/*      */     }
/*  490 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UriComponents buildAndExpand(Map<String, ?> uriVariables) {
/*  501 */     return build().expand(uriVariables);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UriComponents buildAndExpand(Object... uriVariableValues) {
/*  512 */     return build().expand(uriVariableValues);
/*      */   }
/*      */ 
/*      */   
/*      */   public URI build(Object... uriVariables) {
/*  517 */     return buildInternal(EncodingHint.ENCODE_TEMPLATE).expand(uriVariables).toUri();
/*      */   }
/*      */ 
/*      */   
/*      */   public URI build(Map<String, ?> uriVariables) {
/*  522 */     return buildInternal(EncodingHint.ENCODE_TEMPLATE).expand(uriVariables).toUri();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toUriString() {
/*  542 */     return this.uriVariables.isEmpty() ? build().encode().toUriString() : 
/*  543 */       buildInternal(EncodingHint.ENCODE_TEMPLATE).toUriString();
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
/*      */   public UriComponentsBuilder uri(URI uri) {
/*  555 */     Assert.notNull(uri, "URI must not be null");
/*  556 */     this.scheme = uri.getScheme();
/*  557 */     if (uri.isOpaque()) {
/*  558 */       this.ssp = uri.getRawSchemeSpecificPart();
/*  559 */       resetHierarchicalComponents();
/*      */     } else {
/*      */       
/*  562 */       if (uri.getRawUserInfo() != null) {
/*  563 */         this.userInfo = uri.getRawUserInfo();
/*      */       }
/*  565 */       if (uri.getHost() != null) {
/*  566 */         this.host = uri.getHost();
/*      */       }
/*  568 */       if (uri.getPort() != -1) {
/*  569 */         this.port = String.valueOf(uri.getPort());
/*      */       }
/*  571 */       if (StringUtils.hasLength(uri.getRawPath())) {
/*  572 */         this.pathBuilder = new CompositePathComponentBuilder();
/*  573 */         this.pathBuilder.addPath(uri.getRawPath());
/*      */       } 
/*  575 */       if (StringUtils.hasLength(uri.getRawQuery())) {
/*  576 */         this.queryParams.clear();
/*  577 */         query(uri.getRawQuery());
/*      */       } 
/*  579 */       resetSchemeSpecificPart();
/*      */     } 
/*  581 */     if (uri.getRawFragment() != null) {
/*  582 */       this.fragment = uri.getRawFragment();
/*      */     }
/*  584 */     return this;
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
/*      */   public UriComponentsBuilder uriComponents(UriComponents uriComponents) {
/*  597 */     Assert.notNull(uriComponents, "UriComponents must not be null");
/*  598 */     uriComponents.copyToUriComponentsBuilder(this);
/*  599 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder scheme(@Nullable String scheme) {
/*  604 */     this.scheme = scheme;
/*  605 */     return this;
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
/*      */   public UriComponentsBuilder schemeSpecificPart(String ssp) {
/*  617 */     this.ssp = ssp;
/*  618 */     resetHierarchicalComponents();
/*  619 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder userInfo(@Nullable String userInfo) {
/*  624 */     this.userInfo = userInfo;
/*  625 */     resetSchemeSpecificPart();
/*  626 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder host(@Nullable String host) {
/*  631 */     this.host = host;
/*  632 */     if (host != null) {
/*  633 */       resetSchemeSpecificPart();
/*      */     }
/*  635 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder port(int port) {
/*  640 */     Assert.isTrue((port >= -1), "Port must be >= -1");
/*  641 */     this.port = String.valueOf(port);
/*  642 */     if (port > -1) {
/*  643 */       resetSchemeSpecificPart();
/*      */     }
/*  645 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder port(@Nullable String port) {
/*  650 */     this.port = port;
/*  651 */     if (port != null) {
/*  652 */       resetSchemeSpecificPart();
/*      */     }
/*  654 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder path(String path) {
/*  659 */     this.pathBuilder.addPath(path);
/*  660 */     resetSchemeSpecificPart();
/*  661 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder pathSegment(String... pathSegments) throws IllegalArgumentException {
/*  666 */     this.pathBuilder.addPathSegments(pathSegments);
/*  667 */     resetSchemeSpecificPart();
/*  668 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder replacePath(@Nullable String path) {
/*  673 */     this.pathBuilder = new CompositePathComponentBuilder();
/*  674 */     if (path != null) {
/*  675 */       this.pathBuilder.addPath(path);
/*      */     }
/*  677 */     resetSchemeSpecificPart();
/*  678 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder query(@Nullable String query) {
/*  683 */     if (query != null) {
/*  684 */       Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
/*  685 */       while (matcher.find()) {
/*  686 */         String name = matcher.group(1);
/*  687 */         String eq = matcher.group(2);
/*  688 */         String value = matcher.group(3);
/*  689 */         queryParam(name, new Object[] { (value != null) ? value : (StringUtils.hasLength(eq) ? "" : null) });
/*      */       } 
/*  691 */       resetSchemeSpecificPart();
/*      */     } else {
/*      */       
/*  694 */       this.queryParams.clear();
/*      */     } 
/*  696 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder replaceQuery(@Nullable String query) {
/*  701 */     this.queryParams.clear();
/*  702 */     if (query != null) {
/*  703 */       query(query);
/*  704 */       resetSchemeSpecificPart();
/*      */     } 
/*  706 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder queryParam(String name, Object... values) {
/*  711 */     Assert.notNull(name, "Name must not be null");
/*  712 */     if (!ObjectUtils.isEmpty(values)) {
/*  713 */       for (Object value : values) {
/*  714 */         String valueAsString = getQueryParamValue(value);
/*  715 */         this.queryParams.add(name, valueAsString);
/*      */       } 
/*      */     } else {
/*      */       
/*  719 */       this.queryParams.add(name, null);
/*      */     } 
/*  721 */     resetSchemeSpecificPart();
/*  722 */     return this;
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   private String getQueryParamValue(@Nullable Object value) {
/*  727 */     if (value != null) {
/*  728 */       return (value instanceof Optional) ? ((Optional)value)
/*  729 */         .map(Object::toString).orElse(null) : value
/*  730 */         .toString();
/*      */     }
/*  732 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder queryParam(String name, @Nullable Collection<?> values) {
/*  737 */     return queryParam(name, CollectionUtils.isEmpty(values) ? EMPTY_VALUES : values.toArray());
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder queryParamIfPresent(String name, Optional<?> value) {
/*  742 */     value.ifPresent(o -> {
/*      */           if (o instanceof Collection) {
/*      */             queryParam(name, (Collection)o);
/*      */           } else {
/*      */             queryParam(name, new Object[] { o });
/*      */           } 
/*      */         });
/*      */     
/*  750 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder queryParams(@Nullable MultiValueMap<String, String> params) {
/*  759 */     if (params != null) {
/*  760 */       this.queryParams.addAll(params);
/*  761 */       resetSchemeSpecificPart();
/*      */     } 
/*  763 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder replaceQueryParam(String name, Object... values) {
/*  768 */     Assert.notNull(name, "Name must not be null");
/*  769 */     this.queryParams.remove(name);
/*  770 */     if (!ObjectUtils.isEmpty(values)) {
/*  771 */       queryParam(name, values);
/*      */     }
/*  773 */     resetSchemeSpecificPart();
/*  774 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder replaceQueryParam(String name, @Nullable Collection<?> values) {
/*  779 */     return replaceQueryParam(name, CollectionUtils.isEmpty(values) ? EMPTY_VALUES : values.toArray());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder replaceQueryParams(@Nullable MultiValueMap<String, String> params) {
/*  788 */     this.queryParams.clear();
/*  789 */     if (params != null) {
/*  790 */       this.queryParams.putAll((Map)params);
/*      */     }
/*  792 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder fragment(@Nullable String fragment) {
/*  797 */     if (fragment != null) {
/*  798 */       Assert.hasLength(fragment, "Fragment must not be empty");
/*  799 */       this.fragment = fragment;
/*      */     } else {
/*      */       
/*  802 */       this.fragment = null;
/*      */     } 
/*  804 */     return this;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder uriVariables(Map<String, Object> uriVariables) {
/*  822 */     this.uriVariables.putAll(uriVariables);
/*  823 */     return this;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   UriComponentsBuilder adaptFromForwardedHeaders(HttpHeaders headers) {
/*      */     try {
/*  842 */       String forwardedHeader = headers.getFirst("Forwarded");
/*  843 */       if (StringUtils.hasText(forwardedHeader)) {
/*  844 */         Matcher matcher = FORWARDED_PROTO_PATTERN.matcher(forwardedHeader);
/*  845 */         if (matcher.find()) {
/*  846 */           scheme(matcher.group(1).trim());
/*  847 */           port((String)null);
/*      */         }
/*  849 */         else if (isForwardedSslOn(headers)) {
/*  850 */           scheme("https");
/*  851 */           port((String)null);
/*      */         } 
/*  853 */         matcher = FORWARDED_HOST_PATTERN.matcher(forwardedHeader);
/*  854 */         if (matcher.find()) {
/*  855 */           adaptForwardedHost(matcher.group(1).trim());
/*      */         }
/*      */       } else {
/*      */         
/*  859 */         String protocolHeader = headers.getFirst("X-Forwarded-Proto");
/*  860 */         if (StringUtils.hasText(protocolHeader)) {
/*  861 */           scheme(StringUtils.tokenizeToStringArray(protocolHeader, ",")[0]);
/*  862 */           port((String)null);
/*      */         }
/*  864 */         else if (isForwardedSslOn(headers)) {
/*  865 */           scheme("https");
/*  866 */           port((String)null);
/*      */         } 
/*  868 */         String hostHeader = headers.getFirst("X-Forwarded-Host");
/*  869 */         if (StringUtils.hasText(hostHeader)) {
/*  870 */           adaptForwardedHost(StringUtils.tokenizeToStringArray(hostHeader, ",")[0]);
/*      */         }
/*  872 */         String portHeader = headers.getFirst("X-Forwarded-Port");
/*  873 */         if (StringUtils.hasText(portHeader)) {
/*  874 */           port(Integer.parseInt(StringUtils.tokenizeToStringArray(portHeader, ",")[0]));
/*      */         }
/*      */       }
/*      */     
/*  878 */     } catch (NumberFormatException ex) {
/*  879 */       throw new IllegalArgumentException("Failed to parse a port from \"forwarded\"-type headers. If not behind a trusted proxy, consider using ForwardedHeaderFilter with the removeOnly=true. Request headers: " + headers);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  884 */     if (this.scheme != null && (((this.scheme
/*  885 */       .equals("http") || this.scheme.equals("ws")) && "80".equals(this.port)) || ((this.scheme
/*  886 */       .equals("https") || this.scheme.equals("wss")) && "443".equals(this.port)))) {
/*  887 */       port((String)null);
/*      */     }
/*      */     
/*  890 */     return this;
/*      */   }
/*      */   
/*      */   private boolean isForwardedSslOn(HttpHeaders headers) {
/*  894 */     String forwardedSsl = headers.getFirst("X-Forwarded-Ssl");
/*  895 */     return (StringUtils.hasText(forwardedSsl) && forwardedSsl.equalsIgnoreCase("on"));
/*      */   }
/*      */   
/*      */   private void adaptForwardedHost(String rawValue) {
/*  899 */     int portSeparatorIdx = rawValue.lastIndexOf(':');
/*  900 */     int squareBracketIdx = rawValue.lastIndexOf(']');
/*  901 */     if (portSeparatorIdx > squareBracketIdx) {
/*  902 */       if (squareBracketIdx == -1 && rawValue.indexOf(':') != portSeparatorIdx) {
/*  903 */         throw new IllegalArgumentException("Invalid IPv4 address: " + rawValue);
/*      */       }
/*  905 */       host(rawValue.substring(0, portSeparatorIdx));
/*  906 */       port(Integer.parseInt(rawValue.substring(portSeparatorIdx + 1)));
/*      */     } else {
/*      */       
/*  909 */       host(rawValue);
/*  910 */       port((String)null);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void resetHierarchicalComponents() {
/*  915 */     this.userInfo = null;
/*  916 */     this.host = null;
/*  917 */     this.port = null;
/*  918 */     this.pathBuilder = new CompositePathComponentBuilder();
/*  919 */     this.queryParams.clear();
/*      */   }
/*      */   
/*      */   private void resetSchemeSpecificPart() {
/*  923 */     this.ssp = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object clone() {
/*  933 */     return cloneBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UriComponentsBuilder cloneBuilder() {
/*  942 */     return new UriComponentsBuilder(this);
/*      */   }
/*      */ 
/*      */   
/*      */   private static interface PathComponentBuilder
/*      */   {
/*      */     @Nullable
/*      */     HierarchicalUriComponents.PathComponent build();
/*      */     
/*      */     PathComponentBuilder cloneBuilder();
/*      */   }
/*      */   
/*      */   private static class CompositePathComponentBuilder
/*      */     implements PathComponentBuilder
/*      */   {
/*  957 */     private final Deque<UriComponentsBuilder.PathComponentBuilder> builders = new ArrayDeque<>();
/*      */     
/*      */     public void addPathSegments(String... pathSegments) {
/*  960 */       if (!ObjectUtils.isEmpty((Object[])pathSegments)) {
/*  961 */         UriComponentsBuilder.PathSegmentComponentBuilder psBuilder = getLastBuilder(UriComponentsBuilder.PathSegmentComponentBuilder.class);
/*  962 */         UriComponentsBuilder.FullPathComponentBuilder fpBuilder = getLastBuilder(UriComponentsBuilder.FullPathComponentBuilder.class);
/*  963 */         if (psBuilder == null) {
/*  964 */           psBuilder = new UriComponentsBuilder.PathSegmentComponentBuilder();
/*  965 */           this.builders.add(psBuilder);
/*  966 */           if (fpBuilder != null) {
/*  967 */             fpBuilder.removeTrailingSlash();
/*      */           }
/*      */         } 
/*  970 */         psBuilder.append(pathSegments);
/*      */       } 
/*      */     }
/*      */     
/*      */     public void addPath(String path) {
/*  975 */       if (StringUtils.hasText(path)) {
/*  976 */         UriComponentsBuilder.PathSegmentComponentBuilder psBuilder = getLastBuilder(UriComponentsBuilder.PathSegmentComponentBuilder.class);
/*  977 */         UriComponentsBuilder.FullPathComponentBuilder fpBuilder = getLastBuilder(UriComponentsBuilder.FullPathComponentBuilder.class);
/*  978 */         if (psBuilder != null) {
/*  979 */           path = path.startsWith("/") ? path : ("/" + path);
/*      */         }
/*  981 */         if (fpBuilder == null) {
/*  982 */           fpBuilder = new UriComponentsBuilder.FullPathComponentBuilder();
/*  983 */           this.builders.add(fpBuilder);
/*      */         } 
/*  985 */         fpBuilder.append(path);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     private <T> T getLastBuilder(Class<T> builderClass) {
/*  992 */       if (!this.builders.isEmpty()) {
/*  993 */         UriComponentsBuilder.PathComponentBuilder last = this.builders.getLast();
/*  994 */         if (builderClass.isInstance(last)) {
/*  995 */           return (T)last;
/*      */         }
/*      */       } 
/*  998 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     public HierarchicalUriComponents.PathComponent build() {
/* 1003 */       int size = this.builders.size();
/* 1004 */       List<HierarchicalUriComponents.PathComponent> components = new ArrayList<>(size);
/* 1005 */       for (UriComponentsBuilder.PathComponentBuilder componentBuilder : this.builders) {
/* 1006 */         HierarchicalUriComponents.PathComponent pathComponent = componentBuilder.build();
/* 1007 */         if (pathComponent != null) {
/* 1008 */           components.add(pathComponent);
/*      */         }
/*      */       } 
/* 1011 */       if (components.isEmpty()) {
/* 1012 */         return HierarchicalUriComponents.NULL_PATH_COMPONENT;
/*      */       }
/* 1014 */       if (components.size() == 1) {
/* 1015 */         return components.get(0);
/*      */       }
/* 1017 */       return new HierarchicalUriComponents.PathComponentComposite(components);
/*      */     }
/*      */ 
/*      */     
/*      */     public CompositePathComponentBuilder cloneBuilder() {
/* 1022 */       CompositePathComponentBuilder compositeBuilder = new CompositePathComponentBuilder();
/* 1023 */       for (UriComponentsBuilder.PathComponentBuilder builder : this.builders) {
/* 1024 */         compositeBuilder.builders.add(builder.cloneBuilder());
/*      */       }
/* 1026 */       return compositeBuilder;
/*      */     }
/*      */     
/*      */     private CompositePathComponentBuilder() {}
/*      */   }
/*      */   
/*      */   private static class FullPathComponentBuilder implements PathComponentBuilder {
/* 1033 */     private final StringBuilder path = new StringBuilder();
/*      */     
/*      */     public void append(String path) {
/* 1036 */       this.path.append(path);
/*      */     }
/*      */ 
/*      */     
/*      */     public HierarchicalUriComponents.PathComponent build() {
/* 1041 */       if (this.path.length() == 0) {
/* 1042 */         return null;
/*      */       }
/* 1044 */       String sanitized = getSanitizedPath(this.path);
/* 1045 */       return new HierarchicalUriComponents.FullPathComponent(sanitized);
/*      */     }
/*      */     
/*      */     private static String getSanitizedPath(StringBuilder path) {
/* 1049 */       int index = path.indexOf("//");
/* 1050 */       if (index >= 0) {
/* 1051 */         StringBuilder sanitized = new StringBuilder(path);
/* 1052 */         while (index != -1) {
/* 1053 */           sanitized.deleteCharAt(index);
/* 1054 */           index = sanitized.indexOf("//", index);
/*      */         } 
/* 1056 */         return sanitized.toString();
/*      */       } 
/* 1058 */       return path.toString();
/*      */     }
/*      */     
/*      */     public void removeTrailingSlash() {
/* 1062 */       int index = this.path.length() - 1;
/* 1063 */       if (this.path.charAt(index) == '/')
/* 1064 */         this.path.deleteCharAt(index); 
/*      */     }
/*      */     
/*      */     private FullPathComponentBuilder() {}
/*      */     
/*      */     public FullPathComponentBuilder cloneBuilder() {
/* 1070 */       FullPathComponentBuilder builder = new FullPathComponentBuilder();
/* 1071 */       builder.append(this.path.toString());
/* 1072 */       return builder;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class PathSegmentComponentBuilder
/*      */     implements PathComponentBuilder
/*      */   {
/* 1079 */     private final List<String> pathSegments = new ArrayList<>();
/*      */     
/*      */     public void append(String... pathSegments) {
/* 1082 */       for (String pathSegment : pathSegments) {
/* 1083 */         if (StringUtils.hasText(pathSegment)) {
/* 1084 */           this.pathSegments.add(pathSegment);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public HierarchicalUriComponents.PathComponent build() {
/* 1091 */       return this.pathSegments.isEmpty() ? null : new HierarchicalUriComponents.PathSegmentComponent(this.pathSegments);
/*      */     }
/*      */     
/*      */     private PathSegmentComponentBuilder() {}
/*      */     
/*      */     public PathSegmentComponentBuilder cloneBuilder() {
/* 1097 */       PathSegmentComponentBuilder builder = new PathSegmentComponentBuilder();
/* 1098 */       builder.pathSegments.addAll(this.pathSegments);
/* 1099 */       return builder;
/*      */     }
/*      */   }
/*      */   
/*      */   private enum EncodingHint {
/* 1104 */     ENCODE_TEMPLATE, FULLY_ENCODED, NONE;
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/UriComponentsBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */