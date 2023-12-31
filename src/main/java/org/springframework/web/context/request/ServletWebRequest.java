/*     */ package org.springframework.web.context.request;
/*     */ 
/*     */ import java.security.Principal;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.util.WebUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletWebRequest
/*     */   extends ServletRequestAttributes
/*     */   implements NativeWebRequest
/*     */ {
/*  55 */   private static final List<String> SAFE_METHODS = Arrays.asList(new String[] { "GET", "HEAD" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   private static final Pattern ETAG_HEADER_VALUE_PATTERN = Pattern.compile("\\*|\\s*((W\\/)?(\"[^\"]*\"))\\s*,?");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   private static final String[] DATE_FORMATS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM dd HH:mm:ss yyyy" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean notModified = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletWebRequest(HttpServletRequest request) {
/*  83 */     super(request);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletWebRequest(HttpServletRequest request, @Nullable HttpServletResponse response) {
/*  92 */     super(request, response);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getNativeRequest() {
/*  98 */     return getRequest();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getNativeResponse() {
/* 103 */     return getResponse();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getNativeRequest(@Nullable Class<T> requiredType) {
/* 108 */     return (T)WebUtils.getNativeRequest((ServletRequest)getRequest(), requiredType);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getNativeResponse(@Nullable Class<T> requiredType) {
/* 113 */     HttpServletResponse response = getResponse();
/* 114 */     return (response != null) ? (T)WebUtils.getNativeResponse((ServletResponse)response, requiredType) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public HttpMethod getHttpMethod() {
/* 123 */     return HttpMethod.resolve(getRequest().getMethod());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getHeader(String headerName) {
/* 129 */     return getRequest().getHeader(headerName);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String[] getHeaderValues(String headerName) {
/* 135 */     String[] headerValues = StringUtils.toStringArray(getRequest().getHeaders(headerName));
/* 136 */     return !ObjectUtils.isEmpty((Object[])headerValues) ? headerValues : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> getHeaderNames() {
/* 141 */     return CollectionUtils.toIterator(getRequest().getHeaderNames());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getParameter(String paramName) {
/* 147 */     return getRequest().getParameter(paramName);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String[] getParameterValues(String paramName) {
/* 153 */     return getRequest().getParameterValues(paramName);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> getParameterNames() {
/* 158 */     return CollectionUtils.toIterator(getRequest().getParameterNames());
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String[]> getParameterMap() {
/* 163 */     return getRequest().getParameterMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public Locale getLocale() {
/* 168 */     return getRequest().getLocale();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContextPath() {
/* 173 */     return getRequest().getContextPath();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getRemoteUser() {
/* 179 */     return getRequest().getRemoteUser();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Principal getUserPrincipal() {
/* 185 */     return getRequest().getUserPrincipal();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUserInRole(String role) {
/* 190 */     return getRequest().isUserInRole(role);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 195 */     return getRequest().isSecure();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean checkNotModified(long lastModifiedTimestamp) {
/* 201 */     return checkNotModified((String)null, lastModifiedTimestamp);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkNotModified(String etag) {
/* 206 */     return checkNotModified(etag, -1L);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkNotModified(@Nullable String etag, long lastModifiedTimestamp) {
/* 211 */     HttpServletResponse response = getResponse();
/* 212 */     if (this.notModified || (response != null && HttpStatus.OK.value() != response.getStatus())) {
/* 213 */       return this.notModified;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 219 */     if (validateIfUnmodifiedSince(lastModifiedTimestamp)) {
/* 220 */       if (this.notModified && response != null) {
/* 221 */         response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
/*     */       }
/* 223 */       return this.notModified;
/*     */     } 
/*     */     
/* 226 */     boolean validated = validateIfNoneMatch(etag);
/* 227 */     if (!validated) {
/* 228 */       validateIfModifiedSince(lastModifiedTimestamp);
/*     */     }
/*     */ 
/*     */     
/* 232 */     if (response != null) {
/* 233 */       boolean isHttpGetOrHead = SAFE_METHODS.contains(getRequest().getMethod());
/* 234 */       if (this.notModified) {
/* 235 */         response.setStatus(isHttpGetOrHead ? HttpStatus.NOT_MODIFIED
/* 236 */             .value() : HttpStatus.PRECONDITION_FAILED.value());
/*     */       }
/* 238 */       if (isHttpGetOrHead) {
/* 239 */         if (lastModifiedTimestamp > 0L && parseDateValue(response.getHeader("Last-Modified")) == -1L) {
/* 240 */           response.setDateHeader("Last-Modified", lastModifiedTimestamp);
/*     */         }
/* 242 */         if (StringUtils.hasLength(etag) && response.getHeader("ETag") == null) {
/* 243 */           response.setHeader("ETag", padEtagIfNecessary(etag));
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 248 */     return this.notModified;
/*     */   }
/*     */   
/*     */   private boolean validateIfUnmodifiedSince(long lastModifiedTimestamp) {
/* 252 */     if (lastModifiedTimestamp < 0L) {
/* 253 */       return false;
/*     */     }
/* 255 */     long ifUnmodifiedSince = parseDateHeader("If-Unmodified-Since");
/* 256 */     if (ifUnmodifiedSince == -1L) {
/* 257 */       return false;
/*     */     }
/*     */     
/* 260 */     this.notModified = (ifUnmodifiedSince < lastModifiedTimestamp / 1000L * 1000L);
/* 261 */     return true;
/*     */   }
/*     */   private boolean validateIfNoneMatch(@Nullable String etag) {
/*     */     Enumeration<String> ifNoneMatch;
/* 265 */     if (!StringUtils.hasLength(etag)) {
/* 266 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 271 */       ifNoneMatch = getRequest().getHeaders("If-None-Match");
/*     */     }
/* 273 */     catch (IllegalArgumentException ex) {
/* 274 */       return false;
/*     */     } 
/* 276 */     if (!ifNoneMatch.hasMoreElements()) {
/* 277 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 281 */     etag = padEtagIfNecessary(etag);
/* 282 */     if (etag.startsWith("W/")) {
/* 283 */       etag = etag.substring(2);
/*     */     }
/* 285 */     while (ifNoneMatch.hasMoreElements()) {
/* 286 */       String clientETags = ifNoneMatch.nextElement();
/* 287 */       Matcher etagMatcher = ETAG_HEADER_VALUE_PATTERN.matcher(clientETags);
/*     */       
/* 289 */       while (etagMatcher.find()) {
/* 290 */         if (StringUtils.hasLength(etagMatcher.group()) && etag.equals(etagMatcher.group(3))) {
/* 291 */           this.notModified = true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 297 */     return true;
/*     */   }
/*     */   
/*     */   private String padEtagIfNecessary(String etag) {
/* 301 */     if (!StringUtils.hasLength(etag)) {
/* 302 */       return etag;
/*     */     }
/* 304 */     if ((etag.startsWith("\"") || etag.startsWith("W/\"")) && etag.endsWith("\"")) {
/* 305 */       return etag;
/*     */     }
/* 307 */     return "\"" + etag + "\"";
/*     */   }
/*     */   
/*     */   private boolean validateIfModifiedSince(long lastModifiedTimestamp) {
/* 311 */     if (lastModifiedTimestamp < 0L) {
/* 312 */       return false;
/*     */     }
/* 314 */     long ifModifiedSince = parseDateHeader("If-Modified-Since");
/* 315 */     if (ifModifiedSince == -1L) {
/* 316 */       return false;
/*     */     }
/*     */     
/* 319 */     this.notModified = (ifModifiedSince >= lastModifiedTimestamp / 1000L * 1000L);
/* 320 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isNotModified() {
/* 324 */     return this.notModified;
/*     */   }
/*     */   
/*     */   private long parseDateHeader(String headerName) {
/* 328 */     long dateValue = -1L;
/*     */     try {
/* 330 */       dateValue = getRequest().getDateHeader(headerName);
/*     */     }
/* 332 */     catch (IllegalArgumentException ex) {
/* 333 */       String headerValue = getHeader(headerName);
/*     */       
/* 335 */       if (headerValue != null) {
/* 336 */         int separatorIndex = headerValue.indexOf(';');
/* 337 */         if (separatorIndex != -1) {
/* 338 */           String datePart = headerValue.substring(0, separatorIndex);
/* 339 */           dateValue = parseDateValue(datePart);
/*     */         } 
/*     */       } 
/*     */     } 
/* 343 */     return dateValue;
/*     */   }
/*     */   
/*     */   private long parseDateValue(@Nullable String headerValue) {
/* 347 */     if (headerValue == null)
/*     */     {
/* 349 */       return -1L;
/*     */     }
/* 351 */     if (headerValue.length() >= 3)
/*     */     {
/*     */       
/* 354 */       for (String dateFormat : DATE_FORMATS) {
/* 355 */         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
/* 356 */         simpleDateFormat.setTimeZone(GMT);
/*     */         try {
/* 358 */           return simpleDateFormat.parse(headerValue).getTime();
/*     */         }
/* 360 */         catch (ParseException parseException) {}
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 365 */     return -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription(boolean includeClientInfo) {
/* 370 */     HttpServletRequest request = getRequest();
/* 371 */     StringBuilder sb = new StringBuilder();
/* 372 */     sb.append("uri=").append(request.getRequestURI());
/* 373 */     if (includeClientInfo) {
/* 374 */       String client = request.getRemoteAddr();
/* 375 */       if (StringUtils.hasLength(client)) {
/* 376 */         sb.append(";client=").append(client);
/*     */       }
/* 378 */       HttpSession session = request.getSession(false);
/* 379 */       if (session != null) {
/* 380 */         sb.append(";session=").append(session.getId());
/*     */       }
/* 382 */       String user = request.getRemoteUser();
/* 383 */       if (StringUtils.hasLength(user)) {
/* 384 */         sb.append(";user=").append(user);
/*     */       }
/*     */     } 
/* 387 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 393 */     return "ServletWebRequest: " + getDescription(true);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/ServletWebRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */