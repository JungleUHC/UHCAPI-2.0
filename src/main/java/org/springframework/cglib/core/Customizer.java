package org.springframework.cglib.core;

import org.springframework.asm.Type;

public interface Customizer extends KeyFactoryCustomizer {
  void customize(CodeEmitter paramCodeEmitter, Type paramType);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/cglib/core/Customizer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */