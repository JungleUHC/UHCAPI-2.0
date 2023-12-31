package org.springframework.web.cors;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

public interface CorsConfigurationSource {
  @Nullable
  CorsConfiguration getCorsConfiguration(HttpServletRequest paramHttpServletRequest);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/CorsConfigurationSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */