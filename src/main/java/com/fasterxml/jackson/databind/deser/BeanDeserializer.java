/*      */ package com.fasterxml.jackson.databind.deser;
/*      */ 
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.databind.BeanDescription;
/*      */ import com.fasterxml.jackson.databind.BeanProperty;
/*      */ import com.fasterxml.jackson.databind.DeserializationContext;
/*      */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*      */ import com.fasterxml.jackson.databind.JsonMappingException;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*      */ import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayDeserializer;
/*      */ import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
/*      */ import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
/*      */ import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*      */ import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
/*      */ import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*      */ import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*      */ import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
/*      */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*      */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BeanDeserializer
/*      */   extends BeanDeserializerBase
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   protected transient Exception _nullFromCreator;
/*      */   private volatile transient NameTransformer _currentlyTransforming;
/*      */   
/*      */   @Deprecated
/*      */   public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
/*   58 */     super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, (Set<String>)null, hasViews);
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
/*      */   public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, Set<String> includableProps, boolean hasViews) {
/*   72 */     super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, includableProps, hasViews);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeanDeserializer(BeanDeserializerBase src) {
/*   81 */     super(src, src._ignoreAllUnknown);
/*      */   }
/*      */   
/*      */   protected BeanDeserializer(BeanDeserializerBase src, boolean ignoreAllUnknown) {
/*   85 */     super(src, ignoreAllUnknown);
/*      */   }
/*      */   
/*      */   protected BeanDeserializer(BeanDeserializerBase src, NameTransformer unwrapper) {
/*   89 */     super(src, unwrapper);
/*      */   }
/*      */   
/*      */   public BeanDeserializer(BeanDeserializerBase src, ObjectIdReader oir) {
/*   93 */     super(src, oir);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public BeanDeserializer(BeanDeserializerBase src, Set<String> ignorableProps) {
/*  101 */     super(src, ignorableProps);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BeanDeserializer(BeanDeserializerBase src, Set<String> ignorableProps, Set<String> includableProps) {
/*  108 */     super(src, ignorableProps, includableProps);
/*      */   }
/*      */   
/*      */   public BeanDeserializer(BeanDeserializerBase src, BeanPropertyMap props) {
/*  112 */     super(src, props);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer transformer) {
/*  120 */     if (getClass() != BeanDeserializer.class) {
/*  121 */       return (JsonDeserializer<Object>)this;
/*      */     }
/*      */ 
/*      */     
/*  125 */     if (this._currentlyTransforming == transformer) {
/*  126 */       return (JsonDeserializer<Object>)this;
/*      */     }
/*  128 */     this._currentlyTransforming = transformer;
/*      */     
/*  130 */     try { return (JsonDeserializer<Object>)new BeanDeserializer(this, transformer); }
/*  131 */     finally { this._currentlyTransforming = null; }
/*      */   
/*      */   }
/*      */   
/*      */   public BeanDeserializer withObjectIdReader(ObjectIdReader oir) {
/*  136 */     return new BeanDeserializer(this, oir);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public BeanDeserializer withByNameInclusion(Set<String> ignorableProps, Set<String> includableProps) {
/*  142 */     return new BeanDeserializer(this, ignorableProps, includableProps);
/*      */   }
/*      */ 
/*      */   
/*      */   public BeanDeserializerBase withIgnoreAllUnknown(boolean ignoreUnknown) {
/*  147 */     return new BeanDeserializer(this, ignoreUnknown);
/*      */   }
/*      */ 
/*      */   
/*      */   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
/*  152 */     return new BeanDeserializer(this, props);
/*      */   }
/*      */ 
/*      */   
/*      */   protected BeanDeserializerBase asArrayDeserializer() {
/*  157 */     SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
/*  158 */     return (BeanDeserializerBase)new BeanAsArrayDeserializer(this, props);
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
/*      */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  174 */     if (p.isExpectedStartObjectToken()) {
/*  175 */       if (this._vanillaProcessing) {
/*  176 */         return vanillaDeserialize(p, ctxt, p.nextToken());
/*      */       }
/*      */ 
/*      */       
/*  180 */       p.nextToken();
/*  181 */       if (this._objectIdReader != null) {
/*  182 */         return deserializeWithObjectId(p, ctxt);
/*      */       }
/*  184 */       return deserializeFromObject(p, ctxt);
/*      */     } 
/*  186 */     return _deserializeOther(p, ctxt, p.currentToken());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Object _deserializeOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/*  193 */     if (t != null) {
/*  194 */       switch (t) {
/*      */         case AsEmpty:
/*  196 */           return deserializeFromString(p, ctxt);
/*      */         case AsNull:
/*  198 */           return deserializeFromNumber(p, ctxt);
/*      */         case TryConvert:
/*  200 */           return deserializeFromDouble(p, ctxt);
/*      */         case null:
/*  202 */           return deserializeFromEmbedded(p, ctxt);
/*      */         case null:
/*      */         case null:
/*  205 */           return deserializeFromBoolean(p, ctxt);
/*      */         case null:
/*  207 */           return deserializeFromNull(p, ctxt);
/*      */         
/*      */         case null:
/*  210 */           return _deserializeFromArray(p, ctxt);
/*      */         case null:
/*      */         case null:
/*  213 */           if (this._vanillaProcessing) {
/*  214 */             return vanillaDeserialize(p, ctxt, t);
/*      */           }
/*  216 */           if (this._objectIdReader != null) {
/*  217 */             return deserializeWithObjectId(p, ctxt);
/*      */           }
/*  219 */           return deserializeFromObject(p, ctxt);
/*      */       } 
/*      */     
/*      */     }
/*  223 */     return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   protected Object _missingToken(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  228 */     throw ctxt.endOfInputException(handledType());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/*      */     String propName;
/*  240 */     p.setCurrentValue(bean);
/*  241 */     if (this._injectables != null) {
/*  242 */       injectValues(ctxt, bean);
/*      */     }
/*  244 */     if (this._unwrappedPropertyHandler != null) {
/*  245 */       return deserializeWithUnwrapped(p, ctxt, bean);
/*      */     }
/*  247 */     if (this._externalTypeIdHandler != null) {
/*  248 */       return deserializeWithExternalTypeId(p, ctxt, bean);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  253 */     if (p.isExpectedStartObjectToken()) {
/*  254 */       propName = p.nextFieldName();
/*  255 */       if (propName == null) {
/*  256 */         return bean;
/*      */       }
/*      */     }
/*  259 */     else if (p.hasTokenId(5)) {
/*  260 */       propName = p.currentName();
/*      */     } else {
/*  262 */       return bean;
/*      */     } 
/*      */     
/*  265 */     if (this._needViewProcesing) {
/*  266 */       Class<?> view = ctxt.getActiveView();
/*  267 */       if (view != null) {
/*  268 */         return deserializeWithView(p, ctxt, bean, view);
/*      */       }
/*      */     } 
/*      */     while (true) {
/*  272 */       p.nextToken();
/*  273 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/*      */       
/*  275 */       if (prop != null) {
/*      */         try {
/*  277 */           prop.deserializeAndSet(p, ctxt, bean);
/*  278 */         } catch (Exception e) {
/*  279 */           wrapAndThrow(e, bean, propName, ctxt);
/*      */         } 
/*      */       } else {
/*      */         
/*  283 */         handleUnknownVanilla(p, ctxt, bean, propName);
/*  284 */       }  if ((propName = p.nextFieldName()) == null) {
/*  285 */         return bean;
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
/*      */   private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/*  302 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/*      */     
/*  304 */     p.setCurrentValue(bean);
/*  305 */     if (p.hasTokenId(5)) {
/*  306 */       String propName = p.currentName();
/*      */       do {
/*  308 */         p.nextToken();
/*  309 */         SettableBeanProperty prop = this._beanProperties.find(propName);
/*      */         
/*  311 */         if (prop != null)
/*      */         { try {
/*  313 */             prop.deserializeAndSet(p, ctxt, bean);
/*  314 */           } catch (Exception e) {
/*  315 */             wrapAndThrow(e, bean, propName, ctxt);
/*      */           }  }
/*      */         else
/*      */         
/*  319 */         { handleUnknownVanilla(p, ctxt, bean, propName); } 
/*  320 */       } while ((propName = p.nextFieldName()) != null);
/*      */     } 
/*  322 */     return bean;
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
/*      */   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  338 */     if (this._objectIdReader != null && this._objectIdReader.maySerializeAsObject() && 
/*  339 */       p.hasTokenId(5) && this._objectIdReader
/*  340 */       .isValidReferencePropertyName(p.currentName(), p)) {
/*  341 */       return deserializeFromObjectId(p, ctxt);
/*      */     }
/*      */     
/*  344 */     if (this._nonStandardCreation) {
/*  345 */       if (this._unwrappedPropertyHandler != null) {
/*  346 */         return deserializeWithUnwrapped(p, ctxt);
/*      */       }
/*  348 */       if (this._externalTypeIdHandler != null) {
/*  349 */         return deserializeWithExternalTypeId(p, ctxt);
/*      */       }
/*  351 */       Object object = deserializeFromObjectUsingNonDefault(p, ctxt);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  364 */       return object;
/*      */     } 
/*  366 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/*      */     
/*  368 */     p.setCurrentValue(bean);
/*  369 */     if (p.canReadObjectId()) {
/*  370 */       Object id = p.getObjectId();
/*  371 */       if (id != null) {
/*  372 */         _handleTypedObjectId(p, ctxt, bean, id);
/*      */       }
/*      */     } 
/*  375 */     if (this._injectables != null) {
/*  376 */       injectValues(ctxt, bean);
/*      */     }
/*  378 */     if (this._needViewProcesing) {
/*  379 */       Class<?> view = ctxt.getActiveView();
/*  380 */       if (view != null) {
/*  381 */         return deserializeWithView(p, ctxt, bean, view);
/*      */       }
/*      */     } 
/*  384 */     if (p.hasTokenId(5)) {
/*  385 */       String propName = p.currentName();
/*      */       do {
/*  387 */         p.nextToken();
/*  388 */         SettableBeanProperty prop = this._beanProperties.find(propName);
/*  389 */         if (prop != null)
/*      */         { try {
/*  391 */             prop.deserializeAndSet(p, ctxt, bean);
/*  392 */           } catch (Exception e) {
/*  393 */             wrapAndThrow(e, bean, propName, ctxt);
/*      */           }  }
/*      */         else
/*      */         
/*  397 */         { handleUnknownVanilla(p, ctxt, bean, propName); } 
/*  398 */       } while ((propName = p.nextFieldName()) != null);
/*      */     } 
/*  400 */     return bean;
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
/*      */   protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
/*      */     Object bean;
/*  415 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/*  416 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/*  417 */     TokenBuffer unknown = null;
/*  418 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/*      */     
/*  420 */     JsonToken t = p.currentToken();
/*  421 */     List<BeanReferring> referrings = null;
/*  422 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/*  423 */       String propName = p.currentName();
/*  424 */       p.nextToken();
/*  425 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/*      */       
/*  427 */       if (!buffer.readIdProperty(propName) || creatorProp != null)
/*      */       {
/*      */ 
/*      */         
/*  431 */         if (creatorProp != null) {
/*      */ 
/*      */           
/*  434 */           if (activeView != null && !creatorProp.visibleInView(activeView)) {
/*  435 */             p.skipChildren();
/*      */           } else {
/*      */             
/*  438 */             Object value = _deserializeWithErrorWrapping(p, ctxt, creatorProp);
/*  439 */             if (buffer.assignParameter(creatorProp, value)) {
/*  440 */               Object object; p.nextToken();
/*      */               
/*      */               try {
/*  443 */                 object = creator.build(ctxt, buffer);
/*  444 */               } catch (Exception e) {
/*  445 */                 object = wrapInstantiationProblem(e, ctxt);
/*      */               } 
/*  447 */               if (object == null) {
/*  448 */                 return ctxt.handleInstantiationProblem(handledType(), null, 
/*  449 */                     _creatorReturnedNullException());
/*      */               }
/*      */               
/*  452 */               p.setCurrentValue(object);
/*      */ 
/*      */               
/*  455 */               if (object.getClass() != this._beanType.getRawClass()) {
/*  456 */                 return handlePolymorphic(p, ctxt, object, unknown);
/*      */               }
/*  458 */               if (unknown != null) {
/*  459 */                 object = handleUnknownProperties(ctxt, object, unknown);
/*      */               }
/*      */               
/*  462 */               return deserialize(p, ctxt, object);
/*      */             } 
/*      */           } 
/*      */         } else {
/*      */           
/*  467 */           SettableBeanProperty prop = this._beanProperties.find(propName);
/*  468 */           if (prop != null) {
/*      */             try {
/*  470 */               buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/*  471 */             } catch (UnresolvedForwardReference reference) {
/*      */ 
/*      */ 
/*      */               
/*  475 */               BeanReferring referring = handleUnresolvedReference(ctxt, prop, buffer, reference);
/*      */               
/*  477 */               if (referrings == null) {
/*  478 */                 referrings = new ArrayList<>();
/*      */               }
/*  480 */               referrings.add(referring);
/*      */             
/*      */             }
/*      */           
/*      */           }
/*  485 */           else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/*  486 */             handleIgnoredProperty(p, ctxt, handledType(), propName);
/*      */ 
/*      */           
/*      */           }
/*  490 */           else if (this._anySetter != null) {
/*      */             try {
/*  492 */               buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/*  493 */             } catch (Exception e) {
/*  494 */               wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*      */ 
/*      */             
/*      */             }
/*      */ 
/*      */ 
/*      */           
/*      */           }
/*  502 */           else if (this._ignoreAllUnknown) {
/*      */             
/*  504 */             p.skipChildren();
/*      */           }
/*      */           else {
/*      */             
/*  508 */             if (unknown == null) {
/*  509 */               unknown = ctxt.bufferForInputBuffering(p);
/*      */             }
/*  511 */             unknown.writeFieldName(propName);
/*  512 */             unknown.copyCurrentStructure(p);
/*      */           } 
/*      */         } 
/*      */       }
/*      */     } 
/*      */     try {
/*  518 */       bean = creator.build(ctxt, buffer);
/*  519 */     } catch (Exception e) {
/*  520 */       wrapInstantiationProblem(e, ctxt);
/*  521 */       bean = null;
/*      */     } 
/*      */     
/*  524 */     if (this._injectables != null) {
/*  525 */       injectValues(ctxt, bean);
/*      */     }
/*      */     
/*  528 */     if (referrings != null) {
/*  529 */       for (BeanReferring referring : referrings) {
/*  530 */         referring.setBean(bean);
/*      */       }
/*      */     }
/*  533 */     if (unknown != null) {
/*      */       
/*  535 */       if (bean.getClass() != this._beanType.getRawClass()) {
/*  536 */         return handlePolymorphic((JsonParser)null, ctxt, bean, unknown);
/*      */       }
/*      */       
/*  539 */       return handleUnknownProperties(ctxt, bean, unknown);
/*      */     } 
/*  541 */     return bean;
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
/*      */   private BeanReferring handleUnresolvedReference(DeserializationContext ctxt, SettableBeanProperty prop, PropertyValueBuffer buffer, UnresolvedForwardReference reference) throws JsonMappingException {
/*  553 */     BeanReferring referring = new BeanReferring(ctxt, reference, prop.getType(), buffer, prop);
/*  554 */     reference.getRoid().appendReferring(referring);
/*  555 */     return referring;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
/*      */     try {
/*  563 */       return prop.deserialize(p, ctxt);
/*  564 */     } catch (Exception e) {
/*  565 */       wrapAndThrow(e, this._beanType.getRawClass(), prop.getName(), ctxt);
/*      */       
/*  567 */       return null;
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
/*      */   protected Object deserializeFromNull(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  585 */     if (p.requiresCustomCodec()) {
/*      */       
/*  587 */       TokenBuffer tb = ctxt.bufferForInputBuffering(p);
/*  588 */       tb.writeEndObject();
/*  589 */       JsonParser p2 = tb.asParser(p);
/*  590 */       p2.nextToken();
/*      */ 
/*      */       
/*  593 */       Object ob = this._vanillaProcessing ? vanillaDeserialize(p2, ctxt, JsonToken.END_OBJECT) : deserializeFromObject(p2, ctxt);
/*  594 */       p2.close();
/*  595 */       return ob;
/*      */     } 
/*  597 */     return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object _deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  604 */     JsonDeserializer<Object> delegateDeser = this._arrayDelegateDeserializer;
/*      */     
/*  606 */     if (delegateDeser != null || (delegateDeser = this._delegateDeserializer) != null) {
/*  607 */       Object bean = this._valueInstantiator.createUsingArrayDelegate(ctxt, delegateDeser
/*  608 */           .deserialize(p, ctxt));
/*  609 */       if (this._injectables != null) {
/*  610 */         injectValues(ctxt, bean);
/*      */       }
/*  612 */       return bean;
/*      */     } 
/*  614 */     CoercionAction act = _findCoercionFromEmptyArray(ctxt);
/*  615 */     boolean unwrap = ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
/*      */     
/*  617 */     if (unwrap || act != CoercionAction.Fail) {
/*  618 */       JsonToken t = p.nextToken();
/*  619 */       if (t == JsonToken.END_ARRAY) {
/*  620 */         switch (act) {
/*      */           case AsEmpty:
/*  622 */             return getEmptyValue(ctxt);
/*      */           case AsNull:
/*      */           case TryConvert:
/*  625 */             return getNullValue(ctxt);
/*      */         } 
/*      */         
/*  628 */         return ctxt.handleUnexpectedToken(getValueType(ctxt), JsonToken.START_ARRAY, p, null, new Object[0]);
/*      */       } 
/*  630 */       if (unwrap) {
/*  631 */         Object value = deserialize(p, ctxt);
/*  632 */         if (p.nextToken() != JsonToken.END_ARRAY) {
/*  633 */           handleMissingEndArrayForSingle(p, ctxt);
/*      */         }
/*  635 */         return value;
/*      */       } 
/*      */     } 
/*  638 */     return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
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
/*      */   protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException {
/*  651 */     if (p.hasTokenId(5)) {
/*  652 */       String propName = p.currentName();
/*      */       do {
/*  654 */         p.nextToken();
/*      */         
/*  656 */         SettableBeanProperty prop = this._beanProperties.find(propName);
/*  657 */         if (prop != null)
/*  658 */         { if (!prop.visibleInView(activeView)) {
/*  659 */             p.skipChildren();
/*      */           } else {
/*      */             
/*      */             try {
/*  663 */               prop.deserializeAndSet(p, ctxt, bean);
/*  664 */             } catch (Exception e) {
/*  665 */               wrapAndThrow(e, bean, propName, ctxt);
/*      */             } 
/*      */           }  }
/*      */         else
/*  669 */         { handleUnknownVanilla(p, ctxt, bean, propName); } 
/*  670 */       } while ((propName = p.nextFieldName()) != null);
/*      */     } 
/*  672 */     return bean;
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
/*      */   protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  689 */     if (this._delegateDeserializer != null) {
/*  690 */       return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*      */     }
/*  692 */     if (this._propertyBasedCreator != null) {
/*  693 */       return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
/*      */     }
/*  695 */     TokenBuffer tokens = ctxt.bufferForInputBuffering(p);
/*  696 */     tokens.writeStartObject();
/*  697 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/*      */ 
/*      */     
/*  700 */     p.setCurrentValue(bean);
/*      */     
/*  702 */     if (this._injectables != null) {
/*  703 */       injectValues(ctxt, bean);
/*      */     }
/*  705 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/*  706 */     String propName = p.hasTokenId(5) ? p.currentName() : null;
/*      */     
/*  708 */     for (; propName != null; propName = p.nextFieldName()) {
/*  709 */       p.nextToken();
/*  710 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/*  711 */       if (prop != null) {
/*  712 */         if (activeView != null && !prop.visibleInView(activeView)) {
/*  713 */           p.skipChildren();
/*      */         } else {
/*      */           
/*      */           try {
/*  717 */             prop.deserializeAndSet(p, ctxt, bean);
/*  718 */           } catch (Exception e) {
/*  719 */             wrapAndThrow(e, bean, propName, ctxt);
/*      */           }
/*      */         
/*      */         }
/*      */       
/*  724 */       } else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/*  725 */         handleIgnoredProperty(p, ctxt, bean, propName);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  732 */       else if (this._anySetter == null) {
/*      */         
/*  734 */         tokens.writeFieldName(propName);
/*  735 */         tokens.copyCurrentStructure(p);
/*      */       }
/*      */       else {
/*      */         
/*  739 */         TokenBuffer b2 = ctxt.bufferAsCopyOfValue(p);
/*  740 */         tokens.writeFieldName(propName);
/*  741 */         tokens.append(b2);
/*      */         try {
/*  743 */           this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
/*  744 */         } catch (Exception e) {
/*  745 */           wrapAndThrow(e, bean, propName, ctxt);
/*      */         } 
/*      */       } 
/*  748 */     }  tokens.writeEndObject();
/*  749 */     this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/*  750 */     return bean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/*  758 */     JsonToken t = p.currentToken();
/*  759 */     if (t == JsonToken.START_OBJECT) {
/*  760 */       t = p.nextToken();
/*      */     }
/*  762 */     TokenBuffer tokens = ctxt.bufferForInputBuffering(p);
/*  763 */     tokens.writeStartObject();
/*  764 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/*  765 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/*  766 */       String propName = p.currentName();
/*  767 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/*  768 */       p.nextToken();
/*  769 */       if (prop != null) {
/*  770 */         if (activeView != null && !prop.visibleInView(activeView)) {
/*  771 */           p.skipChildren();
/*      */         } else {
/*      */           
/*      */           try {
/*  775 */             prop.deserializeAndSet(p, ctxt, bean);
/*  776 */           } catch (Exception e) {
/*  777 */             wrapAndThrow(e, bean, propName, ctxt);
/*      */           }
/*      */         
/*      */         } 
/*  781 */       } else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/*  782 */         handleIgnoredProperty(p, ctxt, bean, propName);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  789 */       else if (this._anySetter == null) {
/*      */         
/*  791 */         tokens.writeFieldName(propName);
/*  792 */         tokens.copyCurrentStructure(p);
/*      */       } else {
/*      */         
/*  795 */         TokenBuffer b2 = ctxt.bufferAsCopyOfValue(p);
/*  796 */         tokens.writeFieldName(propName);
/*  797 */         tokens.append(b2);
/*      */         try {
/*  799 */           this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
/*  800 */         } catch (Exception e) {
/*  801 */           wrapAndThrow(e, bean, propName, ctxt);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  806 */     tokens.writeEndObject();
/*  807 */     this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/*  808 */     return bean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/*      */     Object bean;
/*  819 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/*  820 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/*      */     
/*  822 */     TokenBuffer tokens = ctxt.bufferForInputBuffering(p);
/*  823 */     tokens.writeStartObject();
/*      */     
/*  825 */     JsonToken t = p.currentToken();
/*  826 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/*  827 */       String propName = p.currentName();
/*  828 */       p.nextToken();
/*      */       
/*  830 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/*      */       
/*  832 */       if (!buffer.readIdProperty(propName) || creatorProp != null)
/*      */       {
/*      */         
/*  835 */         if (creatorProp != null) {
/*      */           
/*  837 */           if (buffer.assignParameter(creatorProp, 
/*  838 */               _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/*  839 */             Object object; t = p.nextToken();
/*      */             
/*      */             try {
/*  842 */               object = creator.build(ctxt, buffer);
/*  843 */             } catch (Exception e) {
/*  844 */               object = wrapInstantiationProblem(e, ctxt);
/*      */             } 
/*      */             
/*  847 */             p.setCurrentValue(object);
/*      */             
/*  849 */             while (t == JsonToken.FIELD_NAME) {
/*      */               
/*  851 */               tokens.copyCurrentStructure(p);
/*  852 */               t = p.nextToken();
/*      */             } 
/*      */ 
/*      */             
/*  856 */             if (t != JsonToken.END_OBJECT)
/*  857 */               ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.END_OBJECT, "Attempted to unwrap '%s' value", new Object[] {
/*      */                     
/*  859 */                     handledType().getName()
/*      */                   }); 
/*  861 */             tokens.writeEndObject();
/*  862 */             if (object.getClass() != this._beanType.getRawClass()) {
/*      */ 
/*      */               
/*  865 */               ctxt.reportInputMismatch((BeanProperty)creatorProp, "Cannot create polymorphic instances with unwrapped values", new Object[0]);
/*      */               
/*  867 */               return null;
/*      */             } 
/*  869 */             return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, object, tokens);
/*      */           }
/*      */         
/*      */         } else {
/*      */           
/*  874 */           SettableBeanProperty prop = this._beanProperties.find(propName);
/*  875 */           if (prop != null) {
/*  876 */             buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/*      */ 
/*      */           
/*      */           }
/*  880 */           else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/*  881 */             handleIgnoredProperty(p, ctxt, handledType(), propName);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           }
/*  888 */           else if (this._anySetter == null) {
/*      */             
/*  890 */             tokens.writeFieldName(propName);
/*  891 */             tokens.copyCurrentStructure(p);
/*      */           } else {
/*      */             
/*  894 */             TokenBuffer b2 = ctxt.bufferAsCopyOfValue(p);
/*  895 */             tokens.writeFieldName(propName);
/*  896 */             tokens.append(b2);
/*      */             try {
/*  898 */               buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter
/*  899 */                   .deserialize(b2.asParserOnFirstToken(), ctxt));
/*  900 */             } catch (Exception e) {
/*  901 */               wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*  910 */       bean = creator.build(ctxt, buffer);
/*  911 */     } catch (Exception e) {
/*  912 */       wrapInstantiationProblem(e, ctxt);
/*  913 */       return null;
/*      */     } 
/*  915 */     return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
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
/*      */   protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  928 */     if (this._propertyBasedCreator != null) {
/*  929 */       return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
/*      */     }
/*  931 */     if (this._delegateDeserializer != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  937 */       return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer
/*  938 */           .deserialize(p, ctxt));
/*      */     }
/*      */     
/*  941 */     return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/*  948 */     return _deserializeWithExternalTypeId(p, ctxt, bean, this._externalTypeIdHandler
/*  949 */         .start());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object _deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean, ExternalTypeHandler ext) throws IOException {
/*  956 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/*  957 */     for (JsonToken t = p.currentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/*  958 */       String propName = p.currentName();
/*  959 */       t = p.nextToken();
/*  960 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/*  961 */       if (prop != null) {
/*      */         
/*  963 */         if (t.isScalarValue()) {
/*  964 */           ext.handleTypePropertyValue(p, ctxt, propName, bean);
/*      */         }
/*  966 */         if (activeView != null && !prop.visibleInView(activeView)) {
/*  967 */           p.skipChildren();
/*      */         } else {
/*      */           
/*      */           try {
/*  971 */             prop.deserializeAndSet(p, ctxt, bean);
/*  972 */           } catch (Exception e) {
/*  973 */             wrapAndThrow(e, bean, propName, ctxt);
/*      */           }
/*      */         
/*      */         }
/*      */       
/*  978 */       } else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/*  979 */         handleIgnoredProperty(p, ctxt, bean, propName);
/*      */ 
/*      */       
/*      */       }
/*  983 */       else if (!ext.handlePropertyValue(p, ctxt, propName, bean)) {
/*      */ 
/*      */ 
/*      */         
/*  987 */         if (this._anySetter != null) {
/*      */           try {
/*  989 */             this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/*  990 */           } catch (Exception e) {
/*  991 */             wrapAndThrow(e, bean, propName, ctxt);
/*      */           }
/*      */         
/*      */         } else {
/*      */           
/*  996 */           handleUnknownProperty(p, ctxt, bean, propName);
/*      */         } 
/*      */       } 
/*  999 */     }  return ext.complete(p, ctxt, bean);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1007 */     ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/* 1008 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/* 1009 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 1010 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/*      */     
/* 1012 */     JsonToken t = p.currentToken();
/* 1013 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 1014 */       String propName = p.currentName();
/* 1015 */       t = p.nextToken();
/*      */       
/* 1017 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/*      */       
/* 1019 */       if (!buffer.readIdProperty(propName) || creatorProp != null)
/*      */       {
/*      */         
/* 1022 */         if (creatorProp != null) {
/*      */ 
/*      */ 
/*      */           
/* 1026 */           if (!ext.handlePropertyValue(p, ctxt, propName, null))
/*      */           {
/*      */ 
/*      */             
/* 1030 */             if (buffer.assignParameter(creatorProp, 
/* 1031 */                 _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/* 1032 */               Object bean; t = p.nextToken();
/*      */               
/*      */               try {
/* 1035 */                 bean = creator.build(ctxt, buffer);
/* 1036 */               } catch (Exception e) {
/* 1037 */                 wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*      */               } 
/*      */               
/* 1040 */               if (bean.getClass() != this._beanType.getRawClass())
/*      */               {
/*      */                 
/* 1043 */                 return ctxt.reportBadDefinition(this._beanType, String.format("Cannot create polymorphic instances with external type ids (%s -> %s)", new Object[] { this._beanType, bean
/*      */                         
/* 1045 */                         .getClass() }));
/*      */               }
/* 1047 */               return _deserializeWithExternalTypeId(p, ctxt, bean, ext);
/*      */             }
/*      */           
/*      */           }
/*      */         } else {
/*      */           
/* 1053 */           SettableBeanProperty prop = this._beanProperties.find(propName);
/* 1054 */           if (prop != null) {
/*      */             
/* 1056 */             if (t.isScalarValue()) {
/* 1057 */               ext.handleTypePropertyValue(p, ctxt, propName, null);
/*      */             }
/*      */             
/* 1060 */             if (activeView != null && !prop.visibleInView(activeView)) {
/* 1061 */               p.skipChildren();
/*      */             } else {
/* 1063 */               buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/*      */             
/*      */             }
/*      */           
/*      */           }
/* 1068 */           else if (!ext.handlePropertyValue(p, ctxt, propName, null)) {
/*      */ 
/*      */ 
/*      */             
/* 1072 */             if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
/* 1073 */               handleIgnoredProperty(p, ctxt, handledType(), propName);
/*      */ 
/*      */             
/*      */             }
/* 1077 */             else if (this._anySetter != null) {
/* 1078 */               buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter
/* 1079 */                   .deserialize(p, ctxt));
/*      */             }
/*      */             else {
/*      */               
/* 1083 */               handleUnknownProperty(p, ctxt, this._valueClass, propName);
/*      */             } 
/*      */           } 
/*      */         }  } 
/*      */     }  try {
/* 1088 */       return ext.complete(p, ctxt, buffer, creator);
/* 1089 */     } catch (Exception e) {
/* 1090 */       return wrapInstantiationProblem(e, ctxt);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Exception _creatorReturnedNullException() {
/* 1101 */     if (this._nullFromCreator == null) {
/* 1102 */       this._nullFromCreator = new NullPointerException("JSON Creator returned null");
/*      */     }
/* 1104 */     return this._nullFromCreator;
/*      */   }
/*      */ 
/*      */   
/*      */   static class BeanReferring
/*      */     extends ReadableObjectId.Referring
/*      */   {
/*      */     private final DeserializationContext _context;
/*      */     
/*      */     private final SettableBeanProperty _prop;
/*      */     
/*      */     private Object _bean;
/*      */ 
/*      */     
/*      */     BeanReferring(DeserializationContext ctxt, UnresolvedForwardReference ref, JavaType valueType, PropertyValueBuffer buffer, SettableBeanProperty prop) {
/* 1119 */       super(ref, valueType);
/* 1120 */       this._context = ctxt;
/* 1121 */       this._prop = prop;
/*      */     }
/*      */     
/*      */     public void setBean(Object bean) {
/* 1125 */       this._bean = bean;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void handleResolvedForwardReference(Object id, Object value) throws IOException {
/* 1131 */       if (this._bean == null) {
/* 1132 */         this._context.reportInputMismatch((BeanProperty)this._prop, "Cannot resolve ObjectId forward reference using property '%s' (of type %s): Bean not yet resolved", new Object[] { this._prop
/*      */               
/* 1134 */               .getName(), this._prop.getDeclaringClass().getName() });
/*      */       }
/* 1136 */       this._prop.set(this._bean, value);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/BeanDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */