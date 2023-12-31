/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.AccessPattern;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class DelegatingDeserializer
/*     */   extends StdDeserializer<Object>
/*     */   implements ContextualDeserializer, ResolvableDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JsonDeserializer<?> _delegatee;
/*     */   
/*     */   public DelegatingDeserializer(JsonDeserializer<?> d) {
/*  38 */     super(d.handledType());
/*  39 */     this._delegatee = d;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> paramJsonDeserializer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
/*  58 */     if (this._delegatee instanceof ResolvableDeserializer) {
/*  59 */       ((ResolvableDeserializer)this._delegatee).resolve(ctxt);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/*  68 */     JavaType vt = ctxt.constructType(this._delegatee.handledType());
/*  69 */     JsonDeserializer<?> del = ctxt.handleSecondaryContextualization(this._delegatee, property, vt);
/*     */     
/*  71 */     if (del == this._delegatee) {
/*  72 */       return this;
/*     */     }
/*  74 */     return newDelegatingInstance(del);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> replaceDelegatee(JsonDeserializer<?> delegatee) {
/*  80 */     if (delegatee == this._delegatee) {
/*  81 */       return this;
/*     */     }
/*  83 */     return newDelegatingInstance(delegatee);
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
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  96 */     return this._delegatee.deserialize(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
/* 105 */     return this._delegatee.deserialize(p, ctxt, intoValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 113 */     return this._delegatee.deserializeWithType(p, ctxt, typeDeserializer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCachable() {
/* 123 */     return this._delegatee.isCachable();
/*     */   }
/*     */   
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 127 */     return this._delegatee.supportsUpdate(config);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> getDelegatee() {
/* 132 */     return this._delegatee;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SettableBeanProperty findBackReference(String logicalName) {
/* 138 */     return this._delegatee.findBackReference(logicalName);
/*     */   }
/*     */ 
/*     */   
/*     */   public AccessPattern getNullAccessPattern() {
/* 143 */     return this._delegatee.getNullAccessPattern();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getNullValue(DeserializationContext ctxt) throws JsonMappingException {
/* 148 */     return this._delegatee.getNullValue(ctxt);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 153 */     return this._delegatee.getEmptyValue(ctxt);
/*     */   }
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 158 */     return this._delegatee.logicalType();
/*     */   }
/*     */   
/*     */   public Collection<Object> getKnownPropertyNames() {
/* 162 */     return this._delegatee.getKnownPropertyNames();
/*     */   }
/*     */   public ObjectIdReader getObjectIdReader() {
/* 165 */     return this._delegatee.getObjectIdReader();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/DelegatingDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */