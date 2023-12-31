package org.springframework.core.env;

public interface Environment extends PropertyResolver {
  String[] getActiveProfiles();
  
  String[] getDefaultProfiles();
  
  @Deprecated
  boolean acceptsProfiles(String... paramVarArgs);
  
  boolean acceptsProfiles(Profiles paramProfiles);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/env/Environment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */