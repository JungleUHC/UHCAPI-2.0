/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.type.TypeBindings;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.Annotations;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class AnnotatedClass
/*     */   extends Annotated
/*     */   implements TypeResolutionContext
/*     */ {
/*  20 */   private static final Creators NO_CREATORS = new Creators(null, 
/*  21 */       Collections.emptyList(), 
/*  22 */       Collections.emptyList());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JavaType _type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Class<?> _class;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final TypeBindings _bindings;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final List<JavaType> _superTypes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AnnotationIntrospector _annotationIntrospector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final TypeFactory _typeFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ClassIntrospector.MixInResolver _mixInResolver;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Class<?> _primaryMixIn;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _collectAnnotations;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Annotations _classAnnotations;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Creators _creators;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedMethodMap _memberMethods;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<AnnotatedField> _fields;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient Boolean _nonStaticInnerClass;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AnnotatedClass(JavaType type, Class<?> rawType, List<JavaType> superTypes, Class<?> primaryMixIn, Annotations classAnnotations, TypeBindings bindings, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir, TypeFactory tf, boolean collectAnnotations) {
/* 143 */     this._type = type;
/* 144 */     this._class = rawType;
/* 145 */     this._superTypes = superTypes;
/* 146 */     this._primaryMixIn = primaryMixIn;
/* 147 */     this._classAnnotations = classAnnotations;
/* 148 */     this._bindings = bindings;
/* 149 */     this._annotationIntrospector = aintr;
/* 150 */     this._mixInResolver = mir;
/* 151 */     this._typeFactory = tf;
/* 152 */     this._collectAnnotations = collectAnnotations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   AnnotatedClass(JavaType type, Class<?> rawType, List<JavaType> superTypes, Class<?> primaryMixIn, Annotations classAnnotations, TypeBindings bindings, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir, TypeFactory tf) {
/* 160 */     this(type, rawType, superTypes, primaryMixIn, classAnnotations, bindings, aintr, mir, tf, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AnnotatedClass(Class<?> rawType) {
/* 171 */     this._type = null;
/* 172 */     this._class = rawType;
/* 173 */     this._superTypes = Collections.emptyList();
/* 174 */     this._primaryMixIn = null;
/* 175 */     this._classAnnotations = AnnotationCollector.emptyAnnotations();
/* 176 */     this._bindings = TypeBindings.emptyBindings();
/* 177 */     this._annotationIntrospector = null;
/* 178 */     this._mixInResolver = null;
/* 179 */     this._typeFactory = null;
/* 180 */     this._collectAnnotations = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static AnnotatedClass construct(JavaType type, MapperConfig<?> config) {
/* 188 */     return construct(type, config, (ClassIntrospector.MixInResolver)config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static AnnotatedClass construct(JavaType type, MapperConfig<?> config, ClassIntrospector.MixInResolver mir) {
/* 198 */     return AnnotatedClassResolver.resolve(config, type, mir);
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
/*     */   public static AnnotatedClass constructWithoutSuperTypes(Class<?> raw, MapperConfig<?> config) {
/* 211 */     return constructWithoutSuperTypes(raw, config, (ClassIntrospector.MixInResolver)config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static AnnotatedClass constructWithoutSuperTypes(Class<?> raw, MapperConfig<?> config, ClassIntrospector.MixInResolver mir) {
/* 221 */     return AnnotatedClassResolver.resolveWithoutSuperTypes(config, raw, mir);
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
/*     */   public JavaType resolveType(Type type) {
/* 234 */     return this._typeFactory.resolveMemberType(type, this._bindings);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getAnnotated() {
/* 244 */     return this._class;
/*     */   }
/*     */   public int getModifiers() {
/* 247 */     return this._class.getModifiers();
/*     */   }
/*     */   public String getName() {
/* 250 */     return this._class.getName();
/*     */   }
/*     */   
/*     */   public <A extends Annotation> A getAnnotation(Class<A> acls) {
/* 254 */     return (A)this._classAnnotations.get(acls);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasAnnotation(Class<?> acls) {
/* 259 */     return this._classAnnotations.has(acls);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
/* 264 */     return this._classAnnotations.hasOneOf((Class[])annoClasses);
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getRawType() {
/* 269 */     return this._class;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Iterable<Annotation> annotations() {
/* 275 */     if (this._classAnnotations instanceof AnnotationMap)
/* 276 */       return ((AnnotationMap)this._classAnnotations).annotations(); 
/* 277 */     if (this._classAnnotations instanceof AnnotationCollector.OneAnnotation || this._classAnnotations instanceof AnnotationCollector.TwoAnnotations)
/*     */     {
/* 279 */       throw new UnsupportedOperationException("please use getAnnotations/ hasAnnotation to check for Annotations");
/*     */     }
/* 281 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getType() {
/* 286 */     return this._type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Annotations getAnnotations() {
/* 296 */     return this._classAnnotations;
/*     */   }
/*     */   
/*     */   public boolean hasAnnotations() {
/* 300 */     return (this._classAnnotations.size() > 0);
/*     */   }
/*     */   
/*     */   public AnnotatedConstructor getDefaultConstructor() {
/* 304 */     return (_creators()).defaultConstructor;
/*     */   }
/*     */   
/*     */   public List<AnnotatedConstructor> getConstructors() {
/* 308 */     return (_creators()).constructors;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<AnnotatedMethod> getFactoryMethods() {
/* 315 */     return (_creators()).creatorMethods;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public List<AnnotatedMethod> getStaticMethods() {
/* 323 */     return getFactoryMethods();
/*     */   }
/*     */   
/*     */   public Iterable<AnnotatedMethod> memberMethods() {
/* 327 */     return _methods();
/*     */   }
/*     */   
/*     */   public int getMemberMethodCount() {
/* 331 */     return _methods().size();
/*     */   }
/*     */   
/*     */   public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
/* 335 */     return _methods().find(name, paramTypes);
/*     */   }
/*     */   
/*     */   public int getFieldCount() {
/* 339 */     return _fields().size();
/*     */   }
/*     */   
/*     */   public Iterable<AnnotatedField> fields() {
/* 343 */     return _fields();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNonStaticInnerClass() {
/* 351 */     Boolean B = this._nonStaticInnerClass;
/* 352 */     if (B == null) {
/* 353 */       this._nonStaticInnerClass = B = Boolean.valueOf(ClassUtil.isNonStaticInnerClass(this._class));
/*     */     }
/* 355 */     return B.booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final List<AnnotatedField> _fields() {
/* 365 */     List<AnnotatedField> f = this._fields;
/* 366 */     if (f == null) {
/*     */       
/* 368 */       if (this._type == null) {
/* 369 */         f = Collections.emptyList();
/*     */       } else {
/* 371 */         f = AnnotatedFieldCollector.collectFields(this._annotationIntrospector, this, this._mixInResolver, this._typeFactory, this._type, this._collectAnnotations);
/*     */       } 
/*     */       
/* 374 */       this._fields = f;
/*     */     } 
/* 376 */     return f;
/*     */   }
/*     */   
/*     */   private final AnnotatedMethodMap _methods() {
/* 380 */     AnnotatedMethodMap m = this._memberMethods;
/* 381 */     if (m == null) {
/*     */ 
/*     */       
/* 384 */       if (this._type == null) {
/* 385 */         m = new AnnotatedMethodMap();
/*     */       } else {
/* 387 */         m = AnnotatedMethodCollector.collectMethods(this._annotationIntrospector, this, this._mixInResolver, this._typeFactory, this._type, this._superTypes, this._primaryMixIn, this._collectAnnotations);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 392 */       this._memberMethods = m;
/*     */     } 
/* 394 */     return m;
/*     */   }
/*     */   
/*     */   private final Creators _creators() {
/* 398 */     Creators c = this._creators;
/* 399 */     if (c == null) {
/* 400 */       if (this._type == null) {
/* 401 */         c = NO_CREATORS;
/*     */       } else {
/* 403 */         c = AnnotatedCreatorCollector.collectCreators(this._annotationIntrospector, this._typeFactory, this, this._type, this._primaryMixIn, this._collectAnnotations);
/*     */       } 
/*     */ 
/*     */       
/* 407 */       this._creators = c;
/*     */     } 
/* 409 */     return c;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 420 */     return "[AnnotedClass " + this._class.getName() + "]";
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 425 */     return this._class.getName().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 430 */     if (o == this) return true; 
/* 431 */     if (!ClassUtil.hasClass(o, getClass())) {
/* 432 */       return false;
/*     */     }
/* 434 */     return (((AnnotatedClass)o)._class == this._class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Creators
/*     */   {
/*     */     public final AnnotatedConstructor defaultConstructor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final List<AnnotatedConstructor> constructors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final List<AnnotatedMethod> creatorMethods;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Creators(AnnotatedConstructor defCtor, List<AnnotatedConstructor> ctors, List<AnnotatedMethod> ctorMethods) {
/* 465 */       this.defaultConstructor = defCtor;
/* 466 */       this.constructors = ctors;
/* 467 */       this.creatorMethods = ctorMethods;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotatedClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */