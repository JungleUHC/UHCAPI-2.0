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
/*     */ public abstract class PropertyNamingStrategies
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*  36 */   public static final PropertyNamingStrategy LOWER_CAMEL_CASE = new LowerCamelCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   public static final PropertyNamingStrategy UPPER_CAMEL_CASE = new UpperCamelCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   public static final PropertyNamingStrategy SNAKE_CASE = new SnakeCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   public static final PropertyNamingStrategy UPPER_SNAKE_CASE = new UpperSnakeCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   public static final PropertyNamingStrategy LOWER_CASE = new LowerCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   public static final PropertyNamingStrategy KEBAB_CASE = new KebabCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public static final PropertyNamingStrategy LOWER_DOT_CASE = new LowerDotCaseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class NamingBase
/*     */     extends PropertyNamingStrategy
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
/* 107 */       return translate(defaultName);
/*     */     }
/*     */ 
/*     */     
/*     */     public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
/* 112 */       return translate(defaultName);
/*     */     }
/*     */ 
/*     */     
/*     */     public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
/* 117 */       return translate(defaultName);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
/* 123 */       return translate(defaultName);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public abstract String translate(String param1String);
/*     */ 
/*     */ 
/*     */     
/*     */     protected String translateLowerCaseWithSeparator(String input, char separator) {
/* 133 */       if (input == null) {
/* 134 */         return input;
/*     */       }
/* 136 */       int length = input.length();
/* 137 */       if (length == 0) {
/* 138 */         return input;
/*     */       }
/*     */       
/* 141 */       StringBuilder result = new StringBuilder(length + (length >> 1));
/* 142 */       int upperCount = 0;
/* 143 */       for (int i = 0; i < length; i++) {
/* 144 */         char ch = input.charAt(i);
/* 145 */         char lc = Character.toLowerCase(ch);
/*     */         
/* 147 */         if (lc == ch) {
/*     */ 
/*     */           
/* 150 */           if (upperCount > 1)
/*     */           {
/* 152 */             result.insert(result.length() - 1, separator);
/*     */           }
/* 154 */           upperCount = 0;
/*     */         } else {
/*     */           
/* 157 */           if (upperCount == 0 && i > 0) {
/* 158 */             result.append(separator);
/*     */           }
/* 160 */           upperCount++;
/*     */         } 
/* 162 */         result.append(lc);
/*     */       } 
/* 164 */       return result.toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class SnakeCaseStrategy
/*     */     extends NamingBase
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String translate(String input) {
/* 230 */       if (input == null) return input; 
/* 231 */       int length = input.length();
/* 232 */       StringBuilder result = new StringBuilder(length * 2);
/* 233 */       int resultLength = 0;
/* 234 */       boolean wasPrevTranslated = false;
/* 235 */       for (int i = 0; i < length; i++) {
/*     */         
/* 237 */         char c = input.charAt(i);
/* 238 */         if (i > 0 || c != '_') {
/*     */           
/* 240 */           if (Character.isUpperCase(c)) {
/*     */             
/* 242 */             if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
/*     */               
/* 244 */               result.append('_');
/* 245 */               resultLength++;
/*     */             } 
/* 247 */             c = Character.toLowerCase(c);
/* 248 */             wasPrevTranslated = true;
/*     */           }
/*     */           else {
/*     */             
/* 252 */             wasPrevTranslated = false;
/*     */           } 
/* 254 */           result.append(c);
/* 255 */           resultLength++;
/*     */         } 
/*     */       } 
/* 258 */       return (resultLength > 0) ? result.toString() : input;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class UpperSnakeCaseStrategy
/*     */     extends SnakeCaseStrategy
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String translate(String input) {
/* 274 */       String output = super.translate(input);
/* 275 */       if (output == null)
/* 276 */         return null; 
/* 277 */       return super.translate(input).toUpperCase();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class LowerCamelCaseStrategy
/*     */     extends NamingBase
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */ 
/*     */ 
/*     */     
/*     */     public String translate(String input) {
/* 291 */       return input;
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
/*     */   public static class UpperCamelCaseStrategy
/*     */     extends NamingBase
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String translate(String input) {
/* 323 */       if (input == null || input.isEmpty()) {
/* 324 */         return input;
/*     */       }
/*     */       
/* 327 */       char c = input.charAt(0);
/* 328 */       char uc = Character.toUpperCase(c);
/* 329 */       if (c == uc) {
/* 330 */         return input;
/*     */       }
/* 332 */       StringBuilder sb = new StringBuilder(input);
/* 333 */       sb.setCharAt(0, uc);
/* 334 */       return sb.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class LowerCaseStrategy
/*     */     extends NamingBase
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String translate(String input) {
/* 350 */       return input.toLowerCase();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class KebabCaseStrategy
/*     */     extends NamingBase
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String translate(String input) {
/* 366 */       return translateLowerCaseWithSeparator(input, '-');
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class LowerDotCaseStrategy
/*     */     extends NamingBase
/*     */   {
/*     */     private static final long serialVersionUID = 2L;
/*     */ 
/*     */ 
/*     */     
/*     */     public String translate(String input) {
/* 380 */       return translateLowerCaseWithSeparator(input, '.');
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/PropertyNamingStrategies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */