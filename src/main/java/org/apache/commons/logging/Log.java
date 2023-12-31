package org.apache.commons.logging;

public interface Log {
  boolean isFatalEnabled();
  
  boolean isErrorEnabled();
  
  boolean isWarnEnabled();
  
  boolean isInfoEnabled();
  
  boolean isDebugEnabled();
  
  boolean isTraceEnabled();
  
  void fatal(Object paramObject);
  
  void fatal(Object paramObject, Throwable paramThrowable);
  
  void error(Object paramObject);
  
  void error(Object paramObject, Throwable paramThrowable);
  
  void warn(Object paramObject);
  
  void warn(Object paramObject, Throwable paramThrowable);
  
  void info(Object paramObject);
  
  void info(Object paramObject, Throwable paramThrowable);
  
  void debug(Object paramObject);
  
  void debug(Object paramObject, Throwable paramThrowable);
  
  void trace(Object paramObject);
  
  void trace(Object paramObject, Throwable paramThrowable);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/apache/commons/logging/Log.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */