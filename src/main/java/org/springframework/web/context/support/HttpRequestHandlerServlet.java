/*    */ package org.springframework.web.context.support;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.springframework.context.i18n.LocaleContextHolder;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.util.StringUtils;
/*    */ import org.springframework.web.HttpRequestHandler;
/*    */ import org.springframework.web.HttpRequestMethodNotSupportedException;
/*    */ import org.springframework.web.context.WebApplicationContext;
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
/*    */ 
/*    */ 
/*    */ public class HttpRequestHandlerServlet
/*    */   extends HttpServlet
/*    */ {
/*    */   @Nullable
/*    */   private HttpRequestHandler target;
/*    */   
/*    */   public void init() throws ServletException {
/* 60 */     WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
/* 61 */     this.target = (HttpRequestHandler)wac.getBean(getServletName(), HttpRequestHandler.class);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 69 */     Assert.state((this.target != null), "No HttpRequestHandler available");
/*    */     
/* 71 */     LocaleContextHolder.setLocale(request.getLocale());
/*    */     try {
/* 73 */       this.target.handleRequest(request, response);
/*    */     }
/* 75 */     catch (HttpRequestMethodNotSupportedException ex) {
/* 76 */       String[] supportedMethods = ex.getSupportedMethods();
/* 77 */       if (supportedMethods != null) {
/* 78 */         response.setHeader("Allow", StringUtils.arrayToDelimitedString((Object[])supportedMethods, ", "));
/*    */       }
/* 80 */       response.sendError(405, ex.getMessage());
/*    */     } finally {
/*    */       
/* 83 */       LocaleContextHolder.resetLocaleContext();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/HttpRequestHandlerServlet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */