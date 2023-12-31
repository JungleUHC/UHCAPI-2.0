/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class StdValueInstantiator
/*     */   extends ValueInstantiator
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final String _valueTypeDesc;
/*     */   protected final Class<?> _valueClass;
/*     */   protected AnnotatedWithParams _defaultCreator;
/*     */   protected AnnotatedWithParams _withArgsCreator;
/*     */   protected SettableBeanProperty[] _constructorArguments;
/*     */   protected JavaType _delegateType;
/*     */   protected AnnotatedWithParams _delegateCreator;
/*     */   protected SettableBeanProperty[] _delegateArguments;
/*     */   protected JavaType _arrayDelegateType;
/*     */   protected AnnotatedWithParams _arrayDelegateCreator;
/*     */   protected SettableBeanProperty[] _arrayDelegateArguments;
/*     */   protected AnnotatedWithParams _fromStringCreator;
/*     */   protected AnnotatedWithParams _fromIntCreator;
/*     */   protected AnnotatedWithParams _fromLongCreator;
/*     */   protected AnnotatedWithParams _fromBigIntegerCreator;
/*     */   protected AnnotatedWithParams _fromDoubleCreator;
/*     */   protected AnnotatedWithParams _fromBigDecimalCreator;
/*     */   protected AnnotatedWithParams _fromBooleanCreator;
/*     */   
/*     */   @Deprecated
/*     */   public StdValueInstantiator(DeserializationConfig config, Class<?> valueType) {
/*  83 */     this._valueTypeDesc = ClassUtil.nameOf(valueType);
/*  84 */     this._valueClass = (valueType == null) ? Object.class : valueType;
/*     */   }
/*     */   
/*     */   public StdValueInstantiator(DeserializationConfig config, JavaType valueType) {
/*  88 */     this._valueTypeDesc = (valueType == null) ? "UNKNOWN TYPE" : valueType.toString();
/*  89 */     this._valueClass = (valueType == null) ? Object.class : valueType.getRawClass();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StdValueInstantiator(StdValueInstantiator src) {
/*  98 */     this._valueTypeDesc = src._valueTypeDesc;
/*  99 */     this._valueClass = src._valueClass;
/*     */     
/* 101 */     this._defaultCreator = src._defaultCreator;
/*     */     
/* 103 */     this._constructorArguments = src._constructorArguments;
/* 104 */     this._withArgsCreator = src._withArgsCreator;
/*     */     
/* 106 */     this._delegateType = src._delegateType;
/* 107 */     this._delegateCreator = src._delegateCreator;
/* 108 */     this._delegateArguments = src._delegateArguments;
/*     */     
/* 110 */     this._arrayDelegateType = src._arrayDelegateType;
/* 111 */     this._arrayDelegateCreator = src._arrayDelegateCreator;
/* 112 */     this._arrayDelegateArguments = src._arrayDelegateArguments;
/*     */     
/* 114 */     this._fromStringCreator = src._fromStringCreator;
/* 115 */     this._fromIntCreator = src._fromIntCreator;
/* 116 */     this._fromLongCreator = src._fromLongCreator;
/* 117 */     this._fromBigIntegerCreator = src._fromBigIntegerCreator;
/* 118 */     this._fromDoubleCreator = src._fromDoubleCreator;
/* 119 */     this._fromBigDecimalCreator = src._fromBigDecimalCreator;
/* 120 */     this._fromBooleanCreator = src._fromBooleanCreator;
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
/*     */   public void configureFromObjectSettings(AnnotatedWithParams defaultCreator, AnnotatedWithParams delegateCreator, JavaType delegateType, SettableBeanProperty[] delegateArgs, AnnotatedWithParams withArgsCreator, SettableBeanProperty[] constructorArgs) {
/* 132 */     this._defaultCreator = defaultCreator;
/* 133 */     this._delegateCreator = delegateCreator;
/* 134 */     this._delegateType = delegateType;
/* 135 */     this._delegateArguments = delegateArgs;
/* 136 */     this._withArgsCreator = withArgsCreator;
/* 137 */     this._constructorArguments = constructorArgs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void configureFromArraySettings(AnnotatedWithParams arrayDelegateCreator, JavaType arrayDelegateType, SettableBeanProperty[] arrayDelegateArgs) {
/* 145 */     this._arrayDelegateCreator = arrayDelegateCreator;
/* 146 */     this._arrayDelegateType = arrayDelegateType;
/* 147 */     this._arrayDelegateArguments = arrayDelegateArgs;
/*     */   }
/*     */   
/*     */   public void configureFromStringCreator(AnnotatedWithParams creator) {
/* 151 */     this._fromStringCreator = creator;
/*     */   }
/*     */   
/*     */   public void configureFromIntCreator(AnnotatedWithParams creator) {
/* 155 */     this._fromIntCreator = creator;
/*     */   }
/*     */   
/*     */   public void configureFromLongCreator(AnnotatedWithParams creator) {
/* 159 */     this._fromLongCreator = creator;
/*     */   }
/*     */   public void configureFromBigIntegerCreator(AnnotatedWithParams creator) {
/* 162 */     this._fromBigIntegerCreator = creator;
/*     */   }
/*     */   public void configureFromDoubleCreator(AnnotatedWithParams creator) {
/* 165 */     this._fromDoubleCreator = creator;
/*     */   }
/*     */   public void configureFromBigDecimalCreator(AnnotatedWithParams creator) {
/* 168 */     this._fromBigDecimalCreator = creator;
/*     */   }
/*     */   public void configureFromBooleanCreator(AnnotatedWithParams creator) {
/* 171 */     this._fromBooleanCreator = creator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValueTypeDesc() {
/* 182 */     return this._valueTypeDesc;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getValueClass() {
/* 187 */     return this._valueClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canCreateFromString() {
/* 192 */     return (this._fromStringCreator != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canCreateFromInt() {
/* 197 */     return (this._fromIntCreator != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canCreateFromLong() {
/* 202 */     return (this._fromLongCreator != null);
/*     */   }
/*     */   
/*     */   public boolean canCreateFromBigInteger() {
/* 206 */     return (this._fromBigIntegerCreator != null);
/*     */   }
/*     */   
/*     */   public boolean canCreateFromDouble() {
/* 210 */     return (this._fromDoubleCreator != null);
/*     */   }
/*     */   
/*     */   public boolean canCreateFromBigDecimal() {
/* 214 */     return (this._fromBigDecimalCreator != null);
/*     */   }
/*     */   
/*     */   public boolean canCreateFromBoolean() {
/* 218 */     return (this._fromBooleanCreator != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canCreateUsingDefault() {
/* 223 */     return (this._defaultCreator != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canCreateUsingDelegate() {
/* 228 */     return (this._delegateType != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canCreateUsingArrayDelegate() {
/* 233 */     return (this._arrayDelegateType != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canCreateFromObjectWith() {
/* 238 */     return (this._withArgsCreator != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canInstantiate() {
/* 243 */     return (canCreateUsingDefault() || 
/* 244 */       canCreateUsingDelegate() || canCreateUsingArrayDelegate() || 
/* 245 */       canCreateFromObjectWith() || canCreateFromString() || 
/* 246 */       canCreateFromInt() || canCreateFromLong() || 
/* 247 */       canCreateFromDouble() || canCreateFromBoolean());
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getDelegateType(DeserializationConfig config) {
/* 252 */     return this._delegateType;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getArrayDelegateType(DeserializationConfig config) {
/* 257 */     return this._arrayDelegateType;
/*     */   }
/*     */ 
/*     */   
/*     */   public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
/* 262 */     return this._constructorArguments;
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
/*     */   public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
/* 274 */     if (this._defaultCreator == null) {
/* 275 */       return super.createUsingDefault(ctxt);
/*     */     }
/*     */     try {
/* 278 */       return this._defaultCreator.call();
/* 279 */     } catch (Exception e) {
/* 280 */       return ctxt.handleInstantiationProblem(this._valueClass, null, (Throwable)rewrapCtorProblem(ctxt, e));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
/* 287 */     if (this._withArgsCreator == null) {
/* 288 */       return super.createFromObjectWith(ctxt, args);
/*     */     }
/*     */     try {
/* 291 */       return this._withArgsCreator.call(args);
/* 292 */     } catch (Exception e) {
/* 293 */       return ctxt.handleInstantiationProblem(this._valueClass, args, (Throwable)rewrapCtorProblem(ctxt, e));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 301 */     if (this._delegateCreator == null && 
/* 302 */       this._arrayDelegateCreator != null) {
/* 303 */       return _createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
/*     */     }
/*     */     
/* 306 */     return _createUsingDelegate(this._delegateCreator, this._delegateArguments, ctxt, delegate);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 312 */     if (this._arrayDelegateCreator == null && 
/* 313 */       this._delegateCreator != null)
/*     */     {
/* 315 */       return createUsingDelegate(ctxt, delegate);
/*     */     }
/*     */     
/* 318 */     return _createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
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
/*     */   public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
/* 330 */     if (this._fromStringCreator != null) {
/*     */       try {
/* 332 */         return this._fromStringCreator.call1(value);
/* 333 */       } catch (Throwable t) {
/* 334 */         return ctxt.handleInstantiationProblem(this._fromStringCreator.getDeclaringClass(), value, (Throwable)
/* 335 */             rewrapCtorProblem(ctxt, t));
/*     */       } 
/*     */     }
/* 338 */     return super.createFromString(ctxt, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
/* 345 */     if (this._fromIntCreator != null) {
/* 346 */       Object arg = Integer.valueOf(value);
/*     */       try {
/* 348 */         return this._fromIntCreator.call1(arg);
/* 349 */       } catch (Throwable t0) {
/* 350 */         return ctxt.handleInstantiationProblem(this._fromIntCreator.getDeclaringClass(), arg, (Throwable)
/* 351 */             rewrapCtorProblem(ctxt, t0));
/*     */       } 
/*     */     } 
/*     */     
/* 355 */     if (this._fromLongCreator != null) {
/* 356 */       Object arg = Long.valueOf(value);
/*     */       try {
/* 358 */         return this._fromLongCreator.call1(arg);
/* 359 */       } catch (Throwable t0) {
/* 360 */         return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, (Throwable)
/* 361 */             rewrapCtorProblem(ctxt, t0));
/*     */       } 
/*     */     } 
/*     */     
/* 365 */     if (this._fromBigIntegerCreator != null) {
/* 366 */       Object arg = BigInteger.valueOf(value);
/*     */       try {
/* 368 */         return this._fromBigIntegerCreator.call1(arg);
/* 369 */       } catch (Throwable t0) {
/* 370 */         return ctxt.handleInstantiationProblem(this._fromBigIntegerCreator.getDeclaringClass(), arg, (Throwable)
/* 371 */             rewrapCtorProblem(ctxt, t0));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 376 */     return super.createFromInt(ctxt, value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
/* 382 */     if (this._fromLongCreator != null) {
/* 383 */       Long arg = Long.valueOf(value);
/*     */       try {
/* 385 */         return this._fromLongCreator.call1(arg);
/* 386 */       } catch (Throwable t0) {
/* 387 */         return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, (Throwable)
/* 388 */             rewrapCtorProblem(ctxt, t0));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 393 */     if (this._fromBigIntegerCreator != null) {
/* 394 */       BigInteger arg = BigInteger.valueOf(value);
/*     */       try {
/* 396 */         return this._fromBigIntegerCreator.call1(arg);
/* 397 */       } catch (Throwable t0) {
/* 398 */         return ctxt.handleInstantiationProblem(this._fromBigIntegerCreator.getDeclaringClass(), arg, (Throwable)
/* 399 */             rewrapCtorProblem(ctxt, t0));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 404 */     return super.createFromLong(ctxt, value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromBigInteger(DeserializationContext ctxt, BigInteger value) throws IOException {
/* 410 */     if (this._fromBigIntegerCreator != null) {
/*     */       try {
/* 412 */         return this._fromBigIntegerCreator.call1(value);
/* 413 */       } catch (Throwable t) {
/* 414 */         return ctxt.handleInstantiationProblem(this._fromBigIntegerCreator.getDeclaringClass(), value, (Throwable)
/* 415 */             rewrapCtorProblem(ctxt, t));
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 420 */     return super.createFromBigInteger(ctxt, value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
/* 426 */     if (this._fromDoubleCreator != null) {
/* 427 */       Double arg = Double.valueOf(value);
/*     */       try {
/* 429 */         return this._fromDoubleCreator.call1(arg);
/* 430 */       } catch (Throwable t0) {
/* 431 */         return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), arg, (Throwable)
/* 432 */             rewrapCtorProblem(ctxt, t0));
/*     */       } 
/*     */     } 
/*     */     
/* 436 */     if (this._fromBigDecimalCreator != null) {
/* 437 */       BigDecimal arg = BigDecimal.valueOf(value);
/*     */       try {
/* 439 */         return this._fromBigDecimalCreator.call1(arg);
/* 440 */       } catch (Throwable t0) {
/* 441 */         return ctxt.handleInstantiationProblem(this._fromBigDecimalCreator.getDeclaringClass(), arg, (Throwable)
/* 442 */             rewrapCtorProblem(ctxt, t0));
/*     */       } 
/*     */     } 
/*     */     
/* 446 */     return super.createFromDouble(ctxt, value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromBigDecimal(DeserializationContext ctxt, BigDecimal value) throws IOException {
/* 452 */     if (this._fromBigDecimalCreator != null) {
/*     */       try {
/* 454 */         return this._fromBigDecimalCreator.call1(value);
/* 455 */       } catch (Throwable t) {
/* 456 */         return ctxt.handleInstantiationProblem(this._fromBigDecimalCreator.getDeclaringClass(), value, (Throwable)
/* 457 */             rewrapCtorProblem(ctxt, t));
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 466 */     if (this._fromDoubleCreator != null) {
/* 467 */       Double dbl = tryConvertToDouble(value);
/* 468 */       if (dbl != null) {
/*     */         try {
/* 470 */           return this._fromDoubleCreator.call1(dbl);
/* 471 */         } catch (Throwable t0) {
/* 472 */           return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), dbl, (Throwable)
/* 473 */               rewrapCtorProblem(ctxt, t0));
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 478 */     return super.createFromBigDecimal(ctxt, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Double tryConvertToDouble(BigDecimal value) {
/* 486 */     double doubleValue = value.doubleValue();
/* 487 */     return Double.isInfinite(doubleValue) ? null : Double.valueOf(doubleValue);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
/* 493 */     if (this._fromBooleanCreator == null) {
/* 494 */       return super.createFromBoolean(ctxt, value);
/*     */     }
/* 496 */     Boolean arg = Boolean.valueOf(value);
/*     */     try {
/* 498 */       return this._fromBooleanCreator.call1(arg);
/* 499 */     } catch (Throwable t0) {
/* 500 */       return ctxt.handleInstantiationProblem(this._fromBooleanCreator.getDeclaringClass(), arg, (Throwable)
/* 501 */           rewrapCtorProblem(ctxt, t0));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedWithParams getDelegateCreator() {
/* 513 */     return this._delegateCreator;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedWithParams getArrayDelegateCreator() {
/* 518 */     return this._arrayDelegateCreator;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedWithParams getDefaultCreator() {
/* 523 */     return this._defaultCreator;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedWithParams getWithArgsCreator() {
/* 528 */     return this._withArgsCreator;
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
/*     */   @Deprecated
/*     */   protected JsonMappingException wrapException(Throwable t) {
/* 546 */     for (Throwable curr = t; curr != null; curr = curr.getCause()) {
/* 547 */       if (curr instanceof JsonMappingException) {
/* 548 */         return (JsonMappingException)curr;
/*     */       }
/*     */     } 
/* 551 */     return new JsonMappingException(null, "Instantiation of " + 
/* 552 */         getValueTypeDesc() + " value failed: " + ClassUtil.exceptionMessage(t), t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected JsonMappingException unwrapAndWrapException(DeserializationContext ctxt, Throwable t) {
/* 564 */     for (Throwable curr = t; curr != null; curr = curr.getCause()) {
/* 565 */       if (curr instanceof JsonMappingException) {
/* 566 */         return (JsonMappingException)curr;
/*     */       }
/*     */     } 
/* 569 */     return ctxt.instantiationException(getValueClass(), t);
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
/*     */   protected JsonMappingException wrapAsJsonMappingException(DeserializationContext ctxt, Throwable t) {
/* 584 */     if (t instanceof JsonMappingException) {
/* 585 */       return (JsonMappingException)t;
/*     */     }
/* 587 */     return ctxt.instantiationException(getValueClass(), t);
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
/*     */   protected JsonMappingException rewrapCtorProblem(DeserializationContext ctxt, Throwable t) {
/* 602 */     if (t instanceof ExceptionInInitializerError || t instanceof java.lang.reflect.InvocationTargetException) {
/*     */ 
/*     */       
/* 605 */       Throwable cause = t.getCause();
/* 606 */       if (cause != null) {
/* 607 */         t = cause;
/*     */       }
/*     */     } 
/* 610 */     return wrapAsJsonMappingException(ctxt, t);
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
/*     */   private Object _createUsingDelegate(AnnotatedWithParams delegateCreator, SettableBeanProperty[] delegateArguments, DeserializationContext ctxt, Object delegate) throws IOException {
/* 625 */     if (delegateCreator == null) {
/* 626 */       throw new IllegalStateException("No delegate constructor for " + getValueTypeDesc());
/*     */     }
/*     */     
/*     */     try {
/* 630 */       if (delegateArguments == null) {
/* 631 */         return delegateCreator.call1(delegate);
/*     */       }
/*     */       
/* 634 */       int len = delegateArguments.length;
/* 635 */       Object[] args = new Object[len];
/* 636 */       for (int i = 0; i < len; i++) {
/* 637 */         SettableBeanProperty prop = delegateArguments[i];
/* 638 */         if (prop == null) {
/* 639 */           args[i] = delegate;
/*     */         } else {
/* 641 */           args[i] = ctxt.findInjectableValue(prop.getInjectableValueId(), (BeanProperty)prop, null);
/*     */         } 
/*     */       } 
/*     */       
/* 645 */       return delegateCreator.call(args);
/* 646 */     } catch (Throwable t) {
/* 647 */       throw rewrapCtorProblem(ctxt, t);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StdValueInstantiator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */