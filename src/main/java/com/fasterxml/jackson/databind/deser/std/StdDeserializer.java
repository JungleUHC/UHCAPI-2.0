/*      */ package com.fasterxml.jackson.databind.deser.std;
/*      */ 
/*      */ import com.fasterxml.jackson.annotation.JsonFormat;
/*      */ import com.fasterxml.jackson.annotation.Nulls;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.StreamReadCapability;
/*      */ import com.fasterxml.jackson.core.exc.StreamReadException;
/*      */ import com.fasterxml.jackson.core.io.NumberInput;
/*      */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*      */ import com.fasterxml.jackson.databind.BeanProperty;
/*      */ import com.fasterxml.jackson.databind.DeserializationContext;
/*      */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*      */ import com.fasterxml.jackson.databind.JsonMappingException;
/*      */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*      */ import com.fasterxml.jackson.databind.MapperFeature;
/*      */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
/*      */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*      */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*      */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*      */ import com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider;
/*      */ import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
/*      */ import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
/*      */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.fasterxml.jackson.databind.type.LogicalType;
/*      */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import com.fasterxml.jackson.databind.util.Converter;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.Map;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class StdDeserializer<T>
/*      */   extends JsonDeserializer<T>
/*      */   implements Serializable, ValueInstantiator.Gettable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*   51 */   protected static final int F_MASK_INT_COERCIONS = DeserializationFeature.USE_BIG_INTEGER_FOR_INTS
/*   52 */     .getMask() | DeserializationFeature.USE_LONG_FOR_INTS
/*   53 */     .getMask();
/*      */   
/*      */   @Deprecated
/*   56 */   protected static final int F_MASK_ACCEPT_ARRAYS = DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS
/*   57 */     .getMask() | DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT
/*   58 */     .getMask();
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Class<?> _valueClass;
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JavaType _valueType;
/*      */ 
/*      */ 
/*      */   
/*      */   protected StdDeserializer(Class<?> vc) {
/*   71 */     this._valueClass = vc;
/*   72 */     this._valueType = null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected StdDeserializer(JavaType valueType) {
/*   77 */     this._valueClass = (valueType == null) ? Object.class : valueType.getRawClass();
/*   78 */     this._valueType = valueType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected StdDeserializer(StdDeserializer<?> src) {
/*   88 */     this._valueClass = src._valueClass;
/*   89 */     this._valueType = src._valueType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Class<?> handledType() {
/*   99 */     return this._valueClass;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public final Class<?> getValueClass() {
/*  111 */     return this._valueClass;
/*      */   }
/*      */ 
/*      */   
/*      */   public JavaType getValueType() {
/*  116 */     return this._valueType;
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
/*      */   public JavaType getValueType(DeserializationContext ctxt) {
/*  132 */     if (this._valueType != null) {
/*  133 */       return this._valueType;
/*      */     }
/*  135 */     return ctxt.constructType(this._valueClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ValueInstantiator getValueInstantiator() {
/*  142 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isDefaultDeserializer(JsonDeserializer<?> deserializer) {
/*  151 */     return ClassUtil.isJacksonStdImpl(deserializer);
/*      */   }
/*      */   
/*      */   protected boolean isDefaultKeyDeserializer(KeyDeserializer keyDeser) {
/*  155 */     return ClassUtil.isJacksonStdImpl(keyDeser);
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
/*      */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/*  172 */     return typeDeserializer.deserializeTypedFromAny(p, ctxt);
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
/*      */ 
/*      */   
/*      */   protected T _deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  200 */     CoercionAction act = _findCoercionFromEmptyArray(ctxt);
/*  201 */     boolean unwrap = ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
/*      */     
/*  203 */     if (unwrap || act != CoercionAction.Fail) {
/*  204 */       JsonToken t = p.nextToken();
/*  205 */       if (t == JsonToken.END_ARRAY) {
/*  206 */         switch (act) {
/*      */           case AsEmpty:
/*  208 */             return (T)getEmptyValue(ctxt);
/*      */           case AsNull:
/*      */           case TryConvert:
/*  211 */             return (T)getNullValue(ctxt);
/*      */         } 
/*      */       
/*  214 */       } else if (unwrap) {
/*  215 */         T parsed = _deserializeWrappedValue(p, ctxt);
/*  216 */         if (p.nextToken() != JsonToken.END_ARRAY) {
/*  217 */           handleMissingEndArrayForSingle(p, ctxt);
/*      */         }
/*  219 */         return parsed;
/*      */       } 
/*      */     } 
/*  222 */     return (T)ctxt.handleUnexpectedToken(getValueType(ctxt), JsonToken.START_ARRAY, p, null, new Object[0]);
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
/*      */   @Deprecated
/*      */   protected T _deserializeFromEmpty(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  238 */     if (p.hasToken(JsonToken.START_ARRAY) && 
/*  239 */       ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
/*  240 */       JsonToken t = p.nextToken();
/*  241 */       if (t == JsonToken.END_ARRAY) {
/*  242 */         return null;
/*      */       }
/*  244 */       return (T)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */     } 
/*      */     
/*  247 */     return (T)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
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
/*      */   protected T _deserializeFromString(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  260 */     ValueInstantiator inst = getValueInstantiator();
/*  261 */     Class<?> rawTargetType = handledType();
/*  262 */     String value = p.getValueAsString();
/*      */     
/*  264 */     if (inst != null && inst.canCreateFromString()) {
/*  265 */       return (T)inst.createFromString(ctxt, value);
/*      */     }
/*  267 */     if (value.isEmpty()) {
/*  268 */       CoercionAction act = ctxt.findCoercionAction(logicalType(), rawTargetType, CoercionInputShape.EmptyString);
/*      */       
/*  270 */       return (T)_deserializeFromEmptyString(p, ctxt, act, rawTargetType, "empty String (\"\")");
/*      */     } 
/*      */     
/*  273 */     if (_isBlank(value)) {
/*  274 */       CoercionAction act = ctxt.findCoercionFromBlankString(logicalType(), rawTargetType, CoercionAction.Fail);
/*      */       
/*  276 */       return (T)_deserializeFromEmptyString(p, ctxt, act, rawTargetType, "blank String (all whitespace)");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  283 */     if (inst != null) {
/*  284 */       value = value.trim();
/*  285 */       if (inst.canCreateFromInt() && 
/*  286 */         ctxt.findCoercionAction(LogicalType.Integer, Integer.class, CoercionInputShape.String) == CoercionAction.TryConvert)
/*      */       {
/*  288 */         return (T)inst.createFromInt(ctxt, _parseIntPrimitive(ctxt, value));
/*      */       }
/*      */       
/*  291 */       if (inst.canCreateFromLong() && 
/*  292 */         ctxt.findCoercionAction(LogicalType.Integer, Long.class, CoercionInputShape.String) == CoercionAction.TryConvert)
/*      */       {
/*  294 */         return (T)inst.createFromLong(ctxt, _parseLongPrimitive(ctxt, value));
/*      */       }
/*      */       
/*  297 */       if (inst.canCreateFromBoolean())
/*      */       {
/*  299 */         if (ctxt.findCoercionAction(LogicalType.Boolean, Boolean.class, CoercionInputShape.String) == CoercionAction.TryConvert) {
/*      */           
/*  301 */           String str = value.trim();
/*  302 */           if ("true".equals(str)) {
/*  303 */             return (T)inst.createFromBoolean(ctxt, true);
/*      */           }
/*  305 */           if ("false".equals(str)) {
/*  306 */             return (T)inst.createFromBoolean(ctxt, false);
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*  311 */     return (T)ctxt.handleMissingInstantiator(rawTargetType, inst, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", new Object[] { value });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object _deserializeFromEmptyString(JsonParser p, DeserializationContext ctxt, CoercionAction act, Class<?> rawTargetType, String desc) throws IOException {
/*  320 */     switch (act) {
/*      */       case AsEmpty:
/*  322 */         return getEmptyValue(ctxt);
/*      */       
/*      */       case Fail:
/*  325 */         _checkCoercionFail(ctxt, act, rawTargetType, "", "empty String (\"\")");
/*      */         break;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  331 */     return null;
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
/*      */ 
/*      */   
/*      */   protected T _deserializeWrappedValue(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  359 */     if (p.hasToken(JsonToken.START_ARRAY)) {
/*  360 */       String msg = String.format("Cannot deserialize instance of %s out of %s token: nested Arrays not allowed with %s", new Object[] {
/*      */             
/*  362 */             ClassUtil.nameOf(this._valueClass), JsonToken.START_ARRAY, "DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS"
/*      */           });
/*      */       
/*  365 */       T result = (T)ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p, msg, new Object[0]);
/*  366 */       return result;
/*      */     } 
/*  368 */     return (T)deserialize(p, ctxt);
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
/*      */   @Deprecated
/*      */   protected final boolean _parseBooleanPrimitive(DeserializationContext ctxt, JsonParser p, Class<?> targetType) throws IOException {
/*  382 */     return _parseBooleanPrimitive(p, ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _parseBooleanPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  393 */     switch (p.currentTokenId()) {
/*      */       case 6:
/*  395 */         text = p.getText();
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case 7:
/*  401 */         return Boolean.TRUE.equals(_coerceBooleanFromInt(p, ctxt, boolean.class));
/*      */       case 9:
/*  403 */         return true;
/*      */       case 10:
/*  405 */         return false;
/*      */       case 11:
/*  407 */         _verifyNullForPrimitive(ctxt);
/*  408 */         return false;
/*      */       
/*      */       case 1:
/*  411 */         text = ctxt.extractScalarFromObject(p, this, boolean.class);
/*      */         break;
/*      */       
/*      */       case 3:
/*  415 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*  416 */           p.nextToken();
/*  417 */           boolean parsed = _parseBooleanPrimitive(p, ctxt);
/*  418 */           _verifyEndArrayForSingle(p, ctxt);
/*  419 */           return parsed;
/*      */         } 
/*      */       
/*      */       default:
/*  423 */         return ((Boolean)ctxt.handleUnexpectedToken(boolean.class, p)).booleanValue();
/*      */     } 
/*      */     
/*  426 */     CoercionAction act = _checkFromStringCoercion(ctxt, text, LogicalType.Boolean, boolean.class);
/*      */     
/*  428 */     if (act == CoercionAction.AsNull) {
/*  429 */       _verifyNullForPrimitive(ctxt);
/*  430 */       return false;
/*      */     } 
/*  432 */     if (act == CoercionAction.AsEmpty) {
/*  433 */       return false;
/*      */     }
/*  435 */     String text = text.trim();
/*  436 */     int len = text.length();
/*      */ 
/*      */ 
/*      */     
/*  440 */     if (len == 4) {
/*  441 */       if (_isTrue(text)) {
/*  442 */         return true;
/*      */       }
/*  444 */     } else if (len == 5 && 
/*  445 */       _isFalse(text)) {
/*  446 */       return false;
/*      */     } 
/*      */     
/*  449 */     if (_hasTextualNull(text)) {
/*  450 */       _verifyNullForPrimitiveCoercion(ctxt, text);
/*  451 */       return false;
/*      */     } 
/*  453 */     Boolean b = (Boolean)ctxt.handleWeirdStringValue(boolean.class, text, "only \"true\"/\"True\"/\"TRUE\" or \"false\"/\"False\"/\"FALSE\" recognized", new Object[0]);
/*      */     
/*  455 */     return Boolean.TRUE.equals(b);
/*      */   }
/*      */ 
/*      */   
/*      */   protected boolean _isTrue(String text) {
/*  460 */     char c = text.charAt(0);
/*  461 */     if (c == 't') {
/*  462 */       return "true".equals(text);
/*      */     }
/*  464 */     if (c == 'T') {
/*  465 */       return ("TRUE".equals(text) || "True".equals(text));
/*      */     }
/*  467 */     return false;
/*      */   }
/*      */   
/*      */   protected boolean _isFalse(String text) {
/*  471 */     char c = text.charAt(0);
/*  472 */     if (c == 'f') {
/*  473 */       return "false".equals(text);
/*      */     }
/*  475 */     if (c == 'F') {
/*  476 */       return ("FALSE".equals(text) || "False".equals(text));
/*      */     }
/*  478 */     return false;
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
/*      */   protected final Boolean _parseBoolean(JsonParser p, DeserializationContext ctxt, Class<?> targetType) throws IOException {
/*  504 */     switch (p.currentTokenId()) {
/*      */       case 6:
/*  506 */         text = p.getText();
/*      */         break;
/*      */       
/*      */       case 7:
/*  510 */         return _coerceBooleanFromInt(p, ctxt, targetType);
/*      */       case 9:
/*  512 */         return Boolean.valueOf(true);
/*      */       case 10:
/*  514 */         return Boolean.valueOf(false);
/*      */       case 11:
/*  516 */         return null;
/*      */       
/*      */       case 1:
/*  519 */         text = ctxt.extractScalarFromObject(p, this, targetType);
/*      */         break;
/*      */       case 3:
/*  522 */         return (Boolean)_deserializeFromArray(p, ctxt);
/*      */       default:
/*  524 */         return (Boolean)ctxt.handleUnexpectedToken(targetType, p);
/*      */     } 
/*      */     
/*  527 */     CoercionAction act = _checkFromStringCoercion(ctxt, text, LogicalType.Boolean, targetType);
/*      */     
/*  529 */     if (act == CoercionAction.AsNull) {
/*  530 */       return null;
/*      */     }
/*  532 */     if (act == CoercionAction.AsEmpty) {
/*  533 */       return Boolean.valueOf(false);
/*      */     }
/*  535 */     String text = text.trim();
/*  536 */     int len = text.length();
/*      */ 
/*      */ 
/*      */     
/*  540 */     if (len == 4) {
/*  541 */       if (_isTrue(text)) {
/*  542 */         return Boolean.valueOf(true);
/*      */       }
/*  544 */     } else if (len == 5 && 
/*  545 */       _isFalse(text)) {
/*  546 */       return Boolean.valueOf(false);
/*      */     } 
/*      */     
/*  549 */     if (_checkTextualNull(ctxt, text)) {
/*  550 */       return null;
/*      */     }
/*  552 */     return (Boolean)ctxt.handleWeirdStringValue(targetType, text, "only \"true\" or \"false\" recognized", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final byte _parseBytePrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
/*      */     int value;
/*  560 */     switch (p.currentTokenId()) {
/*      */       case 6:
/*  562 */         text = p.getText();
/*      */         break;
/*      */       case 8:
/*  565 */         act = _checkFloatToIntCoercion(p, ctxt, byte.class);
/*  566 */         if (act == CoercionAction.AsNull) {
/*  567 */           return 0;
/*      */         }
/*  569 */         if (act == CoercionAction.AsEmpty) {
/*  570 */           return 0;
/*      */         }
/*  572 */         return p.getByteValue();
/*      */       case 7:
/*  574 */         return p.getByteValue();
/*      */       case 11:
/*  576 */         _verifyNullForPrimitive(ctxt);
/*  577 */         return 0;
/*      */       
/*      */       case 1:
/*  580 */         text = ctxt.extractScalarFromObject(p, this, byte.class);
/*      */         break;
/*      */       
/*      */       case 3:
/*  584 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*  585 */           p.nextToken();
/*  586 */           byte parsed = _parseBytePrimitive(p, ctxt);
/*  587 */           _verifyEndArrayForSingle(p, ctxt);
/*  588 */           return parsed;
/*      */         } 
/*      */       
/*      */       default:
/*  592 */         return ((Byte)ctxt.handleUnexpectedToken(ctxt.constructType(byte.class), p)).byteValue();
/*      */     } 
/*      */ 
/*      */     
/*  596 */     CoercionAction act = _checkFromStringCoercion(ctxt, text, LogicalType.Integer, byte.class);
/*      */     
/*  598 */     if (act == CoercionAction.AsNull) {
/*      */       
/*  600 */       _verifyNullForPrimitive(ctxt);
/*  601 */       return 0;
/*      */     } 
/*  603 */     if (act == CoercionAction.AsEmpty) {
/*  604 */       return 0;
/*      */     }
/*  606 */     String text = text.trim();
/*  607 */     if (_hasTextualNull(text)) {
/*  608 */       _verifyNullForPrimitiveCoercion(ctxt, text);
/*  609 */       return 0;
/*      */     } 
/*      */     
/*      */     try {
/*  613 */       value = NumberInput.parseInt(text);
/*  614 */     } catch (IllegalArgumentException iae) {
/*  615 */       return ((Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid `byte` value", new Object[0])).byteValue();
/*      */     } 
/*      */ 
/*      */     
/*  619 */     if (_byteOverflow(value)) {
/*  620 */       return ((Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 8-bit value", new Object[0])).byteValue();
/*      */     }
/*      */     
/*  623 */     return (byte)value;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final short _parseShortPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
/*      */     int value;
/*  630 */     switch (p.currentTokenId()) {
/*      */       case 6:
/*  632 */         text = p.getText();
/*      */         break;
/*      */       case 8:
/*  635 */         act = _checkFloatToIntCoercion(p, ctxt, short.class);
/*  636 */         if (act == CoercionAction.AsNull) {
/*  637 */           return 0;
/*      */         }
/*  639 */         if (act == CoercionAction.AsEmpty) {
/*  640 */           return 0;
/*      */         }
/*  642 */         return p.getShortValue();
/*      */       case 7:
/*  644 */         return p.getShortValue();
/*      */       case 11:
/*  646 */         _verifyNullForPrimitive(ctxt);
/*  647 */         return 0;
/*      */       
/*      */       case 1:
/*  650 */         text = ctxt.extractScalarFromObject(p, this, short.class);
/*      */         break;
/*      */       
/*      */       case 3:
/*  654 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*  655 */           p.nextToken();
/*  656 */           short parsed = _parseShortPrimitive(p, ctxt);
/*  657 */           _verifyEndArrayForSingle(p, ctxt);
/*  658 */           return parsed;
/*      */         } 
/*      */       
/*      */       default:
/*  662 */         return ((Short)ctxt.handleUnexpectedToken(ctxt.constructType(short.class), p)).shortValue();
/*      */     } 
/*      */     
/*  665 */     CoercionAction act = _checkFromStringCoercion(ctxt, text, LogicalType.Integer, short.class);
/*      */     
/*  667 */     if (act == CoercionAction.AsNull) {
/*      */       
/*  669 */       _verifyNullForPrimitive(ctxt);
/*  670 */       return 0;
/*      */     } 
/*  672 */     if (act == CoercionAction.AsEmpty) {
/*  673 */       return 0;
/*      */     }
/*  675 */     String text = text.trim();
/*  676 */     if (_hasTextualNull(text)) {
/*  677 */       _verifyNullForPrimitiveCoercion(ctxt, text);
/*  678 */       return 0;
/*      */     } 
/*      */     
/*      */     try {
/*  682 */       value = NumberInput.parseInt(text);
/*  683 */     } catch (IllegalArgumentException iae) {
/*  684 */       return ((Short)ctxt.handleWeirdStringValue(short.class, text, "not a valid `short` value", new Object[0])).shortValue();
/*      */     } 
/*      */     
/*  687 */     if (_shortOverflow(value)) {
/*  688 */       return ((Short)ctxt.handleWeirdStringValue(short.class, text, "overflow, value cannot be represented as 16-bit value", new Object[0])).shortValue();
/*      */     }
/*      */     
/*  691 */     return (short)value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _parseIntPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  698 */     switch (p.currentTokenId()) {
/*      */       case 6:
/*  700 */         text = p.getText();
/*      */         break;
/*      */       case 8:
/*  703 */         act = _checkFloatToIntCoercion(p, ctxt, int.class);
/*  704 */         if (act == CoercionAction.AsNull) {
/*  705 */           return 0;
/*      */         }
/*  707 */         if (act == CoercionAction.AsEmpty) {
/*  708 */           return 0;
/*      */         }
/*  710 */         return p.getValueAsInt();
/*      */       case 7:
/*  712 */         return p.getIntValue();
/*      */       case 11:
/*  714 */         _verifyNullForPrimitive(ctxt);
/*  715 */         return 0;
/*      */       
/*      */       case 1:
/*  718 */         text = ctxt.extractScalarFromObject(p, this, int.class);
/*      */         break;
/*      */       case 3:
/*  721 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*  722 */           p.nextToken();
/*  723 */           int parsed = _parseIntPrimitive(p, ctxt);
/*  724 */           _verifyEndArrayForSingle(p, ctxt);
/*  725 */           return parsed;
/*      */         } 
/*      */       
/*      */       default:
/*  729 */         return ((Number)ctxt.handleUnexpectedToken(int.class, p)).intValue();
/*      */     } 
/*      */     
/*  732 */     CoercionAction act = _checkFromStringCoercion(ctxt, text, LogicalType.Integer, int.class);
/*      */     
/*  734 */     if (act == CoercionAction.AsNull) {
/*      */       
/*  736 */       _verifyNullForPrimitive(ctxt);
/*  737 */       return 0;
/*      */     } 
/*  739 */     if (act == CoercionAction.AsEmpty) {
/*  740 */       return 0;
/*      */     }
/*  742 */     String text = text.trim();
/*  743 */     if (_hasTextualNull(text)) {
/*  744 */       _verifyNullForPrimitiveCoercion(ctxt, text);
/*  745 */       return 0;
/*      */     } 
/*  747 */     return _parseIntPrimitive(ctxt, text);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int _parseIntPrimitive(DeserializationContext ctxt, String text) throws IOException {
/*      */     try {
/*  756 */       if (text.length() > 9) {
/*  757 */         long l = Long.parseLong(text);
/*  758 */         if (_intOverflow(l)) {
/*  759 */           Number v = (Number)ctxt.handleWeirdStringValue(int.class, text, "Overflow: numeric value (%s) out of range of int (%d -%d)", new Object[] { text, 
/*      */                 
/*  761 */                 Integer.valueOf(-2147483648), Integer.valueOf(2147483647) });
/*  762 */           return _nonNullNumber(v).intValue();
/*      */         } 
/*  764 */         return (int)l;
/*      */       } 
/*  766 */       return NumberInput.parseInt(text);
/*  767 */     } catch (IllegalArgumentException iae) {
/*  768 */       Number v = (Number)ctxt.handleWeirdStringValue(int.class, text, "not a valid `int` value", new Object[0]);
/*      */       
/*  770 */       return _nonNullNumber(v).intValue();
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
/*      */   protected final Integer _parseInteger(JsonParser p, DeserializationContext ctxt, Class<?> targetType) throws IOException {
/*  782 */     switch (p.currentTokenId()) {
/*      */       case 6:
/*  784 */         text = p.getText();
/*      */         break;
/*      */       case 8:
/*  787 */         act = _checkFloatToIntCoercion(p, ctxt, targetType);
/*  788 */         if (act == CoercionAction.AsNull) {
/*  789 */           return (Integer)getNullValue(ctxt);
/*      */         }
/*  791 */         if (act == CoercionAction.AsEmpty) {
/*  792 */           return (Integer)getEmptyValue(ctxt);
/*      */         }
/*  794 */         return Integer.valueOf(p.getValueAsInt());
/*      */       case 7:
/*  796 */         return Integer.valueOf(p.getIntValue());
/*      */       case 11:
/*  798 */         return (Integer)getNullValue(ctxt);
/*      */       
/*      */       case 1:
/*  801 */         text = ctxt.extractScalarFromObject(p, this, targetType);
/*      */         break;
/*      */       case 3:
/*  804 */         return (Integer)_deserializeFromArray(p, ctxt);
/*      */       default:
/*  806 */         return (Integer)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */     } 
/*      */     
/*  809 */     CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  810 */     if (act == CoercionAction.AsNull) {
/*  811 */       return (Integer)getNullValue(ctxt);
/*      */     }
/*  813 */     if (act == CoercionAction.AsEmpty) {
/*  814 */       return (Integer)getEmptyValue(ctxt);
/*      */     }
/*  816 */     String text = text.trim();
/*  817 */     if (_checkTextualNull(ctxt, text)) {
/*  818 */       return (Integer)getNullValue(ctxt);
/*      */     }
/*  820 */     return Integer.valueOf(_parseIntPrimitive(ctxt, text));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final long _parseLongPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  827 */     switch (p.currentTokenId()) {
/*      */       case 6:
/*  829 */         text = p.getText();
/*      */         break;
/*      */       case 8:
/*  832 */         act = _checkFloatToIntCoercion(p, ctxt, long.class);
/*  833 */         if (act == CoercionAction.AsNull) {
/*  834 */           return 0L;
/*      */         }
/*  836 */         if (act == CoercionAction.AsEmpty) {
/*  837 */           return 0L;
/*      */         }
/*  839 */         return p.getValueAsLong();
/*      */       case 7:
/*  841 */         return p.getLongValue();
/*      */       case 11:
/*  843 */         _verifyNullForPrimitive(ctxt);
/*  844 */         return 0L;
/*      */       
/*      */       case 1:
/*  847 */         text = ctxt.extractScalarFromObject(p, this, long.class);
/*      */         break;
/*      */       case 3:
/*  850 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*  851 */           p.nextToken();
/*  852 */           long parsed = _parseLongPrimitive(p, ctxt);
/*  853 */           _verifyEndArrayForSingle(p, ctxt);
/*  854 */           return parsed;
/*      */         } 
/*      */       
/*      */       default:
/*  858 */         return ((Number)ctxt.handleUnexpectedToken(long.class, p)).longValue();
/*      */     } 
/*      */     
/*  861 */     CoercionAction act = _checkFromStringCoercion(ctxt, text, LogicalType.Integer, long.class);
/*      */     
/*  863 */     if (act == CoercionAction.AsNull) {
/*      */       
/*  865 */       _verifyNullForPrimitive(ctxt);
/*  866 */       return 0L;
/*      */     } 
/*  868 */     if (act == CoercionAction.AsEmpty) {
/*  869 */       return 0L;
/*      */     }
/*  871 */     String text = text.trim();
/*  872 */     if (_hasTextualNull(text)) {
/*  873 */       _verifyNullForPrimitiveCoercion(ctxt, text);
/*  874 */       return 0L;
/*      */     } 
/*  876 */     return _parseLongPrimitive(ctxt, text);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final long _parseLongPrimitive(DeserializationContext ctxt, String text) throws IOException {
/*      */     try {
/*  885 */       return NumberInput.parseLong(text);
/*  886 */     } catch (IllegalArgumentException illegalArgumentException) {
/*      */       
/*  888 */       Number v = (Number)ctxt.handleWeirdStringValue(long.class, text, "not a valid `long` value", new Object[0]);
/*      */       
/*  890 */       return _nonNullNumber(v).longValue();
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
/*      */   protected final Long _parseLong(JsonParser p, DeserializationContext ctxt, Class<?> targetType) throws IOException {
/*  902 */     switch (p.currentTokenId()) {
/*      */       case 6:
/*  904 */         text = p.getText();
/*      */         break;
/*      */       case 8:
/*  907 */         act = _checkFloatToIntCoercion(p, ctxt, targetType);
/*  908 */         if (act == CoercionAction.AsNull) {
/*  909 */           return (Long)getNullValue(ctxt);
/*      */         }
/*  911 */         if (act == CoercionAction.AsEmpty) {
/*  912 */           return (Long)getEmptyValue(ctxt);
/*      */         }
/*  914 */         return Long.valueOf(p.getValueAsLong());
/*      */       case 11:
/*  916 */         return (Long)getNullValue(ctxt);
/*      */       case 7:
/*  918 */         return Long.valueOf(p.getLongValue());
/*      */       
/*      */       case 1:
/*  921 */         text = ctxt.extractScalarFromObject(p, this, targetType);
/*      */         break;
/*      */       case 3:
/*  924 */         return (Long)_deserializeFromArray(p, ctxt);
/*      */       default:
/*  926 */         return (Long)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */     } 
/*      */     
/*  929 */     CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  930 */     if (act == CoercionAction.AsNull) {
/*  931 */       return (Long)getNullValue(ctxt);
/*      */     }
/*  933 */     if (act == CoercionAction.AsEmpty) {
/*  934 */       return (Long)getEmptyValue(ctxt);
/*      */     }
/*  936 */     String text = text.trim();
/*  937 */     if (_checkTextualNull(ctxt, text)) {
/*  938 */       return (Long)getNullValue(ctxt);
/*      */     }
/*      */     
/*  941 */     return Long.valueOf(_parseLongPrimitive(ctxt, text));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final float _parseFloatPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  948 */     switch (p.currentTokenId()) {
/*      */       case 6:
/*  950 */         text = p.getText();
/*      */         break;
/*      */       case 7:
/*      */       case 8:
/*  954 */         return p.getFloatValue();
/*      */       case 11:
/*  956 */         _verifyNullForPrimitive(ctxt);
/*  957 */         return 0.0F;
/*      */       
/*      */       case 1:
/*  960 */         text = ctxt.extractScalarFromObject(p, this, float.class);
/*      */         break;
/*      */       case 3:
/*  963 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*  964 */           p.nextToken();
/*  965 */           float parsed = _parseFloatPrimitive(p, ctxt);
/*  966 */           _verifyEndArrayForSingle(p, ctxt);
/*  967 */           return parsed;
/*      */         } 
/*      */       
/*      */       default:
/*  971 */         return ((Number)ctxt.handleUnexpectedToken(float.class, p)).floatValue();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  978 */     Float nan = _checkFloatSpecialValue(text);
/*  979 */     if (nan != null) {
/*  980 */       return nan.floatValue();
/*      */     }
/*      */ 
/*      */     
/*  984 */     CoercionAction act = _checkFromStringCoercion(ctxt, text, LogicalType.Integer, float.class);
/*      */     
/*  986 */     if (act == CoercionAction.AsNull) {
/*      */       
/*  988 */       _verifyNullForPrimitive(ctxt);
/*  989 */       return 0.0F;
/*      */     } 
/*  991 */     if (act == CoercionAction.AsEmpty) {
/*  992 */       return 0.0F;
/*      */     }
/*  994 */     String text = text.trim();
/*  995 */     if (_hasTextualNull(text)) {
/*  996 */       _verifyNullForPrimitiveCoercion(ctxt, text);
/*  997 */       return 0.0F;
/*      */     } 
/*  999 */     return _parseFloatPrimitive(ctxt, text);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final float _parseFloatPrimitive(DeserializationContext ctxt, String text) throws IOException {
/*      */     try {
/* 1009 */       return Float.parseFloat(text);
/* 1010 */     } catch (IllegalArgumentException illegalArgumentException) {
/* 1011 */       Number v = (Number)ctxt.handleWeirdStringValue(float.class, text, "not a valid `float` value", new Object[0]);
/*      */       
/* 1013 */       return _nonNullNumber(v).floatValue();
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
/*      */   protected Float _checkFloatSpecialValue(String text) {
/* 1030 */     if (!text.isEmpty()) {
/* 1031 */       switch (text.charAt(0)) {
/*      */         case 'I':
/* 1033 */           if (_isPosInf(text)) {
/* 1034 */             return Float.valueOf(Float.POSITIVE_INFINITY);
/*      */           }
/*      */           break;
/*      */         case 'N':
/* 1038 */           if (_isNaN(text)) return Float.valueOf(Float.NaN); 
/*      */           break;
/*      */         case '-':
/* 1041 */           if (_isNegInf(text)) {
/* 1042 */             return Float.valueOf(Float.NEGATIVE_INFINITY);
/*      */           }
/*      */           break;
/*      */       } 
/*      */     
/*      */     }
/* 1048 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final double _parseDoublePrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1055 */     switch (p.currentTokenId()) {
/*      */       case 6:
/* 1057 */         text = p.getText();
/*      */         break;
/*      */       case 7:
/*      */       case 8:
/* 1061 */         return p.getDoubleValue();
/*      */       case 11:
/* 1063 */         _verifyNullForPrimitive(ctxt);
/* 1064 */         return 0.0D;
/*      */       
/*      */       case 1:
/* 1067 */         text = ctxt.extractScalarFromObject(p, this, double.class);
/*      */         break;
/*      */       case 3:
/* 1070 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 1071 */           p.nextToken();
/* 1072 */           double parsed = _parseDoublePrimitive(p, ctxt);
/* 1073 */           _verifyEndArrayForSingle(p, ctxt);
/* 1074 */           return parsed;
/*      */         } 
/*      */       
/*      */       default:
/* 1078 */         return ((Number)ctxt.handleUnexpectedToken(double.class, p)).doubleValue();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1085 */     Double nan = _checkDoubleSpecialValue(text);
/* 1086 */     if (nan != null) {
/* 1087 */       return nan.doubleValue();
/*      */     }
/*      */ 
/*      */     
/* 1091 */     CoercionAction act = _checkFromStringCoercion(ctxt, text, LogicalType.Integer, double.class);
/*      */     
/* 1093 */     if (act == CoercionAction.AsNull) {
/*      */       
/* 1095 */       _verifyNullForPrimitive(ctxt);
/* 1096 */       return 0.0D;
/*      */     } 
/* 1098 */     if (act == CoercionAction.AsEmpty) {
/* 1099 */       return 0.0D;
/*      */     }
/* 1101 */     String text = text.trim();
/* 1102 */     if (_hasTextualNull(text)) {
/* 1103 */       _verifyNullForPrimitiveCoercion(ctxt, text);
/* 1104 */       return 0.0D;
/*      */     } 
/* 1106 */     return _parseDoublePrimitive(ctxt, text);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final double _parseDoublePrimitive(DeserializationContext ctxt, String text) throws IOException {
/*      */     try {
/* 1116 */       return _parseDouble(text);
/* 1117 */     } catch (IllegalArgumentException illegalArgumentException) {
/* 1118 */       Number v = (Number)ctxt.handleWeirdStringValue(double.class, text, "not a valid `double` value (as String to convert)", new Object[0]);
/*      */       
/* 1120 */       return _nonNullNumber(v).doubleValue();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final double _parseDouble(String numStr) throws NumberFormatException {
/* 1130 */     if ("2.2250738585072012e-308".equals(numStr)) {
/* 1131 */       return 2.2250738585072014E-308D;
/*      */     }
/* 1133 */     return Double.parseDouble(numStr);
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
/*      */   protected Double _checkDoubleSpecialValue(String text) {
/* 1150 */     if (!text.isEmpty()) {
/* 1151 */       switch (text.charAt(0)) {
/*      */         case 'I':
/* 1153 */           if (_isPosInf(text)) {
/* 1154 */             return Double.valueOf(Double.POSITIVE_INFINITY);
/*      */           }
/*      */           break;
/*      */         case 'N':
/* 1158 */           if (_isNaN(text)) {
/* 1159 */             return Double.valueOf(Double.NaN);
/*      */           }
/*      */           break;
/*      */         case '-':
/* 1163 */           if (_isNegInf(text)) {
/* 1164 */             return Double.valueOf(Double.NEGATIVE_INFINITY);
/*      */           }
/*      */           break;
/*      */       } 
/*      */     
/*      */     }
/* 1170 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Date _parseDate(JsonParser p, DeserializationContext ctxt) throws IOException {
/*      */     String text;
/*      */     long ts;
/* 1177 */     switch (p.currentTokenId()) {
/*      */       case 6:
/* 1179 */         text = p.getText();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1206 */         return _parseDate(text.trim(), ctxt);case 7: try { ts = p.getLongValue(); } catch (StreamReadException e) { Number v = (Number)ctxt.handleWeirdNumberValue(this._valueClass, p.getNumberValue(), "not a valid 64-bit `long` for creating `java.util.Date`", new Object[0]); ts = v.longValue(); }  return new Date(ts);case 11: return (Date)getNullValue(ctxt);case 1: text = ctxt.extractScalarFromObject(p, this, this._valueClass); return _parseDate(text.trim(), ctxt);
/*      */       case 3:
/*      */         return _parseDateFromArray(p, ctxt);
/*      */     } 
/*      */     return (Date)ctxt.handleUnexpectedToken(this._valueClass, p);
/*      */   }
/*      */   protected Date _parseDateFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1213 */     CoercionAction act = _findCoercionFromEmptyArray(ctxt);
/* 1214 */     boolean unwrap = ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
/*      */     
/* 1216 */     if (unwrap || act != CoercionAction.Fail) {
/* 1217 */       JsonToken t = p.nextToken();
/* 1218 */       if (t == JsonToken.END_ARRAY) {
/* 1219 */         switch (act) {
/*      */           case AsEmpty:
/* 1221 */             return (Date)getEmptyValue(ctxt);
/*      */           case AsNull:
/*      */           case TryConvert:
/* 1224 */             return (Date)getNullValue(ctxt);
/*      */         } 
/*      */       
/* 1227 */       } else if (unwrap) {
/* 1228 */         Date parsed = _parseDate(p, ctxt);
/* 1229 */         _verifyEndArrayForSingle(p, ctxt);
/* 1230 */         return parsed;
/*      */       } 
/*      */     } 
/* 1233 */     return (Date)ctxt.handleUnexpectedToken(this._valueClass, JsonToken.START_ARRAY, p, null, new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Date _parseDate(String value, DeserializationContext ctxt) throws IOException {
/*      */     try {
/* 1244 */       if (value.isEmpty()) {
/* 1245 */         CoercionAction act = _checkFromStringCoercion(ctxt, value);
/* 1246 */         switch (act) {
/*      */           case AsEmpty:
/* 1248 */             return new Date(0L);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1253 */         return null;
/*      */       } 
/*      */       
/* 1256 */       if (_hasTextualNull(value)) {
/* 1257 */         return null;
/*      */       }
/* 1259 */       return ctxt.parseDate(value);
/* 1260 */     } catch (IllegalArgumentException iae) {
/* 1261 */       return (Date)ctxt.handleWeirdStringValue(this._valueClass, value, "not a valid representation (error: %s)", new Object[] {
/*      */             
/* 1263 */             ClassUtil.exceptionMessage(iae)
/*      */           });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final String _parseString(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1275 */     if (p.hasToken(JsonToken.VALUE_STRING)) {
/* 1276 */       return p.getText();
/*      */     }
/*      */     
/* 1279 */     if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
/* 1280 */       Object ob = p.getEmbeddedObject();
/* 1281 */       if (ob instanceof byte[]) {
/* 1282 */         return ctxt.getBase64Variant().encode((byte[])ob, false);
/*      */       }
/* 1284 */       if (ob == null) {
/* 1285 */         return null;
/*      */       }
/*      */       
/* 1288 */       return ob.toString();
/*      */     } 
/*      */     
/* 1291 */     if (p.hasToken(JsonToken.START_OBJECT)) {
/* 1292 */       return ctxt.extractScalarFromObject(p, this, this._valueClass);
/*      */     }
/*      */     
/* 1295 */     String value = p.getValueAsString();
/* 1296 */     if (value != null) {
/* 1297 */       return value;
/*      */     }
/* 1299 */     return (String)ctxt.handleUnexpectedToken(String.class, p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _hasTextualNull(String value) {
/* 1310 */     return "null".equals(value);
/*      */   }
/*      */   
/*      */   protected final boolean _isNegInf(String text) {
/* 1314 */     return ("-Infinity".equals(text) || "-INF".equals(text));
/*      */   }
/*      */   
/*      */   protected final boolean _isPosInf(String text) {
/* 1318 */     return ("Infinity".equals(text) || "INF".equals(text));
/*      */   }
/*      */   protected final boolean _isNaN(String text) {
/* 1321 */     return "NaN".equals(text);
/*      */   }
/*      */ 
/*      */   
/*      */   protected static final boolean _isBlank(String text) {
/* 1326 */     int len = text.length();
/* 1327 */     for (int i = 0; i < len; i++) {
/* 1328 */       if (text.charAt(i) > ' ') {
/* 1329 */         return false;
/*      */       }
/*      */     } 
/* 1332 */     return true;
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
/*      */   protected CoercionAction _checkFromStringCoercion(DeserializationContext ctxt, String value) throws IOException {
/* 1347 */     return _checkFromStringCoercion(ctxt, value, logicalType(), handledType());
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
/*      */   protected CoercionAction _checkFromStringCoercion(DeserializationContext ctxt, String value, LogicalType logicalType, Class<?> rawTargetType) throws IOException {
/* 1364 */     if (value.isEmpty()) {
/* 1365 */       CoercionAction coercionAction = ctxt.findCoercionAction(logicalType, rawTargetType, CoercionInputShape.EmptyString);
/*      */       
/* 1367 */       return _checkCoercionFail(ctxt, coercionAction, rawTargetType, value, "empty String (\"\")");
/*      */     } 
/* 1369 */     if (_isBlank(value)) {
/* 1370 */       CoercionAction coercionAction = ctxt.findCoercionFromBlankString(logicalType, rawTargetType, CoercionAction.Fail);
/* 1371 */       return _checkCoercionFail(ctxt, coercionAction, rawTargetType, value, "blank String (all whitespace)");
/*      */     } 
/*      */ 
/*      */     
/* 1375 */     if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)) {
/* 1376 */       return CoercionAction.TryConvert;
/*      */     }
/* 1378 */     CoercionAction act = ctxt.findCoercionAction(logicalType, rawTargetType, CoercionInputShape.String);
/* 1379 */     if (act == CoercionAction.Fail)
/*      */     {
/* 1381 */       ctxt.reportInputMismatch(this, "Cannot coerce String value (\"%s\") to %s (but might if coercion using `CoercionConfig` was enabled)", new Object[] { value, 
/*      */             
/* 1383 */             _coercedTypeDesc() });
/*      */     }
/*      */     
/* 1386 */     return act;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected CoercionAction _checkFloatToIntCoercion(JsonParser p, DeserializationContext ctxt, Class<?> rawTargetType) throws IOException {
/* 1396 */     CoercionAction act = ctxt.findCoercionAction(LogicalType.Integer, rawTargetType, CoercionInputShape.Float);
/*      */     
/* 1398 */     if (act == CoercionAction.Fail) {
/* 1399 */       return _checkCoercionFail(ctxt, act, rawTargetType, p.getNumberValue(), "Floating-point value (" + p
/* 1400 */           .getText() + ")");
/*      */     }
/* 1402 */     return act;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Boolean _coerceBooleanFromInt(JsonParser p, DeserializationContext ctxt, Class<?> rawTargetType) throws IOException {
/* 1412 */     CoercionAction act = ctxt.findCoercionAction(LogicalType.Boolean, rawTargetType, CoercionInputShape.Integer);
/* 1413 */     switch (act) {
/*      */       case Fail:
/* 1415 */         _checkCoercionFail(ctxt, act, rawTargetType, p.getNumberValue(), "Integer value (" + p
/* 1416 */             .getText() + ")");
/* 1417 */         return Boolean.FALSE;
/*      */       case AsNull:
/* 1419 */         return null;
/*      */       case AsEmpty:
/* 1421 */         return Boolean.FALSE;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1427 */     if (p.getNumberType() == JsonParser.NumberType.INT)
/*      */     {
/* 1429 */       return Boolean.valueOf((p.getIntValue() != 0));
/*      */     }
/* 1431 */     return Boolean.valueOf(!"0".equals(p.getText()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected CoercionAction _checkCoercionFail(DeserializationContext ctxt, CoercionAction act, Class<?> targetType, Object inputValue, String inputDesc) throws IOException {
/* 1442 */     if (act == CoercionAction.Fail) {
/* 1443 */       ctxt.reportBadCoercion(this, targetType, inputValue, "Cannot coerce %s to %s (but could if coercion was enabled using `CoercionConfig`)", new Object[] { inputDesc, 
/*      */             
/* 1445 */             _coercedTypeDesc() });
/*      */     }
/* 1447 */     return act;
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
/*      */   protected boolean _checkTextualNull(DeserializationContext ctxt, String text) throws JsonMappingException {
/* 1460 */     if (_hasTextualNull(text)) {
/* 1461 */       if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
/* 1462 */         _reportFailedNullCoerce(ctxt, true, (Enum<?>)MapperFeature.ALLOW_COERCION_OF_SCALARS, "String \"null\"");
/*      */       }
/* 1464 */       return true;
/*      */     } 
/* 1466 */     return false;
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
/*      */   protected Object _coerceIntegral(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1488 */     if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
/* 1489 */       return p.getBigIntegerValue();
/*      */     }
/* 1491 */     if (ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS)) {
/* 1492 */       return Long.valueOf(p.getLongValue());
/*      */     }
/* 1494 */     return p.getNumberValue();
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
/*      */   protected final void _verifyNullForPrimitive(DeserializationContext ctxt) throws JsonMappingException {
/* 1507 */     if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
/* 1508 */       ctxt.reportInputMismatch(this, "Cannot coerce `null` to %s (disable `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to allow)", new Object[] {
/*      */             
/* 1510 */             _coercedTypeDesc()
/*      */           });
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
/*      */   protected final void _verifyNullForPrimitiveCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
/*      */     DeserializationFeature deserializationFeature;
/*      */     boolean enable;
/* 1527 */     if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
/* 1528 */       MapperFeature mapperFeature = MapperFeature.ALLOW_COERCION_OF_SCALARS;
/* 1529 */       enable = true;
/* 1530 */     } else if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
/* 1531 */       deserializationFeature = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
/* 1532 */       enable = false;
/*      */     } else {
/*      */       return;
/*      */     } 
/* 1536 */     String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", new Object[] { str });
/* 1537 */     _reportFailedNullCoerce(ctxt, enable, (Enum<?>)deserializationFeature, strDesc);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportFailedNullCoerce(DeserializationContext ctxt, boolean state, Enum<?> feature, String inputDesc) throws JsonMappingException {
/* 1543 */     String enableDesc = state ? "enable" : "disable";
/* 1544 */     ctxt.reportInputMismatch(this, "Cannot coerce %s to Null value as %s (%s `%s.%s` to allow)", new Object[] { inputDesc, 
/* 1545 */           _coercedTypeDesc(), enableDesc, feature.getDeclaringClass().getSimpleName(), feature.name() });
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
/*      */   protected String _coercedTypeDesc() {
/*      */     boolean structured;
/*      */     String typeDesc;
/* 1561 */     JavaType t = getValueType();
/* 1562 */     if (t != null && !t.isPrimitive()) {
/* 1563 */       structured = (t.isContainerType() || t.isReferenceType());
/* 1564 */       typeDesc = ClassUtil.getTypeDescription(t);
/*      */     } else {
/* 1566 */       Class<?> cls = handledType();
/*      */       
/* 1568 */       structured = (cls.isArray() || Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls));
/* 1569 */       typeDesc = ClassUtil.getClassDescription(cls);
/*      */     } 
/* 1571 */     if (structured) {
/* 1572 */       return "element of " + typeDesc;
/*      */     }
/* 1574 */     return typeDesc + " value";
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
/*      */   @Deprecated
/*      */   protected boolean _parseBooleanFromInt(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 1591 */     _verifyNumberForScalarCoercion(ctxt, p);
/*      */ 
/*      */     
/* 1594 */     return !"0".equals(p.getText());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void _verifyStringForScalarCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
/* 1603 */     MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
/* 1604 */     if (!ctxt.isEnabled(feat)) {
/* 1605 */       ctxt.reportInputMismatch(this, "Cannot coerce String \"%s\" to %s (enable `%s.%s` to allow)", new Object[] { str, 
/* 1606 */             _coercedTypeDesc(), feat.getDeclaringClass().getSimpleName(), feat.name() });
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
/*      */   @Deprecated
/*      */   protected Object _coerceEmptyString(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
/*      */     DeserializationFeature deserializationFeature;
/*      */     boolean enable;
/* 1621 */     if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
/* 1622 */       MapperFeature mapperFeature = MapperFeature.ALLOW_COERCION_OF_SCALARS;
/* 1623 */       enable = true;
/* 1624 */     } else if (isPrimitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
/* 1625 */       deserializationFeature = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
/* 1626 */       enable = false;
/*      */     } else {
/* 1628 */       return getNullValue(ctxt);
/*      */     } 
/* 1630 */     _reportFailedNullCoerce(ctxt, enable, (Enum<?>)deserializationFeature, "empty String (\"\")");
/* 1631 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void _failDoubleToIntCoercion(JsonParser p, DeserializationContext ctxt, String type) throws IOException {
/* 1638 */     ctxt.reportInputMismatch(handledType(), "Cannot coerce a floating-point value ('%s') into %s (enable `DeserializationFeature.ACCEPT_FLOAT_AS_INT` to allow)", new Object[] { p
/*      */           
/* 1640 */           .getValueAsString(), type });
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected final void _verifyNullForScalarCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
/* 1646 */     if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
/* 1647 */       String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", new Object[] { str });
/* 1648 */       _reportFailedNullCoerce(ctxt, true, (Enum<?>)MapperFeature.ALLOW_COERCION_OF_SCALARS, strDesc);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void _verifyNumberForScalarCoercion(DeserializationContext ctxt, JsonParser p) throws IOException {
/* 1655 */     MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
/* 1656 */     if (!ctxt.isEnabled(feat)) {
/*      */ 
/*      */       
/* 1659 */       String valueDesc = p.getText();
/* 1660 */       ctxt.reportInputMismatch(this, "Cannot coerce Number (%s) to %s (enable `%s.%s` to allow)", new Object[] { valueDesc, 
/* 1661 */             _coercedTypeDesc(), feat.getDeclaringClass().getSimpleName(), feat.name() });
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected Object _coerceNullToken(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
/* 1668 */     if (isPrimitive) {
/* 1669 */       _verifyNullForPrimitive(ctxt);
/*      */     }
/* 1671 */     return getNullValue(ctxt);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   protected Object _coerceTextualNull(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
/* 1676 */     if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
/* 1677 */       _reportFailedNullCoerce(ctxt, true, (Enum<?>)MapperFeature.ALLOW_COERCION_OF_SCALARS, "String \"null\"");
/*      */     }
/* 1679 */     return getNullValue(ctxt);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   protected boolean _isEmptyOrTextualNull(String value) {
/* 1684 */     return (value.isEmpty() || "null".equals(value));
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
/*      */   protected JsonDeserializer<Object> findDeserializer(DeserializationContext ctxt, JavaType type, BeanProperty property) throws JsonMappingException {
/* 1706 */     return ctxt.findContextualValueDeserializer(type, property);
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
/*      */   protected final boolean _isIntNumber(String text) {
/* 1718 */     int len = text.length();
/* 1719 */     if (len > 0) {
/* 1720 */       int i; char c = text.charAt(0);
/*      */ 
/*      */ 
/*      */       
/* 1724 */       if (c == '-' || c == '+') {
/* 1725 */         if (len == 1) {
/* 1726 */           return false;
/*      */         }
/* 1728 */         i = 1;
/*      */       } else {
/* 1730 */         i = 0;
/*      */       } 
/*      */       
/* 1733 */       for (; i < len; i++) {
/* 1734 */         int ch = text.charAt(i);
/* 1735 */         if (ch > 57 || ch < 48) {
/* 1736 */           return false;
/*      */         }
/*      */       } 
/* 1739 */       return true;
/*      */     } 
/* 1741 */     return false;
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
/*      */   protected JsonDeserializer<?> findConvertingContentDeserializer(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> existingDeserializer) throws JsonMappingException {
/* 1764 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 1765 */     if (_neitherNull(intr, prop)) {
/* 1766 */       AnnotatedMember member = prop.getMember();
/* 1767 */       if (member != null) {
/* 1768 */         Object convDef = intr.findDeserializationContentConverter(member);
/* 1769 */         if (convDef != null) {
/* 1770 */           Converter<Object, Object> conv = ctxt.converterInstance((Annotated)prop.getMember(), convDef);
/* 1771 */           JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/* 1772 */           if (existingDeserializer == null) {
/* 1773 */             existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop);
/*      */           }
/* 1775 */           return new StdDelegatingDeserializer(conv, delegateType, existingDeserializer);
/*      */         } 
/*      */       } 
/*      */     } 
/* 1779 */     return existingDeserializer;
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
/*      */   protected JsonFormat.Value findFormatOverrides(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults) {
/* 1800 */     if (prop != null) {
/* 1801 */       return prop.findPropertyFormat((MapperConfig)ctxt.getConfig(), typeForDefaults);
/*      */     }
/*      */     
/* 1804 */     return ctxt.getDefaultPropertyFormat(typeForDefaults);
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
/*      */   protected Boolean findFormatFeature(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults, JsonFormat.Feature feat) {
/* 1820 */     JsonFormat.Value format = findFormatOverrides(ctxt, prop, typeForDefaults);
/* 1821 */     if (format != null) {
/* 1822 */       return format.getFeature(feat);
/*      */     }
/* 1824 */     return null;
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
/*      */   protected final NullValueProvider findValueNullProvider(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata) throws JsonMappingException {
/* 1838 */     if (prop != null) {
/* 1839 */       return _findNullProvider(ctxt, (BeanProperty)prop, propMetadata.getValueNulls(), prop
/* 1840 */           .getValueDeserializer());
/*      */     }
/* 1842 */     return null;
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
/*      */   protected NullValueProvider findContentNullProvider(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> valueDeser) throws JsonMappingException {
/* 1857 */     Nulls nulls = findContentNullStyle(ctxt, prop);
/* 1858 */     if (nulls == Nulls.SKIP) {
/* 1859 */       return (NullValueProvider)NullsConstantProvider.skipper();
/*      */     }
/*      */ 
/*      */     
/* 1863 */     if (nulls == Nulls.FAIL) {
/* 1864 */       if (prop == null) {
/* 1865 */         JavaType type = ctxt.constructType(valueDeser.handledType());
/*      */         
/* 1867 */         if (type.isContainerType()) {
/* 1868 */           type = type.getContentType();
/*      */         }
/* 1870 */         return (NullValueProvider)NullsFailProvider.constructForRootValue(type);
/*      */       } 
/* 1872 */       return (NullValueProvider)NullsFailProvider.constructForProperty(prop, prop.getType().getContentType());
/*      */     } 
/*      */     
/* 1875 */     NullValueProvider prov = _findNullProvider(ctxt, prop, nulls, valueDeser);
/* 1876 */     if (prov != null) {
/* 1877 */       return prov;
/*      */     }
/* 1879 */     return (NullValueProvider)valueDeser;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Nulls findContentNullStyle(DeserializationContext ctxt, BeanProperty prop) throws JsonMappingException {
/* 1885 */     if (prop != null) {
/* 1886 */       return prop.getMetadata().getContentNulls();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1891 */     return ctxt.getConfig().getDefaultSetterInfo().getContentNulls();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final NullValueProvider _findNullProvider(DeserializationContext ctxt, BeanProperty prop, Nulls nulls, JsonDeserializer<?> valueDeser) throws JsonMappingException {
/* 1899 */     if (nulls == Nulls.FAIL) {
/* 1900 */       if (prop == null) {
/* 1901 */         Class<?> rawType = (valueDeser == null) ? Object.class : valueDeser.handledType();
/* 1902 */         return (NullValueProvider)NullsFailProvider.constructForRootValue(ctxt.constructType(rawType));
/*      */       } 
/* 1904 */       return (NullValueProvider)NullsFailProvider.constructForProperty(prop);
/*      */     } 
/* 1906 */     if (nulls == Nulls.AS_EMPTY) {
/*      */ 
/*      */       
/* 1909 */       if (valueDeser == null) {
/* 1910 */         return null;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1916 */       if (valueDeser instanceof BeanDeserializerBase) {
/* 1917 */         BeanDeserializerBase bd = (BeanDeserializerBase)valueDeser;
/* 1918 */         ValueInstantiator vi = bd.getValueInstantiator();
/* 1919 */         if (!vi.canCreateUsingDefault()) {
/* 1920 */           JavaType type = (prop == null) ? bd.getValueType() : prop.getType();
/* 1921 */           return (NullValueProvider)ctxt.reportBadDefinition(type, 
/* 1922 */               String.format("Cannot create empty instance of %s, no default Creator", new Object[] { type }));
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1927 */       AccessPattern access = valueDeser.getEmptyAccessPattern();
/* 1928 */       if (access == AccessPattern.ALWAYS_NULL) {
/* 1929 */         return (NullValueProvider)NullsConstantProvider.nuller();
/*      */       }
/* 1931 */       if (access == AccessPattern.CONSTANT) {
/* 1932 */         return (NullValueProvider)NullsConstantProvider.forValue(valueDeser.getEmptyValue(ctxt));
/*      */       }
/*      */       
/* 1935 */       return (NullValueProvider)new NullsAsEmptyProvider(valueDeser);
/*      */     } 
/* 1937 */     if (nulls == Nulls.SKIP) {
/* 1938 */       return (NullValueProvider)NullsConstantProvider.skipper();
/*      */     }
/* 1940 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected CoercionAction _findCoercionFromEmptyString(DeserializationContext ctxt) {
/* 1945 */     return ctxt.findCoercionAction(logicalType(), handledType(), CoercionInputShape.EmptyString);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected CoercionAction _findCoercionFromEmptyArray(DeserializationContext ctxt) {
/* 1951 */     return ctxt.findCoercionAction(logicalType(), handledType(), CoercionInputShape.EmptyArray);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected CoercionAction _findCoercionFromBlankString(DeserializationContext ctxt) {
/* 1957 */     return ctxt.findCoercionFromBlankString(logicalType(), handledType(), CoercionAction.Fail);
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
/*      */ 
/*      */   
/*      */   protected void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object<?> instanceOrClass, String propName) throws IOException {
/* 1985 */     if (instanceOrClass == null) {
/* 1986 */       instanceOrClass = (Object<?>)handledType();
/*      */     }
/*      */     
/* 1989 */     if (ctxt.handleUnknownProperty(p, this, instanceOrClass, propName)) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1995 */     p.skipChildren();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void handleMissingEndArrayForSingle(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 2001 */     ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Attempted to unwrap '%s' value from an array (with `DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS`) but it contains more than one value", new Object[] {
/*      */           
/* 2003 */           handledType().getName()
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _verifyEndArrayForSingle(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 2010 */     JsonToken t = p.nextToken();
/* 2011 */     if (t != JsonToken.END_ARRAY) {
/* 2012 */       handleMissingEndArrayForSingle(p, ctxt);
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
/*      */   protected static final boolean _neitherNull(Object a, Object b) {
/* 2026 */     return (a != null && b != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _byteOverflow(int value) {
/* 2035 */     return (value < -128 || value > 255);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _shortOverflow(int value) {
/* 2042 */     return (value < -32768 || value > 32767);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _intOverflow(long value) {
/* 2049 */     return (value < -2147483648L || value > 2147483647L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Number _nonNullNumber(Number n) {
/* 2056 */     if (n == null) {
/* 2057 */       n = Integer.valueOf(0);
/*      */     }
/* 2059 */     return n;
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StdDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */