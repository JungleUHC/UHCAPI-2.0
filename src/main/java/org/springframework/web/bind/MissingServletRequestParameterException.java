/*    */ package org.springframework.web.bind;
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
/*    */ public class MissingServletRequestParameterException
/*    */   extends MissingRequestValueException
/*    */ {
/*    */   private final String parameterName;
/*    */   private final String parameterType;
/*    */   
/*    */   public MissingServletRequestParameterException(String parameterName, String parameterType) {
/* 39 */     this(parameterName, parameterType, false);
/*    */   }
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
/*    */   public MissingServletRequestParameterException(String parameterName, String parameterType, boolean missingAfterConversion) {
/* 52 */     super("", missingAfterConversion);
/* 53 */     this.parameterName = parameterName;
/* 54 */     this.parameterType = parameterType;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 60 */     return "Required request parameter '" + this.parameterName + "' for method parameter type " + this.parameterType + " is " + (
/*    */       
/* 62 */       isMissingAfterConversion() ? "present but converted to null" : "not present");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String getParameterName() {
/* 69 */     return this.parameterName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String getParameterType() {
/* 76 */     return this.parameterType;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/MissingServletRequestParameterException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */