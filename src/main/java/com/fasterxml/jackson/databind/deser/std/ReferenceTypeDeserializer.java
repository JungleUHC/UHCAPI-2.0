/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.AccessPattern;
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
/*     */ public abstract class ReferenceTypeDeserializer<T>
/*     */   extends StdDeserializer<T>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   protected final JavaType _fullType;
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   protected final JsonDeserializer<Object> _valueDeserializer;
/*     */   
/*     */   public ReferenceTypeDeserializer(JavaType fullType, ValueInstantiator vi, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
/*  50 */     super(fullType);
/*  51 */     this._valueInstantiator = vi;
/*  52 */     this._fullType = fullType;
/*  53 */     this._valueDeserializer = (JsonDeserializer)deser;
/*  54 */     this._valueTypeDeserializer = typeDeser;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ReferenceTypeDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
/*  61 */     this(fullType, null, typeDeser, deser);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/*  68 */     JsonDeserializer<?> deser = this._valueDeserializer;
/*  69 */     if (deser == null) {
/*  70 */       deser = ctxt.findContextualValueDeserializer(this._fullType.getReferencedType(), property);
/*     */     } else {
/*  72 */       deser = ctxt.handleSecondaryContextualization(deser, property, this._fullType.getReferencedType());
/*     */     } 
/*  74 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*  75 */     if (typeDeser != null) {
/*  76 */       typeDeser = typeDeser.forProperty(property);
/*     */     }
/*     */     
/*  79 */     if (deser == this._valueDeserializer && typeDeser == this._valueTypeDeserializer) {
/*  80 */       return this;
/*     */     }
/*  82 */     return withResolved(typeDeser, deser);
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
/*     */   public AccessPattern getNullAccessPattern() {
/*  97 */     return AccessPattern.DYNAMIC;
/*     */   }
/*     */ 
/*     */   
/*     */   public AccessPattern getEmptyAccessPattern() {
/* 102 */     return AccessPattern.DYNAMIC;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract ReferenceTypeDeserializer<T> withResolved(TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract T getNullValue(DeserializationContext paramDeserializationContext) throws JsonMappingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
/* 126 */     return getNullValue(ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract T referenceValue(Object paramObject);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract T updateReference(T paramT, Object paramObject);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Object getReferenced(T paramT);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueInstantiator getValueInstantiator() {
/* 164 */     return this._valueInstantiator;
/*     */   }
/*     */   public JavaType getValueType() {
/* 167 */     return this._fullType;
/*     */   }
/*     */   
/*     */   public LogicalType logicalType() {
/* 171 */     if (this._valueDeserializer != null) {
/* 172 */       return this._valueDeserializer.logicalType();
/*     */     }
/* 174 */     return super.logicalType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 184 */     return (this._valueDeserializer == null) ? null : this._valueDeserializer
/* 185 */       .supportsUpdate(config);
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
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 198 */     if (this._valueInstantiator != null) {
/*     */       
/* 200 */       T value = (T)this._valueInstantiator.createUsingDefault(ctxt);
/* 201 */       return deserialize(p, ctxt, value);
/*     */     } 
/*     */ 
/*     */     
/* 205 */     Object contents = (this._valueTypeDeserializer == null) ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
/* 206 */     return referenceValue(contents);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt, T reference) throws IOException {
/*     */     Object contents;
/* 214 */     Boolean B = this._valueDeserializer.supportsUpdate(ctxt.getConfig());
/*     */     
/* 216 */     if (B.equals(Boolean.FALSE) || this._valueTypeDeserializer != null) {
/*     */ 
/*     */       
/* 219 */       contents = (this._valueTypeDeserializer == null) ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
/*     */     } else {
/*     */       
/* 222 */       contents = getReferenced(reference);
/*     */       
/* 224 */       if (contents == null) {
/*     */ 
/*     */         
/* 227 */         contents = (this._valueTypeDeserializer == null) ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
/* 228 */         return referenceValue(contents);
/*     */       } 
/* 230 */       contents = this._valueDeserializer.deserialize(p, ctxt, contents);
/*     */     } 
/*     */     
/* 233 */     return updateReference(reference, contents);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 240 */     if (p.hasToken(JsonToken.VALUE_NULL)) {
/* 241 */       return getNullValue(ctxt);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 255 */     if (this._valueTypeDeserializer == null) {
/* 256 */       return deserialize(p, ctxt);
/*     */     }
/* 258 */     return referenceValue(this._valueTypeDeserializer.deserializeTypedFromAny(p, ctxt));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/ReferenceTypeDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */