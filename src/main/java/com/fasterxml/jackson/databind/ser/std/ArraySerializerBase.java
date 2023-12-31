/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContainerSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
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
/*     */ public abstract class ArraySerializerBase<T>
/*     */   extends ContainerSerializer<T>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   protected final BeanProperty _property;
/*     */   protected final Boolean _unwrapSingle;
/*     */   
/*     */   protected ArraySerializerBase(Class<T> cls) {
/*  39 */     super(cls);
/*  40 */     this._property = null;
/*  41 */     this._unwrapSingle = null;
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
/*     */   protected ArraySerializerBase(Class<T> cls, BeanProperty property) {
/*  53 */     super(cls);
/*  54 */     this._property = property;
/*  55 */     this._unwrapSingle = null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ArraySerializerBase(ArraySerializerBase<?> src) {
/*  60 */     super(src._handledType, false);
/*  61 */     this._property = src._property;
/*  62 */     this._unwrapSingle = src._unwrapSingle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ArraySerializerBase(ArraySerializerBase<?> src, BeanProperty property, Boolean unwrapSingle) {
/*  71 */     super(src._handledType, false);
/*  72 */     this._property = property;
/*  73 */     this._unwrapSingle = unwrapSingle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected ArraySerializerBase(ArraySerializerBase<?> src, BeanProperty property) {
/*  82 */     super(src._handledType, false);
/*  83 */     this._property = property;
/*  84 */     this._unwrapSingle = src._unwrapSingle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonSerializer<?> _withResolved(BeanProperty paramBeanProperty, Boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
/*  97 */     Boolean unwrapSingle = null;
/*     */ 
/*     */     
/* 100 */     if (property != null) {
/* 101 */       JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
/* 102 */       if (format != null) {
/* 103 */         unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
/* 104 */         if (!Objects.equals(unwrapSingle, this._unwrapSingle)) {
/* 105 */           return _withResolved(property, unwrapSingle);
/*     */         }
/*     */       } 
/*     */     } 
/* 109 */     return (JsonSerializer<?>)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 118 */     if (_shouldUnwrapSingle(provider) && 
/* 119 */       hasSingleElement(value)) {
/* 120 */       serializeContents(value, gen, provider);
/*     */       
/*     */       return;
/*     */     } 
/* 124 */     gen.writeStartArray(value);
/* 125 */     serializeContents(value, gen, provider);
/* 126 */     gen.writeEndArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void serializeWithType(T value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 134 */     WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer
/* 135 */         .typeId(value, JsonToken.START_ARRAY));
/*     */     
/* 137 */     g.setCurrentValue(value);
/* 138 */     serializeContents(value, g, provider);
/* 139 */     typeSer.writeTypeSuffix(g, typeIdDef);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void serializeContents(T paramT, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider) throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _shouldUnwrapSingle(SerializerProvider provider) {
/* 149 */     if (this._unwrapSingle == null) {
/* 150 */       return provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
/*     */     }
/* 152 */     return this._unwrapSingle.booleanValue();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/ArraySerializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */