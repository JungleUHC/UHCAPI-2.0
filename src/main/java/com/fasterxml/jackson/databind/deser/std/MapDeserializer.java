/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonIncludeProperties;
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*     */ import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*     */ import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class MapDeserializer
/*     */   extends ContainerDeserializerBase<Map<Object, Object>>
/*     */   implements ContextualDeserializer, ResolvableDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final KeyDeserializer _keyDeserializer;
/*     */   protected boolean _standardStringKey;
/*     */   protected final JsonDeserializer<Object> _valueDeserializer;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected JsonDeserializer<Object> _delegateDeserializer;
/*     */   protected PropertyBasedCreator _propertyBasedCreator;
/*     */   protected final boolean _hasDefaultCreator;
/*     */   protected Set<String> _ignorableProperties;
/*     */   protected Set<String> _includableProperties;
/*     */   protected IgnorePropertiesUtil.Checker _inclusionChecker;
/*     */   
/*     */   public MapDeserializer(JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
/* 114 */     super(mapType, (NullValueProvider)null, (Boolean)null);
/* 115 */     this._keyDeserializer = keyDeser;
/* 116 */     this._valueDeserializer = valueDeser;
/* 117 */     this._valueTypeDeserializer = valueTypeDeser;
/* 118 */     this._valueInstantiator = valueInstantiator;
/* 119 */     this._hasDefaultCreator = valueInstantiator.canCreateUsingDefault();
/* 120 */     this._delegateDeserializer = null;
/* 121 */     this._propertyBasedCreator = null;
/* 122 */     this._standardStringKey = _isStdKeyDeser(mapType, keyDeser);
/* 123 */     this._inclusionChecker = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MapDeserializer(MapDeserializer src) {
/* 132 */     super(src);
/* 133 */     this._keyDeserializer = src._keyDeserializer;
/* 134 */     this._valueDeserializer = src._valueDeserializer;
/* 135 */     this._valueTypeDeserializer = src._valueTypeDeserializer;
/* 136 */     this._valueInstantiator = src._valueInstantiator;
/* 137 */     this._propertyBasedCreator = src._propertyBasedCreator;
/* 138 */     this._delegateDeserializer = src._delegateDeserializer;
/* 139 */     this._hasDefaultCreator = src._hasDefaultCreator;
/*     */     
/* 141 */     this._ignorableProperties = src._ignorableProperties;
/* 142 */     this._includableProperties = src._includableProperties;
/* 143 */     this._inclusionChecker = src._inclusionChecker;
/*     */     
/* 145 */     this._standardStringKey = src._standardStringKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected MapDeserializer(MapDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, NullValueProvider nuller, Set<String> ignorable) {
/* 154 */     this(src, keyDeser, valueDeser, valueTypeDeser, nuller, ignorable, (Set<String>)null);
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
/*     */   protected MapDeserializer(MapDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, NullValueProvider nuller, Set<String> ignorable, Set<String> includable) {
/* 167 */     super(src, nuller, src._unwrapSingle);
/* 168 */     this._keyDeserializer = keyDeser;
/* 169 */     this._valueDeserializer = valueDeser;
/* 170 */     this._valueTypeDeserializer = valueTypeDeser;
/* 171 */     this._valueInstantiator = src._valueInstantiator;
/* 172 */     this._propertyBasedCreator = src._propertyBasedCreator;
/* 173 */     this._delegateDeserializer = src._delegateDeserializer;
/* 174 */     this._hasDefaultCreator = src._hasDefaultCreator;
/* 175 */     this._ignorableProperties = ignorable;
/* 176 */     this._includableProperties = includable;
/* 177 */     this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(ignorable, includable);
/*     */     
/* 179 */     this._standardStringKey = _isStdKeyDeser(this._containerType, keyDeser);
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
/*     */   protected MapDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Set<String> ignorable) {
/* 191 */     return withResolved(keyDeser, valueTypeDeser, valueDeser, nuller, ignorable, this._includableProperties);
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
/*     */   protected MapDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Set<String> ignorable, Set<String> includable) {
/* 203 */     if (this._keyDeserializer == keyDeser && this._valueDeserializer == valueDeser && this._valueTypeDeserializer == valueTypeDeser && this._nullProvider == nuller && this._ignorableProperties == ignorable && this._includableProperties == includable)
/*     */     {
/*     */       
/* 206 */       return this;
/*     */     }
/* 208 */     return new MapDeserializer(this, keyDeser, (JsonDeserializer)valueDeser, valueTypeDeser, nuller, ignorable, includable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _isStdKeyDeser(JavaType mapType, KeyDeserializer keyDeser) {
/* 219 */     if (keyDeser == null) {
/* 220 */       return true;
/*     */     }
/* 222 */     JavaType keyType = mapType.getKeyType();
/* 223 */     if (keyType == null) {
/* 224 */       return true;
/*     */     }
/* 226 */     Class<?> rawKeyType = keyType.getRawClass();
/* 227 */     return ((rawKeyType == String.class || rawKeyType == Object.class) && 
/* 228 */       isDefaultKeyDeserializer(keyDeser));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setIgnorableProperties(String[] ignorable) {
/* 236 */     this
/* 237 */       ._ignorableProperties = (ignorable == null || ignorable.length == 0) ? null : ArrayBuilders.arrayToSet((Object[])ignorable);
/* 238 */     this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
/*     */   }
/*     */   
/*     */   public void setIgnorableProperties(Set<String> ignorable) {
/* 242 */     this._ignorableProperties = (ignorable == null || ignorable.size() == 0) ? null : ignorable;
/*     */     
/* 244 */     this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
/*     */   }
/*     */   
/*     */   public void setIncludableProperties(Set<String> includable) {
/* 248 */     this._includableProperties = includable;
/* 249 */     this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
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
/*     */   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
/* 262 */     if (this._valueInstantiator.canCreateUsingDelegate()) {
/* 263 */       JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
/* 264 */       if (delegateType == null) {
/* 265 */         ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", new Object[] { this._containerType, this._valueInstantiator
/*     */ 
/*     */                 
/* 268 */                 .getClass().getName() }));
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 273 */       this._delegateDeserializer = findDeserializer(ctxt, delegateType, null);
/* 274 */     } else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
/* 275 */       JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
/* 276 */       if (delegateType == null) {
/* 277 */         ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", new Object[] { this._containerType, this._valueInstantiator
/*     */ 
/*     */                 
/* 280 */                 .getClass().getName() }));
/*     */       }
/* 282 */       this._delegateDeserializer = findDeserializer(ctxt, delegateType, null);
/*     */     } 
/* 284 */     if (this._valueInstantiator.canCreateFromObjectWith()) {
/* 285 */       SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/* 286 */       this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, ctxt
/* 287 */           .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
/*     */     } 
/* 289 */     this._standardStringKey = _isStdKeyDeser(this._containerType, this._keyDeserializer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 300 */     KeyDeserializer keyDeser = this._keyDeserializer;
/* 301 */     if (keyDeser == null) {
/* 302 */       keyDeser = ctxt.findKeyDeserializer(this._containerType.getKeyType(), property);
/*     */     }
/* 304 */     else if (keyDeser instanceof ContextualKeyDeserializer) {
/* 305 */       keyDeser = ((ContextualKeyDeserializer)keyDeser).createContextual(ctxt, property);
/*     */     } 
/*     */ 
/*     */     
/* 309 */     JsonDeserializer<?> valueDeser = this._valueDeserializer;
/*     */     
/* 311 */     if (property != null) {
/* 312 */       valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
/*     */     }
/* 314 */     JavaType vt = this._containerType.getContentType();
/* 315 */     if (valueDeser == null) {
/* 316 */       valueDeser = ctxt.findContextualValueDeserializer(vt, property);
/*     */     } else {
/* 318 */       valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
/*     */     } 
/* 320 */     TypeDeserializer vtd = this._valueTypeDeserializer;
/* 321 */     if (vtd != null) {
/* 322 */       vtd = vtd.forProperty(property);
/*     */     }
/* 324 */     Set<String> ignored = this._ignorableProperties;
/* 325 */     Set<String> included = this._includableProperties;
/* 326 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 327 */     if (_neitherNull(intr, property)) {
/* 328 */       AnnotatedMember member = property.getMember();
/* 329 */       if (member != null) {
/* 330 */         DeserializationConfig config = ctxt.getConfig();
/* 331 */         JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnoralByName((MapperConfig)config, (Annotated)member);
/* 332 */         if (ignorals != null) {
/* 333 */           Set<String> ignoresToAdd = ignorals.findIgnoredForDeserialization();
/* 334 */           if (!ignoresToAdd.isEmpty()) {
/* 335 */             ignored = (ignored == null) ? new HashSet<>() : new HashSet<>(ignored);
/* 336 */             for (String str : ignoresToAdd) {
/* 337 */               ignored.add(str);
/*     */             }
/*     */           } 
/*     */         } 
/* 341 */         JsonIncludeProperties.Value inclusions = intr.findPropertyInclusionByName((MapperConfig)config, (Annotated)member);
/* 342 */         if (inclusions != null) {
/* 343 */           Set<String> includedToAdd = inclusions.getIncluded();
/* 344 */           if (includedToAdd != null) {
/* 345 */             Set<String> newIncluded = new HashSet<>();
/* 346 */             if (included == null) {
/* 347 */               newIncluded = new HashSet<>(includedToAdd);
/*     */             } else {
/* 349 */               for (String str : includedToAdd) {
/* 350 */                 if (included.contains(str)) {
/* 351 */                   newIncluded.add(str);
/*     */                 }
/*     */               } 
/*     */             } 
/* 355 */             included = newIncluded;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 360 */     return withResolved(keyDeser, vtd, valueDeser, 
/* 361 */         findContentNullProvider(ctxt, property, valueDeser), ignored, included);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<Object> getContentDeserializer() {
/* 372 */     return this._valueDeserializer;
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueInstantiator getValueInstantiator() {
/* 377 */     return this._valueInstantiator;
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
/*     */   public boolean isCachable() {
/* 403 */     return (this._valueDeserializer == null && this._keyDeserializer == null && this._valueTypeDeserializer == null && this._ignorableProperties == null && this._includableProperties == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 412 */     return LogicalType.Map;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*     */     Map<Object, Object> result;
/* 419 */     if (this._propertyBasedCreator != null) {
/* 420 */       return _deserializeUsingCreator(p, ctxt);
/*     */     }
/* 422 */     if (this._delegateDeserializer != null) {
/* 423 */       return (Map<Object, Object>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer
/* 424 */           .deserialize(p, ctxt));
/*     */     }
/* 426 */     if (!this._hasDefaultCreator) {
/* 427 */       return (Map<Object, Object>)ctxt.handleMissingInstantiator(getMapClass(), 
/* 428 */           getValueInstantiator(), p, "no default constructor found", new Object[0]);
/*     */     }
/*     */     
/* 431 */     switch (p.currentTokenId()) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 5:
/* 435 */         result = (Map<Object, Object>)this._valueInstantiator.createUsingDefault(ctxt);
/* 436 */         if (this._standardStringKey) {
/* 437 */           _readAndBindStringKeyMap(p, ctxt, result);
/* 438 */           return result;
/*     */         } 
/* 440 */         _readAndBind(p, ctxt, result);
/* 441 */         return result;
/*     */       
/*     */       case 6:
/* 444 */         return _deserializeFromString(p, ctxt);
/*     */       
/*     */       case 3:
/* 447 */         return _deserializeFromArray(p, ctxt);
/*     */     } 
/*     */     
/* 450 */     return (Map<Object, Object>)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
/* 460 */     p.setCurrentValue(result);
/*     */ 
/*     */     
/* 463 */     JsonToken t = p.currentToken();
/* 464 */     if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
/* 465 */       return (Map<Object, Object>)ctxt.handleUnexpectedToken(getMapClass(), p);
/*     */     }
/*     */     
/* 468 */     if (this._standardStringKey) {
/* 469 */       _readAndUpdateStringKeyMap(p, ctxt, result);
/* 470 */       return result;
/*     */     } 
/* 472 */     _readAndUpdate(p, ctxt, result);
/* 473 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 482 */     return typeDeserializer.deserializeTypedFromObject(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Class<?> getMapClass() {
/* 492 */     return this._containerType.getRawClass();
/*     */   } public JavaType getValueType() {
/* 494 */     return this._containerType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void _readAndBind(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
/* 505 */     KeyDeserializer keyDes = this._keyDeserializer;
/* 506 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 507 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */     
/* 509 */     MapReferringAccumulator referringAccumulator = null;
/* 510 */     boolean useObjectId = (valueDes.getObjectIdReader() != null);
/* 511 */     if (useObjectId) {
/* 512 */       referringAccumulator = new MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 517 */     if (p.isExpectedStartObjectToken()) {
/* 518 */       str = p.nextFieldName();
/*     */     } else {
/* 520 */       JsonToken t = p.currentToken();
/* 521 */       if (t != JsonToken.FIELD_NAME) {
/* 522 */         if (t == JsonToken.END_OBJECT) {
/*     */           return;
/*     */         }
/* 525 */         ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
/*     */       } 
/* 527 */       str = p.currentName();
/*     */     } 
/*     */     String str;
/* 530 */     for (; str != null; str = p.nextFieldName()) {
/* 531 */       Object key = keyDes.deserializeKey(str, ctxt);
/*     */       
/* 533 */       JsonToken t = p.nextToken();
/* 534 */       if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(str)) {
/* 535 */         p.skipChildren();
/*     */         
/*     */         continue;
/*     */       } 
/*     */       try {
/*     */         Object value;
/* 541 */         if (t == JsonToken.VALUE_NULL) {
/* 542 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 545 */           value = this._nullProvider.getNullValue(ctxt);
/* 546 */         } else if (typeDeser == null) {
/* 547 */           value = valueDes.deserialize(p, ctxt);
/*     */         } else {
/* 549 */           value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */         } 
/* 551 */         if (useObjectId) {
/* 552 */           referringAccumulator.put(key, value);
/*     */         } else {
/* 554 */           result.put(key, value);
/*     */         } 
/* 556 */       } catch (UnresolvedForwardReference reference) {
/* 557 */         handleUnresolvedReference(ctxt, referringAccumulator, key, reference);
/* 558 */       } catch (Exception e) {
/* 559 */         wrapAndThrow(ctxt, e, result, str);
/*     */       } 
/*     */       continue;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void _readAndBindStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
/* 572 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 573 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/* 574 */     MapReferringAccumulator referringAccumulator = null;
/* 575 */     boolean useObjectId = (valueDes.getObjectIdReader() != null);
/* 576 */     if (useObjectId) {
/* 577 */       referringAccumulator = new MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
/*     */     }
/*     */ 
/*     */     
/* 581 */     if (p.isExpectedStartObjectToken()) {
/* 582 */       str = p.nextFieldName();
/*     */     } else {
/* 584 */       JsonToken t = p.currentToken();
/* 585 */       if (t == JsonToken.END_OBJECT) {
/*     */         return;
/*     */       }
/* 588 */       if (t != JsonToken.FIELD_NAME) {
/* 589 */         ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
/*     */       }
/* 591 */       str = p.currentName();
/*     */     } 
/*     */     String str;
/* 594 */     for (; str != null; str = p.nextFieldName()) {
/* 595 */       JsonToken t = p.nextToken();
/* 596 */       if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(str)) {
/* 597 */         p.skipChildren();
/*     */         
/*     */         continue;
/*     */       } 
/*     */       try {
/*     */         Object value;
/* 603 */         if (t == JsonToken.VALUE_NULL) {
/* 604 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 607 */           value = this._nullProvider.getNullValue(ctxt);
/* 608 */         } else if (typeDeser == null) {
/* 609 */           value = valueDes.deserialize(p, ctxt);
/*     */         } else {
/* 611 */           value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */         } 
/* 613 */         if (useObjectId) {
/* 614 */           referringAccumulator.put(str, value);
/*     */         } else {
/* 616 */           result.put(str, value);
/*     */         } 
/* 618 */       } catch (UnresolvedForwardReference reference) {
/* 619 */         handleUnresolvedReference(ctxt, referringAccumulator, str, reference);
/* 620 */       } catch (Exception e) {
/* 621 */         wrapAndThrow(ctxt, e, result, str);
/*     */       } 
/*     */       continue;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<Object, Object> _deserializeUsingCreator(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 630 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/*     */     
/* 632 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
/*     */     
/* 634 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 635 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */ 
/*     */     
/* 638 */     if (p.isExpectedStartObjectToken()) {
/* 639 */       str = p.nextFieldName();
/* 640 */     } else if (p.hasToken(JsonToken.FIELD_NAME)) {
/* 641 */       str = p.currentName();
/*     */     } else {
/* 643 */       str = null;
/*     */     } 
/*     */     String str;
/* 646 */     for (; str != null; str = p.nextFieldName()) {
/* 647 */       Object value; JsonToken t = p.nextToken();
/* 648 */       if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(str)) {
/* 649 */         p.skipChildren();
/*     */         
/*     */         continue;
/*     */       } 
/* 653 */       SettableBeanProperty prop = creator.findCreatorProperty(str);
/* 654 */       if (prop != null) {
/*     */         
/* 656 */         if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
/* 657 */           Map<Object, Object> result; p.nextToken();
/*     */           
/*     */           try {
/* 660 */             result = (Map<Object, Object>)creator.build(ctxt, buffer);
/* 661 */           } catch (Exception e) {
/* 662 */             return (Map<Object, Object>)wrapAndThrow(ctxt, e, this._containerType.getRawClass(), str);
/*     */           } 
/* 664 */           _readAndBind(p, ctxt, result);
/* 665 */           return result;
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/* 670 */       Object actualKey = this._keyDeserializer.deserializeKey(str, ctxt);
/*     */ 
/*     */       
/*     */       try {
/* 674 */         if (t == JsonToken.VALUE_NULL) {
/* 675 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 678 */           value = this._nullProvider.getNullValue(ctxt);
/* 679 */         } else if (typeDeser == null) {
/* 680 */           value = valueDes.deserialize(p, ctxt);
/*     */         } else {
/* 682 */           value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */         } 
/* 684 */       } catch (Exception e) {
/* 685 */         wrapAndThrow(ctxt, e, this._containerType.getRawClass(), str);
/* 686 */         return null;
/*     */       } 
/* 688 */       buffer.bufferMapProperty(actualKey, value);
/*     */       
/*     */       continue;
/*     */     } 
/*     */     try {
/* 693 */       return (Map<Object, Object>)creator.build(ctxt, buffer);
/* 694 */     } catch (Exception e) {
/* 695 */       wrapAndThrow(ctxt, e, this._containerType.getRawClass(), str);
/* 696 */       return null;
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
/*     */   protected final void _readAndUpdate(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
/* 712 */     KeyDeserializer keyDes = this._keyDeserializer;
/* 713 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 714 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 720 */     if (p.isExpectedStartObjectToken()) {
/* 721 */       keyStr = p.nextFieldName();
/*     */     } else {
/* 723 */       JsonToken t = p.currentToken();
/* 724 */       if (t == JsonToken.END_OBJECT) {
/*     */         return;
/*     */       }
/* 727 */       if (t != JsonToken.FIELD_NAME) {
/* 728 */         ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
/*     */       }
/* 730 */       keyStr = p.currentName();
/*     */     } 
/*     */     String keyStr;
/* 733 */     for (; keyStr != null; keyStr = p.nextFieldName()) {
/* 734 */       Object key = keyDes.deserializeKey(keyStr, ctxt);
/*     */       
/* 736 */       JsonToken t = p.nextToken();
/* 737 */       if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyStr)) {
/* 738 */         p.skipChildren();
/*     */       } else {
/*     */ 
/*     */         
/*     */         try {
/* 743 */           if (t == JsonToken.VALUE_NULL) {
/* 744 */             if (!this._skipNullValues)
/*     */             {
/*     */               
/* 747 */               result.put(key, this._nullProvider.getNullValue(ctxt));
/*     */             }
/*     */           } else {
/* 750 */             Object value, old = result.get(key);
/*     */             
/* 752 */             if (old != null) {
/* 753 */               if (typeDeser == null) {
/* 754 */                 value = valueDes.deserialize(p, ctxt, old);
/*     */               } else {
/* 756 */                 value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
/*     */               } 
/* 758 */             } else if (typeDeser == null) {
/* 759 */               value = valueDes.deserialize(p, ctxt);
/*     */             } else {
/* 761 */               value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */             } 
/* 763 */             if (value != old)
/* 764 */               result.put(key, value); 
/*     */           } 
/* 766 */         } catch (Exception e) {
/* 767 */           wrapAndThrow(ctxt, e, result, keyStr);
/*     */         } 
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
/*     */   protected final void _readAndUpdateStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
/* 782 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 783 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 789 */     if (p.isExpectedStartObjectToken()) {
/* 790 */       key = p.nextFieldName();
/*     */     } else {
/* 792 */       JsonToken t = p.currentToken();
/* 793 */       if (t == JsonToken.END_OBJECT) {
/*     */         return;
/*     */       }
/* 796 */       if (t != JsonToken.FIELD_NAME) {
/* 797 */         ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
/*     */       }
/* 799 */       key = p.currentName();
/*     */     } 
/*     */     String key;
/* 802 */     for (; key != null; key = p.nextFieldName()) {
/* 803 */       JsonToken t = p.nextToken();
/* 804 */       if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
/* 805 */         p.skipChildren();
/*     */       } else {
/*     */ 
/*     */         
/*     */         try {
/* 810 */           if (t == JsonToken.VALUE_NULL) {
/* 811 */             if (!this._skipNullValues)
/*     */             {
/*     */               
/* 814 */               result.put(key, this._nullProvider.getNullValue(ctxt));
/*     */             }
/*     */           } else {
/* 817 */             Object value, old = result.get(key);
/*     */             
/* 819 */             if (old != null) {
/* 820 */               if (typeDeser == null) {
/* 821 */                 value = valueDes.deserialize(p, ctxt, old);
/*     */               } else {
/* 823 */                 value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
/*     */               } 
/* 825 */             } else if (typeDeser == null) {
/* 826 */               value = valueDes.deserialize(p, ctxt);
/*     */             } else {
/* 828 */               value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */             } 
/* 830 */             if (value != old)
/* 831 */               result.put(key, value); 
/*     */           } 
/* 833 */         } catch (Exception e) {
/* 834 */           wrapAndThrow(ctxt, e, result, key);
/*     */         } 
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
/*     */   private void handleUnresolvedReference(DeserializationContext ctxt, MapReferringAccumulator accumulator, Object key, UnresolvedForwardReference reference) throws JsonMappingException {
/* 850 */     if (accumulator == null) {
/* 851 */       ctxt.reportInputMismatch(this, "Unresolved forward reference but no identity info: " + reference, new Object[0]);
/*     */     }
/*     */     
/* 854 */     ReadableObjectId.Referring referring = accumulator.handleUnresolvedReference(reference, key);
/* 855 */     reference.getRoid().appendReferring(referring);
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class MapReferringAccumulator
/*     */   {
/*     */     private final Class<?> _valueType;
/*     */     
/*     */     private Map<Object, Object> _result;
/* 864 */     private List<MapDeserializer.MapReferring> _accumulator = new ArrayList<>();
/*     */     
/*     */     public MapReferringAccumulator(Class<?> valueType, Map<Object, Object> result) {
/* 867 */       this._valueType = valueType;
/* 868 */       this._result = result;
/*     */     }
/*     */ 
/*     */     
/*     */     public void put(Object key, Object value) {
/* 873 */       if (this._accumulator.isEmpty()) {
/* 874 */         this._result.put(key, value);
/*     */       } else {
/* 876 */         MapDeserializer.MapReferring ref = this._accumulator.get(this._accumulator.size() - 1);
/* 877 */         ref.next.put(key, value);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference, Object key) {
/* 883 */       MapDeserializer.MapReferring id = new MapDeserializer.MapReferring(this, reference, this._valueType, key);
/* 884 */       this._accumulator.add(id);
/* 885 */       return id;
/*     */     }
/*     */ 
/*     */     
/*     */     public void resolveForwardReference(Object id, Object value) throws IOException {
/* 890 */       Iterator<MapDeserializer.MapReferring> iterator = this._accumulator.iterator();
/*     */ 
/*     */ 
/*     */       
/* 894 */       Map<Object, Object> previous = this._result;
/* 895 */       while (iterator.hasNext()) {
/* 896 */         MapDeserializer.MapReferring ref = iterator.next();
/* 897 */         if (ref.hasId(id)) {
/* 898 */           iterator.remove();
/* 899 */           previous.put(ref.key, value);
/* 900 */           previous.putAll(ref.next);
/*     */           return;
/*     */         } 
/* 903 */         previous = ref.next;
/*     */       } 
/*     */       
/* 906 */       throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class MapReferring
/*     */     extends ReadableObjectId.Referring
/*     */   {
/*     */     private final MapDeserializer.MapReferringAccumulator _parent;
/*     */ 
/*     */     
/* 919 */     public final Map<Object, Object> next = new LinkedHashMap<>();
/*     */     
/*     */     public final Object key;
/*     */ 
/*     */     
/*     */     MapReferring(MapDeserializer.MapReferringAccumulator parent, UnresolvedForwardReference ref, Class<?> valueType, Object key) {
/* 925 */       super(ref, valueType);
/* 926 */       this._parent = parent;
/* 927 */       this.key = key;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleResolvedForwardReference(Object id, Object value) throws IOException {
/* 932 */       this._parent.resolveForwardReference(id, value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/MapDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */