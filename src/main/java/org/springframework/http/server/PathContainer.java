/*     */ package org.springframework.http.server;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface PathContainer
/*     */ {
/*     */   String value();
/*     */   
/*     */   List<Element> elements();
/*     */   
/*     */   default PathContainer subPath(int index) {
/*  53 */     return subPath(index, elements().size());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default PathContainer subPath(int startIndex, int endIndex) {
/*  64 */     return DefaultPathContainer.subPath(this, startIndex, endIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static PathContainer parsePath(String path) {
/*  75 */     return DefaultPathContainer.createFromUrlPath(path, Options.HTTP_PATH);
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
/*     */   static PathContainer parsePath(String path, Options options) {
/*  87 */     return DefaultPathContainer.createFromUrlPath(path, options);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Options
/*     */   {
/* 143 */     public static final Options HTTP_PATH = create('/', true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 151 */     public static final Options MESSAGE_ROUTE = create('.', false);
/*     */     
/*     */     private final char separator;
/*     */     
/*     */     private final boolean decodeAndParseSegments;
/*     */     
/*     */     private Options(char separator, boolean decodeAndParseSegments) {
/* 158 */       this.separator = separator;
/* 159 */       this.decodeAndParseSegments = decodeAndParseSegments;
/*     */     }
/*     */     
/*     */     public char separator() {
/* 163 */       return this.separator;
/*     */     }
/*     */     
/*     */     public boolean shouldDecodeAndParseSegments() {
/* 167 */       return this.decodeAndParseSegments;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Options create(char separator, boolean decodeAndParseSegments) {
/* 179 */       return new Options(separator, decodeAndParseSegments);
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface PathSegment extends Element {
/*     */     String valueToMatch();
/*     */     
/*     */     char[] valueToMatchAsChars();
/*     */     
/*     */     MultiValueMap<String, String> parameters();
/*     */   }
/*     */   
/*     */   public static interface Separator extends Element {}
/*     */   
/*     */   public static interface Element {
/*     */     String value();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/PathContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */