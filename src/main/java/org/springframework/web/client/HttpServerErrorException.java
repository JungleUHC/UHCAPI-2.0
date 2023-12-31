/*     */ package org.springframework.web.client;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
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
/*     */ public class HttpServerErrorException
/*     */   extends HttpStatusCodeException
/*     */ {
/*     */   private static final long serialVersionUID = -2915754006618138282L;
/*     */   
/*     */   public HttpServerErrorException(HttpStatus statusCode) {
/*  41 */     super(statusCode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerErrorException(HttpStatus statusCode, String statusText) {
/*  48 */     super(statusCode, statusText);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerErrorException(HttpStatus statusCode, String statusText, @Nullable byte[] body, @Nullable Charset charset) {
/*  57 */     super(statusCode, statusText, body, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerErrorException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body, @Nullable Charset charset) {
/*  66 */     super(statusCode, statusText, headers, body, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerErrorException(String message, HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body, @Nullable Charset charset) {
/*  77 */     super(message, statusCode, statusText, headers, body, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpServerErrorException create(HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/*  87 */     return create(null, statusCode, statusText, headers, body, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpServerErrorException create(@Nullable String message, HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/*  98 */     switch (statusCode) {
/*     */       case INTERNAL_SERVER_ERROR:
/* 100 */         return (message != null) ? new InternalServerError(message, statusText, headers, body, charset) : new InternalServerError(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case NOT_IMPLEMENTED:
/* 104 */         return (message != null) ? new NotImplemented(message, statusText, headers, body, charset) : new NotImplemented(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case BAD_GATEWAY:
/* 108 */         return (message != null) ? new BadGateway(message, statusText, headers, body, charset) : new BadGateway(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case SERVICE_UNAVAILABLE:
/* 112 */         return (message != null) ? new ServiceUnavailable(message, statusText, headers, body, charset) : new ServiceUnavailable(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case GATEWAY_TIMEOUT:
/* 116 */         return (message != null) ? new GatewayTimeout(message, statusText, headers, body, charset) : new GatewayTimeout(statusText, headers, body, charset);
/*     */     } 
/*     */ 
/*     */     
/* 120 */     return (message != null) ? new HttpServerErrorException(message, statusCode, statusText, headers, body, charset) : new HttpServerErrorException(statusCode, statusText, headers, body, charset);
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
/*     */   public static final class InternalServerError
/*     */     extends HttpServerErrorException
/*     */   {
/*     */     private InternalServerError(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 137 */       super(HttpStatus.INTERNAL_SERVER_ERROR, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private InternalServerError(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 143 */       super(message, HttpStatus.INTERNAL_SERVER_ERROR, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class NotImplemented
/*     */     extends HttpServerErrorException
/*     */   {
/*     */     private NotImplemented(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 155 */       super(HttpStatus.NOT_IMPLEMENTED, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private NotImplemented(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 161 */       super(message, HttpStatus.NOT_IMPLEMENTED, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class BadGateway
/*     */     extends HttpServerErrorException
/*     */   {
/*     */     private BadGateway(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 173 */       super(HttpStatus.BAD_GATEWAY, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private BadGateway(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 179 */       super(message, HttpStatus.BAD_GATEWAY, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class ServiceUnavailable
/*     */     extends HttpServerErrorException
/*     */   {
/*     */     private ServiceUnavailable(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 191 */       super(HttpStatus.SERVICE_UNAVAILABLE, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private ServiceUnavailable(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 197 */       super(message, HttpStatus.SERVICE_UNAVAILABLE, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class GatewayTimeout
/*     */     extends HttpServerErrorException
/*     */   {
/*     */     private GatewayTimeout(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 209 */       super(HttpStatus.GATEWAY_TIMEOUT, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private GatewayTimeout(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 215 */       super(message, HttpStatus.GATEWAY_TIMEOUT, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/HttpServerErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */