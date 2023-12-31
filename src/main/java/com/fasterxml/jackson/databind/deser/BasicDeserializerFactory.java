/*      */ package com.fasterxml.jackson.databind.deser;
/*      */ import com.fasterxml.jackson.annotation.JacksonInject;
/*      */ import com.fasterxml.jackson.annotation.JsonCreator;
/*      */ import com.fasterxml.jackson.annotation.JsonSetter;
/*      */ import com.fasterxml.jackson.annotation.Nulls;
/*      */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*      */ import com.fasterxml.jackson.databind.BeanDescription;
/*      */ import com.fasterxml.jackson.databind.BeanProperty;
/*      */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*      */ import com.fasterxml.jackson.databind.DeserializationContext;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*      */ import com.fasterxml.jackson.databind.JsonMappingException;
/*      */ import com.fasterxml.jackson.databind.JsonNode;
/*      */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*      */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*      */ import com.fasterxml.jackson.databind.PropertyName;
/*      */ import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
/*      */ import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
/*      */ import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
/*      */ import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
/*      */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*      */ import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*      */ import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*      */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*      */ import com.fasterxml.jackson.databind.type.ArrayType;
/*      */ import com.fasterxml.jackson.databind.type.CollectionLikeType;
/*      */ import com.fasterxml.jackson.databind.type.CollectionType;
/*      */ import com.fasterxml.jackson.databind.type.MapLikeType;
/*      */ import com.fasterxml.jackson.databind.type.MapType;
/*      */ import com.fasterxml.jackson.databind.type.ReferenceType;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable {
/*   49 */   private static final Class<?> CLASS_OBJECT = Object.class;
/*   50 */   private static final Class<?> CLASS_STRING = String.class;
/*   51 */   private static final Class<?> CLASS_CHAR_SEQUENCE = CharSequence.class;
/*   52 */   private static final Class<?> CLASS_ITERABLE = Iterable.class;
/*   53 */   private static final Class<?> CLASS_MAP_ENTRY = Map.Entry.class;
/*   54 */   private static final Class<?> CLASS_SERIALIZABLE = Serializable.class;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   60 */   protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final DeserializerFactoryConfig _factoryConfig;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BasicDeserializerFactory(DeserializerFactoryConfig config) {
/*   81 */     this._factoryConfig = config;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializerFactoryConfig getFactoryConfig() {
/*   92 */     return this._factoryConfig;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig paramDeserializerFactoryConfig);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
/*  109 */     return withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
/*  118 */     return withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
/*  127 */     return withConfig(this._factoryConfig.withDeserializerModifier(modifier));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
/*  136 */     return withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
/*  145 */     return withConfig(this._factoryConfig.withValueInstantiators(instantiators));
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
/*      */   public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
/*      */     while (true) {
/*  159 */       JavaType next = _mapAbstractType2(config, type);
/*  160 */       if (next == null) {
/*  161 */         return type;
/*      */       }
/*      */ 
/*      */       
/*  165 */       Class<?> prevCls = type.getRawClass();
/*  166 */       Class<?> nextCls = next.getRawClass();
/*  167 */       if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
/*  168 */         throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
/*      */       }
/*  170 */       type = next;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
/*  181 */     Class<?> currClass = type.getRawClass();
/*  182 */     if (this._factoryConfig.hasAbstractTypeResolvers()) {
/*  183 */       for (AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
/*  184 */         JavaType concrete = resolver.findTypeMapping(config, type);
/*  185 */         if (concrete != null && !concrete.hasRawClass(currClass)) {
/*  186 */           return concrete;
/*      */         }
/*      */       } 
/*      */     }
/*  190 */     return null;
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
/*      */   public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/*  209 */     DeserializationConfig config = ctxt.getConfig();
/*      */     
/*  211 */     ValueInstantiator instantiator = null;
/*      */     
/*  213 */     AnnotatedClass ac = beanDesc.getClassInfo();
/*  214 */     Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
/*  215 */     if (instDef != null) {
/*  216 */       instantiator = _valueInstantiatorInstance(config, (Annotated)ac, instDef);
/*      */     }
/*  218 */     if (instantiator == null) {
/*      */ 
/*      */       
/*  221 */       instantiator = JDKValueInstantiators.findStdValueInstantiator(config, beanDesc.getBeanClass());
/*  222 */       if (instantiator == null) {
/*  223 */         instantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  228 */     if (this._factoryConfig.hasValueInstantiators()) {
/*  229 */       for (ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
/*  230 */         instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
/*      */         
/*  232 */         if (instantiator == null) {
/*  233 */           ctxt.reportBadTypeDefinition(beanDesc, "Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", new Object[] { insts
/*      */                 
/*  235 */                 .getClass().getName() });
/*      */         }
/*      */       } 
/*      */     }
/*  239 */     if (instantiator != null) {
/*  240 */       instantiator = instantiator.createContextual(ctxt, beanDesc);
/*      */     }
/*      */     
/*  243 */     return instantiator;
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
/*      */   protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/*  258 */     DeserializationConfig config = ctxt.getConfig();
/*      */     
/*  260 */     VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker(beanDesc.getBeanClass(), beanDesc
/*  261 */         .getClassInfo());
/*  262 */     ConstructorDetector ctorDetector = config.getConstructorDetector();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  271 */     CreatorCollector creators = new CreatorCollector(beanDesc, (MapperConfig)config);
/*  272 */     Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = _findCreatorsFromProperties(ctxt, beanDesc);
/*      */     
/*  274 */     CreatorCollectionState ccState = new CreatorCollectionState(ctxt, beanDesc, vchecker, creators, creatorDefs);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  279 */     _addExplicitFactoryCreators(ctxt, ccState, !ctorDetector.requireCtorAnnotation());
/*      */ 
/*      */     
/*  282 */     if (beanDesc.getType().isConcrete()) {
/*      */       
/*  284 */       if (beanDesc.getType().isRecordType()) {
/*  285 */         List<String> names = new ArrayList<>();
/*      */         
/*  287 */         AnnotatedConstructor canonical = JDK14Util.findRecordConstructor(ctxt, beanDesc, names);
/*  288 */         if (canonical != null) {
/*  289 */           _addRecordConstructor(ctxt, ccState, canonical, names);
/*  290 */           return ccState.creators.constructValueInstantiator(ctxt);
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  296 */       boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
/*  297 */       if (!isNonStaticInnerClass) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  303 */         boolean findImplicit = ctorDetector.shouldIntrospectorImplicitConstructors(beanDesc.getBeanClass());
/*  304 */         _addExplicitConstructorCreators(ctxt, ccState, findImplicit);
/*  305 */         if (ccState.hasImplicitConstructorCandidates() && 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  311 */           !ccState.hasExplicitConstructors()) {
/*  312 */           _addImplicitConstructorCreators(ctxt, ccState, ccState.implicitConstructorCandidates());
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  317 */     if (ccState.hasImplicitFactoryCandidates() && 
/*  318 */       !ccState.hasExplicitFactories() && !ccState.hasExplicitConstructors()) {
/*  319 */       _addImplicitFactoryCreators(ctxt, ccState, ccState.implicitFactoryCandidates());
/*      */     }
/*  321 */     return ccState.creators.constructValueInstantiator(ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/*  327 */     Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = (Map)Collections.emptyMap();
/*  328 */     for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
/*  329 */       Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
/*  330 */       while (it.hasNext()) {
/*  331 */         AnnotatedParameter param = it.next();
/*  332 */         AnnotatedWithParams owner = param.getOwner();
/*  333 */         BeanPropertyDefinition[] defs = result.get(owner);
/*  334 */         int index = param.getIndex();
/*      */         
/*  336 */         if (defs == null) {
/*  337 */           if (result.isEmpty()) {
/*  338 */             result = (Map)new LinkedHashMap<>();
/*      */           }
/*  340 */           defs = new BeanPropertyDefinition[owner.getParameterCount()];
/*  341 */           result.put(owner, defs);
/*      */         }
/*  343 */         else if (defs[index] != null) {
/*  344 */           ctxt.reportBadTypeDefinition(beanDesc, "Conflict: parameter #%d of %s bound to more than one property; %s vs %s", new Object[] {
/*      */                 
/*  346 */                 Integer.valueOf(index), owner, defs[index], propDef
/*      */               });
/*      */         } 
/*  349 */         defs[index] = propDef;
/*      */       } 
/*      */     } 
/*  352 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
/*  359 */     if (instDef == null) {
/*  360 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  365 */     if (instDef instanceof ValueInstantiator) {
/*  366 */       return (ValueInstantiator)instDef;
/*      */     }
/*  368 */     if (!(instDef instanceof Class)) {
/*  369 */       throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef
/*  370 */           .getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
/*      */     }
/*      */     
/*  373 */     Class<?> instClass = (Class)instDef;
/*  374 */     if (ClassUtil.isBogusClass(instClass)) {
/*  375 */       return null;
/*      */     }
/*  377 */     if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
/*  378 */       throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
/*      */     }
/*      */     
/*  381 */     HandlerInstantiator hi = config.getHandlerInstantiator();
/*  382 */     if (hi != null) {
/*  383 */       ValueInstantiator inst = hi.valueInstantiatorInstance((MapperConfig)config, annotated, instClass);
/*  384 */       if (inst != null) {
/*  385 */         return inst;
/*      */       }
/*      */     } 
/*  388 */     return (ValueInstantiator)ClassUtil.createInstance(instClass, config
/*  389 */         .canOverrideAccessModifiers());
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
/*      */   protected void _addRecordConstructor(DeserializationContext ctxt, CreatorCollectionState ccState, AnnotatedConstructor canonical, List<String> implicitNames) throws JsonMappingException {
/*  408 */     int argCount = canonical.getParameterCount();
/*  409 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*  410 */     SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/*      */     
/*  412 */     for (int i = 0; i < argCount; i++) {
/*  413 */       AnnotatedParameter param = canonical.getParameter(i);
/*  414 */       JacksonInject.Value injectable = intr.findInjectableValue((AnnotatedMember)param);
/*  415 */       PropertyName name = intr.findNameForDeserialization((Annotated)param);
/*  416 */       if (name == null || name.isEmpty()) {
/*  417 */         name = PropertyName.construct(implicitNames.get(i));
/*      */       }
/*  419 */       properties[i] = constructCreatorProperty(ctxt, ccState.beanDesc, name, i, param, injectable);
/*      */     } 
/*  421 */     ccState.creators.addPropertyCreator((AnnotatedWithParams)canonical, false, properties);
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
/*      */   protected void _addExplicitConstructorCreators(DeserializationContext ctxt, CreatorCollectionState ccState, boolean findImplicit) throws JsonMappingException {
/*  434 */     BeanDescription beanDesc = ccState.beanDesc;
/*  435 */     CreatorCollector creators = ccState.creators;
/*  436 */     AnnotationIntrospector intr = ccState.annotationIntrospector();
/*  437 */     VisibilityChecker<?> vchecker = ccState.vchecker;
/*  438 */     Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams = ccState.creatorParams;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  443 */     AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
/*  444 */     if (defaultCtor != null && (
/*  445 */       !creators.hasDefaultCreator() || _hasCreatorAnnotation(ctxt, (Annotated)defaultCtor))) {
/*  446 */       creators.setDefaultCreator((AnnotatedWithParams)defaultCtor);
/*      */     }
/*      */ 
/*      */     
/*  450 */     for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
/*  451 */       JsonCreator.Mode creatorMode = intr.findCreatorAnnotation((MapperConfig)ctxt.getConfig(), (Annotated)ctor);
/*  452 */       if (JsonCreator.Mode.DISABLED == creatorMode) {
/*      */         continue;
/*      */       }
/*  455 */       if (creatorMode == null) {
/*      */         
/*  457 */         if (findImplicit && vchecker.isCreatorVisible((AnnotatedMember)ctor)) {
/*  458 */           ccState.addImplicitConstructorCandidate(CreatorCandidate.construct(intr, (AnnotatedWithParams)ctor, creatorParams
/*  459 */                 .get(ctor)));
/*      */         }
/*      */         
/*      */         continue;
/*      */       } 
/*  464 */       switch (creatorMode) {
/*      */         case DELEGATING:
/*  466 */           _addExplicitDelegatingCreator(ctxt, beanDesc, creators, 
/*  467 */               CreatorCandidate.construct(intr, (AnnotatedWithParams)ctor, null));
/*      */           break;
/*      */         case PROPERTIES:
/*  470 */           _addExplicitPropertyCreator(ctxt, beanDesc, creators, 
/*  471 */               CreatorCandidate.construct(intr, (AnnotatedWithParams)ctor, creatorParams.get(ctor)));
/*      */           break;
/*      */         default:
/*  474 */           _addExplicitAnyCreator(ctxt, beanDesc, creators, 
/*  475 */               CreatorCandidate.construct(intr, (AnnotatedWithParams)ctor, creatorParams.get(ctor)), ctxt
/*  476 */               .getConfig().getConstructorDetector());
/*      */           break;
/*      */       } 
/*  479 */       ccState.increaseExplicitConstructorCount();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _addImplicitConstructorCreators(DeserializationContext ctxt, CreatorCollectionState ccState, List<CreatorCandidate> ctorCandidates) throws JsonMappingException {
/*  487 */     DeserializationConfig config = ctxt.getConfig();
/*  488 */     BeanDescription beanDesc = ccState.beanDesc;
/*  489 */     CreatorCollector creators = ccState.creators;
/*  490 */     AnnotationIntrospector intr = ccState.annotationIntrospector();
/*  491 */     VisibilityChecker<?> vchecker = ccState.vchecker;
/*  492 */     List<AnnotatedWithParams> implicitCtors = null;
/*  493 */     boolean preferPropsBased = config.getConstructorDetector().singleArgCreatorDefaultsToProperties();
/*      */     
/*  495 */     for (CreatorCandidate candidate : ctorCandidates) {
/*  496 */       int argCount = candidate.paramCount();
/*  497 */       AnnotatedWithParams ctor = candidate.creator();
/*      */       
/*  499 */       if (argCount == 1) {
/*  500 */         BeanPropertyDefinition propDef = candidate.propertyDef(0);
/*  501 */         boolean useProps = (preferPropsBased || _checkIfCreatorPropertyBased(intr, ctor, propDef));
/*      */         
/*  503 */         if (useProps) {
/*  504 */           SettableBeanProperty[] arrayOfSettableBeanProperty = new SettableBeanProperty[1];
/*  505 */           JacksonInject.Value injection = candidate.injection(0);
/*      */ 
/*      */ 
/*      */           
/*  509 */           PropertyName name = candidate.paramName(0);
/*  510 */           if (name == null) {
/*  511 */             name = candidate.findImplicitParamName(0);
/*  512 */             if (name == null && injection == null) {
/*      */               continue;
/*      */             }
/*      */           } 
/*  516 */           arrayOfSettableBeanProperty[0] = constructCreatorProperty(ctxt, beanDesc, name, 0, candidate
/*  517 */               .parameter(0), injection);
/*  518 */           creators.addPropertyCreator(ctor, false, arrayOfSettableBeanProperty); continue;
/*      */         } 
/*  520 */         _handleSingleArgumentCreator(creators, ctor, false, vchecker
/*      */             
/*  522 */             .isCreatorVisible((AnnotatedMember)ctor));
/*      */ 
/*      */         
/*  525 */         if (propDef != null) {
/*  526 */           ((POJOPropertyBuilder)propDef).removeConstructors();
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         continue;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  537 */       int nonAnnotatedParamIndex = -1;
/*  538 */       SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/*  539 */       int explicitNameCount = 0;
/*  540 */       int implicitWithCreatorCount = 0;
/*  541 */       int injectCount = 0;
/*      */       
/*  543 */       for (int i = 0; i < argCount; i++) {
/*  544 */         AnnotatedParameter param = ctor.getParameter(i);
/*  545 */         BeanPropertyDefinition propDef = candidate.propertyDef(i);
/*  546 */         JacksonInject.Value injectable = intr.findInjectableValue((AnnotatedMember)param);
/*  547 */         PropertyName name = (propDef == null) ? null : propDef.getFullName();
/*      */         
/*  549 */         if (propDef != null && propDef.isExplicitlyNamed()) {
/*  550 */           explicitNameCount++;
/*  551 */           properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
/*      */         
/*      */         }
/*  554 */         else if (injectable != null) {
/*  555 */           injectCount++;
/*  556 */           properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
/*      */         } else {
/*      */           
/*  559 */           NameTransformer unwrapper = intr.findUnwrappingNameTransformer((AnnotatedMember)param);
/*  560 */           if (unwrapper != null) {
/*  561 */             _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
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
/*      */           }
/*  576 */           else if (nonAnnotatedParamIndex < 0) {
/*  577 */             nonAnnotatedParamIndex = i;
/*      */           } 
/*      */         } 
/*      */       } 
/*  581 */       int namedCount = explicitNameCount + implicitWithCreatorCount;
/*      */       
/*  583 */       if (explicitNameCount > 0 || injectCount > 0) {
/*      */         
/*  585 */         if (namedCount + injectCount == argCount) {
/*  586 */           creators.addPropertyCreator(ctor, false, properties);
/*      */           continue;
/*      */         } 
/*  589 */         if (explicitNameCount == 0 && injectCount + 1 == argCount) {
/*      */           
/*  591 */           creators.addDelegatingCreator(ctor, false, properties, 0);
/*      */ 
/*      */           
/*      */           continue;
/*      */         } 
/*      */         
/*  597 */         PropertyName impl = candidate.findImplicitParamName(nonAnnotatedParamIndex);
/*  598 */         if (impl == null || impl.isEmpty())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  607 */           ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of constructor %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", new Object[] {
/*      */                 
/*  609 */                 Integer.valueOf(nonAnnotatedParamIndex), ctor
/*      */               });
/*      */         }
/*      */       } 
/*  613 */       if (!creators.hasDefaultCreator()) {
/*  614 */         if (implicitCtors == null) {
/*  615 */           implicitCtors = new LinkedList<>();
/*      */         }
/*  617 */         implicitCtors.add(ctor);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  622 */     if (implicitCtors != null && !creators.hasDelegatingCreator() && 
/*  623 */       !creators.hasPropertyBasedCreator()) {
/*  624 */       _checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors);
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
/*      */   protected void _addExplicitFactoryCreators(DeserializationContext ctxt, CreatorCollectionState ccState, boolean findImplicit) throws JsonMappingException {
/*  639 */     BeanDescription beanDesc = ccState.beanDesc;
/*  640 */     CreatorCollector creators = ccState.creators;
/*  641 */     AnnotationIntrospector intr = ccState.annotationIntrospector();
/*  642 */     VisibilityChecker<?> vchecker = ccState.vchecker;
/*  643 */     Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams = ccState.creatorParams;
/*      */ 
/*      */     
/*  646 */     for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*  647 */       JsonCreator.Mode creatorMode = intr.findCreatorAnnotation((MapperConfig)ctxt.getConfig(), (Annotated)factory);
/*  648 */       int argCount = factory.getParameterCount();
/*  649 */       if (creatorMode == null) {
/*      */         
/*  651 */         if (findImplicit && argCount == 1 && vchecker.isCreatorVisible((AnnotatedMember)factory)) {
/*  652 */           ccState.addImplicitFactoryCandidate(CreatorCandidate.construct(intr, (AnnotatedWithParams)factory, null));
/*      */         }
/*      */         continue;
/*      */       } 
/*  656 */       if (creatorMode == JsonCreator.Mode.DISABLED) {
/*      */         continue;
/*      */       }
/*      */ 
/*      */       
/*  661 */       if (argCount == 0) {
/*  662 */         creators.setDefaultCreator((AnnotatedWithParams)factory);
/*      */         
/*      */         continue;
/*      */       } 
/*  666 */       switch (creatorMode) {
/*      */         case DELEGATING:
/*  668 */           _addExplicitDelegatingCreator(ctxt, beanDesc, creators, 
/*  669 */               CreatorCandidate.construct(intr, (AnnotatedWithParams)factory, null));
/*      */           break;
/*      */         case PROPERTIES:
/*  672 */           _addExplicitPropertyCreator(ctxt, beanDesc, creators, 
/*  673 */               CreatorCandidate.construct(intr, (AnnotatedWithParams)factory, creatorParams.get(factory)));
/*      */           break;
/*      */         
/*      */         default:
/*  677 */           _addExplicitAnyCreator(ctxt, beanDesc, creators, 
/*  678 */               CreatorCandidate.construct(intr, (AnnotatedWithParams)factory, creatorParams.get(factory)), ConstructorDetector.DEFAULT);
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  684 */       ccState.increaseExplicitFactoryCount();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _addImplicitFactoryCreators(DeserializationContext ctxt, CreatorCollectionState ccState, List<CreatorCandidate> factoryCandidates) throws JsonMappingException {
/*  692 */     BeanDescription beanDesc = ccState.beanDesc;
/*  693 */     CreatorCollector creators = ccState.creators;
/*  694 */     AnnotationIntrospector intr = ccState.annotationIntrospector();
/*  695 */     VisibilityChecker<?> vchecker = ccState.vchecker;
/*  696 */     Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams = ccState.creatorParams;
/*      */ 
/*      */     
/*  699 */     for (CreatorCandidate candidate : factoryCandidates) {
/*  700 */       int argCount = candidate.paramCount();
/*  701 */       AnnotatedWithParams factory = candidate.creator();
/*  702 */       BeanPropertyDefinition[] propDefs = creatorParams.get(factory);
/*      */       
/*  704 */       if (argCount != 1) {
/*      */         continue;
/*      */       }
/*  707 */       BeanPropertyDefinition argDef = candidate.propertyDef(0);
/*  708 */       boolean useProps = _checkIfCreatorPropertyBased(intr, factory, argDef);
/*  709 */       if (!useProps) {
/*  710 */         _handleSingleArgumentCreator(creators, factory, false, vchecker
/*  711 */             .isCreatorVisible((AnnotatedMember)factory));
/*      */ 
/*      */         
/*  714 */         if (argDef != null) {
/*  715 */           ((POJOPropertyBuilder)argDef).removeConstructors();
/*      */         }
/*      */         continue;
/*      */       } 
/*  719 */       AnnotatedParameter nonAnnotatedParam = null;
/*  720 */       SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/*  721 */       int implicitNameCount = 0;
/*  722 */       int explicitNameCount = 0;
/*  723 */       int injectCount = 0;
/*      */       
/*  725 */       for (int i = 0; i < argCount; i++) {
/*  726 */         AnnotatedParameter param = factory.getParameter(i);
/*  727 */         BeanPropertyDefinition propDef = (propDefs == null) ? null : propDefs[i];
/*  728 */         JacksonInject.Value injectable = intr.findInjectableValue((AnnotatedMember)param);
/*  729 */         PropertyName name = (propDef == null) ? null : propDef.getFullName();
/*      */         
/*  731 */         if (propDef != null && propDef.isExplicitlyNamed()) {
/*  732 */           explicitNameCount++;
/*  733 */           properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
/*      */         
/*      */         }
/*  736 */         else if (injectable != null) {
/*  737 */           injectCount++;
/*  738 */           properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
/*      */         } else {
/*      */           
/*  741 */           NameTransformer unwrapper = intr.findUnwrappingNameTransformer((AnnotatedMember)param);
/*  742 */           if (unwrapper != null) {
/*  743 */             _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
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
/*      */           }
/*  772 */           else if (nonAnnotatedParam == null) {
/*  773 */             nonAnnotatedParam = param;
/*      */           } 
/*      */         } 
/*  776 */       }  int namedCount = explicitNameCount + implicitNameCount;
/*      */ 
/*      */       
/*  779 */       if (explicitNameCount > 0 || injectCount > 0) {
/*      */         
/*  781 */         if (namedCount + injectCount == argCount) {
/*  782 */           creators.addPropertyCreator(factory, false, properties); continue;
/*  783 */         }  if (explicitNameCount == 0 && injectCount + 1 == argCount) {
/*      */           
/*  785 */           creators.addDelegatingCreator(factory, false, properties, 0); continue;
/*      */         } 
/*  787 */         ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of factory method %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", new Object[] {
/*      */               
/*  789 */               Integer.valueOf((nonAnnotatedParam == null) ? -1 : nonAnnotatedParam.getIndex()), factory
/*      */             });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _addExplicitDelegatingCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
/*  815 */     int ix = -1;
/*  816 */     int argCount = candidate.paramCount();
/*  817 */     SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/*  818 */     for (int i = 0; i < argCount; i++) {
/*  819 */       AnnotatedParameter param = candidate.parameter(i);
/*  820 */       JacksonInject.Value injectId = candidate.injection(i);
/*  821 */       if (injectId != null) {
/*  822 */         properties[i] = constructCreatorProperty(ctxt, beanDesc, null, i, param, injectId);
/*      */       
/*      */       }
/*  825 */       else if (ix < 0) {
/*  826 */         ix = i;
/*      */       }
/*      */       else {
/*      */         
/*  830 */         ctxt.reportBadTypeDefinition(beanDesc, "More than one argument (#%d and #%d) left as delegating for Creator %s: only one allowed", new Object[] {
/*      */               
/*  832 */               Integer.valueOf(ix), Integer.valueOf(i), candidate });
/*      */       } 
/*      */     } 
/*  835 */     if (ix < 0) {
/*  836 */       ctxt.reportBadTypeDefinition(beanDesc, "No argument left as delegating for Creator %s: exactly one required", new Object[] { candidate });
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  841 */     if (argCount == 1) {
/*  842 */       _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
/*      */ 
/*      */       
/*  845 */       BeanPropertyDefinition paramDef = candidate.propertyDef(0);
/*  846 */       if (paramDef != null) {
/*  847 */         ((POJOPropertyBuilder)paramDef).removeConstructors();
/*      */       }
/*      */       return;
/*      */     } 
/*  851 */     creators.addDelegatingCreator(candidate.creator(), true, properties, ix);
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
/*      */   protected void _addExplicitPropertyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
/*  865 */     int paramCount = candidate.paramCount();
/*  866 */     SettableBeanProperty[] properties = new SettableBeanProperty[paramCount];
/*      */     
/*  868 */     for (int i = 0; i < paramCount; i++) {
/*  869 */       JacksonInject.Value injectId = candidate.injection(i);
/*  870 */       AnnotatedParameter param = candidate.parameter(i);
/*  871 */       PropertyName name = candidate.paramName(i);
/*  872 */       if (name == null) {
/*      */ 
/*      */         
/*  875 */         NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer((AnnotatedMember)param);
/*  876 */         if (unwrapper != null) {
/*  877 */           _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  883 */         name = candidate.findImplicitParamName(i);
/*  884 */         _validateNamedPropertyParameter(ctxt, beanDesc, candidate, i, name, injectId);
/*      */       } 
/*      */       
/*  887 */       properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*      */     } 
/*  889 */     creators.addPropertyCreator(candidate.creator(), true, properties);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void _addExplicitAnyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
/*  898 */     _addExplicitAnyCreator(ctxt, beanDesc, creators, candidate, ctxt
/*  899 */         .getConfig().getConstructorDetector());
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
/*      */   protected void _addExplicitAnyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate, ConstructorDetector ctorDetector) throws JsonMappingException {
/*      */     boolean useProps;
/*  914 */     if (1 != candidate.paramCount()) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  919 */       if (!ctorDetector.singleArgCreatorDefaultsToProperties()) {
/*  920 */         int oneNotInjected = candidate.findOnlyParamWithoutInjection();
/*  921 */         if (oneNotInjected >= 0)
/*      */         {
/*      */           
/*  924 */           if (ctorDetector.singleArgCreatorDefaultsToDelegating() || candidate
/*  925 */             .paramName(oneNotInjected) == null) {
/*  926 */             _addExplicitDelegatingCreator(ctxt, beanDesc, creators, candidate);
/*      */             return;
/*      */           } 
/*      */         }
/*      */       } 
/*  931 */       _addExplicitPropertyCreator(ctxt, beanDesc, creators, candidate);
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  936 */     AnnotatedParameter param = candidate.parameter(0);
/*  937 */     JacksonInject.Value injectId = candidate.injection(0);
/*  938 */     PropertyName paramName = null;
/*      */ 
/*      */     
/*  941 */     switch (ctorDetector.singleArgMode()) {
/*      */       case DELEGATING:
/*  943 */         useProps = false;
/*      */         break;
/*      */       case PROPERTIES:
/*  946 */         useProps = true;
/*      */ 
/*      */         
/*  949 */         paramName = candidate.paramName(0);
/*      */         
/*  951 */         if (paramName == null) {
/*  952 */           _validateNamedPropertyParameter(ctxt, beanDesc, candidate, 0, paramName, injectId);
/*      */         }
/*      */         break;
/*      */ 
/*      */       
/*      */       case REQUIRE_MODE:
/*  958 */         ctxt.reportBadTypeDefinition(beanDesc, "Single-argument constructor (%s) is annotated but no 'mode' defined; `CreatorDetector`configured with `SingleArgConstructor.REQUIRE_MODE`", new Object[] { candidate
/*      */ 
/*      */               
/*  961 */               .creator() });
/*      */         return;
/*      */ 
/*      */       
/*      */       default:
/*  966 */         paramDef = candidate.propertyDef(0);
/*      */         
/*  968 */         paramName = candidate.explicitParamName(0);
/*      */ 
/*      */         
/*  971 */         useProps = (paramName != null || injectId != null);
/*  972 */         if (!useProps && paramDef != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  979 */           paramName = candidate.paramName(0);
/*  980 */           useProps = (paramName != null && paramDef.couldSerialize());
/*      */         } 
/*      */         break;
/*      */     } 
/*      */     
/*  985 */     if (useProps) {
/*      */       
/*  987 */       SettableBeanProperty[] properties = { constructCreatorProperty(ctxt, beanDesc, paramName, 0, param, injectId) };
/*      */       
/*  989 */       creators.addPropertyCreator(candidate.creator(), true, properties);
/*      */       
/*      */       return;
/*      */     } 
/*  993 */     _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
/*      */ 
/*      */ 
/*      */     
/*  997 */     BeanPropertyDefinition paramDef = candidate.propertyDef(0);
/*  998 */     if (paramDef != null) {
/*  999 */       ((POJOPropertyBuilder)paramDef).removeConstructors();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean _checkIfCreatorPropertyBased(AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef) {
/* 1007 */     if ((propDef != null && propDef.isExplicitlyNamed()) || intr
/* 1008 */       .findInjectableValue((AnnotatedMember)creator.getParameter(0)) != null) {
/* 1009 */       return true;
/*      */     }
/* 1011 */     if (propDef != null) {
/*      */ 
/*      */       
/* 1014 */       String implName = propDef.getName();
/* 1015 */       if (implName != null && !implName.isEmpty() && 
/* 1016 */         propDef.couldSerialize()) {
/* 1017 */         return true;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1022 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedWithParams> implicitCtors) throws JsonMappingException {
/* 1030 */     AnnotatedWithParams found = null;
/* 1031 */     SettableBeanProperty[] foundProps = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1038 */     label34: for (AnnotatedWithParams ctor : implicitCtors) {
/* 1039 */       if (!vchecker.isCreatorVisible((AnnotatedMember)ctor)) {
/*      */         continue;
/*      */       }
/*      */       
/* 1043 */       int argCount = ctor.getParameterCount();
/* 1044 */       SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 1045 */       for (int i = 0; i < argCount; ) {
/* 1046 */         AnnotatedParameter param = ctor.getParameter(i);
/* 1047 */         PropertyName name = _findParamName(param, intr);
/*      */ 
/*      */         
/* 1050 */         if (name != null) { if (name.isEmpty()) {
/*      */             continue label34;
/*      */           }
/* 1053 */           properties[i] = constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null); i++; }
/*      */          continue label34;
/*      */       } 
/* 1056 */       if (found != null) {
/* 1057 */         found = null;
/*      */         break;
/*      */       } 
/* 1060 */       found = ctor;
/* 1061 */       foundProps = properties;
/*      */     } 
/*      */     
/* 1064 */     if (found != null) {
/* 1065 */       creators.addPropertyCreator(found, false, foundProps);
/* 1066 */       BasicBeanDescription bbd = (BasicBeanDescription)beanDesc;
/*      */       
/* 1068 */       for (SettableBeanProperty prop : foundProps) {
/* 1069 */         PropertyName pn = prop.getFullName();
/* 1070 */         if (!bbd.hasProperty(pn)) {
/* 1071 */           SimpleBeanPropertyDefinition simpleBeanPropertyDefinition = SimpleBeanPropertyDefinition.construct((MapperConfig)ctxt
/* 1072 */               .getConfig(), prop.getMember(), pn);
/* 1073 */           bbd.addProperty((BeanPropertyDefinition)simpleBeanPropertyDefinition);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _handleSingleArgumentCreator(CreatorCollector creators, AnnotatedWithParams ctor, boolean isCreator, boolean isVisible) {
/* 1083 */     Class<?> type = ctor.getRawParameterType(0);
/* 1084 */     if (type == String.class || type == CLASS_CHAR_SEQUENCE) {
/* 1085 */       if (isCreator || isVisible) {
/* 1086 */         creators.addStringCreator(ctor, isCreator);
/*      */       }
/* 1088 */       return true;
/*      */     } 
/* 1090 */     if (type == int.class || type == Integer.class) {
/* 1091 */       if (isCreator || isVisible) {
/* 1092 */         creators.addIntCreator(ctor, isCreator);
/*      */       }
/* 1094 */       return true;
/*      */     } 
/* 1096 */     if (type == long.class || type == Long.class) {
/* 1097 */       if (isCreator || isVisible) {
/* 1098 */         creators.addLongCreator(ctor, isCreator);
/*      */       }
/* 1100 */       return true;
/*      */     } 
/* 1102 */     if (type == double.class || type == Double.class) {
/* 1103 */       if (isCreator || isVisible) {
/* 1104 */         creators.addDoubleCreator(ctor, isCreator);
/*      */       }
/* 1106 */       return true;
/*      */     } 
/* 1108 */     if (type == boolean.class || type == Boolean.class) {
/* 1109 */       if (isCreator || isVisible) {
/* 1110 */         creators.addBooleanCreator(ctor, isCreator);
/*      */       }
/* 1112 */       return true;
/*      */     } 
/* 1114 */     if (type == BigInteger.class && (
/* 1115 */       isCreator || isVisible)) {
/* 1116 */       creators.addBigIntegerCreator(ctor, isCreator);
/*      */     }
/*      */     
/* 1119 */     if (type == BigDecimal.class && (
/* 1120 */       isCreator || isVisible)) {
/* 1121 */       creators.addBigDecimalCreator(ctor, isCreator);
/*      */     }
/*      */ 
/*      */     
/* 1125 */     if (isCreator) {
/* 1126 */       creators.addDelegatingCreator(ctor, isCreator, null, 0);
/* 1127 */       return true;
/*      */     } 
/* 1129 */     return false;
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
/*      */   protected void _validateNamedPropertyParameter(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCandidate candidate, int paramIndex, PropertyName name, JacksonInject.Value injectId) throws JsonMappingException {
/* 1143 */     if (name == null && injectId == null) {
/* 1144 */       ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of constructor %s has no property name (and is not Injectable): can not use as property-based Creator", new Object[] {
/*      */             
/* 1146 */             Integer.valueOf(paramIndex), candidate
/*      */           });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportUnwrappedCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedParameter param) throws JsonMappingException {
/* 1156 */     ctxt.reportBadTypeDefinition(beanDesc, "Cannot define Creator parameter %d as `@JsonUnwrapped`: combination not yet supported", new Object[] {
/*      */           
/* 1158 */           Integer.valueOf(param.getIndex())
/*      */         });
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
/*      */   protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, JacksonInject.Value injectable) throws JsonMappingException {
/*      */     PropertyName wrapperName;
/* 1172 */     DeserializationConfig config = ctxt.getConfig();
/* 1173 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*      */ 
/*      */ 
/*      */     
/* 1177 */     if (intr == null) {
/* 1178 */       metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
/* 1179 */       wrapperName = null;
/*      */     } else {
/* 1181 */       Boolean b = intr.hasRequiredMarker((AnnotatedMember)param);
/* 1182 */       String desc = intr.findPropertyDescription((Annotated)param);
/* 1183 */       Integer idx = intr.findPropertyIndex((Annotated)param);
/* 1184 */       String def = intr.findPropertyDefaultValue((Annotated)param);
/* 1185 */       metadata = PropertyMetadata.construct(b, desc, idx, def);
/* 1186 */       wrapperName = intr.findWrapperName((Annotated)param);
/*      */     } 
/*      */     
/* 1189 */     JavaType type = resolveMemberAndTypeAnnotations(ctxt, (AnnotatedMember)param, param.getType());
/* 1190 */     BeanProperty.Std property = new BeanProperty.Std(name, type, wrapperName, (AnnotatedMember)param, metadata);
/*      */ 
/*      */     
/* 1193 */     TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
/*      */     
/* 1195 */     if (typeDeser == null) {
/* 1196 */       typeDeser = findTypeDeserializer(config, type);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1201 */     PropertyMetadata metadata = _getSetterInfo(ctxt, (BeanProperty)property, metadata);
/*      */ 
/*      */ 
/*      */     
/* 1205 */     SettableBeanProperty prop = CreatorProperty.construct(name, type, property.getWrapperName(), typeDeser, beanDesc
/* 1206 */         .getClassAnnotations(), param, index, injectable, metadata);
/*      */     
/* 1208 */     JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, (Annotated)param);
/* 1209 */     if (deser == null) {
/* 1210 */       deser = (JsonDeserializer)type.getValueHandler();
/*      */     }
/* 1212 */     if (deser != null) {
/*      */       
/* 1214 */       deser = ctxt.handlePrimaryContextualization(deser, (BeanProperty)prop, type);
/* 1215 */       prop = prop.withValueDeserializer(deser);
/*      */     } 
/* 1217 */     return prop;
/*      */   }
/*      */ 
/*      */   
/*      */   private PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 1222 */     if (intr != null) {
/* 1223 */       PropertyName name = intr.findNameForDeserialization((Annotated)param);
/* 1224 */       if (name != null)
/*      */       {
/*      */         
/* 1227 */         if (!name.isEmpty()) {
/* 1228 */           return name;
/*      */         }
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1234 */       String str = intr.findImplicitPropertyName((AnnotatedMember)param);
/* 1235 */       if (str != null && !str.isEmpty()) {
/* 1236 */         return PropertyName.construct(str);
/*      */       }
/*      */     } 
/* 1239 */     return null;
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
/*      */   protected PropertyMetadata _getSetterInfo(DeserializationContext ctxt, BeanProperty prop, PropertyMetadata metadata) {
/* 1251 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 1252 */     DeserializationConfig config = ctxt.getConfig();
/*      */     
/* 1254 */     boolean needMerge = true;
/* 1255 */     Nulls valueNulls = null;
/* 1256 */     Nulls contentNulls = null;
/*      */ 
/*      */ 
/*      */     
/* 1260 */     AnnotatedMember prim = prop.getMember();
/*      */     
/* 1262 */     if (prim != null) {
/*      */       
/* 1264 */       if (intr != null) {
/* 1265 */         JsonSetter.Value setterInfo = intr.findSetterInfo((Annotated)prim);
/* 1266 */         if (setterInfo != null) {
/* 1267 */           valueNulls = setterInfo.nonDefaultValueNulls();
/* 1268 */           contentNulls = setterInfo.nonDefaultContentNulls();
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1273 */       if (needMerge || valueNulls == null || contentNulls == null) {
/* 1274 */         ConfigOverride co = config.getConfigOverride(prop.getType().getRawClass());
/* 1275 */         JsonSetter.Value setterInfo = co.getSetterInfo();
/* 1276 */         if (setterInfo != null) {
/* 1277 */           if (valueNulls == null) {
/* 1278 */             valueNulls = setterInfo.nonDefaultValueNulls();
/*      */           }
/* 1280 */           if (contentNulls == null) {
/* 1281 */             contentNulls = setterInfo.nonDefaultContentNulls();
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 1286 */     if (needMerge || valueNulls == null || contentNulls == null) {
/* 1287 */       JsonSetter.Value setterInfo = config.getDefaultSetterInfo();
/* 1288 */       if (valueNulls == null) {
/* 1289 */         valueNulls = setterInfo.nonDefaultValueNulls();
/*      */       }
/* 1291 */       if (contentNulls == null) {
/* 1292 */         contentNulls = setterInfo.nonDefaultContentNulls();
/*      */       }
/*      */     } 
/* 1295 */     if (valueNulls != null || contentNulls != null) {
/* 1296 */       metadata = metadata.withNulls(valueNulls, contentNulls);
/*      */     }
/* 1298 */     return metadata;
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
/*      */   public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
/*      */     ObjectArrayDeserializer objectArrayDeserializer;
/*      */     JsonDeserializer<?> jsonDeserializer1;
/* 1312 */     DeserializationConfig config = ctxt.getConfig();
/* 1313 */     JavaType elemType = type.getContentType();
/*      */ 
/*      */     
/* 1316 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer<Object>)elemType.getValueHandler();
/*      */     
/* 1318 */     TypeDeserializer elemTypeDeser = (TypeDeserializer)elemType.getTypeHandler();
/*      */     
/* 1320 */     if (elemTypeDeser == null) {
/* 1321 */       elemTypeDeser = findTypeDeserializer(config, elemType);
/*      */     }
/*      */     
/* 1324 */     JsonDeserializer<?> deser = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
/*      */     
/* 1326 */     if (deser == null) {
/* 1327 */       if (contentDeser == null) {
/* 1328 */         Class<?> raw = elemType.getRawClass();
/* 1329 */         if (elemType.isPrimitive()) {
/* 1330 */           return PrimitiveArrayDeserializers.forType(raw);
/*      */         }
/* 1332 */         if (raw == String.class) {
/* 1333 */           return (JsonDeserializer<?>)StringArrayDeserializer.instance;
/*      */         }
/*      */       } 
/* 1336 */       objectArrayDeserializer = new ObjectArrayDeserializer((JavaType)type, contentDeser, elemTypeDeser);
/*      */     } 
/*      */     
/* 1339 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1340 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1341 */         jsonDeserializer1 = mod.modifyArrayDeserializer(config, type, beanDesc, (JsonDeserializer<?>)objectArrayDeserializer);
/*      */       }
/*      */     }
/* 1344 */     return jsonDeserializer1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
/*      */     EnumSetDeserializer enumSetDeserializer;
/*      */     CollectionDeserializer collectionDeserializer;
/*      */     JsonDeserializer<?> jsonDeserializer1;
/* 1358 */     JavaType contentType = type.getContentType();
/*      */     
/* 1360 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer<Object>)contentType.getValueHandler();
/* 1361 */     DeserializationConfig config = ctxt.getConfig();
/*      */ 
/*      */     
/* 1364 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/*      */     
/* 1366 */     if (contentTypeDeser == null) {
/* 1367 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/*      */     
/* 1370 */     JsonDeserializer<?> deser = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*      */     
/* 1372 */     if (deser == null) {
/* 1373 */       Class<?> collectionClass = type.getRawClass();
/* 1374 */       if (contentDeser == null)
/*      */       {
/* 1376 */         if (EnumSet.class.isAssignableFrom(collectionClass)) {
/* 1377 */           enumSetDeserializer = new EnumSetDeserializer(contentType, null);
/*      */         }
/*      */       }
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
/* 1391 */     if (enumSetDeserializer == null) {
/* 1392 */       if (type.isInterface() || type.isAbstract()) {
/* 1393 */         CollectionType implType = _mapAbstractCollectionType((JavaType)type, config);
/* 1394 */         if (implType == null) {
/*      */           
/* 1396 */           if (type.getTypeHandler() == null) {
/* 1397 */             throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Collection type " + type);
/*      */           }
/* 1399 */           jsonDeserializer1 = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*      */         } else {
/* 1401 */           type = implType;
/*      */           
/* 1403 */           beanDesc = config.introspectForCreation((JavaType)type);
/*      */         } 
/*      */       } 
/* 1406 */       if (jsonDeserializer1 == null) {
/* 1407 */         ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/* 1408 */         if (!inst.canCreateUsingDefault()) {
/*      */           
/* 1410 */           if (type.hasRawClass(ArrayBlockingQueue.class)) {
/* 1411 */             return (JsonDeserializer<?>)new ArrayBlockingQueueDeserializer((JavaType)type, contentDeser, contentTypeDeser, inst);
/*      */           }
/*      */           
/* 1414 */           JsonDeserializer<?> jsonDeserializer = JavaUtilCollectionsDeserializers.findForCollection(ctxt, (JavaType)type);
/* 1415 */           if (jsonDeserializer != null) {
/* 1416 */             return jsonDeserializer;
/*      */           }
/*      */         } 
/*      */         
/* 1420 */         if (contentType.hasRawClass(String.class)) {
/*      */           
/* 1422 */           StringCollectionDeserializer stringCollectionDeserializer = new StringCollectionDeserializer((JavaType)type, contentDeser, inst);
/*      */         } else {
/* 1424 */           collectionDeserializer = new CollectionDeserializer((JavaType)type, contentDeser, contentTypeDeser, inst);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1429 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1430 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1431 */         jsonDeserializer1 = mod.modifyCollectionDeserializer(config, type, beanDesc, (JsonDeserializer<?>)collectionDeserializer);
/*      */       }
/*      */     }
/* 1434 */     return jsonDeserializer1;
/*      */   }
/*      */ 
/*      */   
/*      */   protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
/* 1439 */     Class<?> collectionClass = ContainerDefaultMappings.findCollectionFallback(type);
/* 1440 */     if (collectionClass != null) {
/* 1441 */       return (CollectionType)config.getTypeFactory()
/* 1442 */         .constructSpecializedType(type, collectionClass, true);
/*      */     }
/* 1444 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/* 1453 */     JavaType contentType = type.getContentType();
/*      */     
/* 1455 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer<Object>)contentType.getValueHandler();
/* 1456 */     DeserializationConfig config = ctxt.getConfig();
/*      */ 
/*      */     
/* 1459 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/*      */     
/* 1461 */     if (contentTypeDeser == null) {
/* 1462 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/* 1464 */     JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*      */     
/* 1466 */     if (deser != null)
/*      */     {
/* 1468 */       if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1469 */         for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1470 */           deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
/*      */         }
/*      */       }
/*      */     }
/* 1474 */     return deser;
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
/*      */   public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
/*      */     MapDeserializer mapDeserializer;
/*      */     JsonDeserializer<?> jsonDeserializer1;
/* 1488 */     DeserializationConfig config = ctxt.getConfig();
/* 1489 */     JavaType keyType = type.getKeyType();
/* 1490 */     JavaType contentType = type.getContentType();
/*      */ 
/*      */ 
/*      */     
/* 1494 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer<Object>)contentType.getValueHandler();
/*      */ 
/*      */     
/* 1497 */     KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
/*      */     
/* 1499 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/*      */     
/* 1501 */     if (contentTypeDeser == null) {
/* 1502 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/*      */ 
/*      */     
/* 1506 */     JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*      */ 
/*      */     
/* 1509 */     if (deser == null) {
/*      */       EnumMapDeserializer enumMapDeserializer;
/* 1511 */       Class<?> mapClass = type.getRawClass();
/* 1512 */       if (EnumMap.class.isAssignableFrom(mapClass)) {
/*      */         ValueInstantiator inst;
/*      */ 
/*      */ 
/*      */         
/* 1517 */         if (mapClass == EnumMap.class) {
/* 1518 */           inst = null;
/*      */         } else {
/* 1520 */           inst = findValueInstantiator(ctxt, beanDesc);
/*      */         } 
/* 1522 */         if (!keyType.isEnumImplType()) {
/* 1523 */           throw new IllegalArgumentException("Cannot construct EnumMap; generic (key) type not available");
/*      */         }
/* 1525 */         enumMapDeserializer = new EnumMapDeserializer((JavaType)type, inst, null, contentDeser, contentTypeDeser, null);
/*      */       } 
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
/* 1540 */       if (enumMapDeserializer == null) {
/* 1541 */         if (type.isInterface() || type.isAbstract()) {
/* 1542 */           MapType fallback = _mapAbstractMapType((JavaType)type, config);
/* 1543 */           if (fallback != null) {
/* 1544 */             type = fallback;
/* 1545 */             mapClass = type.getRawClass();
/*      */             
/* 1547 */             beanDesc = config.introspectForCreation((JavaType)type);
/*      */           } else {
/*      */             
/* 1550 */             if (type.getTypeHandler() == null) {
/* 1551 */               throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Map type " + type);
/*      */             }
/* 1553 */             jsonDeserializer1 = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*      */           } 
/*      */         } else {
/*      */           
/* 1557 */           jsonDeserializer1 = JavaUtilCollectionsDeserializers.findForMap(ctxt, (JavaType)type);
/* 1558 */           if (jsonDeserializer1 != null) {
/* 1559 */             return jsonDeserializer1;
/*      */           }
/*      */         } 
/* 1562 */         if (jsonDeserializer1 == null) {
/* 1563 */           ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1568 */           MapDeserializer md = new MapDeserializer((JavaType)type, inst, keyDes, contentDeser, contentTypeDeser);
/* 1569 */           JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc
/* 1570 */               .getClassInfo());
/*      */           
/* 1572 */           Set<String> ignored = (ignorals == null) ? null : ignorals.findIgnoredForDeserialization();
/* 1573 */           md.setIgnorableProperties(ignored);
/* 1574 */           JsonIncludeProperties.Value inclusions = config.getDefaultPropertyInclusions(Map.class, beanDesc
/* 1575 */               .getClassInfo());
/* 1576 */           Set<String> included = (inclusions == null) ? null : inclusions.getIncluded();
/* 1577 */           md.setIncludableProperties(included);
/* 1578 */           mapDeserializer = md;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1582 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1583 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1584 */         jsonDeserializer1 = mod.modifyMapDeserializer(config, type, beanDesc, (JsonDeserializer<?>)mapDeserializer);
/*      */       }
/*      */     }
/* 1587 */     return jsonDeserializer1;
/*      */   }
/*      */ 
/*      */   
/*      */   protected MapType _mapAbstractMapType(JavaType type, DeserializationConfig config) {
/* 1592 */     Class<?> mapClass = ContainerDefaultMappings.findMapFallback(type);
/* 1593 */     if (mapClass != null) {
/* 1594 */       return (MapType)config.getTypeFactory()
/* 1595 */         .constructSpecializedType(type, mapClass, true);
/*      */     }
/* 1597 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/* 1606 */     JavaType keyType = type.getKeyType();
/* 1607 */     JavaType contentType = type.getContentType();
/* 1608 */     DeserializationConfig config = ctxt.getConfig();
/*      */ 
/*      */ 
/*      */     
/* 1612 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer<Object>)contentType.getValueHandler();
/*      */ 
/*      */     
/* 1615 */     KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1622 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/*      */     
/* 1624 */     if (contentTypeDeser == null) {
/* 1625 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/* 1627 */     JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*      */     
/* 1629 */     if (deser != null)
/*      */     {
/* 1631 */       if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1632 */         for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1633 */           deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
/*      */         }
/*      */       }
/*      */     }
/* 1637 */     return deser;
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
/*      */   public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*      */     EnumDeserializer enumDeserializer;
/*      */     JsonDeserializer<?> jsonDeserializer1;
/* 1654 */     DeserializationConfig config = ctxt.getConfig();
/* 1655 */     Class<?> enumClass = type.getRawClass();
/*      */     
/* 1657 */     JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*      */     
/* 1659 */     if (deser == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1665 */       if (enumClass == Enum.class) {
/* 1666 */         return AbstractDeserializer.constructForNonPOJO(beanDesc);
/*      */       }
/*      */       
/* 1669 */       ValueInstantiator valueInstantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
/*      */       
/* 1671 */       SettableBeanProperty[] creatorProps = (valueInstantiator == null) ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/*      */       
/* 1673 */       for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/* 1674 */         if (_hasCreatorAnnotation(ctxt, (Annotated)factory)) {
/* 1675 */           if (factory.getParameterCount() == 0) {
/* 1676 */             deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
/*      */             break;
/*      */           } 
/* 1679 */           Class<?> returnType = factory.getRawReturnType();
/*      */           
/* 1681 */           if (!returnType.isAssignableFrom(enumClass)) {
/* 1682 */             ctxt.reportBadDefinition(type, String.format("Invalid `@JsonCreator` annotated Enum factory method [%s]: needs to return compatible type", new Object[] { factory
/*      */                     
/* 1684 */                     .toString() }));
/*      */           }
/* 1686 */           deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */       
/* 1692 */       if (deser == null)
/*      */       {
/*      */         
/* 1695 */         enumDeserializer = new EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor()), Boolean.valueOf(config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)));
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1700 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1701 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1702 */         jsonDeserializer1 = mod.modifyEnumDeserializer(config, type, beanDesc, (JsonDeserializer<?>)enumDeserializer);
/*      */       }
/*      */     }
/* 1705 */     return jsonDeserializer1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
/* 1714 */     Class<? extends JsonNode> nodeClass = nodeType.getRawClass();
/*      */     
/* 1716 */     JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
/*      */     
/* 1718 */     if (custom != null) {
/* 1719 */       return custom;
/*      */     }
/* 1721 */     return JsonNodeDeserializer.getDeserializer(nodeClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc) throws JsonMappingException {
/* 1729 */     JavaType contentType = type.getContentType();
/*      */     
/* 1731 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer<Object>)contentType.getValueHandler();
/* 1732 */     DeserializationConfig config = ctxt.getConfig();
/*      */     
/* 1734 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/* 1735 */     if (contentTypeDeser == null) {
/* 1736 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/* 1738 */     JsonDeserializer<?> deser = _findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*      */ 
/*      */     
/* 1741 */     if (deser == null)
/*      */     {
/* 1743 */       if (type.isTypeOrSubTypeOf(AtomicReference.class)) {
/* 1744 */         ValueInstantiator inst; Class<?> rawType = type.getRawClass();
/*      */         
/* 1746 */         if (rawType == AtomicReference.class) {
/* 1747 */           inst = null;
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */           
/* 1753 */           inst = findValueInstantiator(ctxt, beanDesc);
/*      */         } 
/* 1755 */         return (JsonDeserializer<?>)new AtomicReferenceDeserializer((JavaType)type, inst, contentTypeDeser, contentDeser);
/*      */       } 
/*      */     }
/* 1758 */     if (deser != null)
/*      */     {
/* 1760 */       if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1761 */         for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1762 */           deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser);
/*      */         }
/*      */       }
/*      */     }
/* 1766 */     return deser;
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
/*      */   public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
/* 1780 */     BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
/* 1781 */     AnnotatedClass ac = bean.getClassInfo();
/* 1782 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 1783 */     TypeResolverBuilder<?> b = ai.findTypeResolver((MapperConfig)config, ac, baseType);
/*      */ 
/*      */ 
/*      */     
/* 1787 */     Collection<NamedType> subtypes = null;
/* 1788 */     if (b == null) {
/* 1789 */       b = config.getDefaultTyper(baseType);
/* 1790 */       if (b == null) {
/* 1791 */         return null;
/*      */       }
/*      */     } else {
/* 1794 */       subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId((MapperConfig)config, ac);
/*      */     } 
/*      */ 
/*      */     
/* 1798 */     if (b.getDefaultImpl() == null && baseType.isAbstract()) {
/* 1799 */       JavaType defaultType = mapAbstractType(config, baseType);
/*      */ 
/*      */       
/* 1802 */       if (defaultType != null && !defaultType.hasRawClass(baseType.getRawClass())) {
/* 1803 */         b = b.withDefaultImpl(defaultType.getRawClass());
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/* 1809 */       return b.buildTypeDeserializer(config, baseType, subtypes);
/* 1810 */     } catch (IllegalArgumentException|IllegalStateException e0) {
/* 1811 */       throw InvalidDefinitionException.from((JsonParser)null, 
/* 1812 */           ClassUtil.exceptionMessage(e0), baseType)
/* 1813 */         .withCause(e0);
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
/*      */   protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 1826 */     return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
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
/*      */   public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/* 1840 */     DeserializationConfig config = ctxt.getConfig();
/* 1841 */     BeanDescription beanDesc = null;
/* 1842 */     KeyDeserializer deser = null;
/* 1843 */     if (this._factoryConfig.hasKeyDeserializers()) {
/* 1844 */       beanDesc = config.introspectClassAnnotations(type);
/* 1845 */       for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
/* 1846 */         deser = d.findKeyDeserializer(type, config, beanDesc);
/* 1847 */         if (deser != null) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1854 */     if (deser == null) {
/*      */       
/* 1856 */       if (beanDesc == null) {
/* 1857 */         beanDesc = config.introspectClassAnnotations(type.getRawClass());
/*      */       }
/* 1859 */       deser = findKeyDeserializerFromAnnotation(ctxt, (Annotated)beanDesc.getClassInfo());
/* 1860 */       if (deser == null) {
/* 1861 */         if (type.isEnumType()) {
/* 1862 */           deser = _createEnumKeyDeserializer(ctxt, type);
/*      */         } else {
/* 1864 */           deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/* 1869 */     if (deser != null && 
/* 1870 */       this._factoryConfig.hasDeserializerModifiers()) {
/* 1871 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1872 */         deser = mod.modifyKeyDeserializer(config, type, deser);
/*      */       }
/*      */     }
/*      */     
/* 1876 */     return deser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/* 1883 */     DeserializationConfig config = ctxt.getConfig();
/* 1884 */     Class<?> enumClass = type.getRawClass();
/*      */     
/* 1886 */     BeanDescription beanDesc = config.introspect(type);
/*      */     
/* 1888 */     KeyDeserializer des = findKeyDeserializerFromAnnotation(ctxt, (Annotated)beanDesc.getClassInfo());
/* 1889 */     if (des != null) {
/* 1890 */       return des;
/*      */     }
/*      */     
/* 1893 */     JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/* 1894 */     if (custom != null) {
/* 1895 */       return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
/*      */     }
/* 1897 */     JsonDeserializer<?> valueDesForKey = findDeserializerFromAnnotation(ctxt, (Annotated)beanDesc.getClassInfo());
/* 1898 */     if (valueDesForKey != null) {
/* 1899 */       return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
/*      */     }
/*      */     
/* 1902 */     EnumResolver enumRes = constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor());
/*      */ 
/*      */     
/* 1905 */     for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/* 1906 */       if (_hasCreatorAnnotation(ctxt, (Annotated)factory)) {
/* 1907 */         int argCount = factory.getParameterCount();
/* 1908 */         if (argCount == 1) {
/* 1909 */           Class<?> returnType = factory.getRawReturnType();
/*      */           
/* 1911 */           if (returnType.isAssignableFrom(enumClass)) {
/*      */             
/* 1913 */             if (factory.getRawParameterType(0) != String.class) {
/*      */               continue;
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1920 */             if (config.canOverrideAccessModifiers()) {
/* 1921 */               ClassUtil.checkAndFixAccess(factory.getMember(), ctxt
/* 1922 */                   .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*      */             }
/* 1924 */             return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
/*      */           } 
/*      */         } 
/* 1927 */         throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass
/* 1928 */             .getName() + ")");
/*      */       } 
/*      */     } 
/*      */     
/* 1932 */     return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
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
/*      */   public boolean hasExplicitDeserializerFor(DeserializationConfig config, Class<?> valueType) {
/* 1949 */     while (valueType.isArray()) {
/* 1950 */       valueType = valueType.getComponentType();
/*      */     }
/*      */ 
/*      */     
/* 1954 */     if (Enum.class.isAssignableFrom(valueType)) {
/* 1955 */       return true;
/*      */     }
/*      */     
/* 1958 */     String clsName = valueType.getName();
/* 1959 */     if (clsName.startsWith("java.")) {
/* 1960 */       if (Collection.class.isAssignableFrom(valueType)) {
/* 1961 */         return true;
/*      */       }
/* 1963 */       if (Map.class.isAssignableFrom(valueType)) {
/* 1964 */         return true;
/*      */       }
/* 1966 */       if (Number.class.isAssignableFrom(valueType)) {
/* 1967 */         return (NumberDeserializers.find(valueType, clsName) != null);
/*      */       }
/* 1969 */       if (JdkDeserializers.hasDeserializerFor(valueType) || valueType == CLASS_STRING || valueType == Boolean.class || valueType == EnumMap.class || valueType == AtomicReference.class)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1975 */         return true;
/*      */       }
/* 1977 */       if (DateDeserializers.hasDeserializerFor(valueType))
/* 1978 */         return true; 
/*      */     } else {
/* 1980 */       if (clsName.startsWith("com.fasterxml.")) {
/* 1981 */         return (JsonNode.class.isAssignableFrom(valueType) || valueType == TokenBuffer.class);
/*      */       }
/*      */       
/* 1984 */       return OptionalHandlerFactory.instance.hasDeserializerFor(valueType);
/*      */     } 
/* 1986 */     return false;
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
/*      */ 
/*      */   
/*      */   public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
/* 2012 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 2013 */     TypeResolverBuilder<?> b = ai.findPropertyTypeResolver((MapperConfig)config, annotated, baseType);
/*      */     
/* 2015 */     if (b == null) {
/* 2016 */       return findTypeDeserializer(config, baseType);
/*      */     }
/*      */     
/* 2019 */     Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId((MapperConfig)config, annotated, baseType);
/*      */     
/*      */     try {
/* 2022 */       return b.buildTypeDeserializer(config, baseType, subtypes);
/* 2023 */     } catch (IllegalArgumentException|IllegalStateException e0) {
/* 2024 */       throw InvalidDefinitionException.from((JsonParser)null, 
/* 2025 */           ClassUtil.exceptionMessage(e0), baseType)
/* 2026 */         .withCause(e0);
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
/*      */ 
/*      */   
/*      */   public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
/* 2045 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 2046 */     TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver((MapperConfig)config, propertyEntity, containerType);
/* 2047 */     JavaType contentType = containerType.getContentType();
/*      */     
/* 2049 */     if (b == null) {
/* 2050 */       return findTypeDeserializer(config, contentType);
/*      */     }
/*      */     
/* 2053 */     Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId((MapperConfig)config, propertyEntity, contentType);
/*      */     
/* 2055 */     return b.buildTypeDeserializer(config, contentType, subtypes);
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
/*      */   public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 2069 */     Class<?> rawType = type.getRawClass();
/*      */     
/* 2071 */     if (rawType == CLASS_OBJECT || rawType == CLASS_SERIALIZABLE) {
/*      */       JavaType lt, mt;
/* 2073 */       DeserializationConfig config = ctxt.getConfig();
/*      */ 
/*      */       
/* 2076 */       if (this._factoryConfig.hasAbstractTypeResolvers()) {
/* 2077 */         lt = _findRemappedType(config, List.class);
/* 2078 */         mt = _findRemappedType(config, Map.class);
/*      */       } else {
/* 2080 */         lt = mt = null;
/*      */       } 
/* 2082 */       return (JsonDeserializer<?>)new UntypedObjectDeserializer(lt, mt);
/*      */     } 
/*      */     
/* 2085 */     if (rawType == CLASS_STRING || rawType == CLASS_CHAR_SEQUENCE) {
/* 2086 */       return (JsonDeserializer<?>)StringDeserializer.instance;
/*      */     }
/* 2088 */     if (rawType == CLASS_ITERABLE) {
/*      */       
/* 2090 */       TypeFactory tf = ctxt.getTypeFactory();
/* 2091 */       JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
/* 2092 */       JavaType elemType = (tps == null || tps.length != 1) ? TypeFactory.unknownType() : tps[0];
/* 2093 */       CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
/*      */       
/* 2095 */       return createCollectionDeserializer(ctxt, ct, beanDesc);
/*      */     } 
/* 2097 */     if (rawType == CLASS_MAP_ENTRY) {
/*      */       
/* 2099 */       JavaType kt = type.containedTypeOrUnknown(0);
/* 2100 */       JavaType vt = type.containedTypeOrUnknown(1);
/* 2101 */       TypeDeserializer vts = (TypeDeserializer)vt.getTypeHandler();
/* 2102 */       if (vts == null) {
/* 2103 */         vts = findTypeDeserializer(ctxt.getConfig(), vt);
/*      */       }
/* 2105 */       JsonDeserializer<Object> valueDeser = (JsonDeserializer<Object>)vt.getValueHandler();
/* 2106 */       KeyDeserializer keyDes = (KeyDeserializer)kt.getValueHandler();
/* 2107 */       return (JsonDeserializer<?>)new MapEntryDeserializer(type, keyDes, valueDeser, vts);
/*      */     } 
/* 2109 */     String clsName = rawType.getName();
/* 2110 */     if (rawType.isPrimitive() || clsName.startsWith("java.")) {
/*      */       
/* 2112 */       JsonDeserializer<?> jsonDeserializer = NumberDeserializers.find(rawType, clsName);
/* 2113 */       if (jsonDeserializer == null) {
/* 2114 */         jsonDeserializer = DateDeserializers.find(rawType, clsName);
/*      */       }
/* 2116 */       if (jsonDeserializer != null) {
/* 2117 */         return jsonDeserializer;
/*      */       }
/*      */     } 
/*      */     
/* 2121 */     if (rawType == TokenBuffer.class) {
/* 2122 */       return (JsonDeserializer<?>)new TokenBufferDeserializer();
/*      */     }
/* 2124 */     JsonDeserializer<?> deser = findOptionalStdDeserializer(ctxt, type, beanDesc);
/* 2125 */     if (deser != null) {
/* 2126 */       return deser;
/*      */     }
/* 2128 */     return JdkDeserializers.find(rawType, clsName);
/*      */   }
/*      */   
/*      */   protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
/* 2132 */     JavaType type = mapAbstractType(config, config.constructType(rawType));
/* 2133 */     return (type == null || type.hasRawClass(rawType)) ? null : type;
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
/*      */   protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/* 2146 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 2147 */       JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
/* 2148 */       if (deser != null) {
/* 2149 */         return deser;
/*      */       }
/*      */     } 
/* 2152 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
/* 2160 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 2161 */       JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
/*      */       
/* 2163 */       if (deser != null) {
/* 2164 */         return deser;
/*      */       }
/*      */     } 
/* 2167 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/* 2175 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 2176 */       JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
/* 2177 */       if (deser != null) {
/* 2178 */         return (JsonDeserializer)deser;
/*      */       }
/*      */     } 
/* 2181 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 2189 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 2190 */       JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 2192 */       if (deser != null) {
/* 2193 */         return deser;
/*      */       }
/*      */     } 
/* 2196 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 2204 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 2205 */       JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 2207 */       if (deser != null) {
/* 2208 */         return deser;
/*      */       }
/*      */     } 
/* 2211 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 2219 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 2220 */       JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 2222 */       if (deser != null) {
/* 2223 */         return deser;
/*      */       }
/*      */     } 
/* 2226 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/* 2233 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 2234 */       JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
/* 2235 */       if (deser != null) {
/* 2236 */         return deser;
/*      */       }
/*      */     } 
/* 2239 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 2248 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 2249 */       JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 2251 */       if (deser != null) {
/* 2252 */         return deser;
/*      */       }
/*      */     } 
/* 2255 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 2264 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 2265 */       JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 2267 */       if (deser != null) {
/* 2268 */         return deser;
/*      */       }
/*      */     } 
/* 2271 */     return null;
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
/*      */   protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/* 2292 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 2293 */     if (intr != null) {
/* 2294 */       Object deserDef = intr.findDeserializer(ann);
/* 2295 */       if (deserDef != null) {
/* 2296 */         return ctxt.deserializerInstance(ann, deserDef);
/*      */       }
/*      */     } 
/* 2299 */     return null;
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
/*      */   protected KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/* 2311 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 2312 */     if (intr != null) {
/* 2313 */       Object deserDef = intr.findKeyDeserializer(ann);
/* 2314 */       if (deserDef != null) {
/* 2315 */         return ctxt.keyDeserializerInstance(ann, deserDef);
/*      */       }
/*      */     } 
/* 2318 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> findContentDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/* 2328 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 2329 */     if (intr != null) {
/* 2330 */       Object deserDef = intr.findContentDeserializer(ann);
/* 2331 */       if (deserDef != null) {
/* 2332 */         return ctxt.deserializerInstance(ann, deserDef);
/*      */       }
/*      */     } 
/* 2335 */     return null;
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
/*      */   protected JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type) throws JsonMappingException {
/*      */     MapLikeType mapLikeType;
/* 2351 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 2352 */     if (intr == null) {
/* 2353 */       return type;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2359 */     if (type.isMapLikeType()) {
/* 2360 */       JavaType keyType = type.getKeyType();
/* 2361 */       if (keyType != null) {
/* 2362 */         Object kdDef = intr.findKeyDeserializer((Annotated)member);
/* 2363 */         KeyDeserializer kd = ctxt.keyDeserializerInstance((Annotated)member, kdDef);
/* 2364 */         if (kd != null) {
/* 2365 */           mapLikeType = ((MapLikeType)type).withKeyValueHandler(kd);
/* 2366 */           keyType = mapLikeType.getKeyType();
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 2371 */     if (mapLikeType.hasContentType()) {
/* 2372 */       Object cdDef = intr.findContentDeserializer((Annotated)member);
/* 2373 */       JsonDeserializer<?> cd = ctxt.deserializerInstance((Annotated)member, cdDef);
/* 2374 */       if (cd != null) {
/* 2375 */         javaType = mapLikeType.withContentValueHandler(cd);
/*      */       }
/* 2377 */       TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt
/* 2378 */           .getConfig(), javaType, member);
/* 2379 */       if (contentTypeDeser != null) {
/* 2380 */         javaType = javaType.withContentTypeHandler(contentTypeDeser);
/*      */       }
/*      */     } 
/* 2383 */     TypeDeserializer valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), javaType, member);
/*      */     
/* 2385 */     if (valueTypeDeser != null) {
/* 2386 */       javaType = javaType.withTypeHandler(valueTypeDeser);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2394 */     JavaType javaType = intr.refineDeserializationType((MapperConfig)ctxt.getConfig(), (Annotated)member, javaType);
/* 2395 */     return javaType;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMember jsonValueAccessor) {
/* 2401 */     if (jsonValueAccessor != null) {
/* 2402 */       if (config.canOverrideAccessModifiers()) {
/* 2403 */         ClassUtil.checkAndFixAccess(jsonValueAccessor.getMember(), config
/* 2404 */             .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*      */       }
/* 2406 */       return EnumResolver.constructUsingMethod(config, enumClass, jsonValueAccessor);
/*      */     } 
/*      */ 
/*      */     
/* 2410 */     return EnumResolver.constructFor(config, enumClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _hasCreatorAnnotation(DeserializationContext ctxt, Annotated ann) {
/* 2418 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 2419 */     if (intr != null) {
/* 2420 */       JsonCreator.Mode mode = intr.findCreatorAnnotation((MapperConfig)ctxt.getConfig(), ann);
/* 2421 */       return (mode != null && mode != JsonCreator.Mode.DISABLED);
/*      */     } 
/* 2423 */     return false;
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
/*      */   @Deprecated
/*      */   protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
/* 2443 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 2444 */     if (intr == null) {
/* 2445 */       return type;
/*      */     }
/* 2447 */     return intr.refineDeserializationType((MapperConfig)ctxt.getConfig(), a, type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member) throws JsonMappingException {
/* 2458 */     return resolveMemberAndTypeAnnotations(ctxt, member, type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType) {
/* 2467 */     if (enumType == null) {
/* 2468 */       return null;
/*      */     }
/* 2470 */     BeanDescription beanDesc = config.introspect(enumType);
/* 2471 */     return beanDesc.findJsonValueMethod();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static class ContainerDefaultMappings
/*      */   {
/*      */     static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
/*      */ 
/*      */ 
/*      */     
/*      */     static final HashMap<String, Class<? extends Map>> _mapFallbacks;
/*      */ 
/*      */ 
/*      */     
/*      */     static {
/* 2488 */       HashMap<String, Class<? extends Collection>> hashMap = new HashMap<>();
/*      */       
/* 2490 */       Class<ArrayList> clazz1 = ArrayList.class;
/* 2491 */       Class<HashSet> clazz2 = HashSet.class;
/*      */       
/* 2493 */       hashMap.put(Collection.class.getName(), clazz1);
/* 2494 */       hashMap.put(List.class.getName(), clazz1);
/* 2495 */       hashMap.put(Set.class.getName(), clazz2);
/* 2496 */       hashMap.put(SortedSet.class.getName(), TreeSet.class);
/* 2497 */       hashMap.put(Queue.class.getName(), LinkedList.class);
/*      */ 
/*      */       
/* 2500 */       hashMap.put(AbstractList.class.getName(), clazz1);
/* 2501 */       hashMap.put(AbstractSet.class.getName(), clazz2);
/*      */ 
/*      */       
/* 2504 */       hashMap.put(Deque.class.getName(), LinkedList.class);
/* 2505 */       hashMap.put(NavigableSet.class.getName(), TreeSet.class);
/*      */       
/* 2507 */       _collectionFallbacks = hashMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2515 */       HashMap<String, Class<? extends Map>> fallbacks = new HashMap<>();
/*      */       
/* 2517 */       Class<LinkedHashMap> clazz = LinkedHashMap.class;
/* 2518 */       fallbacks.put(Map.class.getName(), clazz);
/* 2519 */       fallbacks.put(AbstractMap.class.getName(), clazz);
/* 2520 */       fallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
/* 2521 */       fallbacks.put(SortedMap.class.getName(), TreeMap.class);
/*      */       
/* 2523 */       fallbacks.put(NavigableMap.class.getName(), TreeMap.class);
/* 2524 */       fallbacks.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
/*      */ 
/*      */       
/* 2527 */       _mapFallbacks = fallbacks;
/*      */     }
/*      */     
/*      */     public static Class<?> findCollectionFallback(JavaType type) {
/* 2531 */       return _collectionFallbacks.get(type.getRawClass().getName());
/*      */     }
/*      */     
/*      */     public static Class<?> findMapFallback(JavaType type) {
/* 2535 */       return _mapFallbacks.get(type.getRawClass().getName());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static class CreatorCollectionState
/*      */   {
/*      */     public final DeserializationContext context;
/*      */     
/*      */     public final BeanDescription beanDesc;
/*      */     
/*      */     public final VisibilityChecker<?> vchecker;
/*      */     
/*      */     public final CreatorCollector creators;
/*      */     
/*      */     public final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams;
/*      */     
/*      */     private List<CreatorCandidate> _implicitFactoryCandidates;
/*      */     
/*      */     private int _explicitFactoryCount;
/*      */     
/*      */     private List<CreatorCandidate> _implicitConstructorCandidates;
/*      */     
/*      */     private int _explicitConstructorCount;
/*      */ 
/*      */     
/*      */     public CreatorCollectionState(DeserializationContext ctxt, BeanDescription bd, VisibilityChecker<?> vc, CreatorCollector cc, Map<AnnotatedWithParams, BeanPropertyDefinition[]> cp) {
/* 2563 */       this.context = ctxt;
/* 2564 */       this.beanDesc = bd;
/* 2565 */       this.vchecker = vc;
/* 2566 */       this.creators = cc;
/* 2567 */       this.creatorParams = cp;
/*      */     }
/*      */     
/*      */     public AnnotationIntrospector annotationIntrospector() {
/* 2571 */       return this.context.getAnnotationIntrospector();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void addImplicitFactoryCandidate(CreatorCandidate cc) {
/* 2577 */       if (this._implicitFactoryCandidates == null) {
/* 2578 */         this._implicitFactoryCandidates = new LinkedList<>();
/*      */       }
/* 2580 */       this._implicitFactoryCandidates.add(cc);
/*      */     }
/*      */     
/*      */     public void increaseExplicitFactoryCount() {
/* 2584 */       this._explicitFactoryCount++;
/*      */     }
/*      */     
/*      */     public boolean hasExplicitFactories() {
/* 2588 */       return (this._explicitFactoryCount > 0);
/*      */     }
/*      */     
/*      */     public boolean hasImplicitFactoryCandidates() {
/* 2592 */       return (this._implicitFactoryCandidates != null);
/*      */     }
/*      */     
/*      */     public List<CreatorCandidate> implicitFactoryCandidates() {
/* 2596 */       return this._implicitFactoryCandidates;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void addImplicitConstructorCandidate(CreatorCandidate cc) {
/* 2602 */       if (this._implicitConstructorCandidates == null) {
/* 2603 */         this._implicitConstructorCandidates = new LinkedList<>();
/*      */       }
/* 2605 */       this._implicitConstructorCandidates.add(cc);
/*      */     }
/*      */     
/*      */     public void increaseExplicitConstructorCount() {
/* 2609 */       this._explicitConstructorCount++;
/*      */     }
/*      */     
/*      */     public boolean hasExplicitConstructors() {
/* 2613 */       return (this._explicitConstructorCount > 0);
/*      */     }
/*      */     
/*      */     public boolean hasImplicitConstructorCandidates() {
/* 2617 */       return (this._implicitConstructorCandidates != null);
/*      */     }
/*      */     
/*      */     public List<CreatorCandidate> implicitConstructorCandidates() {
/* 2621 */       return this._implicitConstructorCandidates;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/BasicDeserializerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */