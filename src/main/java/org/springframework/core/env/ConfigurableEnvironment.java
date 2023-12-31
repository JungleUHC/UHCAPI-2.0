package org.springframework.core.env;

import java.util.Map;

public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {
  void setActiveProfiles(String... paramVarArgs);
  
  void addActiveProfile(String paramString);
  
  void setDefaultProfiles(String... paramVarArgs);
  
  MutablePropertySources getPropertySources();
  
  Map<String, Object> getSystemProperties();
  
  Map<String, Object> getSystemEnvironment();
  
  void merge(ConfigurableEnvironment paramConfigurableEnvironment);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/env/ConfigurableEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */