/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.List;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.http.HttpServletMapping;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.MappingMatch;
/*     */ import org.springframework.http.server.PathContainer;
/*     */ import org.springframework.http.server.RequestPath;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ObjectUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ServletRequestPathUtils
/*     */ {
/*  49 */   public static final String PATH_ATTRIBUTE = ServletRequestPathUtils.class.getName() + ".PATH";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestPath parseAndCache(HttpServletRequest request) {
/*  66 */     RequestPath requestPath = ServletRequestPath.parse(request);
/*  67 */     request.setAttribute(PATH_ATTRIBUTE, requestPath);
/*  68 */     return requestPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestPath getParsedRequestPath(ServletRequest request) {
/*  76 */     RequestPath path = (RequestPath)request.getAttribute(PATH_ATTRIBUTE);
/*  77 */     Assert.notNull(path, () -> "Expected parsed RequestPath in request attribute \"" + PATH_ATTRIBUTE + "\".");
/*  78 */     return path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setParsedRequestPath(@Nullable RequestPath requestPath, ServletRequest request) {
/*  89 */     if (requestPath != null) {
/*  90 */       request.setAttribute(PATH_ATTRIBUTE, requestPath);
/*     */     } else {
/*     */       
/*  93 */       request.removeAttribute(PATH_ATTRIBUTE);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasParsedRequestPath(ServletRequest request) {
/* 101 */     return (request.getAttribute(PATH_ATTRIBUTE) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clearParsedRequestPath(ServletRequest request) {
/* 109 */     request.removeAttribute(PATH_ATTRIBUTE);
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
/*     */   public static Object getCachedPath(ServletRequest request) {
/* 136 */     String lookupPath = (String)request.getAttribute(UrlPathHelper.PATH_ATTRIBUTE);
/* 137 */     if (lookupPath != null) {
/* 138 */       return lookupPath;
/*     */     }
/* 140 */     RequestPath requestPath = (RequestPath)request.getAttribute(PATH_ATTRIBUTE);
/* 141 */     if (requestPath != null) {
/* 142 */       return requestPath.pathWithinApplication();
/*     */     }
/* 144 */     throw new IllegalArgumentException("Neither a pre-parsed RequestPath nor a pre-resolved String lookupPath is available.");
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
/*     */   public static String getCachedPathValue(ServletRequest request) {
/* 161 */     Object path = getCachedPath(request);
/* 162 */     if (path instanceof PathContainer) {
/* 163 */       String value = ((PathContainer)path).value();
/* 164 */       path = UrlPathHelper.defaultInstance.removeSemicolonContent(value);
/*     */     } 
/* 166 */     return (String)path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasCachedPath(ServletRequest request) {
/* 177 */     return (request.getAttribute(PATH_ATTRIBUTE) != null || request
/* 178 */       .getAttribute(UrlPathHelper.PATH_ATTRIBUTE) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ServletRequestPath
/*     */     implements RequestPath
/*     */   {
/*     */     private final RequestPath requestPath;
/*     */ 
/*     */     
/*     */     private final PathContainer contextPath;
/*     */ 
/*     */ 
/*     */     
/*     */     private ServletRequestPath(String rawPath, @Nullable String contextPath, String servletPathPrefix) {
/* 194 */       Assert.notNull(servletPathPrefix, "`servletPathPrefix` is required");
/* 195 */       this.requestPath = RequestPath.parse(rawPath, contextPath + servletPathPrefix);
/* 196 */       this.contextPath = PathContainer.parsePath(StringUtils.hasText(contextPath) ? contextPath : "");
/*     */     }
/*     */ 
/*     */     
/*     */     public String value() {
/* 201 */       return this.requestPath.value();
/*     */     }
/*     */ 
/*     */     
/*     */     public List<PathContainer.Element> elements() {
/* 206 */       return this.requestPath.elements();
/*     */     }
/*     */ 
/*     */     
/*     */     public PathContainer contextPath() {
/* 211 */       return this.contextPath;
/*     */     }
/*     */ 
/*     */     
/*     */     public PathContainer pathWithinApplication() {
/* 216 */       return this.requestPath.pathWithinApplication();
/*     */     }
/*     */ 
/*     */     
/*     */     public RequestPath modifyContextPath(String contextPath) {
/* 221 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 227 */       if (this == other) {
/* 228 */         return true;
/*     */       }
/* 230 */       if (other == null || getClass() != other.getClass()) {
/* 231 */         return false;
/*     */       }
/* 233 */       return this.requestPath.equals(((ServletRequestPath)other).requestPath);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 238 */       return this.requestPath.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 243 */       return this.requestPath.toString();
/*     */     }
/*     */ 
/*     */     
/*     */     public static RequestPath parse(HttpServletRequest request) {
/* 248 */       String requestUri = (String)request.getAttribute("javax.servlet.include.request_uri");
/* 249 */       if (requestUri == null) {
/* 250 */         requestUri = request.getRequestURI();
/*     */       }
/* 252 */       if (UrlPathHelper.servlet4Present) {
/* 253 */         String servletPathPrefix = ServletRequestPathUtils.Servlet4Delegate.getServletPathPrefix(request);
/* 254 */         if (StringUtils.hasText(servletPathPrefix)) {
/* 255 */           return new ServletRequestPath(requestUri, request.getContextPath(), servletPathPrefix);
/*     */         }
/*     */       } 
/* 258 */       return RequestPath.parse(requestUri, request.getContextPath());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Servlet4Delegate
/*     */   {
/*     */     @Nullable
/*     */     public static String getServletPathPrefix(HttpServletRequest request) {
/* 271 */       HttpServletMapping mapping = (HttpServletMapping)request.getAttribute("javax.servlet.include.mapping");
/* 272 */       if (mapping == null) {
/* 273 */         mapping = request.getHttpServletMapping();
/*     */       }
/* 275 */       MappingMatch match = mapping.getMappingMatch();
/* 276 */       if (!ObjectUtils.nullSafeEquals(match, MappingMatch.PATH)) {
/* 277 */         return null;
/*     */       }
/* 279 */       String servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
/* 280 */       servletPath = (servletPath != null) ? servletPath : request.getServletPath();
/* 281 */       return UriUtils.encodePath(servletPath, StandardCharsets.UTF_8);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/ServletRequestPathUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */