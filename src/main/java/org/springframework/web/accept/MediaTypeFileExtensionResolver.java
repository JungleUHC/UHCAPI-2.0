package org.springframework.web.accept;

import java.util.List;
import org.springframework.http.MediaType;

public interface MediaTypeFileExtensionResolver {
  List<String> resolveFileExtensions(MediaType paramMediaType);
  
  List<String> getAllFileExtensions();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/accept/MediaTypeFileExtensionResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */