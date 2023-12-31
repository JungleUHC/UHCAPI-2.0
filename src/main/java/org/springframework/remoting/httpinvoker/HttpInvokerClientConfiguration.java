package org.springframework.remoting.httpinvoker;

import org.springframework.lang.Nullable;

@Deprecated
public interface HttpInvokerClientConfiguration {
  String getServiceUrl();
  
  @Nullable
  String getCodebaseUrl();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/httpinvoker/HttpInvokerClientConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */