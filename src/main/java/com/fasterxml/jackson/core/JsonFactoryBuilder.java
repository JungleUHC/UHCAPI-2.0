/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*     */ import com.fasterxml.jackson.core.io.SerializedString;
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
/*     */ 
/*     */ 
/*     */ public class JsonFactoryBuilder
/*     */   extends TSFBuilder<JsonFactory, JsonFactoryBuilder>
/*     */ {
/*     */   protected CharacterEscapes _characterEscapes;
/*     */   protected SerializableString _rootValueSeparator;
/*     */   protected int _maximumNonEscapedChar;
/*  32 */   protected char _quoteChar = '"';
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder() {
/*  36 */     this._rootValueSeparator = JsonFactory.DEFAULT_ROOT_VALUE_SEPARATOR;
/*  37 */     this._maximumNonEscapedChar = 0;
/*     */   }
/*     */   
/*     */   public JsonFactoryBuilder(JsonFactory base) {
/*  41 */     super(base);
/*  42 */     this._characterEscapes = base.getCharacterEscapes();
/*  43 */     this._rootValueSeparator = base._rootValueSeparator;
/*  44 */     this._maximumNonEscapedChar = base._maximumNonEscapedChar;
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
/*     */   public JsonFactoryBuilder enable(JsonReadFeature f) {
/*  57 */     _legacyEnable(f.mappedFeature());
/*  58 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder enable(JsonReadFeature first, JsonReadFeature... other) {
/*  63 */     _legacyEnable(first.mappedFeature());
/*  64 */     enable(first);
/*  65 */     for (JsonReadFeature f : other) {
/*  66 */       _legacyEnable(f.mappedFeature());
/*     */     }
/*  68 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder disable(JsonReadFeature f) {
/*  73 */     _legacyDisable(f.mappedFeature());
/*  74 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder disable(JsonReadFeature first, JsonReadFeature... other) {
/*  79 */     _legacyDisable(first.mappedFeature());
/*  80 */     for (JsonReadFeature f : other) {
/*  81 */       _legacyEnable(f.mappedFeature());
/*     */     }
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder configure(JsonReadFeature f, boolean state) {
/*  88 */     return state ? enable(f) : disable(f);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder enable(JsonWriteFeature f) {
/*  95 */     JsonGenerator.Feature old = f.mappedFeature();
/*  96 */     if (old != null) {
/*  97 */       _legacyEnable(old);
/*     */     }
/*  99 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder enable(JsonWriteFeature first, JsonWriteFeature... other) {
/* 104 */     _legacyEnable(first.mappedFeature());
/* 105 */     for (JsonWriteFeature f : other) {
/* 106 */       _legacyEnable(f.mappedFeature());
/*     */     }
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder disable(JsonWriteFeature f) {
/* 113 */     _legacyDisable(f.mappedFeature());
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder disable(JsonWriteFeature first, JsonWriteFeature... other) {
/* 119 */     _legacyDisable(first.mappedFeature());
/* 120 */     for (JsonWriteFeature f : other) {
/* 121 */       _legacyDisable(f.mappedFeature());
/*     */     }
/* 123 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFactoryBuilder configure(JsonWriteFeature f, boolean state) {
/* 128 */     return state ? enable(f) : disable(f);
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
/*     */   public JsonFactoryBuilder characterEscapes(CharacterEscapes esc) {
/* 142 */     this._characterEscapes = esc;
/* 143 */     return this;
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
/*     */   public JsonFactoryBuilder rootValueSeparator(String sep) {
/* 156 */     this._rootValueSeparator = (sep == null) ? null : (SerializableString)new SerializedString(sep);
/* 157 */     return this;
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
/*     */   public JsonFactoryBuilder rootValueSeparator(SerializableString sep) {
/* 170 */     this._rootValueSeparator = sep;
/* 171 */     return this;
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
/*     */   public JsonFactoryBuilder highestNonEscapedChar(int maxNonEscaped) {
/* 195 */     this._maximumNonEscapedChar = (maxNonEscaped <= 0) ? 0 : Math.max(127, maxNonEscaped);
/* 196 */     return this;
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
/*     */   public JsonFactoryBuilder quoteChar(char ch) {
/* 217 */     if (ch > '') {
/* 218 */       throw new IllegalArgumentException("Can only use Unicode characters up to 0x7F as quote characters");
/*     */     }
/* 220 */     this._quoteChar = ch;
/* 221 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public CharacterEscapes characterEscapes() {
/* 226 */     return this._characterEscapes; } public SerializableString rootValueSeparator() {
/* 227 */     return this._rootValueSeparator;
/*     */   } public int highestNonEscapedChar() {
/* 229 */     return this._maximumNonEscapedChar;
/*     */   } public char quoteChar() {
/* 231 */     return this._quoteChar;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFactory build() {
/* 236 */     return new JsonFactory(this);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonFactoryBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */