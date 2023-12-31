/*    */ package org.springframework.web.server;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedHashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import org.springframework.http.HttpHeaders;
/*    */ import org.springframework.http.HttpMethod;
/*    */ import org.springframework.http.HttpStatus;
/*    */ import org.springframework.lang.Nullable;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.util.CollectionUtils;
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
/*    */ public class MethodNotAllowedException
/*    */   extends ResponseStatusException
/*    */ {
/*    */   private final String method;
/*    */   private final Set<HttpMethod> httpMethods;
/*    */   
/*    */   public MethodNotAllowedException(HttpMethod method, Collection<HttpMethod> supportedMethods) {
/* 47 */     this(method.name(), supportedMethods);
/*    */   }
/*    */   
/*    */   public MethodNotAllowedException(String method, @Nullable Collection<HttpMethod> supportedMethods) {
/* 51 */     super(HttpStatus.METHOD_NOT_ALLOWED, "Request method '" + method + "' not supported");
/* 52 */     Assert.notNull(method, "'method' is required");
/* 53 */     if (supportedMethods == null) {
/* 54 */       supportedMethods = Collections.emptySet();
/*    */     }
/* 56 */     this.method = method;
/* 57 */     this.httpMethods = Collections.unmodifiableSet(new LinkedHashSet<>(supportedMethods));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, String> getHeaders() {
/* 68 */     return getResponseHeaders().toSingleValueMap();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpHeaders getResponseHeaders() {
/* 77 */     if (CollectionUtils.isEmpty(this.httpMethods)) {
/* 78 */       return HttpHeaders.EMPTY;
/*    */     }
/* 80 */     HttpHeaders headers = new HttpHeaders();
/* 81 */     headers.setAllow(this.httpMethods);
/* 82 */     return headers;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getHttpMethod() {
/* 89 */     return this.method;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Set<HttpMethod> getSupportedMethods() {
/* 96 */     return this.httpMethods;
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/MethodNotAllowedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */