/*     */ package org.springframework.core.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Deque;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ConcurrentReferenceHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class AnnotationTypeMappings
/*     */ {
/*  51 */   private static final IntrospectionFailureLogger failureLogger = IntrospectionFailureLogger.DEBUG;
/*     */   
/*  53 */   private static final Map<AnnotationFilter, Cache> standardRepeatablesCache = (Map<AnnotationFilter, Cache>)new ConcurrentReferenceHashMap();
/*     */   
/*  55 */   private static final Map<AnnotationFilter, Cache> noRepeatablesCache = (Map<AnnotationFilter, Cache>)new ConcurrentReferenceHashMap();
/*     */ 
/*     */   
/*     */   private final RepeatableContainers repeatableContainers;
/*     */ 
/*     */   
/*     */   private final AnnotationFilter filter;
/*     */ 
/*     */   
/*     */   private final List<AnnotationTypeMapping> mappings;
/*     */ 
/*     */ 
/*     */   
/*     */   private AnnotationTypeMappings(RepeatableContainers repeatableContainers, AnnotationFilter filter, Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
/*  69 */     this.repeatableContainers = repeatableContainers;
/*  70 */     this.filter = filter;
/*  71 */     this.mappings = new ArrayList<>();
/*  72 */     addAllMappings(annotationType, visitedAnnotationTypes);
/*  73 */     this.mappings.forEach(AnnotationTypeMapping::afterAllMappingsSet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addAllMappings(Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
/*  80 */     Deque<AnnotationTypeMapping> queue = new ArrayDeque<>();
/*  81 */     addIfPossible(queue, null, annotationType, null, visitedAnnotationTypes);
/*  82 */     while (!queue.isEmpty()) {
/*  83 */       AnnotationTypeMapping mapping = queue.removeFirst();
/*  84 */       this.mappings.add(mapping);
/*  85 */       addMetaAnnotationsToQueue(queue, mapping);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addMetaAnnotationsToQueue(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source) {
/*  90 */     Annotation[] metaAnnotations = AnnotationsScanner.getDeclaredAnnotations(source.getAnnotationType(), false);
/*  91 */     for (Annotation metaAnnotation : metaAnnotations) {
/*  92 */       if (isMappable(source, metaAnnotation)) {
/*     */ 
/*     */         
/*  95 */         Annotation[] repeatedAnnotations = this.repeatableContainers.findRepeatedAnnotations(metaAnnotation);
/*  96 */         if (repeatedAnnotations != null) {
/*  97 */           for (Annotation repeatedAnnotation : repeatedAnnotations) {
/*  98 */             if (isMappable(source, repeatedAnnotation))
/*     */             {
/*     */               
/* 101 */               addIfPossible(queue, source, repeatedAnnotation);
/*     */             }
/*     */           } 
/*     */         } else {
/* 105 */           addIfPossible(queue, source, metaAnnotation);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private void addIfPossible(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source, Annotation ann) {
/* 111 */     addIfPossible(queue, source, ann.annotationType(), ann, new HashSet<>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addIfPossible(Deque<AnnotationTypeMapping> queue, @Nullable AnnotationTypeMapping source, Class<? extends Annotation> annotationType, @Nullable Annotation ann, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
/*     */     try {
/* 119 */       queue.addLast(new AnnotationTypeMapping(source, annotationType, ann, visitedAnnotationTypes));
/*     */     }
/* 121 */     catch (Exception ex) {
/* 122 */       AnnotationUtils.rethrowAnnotationConfigurationException(ex);
/* 123 */       if (failureLogger.isEnabled()) {
/* 124 */         failureLogger.log("Failed to introspect meta-annotation " + annotationType.getName(), (source != null) ? source
/* 125 */             .getAnnotationType() : null, ex);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isMappable(AnnotationTypeMapping source, @Nullable Annotation metaAnnotation) {
/* 131 */     return (metaAnnotation != null && !this.filter.matches(metaAnnotation) && 
/* 132 */       !AnnotationFilter.PLAIN.matches(source.getAnnotationType()) && 
/* 133 */       !isAlreadyMapped(source, metaAnnotation));
/*     */   }
/*     */   
/*     */   private boolean isAlreadyMapped(AnnotationTypeMapping source, Annotation metaAnnotation) {
/* 137 */     Class<? extends Annotation> annotationType = metaAnnotation.annotationType();
/* 138 */     AnnotationTypeMapping mapping = source;
/* 139 */     while (mapping != null) {
/* 140 */       if (mapping.getAnnotationType() == annotationType) {
/* 141 */         return true;
/*     */       }
/* 143 */       mapping = mapping.getSource();
/*     */     } 
/* 145 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int size() {
/* 153 */     return this.mappings.size();
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
/*     */   AnnotationTypeMapping get(int index) {
/* 166 */     return this.mappings.get(index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType) {
/* 176 */     return forAnnotationType(annotationType, new HashSet<>());
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
/*     */   static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
/* 190 */     return forAnnotationType(annotationType, RepeatableContainers.standardRepeatables(), AnnotationFilter.PLAIN, visitedAnnotationTypes);
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
/*     */   static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
/* 206 */     return forAnnotationType(annotationType, repeatableContainers, annotationFilter, new HashSet<>());
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
/*     */   private static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
/* 225 */     if (repeatableContainers == RepeatableContainers.standardRepeatables()) {
/* 226 */       return ((Cache)standardRepeatablesCache.computeIfAbsent(annotationFilter, key -> new Cache(repeatableContainers, key)))
/* 227 */         .get(annotationType, visitedAnnotationTypes);
/*     */     }
/* 229 */     if (repeatableContainers == RepeatableContainers.none()) {
/* 230 */       return ((Cache)noRepeatablesCache.computeIfAbsent(annotationFilter, key -> new Cache(repeatableContainers, key)))
/* 231 */         .get(annotationType, visitedAnnotationTypes);
/*     */     }
/* 233 */     return new AnnotationTypeMappings(repeatableContainers, annotationFilter, annotationType, visitedAnnotationTypes);
/*     */   }
/*     */   
/*     */   static void clearCache() {
/* 237 */     standardRepeatablesCache.clear();
/* 238 */     noRepeatablesCache.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Cache
/*     */   {
/*     */     private final RepeatableContainers repeatableContainers;
/*     */ 
/*     */ 
/*     */     
/*     */     private final AnnotationFilter filter;
/*     */ 
/*     */     
/*     */     private final Map<Class<? extends Annotation>, AnnotationTypeMappings> mappings;
/*     */ 
/*     */ 
/*     */     
/*     */     Cache(RepeatableContainers repeatableContainers, AnnotationFilter filter) {
/* 258 */       this.repeatableContainers = repeatableContainers;
/* 259 */       this.filter = filter;
/* 260 */       this.mappings = (Map<Class<? extends Annotation>, AnnotationTypeMappings>)new ConcurrentReferenceHashMap();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     AnnotationTypeMappings get(Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
/* 274 */       return this.mappings.computeIfAbsent(annotationType, key -> createMappings(key, visitedAnnotationTypes));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private AnnotationTypeMappings createMappings(Class<? extends Annotation> annotationType, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
/* 280 */       return new AnnotationTypeMappings(this.repeatableContainers, this.filter, annotationType, visitedAnnotationTypes);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/annotation/AnnotationTypeMappings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */