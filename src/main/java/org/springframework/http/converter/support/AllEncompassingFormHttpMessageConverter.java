/*     */ package org.springframework.http.converter.support;
/*     */ 
/*     */ import org.springframework.core.SpringProperties;
/*     */ import org.springframework.http.converter.FormHttpMessageConverter;
/*     */ import org.springframework.http.converter.HttpMessageConverter;
/*     */ import org.springframework.http.converter.json.GsonHttpMessageConverter;
/*     */ import org.springframework.http.converter.json.JsonbHttpMessageConverter;
/*     */ import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter;
/*     */ import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
/*     */ import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
/*     */ import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
/*     */ import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
/*     */ import org.springframework.http.converter.xml.SourceHttpMessageConverter;
/*     */ import org.springframework.util.ClassUtils;
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
/*     */ public class AllEncompassingFormHttpMessageConverter
/*     */   extends FormHttpMessageConverter
/*     */ {
/*  47 */   private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
/*     */   
/*     */   private static final boolean jaxb2Present;
/*     */   
/*     */   private static final boolean jackson2Present;
/*     */   
/*     */   private static final boolean jackson2XmlPresent;
/*     */   
/*     */   private static final boolean jackson2SmilePresent;
/*     */   
/*     */   private static final boolean gsonPresent;
/*     */   
/*     */   private static final boolean jsonbPresent;
/*     */   
/*     */   private static final boolean kotlinSerializationJsonPresent;
/*     */   
/*     */   static {
/*  64 */     ClassLoader classLoader = AllEncompassingFormHttpMessageConverter.class.getClassLoader();
/*  65 */     jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", classLoader);
/*     */     
/*  67 */     jackson2Present = (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader));
/*  68 */     jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader);
/*  69 */     jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
/*  70 */     gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
/*  71 */     jsonbPresent = ClassUtils.isPresent("javax.json.bind.Jsonb", classLoader);
/*  72 */     kotlinSerializationJsonPresent = ClassUtils.isPresent("kotlinx.serialization.json.Json", classLoader);
/*     */   }
/*     */ 
/*     */   
/*     */   public AllEncompassingFormHttpMessageConverter() {
/*  77 */     if (!shouldIgnoreXml) {
/*     */       try {
/*  79 */         addPartConverter((HttpMessageConverter)new SourceHttpMessageConverter());
/*     */       }
/*  81 */       catch (Error error) {}
/*     */ 
/*     */ 
/*     */       
/*  85 */       if (jaxb2Present && !jackson2XmlPresent) {
/*  86 */         addPartConverter((HttpMessageConverter)new Jaxb2RootElementHttpMessageConverter());
/*     */       }
/*     */     } 
/*     */     
/*  90 */     if (jackson2Present) {
/*  91 */       addPartConverter((HttpMessageConverter)new MappingJackson2HttpMessageConverter());
/*     */     }
/*  93 */     else if (gsonPresent) {
/*  94 */       addPartConverter((HttpMessageConverter)new GsonHttpMessageConverter());
/*     */     }
/*  96 */     else if (jsonbPresent) {
/*  97 */       addPartConverter((HttpMessageConverter)new JsonbHttpMessageConverter());
/*     */     }
/*  99 */     else if (kotlinSerializationJsonPresent) {
/* 100 */       addPartConverter((HttpMessageConverter)new KotlinSerializationJsonHttpMessageConverter());
/*     */     } 
/*     */     
/* 103 */     if (jackson2XmlPresent && !shouldIgnoreXml) {
/* 104 */       addPartConverter((HttpMessageConverter)new MappingJackson2XmlHttpMessageConverter());
/*     */     }
/*     */     
/* 107 */     if (jackson2SmilePresent)
/* 108 */       addPartConverter((HttpMessageConverter)new MappingJackson2SmileHttpMessageConverter()); 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/http/converter/support/AllEncompassingFormHttpMessageConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */