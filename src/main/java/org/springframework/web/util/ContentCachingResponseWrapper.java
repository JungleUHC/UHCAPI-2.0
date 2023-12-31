/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.WriteListener;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpServletResponseWrapper;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.FastByteArrayOutputStream;
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
/*     */ public class ContentCachingResponseWrapper
/*     */   extends HttpServletResponseWrapper
/*     */ {
/*  48 */   private final FastByteArrayOutputStream content = new FastByteArrayOutputStream(1024);
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ServletOutputStream outputStream;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private PrintWriter writer;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Integer contentLength;
/*     */ 
/*     */ 
/*     */   
/*     */   public ContentCachingResponseWrapper(HttpServletResponse response) {
/*  65 */     super(response);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendError(int sc) throws IOException {
/*  71 */     copyBodyToResponse(false);
/*     */     try {
/*  73 */       super.sendError(sc);
/*     */     }
/*  75 */     catch (IllegalStateException ex) {
/*     */       
/*  77 */       setStatus(sc);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendError(int sc, String msg) throws IOException {
/*  84 */     copyBodyToResponse(false);
/*     */     try {
/*  86 */       super.sendError(sc, msg);
/*     */     }
/*  88 */     catch (IllegalStateException ex) {
/*     */       
/*  90 */       setStatus(sc, msg);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendRedirect(String location) throws IOException {
/*  96 */     copyBodyToResponse(false);
/*  97 */     super.sendRedirect(location);
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletOutputStream getOutputStream() throws IOException {
/* 102 */     if (this.outputStream == null) {
/* 103 */       this.outputStream = new ResponseServletOutputStream(getResponse().getOutputStream());
/*     */     }
/* 105 */     return this.outputStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter getWriter() throws IOException {
/* 110 */     if (this.writer == null) {
/* 111 */       String characterEncoding = getCharacterEncoding();
/* 112 */       this.writer = (characterEncoding != null) ? new ResponsePrintWriter(characterEncoding) : new ResponsePrintWriter("ISO-8859-1");
/*     */     } 
/*     */     
/* 115 */     return this.writer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void flushBuffer() throws IOException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContentLength(int len) {
/* 125 */     if (len > this.content.size()) {
/* 126 */       this.content.resize(len);
/*     */     }
/* 128 */     this.contentLength = Integer.valueOf(len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContentLengthLong(long len) {
/* 134 */     if (len > 2147483647L) {
/* 135 */       throw new IllegalArgumentException("Content-Length exceeds ContentCachingResponseWrapper's maximum (2147483647): " + len);
/*     */     }
/*     */     
/* 138 */     int lenInt = (int)len;
/* 139 */     if (lenInt > this.content.size()) {
/* 140 */       this.content.resize(lenInt);
/*     */     }
/* 142 */     this.contentLength = Integer.valueOf(lenInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBufferSize(int size) {
/* 147 */     if (size > this.content.size()) {
/* 148 */       this.content.resize(size);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetBuffer() {
/* 154 */     this.content.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 159 */     super.reset();
/* 160 */     this.content.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getStatusCode() {
/* 169 */     return getStatus();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getContentAsByteArray() {
/* 176 */     return this.content.toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getContentInputStream() {
/* 184 */     return this.content.getInputStream();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getContentSize() {
/* 192 */     return this.content.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void copyBodyToResponse() throws IOException {
/* 200 */     copyBodyToResponse(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void copyBodyToResponse(boolean complete) throws IOException {
/* 210 */     if (this.content.size() > 0) {
/* 211 */       HttpServletResponse rawResponse = (HttpServletResponse)getResponse();
/* 212 */       if ((complete || this.contentLength != null) && !rawResponse.isCommitted()) {
/* 213 */         if (rawResponse.getHeader("Transfer-Encoding") == null) {
/* 214 */           rawResponse.setContentLength(complete ? this.content.size() : this.contentLength.intValue());
/*     */         }
/* 216 */         this.contentLength = null;
/*     */       } 
/* 218 */       this.content.writeTo((OutputStream)rawResponse.getOutputStream());
/* 219 */       this.content.reset();
/* 220 */       if (complete) {
/* 221 */         super.flushBuffer();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private class ResponseServletOutputStream
/*     */     extends ServletOutputStream
/*     */   {
/*     */     private final ServletOutputStream os;
/*     */     
/*     */     public ResponseServletOutputStream(ServletOutputStream os) {
/* 232 */       this.os = os;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(int b) throws IOException {
/* 237 */       ContentCachingResponseWrapper.this.content.write(b);
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(byte[] b, int off, int len) throws IOException {
/* 242 */       ContentCachingResponseWrapper.this.content.write(b, off, len);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isReady() {
/* 247 */       return this.os.isReady();
/*     */     }
/*     */ 
/*     */     
/*     */     public void setWriteListener(WriteListener writeListener) {
/* 252 */       this.os.setWriteListener(writeListener);
/*     */     }
/*     */   }
/*     */   
/*     */   private class ResponsePrintWriter
/*     */     extends PrintWriter
/*     */   {
/*     */     public ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException {
/* 260 */       super(new OutputStreamWriter((OutputStream)ContentCachingResponseWrapper.this.content, characterEncoding));
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(char[] buf, int off, int len) {
/* 265 */       super.write(buf, off, len);
/* 266 */       flush();
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(String s, int off, int len) {
/* 271 */       super.write(s, off, len);
/* 272 */       flush();
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(int c) {
/* 277 */       super.write(c);
/* 278 */       flush();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/ContentCachingResponseWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */