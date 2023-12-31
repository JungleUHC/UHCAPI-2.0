package org.springframework.beans.factory;

import org.springframework.lang.Nullable;

public interface HierarchicalBeanFactory extends BeanFactory {
  @Nullable
  BeanFactory getParentBeanFactory();
  
  boolean containsLocalBean(String paramString);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/HierarchicalBeanFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */