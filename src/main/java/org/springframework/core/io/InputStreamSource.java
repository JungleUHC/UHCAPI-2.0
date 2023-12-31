package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamSource {
  InputStream getInputStream() throws IOException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/io/InputStreamSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */