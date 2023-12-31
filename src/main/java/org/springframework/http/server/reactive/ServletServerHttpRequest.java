/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import javax.servlet.AsyncContext;
/*     */ import javax.servlet.AsyncEvent;
/*     */ import javax.servlet.AsyncListener;
/*     */ import javax.servlet.ReadListener;
/*     */ import javax.servlet.ServletInputStream;
/*     */ import javax.servlet.http.Cookie;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DefaultDataBufferFactory;
/*     */ import org.springframework.http.HttpCookie;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.NonNull;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedCaseInsensitiveMap;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.StringUtils;
/*     */ import reactor.core.publisher.Flux;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ServletServerHttpRequest
/*     */   extends AbstractServerHttpRequest
/*     */ {
/*  63 */   static final DataBuffer EOF_BUFFER = (DataBuffer)DefaultDataBufferFactory.sharedInstance.allocateBuffer(0);
/*     */ 
/*     */   
/*     */   private final HttpServletRequest request;
/*     */   
/*     */   private final RequestBodyPublisher bodyPublisher;
/*     */   
/*  70 */   private final Object cookieLock = new Object();
/*     */ 
/*     */   
/*     */   private final DataBufferFactory bufferFactory;
/*     */ 
/*     */   
/*     */   private final byte[] buffer;
/*     */ 
/*     */   
/*     */   private final AsyncListener asyncListener;
/*     */ 
/*     */   
/*     */   public ServletServerHttpRequest(HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
/*  83 */     this(createDefaultHttpHeaders(request), request, asyncContext, servletPath, bufferFactory, bufferSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletServerHttpRequest(MultiValueMap<String, String> headers, HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
/*  90 */     super(initUri(request), request.getContextPath() + servletPath, initHeaders(headers, request));
/*     */     
/*  92 */     Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
/*  93 */     Assert.isTrue((bufferSize > 0), "'bufferSize' must be higher than 0");
/*     */     
/*  95 */     this.request = request;
/*  96 */     this.bufferFactory = bufferFactory;
/*  97 */     this.buffer = new byte[bufferSize];
/*     */     
/*  99 */     this.asyncListener = new RequestAsyncListener();
/*     */ 
/*     */     
/* 102 */     ServletInputStream inputStream = request.getInputStream();
/* 103 */     this.bodyPublisher = new RequestBodyPublisher(inputStream);
/* 104 */     this.bodyPublisher.registerReadListener();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static MultiValueMap<String, String> createDefaultHttpHeaders(HttpServletRequest request) {
/* 110 */     MultiValueMap<String, String> headers = CollectionUtils.toMultiValueMap((Map)new LinkedCaseInsensitiveMap(8, Locale.ENGLISH));
/* 111 */     for (Enumeration<?> names = request.getHeaderNames(); names.hasMoreElements(); ) {
/* 112 */       String name = (String)names.nextElement();
/* 113 */       for (Enumeration<?> values = request.getHeaders(name); values.hasMoreElements();) {
/* 114 */         headers.add(name, values.nextElement());
/*     */       }
/*     */     } 
/* 117 */     return headers;
/*     */   }
/*     */   
/*     */   private static URI initUri(HttpServletRequest request) throws URISyntaxException {
/* 121 */     Assert.notNull(request, "'request' must not be null");
/* 122 */     StringBuffer url = request.getRequestURL();
/* 123 */     String query = request.getQueryString();
/* 124 */     if (StringUtils.hasText(query)) {
/* 125 */       url.append('?').append(query);
/*     */     }
/* 127 */     return new URI(url.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static MultiValueMap<String, String> initHeaders(MultiValueMap<String, String> headerValues, HttpServletRequest request) {
/* 133 */     HttpHeaders headers = null;
/* 134 */     MediaType contentType = null;
/* 135 */     if (!StringUtils.hasLength((String)headerValues.getFirst("Content-Type"))) {
/* 136 */       String requestContentType = request.getContentType();
/* 137 */       if (StringUtils.hasLength(requestContentType)) {
/* 138 */         contentType = MediaType.parseMediaType(requestContentType);
/* 139 */         headers = new HttpHeaders(headerValues);
/* 140 */         headers.setContentType(contentType);
/*     */       } 
/*     */     } 
/* 143 */     if (contentType != null && contentType.getCharset() == null) {
/* 144 */       String encoding = request.getCharacterEncoding();
/* 145 */       if (StringUtils.hasLength(encoding)) {
/* 146 */         LinkedCaseInsensitiveMap<String, String> linkedCaseInsensitiveMap = new LinkedCaseInsensitiveMap();
/* 147 */         linkedCaseInsensitiveMap.putAll(contentType.getParameters());
/* 148 */         linkedCaseInsensitiveMap.put("charset", Charset.forName(encoding).toString());
/* 149 */         headers.setContentType(new MediaType(contentType, (Map)linkedCaseInsensitiveMap));
/*     */       } 
/*     */     } 
/* 152 */     if (headerValues.getFirst("Content-Type") == null) {
/* 153 */       int contentLength = request.getContentLength();
/* 154 */       if (contentLength != -1) {
/* 155 */         headers = (headers != null) ? headers : new HttpHeaders(headerValues);
/* 156 */         headers.setContentLength(contentLength);
/*     */       } 
/*     */     } 
/* 159 */     return (headers != null) ? (MultiValueMap<String, String>)headers : headerValues;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMethodValue() {
/* 165 */     return this.request.getMethod();
/*     */   }
/*     */   
/*     */   protected MultiValueMap<String, HttpCookie> initCookies() {
/*     */     Cookie[] cookies;
/* 170 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/*     */     
/* 172 */     synchronized (this.cookieLock) {
/* 173 */       cookies = this.request.getCookies();
/*     */     } 
/* 175 */     if (cookies != null) {
/* 176 */       for (Cookie cookie : cookies) {
/* 177 */         String name = cookie.getName();
/* 178 */         HttpCookie httpCookie = new HttpCookie(name, cookie.getValue());
/* 179 */         linkedMultiValueMap.add(name, httpCookie);
/*     */       } 
/*     */     }
/* 182 */     return (MultiValueMap<String, HttpCookie>)linkedMultiValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   @NonNull
/*     */   public InetSocketAddress getLocalAddress() {
/* 188 */     return new InetSocketAddress(this.request.getLocalAddr(), this.request.getLocalPort());
/*     */   }
/*     */ 
/*     */   
/*     */   @NonNull
/*     */   public InetSocketAddress getRemoteAddress() {
/* 194 */     return new InetSocketAddress(this.request.getRemoteHost(), this.request.getRemotePort());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected SslInfo initSslInfo() {
/* 200 */     X509Certificate[] certificates = getX509Certificates();
/* 201 */     return (certificates != null) ? new DefaultSslInfo(getSslSessionId(), certificates) : null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private String getSslSessionId() {
/* 206 */     return (String)this.request.getAttribute("javax.servlet.request.ssl_session_id");
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private X509Certificate[] getX509Certificates() {
/* 211 */     String name = "javax.servlet.request.X509Certificate";
/* 212 */     return (X509Certificate[])this.request.getAttribute(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Flux<DataBuffer> getBody() {
/* 217 */     return Flux.from(this.bodyPublisher);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeRequest() {
/* 223 */     return (T)this.request;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AsyncListener getAsyncListener() {
/* 233 */     return this.asyncListener;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   DataBuffer readFromInputStream() throws IOException {
/* 244 */     int read = this.request.getInputStream().read(this.buffer);
/* 245 */     logBytesRead(read);
/*     */     
/* 247 */     if (read > 0) {
/* 248 */       DataBuffer dataBuffer = this.bufferFactory.allocateBuffer(read);
/* 249 */       dataBuffer.write(this.buffer, 0, read);
/* 250 */       return dataBuffer;
/*     */     } 
/*     */     
/* 253 */     if (read == -1) {
/* 254 */       return EOF_BUFFER;
/*     */     }
/*     */     
/* 257 */     return null;
/*     */   }
/*     */   
/*     */   protected final void logBytesRead(int read) {
/* 261 */     Log rsReadLogger = AbstractListenerReadPublisher.rsReadLogger;
/* 262 */     if (rsReadLogger.isTraceEnabled()) {
/* 263 */       rsReadLogger.trace(getLogPrefix() + "Read " + read + ((read != -1) ? " bytes" : ""));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private final class RequestAsyncListener
/*     */     implements AsyncListener
/*     */   {
/*     */     private RequestAsyncListener() {}
/*     */     
/*     */     public void onStartAsync(AsyncEvent event) {}
/*     */     
/*     */     public void onTimeout(AsyncEvent event) {
/* 276 */       Throwable ex = event.getThrowable();
/* 277 */       ex = (ex != null) ? ex : new IllegalStateException("Async operation timeout.");
/* 278 */       ServletServerHttpRequest.this.bodyPublisher.onError(ex);
/*     */     }
/*     */ 
/*     */     
/*     */     public void onError(AsyncEvent event) {
/* 283 */       ServletServerHttpRequest.this.bodyPublisher.onError(event.getThrowable());
/*     */     }
/*     */ 
/*     */     
/*     */     public void onComplete(AsyncEvent event) {
/* 288 */       ServletServerHttpRequest.this.bodyPublisher.onAllDataRead();
/*     */     }
/*     */   }
/*     */   
/*     */   private class RequestBodyPublisher
/*     */     extends AbstractListenerReadPublisher<DataBuffer>
/*     */   {
/*     */     private final ServletInputStream inputStream;
/*     */     
/*     */     public RequestBodyPublisher(ServletInputStream inputStream) {
/* 298 */       super(ServletServerHttpRequest.this.getLogPrefix());
/* 299 */       this.inputStream = inputStream;
/*     */     }
/*     */     
/*     */     public void registerReadListener() throws IOException {
/* 303 */       this.inputStream.setReadListener(new RequestBodyPublisherReadListener());
/*     */     }
/*     */ 
/*     */     
/*     */     protected void checkOnDataAvailable() {
/* 308 */       if (this.inputStream.isReady() && !this.inputStream.isFinished()) {
/* 309 */         onDataAvailable();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     protected DataBuffer read() throws IOException {
/* 316 */       if (this.inputStream.isReady()) {
/* 317 */         DataBuffer dataBuffer = ServletServerHttpRequest.this.readFromInputStream();
/* 318 */         if (dataBuffer == ServletServerHttpRequest.EOF_BUFFER) {
/*     */           
/* 320 */           onAllDataRead();
/* 321 */           dataBuffer = null;
/*     */         } 
/* 323 */         return dataBuffer;
/*     */       } 
/* 325 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void readingPaused() {}
/*     */ 
/*     */     
/*     */     protected void discardData() {}
/*     */ 
/*     */     
/*     */     private class RequestBodyPublisherReadListener
/*     */       implements ReadListener
/*     */     {
/*     */       private RequestBodyPublisherReadListener() {}
/*     */ 
/*     */       
/*     */       public void onDataAvailable() throws IOException {
/* 343 */         ServletServerHttpRequest.RequestBodyPublisher.this.onDataAvailable();
/*     */       }
/*     */ 
/*     */       
/*     */       public void onAllDataRead() throws IOException {
/* 348 */         ServletServerHttpRequest.RequestBodyPublisher.this.onAllDataRead();
/*     */       }
/*     */ 
/*     */       
/*     */       public void onError(Throwable throwable) {
/* 353 */         ServletServerHttpRequest.RequestBodyPublisher.this.onError(throwable);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/ServletServerHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */