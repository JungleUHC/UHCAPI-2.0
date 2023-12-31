/*     */ package com.fasterxml.jackson.databind.deser;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*     */ import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
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
/*     */ public abstract class ValueInstantiator
/*     */ {
/*     */   public ValueInstantiator createContextual(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/*  73 */     return this;
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
/*     */   public Class<?> getValueClass() {
/*  93 */     return Object.class;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValueTypeDesc() {
/* 101 */     Class<?> cls = getValueClass();
/* 102 */     if (cls == null) {
/* 103 */       return "UNKNOWN";
/*     */     }
/* 105 */     return cls.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canInstantiate() {
/* 114 */     return (canCreateUsingDefault() || 
/* 115 */       canCreateUsingDelegate() || canCreateUsingArrayDelegate() || 
/* 116 */       canCreateFromObjectWith() || canCreateFromString() || 
/* 117 */       canCreateFromInt() || canCreateFromLong() || 
/* 118 */       canCreateFromDouble() || canCreateFromBoolean());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateFromString() {
/* 128 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateFromInt() {
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateFromLong() {
/* 140 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateFromBigInteger() {
/* 146 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateFromDouble() {
/* 152 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateFromBigDecimal() {
/* 158 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateFromBoolean() {
/* 164 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateUsingDefault() {
/* 172 */     return (getDefaultCreator() != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateUsingDelegate() {
/* 179 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateUsingArrayDelegate() {
/* 188 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canCreateFromObjectWith() {
/* 195 */     return false;
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
/*     */   public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
/* 208 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType getDelegateType(DeserializationConfig config) {
/* 218 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType getArrayDelegateType(DeserializationConfig config) {
/* 229 */     return null;
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
/*     */   public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
/* 248 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no default no-arguments constructor found", new Object[0]);
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
/*     */   public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
/* 262 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no creator with arguments specified", new Object[0]);
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
/*     */   public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer) throws IOException {
/* 288 */     return createFromObjectWith(ctxt, buffer.getParameters(props));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 296 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no delegate creator specified", new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 305 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no array delegate creator specified", new Object[0]);
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
/* 317 */     return ctxt.handleMissingInstantiator(getValueClass(), this, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", new Object[] { value });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
/* 324 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no int/Int-argument constructor/factory method to deserialize from Number value (%s)", new Object[] {
/*     */           
/* 326 */           Integer.valueOf(value) });
/*     */   }
/*     */   
/*     */   public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
/* 330 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no long/Long-argument constructor/factory method to deserialize from Number value (%s)", new Object[] {
/*     */           
/* 332 */           Long.valueOf(value)
/*     */         });
/*     */   }
/*     */   
/*     */   public Object createFromBigInteger(DeserializationContext ctxt, BigInteger value) throws IOException {
/* 337 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no BigInteger-argument constructor/factory method to deserialize from Number value (%s)", new Object[] { value });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
/* 344 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no double/Double-argument constructor/factory method to deserialize from Number value (%s)", new Object[] {
/*     */           
/* 346 */           Double.valueOf(value)
/*     */         });
/*     */   }
/*     */   
/*     */   public Object createFromBigDecimal(DeserializationContext ctxt, BigDecimal value) throws IOException {
/* 351 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no BigDecimal/double/Double-argument constructor/factory method to deserialize from Number value (%s)", new Object[] { value });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
/* 358 */     return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no boolean/Boolean-argument constructor/factory method to deserialize from boolean value (%s)", new Object[] {
/*     */           
/* 360 */           Boolean.valueOf(value)
/*     */         });
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
/*     */   public AnnotatedWithParams getDefaultCreator() {
/* 379 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedWithParams getDelegateCreator() {
/* 389 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedWithParams getArrayDelegateCreator() {
/* 399 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedWithParams getWithArgsCreator() {
/* 410 */     return null;
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
/*     */   protected Object _createFromStringFallbacks(DeserializationContext ctxt, String value) throws IOException {
/* 428 */     if (value.isEmpty() && 
/* 429 */       ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
/* 430 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 438 */     if (canCreateFromBoolean())
/*     */     {
/* 440 */       if (ctxt.findCoercionAction(LogicalType.Boolean, Boolean.class, CoercionInputShape.String) == CoercionAction.TryConvert) {
/*     */         
/* 442 */         String str = value.trim();
/* 443 */         if ("true".equals(str)) {
/* 444 */           return createFromBoolean(ctxt, true);
/*     */         }
/* 446 */         if ("false".equals(str)) {
/* 447 */           return createFromBoolean(ctxt, false);
/*     */         }
/*     */       } 
/*     */     }
/* 451 */     return ctxt.handleMissingInstantiator(getValueClass(), this, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", new Object[] { value });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Gettable
/*     */   {
/*     */     ValueInstantiator getValueInstantiator();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Base
/*     */     extends ValueInstantiator
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     
/*     */     protected final Class<?> _valueType;
/*     */ 
/*     */     
/*     */     public Base(Class<?> type) {
/* 474 */       this._valueType = type;
/*     */     }
/*     */     
/*     */     public Base(JavaType type) {
/* 478 */       this._valueType = type.getRawClass();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getValueTypeDesc() {
/* 483 */       return this._valueType.getName();
/*     */     }
/*     */ 
/*     */     
/*     */     public Class<?> getValueClass() {
/* 488 */       return this._valueType;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Delegating
/*     */     extends ValueInstantiator
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     
/*     */     protected final ValueInstantiator _delegate;
/*     */ 
/*     */ 
/*     */     
/*     */     protected Delegating(ValueInstantiator delegate) {
/* 506 */       this._delegate = delegate;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ValueInstantiator createContextual(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 513 */       ValueInstantiator d = this._delegate.createContextual(ctxt, beanDesc);
/* 514 */       return (d == this._delegate) ? this : new Delegating(d);
/*     */     }
/*     */     protected ValueInstantiator delegate() {
/* 517 */       return this._delegate;
/*     */     }
/*     */     public Class<?> getValueClass() {
/* 520 */       return delegate().getValueClass();
/*     */     }
/*     */     public String getValueTypeDesc() {
/* 523 */       return delegate().getValueTypeDesc();
/*     */     }
/*     */     public boolean canInstantiate() {
/* 526 */       return delegate().canInstantiate();
/*     */     }
/*     */     public boolean canCreateFromString() {
/* 529 */       return delegate().canCreateFromString();
/*     */     } public boolean canCreateFromInt() {
/* 531 */       return delegate().canCreateFromInt();
/*     */     } public boolean canCreateFromLong() {
/* 533 */       return delegate().canCreateFromLong();
/*     */     } public boolean canCreateFromDouble() {
/* 535 */       return delegate().canCreateFromDouble();
/*     */     } public boolean canCreateFromBoolean() {
/* 537 */       return delegate().canCreateFromBoolean();
/*     */     } public boolean canCreateUsingDefault() {
/* 539 */       return delegate().canCreateUsingDefault();
/*     */     } public boolean canCreateUsingDelegate() {
/* 541 */       return delegate().canCreateUsingDelegate();
/*     */     } public boolean canCreateUsingArrayDelegate() {
/* 543 */       return delegate().canCreateUsingArrayDelegate();
/*     */     } public boolean canCreateFromObjectWith() {
/* 545 */       return delegate().canCreateFromObjectWith();
/*     */     }
/*     */     
/*     */     public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
/* 549 */       return delegate().getFromObjectArguments(config);
/*     */     }
/*     */ 
/*     */     
/*     */     public JavaType getDelegateType(DeserializationConfig config) {
/* 554 */       return delegate().getDelegateType(config);
/*     */     }
/*     */ 
/*     */     
/*     */     public JavaType getArrayDelegateType(DeserializationConfig config) {
/* 559 */       return delegate().getArrayDelegateType(config);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
/* 570 */       return delegate().createUsingDefault(ctxt);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
/* 575 */       return delegate().createFromObjectWith(ctxt, args);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer) throws IOException {
/* 582 */       return delegate().createFromObjectWith(ctxt, props, buffer);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 587 */       return delegate().createUsingDelegate(ctxt, delegate);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 592 */       return delegate().createUsingArrayDelegate(ctxt, delegate);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
/* 597 */       return delegate().createFromString(ctxt, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
/* 602 */       return delegate().createFromInt(ctxt, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
/* 607 */       return delegate().createFromLong(ctxt, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createFromBigInteger(DeserializationContext ctxt, BigInteger value) throws IOException {
/* 612 */       return delegate().createFromBigInteger(ctxt, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
/* 617 */       return delegate().createFromDouble(ctxt, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createFromBigDecimal(DeserializationContext ctxt, BigDecimal value) throws IOException {
/* 622 */       return delegate().createFromBigDecimal(ctxt, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
/* 627 */       return delegate().createFromBoolean(ctxt, value);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public AnnotatedWithParams getDefaultCreator() {
/* 637 */       return delegate().getDefaultCreator();
/*     */     }
/*     */     public AnnotatedWithParams getDelegateCreator() {
/* 640 */       return delegate().getDelegateCreator();
/*     */     }
/*     */     public AnnotatedWithParams getArrayDelegateCreator() {
/* 643 */       return delegate().getArrayDelegateCreator();
/*     */     }
/*     */     public AnnotatedWithParams getWithArgsCreator() {
/* 646 */       return delegate().getWithArgsCreator();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/ValueInstantiator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */