/*     */ package com.fasterxml.jackson.databind.ser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
/*     */ import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeanAsArraySerializer
/*     */   extends BeanSerializerBase
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final BeanSerializerBase _defaultSerializer;
/*     */   
/*     */   public BeanAsArraySerializer(BeanSerializerBase src) {
/*  65 */     super(src, (ObjectIdWriter)null);
/*  66 */     this._defaultSerializer = src;
/*     */   }
/*     */   
/*     */   protected BeanAsArraySerializer(BeanSerializerBase src, Set<String> toIgnore) {
/*  70 */     this(src, toIgnore, (Set<String>)null);
/*     */   }
/*     */   
/*     */   protected BeanAsArraySerializer(BeanSerializerBase src, Set<String> toIgnore, Set<String> toInclude) {
/*  74 */     super(src, toIgnore, toInclude);
/*  75 */     this._defaultSerializer = src;
/*     */   }
/*     */ 
/*     */   
/*     */   protected BeanAsArraySerializer(BeanSerializerBase src, ObjectIdWriter oiw, Object filterId) {
/*  80 */     super(src, oiw, filterId);
/*  81 */     this._defaultSerializer = src;
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
/*     */   public JsonSerializer<Object> unwrappingSerializer(NameTransformer transformer) {
/*  95 */     return this._defaultSerializer.unwrappingSerializer(transformer);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUnwrappingSerializer() {
/* 100 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
/* 106 */     return this._defaultSerializer.withObjectIdWriter(objectIdWriter);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanSerializerBase withFilterId(Object filterId) {
/* 111 */     return new BeanAsArraySerializer(this, this._objectIdWriter, filterId);
/*     */   }
/*     */ 
/*     */   
/*     */   protected BeanAsArraySerializer withByNameInclusion(Set<String> toIgnore, Set<String> toInclude) {
/* 116 */     return new BeanAsArraySerializer(this, toIgnore, toInclude);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase withProperties(BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
/* 124 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase asArraySerializer() {
/* 130 */     return this;
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
/*     */   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 148 */     if (this._objectIdWriter != null) {
/* 149 */       _serializeWithObjectId(bean, gen, provider, typeSer);
/*     */       return;
/*     */     } 
/* 152 */     WritableTypeId typeIdDef = _typeIdDef(typeSer, bean, JsonToken.START_ARRAY);
/* 153 */     typeSer.writeTypePrefix(gen, typeIdDef);
/* 154 */     gen.setCurrentValue(bean);
/* 155 */     serializeAsArray(bean, gen, provider);
/* 156 */     typeSer.writeTypeSuffix(gen, typeIdDef);
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
/*     */   public final void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 168 */     if (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) && 
/* 169 */       hasSingleElement(provider)) {
/* 170 */       serializeAsArray(bean, gen, provider);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 177 */     gen.writeStartArray(bean);
/* 178 */     serializeAsArray(bean, gen, provider);
/* 179 */     gen.writeEndArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasSingleElement(SerializerProvider provider) {
/*     */     BeanPropertyWriter[] props;
/* 190 */     if (this._filteredProps != null && provider.getActiveView() != null) {
/* 191 */       props = this._filteredProps;
/*     */     } else {
/* 193 */       props = this._props;
/*     */     } 
/* 195 */     return (props.length == 1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void serializeAsArray(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*     */     BeanPropertyWriter[] props;
/* 202 */     if (this._filteredProps != null && provider.getActiveView() != null) {
/* 203 */       props = this._filteredProps;
/*     */     } else {
/* 205 */       props = this._props;
/*     */     } 
/*     */     
/* 208 */     int i = 0;
/*     */     try {
/* 210 */       for (int len = props.length; i < len; i++) {
/* 211 */         BeanPropertyWriter prop = props[i];
/* 212 */         if (prop == null) {
/* 213 */           gen.writeNull();
/*     */         } else {
/* 215 */           prop.serializeAsElement(bean, gen, provider);
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 222 */     catch (Exception e) {
/* 223 */       wrapAndThrow(provider, e, bean, props[i].getName());
/* 224 */     } catch (StackOverflowError e) {
/* 225 */       JsonMappingException jsonMappingException = JsonMappingException.from(gen, "Infinite recursion (StackOverflowError)", e);
/* 226 */       jsonMappingException.prependPath(bean, props[i].getName());
/* 227 */       throw jsonMappingException;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 238 */     return "BeanAsArraySerializer for " + handledType().getName();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/impl/BeanAsArraySerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */