/*    */ package org.springframework.web.filter;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.springframework.http.HttpStatus;
/*    */ import org.springframework.util.Assert;
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
/*    */ public class RelativeRedirectFilter
/*    */   extends OncePerRequestFilter
/*    */ {
/* 46 */   private HttpStatus redirectStatus = HttpStatus.SEE_OTHER;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setRedirectStatus(HttpStatus status) {
/* 55 */     Assert.notNull(status, "Property 'redirectStatus' is required");
/* 56 */     Assert.isTrue(status.is3xxRedirection(), "Not a redirect status code");
/* 57 */     this.redirectStatus = status;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpStatus getRedirectStatus() {
/* 64 */     return this.redirectStatus;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/* 72 */     response = RelativeRedirectResponseWrapper.wrapIfNecessary(response, this.redirectStatus);
/* 73 */     filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/RelativeRedirectFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */