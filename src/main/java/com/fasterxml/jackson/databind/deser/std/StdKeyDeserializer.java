/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.io.NumberInput;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.EnumResolver;
/*     */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.Calendar;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class StdKeyDeserializer
/*     */   extends KeyDeserializer
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int TYPE_BOOLEAN = 1;
/*     */   public static final int TYPE_BYTE = 2;
/*     */   public static final int TYPE_SHORT = 3;
/*     */   public static final int TYPE_CHAR = 4;
/*     */   public static final int TYPE_INT = 5;
/*     */   public static final int TYPE_LONG = 6;
/*     */   public static final int TYPE_FLOAT = 7;
/*     */   public static final int TYPE_DOUBLE = 8;
/*     */   public static final int TYPE_LOCALE = 9;
/*     */   public static final int TYPE_DATE = 10;
/*     */   public static final int TYPE_CALENDAR = 11;
/*     */   public static final int TYPE_UUID = 12;
/*     */   public static final int TYPE_URI = 13;
/*     */   public static final int TYPE_URL = 14;
/*     */   public static final int TYPE_CLASS = 15;
/*     */   public static final int TYPE_CURRENCY = 16;
/*     */   public static final int TYPE_BYTE_ARRAY = 17;
/*     */   protected final int _kind;
/*     */   protected final Class<?> _keyClass;
/*     */   protected final FromStringDeserializer<?> _deser;
/*     */   
/*     */   protected StdKeyDeserializer(int kind, Class<?> cls) {
/*  62 */     this(kind, cls, null);
/*     */   }
/*     */   
/*     */   protected StdKeyDeserializer(int kind, Class<?> cls, FromStringDeserializer<?> deser) {
/*  66 */     this._kind = kind;
/*  67 */     this._keyClass = cls;
/*  68 */     this._deser = deser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StdKeyDeserializer forType(Class<?> raw) {
/*     */     int kind;
/*  76 */     if (raw == String.class || raw == Object.class || raw == CharSequence.class || raw == Serializable.class)
/*     */     {
/*     */ 
/*     */       
/*  80 */       return StringKD.forType(raw);
/*     */     }
/*  82 */     if (raw == UUID.class)
/*  83 */     { kind = 12; }
/*  84 */     else if (raw == Integer.class)
/*  85 */     { kind = 5; }
/*  86 */     else if (raw == Long.class)
/*  87 */     { kind = 6; }
/*  88 */     else if (raw == Date.class)
/*  89 */     { kind = 10; }
/*  90 */     else if (raw == Calendar.class)
/*  91 */     { kind = 11; }
/*     */     
/*  93 */     else if (raw == Boolean.class)
/*  94 */     { kind = 1; }
/*  95 */     else if (raw == Byte.class)
/*  96 */     { kind = 2; }
/*  97 */     else if (raw == Character.class)
/*  98 */     { kind = 4; }
/*  99 */     else if (raw == Short.class)
/* 100 */     { kind = 3; }
/* 101 */     else if (raw == Float.class)
/* 102 */     { kind = 7; }
/* 103 */     else if (raw == Double.class)
/* 104 */     { kind = 8; }
/* 105 */     else if (raw == URI.class)
/* 106 */     { kind = 13; }
/* 107 */     else if (raw == URL.class)
/* 108 */     { kind = 14; }
/* 109 */     else if (raw == Class.class)
/* 110 */     { kind = 15; }
/* 111 */     else { if (raw == Locale.class) {
/* 112 */         FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Locale.class);
/* 113 */         return new StdKeyDeserializer(9, raw, deser);
/* 114 */       }  if (raw == Currency.class) {
/* 115 */         FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Currency.class);
/* 116 */         return new StdKeyDeserializer(16, raw, deser);
/* 117 */       }  if (raw == byte[].class) {
/* 118 */         kind = 17;
/*     */       } else {
/* 120 */         return null;
/*     */       }  }
/* 122 */      return new StdKeyDeserializer(kind, raw);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
/* 129 */     if (key == null) {
/* 130 */       return null;
/*     */     }
/*     */     try {
/* 133 */       Object result = _parse(key, ctxt);
/* 134 */       if (result != null) {
/* 135 */         return result;
/*     */       }
/* 137 */     } catch (Exception re) {
/* 138 */       return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation, problem: (%s) %s", new Object[] { re
/* 139 */             .getClass().getName(), 
/* 140 */             ClassUtil.exceptionMessage(re) });
/*     */     } 
/* 142 */     if (ClassUtil.isEnumType(this._keyClass) && ctxt
/* 143 */       .getConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
/* 144 */       return null;
/*     */     }
/* 146 */     return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation", new Object[0]);
/*     */   }
/*     */   public Class<?> getKeyClass() {
/* 149 */     return this._keyClass;
/*     */   }
/*     */   protected Object _parse(String key, DeserializationContext ctxt) throws Exception {
/*     */     int value;
/* 153 */     switch (this._kind) {
/*     */       case 1:
/* 155 */         if ("true".equals(key)) {
/* 156 */           return Boolean.TRUE;
/*     */         }
/* 158 */         if ("false".equals(key)) {
/* 159 */           return Boolean.FALSE;
/*     */         }
/* 161 */         return ctxt.handleWeirdKey(this._keyClass, key, "value not 'true' or 'false'", new Object[0]);
/*     */       
/*     */       case 2:
/* 164 */         value = _parseInt(key);
/*     */         
/* 166 */         if (value < -128 || value > 255) {
/* 167 */           return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value cannot be represented as 8-bit value", new Object[0]);
/*     */         }
/* 169 */         return Byte.valueOf((byte)value);
/*     */ 
/*     */       
/*     */       case 3:
/* 173 */         value = _parseInt(key);
/* 174 */         if (value < -32768 || value > 32767) {
/* 175 */           return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value cannot be represented as 16-bit value", new Object[0]);
/*     */         }
/*     */         
/* 178 */         return Short.valueOf((short)value);
/*     */       
/*     */       case 4:
/* 181 */         if (key.length() == 1) {
/* 182 */           return Character.valueOf(key.charAt(0));
/*     */         }
/* 184 */         return ctxt.handleWeirdKey(this._keyClass, key, "can only convert 1-character Strings", new Object[0]);
/*     */       case 5:
/* 186 */         return Integer.valueOf(_parseInt(key));
/*     */       
/*     */       case 6:
/* 189 */         return Long.valueOf(_parseLong(key));
/*     */ 
/*     */       
/*     */       case 7:
/* 193 */         return Float.valueOf((float)_parseDouble(key));
/*     */       case 8:
/* 195 */         return Double.valueOf(_parseDouble(key));
/*     */       case 9:
/*     */         try {
/* 198 */           return this._deser._deserialize(key, ctxt);
/* 199 */         } catch (IllegalArgumentException e) {
/* 200 */           return _weirdKey(ctxt, key, e);
/*     */         } 
/*     */       case 16:
/*     */         try {
/* 204 */           return this._deser._deserialize(key, ctxt);
/* 205 */         } catch (IllegalArgumentException e) {
/* 206 */           return _weirdKey(ctxt, key, e);
/*     */         } 
/*     */       case 10:
/* 209 */         return ctxt.parseDate(key);
/*     */       case 11:
/* 211 */         return ctxt.constructCalendar(ctxt.parseDate(key));
/*     */       case 12:
/*     */         try {
/* 214 */           return UUID.fromString(key);
/* 215 */         } catch (Exception e) {
/* 216 */           return _weirdKey(ctxt, key, e);
/*     */         } 
/*     */       case 13:
/*     */         try {
/* 220 */           return URI.create(key);
/* 221 */         } catch (Exception e) {
/* 222 */           return _weirdKey(ctxt, key, e);
/*     */         } 
/*     */       case 14:
/*     */         try {
/* 226 */           return new URL(key);
/* 227 */         } catch (MalformedURLException e) {
/* 228 */           return _weirdKey(ctxt, key, e);
/*     */         } 
/*     */       case 15:
/*     */         try {
/* 232 */           return ctxt.findClass(key);
/* 233 */         } catch (Exception e) {
/* 234 */           return ctxt.handleWeirdKey(this._keyClass, key, "unable to parse key as Class", new Object[0]);
/*     */         } 
/*     */       case 17:
/*     */         try {
/* 238 */           return ctxt.getConfig().getBase64Variant().decode(key);
/* 239 */         } catch (IllegalArgumentException e) {
/* 240 */           return _weirdKey(ctxt, key, e);
/*     */         } 
/*     */     } 
/* 243 */     throw new IllegalStateException("Internal error: unknown key type " + this._keyClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _parseInt(String key) throws IllegalArgumentException {
/* 254 */     return Integer.parseInt(key);
/*     */   }
/*     */   
/*     */   protected long _parseLong(String key) throws IllegalArgumentException {
/* 258 */     return Long.parseLong(key);
/*     */   }
/*     */   
/*     */   protected double _parseDouble(String key) throws IllegalArgumentException {
/* 262 */     return NumberInput.parseDouble(key);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object _weirdKey(DeserializationContext ctxt, String key, Exception e) throws IOException {
/* 267 */     return ctxt.handleWeirdKey(this._keyClass, key, "problem: %s", new Object[] {
/* 268 */           ClassUtil.exceptionMessage(e)
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class StringKD
/*     */     extends StdKeyDeserializer
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     
/* 281 */     private static final StringKD sString = new StringKD(String.class);
/* 282 */     private static final StringKD sObject = new StringKD(Object.class);
/*     */     private StringKD(Class<?> nominalType) {
/* 284 */       super(-1, nominalType);
/*     */     }
/*     */     
/*     */     public static StringKD forType(Class<?> nominalType) {
/* 288 */       if (nominalType == String.class) {
/* 289 */         return sString;
/*     */       }
/* 291 */       if (nominalType == Object.class) {
/* 292 */         return sObject;
/*     */       }
/* 294 */       return new StringKD(nominalType);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
/* 299 */       return key;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class DelegatingKD
/*     */     extends KeyDeserializer
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */     
/*     */     protected final Class<?> _keyClass;
/*     */ 
/*     */ 
/*     */     
/*     */     protected final JsonDeserializer<?> _delegate;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected DelegatingKD(Class<?> cls, JsonDeserializer<?> deser) {
/* 325 */       this._keyClass = cls;
/* 326 */       this._delegate = deser;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
/* 334 */       if (key == null) {
/* 335 */         return null;
/*     */       }
/* 337 */       TokenBuffer tb = ctxt.bufferForInputBuffering();
/* 338 */       tb.writeString(key);
/*     */       
/*     */       try {
/* 341 */         JsonParser p = tb.asParser();
/* 342 */         p.nextToken();
/* 343 */         Object result = this._delegate.deserialize(p, ctxt);
/* 344 */         if (result != null) {
/* 345 */           return result;
/*     */         }
/* 347 */         return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation", new Object[0]);
/* 348 */       } catch (Exception re) {
/* 349 */         return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation: %s", new Object[] { re.getMessage() });
/*     */       } 
/*     */     }
/*     */     public Class<?> getKeyClass() {
/* 353 */       return this._keyClass;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @JacksonStdImpl
/*     */   static final class EnumKD
/*     */     extends StdKeyDeserializer
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     
/*     */     protected final EnumResolver _byNameResolver;
/*     */     
/*     */     protected final AnnotatedMethod _factory;
/*     */     
/*     */     protected EnumResolver _byToStringResolver;
/*     */     
/*     */     protected final Enum<?> _enumDefaultValue;
/*     */ 
/*     */     
/*     */     protected EnumKD(EnumResolver er, AnnotatedMethod factory) {
/* 376 */       super(-1, er.getEnumClass());
/* 377 */       this._byNameResolver = er;
/* 378 */       this._factory = factory;
/* 379 */       this._enumDefaultValue = er.getDefaultValue();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Object _parse(String key, DeserializationContext ctxt) throws IOException {
/* 385 */       if (this._factory != null) {
/*     */         try {
/* 387 */           return this._factory.call1(key);
/* 388 */         } catch (Exception exception) {
/* 389 */           ClassUtil.unwrapAndThrowAsIAE(exception);
/*     */         } 
/*     */       }
/*     */       
/* 393 */       EnumResolver res = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? _getToStringResolver(ctxt) : this._byNameResolver;
/* 394 */       Enum<?> e = res.findEnum(key);
/* 395 */       if (e == null) {
/* 396 */         if (this._enumDefaultValue != null && ctxt
/* 397 */           .isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
/* 398 */           e = this._enumDefaultValue;
/* 399 */         } else if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
/* 400 */           return ctxt.handleWeirdKey(this._keyClass, key, "not one of the values accepted for Enum class: %s", new Object[] { res
/* 401 */                 .getEnumIds() });
/*     */         } 
/*     */       }
/*     */       
/* 405 */       return e;
/*     */     }
/*     */ 
/*     */     
/*     */     private EnumResolver _getToStringResolver(DeserializationContext ctxt) {
/* 410 */       EnumResolver res = this._byToStringResolver;
/* 411 */       if (res == null) {
/* 412 */         synchronized (this) {
/* 413 */           res = EnumResolver.constructUsingToString(ctxt.getConfig(), this._byNameResolver
/* 414 */               .getEnumClass());
/* 415 */           this._byToStringResolver = res;
/*     */         } 
/*     */       }
/* 418 */       return res;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class StringCtorKeyDeserializer
/*     */     extends StdKeyDeserializer
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     protected final Constructor<?> _ctor;
/*     */ 
/*     */     
/*     */     public StringCtorKeyDeserializer(Constructor<?> ctor) {
/* 433 */       super(-1, ctor.getDeclaringClass());
/* 434 */       this._ctor = ctor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Object _parse(String key, DeserializationContext ctxt) throws Exception {
/* 440 */       return this._ctor.newInstance(new Object[] { key });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class StringFactoryKeyDeserializer
/*     */     extends StdKeyDeserializer
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     final Method _factoryMethod;
/*     */ 
/*     */     
/*     */     public StringFactoryKeyDeserializer(Method fm) {
/* 455 */       super(-1, fm.getDeclaringClass());
/* 456 */       this._factoryMethod = fm;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Object _parse(String key, DeserializationContext ctxt) throws Exception {
/* 462 */       return this._factoryMethod.invoke(null, new Object[] { key });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StdKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */