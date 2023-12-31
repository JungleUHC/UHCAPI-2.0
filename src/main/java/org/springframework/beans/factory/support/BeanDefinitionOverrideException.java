/*    */ package org.springframework.beans.factory.support;
/*    */ 
/*    */ import org.springframework.beans.factory.BeanDefinitionStoreException;
/*    */ import org.springframework.beans.factory.config.BeanDefinition;
/*    */ import org.springframework.lang.NonNull;
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
/*    */ public class BeanDefinitionOverrideException
/*    */   extends BeanDefinitionStoreException
/*    */ {
/*    */   private final BeanDefinition beanDefinition;
/*    */   private final BeanDefinition existingDefinition;
/*    */   
/*    */   public BeanDefinitionOverrideException(String beanName, BeanDefinition beanDefinition, BeanDefinition existingDefinition) {
/* 50 */     super(beanDefinition.getResourceDescription(), beanName, "Cannot register bean definition [" + beanDefinition + "] for bean '" + beanName + "': There is already [" + existingDefinition + "] bound.");
/*    */ 
/*    */     
/* 53 */     this.beanDefinition = beanDefinition;
/* 54 */     this.existingDefinition = existingDefinition;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NonNull
/*    */   public String getResourceDescription() {
/* 64 */     return String.valueOf(super.getResourceDescription());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NonNull
/*    */   public String getBeanName() {
/* 73 */     return String.valueOf(super.getBeanName());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BeanDefinition getBeanDefinition() {
/* 81 */     return this.beanDefinition;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BeanDefinition getExistingDefinition() {
/* 89 */     return this.existingDefinition;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/BeanDefinitionOverrideException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */