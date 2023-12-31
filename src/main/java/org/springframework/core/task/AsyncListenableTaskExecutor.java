package org.springframework.core.task;

import java.util.concurrent.Callable;
import org.springframework.util.concurrent.ListenableFuture;

public interface AsyncListenableTaskExecutor extends AsyncTaskExecutor {
  ListenableFuture<?> submitListenable(Runnable paramRunnable);
  
  <T> ListenableFuture<T> submitListenable(Callable<T> paramCallable);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/task/AsyncListenableTaskExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */