/*     */ package org.springframework.web.filter;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.servlet.Filter;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.FilterConfig;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompositeFilter
/*     */   implements Filter
/*     */ {
/*  45 */   private List<? extends Filter> filters = new ArrayList<>();
/*     */ 
/*     */   
/*     */   public void setFilters(List<? extends Filter> filters) {
/*  49 */     this.filters = new ArrayList<>(filters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(FilterConfig config) throws ServletException {
/*  59 */     for (Filter filter : this.filters) {
/*  60 */       filter.init(config);
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
/*     */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
/*  74 */     (new VirtualFilterChain(chain, this.filters)).doFilter(request, response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() {
/*  83 */     for (int i = this.filters.size(); i-- > 0; ) {
/*  84 */       Filter filter = this.filters.get(i);
/*  85 */       filter.destroy();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static class VirtualFilterChain
/*     */     implements FilterChain
/*     */   {
/*     */     private final FilterChain originalChain;
/*     */     
/*     */     private final List<? extends Filter> additionalFilters;
/*  96 */     private int currentPosition = 0;
/*     */     
/*     */     public VirtualFilterChain(FilterChain chain, List<? extends Filter> additionalFilters) {
/*  99 */       this.originalChain = chain;
/* 100 */       this.additionalFilters = additionalFilters;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
/* 107 */       if (this.currentPosition == this.additionalFilters.size()) {
/* 108 */         this.originalChain.doFilter(request, response);
/*     */       } else {
/*     */         
/* 111 */         this.currentPosition++;
/* 112 */         Filter nextFilter = this.additionalFilters.get(this.currentPosition - 1);
/* 113 */         nextFilter.doFilter(request, response, this);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/CompositeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */