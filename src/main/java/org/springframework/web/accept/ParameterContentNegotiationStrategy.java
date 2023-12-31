/*    */ package org.springframework.web.accept;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.springframework.http.MediaType;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.web.context.request.NativeWebRequest;
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
/*    */ public class ParameterContentNegotiationStrategy
/*    */   extends AbstractMappingContentNegotiationStrategy
/*    */ {
/* 40 */   private String parameterName = "format";
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ParameterContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
/* 47 */     super(mediaTypes);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setParameterName(String parameterName) {
/* 56 */     Assert.notNull(parameterName, "'parameterName' is required");
/* 57 */     this.parameterName = parameterName;
/*    */   }
/*    */   
/*    */   public String getParameterName() {
/* 61 */     return this.parameterName;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected String getMediaTypeKey(NativeWebRequest request) {
/* 68 */     return request.getParameter(getParameterName());
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/accept/ParameterContentNegotiationStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */