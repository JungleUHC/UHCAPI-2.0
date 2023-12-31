package org.springframework.util.concurrent;

@FunctionalInterface
public interface FailureCallback {
  void onFailure(Throwable paramThrowable);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/concurrent/FailureCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */