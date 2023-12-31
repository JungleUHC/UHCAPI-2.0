package com.fasterxml.jackson.databind;

import java.io.IOException;

public abstract class KeyDeserializer {
  public abstract Object deserializeKey(String paramString, DeserializationContext paramDeserializationContext) throws IOException;
  
  public static abstract class None extends KeyDeserializer {}
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/KeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */