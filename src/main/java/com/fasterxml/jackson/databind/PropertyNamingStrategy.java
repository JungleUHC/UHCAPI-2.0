/*     */ package com.fasterxml.jackson.databind;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedField;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropertyNamingStrategy
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   @Deprecated
/*  50 */   public static final PropertyNamingStrategy LOWER_CAMEL_CASE = new PropertyNamingStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  59 */   public static final PropertyNamingStrategy UPPER_CAMEL_CASE = new UpperCamelCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  68 */   public static final PropertyNamingStrategy SNAKE_CASE = new SnakeCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  77 */   public static final PropertyNamingStrategy LOWER_CASE = new LowerCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  86 */   public static final PropertyNamingStrategy KEBAB_CASE = new KebabCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  95 */   public static final PropertyNamingStrategy LOWER_DOT_CASE = new LowerDotCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
/* 119 */     return defaultName;
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
/*     */   public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
/* 140 */     return defaultName;
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
/*     */   public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
/* 160 */     return defaultName;
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
/*     */   public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
/* 178 */     return defaultName;
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
/*     */   @Deprecated
/*     */   public static abstract class PropertyNamingStrategyBase
/*     */     extends PropertyNamingStrategy
/*     */   {
/*     */     public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
/* 198 */       return translate(defaultName);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
/* 204 */       return translate(defaultName);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
/* 210 */       return translate(defaultName);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
/* 217 */       return translate(defaultName);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public abstract String translate(String param1String);
/*     */ 
/*     */ 
/*     */     
/*     */     protected static String translateLowerCaseWithSeparator(String input, char separator) {
/* 227 */       if (input == null) {
/* 228 */         return input;
/*     */       }
/* 230 */       int length = input.length();
/* 231 */       if (length == 0) {
/* 232 */         return input;
/*     */       }
/*     */       
/* 235 */       StringBuilder result = new StringBuilder(length + (length >> 1));
/* 236 */       int upperCount = 0;
/* 237 */       for (int i = 0; i < length; i++) {
/* 238 */         char ch = input.charAt(i);
/* 239 */         char lc = Character.toLowerCase(ch);
/*     */         
/* 241 */         if (lc == ch) {
/*     */ 
/*     */           
/* 244 */           if (upperCount > 1)
/*     */           {
/* 246 */             result.insert(result.length() - 1, separator);
/*     */           }
/* 248 */           upperCount = 0;
/*     */         } else {
/*     */           
/* 251 */           if (upperCount == 0 && i > 0) {
/* 252 */             result.append(separator);
/*     */           }
/* 254 */           upperCount++;
/*     */         } 
/* 256 */         result.append(lc);
/*     */       } 
/* 258 */       return result.toString();
/*     */     }
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
/*     */   @Deprecated
/*     */   public static class SnakeCaseStrategy
/*     */     extends PropertyNamingStrategyBase
/*     */   {
/*     */     public String translate(String input) {
/* 280 */       if (input == null) return input; 
/* 281 */       int length = input.length();
/* 282 */       StringBuilder result = new StringBuilder(length * 2);
/* 283 */       int resultLength = 0;
/* 284 */       boolean wasPrevTranslated = false;
/* 285 */       for (int i = 0; i < length; i++) {
/*     */         
/* 287 */         char c = input.charAt(i);
/* 288 */         if (i > 0 || c != '_') {
/*     */           
/* 290 */           if (Character.isUpperCase(c)) {
/*     */             
/* 292 */             if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
/*     */               
/* 294 */               result.append('_');
/* 295 */               resultLength++;
/*     */             } 
/* 297 */             c = Character.toLowerCase(c);
/* 298 */             wasPrevTranslated = true;
/*     */           }
/*     */           else {
/*     */             
/* 302 */             wasPrevTranslated = false;
/*     */           } 
/* 304 */           result.append(c);
/* 305 */           resultLength++;
/*     */         } 
/*     */       } 
/* 308 */       return (resultLength > 0) ? result.toString() : input;
/*     */     }
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
/*     */   @Deprecated
/*     */   public static class UpperCamelCaseStrategy
/*     */     extends PropertyNamingStrategyBase
/*     */   {
/*     */     public String translate(String input) {
/* 332 */       if (input == null || input.isEmpty()) {
/* 333 */         return input;
/*     */       }
/*     */       
/* 336 */       char c = input.charAt(0);
/* 337 */       char uc = Character.toUpperCase(c);
/* 338 */       if (c == uc) {
/* 339 */         return input;
/*     */       }
/* 341 */       StringBuilder sb = new StringBuilder(input);
/* 342 */       sb.setCharAt(0, uc);
/* 343 */       return sb.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static class LowerCaseStrategy
/*     */     extends PropertyNamingStrategyBase
/*     */   {
/*     */     public String translate(String input) {
/* 358 */       return input.toLowerCase();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static class KebabCaseStrategy
/*     */     extends PropertyNamingStrategyBase
/*     */   {
/*     */     public String translate(String input) {
/* 373 */       return translateLowerCaseWithSeparator(input, '-');
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static class LowerDotCaseStrategy
/*     */     extends PropertyNamingStrategyBase
/*     */   {
/*     */     public String translate(String input) {
/* 387 */       return translateLowerCaseWithSeparator(input, '.');
/*     */     }
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
/*     */   @Deprecated
/* 401 */   public static final PropertyNamingStrategy CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES = SNAKE_CASE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 407 */   public static final PropertyNamingStrategy PASCAL_CASE_TO_CAMEL_CASE = UPPER_CAMEL_CASE;
/*     */   
/*     */   @Deprecated
/*     */   public static class LowerCaseWithUnderscoresStrategy extends SnakeCaseStrategy {}
/*     */   
/*     */   @Deprecated
/*     */   public static class PascalCaseStrategy extends UpperCamelCaseStrategy {}
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/PropertyNamingStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */