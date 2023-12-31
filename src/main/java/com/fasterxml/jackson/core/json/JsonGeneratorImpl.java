/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.StreamWriteCapability;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.core.base.GeneratorBase;
/*     */ import com.fasterxml.jackson.core.io.CharTypes;
/*     */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*     */ import com.fasterxml.jackson.core.io.IOContext;
/*     */ import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*     */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*     */ import com.fasterxml.jackson.core.util.VersionUtil;
/*     */ import java.io.IOException;
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
/*     */ public abstract class JsonGeneratorImpl
/*     */   extends GeneratorBase
/*     */ {
/*  32 */   protected static final int[] sOutputEscapes = CharTypes.get7BitOutputEscapes();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  40 */   protected static final JacksonFeatureSet<StreamWriteCapability> JSON_WRITE_CAPABILITIES = DEFAULT_TEXTUAL_WRITE_CAPABILITIES;
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
/*     */   protected final IOContext _ioContext;
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
/*  63 */   protected int[] _outputEscapes = sOutputEscapes;
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
/*     */   protected int _maximumNonEscapedChar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CharacterEscapes _characterEscapes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   protected SerializableString _rootValueSeparator = (SerializableString)DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _cfgUnqNames;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGeneratorImpl(IOContext ctxt, int features, ObjectCodec codec) {
/* 114 */     super(features, codec);
/* 115 */     this._ioContext = ctxt;
/* 116 */     if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(features))
/*     */     {
/* 118 */       this._maximumNonEscapedChar = 127;
/*     */     }
/* 120 */     this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(features);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Version version() {
/* 131 */     return VersionUtil.versionFor(getClass());
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
/*     */   public JsonGenerator enable(JsonGenerator.Feature f) {
/* 143 */     super.enable(f);
/* 144 */     if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
/* 145 */       this._cfgUnqNames = false;
/*     */     }
/* 147 */     return (JsonGenerator)this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f) {
/* 153 */     super.disable(f);
/* 154 */     if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
/* 155 */       this._cfgUnqNames = true;
/*     */     }
/* 157 */     return (JsonGenerator)this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
/* 163 */     super._checkStdFeatureChanges(newFeatureFlags, changedFeatures);
/* 164 */     this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(newFeatureFlags);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator setHighestNonEscapedChar(int charCode) {
/* 169 */     this._maximumNonEscapedChar = (charCode < 0) ? 0 : charCode;
/* 170 */     return (JsonGenerator)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHighestEscapedChar() {
/* 175 */     return this._maximumNonEscapedChar;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
/* 181 */     this._characterEscapes = esc;
/* 182 */     if (esc == null) {
/* 183 */       this._outputEscapes = sOutputEscapes;
/*     */     } else {
/* 185 */       this._outputEscapes = esc.getEscapeCodesForAscii();
/*     */     } 
/* 187 */     return (JsonGenerator)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharacterEscapes getCharacterEscapes() {
/* 196 */     return this._characterEscapes;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator setRootValueSeparator(SerializableString sep) {
/* 201 */     this._rootValueSeparator = sep;
/* 202 */     return (JsonGenerator)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
/* 207 */     return JSON_WRITE_CAPABILITIES;
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
/*     */   protected void _verifyPrettyValueWrite(String typeMsg, int status) throws IOException {
/* 219 */     switch (status) {
/*     */       case 1:
/* 221 */         this._cfgPrettyPrinter.writeArrayValueSeparator((JsonGenerator)this);
/*     */         return;
/*     */       case 2:
/* 224 */         this._cfgPrettyPrinter.writeObjectFieldValueSeparator((JsonGenerator)this);
/*     */         return;
/*     */       case 3:
/* 227 */         this._cfgPrettyPrinter.writeRootValueSeparator((JsonGenerator)this);
/*     */         return;
/*     */       
/*     */       case 0:
/* 231 */         if (this._writeContext.inArray()) {
/* 232 */           this._cfgPrettyPrinter.beforeArrayValues((JsonGenerator)this);
/* 233 */         } else if (this._writeContext.inObject()) {
/* 234 */           this._cfgPrettyPrinter.beforeObjectEntries((JsonGenerator)this);
/*     */         } 
/*     */         return;
/*     */       case 5:
/* 238 */         _reportCantWriteValueExpectName(typeMsg);
/*     */         return;
/*     */     } 
/* 241 */     _throwInternal();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _reportCantWriteValueExpectName(String typeMsg) throws IOException {
/* 248 */     _reportError(String.format("Can not %s, expecting field name (context: %s)", new Object[] { typeMsg, this._writeContext
/* 249 */             .typeDesc() }));
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/json/JsonGeneratorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */