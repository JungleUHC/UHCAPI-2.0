/*     */ package com.fasterxml.jackson.databind.deser;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*     */ import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayBuilderDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*     */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BuilderBasedDeserializer
/*     */   extends BeanDeserializerBase
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final AnnotatedMethod _buildMethod;
/*     */   protected final JavaType _targetType;
/*     */   
/*     */   public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, JavaType targetType, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
/*  55 */     this(builder, beanDesc, targetType, properties, backRefs, ignorableProps, ignoreAllUnknown, (Set<String>)null, hasViews);
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
/*     */   public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, JavaType targetType, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, Set<String> includableProps, boolean hasViews) {
/*  67 */     super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, includableProps, hasViews);
/*     */     
/*  69 */     this._targetType = targetType;
/*  70 */     this._buildMethod = builder.getBuildMethod();
/*     */     
/*  72 */     if (this._objectIdReader != null) {
/*  73 */       throw new IllegalArgumentException("Cannot use Object Id with Builder-based deserialization (type " + beanDesc
/*  74 */           .getType() + ")");
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
/*     */   @Deprecated
/*     */   public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
/*  88 */     this(builder, beanDesc, beanDesc
/*  89 */         .getType(), properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BuilderBasedDeserializer(BuilderBasedDeserializer src) {
/*  99 */     this(src, src._ignoreAllUnknown);
/*     */   }
/*     */ 
/*     */   
/*     */   protected BuilderBasedDeserializer(BuilderBasedDeserializer src, boolean ignoreAllUnknown) {
/* 104 */     super(src, ignoreAllUnknown);
/* 105 */     this._buildMethod = src._buildMethod;
/* 106 */     this._targetType = src._targetType;
/*     */   }
/*     */   
/*     */   protected BuilderBasedDeserializer(BuilderBasedDeserializer src, NameTransformer unwrapper) {
/* 110 */     super(src, unwrapper);
/* 111 */     this._buildMethod = src._buildMethod;
/* 112 */     this._targetType = src._targetType;
/*     */   }
/*     */   
/*     */   public BuilderBasedDeserializer(BuilderBasedDeserializer src, ObjectIdReader oir) {
/* 116 */     super(src, oir);
/* 117 */     this._buildMethod = src._buildMethod;
/* 118 */     this._targetType = src._targetType;
/*     */   }
/*     */   
/*     */   public BuilderBasedDeserializer(BuilderBasedDeserializer src, Set<String> ignorableProps) {
/* 122 */     this(src, ignorableProps, src._includableProps);
/*     */   }
/*     */   
/*     */   public BuilderBasedDeserializer(BuilderBasedDeserializer src, Set<String> ignorableProps, Set<String> includableProps) {
/* 126 */     super(src, ignorableProps, includableProps);
/* 127 */     this._buildMethod = src._buildMethod;
/* 128 */     this._targetType = src._targetType;
/*     */   }
/*     */   
/*     */   public BuilderBasedDeserializer(BuilderBasedDeserializer src, BeanPropertyMap props) {
/* 132 */     super(src, props);
/* 133 */     this._buildMethod = src._buildMethod;
/* 134 */     this._targetType = src._targetType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
/* 144 */     return (JsonDeserializer<Object>)new BuilderBasedDeserializer(this, unwrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
/* 149 */     return new BuilderBasedDeserializer(this, oir);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withByNameInclusion(Set<String> ignorableProps, Set<String> includableProps) {
/* 155 */     return new BuilderBasedDeserializer(this, ignorableProps, includableProps);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withIgnoreAllUnknown(boolean ignoreUnknown) {
/* 160 */     return new BuilderBasedDeserializer(this, ignoreUnknown);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
/* 165 */     return new BuilderBasedDeserializer(this, props);
/*     */   }
/*     */ 
/*     */   
/*     */   protected BeanDeserializerBase asArrayDeserializer() {
/* 170 */     SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
/* 171 */     return (BeanDeserializerBase)new BeanAsArrayBuilderDeserializer(this, this._targetType, props, this._buildMethod);
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
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 183 */     return Boolean.FALSE;
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
/*     */   protected Object finishBuild(DeserializationContext ctxt, Object builder) throws IOException {
/* 196 */     if (null == this._buildMethod) {
/* 197 */       return builder;
/*     */     }
/*     */     try {
/* 200 */       return this._buildMethod.getMember().invoke(builder, (Object[])null);
/* 201 */     } catch (Exception e) {
/* 202 */       return wrapInstantiationProblem(e, ctxt);
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
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 214 */     if (p.isExpectedStartObjectToken()) {
/* 215 */       JsonToken t = p.nextToken();
/* 216 */       if (this._vanillaProcessing) {
/* 217 */         return finishBuild(ctxt, vanillaDeserialize(p, ctxt, t));
/*     */       }
/* 219 */       return finishBuild(ctxt, deserializeFromObject(p, ctxt));
/*     */     } 
/*     */     
/* 222 */     switch (p.currentTokenId()) {
/*     */       case 6:
/* 224 */         return finishBuild(ctxt, deserializeFromString(p, ctxt));
/*     */       case 7:
/* 226 */         return finishBuild(ctxt, deserializeFromNumber(p, ctxt));
/*     */       case 8:
/* 228 */         return finishBuild(ctxt, deserializeFromDouble(p, ctxt));
/*     */       case 12:
/* 230 */         return p.getEmbeddedObject();
/*     */       case 9:
/*     */       case 10:
/* 233 */         return finishBuild(ctxt, deserializeFromBoolean(p, ctxt));
/*     */ 
/*     */       
/*     */       case 3:
/* 237 */         return _deserializeFromArray(p, ctxt);
/*     */       case 2:
/*     */       case 5:
/* 240 */         return finishBuild(ctxt, deserializeFromObject(p, ctxt));
/*     */     } 
/*     */     
/* 243 */     return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
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
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object value) throws IOException {
/* 257 */     JavaType valueType = this._targetType;
/*     */     
/* 259 */     Class<?> builderRawType = handledType();
/* 260 */     Class<?> instRawType = value.getClass();
/* 261 */     if (builderRawType.isAssignableFrom(instRawType)) {
/* 262 */       return ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing Builder (%s) instance not supported", new Object[] { valueType, builderRawType
/*     */               
/* 264 */               .getName() }));
/*     */     }
/* 266 */     return ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing instance (of %s) not supported", new Object[] { valueType, instRawType
/*     */             
/* 268 */             .getName() }));
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
/*     */   private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/* 285 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 286 */     for (; p.currentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
/* 287 */       String propName = p.currentName();
/*     */       
/* 289 */       p.nextToken();
/* 290 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/* 291 */       if (prop != null) {
/*     */         try {
/* 293 */           bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 294 */         } catch (Exception e) {
/* 295 */           wrapAndThrow(e, bean, propName, ctxt);
/*     */         } 
/*     */       } else {
/* 298 */         handleUnknownVanilla(p, ctxt, bean, propName);
/*     */       } 
/*     */     } 
/* 301 */     return bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 312 */     if (this._nonStandardCreation) {
/* 313 */       if (this._unwrappedPropertyHandler != null) {
/* 314 */         return deserializeWithUnwrapped(p, ctxt);
/*     */       }
/* 316 */       if (this._externalTypeIdHandler != null) {
/* 317 */         return deserializeWithExternalTypeId(p, ctxt);
/*     */       }
/* 319 */       return deserializeFromObjectUsingNonDefault(p, ctxt);
/*     */     } 
/* 321 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 322 */     if (this._injectables != null) {
/* 323 */       injectValues(ctxt, bean);
/*     */     }
/* 325 */     if (this._needViewProcesing) {
/* 326 */       Class<?> view = ctxt.getActiveView();
/* 327 */       if (view != null) {
/* 328 */         return deserializeWithView(p, ctxt, bean, view);
/*     */       }
/*     */     } 
/* 331 */     for (; p.currentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
/* 332 */       String propName = p.currentName();
/*     */       
/* 334 */       p.nextToken();
/* 335 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/* 336 */       if (prop != null) {
/*     */         try {
/* 338 */           bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 339 */         } catch (Exception e) {
/* 340 */           wrapAndThrow(e, bean, propName, ctxt);
/*     */         } 
/*     */       } else {
/*     */         
/* 344 */         handleUnknownVanilla(p, ctxt, bean, propName);
/*     */       } 
/* 346 */     }  return bean;
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
/*     */   protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
/*     */     Object builder;
/* 364 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/* 365 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 366 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/*     */ 
/*     */     
/* 369 */     TokenBuffer unknown = null;
/*     */     
/* 371 */     JsonToken t = p.currentToken();
/* 372 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 373 */       String propName = p.currentName();
/* 374 */       p.nextToken();
/*     */       
/* 376 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/*     */       
/* 378 */       if (!buffer.readIdProperty(propName) || creatorProp != null)
/*     */       {
/*     */         
/* 381 */         if (creatorProp != null) {
/* 382 */           if (activeView != null && !creatorProp.visibleInView(activeView)) {
/* 383 */             p.skipChildren();
/*     */ 
/*     */           
/*     */           }
/* 387 */           else if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
/* 388 */             Object object; p.nextToken();
/*     */             
/*     */             try {
/* 391 */               object = creator.build(ctxt, buffer);
/* 392 */             } catch (Exception e) {
/* 393 */               wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*     */             } 
/*     */ 
/*     */             
/* 397 */             if (object.getClass() != this._beanType.getRawClass()) {
/* 398 */               return handlePolymorphic(p, ctxt, object, unknown);
/*     */             }
/* 400 */             if (unknown != null) {
/* 401 */               object = handleUnknownProperties(ctxt, object, unknown);
/*     */             }
/*     */             
/* 404 */             return _deserialize(p, ctxt, object);
/*     */           }
/*     */         
/*     */         } else {
/*     */           
/* 409 */           SettableBeanProperty prop = this._beanProperties.find(propName);
/* 410 */           if (prop != null) {
/* 411 */             buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/*     */ 
/*     */ 
/*     */           
/*     */           }
/* 416 */           else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/* 417 */             handleIgnoredProperty(p, ctxt, handledType(), propName);
/*     */ 
/*     */           
/*     */           }
/* 421 */           else if (this._anySetter != null) {
/* 422 */             buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/*     */           }
/*     */           else {
/*     */             
/* 426 */             if (unknown == null) {
/* 427 */               unknown = ctxt.bufferForInputBuffering(p);
/*     */             }
/* 429 */             unknown.writeFieldName(propName);
/* 430 */             unknown.copyCurrentStructure(p);
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*     */     try {
/* 436 */       builder = creator.build(ctxt, buffer);
/* 437 */     } catch (Exception e) {
/* 438 */       builder = wrapInstantiationProblem(e, ctxt);
/*     */     } 
/* 440 */     if (unknown != null) {
/*     */       
/* 442 */       if (builder.getClass() != this._beanType.getRawClass()) {
/* 443 */         return handlePolymorphic((JsonParser)null, ctxt, builder, unknown);
/*     */       }
/*     */       
/* 446 */       return handleUnknownProperties(ctxt, builder, unknown);
/*     */     } 
/* 448 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, Object builder) throws IOException {
/* 454 */     if (this._injectables != null) {
/* 455 */       injectValues(ctxt, builder);
/*     */     }
/* 457 */     if (this._unwrappedPropertyHandler != null) {
/* 458 */       if (p.hasToken(JsonToken.START_OBJECT)) {
/* 459 */         p.nextToken();
/*     */       }
/* 461 */       TokenBuffer tokens = ctxt.bufferForInputBuffering(p);
/* 462 */       tokens.writeStartObject();
/* 463 */       return deserializeWithUnwrapped(p, ctxt, builder, tokens);
/*     */     } 
/* 465 */     if (this._externalTypeIdHandler != null) {
/* 466 */       return deserializeWithExternalTypeId(p, ctxt, builder);
/*     */     }
/* 468 */     if (this._needViewProcesing) {
/* 469 */       Class<?> view = ctxt.getActiveView();
/* 470 */       if (view != null) {
/* 471 */         return deserializeWithView(p, ctxt, builder, view);
/*     */       }
/*     */     } 
/* 474 */     JsonToken t = p.currentToken();
/*     */     
/* 476 */     if (t == JsonToken.START_OBJECT) {
/* 477 */       t = p.nextToken();
/*     */     }
/* 479 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 480 */       String propName = p.currentName();
/*     */       
/* 482 */       p.nextToken();
/* 483 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/*     */       
/* 485 */       if (prop != null) {
/*     */         try {
/* 487 */           builder = prop.deserializeSetAndReturn(p, ctxt, builder);
/* 488 */         } catch (Exception e) {
/* 489 */           wrapAndThrow(e, builder, propName, ctxt);
/*     */         } 
/*     */       } else {
/*     */         
/* 493 */         handleUnknownVanilla(p, ctxt, builder, propName);
/*     */       } 
/* 495 */     }  return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 502 */     JsonDeserializer<Object> delegateDeser = this._arrayDelegateDeserializer;
/*     */     
/* 504 */     if (delegateDeser != null || (delegateDeser = this._delegateDeserializer) != null) {
/* 505 */       Object builder = this._valueInstantiator.createUsingArrayDelegate(ctxt, delegateDeser
/* 506 */           .deserialize(p, ctxt));
/* 507 */       if (this._injectables != null) {
/* 508 */         injectValues(ctxt, builder);
/*     */       }
/* 510 */       return finishBuild(ctxt, builder);
/*     */     } 
/* 512 */     CoercionAction act = _findCoercionFromEmptyArray(ctxt);
/* 513 */     boolean unwrap = ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
/*     */     
/* 515 */     if (unwrap || act != CoercionAction.Fail) {
/* 516 */       JsonToken t = p.nextToken();
/* 517 */       if (t == JsonToken.END_ARRAY) {
/* 518 */         switch (act) {
/*     */           case AsEmpty:
/* 520 */             return getEmptyValue(ctxt);
/*     */           case AsNull:
/*     */           case TryConvert:
/* 523 */             return getNullValue(ctxt);
/*     */         } 
/*     */         
/* 526 */         return ctxt.handleUnexpectedToken(getValueType(ctxt), JsonToken.START_ARRAY, p, null, new Object[0]);
/*     */       } 
/* 528 */       if (unwrap) {
/* 529 */         Object value = deserialize(p, ctxt);
/* 530 */         if (p.nextToken() != JsonToken.END_ARRAY) {
/* 531 */           handleMissingEndArrayForSingle(p, ctxt);
/*     */         }
/* 533 */         return value;
/*     */       } 
/*     */     } 
/* 536 */     return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
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
/*     */   protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException {
/* 549 */     JsonToken t = p.currentToken();
/* 550 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 551 */       String propName = p.currentName();
/*     */       
/* 553 */       p.nextToken();
/* 554 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/* 555 */       if (prop != null) {
/* 556 */         if (!prop.visibleInView(activeView)) {
/* 557 */           p.skipChildren();
/*     */         } else {
/*     */           
/*     */           try {
/* 561 */             bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 562 */           } catch (Exception e) {
/* 563 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           } 
/*     */         } 
/*     */       } else {
/* 567 */         handleUnknownVanilla(p, ctxt, bean, propName);
/*     */       } 
/* 569 */     }  return bean;
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
/*     */   protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 586 */     if (this._delegateDeserializer != null) {
/* 587 */       return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*     */     }
/* 589 */     if (this._propertyBasedCreator != null) {
/* 590 */       return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
/*     */     }
/* 592 */     TokenBuffer tokens = ctxt.bufferForInputBuffering(p);
/* 593 */     tokens.writeStartObject();
/* 594 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/*     */     
/* 596 */     if (this._injectables != null) {
/* 597 */       injectValues(ctxt, bean);
/*     */     }
/*     */     
/* 600 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 601 */     for (; p.currentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
/* 602 */       String propName = p.currentName();
/* 603 */       p.nextToken();
/* 604 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/* 605 */       if (prop != null) {
/* 606 */         if (activeView != null && !prop.visibleInView(activeView)) {
/* 607 */           p.skipChildren();
/*     */         } else {
/*     */           
/*     */           try {
/* 611 */             bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 612 */           } catch (Exception e) {
/* 613 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */         
/*     */         }
/*     */       
/* 618 */       } else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/* 619 */         handleIgnoredProperty(p, ctxt, bean, propName);
/*     */       }
/*     */       else {
/*     */         
/* 623 */         tokens.writeFieldName(propName);
/* 624 */         tokens.copyCurrentStructure(p);
/*     */         
/* 626 */         if (this._anySetter != null) {
/*     */           try {
/* 628 */             this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/* 629 */           } catch (Exception e) {
/* 630 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/* 635 */     tokens.writeEndObject();
/* 636 */     return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 644 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/* 645 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/*     */     
/* 647 */     TokenBuffer tokens = ctxt.bufferForInputBuffering(p);
/* 648 */     tokens.writeStartObject();
/* 649 */     Object builder = null;
/*     */     
/* 651 */     JsonToken t = p.currentToken();
/* 652 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 653 */       String propName = p.currentName();
/* 654 */       p.nextToken();
/*     */       
/* 656 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/*     */       
/* 658 */       if (!buffer.readIdProperty(propName) || creatorProp != null)
/*     */       {
/*     */         
/* 661 */         if (creatorProp != null) {
/*     */           
/* 663 */           if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
/* 664 */             t = p.nextToken();
/*     */             try {
/* 666 */               builder = creator.build(ctxt, buffer);
/* 667 */             } catch (Exception e) {
/* 668 */               wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*     */             } 
/*     */             
/* 671 */             if (builder.getClass() != this._beanType.getRawClass()) {
/* 672 */               return handlePolymorphic(p, ctxt, builder, tokens);
/*     */             }
/* 674 */             return deserializeWithUnwrapped(p, ctxt, builder, tokens);
/*     */           }
/*     */         
/*     */         } else {
/*     */           
/* 679 */           SettableBeanProperty prop = this._beanProperties.find(propName);
/* 680 */           if (prop != null) {
/* 681 */             buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/*     */           
/*     */           }
/* 684 */           else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/* 685 */             handleIgnoredProperty(p, ctxt, handledType(), propName);
/*     */           } else {
/*     */             
/* 688 */             tokens.writeFieldName(propName);
/* 689 */             tokens.copyCurrentStructure(p);
/*     */             
/* 691 */             if (this._anySetter != null)
/* 692 */               buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt)); 
/*     */           } 
/*     */         }  } 
/* 695 */     }  tokens.writeEndObject();
/*     */ 
/*     */     
/* 698 */     if (builder == null) {
/*     */       try {
/* 700 */         builder = creator.build(ctxt, buffer);
/* 701 */       } catch (Exception e) {
/* 702 */         return wrapInstantiationProblem(e, ctxt);
/*     */       } 
/*     */     }
/* 705 */     return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, builder, tokens);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object builder, TokenBuffer tokens) throws IOException {
/* 712 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 713 */     for (JsonToken t = p.currentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 714 */       String propName = p.currentName();
/* 715 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/* 716 */       p.nextToken();
/* 717 */       if (prop != null) {
/* 718 */         if (activeView != null && !prop.visibleInView(activeView)) {
/* 719 */           p.skipChildren();
/*     */         } else {
/*     */           
/*     */           try {
/* 723 */             builder = prop.deserializeSetAndReturn(p, ctxt, builder);
/* 724 */           } catch (Exception e) {
/* 725 */             wrapAndThrow(e, builder, propName, ctxt);
/*     */           }
/*     */         
/*     */         } 
/* 729 */       } else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/* 730 */         handleIgnoredProperty(p, ctxt, builder, propName);
/*     */       }
/*     */       else {
/*     */         
/* 734 */         tokens.writeFieldName(propName);
/* 735 */         tokens.copyCurrentStructure(p);
/*     */         
/* 737 */         if (this._anySetter != null)
/* 738 */           this._anySetter.deserializeAndSet(p, ctxt, builder, propName); 
/*     */       } 
/*     */     } 
/* 741 */     tokens.writeEndObject();
/* 742 */     return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, builder, tokens);
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
/*     */   protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 755 */     if (this._propertyBasedCreator != null) {
/* 756 */       return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
/*     */     }
/* 758 */     return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/* 765 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 766 */     ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/*     */     
/* 768 */     for (JsonToken t = p.currentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 769 */       String propName = p.currentName();
/* 770 */       t = p.nextToken();
/* 771 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/* 772 */       if (prop != null) {
/*     */         
/* 774 */         if (t.isScalarValue()) {
/* 775 */           ext.handleTypePropertyValue(p, ctxt, propName, bean);
/*     */         }
/* 777 */         if (activeView != null && !prop.visibleInView(activeView)) {
/* 778 */           p.skipChildren();
/*     */         } else {
/*     */           
/*     */           try {
/* 782 */             bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 783 */           } catch (Exception e) {
/* 784 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */         
/*     */         }
/*     */       
/* 789 */       } else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/* 790 */         handleIgnoredProperty(p, ctxt, bean, propName);
/*     */ 
/*     */       
/*     */       }
/* 794 */       else if (!ext.handlePropertyValue(p, ctxt, propName, bean)) {
/*     */ 
/*     */ 
/*     */         
/* 798 */         if (this._anySetter != null) {
/*     */           try {
/* 800 */             this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/* 801 */           } catch (Exception e) {
/* 802 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */         
/*     */         } else {
/*     */           
/* 807 */           handleUnknownProperty(p, ctxt, bean, propName);
/*     */         } 
/*     */       } 
/*     */     } 
/* 811 */     return ext.complete(p, ctxt, bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 819 */     JavaType t = this._targetType;
/* 820 */     return ctxt.reportBadDefinition(t, String.format("Deserialization (of %s) with Builder, External type id, @JsonCreator not yet implemented", new Object[] { t }));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/BuilderBasedDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */