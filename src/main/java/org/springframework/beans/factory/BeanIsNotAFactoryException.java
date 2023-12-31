/*    */ package org.springframework.beans.factory;
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
/*    */ public class BeanIsNotAFactoryException
/*    */   extends BeanNotOfRequiredTypeException
/*    */ {
/*    */   public BeanIsNotAFactoryException(String name, Class<?> actualType) {
/* 38 */     super(name, FactoryBean.class, actualType);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/BeanIsNotAFactoryException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */