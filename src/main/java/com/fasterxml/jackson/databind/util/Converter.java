package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public interface Converter<IN, OUT> {
  OUT convert(IN paramIN);
  
  JavaType getInputType(TypeFactory paramTypeFactory);
  
  JavaType getOutputType(TypeFactory paramTypeFactory);
  
  public static abstract class None implements Converter<Object, Object> {}
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/Converter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */