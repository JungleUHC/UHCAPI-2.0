/*    */ package org.springframework.web.bind;
/*    */ 
/*    */ import org.springframework.core.MethodParameter;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MissingRequestCookieException
/*    */   extends MissingRequestValueException
/*    */ {
/*    */   private final String cookieName;
/*    */   private final MethodParameter parameter;
/*    */   
/*    */   public MissingRequestCookieException(String cookieName, MethodParameter parameter) {
/* 44 */     this(cookieName, parameter, false);
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
/*    */   public MissingRequestCookieException(String cookieName, MethodParameter parameter, boolean missingAfterConversion) {
/* 57 */     super("", missingAfterConversion);
/* 58 */     this.cookieName = cookieName;
/* 59 */     this.parameter = parameter;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 65 */     return "Required cookie '" + this.cookieName + "' for method parameter type " + this.parameter
/* 66 */       .getNestedParameterType().getSimpleName() + " is " + (
/* 67 */       isMissingAfterConversion() ? "present but converted to null" : "not present");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String getCookieName() {
/* 74 */     return this.cookieName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final MethodParameter getParameter() {
/* 81 */     return this.parameter;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/MissingRequestCookieException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */