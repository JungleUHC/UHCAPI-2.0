/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
/*     */ import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.node.ObjectNode;
/*     */ import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
/*     */ import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
/*     */ import com.fasterxml.jackson.databind.ser.ContainerSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.PropertyFilter;
/*     */ import com.fasterxml.jackson.databind.ser.PropertyWriter;
/*     */ import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
/*     */ import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
/*     */ import com.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
/*     */ import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
/*     */ import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.Converter;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class BeanSerializerBase extends StdSerializer<Object> implements ContextualSerializer, ResolvableSerializer, JsonFormatVisitable, SchemaAware {
/*  45 */   protected static final PropertyName NAME_FOR_OBJECT_REF = new PropertyName("#object-ref");
/*     */   
/*  47 */   protected static final BeanPropertyWriter[] NO_PROPS = new BeanPropertyWriter[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JavaType _beanType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final BeanPropertyWriter[] _props;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final BeanPropertyWriter[] _filteredProps;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AnyGetterWriter _anyGetterWriter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Object _propertyFilterId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AnnotatedMember _typeId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ObjectIdWriter _objectIdWriter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonFormat.Shape _serializationShape;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase(JavaType type, BeanSerializerBuilder builder, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
/* 116 */     super(type);
/* 117 */     this._beanType = type;
/* 118 */     this._props = properties;
/* 119 */     this._filteredProps = filteredProperties;
/* 120 */     if (builder == null) {
/*     */ 
/*     */       
/* 123 */       this._typeId = null;
/* 124 */       this._anyGetterWriter = null;
/* 125 */       this._propertyFilterId = null;
/* 126 */       this._objectIdWriter = null;
/* 127 */       this._serializationShape = null;
/*     */     } else {
/* 129 */       this._typeId = builder.getTypeId();
/* 130 */       this._anyGetterWriter = builder.getAnyGetter();
/* 131 */       this._propertyFilterId = builder.getFilterId();
/* 132 */       this._objectIdWriter = builder.getObjectIdWriter();
/* 133 */       JsonFormat.Value format = builder.getBeanDescription().findExpectedFormat(null);
/* 134 */       this._serializationShape = format.getShape();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase(BeanSerializerBase src, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
/* 141 */     super(src._handledType);
/* 142 */     this._beanType = src._beanType;
/* 143 */     this._props = properties;
/* 144 */     this._filteredProps = filteredProperties;
/*     */     
/* 146 */     this._typeId = src._typeId;
/* 147 */     this._anyGetterWriter = src._anyGetterWriter;
/* 148 */     this._objectIdWriter = src._objectIdWriter;
/* 149 */     this._propertyFilterId = src._propertyFilterId;
/* 150 */     this._serializationShape = src._serializationShape;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter) {
/* 156 */     this(src, objectIdWriter, src._propertyFilterId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter, Object filterId) {
/* 165 */     super(src._handledType);
/* 166 */     this._beanType = src._beanType;
/* 167 */     this._props = src._props;
/* 168 */     this._filteredProps = src._filteredProps;
/*     */     
/* 170 */     this._typeId = src._typeId;
/* 171 */     this._anyGetterWriter = src._anyGetterWriter;
/* 172 */     this._objectIdWriter = objectIdWriter;
/* 173 */     this._propertyFilterId = filterId;
/* 174 */     this._serializationShape = src._serializationShape;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected BeanSerializerBase(BeanSerializerBase src, String[] toIgnore) {
/* 180 */     this(src, ArrayBuilders.arrayToSet((Object[])toIgnore), (Set<String>)null);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected BeanSerializerBase(BeanSerializerBase src, Set<String> toIgnore) {
/* 185 */     this(src, toIgnore, (Set<String>)null);
/*     */   }
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase(BeanSerializerBase src, Set<String> toIgnore, Set<String> toInclude) {
/* 190 */     super(src._handledType);
/*     */     
/* 192 */     this._beanType = src._beanType;
/* 193 */     BeanPropertyWriter[] propsIn = src._props;
/* 194 */     BeanPropertyWriter[] fpropsIn = src._filteredProps;
/* 195 */     int len = propsIn.length;
/*     */     
/* 197 */     ArrayList<BeanPropertyWriter> propsOut = new ArrayList<>(len);
/* 198 */     ArrayList<BeanPropertyWriter> fpropsOut = (fpropsIn == null) ? null : new ArrayList<>(len);
/*     */     
/* 200 */     for (int i = 0; i < len; i++) {
/* 201 */       BeanPropertyWriter bpw = propsIn[i];
/*     */       
/* 203 */       if (!IgnorePropertiesUtil.shouldIgnore(bpw.getName(), toIgnore, toInclude)) {
/*     */ 
/*     */         
/* 206 */         propsOut.add(bpw);
/* 207 */         if (fpropsIn != null)
/* 208 */           fpropsOut.add(fpropsIn[i]); 
/*     */       } 
/*     */     } 
/* 211 */     this._props = propsOut.<BeanPropertyWriter>toArray(new BeanPropertyWriter[propsOut.size()]);
/* 212 */     this._filteredProps = (fpropsOut == null) ? null : fpropsOut.<BeanPropertyWriter>toArray(new BeanPropertyWriter[fpropsOut.size()]);
/*     */     
/* 214 */     this._typeId = src._typeId;
/* 215 */     this._anyGetterWriter = src._anyGetterWriter;
/* 216 */     this._objectIdWriter = src._objectIdWriter;
/* 217 */     this._propertyFilterId = src._propertyFilterId;
/* 218 */     this._serializationShape = src._serializationShape;
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
/*     */   @Deprecated
/*     */   protected BeanSerializerBase withIgnorals(Set<String> toIgnore) {
/* 238 */     return withByNameInclusion(toIgnore, (Set<String>)null);
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
/*     */   @Deprecated
/*     */   protected BeanSerializerBase withIgnorals(String[] toIgnore) {
/* 258 */     return withIgnorals(ArrayBuilders.arrayToSet((Object[])toIgnore));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase(BeanSerializerBase src) {
/* 296 */     this(src, src._props, src._filteredProps);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerBase(BeanSerializerBase src, NameTransformer unwrapper) {
/* 304 */     this(src, rename(src._props, unwrapper), rename(src._filteredProps, unwrapper));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final BeanPropertyWriter[] rename(BeanPropertyWriter[] props, NameTransformer transformer) {
/* 310 */     if (props == null || props.length == 0 || transformer == null || transformer == NameTransformer.NOP) {
/* 311 */       return props;
/*     */     }
/* 313 */     int len = props.length;
/* 314 */     BeanPropertyWriter[] result = new BeanPropertyWriter[len];
/* 315 */     for (int i = 0; i < len; i++) {
/* 316 */       BeanPropertyWriter bpw = props[i];
/* 317 */       if (bpw != null) {
/* 318 */         result[i] = bpw.rename(transformer);
/*     */       }
/*     */     } 
/* 321 */     return result;
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
/*     */   public void resolve(SerializerProvider provider) throws JsonMappingException {
/* 338 */     int filteredCount = (this._filteredProps == null) ? 0 : this._filteredProps.length;
/* 339 */     for (int i = 0, len = this._props.length; i < len; i++) {
/* 340 */       ContainerSerializer containerSerializer; BeanPropertyWriter prop = this._props[i];
/*     */       
/* 342 */       if (!prop.willSuppressNulls() && !prop.hasNullSerializer()) {
/* 343 */         JsonSerializer<Object> nullSer = provider.findNullValueSerializer((BeanProperty)prop);
/* 344 */         if (nullSer != null) {
/* 345 */           prop.assignNullSerializer(nullSer);
/*     */           
/* 347 */           if (i < filteredCount) {
/* 348 */             BeanPropertyWriter w2 = this._filteredProps[i];
/* 349 */             if (w2 != null) {
/* 350 */               w2.assignNullSerializer(nullSer);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 356 */       if (prop.hasSerializer()) {
/*     */         continue;
/*     */       }
/*     */       
/* 360 */       JsonSerializer<Object> ser = findConvertingSerializer(provider, prop);
/* 361 */       if (ser == null) {
/*     */         
/* 363 */         JavaType type = prop.getSerializationType();
/*     */ 
/*     */ 
/*     */         
/* 367 */         if (type == null) {
/* 368 */           type = prop.getType();
/* 369 */           if (!type.isFinal()) {
/* 370 */             if (type.isContainerType() || type.containedTypeCount() > 0) {
/* 371 */               prop.setNonTrivialBaseType(type);
/*     */             }
/*     */             continue;
/*     */           } 
/*     */         } 
/* 376 */         ser = provider.findValueSerializer(type, (BeanProperty)prop);
/*     */ 
/*     */         
/* 379 */         if (type.isContainerType()) {
/* 380 */           TypeSerializer typeSer = (TypeSerializer)type.getContentType().getTypeHandler();
/* 381 */           if (typeSer != null)
/*     */           {
/* 383 */             if (ser instanceof ContainerSerializer) {
/*     */ 
/*     */               
/* 386 */               ContainerSerializer containerSerializer1 = ((ContainerSerializer)ser).withValueTypeSerializer(typeSer);
/* 387 */               containerSerializer = containerSerializer1;
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 393 */       if (i < filteredCount) {
/* 394 */         BeanPropertyWriter w2 = this._filteredProps[i];
/* 395 */         if (w2 != null) {
/* 396 */           w2.assignSerializer((JsonSerializer)containerSerializer);
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */       } 
/*     */       
/* 403 */       prop.assignSerializer((JsonSerializer)containerSerializer);
/*     */       
/*     */       continue;
/*     */     } 
/* 407 */     if (this._anyGetterWriter != null)
/*     */     {
/* 409 */       this._anyGetterWriter.resolve(provider);
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonSerializer<Object> findConvertingSerializer(SerializerProvider provider, BeanPropertyWriter prop) throws JsonMappingException {
/* 424 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/* 425 */     if (intr != null) {
/* 426 */       AnnotatedMember m = prop.getMember();
/* 427 */       if (m != null) {
/* 428 */         Object convDef = intr.findSerializationConverter((Annotated)m);
/* 429 */         if (convDef != null) {
/* 430 */           Converter<Object, Object> conv = provider.converterInstance((Annotated)prop.getMember(), convDef);
/* 431 */           JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
/*     */ 
/*     */           
/* 434 */           JsonSerializer<?> ser = delegateType.isJavaLangObject() ? null : provider.findValueSerializer(delegateType, (BeanProperty)prop);
/* 435 */           return new StdDelegatingSerializer(conv, delegateType, ser);
/*     */         } 
/*     */       } 
/*     */     } 
/* 439 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/* 448 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/*     */     
/* 450 */     AnnotatedMember accessor = (property == null || intr == null) ? null : property.getMember();
/* 451 */     SerializationConfig config = provider.getConfig();
/*     */ 
/*     */ 
/*     */     
/* 455 */     JsonFormat.Value format = findFormatOverrides(provider, property, this._handledType);
/* 456 */     JsonFormat.Shape shape = null;
/* 457 */     if (format != null && format.hasShape()) {
/* 458 */       shape = format.getShape();
/*     */       
/* 460 */       if (shape != JsonFormat.Shape.ANY && shape != this._serializationShape) {
/* 461 */         if (this._beanType.isEnumType()) {
/* 462 */           BeanDescription desc; JsonSerializer<?> ser; switch (shape) {
/*     */ 
/*     */             
/*     */             case STRING:
/*     */             case NUMBER:
/*     */             case NUMBER_INT:
/* 468 */               desc = config.introspectClassAnnotations(this._beanType);
/* 469 */               ser = EnumSerializer.construct(this._beanType.getRawClass(), provider
/* 470 */                   .getConfig(), desc, format);
/* 471 */               return provider.handlePrimaryContextualization(ser, property);
/*     */           } 
/*     */         
/* 474 */         } else if (shape == JsonFormat.Shape.NATURAL && (
/* 475 */           !this._beanType.isMapLikeType() || !Map.class.isAssignableFrom(this._handledType))) {
/*     */           
/* 477 */           if (Map.Entry.class.isAssignableFrom(this._handledType)) {
/* 478 */             JavaType mapEntryType = this._beanType.findSuperType(Map.Entry.class);
/*     */             
/* 480 */             JavaType kt = mapEntryType.containedTypeOrUnknown(0);
/* 481 */             JavaType vt = mapEntryType.containedTypeOrUnknown(1);
/*     */ 
/*     */ 
/*     */             
/* 485 */             MapEntrySerializer mapEntrySerializer = new MapEntrySerializer(this._beanType, kt, vt, false, null, property);
/*     */             
/* 487 */             return provider.handlePrimaryContextualization((JsonSerializer)mapEntrySerializer, property);
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 493 */     ObjectIdWriter oiw = this._objectIdWriter;
/*     */ 
/*     */ 
/*     */     
/* 497 */     int idPropOrigIndex = 0;
/* 498 */     Set<String> ignoredProps = null;
/* 499 */     Set<String> includedProps = null;
/* 500 */     Object newFilterId = null;
/*     */ 
/*     */     
/* 503 */     if (accessor != null) {
/* 504 */       ignoredProps = intr.findPropertyIgnoralByName((MapperConfig)config, (Annotated)accessor).findIgnoredForSerialization();
/* 505 */       includedProps = intr.findPropertyInclusionByName((MapperConfig)config, (Annotated)accessor).getIncluded();
/* 506 */       ObjectIdInfo objectIdInfo = intr.findObjectIdInfo((Annotated)accessor);
/* 507 */       if (objectIdInfo == null) {
/*     */         
/* 509 */         if (oiw != null) {
/* 510 */           objectIdInfo = intr.findObjectReferenceInfo((Annotated)accessor, null);
/* 511 */           if (objectIdInfo != null) {
/* 512 */             oiw = this._objectIdWriter.withAlwaysAsId(objectIdInfo.getAlwaysAsId());
/*     */           
/*     */           }
/*     */         }
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 520 */         objectIdInfo = intr.findObjectReferenceInfo((Annotated)accessor, objectIdInfo);
/* 521 */         Class<?> implClass = objectIdInfo.getGeneratorType();
/* 522 */         JavaType type = provider.constructType(implClass);
/* 523 */         JavaType idType = provider.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/*     */         
/* 525 */         if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
/* 526 */           String propName = objectIdInfo.getPropertyName().getSimpleName();
/* 527 */           BeanPropertyWriter idProp = null;
/*     */           
/* 529 */           for (int i = 0, len = this._props.length;; i++) {
/* 530 */             if (i == len)
/* 531 */               provider.reportBadDefinition(this._beanType, String.format("Invalid Object Id definition for %s: cannot find property with name %s", new Object[] {
/*     */                       
/* 533 */                       ClassUtil.nameOf(handledType()), ClassUtil.name(propName)
/*     */                     })); 
/* 535 */             BeanPropertyWriter prop = this._props[i];
/* 536 */             if (propName.equals(prop.getName())) {
/* 537 */               idProp = prop;
/*     */ 
/*     */               
/* 540 */               idPropOrigIndex = i;
/*     */               break;
/*     */             } 
/*     */           } 
/* 544 */           idType = idProp.getType();
/* 545 */           PropertyBasedObjectIdGenerator propertyBasedObjectIdGenerator = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
/* 546 */           oiw = ObjectIdWriter.construct(idType, (PropertyName)null, (ObjectIdGenerator)propertyBasedObjectIdGenerator, objectIdInfo.getAlwaysAsId());
/*     */         } else {
/* 548 */           ObjectIdGenerator<?> gen = provider.objectIdGeneratorInstance((Annotated)accessor, objectIdInfo);
/* 549 */           oiw = ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen, objectIdInfo
/* 550 */               .getAlwaysAsId());
/*     */         } 
/*     */       } 
/*     */       
/* 554 */       Object filterId = intr.findFilterId((Annotated)accessor);
/* 555 */       if (filterId != null)
/*     */       {
/* 557 */         if (this._propertyFilterId == null || !filterId.equals(this._propertyFilterId)) {
/* 558 */           newFilterId = filterId;
/*     */         }
/*     */       }
/*     */     } 
/*     */     
/* 563 */     BeanSerializerBase contextual = this;
/*     */ 
/*     */     
/* 566 */     if (idPropOrigIndex > 0) {
/* 567 */       BeanPropertyWriter[] newFiltered, newProps = Arrays.<BeanPropertyWriter>copyOf(this._props, this._props.length);
/* 568 */       BeanPropertyWriter bpw = newProps[idPropOrigIndex];
/* 569 */       System.arraycopy(newProps, 0, newProps, 1, idPropOrigIndex);
/* 570 */       newProps[0] = bpw;
/*     */       
/* 572 */       if (this._filteredProps == null) {
/* 573 */         newFiltered = null;
/*     */       } else {
/* 575 */         newFiltered = Arrays.<BeanPropertyWriter>copyOf(this._filteredProps, this._filteredProps.length);
/* 576 */         bpw = newFiltered[idPropOrigIndex];
/* 577 */         System.arraycopy(newFiltered, 0, newFiltered, 1, idPropOrigIndex);
/* 578 */         newFiltered[0] = bpw;
/*     */       } 
/* 580 */       contextual = contextual.withProperties(newProps, newFiltered);
/*     */     } 
/*     */     
/* 583 */     if (oiw != null) {
/* 584 */       JsonSerializer<?> ser = provider.findValueSerializer(oiw.idType, property);
/* 585 */       oiw = oiw.withSerializer(ser);
/* 586 */       if (oiw != this._objectIdWriter) {
/* 587 */         contextual = contextual.withObjectIdWriter(oiw);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 592 */     if ((ignoredProps != null && !ignoredProps.isEmpty()) || includedProps != null)
/*     */     {
/* 594 */       contextual = contextual.withByNameInclusion(ignoredProps, includedProps);
/*     */     }
/* 596 */     if (newFilterId != null) {
/* 597 */       contextual = contextual.withFilterId(newFilterId);
/*     */     }
/*     */     
/* 600 */     if (shape == null) {
/* 601 */       shape = this._serializationShape;
/*     */     }
/*     */     
/* 604 */     if (shape == JsonFormat.Shape.ARRAY) {
/* 605 */       return contextual.asArraySerializer();
/*     */     }
/* 607 */     return contextual;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<PropertyWriter> properties() {
/* 618 */     return Arrays.asList((Object[])this._props).iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean usesObjectId() {
/* 629 */     return (this._objectIdWriter != null);
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
/*     */   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 643 */     if (this._objectIdWriter != null) {
/*     */ 
/*     */ 
/*     */       
/* 647 */       _serializeWithObjectId(bean, gen, provider, typeSer);
/*     */       
/*     */       return;
/*     */     } 
/* 651 */     WritableTypeId typeIdDef = _typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
/* 652 */     typeSer.writeTypePrefix(gen, typeIdDef);
/* 653 */     gen.setCurrentValue(bean);
/* 654 */     if (this._propertyFilterId != null) {
/* 655 */       serializeFieldsFiltered(bean, gen, provider);
/*     */     } else {
/* 657 */       serializeFields(bean, gen, provider);
/*     */     } 
/* 659 */     typeSer.writeTypeSuffix(gen, typeIdDef);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, boolean startEndObject) throws IOException {
/* 665 */     ObjectIdWriter w = this._objectIdWriter;
/* 666 */     WritableObjectId objectId = provider.findObjectId(bean, w.generator);
/*     */     
/* 668 */     if (objectId.writeAsId(gen, provider, w)) {
/*     */       return;
/*     */     }
/*     */     
/* 672 */     Object id = objectId.generateId(bean);
/* 673 */     if (w.alwaysAsId) {
/* 674 */       w.serializer.serialize(id, gen, provider);
/*     */       return;
/*     */     } 
/* 677 */     if (startEndObject) {
/* 678 */       gen.writeStartObject(bean);
/*     */     }
/* 680 */     objectId.writeAsField(gen, provider, w);
/* 681 */     if (this._propertyFilterId != null) {
/* 682 */       serializeFieldsFiltered(bean, gen, provider);
/*     */     } else {
/* 684 */       serializeFields(bean, gen, provider);
/*     */     } 
/* 686 */     if (startEndObject) {
/* 687 */       gen.writeEndObject();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 694 */     ObjectIdWriter w = this._objectIdWriter;
/* 695 */     WritableObjectId objectId = provider.findObjectId(bean, w.generator);
/*     */     
/* 697 */     if (objectId.writeAsId(gen, provider, w)) {
/*     */       return;
/*     */     }
/*     */     
/* 701 */     Object id = objectId.generateId(bean);
/* 702 */     if (w.alwaysAsId) {
/* 703 */       w.serializer.serialize(id, gen, provider);
/*     */       return;
/*     */     } 
/* 706 */     _serializeObjectId(bean, gen, provider, typeSer, objectId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _serializeObjectId(Object bean, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer, WritableObjectId objectId) throws IOException {
/* 713 */     ObjectIdWriter w = this._objectIdWriter;
/* 714 */     WritableTypeId typeIdDef = _typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
/*     */     
/* 716 */     typeSer.writeTypePrefix(g, typeIdDef);
/*     */     
/* 718 */     g.setCurrentValue(bean);
/* 719 */     objectId.writeAsField(g, provider, w);
/* 720 */     if (this._propertyFilterId != null) {
/* 721 */       serializeFieldsFiltered(bean, g, provider);
/*     */     } else {
/* 723 */       serializeFields(bean, g, provider);
/*     */     } 
/* 725 */     typeSer.writeTypeSuffix(g, typeIdDef);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final WritableTypeId _typeIdDef(TypeSerializer typeSer, Object bean, JsonToken valueShape) {
/* 733 */     if (this._typeId == null) {
/* 734 */       return typeSer.typeId(bean, valueShape);
/*     */     }
/* 736 */     Object typeId = this._typeId.getValue(bean);
/* 737 */     if (typeId == null)
/*     */     {
/* 739 */       typeId = "";
/*     */     }
/* 741 */     return typeSer.typeId(bean, valueShape, typeId);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected final String _customTypeId(Object bean) {
/* 747 */     Object typeId = this._typeId.getValue(bean);
/* 748 */     if (typeId == null) {
/* 749 */       return "";
/*     */     }
/* 751 */     return (typeId instanceof String) ? (String)typeId : typeId.toString();
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
/*     */   protected void serializeFields(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*     */     BeanPropertyWriter[] props;
/* 764 */     if (this._filteredProps != null && provider.getActiveView() != null) {
/* 765 */       props = this._filteredProps;
/*     */     } else {
/* 767 */       props = this._props;
/*     */     } 
/* 769 */     int i = 0;
/*     */     try {
/* 771 */       for (int len = props.length; i < len; i++) {
/* 772 */         BeanPropertyWriter prop = props[i];
/* 773 */         if (prop != null) {
/* 774 */           prop.serializeAsField(bean, gen, provider);
/*     */         }
/*     */       } 
/* 777 */       if (this._anyGetterWriter != null) {
/* 778 */         this._anyGetterWriter.getAndSerialize(bean, gen, provider);
/*     */       }
/* 780 */     } catch (Exception e) {
/* 781 */       String name = (i == props.length) ? "[anySetter]" : props[i].getName();
/* 782 */       wrapAndThrow(provider, e, bean, name);
/* 783 */     } catch (StackOverflowError e) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 789 */       JsonMappingException jsonMappingException = new JsonMappingException((Closeable)gen, "Infinite recursion (StackOverflowError)", e);
/*     */       
/* 791 */       String name = (i == props.length) ? "[anySetter]" : props[i].getName();
/* 792 */       jsonMappingException.prependPath(bean, name);
/* 793 */       throw jsonMappingException;
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected void serializeFieldsFiltered(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*     */     BeanPropertyWriter[] props;
/* 809 */     if (this._filteredProps != null && provider.getActiveView() != null) {
/* 810 */       props = this._filteredProps;
/*     */     } else {
/* 812 */       props = this._props;
/*     */     } 
/* 814 */     PropertyFilter filter = findPropertyFilter(provider, this._propertyFilterId, bean);
/*     */     
/* 816 */     if (filter == null) {
/* 817 */       serializeFields(bean, gen, provider);
/*     */       return;
/*     */     } 
/* 820 */     int i = 0;
/*     */     try {
/* 822 */       for (int len = props.length; i < len; i++) {
/* 823 */         BeanPropertyWriter prop = props[i];
/* 824 */         if (prop != null) {
/* 825 */           filter.serializeAsField(bean, gen, provider, (PropertyWriter)prop);
/*     */         }
/*     */       } 
/* 828 */       if (this._anyGetterWriter != null) {
/* 829 */         this._anyGetterWriter.getAndFilter(bean, gen, provider, filter);
/*     */       }
/* 831 */     } catch (Exception e) {
/* 832 */       String name = (i == props.length) ? "[anySetter]" : props[i].getName();
/* 833 */       wrapAndThrow(provider, e, bean, name);
/* 834 */     } catch (StackOverflowError e) {
/*     */ 
/*     */       
/* 837 */       JsonMappingException jsonMappingException = new JsonMappingException((Closeable)gen, "Infinite recursion (StackOverflowError)", e);
/* 838 */       String name = (i == props.length) ? "[anySetter]" : props[i].getName();
/* 839 */       jsonMappingException.prependPath(bean, name);
/* 840 */       throw jsonMappingException;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
/*     */     PropertyFilter filter;
/* 849 */     ObjectNode o = createSchemaNode("object", true);
/*     */ 
/*     */     
/* 852 */     JsonSerializableSchema ann = (JsonSerializableSchema)this._handledType.getAnnotation(JsonSerializableSchema.class);
/* 853 */     if (ann != null) {
/* 854 */       String id = ann.id();
/* 855 */       if (id != null && !id.isEmpty()) {
/* 856 */         o.put("id", id);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 862 */     ObjectNode propertiesNode = o.objectNode();
/*     */     
/* 864 */     if (this._propertyFilterId != null) {
/* 865 */       filter = findPropertyFilter(provider, this._propertyFilterId, null);
/*     */     } else {
/* 867 */       filter = null;
/*     */     } 
/*     */     
/* 870 */     for (int i = 0; i < this._props.length; i++) {
/* 871 */       BeanPropertyWriter prop = this._props[i];
/* 872 */       if (filter == null) {
/* 873 */         prop.depositSchemaProperty(propertiesNode, provider);
/*     */       } else {
/* 875 */         filter.depositSchemaProperty((PropertyWriter)prop, propertiesNode, provider);
/*     */       } 
/*     */     } 
/*     */     
/* 879 */     o.set("properties", (JsonNode)propertiesNode);
/* 880 */     return (JsonNode)o;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 888 */     if (visitor == null) {
/*     */       return;
/*     */     }
/* 891 */     JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
/* 892 */     if (objectVisitor == null) {
/*     */       return;
/*     */     }
/* 895 */     SerializerProvider provider = visitor.getProvider();
/* 896 */     if (this._propertyFilterId != null) {
/* 897 */       PropertyFilter filter = findPropertyFilter(visitor.getProvider(), this._propertyFilterId, null);
/*     */       
/* 899 */       for (int i = 0, end = this._props.length; i < end; i++) {
/* 900 */         filter.depositSchemaProperty((PropertyWriter)this._props[i], objectVisitor, provider);
/*     */       }
/*     */     } else {
/*     */       BeanPropertyWriter[] props;
/* 904 */       Class<?> view = (this._filteredProps == null || provider == null) ? null : provider.getActiveView();
/*     */       
/* 906 */       if (view != null) {
/* 907 */         props = this._filteredProps;
/*     */       } else {
/* 909 */         props = this._props;
/*     */       } 
/*     */       
/* 912 */       for (int i = 0, end = props.length; i < end; i++) {
/* 913 */         BeanPropertyWriter prop = props[i];
/* 914 */         if (prop != null)
/* 915 */           prop.depositSchemaProperty(objectVisitor, provider); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public abstract BeanSerializerBase withObjectIdWriter(ObjectIdWriter paramObjectIdWriter);
/*     */   
/*     */   protected abstract BeanSerializerBase withByNameInclusion(Set<String> paramSet1, Set<String> paramSet2);
/*     */   
/*     */   protected abstract BeanSerializerBase asArraySerializer();
/*     */   
/*     */   public abstract BeanSerializerBase withFilterId(Object paramObject);
/*     */   
/*     */   protected abstract BeanSerializerBase withProperties(BeanPropertyWriter[] paramArrayOfBeanPropertyWriter1, BeanPropertyWriter[] paramArrayOfBeanPropertyWriter2);
/*     */   
/*     */   public abstract void serialize(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider) throws IOException;
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/BeanSerializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */