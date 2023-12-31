/*      */ package com.fasterxml.jackson.databind;
/*      */ 
/*      */ import com.fasterxml.jackson.annotation.JsonFormat;
/*      */ import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*      */ import com.fasterxml.jackson.annotation.ObjectIdResolver;
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.StreamReadCapability;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeature;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionAction;
/*      */ import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
/*      */ import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*      */ import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
/*      */ import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*      */ import com.fasterxml.jackson.databind.deser.DeserializerCache;
/*      */ import com.fasterxml.jackson.databind.deser.DeserializerFactory;
/*      */ import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
/*      */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*      */ import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*      */ import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*      */ import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
/*      */ import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
/*      */ import com.fasterxml.jackson.databind.exc.InvalidFormatException;
/*      */ import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
/*      */ import com.fasterxml.jackson.databind.exc.MismatchedInputException;
/*      */ import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
/*      */ import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
/*      */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*      */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*      */ import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*      */ import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*      */ import com.fasterxml.jackson.databind.node.TreeTraversingParser;
/*      */ import com.fasterxml.jackson.databind.type.LogicalType;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import com.fasterxml.jackson.databind.util.LinkedNode;
/*      */ import com.fasterxml.jackson.databind.util.Named;
/*      */ import com.fasterxml.jackson.databind.util.ObjectBuffer;
/*      */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParseException;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class DeserializationContext
/*      */   extends DatabindContext
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   protected final DeserializerCache _cache;
/*      */   protected final DeserializerFactory _factory;
/*      */   protected final DeserializationConfig _config;
/*      */   protected final int _featureFlags;
/*      */   protected final JacksonFeatureSet<StreamReadCapability> _readCapabilities;
/*      */   protected final Class<?> _view;
/*      */   protected transient JsonParser _parser;
/*      */   protected final InjectableValues _injectableValues;
/*      */   protected transient ArrayBuilders _arrayBuilders;
/*      */   protected transient ObjectBuffer _objectBuffer;
/*      */   protected transient DateFormat _dateFormat;
/*      */   protected transient ContextAttributes _attributes;
/*      */   protected LinkedNode<JavaType> _currentType;
/*      */   
/*      */   protected DeserializationContext(DeserializerFactory df) {
/*  163 */     this(df, (DeserializerCache)null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected DeserializationContext(DeserializerFactory df, DeserializerCache cache) {
/*  169 */     if (df == null) {
/*  170 */       throw new NullPointerException("Cannot pass null DeserializerFactory");
/*      */     }
/*  172 */     this._factory = df;
/*  173 */     if (cache == null) {
/*  174 */       cache = new DeserializerCache();
/*      */     }
/*  176 */     this._cache = cache;
/*  177 */     this._featureFlags = 0;
/*  178 */     this._readCapabilities = null;
/*  179 */     this._config = null;
/*  180 */     this._injectableValues = null;
/*  181 */     this._view = null;
/*  182 */     this._attributes = null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected DeserializationContext(DeserializationContext src, DeserializerFactory factory) {
/*  188 */     this._cache = src._cache;
/*  189 */     this._factory = factory;
/*      */     
/*  191 */     this._config = src._config;
/*  192 */     this._featureFlags = src._featureFlags;
/*  193 */     this._readCapabilities = src._readCapabilities;
/*  194 */     this._view = src._view;
/*  195 */     this._parser = src._parser;
/*  196 */     this._injectableValues = src._injectableValues;
/*  197 */     this._attributes = src._attributes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DeserializationContext(DeserializationContext src, DeserializationConfig config, JsonParser p, InjectableValues injectableValues) {
/*  207 */     this._cache = src._cache;
/*  208 */     this._factory = src._factory;
/*      */ 
/*      */ 
/*      */     
/*  212 */     this._readCapabilities = (p == null) ? null : p.getReadCapabilities();
/*      */     
/*  214 */     this._config = config;
/*  215 */     this._featureFlags = config.getDeserializationFeatures();
/*  216 */     this._view = config.getActiveView();
/*  217 */     this._parser = p;
/*  218 */     this._injectableValues = injectableValues;
/*  219 */     this._attributes = config.getAttributes();
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
/*      */   protected DeserializationContext(DeserializationContext src, DeserializationConfig config) {
/*  231 */     this._cache = src._cache;
/*  232 */     this._factory = src._factory;
/*  233 */     this._readCapabilities = null;
/*      */     
/*  235 */     this._config = config;
/*  236 */     this._featureFlags = config.getDeserializationFeatures();
/*  237 */     this._view = null;
/*  238 */     this._parser = null;
/*  239 */     this._injectableValues = null;
/*  240 */     this._attributes = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DeserializationContext(DeserializationContext src) {
/*  247 */     this._cache = new DeserializerCache();
/*  248 */     this._factory = src._factory;
/*      */     
/*  250 */     this._config = src._config;
/*  251 */     this._featureFlags = src._featureFlags;
/*  252 */     this._readCapabilities = src._readCapabilities;
/*  253 */     this._view = src._view;
/*  254 */     this._injectableValues = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig getConfig() {
/*  264 */     return this._config;
/*      */   }
/*      */   public final Class<?> getActiveView() {
/*  267 */     return this._view;
/*      */   }
/*      */   
/*      */   public final boolean canOverrideAccessModifiers() {
/*  271 */     return this._config.canOverrideAccessModifiers();
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(MapperFeature feature) {
/*  276 */     return this._config.isEnabled(feature);
/*      */   }
/*      */ 
/*      */   
/*      */   public final JsonFormat.Value getDefaultPropertyFormat(Class<?> baseType) {
/*  281 */     return this._config.getDefaultPropertyFormat(baseType);
/*      */   }
/*      */ 
/*      */   
/*      */   public final AnnotationIntrospector getAnnotationIntrospector() {
/*  286 */     return this._config.getAnnotationIntrospector();
/*      */   }
/*      */ 
/*      */   
/*      */   public final TypeFactory getTypeFactory() {
/*  291 */     return this._config.getTypeFactory();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) throws IllegalArgumentException {
/*  298 */     if (baseType.hasRawClass(subclass)) {
/*  299 */       return baseType;
/*      */     }
/*      */ 
/*      */     
/*  303 */     return getConfig().getTypeFactory().constructSpecializedType(baseType, subclass, false);
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
/*  314 */     return this._config.getLocale();
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
/*  325 */     return this._config.getTimeZone();
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
/*  336 */     return this._attributes.getAttribute(key);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationContext setAttribute(Object key, Object value) {
/*  342 */     this._attributes = this._attributes.withPerCallAttribute(key, value);
/*  343 */     return this;
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
/*      */   public JavaType getContextualType() {
/*  360 */     return (this._currentType == null) ? null : (JavaType)this._currentType.value();
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
/*      */   public DeserializerFactory getFactory() {
/*  373 */     return this._factory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(DeserializationFeature feat) {
/*  384 */     return ((this._featureFlags & feat.getMask()) != 0);
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
/*      */   public final boolean isEnabled(StreamReadCapability cap) {
/*  396 */     return this._readCapabilities.isEnabled((JacksonFeature)cap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getDeserializationFeatures() {
/*  406 */     return this._featureFlags;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean hasDeserializationFeatures(int featureMask) {
/*  416 */     return ((this._featureFlags & featureMask) == featureMask);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean hasSomeOfFeatures(int featureMask) {
/*  426 */     return ((this._featureFlags & featureMask) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final JsonParser getParser() {
/*  437 */     return this._parser;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final Object findInjectableValue(Object valueId, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
/*  443 */     if (this._injectableValues == null) {
/*  444 */       return reportBadDefinition(ClassUtil.classOf(valueId), String.format("No 'injectableValues' configured, cannot inject value with id [%s]", new Object[] { valueId }));
/*      */     }
/*      */     
/*  447 */     return this._injectableValues.findInjectableValue(valueId, this, forProperty, beanInstance);
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
/*      */   public final Base64Variant getBase64Variant() {
/*  459 */     return this._config.getBase64Variant();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final JsonNodeFactory getNodeFactory() {
/*  469 */     return this._config.getNodeFactory();
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
/*      */   public CoercionAction findCoercionAction(LogicalType targetType, Class<?> targetClass, CoercionInputShape inputShape) {
/*  493 */     return this._config.findCoercionAction(targetType, targetClass, inputShape);
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
/*      */   public CoercionAction findCoercionFromBlankString(LogicalType targetType, Class<?> targetClass, CoercionAction actionIfBlankNotAllowed) {
/*  516 */     return this._config.findCoercionFromBlankString(targetType, targetClass, actionIfBlankNotAllowed);
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
/*      */   public TokenBuffer bufferForInputBuffering(JsonParser p) {
/*  535 */     return new TokenBuffer(p, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final TokenBuffer bufferForInputBuffering() {
/*  545 */     return bufferForInputBuffering(getParser());
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
/*      */   public TokenBuffer bufferAsCopyOfValue(JsonParser p) throws IOException {
/*  564 */     TokenBuffer buf = bufferForInputBuffering(p);
/*  565 */     buf.copyCurrentStructure(p);
/*  566 */     return buf;
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
/*      */   public boolean hasValueDeserializerFor(JavaType type, AtomicReference<Throwable> cause) {
/*      */     try {
/*  587 */       return this._cache.hasValueDeserializerFor(this, this._factory, type);
/*  588 */     } catch (DatabindException e) {
/*  589 */       if (cause != null) {
/*  590 */         cause.set(e);
/*      */       }
/*  592 */     } catch (RuntimeException e) {
/*  593 */       if (cause == null) {
/*  594 */         throw e;
/*      */       }
/*  596 */       cause.set(e);
/*      */     } 
/*  598 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final JsonDeserializer<Object> findContextualValueDeserializer(JavaType type, BeanProperty prop) throws JsonMappingException {
/*  609 */     JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
/*  610 */     if (deser != null) {
/*  611 */       deser = (JsonDeserializer)handleSecondaryContextualization(deser, prop, type);
/*      */     }
/*  613 */     return deser;
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
/*      */   public final JsonDeserializer<Object> findNonContextualValueDeserializer(JavaType type) throws JsonMappingException {
/*  632 */     return this._cache.findValueDeserializer(this, this._factory, type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final JsonDeserializer<Object> findRootValueDeserializer(JavaType type) throws JsonMappingException {
/*  642 */     JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
/*      */     
/*  644 */     if (deser == null) {
/*  645 */       return null;
/*      */     }
/*  647 */     deser = (JsonDeserializer)handleSecondaryContextualization(deser, (BeanProperty)null, type);
/*  648 */     TypeDeserializer typeDeser = this._factory.findTypeDeserializer(this._config, type);
/*  649 */     if (typeDeser != null) {
/*      */       
/*  651 */       typeDeser = typeDeser.forProperty(null);
/*  652 */       return (JsonDeserializer<Object>)new TypeWrappedDeserializer(typeDeser, deser);
/*      */     } 
/*  654 */     return deser;
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
/*      */   public final KeyDeserializer findKeyDeserializer(JavaType keyType, BeanProperty prop) throws JsonMappingException {
/*      */     KeyDeserializer kd;
/*      */     try {
/*  669 */       kd = this._cache.findKeyDeserializer(this, this._factory, keyType);
/*  670 */     } catch (IllegalArgumentException iae) {
/*      */ 
/*      */       
/*  673 */       reportBadDefinition(keyType, ClassUtil.exceptionMessage(iae));
/*  674 */       kd = null;
/*      */     } 
/*      */     
/*  677 */     if (kd instanceof ContextualKeyDeserializer) {
/*  678 */       kd = ((ContextualKeyDeserializer)kd).createContextual(this, prop);
/*      */     }
/*  680 */     return kd;
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
/*      */   public final JavaType constructType(Class<?> cls) {
/*  717 */     return (cls == null) ? null : this._config.constructType(cls);
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
/*      */   public Class<?> findClass(String className) throws ClassNotFoundException {
/*  731 */     return getTypeFactory().findClass(className);
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
/*      */   public final ObjectBuffer leaseObjectBuffer() {
/*  748 */     ObjectBuffer buf = this._objectBuffer;
/*  749 */     if (buf == null) {
/*  750 */       buf = new ObjectBuffer();
/*      */     } else {
/*  752 */       this._objectBuffer = null;
/*      */     } 
/*  754 */     return buf;
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
/*      */   public final void returnObjectBuffer(ObjectBuffer buf) {
/*  768 */     if (this._objectBuffer == null || buf
/*  769 */       .initialCapacity() >= this._objectBuffer.initialCapacity()) {
/*  770 */       this._objectBuffer = buf;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ArrayBuilders getArrayBuilders() {
/*  780 */     if (this._arrayBuilders == null) {
/*  781 */       this._arrayBuilders = new ArrayBuilders();
/*      */     }
/*  783 */     return this._arrayBuilders;
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
/*      */   public JsonDeserializer<?> handlePrimaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type) throws JsonMappingException {
/*  822 */     if (deser instanceof ContextualDeserializer) {
/*  823 */       this._currentType = new LinkedNode(type, this._currentType);
/*      */       try {
/*  825 */         deser = ((ContextualDeserializer)deser).createContextual(this, prop);
/*      */       } finally {
/*  827 */         this._currentType = this._currentType.next();
/*      */       } 
/*      */     } 
/*  830 */     return deser;
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
/*      */   public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type) throws JsonMappingException {
/*  853 */     if (deser instanceof ContextualDeserializer) {
/*  854 */       this._currentType = new LinkedNode(type, this._currentType);
/*      */       try {
/*  856 */         deser = ((ContextualDeserializer)deser).createContextual(this, prop);
/*      */       } finally {
/*  858 */         this._currentType = this._currentType.next();
/*      */       } 
/*      */     } 
/*  861 */     return deser;
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
/*      */   public Date parseDate(String dateStr) throws IllegalArgumentException {
/*      */     try {
/*  883 */       DateFormat df = _getDateFormat();
/*  884 */       return df.parse(dateStr);
/*  885 */     } catch (ParseException e) {
/*  886 */       throw new IllegalArgumentException(String.format("Failed to parse Date value '%s': %s", new Object[] { dateStr, 
/*      */               
/*  888 */               ClassUtil.exceptionMessage(e) }));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Calendar constructCalendar(Date d) {
/*  898 */     Calendar c = Calendar.getInstance(getTimeZone());
/*  899 */     c.setTime(d);
/*  900 */     return c;
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
/*      */   public String extractScalarFromObject(JsonParser p, JsonDeserializer<?> deser, Class<?> scalarType) throws IOException {
/*  932 */     return (String)handleUnexpectedToken(scalarType, p);
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
/*      */   public <T> T readValue(JsonParser p, Class<T> type) throws IOException {
/*  953 */     return readValue(p, getTypeFactory().constructType(type));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(JsonParser p, JavaType type) throws IOException {
/*  961 */     JsonDeserializer<Object> deser = findRootValueDeserializer(type);
/*  962 */     if (deser == null) {
/*  963 */       return reportBadDefinition(type, "Could not find JsonDeserializer for type " + 
/*  964 */           ClassUtil.getTypeDescription(type));
/*      */     }
/*  966 */     return (T)deser.deserialize(p, this);
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
/*      */   public <T> T readPropertyValue(JsonParser p, BeanProperty prop, Class<T> type) throws IOException {
/*  982 */     return readPropertyValue(p, prop, getTypeFactory().constructType(type));
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
/*      */   public <T> T readPropertyValue(JsonParser p, BeanProperty prop, JavaType type) throws IOException {
/*  994 */     JsonDeserializer<Object> deser = findContextualValueDeserializer(type, prop);
/*  995 */     if (deser == null)
/*  996 */       return reportBadDefinition(type, String.format("Could not find JsonDeserializer for type %s (via property %s)", new Object[] {
/*      */               
/*  998 */               ClassUtil.getTypeDescription(type), ClassUtil.nameOf(prop)
/*      */             })); 
/* 1000 */     return (T)deser.deserialize(p, this);
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
/*      */   public JsonNode readTree(JsonParser p) throws IOException {
/* 1014 */     JsonToken t = p.currentToken();
/* 1015 */     if (t == null) {
/* 1016 */       t = p.nextToken();
/* 1017 */       if (t == null) {
/* 1018 */         return getNodeFactory().missingNode();
/*      */       }
/*      */     } 
/* 1021 */     if (t == JsonToken.VALUE_NULL) {
/* 1022 */       return (JsonNode)getNodeFactory().nullNode();
/*      */     }
/* 1024 */     return (JsonNode)findRootValueDeserializer(this._config.constructType(JsonNode.class))
/* 1025 */       .deserialize(p, this);
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
/*      */   public <T> T readTreeAsValue(JsonNode n, Class<T> targetType) throws IOException {
/* 1051 */     if (n == null) {
/* 1052 */       return null;
/*      */     }
/* 1054 */     try (TreeTraversingParser p = _treeAsTokens(n)) {
/* 1055 */       return (T)readValue((JsonParser)p, (Class)targetType);
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
/*      */   public <T> T readTreeAsValue(JsonNode n, JavaType targetType) throws IOException {
/* 1075 */     if (n == null) {
/* 1076 */       return null;
/*      */     }
/* 1078 */     try (TreeTraversingParser p = _treeAsTokens(n)) {
/* 1079 */       return (T)readValue((JsonParser)p, targetType);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private TreeTraversingParser _treeAsTokens(JsonNode n) throws IOException {
/* 1086 */     ObjectCodec codec = (this._parser == null) ? null : this._parser.getCodec();
/* 1087 */     TreeTraversingParser p = new TreeTraversingParser(n, codec);
/*      */     
/* 1089 */     p.nextToken();
/* 1090 */     return p;
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
/*      */   public boolean handleUnknownProperty(JsonParser p, JsonDeserializer<?> deser, Object instanceOrClass, String propName) throws IOException {
/* 1112 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1113 */     while (h != null) {
/*      */       
/* 1115 */       if (((DeserializationProblemHandler)h.value()).handleUnknownProperty(this, p, deser, instanceOrClass, propName)) {
/* 1116 */         return true;
/*      */       }
/* 1118 */       h = h.next();
/*      */     } 
/*      */     
/* 1121 */     if (!isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/* 1122 */       p.skipChildren();
/* 1123 */       return true;
/*      */     } 
/*      */     
/* 1126 */     Collection<Object> propIds = (deser == null) ? null : deser.getKnownPropertyNames();
/* 1127 */     throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, propName, propIds);
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
/*      */   public Object handleWeirdKey(Class<?> keyClass, String keyValue, String msg, Object... msgArgs) throws IOException {
/* 1155 */     msg = _format(msg, msgArgs);
/* 1156 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1157 */     while (h != null) {
/*      */       
/* 1159 */       Object key = ((DeserializationProblemHandler)h.value()).handleWeirdKey(this, keyClass, keyValue, msg);
/* 1160 */       if (key != DeserializationProblemHandler.NOT_HANDLED) {
/*      */         
/* 1162 */         if (key == null || keyClass.isInstance(key)) {
/* 1163 */           return key;
/*      */         }
/* 1165 */         throw weirdStringException(keyValue, keyClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", new Object[] {
/*      */                 
/* 1167 */                 ClassUtil.getClassDescription(keyClass), 
/* 1168 */                 ClassUtil.getClassDescription(key)
/*      */               }));
/*      */       } 
/* 1171 */       h = h.next();
/*      */     } 
/* 1173 */     throw weirdKeyException(keyClass, keyValue, msg);
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
/*      */   public Object handleWeirdStringValue(Class<?> targetClass, String value, String msg, Object... msgArgs) throws IOException {
/* 1201 */     msg = _format(msg, msgArgs);
/* 1202 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1203 */     while (h != null) {
/*      */       
/* 1205 */       Object instance = ((DeserializationProblemHandler)h.value()).handleWeirdStringValue(this, targetClass, value, msg);
/* 1206 */       if (instance != DeserializationProblemHandler.NOT_HANDLED) {
/*      */         
/* 1208 */         if (_isCompatible(targetClass, instance)) {
/* 1209 */           return instance;
/*      */         }
/* 1211 */         throw weirdStringException(value, targetClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", new Object[] {
/*      */                 
/* 1213 */                 ClassUtil.getClassDescription(targetClass), 
/* 1214 */                 ClassUtil.getClassDescription(instance)
/*      */               }));
/*      */       } 
/* 1217 */       h = h.next();
/*      */     } 
/* 1219 */     throw weirdStringException(value, targetClass, msg);
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
/*      */   public Object handleWeirdNumberValue(Class<?> targetClass, Number value, String msg, Object... msgArgs) throws IOException {
/* 1246 */     msg = _format(msg, msgArgs);
/* 1247 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1248 */     while (h != null) {
/*      */       
/* 1250 */       Object key = ((DeserializationProblemHandler)h.value()).handleWeirdNumberValue(this, targetClass, value, msg);
/* 1251 */       if (key != DeserializationProblemHandler.NOT_HANDLED) {
/*      */         
/* 1253 */         if (_isCompatible(targetClass, key)) {
/* 1254 */           return key;
/*      */         }
/* 1256 */         throw weirdNumberException(value, targetClass, _format("DeserializationProblemHandler.handleWeirdNumberValue() for type %s returned value of type %s", new Object[] {
/*      */                 
/* 1258 */                 ClassUtil.getClassDescription(targetClass), 
/* 1259 */                 ClassUtil.getClassDescription(key)
/*      */               }));
/*      */       } 
/* 1262 */       h = h.next();
/*      */     } 
/* 1264 */     throw weirdNumberException(value, targetClass, msg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object handleWeirdNativeValue(JavaType targetType, Object badValue, JsonParser p) throws IOException {
/* 1271 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1272 */     Class<?> raw = targetType.getRawClass();
/* 1273 */     for (; h != null; h = h.next()) {
/*      */       
/* 1275 */       Object goodValue = ((DeserializationProblemHandler)h.value()).handleWeirdNativeValue(this, targetType, badValue, p);
/* 1276 */       if (goodValue != DeserializationProblemHandler.NOT_HANDLED) {
/*      */         
/* 1278 */         if (goodValue == null || raw.isInstance(goodValue)) {
/* 1279 */           return goodValue;
/*      */         }
/* 1281 */         throw JsonMappingException.from(p, _format("DeserializationProblemHandler.handleWeirdNativeValue() for type %s returned value of type %s", new Object[] {
/*      */                 
/* 1283 */                 ClassUtil.getClassDescription(targetType), 
/* 1284 */                 ClassUtil.getClassDescription(goodValue)
/*      */               }));
/*      */       } 
/*      */     } 
/* 1288 */     throw weirdNativeValueException(badValue, raw);
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
/*      */   public Object handleMissingInstantiator(Class<?> instClass, ValueInstantiator valueInst, JsonParser p, String msg, Object... msgArgs) throws IOException {
/* 1313 */     if (p == null) {
/* 1314 */       p = getParser();
/*      */     }
/* 1316 */     msg = _format(msg, msgArgs);
/* 1317 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1318 */     while (h != null) {
/*      */       
/* 1320 */       Object instance = ((DeserializationProblemHandler)h.value()).handleMissingInstantiator(this, instClass, valueInst, p, msg);
/*      */       
/* 1322 */       if (instance != DeserializationProblemHandler.NOT_HANDLED) {
/*      */         
/* 1324 */         if (_isCompatible(instClass, instance)) {
/* 1325 */           return instance;
/*      */         }
/* 1327 */         reportBadDefinition(constructType(instClass), String.format("DeserializationProblemHandler.handleMissingInstantiator() for type %s returned value of type %s", new Object[] {
/*      */                 
/* 1329 */                 ClassUtil.getClassDescription(instClass), 
/* 1330 */                 ClassUtil.getClassDescription(instance)
/*      */               }));
/*      */       } 
/* 1333 */       h = h.next();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1341 */     if (valueInst == null) {
/* 1342 */       msg = String.format("Cannot construct instance of %s: %s", new Object[] {
/* 1343 */             ClassUtil.nameOf(instClass), msg });
/* 1344 */       return reportBadDefinition(instClass, msg);
/*      */     } 
/* 1346 */     if (!valueInst.canInstantiate()) {
/* 1347 */       msg = String.format("Cannot construct instance of %s (no Creators, like default constructor, exist): %s", new Object[] {
/* 1348 */             ClassUtil.nameOf(instClass), msg });
/* 1349 */       return reportBadDefinition(instClass, msg);
/*      */     } 
/* 1351 */     msg = String.format("Cannot construct instance of %s (although at least one Creator exists): %s", new Object[] {
/* 1352 */           ClassUtil.nameOf(instClass), msg });
/* 1353 */     return reportInputMismatch(instClass, msg, new Object[0]);
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
/*      */   public Object handleInstantiationProblem(Class<?> instClass, Object argument, Throwable t) throws IOException {
/* 1377 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1378 */     while (h != null) {
/*      */       
/* 1380 */       Object instance = ((DeserializationProblemHandler)h.value()).handleInstantiationProblem(this, instClass, argument, t);
/* 1381 */       if (instance != DeserializationProblemHandler.NOT_HANDLED) {
/*      */         
/* 1383 */         if (_isCompatible(instClass, instance)) {
/* 1384 */           return instance;
/*      */         }
/* 1386 */         reportBadDefinition(constructType(instClass), String.format("DeserializationProblemHandler.handleInstantiationProblem() for type %s returned value of type %s", new Object[] {
/*      */                 
/* 1388 */                 ClassUtil.getClassDescription(instClass), 
/* 1389 */                 ClassUtil.classNameOf(instance)
/*      */               }));
/*      */       } 
/* 1392 */       h = h.next();
/*      */     } 
/*      */     
/* 1395 */     ClassUtil.throwIfIOE(t);
/*      */     
/* 1397 */     if (!isEnabled(DeserializationFeature.WRAP_EXCEPTIONS)) {
/* 1398 */       ClassUtil.throwIfRTE(t);
/*      */     }
/* 1400 */     throw instantiationException(instClass, t);
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
/*      */   public Object handleUnexpectedToken(Class<?> instClass, JsonParser p) throws IOException {
/* 1420 */     return handleUnexpectedToken(constructType(instClass), p.currentToken(), p, (String)null, new Object[0]);
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
/*      */   public Object handleUnexpectedToken(Class<?> instClass, JsonToken t, JsonParser p, String msg, Object... msgArgs) throws IOException {
/* 1442 */     return handleUnexpectedToken(constructType(instClass), t, p, msg, msgArgs);
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
/*      */   public Object handleUnexpectedToken(JavaType targetType, JsonParser p) throws IOException {
/* 1462 */     return handleUnexpectedToken(targetType, p.currentToken(), p, (String)null, new Object[0]);
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
/*      */   public Object handleUnexpectedToken(JavaType targetType, JsonToken t, JsonParser p, String msg, Object... msgArgs) throws IOException {
/* 1484 */     msg = _format(msg, msgArgs);
/* 1485 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1486 */     while (h != null) {
/* 1487 */       Object instance = ((DeserializationProblemHandler)h.value()).handleUnexpectedToken(this, targetType, t, p, msg);
/*      */       
/* 1489 */       if (instance != DeserializationProblemHandler.NOT_HANDLED) {
/* 1490 */         if (_isCompatible(targetType.getRawClass(), instance)) {
/* 1491 */           return instance;
/*      */         }
/* 1493 */         reportBadDefinition(targetType, String.format("DeserializationProblemHandler.handleUnexpectedToken() for type %s returned value of type %s", new Object[] {
/*      */                 
/* 1495 */                 ClassUtil.getTypeDescription(targetType), 
/* 1496 */                 ClassUtil.classNameOf(instance)
/*      */               }));
/*      */       } 
/* 1499 */       h = h.next();
/*      */     } 
/* 1501 */     if (msg == null) {
/* 1502 */       String targetDesc = ClassUtil.getTypeDescription(targetType);
/* 1503 */       if (t == null) {
/* 1504 */         msg = String.format("Unexpected end-of-input when trying read value of type %s", new Object[] { targetDesc });
/*      */       } else {
/*      */         
/* 1507 */         msg = String.format("Cannot deserialize value of type %s from %s (token `JsonToken.%s`)", new Object[] { targetDesc, 
/* 1508 */               _shapeForToken(t), t });
/*      */       } 
/*      */     } 
/*      */     
/* 1512 */     if (t != null && t.isScalarValue()) {
/* 1513 */       p.getText();
/*      */     }
/* 1515 */     reportInputMismatch(targetType, msg, new Object[0]);
/* 1516 */     return null;
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
/*      */   public JavaType handleUnknownTypeId(JavaType baseType, String id, TypeIdResolver idResolver, String extraDesc) throws IOException {
/* 1542 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1543 */     while (h != null) {
/*      */       
/* 1545 */       JavaType type = ((DeserializationProblemHandler)h.value()).handleUnknownTypeId(this, baseType, id, idResolver, extraDesc);
/* 1546 */       if (type != null) {
/* 1547 */         if (type.hasRawClass(Void.class)) {
/* 1548 */           return null;
/*      */         }
/*      */         
/* 1551 */         if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
/* 1552 */           return type;
/*      */         }
/* 1554 */         throw invalidTypeIdException(baseType, id, "problem handler tried to resolve into non-subtype: " + 
/*      */             
/* 1556 */             ClassUtil.getTypeDescription(type));
/*      */       } 
/* 1558 */       h = h.next();
/*      */     } 
/*      */     
/* 1561 */     if (!isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)) {
/* 1562 */       return null;
/*      */     }
/* 1564 */     throw invalidTypeIdException(baseType, id, extraDesc);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType handleMissingTypeId(JavaType baseType, TypeIdResolver idResolver, String extraDesc) throws IOException {
/* 1573 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1574 */     while (h != null) {
/*      */       
/* 1576 */       JavaType type = ((DeserializationProblemHandler)h.value()).handleMissingTypeId(this, baseType, idResolver, extraDesc);
/* 1577 */       if (type != null) {
/* 1578 */         if (type.hasRawClass(Void.class)) {
/* 1579 */           return null;
/*      */         }
/*      */         
/* 1582 */         if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
/* 1583 */           return type;
/*      */         }
/* 1585 */         throw invalidTypeIdException(baseType, null, "problem handler tried to resolve into non-subtype: " + 
/*      */             
/* 1587 */             ClassUtil.getTypeDescription(type));
/*      */       } 
/* 1589 */       h = h.next();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1596 */     throw missingTypeIdException(baseType, extraDesc);
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
/*      */   public void handleBadMerge(JsonDeserializer<?> deser) throws JsonMappingException {
/* 1612 */     if (!isEnabled(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)) {
/* 1613 */       JavaType type = constructType(deser.handledType());
/* 1614 */       String msg = String.format("Invalid configuration: values of type %s cannot be merged", new Object[] {
/* 1615 */             ClassUtil.getTypeDescription(type) });
/* 1616 */       throw InvalidDefinitionException.from(getParser(), msg, type);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _isCompatible(Class<?> target, Object value) {
/* 1625 */     if (value == null || target.isInstance(value)) {
/* 1626 */       return true;
/*      */     }
/*      */     
/* 1629 */     return (target.isPrimitive() && 
/* 1630 */       ClassUtil.wrapperType(target).isInstance(value));
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
/*      */   public void reportWrongTokenException(JsonDeserializer<?> deser, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
/* 1654 */     msg = _format(msg, msgArgs);
/* 1655 */     throw wrongTokenException(getParser(), deser.handledType(), expToken, msg);
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
/*      */   public void reportWrongTokenException(JavaType targetType, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
/* 1672 */     msg = _format(msg, msgArgs);
/* 1673 */     throw wrongTokenException(getParser(), targetType, expToken, msg);
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
/*      */   public void reportWrongTokenException(Class<?> targetType, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
/* 1690 */     msg = _format(msg, msgArgs);
/* 1691 */     throw wrongTokenException(getParser(), targetType, expToken, msg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T reportUnresolvedObjectId(ObjectIdReader oidReader, Object bean) throws JsonMappingException {
/* 1700 */     String msg = String.format("No Object Id found for an instance of %s, to assign to property '%s'", new Object[] {
/* 1701 */           ClassUtil.classNameOf(bean), oidReader.propertyName });
/* 1702 */     return reportInputMismatch((BeanProperty)oidReader.idProperty, msg, new Object[0]);
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
/*      */   public <T> T reportInputMismatch(JsonDeserializer<?> src, String msg, Object... msgArgs) throws JsonMappingException {
/* 1714 */     msg = _format(msg, msgArgs);
/* 1715 */     throw MismatchedInputException.from(getParser(), src.handledType(), msg);
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
/*      */   public <T> T reportInputMismatch(Class<?> targetType, String msg, Object... msgArgs) throws JsonMappingException {
/* 1727 */     msg = _format(msg, msgArgs);
/* 1728 */     throw MismatchedInputException.from(getParser(), targetType, msg);
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
/*      */   public <T> T reportInputMismatch(JavaType targetType, String msg, Object... msgArgs) throws JsonMappingException {
/* 1740 */     msg = _format(msg, msgArgs);
/* 1741 */     throw MismatchedInputException.from(getParser(), targetType, msg);
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
/*      */   public <T> T reportInputMismatch(BeanProperty prop, String msg, Object... msgArgs) throws JsonMappingException {
/* 1753 */     msg = _format(msg, msgArgs);
/* 1754 */     JavaType type = (prop == null) ? null : prop.getType();
/* 1755 */     MismatchedInputException e = MismatchedInputException.from(getParser(), type, msg);
/*      */     
/* 1757 */     if (prop != null) {
/* 1758 */       AnnotatedMember member = prop.getMember();
/* 1759 */       if (member != null) {
/* 1760 */         e.prependPath(member.getDeclaringClass(), prop.getName());
/*      */       }
/*      */     } 
/* 1763 */     throw e;
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
/*      */   public <T> T reportPropertyInputMismatch(Class<?> targetType, String propertyName, String msg, Object... msgArgs) throws JsonMappingException {
/* 1775 */     msg = _format(msg, msgArgs);
/* 1776 */     MismatchedInputException e = MismatchedInputException.from(getParser(), targetType, msg);
/* 1777 */     if (propertyName != null) {
/* 1778 */       e.prependPath(targetType, propertyName);
/*      */     }
/* 1780 */     throw e;
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
/*      */   public <T> T reportPropertyInputMismatch(JavaType targetType, String propertyName, String msg, Object... msgArgs) throws JsonMappingException {
/* 1792 */     return reportPropertyInputMismatch(targetType.getRawClass(), propertyName, msg, msgArgs);
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
/*      */   public <T> T reportBadCoercion(JsonDeserializer<?> src, Class<?> targetType, Object inputValue, String msg, Object... msgArgs) throws JsonMappingException {
/* 1805 */     msg = _format(msg, msgArgs);
/* 1806 */     InvalidFormatException e = InvalidFormatException.from(getParser(), msg, inputValue, targetType);
/*      */     
/* 1808 */     throw e;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T reportTrailingTokens(Class<?> targetType, JsonParser p, JsonToken trailingToken) throws JsonMappingException {
/* 1814 */     throw MismatchedInputException.from(p, targetType, String.format("Trailing token (of type %s) found after value (bound as %s): not allowed as per `DeserializationFeature.FAIL_ON_TRAILING_TOKENS`", new Object[] { trailingToken, 
/*      */             
/* 1816 */             ClassUtil.nameOf(targetType) }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void reportWrongTokenException(JsonParser p, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
/* 1825 */     msg = _format(msg, msgArgs);
/* 1826 */     throw wrongTokenException(p, expToken, msg);
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
/*      */   @Deprecated
/*      */   public void reportUnknownProperty(Object instanceOrClass, String fieldName, JsonDeserializer<?> deser) throws JsonMappingException {
/* 1845 */     if (isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/*      */       
/* 1847 */       Collection<Object> propIds = (deser == null) ? null : deser.getKnownPropertyNames();
/* 1848 */       throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, fieldName, propIds);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void reportMissingContent(String msg, Object... msgArgs) throws JsonMappingException {
/* 1860 */     throw MismatchedInputException.from(getParser(), (JavaType)null, "No content to map due to end-of-input");
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
/*      */   public <T> T reportBadTypeDefinition(BeanDescription bean, String msg, Object... msgArgs) throws JsonMappingException {
/* 1879 */     msg = _format(msg, msgArgs);
/* 1880 */     String beanDesc = ClassUtil.nameOf(bean.getBeanClass());
/* 1881 */     msg = String.format("Invalid type definition for type %s: %s", new Object[] { beanDesc, msg });
/* 1882 */     throw InvalidDefinitionException.from(this._parser, msg, bean, null);
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
/*      */   public <T> T reportBadPropertyDefinition(BeanDescription bean, BeanPropertyDefinition prop, String msg, Object... msgArgs) throws JsonMappingException {
/* 1894 */     msg = _format(msg, msgArgs);
/* 1895 */     String propName = ClassUtil.nameOf((Named)prop);
/* 1896 */     String beanDesc = ClassUtil.nameOf(bean.getBeanClass());
/* 1897 */     msg = String.format("Invalid definition for property %s (of type %s): %s", new Object[] { propName, beanDesc, msg });
/*      */     
/* 1899 */     throw InvalidDefinitionException.from(this._parser, msg, bean, prop);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T reportBadDefinition(JavaType type, String msg) throws JsonMappingException {
/* 1904 */     throw InvalidDefinitionException.from(this._parser, msg, type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public <T> T reportBadMerge(JsonDeserializer<?> deser) throws JsonMappingException {
/* 1912 */     handleBadMerge(deser);
/* 1913 */     return null;
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
/*      */   public JsonMappingException wrongTokenException(JsonParser p, JavaType targetType, JsonToken expToken, String extra) {
/* 1936 */     String msg = String.format("Unexpected token (%s), expected %s", new Object[] { p
/* 1937 */           .currentToken(), expToken });
/* 1938 */     msg = _colonConcat(msg, extra);
/* 1939 */     return (JsonMappingException)MismatchedInputException.from(p, targetType, msg);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonMappingException wrongTokenException(JsonParser p, Class<?> targetType, JsonToken expToken, String extra) {
/* 1945 */     String msg = String.format("Unexpected token (%s), expected %s", new Object[] { p
/* 1946 */           .currentToken(), expToken });
/* 1947 */     msg = _colonConcat(msg, extra);
/* 1948 */     return (JsonMappingException)MismatchedInputException.from(p, targetType, msg);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonMappingException wrongTokenException(JsonParser p, JsonToken expToken, String msg) {
/* 1955 */     return wrongTokenException(p, (JavaType)null, expToken, msg);
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
/*      */   public JsonMappingException weirdKeyException(Class<?> keyClass, String keyValue, String msg) {
/* 1968 */     return (JsonMappingException)InvalidFormatException.from(this._parser, 
/* 1969 */         String.format("Cannot deserialize Map key of type %s from String %s: %s", new Object[] {
/* 1970 */             ClassUtil.nameOf(keyClass), _quotedString(keyValue), msg
/*      */           }), keyValue, keyClass);
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
/*      */   public JsonMappingException weirdStringException(String value, Class<?> instClass, String msgBase) {
/* 1989 */     String msg = String.format("Cannot deserialize value of type %s from String %s: %s", new Object[] {
/* 1990 */           ClassUtil.nameOf(instClass), _quotedString(value), msgBase });
/* 1991 */     return (JsonMappingException)InvalidFormatException.from(this._parser, msg, value, instClass);
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
/*      */   public JsonMappingException weirdNumberException(Number value, Class<?> instClass, String msg) {
/* 2003 */     return (JsonMappingException)InvalidFormatException.from(this._parser, 
/* 2004 */         String.format("Cannot deserialize value of type %s from number %s: %s", new Object[] {
/* 2005 */             ClassUtil.nameOf(instClass), String.valueOf(value), msg
/*      */           }), value, instClass);
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
/*      */   public JsonMappingException weirdNativeValueException(Object value, Class<?> instClass) {
/* 2021 */     return (JsonMappingException)InvalidFormatException.from(this._parser, String.format("Cannot deserialize value of type %s from native value (`JsonToken.VALUE_EMBEDDED_OBJECT`) of type %s: incompatible types", new Object[] {
/*      */             
/* 2023 */             ClassUtil.nameOf(instClass), ClassUtil.classNameOf(value)
/*      */           }), value, instClass);
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
/*      */   public JsonMappingException instantiationException(Class<?> instClass, Throwable cause) {
/*      */     String excMsg;
/* 2038 */     if (cause == null) {
/* 2039 */       excMsg = "N/A";
/* 2040 */     } else if ((excMsg = ClassUtil.exceptionMessage(cause)) == null) {
/* 2041 */       excMsg = ClassUtil.nameOf(cause.getClass());
/*      */     } 
/* 2043 */     String msg = String.format("Cannot construct instance of %s, problem: %s", new Object[] {
/* 2044 */           ClassUtil.nameOf(instClass), excMsg
/*      */         });
/*      */     
/* 2047 */     return (JsonMappingException)ValueInstantiationException.from(this._parser, msg, constructType(instClass), cause);
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
/*      */   public JsonMappingException instantiationException(Class<?> instClass, String msg0) {
/* 2062 */     return (JsonMappingException)ValueInstantiationException.from(this._parser, 
/* 2063 */         String.format("Cannot construct instance of %s: %s", new Object[] {
/* 2064 */             ClassUtil.nameOf(instClass), msg0
/* 2065 */           }), constructType(instClass));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonMappingException invalidTypeIdException(JavaType baseType, String typeId, String extraDesc) {
/* 2071 */     String msg = String.format("Could not resolve type id '%s' as a subtype of %s", new Object[] { typeId, 
/* 2072 */           ClassUtil.getTypeDescription(baseType) });
/* 2073 */     return (JsonMappingException)InvalidTypeIdException.from(this._parser, _colonConcat(msg, extraDesc), baseType, typeId);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonMappingException missingTypeIdException(JavaType baseType, String extraDesc) {
/* 2081 */     String msg = String.format("Could not resolve subtype of %s", new Object[] { baseType });
/*      */     
/* 2083 */     return (JsonMappingException)InvalidTypeIdException.from(this._parser, _colonConcat(msg, extraDesc), baseType, null);
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
/*      */   @Deprecated
/*      */   public JsonMappingException unknownTypeException(JavaType type, String id, String extraDesc) {
/* 2101 */     String msg = String.format("Could not resolve type id '%s' into a subtype of %s", new Object[] { id, 
/* 2102 */           ClassUtil.getTypeDescription(type) });
/* 2103 */     msg = _colonConcat(msg, extraDesc);
/* 2104 */     return (JsonMappingException)MismatchedInputException.from(this._parser, type, msg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonMappingException endOfInputException(Class<?> instClass) {
/* 2115 */     return (JsonMappingException)MismatchedInputException.from(this._parser, instClass, "Unexpected end-of-input when trying to deserialize a " + instClass
/* 2116 */         .getName());
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
/*      */   @Deprecated
/*      */   public void reportMappingException(String msg, Object... msgArgs) throws JsonMappingException {
/* 2140 */     throw JsonMappingException.from(getParser(), _format(msg, msgArgs));
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
/*      */   @Deprecated
/*      */   public JsonMappingException mappingException(String message) {
/* 2156 */     return JsonMappingException.from(getParser(), message);
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
/*      */   @Deprecated
/*      */   public JsonMappingException mappingException(String msg, Object... msgArgs) {
/* 2172 */     return JsonMappingException.from(getParser(), _format(msg, msgArgs));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonMappingException mappingException(Class<?> targetClass) {
/* 2182 */     return mappingException(targetClass, this._parser.currentToken());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonMappingException mappingException(Class<?> targetClass, JsonToken token) {
/* 2190 */     return JsonMappingException.from(this._parser, 
/* 2191 */         String.format("Cannot deserialize instance of %s out of %s token", new Object[] {
/* 2192 */             ClassUtil.nameOf(targetClass), token
/*      */           }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected DateFormat getDateFormat() {
/* 2203 */     return _getDateFormat();
/*      */   }
/*      */   
/*      */   protected DateFormat _getDateFormat() {
/* 2207 */     if (this._dateFormat != null) {
/* 2208 */       return this._dateFormat;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2215 */     DateFormat df = this._config.getDateFormat();
/* 2216 */     this._dateFormat = df = (DateFormat)df.clone();
/* 2217 */     return df;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String _shapeForToken(JsonToken t) {
/* 2226 */     if (t != null) {
/* 2227 */       switch (t) {
/*      */         
/*      */         case START_OBJECT:
/*      */         case END_OBJECT:
/*      */         case FIELD_NAME:
/* 2232 */           return "Object value";
/*      */ 
/*      */         
/*      */         case START_ARRAY:
/*      */         case END_ARRAY:
/* 2237 */           return "Array value";
/*      */         
/*      */         case VALUE_FALSE:
/*      */         case VALUE_TRUE:
/* 2241 */           return "Boolean value";
/*      */         
/*      */         case VALUE_EMBEDDED_OBJECT:
/* 2244 */           return "Embedded Object";
/*      */         
/*      */         case VALUE_NUMBER_FLOAT:
/* 2247 */           return "Floating-point value";
/*      */         case VALUE_NUMBER_INT:
/* 2249 */           return "Integer value";
/*      */         case VALUE_STRING:
/* 2251 */           return "String value";
/*      */         
/*      */         case VALUE_NULL:
/* 2254 */           return "Null value";
/*      */       } 
/*      */ 
/*      */       
/* 2258 */       return "[Unavailable value]";
/*      */     } 
/*      */     
/* 2261 */     return "<end of input>";
/*      */   }
/*      */   
/*      */   public abstract ReadableObjectId findObjectId(Object paramObject, ObjectIdGenerator<?> paramObjectIdGenerator, ObjectIdResolver paramObjectIdResolver);
/*      */   
/*      */   public abstract void checkUnresolvedObjectId() throws UnresolvedForwardReference;
/*      */   
/*      */   public abstract JsonDeserializer<Object> deserializerInstance(Annotated paramAnnotated, Object paramObject) throws JsonMappingException;
/*      */   
/*      */   public abstract KeyDeserializer keyDeserializerInstance(Annotated paramAnnotated, Object paramObject) throws JsonMappingException;
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/DeserializationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */