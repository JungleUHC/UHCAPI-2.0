package org.springframework.web.server;

import reactor.core.publisher.Mono;

public interface WebFilterChain {
  Mono<Void> filter(ServerWebExchange paramServerWebExchange);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/WebFilterChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */