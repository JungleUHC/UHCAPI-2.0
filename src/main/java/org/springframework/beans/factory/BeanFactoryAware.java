package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

public interface BeanFactoryAware extends Aware {
  void setBeanFactory(BeanFactory paramBeanFactory) throws BeansException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/BeanFactoryAware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */