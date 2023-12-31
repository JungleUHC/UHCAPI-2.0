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
/*    */ public class MissingMatrixVariableException
/*    */   extends MissingRequestValueException
/*    */ {
/*    */   private final String variableName;
/*    */   private final MethodParameter parameter;
/*    */   
/*    */   public MissingMatrixVariableException(String variableName, MethodParameter parameter) {
/* 44 */     this(variableName, parameter, false);
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
/*    */   public MissingMatrixVariableException(String variableName, MethodParameter parameter, boolean missingAfterConversion) {
/* 57 */     super("", missingAfterConversion);
/* 58 */     this.variableName = variableName;
/* 59 */     this.parameter = parameter;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 65 */     return "Required matrix variable '" + this.variableName + "' for method parameter type " + this.parameter
/* 66 */       .getNestedParameterType().getSimpleName() + " is " + (
/* 67 */       isMissingAfterConversion() ? "present but converted to null" : "not present");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String getVariableName() {
/* 74 */     return this.variableName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final MethodParameter getParameter() {
/* 81 */     return this.parameter;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/MissingMatrixVariableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */