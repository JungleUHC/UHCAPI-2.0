/*      */ package com.fasterxml.jackson.databind.ser.std;
/*      */ import com.fasterxml.jackson.annotation.JsonFormat;
/*      */ import com.fasterxml.jackson.annotation.JsonInclude;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*      */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*      */ import com.fasterxml.jackson.databind.BeanProperty;
/*      */ import com.fasterxml.jackson.databind.DatabindException;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.JsonMappingException;
/*      */ import com.fasterxml.jackson.databind.JsonNode;
/*      */ import com.fasterxml.jackson.databind.JsonSerializer;
/*      */ import com.fasterxml.jackson.databind.SerializationConfig;
/*      */ import com.fasterxml.jackson.databind.SerializationFeature;
/*      */ import com.fasterxml.jackson.databind.SerializerProvider;
/*      */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*      */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
/*      */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*      */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.ContainerSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.PropertyFilter;
/*      */ import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Type;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ @JacksonStdImpl
/*      */ public class MapSerializer extends ContainerSerializer<Map<?, ?>> implements ContextualSerializer {
/*   42 */   protected static final JavaType UNSPECIFIED_TYPE = TypeFactory.unknownType();
/*      */ 
/*      */   
/*      */   private static final long serialVersionUID = 1L;
/*      */   
/*   47 */   public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final BeanProperty _property;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _valueTypeIsStatic;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JavaType _keyType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JavaType _valueType;
/*      */ 
/*      */ 
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
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonSerializer<Object> _valueSerializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final TypeSerializer _valueTypeSerializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected PropertySerializerMap _dynamicValueSerializers;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Set<String> _ignoredEntries;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Set<String> _includedEntries;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Object _filterId;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Object _suppressableValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _suppressNulls;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final IgnorePropertiesUtil.Checker _inclusionChecker;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean _sortKeys;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected MapSerializer(Set<String> ignoredEntries, Set<String> includedEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer) {
/*  184 */     super(Map.class, false);
/*  185 */     this._ignoredEntries = (ignoredEntries == null || ignoredEntries.isEmpty()) ? null : ignoredEntries;
/*      */     
/*  187 */     this._includedEntries = includedEntries;
/*  188 */     this._keyType = keyType;
/*  189 */     this._valueType = valueType;
/*  190 */     this._valueTypeIsStatic = valueTypeIsStatic;
/*  191 */     this._valueTypeSerializer = vts;
/*  192 */     this._keySerializer = (JsonSerializer)keySerializer;
/*  193 */     this._valueSerializer = (JsonSerializer)valueSerializer;
/*  194 */     this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
/*  195 */     this._property = null;
/*  196 */     this._filterId = null;
/*  197 */     this._sortKeys = false;
/*  198 */     this._suppressableValue = null;
/*  199 */     this._suppressNulls = false;
/*      */     
/*  201 */     this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignoredEntries, this._includedEntries);
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
/*      */   @Deprecated
/*      */   protected MapSerializer(Set<String> ignoredEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer) {
/*  214 */     this(ignoredEntries, (Set<String>)null, keyType, valueType, valueTypeIsStatic, vts, keySerializer, valueSerializer);
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
/*      */   protected MapSerializer(MapSerializer src, BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignoredEntries, Set<String> includedEntries) {
/*  228 */     super(Map.class, false);
/*  229 */     this._ignoredEntries = (ignoredEntries == null || ignoredEntries.isEmpty()) ? null : ignoredEntries;
/*      */     
/*  231 */     this._includedEntries = includedEntries;
/*  232 */     this._keyType = src._keyType;
/*  233 */     this._valueType = src._valueType;
/*  234 */     this._valueTypeIsStatic = src._valueTypeIsStatic;
/*  235 */     this._valueTypeSerializer = src._valueTypeSerializer;
/*  236 */     this._keySerializer = (JsonSerializer)keySerializer;
/*  237 */     this._valueSerializer = (JsonSerializer)valueSerializer;
/*      */     
/*  239 */     this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
/*  240 */     this._property = property;
/*  241 */     this._filterId = src._filterId;
/*  242 */     this._sortKeys = src._sortKeys;
/*  243 */     this._suppressableValue = src._suppressableValue;
/*  244 */     this._suppressNulls = src._suppressNulls;
/*      */     
/*  246 */     this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignoredEntries, this._includedEntries);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected MapSerializer(MapSerializer src, BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignoredEntries) {
/*  257 */     this(src, property, keySerializer, valueSerializer, ignoredEntries, (Set<String>)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected MapSerializer(MapSerializer src, TypeSerializer vts, Object suppressableValue, boolean suppressNulls) {
/*  266 */     super(Map.class, false);
/*  267 */     this._ignoredEntries = src._ignoredEntries;
/*  268 */     this._includedEntries = src._includedEntries;
/*  269 */     this._keyType = src._keyType;
/*  270 */     this._valueType = src._valueType;
/*  271 */     this._valueTypeIsStatic = src._valueTypeIsStatic;
/*  272 */     this._valueTypeSerializer = vts;
/*  273 */     this._keySerializer = src._keySerializer;
/*  274 */     this._valueSerializer = src._valueSerializer;
/*      */ 
/*      */     
/*  277 */     this._dynamicValueSerializers = src._dynamicValueSerializers;
/*  278 */     this._property = src._property;
/*  279 */     this._filterId = src._filterId;
/*  280 */     this._sortKeys = src._sortKeys;
/*  281 */     this._suppressableValue = suppressableValue;
/*  282 */     this._suppressNulls = suppressNulls;
/*      */     
/*  284 */     this._inclusionChecker = src._inclusionChecker;
/*      */   }
/*      */ 
/*      */   
/*      */   protected MapSerializer(MapSerializer src, Object filterId, boolean sortKeys) {
/*  289 */     super(Map.class, false);
/*  290 */     this._ignoredEntries = src._ignoredEntries;
/*  291 */     this._includedEntries = src._includedEntries;
/*  292 */     this._keyType = src._keyType;
/*  293 */     this._valueType = src._valueType;
/*  294 */     this._valueTypeIsStatic = src._valueTypeIsStatic;
/*  295 */     this._valueTypeSerializer = src._valueTypeSerializer;
/*  296 */     this._keySerializer = src._keySerializer;
/*  297 */     this._valueSerializer = src._valueSerializer;
/*      */     
/*  299 */     this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
/*  300 */     this._property = src._property;
/*  301 */     this._filterId = filterId;
/*  302 */     this._sortKeys = sortKeys;
/*  303 */     this._suppressableValue = src._suppressableValue;
/*  304 */     this._suppressNulls = src._suppressNulls;
/*      */     
/*  306 */     this._inclusionChecker = src._inclusionChecker;
/*      */   }
/*      */ 
/*      */   
/*      */   public MapSerializer _withValueTypeSerializer(TypeSerializer vts) {
/*  311 */     if (this._valueTypeSerializer == vts) {
/*  312 */       return this;
/*      */     }
/*  314 */     _ensureOverride("_withValueTypeSerializer");
/*  315 */     return new MapSerializer(this, vts, this._suppressableValue, this._suppressNulls);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignored, Set<String> included, boolean sortKeys) {
/*  325 */     _ensureOverride("withResolved");
/*  326 */     MapSerializer ser = new MapSerializer(this, property, keySerializer, valueSerializer, ignored, included);
/*  327 */     if (sortKeys != ser._sortKeys) {
/*  328 */       ser = new MapSerializer(ser, this._filterId, sortKeys);
/*      */     }
/*  330 */     return ser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignored, boolean sortKeys) {
/*  340 */     return withResolved(property, keySerializer, valueSerializer, ignored, (Set<String>)null, sortKeys);
/*      */   }
/*      */ 
/*      */   
/*      */   public MapSerializer withFilterId(Object filterId) {
/*  345 */     if (this._filterId == filterId) {
/*  346 */       return this;
/*      */     }
/*  348 */     _ensureOverride("withFilterId");
/*  349 */     return new MapSerializer(this, filterId, this._sortKeys);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MapSerializer withContentInclusion(Object suppressableValue, boolean suppressNulls) {
/*  359 */     if (suppressableValue == this._suppressableValue && suppressNulls == this._suppressNulls) {
/*  360 */       return this;
/*      */     }
/*  362 */     _ensureOverride("withContentInclusion");
/*  363 */     return new MapSerializer(this, this._valueTypeSerializer, suppressableValue, suppressNulls);
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
/*      */   public static MapSerializer construct(Set<String> ignoredEntries, Set<String> includedEntries, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId) {
/*      */     JavaType keyType, valueType;
/*  376 */     if (mapType == null) {
/*  377 */       keyType = valueType = UNSPECIFIED_TYPE;
/*      */     } else {
/*  379 */       keyType = mapType.getKeyType();
/*  380 */       if (mapType.hasRawClass(Properties.class)) {
/*      */ 
/*      */         
/*  383 */         valueType = TypeFactory.unknownType();
/*      */       } else {
/*  385 */         valueType = mapType.getContentType();
/*      */       } 
/*      */     } 
/*      */     
/*  389 */     if (!staticValueType) {
/*  390 */       staticValueType = (valueType != null && valueType.isFinal());
/*      */     
/*      */     }
/*  393 */     else if (valueType.getRawClass() == Object.class) {
/*  394 */       staticValueType = false;
/*      */     } 
/*      */     
/*  397 */     MapSerializer ser = new MapSerializer(ignoredEntries, includedEntries, keyType, valueType, staticValueType, vts, keySerializer, valueSerializer);
/*      */     
/*  399 */     if (filterId != null) {
/*  400 */       ser = ser.withFilterId(filterId);
/*      */     }
/*  402 */     return ser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static MapSerializer construct(Set<String> ignoredEntries, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId) {
/*  413 */     return construct(ignoredEntries, (Set<String>)null, mapType, staticValueType, vts, keySerializer, valueSerializer, filterId);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _ensureOverride(String method) {
/*  420 */     ClassUtil.verifyMustOverride(MapSerializer.class, this, method);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected void _ensureOverride() {
/*  428 */     _ensureOverride("N/A");
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
/*      */   @Deprecated
/*      */   protected MapSerializer(MapSerializer src, TypeSerializer vts, Object suppressableValue) {
/*  445 */     this(src, vts, suppressableValue, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public MapSerializer withContentInclusion(Object suppressableValue) {
/*  453 */     return new MapSerializer(this, this._valueTypeSerializer, suppressableValue, this._suppressNulls);
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
/*      */   public static MapSerializer construct(String[] ignoredList, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId) {
/*  467 */     Set<String> ignoredEntries = ArrayBuilders.arrayToSet((Object[])ignoredList);
/*  468 */     return construct(ignoredEntries, mapType, staticValueType, vts, keySerializer, valueSerializer, filterId);
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
/*      */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/*  483 */     JsonSerializer<?> ser = null;
/*  484 */     JsonSerializer<?> keySer = null;
/*  485 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/*  486 */     AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();
/*      */ 
/*      */     
/*  489 */     if (_neitherNull(propertyAcc, intr)) {
/*  490 */       Object serDef = intr.findKeySerializer((Annotated)propertyAcc);
/*  491 */       if (serDef != null) {
/*  492 */         keySer = provider.serializerInstance((Annotated)propertyAcc, serDef);
/*      */       }
/*  494 */       serDef = intr.findContentSerializer((Annotated)propertyAcc);
/*  495 */       if (serDef != null) {
/*  496 */         ser = provider.serializerInstance((Annotated)propertyAcc, serDef);
/*      */       }
/*      */     } 
/*  499 */     if (ser == null) {
/*  500 */       ser = this._valueSerializer;
/*      */     }
/*      */     
/*  503 */     ser = findContextualConvertingSerializer(provider, property, ser);
/*  504 */     if (ser == null)
/*      */     {
/*      */ 
/*      */       
/*  508 */       if (this._valueTypeIsStatic && !this._valueType.isJavaLangObject()) {
/*  509 */         ser = provider.findContentValueSerializer(this._valueType, property);
/*      */       }
/*      */     }
/*  512 */     if (keySer == null) {
/*  513 */       keySer = this._keySerializer;
/*      */     }
/*  515 */     if (keySer == null) {
/*  516 */       keySer = provider.findKeySerializer(this._keyType, property);
/*      */     } else {
/*  518 */       keySer = provider.handleSecondaryContextualization(keySer, property);
/*      */     } 
/*  520 */     Set<String> ignored = this._ignoredEntries;
/*  521 */     Set<String> included = this._includedEntries;
/*  522 */     boolean sortKeys = false;
/*  523 */     if (_neitherNull(propertyAcc, intr)) {
/*  524 */       SerializationConfig config = provider.getConfig();
/*      */       
/*  526 */       Set<String> newIgnored = intr.findPropertyIgnoralByName((MapperConfig)config, (Annotated)propertyAcc).findIgnoredForSerialization();
/*  527 */       if (_nonEmpty(newIgnored)) {
/*  528 */         ignored = (ignored == null) ? new HashSet<>() : new HashSet<>(ignored);
/*  529 */         for (String str : newIgnored) {
/*  530 */           ignored.add(str);
/*      */         }
/*      */       } 
/*      */       
/*  534 */       Set<String> newIncluded = intr.findPropertyInclusionByName((MapperConfig)config, (Annotated)propertyAcc).getIncluded();
/*  535 */       if (newIncluded != null) {
/*  536 */         included = (included == null) ? new HashSet<>() : new HashSet<>(included);
/*  537 */         for (String str : newIncluded) {
/*  538 */           included.add(str);
/*      */         }
/*      */       } 
/*      */       
/*  542 */       Boolean b = intr.findSerializationSortAlphabetically((Annotated)propertyAcc);
/*  543 */       sortKeys = Boolean.TRUE.equals(b);
/*      */     } 
/*  545 */     JsonFormat.Value format = findFormatOverrides(provider, property, Map.class);
/*  546 */     if (format != null) {
/*  547 */       Boolean B = format.getFeature(JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES);
/*  548 */       if (B != null) {
/*  549 */         sortKeys = B.booleanValue();
/*      */       }
/*      */     } 
/*  552 */     MapSerializer mser = withResolved(property, keySer, ser, ignored, included, sortKeys);
/*      */ 
/*      */     
/*  555 */     if (propertyAcc != null) {
/*  556 */       Object filterId = intr.findFilterId((Annotated)propertyAcc);
/*  557 */       if (filterId != null) {
/*  558 */         mser = mser.withFilterId(filterId);
/*      */       }
/*      */     } 
/*  561 */     JsonInclude.Value inclV = findIncludeOverrides(provider, property, Map.class);
/*  562 */     if (inclV != null) {
/*  563 */       JsonInclude.Include incl = inclV.getContentInclusion();
/*      */       
/*  565 */       if (incl != JsonInclude.Include.USE_DEFAULTS) {
/*      */         Object valueToSuppress;
/*      */         boolean suppressNulls;
/*  568 */         switch (incl) {
/*      */           case NON_DEFAULT:
/*  570 */             valueToSuppress = BeanUtil.getDefaultValue(this._valueType);
/*  571 */             suppressNulls = true;
/*  572 */             if (valueToSuppress != null && 
/*  573 */               valueToSuppress.getClass().isArray()) {
/*  574 */               valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
/*      */             }
/*      */             break;
/*      */           
/*      */           case NON_ABSENT:
/*  579 */             suppressNulls = true;
/*  580 */             valueToSuppress = this._valueType.isReferenceType() ? MARKER_FOR_EMPTY : null;
/*      */             break;
/*      */           case NON_EMPTY:
/*  583 */             suppressNulls = true;
/*  584 */             valueToSuppress = MARKER_FOR_EMPTY;
/*      */             break;
/*      */           case CUSTOM:
/*  587 */             valueToSuppress = provider.includeFilterInstance(null, inclV.getContentFilter());
/*  588 */             if (valueToSuppress == null) {
/*  589 */               suppressNulls = true; break;
/*      */             } 
/*  591 */             suppressNulls = provider.includeFilterSuppressNulls(valueToSuppress);
/*      */             break;
/*      */           
/*      */           case NON_NULL:
/*  595 */             valueToSuppress = null;
/*  596 */             suppressNulls = true;
/*      */             break;
/*      */           
/*      */           default:
/*  600 */             valueToSuppress = null;
/*      */ 
/*      */             
/*  603 */             suppressNulls = false;
/*      */             break;
/*      */         } 
/*  606 */         mser = mser.withContentInclusion(valueToSuppress, suppressNulls);
/*      */       } 
/*      */     } 
/*  609 */     return (JsonSerializer<?>)mser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType getContentType() {
/*  620 */     return this._valueType;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonSerializer<?> getContentSerializer() {
/*  625 */     return this._valueSerializer;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEmpty(SerializerProvider prov, Map<?, ?> value) {
/*  631 */     if (value.isEmpty()) {
/*  632 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  637 */     Object supp = this._suppressableValue;
/*  638 */     if (supp == null && !this._suppressNulls) {
/*  639 */       return false;
/*      */     }
/*  641 */     JsonSerializer<Object> valueSer = this._valueSerializer;
/*  642 */     boolean checkEmpty = (MARKER_FOR_EMPTY == supp);
/*  643 */     if (valueSer != null) {
/*  644 */       for (Object elemValue : value.values()) {
/*  645 */         if (elemValue == null) {
/*  646 */           if (this._suppressNulls) {
/*      */             continue;
/*      */           }
/*  649 */           return false;
/*      */         } 
/*  651 */         if (checkEmpty) {
/*  652 */           if (!valueSer.isEmpty(prov, elemValue))
/*  653 */             return false;  continue;
/*      */         } 
/*  655 */         if (supp == null || !supp.equals(value)) {
/*  656 */           return false;
/*      */         }
/*      */       } 
/*  659 */       return true;
/*      */     } 
/*      */     
/*  662 */     for (Object elemValue : value.values()) {
/*  663 */       if (elemValue == null) {
/*  664 */         if (this._suppressNulls) {
/*      */           continue;
/*      */         }
/*  667 */         return false;
/*      */       } 
/*      */       try {
/*  670 */         valueSer = _findSerializer(prov, elemValue);
/*  671 */       } catch (DatabindException e) {
/*      */         
/*  673 */         return false;
/*      */       } 
/*  675 */       if (checkEmpty) {
/*  676 */         if (!valueSer.isEmpty(prov, elemValue))
/*  677 */           return false;  continue;
/*      */       } 
/*  679 */       if (supp == null || !supp.equals(value)) {
/*  680 */         return false;
/*      */       }
/*      */     } 
/*  683 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasSingleElement(Map<?, ?> value) {
/*  688 */     return (value.size() == 1);
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
/*      */   public JsonSerializer<?> getKeySerializer() {
/*  706 */     return this._keySerializer;
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
/*      */   public void serialize(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*  719 */     gen.writeStartObject(value);
/*  720 */     serializeWithoutTypeInfo(value, gen, provider);
/*  721 */     gen.writeEndObject();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serializeWithType(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/*  730 */     gen.setCurrentValue(value);
/*  731 */     WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer
/*  732 */         .typeId(value, JsonToken.START_OBJECT));
/*  733 */     serializeWithoutTypeInfo(value, gen, provider);
/*  734 */     typeSer.writeTypeSuffix(gen, typeIdDef);
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
/*      */   public void serializeWithoutTypeInfo(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*  752 */     if (!value.isEmpty()) {
/*  753 */       if (this._sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
/*  754 */         value = _orderEntries(value, gen, provider);
/*      */       }
/*      */       PropertyFilter pf;
/*  757 */       if (this._filterId != null && (pf = findPropertyFilter(provider, this._filterId, value)) != null) {
/*  758 */         serializeFilteredFields(value, gen, provider, pf, this._suppressableValue);
/*  759 */       } else if (this._suppressableValue != null || this._suppressNulls) {
/*  760 */         serializeOptionalFields(value, gen, provider, this._suppressableValue);
/*  761 */       } else if (this._valueSerializer != null) {
/*  762 */         serializeFieldsUsing(value, gen, provider, this._valueSerializer);
/*      */       } else {
/*  764 */         serializeFields(value, gen, provider);
/*      */       } 
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
/*      */   public void serializeFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*  778 */     if (this._valueTypeSerializer != null) {
/*  779 */       serializeTypedFields(value, gen, provider, (Object)null);
/*      */       return;
/*      */     } 
/*  782 */     JsonSerializer<Object> keySerializer = this._keySerializer;
/*  783 */     Object keyElem = null;
/*      */     
/*      */     try {
/*  786 */       for (Map.Entry<?, ?> entry : value.entrySet()) {
/*  787 */         Object valueElem = entry.getValue();
/*      */         
/*  789 */         keyElem = entry.getKey();
/*  790 */         if (keyElem == null) {
/*  791 */           provider.findNullKeySerializer(this._keyType, this._property).serialize(null, gen, provider);
/*      */         } else {
/*      */           
/*  794 */           if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyElem)) {
/*      */             continue;
/*      */           }
/*  797 */           keySerializer.serialize(keyElem, gen, provider);
/*      */         } 
/*      */         
/*  800 */         if (valueElem == null) {
/*  801 */           provider.defaultSerializeNull(gen);
/*      */           continue;
/*      */         } 
/*  804 */         JsonSerializer<Object> serializer = this._valueSerializer;
/*  805 */         if (serializer == null) {
/*  806 */           serializer = _findSerializer(provider, valueElem);
/*      */         }
/*  808 */         serializer.serialize(valueElem, gen, provider);
/*      */       } 
/*  810 */     } catch (Exception e) {
/*  811 */       wrapAndThrow(provider, e, value, String.valueOf(keyElem));
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
/*      */   public void serializeOptionalFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, Object suppressableValue) throws IOException {
/*  823 */     if (this._valueTypeSerializer != null) {
/*  824 */       serializeTypedFields(value, gen, provider, suppressableValue);
/*      */       return;
/*      */     } 
/*  827 */     boolean checkEmpty = (MARKER_FOR_EMPTY == suppressableValue);
/*      */     
/*  829 */     for (Map.Entry<?, ?> entry : value.entrySet()) {
/*      */       JsonSerializer<Object> keySerializer, valueSer;
/*  831 */       Object keyElem = entry.getKey();
/*      */       
/*  833 */       if (keyElem == null) {
/*  834 */         keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
/*      */       } else {
/*  836 */         if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyElem)) {
/*      */           continue;
/*      */         }
/*  839 */         keySerializer = this._keySerializer;
/*      */       } 
/*      */ 
/*      */       
/*  843 */       Object valueElem = entry.getValue();
/*      */       
/*  845 */       if (valueElem == null) {
/*  846 */         if (this._suppressNulls) {
/*      */           continue;
/*      */         }
/*  849 */         valueSer = provider.getDefaultNullValueSerializer();
/*      */       } else {
/*  851 */         valueSer = this._valueSerializer;
/*  852 */         if (valueSer == null) {
/*  853 */           valueSer = _findSerializer(provider, valueElem);
/*      */         }
/*      */         
/*  856 */         if (checkEmpty ? 
/*  857 */           valueSer.isEmpty(provider, valueElem) : (
/*      */ 
/*      */           
/*  860 */           suppressableValue != null && 
/*  861 */           suppressableValue.equals(valueElem))) {
/*      */           continue;
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/*      */       try {
/*  868 */         keySerializer.serialize(keyElem, gen, provider);
/*  869 */         valueSer.serialize(valueElem, gen, provider);
/*  870 */       } catch (Exception e) {
/*  871 */         wrapAndThrow(provider, e, value, String.valueOf(keyElem));
/*      */       } 
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
/*      */   public void serializeFieldsUsing(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException {
/*  885 */     JsonSerializer<Object> keySerializer = this._keySerializer;
/*  886 */     TypeSerializer typeSer = this._valueTypeSerializer;
/*      */     
/*  888 */     for (Map.Entry<?, ?> entry : value.entrySet()) {
/*  889 */       Object keyElem = entry.getKey();
/*  890 */       if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyElem)) {
/*      */         continue;
/*      */       }
/*      */       
/*  894 */       if (keyElem == null) {
/*  895 */         provider.findNullKeySerializer(this._keyType, this._property).serialize(null, gen, provider);
/*      */       } else {
/*  897 */         keySerializer.serialize(keyElem, gen, provider);
/*      */       } 
/*  899 */       Object valueElem = entry.getValue();
/*  900 */       if (valueElem == null) {
/*  901 */         provider.defaultSerializeNull(gen); continue;
/*      */       } 
/*      */       try {
/*  904 */         if (typeSer == null) {
/*  905 */           ser.serialize(valueElem, gen, provider); continue;
/*      */         } 
/*  907 */         ser.serializeWithType(valueElem, gen, provider, typeSer);
/*      */       }
/*  909 */       catch (Exception e) {
/*  910 */         wrapAndThrow(provider, e, value, String.valueOf(keyElem));
/*      */       } 
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
/*      */   public void serializeFilteredFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, PropertyFilter filter, Object suppressableValue) throws IOException {
/*  927 */     MapProperty prop = new MapProperty(this._valueTypeSerializer, this._property);
/*  928 */     boolean checkEmpty = (MARKER_FOR_EMPTY == suppressableValue);
/*      */     
/*  930 */     for (Map.Entry<?, ?> entry : value.entrySet()) {
/*      */       JsonSerializer<Object> keySerializer, valueSer;
/*  932 */       Object keyElem = entry.getKey();
/*  933 */       if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyElem)) {
/*      */         continue;
/*      */       }
/*      */ 
/*      */       
/*  938 */       if (keyElem == null) {
/*  939 */         keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
/*      */       } else {
/*  941 */         keySerializer = this._keySerializer;
/*      */       } 
/*      */       
/*  944 */       Object valueElem = entry.getValue();
/*      */ 
/*      */ 
/*      */       
/*  948 */       if (valueElem == null) {
/*  949 */         if (this._suppressNulls) {
/*      */           continue;
/*      */         }
/*  952 */         valueSer = provider.getDefaultNullValueSerializer();
/*      */       } else {
/*  954 */         valueSer = this._valueSerializer;
/*  955 */         if (valueSer == null) {
/*  956 */           valueSer = _findSerializer(provider, valueElem);
/*      */         }
/*      */         
/*  959 */         if (checkEmpty ? 
/*  960 */           valueSer.isEmpty(provider, valueElem) : (
/*      */ 
/*      */           
/*  963 */           suppressableValue != null && 
/*  964 */           suppressableValue.equals(valueElem))) {
/*      */           continue;
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/*  970 */       prop.reset(keyElem, valueElem, keySerializer, valueSer);
/*      */       try {
/*  972 */         filter.serializeAsField(value, gen, provider, prop);
/*  973 */       } catch (Exception e) {
/*  974 */         wrapAndThrow(provider, e, value, String.valueOf(keyElem));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serializeTypedFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, Object suppressableValue) throws IOException {
/*  986 */     boolean checkEmpty = (MARKER_FOR_EMPTY == suppressableValue);
/*      */     
/*  988 */     for (Map.Entry<?, ?> entry : value.entrySet()) {
/*  989 */       JsonSerializer<Object> keySerializer, valueSer; Object keyElem = entry.getKey();
/*      */       
/*  991 */       if (keyElem == null) {
/*  992 */         keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
/*      */       } else {
/*      */         
/*  995 */         if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyElem)) {
/*      */           continue;
/*      */         }
/*  998 */         keySerializer = this._keySerializer;
/*      */       } 
/* 1000 */       Object valueElem = entry.getValue();
/*      */ 
/*      */ 
/*      */       
/* 1004 */       if (valueElem == null) {
/* 1005 */         if (this._suppressNulls) {
/*      */           continue;
/*      */         }
/* 1008 */         valueSer = provider.getDefaultNullValueSerializer();
/*      */       } else {
/* 1010 */         valueSer = this._valueSerializer;
/* 1011 */         if (valueSer == null) {
/* 1012 */           valueSer = _findSerializer(provider, valueElem);
/*      */         }
/*      */         
/* 1015 */         if (checkEmpty ? 
/* 1016 */           valueSer.isEmpty(provider, valueElem) : (
/*      */ 
/*      */           
/* 1019 */           suppressableValue != null && 
/* 1020 */           suppressableValue.equals(valueElem))) {
/*      */           continue;
/*      */         }
/*      */       } 
/*      */       
/* 1025 */       keySerializer.serialize(keyElem, gen, provider);
/*      */       try {
/* 1027 */         valueSer.serializeWithType(valueElem, gen, provider, this._valueTypeSerializer);
/* 1028 */       } catch (Exception e) {
/* 1029 */         wrapAndThrow(provider, e, value, String.valueOf(keyElem));
/*      */       } 
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
/*      */   public void serializeFilteredAnyProperties(SerializerProvider provider, JsonGenerator gen, Object bean, Map<?, ?> value, PropertyFilter filter, Object suppressableValue) throws IOException {
/* 1047 */     MapProperty prop = new MapProperty(this._valueTypeSerializer, this._property);
/* 1048 */     boolean checkEmpty = (MARKER_FOR_EMPTY == suppressableValue);
/*      */     
/* 1050 */     for (Map.Entry<?, ?> entry : value.entrySet()) {
/*      */       JsonSerializer<Object> keySerializer, valueSer;
/* 1052 */       Object keyElem = entry.getKey();
/* 1053 */       if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyElem)) {
/*      */         continue;
/*      */       }
/*      */ 
/*      */       
/* 1058 */       if (keyElem == null) {
/* 1059 */         keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
/*      */       } else {
/* 1061 */         keySerializer = this._keySerializer;
/*      */       } 
/*      */       
/* 1064 */       Object valueElem = entry.getValue();
/*      */ 
/*      */ 
/*      */       
/* 1068 */       if (valueElem == null) {
/* 1069 */         if (this._suppressNulls) {
/*      */           continue;
/*      */         }
/* 1072 */         valueSer = provider.getDefaultNullValueSerializer();
/*      */       } else {
/* 1074 */         valueSer = this._valueSerializer;
/* 1075 */         if (valueSer == null) {
/* 1076 */           valueSer = _findSerializer(provider, valueElem);
/*      */         }
/*      */         
/* 1079 */         if (checkEmpty ? 
/* 1080 */           valueSer.isEmpty(provider, valueElem) : (
/*      */ 
/*      */           
/* 1083 */           suppressableValue != null && 
/* 1084 */           suppressableValue.equals(valueElem))) {
/*      */           continue;
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 1090 */       prop.reset(keyElem, valueElem, keySerializer, valueSer);
/*      */       try {
/* 1092 */         filter.serializeAsField(bean, gen, provider, prop);
/* 1093 */       } catch (Exception e) {
/* 1094 */         wrapAndThrow(provider, e, value, String.valueOf(keyElem));
/*      */       } 
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
/*      */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/* 1110 */     return (JsonNode)createSchemaNode("object", true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 1117 */     JsonMapFormatVisitor v2 = visitor.expectMapFormat(typeHint);
/* 1118 */     if (v2 != null) {
/* 1119 */       v2.keyFormat((JsonFormatVisitable)this._keySerializer, this._keyType);
/* 1120 */       JsonSerializer<?> valueSer = this._valueSerializer;
/* 1121 */       if (valueSer == null) {
/* 1122 */         valueSer = _findAndAddDynamic(this._dynamicValueSerializers, this._valueType, visitor
/* 1123 */             .getProvider());
/*      */       }
/* 1125 */       v2.valueFormat((JsonFormatVisitable)valueSer, this._valueType);
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
/*      */   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
/* 1139 */     PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
/*      */     
/* 1141 */     if (map != result.map) {
/* 1142 */       this._dynamicValueSerializers = result.map;
/*      */     }
/* 1144 */     return result.serializer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider) throws JsonMappingException {
/* 1151 */     PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
/* 1152 */     if (map != result.map) {
/* 1153 */       this._dynamicValueSerializers = result.map;
/*      */     }
/* 1155 */     return result.serializer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Map<?, ?> _orderEntries(Map<?, ?> input, JsonGenerator gen, SerializerProvider provider) throws IOException {
/* 1163 */     if (input instanceof java.util.SortedMap) {
/* 1164 */       return input;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1170 */     if (_hasNullKey(input)) {
/* 1171 */       TreeMap<Object, Object> result = new TreeMap<>();
/* 1172 */       for (Map.Entry<?, ?> entry : input.entrySet()) {
/* 1173 */         Object key = entry.getKey();
/* 1174 */         if (key == null) {
/* 1175 */           _writeNullKeyedEntry(gen, provider, entry.getValue());
/*      */           continue;
/*      */         } 
/* 1178 */         result.put(key, entry.getValue());
/*      */       } 
/* 1180 */       return result;
/*      */     } 
/* 1182 */     return new TreeMap<>(input);
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
/*      */   protected boolean _hasNullKey(Map<?, ?> input) {
/* 1198 */     return (input instanceof java.util.HashMap && input.containsKey(null));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _writeNullKeyedEntry(JsonGenerator gen, SerializerProvider provider, Object value) throws IOException {
/* 1205 */     JsonSerializer<Object> valueSer, keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
/*      */     
/* 1207 */     if (value == null) {
/* 1208 */       if (this._suppressNulls) {
/*      */         return;
/*      */       }
/* 1211 */       valueSer = provider.getDefaultNullValueSerializer();
/*      */     } else {
/* 1213 */       valueSer = this._valueSerializer;
/* 1214 */       if (valueSer == null) {
/* 1215 */         valueSer = _findSerializer(provider, value);
/*      */       }
/* 1217 */       if (this._suppressableValue == MARKER_FOR_EMPTY) {
/* 1218 */         if (valueSer.isEmpty(provider, value)) {
/*      */           return;
/*      */         }
/* 1221 */       } else if (this._suppressableValue != null && this._suppressableValue
/* 1222 */         .equals(value)) {
/*      */         return;
/*      */       } 
/*      */     } 
/*      */     
/*      */     try {
/* 1228 */       keySerializer.serialize(null, gen, provider);
/* 1229 */       valueSer.serialize(value, gen, provider);
/* 1230 */     } catch (Exception e) {
/* 1231 */       wrapAndThrow(provider, e, value, "");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, Object value) throws JsonMappingException {
/* 1239 */     Class<?> cc = value.getClass();
/* 1240 */     JsonSerializer<Object> valueSer = this._dynamicValueSerializers.serializerFor(cc);
/* 1241 */     if (valueSer != null) {
/* 1242 */       return valueSer;
/*      */     }
/* 1244 */     if (this._valueType.hasGenericTypes()) {
/* 1245 */       return _findAndAddDynamic(this._dynamicValueSerializers, provider
/* 1246 */           .constructSpecializedType(this._valueType, cc), provider);
/*      */     }
/* 1248 */     return _findAndAddDynamic(this._dynamicValueSerializers, cc, provider);
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/MapSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */