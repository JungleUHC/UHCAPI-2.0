/*     */ package com.fasterxml.jackson.databind.ser;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonIncludeProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*     */ import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*     */ import com.fasterxml.jackson.annotation.ObjectIdGenerators;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.TokenStreamFactory;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DatabindContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import com.fasterxml.jackson.databind.ObjectReader;
/*     */ import com.fasterxml.jackson.databind.ObjectWriter;
/*     */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.impl.FilteredBeanPropertyWriter;
/*     */ import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
/*     */ import com.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
/*     */ import com.fasterxml.jackson.databind.ser.impl.UnsupportedTypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.std.MapSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.std.ToEmptyObjectSerializer;
/*     */ import com.fasterxml.jackson.databind.type.ReferenceType;
/*     */ import com.fasterxml.jackson.databind.util.BeanUtil;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.Converter;
/*     */ import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class BeanSerializerFactory
/*     */   extends BasicSerializerFactory
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  72 */   public static final BeanSerializerFactory instance = new BeanSerializerFactory(null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanSerializerFactory(SerializerFactoryConfig config) {
/*  85 */     super(config);
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
/*     */   public SerializerFactory withConfig(SerializerFactoryConfig config) {
/*  97 */     if (this._factoryConfig == config) {
/*  98 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 106 */     if (getClass() != BeanSerializerFactory.class) {
/* 107 */       throw new IllegalStateException("Subtype of BeanSerializerFactory (" + getClass().getName() + ") has not properly overridden method 'withAdditionalSerializers': cannot instantiate subtype with additional serializer definitions");
/*     */     }
/*     */ 
/*     */     
/* 111 */     return new BeanSerializerFactory(config);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Iterable<Serializers> customSerializers() {
/* 116 */     return this._factoryConfig.serializers();
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
/*     */   public JsonSerializer<Object> createSerializer(SerializerProvider prov, JavaType origType) throws JsonMappingException {
/*     */     boolean staticTyping;
/*     */     JavaType type;
/* 142 */     SerializationConfig config = prov.getConfig();
/* 143 */     BeanDescription beanDesc = config.introspect(origType);
/* 144 */     JsonSerializer<?> ser = findSerializerFromAnnotation(prov, (Annotated)beanDesc.getClassInfo());
/* 145 */     if (ser != null) {
/* 146 */       return (JsonSerializer)ser;
/*     */     }
/*     */ 
/*     */     
/* 150 */     AnnotationIntrospector intr = config.getAnnotationIntrospector();
/*     */ 
/*     */     
/* 153 */     if (intr == null) {
/* 154 */       type = origType;
/*     */     } else {
/*     */       try {
/* 157 */         type = intr.refineSerializationType((MapperConfig)config, (Annotated)beanDesc.getClassInfo(), origType);
/* 158 */       } catch (JsonMappingException e) {
/* 159 */         return (JsonSerializer<Object>)prov.reportBadTypeDefinition(beanDesc, e.getMessage(), new Object[0]);
/*     */       } 
/*     */     } 
/* 162 */     if (type == origType) {
/* 163 */       staticTyping = false;
/*     */     } else {
/* 165 */       staticTyping = true;
/* 166 */       if (!type.hasRawClass(origType.getRawClass())) {
/* 167 */         beanDesc = config.introspect(type);
/*     */       }
/*     */     } 
/*     */     
/* 171 */     Converter<Object, Object> conv = beanDesc.findSerializationConverter();
/* 172 */     if (conv == null) {
/* 173 */       return (JsonSerializer)_createSerializer2(prov, type, beanDesc, staticTyping);
/*     */     }
/* 175 */     JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
/*     */ 
/*     */     
/* 178 */     if (!delegateType.hasRawClass(type.getRawClass())) {
/* 179 */       beanDesc = config.introspect(delegateType);
/*     */       
/* 181 */       ser = findSerializerFromAnnotation(prov, (Annotated)beanDesc.getClassInfo());
/*     */     } 
/*     */     
/* 184 */     if (ser == null && !delegateType.isJavaLangObject()) {
/* 185 */       ser = _createSerializer2(prov, delegateType, beanDesc, true);
/*     */     }
/* 187 */     return (JsonSerializer<Object>)new StdDelegatingSerializer(conv, delegateType, ser);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonSerializer<?> _createSerializer2(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
/* 194 */     JsonSerializer<?> ser = null;
/* 195 */     SerializationConfig config = prov.getConfig();
/*     */ 
/*     */ 
/*     */     
/* 199 */     if (type.isContainerType()) {
/* 200 */       if (!staticTyping) {
/* 201 */         staticTyping = usesStaticTyping(config, beanDesc, null);
/*     */       }
/*     */       
/* 204 */       ser = buildContainerSerializer(prov, type, beanDesc, staticTyping);
/*     */       
/* 206 */       if (ser != null) {
/* 207 */         return ser;
/*     */       }
/*     */     } else {
/* 210 */       if (type.isReferenceType()) {
/* 211 */         ser = findReferenceSerializer(prov, (ReferenceType)type, beanDesc, staticTyping);
/*     */       } else {
/*     */         
/* 214 */         for (Serializers serializers : customSerializers()) {
/* 215 */           ser = serializers.findSerializer(config, type, beanDesc);
/* 216 */           if (ser != null) {
/*     */             break;
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 223 */       if (ser == null) {
/* 224 */         ser = findSerializerByAnnotations(prov, type, beanDesc);
/*     */       }
/*     */     } 
/*     */     
/* 228 */     if (ser == null) {
/*     */ 
/*     */ 
/*     */       
/* 232 */       ser = findSerializerByLookup(type, config, beanDesc, staticTyping);
/* 233 */       if (ser == null) {
/* 234 */         ser = findSerializerByPrimaryType(prov, type, beanDesc, staticTyping);
/* 235 */         if (ser == null) {
/*     */ 
/*     */ 
/*     */           
/* 239 */           ser = findBeanOrAddOnSerializer(prov, type, beanDesc, staticTyping);
/*     */ 
/*     */ 
/*     */           
/* 243 */           if (ser == null) {
/* 244 */             ser = prov.getUnknownTypeSerializer(beanDesc.getBeanClass());
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 249 */     if (ser != null)
/*     */     {
/* 251 */       if (this._factoryConfig.hasSerializerModifiers()) {
/* 252 */         for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 253 */           ser = mod.modifySerializer(config, beanDesc, ser);
/*     */         }
/*     */       }
/*     */     }
/* 257 */     return ser;
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
/*     */   public JsonSerializer<Object> findBeanSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 272 */     return findBeanOrAddOnSerializer(prov, type, beanDesc, prov.isEnabled(MapperFeature.USE_STATIC_TYPING));
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
/*     */   public JsonSerializer<Object> findBeanOrAddOnSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
/* 287 */     if (!isPotentialBeanType(type.getRawClass()))
/*     */     {
/*     */       
/* 290 */       if (!ClassUtil.isEnumType(type.getRawClass())) {
/* 291 */         return null;
/*     */       }
/*     */     }
/* 294 */     return constructBeanOrAddOnSerializer(prov, type, beanDesc, staticTyping);
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
/*     */   public TypeSerializer findPropertyTypeSerializer(JavaType baseType, SerializationConfig config, AnnotatedMember accessor) throws JsonMappingException {
/*     */     TypeSerializer typeSer;
/* 311 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 312 */     TypeResolverBuilder<?> b = ai.findPropertyTypeResolver((MapperConfig)config, accessor, baseType);
/*     */ 
/*     */ 
/*     */     
/* 316 */     if (b == null) {
/* 317 */       typeSer = createTypeSerializer(config, baseType);
/*     */     } else {
/* 319 */       Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass((MapperConfig)config, accessor, baseType);
/*     */       
/* 321 */       typeSer = b.buildTypeSerializer(config, baseType, subtypes);
/*     */     } 
/* 323 */     return typeSer;
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
/*     */   public TypeSerializer findPropertyContentTypeSerializer(JavaType containerType, SerializationConfig config, AnnotatedMember accessor) throws JsonMappingException {
/*     */     TypeSerializer typeSer;
/* 340 */     JavaType contentType = containerType.getContentType();
/* 341 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 342 */     TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver((MapperConfig)config, accessor, containerType);
/*     */ 
/*     */ 
/*     */     
/* 346 */     if (b == null) {
/* 347 */       typeSer = createTypeSerializer(config, contentType);
/*     */     } else {
/* 349 */       Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass((MapperConfig)config, accessor, contentType);
/*     */       
/* 351 */       typeSer = b.buildTypeSerializer(config, contentType, subtypes);
/*     */     } 
/* 353 */     return typeSer;
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
/*     */   @Deprecated
/*     */   protected JsonSerializer<Object> constructBeanSerializer(SerializerProvider prov, BeanDescription beanDesc) throws JsonMappingException {
/* 367 */     return constructBeanOrAddOnSerializer(prov, beanDesc.getType(), beanDesc, prov.isEnabled(MapperFeature.USE_STATIC_TYPING));
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
/*     */   protected JsonSerializer<Object> constructBeanOrAddOnSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
/* 383 */     if (beanDesc.getBeanClass() == Object.class) {
/* 384 */       return prov.getUnknownTypeSerializer(Object.class);
/*     */     }
/*     */     
/* 387 */     JsonSerializer<?> ser = _findUnsupportedTypeSerializer(prov, type, beanDesc);
/* 388 */     if (ser != null) {
/* 389 */       return (JsonSerializer)ser;
/*     */     }
/*     */ 
/*     */     
/* 393 */     if (_isUnserializableJacksonType(prov, type)) {
/* 394 */       return (JsonSerializer<Object>)new ToEmptyObjectSerializer(type);
/*     */     }
/* 396 */     SerializationConfig config = prov.getConfig();
/* 397 */     BeanSerializerBuilder builder = constructBeanSerializerBuilder(beanDesc);
/* 398 */     builder.setConfig(config);
/*     */ 
/*     */     
/* 401 */     List<BeanPropertyWriter> props = findBeanProperties(prov, beanDesc, builder);
/* 402 */     if (props == null) {
/* 403 */       props = new ArrayList<>();
/*     */     } else {
/* 405 */       props = removeOverlappingTypeIds(prov, beanDesc, builder, props);
/*     */     } 
/*     */ 
/*     */     
/* 409 */     prov.getAnnotationIntrospector().findAndAddVirtualProperties((MapperConfig)config, beanDesc.getClassInfo(), props);
/*     */ 
/*     */     
/* 412 */     if (this._factoryConfig.hasSerializerModifiers()) {
/* 413 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 414 */         props = mod.changeProperties(config, beanDesc, props);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 422 */     props = filterUnwantedJDKProperties(config, beanDesc, props);
/* 423 */     props = filterBeanProperties(config, beanDesc, props);
/*     */ 
/*     */     
/* 426 */     if (this._factoryConfig.hasSerializerModifiers()) {
/* 427 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 428 */         props = mod.orderProperties(config, beanDesc, props);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 435 */     builder.setObjectIdWriter(constructObjectIdHandler(prov, beanDesc, props));
/*     */     
/* 437 */     builder.setProperties(props);
/* 438 */     builder.setFilterId(findFilterId(config, beanDesc));
/*     */     
/* 440 */     AnnotatedMember anyGetter = beanDesc.findAnyGetter();
/* 441 */     if (anyGetter != null) {
/* 442 */       MapSerializer mapSerializer; JavaType anyType = anyGetter.getType();
/*     */       
/* 444 */       JavaType valueType = anyType.getContentType();
/* 445 */       TypeSerializer typeSer = createTypeSerializer(config, valueType);
/*     */ 
/*     */       
/* 448 */       JsonSerializer<?> anySer = findSerializerFromAnnotation(prov, (Annotated)anyGetter);
/* 449 */       if (anySer == null)
/*     */       {
/* 451 */         mapSerializer = MapSerializer.construct((Set)null, anyType, config
/* 452 */             .isEnabled(MapperFeature.USE_STATIC_TYPING), typeSer, null, null, null);
/*     */       }
/*     */ 
/*     */       
/* 456 */       PropertyName name = PropertyName.construct(anyGetter.getName());
/* 457 */       BeanProperty.Std anyProp = new BeanProperty.Std(name, valueType, null, anyGetter, PropertyMetadata.STD_OPTIONAL);
/*     */       
/* 459 */       builder.setAnyGetter(new AnyGetterWriter((BeanProperty)anyProp, anyGetter, (JsonSerializer<?>)mapSerializer));
/*     */     } 
/*     */     
/* 462 */     processViews(config, builder);
/*     */ 
/*     */     
/* 465 */     if (this._factoryConfig.hasSerializerModifiers()) {
/* 466 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 467 */         builder = mod.updateBuilder(config, beanDesc, builder);
/*     */       }
/*     */     }
/*     */     
/*     */     try {
/* 472 */       ser = builder.build();
/* 473 */     } catch (RuntimeException e) {
/* 474 */       return (JsonSerializer<Object>)prov.reportBadTypeDefinition(beanDesc, "Failed to construct BeanSerializer for %s: (%s) %s", new Object[] { beanDesc
/* 475 */             .getType(), e.getClass().getName(), e.getMessage() });
/*     */     } 
/* 477 */     if (ser == null) {
/*     */       
/* 479 */       if (type.isRecordType()) {
/* 480 */         return (JsonSerializer<Object>)builder.createDummy();
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 485 */       ser = findSerializerByAddonType(config, type, beanDesc, staticTyping);
/* 486 */       if (ser == null)
/*     */       {
/*     */ 
/*     */         
/* 490 */         if (beanDesc.hasKnownClassAnnotations()) {
/* 491 */           return (JsonSerializer<Object>)builder.createDummy();
/*     */         }
/*     */       }
/*     */     } 
/* 495 */     return (JsonSerializer)ser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ObjectIdWriter constructObjectIdHandler(SerializerProvider prov, BeanDescription beanDesc, List<BeanPropertyWriter> props) throws JsonMappingException {
/* 502 */     ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
/* 503 */     if (objectIdInfo == null) {
/* 504 */       return null;
/*     */     }
/*     */     
/* 507 */     Class<?> implClass = objectIdInfo.getGeneratorType();
/*     */ 
/*     */     
/* 510 */     if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
/* 511 */       String propName = objectIdInfo.getPropertyName().getSimpleName();
/* 512 */       BeanPropertyWriter idProp = null;
/*     */       
/* 514 */       for (int i = 0, len = props.size();; i++) {
/* 515 */         if (i == len)
/* 516 */           throw new IllegalArgumentException(String.format("Invalid Object Id definition for %s: cannot find property with name %s", new Object[] {
/*     */                   
/* 518 */                   ClassUtil.getTypeDescription(beanDesc.getType()), ClassUtil.name(propName)
/*     */                 })); 
/* 520 */         BeanPropertyWriter prop = props.get(i);
/* 521 */         if (propName.equals(prop.getName())) {
/* 522 */           idProp = prop;
/*     */ 
/*     */           
/* 525 */           if (i > 0) {
/* 526 */             props.remove(i);
/* 527 */             props.add(0, idProp);
/*     */           } 
/*     */           break;
/*     */         } 
/*     */       } 
/* 532 */       JavaType javaType = idProp.getType();
/* 533 */       PropertyBasedObjectIdGenerator propertyBasedObjectIdGenerator = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
/*     */       
/* 535 */       return ObjectIdWriter.construct(javaType, (PropertyName)null, (ObjectIdGenerator)propertyBasedObjectIdGenerator, objectIdInfo.getAlwaysAsId());
/*     */     } 
/*     */ 
/*     */     
/* 539 */     JavaType type = prov.constructType(implClass);
/*     */     
/* 541 */     JavaType idType = prov.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/* 542 */     ObjectIdGenerator<?> gen = prov.objectIdGeneratorInstance((Annotated)beanDesc.getClassInfo(), objectIdInfo);
/* 543 */     return ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen, objectIdInfo
/* 544 */         .getAlwaysAsId());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanPropertyWriter constructFilteredBeanWriter(BeanPropertyWriter writer, Class<?>[] inViews) {
/* 555 */     return FilteredBeanPropertyWriter.constructViewBased(writer, inViews);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected PropertyBuilder constructPropertyBuilder(SerializationConfig config, BeanDescription beanDesc) {
/* 561 */     return new PropertyBuilder(config, beanDesc);
/*     */   }
/*     */   
/*     */   protected BeanSerializerBuilder constructBeanSerializerBuilder(BeanDescription beanDesc) {
/* 565 */     return new BeanSerializerBuilder(beanDesc);
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
/*     */   protected boolean isPotentialBeanType(Class<?> type) {
/* 584 */     return (ClassUtil.canBeABeanType(type) == null && !ClassUtil.isProxyType(type));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<BeanPropertyWriter> findBeanProperties(SerializerProvider prov, BeanDescription beanDesc, BeanSerializerBuilder builder) throws JsonMappingException {
/* 595 */     List<BeanPropertyDefinition> properties = beanDesc.findProperties();
/* 596 */     SerializationConfig config = prov.getConfig();
/*     */ 
/*     */     
/* 599 */     removeIgnorableTypes(config, beanDesc, properties);
/*     */ 
/*     */     
/* 602 */     if (config.isEnabled(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)) {
/* 603 */       removeSetterlessGetters(config, beanDesc, properties);
/*     */     }
/*     */ 
/*     */     
/* 607 */     if (properties.isEmpty()) {
/* 608 */       return null;
/*     */     }
/*     */     
/* 611 */     boolean staticTyping = usesStaticTyping(config, beanDesc, null);
/* 612 */     PropertyBuilder pb = constructPropertyBuilder(config, beanDesc);
/*     */     
/* 614 */     ArrayList<BeanPropertyWriter> result = new ArrayList<>(properties.size());
/* 615 */     for (BeanPropertyDefinition property : properties) {
/* 616 */       AnnotatedMember accessor = property.getAccessor();
/*     */       
/* 618 */       if (property.isTypeId()) {
/* 619 */         if (accessor != null) {
/* 620 */           builder.setTypeId(accessor);
/*     */         }
/*     */         
/*     */         continue;
/*     */       } 
/* 625 */       AnnotationIntrospector.ReferenceProperty refType = property.findReferenceType();
/* 626 */       if (refType != null && refType.isBackReference()) {
/*     */         continue;
/*     */       }
/* 629 */       if (accessor instanceof com.fasterxml.jackson.databind.introspect.AnnotatedMethod) {
/* 630 */         result.add(_constructWriter(prov, property, pb, staticTyping, accessor)); continue;
/*     */       } 
/* 632 */       result.add(_constructWriter(prov, property, pb, staticTyping, accessor));
/*     */     } 
/*     */     
/* 635 */     return result;
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
/*     */   protected List<BeanPropertyWriter> filterBeanProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> props) {
/* 655 */     JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc
/* 656 */         .getClassInfo());
/* 657 */     Set<String> ignored = null;
/* 658 */     if (ignorals != null) {
/* 659 */       ignored = ignorals.findIgnoredForSerialization();
/*     */     }
/* 661 */     JsonIncludeProperties.Value inclusions = config.getDefaultPropertyInclusions(beanDesc.getBeanClass(), beanDesc
/* 662 */         .getClassInfo());
/* 663 */     Set<String> included = null;
/* 664 */     if (inclusions != null) {
/* 665 */       included = inclusions.getIncluded();
/*     */     }
/* 667 */     if (included != null || (ignored != null && !ignored.isEmpty())) {
/* 668 */       Iterator<BeanPropertyWriter> it = props.iterator();
/* 669 */       while (it.hasNext()) {
/* 670 */         if (IgnorePropertiesUtil.shouldIgnore(((BeanPropertyWriter)it.next()).getName(), ignored, included)) {
/* 671 */           it.remove();
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 676 */     return props;
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
/*     */   protected List<BeanPropertyWriter> filterUnwantedJDKProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> props) {
/* 692 */     if (beanDesc.getType().isTypeOrSubTypeOf(CharSequence.class))
/*     */     {
/* 694 */       if (props.size() == 1) {
/* 695 */         BeanPropertyWriter prop = props.get(0);
/*     */ 
/*     */ 
/*     */         
/* 699 */         AnnotatedMember m = prop.getMember();
/* 700 */         if (m instanceof com.fasterxml.jackson.databind.introspect.AnnotatedMethod && "isEmpty"
/* 701 */           .equals(m.getName()) && m
/* 702 */           .getDeclaringClass() == CharSequence.class) {
/* 703 */           props.remove(0);
/*     */         }
/*     */       } 
/*     */     }
/* 707 */     return props;
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
/*     */   protected void processViews(SerializationConfig config, BeanSerializerBuilder builder) {
/* 722 */     List<BeanPropertyWriter> props = builder.getProperties();
/* 723 */     boolean includeByDefault = config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
/* 724 */     int propCount = props.size();
/* 725 */     int viewsFound = 0;
/* 726 */     BeanPropertyWriter[] filtered = new BeanPropertyWriter[propCount];
/*     */     
/* 728 */     for (int i = 0; i < propCount; i++) {
/* 729 */       BeanPropertyWriter bpw = props.get(i);
/* 730 */       Class<?>[] views = bpw.getViews();
/* 731 */       if (views == null || views.length == 0) {
/*     */ 
/*     */         
/* 734 */         if (includeByDefault) {
/* 735 */           filtered[i] = bpw;
/*     */         }
/*     */       } else {
/* 738 */         viewsFound++;
/* 739 */         filtered[i] = constructFilteredBeanWriter(bpw, views);
/*     */       } 
/*     */     } 
/*     */     
/* 743 */     if (includeByDefault && viewsFound == 0) {
/*     */       return;
/*     */     }
/* 746 */     builder.setFilteredProperties(filtered);
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
/*     */   protected void removeIgnorableTypes(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> properties) {
/* 758 */     AnnotationIntrospector intr = config.getAnnotationIntrospector();
/* 759 */     HashMap<Class<?>, Boolean> ignores = new HashMap<>();
/* 760 */     Iterator<BeanPropertyDefinition> it = properties.iterator();
/* 761 */     while (it.hasNext()) {
/* 762 */       BeanPropertyDefinition property = it.next();
/* 763 */       AnnotatedMember accessor = property.getAccessor();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 768 */       if (accessor == null) {
/* 769 */         it.remove();
/*     */         continue;
/*     */       } 
/* 772 */       Class<?> type = property.getRawPrimaryType();
/* 773 */       Boolean result = ignores.get(type);
/* 774 */       if (result == null) {
/*     */         
/* 776 */         result = config.getConfigOverride(type).getIsIgnoredType();
/* 777 */         if (result == null) {
/* 778 */           BeanDescription desc = config.introspectClassAnnotations(type);
/* 779 */           AnnotatedClass ac = desc.getClassInfo();
/* 780 */           result = intr.isIgnorableType(ac);
/*     */           
/* 782 */           if (result == null) {
/* 783 */             result = Boolean.FALSE;
/*     */           }
/*     */         } 
/* 786 */         ignores.put(type, result);
/*     */       } 
/*     */       
/* 789 */       if (result.booleanValue()) {
/* 790 */         it.remove();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void removeSetterlessGetters(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> properties) {
/* 801 */     Iterator<BeanPropertyDefinition> it = properties.iterator();
/* 802 */     while (it.hasNext()) {
/* 803 */       BeanPropertyDefinition property = it.next();
/*     */ 
/*     */       
/* 806 */       if (!property.couldDeserialize() && !property.isExplicitlyIncluded()) {
/* 807 */         it.remove();
/*     */       }
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
/*     */   protected List<BeanPropertyWriter> removeOverlappingTypeIds(SerializerProvider prov, BeanDescription beanDesc, BeanSerializerBuilder builder, List<BeanPropertyWriter> props) {
/* 822 */     for (int i = 0, end = props.size(); i < end; i++) {
/* 823 */       BeanPropertyWriter bpw = props.get(i);
/* 824 */       TypeSerializer td = bpw.getTypeSerializer();
/* 825 */       if (td != null && td.getTypeInclusion() == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
/*     */ 
/*     */         
/* 828 */         String n = td.getPropertyName();
/* 829 */         PropertyName typePropName = PropertyName.construct(n);
/*     */         
/* 831 */         for (BeanPropertyWriter w2 : props) {
/* 832 */           if (w2 != bpw && w2.wouldConflictWithName(typePropName)) {
/* 833 */             bpw.assignTypeSerializer(null); break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 838 */     return props;
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
/*     */   protected BeanPropertyWriter _constructWriter(SerializerProvider prov, BeanPropertyDefinition propDef, PropertyBuilder pb, boolean staticTyping, AnnotatedMember accessor) throws JsonMappingException {
/* 856 */     PropertyName name = propDef.getFullName();
/* 857 */     JavaType type = accessor.getType();
/*     */     
/* 859 */     BeanProperty.Std property = new BeanProperty.Std(name, type, propDef.getWrapperName(), accessor, propDef.getMetadata());
/*     */ 
/*     */     
/* 862 */     JsonSerializer<?> annotatedSerializer = findSerializerFromAnnotation(prov, (Annotated)accessor);
/*     */ 
/*     */ 
/*     */     
/* 866 */     if (annotatedSerializer instanceof ResolvableSerializer) {
/* 867 */       ((ResolvableSerializer)annotatedSerializer).resolve(prov);
/*     */     }
/*     */     
/* 870 */     annotatedSerializer = prov.handlePrimaryContextualization(annotatedSerializer, (BeanProperty)property);
/*     */     
/* 872 */     TypeSerializer contentTypeSer = null;
/*     */     
/* 874 */     if (type.isContainerType() || type.isReferenceType()) {
/* 875 */       contentTypeSer = findPropertyContentTypeSerializer(type, prov.getConfig(), accessor);
/*     */     }
/*     */     
/* 878 */     TypeSerializer typeSer = findPropertyTypeSerializer(type, prov.getConfig(), accessor);
/* 879 */     return pb.buildWriter(prov, propDef, type, annotatedSerializer, typeSer, contentTypeSer, accessor, staticTyping);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonSerializer<?> _findUnsupportedTypeSerializer(SerializerProvider ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 889 */     String errorMsg = BeanUtil.checkUnsupportedType(type);
/* 890 */     if (errorMsg != null)
/*     */     {
/*     */       
/* 893 */       if (ctxt.getConfig().findMixInClassFor(type.getRawClass()) == null) {
/* 894 */         return (JsonSerializer<?>)new UnsupportedTypeSerializer(type, errorMsg);
/*     */       }
/*     */     }
/* 897 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _isUnserializableJacksonType(SerializerProvider ctxt, JavaType type) {
/* 908 */     Class<?> raw = type.getRawClass();
/* 909 */     return (ObjectMapper.class.isAssignableFrom(raw) || ObjectReader.class
/* 910 */       .isAssignableFrom(raw) || ObjectWriter.class
/* 911 */       .isAssignableFrom(raw) || DatabindContext.class
/* 912 */       .isAssignableFrom(raw) || TokenStreamFactory.class
/* 913 */       .isAssignableFrom(raw) || JsonParser.class
/* 914 */       .isAssignableFrom(raw) || JsonGenerator.class
/* 915 */       .isAssignableFrom(raw));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/BeanSerializerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */