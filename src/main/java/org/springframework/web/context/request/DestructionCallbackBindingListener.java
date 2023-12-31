/*    */ package org.springframework.web.context.request;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import javax.servlet.http.HttpSessionBindingEvent;
/*    */ import javax.servlet.http.HttpSessionBindingListener;
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
/*    */ public class DestructionCallbackBindingListener
/*    */   implements HttpSessionBindingListener, Serializable
/*    */ {
/*    */   private final Runnable destructionCallback;
/*    */   
/*    */   public DestructionCallbackBindingListener(Runnable destructionCallback) {
/* 45 */     this.destructionCallback = destructionCallback;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void valueBound(HttpSessionBindingEvent event) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void valueUnbound(HttpSessionBindingEvent event) {
/* 55 */     this.destructionCallback.run();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/DestructionCallbackBindingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */