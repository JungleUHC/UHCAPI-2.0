/*    */ package org.springframework.beans.factory.support;
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
/*    */ class ImplicitlyAppearedSingletonException
/*    */   extends IllegalStateException
/*    */ {
/*    */   public ImplicitlyAppearedSingletonException() {
/* 31 */     super("About-to-be-created singleton instance implicitly appeared through the creation of the factory bean that its bean definition points to");
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/ImplicitlyAppearedSingletonException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */