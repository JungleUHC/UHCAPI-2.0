package org.springframework.beans.factory.xml;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface NamespaceHandlerResolver {
  @Nullable
  NamespaceHandler resolve(String paramString);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/xml/NamespaceHandlerResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */