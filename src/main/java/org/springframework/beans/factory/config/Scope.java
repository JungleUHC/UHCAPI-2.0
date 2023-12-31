package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;

public interface Scope {
  Object get(String paramString, ObjectFactory<?> paramObjectFactory);
  
  @Nullable
  Object remove(String paramString);
  
  void registerDestructionCallback(String paramString, Runnable paramRunnable);
  
  @Nullable
  Object resolveContextualObject(String paramString);
  
  @Nullable
  String getConversationId();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/Scope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */