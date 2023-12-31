/*     */ package org.springframework.web.method;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.function.Predicate;
/*     */ import org.springframework.core.annotation.AnnotationUtils;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.ClassUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HandlerTypePredicate
/*     */   implements Predicate<Class<?>>
/*     */ {
/*     */   private final Set<String> basePackages;
/*     */   private final List<Class<?>> assignableTypes;
/*     */   private final List<Class<? extends Annotation>> annotations;
/*     */   
/*     */   private HandlerTypePredicate(Set<String> basePackages, List<Class<?>> assignableTypes, List<Class<? extends Annotation>> annotations) {
/*  66 */     this.basePackages = Collections.unmodifiableSet(basePackages);
/*  67 */     this.assignableTypes = Collections.unmodifiableList(assignableTypes);
/*  68 */     this.annotations = Collections.unmodifiableList(annotations);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean test(@Nullable Class<?> controllerType) {
/*  74 */     if (!hasSelectors()) {
/*  75 */       return true;
/*     */     }
/*  77 */     if (controllerType != null) {
/*  78 */       for (String basePackage : this.basePackages) {
/*  79 */         if (controllerType.getName().startsWith(basePackage)) {
/*  80 */           return true;
/*     */         }
/*     */       } 
/*  83 */       for (Class<?> clazz : this.assignableTypes) {
/*  84 */         if (ClassUtils.isAssignable(clazz, controllerType)) {
/*  85 */           return true;
/*     */         }
/*     */       } 
/*  88 */       for (Class<? extends Annotation> annotationClass : this.annotations) {
/*  89 */         if (AnnotationUtils.findAnnotation(controllerType, annotationClass) != null) {
/*  90 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*  94 */     return false;
/*     */   }
/*     */   
/*     */   private boolean hasSelectors() {
/*  98 */     return (!this.basePackages.isEmpty() || !this.assignableTypes.isEmpty() || !this.annotations.isEmpty());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HandlerTypePredicate forAnyHandlerType() {
/* 108 */     return new HandlerTypePredicate(
/* 109 */         Collections.emptySet(), Collections.emptyList(), Collections.emptyList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HandlerTypePredicate forBasePackage(String... packages) {
/* 117 */     return (new Builder()).basePackage(packages).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HandlerTypePredicate forBasePackageClass(Class<?>... packageClasses) {
/* 126 */     return (new Builder()).basePackageClass(packageClasses).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HandlerTypePredicate forAssignableType(Class<?>... types) {
/* 134 */     return (new Builder()).assignableType(types).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public static HandlerTypePredicate forAnnotation(Class<? extends Annotation>... annotations) {
/* 143 */     return (new Builder()).annotation(annotations).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder builder() {
/* 150 */     return new Builder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/* 159 */     private final Set<String> basePackages = new LinkedHashSet<>();
/*     */     
/* 161 */     private final List<Class<?>> assignableTypes = new ArrayList<>();
/*     */     
/* 163 */     private final List<Class<? extends Annotation>> annotations = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder basePackage(String... packages) {
/* 170 */       Arrays.<String>stream(packages).filter(StringUtils::hasText).forEach(this::addBasePackage);
/* 171 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder basePackageClass(Class<?>... packageClasses) {
/* 180 */       Arrays.<Class<?>>stream(packageClasses).forEach(clazz -> addBasePackage(ClassUtils.getPackageName(clazz)));
/* 181 */       return this;
/*     */     }
/*     */     
/*     */     private void addBasePackage(String basePackage) {
/* 185 */       this.basePackages.add(basePackage.endsWith(".") ? basePackage : (basePackage + "."));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder assignableType(Class<?>... types) {
/* 193 */       this.assignableTypes.addAll(Arrays.asList(types));
/* 194 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final Builder annotation(Class<? extends Annotation>... annotations) {
/* 203 */       this.annotations.addAll(Arrays.asList(annotations));
/* 204 */       return this;
/*     */     }
/*     */     
/*     */     public HandlerTypePredicate build() {
/* 208 */       return new HandlerTypePredicate(this.basePackages, this.assignableTypes, this.annotations);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/web/method/HandlerTypePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */