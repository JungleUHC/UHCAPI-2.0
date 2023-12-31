/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.UnaryOperator;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
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
/*     */ public abstract class UriComponents
/*     */   implements Serializable
/*     */ {
/*  51 */   private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final String scheme;
/*     */   
/*     */   @Nullable
/*     */   private final String fragment;
/*     */ 
/*     */   
/*     */   protected UriComponents(@Nullable String scheme, @Nullable String fragment) {
/*  62 */     this.scheme = scheme;
/*  63 */     this.fragment = fragment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public final String getScheme() {
/*  74 */     return this.scheme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public final String getFragment() {
/*  82 */     return this.fragment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public abstract String getSchemeSpecificPart();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public abstract String getUserInfo();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public abstract String getHost();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getPort();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public abstract String getPath();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract List<String> getPathSegments();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public abstract String getQuery();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract MultiValueMap<String, String> getQueryParams();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final UriComponents encode() {
/* 142 */     return encode(StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract UriComponents encode(Charset paramCharset);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final UriComponents expand(Map<String, ?> uriVariables) {
/* 160 */     Assert.notNull(uriVariables, "'uriVariables' must not be null");
/* 161 */     return expandInternal(new MapTemplateVariables(uriVariables));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final UriComponents expand(Object... uriVariableValues) {
/* 171 */     Assert.notNull(uriVariableValues, "'uriVariableValues' must not be null");
/* 172 */     return expandInternal(new VarArgsTemplateVariables(uriVariableValues));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final UriComponents expand(UriTemplateVariables uriVariables) {
/* 182 */     Assert.notNull(uriVariables, "'uriVariables' must not be null");
/* 183 */     return expandInternal(uriVariables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract UriComponents expandInternal(UriTemplateVariables paramUriTemplateVariables);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract UriComponents normalize();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String toUriString();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract URI toUri();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 228 */     return toUriString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void copyToUriComponentsBuilder(UriComponentsBuilder paramUriComponentsBuilder);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   static String expandUriComponent(@Nullable String source, UriTemplateVariables uriVariables) {
/* 242 */     return expandUriComponent(source, uriVariables, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   static String expandUriComponent(@Nullable String source, UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
/* 249 */     if (source == null) {
/* 250 */       return null;
/*     */     }
/* 252 */     if (source.indexOf('{') == -1) {
/* 253 */       return source;
/*     */     }
/* 255 */     if (source.indexOf(':') != -1) {
/* 256 */       source = sanitizeSource(source);
/*     */     }
/* 258 */     Matcher matcher = NAMES_PATTERN.matcher(source);
/* 259 */     StringBuffer sb = new StringBuffer();
/* 260 */     while (matcher.find()) {
/* 261 */       String match = matcher.group(1);
/* 262 */       String varName = getVariableName(match);
/* 263 */       Object varValue = uriVariables.getValue(varName);
/* 264 */       if (UriTemplateVariables.SKIP_VALUE.equals(varValue)) {
/*     */         continue;
/*     */       }
/* 267 */       String formatted = getVariableValueAsString(varValue);
/* 268 */       formatted = (encoder != null) ? encoder.apply(formatted) : Matcher.quoteReplacement(formatted);
/* 269 */       matcher.appendReplacement(sb, formatted);
/*     */     } 
/* 271 */     matcher.appendTail(sb);
/* 272 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String sanitizeSource(String source) {
/* 279 */     int level = 0;
/* 280 */     int lastCharIndex = 0;
/* 281 */     char[] chars = new char[source.length()];
/* 282 */     for (int i = 0; i < source.length(); i++) {
/* 283 */       char c = source.charAt(i);
/* 284 */       if (c == '{') {
/* 285 */         level++;
/*     */       }
/* 287 */       if (c == '}') {
/* 288 */         level--;
/*     */       }
/* 290 */       if (level <= 1 && (level != 1 || c != '}'))
/*     */       {
/*     */         
/* 293 */         chars[lastCharIndex++] = c; } 
/*     */     } 
/* 295 */     return new String(chars, 0, lastCharIndex);
/*     */   }
/*     */   
/*     */   private static String getVariableName(String match) {
/* 299 */     int colonIdx = match.indexOf(':');
/* 300 */     return (colonIdx != -1) ? match.substring(0, colonIdx) : match;
/*     */   }
/*     */   
/*     */   private static String getVariableValueAsString(@Nullable Object variableValue) {
/* 304 */     return (variableValue != null) ? variableValue.toString() : "";
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
/*     */   public static interface UriTemplateVariables
/*     */   {
/* 319 */     public static final Object SKIP_VALUE = UriTemplateVariables.class;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     Object getValue(@Nullable String param1String);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class MapTemplateVariables
/*     */     implements UriTemplateVariables
/*     */   {
/*     */     private final Map<String, ?> uriVariables;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public MapTemplateVariables(Map<String, ?> uriVariables) {
/* 341 */       this.uriVariables = uriVariables;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Object getValue(@Nullable String name) {
/* 347 */       if (!this.uriVariables.containsKey(name)) {
/* 348 */         throw new IllegalArgumentException("Map has no value for '" + name + "'");
/*     */       }
/* 350 */       return this.uriVariables.get(name);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class VarArgsTemplateVariables
/*     */     implements UriTemplateVariables
/*     */   {
/*     */     private final Iterator<Object> valueIterator;
/*     */ 
/*     */     
/*     */     public VarArgsTemplateVariables(Object... uriVariableValues) {
/* 363 */       this.valueIterator = Arrays.<Object>asList(uriVariableValues).iterator();
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Object getValue(@Nullable String name) {
/* 369 */       if (!this.valueIterator.hasNext()) {
/* 370 */         throw new IllegalArgumentException("Not enough variable values available to expand '" + name + "'");
/*     */       }
/* 372 */       return this.valueIterator.next();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/UriComponents.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */