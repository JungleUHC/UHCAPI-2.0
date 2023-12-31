/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import java.net.URLDecoder;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.http.HttpServletMapping;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.MappingMatch;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ClassUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UrlPathHelper
/*     */ {
/*  63 */   public static final String PATH_ATTRIBUTE = UrlPathHelper.class.getName() + ".PATH";
/*     */ 
/*     */   
/*  66 */   static final boolean servlet4Present = ClassUtils.hasMethod(HttpServletRequest.class, "getHttpServletMapping", new Class[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String WEBSPHERE_URI_ATTRIBUTE = "com.ibm.websphere.servlet.uri_non_decoded";
/*     */ 
/*     */ 
/*     */   
/*  75 */   private static final Log logger = LogFactory.getLog(UrlPathHelper.class);
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   static volatile Boolean websphereComplianceFlag;
/*     */   
/*     */   private boolean alwaysUseFullPath = false;
/*     */   
/*     */   private boolean urlDecode = true;
/*     */   
/*     */   private boolean removeSemicolonContent = true;
/*     */   
/*  87 */   private String defaultEncoding = "ISO-8859-1";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean readOnly = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
/* 102 */     checkReadOnly();
/* 103 */     this.alwaysUseFullPath = alwaysUseFullPath;
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
/*     */   public void setUrlDecode(boolean urlDecode) {
/* 125 */     checkReadOnly();
/* 126 */     this.urlDecode = urlDecode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUrlDecode() {
/* 134 */     return this.urlDecode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
/* 142 */     checkReadOnly();
/* 143 */     this.removeSemicolonContent = removeSemicolonContent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean shouldRemoveSemicolonContent() {
/* 150 */     return this.removeSemicolonContent;
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
/*     */   public void setDefaultEncoding(String defaultEncoding) {
/* 167 */     checkReadOnly();
/* 168 */     this.defaultEncoding = defaultEncoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getDefaultEncoding() {
/* 175 */     return this.defaultEncoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setReadOnly() {
/* 182 */     this.readOnly = true;
/*     */   }
/*     */   
/*     */   private void checkReadOnly() {
/* 186 */     Assert.isTrue(!this.readOnly, "This instance cannot be modified");
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
/*     */   public String resolveAndCacheLookupPath(HttpServletRequest request) {
/* 199 */     String lookupPath = getLookupPathForRequest(request);
/* 200 */     request.setAttribute(PATH_ATTRIBUTE, lookupPath);
/* 201 */     return lookupPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getResolvedLookupPath(ServletRequest request) {
/* 212 */     String lookupPath = (String)request.getAttribute(PATH_ATTRIBUTE);
/* 213 */     Assert.notNull(lookupPath, "Expected lookupPath in request attribute \"" + PATH_ATTRIBUTE + "\".");
/* 214 */     return lookupPath;
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
/*     */   @Deprecated
/*     */   public String getLookupPathForRequest(HttpServletRequest request, @Nullable String name) {
/* 231 */     String result = null;
/* 232 */     if (name != null) {
/* 233 */       result = (String)request.getAttribute(name);
/*     */     }
/* 235 */     return (result != null) ? result : getLookupPathForRequest(request);
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
/*     */   public String getLookupPathForRequest(HttpServletRequest request) {
/* 248 */     String pathWithinApp = getPathWithinApplication(request);
/*     */     
/* 250 */     if (this.alwaysUseFullPath || skipServletPathDetermination(request)) {
/* 251 */       return pathWithinApp;
/*     */     }
/*     */     
/* 254 */     String rest = getPathWithinServletMapping(request, pathWithinApp);
/* 255 */     if (StringUtils.hasLength(rest)) {
/* 256 */       return rest;
/*     */     }
/*     */     
/* 259 */     return pathWithinApp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean skipServletPathDetermination(HttpServletRequest request) {
/* 270 */     if (servlet4Present) {
/* 271 */       return Servlet4Delegate.skipServletPathDetermination(request);
/*     */     }
/* 273 */     return false;
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
/*     */   public String getPathWithinServletMapping(HttpServletRequest request) {
/* 285 */     return getPathWithinServletMapping(request, getPathWithinApplication(request));
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
/*     */   protected String getPathWithinServletMapping(HttpServletRequest request, String pathWithinApp) {
/* 305 */     String path, servletPath = getServletPath(request);
/* 306 */     String sanitizedPathWithinApp = getSanitizedPath(pathWithinApp);
/*     */ 
/*     */ 
/*     */     
/* 310 */     if (servletPath.contains(sanitizedPathWithinApp)) {
/* 311 */       path = getRemainingPath(sanitizedPathWithinApp, servletPath, false);
/*     */     } else {
/*     */       
/* 314 */       path = getRemainingPath(pathWithinApp, servletPath, false);
/*     */     } 
/*     */     
/* 317 */     if (path != null)
/*     */     {
/* 319 */       return path;
/*     */     }
/*     */ 
/*     */     
/* 323 */     String pathInfo = request.getPathInfo();
/* 324 */     if (pathInfo != null)
/*     */     {
/*     */       
/* 327 */       return pathInfo;
/*     */     }
/* 329 */     if (!this.urlDecode) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 334 */       path = getRemainingPath(decodeInternal(request, pathWithinApp), servletPath, false);
/* 335 */       if (path != null) {
/* 336 */         return pathWithinApp;
/*     */       }
/*     */     } 
/*     */     
/* 340 */     return servletPath;
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
/*     */   public String getPathWithinApplication(HttpServletRequest request) {
/* 352 */     String contextPath = getContextPath(request);
/* 353 */     String requestUri = getRequestUri(request);
/* 354 */     String path = getRemainingPath(requestUri, contextPath, true);
/* 355 */     if (path != null)
/*     */     {
/* 357 */       return StringUtils.hasText(path) ? path : "/";
/*     */     }
/*     */     
/* 360 */     return requestUri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String getRemainingPath(String requestUri, String mapping, boolean ignoreCase) {
/* 372 */     int index1 = 0;
/* 373 */     int index2 = 0;
/* 374 */     while (index1 < requestUri.length() && index2 < mapping.length()) {
/* 375 */       char c1 = requestUri.charAt(index1);
/* 376 */       char c2 = mapping.charAt(index2);
/* 377 */       if (c1 == ';') {
/* 378 */         index1 = requestUri.indexOf('/', index1);
/* 379 */         if (index1 == -1) {
/* 380 */           return null;
/*     */         }
/* 382 */         c1 = requestUri.charAt(index1);
/*     */       } 
/* 384 */       if (c1 == c2 || (ignoreCase && Character.toLowerCase(c1) == Character.toLowerCase(c2))) {
/*     */         index1++; index2++; continue;
/*     */       } 
/* 387 */       return null;
/*     */     } 
/* 389 */     if (index2 != mapping.length()) {
/* 390 */       return null;
/*     */     }
/* 392 */     if (index1 == requestUri.length()) {
/* 393 */       return "";
/*     */     }
/* 395 */     if (requestUri.charAt(index1) == ';') {
/* 396 */       index1 = requestUri.indexOf('/', index1);
/*     */     }
/* 398 */     return (index1 != -1) ? requestUri.substring(index1) : "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getSanitizedPath(String path) {
/* 408 */     int start = path.indexOf("//");
/* 409 */     if (start == -1) {
/* 410 */       return path;
/*     */     }
/* 412 */     char[] content = path.toCharArray();
/* 413 */     int slowIndex = start;
/* 414 */     for (int fastIndex = start + 1; fastIndex < content.length; fastIndex++) {
/* 415 */       if (content[fastIndex] != '/' || content[slowIndex] != '/') {
/* 416 */         content[++slowIndex] = content[fastIndex];
/*     */       }
/*     */     } 
/* 419 */     return new String(content, 0, slowIndex + 1);
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
/*     */   public String getRequestUri(HttpServletRequest request) {
/* 434 */     String uri = (String)request.getAttribute("javax.servlet.include.request_uri");
/* 435 */     if (uri == null) {
/* 436 */       uri = request.getRequestURI();
/*     */     }
/* 438 */     return decodeAndCleanUriString(request, uri);
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
/*     */   public String getContextPath(HttpServletRequest request) {
/* 450 */     String contextPath = (String)request.getAttribute("javax.servlet.include.context_path");
/* 451 */     if (contextPath == null) {
/* 452 */       contextPath = request.getContextPath();
/*     */     }
/* 454 */     if (StringUtils.matchesCharacter(contextPath, '/'))
/*     */     {
/* 456 */       contextPath = "";
/*     */     }
/* 458 */     return decodeRequestString(request, contextPath);
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
/*     */   public String getServletPath(HttpServletRequest request) {
/* 470 */     String servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
/* 471 */     if (servletPath == null) {
/* 472 */       servletPath = request.getServletPath();
/*     */     }
/* 474 */     if (servletPath.length() > 1 && servletPath.endsWith("/") && shouldRemoveTrailingServletPathSlash(request))
/*     */     {
/*     */ 
/*     */       
/* 478 */       servletPath = servletPath.substring(0, servletPath.length() - 1);
/*     */     }
/* 480 */     return servletPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getOriginatingRequestUri(HttpServletRequest request) {
/* 489 */     String uri = (String)request.getAttribute("com.ibm.websphere.servlet.uri_non_decoded");
/* 490 */     if (uri == null) {
/* 491 */       uri = (String)request.getAttribute("javax.servlet.forward.request_uri");
/* 492 */       if (uri == null) {
/* 493 */         uri = request.getRequestURI();
/*     */       }
/*     */     } 
/* 496 */     return decodeAndCleanUriString(request, uri);
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
/*     */   public String getOriginatingContextPath(HttpServletRequest request) {
/* 508 */     String contextPath = (String)request.getAttribute("javax.servlet.forward.context_path");
/* 509 */     if (contextPath == null) {
/* 510 */       contextPath = request.getContextPath();
/*     */     }
/* 512 */     return decodeRequestString(request, contextPath);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getOriginatingServletPath(HttpServletRequest request) {
/* 522 */     String servletPath = (String)request.getAttribute("javax.servlet.forward.servlet_path");
/* 523 */     if (servletPath == null) {
/* 524 */       servletPath = request.getServletPath();
/*     */     }
/* 526 */     return servletPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getOriginatingQueryString(HttpServletRequest request) {
/* 536 */     if (request.getAttribute("javax.servlet.forward.request_uri") != null || request
/* 537 */       .getAttribute("javax.servlet.error.request_uri") != null) {
/* 538 */       return (String)request.getAttribute("javax.servlet.forward.query_string");
/*     */     }
/*     */     
/* 541 */     return request.getQueryString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String decodeAndCleanUriString(HttpServletRequest request, String uri) {
/* 549 */     uri = removeSemicolonContent(uri);
/* 550 */     uri = decodeRequestString(request, uri);
/* 551 */     uri = getSanitizedPath(uri);
/* 552 */     return uri;
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
/*     */   public String decodeRequestString(HttpServletRequest request, String source) {
/* 568 */     if (this.urlDecode) {
/* 569 */       return decodeInternal(request, source);
/*     */     }
/* 571 */     return source;
/*     */   }
/*     */ 
/*     */   
/*     */   private String decodeInternal(HttpServletRequest request, String source) {
/* 576 */     String enc = determineEncoding(request);
/*     */     try {
/* 578 */       return UriUtils.decode(source, enc);
/*     */     }
/* 580 */     catch (UnsupportedCharsetException ex) {
/* 581 */       if (logger.isDebugEnabled()) {
/* 582 */         logger.debug("Could not decode request string [" + source + "] with encoding '" + enc + "': falling back to platform default encoding; exception message: " + ex
/* 583 */             .getMessage());
/*     */       }
/* 585 */       return URLDecoder.decode(source);
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
/*     */   protected String determineEncoding(HttpServletRequest request) {
/* 600 */     String enc = request.getCharacterEncoding();
/* 601 */     if (enc == null) {
/* 602 */       enc = getDefaultEncoding();
/*     */     }
/* 604 */     return enc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String removeSemicolonContent(String requestUri) {
/* 615 */     return this.removeSemicolonContent ? 
/* 616 */       removeSemicolonContentInternal(requestUri) : removeJsessionid(requestUri);
/*     */   }
/*     */   
/*     */   private static String removeSemicolonContentInternal(String requestUri) {
/* 620 */     int semicolonIndex = requestUri.indexOf(';');
/* 621 */     if (semicolonIndex == -1) {
/* 622 */       return requestUri;
/*     */     }
/* 624 */     StringBuilder sb = new StringBuilder(requestUri);
/* 625 */     while (semicolonIndex != -1) {
/* 626 */       int slashIndex = sb.indexOf("/", semicolonIndex + 1);
/* 627 */       if (slashIndex == -1) {
/* 628 */         return sb.substring(0, semicolonIndex);
/*     */       }
/* 630 */       sb.delete(semicolonIndex, slashIndex);
/* 631 */       semicolonIndex = sb.indexOf(";", semicolonIndex);
/*     */     } 
/* 633 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private String removeJsessionid(String requestUri) {
/* 637 */     String key = ";jsessionid=";
/* 638 */     int index = requestUri.toLowerCase().indexOf(key);
/* 639 */     if (index == -1) {
/* 640 */       return requestUri;
/*     */     }
/* 642 */     String start = requestUri.substring(0, index);
/* 643 */     for (int i = index + key.length(); i < requestUri.length(); i++) {
/* 644 */       char c = requestUri.charAt(i);
/* 645 */       if (c == ';' || c == '/') {
/* 646 */         return start + requestUri.substring(i);
/*     */       }
/*     */     } 
/* 649 */     return start;
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
/*     */   public Map<String, String> decodePathVariables(HttpServletRequest request, Map<String, String> vars) {
/* 662 */     if (this.urlDecode) {
/* 663 */       return vars;
/*     */     }
/*     */     
/* 666 */     Map<String, String> decodedVars = CollectionUtils.newLinkedHashMap(vars.size());
/* 667 */     vars.forEach((key, value) -> (String)decodedVars.put(key, decodeInternal(request, value)));
/* 668 */     return decodedVars;
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
/*     */   public MultiValueMap<String, String> decodeMatrixVariables(HttpServletRequest request, MultiValueMap<String, String> vars) {
/* 684 */     if (this.urlDecode) {
/* 685 */       return vars;
/*     */     }
/*     */     
/* 688 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(vars.size());
/* 689 */     vars.forEach((key, values) -> {
/*     */           for (String value : values) {
/*     */             decodedVars.add(key, decodeInternal(request, value));
/*     */           }
/*     */         });
/* 694 */     return (MultiValueMap<String, String>)linkedMultiValueMap;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean shouldRemoveTrailingServletPathSlash(HttpServletRequest request) {
/* 699 */     if (request.getAttribute("com.ibm.websphere.servlet.uri_non_decoded") == null)
/*     */     {
/*     */ 
/*     */       
/* 703 */       return false;
/*     */     }
/* 705 */     Boolean flagToUse = websphereComplianceFlag;
/* 706 */     if (flagToUse == null) {
/* 707 */       ClassLoader classLoader = UrlPathHelper.class.getClassLoader();
/* 708 */       String className = "com.ibm.ws.webcontainer.WebContainer";
/* 709 */       String methodName = "getWebContainerProperties";
/* 710 */       String propName = "com.ibm.ws.webcontainer.removetrailingservletpathslash";
/* 711 */       boolean flag = false;
/*     */       try {
/* 713 */         Class<?> cl = classLoader.loadClass(className);
/* 714 */         Properties prop = (Properties)cl.getMethod(methodName, new Class[0]).invoke(null, new Object[0]);
/* 715 */         flag = Boolean.parseBoolean(prop.getProperty(propName));
/*     */       }
/* 717 */       catch (Throwable ex) {
/* 718 */         if (logger.isDebugEnabled()) {
/* 719 */           logger.debug("Could not introspect WebSphere web container properties: " + ex);
/*     */         }
/*     */       } 
/* 722 */       flagToUse = Boolean.valueOf(flag);
/* 723 */       websphereComplianceFlag = Boolean.valueOf(flag);
/*     */     } 
/*     */ 
/*     */     
/* 727 */     return !flagToUse.booleanValue();
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
/* 740 */   public static final UrlPathHelper defaultInstance = new UrlPathHelper();
/*     */   
/*     */   static {
/* 743 */     defaultInstance.setReadOnly();
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
/* 756 */   public static final UrlPathHelper rawPathInstance = new UrlPathHelper()
/*     */     {
/*     */       public String removeSemicolonContent(String requestUri)
/*     */       {
/* 760 */         return requestUri;
/*     */       }
/*     */     };
/*     */   
/*     */   static {
/* 765 */     rawPathInstance.setAlwaysUseFullPath(true);
/* 766 */     rawPathInstance.setUrlDecode(false);
/* 767 */     rawPathInstance.setRemoveSemicolonContent(false);
/* 768 */     rawPathInstance.setReadOnly();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Servlet4Delegate
/*     */   {
/*     */     public static boolean skipServletPathDetermination(HttpServletRequest request) {
/* 779 */       HttpServletMapping mapping = (HttpServletMapping)request.getAttribute("javax.servlet.include.mapping");
/* 780 */       if (mapping == null) {
/* 781 */         mapping = request.getHttpServletMapping();
/*     */       }
/* 783 */       MappingMatch match = mapping.getMappingMatch();
/* 784 */       return (match != null && (!match.equals(MappingMatch.PATH) || mapping.getPattern().equals("/*")));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/UrlPathHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */