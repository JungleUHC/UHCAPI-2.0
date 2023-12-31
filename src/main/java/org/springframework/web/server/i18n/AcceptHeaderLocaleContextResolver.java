/*     */ package org.springframework.web.server.i18n;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import org.springframework.context.i18n.LocaleContext;
/*     */ import org.springframework.context.i18n.SimpleLocaleContext;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ import org.springframework.web.server.ServerWebExchange;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AcceptHeaderLocaleContextResolver
/*     */   implements LocaleContextResolver
/*     */ {
/*  46 */   private final List<Locale> supportedLocales = new ArrayList<>(4);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Locale defaultLocale;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSupportedLocales(List<Locale> locales) {
/*  58 */     this.supportedLocales.clear();
/*  59 */     this.supportedLocales.addAll(locales);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Locale> getSupportedLocales() {
/*  66 */     return this.supportedLocales;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultLocale(@Nullable Locale defaultLocale) {
/*  75 */     this.defaultLocale = defaultLocale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Locale getDefaultLocale() {
/*  84 */     return this.defaultLocale;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
/*  90 */     List<Locale> requestLocales = null;
/*     */     try {
/*  92 */       requestLocales = exchange.getRequest().getHeaders().getAcceptLanguageAsLocales();
/*     */     }
/*  94 */     catch (IllegalArgumentException illegalArgumentException) {}
/*     */ 
/*     */     
/*  97 */     return (LocaleContext)new SimpleLocaleContext(resolveSupportedLocale(requestLocales));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private Locale resolveSupportedLocale(@Nullable List<Locale> requestLocales) {
/* 102 */     if (CollectionUtils.isEmpty(requestLocales)) {
/* 103 */       return getDefaultLocale();
/*     */     }
/* 105 */     List<Locale> supportedLocales = getSupportedLocales();
/* 106 */     if (supportedLocales.isEmpty()) {
/* 107 */       return requestLocales.get(0);
/*     */     }
/*     */     
/* 110 */     Locale languageMatch = null;
/* 111 */     for (Locale locale : requestLocales) {
/* 112 */       if (supportedLocales.contains(locale)) {
/* 113 */         if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage()))
/*     */         {
/* 115 */           return locale; } 
/*     */         continue;
/*     */       } 
/* 118 */       if (languageMatch == null)
/*     */       {
/* 120 */         for (Locale candidate : supportedLocales) {
/* 121 */           if (!StringUtils.hasLength(candidate.getCountry()) && candidate
/* 122 */             .getLanguage().equals(locale.getLanguage())) {
/* 123 */             languageMatch = candidate;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 129 */     if (languageMatch != null) {
/* 130 */       return languageMatch;
/*     */     }
/*     */     
/* 133 */     Locale defaultLocale = getDefaultLocale();
/* 134 */     return (defaultLocale != null) ? defaultLocale : requestLocales.get(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLocaleContext(ServerWebExchange exchange, @Nullable LocaleContext locale) {
/* 139 */     throw new UnsupportedOperationException("Cannot change HTTP accept header - use a different locale context resolution strategy");
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/server/i18n/AcceptHeaderLocaleContextResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */