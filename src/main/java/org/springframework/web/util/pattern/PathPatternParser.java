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
/*     */ 
/*     */ 
/*     */ public class PathPatternParser
/*     */ {
/*     */   private boolean matchOptionalTrailingSeparator = true;
/*     */   private boolean caseSensitive = true;
/*  41 */   private PathContainer.Options pathOptions = PathContainer.Options.HTTP_PATH;
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
/*     */   public void setMatchOptionalTrailingSeparator(boolean matchOptionalTrailingSeparator) {
/*  54 */     this.matchOptionalTrailingSeparator = matchOptionalTrailingSeparator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMatchOptionalTrailingSeparator() {
/*  61 */     return this.matchOptionalTrailingSeparator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCaseSensitive(boolean caseSensitive) {
/*  69 */     this.caseSensitive = caseSensitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCaseSensitive() {
/*  76 */     return this.caseSensitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPathOptions(PathContainer.Options pathOptions) {
/*  87 */     this.pathOptions = pathOptions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathContainer.Options getPathOptions() {
/*  95 */     return this.pathOptions;
/*     */   }
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
/*     */   public PathPattern parse(String pathPattern) throws PatternParseException {
/* 110 */     return (new InternalPathPatternParser(this)).parse(pathPattern);
/*     */   }
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
/* 122 */   public static final PathPatternParser defaultInstance = new PathPatternParser()
/*     */     {
/*     */       public void setMatchOptionalTrailingSeparator(boolean matchOptionalTrailingSeparator)
/*     */       {
/* 126 */         raiseError();
/*     */       }
/*     */ 
/*     */       
/*     */       public void setCaseSensitive(boolean caseSensitive) {
/* 131 */         raiseError();
/*     */       }
/*     */ 
/*     */       
/*     */       public void setPathOptions(PathContainer.Options pathOptions) {
/* 136 */         raiseError();
/*     */       }
/*     */       
/*     */       private void raiseError() {
/* 140 */         throw new UnsupportedOperationException("This is a read-only, shared instance that cannot be modified");
/*     */       }
/*     */     };
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/pattern/PathPatternParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */