/*     */ package com.fasterxml.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.deser.NullValueProvider;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.util.Annotations;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SetterlessProperty
/*     */   extends SettableBeanProperty
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final AnnotatedMethod _annotated;
/*     */   protected final Method _getter;
/*     */   
/*     */   public SetterlessProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedMethod method) {
/*  39 */     super(propDef, type, typeDeser, contextAnnotations);
/*  40 */     this._annotated = method;
/*  41 */     this._getter = method.getAnnotated();
/*     */   }
/*     */ 
/*     */   
/*     */   protected SetterlessProperty(SetterlessProperty src, JsonDeserializer<?> deser, NullValueProvider nva) {
/*  46 */     super(src, deser, nva);
/*  47 */     this._annotated = src._annotated;
/*  48 */     this._getter = src._getter;
/*     */   }
/*     */   
/*     */   protected SetterlessProperty(SetterlessProperty src, PropertyName newName) {
/*  52 */     super(src, newName);
/*  53 */     this._annotated = src._annotated;
/*  54 */     this._getter = src._getter;
/*     */   }
/*     */ 
/*     */   
/*     */   public SettableBeanProperty withName(PropertyName newName) {
/*  59 */     return new SetterlessProperty(this, newName);
/*     */   }
/*     */ 
/*     */   
/*     */   public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
/*  64 */     if (this._valueDeserializer == deser) {
/*  65 */       return this;
/*     */     }
/*     */     
/*  68 */     NullValueProvider nvp = (this._valueDeserializer == this._nullProvider) ? (NullValueProvider)deser : this._nullProvider;
/*  69 */     return new SetterlessProperty(this, deser, nvp);
/*     */   }
/*     */ 
/*     */   
/*     */   public SettableBeanProperty withNullProvider(NullValueProvider nva) {
/*  74 */     return new SetterlessProperty(this, this._valueDeserializer, nva);
/*     */   }
/*     */ 
/*     */   
/*     */   public void fixAccess(DeserializationConfig config) {
/*  79 */     this._annotated.fixAccess(config
/*  80 */         .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> acls) {
/*  91 */     return (A)this._annotated.getAnnotation(acls);
/*     */   }
/*     */   public AnnotatedMember getMember() {
/*  94 */     return (AnnotatedMember)this._annotated;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
/*     */     Object toModify;
/* 106 */     if (p.hasToken(JsonToken.VALUE_NULL)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 112 */     if (this._valueTypeDeserializer != null) {
/* 113 */       ctxt.reportBadDefinition(getType(), String.format("Problem deserializing 'setterless' property (\"%s\"): no way to handle typed deser with setterless yet", new Object[] {
/*     */               
/* 115 */               getName()
/*     */             }));
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 121 */       toModify = this._getter.invoke(instance, (Object[])null);
/* 122 */     } catch (Exception e) {
/* 123 */       _throwAsIOE(p, e);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 129 */     if (toModify == null)
/* 130 */       ctxt.reportBadDefinition(getType(), String.format("Problem deserializing 'setterless' property '%s': get method returned null", new Object[] {
/*     */               
/* 132 */               getName()
/*     */             })); 
/* 134 */     this._valueDeserializer.deserialize(p, ctxt, toModify);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
/* 141 */     deserializeAndSet(p, ctxt, instance);
/* 142 */     return instance;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void set(Object instance, Object value) throws IOException {
/* 147 */     throw new UnsupportedOperationException("Should never call `set()` on setterless property ('" + getName() + "')");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object setAndReturn(Object instance, Object value) throws IOException {
/* 153 */     set(instance, value);
/* 154 */     return instance;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/SetterlessProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */