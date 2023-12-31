/*    */ package org.springframework.web.util;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import javax.servlet.http.HttpSessionEvent;
/*    */ import javax.servlet.http.HttpSessionListener;
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
/*    */ 
/*    */ public class HttpSessionMutexListener
/*    */   implements HttpSessionListener
/*    */ {
/*    */   public void sessionCreated(HttpSessionEvent event) {
/* 49 */     event.getSession().setAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE, new Mutex());
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionDestroyed(HttpSessionEvent event) {
/* 54 */     event.getSession().removeAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE);
/*    */   }
/*    */   
/*    */   private static class Mutex implements Serializable {
/*    */     private Mutex() {}
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/HttpSessionMutexListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */