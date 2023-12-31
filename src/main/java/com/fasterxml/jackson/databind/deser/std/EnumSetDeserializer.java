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
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*     */ import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*     */ import java.io.IOException;
/*     */ import java.util.EnumSet;
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
/*     */ public class EnumSetDeserializer
/*     */   extends StdDeserializer<EnumSet<?>>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _enumType;
/*     */   protected JsonDeserializer<Enum<?>> _enumDeserializer;
/*     */   protected final NullValueProvider _nullProvider;
/*     */   protected final boolean _skipNullValues;
/*     */   protected final Boolean _unwrapSingle;
/*     */   
/*     */   public EnumSetDeserializer(JavaType enumType, JsonDeserializer<?> deser) {
/*  68 */     super(EnumSet.class);
/*  69 */     this._enumType = enumType;
/*     */     
/*  71 */     if (!enumType.isEnumType()) {
/*  72 */       throw new IllegalArgumentException("Type " + enumType + " not Java Enum type");
/*     */     }
/*  74 */     this._enumDeserializer = (JsonDeserializer)deser;
/*  75 */     this._unwrapSingle = null;
/*  76 */     this._nullProvider = null;
/*  77 */     this._skipNullValues = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected EnumSetDeserializer(EnumSetDeserializer base, JsonDeserializer<?> deser, Boolean unwrapSingle) {
/*  87 */     this(base, deser, base._nullProvider, unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected EnumSetDeserializer(EnumSetDeserializer base, JsonDeserializer<?> deser, NullValueProvider nuller, Boolean unwrapSingle) {
/*  96 */     super(base);
/*  97 */     this._enumType = base._enumType;
/*  98 */     this._enumDeserializer = (JsonDeserializer)deser;
/*  99 */     this._nullProvider = nuller;
/* 100 */     this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
/* 101 */     this._unwrapSingle = unwrapSingle;
/*     */   }
/*     */   
/*     */   public EnumSetDeserializer withDeserializer(JsonDeserializer<?> deser) {
/* 105 */     if (this._enumDeserializer == deser) {
/* 106 */       return this;
/*     */     }
/* 108 */     return new EnumSetDeserializer(this, deser, this._nullProvider, this._unwrapSingle);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public EnumSetDeserializer withResolved(JsonDeserializer<?> deser, Boolean unwrapSingle) {
/* 113 */     return withResolved(deser, this._nullProvider, unwrapSingle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EnumSetDeserializer withResolved(JsonDeserializer<?> deser, NullValueProvider nuller, Boolean unwrapSingle) {
/* 121 */     if (Objects.equals(this._unwrapSingle, unwrapSingle) && this._enumDeserializer == deser && this._nullProvider == deser) {
/* 122 */       return this;
/*     */     }
/* 124 */     return new EnumSetDeserializer(this, deser, nuller, unwrapSingle);
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
/*     */   public boolean isCachable() {
/* 140 */     if (this._enumType.getValueHandler() != null) {
/* 141 */       return false;
/*     */     }
/* 143 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 148 */     return LogicalType.Collection;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 153 */     return Boolean.TRUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 158 */     return constructSet();
/*     */   }
/*     */ 
/*     */   
/*     */   public AccessPattern getEmptyAccessPattern() {
/* 163 */     return AccessPattern.DYNAMIC;
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
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 179 */     Boolean unwrapSingle = findFormatFeature(ctxt, property, EnumSet.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
/*     */     
/* 181 */     JsonDeserializer<?> deser = this._enumDeserializer;
/* 182 */     if (deser == null) {
/* 183 */       deser = ctxt.findContextualValueDeserializer(this._enumType, property);
/*     */     } else {
/* 185 */       deser = ctxt.handleSecondaryContextualization(deser, property, this._enumType);
/*     */     } 
/* 187 */     return withResolved(deser, findContentNullProvider(ctxt, property, deser), unwrapSingle);
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
/*     */   public EnumSet<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 199 */     EnumSet result = constructSet();
/*     */     
/* 201 */     if (!p.isExpectedStartArrayToken()) {
/* 202 */       return handleNonArray(p, ctxt, result);
/*     */     }
/* 204 */     return _deserialize(p, ctxt, result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EnumSet<?> deserialize(JsonParser p, DeserializationContext ctxt, EnumSet<?> result) throws IOException {
/* 212 */     if (!p.isExpectedStartArrayToken()) {
/* 213 */       return handleNonArray(p, ctxt, result);
/*     */     }
/* 215 */     return _deserialize(p, ctxt, result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final EnumSet<?> _deserialize(JsonParser p, DeserializationContext ctxt, EnumSet<Enum<?>> result) throws IOException {
/*     */     try {
/*     */       JsonToken t;
/* 225 */       while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */         Enum<?> value;
/*     */ 
/*     */ 
/*     */         
/* 230 */         if (t == JsonToken.VALUE_NULL) {
/* 231 */           if (this._skipNullValues) {
/*     */             continue;
/*     */           }
/* 234 */           value = (Enum)this._nullProvider.getNullValue(ctxt);
/*     */         } else {
/* 236 */           value = (Enum)this._enumDeserializer.deserialize(p, ctxt);
/*     */         } 
/* 238 */         if (value != null) {
/* 239 */           result.add(value);
/*     */         }
/*     */       } 
/* 242 */     } catch (Exception e) {
/* 243 */       throw JsonMappingException.wrapWithPath(e, result, result.size());
/*     */     } 
/* 245 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 253 */     return typeDeserializer.deserializeTypedFromArray(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private EnumSet constructSet() {
/* 259 */     return EnumSet.noneOf(this._enumType.getRawClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected EnumSet<?> handleNonArray(JsonParser p, DeserializationContext ctxt, EnumSet<Enum<?>> result) throws IOException {
/* 269 */     boolean canWrap = (this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
/*     */     
/* 271 */     if (!canWrap) {
/* 272 */       return (EnumSet)ctxt.handleUnexpectedToken(EnumSet.class, p);
/*     */     }
/*     */     
/* 275 */     if (p.hasToken(JsonToken.VALUE_NULL)) {
/* 276 */       return (EnumSet)ctxt.handleUnexpectedToken(this._enumType, p);
/*     */     }
/*     */     try {
/* 279 */       Enum<?> value = (Enum)this._enumDeserializer.deserialize(p, ctxt);
/* 280 */       if (value != null) {
/* 281 */         result.add(value);
/*     */       }
/* 283 */     } catch (Exception e) {
/* 284 */       throw JsonMappingException.wrapWithPath(e, result, result.size());
/*     */     } 
/* 286 */     return result;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/EnumSetDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */