package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

public interface WebDataBinderFactory {
  WebDataBinder createBinder(NativeWebRequest paramNativeWebRequest, @Nullable Object paramObject, String paramString) throws Exception;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/support/WebDataBinderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */