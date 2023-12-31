/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import javax.servlet.AsyncContext;
/*     */ import javax.servlet.AsyncEvent;
/*     */ import javax.servlet.AsyncListener;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.WriteListener;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.reactivestreams.Processor;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.ResponseCookie;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ class ServletServerHttpResponse
/*     */   extends AbstractListenerServerHttpResponse
/*     */ {
/*     */   private final HttpServletResponse response;
/*     */   private final ServletOutputStream outputStream;
/*     */   private final int bufferSize;
/*     */   @Nullable
/*     */   private volatile ResponseBodyFlushProcessor bodyFlushProcessor;
/*     */   @Nullable
/*     */   private volatile ResponseBodyProcessor bodyProcessor;
/*     */   private volatile boolean flushOnNext;
/*     */   private final ServletServerHttpRequest request;
/*     */   private final ResponseAsyncListener asyncListener;
/*     */   
/*     */   public ServletServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
/*  74 */     this(new HttpHeaders(), response, asyncContext, bufferFactory, bufferSize, request);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletServerHttpResponse(HttpHeaders headers, HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
/*  80 */     super(bufferFactory, headers);
/*     */     
/*  82 */     Assert.notNull(response, "HttpServletResponse must not be null");
/*  83 */     Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
/*  84 */     Assert.isTrue((bufferSize > 0), "Buffer size must be greater than 0");
/*     */     
/*  86 */     this.response = response;
/*  87 */     this.outputStream = response.getOutputStream();
/*  88 */     this.bufferSize = bufferSize;
/*  89 */     this.request = request;
/*     */     
/*  91 */     this.asyncListener = new ResponseAsyncListener();
/*     */ 
/*     */     
/*  94 */     response.getOutputStream().setWriteListener(new ResponseBodyWriteListener());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getNativeResponse() {
/* 101 */     return (T)this.response;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpStatus getStatusCode() {
/* 106 */     HttpStatus status = super.getStatusCode();
/* 107 */     return (status != null) ? status : HttpStatus.resolve(this.response.getStatus());
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getRawStatusCode() {
/* 112 */     Integer status = super.getRawStatusCode();
/* 113 */     return Integer.valueOf((status != null) ? status.intValue() : this.response.getStatus());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyStatusCode() {
/* 118 */     Integer status = super.getRawStatusCode();
/* 119 */     if (status != null) {
/* 120 */       this.response.setStatus(status.intValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyHeaders() {
/* 126 */     getHeaders().forEach((headerName, headerValues) -> {
/*     */           for (String headerValue : headerValues) {
/*     */             this.response.addHeader(headerName, headerValue);
/*     */           }
/*     */         });
/* 131 */     MediaType contentType = null;
/*     */     try {
/* 133 */       contentType = getHeaders().getContentType();
/*     */     }
/* 135 */     catch (Exception ex) {
/* 136 */       String rawContentType = getHeaders().getFirst("Content-Type");
/* 137 */       this.response.setContentType(rawContentType);
/*     */     } 
/* 139 */     if (this.response.getContentType() == null && contentType != null) {
/* 140 */       this.response.setContentType(contentType.toString());
/*     */     }
/* 142 */     Charset charset = (contentType != null) ? contentType.getCharset() : null;
/* 143 */     if (this.response.getCharacterEncoding() == null && charset != null) {
/* 144 */       this.response.setCharacterEncoding(charset.name());
/*     */     }
/* 146 */     long contentLength = getHeaders().getContentLength();
/* 147 */     if (contentLength != -1L) {
/* 148 */       this.response.setContentLengthLong(contentLength);
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
/*     */ 
/*     */   
/*     */   protected void applyCookies() {
/* 164 */     for (List<ResponseCookie> cookies : (Iterable<List<ResponseCookie>>)getCookies().values()) {
/* 165 */       for (ResponseCookie cookie : cookies) {
/* 166 */         this.response.addHeader("Set-Cookie", cookie.toString());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AsyncListener getAsyncListener() {
/* 178 */     return this.asyncListener;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor() {
/* 183 */     ResponseBodyFlushProcessor processor = new ResponseBodyFlushProcessor();
/* 184 */     this.bodyFlushProcessor = processor;
/* 185 */     return processor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
/* 195 */     ServletOutputStream outputStream = this.outputStream;
/* 196 */     InputStream input = dataBuffer.asInputStream();
/* 197 */     int bytesWritten = 0;
/* 198 */     byte[] buffer = new byte[this.bufferSize];
/*     */     int bytesRead;
/* 200 */     while (outputStream.isReady() && (bytesRead = input.read(buffer)) != -1) {
/* 201 */       outputStream.write(buffer, 0, bytesRead);
/* 202 */       bytesWritten += bytesRead;
/*     */     } 
/* 204 */     return bytesWritten;
/*     */   }
/*     */   
/*     */   private void flush() throws IOException {
/* 208 */     ServletOutputStream outputStream = this.outputStream;
/* 209 */     if (outputStream.isReady()) {
/*     */       try {
/* 211 */         outputStream.flush();
/* 212 */         this.flushOnNext = false;
/*     */       }
/* 214 */       catch (IOException ex) {
/* 215 */         this.flushOnNext = true;
/* 216 */         throw ex;
/*     */       } 
/*     */     } else {
/*     */       
/* 220 */       this.flushOnNext = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isWritePossible() {
/* 225 */     return this.outputStream.isReady();
/*     */   }
/*     */   
/*     */   private final class ResponseAsyncListener
/*     */     implements AsyncListener
/*     */   {
/*     */     private ResponseAsyncListener() {}
/*     */     
/*     */     public void onStartAsync(AsyncEvent event) {}
/*     */     
/*     */     public void onTimeout(AsyncEvent event) {
/* 236 */       Throwable ex = event.getThrowable();
/* 237 */       ex = (ex != null) ? ex : new IllegalStateException("Async operation timeout.");
/* 238 */       handleError(ex);
/*     */     }
/*     */ 
/*     */     
/*     */     public void onError(AsyncEvent event) {
/* 243 */       handleError(event.getThrowable());
/*     */     }
/*     */     
/*     */     public void handleError(Throwable ex) {
/* 247 */       ServletServerHttpResponse.ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
/* 248 */       ServletServerHttpResponse.ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
/* 249 */       if (flushProcessor != null) {
/*     */         
/* 251 */         flushProcessor.cancel();
/*     */         
/* 253 */         if (processor != null) {
/* 254 */           processor.cancel();
/* 255 */           processor.onError(ex);
/*     */         } 
/*     */         
/* 258 */         flushProcessor.onError(ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void onComplete(AsyncEvent event) {
/* 264 */       ServletServerHttpResponse.ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
/* 265 */       ServletServerHttpResponse.ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
/* 266 */       if (flushProcessor != null) {
/*     */         
/* 268 */         flushProcessor.cancel();
/*     */         
/* 270 */         if (processor != null) {
/* 271 */           processor.cancel();
/* 272 */           processor.onComplete();
/*     */         } 
/*     */         
/* 275 */         flushProcessor.onComplete();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private class ResponseBodyWriteListener
/*     */     implements WriteListener {
/*     */     private ResponseBodyWriteListener() {}
/*     */     
/*     */     public void onWritePossible() {
/* 285 */       ServletServerHttpResponse.ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
/* 286 */       if (processor != null) {
/* 287 */         processor.onWritePossible();
/*     */       } else {
/*     */         
/* 290 */         ServletServerHttpResponse.ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
/* 291 */         if (flushProcessor != null) {
/* 292 */           flushProcessor.onFlushPossible();
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void onError(Throwable ex) {
/* 299 */       ServletServerHttpResponse.this.asyncListener.handleError(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   private class ResponseBodyFlushProcessor
/*     */     extends AbstractListenerWriteFlushProcessor<DataBuffer>
/*     */   {
/*     */     public ResponseBodyFlushProcessor() {
/* 307 */       super(ServletServerHttpResponse.this.request.getLogPrefix());
/*     */     }
/*     */ 
/*     */     
/*     */     protected Processor<? super DataBuffer, Void> createWriteProcessor() {
/* 312 */       ServletServerHttpResponse.ResponseBodyProcessor processor = new ServletServerHttpResponse.ResponseBodyProcessor();
/* 313 */       ServletServerHttpResponse.this.bodyProcessor = processor;
/* 314 */       return processor;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void flush() throws IOException {
/* 319 */       if (rsWriteFlushLogger.isTraceEnabled()) {
/* 320 */         rsWriteFlushLogger.trace(getLogPrefix() + "flushing");
/*     */       }
/* 322 */       ServletServerHttpResponse.this.flush();
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isWritePossible() {
/* 327 */       return ServletServerHttpResponse.this.isWritePossible();
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isFlushPending() {
/* 332 */       return ServletServerHttpResponse.this.flushOnNext;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private class ResponseBodyProcessor
/*     */     extends AbstractListenerWriteProcessor<DataBuffer>
/*     */   {
/*     */     public ResponseBodyProcessor() {
/* 341 */       super(ServletServerHttpResponse.this.request.getLogPrefix());
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isWritePossible() {
/* 346 */       return ServletServerHttpResponse.this.isWritePossible();
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isDataEmpty(DataBuffer dataBuffer) {
/* 351 */       return (dataBuffer.readableByteCount() == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean write(DataBuffer dataBuffer) throws IOException {
/* 356 */       if (ServletServerHttpResponse.this.flushOnNext) {
/* 357 */         if (rsWriteLogger.isTraceEnabled()) {
/* 358 */           rsWriteLogger.trace(getLogPrefix() + "flushing");
/*     */         }
/* 360 */         ServletServerHttpResponse.this.flush();
/*     */       } 
/*     */       
/* 363 */       boolean ready = ServletServerHttpResponse.this.isWritePossible();
/* 364 */       int remaining = dataBuffer.readableByteCount();
/* 365 */       if (ready && remaining > 0) {
/*     */         
/* 367 */         int written = ServletServerHttpResponse.this.writeToOutputStream(dataBuffer);
/* 368 */         if (rsWriteLogger.isTraceEnabled()) {
/* 369 */           rsWriteLogger.trace(getLogPrefix() + "Wrote " + written + " of " + remaining + " bytes");
/*     */         }
/* 371 */         if (written == remaining) {
/* 372 */           DataBufferUtils.release(dataBuffer);
/* 373 */           return true;
/*     */         }
/*     */       
/*     */       }
/* 377 */       else if (rsWriteLogger.isTraceEnabled()) {
/* 378 */         rsWriteLogger.trace(getLogPrefix() + "ready: " + ready + ", remaining: " + remaining);
/*     */       } 
/*     */ 
/*     */       
/* 382 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void writingComplete() {
/* 387 */       ServletServerHttpResponse.this.bodyProcessor = null;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void discardData(DataBuffer dataBuffer) {
/* 392 */       DataBufferUtils.release(dataBuffer);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/ServletServerHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */