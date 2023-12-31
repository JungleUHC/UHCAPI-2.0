/*     */ package com.fasterxml.jackson.databind.deser;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DatabindException;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.util.Annotations;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeanDeserializerBuilder
/*     */ {
/*     */   protected final DeserializationConfig _config;
/*     */   protected final DeserializationContext _context;
/*     */   protected final BeanDescription _beanDesc;
/*  57 */   protected final Map<String, SettableBeanProperty> _properties = new LinkedHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<ValueInjector> _injectables;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HashMap<String, SettableBeanProperty> _backRefProperties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HashSet<String> _ignorableProps;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HashSet<String> _includableProps;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ValueInstantiator _valueInstantiator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ObjectIdReader _objectIdReader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SettableAnyProperty _anySetter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _ignoreAllUnknown;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedMethod _buildMethod;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonPOJOBuilder.Value _builderConfig;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDeserializerBuilder(BeanDescription beanDesc, DeserializationContext ctxt) {
/* 126 */     this._beanDesc = beanDesc;
/* 127 */     this._context = ctxt;
/* 128 */     this._config = ctxt.getConfig();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanDeserializerBuilder(BeanDeserializerBuilder src) {
/* 137 */     this._beanDesc = src._beanDesc;
/* 138 */     this._context = src._context;
/* 139 */     this._config = src._config;
/*     */ 
/*     */     
/* 142 */     this._properties.putAll(src._properties);
/* 143 */     this._injectables = _copy(src._injectables);
/* 144 */     this._backRefProperties = _copy(src._backRefProperties);
/*     */     
/* 146 */     this._ignorableProps = src._ignorableProps;
/* 147 */     this._includableProps = src._includableProps;
/* 148 */     this._valueInstantiator = src._valueInstantiator;
/* 149 */     this._objectIdReader = src._objectIdReader;
/*     */     
/* 151 */     this._anySetter = src._anySetter;
/* 152 */     this._ignoreAllUnknown = src._ignoreAllUnknown;
/*     */     
/* 154 */     this._buildMethod = src._buildMethod;
/* 155 */     this._builderConfig = src._builderConfig;
/*     */   }
/*     */   
/*     */   private static HashMap<String, SettableBeanProperty> _copy(HashMap<String, SettableBeanProperty> src) {
/* 159 */     return (src == null) ? null : new HashMap<>(src);
/*     */   }
/*     */ 
/*     */   
/*     */   private static <T> List<T> _copy(List<T> src) {
/* 164 */     return (src == null) ? null : new ArrayList<>(src);
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
/*     */   public void addOrReplaceProperty(SettableBeanProperty prop, boolean allowOverride) {
/* 177 */     this._properties.put(prop.getName(), prop);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addProperty(SettableBeanProperty prop) {
/* 187 */     SettableBeanProperty old = this._properties.put(prop.getName(), prop);
/* 188 */     if (old != null && old != prop) {
/* 189 */       throw new IllegalArgumentException("Duplicate property '" + prop.getName() + "' for " + this._beanDesc.getType());
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
/*     */   public void addBackReferenceProperty(String referenceName, SettableBeanProperty prop) throws JsonMappingException {
/* 201 */     if (this._backRefProperties == null) {
/* 202 */       this._backRefProperties = new HashMap<>(4);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 207 */     if (this._config.canOverrideAccessModifiers()) {
/*     */       try {
/* 209 */         prop.fixAccess(this._config);
/* 210 */       } catch (IllegalArgumentException e) {
/* 211 */         _handleBadAccess(e);
/*     */       } 
/*     */     }
/* 214 */     this._backRefProperties.put(referenceName, prop);
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
/*     */   public void addInjectable(PropertyName propName, JavaType propType, Annotations contextAnnotations, AnnotatedMember member, Object valueId) throws JsonMappingException {
/* 231 */     if (this._injectables == null) {
/* 232 */       this._injectables = new ArrayList<>();
/*     */     }
/* 234 */     if (this._config.canOverrideAccessModifiers()) {
/*     */       try {
/* 236 */         member.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/* 237 */       } catch (IllegalArgumentException e) {
/* 238 */         _handleBadAccess(e);
/*     */       } 
/*     */     }
/* 241 */     this._injectables.add(new ValueInjector(propName, propType, member, valueId));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addIgnorable(String propName) {
/* 250 */     if (this._ignorableProps == null) {
/* 251 */       this._ignorableProps = new HashSet<>();
/*     */     }
/* 253 */     this._ignorableProps.add(propName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addIncludable(String propName) {
/* 263 */     if (this._includableProps == null) {
/* 264 */       this._includableProps = new HashSet<>();
/*     */     }
/* 266 */     this._includableProps.add(propName);
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
/*     */   public void addCreatorProperty(SettableBeanProperty prop) {
/* 281 */     addProperty(prop);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAnySetter(SettableAnyProperty s) {
/* 286 */     if (this._anySetter != null && s != null) {
/* 287 */       throw new IllegalStateException("_anySetter already set to non-null");
/*     */     }
/* 289 */     this._anySetter = s;
/*     */   }
/*     */   
/*     */   public void setIgnoreUnknownProperties(boolean ignore) {
/* 293 */     this._ignoreAllUnknown = ignore;
/*     */   }
/*     */   
/*     */   public void setValueInstantiator(ValueInstantiator inst) {
/* 297 */     this._valueInstantiator = inst;
/*     */   }
/*     */   
/*     */   public void setObjectIdReader(ObjectIdReader r) {
/* 301 */     this._objectIdReader = r;
/*     */   }
/*     */   
/*     */   public void setPOJOBuilder(AnnotatedMethod buildMethod, JsonPOJOBuilder.Value config) {
/* 305 */     this._buildMethod = buildMethod;
/* 306 */     this._builderConfig = config;
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
/*     */   public Iterator<SettableBeanProperty> getProperties() {
/* 324 */     return this._properties.values().iterator();
/*     */   }
/*     */   
/*     */   public SettableBeanProperty findProperty(PropertyName propertyName) {
/* 328 */     return this._properties.get(propertyName.getSimpleName());
/*     */   }
/*     */   
/*     */   public boolean hasProperty(PropertyName propertyName) {
/* 332 */     return (findProperty(propertyName) != null);
/*     */   }
/*     */   
/*     */   public SettableBeanProperty removeProperty(PropertyName name) {
/* 336 */     return this._properties.remove(name.getSimpleName());
/*     */   }
/*     */   
/*     */   public SettableAnyProperty getAnySetter() {
/* 340 */     return this._anySetter;
/*     */   }
/*     */   
/*     */   public ValueInstantiator getValueInstantiator() {
/* 344 */     return this._valueInstantiator;
/*     */   }
/*     */   
/*     */   public List<ValueInjector> getInjectables() {
/* 348 */     return this._injectables;
/*     */   }
/*     */   
/*     */   public ObjectIdReader getObjectIdReader() {
/* 352 */     return this._objectIdReader;
/*     */   }
/*     */   
/*     */   public AnnotatedMethod getBuildMethod() {
/* 356 */     return this._buildMethod;
/*     */   }
/*     */   
/*     */   public JsonPOJOBuilder.Value getBuilderConfig() {
/* 360 */     return this._builderConfig;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasIgnorable(String name) {
/* 367 */     return IgnorePropertiesUtil.shouldIgnore(name, this._ignorableProps, this._includableProps);
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
/*     */   public JsonDeserializer<?> build() throws JsonMappingException {
/* 383 */     Collection<SettableBeanProperty> props = this._properties.values();
/* 384 */     _fixAccess(props);
/* 385 */     BeanPropertyMap propertyMap = BeanPropertyMap.construct((MapperConfig)this._config, props, 
/* 386 */         _collectAliases(props), 
/* 387 */         _findCaseInsensitivity());
/* 388 */     propertyMap.assignIndexes();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 393 */     boolean anyViews = !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
/* 394 */     if (!anyViews) {
/* 395 */       for (SettableBeanProperty prop : props) {
/* 396 */         if (prop.hasViews()) {
/* 397 */           anyViews = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 404 */     if (this._objectIdReader != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 409 */       ObjectIdValueProperty prop = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
/* 410 */       propertyMap = propertyMap.withProperty((SettableBeanProperty)prop);
/*     */     } 
/*     */     
/* 413 */     return (JsonDeserializer<?>)new BeanDeserializer(this, this._beanDesc, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, this._includableProps, anyViews);
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
/*     */   public AbstractDeserializer buildAbstract() {
/* 426 */     return new AbstractDeserializer(this, this._beanDesc, this._backRefProperties, this._properties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> buildBuilderBased(JavaType valueType, String expBuildMethodName) throws JsonMappingException {
/* 437 */     if (this._buildMethod == null) {
/*     */       
/* 439 */       if (!expBuildMethodName.isEmpty()) {
/* 440 */         this._context.reportBadDefinition(this._beanDesc.getType(), 
/* 441 */             String.format("Builder class %s does not have build method (name: '%s')", new Object[] {
/* 442 */                 ClassUtil.getTypeDescription(this._beanDesc.getType()), expBuildMethodName
/*     */               }));
/*     */       }
/*     */     } else {
/*     */       
/* 447 */       Class<?> rawBuildType = this._buildMethod.getRawReturnType();
/* 448 */       Class<?> rawValueType = valueType.getRawClass();
/* 449 */       if (rawBuildType != rawValueType && 
/* 450 */         !rawBuildType.isAssignableFrom(rawValueType) && 
/* 451 */         !rawValueType.isAssignableFrom(rawBuildType)) {
/* 452 */         this._context.reportBadDefinition(this._beanDesc.getType(), 
/* 453 */             String.format("Build method `%s` has wrong return type (%s), not compatible with POJO type (%s)", new Object[] {
/* 454 */                 this._buildMethod.getFullName(), 
/* 455 */                 ClassUtil.getClassDescription(rawBuildType), 
/* 456 */                 ClassUtil.getTypeDescription(valueType)
/*     */               }));
/*     */       }
/*     */     } 
/* 460 */     Collection<SettableBeanProperty> props = this._properties.values();
/* 461 */     _fixAccess(props);
/* 462 */     BeanPropertyMap propertyMap = BeanPropertyMap.construct((MapperConfig)this._config, props, 
/* 463 */         _collectAliases(props), 
/* 464 */         _findCaseInsensitivity());
/* 465 */     propertyMap.assignIndexes();
/*     */     
/* 467 */     boolean anyViews = !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
/*     */     
/* 469 */     if (!anyViews) {
/* 470 */       for (SettableBeanProperty prop : props) {
/* 471 */         if (prop.hasViews()) {
/* 472 */           anyViews = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/* 478 */     if (this._objectIdReader != null) {
/*     */ 
/*     */       
/* 481 */       ObjectIdValueProperty prop = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
/*     */       
/* 483 */       propertyMap = propertyMap.withProperty((SettableBeanProperty)prop);
/*     */     } 
/*     */     
/* 486 */     return createBuilderBasedDeserializer(valueType, propertyMap, anyViews);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonDeserializer<?> createBuilderBasedDeserializer(JavaType valueType, BeanPropertyMap propertyMap, boolean anyViews) {
/* 496 */     return (JsonDeserializer<?>)new BuilderBasedDeserializer(this, this._beanDesc, valueType, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, this._includableProps, anyViews);
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
/*     */   protected void _fixAccess(Collection<SettableBeanProperty> mainProps) throws JsonMappingException {
/* 526 */     if (this._config.canOverrideAccessModifiers()) {
/* 527 */       for (SettableBeanProperty prop : mainProps) {
/*     */ 
/*     */         
/*     */         try {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 535 */           prop.fixAccess(this._config);
/* 536 */         } catch (IllegalArgumentException e) {
/* 537 */           _handleBadAccess(e);
/*     */         } 
/*     */       } 
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
/*     */ 
/*     */     
/* 560 */     if (this._anySetter != null) {
/*     */       try {
/* 562 */         this._anySetter.fixAccess(this._config);
/* 563 */       } catch (IllegalArgumentException e) {
/* 564 */         _handleBadAccess(e);
/*     */       } 
/*     */     }
/* 567 */     if (this._buildMethod != null) {
/*     */       try {
/* 569 */         this._buildMethod.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/* 570 */       } catch (IllegalArgumentException e) {
/* 571 */         _handleBadAccess(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected Map<String, List<PropertyName>> _collectAliases(Collection<SettableBeanProperty> props) {
/* 578 */     Map<String, List<PropertyName>> mapping = null;
/* 579 */     AnnotationIntrospector intr = this._config.getAnnotationIntrospector();
/* 580 */     if (intr != null) {
/* 581 */       for (SettableBeanProperty prop : props) {
/* 582 */         List<PropertyName> aliases = intr.findPropertyAliases((Annotated)prop.getMember());
/* 583 */         if (aliases == null || aliases.isEmpty()) {
/*     */           continue;
/*     */         }
/* 586 */         if (mapping == null) {
/* 587 */           mapping = new HashMap<>();
/*     */         }
/* 589 */         mapping.put(prop.getName(), aliases);
/*     */       } 
/*     */     }
/* 592 */     if (mapping == null) {
/* 593 */       return Collections.emptyMap();
/*     */     }
/* 595 */     return mapping;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _findCaseInsensitivity() {
/* 602 */     JsonFormat.Value format = this._beanDesc.findExpectedFormat(null);
/*     */     
/* 604 */     Boolean B = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
/* 605 */     return (B == null) ? this._config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES) : B
/* 606 */       .booleanValue();
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
/*     */   protected void _handleBadAccess(IllegalArgumentException e0) throws JsonMappingException {
/*     */     try {
/* 620 */       this._context.reportBadTypeDefinition(this._beanDesc, e0.getMessage(), new Object[0]);
/* 621 */     } catch (DatabindException e) {
/* 622 */       if (e.getCause() == null) {
/* 623 */         e.initCause(e0);
/*     */       }
/* 625 */       throw e;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/BeanDeserializerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */