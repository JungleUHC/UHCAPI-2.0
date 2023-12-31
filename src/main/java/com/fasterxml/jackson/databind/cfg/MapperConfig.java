/*     */ package com.fasterxml.jackson.databind.cfg;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.annotation.JsonIncludeProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonSetter;
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.io.SerializedString;
/*     */ import com.fasterxml.jackson.core.type.TypeReference;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*     */ import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
/*     */ import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*     */ import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
/*     */ import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
/*     */ import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ import java.text.DateFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class MapperConfig<T extends MapperConfig<T>>
/*     */   implements ClassIntrospector.MixInResolver, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*  51 */   protected static final JsonInclude.Value EMPTY_INCLUDE = JsonInclude.Value.empty();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   protected static final JsonFormat.Value EMPTY_FORMAT = JsonFormat.Value.empty();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final long _mapperFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final BaseSettings _base;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MapperConfig(BaseSettings base, long mapperFeatures) {
/*  79 */     this._base = base;
/*  80 */     this._mapperFeatures = mapperFeatures;
/*     */   }
/*     */ 
/*     */   
/*     */   protected MapperConfig(MapperConfig<T> src, long mapperFeatures) {
/*  85 */     this._base = src._base;
/*  86 */     this._mapperFeatures = mapperFeatures;
/*     */   }
/*     */ 
/*     */   
/*     */   protected MapperConfig(MapperConfig<T> src, BaseSettings base) {
/*  91 */     this._base = base;
/*  92 */     this._mapperFeatures = src._mapperFeatures;
/*     */   }
/*     */ 
/*     */   
/*     */   protected MapperConfig(MapperConfig<T> src) {
/*  97 */     this._base = src._base;
/*  98 */     this._mapperFeatures = src._mapperFeatures;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <F extends Enum<F> & ConfigFeature> int collectFeatureDefaults(Class<F> enumClass) {
/* 107 */     int flags = 0;
/* 108 */     for (Enum enum_ : (Enum[])enumClass.getEnumConstants()) {
/* 109 */       if (((ConfigFeature)enum_).enabledByDefault()) {
/* 110 */         flags |= ((ConfigFeature)enum_).getMask();
/*     */       }
/*     */     } 
/* 113 */     return flags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract T with(MapperFeature... paramVarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract T without(MapperFeature... paramVarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract T with(MapperFeature paramMapperFeature, boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEnabled(MapperFeature f) {
/* 150 */     return f.enabledIn(this._mapperFeatures);
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
/*     */   public final boolean hasMapperFeatures(int featureMask) {
/* 163 */     return ((this._mapperFeatures & featureMask) == featureMask);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isAnnotationProcessingEnabled() {
/* 173 */     return isEnabled(MapperFeature.USE_ANNOTATIONS);
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
/*     */   public final boolean canOverrideAccessModifiers() {
/* 188 */     return isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean shouldSortPropertiesAlphabetically() {
/* 196 */     return isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
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
/*     */   public abstract boolean useRootWrapping();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SerializableString compileString(String src) {
/* 228 */     return (SerializableString)new SerializedString(src);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassIntrospector getClassIntrospector() {
/* 238 */     return this._base.getClassIntrospector();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationIntrospector getAnnotationIntrospector() {
/* 248 */     if (isEnabled(MapperFeature.USE_ANNOTATIONS)) {
/* 249 */       return this._base.getAnnotationIntrospector();
/*     */     }
/* 251 */     return (AnnotationIntrospector)NopAnnotationIntrospector.instance;
/*     */   }
/*     */   
/*     */   public final PropertyNamingStrategy getPropertyNamingStrategy() {
/* 255 */     return this._base.getPropertyNamingStrategy();
/*     */   }
/*     */ 
/*     */   
/*     */   public final AccessorNamingStrategy.Provider getAccessorNaming() {
/* 260 */     return this._base.getAccessorNaming();
/*     */   }
/*     */   
/*     */   public final HandlerInstantiator getHandlerInstantiator() {
/* 264 */     return this._base.getHandlerInstantiator();
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
/*     */   public final TypeResolverBuilder<?> getDefaultTyper(JavaType baseType) {
/* 280 */     return this._base.getTypeResolverBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract SubtypeResolver getSubtypeResolver();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolymorphicTypeValidator getPolymorphicTypeValidator() {
/*     */     DefaultBaseTypeLimitingValidator defaultBaseTypeLimitingValidator;
/* 297 */     PolymorphicTypeValidator ptv = this._base.getPolymorphicTypeValidator();
/*     */     
/* 299 */     if (ptv == LaissezFaireSubTypeValidator.instance && 
/* 300 */       isEnabled(MapperFeature.BLOCK_UNSAFE_POLYMORPHIC_BASE_TYPES)) {
/* 301 */       defaultBaseTypeLimitingValidator = new DefaultBaseTypeLimitingValidator();
/*     */     }
/*     */     
/* 304 */     return (PolymorphicTypeValidator)defaultBaseTypeLimitingValidator;
/*     */   }
/*     */   
/*     */   public final TypeFactory getTypeFactory() {
/* 308 */     return this._base.getTypeFactory();
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
/*     */   public final JavaType constructType(Class<?> cls) {
/* 320 */     return getTypeFactory().constructType(cls);
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
/*     */   public final JavaType constructType(TypeReference<?> valueTypeRef) {
/* 332 */     return getTypeFactory().constructType(valueTypeRef.getType());
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
/* 337 */     return getTypeFactory().constructSpecializedType(baseType, subclass, true);
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
/*     */   public BeanDescription introspectClassAnnotations(Class<?> cls) {
/* 351 */     return introspectClassAnnotations(constructType(cls));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDescription introspectClassAnnotations(JavaType type) {
/* 359 */     return getClassIntrospector().forClassAnnotations(this, type, this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDescription introspectDirectClassAnnotations(Class<?> cls) {
/* 368 */     return introspectDirectClassAnnotations(constructType(cls));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final BeanDescription introspectDirectClassAnnotations(JavaType type) {
/* 377 */     return getClassIntrospector().forDirectClassAnnotations(this, type, this);
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
/*     */   public abstract ConfigOverride findConfigOverride(Class<?> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ConfigOverride getConfigOverride(Class<?> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonInclude.Value getDefaultPropertyInclusion();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonInclude.Value getDefaultPropertyInclusion(Class<?> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType, JsonInclude.Value defaultIncl) {
/* 443 */     JsonInclude.Value v = getConfigOverride(baseType).getInclude();
/* 444 */     if (v != null) {
/* 445 */       return v;
/*     */     }
/* 447 */     return defaultIncl;
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
/*     */   public abstract JsonInclude.Value getDefaultInclusion(Class<?> paramClass1, Class<?> paramClass2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonInclude.Value getDefaultInclusion(Class<?> baseType, Class<?> propertyType, JsonInclude.Value defaultIncl) {
/* 480 */     JsonInclude.Value baseOverride = getConfigOverride(baseType).getInclude();
/* 481 */     JsonInclude.Value propOverride = getConfigOverride(propertyType).getIncludeAsProperty();
/*     */     
/* 483 */     JsonInclude.Value result = JsonInclude.Value.mergeAll(new JsonInclude.Value[] { defaultIncl, baseOverride, propOverride });
/* 484 */     return result;
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
/*     */   public abstract JsonFormat.Value getDefaultPropertyFormat(Class<?> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> paramClass, AnnotatedClass paramAnnotatedClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonIncludeProperties.Value getDefaultPropertyInclusions(Class<?> paramClass, AnnotatedClass paramAnnotatedClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract VisibilityChecker<?> getDefaultVisibilityChecker();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract VisibilityChecker<?> getDefaultVisibilityChecker(Class<?> paramClass, AnnotatedClass paramAnnotatedClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonSetter.Value getDefaultSetterInfo();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Boolean getDefaultMergeable();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Boolean getDefaultMergeable(Class<?> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final DateFormat getDateFormat() {
/* 604 */     return this._base.getDateFormat();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Locale getLocale() {
/* 611 */     return this._base.getLocale();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final TimeZone getTimeZone() {
/* 618 */     return this._base.getTimeZone();
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
/*     */   public boolean hasExplicitTimeZone() {
/* 632 */     return this._base.hasExplicitTimeZone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Class<?> getActiveView();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64Variant getBase64Variant() {
/* 648 */     return this._base.getBase64Variant();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ContextAttributes getAttributes();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract PropertyName findRootName(JavaType paramJavaType);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract PropertyName findRootName(Class<?> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeResolverBuilder<?> typeResolverBuilderInstance(Annotated annotated, Class<? extends TypeResolverBuilder<?>> builderClass) {
/* 683 */     HandlerInstantiator hi = getHandlerInstantiator();
/* 684 */     if (hi != null) {
/* 685 */       TypeResolverBuilder<?> builder = hi.typeResolverBuilderInstance(this, annotated, builderClass);
/* 686 */       if (builder != null) {
/* 687 */         return builder;
/*     */       }
/*     */     } 
/* 690 */     return (TypeResolverBuilder)ClassUtil.createInstance(builderClass, canOverrideAccessModifiers());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeIdResolver typeIdResolverInstance(Annotated annotated, Class<? extends TypeIdResolver> resolverClass) {
/* 700 */     HandlerInstantiator hi = getHandlerInstantiator();
/* 701 */     if (hi != null) {
/* 702 */       TypeIdResolver builder = hi.typeIdResolverInstance(this, annotated, resolverClass);
/* 703 */       if (builder != null) {
/* 704 */         return builder;
/*     */       }
/*     */     } 
/* 707 */     return (TypeIdResolver)ClassUtil.createInstance(resolverClass, canOverrideAccessModifiers());
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/cfg/MapperConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */