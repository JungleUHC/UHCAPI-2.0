package org.springframework.core.io.buffer;

public interface PooledDataBuffer extends DataBuffer {
  boolean isAllocated();
  
  PooledDataBuffer retain();
  
  PooledDataBuffer touch(Object paramObject);
  
  boolean release();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/io/buffer/PooledDataBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */