package com.fasterxml.jackson.databind.cfg;

public interface ConfigFeature {
  boolean enabledByDefault();
  
  int getMask();
  
  boolean enabledIn(int paramInt);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/cfg/ConfigFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */