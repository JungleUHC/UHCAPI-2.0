/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.CompactStringObjectMap;
/*     */ import com.fasterxml.jackson.databind.util.EnumResolver;
/*     */ import java.io.IOException;
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
/*     */ @JacksonStdImpl
/*     */ public class EnumDeserializer
/*     */   extends StdScalarDeserializer<Object>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected Object[] _enumsByIndex;
/*     */   private final Enum<?> _enumDefaultValue;
/*     */   protected final CompactStringObjectMap _lookupByName;
/*     */   protected CompactStringObjectMap _lookupByToString;
/*     */   protected final Boolean _caseInsensitive;
/*     */   protected final boolean _isFromIntValue;
/*     */   
/*     */   public EnumDeserializer(EnumResolver byNameResolver, Boolean caseInsensitive) {
/*  69 */     super(byNameResolver.getEnumClass());
/*  70 */     this._lookupByName = byNameResolver.constructLookup();
/*  71 */     this._enumsByIndex = (Object[])byNameResolver.getRawEnums();
/*  72 */     this._enumDefaultValue = byNameResolver.getDefaultValue();
/*  73 */     this._caseInsensitive = caseInsensitive;
/*  74 */     this._isFromIntValue = byNameResolver.isFromIntValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected EnumDeserializer(EnumDeserializer base, Boolean caseInsensitive) {
/*  82 */     super(base);
/*  83 */     this._lookupByName = base._lookupByName;
/*  84 */     this._enumsByIndex = base._enumsByIndex;
/*  85 */     this._enumDefaultValue = base._enumDefaultValue;
/*  86 */     this._caseInsensitive = caseInsensitive;
/*  87 */     this._isFromIntValue = base._isFromIntValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public EnumDeserializer(EnumResolver byNameResolver) {
/*  95 */     this(byNameResolver, (Boolean)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory) {
/* 104 */     return deserializerForCreator(config, enumClass, factory, (ValueInstantiator)null, (SettableBeanProperty[])null);
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
/*     */   public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps) {
/* 119 */     if (config.canOverrideAccessModifiers()) {
/* 120 */       ClassUtil.checkAndFixAccess(factory.getMember(), config
/* 121 */           .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/* 123 */     return new FactoryBasedEnumDeserializer(enumClass, factory, factory
/* 124 */         .getParameterType(0), valueInstantiator, creatorProps);
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
/*     */   public static JsonDeserializer<?> deserializerForNoArgsCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory) {
/* 139 */     if (config.canOverrideAccessModifiers()) {
/* 140 */       ClassUtil.checkAndFixAccess(factory.getMember(), config
/* 141 */           .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/* 143 */     return new FactoryBasedEnumDeserializer(enumClass, factory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EnumDeserializer withResolved(Boolean caseInsensitive) {
/* 150 */     if (Objects.equals(this._caseInsensitive, caseInsensitive)) {
/* 151 */       return this;
/*     */     }
/* 153 */     return new EnumDeserializer(this, caseInsensitive);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 160 */     Boolean caseInsensitive = findFormatFeature(ctxt, property, handledType(), JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
/*     */     
/* 162 */     if (caseInsensitive == null) {
/* 163 */       caseInsensitive = this._caseInsensitive;
/*     */     }
/* 165 */     return withResolved(caseInsensitive);
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
/*     */   public boolean isCachable() {
/* 179 */     return true;
/*     */   }
/*     */   
/*     */   public LogicalType logicalType() {
/* 183 */     return LogicalType.Enum;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 188 */     return this._enumDefaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 197 */     if (p.hasToken(JsonToken.VALUE_STRING)) {
/* 198 */       return _fromString(p, ctxt, p.getText());
/*     */     }
/*     */ 
/*     */     
/* 202 */     if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
/*     */ 
/*     */       
/* 205 */       if (this._isFromIntValue)
/*     */       {
/*     */         
/* 208 */         return _fromString(p, ctxt, p.getText());
/*     */       }
/* 210 */       return _fromInteger(p, ctxt, p.getIntValue());
/*     */     } 
/*     */     
/* 213 */     if (p.isExpectedStartObjectToken()) {
/* 214 */       return _fromString(p, ctxt, ctxt
/* 215 */           .extractScalarFromObject(p, this, this._valueClass));
/*     */     }
/* 217 */     return _deserializeOther(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _fromString(JsonParser p, DeserializationContext ctxt, String text) throws IOException {
/* 225 */     CompactStringObjectMap lookup = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? _getToStringLookup(ctxt) : this._lookupByName;
/* 226 */     Object result = lookup.find(text);
/* 227 */     if (result == null) {
/* 228 */       String trimmed = text.trim();
/* 229 */       if (trimmed == text || (result = lookup.find(trimmed)) == null) {
/* 230 */         return _deserializeAltString(p, ctxt, lookup, trimmed);
/*     */       }
/*     */     } 
/* 233 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _fromInteger(JsonParser p, DeserializationContext ctxt, int index) throws IOException {
/* 240 */     CoercionAction act = ctxt.findCoercionAction(logicalType(), handledType(), CoercionInputShape.Integer);
/*     */ 
/*     */ 
/*     */     
/* 244 */     if (act == CoercionAction.Fail) {
/* 245 */       if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
/* 246 */         return ctxt.handleWeirdNumberValue(_enumClass(), Integer.valueOf(index), "not allowed to deserialize Enum value out of number: disable DeserializationConfig.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow", new Object[0]);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 251 */       _checkCoercionFail(ctxt, act, handledType(), Integer.valueOf(index), "Integer value (" + index + ")");
/*     */     } 
/*     */     
/* 254 */     switch (act) {
/*     */       case AsNull:
/* 256 */         return null;
/*     */       case AsEmpty:
/* 258 */         return getEmptyValue(ctxt);
/*     */     } 
/*     */ 
/*     */     
/* 262 */     if (index >= 0 && index < this._enumsByIndex.length) {
/* 263 */       return this._enumsByIndex[index];
/*     */     }
/* 265 */     if (this._enumDefaultValue != null && ctxt
/* 266 */       .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
/* 267 */       return this._enumDefaultValue;
/*     */     }
/* 269 */     if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL))
/* 270 */       return ctxt.handleWeirdNumberValue(_enumClass(), Integer.valueOf(index), "index value outside legal index range [0..%s]", new Object[] {
/*     */             
/* 272 */             Integer.valueOf(this._enumsByIndex.length - 1)
/*     */           }); 
/* 274 */     return null;
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
/*     */   private final Object _deserializeAltString(JsonParser p, DeserializationContext ctxt, CompactStringObjectMap lookup, String nameOrig) throws IOException {
/* 290 */     String name = nameOrig.trim();
/* 291 */     if (name.isEmpty()) {
/*     */       CoercionAction act;
/*     */       
/* 294 */       if (this._enumDefaultValue != null && ctxt
/* 295 */         .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
/* 296 */         return this._enumDefaultValue;
/*     */       }
/* 298 */       if (ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
/* 299 */         return null;
/*     */       }
/*     */ 
/*     */       
/* 303 */       if (nameOrig.isEmpty()) {
/* 304 */         act = _findCoercionFromEmptyString(ctxt);
/* 305 */         act = _checkCoercionFail(ctxt, act, handledType(), nameOrig, "empty String (\"\")");
/*     */       } else {
/*     */         
/* 308 */         act = _findCoercionFromBlankString(ctxt);
/* 309 */         act = _checkCoercionFail(ctxt, act, handledType(), nameOrig, "blank String (all whitespace)");
/*     */       } 
/*     */       
/* 312 */       switch (act) {
/*     */         case AsEmpty:
/*     */         case TryConvert:
/* 315 */           return getEmptyValue(ctxt);
/*     */       } 
/*     */ 
/*     */       
/* 319 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 323 */     if (Boolean.TRUE.equals(this._caseInsensitive)) {
/* 324 */       Object match = lookup.findCaseInsensitive(name);
/* 325 */       if (match != null) {
/* 326 */         return match;
/*     */       }
/* 328 */     } else if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS) && !this._isFromIntValue) {
/*     */ 
/*     */       
/* 331 */       char c = name.charAt(0);
/* 332 */       if (c >= '0' && c <= '9') {
/*     */         try {
/* 334 */           int index = Integer.parseInt(name);
/* 335 */           if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
/* 336 */             return ctxt.handleWeirdStringValue(_enumClass(), name, "value looks like quoted Enum index, but `MapperFeature.ALLOW_COERCION_OF_SCALARS` prevents use", new Object[0]);
/*     */           }
/*     */ 
/*     */           
/* 340 */           if (index >= 0 && index < this._enumsByIndex.length) {
/* 341 */             return this._enumsByIndex[index];
/*     */           }
/* 343 */         } catch (NumberFormatException numberFormatException) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 349 */     if (this._enumDefaultValue != null && ctxt
/* 350 */       .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
/* 351 */       return this._enumDefaultValue;
/*     */     }
/* 353 */     if (ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
/* 354 */       return null;
/*     */     }
/* 356 */     return ctxt.handleWeirdStringValue(_enumClass(), name, "not one of the values accepted for Enum class: %s", new Object[] { lookup
/* 357 */           .keys() });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _deserializeOther(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 363 */     if (p.hasToken(JsonToken.START_ARRAY)) {
/* 364 */       return _deserializeFromArray(p, ctxt);
/*     */     }
/* 366 */     return ctxt.handleUnexpectedToken(_enumClass(), p);
/*     */   }
/*     */   
/*     */   protected Class<?> _enumClass() {
/* 370 */     return handledType();
/*     */   }
/*     */ 
/*     */   
/*     */   protected CompactStringObjectMap _getToStringLookup(DeserializationContext ctxt) {
/* 375 */     CompactStringObjectMap lookup = this._lookupByToString;
/*     */ 
/*     */     
/* 378 */     if (lookup == null) {
/* 379 */       synchronized (this) {
/*     */         
/* 381 */         lookup = EnumResolver.constructUsingToString(ctxt.getConfig(), _enumClass()).constructLookup();
/*     */       } 
/* 383 */       this._lookupByToString = lookup;
/*     */     } 
/* 385 */     return lookup;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/EnumDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */