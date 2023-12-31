package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ResponseExtractor<T> {
  @Nullable
  T extractData(ClientHttpResponse paramClientHttpResponse) throws IOException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/ResponseExtractor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */