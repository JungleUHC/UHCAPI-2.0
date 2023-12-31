package org.springframework.http.client;

@FunctionalInterface
public interface ClientHttpRequestInitializer {
  void initialize(ClientHttpRequest paramClientHttpRequest);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/ClientHttpRequestInitializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */