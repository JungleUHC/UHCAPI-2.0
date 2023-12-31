/*     */ package org.springframework.web;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.servlet.ServletException;
/*     */ import org.springframework.http.HttpMethod;
/*     */ import org.springframework.lang.Nullable;
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
/*     */ public class HttpRequestMethodNotSupportedException
/*     */   extends ServletException
/*     */ {
/*     */   private final String method;
/*     */   @Nullable
/*     */   private final String[] supportedMethods;
/*     */   
/*     */   public HttpRequestMethodNotSupportedException(String method) {
/*  52 */     this(method, (String[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequestMethodNotSupportedException(String method, String msg) {
/*  61 */     this(method, null, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequestMethodNotSupportedException(String method, @Nullable Collection<String> supportedMethods) {
/*  70 */     this(method, (supportedMethods != null) ? StringUtils.toStringArray(supportedMethods) : null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequestMethodNotSupportedException(String method, @Nullable String[] supportedMethods) {
/*  79 */     this(method, supportedMethods, "Request method '" + method + "' not supported");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequestMethodNotSupportedException(String method, @Nullable String[] supportedMethods, String msg) {
/*  89 */     super(msg);
/*  90 */     this.method = method;
/*  91 */     this.supportedMethods = supportedMethods;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMethod() {
/*  99 */     return this.method;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String[] getSupportedMethods() {
/* 107 */     return this.supportedMethods;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Set<HttpMethod> getSupportedHttpMethods() {
/* 117 */     if (this.supportedMethods == null) {
/* 118 */       return null;
/*     */     }
/* 120 */     List<HttpMethod> supportedMethods = new ArrayList<>(this.supportedMethods.length);
/* 121 */     for (String value : this.supportedMethods) {
/* 122 */       HttpMethod resolved = HttpMethod.resolve(value);
/* 123 */       if (resolved != null) {
/* 124 */         supportedMethods.add(resolved);
/*     */       }
/*     */     } 
/* 127 */     return EnumSet.copyOf(supportedMethods);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/HttpRequestMethodNotSupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */