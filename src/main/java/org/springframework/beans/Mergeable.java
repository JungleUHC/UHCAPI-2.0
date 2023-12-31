package org.springframework.beans;

import org.springframework.lang.Nullable;

public interface Mergeable {
  boolean isMergeEnabled();
  
  Object merge(@Nullable Object paramObject);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/Mergeable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */