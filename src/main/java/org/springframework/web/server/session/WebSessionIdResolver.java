package org.springframework.web.server.session;

import java.util.List;
import org.springframework.web.server.ServerWebExchange;

public interface WebSessionIdResolver {
  List<String> resolveSessionIds(ServerWebExchange paramServerWebExchange);
  
  void setSessionId(ServerWebExchange paramServerWebExchange, String paramString);
  
  void expireSession(ServerWebExchange paramServerWebExchange);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/session/WebSessionIdResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */