/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.type.TypeBindings;
/*     */ import com.fasterxml.jackson.databind.util.Annotations;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AnnotatedClassResolver
/*     */ {
/*  27 */   private static final Annotations NO_ANNOTATIONS = AnnotationCollector.emptyAnnotations();
/*     */   
/*  29 */   private static final Class<?> CLS_OBJECT = Object.class;
/*  30 */   private static final Class<?> CLS_ENUM = Enum.class;
/*     */   
/*  32 */   private static final Class<?> CLS_LIST = List.class;
/*  33 */   private static final Class<?> CLS_MAP = Map.class;
/*     */   
/*     */   private final MapperConfig<?> _config;
/*     */   
/*     */   private final AnnotationIntrospector _intr;
/*     */   
/*     */   private final ClassIntrospector.MixInResolver _mixInResolver;
/*     */   
/*     */   private final TypeBindings _bindings;
/*     */   
/*     */   private final JavaType _type;
/*     */   
/*     */   private final Class<?> _class;
/*     */   private final Class<?> _primaryMixin;
/*     */   private final boolean _collectAnnotations;
/*     */   
/*     */   AnnotatedClassResolver(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
/*  50 */     this._config = config;
/*  51 */     this._type = type;
/*  52 */     this._class = type.getRawClass();
/*  53 */     this._mixInResolver = r;
/*  54 */     this._bindings = type.getBindings();
/*  55 */     this
/*  56 */       ._intr = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
/*  57 */     this._primaryMixin = (r == null) ? null : r.findMixInClassFor(this._class);
/*     */ 
/*     */ 
/*     */     
/*  61 */     this
/*  62 */       ._collectAnnotations = (this._intr != null && (!ClassUtil.isJDKClass(this._class) || !this._type.isContainerType()));
/*     */   }
/*     */   
/*     */   AnnotatedClassResolver(MapperConfig<?> config, Class<?> cls, ClassIntrospector.MixInResolver r) {
/*  66 */     this._config = config;
/*  67 */     this._type = null;
/*  68 */     this._class = cls;
/*  69 */     this._mixInResolver = r;
/*  70 */     this._bindings = TypeBindings.emptyBindings();
/*  71 */     if (config == null) {
/*  72 */       this._intr = null;
/*  73 */       this._primaryMixin = null;
/*     */     } else {
/*  75 */       this
/*  76 */         ._intr = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
/*  77 */       this._primaryMixin = (r == null) ? null : r.findMixInClassFor(this._class);
/*     */     } 
/*     */     
/*  80 */     this._collectAnnotations = (this._intr != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static AnnotatedClass resolve(MapperConfig<?> config, JavaType forType, ClassIntrospector.MixInResolver r) {
/*  86 */     if (forType.isArrayType() && skippableArray(config, forType.getRawClass())) {
/*  87 */       return createArrayType(config, forType.getRawClass());
/*     */     }
/*  89 */     return (new AnnotatedClassResolver(config, forType, r)).resolveFully();
/*     */   }
/*     */   
/*     */   public static AnnotatedClass resolveWithoutSuperTypes(MapperConfig<?> config, Class<?> forType) {
/*  93 */     return resolveWithoutSuperTypes(config, forType, (ClassIntrospector.MixInResolver)config);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static AnnotatedClass resolveWithoutSuperTypes(MapperConfig<?> config, JavaType forType, ClassIntrospector.MixInResolver r) {
/*  99 */     if (forType.isArrayType() && skippableArray(config, forType.getRawClass())) {
/* 100 */       return createArrayType(config, forType.getRawClass());
/*     */     }
/* 102 */     return (new AnnotatedClassResolver(config, forType, r)).resolveWithoutSuperTypes();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static AnnotatedClass resolveWithoutSuperTypes(MapperConfig<?> config, Class<?> forType, ClassIntrospector.MixInResolver r) {
/* 108 */     if (forType.isArray() && skippableArray(config, forType)) {
/* 109 */       return createArrayType(config, forType);
/*     */     }
/* 111 */     return (new AnnotatedClassResolver(config, forType, r)).resolveWithoutSuperTypes();
/*     */   }
/*     */   
/*     */   private static boolean skippableArray(MapperConfig<?> config, Class<?> type) {
/* 115 */     return (config == null || config.findMixInClassFor(type) == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static AnnotatedClass createPrimordial(Class<?> raw) {
/* 123 */     return new AnnotatedClass(raw);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static AnnotatedClass createArrayType(MapperConfig<?> config, Class<?> raw) {
/* 131 */     return new AnnotatedClass(raw);
/*     */   }
/*     */   
/*     */   AnnotatedClass resolveFully() {
/* 135 */     List<JavaType> superTypes = new ArrayList<>(8);
/* 136 */     if (!this._type.hasRawClass(Object.class)) {
/* 137 */       if (this._type.isInterface()) {
/* 138 */         _addSuperInterfaces(this._type, superTypes, false);
/*     */       } else {
/* 140 */         _addSuperTypes(this._type, superTypes, false);
/*     */       } 
/*     */     }
/*     */     
/* 144 */     return new AnnotatedClass(this._type, this._class, superTypes, this._primaryMixin, 
/* 145 */         resolveClassAnnotations(superTypes), this._bindings, this._intr, this._mixInResolver, this._config
/* 146 */         .getTypeFactory(), this._collectAnnotations);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   AnnotatedClass resolveWithoutSuperTypes() {
/* 152 */     List<JavaType> superTypes = Collections.emptyList();
/* 153 */     return new AnnotatedClass(null, this._class, superTypes, this._primaryMixin, 
/* 154 */         resolveClassAnnotations(superTypes), this._bindings, this._intr, this._mixInResolver, this._config
/* 155 */         .getTypeFactory(), this._collectAnnotations);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void _addSuperTypes(JavaType type, List<JavaType> result, boolean addClassItself) {
/* 162 */     Class<?> cls = type.getRawClass();
/*     */ 
/*     */     
/* 165 */     if (cls == CLS_OBJECT || cls == CLS_ENUM) {
/*     */       return;
/*     */     }
/* 168 */     if (addClassItself) {
/* 169 */       if (_contains(result, cls)) {
/*     */         return;
/*     */       }
/* 172 */       result.add(type);
/*     */     } 
/* 174 */     for (JavaType intCls : type.getInterfaces()) {
/* 175 */       _addSuperInterfaces(intCls, result, true);
/*     */     }
/* 177 */     JavaType superType = type.getSuperClass();
/* 178 */     if (superType != null) {
/* 179 */       _addSuperTypes(superType, result, true);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void _addSuperInterfaces(JavaType type, List<JavaType> result, boolean addClassItself) {
/* 186 */     Class<?> cls = type.getRawClass();
/* 187 */     if (addClassItself) {
/* 188 */       if (_contains(result, cls)) {
/*     */         return;
/*     */       }
/* 191 */       result.add(type);
/*     */       
/* 193 */       if (cls == CLS_LIST || cls == CLS_MAP) {
/*     */         return;
/*     */       }
/*     */     } 
/* 197 */     for (JavaType intCls : type.getInterfaces()) {
/* 198 */       _addSuperInterfaces(intCls, result, true);
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean _contains(List<JavaType> found, Class<?> raw) {
/* 203 */     for (int i = 0, end = found.size(); i < end; i++) {
/* 204 */       if (((JavaType)found.get(i)).getRawClass() == raw) {
/* 205 */         return true;
/*     */       }
/*     */     } 
/* 208 */     return false;
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
/*     */   private Annotations resolveClassAnnotations(List<JavaType> superTypes) {
/* 225 */     if (this._intr == null) {
/* 226 */       return NO_ANNOTATIONS;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 231 */     boolean checkMixIns = (this._mixInResolver != null && (!(this._mixInResolver instanceof SimpleMixInResolver) || ((SimpleMixInResolver)this._mixInResolver).hasMixIns()));
/*     */ 
/*     */     
/* 234 */     if (!checkMixIns && !this._collectAnnotations) {
/* 235 */       return NO_ANNOTATIONS;
/*     */     }
/*     */     
/* 238 */     AnnotationCollector resolvedCA = AnnotationCollector.emptyCollector();
/*     */     
/* 240 */     if (this._primaryMixin != null) {
/* 241 */       resolvedCA = _addClassMixIns(resolvedCA, this._class, this._primaryMixin);
/*     */     }
/*     */ 
/*     */     
/* 245 */     if (this._collectAnnotations) {
/* 246 */       resolvedCA = _addAnnotationsIfNotPresent(resolvedCA, 
/* 247 */           ClassUtil.findClassAnnotations(this._class));
/*     */     }
/*     */ 
/*     */     
/* 251 */     for (JavaType type : superTypes) {
/*     */       
/* 253 */       if (checkMixIns) {
/* 254 */         Class<?> cls = type.getRawClass();
/* 255 */         resolvedCA = _addClassMixIns(resolvedCA, cls, this._mixInResolver
/* 256 */             .findMixInClassFor(cls));
/*     */       } 
/* 258 */       if (this._collectAnnotations) {
/* 259 */         resolvedCA = _addAnnotationsIfNotPresent(resolvedCA, 
/* 260 */             ClassUtil.findClassAnnotations(type.getRawClass()));
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 269 */     if (checkMixIns) {
/* 270 */       resolvedCA = _addClassMixIns(resolvedCA, Object.class, this._mixInResolver
/* 271 */           .findMixInClassFor(Object.class));
/*     */     }
/* 273 */     return resolvedCA.asAnnotations();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private AnnotationCollector _addClassMixIns(AnnotationCollector annotations, Class<?> target, Class<?> mixin) {
/* 279 */     if (mixin != null) {
/*     */       
/* 281 */       annotations = _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(mixin));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 288 */       for (Class<?> parent : (Iterable<Class<?>>)ClassUtil.findSuperClasses(mixin, target, false)) {
/* 289 */         annotations = _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(parent));
/*     */       }
/*     */     } 
/* 292 */     return annotations;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private AnnotationCollector _addAnnotationsIfNotPresent(AnnotationCollector c, Annotation[] anns) {
/* 298 */     if (anns != null) {
/* 299 */       for (Annotation ann : anns) {
/*     */         
/* 301 */         if (!c.isPresent(ann)) {
/* 302 */           c = c.addOrOverride(ann);
/* 303 */           if (this._intr.isAnnotationBundle(ann)) {
/* 304 */             c = _addFromBundleIfNotPresent(c, ann);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     }
/* 309 */     return c;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private AnnotationCollector _addFromBundleIfNotPresent(AnnotationCollector c, Annotation bundle) {
/* 315 */     for (Annotation ann : ClassUtil.findClassAnnotations(bundle.annotationType())) {
/*     */       
/* 317 */       if (!(ann instanceof java.lang.annotation.Target) && !(ann instanceof java.lang.annotation.Retention))
/*     */       {
/*     */         
/* 320 */         if (!c.isPresent(ann)) {
/* 321 */           c = c.addOrOverride(ann);
/* 322 */           if (this._intr.isAnnotationBundle(ann))
/* 323 */             c = _addFromBundleIfNotPresent(c, ann); 
/*     */         } 
/*     */       }
/*     */     } 
/* 327 */     return c;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedClassResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */