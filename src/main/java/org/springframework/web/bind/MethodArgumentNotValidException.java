/*    */ package org.springframework.web.bind;
/*    */ 
/*    */ import org.springframework.core.MethodParameter;
/*    */ import org.springframework.validation.BindException;
/*    */ import org.springframework.validation.BindingResult;
/*    */ import org.springframework.validation.ObjectError;
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
/*    */ public class MethodArgumentNotValidException
/*    */   extends BindException
/*    */ {
/*    */   private final MethodParameter parameter;
/*    */   
/*    */   public MethodArgumentNotValidException(MethodParameter parameter, BindingResult bindingResult) {
/* 44 */     super(bindingResult);
/* 45 */     this.parameter = parameter;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final MethodParameter getParameter() {
/* 53 */     return this.parameter;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 60 */     StringBuilder sb = (new StringBuilder("Validation failed for argument [")).append(this.parameter.getParameterIndex()).append("] in ").append(this.parameter.getExecutable().toGenericString());
/* 61 */     BindingResult bindingResult = getBindingResult();
/* 62 */     if (bindingResult.getErrorCount() > 1) {
/* 63 */       sb.append(" with ").append(bindingResult.getErrorCount()).append(" errors");
/*    */     }
/* 65 */     sb.append(": ");
/* 66 */     for (ObjectError error : bindingResult.getAllErrors()) {
/* 67 */       sb.append('[').append(error).append("] ");
/*    */     }
/* 69 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/MethodArgumentNotValidException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */