/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.annotation.Nulls;
/*     */ import com.fasterxml.jackson.core.Base64Variants;
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.exc.StreamReadException;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*     */ import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
/*     */ import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
/*     */ import com.fasterxml.jackson.databind.exc.InvalidNullException;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*     */ import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Arrays;
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
/*     */ public abstract class PrimitiveArrayDeserializers<T>
/*     */   extends StdDeserializer<T>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   protected final Boolean _unwrapSingle;
/*     */   private transient Object _emptyValue;
/*     */   protected final NullValueProvider _nuller;
/*     */   
/*     */   protected PrimitiveArrayDeserializers(Class<T> cls) {
/*  58 */     super(cls);
/*  59 */     this._unwrapSingle = null;
/*  60 */     this._nuller = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected PrimitiveArrayDeserializers(PrimitiveArrayDeserializers<?> base, NullValueProvider nuller, Boolean unwrapSingle) {
/*  68 */     super(base._valueClass);
/*  69 */     this._unwrapSingle = unwrapSingle;
/*  70 */     this._nuller = nuller;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonDeserializer<?> forType(Class<?> rawType) {
/*  76 */     if (rawType == int.class) {
/*  77 */       return IntDeser.instance;
/*     */     }
/*  79 */     if (rawType == long.class) {
/*  80 */       return LongDeser.instance;
/*     */     }
/*     */     
/*  83 */     if (rawType == byte.class) {
/*  84 */       return new ByteDeser();
/*     */     }
/*  86 */     if (rawType == short.class) {
/*  87 */       return new ShortDeser();
/*     */     }
/*  89 */     if (rawType == float.class) {
/*  90 */       return new FloatDeser();
/*     */     }
/*  92 */     if (rawType == double.class) {
/*  93 */       return new DoubleDeser();
/*     */     }
/*  95 */     if (rawType == boolean.class) {
/*  96 */       return new BooleanDeser();
/*     */     }
/*  98 */     if (rawType == char.class) {
/*  99 */       return new CharDeser();
/*     */     }
/* 101 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/*     */     NullsFailProvider nullsFailProvider;
/* 108 */     Boolean unwrapSingle = findFormatFeature(ctxt, property, this._valueClass, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
/*     */     
/* 110 */     NullValueProvider nuller = null;
/*     */     
/* 112 */     Nulls nullStyle = findContentNullStyle(ctxt, property);
/* 113 */     if (nullStyle == Nulls.SKIP) {
/* 114 */       NullsConstantProvider nullsConstantProvider = NullsConstantProvider.skipper();
/* 115 */     } else if (nullStyle == Nulls.FAIL) {
/* 116 */       if (property == null) {
/*     */         
/* 118 */         nullsFailProvider = NullsFailProvider.constructForRootValue(ctxt.constructType(this._valueClass.getComponentType()));
/*     */       } else {
/*     */         
/* 121 */         nullsFailProvider = NullsFailProvider.constructForProperty(property, property.getType().getContentType());
/*     */       } 
/*     */     } 
/* 124 */     if (Objects.equals(unwrapSingle, this._unwrapSingle) && nullsFailProvider == this._nuller) {
/* 125 */       return this;
/*     */     }
/* 127 */     return withResolved((NullValueProvider)nullsFailProvider, unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract T _concat(T paramT1, T paramT2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract T handleSingleElementUnwrapped(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext) throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract PrimitiveArrayDeserializers<?> withResolved(NullValueProvider paramNullValueProvider, Boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract T _constructEmpty();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 161 */     return LogicalType.Array;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 166 */     return Boolean.TRUE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AccessPattern getEmptyAccessPattern() {
/* 172 */     return AccessPattern.CONSTANT;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 177 */     Object empty = this._emptyValue;
/* 178 */     if (empty == null) {
/* 179 */       this._emptyValue = empty = _constructEmpty();
/*     */     }
/* 181 */     return empty;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 190 */     return typeDeserializer.deserializeTypedFromArray(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt, T existing) throws IOException {
/* 196 */     T newValue = (T)deserialize(p, ctxt);
/* 197 */     if (existing == null) {
/* 198 */       return newValue;
/*     */     }
/* 200 */     int len = Array.getLength(existing);
/* 201 */     if (len == 0) {
/* 202 */       return newValue;
/*     */     }
/* 204 */     return _concat(existing, newValue);
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
/*     */   protected T handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 217 */     if (p.hasToken(JsonToken.VALUE_STRING)) {
/* 218 */       return _deserializeFromString(p, ctxt);
/*     */     }
/*     */ 
/*     */     
/* 222 */     boolean canWrap = (this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
/* 223 */     if (canWrap) {
/* 224 */       return handleSingleElementUnwrapped(p, ctxt);
/*     */     }
/* 226 */     return (T)ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _failOnNull(DeserializationContext ctxt) throws IOException {
/* 231 */     throw InvalidNullException.from(ctxt, null, ctxt.constructType(this._valueClass));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class CharDeser
/*     */     extends PrimitiveArrayDeserializers<char[]>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */     
/*     */     public CharDeser() {
/* 246 */       super((Class)char[].class);
/*     */     } protected CharDeser(CharDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
/* 248 */       super(base, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
/* 255 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     protected char[] _constructEmpty() {
/* 260 */       return new char[0];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public char[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 270 */       if (p.hasToken(JsonToken.VALUE_STRING)) {
/*     */         
/* 272 */         char[] buffer = p.getTextCharacters();
/* 273 */         int offset = p.getTextOffset();
/* 274 */         int len = p.getTextLength();
/*     */         
/* 276 */         char[] result = new char[len];
/* 277 */         System.arraycopy(buffer, offset, result, 0, len);
/* 278 */         return result;
/*     */       } 
/* 280 */       if (p.isExpectedStartArrayToken()) {
/*     */         
/* 282 */         StringBuilder sb = new StringBuilder(64);
/*     */         JsonToken t;
/* 284 */         while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */           String str;
/* 286 */           if (t == JsonToken.VALUE_STRING) {
/* 287 */             str = p.getText();
/* 288 */           } else if (t == JsonToken.VALUE_NULL) {
/* 289 */             if (this._nuller != null) {
/* 290 */               this._nuller.getNullValue(ctxt);
/*     */               continue;
/*     */             } 
/* 293 */             _verifyNullForPrimitive(ctxt);
/* 294 */             str = "\000";
/*     */           } else {
/* 296 */             CharSequence cs = (CharSequence)ctxt.handleUnexpectedToken(char.class, p);
/* 297 */             str = cs.toString();
/*     */           } 
/* 299 */           if (str.length() != 1)
/* 300 */             ctxt.reportInputMismatch(this, "Cannot convert a JSON String of length %d into a char element of char array", new Object[] {
/* 301 */                   Integer.valueOf(str.length())
/*     */                 }); 
/* 303 */           sb.append(str.charAt(0));
/*     */         } 
/* 305 */         return sb.toString().toCharArray();
/*     */       } 
/*     */       
/* 308 */       if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
/* 309 */         Object ob = p.getEmbeddedObject();
/* 310 */         if (ob == null) return null; 
/* 311 */         if (ob instanceof char[]) {
/* 312 */           return (char[])ob;
/*     */         }
/* 314 */         if (ob instanceof String) {
/* 315 */           return ((String)ob).toCharArray();
/*     */         }
/*     */         
/* 318 */         if (ob instanceof byte[]) {
/* 319 */           return Base64Variants.getDefaultVariant().encode((byte[])ob, false).toCharArray();
/*     */         }
/*     */       } 
/*     */       
/* 323 */       return (char[])ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected char[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 330 */       return (char[])ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */     }
/*     */ 
/*     */     
/*     */     protected char[] _concat(char[] oldValue, char[] newValue) {
/* 335 */       int len1 = oldValue.length;
/* 336 */       int len2 = newValue.length;
/* 337 */       char[] result = Arrays.copyOf(oldValue, len1 + len2);
/* 338 */       System.arraycopy(newValue, 0, result, len1, len2);
/* 339 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class BooleanDeser
/*     */     extends PrimitiveArrayDeserializers<boolean[]>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */     
/*     */     public BooleanDeser() {
/* 355 */       super((Class)boolean[].class);
/*     */     } protected BooleanDeser(BooleanDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
/* 357 */       super(base, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
/* 363 */       return new BooleanDeser(this, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean[] _constructEmpty() {
/* 368 */       return new boolean[0];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 375 */       if (!p.isExpectedStartArrayToken()) {
/* 376 */         return handleNonArray(p, ctxt);
/*     */       }
/* 378 */       ArrayBuilders.BooleanBuilder builder = ctxt.getArrayBuilders().getBooleanBuilder();
/* 379 */       boolean[] chunk = (boolean[])builder.resetAndStart();
/* 380 */       int ix = 0;
/*     */       
/*     */       try {
/*     */         JsonToken t;
/* 384 */         while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */           boolean value;
/* 386 */           if (t == JsonToken.VALUE_TRUE) {
/* 387 */             value = true;
/* 388 */           } else if (t == JsonToken.VALUE_FALSE) {
/* 389 */             value = false;
/* 390 */           } else if (t == JsonToken.VALUE_NULL) {
/* 391 */             if (this._nuller != null) {
/* 392 */               this._nuller.getNullValue(ctxt);
/*     */               continue;
/*     */             } 
/* 395 */             _verifyNullForPrimitive(ctxt);
/* 396 */             value = false;
/*     */           } else {
/* 398 */             value = _parseBooleanPrimitive(p, ctxt);
/*     */           } 
/* 400 */           if (ix >= chunk.length) {
/* 401 */             chunk = (boolean[])builder.appendCompletedChunk(chunk, ix);
/* 402 */             ix = 0;
/*     */           } 
/* 404 */           chunk[ix++] = value;
/*     */         } 
/* 406 */       } catch (Exception e) {
/* 407 */         throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
/*     */       } 
/* 409 */       return (boolean[])builder.completeAndClearBuffer(chunk, ix);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected boolean[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 415 */       return new boolean[] { _parseBooleanPrimitive(p, ctxt) };
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean[] _concat(boolean[] oldValue, boolean[] newValue) {
/* 420 */       int len1 = oldValue.length;
/* 421 */       int len2 = newValue.length;
/* 422 */       boolean[] result = Arrays.copyOf(oldValue, len1 + len2);
/* 423 */       System.arraycopy(newValue, 0, result, len1, len2);
/* 424 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class ByteDeser
/*     */     extends PrimitiveArrayDeserializers<byte[]>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     
/*     */     public ByteDeser() {
/* 438 */       super((Class)byte[].class);
/*     */     } protected ByteDeser(ByteDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
/* 440 */       super(base, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
/* 446 */       return new ByteDeser(this, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     protected byte[] _constructEmpty() {
/* 451 */       return new byte[0];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public LogicalType logicalType() {
/* 458 */       return LogicalType.Binary;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 464 */       JsonToken t = p.currentToken();
/*     */ 
/*     */       
/* 467 */       if (t == JsonToken.VALUE_STRING) {
/*     */         try {
/* 469 */           return p.getBinaryValue(ctxt.getBase64Variant());
/* 470 */         } catch (StreamReadException e) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 475 */           String msg = e.getOriginalMessage();
/* 476 */           if (msg.contains("base64")) {
/* 477 */             return (byte[])ctxt.handleWeirdStringValue(byte[].class, p
/* 478 */                 .getText(), msg, new Object[0]);
/*     */           }
/*     */         } 
/*     */       }
/*     */       
/* 483 */       if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 484 */         Object ob = p.getEmbeddedObject();
/* 485 */         if (ob == null) return null; 
/* 486 */         if (ob instanceof byte[]) {
/* 487 */           return (byte[])ob;
/*     */         }
/*     */       } 
/* 490 */       if (!p.isExpectedStartArrayToken()) {
/* 491 */         return handleNonArray(p, ctxt);
/*     */       }
/* 493 */       ArrayBuilders.ByteBuilder builder = ctxt.getArrayBuilders().getByteBuilder();
/* 494 */       byte[] chunk = (byte[])builder.resetAndStart();
/* 495 */       int ix = 0;
/*     */       
/*     */       try {
/* 498 */         while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */           byte value;
/*     */           
/* 501 */           if (t == JsonToken.VALUE_NUMBER_INT) {
/* 502 */             value = p.getByteValue();
/*     */           
/*     */           }
/* 505 */           else if (t == JsonToken.VALUE_NULL) {
/* 506 */             if (this._nuller != null) {
/* 507 */               this._nuller.getNullValue(ctxt);
/*     */               continue;
/*     */             } 
/* 510 */             _verifyNullForPrimitive(ctxt);
/* 511 */             value = 0;
/*     */           } else {
/* 513 */             value = _parseBytePrimitive(p, ctxt);
/*     */           } 
/*     */           
/* 516 */           if (ix >= chunk.length) {
/* 517 */             chunk = (byte[])builder.appendCompletedChunk(chunk, ix);
/* 518 */             ix = 0;
/*     */           } 
/* 520 */           chunk[ix++] = value;
/*     */         } 
/* 522 */       } catch (Exception e) {
/* 523 */         throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
/*     */       } 
/* 525 */       return (byte[])builder.completeAndClearBuffer(chunk, ix);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected byte[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/*     */       byte value;
/* 533 */       JsonToken t = p.currentToken();
/* 534 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/* 535 */         value = p.getByteValue();
/*     */       } else {
/*     */         
/* 538 */         if (t == JsonToken.VALUE_NULL) {
/* 539 */           if (this._nuller != null) {
/* 540 */             this._nuller.getNullValue(ctxt);
/* 541 */             return (byte[])getEmptyValue(ctxt);
/*     */           } 
/* 543 */           _verifyNullForPrimitive(ctxt);
/* 544 */           return null;
/*     */         } 
/* 546 */         Number n = (Number)ctxt.handleUnexpectedToken(this._valueClass.getComponentType(), p);
/* 547 */         value = n.byteValue();
/*     */       } 
/* 549 */       return new byte[] { value };
/*     */     }
/*     */ 
/*     */     
/*     */     protected byte[] _concat(byte[] oldValue, byte[] newValue) {
/* 554 */       int len1 = oldValue.length;
/* 555 */       int len2 = newValue.length;
/* 556 */       byte[] result = Arrays.copyOf(oldValue, len1 + len2);
/* 557 */       System.arraycopy(newValue, 0, result, len1, len2);
/* 558 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class ShortDeser
/*     */     extends PrimitiveArrayDeserializers<short[]> {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     public ShortDeser() {
/* 568 */       super((Class)short[].class);
/*     */     } protected ShortDeser(ShortDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
/* 570 */       super(base, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
/* 576 */       return new ShortDeser(this, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     protected short[] _constructEmpty() {
/* 581 */       return new short[0];
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public short[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 587 */       if (!p.isExpectedStartArrayToken()) {
/* 588 */         return handleNonArray(p, ctxt);
/*     */       }
/* 590 */       ArrayBuilders.ShortBuilder builder = ctxt.getArrayBuilders().getShortBuilder();
/* 591 */       short[] chunk = (short[])builder.resetAndStart();
/* 592 */       int ix = 0;
/*     */       
/*     */       try {
/*     */         JsonToken t;
/* 596 */         while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */           short value;
/* 598 */           if (t == JsonToken.VALUE_NULL) {
/* 599 */             if (this._nuller != null) {
/* 600 */               this._nuller.getNullValue(ctxt);
/*     */               continue;
/*     */             } 
/* 603 */             _verifyNullForPrimitive(ctxt);
/* 604 */             value = 0;
/*     */           } else {
/* 606 */             value = _parseShortPrimitive(p, ctxt);
/*     */           } 
/* 608 */           if (ix >= chunk.length) {
/* 609 */             chunk = (short[])builder.appendCompletedChunk(chunk, ix);
/* 610 */             ix = 0;
/*     */           } 
/* 612 */           chunk[ix++] = value;
/*     */         } 
/* 614 */       } catch (Exception e) {
/* 615 */         throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
/*     */       } 
/* 617 */       return (short[])builder.completeAndClearBuffer(chunk, ix);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected short[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 623 */       return new short[] { _parseShortPrimitive(p, ctxt) };
/*     */     }
/*     */ 
/*     */     
/*     */     protected short[] _concat(short[] oldValue, short[] newValue) {
/* 628 */       int len1 = oldValue.length;
/* 629 */       int len2 = newValue.length;
/* 630 */       short[] result = Arrays.copyOf(oldValue, len1 + len2);
/* 631 */       System.arraycopy(newValue, 0, result, len1, len2);
/* 632 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class IntDeser
/*     */     extends PrimitiveArrayDeserializers<int[]>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 642 */     public static final IntDeser instance = new IntDeser();
/*     */     public IntDeser() {
/* 644 */       super((Class)int[].class);
/*     */     } protected IntDeser(IntDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
/* 646 */       super(base, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
/* 652 */       return new IntDeser(this, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     protected int[] _constructEmpty() {
/* 657 */       return new int[0];
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 663 */       if (!p.isExpectedStartArrayToken()) {
/* 664 */         return handleNonArray(p, ctxt);
/*     */       }
/* 666 */       ArrayBuilders.IntBuilder builder = ctxt.getArrayBuilders().getIntBuilder();
/* 667 */       int[] chunk = (int[])builder.resetAndStart();
/* 668 */       int ix = 0;
/*     */       
/*     */       try {
/*     */         JsonToken t;
/* 672 */         while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */           int value;
/* 674 */           if (t == JsonToken.VALUE_NUMBER_INT) {
/* 675 */             value = p.getIntValue();
/* 676 */           } else if (t == JsonToken.VALUE_NULL) {
/* 677 */             if (this._nuller != null) {
/* 678 */               this._nuller.getNullValue(ctxt);
/*     */               continue;
/*     */             } 
/* 681 */             _verifyNullForPrimitive(ctxt);
/* 682 */             value = 0;
/*     */           } else {
/* 684 */             value = _parseIntPrimitive(p, ctxt);
/*     */           } 
/* 686 */           if (ix >= chunk.length) {
/* 687 */             chunk = (int[])builder.appendCompletedChunk(chunk, ix);
/* 688 */             ix = 0;
/*     */           } 
/* 690 */           chunk[ix++] = value;
/*     */         } 
/* 692 */       } catch (Exception e) {
/* 693 */         throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
/*     */       } 
/* 695 */       return (int[])builder.completeAndClearBuffer(chunk, ix);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected int[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 701 */       return new int[] { _parseIntPrimitive(p, ctxt) };
/*     */     }
/*     */ 
/*     */     
/*     */     protected int[] _concat(int[] oldValue, int[] newValue) {
/* 706 */       int len1 = oldValue.length;
/* 707 */       int len2 = newValue.length;
/* 708 */       int[] result = Arrays.copyOf(oldValue, len1 + len2);
/* 709 */       System.arraycopy(newValue, 0, result, len1, len2);
/* 710 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class LongDeser
/*     */     extends PrimitiveArrayDeserializers<long[]>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 720 */     public static final LongDeser instance = new LongDeser();
/*     */     public LongDeser() {
/* 722 */       super((Class)long[].class);
/*     */     } protected LongDeser(LongDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
/* 724 */       super(base, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
/* 730 */       return new LongDeser(this, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     protected long[] _constructEmpty() {
/* 735 */       return new long[0];
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 741 */       if (!p.isExpectedStartArrayToken()) {
/* 742 */         return handleNonArray(p, ctxt);
/*     */       }
/* 744 */       ArrayBuilders.LongBuilder builder = ctxt.getArrayBuilders().getLongBuilder();
/* 745 */       long[] chunk = (long[])builder.resetAndStart();
/* 746 */       int ix = 0;
/*     */       
/*     */       try {
/*     */         JsonToken t;
/* 750 */         while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */           long value;
/* 752 */           if (t == JsonToken.VALUE_NUMBER_INT) {
/* 753 */             value = p.getLongValue();
/* 754 */           } else if (t == JsonToken.VALUE_NULL) {
/* 755 */             if (this._nuller != null) {
/* 756 */               this._nuller.getNullValue(ctxt);
/*     */               continue;
/*     */             } 
/* 759 */             _verifyNullForPrimitive(ctxt);
/* 760 */             value = 0L;
/*     */           } else {
/* 762 */             value = _parseLongPrimitive(p, ctxt);
/*     */           } 
/* 764 */           if (ix >= chunk.length) {
/* 765 */             chunk = (long[])builder.appendCompletedChunk(chunk, ix);
/* 766 */             ix = 0;
/*     */           } 
/* 768 */           chunk[ix++] = value;
/*     */         } 
/* 770 */       } catch (Exception e) {
/* 771 */         throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
/*     */       } 
/* 773 */       return (long[])builder.completeAndClearBuffer(chunk, ix);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected long[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 779 */       return new long[] { _parseLongPrimitive(p, ctxt) };
/*     */     }
/*     */ 
/*     */     
/*     */     protected long[] _concat(long[] oldValue, long[] newValue) {
/* 784 */       int len1 = oldValue.length;
/* 785 */       int len2 = newValue.length;
/* 786 */       long[] result = Arrays.copyOf(oldValue, len1 + len2);
/* 787 */       System.arraycopy(newValue, 0, result, len1, len2);
/* 788 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class FloatDeser
/*     */     extends PrimitiveArrayDeserializers<float[]> {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     public FloatDeser() {
/* 798 */       super((Class)float[].class);
/*     */     } protected FloatDeser(FloatDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
/* 800 */       super(base, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
/* 806 */       return new FloatDeser(this, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     protected float[] _constructEmpty() {
/* 811 */       return new float[0];
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public float[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 817 */       if (!p.isExpectedStartArrayToken()) {
/* 818 */         return handleNonArray(p, ctxt);
/*     */       }
/* 820 */       ArrayBuilders.FloatBuilder builder = ctxt.getArrayBuilders().getFloatBuilder();
/* 821 */       float[] chunk = (float[])builder.resetAndStart();
/* 822 */       int ix = 0;
/*     */       
/*     */       try {
/*     */         JsonToken t;
/* 826 */         while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */           
/* 828 */           if (t == JsonToken.VALUE_NULL && 
/* 829 */             this._nuller != null) {
/* 830 */             this._nuller.getNullValue(ctxt);
/*     */             
/*     */             continue;
/*     */           } 
/* 834 */           float value = _parseFloatPrimitive(p, ctxt);
/* 835 */           if (ix >= chunk.length) {
/* 836 */             chunk = (float[])builder.appendCompletedChunk(chunk, ix);
/* 837 */             ix = 0;
/*     */           } 
/* 839 */           chunk[ix++] = value;
/*     */         } 
/* 841 */       } catch (Exception e) {
/* 842 */         throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
/*     */       } 
/* 844 */       return (float[])builder.completeAndClearBuffer(chunk, ix);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected float[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 850 */       return new float[] { _parseFloatPrimitive(p, ctxt) };
/*     */     }
/*     */ 
/*     */     
/*     */     protected float[] _concat(float[] oldValue, float[] newValue) {
/* 855 */       int len1 = oldValue.length;
/* 856 */       int len2 = newValue.length;
/* 857 */       float[] result = Arrays.copyOf(oldValue, len1 + len2);
/* 858 */       System.arraycopy(newValue, 0, result, len1, len2);
/* 859 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class DoubleDeser
/*     */     extends PrimitiveArrayDeserializers<double[]> {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     public DoubleDeser() {
/* 869 */       super((Class)double[].class);
/*     */     } protected DoubleDeser(DoubleDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
/* 871 */       super(base, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
/* 877 */       return new DoubleDeser(this, nuller, unwrapSingle);
/*     */     }
/*     */ 
/*     */     
/*     */     protected double[] _constructEmpty() {
/* 882 */       return new double[0];
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public double[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 888 */       if (!p.isExpectedStartArrayToken()) {
/* 889 */         return handleNonArray(p, ctxt);
/*     */       }
/* 891 */       ArrayBuilders.DoubleBuilder builder = ctxt.getArrayBuilders().getDoubleBuilder();
/* 892 */       double[] chunk = (double[])builder.resetAndStart();
/* 893 */       int ix = 0;
/*     */       
/*     */       try {
/*     */         JsonToken t;
/* 897 */         while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/* 898 */           if (t == JsonToken.VALUE_NULL && 
/* 899 */             this._nuller != null) {
/* 900 */             this._nuller.getNullValue(ctxt);
/*     */             
/*     */             continue;
/*     */           } 
/* 904 */           double value = _parseDoublePrimitive(p, ctxt);
/* 905 */           if (ix >= chunk.length) {
/* 906 */             chunk = (double[])builder.appendCompletedChunk(chunk, ix);
/* 907 */             ix = 0;
/*     */           } 
/* 909 */           chunk[ix++] = value;
/*     */         } 
/* 911 */       } catch (Exception e) {
/* 912 */         throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
/*     */       } 
/* 914 */       return (double[])builder.completeAndClearBuffer(chunk, ix);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected double[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 920 */       return new double[] { _parseDoublePrimitive(p, ctxt) };
/*     */     }
/*     */ 
/*     */     
/*     */     protected double[] _concat(double[] oldValue, double[] newValue) {
/* 925 */       int len1 = oldValue.length;
/* 926 */       int len2 = newValue.length;
/* 927 */       double[] result = Arrays.copyOf(oldValue, len1 + len2);
/* 928 */       System.arraycopy(newValue, 0, result, len1, len2);
/* 929 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */