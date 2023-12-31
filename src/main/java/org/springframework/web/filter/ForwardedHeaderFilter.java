/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Supplier;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletRequestWrapper;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpServletResponseWrapper;
/*     */ import org.springframework.http.HttpRequest;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.server.ServletServerHttpRequest;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedCaseInsensitiveMap;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.util.UriComponents;
/*     */ import org.springframework.web.util.UriComponentsBuilder;
/*     */ import org.springframework.web.util.UrlPathHelper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ForwardedHeaderFilter
/*     */   extends OncePerRequestFilter
/*     */ {
/*  76 */   private static final Set<String> FORWARDED_HEADER_NAMES = Collections.newSetFromMap((Map<String, Boolean>)new LinkedCaseInsensitiveMap(10, Locale.ENGLISH));
/*     */   
/*     */   static {
/*  79 */     FORWARDED_HEADER_NAMES.add("Forwarded");
/*  80 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
/*  81 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
/*  82 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
/*  83 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
/*  84 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-Ssl");
/*  85 */     FORWARDED_HEADER_NAMES.add("X-Forwarded-For");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean removeOnly;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean relativeRedirects;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRemoveOnly(boolean removeOnly) {
/* 101 */     this.removeOnly = removeOnly;
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
/*     */   public void setRelativeRedirects(boolean relativeRedirects) {
/* 116 */     this.relativeRedirects = relativeRedirects;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean shouldNotFilter(HttpServletRequest request) {
/* 122 */     for (String headerName : FORWARDED_HEADER_NAMES) {
/* 123 */       if (request.getHeader(headerName) != null) {
/* 124 */         return false;
/*     */       }
/*     */     } 
/* 127 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean shouldNotFilterAsyncDispatch() {
/* 132 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean shouldNotFilterErrorDispatch() {
/* 137 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/* 144 */     if (this.removeOnly) {
/* 145 */       ForwardedHeaderRemovingRequest wrappedRequest = new ForwardedHeaderRemovingRequest(request);
/* 146 */       filterChain.doFilter((ServletRequest)wrappedRequest, (ServletResponse)response);
/*     */     } else {
/*     */       
/* 149 */       ForwardedHeaderExtractingRequest forwardedHeaderExtractingRequest = new ForwardedHeaderExtractingRequest(request);
/*     */ 
/*     */ 
/*     */       
/* 153 */       HttpServletResponse wrappedResponse = this.relativeRedirects ? RelativeRedirectResponseWrapper.wrapIfNecessary(response, HttpStatus.SEE_OTHER) : (HttpServletResponse)new ForwardedHeaderExtractingResponse(response, (HttpServletRequest)forwardedHeaderExtractingRequest);
/*     */ 
/*     */       
/* 156 */       filterChain.doFilter((ServletRequest)forwardedHeaderExtractingRequest, (ServletResponse)wrappedResponse);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/* 164 */     doFilterInternal(request, response, filterChain);
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ForwardedHeaderRemovingRequest
/*     */     extends HttpServletRequestWrapper
/*     */   {
/*     */     private final Map<String, List<String>> headers;
/*     */ 
/*     */     
/*     */     public ForwardedHeaderRemovingRequest(HttpServletRequest request) {
/* 175 */       super(request);
/* 176 */       this.headers = initHeaders(request);
/*     */     }
/*     */     
/*     */     private static Map<String, List<String>> initHeaders(HttpServletRequest request) {
/* 180 */       LinkedCaseInsensitiveMap linkedCaseInsensitiveMap = new LinkedCaseInsensitiveMap(Locale.ENGLISH);
/* 181 */       Enumeration<String> names = request.getHeaderNames();
/* 182 */       while (names.hasMoreElements()) {
/* 183 */         String name = names.nextElement();
/* 184 */         if (!ForwardedHeaderFilter.FORWARDED_HEADER_NAMES.contains(name)) {
/* 185 */           linkedCaseInsensitiveMap.put(name, Collections.list(request.getHeaders(name)));
/*     */         }
/*     */       } 
/* 188 */       return (Map<String, List<String>>)linkedCaseInsensitiveMap;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String getHeader(String name) {
/* 196 */       List<String> value = this.headers.get(name);
/* 197 */       return CollectionUtils.isEmpty(value) ? null : value.get(0);
/*     */     }
/*     */ 
/*     */     
/*     */     public Enumeration<String> getHeaders(String name) {
/* 202 */       List<String> value = this.headers.get(name);
/* 203 */       return Collections.enumeration((value != null) ? value : Collections.<String>emptySet());
/*     */     }
/*     */ 
/*     */     
/*     */     public Enumeration<String> getHeaderNames() {
/* 208 */       return Collections.enumeration(this.headers.keySet());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ForwardedHeaderExtractingRequest
/*     */     extends ForwardedHeaderRemovingRequest
/*     */   {
/*     */     @Nullable
/*     */     private final String scheme;
/*     */ 
/*     */     
/*     */     private final boolean secure;
/*     */     
/*     */     @Nullable
/*     */     private final String host;
/*     */     
/*     */     private final int port;
/*     */     
/*     */     @Nullable
/*     */     private final InetSocketAddress remoteAddress;
/*     */     
/*     */     private final ForwardedHeaderFilter.ForwardedPrefixExtractor forwardedPrefixExtractor;
/*     */ 
/*     */     
/*     */     ForwardedHeaderExtractingRequest(HttpServletRequest servletRequest) {
/* 235 */       super(servletRequest);
/*     */       
/* 237 */       ServletServerHttpRequest servletServerHttpRequest = new ServletServerHttpRequest(servletRequest);
/* 238 */       UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest((HttpRequest)servletServerHttpRequest).build();
/* 239 */       int port = uriComponents.getPort();
/*     */       
/* 241 */       this.scheme = uriComponents.getScheme();
/* 242 */       this.secure = ("https".equals(this.scheme) || "wss".equals(this.scheme));
/* 243 */       this.host = uriComponents.getHost();
/* 244 */       this.port = (port == -1) ? (this.secure ? 443 : 80) : port;
/*     */       
/* 246 */       this.remoteAddress = UriComponentsBuilder.parseForwardedFor((HttpRequest)servletServerHttpRequest, servletServerHttpRequest.getRemoteAddress());
/*     */       
/* 248 */       String baseUrl = this.scheme + "://" + this.host + ((port == -1) ? "" : (":" + port));
/* 249 */       Supplier<HttpServletRequest> delegateRequest = () -> (HttpServletRequest)getRequest();
/* 250 */       this.forwardedPrefixExtractor = new ForwardedHeaderFilter.ForwardedPrefixExtractor(delegateRequest, baseUrl);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String getScheme() {
/* 257 */       return this.scheme;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String getServerName() {
/* 263 */       return this.host;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getServerPort() {
/* 268 */       return this.port;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSecure() {
/* 273 */       return this.secure;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getContextPath() {
/* 278 */       return this.forwardedPrefixExtractor.getContextPath();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getRequestURI() {
/* 283 */       return this.forwardedPrefixExtractor.getRequestUri();
/*     */     }
/*     */ 
/*     */     
/*     */     public StringBuffer getRequestURL() {
/* 288 */       return this.forwardedPrefixExtractor.getRequestUrl();
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String getRemoteHost() {
/* 294 */       return (this.remoteAddress != null) ? this.remoteAddress.getHostString() : super.getRemoteHost();
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String getRemoteAddr() {
/* 300 */       return (this.remoteAddress != null) ? this.remoteAddress.getHostString() : super.getRemoteAddr();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getRemotePort() {
/* 305 */       return (this.remoteAddress != null) ? this.remoteAddress.getPort() : super.getRemotePort();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ForwardedPrefixExtractor
/*     */   {
/*     */     private final Supplier<HttpServletRequest> delegate;
/*     */ 
/*     */ 
/*     */     
/*     */     private final String baseUrl;
/*     */ 
/*     */ 
/*     */     
/*     */     private String actualRequestUri;
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private final String forwardedPrefix;
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private String requestUri;
/*     */ 
/*     */     
/*     */     private String requestUrl;
/*     */ 
/*     */ 
/*     */     
/*     */     public ForwardedPrefixExtractor(Supplier<HttpServletRequest> delegateRequest, String baseUrl) {
/* 340 */       this.delegate = delegateRequest;
/* 341 */       this.baseUrl = baseUrl;
/* 342 */       this.actualRequestUri = ((HttpServletRequest)delegateRequest.get()).getRequestURI();
/*     */       
/* 344 */       this.forwardedPrefix = initForwardedPrefix(delegateRequest.get());
/* 345 */       this.requestUri = initRequestUri();
/* 346 */       this.requestUrl = initRequestUrl();
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private static String initForwardedPrefix(HttpServletRequest request) {
/* 351 */       String result = null;
/* 352 */       Enumeration<String> names = request.getHeaderNames();
/* 353 */       while (names.hasMoreElements()) {
/* 354 */         String name = names.nextElement();
/* 355 */         if ("X-Forwarded-Prefix".equalsIgnoreCase(name)) {
/* 356 */           result = request.getHeader(name);
/*     */         }
/*     */       } 
/* 359 */       if (result != null) {
/* 360 */         StringBuilder prefix = new StringBuilder(result.length());
/* 361 */         String[] rawPrefixes = StringUtils.tokenizeToStringArray(result, ",");
/* 362 */         for (String rawPrefix : rawPrefixes) {
/* 363 */           int endIndex = rawPrefix.length();
/* 364 */           while (endIndex > 0 && rawPrefix.charAt(endIndex - 1) == '/') {
/* 365 */             endIndex--;
/*     */           }
/* 367 */           prefix.append((endIndex != rawPrefix.length()) ? rawPrefix.substring(0, endIndex) : rawPrefix);
/*     */         } 
/* 369 */         return prefix.toString();
/*     */       } 
/* 371 */       return null;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private String initRequestUri() {
/* 376 */       if (this.forwardedPrefix != null) {
/* 377 */         return this.forwardedPrefix + UrlPathHelper.rawPathInstance
/* 378 */           .getPathWithinApplication(this.delegate.get());
/*     */       }
/* 380 */       return null;
/*     */     }
/*     */     
/*     */     private String initRequestUrl() {
/* 384 */       return this.baseUrl + ((this.requestUri != null) ? this.requestUri : ((HttpServletRequest)this.delegate.get()).getRequestURI());
/*     */     }
/*     */ 
/*     */     
/*     */     public String getContextPath() {
/* 389 */       return (this.forwardedPrefix != null) ? this.forwardedPrefix : ((HttpServletRequest)this.delegate.get()).getContextPath();
/*     */     }
/*     */     
/*     */     public String getRequestUri() {
/* 393 */       if (this.requestUri == null) {
/* 394 */         return ((HttpServletRequest)this.delegate.get()).getRequestURI();
/*     */       }
/* 396 */       recalculatePathsIfNecessary();
/* 397 */       return this.requestUri;
/*     */     }
/*     */     
/*     */     public StringBuffer getRequestUrl() {
/* 401 */       recalculatePathsIfNecessary();
/* 402 */       return new StringBuffer(this.requestUrl);
/*     */     }
/*     */     
/*     */     private void recalculatePathsIfNecessary() {
/* 406 */       if (!this.actualRequestUri.equals(((HttpServletRequest)this.delegate.get()).getRequestURI())) {
/*     */         
/* 408 */         this.actualRequestUri = ((HttpServletRequest)this.delegate.get()).getRequestURI();
/* 409 */         this.requestUri = initRequestUri();
/* 410 */         this.requestUrl = initRequestUrl();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ForwardedHeaderExtractingResponse
/*     */     extends HttpServletResponseWrapper
/*     */   {
/*     */     private static final String FOLDER_SEPARATOR = "/";
/*     */     
/*     */     private final HttpServletRequest request;
/*     */     
/*     */     ForwardedHeaderExtractingResponse(HttpServletResponse response, HttpServletRequest request) {
/* 424 */       super(response);
/* 425 */       this.request = request;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void sendRedirect(String location) throws IOException {
/* 432 */       UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(location);
/* 433 */       UriComponents uriComponents = builder.build();
/*     */ 
/*     */       
/* 436 */       if (uriComponents.getScheme() != null) {
/* 437 */         super.sendRedirect(location);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 442 */       if (location.startsWith("//")) {
/* 443 */         String scheme = this.request.getScheme();
/* 444 */         super.sendRedirect(builder.scheme(scheme).toUriString());
/*     */         
/*     */         return;
/*     */       } 
/* 448 */       String path = uriComponents.getPath();
/* 449 */       if (path != null)
/*     */       {
/*     */         
/* 452 */         path = path.startsWith("/") ? path : StringUtils.applyRelativePath(this.request.getRequestURI(), path);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 460 */       String result = UriComponentsBuilder.fromHttpRequest((HttpRequest)new ServletServerHttpRequest(this.request)).replacePath(path).replaceQuery(uriComponents.getQuery()).fragment(uriComponents.getFragment()).build().normalize().toUriString();
/*     */       
/* 462 */       super.sendRedirect(result);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/ForwardedHeaderFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */