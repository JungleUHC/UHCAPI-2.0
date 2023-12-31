/*     */ package org.springframework.web.context.support;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.core.io.Resource;
/*     */ import org.springframework.core.io.ResourceLoader;
/*     */ import org.springframework.core.io.UrlResource;
/*     */ import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletContextResourcePatternResolver
/*     */   extends PathMatchingResourcePatternResolver
/*     */ {
/*  49 */   private static final Log logger = LogFactory.getLog(ServletContextResourcePatternResolver.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletContextResourcePatternResolver(ServletContext servletContext) {
/*  58 */     super((ResourceLoader)new ServletContextResourceLoader(servletContext));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletContextResourcePatternResolver(ResourceLoader resourceLoader) {
/*  67 */     super(resourceLoader);
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
/*     */   protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException {
/*  84 */     if (rootDirResource instanceof ServletContextResource) {
/*  85 */       ServletContextResource scResource = (ServletContextResource)rootDirResource;
/*  86 */       ServletContext sc = scResource.getServletContext();
/*  87 */       String fullPattern = scResource.getPath() + subPattern;
/*  88 */       Set<Resource> result = new LinkedHashSet<>(8);
/*  89 */       doRetrieveMatchingServletContextResources(sc, fullPattern, scResource.getPath(), result);
/*  90 */       return result;
/*     */     } 
/*     */     
/*  93 */     return super.doFindPathMatchingFileResources(rootDirResource, subPattern);
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
/*     */   protected void doRetrieveMatchingServletContextResources(ServletContext servletContext, String fullPattern, String dir, Set<Resource> result) throws IOException {
/* 113 */     Set<String> candidates = servletContext.getResourcePaths(dir);
/* 114 */     if (candidates != null) {
/* 115 */       boolean dirDepthNotFixed = fullPattern.contains("**");
/* 116 */       int jarFileSep = fullPattern.indexOf("!/");
/* 117 */       String jarFilePath = null;
/* 118 */       String pathInJarFile = null;
/* 119 */       if (jarFileSep > 0 && jarFileSep + "!/".length() < fullPattern.length()) {
/* 120 */         jarFilePath = fullPattern.substring(0, jarFileSep);
/* 121 */         pathInJarFile = fullPattern.substring(jarFileSep + "!/".length());
/*     */       } 
/* 123 */       for (String currPath : candidates) {
/* 124 */         if (!currPath.startsWith(dir)) {
/*     */ 
/*     */           
/* 127 */           int dirIndex = currPath.indexOf(dir);
/* 128 */           if (dirIndex != -1) {
/* 129 */             currPath = currPath.substring(dirIndex);
/*     */           }
/*     */         } 
/* 132 */         if (currPath.endsWith("/") && (dirDepthNotFixed || StringUtils.countOccurrencesOf(currPath, "/") <= 
/* 133 */           StringUtils.countOccurrencesOf(fullPattern, "/")))
/*     */         {
/*     */           
/* 136 */           doRetrieveMatchingServletContextResources(servletContext, fullPattern, currPath, result);
/*     */         }
/* 138 */         if (jarFilePath != null && getPathMatcher().match(jarFilePath, currPath)) {
/*     */           
/* 140 */           String absoluteJarPath = servletContext.getRealPath(currPath);
/* 141 */           if (absoluteJarPath != null) {
/* 142 */             doRetrieveMatchingJarEntries(absoluteJarPath, pathInJarFile, result);
/*     */           }
/*     */         } 
/* 145 */         if (getPathMatcher().match(fullPattern, currPath)) {
/* 146 */           result.add(new ServletContextResource(servletContext, currPath));
/*     */         }
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
/*     */   private void doRetrieveMatchingJarEntries(String jarFilePath, String entryPattern, Set<Resource> result) {
/* 159 */     if (logger.isDebugEnabled()) {
/* 160 */       logger.debug("Searching jar file [" + jarFilePath + "] for entries matching [" + entryPattern + "]");
/*     */     }
/* 162 */     try (JarFile jarFile = new JarFile(jarFilePath)) {
/* 163 */       for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
/* 164 */         JarEntry entry = entries.nextElement();
/* 165 */         String entryPath = entry.getName();
/* 166 */         if (getPathMatcher().match(entryPattern, entryPath)) {
/* 167 */           result.add(new UrlResource("jar", "file:" + jarFilePath + "!/" + entryPath));
/*     */         
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 173 */     catch (IOException ex) {
/* 174 */       if (logger.isWarnEnabled())
/* 175 */         logger.warn("Cannot search for matching resources in jar file [" + jarFilePath + "] because the jar cannot be opened through the file system", ex); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/context/support/ServletContextResourcePatternResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */