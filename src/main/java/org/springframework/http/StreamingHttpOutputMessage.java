package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

public interface StreamingHttpOutputMessage extends HttpOutputMessage {
  void setBody(Body paramBody);
  
  @FunctionalInterface
  public static interface Body {
    void writeTo(OutputStream param1OutputStream) throws IOException;
  }
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/StreamingHttpOutputMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */