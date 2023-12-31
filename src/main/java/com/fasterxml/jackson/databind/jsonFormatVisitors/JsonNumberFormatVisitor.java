package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.core.JsonParser;

public interface JsonNumberFormatVisitor extends JsonValueFormatVisitor {
  void numberType(JsonParser.NumberType paramNumberType);
  
  public static class Base extends JsonValueFormatVisitor.Base implements JsonNumberFormatVisitor {
    public void numberType(JsonParser.NumberType type) {}
  }
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsonFormatVisitors/JsonNumberFormatVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */