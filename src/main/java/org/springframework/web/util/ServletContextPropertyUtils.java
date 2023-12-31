/*     */ package org.springframework.web.util;
/*     */ 
/*     */ import javax.servlet.ServletContext;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.PropertyPlaceholderHelper;
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
/*     */ public abstract class ServletContextPropertyUtils
/*     */ {
/*  40 */   private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper("${", "}", ":", false);
/*     */ 
/*     */ 
/*     */   
/*  44 */   private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper("${", "}", ":", true);
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
/*     */   public static String resolvePlaceholders(String text, ServletContext servletContext) {
/*  61 */     return resolvePlaceholders(text, servletContext, false);
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
/*     */   public static String resolvePlaceholders(String text, ServletContext servletContext, boolean ignoreUnresolvablePlaceholders) {
/*  80 */     if (text.isEmpty()) {
/*  81 */       return text;
/*     */     }
/*  83 */     PropertyPlaceholderHelper helper = ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper;
/*  84 */     return helper.replacePlaceholders(text, new ServletContextPlaceholderResolver(text, servletContext));
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ServletContextPlaceholderResolver
/*     */     implements PropertyPlaceholderHelper.PlaceholderResolver
/*     */   {
/*     */     private final String text;
/*     */     private final ServletContext servletContext;
/*     */     
/*     */     public ServletContextPlaceholderResolver(String text, ServletContext servletContext) {
/*  95 */       this.text = text;
/*  96 */       this.servletContext = servletContext;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String resolvePlaceholder(String placeholderName) {
/*     */       try {
/* 103 */         String propVal = this.servletContext.getInitParameter(placeholderName);
/* 104 */         if (propVal == null) {
/*     */           
/* 106 */           propVal = System.getProperty(placeholderName);
/* 107 */           if (propVal == null)
/*     */           {
/* 109 */             propVal = System.getenv(placeholderName);
/*     */           }
/*     */         } 
/* 112 */         return propVal;
/*     */       }
/* 114 */       catch (Throwable ex) {
/* 115 */         System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" + this.text + "] as ServletContext init-parameter or system property: " + ex);
/*     */         
/* 117 */         return null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/util/ServletContextPropertyUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */