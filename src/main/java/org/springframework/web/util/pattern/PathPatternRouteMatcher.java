/*     */ package org.springframework.web.util.pattern;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.springframework.http.server.PathContainer;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.RouteMatcher;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathPatternRouteMatcher
/*     */   implements RouteMatcher
/*     */ {
/*     */   private final PathPatternParser parser;
/*  40 */   private final Map<String, PathPattern> pathPatternCache = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathPatternRouteMatcher() {
/*  49 */     this.parser = new PathPatternParser();
/*  50 */     this.parser.setPathOptions(PathContainer.Options.MESSAGE_ROUTE);
/*  51 */     this.parser.setMatchOptionalTrailingSeparator(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathPatternRouteMatcher(PathPatternParser parser) {
/*  58 */     Assert.notNull(parser, "PathPatternParser must not be null");
/*  59 */     this.parser = parser;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RouteMatcher.Route parseRoute(String routeValue) {
/*  65 */     return new PathContainerRoute(PathContainer.parsePath(routeValue, this.parser.getPathOptions()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPattern(String route) {
/*  70 */     return getPathPattern(route).hasPatternSyntax();
/*     */   }
/*     */ 
/*     */   
/*     */   public String combine(String pattern1, String pattern2) {
/*  75 */     return getPathPattern(pattern1).combine(getPathPattern(pattern2)).getPatternString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean match(String pattern, RouteMatcher.Route route) {
/*  80 */     return getPathPattern(pattern).matches(getPathContainer(route));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Map<String, String> matchAndExtract(String pattern, RouteMatcher.Route route) {
/*  86 */     PathPattern.PathMatchInfo info = getPathPattern(pattern).matchAndExtract(getPathContainer(route));
/*  87 */     return (info != null) ? info.getUriVariables() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Comparator<String> getPatternComparator(RouteMatcher.Route route) {
/*  92 */     return Comparator.comparing(this::getPathPattern);
/*     */   }
/*     */   
/*     */   private PathPattern getPathPattern(String pattern) {
/*  96 */     return this.pathPatternCache.computeIfAbsent(pattern, this.parser::parse);
/*     */   }
/*     */   
/*     */   private PathContainer getPathContainer(RouteMatcher.Route route) {
/* 100 */     Assert.isInstanceOf(PathContainerRoute.class, route);
/* 101 */     return ((PathContainerRoute)route).pathContainer;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class PathContainerRoute
/*     */     implements RouteMatcher.Route
/*     */   {
/*     */     private final PathContainer pathContainer;
/*     */     
/*     */     PathContainerRoute(PathContainer pathContainer) {
/* 111 */       this.pathContainer = pathContainer;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String value() {
/* 117 */       return this.pathContainer.value();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 123 */       return value();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/pattern/PathPatternRouteMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */