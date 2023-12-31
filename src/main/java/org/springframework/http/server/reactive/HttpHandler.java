package org.springframework.http.server.reactive;

import reactor.core.publisher.Mono;

public interface HttpHandler {
  Mono<Void> handle(ServerHttpRequest paramServerHttpRequest, ServerHttpResponse paramServerHttpResponse);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/reactive/HttpHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */