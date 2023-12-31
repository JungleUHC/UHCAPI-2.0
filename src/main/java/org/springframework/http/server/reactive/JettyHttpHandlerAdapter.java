/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import javax.servlet.AsyncContext;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletRequestWrapper;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpServletResponseWrapper;
/*     */ import org.eclipse.jetty.http.HttpFields;
/*     */ import org.eclipse.jetty.server.HttpOutput;
/*     */ import org.eclipse.jetty.server.Request;
/*     */ import org.eclipse.jetty.server.Response;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JettyHttpHandlerAdapter
/*     */   extends ServletHttpHandlerAdapter
/*     */ {
/*  55 */   private static final boolean jetty10Present = ClassUtils.isPresent("org.eclipse.jetty.http.CookieCutter", JettyHttpHandlerAdapter.class
/*  56 */       .getClassLoader());
/*     */ 
/*     */   
/*     */   public JettyHttpHandlerAdapter(HttpHandler httpHandler) {
/*  60 */     super(httpHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext context) throws IOException, URISyntaxException {
/*  69 */     if (jetty10Present) {
/*  70 */       return super.createRequest(request, context);
/*     */     }
/*     */     
/*  73 */     Assert.notNull(getServletPath(), "Servlet path is not initialized");
/*  74 */     return new JettyServerHttpRequest(request, context, 
/*  75 */         getServletPath(), getDataBufferFactory(), getBufferSize());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context, ServletServerHttpRequest request) throws IOException {
/*  83 */     if (jetty10Present) {
/*  84 */       return new BaseJettyServerHttpResponse(response, context, 
/*  85 */           getDataBufferFactory(), getBufferSize(), request);
/*     */     }
/*     */     
/*  88 */     return new JettyServerHttpResponse(response, context, 
/*  89 */         getDataBufferFactory(), getBufferSize(), request);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class JettyServerHttpRequest
/*     */     extends ServletServerHttpRequest
/*     */   {
/*     */     JettyServerHttpRequest(HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
/* 100 */       super(createHeaders(request), request, asyncContext, servletPath, bufferFactory, bufferSize);
/*     */     }
/*     */     
/*     */     private static MultiValueMap<String, String> createHeaders(HttpServletRequest servletRequest) {
/* 104 */       Request request = getRequest(servletRequest);
/* 105 */       HttpFields fields = request.getMetaData().getFields();
/* 106 */       return new JettyHeadersAdapter(fields);
/*     */     }
/*     */     
/*     */     private static Request getRequest(HttpServletRequest request) {
/* 110 */       if (request instanceof Request) {
/* 111 */         return (Request)request;
/*     */       }
/* 113 */       if (request instanceof HttpServletRequestWrapper) {
/* 114 */         HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper)request;
/* 115 */         HttpServletRequest wrappedRequest = (HttpServletRequest)wrapper.getRequest();
/* 116 */         return getRequest(wrappedRequest);
/*     */       } 
/*     */       
/* 119 */       throw new IllegalArgumentException("Cannot convert [" + request.getClass() + "] to org.eclipse.jetty.server.Request");
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
/*     */   private static class BaseJettyServerHttpResponse
/*     */     extends ServletServerHttpResponse
/*     */   {
/*     */     BaseJettyServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
/* 134 */       super(response, asyncContext, bufferFactory, bufferSize, request);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     BaseJettyServerHttpResponse(HttpHeaders headers, HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
/* 141 */       super(headers, response, asyncContext, bufferFactory, bufferSize, request);
/*     */     }
/*     */ 
/*     */     
/*     */     protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
/* 146 */       ByteBuffer input = dataBuffer.asByteBuffer();
/* 147 */       int len = input.remaining();
/* 148 */       ServletResponse response = getNativeResponse();
/* 149 */       ((HttpOutput)response.getOutputStream()).write(input);
/* 150 */       return len;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class JettyServerHttpResponse
/*     */     extends BaseJettyServerHttpResponse
/*     */   {
/*     */     JettyServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
/* 161 */       super(createHeaders(response), response, asyncContext, bufferFactory, bufferSize, request);
/*     */     }
/*     */     
/*     */     private static HttpHeaders createHeaders(HttpServletResponse servletResponse) {
/* 165 */       Response response = getResponse(servletResponse);
/* 166 */       HttpFields fields = response.getHttpFields();
/* 167 */       return new HttpHeaders(new JettyHeadersAdapter(fields));
/*     */     }
/*     */     
/*     */     private static Response getResponse(HttpServletResponse response) {
/* 171 */       if (response instanceof Response) {
/* 172 */         return (Response)response;
/*     */       }
/* 174 */       if (response instanceof HttpServletResponseWrapper) {
/* 175 */         HttpServletResponseWrapper wrapper = (HttpServletResponseWrapper)response;
/* 176 */         HttpServletResponse wrappedResponse = (HttpServletResponse)wrapper.getResponse();
/* 177 */         return getResponse(wrappedResponse);
/*     */       } 
/*     */       
/* 180 */       throw new IllegalArgumentException("Cannot convert [" + response.getClass() + "] to org.eclipse.jetty.server.Response");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void applyHeaders() {
/* 187 */       HttpServletResponse response = getNativeResponse();
/* 188 */       MediaType contentType = null;
/*     */       try {
/* 190 */         contentType = getHeaders().getContentType();
/*     */       }
/* 192 */       catch (Exception ex) {
/* 193 */         String rawContentType = getHeaders().getFirst("Content-Type");
/* 194 */         response.setContentType(rawContentType);
/*     */       } 
/* 196 */       if (response.getContentType() == null && contentType != null) {
/* 197 */         response.setContentType(contentType.toString());
/*     */       }
/* 199 */       Charset charset = (contentType != null) ? contentType.getCharset() : null;
/* 200 */       if (response.getCharacterEncoding() == null && charset != null) {
/* 201 */         response.setCharacterEncoding(charset.name());
/*     */       }
/* 203 */       long contentLength = getHeaders().getContentLength();
/* 204 */       if (contentLength != -1L)
/* 205 */         response.setContentLengthLong(contentLength); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/JettyHttpHandlerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */