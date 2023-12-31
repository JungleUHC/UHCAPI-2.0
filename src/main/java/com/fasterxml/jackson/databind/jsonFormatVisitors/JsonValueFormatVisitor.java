package com.fasterxml.jackson.databind.jsonFormatVisitors;

import java.util.Set;

public interface JsonValueFormatVisitor {
  void format(JsonValueFormat paramJsonValueFormat);
  
  void enumTypes(Set<String> paramSet);
  
  public static class Base implements JsonValueFormatVisitor {
    public void format(JsonValueFormat format) {}
    
    public void enumTypes(Set<String> enums) {}
  }
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsonFormatVisitors/JsonValueFormatVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */