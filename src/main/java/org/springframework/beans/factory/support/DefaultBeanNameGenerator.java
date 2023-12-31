/*    */ package org.springframework.beans.factory.support;
/*    */ 
/*    */ import org.springframework.beans.factory.config.BeanDefinition;
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
/*    */ public class DefaultBeanNameGenerator
/*    */   implements BeanNameGenerator
/*    */ {
/* 35 */   public static final DefaultBeanNameGenerator INSTANCE = new DefaultBeanNameGenerator();
/*    */ 
/*    */ 
/*    */   
/*    */   public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
/* 40 */     return BeanDefinitionReaderUtils.generateBeanName(definition, registry);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/DefaultBeanNameGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */