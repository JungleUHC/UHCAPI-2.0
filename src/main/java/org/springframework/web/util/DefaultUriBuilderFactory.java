/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class DefaultUriBuilderFactory
/*     */   implements UriBuilderFactory
/*     */ {
/*     */   @Nullable
/*     */   private final UriComponentsBuilder baseUri;
/*  47 */   private EncodingMode encodingMode = EncodingMode.TEMPLATE_AND_VALUES;
/*     */   
/*  49 */   private final Map<String, Object> defaultUriVariables = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean parsePath = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultUriBuilderFactory() {
/*  59 */     this.baseUri = null;
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
/*     */   public DefaultUriBuilderFactory(String baseUriTemplate) {
/*  72 */     this.baseUri = UriComponentsBuilder.fromUriString(baseUriTemplate);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultUriBuilderFactory(UriComponentsBuilder baseUri) {
/*  80 */     this.baseUri = baseUri;
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
/*     */   public void setEncodingMode(EncodingMode encodingMode) {
/*  95 */     this.encodingMode = encodingMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EncodingMode getEncodingMode() {
/* 102 */     return this.encodingMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultUriVariables(@Nullable Map<String, ?> defaultUriVariables) {
/* 111 */     this.defaultUriVariables.clear();
/* 112 */     if (defaultUriVariables != null) {
/* 113 */       this.defaultUriVariables.putAll(defaultUriVariables);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, ?> getDefaultUriVariables() {
/* 121 */     return Collections.unmodifiableMap(this.defaultUriVariables);
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
/*     */   public void setParsePath(boolean parsePath) {
/* 133 */     this.parsePath = parsePath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean shouldParsePath() {
/* 141 */     return this.parsePath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI expand(String uriTemplate, Map<String, ?> uriVars) {
/* 149 */     return uriString(uriTemplate).build(uriVars);
/*     */   }
/*     */ 
/*     */   
/*     */   public URI expand(String uriTemplate, Object... uriVars) {
/* 154 */     return uriString(uriTemplate).build(uriVars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UriBuilder uriString(String uriTemplate) {
/* 161 */     return new DefaultUriBuilder(uriTemplate);
/*     */   }
/*     */ 
/*     */   
/*     */   public UriBuilder builder() {
/* 166 */     return new DefaultUriBuilder("");
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum EncodingMode
/*     */   {
/* 199 */     TEMPLATE_AND_VALUES,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 208 */     VALUES_ONLY,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 217 */     URI_COMPONENT,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 222 */     NONE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class DefaultUriBuilder
/*     */     implements UriBuilder
/*     */   {
/*     */     private final UriComponentsBuilder uriComponentsBuilder;
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder(String uriTemplate) {
/* 234 */       this.uriComponentsBuilder = initUriComponentsBuilder(uriTemplate);
/*     */     }
/*     */     
/*     */     private UriComponentsBuilder initUriComponentsBuilder(String uriTemplate) {
/*     */       UriComponentsBuilder result;
/* 239 */       if (!StringUtils.hasLength(uriTemplate)) {
/* 240 */         result = (DefaultUriBuilderFactory.this.baseUri != null) ? DefaultUriBuilderFactory.this.baseUri.cloneBuilder() : UriComponentsBuilder.newInstance();
/*     */       }
/* 242 */       else if (DefaultUriBuilderFactory.this.baseUri != null) {
/* 243 */         UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriTemplate);
/* 244 */         UriComponents uri = builder.build();
/* 245 */         result = (uri.getHost() == null) ? DefaultUriBuilderFactory.this.baseUri.cloneBuilder().uriComponents(uri) : builder;
/*     */       } else {
/*     */         
/* 248 */         result = UriComponentsBuilder.fromUriString(uriTemplate);
/*     */       } 
/* 250 */       if (DefaultUriBuilderFactory.this.encodingMode.equals(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES)) {
/* 251 */         result.encode();
/*     */       }
/* 253 */       parsePathIfNecessary(result);
/* 254 */       return result;
/*     */     }
/*     */     
/*     */     private void parsePathIfNecessary(UriComponentsBuilder result) {
/* 258 */       if (DefaultUriBuilderFactory.this.parsePath && DefaultUriBuilderFactory.this.encodingMode.equals(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT)) {
/* 259 */         UriComponents uric = result.build();
/* 260 */         String path = uric.getPath();
/* 261 */         result.replacePath((String)null);
/* 262 */         for (String segment : uric.getPathSegments()) {
/* 263 */           result.pathSegment(new String[] { segment });
/*     */         } 
/* 265 */         if (path != null && path.endsWith("/")) {
/* 266 */           result.path("/");
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder scheme(@Nullable String scheme) {
/* 274 */       this.uriComponentsBuilder.scheme(scheme);
/* 275 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder userInfo(@Nullable String userInfo) {
/* 280 */       this.uriComponentsBuilder.userInfo(userInfo);
/* 281 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder host(@Nullable String host) {
/* 286 */       this.uriComponentsBuilder.host(host);
/* 287 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder port(int port) {
/* 292 */       this.uriComponentsBuilder.port(port);
/* 293 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder port(@Nullable String port) {
/* 298 */       this.uriComponentsBuilder.port(port);
/* 299 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder path(String path) {
/* 304 */       this.uriComponentsBuilder.path(path);
/* 305 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder replacePath(@Nullable String path) {
/* 310 */       this.uriComponentsBuilder.replacePath(path);
/* 311 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder pathSegment(String... pathSegments) {
/* 316 */       this.uriComponentsBuilder.pathSegment(pathSegments);
/* 317 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder query(String query) {
/* 322 */       this.uriComponentsBuilder.query(query);
/* 323 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder replaceQuery(@Nullable String query) {
/* 328 */       this.uriComponentsBuilder.replaceQuery(query);
/* 329 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder queryParam(String name, Object... values) {
/* 334 */       this.uriComponentsBuilder.queryParam(name, values);
/* 335 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder queryParam(String name, @Nullable Collection<?> values) {
/* 340 */       this.uriComponentsBuilder.queryParam(name, values);
/* 341 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder queryParamIfPresent(String name, Optional<?> value) {
/* 346 */       this.uriComponentsBuilder.queryParamIfPresent(name, value);
/* 347 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder queryParams(MultiValueMap<String, String> params) {
/* 352 */       this.uriComponentsBuilder.queryParams(params);
/* 353 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder replaceQueryParam(String name, Object... values) {
/* 358 */       this.uriComponentsBuilder.replaceQueryParam(name, values);
/* 359 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder replaceQueryParam(String name, @Nullable Collection<?> values) {
/* 364 */       this.uriComponentsBuilder.replaceQueryParam(name, values);
/* 365 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder replaceQueryParams(MultiValueMap<String, String> params) {
/* 370 */       this.uriComponentsBuilder.replaceQueryParams(params);
/* 371 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultUriBuilder fragment(@Nullable String fragment) {
/* 376 */       this.uriComponentsBuilder.fragment(fragment);
/* 377 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public URI build(Map<String, ?> uriVars) {
/* 382 */       if (!DefaultUriBuilderFactory.this.defaultUriVariables.isEmpty()) {
/* 383 */         Map<String, Object> map = new HashMap<>();
/* 384 */         map.putAll(DefaultUriBuilderFactory.this.defaultUriVariables);
/* 385 */         map.putAll(uriVars);
/* 386 */         uriVars = map;
/*     */       } 
/* 388 */       if (DefaultUriBuilderFactory.this.encodingMode.equals(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY)) {
/* 389 */         uriVars = UriUtils.encodeUriVariables(uriVars);
/*     */       }
/* 391 */       UriComponents uric = this.uriComponentsBuilder.build().expand(uriVars);
/* 392 */       return createUri(uric);
/*     */     }
/*     */ 
/*     */     
/*     */     public URI build(Object... uriVars) {
/* 397 */       if (ObjectUtils.isEmpty(uriVars) && !DefaultUriBuilderFactory.this.defaultUriVariables.isEmpty()) {
/* 398 */         return build(Collections.emptyMap());
/*     */       }
/* 400 */       if (DefaultUriBuilderFactory.this.encodingMode.equals(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY)) {
/* 401 */         uriVars = UriUtils.encodeUriVariables(uriVars);
/*     */       }
/* 403 */       UriComponents uric = this.uriComponentsBuilder.build().expand(uriVars);
/* 404 */       return createUri(uric);
/*     */     }
/*     */     
/*     */     private URI createUri(UriComponents uric) {
/* 408 */       if (DefaultUriBuilderFactory.this.encodingMode.equals(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT)) {
/* 409 */         uric = uric.encode();
/*     */       }
/* 411 */       return URI.create(uric.toString());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/DefaultUriBuilderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */