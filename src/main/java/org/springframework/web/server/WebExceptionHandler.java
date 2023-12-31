package org.springframework.web.server;

import reactor.core.publisher.Mono;

public interface WebExceptionHandler {
  Mono<Void> handle(ServerWebExchange paramServerWebExchange, Throwable paramThrowable);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/WebExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */