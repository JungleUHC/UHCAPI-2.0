/*     */ package org.springframework.http.client.support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.springframework.core.annotation.AnnotationAwareOrderComparator;
/*     */ import org.springframework.http.client.ClientHttpRequestFactory;
/*     */ import org.springframework.http.client.ClientHttpRequestInterceptor;
/*     */ import org.springframework.http.client.InterceptingClientHttpRequestFactory;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class InterceptingHttpAccessor
/*     */   extends HttpAccessor
/*     */ {
/*  47 */   private final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private volatile ClientHttpRequestFactory interceptingRequestFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
/*  61 */     Assert.noNullElements(interceptors, "'interceptors' must not contain null elements");
/*     */     
/*  63 */     if (this.interceptors != interceptors) {
/*  64 */       this.interceptors.clear();
/*  65 */       this.interceptors.addAll(interceptors);
/*  66 */       AnnotationAwareOrderComparator.sort(this.interceptors);
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
/*     */   public List<ClientHttpRequestInterceptor> getInterceptors() {
/*  78 */     return this.interceptors;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
/*  86 */     super.setRequestFactory(requestFactory);
/*  87 */     this.interceptingRequestFactory = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientHttpRequestFactory getRequestFactory() {
/*  97 */     List<ClientHttpRequestInterceptor> interceptors = getInterceptors();
/*  98 */     if (!CollectionUtils.isEmpty(interceptors)) {
/*  99 */       InterceptingClientHttpRequestFactory interceptingClientHttpRequestFactory; ClientHttpRequestFactory factory = this.interceptingRequestFactory;
/* 100 */       if (factory == null) {
/* 101 */         interceptingClientHttpRequestFactory = new InterceptingClientHttpRequestFactory(super.getRequestFactory(), interceptors);
/* 102 */         this.interceptingRequestFactory = (ClientHttpRequestFactory)interceptingClientHttpRequestFactory;
/*     */       } 
/* 104 */       return (ClientHttpRequestFactory)interceptingClientHttpRequestFactory;
/*     */     } 
/*     */     
/* 107 */     return super.getRequestFactory();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/client/support/InterceptingHttpAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */