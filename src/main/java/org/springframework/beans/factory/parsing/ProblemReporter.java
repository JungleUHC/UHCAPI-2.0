package org.springframework.beans.factory.parsing;

public interface ProblemReporter {
  void fatal(Problem paramProblem);
  
  void error(Problem paramProblem);
  
  void warning(Problem paramProblem);
}


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/parsing/ProblemReporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */