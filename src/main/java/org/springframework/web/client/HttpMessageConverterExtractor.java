/*     */ package org.springframework.web.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.core.ResolvableType;
/*     */ import org.springframework.http.HttpInputMessage;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.client.ClientHttpResponse;
/*     */ import org.springframework.http.converter.GenericHttpMessageConverter;
/*     */ import org.springframework.http.converter.HttpMessageConverter;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.FileCopyUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpMessageConverterExtractor<T>
/*     */   implements ResponseExtractor<T>
/*     */ {
/*     */   private final Type responseType;
/*     */   @Nullable
/*     */   private final Class<T> responseClass;
/*     */   private final List<HttpMessageConverter<?>> messageConverters;
/*     */   private final Log logger;
/*     */   
/*     */   public HttpMessageConverterExtractor(Class<T> responseType, List<HttpMessageConverter<?>> messageConverters) {
/*  63 */     this(responseType, messageConverters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters) {
/*  71 */     this(responseType, messageConverters, LogFactory.getLog(HttpMessageConverterExtractor.class));
/*     */   }
/*     */ 
/*     */   
/*     */   HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters, Log logger) {
/*  76 */     Assert.notNull(responseType, "'responseType' must not be null");
/*  77 */     Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
/*  78 */     Assert.noNullElements(messageConverters, "'messageConverters' must not contain null elements");
/*  79 */     this.responseType = responseType;
/*  80 */     this.responseClass = (responseType instanceof Class) ? (Class<T>)responseType : null;
/*  81 */     this.messageConverters = messageConverters;
/*  82 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T extractData(ClientHttpResponse response) throws IOException {
/*  89 */     MessageBodyClientHttpResponseWrapper responseWrapper = new MessageBodyClientHttpResponseWrapper(response);
/*  90 */     if (!responseWrapper.hasMessageBody() || responseWrapper.hasEmptyMessageBody()) {
/*  91 */       return null;
/*     */     }
/*  93 */     MediaType contentType = getContentType(responseWrapper);
/*     */     
/*     */     try {
/*  96 */       for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
/*  97 */         if (messageConverter instanceof GenericHttpMessageConverter) {
/*  98 */           GenericHttpMessageConverter<?> genericMessageConverter = (GenericHttpMessageConverter)messageConverter;
/*     */           
/* 100 */           if (genericMessageConverter.canRead(this.responseType, null, contentType)) {
/* 101 */             if (this.logger.isDebugEnabled()) {
/* 102 */               ResolvableType resolvableType = ResolvableType.forType(this.responseType);
/* 103 */               this.logger.debug("Reading to [" + resolvableType + "]");
/*     */             } 
/* 105 */             return (T)genericMessageConverter.read(this.responseType, null, (HttpInputMessage)responseWrapper);
/*     */           } 
/*     */         } 
/* 108 */         if (this.responseClass != null && 
/* 109 */           messageConverter.canRead(this.responseClass, contentType)) {
/* 110 */           if (this.logger.isDebugEnabled()) {
/* 111 */             String className = this.responseClass.getName();
/* 112 */             this.logger.debug("Reading to [" + className + "] as \"" + contentType + "\"");
/*     */           } 
/* 114 */           return (T)messageConverter.read(this.responseClass, (HttpInputMessage)responseWrapper);
/*     */         }
/*     */       
/*     */       }
/*     */     
/* 119 */     } catch (IOException|org.springframework.http.converter.HttpMessageNotReadableException ex) {
/* 120 */       throw new RestClientException("Error while extracting response for type [" + this.responseType + "] and content type [" + contentType + "]", ex);
/*     */     } 
/*     */ 
/*     */     
/* 124 */     throw new UnknownContentTypeException(this.responseType, contentType, responseWrapper
/* 125 */         .getRawStatusCode(), responseWrapper.getStatusText(), responseWrapper
/* 126 */         .getHeaders(), getResponseBody(responseWrapper));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MediaType getContentType(ClientHttpResponse response) {
/* 136 */     MediaType contentType = response.getHeaders().getContentType();
/* 137 */     if (contentType == null) {
/* 138 */       if (this.logger.isTraceEnabled()) {
/* 139 */         this.logger.trace("No content-type, using 'application/octet-stream'");
/*     */       }
/* 141 */       contentType = MediaType.APPLICATION_OCTET_STREAM;
/*     */     } 
/* 143 */     return contentType;
/*     */   }
/*     */   
/*     */   private static byte[] getResponseBody(ClientHttpResponse response) {
/*     */     try {
/* 148 */       return FileCopyUtils.copyToByteArray(response.getBody());
/*     */     }
/* 150 */     catch (IOException iOException) {
/*     */ 
/*     */       
/* 153 */       return new byte[0];
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/HttpMessageConverterExtractor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */