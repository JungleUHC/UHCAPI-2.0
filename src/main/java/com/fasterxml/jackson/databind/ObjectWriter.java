/*      */ package com.fasterxml.jackson.databind;
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.FormatFeature;
/*      */ import com.fasterxml.jackson.core.FormatSchema;
/*      */ import com.fasterxml.jackson.core.JsonEncoding;
/*      */ import com.fasterxml.jackson.core.JsonFactory;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonProcessingException;
/*      */ import com.fasterxml.jackson.core.PrettyPrinter;
/*      */ import com.fasterxml.jackson.core.SerializableString;
/*      */ import com.fasterxml.jackson.core.StreamWriteFeature;
/*      */ import com.fasterxml.jackson.core.exc.StreamWriteException;
/*      */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*      */ import com.fasterxml.jackson.core.io.SegmentedStringWriter;
/*      */ import com.fasterxml.jackson.core.io.SerializedString;
/*      */ import com.fasterxml.jackson.core.type.TypeReference;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import com.fasterxml.jackson.core.util.Instantiatable;
/*      */ import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
/*      */ import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*      */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*      */ import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
/*      */ import com.fasterxml.jackson.databind.ser.FilterProvider;
/*      */ import com.fasterxml.jackson.databind.ser.SerializerFactory;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*      */ import java.io.Closeable;
/*      */ import java.io.DataOutput;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.io.Writer;
/*      */ import java.text.DateFormat;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ 
/*      */ public class ObjectWriter implements Versioned, Serializable {
/*   43 */   protected static final PrettyPrinter NULL_PRETTY_PRINTER = (PrettyPrinter)new MinimalPrettyPrinter();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final long serialVersionUID = 1L;
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
/*      */   protected final DefaultSerializerProvider _serializerProvider;
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
/*      */   protected final JsonFactory _generatorFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final GeneratorSettings _generatorSettings;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final Prefetch _prefetch;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter(ObjectMapper mapper, SerializationConfig config, JavaType rootType, PrettyPrinter pp) {
/*  101 */     this._config = config;
/*  102 */     this._serializerProvider = mapper._serializerProvider;
/*  103 */     this._serializerFactory = mapper._serializerFactory;
/*  104 */     this._generatorFactory = mapper._jsonFactory;
/*  105 */     this._generatorSettings = (pp == null) ? GeneratorSettings.empty : new GeneratorSettings(pp, null, null, null);
/*      */ 
/*      */     
/*  108 */     if (rootType == null) {
/*  109 */       this._prefetch = Prefetch.empty;
/*  110 */     } else if (rootType.hasRawClass(Object.class)) {
/*      */ 
/*      */       
/*  113 */       this._prefetch = Prefetch.empty.forRootType(this, rootType);
/*      */     } else {
/*  115 */       this._prefetch = Prefetch.empty.forRootType(this, rootType.withStaticTyping());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter(ObjectMapper mapper, SerializationConfig config) {
/*  124 */     this._config = config;
/*  125 */     this._serializerProvider = mapper._serializerProvider;
/*  126 */     this._serializerFactory = mapper._serializerFactory;
/*  127 */     this._generatorFactory = mapper._jsonFactory;
/*      */     
/*  129 */     this._generatorSettings = GeneratorSettings.empty;
/*  130 */     this._prefetch = Prefetch.empty;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter(ObjectMapper mapper, SerializationConfig config, FormatSchema s) {
/*  139 */     this._config = config;
/*      */     
/*  141 */     this._serializerProvider = mapper._serializerProvider;
/*  142 */     this._serializerFactory = mapper._serializerFactory;
/*  143 */     this._generatorFactory = mapper._jsonFactory;
/*      */     
/*  145 */     this._generatorSettings = (s == null) ? GeneratorSettings.empty : new GeneratorSettings(null, s, null, null);
/*      */     
/*  147 */     this._prefetch = Prefetch.empty;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter(ObjectWriter base, SerializationConfig config, GeneratorSettings genSettings, Prefetch prefetch) {
/*  156 */     this._config = config;
/*      */     
/*  158 */     this._serializerProvider = base._serializerProvider;
/*  159 */     this._serializerFactory = base._serializerFactory;
/*  160 */     this._generatorFactory = base._generatorFactory;
/*      */     
/*  162 */     this._generatorSettings = genSettings;
/*  163 */     this._prefetch = prefetch;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter(ObjectWriter base, SerializationConfig config) {
/*  171 */     this._config = config;
/*      */     
/*  173 */     this._serializerProvider = base._serializerProvider;
/*  174 */     this._serializerFactory = base._serializerFactory;
/*  175 */     this._generatorFactory = base._generatorFactory;
/*      */     
/*  177 */     this._generatorSettings = base._generatorSettings;
/*  178 */     this._prefetch = base._prefetch;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter(ObjectWriter base, JsonFactory f) {
/*  187 */     this
/*  188 */       ._config = (SerializationConfig)base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
/*      */     
/*  190 */     this._serializerProvider = base._serializerProvider;
/*  191 */     this._serializerFactory = base._serializerFactory;
/*  192 */     this._generatorFactory = f;
/*      */     
/*  194 */     this._generatorSettings = base._generatorSettings;
/*  195 */     this._prefetch = base._prefetch;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Version version() {
/*  204 */     return PackageVersion.VERSION;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter _new(ObjectWriter base, JsonFactory f) {
/*  219 */     return new ObjectWriter(base, f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter _new(ObjectWriter base, SerializationConfig config) {
/*  228 */     if (config == this._config) {
/*  229 */       return this;
/*      */     }
/*  231 */     return new ObjectWriter(base, config);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectWriter _new(GeneratorSettings genSettings, Prefetch prefetch) {
/*  242 */     if (this._generatorSettings == genSettings && this._prefetch == prefetch) {
/*  243 */       return this;
/*      */     }
/*  245 */     return new ObjectWriter(this, this._config, genSettings, prefetch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SequenceWriter _newSequenceWriter(boolean wrapInArray, JsonGenerator gen, boolean managedInput) throws IOException {
/*  259 */     return (new SequenceWriter(_serializerProvider(), 
/*  260 */         _configureGenerator(gen), managedInput, this._prefetch))
/*  261 */       .init(wrapInArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(SerializationFeature feature) {
/*  275 */     return _new(this, this._config.with(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(SerializationFeature first, SerializationFeature... other) {
/*  283 */     return _new(this, this._config.with(first, other));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withFeatures(SerializationFeature... features) {
/*  291 */     return _new(this, this._config.withFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter without(SerializationFeature feature) {
/*  299 */     return _new(this, this._config.without(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter without(SerializationFeature first, SerializationFeature... other) {
/*  307 */     return _new(this, this._config.without(first, other));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withoutFeatures(SerializationFeature... features) {
/*  315 */     return _new(this, this._config.withoutFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(JsonGenerator.Feature feature) {
/*  328 */     return _new(this, this._config.with(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withFeatures(JsonGenerator.Feature... features) {
/*  335 */     return _new(this, this._config.withFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter without(JsonGenerator.Feature feature) {
/*  342 */     return _new(this, this._config.without(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withoutFeatures(JsonGenerator.Feature... features) {
/*  349 */     return _new(this, this._config.withoutFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(StreamWriteFeature feature) {
/*  362 */     return _new(this, this._config.with(feature.mappedFeature()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter without(StreamWriteFeature feature) {
/*  369 */     return _new(this, this._config.without(feature.mappedFeature()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(FormatFeature feature) {
/*  382 */     return _new(this, this._config.with(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withFeatures(FormatFeature... features) {
/*  389 */     return _new(this, this._config.withFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter without(FormatFeature feature) {
/*  396 */     return _new(this, this._config.without(feature));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withoutFeatures(FormatFeature... features) {
/*  403 */     return _new(this, this._config.withoutFeatures(features));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter forType(JavaType rootType) {
/*  423 */     return _new(this._generatorSettings, this._prefetch.forRootType(this, rootType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter forType(Class<?> rootType) {
/*  434 */     return forType(this._config.constructType(rootType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter forType(TypeReference<?> rootType) {
/*  445 */     return forType(this._config.getTypeFactory().constructType(rootType.getType()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectWriter withType(JavaType rootType) {
/*  453 */     return forType(rootType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectWriter withType(Class<?> rootType) {
/*  461 */     return forType(rootType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectWriter withType(TypeReference<?> rootType) {
/*  469 */     return forType(rootType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(DateFormat df) {
/*  487 */     return _new(this, this._config.with(df));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withDefaultPrettyPrinter() {
/*  495 */     return with(this._config.getDefaultPrettyPrinter());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(FilterProvider filterProvider) {
/*  503 */     if (filterProvider == this._config.getFilterProvider()) {
/*  504 */       return this;
/*      */     }
/*  506 */     return _new(this, this._config.withFilters(filterProvider));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(PrettyPrinter pp) {
/*  514 */     return _new(this._generatorSettings.with(pp), this._prefetch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withRootName(String rootName) {
/*  529 */     return _new(this, (SerializationConfig)this._config.withRootName(rootName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withRootName(PropertyName rootName) {
/*  536 */     return _new(this, this._config.withRootName(rootName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withoutRootName() {
/*  550 */     return _new(this, this._config.withRootName(PropertyName.NO_NAME));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(FormatSchema schema) {
/*  561 */     _verifySchemaType(schema);
/*  562 */     return _new(this._generatorSettings.with(schema), this._prefetch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public ObjectWriter withSchema(FormatSchema schema) {
/*  570 */     return with(schema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withView(Class<?> view) {
/*  582 */     return _new(this, this._config.withView(view));
/*      */   }
/*      */   
/*      */   public ObjectWriter with(Locale l) {
/*  586 */     return _new(this, (SerializationConfig)this._config.with(l));
/*      */   }
/*      */   
/*      */   public ObjectWriter with(TimeZone tz) {
/*  590 */     return _new(this, (SerializationConfig)this._config.with(tz));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(Base64Variant b64variant) {
/*  600 */     return _new(this, (SerializationConfig)this._config.with(b64variant));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(CharacterEscapes escapes) {
/*  607 */     return _new(this._generatorSettings.with(escapes), this._prefetch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(JsonFactory f) {
/*  614 */     return (f == this._generatorFactory) ? this : _new(this, f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter with(ContextAttributes attrs) {
/*  621 */     return _new(this, this._config.with(attrs));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withAttributes(Map<?, ?> attrs) {
/*  631 */     return _new(this, (SerializationConfig)this._config.withAttributes(attrs));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withAttribute(Object key, Object value) {
/*  638 */     return _new(this, (SerializationConfig)this._config.withAttribute(key, value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withoutAttribute(Object key) {
/*  645 */     return _new(this, (SerializationConfig)this._config.withoutAttribute(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withRootValueSeparator(String sep) {
/*  652 */     return _new(this._generatorSettings.withRootValueSeparator(sep), this._prefetch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWriter withRootValueSeparator(SerializableString sep) {
/*  659 */     return _new(this._generatorSettings.withRootValueSeparator(sep), this._prefetch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*  677 */     _assertNotNull("out", out);
/*  678 */     return _configureGenerator(this._generatorFactory.createGenerator(out, JsonEncoding.UTF8));
/*      */   }
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
/*  690 */     _assertNotNull("out", out);
/*  691 */     return _configureGenerator(this._generatorFactory.createGenerator(out, enc));
/*      */   }
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
/*  703 */     _assertNotNull("w", w);
/*  704 */     return _configureGenerator(this._generatorFactory.createGenerator(w));
/*      */   }
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
/*  716 */     _assertNotNull("outputFile", outputFile);
/*  717 */     return _configureGenerator(this._generatorFactory.createGenerator(outputFile, enc));
/*      */   }
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
/*  729 */     _assertNotNull("out", out);
/*  730 */     return _configureGenerator(this._generatorFactory.createGenerator(out));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValues(File out) throws IOException {
/*  753 */     return _newSequenceWriter(false, createGenerator(out, JsonEncoding.UTF8), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValues(JsonGenerator g) throws IOException {
/*  772 */     _assertNotNull("g", g);
/*  773 */     return _newSequenceWriter(false, _configureGenerator(g), false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValues(Writer out) throws IOException {
/*  790 */     return _newSequenceWriter(false, createGenerator(out), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValues(OutputStream out) throws IOException {
/*  807 */     return _newSequenceWriter(false, createGenerator(out, JsonEncoding.UTF8), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValues(DataOutput out) throws IOException {
/*  814 */     return _newSequenceWriter(false, createGenerator(out), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValuesAsArray(File out) throws IOException {
/*  833 */     return _newSequenceWriter(true, createGenerator(out, JsonEncoding.UTF8), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValuesAsArray(JsonGenerator gen) throws IOException {
/*  853 */     _assertNotNull("gen", gen);
/*  854 */     return _newSequenceWriter(true, gen, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValuesAsArray(Writer out) throws IOException {
/*  873 */     return _newSequenceWriter(true, createGenerator(out), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValuesAsArray(OutputStream out) throws IOException {
/*  892 */     return _newSequenceWriter(true, createGenerator(out, JsonEncoding.UTF8), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SequenceWriter writeValuesAsArray(DataOutput out) throws IOException {
/*  899 */     return _newSequenceWriter(true, createGenerator(out), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(SerializationFeature f) {
/*  909 */     return this._config.isEnabled(f);
/*      */   }
/*      */   
/*      */   public boolean isEnabled(MapperFeature f) {
/*  913 */     return this._config.isEnabled(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public boolean isEnabled(JsonParser.Feature f) {
/*  921 */     return this._generatorFactory.isEnabled(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(JsonGenerator.Feature f) {
/*  928 */     return this._generatorFactory.isEnabled(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(StreamWriteFeature f) {
/*  935 */     return this._generatorFactory.isEnabled(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SerializationConfig getConfig() {
/*  942 */     return this._config;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory getFactory() {
/*  949 */     return this._generatorFactory;
/*      */   }
/*      */   
/*      */   public TypeFactory getTypeFactory() {
/*  953 */     return this._config.getTypeFactory();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasPrefetchedSerializer() {
/*  965 */     return this._prefetch.hasSerializer();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ContextAttributes getAttributes() {
/*  972 */     return this._config.getAttributes();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeValue(JsonGenerator g, Object value) throws IOException {
/*  990 */     _assertNotNull("g", g);
/*  991 */     _configureGenerator(g);
/*  992 */     if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
/*      */ 
/*      */       
/*  995 */       Closeable toClose = (Closeable)value;
/*      */       try {
/*  997 */         this._prefetch.serialize(g, value, _serializerProvider());
/*  998 */         if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/*  999 */           g.flush();
/*      */         }
/* 1001 */       } catch (Exception e) {
/* 1002 */         ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, e);
/*      */         return;
/*      */       } 
/* 1005 */       toClose.close();
/*      */     } else {
/* 1007 */       this._prefetch.serialize(g, value, _serializerProvider());
/* 1008 */       if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/* 1009 */         g.flush();
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
/*      */   public void writeValue(File resultFile, Object value) throws IOException, StreamWriteException, DatabindException {
/* 1027 */     _writeValueAndClose(createGenerator(resultFile, JsonEncoding.UTF8), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/* 1044 */     _writeValueAndClose(createGenerator(out, JsonEncoding.UTF8), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/* 1060 */     _writeValueAndClose(createGenerator(w), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeValue(DataOutput out, Object value) throws IOException, StreamWriteException, DatabindException {
/* 1069 */     _writeValueAndClose(createGenerator(out), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
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
/* 1084 */     SegmentedStringWriter sw = new SegmentedStringWriter(this._generatorFactory._getBufferRecycler());
/*      */     try {
/* 1086 */       _writeValueAndClose(createGenerator((Writer)sw), value);
/* 1087 */     } catch (JsonProcessingException e) {
/* 1088 */       throw e;
/* 1089 */     } catch (IOException e) {
/* 1090 */       throw JsonMappingException.fromUnexpectedIOE(e);
/*      */     } 
/* 1092 */     return sw.getAndClear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/* 1108 */     try (ByteArrayBuilder bb = new ByteArrayBuilder(this._generatorFactory._getBufferRecycler())) {
/* 1109 */       _writeValueAndClose(createGenerator((OutputStream)bb, JsonEncoding.UTF8), value);
/* 1110 */       byte[] result = bb.toByteArray();
/* 1111 */       bb.release();
/* 1112 */       return result;
/* 1113 */     } catch (JsonProcessingException e) {
/* 1114 */       throw e;
/* 1115 */     } catch (IOException e) {
/* 1116 */       throw JsonMappingException.fromUnexpectedIOE(e);
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
/*      */   public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
/* 1141 */     _assertNotNull("type", type);
/* 1142 */     _assertNotNull("visitor", visitor);
/* 1143 */     _serializerProvider().acceptJsonFormatVisitor(type, visitor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
/* 1152 */     _assertNotNull("type", type);
/* 1153 */     _assertNotNull("visitor", visitor);
/* 1154 */     acceptJsonFormatVisitor(this._config.constructType(type), visitor);
/*      */   }
/*      */   
/*      */   public boolean canSerialize(Class<?> type) {
/* 1158 */     _assertNotNull("type", type);
/* 1159 */     return _serializerProvider().hasSerializerFor(type, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause) {
/* 1169 */     _assertNotNull("type", type);
/* 1170 */     return _serializerProvider().hasSerializerFor(type, cause);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DefaultSerializerProvider _serializerProvider() {
/* 1184 */     return this._serializerProvider.createInstance(this._config, this._serializerFactory);
/*      */   }
/*      */ 
/*      */ 
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
/* 1198 */     if (schema != null && 
/* 1199 */       !this._generatorFactory.canUseSchema(schema)) {
/* 1200 */       throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._generatorFactory
/* 1201 */           .getFormatName());
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
/*      */   protected final void _writeValueAndClose(JsonGenerator gen, Object value) throws IOException {
/* 1214 */     if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
/* 1215 */       _writeCloseable(gen, value);
/*      */       return;
/*      */     } 
/*      */     try {
/* 1219 */       this._prefetch.serialize(gen, value, _serializerProvider());
/* 1220 */     } catch (Exception e) {
/* 1221 */       ClassUtil.closeOnFailAndThrowAsIOE(gen, e);
/*      */       return;
/*      */     } 
/* 1224 */     gen.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _writeCloseable(JsonGenerator gen, Object value) throws IOException {
/* 1234 */     Closeable toClose = (Closeable)value;
/*      */     try {
/* 1236 */       this._prefetch.serialize(gen, value, _serializerProvider());
/* 1237 */       Closeable tmpToClose = toClose;
/* 1238 */       toClose = null;
/* 1239 */       tmpToClose.close();
/* 1240 */     } catch (Exception e) {
/* 1241 */       ClassUtil.closeOnFailAndThrowAsIOE(gen, toClose, e);
/*      */       return;
/*      */     } 
/* 1244 */     gen.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final JsonGenerator _configureGenerator(JsonGenerator gen) {
/* 1257 */     this._config.initialize(gen);
/* 1258 */     this._generatorSettings.initialize(gen);
/* 1259 */     return gen;
/*      */   }
/*      */   
/*      */   protected final void _assertNotNull(String paramName, Object src) {
/* 1263 */     if (src == null) {
/* 1264 */       throw new IllegalArgumentException(String.format("argument \"%s\" is null", new Object[] { paramName }));
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
/*      */   public static final class GeneratorSettings
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1286 */     public static final GeneratorSettings empty = new GeneratorSettings(null, null, null, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final PrettyPrinter prettyPrinter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final FormatSchema schema;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final CharacterEscapes characterEscapes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final SerializableString rootValueSeparator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public GeneratorSettings(PrettyPrinter pp, FormatSchema sch, CharacterEscapes esc, SerializableString rootSep) {
/* 1317 */       this.prettyPrinter = pp;
/* 1318 */       this.schema = sch;
/* 1319 */       this.characterEscapes = esc;
/* 1320 */       this.rootValueSeparator = rootSep;
/*      */     }
/*      */ 
/*      */     
/*      */     public GeneratorSettings with(PrettyPrinter pp) {
/* 1325 */       if (pp == null) {
/* 1326 */         pp = ObjectWriter.NULL_PRETTY_PRINTER;
/*      */       }
/* 1328 */       return (pp == this.prettyPrinter) ? this : new GeneratorSettings(pp, this.schema, this.characterEscapes, this.rootValueSeparator);
/*      */     }
/*      */ 
/*      */     
/*      */     public GeneratorSettings with(FormatSchema sch) {
/* 1333 */       return (this.schema == sch) ? this : new GeneratorSettings(this.prettyPrinter, sch, this.characterEscapes, this.rootValueSeparator);
/*      */     }
/*      */ 
/*      */     
/*      */     public GeneratorSettings with(CharacterEscapes esc) {
/* 1338 */       return (this.characterEscapes == esc) ? this : new GeneratorSettings(this.prettyPrinter, this.schema, esc, this.rootValueSeparator);
/*      */     }
/*      */ 
/*      */     
/*      */     public GeneratorSettings withRootValueSeparator(String sep) {
/* 1343 */       if (sep == null) {
/* 1344 */         if (this.rootValueSeparator == null) {
/* 1345 */           return this;
/*      */         }
/* 1347 */         return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, null);
/*      */       } 
/* 1349 */       if (sep.equals(_rootValueSeparatorAsString())) {
/* 1350 */         return this;
/*      */       }
/* 1352 */       return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, (SerializableString)new SerializedString(sep));
/*      */     }
/*      */ 
/*      */     
/*      */     public GeneratorSettings withRootValueSeparator(SerializableString sep) {
/* 1357 */       if (sep == null) {
/* 1358 */         if (this.rootValueSeparator == null) {
/* 1359 */           return this;
/*      */         }
/* 1361 */         return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, null);
/*      */       } 
/* 1363 */       if (sep.equals(this.rootValueSeparator)) {
/* 1364 */         return this;
/*      */       }
/* 1366 */       return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, sep);
/*      */     }
/*      */     
/*      */     private final String _rootValueSeparatorAsString() {
/* 1370 */       return (this.rootValueSeparator == null) ? null : this.rootValueSeparator.getValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void initialize(JsonGenerator gen) {
/* 1378 */       PrettyPrinter pp = this.prettyPrinter;
/* 1379 */       if (this.prettyPrinter != null) {
/* 1380 */         if (pp == ObjectWriter.NULL_PRETTY_PRINTER) {
/* 1381 */           gen.setPrettyPrinter(null);
/*      */         } else {
/* 1383 */           if (pp instanceof Instantiatable) {
/* 1384 */             pp = (PrettyPrinter)((Instantiatable)pp).createInstance();
/*      */           }
/* 1386 */           gen.setPrettyPrinter(pp);
/*      */         } 
/*      */       }
/* 1389 */       if (this.characterEscapes != null) {
/* 1390 */         gen.setCharacterEscapes(this.characterEscapes);
/*      */       }
/* 1392 */       if (this.schema != null) {
/* 1393 */         gen.setSchema(this.schema);
/*      */       }
/* 1395 */       if (this.rootValueSeparator != null) {
/* 1396 */         gen.setRootValueSeparator(this.rootValueSeparator);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Prefetch
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1413 */     public static final Prefetch empty = new Prefetch(null, null, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final JavaType rootType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final JsonSerializer<Object> valueSerializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final TypeSerializer typeSerializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Prefetch(JavaType rootT, JsonSerializer<Object> ser, TypeSerializer typeSer) {
/* 1439 */       this.rootType = rootT;
/* 1440 */       this.valueSerializer = ser;
/* 1441 */       this.typeSerializer = typeSer;
/*      */     }
/*      */ 
/*      */     
/*      */     public Prefetch forRootType(ObjectWriter parent, JavaType newType) {
/* 1446 */       if (newType == null) {
/* 1447 */         if (this.rootType == null || this.valueSerializer == null) {
/* 1448 */           return this;
/*      */         }
/* 1450 */         return new Prefetch(null, null, null);
/*      */       } 
/*      */ 
/*      */       
/* 1454 */       if (newType.equals(this.rootType)) {
/* 1455 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1460 */       if (newType.isJavaLangObject()) {
/* 1461 */         TypeSerializer typeSer; DefaultSerializerProvider prov = parent._serializerProvider();
/*      */ 
/*      */         
/*      */         try {
/* 1465 */           typeSer = prov.findTypeSerializer(newType);
/* 1466 */         } catch (JsonMappingException e) {
/*      */ 
/*      */           
/* 1469 */           throw new RuntimeJsonMappingException(e);
/*      */         } 
/* 1471 */         return new Prefetch(null, null, typeSer);
/*      */       } 
/*      */       
/* 1474 */       if (parent.isEnabled(SerializationFeature.EAGER_SERIALIZER_FETCH)) {
/* 1475 */         DefaultSerializerProvider prov = parent._serializerProvider();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/* 1481 */           JsonSerializer<Object> ser = prov.findTypedValueSerializer(newType, true, null);
/*      */           
/* 1483 */           if (ser instanceof TypeWrappedSerializer) {
/* 1484 */             return new Prefetch(newType, null, ((TypeWrappedSerializer)ser)
/* 1485 */                 .typeSerializer());
/*      */           }
/* 1487 */           return new Prefetch(newType, ser, null);
/* 1488 */         } catch (DatabindException typeSer) {}
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1493 */       return new Prefetch(newType, null, this.typeSerializer);
/*      */     }
/*      */     
/*      */     public final JsonSerializer<Object> getValueSerializer() {
/* 1497 */       return this.valueSerializer;
/*      */     }
/*      */     
/*      */     public final TypeSerializer getTypeSerializer() {
/* 1501 */       return this.typeSerializer;
/*      */     }
/*      */     
/*      */     public boolean hasSerializer() {
/* 1505 */       return (this.valueSerializer != null || this.typeSerializer != null);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void serialize(JsonGenerator gen, Object value, DefaultSerializerProvider prov) throws IOException {
/* 1511 */       if (this.typeSerializer != null) {
/* 1512 */         prov.serializePolymorphic(gen, value, this.rootType, this.valueSerializer, this.typeSerializer);
/* 1513 */       } else if (this.valueSerializer != null) {
/* 1514 */         prov.serializeValue(gen, value, this.rootType, this.valueSerializer);
/* 1515 */       } else if (this.rootType != null) {
/* 1516 */         prov.serializeValue(gen, value, this.rootType);
/*      */       } else {
/* 1518 */         prov.serializeValue(gen, value);
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ObjectWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */