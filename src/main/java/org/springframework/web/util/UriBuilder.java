package org.springframework.web.util;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public interface UriBuilder {
  UriBuilder scheme(@Nullable String paramString);
  
  UriBuilder userInfo(@Nullable String paramString);
  
  UriBuilder host(@Nullable String paramString);
  
  UriBuilder port(int paramInt);
  
  UriBuilder port(@Nullable String paramString);
  
  UriBuilder path(String paramString);
  
  UriBuilder replacePath(@Nullable String paramString);
  
  UriBuilder pathSegment(String... paramVarArgs) throws IllegalArgumentException;
  
  UriBuilder query(String paramString);
  
  UriBuilder replaceQuery(@Nullable String paramString);
  
  UriBuilder queryParam(String paramString, Object... paramVarArgs);
  
  UriBuilder queryParam(String paramString, @Nullable Collection<?> paramCollection);
  
  UriBuilder queryParamIfPresent(String paramString, Optional<?> paramOptional);
  
  UriBuilder queryParams(MultiValueMap<String, String> paramMultiValueMap);
  
  UriBuilder replaceQueryParam(String paramString, Object... paramVarArgs);
  
  UriBuilder replaceQueryParam(String paramString, @Nullable Collection<?> paramCollection);
  
  UriBuilder replaceQueryParams(MultiValueMap<String, String> paramMultiValueMap);
  
  UriBuilder fragment(@Nullable String paramString);
  
  URI build(Object... paramVarArgs);
  
  URI build(Map<String, ?> paramMap);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/UriBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */