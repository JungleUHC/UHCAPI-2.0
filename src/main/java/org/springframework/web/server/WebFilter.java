package org.springframework.web.server;

import reactor.core.publisher.Mono;

public interface WebFilter {
  Mono<Void> filter(ServerWebExchange paramServerWebExchange, WebFilterChain paramWebFilterChain);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/WebFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */