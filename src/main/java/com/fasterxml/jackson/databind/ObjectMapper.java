/*      */ package com.fasterxml.jackson.databind;
/*      */ 
/*      */ import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*      */ import com.fasterxml.jackson.annotation.JsonInclude;
/*      */ import com.fasterxml.jackson.annotation.JsonSetter;
/*      */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*      */ import com.fasterxml.jackson.annotation.PropertyAccessor;
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.Base64Variants;
/*      */ import com.fasterxml.jackson.core.FormatSchema;
/*      */ import com.fasterxml.jackson.core.JsonEncoding;
/*      */ import com.fasterxml.jackson.core.JsonFactory;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonProcessingException;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.PrettyPrinter;
/*      */ import com.fasterxml.jackson.core.StreamReadFeature;
/*      */ import com.fasterxml.jackson.core.StreamWriteFeature;
/*      */ import com.fasterxml.jackson.core.TreeNode;
/*      */ import com.fasterxml.jackson.core.Version;
/*      */ import com.fasterxml.jackson.core.Versioned;
/*      */ import com.fasterxml.jackson.core.exc.StreamReadException;
/*      */ import com.fasterxml.jackson.core.exc.StreamWriteException;
/*      */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*      */ import com.fasterxml.jackson.core.io.SegmentedStringWriter;
/*      */ import com.fasterxml.jackson.core.type.ResolvedType;
/*      */ import com.fasterxml.jackson.core.type.TypeReference;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import com.fasterxml.jackson.databind.cfg.BaseSettings;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionConfigs;
/*      */ import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
/*      */ import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
/*      */ import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*      */ import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.cfg.MutableCoercionConfig;
/*      */ import com.fasterxml.jackson.databind.cfg.MutableConfigOverride;
/*      */ import com.fasterxml.jackson.databind.cfg.PackageVersion;
/*      */ import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
/*      */ import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
/*      */ import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
/*      */ import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*      */ import com.fasterxml.jackson.databind.deser.DeserializerFactory;
/*      */ import com.fasterxml.jackson.databind.deser.Deserializers;
/*      */ import com.fasterxml.jackson.databind.deser.KeyDeserializers;
/*      */ import com.fasterxml.jackson.databind.deser.ValueInstantiators;
/*      */ import com.fasterxml.jackson.databind.exc.MismatchedInputException;
/*      */ import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
/*      */ import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
/*      */ import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
/*      */ import com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy;
/*      */ import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
/*      */ import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
/*      */ import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*      */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*      */ import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
/*      */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*      */ import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
/*      */ import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*      */ import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
/*      */ import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
/*      */ import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
/*      */ import com.fasterxml.jackson.databind.node.ArrayNode;
/*      */ import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*      */ import com.fasterxml.jackson.databind.node.NullNode;
/*      */ import com.fasterxml.jackson.databind.node.ObjectNode;
/*      */ import com.fasterxml.jackson.databind.node.POJONode;
/*      */ import com.fasterxml.jackson.databind.node.TreeTraversingParser;
/*      */ import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
/*      */ import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
/*      */ import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
/*      */ import com.fasterxml.jackson.databind.ser.FilterProvider;
/*      */ import com.fasterxml.jackson.databind.ser.SerializerFactory;
/*      */ import com.fasterxml.jackson.databind.ser.Serializers;
/*      */ import com.fasterxml.jackson.databind.type.LogicalType;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import com.fasterxml.jackson.databind.type.TypeModifier;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import com.fasterxml.jackson.databind.util.RootNameLookup;
/*      */ import com.fasterxml.jackson.databind.util.StdDateFormat;
/*      */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*      */ import java.io.Closeable;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataOutput;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.Type;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.text.DateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.ServiceLoader;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ObjectMapper
/*      */   extends ObjectCodec
/*      */   implements Versioned, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 2L;
/*      */   
/*      */   public enum DefaultTyping
/*      */   {
/*  159 */     JAVA_LANG_OBJECT,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  169 */     OBJECT_AND_NON_CONCRETE,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  178 */     NON_CONCRETE_AND_ARRAYS,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  189 */     NON_FINAL,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  214 */     EVERYTHING;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class DefaultTypeResolverBuilder
/*      */     extends StdTypeResolverBuilder
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final ObjectMapper.DefaultTyping _appliesFor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final PolymorphicTypeValidator _subtypeValidator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping t) {
/*  252 */       this(t, (PolymorphicTypeValidator)LaissezFaireSubTypeValidator.instance);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv) {
/*  259 */       this._appliesFor = _requireNonNull(t, "Can not pass `null` DefaultTyping");
/*  260 */       this._subtypeValidator = _requireNonNull(ptv, "Can not pass `null` PolymorphicTypeValidator");
/*      */     }
/*      */ 
/*      */     
/*      */     protected DefaultTypeResolverBuilder(DefaultTypeResolverBuilder base, Class<?> defaultImpl) {
/*  265 */       super(base, defaultImpl);
/*  266 */       this._appliesFor = base._appliesFor;
/*  267 */       this._subtypeValidator = base._subtypeValidator;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private static <T> T _requireNonNull(T value, String msg) {
/*  273 */       if (value == null) {
/*  274 */         throw new NullPointerException(msg);
/*      */       }
/*  276 */       return value;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static DefaultTypeResolverBuilder construct(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv) {
/*  284 */       return new DefaultTypeResolverBuilder(t, ptv);
/*      */     }
/*      */ 
/*      */     
/*      */     public DefaultTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
/*  289 */       if (this._defaultImpl == defaultImpl) {
/*  290 */         return this;
/*      */       }
/*  292 */       ClassUtil.verifyMustOverride(DefaultTypeResolverBuilder.class, this, "withDefaultImpl");
/*  293 */       return new DefaultTypeResolverBuilder(this, defaultImpl);
/*      */     }
/*      */ 
/*      */     
/*      */     public PolymorphicTypeValidator subTypeValidator(MapperConfig<?> config) {
/*  298 */       return this._subtypeValidator;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
/*  305 */       return useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
/*  312 */       return useForType(baseType) ? super.buildTypeSerializer(config, baseType, subtypes) : null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean useForType(JavaType t) {
/*  327 */       if (t.isPrimitive()) {
/*  328 */         return false;
/*      */       }
/*      */       
/*  331 */       switch (this._appliesFor) {
/*      */         case NON_CONCRETE_AND_ARRAYS:
/*  333 */           while (t.isArrayType()) {
/*  334 */             t = t.getContentType();
/*      */           }
/*      */ 
/*      */         
/*      */         case OBJECT_AND_NON_CONCRETE:
/*  339 */           while (t.isReferenceType()) {
/*  340 */             t = t.getReferencedType();
/*      */           }
/*  342 */           return (t.isJavaLangObject() || (
/*  343 */             !t.isConcrete() && 
/*      */             
/*  345 */             !TreeNode.class.isAssignableFrom(t.getRawClass())));
/*      */         
/*      */         case NON_FINAL:
/*  348 */           while (t.isArrayType()) {
/*  349 */             t = t.getContentType();
/*      */           }
/*      */           
/*  352 */           while (t.isReferenceType()) {
/*  353 */             t = t.getReferencedType();
/*      */           }
/*      */           
/*  356 */           return (!t.isFinal() && !TreeNode.class.isAssignableFrom(t.getRawClass()));
/*      */ 
/*      */         
/*      */         case EVERYTHING:
/*  360 */           return true;
/*      */       } 
/*      */       
/*  363 */       return t.isJavaLangObject();
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
/*  375 */   protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR = (AnnotationIntrospector)new JacksonAnnotationIntrospector();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  381 */   protected static final BaseSettings DEFAULT_BASE = new BaseSettings(null, DEFAULT_ANNOTATION_INTROSPECTOR, null, 
/*      */ 
/*      */       
/*  384 */       TypeFactory.defaultInstance(), null, (DateFormat)StdDateFormat.instance, null, 
/*      */       
/*  386 */       Locale.getDefault(), null, 
/*      */       
/*  388 */       Base64Variants.getDefaultVariant(), (PolymorphicTypeValidator)LaissezFaireSubTypeValidator.instance, (AccessorNamingStrategy.Provider)new DefaultAccessorNamingStrategy.Provider());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonFactory _jsonFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected TypeFactory _typeFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected InjectableValues _injectableValues;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SubtypeResolver _subtypeResolver;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final ConfigOverrides _configOverrides;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final CoercionConfigs _coercionConfigs;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SimpleMixInResolver _mixIns;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SerializationConfig _serializationConfig;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DefaultSerializerProvider _serializerProvider;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SerializerFactory _serializerFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DeserializationConfig _deserializationConfig;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DefaultDeserializationContext _deserializationContext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Set<Object> _registeredModuleTypes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  554 */   protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers = new ConcurrentHashMap<>(64, 0.6F, 2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper() {
/*  576 */     this(null, null, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper(JsonFactory jf) {
/*  585 */     this(jf, null, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectMapper(ObjectMapper src) {
/*  595 */     this._jsonFactory = src._jsonFactory.copy();
/*  596 */     this._jsonFactory.setCodec(this);
/*  597 */     this._subtypeResolver = src._subtypeResolver.copy();
/*  598 */     this._typeFactory = src._typeFactory;
/*  599 */     this._injectableValues = src._injectableValues;
/*  600 */     this._configOverrides = src._configOverrides.copy();
/*  601 */     this._coercionConfigs = src._coercionConfigs.copy();
/*  602 */     this._mixIns = src._mixIns.copy();
/*      */     
/*  604 */     RootNameLookup rootNames = new RootNameLookup();
/*  605 */     this._serializationConfig = new SerializationConfig(src._serializationConfig, this._subtypeResolver, this._mixIns, rootNames, this._configOverrides);
/*      */     
/*  607 */     this._deserializationConfig = new DeserializationConfig(src._deserializationConfig, this._subtypeResolver, this._mixIns, rootNames, this._configOverrides, this._coercionConfigs);
/*      */ 
/*      */     
/*  610 */     this._serializerProvider = src._serializerProvider.copy();
/*  611 */     this._deserializationContext = src._deserializationContext.copy();
/*      */ 
/*      */     
/*  614 */     this._serializerFactory = src._serializerFactory;
/*      */ 
/*      */     
/*  617 */     Set<Object> reg = src._registeredModuleTypes;
/*  618 */     if (reg == null) {
/*  619 */       this._registeredModuleTypes = null;
/*      */     } else {
/*  621 */       this._registeredModuleTypes = new LinkedHashSet(reg);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc) {
/*  642 */     if (jf == null) {
/*  643 */       this._jsonFactory = new MappingJsonFactory(this);
/*      */     } else {
/*  645 */       this._jsonFactory = jf;
/*  646 */       if (jf.getCodec() == null) {
/*  647 */         this._jsonFactory.setCodec(this);
/*      */       }
/*      */     } 
/*  650 */     this._subtypeResolver = (SubtypeResolver)new StdSubtypeResolver();
/*  651 */     RootNameLookup rootNames = new RootNameLookup();
/*      */     
/*  653 */     this._typeFactory = TypeFactory.defaultInstance();
/*      */     
/*  655 */     SimpleMixInResolver mixins = new SimpleMixInResolver(null);
/*  656 */     this._mixIns = mixins;
/*  657 */     BaseSettings base = DEFAULT_BASE.withClassIntrospector(defaultClassIntrospector());
/*  658 */     this._configOverrides = new ConfigOverrides();
/*  659 */     this._coercionConfigs = new CoercionConfigs();
/*  660 */     this._serializationConfig = new SerializationConfig(base, this._subtypeResolver, mixins, rootNames, this._configOverrides);
/*      */     
/*  662 */     this._deserializationConfig = new DeserializationConfig(base, this._subtypeResolver, mixins, rootNames, this._configOverrides, this._coercionConfigs);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  667 */     boolean needOrder = this._jsonFactory.requiresPropertyOrdering();
/*  668 */     if (needOrder ^ this._serializationConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)) {
/*  669 */       configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, needOrder);
/*      */     }
/*      */     
/*  672 */     this._serializerProvider = (sp == null) ? (DefaultSerializerProvider)new DefaultSerializerProvider.Impl() : sp;
/*  673 */     this._deserializationContext = (dc == null) ? (DefaultDeserializationContext)new DefaultDeserializationContext.Impl((DeserializerFactory)BeanDeserializerFactory.instance) : dc;
/*      */ 
/*      */ 
/*      */     
/*  677 */     this._serializerFactory = (SerializerFactory)BeanSerializerFactory.instance;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ClassIntrospector defaultClassIntrospector() {
/*  687 */     return (ClassIntrospector)new BasicClassIntrospector();
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
/*      */   public ObjectMapper copy() {
/*  712 */     _checkInvalidCopy(ObjectMapper.class);
/*  713 */     return new ObjectMapper(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _checkInvalidCopy(Class<?> exp) {
/*  721 */     if (getClass() != exp)
/*      */     {
/*  723 */       throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + 
/*  724 */           version() + ") does not override copy(); it has to");
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
/*      */   protected ObjectReader _newReader(DeserializationConfig config) {
/*  742 */     return new ObjectReader(this, config);
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
/*      */   protected ObjectReader _newReader(DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues) {
/*  754 */     return new ObjectReader(this, config, valueType, valueToUpdate, schema, injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter _newWriter(SerializationConfig config) {
/*  764 */     return new ObjectWriter(this, config);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter _newWriter(SerializationConfig config, FormatSchema schema) {
/*  774 */     return new ObjectWriter(this, config, schema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter _newWriter(SerializationConfig config, JavaType rootType, PrettyPrinter pp) {
/*  785 */     return new ObjectWriter(this, config, rootType, pp);
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
/*      */   public Version version() {
/*  800 */     return PackageVersion.VERSION;
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
/*      */   public ObjectMapper registerModule(Module module) {
/*  818 */     _assertNotNull("module", module);
/*      */ 
/*      */ 
/*      */     
/*  822 */     String name = module.getModuleName();
/*  823 */     if (name == null) {
/*  824 */       throw new IllegalArgumentException("Module without defined name");
/*      */     }
/*  826 */     Version version = module.version();
/*  827 */     if (version == null) {
/*  828 */       throw new IllegalArgumentException("Module without defined version");
/*      */     }
/*      */ 
/*      */     
/*  832 */     for (Module dep : module.getDependencies()) {
/*  833 */       registerModule(dep);
/*      */     }
/*      */ 
/*      */     
/*  837 */     if (isEnabled(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)) {
/*  838 */       Object typeId = module.getTypeId();
/*  839 */       if (typeId != null) {
/*  840 */         if (this._registeredModuleTypes == null)
/*      */         {
/*      */           
/*  843 */           this._registeredModuleTypes = new LinkedHashSet();
/*      */         }
/*      */         
/*  846 */         if (!this._registeredModuleTypes.add(typeId)) {
/*  847 */           return this;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  853 */     module.setupModule(new Module.SetupContext()
/*      */         {
/*      */ 
/*      */           
/*      */           public Version getMapperVersion()
/*      */           {
/*  859 */             return ObjectMapper.this.version();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public <C extends ObjectCodec> C getOwner() {
/*  866 */             return (C)ObjectMapper.this;
/*      */           }
/*      */ 
/*      */           
/*      */           public TypeFactory getTypeFactory() {
/*  871 */             return ObjectMapper.this._typeFactory;
/*      */           }
/*      */ 
/*      */           
/*      */           public boolean isEnabled(MapperFeature f) {
/*  876 */             return ObjectMapper.this.isEnabled(f);
/*      */           }
/*      */ 
/*      */           
/*      */           public boolean isEnabled(DeserializationFeature f) {
/*  881 */             return ObjectMapper.this.isEnabled(f);
/*      */           }
/*      */ 
/*      */           
/*      */           public boolean isEnabled(SerializationFeature f) {
/*  886 */             return ObjectMapper.this.isEnabled(f);
/*      */           }
/*      */ 
/*      */           
/*      */           public boolean isEnabled(JsonFactory.Feature f) {
/*  891 */             return ObjectMapper.this.isEnabled(f);
/*      */           }
/*      */ 
/*      */           
/*      */           public boolean isEnabled(JsonParser.Feature f) {
/*  896 */             return ObjectMapper.this.isEnabled(f);
/*      */           }
/*      */ 
/*      */           
/*      */           public boolean isEnabled(JsonGenerator.Feature f) {
/*  901 */             return ObjectMapper.this.isEnabled(f);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public MutableConfigOverride configOverride(Class<?> type) {
/*  908 */             return ObjectMapper.this.configOverride(type);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void addDeserializers(Deserializers d) {
/*  915 */             DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalDeserializers(d);
/*  916 */             ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
/*      */           }
/*      */ 
/*      */           
/*      */           public void addKeyDeserializers(KeyDeserializers d) {
/*  921 */             DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalKeyDeserializers(d);
/*  922 */             ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
/*      */           }
/*      */ 
/*      */           
/*      */           public void addBeanDeserializerModifier(BeanDeserializerModifier modifier) {
/*  927 */             DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withDeserializerModifier(modifier);
/*  928 */             ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void addSerializers(Serializers s) {
/*  935 */             ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalSerializers(s);
/*      */           }
/*      */ 
/*      */           
/*      */           public void addKeySerializers(Serializers s) {
/*  940 */             ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalKeySerializers(s);
/*      */           }
/*      */ 
/*      */           
/*      */           public void addBeanSerializerModifier(BeanSerializerModifier modifier) {
/*  945 */             ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withSerializerModifier(modifier);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void addAbstractTypeResolver(AbstractTypeResolver resolver) {
/*  952 */             DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAbstractTypeResolver(resolver);
/*  953 */             ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
/*      */           }
/*      */ 
/*      */           
/*      */           public void addTypeModifier(TypeModifier modifier) {
/*  958 */             TypeFactory f = ObjectMapper.this._typeFactory;
/*  959 */             f = f.withModifier(modifier);
/*  960 */             ObjectMapper.this.setTypeFactory(f);
/*      */           }
/*      */ 
/*      */           
/*      */           public void addValueInstantiators(ValueInstantiators instantiators) {
/*  965 */             DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withValueInstantiators(instantiators);
/*  966 */             ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
/*      */           }
/*      */ 
/*      */           
/*      */           public void setClassIntrospector(ClassIntrospector ci) {
/*  971 */             ObjectMapper.this._deserializationConfig = (DeserializationConfig)ObjectMapper.this._deserializationConfig.with(ci);
/*  972 */             ObjectMapper.this._serializationConfig = (SerializationConfig)ObjectMapper.this._serializationConfig.with(ci);
/*      */           }
/*      */ 
/*      */           
/*      */           public void insertAnnotationIntrospector(AnnotationIntrospector ai) {
/*  977 */             ObjectMapper.this._deserializationConfig = (DeserializationConfig)ObjectMapper.this._deserializationConfig.withInsertedAnnotationIntrospector(ai);
/*  978 */             ObjectMapper.this._serializationConfig = (SerializationConfig)ObjectMapper.this._serializationConfig.withInsertedAnnotationIntrospector(ai);
/*      */           }
/*      */ 
/*      */           
/*      */           public void appendAnnotationIntrospector(AnnotationIntrospector ai) {
/*  983 */             ObjectMapper.this._deserializationConfig = (DeserializationConfig)ObjectMapper.this._deserializationConfig.withAppendedAnnotationIntrospector(ai);
/*  984 */             ObjectMapper.this._serializationConfig = (SerializationConfig)ObjectMapper.this._serializationConfig.withAppendedAnnotationIntrospector(ai);
/*      */           }
/*      */ 
/*      */           
/*      */           public void registerSubtypes(Class<?>... subtypes) {
/*  989 */             ObjectMapper.this.registerSubtypes(subtypes);
/*      */           }
/*      */ 
/*      */           
/*      */           public void registerSubtypes(NamedType... subtypes) {
/*  994 */             ObjectMapper.this.registerSubtypes(subtypes);
/*      */           }
/*      */ 
/*      */           
/*      */           public void registerSubtypes(Collection<Class<?>> subtypes) {
/*  999 */             ObjectMapper.this.registerSubtypes(subtypes);
/*      */           }
/*      */ 
/*      */           
/*      */           public void setMixInAnnotations(Class<?> target, Class<?> mixinSource) {
/* 1004 */             ObjectMapper.this.addMixIn(target, mixinSource);
/*      */           }
/*      */ 
/*      */           
/*      */           public void addDeserializationProblemHandler(DeserializationProblemHandler handler) {
/* 1009 */             ObjectMapper.this.addHandler(handler);
/*      */           }
/*      */ 
/*      */           
/*      */           public void setNamingStrategy(PropertyNamingStrategy naming) {
/* 1014 */             ObjectMapper.this.setPropertyNamingStrategy(naming);
/*      */           }
/*      */         });
/*      */     
/* 1018 */     return this;
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
/*      */   public ObjectMapper registerModules(Module... modules) {
/* 1034 */     for (Module module : modules) {
/* 1035 */       registerModule(module);
/*      */     }
/* 1037 */     return this;
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
/*      */   public ObjectMapper registerModules(Iterable<? extends Module> modules) {
/* 1053 */     _assertNotNull("modules", modules);
/* 1054 */     for (Module module : modules) {
/* 1055 */       registerModule(module);
/*      */     }
/* 1057 */     return this;
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
/*      */   public Set<Object> getRegisteredModuleIds() {
/* 1075 */     return (this._registeredModuleTypes == null) ? 
/* 1076 */       Collections.<Object>emptySet() : Collections.<Object>unmodifiableSet(this._registeredModuleTypes);
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
/*      */   public static List<Module> findModules() {
/* 1089 */     return findModules(null);
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
/*      */   public static List<Module> findModules(ClassLoader classLoader) {
/* 1103 */     ArrayList<Module> modules = new ArrayList<>();
/* 1104 */     ServiceLoader<Module> loader = secureGetServiceLoader(Module.class, classLoader);
/* 1105 */     for (Module module : loader) {
/* 1106 */       modules.add(module);
/*      */     }
/* 1108 */     return modules;
/*      */   }
/*      */   
/*      */   private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
/* 1112 */     SecurityManager sm = System.getSecurityManager();
/* 1113 */     if (sm == null) {
/* 1114 */       return (classLoader == null) ? 
/* 1115 */         ServiceLoader.<T>load(clazz) : ServiceLoader.<T>load(clazz, classLoader);
/*      */     }
/* 1117 */     return AccessController.<ServiceLoader<T>>doPrivileged((PrivilegedAction)new PrivilegedAction<ServiceLoader<ServiceLoader<T>>>()
/*      */         {
/*      */           public ServiceLoader<T> run() {
/* 1120 */             return (classLoader == null) ? 
/* 1121 */               ServiceLoader.<T>load(clazz) : ServiceLoader.<T>load(clazz, classLoader);
/*      */           }
/*      */         });
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
/*      */   public ObjectMapper findAndRegisterModules() {
/* 1139 */     return registerModules(findModules());
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
/*      */   public JsonGenerator createGenerator(OutputStream out) throws IOException {
/* 1157 */     _assertNotNull("out", out);
/* 1158 */     JsonGenerator g = this._jsonFactory.createGenerator(out, JsonEncoding.UTF8);
/* 1159 */     this._serializationConfig.initialize(g);
/* 1160 */     return g;
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
/*      */   public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/* 1172 */     _assertNotNull("out", out);
/* 1173 */     JsonGenerator g = this._jsonFactory.createGenerator(out, enc);
/* 1174 */     this._serializationConfig.initialize(g);
/* 1175 */     return g;
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
/*      */   public JsonGenerator createGenerator(Writer w) throws IOException {
/* 1187 */     _assertNotNull("w", w);
/* 1188 */     JsonGenerator g = this._jsonFactory.createGenerator(w);
/* 1189 */     this._serializationConfig.initialize(g);
/* 1190 */     return g;
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
/*      */   public JsonGenerator createGenerator(File outputFile, JsonEncoding enc) throws IOException {
/* 1202 */     _assertNotNull("outputFile", outputFile);
/* 1203 */     JsonGenerator g = this._jsonFactory.createGenerator(outputFile, enc);
/* 1204 */     this._serializationConfig.initialize(g);
/* 1205 */     return g;
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
/*      */   public JsonGenerator createGenerator(DataOutput out) throws IOException {
/* 1217 */     _assertNotNull("out", out);
/* 1218 */     JsonGenerator g = this._jsonFactory.createGenerator(out);
/* 1219 */     this._serializationConfig.initialize(g);
/* 1220 */     return g;
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
/*      */   public JsonParser createParser(File src) throws IOException {
/* 1238 */     _assertNotNull("src", src);
/* 1239 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(src));
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
/*      */   public JsonParser createParser(URL src) throws IOException {
/* 1251 */     _assertNotNull("src", src);
/* 1252 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(src));
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
/*      */   public JsonParser createParser(InputStream in) throws IOException {
/* 1264 */     _assertNotNull("in", in);
/* 1265 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(in));
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
/*      */   public JsonParser createParser(Reader r) throws IOException {
/* 1277 */     _assertNotNull("r", r);
/* 1278 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(r));
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
/*      */   public JsonParser createParser(byte[] content) throws IOException {
/* 1290 */     _assertNotNull("content", content);
/* 1291 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(content));
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
/*      */   public JsonParser createParser(byte[] content, int offset, int len) throws IOException {
/* 1303 */     _assertNotNull("content", content);
/* 1304 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(content, offset, len));
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
/*      */   public JsonParser createParser(String content) throws IOException {
/* 1316 */     _assertNotNull("content", content);
/* 1317 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(content));
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
/*      */   public JsonParser createParser(char[] content) throws IOException {
/* 1329 */     _assertNotNull("content", content);
/* 1330 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(content));
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
/*      */   public JsonParser createParser(char[] content, int offset, int len) throws IOException {
/* 1342 */     _assertNotNull("content", content);
/* 1343 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(content, offset, len));
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
/*      */   public JsonParser createParser(DataInput content) throws IOException {
/* 1355 */     _assertNotNull("content", content);
/* 1356 */     return this._deserializationConfig.initialize(this._jsonFactory.createParser(content));
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
/*      */   public JsonParser createNonBlockingByteArrayParser() throws IOException {
/* 1368 */     return this._deserializationConfig.initialize(this._jsonFactory.createNonBlockingByteArrayParser());
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
/*      */   public SerializationConfig getSerializationConfig() {
/* 1386 */     return this._serializationConfig;
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
/*      */   public DeserializationConfig getDeserializationConfig() {
/* 1399 */     return this._deserializationConfig;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationContext getDeserializationContext() {
/* 1410 */     return (DeserializationContext)this._deserializationContext;
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
/*      */   public ObjectMapper setSerializerFactory(SerializerFactory f) {
/* 1424 */     this._serializerFactory = f;
/* 1425 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SerializerFactory getSerializerFactory() {
/* 1436 */     return this._serializerFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setSerializerProvider(DefaultSerializerProvider p) {
/* 1445 */     this._serializerProvider = p;
/* 1446 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SerializerProvider getSerializerProvider() {
/* 1457 */     return (SerializerProvider)this._serializerProvider;
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
/*      */   public SerializerProvider getSerializerProviderInstance() {
/* 1469 */     return (SerializerProvider)_serializerProvider(this._serializationConfig);
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
/*      */   public ObjectMapper setMixIns(Map<Class<?>, Class<?>> sourceMixins) {
/* 1498 */     this._mixIns.setLocalDefinitions(sourceMixins);
/* 1499 */     return this;
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
/*      */   public ObjectMapper addMixIn(Class<?> target, Class<?> mixinSource) {
/* 1516 */     this._mixIns.addLocalDefinition(target, mixinSource);
/* 1517 */     return this;
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
/*      */   public ObjectMapper setMixInResolver(ClassIntrospector.MixInResolver resolver) {
/* 1530 */     SimpleMixInResolver r = this._mixIns.withOverrides(resolver);
/* 1531 */     if (r != this._mixIns) {
/* 1532 */       this._mixIns = r;
/* 1533 */       this._deserializationConfig = new DeserializationConfig(this._deserializationConfig, r);
/* 1534 */       this._serializationConfig = new SerializationConfig(this._serializationConfig, r);
/*      */     } 
/* 1536 */     return this;
/*      */   }
/*      */   
/*      */   public Class<?> findMixInClassFor(Class<?> cls) {
/* 1540 */     return this._mixIns.findMixInClassFor(cls);
/*      */   }
/*      */ 
/*      */   
/*      */   public int mixInCount() {
/* 1545 */     return this._mixIns.localSize();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setMixInAnnotations(Map<Class<?>, Class<?>> sourceMixins) {
/* 1553 */     setMixIns(sourceMixins);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public final void addMixInAnnotations(Class<?> target, Class<?> mixinSource) {
/* 1561 */     addMixIn(target, mixinSource);
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
/*      */   public VisibilityChecker<?> getVisibilityChecker() {
/* 1576 */     return this._serializationConfig.getDefaultVisibilityChecker();
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
/*      */   public ObjectMapper setVisibility(VisibilityChecker<?> vc) {
/* 1590 */     this._configOverrides.setDefaultVisibility(vc);
/* 1591 */     return this;
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
/*      */   public ObjectMapper setVisibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility) {
/* 1620 */     VisibilityChecker<?> vc = this._configOverrides.getDefaultVisibility();
/* 1621 */     vc = vc.withVisibility(forMethod, visibility);
/* 1622 */     this._configOverrides.setDefaultVisibility(vc);
/* 1623 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SubtypeResolver getSubtypeResolver() {
/* 1630 */     return this._subtypeResolver;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setSubtypeResolver(SubtypeResolver str) {
/* 1637 */     this._subtypeResolver = str;
/* 1638 */     this._deserializationConfig = this._deserializationConfig.with(str);
/* 1639 */     this._serializationConfig = this._serializationConfig.with(str);
/* 1640 */     return this;
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
/*      */   public ObjectMapper setAnnotationIntrospector(AnnotationIntrospector ai) {
/* 1654 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(ai);
/* 1655 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(ai);
/* 1656 */     return this;
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
/*      */   public ObjectMapper setAnnotationIntrospectors(AnnotationIntrospector serializerAI, AnnotationIntrospector deserializerAI) {
/* 1676 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(serializerAI);
/* 1677 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(deserializerAI);
/* 1678 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setPropertyNamingStrategy(PropertyNamingStrategy s) {
/* 1685 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(s);
/* 1686 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(s);
/* 1687 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PropertyNamingStrategy getPropertyNamingStrategy() {
/* 1695 */     return this._serializationConfig.getPropertyNamingStrategy();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setAccessorNaming(AccessorNamingStrategy.Provider s) {
/* 1704 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(s);
/* 1705 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(s);
/* 1706 */     return this;
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
/*      */   public ObjectMapper setDefaultPrettyPrinter(PrettyPrinter pp) {
/* 1720 */     this._serializationConfig = this._serializationConfig.withDefaultPrettyPrinter(pp);
/* 1721 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setVisibilityChecker(VisibilityChecker<?> vc) {
/* 1729 */     setVisibility(vc);
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
/*      */   public ObjectMapper setPolymorphicTypeValidator(PolymorphicTypeValidator ptv) {
/* 1741 */     BaseSettings s = this._deserializationConfig.getBaseSettings().with(ptv);
/* 1742 */     this._deserializationConfig = this._deserializationConfig._withBase(s);
/* 1743 */     return this;
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
/*      */   public PolymorphicTypeValidator getPolymorphicTypeValidator() {
/* 1755 */     return this._deserializationConfig.getBaseSettings().getPolymorphicTypeValidator();
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
/*      */   public ObjectMapper setSerializationInclusion(JsonInclude.Include incl) {
/* 1774 */     setPropertyInclusion(JsonInclude.Value.construct(incl, incl));
/* 1775 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectMapper setPropertyInclusion(JsonInclude.Value incl) {
/* 1784 */     return setDefaultPropertyInclusion(incl);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setDefaultPropertyInclusion(JsonInclude.Value incl) {
/* 1795 */     this._configOverrides.setDefaultInclusion(incl);
/* 1796 */     return this;
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
/*      */   public ObjectMapper setDefaultPropertyInclusion(JsonInclude.Include incl) {
/* 1808 */     this._configOverrides.setDefaultInclusion(JsonInclude.Value.construct(incl, incl));
/* 1809 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setDefaultSetterInfo(JsonSetter.Value v) {
/* 1820 */     this._configOverrides.setDefaultSetterInfo(v);
/* 1821 */     return this;
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
/*      */   public ObjectMapper setDefaultVisibility(JsonAutoDetect.Value vis) {
/* 1833 */     this._configOverrides.setDefaultVisibility((VisibilityChecker)VisibilityChecker.Std.construct(vis));
/* 1834 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setDefaultMergeable(Boolean b) {
/* 1845 */     this._configOverrides.setDefaultMergeable(b);
/* 1846 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setDefaultLeniency(Boolean b) {
/* 1853 */     this._configOverrides.setDefaultLeniency(b);
/* 1854 */     return this;
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
/*      */   public void registerSubtypes(Class<?>... classes) {
/* 1871 */     getSubtypeResolver().registerSubtypes(classes);
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
/*      */   public void registerSubtypes(NamedType... types) {
/* 1883 */     getSubtypeResolver().registerSubtypes(types);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerSubtypes(Collection<Class<?>> subtypes) {
/* 1890 */     getSubtypeResolver().registerSubtypes(subtypes);
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
/*      */   public ObjectMapper activateDefaultTyping(PolymorphicTypeValidator ptv) {
/* 1914 */     return activateDefaultTyping(ptv, DefaultTyping.OBJECT_AND_NON_CONCRETE);
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
/*      */   public ObjectMapper activateDefaultTyping(PolymorphicTypeValidator ptv, DefaultTyping applicability) {
/* 1935 */     return activateDefaultTyping(ptv, applicability, JsonTypeInfo.As.WRAPPER_ARRAY);
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
/*      */   public ObjectMapper activateDefaultTyping(PolymorphicTypeValidator ptv, DefaultTyping applicability, JsonTypeInfo.As includeAs) {
/* 1963 */     if (includeAs == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
/* 1964 */       throw new IllegalArgumentException("Cannot use includeAs of " + includeAs);
/*      */     }
/*      */     
/* 1967 */     TypeResolverBuilder<?> typer = _constructDefaultTypeResolverBuilder(applicability, ptv);
/*      */     
/* 1969 */     typer = typer.init(JsonTypeInfo.Id.CLASS, null);
/* 1970 */     typer = typer.inclusion(includeAs);
/* 1971 */     return setDefaultTyping(typer);
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
/*      */   public ObjectMapper activateDefaultTypingAsProperty(PolymorphicTypeValidator ptv, DefaultTyping applicability, String propertyName) {
/* 1996 */     TypeResolverBuilder<?> typer = _constructDefaultTypeResolverBuilder(applicability, ptv);
/*      */ 
/*      */     
/* 1999 */     typer = typer.init(JsonTypeInfo.Id.CLASS, null);
/* 2000 */     typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
/* 2001 */     typer = typer.typeProperty(propertyName);
/* 2002 */     return setDefaultTyping(typer);
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
/*      */   public ObjectMapper deactivateDefaultTyping() {
/* 2014 */     return setDefaultTyping(null);
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
/*      */   public ObjectMapper setDefaultTyping(TypeResolverBuilder<?> typer) {
/* 2033 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(typer);
/* 2034 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(typer);
/* 2035 */     return this;
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
/*      */   public ObjectMapper enableDefaultTyping() {
/* 2049 */     return activateDefaultTyping(getPolymorphicTypeValidator());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectMapper enableDefaultTyping(DefaultTyping dti) {
/* 2057 */     return enableDefaultTyping(dti, JsonTypeInfo.As.WRAPPER_ARRAY);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectMapper enableDefaultTyping(DefaultTyping applicability, JsonTypeInfo.As includeAs) {
/* 2065 */     return activateDefaultTyping(getPolymorphicTypeValidator(), applicability, includeAs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectMapper enableDefaultTypingAsProperty(DefaultTyping applicability, String propertyName) {
/* 2073 */     return activateDefaultTypingAsProperty(getPolymorphicTypeValidator(), applicability, propertyName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectMapper disableDefaultTyping() {
/* 2081 */     return setDefaultTyping(null);
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
/*      */   public MutableConfigOverride configOverride(Class<?> type) {
/* 2108 */     return this._configOverrides.findOrCreateOverride(type);
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
/*      */   public MutableCoercionConfig coercionConfigDefaults() {
/* 2127 */     return this._coercionConfigs.defaultCoercions();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MutableCoercionConfig coercionConfigFor(LogicalType logicalType) {
/* 2137 */     return this._coercionConfigs.findOrCreateCoercion(logicalType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MutableCoercionConfig coercionConfigFor(Class<?> physicalType) {
/* 2147 */     return this._coercionConfigs.findOrCreateCoercion(physicalType);
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
/*      */   public TypeFactory getTypeFactory() {
/* 2160 */     return this._typeFactory;
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
/*      */   public ObjectMapper setTypeFactory(TypeFactory f) {
/* 2172 */     this._typeFactory = f;
/* 2173 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(f);
/* 2174 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(f);
/* 2175 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructType(Type t) {
/* 2184 */     _assertNotNull("t", t);
/* 2185 */     return this._typeFactory.constructType(t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructType(TypeReference<?> typeRef) {
/* 2195 */     _assertNotNull("typeRef", typeRef);
/* 2196 */     return this._typeFactory.constructType(typeRef);
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
/*      */   public JsonNodeFactory getNodeFactory() {
/* 2216 */     return this._deserializationConfig.getNodeFactory();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setNodeFactory(JsonNodeFactory f) {
/* 2225 */     this._deserializationConfig = this._deserializationConfig.with(f);
/* 2226 */     return this;
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
/*      */   public ObjectMapper setConstructorDetector(ConstructorDetector cd) {
/* 2238 */     this._deserializationConfig = this._deserializationConfig.with(cd);
/* 2239 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper addHandler(DeserializationProblemHandler h) {
/* 2247 */     this._deserializationConfig = this._deserializationConfig.withHandler(h);
/* 2248 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper clearProblemHandlers() {
/* 2256 */     this._deserializationConfig = this._deserializationConfig.withNoProblemHandlers();
/* 2257 */     return this;
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
/*      */   public ObjectMapper setConfig(DeserializationConfig config) {
/* 2275 */     _assertNotNull("config", config);
/* 2276 */     this._deserializationConfig = config;
/* 2277 */     return this;
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
/*      */   public void setFilters(FilterProvider filterProvider) {
/* 2291 */     this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
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
/*      */   public ObjectMapper setFilterProvider(FilterProvider filterProvider) {
/* 2306 */     this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
/* 2307 */     return this;
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
/*      */   public ObjectMapper setBase64Variant(Base64Variant v) {
/* 2321 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(v);
/* 2322 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(v);
/* 2323 */     return this;
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
/*      */   public ObjectMapper setConfig(SerializationConfig config) {
/* 2341 */     _assertNotNull("config", config);
/* 2342 */     this._serializationConfig = config;
/* 2343 */     return this;
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
/*      */   public JsonFactory tokenStreamFactory() {
/* 2371 */     return this._jsonFactory;
/*      */   }
/*      */   public JsonFactory getFactory() {
/* 2374 */     return this._jsonFactory;
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
/*      */   public ObjectMapper setDateFormat(DateFormat dateFormat) {
/* 2388 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(dateFormat);
/* 2389 */     this._serializationConfig = this._serializationConfig.with(dateFormat);
/* 2390 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateFormat getDateFormat() {
/* 2398 */     return this._serializationConfig.getDateFormat();
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
/*      */   public Object setHandlerInstantiator(HandlerInstantiator hi) {
/* 2410 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(hi);
/* 2411 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(hi);
/* 2412 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setInjectableValues(InjectableValues injectableValues) {
/* 2420 */     this._injectableValues = injectableValues;
/* 2421 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InjectableValues getInjectableValues() {
/* 2428 */     return this._injectableValues;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setLocale(Locale l) {
/* 2436 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(l);
/* 2437 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(l);
/* 2438 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper setTimeZone(TimeZone tz) {
/* 2446 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(tz);
/* 2447 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(tz);
/* 2448 */     return this;
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
/*      */   public ObjectMapper setDefaultAttributes(ContextAttributes attrs) {
/* 2460 */     this._deserializationConfig = this._deserializationConfig.with(attrs);
/* 2461 */     this._serializationConfig = this._serializationConfig.with(attrs);
/* 2462 */     return this;
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
/*      */   public boolean isEnabled(MapperFeature f) {
/* 2476 */     return this._serializationConfig.isEnabled(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectMapper configure(MapperFeature f, boolean state) {
/* 2484 */     this
/* 2485 */       ._serializationConfig = state ? (SerializationConfig)this._serializationConfig.with(new MapperFeature[] { f }) : (SerializationConfig)this._serializationConfig.without(new MapperFeature[] { f });
/* 2486 */     this
/* 2487 */       ._deserializationConfig = state ? (DeserializationConfig)this._deserializationConfig.with(new MapperFeature[] { f }) : (DeserializationConfig)this._deserializationConfig.without(new MapperFeature[] { f });
/* 2488 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectMapper enable(MapperFeature... f) {
/* 2496 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with(f);
/* 2497 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.with(f);
/* 2498 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectMapper disable(MapperFeature... f) {
/* 2506 */     this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.without(f);
/* 2507 */     this._serializationConfig = (SerializationConfig)this._serializationConfig.without(f);
/* 2508 */     return this;
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
/*      */   public boolean isEnabled(SerializationFeature f) {
/* 2522 */     return this._serializationConfig.isEnabled(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper configure(SerializationFeature f, boolean state) {
/* 2530 */     this
/* 2531 */       ._serializationConfig = state ? this._serializationConfig.with(f) : this._serializationConfig.without(f);
/* 2532 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper enable(SerializationFeature f) {
/* 2540 */     this._serializationConfig = this._serializationConfig.with(f);
/* 2541 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper enable(SerializationFeature first, SerializationFeature... f) {
/* 2550 */     this._serializationConfig = this._serializationConfig.with(first, f);
/* 2551 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper disable(SerializationFeature f) {
/* 2559 */     this._serializationConfig = this._serializationConfig.without(f);
/* 2560 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper disable(SerializationFeature first, SerializationFeature... f) {
/* 2569 */     this._serializationConfig = this._serializationConfig.without(first, f);
/* 2570 */     return this;
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
/*      */   public boolean isEnabled(DeserializationFeature f) {
/* 2584 */     return this._deserializationConfig.isEnabled(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper configure(DeserializationFeature f, boolean state) {
/* 2592 */     this
/* 2593 */       ._deserializationConfig = state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f);
/* 2594 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper enable(DeserializationFeature feature) {
/* 2602 */     this._deserializationConfig = this._deserializationConfig.with(feature);
/* 2603 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper enable(DeserializationFeature first, DeserializationFeature... f) {
/* 2612 */     this._deserializationConfig = this._deserializationConfig.with(first, f);
/* 2613 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper disable(DeserializationFeature feature) {
/* 2621 */     this._deserializationConfig = this._deserializationConfig.without(feature);
/* 2622 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectMapper disable(DeserializationFeature first, DeserializationFeature... f) {
/* 2631 */     this._deserializationConfig = this._deserializationConfig.without(first, f);
/* 2632 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(JsonParser.Feature f) {
/* 2642 */     return this._deserializationConfig.isEnabled(f, this._jsonFactory);
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
/*      */   public ObjectMapper configure(JsonParser.Feature f, boolean state) {
/* 2657 */     this._jsonFactory.configure(f, state);
/* 2658 */     return this;
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
/*      */   public ObjectMapper enable(JsonParser.Feature... features) {
/* 2674 */     for (JsonParser.Feature f : features) {
/* 2675 */       this._jsonFactory.enable(f);
/*      */     }
/* 2677 */     return this;
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
/*      */   public ObjectMapper disable(JsonParser.Feature... features) {
/* 2693 */     for (JsonParser.Feature f : features) {
/* 2694 */       this._jsonFactory.disable(f);
/*      */     }
/* 2696 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(JsonGenerator.Feature f) {
/* 2706 */     return this._serializationConfig.isEnabled(f, this._jsonFactory);
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
/*      */   public ObjectMapper configure(JsonGenerator.Feature f, boolean state) {
/* 2721 */     this._jsonFactory.configure(f, state);
/* 2722 */     return this;
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
/*      */   public ObjectMapper enable(JsonGenerator.Feature... features) {
/* 2738 */     for (JsonGenerator.Feature f : features) {
/* 2739 */       this._jsonFactory.enable(f);
/*      */     }
/* 2741 */     return this;
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
/*      */   public ObjectMapper disable(JsonGenerator.Feature... features) {
/* 2757 */     for (JsonGenerator.Feature f : features) {
/* 2758 */       this._jsonFactory.disable(f);
/*      */     }
/* 2760 */     return this;
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
/*      */   public boolean isEnabled(JsonFactory.Feature f) {
/* 2776 */     return this._jsonFactory.isEnabled(f);
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
/*      */   public boolean isEnabled(StreamReadFeature f) {
/* 2789 */     return isEnabled(f.mappedFeature());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(StreamWriteFeature f) {
/* 2796 */     return isEnabled(f.mappedFeature());
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
/*      */   public <T> T readValue(JsonParser p, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
/* 2830 */     _assertNotNull("p", p);
/* 2831 */     return (T)_readValue(getDeserializationConfig(), p, this._typeFactory.constructType(valueType));
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
/*      */   public <T> T readValue(JsonParser p, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
/* 2855 */     _assertNotNull("p", p);
/* 2856 */     return (T)_readValue(getDeserializationConfig(), p, this._typeFactory.constructType(valueTypeRef));
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
/*      */   public final <T> T readValue(JsonParser p, ResolvedType valueType) throws IOException, StreamReadException, DatabindException {
/* 2879 */     _assertNotNull("p", p);
/* 2880 */     return (T)_readValue(getDeserializationConfig(), p, (JavaType)valueType);
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
/*      */   public <T> T readValue(JsonParser p, JavaType valueType) throws IOException, StreamReadException, DatabindException {
/* 2899 */     _assertNotNull("p", p);
/* 2900 */     return (T)_readValue(getDeserializationConfig(), p, valueType);
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
/*      */   public <T extends TreeNode> T readTree(JsonParser p) throws IOException {
/*      */     NullNode nullNode;
/* 2930 */     _assertNotNull("p", p);
/*      */     
/* 2932 */     DeserializationConfig cfg = getDeserializationConfig();
/* 2933 */     JsonToken t = p.currentToken();
/* 2934 */     if (t == null) {
/* 2935 */       t = p.nextToken();
/* 2936 */       if (t == null) {
/* 2937 */         return null;
/*      */       }
/*      */     } 
/*      */     
/* 2941 */     JsonNode n = (JsonNode)_readValue(cfg, p, constructType(JsonNode.class));
/* 2942 */     if (n == null) {
/* 2943 */       nullNode = getNodeFactory().nullNode();
/*      */     }
/*      */     
/* 2946 */     return (T)nullNode;
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
/*      */   public <T> MappingIterator<T> readValues(JsonParser p, ResolvedType valueType) throws IOException {
/* 2972 */     return readValues(p, (JavaType)valueType);
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
/*      */   public <T> MappingIterator<T> readValues(JsonParser p, JavaType valueType) throws IOException {
/* 2986 */     _assertNotNull("p", p);
/* 2987 */     DeserializationConfig config = getDeserializationConfig();
/* 2988 */     DefaultDeserializationContext defaultDeserializationContext = createDeserializationContext(p, config);
/* 2989 */     JsonDeserializer<?> deser = _findRootDeserializer((DeserializationContext)defaultDeserializationContext, valueType);
/*      */     
/* 2991 */     return new MappingIterator<>(valueType, p, (DeserializationContext)defaultDeserializationContext, deser, false, null);
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
/*      */   public <T> MappingIterator<T> readValues(JsonParser p, Class<T> valueType) throws IOException {
/* 3007 */     return readValues(p, this._typeFactory.constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> MappingIterator<T> readValues(JsonParser p, TypeReference<T> valueTypeRef) throws IOException {
/* 3017 */     return readValues(p, this._typeFactory.constructType(valueTypeRef));
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
/*      */   public JsonNode readTree(InputStream in) throws IOException {
/* 3055 */     _assertNotNull("in", in);
/* 3056 */     return _readTreeAndClose(this._jsonFactory.createParser(in));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(Reader r) throws IOException {
/* 3064 */     _assertNotNull("r", r);
/* 3065 */     return _readTreeAndClose(this._jsonFactory.createParser(r));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(String content) throws JsonProcessingException, JsonMappingException {
/* 3074 */     _assertNotNull("content", content);
/*      */     try {
/* 3076 */       return _readTreeAndClose(this._jsonFactory.createParser(content));
/* 3077 */     } catch (JsonProcessingException e) {
/* 3078 */       throw e;
/* 3079 */     } catch (IOException e) {
/* 3080 */       throw JsonMappingException.fromUnexpectedIOE(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(byte[] content) throws IOException {
/* 3089 */     _assertNotNull("content", content);
/* 3090 */     return _readTreeAndClose(this._jsonFactory.createParser(content));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(byte[] content, int offset, int len) throws IOException {
/* 3098 */     _assertNotNull("content", content);
/* 3099 */     return _readTreeAndClose(this._jsonFactory.createParser(content, offset, len));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(File file) throws IOException {
/* 3108 */     _assertNotNull("file", file);
/* 3109 */     return _readTreeAndClose(this._jsonFactory.createParser(file));
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
/*      */   public JsonNode readTree(URL source) throws IOException {
/* 3124 */     _assertNotNull("source", source);
/* 3125 */     return _readTreeAndClose(this._jsonFactory.createParser(source));
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
/*      */   public void writeValue(JsonGenerator g, Object value) throws IOException, StreamWriteException, DatabindException {
/* 3143 */     _assertNotNull("g", g);
/* 3144 */     SerializationConfig config = getSerializationConfig();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3152 */     if (config.isEnabled(SerializationFeature.INDENT_OUTPUT) && 
/* 3153 */       g.getPrettyPrinter() == null) {
/* 3154 */       g.setPrettyPrinter(config.constructDefaultPrettyPrinter());
/*      */     }
/*      */     
/* 3157 */     if (config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
/* 3158 */       _writeCloseableValue(g, value, config);
/*      */     } else {
/* 3160 */       _serializerProvider(config).serializeValue(g, value);
/* 3161 */       if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/* 3162 */         g.flush();
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
/*      */   public void writeTree(JsonGenerator g, TreeNode rootNode) throws IOException {
/* 3177 */     _assertNotNull("g", g);
/* 3178 */     SerializationConfig config = getSerializationConfig();
/* 3179 */     _serializerProvider(config).serializeValue(g, rootNode);
/* 3180 */     if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/* 3181 */       g.flush();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTree(JsonGenerator g, JsonNode rootNode) throws IOException {
/* 3192 */     _assertNotNull("g", g);
/* 3193 */     SerializationConfig config = getSerializationConfig();
/* 3194 */     _serializerProvider(config).serializeValue(g, rootNode);
/* 3195 */     if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/* 3196 */       g.flush();
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
/*      */   public ObjectNode createObjectNode() {
/* 3209 */     return this._deserializationConfig.getNodeFactory().objectNode();
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
/*      */   public ArrayNode createArrayNode() {
/* 3221 */     return this._deserializationConfig.getNodeFactory().arrayNode();
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonNode missingNode() {
/* 3226 */     return this._deserializationConfig.getNodeFactory().missingNode();
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonNode nullNode() {
/* 3231 */     return (JsonNode)this._deserializationConfig.getNodeFactory().nullNode();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser treeAsTokens(TreeNode n) {
/* 3242 */     _assertNotNull("n", n);
/* 3243 */     return (JsonParser)new TreeTraversingParser((JsonNode)n, this);
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
/*      */   public <T> T treeToValue(TreeNode n, Class<T> valueType) throws IllegalArgumentException, JsonProcessingException {
/* 3268 */     if (n == null) {
/* 3269 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 3274 */       if (TreeNode.class.isAssignableFrom(valueType) && valueType
/* 3275 */         .isAssignableFrom(n.getClass())) {
/* 3276 */         return (T)n;
/*      */       }
/* 3278 */       JsonToken tt = n.asToken();
/*      */ 
/*      */       
/* 3281 */       if (tt == JsonToken.VALUE_EMBEDDED_OBJECT && 
/* 3282 */         n instanceof POJONode) {
/* 3283 */         Object ob = ((POJONode)n).getPojo();
/* 3284 */         if (ob == null || valueType.isInstance(ob)) {
/* 3285 */           return (T)ob;
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3295 */       return readValue(treeAsTokens(n), valueType);
/* 3296 */     } catch (JsonProcessingException e) {
/*      */ 
/*      */       
/* 3299 */       throw e;
/* 3300 */     } catch (IOException e) {
/* 3301 */       throw new IllegalArgumentException(e.getMessage(), e);
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
/*      */   public <T> T treeToValue(TreeNode n, JavaType valueType) throws IllegalArgumentException, JsonProcessingException {
/* 3317 */     if (n == null) {
/* 3318 */       return null;
/*      */     }
/*      */     try {
/* 3321 */       if (valueType.isTypeOrSubTypeOf(TreeNode.class) && valueType
/* 3322 */         .isTypeOrSuperTypeOf(n.getClass())) {
/* 3323 */         return (T)n;
/*      */       }
/* 3325 */       JsonToken tt = n.asToken();
/* 3326 */       if (tt == JsonToken.VALUE_EMBEDDED_OBJECT && 
/* 3327 */         n instanceof POJONode) {
/* 3328 */         Object ob = ((POJONode)n).getPojo();
/* 3329 */         if (ob == null || valueType.isTypeOrSuperTypeOf(ob.getClass())) {
/* 3330 */           return (T)ob;
/*      */         }
/*      */       } 
/*      */       
/* 3334 */       return readValue(treeAsTokens(n), valueType);
/* 3335 */     } catch (JsonProcessingException e) {
/*      */ 
/*      */       
/* 3338 */       throw e;
/* 3339 */     } catch (IOException e) {
/* 3340 */       throw new IllegalArgumentException(e.getMessage(), e);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T extends JsonNode> T valueToTree(Object fromValue) throws IllegalArgumentException {
/* 3374 */     if (fromValue == null) {
/* 3375 */       return (T)getNodeFactory().nullNode();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 3380 */     SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
/* 3381 */     DefaultSerializerProvider context = _serializerProvider(config);
/*      */ 
/*      */     
/* 3384 */     TokenBuffer buf = context.bufferForValueConversion(this);
/* 3385 */     if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 3386 */       buf = buf.forceUseOfBigDecimal(true);
/*      */     }
/*      */     try {
/* 3389 */       context.serializeValue((JsonGenerator)buf, fromValue);
/* 3390 */       try (JsonParser p = buf.asParser()) {
/* 3391 */         return (T)readTree(p);
/*      */       } 
/* 3393 */     } catch (IOException e) {
/* 3394 */       throw new IllegalArgumentException(e.getMessage(), e);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canSerialize(Class<?> type) {
/* 3420 */     return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause) {
/* 3431 */     return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, cause);
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
/*      */   public boolean canDeserialize(JavaType type) {
/* 3453 */     return createDeserializationContext(null, 
/* 3454 */         getDeserializationConfig()).hasValueDeserializerFor(type, null);
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
/*      */   public boolean canDeserialize(JavaType type, AtomicReference<Throwable> cause) {
/* 3466 */     return createDeserializationContext(null, 
/* 3467 */         getDeserializationConfig()).hasValueDeserializerFor(type, cause);
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
/*      */   public <T> T readValue(File src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
/* 3493 */     _assertNotNull("src", src);
/* 3494 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
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
/*      */   public <T> T readValue(File src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
/* 3513 */     _assertNotNull("src", src);
/* 3514 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
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
/*      */   public <T> T readValue(File src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
/* 3533 */     _assertNotNull("src", src);
/* 3534 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
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
/*      */   public <T> T readValue(URL src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
/* 3559 */     _assertNotNull("src", src);
/* 3560 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(URL src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
/* 3570 */     _assertNotNull("src", src);
/* 3571 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(URL src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
/* 3581 */     _assertNotNull("src", src);
/* 3582 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
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
/*      */   public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException, JsonMappingException {
/* 3596 */     _assertNotNull("content", content);
/* 3597 */     return readValue(content, this._typeFactory.constructType(valueType));
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
/*      */   public <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException, JsonMappingException {
/* 3611 */     _assertNotNull("content", content);
/* 3612 */     return readValue(content, this._typeFactory.constructType(valueTypeRef));
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
/*      */   public <T> T readValue(String content, JavaType valueType) throws JsonProcessingException, JsonMappingException {
/* 3627 */     _assertNotNull("content", content);
/*      */     try {
/* 3629 */       return (T)_readMapAndClose(this._jsonFactory.createParser(content), valueType);
/* 3630 */     } catch (JsonProcessingException e) {
/* 3631 */       throw e;
/* 3632 */     } catch (IOException e) {
/* 3633 */       throw JsonMappingException.fromUnexpectedIOE(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(Reader src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
/* 3641 */     _assertNotNull("src", src);
/* 3642 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(Reader src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
/* 3649 */     _assertNotNull("src", src);
/* 3650 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(Reader src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
/* 3657 */     _assertNotNull("src", src);
/* 3658 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(InputStream src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
/* 3665 */     _assertNotNull("src", src);
/* 3666 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(InputStream src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
/* 3673 */     _assertNotNull("src", src);
/* 3674 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(InputStream src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
/* 3681 */     _assertNotNull("src", src);
/* 3682 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
/* 3689 */     _assertNotNull("src", src);
/* 3690 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
/* 3698 */     _assertNotNull("src", src);
/* 3699 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
/* 3706 */     _assertNotNull("src", src);
/* 3707 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] src, int offset, int len, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
/* 3714 */     _assertNotNull("src", src);
/* 3715 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
/* 3722 */     _assertNotNull("src", src);
/* 3723 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] src, int offset, int len, JavaType valueType) throws IOException, StreamReadException, DatabindException {
/* 3730 */     _assertNotNull("src", src);
/* 3731 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src, offset, len), valueType);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(DataInput src, Class<T> valueType) throws IOException {
/* 3737 */     _assertNotNull("src", src);
/* 3738 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory
/* 3739 */         .constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(DataInput src, JavaType valueType) throws IOException {
/* 3745 */     _assertNotNull("src", src);
/* 3746 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
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
/*      */   public void writeValue(File resultFile, Object value) throws IOException, StreamWriteException, DatabindException {
/* 3763 */     _writeValueAndClose(createGenerator(resultFile, JsonEncoding.UTF8), value);
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
/*      */   public void writeValue(OutputStream out, Object value) throws IOException, StreamWriteException, DatabindException {
/* 3780 */     _writeValueAndClose(createGenerator(out, JsonEncoding.UTF8), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeValue(DataOutput out, Object value) throws IOException {
/* 3788 */     _writeValueAndClose(createGenerator(out), value);
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
/*      */   public void writeValue(Writer w, Object value) throws IOException, StreamWriteException, DatabindException {
/* 3804 */     _writeValueAndClose(createGenerator(w), value);
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
/*      */   public String writeValueAsString(Object value) throws JsonProcessingException {
/* 3819 */     SegmentedStringWriter sw = new SegmentedStringWriter(this._jsonFactory._getBufferRecycler());
/*      */     try {
/* 3821 */       _writeValueAndClose(createGenerator((Writer)sw), value);
/* 3822 */     } catch (JsonProcessingException e) {
/* 3823 */       throw e;
/* 3824 */     } catch (IOException e) {
/* 3825 */       throw JsonMappingException.fromUnexpectedIOE(e);
/*      */     } 
/* 3827 */     return sw.getAndClear();
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
/*      */   public byte[] writeValueAsBytes(Object value) throws JsonProcessingException {
/* 3843 */     try (ByteArrayBuilder bb = new ByteArrayBuilder(this._jsonFactory._getBufferRecycler())) {
/* 3844 */       _writeValueAndClose(createGenerator((OutputStream)bb, JsonEncoding.UTF8), value);
/* 3845 */       byte[] result = bb.toByteArray();
/* 3846 */       bb.release();
/* 3847 */       return result;
/* 3848 */     } catch (JsonProcessingException e) {
/* 3849 */       throw e;
/* 3850 */     } catch (IOException e) {
/* 3851 */       throw JsonMappingException.fromUnexpectedIOE(e);
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
/*      */   public ObjectWriter writer() {
/* 3867 */     return _newWriter(getSerializationConfig());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writer(SerializationFeature feature) {
/* 3876 */     return _newWriter(getSerializationConfig().with(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writer(SerializationFeature first, SerializationFeature... other) {
/* 3886 */     return _newWriter(getSerializationConfig().with(first, other));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writer(DateFormat df) {
/* 3895 */     return _newWriter(getSerializationConfig().with(df));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writerWithView(Class<?> serializationView) {
/* 3903 */     return _newWriter(getSerializationConfig().withView(serializationView));
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
/*      */   public ObjectWriter writerFor(Class<?> rootType) {
/* 3918 */     return _newWriter(getSerializationConfig(), (rootType == null) ? null : this._typeFactory
/* 3919 */         .constructType(rootType), null);
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
/*      */   public ObjectWriter writerFor(TypeReference<?> rootType) {
/* 3935 */     return _newWriter(getSerializationConfig(), (rootType == null) ? null : this._typeFactory
/* 3936 */         .constructType(rootType), null);
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
/*      */   public ObjectWriter writerFor(JavaType rootType) {
/* 3952 */     return _newWriter(getSerializationConfig(), rootType, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writer(PrettyPrinter pp) {
/* 3961 */     if (pp == null) {
/* 3962 */       pp = ObjectWriter.NULL_PRETTY_PRINTER;
/*      */     }
/* 3964 */     return _newWriter(getSerializationConfig(), null, pp);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writerWithDefaultPrettyPrinter() {
/* 3972 */     SerializationConfig config = getSerializationConfig();
/* 3973 */     return _newWriter(config, null, config
/* 3974 */         .getDefaultPrettyPrinter());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writer(FilterProvider filterProvider) {
/* 3982 */     return _newWriter(getSerializationConfig().withFilters(filterProvider));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writer(FormatSchema schema) {
/* 3993 */     _verifySchemaType(schema);
/* 3994 */     return _newWriter(getSerializationConfig(), schema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writer(Base64Variant defaultBase64) {
/* 4004 */     return _newWriter((SerializationConfig)getSerializationConfig().with(defaultBase64));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writer(CharacterEscapes escapes) {
/* 4014 */     return _newWriter(getSerializationConfig()).with(escapes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter writer(ContextAttributes attrs) {
/* 4024 */     return _newWriter(getSerializationConfig().with(attrs));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectWriter writerWithType(Class<?> rootType) {
/* 4032 */     return _newWriter(getSerializationConfig(), (rootType == null) ? null : this._typeFactory
/*      */         
/* 4034 */         .constructType(rootType), null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectWriter writerWithType(TypeReference<?> rootType) {
/* 4043 */     return _newWriter(getSerializationConfig(), (rootType == null) ? null : this._typeFactory
/*      */         
/* 4045 */         .constructType(rootType), null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectWriter writerWithType(JavaType rootType) {
/* 4054 */     return _newWriter(getSerializationConfig(), rootType, null);
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
/*      */   public ObjectReader reader() {
/* 4070 */     return _newReader(getDeserializationConfig()).with(this._injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader reader(DeserializationFeature feature) {
/* 4081 */     return _newReader(getDeserializationConfig().with(feature));
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
/*      */   public ObjectReader reader(DeserializationFeature first, DeserializationFeature... other) {
/* 4093 */     return _newReader(getDeserializationConfig().with(first, other));
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
/*      */   public ObjectReader readerForUpdating(Object valueToUpdate) {
/* 4107 */     JavaType t = this._typeFactory.constructType(valueToUpdate.getClass());
/* 4108 */     return _newReader(getDeserializationConfig(), t, valueToUpdate, null, this._injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader readerFor(JavaType type) {
/* 4119 */     return _newReader(getDeserializationConfig(), type, null, null, this._injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader readerFor(Class<?> type) {
/* 4130 */     return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader readerFor(TypeReference<?> type) {
/* 4141 */     return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
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
/*      */   public ObjectReader readerForArrayOf(Class<?> type) {
/* 4156 */     return _newReader(getDeserializationConfig(), (JavaType)this._typeFactory
/* 4157 */         .constructArrayType(type), null, null, this._injectableValues);
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
/*      */   public ObjectReader readerForListOf(Class<?> type) {
/* 4172 */     return _newReader(getDeserializationConfig(), (JavaType)this._typeFactory
/* 4173 */         .constructCollectionType(List.class, type), null, null, this._injectableValues);
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
/*      */   public ObjectReader readerForMapOf(Class<?> type) {
/* 4188 */     return _newReader(getDeserializationConfig(), (JavaType)this._typeFactory
/* 4189 */         .constructMapType(Map.class, String.class, type), null, null, this._injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader reader(JsonNodeFactory f) {
/* 4198 */     return _newReader(getDeserializationConfig()).with(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader reader(FormatSchema schema) {
/* 4209 */     _verifySchemaType(schema);
/* 4210 */     return _newReader(getDeserializationConfig(), null, null, schema, this._injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader reader(InjectableValues injectableValues) {
/* 4221 */     return _newReader(getDeserializationConfig(), null, null, null, injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader readerWithView(Class<?> view) {
/* 4230 */     return _newReader(getDeserializationConfig().withView(view));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader reader(Base64Variant defaultBase64) {
/* 4240 */     return _newReader((DeserializationConfig)getDeserializationConfig().with(defaultBase64));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader reader(ContextAttributes attrs) {
/* 4250 */     return _newReader(getDeserializationConfig().with(attrs));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectReader reader(JavaType type) {
/* 4258 */     return _newReader(getDeserializationConfig(), type, null, null, this._injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectReader reader(Class<?> type) {
/* 4267 */     return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectReader reader(TypeReference<?> type) {
/* 4276 */     return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
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
/*      */   public <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
/* 4324 */     return (T)_convert(fromValue, this._typeFactory.constructType(toValueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) throws IllegalArgumentException {
/* 4334 */     return (T)_convert(fromValue, this._typeFactory.constructType(toValueTypeRef));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T convertValue(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
/* 4344 */     return (T)_convert(fromValue, toValueType);
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
/*      */   protected Object _convert(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
/* 4361 */     SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
/* 4362 */     DefaultSerializerProvider context = _serializerProvider(config);
/*      */ 
/*      */     
/* 4365 */     TokenBuffer buf = context.bufferForValueConversion(this);
/* 4366 */     if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 4367 */       buf = buf.forceUseOfBigDecimal(true);
/*      */     }
/*      */     try {
/*      */       Object result;
/* 4371 */       context.serializeValue((JsonGenerator)buf, fromValue);
/*      */ 
/*      */       
/* 4374 */       JsonParser p = buf.asParser();
/*      */ 
/*      */       
/* 4377 */       DeserializationConfig deserConfig = getDeserializationConfig();
/* 4378 */       JsonToken t = _initForReading(p, toValueType);
/* 4379 */       if (t == JsonToken.VALUE_NULL) {
/* 4380 */         DefaultDeserializationContext defaultDeserializationContext = createDeserializationContext(p, deserConfig);
/* 4381 */         result = _findRootDeserializer((DeserializationContext)defaultDeserializationContext, toValueType).getNullValue((DeserializationContext)defaultDeserializationContext);
/* 4382 */       } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/* 4383 */         result = null;
/*      */       } else {
/* 4385 */         DefaultDeserializationContext defaultDeserializationContext = createDeserializationContext(p, deserConfig);
/* 4386 */         JsonDeserializer<Object> deser = _findRootDeserializer((DeserializationContext)defaultDeserializationContext, toValueType);
/*      */         
/* 4388 */         result = deser.deserialize(p, (DeserializationContext)defaultDeserializationContext);
/*      */       } 
/* 4390 */       p.close();
/* 4391 */       return result;
/* 4392 */     } catch (IOException e) {
/* 4393 */       throw new IllegalArgumentException(e.getMessage(), e);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T updateValue(T valueToUpdate, Object overrides) throws JsonMappingException {
/* 4436 */     T result = valueToUpdate;
/* 4437 */     if (valueToUpdate != null && overrides != null) {
/*      */       
/* 4439 */       SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
/* 4440 */       DefaultSerializerProvider context = _serializerProvider(config);
/*      */       
/* 4442 */       TokenBuffer buf = context.bufferForValueConversion(this);
/* 4443 */       if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 4444 */         buf = buf.forceUseOfBigDecimal(true);
/*      */       }
/*      */       try {
/* 4447 */         context.serializeValue((JsonGenerator)buf, overrides);
/* 4448 */         JsonParser p = buf.asParser();
/* 4449 */         result = readerForUpdating(valueToUpdate).readValue(p);
/* 4450 */         p.close();
/* 4451 */       } catch (IOException e) {
/* 4452 */         if (e instanceof JsonMappingException) {
/* 4453 */           throw (JsonMappingException)e;
/*      */         }
/*      */         
/* 4456 */         throw JsonMappingException.fromUnexpectedIOE(e);
/*      */       } 
/*      */     } 
/* 4459 */     return result;
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
/*      */   @Deprecated
/*      */   public JsonSchema generateJsonSchema(Class<?> t) throws JsonMappingException {
/* 4481 */     return _serializerProvider(getSerializationConfig()).generateJsonSchema(t);
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
/*      */   public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
/* 4498 */     acceptJsonFormatVisitor(this._typeFactory.constructType(type), visitor);
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
/*      */   public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
/* 4516 */     if (type == null) {
/* 4517 */       throw new IllegalArgumentException("type must be provided");
/*      */     }
/* 4519 */     _serializerProvider(getSerializationConfig()).acceptJsonFormatVisitor(type, visitor);
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
/*      */   protected TypeResolverBuilder<?> _constructDefaultTypeResolverBuilder(DefaultTyping applicability, PolymorphicTypeValidator ptv) {
/* 4536 */     return (TypeResolverBuilder<?>)DefaultTypeResolverBuilder.construct(applicability, ptv);
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
/*      */   protected DefaultSerializerProvider _serializerProvider(SerializationConfig config) {
/* 4550 */     return this._serializerProvider.createInstance(config, this._serializerFactory);
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
/*      */   protected final void _writeValueAndClose(JsonGenerator g, Object value) throws IOException {
/* 4562 */     SerializationConfig cfg = getSerializationConfig();
/* 4563 */     if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
/* 4564 */       _writeCloseable(g, value, cfg);
/*      */       return;
/*      */     } 
/*      */     try {
/* 4568 */       _serializerProvider(cfg).serializeValue(g, value);
/* 4569 */     } catch (Exception e) {
/* 4570 */       ClassUtil.closeOnFailAndThrowAsIOE(g, e);
/*      */       return;
/*      */     } 
/* 4573 */     g.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeCloseable(JsonGenerator g, Object value, SerializationConfig cfg) throws IOException {
/* 4583 */     Closeable toClose = (Closeable)value;
/*      */     try {
/* 4585 */       _serializerProvider(cfg).serializeValue(g, value);
/* 4586 */       Closeable tmpToClose = toClose;
/* 4587 */       toClose = null;
/* 4588 */       tmpToClose.close();
/* 4589 */     } catch (Exception e) {
/* 4590 */       ClassUtil.closeOnFailAndThrowAsIOE(g, toClose, e);
/*      */       return;
/*      */     } 
/* 4593 */     g.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeCloseableValue(JsonGenerator g, Object value, SerializationConfig cfg) throws IOException {
/* 4603 */     Closeable toClose = (Closeable)value;
/*      */     try {
/* 4605 */       _serializerProvider(cfg).serializeValue(g, value);
/* 4606 */       if (cfg.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/* 4607 */         g.flush();
/*      */       }
/* 4609 */     } catch (Exception e) {
/* 4610 */       ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, e);
/*      */       return;
/*      */     } 
/* 4613 */     toClose.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected final void _configAndWriteValue(JsonGenerator g, Object value) throws IOException {
/* 4621 */     getSerializationConfig().initialize(g);
/* 4622 */     _writeValueAndClose(g, value);
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
/*      */   protected Object _readValue(DeserializationConfig cfg, JsonParser p, JavaType valueType) throws IOException {
/*      */     Object result;
/* 4642 */     JsonToken t = _initForReading(p, valueType);
/* 4643 */     DefaultDeserializationContext ctxt = createDeserializationContext(p, cfg);
/* 4644 */     if (t == JsonToken.VALUE_NULL) {
/*      */       
/* 4646 */       result = _findRootDeserializer((DeserializationContext)ctxt, valueType).getNullValue((DeserializationContext)ctxt);
/* 4647 */     } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/* 4648 */       result = null;
/*      */     } else {
/* 4650 */       result = ctxt.readRootValue(p, valueType, _findRootDeserializer((DeserializationContext)ctxt, valueType), null);
/*      */     } 
/*      */     
/* 4653 */     p.clearCurrentToken();
/* 4654 */     if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/* 4655 */       _verifyNoTrailingTokens(p, (DeserializationContext)ctxt, valueType);
/*      */     }
/* 4657 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object _readMapAndClose(JsonParser p0, JavaType valueType) throws IOException {
/* 4663 */     try (JsonParser p = p0) {
/*      */       Object result;
/* 4665 */       DeserializationConfig cfg = getDeserializationConfig();
/* 4666 */       DefaultDeserializationContext ctxt = createDeserializationContext(p, cfg);
/* 4667 */       JsonToken t = _initForReading(p, valueType);
/* 4668 */       if (t == JsonToken.VALUE_NULL) {
/*      */         
/* 4670 */         result = _findRootDeserializer((DeserializationContext)ctxt, valueType).getNullValue((DeserializationContext)ctxt);
/* 4671 */       } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/* 4672 */         result = null;
/*      */       } else {
/* 4674 */         result = ctxt.readRootValue(p, valueType, 
/* 4675 */             _findRootDeserializer((DeserializationContext)ctxt, valueType), null);
/* 4676 */         ctxt.checkUnresolvedObjectId();
/*      */       } 
/* 4678 */       if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/* 4679 */         _verifyNoTrailingTokens(p, (DeserializationContext)ctxt, valueType);
/*      */       }
/* 4681 */       return result;
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
/*      */   protected JsonNode _readTreeAndClose(JsonParser p0) throws IOException {
/* 4693 */     try (JsonParser p = p0) {
/* 4694 */       JsonNode resultNode; JavaType valueType = constructType(JsonNode.class);
/*      */       
/* 4696 */       DeserializationConfig cfg = getDeserializationConfig();
/*      */ 
/*      */ 
/*      */       
/* 4700 */       cfg.initialize(p);
/* 4701 */       JsonToken t = p.currentToken();
/* 4702 */       if (t == null) {
/* 4703 */         t = p.nextToken();
/* 4704 */         if (t == null) {
/*      */ 
/*      */           
/* 4707 */           resultNode = cfg.getNodeFactory().missingNode(); return resultNode;
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 4712 */       DefaultDeserializationContext ctxt = createDeserializationContext(p, cfg);
/* 4713 */       if (t == JsonToken.VALUE_NULL) {
/* 4714 */         NullNode nullNode = cfg.getNodeFactory().nullNode();
/*      */       } else {
/* 4716 */         resultNode = (JsonNode)ctxt.readRootValue(p, valueType, 
/* 4717 */             _findRootDeserializer((DeserializationContext)ctxt, valueType), null);
/*      */       } 
/*      */ 
/*      */       
/* 4721 */       if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/* 4722 */         _verifyNoTrailingTokens(p, (DeserializationContext)ctxt, valueType);
/*      */       }
/* 4724 */       return resultNode;
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
/*      */   protected DefaultDeserializationContext createDeserializationContext(JsonParser p, DeserializationConfig cfg) {
/* 4737 */     return this._deserializationContext.createInstance(cfg, p, this._injectableValues);
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
/*      */   protected JsonToken _initForReading(JsonParser p, JavaType targetType) throws IOException {
/* 4753 */     this._deserializationConfig.initialize(p);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 4758 */     JsonToken t = p.currentToken();
/* 4759 */     if (t == null) {
/*      */       
/* 4761 */       t = p.nextToken();
/* 4762 */       if (t == null)
/*      */       {
/*      */         
/* 4765 */         throw MismatchedInputException.from(p, targetType, "No content to map due to end-of-input");
/*      */       }
/*      */     } 
/*      */     
/* 4769 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType) throws IOException {
/* 4779 */     JsonToken t = p.nextToken();
/* 4780 */     if (t != null) {
/* 4781 */       Class<?> bt = ClassUtil.rawClass(bindType);
/* 4782 */       ctxt.reportTrailingTokens(bt, p, t);
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
/*      */   protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt, JavaType valueType) throws DatabindException {
/* 4800 */     JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
/* 4801 */     if (deser != null) {
/* 4802 */       return deser;
/*      */     }
/*      */     
/* 4805 */     deser = ctxt.findRootValueDeserializer(valueType);
/* 4806 */     if (deser == null) {
/* 4807 */       return ctxt.<JsonDeserializer<Object>>reportBadDefinition(valueType, "Cannot find a deserializer for type " + valueType);
/*      */     }
/*      */     
/* 4810 */     this._rootDeserializers.put(valueType, deser);
/* 4811 */     return deser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _verifySchemaType(FormatSchema schema) {
/* 4819 */     if (schema != null && 
/* 4820 */       !this._jsonFactory.canUseSchema(schema)) {
/* 4821 */       throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._jsonFactory
/* 4822 */           .getFormatName());
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void _assertNotNull(String paramName, Object src) {
/* 4828 */     if (src == null)
/* 4829 */       throw new IllegalArgumentException(String.format("argument \"%s\" is null", new Object[] { paramName })); 
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ObjectMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */