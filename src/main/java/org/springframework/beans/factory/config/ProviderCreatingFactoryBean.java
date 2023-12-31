/*    */ package org.springframework.beans.factory.config;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import javax.inject.Provider;
/*    */ import org.springframework.beans.BeansException;
/*    */ import org.springframework.beans.factory.BeanFactory;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.Assert;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProviderCreatingFactoryBean
/*    */   extends AbstractFactoryBean<Provider<Object>>
/*    */ {
/*    */   @Nullable
/*    */   private String targetBeanName;
/*    */   
/*    */   public void setTargetBeanName(String targetBeanName) {
/* 58 */     this.targetBeanName = targetBeanName;
/*    */   }
/*    */ 
/*    */   
/*    */   public void afterPropertiesSet() throws Exception {
/* 63 */     Assert.hasText(this.targetBeanName, "Property 'targetBeanName' is required");
/* 64 */     super.afterPropertiesSet();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<?> getObjectType() {
/* 70 */     return Provider.class;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Provider<Object> createInstance() {
/* 75 */     BeanFactory beanFactory = getBeanFactory();
/* 76 */     Assert.state((beanFactory != null), "No BeanFactory available");
/* 77 */     Assert.state((this.targetBeanName != null), "No target bean name specified");
/* 78 */     return new TargetBeanProvider(beanFactory, this.targetBeanName);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static class TargetBeanProvider
/*    */     implements Provider<Object>, Serializable
/*    */   {
/*    */     private final BeanFactory beanFactory;
/*    */ 
/*    */     
/*    */     private final String targetBeanName;
/*    */ 
/*    */     
/*    */     public TargetBeanProvider(BeanFactory beanFactory, String targetBeanName) {
/* 93 */       this.beanFactory = beanFactory;
/* 94 */       this.targetBeanName = targetBeanName;
/*    */     }
/*    */ 
/*    */     
/*    */     public Object get() throws BeansException {
/* 99 */       return this.beanFactory.getBean(this.targetBeanName);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/ProviderCreatingFactoryBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */