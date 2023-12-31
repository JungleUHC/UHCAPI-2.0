/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class AnnotatedCreatorCollector
/*     */   extends CollectorBase
/*     */ {
/*     */   private final TypeResolutionContext _typeContext;
/*     */   private final boolean _collectAnnotations;
/*     */   private AnnotatedConstructor _defaultConstructor;
/*     */   
/*     */   AnnotatedCreatorCollector(AnnotationIntrospector intr, TypeResolutionContext tc, boolean collectAnnotations) {
/*  43 */     super(intr);
/*  44 */     this._typeContext = tc;
/*  45 */     this._collectAnnotations = collectAnnotations;
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
/*     */   public static AnnotatedClass.Creators collectCreators(AnnotationIntrospector intr, TypeFactory typeFactory, TypeResolutionContext tc, JavaType type, Class<?> primaryMixIn, boolean collectAnnotations) {
/*  57 */     int i = collectAnnotations | ((primaryMixIn != null) ? 1 : 0);
/*     */ 
/*     */     
/*  60 */     return (new AnnotatedCreatorCollector(intr, tc, i))
/*  61 */       .collect(typeFactory, type, primaryMixIn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AnnotatedClass.Creators collect(TypeFactory typeFactory, JavaType type, Class<?> primaryMixIn) {
/*  70 */     List<AnnotatedConstructor> constructors = _findPotentialConstructors(type, primaryMixIn);
/*  71 */     List<AnnotatedMethod> factories = _findPotentialFactories(typeFactory, type, primaryMixIn);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  77 */     if (this._collectAnnotations) {
/*  78 */       if (this._defaultConstructor != null && 
/*  79 */         this._intr.hasIgnoreMarker(this._defaultConstructor)) {
/*  80 */         this._defaultConstructor = null;
/*     */       }
/*     */       
/*     */       int i;
/*  84 */       for (i = constructors.size(); --i >= 0;) {
/*  85 */         if (this._intr.hasIgnoreMarker(constructors.get(i))) {
/*  86 */           constructors.remove(i);
/*     */         }
/*     */       } 
/*  89 */       for (i = factories.size(); --i >= 0;) {
/*  90 */         if (this._intr.hasIgnoreMarker(factories.get(i))) {
/*  91 */           factories.remove(i);
/*     */         }
/*     */       } 
/*     */     } 
/*  95 */     return new AnnotatedClass.Creators(this._defaultConstructor, constructors, factories);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<AnnotatedConstructor> _findPotentialConstructors(JavaType type, Class<?> primaryMixIn) {
/*     */     List<AnnotatedConstructor> result;
/*     */     int ctorCount;
/* 106 */     ClassUtil.Ctor defaultCtor = null;
/* 107 */     List<ClassUtil.Ctor> ctors = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     if (!type.isEnumType()) {
/* 115 */       ClassUtil.Ctor[] declaredCtors = ClassUtil.getConstructors(type.getRawClass());
/* 116 */       for (ClassUtil.Ctor ctor : declaredCtors) {
/* 117 */         if (isIncludableConstructor(ctor.getConstructor()))
/*     */         {
/*     */           
/* 120 */           if (ctor.getParamCount() == 0) {
/* 121 */             defaultCtor = ctor;
/*     */           } else {
/* 123 */             if (ctors == null) {
/* 124 */               ctors = new ArrayList<>();
/*     */             }
/* 126 */             ctors.add(ctor);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 132 */     if (ctors == null) {
/* 133 */       result = Collections.emptyList();
/*     */       
/* 135 */       if (defaultCtor == null) {
/* 136 */         return result;
/*     */       }
/* 138 */       ctorCount = 0;
/*     */     } else {
/* 140 */       ctorCount = ctors.size();
/* 141 */       result = new ArrayList<>(ctorCount);
/* 142 */       for (int j = 0; j < ctorCount; j++) {
/* 143 */         result.add(null);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 148 */     if (primaryMixIn != null) {
/* 149 */       MemberKey[] ctorKeys = null;
/* 150 */       for (ClassUtil.Ctor mixinCtor : ClassUtil.getConstructors(primaryMixIn)) {
/* 151 */         if (mixinCtor.getParamCount() == 0) {
/* 152 */           if (defaultCtor != null) {
/* 153 */             this._defaultConstructor = constructDefaultConstructor(defaultCtor, mixinCtor);
/* 154 */             defaultCtor = null;
/*     */           }
/*     */         
/*     */         }
/* 158 */         else if (ctors != null) {
/* 159 */           if (ctorKeys == null) {
/* 160 */             ctorKeys = new MemberKey[ctorCount];
/* 161 */             for (int k = 0; k < ctorCount; k++) {
/* 162 */               ctorKeys[k] = new MemberKey(((ClassUtil.Ctor)ctors.get(k)).getConstructor());
/*     */             }
/*     */           } 
/* 165 */           MemberKey key = new MemberKey(mixinCtor.getConstructor());
/*     */           
/* 167 */           for (int j = 0; j < ctorCount; j++) {
/* 168 */             if (key.equals(ctorKeys[j])) {
/* 169 */               result.set(j, 
/* 170 */                   constructNonDefaultConstructor(ctors.get(j), mixinCtor));
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 178 */     if (defaultCtor != null) {
/* 179 */       this._defaultConstructor = constructDefaultConstructor(defaultCtor, (ClassUtil.Ctor)null);
/*     */     }
/* 181 */     for (int i = 0; i < ctorCount; i++) {
/* 182 */       AnnotatedConstructor ctor = result.get(i);
/* 183 */       if (ctor == null) {
/* 184 */         result.set(i, 
/* 185 */             constructNonDefaultConstructor(ctors.get(i), (ClassUtil.Ctor)null));
/*     */       }
/*     */     } 
/* 188 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private List<AnnotatedMethod> _findPotentialFactories(TypeFactory typeFactory, JavaType type, Class<?> primaryMixIn) {
/* 194 */     List<Method> candidates = null;
/*     */ 
/*     */     
/* 197 */     for (Method m : ClassUtil.getClassMethods(type.getRawClass())) {
/* 198 */       if (_isIncludableFactoryMethod(m)) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 203 */         if (candidates == null) {
/* 204 */           candidates = new ArrayList<>();
/*     */         }
/* 206 */         candidates.add(m);
/*     */       } 
/*     */     } 
/* 209 */     if (candidates == null) {
/* 210 */       return Collections.emptyList();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 229 */     TypeResolutionContext initialTypeResCtxt = this._typeContext;
/*     */     
/* 231 */     int factoryCount = candidates.size();
/* 232 */     List<AnnotatedMethod> result = new ArrayList<>(factoryCount); int i;
/* 233 */     for (i = 0; i < factoryCount; i++) {
/* 234 */       result.add(null);
/*     */     }
/*     */     
/* 237 */     if (primaryMixIn != null) {
/* 238 */       MemberKey[] methodKeys = null;
/* 239 */       for (Method mixinFactory : primaryMixIn.getDeclaredMethods()) {
/* 240 */         if (_isIncludableFactoryMethod(mixinFactory)) {
/*     */ 
/*     */           
/* 243 */           if (methodKeys == null) {
/* 244 */             methodKeys = new MemberKey[factoryCount];
/* 245 */             for (int k = 0; k < factoryCount; k++) {
/* 246 */               methodKeys[k] = new MemberKey(candidates.get(k));
/*     */             }
/*     */           } 
/* 249 */           MemberKey key = new MemberKey(mixinFactory);
/* 250 */           for (int j = 0; j < factoryCount; j++) {
/* 251 */             if (key.equals(methodKeys[j])) {
/* 252 */               result.set(j, 
/* 253 */                   constructFactoryCreator(candidates.get(j), initialTypeResCtxt, mixinFactory));
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 261 */     for (i = 0; i < factoryCount; i++) {
/* 262 */       AnnotatedMethod factory = result.get(i);
/* 263 */       if (factory == null) {
/* 264 */         Method candidate = candidates.get(i);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 269 */         TypeResolutionContext typeResCtxt = MethodGenericTypeResolver.narrowMethodTypeParameters(candidate, type, typeFactory, initialTypeResCtxt);
/*     */         
/* 271 */         result.set(i, 
/* 272 */             constructFactoryCreator(candidate, typeResCtxt, (Method)null));
/*     */       } 
/*     */     } 
/* 275 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean _isIncludableFactoryMethod(Method m) {
/* 280 */     return (Modifier.isStatic(m.getModifiers()) && 
/*     */ 
/*     */       
/* 283 */       !m.isSynthetic());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedConstructor constructDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
/* 289 */     return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), 
/* 290 */         collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedConstructor constructNonDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
/*     */     AnnotationMap[] resolvedAnnotations;
/* 298 */     int paramCount = ctor.getParamCount();
/* 299 */     if (this._intr == null) {
/* 300 */       return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), 
/* 301 */           _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 308 */     if (paramCount == 0) {
/* 309 */       return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), 
/* 310 */           collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 315 */     Annotation[][] paramAnns = ctor.getParameterAnnotations();
/* 316 */     if (paramCount != paramAnns.length) {
/*     */ 
/*     */ 
/*     */       
/* 320 */       resolvedAnnotations = null;
/* 321 */       Class<?> dc = ctor.getDeclaringClass();
/*     */       
/* 323 */       if (ClassUtil.isEnumType(dc) && paramCount == paramAnns.length + 2) {
/* 324 */         Annotation[][] old = paramAnns;
/* 325 */         paramAnns = new Annotation[old.length + 2][];
/* 326 */         System.arraycopy(old, 0, paramAnns, 2, old.length);
/* 327 */         resolvedAnnotations = collectAnnotations(paramAnns, (Annotation[][])null);
/* 328 */       } else if (dc.isMemberClass()) {
/*     */         
/* 330 */         if (paramCount == paramAnns.length + 1) {
/*     */           
/* 332 */           Annotation[][] old = paramAnns;
/* 333 */           paramAnns = new Annotation[old.length + 1][];
/* 334 */           System.arraycopy(old, 0, paramAnns, 1, old.length);
/* 335 */           paramAnns[0] = NO_ANNOTATIONS;
/* 336 */           resolvedAnnotations = collectAnnotations(paramAnns, (Annotation[][])null);
/*     */         } 
/*     */       } 
/* 339 */       if (resolvedAnnotations == null) {
/* 340 */         throw new IllegalStateException(String.format("Internal error: constructor for %s has mismatch: %d parameters; %d sets of annotations", new Object[] { ctor
/*     */                 
/* 342 */                 .getDeclaringClass().getName(), Integer.valueOf(paramCount), Integer.valueOf(paramAnns.length) }));
/*     */       }
/*     */     } else {
/* 345 */       resolvedAnnotations = collectAnnotations(paramAnns, (mixin == null) ? (Annotation[][])null : mixin
/* 346 */           .getParameterAnnotations());
/*     */     } 
/* 348 */     return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), 
/* 349 */         collectAnnotations(ctor, mixin), resolvedAnnotations);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedMethod constructFactoryCreator(Method m, TypeResolutionContext typeResCtxt, Method mixin) {
/* 355 */     int paramCount = (m.getParameterTypes()).length;
/* 356 */     if (this._intr == null) {
/* 357 */       return new AnnotatedMethod(typeResCtxt, m, _emptyAnnotationMap(), 
/* 358 */           _emptyAnnotationMaps(paramCount));
/*     */     }
/* 360 */     if (paramCount == 0) {
/* 361 */       return new AnnotatedMethod(typeResCtxt, m, collectAnnotations(m, mixin), NO_ANNOTATION_MAPS);
/*     */     }
/*     */     
/* 364 */     return new AnnotatedMethod(typeResCtxt, m, collectAnnotations(m, mixin), 
/* 365 */         collectAnnotations(m.getParameterAnnotations(), (mixin == null) ? (Annotation[][])null : mixin
/* 366 */           .getParameterAnnotations()));
/*     */   }
/*     */   
/*     */   private AnnotationMap[] collectAnnotations(Annotation[][] mainAnns, Annotation[][] mixinAnns) {
/* 370 */     if (this._collectAnnotations) {
/* 371 */       int count = mainAnns.length;
/* 372 */       AnnotationMap[] result = new AnnotationMap[count];
/* 373 */       for (int i = 0; i < count; i++) {
/* 374 */         AnnotationCollector c = collectAnnotations(AnnotationCollector.emptyCollector(), mainAnns[i]);
/*     */         
/* 376 */         if (mixinAnns != null) {
/* 377 */           c = collectAnnotations(c, mixinAnns[i]);
/*     */         }
/* 379 */         result[i] = c.asAnnotationMap();
/*     */       } 
/* 381 */       return result;
/*     */     } 
/* 383 */     return NO_ANNOTATION_MAPS;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private AnnotationMap collectAnnotations(ClassUtil.Ctor main, ClassUtil.Ctor mixin) {
/* 389 */     if (this._collectAnnotations) {
/* 390 */       AnnotationCollector c = collectAnnotations(main.getDeclaredAnnotations());
/* 391 */       if (mixin != null) {
/* 392 */         c = collectAnnotations(c, mixin.getDeclaredAnnotations());
/*     */       }
/* 394 */       return c.asAnnotationMap();
/*     */     } 
/* 396 */     return _emptyAnnotationMap();
/*     */   }
/*     */   
/*     */   private final AnnotationMap collectAnnotations(AnnotatedElement main, AnnotatedElement mixin) {
/* 400 */     AnnotationCollector c = collectAnnotations(main.getDeclaredAnnotations());
/* 401 */     if (mixin != null) {
/* 402 */       c = collectAnnotations(c, mixin.getDeclaredAnnotations());
/*     */     }
/* 404 */     return c.asAnnotationMap();
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isIncludableConstructor(Constructor<?> c) {
/* 409 */     return !c.isSynthetic();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedCreatorCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */