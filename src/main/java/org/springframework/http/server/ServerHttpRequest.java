package org.springframework.http.server;

import java.net.InetSocketAddress;
import java.security.Principal;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;

public interface ServerHttpRequest extends HttpRequest, HttpInputMessage {
  @Nullable
  Principal getPrincipal();
  
  InetSocketAddress getLocalAddress();
  
  InetSocketAddress getRemoteAddress();
  
  ServerHttpAsyncRequestControl getAsyncRequestControl(ServerHttpResponse paramServerHttpResponse);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/ServerHttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */