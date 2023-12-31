package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

public interface MultipartHttpServletRequest extends HttpServletRequest, MultipartRequest {
  @Nullable
  HttpMethod getRequestMethod();
  
  HttpHeaders getRequestHeaders();
  
  @Nullable
  HttpHeaders getMultipartHeaders(String paramString);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/multipart/MultipartHttpServletRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */