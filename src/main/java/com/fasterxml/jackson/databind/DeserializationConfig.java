/*      */ package com.fasterxml.jackson.databind;
/*      */ 
/*      */ import com.fasterxml.jackson.core.FormatFeature;
/*      */ import com.fasterxml.jackson.core.FormatSchema;
/*      */ import com.fasterxml.jackson.core.JsonFactory;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.json.JsonReadFeature;
/*      */ import com.fasterxml.jackson.databind.cfg.BaseSettings;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionConfigs;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
/*      */ import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
/*      */ import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
/*      */ import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
/*      */ import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*      */ import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
/*      */ import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
/*      */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*      */ import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*      */ import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*      */ import com.fasterxml.jackson.databind.type.LogicalType;
/*      */ import com.fasterxml.jackson.databind.util.LinkedNode;
/*      */ import com.fasterxml.jackson.databind.util.RootNameLookup;
/*      */ import java.io.Serializable;
/*      */ import java.util.Collection;
/*      */ 
/*      */ public final class DeserializationConfig extends MapperConfigBase<DeserializationFeature, DeserializationConfig> implements Serializable {
/*   33 */   private static final int DESER_FEATURE_DEFAULTS = collectFeatureDefaults(DeserializationFeature.class);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final long serialVersionUID = 2L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final LinkedNode<DeserializationProblemHandler> _problemHandlers;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonNodeFactory _nodeFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final CoercionConfigs _coercionConfigs;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final ConstructorDetector _ctorDetector;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _deserFeatures;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _parserFeatures;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _parserFeaturesToChange;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _formatReadFeatures;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _formatReadFeaturesToChange;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides, CoercionConfigs coercionConfigs) {
/*  119 */     super(base, str, mixins, rootNames, configOverrides);
/*  120 */     this._deserFeatures = DESER_FEATURE_DEFAULTS;
/*  121 */     this._problemHandlers = null;
/*  122 */     this._nodeFactory = JsonNodeFactory.instance;
/*  123 */     this._ctorDetector = null;
/*  124 */     this._coercionConfigs = coercionConfigs;
/*  125 */     this._parserFeatures = 0;
/*  126 */     this._parserFeaturesToChange = 0;
/*  127 */     this._formatReadFeatures = 0;
/*  128 */     this._formatReadFeaturesToChange = 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DeserializationConfig(DeserializationConfig src, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides, CoercionConfigs coercionConfigs) {
/*  141 */     super(src, str, mixins, rootNames, configOverrides);
/*  142 */     this._deserFeatures = src._deserFeatures;
/*  143 */     this._problemHandlers = src._problemHandlers;
/*  144 */     this._nodeFactory = src._nodeFactory;
/*  145 */     this._ctorDetector = src._ctorDetector;
/*  146 */     this._coercionConfigs = coercionConfigs;
/*  147 */     this._parserFeatures = src._parserFeatures;
/*  148 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  149 */     this._formatReadFeatures = src._formatReadFeatures;
/*  150 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public DeserializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
/*  157 */     this(base, str, mixins, rootNames, configOverrides, new CoercionConfigs());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected DeserializationConfig(DeserializationConfig src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
/*  165 */     this(src, src._subtypeResolver, mixins, rootNames, configOverrides, new CoercionConfigs());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private DeserializationConfig(DeserializationConfig src, long mapperFeatures, int deserFeatures, int parserFeatures, int parserFeatureMask, int formatFeatures, int formatFeatureMask) {
/*  181 */     super(src, mapperFeatures);
/*  182 */     this._deserFeatures = deserFeatures;
/*  183 */     this._problemHandlers = src._problemHandlers;
/*  184 */     this._nodeFactory = src._nodeFactory;
/*  185 */     this._coercionConfigs = src._coercionConfigs;
/*  186 */     this._ctorDetector = src._ctorDetector;
/*  187 */     this._parserFeatures = parserFeatures;
/*  188 */     this._parserFeaturesToChange = parserFeatureMask;
/*  189 */     this._formatReadFeatures = formatFeatures;
/*  190 */     this._formatReadFeaturesToChange = formatFeatureMask;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private DeserializationConfig(DeserializationConfig src, SubtypeResolver str) {
/*  199 */     super(src, str);
/*  200 */     this._deserFeatures = src._deserFeatures;
/*  201 */     this._problemHandlers = src._problemHandlers;
/*  202 */     this._nodeFactory = src._nodeFactory;
/*  203 */     this._coercionConfigs = src._coercionConfigs;
/*  204 */     this._ctorDetector = src._ctorDetector;
/*  205 */     this._parserFeatures = src._parserFeatures;
/*  206 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  207 */     this._formatReadFeatures = src._formatReadFeatures;
/*  208 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */ 
/*      */   
/*      */   private DeserializationConfig(DeserializationConfig src, BaseSettings base) {
/*  213 */     super(src, base);
/*  214 */     this._deserFeatures = src._deserFeatures;
/*  215 */     this._problemHandlers = src._problemHandlers;
/*  216 */     this._nodeFactory = src._nodeFactory;
/*  217 */     this._coercionConfigs = src._coercionConfigs;
/*  218 */     this._ctorDetector = src._ctorDetector;
/*  219 */     this._parserFeatures = src._parserFeatures;
/*  220 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  221 */     this._formatReadFeatures = src._formatReadFeatures;
/*  222 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */ 
/*      */   
/*      */   private DeserializationConfig(DeserializationConfig src, JsonNodeFactory f) {
/*  227 */     super(src);
/*  228 */     this._deserFeatures = src._deserFeatures;
/*  229 */     this._problemHandlers = src._problemHandlers;
/*  230 */     this._nodeFactory = f;
/*  231 */     this._coercionConfigs = src._coercionConfigs;
/*  232 */     this._ctorDetector = src._ctorDetector;
/*  233 */     this._parserFeatures = src._parserFeatures;
/*  234 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  235 */     this._formatReadFeatures = src._formatReadFeatures;
/*  236 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private DeserializationConfig(DeserializationConfig src, ConstructorDetector ctorDetector) {
/*  242 */     super(src);
/*  243 */     this._deserFeatures = src._deserFeatures;
/*  244 */     this._problemHandlers = src._problemHandlers;
/*  245 */     this._nodeFactory = src._nodeFactory;
/*  246 */     this._coercionConfigs = src._coercionConfigs;
/*  247 */     this._ctorDetector = ctorDetector;
/*  248 */     this._parserFeatures = src._parserFeatures;
/*  249 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  250 */     this._formatReadFeatures = src._formatReadFeatures;
/*  251 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private DeserializationConfig(DeserializationConfig src, LinkedNode<DeserializationProblemHandler> problemHandlers) {
/*  257 */     super(src);
/*  258 */     this._deserFeatures = src._deserFeatures;
/*  259 */     this._problemHandlers = problemHandlers;
/*  260 */     this._nodeFactory = src._nodeFactory;
/*  261 */     this._coercionConfigs = src._coercionConfigs;
/*  262 */     this._ctorDetector = src._ctorDetector;
/*  263 */     this._parserFeatures = src._parserFeatures;
/*  264 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  265 */     this._formatReadFeatures = src._formatReadFeatures;
/*  266 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */ 
/*      */   
/*      */   private DeserializationConfig(DeserializationConfig src, PropertyName rootName) {
/*  271 */     super(src, rootName);
/*  272 */     this._deserFeatures = src._deserFeatures;
/*  273 */     this._problemHandlers = src._problemHandlers;
/*  274 */     this._nodeFactory = src._nodeFactory;
/*  275 */     this._coercionConfigs = src._coercionConfigs;
/*  276 */     this._ctorDetector = src._ctorDetector;
/*  277 */     this._parserFeatures = src._parserFeatures;
/*  278 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  279 */     this._formatReadFeatures = src._formatReadFeatures;
/*  280 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */ 
/*      */   
/*      */   private DeserializationConfig(DeserializationConfig src, Class<?> view) {
/*  285 */     super(src, view);
/*  286 */     this._deserFeatures = src._deserFeatures;
/*  287 */     this._problemHandlers = src._problemHandlers;
/*  288 */     this._nodeFactory = src._nodeFactory;
/*  289 */     this._coercionConfigs = src._coercionConfigs;
/*  290 */     this._ctorDetector = src._ctorDetector;
/*  291 */     this._parserFeatures = src._parserFeatures;
/*  292 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  293 */     this._formatReadFeatures = src._formatReadFeatures;
/*  294 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */ 
/*      */   
/*      */   protected DeserializationConfig(DeserializationConfig src, ContextAttributes attrs) {
/*  299 */     super(src, attrs);
/*  300 */     this._deserFeatures = src._deserFeatures;
/*  301 */     this._problemHandlers = src._problemHandlers;
/*  302 */     this._nodeFactory = src._nodeFactory;
/*  303 */     this._coercionConfigs = src._coercionConfigs;
/*  304 */     this._ctorDetector = src._ctorDetector;
/*  305 */     this._parserFeatures = src._parserFeatures;
/*  306 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  307 */     this._formatReadFeatures = src._formatReadFeatures;
/*  308 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */ 
/*      */   
/*      */   protected DeserializationConfig(DeserializationConfig src, SimpleMixInResolver mixins) {
/*  313 */     super(src, mixins);
/*  314 */     this._deserFeatures = src._deserFeatures;
/*  315 */     this._problemHandlers = src._problemHandlers;
/*  316 */     this._nodeFactory = src._nodeFactory;
/*  317 */     this._coercionConfigs = src._coercionConfigs;
/*  318 */     this._ctorDetector = src._ctorDetector;
/*  319 */     this._parserFeatures = src._parserFeatures;
/*  320 */     this._parserFeaturesToChange = src._parserFeaturesToChange;
/*  321 */     this._formatReadFeatures = src._formatReadFeatures;
/*  322 */     this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*      */   }
/*      */   
/*      */   protected BaseSettings getBaseSettings() {
/*  326 */     return this._base;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final DeserializationConfig _withBase(BaseSettings newBase) {
/*  336 */     return (this._base == newBase) ? this : new DeserializationConfig(this, newBase);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final DeserializationConfig _withMapperFeatures(long mapperFeatures) {
/*  341 */     return new DeserializationConfig(this, mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig with(SubtypeResolver str) {
/*  354 */     return (this._subtypeResolver == str) ? this : new DeserializationConfig(this, str);
/*      */   }
/*      */ 
/*      */   
/*      */   public DeserializationConfig withRootName(PropertyName rootName) {
/*  359 */     if (rootName == null) {
/*  360 */       if (this._rootName == null) {
/*  361 */         return this;
/*      */       }
/*  363 */     } else if (rootName.equals(this._rootName)) {
/*  364 */       return this;
/*      */     } 
/*  366 */     return new DeserializationConfig(this, rootName);
/*      */   }
/*      */ 
/*      */   
/*      */   public DeserializationConfig withView(Class<?> view) {
/*  371 */     return (this._view == view) ? this : new DeserializationConfig(this, view);
/*      */   }
/*      */ 
/*      */   
/*      */   public DeserializationConfig with(ContextAttributes attrs) {
/*  376 */     return (attrs == this._attributes) ? this : new DeserializationConfig(this, attrs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig with(DeserializationFeature feature) {
/*  391 */     int newDeserFeatures = this._deserFeatures | feature.getMask();
/*  392 */     return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig with(DeserializationFeature first, DeserializationFeature... features) {
/*  405 */     int newDeserFeatures = this._deserFeatures | first.getMask();
/*  406 */     for (DeserializationFeature f : features) {
/*  407 */       newDeserFeatures |= f.getMask();
/*      */     }
/*  409 */     return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig withFeatures(DeserializationFeature... features) {
/*  421 */     int newDeserFeatures = this._deserFeatures;
/*  422 */     for (DeserializationFeature f : features) {
/*  423 */       newDeserFeatures |= f.getMask();
/*      */     }
/*  425 */     return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig without(DeserializationFeature feature) {
/*  437 */     int newDeserFeatures = this._deserFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/*  438 */     return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig without(DeserializationFeature first, DeserializationFeature... features) {
/*  451 */     int newDeserFeatures = this._deserFeatures & (first.getMask() ^ 0xFFFFFFFF);
/*  452 */     for (DeserializationFeature f : features) {
/*  453 */       newDeserFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*      */     }
/*  455 */     return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig withoutFeatures(DeserializationFeature... features) {
/*  467 */     int newDeserFeatures = this._deserFeatures;
/*  468 */     for (DeserializationFeature f : features) {
/*  469 */       newDeserFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*      */     }
/*  471 */     return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig with(JsonParser.Feature feature) {
/*  491 */     int newSet = this._parserFeatures | feature.getMask();
/*  492 */     int newMask = this._parserFeaturesToChange | feature.getMask();
/*  493 */     return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig withFeatures(JsonParser.Feature... features) {
/*  507 */     int newSet = this._parserFeatures;
/*  508 */     int newMask = this._parserFeaturesToChange;
/*  509 */     for (JsonParser.Feature f : features) {
/*  510 */       int mask = f.getMask();
/*  511 */       newSet |= mask;
/*  512 */       newMask |= mask;
/*      */     } 
/*  514 */     return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig without(JsonParser.Feature feature) {
/*  528 */     int newSet = this._parserFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/*  529 */     int newMask = this._parserFeaturesToChange | feature.getMask();
/*  530 */     return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig withoutFeatures(JsonParser.Feature... features) {
/*  544 */     int newSet = this._parserFeatures;
/*  545 */     int newMask = this._parserFeaturesToChange;
/*  546 */     for (JsonParser.Feature f : features) {
/*  547 */       int mask = f.getMask();
/*  548 */       newSet &= mask ^ 0xFFFFFFFF;
/*  549 */       newMask |= mask;
/*      */     } 
/*  551 */     return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig with(FormatFeature feature) {
/*  572 */     if (feature instanceof JsonReadFeature) {
/*  573 */       return _withJsonReadFeatures(new FormatFeature[] { feature });
/*      */     }
/*  575 */     int newSet = this._formatReadFeatures | feature.getMask();
/*  576 */     int newMask = this._formatReadFeaturesToChange | feature.getMask();
/*  577 */     return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig withFeatures(FormatFeature... features) {
/*  592 */     if (features.length > 0 && features[0] instanceof JsonReadFeature) {
/*  593 */       return _withJsonReadFeatures(features);
/*      */     }
/*  595 */     int newSet = this._formatReadFeatures;
/*  596 */     int newMask = this._formatReadFeaturesToChange;
/*  597 */     for (FormatFeature f : features) {
/*  598 */       int mask = f.getMask();
/*  599 */       newSet |= mask;
/*  600 */       newMask |= mask;
/*      */     } 
/*  602 */     return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig without(FormatFeature feature) {
/*  617 */     if (feature instanceof JsonReadFeature) {
/*  618 */       return _withoutJsonReadFeatures(new FormatFeature[] { feature });
/*      */     }
/*  620 */     int newSet = this._formatReadFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/*  621 */     int newMask = this._formatReadFeaturesToChange | feature.getMask();
/*  622 */     return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig withoutFeatures(FormatFeature... features) {
/*  637 */     if (features.length > 0 && features[0] instanceof JsonReadFeature) {
/*  638 */       return _withoutJsonReadFeatures(features);
/*      */     }
/*  640 */     int newSet = this._formatReadFeatures;
/*  641 */     int newMask = this._formatReadFeaturesToChange;
/*  642 */     for (FormatFeature f : features) {
/*  643 */       int mask = f.getMask();
/*  644 */       newSet &= mask ^ 0xFFFFFFFF;
/*  645 */       newMask |= mask;
/*      */     } 
/*  647 */     return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private DeserializationConfig _withJsonReadFeatures(FormatFeature... features) {
/*  655 */     int parserSet = this._parserFeatures;
/*  656 */     int parserMask = this._parserFeaturesToChange;
/*  657 */     int newSet = this._formatReadFeatures;
/*  658 */     int newMask = this._formatReadFeaturesToChange;
/*  659 */     for (FormatFeature f : features) {
/*  660 */       int mask = f.getMask();
/*  661 */       newSet |= mask;
/*  662 */       newMask |= mask;
/*      */       
/*  664 */       if (f instanceof JsonReadFeature) {
/*  665 */         JsonParser.Feature oldF = ((JsonReadFeature)f).mappedFeature();
/*  666 */         if (oldF != null) {
/*  667 */           int pmask = oldF.getMask();
/*  668 */           parserSet |= pmask;
/*  669 */           parserMask |= pmask;
/*      */         } 
/*      */       } 
/*      */     } 
/*  673 */     return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask && this._parserFeatures == parserSet && this._parserFeaturesToChange == parserMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, parserSet, parserMask, newSet, newMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private DeserializationConfig _withoutJsonReadFeatures(FormatFeature... features) {
/*  682 */     int parserSet = this._parserFeatures;
/*  683 */     int parserMask = this._parserFeaturesToChange;
/*  684 */     int newSet = this._formatReadFeatures;
/*  685 */     int newMask = this._formatReadFeaturesToChange;
/*  686 */     for (FormatFeature f : features) {
/*  687 */       int mask = f.getMask();
/*  688 */       newSet &= mask ^ 0xFFFFFFFF;
/*  689 */       newMask |= mask;
/*      */       
/*  691 */       if (f instanceof JsonReadFeature) {
/*  692 */         JsonParser.Feature oldF = ((JsonReadFeature)f).mappedFeature();
/*  693 */         if (oldF != null) {
/*  694 */           int pmask = oldF.getMask();
/*  695 */           parserSet &= pmask ^ 0xFFFFFFFF;
/*  696 */           parserMask |= pmask;
/*      */         } 
/*      */       } 
/*      */     } 
/*  700 */     return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask && this._parserFeatures == parserSet && this._parserFeaturesToChange == parserMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, parserSet, parserMask, newSet, newMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig with(JsonNodeFactory f) {
/*  718 */     if (this._nodeFactory == f) {
/*  719 */       return this;
/*      */     }
/*  721 */     return new DeserializationConfig(this, f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig with(ConstructorDetector ctorDetector) {
/*  728 */     if (this._ctorDetector == ctorDetector) {
/*  729 */       return this;
/*      */     }
/*  731 */     return new DeserializationConfig(this, ctorDetector);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig withHandler(DeserializationProblemHandler h) {
/*  741 */     if (LinkedNode.contains(this._problemHandlers, h)) {
/*  742 */       return this;
/*      */     }
/*  744 */     return new DeserializationConfig(this, new LinkedNode(h, this._problemHandlers));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig withNoProblemHandlers() {
/*  753 */     if (this._problemHandlers == null) {
/*  754 */       return this;
/*      */     }
/*  756 */     return new DeserializationConfig(this, (LinkedNode<DeserializationProblemHandler>)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser initialize(JsonParser p) {
/*  774 */     if (this._parserFeaturesToChange != 0) {
/*  775 */       p.overrideStdFeatures(this._parserFeatures, this._parserFeaturesToChange);
/*      */     }
/*  777 */     if (this._formatReadFeaturesToChange != 0) {
/*  778 */       p.overrideFormatFeatures(this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */     }
/*  780 */     return p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser initialize(JsonParser p, FormatSchema schema) {
/*  787 */     if (this._parserFeaturesToChange != 0) {
/*  788 */       p.overrideStdFeatures(this._parserFeatures, this._parserFeaturesToChange);
/*      */     }
/*  790 */     if (this._formatReadFeaturesToChange != 0) {
/*  791 */       p.overrideFormatFeatures(this._formatReadFeatures, this._formatReadFeaturesToChange);
/*      */     }
/*  793 */     if (schema != null) {
/*  794 */       p.setSchema(schema);
/*      */     }
/*  796 */     return p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean useRootWrapping() {
/*  808 */     if (this._rootName != null) {
/*  809 */       return !this._rootName.isEmpty();
/*      */     }
/*  811 */     return isEnabled(DeserializationFeature.UNWRAP_ROOT_VALUE);
/*      */   }
/*      */   
/*      */   public final boolean isEnabled(DeserializationFeature f) {
/*  815 */     return ((this._deserFeatures & f.getMask()) != 0);
/*      */   }
/*      */   
/*      */   public final boolean isEnabled(JsonParser.Feature f, JsonFactory factory) {
/*  819 */     int mask = f.getMask();
/*  820 */     if ((this._parserFeaturesToChange & mask) != 0) {
/*  821 */       return ((this._parserFeatures & f.getMask()) != 0);
/*      */     }
/*  823 */     return factory.isEnabled(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean hasDeserializationFeatures(int featureMask) {
/*  833 */     return ((this._deserFeatures & featureMask) == featureMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean hasSomeOfFeatures(int featureMask) {
/*  843 */     return ((this._deserFeatures & featureMask) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getDeserializationFeatures() {
/*  851 */     return this._deserFeatures;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean requiresFullValue() {
/*  863 */     return DeserializationFeature.FAIL_ON_TRAILING_TOKENS.enabledIn(this._deserFeatures);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public LinkedNode<DeserializationProblemHandler> getProblemHandlers() {
/*  877 */     return this._problemHandlers;
/*      */   }
/*      */   
/*      */   public final JsonNodeFactory getNodeFactory() {
/*  881 */     return this._nodeFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ConstructorDetector getConstructorDetector() {
/*  888 */     if (this._ctorDetector == null) {
/*  889 */       return ConstructorDetector.DEFAULT;
/*      */     }
/*  891 */     return this._ctorDetector;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BeanDescription introspect(JavaType type) {
/*  907 */     return getClassIntrospector().forDeserialization(this, type, (ClassIntrospector.MixInResolver)this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BeanDescription introspectForCreation(JavaType type) {
/*  915 */     return getClassIntrospector().forCreation(this, type, (ClassIntrospector.MixInResolver)this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BeanDescription introspectForBuilder(JavaType builderType, BeanDescription valueTypeDesc) {
/*  922 */     return getClassIntrospector().forDeserializationWithBuilder(this, builderType, (ClassIntrospector.MixInResolver)this, valueTypeDesc);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public BeanDescription introspectForBuilder(JavaType type) {
/*  932 */     return getClassIntrospector().forDeserializationWithBuilder(this, type, (ClassIntrospector.MixInResolver)this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeDeserializer findTypeDeserializer(JavaType baseType) throws JsonMappingException {
/*  951 */     BeanDescription bean = introspectClassAnnotations(baseType.getRawClass());
/*  952 */     AnnotatedClass ac = bean.getClassInfo();
/*  953 */     TypeResolverBuilder<?> b = getAnnotationIntrospector().findTypeResolver((MapperConfig<?>)this, ac, baseType);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  958 */     Collection<NamedType> subtypes = null;
/*  959 */     if (b == null) {
/*  960 */       b = getDefaultTyper(baseType);
/*  961 */       if (b == null) {
/*  962 */         return null;
/*      */       }
/*      */     } else {
/*  965 */       subtypes = getSubtypeResolver().collectAndResolveSubtypesByTypeId((MapperConfig)this, ac);
/*      */     } 
/*  967 */     return b.buildTypeDeserializer(this, baseType, subtypes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CoercionAction findCoercionAction(LogicalType targetType, Class<?> targetClass, CoercionInputShape inputShape) {
/*  991 */     return this._coercionConfigs.findCoercion(this, targetType, targetClass, inputShape);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CoercionAction findCoercionFromBlankString(LogicalType targetType, Class<?> targetClass, CoercionAction actionIfBlankNotAllowed) {
/* 1015 */     return this._coercionConfigs.findCoercionFromBlankString(this, targetType, targetClass, actionIfBlankNotAllowed);
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/DeserializationConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */