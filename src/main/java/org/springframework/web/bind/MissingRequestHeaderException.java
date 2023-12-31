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
/*    */ public class MissingRequestHeaderException
/*    */   extends MissingRequestValueException
/*    */ {
/*    */   private final String headerName;
/*    */   private final MethodParameter parameter;
/*    */   
/*    */   public MissingRequestHeaderException(String headerName, MethodParameter parameter) {
/* 44 */     this(headerName, parameter, false);
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
/*    */   public MissingRequestHeaderException(String headerName, MethodParameter parameter, boolean missingAfterConversion) {
/* 57 */     super("", missingAfterConversion);
/* 58 */     this.headerName = headerName;
/* 59 */     this.parameter = parameter;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 65 */     String typeName = this.parameter.getNestedParameterType().getSimpleName();
/* 66 */     return "Required request header '" + this.headerName + "' for method parameter type " + typeName + " is " + (
/* 67 */       isMissingAfterConversion() ? "present but converted to null" : "not present");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String getHeaderName() {
/* 74 */     return this.headerName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final MethodParameter getParameter() {
/* 81 */     return this.parameter;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/MissingRequestHeaderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */