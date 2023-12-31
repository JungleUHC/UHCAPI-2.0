/*     */ package com.fasterxml.jackson.databind.ser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
/*     */ import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
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
/*     */ public class UnwrappingBeanSerializer
/*     */   extends BeanSerializerBase
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final NameTransformer _nameTransformer;
/*     */   
/*     */   public UnwrappingBeanSerializer(BeanSerializerBase src, NameTransformer transformer) {
/*  37 */     super(src, transformer);
/*  38 */     this._nameTransformer = transformer;
/*     */   }
/*     */ 
/*     */   
/*     */   public UnwrappingBeanSerializer(UnwrappingBeanSerializer src, ObjectIdWriter objectIdWriter) {
/*  43 */     super(src, objectIdWriter);
/*  44 */     this._nameTransformer = src._nameTransformer;
/*     */   }
/*     */ 
/*     */   
/*     */   public UnwrappingBeanSerializer(UnwrappingBeanSerializer src, ObjectIdWriter objectIdWriter, Object filterId) {
/*  49 */     super(src, objectIdWriter, filterId);
/*  50 */     this._nameTransformer = src._nameTransformer;
/*     */   }
/*     */   
/*     */   protected UnwrappingBeanSerializer(UnwrappingBeanSerializer src, Set<String> toIgnore) {
/*  54 */     this(src, toIgnore, (Set<String>)null);
/*     */   }
/*     */   
/*     */   protected UnwrappingBeanSerializer(UnwrappingBeanSerializer src, Set<String> toIgnore, Set<String> toInclude) {
/*  58 */     super(src, toIgnore, toInclude);
/*  59 */     this._nameTransformer = src._nameTransformer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected UnwrappingBeanSerializer(UnwrappingBeanSerializer src, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
/*  65 */     super(src, properties, filteredProperties);
/*  66 */     this._nameTransformer = src._nameTransformer;
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
/*     */   public JsonSerializer<Object> unwrappingSerializer(NameTransformer transformer) {
/*  78 */     return (JsonSerializer<Object>)new UnwrappingBeanSerializer(this, transformer);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUnwrappingSerializer() {
/*  83 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
/*  88 */     return new UnwrappingBeanSerializer(this, objectIdWriter);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanSerializerBase withFilterId(Object filterId) {
/*  93 */     return new UnwrappingBeanSerializer(this, this._objectIdWriter, filterId);
/*     */   }
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase withByNameInclusion(Set<String> toIgnore, Set<String> toInclude) {
/*  98 */     return new UnwrappingBeanSerializer(this, toIgnore, toInclude);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase withProperties(BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
/* 104 */     return new UnwrappingBeanSerializer(this, properties, filteredProperties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase asArraySerializer() {
/* 113 */     return this;
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
/*     */   public final void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 130 */     gen.setCurrentValue(bean);
/* 131 */     if (this._objectIdWriter != null) {
/* 132 */       _serializeWithObjectId(bean, gen, provider, false);
/*     */       return;
/*     */     } 
/* 135 */     if (this._propertyFilterId != null) {
/* 136 */       serializeFieldsFiltered(bean, gen, provider);
/*     */     } else {
/* 138 */       serializeFields(bean, gen, provider);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 146 */     if (provider.isEnabled(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)) {
/* 147 */       provider.reportBadDefinition(handledType(), "Unwrapped property requires use of type information: cannot serialize without disabling `SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS`");
/*     */     }
/*     */     
/* 150 */     gen.setCurrentValue(bean);
/* 151 */     if (this._objectIdWriter != null) {
/* 152 */       _serializeWithObjectId(bean, gen, provider, typeSer);
/*     */       return;
/*     */     } 
/* 155 */     if (this._propertyFilterId != null) {
/* 156 */       serializeFieldsFiltered(bean, gen, provider);
/*     */     } else {
/* 158 */       serializeFields(bean, gen, provider);
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
/* 169 */     return "UnwrappingBeanSerializer for " + handledType().getName();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/impl/UnwrappingBeanSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */