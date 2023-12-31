package org.springframework.core.task;

@FunctionalInterface
public interface TaskDecorator {
  Runnable decorate(Runnable paramRunnable);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/task/TaskDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */