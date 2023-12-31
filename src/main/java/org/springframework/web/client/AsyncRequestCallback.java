package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.AsyncClientHttpRequest;

@FunctionalInterface
@Deprecated
public interface AsyncRequestCallback {
  void doWithRequest(AsyncClientHttpRequest paramAsyncClientHttpRequest) throws IOException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/client/AsyncRequestCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */