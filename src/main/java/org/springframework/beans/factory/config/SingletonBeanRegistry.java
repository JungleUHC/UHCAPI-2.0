package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;

public interface SingletonBeanRegistry {
  void registerSingleton(String paramString, Object paramObject);
  
  @Nullable
  Object getSingleton(String paramString);
  
  boolean containsSingleton(String paramString);
  
  String[] getSingletonNames();
  
  int getSingletonCount();
  
  Object getSingletonMutex();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/SingletonBeanRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */