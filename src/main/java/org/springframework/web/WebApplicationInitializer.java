package org.springframework.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public interface WebApplicationInitializer {
  void onStartup(ServletContext paramServletContext) throws ServletException;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/WebApplicationInitializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */