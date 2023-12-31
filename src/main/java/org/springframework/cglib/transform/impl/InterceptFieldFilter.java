package org.springframework.cglib.transform.impl;

import org.springframework.asm.Type;

public interface InterceptFieldFilter {
  boolean acceptRead(Type paramType, String paramString);
  
  boolean acceptWrite(Type paramType, String paramString);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/cglib/transform/impl/InterceptFieldFilter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */