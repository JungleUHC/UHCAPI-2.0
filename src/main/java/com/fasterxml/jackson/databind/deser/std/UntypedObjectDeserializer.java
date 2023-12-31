/*      */ package com.fasterxml.jackson.databind.deser.std;
/*      */ 
/*      */ import com.fasterxml.jackson.core.JsonParseException;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.StreamReadCapability;
/*      */ import com.fasterxml.jackson.databind.BeanProperty;
/*      */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*      */ import com.fasterxml.jackson.databind.DeserializationContext;
/*      */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*      */ import com.fasterxml.jackson.databind.JsonMappingException;
/*      */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*      */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*      */ import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.fasterxml.jackson.databind.type.LogicalType;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import com.fasterxml.jackson.databind.util.ObjectBuffer;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ @JacksonStdImpl
/*      */ public class UntypedObjectDeserializer
/*      */   extends StdDeserializer<Object>
/*      */   implements ResolvableDeserializer, ContextualDeserializer
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*   35 */   protected static final Object[] NO_OBJECTS = new Object[0];
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _mapDeserializer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _listDeserializer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _stringDeserializer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _numberDeserializer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _listType;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JavaType _mapType;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _nonMerging;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public UntypedObjectDeserializer() {
/*   77 */     this((JavaType)null, (JavaType)null);
/*      */   }
/*      */   
/*      */   public UntypedObjectDeserializer(JavaType listType, JavaType mapType) {
/*   81 */     super(Object.class);
/*   82 */     this._listType = listType;
/*   83 */     this._mapType = mapType;
/*   84 */     this._nonMerging = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UntypedObjectDeserializer(UntypedObjectDeserializer base, JsonDeserializer<?> mapDeser, JsonDeserializer<?> listDeser, JsonDeserializer<?> stringDeser, JsonDeserializer<?> numberDeser) {
/*   92 */     super(Object.class);
/*   93 */     this._mapDeserializer = (JsonDeserializer)mapDeser;
/*   94 */     this._listDeserializer = (JsonDeserializer)listDeser;
/*   95 */     this._stringDeserializer = (JsonDeserializer)stringDeser;
/*   96 */     this._numberDeserializer = (JsonDeserializer)numberDeser;
/*   97 */     this._listType = base._listType;
/*   98 */     this._mapType = base._mapType;
/*   99 */     this._nonMerging = base._nonMerging;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected UntypedObjectDeserializer(UntypedObjectDeserializer base, boolean nonMerging) {
/*  108 */     super(Object.class);
/*  109 */     this._mapDeserializer = base._mapDeserializer;
/*  110 */     this._listDeserializer = base._listDeserializer;
/*  111 */     this._stringDeserializer = base._stringDeserializer;
/*  112 */     this._numberDeserializer = base._numberDeserializer;
/*  113 */     this._listType = base._listType;
/*  114 */     this._mapType = base._mapType;
/*  115 */     this._nonMerging = nonMerging;
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
/*      */   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
/*  133 */     JavaType obType = ctxt.constructType(Object.class);
/*  134 */     JavaType stringType = ctxt.constructType(String.class);
/*  135 */     TypeFactory tf = ctxt.getTypeFactory();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  147 */     if (this._listType == null) {
/*  148 */       this._listDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, (JavaType)tf.constructCollectionType(List.class, obType)));
/*      */     } else {
/*      */       
/*  151 */       this._listDeserializer = _findCustomDeser(ctxt, this._listType);
/*      */     } 
/*  153 */     if (this._mapType == null) {
/*  154 */       this._mapDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, (JavaType)tf.constructMapType(Map.class, stringType, obType)));
/*      */     } else {
/*      */       
/*  157 */       this._mapDeserializer = _findCustomDeser(ctxt, this._mapType);
/*      */     } 
/*  159 */     this._stringDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, stringType));
/*  160 */     this._numberDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructType(Number.class)));
/*      */ 
/*      */ 
/*      */     
/*  164 */     JavaType unknown = TypeFactory.unknownType();
/*  165 */     this._mapDeserializer = ctxt.handleSecondaryContextualization(this._mapDeserializer, null, unknown);
/*  166 */     this._listDeserializer = ctxt.handleSecondaryContextualization(this._listDeserializer, null, unknown);
/*  167 */     this._stringDeserializer = ctxt.handleSecondaryContextualization(this._stringDeserializer, null, unknown);
/*  168 */     this._numberDeserializer = ctxt.handleSecondaryContextualization(this._numberDeserializer, null, unknown);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _findCustomDeser(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*  176 */     return ctxt.findNonContextualValueDeserializer(type);
/*      */   }
/*      */   
/*      */   protected JsonDeserializer<Object> _clearIfStdImpl(JsonDeserializer<Object> deser) {
/*  180 */     return ClassUtil.isJacksonStdImpl(deser) ? null : deser;
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
/*      */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/*  193 */     boolean preventMerge = (property == null && Boolean.FALSE.equals(ctxt.getConfig().getDefaultMergeable(Object.class)));
/*      */ 
/*      */     
/*  196 */     if (this._stringDeserializer == null && this._numberDeserializer == null && this._mapDeserializer == null && this._listDeserializer == null && 
/*      */       
/*  198 */       getClass() == UntypedObjectDeserializer.class) {
/*  199 */       return Vanilla.instance(preventMerge);
/*      */     }
/*      */     
/*  202 */     if (preventMerge != this._nonMerging) {
/*  203 */       return new UntypedObjectDeserializer(this, preventMerge);
/*      */     }
/*      */     
/*  206 */     return this;
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
/*      */   public boolean isCachable() {
/*  224 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public LogicalType logicalType() {
/*  229 */     return LogicalType.Untyped;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Boolean supportsUpdate(DeserializationConfig config) {
/*  235 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  241 */     switch (p.currentTokenId()) {
/*      */ 
/*      */       
/*      */       case 1:
/*      */       case 2:
/*      */       case 5:
/*  247 */         if (this._mapDeserializer != null) {
/*  248 */           return this._mapDeserializer.deserialize(p, ctxt);
/*      */         }
/*  250 */         return mapObject(p, ctxt);
/*      */       case 3:
/*  252 */         if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
/*  253 */           return mapArrayToArray(p, ctxt);
/*      */         }
/*  255 */         if (this._listDeserializer != null) {
/*  256 */           return this._listDeserializer.deserialize(p, ctxt);
/*      */         }
/*  258 */         return mapArray(p, ctxt);
/*      */       case 12:
/*  260 */         return p.getEmbeddedObject();
/*      */       case 6:
/*  262 */         if (this._stringDeserializer != null) {
/*  263 */           return this._stringDeserializer.deserialize(p, ctxt);
/*      */         }
/*  265 */         return p.getText();
/*      */       
/*      */       case 7:
/*  268 */         if (this._numberDeserializer != null) {
/*  269 */           return this._numberDeserializer.deserialize(p, ctxt);
/*      */         }
/*      */ 
/*      */         
/*  273 */         if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
/*  274 */           return _coerceIntegral(p, ctxt);
/*      */         }
/*  276 */         return p.getNumberValue();
/*      */       
/*      */       case 8:
/*  279 */         if (this._numberDeserializer != null) {
/*  280 */           return this._numberDeserializer.deserialize(p, ctxt);
/*      */         }
/*      */         
/*  283 */         if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/*  284 */           return p.getDecimalValue();
/*      */         }
/*      */         
/*  287 */         return p.getNumberValue();
/*      */       
/*      */       case 9:
/*  290 */         return Boolean.TRUE;
/*      */       case 10:
/*  292 */         return Boolean.FALSE;
/*      */       
/*      */       case 11:
/*  295 */         return null;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  300 */     return ctxt.handleUnexpectedToken(Object.class, p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/*  307 */     switch (p.currentTokenId()) {
/*      */ 
/*      */       
/*      */       case 1:
/*      */       case 3:
/*      */       case 5:
/*  313 */         return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*      */       
/*      */       case 12:
/*  316 */         return p.getEmbeddedObject();
/*      */ 
/*      */ 
/*      */       
/*      */       case 6:
/*  321 */         if (this._stringDeserializer != null) {
/*  322 */           return this._stringDeserializer.deserialize(p, ctxt);
/*      */         }
/*  324 */         return p.getText();
/*      */       
/*      */       case 7:
/*  327 */         if (this._numberDeserializer != null) {
/*  328 */           return this._numberDeserializer.deserialize(p, ctxt);
/*      */         }
/*      */         
/*  331 */         if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
/*  332 */           return _coerceIntegral(p, ctxt);
/*      */         }
/*  334 */         return p.getNumberValue();
/*      */       
/*      */       case 8:
/*  337 */         if (this._numberDeserializer != null) {
/*  338 */           return this._numberDeserializer.deserialize(p, ctxt);
/*      */         }
/*  340 */         if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/*  341 */           return p.getDecimalValue();
/*      */         }
/*  343 */         return p.getNumberValue();
/*      */       
/*      */       case 9:
/*  346 */         return Boolean.TRUE;
/*      */       case 10:
/*  348 */         return Boolean.FALSE;
/*      */       
/*      */       case 11:
/*  351 */         return null;
/*      */     } 
/*      */     
/*  354 */     return ctxt.handleUnexpectedToken(Object.class, p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
/*  362 */     if (this._nonMerging) {
/*  363 */       return deserialize(p, ctxt);
/*      */     }
/*      */     
/*  366 */     switch (p.currentTokenId()) {
/*      */ 
/*      */       
/*      */       case 1:
/*      */       case 2:
/*      */       case 5:
/*  372 */         if (this._mapDeserializer != null) {
/*  373 */           return this._mapDeserializer.deserialize(p, ctxt, intoValue);
/*      */         }
/*  375 */         if (intoValue instanceof Map) {
/*  376 */           return mapObject(p, ctxt, (Map<Object, Object>)intoValue);
/*      */         }
/*  378 */         return mapObject(p, ctxt);
/*      */       case 3:
/*  380 */         if (this._listDeserializer != null) {
/*  381 */           return this._listDeserializer.deserialize(p, ctxt, intoValue);
/*      */         }
/*  383 */         if (intoValue instanceof Collection) {
/*  384 */           return mapArray(p, ctxt, (Collection<Object>)intoValue);
/*      */         }
/*  386 */         if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
/*  387 */           return mapArrayToArray(p, ctxt);
/*      */         }
/*  389 */         return mapArray(p, ctxt);
/*      */       case 12:
/*  391 */         return p.getEmbeddedObject();
/*      */       case 6:
/*  393 */         if (this._stringDeserializer != null) {
/*  394 */           return this._stringDeserializer.deserialize(p, ctxt, intoValue);
/*      */         }
/*  396 */         return p.getText();
/*      */       
/*      */       case 7:
/*  399 */         if (this._numberDeserializer != null) {
/*  400 */           return this._numberDeserializer.deserialize(p, ctxt, intoValue);
/*      */         }
/*  402 */         if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
/*  403 */           return _coerceIntegral(p, ctxt);
/*      */         }
/*  405 */         return p.getNumberValue();
/*      */       
/*      */       case 8:
/*  408 */         if (this._numberDeserializer != null) {
/*  409 */           return this._numberDeserializer.deserialize(p, ctxt, intoValue);
/*      */         }
/*  411 */         if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/*  412 */           return p.getDecimalValue();
/*      */         }
/*  414 */         return p.getNumberValue();
/*      */       case 9:
/*  416 */         return Boolean.TRUE;
/*      */       case 10:
/*  418 */         return Boolean.FALSE;
/*      */ 
/*      */       
/*      */       case 11:
/*  422 */         return null;
/*      */     } 
/*      */ 
/*      */     
/*  426 */     return deserialize(p, ctxt);
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
/*      */   protected Object mapArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  441 */     if (p.nextToken() == JsonToken.END_ARRAY) {
/*  442 */       return new ArrayList(2);
/*      */     }
/*  444 */     Object value = deserialize(p, ctxt);
/*  445 */     if (p.nextToken() == JsonToken.END_ARRAY) {
/*  446 */       ArrayList<Object> l = new ArrayList(2);
/*  447 */       l.add(value);
/*  448 */       return l;
/*      */     } 
/*  450 */     Object value2 = deserialize(p, ctxt);
/*  451 */     if (p.nextToken() == JsonToken.END_ARRAY) {
/*  452 */       ArrayList<Object> l = new ArrayList(2);
/*  453 */       l.add(value);
/*  454 */       l.add(value2);
/*  455 */       return l;
/*      */     } 
/*  457 */     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/*  458 */     Object[] values = buffer.resetAndStart();
/*  459 */     int ptr = 0;
/*  460 */     values[ptr++] = value;
/*  461 */     values[ptr++] = value2;
/*  462 */     int totalSize = ptr;
/*      */     do {
/*  464 */       value = deserialize(p, ctxt);
/*  465 */       totalSize++;
/*  466 */       if (ptr >= values.length) {
/*  467 */         values = buffer.appendCompletedChunk(values);
/*  468 */         ptr = 0;
/*      */       } 
/*  470 */       values[ptr++] = value;
/*  471 */     } while (p.nextToken() != JsonToken.END_ARRAY);
/*      */     
/*  473 */     ArrayList<Object> result = new ArrayList(totalSize);
/*  474 */     buffer.completeAndClearBuffer(values, ptr, result);
/*  475 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object mapArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
/*  483 */     while (p.nextToken() != JsonToken.END_ARRAY) {
/*  484 */       result.add(deserialize(p, ctxt));
/*      */     }
/*  486 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/*      */     String key1;
/*  495 */     JsonToken t = p.currentToken();
/*      */     
/*  497 */     if (t == JsonToken.START_OBJECT) {
/*  498 */       key1 = p.nextFieldName();
/*  499 */     } else if (t == JsonToken.FIELD_NAME) {
/*  500 */       key1 = p.currentName();
/*      */     } else {
/*  502 */       if (t != JsonToken.END_OBJECT) {
/*  503 */         return ctxt.handleUnexpectedToken(handledType(), p);
/*      */       }
/*  505 */       key1 = null;
/*      */     } 
/*  507 */     if (key1 == null)
/*      */     {
/*  509 */       return new LinkedHashMap<>(2);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  514 */     p.nextToken();
/*  515 */     Object value1 = deserialize(p, ctxt);
/*  516 */     String key2 = p.nextFieldName();
/*  517 */     if (key2 == null) {
/*      */       
/*  519 */       LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>(2);
/*  520 */       linkedHashMap.put(key1, value1);
/*  521 */       return linkedHashMap;
/*      */     } 
/*  523 */     p.nextToken();
/*  524 */     Object value2 = deserialize(p, ctxt);
/*      */     
/*  526 */     String key = p.nextFieldName();
/*  527 */     if (key == null) {
/*  528 */       LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>(4);
/*  529 */       linkedHashMap.put(key1, value1);
/*  530 */       if (linkedHashMap.put(key2, value2) != null)
/*      */       {
/*  532 */         return _mapObjectWithDups(p, ctxt, linkedHashMap, key1, value1, value2, key);
/*      */       }
/*  534 */       return linkedHashMap;
/*      */     } 
/*      */     
/*  537 */     LinkedHashMap<String, Object> result = new LinkedHashMap<>();
/*  538 */     result.put(key1, value1);
/*  539 */     if (result.put(key2, value2) != null)
/*      */     {
/*  541 */       return _mapObjectWithDups(p, ctxt, result, key1, value1, value2, key);
/*      */     }
/*      */     
/*      */     while (true) {
/*  545 */       p.nextToken();
/*  546 */       Object newValue = deserialize(p, ctxt);
/*  547 */       Object oldValue = result.put(key, newValue);
/*  548 */       if (oldValue != null) {
/*  549 */         return _mapObjectWithDups(p, ctxt, result, key, oldValue, newValue, p
/*  550 */             .nextFieldName());
/*      */       }
/*  552 */       if ((key = p.nextFieldName()) == null) {
/*  553 */         return result;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object _mapObjectWithDups(JsonParser p, DeserializationContext ctxt, Map<String, Object> result, String key, Object oldValue, Object newValue, String nextKey) throws IOException {
/*  561 */     boolean squashDups = ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES);
/*      */     
/*  563 */     if (squashDups) {
/*  564 */       _squashDups(result, key, oldValue, newValue);
/*      */     }
/*      */     
/*  567 */     while (nextKey != null) {
/*  568 */       p.nextToken();
/*  569 */       newValue = deserialize(p, ctxt);
/*  570 */       oldValue = result.put(nextKey, newValue);
/*  571 */       if (oldValue != null && squashDups) {
/*  572 */         _squashDups(result, key, oldValue, newValue);
/*      */       }
/*  574 */       nextKey = p.nextFieldName();
/*      */     } 
/*      */     
/*  577 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _squashDups(Map<String, Object> result, String key, Object oldValue, Object newValue) {
/*  584 */     if (oldValue instanceof List) {
/*  585 */       ((List<Object>)oldValue).add(newValue);
/*  586 */       result.put(key, oldValue);
/*      */     } else {
/*  588 */       ArrayList<Object> l = new ArrayList();
/*  589 */       l.add(oldValue);
/*  590 */       l.add(newValue);
/*  591 */       result.put(key, l);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object[] mapArrayToArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  601 */     if (p.nextToken() == JsonToken.END_ARRAY) {
/*  602 */       return NO_OBJECTS;
/*      */     }
/*  604 */     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/*  605 */     Object[] values = buffer.resetAndStart();
/*  606 */     int ptr = 0;
/*      */     while (true) {
/*  608 */       Object value = deserialize(p, ctxt);
/*  609 */       if (ptr >= values.length) {
/*  610 */         values = buffer.appendCompletedChunk(values);
/*  611 */         ptr = 0;
/*      */       } 
/*  613 */       values[ptr++] = value;
/*  614 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/*  615 */         return buffer.completeAndClearBuffer(values, ptr);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   protected Object mapObject(JsonParser p, DeserializationContext ctxt, Map<Object, Object> m) throws IOException {
/*  621 */     JsonToken t = p.currentToken();
/*  622 */     if (t == JsonToken.START_OBJECT) {
/*  623 */       t = p.nextToken();
/*      */     }
/*  625 */     if (t == JsonToken.END_OBJECT) {
/*  626 */       return m;
/*      */     }
/*      */     
/*  629 */     String key = p.currentName(); while (true) {
/*      */       Object newV;
/*  631 */       p.nextToken();
/*      */       
/*  633 */       Object old = m.get(key);
/*      */ 
/*      */       
/*  636 */       if (old != null) {
/*  637 */         newV = deserialize(p, ctxt, old);
/*      */       } else {
/*  639 */         newV = deserialize(p, ctxt);
/*      */       } 
/*  641 */       if (newV != old) {
/*  642 */         m.put(key, newV);
/*      */       }
/*  644 */       if ((key = p.nextFieldName()) == null) {
/*  645 */         return m;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static class Vanilla
/*      */     extends StdDeserializer<Object>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static final int MAX_DEPTH = 1000;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  668 */     public static final Vanilla std = new Vanilla();
/*      */     
/*      */     protected final boolean _nonMerging;
/*      */     
/*      */     public Vanilla() {
/*  673 */       this(false);
/*      */     }
/*      */     protected Vanilla(boolean nonMerging) {
/*  676 */       super(Object.class);
/*  677 */       this._nonMerging = nonMerging;
/*      */     }
/*      */     
/*      */     public static Vanilla instance(boolean nonMerging) {
/*  681 */       if (nonMerging) {
/*  682 */         return new Vanilla(true);
/*      */       }
/*  684 */       return std;
/*      */     }
/*      */ 
/*      */     
/*      */     public LogicalType logicalType() {
/*  689 */       return LogicalType.Untyped;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Boolean supportsUpdate(DeserializationConfig config) {
/*  696 */       return this._nonMerging ? Boolean.FALSE : null;
/*      */     }
/*      */ 
/*      */     
/*      */     public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  701 */       return deserialize(p, ctxt, 0);
/*      */     }
/*      */     
/*      */     private Object deserialize(JsonParser p, DeserializationContext ctxt, int depth) throws IOException {
/*      */       JsonToken t;
/*  706 */       switch (p.currentTokenId()) {
/*      */         case 1:
/*  708 */           t = p.nextToken();
/*  709 */           if (t == JsonToken.END_OBJECT) {
/*  710 */             return new LinkedHashMap<>(2);
/*      */           }
/*      */         
/*      */         case 5:
/*  714 */           if (depth > 1000) {
/*  715 */             throw new JsonParseException(p, "JSON is too deeply nested.");
/*      */           }
/*      */           
/*  718 */           return mapObject(p, ctxt, depth);
/*      */         case 3:
/*  720 */           t = p.nextToken();
/*  721 */           if (t == JsonToken.END_ARRAY) {
/*  722 */             if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY))
/*      */             {
/*  724 */               return UntypedObjectDeserializer.NO_OBJECTS;
/*      */             }
/*  726 */             return new ArrayList(2);
/*      */           } 
/*      */ 
/*      */           
/*  730 */           if (depth > 1000) {
/*  731 */             throw new JsonParseException(p, "JSON is too deeply nested.");
/*      */           }
/*      */           
/*  734 */           if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
/*  735 */             return mapArrayToArray(p, ctxt, depth);
/*      */           }
/*  737 */           return mapArray(p, ctxt, depth);
/*      */         case 12:
/*  739 */           return p.getEmbeddedObject();
/*      */         case 6:
/*  741 */           return p.getText();
/*      */         
/*      */         case 7:
/*  744 */           if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
/*  745 */             return _coerceIntegral(p, ctxt);
/*      */           }
/*  747 */           return p.getNumberValue();
/*      */         
/*      */         case 8:
/*  750 */           if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/*  751 */             return p.getDecimalValue();
/*      */           }
/*  753 */           return p.getNumberValue();
/*      */         
/*      */         case 9:
/*  756 */           return Boolean.TRUE;
/*      */         case 10:
/*  758 */           return Boolean.FALSE;
/*      */ 
/*      */ 
/*      */         
/*      */         case 2:
/*  763 */           return new LinkedHashMap<>(2);
/*      */         
/*      */         case 11:
/*  766 */           return null;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  771 */       return ctxt.handleUnexpectedToken(Object.class, p);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/*  777 */       switch (p.currentTokenId()) {
/*      */         case 1:
/*      */         case 3:
/*      */         case 5:
/*  781 */           return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*      */         
/*      */         case 6:
/*  784 */           return p.getText();
/*      */         
/*      */         case 7:
/*  787 */           if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
/*  788 */             return p.getBigIntegerValue();
/*      */           }
/*  790 */           return p.getNumberValue();
/*      */         
/*      */         case 8:
/*  793 */           if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/*  794 */             return p.getDecimalValue();
/*      */           }
/*  796 */           return p.getNumberValue();
/*      */         
/*      */         case 9:
/*  799 */           return Boolean.TRUE;
/*      */         case 10:
/*  801 */           return Boolean.FALSE;
/*      */         case 12:
/*  803 */           return p.getEmbeddedObject();
/*      */         
/*      */         case 11:
/*  806 */           return null;
/*      */       } 
/*      */       
/*  809 */       return ctxt.handleUnexpectedToken(Object.class, p);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
/*      */       JsonToken t;
/*  817 */       if (this._nonMerging) {
/*  818 */         return deserialize(p, ctxt);
/*      */       }
/*  820 */       switch (p.currentTokenId()) {
/*      */         case 2:
/*      */         case 4:
/*  823 */           return intoValue;
/*      */         
/*      */         case 1:
/*  826 */           t = p.nextToken();
/*  827 */           if (t == JsonToken.END_OBJECT) {
/*  828 */             return intoValue;
/*      */           }
/*      */         
/*      */         case 5:
/*  832 */           if (intoValue instanceof Map) {
/*  833 */             Map<Object, Object> m = (Map<Object, Object>)intoValue;
/*      */             
/*  835 */             String key = p.currentName(); while (true) {
/*      */               Object newV;
/*  837 */               p.nextToken();
/*      */               
/*  839 */               Object old = m.get(key);
/*      */               
/*  841 */               if (old != null) {
/*  842 */                 newV = deserialize(p, ctxt, old);
/*      */               } else {
/*  844 */                 newV = deserialize(p, ctxt);
/*      */               } 
/*  846 */               if (newV != old) {
/*  847 */                 m.put(key, newV);
/*      */               }
/*  849 */               if ((key = p.nextFieldName()) == null)
/*  850 */                 return intoValue; 
/*      */             } 
/*      */           } 
/*      */           break;
/*      */         case 3:
/*  855 */           t = p.nextToken();
/*  856 */           if (t == JsonToken.END_ARRAY) {
/*  857 */             return intoValue;
/*      */           }
/*      */ 
/*      */           
/*  861 */           if (intoValue instanceof Collection) {
/*  862 */             Collection<Object> c = (Collection<Object>)intoValue;
/*      */             
/*      */             while (true) {
/*  865 */               c.add(deserialize(p, ctxt));
/*  866 */               if (p.nextToken() == JsonToken.END_ARRAY) {
/*  867 */                 return intoValue;
/*      */               }
/*      */             } 
/*      */           } 
/*      */           break;
/*      */       } 
/*      */       
/*  874 */       return deserialize(p, ctxt);
/*      */     }
/*      */ 
/*      */     
/*      */     protected Object mapArray(JsonParser p, DeserializationContext ctxt, int depth) throws IOException {
/*  879 */       depth++;
/*  880 */       Object value = deserialize(p, ctxt, depth);
/*  881 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/*  882 */         ArrayList<Object> l = new ArrayList(2);
/*  883 */         l.add(value);
/*  884 */         return l;
/*      */       } 
/*  886 */       Object value2 = deserialize(p, ctxt, depth);
/*  887 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/*  888 */         ArrayList<Object> l = new ArrayList(2);
/*  889 */         l.add(value);
/*  890 */         l.add(value2);
/*  891 */         return l;
/*      */       } 
/*  893 */       ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/*  894 */       Object[] values = buffer.resetAndStart();
/*  895 */       int ptr = 0;
/*  896 */       values[ptr++] = value;
/*  897 */       values[ptr++] = value2;
/*  898 */       int totalSize = ptr;
/*      */       do {
/*  900 */         value = deserialize(p, ctxt, depth);
/*  901 */         totalSize++;
/*  902 */         if (ptr >= values.length) {
/*  903 */           values = buffer.appendCompletedChunk(values);
/*  904 */           ptr = 0;
/*      */         } 
/*  906 */         values[ptr++] = value;
/*  907 */       } while (p.nextToken() != JsonToken.END_ARRAY);
/*      */       
/*  909 */       ArrayList<Object> result = new ArrayList(totalSize);
/*  910 */       buffer.completeAndClearBuffer(values, ptr, result);
/*  911 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Object[] mapArrayToArray(JsonParser p, DeserializationContext ctxt, int depth) throws IOException {
/*  918 */       depth++;
/*  919 */       ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/*  920 */       Object[] values = buffer.resetAndStart();
/*  921 */       int ptr = 0;
/*      */       while (true) {
/*  923 */         Object value = deserialize(p, ctxt, depth);
/*  924 */         if (ptr >= values.length) {
/*  925 */           values = buffer.appendCompletedChunk(values);
/*  926 */           ptr = 0;
/*      */         } 
/*  928 */         values[ptr++] = value;
/*  929 */         if (p.nextToken() == JsonToken.END_ARRAY) {
/*  930 */           return buffer.completeAndClearBuffer(values, ptr);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Object mapObject(JsonParser p, DeserializationContext ctxt, int depth) throws IOException {
/*  938 */       depth++;
/*      */ 
/*      */       
/*  941 */       String key1 = p.currentName();
/*  942 */       p.nextToken();
/*  943 */       Object value1 = deserialize(p, ctxt, depth);
/*      */       
/*  945 */       String key2 = p.nextFieldName();
/*  946 */       if (key2 == null) {
/*  947 */         LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>(2);
/*  948 */         linkedHashMap.put(key1, value1);
/*  949 */         return linkedHashMap;
/*      */       } 
/*  951 */       p.nextToken();
/*  952 */       Object value2 = deserialize(p, ctxt, depth);
/*      */       
/*  954 */       String key = p.nextFieldName();
/*  955 */       if (key == null) {
/*  956 */         LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>(4);
/*  957 */         linkedHashMap.put(key1, value1);
/*  958 */         if (linkedHashMap.put(key2, value2) != null)
/*      */         {
/*  960 */           return _mapObjectWithDups(p, ctxt, linkedHashMap, key1, value1, value2, key);
/*      */         }
/*  962 */         return linkedHashMap;
/*      */       } 
/*      */       
/*  965 */       LinkedHashMap<String, Object> result = new LinkedHashMap<>();
/*  966 */       result.put(key1, value1);
/*  967 */       if (result.put(key2, value2) != null)
/*      */       {
/*  969 */         return _mapObjectWithDups(p, ctxt, result, key1, value1, value2, key);
/*      */       }
/*      */       
/*      */       while (true) {
/*  973 */         p.nextToken();
/*  974 */         Object newValue = deserialize(p, ctxt, depth);
/*  975 */         Object oldValue = result.put(key, newValue);
/*  976 */         if (oldValue != null) {
/*  977 */           return _mapObjectWithDups(p, ctxt, result, key, oldValue, newValue, p
/*  978 */               .nextFieldName());
/*      */         }
/*  980 */         if ((key = p.nextFieldName()) == null) {
/*  981 */           return result;
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Object _mapObjectWithDups(JsonParser p, DeserializationContext ctxt, Map<String, Object> result, String initialKey, Object oldValue, Object newValue, String nextKey) throws IOException {
/*  990 */       boolean squashDups = ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES);
/*      */       
/*  992 */       if (squashDups) {
/*  993 */         _squashDups(result, initialKey, oldValue, newValue);
/*      */       }
/*      */       
/*  996 */       while (nextKey != null) {
/*  997 */         p.nextToken();
/*  998 */         newValue = deserialize(p, ctxt);
/*  999 */         oldValue = result.put(nextKey, newValue);
/* 1000 */         if (oldValue != null && squashDups) {
/* 1001 */           _squashDups(result, nextKey, oldValue, newValue);
/*      */         }
/* 1003 */         nextKey = p.nextFieldName();
/*      */       } 
/*      */       
/* 1006 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void _squashDups(Map<String, Object> result, String key, Object oldValue, Object newValue) {
/* 1014 */       if (oldValue instanceof List) {
/* 1015 */         ((List<Object>)oldValue).add(newValue);
/* 1016 */         result.put(key, oldValue);
/*      */       } else {
/* 1018 */         ArrayList<Object> l = new ArrayList();
/* 1019 */         l.add(oldValue);
/* 1020 */         l.add(newValue);
/* 1021 */         result.put(key, l);
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/UntypedObjectDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */