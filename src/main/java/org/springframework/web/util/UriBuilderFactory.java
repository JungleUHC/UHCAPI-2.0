package org.springframework.web.util;

public interface UriBuilderFactory extends UriTemplateHandler {
  UriBuilder uriString(String paramString);
  
  UriBuilder builder();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/UriBuilderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */