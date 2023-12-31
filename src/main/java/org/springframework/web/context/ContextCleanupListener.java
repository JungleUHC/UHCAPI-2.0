/*    */ package org.springframework.web.context;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletContextEvent;
/*    */ import javax.servlet.ServletContextListener;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.springframework.beans.factory.DisposableBean;
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
/*    */ public class ContextCleanupListener
/*    */   implements ServletContextListener
/*    */ {
/* 44 */   private static final Log logger = LogFactory.getLog(ContextCleanupListener.class);
/*    */ 
/*    */ 
/*    */   
/*    */   public void contextInitialized(ServletContextEvent event) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void contextDestroyed(ServletContextEvent event) {
/* 53 */     cleanupAttributes(event.getServletContext());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static void cleanupAttributes(ServletContext servletContext) {
/* 64 */     Enumeration<String> attrNames = servletContext.getAttributeNames();
/* 65 */     while (attrNames.hasMoreElements()) {
/* 66 */       String attrName = attrNames.nextElement();
/* 67 */       if (attrName.startsWith("org.springframework.")) {
/* 68 */         Object attrValue = servletContext.getAttribute(attrName);
/* 69 */         if (attrValue instanceof DisposableBean)
/*    */           try {
/* 71 */             ((DisposableBean)attrValue).destroy();
/*    */           }
/* 73 */           catch (Throwable ex) {
/* 74 */             if (logger.isWarnEnabled())
/* 75 */               logger.warn("Invocation of destroy method failed on ServletContext attribute with name '" + attrName + "'", ex); 
/*    */           }  
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/ContextCleanupListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */