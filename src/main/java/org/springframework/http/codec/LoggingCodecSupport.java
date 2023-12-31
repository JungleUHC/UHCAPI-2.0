/*    */ package org.springframework.http.codec;
/*    */ 
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.springframework.http.HttpLogging;
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
/*    */ public class LoggingCodecSupport
/*    */ {
/* 34 */   protected final Log logger = HttpLogging.forLogName(getClass());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean enableLoggingRequestDetails = false;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setEnableLoggingRequestDetails(boolean enable) {
/* 47 */     this.enableLoggingRequestDetails = enable;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isEnableLoggingRequestDetails() {
/* 55 */     return this.enableLoggingRequestDetails;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/codec/LoggingCodecSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */