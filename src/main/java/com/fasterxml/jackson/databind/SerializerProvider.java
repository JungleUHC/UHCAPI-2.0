/*      */ package com.fasterxml.jackson.databind;
/*      */ 
/*      */ import com.fasterxml.jackson.annotation.JsonFormat;
/*      */ import com.fasterxml.jackson.annotation.JsonInclude;
/*      */ import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
/*      */ import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
/*      */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*      */ import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.FilterProvider;
/*      */ import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.SerializerCache;
/*      */ import com.fasterxml.jackson.databind.ser.SerializerFactory;
/*      */ import com.fasterxml.jackson.databind.ser.impl.FailingSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
/*      */ import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.impl.UnknownSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
/*      */ import com.fasterxml.jackson.databind.ser.std.NullSerializer;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*      */ import java.io.IOException;
/*      */ import java.text.DateFormat;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
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
/*      */ public abstract class SerializerProvider
/*      */   extends DatabindContext
/*      */ {
/*      */   protected static final boolean CACHE_UNKNOWN_MAPPINGS = false;
/*   59 */   public static final JsonSerializer<Object> DEFAULT_NULL_KEY_SERIALIZER = (JsonSerializer<Object>)new FailingSerializer("Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)");
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
/*   71 */   protected static final JsonSerializer<Object> DEFAULT_UNKNOWN_SERIALIZER = (JsonSerializer<Object>)new UnknownSerializer();
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
/*      */   protected final SerializationConfig _config;
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
/*      */   protected final Class<?> _serializationView;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final SerializerFactory _serializerFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final SerializerCache _serializerCache;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected transient ContextAttributes _attributes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  133 */   protected JsonSerializer<Object> _unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<Object> _keySerializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  146 */   protected JsonSerializer<Object> _nullValueSerializer = (JsonSerializer<Object>)NullSerializer.instance;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  155 */   protected JsonSerializer<Object> _nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final ReadOnlyClassToSerializerMap _knownSerializers;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DateFormat _dateFormat;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _stdNullValueSerializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SerializerProvider() {
/*  196 */     this._config = null;
/*  197 */     this._serializerFactory = null;
/*  198 */     this._serializerCache = new SerializerCache();
/*      */     
/*  200 */     this._knownSerializers = null;
/*      */     
/*  202 */     this._serializationView = null;
/*  203 */     this._attributes = null;
/*      */ 
/*      */     
/*  206 */     this._stdNullValueSerializer = true;
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
/*      */   protected SerializerProvider(SerializerProvider src, SerializationConfig config, SerializerFactory f) {
/*  218 */     this._serializerFactory = f;
/*  219 */     this._config = config;
/*      */     
/*  221 */     this._serializerCache = src._serializerCache;
/*  222 */     this._unknownTypeSerializer = src._unknownTypeSerializer;
/*  223 */     this._keySerializer = src._keySerializer;
/*  224 */     this._nullValueSerializer = src._nullValueSerializer;
/*  225 */     this._nullKeySerializer = src._nullKeySerializer;
/*      */     
/*  227 */     this._stdNullValueSerializer = (this._nullValueSerializer == DEFAULT_NULL_KEY_SERIALIZER);
/*      */     
/*  229 */     this._serializationView = config.getActiveView();
/*  230 */     this._attributes = config.getAttributes();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  235 */     this._knownSerializers = this._serializerCache.getReadOnlyLookupMap();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SerializerProvider(SerializerProvider src) {
/*  246 */     this._config = null;
/*  247 */     this._serializationView = null;
/*  248 */     this._serializerFactory = null;
/*  249 */     this._knownSerializers = null;
/*      */ 
/*      */     
/*  252 */     this._serializerCache = new SerializerCache();
/*      */     
/*  254 */     this._unknownTypeSerializer = src._unknownTypeSerializer;
/*  255 */     this._keySerializer = src._keySerializer;
/*  256 */     this._nullValueSerializer = src._nullValueSerializer;
/*  257 */     this._nullKeySerializer = src._nullKeySerializer;
/*      */     
/*  259 */     this._stdNullValueSerializer = src._stdNullValueSerializer;
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
/*      */   public void setDefaultKeySerializer(JsonSerializer<Object> ks) {
/*  279 */     if (ks == null) {
/*  280 */       throw new IllegalArgumentException("Cannot pass null JsonSerializer");
/*      */     }
/*  282 */     this._keySerializer = ks;
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
/*      */   public void setNullValueSerializer(JsonSerializer<Object> nvs) {
/*  296 */     if (nvs == null) {
/*  297 */       throw new IllegalArgumentException("Cannot pass null JsonSerializer");
/*      */     }
/*  299 */     this._nullValueSerializer = nvs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNullKeySerializer(JsonSerializer<Object> nks) {
/*  309 */     if (nks == null) {
/*  310 */       throw new IllegalArgumentException("Cannot pass null JsonSerializer");
/*      */     }
/*  312 */     this._nullKeySerializer = nks;
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
/*      */   public final SerializationConfig getConfig() {
/*  325 */     return this._config;
/*      */   }
/*      */   
/*      */   public final AnnotationIntrospector getAnnotationIntrospector() {
/*  329 */     return this._config.getAnnotationIntrospector();
/*      */   }
/*      */ 
/*      */   
/*      */   public final TypeFactory getTypeFactory() {
/*  334 */     return this._config.getTypeFactory();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) throws IllegalArgumentException {
/*  341 */     if (baseType.hasRawClass(subclass)) {
/*  342 */       return baseType;
/*      */     }
/*      */ 
/*      */     
/*  346 */     return getConfig().getTypeFactory().constructSpecializedType(baseType, subclass, true);
/*      */   }
/*      */   
/*      */   public final Class<?> getActiveView() {
/*  350 */     return this._serializationView;
/*      */   }
/*      */   
/*      */   public final boolean canOverrideAccessModifiers() {
/*  354 */     return this._config.canOverrideAccessModifiers();
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(MapperFeature feature) {
/*  359 */     return this._config.isEnabled(feature);
/*      */   }
/*      */ 
/*      */   
/*      */   public final JsonFormat.Value getDefaultPropertyFormat(Class<?> baseType) {
/*  364 */     return this._config.getDefaultPropertyFormat(baseType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType) {
/*  371 */     return this._config.getDefaultPropertyInclusion(baseType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Locale getLocale() {
/*  382 */     return this._config.getLocale();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TimeZone getTimeZone() {
/*  393 */     return this._config.getTimeZone();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getAttribute(Object key) {
/*  404 */     return this._attributes.getAttribute(key);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public SerializerProvider setAttribute(Object key, Object value) {
/*  410 */     this._attributes = this._attributes.withPerCallAttribute(key, value);
/*  411 */     return this;
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
/*      */   public final boolean isEnabled(SerializationFeature feature) {
/*  429 */     return this._config.isEnabled(feature);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean hasSerializationFeatures(int featureMask) {
/*  439 */     return this._config.hasSerializationFeatures(featureMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final FilterProvider getFilterProvider() {
/*  450 */     return this._config.getFilterProvider();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator getGenerator() {
/*  461 */     return null;
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
/*      */   public TokenBuffer bufferForValueConversion(ObjectCodec oc) {
/*  479 */     return new TokenBuffer(oc, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final TokenBuffer bufferForValueConversion() {
/*  489 */     return bufferForValueConversion((ObjectCodec)null);
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
/*      */   public JsonSerializer<Object> findValueSerializer(Class<?> valueType, BeanProperty property) throws JsonMappingException {
/*  535 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  536 */     if (ser == null) {
/*      */       
/*  538 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  539 */       if (ser == null) {
/*      */         
/*  541 */         ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
/*  542 */         if (ser == null) {
/*      */           
/*  544 */           ser = _createAndCacheUntypedSerializer(valueType);
/*      */           
/*  546 */           if (ser == null) {
/*  547 */             ser = getUnknownTypeSerializer(valueType);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  552 */             return ser;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  558 */     return (JsonSerializer)handleSecondaryContextualization(ser, property);
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
/*      */   public JsonSerializer<Object> findValueSerializer(JavaType valueType, BeanProperty property) throws JsonMappingException {
/*  577 */     if (valueType == null) {
/*  578 */       reportMappingProblem("Null passed for `valueType` of `findValueSerializer()`", new Object[0]);
/*      */     }
/*      */     
/*  581 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  582 */     if (ser == null) {
/*  583 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  584 */       if (ser == null) {
/*  585 */         ser = _createAndCacheUntypedSerializer(valueType);
/*  586 */         if (ser == null) {
/*  587 */           ser = getUnknownTypeSerializer(valueType.getRawClass());
/*      */ 
/*      */ 
/*      */           
/*  591 */           return ser;
/*      */         } 
/*      */       } 
/*      */     } 
/*  595 */     return (JsonSerializer)handleSecondaryContextualization(ser, property);
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
/*      */   public JsonSerializer<Object> findValueSerializer(Class<?> valueType) throws JsonMappingException {
/*  608 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  609 */     if (ser == null) {
/*  610 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  611 */       if (ser == null) {
/*  612 */         ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
/*  613 */         if (ser == null) {
/*  614 */           ser = _createAndCacheUntypedSerializer(valueType);
/*  615 */           if (ser == null) {
/*  616 */             ser = getUnknownTypeSerializer(valueType);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  624 */     return ser;
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
/*      */   public JsonSerializer<Object> findValueSerializer(JavaType valueType) throws JsonMappingException {
/*  638 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  639 */     if (ser == null) {
/*  640 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  641 */       if (ser == null) {
/*  642 */         ser = _createAndCacheUntypedSerializer(valueType);
/*  643 */         if (ser == null) {
/*  644 */           ser = getUnknownTypeSerializer(valueType.getRawClass());
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  651 */     return ser;
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
/*      */   public JsonSerializer<Object> findPrimaryPropertySerializer(JavaType valueType, BeanProperty property) throws JsonMappingException {
/*  671 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  672 */     if (ser == null) {
/*  673 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  674 */       if (ser == null) {
/*  675 */         ser = _createAndCacheUntypedSerializer(valueType);
/*  676 */         if (ser == null) {
/*  677 */           ser = getUnknownTypeSerializer(valueType.getRawClass());
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  682 */           return ser;
/*      */         } 
/*      */       } 
/*      */     } 
/*  686 */     return (JsonSerializer)handlePrimaryContextualization(ser, property);
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
/*      */   public JsonSerializer<Object> findPrimaryPropertySerializer(Class<?> valueType, BeanProperty property) throws JsonMappingException {
/*  699 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  700 */     if (ser == null) {
/*  701 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  702 */       if (ser == null) {
/*  703 */         ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
/*  704 */         if (ser == null) {
/*  705 */           ser = _createAndCacheUntypedSerializer(valueType);
/*  706 */           if (ser == null) {
/*  707 */             ser = getUnknownTypeSerializer(valueType);
/*      */ 
/*      */ 
/*      */             
/*  711 */             return ser;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*  716 */     return (JsonSerializer)handlePrimaryContextualization(ser, property);
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
/*      */ 
/*      */   
/*      */   public JsonSerializer<Object> findContentValueSerializer(JavaType valueType, BeanProperty property) throws JsonMappingException {
/*  743 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  744 */     if (ser == null) {
/*  745 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  746 */       if (ser == null) {
/*  747 */         ser = _createAndCacheUntypedSerializer(valueType);
/*  748 */         if (ser == null) {
/*  749 */           ser = getUnknownTypeSerializer(valueType.getRawClass());
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  754 */           return ser;
/*      */         } 
/*      */       } 
/*      */     } 
/*  758 */     return (JsonSerializer)handleSecondaryContextualization(ser, property);
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
/*      */   public JsonSerializer<Object> findContentValueSerializer(Class<?> valueType, BeanProperty property) throws JsonMappingException {
/*  771 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  772 */     if (ser == null) {
/*  773 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  774 */       if (ser == null) {
/*  775 */         ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
/*  776 */         if (ser == null) {
/*  777 */           ser = _createAndCacheUntypedSerializer(valueType);
/*  778 */           if (ser == null) {
/*  779 */             ser = getUnknownTypeSerializer(valueType);
/*      */ 
/*      */ 
/*      */             
/*  783 */             return ser;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*  788 */     return (JsonSerializer)handleSecondaryContextualization(ser, property);
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
/*      */   public JsonSerializer<Object> findTypedValueSerializer(Class<?> valueType, boolean cache, BeanProperty property) throws JsonMappingException {
/*      */     TypeWrappedSerializer typeWrappedSerializer;
/*  811 */     JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
/*  812 */     if (ser != null) {
/*  813 */       return ser;
/*      */     }
/*      */     
/*  816 */     ser = this._serializerCache.typedValueSerializer(valueType);
/*  817 */     if (ser != null) {
/*  818 */       return ser;
/*      */     }
/*      */ 
/*      */     
/*  822 */     ser = findValueSerializer(valueType, property);
/*  823 */     TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, this._config
/*  824 */         .constructType(valueType));
/*  825 */     if (typeSer != null) {
/*  826 */       typeSer = typeSer.forProperty(property);
/*  827 */       typeWrappedSerializer = new TypeWrappedSerializer(typeSer, ser);
/*      */     } 
/*  829 */     if (cache) {
/*  830 */       this._serializerCache.addTypedSerializer(valueType, (JsonSerializer)typeWrappedSerializer);
/*      */     }
/*  832 */     return (JsonSerializer<Object>)typeWrappedSerializer;
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
/*      */   public JsonSerializer<Object> findTypedValueSerializer(JavaType valueType, boolean cache, BeanProperty property) throws JsonMappingException {
/*      */     TypeWrappedSerializer typeWrappedSerializer;
/*  856 */     JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
/*  857 */     if (ser != null) {
/*  858 */       return ser;
/*      */     }
/*      */     
/*  861 */     ser = this._serializerCache.typedValueSerializer(valueType);
/*  862 */     if (ser != null) {
/*  863 */       return ser;
/*      */     }
/*      */ 
/*      */     
/*  867 */     ser = findValueSerializer(valueType, property);
/*  868 */     TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, valueType);
/*  869 */     if (typeSer != null) {
/*  870 */       typeSer = typeSer.forProperty(property);
/*  871 */       typeWrappedSerializer = new TypeWrappedSerializer(typeSer, ser);
/*      */     } 
/*  873 */     if (cache) {
/*  874 */       this._serializerCache.addTypedSerializer(valueType, (JsonSerializer)typeWrappedSerializer);
/*      */     }
/*  876 */     return (JsonSerializer<Object>)typeWrappedSerializer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeSerializer findTypeSerializer(JavaType javaType) throws JsonMappingException {
/*  887 */     return this._serializerFactory.createTypeSerializer(this._config, javaType);
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
/*      */   public JsonSerializer<Object> findKeySerializer(JavaType keyType, BeanProperty property) throws JsonMappingException {
/*  903 */     JsonSerializer<Object> ser = this._serializerFactory.createKeySerializer(this, keyType, this._keySerializer);
/*      */     
/*  905 */     return _handleContextualResolvable(ser, property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonSerializer<Object> findKeySerializer(Class<?> rawKeyType, BeanProperty property) throws JsonMappingException {
/*  914 */     return findKeySerializer(this._config.constructType(rawKeyType), property);
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
/*      */   public JsonSerializer<Object> getDefaultNullKeySerializer() {
/*  927 */     return this._nullKeySerializer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonSerializer<Object> getDefaultNullValueSerializer() {
/*  934 */     return this._nullValueSerializer;
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
/*      */   public JsonSerializer<Object> findNullKeySerializer(JavaType serializationType, BeanProperty property) throws JsonMappingException {
/*  958 */     return this._nullKeySerializer;
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
/*      */   public JsonSerializer<Object> findNullValueSerializer(BeanProperty property) throws JsonMappingException {
/*  974 */     return this._nullValueSerializer;
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
/*      */   public JsonSerializer<Object> getUnknownTypeSerializer(Class<?> unknownType) {
/*  991 */     if (unknownType == Object.class) {
/*  992 */       return this._unknownTypeSerializer;
/*      */     }
/*      */     
/*  995 */     return (JsonSerializer<Object>)new UnknownSerializer(unknownType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isUnknownTypeSerializer(JsonSerializer<?> ser) {
/* 1006 */     if (ser == this._unknownTypeSerializer || ser == null) {
/* 1007 */       return true;
/*      */     }
/*      */ 
/*      */     
/* 1011 */     if (isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS) && 
/* 1012 */       ser.getClass() == UnknownSerializer.class) {
/* 1013 */       return true;
/*      */     }
/*      */     
/* 1016 */     return false;
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
/*      */   public JsonSerializer<?> handlePrimaryContextualization(JsonSerializer<?> ser, BeanProperty property) throws JsonMappingException {
/* 1083 */     if (ser != null && 
/* 1084 */       ser instanceof ContextualSerializer) {
/* 1085 */       ser = ((ContextualSerializer)ser).createContextual(this, property);
/*      */     }
/*      */     
/* 1088 */     return ser;
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
/*      */   public JsonSerializer<?> handleSecondaryContextualization(JsonSerializer<?> ser, BeanProperty property) throws JsonMappingException {
/* 1111 */     if (ser != null && 
/* 1112 */       ser instanceof ContextualSerializer) {
/* 1113 */       ser = ((ContextualSerializer)ser).createContextual(this, property);
/*      */     }
/*      */     
/* 1116 */     return ser;
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
/*      */   public final void defaultSerializeValue(Object value, JsonGenerator gen) throws IOException {
/* 1134 */     if (value == null) {
/* 1135 */       if (this._stdNullValueSerializer) {
/* 1136 */         gen.writeNull();
/*      */       } else {
/* 1138 */         this._nullValueSerializer.serialize(null, gen, this);
/*      */       } 
/*      */     } else {
/* 1141 */       Class<?> cls = value.getClass();
/* 1142 */       findTypedValueSerializer(cls, true, (BeanProperty)null).serialize(value, gen, this);
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
/*      */   public final void defaultSerializeField(String fieldName, Object value, JsonGenerator gen) throws IOException {
/* 1154 */     gen.writeFieldName(fieldName);
/* 1155 */     if (value == null) {
/*      */ 
/*      */ 
/*      */       
/* 1159 */       if (this._stdNullValueSerializer) {
/* 1160 */         gen.writeNull();
/*      */       } else {
/* 1162 */         this._nullValueSerializer.serialize(null, gen, this);
/*      */       } 
/*      */     } else {
/* 1165 */       Class<?> cls = value.getClass();
/* 1166 */       findTypedValueSerializer(cls, true, (BeanProperty)null).serialize(value, gen, this);
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
/*      */   public final void defaultSerializeDateValue(long timestamp, JsonGenerator gen) throws IOException {
/* 1180 */     if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
/* 1181 */       gen.writeNumber(timestamp);
/*      */     } else {
/* 1183 */       gen.writeString(_dateFormat().format(new Date(timestamp)));
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
/*      */   public final void defaultSerializeDateValue(Date date, JsonGenerator gen) throws IOException {
/* 1196 */     if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
/* 1197 */       gen.writeNumber(date.getTime());
/*      */     } else {
/* 1199 */       gen.writeString(_dateFormat().format(date));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void defaultSerializeDateKey(long timestamp, JsonGenerator gen) throws IOException {
/* 1210 */     if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
/* 1211 */       gen.writeFieldName(String.valueOf(timestamp));
/*      */     } else {
/* 1213 */       gen.writeFieldName(_dateFormat().format(new Date(timestamp)));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void defaultSerializeDateKey(Date date, JsonGenerator gen) throws IOException {
/* 1224 */     if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
/* 1225 */       gen.writeFieldName(String.valueOf(date.getTime()));
/*      */     } else {
/* 1227 */       gen.writeFieldName(_dateFormat().format(date));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final void defaultSerializeNull(JsonGenerator gen) throws IOException {
/* 1233 */     if (this._stdNullValueSerializer) {
/* 1234 */       gen.writeNull();
/*      */     } else {
/* 1236 */       this._nullValueSerializer.serialize(null, gen, this);
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
/*      */   
/*      */   public void reportMappingProblem(String message, Object... args) throws JsonMappingException {
/* 1254 */     throw mappingException(message, args);
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
/*      */   public <T> T reportBadTypeDefinition(BeanDescription bean, String msg, Object... msgArgs) throws JsonMappingException {
/* 1266 */     String beanDesc = "N/A";
/* 1267 */     if (bean != null) {
/* 1268 */       beanDesc = ClassUtil.nameOf(bean.getBeanClass());
/*      */     }
/* 1270 */     msg = String.format("Invalid type definition for type %s: %s", new Object[] { beanDesc, 
/* 1271 */           _format(msg, msgArgs) });
/* 1272 */     throw InvalidDefinitionException.from(getGenerator(), msg, bean, null);
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
/*      */   public <T> T reportBadPropertyDefinition(BeanDescription bean, BeanPropertyDefinition prop, String message, Object... msgArgs) throws JsonMappingException {
/* 1284 */     message = _format(message, msgArgs);
/* 1285 */     String propName = "N/A";
/* 1286 */     if (prop != null) {
/* 1287 */       propName = _quotedString(prop.getName());
/*      */     }
/* 1289 */     String beanDesc = "N/A";
/* 1290 */     if (bean != null) {
/* 1291 */       beanDesc = ClassUtil.nameOf(bean.getBeanClass());
/*      */     }
/* 1293 */     message = String.format("Invalid definition for property %s (of type %s): %s", new Object[] { propName, beanDesc, message });
/*      */     
/* 1295 */     throw InvalidDefinitionException.from(getGenerator(), message, bean, prop);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T reportBadDefinition(JavaType type, String msg) throws JsonMappingException {
/* 1300 */     throw InvalidDefinitionException.from(getGenerator(), msg, type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T reportBadDefinition(JavaType type, String msg, Throwable cause) throws JsonMappingException {
/* 1308 */     throw InvalidDefinitionException.from(getGenerator(), msg, type)
/* 1309 */       .withCause(cause);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T reportBadDefinition(Class<?> raw, String msg, Throwable cause) throws JsonMappingException {
/* 1317 */     throw InvalidDefinitionException.from(getGenerator(), msg, constructType(raw))
/* 1318 */       .withCause(cause);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void reportMappingProblem(Throwable t, String message, Object... msgArgs) throws JsonMappingException {
/* 1329 */     message = _format(message, msgArgs);
/* 1330 */     throw JsonMappingException.from(getGenerator(), message, t);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonMappingException invalidTypeIdException(JavaType baseType, String typeId, String extraDesc) {
/* 1336 */     String msg = String.format("Could not resolve type id '%s' as a subtype of %s", new Object[] { typeId, 
/* 1337 */           ClassUtil.getTypeDescription(baseType) });
/* 1338 */     return (JsonMappingException)InvalidTypeIdException.from(null, _colonConcat(msg, extraDesc), baseType, typeId);
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
/*      */   @Deprecated
/*      */   public JsonMappingException mappingException(String message, Object... msgArgs) {
/* 1358 */     return JsonMappingException.from(getGenerator(), _format(message, msgArgs));
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
/*      */   @Deprecated
/*      */   protected JsonMappingException mappingException(Throwable t, String message, Object... msgArgs) {
/* 1372 */     return JsonMappingException.from(getGenerator(), _format(message, msgArgs), t);
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
/*      */   protected void _reportIncompatibleRootType(Object value, JavaType rootType) throws IOException {
/* 1384 */     if (rootType.isPrimitive()) {
/* 1385 */       Class<?> wrapperType = ClassUtil.wrapperType(rootType.getRawClass());
/*      */       
/* 1387 */       if (wrapperType.isAssignableFrom(value.getClass())) {
/*      */         return;
/*      */       }
/*      */     } 
/* 1391 */     reportBadDefinition(rootType, String.format("Incompatible types: declared root type (%s) vs %s", new Object[] { rootType, 
/*      */             
/* 1393 */             ClassUtil.classNameOf(value) }));
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
/*      */   protected JsonSerializer<Object> _findExplicitUntypedSerializer(Class<?> runtimeType) throws JsonMappingException {
/* 1407 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(runtimeType);
/* 1408 */     if (ser == null) {
/*      */       
/* 1410 */       ser = this._serializerCache.untypedValueSerializer(runtimeType);
/* 1411 */       if (ser == null) {
/* 1412 */         ser = _createAndCacheUntypedSerializer(runtimeType);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1420 */     if (isUnknownTypeSerializer(ser)) {
/* 1421 */       return null;
/*      */     }
/* 1423 */     return ser;
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
/*      */   protected JsonSerializer<Object> _createAndCacheUntypedSerializer(Class<?> rawType) throws JsonMappingException {
/*      */     JsonSerializer<Object> ser;
/* 1440 */     JavaType fullType = this._config.constructType(rawType);
/*      */     
/*      */     try {
/* 1443 */       ser = _createUntypedSerializer(fullType);
/* 1444 */     } catch (IllegalArgumentException iae) {
/*      */ 
/*      */       
/* 1447 */       reportBadDefinition(fullType, ClassUtil.exceptionMessage(iae));
/* 1448 */       ser = null;
/*      */     } 
/*      */     
/* 1451 */     if (ser != null)
/*      */     {
/* 1453 */       this._serializerCache.addAndResolveNonTypedSerializer(rawType, fullType, ser, this);
/*      */     }
/* 1455 */     return ser;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<Object> _createAndCacheUntypedSerializer(JavaType type) throws JsonMappingException {
/*      */     JsonSerializer<Object> ser;
/*      */     try {
/* 1463 */       ser = _createUntypedSerializer(type);
/* 1464 */     } catch (IllegalArgumentException iae) {
/*      */ 
/*      */       
/* 1467 */       ser = null;
/* 1468 */       reportMappingProblem(iae, ClassUtil.exceptionMessage(iae), new Object[0]);
/*      */     } 
/*      */     
/* 1471 */     if (ser != null)
/*      */     {
/* 1473 */       this._serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
/*      */     }
/* 1475 */     return ser;
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
/*      */   protected JsonSerializer<Object> _createUntypedSerializer(JavaType type) throws JsonMappingException {
/* 1495 */     return this._serializerFactory.createSerializer(this, type);
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
/*      */   protected JsonSerializer<Object> _handleContextualResolvable(JsonSerializer<?> ser, BeanProperty property) throws JsonMappingException {
/* 1508 */     if (ser instanceof ResolvableSerializer) {
/* 1509 */       ((ResolvableSerializer)ser).resolve(this);
/*      */     }
/* 1511 */     return (JsonSerializer)handleSecondaryContextualization(ser, property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<Object> _handleResolvable(JsonSerializer<?> ser) throws JsonMappingException {
/* 1518 */     if (ser instanceof ResolvableSerializer) {
/* 1519 */       ((ResolvableSerializer)ser).resolve(this);
/*      */     }
/* 1521 */     return (JsonSerializer)ser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final DateFormat _dateFormat() {
/* 1532 */     if (this._dateFormat != null) {
/* 1533 */       return this._dateFormat;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1539 */     DateFormat df = this._config.getDateFormat();
/* 1540 */     this._dateFormat = df = (DateFormat)df.clone();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1549 */     return df;
/*      */   }
/*      */   
/*      */   public abstract WritableObjectId findObjectId(Object paramObject, ObjectIdGenerator<?> paramObjectIdGenerator);
/*      */   
/*      */   public abstract JsonSerializer<Object> serializerInstance(Annotated paramAnnotated, Object paramObject) throws JsonMappingException;
/*      */   
/*      */   public abstract Object includeFilterInstance(BeanPropertyDefinition paramBeanPropertyDefinition, Class<?> paramClass) throws JsonMappingException;
/*      */   
/*      */   public abstract boolean includeFilterSuppressNulls(Object paramObject) throws JsonMappingException;
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/SerializerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */