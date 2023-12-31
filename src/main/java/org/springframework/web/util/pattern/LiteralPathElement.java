/*     */ package org.springframework.web.util.pattern;
/*     */ 
/*     */ import org.springframework.http.server.PathContainer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class LiteralPathElement
/*     */   extends PathElement
/*     */ {
/*     */   private final char[] text;
/*     */   private final int len;
/*     */   private final boolean caseSensitive;
/*     */   
/*     */   public LiteralPathElement(int pos, char[] literalText, boolean caseSensitive, char separator) {
/*  41 */     super(pos, separator);
/*  42 */     this.len = literalText.length;
/*  43 */     this.caseSensitive = caseSensitive;
/*  44 */     if (caseSensitive) {
/*  45 */       this.text = literalText;
/*     */     }
/*     */     else {
/*     */       
/*  49 */       this.text = new char[literalText.length];
/*  50 */       for (int i = 0; i < this.len; i++) {
/*  51 */         this.text[i] = Character.toLowerCase(literalText[i]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
/*  59 */     if (pathIndex >= matchingContext.pathLength)
/*     */     {
/*  61 */       return false;
/*     */     }
/*  63 */     PathContainer.Element element = matchingContext.pathElements.get(pathIndex);
/*  64 */     if (!(element instanceof PathContainer.PathSegment)) {
/*  65 */       return false;
/*     */     }
/*  67 */     String value = ((PathContainer.PathSegment)element).valueToMatch();
/*  68 */     if (value.length() != this.len)
/*     */     {
/*  70 */       return false;
/*     */     }
/*     */     
/*  73 */     if (this.caseSensitive) {
/*  74 */       for (int i = 0; i < this.len; i++) {
/*  75 */         if (value.charAt(i) != this.text[i]) {
/*  76 */           return false;
/*     */         }
/*     */       } 
/*     */     } else {
/*     */       
/*  81 */       for (int i = 0; i < this.len; i++) {
/*     */         
/*  83 */         if (Character.toLowerCase(value.charAt(i)) != this.text[i]) {
/*  84 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  89 */     pathIndex++;
/*  90 */     if (isNoMorePattern()) {
/*  91 */       if (matchingContext.determineRemainingPath) {
/*  92 */         matchingContext.remainingPathIndex = pathIndex;
/*  93 */         return true;
/*     */       } 
/*     */       
/*  96 */       if (pathIndex == matchingContext.pathLength) {
/*  97 */         return true;
/*     */       }
/*     */       
/* 100 */       return (matchingContext.isMatchOptionalTrailingSeparator() && pathIndex + 1 == matchingContext.pathLength && matchingContext
/*     */         
/* 102 */         .isSeparator(pathIndex));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 107 */     return (this.next != null && this.next.matches(pathIndex, matchingContext));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNormalizedLength() {
/* 113 */     return this.len;
/*     */   }
/*     */ 
/*     */   
/*     */   public char[] getChars() {
/* 118 */     return this.text;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 124 */     return "Literal(" + String.valueOf(this.text) + ")";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/pattern/LiteralPathElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */