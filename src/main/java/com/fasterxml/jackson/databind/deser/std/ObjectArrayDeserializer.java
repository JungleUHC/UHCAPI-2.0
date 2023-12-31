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
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.ArrayType;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*     */ import com.fasterxml.jackson.databind.util.ObjectBuffer;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Array;
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
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class ObjectArrayDeserializer
/*     */   extends ContainerDeserializerBase<Object[]>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final boolean _untyped;
/*     */   protected final Class<?> _elementClass;
/*     */   protected JsonDeserializer<Object> _elementDeserializer;
/*     */   protected final TypeDeserializer _elementTypeDeserializer;
/*     */   protected final Object[] _emptyValue;
/*     */   
/*     */   public ObjectArrayDeserializer(JavaType arrayType0, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser) {
/*  70 */     super(arrayType0, (NullValueProvider)null, (Boolean)null);
/*  71 */     ArrayType arrayType = (ArrayType)arrayType0;
/*  72 */     this._elementClass = arrayType.getContentType().getRawClass();
/*  73 */     this._untyped = (this._elementClass == Object.class);
/*  74 */     this._elementDeserializer = elemDeser;
/*  75 */     this._elementTypeDeserializer = elemTypeDeser;
/*  76 */     this._emptyValue = arrayType.getEmptyArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ObjectArrayDeserializer(ObjectArrayDeserializer base, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser, NullValueProvider nuller, Boolean unwrapSingle) {
/*  83 */     super(base, nuller, unwrapSingle);
/*  84 */     this._elementClass = base._elementClass;
/*  85 */     this._untyped = base._untyped;
/*  86 */     this._emptyValue = base._emptyValue;
/*     */     
/*  88 */     this._elementDeserializer = elemDeser;
/*  89 */     this._elementTypeDeserializer = elemTypeDeser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectArrayDeserializer withDeserializer(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser) {
/*  98 */     return withResolved(elemTypeDeser, elemDeser, this._nullProvider, this._unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectArrayDeserializer withResolved(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser, NullValueProvider nuller, Boolean unwrapSingle) {
/* 109 */     if (Objects.equals(unwrapSingle, this._unwrapSingle) && nuller == this._nullProvider && elemDeser == this._elementDeserializer && elemTypeDeser == this._elementTypeDeserializer)
/*     */     {
/*     */       
/* 112 */       return this;
/*     */     }
/* 114 */     return new ObjectArrayDeserializer(this, (JsonDeserializer)elemDeser, elemTypeDeser, nuller, unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCachable() {
/* 123 */     return (this._elementDeserializer == null && this._elementTypeDeserializer == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 128 */     return LogicalType.Array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 135 */     JsonDeserializer<?> valueDeser = this._elementDeserializer;
/*     */ 
/*     */ 
/*     */     
/* 139 */     Boolean unwrapSingle = findFormatFeature(ctxt, property, this._containerType.getRawClass(), JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
/*     */ 
/*     */     
/* 142 */     valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
/* 143 */     JavaType vt = this._containerType.getContentType();
/* 144 */     if (valueDeser == null) {
/* 145 */       valueDeser = ctxt.findContextualValueDeserializer(vt, property);
/*     */     } else {
/* 147 */       valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
/*     */     } 
/* 149 */     TypeDeserializer elemTypeDeser = this._elementTypeDeserializer;
/* 150 */     if (elemTypeDeser != null) {
/* 151 */       elemTypeDeser = elemTypeDeser.forProperty(property);
/*     */     }
/* 153 */     NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);
/* 154 */     return withResolved(elemTypeDeser, valueDeser, nuller, unwrapSingle);
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
/* 165 */     return this._elementDeserializer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AccessPattern getEmptyAccessPattern() {
/* 171 */     return AccessPattern.CONSTANT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 179 */     return this._emptyValue;
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
/*     */   public Object[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*     */     Object[] result;
/* 193 */     if (!p.isExpectedStartArrayToken()) {
/* 194 */       return handleNonArray(p, ctxt);
/*     */     }
/*     */     
/* 197 */     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/* 198 */     Object[] chunk = buffer.resetAndStart();
/* 199 */     int ix = 0;
/*     */     
/* 201 */     TypeDeserializer typeDeser = this._elementTypeDeserializer;
/*     */     try {
/*     */       JsonToken t;
/* 204 */       while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */         Object value;
/*     */ 
/*     */         
/* 208 */         if (t == JsonToken.VALUE_NULL) {
/* 209 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 212 */           value = this._nullProvider.getNullValue(ctxt);
/* 213 */         } else if (typeDeser == null) {
/* 214 */           value = this._elementDeserializer.deserialize(p, ctxt);
/*     */         } else {
/* 216 */           value = this._elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
/*     */         } 
/* 218 */         if (ix >= chunk.length) {
/* 219 */           chunk = buffer.appendCompletedChunk(chunk);
/* 220 */           ix = 0;
/*     */         } 
/* 222 */         chunk[ix++] = value;
/*     */       } 
/* 224 */     } catch (Exception e) {
/* 225 */       throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 230 */     if (this._untyped) {
/* 231 */       result = buffer.completeAndClearBuffer(chunk, ix);
/*     */     } else {
/* 233 */       result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
/*     */     } 
/* 235 */     ctxt.returnObjectBuffer(buffer);
/* 236 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 246 */     return (Object[])typeDeserializer.deserializeTypedFromArray(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] deserialize(JsonParser p, DeserializationContext ctxt, Object[] intoValue) throws IOException {
/*     */     Object[] result;
/* 253 */     if (!p.isExpectedStartArrayToken()) {
/* 254 */       Object[] arr = handleNonArray(p, ctxt);
/* 255 */       if (arr == null) {
/* 256 */         return intoValue;
/*     */       }
/* 258 */       int offset = intoValue.length;
/* 259 */       Object[] arrayOfObject1 = new Object[offset + arr.length];
/* 260 */       System.arraycopy(intoValue, 0, arrayOfObject1, 0, offset);
/* 261 */       System.arraycopy(arr, 0, arrayOfObject1, offset, arr.length);
/* 262 */       return arrayOfObject1;
/*     */     } 
/*     */     
/* 265 */     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/* 266 */     int ix = intoValue.length;
/* 267 */     Object[] chunk = buffer.resetAndStart(intoValue, ix);
/*     */     
/* 269 */     TypeDeserializer typeDeser = this._elementTypeDeserializer;
/*     */     try {
/*     */       JsonToken t;
/* 272 */       while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */         Object value;
/*     */         
/* 275 */         if (t == JsonToken.VALUE_NULL) {
/* 276 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 279 */           value = this._nullProvider.getNullValue(ctxt);
/* 280 */         } else if (typeDeser == null) {
/* 281 */           value = this._elementDeserializer.deserialize(p, ctxt);
/*     */         } else {
/* 283 */           value = this._elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
/*     */         } 
/* 285 */         if (ix >= chunk.length) {
/* 286 */           chunk = buffer.appendCompletedChunk(chunk);
/* 287 */           ix = 0;
/*     */         } 
/* 289 */         chunk[ix++] = value;
/*     */       } 
/* 291 */     } catch (Exception e) {
/* 292 */       throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 297 */     if (this._untyped) {
/* 298 */       result = buffer.completeAndClearBuffer(chunk, ix);
/*     */     } else {
/* 300 */       result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
/*     */     } 
/* 302 */     ctxt.returnObjectBuffer(buffer);
/* 303 */     return result;
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
/*     */   protected Byte[] deserializeFromBase64(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 316 */     byte[] b = p.getBinaryValue(ctxt.getBase64Variant());
/*     */     
/* 318 */     Byte[] result = new Byte[b.length];
/* 319 */     for (int i = 0, len = b.length; i < len; i++) {
/* 320 */       result[i] = Byte.valueOf(b[i]);
/*     */     }
/* 322 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object[] handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/*     */     Object value, result[];
/* 331 */     boolean canWrap = (this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
/* 332 */     if (!canWrap) {
/*     */       
/* 334 */       if (p.hasToken(JsonToken.VALUE_STRING)) {
/*     */ 
/*     */         
/* 337 */         if (this._elementClass == Byte.class) {
/* 338 */           return (Object[])deserializeFromBase64(p, ctxt);
/*     */         }
/*     */         
/* 341 */         return _deserializeFromString(p, ctxt);
/*     */       } 
/* 343 */       return (Object[])ctxt.handleUnexpectedToken(this._containerType, p);
/*     */     } 
/*     */ 
/*     */     
/* 347 */     if (p.hasToken(JsonToken.VALUE_NULL)) {
/*     */       
/* 349 */       if (this._skipNullValues) {
/* 350 */         return this._emptyValue;
/*     */       }
/* 352 */       value = this._nullProvider.getNullValue(ctxt);
/* 353 */     } else if (this._elementTypeDeserializer == null) {
/* 354 */       value = this._elementDeserializer.deserialize(p, ctxt);
/*     */     } else {
/* 356 */       value = this._elementDeserializer.deserializeWithType(p, ctxt, this._elementTypeDeserializer);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 361 */     if (this._untyped) {
/* 362 */       result = new Object[1];
/*     */     } else {
/* 364 */       result = (Object[])Array.newInstance(this._elementClass, 1);
/*     */     } 
/* 366 */     result[0] = value;
/* 367 */     return result;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/ObjectArrayDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */