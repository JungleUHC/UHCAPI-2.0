/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URI;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.server.RequestPath;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ public abstract class AbstractServerHttpRequest
/*     */   implements ServerHttpRequest
/*     */ {
/*  43 */   private static final Pattern QUERY_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
/*     */ 
/*     */   
/*     */   private final URI uri;
/*     */ 
/*     */   
/*     */   private final RequestPath path;
/*     */ 
/*     */   
/*     */   private final HttpHeaders headers;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private MultiValueMap<String, String> queryParams;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private MultiValueMap<String, HttpCookie> cookies;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private SslInfo sslInfo;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String id;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String logPrefix;
/*     */ 
/*     */   
/*     */   public AbstractServerHttpRequest(URI uri, @Nullable String contextPath, MultiValueMap<String, String> headers) {
/*  76 */     this.uri = uri;
/*  77 */     this.path = RequestPath.parse(uri, contextPath);
/*  78 */     this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractServerHttpRequest(URI uri, @Nullable String contextPath, HttpHeaders headers) {
/*  88 */     this.uri = uri;
/*  89 */     this.path = RequestPath.parse(uri, contextPath);
/*  90 */     this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getId() {
/*  96 */     if (this.id == null) {
/*  97 */       this.id = initId();
/*  98 */       if (this.id == null) {
/*  99 */         this.id = ObjectUtils.getIdentityHexString(this);
/*     */       }
/*     */     } 
/* 102 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String initId() {
/* 112 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/* 117 */     return this.uri;
/*     */   }
/*     */ 
/*     */   
/*     */   public RequestPath getPath() {
/* 122 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/* 127 */     return this.headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, String> getQueryParams() {
/* 132 */     if (this.queryParams == null) {
/* 133 */       this.queryParams = CollectionUtils.unmodifiableMultiValueMap(initQueryParams());
/*     */     }
/* 135 */     return this.queryParams;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MultiValueMap<String, String> initQueryParams() {
/* 146 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/* 147 */     String query = getURI().getRawQuery();
/* 148 */     if (query != null) {
/* 149 */       Matcher matcher = QUERY_PATTERN.matcher(query);
/* 150 */       while (matcher.find()) {
/* 151 */         String name = decodeQueryParam(matcher.group(1));
/* 152 */         String eq = matcher.group(2);
/* 153 */         String value = matcher.group(3);
/* 154 */         value = (value != null) ? decodeQueryParam(value) : (StringUtils.hasLength(eq) ? "" : null);
/* 155 */         linkedMultiValueMap.add(name, value);
/*     */       } 
/*     */     } 
/* 158 */     return (MultiValueMap<String, String>)linkedMultiValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   private String decodeQueryParam(String value) {
/*     */     try {
/* 164 */       return URLDecoder.decode(value, "UTF-8");
/*     */     }
/* 166 */     catch (UnsupportedEncodingException ex) {
/*     */       
/* 168 */       return URLDecoder.decode(value);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, HttpCookie> getCookies() {
/* 174 */     if (this.cookies == null) {
/* 175 */       this.cookies = CollectionUtils.unmodifiableMultiValueMap(initCookies());
/*     */     }
/* 177 */     return this.cookies;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract MultiValueMap<String, HttpCookie> initCookies();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SslInfo getSslInfo() {
/* 194 */     if (this.sslInfo == null) {
/* 195 */       this.sslInfo = initSslInfo();
/*     */     }
/* 197 */     return this.sslInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected abstract SslInfo initSslInfo();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract <T> T getNativeRequest();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getLogPrefix() {
/* 220 */     if (this.logPrefix == null) {
/* 221 */       this.logPrefix = "[" + initLogPrefix() + "] ";
/*     */     }
/* 223 */     return this.logPrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String initLogPrefix() {
/* 232 */     return getId();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/AbstractServerHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */