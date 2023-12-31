/*      */ package com.fasterxml.jackson.databind.ser;
/*      */ import com.fasterxml.jackson.annotation.JsonFormat;
/*      */ import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*      */ import com.fasterxml.jackson.annotation.JsonInclude;
/*      */ import com.fasterxml.jackson.annotation.JsonIncludeProperties;
/*      */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*      */ import com.fasterxml.jackson.databind.BeanDescription;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.JsonMappingException;
/*      */ import com.fasterxml.jackson.databind.JsonSerializer;
/*      */ import com.fasterxml.jackson.databind.MapperFeature;
/*      */ import com.fasterxml.jackson.databind.SerializationConfig;
/*      */ import com.fasterxml.jackson.databind.SerializationFeature;
/*      */ import com.fasterxml.jackson.databind.SerializerProvider;
/*      */ import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
/*      */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*      */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.impl.IndexedStringListSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
/*      */ import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
/*      */ import com.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.AtomicReferenceSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.DateSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.EnumSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.MapSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
/*      */ import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
/*      */ import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
/*      */ import com.fasterxml.jackson.databind.type.ArrayType;
/*      */ import com.fasterxml.jackson.databind.type.CollectionLikeType;
/*      */ import com.fasterxml.jackson.databind.type.CollectionType;
/*      */ import com.fasterxml.jackson.databind.type.MapLikeType;
/*      */ import com.fasterxml.jackson.databind.type.MapType;
/*      */ import com.fasterxml.jackson.databind.type.ReferenceType;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*      */ import com.fasterxml.jackson.databind.util.BeanUtil;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import com.fasterxml.jackson.databind.util.Converter;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ 
/*      */ public abstract class BasicSerializerFactory extends SerializerFactory implements Serializable {
/*      */   static {
/*   63 */     HashMap<String, Class<? extends JsonSerializer<?>>> concLazy = new HashMap<>();
/*      */     
/*   65 */     HashMap<String, JsonSerializer<?>> concrete = new HashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   72 */     concrete.put(String.class.getName(), new StringSerializer());
/*   73 */     ToStringSerializer sls = ToStringSerializer.instance;
/*   74 */     concrete.put(StringBuffer.class.getName(), sls);
/*   75 */     concrete.put(StringBuilder.class.getName(), sls);
/*   76 */     concrete.put(Character.class.getName(), sls);
/*   77 */     concrete.put(char.class.getName(), sls);
/*      */ 
/*      */     
/*   80 */     NumberSerializers.addAll(concrete);
/*   81 */     concrete.put(boolean.class.getName(), new BooleanSerializer(true));
/*   82 */     concrete.put(Boolean.class.getName(), new BooleanSerializer(false));
/*      */ 
/*      */     
/*   85 */     concrete.put(BigInteger.class.getName(), new NumberSerializer(BigInteger.class));
/*   86 */     concrete.put(BigDecimal.class.getName(), new NumberSerializer(BigDecimal.class));
/*      */ 
/*      */ 
/*      */     
/*   90 */     concrete.put(Calendar.class.getName(), CalendarSerializer.instance);
/*   91 */     concrete.put(Date.class.getName(), DateSerializer.instance);
/*      */ 
/*      */     
/*   94 */     for (Map.Entry<Class<?>, Object> en : (Iterable<Map.Entry<Class<?>, Object>>)StdJdkSerializers.all()) {
/*   95 */       Object value = en.getValue();
/*   96 */       if (value instanceof JsonSerializer) {
/*   97 */         concrete.put(((Class)en.getKey()).getName(), (JsonSerializer)value);
/*      */         continue;
/*      */       } 
/*  100 */       Class<? extends JsonSerializer<?>> cls = (Class<? extends JsonSerializer<?>>)value;
/*  101 */       concLazy.put(((Class)en.getKey()).getName(), cls);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  107 */     concLazy.put(TokenBuffer.class.getName(), TokenBufferSerializer.class);
/*      */     
/*  109 */     _concrete = concrete;
/*  110 */     _concreteLazy = concLazy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final HashMap<String, JsonSerializer<?>> _concrete;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final HashMap<String, Class<? extends JsonSerializer<?>>> _concreteLazy;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final SerializerFactoryConfig _factoryConfig;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BasicSerializerFactory(SerializerFactoryConfig config) {
/*  137 */     this._factoryConfig = (config == null) ? new SerializerFactoryConfig() : config;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SerializerFactoryConfig getFactoryConfig() {
/*  148 */     return this._factoryConfig;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final SerializerFactory withAdditionalSerializers(Serializers additional) {
/*  169 */     return withConfig(this._factoryConfig.withAdditionalSerializers(additional));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final SerializerFactory withAdditionalKeySerializers(Serializers additional) {
/*  178 */     return withConfig(this._factoryConfig.withAdditionalKeySerializers(additional));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final SerializerFactory withSerializerModifier(BeanSerializerModifier modifier) {
/*  187 */     return withConfig(this._factoryConfig.withSerializerModifier(modifier));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonSerializer<Object> createKeySerializer(SerializerProvider ctxt, JavaType keyType, JsonSerializer<Object> defaultImpl) throws JsonMappingException {
/*  209 */     SerializationConfig config = ctxt.getConfig();
/*  210 */     BeanDescription beanDesc = config.introspect(keyType);
/*  211 */     JsonSerializer<?> ser = null;
/*      */     
/*  213 */     if (this._factoryConfig.hasKeySerializers())
/*      */     {
/*  215 */       for (Serializers serializers : this._factoryConfig.keySerializers()) {
/*  216 */         ser = serializers.findSerializer(config, keyType, beanDesc);
/*  217 */         if (ser != null) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     }
/*  222 */     if (ser == null) {
/*      */       
/*  224 */       ser = _findKeySerializer(ctxt, (Annotated)beanDesc.getClassInfo());
/*  225 */       if (ser == null) {
/*      */ 
/*      */ 
/*      */         
/*  229 */         ser = defaultImpl;
/*  230 */         if (ser == null) {
/*      */ 
/*      */           
/*  233 */           ser = StdKeySerializers.getStdKeySerializer(config, keyType.getRawClass(), false);
/*  234 */           if (ser == null) {
/*  235 */             AnnotatedMember acc = beanDesc.findJsonKeyAccessor();
/*  236 */             if (acc == null)
/*      */             {
/*  238 */               acc = beanDesc.findJsonValueAccessor();
/*      */             }
/*  240 */             if (acc != null) {
/*  241 */               JsonSerializer<?> delegate = createKeySerializer(ctxt, acc.getType(), defaultImpl);
/*  242 */               if (config.canOverrideAccessModifiers()) {
/*  243 */                 ClassUtil.checkAndFixAccess(acc.getMember(), config
/*  244 */                     .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*      */               }
/*      */               
/*  247 */               JsonValueSerializer jsonValueSerializer = new JsonValueSerializer(acc, null, delegate);
/*      */             } else {
/*  249 */               ser = StdKeySerializers.getFallbackKeySerializer(config, keyType.getRawClass());
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  257 */     if (this._factoryConfig.hasSerializerModifiers()) {
/*  258 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/*  259 */         ser = mod.modifyKeySerializer(config, keyType, beanDesc, ser);
/*      */       }
/*      */     }
/*  262 */     return (JsonSerializer)ser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonSerializer<Object> createKeySerializer(SerializationConfig config, JavaType keyType, JsonSerializer<Object> defaultImpl) {
/*  271 */     BeanDescription beanDesc = config.introspect(keyType);
/*  272 */     JsonSerializer<?> ser = null;
/*  273 */     if (this._factoryConfig.hasKeySerializers()) {
/*  274 */       for (Serializers serializers : this._factoryConfig.keySerializers()) {
/*  275 */         ser = serializers.findSerializer(config, keyType, beanDesc);
/*  276 */         if (ser != null) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     }
/*  281 */     if (ser == null) {
/*  282 */       ser = defaultImpl;
/*  283 */       if (ser == null) {
/*  284 */         ser = StdKeySerializers.getStdKeySerializer(config, keyType.getRawClass(), false);
/*  285 */         if (ser == null) {
/*  286 */           ser = StdKeySerializers.getFallbackKeySerializer(config, keyType.getRawClass());
/*      */         }
/*      */       } 
/*      */     } 
/*  290 */     if (this._factoryConfig.hasSerializerModifiers()) {
/*  291 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/*  292 */         ser = mod.modifyKeySerializer(config, keyType, beanDesc, ser);
/*      */       }
/*      */     }
/*  295 */     return (JsonSerializer)ser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeSerializer createTypeSerializer(SerializationConfig config, JavaType baseType) {
/*  307 */     BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
/*  308 */     AnnotatedClass ac = bean.getClassInfo();
/*  309 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*  310 */     TypeResolverBuilder<?> b = ai.findTypeResolver((MapperConfig)config, ac, baseType);
/*      */ 
/*      */ 
/*      */     
/*  314 */     Collection<NamedType> subtypes = null;
/*  315 */     if (b == null) {
/*  316 */       b = config.getDefaultTyper(baseType);
/*      */     } else {
/*  318 */       subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass((MapperConfig)config, ac);
/*      */     } 
/*  320 */     if (b == null) {
/*  321 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  325 */     return b.buildTypeSerializer(config, baseType, subtypes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonSerializer<?> findSerializerByLookup(JavaType type, SerializationConfig config, BeanDescription beanDesc, boolean staticTyping) {
/*  350 */     Class<?> raw = type.getRawClass();
/*  351 */     String clsName = raw.getName();
/*  352 */     JsonSerializer<?> ser = _concrete.get(clsName);
/*  353 */     if (ser == null) {
/*  354 */       Class<? extends JsonSerializer<?>> serClass = _concreteLazy.get(clsName);
/*  355 */       if (serClass != null)
/*      */       {
/*      */ 
/*      */         
/*  359 */         return (JsonSerializer)ClassUtil.createInstance(serClass, false);
/*      */       }
/*      */     } 
/*  362 */     return ser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonSerializer<?> findSerializerByAnnotations(SerializerProvider prov, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*  385 */     Class<?> raw = type.getRawClass();
/*      */     
/*  387 */     if (JsonSerializable.class.isAssignableFrom(raw)) {
/*  388 */       return (JsonSerializer<?>)SerializableSerializer.instance;
/*      */     }
/*      */     
/*  391 */     AnnotatedMember valueAccessor = beanDesc.findJsonValueAccessor();
/*  392 */     if (valueAccessor != null) {
/*  393 */       if (prov.canOverrideAccessModifiers()) {
/*  394 */         ClassUtil.checkAndFixAccess(valueAccessor.getMember(), prov
/*  395 */             .isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*      */       }
/*  397 */       JavaType valueType = valueAccessor.getType();
/*  398 */       JsonSerializer<Object> valueSerializer = findSerializerFromAnnotation(prov, (Annotated)valueAccessor);
/*  399 */       if (valueSerializer == null) {
/*  400 */         valueSerializer = (JsonSerializer<Object>)valueType.getValueHandler();
/*      */       }
/*  402 */       TypeSerializer typeSerializer = (TypeSerializer)valueType.getTypeHandler();
/*  403 */       if (typeSerializer == null) {
/*  404 */         typeSerializer = createTypeSerializer(prov.getConfig(), valueType);
/*      */       }
/*  406 */       return (JsonSerializer<?>)new JsonValueSerializer(valueAccessor, typeSerializer, valueSerializer);
/*      */     } 
/*      */     
/*  409 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonSerializer<?> findSerializerByPrimaryType(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
/*  424 */     if (type.isEnumType()) {
/*  425 */       return buildEnumSerializer(prov.getConfig(), type, beanDesc);
/*      */     }
/*      */     
/*  428 */     Class<?> raw = type.getRawClass();
/*      */     
/*  430 */     JsonSerializer<?> ser = findOptionalStdSerializer(prov, type, beanDesc, staticTyping);
/*  431 */     if (ser != null) {
/*  432 */       return ser;
/*      */     }
/*      */     
/*  435 */     if (Calendar.class.isAssignableFrom(raw)) {
/*  436 */       return (JsonSerializer<?>)CalendarSerializer.instance;
/*      */     }
/*  438 */     if (Date.class.isAssignableFrom(raw)) {
/*  439 */       return (JsonSerializer<?>)DateSerializer.instance;
/*      */     }
/*  441 */     if (Map.Entry.class.isAssignableFrom(raw)) {
/*      */       
/*  443 */       JavaType mapEntryType = type.findSuperType(Map.Entry.class);
/*      */ 
/*      */       
/*  446 */       JavaType kt = mapEntryType.containedTypeOrUnknown(0);
/*  447 */       JavaType vt = mapEntryType.containedTypeOrUnknown(1);
/*  448 */       return buildMapEntrySerializer(prov, type, beanDesc, staticTyping, kt, vt);
/*      */     } 
/*  450 */     if (ByteBuffer.class.isAssignableFrom(raw)) {
/*  451 */       return (JsonSerializer<?>)new ByteBufferSerializer();
/*      */     }
/*  453 */     if (InetAddress.class.isAssignableFrom(raw)) {
/*  454 */       return (JsonSerializer<?>)new InetAddressSerializer();
/*      */     }
/*  456 */     if (InetSocketAddress.class.isAssignableFrom(raw)) {
/*  457 */       return (JsonSerializer<?>)new InetSocketAddressSerializer();
/*      */     }
/*  459 */     if (TimeZone.class.isAssignableFrom(raw)) {
/*  460 */       return (JsonSerializer<?>)new TimeZoneSerializer();
/*      */     }
/*  462 */     if (Charset.class.isAssignableFrom(raw)) {
/*  463 */       return (JsonSerializer<?>)ToStringSerializer.instance;
/*      */     }
/*  465 */     if (Number.class.isAssignableFrom(raw)) {
/*      */       
/*  467 */       JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/*  468 */       switch (format.getShape()) {
/*      */         case NON_DEFAULT:
/*  470 */           return (JsonSerializer<?>)ToStringSerializer.instance;
/*      */         case NON_ABSENT:
/*      */         case NON_EMPTY:
/*  473 */           return null;
/*      */       } 
/*      */       
/*  476 */       return (JsonSerializer<?>)NumberSerializer.instance;
/*      */     } 
/*      */     
/*  479 */     if (ClassLoader.class.isAssignableFrom(raw)) {
/*  480 */       return (JsonSerializer<?>)new ToEmptyObjectSerializer(type);
/*      */     }
/*  482 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> findOptionalStdSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
/*  494 */     return OptionalHandlerFactory.instance.findSerializer(prov.getConfig(), type, beanDesc);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonSerializer<?> findSerializerByAddonType(SerializationConfig config, JavaType javaType, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
/*  508 */     Class<?> rawType = javaType.getRawClass();
/*      */     
/*  510 */     if (Iterator.class.isAssignableFrom(rawType)) {
/*  511 */       JavaType[] params = config.getTypeFactory().findTypeParameters(javaType, Iterator.class);
/*      */       
/*  513 */       JavaType vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
/*  514 */       return buildIteratorSerializer(config, javaType, beanDesc, staticTyping, vt);
/*      */     } 
/*  516 */     if (Iterable.class.isAssignableFrom(rawType)) {
/*  517 */       JavaType[] params = config.getTypeFactory().findTypeParameters(javaType, Iterable.class);
/*      */       
/*  519 */       JavaType vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
/*  520 */       return buildIterableSerializer(config, javaType, beanDesc, staticTyping, vt);
/*      */     } 
/*  522 */     if (CharSequence.class.isAssignableFrom(rawType)) {
/*  523 */       return (JsonSerializer<?>)ToStringSerializer.instance;
/*      */     }
/*  525 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<Object> findSerializerFromAnnotation(SerializerProvider prov, Annotated a) throws JsonMappingException {
/*  540 */     Object serDef = prov.getAnnotationIntrospector().findSerializer(a);
/*  541 */     if (serDef == null) {
/*  542 */       return null;
/*      */     }
/*  544 */     JsonSerializer<Object> ser = prov.serializerInstance(a, serDef);
/*      */     
/*  546 */     return (JsonSerializer)findConvertingSerializer(prov, a, ser);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> findConvertingSerializer(SerializerProvider prov, Annotated a, JsonSerializer<?> ser) throws JsonMappingException {
/*  559 */     Converter<Object, Object> conv = findConverter(prov, a);
/*  560 */     if (conv == null) {
/*  561 */       return ser;
/*      */     }
/*  563 */     JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
/*  564 */     return (JsonSerializer<?>)new StdDelegatingSerializer(conv, delegateType, ser);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Converter<Object, Object> findConverter(SerializerProvider prov, Annotated a) throws JsonMappingException {
/*  571 */     Object convDef = prov.getAnnotationIntrospector().findSerializationConverter(a);
/*  572 */     if (convDef == null) {
/*  573 */       return null;
/*      */     }
/*  575 */     return prov.converterInstance(a, convDef);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> buildContainerSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
/*  591 */     SerializationConfig config = prov.getConfig();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  597 */     if (!staticTyping && type.useStaticType() && (
/*  598 */       !type.isContainerType() || !type.getContentType().isJavaLangObject())) {
/*  599 */       staticTyping = true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  604 */     JavaType elementType = type.getContentType();
/*  605 */     TypeSerializer elementTypeSerializer = createTypeSerializer(config, elementType);
/*      */ 
/*      */ 
/*      */     
/*  609 */     if (elementTypeSerializer != null) {
/*  610 */       staticTyping = false;
/*      */     }
/*  612 */     JsonSerializer<Object> elementValueSerializer = _findContentSerializer(prov, (Annotated)beanDesc
/*  613 */         .getClassInfo());
/*  614 */     if (type.isMapLikeType()) {
/*  615 */       MapLikeType mlt = (MapLikeType)type;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  621 */       JsonSerializer<Object> keySerializer = _findKeySerializer(prov, (Annotated)beanDesc.getClassInfo());
/*  622 */       if (mlt instanceof MapType) {
/*  623 */         return buildMapSerializer(prov, (MapType)mlt, beanDesc, staticTyping, keySerializer, elementTypeSerializer, elementValueSerializer);
/*      */       }
/*      */ 
/*      */       
/*  627 */       JsonSerializer<?> ser = null;
/*  628 */       MapLikeType mlType = (MapLikeType)type;
/*  629 */       for (Serializers serializers : customSerializers()) {
/*  630 */         ser = serializers.findMapLikeSerializer(config, mlType, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
/*      */         
/*  632 */         if (ser != null) {
/*      */           break;
/*      */         }
/*      */       } 
/*  636 */       if (ser == null) {
/*  637 */         ser = findSerializerByAnnotations(prov, type, beanDesc);
/*      */       }
/*  639 */       if (ser != null && 
/*  640 */         this._factoryConfig.hasSerializerModifiers()) {
/*  641 */         for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/*  642 */           ser = mod.modifyMapLikeSerializer(config, mlType, beanDesc, ser);
/*      */         }
/*      */       }
/*      */       
/*  646 */       return ser;
/*      */     } 
/*  648 */     if (type.isCollectionLikeType()) {
/*  649 */       CollectionLikeType clt = (CollectionLikeType)type;
/*  650 */       if (clt instanceof CollectionType) {
/*  651 */         return buildCollectionSerializer(prov, (CollectionType)clt, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
/*      */       }
/*      */ 
/*      */       
/*  655 */       JsonSerializer<?> ser = null;
/*  656 */       CollectionLikeType clType = (CollectionLikeType)type;
/*  657 */       for (Serializers serializers : customSerializers()) {
/*  658 */         ser = serializers.findCollectionLikeSerializer(config, clType, beanDesc, elementTypeSerializer, elementValueSerializer);
/*      */         
/*  660 */         if (ser != null) {
/*      */           break;
/*      */         }
/*      */       } 
/*  664 */       if (ser == null) {
/*  665 */         ser = findSerializerByAnnotations(prov, type, beanDesc);
/*      */       }
/*  667 */       if (ser != null && 
/*  668 */         this._factoryConfig.hasSerializerModifiers()) {
/*  669 */         for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/*  670 */           ser = mod.modifyCollectionLikeSerializer(config, clType, beanDesc, ser);
/*      */         }
/*      */       }
/*      */       
/*  674 */       return ser;
/*      */     } 
/*  676 */     if (type.isArrayType()) {
/*  677 */       return buildArraySerializer(prov, (ArrayType)type, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
/*      */     }
/*      */     
/*  680 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> buildCollectionSerializer(SerializerProvider prov, CollectionType type, BeanDescription beanDesc, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
/*      */     ContainerSerializer<?> containerSerializer;
/*      */     JsonSerializer<?> jsonSerializer1;
/*  694 */     SerializationConfig config = prov.getConfig();
/*  695 */     JsonSerializer<?> ser = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  700 */     for (Serializers serializers : customSerializers()) {
/*  701 */       ser = serializers.findCollectionSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
/*      */       
/*  703 */       if (ser != null) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */     
/*  708 */     if (ser == null) {
/*  709 */       ser = findSerializerByAnnotations(prov, (JavaType)type, beanDesc);
/*  710 */       if (ser == null) {
/*      */ 
/*      */         
/*  713 */         JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/*  714 */         if (format.getShape() == JsonFormat.Shape.OBJECT) {
/*  715 */           return null;
/*      */         }
/*  717 */         Class<?> raw = type.getRawClass();
/*  718 */         if (EnumSet.class.isAssignableFrom(raw)) {
/*      */           
/*  720 */           JavaType enumType = type.getContentType();
/*      */           
/*  722 */           if (!enumType.isEnumImplType()) {
/*  723 */             enumType = null;
/*      */           }
/*  725 */           ser = buildEnumSetSerializer(enumType);
/*      */         } else {
/*  727 */           StringCollectionSerializer stringCollectionSerializer; Class<?> elementRaw = type.getContentType().getRawClass();
/*  728 */           if (isIndexedList(raw)) {
/*  729 */             if (elementRaw == String.class) {
/*      */               
/*  731 */               if (ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
/*  732 */                 IndexedStringListSerializer indexedStringListSerializer = IndexedStringListSerializer.instance;
/*      */               }
/*      */             } else {
/*  735 */               containerSerializer = buildIndexedListSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
/*      */             }
/*      */           
/*  738 */           } else if (elementRaw == String.class) {
/*      */             
/*  740 */             if (ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
/*  741 */               stringCollectionSerializer = StringCollectionSerializer.instance;
/*      */             }
/*      */           } 
/*  744 */           if (stringCollectionSerializer == null) {
/*  745 */             containerSerializer = buildCollectionSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  752 */     if (this._factoryConfig.hasSerializerModifiers()) {
/*  753 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/*  754 */         jsonSerializer1 = mod.modifyCollectionSerializer(config, type, beanDesc, (JsonSerializer<?>)containerSerializer);
/*      */       }
/*      */     }
/*  757 */     return jsonSerializer1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isIndexedList(Class<?> cls) {
/*  768 */     return RandomAccess.class.isAssignableFrom(cls);
/*      */   }
/*      */ 
/*      */   
/*      */   public ContainerSerializer<?> buildIndexedListSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
/*  773 */     return (ContainerSerializer<?>)new IndexedListSerializer(elemType, staticTyping, vts, valueSerializer);
/*      */   }
/*      */ 
/*      */   
/*      */   public ContainerSerializer<?> buildCollectionSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
/*  778 */     return (ContainerSerializer<?>)new CollectionSerializer(elemType, staticTyping, vts, valueSerializer);
/*      */   }
/*      */   
/*      */   public JsonSerializer<?> buildEnumSetSerializer(JavaType enumType) {
/*  782 */     return (JsonSerializer<?>)new EnumSetSerializer(enumType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> buildMapSerializer(SerializerProvider prov, MapType type, BeanDescription beanDesc, boolean staticTyping, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
/*      */     MapSerializer mapSerializer;
/*      */     JsonSerializer<?> jsonSerializer1;
/*  803 */     JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/*  804 */     if (format.getShape() == JsonFormat.Shape.OBJECT) {
/*  805 */       return null;
/*      */     }
/*      */     
/*  808 */     JsonSerializer<?> ser = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  815 */     SerializationConfig config = prov.getConfig();
/*  816 */     for (Serializers serializers : customSerializers()) {
/*  817 */       ser = serializers.findMapSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
/*      */       
/*  819 */       if (ser != null)
/*      */         break; 
/*  821 */     }  if (ser == null) {
/*  822 */       ser = findSerializerByAnnotations(prov, (JavaType)type, beanDesc);
/*  823 */       if (ser == null) {
/*  824 */         Object filterId = findFilterId(config, beanDesc);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  829 */         JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc
/*  830 */             .getClassInfo());
/*      */         
/*  832 */         Set<String> ignored = (ignorals == null) ? null : ignorals.findIgnoredForSerialization();
/*  833 */         JsonIncludeProperties.Value inclusions = config.getDefaultPropertyInclusions(Map.class, beanDesc
/*  834 */             .getClassInfo());
/*      */         
/*  836 */         Set<String> included = (inclusions == null) ? null : inclusions.getIncluded();
/*  837 */         MapSerializer mapSer = MapSerializer.construct(ignored, included, (JavaType)type, staticTyping, elementTypeSerializer, keySerializer, elementValueSerializer, filterId);
/*      */ 
/*      */         
/*  840 */         mapSerializer = _checkMapContentInclusion(prov, beanDesc, mapSer);
/*      */       } 
/*      */     } 
/*      */     
/*  844 */     if (this._factoryConfig.hasSerializerModifiers()) {
/*  845 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/*  846 */         jsonSerializer1 = mod.modifyMapSerializer(config, type, beanDesc, (JsonSerializer<?>)mapSerializer);
/*      */       }
/*      */     }
/*  849 */     return jsonSerializer1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected MapSerializer _checkMapContentInclusion(SerializerProvider prov, BeanDescription beanDesc, MapSerializer mapSer) throws JsonMappingException {
/*  863 */     JavaType contentType = mapSer.getContentType();
/*  864 */     JsonInclude.Value inclV = _findInclusionWithContent(prov, beanDesc, contentType, Map.class);
/*      */ 
/*      */ 
/*      */     
/*  868 */     JsonInclude.Include incl = (inclV == null) ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
/*  869 */     if (incl == JsonInclude.Include.USE_DEFAULTS || incl == JsonInclude.Include.ALWAYS) {
/*      */       
/*  871 */       if (!prov.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES)) {
/*  872 */         return mapSer.withContentInclusion(null, true);
/*      */       }
/*  874 */       return mapSer;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  880 */     boolean suppressNulls = true;
/*      */     
/*  882 */     switch (incl)
/*      */     { case NON_DEFAULT:
/*  884 */         valueToSuppress = BeanUtil.getDefaultValue(contentType);
/*  885 */         if (valueToSuppress != null && 
/*  886 */           valueToSuppress.getClass().isArray()) {
/*  887 */           valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  912 */         return mapSer.withContentInclusion(valueToSuppress, suppressNulls);case NON_ABSENT: valueToSuppress = contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null; return mapSer.withContentInclusion(valueToSuppress, suppressNulls);case NON_EMPTY: valueToSuppress = MapSerializer.MARKER_FOR_EMPTY; return mapSer.withContentInclusion(valueToSuppress, suppressNulls);case CUSTOM: valueToSuppress = prov.includeFilterInstance(null, inclV.getContentFilter()); if (valueToSuppress == null) { suppressNulls = true; } else { suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress); }  return mapSer.withContentInclusion(valueToSuppress, suppressNulls); }  Object valueToSuppress = null; return mapSer.withContentInclusion(valueToSuppress, suppressNulls);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> buildMapEntrySerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType keyType, JavaType valueType) throws JsonMappingException {
/*  925 */     JsonFormat.Value formatOverride = prov.getDefaultPropertyFormat(Map.Entry.class);
/*  926 */     JsonFormat.Value formatFromAnnotation = beanDesc.findExpectedFormat(null);
/*  927 */     JsonFormat.Value format = JsonFormat.Value.merge(formatFromAnnotation, formatOverride);
/*  928 */     if (format.getShape() == JsonFormat.Shape.OBJECT) {
/*  929 */       return null;
/*      */     }
/*      */     
/*  932 */     MapEntrySerializer ser = new MapEntrySerializer(valueType, keyType, valueType, staticTyping, createTypeSerializer(prov.getConfig(), valueType), null);
/*      */     
/*  934 */     JavaType contentType = ser.getContentType();
/*  935 */     JsonInclude.Value inclV = _findInclusionWithContent(prov, beanDesc, contentType, Map.Entry.class);
/*      */ 
/*      */ 
/*      */     
/*  939 */     JsonInclude.Include incl = (inclV == null) ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
/*  940 */     if (incl == JsonInclude.Include.USE_DEFAULTS || incl == JsonInclude.Include.ALWAYS)
/*      */     {
/*  942 */       return (JsonSerializer<?>)ser;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  948 */     boolean suppressNulls = true;
/*      */     
/*  950 */     switch (incl)
/*      */     { case NON_DEFAULT:
/*  952 */         valueToSuppress = BeanUtil.getDefaultValue(contentType);
/*  953 */         if (valueToSuppress != null && 
/*  954 */           valueToSuppress.getClass().isArray()) {
/*  955 */           valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  979 */         return (JsonSerializer<?>)ser.withContentInclusion(valueToSuppress, suppressNulls);case NON_ABSENT: valueToSuppress = contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null; return (JsonSerializer<?>)ser.withContentInclusion(valueToSuppress, suppressNulls);case NON_EMPTY: valueToSuppress = MapSerializer.MARKER_FOR_EMPTY; return (JsonSerializer<?>)ser.withContentInclusion(valueToSuppress, suppressNulls);case CUSTOM: valueToSuppress = prov.includeFilterInstance(null, inclV.getContentFilter()); if (valueToSuppress == null) { suppressNulls = true; } else { suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress); }  return (JsonSerializer<?>)ser.withContentInclusion(valueToSuppress, suppressNulls); }  Object valueToSuppress = null; return (JsonSerializer<?>)ser.withContentInclusion(valueToSuppress, suppressNulls);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonInclude.Value _findInclusionWithContent(SerializerProvider prov, BeanDescription beanDesc, JavaType contentType, Class<?> configType) throws JsonMappingException {
/*  995 */     SerializationConfig config = prov.getConfig();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1002 */     JsonInclude.Value inclV = beanDesc.findPropertyInclusion(config.getDefaultPropertyInclusion());
/* 1003 */     inclV = config.getDefaultPropertyInclusion(configType, inclV);
/*      */ 
/*      */ 
/*      */     
/* 1007 */     JsonInclude.Value valueIncl = config.getDefaultPropertyInclusion(contentType.getRawClass(), null);
/*      */     
/* 1009 */     if (valueIncl != null) {
/* 1010 */       switch (valueIncl.getValueInclusion()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case USE_DEFAULTS:
/* 1020 */           return inclV;
/*      */         case CUSTOM:
/*      */           inclV = inclV.withContentFilter(valueIncl.getContentFilter());
/*      */       } 
/*      */       inclV = inclV.withContentInclusion(valueIncl.getValueInclusion());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> buildArraySerializer(SerializerProvider prov, ArrayType type, BeanDescription beanDesc, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
/*      */     ObjectArraySerializer objectArraySerializer;
/*      */     JsonSerializer<?> jsonSerializer1;
/* 1043 */     SerializationConfig config = prov.getConfig();
/* 1044 */     JsonSerializer<?> ser = null;
/*      */     
/* 1046 */     for (Serializers serializers : customSerializers()) {
/* 1047 */       ser = serializers.findArraySerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
/*      */       
/* 1049 */       if (ser != null) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */     
/* 1054 */     if (ser == null) {
/* 1055 */       Class<?> raw = type.getRawClass();
/*      */       
/* 1057 */       if (elementValueSerializer == null || ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
/* 1058 */         if (String[].class == raw) {
/* 1059 */           StringArraySerializer stringArraySerializer = StringArraySerializer.instance;
/*      */         } else {
/*      */           
/* 1062 */           ser = StdArraySerializers.findStandardImpl(raw);
/*      */         } 
/*      */       }
/* 1065 */       if (ser == null) {
/* 1066 */         objectArraySerializer = new ObjectArraySerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1071 */     if (this._factoryConfig.hasSerializerModifiers()) {
/* 1072 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 1073 */         jsonSerializer1 = mod.modifyArraySerializer(config, type, beanDesc, (JsonSerializer<?>)objectArraySerializer);
/*      */       }
/*      */     }
/* 1076 */     return jsonSerializer1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonSerializer<?> findReferenceSerializer(SerializerProvider prov, ReferenceType refType, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
/* 1093 */     JavaType contentType = refType.getContentType();
/* 1094 */     TypeSerializer contentTypeSerializer = (TypeSerializer)contentType.getTypeHandler();
/* 1095 */     SerializationConfig config = prov.getConfig();
/* 1096 */     if (contentTypeSerializer == null) {
/* 1097 */       contentTypeSerializer = createTypeSerializer(config, contentType);
/*      */     }
/* 1099 */     JsonSerializer<Object> contentSerializer = (JsonSerializer<Object>)contentType.getValueHandler();
/* 1100 */     for (Serializers serializers : customSerializers()) {
/* 1101 */       JsonSerializer<?> ser = serializers.findReferenceSerializer(config, refType, beanDesc, contentTypeSerializer, contentSerializer);
/*      */       
/* 1103 */       if (ser != null) {
/* 1104 */         return ser;
/*      */       }
/*      */     } 
/* 1107 */     if (refType.isTypeOrSubTypeOf(AtomicReference.class)) {
/* 1108 */       return buildAtomicReferenceSerializer(prov, refType, beanDesc, staticTyping, contentTypeSerializer, contentSerializer);
/*      */     }
/*      */     
/* 1111 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> buildAtomicReferenceSerializer(SerializerProvider prov, ReferenceType refType, BeanDescription beanDesc, boolean staticTyping, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentSerializer) throws JsonMappingException {
/*      */     Object valueToSuppress;
/*      */     boolean suppressNulls;
/* 1119 */     JavaType contentType = refType.getReferencedType();
/* 1120 */     JsonInclude.Value inclV = _findInclusionWithContent(prov, beanDesc, contentType, AtomicReference.class);
/*      */ 
/*      */ 
/*      */     
/* 1124 */     JsonInclude.Include incl = (inclV == null) ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
/*      */ 
/*      */ 
/*      */     
/* 1128 */     if (incl == JsonInclude.Include.USE_DEFAULTS || incl == JsonInclude.Include.ALWAYS)
/*      */     
/* 1130 */     { valueToSuppress = null;
/* 1131 */       suppressNulls = false; }
/*      */     else
/* 1133 */     { AtomicReferenceSerializer ser; suppressNulls = true;
/* 1134 */       switch (incl)
/*      */       { case NON_DEFAULT:
/* 1136 */           valueToSuppress = BeanUtil.getDefaultValue(contentType);
/* 1137 */           if (valueToSuppress != null && 
/* 1138 */             valueToSuppress.getClass().isArray()) {
/* 1139 */             valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1164 */           ser = new AtomicReferenceSerializer(refType, staticTyping, contentTypeSerializer, contentSerializer);
/*      */           
/* 1166 */           return (JsonSerializer<?>)ser.withContentInclusion(valueToSuppress, suppressNulls);case NON_ABSENT: valueToSuppress = contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null; ser = new AtomicReferenceSerializer(refType, staticTyping, contentTypeSerializer, contentSerializer); return (JsonSerializer<?>)ser.withContentInclusion(valueToSuppress, suppressNulls);case NON_EMPTY: valueToSuppress = MapSerializer.MARKER_FOR_EMPTY; ser = new AtomicReferenceSerializer(refType, staticTyping, contentTypeSerializer, contentSerializer); return (JsonSerializer<?>)ser.withContentInclusion(valueToSuppress, suppressNulls);case CUSTOM: valueToSuppress = prov.includeFilterInstance(null, inclV.getContentFilter()); if (valueToSuppress == null) { suppressNulls = true; } else { suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress); }  ser = new AtomicReferenceSerializer(refType, staticTyping, contentTypeSerializer, contentSerializer); return (JsonSerializer<?>)ser.withContentInclusion(valueToSuppress, suppressNulls); }  valueToSuppress = null; }  AtomicReferenceSerializer atomicReferenceSerializer = new AtomicReferenceSerializer(refType, staticTyping, contentTypeSerializer, contentSerializer); return (JsonSerializer<?>)atomicReferenceSerializer.withContentInclusion(valueToSuppress, suppressNulls);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> buildIteratorSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType valueType) throws JsonMappingException {
/* 1183 */     return (JsonSerializer<?>)new IteratorSerializer(valueType, staticTyping, createTypeSerializer(config, valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> buildIterableSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType valueType) throws JsonMappingException {
/* 1194 */     return (JsonSerializer<?>)new IterableSerializer(valueType, staticTyping, createTypeSerializer(config, valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<?> buildEnumSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*      */     JsonSerializer<?> jsonSerializer;
/* 1206 */     JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/* 1207 */     if (format.getShape() == JsonFormat.Shape.OBJECT) {
/*      */       
/* 1209 */       ((BasicBeanDescription)beanDesc).removeProperty("declaringClass");
/*      */       
/* 1211 */       return null;
/*      */     } 
/*      */     
/* 1214 */     Class<Enum<?>> enumClass = type.getRawClass();
/* 1215 */     EnumSerializer enumSerializer = EnumSerializer.construct(enumClass, config, beanDesc, format);
/*      */     
/* 1217 */     if (this._factoryConfig.hasSerializerModifiers()) {
/* 1218 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 1219 */         jsonSerializer = mod.modifyEnumSerializer(config, type, beanDesc, (JsonSerializer<?>)enumSerializer);
/*      */       }
/*      */     }
/* 1222 */     return jsonSerializer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<Object> _findKeySerializer(SerializerProvider prov, Annotated a) throws JsonMappingException {
/* 1240 */     AnnotationIntrospector intr = prov.getAnnotationIntrospector();
/* 1241 */     Object serDef = intr.findKeySerializer(a);
/* 1242 */     if (serDef != null) {
/* 1243 */       return prov.serializerInstance(a, serDef);
/*      */     }
/* 1245 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<Object> _findContentSerializer(SerializerProvider prov, Annotated a) throws JsonMappingException {
/* 1257 */     AnnotationIntrospector intr = prov.getAnnotationIntrospector();
/* 1258 */     Object serDef = intr.findContentSerializer(a);
/* 1259 */     if (serDef != null) {
/* 1260 */       return prov.serializerInstance(a, serDef);
/*      */     }
/* 1262 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object findFilterId(SerializationConfig config, BeanDescription beanDesc) {
/* 1270 */     return config.getAnnotationIntrospector().findFilterId((Annotated)beanDesc.getClassInfo());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean usesStaticTyping(SerializationConfig config, BeanDescription beanDesc, TypeSerializer typeSer) {
/* 1287 */     if (typeSer != null) {
/* 1288 */       return false;
/*      */     }
/* 1290 */     AnnotationIntrospector intr = config.getAnnotationIntrospector();
/* 1291 */     JsonSerialize.Typing t = intr.findSerializationTyping((Annotated)beanDesc.getClassInfo());
/* 1292 */     if (t != null && t != JsonSerialize.Typing.DEFAULT_TYPING) {
/* 1293 */       return (t == JsonSerialize.Typing.STATIC);
/*      */     }
/* 1295 */     return config.isEnabled(MapperFeature.USE_STATIC_TYPING);
/*      */   }
/*      */   
/*      */   public abstract SerializerFactory withConfig(SerializerFactoryConfig paramSerializerFactoryConfig);
/*      */   
/*      */   public abstract JsonSerializer<Object> createSerializer(SerializerProvider paramSerializerProvider, JavaType paramJavaType) throws JsonMappingException;
/*      */   
/*      */   protected abstract Iterable<Serializers> customSerializers();
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/BasicSerializerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */