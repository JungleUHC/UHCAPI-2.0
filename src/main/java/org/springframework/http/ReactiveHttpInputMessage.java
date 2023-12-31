package org.springframework.http;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

public interface ReactiveHttpInputMessage extends HttpMessage {
  Flux<DataBuffer> getBody();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/ReactiveHttpInputMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */