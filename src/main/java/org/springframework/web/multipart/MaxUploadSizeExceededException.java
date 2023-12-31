/*    */ package org.springframework.web.multipart;
/*    */ 
/*    */ import org.springframework.lang.Nullable;
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
/*    */ public class MaxUploadSizeExceededException
/*    */   extends MultipartException
/*    */ {
/*    */   private final long maxUploadSize;
/*    */   
/*    */   public MaxUploadSizeExceededException(long maxUploadSize) {
/* 40 */     this(maxUploadSize, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MaxUploadSizeExceededException(long maxUploadSize, @Nullable Throwable ex) {
/* 50 */     super("Maximum upload size " + ((maxUploadSize >= 0L) ? ("of " + maxUploadSize + " bytes ") : "") + "exceeded", ex);
/* 51 */     this.maxUploadSize = maxUploadSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getMaxUploadSize() {
/* 60 */     return this.maxUploadSize;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/MaxUploadSizeExceededException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */