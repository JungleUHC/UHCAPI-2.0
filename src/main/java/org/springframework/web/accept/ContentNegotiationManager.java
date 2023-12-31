/*     */ package org.springframework.web.accept;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ import org.springframework.http.MediaType;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.CollectionUtils;
/*     */ import org.springframework.web.HttpMediaTypeNotAcceptableException;
/*     */ import org.springframework.web.context.request.NativeWebRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContentNegotiationManager
/*     */   implements ContentNegotiationStrategy, MediaTypeFileExtensionResolver
/*     */ {
/*  52 */   private final List<ContentNegotiationStrategy> strategies = new ArrayList<>();
/*     */   
/*  54 */   private final Set<MediaTypeFileExtensionResolver> resolvers = new LinkedHashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContentNegotiationManager(ContentNegotiationStrategy... strategies) {
/*  64 */     this(Arrays.asList(strategies));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContentNegotiationManager(Collection<ContentNegotiationStrategy> strategies) {
/*  74 */     Assert.notEmpty(strategies, "At least one ContentNegotiationStrategy is expected");
/*  75 */     this.strategies.addAll(strategies);
/*  76 */     for (ContentNegotiationStrategy strategy : this.strategies) {
/*  77 */       if (strategy instanceof MediaTypeFileExtensionResolver) {
/*  78 */         this.resolvers.add((MediaTypeFileExtensionResolver)strategy);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContentNegotiationManager() {
/*  87 */     this(new ContentNegotiationStrategy[] { new HeaderContentNegotiationStrategy() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<ContentNegotiationStrategy> getStrategies() {
/*  96 */     return this.strategies;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public <T extends ContentNegotiationStrategy> T getStrategy(Class<T> strategyType) {
/* 108 */     for (ContentNegotiationStrategy strategy : getStrategies()) {
/* 109 */       if (strategyType.isInstance(strategy)) {
/* 110 */         return (T)strategy;
/*     */       }
/*     */     } 
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addFileExtensionResolvers(MediaTypeFileExtensionResolver... resolvers) {
/* 122 */     Collections.addAll(this.resolvers, resolvers);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
/* 127 */     for (ContentNegotiationStrategy strategy : this.strategies) {
/* 128 */       List<MediaType> mediaTypes = strategy.resolveMediaTypes(request);
/* 129 */       if (mediaTypes.equals(MEDIA_TYPE_ALL_LIST)) {
/*     */         continue;
/*     */       }
/* 132 */       return mediaTypes;
/*     */     } 
/* 134 */     return MEDIA_TYPE_ALL_LIST;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> resolveFileExtensions(MediaType mediaType) {
/* 139 */     return doResolveExtensions(resolver -> resolver.resolveFileExtensions(mediaType));
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
/*     */   public List<String> getAllFileExtensions() {
/* 155 */     return doResolveExtensions(MediaTypeFileExtensionResolver::getAllFileExtensions);
/*     */   }
/*     */   
/*     */   private List<String> doResolveExtensions(Function<MediaTypeFileExtensionResolver, List<String>> extractor) {
/* 159 */     List<String> result = null;
/* 160 */     for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
/* 161 */       List<String> extensions = extractor.apply(resolver);
/* 162 */       if (CollectionUtils.isEmpty(extensions)) {
/*     */         continue;
/*     */       }
/* 165 */       result = (result != null) ? result : new ArrayList<>(4);
/* 166 */       for (String extension : extensions) {
/* 167 */         if (!result.contains(extension)) {
/* 168 */           result.add(extension);
/*     */         }
/*     */       } 
/*     */     } 
/* 172 */     return (result != null) ? result : Collections.<String>emptyList();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, MediaType> getMediaTypeMappings() {
/* 181 */     Map<String, MediaType> result = null;
/* 182 */     for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
/* 183 */       if (resolver instanceof MappingMediaTypeFileExtensionResolver) {
/* 184 */         Map<String, MediaType> map = ((MappingMediaTypeFileExtensionResolver)resolver).getMediaTypes();
/* 185 */         if (CollectionUtils.isEmpty(map)) {
/*     */           continue;
/*     */         }
/* 188 */         result = (result != null) ? result : new HashMap<>(4);
/* 189 */         result.putAll(map);
/*     */       } 
/*     */     } 
/* 192 */     return (result != null) ? result : Collections.<String, MediaType>emptyMap();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/accept/ContentNegotiationManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */