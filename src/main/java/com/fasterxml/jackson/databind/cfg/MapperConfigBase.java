/*     */ package com.fasterxml.jackson.databind.cfg;
/*     */ import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.annotation.JsonIncludeProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonSetter;
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*     */ import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
/*     */ import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
/*     */ import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*     */ import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.RootNameLookup;
/*     */ import java.io.Serializable;
/*     */ import java.text.DateFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public abstract class MapperConfigBase<CFG extends ConfigFeature, T extends MapperConfigBase<CFG, T>> extends MapperConfig<T> implements Serializable {
/*  32 */   protected static final ConfigOverride EMPTY_OVERRIDE = ConfigOverride.empty();
/*     */   
/*  34 */   private static final long DEFAULT_MAPPER_FEATURES = MapperFeature.collectLongDefaults();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  39 */   private static final long AUTO_DETECT_MASK = MapperFeature.AUTO_DETECT_FIELDS
/*  40 */     .getLongMask() | MapperFeature.AUTO_DETECT_GETTERS
/*  41 */     .getLongMask() | MapperFeature.AUTO_DETECT_IS_GETTERS
/*  42 */     .getLongMask() | MapperFeature.AUTO_DETECT_SETTERS
/*  43 */     .getLongMask() | MapperFeature.AUTO_DETECT_CREATORS
/*  44 */     .getLongMask();
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
/*     */   protected final SimpleMixInResolver _mixIns;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SubtypeResolver _subtypeResolver;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final PropertyName _rootName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Class<?> _view;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ContextAttributes _attributes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final RootNameLookup _rootNames;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ConfigOverrides _configOverrides;
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
/*     */   protected MapperConfigBase(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
/* 127 */     super(base, DEFAULT_MAPPER_FEATURES);
/* 128 */     this._mixIns = mixins;
/* 129 */     this._subtypeResolver = str;
/* 130 */     this._rootNames = rootNames;
/* 131 */     this._rootName = null;
/* 132 */     this._view = null;
/*     */     
/* 134 */     this._attributes = ContextAttributes.getEmpty();
/* 135 */     this._configOverrides = configOverrides;
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
/*     */   protected MapperConfigBase(MapperConfigBase<CFG, T> src, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
/* 150 */     super(src, src._base.copy());
/* 151 */     this._mixIns = mixins;
/* 152 */     this._subtypeResolver = str;
/* 153 */     this._rootNames = rootNames;
/* 154 */     this._rootName = src._rootName;
/* 155 */     this._view = src._view;
/* 156 */     this._attributes = src._attributes;
/* 157 */     this._configOverrides = configOverrides;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MapperConfigBase(MapperConfigBase<CFG, T> src) {
/* 166 */     super(src);
/* 167 */     this._mixIns = src._mixIns;
/* 168 */     this._subtypeResolver = src._subtypeResolver;
/* 169 */     this._rootNames = src._rootNames;
/* 170 */     this._rootName = src._rootName;
/* 171 */     this._view = src._view;
/* 172 */     this._attributes = src._attributes;
/* 173 */     this._configOverrides = src._configOverrides;
/*     */   }
/*     */ 
/*     */   
/*     */   protected MapperConfigBase(MapperConfigBase<CFG, T> src, BaseSettings base) {
/* 178 */     super(src, base);
/* 179 */     this._mixIns = src._mixIns;
/* 180 */     this._subtypeResolver = src._subtypeResolver;
/* 181 */     this._rootNames = src._rootNames;
/* 182 */     this._rootName = src._rootName;
/* 183 */     this._view = src._view;
/* 184 */     this._attributes = src._attributes;
/* 185 */     this._configOverrides = src._configOverrides;
/*     */   }
/*     */ 
/*     */   
/*     */   protected MapperConfigBase(MapperConfigBase<CFG, T> src, long mapperFeatures) {
/* 190 */     super(src, mapperFeatures);
/* 191 */     this._mixIns = src._mixIns;
/* 192 */     this._subtypeResolver = src._subtypeResolver;
/* 193 */     this._rootNames = src._rootNames;
/* 194 */     this._rootName = src._rootName;
/* 195 */     this._view = src._view;
/* 196 */     this._attributes = src._attributes;
/* 197 */     this._configOverrides = src._configOverrides;
/*     */   }
/*     */   
/*     */   protected MapperConfigBase(MapperConfigBase<CFG, T> src, SubtypeResolver str) {
/* 201 */     super(src);
/* 202 */     this._mixIns = src._mixIns;
/* 203 */     this._subtypeResolver = str;
/* 204 */     this._rootNames = src._rootNames;
/* 205 */     this._rootName = src._rootName;
/* 206 */     this._view = src._view;
/* 207 */     this._attributes = src._attributes;
/* 208 */     this._configOverrides = src._configOverrides;
/*     */   }
/*     */   
/*     */   protected MapperConfigBase(MapperConfigBase<CFG, T> src, PropertyName rootName) {
/* 212 */     super(src);
/* 213 */     this._mixIns = src._mixIns;
/* 214 */     this._subtypeResolver = src._subtypeResolver;
/* 215 */     this._rootNames = src._rootNames;
/* 216 */     this._rootName = rootName;
/* 217 */     this._view = src._view;
/* 218 */     this._attributes = src._attributes;
/* 219 */     this._configOverrides = src._configOverrides;
/*     */   }
/*     */ 
/*     */   
/*     */   protected MapperConfigBase(MapperConfigBase<CFG, T> src, Class<?> view) {
/* 224 */     super(src);
/* 225 */     this._mixIns = src._mixIns;
/* 226 */     this._subtypeResolver = src._subtypeResolver;
/* 227 */     this._rootNames = src._rootNames;
/* 228 */     this._rootName = src._rootName;
/* 229 */     this._view = view;
/* 230 */     this._attributes = src._attributes;
/* 231 */     this._configOverrides = src._configOverrides;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins) {
/* 239 */     super(src);
/* 240 */     this._mixIns = mixins;
/* 241 */     this._subtypeResolver = src._subtypeResolver;
/* 242 */     this._rootNames = src._rootNames;
/* 243 */     this._rootName = src._rootName;
/* 244 */     this._view = src._view;
/* 245 */     this._attributes = src._attributes;
/* 246 */     this._configOverrides = src._configOverrides;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MapperConfigBase(MapperConfigBase<CFG, T> src, ContextAttributes attr) {
/* 254 */     super(src);
/* 255 */     this._mixIns = src._mixIns;
/* 256 */     this._subtypeResolver = src._subtypeResolver;
/* 257 */     this._rootNames = src._rootNames;
/* 258 */     this._rootName = src._rootName;
/* 259 */     this._view = src._view;
/* 260 */     this._attributes = attr;
/* 261 */     this._configOverrides = src._configOverrides;
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
/*     */   public final T with(MapperFeature... features) {
/* 294 */     long newMapperFlags = this._mapperFeatures;
/* 295 */     for (MapperFeature f : features) {
/* 296 */       newMapperFlags |= f.getLongMask();
/*     */     }
/* 298 */     if (newMapperFlags == this._mapperFeatures) {
/* 299 */       return (T)this;
/*     */     }
/* 301 */     return _withMapperFeatures(newMapperFlags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T without(MapperFeature... features) {
/* 312 */     long newMapperFlags = this._mapperFeatures;
/* 313 */     for (MapperFeature f : features) {
/* 314 */       newMapperFlags &= f.getLongMask() ^ 0xFFFFFFFFFFFFFFFFL;
/*     */     }
/* 316 */     if (newMapperFlags == this._mapperFeatures) {
/* 317 */       return (T)this;
/*     */     }
/* 319 */     return _withMapperFeatures(newMapperFlags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T with(MapperFeature feature, boolean state) {
/*     */     long newMapperFlags;
/* 327 */     if (state) {
/* 328 */       newMapperFlags = this._mapperFeatures | feature.getLongMask();
/*     */     } else {
/* 330 */       newMapperFlags = this._mapperFeatures & (feature.getLongMask() ^ 0xFFFFFFFFFFFFFFFFL);
/*     */     } 
/* 332 */     if (newMapperFlags == this._mapperFeatures) {
/* 333 */       return (T)this;
/*     */     }
/* 335 */     return _withMapperFeatures(newMapperFlags);
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
/*     */   public final T with(AnnotationIntrospector ai) {
/* 352 */     return _withBase(this._base.withAnnotationIntrospector(ai));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
/* 360 */     return _withBase(this._base.withAppendedAnnotationIntrospector(ai));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
/* 368 */     return _withBase(this._base.withInsertedAnnotationIntrospector(ai));
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
/*     */   public final T with(ClassIntrospector ci) {
/* 380 */     return _withBase(this._base.withClassIntrospector(ci));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T withAttributes(Map<?, ?> attributes) {
/* 404 */     return with(getAttributes().withSharedAttributes(attributes));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T withAttribute(Object key, Object value) {
/* 414 */     return with(getAttributes().withSharedAttribute(key, value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T withoutAttribute(Object key) {
/* 424 */     return with(getAttributes().withoutSharedAttribute(key));
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
/*     */   public final T with(TypeFactory tf) {
/* 439 */     return _withBase(this._base.withTypeFactory(tf));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T with(TypeResolverBuilder<?> trb) {
/* 447 */     return _withBase(this._base.withTypeResolverBuilder(trb));
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
/*     */   public final T with(PropertyNamingStrategy pns) {
/* 459 */     return _withBase(this._base.withPropertyNamingStrategy(pns));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T with(AccessorNamingStrategy.Provider p) {
/* 470 */     return _withBase(this._base.withAccessorNaming(p));
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
/*     */   public final T with(HandlerInstantiator hi) {
/* 482 */     return _withBase(this._base.withHandlerInstantiator(hi));
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
/*     */   public final T with(Base64Variant base64) {
/* 496 */     return _withBase(this._base.with(base64));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T with(DateFormat df) {
/* 507 */     return _withBase(this._base.withDateFormat(df));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T with(Locale l) {
/* 515 */     return _withBase(this._base.with(l));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T with(TimeZone tz) {
/* 523 */     return _withBase(this._base.with(tz));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public T withRootName(String rootName) {
/* 545 */     if (rootName == null) {
/* 546 */       return withRootName((PropertyName)null);
/*     */     }
/* 548 */     return withRootName(PropertyName.construct(rootName));
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
/*     */   public final SubtypeResolver getSubtypeResolver() {
/* 580 */     return this._subtypeResolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final String getRootName() {
/* 588 */     return (this._rootName == null) ? null : this._rootName.getSimpleName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final PropertyName getFullRootName() {
/* 595 */     return this._rootName;
/*     */   }
/*     */ 
/*     */   
/*     */   public final Class<?> getActiveView() {
/* 600 */     return this._view;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ContextAttributes getAttributes() {
/* 605 */     return this._attributes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ConfigOverride getConfigOverride(Class<?> type) {
/* 616 */     ConfigOverride override = this._configOverrides.findOverride(type);
/* 617 */     return (override == null) ? EMPTY_OVERRIDE : override;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ConfigOverride findConfigOverride(Class<?> type) {
/* 622 */     return this._configOverrides.findOverride(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public final JsonInclude.Value getDefaultPropertyInclusion() {
/* 627 */     return this._configOverrides.getDefaultInclusion();
/*     */   }
/*     */ 
/*     */   
/*     */   public final JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType) {
/* 632 */     JsonInclude.Value v = getConfigOverride(baseType).getInclude();
/* 633 */     JsonInclude.Value def = getDefaultPropertyInclusion();
/* 634 */     if (def == null) {
/* 635 */       return v;
/*     */     }
/* 637 */     return def.withOverrides(v);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final JsonInclude.Value getDefaultInclusion(Class<?> baseType, Class<?> propertyType) {
/* 643 */     JsonInclude.Value v = getConfigOverride(propertyType).getIncludeAsProperty();
/* 644 */     JsonInclude.Value def = getDefaultPropertyInclusion(baseType);
/* 645 */     if (def == null) {
/* 646 */       return v;
/*     */     }
/* 648 */     return def.withOverrides(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final JsonFormat.Value getDefaultPropertyFormat(Class<?> type) {
/* 653 */     return this._configOverrides.findFormatDefaults(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> type) {
/* 658 */     ConfigOverride overrides = this._configOverrides.findOverride(type);
/* 659 */     if (overrides != null) {
/* 660 */       JsonIgnoreProperties.Value v = overrides.getIgnorals();
/* 661 */       if (v != null) {
/* 662 */         return v;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 667 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> baseType, AnnotatedClass actualClass) {
/* 674 */     AnnotationIntrospector intr = getAnnotationIntrospector();
/*     */     
/* 676 */     JsonIgnoreProperties.Value base = (intr == null) ? null : intr.findPropertyIgnoralByName(this, (Annotated)actualClass);
/* 677 */     JsonIgnoreProperties.Value overrides = getDefaultPropertyIgnorals(baseType);
/* 678 */     return JsonIgnoreProperties.Value.merge(base, overrides);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final JsonIncludeProperties.Value getDefaultPropertyInclusions(Class<?> baseType, AnnotatedClass actualClass) {
/* 685 */     AnnotationIntrospector intr = getAnnotationIntrospector();
/* 686 */     return (intr == null) ? null : intr.findPropertyInclusionByName(this, (Annotated)actualClass);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final VisibilityChecker<?> getDefaultVisibilityChecker() {
/* 692 */     VisibilityChecker<?> vchecker = this._configOverrides.getDefaultVisibility();
/*     */ 
/*     */     
/* 695 */     if ((this._mapperFeatures & AUTO_DETECT_MASK) != AUTO_DETECT_MASK) {
/* 696 */       if (!isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
/* 697 */         vchecker = vchecker.withFieldVisibility(JsonAutoDetect.Visibility.NONE);
/*     */       }
/* 699 */       if (!isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
/* 700 */         vchecker = vchecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE);
/*     */       }
/* 702 */       if (!isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS)) {
/* 703 */         vchecker = vchecker.withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
/*     */       }
/* 705 */       if (!isEnabled(MapperFeature.AUTO_DETECT_SETTERS)) {
/* 706 */         vchecker = vchecker.withSetterVisibility(JsonAutoDetect.Visibility.NONE);
/*     */       }
/* 708 */       if (!isEnabled(MapperFeature.AUTO_DETECT_CREATORS)) {
/* 709 */         vchecker = vchecker.withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
/*     */       }
/*     */     } 
/* 712 */     return vchecker;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final VisibilityChecker<?> getDefaultVisibilityChecker(Class<?> baseType, AnnotatedClass actualClass) {
/*     */     VisibilityChecker<?> vc;
/* 723 */     if (ClassUtil.isJDKClass(baseType)) {
/* 724 */       VisibilityChecker.Std std = VisibilityChecker.Std.allPublicInstance();
/*     */     } else {
/* 726 */       vc = getDefaultVisibilityChecker();
/*     */     } 
/* 728 */     AnnotationIntrospector intr = getAnnotationIntrospector();
/* 729 */     if (intr != null) {
/* 730 */       vc = intr.findAutoDetectVisibility(actualClass, vc);
/*     */     }
/* 732 */     ConfigOverride overrides = this._configOverrides.findOverride(baseType);
/* 733 */     if (overrides != null) {
/* 734 */       vc = vc.withOverrides(overrides.getVisibility());
/*     */     }
/* 736 */     return vc;
/*     */   }
/*     */ 
/*     */   
/*     */   public final JsonSetter.Value getDefaultSetterInfo() {
/* 741 */     return this._configOverrides.getDefaultSetterInfo();
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean getDefaultMergeable() {
/* 746 */     return this._configOverrides.getDefaultMergeable();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean getDefaultMergeable(Class<?> baseType) {
/* 752 */     ConfigOverride cfg = this._configOverrides.findOverride(baseType);
/* 753 */     if (cfg != null) {
/* 754 */       Boolean b = cfg.getMergeable();
/* 755 */       if (b != null) {
/* 756 */         return b;
/*     */       }
/*     */     } 
/* 759 */     return this._configOverrides.getDefaultMergeable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertyName findRootName(JavaType rootType) {
/* 770 */     if (this._rootName != null) {
/* 771 */       return this._rootName;
/*     */     }
/* 773 */     return this._rootNames.findRootName(rootType, this);
/*     */   }
/*     */ 
/*     */   
/*     */   public PropertyName findRootName(Class<?> rawRootType) {
/* 778 */     if (this._rootName != null) {
/* 779 */       return this._rootName;
/*     */     }
/* 781 */     return this._rootNames.findRootName(rawRootType, this);
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
/*     */   public final Class<?> findMixInClassFor(Class<?> cls) {
/* 796 */     return this._mixIns.findMixInClassFor(cls);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassIntrospector.MixInResolver copy() {
/* 802 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int mixInCount() {
/* 810 */     return this._mixIns.localSize();
/*     */   }
/*     */   
/*     */   protected abstract T _withBase(BaseSettings paramBaseSettings);
/*     */   
/*     */   protected abstract T _withMapperFeatures(long paramLong);
/*     */   
/*     */   public abstract T with(ContextAttributes paramContextAttributes);
/*     */   
/*     */   public abstract T withRootName(PropertyName paramPropertyName);
/*     */   
/*     */   public abstract T with(SubtypeResolver paramSubtypeResolver);
/*     */   
/*     */   public abstract T withView(Class<?> paramClass);
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/cfg/MapperConfigBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */