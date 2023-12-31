/*    */ package org.springframework.beans.factory.parsing;
/*    */ 
/*    */ import org.springframework.beans.factory.BeanDefinitionStoreException;
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
/*    */ public class BeanDefinitionParsingException
/*    */   extends BeanDefinitionStoreException
/*    */ {
/*    */   public BeanDefinitionParsingException(Problem problem) {
/* 37 */     super(problem.getResourceDescription(), problem.toString(), problem.getRootCause());
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/parsing/BeanDefinitionParsingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */