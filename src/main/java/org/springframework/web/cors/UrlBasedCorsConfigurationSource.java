/*     */ package org.springframework.web.cors;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.springframework.http.server.PathContainer;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.AntPathMatcher;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.PathMatcher;
/*     */ import org.springframework.web.util.ServletRequestPathUtils;
/*     */ import org.springframework.web.util.UrlPathHelper;
/*     */ import org.springframework.web.util.pattern.PathPattern;
/*     */ import org.springframework.web.util.pattern.PathPatternParser;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UrlBasedCorsConfigurationSource
/*     */   implements CorsConfigurationSource
/*     */ {
/*  56 */   private static PathMatcher defaultPathMatcher = (PathMatcher)new AntPathMatcher();
/*     */ 
/*     */   
/*     */   private final PathPatternParser patternParser;
/*     */   
/*  61 */   private UrlPathHelper urlPathHelper = UrlPathHelper.defaultInstance;
/*     */   
/*  63 */   private PathMatcher pathMatcher = defaultPathMatcher;
/*     */   
/*     */   @Nullable
/*     */   private String lookupPathAttributeName;
/*     */   
/*     */   private boolean allowInitLookupPath = true;
/*     */   
/*  70 */   private final Map<PathPattern, CorsConfiguration> corsConfigurations = new LinkedHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBasedCorsConfigurationSource() {
/*  77 */     this(PathPatternParser.defaultInstance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBasedCorsConfigurationSource(PathPatternParser parser) {
/*  86 */     Assert.notNull(parser, "PathPatternParser must not be null");
/*  87 */     this.patternParser = parser;
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
/*     */   public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
/* 101 */     initUrlPathHelper();
/* 102 */     this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
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
/*     */   @Deprecated
/*     */   public void setUrlDecode(boolean urlDecode) {
/* 115 */     initUrlPathHelper();
/* 116 */     this.urlPathHelper.setUrlDecode(urlDecode);
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
/*     */   @Deprecated
/*     */   public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
/* 129 */     initUrlPathHelper();
/* 130 */     this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
/*     */   }
/*     */   
/*     */   private void initUrlPathHelper() {
/* 134 */     if (this.urlPathHelper == UrlPathHelper.defaultInstance) {
/* 135 */       this.urlPathHelper = new UrlPathHelper();
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
/*     */   public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
/* 147 */     Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
/* 148 */     this.urlPathHelper = urlPathHelper;
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
/*     */   public void setAllowInitLookupPath(boolean allowInitLookupPath) {
/* 173 */     this.allowInitLookupPath = allowInitLookupPath;
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
/*     */   @Deprecated
/*     */   public void setLookupPathAttributeName(String name) {
/* 186 */     this.lookupPathAttributeName = name;
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
/*     */   public void setPathMatcher(PathMatcher pathMatcher) {
/* 200 */     this.pathMatcher = pathMatcher;
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
/*     */   public void setCorsConfigurations(@Nullable Map<String, CorsConfiguration> corsConfigurations) {
/* 214 */     this.corsConfigurations.clear();
/* 215 */     if (corsConfigurations != null) {
/* 216 */       corsConfigurations.forEach(this::registerCorsConfiguration);
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
/*     */   public void registerCorsConfiguration(String pattern, CorsConfiguration config) {
/* 228 */     this.corsConfigurations.put(this.patternParser.parse(pattern), config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, CorsConfiguration> getCorsConfigurations() {
/* 235 */     Map<String, CorsConfiguration> result = CollectionUtils.newHashMap(this.corsConfigurations.size());
/* 236 */     this.corsConfigurations.forEach((pattern, config) -> (CorsConfiguration)result.put(pattern.getPatternString(), config));
/* 237 */     return Collections.unmodifiableMap(result);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
/* 244 */     Object path = resolvePath(request);
/* 245 */     boolean isPathContainer = path instanceof PathContainer;
/* 246 */     for (Map.Entry<PathPattern, CorsConfiguration> entry : this.corsConfigurations.entrySet()) {
/* 247 */       if (match(path, isPathContainer, entry.getKey())) {
/* 248 */         return entry.getValue();
/*     */       }
/*     */     } 
/*     */     
/* 252 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private Object resolvePath(HttpServletRequest request) {
/* 257 */     if (this.allowInitLookupPath && !ServletRequestPathUtils.hasCachedPath((ServletRequest)request)) {
/* 258 */       return (this.lookupPathAttributeName != null) ? this.urlPathHelper
/* 259 */         .getLookupPathForRequest(request, this.lookupPathAttributeName) : this.urlPathHelper
/* 260 */         .getLookupPathForRequest(request);
/*     */     }
/* 262 */     Object lookupPath = ServletRequestPathUtils.getCachedPath((ServletRequest)request);
/* 263 */     if (this.pathMatcher != defaultPathMatcher) {
/* 264 */       lookupPath = lookupPath.toString();
/*     */     }
/* 266 */     return lookupPath;
/*     */   }
/*     */   
/*     */   private boolean match(Object path, boolean isPathContainer, PathPattern pattern) {
/* 270 */     return isPathContainer ? pattern
/* 271 */       .matches((PathContainer)path) : this.pathMatcher
/* 272 */       .match(pattern.getPatternString(), (String)path);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/UrlBasedCorsConfigurationSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */