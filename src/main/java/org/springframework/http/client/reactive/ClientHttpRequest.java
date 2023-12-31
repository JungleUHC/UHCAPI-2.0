package org.springframework.http.client.reactive;

import java.net.URI;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.util.MultiValueMap;

public interface ClientHttpRequest extends ReactiveHttpOutputMessage {
  HttpMethod getMethod();
  
  URI getURI();
  
  MultiValueMap<String, HttpCookie> getCookies();
  
  <T> T getNativeRequest();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/ClientHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */