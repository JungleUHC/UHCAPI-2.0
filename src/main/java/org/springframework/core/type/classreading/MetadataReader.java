package org.springframework.core.type.classreading;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

public interface MetadataReader {
  Resource getResource();
  
  ClassMetadata getClassMetadata();
  
  AnnotationMetadata getAnnotationMetadata();
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/type/classreading/MetadataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */