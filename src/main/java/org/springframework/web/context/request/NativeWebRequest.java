package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

public interface NativeWebRequest extends WebRequest {
  Object getNativeRequest();
  
  @Nullable
  Object getNativeResponse();
  
  @Nullable
  <T> T getNativeRequest(@Nullable Class<T> paramClass);
  
  @Nullable
  <T> T getNativeResponse(@Nullable Class<T> paramClass);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/NativeWebRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */