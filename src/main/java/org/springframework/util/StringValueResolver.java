package org.springframework.util;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface StringValueResolver {
  @Nullable
  String resolveStringValue(String paramString);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/StringValueResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */