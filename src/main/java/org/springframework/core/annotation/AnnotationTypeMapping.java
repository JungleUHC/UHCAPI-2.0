/*     */ package org.springframework.core.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ObjectUtils;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ import org.springframework.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class AnnotationTypeMapping
/*     */ {
/*  49 */   private static final MirrorSets.MirrorSet[] EMPTY_MIRROR_SETS = new MirrorSets.MirrorSet[0];
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final AnnotationTypeMapping source;
/*     */   
/*     */   private final AnnotationTypeMapping root;
/*     */   
/*     */   private final int distance;
/*     */   
/*     */   private final Class<? extends Annotation> annotationType;
/*     */   
/*     */   private final List<Class<? extends Annotation>> metaTypes;
/*     */   
/*     */   @Nullable
/*     */   private final Annotation annotation;
/*     */   
/*     */   private final AttributeMethods attributes;
/*     */   
/*     */   private final MirrorSets mirrorSets;
/*     */   
/*     */   private final int[] aliasMappings;
/*     */   
/*     */   private final int[] conventionMappings;
/*     */   
/*     */   private final int[] annotationValueMappings;
/*     */   
/*     */   private final AnnotationTypeMapping[] annotationValueSource;
/*     */   
/*     */   private final Map<Method, List<Method>> aliasedBy;
/*     */   
/*     */   private final boolean synthesizable;
/*     */   
/*  82 */   private final Set<Method> claimedAliases = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AnnotationTypeMapping(@Nullable AnnotationTypeMapping source, Class<? extends Annotation> annotationType, @Nullable Annotation annotation, Set<Class<? extends Annotation>> visitedAnnotationTypes) {
/*  88 */     this.source = source;
/*  89 */     this.root = (source != null) ? source.getRoot() : this;
/*  90 */     this.distance = (source == null) ? 0 : (source.getDistance() + 1);
/*  91 */     this.annotationType = annotationType;
/*  92 */     this.metaTypes = merge((source != null) ? source
/*  93 */         .getMetaTypes() : null, annotationType);
/*     */     
/*  95 */     this.annotation = annotation;
/*  96 */     this.attributes = AttributeMethods.forAnnotationType(annotationType);
/*  97 */     this.mirrorSets = new MirrorSets();
/*  98 */     this.aliasMappings = filledIntArray(this.attributes.size());
/*  99 */     this.conventionMappings = filledIntArray(this.attributes.size());
/* 100 */     this.annotationValueMappings = filledIntArray(this.attributes.size());
/* 101 */     this.annotationValueSource = new AnnotationTypeMapping[this.attributes.size()];
/* 102 */     this.aliasedBy = resolveAliasedForTargets();
/* 103 */     processAliases();
/* 104 */     addConventionMappings();
/* 105 */     addConventionAnnotationValues();
/* 106 */     this.synthesizable = computeSynthesizableFlag(visitedAnnotationTypes);
/*     */   }
/*     */ 
/*     */   
/*     */   private static <T> List<T> merge(@Nullable List<T> existing, T element) {
/* 111 */     if (existing == null) {
/* 112 */       return Collections.singletonList(element);
/*     */     }
/* 114 */     List<T> merged = new ArrayList<>(existing.size() + 1);
/* 115 */     merged.addAll(existing);
/* 116 */     merged.add(element);
/* 117 */     return Collections.unmodifiableList(merged);
/*     */   }
/*     */   
/*     */   private Map<Method, List<Method>> resolveAliasedForTargets() {
/* 121 */     Map<Method, List<Method>> aliasedBy = new HashMap<>();
/* 122 */     for (int i = 0; i < this.attributes.size(); i++) {
/* 123 */       Method attribute = this.attributes.get(i);
/* 124 */       AliasFor aliasFor = AnnotationsScanner.<AliasFor>getDeclaredAnnotation(attribute, AliasFor.class);
/* 125 */       if (aliasFor != null) {
/* 126 */         Method target = resolveAliasTarget(attribute, aliasFor);
/* 127 */         ((List<Method>)aliasedBy.computeIfAbsent(target, key -> new ArrayList())).add(attribute);
/*     */       } 
/*     */     } 
/* 130 */     return Collections.unmodifiableMap(aliasedBy);
/*     */   }
/*     */   
/*     */   private Method resolveAliasTarget(Method attribute, AliasFor aliasFor) {
/* 134 */     return resolveAliasTarget(attribute, aliasFor, true);
/*     */   }
/*     */   
/*     */   private Method resolveAliasTarget(Method attribute, AliasFor aliasFor, boolean checkAliasPair) {
/* 138 */     if (StringUtils.hasText(aliasFor.value()) && StringUtils.hasText(aliasFor.attribute()))
/* 139 */       throw new AnnotationConfigurationException(String.format("In @AliasFor declared on %s, attribute 'attribute' and its alias 'value' are present with values of '%s' and '%s', but only one is permitted.", new Object[] {
/*     */ 
/*     */               
/* 142 */               AttributeMethods.describe(attribute), aliasFor.attribute(), aliasFor
/* 143 */               .value()
/*     */             })); 
/* 145 */     Class<? extends Annotation> targetAnnotation = aliasFor.annotation();
/* 146 */     if (targetAnnotation == Annotation.class) {
/* 147 */       targetAnnotation = this.annotationType;
/*     */     }
/* 149 */     String targetAttributeName = aliasFor.attribute();
/* 150 */     if (!StringUtils.hasLength(targetAttributeName)) {
/* 151 */       targetAttributeName = aliasFor.value();
/*     */     }
/* 153 */     if (!StringUtils.hasLength(targetAttributeName)) {
/* 154 */       targetAttributeName = attribute.getName();
/*     */     }
/* 156 */     Method target = AttributeMethods.forAnnotationType(targetAnnotation).get(targetAttributeName);
/* 157 */     if (target == null) {
/* 158 */       if (targetAnnotation == this.annotationType)
/* 159 */         throw new AnnotationConfigurationException(String.format("@AliasFor declaration on %s declares an alias for '%s' which is not present.", new Object[] {
/*     */                 
/* 161 */                 AttributeMethods.describe(attribute), targetAttributeName
/*     */               })); 
/* 163 */       throw new AnnotationConfigurationException(String.format("%s is declared as an @AliasFor nonexistent %s.", new Object[] {
/*     */               
/* 165 */               StringUtils.capitalize(AttributeMethods.describe(attribute)), 
/* 166 */               AttributeMethods.describe(targetAnnotation, targetAttributeName) }));
/*     */     } 
/* 168 */     if (target.equals(attribute))
/* 169 */       throw new AnnotationConfigurationException(String.format("@AliasFor declaration on %s points to itself. Specify 'annotation' to point to a same-named attribute on a meta-annotation.", new Object[] {
/*     */ 
/*     */               
/* 172 */               AttributeMethods.describe(attribute)
/*     */             })); 
/* 174 */     if (!isCompatibleReturnType(attribute.getReturnType(), target.getReturnType()))
/* 175 */       throw new AnnotationConfigurationException(String.format("Misconfigured aliases: %s and %s must declare the same return type.", new Object[] {
/*     */               
/* 177 */               AttributeMethods.describe(attribute), 
/* 178 */               AttributeMethods.describe(target)
/*     */             })); 
/* 180 */     if (isAliasPair(target) && checkAliasPair) {
/* 181 */       AliasFor targetAliasFor = target.<AliasFor>getAnnotation(AliasFor.class);
/* 182 */       if (targetAliasFor != null) {
/* 183 */         Method mirror = resolveAliasTarget(target, targetAliasFor, false);
/* 184 */         if (!mirror.equals(attribute))
/* 185 */           throw new AnnotationConfigurationException(String.format("%s must be declared as an @AliasFor %s, not %s.", new Object[] {
/*     */                   
/* 187 */                   StringUtils.capitalize(AttributeMethods.describe(target)), 
/* 188 */                   AttributeMethods.describe(attribute), AttributeMethods.describe(mirror)
/*     */                 })); 
/*     */       } 
/*     */     } 
/* 192 */     return target;
/*     */   }
/*     */   
/*     */   private boolean isAliasPair(Method target) {
/* 196 */     return (this.annotationType == target.getDeclaringClass());
/*     */   }
/*     */   
/*     */   private boolean isCompatibleReturnType(Class<?> attributeType, Class<?> targetType) {
/* 200 */     return (attributeType == targetType || attributeType == targetType.getComponentType());
/*     */   }
/*     */   
/*     */   private void processAliases() {
/* 204 */     List<Method> aliases = new ArrayList<>();
/* 205 */     for (int i = 0; i < this.attributes.size(); i++) {
/* 206 */       aliases.clear();
/* 207 */       aliases.add(this.attributes.get(i));
/* 208 */       collectAliases(aliases);
/* 209 */       if (aliases.size() > 1) {
/* 210 */         processAliases(i, aliases);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void collectAliases(List<Method> aliases) {
/* 216 */     AnnotationTypeMapping mapping = this;
/* 217 */     while (mapping != null) {
/* 218 */       int size = aliases.size();
/* 219 */       for (int j = 0; j < size; j++) {
/* 220 */         List<Method> additional = mapping.aliasedBy.get(aliases.get(j));
/* 221 */         if (additional != null) {
/* 222 */           aliases.addAll(additional);
/*     */         }
/*     */       } 
/* 225 */       mapping = mapping.source;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void processAliases(int attributeIndex, List<Method> aliases) {
/* 230 */     int rootAttributeIndex = getFirstRootAttributeIndex(aliases);
/* 231 */     AnnotationTypeMapping mapping = this;
/* 232 */     while (mapping != null) {
/* 233 */       if (rootAttributeIndex != -1 && mapping != this.root) {
/* 234 */         for (int i = 0; i < mapping.attributes.size(); i++) {
/* 235 */           if (aliases.contains(mapping.attributes.get(i))) {
/* 236 */             mapping.aliasMappings[i] = rootAttributeIndex;
/*     */           }
/*     */         } 
/*     */       }
/* 240 */       mapping.mirrorSets.updateFrom(aliases);
/* 241 */       mapping.claimedAliases.addAll(aliases);
/* 242 */       if (mapping.annotation != null) {
/* 243 */         int[] resolvedMirrors = mapping.mirrorSets.resolve(null, mapping.annotation, ReflectionUtils::invokeMethod);
/*     */         
/* 245 */         for (int i = 0; i < mapping.attributes.size(); i++) {
/* 246 */           if (aliases.contains(mapping.attributes.get(i))) {
/* 247 */             this.annotationValueMappings[attributeIndex] = resolvedMirrors[i];
/* 248 */             this.annotationValueSource[attributeIndex] = mapping;
/*     */           } 
/*     */         } 
/*     */       } 
/* 252 */       mapping = mapping.source;
/*     */     } 
/*     */   }
/*     */   
/*     */   private int getFirstRootAttributeIndex(Collection<Method> aliases) {
/* 257 */     AttributeMethods rootAttributes = this.root.getAttributes();
/* 258 */     for (int i = 0; i < rootAttributes.size(); i++) {
/* 259 */       if (aliases.contains(rootAttributes.get(i))) {
/* 260 */         return i;
/*     */       }
/*     */     } 
/* 263 */     return -1;
/*     */   }
/*     */   
/*     */   private void addConventionMappings() {
/* 267 */     if (this.distance == 0) {
/*     */       return;
/*     */     }
/* 270 */     AttributeMethods rootAttributes = this.root.getAttributes();
/* 271 */     int[] mappings = this.conventionMappings;
/* 272 */     for (int i = 0; i < mappings.length; i++) {
/* 273 */       String name = this.attributes.get(i).getName();
/* 274 */       MirrorSets.MirrorSet mirrors = getMirrorSets().getAssigned(i);
/* 275 */       int mapped = rootAttributes.indexOf(name);
/* 276 */       if (!"value".equals(name) && mapped != -1) {
/* 277 */         mappings[i] = mapped;
/* 278 */         if (mirrors != null) {
/* 279 */           for (int j = 0; j < mirrors.size(); j++) {
/* 280 */             mappings[mirrors.getAttributeIndex(j)] = mapped;
/*     */           }
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addConventionAnnotationValues() {
/* 288 */     for (int i = 0; i < this.attributes.size(); i++) {
/* 289 */       Method attribute = this.attributes.get(i);
/* 290 */       boolean isValueAttribute = "value".equals(attribute.getName());
/* 291 */       AnnotationTypeMapping mapping = this;
/* 292 */       while (mapping != null && mapping.distance > 0) {
/* 293 */         int mapped = mapping.getAttributes().indexOf(attribute.getName());
/* 294 */         if (mapped != -1 && isBetterConventionAnnotationValue(i, isValueAttribute, mapping)) {
/* 295 */           this.annotationValueMappings[i] = mapped;
/* 296 */           this.annotationValueSource[i] = mapping;
/*     */         } 
/* 298 */         mapping = mapping.source;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isBetterConventionAnnotationValue(int index, boolean isValueAttribute, AnnotationTypeMapping mapping) {
/* 306 */     if (this.annotationValueMappings[index] == -1) {
/* 307 */       return true;
/*     */     }
/* 309 */     int existingDistance = (this.annotationValueSource[index]).distance;
/* 310 */     return (!isValueAttribute && existingDistance > mapping.distance);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean computeSynthesizableFlag(Set<Class<? extends Annotation>> visitedAnnotationTypes) {
/* 316 */     visitedAnnotationTypes.add(this.annotationType);
/*     */ 
/*     */     
/* 319 */     for (int index : this.aliasMappings) {
/* 320 */       if (index != -1) {
/* 321 */         return true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 326 */     if (!this.aliasedBy.isEmpty()) {
/* 327 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 331 */     for (int index : this.conventionMappings) {
/* 332 */       if (index != -1) {
/* 333 */         return true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 338 */     if (getAttributes().hasNestedAnnotation()) {
/* 339 */       AttributeMethods attributeMethods = getAttributes();
/* 340 */       for (int i = 0; i < attributeMethods.size(); i++) {
/* 341 */         Method method = attributeMethods.get(i);
/* 342 */         Class<?> type = method.getReturnType();
/* 343 */         if (type.isAnnotation() || (type.isArray() && type.getComponentType().isAnnotation())) {
/*     */           
/* 345 */           Class<? extends Annotation> annotationType = type.isAnnotation() ? (Class)type : (Class)type.getComponentType();
/*     */ 
/*     */ 
/*     */           
/* 349 */           if (visitedAnnotationTypes.add(annotationType)) {
/*     */             
/* 351 */             AnnotationTypeMapping mapping = AnnotationTypeMappings.forAnnotationType(annotationType, visitedAnnotationTypes).get(0);
/* 352 */             if (mapping.isSynthesizable()) {
/* 353 */               return true;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 360 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void afterAllMappingsSet() {
/* 368 */     validateAllAliasesClaimed();
/* 369 */     for (int i = 0; i < this.mirrorSets.size(); i++) {
/* 370 */       validateMirrorSet(this.mirrorSets.get(i));
/*     */     }
/* 372 */     this.claimedAliases.clear();
/*     */   }
/*     */   
/*     */   private void validateAllAliasesClaimed() {
/* 376 */     for (int i = 0; i < this.attributes.size(); i++) {
/* 377 */       Method attribute = this.attributes.get(i);
/* 378 */       AliasFor aliasFor = AnnotationsScanner.<AliasFor>getDeclaredAnnotation(attribute, AliasFor.class);
/* 379 */       if (aliasFor != null && !this.claimedAliases.contains(attribute)) {
/* 380 */         Method target = resolveAliasTarget(attribute, aliasFor);
/* 381 */         throw new AnnotationConfigurationException(String.format("@AliasFor declaration on %s declares an alias for %s which is not meta-present.", new Object[] {
/*     */                 
/* 383 */                 AttributeMethods.describe(attribute), AttributeMethods.describe(target) }));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void validateMirrorSet(MirrorSets.MirrorSet mirrorSet) {
/* 389 */     Method firstAttribute = mirrorSet.get(0);
/* 390 */     Object firstDefaultValue = firstAttribute.getDefaultValue();
/* 391 */     for (int i = 1; i <= mirrorSet.size() - 1; i++) {
/* 392 */       Method mirrorAttribute = mirrorSet.get(i);
/* 393 */       Object mirrorDefaultValue = mirrorAttribute.getDefaultValue();
/* 394 */       if (firstDefaultValue == null || mirrorDefaultValue == null)
/* 395 */         throw new AnnotationConfigurationException(String.format("Misconfigured aliases: %s and %s must declare default values.", new Object[] {
/*     */                 
/* 397 */                 AttributeMethods.describe(firstAttribute), AttributeMethods.describe(mirrorAttribute)
/*     */               })); 
/* 399 */       if (!ObjectUtils.nullSafeEquals(firstDefaultValue, mirrorDefaultValue)) {
/* 400 */         throw new AnnotationConfigurationException(String.format("Misconfigured aliases: %s and %s must declare the same default value.", new Object[] {
/*     */                 
/* 402 */                 AttributeMethods.describe(firstAttribute), AttributeMethods.describe(mirrorAttribute)
/*     */               }));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AnnotationTypeMapping getRoot() {
/* 412 */     return this.root;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   AnnotationTypeMapping getSource() {
/* 421 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getDistance() {
/* 429 */     return this.distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Class<? extends Annotation> getAnnotationType() {
/* 437 */     return this.annotationType;
/*     */   }
/*     */   
/*     */   List<Class<? extends Annotation>> getMetaTypes() {
/* 441 */     return this.metaTypes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   Annotation getAnnotation() {
/* 451 */     return this.annotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AttributeMethods getAttributes() {
/* 459 */     return this.attributes;
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
/*     */   int getAliasMapping(int attributeIndex) {
/* 471 */     return this.aliasMappings[attributeIndex];
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
/*     */   int getConventionMapping(int attributeIndex) {
/* 483 */     return this.conventionMappings[attributeIndex];
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
/*     */   @Nullable
/*     */   Object getMappedAnnotationValue(int attributeIndex, boolean metaAnnotationsOnly) {
/* 500 */     int mappedIndex = this.annotationValueMappings[attributeIndex];
/* 501 */     if (mappedIndex == -1) {
/* 502 */       return null;
/*     */     }
/* 504 */     AnnotationTypeMapping source = this.annotationValueSource[attributeIndex];
/* 505 */     if (source == this && metaAnnotationsOnly) {
/* 506 */       return null;
/*     */     }
/* 508 */     return ReflectionUtils.invokeMethod(source.attributes.get(mappedIndex), source.annotation);
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
/*     */   boolean isEquivalentToDefaultValue(int attributeIndex, Object value, ValueExtractor valueExtractor) {
/* 522 */     Method attribute = this.attributes.get(attributeIndex);
/* 523 */     return isEquivalentToDefaultValue(attribute, value, valueExtractor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   MirrorSets getMirrorSets() {
/* 531 */     return this.mirrorSets;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isSynthesizable() {
/* 542 */     return this.synthesizable;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int[] filledIntArray(int size) {
/* 547 */     int[] array = new int[size];
/* 548 */     Arrays.fill(array, -1);
/* 549 */     return array;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isEquivalentToDefaultValue(Method attribute, Object value, ValueExtractor valueExtractor) {
/* 555 */     return areEquivalent(attribute.getDefaultValue(), value, valueExtractor);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean areEquivalent(@Nullable Object value, @Nullable Object extractedValue, ValueExtractor valueExtractor) {
/* 561 */     if (ObjectUtils.nullSafeEquals(value, extractedValue)) {
/* 562 */       return true;
/*     */     }
/* 564 */     if (value instanceof Class && extractedValue instanceof String) {
/* 565 */       return areEquivalent((Class)value, (String)extractedValue);
/*     */     }
/* 567 */     if (value instanceof Class[] && extractedValue instanceof String[]) {
/* 568 */       return areEquivalent((Class[])value, (String[])extractedValue);
/*     */     }
/* 570 */     if (value instanceof Annotation) {
/* 571 */       return areEquivalent((Annotation)value, extractedValue, valueExtractor);
/*     */     }
/* 573 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean areEquivalent(Class<?>[] value, String[] extractedValue) {
/* 577 */     if (value.length != extractedValue.length) {
/* 578 */       return false;
/*     */     }
/* 580 */     for (int i = 0; i < value.length; i++) {
/* 581 */       if (!areEquivalent(value[i], extractedValue[i])) {
/* 582 */         return false;
/*     */       }
/*     */     } 
/* 585 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean areEquivalent(Class<?> value, String extractedValue) {
/* 589 */     return value.getName().equals(extractedValue);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean areEquivalent(Annotation annotation, @Nullable Object extractedValue, ValueExtractor valueExtractor) {
/* 595 */     AttributeMethods attributes = AttributeMethods.forAnnotationType(annotation.annotationType());
/* 596 */     for (int i = 0; i < attributes.size(); i++) {
/* 597 */       Object value2; Method attribute = attributes.get(i);
/* 598 */       Object value1 = ReflectionUtils.invokeMethod(attribute, annotation);
/*     */       
/* 600 */       if (extractedValue instanceof TypeMappedAnnotation) {
/* 601 */         value2 = ((TypeMappedAnnotation)extractedValue).getValue(attribute.getName()).orElse(null);
/*     */       } else {
/*     */         
/* 604 */         value2 = valueExtractor.extract(attribute, extractedValue);
/*     */       } 
/* 606 */       if (!areEquivalent(value1, value2, valueExtractor)) {
/* 607 */         return false;
/*     */       }
/*     */     } 
/* 610 */     return true;
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
/*     */   class MirrorSets
/*     */   {
/* 625 */     private final MirrorSet[] assigned = new MirrorSet[AnnotationTypeMapping.this.attributes.size()];
/* 626 */     private MirrorSet[] mirrorSets = AnnotationTypeMapping.EMPTY_MIRROR_SETS;
/*     */ 
/*     */     
/*     */     void updateFrom(Collection<Method> aliases) {
/* 630 */       MirrorSet mirrorSet = null;
/* 631 */       int size = 0;
/* 632 */       int last = -1;
/* 633 */       for (int i = 0; i < AnnotationTypeMapping.this.attributes.size(); i++) {
/* 634 */         Method attribute = AnnotationTypeMapping.this.attributes.get(i);
/* 635 */         if (aliases.contains(attribute)) {
/* 636 */           size++;
/* 637 */           if (size > 1) {
/* 638 */             if (mirrorSet == null) {
/* 639 */               mirrorSet = new MirrorSet();
/* 640 */               this.assigned[last] = mirrorSet;
/*     */             } 
/* 642 */             this.assigned[i] = mirrorSet;
/*     */           } 
/* 644 */           last = i;
/*     */         } 
/*     */       } 
/* 647 */       if (mirrorSet != null) {
/* 648 */         mirrorSet.update();
/* 649 */         Set<MirrorSet> unique = new LinkedHashSet<>(Arrays.asList(this.assigned));
/* 650 */         unique.remove(null);
/* 651 */         this.mirrorSets = unique.<MirrorSet>toArray(AnnotationTypeMapping.EMPTY_MIRROR_SETS);
/*     */       } 
/*     */     }
/*     */     
/*     */     int size() {
/* 656 */       return this.mirrorSets.length;
/*     */     }
/*     */     
/*     */     MirrorSet get(int index) {
/* 660 */       return this.mirrorSets[index];
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     MirrorSet getAssigned(int attributeIndex) {
/* 665 */       return this.assigned[attributeIndex];
/*     */     }
/*     */     
/*     */     int[] resolve(@Nullable Object source, @Nullable Object annotation, ValueExtractor valueExtractor) {
/* 669 */       int[] result = new int[AnnotationTypeMapping.this.attributes.size()]; int i;
/* 670 */       for (i = 0; i < result.length; i++) {
/* 671 */         result[i] = i;
/*     */       }
/* 673 */       for (i = 0; i < size(); i++) {
/* 674 */         MirrorSet mirrorSet = get(i);
/* 675 */         int resolved = mirrorSet.resolve(source, annotation, valueExtractor);
/* 676 */         for (int j = 0; j < mirrorSet.size; j++) {
/* 677 */           result[mirrorSet.indexes[j]] = resolved;
/*     */         }
/*     */       } 
/* 680 */       return result;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     class MirrorSet
/*     */     {
/*     */       private int size;
/*     */ 
/*     */       
/* 691 */       private final int[] indexes = new int[AnnotationTypeMapping.this.attributes.size()];
/*     */       
/*     */       void update() {
/* 694 */         this.size = 0;
/* 695 */         Arrays.fill(this.indexes, -1);
/* 696 */         for (int i = 0; i < AnnotationTypeMapping.MirrorSets.this.assigned.length; i++) {
/* 697 */           if (AnnotationTypeMapping.MirrorSets.this.assigned[i] == this) {
/* 698 */             this.indexes[this.size] = i;
/* 699 */             this.size++;
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/*     */       <A> int resolve(@Nullable Object source, @Nullable A annotation, ValueExtractor valueExtractor) {
/* 705 */         int result = -1;
/* 706 */         Object lastValue = null;
/* 707 */         for (int i = 0; i < this.size; i++) {
/* 708 */           Method attribute = AnnotationTypeMapping.this.attributes.get(this.indexes[i]);
/* 709 */           Object value = valueExtractor.extract(attribute, annotation);
/*     */           
/* 711 */           boolean isDefaultValue = (value == null || AnnotationTypeMapping.isEquivalentToDefaultValue(attribute, value, valueExtractor));
/* 712 */           if (isDefaultValue || ObjectUtils.nullSafeEquals(lastValue, value)) {
/* 713 */             if (result == -1) {
/* 714 */               result = this.indexes[i];
/*     */             }
/*     */           } else {
/*     */             
/* 718 */             if (lastValue != null && !ObjectUtils.nullSafeEquals(lastValue, value)) {
/* 719 */               String on = (source != null) ? (" declared on " + source) : "";
/* 720 */               throw new AnnotationConfigurationException(String.format("Different @AliasFor mirror values for annotation [%s]%s; attribute '%s' and its alias '%s' are declared with values of [%s] and [%s].", new Object[] { this.this$1.this$0
/*     */ 
/*     */                       
/* 723 */                       .getAnnotationType().getName(), on, 
/* 724 */                       AnnotationTypeMapping.access$000(this.this$1.this$0).get(result).getName(), attribute
/* 725 */                       .getName(), 
/* 726 */                       ObjectUtils.nullSafeToString(lastValue), 
/* 727 */                       ObjectUtils.nullSafeToString(value) }));
/*     */             } 
/* 729 */             result = this.indexes[i];
/* 730 */             lastValue = value;
/*     */           } 
/* 732 */         }  return result;
/*     */       }
/*     */       
/*     */       int size() {
/* 736 */         return this.size;
/*     */       }
/*     */       
/*     */       Method get(int index) {
/* 740 */         int attributeIndex = this.indexes[index];
/* 741 */         return AnnotationTypeMapping.this.attributes.get(attributeIndex);
/*     */       }
/*     */       
/*     */       int getAttributeIndex(int index) {
/* 745 */         return this.indexes[index]; } } } class MirrorSet { int getAttributeIndex(int index) { return this.indexes[index]; }
/*     */ 
/*     */     
/*     */     private int size;
/*     */     private final int[] indexes;
/*     */     
/*     */     MirrorSet() {
/*     */       this.indexes = new int[AnnotationTypeMapping.this.attributes.size()];
/*     */     }
/*     */     
/*     */     void update() {
/*     */       this.size = 0;
/*     */       Arrays.fill(this.indexes, -1);
/*     */       for (int i = 0; i < AnnotationTypeMapping.MirrorSets.this.assigned.length; i++) {
/*     */         if (AnnotationTypeMapping.MirrorSets.this.assigned[i] == this) {
/*     */           this.indexes[this.size] = i;
/*     */           this.size++;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     <A> int resolve(@Nullable Object source, @Nullable A annotation, ValueExtractor valueExtractor) {
/*     */       int result = -1;
/*     */       Object lastValue = null;
/*     */       for (int i = 0; i < this.size; i++) {
/*     */         Method attribute = AnnotationTypeMapping.this.attributes.get(this.indexes[i]);
/*     */         Object value = valueExtractor.extract(attribute, annotation);
/*     */         boolean isDefaultValue = (value == null || AnnotationTypeMapping.isEquivalentToDefaultValue(attribute, value, valueExtractor));
/*     */         if (isDefaultValue || ObjectUtils.nullSafeEquals(lastValue, value)) {
/*     */           if (result == -1)
/*     */             result = this.indexes[i]; 
/*     */         } else {
/*     */           if (lastValue != null && !ObjectUtils.nullSafeEquals(lastValue, value)) {
/*     */             String on = (source != null) ? (" declared on " + source) : "";
/*     */             throw new AnnotationConfigurationException(String.format("Different @AliasFor mirror values for annotation [%s]%s; attribute '%s' and its alias '%s' are declared with values of [%s] and [%s].", new Object[] { this.this$1.this$0.getAnnotationType().getName(), on, AnnotationTypeMapping.access$000(this.this$1.this$0).get(result).getName(), attribute.getName(), ObjectUtils.nullSafeToString(lastValue), ObjectUtils.nullSafeToString(value) }));
/*     */           } 
/*     */           result = this.indexes[i];
/*     */           lastValue = value;
/*     */         } 
/*     */       } 
/*     */       return result;
/*     */     }
/*     */     
/*     */     int size() {
/*     */       return this.size;
/*     */     }
/*     */     
/*     */     Method get(int index) {
/*     */       int attributeIndex = this.indexes[index];
/*     */       return AnnotationTypeMapping.this.attributes.get(attributeIndex);
/*     */     } }
/*     */ 
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/annotation/AnnotationTypeMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */