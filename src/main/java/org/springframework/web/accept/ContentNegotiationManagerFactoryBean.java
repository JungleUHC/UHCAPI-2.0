/*     */ package org.springframework.web.accept;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.springframework.beans.factory.FactoryBean;
/*     */ import org.springframework.beans.factory.InitializingBean;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.web.context.ServletContextAware;
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
/*     */ public class ContentNegotiationManagerFactoryBean
/*     */   implements FactoryBean<ContentNegotiationManager>, ServletContextAware, InitializingBean
/*     */ {
/*     */   @Nullable
/*     */   private List<ContentNegotiationStrategy> strategies;
/*     */   private boolean favorParameter = false;
/* 109 */   private String parameterName = "format";
/*     */   
/*     */   private boolean favorPathExtension = false;
/*     */   
/* 113 */   private Map<String, MediaType> mediaTypes = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean ignoreUnknownPathExtensions = true;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Boolean useRegisteredExtensionsOnly;
/*     */ 
/*     */   
/*     */   private boolean ignoreAcceptHeader = false;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ContentNegotiationStrategy defaultNegotiationStrategy;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ContentNegotiationManager contentNegotiationManager;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private ServletContext servletContext;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStrategies(@Nullable List<ContentNegotiationStrategy> strategies) {
/* 141 */     this.strategies = (strategies != null) ? new ArrayList<>(strategies) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFavorParameter(boolean favorParameter) {
/* 152 */     this.favorParameter = favorParameter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParameterName(String parameterName) {
/* 160 */     Assert.notNull(parameterName, "parameterName is required");
/* 161 */     this.parameterName = parameterName;
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
/*     */   @Deprecated
/*     */   public void setFavorPathExtension(boolean favorPathExtension) {
/* 176 */     this.favorPathExtension = favorPathExtension;
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
/*     */   public void setMediaTypes(Properties mediaTypes) {
/* 207 */     if (!CollectionUtils.isEmpty(mediaTypes)) {
/* 208 */       mediaTypes.forEach((key, value) -> addMediaType((String)key, MediaType.valueOf((String)value)));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMediaType(String key, MediaType mediaType) {
/* 217 */     this.mediaTypes.put(key.toLowerCase(Locale.ENGLISH), mediaType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMediaTypes(@Nullable Map<String, MediaType> mediaTypes) {
/* 224 */     if (mediaTypes != null) {
/* 225 */       mediaTypes.forEach(this::addMediaType);
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
/*     */   @Deprecated
/*     */   public void setIgnoreUnknownPathExtensions(boolean ignore) {
/* 239 */     this.ignoreUnknownPathExtensions = ignore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setUseJaf(boolean useJaf) {
/* 250 */     setUseRegisteredExtensionsOnly(!useJaf);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUseRegisteredExtensionsOnly(boolean useRegisteredExtensionsOnly) {
/* 261 */     this.useRegisteredExtensionsOnly = Boolean.valueOf(useRegisteredExtensionsOnly);
/*     */   }
/*     */   
/*     */   private boolean useRegisteredExtensionsOnly() {
/* 265 */     return (this.useRegisteredExtensionsOnly != null && this.useRegisteredExtensionsOnly.booleanValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIgnoreAcceptHeader(boolean ignoreAcceptHeader) {
/* 273 */     this.ignoreAcceptHeader = ignoreAcceptHeader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultContentType(MediaType contentType) {
/* 282 */     this.defaultNegotiationStrategy = new FixedContentNegotiationStrategy(contentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultContentTypes(List<MediaType> contentTypes) {
/* 292 */     this.defaultNegotiationStrategy = new FixedContentNegotiationStrategy(contentTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultContentTypeStrategy(ContentNegotiationStrategy strategy) {
/* 303 */     this.defaultNegotiationStrategy = strategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServletContext(ServletContext servletContext) {
/* 311 */     this.servletContext = servletContext;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterPropertiesSet() {
/* 317 */     build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContentNegotiationManager build() {
/* 326 */     List<ContentNegotiationStrategy> strategies = new ArrayList<>();
/*     */     
/* 328 */     if (this.strategies != null) {
/* 329 */       strategies.addAll(this.strategies);
/*     */     } else {
/*     */       
/* 332 */       if (this.favorPathExtension) {
/*     */         PathExtensionContentNegotiationStrategy strategy;
/* 334 */         if (this.servletContext != null && !useRegisteredExtensionsOnly()) {
/* 335 */           strategy = new ServletPathExtensionContentNegotiationStrategy(this.servletContext, this.mediaTypes);
/*     */         } else {
/*     */           
/* 338 */           strategy = new PathExtensionContentNegotiationStrategy(this.mediaTypes);
/*     */         } 
/* 340 */         strategy.setIgnoreUnknownExtensions(this.ignoreUnknownPathExtensions);
/* 341 */         if (this.useRegisteredExtensionsOnly != null) {
/* 342 */           strategy.setUseRegisteredExtensionsOnly(this.useRegisteredExtensionsOnly.booleanValue());
/*     */         }
/* 344 */         strategies.add(strategy);
/*     */       } 
/* 346 */       if (this.favorParameter) {
/* 347 */         ParameterContentNegotiationStrategy strategy = new ParameterContentNegotiationStrategy(this.mediaTypes);
/* 348 */         strategy.setParameterName(this.parameterName);
/* 349 */         if (this.useRegisteredExtensionsOnly != null) {
/* 350 */           strategy.setUseRegisteredExtensionsOnly(this.useRegisteredExtensionsOnly.booleanValue());
/*     */         } else {
/*     */           
/* 353 */           strategy.setUseRegisteredExtensionsOnly(true);
/*     */         } 
/* 355 */         strategies.add(strategy);
/*     */       } 
/* 357 */       if (!this.ignoreAcceptHeader) {
/* 358 */         strategies.add(new HeaderContentNegotiationStrategy());
/*     */       }
/* 360 */       if (this.defaultNegotiationStrategy != null) {
/* 361 */         strategies.add(this.defaultNegotiationStrategy);
/*     */       }
/*     */     } 
/*     */     
/* 365 */     this.contentNegotiationManager = new ContentNegotiationManager(strategies);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 370 */     if (!CollectionUtils.isEmpty(this.mediaTypes) && !this.favorPathExtension && !this.favorParameter) {
/* 371 */       this.contentNegotiationManager.addFileExtensionResolvers(new MediaTypeFileExtensionResolver[] { new MappingMediaTypeFileExtensionResolver(this.mediaTypes) });
/*     */     }
/*     */ 
/*     */     
/* 375 */     return this.contentNegotiationManager;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ContentNegotiationManager getObject() {
/* 382 */     return this.contentNegotiationManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getObjectType() {
/* 387 */     return ContentNegotiationManager.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSingleton() {
/* 392 */     return true;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/accept/ContentNegotiationManagerFactoryBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */