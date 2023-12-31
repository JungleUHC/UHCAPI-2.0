/*     */ package com.fasterxml.jackson.databind.ser;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
/*     */ import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
/*     */ import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanSerializer;
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
/*     */ public class BeanSerializer
/*     */   extends BeanSerializerBase
/*     */ {
/*     */   private static final long serialVersionUID = 29L;
/*     */   
/*     */   public BeanSerializer(JavaType type, BeanSerializerBuilder builder, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
/*  45 */     super(type, builder, properties, filteredProperties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializer(BeanSerializerBase src) {
/*  54 */     super(src);
/*     */   }
/*     */ 
/*     */   
/*     */   protected BeanSerializer(BeanSerializerBase src, ObjectIdWriter objectIdWriter) {
/*  59 */     super(src, objectIdWriter);
/*     */   }
/*     */ 
/*     */   
/*     */   protected BeanSerializer(BeanSerializerBase src, ObjectIdWriter objectIdWriter, Object filterId) {
/*  64 */     super(src, objectIdWriter, filterId);
/*     */   }
/*     */   
/*     */   protected BeanSerializer(BeanSerializerBase src, Set<String> toIgnore, Set<String> toInclude) {
/*  68 */     super(src, toIgnore, toInclude);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializer(BeanSerializerBase src, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
/*  74 */     super(src, properties, filteredProperties);
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
/*     */   @Deprecated
/*     */   public static BeanSerializer createDummy(JavaType forType) {
/*  89 */     return new BeanSerializer(forType, null, NO_PROPS, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanSerializer createDummy(JavaType forType, BeanSerializerBuilder builder) {
/* 100 */     return new BeanSerializer(forType, builder, NO_PROPS, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<Object> unwrappingSerializer(NameTransformer unwrapper) {
/* 105 */     return (JsonSerializer<Object>)new UnwrappingBeanSerializer(this, unwrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
/* 110 */     return new BeanSerializer(this, objectIdWriter, this._propertyFilterId);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanSerializerBase withFilterId(Object filterId) {
/* 115 */     return new BeanSerializer(this, this._objectIdWriter, filterId);
/*     */   }
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase withByNameInclusion(Set<String> toIgnore, Set<String> toInclude) {
/* 120 */     return new BeanSerializer(this, toIgnore, toInclude);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase withProperties(BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
/* 126 */     return new BeanSerializer(this, properties, filteredProperties);
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
/*     */   protected BeanSerializerBase asArraySerializer() {
/* 144 */     if (this._objectIdWriter == null && this._anyGetterWriter == null && this._propertyFilterId == null)
/*     */     {
/*     */ 
/*     */       
/* 148 */       return (BeanSerializerBase)new BeanAsArraySerializer(this);
/*     */     }
/*     */     
/* 151 */     return this;
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
/*     */   public final void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 169 */     if (this._objectIdWriter != null) {
/* 170 */       gen.setCurrentValue(bean);
/* 171 */       _serializeWithObjectId(bean, gen, provider, true);
/*     */       return;
/*     */     } 
/* 174 */     gen.writeStartObject(bean);
/* 175 */     if (this._propertyFilterId != null) {
/* 176 */       serializeFieldsFiltered(bean, gen, provider);
/*     */     } else {
/* 178 */       serializeFields(bean, gen, provider);
/*     */     } 
/* 180 */     gen.writeEndObject();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 190 */     return "BeanSerializer for " + handledType().getName();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/BeanSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */