package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.WebRequest;

public interface SessionAttributeStore {
  void storeAttribute(WebRequest paramWebRequest, String paramString, Object paramObject);
  
  @Nullable
  Object retrieveAttribute(WebRequest paramWebRequest, String paramString);
  
  void cleanupAttribute(WebRequest paramWebRequest, String paramString);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/support/SessionAttributeStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */