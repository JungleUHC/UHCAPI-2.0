package org.springframework.beans.factory.parsing;

public class EmptyReaderEventListener implements ReaderEventListener {
  public void defaultsRegistered(DefaultsDefinition defaultsDefinition) {}
  
  public void componentRegistered(ComponentDefinition componentDefinition) {}
  
  public void aliasRegistered(AliasDefinition aliasDefinition) {}
  
  public void importProcessed(ImportDefinition importDefinition) {}
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/parsing/EmptyReaderEventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */