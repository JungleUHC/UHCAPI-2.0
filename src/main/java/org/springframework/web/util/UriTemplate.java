/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UriTemplate
/*     */   implements Serializable
/*     */ {
/*     */   private final String uriTemplate;
/*     */   private final UriComponents uriComponents;
/*     */   private final List<String> variableNames;
/*     */   private final Pattern matchPattern;
/*     */   
/*     */   public UriTemplate(String uriTemplate) {
/*  69 */     Assert.hasText(uriTemplate, "'uriTemplate' must not be null");
/*  70 */     this.uriTemplate = uriTemplate;
/*  71 */     this.uriComponents = UriComponentsBuilder.fromUriString(uriTemplate).build();
/*     */     
/*  73 */     TemplateInfo info = TemplateInfo.parse(uriTemplate);
/*  74 */     this.variableNames = Collections.unmodifiableList(info.getVariableNames());
/*  75 */     this.matchPattern = info.getMatchPattern();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getVariableNames() {
/*  84 */     return this.variableNames;
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
/*     */   public URI expand(Map<String, ?> uriVariables) {
/* 105 */     UriComponents expandedComponents = this.uriComponents.expand(uriVariables);
/* 106 */     UriComponents encodedComponents = expandedComponents.encode();
/* 107 */     return encodedComponents.toUri();
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
/*     */   public URI expand(Object... uriVariableValues) {
/* 125 */     UriComponents expandedComponents = this.uriComponents.expand(uriVariableValues);
/* 126 */     UriComponents encodedComponents = expandedComponents.encode();
/* 127 */     return encodedComponents.toUri();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(@Nullable String uri) {
/* 136 */     if (uri == null) {
/* 137 */       return false;
/*     */     }
/* 139 */     Matcher matcher = this.matchPattern.matcher(uri);
/* 140 */     return matcher.matches();
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
/*     */   public Map<String, String> match(String uri) {
/* 156 */     Assert.notNull(uri, "'uri' must not be null");
/* 157 */     Map<String, String> result = CollectionUtils.newLinkedHashMap(this.variableNames.size());
/* 158 */     Matcher matcher = this.matchPattern.matcher(uri);
/* 159 */     if (matcher.find()) {
/* 160 */       for (int i = 1; i <= matcher.groupCount(); i++) {
/* 161 */         String name = this.variableNames.get(i - 1);
/* 162 */         String value = matcher.group(i);
/* 163 */         result.put(name, value);
/*     */       } 
/*     */     }
/* 166 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 171 */     return this.uriTemplate;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class TemplateInfo
/*     */   {
/*     */     private final List<String> variableNames;
/*     */ 
/*     */     
/*     */     private final Pattern pattern;
/*     */ 
/*     */     
/*     */     private TemplateInfo(List<String> vars, Pattern pattern) {
/* 185 */       this.variableNames = vars;
/* 186 */       this.pattern = pattern;
/*     */     }
/*     */     
/*     */     public List<String> getVariableNames() {
/* 190 */       return this.variableNames;
/*     */     }
/*     */     
/*     */     public Pattern getMatchPattern() {
/* 194 */       return this.pattern;
/*     */     }
/*     */     
/*     */     public static TemplateInfo parse(String uriTemplate) {
/* 198 */       int level = 0;
/* 199 */       List<String> variableNames = new ArrayList<>();
/* 200 */       StringBuilder pattern = new StringBuilder();
/* 201 */       StringBuilder builder = new StringBuilder();
/* 202 */       for (int i = 0; i < uriTemplate.length(); i++) {
/* 203 */         char c = uriTemplate.charAt(i);
/* 204 */         if (c == '{') {
/* 205 */           level++;
/* 206 */           if (level == 1) {
/*     */             
/* 208 */             pattern.append(quote(builder));
/* 209 */             builder = new StringBuilder();
/*     */             
/*     */             continue;
/*     */           } 
/*     */         } else {
/* 214 */           level--;
/* 215 */           if (c == '}' && level == 0) {
/*     */             
/* 217 */             String variable = builder.toString();
/* 218 */             int idx = variable.indexOf(':');
/* 219 */             if (idx == -1) {
/* 220 */               pattern.append("([^/]*)");
/* 221 */               variableNames.add(variable);
/*     */             } else {
/*     */               
/* 224 */               if (idx + 1 == variable.length()) {
/* 225 */                 throw new IllegalArgumentException("No custom regular expression specified after ':' in \"" + variable + "\"");
/*     */               }
/*     */               
/* 228 */               String regex = variable.substring(idx + 1);
/* 229 */               pattern.append('(');
/* 230 */               pattern.append(regex);
/* 231 */               pattern.append(')');
/* 232 */               variableNames.add(variable.substring(0, idx));
/*     */             } 
/* 234 */             builder = new StringBuilder();
/*     */             continue;
/*     */           } 
/*     */         } 
/* 238 */         builder.append(c); continue;
/*     */       } 
/* 240 */       if (builder.length() > 0) {
/* 241 */         pattern.append(quote(builder));
/*     */       }
/* 243 */       return new TemplateInfo(variableNames, Pattern.compile(pattern.toString()));
/*     */     }
/*     */     
/*     */     private static String quote(StringBuilder builder) {
/* 247 */       return (builder.length() > 0) ? Pattern.quote(builder.toString()) : "";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/UriTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */