/*    */ package org.springframework.web.bind.support;
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
/*    */ public class SimpleSessionStatus
/*    */   implements SessionStatus
/*    */ {
/*    */   private boolean complete = false;
/*    */   
/*    */   public void setComplete() {
/* 33 */     this.complete = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isComplete() {
/* 38 */     return this.complete;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/support/SimpleSessionStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */