package org.springframework.web.context;

import javax.servlet.ServletConfig;
import org.springframework.beans.factory.Aware;

public interface ServletConfigAware extends Aware {
  void setServletConfig(ServletConfig paramServletConfig);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/ServletConfigAware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */