package org.springframework.web.server.i18n;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

public interface LocaleContextResolver {
  LocaleContext resolveLocaleContext(ServerWebExchange paramServerWebExchange);
  
  void setLocaleContext(ServerWebExchange paramServerWebExchange, @Nullable LocaleContext paramLocaleContext);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/i18n/LocaleContextResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */