/*    */ package org.springframework.beans.factory.support;
/*    */ 
/*    */ import org.springframework.beans.factory.config.BeanDefinitionHolder;
/*    */ import org.springframework.beans.factory.config.DependencyDescriptor;
/*    */ import org.springframework.lang.Nullable;
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
/*    */ public class SimpleAutowireCandidateResolver
/*    */   implements AutowireCandidateResolver
/*    */ {
/* 37 */   public static final SimpleAutowireCandidateResolver INSTANCE = new SimpleAutowireCandidateResolver();
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
/* 42 */     return bdHolder.getBeanDefinition().isAutowireCandidate();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isRequired(DependencyDescriptor descriptor) {
/* 47 */     return descriptor.isRequired();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasQualifier(DependencyDescriptor descriptor) {
/* 52 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Object getSuggestedValue(DependencyDescriptor descriptor) {
/* 58 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
/* 64 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AutowireCandidateResolver cloneIfNecessary() {
/* 73 */     return this;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/support/SimpleAutowireCandidateResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */