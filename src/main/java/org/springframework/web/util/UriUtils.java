/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class UriUtils
/*     */ {
/*     */   public static String encodeScheme(String scheme, String encoding) {
/*  63 */     return encode(scheme, encoding, HierarchicalUriComponents.Type.SCHEME);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeScheme(String scheme, Charset charset) {
/*  74 */     return encode(scheme, charset, HierarchicalUriComponents.Type.SCHEME);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeAuthority(String authority, String encoding) {
/*  84 */     return encode(authority, encoding, HierarchicalUriComponents.Type.AUTHORITY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeAuthority(String authority, Charset charset) {
/*  95 */     return encode(authority, charset, HierarchicalUriComponents.Type.AUTHORITY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeUserInfo(String userInfo, String encoding) {
/* 105 */     return encode(userInfo, encoding, HierarchicalUriComponents.Type.USER_INFO);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeUserInfo(String userInfo, Charset charset) {
/* 116 */     return encode(userInfo, charset, HierarchicalUriComponents.Type.USER_INFO);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHost(String host, String encoding) {
/* 126 */     return encode(host, encoding, HierarchicalUriComponents.Type.HOST_IPV4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHost(String host, Charset charset) {
/* 137 */     return encode(host, charset, HierarchicalUriComponents.Type.HOST_IPV4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodePort(String port, String encoding) {
/* 147 */     return encode(port, encoding, HierarchicalUriComponents.Type.PORT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodePort(String port, Charset charset) {
/* 158 */     return encode(port, charset, HierarchicalUriComponents.Type.PORT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodePath(String path, String encoding) {
/* 168 */     return encode(path, encoding, HierarchicalUriComponents.Type.PATH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodePath(String path, Charset charset) {
/* 179 */     return encode(path, charset, HierarchicalUriComponents.Type.PATH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodePathSegment(String segment, String encoding) {
/* 189 */     return encode(segment, encoding, HierarchicalUriComponents.Type.PATH_SEGMENT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodePathSegment(String segment, Charset charset) {
/* 200 */     return encode(segment, charset, HierarchicalUriComponents.Type.PATH_SEGMENT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeQuery(String query, String encoding) {
/* 210 */     return encode(query, encoding, HierarchicalUriComponents.Type.QUERY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeQuery(String query, Charset charset) {
/* 221 */     return encode(query, charset, HierarchicalUriComponents.Type.QUERY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeQueryParam(String queryParam, String encoding) {
/* 231 */     return encode(queryParam, encoding, HierarchicalUriComponents.Type.QUERY_PARAM);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeQueryParam(String queryParam, Charset charset) {
/* 242 */     return encode(queryParam, charset, HierarchicalUriComponents.Type.QUERY_PARAM);
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
/*     */   public static MultiValueMap<String, String> encodeQueryParams(MultiValueMap<String, String> params) {
/* 263 */     Charset charset = StandardCharsets.UTF_8;
/* 264 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(params.size());
/* 265 */     for (Map.Entry<String, List<String>> entry : (Iterable<Map.Entry<String, List<String>>>)params.entrySet()) {
/* 266 */       for (String value : entry.getValue()) {
/* 267 */         linkedMultiValueMap.add(encodeQueryParam(entry.getKey(), charset), encodeQueryParam(value, charset));
/*     */       }
/*     */     } 
/* 270 */     return (MultiValueMap<String, String>)linkedMultiValueMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeFragment(String fragment, String encoding) {
/* 280 */     return encode(fragment, encoding, HierarchicalUriComponents.Type.FRAGMENT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeFragment(String fragment, Charset charset) {
/* 291 */     return encode(fragment, charset, HierarchicalUriComponents.Type.FRAGMENT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encode(String source, String encoding) {
/* 302 */     return encode(source, encoding, HierarchicalUriComponents.Type.URI);
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
/*     */   public static String encode(String source, Charset charset) {
/* 317 */     return encode(source, charset, HierarchicalUriComponents.Type.URI);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, String> encodeUriVariables(Map<String, ?> uriVariables) {
/* 328 */     Map<String, String> result = CollectionUtils.newLinkedHashMap(uriVariables.size());
/* 329 */     uriVariables.forEach((key, value) -> {
/*     */           String stringValue = (value != null) ? value.toString() : "";
/*     */           result.put(key, encode(stringValue, StandardCharsets.UTF_8));
/*     */         });
/* 333 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object[] encodeUriVariables(Object... uriVariables) {
/* 344 */     return Arrays.<Object>stream(uriVariables)
/* 345 */       .map(value -> {
/*     */           String stringValue = (value != null) ? value.toString() : "";
/*     */           
/*     */           return encode(stringValue, StandardCharsets.UTF_8);
/* 349 */         }).toArray();
/*     */   }
/*     */   
/*     */   private static String encode(String scheme, String encoding, HierarchicalUriComponents.Type type) {
/* 353 */     return HierarchicalUriComponents.encodeUriComponent(scheme, encoding, type);
/*     */   }
/*     */   
/*     */   private static String encode(String scheme, Charset charset, HierarchicalUriComponents.Type type) {
/* 357 */     return HierarchicalUriComponents.encodeUriComponent(scheme, charset, type);
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
/*     */   public static String decode(String source, String encoding) {
/* 372 */     return StringUtils.uriDecode(source, Charset.forName(encoding));
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
/*     */   public static String decode(String source, Charset charset) {
/* 387 */     return StringUtils.uriDecode(source, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static String extractFileExtension(String path) {
/* 398 */     int end = path.indexOf('?');
/* 399 */     int fragmentIndex = path.indexOf('#');
/* 400 */     if (fragmentIndex != -1 && (end == -1 || fragmentIndex < end)) {
/* 401 */       end = fragmentIndex;
/*     */     }
/* 403 */     if (end == -1) {
/* 404 */       end = path.length();
/*     */     }
/* 406 */     int begin = path.lastIndexOf('/', end) + 1;
/* 407 */     int paramIndex = path.indexOf(';', begin);
/* 408 */     end = (paramIndex != -1 && paramIndex < end) ? paramIndex : end;
/* 409 */     int extIndex = path.lastIndexOf('.', end);
/* 410 */     if (extIndex != -1 && extIndex >= begin) {
/* 411 */       return path.substring(extIndex + 1, end);
/*     */     }
/* 413 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/UriUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */