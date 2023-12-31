/*     */ package com.fasterxml.jackson.databind;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.core.FormatFeature;
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import com.fasterxml.jackson.core.json.JsonWriteFeature;
/*     */ import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*     */ import com.fasterxml.jackson.core.util.Instantiatable;
/*     */ import com.fasterxml.jackson.databind.cfg.BaseSettings;
/*     */ import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
/*     */ import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
/*     */ import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
/*     */ import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
/*     */ import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*     */ import com.fasterxml.jackson.databind.ser.FilterProvider;
/*     */ import com.fasterxml.jackson.databind.util.RootNameLookup;
/*     */ import java.io.Serializable;
/*     */ import java.text.DateFormat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SerializationConfig
/*     */   extends MapperConfigBase<SerializationFeature, SerializationConfig>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  38 */   protected static final PrettyPrinter DEFAULT_PRETTY_PRINTER = (PrettyPrinter)new DefaultPrettyPrinter();
/*     */ 
/*     */   
/*  41 */   private static final int SER_FEATURE_DEFAULTS = collectFeatureDefaults(SerializationFeature.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final FilterProvider _filterProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final PrettyPrinter _defaultPrettyPrinter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _serFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _generatorFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _generatorFeaturesToChange;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _formatWriteFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _formatWriteFeaturesToChange;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SerializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
/* 118 */     super(base, str, mixins, rootNames, configOverrides);
/* 119 */     this._serFeatures = SER_FEATURE_DEFAULTS;
/* 120 */     this._filterProvider = null;
/* 121 */     this._defaultPrettyPrinter = DEFAULT_PRETTY_PRINTER;
/* 122 */     this._generatorFeatures = 0;
/* 123 */     this._generatorFeaturesToChange = 0;
/* 124 */     this._formatWriteFeatures = 0;
/* 125 */     this._formatWriteFeaturesToChange = 0;
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
/*     */   protected SerializationConfig(SerializationConfig src, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
/* 137 */     super(src, str, mixins, rootNames, configOverrides);
/* 138 */     this._serFeatures = src._serFeatures;
/* 139 */     this._filterProvider = src._filterProvider;
/* 140 */     this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 141 */     this._generatorFeatures = src._generatorFeatures;
/* 142 */     this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 143 */     this._formatWriteFeatures = src._formatWriteFeatures;
/* 144 */     this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
/* 156 */     this(src, src._subtypeResolver, mixins, rootNames, configOverrides);
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
/*     */   private SerializationConfig(SerializationConfig src, SubtypeResolver str) {
/* 168 */     super(src, str);
/* 169 */     this._serFeatures = src._serFeatures;
/* 170 */     this._filterProvider = src._filterProvider;
/* 171 */     this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 172 */     this._generatorFeatures = src._generatorFeatures;
/* 173 */     this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 174 */     this._formatWriteFeatures = src._formatWriteFeatures;
/* 175 */     this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SerializationConfig(SerializationConfig src, long mapperFeatures, int serFeatures, int generatorFeatures, int generatorFeatureMask, int formatFeatures, int formatFeaturesMask) {
/* 183 */     super(src, mapperFeatures);
/* 184 */     this._serFeatures = serFeatures;
/* 185 */     this._filterProvider = src._filterProvider;
/* 186 */     this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 187 */     this._generatorFeatures = generatorFeatures;
/* 188 */     this._generatorFeaturesToChange = generatorFeatureMask;
/* 189 */     this._formatWriteFeatures = formatFeatures;
/* 190 */     this._formatWriteFeaturesToChange = formatFeaturesMask;
/*     */   }
/*     */ 
/*     */   
/*     */   private SerializationConfig(SerializationConfig src, BaseSettings base) {
/* 195 */     super(src, base);
/* 196 */     this._serFeatures = src._serFeatures;
/* 197 */     this._filterProvider = src._filterProvider;
/* 198 */     this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 199 */     this._generatorFeatures = src._generatorFeatures;
/* 200 */     this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 201 */     this._formatWriteFeatures = src._formatWriteFeatures;
/* 202 */     this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*     */   }
/*     */ 
/*     */   
/*     */   private SerializationConfig(SerializationConfig src, FilterProvider filters) {
/* 207 */     super(src);
/* 208 */     this._serFeatures = src._serFeatures;
/* 209 */     this._filterProvider = filters;
/* 210 */     this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 211 */     this._generatorFeatures = src._generatorFeatures;
/* 212 */     this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 213 */     this._formatWriteFeatures = src._formatWriteFeatures;
/* 214 */     this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*     */   }
/*     */ 
/*     */   
/*     */   private SerializationConfig(SerializationConfig src, Class<?> view) {
/* 219 */     super(src, view);
/* 220 */     this._serFeatures = src._serFeatures;
/* 221 */     this._filterProvider = src._filterProvider;
/* 222 */     this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 223 */     this._generatorFeatures = src._generatorFeatures;
/* 224 */     this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 225 */     this._formatWriteFeatures = src._formatWriteFeatures;
/* 226 */     this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*     */   }
/*     */ 
/*     */   
/*     */   private SerializationConfig(SerializationConfig src, PropertyName rootName) {
/* 231 */     super(src, rootName);
/* 232 */     this._serFeatures = src._serFeatures;
/* 233 */     this._filterProvider = src._filterProvider;
/* 234 */     this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 235 */     this._generatorFeatures = src._generatorFeatures;
/* 236 */     this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 237 */     this._formatWriteFeatures = src._formatWriteFeatures;
/* 238 */     this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SerializationConfig(SerializationConfig src, ContextAttributes attrs) {
/* 246 */     super(src, attrs);
/* 247 */     this._serFeatures = src._serFeatures;
/* 248 */     this._filterProvider = src._filterProvider;
/* 249 */     this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 250 */     this._generatorFeatures = src._generatorFeatures;
/* 251 */     this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 252 */     this._formatWriteFeatures = src._formatWriteFeatures;
/* 253 */     this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins) {
/* 261 */     super(src, mixins);
/* 262 */     this._serFeatures = src._serFeatures;
/* 263 */     this._filterProvider = src._filterProvider;
/* 264 */     this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 265 */     this._generatorFeatures = src._generatorFeatures;
/* 266 */     this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 267 */     this._formatWriteFeatures = src._formatWriteFeatures;
/* 268 */     this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SerializationConfig(SerializationConfig src, PrettyPrinter defaultPP) {
/* 276 */     super(src);
/* 277 */     this._serFeatures = src._serFeatures;
/* 278 */     this._filterProvider = src._filterProvider;
/* 279 */     this._defaultPrettyPrinter = defaultPP;
/* 280 */     this._generatorFeatures = src._generatorFeatures;
/* 281 */     this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 282 */     this._formatWriteFeatures = src._formatWriteFeatures;
/* 283 */     this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SerializationConfig _withBase(BaseSettings newBase) {
/* 294 */     return (this._base == newBase) ? this : new SerializationConfig(this, newBase);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final SerializationConfig _withMapperFeatures(long mapperFeatures) {
/* 299 */     return new SerializationConfig(this, mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SerializationConfig withRootName(PropertyName rootName) {
/* 306 */     if (rootName == null) {
/* 307 */       if (this._rootName == null) {
/* 308 */         return this;
/*     */       }
/* 310 */     } else if (rootName.equals(this._rootName)) {
/* 311 */       return this;
/*     */     } 
/* 313 */     return new SerializationConfig(this, rootName);
/*     */   }
/*     */ 
/*     */   
/*     */   public SerializationConfig with(SubtypeResolver str) {
/* 318 */     return (str == this._subtypeResolver) ? this : new SerializationConfig(this, str);
/*     */   }
/*     */ 
/*     */   
/*     */   public SerializationConfig withView(Class<?> view) {
/* 323 */     return (this._view == view) ? this : new SerializationConfig(this, view);
/*     */   }
/*     */ 
/*     */   
/*     */   public SerializationConfig with(ContextAttributes attrs) {
/* 328 */     return (attrs == this._attributes) ? this : new SerializationConfig(this, attrs);
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
/*     */   public SerializationConfig with(DateFormat df) {
/* 344 */     SerializationConfig cfg = (SerializationConfig)super.with(df);
/*     */     
/* 346 */     if (df == null) {
/* 347 */       return cfg.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
/*     */     }
/* 349 */     return cfg.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
/*     */   public SerializationConfig with(SerializationFeature feature) {
/* 364 */     int newSerFeatures = this._serFeatures | feature.getMask();
/* 365 */     return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig with(SerializationFeature first, SerializationFeature... features) {
/* 377 */     int newSerFeatures = this._serFeatures | first.getMask();
/* 378 */     for (SerializationFeature f : features) {
/* 379 */       newSerFeatures |= f.getMask();
/*     */     }
/* 381 */     return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig withFeatures(SerializationFeature... features) {
/* 393 */     int newSerFeatures = this._serFeatures;
/* 394 */     for (SerializationFeature f : features) {
/* 395 */       newSerFeatures |= f.getMask();
/*     */     }
/* 397 */     return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig without(SerializationFeature feature) {
/* 409 */     int newSerFeatures = this._serFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/* 410 */     return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig without(SerializationFeature first, SerializationFeature... features) {
/* 422 */     int newSerFeatures = this._serFeatures & (first.getMask() ^ 0xFFFFFFFF);
/* 423 */     for (SerializationFeature f : features) {
/* 424 */       newSerFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*     */     }
/* 426 */     return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig withoutFeatures(SerializationFeature... features) {
/* 438 */     int newSerFeatures = this._serFeatures;
/* 439 */     for (SerializationFeature f : features) {
/* 440 */       newSerFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*     */     }
/* 442 */     return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig with(JsonGenerator.Feature feature) {
/* 461 */     int newSet = this._generatorFeatures | feature.getMask();
/* 462 */     int newMask = this._generatorFeaturesToChange | feature.getMask();
/* 463 */     return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig withFeatures(JsonGenerator.Feature... features) {
/* 477 */     int newSet = this._generatorFeatures;
/* 478 */     int newMask = this._generatorFeaturesToChange;
/* 479 */     for (JsonGenerator.Feature f : features) {
/* 480 */       int mask = f.getMask();
/* 481 */       newSet |= mask;
/* 482 */       newMask |= mask;
/*     */     } 
/* 484 */     return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig without(JsonGenerator.Feature feature) {
/* 498 */     int newSet = this._generatorFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/* 499 */     int newMask = this._generatorFeaturesToChange | feature.getMask();
/* 500 */     return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig withoutFeatures(JsonGenerator.Feature... features) {
/* 514 */     int newSet = this._generatorFeatures;
/* 515 */     int newMask = this._generatorFeaturesToChange;
/* 516 */     for (JsonGenerator.Feature f : features) {
/* 517 */       int mask = f.getMask();
/* 518 */       newSet &= mask ^ 0xFFFFFFFF;
/* 519 */       newMask |= mask;
/*     */     } 
/* 521 */     return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
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
/*     */   public SerializationConfig with(FormatFeature feature) {
/* 541 */     if (feature instanceof JsonWriteFeature) {
/* 542 */       return _withJsonWriteFeatures(new FormatFeature[] { feature });
/*     */     }
/* 544 */     int newSet = this._formatWriteFeatures | feature.getMask();
/* 545 */     int newMask = this._formatWriteFeaturesToChange | feature.getMask();
/* 546 */     return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
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
/*     */   public SerializationConfig withFeatures(FormatFeature... features) {
/* 561 */     if (features.length > 0 && features[0] instanceof JsonWriteFeature) {
/* 562 */       return _withJsonWriteFeatures(features);
/*     */     }
/* 564 */     int newSet = this._formatWriteFeatures;
/* 565 */     int newMask = this._formatWriteFeaturesToChange;
/* 566 */     for (FormatFeature f : features) {
/* 567 */       int mask = f.getMask();
/* 568 */       newSet |= mask;
/* 569 */       newMask |= mask;
/*     */     } 
/* 571 */     return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
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
/*     */   public SerializationConfig without(FormatFeature feature) {
/* 586 */     if (feature instanceof JsonWriteFeature) {
/* 587 */       return _withoutJsonWriteFeatures(new FormatFeature[] { feature });
/*     */     }
/* 589 */     int newSet = this._formatWriteFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/* 590 */     int newMask = this._formatWriteFeaturesToChange | feature.getMask();
/* 591 */     return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
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
/*     */   public SerializationConfig withoutFeatures(FormatFeature... features) {
/* 605 */     if (features.length > 0 && features[0] instanceof JsonWriteFeature) {
/* 606 */       return _withoutJsonWriteFeatures(features);
/*     */     }
/* 608 */     int newSet = this._formatWriteFeatures;
/* 609 */     int newMask = this._formatWriteFeaturesToChange;
/* 610 */     for (FormatFeature f : features) {
/* 611 */       int mask = f.getMask();
/* 612 */       newSet &= mask ^ 0xFFFFFFFF;
/* 613 */       newMask |= mask;
/*     */     } 
/* 615 */     return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SerializationConfig _withJsonWriteFeatures(FormatFeature... features) {
/* 623 */     int parserSet = this._generatorFeatures;
/* 624 */     int parserMask = this._generatorFeaturesToChange;
/* 625 */     int newSet = this._formatWriteFeatures;
/* 626 */     int newMask = this._formatWriteFeaturesToChange;
/* 627 */     for (FormatFeature f : features) {
/* 628 */       int mask = f.getMask();
/* 629 */       newSet |= mask;
/* 630 */       newMask |= mask;
/*     */       
/* 632 */       if (f instanceof JsonWriteFeature) {
/* 633 */         JsonGenerator.Feature oldF = ((JsonWriteFeature)f).mappedFeature();
/* 634 */         if (oldF != null) {
/* 635 */           int pmask = oldF.getMask();
/* 636 */           parserSet |= pmask;
/* 637 */           parserMask |= pmask;
/*     */         } 
/*     */       } 
/*     */     } 
/* 641 */     return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask && this._generatorFeatures == parserSet && this._generatorFeaturesToChange == parserMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, parserSet, parserMask, newSet, newMask);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SerializationConfig _withoutJsonWriteFeatures(FormatFeature... features) {
/* 650 */     int parserSet = this._generatorFeatures;
/* 651 */     int parserMask = this._generatorFeaturesToChange;
/* 652 */     int newSet = this._formatWriteFeatures;
/* 653 */     int newMask = this._formatWriteFeaturesToChange;
/* 654 */     for (FormatFeature f : features) {
/* 655 */       int mask = f.getMask();
/* 656 */       newSet &= mask ^ 0xFFFFFFFF;
/* 657 */       newMask |= mask;
/*     */       
/* 659 */       if (f instanceof JsonWriteFeature) {
/* 660 */         JsonGenerator.Feature oldF = ((JsonWriteFeature)f).mappedFeature();
/* 661 */         if (oldF != null) {
/* 662 */           int pmask = oldF.getMask();
/* 663 */           parserSet &= pmask ^ 0xFFFFFFFF;
/* 664 */           parserMask |= pmask;
/*     */         } 
/*     */       } 
/*     */     } 
/* 668 */     return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask && this._generatorFeatures == parserSet && this._generatorFeaturesToChange == parserMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, parserSet, parserMask, newSet, newMask);
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
/*     */   public SerializationConfig withFilters(FilterProvider filterProvider) {
/* 682 */     return (filterProvider == this._filterProvider) ? this : new SerializationConfig(this, filterProvider);
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
/*     */   public SerializationConfig withPropertyInclusion(JsonInclude.Value incl) {
/* 695 */     this._configOverrides.setDefaultInclusion(incl);
/* 696 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SerializationConfig withDefaultPrettyPrinter(PrettyPrinter pp) {
/* 703 */     return (this._defaultPrettyPrinter == pp) ? this : new SerializationConfig(this, pp);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrettyPrinter constructDefaultPrettyPrinter() {
/* 713 */     PrettyPrinter pp = this._defaultPrettyPrinter;
/* 714 */     if (pp instanceof Instantiatable) {
/* 715 */       pp = (PrettyPrinter)((Instantiatable)pp).createInstance();
/*     */     }
/* 717 */     return pp;
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
/*     */   public void initialize(JsonGenerator g) {
/* 735 */     if (SerializationFeature.INDENT_OUTPUT.enabledIn(this._serFeatures))
/*     */     {
/* 737 */       if (g.getPrettyPrinter() == null) {
/* 738 */         PrettyPrinter pp = constructDefaultPrettyPrinter();
/* 739 */         if (pp != null) {
/* 740 */           g.setPrettyPrinter(pp);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 745 */     boolean useBigDec = SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._serFeatures);
/*     */     
/* 747 */     int mask = this._generatorFeaturesToChange;
/* 748 */     if (mask != 0 || useBigDec) {
/* 749 */       int newFlags = this._generatorFeatures;
/*     */       
/* 751 */       if (useBigDec) {
/* 752 */         int f = JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.getMask();
/* 753 */         newFlags |= f;
/* 754 */         mask |= f;
/*     */       } 
/* 756 */       g.overrideStdFeatures(newFlags, mask);
/*     */     } 
/* 758 */     if (this._formatWriteFeaturesToChange != 0) {
/* 759 */       g.overrideFormatFeatures(this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*     */     }
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
/*     */   @Deprecated
/*     */   public JsonInclude.Include getSerializationInclusion() {
/* 775 */     JsonInclude.Include incl = getDefaultPropertyInclusion().getValueInclusion();
/* 776 */     return (incl == JsonInclude.Include.USE_DEFAULTS) ? JsonInclude.Include.ALWAYS : incl;
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
/*     */   public boolean useRootWrapping() {
/* 788 */     if (this._rootName != null) {
/* 789 */       return !this._rootName.isEmpty();
/*     */     }
/* 791 */     return isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
/*     */   }
/*     */   
/*     */   public final boolean isEnabled(SerializationFeature f) {
/* 795 */     return ((this._serFeatures & f.getMask()) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEnabled(JsonGenerator.Feature f, JsonFactory factory) {
/* 806 */     int mask = f.getMask();
/* 807 */     if ((this._generatorFeaturesToChange & mask) != 0) {
/* 808 */       return ((this._generatorFeatures & f.getMask()) != 0);
/*     */     }
/* 810 */     return factory.isEnabled(f);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean hasSerializationFeatures(int featureMask) {
/* 820 */     return ((this._serFeatures & featureMask) == featureMask);
/*     */   }
/*     */   
/*     */   public final int getSerializationFeatures() {
/* 824 */     return this._serFeatures;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FilterProvider getFilterProvider() {
/* 834 */     return this._filterProvider;
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
/*     */   public PrettyPrinter getDefaultPrettyPrinter() {
/* 848 */     return this._defaultPrettyPrinter;
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
/*     */   public BeanDescription introspect(JavaType type) {
/* 862 */     return getClassIntrospector().forSerialization(this, type, (ClassIntrospector.MixInResolver)this);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/SerializationConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */