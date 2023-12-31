package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;

@FunctionalInterface
public interface ClientHttpRequestInterceptor {
  ClientHttpResponse intercept(HttpRequest paramHttpRequest, byte[] paramArrayOfbyte, ClientHttpRequestExecution paramClientHttpRequestExecution) throws IOException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/ClientHttpRequestInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */