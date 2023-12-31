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
/*     */ public class HttpClientErrorException
/*     */   extends HttpStatusCodeException
/*     */ {
/*     */   private static final long serialVersionUID = 5177019431887513952L;
/*     */   
/*     */   public HttpClientErrorException(HttpStatus statusCode) {
/*  41 */     super(statusCode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpClientErrorException(HttpStatus statusCode, String statusText) {
/*  48 */     super(statusCode, statusText);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpClientErrorException(HttpStatus statusCode, String statusText, @Nullable byte[] body, @Nullable Charset responseCharset) {
/*  57 */     super(statusCode, statusText, body, responseCharset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpClientErrorException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body, @Nullable Charset responseCharset) {
/*  66 */     super(statusCode, statusText, headers, body, responseCharset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpClientErrorException(String message, HttpStatus statusCode, String statusText, @Nullable HttpHeaders headers, @Nullable byte[] body, @Nullable Charset responseCharset) {
/*  77 */     super(message, statusCode, statusText, headers, body, responseCharset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpClientErrorException create(HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/*  88 */     return create(null, statusCode, statusText, headers, body, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpClientErrorException create(@Nullable String message, HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/*  99 */     switch (statusCode) {
/*     */       case BAD_REQUEST:
/* 101 */         return (message != null) ? new BadRequest(message, statusText, headers, body, charset) : new BadRequest(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case UNAUTHORIZED:
/* 105 */         return (message != null) ? new Unauthorized(message, statusText, headers, body, charset) : new Unauthorized(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case FORBIDDEN:
/* 109 */         return (message != null) ? new Forbidden(message, statusText, headers, body, charset) : new Forbidden(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case NOT_FOUND:
/* 113 */         return (message != null) ? new NotFound(message, statusText, headers, body, charset) : new NotFound(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case METHOD_NOT_ALLOWED:
/* 117 */         return (message != null) ? new MethodNotAllowed(message, statusText, headers, body, charset) : new MethodNotAllowed(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case NOT_ACCEPTABLE:
/* 121 */         return (message != null) ? new NotAcceptable(message, statusText, headers, body, charset) : new NotAcceptable(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case CONFLICT:
/* 125 */         return (message != null) ? new Conflict(message, statusText, headers, body, charset) : new Conflict(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case GONE:
/* 129 */         return (message != null) ? new Gone(message, statusText, headers, body, charset) : new Gone(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case UNSUPPORTED_MEDIA_TYPE:
/* 133 */         return (message != null) ? new UnsupportedMediaType(message, statusText, headers, body, charset) : new UnsupportedMediaType(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case TOO_MANY_REQUESTS:
/* 137 */         return (message != null) ? new TooManyRequests(message, statusText, headers, body, charset) : new TooManyRequests(statusText, headers, body, charset);
/*     */ 
/*     */       
/*     */       case UNPROCESSABLE_ENTITY:
/* 141 */         return (message != null) ? new UnprocessableEntity(message, statusText, headers, body, charset) : new UnprocessableEntity(statusText, headers, body, charset);
/*     */     } 
/*     */ 
/*     */     
/* 145 */     return (message != null) ? new HttpClientErrorException(message, statusCode, statusText, headers, body, charset) : new HttpClientErrorException(statusCode, statusText, headers, body, charset);
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
/*     */   public static final class BadRequest
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private BadRequest(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 162 */       super(HttpStatus.BAD_REQUEST, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private BadRequest(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 168 */       super(message, HttpStatus.BAD_REQUEST, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Unauthorized
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private Unauthorized(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 180 */       super(HttpStatus.UNAUTHORIZED, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Unauthorized(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 186 */       super(message, HttpStatus.UNAUTHORIZED, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Forbidden
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private Forbidden(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 198 */       super(HttpStatus.FORBIDDEN, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Forbidden(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 204 */       super(message, HttpStatus.FORBIDDEN, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class NotFound
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private NotFound(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 216 */       super(HttpStatus.NOT_FOUND, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private NotFound(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 222 */       super(message, HttpStatus.NOT_FOUND, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class MethodNotAllowed
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private MethodNotAllowed(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 234 */       super(HttpStatus.METHOD_NOT_ALLOWED, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private MethodNotAllowed(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 240 */       super(message, HttpStatus.METHOD_NOT_ALLOWED, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class NotAcceptable
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private NotAcceptable(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 252 */       super(HttpStatus.NOT_ACCEPTABLE, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private NotAcceptable(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 258 */       super(message, HttpStatus.NOT_ACCEPTABLE, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Conflict
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private Conflict(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 270 */       super(HttpStatus.CONFLICT, statusText, headers, body, charset);
/*     */     }
/*     */     
/*     */     private Conflict(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 274 */       super(message, HttpStatus.CONFLICT, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Gone
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private Gone(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 286 */       super(HttpStatus.GONE, statusText, headers, body, charset);
/*     */     }
/*     */     
/*     */     private Gone(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 290 */       super(message, HttpStatus.GONE, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class UnsupportedMediaType
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private UnsupportedMediaType(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 302 */       super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private UnsupportedMediaType(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 308 */       super(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class UnprocessableEntity
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private UnprocessableEntity(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 320 */       super(HttpStatus.UNPROCESSABLE_ENTITY, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private UnprocessableEntity(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 326 */       super(message, HttpStatus.UNPROCESSABLE_ENTITY, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class TooManyRequests
/*     */     extends HttpClientErrorException
/*     */   {
/*     */     private TooManyRequests(String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 338 */       super(HttpStatus.TOO_MANY_REQUESTS, statusText, headers, body, charset);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private TooManyRequests(String message, String statusText, HttpHeaders headers, byte[] body, @Nullable Charset charset) {
/* 344 */       super(message, HttpStatus.TOO_MANY_REQUESTS, statusText, headers, body, charset);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/HttpClientErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */