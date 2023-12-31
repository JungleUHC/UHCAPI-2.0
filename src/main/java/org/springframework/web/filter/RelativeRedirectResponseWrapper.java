/*    */ package org.springframework.web.filter;
/*    */ 
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import javax.servlet.http.HttpServletResponseWrapper;
/*    */ import org.springframework.http.HttpStatus;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.web.util.WebUtils;
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
/*    */ final class RelativeRedirectResponseWrapper
/*    */   extends HttpServletResponseWrapper
/*    */ {
/*    */   private final HttpStatus redirectStatus;
/*    */   
/*    */   private RelativeRedirectResponseWrapper(HttpServletResponse response, HttpStatus redirectStatus) {
/* 40 */     super(response);
/* 41 */     Assert.notNull(redirectStatus, "'redirectStatus' is required");
/* 42 */     this.redirectStatus = redirectStatus;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void sendRedirect(String location) {
/* 48 */     setStatus(this.redirectStatus.value());
/* 49 */     setHeader("Location", location);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static HttpServletResponse wrapIfNecessary(HttpServletResponse response, HttpStatus redirectStatus) {
/* 57 */     RelativeRedirectResponseWrapper wrapper = (RelativeRedirectResponseWrapper)WebUtils.getNativeResponse((ServletResponse)response, RelativeRedirectResponseWrapper.class);
/*    */     
/* 59 */     return (wrapper != null) ? response : (HttpServletResponse)new RelativeRedirectResponseWrapper(response, redirectStatus);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/RelativeRedirectResponseWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */