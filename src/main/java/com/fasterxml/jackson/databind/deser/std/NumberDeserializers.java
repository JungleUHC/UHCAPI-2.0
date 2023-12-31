/*      */ package com.fasterxml.jackson.databind.deser.std;
/*      */ 
/*      */ import com.fasterxml.jackson.core.JacksonException;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.io.NumberInput;
/*      */ import com.fasterxml.jackson.databind.DeserializationContext;
/*      */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*      */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*      */ import com.fasterxml.jackson.databind.JsonMappingException;
/*      */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.fasterxml.jackson.databind.type.LogicalType;
/*      */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import java.io.IOException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.HashSet;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class NumberDeserializers
/*      */ {
/*   28 */   private static final HashSet<String> _classNames = new HashSet<>();
/*      */   
/*      */   static {
/*   31 */     Class<?>[] numberTypes = new Class[] { Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   43 */     for (Class<?> cls : numberTypes) {
/*   44 */       _classNames.add(cls.getName());
/*      */     }
/*      */   }
/*      */   
/*      */   public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
/*   49 */     if (rawType.isPrimitive()) {
/*   50 */       if (rawType == int.class) {
/*   51 */         return IntegerDeserializer.primitiveInstance;
/*      */       }
/*   53 */       if (rawType == boolean.class) {
/*   54 */         return BooleanDeserializer.primitiveInstance;
/*      */       }
/*   56 */       if (rawType == long.class) {
/*   57 */         return LongDeserializer.primitiveInstance;
/*      */       }
/*   59 */       if (rawType == double.class) {
/*   60 */         return DoubleDeserializer.primitiveInstance;
/*      */       }
/*   62 */       if (rawType == char.class) {
/*   63 */         return CharacterDeserializer.primitiveInstance;
/*      */       }
/*   65 */       if (rawType == byte.class) {
/*   66 */         return ByteDeserializer.primitiveInstance;
/*      */       }
/*   68 */       if (rawType == short.class) {
/*   69 */         return ShortDeserializer.primitiveInstance;
/*      */       }
/*   71 */       if (rawType == float.class) {
/*   72 */         return FloatDeserializer.primitiveInstance;
/*      */       }
/*      */ 
/*      */       
/*   76 */       if (rawType == void.class) {
/*   77 */         return NullifyingDeserializer.instance;
/*      */       }
/*   79 */     } else if (_classNames.contains(clsName)) {
/*      */       
/*   81 */       if (rawType == Integer.class) {
/*   82 */         return IntegerDeserializer.wrapperInstance;
/*      */       }
/*   84 */       if (rawType == Boolean.class) {
/*   85 */         return BooleanDeserializer.wrapperInstance;
/*      */       }
/*   87 */       if (rawType == Long.class) {
/*   88 */         return LongDeserializer.wrapperInstance;
/*      */       }
/*   90 */       if (rawType == Double.class) {
/*   91 */         return DoubleDeserializer.wrapperInstance;
/*      */       }
/*   93 */       if (rawType == Character.class) {
/*   94 */         return CharacterDeserializer.wrapperInstance;
/*      */       }
/*   96 */       if (rawType == Byte.class) {
/*   97 */         return ByteDeserializer.wrapperInstance;
/*      */       }
/*   99 */       if (rawType == Short.class) {
/*  100 */         return ShortDeserializer.wrapperInstance;
/*      */       }
/*  102 */       if (rawType == Float.class) {
/*  103 */         return FloatDeserializer.wrapperInstance;
/*      */       }
/*  105 */       if (rawType == Number.class) {
/*  106 */         return NumberDeserializer.instance;
/*      */       }
/*  108 */       if (rawType == BigDecimal.class) {
/*  109 */         return BigDecimalDeserializer.instance;
/*      */       }
/*  111 */       if (rawType == BigInteger.class) {
/*  112 */         return BigIntegerDeserializer.instance;
/*      */       }
/*      */     } else {
/*  115 */       return null;
/*      */     } 
/*      */     
/*  118 */     throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static abstract class PrimitiveOrWrapperDeserializer<T>
/*      */     extends StdScalarDeserializer<T>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */     
/*      */     protected final LogicalType _logicalType;
/*      */ 
/*      */ 
/*      */     
/*      */     protected final T _nullValue;
/*      */ 
/*      */     
/*      */     protected final T _emptyValue;
/*      */ 
/*      */     
/*      */     protected final boolean _primitive;
/*      */ 
/*      */ 
/*      */     
/*      */     protected PrimitiveOrWrapperDeserializer(Class<T> vc, LogicalType logicalType, T nvl, T empty) {
/*  146 */       super(vc);
/*  147 */       this._logicalType = logicalType;
/*  148 */       this._nullValue = nvl;
/*  149 */       this._emptyValue = empty;
/*  150 */       this._primitive = vc.isPrimitive();
/*      */     }
/*      */     
/*      */     @Deprecated
/*      */     protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl, T empty) {
/*  155 */       this(vc, LogicalType.OtherScalar, nvl, empty);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public AccessPattern getNullAccessPattern() {
/*  162 */       if (this._primitive) {
/*  163 */         return AccessPattern.DYNAMIC;
/*      */       }
/*  165 */       if (this._nullValue == null) {
/*  166 */         return AccessPattern.ALWAYS_NULL;
/*      */       }
/*  168 */       return AccessPattern.CONSTANT;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
/*  175 */       if (this._primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))
/*  176 */         ctxt.reportInputMismatch(this, "Cannot map `null` into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", new Object[] {
/*      */               
/*  178 */               ClassUtil.classNameOf(handledType())
/*      */             }); 
/*  180 */       return this._nullValue;
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/*  185 */       return this._emptyValue;
/*      */     }
/*      */ 
/*      */     
/*      */     public final LogicalType logicalType() {
/*  190 */       return this._logicalType;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static final class BooleanDeserializer
/*      */     extends PrimitiveOrWrapperDeserializer<Boolean>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */     
/*  206 */     static final BooleanDeserializer primitiveInstance = new BooleanDeserializer((Class)boolean.class, Boolean.FALSE);
/*  207 */     static final BooleanDeserializer wrapperInstance = new BooleanDeserializer(Boolean.class, null);
/*      */ 
/*      */     
/*      */     public BooleanDeserializer(Class<Boolean> cls, Boolean nvl) {
/*  211 */       super(cls, LogicalType.Boolean, nvl, Boolean.FALSE);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  217 */       JsonToken t = p.currentToken();
/*  218 */       if (t == JsonToken.VALUE_TRUE) {
/*  219 */         return Boolean.TRUE;
/*      */       }
/*  221 */       if (t == JsonToken.VALUE_FALSE) {
/*  222 */         return Boolean.FALSE;
/*      */       }
/*  224 */       if (this._primitive) {
/*  225 */         return Boolean.valueOf(_parseBooleanPrimitive(p, ctxt));
/*      */       }
/*  227 */       return _parseBoolean(p, ctxt, this._valueClass);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Boolean deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/*  237 */       JsonToken t = p.currentToken();
/*  238 */       if (t == JsonToken.VALUE_TRUE) {
/*  239 */         return Boolean.TRUE;
/*      */       }
/*  241 */       if (t == JsonToken.VALUE_FALSE) {
/*  242 */         return Boolean.FALSE;
/*      */       }
/*  244 */       if (this._primitive) {
/*  245 */         return Boolean.valueOf(_parseBooleanPrimitive(p, ctxt));
/*      */       }
/*  247 */       return _parseBoolean(p, ctxt, this._valueClass);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static class ByteDeserializer
/*      */     extends PrimitiveOrWrapperDeserializer<Byte>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*  257 */     static final ByteDeserializer primitiveInstance = new ByteDeserializer((Class)byte.class, Byte.valueOf((byte)0));
/*  258 */     static final ByteDeserializer wrapperInstance = new ByteDeserializer(Byte.class, null);
/*      */ 
/*      */     
/*      */     public ByteDeserializer(Class<Byte> cls, Byte nvl) {
/*  262 */       super(cls, LogicalType.Integer, nvl, Byte.valueOf((byte)0));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Byte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  268 */       if (p.isExpectedNumberIntToken()) {
/*  269 */         return Byte.valueOf(p.getByteValue());
/*      */       }
/*  271 */       if (this._primitive) {
/*  272 */         return Byte.valueOf(_parseBytePrimitive(p, ctxt));
/*      */       }
/*  274 */       return _parseByte(p, ctxt);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Byte _parseByte(JsonParser p, DeserializationContext ctxt) throws IOException {
/*      */       int value;
/*  282 */       switch (p.currentTokenId()) {
/*      */         case 6:
/*  284 */           text = p.getText();
/*      */           break;
/*      */         case 8:
/*  287 */           act = _checkFloatToIntCoercion(p, ctxt, this._valueClass);
/*  288 */           if (act == CoercionAction.AsNull) {
/*  289 */             return getNullValue(ctxt);
/*      */           }
/*  291 */           if (act == CoercionAction.AsEmpty) {
/*  292 */             return (Byte)getEmptyValue(ctxt);
/*      */           }
/*  294 */           return Byte.valueOf(p.getByteValue());
/*      */         case 11:
/*  296 */           return getNullValue(ctxt);
/*      */         case 7:
/*  298 */           return Byte.valueOf(p.getByteValue());
/*      */         case 3:
/*  300 */           return _deserializeFromArray(p, ctxt);
/*      */         
/*      */         case 1:
/*  303 */           text = ctxt.extractScalarFromObject(p, this, this._valueClass);
/*      */           break;
/*      */         default:
/*  306 */           return (Byte)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */       } 
/*      */ 
/*      */       
/*  310 */       CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  311 */       if (act == CoercionAction.AsNull) {
/*  312 */         return getNullValue(ctxt);
/*      */       }
/*  314 */       if (act == CoercionAction.AsEmpty) {
/*  315 */         return (Byte)getEmptyValue(ctxt);
/*      */       }
/*  317 */       String text = text.trim();
/*  318 */       if (_checkTextualNull(ctxt, text)) {
/*  319 */         return getNullValue(ctxt);
/*      */       }
/*      */       
/*      */       try {
/*  323 */         value = NumberInput.parseInt(text);
/*  324 */       } catch (IllegalArgumentException iae) {
/*  325 */         return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Byte value", new Object[0]);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  330 */       if (_byteOverflow(value)) {
/*  331 */         return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 8-bit value", new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*  335 */       return Byte.valueOf((byte)value);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static class ShortDeserializer
/*      */     extends PrimitiveOrWrapperDeserializer<Short>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*  345 */     static final ShortDeserializer primitiveInstance = new ShortDeserializer((Class)short.class, Short.valueOf((short)0));
/*  346 */     static final ShortDeserializer wrapperInstance = new ShortDeserializer(Short.class, null);
/*      */ 
/*      */     
/*      */     public ShortDeserializer(Class<Short> cls, Short nvl) {
/*  350 */       super(cls, LogicalType.Integer, nvl, Short.valueOf((short)0));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Short deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  357 */       if (p.isExpectedNumberIntToken()) {
/*  358 */         return Short.valueOf(p.getShortValue());
/*      */       }
/*  360 */       if (this._primitive) {
/*  361 */         return Short.valueOf(_parseShortPrimitive(p, ctxt));
/*      */       }
/*  363 */       return _parseShort(p, ctxt);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Short _parseShort(JsonParser p, DeserializationContext ctxt) throws IOException {
/*      */       int value;
/*  370 */       switch (p.currentTokenId()) {
/*      */         case 6:
/*  372 */           text = p.getText();
/*      */           break;
/*      */         case 8:
/*  375 */           act = _checkFloatToIntCoercion(p, ctxt, this._valueClass);
/*  376 */           if (act == CoercionAction.AsNull) {
/*  377 */             return getNullValue(ctxt);
/*      */           }
/*  379 */           if (act == CoercionAction.AsEmpty) {
/*  380 */             return (Short)getEmptyValue(ctxt);
/*      */           }
/*  382 */           return Short.valueOf(p.getShortValue());
/*      */         case 11:
/*  384 */           return getNullValue(ctxt);
/*      */         case 7:
/*  386 */           return Short.valueOf(p.getShortValue());
/*      */         
/*      */         case 1:
/*  389 */           text = ctxt.extractScalarFromObject(p, this, this._valueClass);
/*      */           break;
/*      */         case 3:
/*  392 */           return _deserializeFromArray(p, ctxt);
/*      */         default:
/*  394 */           return (Short)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */       } 
/*      */ 
/*      */       
/*  398 */       CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  399 */       if (act == CoercionAction.AsNull) {
/*  400 */         return getNullValue(ctxt);
/*      */       }
/*  402 */       if (act == CoercionAction.AsEmpty) {
/*  403 */         return (Short)getEmptyValue(ctxt);
/*      */       }
/*  405 */       String text = text.trim();
/*  406 */       if (_checkTextualNull(ctxt, text)) {
/*  407 */         return getNullValue(ctxt);
/*      */       }
/*      */       
/*      */       try {
/*  411 */         value = NumberInput.parseInt(text);
/*  412 */       } catch (IllegalArgumentException iae) {
/*  413 */         return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Short value", new Object[0]);
/*      */       } 
/*      */ 
/*      */       
/*  417 */       if (_shortOverflow(value)) {
/*  418 */         return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 16-bit value", new Object[0]);
/*      */       }
/*      */       
/*  421 */       return Short.valueOf((short)value);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static class CharacterDeserializer
/*      */     extends PrimitiveOrWrapperDeserializer<Character>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*  431 */     static final CharacterDeserializer primitiveInstance = new CharacterDeserializer((Class)char.class, Character.valueOf(false));
/*  432 */     static final CharacterDeserializer wrapperInstance = new CharacterDeserializer(Character.class, null);
/*      */ 
/*      */     
/*      */     public CharacterDeserializer(Class<Character> cls, Character nvl) {
/*  436 */       super(cls, LogicalType.Integer, nvl, 
/*      */           
/*  438 */           Character.valueOf(false));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Character deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*      */       int value;
/*  446 */       switch (p.currentTokenId()) {
/*      */ 
/*      */ 
/*      */         
/*      */         case 6:
/*  451 */           text = p.getText();
/*      */           break;
/*      */         case 7:
/*  454 */           act = ctxt.findCoercionAction(logicalType(), this._valueClass, CoercionInputShape.Integer);
/*  455 */           switch (act) {
/*      */             case Fail:
/*  457 */               _checkCoercionFail(ctxt, act, this._valueClass, p.getNumberValue(), "Integer value (" + p
/*  458 */                   .getText() + ")");
/*      */             
/*      */             case AsNull:
/*  461 */               return getNullValue(ctxt);
/*      */             case AsEmpty:
/*  463 */               return (Character)getEmptyValue(ctxt);
/*      */           } 
/*      */           
/*  466 */           value = p.getIntValue();
/*  467 */           if (value >= 0 && value <= 65535) {
/*  468 */             return Character.valueOf((char)value);
/*      */           }
/*  470 */           return (Character)ctxt.handleWeirdNumberValue(handledType(), Integer.valueOf(value), "value outside valid Character range (0x0000 - 0xFFFF)", new Object[0]);
/*      */         
/*      */         case 11:
/*  473 */           if (this._primitive) {
/*  474 */             _verifyNullForPrimitive(ctxt);
/*      */           }
/*  476 */           return getNullValue(ctxt);
/*      */         
/*      */         case 1:
/*  479 */           text = ctxt.extractScalarFromObject(p, this, this._valueClass);
/*      */           break;
/*      */         case 3:
/*  482 */           return _deserializeFromArray(p, ctxt);
/*      */         default:
/*  484 */           return (Character)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */       } 
/*      */       
/*  487 */       if (text.length() == 1) {
/*  488 */         return Character.valueOf(text.charAt(0));
/*      */       }
/*  490 */       CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  491 */       if (act == CoercionAction.AsNull) {
/*  492 */         return getNullValue(ctxt);
/*      */       }
/*  494 */       if (act == CoercionAction.AsEmpty) {
/*  495 */         return (Character)getEmptyValue(ctxt);
/*      */       }
/*  497 */       String text = text.trim();
/*  498 */       if (_checkTextualNull(ctxt, text)) {
/*  499 */         return getNullValue(ctxt);
/*      */       }
/*  501 */       return (Character)ctxt.handleWeirdStringValue(handledType(), text, "Expected either Integer value code or 1-character String", new Object[0]);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static final class IntegerDeserializer
/*      */     extends PrimitiveOrWrapperDeserializer<Integer>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*  512 */     static final IntegerDeserializer primitiveInstance = new IntegerDeserializer((Class)int.class, Integer.valueOf(0));
/*  513 */     static final IntegerDeserializer wrapperInstance = new IntegerDeserializer(Integer.class, null);
/*      */     
/*      */     public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
/*  516 */       super(cls, LogicalType.Integer, nvl, Integer.valueOf(0));
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isCachable() {
/*  521 */       return true;
/*      */     }
/*      */     
/*      */     public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  525 */       if (p.isExpectedNumberIntToken()) {
/*  526 */         return Integer.valueOf(p.getIntValue());
/*      */       }
/*  528 */       if (this._primitive) {
/*  529 */         return Integer.valueOf(_parseIntPrimitive(p, ctxt));
/*      */       }
/*  531 */       return _parseInteger(p, ctxt, Integer.class);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Integer deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/*  540 */       if (p.isExpectedNumberIntToken()) {
/*  541 */         return Integer.valueOf(p.getIntValue());
/*      */       }
/*  543 */       if (this._primitive) {
/*  544 */         return Integer.valueOf(_parseIntPrimitive(p, ctxt));
/*      */       }
/*  546 */       return _parseInteger(p, ctxt, Integer.class);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static final class LongDeserializer
/*      */     extends PrimitiveOrWrapperDeserializer<Long>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*  556 */     static final LongDeserializer primitiveInstance = new LongDeserializer((Class)long.class, Long.valueOf(0L));
/*  557 */     static final LongDeserializer wrapperInstance = new LongDeserializer(Long.class, null);
/*      */     
/*      */     public LongDeserializer(Class<Long> cls, Long nvl) {
/*  560 */       super(cls, LogicalType.Integer, nvl, Long.valueOf(0L));
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isCachable() {
/*  565 */       return true;
/*      */     }
/*      */     
/*      */     public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  569 */       if (p.isExpectedNumberIntToken()) {
/*  570 */         return Long.valueOf(p.getLongValue());
/*      */       }
/*  572 */       if (this._primitive) {
/*  573 */         return Long.valueOf(_parseLongPrimitive(p, ctxt));
/*      */       }
/*  575 */       return _parseLong(p, ctxt, Long.class);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static class FloatDeserializer
/*      */     extends PrimitiveOrWrapperDeserializer<Float>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*  585 */     static final FloatDeserializer primitiveInstance = new FloatDeserializer((Class)float.class, Float.valueOf(0.0F));
/*  586 */     static final FloatDeserializer wrapperInstance = new FloatDeserializer(Float.class, null);
/*      */     
/*      */     public FloatDeserializer(Class<Float> cls, Float nvl) {
/*  589 */       super(cls, LogicalType.Float, nvl, Float.valueOf(0.0F));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  595 */       if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
/*  596 */         return Float.valueOf(p.getFloatValue());
/*      */       }
/*  598 */       if (this._primitive) {
/*  599 */         return Float.valueOf(_parseFloatPrimitive(p, ctxt));
/*      */       }
/*  601 */       return _parseFloat(p, ctxt);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final Float _parseFloat(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  608 */       switch (p.currentTokenId()) {
/*      */         case 6:
/*  610 */           text = p.getText();
/*      */           break;
/*      */         case 11:
/*  613 */           return getNullValue(ctxt);
/*      */         case 7:
/*      */         case 8:
/*  616 */           return Float.valueOf(p.getFloatValue());
/*      */         
/*      */         case 1:
/*  619 */           text = ctxt.extractScalarFromObject(p, this, this._valueClass);
/*      */           break;
/*      */         case 3:
/*  622 */           return _deserializeFromArray(p, ctxt);
/*      */         default:
/*  624 */           return (Float)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  631 */       Float nan = _checkFloatSpecialValue(text);
/*  632 */       if (nan != null) {
/*  633 */         return nan;
/*      */       }
/*      */       
/*  636 */       CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  637 */       if (act == CoercionAction.AsNull) {
/*  638 */         return getNullValue(ctxt);
/*      */       }
/*  640 */       if (act == CoercionAction.AsEmpty) {
/*  641 */         return (Float)getEmptyValue(ctxt);
/*      */       }
/*  643 */       String text = text.trim();
/*  644 */       if (_checkTextualNull(ctxt, text)) {
/*  645 */         return getNullValue(ctxt);
/*      */       }
/*      */       try {
/*  648 */         return Float.valueOf(Float.parseFloat(text));
/*  649 */       } catch (IllegalArgumentException illegalArgumentException) {
/*  650 */         return (Float)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid `Float` value", new Object[0]);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static class DoubleDeserializer
/*      */     extends PrimitiveOrWrapperDeserializer<Double>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*  661 */     static final DoubleDeserializer primitiveInstance = new DoubleDeserializer((Class)double.class, Double.valueOf(0.0D));
/*  662 */     static final DoubleDeserializer wrapperInstance = new DoubleDeserializer(Double.class, null);
/*      */     
/*      */     public DoubleDeserializer(Class<Double> cls, Double nvl) {
/*  665 */       super(cls, LogicalType.Float, nvl, Double.valueOf(0.0D));
/*      */     }
/*      */ 
/*      */     
/*      */     public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  670 */       if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
/*  671 */         return Double.valueOf(p.getDoubleValue());
/*      */       }
/*  673 */       if (this._primitive) {
/*  674 */         return Double.valueOf(_parseDoublePrimitive(p, ctxt));
/*      */       }
/*  676 */       return _parseDouble(p, ctxt);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Double deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/*  685 */       if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
/*  686 */         return Double.valueOf(p.getDoubleValue());
/*      */       }
/*  688 */       if (this._primitive) {
/*  689 */         return Double.valueOf(_parseDoublePrimitive(p, ctxt));
/*      */       }
/*  691 */       return _parseDouble(p, ctxt);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected final Double _parseDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  697 */       switch (p.currentTokenId()) {
/*      */         case 6:
/*  699 */           text = p.getText();
/*      */           break;
/*      */         case 11:
/*  702 */           return getNullValue(ctxt);
/*      */         case 7:
/*      */         case 8:
/*  705 */           return Double.valueOf(p.getDoubleValue());
/*      */         
/*      */         case 1:
/*  708 */           text = ctxt.extractScalarFromObject(p, this, this._valueClass);
/*      */           break;
/*      */         case 3:
/*  711 */           return _deserializeFromArray(p, ctxt);
/*      */         default:
/*  713 */           return (Double)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  720 */       Double nan = _checkDoubleSpecialValue(text);
/*  721 */       if (nan != null) {
/*  722 */         return nan;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  727 */       CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  728 */       if (act == CoercionAction.AsNull) {
/*  729 */         return getNullValue(ctxt);
/*      */       }
/*  731 */       if (act == CoercionAction.AsEmpty) {
/*  732 */         return (Double)getEmptyValue(ctxt);
/*      */       }
/*  734 */       String text = text.trim();
/*  735 */       if (_checkTextualNull(ctxt, text)) {
/*  736 */         return getNullValue(ctxt);
/*      */       }
/*      */       try {
/*  739 */         return Double.valueOf(_parseDouble(text));
/*  740 */       } catch (IllegalArgumentException illegalArgumentException) {
/*  741 */         return (Double)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid `Double` value", new Object[0]);
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
/*      */   @JacksonStdImpl
/*      */   public static class NumberDeserializer
/*      */     extends StdScalarDeserializer<Object>
/*      */   {
/*  761 */     public static final NumberDeserializer instance = new NumberDeserializer();
/*      */     
/*      */     public NumberDeserializer() {
/*  764 */       super(Number.class);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final LogicalType logicalType() {
/*  770 */       return LogicalType.Integer;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  777 */       switch (p.currentTokenId()) {
/*      */         case 6:
/*  779 */           text = p.getText();
/*      */           break;
/*      */         case 7:
/*  782 */           if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
/*  783 */             return _coerceIntegral(p, ctxt);
/*      */           }
/*  785 */           return p.getNumberValue();
/*      */         
/*      */         case 8:
/*  788 */           if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS))
/*      */           {
/*  790 */             if (!p.isNaN()) {
/*  791 */               return p.getDecimalValue();
/*      */             }
/*      */           }
/*  794 */           return p.getNumberValue();
/*      */         
/*      */         case 1:
/*  797 */           text = ctxt.extractScalarFromObject(p, this, this._valueClass);
/*      */           break;
/*      */         case 3:
/*  800 */           return _deserializeFromArray(p, ctxt);
/*      */         default:
/*  802 */           return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  807 */       CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  808 */       if (act == CoercionAction.AsNull) {
/*  809 */         return getNullValue(ctxt);
/*      */       }
/*  811 */       if (act == CoercionAction.AsEmpty) {
/*  812 */         return getEmptyValue(ctxt);
/*      */       }
/*  814 */       String text = text.trim();
/*  815 */       if (_hasTextualNull(text))
/*      */       {
/*  817 */         return getNullValue(ctxt);
/*      */       }
/*  819 */       if (_isPosInf(text)) {
/*  820 */         return Double.valueOf(Double.POSITIVE_INFINITY);
/*      */       }
/*  822 */       if (_isNegInf(text)) {
/*  823 */         return Double.valueOf(Double.NEGATIVE_INFINITY);
/*      */       }
/*  825 */       if (_isNaN(text)) {
/*  826 */         return Double.valueOf(Double.NaN);
/*      */       }
/*      */       try {
/*  829 */         if (!_isIntNumber(text)) {
/*  830 */           if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/*  831 */             return new BigDecimal(text);
/*      */           }
/*  833 */           return Double.valueOf(text);
/*      */         } 
/*  835 */         if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
/*  836 */           return new BigInteger(text);
/*      */         }
/*  838 */         long value = Long.parseLong(text);
/*  839 */         if (!ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS) && 
/*  840 */           value <= 2147483647L && value >= -2147483648L) {
/*  841 */           return Integer.valueOf((int)value);
/*      */         }
/*      */         
/*  844 */         return Long.valueOf(value);
/*  845 */       } catch (IllegalArgumentException iae) {
/*  846 */         return ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid number", new Object[0]);
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
/*      */ 
/*      */     
/*      */     public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/*  862 */       switch (p.currentTokenId()) {
/*      */         
/*      */         case 6:
/*      */         case 7:
/*      */         case 8:
/*  867 */           return deserialize(p, ctxt);
/*      */       } 
/*  869 */       return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
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
/*      */   @JacksonStdImpl
/*      */   public static class BigIntegerDeserializer
/*      */     extends StdScalarDeserializer<BigInteger>
/*      */   {
/*  888 */     public static final BigIntegerDeserializer instance = new BigIntegerDeserializer();
/*      */     public BigIntegerDeserializer() {
/*  890 */       super(BigInteger.class);
/*      */     }
/*      */     
/*      */     public Object getEmptyValue(DeserializationContext ctxt) {
/*  894 */       return BigInteger.ZERO;
/*      */     }
/*      */ 
/*      */     
/*      */     public final LogicalType logicalType() {
/*  899 */       return LogicalType.Integer;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  905 */       if (p.isExpectedNumberIntToken()) {
/*  906 */         return p.getBigIntegerValue();
/*      */       }
/*      */ 
/*      */       
/*  910 */       switch (p.currentTokenId()) {
/*      */         case 6:
/*  912 */           text = p.getText();
/*      */           break;
/*      */         case 8:
/*  915 */           act = _checkFloatToIntCoercion(p, ctxt, this._valueClass);
/*  916 */           if (act == CoercionAction.AsNull) {
/*  917 */             return (BigInteger)getNullValue(ctxt);
/*      */           }
/*  919 */           if (act == CoercionAction.AsEmpty) {
/*  920 */             return (BigInteger)getEmptyValue(ctxt);
/*      */           }
/*  922 */           return p.getDecimalValue().toBigInteger();
/*      */         
/*      */         case 1:
/*  925 */           text = ctxt.extractScalarFromObject(p, this, this._valueClass);
/*      */           break;
/*      */         case 3:
/*  928 */           return _deserializeFromArray(p, ctxt);
/*      */         
/*      */         default:
/*  931 */           return (BigInteger)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */       } 
/*      */       
/*  934 */       CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  935 */       if (act == CoercionAction.AsNull) {
/*  936 */         return (BigInteger)getNullValue(ctxt);
/*      */       }
/*  938 */       if (act == CoercionAction.AsEmpty) {
/*  939 */         return (BigInteger)getEmptyValue(ctxt);
/*      */       }
/*  941 */       String text = text.trim();
/*  942 */       if (_hasTextualNull(text))
/*      */       {
/*  944 */         return (BigInteger)getNullValue(ctxt);
/*      */       }
/*      */       try {
/*  947 */         return new BigInteger(text);
/*  948 */       } catch (IllegalArgumentException illegalArgumentException) {
/*  949 */         return (BigInteger)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @JacksonStdImpl
/*      */   public static class BigDecimalDeserializer
/*      */     extends StdScalarDeserializer<BigDecimal>
/*      */   {
/*  959 */     public static final BigDecimalDeserializer instance = new BigDecimalDeserializer();
/*      */     public BigDecimalDeserializer() {
/*  961 */       super(BigDecimal.class);
/*      */     }
/*      */     
/*      */     public Object getEmptyValue(DeserializationContext ctxt) {
/*  965 */       return BigDecimal.ZERO;
/*      */     }
/*      */ 
/*      */     
/*      */     public final LogicalType logicalType() {
/*  970 */       return LogicalType.Float;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  978 */       switch (p.currentTokenId()) {
/*      */         case 7:
/*      */         case 8:
/*  981 */           return p.getDecimalValue();
/*      */         case 6:
/*  983 */           text = p.getText();
/*      */           break;
/*      */         
/*      */         case 1:
/*  987 */           text = ctxt.extractScalarFromObject(p, this, this._valueClass);
/*      */           break;
/*      */         case 3:
/*  990 */           return _deserializeFromArray(p, ctxt);
/*      */         default:
/*  992 */           return (BigDecimal)ctxt.handleUnexpectedToken(getValueType(ctxt), p);
/*      */       } 
/*      */       
/*  995 */       CoercionAction act = _checkFromStringCoercion(ctxt, text);
/*  996 */       if (act == CoercionAction.AsNull) {
/*  997 */         return (BigDecimal)getNullValue(ctxt);
/*      */       }
/*  999 */       if (act == CoercionAction.AsEmpty) {
/* 1000 */         return (BigDecimal)getEmptyValue(ctxt);
/*      */       }
/* 1002 */       String text = text.trim();
/* 1003 */       if (_hasTextualNull(text))
/*      */       {
/* 1005 */         return (BigDecimal)getNullValue(ctxt);
/*      */       }
/*      */       try {
/* 1008 */         return new BigDecimal(text);
/* 1009 */       } catch (IllegalArgumentException illegalArgumentException) {
/* 1010 */         return (BigDecimal)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/NumberDeserializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */