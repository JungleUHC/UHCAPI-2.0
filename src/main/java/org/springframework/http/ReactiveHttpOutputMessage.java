package org.springframework.http;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import reactor.core.publisher.Mono;

public interface ReactiveHttpOutputMessage extends HttpMessage {
  DataBufferFactory bufferFactory();
  
  void beforeCommit(Supplier<? extends Mono<Void>> paramSupplier);
  
  boolean isCommitted();
  
  Mono<Void> writeWith(Publisher<? extends DataBuffer> paramPublisher);
  
  Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> paramPublisher);
  
  Mono<Void> setComplete();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/ReactiveHttpOutputMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */