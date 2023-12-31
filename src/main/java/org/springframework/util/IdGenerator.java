package org.springframework.util;

import java.util.UUID;

@FunctionalInterface
public interface IdGenerator {
  UUID generateId();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/IdGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */