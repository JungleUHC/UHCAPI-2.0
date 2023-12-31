package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.SerializerProvider;

public interface JsonFormatVisitorWithSerializerProvider {
  SerializerProvider getProvider();
  
  void setProvider(SerializerProvider paramSerializerProvider);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsonFormatVisitors/JsonFormatVisitorWithSerializerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */