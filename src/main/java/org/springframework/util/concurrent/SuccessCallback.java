package org.springframework.util.concurrent;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface SuccessCallback<T> {
  void onSuccess(@Nullable T paramT);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/concurrent/SuccessCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */