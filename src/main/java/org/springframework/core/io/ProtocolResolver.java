package org.springframework.core.io;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ProtocolResolver {
  @Nullable
  Resource resolve(String paramString, ResourceLoader paramResourceLoader);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/io/ProtocolResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */