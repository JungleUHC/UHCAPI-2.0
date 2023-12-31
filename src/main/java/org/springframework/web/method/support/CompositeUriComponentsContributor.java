/*     */ package org.springframework.web.method.support;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.springframework.core.MethodParameter;
/*     */ import org.springframework.core.convert.ConversionService;
/*     */ import org.springframework.format.support.DefaultFormattingConversionService;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.web.util.UriComponentsBuilder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompositeUriComponentsContributor
/*     */   implements UriComponentsContributor
/*     */ {
/*     */   private final List<Object> contributors;
/*     */   private final ConversionService conversionService;
/*     */   
/*     */   public CompositeUriComponentsContributor(UriComponentsContributor... contributors) {
/*  58 */     this.contributors = Arrays.asList((Object[])contributors);
/*  59 */     this.conversionService = (ConversionService)new DefaultFormattingConversionService();
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
/*     */   public CompositeUriComponentsContributor(Collection<?> contributors) {
/*  72 */     this(contributors, null);
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
/*     */   public CompositeUriComponentsContributor(@Nullable Collection<?> contributors, @Nullable ConversionService cs) {
/*  90 */     this.contributors = (contributors != null) ? new ArrayList(contributors) : Collections.<Object>emptyList();
/*  91 */     this.conversionService = (cs != null) ? cs : (ConversionService)new DefaultFormattingConversionService();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasContributors() {
/* 101 */     return !this.contributors.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsParameter(MethodParameter parameter) {
/* 106 */     for (Object contributor : this.contributors) {
/* 107 */       if (contributor instanceof UriComponentsContributor) {
/* 108 */         if (((UriComponentsContributor)contributor).supportsParameter(parameter))
/* 109 */           return true; 
/*     */         continue;
/*     */       } 
/* 112 */       if (contributor instanceof HandlerMethodArgumentResolver && (
/* 113 */         (HandlerMethodArgumentResolver)contributor).supportsParameter(parameter)) {
/* 114 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 118 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
/* 125 */     for (Object contributor : this.contributors) {
/* 126 */       if (contributor instanceof UriComponentsContributor) {
/* 127 */         UriComponentsContributor ucc = (UriComponentsContributor)contributor;
/* 128 */         if (ucc.supportsParameter(parameter)) {
/* 129 */           ucc.contributeMethodArgument(parameter, value, builder, uriVariables, conversionService); break;
/*     */         } 
/*     */         continue;
/*     */       } 
/* 133 */       if (contributor instanceof HandlerMethodArgumentResolver && (
/* 134 */         (HandlerMethodArgumentResolver)contributor).supportsParameter(parameter)) {
/*     */         break;
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
/*     */   public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables) {
/* 147 */     contributeMethodArgument(parameter, value, builder, uriVariables, this.conversionService);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/support/CompositeUriComponentsContributor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */