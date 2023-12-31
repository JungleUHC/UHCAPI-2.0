/*    */ package org.springframework.web.filter;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.springframework.util.Assert;
/*    */ import org.springframework.web.cors.CorsConfiguration;
/*    */ import org.springframework.web.cors.CorsConfigurationSource;
/*    */ import org.springframework.web.cors.CorsProcessor;
/*    */ import org.springframework.web.cors.CorsUtils;
/*    */ import org.springframework.web.cors.DefaultCorsProcessor;
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
/*    */ public class CorsFilter
/*    */   extends OncePerRequestFilter
/*    */ {
/*    */   private final CorsConfigurationSource configSource;
/* 57 */   private CorsProcessor processor = (CorsProcessor)new DefaultCorsProcessor();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CorsFilter(CorsConfigurationSource configSource) {
/* 66 */     Assert.notNull(configSource, "CorsConfigurationSource must not be null");
/* 67 */     this.configSource = configSource;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCorsProcessor(CorsProcessor processor) {
/* 77 */     Assert.notNull(processor, "CorsProcessor must not be null");
/* 78 */     this.processor = processor;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/* 86 */     CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(request);
/* 87 */     boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
/* 88 */     if (!isValid || CorsUtils.isPreFlightRequest(request)) {
/*    */       return;
/*    */     }
/* 91 */     filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/filter/CorsFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */