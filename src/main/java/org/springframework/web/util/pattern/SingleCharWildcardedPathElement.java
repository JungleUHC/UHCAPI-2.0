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
/*     */ 
/*     */ 
/*     */ class SingleCharWildcardedPathElement
/*     */   extends PathElement
/*     */ {
/*     */   private final char[] text;
/*     */   private final int len;
/*     */   private final int questionMarkCount;
/*     */   private final boolean caseSensitive;
/*     */   
/*     */   public SingleCharWildcardedPathElement(int pos, char[] literalText, int questionMarkCount, boolean caseSensitive, char separator) {
/*  44 */     super(pos, separator);
/*  45 */     this.len = literalText.length;
/*  46 */     this.questionMarkCount = questionMarkCount;
/*  47 */     this.caseSensitive = caseSensitive;
/*  48 */     if (caseSensitive) {
/*  49 */       this.text = literalText;
/*     */     } else {
/*     */       
/*  52 */       this.text = new char[literalText.length];
/*  53 */       for (int i = 0; i < this.len; i++) {
/*  54 */         this.text[i] = Character.toLowerCase(literalText[i]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
/*  62 */     if (pathIndex >= matchingContext.pathLength)
/*     */     {
/*  64 */       return false;
/*     */     }
/*     */     
/*  67 */     PathContainer.Element element = matchingContext.pathElements.get(pathIndex);
/*  68 */     if (!(element instanceof PathContainer.PathSegment)) {
/*  69 */       return false;
/*     */     }
/*  71 */     String value = ((PathContainer.PathSegment)element).valueToMatch();
/*  72 */     if (value.length() != this.len)
/*     */     {
/*  74 */       return false;
/*     */     }
/*     */     
/*  77 */     if (this.caseSensitive) {
/*  78 */       for (int i = 0; i < this.len; i++) {
/*  79 */         char ch = this.text[i];
/*  80 */         if (ch != '?' && ch != value.charAt(i)) {
/*  81 */           return false;
/*     */         }
/*     */       } 
/*     */     } else {
/*     */       
/*  86 */       for (int i = 0; i < this.len; i++) {
/*  87 */         char ch = this.text[i];
/*     */         
/*  89 */         if (ch != '?' && ch != Character.toLowerCase(value.charAt(i))) {
/*  90 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  95 */     pathIndex++;
/*  96 */     if (isNoMorePattern()) {
/*  97 */       if (matchingContext.determineRemainingPath) {
/*  98 */         matchingContext.remainingPathIndex = pathIndex;
/*  99 */         return true;
/*     */       } 
/*     */       
/* 102 */       if (pathIndex == matchingContext.pathLength) {
/* 103 */         return true;
/*     */       }
/*     */       
/* 106 */       return (matchingContext.isMatchOptionalTrailingSeparator() && pathIndex + 1 == matchingContext.pathLength && matchingContext
/*     */         
/* 108 */         .isSeparator(pathIndex));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 113 */     return (this.next != null && this.next.matches(pathIndex, matchingContext));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWildcardCount() {
/* 119 */     return this.questionMarkCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNormalizedLength() {
/* 124 */     return this.len;
/*     */   }
/*     */ 
/*     */   
/*     */   public char[] getChars() {
/* 129 */     return this.text;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 135 */     return "SingleCharWildcarded(" + String.valueOf(this.text) + ")";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/pattern/SingleCharWildcardedPathElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */