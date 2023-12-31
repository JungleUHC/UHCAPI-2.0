package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;

public interface ResolvableSerializer {
  void resolve(SerializerProvider paramSerializerProvider) throws JsonMappingException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/ResolvableSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */