/*    */ package org.springframework.web.context.support;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.springframework.context.support.LiveBeansView;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ 
/*    */ @Deprecated
/*    */ public class LiveBeansViewServlet
/*    */   extends HttpServlet
/*    */ {
/*    */   @Nullable
/*    */   private LiveBeansView liveBeansView;
/*    */   
/*    */   public void init() throws ServletException {
/* 51 */     this.liveBeansView = buildLiveBeansView();
/*    */   }
/*    */   
/*    */   protected LiveBeansView buildLiveBeansView() {
/* 55 */     return new ServletContextLiveBeansView(getServletContext());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 63 */     Assert.state((this.liveBeansView != null), "No LiveBeansView available");
/* 64 */     String content = this.liveBeansView.getSnapshotAsJson();
/* 65 */     response.setContentType("application/json");
/* 66 */     response.setContentLength(content.length());
/* 67 */     response.getWriter().write(content);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/LiveBeansViewServlet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */