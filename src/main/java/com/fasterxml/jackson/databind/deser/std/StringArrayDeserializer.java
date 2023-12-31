/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*     */ import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*     */ import com.fasterxml.jackson.databind.util.ObjectBuffer;
/*     */ import java.io.IOException;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public final class StringArrayDeserializer
/*     */   extends StdDeserializer<String[]>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*  35 */   private static final String[] NO_STRINGS = new String[0];
/*     */   
/*  37 */   public static final StringArrayDeserializer instance = new StringArrayDeserializer();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonDeserializer<String> _elementDeserializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final NullValueProvider _nullProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Boolean _unwrapSingle;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _skipNullValues;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringArrayDeserializer() {
/*  69 */     this((JsonDeserializer<?>)null, (NullValueProvider)null, (Boolean)null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected StringArrayDeserializer(JsonDeserializer<?> deser, NullValueProvider nuller, Boolean unwrapSingle) {
/*  75 */     super(String[].class);
/*  76 */     this._elementDeserializer = (JsonDeserializer)deser;
/*  77 */     this._nullProvider = nuller;
/*  78 */     this._unwrapSingle = unwrapSingle;
/*  79 */     this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
/*     */   }
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/*  84 */     return LogicalType.Array;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/*  89 */     return Boolean.TRUE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AccessPattern getEmptyAccessPattern() {
/*  95 */     return AccessPattern.CONSTANT;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 100 */     return NO_STRINGS;
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
/* 111 */     JsonDeserializer<?> deser = this._elementDeserializer;
/*     */     
/* 113 */     deser = findConvertingContentDeserializer(ctxt, property, deser);
/* 114 */     JavaType type = ctxt.constructType(String.class);
/* 115 */     if (deser == null) {
/* 116 */       deser = ctxt.findContextualValueDeserializer(type, property);
/*     */     } else {
/* 118 */       deser = ctxt.handleSecondaryContextualization(deser, property, type);
/*     */     } 
/*     */     
/* 121 */     Boolean unwrapSingle = findFormatFeature(ctxt, property, String[].class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
/*     */     
/* 123 */     NullValueProvider nuller = findContentNullProvider(ctxt, property, deser);
/*     */     
/* 125 */     if (deser != null && isDefaultDeserializer(deser)) {
/* 126 */       deser = null;
/*     */     }
/* 128 */     if (this._elementDeserializer == deser && 
/* 129 */       Objects.equals(this._unwrapSingle, unwrapSingle) && this._nullProvider == nuller)
/*     */     {
/* 131 */       return this;
/*     */     }
/* 133 */     return new StringArrayDeserializer(deser, nuller, unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 140 */     if (!p.isExpectedStartArrayToken()) {
/* 141 */       return handleNonArray(p, ctxt);
/*     */     }
/* 143 */     if (this._elementDeserializer != null) {
/* 144 */       return _deserializeCustom(p, ctxt, (String[])null);
/*     */     }
/*     */     
/* 147 */     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/* 148 */     Object[] chunk = buffer.resetAndStart();
/*     */     
/* 150 */     int ix = 0;
/*     */     
/*     */     try {
/*     */       while (true) {
/* 154 */         String value = p.nextTextValue();
/* 155 */         if (value == null) {
/* 156 */           JsonToken t = p.currentToken();
/* 157 */           if (t == JsonToken.END_ARRAY) {
/*     */             break;
/*     */           }
/* 160 */           if (t == JsonToken.VALUE_NULL) {
/* 161 */             if (this._skipNullValues) {
/*     */               continue;
/*     */             }
/* 164 */             value = (String)this._nullProvider.getNullValue(ctxt);
/*     */           } else {
/* 166 */             value = _parseString(p, ctxt);
/*     */           } 
/*     */         } 
/* 169 */         if (ix >= chunk.length) {
/* 170 */           chunk = buffer.appendCompletedChunk(chunk);
/* 171 */           ix = 0;
/*     */         } 
/* 173 */         chunk[ix++] = value;
/*     */       } 
/* 175 */     } catch (Exception e) {
/* 176 */       throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
/*     */     } 
/* 178 */     String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
/* 179 */     ctxt.returnObjectBuffer(buffer);
/* 180 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String[] _deserializeCustom(JsonParser p, DeserializationContext ctxt, String[] old) throws IOException {
/*     */     int ix;
/*     */     Object[] chunk;
/* 189 */     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/*     */ 
/*     */ 
/*     */     
/* 193 */     if (old == null) {
/* 194 */       ix = 0;
/* 195 */       chunk = buffer.resetAndStart();
/*     */     } else {
/* 197 */       ix = old.length;
/* 198 */       chunk = buffer.resetAndStart((Object[])old, ix);
/*     */     } 
/*     */     
/* 201 */     JsonDeserializer<String> deser = this._elementDeserializer;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*     */       while (true) {
/*     */         String value;
/*     */ 
/*     */ 
/*     */         
/* 211 */         if (p.nextTextValue() == null) {
/* 212 */           JsonToken t = p.currentToken();
/* 213 */           if (t == JsonToken.END_ARRAY) {
/*     */             break;
/*     */           }
/*     */           
/* 217 */           if (t == JsonToken.VALUE_NULL) {
/* 218 */             if (this._skipNullValues) {
/*     */               continue;
/*     */             }
/* 221 */             value = (String)this._nullProvider.getNullValue(ctxt);
/*     */           } else {
/* 223 */             value = (String)deser.deserialize(p, ctxt);
/*     */           } 
/*     */         } else {
/* 226 */           value = (String)deser.deserialize(p, ctxt);
/*     */         } 
/* 228 */         if (ix >= chunk.length) {
/* 229 */           chunk = buffer.appendCompletedChunk(chunk);
/* 230 */           ix = 0;
/*     */         } 
/* 232 */         chunk[ix++] = value;
/*     */       } 
/* 234 */     } catch (Exception e) {
/*     */       
/* 236 */       throw JsonMappingException.wrapWithPath(e, String.class, ix);
/*     */     } 
/* 238 */     String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
/* 239 */     ctxt.returnObjectBuffer(buffer);
/* 240 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 245 */     return typeDeserializer.deserializeTypedFromArray(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] deserialize(JsonParser p, DeserializationContext ctxt, String[] intoValue) throws IOException {
/* 253 */     if (!p.isExpectedStartArrayToken()) {
/* 254 */       String[] arr = handleNonArray(p, ctxt);
/* 255 */       if (arr == null) {
/* 256 */         return intoValue;
/*     */       }
/* 258 */       int offset = intoValue.length;
/* 259 */       String[] arrayOfString1 = new String[offset + arr.length];
/* 260 */       System.arraycopy(intoValue, 0, arrayOfString1, 0, offset);
/* 261 */       System.arraycopy(arr, 0, arrayOfString1, offset, arr.length);
/* 262 */       return arrayOfString1;
/*     */     } 
/*     */     
/* 265 */     if (this._elementDeserializer != null) {
/* 266 */       return _deserializeCustom(p, ctxt, intoValue);
/*     */     }
/* 268 */     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/* 269 */     int ix = intoValue.length;
/* 270 */     Object[] chunk = buffer.resetAndStart((Object[])intoValue, ix);
/*     */     
/*     */     try {
/*     */       while (true) {
/* 274 */         String value = p.nextTextValue();
/* 275 */         if (value == null) {
/* 276 */           JsonToken t = p.currentToken();
/* 277 */           if (t == JsonToken.END_ARRAY) {
/*     */             break;
/*     */           }
/* 280 */           if (t == JsonToken.VALUE_NULL) {
/*     */             
/* 282 */             if (this._skipNullValues) {
/* 283 */               return NO_STRINGS;
/*     */             }
/* 285 */             value = (String)this._nullProvider.getNullValue(ctxt);
/*     */           } else {
/* 287 */             value = _parseString(p, ctxt);
/*     */           } 
/*     */         } 
/* 290 */         if (ix >= chunk.length) {
/* 291 */           chunk = buffer.appendCompletedChunk(chunk);
/* 292 */           ix = 0;
/*     */         } 
/* 294 */         chunk[ix++] = value;
/*     */       } 
/* 296 */     } catch (Exception e) {
/* 297 */       throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
/*     */     } 
/* 299 */     String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
/* 300 */     ctxt.returnObjectBuffer(buffer);
/* 301 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 309 */     boolean canWrap = (this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
/* 310 */     if (canWrap) {
/*     */ 
/*     */       
/* 313 */       String value = p.hasToken(JsonToken.VALUE_NULL) ? (String)this._nullProvider.getNullValue(ctxt) : _parseString(p, ctxt);
/* 314 */       return new String[] { value };
/*     */     } 
/* 316 */     if (p.hasToken(JsonToken.VALUE_STRING)) {
/* 317 */       return _deserializeFromString(p, ctxt);
/*     */     }
/* 319 */     return (String[])ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StringArrayDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */