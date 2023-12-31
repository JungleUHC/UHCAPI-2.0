/*      */ package org.springframework.http;
/*      */ 
/*      */ import java.io.Serializable;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URI;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.DecimalFormatSymbols;
/*      */ import java.time.Duration;
/*      */ import java.time.Instant;
/*      */ import java.time.ZoneId;
/*      */ import java.time.ZonedDateTime;
/*      */ import java.time.format.DateTimeFormatter;
/*      */ import java.time.format.DateTimeParseException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Base64;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.EnumSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringJoiner;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.stream.Collectors;
/*      */ import org.springframework.lang.Nullable;
/*      */ import org.springframework.util.Assert;
/*      */ import org.springframework.util.CollectionUtils;
/*      */ import org.springframework.util.LinkedCaseInsensitiveMap;
/*      */ import org.springframework.util.LinkedMultiValueMap;
/*      */ import org.springframework.util.MultiValueMap;
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
/*      */ public class HttpHeaders
/*      */   implements MultiValueMap<String, String>, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -8578554704772377436L;
/*      */   public static final String ACCEPT = "Accept";
/*      */   public static final String ACCEPT_CHARSET = "Accept-Charset";
/*      */   public static final String ACCEPT_ENCODING = "Accept-Encoding";
/*      */   public static final String ACCEPT_LANGUAGE = "Accept-Language";
/*      */   public static final String ACCEPT_PATCH = "Accept-Patch";
/*      */   public static final String ACCEPT_RANGES = "Accept-Ranges";
/*      */   public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
/*      */   public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
/*      */   public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
/*      */   public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
/*      */   public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
/*      */   public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
/*      */   public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
/*      */   public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
/*      */   public static final String AGE = "Age";
/*      */   public static final String ALLOW = "Allow";
/*      */   public static final String AUTHORIZATION = "Authorization";
/*      */   public static final String CACHE_CONTROL = "Cache-Control";
/*      */   public static final String CONNECTION = "Connection";
/*      */   public static final String CONTENT_ENCODING = "Content-Encoding";
/*      */   public static final String CONTENT_DISPOSITION = "Content-Disposition";
/*      */   public static final String CONTENT_LANGUAGE = "Content-Language";
/*      */   public static final String CONTENT_LENGTH = "Content-Length";
/*      */   public static final String CONTENT_LOCATION = "Content-Location";
/*      */   public static final String CONTENT_RANGE = "Content-Range";
/*      */   public static final String CONTENT_TYPE = "Content-Type";
/*      */   public static final String COOKIE = "Cookie";
/*      */   public static final String DATE = "Date";
/*      */   public static final String ETAG = "ETag";
/*      */   public static final String EXPECT = "Expect";
/*      */   public static final String EXPIRES = "Expires";
/*      */   public static final String FROM = "From";
/*      */   public static final String HOST = "Host";
/*      */   public static final String IF_MATCH = "If-Match";
/*      */   public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
/*      */   public static final String IF_NONE_MATCH = "If-None-Match";
/*      */   public static final String IF_RANGE = "If-Range";
/*      */   public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
/*      */   public static final String LAST_MODIFIED = "Last-Modified";
/*      */   public static final String LINK = "Link";
/*      */   public static final String LOCATION = "Location";
/*      */   public static final String MAX_FORWARDS = "Max-Forwards";
/*      */   public static final String ORIGIN = "Origin";
/*      */   public static final String PRAGMA = "Pragma";
/*      */   public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
/*      */   public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
/*      */   public static final String RANGE = "Range";
/*      */   public static final String REFERER = "Referer";
/*      */   public static final String RETRY_AFTER = "Retry-After";
/*      */   public static final String SERVER = "Server";
/*      */   public static final String SET_COOKIE = "Set-Cookie";
/*      */   public static final String SET_COOKIE2 = "Set-Cookie2";
/*      */   public static final String TE = "TE";
/*      */   public static final String TRAILER = "Trailer";
/*      */   public static final String TRANSFER_ENCODING = "Transfer-Encoding";
/*      */   public static final String UPGRADE = "Upgrade";
/*      */   public static final String USER_AGENT = "User-Agent";
/*      */   public static final String VARY = "Vary";
/*      */   public static final String VIA = "Via";
/*      */   public static final String WARNING = "Warning";
/*      */   public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
/*  394 */   public static final HttpHeaders EMPTY = new ReadOnlyHttpHeaders((MultiValueMap<String, String>)new LinkedMultiValueMap());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  400 */   private static final Pattern ETAG_HEADER_VALUE_PATTERN = Pattern.compile("\\*|\\s*((W\\/)?(\"[^\"]*\"))\\s*,?");
/*      */   
/*  402 */   private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.ENGLISH);
/*      */   
/*  404 */   private static final ZoneId GMT = ZoneId.of("GMT");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  410 */   private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).withZone(GMT);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  416 */   private static final DateTimeFormatter[] DATE_PARSERS = new DateTimeFormatter[] { DateTimeFormatter.RFC_1123_DATE_TIME, 
/*      */       
/*  418 */       DateTimeFormatter.ofPattern("EEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US), 
/*  419 */       DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.US).withZone(GMT) };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final MultiValueMap<String, String> headers;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders() {
/*  431 */     this(CollectionUtils.toMultiValueMap((Map)new LinkedCaseInsensitiveMap(8, Locale.ENGLISH)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpHeaders(MultiValueMap<String, String> headers) {
/*  442 */     Assert.notNull(headers, "MultiValueMap must not be null");
/*  443 */     this.headers = headers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getOrEmpty(Object headerName) {
/*  454 */     List<String> values = get(headerName);
/*  455 */     return (values != null) ? values : Collections.<String>emptyList();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccept(List<MediaType> acceptableMediaTypes) {
/*  463 */     set("Accept", MediaType.toString(acceptableMediaTypes));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<MediaType> getAccept() {
/*  472 */     return MediaType.parseMediaTypes(get("Accept"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAcceptLanguage(List<Locale.LanguageRange> languages) {
/*  481 */     Assert.notNull(languages, "LanguageRange List must not be null");
/*  482 */     DecimalFormat decimal = new DecimalFormat("0.0", DECIMAL_FORMAT_SYMBOLS);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  488 */     List<String> values = (List<String>)languages.stream().map(range -> (range.getWeight() == 1.0D) ? range.getRange() : (range.getRange() + ";q=" + decimal.format(range.getWeight()))).collect(Collectors.toList());
/*  489 */     set("Accept-Language", toCommaDelimitedString(values));
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
/*      */   public List<Locale.LanguageRange> getAcceptLanguage() {
/*  502 */     String value = getFirst("Accept-Language");
/*  503 */     return StringUtils.hasText(value) ? Locale.LanguageRange.parse(value) : Collections.<Locale.LanguageRange>emptyList();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAcceptLanguageAsLocales(List<Locale> locales) {
/*  511 */     setAcceptLanguage((List<Locale.LanguageRange>)locales.stream()
/*  512 */         .map(locale -> new Locale.LanguageRange(locale.toLanguageTag()))
/*  513 */         .collect(Collectors.toList()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Locale> getAcceptLanguageAsLocales() {
/*  524 */     List<Locale.LanguageRange> ranges = getAcceptLanguage();
/*  525 */     if (ranges.isEmpty()) {
/*  526 */       return Collections.emptyList();
/*      */     }
/*  528 */     return (List<Locale>)ranges.stream()
/*  529 */       .map(range -> Locale.forLanguageTag(range.getRange()))
/*  530 */       .filter(locale -> StringUtils.hasText(locale.getDisplayName()))
/*  531 */       .collect(Collectors.toList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAcceptPatch(List<MediaType> mediaTypes) {
/*  540 */     set("Accept-Patch", MediaType.toString(mediaTypes));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<MediaType> getAcceptPatch() {
/*  550 */     return MediaType.parseMediaTypes(get("Accept-Patch"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccessControlAllowCredentials(boolean allowCredentials) {
/*  557 */     set("Access-Control-Allow-Credentials", Boolean.toString(allowCredentials));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAccessControlAllowCredentials() {
/*  564 */     return Boolean.parseBoolean(getFirst("Access-Control-Allow-Credentials"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccessControlAllowHeaders(List<String> allowedHeaders) {
/*  571 */     set("Access-Control-Allow-Headers", toCommaDelimitedString(allowedHeaders));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getAccessControlAllowHeaders() {
/*  578 */     return getValuesAsList("Access-Control-Allow-Headers");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccessControlAllowMethods(List<HttpMethod> allowedMethods) {
/*  585 */     set("Access-Control-Allow-Methods", StringUtils.collectionToCommaDelimitedString(allowedMethods));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<HttpMethod> getAccessControlAllowMethods() {
/*  592 */     List<HttpMethod> result = new ArrayList<>();
/*  593 */     String value = getFirst("Access-Control-Allow-Methods");
/*  594 */     if (value != null) {
/*  595 */       String[] tokens = StringUtils.tokenizeToStringArray(value, ",");
/*  596 */       for (String token : tokens) {
/*  597 */         HttpMethod resolved = HttpMethod.resolve(token);
/*  598 */         if (resolved != null) {
/*  599 */           result.add(resolved);
/*      */         }
/*      */       } 
/*      */     } 
/*  603 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccessControlAllowOrigin(@Nullable String allowedOrigin) {
/*  610 */     setOrRemove("Access-Control-Allow-Origin", allowedOrigin);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getAccessControlAllowOrigin() {
/*  618 */     return getFieldValues("Access-Control-Allow-Origin");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccessControlExposeHeaders(List<String> exposedHeaders) {
/*  625 */     set("Access-Control-Expose-Headers", toCommaDelimitedString(exposedHeaders));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getAccessControlExposeHeaders() {
/*  632 */     return getValuesAsList("Access-Control-Expose-Headers");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccessControlMaxAge(Duration maxAge) {
/*  640 */     set("Access-Control-Max-Age", Long.toString(maxAge.getSeconds()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccessControlMaxAge(long maxAge) {
/*  647 */     set("Access-Control-Max-Age", Long.toString(maxAge));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getAccessControlMaxAge() {
/*  655 */     String value = getFirst("Access-Control-Max-Age");
/*  656 */     return (value != null) ? Long.parseLong(value) : -1L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccessControlRequestHeaders(List<String> requestHeaders) {
/*  663 */     set("Access-Control-Request-Headers", toCommaDelimitedString(requestHeaders));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getAccessControlRequestHeaders() {
/*  670 */     return getValuesAsList("Access-Control-Request-Headers");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAccessControlRequestMethod(@Nullable HttpMethod requestMethod) {
/*  677 */     setOrRemove("Access-Control-Request-Method", (requestMethod != null) ? requestMethod.name() : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public HttpMethod getAccessControlRequestMethod() {
/*  685 */     return HttpMethod.resolve(getFirst("Access-Control-Request-Method"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAcceptCharset(List<Charset> acceptableCharsets) {
/*  693 */     StringJoiner joiner = new StringJoiner(", ");
/*  694 */     for (Charset charset : acceptableCharsets) {
/*  695 */       joiner.add(charset.name().toLowerCase(Locale.ENGLISH));
/*      */     }
/*  697 */     set("Accept-Charset", joiner.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Charset> getAcceptCharset() {
/*  705 */     String value = getFirst("Accept-Charset");
/*  706 */     if (value != null) {
/*  707 */       String[] tokens = StringUtils.tokenizeToStringArray(value, ",");
/*  708 */       List<Charset> result = new ArrayList<>(tokens.length);
/*  709 */       for (String token : tokens) {
/*  710 */         String charsetName; int paramIdx = token.indexOf(';');
/*      */         
/*  712 */         if (paramIdx == -1) {
/*  713 */           charsetName = token;
/*      */         } else {
/*      */           
/*  716 */           charsetName = token.substring(0, paramIdx);
/*      */         } 
/*  718 */         if (!charsetName.equals("*")) {
/*  719 */           result.add(Charset.forName(charsetName));
/*      */         }
/*      */       } 
/*  722 */       return result;
/*      */     } 
/*      */     
/*  725 */     return Collections.emptyList();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAllow(Set<HttpMethod> allowedMethods) {
/*  734 */     set("Allow", StringUtils.collectionToCommaDelimitedString(allowedMethods));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<HttpMethod> getAllow() {
/*  743 */     String value = getFirst("Allow");
/*  744 */     if (StringUtils.hasLength(value)) {
/*  745 */       String[] tokens = StringUtils.tokenizeToStringArray(value, ",");
/*  746 */       List<HttpMethod> result = new ArrayList<>(tokens.length);
/*  747 */       for (String token : tokens) {
/*  748 */         HttpMethod resolved = HttpMethod.resolve(token);
/*  749 */         if (resolved != null) {
/*  750 */           result.add(resolved);
/*      */         }
/*      */       } 
/*  753 */       return EnumSet.copyOf(result);
/*      */     } 
/*      */     
/*  756 */     return EnumSet.noneOf(HttpMethod.class);
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
/*      */   public void setBasicAuth(String username, String password) {
/*  776 */     setBasicAuth(username, password, null);
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
/*      */   public void setBasicAuth(String username, String password, @Nullable Charset charset) {
/*  795 */     setBasicAuth(encodeBasicAuth(username, password, charset));
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
/*      */   public void setBasicAuth(String encodedCredentials) {
/*  815 */     Assert.hasText(encodedCredentials, "'encodedCredentials' must not be null or blank");
/*  816 */     set("Authorization", "Basic " + encodedCredentials);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBearerAuth(String token) {
/*  827 */     set("Authorization", "Bearer " + token);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCacheControl(CacheControl cacheControl) {
/*  836 */     setOrRemove("Cache-Control", cacheControl.getHeaderValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCacheControl(@Nullable String cacheControl) {
/*  843 */     setOrRemove("Cache-Control", cacheControl);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getCacheControl() {
/*  851 */     return getFieldValues("Cache-Control");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConnection(String connection) {
/*  858 */     set("Connection", connection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConnection(List<String> connection) {
/*  865 */     set("Connection", toCommaDelimitedString(connection));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getConnection() {
/*  872 */     return getValuesAsList("Connection");
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
/*      */   public void setContentDispositionFormData(String name, @Nullable String filename) {
/*  887 */     Assert.notNull(name, "Name must not be null");
/*  888 */     ContentDisposition.Builder disposition = ContentDisposition.formData().name(name);
/*  889 */     if (StringUtils.hasText(filename)) {
/*  890 */       disposition.filename(filename);
/*      */     }
/*  892 */     setContentDisposition(disposition.build());
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
/*      */   public void setContentDisposition(ContentDisposition contentDisposition) {
/*  906 */     set("Content-Disposition", contentDisposition.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ContentDisposition getContentDisposition() {
/*  915 */     String contentDisposition = getFirst("Content-Disposition");
/*  916 */     if (StringUtils.hasText(contentDisposition)) {
/*  917 */       return ContentDisposition.parse(contentDisposition);
/*      */     }
/*  919 */     return ContentDisposition.empty();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setContentLanguage(@Nullable Locale locale) {
/*  930 */     setOrRemove("Content-Language", (locale != null) ? locale.toLanguageTag() : null);
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
/*      */   @Nullable
/*      */   public Locale getContentLanguage() {
/*  944 */     return getValuesAsList("Content-Language")
/*  945 */       .stream()
/*  946 */       .findFirst()
/*  947 */       .map(Locale::forLanguageTag)
/*  948 */       .orElse(null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setContentLength(long contentLength) {
/*  956 */     set("Content-Length", Long.toString(contentLength));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getContentLength() {
/*  965 */     String value = getFirst("Content-Length");
/*  966 */     return (value != null) ? Long.parseLong(value) : -1L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setContentType(@Nullable MediaType mediaType) {
/*  974 */     if (mediaType != null) {
/*  975 */       Assert.isTrue(!mediaType.isWildcardType(), "Content-Type cannot contain wildcard type '*'");
/*  976 */       Assert.isTrue(!mediaType.isWildcardSubtype(), "Content-Type cannot contain wildcard subtype '*'");
/*  977 */       set("Content-Type", mediaType.toString());
/*      */     } else {
/*      */       
/*  980 */       remove("Content-Type");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public MediaType getContentType() {
/*  991 */     String value = getFirst("Content-Type");
/*  992 */     return StringUtils.hasLength(value) ? MediaType.parseMediaType(value) : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDate(ZonedDateTime date) {
/* 1001 */     setZonedDateTime("Date", date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDate(Instant date) {
/* 1010 */     setInstant("Date", date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDate(long date) {
/* 1020 */     setDate("Date", date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getDate() {
/* 1031 */     return getFirstDate("Date");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setETag(@Nullable String etag) {
/* 1038 */     if (etag != null) {
/* 1039 */       Assert.isTrue((etag.startsWith("\"") || etag.startsWith("W/")), "Invalid ETag: does not start with W/ or \"");
/*      */       
/* 1041 */       Assert.isTrue(etag.endsWith("\""), "Invalid ETag: does not end with \"");
/* 1042 */       set("ETag", etag);
/*      */     } else {
/*      */       
/* 1045 */       remove("ETag");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getETag() {
/* 1054 */     return getFirst("ETag");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExpires(ZonedDateTime expires) {
/* 1063 */     setZonedDateTime("Expires", expires);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExpires(Instant expires) {
/* 1072 */     setInstant("Expires", expires);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExpires(long expires) {
/* 1082 */     setDate("Expires", expires);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getExpires() {
/* 1093 */     return getFirstDate("Expires", false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHost(@Nullable InetSocketAddress host) {
/* 1104 */     if (host != null) {
/* 1105 */       String value = host.getHostString();
/* 1106 */       int port = host.getPort();
/* 1107 */       if (port != 0) {
/* 1108 */         value = value + ":" + port;
/*      */       }
/* 1110 */       set("Host", value);
/*      */     } else {
/*      */       
/* 1113 */       remove("Host", null);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public InetSocketAddress getHost() {
/* 1126 */     String value = getFirst("Host");
/* 1127 */     if (value == null) {
/* 1128 */       return null;
/*      */     }
/*      */     
/* 1131 */     String host = null;
/* 1132 */     int port = 0;
/* 1133 */     int separator = value.startsWith("[") ? value.indexOf(':', value.indexOf(']')) : value.lastIndexOf(':');
/* 1134 */     if (separator != -1) {
/* 1135 */       host = value.substring(0, separator);
/* 1136 */       String portString = value.substring(separator + 1);
/*      */       try {
/* 1138 */         port = Integer.parseInt(portString);
/*      */       }
/* 1140 */       catch (NumberFormatException numberFormatException) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1145 */     if (host == null) {
/* 1146 */       host = value;
/*      */     }
/* 1148 */     return InetSocketAddress.createUnresolved(host, port);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfMatch(String ifMatch) {
/* 1156 */     set("If-Match", ifMatch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfMatch(List<String> ifMatchList) {
/* 1164 */     set("If-Match", toCommaDelimitedString(ifMatchList));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getIfMatch() {
/* 1173 */     return getETagValuesAsList("If-Match");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfModifiedSince(ZonedDateTime ifModifiedSince) {
/* 1182 */     setZonedDateTime("If-Modified-Since", ifModifiedSince.withZoneSameInstant(GMT));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfModifiedSince(Instant ifModifiedSince) {
/* 1191 */     setInstant("If-Modified-Since", ifModifiedSince);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfModifiedSince(long ifModifiedSince) {
/* 1200 */     setDate("If-Modified-Since", ifModifiedSince);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getIfModifiedSince() {
/* 1210 */     return getFirstDate("If-Modified-Since", false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfNoneMatch(String ifNoneMatch) {
/* 1217 */     set("If-None-Match", ifNoneMatch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfNoneMatch(List<String> ifNoneMatchList) {
/* 1224 */     set("If-None-Match", toCommaDelimitedString(ifNoneMatchList));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getIfNoneMatch() {
/* 1232 */     return getETagValuesAsList("If-None-Match");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfUnmodifiedSince(ZonedDateTime ifUnmodifiedSince) {
/* 1241 */     setZonedDateTime("If-Unmodified-Since", ifUnmodifiedSince.withZoneSameInstant(GMT));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfUnmodifiedSince(Instant ifUnmodifiedSince) {
/* 1250 */     setInstant("If-Unmodified-Since", ifUnmodifiedSince);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIfUnmodifiedSince(long ifUnmodifiedSince) {
/* 1260 */     setDate("If-Unmodified-Since", ifUnmodifiedSince);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getIfUnmodifiedSince() {
/* 1271 */     return getFirstDate("If-Unmodified-Since", false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLastModified(ZonedDateTime lastModified) {
/* 1280 */     setZonedDateTime("Last-Modified", lastModified.withZoneSameInstant(GMT));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLastModified(Instant lastModified) {
/* 1289 */     setInstant("Last-Modified", lastModified);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLastModified(long lastModified) {
/* 1299 */     setDate("Last-Modified", lastModified);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLastModified() {
/* 1310 */     return getFirstDate("Last-Modified", false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLocation(@Nullable URI location) {
/* 1318 */     setOrRemove("Location", (location != null) ? location.toASCIIString() : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public URI getLocation() {
/* 1328 */     String value = getFirst("Location");
/* 1329 */     return (value != null) ? URI.create(value) : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOrigin(@Nullable String origin) {
/* 1336 */     setOrRemove("Origin", origin);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getOrigin() {
/* 1344 */     return getFirst("Origin");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPragma(@Nullable String pragma) {
/* 1351 */     setOrRemove("Pragma", pragma);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getPragma() {
/* 1359 */     return getFirst("Pragma");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRange(List<HttpRange> ranges) {
/* 1366 */     String value = HttpRange.toString(ranges);
/* 1367 */     set("Range", value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<HttpRange> getRange() {
/* 1375 */     String value = getFirst("Range");
/* 1376 */     return HttpRange.parseRanges(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUpgrade(@Nullable String upgrade) {
/* 1383 */     setOrRemove("Upgrade", upgrade);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public String getUpgrade() {
/* 1391 */     return getFirst("Upgrade");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setVary(List<String> requestHeaders) {
/* 1402 */     set("Vary", toCommaDelimitedString(requestHeaders));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getVary() {
/* 1410 */     return getValuesAsList("Vary");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setZonedDateTime(String headerName, ZonedDateTime date) {
/* 1420 */     set(headerName, DATE_FORMATTER.format(date));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInstant(String headerName, Instant date) {
/* 1430 */     setZonedDateTime(headerName, ZonedDateTime.ofInstant(date, GMT));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDate(String headerName, long date) {
/* 1441 */     setInstant(headerName, Instant.ofEpochMilli(date));
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
/*      */   public long getFirstDate(String headerName) {
/* 1454 */     return getFirstDate(headerName, true);
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
/*      */   private long getFirstDate(String headerName, boolean rejectInvalid) {
/* 1470 */     ZonedDateTime zonedDateTime = getFirstZonedDateTime(headerName, rejectInvalid);
/* 1471 */     return (zonedDateTime != null) ? zonedDateTime.toInstant().toEpochMilli() : -1L;
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
/*      */   @Nullable
/*      */   public ZonedDateTime getFirstZonedDateTime(String headerName) {
/* 1484 */     return getFirstZonedDateTime(headerName, true);
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
/*      */   @Nullable
/*      */   private ZonedDateTime getFirstZonedDateTime(String headerName, boolean rejectInvalid) {
/* 1500 */     String headerValue = getFirst(headerName);
/* 1501 */     if (headerValue == null)
/*      */     {
/* 1503 */       return null;
/*      */     }
/* 1505 */     if (headerValue.length() >= 3) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1510 */       int parametersIndex = headerValue.indexOf(';');
/* 1511 */       if (parametersIndex != -1) {
/* 1512 */         headerValue = headerValue.substring(0, parametersIndex);
/*      */       }
/*      */       
/* 1515 */       for (DateTimeFormatter dateFormatter : DATE_PARSERS) {
/*      */         try {
/* 1517 */           return ZonedDateTime.parse(headerValue, dateFormatter);
/*      */         }
/* 1519 */         catch (DateTimeParseException dateTimeParseException) {}
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1525 */     if (rejectInvalid) {
/* 1526 */       throw new IllegalArgumentException("Cannot parse date value \"" + headerValue + "\" for \"" + headerName + "\" header");
/*      */     }
/*      */     
/* 1529 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getValuesAsList(String headerName) {
/* 1540 */     List<String> values = get(headerName);
/* 1541 */     if (values != null) {
/* 1542 */       List<String> result = new ArrayList<>();
/* 1543 */       for (String value : values) {
/* 1544 */         if (value != null) {
/* 1545 */           Collections.addAll(result, StringUtils.tokenizeToStringArray(value, ","));
/*      */         }
/*      */       } 
/* 1548 */       return result;
/*      */     } 
/* 1550 */     return Collections.emptyList();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearContentHeaders() {
/* 1560 */     this.headers.remove("Content-Disposition");
/* 1561 */     this.headers.remove("Content-Encoding");
/* 1562 */     this.headers.remove("Content-Language");
/* 1563 */     this.headers.remove("Content-Length");
/* 1564 */     this.headers.remove("Content-Location");
/* 1565 */     this.headers.remove("Content-Range");
/* 1566 */     this.headers.remove("Content-Type");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected List<String> getETagValuesAsList(String headerName) {
/* 1577 */     List<String> values = get(headerName);
/* 1578 */     if (values != null) {
/* 1579 */       List<String> result = new ArrayList<>();
/* 1580 */       for (String value : values) {
/* 1581 */         if (value != null) {
/* 1582 */           Matcher matcher = ETAG_HEADER_VALUE_PATTERN.matcher(value);
/* 1583 */           while (matcher.find()) {
/* 1584 */             if ("*".equals(matcher.group())) {
/* 1585 */               result.add(matcher.group());
/*      */               continue;
/*      */             } 
/* 1588 */             result.add(matcher.group(1));
/*      */           } 
/*      */           
/* 1591 */           if (result.isEmpty()) {
/* 1592 */             throw new IllegalArgumentException("Could not parse header '" + headerName + "' with value '" + value + "'");
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/* 1597 */       return result;
/*      */     } 
/* 1599 */     return Collections.emptyList();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected String getFieldValues(String headerName) {
/* 1610 */     List<String> headerValues = get(headerName);
/* 1611 */     return (headerValues != null) ? toCommaDelimitedString(headerValues) : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String toCommaDelimitedString(List<String> headerValues) {
/* 1620 */     StringJoiner joiner = new StringJoiner(", ");
/* 1621 */     for (String val : headerValues) {
/* 1622 */       if (val != null) {
/* 1623 */         joiner.add(val);
/*      */       }
/*      */     } 
/* 1626 */     return joiner.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setOrRemove(String headerName, @Nullable String headerValue) {
/* 1635 */     if (headerValue != null) {
/* 1636 */       set(headerName, headerValue);
/*      */     } else {
/*      */       
/* 1639 */       remove(headerName);
/*      */     } 
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
/*      */   @Nullable
/*      */   public String getFirst(String headerName) {
/* 1654 */     return (String)this.headers.getFirst(headerName);
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
/*      */   public void add(String headerName, @Nullable String headerValue) {
/* 1667 */     this.headers.add(headerName, headerValue);
/*      */   }
/*      */ 
/*      */   
/*      */   public void addAll(String key, List<? extends String> values) {
/* 1672 */     this.headers.addAll(key, values);
/*      */   }
/*      */ 
/*      */   
/*      */   public void addAll(MultiValueMap<String, String> values) {
/* 1677 */     this.headers.addAll(values);
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
/*      */   public void set(String headerName, @Nullable String headerValue) {
/* 1690 */     this.headers.set(headerName, headerValue);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAll(Map<String, String> values) {
/* 1695 */     this.headers.setAll(values);
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<String, String> toSingleValueMap() {
/* 1700 */     return this.headers.toSingleValueMap();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int size() {
/* 1708 */     return this.headers.size();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/* 1713 */     return this.headers.isEmpty();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsKey(Object key) {
/* 1718 */     return this.headers.containsKey(key);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean containsValue(Object value) {
/* 1723 */     return this.headers.containsValue(value);
/*      */   }
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public List<String> get(Object key) {
/* 1729 */     return (List<String>)this.headers.get(key);
/*      */   }
/*      */ 
/*      */   
/*      */   public List<String> put(String key, List<String> value) {
/* 1734 */     return (List<String>)this.headers.put(key, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public List<String> remove(Object key) {
/* 1739 */     return (List<String>)this.headers.remove(key);
/*      */   }
/*      */ 
/*      */   
/*      */   public void putAll(Map<? extends String, ? extends List<String>> map) {
/* 1744 */     this.headers.putAll(map);
/*      */   }
/*      */ 
/*      */   
/*      */   public void clear() {
/* 1749 */     this.headers.clear();
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<String> keySet() {
/* 1754 */     return this.headers.keySet();
/*      */   }
/*      */ 
/*      */   
/*      */   public Collection<List<String>> values() {
/* 1759 */     return this.headers.values();
/*      */   }
/*      */ 
/*      */   
/*      */   public Set<Map.Entry<String, List<String>>> entrySet() {
/* 1764 */     return this.headers.entrySet();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(@Nullable Object other) {
/* 1770 */     if (this == other) {
/* 1771 */       return true;
/*      */     }
/* 1773 */     if (!(other instanceof HttpHeaders)) {
/* 1774 */       return false;
/*      */     }
/* 1776 */     return unwrap(this).equals(unwrap((HttpHeaders)other));
/*      */   }
/*      */   
/*      */   private static MultiValueMap<String, String> unwrap(HttpHeaders headers) {
/* 1780 */     while (headers.headers instanceof HttpHeaders) {
/* 1781 */       headers = (HttpHeaders)headers.headers;
/*      */     }
/* 1783 */     return headers.headers;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1788 */     return this.headers.hashCode();
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1793 */     return formatHeaders(this.headers);
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
/*      */   public static HttpHeaders readOnlyHttpHeaders(MultiValueMap<String, String> headers) {
/* 1806 */     return (headers instanceof HttpHeaders) ? 
/* 1807 */       readOnlyHttpHeaders((HttpHeaders)headers) : new ReadOnlyHttpHeaders(headers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpHeaders readOnlyHttpHeaders(HttpHeaders headers) {
/* 1817 */     Assert.notNull(headers, "HttpHeaders must not be null");
/* 1818 */     return (headers instanceof ReadOnlyHttpHeaders) ? headers : new ReadOnlyHttpHeaders(headers.headers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpHeaders writableHttpHeaders(HttpHeaders headers) {
/* 1829 */     Assert.notNull(headers, "HttpHeaders must not be null");
/* 1830 */     if (headers == EMPTY) {
/* 1831 */       return new HttpHeaders();
/*      */     }
/* 1833 */     return (headers instanceof ReadOnlyHttpHeaders) ? new HttpHeaders(headers.headers) : headers;
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
/*      */   public static String formatHeaders(MultiValueMap<String, String> headers) {
/* 1845 */     return headers.entrySet().stream()
/* 1846 */       .map(entry -> {
/*      */           List<String> values = (List<String>)entry.getValue();
/*      */ 
/*      */ 
/*      */           
/*      */           return (String)entry.getKey() + ":" + ((values.size() == 1) ? ("\"" + (String)values.get(0) + "\"") : values.stream().map(()).collect(Collectors.joining(", ")));
/* 1852 */         }).collect(Collectors.joining(", ", "[", "]"));
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
/*      */   public static String encodeBasicAuth(String username, String password, @Nullable Charset charset) {
/* 1872 */     Assert.notNull(username, "Username must not be null");
/* 1873 */     Assert.doesNotContain(username, ":", "Username must not contain a colon");
/* 1874 */     Assert.notNull(password, "Password must not be null");
/* 1875 */     if (charset == null) {
/* 1876 */       charset = StandardCharsets.ISO_8859_1;
/*      */     }
/*      */     
/* 1879 */     CharsetEncoder encoder = charset.newEncoder();
/* 1880 */     if (!encoder.canEncode(username) || !encoder.canEncode(password)) {
/* 1881 */       throw new IllegalArgumentException("Username or password contains characters that cannot be encoded to " + charset
/* 1882 */           .displayName());
/*      */     }
/*      */     
/* 1885 */     String credentialsString = username + ":" + password;
/* 1886 */     byte[] encodedBytes = Base64.getEncoder().encode(credentialsString.getBytes(charset));
/* 1887 */     return new String(encodedBytes, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   static String formatDate(long date) {
/* 1892 */     Instant instant = Instant.ofEpochMilli(date);
/* 1893 */     ZonedDateTime time = ZonedDateTime.ofInstant(instant, GMT);
/* 1894 */     return DATE_FORMATTER.format(time);
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/HttpHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */