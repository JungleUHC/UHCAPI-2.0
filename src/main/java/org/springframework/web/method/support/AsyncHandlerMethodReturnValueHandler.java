package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

public interface AsyncHandlerMethodReturnValueHandler extends HandlerMethodReturnValueHandler {
  boolean isAsyncReturnValue(@Nullable Object paramObject, MethodParameter paramMethodParameter);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/support/AsyncHandlerMethodReturnValueHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */