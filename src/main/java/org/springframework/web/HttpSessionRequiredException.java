/*    */ package org.springframework.web;
/*    */ 
/*    */ import javax.servlet.ServletException;
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
/*    */ public class HttpSessionRequiredException
/*    */   extends ServletException
/*    */ {
/*    */   @Nullable
/*    */   private final String expectedAttribute;
/*    */   
/*    */   public HttpSessionRequiredException(String msg) {
/* 41 */     super(msg);
/* 42 */     this.expectedAttribute = null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpSessionRequiredException(String msg, String expectedAttribute) {
/* 52 */     super(msg);
/* 53 */     this.expectedAttribute = expectedAttribute;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getExpectedAttribute() {
/* 63 */     return this.expectedAttribute;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/HttpSessionRequiredException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */