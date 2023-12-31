package org.springframework.util.backoff;

@FunctionalInterface
public interface BackOff {
  BackOffExecution start();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/backoff/BackOff.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */