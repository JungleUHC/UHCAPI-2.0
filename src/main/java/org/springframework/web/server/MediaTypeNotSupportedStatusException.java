/*    */ package org.springframework.web.server;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.springframework.http.HttpStatus;
/*    */ import org.springframework.http.MediaType;
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
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class MediaTypeNotSupportedStatusException
/*    */   extends ResponseStatusException
/*    */ {
/*    */   private final List<MediaType> supportedMediaTypes;
/*    */   
/*    */   public MediaTypeNotSupportedStatusException(String reason) {
/* 44 */     super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason);
/* 45 */     this.supportedMediaTypes = Collections.emptyList();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MediaTypeNotSupportedStatusException(List<MediaType> supportedMediaTypes) {
/* 52 */     super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type", (Throwable)null);
/* 53 */     this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<MediaType> getSupportedMediaTypes() {
/* 62 */     return this.supportedMediaTypes;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/MediaTypeNotSupportedStatusException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */