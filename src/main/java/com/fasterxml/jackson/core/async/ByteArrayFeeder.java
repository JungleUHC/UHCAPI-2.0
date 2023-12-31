package com.fasterxml.jackson.core.async;

import java.io.IOException;

public interface ByteArrayFeeder extends NonBlockingInputFeeder {
  void feedInput(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/async/ByteArrayFeeder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */