/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.function.Predicate;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.server.ServletServerHttpRequest;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.util.ContentCachingRequestWrapper;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractRequestLoggingFilter
/*     */   extends OncePerRequestFilter
/*     */ {
/*     */   public static final String DEFAULT_BEFORE_MESSAGE_PREFIX = "Before request [";
/*     */   public static final String DEFAULT_BEFORE_MESSAGE_SUFFIX = "]";
/*     */   public static final String DEFAULT_AFTER_MESSAGE_PREFIX = "After request [";
/*     */   public static final String DEFAULT_AFTER_MESSAGE_SUFFIX = "]";
/*     */   private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 50;
/*     */   private boolean includeQueryString = false;
/*     */   private boolean includeClientInfo = false;
/*     */   private boolean includeHeaders = false;
/*     */   private boolean includePayload = false;
/*     */   @Nullable
/*     */   private Predicate<String> headerPredicate;
/* 105 */   private int maxPayloadLength = 50;
/*     */   
/* 107 */   private String beforeMessagePrefix = "Before request [";
/*     */   
/* 109 */   private String beforeMessageSuffix = "]";
/*     */   
/* 111 */   private String afterMessagePrefix = "After request [";
/*     */   
/* 113 */   private String afterMessageSuffix = "]";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncludeQueryString(boolean includeQueryString) {
/* 122 */     this.includeQueryString = includeQueryString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isIncludeQueryString() {
/* 129 */     return this.includeQueryString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncludeClientInfo(boolean includeClientInfo) {
/* 139 */     this.includeClientInfo = includeClientInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isIncludeClientInfo() {
/* 147 */     return this.includeClientInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncludeHeaders(boolean includeHeaders) {
/* 157 */     this.includeHeaders = includeHeaders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isIncludeHeaders() {
/* 165 */     return this.includeHeaders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncludePayload(boolean includePayload) {
/* 175 */     this.includePayload = includePayload;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isIncludePayload() {
/* 183 */     return this.includePayload;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeaderPredicate(@Nullable Predicate<String> headerPredicate) {
/* 194 */     this.headerPredicate = headerPredicate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Predicate<String> getHeaderPredicate() {
/* 203 */     return this.headerPredicate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxPayloadLength(int maxPayloadLength) {
/* 212 */     Assert.isTrue((maxPayloadLength >= 0), "'maxPayloadLength' should be larger than or equal to 0");
/* 213 */     this.maxPayloadLength = maxPayloadLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getMaxPayloadLength() {
/* 221 */     return this.maxPayloadLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBeforeMessagePrefix(String beforeMessagePrefix) {
/* 229 */     this.beforeMessagePrefix = beforeMessagePrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBeforeMessageSuffix(String beforeMessageSuffix) {
/* 237 */     this.beforeMessageSuffix = beforeMessageSuffix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAfterMessagePrefix(String afterMessagePrefix) {
/* 245 */     this.afterMessagePrefix = afterMessagePrefix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAfterMessageSuffix(String afterMessageSuffix) {
/* 253 */     this.afterMessageSuffix = afterMessageSuffix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean shouldNotFilterAsyncDispatch() {
/* 264 */     return false;
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
/*     */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*     */     ContentCachingRequestWrapper contentCachingRequestWrapper;
/* 277 */     boolean isFirstRequest = !isAsyncDispatch(request);
/* 278 */     HttpServletRequest requestToUse = request;
/*     */     
/* 280 */     if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
/* 281 */       contentCachingRequestWrapper = new ContentCachingRequestWrapper(request, getMaxPayloadLength());
/*     */     }
/*     */     
/* 284 */     boolean shouldLog = shouldLog((HttpServletRequest)contentCachingRequestWrapper);
/* 285 */     if (shouldLog && isFirstRequest) {
/* 286 */       beforeRequest((HttpServletRequest)contentCachingRequestWrapper, getBeforeMessage((HttpServletRequest)contentCachingRequestWrapper));
/*     */     }
/*     */     try {
/* 289 */       filterChain.doFilter((ServletRequest)contentCachingRequestWrapper, (ServletResponse)response);
/*     */     } finally {
/*     */       
/* 292 */       if (shouldLog && !isAsyncStarted((HttpServletRequest)contentCachingRequestWrapper)) {
/* 293 */         afterRequest((HttpServletRequest)contentCachingRequestWrapper, getAfterMessage((HttpServletRequest)contentCachingRequestWrapper));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getBeforeMessage(HttpServletRequest request) {
/* 303 */     return createMessage(request, this.beforeMessagePrefix, this.beforeMessageSuffix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getAfterMessage(HttpServletRequest request) {
/* 311 */     return createMessage(request, this.afterMessagePrefix, this.afterMessageSuffix);
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
/*     */   protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
/* 323 */     StringBuilder msg = new StringBuilder();
/* 324 */     msg.append(prefix);
/* 325 */     msg.append(request.getMethod()).append(' ');
/* 326 */     msg.append(request.getRequestURI());
/*     */     
/* 328 */     if (isIncludeQueryString()) {
/* 329 */       String queryString = request.getQueryString();
/* 330 */       if (queryString != null) {
/* 331 */         msg.append('?').append(queryString);
/*     */       }
/*     */     } 
/*     */     
/* 335 */     if (isIncludeClientInfo()) {
/* 336 */       String client = request.getRemoteAddr();
/* 337 */       if (StringUtils.hasLength(client)) {
/* 338 */         msg.append(", client=").append(client);
/*     */       }
/* 340 */       HttpSession session = request.getSession(false);
/* 341 */       if (session != null) {
/* 342 */         msg.append(", session=").append(session.getId());
/*     */       }
/* 344 */       String user = request.getRemoteUser();
/* 345 */       if (user != null) {
/* 346 */         msg.append(", user=").append(user);
/*     */       }
/*     */     } 
/*     */     
/* 350 */     if (isIncludeHeaders()) {
/* 351 */       HttpHeaders headers = (new ServletServerHttpRequest(request)).getHeaders();
/* 352 */       if (getHeaderPredicate() != null) {
/* 353 */         Enumeration<String> names = request.getHeaderNames();
/* 354 */         while (names.hasMoreElements()) {
/* 355 */           String header = names.nextElement();
/* 356 */           if (!getHeaderPredicate().test(header)) {
/* 357 */             headers.set(header, "masked");
/*     */           }
/*     */         } 
/*     */       } 
/* 361 */       msg.append(", headers=").append(headers);
/*     */     } 
/*     */     
/* 364 */     if (isIncludePayload()) {
/* 365 */       String payload = getMessagePayload(request);
/* 366 */       if (payload != null) {
/* 367 */         msg.append(", payload=").append(payload);
/*     */       }
/*     */     } 
/*     */     
/* 371 */     msg.append(suffix);
/* 372 */     return msg.toString();
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
/*     */   protected String getMessagePayload(HttpServletRequest request) {
/* 384 */     ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper)WebUtils.getNativeRequest((ServletRequest)request, ContentCachingRequestWrapper.class);
/* 385 */     if (wrapper != null) {
/* 386 */       byte[] buf = wrapper.getContentAsByteArray();
/* 387 */       if (buf.length > 0) {
/* 388 */         int length = Math.min(buf.length, getMaxPayloadLength());
/*     */         try {
/* 390 */           return new String(buf, 0, length, wrapper.getCharacterEncoding());
/*     */         }
/* 392 */         catch (UnsupportedEncodingException ex) {
/* 393 */           return "[unknown]";
/*     */         } 
/*     */       } 
/*     */     } 
/* 397 */     return null;
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
/*     */   protected boolean shouldLog(HttpServletRequest request) {
/* 413 */     return true;
/*     */   }
/*     */   
/*     */   protected abstract void beforeRequest(HttpServletRequest paramHttpServletRequest, String paramString);
/*     */   
/*     */   protected abstract void afterRequest(HttpServletRequest paramHttpServletRequest, String paramString);
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/AbstractRequestLoggingFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */