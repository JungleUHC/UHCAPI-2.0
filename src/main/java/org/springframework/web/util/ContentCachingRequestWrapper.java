/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ReadListener;
/*     */ import javax.servlet.ServletInputStream;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletRequestWrapper;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class ContentCachingRequestWrapper
/*     */   extends HttpServletRequestWrapper
/*     */ {
/*     */   private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
/*     */   private final ByteArrayOutputStream cachedContent;
/*     */   @Nullable
/*     */   private final Integer contentCacheLimit;
/*     */   @Nullable
/*     */   private ServletInputStream inputStream;
/*     */   @Nullable
/*     */   private BufferedReader reader;
/*     */   
/*     */   public ContentCachingRequestWrapper(HttpServletRequest request) {
/*  78 */     super(request);
/*  79 */     int contentLength = request.getContentLength();
/*  80 */     this.cachedContent = new ByteArrayOutputStream((contentLength >= 0) ? contentLength : 1024);
/*  81 */     this.contentCacheLimit = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContentCachingRequestWrapper(HttpServletRequest request, int contentCacheLimit) {
/*  92 */     super(request);
/*  93 */     this.cachedContent = new ByteArrayOutputStream(contentCacheLimit);
/*  94 */     this.contentCacheLimit = Integer.valueOf(contentCacheLimit);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletInputStream getInputStream() throws IOException {
/* 100 */     if (this.inputStream == null) {
/* 101 */       this.inputStream = new ContentCachingInputStream(getRequest().getInputStream());
/*     */     }
/* 103 */     return this.inputStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCharacterEncoding() {
/* 108 */     String enc = super.getCharacterEncoding();
/* 109 */     return (enc != null) ? enc : "ISO-8859-1";
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedReader getReader() throws IOException {
/* 114 */     if (this.reader == null) {
/* 115 */       this.reader = new BufferedReader(new InputStreamReader((InputStream)getInputStream(), getCharacterEncoding()));
/*     */     }
/* 117 */     return this.reader;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getParameter(String name) {
/* 122 */     if (this.cachedContent.size() == 0 && isFormPost()) {
/* 123 */       writeRequestParametersToCachedContent();
/*     */     }
/* 125 */     return super.getParameter(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String[]> getParameterMap() {
/* 130 */     if (this.cachedContent.size() == 0 && isFormPost()) {
/* 131 */       writeRequestParametersToCachedContent();
/*     */     }
/* 133 */     return super.getParameterMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public Enumeration<String> getParameterNames() {
/* 138 */     if (this.cachedContent.size() == 0 && isFormPost()) {
/* 139 */       writeRequestParametersToCachedContent();
/*     */     }
/* 141 */     return super.getParameterNames();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getParameterValues(String name) {
/* 146 */     if (this.cachedContent.size() == 0 && isFormPost()) {
/* 147 */       writeRequestParametersToCachedContent();
/*     */     }
/* 149 */     return super.getParameterValues(name);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isFormPost() {
/* 154 */     String contentType = getContentType();
/* 155 */     return (contentType != null && contentType.contains("application/x-www-form-urlencoded") && HttpMethod.POST
/* 156 */       .matches(getMethod()));
/*     */   }
/*     */   
/*     */   private void writeRequestParametersToCachedContent() {
/*     */     try {
/* 161 */       if (this.cachedContent.size() == 0) {
/* 162 */         String requestEncoding = getCharacterEncoding();
/* 163 */         Map<String, String[]> form = super.getParameterMap();
/* 164 */         for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
/* 165 */           String name = nameIterator.next();
/* 166 */           List<String> values = Arrays.asList((Object[])form.get(name));
/* 167 */           for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
/* 168 */             String value = valueIterator.next();
/* 169 */             this.cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
/* 170 */             if (value != null) {
/* 171 */               this.cachedContent.write(61);
/* 172 */               this.cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
/* 173 */               if (valueIterator.hasNext()) {
/* 174 */                 this.cachedContent.write(38);
/*     */               }
/*     */             } 
/*     */           } 
/* 178 */           if (nameIterator.hasNext()) {
/* 179 */             this.cachedContent.write(38);
/*     */           }
/*     */         }
/*     */       
/*     */       } 
/* 184 */     } catch (IOException ex) {
/* 185 */       throw new IllegalStateException("Failed to write request parameters to cached content", ex);
/*     */     } 
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
/*     */   public byte[] getContentAsByteArray() {
/* 199 */     return this.cachedContent.toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleContentOverflow(int contentCacheLimit) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class ContentCachingInputStream
/*     */     extends ServletInputStream
/*     */   {
/*     */     private final ServletInputStream is;
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean overflow = false;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ContentCachingInputStream(ServletInputStream is) {
/* 223 */       this.is = is;
/*     */     }
/*     */ 
/*     */     
/*     */     public int read() throws IOException {
/* 228 */       int ch = this.is.read();
/* 229 */       if (ch != -1 && !this.overflow) {
/* 230 */         if (ContentCachingRequestWrapper.this.contentCacheLimit != null && ContentCachingRequestWrapper.this.cachedContent.size() == ContentCachingRequestWrapper.this.contentCacheLimit.intValue()) {
/* 231 */           this.overflow = true;
/* 232 */           ContentCachingRequestWrapper.this.handleContentOverflow(ContentCachingRequestWrapper.this.contentCacheLimit.intValue());
/*     */         } else {
/*     */           
/* 235 */           ContentCachingRequestWrapper.this.cachedContent.write(ch);
/*     */         } 
/*     */       }
/* 238 */       return ch;
/*     */     }
/*     */ 
/*     */     
/*     */     public int read(byte[] b) throws IOException {
/* 243 */       int count = this.is.read(b);
/* 244 */       writeToCache(b, 0, count);
/* 245 */       return count;
/*     */     }
/*     */     
/*     */     private void writeToCache(byte[] b, int off, int count) {
/* 249 */       if (!this.overflow && count > 0) {
/* 250 */         if (ContentCachingRequestWrapper.this.contentCacheLimit != null && count + ContentCachingRequestWrapper.this
/* 251 */           .cachedContent.size() > ContentCachingRequestWrapper.this.contentCacheLimit.intValue()) {
/* 252 */           this.overflow = true;
/* 253 */           ContentCachingRequestWrapper.this.cachedContent.write(b, off, ContentCachingRequestWrapper.this.contentCacheLimit.intValue() - ContentCachingRequestWrapper.this.cachedContent.size());
/* 254 */           ContentCachingRequestWrapper.this.handleContentOverflow(ContentCachingRequestWrapper.this.contentCacheLimit.intValue());
/*     */           return;
/*     */         } 
/* 257 */         ContentCachingRequestWrapper.this.cachedContent.write(b, off, count);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int read(byte[] b, int off, int len) throws IOException {
/* 263 */       int count = this.is.read(b, off, len);
/* 264 */       writeToCache(b, off, count);
/* 265 */       return count;
/*     */     }
/*     */ 
/*     */     
/*     */     public int readLine(byte[] b, int off, int len) throws IOException {
/* 270 */       int count = this.is.readLine(b, off, len);
/* 271 */       writeToCache(b, off, count);
/* 272 */       return count;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFinished() {
/* 277 */       return this.is.isFinished();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isReady() {
/* 282 */       return this.is.isReady();
/*     */     }
/*     */ 
/*     */     
/*     */     public void setReadListener(ReadListener readListener) {
/* 287 */       this.is.setReadListener(readListener);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/ContentCachingRequestWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */