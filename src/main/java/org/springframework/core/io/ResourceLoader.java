package org.springframework.core.io;

import org.springframework.lang.Nullable;

public interface ResourceLoader {
  public static final String CLASSPATH_URL_PREFIX = "classpath:";
  
  Resource getResource(String paramString);
  
  @Nullable
  ClassLoader getClassLoader();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/io/ResourceLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */