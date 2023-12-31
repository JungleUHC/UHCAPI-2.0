/*     */ package org.springframework.http.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
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
/*     */ public class ServletServerHttpResponse
/*     */   implements ServerHttpResponse
/*     */ {
/*     */   private final HttpServletResponse servletResponse;
/*     */   private final HttpHeaders headers;
/*     */   private boolean headersWritten = false;
/*     */   private boolean bodyUsed = false;
/*     */   @Nullable
/*     */   private HttpHeaders readOnlyHeaders;
/*     */   
/*     */   public ServletServerHttpResponse(HttpServletResponse servletResponse) {
/*  60 */     Assert.notNull(servletResponse, "HttpServletResponse must not be null");
/*  61 */     this.servletResponse = servletResponse;
/*  62 */     this.headers = new ServletResponseHttpHeaders();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServletResponse getServletResponse() {
/*  70 */     return this.servletResponse;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatusCode(HttpStatus status) {
/*  75 */     Assert.notNull(status, "HttpStatus must not be null");
/*  76 */     this.servletResponse.setStatus(status.value());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/*  81 */     if (this.readOnlyHeaders != null) {
/*  82 */       return this.readOnlyHeaders;
/*     */     }
/*  84 */     if (this.headersWritten) {
/*  85 */       this.readOnlyHeaders = HttpHeaders.readOnlyHttpHeaders(this.headers);
/*  86 */       return this.readOnlyHeaders;
/*     */     } 
/*     */     
/*  89 */     return this.headers;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getBody() throws IOException {
/*  95 */     this.bodyUsed = true;
/*  96 */     writeHeaders();
/*  97 */     return (OutputStream)this.servletResponse.getOutputStream();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 102 */     writeHeaders();
/* 103 */     if (this.bodyUsed) {
/* 104 */       this.servletResponse.flushBuffer();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 110 */     writeHeaders();
/*     */   }
/*     */   
/*     */   private void writeHeaders() {
/* 114 */     if (!this.headersWritten) {
/* 115 */       getHeaders().forEach((headerName, headerValues) -> {
/*     */             for (String headerValue : headerValues) {
/*     */               this.servletResponse.addHeader(headerName, headerValue);
/*     */             }
/*     */           });
/*     */       
/* 121 */       if (this.servletResponse.getContentType() == null && this.headers.getContentType() != null) {
/* 122 */         this.servletResponse.setContentType(this.headers.getContentType().toString());
/*     */       }
/* 124 */       if (this.servletResponse.getCharacterEncoding() == null && this.headers.getContentType() != null && this.headers
/* 125 */         .getContentType().getCharset() != null) {
/* 126 */         this.servletResponse.setCharacterEncoding(this.headers.getContentType().getCharset().name());
/*     */       }
/* 128 */       long contentLength = getHeaders().getContentLength();
/* 129 */       if (contentLength != -1L) {
/* 130 */         this.servletResponse.setContentLengthLong(contentLength);
/*     */       }
/* 132 */       this.headersWritten = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class ServletResponseHttpHeaders
/*     */     extends HttpHeaders
/*     */   {
/*     */     private static final long serialVersionUID = 3410708522401046302L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private ServletResponseHttpHeaders() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean containsKey(Object key) {
/* 154 */       return (super.containsKey(key) || get(key) != null);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String getFirst(String headerName) {
/* 160 */       if (headerName.equalsIgnoreCase("Content-Type")) {
/*     */         
/* 162 */         String str = super.getFirst(headerName);
/* 163 */         return (str != null) ? str : ServletServerHttpResponse.this.servletResponse.getHeader(headerName);
/*     */       } 
/*     */       
/* 166 */       String value = ServletServerHttpResponse.this.servletResponse.getHeader(headerName);
/* 167 */       return (value != null) ? value : super.getFirst(headerName);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public List<String> get(Object key) {
/* 173 */       Assert.isInstanceOf(String.class, key, "Key must be a String-based header name");
/*     */       
/* 175 */       String headerName = (String)key;
/* 176 */       if (headerName.equalsIgnoreCase("Content-Type"))
/*     */       {
/* 178 */         return Collections.singletonList(getFirst(headerName));
/*     */       }
/*     */       
/* 181 */       Collection<String> values1 = ServletServerHttpResponse.this.servletResponse.getHeaders(headerName);
/* 182 */       if (ServletServerHttpResponse.this.headersWritten) {
/* 183 */         return new ArrayList<>(values1);
/*     */       }
/* 185 */       boolean isEmpty1 = CollectionUtils.isEmpty(values1);
/*     */       
/* 187 */       List<String> values2 = super.get(key);
/* 188 */       boolean isEmpty2 = CollectionUtils.isEmpty(values2);
/*     */       
/* 190 */       if (isEmpty1 && isEmpty2) {
/* 191 */         return null;
/*     */       }
/*     */       
/* 194 */       List<String> values = new ArrayList<>();
/* 195 */       if (!isEmpty1) {
/* 196 */         values.addAll(values1);
/*     */       }
/* 198 */       if (!isEmpty2) {
/* 199 */         values.addAll(values2);
/*     */       }
/* 201 */       return values;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/ServletServerHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */