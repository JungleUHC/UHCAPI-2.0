package org.springframework.remoting.httpinvoker;

import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

@Deprecated
@FunctionalInterface
public interface HttpInvokerRequestExecutor {
  RemoteInvocationResult executeRequest(HttpInvokerClientConfiguration paramHttpInvokerClientConfiguration, RemoteInvocation paramRemoteInvocation) throws Exception;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/remoting/httpinvoker/HttpInvokerRequestExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */