/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.InputDecorator;
/*     */ import com.fasterxml.jackson.core.io.OutputDecorator;
/*     */ import com.fasterxml.jackson.core.json.JsonReadFeature;
/*     */ import com.fasterxml.jackson.core.json.JsonWriteFeature;
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
/*     */ public abstract class TSFBuilder<F extends JsonFactory, B extends TSFBuilder<F, B>>
/*     */ {
/*  26 */   protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = JsonFactory.Feature.collectDefaults();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  32 */   protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  38 */   protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _factoryFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _streamReadFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _streamWriteFeatures;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected InputDecorator _inputDecorator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected OutputDecorator _outputDecorator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TSFBuilder() {
/*  87 */     this._factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
/*  88 */     this._streamReadFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
/*  89 */     this._streamWriteFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
/*  90 */     this._inputDecorator = null;
/*  91 */     this._outputDecorator = null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected TSFBuilder(JsonFactory base) {
/*  96 */     this(base._factoryFeatures, base._parserFeatures, base._generatorFeatures);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TSFBuilder(int factoryFeatures, int parserFeatures, int generatorFeatures) {
/* 103 */     this._factoryFeatures = factoryFeatures;
/* 104 */     this._streamReadFeatures = parserFeatures;
/* 105 */     this._streamWriteFeatures = generatorFeatures;
/*     */   }
/*     */ 
/*     */   
/*     */   public int factoryFeaturesMask() {
/* 110 */     return this._factoryFeatures; }
/* 111 */   public int streamReadFeatures() { return this._streamReadFeatures; } public int streamWriteFeatures() {
/* 112 */     return this._streamWriteFeatures;
/*     */   }
/* 114 */   public InputDecorator inputDecorator() { return this._inputDecorator; } public OutputDecorator outputDecorator() {
/* 115 */     return this._outputDecorator;
/*     */   }
/*     */ 
/*     */   
/*     */   public B enable(JsonFactory.Feature f) {
/* 120 */     this._factoryFeatures |= f.getMask();
/* 121 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(JsonFactory.Feature f) {
/* 125 */     this._factoryFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 126 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(JsonFactory.Feature f, boolean state) {
/* 130 */     return state ? enable(f) : disable(f);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public B enable(StreamReadFeature f) {
/* 136 */     this._streamReadFeatures |= f.mappedFeature().getMask();
/* 137 */     return _this();
/*     */   }
/*     */   
/*     */   public B enable(StreamReadFeature first, StreamReadFeature... other) {
/* 141 */     this._streamReadFeatures |= first.mappedFeature().getMask();
/* 142 */     for (StreamReadFeature f : other) {
/* 143 */       this._streamReadFeatures |= f.mappedFeature().getMask();
/*     */     }
/* 145 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamReadFeature f) {
/* 149 */     this._streamReadFeatures &= f.mappedFeature().getMask() ^ 0xFFFFFFFF;
/* 150 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamReadFeature first, StreamReadFeature... other) {
/* 154 */     this._streamReadFeatures &= first.mappedFeature().getMask() ^ 0xFFFFFFFF;
/* 155 */     for (StreamReadFeature f : other) {
/* 156 */       this._streamReadFeatures &= f.mappedFeature().getMask() ^ 0xFFFFFFFF;
/*     */     }
/* 158 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(StreamReadFeature f, boolean state) {
/* 162 */     return state ? enable(f) : disable(f);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public B enable(StreamWriteFeature f) {
/* 168 */     this._streamWriteFeatures |= f.mappedFeature().getMask();
/* 169 */     return _this();
/*     */   }
/*     */   
/*     */   public B enable(StreamWriteFeature first, StreamWriteFeature... other) {
/* 173 */     this._streamWriteFeatures |= first.mappedFeature().getMask();
/* 174 */     for (StreamWriteFeature f : other) {
/* 175 */       this._streamWriteFeatures |= f.mappedFeature().getMask();
/*     */     }
/* 177 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamWriteFeature f) {
/* 181 */     this._streamWriteFeatures &= f.mappedFeature().getMask() ^ 0xFFFFFFFF;
/* 182 */     return _this();
/*     */   }
/*     */   
/*     */   public B disable(StreamWriteFeature first, StreamWriteFeature... other) {
/* 186 */     this._streamWriteFeatures &= first.mappedFeature().getMask() ^ 0xFFFFFFFF;
/* 187 */     for (StreamWriteFeature f : other) {
/* 188 */       this._streamWriteFeatures &= f.mappedFeature().getMask() ^ 0xFFFFFFFF;
/*     */     }
/* 190 */     return _this();
/*     */   }
/*     */   
/*     */   public B configure(StreamWriteFeature f, boolean state) {
/* 194 */     return state ? enable(f) : disable(f);
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
/*     */   public B enable(JsonReadFeature f) {
/* 207 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   public B enable(JsonReadFeature first, JsonReadFeature... other) {
/* 211 */     return _failNonJSON(first);
/*     */   }
/*     */   
/*     */   public B disable(JsonReadFeature f) {
/* 215 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   public B disable(JsonReadFeature first, JsonReadFeature... other) {
/* 219 */     return _failNonJSON(first);
/*     */   }
/*     */   
/*     */   public B configure(JsonReadFeature f, boolean state) {
/* 223 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   private B _failNonJSON(Object feature) {
/* 227 */     throw new IllegalArgumentException("Feature " + feature.getClass().getName() + "#" + feature
/* 228 */         .toString() + " not supported for non-JSON backend");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public B enable(JsonWriteFeature f) {
/* 234 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   public B enable(JsonWriteFeature first, JsonWriteFeature... other) {
/* 238 */     return _failNonJSON(first);
/*     */   }
/*     */   
/*     */   public B disable(JsonWriteFeature f) {
/* 242 */     return _failNonJSON(f);
/*     */   }
/*     */   
/*     */   public B disable(JsonWriteFeature first, JsonWriteFeature... other) {
/* 246 */     return _failNonJSON(first);
/*     */   }
/*     */   
/*     */   public B configure(JsonWriteFeature f, boolean state) {
/* 250 */     return _failNonJSON(f);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public B inputDecorator(InputDecorator dec) {
/* 256 */     this._inputDecorator = dec;
/* 257 */     return _this();
/*     */   }
/*     */   
/*     */   public B outputDecorator(OutputDecorator dec) {
/* 261 */     this._outputDecorator = dec;
/* 262 */     return _this();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract F build();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final B _this() {
/* 277 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _legacyEnable(JsonParser.Feature f) {
/* 282 */     if (f != null) {
/* 283 */       this._streamReadFeatures |= f.getMask();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void _legacyDisable(JsonParser.Feature f) {
/* 288 */     if (f != null) {
/* 289 */       this._streamReadFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*     */     }
/*     */   }
/*     */   
/*     */   protected void _legacyEnable(JsonGenerator.Feature f) {
/* 294 */     if (f != null)
/* 295 */       this._streamWriteFeatures |= f.getMask(); 
/*     */   }
/*     */   
/*     */   protected void _legacyDisable(JsonGenerator.Feature f) {
/* 299 */     if (f != null)
/* 300 */       this._streamWriteFeatures &= f.getMask() ^ 0xFFFFFFFF; 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/TSFBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */