/*    */ package org.springframework.web.server;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.http.HttpStatus;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.util.CollectionUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NotAcceptableStatusException
/*    */   extends ResponseStatusException
/*    */ {
/*    */   private final List<MediaType> supportedMediaTypes;
/*    */   
/*    */   public NotAcceptableStatusException(String reason) {
/* 44 */     super(HttpStatus.NOT_ACCEPTABLE, reason);
/* 45 */     this.supportedMediaTypes = Collections.emptyList();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public NotAcceptableStatusException(List<MediaType> supportedMediaTypes) {
/* 52 */     super(HttpStatus.NOT_ACCEPTABLE, "Could not find acceptable representation");
/* 53 */     this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, String> getHeaders() {
/* 64 */     return getResponseHeaders().toSingleValueMap();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpHeaders getResponseHeaders() {
/* 73 */     if (CollectionUtils.isEmpty(this.supportedMediaTypes)) {
/* 74 */       return HttpHeaders.EMPTY;
/*    */     }
/* 76 */     HttpHeaders headers = new HttpHeaders();
/* 77 */     headers.setAccept(this.supportedMediaTypes);
/* 78 */     return headers;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<MediaType> getSupportedMediaTypes() {
/* 86 */     return this.supportedMediaTypes;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/NotAcceptableStatusException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */