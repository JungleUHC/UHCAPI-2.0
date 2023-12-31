/*    */ package org.springframework.web.cors.reactive;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ import org.springframework.http.server.PathContainer;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.web.cors.CorsConfiguration;
/*    */ import org.springframework.web.server.ServerWebExchange;
/*    */ import org.springframework.web.util.pattern.PathPattern;
/*    */ import org.springframework.web.util.pattern.PathPatternParser;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UrlBasedCorsConfigurationSource
/*    */   implements CorsConfigurationSource
/*    */ {
/*    */   private final PathPatternParser patternParser;
/* 42 */   private final Map<PathPattern, CorsConfiguration> corsConfigurations = new LinkedHashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UrlBasedCorsConfigurationSource() {
/* 51 */     this(PathPatternParser.defaultInstance);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UrlBasedCorsConfigurationSource(PathPatternParser patternParser) {
/* 59 */     this.patternParser = patternParser;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCorsConfigurations(@Nullable Map<String, CorsConfiguration> configMap) {
/* 67 */     this.corsConfigurations.clear();
/* 68 */     if (configMap != null) {
/* 69 */       configMap.forEach(this::registerCorsConfiguration);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void registerCorsConfiguration(String path, CorsConfiguration config) {
/* 77 */     this.corsConfigurations.put(this.patternParser.parse(path), config);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
/* 83 */     PathContainer path = exchange.getRequest().getPath().pathWithinApplication();
/* 84 */     for (Map.Entry<PathPattern, CorsConfiguration> entry : this.corsConfigurations.entrySet()) {
/* 85 */       if (((PathPattern)entry.getKey()).matches(path)) {
/* 86 */         return entry.getValue();
/*    */       }
/*    */     } 
/* 89 */     return null;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/cors/reactive/UrlBasedCorsConfigurationSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */