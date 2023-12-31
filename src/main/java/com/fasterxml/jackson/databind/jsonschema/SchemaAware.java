package com.fasterxml.jackson.databind.jsonschema;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.lang.reflect.Type;

public interface SchemaAware {
  JsonNode getSchema(SerializerProvider paramSerializerProvider, Type paramType) throws JsonMappingException;
  
  JsonNode getSchema(SerializerProvider paramSerializerProvider, Type paramType, boolean paramBoolean) throws JsonMappingException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsonschema/SchemaAware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */