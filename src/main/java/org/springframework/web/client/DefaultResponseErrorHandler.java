/*     */ package org.springframework.web.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.springframework.core.log.LogFormatUtils;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.http.client.ClientHttpResponse;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.FileCopyUtils;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultResponseErrorHandler
/*     */   implements ResponseErrorHandler
/*     */ {
/*     */   public boolean hasError(ClientHttpResponse response) throws IOException {
/*  61 */     int rawStatusCode = response.getRawStatusCode();
/*  62 */     HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
/*  63 */     return (statusCode != null) ? hasError(statusCode) : hasError(rawStatusCode);
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
/*     */   protected boolean hasError(HttpStatus statusCode) {
/*  75 */     return statusCode.isError();
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
/*     */   protected boolean hasError(int unknownStatusCode) {
/*  91 */     HttpStatus.Series series = HttpStatus.Series.resolve(unknownStatusCode);
/*  92 */     return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleError(ClientHttpResponse response) throws IOException {
/* 113 */     HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
/* 114 */     if (statusCode == null) {
/* 115 */       byte[] body = getResponseBody(response);
/* 116 */       String message = getErrorMessage(response.getRawStatusCode(), response
/* 117 */           .getStatusText(), body, getCharset(response));
/* 118 */       throw new UnknownHttpStatusCodeException(message, response
/* 119 */           .getRawStatusCode(), response.getStatusText(), response
/* 120 */           .getHeaders(), body, getCharset(response));
/*     */     } 
/* 122 */     handleError(response, statusCode);
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
/*     */   private String getErrorMessage(int rawStatusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset charset) {
/* 134 */     String preface = rawStatusCode + " " + statusText + ": ";
/*     */     
/* 136 */     if (ObjectUtils.isEmpty(responseBody)) {
/* 137 */       return preface + "[no body]";
/*     */     }
/*     */     
/* 140 */     charset = (charset != null) ? charset : StandardCharsets.UTF_8;
/*     */     
/* 142 */     String bodyText = new String(responseBody, charset);
/* 143 */     bodyText = LogFormatUtils.formatValue(bodyText, -1, true);
/*     */     
/* 145 */     return preface + bodyText;
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
/*     */   protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
/* 160 */     String statusText = response.getStatusText();
/* 161 */     HttpHeaders headers = response.getHeaders();
/* 162 */     byte[] body = getResponseBody(response);
/* 163 */     Charset charset = getCharset(response);
/* 164 */     String message = getErrorMessage(statusCode.value(), statusText, body, charset);
/*     */     
/* 166 */     switch (statusCode.series()) {
/*     */       case CLIENT_ERROR:
/* 168 */         throw HttpClientErrorException.create(message, statusCode, statusText, headers, body, charset);
/*     */       case SERVER_ERROR:
/* 170 */         throw HttpServerErrorException.create(message, statusCode, statusText, headers, body, charset);
/*     */     } 
/* 172 */     throw new UnknownHttpStatusCodeException(message, statusCode.value(), statusText, headers, body, charset);
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
/*     */   @Deprecated
/*     */   protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
/* 188 */     HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
/* 189 */     if (statusCode == null) {
/* 190 */       throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(), response
/* 191 */           .getHeaders(), getResponseBody(response), getCharset(response));
/*     */     }
/* 193 */     return statusCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] getResponseBody(ClientHttpResponse response) {
/*     */     try {
/* 205 */       return FileCopyUtils.copyToByteArray(response.getBody());
/*     */     }
/* 207 */     catch (IOException iOException) {
/*     */ 
/*     */       
/* 210 */       return new byte[0];
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected Charset getCharset(ClientHttpResponse response) {
/* 221 */     HttpHeaders headers = response.getHeaders();
/* 222 */     MediaType contentType = headers.getContentType();
/* 223 */     return (contentType != null) ? contentType.getCharset() : null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/DefaultResponseErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */