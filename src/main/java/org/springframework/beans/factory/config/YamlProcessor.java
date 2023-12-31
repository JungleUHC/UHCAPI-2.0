/*     */ package org.springframework.beans.factory.config;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.core.CollectionFactory;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.yaml.snakeyaml.DumperOptions;
/*     */ import org.yaml.snakeyaml.LoaderOptions;
/*     */ import org.yaml.snakeyaml.Yaml;
/*     */ import org.yaml.snakeyaml.constructor.BaseConstructor;
/*     */ import org.yaml.snakeyaml.constructor.Constructor;
/*     */ import org.yaml.snakeyaml.reader.UnicodeReader;
/*     */ import org.yaml.snakeyaml.representer.Representer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class YamlProcessor
/*     */ {
/*  60 */   private final Log logger = LogFactory.getLog(getClass());
/*     */   
/*  62 */   private ResolutionMethod resolutionMethod = ResolutionMethod.OVERRIDE;
/*     */   
/*  64 */   private Resource[] resources = new Resource[0];
/*     */   
/*  66 */   private List<DocumentMatcher> documentMatchers = Collections.emptyList();
/*     */   
/*     */   private boolean matchDefault = true;
/*     */   
/*  70 */   private Set<String> supportedTypes = Collections.emptySet();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDocumentMatchers(DocumentMatcher... matchers) {
/* 100 */     this.documentMatchers = Arrays.asList(matchers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMatchDefault(boolean matchDefault) {
/* 109 */     this.matchDefault = matchDefault;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setResolutionMethod(ResolutionMethod resolutionMethod) {
/* 118 */     Assert.notNull(resolutionMethod, "ResolutionMethod must not be null");
/* 119 */     this.resolutionMethod = resolutionMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setResources(Resource... resources) {
/* 127 */     this.resources = resources;
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
/*     */   public void setSupportedTypes(Class<?>... supportedTypes) {
/* 143 */     if (ObjectUtils.isEmpty((Object[])supportedTypes)) {
/* 144 */       this.supportedTypes = Collections.emptySet();
/*     */     } else {
/*     */       
/* 147 */       Assert.noNullElements((Object[])supportedTypes, "'supportedTypes' must not contain null elements");
/* 148 */       this
/* 149 */         .supportedTypes = (Set<String>)Arrays.<Class<?>>stream(supportedTypes).map(Class::getName).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
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
/*     */   protected void process(MatchCallback callback) {
/* 164 */     Yaml yaml = createYaml();
/* 165 */     for (Resource resource : this.resources) {
/* 166 */       boolean found = process(callback, yaml, resource);
/* 167 */       if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND && found) {
/*     */         return;
/*     */       }
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
/*     */   protected Yaml createYaml() {
/* 185 */     LoaderOptions loaderOptions = new LoaderOptions();
/* 186 */     loaderOptions.setAllowDuplicateKeys(false);
/* 187 */     return new Yaml((BaseConstructor)new FilteringConstructor(loaderOptions), new Representer(), new DumperOptions(), loaderOptions);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean process(MatchCallback callback, Yaml yaml, Resource resource) {
/* 192 */     int count = 0;
/*     */     try {
/* 194 */       if (this.logger.isDebugEnabled()) {
/* 195 */         this.logger.debug("Loading from YAML: " + resource);
/*     */       }
/* 197 */       try (UnicodeReader null = new UnicodeReader(resource.getInputStream())) {
/* 198 */         for (Object object : yaml.loadAll((Reader)unicodeReader)) {
/* 199 */           if (object != null && process(asMap(object), callback)) {
/* 200 */             count++;
/* 201 */             if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND) {
/*     */               break;
/*     */             }
/*     */           } 
/*     */         } 
/* 206 */         if (this.logger.isDebugEnabled()) {
/* 207 */           this.logger.debug("Loaded " + count + " document" + ((count > 1) ? "s" : "") + " from YAML resource: " + resource);
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 212 */     catch (IOException ex) {
/* 213 */       handleProcessError(resource, ex);
/*     */     } 
/* 215 */     return (count > 0);
/*     */   }
/*     */   
/*     */   private void handleProcessError(Resource resource, IOException ex) {
/* 219 */     if (this.resolutionMethod != ResolutionMethod.FIRST_FOUND && this.resolutionMethod != ResolutionMethod.OVERRIDE_AND_IGNORE)
/*     */     {
/* 221 */       throw new IllegalStateException(ex);
/*     */     }
/* 223 */     if (this.logger.isWarnEnabled()) {
/* 224 */       this.logger.warn("Could not load map from " + resource + ": " + ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<String, Object> asMap(Object object) {
/* 231 */     Map<String, Object> result = new LinkedHashMap<>();
/* 232 */     if (!(object instanceof Map)) {
/*     */       
/* 234 */       result.put("document", object);
/* 235 */       return result;
/*     */     } 
/*     */     
/* 238 */     Map<Object, Object> map = (Map<Object, Object>)object;
/* 239 */     map.forEach((key, value) -> {
/*     */           if (value instanceof Map) {
/*     */             value = (Object<String, Object>)asMap(value);
/*     */           }
/*     */           
/*     */           if (key instanceof CharSequence) {
/*     */             result.put(key.toString(), value);
/*     */           } else {
/*     */             result.put("[" + key.toString() + "]", value);
/*     */           } 
/*     */         });
/*     */     
/* 251 */     return result;
/*     */   }
/*     */   
/*     */   private boolean process(Map<String, Object> map, MatchCallback callback) {
/* 255 */     Properties properties = CollectionFactory.createStringAdaptingProperties();
/* 256 */     properties.putAll(getFlattenedMap(map));
/*     */     
/* 258 */     if (this.documentMatchers.isEmpty()) {
/* 259 */       if (this.logger.isDebugEnabled()) {
/* 260 */         this.logger.debug("Merging document (no matchers set): " + map);
/*     */       }
/* 262 */       callback.process(properties, map);
/* 263 */       return true;
/*     */     } 
/*     */     
/* 266 */     MatchStatus result = MatchStatus.ABSTAIN;
/* 267 */     for (DocumentMatcher matcher : this.documentMatchers) {
/* 268 */       MatchStatus match = matcher.matches(properties);
/* 269 */       result = MatchStatus.getMostSpecific(match, result);
/* 270 */       if (match == MatchStatus.FOUND) {
/* 271 */         if (this.logger.isDebugEnabled()) {
/* 272 */           this.logger.debug("Matched document with document matcher: " + properties);
/*     */         }
/* 274 */         callback.process(properties, map);
/* 275 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 279 */     if (result == MatchStatus.ABSTAIN && this.matchDefault) {
/* 280 */       if (this.logger.isDebugEnabled()) {
/* 281 */         this.logger.debug("Matched document with default matcher: " + map);
/*     */       }
/* 283 */       callback.process(properties, map);
/* 284 */       return true;
/*     */     } 
/*     */     
/* 287 */     if (this.logger.isDebugEnabled()) {
/* 288 */       this.logger.debug("Unmatched document: " + map);
/*     */     }
/* 290 */     return false;
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
/*     */   protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
/* 303 */     Map<String, Object> result = new LinkedHashMap<>();
/* 304 */     buildFlattenedMap(result, source, null);
/* 305 */     return result;
/*     */   }
/*     */   
/*     */   private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String path) {
/* 309 */     source.forEach((key, value) -> {
/*     */           if (StringUtils.hasText(path)) {
/*     */             if (key.startsWith("[")) {
/*     */               key = path + key;
/*     */             } else {
/*     */               key = path + '.' + key;
/*     */             } 
/*     */           }
/*     */           if (value instanceof String) {
/*     */             result.put(key, value);
/*     */           } else if (value instanceof Map) {
/*     */             Map<String, Object> map = (Map<String, Object>)value;
/*     */             buildFlattenedMap(result, map, key);
/*     */           } else if (value instanceof Collection) {
/*     */             Collection<Object> collection = (Collection<Object>)value;
/*     */             if (collection.isEmpty()) {
/*     */               result.put(key, "");
/*     */             } else {
/*     */               int count = 0;
/*     */               for (Object object : collection) {
/*     */                 buildFlattenedMap(result, Collections.singletonMap("[" + count++ + "]", object), key);
/*     */               }
/*     */             } 
/*     */           } else {
/*     */             result.put(key, (value != null) ? value : "");
/*     */           } 
/*     */         });
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
/*     */   @FunctionalInterface
/*     */   public static interface MatchCallback
/*     */   {
/*     */     void process(Properties param1Properties, Map<String, Object> param1Map);
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
/*     */   @FunctionalInterface
/*     */   public static interface DocumentMatcher
/*     */   {
/*     */     YamlProcessor.MatchStatus matches(Properties param1Properties);
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
/*     */   public enum MatchStatus
/*     */   {
/* 389 */     FOUND,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 394 */     NOT_FOUND,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 399 */     ABSTAIN;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static MatchStatus getMostSpecific(MatchStatus a, MatchStatus b) {
/* 405 */       return (a.ordinal() < b.ordinal()) ? a : b;
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
/*     */   public enum ResolutionMethod
/*     */   {
/* 418 */     OVERRIDE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 423 */     OVERRIDE_AND_IGNORE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 428 */     FIRST_FOUND;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class FilteringConstructor
/*     */     extends Constructor
/*     */   {
/*     */     FilteringConstructor(LoaderOptions loaderOptions) {
/* 440 */       super(loaderOptions);
/*     */     }
/*     */ 
/*     */     
/*     */     protected Class<?> getClassForName(String name) throws ClassNotFoundException {
/* 445 */       Assert.state(YamlProcessor.this.supportedTypes.contains(name), () -> "Unsupported type encountered in YAML document: " + name);
/*     */       
/* 447 */       return super.getClassForName(name);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/beans/factory/config/YamlProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */