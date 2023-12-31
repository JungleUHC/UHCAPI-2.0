/*    */ package org.springframework.beans.factory.parsing;
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
/*    */ public class BeanEntry
/*    */   implements ParseState.Entry
/*    */ {
/*    */   private final String beanDefinitionName;
/*    */   
/*    */   public BeanEntry(String beanDefinitionName) {
/* 35 */     this.beanDefinitionName = beanDefinitionName;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return "Bean '" + this.beanDefinitionName + "'";
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/parsing/BeanEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */