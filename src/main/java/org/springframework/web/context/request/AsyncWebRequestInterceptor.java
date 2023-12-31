package org.springframework.web.context.request;

public interface AsyncWebRequestInterceptor extends WebRequestInterceptor {
  void afterConcurrentHandlingStarted(WebRequest paramWebRequest);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/request/AsyncWebRequestInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */