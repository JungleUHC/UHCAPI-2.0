package org.springframework.http;

import java.io.IOException;
import java.io.InputStream;

public interface HttpInputMessage extends HttpMessage {
  InputStream getBody() throws IOException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/HttpInputMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */