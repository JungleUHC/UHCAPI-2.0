/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*     */ import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public class CollectionDeserializer
/*     */   extends ContainerDeserializerBase<Collection<Object>>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = -1L;
/*     */   protected final JsonDeserializer<Object> _valueDeserializer;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected final JsonDeserializer<Object> _delegateDeserializer;
/*     */   
/*     */   public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator) {
/*  74 */     this(collectionType, valueDeser, valueTypeDeser, valueInstantiator, (JsonDeserializer<Object>)null, (NullValueProvider)null, (Boolean)null);
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
/*     */   protected CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator, JsonDeserializer<Object> delegateDeser, NullValueProvider nuller, Boolean unwrapSingle) {
/*  87 */     super(collectionType, nuller, unwrapSingle);
/*  88 */     this._valueDeserializer = valueDeser;
/*  89 */     this._valueTypeDeserializer = valueTypeDeser;
/*  90 */     this._valueInstantiator = valueInstantiator;
/*  91 */     this._delegateDeserializer = delegateDeser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CollectionDeserializer(CollectionDeserializer src) {
/* 100 */     super(src);
/* 101 */     this._valueDeserializer = src._valueDeserializer;
/* 102 */     this._valueTypeDeserializer = src._valueTypeDeserializer;
/* 103 */     this._valueInstantiator = src._valueInstantiator;
/* 104 */     this._delegateDeserializer = src._delegateDeserializer;
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
/*     */   protected CollectionDeserializer withResolved(JsonDeserializer<?> dd, JsonDeserializer<?> vd, TypeDeserializer vtd, NullValueProvider nuller, Boolean unwrapSingle) {
/* 117 */     return new CollectionDeserializer(this._containerType, (JsonDeserializer)vd, vtd, this._valueInstantiator, (JsonDeserializer)dd, nuller, unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCachable() {
/* 127 */     return (this._valueDeserializer == null && this._valueTypeDeserializer == null && this._delegateDeserializer == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 135 */     return LogicalType.Collection;
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
/*     */   public CollectionDeserializer createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 154 */     JsonDeserializer<Object> delegateDeser = null;
/* 155 */     if (this._valueInstantiator != null) {
/* 156 */       if (this._valueInstantiator.canCreateUsingDelegate()) {
/* 157 */         JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
/* 158 */         if (delegateType == null) {
/* 159 */           ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", new Object[] { this._containerType, this._valueInstantiator
/*     */ 
/*     */                   
/* 162 */                   .getClass().getName() }));
/*     */         }
/* 164 */         delegateDeser = findDeserializer(ctxt, delegateType, property);
/* 165 */       } else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
/* 166 */         JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
/* 167 */         if (delegateType == null) {
/* 168 */           ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", new Object[] { this._containerType, this._valueInstantiator
/*     */ 
/*     */                   
/* 171 */                   .getClass().getName() }));
/*     */         }
/* 173 */         delegateDeser = findDeserializer(ctxt, delegateType, property);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 179 */     Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
/*     */ 
/*     */     
/* 182 */     JsonDeserializer<?> valueDeser = this._valueDeserializer;
/*     */ 
/*     */     
/* 185 */     valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
/* 186 */     JavaType vt = this._containerType.getContentType();
/* 187 */     if (valueDeser == null) {
/* 188 */       valueDeser = ctxt.findContextualValueDeserializer(vt, property);
/*     */     } else {
/* 190 */       valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
/*     */     } 
/*     */     
/* 193 */     TypeDeserializer valueTypeDeser = this._valueTypeDeserializer;
/* 194 */     if (valueTypeDeser != null) {
/* 195 */       valueTypeDeser = valueTypeDeser.forProperty(property);
/*     */     }
/* 197 */     NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);
/* 198 */     if (!Objects.equals(unwrapSingle, this._unwrapSingle) || nuller != this._nullProvider || delegateDeser != this._delegateDeserializer || valueDeser != this._valueDeserializer || valueTypeDeser != this._valueTypeDeserializer)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 204 */       return withResolved(delegateDeser, valueDeser, valueTypeDeser, nuller, unwrapSingle);
/*     */     }
/*     */     
/* 207 */     return this;
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
/* 218 */     return this._valueDeserializer;
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueInstantiator getValueInstantiator() {
/* 223 */     return this._valueInstantiator;
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
/*     */   public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 237 */     if (this._delegateDeserializer != null) {
/* 238 */       return (Collection<Object>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer
/* 239 */           .deserialize(p, ctxt));
/*     */     }
/*     */ 
/*     */     
/* 243 */     if (p.isExpectedStartArrayToken()) {
/* 244 */       return _deserializeFromArray(p, ctxt, createDefaultInstance(ctxt));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 249 */     if (p.hasToken(JsonToken.VALUE_STRING)) {
/* 250 */       return _deserializeFromString(p, ctxt, p.getText());
/*     */     }
/* 252 */     return handleNonArray(p, ctxt, createDefaultInstance(ctxt));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Collection<Object> createDefaultInstance(DeserializationContext ctxt) throws IOException {
/* 262 */     return (Collection<Object>)this._valueInstantiator.createUsingDefault(ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
/* 271 */     if (p.isExpectedStartArrayToken()) {
/* 272 */       return _deserializeFromArray(p, ctxt, result);
/*     */     }
/* 274 */     return handleNonArray(p, ctxt, result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 283 */     return typeDeserializer.deserializeTypedFromArray(p, ctxt);
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
/*     */   protected Collection<Object> _deserializeFromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
/* 296 */     Class<?> rawTargetType = handledType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 305 */     if (value.isEmpty()) {
/* 306 */       CoercionAction act = ctxt.findCoercionAction(logicalType(), rawTargetType, CoercionInputShape.EmptyString);
/*     */       
/* 308 */       act = _checkCoercionFail(ctxt, act, rawTargetType, value, "empty String (\"\")");
/*     */       
/* 310 */       if (act != null)
/*     */       {
/*     */ 
/*     */         
/* 314 */         return (Collection<Object>)_deserializeFromEmptyString(p, ctxt, act, rawTargetType, "empty String (\"\")");
/*     */ 
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 320 */     else if (_isBlank(value)) {
/* 321 */       CoercionAction act = ctxt.findCoercionFromBlankString(logicalType(), rawTargetType, CoercionAction.Fail);
/*     */       
/* 323 */       return (Collection<Object>)_deserializeFromEmptyString(p, ctxt, act, rawTargetType, "blank String (all whitespace)");
/*     */     } 
/*     */     
/* 326 */     return handleNonArray(p, ctxt, createDefaultInstance(ctxt));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Collection<Object> _deserializeFromArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
/* 337 */     p.setCurrentValue(result);
/*     */     
/* 339 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/*     */     
/* 341 */     if (valueDes.getObjectIdReader() != null) {
/* 342 */       return _deserializeWithObjectId(p, ctxt, result);
/*     */     }
/* 344 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */     JsonToken t;
/* 346 */     while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */       try {
/*     */         Object value;
/* 349 */         if (t == JsonToken.VALUE_NULL) {
/* 350 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 353 */           value = this._nullProvider.getNullValue(ctxt);
/* 354 */         } else if (typeDeser == null) {
/* 355 */           value = valueDes.deserialize(p, ctxt);
/*     */         } else {
/* 357 */           value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */         } 
/* 359 */         result.add(value);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 366 */       catch (Exception e) {
/* 367 */         boolean wrap = (ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/* 368 */         if (!wrap) {
/* 369 */           ClassUtil.throwIfRTE(e);
/*     */         }
/* 371 */         throw JsonMappingException.wrapWithPath(e, result, result.size());
/*     */       } 
/*     */     } 
/* 374 */     return result;
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
/*     */   protected final Collection<Object> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
/*     */     Object value;
/* 390 */     boolean canWrap = (this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
/* 391 */     if (!canWrap) {
/* 392 */       return (Collection<Object>)ctxt.handleUnexpectedToken(this._containerType, p);
/*     */     }
/* 394 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 395 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 400 */       if (p.hasToken(JsonToken.VALUE_NULL)) {
/*     */         
/* 402 */         if (this._skipNullValues) {
/* 403 */           return result;
/*     */         }
/* 405 */         value = this._nullProvider.getNullValue(ctxt);
/* 406 */       } else if (typeDeser == null) {
/* 407 */         value = valueDes.deserialize(p, ctxt);
/*     */       } else {
/* 409 */         value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */       } 
/* 411 */     } catch (Exception e) {
/* 412 */       boolean wrap = ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
/* 413 */       if (!wrap) {
/* 414 */         ClassUtil.throwIfRTE(e);
/*     */       }
/*     */       
/* 417 */       throw JsonMappingException.wrapWithPath(e, Object.class, result.size());
/*     */     } 
/* 419 */     result.add(value);
/* 420 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Collection<Object> _deserializeWithObjectId(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
/* 428 */     if (!p.isExpectedStartArrayToken()) {
/* 429 */       return handleNonArray(p, ctxt, result);
/*     */     }
/*     */     
/* 432 */     p.setCurrentValue(result);
/*     */     
/* 434 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 435 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */     
/* 437 */     CollectionReferringAccumulator referringAccumulator = new CollectionReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
/*     */     
/*     */     JsonToken t;
/* 440 */     while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */       try {
/*     */         Object value;
/* 443 */         if (t == JsonToken.VALUE_NULL) {
/* 444 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 447 */           value = this._nullProvider.getNullValue(ctxt);
/* 448 */         } else if (typeDeser == null) {
/* 449 */           value = valueDes.deserialize(p, ctxt);
/*     */         } else {
/* 451 */           value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */         } 
/* 453 */         referringAccumulator.add(value);
/* 454 */       } catch (UnresolvedForwardReference reference) {
/* 455 */         ReadableObjectId.Referring ref = referringAccumulator.handleUnresolvedReference(reference);
/* 456 */         reference.getRoid().appendReferring(ref);
/* 457 */       } catch (Exception e) {
/* 458 */         boolean wrap = (ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/* 459 */         if (!wrap) {
/* 460 */           ClassUtil.throwIfRTE(e);
/*     */         }
/* 462 */         throw JsonMappingException.wrapWithPath(e, result, result.size());
/*     */       } 
/*     */     } 
/* 465 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CollectionReferringAccumulator
/*     */   {
/*     */     private final Class<?> _elementType;
/*     */ 
/*     */     
/*     */     private final Collection<Object> _result;
/*     */ 
/*     */     
/* 479 */     private List<CollectionDeserializer.CollectionReferring> _accumulator = new ArrayList<>();
/*     */     
/*     */     public CollectionReferringAccumulator(Class<?> elementType, Collection<Object> result) {
/* 482 */       this._elementType = elementType;
/* 483 */       this._result = result;
/*     */     }
/*     */ 
/*     */     
/*     */     public void add(Object value) {
/* 488 */       if (this._accumulator.isEmpty()) {
/* 489 */         this._result.add(value);
/*     */       } else {
/* 491 */         CollectionDeserializer.CollectionReferring ref = this._accumulator.get(this._accumulator.size() - 1);
/* 492 */         ref.next.add(value);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference) {
/* 498 */       CollectionDeserializer.CollectionReferring id = new CollectionDeserializer.CollectionReferring(this, reference, this._elementType);
/* 499 */       this._accumulator.add(id);
/* 500 */       return id;
/*     */     }
/*     */ 
/*     */     
/*     */     public void resolveForwardReference(Object id, Object value) throws IOException {
/* 505 */       Iterator<CollectionDeserializer.CollectionReferring> iterator = this._accumulator.iterator();
/*     */ 
/*     */ 
/*     */       
/* 509 */       Collection<Object> previous = this._result;
/* 510 */       while (iterator.hasNext()) {
/* 511 */         CollectionDeserializer.CollectionReferring ref = iterator.next();
/* 512 */         if (ref.hasId(id)) {
/* 513 */           iterator.remove();
/* 514 */           previous.add(value);
/* 515 */           previous.addAll(ref.next);
/*     */           return;
/*     */         } 
/* 518 */         previous = ref.next;
/*     */       } 
/*     */       
/* 521 */       throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class CollectionReferring
/*     */     extends ReadableObjectId.Referring
/*     */   {
/*     */     private final CollectionDeserializer.CollectionReferringAccumulator _parent;
/*     */ 
/*     */     
/* 533 */     public final List<Object> next = new ArrayList();
/*     */ 
/*     */ 
/*     */     
/*     */     CollectionReferring(CollectionDeserializer.CollectionReferringAccumulator parent, UnresolvedForwardReference reference, Class<?> contentType) {
/* 538 */       super(reference, contentType);
/* 539 */       this._parent = parent;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleResolvedForwardReference(Object id, Object value) throws IOException {
/* 544 */       this._parent.resolveForwardReference(id, value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/CollectionDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */