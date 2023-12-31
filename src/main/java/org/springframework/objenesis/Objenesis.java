package org.springframework.objenesis;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

public interface Objenesis {
  <T> T newInstance(Class<T> paramClass);
  
  <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> paramClass);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/objenesis/Objenesis.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */