/*     */ package org.springframework.http.server;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ final class DefaultPathContainer
/*     */   implements PathContainer
/*     */ {
/*  44 */   private static final PathContainer EMPTY_PATH = new DefaultPathContainer("", Collections.emptyList());
/*     */   
/*  46 */   private static final Map<Character, DefaultSeparator> SEPARATORS = new HashMap<>(2);
/*     */   
/*     */   static {
/*  49 */     SEPARATORS.put(Character.valueOf('/'), new DefaultSeparator('/', "%2F"));
/*  50 */     SEPARATORS.put(Character.valueOf('.'), new DefaultSeparator('.', "%2E"));
/*     */   }
/*     */ 
/*     */   
/*     */   private final String path;
/*     */   
/*     */   private final List<PathContainer.Element> elements;
/*     */ 
/*     */   
/*     */   private DefaultPathContainer(String path, List<PathContainer.Element> elements) {
/*  60 */     this.path = path;
/*  61 */     this.elements = Collections.unmodifiableList(elements);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String value() {
/*  67 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PathContainer.Element> elements() {
/*  72 */     return this.elements;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/*  78 */     if (this == other) {
/*  79 */       return true;
/*     */     }
/*  81 */     if (!(other instanceof PathContainer)) {
/*  82 */       return false;
/*     */     }
/*  84 */     return value().equals(((PathContainer)other).value());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  89 */     return this.path.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  94 */     return value();
/*     */   }
/*     */   
/*     */   static PathContainer createFromUrlPath(String path, PathContainer.Options options) {
/*     */     int begin;
/*  99 */     if (path.isEmpty()) {
/* 100 */       return EMPTY_PATH;
/*     */     }
/* 102 */     char separator = options.separator();
/* 103 */     DefaultSeparator separatorElement = SEPARATORS.get(Character.valueOf(separator));
/* 104 */     if (separatorElement == null) {
/* 105 */       throw new IllegalArgumentException("Unexpected separator: '" + separator + "'");
/*     */     }
/* 107 */     List<PathContainer.Element> elements = new ArrayList<>();
/*     */     
/* 109 */     if (path.charAt(0) == separator) {
/* 110 */       begin = 1;
/* 111 */       elements.add(separatorElement);
/*     */     } else {
/*     */       
/* 114 */       begin = 0;
/*     */     } 
/* 116 */     while (begin < path.length()) {
/* 117 */       int end = path.indexOf(separator, begin);
/* 118 */       String segment = (end != -1) ? path.substring(begin, end) : path.substring(begin);
/* 119 */       if (!segment.isEmpty()) {
/* 120 */         elements.add(options.shouldDecodeAndParseSegments() ? 
/* 121 */             decodeAndParsePathSegment(segment) : 
/* 122 */             DefaultPathSegment.from(segment, separatorElement));
/*     */       }
/* 124 */       if (end == -1) {
/*     */         break;
/*     */       }
/* 127 */       elements.add(separatorElement);
/* 128 */       begin = end + 1;
/*     */     } 
/* 130 */     return new DefaultPathContainer(path, elements);
/*     */   }
/*     */   
/*     */   private static PathContainer.PathSegment decodeAndParsePathSegment(String segment) {
/* 134 */     Charset charset = StandardCharsets.UTF_8;
/* 135 */     int index = segment.indexOf(';');
/* 136 */     if (index == -1) {
/* 137 */       String str = StringUtils.uriDecode(segment, charset);
/* 138 */       return DefaultPathSegment.from(segment, str);
/*     */     } 
/*     */     
/* 141 */     String valueToMatch = StringUtils.uriDecode(segment.substring(0, index), charset);
/* 142 */     String pathParameterContent = segment.substring(index);
/* 143 */     MultiValueMap<String, String> parameters = parsePathParams(pathParameterContent, charset);
/* 144 */     return DefaultPathSegment.from(segment, valueToMatch, parameters);
/*     */   }
/*     */ 
/*     */   
/*     */   private static MultiValueMap<String, String> parsePathParams(String input, Charset charset) {
/* 149 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/* 150 */     int begin = 1;
/* 151 */     while (begin < input.length()) {
/* 152 */       int end = input.indexOf(';', begin);
/* 153 */       String param = (end != -1) ? input.substring(begin, end) : input.substring(begin);
/* 154 */       parsePathParamValues(param, charset, (MultiValueMap<String, String>)linkedMultiValueMap);
/* 155 */       if (end == -1) {
/*     */         break;
/*     */       }
/* 158 */       begin = end + 1;
/*     */     } 
/* 160 */     return (MultiValueMap<String, String>)linkedMultiValueMap;
/*     */   }
/*     */   
/*     */   private static void parsePathParamValues(String input, Charset charset, MultiValueMap<String, String> output) {
/* 164 */     if (StringUtils.hasText(input)) {
/* 165 */       int index = input.indexOf('=');
/* 166 */       if (index != -1) {
/* 167 */         String name = input.substring(0, index);
/* 168 */         name = StringUtils.uriDecode(name, charset);
/* 169 */         if (StringUtils.hasText(name)) {
/* 170 */           String value = input.substring(index + 1);
/* 171 */           for (String v : StringUtils.commaDelimitedListToStringArray(value)) {
/* 172 */             output.add(name, StringUtils.uriDecode(v, charset));
/*     */           }
/*     */         } 
/*     */       } else {
/*     */         
/* 177 */         String name = StringUtils.uriDecode(input, charset);
/* 178 */         if (StringUtils.hasText(name)) {
/* 179 */           output.add(input, "");
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   static PathContainer subPath(PathContainer container, int fromIndex, int toIndex) {
/* 186 */     List<PathContainer.Element> elements = container.elements();
/* 187 */     if (fromIndex == 0 && toIndex == elements.size()) {
/* 188 */       return container;
/*     */     }
/* 190 */     if (fromIndex == toIndex) {
/* 191 */       return EMPTY_PATH;
/*     */     }
/*     */     
/* 194 */     Assert.isTrue((fromIndex >= 0 && fromIndex < elements.size()), () -> "Invalid fromIndex: " + fromIndex);
/* 195 */     Assert.isTrue((toIndex >= 0 && toIndex <= elements.size()), () -> "Invalid toIndex: " + toIndex);
/* 196 */     Assert.isTrue((fromIndex < toIndex), () -> "fromIndex: " + fromIndex + " should be < toIndex " + toIndex);
/*     */     
/* 198 */     List<PathContainer.Element> subList = elements.subList(fromIndex, toIndex);
/* 199 */     String path = subList.stream().map(PathContainer.Element::value).collect(Collectors.joining(""));
/* 200 */     return new DefaultPathContainer(path, subList);
/*     */   }
/*     */ 
/*     */   
/*     */   private static class DefaultSeparator
/*     */     implements PathContainer.Separator
/*     */   {
/*     */     private final String separator;
/*     */     
/*     */     private final String encodedSequence;
/*     */     
/*     */     DefaultSeparator(char separator, String encodedSequence) {
/* 212 */       this.separator = String.valueOf(separator);
/* 213 */       this.encodedSequence = encodedSequence;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String value() {
/* 219 */       return this.separator;
/*     */     }
/*     */     
/*     */     public String encodedSequence() {
/* 223 */       return this.encodedSequence;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class DefaultPathSegment
/*     */     implements PathContainer.PathSegment
/*     */   {
/* 231 */     private static final MultiValueMap<String, String> EMPTY_PARAMS = CollectionUtils.unmodifiableMultiValueMap((MultiValueMap)new LinkedMultiValueMap());
/*     */ 
/*     */     
/*     */     private final String value;
/*     */ 
/*     */     
/*     */     private final String valueToMatch;
/*     */ 
/*     */     
/*     */     private final MultiValueMap<String, String> parameters;
/*     */ 
/*     */     
/*     */     static DefaultPathSegment from(String value, DefaultPathContainer.DefaultSeparator separator) {
/* 244 */       String valueToMatch = value.contains(separator.encodedSequence()) ? value.replaceAll(separator.encodedSequence(), separator.value()) : value;
/* 245 */       return from(value, valueToMatch);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static DefaultPathSegment from(String value, String valueToMatch) {
/* 252 */       return new DefaultPathSegment(value, valueToMatch, EMPTY_PARAMS);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static DefaultPathSegment from(String value, String valueToMatch, MultiValueMap<String, String> params) {
/* 259 */       return new DefaultPathSegment(value, valueToMatch, CollectionUtils.unmodifiableMultiValueMap(params));
/*     */     }
/*     */     
/*     */     private DefaultPathSegment(String value, String valueToMatch, MultiValueMap<String, String> params) {
/* 263 */       this.value = value;
/* 264 */       this.valueToMatch = valueToMatch;
/* 265 */       this.parameters = params;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String value() {
/* 271 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String valueToMatch() {
/* 276 */       return this.valueToMatch;
/*     */     }
/*     */ 
/*     */     
/*     */     public char[] valueToMatchAsChars() {
/* 281 */       return this.valueToMatch.toCharArray();
/*     */     }
/*     */ 
/*     */     
/*     */     public MultiValueMap<String, String> parameters() {
/* 286 */       return this.parameters;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 291 */       if (this == other) {
/* 292 */         return true;
/*     */       }
/* 294 */       if (!(other instanceof PathContainer.PathSegment)) {
/* 295 */         return false;
/*     */       }
/* 297 */       return value().equals(((PathContainer.PathSegment)other).value());
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 302 */       return this.value.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 307 */       return "[value='" + this.value + "']";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/server/DefaultPathContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */