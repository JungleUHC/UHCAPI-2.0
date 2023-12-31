package org.springframework.core.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface AsyncTaskExecutor extends TaskExecutor {
  @Deprecated
  public static final long TIMEOUT_IMMEDIATE = 0L;
  
  @Deprecated
  public static final long TIMEOUT_INDEFINITE = 9223372036854775807L;
  
  @Deprecated
  void execute(Runnable paramRunnable, long paramLong);
  
  Future<?> submit(Runnable paramRunnable);
  
  <T> Future<T> submit(Callable<T> paramCallable);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/task/AsyncTaskExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */