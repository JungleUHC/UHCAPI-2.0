/*     */ package org.springframework.core.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import org.springframework.lang.Nullable;
/*     */ import org.springframework.util.Assert;
/*     */ import org.springframework.util.ConcurrentReferenceHashMap;
/*     */ import org.springframework.util.ReflectionUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class AttributeMethods
/*     */ {
/*  40 */   static final AttributeMethods NONE = new AttributeMethods(null, new Method[0]);
/*     */ 
/*     */   
/*  43 */   private static final Map<Class<? extends Annotation>, AttributeMethods> cache = (Map<Class<? extends Annotation>, AttributeMethods>)new ConcurrentReferenceHashMap();
/*     */   
/*     */   static {
/*  46 */     methodComparator = ((m1, m2) -> 
/*  47 */       (m1 != null && m2 != null) ? m1.getName().compareTo(m2.getName()) : ((m1 != null) ? -1 : 1));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Comparator<Method> methodComparator;
/*     */   
/*     */   @Nullable
/*     */   private final Class<? extends Annotation> annotationType;
/*     */   
/*     */   private final Method[] attributeMethods;
/*     */   
/*     */   private final boolean[] canThrowTypeNotPresentException;
/*     */   
/*     */   private final boolean hasDefaultValueMethod;
/*     */   
/*     */   private final boolean hasNestedAnnotation;
/*     */ 
/*     */   
/*     */   private AttributeMethods(@Nullable Class<? extends Annotation> annotationType, Method[] attributeMethods) {
/*  67 */     this.annotationType = annotationType;
/*  68 */     this.attributeMethods = attributeMethods;
/*  69 */     this.canThrowTypeNotPresentException = new boolean[attributeMethods.length];
/*  70 */     boolean foundDefaultValueMethod = false;
/*  71 */     boolean foundNestedAnnotation = false;
/*  72 */     for (int i = 0; i < attributeMethods.length; i++) {
/*  73 */       Method method = this.attributeMethods[i];
/*  74 */       Class<?> type = method.getReturnType();
/*  75 */       if (!foundDefaultValueMethod && method.getDefaultValue() != null) {
/*  76 */         foundDefaultValueMethod = true;
/*     */       }
/*  78 */       if (!foundNestedAnnotation && (type.isAnnotation() || (type.isArray() && type.getComponentType().isAnnotation()))) {
/*  79 */         foundNestedAnnotation = true;
/*     */       }
/*  81 */       ReflectionUtils.makeAccessible(method);
/*  82 */       this.canThrowTypeNotPresentException[i] = (type == Class.class || type == Class[].class || type.isEnum());
/*     */     } 
/*  84 */     this.hasDefaultValueMethod = foundDefaultValueMethod;
/*  85 */     this.hasNestedAnnotation = foundNestedAnnotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasOnlyValueAttribute() {
/*  95 */     return (this.attributeMethods.length == 1 && "value"
/*  96 */       .equals(this.attributeMethods[0].getName()));
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
/*     */   boolean isValid(Annotation annotation) {
/* 108 */     assertAnnotation(annotation);
/* 109 */     for (int i = 0; i < size(); i++) {
/* 110 */       if (canThrowTypeNotPresentException(i)) {
/*     */         try {
/* 112 */           get(i).invoke(annotation, new Object[0]);
/*     */         }
/* 114 */         catch (Throwable ex) {
/* 115 */           return false;
/*     */         } 
/*     */       }
/*     */     } 
/* 119 */     return true;
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
/*     */   void validate(Annotation annotation) {
/* 133 */     assertAnnotation(annotation);
/* 134 */     for (int i = 0; i < size(); i++) {
/* 135 */       if (canThrowTypeNotPresentException(i)) {
/*     */         try {
/* 137 */           get(i).invoke(annotation, new Object[0]);
/*     */         }
/* 139 */         catch (Throwable ex) {
/* 140 */           throw new IllegalStateException("Could not obtain annotation attribute value for " + 
/* 141 */               get(i).getName() + " declared on " + annotation.annotationType(), ex);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void assertAnnotation(Annotation annotation) {
/* 148 */     Assert.notNull(annotation, "Annotation must not be null");
/* 149 */     if (this.annotationType != null) {
/* 150 */       Assert.isInstanceOf(this.annotationType, annotation);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   Method get(String name) {
/* 162 */     int index = indexOf(name);
/* 163 */     return (index != -1) ? this.attributeMethods[index] : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Method get(int index) {
/* 174 */     return this.attributeMethods[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean canThrowTypeNotPresentException(int index) {
/* 185 */     return this.canThrowTypeNotPresentException[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int indexOf(String name) {
/* 195 */     for (int i = 0; i < this.attributeMethods.length; i++) {
/* 196 */       if (this.attributeMethods[i].getName().equals(name)) {
/* 197 */         return i;
/*     */       }
/*     */     } 
/* 200 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int indexOf(Method attribute) {
/* 210 */     for (int i = 0; i < this.attributeMethods.length; i++) {
/* 211 */       if (this.attributeMethods[i].equals(attribute)) {
/* 212 */         return i;
/*     */       }
/*     */     } 
/* 215 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int size() {
/* 223 */     return this.attributeMethods.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasDefaultValueMethod() {
/* 231 */     return this.hasDefaultValueMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasNestedAnnotation() {
/* 240 */     return this.hasNestedAnnotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static AttributeMethods forAnnotationType(@Nullable Class<? extends Annotation> annotationType) {
/* 250 */     if (annotationType == null) {
/* 251 */       return NONE;
/*     */     }
/* 253 */     return cache.computeIfAbsent(annotationType, AttributeMethods::compute);
/*     */   }
/*     */   
/*     */   private static AttributeMethods compute(Class<? extends Annotation> annotationType) {
/* 257 */     Method[] methods = annotationType.getDeclaredMethods();
/* 258 */     int size = methods.length;
/* 259 */     for (int i = 0; i < methods.length; i++) {
/* 260 */       if (!isAttributeMethod(methods[i])) {
/* 261 */         methods[i] = null;
/* 262 */         size--;
/*     */       } 
/*     */     } 
/* 265 */     if (size == 0) {
/* 266 */       return NONE;
/*     */     }
/* 268 */     Arrays.sort(methods, methodComparator);
/* 269 */     Method[] attributeMethods = Arrays.<Method>copyOf(methods, size);
/* 270 */     return new AttributeMethods(annotationType, attributeMethods);
/*     */   }
/*     */   
/*     */   private static boolean isAttributeMethod(Method method) {
/* 274 */     return (method.getParameterCount() == 0 && method.getReturnType() != void.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String describe(@Nullable Method attribute) {
/* 284 */     if (attribute == null) {
/* 285 */       return "(none)";
/*     */     }
/* 287 */     return describe(attribute.getDeclaringClass(), attribute.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String describe(@Nullable Class<?> annotationType, @Nullable String attributeName) {
/* 298 */     if (attributeName == null) {
/* 299 */       return "(none)";
/*     */     }
/* 301 */     String in = (annotationType != null) ? (" in annotation [" + annotationType.getName() + "]") : "";
/* 302 */     return "attribute '" + attributeName + "'" + in;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/annotation/AttributeMethods.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */