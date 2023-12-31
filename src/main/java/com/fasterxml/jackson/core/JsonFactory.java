/*      */ package com.fasterxml.jackson.core;
/*      */ 
/*      */ import com.fasterxml.jackson.core.format.InputAccessor;
/*      */ import com.fasterxml.jackson.core.format.MatchStrength;
/*      */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*      */ import com.fasterxml.jackson.core.io.ContentReference;
/*      */ import com.fasterxml.jackson.core.io.IOContext;
/*      */ import com.fasterxml.jackson.core.io.InputDecorator;
/*      */ import com.fasterxml.jackson.core.io.OutputDecorator;
/*      */ import com.fasterxml.jackson.core.io.SerializedString;
/*      */ import com.fasterxml.jackson.core.io.UTF8Writer;
/*      */ import com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper;
/*      */ import com.fasterxml.jackson.core.json.PackageVersion;
/*      */ import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
/*      */ import com.fasterxml.jackson.core.json.UTF8DataInputJsonParser;
/*      */ import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
/*      */ import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
/*      */ import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
/*      */ import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*      */ import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
/*      */ import com.fasterxml.jackson.core.util.BufferRecycler;
/*      */ import com.fasterxml.jackson.core.util.BufferRecyclers;
/*      */ import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeature;
/*      */ import java.io.CharArrayReader;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataOutput;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.io.StringReader;
/*      */ import java.io.Writer;
/*      */ import java.net.URL;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class JsonFactory
/*      */   extends TokenStreamFactory
/*      */   implements Versioned, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 2L;
/*      */   public static final String FORMAT_NAME_JSON = "JSON";
/*      */   
/*      */   public enum Feature
/*      */     implements JacksonFeature
/*      */   {
/*   81 */     INTERN_FIELD_NAMES(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   91 */     CANONICALIZE_FIELD_NAMES(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  107 */     FAIL_ON_SYMBOL_HASH_OVERFLOW(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  124 */     USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final boolean _defaultState;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int collectDefaults() {
/*  140 */       int flags = 0;
/*  141 */       for (Feature f : values()) {
/*  142 */         if (f.enabledByDefault()) flags |= f.getMask(); 
/*      */       } 
/*  144 */       return flags;
/*      */     }
/*      */     Feature(boolean defaultState) {
/*  147 */       this._defaultState = defaultState;
/*      */     }
/*      */     public boolean enabledByDefault() {
/*  150 */       return this._defaultState;
/*      */     } public boolean enabledIn(int flags) {
/*  152 */       return ((flags & getMask()) != 0);
/*      */     } public int getMask() {
/*  154 */       return 1 << ordinal();
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
/*  172 */   protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = Feature.collectDefaults();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  178 */   protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  184 */   protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
/*      */   
/*  186 */   public static final SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = (SerializableString)DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final char DEFAULT_QUOTE_CHAR = '"';
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  204 */   protected final transient CharsToNameCanonicalizer _rootCharSymbols = CharsToNameCanonicalizer.createRoot();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  215 */   protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  226 */   protected int _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  231 */   protected int _parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  236 */   protected int _generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ObjectCodec _objectCodec;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected CharacterEscapes _characterEscapes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected InputDecorator _inputDecorator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected OutputDecorator _outputDecorator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  279 */   protected SerializableString _rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _maximumNonEscapedChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final char _quoteChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory() {
/*  314 */     this((ObjectCodec)null);
/*      */   }
/*      */   public JsonFactory(ObjectCodec oc) {
/*  317 */     this._objectCodec = oc;
/*  318 */     this._quoteChar = '"';
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonFactory(JsonFactory src, ObjectCodec codec) {
/*  331 */     this._objectCodec = codec;
/*      */ 
/*      */     
/*  334 */     this._factoryFeatures = src._factoryFeatures;
/*  335 */     this._parserFeatures = src._parserFeatures;
/*  336 */     this._generatorFeatures = src._generatorFeatures;
/*  337 */     this._inputDecorator = src._inputDecorator;
/*  338 */     this._outputDecorator = src._outputDecorator;
/*      */ 
/*      */     
/*  341 */     this._characterEscapes = src._characterEscapes;
/*  342 */     this._rootValueSeparator = src._rootValueSeparator;
/*  343 */     this._maximumNonEscapedChar = src._maximumNonEscapedChar;
/*  344 */     this._quoteChar = src._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory(JsonFactoryBuilder b) {
/*  355 */     this._objectCodec = null;
/*      */ 
/*      */     
/*  358 */     this._factoryFeatures = b._factoryFeatures;
/*  359 */     this._parserFeatures = b._streamReadFeatures;
/*  360 */     this._generatorFeatures = b._streamWriteFeatures;
/*  361 */     this._inputDecorator = b._inputDecorator;
/*  362 */     this._outputDecorator = b._outputDecorator;
/*      */ 
/*      */     
/*  365 */     this._characterEscapes = b._characterEscapes;
/*  366 */     this._rootValueSeparator = b._rootValueSeparator;
/*  367 */     this._maximumNonEscapedChar = b._maximumNonEscapedChar;
/*  368 */     this._quoteChar = b._quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonFactory(TSFBuilder<?, ?> b, boolean bogus) {
/*  380 */     this._objectCodec = null;
/*      */     
/*  382 */     this._factoryFeatures = b._factoryFeatures;
/*  383 */     this._parserFeatures = b._streamReadFeatures;
/*  384 */     this._generatorFeatures = b._streamWriteFeatures;
/*  385 */     this._inputDecorator = b._inputDecorator;
/*  386 */     this._outputDecorator = b._outputDecorator;
/*      */ 
/*      */     
/*  389 */     this._characterEscapes = null;
/*  390 */     this._rootValueSeparator = null;
/*  391 */     this._maximumNonEscapedChar = 0;
/*  392 */     this._quoteChar = '"';
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TSFBuilder<?, ?> rebuild() {
/*  405 */     _requireJSONFactory("Factory implementation for format (%s) MUST override `rebuild()` method");
/*  406 */     return new JsonFactoryBuilder(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TSFBuilder<?, ?> builder() {
/*  421 */     return new JsonFactoryBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory copy() {
/*  442 */     _checkInvalidCopy(JsonFactory.class);
/*      */     
/*  444 */     return new JsonFactory(this, null);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _checkInvalidCopy(Class<?> exp) {
/*  449 */     if (getClass() != exp) {
/*  450 */       throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + 
/*  451 */           version() + ") does not override copy(); it has to");
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
/*      */   protected Object readResolve() {
/*  471 */     return new JsonFactory(this, this._objectCodec);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean requiresPropertyOrdering() {
/*  499 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canHandleBinaryNatively() {
/*  517 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canUseCharArrays() {
/*  534 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canParseAsync() {
/*  551 */     return _isJSONFactory();
/*      */   }
/*      */ 
/*      */   
/*      */   public Class<? extends FormatFeature> getFormatReadFeatureType() {
/*  556 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public Class<? extends FormatFeature> getFormatWriteFeatureType() {
/*  561 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canUseSchema(FormatSchema schema) {
/*  585 */     if (schema == null) {
/*  586 */       return false;
/*      */     }
/*  588 */     String ourFormat = getFormatName();
/*  589 */     return (ourFormat != null && ourFormat.equals(schema.getSchemaType()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getFormatName() {
/*  608 */     if (getClass() == JsonFactory.class) {
/*  609 */       return "JSON";
/*      */     }
/*  611 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public MatchStrength hasFormat(InputAccessor acc) throws IOException {
/*  617 */     if (getClass() == JsonFactory.class) {
/*  618 */       return hasJSONFormat(acc);
/*      */     }
/*  620 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean requiresCustomCodec() {
/*  637 */     return false;
/*      */   }
/*      */   
/*      */   protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
/*  641 */     return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Version version() {
/*  652 */     return PackageVersion.VERSION;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public final JsonFactory configure(Feature f, boolean state) {
/*  674 */     return state ? enable(f) : disable(f);
/*      */   }
/*      */ 
/*      */ 
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
/*      */   public JsonFactory enable(Feature f) {
/*  689 */     this._factoryFeatures |= f.getMask();
/*  690 */     return this;
/*      */   }
/*      */ 
/*      */ 
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
/*      */   public JsonFactory disable(Feature f) {
/*  705 */     this._factoryFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*  706 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(Feature f) {
/*  717 */     return ((this._factoryFeatures & f.getMask()) != 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public final int getParserFeatures() {
/*  722 */     return this._parserFeatures;
/*      */   }
/*      */ 
/*      */   
/*      */   public final int getGeneratorFeatures() {
/*  727 */     return this._generatorFeatures;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getFormatParserFeatures() {
/*  733 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getFormatGeneratorFeatures() {
/*  739 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final JsonFactory configure(JsonParser.Feature f, boolean state) {
/*  758 */     return state ? enable(f) : disable(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory enable(JsonParser.Feature f) {
/*  770 */     this._parserFeatures |= f.getMask();
/*  771 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory disable(JsonParser.Feature f) {
/*  783 */     this._parserFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*  784 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(JsonParser.Feature f) {
/*  796 */     return ((this._parserFeatures & f.getMask()) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(StreamReadFeature f) {
/*  809 */     return ((this._parserFeatures & f.mappedFeature().getMask()) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputDecorator getInputDecorator() {
/*  819 */     return this._inputDecorator;
/*      */   }
/*      */ 
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
/*      */   public JsonFactory setInputDecorator(InputDecorator d) {
/*  833 */     this._inputDecorator = d;
/*  834 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
/*  853 */     return state ? enable(f) : disable(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory enable(JsonGenerator.Feature f) {
/*  865 */     this._generatorFeatures |= f.getMask();
/*  866 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory disable(JsonGenerator.Feature f) {
/*  878 */     this._generatorFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*  879 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(JsonGenerator.Feature f) {
/*  891 */     return ((this._generatorFeatures & f.getMask()) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isEnabled(StreamWriteFeature f) {
/*  904 */     return ((this._generatorFeatures & f.mappedFeature().getMask()) != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CharacterEscapes getCharacterEscapes() {
/*  913 */     return this._characterEscapes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
/*  924 */     this._characterEscapes = esc;
/*  925 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public OutputDecorator getOutputDecorator() {
/*  936 */     return this._outputDecorator;
/*      */   }
/*      */ 
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
/*      */   public JsonFactory setOutputDecorator(OutputDecorator d) {
/*  950 */     this._outputDecorator = d;
/*  951 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory setRootValueSeparator(String sep) {
/*  964 */     this._rootValueSeparator = (sep == null) ? null : (SerializableString)new SerializedString(sep);
/*  965 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getRootValueSeparator() {
/*  972 */     return (this._rootValueSeparator == null) ? null : this._rootValueSeparator.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonFactory setCodec(ObjectCodec oc) {
/*  993 */     this._objectCodec = oc;
/*  994 */     return this;
/*      */   }
/*      */   public ObjectCodec getCodec() {
/*  997 */     return this._objectCodec;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(File f) throws IOException, JsonParseException {
/* 1028 */     IOContext ctxt = _createContext(_createContentReference(f), true);
/* 1029 */     InputStream in = new FileInputStream(f);
/* 1030 */     return _createParser(_decorate(in, ctxt), ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(URL url) throws IOException, JsonParseException {
/* 1054 */     IOContext ctxt = _createContext(_createContentReference(url), true);
/* 1055 */     InputStream in = _optimizedStreamFromURL(url);
/* 1056 */     return _createParser(_decorate(in, ctxt), ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
/* 1082 */     IOContext ctxt = _createContext(_createContentReference(in), false);
/* 1083 */     return _createParser(_decorate(in, ctxt), ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(Reader r) throws IOException, JsonParseException {
/* 1103 */     IOContext ctxt = _createContext(_createContentReference(r), false);
/* 1104 */     return _createParser(_decorate(r, ctxt), ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
/* 1115 */     IOContext ctxt = _createContext(_createContentReference(data), true);
/* 1116 */     if (this._inputDecorator != null) {
/* 1117 */       InputStream in = this._inputDecorator.decorate(ctxt, data, 0, data.length);
/* 1118 */       if (in != null) {
/* 1119 */         return _createParser(in, ctxt);
/*      */       }
/*      */     } 
/* 1122 */     return _createParser(data, 0, data.length, ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/* 1137 */     IOContext ctxt = _createContext(_createContentReference(data, offset, len), true);
/*      */     
/* 1139 */     if (this._inputDecorator != null) {
/* 1140 */       InputStream in = this._inputDecorator.decorate(ctxt, data, offset, len);
/* 1141 */       if (in != null) {
/* 1142 */         return _createParser(in, ctxt);
/*      */       }
/*      */     } 
/* 1145 */     return _createParser(data, offset, len, ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(String content) throws IOException, JsonParseException {
/* 1156 */     int strLen = content.length();
/*      */     
/* 1158 */     if (this._inputDecorator != null || strLen > 32768 || !canUseCharArrays())
/*      */     {
/*      */       
/* 1161 */       return createParser(new StringReader(content));
/*      */     }
/* 1163 */     IOContext ctxt = _createContext(_createContentReference(content), true);
/* 1164 */     char[] buf = ctxt.allocTokenBuffer(strLen);
/* 1165 */     content.getChars(0, strLen, buf, 0);
/* 1166 */     return _createParser(buf, 0, strLen, ctxt, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(char[] content) throws IOException {
/* 1177 */     return createParser(content, 0, content.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(char[] content, int offset, int len) throws IOException {
/* 1187 */     if (this._inputDecorator != null) {
/* 1188 */       return createParser(new CharArrayReader(content, offset, len));
/*      */     }
/* 1190 */     return _createParser(content, offset, len, 
/* 1191 */         _createContext(_createContentReference(content, offset, len), true), false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser createParser(DataInput in) throws IOException {
/* 1207 */     IOContext ctxt = _createContext(_createContentReference(in), false);
/* 1208 */     return _createParser(_decorate(in, ctxt), ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/* 1238 */     _requireJSONFactory("Non-blocking source not (yet?) supported for this format (%s)");
/* 1239 */     IOContext ctxt = _createNonBlockingContext((Object)null);
/* 1240 */     ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/* 1241 */     return (JsonParser)new NonBlockingJsonParser(ctxt, this._parserFeatures, can);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/* 1277 */     IOContext ctxt = _createContext(_createContentReference(out), false);
/* 1278 */     ctxt.setEncoding(enc);
/* 1279 */     if (enc == JsonEncoding.UTF8) {
/* 1280 */       return _createUTF8Generator(_decorate(out, ctxt), ctxt);
/*      */     }
/* 1282 */     Writer w = _createWriter(out, enc, ctxt);
/* 1283 */     return _createGenerator(_decorate(w, ctxt), ctxt);
/*      */   }
/*      */ 
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
/* 1296 */     return createGenerator(out, JsonEncoding.UTF8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/* 1316 */     IOContext ctxt = _createContext(_createContentReference(w), false);
/* 1317 */     return _createGenerator(_decorate(w, ctxt), ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
/* 1339 */     OutputStream out = new FileOutputStream(f);
/*      */     
/* 1341 */     IOContext ctxt = _createContext(_createContentReference(out), true);
/* 1342 */     ctxt.setEncoding(enc);
/* 1343 */     if (enc == JsonEncoding.UTF8) {
/* 1344 */       return _createUTF8Generator(_decorate(out, ctxt), ctxt);
/*      */     }
/* 1346 */     Writer w = _createWriter(out, enc, ctxt);
/* 1347 */     return _createGenerator(_decorate(w, ctxt), ctxt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc) throws IOException {
/* 1358 */     return createGenerator(_createDataOutputWrapper(out), enc);
/*      */   }
/*      */ 
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
/* 1371 */     return createGenerator(_createDataOutputWrapper(out), JsonEncoding.UTF8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
/* 1406 */     return createParser(f);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
/* 1434 */     return createParser(url);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
/* 1465 */     return createParser(in);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
/* 1489 */     return createParser(r);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
/* 1506 */     return createParser(data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/* 1526 */     return createParser(data, offset, len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
/* 1544 */     return createParser(content);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/* 1581 */     return createGenerator(out, enc);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonGenerator createJsonGenerator(Writer out) throws IOException {
/* 1605 */     return createGenerator(out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public JsonGenerator createJsonGenerator(OutputStream out) throws IOException {
/* 1624 */     return createGenerator(out, JsonEncoding.UTF8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
/* 1655 */     return (new ByteSourceJsonBootstrapper(ctxt, in)).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
/* 1679 */     return (JsonParser)new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols
/* 1680 */         .makeChild(this._factoryFeatures));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
/* 1701 */     return (JsonParser)new ReaderBasedJsonParser(ctxt, this._parserFeatures, null, this._objectCodec, this._rootCharSymbols
/* 1702 */         .makeChild(this._factoryFeatures), data, offset, offset + len, recyclable);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
/* 1728 */     return (new ByteSourceJsonBootstrapper(ctxt, data, offset, len)).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonParser _createParser(DataInput input, IOContext ctxt) throws IOException {
/* 1748 */     _requireJSONFactory("InputData source not (yet?) supported for this format (%s)");
/*      */ 
/*      */     
/* 1751 */     int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
/* 1752 */     ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/* 1753 */     return (JsonParser)new UTF8DataInputJsonParser(ctxt, this._parserFeatures, input, this._objectCodec, can, firstByte);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
/* 1783 */     WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out, this._quoteChar);
/*      */     
/* 1785 */     if (this._maximumNonEscapedChar > 0) {
/* 1786 */       gen.setHighestNonEscapedChar(this._maximumNonEscapedChar);
/*      */     }
/* 1788 */     if (this._characterEscapes != null) {
/* 1789 */       gen.setCharacterEscapes(this._characterEscapes);
/*      */     }
/* 1791 */     SerializableString rootSep = this._rootValueSeparator;
/* 1792 */     if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/* 1793 */       gen.setRootValueSeparator(rootSep);
/*      */     }
/* 1795 */     return (JsonGenerator)gen;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
/* 1816 */     UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out, this._quoteChar);
/*      */     
/* 1818 */     if (this._maximumNonEscapedChar > 0) {
/* 1819 */       gen.setHighestNonEscapedChar(this._maximumNonEscapedChar);
/*      */     }
/* 1821 */     if (this._characterEscapes != null) {
/* 1822 */       gen.setCharacterEscapes(this._characterEscapes);
/*      */     }
/* 1824 */     SerializableString rootSep = this._rootValueSeparator;
/* 1825 */     if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/* 1826 */       gen.setRootValueSeparator(rootSep);
/*      */     }
/* 1828 */     return (JsonGenerator)gen;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
/* 1834 */     if (enc == JsonEncoding.UTF8) {
/* 1835 */       return (Writer)new UTF8Writer(ctxt, out);
/*      */     }
/*      */     
/* 1838 */     return new OutputStreamWriter(out, enc.getJavaName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final InputStream _decorate(InputStream in, IOContext ctxt) throws IOException {
/* 1848 */     if (this._inputDecorator != null) {
/* 1849 */       InputStream in2 = this._inputDecorator.decorate(ctxt, in);
/* 1850 */       if (in2 != null) {
/* 1851 */         return in2;
/*      */       }
/*      */     } 
/* 1854 */     return in;
/*      */   }
/*      */   
/*      */   protected final Reader _decorate(Reader in, IOContext ctxt) throws IOException {
/* 1858 */     if (this._inputDecorator != null) {
/* 1859 */       Reader in2 = this._inputDecorator.decorate(ctxt, in);
/* 1860 */       if (in2 != null) {
/* 1861 */         return in2;
/*      */       }
/*      */     } 
/* 1864 */     return in;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final DataInput _decorate(DataInput in, IOContext ctxt) throws IOException {
/* 1869 */     if (this._inputDecorator != null) {
/* 1870 */       DataInput in2 = this._inputDecorator.decorate(ctxt, in);
/* 1871 */       if (in2 != null) {
/* 1872 */         return in2;
/*      */       }
/*      */     } 
/* 1875 */     return in;
/*      */   }
/*      */   
/*      */   protected final OutputStream _decorate(OutputStream out, IOContext ctxt) throws IOException {
/* 1879 */     if (this._outputDecorator != null) {
/* 1880 */       OutputStream out2 = this._outputDecorator.decorate(ctxt, out);
/* 1881 */       if (out2 != null) {
/* 1882 */         return out2;
/*      */       }
/*      */     } 
/* 1885 */     return out;
/*      */   }
/*      */   
/*      */   protected final Writer _decorate(Writer out, IOContext ctxt) throws IOException {
/* 1889 */     if (this._outputDecorator != null) {
/* 1890 */       Writer out2 = this._outputDecorator.decorate(ctxt, out);
/* 1891 */       if (out2 != null) {
/* 1892 */         return out2;
/*      */       }
/*      */     } 
/* 1895 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BufferRecycler _getBufferRecycler() {
/* 1917 */     if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(this._factoryFeatures)) {
/* 1918 */       return BufferRecyclers.getBufferRecycler();
/*      */     }
/* 1920 */     return new BufferRecycler();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IOContext _createContext(ContentReference contentRef, boolean resourceManaged) {
/* 1934 */     if (contentRef == null) {
/* 1935 */       contentRef = ContentReference.unknown();
/*      */     }
/* 1937 */     return new IOContext(_getBufferRecycler(), contentRef, resourceManaged);
/*      */   }
/*      */ 
/*      */ 
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
/*      */   protected IOContext _createContext(Object rawContentRef, boolean resourceManaged) {
/* 1952 */     return new IOContext(_getBufferRecycler(), 
/* 1953 */         _createContentReference(rawContentRef), resourceManaged);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IOContext _createNonBlockingContext(Object srcRef) {
/* 1970 */     return new IOContext(_getBufferRecycler(), 
/* 1971 */         _createContentReference(srcRef), false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ContentReference _createContentReference(Object contentAccessor) {
/* 1990 */     return ContentReference.construct(!canHandleBinaryNatively(), contentAccessor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ContentReference _createContentReference(Object contentAccessor, int offset, int length) {
/* 2013 */     return ContentReference.construct(!canHandleBinaryNatively(), contentAccessor, offset, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _requireJSONFactory(String msg) {
/* 2038 */     if (!_isJSONFactory()) {
/* 2039 */       throw new UnsupportedOperationException(String.format(msg, new Object[] { getFormatName() }));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean _isJSONFactory() {
/* 2046 */     return (getFormatName() == "JSON");
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */