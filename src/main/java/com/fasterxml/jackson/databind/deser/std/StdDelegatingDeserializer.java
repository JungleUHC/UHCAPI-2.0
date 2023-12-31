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
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.Converter;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StdDelegatingDeserializer<T>
/*     */   extends StdDeserializer<T>
/*     */   implements ContextualDeserializer, ResolvableDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Converter<Object, T> _converter;
/*     */   protected final JavaType _delegateType;
/*     */   protected final JsonDeserializer<Object> _delegateDeserializer;
/*     */   
/*     */   public StdDelegatingDeserializer(Converter<?, T> converter) {
/*  66 */     super(Object.class);
/*  67 */     this._converter = (Converter)converter;
/*  68 */     this._delegateType = null;
/*  69 */     this._delegateDeserializer = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdDelegatingDeserializer(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer) {
/*  76 */     super(delegateType);
/*  77 */     this._converter = converter;
/*  78 */     this._delegateType = delegateType;
/*  79 */     this._delegateDeserializer = (JsonDeserializer)delegateDeserializer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StdDelegatingDeserializer(StdDelegatingDeserializer<T> src) {
/*  87 */     super(src);
/*  88 */     this._converter = src._converter;
/*  89 */     this._delegateType = src._delegateType;
/*  90 */     this._delegateDeserializer = src._delegateDeserializer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StdDelegatingDeserializer<T> withDelegate(Converter<Object, T> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer) {
/* 100 */     ClassUtil.verifyMustOverride(StdDelegatingDeserializer.class, this, "withDelegate");
/* 101 */     return new StdDelegatingDeserializer(converter, delegateType, delegateDeserializer);
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
/*     */   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
/* 116 */     if (this._delegateDeserializer != null && this._delegateDeserializer instanceof ResolvableDeserializer) {
/* 117 */       ((ResolvableDeserializer)this._delegateDeserializer).resolve(ctxt);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 126 */     if (this._delegateDeserializer != null) {
/* 127 */       JsonDeserializer<?> deser = ctxt.handleSecondaryContextualization(this._delegateDeserializer, property, this._delegateType);
/*     */       
/* 129 */       if (deser != this._delegateDeserializer) {
/* 130 */         return withDelegate(this._converter, this._delegateType, deser);
/*     */       }
/* 132 */       return this;
/*     */     } 
/*     */     
/* 135 */     JavaType delegateType = this._converter.getInputType(ctxt.getTypeFactory());
/* 136 */     return withDelegate(this._converter, delegateType, ctxt
/* 137 */         .findContextualValueDeserializer(delegateType, property));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> getDelegatee() {
/* 148 */     return this._delegateDeserializer;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> handledType() {
/* 153 */     return this._delegateDeserializer.handledType();
/*     */   }
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 158 */     return this._delegateDeserializer.logicalType();
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 163 */     return this._delegateDeserializer.supportsUpdate(config);
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
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 175 */     Object delegateValue = this._delegateDeserializer.deserialize(p, ctxt);
/* 176 */     if (delegateValue == null) {
/* 177 */       return null;
/*     */     }
/* 179 */     return convertValue(delegateValue);
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
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 197 */     Object delegateValue = this._delegateDeserializer.deserialize(p, ctxt);
/* 198 */     if (delegateValue == null) {
/* 199 */       return null;
/*     */     }
/* 201 */     return convertValue(delegateValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
/* 209 */     if (this._delegateType.getRawClass().isAssignableFrom(intoValue.getClass())) {
/* 210 */       return (T)this._delegateDeserializer.deserialize(p, ctxt, intoValue);
/*     */     }
/* 212 */     return (T)_handleIncompatibleUpdateValue(p, ctxt, intoValue);
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
/*     */   protected Object _handleIncompatibleUpdateValue(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
/* 228 */     throw new UnsupportedOperationException(
/* 229 */         String.format("Cannot update object of type %s (using deserializer for type %s)" + intoValue
/* 230 */           .getClass().getName(), new Object[] { this._delegateType }));
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
/*     */   protected T convertValue(Object delegateValue) {
/* 252 */     return (T)this._converter.convert(delegateValue);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/StdDelegatingDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */