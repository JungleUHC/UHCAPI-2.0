/*      */ package com.fasterxml.jackson.databind;
/*      */ 
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.FormatFeature;
/*      */ import com.fasterxml.jackson.core.FormatSchema;
/*      */ import com.fasterxml.jackson.core.JacksonException;
/*      */ import com.fasterxml.jackson.core.JsonFactory;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.JsonParseException;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonPointer;
/*      */ import com.fasterxml.jackson.core.JsonProcessingException;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.StreamReadFeature;
/*      */ import com.fasterxml.jackson.core.TreeNode;
/*      */ import com.fasterxml.jackson.core.Version;
/*      */ import com.fasterxml.jackson.core.Versioned;
/*      */ import com.fasterxml.jackson.core.exc.StreamReadException;
/*      */ import com.fasterxml.jackson.core.filter.FilteringParserDelegate;
/*      */ import com.fasterxml.jackson.core.filter.JsonPointerBasedFilter;
/*      */ import com.fasterxml.jackson.core.filter.TokenFilter;
/*      */ import com.fasterxml.jackson.core.type.ResolvedType;
/*      */ import com.fasterxml.jackson.core.type.TypeReference;
/*      */ import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*      */ import com.fasterxml.jackson.databind.cfg.PackageVersion;
/*      */ import com.fasterxml.jackson.databind.deser.DataFormatReaders;
/*      */ import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
/*      */ import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*      */ import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*      */ import com.fasterxml.jackson.databind.node.NullNode;
/*      */ import com.fasterxml.jackson.databind.node.TreeTraversingParser;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import java.io.DataInput;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Type;
/*      */ import java.net.URL;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ObjectReader
/*      */   extends ObjectCodec
/*      */   implements Versioned, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 2L;
/*      */   protected final DeserializationConfig _config;
/*      */   protected final DefaultDeserializationContext _context;
/*      */   protected final JsonFactory _parserFactory;
/*      */   protected final boolean _unwrapRoot;
/*      */   private final TokenFilter _filter;
/*      */   protected final JavaType _valueType;
/*      */   protected final JsonDeserializer<Object> _rootDeserializer;
/*      */   protected final Object _valueToUpdate;
/*      */   protected final FormatSchema _schema;
/*      */   protected final InjectableValues _injectableValues;
/*      */   protected final DataFormatReaders _dataFormatReaders;
/*      */   protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;
/*      */   protected transient JavaType _jsonNodeType;
/*      */   
/*      */   protected ObjectReader(ObjectMapper mapper, DeserializationConfig config) {
/*  172 */     this(mapper, config, null, null, null, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectReader(ObjectMapper mapper, DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues) {
/*  183 */     this._config = config;
/*  184 */     this._context = mapper._deserializationContext;
/*  185 */     this._rootDeserializers = mapper._rootDeserializers;
/*  186 */     this._parserFactory = mapper._jsonFactory;
/*  187 */     this._valueType = valueType;
/*  188 */     this._valueToUpdate = valueToUpdate;
/*  189 */     this._schema = schema;
/*  190 */     this._injectableValues = injectableValues;
/*  191 */     this._unwrapRoot = config.useRootWrapping();
/*      */     
/*  193 */     this._rootDeserializer = _prefetchRootDeserializer(valueType);
/*  194 */     this._dataFormatReaders = null;
/*  195 */     this._filter = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectReader(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
/*  206 */     this._config = config;
/*  207 */     this._context = base._context;
/*      */     
/*  209 */     this._rootDeserializers = base._rootDeserializers;
/*  210 */     this._parserFactory = base._parserFactory;
/*      */     
/*  212 */     this._valueType = valueType;
/*  213 */     this._rootDeserializer = rootDeser;
/*  214 */     this._valueToUpdate = valueToUpdate;
/*  215 */     this._schema = schema;
/*  216 */     this._injectableValues = injectableValues;
/*  217 */     this._unwrapRoot = config.useRootWrapping();
/*  218 */     this._dataFormatReaders = dataFormatReaders;
/*  219 */     this._filter = base._filter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectReader(ObjectReader base, DeserializationConfig config) {
/*  227 */     this._config = config;
/*  228 */     this._context = base._context;
/*      */     
/*  230 */     this._rootDeserializers = base._rootDeserializers;
/*  231 */     this._parserFactory = base._parserFactory;
/*      */     
/*  233 */     this._valueType = base._valueType;
/*  234 */     this._rootDeserializer = base._rootDeserializer;
/*  235 */     this._valueToUpdate = base._valueToUpdate;
/*  236 */     this._schema = base._schema;
/*  237 */     this._injectableValues = base._injectableValues;
/*  238 */     this._unwrapRoot = config.useRootWrapping();
/*  239 */     this._dataFormatReaders = base._dataFormatReaders;
/*  240 */     this._filter = base._filter;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectReader(ObjectReader base, JsonFactory f) {
/*  246 */     this
/*  247 */       ._config = (DeserializationConfig)base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
/*  248 */     this._context = base._context;
/*      */     
/*  250 */     this._rootDeserializers = base._rootDeserializers;
/*  251 */     this._parserFactory = f;
/*      */     
/*  253 */     this._valueType = base._valueType;
/*  254 */     this._rootDeserializer = base._rootDeserializer;
/*  255 */     this._valueToUpdate = base._valueToUpdate;
/*  256 */     this._schema = base._schema;
/*  257 */     this._injectableValues = base._injectableValues;
/*  258 */     this._unwrapRoot = base._unwrapRoot;
/*  259 */     this._dataFormatReaders = base._dataFormatReaders;
/*  260 */     this._filter = base._filter;
/*      */   }
/*      */   
/*      */   protected ObjectReader(ObjectReader base, TokenFilter filter) {
/*  264 */     this._config = base._config;
/*  265 */     this._context = base._context;
/*  266 */     this._rootDeserializers = base._rootDeserializers;
/*  267 */     this._parserFactory = base._parserFactory;
/*  268 */     this._valueType = base._valueType;
/*  269 */     this._rootDeserializer = base._rootDeserializer;
/*  270 */     this._valueToUpdate = base._valueToUpdate;
/*  271 */     this._schema = base._schema;
/*  272 */     this._injectableValues = base._injectableValues;
/*  273 */     this._unwrapRoot = base._unwrapRoot;
/*  274 */     this._dataFormatReaders = base._dataFormatReaders;
/*  275 */     this._filter = filter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Version version() {
/*  284 */     return PackageVersion.VERSION;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectReader _new(ObjectReader base, JsonFactory f) {
/*  301 */     return new ObjectReader(base, f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectReader _new(ObjectReader base, DeserializationConfig config) {
/*  310 */     return new ObjectReader(base, config);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectReader _new(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
/*  322 */     return new ObjectReader(base, config, valueType, rootDeser, valueToUpdate, schema, injectableValues, dataFormatReaders);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected <T> MappingIterator<T> _newIterator(JsonParser p, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean parserManaged) {
/*  335 */     return new MappingIterator<>(this._valueType, p, ctxt, deser, parserManaged, this._valueToUpdate);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonToken _initForReading(DeserializationContext ctxt, JsonParser p) throws IOException {
/*  348 */     this._config.initialize(p, this._schema);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  354 */     JsonToken t = p.currentToken();
/*  355 */     if (t == null) {
/*  356 */       t = p.nextToken();
/*  357 */       if (t == null)
/*      */       {
/*  359 */         ctxt.reportInputMismatch(this._valueType, "No content to map due to end-of-input", new Object[0]);
/*      */       }
/*      */     } 
/*      */     
/*  363 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _initForMultiRead(DeserializationContext ctxt, JsonParser p) throws IOException {
/*  378 */     this._config.initialize(p, this._schema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(DeserializationFeature feature) {
/*  392 */     return _with(this._config.with(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(DeserializationFeature first, DeserializationFeature... other) {
/*  402 */     return _with(this._config.with(first, other));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withFeatures(DeserializationFeature... features) {
/*  410 */     return _with(this._config.withFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader without(DeserializationFeature feature) {
/*  418 */     return _with(this._config.without(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader without(DeserializationFeature first, DeserializationFeature... other) {
/*  427 */     return _with(this._config.without(first, other));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withoutFeatures(DeserializationFeature... features) {
/*  435 */     return _with(this._config.withoutFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(JsonParser.Feature feature) {
/*  454 */     return _with(this._config.with(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withFeatures(JsonParser.Feature... features) {
/*  466 */     return _with(this._config.withFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader without(JsonParser.Feature feature) {
/*  478 */     return _with(this._config.without(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withoutFeatures(JsonParser.Feature... features) {
/*  490 */     return _with(this._config.withoutFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(StreamReadFeature feature) {
/*  508 */     return _with(this._config.with(feature.mappedFeature()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader without(StreamReadFeature feature) {
/*  520 */     return _with(this._config.without(feature.mappedFeature()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(FormatFeature feature) {
/*  536 */     return _with(this._config.with(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withFeatures(FormatFeature... features) {
/*  546 */     return _with(this._config.withFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader without(FormatFeature feature) {
/*  556 */     return _with(this._config.without(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withoutFeatures(FormatFeature... features) {
/*  566 */     return _with(this._config.withoutFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader at(String pointerExpr) {
/*  581 */     _assertNotNull("pointerExpr", pointerExpr);
/*  582 */     return new ObjectReader(this, (TokenFilter)new JsonPointerBasedFilter(pointerExpr));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader at(JsonPointer pointer) {
/*  591 */     _assertNotNull("pointer", pointer);
/*  592 */     return new ObjectReader(this, (TokenFilter)new JsonPointerBasedFilter(pointer));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(DeserializationConfig config) {
/*  603 */     return _with(config);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(InjectableValues injectableValues) {
/*  615 */     if (this._injectableValues == injectableValues) {
/*  616 */       return this;
/*      */     }
/*  618 */     return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, injectableValues, this._dataFormatReaders);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(JsonNodeFactory f) {
/*  632 */     return _with(this._config.with(f));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(JsonFactory f) {
/*  647 */     if (f == this._parserFactory) {
/*  648 */       return this;
/*      */     }
/*  650 */     ObjectReader r = _new(this, f);
/*      */     
/*  652 */     if (f.getCodec() == null) {
/*  653 */       f.setCodec(r);
/*      */     }
/*  655 */     return r;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withRootName(String rootName) {
/*  668 */     return _with((DeserializationConfig)this._config.withRootName(rootName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withRootName(PropertyName rootName) {
/*  675 */     return _with(this._config.withRootName(rootName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withoutRootName() {
/*  689 */     return _with(this._config.withRootName(PropertyName.NO_NAME));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(FormatSchema schema) {
/*  702 */     if (this._schema == schema) {
/*  703 */       return this;
/*      */     }
/*  705 */     _verifySchemaType(schema);
/*  706 */     return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, schema, this._injectableValues, this._dataFormatReaders);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader forType(JavaType valueType) {
/*  721 */     if (valueType != null && valueType.equals(this._valueType)) {
/*  722 */       return this;
/*      */     }
/*  724 */     JsonDeserializer<Object> rootDeser = _prefetchRootDeserializer(valueType);
/*      */     
/*  726 */     DataFormatReaders det = this._dataFormatReaders;
/*  727 */     if (det != null) {
/*  728 */       det = det.withType(valueType);
/*      */     }
/*  730 */     return _new(this, this._config, valueType, rootDeser, this._valueToUpdate, this._schema, this._injectableValues, det);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader forType(Class<?> valueType) {
/*  744 */     return forType(this._config.constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader forType(TypeReference<?> valueTypeRef) {
/*  757 */     return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectReader withType(JavaType valueType) {
/*  765 */     return forType(valueType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectReader withType(Class<?> valueType) {
/*  773 */     return forType(this._config.constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectReader withType(Type valueType) {
/*  781 */     return forType(this._config.getTypeFactory().constructType(valueType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectReader withType(TypeReference<?> valueTypeRef) {
/*  789 */     return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withValueToUpdate(Object value) {
/*      */     JavaType t;
/*  802 */     if (value == this._valueToUpdate) return this; 
/*  803 */     if (value == null)
/*      */     {
/*      */       
/*  806 */       return _new(this, this._config, this._valueType, this._rootDeserializer, null, this._schema, this._injectableValues, this._dataFormatReaders);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  815 */     if (this._valueType == null) {
/*  816 */       t = this._config.constructType(value.getClass());
/*      */     } else {
/*  818 */       t = this._valueType;
/*      */     } 
/*  820 */     return _new(this, this._config, t, this._rootDeserializer, value, this._schema, this._injectableValues, this._dataFormatReaders);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withView(Class<?> activeView) {
/*  832 */     return _with(this._config.withView(activeView));
/*      */   }
/*      */   
/*      */   public ObjectReader with(Locale l) {
/*  836 */     return _with((DeserializationConfig)this._config.with(l));
/*      */   }
/*      */   
/*      */   public ObjectReader with(TimeZone tz) {
/*  840 */     return _with((DeserializationConfig)this._config.with(tz));
/*      */   }
/*      */   
/*      */   public ObjectReader withHandler(DeserializationProblemHandler h) {
/*  844 */     return _with(this._config.withHandler(h));
/*      */   }
/*      */   
/*      */   public ObjectReader with(Base64Variant defaultBase64) {
/*  848 */     return _with((DeserializationConfig)this._config.with(defaultBase64));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withFormatDetection(ObjectReader... readers) {
/*  874 */     return withFormatDetection(new DataFormatReaders(readers));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withFormatDetection(DataFormatReaders readers) {
/*  893 */     return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, this._injectableValues, readers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader with(ContextAttributes attrs) {
/*  901 */     return _with(this._config.with(attrs));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withAttributes(Map<?, ?> attrs) {
/*  908 */     return _with((DeserializationConfig)this._config.withAttributes(attrs));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withAttribute(Object key, Object value) {
/*  915 */     return _with((DeserializationConfig)this._config.withAttribute(key, value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectReader withoutAttribute(Object key) {
/*  922 */     return _with((DeserializationConfig)this._config.withoutAttribute(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectReader _with(DeserializationConfig newConfig) {
/*  932 */     if (newConfig == this._config) {
/*  933 */       return this;
/*      */     }
/*  935 */     ObjectReader r = _new(this, newConfig);
/*  936 */     if (this._dataFormatReaders != null) {
/*  937 */       r = r.withFormatDetection(this._dataFormatReaders.with(newConfig));
/*      */     }
/*  939 */     return r;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(DeserializationFeature f) {
/*  949 */     return this._config.isEnabled(f);
/*      */   }
/*      */   
/*      */   public boolean isEnabled(MapperFeature f) {
/*  953 */     return this._config.isEnabled(f);
/*      */   }
/*      */   
/*      */   public boolean isEnabled(JsonParser.Feature f) {
/*  957 */     return this._config.isEnabled(f, this._parserFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(StreamReadFeature f) {
/*  964 */     return this._config.isEnabled(f.mappedFeature(), this._parserFactory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DeserializationConfig getConfig() {
/*  971 */     return this._config;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory getFactory() {
/*  979 */     return this._parserFactory;
/*      */   }
/*      */   
/*      */   public TypeFactory getTypeFactory() {
/*  983 */     return this._config.getTypeFactory();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ContextAttributes getAttributes() {
/*  990 */     return this._config.getAttributes();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InjectableValues getInjectableValues() {
/*  997 */     return this._injectableValues;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType getValueType() {
/* 1004 */     return this._valueType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/* 1022 */     _assertNotNull("src", src);
/* 1023 */     return this._config.initialize(this._parserFactory.createParser(src), this._schema);
/*      */   }
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
/* 1035 */     _assertNotNull("src", src);
/* 1036 */     return this._config.initialize(this._parserFactory.createParser(src), this._schema);
/*      */   }
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
/* 1048 */     _assertNotNull("in", in);
/* 1049 */     return this._config.initialize(this._parserFactory.createParser(in), this._schema);
/*      */   }
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
/* 1061 */     _assertNotNull("r", r);
/* 1062 */     return this._config.initialize(this._parserFactory.createParser(r), this._schema);
/*      */   }
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
/* 1074 */     _assertNotNull("content", content);
/* 1075 */     return this._config.initialize(this._parserFactory.createParser(content), this._schema);
/*      */   }
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
/* 1087 */     _assertNotNull("content", content);
/* 1088 */     return this._config.initialize(this._parserFactory.createParser(content, offset, len), this._schema);
/*      */   }
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
/* 1100 */     _assertNotNull("content", content);
/* 1101 */     return this._config.initialize(this._parserFactory.createParser(content), this._schema);
/*      */   }
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
/* 1113 */     _assertNotNull("content", content);
/* 1114 */     return this._config.initialize(this._parserFactory.createParser(content), this._schema);
/*      */   }
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
/* 1126 */     _assertNotNull("content", content);
/* 1127 */     return this._config.initialize(this._parserFactory.createParser(content, offset, len), this._schema);
/*      */   }
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
/* 1139 */     _assertNotNull("content", content);
/* 1140 */     return this._config.initialize(this._parserFactory.createParser(content), this._schema);
/*      */   }
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
/* 1152 */     return this._config.initialize(this._parserFactory.createNonBlockingByteArrayParser(), this._schema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(JsonParser p) throws IOException {
/* 1174 */     _assertNotNull("p", p);
/* 1175 */     return (T)_bind(p, this._valueToUpdate);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(JsonParser p, Class<T> valueType) throws IOException {
/* 1192 */     _assertNotNull("p", p);
/* 1193 */     return forType(valueType).readValue(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(JsonParser p, TypeReference<T> valueTypeRef) throws IOException {
/* 1210 */     _assertNotNull("p", p);
/* 1211 */     return forType(valueTypeRef).readValue(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(JsonParser p, ResolvedType valueType) throws IOException {
/* 1227 */     _assertNotNull("p", p);
/* 1228 */     return forType((JavaType)valueType).readValue(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(JsonParser p, JavaType valueType) throws IOException {
/* 1239 */     _assertNotNull("p", p);
/* 1240 */     return forType(valueType).readValue(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> Iterator<T> readValues(JsonParser p, Class<T> valueType) throws IOException {
/* 1264 */     _assertNotNull("p", p);
/* 1265 */     return forType(valueType).readValues(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> Iterator<T> readValues(JsonParser p, TypeReference<T> valueTypeRef) throws IOException {
/* 1289 */     _assertNotNull("p", p);
/* 1290 */     return forType(valueTypeRef).readValues(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> Iterator<T> readValues(JsonParser p, ResolvedType valueType) throws IOException {
/* 1314 */     _assertNotNull("p", p);
/* 1315 */     return readValues(p, (JavaType)valueType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> Iterator<T> readValues(JsonParser p, JavaType valueType) throws IOException {
/* 1338 */     _assertNotNull("p", p);
/* 1339 */     return forType(valueType).readValues(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode createArrayNode() {
/* 1350 */     return (JsonNode)this._config.getNodeFactory().arrayNode();
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonNode createObjectNode() {
/* 1355 */     return (JsonNode)this._config.getNodeFactory().objectNode();
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonNode missingNode() {
/* 1360 */     return this._config.getNodeFactory().missingNode();
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonNode nullNode() {
/* 1365 */     return (JsonNode)this._config.getNodeFactory().nullNode();
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonParser treeAsTokens(TreeNode n) {
/* 1370 */     _assertNotNull("n", n);
/*      */ 
/*      */     
/* 1373 */     ObjectReader codec = withValueToUpdate(null);
/* 1374 */     return (JsonParser)new TreeTraversingParser((JsonNode)n, codec);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/* 1398 */     _assertNotNull("p", p);
/* 1399 */     return (T)_bindAsTreeOrNull(p);
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeTree(JsonGenerator g, TreeNode rootNode) {
/* 1404 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(InputStream src) throws IOException {
/* 1424 */     if (this._dataFormatReaders != null) {
/* 1425 */       return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(src), false);
/*      */     }
/* 1427 */     return (T)_bindAndClose(_considerFilter(createParser(src), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
/* 1442 */     return forType(valueType).readValue(src);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(Reader src) throws IOException {
/* 1456 */     if (this._dataFormatReaders != null) {
/* 1457 */       _reportUndetectableSource(src);
/*      */     }
/* 1459 */     return (T)_bindAndClose(_considerFilter(createParser(src), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(Reader src, Class<T> valueType) throws IOException {
/* 1474 */     return forType(valueType).readValue(src);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(String src) throws JsonProcessingException, JsonMappingException {
/* 1488 */     if (this._dataFormatReaders != null) {
/* 1489 */       _reportUndetectableSource(src);
/*      */     }
/*      */     try {
/* 1492 */       return (T)_bindAndClose(_considerFilter(createParser(src), false));
/* 1493 */     } catch (JsonProcessingException e) {
/* 1494 */       throw e;
/* 1495 */     } catch (IOException e) {
/* 1496 */       throw JsonMappingException.fromUnexpectedIOE(e);
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
/*      */   public <T> T readValue(String src, Class<T> valueType) throws IOException {
/* 1512 */     return forType(valueType).readValue(src);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] content) throws IOException {
/* 1526 */     if (this._dataFormatReaders != null) {
/* 1527 */       return (T)_detectBindAndClose(content, 0, content.length);
/*      */     }
/* 1529 */     return (T)_bindAndClose(_considerFilter(createParser(content), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] content, Class<T> valueType) throws IOException {
/* 1544 */     return forType(valueType).readValue(content);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] buffer, int offset, int length) throws IOException {
/* 1560 */     if (this._dataFormatReaders != null) {
/* 1561 */       return (T)_detectBindAndClose(buffer, offset, length);
/*      */     }
/* 1563 */     return (T)_bindAndClose(_considerFilter(createParser(buffer, offset, length), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(byte[] buffer, int offset, int length, Class<T> valueType) throws IOException {
/* 1581 */     return forType(valueType).readValue(buffer, offset, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(File src) throws IOException {
/* 1595 */     if (this._dataFormatReaders != null) {
/* 1596 */       return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true);
/*      */     }
/*      */     
/* 1599 */     return (T)_bindAndClose(_considerFilter(createParser(src), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(File src, Class<T> valueType) throws IOException {
/* 1614 */     return forType(valueType).readValue(src);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(URL src) throws IOException {
/* 1633 */     if (this._dataFormatReaders != null) {
/* 1634 */       return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true);
/*      */     }
/* 1636 */     return (T)_bindAndClose(_considerFilter(createParser(src), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(URL src, Class<T> valueType) throws IOException {
/* 1651 */     return forType(valueType).readValue(src);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(JsonNode content) throws IOException {
/* 1666 */     _assertNotNull("content", content);
/* 1667 */     if (this._dataFormatReaders != null) {
/* 1668 */       _reportUndetectableSource(content);
/*      */     }
/* 1670 */     return (T)_bindAndClose(_considerFilter(treeAsTokens(content), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(JsonNode content, Class<T> valueType) throws IOException {
/* 1685 */     return forType(valueType).readValue(content);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(DataInput src) throws IOException {
/* 1691 */     if (this._dataFormatReaders != null) {
/* 1692 */       _reportUndetectableSource(src);
/*      */     }
/* 1694 */     return (T)_bindAndClose(_considerFilter(createParser(src), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T readValue(DataInput content, Class<T> valueType) throws IOException {
/* 1709 */     return forType(valueType).readValue(content);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(InputStream src) throws IOException {
/* 1737 */     if (this._dataFormatReaders != null) {
/* 1738 */       return _detectBindAndCloseAsTree(src);
/*      */     }
/* 1740 */     return _bindAndCloseAsTree(_considerFilter(createParser(src), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(Reader src) throws IOException {
/* 1749 */     if (this._dataFormatReaders != null) {
/* 1750 */       _reportUndetectableSource(src);
/*      */     }
/* 1752 */     return _bindAndCloseAsTree(_considerFilter(createParser(src), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(String json) throws JsonProcessingException, JsonMappingException {
/* 1761 */     if (this._dataFormatReaders != null) {
/* 1762 */       _reportUndetectableSource(json);
/*      */     }
/*      */     try {
/* 1765 */       return _bindAndCloseAsTree(_considerFilter(createParser(json), false));
/* 1766 */     } catch (JsonProcessingException e) {
/* 1767 */       throw e;
/* 1768 */     } catch (IOException e) {
/* 1769 */       throw JsonMappingException.fromUnexpectedIOE(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(byte[] json) throws IOException {
/* 1779 */     _assertNotNull("json", json);
/* 1780 */     if (this._dataFormatReaders != null) {
/* 1781 */       _reportUndetectableSource(json);
/*      */     }
/* 1783 */     return _bindAndCloseAsTree(_considerFilter(createParser(json), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(byte[] json, int offset, int len) throws IOException {
/* 1792 */     if (this._dataFormatReaders != null) {
/* 1793 */       _reportUndetectableSource(json);
/*      */     }
/* 1795 */     return _bindAndCloseAsTree(_considerFilter(createParser(json, offset, len), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonNode readTree(DataInput src) throws IOException {
/* 1804 */     if (this._dataFormatReaders != null) {
/* 1805 */       _reportUndetectableSource(src);
/*      */     }
/* 1807 */     return _bindAndCloseAsTree(_considerFilter(createParser(src), false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> MappingIterator<T> readValues(JsonParser p) throws IOException {
/* 1829 */     _assertNotNull("p", p);
/* 1830 */     DefaultDeserializationContext defaultDeserializationContext = createDeserializationContext(p);
/*      */     
/* 1832 */     return _newIterator(p, (DeserializationContext)defaultDeserializationContext, _findRootDeserializer((DeserializationContext)defaultDeserializationContext), false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> MappingIterator<T> readValues(InputStream src) throws IOException {
/* 1857 */     if (this._dataFormatReaders != null) {
/* 1858 */       return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src), false);
/*      */     }
/*      */     
/* 1861 */     return _bindAndReadValues(_considerFilter(createParser(src), true));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> MappingIterator<T> readValues(Reader src) throws IOException {
/* 1869 */     if (this._dataFormatReaders != null) {
/* 1870 */       _reportUndetectableSource(src);
/*      */     }
/* 1872 */     JsonParser p = _considerFilter(createParser(src), true);
/* 1873 */     DefaultDeserializationContext defaultDeserializationContext = createDeserializationContext(p);
/* 1874 */     _initForMultiRead((DeserializationContext)defaultDeserializationContext, p);
/* 1875 */     p.nextToken();
/* 1876 */     return _newIterator(p, (DeserializationContext)defaultDeserializationContext, _findRootDeserializer((DeserializationContext)defaultDeserializationContext), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> MappingIterator<T> readValues(String json) throws IOException {
/* 1886 */     if (this._dataFormatReaders != null) {
/* 1887 */       _reportUndetectableSource(json);
/*      */     }
/* 1889 */     JsonParser p = _considerFilter(createParser(json), true);
/* 1890 */     DefaultDeserializationContext defaultDeserializationContext = createDeserializationContext(p);
/* 1891 */     _initForMultiRead((DeserializationContext)defaultDeserializationContext, p);
/* 1892 */     p.nextToken();
/* 1893 */     return _newIterator(p, (DeserializationContext)defaultDeserializationContext, _findRootDeserializer((DeserializationContext)defaultDeserializationContext), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> MappingIterator<T> readValues(byte[] src, int offset, int length) throws IOException {
/* 1901 */     if (this._dataFormatReaders != null) {
/* 1902 */       return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src, offset, length), false);
/*      */     }
/* 1904 */     return _bindAndReadValues(_considerFilter(createParser(src, offset, length), true));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final <T> MappingIterator<T> readValues(byte[] src) throws IOException {
/* 1912 */     _assertNotNull("src", src);
/* 1913 */     return readValues(src, 0, src.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> MappingIterator<T> readValues(File src) throws IOException {
/* 1921 */     if (this._dataFormatReaders != null) {
/* 1922 */       return _detectBindAndReadValues(this._dataFormatReaders
/* 1923 */           .findFormat(_inputStream(src)), false);
/*      */     }
/* 1925 */     return _bindAndReadValues(_considerFilter(createParser(src), true));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> MappingIterator<T> readValues(URL src) throws IOException {
/* 1941 */     if (this._dataFormatReaders != null) {
/* 1942 */       return _detectBindAndReadValues(this._dataFormatReaders
/* 1943 */           .findFormat(_inputStream(src)), true);
/*      */     }
/* 1945 */     return _bindAndReadValues(_considerFilter(createParser(src), true));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> MappingIterator<T> readValues(DataInput src) throws IOException {
/* 1953 */     if (this._dataFormatReaders != null) {
/* 1954 */       _reportUndetectableSource(src);
/*      */     }
/* 1956 */     return _bindAndReadValues(_considerFilter(createParser(src), true));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
/* 1968 */     _assertNotNull("n", n);
/*      */     try {
/* 1970 */       return readValue(treeAsTokens(n), valueType);
/* 1971 */     } catch (JsonProcessingException e) {
/* 1972 */       throw e;
/* 1973 */     } catch (IOException e) {
/* 1974 */       throw JsonMappingException.fromUnexpectedIOE(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T treeToValue(TreeNode n, JavaType valueType) throws JsonProcessingException {
/* 1985 */     _assertNotNull("n", n);
/*      */     try {
/* 1987 */       return readValue(treeAsTokens(n), valueType);
/* 1988 */     } catch (JsonProcessingException e) {
/* 1989 */       throw e;
/* 1990 */     } catch (IOException e) {
/* 1991 */       throw JsonMappingException.fromUnexpectedIOE(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeValue(JsonGenerator gen, Object value) throws IOException {
/* 1997 */     throw new UnsupportedOperationException("Not implemented for ObjectReader");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object _bind(JsonParser p, Object valueToUpdate) throws IOException {
/*      */     Object result;
/* 2014 */     DefaultDeserializationContext ctxt = createDeserializationContext(p);
/* 2015 */     JsonToken t = _initForReading((DeserializationContext)ctxt, p);
/* 2016 */     if (t == JsonToken.VALUE_NULL) {
/* 2017 */       if (valueToUpdate == null) {
/* 2018 */         result = _findRootDeserializer((DeserializationContext)ctxt).getNullValue((DeserializationContext)ctxt);
/*      */       } else {
/* 2020 */         result = valueToUpdate;
/*      */       } 
/* 2022 */     } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/* 2023 */       result = valueToUpdate;
/*      */     } else {
/* 2025 */       result = ctxt.readRootValue(p, this._valueType, _findRootDeserializer((DeserializationContext)ctxt), this._valueToUpdate);
/*      */     } 
/*      */     
/* 2028 */     p.clearCurrentToken();
/* 2029 */     if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/* 2030 */       _verifyNoTrailingTokens(p, (DeserializationContext)ctxt, this._valueType);
/*      */     }
/* 2032 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object _bindAndClose(JsonParser p0) throws IOException {
/* 2037 */     try (JsonParser p = p0) {
/*      */       Object result;
/*      */       
/* 2040 */       DefaultDeserializationContext ctxt = createDeserializationContext(p);
/* 2041 */       JsonToken t = _initForReading((DeserializationContext)ctxt, p);
/* 2042 */       if (t == JsonToken.VALUE_NULL) {
/* 2043 */         if (this._valueToUpdate == null) {
/* 2044 */           result = _findRootDeserializer((DeserializationContext)ctxt).getNullValue((DeserializationContext)ctxt);
/*      */         } else {
/* 2046 */           result = this._valueToUpdate;
/*      */         } 
/* 2048 */       } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/* 2049 */         result = this._valueToUpdate;
/*      */       } else {
/* 2051 */         result = ctxt.readRootValue(p, this._valueType, _findRootDeserializer((DeserializationContext)ctxt), this._valueToUpdate);
/*      */       } 
/*      */       
/* 2054 */       if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/* 2055 */         _verifyNoTrailingTokens(p, (DeserializationContext)ctxt, this._valueType);
/*      */       }
/* 2057 */       return result;
/*      */     } 
/*      */   }
/*      */   
/*      */   protected final JsonNode _bindAndCloseAsTree(JsonParser p0) throws IOException {
/* 2062 */     try (JsonParser p = p0) {
/* 2063 */       return _bindAsTree(p);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonNode _bindAsTree(JsonParser p) throws IOException {
/*      */     JsonNode resultNode;
/* 2071 */     if (this._valueToUpdate != null) {
/* 2072 */       return (JsonNode)_bind(p, this._valueToUpdate);
/*      */     }
/*      */ 
/*      */     
/* 2076 */     this._config.initialize(p);
/* 2077 */     if (this._schema != null) {
/* 2078 */       p.setSchema(this._schema);
/*      */     }
/*      */     
/* 2081 */     JsonToken t = p.currentToken();
/* 2082 */     if (t == null) {
/* 2083 */       t = p.nextToken();
/* 2084 */       if (t == null) {
/* 2085 */         return this._config.getNodeFactory().missingNode();
/*      */       }
/*      */     } 
/* 2088 */     DefaultDeserializationContext ctxt = createDeserializationContext(p);
/*      */ 
/*      */     
/* 2091 */     if (t == JsonToken.VALUE_NULL) {
/* 2092 */       NullNode nullNode = this._config.getNodeFactory().nullNode();
/*      */     } else {
/*      */       
/* 2095 */       resultNode = (JsonNode)ctxt.readRootValue(p, _jsonNodeType(), _findTreeDeserializer((DeserializationContext)ctxt), null);
/*      */     } 
/*      */     
/* 2098 */     p.clearCurrentToken();
/* 2099 */     if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/* 2100 */       _verifyNoTrailingTokens(p, (DeserializationContext)ctxt, _jsonNodeType());
/*      */     }
/* 2102 */     return resultNode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonNode _bindAsTreeOrNull(JsonParser p) throws IOException {
/*      */     JsonNode resultNode;
/* 2113 */     if (this._valueToUpdate != null) {
/* 2114 */       return (JsonNode)_bind(p, this._valueToUpdate);
/*      */     }
/*      */ 
/*      */     
/* 2118 */     this._config.initialize(p);
/* 2119 */     if (this._schema != null) {
/* 2120 */       p.setSchema(this._schema);
/*      */     }
/* 2122 */     JsonToken t = p.currentToken();
/* 2123 */     if (t == null) {
/* 2124 */       t = p.nextToken();
/* 2125 */       if (t == null) {
/* 2126 */         return null;
/*      */       }
/*      */     } 
/* 2129 */     DefaultDeserializationContext ctxt = createDeserializationContext(p);
/*      */     
/* 2131 */     if (t == JsonToken.VALUE_NULL) {
/* 2132 */       NullNode nullNode = this._config.getNodeFactory().nullNode();
/*      */     } else {
/*      */       
/* 2135 */       resultNode = (JsonNode)ctxt.readRootValue(p, _jsonNodeType(), _findTreeDeserializer((DeserializationContext)ctxt), null);
/*      */     } 
/*      */     
/* 2138 */     p.clearCurrentToken();
/* 2139 */     if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/* 2140 */       _verifyNoTrailingTokens(p, (DeserializationContext)ctxt, _jsonNodeType());
/*      */     }
/* 2142 */     return resultNode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected <T> MappingIterator<T> _bindAndReadValues(JsonParser p) throws IOException {
/* 2150 */     DefaultDeserializationContext defaultDeserializationContext = createDeserializationContext(p);
/* 2151 */     _initForMultiRead((DeserializationContext)defaultDeserializationContext, p);
/* 2152 */     p.nextToken();
/* 2153 */     return _newIterator(p, (DeserializationContext)defaultDeserializationContext, _findRootDeserializer((DeserializationContext)defaultDeserializationContext), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonParser _considerFilter(JsonParser p, boolean multiValue) {
/* 2162 */     return (this._filter == null || FilteringParserDelegate.class.isInstance(p)) ? p : (JsonParser)new FilteringParserDelegate(p, this._filter, TokenFilter.Inclusion.ONLY_INCLUDE_ALL, multiValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType) throws IOException {
/* 2173 */     JsonToken t = p.nextToken();
/* 2174 */     if (t != null) {
/* 2175 */       Class<?> bt = ClassUtil.rawClass(bindType);
/* 2176 */       if (bt == null && 
/* 2177 */         this._valueToUpdate != null) {
/* 2178 */         bt = this._valueToUpdate.getClass();
/*      */       }
/*      */       
/* 2181 */       ctxt.reportTrailingTokens(bt, p, t);
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
/*      */   protected Object _detectBindAndClose(byte[] src, int offset, int length) throws IOException {
/* 2193 */     DataFormatReaders.Match match = this._dataFormatReaders.findFormat(src, offset, length);
/* 2194 */     if (!match.hasMatch()) {
/* 2195 */       _reportUnkownFormat(this._dataFormatReaders, match);
/*      */     }
/* 2197 */     JsonParser p = match.createParserWithMatch();
/* 2198 */     return match.getReader()._bindAndClose(p);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object _detectBindAndClose(DataFormatReaders.Match match, boolean forceClosing) throws IOException {
/* 2204 */     if (!match.hasMatch()) {
/* 2205 */       _reportUnkownFormat(this._dataFormatReaders, match);
/*      */     }
/* 2207 */     JsonParser p = match.createParserWithMatch();
/*      */ 
/*      */     
/* 2210 */     if (forceClosing) {
/* 2211 */       p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
/*      */     }
/*      */     
/* 2214 */     return match.getReader()._bindAndClose(p);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected <T> MappingIterator<T> _detectBindAndReadValues(DataFormatReaders.Match match, boolean forceClosing) throws IOException {
/* 2220 */     if (!match.hasMatch()) {
/* 2221 */       _reportUnkownFormat(this._dataFormatReaders, match);
/*      */     }
/* 2223 */     JsonParser p = match.createParserWithMatch();
/*      */ 
/*      */     
/* 2226 */     if (forceClosing) {
/* 2227 */       p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
/*      */     }
/*      */     
/* 2230 */     return match.getReader()._bindAndReadValues(p);
/*      */   }
/*      */ 
/*      */   
/*      */   protected JsonNode _detectBindAndCloseAsTree(InputStream in) throws IOException {
/* 2235 */     DataFormatReaders.Match match = this._dataFormatReaders.findFormat(in);
/* 2236 */     if (!match.hasMatch()) {
/* 2237 */       _reportUnkownFormat(this._dataFormatReaders, match);
/*      */     }
/* 2239 */     JsonParser p = match.createParserWithMatch();
/* 2240 */     p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
/* 2241 */     return match.getReader()._bindAndCloseAsTree(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportUnkownFormat(DataFormatReaders detector, DataFormatReaders.Match match) throws IOException {
/* 2252 */     throw new JsonParseException(null, "Cannot detect format from input, does not look like any of detectable formats " + detector
/* 2253 */         .toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _verifySchemaType(FormatSchema schema) {
/* 2267 */     if (schema != null && 
/* 2268 */       !this._parserFactory.canUseSchema(schema)) {
/* 2269 */       throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._parserFactory
/* 2270 */           .getFormatName());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DefaultDeserializationContext createDeserializationContext(JsonParser p) {
/* 2281 */     return this._context.createInstance(this._config, p, this._injectableValues);
/*      */   }
/*      */ 
/*      */   
/*      */   protected DefaultDeserializationContext createDummyDeserializationContext() {
/* 2286 */     return this._context.createDummyInstance(this._config);
/*      */   }
/*      */   
/*      */   protected InputStream _inputStream(URL src) throws IOException {
/* 2290 */     return src.openStream();
/*      */   }
/*      */   
/*      */   protected InputStream _inputStream(File f) throws IOException {
/* 2294 */     return new FileInputStream(f);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportUndetectableSource(Object src) throws StreamReadException {
/* 2300 */     throw new JsonParseException(null, "Cannot use source of type " + src
/* 2301 */         .getClass().getName() + " with format auto-detection: must be byte- not char-based");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt) throws DatabindException {
/* 2316 */     if (this._rootDeserializer != null) {
/* 2317 */       return this._rootDeserializer;
/*      */     }
/*      */ 
/*      */     
/* 2321 */     JavaType t = this._valueType;
/* 2322 */     if (t == null) {
/* 2323 */       ctxt.reportBadDefinition((JavaType)null, "No value type configured for ObjectReader");
/*      */     }
/*      */ 
/*      */     
/* 2327 */     JsonDeserializer<Object> deser = this._rootDeserializers.get(t);
/* 2328 */     if (deser != null) {
/* 2329 */       return deser;
/*      */     }
/*      */     
/* 2332 */     deser = ctxt.findRootValueDeserializer(t);
/* 2333 */     if (deser == null) {
/* 2334 */       ctxt.reportBadDefinition(t, "Cannot find a deserializer for type " + t);
/*      */     }
/* 2336 */     this._rootDeserializers.put(t, deser);
/* 2337 */     return deser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _findTreeDeserializer(DeserializationContext ctxt) throws DatabindException {
/* 2346 */     JavaType nodeType = _jsonNodeType();
/* 2347 */     JsonDeserializer<Object> deser = this._rootDeserializers.get(nodeType);
/* 2348 */     if (deser == null) {
/*      */       
/* 2350 */       deser = ctxt.findRootValueDeserializer(nodeType);
/* 2351 */       if (deser == null) {
/* 2352 */         ctxt.reportBadDefinition(nodeType, "Cannot find a deserializer for type " + nodeType);
/*      */       }
/*      */       
/* 2355 */       this._rootDeserializers.put(nodeType, deser);
/*      */     } 
/* 2357 */     return deser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonDeserializer<Object> _prefetchRootDeserializer(JavaType valueType) {
/* 2367 */     if (valueType == null || !this._config.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH)) {
/* 2368 */       return null;
/*      */     }
/*      */     
/* 2371 */     JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
/* 2372 */     if (deser == null) {
/*      */       
/*      */       try {
/* 2375 */         DefaultDeserializationContext defaultDeserializationContext = createDummyDeserializationContext();
/* 2376 */         deser = defaultDeserializationContext.findRootValueDeserializer(valueType);
/* 2377 */         if (deser != null) {
/* 2378 */           this._rootDeserializers.put(valueType, deser);
/*      */         }
/* 2380 */         return deser;
/* 2381 */       } catch (JacksonException jacksonException) {}
/*      */     }
/*      */ 
/*      */     
/* 2385 */     return deser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JavaType _jsonNodeType() {
/* 2392 */     JavaType t = this._jsonNodeType;
/* 2393 */     if (t == null) {
/* 2394 */       t = getTypeFactory().constructType(JsonNode.class);
/* 2395 */       this._jsonNodeType = t;
/*      */     } 
/* 2397 */     return t;
/*      */   }
/*      */   
/*      */   protected final void _assertNotNull(String paramName, Object src) {
/* 2401 */     if (src == null)
/* 2402 */       throw new IllegalArgumentException(String.format("argument \"%s\" is null", new Object[] { paramName })); 
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ObjectReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */