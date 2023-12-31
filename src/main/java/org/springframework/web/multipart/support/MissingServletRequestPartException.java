/*    */ package org.springframework.web.multipart.support;
/*    */ 
/*    */ import javax.servlet.ServletException;
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
/*    */ 
/*    */ public class MissingServletRequestPartException
/*    */   extends ServletException
/*    */ {
/*    */   private final String requestPartName;
/*    */   
/*    */   public MissingServletRequestPartException(String requestPartName) {
/* 46 */     super("Required request part '" + requestPartName + "' is not present");
/* 47 */     this.requestPartName = requestPartName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getRequestPartName() {
/* 55 */     return this.requestPartName;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/support/MissingServletRequestPartException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */