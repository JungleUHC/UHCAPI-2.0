package com.fasterxml.jackson.databind.util;

import java.lang.annotation.Annotation;

public interface Annotations {
  <A extends Annotation> A get(Class<A> paramClass);
  
  boolean has(Class<?> paramClass);
  
  boolean hasOneOf(Class<? extends Annotation>[] paramArrayOfClass);
  
  int size();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/Annotations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */