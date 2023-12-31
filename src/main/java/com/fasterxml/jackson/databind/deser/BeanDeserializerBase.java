/*      */ package com.fasterxml.jackson.databind.deser;
/*      */ import com.fasterxml.jackson.annotation.JsonFormat;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*      */ import com.fasterxml.jackson.databind.BeanProperty;
/*      */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*      */ import com.fasterxml.jackson.databind.DeserializationContext;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*      */ import com.fasterxml.jackson.databind.JsonMappingException;
/*      */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*      */ import com.fasterxml.jackson.databind.PropertyName;
/*      */ import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
/*      */ import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
/*      */ import com.fasterxml.jackson.databind.deser.impl.MergingSettableBeanProperty;
/*      */ import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*      */ import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*      */ import com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
/*      */ import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
/*      */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*      */ import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*      */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ 
/*      */ public abstract class BeanDeserializerBase extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer, ValueInstantiator.Gettable, Serializable {
/*   35 */   protected static final PropertyName TEMP_PROPERTY_NAME = new PropertyName("#temporary-name");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JavaType _beanType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonFormat.Shape _serializationShape;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final ValueInstantiator _valueInstantiator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _delegateDeserializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _arrayDelegateDeserializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected PropertyBasedCreator _propertyBasedCreator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _nonStandardCreation;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _vanillaProcessing;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final BeanPropertyMap _beanProperties;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final ValueInjector[] _injectables;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SettableAnyProperty _anySetter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Set<String> _ignorableProps;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Set<String> _includableProps;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _ignoreAllUnknown;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _needViewProcesing;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Map<String, SettableBeanProperty> _backRefs;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected transient HashMap<ClassKey, JsonDeserializer<Object>> _subDeserializers;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ExternalTypeHandler _externalTypeIdHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final ObjectIdReader _objectIdReader;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeanDeserializerBase(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, Set<String> includableProps, boolean hasViews) {
/*  218 */     super(beanDesc.getType());
/*  219 */     this._beanType = beanDesc.getType();
/*      */     
/*  221 */     this._valueInstantiator = builder.getValueInstantiator();
/*  222 */     this._delegateDeserializer = null;
/*  223 */     this._arrayDelegateDeserializer = null;
/*  224 */     this._propertyBasedCreator = null;
/*      */     
/*  226 */     this._beanProperties = properties;
/*  227 */     this._backRefs = backRefs;
/*  228 */     this._ignorableProps = ignorableProps;
/*  229 */     this._ignoreAllUnknown = ignoreAllUnknown;
/*  230 */     this._includableProps = includableProps;
/*      */     
/*  232 */     this._anySetter = builder.getAnySetter();
/*  233 */     List<ValueInjector> injectables = builder.getInjectables();
/*  234 */     this
/*  235 */       ._injectables = (injectables == null || injectables.isEmpty()) ? null : injectables.<ValueInjector>toArray(new ValueInjector[injectables.size()]);
/*  236 */     this._objectIdReader = builder.getObjectIdReader();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  243 */     this
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  249 */       ._nonStandardCreation = (this._unwrappedPropertyHandler != null || this._valueInstantiator.canCreateUsingDelegate() || this._valueInstantiator.canCreateFromObjectWith() || !this._valueInstantiator.canCreateUsingDefault());
/*      */ 
/*      */ 
/*      */     
/*  253 */     JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/*  254 */     this._serializationShape = format.getShape();
/*      */     
/*  256 */     this._needViewProcesing = hasViews;
/*  257 */     this._vanillaProcessing = (!this._nonStandardCreation && this._injectables == null && !this._needViewProcesing && this._objectIdReader == null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeanDeserializerBase(BeanDeserializerBase src) {
/*  266 */     this(src, src._ignoreAllUnknown);
/*      */   }
/*      */ 
/*      */   
/*      */   protected BeanDeserializerBase(BeanDeserializerBase src, boolean ignoreAllUnknown) {
/*  271 */     super(src._beanType);
/*      */     
/*  273 */     this._beanType = src._beanType;
/*      */     
/*  275 */     this._valueInstantiator = src._valueInstantiator;
/*  276 */     this._delegateDeserializer = src._delegateDeserializer;
/*  277 */     this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
/*  278 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  280 */     this._beanProperties = src._beanProperties;
/*  281 */     this._backRefs = src._backRefs;
/*  282 */     this._ignorableProps = src._ignorableProps;
/*  283 */     this._ignoreAllUnknown = ignoreAllUnknown;
/*  284 */     this._includableProps = src._includableProps;
/*  285 */     this._anySetter = src._anySetter;
/*  286 */     this._injectables = src._injectables;
/*  287 */     this._objectIdReader = src._objectIdReader;
/*      */     
/*  289 */     this._nonStandardCreation = src._nonStandardCreation;
/*  290 */     this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/*  291 */     this._needViewProcesing = src._needViewProcesing;
/*  292 */     this._serializationShape = src._serializationShape;
/*      */     
/*  294 */     this._vanillaProcessing = src._vanillaProcessing;
/*      */   }
/*      */ 
/*      */   
/*      */   protected BeanDeserializerBase(BeanDeserializerBase src, NameTransformer unwrapper) {
/*  299 */     super(src._beanType);
/*      */     
/*  301 */     this._beanType = src._beanType;
/*      */     
/*  303 */     this._valueInstantiator = src._valueInstantiator;
/*  304 */     this._delegateDeserializer = src._delegateDeserializer;
/*  305 */     this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
/*  306 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  308 */     this._backRefs = src._backRefs;
/*  309 */     this._ignorableProps = src._ignorableProps;
/*  310 */     this._ignoreAllUnknown = (unwrapper != null || src._ignoreAllUnknown);
/*  311 */     this._includableProps = src._includableProps;
/*  312 */     this._anySetter = src._anySetter;
/*  313 */     this._injectables = src._injectables;
/*  314 */     this._objectIdReader = src._objectIdReader;
/*      */     
/*  316 */     this._nonStandardCreation = src._nonStandardCreation;
/*  317 */     UnwrappedPropertyHandler uph = src._unwrappedPropertyHandler;
/*      */     
/*  319 */     if (unwrapper != null) {
/*      */       
/*  321 */       if (uph != null) {
/*  322 */         uph = uph.renameAll(unwrapper);
/*      */       }
/*      */       
/*  325 */       this._beanProperties = src._beanProperties.renameAll(unwrapper);
/*      */     } else {
/*  327 */       this._beanProperties = src._beanProperties;
/*      */     } 
/*  329 */     this._unwrappedPropertyHandler = uph;
/*  330 */     this._needViewProcesing = src._needViewProcesing;
/*  331 */     this._serializationShape = src._serializationShape;
/*      */ 
/*      */     
/*  334 */     this._vanillaProcessing = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public BeanDeserializerBase(BeanDeserializerBase src, ObjectIdReader oir) {
/*  339 */     super(src._beanType);
/*  340 */     this._beanType = src._beanType;
/*      */     
/*  342 */     this._valueInstantiator = src._valueInstantiator;
/*  343 */     this._delegateDeserializer = src._delegateDeserializer;
/*  344 */     this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
/*  345 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  347 */     this._backRefs = src._backRefs;
/*  348 */     this._ignorableProps = src._ignorableProps;
/*  349 */     this._ignoreAllUnknown = src._ignoreAllUnknown;
/*  350 */     this._includableProps = src._includableProps;
/*  351 */     this._anySetter = src._anySetter;
/*  352 */     this._injectables = src._injectables;
/*      */     
/*  354 */     this._nonStandardCreation = src._nonStandardCreation;
/*  355 */     this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/*  356 */     this._needViewProcesing = src._needViewProcesing;
/*  357 */     this._serializationShape = src._serializationShape;
/*      */ 
/*      */     
/*  360 */     this._objectIdReader = oir;
/*      */     
/*  362 */     if (oir == null) {
/*  363 */       this._beanProperties = src._beanProperties;
/*  364 */       this._vanillaProcessing = src._vanillaProcessing;
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  370 */       ObjectIdValueProperty idProp = new ObjectIdValueProperty(oir, PropertyMetadata.STD_REQUIRED);
/*  371 */       this._beanProperties = src._beanProperties.withProperty((SettableBeanProperty)idProp);
/*  372 */       this._vanillaProcessing = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BeanDeserializerBase(BeanDeserializerBase src, Set<String> ignorableProps, Set<String> includableProps) {
/*  382 */     super(src._beanType);
/*  383 */     this._beanType = src._beanType;
/*      */     
/*  385 */     this._valueInstantiator = src._valueInstantiator;
/*  386 */     this._delegateDeserializer = src._delegateDeserializer;
/*  387 */     this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
/*  388 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  390 */     this._backRefs = src._backRefs;
/*  391 */     this._ignorableProps = ignorableProps;
/*  392 */     this._ignoreAllUnknown = src._ignoreAllUnknown;
/*  393 */     this._includableProps = includableProps;
/*  394 */     this._anySetter = src._anySetter;
/*  395 */     this._injectables = src._injectables;
/*      */     
/*  397 */     this._nonStandardCreation = src._nonStandardCreation;
/*  398 */     this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/*  399 */     this._needViewProcesing = src._needViewProcesing;
/*  400 */     this._serializationShape = src._serializationShape;
/*      */     
/*  402 */     this._vanillaProcessing = src._vanillaProcessing;
/*  403 */     this._objectIdReader = src._objectIdReader;
/*      */ 
/*      */ 
/*      */     
/*  407 */     this._beanProperties = src._beanProperties.withoutProperties(ignorableProps, includableProps);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeanDeserializerBase(BeanDeserializerBase src, BeanPropertyMap beanProps) {
/*  415 */     super(src._beanType);
/*  416 */     this._beanType = src._beanType;
/*      */     
/*  418 */     this._valueInstantiator = src._valueInstantiator;
/*  419 */     this._delegateDeserializer = src._delegateDeserializer;
/*  420 */     this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
/*  421 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  423 */     this._beanProperties = beanProps;
/*  424 */     this._backRefs = src._backRefs;
/*  425 */     this._ignorableProps = src._ignorableProps;
/*  426 */     this._ignoreAllUnknown = src._ignoreAllUnknown;
/*  427 */     this._includableProps = src._includableProps;
/*  428 */     this._anySetter = src._anySetter;
/*  429 */     this._injectables = src._injectables;
/*  430 */     this._objectIdReader = src._objectIdReader;
/*      */     
/*  432 */     this._nonStandardCreation = src._nonStandardCreation;
/*  433 */     this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/*  434 */     this._needViewProcesing = src._needViewProcesing;
/*  435 */     this._serializationShape = src._serializationShape;
/*      */     
/*  437 */     this._vanillaProcessing = src._vanillaProcessing;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected BeanDeserializerBase(BeanDeserializerBase src, Set<String> ignorableProps) {
/*  443 */     this(src, ignorableProps, src._includableProps);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract JsonDeserializer<Object> unwrappingDeserializer(NameTransformer paramNameTransformer);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract BeanDeserializerBase withObjectIdReader(ObjectIdReader paramObjectIdReader);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract BeanDeserializerBase withByNameInclusion(Set<String> paramSet1, Set<String> paramSet2);
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract BeanDeserializerBase withIgnoreAllUnknown(boolean paramBoolean);
/*      */ 
/*      */ 
/*      */   
/*      */   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
/*  468 */     throw new UnsupportedOperationException("Class " + getClass().getName() + " does not override `withBeanProperties()`, needs to");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected abstract BeanDeserializerBase asArrayDeserializer();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps) {
/*  486 */     return withByNameInclusion(ignorableProps, this._includableProps);
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
/*      */   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
/*      */     SettableBeanProperty[] creatorProps;
/*  503 */     ExternalTypeHandler.Builder extTypes = null;
/*      */ 
/*      */ 
/*      */     
/*  507 */     if (this._valueInstantiator.canCreateFromObjectWith()) {
/*  508 */       creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  513 */       if (this._ignorableProps != null || this._includableProps != null) {
/*  514 */         for (int i = 0, end = creatorProps.length; i < end; i++) {
/*  515 */           SettableBeanProperty prop = creatorProps[i];
/*  516 */           if (IgnorePropertiesUtil.shouldIgnore(prop.getName(), this._ignorableProps, this._includableProps)) {
/*  517 */             creatorProps[i].markAsIgnorable();
/*      */           }
/*      */         } 
/*      */       }
/*      */     } else {
/*  522 */       creatorProps = null;
/*      */     } 
/*  524 */     UnwrappedPropertyHandler unwrapped = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  534 */     for (SettableBeanProperty prop : this._beanProperties) {
/*  535 */       if (!prop.hasValueDeserializer()) {
/*      */         
/*  537 */         JsonDeserializer<?> deser = findConvertingDeserializer(ctxt, prop);
/*  538 */         if (deser == null) {
/*  539 */           deser = ctxt.findNonContextualValueDeserializer(prop.getType());
/*      */         }
/*  541 */         SettableBeanProperty newProp = prop.withValueDeserializer(deser);
/*  542 */         _replaceProperty(this._beanProperties, creatorProps, prop, newProp);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  547 */     for (SettableBeanProperty origProp : this._beanProperties) {
/*  548 */       SettableBeanProperty prop = origProp;
/*  549 */       JsonDeserializer<?> deser = prop.getValueDeserializer();
/*  550 */       deser = ctxt.handlePrimaryContextualization(deser, (BeanProperty)prop, prop.getType());
/*  551 */       prop = prop.withValueDeserializer(deser);
/*      */       
/*  553 */       prop = _resolveManagedReferenceProperty(ctxt, prop);
/*      */ 
/*      */       
/*  556 */       if (!(prop instanceof ManagedReferenceProperty)) {
/*  557 */         prop = _resolvedObjectIdProperty(ctxt, prop);
/*      */       }
/*      */       
/*  560 */       NameTransformer xform = _findPropertyUnwrapper(ctxt, prop);
/*  561 */       if (xform != null) {
/*  562 */         JsonDeserializer<Object> orig = prop.getValueDeserializer();
/*  563 */         JsonDeserializer<Object> unwrapping = orig.unwrappingDeserializer(xform);
/*  564 */         if (unwrapping != orig && unwrapping != null) {
/*  565 */           prop = prop.withValueDeserializer(unwrapping);
/*  566 */           if (unwrapped == null) {
/*  567 */             unwrapped = new UnwrappedPropertyHandler();
/*      */           }
/*  569 */           unwrapped.addProperty(prop);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  574 */           this._beanProperties.remove(prop);
/*      */ 
/*      */           
/*      */           continue;
/*      */         } 
/*      */       } 
/*      */       
/*  581 */       PropertyMetadata md = prop.getMetadata();
/*  582 */       prop = _resolveMergeAndNullSettings(ctxt, prop, md);
/*      */ 
/*      */       
/*  585 */       prop = _resolveInnerClassValuedProperty(ctxt, prop);
/*  586 */       if (prop != origProp) {
/*  587 */         _replaceProperty(this._beanProperties, creatorProps, origProp, prop);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  592 */       if (prop.hasValueTypeDeserializer()) {
/*  593 */         TypeDeserializer typeDeser = prop.getValueTypeDeserializer();
/*  594 */         if (typeDeser.getTypeInclusion() == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
/*  595 */           if (extTypes == null) {
/*  596 */             extTypes = ExternalTypeHandler.builder(this._beanType);
/*      */           }
/*  598 */           extTypes.addExternal(prop, typeDeser);
/*      */           
/*  600 */           this._beanProperties.remove(prop);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  606 */     if (this._anySetter != null && !this._anySetter.hasValueDeserializer()) {
/*  607 */       this._anySetter = this._anySetter.withValueDeserializer(findDeserializer(ctxt, this._anySetter
/*  608 */             .getType(), this._anySetter.getProperty()));
/*      */     }
/*      */     
/*  611 */     if (this._valueInstantiator.canCreateUsingDelegate()) {
/*  612 */       JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
/*  613 */       if (delegateType == null)
/*  614 */         ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", new Object[] {
/*      */                 
/*  616 */                 ClassUtil.getTypeDescription(this._beanType), ClassUtil.classNameOf(this._valueInstantiator)
/*      */               })); 
/*  618 */       this._delegateDeserializer = _findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator
/*  619 */           .getDelegateCreator());
/*      */     } 
/*      */ 
/*      */     
/*  623 */     if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
/*  624 */       JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
/*  625 */       if (delegateType == null)
/*  626 */         ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", new Object[] {
/*      */                 
/*  628 */                 ClassUtil.getTypeDescription(this._beanType), ClassUtil.classNameOf(this._valueInstantiator)
/*      */               })); 
/*  630 */       this._arrayDelegateDeserializer = _findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator
/*  631 */           .getArrayDelegateCreator());
/*      */     } 
/*      */ 
/*      */     
/*  635 */     if (creatorProps != null) {
/*  636 */       this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, this._beanProperties);
/*      */     }
/*      */ 
/*      */     
/*  640 */     if (extTypes != null) {
/*      */ 
/*      */       
/*  643 */       this._externalTypeIdHandler = extTypes.build(this._beanProperties);
/*      */       
/*  645 */       this._nonStandardCreation = true;
/*      */     } 
/*      */     
/*  648 */     this._unwrappedPropertyHandler = unwrapped;
/*  649 */     if (unwrapped != null) {
/*  650 */       this._nonStandardCreation = true;
/*      */     }
/*      */     
/*  653 */     this._vanillaProcessing = (this._vanillaProcessing && !this._nonStandardCreation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _replaceProperty(BeanPropertyMap props, SettableBeanProperty[] creatorProps, SettableBeanProperty origProp, SettableBeanProperty newProp) {
/*  662 */     props.replace(origProp, newProp);
/*      */     
/*  664 */     if (creatorProps != null)
/*      */     {
/*      */       
/*  667 */       for (int i = 0, len = creatorProps.length; i < len; i++) {
/*  668 */         if (creatorProps[i] == origProp) {
/*  669 */           creatorProps[i] = newProp;
/*      */           return;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private JsonDeserializer<Object> _findDelegateDeserializer(DeserializationContext ctxt, JavaType delegateType, AnnotatedWithParams delegateCreator) throws JsonMappingException {
/*  691 */     BeanProperty.Std property = new BeanProperty.Std(TEMP_PROPERTY_NAME, delegateType, null, (AnnotatedMember)delegateCreator, PropertyMetadata.STD_OPTIONAL);
/*      */ 
/*      */     
/*  694 */     TypeDeserializer td = (TypeDeserializer)delegateType.getTypeHandler();
/*  695 */     if (td == null) {
/*  696 */       td = ctxt.getConfig().findTypeDeserializer(delegateType);
/*      */     }
/*      */ 
/*      */     
/*  700 */     JsonDeserializer<Object> dd = (JsonDeserializer<Object>)delegateType.getValueHandler();
/*  701 */     if (dd == null) {
/*  702 */       dd = findDeserializer(ctxt, delegateType, (BeanProperty)property);
/*      */     } else {
/*  704 */       dd = ctxt.handleSecondaryContextualization(dd, (BeanProperty)property, delegateType);
/*      */     } 
/*  706 */     if (td != null) {
/*  707 */       td = td.forProperty((BeanProperty)property);
/*  708 */       return (JsonDeserializer<Object>)new TypeWrappedDeserializer(td, dd);
/*      */     } 
/*  710 */     return dd;
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
/*      */   protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
/*  727 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*  728 */     if (intr != null) {
/*  729 */       Object convDef = intr.findDeserializationConverter((Annotated)prop.getMember());
/*  730 */       if (convDef != null) {
/*  731 */         Converter<Object, Object> conv = ctxt.converterInstance((Annotated)prop.getMember(), convDef);
/*  732 */         JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/*      */ 
/*      */         
/*  735 */         JsonDeserializer<?> deser = ctxt.findNonContextualValueDeserializer(delegateType);
/*  736 */         return (JsonDeserializer<Object>)new StdDelegatingDeserializer(conv, delegateType, deser);
/*      */       } 
/*      */     } 
/*  739 */     return null;
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
/*      */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/*  753 */     ObjectIdReader oir = this._objectIdReader;
/*      */ 
/*      */     
/*  756 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*  757 */     AnnotatedMember accessor = _neitherNull(property, intr) ? property.getMember() : null;
/*  758 */     if (accessor != null) {
/*  759 */       ObjectIdInfo objectIdInfo = intr.findObjectIdInfo((Annotated)accessor);
/*  760 */       if (objectIdInfo != null) {
/*      */         JavaType idType; SettableBeanProperty idProp; ObjectIdGenerator<?> idGen;
/*  762 */         objectIdInfo = intr.findObjectReferenceInfo((Annotated)accessor, objectIdInfo);
/*      */         
/*  764 */         Class<?> implClass = objectIdInfo.getGeneratorType();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  769 */         ObjectIdResolver resolver = ctxt.objectIdResolverInstance((Annotated)accessor, objectIdInfo);
/*  770 */         if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
/*  771 */           PropertyName propName = objectIdInfo.getPropertyName();
/*  772 */           idProp = findProperty(propName);
/*  773 */           if (idProp == null)
/*  774 */             return (JsonDeserializer)ctxt.reportBadDefinition(this._beanType, String.format("Invalid Object Id definition for %s: cannot find property with name %s", new Object[] {
/*      */                     
/*  776 */                     ClassUtil.nameOf(handledType()), ClassUtil.name(propName)
/*      */                   })); 
/*  778 */           idType = idProp.getType();
/*  779 */           PropertyBasedObjectIdGenerator propertyBasedObjectIdGenerator = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
/*      */         } else {
/*  781 */           JavaType type = ctxt.constructType(implClass);
/*  782 */           idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/*  783 */           idProp = null;
/*  784 */           idGen = ctxt.objectIdGeneratorInstance((Annotated)accessor, objectIdInfo);
/*      */         } 
/*  786 */         JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
/*  787 */         oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  792 */     BeanDeserializerBase contextual = this;
/*  793 */     if (oir != null && oir != this._objectIdReader) {
/*  794 */       contextual = contextual.withObjectIdReader(oir);
/*      */     }
/*      */     
/*  797 */     if (accessor != null) {
/*  798 */       contextual = _handleByNameInclusion(ctxt, intr, contextual, accessor);
/*      */     }
/*      */ 
/*      */     
/*  802 */     JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
/*  803 */     JsonFormat.Shape shape = null;
/*  804 */     if (format != null) {
/*  805 */       if (format.hasShape()) {
/*  806 */         shape = format.getShape();
/*      */       }
/*      */       
/*  809 */       Boolean B = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
/*  810 */       if (B != null) {
/*  811 */         BeanPropertyMap propsOrig = this._beanProperties;
/*  812 */         BeanPropertyMap props = propsOrig.withCaseInsensitivity(B.booleanValue());
/*  813 */         if (props != propsOrig) {
/*  814 */           contextual = contextual.withBeanProperties(props);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  819 */     if (shape == null) {
/*  820 */       shape = this._serializationShape;
/*      */     }
/*  822 */     if (shape == JsonFormat.Shape.ARRAY) {
/*  823 */       contextual = contextual.asArrayDeserializer();
/*      */     }
/*  825 */     return (JsonDeserializer<?>)contextual;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeanDeserializerBase _handleByNameInclusion(DeserializationContext ctxt, AnnotationIntrospector intr, BeanDeserializerBase contextual, AnnotatedMember accessor) throws JsonMappingException {
/*      */     Set<String> newNamesToIgnore;
/*  834 */     DeserializationConfig config = ctxt.getConfig();
/*  835 */     JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnoralByName((MapperConfig)config, (Annotated)accessor);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  841 */     if (ignorals.getIgnoreUnknown() && !this._ignoreAllUnknown) {
/*  842 */       contextual = contextual.withIgnoreAllUnknown(true);
/*      */     }
/*      */     
/*  845 */     Set<String> namesToIgnore = ignorals.findIgnoredForDeserialization();
/*  846 */     Set<String> prevNamesToIgnore = contextual._ignorableProps;
/*      */ 
/*      */     
/*  849 */     if (namesToIgnore.isEmpty()) {
/*  850 */       newNamesToIgnore = prevNamesToIgnore;
/*  851 */     } else if (prevNamesToIgnore == null || prevNamesToIgnore.isEmpty()) {
/*  852 */       newNamesToIgnore = namesToIgnore;
/*      */     } else {
/*  854 */       newNamesToIgnore = new HashSet<>(prevNamesToIgnore);
/*  855 */       newNamesToIgnore.addAll(namesToIgnore);
/*      */     } 
/*      */     
/*  858 */     Set<String> prevNamesToInclude = contextual._includableProps;
/*  859 */     Set<String> newNamesToInclude = IgnorePropertiesUtil.combineNamesToInclude(prevNamesToInclude, intr
/*  860 */         .findPropertyInclusionByName((MapperConfig)config, (Annotated)accessor).getIncluded());
/*      */     
/*  862 */     if (newNamesToIgnore != prevNamesToIgnore || newNamesToInclude != prevNamesToInclude)
/*      */     {
/*  864 */       contextual = contextual.withByNameInclusion(newNamesToIgnore, newNamesToInclude);
/*      */     }
/*  866 */     return contextual;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SettableBeanProperty _resolveManagedReferenceProperty(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
/*  877 */     String refName = prop.getManagedReferenceName();
/*  878 */     if (refName == null) {
/*  879 */       return prop;
/*      */     }
/*  881 */     JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
/*  882 */     SettableBeanProperty backProp = valueDeser.findBackReference(refName);
/*  883 */     if (backProp == null) {
/*  884 */       return (SettableBeanProperty)ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference %s: no back reference property found from type %s", new Object[] {
/*      */               
/*  886 */               ClassUtil.name(refName), ClassUtil.getTypeDescription(prop.getType())
/*      */             }));
/*      */     }
/*  889 */     JavaType referredType = this._beanType;
/*  890 */     JavaType backRefType = backProp.getType();
/*  891 */     boolean isContainer = prop.getType().isContainerType();
/*  892 */     if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass()))
/*  893 */       ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference %s: back reference type (%s) not compatible with managed type (%s)", new Object[] {
/*      */               
/*  895 */               ClassUtil.name(refName), ClassUtil.getTypeDescription(backRefType), referredType
/*  896 */               .getRawClass().getName()
/*      */             })); 
/*  898 */     return (SettableBeanProperty)new ManagedReferenceProperty(prop, refName, backProp, isContainer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SettableBeanProperty _resolvedObjectIdProperty(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
/*  908 */     ObjectIdInfo objectIdInfo = prop.getObjectIdInfo();
/*  909 */     JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
/*  910 */     ObjectIdReader objectIdReader = (valueDeser == null) ? null : valueDeser.getObjectIdReader();
/*  911 */     if (objectIdInfo == null && objectIdReader == null) {
/*  912 */       return prop;
/*      */     }
/*  914 */     return (SettableBeanProperty)new ObjectIdReferenceProperty(prop, objectIdInfo);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected NameTransformer _findPropertyUnwrapper(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
/*  925 */     AnnotatedMember am = prop.getMember();
/*  926 */     if (am != null) {
/*  927 */       NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(am);
/*  928 */       if (unwrapper != null) {
/*      */ 
/*      */         
/*  931 */         if (prop instanceof CreatorProperty) {
/*  932 */           ctxt.reportBadDefinition(getValueType(), String.format("Cannot define Creator property \"%s\" as `@JsonUnwrapped`: combination not yet supported", new Object[] { prop
/*      */                   
/*  934 */                   .getName() }));
/*      */         }
/*  936 */         return unwrapper;
/*      */       } 
/*      */     } 
/*  939 */     return null;
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
/*      */   protected SettableBeanProperty _resolveInnerClassValuedProperty(DeserializationContext ctxt, SettableBeanProperty prop) {
/*  952 */     JsonDeserializer<Object> deser = prop.getValueDeserializer();
/*      */     
/*  954 */     if (deser instanceof BeanDeserializerBase) {
/*  955 */       BeanDeserializerBase bd = (BeanDeserializerBase)deser;
/*  956 */       ValueInstantiator vi = bd.getValueInstantiator();
/*  957 */       if (!vi.canCreateUsingDefault()) {
/*  958 */         Class<?> valueClass = prop.getType().getRawClass();
/*      */         
/*  960 */         Class<?> enclosing = ClassUtil.getOuterClass(valueClass);
/*      */         
/*  962 */         if (enclosing != null && enclosing == this._beanType.getRawClass()) {
/*  963 */           for (Constructor<?> ctor : valueClass.getConstructors()) {
/*  964 */             Class<?>[] paramTypes = ctor.getParameterTypes();
/*  965 */             if (paramTypes.length == 1 && 
/*  966 */               enclosing.equals(paramTypes[0])) {
/*  967 */               if (ctxt.canOverrideAccessModifiers()) {
/*  968 */                 ClassUtil.checkAndFixAccess(ctor, ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*      */               }
/*  970 */               return (SettableBeanProperty)new InnerClassProperty(prop, ctor);
/*      */             } 
/*      */           } 
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  977 */     return prop;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected SettableBeanProperty _resolveMergeAndNullSettings(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata) throws JsonMappingException {
/*      */     MergingSettableBeanProperty mergingSettableBeanProperty;
/*      */     SettableBeanProperty settableBeanProperty;
/*  985 */     PropertyMetadata.MergeInfo merge = propMetadata.getMergeInfo();
/*      */     
/*  987 */     if (merge != null) {
/*  988 */       JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
/*  989 */       Boolean mayMerge = valueDeser.supportsUpdate(ctxt.getConfig());
/*      */       
/*  991 */       if (mayMerge == null) {
/*      */         
/*  993 */         if (merge.fromDefaults) {
/*  994 */           return prop;
/*      */         }
/*  996 */       } else if (!mayMerge.booleanValue()) {
/*  997 */         if (!merge.fromDefaults)
/*      */         {
/*      */           
/* 1000 */           ctxt.handleBadMerge(valueDeser);
/*      */         }
/* 1002 */         return prop;
/*      */       } 
/*      */       
/* 1005 */       AnnotatedMember accessor = merge.getter;
/* 1006 */       accessor.fixAccess(ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/* 1007 */       if (!(prop instanceof com.fasterxml.jackson.databind.deser.impl.SetterlessProperty)) {
/* 1008 */         mergingSettableBeanProperty = MergingSettableBeanProperty.construct(prop, accessor);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1013 */     NullValueProvider nuller = findValueNullProvider(ctxt, (SettableBeanProperty)mergingSettableBeanProperty, propMetadata);
/* 1014 */     if (nuller != null) {
/* 1015 */       settableBeanProperty = mergingSettableBeanProperty.withNullProvider(nuller);
/*      */     }
/* 1017 */     return settableBeanProperty;
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
/*      */   public AccessPattern getNullAccessPattern() {
/* 1029 */     return AccessPattern.ALWAYS_NULL;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public AccessPattern getEmptyAccessPattern() {
/* 1035 */     return AccessPattern.DYNAMIC;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/*      */     try {
/* 1042 */       return this._valueInstantiator.createUsingDefault(ctxt);
/* 1043 */     } catch (IOException e) {
/* 1044 */       return ClassUtil.throwAsMappingException(ctxt, e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCachable() {
/* 1055 */     return true;
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
/*      */   public boolean isCaseInsensitive() {
/* 1067 */     return this._beanProperties.isCaseInsensitive();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 1075 */     return Boolean.TRUE;
/*      */   }
/*      */ 
/*      */   
/*      */   public Class<?> handledType() {
/* 1080 */     return this._beanType.getRawClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectIdReader getObjectIdReader() {
/* 1090 */     return this._objectIdReader;
/*      */   }
/*      */   
/*      */   public boolean hasProperty(String propertyName) {
/* 1094 */     return (this._beanProperties.find(propertyName) != null);
/*      */   }
/*      */   
/*      */   public boolean hasViews() {
/* 1098 */     return this._needViewProcesing;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPropertyCount() {
/* 1105 */     return this._beanProperties.size();
/*      */   }
/*      */ 
/*      */   
/*      */   public Collection<Object> getKnownPropertyNames() {
/* 1110 */     ArrayList<Object> names = new ArrayList();
/* 1111 */     for (SettableBeanProperty prop : this._beanProperties) {
/* 1112 */       names.add(prop.getName());
/*      */     }
/* 1114 */     return names;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public final Class<?> getBeanClass() {
/* 1121 */     return this._beanType.getRawClass();
/*      */   }
/*      */   public JavaType getValueType() {
/* 1124 */     return this._beanType;
/*      */   }
/*      */   
/*      */   public LogicalType logicalType() {
/* 1128 */     return LogicalType.POJO;
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
/*      */   public Iterator<SettableBeanProperty> properties() {
/* 1140 */     if (this._beanProperties == null) {
/* 1141 */       throw new IllegalStateException("Can only call after BeanDeserializer has been resolved");
/*      */     }
/* 1143 */     return this._beanProperties.iterator();
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
/*      */   public Iterator<SettableBeanProperty> creatorProperties() {
/* 1155 */     if (this._propertyBasedCreator == null) {
/* 1156 */       return Collections.<SettableBeanProperty>emptyList().iterator();
/*      */     }
/* 1158 */     return this._propertyBasedCreator.properties().iterator();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public SettableBeanProperty findProperty(PropertyName propertyName) {
/* 1164 */     return findProperty(propertyName.getSimpleName());
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
/*      */   public SettableBeanProperty findProperty(String propertyName) {
/* 1177 */     SettableBeanProperty prop = (this._beanProperties == null) ? null : this._beanProperties.find(propertyName);
/* 1178 */     if (prop == null && this._propertyBasedCreator != null) {
/* 1179 */       prop = this._propertyBasedCreator.findCreatorProperty(propertyName);
/*      */     }
/* 1181 */     return prop;
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
/*      */   public SettableBeanProperty findProperty(int propertyIndex) {
/* 1197 */     SettableBeanProperty prop = (this._beanProperties == null) ? null : this._beanProperties.find(propertyIndex);
/* 1198 */     if (prop == null && this._propertyBasedCreator != null) {
/* 1199 */       prop = this._propertyBasedCreator.findCreatorProperty(propertyIndex);
/*      */     }
/* 1201 */     return prop;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettableBeanProperty findBackReference(String logicalName) {
/* 1211 */     if (this._backRefs == null) {
/* 1212 */       return null;
/*      */     }
/* 1214 */     return this._backRefs.get(logicalName);
/*      */   }
/*      */ 
/*      */   
/*      */   public ValueInstantiator getValueInstantiator() {
/* 1219 */     return this._valueInstantiator;
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
/*      */   public void replaceProperty(SettableBeanProperty original, SettableBeanProperty replacement) {
/* 1243 */     this._beanProperties.replace(original, replacement);
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
/*      */   public abstract Object deserializeFromObject(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 1265 */     if (this._objectIdReader != null) {
/*      */       
/* 1267 */       if (p.canReadObjectId()) {
/* 1268 */         Object id = p.getObjectId();
/* 1269 */         if (id != null) {
/* 1270 */           Object ob = typeDeserializer.deserializeTypedFromObject(p, ctxt);
/* 1271 */           return _handleTypedObjectId(p, ctxt, ob, id);
/*      */         } 
/*      */       } 
/*      */       
/* 1275 */       JsonToken t = p.currentToken();
/* 1276 */       if (t != null) {
/*      */         
/* 1278 */         if (t.isScalarValue()) {
/* 1279 */           return deserializeFromObjectId(p, ctxt);
/*      */         }
/*      */         
/* 1282 */         if (t == JsonToken.START_OBJECT) {
/* 1283 */           t = p.nextToken();
/*      */         }
/* 1285 */         if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader
/* 1286 */           .isValidReferencePropertyName(p.currentName(), p)) {
/* 1287 */           return deserializeFromObjectId(p, ctxt);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1292 */     return typeDeserializer.deserializeTypedFromObject(p, ctxt);
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
/*      */   protected Object _handleTypedObjectId(JsonParser p, DeserializationContext ctxt, Object pojo, Object rawId) throws IOException {
/*      */     Object id;
/* 1307 */     JsonDeserializer<Object> idDeser = this._objectIdReader.getDeserializer();
/*      */ 
/*      */ 
/*      */     
/* 1311 */     if (idDeser.handledType() == rawId.getClass()) {
/*      */       
/* 1313 */       id = rawId;
/*      */     } else {
/* 1315 */       id = _convertObjectId(p, ctxt, rawId, idDeser);
/*      */     } 
/*      */     
/* 1318 */     ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
/* 1319 */     roid.bindItem(pojo);
/*      */     
/* 1321 */     SettableBeanProperty idProp = this._objectIdReader.idProperty;
/* 1322 */     if (idProp != null) {
/* 1323 */       return idProp.setAndReturn(pojo, id);
/*      */     }
/* 1325 */     return pojo;
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
/*      */   protected Object _convertObjectId(JsonParser p, DeserializationContext ctxt, Object rawId, JsonDeserializer<Object> idDeser) throws IOException {
/* 1341 */     TokenBuffer buf = ctxt.bufferForInputBuffering(p);
/* 1342 */     if (rawId instanceof String) {
/* 1343 */       buf.writeString((String)rawId);
/* 1344 */     } else if (rawId instanceof Long) {
/* 1345 */       buf.writeNumber(((Long)rawId).longValue());
/* 1346 */     } else if (rawId instanceof Integer) {
/* 1347 */       buf.writeNumber(((Integer)rawId).intValue());
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/* 1354 */       buf.writeObject(rawId);
/*      */     } 
/* 1356 */     JsonParser bufParser = buf.asParser();
/* 1357 */     bufParser.nextToken();
/* 1358 */     return idDeser.deserialize(bufParser, ctxt);
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
/*      */   protected Object deserializeWithObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1371 */     return deserializeFromObject(p, ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object deserializeFromObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1380 */     Object id = this._objectIdReader.readObjectReference(p, ctxt);
/* 1381 */     ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
/*      */     
/* 1383 */     Object pojo = roid.resolve();
/* 1384 */     if (pojo == null) {
/* 1385 */       throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] (for " + this._beanType + ").", p
/*      */           
/* 1387 */           .getCurrentLocation(), roid);
/*      */     }
/* 1389 */     return pojo;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object deserializeFromObjectUsingNonDefault(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1395 */     JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
/* 1396 */     if (delegateDeser != null) {
/* 1397 */       Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser
/* 1398 */           .deserialize(p, ctxt));
/* 1399 */       if (this._injectables != null) {
/* 1400 */         injectValues(ctxt, bean);
/*      */       }
/* 1402 */       return bean;
/*      */     } 
/* 1404 */     if (this._propertyBasedCreator != null) {
/* 1405 */       return _deserializeUsingPropertyBased(p, ctxt);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1410 */     Class<?> raw = this._beanType.getRawClass();
/* 1411 */     if (ClassUtil.isNonStaticInnerClass(raw)) {
/* 1412 */       return ctxt.handleMissingInstantiator(raw, null, p, "non-static inner classes like this can only by instantiated using default, no-argument constructor", new Object[0]);
/*      */     }
/*      */     
/* 1415 */     return ctxt.handleMissingInstantiator(raw, getValueInstantiator(), p, "cannot deserialize from Object value (no delegate- or property-based Creator)", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected abstract Object _deserializeUsingPropertyBased(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext) throws IOException;
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserializeFromNumber(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1426 */     if (this._objectIdReader != null) {
/* 1427 */       return deserializeFromObjectId(p, ctxt);
/*      */     }
/* 1429 */     JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
/* 1430 */     JsonParser.NumberType nt = p.getNumberType();
/* 1431 */     if (nt == JsonParser.NumberType.INT) {
/* 1432 */       if (delegateDeser != null && 
/* 1433 */         !this._valueInstantiator.canCreateFromInt()) {
/* 1434 */         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser
/* 1435 */             .deserialize(p, ctxt));
/* 1436 */         if (this._injectables != null) {
/* 1437 */           injectValues(ctxt, bean);
/*      */         }
/* 1439 */         return bean;
/*      */       } 
/*      */       
/* 1442 */       return this._valueInstantiator.createFromInt(ctxt, p.getIntValue());
/*      */     } 
/* 1444 */     if (nt == JsonParser.NumberType.LONG) {
/* 1445 */       if (delegateDeser != null && 
/* 1446 */         !this._valueInstantiator.canCreateFromInt()) {
/* 1447 */         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser
/* 1448 */             .deserialize(p, ctxt));
/* 1449 */         if (this._injectables != null) {
/* 1450 */           injectValues(ctxt, bean);
/*      */         }
/* 1452 */         return bean;
/*      */       } 
/*      */       
/* 1455 */       return this._valueInstantiator.createFromLong(ctxt, p.getLongValue());
/*      */     } 
/* 1457 */     if (nt == JsonParser.NumberType.BIG_INTEGER) {
/* 1458 */       if (delegateDeser != null && 
/* 1459 */         !this._valueInstantiator.canCreateFromBigInteger()) {
/* 1460 */         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
/* 1461 */         if (this._injectables != null) {
/* 1462 */           injectValues(ctxt, bean);
/*      */         }
/* 1464 */         return bean;
/*      */       } 
/*      */       
/* 1467 */       return this._valueInstantiator.createFromBigInteger(ctxt, p.getBigIntegerValue());
/*      */     } 
/*      */     
/* 1470 */     return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", new Object[] { p
/*      */           
/* 1472 */           .getNumberValue() });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserializeFromString(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1479 */     if (this._objectIdReader != null) {
/* 1480 */       return deserializeFromObjectId(p, ctxt);
/*      */     }
/*      */ 
/*      */     
/* 1484 */     JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
/* 1485 */     if (delegateDeser != null && 
/* 1486 */       !this._valueInstantiator.canCreateFromString()) {
/* 1487 */       Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser
/* 1488 */           .deserialize(p, ctxt));
/* 1489 */       if (this._injectables != null) {
/* 1490 */         injectValues(ctxt, bean);
/*      */       }
/* 1492 */       return bean;
/*      */     } 
/*      */     
/* 1495 */     return _deserializeFromString(p, ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserializeFromDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1504 */     JsonParser.NumberType t = p.getNumberType();
/*      */     
/* 1506 */     if (t == JsonParser.NumberType.DOUBLE || t == JsonParser.NumberType.FLOAT) {
/* 1507 */       JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
/* 1508 */       if (delegateDeser != null && 
/* 1509 */         !this._valueInstantiator.canCreateFromDouble()) {
/* 1510 */         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser
/* 1511 */             .deserialize(p, ctxt));
/* 1512 */         if (this._injectables != null) {
/* 1513 */           injectValues(ctxt, bean);
/*      */         }
/* 1515 */         return bean;
/*      */       } 
/*      */       
/* 1518 */       return this._valueInstantiator.createFromDouble(ctxt, p.getDoubleValue());
/*      */     } 
/*      */     
/* 1521 */     if (t == JsonParser.NumberType.BIG_DECIMAL) {
/* 1522 */       JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
/* 1523 */       if (delegateDeser != null && 
/* 1524 */         !this._valueInstantiator.canCreateFromBigDecimal()) {
/* 1525 */         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
/* 1526 */         if (this._injectables != null) {
/* 1527 */           injectValues(ctxt, bean);
/*      */         }
/* 1529 */         return bean;
/*      */       } 
/*      */ 
/*      */       
/* 1533 */       return this._valueInstantiator.createFromBigDecimal(ctxt, p.getDecimalValue());
/*      */     } 
/*      */     
/* 1536 */     return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", new Object[] { p
/*      */           
/* 1538 */           .getNumberValue() });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserializeFromBoolean(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1546 */     JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
/* 1547 */     if (delegateDeser != null && 
/* 1548 */       !this._valueInstantiator.canCreateFromBoolean()) {
/* 1549 */       Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser
/* 1550 */           .deserialize(p, ctxt));
/* 1551 */       if (this._injectables != null) {
/* 1552 */         injectValues(ctxt, bean);
/*      */       }
/* 1554 */       return bean;
/*      */     } 
/*      */     
/* 1557 */     boolean value = (p.currentToken() == JsonToken.VALUE_TRUE);
/* 1558 */     return this._valueInstantiator.createFromBoolean(ctxt, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Object deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1569 */     return _deserializeFromArray(p, ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserializeFromEmbedded(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1577 */     if (this._objectIdReader != null) {
/* 1578 */       return deserializeFromObjectId(p, ctxt);
/*      */     }
/*      */     
/* 1581 */     JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
/* 1582 */     if (delegateDeser != null && 
/* 1583 */       !this._valueInstantiator.canCreateFromString()) {
/* 1584 */       Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser
/* 1585 */           .deserialize(p, ctxt));
/* 1586 */       if (this._injectables != null) {
/* 1587 */         injectValues(ctxt, bean);
/*      */       }
/* 1589 */       return bean;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1597 */     Object value = p.getEmbeddedObject();
/* 1598 */     if (value != null && 
/* 1599 */       !this._beanType.isTypeOrSuperTypeOf(value.getClass()))
/*      */     {
/* 1601 */       value = ctxt.handleWeirdNativeValue(this._beanType, value, p);
/*      */     }
/*      */     
/* 1604 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonDeserializer<Object> _delegateDeserializer() {
/* 1611 */     JsonDeserializer<Object> deser = this._delegateDeserializer;
/* 1612 */     if (deser == null) {
/* 1613 */       deser = this._arrayDelegateDeserializer;
/*      */     }
/* 1615 */     return deser;
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
/*      */   protected void injectValues(DeserializationContext ctxt, Object bean) throws IOException {
/* 1627 */     for (ValueInjector injector : this._injectables) {
/* 1628 */       injector.inject(ctxt, bean);
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
/*      */   protected Object handleUnknownProperties(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
/* 1642 */     unknownTokens.writeEndObject();
/*      */ 
/*      */     
/* 1645 */     JsonParser bufferParser = unknownTokens.asParser();
/* 1646 */     while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
/* 1647 */       String propName = bufferParser.currentName();
/*      */       
/* 1649 */       bufferParser.nextToken();
/* 1650 */       handleUnknownProperty(bufferParser, ctxt, bean, propName);
/*      */     } 
/* 1652 */     return bean;
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
/*      */   protected void handleUnknownVanilla(JsonParser p, DeserializationContext ctxt, Object beanOrBuilder, String propName) throws IOException {
/* 1667 */     if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/* 1668 */       handleIgnoredProperty(p, ctxt, beanOrBuilder, propName);
/* 1669 */     } else if (this._anySetter != null) {
/*      */       
/*      */       try {
/* 1672 */         this._anySetter.deserializeAndSet(p, ctxt, beanOrBuilder, propName);
/* 1673 */       } catch (Exception e) {
/* 1674 */         wrapAndThrow(e, beanOrBuilder, propName, ctxt);
/*      */       } 
/*      */     } else {
/*      */       
/* 1678 */       handleUnknownProperty(p, ctxt, beanOrBuilder, propName);
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
/*      */   protected void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException {
/* 1691 */     if (this._ignoreAllUnknown) {
/* 1692 */       p.skipChildren();
/*      */       return;
/*      */     } 
/* 1695 */     if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/* 1696 */       handleIgnoredProperty(p, ctxt, beanOrClass, propName);
/*      */     }
/*      */ 
/*      */     
/* 1700 */     super.handleUnknownProperty(p, ctxt, beanOrClass, propName);
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
/*      */   protected void handleIgnoredProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException {
/* 1713 */     if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)) {
/* 1714 */       throw IgnoredPropertyException.from(p, beanOrClass, propName, getKnownPropertyNames());
/*      */     }
/* 1716 */     p.skipChildren();
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
/*      */   protected Object handlePolymorphic(JsonParser p, DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
/* 1735 */     JsonDeserializer<Object> subDeser = _findSubclassDeserializer(ctxt, bean, unknownTokens);
/* 1736 */     if (subDeser != null) {
/* 1737 */       if (unknownTokens != null) {
/*      */         
/* 1739 */         unknownTokens.writeEndObject();
/* 1740 */         JsonParser p2 = unknownTokens.asParser();
/* 1741 */         p2.nextToken();
/* 1742 */         bean = subDeser.deserialize(p2, ctxt, bean);
/*      */       } 
/*      */       
/* 1745 */       if (p != null) {
/* 1746 */         bean = subDeser.deserialize(p, ctxt, bean);
/*      */       }
/* 1748 */       return bean;
/*      */     } 
/*      */     
/* 1751 */     if (unknownTokens != null) {
/* 1752 */       bean = handleUnknownProperties(ctxt, bean, unknownTokens);
/*      */     }
/*      */     
/* 1755 */     if (p != null) {
/* 1756 */       bean = deserialize(p, ctxt, bean);
/*      */     }
/* 1758 */     return bean;
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
/*      */   protected JsonDeserializer<Object> _findSubclassDeserializer(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
/* 1772 */     synchronized (this) {
/* 1773 */       subDeser = (this._subDeserializers == null) ? null : this._subDeserializers.get(new ClassKey(bean.getClass()));
/*      */     } 
/* 1775 */     if (subDeser != null) {
/* 1776 */       return subDeser;
/*      */     }
/*      */     
/* 1779 */     JavaType type = ctxt.constructType(bean.getClass());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1786 */     JsonDeserializer<Object> subDeser = ctxt.findRootValueDeserializer(type);
/*      */     
/* 1788 */     if (subDeser != null) {
/* 1789 */       synchronized (this) {
/* 1790 */         if (this._subDeserializers == null) {
/* 1791 */           this._subDeserializers = new HashMap<>();
/*      */         }
/* 1793 */         this._subDeserializers.put(new ClassKey(bean.getClass()), subDeser);
/*      */       } 
/*      */     }
/* 1796 */     return subDeser;
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
/*      */   public void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
/* 1821 */     throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, fieldName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException {
/* 1831 */     while (t instanceof java.lang.reflect.InvocationTargetException && t.getCause() != null) {
/* 1832 */       t = t.getCause();
/*      */     }
/*      */     
/* 1835 */     ClassUtil.throwIfError(t);
/* 1836 */     boolean wrap = (ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/*      */     
/* 1838 */     if (t instanceof IOException) {
/* 1839 */       if (!wrap || !(t instanceof com.fasterxml.jackson.core.JacksonException)) {
/* 1840 */         throw (IOException)t;
/*      */       }
/* 1842 */     } else if (!wrap) {
/* 1843 */       ClassUtil.throwIfRTE(t);
/*      */     } 
/* 1845 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object wrapInstantiationProblem(Throwable t, DeserializationContext ctxt) throws IOException {
/* 1851 */     while (t instanceof java.lang.reflect.InvocationTargetException && t.getCause() != null) {
/* 1852 */       t = t.getCause();
/*      */     }
/*      */     
/* 1855 */     ClassUtil.throwIfError(t);
/* 1856 */     if (t instanceof IOException)
/*      */     {
/* 1858 */       throw (IOException)t;
/*      */     }
/* 1860 */     if (ctxt == null) {
/* 1861 */       throw new IllegalArgumentException(t.getMessage(), t);
/*      */     }
/* 1863 */     if (!ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS)) {
/* 1864 */       ClassUtil.throwIfRTE(t);
/*      */     }
/* 1866 */     return ctxt.handleInstantiationProblem(this._beanType.getRawClass(), null, t);
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/BeanDeserializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */