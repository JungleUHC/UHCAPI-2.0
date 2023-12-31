package org.springframework.core.annotation;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

@FunctionalInterface
interface ValueExtractor {
  @Nullable
  Object extract(Method paramMethod, @Nullable Object paramObject);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/annotation/ValueExtractor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */