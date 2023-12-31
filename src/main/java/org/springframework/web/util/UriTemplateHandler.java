package org.springframework.web.util;

import java.net.URI;
import java.util.Map;

public interface UriTemplateHandler {
  URI expand(String paramString, Map<String, ?> paramMap);
  
  URI expand(String paramString, Object... paramVarArgs);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/UriTemplateHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */