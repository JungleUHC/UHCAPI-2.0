/*     */ package com.fasterxml.jackson.databind.deser;
/*     */ import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonIncludeProperties;
/*     */ import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
/*     */ import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
/*     */ import com.fasterxml.jackson.databind.deser.impl.FieldProperty;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
/*     */ import com.fasterxml.jackson.databind.deser.std.ThrowableDeserializer;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedField;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class BeanDeserializerFactory extends BasicDeserializerFactory implements Serializable {
/*  40 */   private static final Class<?>[] INIT_CAUSE_PARAMS = new Class[] { Throwable.class };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   public static final BeanDeserializerFactory instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());
/*     */ 
/*     */   
/*     */   public BeanDeserializerFactory(DeserializerFactoryConfig config) {
/*  56 */     super(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DeserializerFactory withConfig(DeserializerFactoryConfig config) {
/*  67 */     if (this._factoryConfig == config) {
/*  68 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  76 */     ClassUtil.verifyMustOverride(BeanDeserializerFactory.class, this, "withConfig");
/*  77 */     return new BeanDeserializerFactory(config);
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
/*     */   public JsonDeserializer<Object> createBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*  97 */     DeserializationConfig config = ctxt.getConfig();
/*     */     
/*  99 */     JsonDeserializer<?> deser = _findCustomBeanDeserializer(type, config, beanDesc);
/* 100 */     if (deser != null) {
/*     */       
/* 102 */       if (this._factoryConfig.hasDeserializerModifiers()) {
/* 103 */         for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 104 */           deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser);
/*     */         }
/*     */       }
/* 107 */       return (JsonDeserializer)deser;
/*     */     } 
/*     */ 
/*     */     
/* 111 */     if (type.isThrowable()) {
/* 112 */       return buildThrowableDeserializer(ctxt, type, beanDesc);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 119 */     if (type.isAbstract() && !type.isPrimitive() && !type.isEnumType()) {
/*     */       
/* 121 */       JavaType concreteType = materializeAbstractType(ctxt, type, beanDesc);
/* 122 */       if (concreteType != null) {
/*     */ 
/*     */         
/* 125 */         beanDesc = config.introspect(concreteType);
/* 126 */         return buildBeanDeserializer(ctxt, concreteType, beanDesc);
/*     */       } 
/*     */     } 
/*     */     
/* 130 */     deser = findStdDeserializer(ctxt, type, beanDesc);
/* 131 */     if (deser != null) {
/* 132 */       return (JsonDeserializer)deser;
/*     */     }
/*     */ 
/*     */     
/* 136 */     if (!isPotentialBeanType(type.getRawClass())) {
/* 137 */       return null;
/*     */     }
/*     */     
/* 140 */     _validateSubType(ctxt, type, beanDesc);
/*     */ 
/*     */ 
/*     */     
/* 144 */     deser = _findUnsupportedTypeDeserializer(ctxt, type, beanDesc);
/* 145 */     if (deser != null) {
/* 146 */       return (JsonDeserializer)deser;
/*     */     }
/*     */ 
/*     */     
/* 150 */     return buildBeanDeserializer(ctxt, type, beanDesc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription valueBeanDesc, Class<?> builderClass) throws JsonMappingException {
/*     */     JavaType builderType;
/* 161 */     if (ctxt.isEnabled(MapperFeature.INFER_BUILDER_TYPE_BINDINGS)) {
/* 162 */       builderType = ctxt.getTypeFactory().constructParametricType(builderClass, valueType.getBindings());
/*     */     } else {
/* 164 */       builderType = ctxt.constructType(builderClass);
/*     */     } 
/* 166 */     BeanDescription builderDesc = ctxt.getConfig().introspectForBuilder(builderType, valueBeanDesc);
/*     */ 
/*     */     
/* 169 */     return buildBuilderBasedDeserializer(ctxt, valueType, builderDesc);
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
/*     */   protected JsonDeserializer<?> findStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 182 */     JsonDeserializer<?> deser = findDefaultDeserializer(ctxt, type, beanDesc);
/*     */     
/* 184 */     if (deser != null && 
/* 185 */       this._factoryConfig.hasDeserializerModifiers()) {
/* 186 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 187 */         deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser);
/*     */       }
/*     */     }
/*     */     
/* 191 */     return deser;
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
/*     */   protected JsonDeserializer<Object> _findUnsupportedTypeDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 209 */     String errorMsg = BeanUtil.checkUnsupportedType(type);
/* 210 */     if (errorMsg != null)
/*     */     {
/*     */       
/* 213 */       if (ctxt.getConfig().findMixInClassFor(type.getRawClass()) == null) {
/* 214 */         return (JsonDeserializer<Object>)new UnsupportedTypeDeserializer(type, errorMsg);
/*     */       }
/*     */     }
/* 217 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JavaType materializeAbstractType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 225 */     for (AbstractTypeResolver r : this._factoryConfig.abstractTypeResolvers()) {
/* 226 */       JavaType concrete = r.resolveAbstractType(ctxt.getConfig(), beanDesc);
/* 227 */       if (concrete != null) {
/* 228 */         return concrete;
/*     */       }
/*     */     } 
/* 231 */     return null;
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
/*     */   public JsonDeserializer<Object> buildBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*     */     ValueInstantiator valueInstantiator;
/*     */     JsonDeserializer<?> deserializer;
/*     */     try {
/* 261 */       valueInstantiator = findValueInstantiator(ctxt, beanDesc);
/* 262 */     } catch (NoClassDefFoundError error) {
/* 263 */       return (JsonDeserializer<Object>)new ErrorThrowingDeserializer(error);
/* 264 */     } catch (IllegalArgumentException e0) {
/*     */ 
/*     */ 
/*     */       
/* 268 */       throw InvalidDefinitionException.from(ctxt.getParser(), 
/* 269 */           ClassUtil.exceptionMessage(e0), beanDesc, null)
/*     */         
/* 271 */         .withCause(e0);
/*     */     } 
/* 273 */     BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
/* 274 */     builder.setValueInstantiator(valueInstantiator);
/*     */     
/* 276 */     addBeanProps(ctxt, beanDesc, builder);
/* 277 */     addObjectIdReader(ctxt, beanDesc, builder);
/*     */ 
/*     */     
/* 280 */     addBackReferenceProperties(ctxt, beanDesc, builder);
/* 281 */     addInjectables(ctxt, beanDesc, builder);
/*     */     
/* 283 */     DeserializationConfig config = ctxt.getConfig();
/* 284 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 285 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 286 */         builder = mod.updateBuilder(config, beanDesc, builder);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 291 */     if (type.isAbstract() && !valueInstantiator.canInstantiate()) {
/* 292 */       deserializer = builder.buildAbstract();
/*     */     } else {
/* 294 */       deserializer = builder.build();
/*     */     } 
/*     */ 
/*     */     
/* 298 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 299 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 300 */         deserializer = mod.modifyDeserializer(config, beanDesc, deserializer);
/*     */       }
/*     */     }
/* 303 */     return (JsonDeserializer)deserializer;
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
/*     */   protected JsonDeserializer<Object> buildBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription builderDesc) throws JsonMappingException {
/*     */     ValueInstantiator valueInstantiator;
/*     */     try {
/* 321 */       valueInstantiator = findValueInstantiator(ctxt, builderDesc);
/* 322 */     } catch (NoClassDefFoundError error) {
/* 323 */       return (JsonDeserializer<Object>)new ErrorThrowingDeserializer(error);
/* 324 */     } catch (IllegalArgumentException e) {
/*     */ 
/*     */ 
/*     */       
/* 328 */       throw InvalidDefinitionException.from(ctxt.getParser(), 
/* 329 */           ClassUtil.exceptionMessage(e), builderDesc, null);
/*     */     } 
/*     */     
/* 332 */     DeserializationConfig config = ctxt.getConfig();
/* 333 */     BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, builderDesc);
/* 334 */     builder.setValueInstantiator(valueInstantiator);
/*     */     
/* 336 */     addBeanProps(ctxt, builderDesc, builder);
/* 337 */     addObjectIdReader(ctxt, builderDesc, builder);
/*     */ 
/*     */     
/* 340 */     addBackReferenceProperties(ctxt, builderDesc, builder);
/* 341 */     addInjectables(ctxt, builderDesc, builder);
/*     */     
/* 343 */     JsonPOJOBuilder.Value builderConfig = builderDesc.findPOJOBuilderConfig();
/* 344 */     String buildMethodName = (builderConfig == null) ? "build" : builderConfig.buildMethodName;
/*     */ 
/*     */ 
/*     */     
/* 348 */     AnnotatedMethod buildMethod = builderDesc.findMethod(buildMethodName, null);
/* 349 */     if (buildMethod != null && 
/* 350 */       config.canOverrideAccessModifiers()) {
/* 351 */       ClassUtil.checkAndFixAccess(buildMethod.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/*     */     
/* 354 */     builder.setPOJOBuilder(buildMethod, builderConfig);
/*     */     
/* 356 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 357 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 358 */         builder = mod.updateBuilder(config, builderDesc, builder);
/*     */       }
/*     */     }
/* 361 */     JsonDeserializer<?> deserializer = builder.buildBuilderBased(valueType, buildMethodName);
/*     */ 
/*     */ 
/*     */     
/* 365 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 366 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 367 */         deserializer = mod.modifyDeserializer(config, builderDesc, deserializer);
/*     */       }
/*     */     }
/* 370 */     return (JsonDeserializer)deserializer;
/*     */   }
/*     */   
/*     */   protected void addObjectIdReader(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
/*     */     JavaType idType;
/*     */     SettableBeanProperty idProp;
/*     */     ObjectIdGenerator<?> gen;
/* 377 */     ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
/* 378 */     if (objectIdInfo == null) {
/*     */       return;
/*     */     }
/* 381 */     Class<?> implClass = objectIdInfo.getGeneratorType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 386 */     ObjectIdResolver resolver = ctxt.objectIdResolverInstance((Annotated)beanDesc.getClassInfo(), objectIdInfo);
/*     */ 
/*     */     
/* 389 */     if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
/* 390 */       PropertyName propName = objectIdInfo.getPropertyName();
/* 391 */       idProp = builder.findProperty(propName);
/* 392 */       if (idProp == null)
/* 393 */         throw new IllegalArgumentException(String.format("Invalid Object Id definition for %s: cannot find property with name %s", new Object[] {
/*     */                 
/* 395 */                 ClassUtil.getTypeDescription(beanDesc.getType()), 
/* 396 */                 ClassUtil.name(propName)
/*     */               })); 
/* 398 */       idType = idProp.getType();
/* 399 */       PropertyBasedObjectIdGenerator propertyBasedObjectIdGenerator = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
/*     */     } else {
/* 401 */       JavaType type = ctxt.constructType(implClass);
/* 402 */       idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/* 403 */       idProp = null;
/* 404 */       gen = ctxt.objectIdGeneratorInstance((Annotated)beanDesc.getClassInfo(), objectIdInfo);
/*     */     } 
/*     */     
/* 407 */     JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
/* 408 */     builder.setObjectIdReader(ObjectIdReader.construct(idType, objectIdInfo
/* 409 */           .getPropertyName(), gen, deser, idProp, resolver));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<Object> buildThrowableDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*     */     ThrowableDeserializer throwableDeserializer;
/*     */     JsonDeserializer<?> jsonDeserializer1;
/* 417 */     DeserializationConfig config = ctxt.getConfig();
/*     */     
/* 419 */     BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
/* 420 */     builder.setValueInstantiator(findValueInstantiator(ctxt, beanDesc));
/*     */     
/* 422 */     addBeanProps(ctxt, beanDesc, builder);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 427 */     AnnotatedMethod am = beanDesc.findMethod("initCause", INIT_CAUSE_PARAMS);
/* 428 */     if (am != null) {
/* 429 */       SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct((MapperConfig)ctxt.getConfig(), (AnnotatedMember)am, new PropertyName("cause"));
/*     */       
/* 431 */       SettableBeanProperty prop = constructSettableProperty(ctxt, beanDesc, (BeanPropertyDefinition)propDef, am
/* 432 */           .getParameterType(0));
/* 433 */       if (prop != null)
/*     */       {
/*     */         
/* 436 */         builder.addOrReplaceProperty(prop, true);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 441 */     builder.addIgnorable("localizedMessage");
/*     */     
/* 443 */     builder.addIgnorable("suppressed");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 450 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 451 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 452 */         builder = mod.updateBuilder(config, beanDesc, builder);
/*     */       }
/*     */     }
/* 455 */     JsonDeserializer<?> deserializer = builder.build();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 460 */     if (deserializer instanceof BeanDeserializer) {
/* 461 */       throwableDeserializer = new ThrowableDeserializer((BeanDeserializer)deserializer);
/*     */     }
/*     */ 
/*     */     
/* 465 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 466 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 467 */         jsonDeserializer1 = mod.modifyDeserializer(config, beanDesc, (JsonDeserializer<?>)throwableDeserializer);
/*     */       }
/*     */     }
/* 470 */     return (JsonDeserializer)jsonDeserializer1;
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
/*     */   protected BeanDeserializerBuilder constructBeanDeserializerBuilder(DeserializationContext ctxt, BeanDescription beanDesc) {
/* 486 */     return new BeanDeserializerBuilder(beanDesc, ctxt);
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
/*     */   protected void addBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
/*     */     Set<String> ignored;
/* 500 */     boolean isConcrete = !beanDesc.getType().isAbstract();
/*     */     
/* 502 */     SettableBeanProperty[] creatorProps = isConcrete ? builder.getValueInstantiator().getFromObjectArguments(ctxt.getConfig()) : null;
/*     */     
/* 504 */     boolean hasCreatorProps = (creatorProps != null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 511 */     JsonIgnoreProperties.Value ignorals = ctxt.getConfig().getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc
/* 512 */         .getClassInfo());
/*     */     
/* 514 */     if (ignorals != null) {
/* 515 */       boolean ignoreAny = ignorals.getIgnoreUnknown();
/* 516 */       builder.setIgnoreUnknownProperties(ignoreAny);
/*     */       
/* 518 */       ignored = ignorals.findIgnoredForDeserialization();
/* 519 */       for (String propName : ignored) {
/* 520 */         builder.addIgnorable(propName);
/*     */       }
/*     */     } else {
/* 523 */       ignored = Collections.emptySet();
/*     */     } 
/*     */     
/* 526 */     JsonIncludeProperties.Value inclusions = ctxt.getConfig().getDefaultPropertyInclusions(beanDesc.getBeanClass(), beanDesc
/* 527 */         .getClassInfo());
/* 528 */     Set<String> included = null;
/* 529 */     if (inclusions != null) {
/* 530 */       included = inclusions.getIncluded();
/* 531 */       if (included != null) {
/* 532 */         for (String propName : included) {
/* 533 */           builder.addIncludable(propName);
/*     */         }
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 539 */     AnnotatedMember anySetter = beanDesc.findAnySetterAccessor();
/* 540 */     if (anySetter != null) {
/* 541 */       builder.setAnySetter(constructAnySetter(ctxt, beanDesc, anySetter));
/*     */     }
/*     */     else {
/*     */       
/* 545 */       Collection<String> ignored2 = beanDesc.getIgnoredPropertyNames();
/* 546 */       if (ignored2 != null) {
/* 547 */         for (String propName : ignored2)
/*     */         {
/*     */           
/* 550 */           builder.addIgnorable(propName);
/*     */         }
/*     */       }
/*     */     } 
/*     */     
/* 555 */     boolean useGettersAsSetters = (ctxt.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS) && ctxt.isEnabled(MapperFeature.AUTO_DETECT_GETTERS));
/*     */ 
/*     */     
/* 558 */     List<BeanPropertyDefinition> propDefs = filterBeanProps(ctxt, beanDesc, builder, beanDesc
/* 559 */         .findProperties(), ignored, included);
/*     */     
/* 561 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 562 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 563 */         propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 568 */     for (BeanPropertyDefinition propDef : propDefs) {
/* 569 */       SettableBeanProperty prop = null;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 574 */       if (propDef.hasSetter()) {
/* 575 */         AnnotatedMethod setter = propDef.getSetter();
/* 576 */         JavaType propertyType = setter.getParameterType(0);
/* 577 */         prop = constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
/* 578 */       } else if (propDef.hasField()) {
/* 579 */         AnnotatedField field = propDef.getField();
/* 580 */         JavaType propertyType = field.getType();
/* 581 */         prop = constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
/*     */       } else {
/*     */         
/* 584 */         AnnotatedMethod getter = propDef.getGetter();
/* 585 */         if (getter != null) {
/* 586 */           if (useGettersAsSetters && _isSetterlessType(getter.getRawType())) {
/*     */ 
/*     */             
/* 589 */             if (!builder.hasIgnorable(propDef.getName()))
/*     */             {
/*     */               
/* 592 */               prop = constructSetterlessProperty(ctxt, beanDesc, propDef);
/*     */             }
/* 594 */           } else if (!propDef.hasConstructorParameter()) {
/* 595 */             PropertyMetadata md = propDef.getMetadata();
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 600 */             if (md.getMergeInfo() != null) {
/* 601 */               prop = constructSetterlessProperty(ctxt, beanDesc, propDef);
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 609 */       if (hasCreatorProps && propDef.hasConstructorParameter()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 615 */         String name = propDef.getName();
/* 616 */         CreatorProperty cprop = null;
/*     */         
/* 618 */         for (SettableBeanProperty cp : creatorProps) {
/* 619 */           if (name.equals(cp.getName()) && cp instanceof CreatorProperty) {
/* 620 */             cprop = (CreatorProperty)cp;
/*     */             break;
/*     */           } 
/*     */         } 
/* 624 */         if (cprop == null) {
/* 625 */           List<String> n = new ArrayList<>();
/* 626 */           for (SettableBeanProperty cp : creatorProps) {
/* 627 */             n.add(cp.getName());
/*     */           }
/* 629 */           ctxt.reportBadPropertyDefinition(beanDesc, propDef, "Could not find creator property with name %s (known Creator properties: %s)", new Object[] {
/*     */                 
/* 631 */                 ClassUtil.name(name), n });
/*     */           continue;
/*     */         } 
/* 634 */         if (prop != null) {
/* 635 */           cprop.setFallbackSetter(prop);
/*     */         }
/* 637 */         Class<?>[] views = propDef.findViews();
/* 638 */         if (views == null) {
/* 639 */           views = beanDesc.findDefaultViews();
/*     */         }
/* 641 */         cprop.setViews(views);
/* 642 */         builder.addCreatorProperty(cprop);
/*     */         continue;
/*     */       } 
/* 645 */       if (prop != null) {
/*     */         
/* 647 */         Class<?>[] views = propDef.findViews();
/* 648 */         if (views == null) {
/* 649 */           views = beanDesc.findDefaultViews();
/*     */         }
/* 651 */         prop.setViews(views);
/* 652 */         builder.addProperty(prop);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean _isSetterlessType(Class<?> rawType) {
/* 661 */     return (Collection.class.isAssignableFrom(rawType) || Map.class
/* 662 */       .isAssignableFrom(rawType));
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
/*     */   @Deprecated
/*     */   protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored) throws JsonMappingException {
/* 680 */     return filterBeanProps(ctxt, beanDesc, builder, propDefsIn, ignored, (Set<String>)null);
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
/*     */   protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored, Set<String> included) {
/* 698 */     ArrayList<BeanPropertyDefinition> result = new ArrayList<>(Math.max(4, propDefsIn.size()));
/* 699 */     HashMap<Class<?>, Boolean> ignoredTypes = new HashMap<>();
/*     */     
/* 701 */     for (BeanPropertyDefinition property : propDefsIn) {
/* 702 */       String name = property.getName();
/*     */       
/* 704 */       if (IgnorePropertiesUtil.shouldIgnore(name, ignored, included)) {
/*     */         continue;
/*     */       }
/* 707 */       if (!property.hasConstructorParameter()) {
/* 708 */         Class<?> rawPropertyType = property.getRawPrimaryType();
/*     */         
/* 710 */         if (rawPropertyType != null && 
/* 711 */           isIgnorableType(ctxt.getConfig(), property, rawPropertyType, ignoredTypes)) {
/*     */           
/* 713 */           builder.addIgnorable(name);
/*     */           continue;
/*     */         } 
/*     */       } 
/* 717 */       result.add(property);
/*     */     } 
/* 719 */     return result;
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
/*     */   protected void addBackReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
/* 733 */     List<BeanPropertyDefinition> refProps = beanDesc.findBackReferences();
/* 734 */     if (refProps != null) {
/* 735 */       for (BeanPropertyDefinition refProp : refProps) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 752 */         String refName = refProp.findReferenceName();
/* 753 */         builder.addBackReferenceProperty(refName, constructSettableProperty(ctxt, beanDesc, refProp, refProp
/* 754 */               .getPrimaryType()));
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void addReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
/* 764 */     addBackReferenceProperties(ctxt, beanDesc, builder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addInjectables(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
/* 775 */     Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
/* 776 */     if (raw != null) {
/* 777 */       for (Map.Entry<Object, AnnotatedMember> entry : raw.entrySet()) {
/* 778 */         AnnotatedMember m = entry.getValue();
/* 779 */         builder.addInjectable(PropertyName.construct(m.getName()), m
/* 780 */             .getType(), beanDesc
/* 781 */             .getClassAnnotations(), m, entry.getKey());
/*     */       } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SettableAnyProperty constructAnySetter(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedMember mutator) throws JsonMappingException {
/*     */     BeanProperty.Std std;
/*     */     JavaType keyType, valueType;
/* 804 */     if (mutator instanceof AnnotatedMethod) {
/*     */       
/* 806 */       AnnotatedMethod am = (AnnotatedMethod)mutator;
/* 807 */       keyType = am.getParameterType(0);
/* 808 */       valueType = am.getParameterType(1);
/* 809 */       valueType = resolveMemberAndTypeAnnotations(ctxt, mutator, valueType);
/* 810 */       std = new BeanProperty.Std(PropertyName.construct(mutator.getName()), valueType, null, mutator, PropertyMetadata.STD_OPTIONAL);
/*     */ 
/*     */     
/*     */     }
/* 814 */     else if (mutator instanceof AnnotatedField) {
/* 815 */       AnnotatedField af = (AnnotatedField)mutator;
/*     */       
/* 817 */       JavaType mapType = af.getType();
/* 818 */       mapType = resolveMemberAndTypeAnnotations(ctxt, mutator, mapType);
/* 819 */       keyType = mapType.getKeyType();
/* 820 */       valueType = mapType.getContentType();
/* 821 */       std = new BeanProperty.Std(PropertyName.construct(mutator.getName()), mapType, null, mutator, PropertyMetadata.STD_OPTIONAL);
/*     */     } else {
/*     */       
/* 824 */       return (SettableAnyProperty)ctxt.reportBadDefinition(beanDesc.getType(), String.format("Unrecognized mutator type for any setter: %s", new Object[] { mutator
/* 825 */               .getClass() }));
/*     */     } 
/*     */ 
/*     */     
/* 829 */     KeyDeserializer keyDeser = findKeyDeserializerFromAnnotation(ctxt, (Annotated)mutator);
/* 830 */     if (keyDeser == null) {
/* 831 */       keyDeser = (KeyDeserializer)keyType.getValueHandler();
/*     */     }
/* 833 */     if (keyDeser == null) {
/* 834 */       keyDeser = ctxt.findKeyDeserializer(keyType, (BeanProperty)std);
/*     */     }
/* 836 */     else if (keyDeser instanceof ContextualKeyDeserializer) {
/*     */       
/* 838 */       keyDeser = ((ContextualKeyDeserializer)keyDeser).createContextual(ctxt, (BeanProperty)std);
/*     */     } 
/*     */     
/* 841 */     JsonDeserializer<Object> deser = findContentDeserializerFromAnnotation(ctxt, (Annotated)mutator);
/* 842 */     if (deser == null) {
/* 843 */       deser = (JsonDeserializer<Object>)valueType.getValueHandler();
/*     */     }
/* 845 */     if (deser != null)
/*     */     {
/* 847 */       deser = ctxt.handlePrimaryContextualization(deser, (BeanProperty)std, valueType);
/*     */     }
/* 849 */     TypeDeserializer typeDeser = (TypeDeserializer)valueType.getTypeHandler();
/* 850 */     return new SettableAnyProperty((BeanProperty)std, mutator, valueType, keyDeser, deser, typeDeser);
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
/*     */   protected SettableBeanProperty constructSettableProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef, JavaType propType0) throws JsonMappingException {
/*     */     FieldProperty fieldProperty;
/*     */     SettableBeanProperty settableBeanProperty;
/* 867 */     AnnotatedMember mutator = propDef.getNonConstructorMutator();
/*     */ 
/*     */ 
/*     */     
/* 871 */     if (mutator == null) {
/* 872 */       ctxt.reportBadPropertyDefinition(beanDesc, propDef, "No non-constructor mutator available", new Object[0]);
/*     */     }
/* 874 */     JavaType type = resolveMemberAndTypeAnnotations(ctxt, mutator, propType0);
/*     */     
/* 876 */     TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
/*     */     
/* 878 */     if (mutator instanceof AnnotatedMethod) {
/*     */       
/* 880 */       MethodProperty methodProperty = new MethodProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedMethod)mutator);
/*     */     }
/*     */     else {
/*     */       
/* 884 */       fieldProperty = new FieldProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedField)mutator);
/*     */     } 
/* 886 */     JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, (Annotated)mutator);
/* 887 */     if (deser == null) {
/* 888 */       deser = (JsonDeserializer)type.getValueHandler();
/*     */     }
/* 890 */     if (deser != null) {
/* 891 */       deser = ctxt.handlePrimaryContextualization(deser, (BeanProperty)fieldProperty, type);
/* 892 */       settableBeanProperty = fieldProperty.withValueDeserializer(deser);
/*     */     } 
/*     */     
/* 895 */     AnnotationIntrospector.ReferenceProperty ref = propDef.findReferenceType();
/* 896 */     if (ref != null && ref.isManagedReference()) {
/* 897 */       settableBeanProperty.setManagedReferenceName(ref.getName());
/*     */     }
/* 899 */     ObjectIdInfo objectIdInfo = propDef.findObjectIdInfo();
/* 900 */     if (objectIdInfo != null) {
/* 901 */       settableBeanProperty.setObjectIdInfo(objectIdInfo);
/*     */     }
/* 903 */     return settableBeanProperty;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SettableBeanProperty constructSetterlessProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef) throws JsonMappingException {
/*     */     SettableBeanProperty settableBeanProperty;
/* 914 */     AnnotatedMethod getter = propDef.getGetter();
/* 915 */     JavaType type = resolveMemberAndTypeAnnotations(ctxt, (AnnotatedMember)getter, getter.getType());
/* 916 */     TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
/*     */     
/* 918 */     SetterlessProperty setterlessProperty = new SetterlessProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), getter);
/* 919 */     JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, (Annotated)getter);
/* 920 */     if (deser == null) {
/* 921 */       deser = (JsonDeserializer)type.getValueHandler();
/*     */     }
/* 923 */     if (deser != null) {
/* 924 */       deser = ctxt.handlePrimaryContextualization(deser, (BeanProperty)setterlessProperty, type);
/* 925 */       settableBeanProperty = setterlessProperty.withValueDeserializer(deser);
/*     */     } 
/* 927 */     return settableBeanProperty;
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
/*     */   protected boolean isPotentialBeanType(Class<?> type) {
/* 946 */     String typeStr = ClassUtil.canBeABeanType(type);
/* 947 */     if (typeStr != null) {
/* 948 */       throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
/*     */     }
/* 950 */     if (ClassUtil.isProxyType(type)) {
/* 951 */       throw new IllegalArgumentException("Cannot deserialize Proxy class " + type.getName() + " as a Bean");
/*     */     }
/*     */ 
/*     */     
/* 955 */     typeStr = ClassUtil.isLocalType(type, true);
/* 956 */     if (typeStr != null) {
/* 957 */       throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
/*     */     }
/* 959 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isIgnorableType(DeserializationConfig config, BeanPropertyDefinition propDef, Class<?> type, Map<Class<?>, Boolean> ignoredTypes) {
/* 969 */     Boolean status = ignoredTypes.get(type);
/* 970 */     if (status != null) {
/* 971 */       return status.booleanValue();
/*     */     }
/*     */     
/* 974 */     if (type == String.class || type.isPrimitive()) {
/* 975 */       status = Boolean.FALSE;
/*     */     } else {
/*     */       
/* 978 */       status = config.getConfigOverride(type).getIsIgnoredType();
/* 979 */       if (status == null) {
/* 980 */         BeanDescription desc = config.introspectClassAnnotations(type);
/* 981 */         status = config.getAnnotationIntrospector().isIgnorableType(desc.getClassInfo());
/*     */         
/* 983 */         if (status == null) {
/* 984 */           status = Boolean.FALSE;
/*     */         }
/*     */       } 
/*     */     } 
/* 988 */     ignoredTypes.put(type, status);
/* 989 */     return status.booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _validateSubType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 999 */     SubTypeValidator.instance().validateSubType(ctxt, type, beanDesc);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/BeanDeserializerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */