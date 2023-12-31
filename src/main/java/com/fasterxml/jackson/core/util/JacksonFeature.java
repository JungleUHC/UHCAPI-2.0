package com.fasterxml.jackson.core.util;

public interface JacksonFeature {
  boolean enabledByDefault();
  
  int getMask();
  
  boolean enabledIn(int paramInt);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/JacksonFeature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */