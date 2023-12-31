package org.springframework.beans;

import java.beans.PropertyEditor;
import org.springframework.lang.Nullable;

public interface PropertyEditorRegistry {
  void registerCustomEditor(Class<?> paramClass, PropertyEditor paramPropertyEditor);
  
  void registerCustomEditor(@Nullable Class<?> paramClass, @Nullable String paramString, PropertyEditor paramPropertyEditor);
  
  @Nullable
  PropertyEditor findCustomEditor(@Nullable Class<?> paramClass, @Nullable String paramString);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/PropertyEditorRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */