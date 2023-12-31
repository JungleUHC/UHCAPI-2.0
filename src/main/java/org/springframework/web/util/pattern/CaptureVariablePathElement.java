/*     */ package org.springframework.web.util.pattern;
/*     */ 
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.springframework.http.server.PathContainer;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ 
/*     */ class CaptureVariablePathElement
/*     */   extends PathElement
/*     */ {
/*     */   private final String variableName;
/*     */   @Nullable
/*     */   private final Pattern constraintPattern;
/*     */   
/*     */   CaptureVariablePathElement(int pos, char[] captureDescriptor, boolean caseSensitive, char separator) {
/*  47 */     super(pos, separator);
/*  48 */     int colon = -1;
/*  49 */     for (int i = 0; i < captureDescriptor.length; i++) {
/*  50 */       if (captureDescriptor[i] == ':') {
/*  51 */         colon = i;
/*     */         break;
/*     */       } 
/*     */     } 
/*  55 */     if (colon == -1) {
/*     */       
/*  57 */       this.variableName = new String(captureDescriptor, 1, captureDescriptor.length - 2);
/*  58 */       this.constraintPattern = null;
/*     */     } else {
/*     */       
/*  61 */       this.variableName = new String(captureDescriptor, 1, colon - 1);
/*  62 */       if (caseSensitive) {
/*  63 */         this.constraintPattern = Pattern.compile(new String(captureDescriptor, colon + 1, captureDescriptor.length - colon - 2));
/*     */       }
/*     */       else {
/*     */         
/*  67 */         this.constraintPattern = Pattern.compile(new String(captureDescriptor, colon + 1, captureDescriptor.length - colon - 2), 2);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
/*  77 */     if (pathIndex >= matchingContext.pathLength)
/*     */     {
/*  79 */       return false;
/*     */     }
/*  81 */     String candidateCapture = matchingContext.pathElementValue(pathIndex);
/*  82 */     if (candidateCapture.length() == 0) {
/*  83 */       return false;
/*     */     }
/*     */     
/*  86 */     if (this.constraintPattern != null) {
/*     */ 
/*     */       
/*  89 */       Matcher matcher = this.constraintPattern.matcher(candidateCapture);
/*  90 */       if (matcher.groupCount() != 0) {
/*  91 */         throw new IllegalArgumentException("No capture groups allowed in the constraint regex: " + this.constraintPattern
/*  92 */             .pattern());
/*     */       }
/*  94 */       if (!matcher.matches()) {
/*  95 */         return false;
/*     */       }
/*     */     } 
/*     */     
/*  99 */     boolean match = false;
/* 100 */     pathIndex++;
/* 101 */     if (isNoMorePattern()) {
/* 102 */       if (matchingContext.determineRemainingPath) {
/* 103 */         matchingContext.remainingPathIndex = pathIndex;
/* 104 */         match = true;
/*     */       }
/*     */       else {
/*     */         
/* 108 */         match = (pathIndex == matchingContext.pathLength);
/* 109 */         if (!match && matchingContext.isMatchOptionalTrailingSeparator())
/*     */         {
/*     */           
/* 112 */           match = (pathIndex + 1 == matchingContext.pathLength && matchingContext.isSeparator(pathIndex));
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 117 */     else if (this.next != null) {
/* 118 */       match = this.next.matches(pathIndex, matchingContext);
/*     */     } 
/*     */ 
/*     */     
/* 122 */     if (match && matchingContext.extractingVariables) {
/* 123 */       matchingContext.set(this.variableName, candidateCapture, ((PathContainer.PathSegment)matchingContext.pathElements
/* 124 */           .get(pathIndex - 1)).parameters());
/*     */     }
/* 126 */     return match;
/*     */   }
/*     */   
/*     */   public String getVariableName() {
/* 130 */     return this.variableName;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNormalizedLength() {
/* 135 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public char[] getChars() {
/* 140 */     StringBuilder sb = new StringBuilder();
/* 141 */     sb.append('{');
/* 142 */     sb.append(this.variableName);
/* 143 */     if (this.constraintPattern != null) {
/* 144 */       sb.append(':').append(this.constraintPattern.pattern());
/*     */     }
/* 146 */     sb.append('}');
/* 147 */     return sb.toString().toCharArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWildcardCount() {
/* 152 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCaptureCount() {
/* 157 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getScore() {
/* 162 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 168 */     return "CaptureVariable({" + this.variableName + ((this.constraintPattern != null) ? (":" + this.constraintPattern
/* 169 */       .pattern()) : "") + "})";
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/pattern/CaptureVariablePathElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */