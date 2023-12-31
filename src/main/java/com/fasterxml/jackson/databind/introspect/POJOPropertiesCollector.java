/*      */ package com.fasterxml.jackson.databind.introspect;
/*      */ 
/*      */ import com.fasterxml.jackson.annotation.JacksonInject;
/*      */ import com.fasterxml.jackson.annotation.JsonCreator;
/*      */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.MapperFeature;
/*      */ import com.fasterxml.jackson.databind.PropertyName;
/*      */ import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*      */ import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
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
/*      */ public class POJOPropertiesCollector
/*      */ {
/*      */   protected final MapperConfig<?> _config;
/*      */   protected final AccessorNamingStrategy _accessorNaming;
/*      */   protected final boolean _forSerialization;
/*      */   protected final JavaType _type;
/*      */   protected final AnnotatedClass _classDef;
/*      */   protected final VisibilityChecker<?> _visibilityChecker;
/*      */   protected final AnnotationIntrospector _annotationIntrospector;
/*      */   protected final boolean _useAnnotations;
/*      */   protected boolean _collected;
/*      */   protected LinkedHashMap<String, POJOPropertyBuilder> _properties;
/*      */   protected LinkedList<POJOPropertyBuilder> _creatorProperties;
/*      */   protected Map<PropertyName, PropertyName> _fieldRenameMappings;
/*      */   protected LinkedList<AnnotatedMember> _anyGetters;
/*      */   protected LinkedList<AnnotatedMember> _anyGetterField;
/*      */   protected LinkedList<AnnotatedMethod> _anySetters;
/*      */   protected LinkedList<AnnotatedMember> _anySetterField;
/*      */   protected LinkedList<AnnotatedMember> _jsonKeyAccessors;
/*      */   protected LinkedList<AnnotatedMember> _jsonValueAccessors;
/*      */   protected HashSet<String> _ignoredPropertyNames;
/*      */   protected LinkedHashMap<Object, AnnotatedMember> _injectables;
/*      */   @Deprecated
/*      */   protected final boolean _stdBeanNaming;
/*      */   @Deprecated
/*  152 */   protected String _mutatorPrefix = "set";
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
/*      */   protected POJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef, AccessorNamingStrategy accessorNaming) {
/*  168 */     this._config = config;
/*  169 */     this._forSerialization = forSerialization;
/*  170 */     this._type = type;
/*  171 */     this._classDef = classDef;
/*  172 */     if (config.isAnnotationProcessingEnabled()) {
/*  173 */       this._useAnnotations = true;
/*  174 */       this._annotationIntrospector = this._config.getAnnotationIntrospector();
/*      */     } else {
/*  176 */       this._useAnnotations = false;
/*  177 */       this._annotationIntrospector = AnnotationIntrospector.nopInstance();
/*      */     } 
/*  179 */     this._visibilityChecker = this._config.getDefaultVisibilityChecker(type.getRawClass(), classDef);
/*      */     
/*  181 */     this._accessorNaming = accessorNaming;
/*      */ 
/*      */     
/*  184 */     this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected POJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef, String mutatorPrefix) {
/*  195 */     this(config, forSerialization, type, classDef, 
/*  196 */         _accessorNaming(config, classDef, mutatorPrefix));
/*  197 */     this._mutatorPrefix = mutatorPrefix;
/*      */   }
/*      */ 
/*      */   
/*      */   private static AccessorNamingStrategy _accessorNaming(MapperConfig<?> config, AnnotatedClass classDef, String mutatorPrefix) {
/*  202 */     if (mutatorPrefix == null) {
/*  203 */       mutatorPrefix = "set";
/*      */     }
/*  205 */     return (new DefaultAccessorNamingStrategy.Provider())
/*  206 */       .withSetterPrefix(mutatorPrefix).forPOJO(config, classDef);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapperConfig<?> getConfig() {
/*  216 */     return this._config;
/*      */   }
/*      */   
/*      */   public JavaType getType() {
/*  220 */     return this._type;
/*      */   }
/*      */   
/*      */   public AnnotatedClass getClassDef() {
/*  224 */     return this._classDef;
/*      */   }
/*      */   
/*      */   public AnnotationIntrospector getAnnotationIntrospector() {
/*  228 */     return this._annotationIntrospector;
/*      */   }
/*      */ 
/*      */   
/*      */   public List<BeanPropertyDefinition> getProperties() {
/*  233 */     Map<String, POJOPropertyBuilder> props = getPropertyMap();
/*  234 */     return new ArrayList<>(props.values());
/*      */   }
/*      */   
/*      */   public Map<Object, AnnotatedMember> getInjectables() {
/*  238 */     if (!this._collected) {
/*  239 */       collectAll();
/*      */     }
/*  241 */     return this._injectables;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotatedMember getJsonKeyAccessor() {
/*  248 */     if (!this._collected) {
/*  249 */       collectAll();
/*      */     }
/*      */     
/*  252 */     if (this._jsonKeyAccessors != null) {
/*  253 */       if (this._jsonKeyAccessors.size() > 1) {
/*  254 */         reportProblem("Multiple 'as-key' properties defined (%s vs %s)", new Object[] { this._jsonKeyAccessors
/*  255 */               .get(0), this._jsonKeyAccessors
/*  256 */               .get(1) });
/*      */       }
/*      */       
/*  259 */       return this._jsonKeyAccessors.get(0);
/*      */     } 
/*  261 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotatedMember getJsonValueAccessor() {
/*  269 */     if (!this._collected) {
/*  270 */       collectAll();
/*      */     }
/*      */     
/*  273 */     if (this._jsonValueAccessors != null) {
/*  274 */       if (this._jsonValueAccessors.size() > 1) {
/*  275 */         reportProblem("Multiple 'as-value' properties defined (%s vs %s)", new Object[] { this._jsonValueAccessors
/*  276 */               .get(0), this._jsonValueAccessors
/*  277 */               .get(1) });
/*      */       }
/*      */       
/*  280 */       return this._jsonValueAccessors.get(0);
/*      */     } 
/*  282 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public AnnotatedMember getAnyGetter() {
/*  293 */     return getAnyGetterMethod();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotatedMember getAnyGetterField() {
/*  301 */     if (!this._collected) {
/*  302 */       collectAll();
/*      */     }
/*  304 */     if (this._anyGetterField != null) {
/*  305 */       if (this._anyGetterField.size() > 1) {
/*  306 */         reportProblem("Multiple 'any-getter' fields defined (%s vs %s)", new Object[] { this._anyGetterField
/*  307 */               .get(0), this._anyGetterField.get(1) });
/*      */       }
/*  309 */       return this._anyGetterField.getFirst();
/*      */     } 
/*  311 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotatedMember getAnyGetterMethod() {
/*  319 */     if (!this._collected) {
/*  320 */       collectAll();
/*      */     }
/*  322 */     if (this._anyGetters != null) {
/*  323 */       if (this._anyGetters.size() > 1) {
/*  324 */         reportProblem("Multiple 'any-getter' methods defined (%s vs %s)", new Object[] { this._anyGetters
/*  325 */               .get(0), this._anyGetters.get(1) });
/*      */       }
/*  327 */       return this._anyGetters.getFirst();
/*      */     } 
/*  329 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public AnnotatedMember getAnySetterField() {
/*  334 */     if (!this._collected) {
/*  335 */       collectAll();
/*      */     }
/*  337 */     if (this._anySetterField != null) {
/*  338 */       if (this._anySetterField.size() > 1) {
/*  339 */         reportProblem("Multiple 'any-setter' fields defined (%s vs %s)", new Object[] { this._anySetterField
/*  340 */               .get(0), this._anySetterField.get(1) });
/*      */       }
/*  342 */       return this._anySetterField.getFirst();
/*      */     } 
/*  344 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public AnnotatedMethod getAnySetterMethod() {
/*  349 */     if (!this._collected) {
/*  350 */       collectAll();
/*      */     }
/*  352 */     if (this._anySetters != null) {
/*  353 */       if (this._anySetters.size() > 1) {
/*  354 */         reportProblem("Multiple 'any-setter' methods defined (%s vs %s)", new Object[] { this._anySetters
/*  355 */               .get(0), this._anySetters.get(1) });
/*      */       }
/*  357 */       return this._anySetters.getFirst();
/*      */     } 
/*  359 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<String> getIgnoredPropertyNames() {
/*  367 */     return this._ignoredPropertyNames;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectIdInfo getObjectIdInfo() {
/*  376 */     ObjectIdInfo info = this._annotationIntrospector.findObjectIdInfo(this._classDef);
/*  377 */     if (info != null) {
/*  378 */       info = this._annotationIntrospector.findObjectReferenceInfo(this._classDef, info);
/*      */     }
/*  380 */     return info;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Map<String, POJOPropertyBuilder> getPropertyMap() {
/*  385 */     if (!this._collected) {
/*  386 */       collectAll();
/*      */     }
/*  388 */     return this._properties;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public AnnotatedMethod getJsonValueMethod() {
/*  393 */     AnnotatedMember m = getJsonValueAccessor();
/*  394 */     if (m instanceof AnnotatedMethod) {
/*  395 */       return (AnnotatedMethod)m;
/*      */     }
/*  397 */     return null;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public Class<?> findPOJOBuilderClass() {
/*  402 */     return this._annotationIntrospector.findPOJOBuilder(this._classDef);
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
/*      */   protected void collectAll() {
/*  418 */     LinkedHashMap<String, POJOPropertyBuilder> props = new LinkedHashMap<>();
/*      */ 
/*      */     
/*  421 */     _addFields(props);
/*  422 */     _addMethods(props);
/*      */ 
/*      */     
/*  425 */     if (!this._classDef.isNonStaticInnerClass()) {
/*  426 */       _addCreators(props);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  431 */     _removeUnwantedProperties(props);
/*      */     
/*  433 */     _removeUnwantedAccessor(props);
/*      */ 
/*      */     
/*  436 */     _renameProperties(props);
/*      */ 
/*      */ 
/*      */     
/*  440 */     _addInjectables(props);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  445 */     for (POJOPropertyBuilder property : props.values()) {
/*  446 */       property.mergeAnnotations(this._forSerialization);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  452 */     PropertyNamingStrategy naming = _findNamingStrategy();
/*  453 */     if (naming != null) {
/*  454 */       _renameUsing(props, naming);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  459 */     for (POJOPropertyBuilder property : props.values()) {
/*  460 */       property.trimByVisibility();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  465 */     if (this._config.isEnabled(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)) {
/*  466 */       _renameWithWrappers(props);
/*      */     }
/*      */ 
/*      */     
/*  470 */     _sortProperties(props);
/*  471 */     this._properties = props;
/*  472 */     this._collected = true;
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
/*      */   protected void _addFields(Map<String, POJOPropertyBuilder> props) {
/*  486 */     AnnotationIntrospector ai = this._annotationIntrospector;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  491 */     boolean pruneFinalFields = (!this._forSerialization && !this._config.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS));
/*  492 */     boolean transientAsIgnoral = this._config.isEnabled(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
/*      */     
/*  494 */     for (AnnotatedField f : this._classDef.fields()) {
/*      */       PropertyName pn;
/*  496 */       if (Boolean.TRUE.equals(ai.hasAsKey(this._config, f))) {
/*  497 */         if (this._jsonKeyAccessors == null) {
/*  498 */           this._jsonKeyAccessors = new LinkedList<>();
/*      */         }
/*  500 */         this._jsonKeyAccessors.add(f);
/*      */       } 
/*      */       
/*  503 */       if (Boolean.TRUE.equals(ai.hasAsValue(f))) {
/*  504 */         if (this._jsonValueAccessors == null) {
/*  505 */           this._jsonValueAccessors = new LinkedList<>();
/*      */         }
/*  507 */         this._jsonValueAccessors.add(f);
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/*  512 */       boolean anyGetter = Boolean.TRUE.equals(ai.hasAnyGetter(f));
/*  513 */       boolean anySetter = Boolean.TRUE.equals(ai.hasAnySetter(f));
/*  514 */       if (anyGetter || anySetter) {
/*      */         
/*  516 */         if (anyGetter) {
/*  517 */           if (this._anyGetterField == null) {
/*  518 */             this._anyGetterField = new LinkedList<>();
/*      */           }
/*  520 */           this._anyGetterField.add(f);
/*      */         } 
/*      */         
/*  523 */         if (anySetter) {
/*  524 */           if (this._anySetterField == null) {
/*  525 */             this._anySetterField = new LinkedList<>();
/*      */           }
/*  527 */           this._anySetterField.add(f);
/*      */         } 
/*      */         continue;
/*      */       } 
/*  531 */       String implName = ai.findImplicitPropertyName(f);
/*  532 */       if (implName == null) {
/*  533 */         implName = f.getName();
/*      */       }
/*      */ 
/*      */       
/*  537 */       implName = this._accessorNaming.modifyFieldName(f, implName);
/*  538 */       if (implName == null) {
/*      */         continue;
/*      */       }
/*      */       
/*  542 */       PropertyName implNameP = _propNameFromSimple(implName);
/*      */ 
/*      */ 
/*      */       
/*  546 */       PropertyName rename = ai.findRenameByField(this._config, f, implNameP);
/*  547 */       if (rename != null && !rename.equals(implNameP)) {
/*  548 */         if (this._fieldRenameMappings == null) {
/*  549 */           this._fieldRenameMappings = new HashMap<>();
/*      */         }
/*  551 */         this._fieldRenameMappings.put(rename, implNameP);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  556 */       if (this._forSerialization) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  561 */         pn = ai.findNameForSerialization(f);
/*      */       } else {
/*  563 */         pn = ai.findNameForDeserialization(f);
/*      */       } 
/*  565 */       boolean hasName = (pn != null);
/*  566 */       boolean nameExplicit = hasName;
/*      */       
/*  568 */       if (nameExplicit && pn.isEmpty()) {
/*  569 */         pn = _propNameFromSimple(implName);
/*  570 */         nameExplicit = false;
/*      */       } 
/*      */       
/*  573 */       boolean visible = (pn != null);
/*  574 */       if (!visible) {
/*  575 */         visible = this._visibilityChecker.isFieldVisible(f);
/*      */       }
/*      */       
/*  578 */       boolean ignored = ai.hasIgnoreMarker(f);
/*      */ 
/*      */       
/*  581 */       if (f.isTransient())
/*      */       {
/*      */         
/*  584 */         if (!hasName) {
/*  585 */           visible = false;
/*  586 */           if (transientAsIgnoral) {
/*  587 */             ignored = true;
/*      */           }
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  596 */       if (pruneFinalFields && pn == null && !ignored && 
/*  597 */         Modifier.isFinal(f.getModifiers())) {
/*      */         continue;
/*      */       }
/*  600 */       _property(props, implName).addField(f, pn, nameExplicit, visible, ignored);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _addCreators(Map<String, POJOPropertyBuilder> props) {
/*  610 */     if (!this._useAnnotations) {
/*      */       return;
/*      */     }
/*  613 */     for (AnnotatedConstructor ctor : this._classDef.getConstructors()) {
/*  614 */       if (this._creatorProperties == null) {
/*  615 */         this._creatorProperties = new LinkedList<>();
/*      */       }
/*  617 */       for (int i = 0, len = ctor.getParameterCount(); i < len; i++) {
/*  618 */         _addCreatorParam(props, ctor.getParameter(i));
/*      */       }
/*      */     } 
/*  621 */     for (AnnotatedMethod factory : this._classDef.getFactoryMethods()) {
/*  622 */       if (this._creatorProperties == null) {
/*  623 */         this._creatorProperties = new LinkedList<>();
/*      */       }
/*  625 */       for (int i = 0, len = factory.getParameterCount(); i < len; i++) {
/*  626 */         _addCreatorParam(props, factory.getParameter(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _addCreatorParam(Map<String, POJOPropertyBuilder> props, AnnotatedParameter param) {
/*  638 */     String impl = this._annotationIntrospector.findImplicitPropertyName(param);
/*  639 */     if (impl == null) {
/*  640 */       impl = "";
/*      */     }
/*  642 */     PropertyName pn = this._annotationIntrospector.findNameForDeserialization(param);
/*  643 */     boolean expl = (pn != null && !pn.isEmpty());
/*  644 */     if (!expl) {
/*  645 */       if (impl.isEmpty()) {
/*      */         return;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  651 */       JsonCreator.Mode creatorMode = this._annotationIntrospector.findCreatorAnnotation(this._config, param
/*  652 */           .getOwner());
/*  653 */       if (creatorMode == null || creatorMode == JsonCreator.Mode.DISABLED) {
/*      */         return;
/*      */       }
/*  656 */       pn = PropertyName.construct(impl);
/*      */     } 
/*      */ 
/*      */     
/*  660 */     impl = _checkRenameByField(impl);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  670 */     POJOPropertyBuilder prop = (expl && impl.isEmpty()) ? _property(props, pn) : _property(props, impl);
/*  671 */     prop.addCtor(param, pn, expl, true, false);
/*  672 */     this._creatorProperties.add(prop);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _addMethods(Map<String, POJOPropertyBuilder> props) {
/*  680 */     for (AnnotatedMethod m : this._classDef.memberMethods()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  686 */       int argCount = m.getParameterCount();
/*  687 */       if (argCount == 0) {
/*  688 */         _addGetterMethod(props, m, this._annotationIntrospector); continue;
/*  689 */       }  if (argCount == 1) {
/*  690 */         _addSetterMethod(props, m, this._annotationIntrospector); continue;
/*  691 */       }  if (argCount == 2 && 
/*  692 */         Boolean.TRUE.equals(this._annotationIntrospector.hasAnySetter(m))) {
/*  693 */         if (this._anySetters == null) {
/*  694 */           this._anySetters = new LinkedList<>();
/*      */         }
/*  696 */         this._anySetters.add(m);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _addGetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai) {
/*      */     boolean visible;
/*  708 */     Class<?> rt = m.getRawReturnType();
/*  709 */     if (rt == void.class || (rt == Void.class && 
/*  710 */       !this._config.isEnabled(MapperFeature.ALLOW_VOID_VALUED_PROPERTIES))) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  717 */     if (Boolean.TRUE.equals(ai.hasAnyGetter(m))) {
/*  718 */       if (this._anyGetters == null) {
/*  719 */         this._anyGetters = new LinkedList<>();
/*      */       }
/*  721 */       this._anyGetters.add(m);
/*      */       
/*      */       return;
/*      */     } 
/*  725 */     if (Boolean.TRUE.equals(ai.hasAsKey(this._config, m))) {
/*  726 */       if (this._jsonKeyAccessors == null) {
/*  727 */         this._jsonKeyAccessors = new LinkedList<>();
/*      */       }
/*  729 */       this._jsonKeyAccessors.add(m);
/*      */       
/*      */       return;
/*      */     } 
/*  733 */     if (Boolean.TRUE.equals(ai.hasAsValue(m))) {
/*  734 */       if (this._jsonValueAccessors == null) {
/*  735 */         this._jsonValueAccessors = new LinkedList<>();
/*      */       }
/*  737 */       this._jsonValueAccessors.add(m);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  743 */     PropertyName pn = ai.findNameForSerialization(m);
/*  744 */     boolean nameExplicit = (pn != null);
/*      */     
/*  746 */     if (!nameExplicit) {
/*  747 */       implName = ai.findImplicitPropertyName(m);
/*  748 */       if (implName == null) {
/*  749 */         implName = this._accessorNaming.findNameForRegularGetter(m, m.getName());
/*      */       }
/*  751 */       if (implName == null) {
/*  752 */         implName = this._accessorNaming.findNameForIsGetter(m, m.getName());
/*  753 */         if (implName == null) {
/*      */           return;
/*      */         }
/*  756 */         visible = this._visibilityChecker.isIsGetterVisible(m);
/*      */       } else {
/*  758 */         visible = this._visibilityChecker.isGetterVisible(m);
/*      */       } 
/*      */     } else {
/*      */       
/*  762 */       implName = ai.findImplicitPropertyName(m);
/*  763 */       if (implName == null) {
/*  764 */         implName = this._accessorNaming.findNameForRegularGetter(m, m.getName());
/*  765 */         if (implName == null) {
/*  766 */           implName = this._accessorNaming.findNameForIsGetter(m, m.getName());
/*      */         }
/*      */       } 
/*      */       
/*  770 */       if (implName == null) {
/*  771 */         implName = m.getName();
/*      */       }
/*  773 */       if (pn.isEmpty()) {
/*      */         
/*  775 */         pn = _propNameFromSimple(implName);
/*  776 */         nameExplicit = false;
/*      */       } 
/*  778 */       visible = true;
/*      */     } 
/*      */     
/*  781 */     String implName = _checkRenameByField(implName);
/*  782 */     boolean ignore = ai.hasIgnoreMarker(m);
/*  783 */     _property(props, implName).addGetter(m, pn, nameExplicit, visible, ignore);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _addSetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai) {
/*      */     boolean visible;
/*  791 */     PropertyName pn = ai.findNameForDeserialization(m);
/*  792 */     boolean nameExplicit = (pn != null);
/*  793 */     if (!nameExplicit) {
/*  794 */       implName = ai.findImplicitPropertyName(m);
/*  795 */       if (implName == null) {
/*  796 */         implName = this._accessorNaming.findNameForMutator(m, m.getName());
/*      */       }
/*  798 */       if (implName == null) {
/*      */         return;
/*      */       }
/*  801 */       visible = this._visibilityChecker.isSetterVisible(m);
/*      */     } else {
/*      */       
/*  804 */       implName = ai.findImplicitPropertyName(m);
/*  805 */       if (implName == null) {
/*  806 */         implName = this._accessorNaming.findNameForMutator(m, m.getName());
/*      */       }
/*      */       
/*  809 */       if (implName == null) {
/*  810 */         implName = m.getName();
/*      */       }
/*  812 */       if (pn.isEmpty()) {
/*      */         
/*  814 */         pn = _propNameFromSimple(implName);
/*  815 */         nameExplicit = false;
/*      */       } 
/*  817 */       visible = true;
/*      */     } 
/*      */     
/*  820 */     String implName = _checkRenameByField(implName);
/*  821 */     boolean ignore = ai.hasIgnoreMarker(m);
/*  822 */     _property(props, implName)
/*  823 */       .addSetter(m, pn, nameExplicit, visible, ignore);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _addInjectables(Map<String, POJOPropertyBuilder> props) {
/*  829 */     for (AnnotatedField f : this._classDef.fields()) {
/*  830 */       _doAddInjectable(this._annotationIntrospector.findInjectableValue(f), f);
/*      */     }
/*      */     
/*  833 */     for (AnnotatedMethod m : this._classDef.memberMethods()) {
/*      */       
/*  835 */       if (m.getParameterCount() != 1) {
/*      */         continue;
/*      */       }
/*  838 */       _doAddInjectable(this._annotationIntrospector.findInjectableValue(m), m);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _doAddInjectable(JacksonInject.Value injectable, AnnotatedMember m) {
/*  844 */     if (injectable == null) {
/*      */       return;
/*      */     }
/*  847 */     Object id = injectable.getId();
/*  848 */     if (this._injectables == null) {
/*  849 */       this._injectables = new LinkedHashMap<>();
/*      */     }
/*  851 */     AnnotatedMember prev = this._injectables.put(id, m);
/*  852 */     if (prev != null)
/*      */     {
/*  854 */       if (prev.getClass() == m.getClass()) {
/*  855 */         String type = id.getClass().getName();
/*  856 */         throw new IllegalArgumentException("Duplicate injectable value with id '" + id + "' (of type " + type + ")");
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private PropertyName _propNameFromSimple(String simpleName) {
/*  863 */     return PropertyName.construct(simpleName, null);
/*      */   }
/*      */ 
/*      */   
/*      */   private String _checkRenameByField(String implName) {
/*  868 */     if (this._fieldRenameMappings != null) {
/*  869 */       PropertyName p = this._fieldRenameMappings.get(_propNameFromSimple(implName));
/*  870 */       if (p != null) {
/*  871 */         implName = p.getSimpleName();
/*  872 */         return implName;
/*      */       } 
/*      */     } 
/*      */     
/*  876 */     return implName;
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
/*      */   protected void _removeUnwantedProperties(Map<String, POJOPropertyBuilder> props) {
/*  891 */     Iterator<POJOPropertyBuilder> it = props.values().iterator();
/*  892 */     while (it.hasNext()) {
/*  893 */       POJOPropertyBuilder prop = it.next();
/*      */ 
/*      */       
/*  896 */       if (!prop.anyVisible()) {
/*  897 */         it.remove();
/*      */         
/*      */         continue;
/*      */       } 
/*  901 */       if (prop.anyIgnorals()) {
/*      */         
/*  903 */         if (!prop.isExplicitlyIncluded()) {
/*  904 */           it.remove();
/*  905 */           _collectIgnorals(prop.getName());
/*      */           
/*      */           continue;
/*      */         } 
/*  909 */         prop.removeIgnored();
/*  910 */         if (!prop.couldDeserialize()) {
/*  911 */           _collectIgnorals(prop.getName());
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _removeUnwantedAccessor(Map<String, POJOPropertyBuilder> props) {
/*  924 */     boolean inferMutators = this._config.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS);
/*  925 */     Iterator<POJOPropertyBuilder> it = props.values().iterator();
/*      */     
/*  927 */     while (it.hasNext()) {
/*  928 */       POJOPropertyBuilder prop = it.next();
/*      */ 
/*      */ 
/*      */       
/*  932 */       prop.removeNonVisible(inferMutators, this._forSerialization ? null : this);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _collectIgnorals(String name) {
/*  943 */     if (!this._forSerialization && name != null) {
/*  944 */       if (this._ignoredPropertyNames == null) {
/*  945 */         this._ignoredPropertyNames = new HashSet<>();
/*      */       }
/*  947 */       this._ignoredPropertyNames.add(name);
/*      */     } 
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
/*      */   protected void _renameProperties(Map<String, POJOPropertyBuilder> props) {
/*  960 */     Iterator<Map.Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
/*  961 */     LinkedList<POJOPropertyBuilder> renamed = null;
/*  962 */     while (it.hasNext()) {
/*  963 */       Map.Entry<String, POJOPropertyBuilder> entry = it.next();
/*  964 */       POJOPropertyBuilder prop = entry.getValue();
/*      */       
/*  966 */       Collection<PropertyName> l = prop.findExplicitNames();
/*      */ 
/*      */       
/*  969 */       if (l.isEmpty()) {
/*      */         continue;
/*      */       }
/*  972 */       it.remove();
/*  973 */       if (renamed == null) {
/*  974 */         renamed = new LinkedList<>();
/*      */       }
/*      */       
/*  977 */       if (l.size() == 1) {
/*  978 */         PropertyName n = l.iterator().next();
/*  979 */         renamed.add(prop.withName(n));
/*      */         
/*      */         continue;
/*      */       } 
/*  983 */       renamed.addAll(prop.explode(l));
/*      */     } 
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
/*  999 */     if (renamed != null) {
/* 1000 */       for (POJOPropertyBuilder prop : renamed) {
/* 1001 */         String name = prop.getName();
/* 1002 */         POJOPropertyBuilder old = props.get(name);
/* 1003 */         if (old == null) {
/* 1004 */           props.put(name, prop);
/*      */         } else {
/* 1006 */           old.addAll(prop);
/*      */         } 
/*      */         
/* 1009 */         if (_replaceCreatorProperty(prop, this._creatorProperties))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1020 */           if (this._ignoredPropertyNames != null) {
/* 1021 */             this._ignoredPropertyNames.remove(name);
/*      */           }
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _renameUsing(Map<String, POJOPropertyBuilder> propMap, PropertyNamingStrategy naming) {
/* 1031 */     POJOPropertyBuilder[] props = (POJOPropertyBuilder[])propMap.values().toArray((Object[])new POJOPropertyBuilder[propMap.size()]);
/* 1032 */     propMap.clear();
/* 1033 */     for (POJOPropertyBuilder prop : props) {
/* 1034 */       String simpleName; PropertyName fullName = prop.getFullName();
/* 1035 */       String rename = null;
/*      */ 
/*      */       
/* 1038 */       if (!prop.isExplicitlyNamed() || this._config.isEnabled(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)) {
/* 1039 */         if (this._forSerialization) {
/* 1040 */           if (prop.hasGetter()) {
/* 1041 */             rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
/* 1042 */           } else if (prop.hasField()) {
/* 1043 */             rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
/*      */           }
/*      */         
/* 1046 */         } else if (prop.hasSetter()) {
/* 1047 */           rename = naming.nameForSetterMethod(this._config, prop.getSetterUnchecked(), fullName.getSimpleName());
/* 1048 */         } else if (prop.hasConstructorParameter()) {
/* 1049 */           rename = naming.nameForConstructorParameter(this._config, prop.getConstructorParameter(), fullName.getSimpleName());
/* 1050 */         } else if (prop.hasField()) {
/* 1051 */           rename = naming.nameForField(this._config, prop.getFieldUnchecked(), fullName.getSimpleName());
/* 1052 */         } else if (prop.hasGetter()) {
/*      */ 
/*      */           
/* 1055 */           rename = naming.nameForGetterMethod(this._config, prop.getGetterUnchecked(), fullName.getSimpleName());
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/* 1060 */       if (rename != null && !fullName.hasSimpleName(rename)) {
/* 1061 */         prop = prop.withSimpleName(rename);
/* 1062 */         simpleName = rename;
/*      */       } else {
/* 1064 */         simpleName = fullName.getSimpleName();
/*      */       } 
/*      */       
/* 1067 */       POJOPropertyBuilder old = propMap.get(simpleName);
/* 1068 */       if (old == null) {
/* 1069 */         propMap.put(simpleName, prop);
/*      */       } else {
/* 1071 */         old.addAll(prop);
/*      */       } 
/*      */ 
/*      */       
/* 1075 */       _replaceCreatorProperty(prop, this._creatorProperties);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _renameWithWrappers(Map<String, POJOPropertyBuilder> props) {
/* 1083 */     Iterator<Map.Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
/* 1084 */     LinkedList<POJOPropertyBuilder> renamed = null;
/* 1085 */     while (it.hasNext()) {
/* 1086 */       Map.Entry<String, POJOPropertyBuilder> entry = it.next();
/* 1087 */       POJOPropertyBuilder prop = entry.getValue();
/* 1088 */       AnnotatedMember member = prop.getPrimaryMember();
/* 1089 */       if (member == null) {
/*      */         continue;
/*      */       }
/* 1092 */       PropertyName wrapperName = this._annotationIntrospector.findWrapperName(member);
/*      */ 
/*      */ 
/*      */       
/* 1096 */       if (wrapperName == null || !wrapperName.hasSimpleName()) {
/*      */         continue;
/*      */       }
/* 1099 */       if (!wrapperName.equals(prop.getFullName())) {
/* 1100 */         if (renamed == null) {
/* 1101 */           renamed = new LinkedList<>();
/*      */         }
/* 1103 */         prop = prop.withName(wrapperName);
/* 1104 */         renamed.add(prop);
/* 1105 */         it.remove();
/*      */       } 
/*      */     } 
/*      */     
/* 1109 */     if (renamed != null) {
/* 1110 */       for (POJOPropertyBuilder prop : renamed) {
/* 1111 */         String name = prop.getName();
/* 1112 */         POJOPropertyBuilder old = props.get(name);
/* 1113 */         if (old == null) {
/* 1114 */           props.put(name, prop); continue;
/*      */         } 
/* 1116 */         old.addAll(prop);
/*      */       } 
/*      */     }
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
/*      */   protected void _sortProperties(Map<String, POJOPropertyBuilder> props) {
/*      */     Map<String, POJOPropertyBuilder> all;
/* 1135 */     AnnotationIntrospector intr = this._annotationIntrospector;
/* 1136 */     Boolean alpha = intr.findSerializationSortAlphabetically(this._classDef);
/*      */ 
/*      */     
/* 1139 */     boolean sortAlpha = (alpha == null) ? this._config.shouldSortPropertiesAlphabetically() : alpha.booleanValue();
/* 1140 */     boolean indexed = _anyIndexed(props.values());
/*      */     
/* 1142 */     String[] propertyOrder = intr.findSerializationPropertyOrder(this._classDef);
/*      */ 
/*      */     
/* 1145 */     if (!sortAlpha && !indexed && this._creatorProperties == null && propertyOrder == null) {
/*      */       return;
/*      */     }
/* 1148 */     int size = props.size();
/*      */ 
/*      */     
/* 1151 */     if (sortAlpha) {
/* 1152 */       all = new TreeMap<>();
/*      */     } else {
/* 1154 */       all = new LinkedHashMap<>(size + size);
/*      */     } 
/*      */     
/* 1157 */     for (POJOPropertyBuilder prop : props.values()) {
/* 1158 */       all.put(prop.getName(), prop);
/*      */     }
/* 1160 */     Map<String, POJOPropertyBuilder> ordered = new LinkedHashMap<>(size + size);
/*      */     
/* 1162 */     if (propertyOrder != null) {
/* 1163 */       for (String name : propertyOrder) {
/* 1164 */         POJOPropertyBuilder w = all.remove(name);
/* 1165 */         if (w == null) {
/* 1166 */           for (POJOPropertyBuilder prop : props.values()) {
/* 1167 */             if (name.equals(prop.getInternalName())) {
/* 1168 */               w = prop;
/*      */               
/* 1170 */               name = prop.getName();
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         }
/* 1175 */         if (w != null) {
/* 1176 */           ordered.put(name, w);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1182 */     if (indexed) {
/* 1183 */       Map<Integer, POJOPropertyBuilder> byIndex = new TreeMap<>();
/* 1184 */       Iterator<Map.Entry<String, POJOPropertyBuilder>> it = all.entrySet().iterator();
/* 1185 */       while (it.hasNext()) {
/* 1186 */         Map.Entry<String, POJOPropertyBuilder> entry = it.next();
/* 1187 */         POJOPropertyBuilder prop = entry.getValue();
/* 1188 */         Integer index = prop.getMetadata().getIndex();
/* 1189 */         if (index != null) {
/* 1190 */           byIndex.put(index, prop);
/* 1191 */           it.remove();
/*      */         } 
/*      */       } 
/* 1194 */       for (POJOPropertyBuilder prop : byIndex.values()) {
/* 1195 */         ordered.put(prop.getName(), prop);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1201 */     if (this._creatorProperties != null && (!sortAlpha || this._config
/* 1202 */       .isEnabled(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST))) {
/*      */       Collection<POJOPropertyBuilder> cr;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1209 */       if (sortAlpha) {
/* 1210 */         TreeMap<String, POJOPropertyBuilder> sorted = new TreeMap<>();
/*      */         
/* 1212 */         for (POJOPropertyBuilder prop : this._creatorProperties) {
/* 1213 */           sorted.put(prop.getName(), prop);
/*      */         }
/* 1215 */         cr = sorted.values();
/*      */       } else {
/* 1217 */         cr = this._creatorProperties;
/*      */       } 
/* 1219 */       for (POJOPropertyBuilder prop : cr) {
/*      */ 
/*      */         
/* 1222 */         String name = prop.getName();
/*      */ 
/*      */         
/* 1225 */         if (all.containsKey(name)) {
/* 1226 */           ordered.put(name, prop);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1231 */     ordered.putAll(all);
/* 1232 */     props.clear();
/* 1233 */     props.putAll(ordered);
/*      */   }
/*      */   
/*      */   private boolean _anyIndexed(Collection<POJOPropertyBuilder> props) {
/* 1237 */     for (POJOPropertyBuilder prop : props) {
/* 1238 */       if (prop.getMetadata().hasIndex()) {
/* 1239 */         return true;
/*      */       }
/*      */     } 
/* 1242 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void reportProblem(String msg, Object... args) {
/* 1252 */     if (args.length > 0) {
/* 1253 */       msg = String.format(msg, args);
/*      */     }
/* 1255 */     throw new IllegalArgumentException("Problem with definition of " + this._classDef + ": " + msg);
/*      */   }
/*      */ 
/*      */   
/*      */   protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, PropertyName name) {
/* 1260 */     String simpleName = name.getSimpleName();
/* 1261 */     POJOPropertyBuilder prop = props.get(simpleName);
/* 1262 */     if (prop == null) {
/* 1263 */       prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, name);
/*      */       
/* 1265 */       props.put(simpleName, prop);
/*      */     } 
/* 1267 */     return prop;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, String implName) {
/* 1274 */     POJOPropertyBuilder prop = props.get(implName);
/* 1275 */     if (prop == null) {
/*      */       
/* 1277 */       prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, PropertyName.construct(implName));
/* 1278 */       props.put(implName, prop);
/*      */     } 
/* 1280 */     return prop;
/*      */   }
/*      */ 
/*      */   
/*      */   private PropertyNamingStrategy _findNamingStrategy() {
/* 1285 */     Object namingDef = this._annotationIntrospector.findNamingStrategy(this._classDef);
/* 1286 */     if (namingDef == null) {
/* 1287 */       return this._config.getPropertyNamingStrategy();
/*      */     }
/* 1289 */     if (namingDef instanceof PropertyNamingStrategy) {
/* 1290 */       return (PropertyNamingStrategy)namingDef;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1295 */     if (!(namingDef instanceof Class)) {
/* 1296 */       throw new IllegalStateException("AnnotationIntrospector returned PropertyNamingStrategy definition of type " + namingDef
/* 1297 */           .getClass().getName() + "; expected type PropertyNamingStrategy or Class<PropertyNamingStrategy> instead");
/*      */     }
/* 1299 */     Class<?> namingClass = (Class)namingDef;
/*      */     
/* 1301 */     if (namingClass == PropertyNamingStrategy.class) {
/* 1302 */       return null;
/*      */     }
/*      */     
/* 1305 */     if (!PropertyNamingStrategy.class.isAssignableFrom(namingClass)) {
/* 1306 */       throw new IllegalStateException("AnnotationIntrospector returned Class " + namingClass
/* 1307 */           .getName() + "; expected Class<PropertyNamingStrategy>");
/*      */     }
/* 1309 */     HandlerInstantiator hi = this._config.getHandlerInstantiator();
/* 1310 */     if (hi != null) {
/* 1311 */       PropertyNamingStrategy pns = hi.namingStrategyInstance(this._config, this._classDef, namingClass);
/* 1312 */       if (pns != null) {
/* 1313 */         return pns;
/*      */       }
/*      */     } 
/* 1316 */     return (PropertyNamingStrategy)ClassUtil.createInstance(namingClass, this._config
/* 1317 */         .canOverrideAccessModifiers());
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   protected void _updateCreatorProperty(POJOPropertyBuilder prop, List<POJOPropertyBuilder> creatorProperties) {
/* 1322 */     _replaceCreatorProperty(prop, creatorProperties);
/*      */   }
/*      */   
/*      */   protected boolean _replaceCreatorProperty(POJOPropertyBuilder prop, List<POJOPropertyBuilder> creatorProperties) {
/* 1326 */     if (creatorProperties != null) {
/* 1327 */       String intName = prop.getInternalName();
/* 1328 */       for (int i = 0, len = creatorProperties.size(); i < len; i++) {
/* 1329 */         if (((POJOPropertyBuilder)creatorProperties.get(i)).getInternalName().equals(intName)) {
/* 1330 */           creatorProperties.set(i, prop);
/* 1331 */           return true;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1335 */     return false;
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/POJOPropertiesCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */