package org.springframework.cglib.core;

import org.springframework.asm.ClassVisitor;

public interface ClassGenerator {
  void generateClass(ClassVisitor paramClassVisitor) throws Exception;
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/cglib/core/ClassGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */