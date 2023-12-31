/*     */ package org.springframework.http.client.reactive;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.HttpCookie;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.jetty.client.api.Response;
/*     */ import org.eclipse.jetty.reactive.client.ReactiveResponse;
/*     */ import org.reactivestreams.Publisher;
/*     */ import org.springframework.core.io.buffer.DataBuffer;
/*     */ import org.springframework.http.HttpHeaders;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.ResponseCookie;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ import reactor.core.publisher.Flux;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class JettyClientHttpResponse
/*     */   implements ClientHttpResponse
/*     */ {
/*  51 */   private static final Pattern SAMESITE_PATTERN = Pattern.compile("(?i).*SameSite=(Strict|Lax|None).*");
/*     */   
/*  53 */   private static final ClassLoader classLoader = JettyClientHttpResponse.class.getClassLoader();
/*     */ 
/*     */   
/*     */   private static final boolean jetty10Present;
/*     */   
/*     */   private final ReactiveResponse reactiveResponse;
/*     */   
/*     */   private final Flux<DataBuffer> content;
/*     */   
/*     */   private final HttpHeaders headers;
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/*  67 */       Class<?> httpFieldsClass = classLoader.loadClass("org.eclipse.jetty.http.HttpFields");
/*  68 */       jetty10Present = httpFieldsClass.isInterface();
/*     */     }
/*  70 */     catch (ClassNotFoundException ex) {
/*  71 */       throw new IllegalStateException("No compatible Jetty version found", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public JettyClientHttpResponse(ReactiveResponse reactiveResponse, Publisher<DataBuffer> content) {
/*  77 */     this.reactiveResponse = reactiveResponse;
/*  78 */     this.content = Flux.from(content);
/*     */ 
/*     */ 
/*     */     
/*  82 */     MultiValueMap<String, String> headers = jetty10Present ? (MultiValueMap<String, String>)Jetty10HttpFieldsHelper.getHttpHeaders(reactiveResponse) : new JettyHeadersAdapter(reactiveResponse.getHeaders());
/*     */     
/*  84 */     this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpStatus getStatusCode() {
/*  90 */     return HttpStatus.valueOf(getRawStatusCode());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRawStatusCode() {
/*  95 */     return this.reactiveResponse.getStatus();
/*     */   }
/*     */ 
/*     */   
/*     */   public MultiValueMap<String, ResponseCookie> getCookies() {
/* 100 */     LinkedMultiValueMap linkedMultiValueMap = new LinkedMultiValueMap();
/* 101 */     List<String> cookieHeader = getHeaders().get("Set-Cookie");
/* 102 */     if (cookieHeader != null) {
/* 103 */       cookieHeader.forEach(header -> HttpCookie.parse(header).forEach(()));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     return CollectionUtils.unmodifiableMultiValueMap((MultiValueMap)linkedMultiValueMap);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String parseSameSite(String headerValue) {
/* 120 */     Matcher matcher = SAMESITE_PATTERN.matcher(headerValue);
/* 121 */     return matcher.matches() ? matcher.group(1) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Flux<DataBuffer> getBody() {
/* 127 */     return this.content;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders getHeaders() {
/* 132 */     return this.headers;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class Jetty10HttpFieldsHelper
/*     */   {
/*     */     private static final Method getHeadersMethod;
/*     */     
/*     */     private static final Method getNameMethod;
/*     */     
/*     */     private static final Method getValueMethod;
/*     */     
/*     */     static {
/*     */       try {
/* 146 */         getHeadersMethod = Response.class.getMethod("getHeaders", new Class[0]);
/* 147 */         Class<?> type = JettyClientHttpResponse.classLoader.loadClass("org.eclipse.jetty.http.HttpField");
/* 148 */         getNameMethod = type.getMethod("getName", new Class[0]);
/* 149 */         getValueMethod = type.getMethod("getValue", new Class[0]);
/*     */       }
/* 151 */       catch (ClassNotFoundException|NoSuchMethodException ex) {
/* 152 */         throw new IllegalStateException("No compatible Jetty version found", ex);
/*     */       } 
/*     */     }
/*     */     
/*     */     public static HttpHeaders getHttpHeaders(ReactiveResponse response) {
/* 157 */       HttpHeaders headers = new HttpHeaders();
/*     */       
/* 159 */       Iterable<?> iterator = (Iterable)ReflectionUtils.invokeMethod(getHeadersMethod, response.getResponse());
/* 160 */       Assert.notNull(iterator, "Iterator must not be null");
/* 161 */       for (Object field : iterator) {
/* 162 */         String name = (String)ReflectionUtils.invokeMethod(getNameMethod, field);
/* 163 */         Assert.notNull(name, "Header name must not be null");
/* 164 */         String value = (String)ReflectionUtils.invokeMethod(getValueMethod, field);
/* 165 */         headers.add(name, value);
/*     */       } 
/* 167 */       return headers;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/reactive/JettyClientHttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */