/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AnnotatedMethodCollector
/*     */   extends CollectorBase
/*     */ {
/*     */   private final ClassIntrospector.MixInResolver _mixInResolver;
/*     */   private final boolean _collectAnnotations;
/*     */   
/*     */   AnnotatedMethodCollector(AnnotationIntrospector intr, ClassIntrospector.MixInResolver mixins, boolean collectAnnotations) {
/*  27 */     super(intr);
/*  28 */     this._mixInResolver = (intr == null) ? null : mixins;
/*  29 */     this._collectAnnotations = collectAnnotations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AnnotatedMethodMap collectMethods(AnnotationIntrospector intr, TypeResolutionContext tc, ClassIntrospector.MixInResolver mixins, TypeFactory types, JavaType type, List<JavaType> superTypes, Class<?> primaryMixIn, boolean collectAnnotations) {
/*  39 */     return (new AnnotatedMethodCollector(intr, mixins, collectAnnotations))
/*  40 */       .collect(types, tc, type, superTypes, primaryMixIn);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   AnnotatedMethodMap collect(TypeFactory typeFactory, TypeResolutionContext tc, JavaType mainType, List<JavaType> superTypes, Class<?> primaryMixIn) {
/*  46 */     Map<MemberKey, MethodBuilder> methods = new LinkedHashMap<>();
/*     */ 
/*     */     
/*  49 */     _addMemberMethods(tc, mainType.getRawClass(), methods, primaryMixIn);
/*     */ 
/*     */     
/*  52 */     for (JavaType type : superTypes) {
/*  53 */       Class<?> mixin = (this._mixInResolver == null) ? null : this._mixInResolver.findMixInClassFor(type.getRawClass());
/*  54 */       _addMemberMethods(new TypeResolutionContext.Basic(typeFactory, type
/*  55 */             .getBindings()), type
/*  56 */           .getRawClass(), methods, mixin);
/*     */     } 
/*     */     
/*  59 */     boolean checkJavaLangObject = false;
/*  60 */     if (this._mixInResolver != null) {
/*  61 */       Class<?> mixin = this._mixInResolver.findMixInClassFor(Object.class);
/*  62 */       if (mixin != null) {
/*  63 */         _addMethodMixIns(tc, mainType.getRawClass(), methods, mixin);
/*  64 */         checkJavaLangObject = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     if (checkJavaLangObject && this._intr != null && !methods.isEmpty())
/*     */     {
/*  74 */       for (Map.Entry<MemberKey, MethodBuilder> entry : methods.entrySet()) {
/*  75 */         MemberKey k = entry.getKey();
/*  76 */         if (!"hashCode".equals(k.getName()) || 0 != k.argCount()) {
/*     */           continue;
/*     */         }
/*     */         
/*     */         try {
/*  81 */           Method m = Object.class.getDeclaredMethod(k.getName(), new Class[0]);
/*  82 */           if (m != null) {
/*  83 */             MethodBuilder b = entry.getValue();
/*  84 */             b.annotations = collectDefaultAnnotations(b.annotations, m
/*  85 */                 .getDeclaredAnnotations());
/*  86 */             b.method = m;
/*     */           } 
/*  88 */         } catch (Exception exception) {}
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  93 */     if (methods.isEmpty()) {
/*  94 */       return new AnnotatedMethodMap();
/*     */     }
/*  96 */     Map<MemberKey, AnnotatedMethod> actual = new LinkedHashMap<>(methods.size());
/*  97 */     for (Map.Entry<MemberKey, MethodBuilder> entry : methods.entrySet()) {
/*  98 */       AnnotatedMethod am = ((MethodBuilder)entry.getValue()).build();
/*  99 */       if (am != null) {
/* 100 */         actual.put(entry.getKey(), am);
/*     */       }
/*     */     } 
/* 103 */     return new AnnotatedMethodMap(actual);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void _addMemberMethods(TypeResolutionContext tc, Class<?> cls, Map<MemberKey, MethodBuilder> methods, Class<?> mixInCls) {
/* 110 */     if (mixInCls != null) {
/* 111 */       _addMethodMixIns(tc, cls, methods, mixInCls);
/*     */     }
/* 113 */     if (cls == null) {
/*     */       return;
/*     */     }
/*     */     
/* 117 */     for (Method m : ClassUtil.getClassMethods(cls)) {
/* 118 */       if (_isIncludableMemberMethod(m)) {
/*     */ 
/*     */         
/* 121 */         MemberKey key = new MemberKey(m);
/* 122 */         MethodBuilder b = methods.get(key);
/* 123 */         if (b == null) {
/*     */           
/* 125 */           AnnotationCollector c = (this._intr == null) ? AnnotationCollector.emptyCollector() : collectAnnotations(m.getDeclaredAnnotations());
/* 126 */           methods.put(key, new MethodBuilder(tc, m, c));
/*     */         } else {
/* 128 */           if (this._collectAnnotations) {
/* 129 */             b.annotations = collectDefaultAnnotations(b.annotations, m.getDeclaredAnnotations());
/*     */           }
/* 131 */           Method old = b.method;
/* 132 */           if (old == null) {
/* 133 */             b.method = m;
/*     */           }
/* 135 */           else if (Modifier.isAbstract(old.getModifiers()) && 
/* 136 */             !Modifier.isAbstract(m.getModifiers())) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 143 */             b.method = m;
/*     */ 
/*     */             
/* 146 */             b.typeContext = tc;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _addMethodMixIns(TypeResolutionContext tc, Class<?> targetClass, Map<MemberKey, MethodBuilder> methods, Class<?> mixInCls) {
/* 155 */     if (this._intr == null) {
/*     */       return;
/*     */     }
/* 158 */     for (Class<?> mixin : (Iterable<Class<?>>)ClassUtil.findRawSuperTypes(mixInCls, targetClass, true)) {
/* 159 */       for (Method m : mixin.getDeclaredMethods()) {
/* 160 */         if (_isIncludableMemberMethod(m)) {
/*     */ 
/*     */           
/* 163 */           MemberKey key = new MemberKey(m);
/* 164 */           MethodBuilder b = methods.get(key);
/* 165 */           Annotation[] anns = m.getDeclaredAnnotations();
/* 166 */           if (b == null) {
/*     */ 
/*     */             
/* 169 */             methods.put(key, new MethodBuilder(tc, null, collectAnnotations(anns)));
/*     */           } else {
/* 171 */             b.annotations = collectDefaultAnnotations(b.annotations, anns);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean _isIncludableMemberMethod(Method m) {
/* 179 */     if (Modifier.isStatic(m.getModifiers()) || m
/*     */ 
/*     */       
/* 182 */       .isSynthetic() || m.isBridge()) {
/* 183 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 187 */     return ((m.getParameterTypes()).length <= 2);
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class MethodBuilder
/*     */   {
/*     */     public TypeResolutionContext typeContext;
/*     */     
/*     */     public Method method;
/*     */     public AnnotationCollector annotations;
/*     */     
/*     */     public MethodBuilder(TypeResolutionContext tc, Method m, AnnotationCollector ann) {
/* 199 */       this.typeContext = tc;
/* 200 */       this.method = m;
/* 201 */       this.annotations = ann;
/*     */     }
/*     */     
/*     */     public AnnotatedMethod build() {
/* 205 */       if (this.method == null) {
/* 206 */         return null;
/*     */       }
/*     */ 
/*     */       
/* 210 */       return new AnnotatedMethod(this.typeContext, this.method, this.annotations.asAnnotationMap(), null);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedMethodCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */