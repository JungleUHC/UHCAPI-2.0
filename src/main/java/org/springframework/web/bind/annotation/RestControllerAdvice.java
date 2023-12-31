package org.springframework.web.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ControllerAdvice
@ResponseBody
public @interface RestControllerAdvice {
  @AliasFor(annotation = ControllerAdvice.class)
  String[] value() default {};
  
  @AliasFor(annotation = ControllerAdvice.class)
  String[] basePackages() default {};
  
  @AliasFor(annotation = ControllerAdvice.class)
  Class<?>[] basePackageClasses() default {};
  
  @AliasFor(annotation = ControllerAdvice.class)
  Class<?>[] assignableTypes() default {};
  
  @AliasFor(annotation = ControllerAdvice.class)
  Class<? extends Annotation>[] annotations() default {};
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/bind/annotation/RestControllerAdvice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */