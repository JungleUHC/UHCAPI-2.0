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
/*    */ 
/*    */ 
/*    */ public class MissingPathVariableException
/*    */   extends MissingRequestValueException
/*    */ {
/*    */   private final String variableName;
/*    */   private final MethodParameter parameter;
/*    */   
/*    */   public MissingPathVariableException(String variableName, MethodParameter parameter) {
/* 46 */     this(variableName, parameter, false);
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
/*    */   public MissingPathVariableException(String variableName, MethodParameter parameter, boolean missingAfterConversion) {
/* 59 */     super("", missingAfterConversion);
/* 60 */     this.variableName = variableName;
/* 61 */     this.parameter = parameter;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 67 */     return "Required URI template variable '" + this.variableName + "' for method parameter type " + this.parameter
/* 68 */       .getNestedParameterType().getSimpleName() + " is " + (
/* 69 */       isMissingAfterConversion() ? "present but converted to null" : "not present");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String getVariableName() {
/* 76 */     return this.variableName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final MethodParameter getParameter() {
/* 83 */     return this.parameter;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/MissingPathVariableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */