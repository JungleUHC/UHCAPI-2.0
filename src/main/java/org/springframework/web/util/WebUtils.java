/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.net.URI;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TreeMap;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletRequestWrapper;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.ServletResponseWrapper;
/*     */ import javax.servlet.http.Cookie;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpRequest;
/*     */ import org.springframework.http.server.ServletServerHttpRequest;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class WebUtils
/*     */ {
/*     */   public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
/*     */   public static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path";
/*     */   public static final String INCLUDE_SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path";
/*     */   public static final String INCLUDE_PATH_INFO_ATTRIBUTE = "javax.servlet.include.path_info";
/*     */   public static final String INCLUDE_QUERY_STRING_ATTRIBUTE = "javax.servlet.include.query_string";
/*     */   public static final String FORWARD_REQUEST_URI_ATTRIBUTE = "javax.servlet.forward.request_uri";
/*     */   public static final String FORWARD_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.forward.context_path";
/*     */   public static final String FORWARD_SERVLET_PATH_ATTRIBUTE = "javax.servlet.forward.servlet_path";
/*     */   public static final String FORWARD_PATH_INFO_ATTRIBUTE = "javax.servlet.forward.path_info";
/*     */   public static final String FORWARD_QUERY_STRING_ATTRIBUTE = "javax.servlet.forward.query_string";
/*     */   public static final String ERROR_STATUS_CODE_ATTRIBUTE = "javax.servlet.error.status_code";
/*     */   public static final String ERROR_EXCEPTION_TYPE_ATTRIBUTE = "javax.servlet.error.exception_type";
/*     */   public static final String ERROR_MESSAGE_ATTRIBUTE = "javax.servlet.error.message";
/*     */   public static final String ERROR_EXCEPTION_ATTRIBUTE = "javax.servlet.error.exception";
/*     */   public static final String ERROR_REQUEST_URI_ATTRIBUTE = "javax.servlet.error.request_uri";
/*     */   public static final String ERROR_SERVLET_NAME_ATTRIBUTE = "javax.servlet.error.servlet_name";
/*     */   public static final String CONTENT_TYPE_CHARSET_PREFIX = ";charset=";
/*     */   public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";
/*     */   public static final String TEMP_DIR_CONTEXT_ATTRIBUTE = "javax.servlet.context.tempdir";
/*     */   public static final String HTML_ESCAPE_CONTEXT_PARAM = "defaultHtmlEscape";
/*     */   public static final String RESPONSE_ENCODED_HTML_ESCAPE_CONTEXT_PARAM = "responseEncodedHtmlEscape";
/*     */   public static final String WEB_APP_ROOT_KEY_PARAM = "webAppRootKey";
/*     */   public static final String DEFAULT_WEB_APP_ROOT_KEY = "webapp.root";
/* 218 */   public static final String[] SUBMIT_IMAGE_SUFFIXES = new String[] { ".x", ".y" };
/*     */ 
/*     */   
/* 221 */   public static final String SESSION_MUTEX_ATTRIBUTE = WebUtils.class.getName() + ".MUTEX";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setWebAppRootSystemProperty(ServletContext servletContext) throws IllegalStateException {
/* 238 */     Assert.notNull(servletContext, "ServletContext must not be null");
/* 239 */     String root = servletContext.getRealPath("/");
/* 240 */     if (root == null) {
/* 241 */       throw new IllegalStateException("Cannot set web app root system property when WAR file is not expanded");
/*     */     }
/*     */     
/* 244 */     String param = servletContext.getInitParameter("webAppRootKey");
/* 245 */     String key = (param != null) ? param : "webapp.root";
/* 246 */     String oldValue = System.getProperty(key);
/* 247 */     if (oldValue != null && !StringUtils.pathEquals(oldValue, root)) {
/* 248 */       throw new IllegalStateException("Web app root system property already set to different value: '" + key + "' = [" + oldValue + "] instead of [" + root + "] - Choose unique values for the 'webAppRootKey' context-param in your web.xml files!");
/*     */     }
/*     */ 
/*     */     
/* 252 */     System.setProperty(key, root);
/* 253 */     servletContext.log("Set web app root system property: '" + key + "' = [" + root + "]");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void removeWebAppRootSystemProperty(ServletContext servletContext) {
/* 263 */     Assert.notNull(servletContext, "ServletContext must not be null");
/* 264 */     String param = servletContext.getInitParameter("webAppRootKey");
/* 265 */     String key = (param != null) ? param : "webapp.root";
/* 266 */     System.getProperties().remove(key);
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
/*     */   @Nullable
/*     */   public static Boolean getDefaultHtmlEscape(@Nullable ServletContext servletContext) {
/* 282 */     if (servletContext == null) {
/* 283 */       return null;
/*     */     }
/* 285 */     String param = servletContext.getInitParameter("defaultHtmlEscape");
/* 286 */     return StringUtils.hasText(param) ? Boolean.valueOf(param) : null;
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
/*     */   @Nullable
/*     */   public static Boolean getResponseEncodedHtmlEscape(@Nullable ServletContext servletContext) {
/* 305 */     if (servletContext == null) {
/* 306 */       return null;
/*     */     }
/* 308 */     String param = servletContext.getInitParameter("responseEncodedHtmlEscape");
/* 309 */     return StringUtils.hasText(param) ? Boolean.valueOf(param) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static File getTempDir(ServletContext servletContext) {
/* 319 */     Assert.notNull(servletContext, "ServletContext must not be null");
/* 320 */     return (File)servletContext.getAttribute("javax.servlet.context.tempdir");
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
/*     */   public static String getRealPath(ServletContext servletContext, String path) throws FileNotFoundException {
/* 337 */     Assert.notNull(servletContext, "ServletContext must not be null");
/*     */     
/* 339 */     if (!path.startsWith("/")) {
/* 340 */       path = "/" + path;
/*     */     }
/* 342 */     String realPath = servletContext.getRealPath(path);
/* 343 */     if (realPath == null) {
/* 344 */       throw new FileNotFoundException("ServletContext resource [" + path + "] cannot be resolved to absolute file path - web application archive not expanded?");
/*     */     }
/*     */ 
/*     */     
/* 348 */     return realPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static String getSessionId(HttpServletRequest request) {
/* 358 */     Assert.notNull(request, "Request must not be null");
/* 359 */     HttpSession session = request.getSession(false);
/* 360 */     return (session != null) ? session.getId() : null;
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
/*     */   @Nullable
/*     */   public static Object getSessionAttribute(HttpServletRequest request, String name) {
/* 373 */     Assert.notNull(request, "Request must not be null");
/* 374 */     HttpSession session = request.getSession(false);
/* 375 */     return (session != null) ? session.getAttribute(name) : null;
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
/*     */   public static Object getRequiredSessionAttribute(HttpServletRequest request, String name) throws IllegalStateException {
/* 390 */     Object attr = getSessionAttribute(request, name);
/* 391 */     if (attr == null) {
/* 392 */       throw new IllegalStateException("No session attribute '" + name + "' found");
/*     */     }
/* 394 */     return attr;
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
/*     */   public static void setSessionAttribute(HttpServletRequest request, String name, @Nullable Object value) {
/* 406 */     Assert.notNull(request, "Request must not be null");
/* 407 */     if (value != null) {
/* 408 */       request.getSession().setAttribute(name, value);
/*     */     } else {
/*     */       
/* 411 */       HttpSession session = request.getSession(false);
/* 412 */       if (session != null) {
/* 413 */         session.removeAttribute(name);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object getSessionMutex(HttpSession session) {
/* 439 */     Assert.notNull(session, "Session must not be null");
/* 440 */     Object mutex = session.getAttribute(SESSION_MUTEX_ATTRIBUTE);
/* 441 */     if (mutex == null) {
/* 442 */       mutex = session;
/*     */     }
/* 444 */     return mutex;
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
/*     */   @Nullable
/*     */   public static <T> T getNativeRequest(ServletRequest request, @Nullable Class<T> requiredType) {
/* 459 */     if (requiredType != null) {
/* 460 */       if (requiredType.isInstance(request)) {
/* 461 */         return (T)request;
/*     */       }
/* 463 */       if (request instanceof ServletRequestWrapper) {
/* 464 */         return getNativeRequest(((ServletRequestWrapper)request).getRequest(), requiredType);
/*     */       }
/*     */     } 
/* 467 */     return null;
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
/*     */   @Nullable
/*     */   public static <T> T getNativeResponse(ServletResponse response, @Nullable Class<T> requiredType) {
/* 481 */     if (requiredType != null) {
/* 482 */       if (requiredType.isInstance(response)) {
/* 483 */         return (T)response;
/*     */       }
/* 485 */       if (response instanceof ServletResponseWrapper) {
/* 486 */         return getNativeResponse(((ServletResponseWrapper)response).getResponse(), requiredType);
/*     */       }
/*     */     } 
/* 489 */     return null;
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
/*     */   public static boolean isIncludeRequest(ServletRequest request) {
/* 502 */     return (request.getAttribute("javax.servlet.include.request_uri") != null);
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
/*     */   public static void exposeErrorRequestAttributes(HttpServletRequest request, Throwable ex, @Nullable String servletName) {
/* 526 */     exposeRequestAttributeIfNotPresent((ServletRequest)request, "javax.servlet.error.status_code", Integer.valueOf(200));
/* 527 */     exposeRequestAttributeIfNotPresent((ServletRequest)request, "javax.servlet.error.exception_type", ex.getClass());
/* 528 */     exposeRequestAttributeIfNotPresent((ServletRequest)request, "javax.servlet.error.message", ex.getMessage());
/* 529 */     exposeRequestAttributeIfNotPresent((ServletRequest)request, "javax.servlet.error.exception", ex);
/* 530 */     exposeRequestAttributeIfNotPresent((ServletRequest)request, "javax.servlet.error.request_uri", request.getRequestURI());
/* 531 */     if (servletName != null) {
/* 532 */       exposeRequestAttributeIfNotPresent((ServletRequest)request, "javax.servlet.error.servlet_name", servletName);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void exposeRequestAttributeIfNotPresent(ServletRequest request, String name, Object value) {
/* 543 */     if (request.getAttribute(name) == null) {
/* 544 */       request.setAttribute(name, value);
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
/*     */   public static void clearErrorRequestAttributes(HttpServletRequest request) {
/* 560 */     request.removeAttribute("javax.servlet.error.status_code");
/* 561 */     request.removeAttribute("javax.servlet.error.exception_type");
/* 562 */     request.removeAttribute("javax.servlet.error.message");
/* 563 */     request.removeAttribute("javax.servlet.error.exception");
/* 564 */     request.removeAttribute("javax.servlet.error.request_uri");
/* 565 */     request.removeAttribute("javax.servlet.error.servlet_name");
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
/*     */   public static Cookie getCookie(HttpServletRequest request, String name) {
/* 577 */     Assert.notNull(request, "Request must not be null");
/* 578 */     Cookie[] cookies = request.getCookies();
/* 579 */     if (cookies != null) {
/* 580 */       for (Cookie cookie : cookies) {
/* 581 */         if (name.equals(cookie.getName())) {
/* 582 */           return cookie;
/*     */         }
/*     */       } 
/*     */     }
/* 586 */     return null;
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
/*     */   public static boolean hasSubmitParameter(ServletRequest request, String name) {
/* 599 */     Assert.notNull(request, "Request must not be null");
/* 600 */     if (request.getParameter(name) != null) {
/* 601 */       return true;
/*     */     }
/* 603 */     for (String suffix : SUBMIT_IMAGE_SUFFIXES) {
/* 604 */       if (request.getParameter(name + suffix) != null) {
/* 605 */         return true;
/*     */       }
/*     */     } 
/* 608 */     return false;
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
/*     */   @Nullable
/*     */   public static String findParameterValue(ServletRequest request, String name) {
/* 622 */     return findParameterValue(request.getParameterMap(), name);
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
/*     */   
/*     */   @Nullable
/*     */   public static String findParameterValue(Map<String, ?> parameters, String name) {
/* 651 */     Object value = parameters.get(name);
/* 652 */     if (value instanceof String[]) {
/* 653 */       String[] values = (String[])value;
/* 654 */       return (values.length > 0) ? values[0] : null;
/*     */     } 
/* 656 */     if (value != null) {
/* 657 */       return value.toString();
/*     */     }
/*     */     
/* 660 */     String prefix = name + "_";
/* 661 */     for (String paramName : parameters.keySet()) {
/* 662 */       if (paramName.startsWith(prefix)) {
/*     */         
/* 664 */         for (String suffix : SUBMIT_IMAGE_SUFFIXES) {
/* 665 */           if (paramName.endsWith(suffix)) {
/* 666 */             return paramName.substring(prefix.length(), paramName.length() - suffix.length());
/*     */           }
/*     */         } 
/* 669 */         return paramName.substring(prefix.length());
/*     */       } 
/*     */     } 
/*     */     
/* 673 */     return null;
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
/*     */   public static Map<String, Object> getParametersStartingWith(ServletRequest request, @Nullable String prefix) {
/* 691 */     Assert.notNull(request, "Request must not be null");
/* 692 */     Enumeration<String> paramNames = request.getParameterNames();
/* 693 */     Map<String, Object> params = new TreeMap<>();
/* 694 */     if (prefix == null) {
/* 695 */       prefix = "";
/*     */     }
/* 697 */     while (paramNames != null && paramNames.hasMoreElements()) {
/* 698 */       String paramName = paramNames.nextElement();
/* 699 */       if (prefix.isEmpty() || paramName.startsWith(prefix)) {
/* 700 */         String unprefixed = paramName.substring(prefix.length());
/* 701 */         String[] values = request.getParameterValues(paramName);
/* 702 */         if (values == null || values.length == 0) {
/*     */           continue;
/*     */         }
/* 705 */         if (values.length > 1) {
/* 706 */           params.put(unprefixed, values);
/*     */           continue;
/*     */         } 
/* 709 */         params.put(unprefixed, values[0]);
/*     */       } 
/*     */     } 
/*     */     
/* 713 */     return params;
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
/*     */   public static MultiValueMap<String, String> parseMatrixVariables(String matrixVariables) {
/* 726 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/* 727 */     if (!StringUtils.hasText(matrixVariables)) {
/* 728 */       return (MultiValueMap<String, String>)linkedMultiValueMap;
/*     */     }
/* 730 */     StringTokenizer pairs = new StringTokenizer(matrixVariables, ";");
/* 731 */     while (pairs.hasMoreTokens()) {
/* 732 */       String pair = pairs.nextToken();
/* 733 */       int index = pair.indexOf('=');
/* 734 */       if (index != -1) {
/* 735 */         String name = pair.substring(0, index);
/* 736 */         if (name.equalsIgnoreCase("jsessionid")) {
/*     */           continue;
/*     */         }
/* 739 */         String rawValue = pair.substring(index + 1);
/* 740 */         for (String value : StringUtils.commaDelimitedListToStringArray(rawValue)) {
/* 741 */           linkedMultiValueMap.add(name, value);
/*     */         }
/*     */         continue;
/*     */       } 
/* 745 */       linkedMultiValueMap.add(pair, "");
/*     */     } 
/*     */     
/* 748 */     return (MultiValueMap<String, String>)linkedMultiValueMap;
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
/*     */   public static boolean isValidOrigin(HttpRequest request, Collection<String> allowedOrigins) {
/* 765 */     Assert.notNull(request, "Request must not be null");
/* 766 */     Assert.notNull(allowedOrigins, "Allowed origins must not be null");
/*     */     
/* 768 */     String origin = request.getHeaders().getOrigin();
/* 769 */     if (origin == null || allowedOrigins.contains("*")) {
/* 770 */       return true;
/*     */     }
/* 772 */     if (CollectionUtils.isEmpty(allowedOrigins)) {
/* 773 */       return isSameOrigin(request);
/*     */     }
/*     */     
/* 776 */     return allowedOrigins.contains(origin);
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
/*     */   public static boolean isSameOrigin(HttpRequest request) {
/*     */     String scheme, host;
/*     */     int port;
/* 795 */     HttpHeaders headers = request.getHeaders();
/* 796 */     String origin = headers.getOrigin();
/* 797 */     if (origin == null) {
/* 798 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 804 */     if (request instanceof ServletServerHttpRequest) {
/*     */       
/* 806 */       HttpServletRequest servletRequest = ((ServletServerHttpRequest)request).getServletRequest();
/* 807 */       scheme = servletRequest.getScheme();
/* 808 */       host = servletRequest.getServerName();
/* 809 */       port = servletRequest.getServerPort();
/*     */     } else {
/*     */       
/* 812 */       URI uri = request.getURI();
/* 813 */       scheme = uri.getScheme();
/* 814 */       host = uri.getHost();
/* 815 */       port = uri.getPort();
/*     */     } 
/*     */     
/* 818 */     UriComponents originUrl = UriComponentsBuilder.fromOriginHeader(origin).build();
/* 819 */     return (ObjectUtils.nullSafeEquals(scheme, originUrl.getScheme()) && 
/* 820 */       ObjectUtils.nullSafeEquals(host, originUrl.getHost()) && 
/* 821 */       getPort(scheme, port) == getPort(originUrl.getScheme(), originUrl.getPort()));
/*     */   }
/*     */   
/*     */   private static int getPort(@Nullable String scheme, int port) {
/* 825 */     if (port == -1) {
/* 826 */       if ("http".equals(scheme) || "ws".equals(scheme)) {
/* 827 */         port = 80;
/*     */       }
/* 829 */       else if ("https".equals(scheme) || "wss".equals(scheme)) {
/* 830 */         port = 443;
/*     */       } 
/*     */     }
/* 833 */     return port;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/WebUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */