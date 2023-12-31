/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.util.VersionUtil;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
/*     */ import com.fasterxml.jackson.databind.exc.InvalidFormatException;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Currency;
/*     */ import java.util.IllformedLocaleException;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Pattern;
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
/*     */ public abstract class FromStringDeserializer<T>
/*     */   extends StdScalarDeserializer<T>
/*     */ {
/*     */   public static Class<?>[] types() {
/*  61 */     return new Class[] { File.class, URL.class, URI.class, Class.class, JavaType.class, Currency.class, Pattern.class, Locale.class, Charset.class, TimeZone.class, InetAddress.class, InetSocketAddress.class, StringBuilder.class };
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
/*     */   protected FromStringDeserializer(Class<?> vc) {
/*  87 */     super(vc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FromStringDeserializer<?> findDeserializer(Class<?> rawType) {
/*  96 */     int kind = 0;
/*  97 */     if (rawType == File.class)
/*  98 */     { kind = 1; }
/*  99 */     else if (rawType == URL.class)
/* 100 */     { kind = 2; }
/* 101 */     else if (rawType == URI.class)
/* 102 */     { kind = 3; }
/* 103 */     else if (rawType == Class.class)
/* 104 */     { kind = 4; }
/* 105 */     else if (rawType == JavaType.class)
/* 106 */     { kind = 5; }
/* 107 */     else if (rawType == Currency.class)
/* 108 */     { kind = 6; }
/* 109 */     else if (rawType == Pattern.class)
/* 110 */     { kind = 7; }
/* 111 */     else if (rawType == Locale.class)
/* 112 */     { kind = 8; }
/* 113 */     else if (rawType == Charset.class)
/* 114 */     { kind = 9; }
/* 115 */     else if (rawType == TimeZone.class)
/* 116 */     { kind = 10; }
/* 117 */     else if (rawType == InetAddress.class)
/* 118 */     { kind = 11; }
/* 119 */     else if (rawType == InetSocketAddress.class)
/* 120 */     { kind = 12; }
/* 121 */     else { if (rawType == StringBuilder.class) {
/* 122 */         return new StringBuilderDeserializer();
/*     */       }
/* 124 */       return null; }
/*     */     
/* 126 */     return new Std(rawType, kind);
/*     */   }
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 131 */     return LogicalType.OtherScalar;
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
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 145 */     String text = p.getValueAsString();
/* 146 */     if (text == null) {
/* 147 */       JsonToken t = p.currentToken();
/* 148 */       if (t != JsonToken.START_OBJECT) {
/* 149 */         return (T)_deserializeFromOther(p, ctxt, t);
/*     */       }
/*     */       
/* 152 */       text = ctxt.extractScalarFromObject(p, this, this._valueClass);
/*     */     } 
/* 154 */     if (text.isEmpty())
/*     */     {
/* 156 */       return (T)_deserializeFromEmptyString(ctxt);
/*     */     }
/* 158 */     if (_shouldTrim()) {
/* 159 */       String old = text;
/* 160 */       text = text.trim();
/* 161 */       if (text != old && 
/* 162 */         text.isEmpty()) {
/* 163 */         return (T)_deserializeFromEmptyString(ctxt);
/*     */       }
/*     */     } 
/*     */     
/* 167 */     Exception cause = null;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 172 */       return _deserialize(text, ctxt);
/* 173 */     } catch (IllegalArgumentException|java.net.MalformedURLException e) {
/* 174 */       cause = e;
/*     */ 
/*     */       
/* 177 */       String msg = "not a valid textual representation";
/* 178 */       String m2 = cause.getMessage();
/* 179 */       if (m2 != null) {
/* 180 */         msg = msg + ", problem: " + m2;
/*     */       }
/*     */       
/* 183 */       throw ctxt.weirdStringException(text, this._valueClass, msg)
/* 184 */         .withCause(cause);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract T _deserialize(String paramString, DeserializationContext paramDeserializationContext) throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _shouldTrim() {
/* 195 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _deserializeFromOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/* 203 */     if (t == JsonToken.START_ARRAY) {
/* 204 */       return _deserializeFromArray(p, ctxt);
/*     */     }
/* 206 */     if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
/*     */       
/* 208 */       Object ob = p.getEmbeddedObject();
/* 209 */       if (ob == null) {
/* 210 */         return null;
/*     */       }
/* 212 */       if (this._valueClass.isAssignableFrom(ob.getClass())) {
/* 213 */         return ob;
/*     */       }
/* 215 */       return _deserializeEmbedded(ob, ctxt);
/*     */     } 
/* 217 */     return ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected T _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException {
/* 227 */     ctxt.reportInputMismatch(this, "Don't know how to convert embedded Object of type %s into %s", new Object[] { ob
/*     */           
/* 229 */           .getClass().getName(), this._valueClass.getName() });
/* 230 */     return null;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected final T _deserializeFromEmptyString() throws IOException {
/* 235 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _deserializeFromEmptyString(DeserializationContext ctxt) throws IOException {
/* 242 */     CoercionAction act = ctxt.findCoercionAction(logicalType(), this._valueClass, CoercionInputShape.EmptyString);
/*     */     
/* 244 */     if (act == CoercionAction.Fail)
/* 245 */       ctxt.reportInputMismatch(this, "Cannot coerce empty String (\"\") to %s (but could if enabling coercion using `CoercionConfig`)", new Object[] {
/*     */             
/* 247 */             _coercedTypeDesc()
/*     */           }); 
/* 249 */     if (act == CoercionAction.AsNull) {
/* 250 */       return getNullValue(ctxt);
/*     */     }
/* 252 */     if (act == CoercionAction.AsEmpty) {
/* 253 */       return getEmptyValue(ctxt);
/*     */     }
/*     */ 
/*     */     
/* 257 */     return _deserializeFromEmptyStringDefault(ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _deserializeFromEmptyStringDefault(DeserializationContext ctxt) throws IOException {
/* 265 */     return getNullValue(ctxt);
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Std
/*     */     extends FromStringDeserializer<Object>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     public static final int STD_FILE = 1;
/*     */     
/*     */     public static final int STD_URL = 2;
/*     */     
/*     */     public static final int STD_URI = 3;
/*     */     
/*     */     public static final int STD_CLASS = 4;
/*     */     
/*     */     public static final int STD_JAVA_TYPE = 5;
/*     */     
/*     */     public static final int STD_CURRENCY = 6;
/*     */     
/*     */     public static final int STD_PATTERN = 7;
/*     */     
/*     */     public static final int STD_LOCALE = 8;
/*     */     
/*     */     public static final int STD_CHARSET = 9;
/*     */     
/*     */     public static final int STD_TIME_ZONE = 10;
/*     */     
/*     */     public static final int STD_INET_ADDRESS = 11;
/*     */     
/*     */     public static final int STD_INET_SOCKET_ADDRESS = 12;
/*     */     
/*     */     protected static final String LOCALE_EXT_MARKER = "_#";
/*     */     
/*     */     protected final int _kind;
/*     */ 
/*     */     
/*     */     protected Std(Class<?> valueType, int kind) {
/* 304 */       super(valueType);
/* 305 */       this._kind = kind;
/*     */     }
/*     */ 
/*     */     
/*     */     protected Object _deserialize(String value, DeserializationContext ctxt) throws IOException {
/*     */       int ix;
/* 311 */       switch (this._kind) {
/*     */         case 1:
/* 313 */           return new File(value);
/*     */         case 2:
/* 315 */           return new URL(value);
/*     */         case 3:
/* 317 */           return URI.create(value);
/*     */         case 4:
/*     */           try {
/* 320 */             return ctxt.findClass(value);
/* 321 */           } catch (Exception e) {
/* 322 */             return ctxt.handleInstantiationProblem(this._valueClass, value, 
/* 323 */                 ClassUtil.getRootCause(e));
/*     */           } 
/*     */         case 5:
/* 326 */           return ctxt.getTypeFactory().constructFromCanonical(value);
/*     */         
/*     */         case 6:
/* 329 */           return Currency.getInstance(value);
/*     */         
/*     */         case 7:
/* 332 */           return Pattern.compile(value);
/*     */         case 8:
/* 334 */           return _deserializeLocale(value, ctxt);
/*     */         case 9:
/* 336 */           return Charset.forName(value);
/*     */         case 10:
/* 338 */           return TimeZone.getTimeZone(value);
/*     */         case 11:
/* 340 */           return InetAddress.getByName(value);
/*     */         case 12:
/* 342 */           if (value.startsWith("[")) {
/*     */ 
/*     */             
/* 345 */             int i = value.lastIndexOf(']');
/* 346 */             if (i == -1) {
/* 347 */               throw new InvalidFormatException(ctxt.getParser(), "Bracketed IPv6 address must contain closing bracket", value, InetSocketAddress.class);
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 352 */             int j = value.indexOf(':', i);
/* 353 */             int port = (j > -1) ? Integer.parseInt(value.substring(j + 1)) : 0;
/* 354 */             return new InetSocketAddress(value.substring(0, i + 1), port);
/*     */           } 
/* 356 */           ix = value.indexOf(':');
/* 357 */           if (ix >= 0 && value.indexOf(':', ix + 1) < 0) {
/*     */             
/* 359 */             int port = Integer.parseInt(value.substring(ix + 1));
/* 360 */             return new InetSocketAddress(value.substring(0, ix), port);
/*     */           } 
/*     */           
/* 363 */           return new InetSocketAddress(value, 0);
/*     */       } 
/* 365 */       VersionUtil.throwInternal();
/* 366 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 373 */       switch (this._kind) {
/*     */         
/*     */         case 3:
/* 376 */           return URI.create("");
/*     */         
/*     */         case 8:
/* 379 */           return Locale.ROOT;
/*     */       } 
/* 381 */       return super.getEmptyValue(ctxt);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Object _deserializeFromEmptyStringDefault(DeserializationContext ctxt) throws IOException {
/* 389 */       return getEmptyValue(ctxt);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected boolean _shouldTrim() {
/* 396 */       return (this._kind != 7);
/*     */     }
/*     */ 
/*     */     
/*     */     protected int _firstHyphenOrUnderscore(String str) {
/* 401 */       for (int i = 0, end = str.length(); i < end; i++) {
/* 402 */         char c = str.charAt(i);
/* 403 */         if (c == '_' || c == '-') {
/* 404 */           return i;
/*     */         }
/*     */       } 
/* 407 */       return -1;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Locale _deserializeLocale(String value, DeserializationContext ctxt) throws IOException {
/* 413 */       int ix = _firstHyphenOrUnderscore(value);
/* 414 */       if (ix < 0) {
/* 415 */         return new Locale(value);
/*     */       }
/* 417 */       String first = value.substring(0, ix);
/* 418 */       value = value.substring(ix + 1);
/* 419 */       ix = _firstHyphenOrUnderscore(value);
/* 420 */       if (ix < 0) {
/* 421 */         return new Locale(first, value);
/*     */       }
/* 423 */       String second = value.substring(0, ix);
/*     */       
/* 425 */       int extMarkerIx = value.indexOf("_#");
/* 426 */       if (extMarkerIx < 0) {
/* 427 */         return new Locale(first, second, value.substring(ix + 1));
/*     */       }
/* 429 */       return _deSerializeBCP47Locale(value, ix, first, second, extMarkerIx);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Locale _deSerializeBCP47Locale(String value, int ix, String first, String second, int extMarkerIx) {
/* 435 */       String third = "";
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 440 */         if (extMarkerIx > 0 && extMarkerIx > ix) {
/* 441 */           third = value.substring(ix + 1, extMarkerIx);
/*     */         }
/* 443 */         value = value.substring(extMarkerIx + 2);
/*     */         
/* 445 */         if (value.indexOf('_') < 0 && value.indexOf('-') < 0) {
/* 446 */           return (new Locale.Builder()).setLanguage(first)
/* 447 */             .setRegion(second).setVariant(third).setScript(value).build();
/*     */         }
/* 449 */         if (value.indexOf('_') < 0) {
/* 450 */           ix = value.indexOf('-');
/* 451 */           return (new Locale.Builder()).setLanguage(first)
/* 452 */             .setRegion(second).setVariant(third)
/* 453 */             .setExtension(value.charAt(0), value.substring(ix + 1))
/* 454 */             .build();
/*     */         } 
/* 456 */         ix = value.indexOf('_');
/* 457 */         return (new Locale.Builder()).setLanguage(first)
/* 458 */           .setRegion(second).setVariant(third)
/* 459 */           .setScript(value.substring(0, ix))
/* 460 */           .setExtension(value.charAt(ix + 1), value.substring(ix + 3))
/* 461 */           .build();
/* 462 */       } catch (IllformedLocaleException ex) {
/*     */         
/* 464 */         return new Locale(first, second, third);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class StringBuilderDeserializer
/*     */     extends FromStringDeserializer<Object>
/*     */   {
/*     */     public StringBuilderDeserializer() {
/* 474 */       super(StringBuilder.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public LogicalType logicalType() {
/* 479 */       return LogicalType.Textual;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 486 */       return new StringBuilder();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 492 */       String text = p.getValueAsString();
/* 493 */       if (text != null) {
/* 494 */         return _deserialize(text, ctxt);
/*     */       }
/* 496 */       return super.deserialize(p, ctxt);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Object _deserialize(String value, DeserializationContext ctxt) throws IOException {
/* 503 */       return new StringBuilder(value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/FromStringDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */