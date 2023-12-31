/*    */ package org.springframework.beans.factory.annotation;
/*    */ 
/*    */ import org.springframework.beans.factory.wiring.BeanWiringInfo;
/*    */ import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.util.ClassUtils;
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
/*    */ public class AnnotationBeanWiringInfoResolver
/*    */   implements BeanWiringInfoResolver
/*    */ {
/*    */   @Nullable
/*    */   public BeanWiringInfo resolveWiringInfo(Object beanInstance) {
/* 43 */     Assert.notNull(beanInstance, "Bean instance must not be null");
/* 44 */     Configurable annotation = beanInstance.getClass().<Configurable>getAnnotation(Configurable.class);
/* 45 */     return (annotation != null) ? buildWiringInfo(beanInstance, annotation) : null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected BeanWiringInfo buildWiringInfo(Object beanInstance, Configurable annotation) {
/* 55 */     if (!Autowire.NO.equals(annotation.autowire()))
/*    */     {
/* 57 */       return new BeanWiringInfo(annotation.autowire().value(), annotation.dependencyCheck());
/*    */     }
/* 59 */     if (!annotation.value().isEmpty())
/*    */     {
/* 61 */       return new BeanWiringInfo(annotation.value(), false);
/*    */     }
/*    */ 
/*    */     
/* 65 */     return new BeanWiringInfo(getDefaultBeanName(beanInstance), true);
/*    */   }
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
/*    */   protected String getDefaultBeanName(Object beanInstance) {
/* 78 */     return ClassUtils.getUserClass(beanInstance).getName();
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/annotation/AnnotationBeanWiringInfoResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */