/*     */ package org.springframework.http.server.reactive;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import javax.servlet.AsyncContext;
/*     */ import javax.servlet.ServletInputStream;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletRequestWrapper;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpServletResponseWrapper;
/*     */ import org.apache.catalina.connector.CoyoteInputStream;
/*     */ import org.apache.catalina.connector.CoyoteOutputStream;
/*     */ import org.apache.catalina.connector.Request;
/*     */ import org.apache.catalina.connector.RequestFacade;
/*     */ import org.apache.catalina.connector.Response;
/*     */ import org.apache.catalina.connector.ResponseFacade;
/*     */ import org.apache.coyote.Request;
/*     */ import org.apache.coyote.Response;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.core.io.buffer.DataBufferFactory;
/*     */ import org.springframework.core.io.buffer.DataBufferUtils;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TomcatHttpHandlerAdapter
/*     */   extends ServletHttpHandlerAdapter
/*     */ {
/*     */   public TomcatHttpHandlerAdapter(HttpHandler httpHandler) {
/*  63 */     super(httpHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext asyncContext) throws IOException, URISyntaxException {
/*  71 */     Assert.notNull(getServletPath(), "Servlet path is not initialized");
/*  72 */     return new TomcatServerHttpRequest(request, asyncContext, 
/*  73 */         getServletPath(), getDataBufferFactory(), getBufferSize());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext asyncContext, ServletServerHttpRequest request) throws IOException {
/*  80 */     return new TomcatServerHttpResponse(response, asyncContext, 
/*  81 */         getDataBufferFactory(), getBufferSize(), request);
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class TomcatServerHttpRequest
/*     */     extends ServletServerHttpRequest
/*     */   {
/*     */     private static final Field COYOTE_REQUEST_FIELD;
/*     */     
/*     */     private final int bufferSize;
/*     */     private final DataBufferFactory factory;
/*     */     
/*     */     static {
/*  94 */       Field field = ReflectionUtils.findField(RequestFacade.class, "request");
/*  95 */       Assert.state((field != null), "Incompatible Tomcat implementation");
/*  96 */       ReflectionUtils.makeAccessible(field);
/*  97 */       COYOTE_REQUEST_FIELD = field;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     TomcatServerHttpRequest(HttpServletRequest request, AsyncContext context, String servletPath, DataBufferFactory factory, int bufferSize) throws IOException, URISyntaxException {
/* 104 */       super(createTomcatHttpHeaders(request), request, context, servletPath, factory, bufferSize);
/* 105 */       this.factory = factory;
/* 106 */       this.bufferSize = bufferSize;
/*     */     }
/*     */     
/*     */     private static MultiValueMap<String, String> createTomcatHttpHeaders(HttpServletRequest request) {
/* 110 */       RequestFacade requestFacade = getRequestFacade(request);
/*     */       
/* 112 */       Request connectorRequest = (Request)ReflectionUtils.getField(COYOTE_REQUEST_FIELD, requestFacade);
/* 113 */       Assert.state((connectorRequest != null), "No Tomcat connector request");
/* 114 */       Request tomcatRequest = connectorRequest.getCoyoteRequest();
/* 115 */       return new TomcatHeadersAdapter(tomcatRequest.getMimeHeaders());
/*     */     }
/*     */     
/*     */     private static RequestFacade getRequestFacade(HttpServletRequest request) {
/* 119 */       if (request instanceof RequestFacade) {
/* 120 */         return (RequestFacade)request;
/*     */       }
/* 122 */       if (request instanceof HttpServletRequestWrapper) {
/* 123 */         HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper)request;
/* 124 */         HttpServletRequest wrappedRequest = (HttpServletRequest)wrapper.getRequest();
/* 125 */         return getRequestFacade(wrappedRequest);
/*     */       } 
/*     */       
/* 128 */       throw new IllegalArgumentException("Cannot convert [" + request.getClass() + "] to org.apache.catalina.connector.RequestFacade");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected DataBuffer readFromInputStream() throws IOException {
/* 135 */       ServletInputStream inputStream = ((ServletRequest)getNativeRequest()).getInputStream();
/* 136 */       if (!(inputStream instanceof CoyoteInputStream))
/*     */       {
/* 138 */         return super.readFromInputStream();
/*     */       }
/* 140 */       boolean release = true;
/* 141 */       int capacity = this.bufferSize;
/* 142 */       DataBuffer dataBuffer = this.factory.allocateBuffer(capacity);
/*     */       try {
/* 144 */         ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, capacity);
/* 145 */         int read = ((CoyoteInputStream)inputStream).read(byteBuffer);
/* 146 */         logBytesRead(read);
/* 147 */         if (read > 0) {
/* 148 */           dataBuffer.writePosition(read);
/* 149 */           release = false;
/* 150 */           return dataBuffer;
/*     */         } 
/* 152 */         if (read == -1) {
/* 153 */           return EOF_BUFFER;
/*     */         }
/*     */         
/* 156 */         return null;
/*     */       }
/*     */       finally {
/*     */         
/* 160 */         if (release) {
/* 161 */           DataBufferUtils.release(dataBuffer);
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class TomcatServerHttpResponse
/*     */     extends ServletServerHttpResponse
/*     */   {
/*     */     private static final Field COYOTE_RESPONSE_FIELD;
/*     */     
/*     */     static {
/* 173 */       Field field = ReflectionUtils.findField(ResponseFacade.class, "response");
/* 174 */       Assert.state((field != null), "Incompatible Tomcat implementation");
/* 175 */       ReflectionUtils.makeAccessible(field);
/* 176 */       COYOTE_RESPONSE_FIELD = field;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TomcatServerHttpResponse(HttpServletResponse response, AsyncContext context, DataBufferFactory factory, int bufferSize, ServletServerHttpRequest request) throws IOException {
/* 182 */       super(createTomcatHttpHeaders(response), response, context, factory, bufferSize, request);
/*     */     }
/*     */     
/*     */     private static HttpHeaders createTomcatHttpHeaders(HttpServletResponse response) {
/* 186 */       ResponseFacade responseFacade = getResponseFacade(response);
/*     */       
/* 188 */       Response connectorResponse = (Response)ReflectionUtils.getField(COYOTE_RESPONSE_FIELD, responseFacade);
/* 189 */       Assert.state((connectorResponse != null), "No Tomcat connector response");
/* 190 */       Response tomcatResponse = connectorResponse.getCoyoteResponse();
/* 191 */       TomcatHeadersAdapter headers = new TomcatHeadersAdapter(tomcatResponse.getMimeHeaders());
/* 192 */       return new HttpHeaders(headers);
/*     */     }
/*     */     
/*     */     private static ResponseFacade getResponseFacade(HttpServletResponse response) {
/* 196 */       if (response instanceof ResponseFacade) {
/* 197 */         return (ResponseFacade)response;
/*     */       }
/* 199 */       if (response instanceof HttpServletResponseWrapper) {
/* 200 */         HttpServletResponseWrapper wrapper = (HttpServletResponseWrapper)response;
/* 201 */         HttpServletResponse wrappedResponse = (HttpServletResponse)wrapper.getResponse();
/* 202 */         return getResponseFacade(wrappedResponse);
/*     */       } 
/*     */       
/* 205 */       throw new IllegalArgumentException("Cannot convert [" + response.getClass() + "] to org.apache.catalina.connector.ResponseFacade");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void applyHeaders() {
/* 212 */       HttpServletResponse response = getNativeResponse();
/* 213 */       MediaType contentType = null;
/*     */       try {
/* 215 */         contentType = getHeaders().getContentType();
/*     */       }
/* 217 */       catch (Exception ex) {
/* 218 */         String rawContentType = getHeaders().getFirst("Content-Type");
/* 219 */         response.setContentType(rawContentType);
/*     */       } 
/* 221 */       if (response.getContentType() == null && contentType != null) {
/* 222 */         response.setContentType(contentType.toString());
/*     */       }
/* 224 */       getHeaders().remove("Content-Type");
/* 225 */       Charset charset = (contentType != null) ? contentType.getCharset() : null;
/* 226 */       if (response.getCharacterEncoding() == null && charset != null) {
/* 227 */         response.setCharacterEncoding(charset.name());
/*     */       }
/* 229 */       long contentLength = getHeaders().getContentLength();
/* 230 */       if (contentLength != -1L) {
/* 231 */         response.setContentLengthLong(contentLength);
/*     */       }
/* 233 */       getHeaders().remove("Content-Length");
/*     */     }
/*     */ 
/*     */     
/*     */     protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
/* 238 */       ByteBuffer input = dataBuffer.asByteBuffer();
/* 239 */       int len = input.remaining();
/* 240 */       ServletResponse response = getNativeResponse();
/* 241 */       ((CoyoteOutputStream)response.getOutputStream()).write(input);
/* 242 */       return len;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/TomcatHttpHandlerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */