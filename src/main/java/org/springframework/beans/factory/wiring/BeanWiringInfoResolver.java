package org.springframework.beans.factory.wiring;

import org.springframework.lang.Nullable;

public interface BeanWiringInfoResolver {
  @Nullable
  BeanWiringInfo resolveWiringInfo(Object paramObject);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/wiring/BeanWiringInfoResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */