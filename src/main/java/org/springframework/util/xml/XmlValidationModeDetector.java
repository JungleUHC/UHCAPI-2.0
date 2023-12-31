/*     */ package org.springframework.util.xml;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.CharConversionException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.StringUtils;
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
/*     */ public class XmlValidationModeDetector
/*     */ {
/*     */   public static final int VALIDATION_NONE = 0;
/*     */   public static final int VALIDATION_AUTO = 1;
/*     */   public static final int VALIDATION_DTD = 2;
/*     */   public static final int VALIDATION_XSD = 3;
/*     */   private static final String DOCTYPE = "DOCTYPE";
/*     */   private static final String START_COMMENT = "<!--";
/*     */   private static final String END_COMMENT = "-->";
/*     */   private boolean inComment;
/*     */   
/*     */   public int detectValidationMode(InputStream inputStream) throws IOException {
/*  92 */     this.inComment = false;
/*     */ 
/*     */     
/*  95 */     try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
/*  96 */       boolean isDtdValidated = false;
/*     */       String content;
/*  98 */       while ((content = reader.readLine()) != null) {
/*  99 */         content = consumeCommentTokens(content);
/* 100 */         if (!StringUtils.hasText(content)) {
/*     */           continue;
/*     */         }
/* 103 */         if (hasDoctype(content)) {
/* 104 */           isDtdValidated = true;
/*     */           break;
/*     */         } 
/* 107 */         if (hasOpeningTag(content)) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */       
/* 112 */       return isDtdValidated ? 2 : 3;
/*     */     }
/* 114 */     catch (CharConversionException ex) {
/*     */ 
/*     */       
/* 117 */       return 1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasDoctype(String content) {
/* 126 */     return content.contains("DOCTYPE");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasOpeningTag(String content) {
/* 137 */     if (this.inComment) {
/* 138 */       return false;
/*     */     }
/* 140 */     int openTagIndex = content.indexOf('<');
/* 141 */     return (openTagIndex > -1 && content.length() > openTagIndex + 1 && 
/* 142 */       Character.isLetter(content.charAt(openTagIndex + 1)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String consumeCommentTokens(String line) {
/* 151 */     int indexOfStartComment = line.indexOf("<!--");
/* 152 */     if (indexOfStartComment == -1 && !line.contains("-->")) {
/* 153 */       return line;
/*     */     }
/*     */     
/* 156 */     String result = "";
/* 157 */     String currLine = line;
/* 158 */     if (!this.inComment && indexOfStartComment >= 0) {
/* 159 */       result = line.substring(0, indexOfStartComment);
/* 160 */       currLine = line.substring(indexOfStartComment);
/*     */     } 
/*     */     
/* 163 */     if ((currLine = consume(currLine)) != null) {
/* 164 */       result = result + consumeCommentTokens(currLine);
/*     */     }
/* 166 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String consume(String line) {
/* 175 */     int index = this.inComment ? endComment(line) : startComment(line);
/* 176 */     return (index == -1) ? null : line.substring(index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int startComment(String line) {
/* 184 */     return commentToken(line, "<!--", true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int endComment(String line) {
/* 192 */     return commentToken(line, "-->", false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int commentToken(String line, String token, boolean inCommentIfPresent) {
/* 202 */     int index = line.indexOf(token);
/* 203 */     if (index > -1) {
/* 204 */       this.inComment = inCommentIfPresent;
/*     */     }
/* 206 */     return (index == -1) ? index : (index + token.length());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/util/xml/XmlValidationModeDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */