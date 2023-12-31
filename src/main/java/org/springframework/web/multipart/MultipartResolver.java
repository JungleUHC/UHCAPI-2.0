package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;

public interface MultipartResolver {
  boolean isMultipart(HttpServletRequest paramHttpServletRequest);
  
  MultipartHttpServletRequest resolveMultipart(HttpServletRequest paramHttpServletRequest) throws MultipartException;
  
  void cleanupMultipart(MultipartHttpServletRequest paramMultipartHttpServletRequest);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/MultipartResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */