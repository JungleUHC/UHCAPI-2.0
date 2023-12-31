/*    */ package org.springframework.beans.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import org.springframework.beans.BeanWrapper;
/*    */ import org.springframework.beans.PropertyAccessorFactory;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.ReflectionUtils;
/*    */ import org.springframework.util.StringValueResolver;
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
/*    */ @Deprecated
/*    */ public abstract class AnnotationBeanUtils
/*    */ {
/*    */   public static void copyPropertiesToBean(Annotation ann, Object bean, String... excludedProperties) {
/* 52 */     copyPropertiesToBean(ann, bean, null, excludedProperties);
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
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void copyPropertiesToBean(Annotation ann, Object bean, @Nullable StringValueResolver valueResolver, String... excludedProperties) {
/* 69 */     Set<String> excluded = (excludedProperties.length == 0) ? Collections.<String>emptySet() : new HashSet<>(Arrays.asList(excludedProperties));
/* 70 */     Method[] annotationProperties = ann.annotationType().getDeclaredMethods();
/* 71 */     BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
/* 72 */     for (Method annotationProperty : annotationProperties) {
/* 73 */       String propertyName = annotationProperty.getName();
/* 74 */       if (!excluded.contains(propertyName) && bw.isWritableProperty(propertyName)) {
/* 75 */         Object value = ReflectionUtils.invokeMethod(annotationProperty, ann);
/* 76 */         if (valueResolver != null && value instanceof String) {
/* 77 */           value = valueResolver.resolveStringValue((String)value);
/*    */         }
/* 79 */         bw.setPropertyValue(propertyName, value);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/annotation/AnnotationBeanUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */