/*    */ package org.springframework.web.util.pattern;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class SeparatorPathElement
/*    */   extends PathElement
/*    */ {
/*    */   SeparatorPathElement(int pos, char separator) {
/* 32 */     super(pos, separator);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
/* 42 */     if (pathIndex < matchingContext.pathLength && matchingContext.isSeparator(pathIndex)) {
/* 43 */       if (isNoMorePattern()) {
/* 44 */         if (matchingContext.determineRemainingPath) {
/* 45 */           matchingContext.remainingPathIndex = pathIndex + 1;
/* 46 */           return true;
/*    */         } 
/*    */         
/* 49 */         return (pathIndex + 1 == matchingContext.pathLength);
/*    */       } 
/*    */ 
/*    */       
/* 53 */       pathIndex++;
/* 54 */       return (this.next != null && this.next.matches(pathIndex, matchingContext));
/*    */     } 
/*    */     
/* 57 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNormalizedLength() {
/* 62 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public char[] getChars() {
/* 67 */     return new char[] { this.separator };
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 73 */     return "Separator(" + this.separator + ")";
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/pattern/SeparatorPathElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */