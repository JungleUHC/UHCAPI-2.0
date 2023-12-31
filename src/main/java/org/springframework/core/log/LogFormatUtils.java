/*     */ package org.springframework.core.log;
/*     */ 
/*     */ import java.util.function.Function;
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ObjectUtils;
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
/*     */ public abstract class LogFormatUtils
/*     */ {
/*  40 */   private static final Pattern NEWLINE_PATTERN = Pattern.compile("[\n\r]");
/*     */   
/*  42 */   private static final Pattern CONTROL_CHARACTER_PATTERN = Pattern.compile("\\p{Cc}");
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
/*     */   public static String formatValue(@Nullable Object value, boolean limitLength) {
/*  54 */     return formatValue(value, limitLength ? 100 : -1, limitLength);
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
/*     */   public static String formatValue(@Nullable Object value, int maxLength, boolean replaceNewlinesAndControlCharacters) {
/*     */     String result;
/*  70 */     if (value == null) {
/*  71 */       return "";
/*     */     }
/*     */     
/*     */     try {
/*  75 */       result = ObjectUtils.nullSafeToString(value);
/*     */     }
/*  77 */     catch (Throwable ex) {
/*  78 */       result = ObjectUtils.nullSafeToString(ex);
/*     */     } 
/*  80 */     if (maxLength != -1) {
/*  81 */       result = (result.length() > maxLength) ? (result.substring(0, maxLength) + " (truncated)...") : result;
/*     */     }
/*  83 */     if (replaceNewlinesAndControlCharacters) {
/*  84 */       result = NEWLINE_PATTERN.matcher(result).replaceAll("<EOL>");
/*  85 */       result = CONTROL_CHARACTER_PATTERN.matcher(result).replaceAll("?");
/*     */     } 
/*  87 */     if (value instanceof CharSequence) {
/*  88 */       result = "\"" + result + "\"";
/*     */     }
/*  90 */     return result;
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
/*     */   public static void traceDebug(Log logger, Function<Boolean, String> messageFactory) {
/* 112 */     if (logger.isDebugEnabled()) {
/* 113 */       boolean traceEnabled = logger.isTraceEnabled();
/* 114 */       String logMessage = messageFactory.apply(Boolean.valueOf(traceEnabled));
/* 115 */       if (traceEnabled) {
/* 116 */         logger.trace(logMessage);
/*     */       } else {
/*     */         
/* 119 */         logger.debug(logMessage);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/log/LogFormatUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */