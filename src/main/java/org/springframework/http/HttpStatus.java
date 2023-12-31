/*     */ package org.springframework.http;
/*     */ 
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
/*     */ public enum HttpStatus
/*     */ {
/*  42 */   CONTINUE(100, Series.INFORMATIONAL, "Continue"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   SWITCHING_PROTOCOLS(101, Series.INFORMATIONAL, "Switching Protocols"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   PROCESSING(102, Series.INFORMATIONAL, "Processing"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   CHECKPOINT(103, Series.INFORMATIONAL, "Checkpoint"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   OK(200, Series.SUCCESSFUL, "OK"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   CREATED(201, Series.SUCCESSFUL, "Created"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   ACCEPTED(202, Series.SUCCESSFUL, "Accepted"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  81 */   NON_AUTHORITATIVE_INFORMATION(203, Series.SUCCESSFUL, "Non-Authoritative Information"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  86 */   NO_CONTENT(204, Series.SUCCESSFUL, "No Content"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   RESET_CONTENT(205, Series.SUCCESSFUL, "Reset Content"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   PARTIAL_CONTENT(206, Series.SUCCESSFUL, "Partial Content"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 101 */   MULTI_STATUS(207, Series.SUCCESSFUL, "Multi-Status"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 106 */   ALREADY_REPORTED(208, Series.SUCCESSFUL, "Already Reported"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 111 */   IM_USED(226, Series.SUCCESSFUL, "IM Used"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 119 */   MULTIPLE_CHOICES(300, Series.REDIRECTION, "Multiple Choices"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 124 */   MOVED_PERMANENTLY(301, Series.REDIRECTION, "Moved Permanently"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 129 */   FOUND(302, Series.REDIRECTION, "Found"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 135 */   MOVED_TEMPORARILY(302, Series.REDIRECTION, "Moved Temporarily"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 141 */   SEE_OTHER(303, Series.REDIRECTION, "See Other"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 146 */   NOT_MODIFIED(304, Series.REDIRECTION, "Not Modified"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 152 */   USE_PROXY(305, Series.REDIRECTION, "Use Proxy"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 158 */   TEMPORARY_REDIRECT(307, Series.REDIRECTION, "Temporary Redirect"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 163 */   PERMANENT_REDIRECT(308, Series.REDIRECTION, "Permanent Redirect"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 171 */   BAD_REQUEST(400, Series.CLIENT_ERROR, "Bad Request"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 176 */   UNAUTHORIZED(401, Series.CLIENT_ERROR, "Unauthorized"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 181 */   PAYMENT_REQUIRED(402, Series.CLIENT_ERROR, "Payment Required"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 186 */   FORBIDDEN(403, Series.CLIENT_ERROR, "Forbidden"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 191 */   NOT_FOUND(404, Series.CLIENT_ERROR, "Not Found"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 196 */   METHOD_NOT_ALLOWED(405, Series.CLIENT_ERROR, "Method Not Allowed"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 201 */   NOT_ACCEPTABLE(406, Series.CLIENT_ERROR, "Not Acceptable"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 206 */   PROXY_AUTHENTICATION_REQUIRED(407, Series.CLIENT_ERROR, "Proxy Authentication Required"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 211 */   REQUEST_TIMEOUT(408, Series.CLIENT_ERROR, "Request Timeout"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 216 */   CONFLICT(409, Series.CLIENT_ERROR, "Conflict"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 222 */   GONE(410, Series.CLIENT_ERROR, "Gone"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 228 */   LENGTH_REQUIRED(411, Series.CLIENT_ERROR, "Length Required"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 234 */   PRECONDITION_FAILED(412, Series.CLIENT_ERROR, "Precondition Failed"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 241 */   PAYLOAD_TOO_LARGE(413, Series.CLIENT_ERROR, "Payload Too Large"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 248 */   REQUEST_ENTITY_TOO_LARGE(413, Series.CLIENT_ERROR, "Request Entity Too Large"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 256 */   URI_TOO_LONG(414, Series.CLIENT_ERROR, "URI Too Long"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 262 */   REQUEST_URI_TOO_LONG(414, Series.CLIENT_ERROR, "Request-URI Too Long"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 269 */   UNSUPPORTED_MEDIA_TYPE(415, Series.CLIENT_ERROR, "Unsupported Media Type"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 274 */   REQUESTED_RANGE_NOT_SATISFIABLE(416, Series.CLIENT_ERROR, "Requested range not satisfiable"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 280 */   EXPECTATION_FAILED(417, Series.CLIENT_ERROR, "Expectation Failed"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 285 */   I_AM_A_TEAPOT(418, Series.CLIENT_ERROR, "I'm a teapot"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 291 */   INSUFFICIENT_SPACE_ON_RESOURCE(419, Series.CLIENT_ERROR, "Insufficient Space On Resource"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 298 */   METHOD_FAILURE(420, Series.CLIENT_ERROR, "Method Failure"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 305 */   DESTINATION_LOCKED(421, Series.CLIENT_ERROR, "Destination Locked"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 311 */   UNPROCESSABLE_ENTITY(422, Series.CLIENT_ERROR, "Unprocessable Entity"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 316 */   LOCKED(423, Series.CLIENT_ERROR, "Locked"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 321 */   FAILED_DEPENDENCY(424, Series.CLIENT_ERROR, "Failed Dependency"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 327 */   TOO_EARLY(425, Series.CLIENT_ERROR, "Too Early"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 332 */   UPGRADE_REQUIRED(426, Series.CLIENT_ERROR, "Upgrade Required"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 337 */   PRECONDITION_REQUIRED(428, Series.CLIENT_ERROR, "Precondition Required"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 342 */   TOO_MANY_REQUESTS(429, Series.CLIENT_ERROR, "Too Many Requests"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 347 */   REQUEST_HEADER_FIELDS_TOO_LARGE(431, Series.CLIENT_ERROR, "Request Header Fields Too Large"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 354 */   UNAVAILABLE_FOR_LEGAL_REASONS(451, Series.CLIENT_ERROR, "Unavailable For Legal Reasons"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 362 */   INTERNAL_SERVER_ERROR(500, Series.SERVER_ERROR, "Internal Server Error"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 367 */   NOT_IMPLEMENTED(501, Series.SERVER_ERROR, "Not Implemented"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 372 */   BAD_GATEWAY(502, Series.SERVER_ERROR, "Bad Gateway"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 377 */   SERVICE_UNAVAILABLE(503, Series.SERVER_ERROR, "Service Unavailable"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 382 */   GATEWAY_TIMEOUT(504, Series.SERVER_ERROR, "Gateway Timeout"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 387 */   HTTP_VERSION_NOT_SUPPORTED(505, Series.SERVER_ERROR, "HTTP Version not supported"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 392 */   VARIANT_ALSO_NEGOTIATES(506, Series.SERVER_ERROR, "Variant Also Negotiates"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 397 */   INSUFFICIENT_STORAGE(507, Series.SERVER_ERROR, "Insufficient Storage"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 402 */   LOOP_DETECTED(508, Series.SERVER_ERROR, "Loop Detected"),
/*     */ 
/*     */ 
/*     */   
/* 406 */   BANDWIDTH_LIMIT_EXCEEDED(509, Series.SERVER_ERROR, "Bandwidth Limit Exceeded"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 411 */   NOT_EXTENDED(510, Series.SERVER_ERROR, "Not Extended"),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 416 */   NETWORK_AUTHENTICATION_REQUIRED(511, Series.SERVER_ERROR, "Network Authentication Required");
/*     */   
/*     */   private static final HttpStatus[] VALUES;
/*     */   private final int value;
/*     */   
/*     */   static {
/* 422 */     VALUES = values();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final Series series;
/*     */   
/*     */   private final String reasonPhrase;
/*     */ 
/*     */   
/*     */   HttpStatus(int value, Series series, String reasonPhrase) {
/* 433 */     this.value = value;
/* 434 */     this.series = series;
/* 435 */     this.reasonPhrase = reasonPhrase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int value() {
/* 443 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Series series() {
/* 451 */     return this.series;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getReasonPhrase() {
/* 458 */     return this.reasonPhrase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean is1xxInformational() {
/* 469 */     return (series() == Series.INFORMATIONAL);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean is2xxSuccessful() {
/* 480 */     return (series() == Series.SUCCESSFUL);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean is3xxRedirection() {
/* 491 */     return (series() == Series.REDIRECTION);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean is4xxClientError() {
/* 502 */     return (series() == Series.CLIENT_ERROR);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean is5xxServerError() {
/* 513 */     return (series() == Series.SERVER_ERROR);
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
/*     */   public boolean isError() {
/* 526 */     return (is4xxClientError() || is5xxServerError());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 534 */     return this.value + " " + name();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static HttpStatus resolve(int statusCode) {
/* 561 */     for (HttpStatus status : VALUES) {
/* 562 */       if (status.value == statusCode) {
/* 563 */         return status;
/*     */       }
/*     */     } 
/* 566 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum Series
/*     */   {
/* 576 */     INFORMATIONAL(1),
/* 577 */     SUCCESSFUL(2),
/* 578 */     REDIRECTION(3),
/* 579 */     CLIENT_ERROR(4),
/* 580 */     SERVER_ERROR(5);
/*     */     
/*     */     private final int value;
/*     */     
/*     */     Series(int value) {
/* 585 */       this.value = value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int value() {
/* 592 */       return this.value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public static Series resolve(int statusCode) {
/* 628 */       int seriesCode = statusCode / 100;
/* 629 */       for (Series series : values()) {
/* 630 */         if (series.value == seriesCode) {
/* 631 */           return series;
/*     */         }
/*     */       } 
/* 634 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/HttpStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */