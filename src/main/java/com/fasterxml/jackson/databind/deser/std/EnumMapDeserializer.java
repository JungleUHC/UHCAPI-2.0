/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*     */ import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.util.EnumMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EnumMapDeserializer
/*     */   extends ContainerDeserializerBase<EnumMap<?, ?>>
/*     */   implements ContextualDeserializer, ResolvableDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Class<?> _enumClass;
/*     */   protected KeyDeserializer _keyDeserializer;
/*     */   protected JsonDeserializer<Object> _valueDeserializer;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected JsonDeserializer<Object> _delegateDeserializer;
/*     */   protected PropertyBasedCreator _propertyBasedCreator;
/*     */   
/*     */   public EnumMapDeserializer(JavaType mapType, ValueInstantiator valueInst, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd, NullValueProvider nuller) {
/*  78 */     super(mapType, nuller, (Boolean)null);
/*  79 */     this._enumClass = mapType.getKeyType().getRawClass();
/*  80 */     this._keyDeserializer = keyDeser;
/*  81 */     this._valueDeserializer = (JsonDeserializer)valueDeser;
/*  82 */     this._valueTypeDeserializer = vtd;
/*  83 */     this._valueInstantiator = valueInst;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected EnumMapDeserializer(EnumMapDeserializer base, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd, NullValueProvider nuller) {
/*  93 */     super(base, nuller, base._unwrapSingle);
/*  94 */     this._enumClass = base._enumClass;
/*  95 */     this._keyDeserializer = keyDeser;
/*  96 */     this._valueDeserializer = (JsonDeserializer)valueDeser;
/*  97 */     this._valueTypeDeserializer = vtd;
/*     */     
/*  99 */     this._valueInstantiator = base._valueInstantiator;
/* 100 */     this._delegateDeserializer = base._delegateDeserializer;
/* 101 */     this._propertyBasedCreator = base._propertyBasedCreator;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public EnumMapDeserializer(JavaType mapType, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd) {
/* 108 */     this(mapType, (ValueInstantiator)null, keyDeser, valueDeser, vtd, (NullValueProvider)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EnumMapDeserializer withResolved(KeyDeserializer keyDeserializer, JsonDeserializer<?> valueDeserializer, TypeDeserializer valueTypeDeser, NullValueProvider nuller) {
/* 115 */     if (keyDeserializer == this._keyDeserializer && nuller == this._nullProvider && valueDeserializer == this._valueDeserializer && valueTypeDeser == this._valueTypeDeserializer)
/*     */     {
/* 117 */       return this;
/*     */     }
/* 119 */     return new EnumMapDeserializer(this, keyDeserializer, valueDeserializer, valueTypeDeser, nuller);
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
/*     */   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
/* 133 */     if (this._valueInstantiator != null) {
/* 134 */       if (this._valueInstantiator.canCreateUsingDelegate()) {
/* 135 */         JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
/* 136 */         if (delegateType == null) {
/* 137 */           ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", new Object[] { this._containerType, this._valueInstantiator
/*     */ 
/*     */                   
/* 140 */                   .getClass().getName() }));
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 146 */         this._delegateDeserializer = findDeserializer(ctxt, delegateType, null);
/* 147 */       } else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
/* 148 */         JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
/* 149 */         if (delegateType == null) {
/* 150 */           ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", new Object[] { this._containerType, this._valueInstantiator
/*     */ 
/*     */                   
/* 153 */                   .getClass().getName() }));
/*     */         }
/* 155 */         this._delegateDeserializer = findDeserializer(ctxt, delegateType, null);
/* 156 */       } else if (this._valueInstantiator.canCreateFromObjectWith()) {
/* 157 */         SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/* 158 */         this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, ctxt
/* 159 */             .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
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
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 174 */     KeyDeserializer keyDeser = this._keyDeserializer;
/* 175 */     if (keyDeser == null) {
/* 176 */       keyDeser = ctxt.findKeyDeserializer(this._containerType.getKeyType(), property);
/*     */     }
/* 178 */     JsonDeserializer<?> valueDeser = this._valueDeserializer;
/* 179 */     JavaType vt = this._containerType.getContentType();
/* 180 */     if (valueDeser == null) {
/* 181 */       valueDeser = ctxt.findContextualValueDeserializer(vt, property);
/*     */     } else {
/* 183 */       valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
/*     */     } 
/* 185 */     TypeDeserializer vtd = this._valueTypeDeserializer;
/* 186 */     if (vtd != null) {
/* 187 */       vtd = vtd.forProperty(property);
/*     */     }
/* 189 */     return withResolved(keyDeser, valueDeser, vtd, findContentNullProvider(ctxt, property, valueDeser));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCachable() {
/* 199 */     return (this._valueDeserializer == null && this._keyDeserializer == null && this._valueTypeDeserializer == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 206 */     return LogicalType.Map;
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
/* 217 */     return this._valueDeserializer;
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueInstantiator getValueInstantiator() {
/* 222 */     return this._valueInstantiator;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 227 */     return constructMap(ctxt);
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
/*     */   public EnumMap<?, ?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 240 */     if (this._propertyBasedCreator != null) {
/* 241 */       return _deserializeUsingProperties(p, ctxt);
/*     */     }
/* 243 */     if (this._delegateDeserializer != null) {
/* 244 */       return (EnumMap<?, ?>)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer
/* 245 */           .deserialize(p, ctxt));
/*     */     }
/*     */     
/* 248 */     switch (p.currentTokenId()) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 5:
/* 252 */         return deserialize(p, ctxt, constructMap(ctxt));
/*     */       
/*     */       case 6:
/* 255 */         return _deserializeFromString(p, ctxt);
/*     */       
/*     */       case 3:
/* 258 */         return _deserializeFromArray(p, ctxt);
/*     */     } 
/*     */     
/* 261 */     return (EnumMap<?, ?>)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EnumMap<?, ?> deserialize(JsonParser p, DeserializationContext ctxt, EnumMap<?, ?> result) throws IOException {
/* 270 */     p.setCurrentValue(result);
/*     */     
/* 272 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 273 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */ 
/*     */     
/* 276 */     if (p.isExpectedStartObjectToken()) {
/* 277 */       str = p.nextFieldName();
/*     */     } else {
/* 279 */       JsonToken t = p.currentToken();
/* 280 */       if (t != JsonToken.FIELD_NAME) {
/* 281 */         if (t == JsonToken.END_OBJECT) {
/* 282 */           return result;
/*     */         }
/* 284 */         ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
/*     */       } 
/* 286 */       str = p.currentName();
/*     */     } 
/*     */     String str;
/* 289 */     for (; str != null; str = p.nextFieldName()) {
/*     */       Object value;
/* 291 */       Enum<?> key = (Enum)this._keyDeserializer.deserializeKey(str, ctxt);
/* 292 */       JsonToken t = p.nextToken();
/* 293 */       if (key == null) {
/* 294 */         if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
/* 295 */           return (EnumMap<?, ?>)ctxt.handleWeirdStringValue(this._enumClass, str, "value not one of declared Enum instance names for %s", new Object[] { this._containerType
/*     */                 
/* 297 */                 .getKeyType() });
/*     */         }
/*     */ 
/*     */         
/* 301 */         p.skipChildren();
/*     */ 
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 310 */         if (t == JsonToken.VALUE_NULL) {
/* 311 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 314 */           value = this._nullProvider.getNullValue(ctxt);
/* 315 */         } else if (typeDeser == null) {
/* 316 */           value = valueDes.deserialize(p, ctxt);
/*     */         } else {
/* 318 */           value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */         } 
/* 320 */       } catch (Exception e) {
/* 321 */         return (EnumMap<?, ?>)wrapAndThrow(ctxt, e, result, str);
/*     */       } 
/* 323 */       result.put(key, value); continue;
/*     */     } 
/* 325 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 334 */     return typeDeserializer.deserializeTypedFromObject(p, ctxt);
/*     */   }
/*     */   
/*     */   protected EnumMap<?, ?> constructMap(DeserializationContext ctxt) throws JsonMappingException {
/* 338 */     if (this._valueInstantiator == null) {
/* 339 */       return new EnumMap<>(this._enumClass);
/*     */     }
/*     */     try {
/* 342 */       if (!this._valueInstantiator.canCreateUsingDefault()) {
/* 343 */         return (EnumMap<?, ?>)ctxt.handleMissingInstantiator(handledType(), 
/* 344 */             getValueInstantiator(), null, "no default constructor found", new Object[0]);
/*     */       }
/*     */       
/* 347 */       return (EnumMap<?, ?>)this._valueInstantiator.createUsingDefault(ctxt);
/* 348 */     } catch (IOException e) {
/* 349 */       return (EnumMap<?, ?>)ClassUtil.throwAsMappingException(ctxt, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public EnumMap<?, ?> _deserializeUsingProperties(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 355 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/*     */     
/* 357 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
/*     */ 
/*     */     
/* 360 */     if (p.isExpectedStartObjectToken()) {
/* 361 */       str = p.nextFieldName();
/* 362 */     } else if (p.hasToken(JsonToken.FIELD_NAME)) {
/* 363 */       str = p.currentName();
/*     */     } else {
/* 365 */       str = null;
/*     */     } 
/*     */     String str;
/* 368 */     for (; str != null; str = p.nextFieldName()) {
/* 369 */       Object value; JsonToken t = p.nextToken();
/*     */       
/* 371 */       SettableBeanProperty prop = creator.findCreatorProperty(str);
/* 372 */       if (prop != null) {
/*     */         
/* 374 */         if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
/* 375 */           EnumMap<?, ?> result; p.nextToken();
/*     */           
/*     */           try {
/* 378 */             result = (EnumMap<?, ?>)creator.build(ctxt, buffer);
/* 379 */           } catch (Exception e) {
/* 380 */             return (EnumMap<?, ?>)wrapAndThrow(ctxt, e, this._containerType.getRawClass(), str);
/*     */           } 
/* 382 */           return deserialize(p, ctxt, result);
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 388 */       Enum<?> key = (Enum)this._keyDeserializer.deserializeKey(str, ctxt);
/* 389 */       if (key == null) {
/* 390 */         if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
/* 391 */           return (EnumMap<?, ?>)ctxt.handleWeirdStringValue(this._enumClass, str, "value not one of declared Enum instance names for %s", new Object[] { this._containerType
/*     */                 
/* 393 */                 .getKeyType() });
/*     */         }
/*     */ 
/*     */         
/* 397 */         p.nextToken();
/* 398 */         p.skipChildren();
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/*     */       try {
/* 404 */         if (t == JsonToken.VALUE_NULL) {
/* 405 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 408 */           value = this._nullProvider.getNullValue(ctxt);
/* 409 */         } else if (this._valueTypeDeserializer == null) {
/* 410 */           value = this._valueDeserializer.deserialize(p, ctxt);
/*     */         } else {
/* 412 */           value = this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
/*     */         } 
/* 414 */       } catch (Exception e) {
/* 415 */         wrapAndThrow(ctxt, e, this._containerType.getRawClass(), str);
/* 416 */         return null;
/*     */       } 
/* 418 */       buffer.bufferMapProperty(key, value);
/*     */       
/*     */       continue;
/*     */     } 
/*     */     try {
/* 423 */       return (EnumMap<?, ?>)creator.build(ctxt, buffer);
/* 424 */     } catch (Exception e) {
/* 425 */       wrapAndThrow(ctxt, e, this._containerType.getRawClass(), str);
/* 426 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/EnumMapDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */