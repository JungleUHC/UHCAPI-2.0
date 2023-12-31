/*     */ package org.springframework.http.server;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URLEncoder;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.Principal;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.http.InvalidMediaTypeException;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.LinkedCaseInsensitiveMap;
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
/*     */ public class ServletServerHttpRequest
/*     */   implements ServerHttpRequest
/*     */ {
/*     */   protected static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
/*  61 */   protected static final Charset FORM_CHARSET = StandardCharsets.UTF_8;
/*     */ 
/*     */ 
/*     */   
/*     */   private final HttpServletRequest servletRequest;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private URI uri;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private HttpHeaders headers;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ServerHttpAsyncRequestControl asyncRequestControl;
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletServerHttpRequest(HttpServletRequest servletRequest) {
/*  82 */     Assert.notNull(servletRequest, "HttpServletRequest must not be null");
/*  83 */     this.servletRequest = servletRequest;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServletRequest getServletRequest() {
/*  91 */     return this.servletRequest;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public HttpMethod getMethod() {
/*  97 */     return HttpMethod.resolve(this.servletRequest.getMethod());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMethodValue() {
/* 102 */     return this.servletRequest.getMethod();
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/* 107 */     if (this.uri == null) {
/* 108 */       String urlString = null;
/* 109 */       boolean hasQuery = false;
/*     */       try {
/* 111 */         StringBuffer url = this.servletRequest.getRequestURL();
/* 112 */         String query = this.servletRequest.getQueryString();
/* 113 */         hasQuery = StringUtils.hasText(query);
/* 114 */         if (hasQuery) {
/* 115 */           url.append('?').append(query);
/*     */         }
/* 117 */         urlString = url.toString();
/* 118 */         this.uri = new URI(urlString);
/*     */       }
/* 120 */       catch (URISyntaxException ex) {
/* 121 */         if (!hasQuery) {
/* 122 */           throw new IllegalStateException("Could not resolve HttpServletRequest as URI: " + urlString, ex);
/*     */         }
/*     */ 
/*     */         
/*     */         try {
/* 127 */           urlString = this.servletRequest.getRequestURL().toString();
/* 128 */           this.uri = new URI(urlString);
/*     */         }
/* 130 */         catch (URISyntaxException ex2) {
/* 131 */           throw new IllegalStateException("Could not resolve HttpServletRequest as URI: " + urlString, ex2);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 136 */     return this.uri;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/* 141 */     if (this.headers == null) {
/* 142 */       this.headers = new HttpHeaders();
/*     */       
/* 144 */       for (Enumeration<?> names = this.servletRequest.getHeaderNames(); names.hasMoreElements(); ) {
/* 145 */         String headerName = (String)names.nextElement();
/* 146 */         Enumeration<?> headerValues = this.servletRequest.getHeaders(headerName);
/* 147 */         while (headerValues.hasMoreElements()) {
/* 148 */           String headerValue = (String)headerValues.nextElement();
/* 149 */           this.headers.add(headerName, headerValue);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 156 */         MediaType contentType = this.headers.getContentType();
/* 157 */         if (contentType == null) {
/* 158 */           String requestContentType = this.servletRequest.getContentType();
/* 159 */           if (StringUtils.hasLength(requestContentType)) {
/* 160 */             contentType = MediaType.parseMediaType(requestContentType);
/* 161 */             if (contentType.isConcrete()) {
/* 162 */               this.headers.setContentType(contentType);
/*     */             }
/*     */           } 
/*     */         } 
/* 166 */         if (contentType != null && contentType.getCharset() == null) {
/* 167 */           String requestEncoding = this.servletRequest.getCharacterEncoding();
/* 168 */           if (StringUtils.hasLength(requestEncoding)) {
/* 169 */             Charset charSet = Charset.forName(requestEncoding);
/* 170 */             LinkedCaseInsensitiveMap<String, String> linkedCaseInsensitiveMap = new LinkedCaseInsensitiveMap();
/* 171 */             linkedCaseInsensitiveMap.putAll(contentType.getParameters());
/* 172 */             linkedCaseInsensitiveMap.put("charset", charSet.toString());
/* 173 */             MediaType mediaType = new MediaType(contentType.getType(), contentType.getSubtype(), (Map)linkedCaseInsensitiveMap);
/* 174 */             this.headers.setContentType(mediaType);
/*     */           }
/*     */         
/*     */         } 
/* 178 */       } catch (InvalidMediaTypeException invalidMediaTypeException) {}
/*     */ 
/*     */ 
/*     */       
/* 182 */       if (this.headers.getContentLength() < 0L) {
/* 183 */         int requestContentLength = this.servletRequest.getContentLength();
/* 184 */         if (requestContentLength != -1) {
/* 185 */           this.headers.setContentLength(requestContentLength);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 190 */     return this.headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getPrincipal() {
/* 195 */     return this.servletRequest.getUserPrincipal();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress getLocalAddress() {
/* 200 */     return new InetSocketAddress(this.servletRequest.getLocalName(), this.servletRequest.getLocalPort());
/*     */   }
/*     */ 
/*     */   
/*     */   public InetSocketAddress getRemoteAddress() {
/* 205 */     return new InetSocketAddress(this.servletRequest.getRemoteHost(), this.servletRequest.getRemotePort());
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getBody() throws IOException {
/* 210 */     if (isFormPost(this.servletRequest)) {
/* 211 */       return getBodyFromServletRequestParameters(this.servletRequest);
/*     */     }
/*     */     
/* 214 */     return (InputStream)this.servletRequest.getInputStream();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerHttpAsyncRequestControl getAsyncRequestControl(ServerHttpResponse response) {
/* 220 */     if (this.asyncRequestControl == null) {
/* 221 */       if (!(response instanceof ServletServerHttpResponse)) {
/* 222 */         throw new IllegalArgumentException("Response must be a ServletServerHttpResponse: " + response
/* 223 */             .getClass());
/*     */       }
/* 225 */       ServletServerHttpResponse servletServerResponse = (ServletServerHttpResponse)response;
/* 226 */       this.asyncRequestControl = new ServletServerHttpAsyncRequestControl(this, servletServerResponse);
/*     */     } 
/* 228 */     return this.asyncRequestControl;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isFormPost(HttpServletRequest request) {
/* 233 */     String contentType = request.getContentType();
/* 234 */     return (contentType != null && contentType.contains("application/x-www-form-urlencoded") && HttpMethod.POST
/* 235 */       .matches(request.getMethod()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static InputStream getBodyFromServletRequestParameters(HttpServletRequest request) throws IOException {
/* 245 */     ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
/* 246 */     Writer writer = new OutputStreamWriter(bos, FORM_CHARSET);
/*     */     
/* 248 */     Map<String, String[]> form = request.getParameterMap();
/* 249 */     for (Iterator<Map.Entry<String, String[]>> entryIterator = form.entrySet().iterator(); entryIterator.hasNext(); ) {
/* 250 */       Map.Entry<String, String[]> entry = entryIterator.next();
/* 251 */       String name = entry.getKey();
/* 252 */       List<String> values = Arrays.asList((Object[])entry.getValue());
/* 253 */       for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
/* 254 */         String value = valueIterator.next();
/* 255 */         writer.write(URLEncoder.encode(name, FORM_CHARSET.name()));
/* 256 */         if (value != null) {
/* 257 */           writer.write(61);
/* 258 */           writer.write(URLEncoder.encode(value, FORM_CHARSET.name()));
/* 259 */           if (valueIterator.hasNext()) {
/* 260 */             writer.write(38);
/*     */           }
/*     */         } 
/*     */       } 
/* 264 */       if (entryIterator.hasNext()) {
/* 265 */         writer.append('&');
/*     */       }
/*     */     } 
/* 268 */     writer.flush();
/*     */     
/* 270 */     return new ByteArrayInputStream(bos.toByteArray());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/ServletServerHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */