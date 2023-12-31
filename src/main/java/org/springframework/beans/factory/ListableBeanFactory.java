package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public interface ListableBeanFactory extends BeanFactory {
  boolean containsBeanDefinition(String paramString);
  
  int getBeanDefinitionCount();
  
  String[] getBeanDefinitionNames();
  
  <T> ObjectProvider<T> getBeanProvider(Class<T> paramClass, boolean paramBoolean);
  
  <T> ObjectProvider<T> getBeanProvider(ResolvableType paramResolvableType, boolean paramBoolean);
  
  String[] getBeanNamesForType(ResolvableType paramResolvableType);
  
  String[] getBeanNamesForType(ResolvableType paramResolvableType, boolean paramBoolean1, boolean paramBoolean2);
  
  String[] getBeanNamesForType(@Nullable Class<?> paramClass);
  
  String[] getBeanNamesForType(@Nullable Class<?> paramClass, boolean paramBoolean1, boolean paramBoolean2);
  
  <T> Map<String, T> getBeansOfType(@Nullable Class<T> paramClass) throws BeansException;
  
  <T> Map<String, T> getBeansOfType(@Nullable Class<T> paramClass, boolean paramBoolean1, boolean paramBoolean2) throws BeansException;
  
  String[] getBeanNamesForAnnotation(Class<? extends Annotation> paramClass);
  
  Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> paramClass) throws BeansException;
  
  @Nullable
  <A extends Annotation> A findAnnotationOnBean(String paramString, Class<A> paramClass) throws NoSuchBeanDefinitionException;
  
  @Nullable
  <A extends Annotation> A findAnnotationOnBean(String paramString, Class<A> paramClass, boolean paramBoolean) throws NoSuchBeanDefinitionException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/ListableBeanFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */