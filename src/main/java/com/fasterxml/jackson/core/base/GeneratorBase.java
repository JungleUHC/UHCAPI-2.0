/*     */ package com.fasterxml.jackson.core.base;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.TreeNode;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.core.json.DupDetector;
/*     */ import com.fasterxml.jackson.core.json.JsonWriteContext;
/*     */ import com.fasterxml.jackson.core.json.PackageVersion;
/*     */ import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class GeneratorBase
/*     */   extends JsonGenerator
/*     */ {
/*     */   public static final int SURR1_FIRST = 55296;
/*     */   public static final int SURR1_LAST = 56319;
/*     */   public static final int SURR2_FIRST = 56320;
/*     */   public static final int SURR2_LAST = 57343;
/*  32 */   protected static final int DERIVED_FEATURES_MASK = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS
/*  33 */     .getMask() | JsonGenerator.Feature.ESCAPE_NON_ASCII
/*  34 */     .getMask() | JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION
/*  35 */     .getMask();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_BINARY = "write a binary value";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_BOOLEAN = "write a boolean value";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_NULL = "write a null";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_NUMBER = "write a number";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_RAW = "write a raw (unencoded) value";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final String WRITE_STRING = "write a string";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final int MAX_BIG_DECIMAL_SCALE = 9999;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ObjectCodec _objectCodec;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int _features;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _cfgNumbersAsStrings;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonWriteContext _writeContext;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _closed;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected GeneratorBase(int features, ObjectCodec codec) {
/* 107 */     this._features = features;
/* 108 */     this._objectCodec = codec;
/*     */     
/* 110 */     DupDetector dups = JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
/* 111 */     this._writeContext = JsonWriteContext.createRootContext(dups);
/* 112 */     this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected GeneratorBase(int features, ObjectCodec codec, JsonWriteContext ctxt) {
/* 119 */     this._features = features;
/* 120 */     this._objectCodec = codec;
/* 121 */     this._writeContext = ctxt;
/* 122 */     this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
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
/* 133 */     return PackageVersion.VERSION;
/*     */   }
/*     */   
/*     */   public Object getCurrentValue() {
/* 137 */     return this._writeContext.getCurrentValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentValue(Object v) {
/* 142 */     if (this._writeContext != null) {
/* 143 */       this._writeContext.setCurrentValue(v);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEnabled(JsonGenerator.Feature f) {
/* 154 */     return ((this._features & f.getMask()) != 0); } public int getFeatureMask() {
/* 155 */     return this._features;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator enable(JsonGenerator.Feature f) {
/* 162 */     int mask = f.getMask();
/* 163 */     this._features |= mask;
/* 164 */     if ((mask & DERIVED_FEATURES_MASK) != 0)
/*     */     {
/* 166 */       if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/* 167 */         this._cfgNumbersAsStrings = true;
/* 168 */       } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/* 169 */         setHighestNonEscapedChar(127);
/* 170 */       } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION && 
/* 171 */         this._writeContext.getDupDetector() == null) {
/* 172 */         this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this));
/*     */       } 
/*     */     }
/*     */     
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f) {
/* 182 */     int mask = f.getMask();
/* 183 */     this._features &= mask ^ 0xFFFFFFFF;
/* 184 */     if ((mask & DERIVED_FEATURES_MASK) != 0) {
/* 185 */       if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/* 186 */         this._cfgNumbersAsStrings = false;
/* 187 */       } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/* 188 */         setHighestNonEscapedChar(0);
/* 189 */       } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION) {
/* 190 */         this._writeContext = this._writeContext.withDupDetector(null);
/*     */       } 
/*     */     }
/* 193 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonGenerator setFeatureMask(int newMask) {
/* 199 */     int changed = newMask ^ this._features;
/* 200 */     this._features = newMask;
/* 201 */     if (changed != 0) {
/* 202 */       _checkStdFeatureChanges(newMask, changed);
/*     */     }
/* 204 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator overrideStdFeatures(int values, int mask) {
/* 209 */     int oldState = this._features;
/* 210 */     int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/* 211 */     int changed = oldState ^ newState;
/* 212 */     if (changed != 0) {
/* 213 */       this._features = newState;
/* 214 */       _checkStdFeatureChanges(newState, changed);
/*     */     } 
/* 216 */     return this;
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
/*     */   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
/* 231 */     if ((changedFeatures & DERIVED_FEATURES_MASK) == 0) {
/*     */       return;
/*     */     }
/* 234 */     this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(newFeatureFlags);
/* 235 */     if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(changedFeatures)) {
/* 236 */       if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(newFeatureFlags)) {
/* 237 */         setHighestNonEscapedChar(127);
/*     */       } else {
/* 239 */         setHighestNonEscapedChar(0);
/*     */       } 
/*     */     }
/* 242 */     if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(changedFeatures)) {
/* 243 */       if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(newFeatureFlags)) {
/* 244 */         if (this._writeContext.getDupDetector() == null) {
/* 245 */           this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this));
/*     */         }
/*     */       } else {
/* 248 */         this._writeContext = this._writeContext.withDupDetector(null);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator useDefaultPrettyPrinter() {
/* 255 */     if (getPrettyPrinter() != null) {
/* 256 */       return this;
/*     */     }
/* 258 */     return setPrettyPrinter(_constructDefaultPrettyPrinter());
/*     */   }
/*     */   
/*     */   public JsonGenerator setCodec(ObjectCodec oc) {
/* 262 */     this._objectCodec = oc;
/* 263 */     return this;
/*     */   }
/*     */   public ObjectCodec getCodec() {
/* 266 */     return this._objectCodec;
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
/*     */   public JsonStreamContext getOutputContext() {
/* 279 */     return (JsonStreamContext)this._writeContext;
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
/*     */   public void writeStartObject(Object forValue) throws IOException {
/* 295 */     writeStartObject();
/* 296 */     if (forValue != null) {
/* 297 */       setCurrentValue(forValue);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeFieldName(SerializableString name) throws IOException {
/* 308 */     writeFieldName(name.getValue());
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
/*     */   public void writeString(SerializableString text) throws IOException {
/* 323 */     writeString(text.getValue());
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text) throws IOException {
/* 327 */     _verifyValueWrite("write raw value");
/* 328 */     writeRaw(text);
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text, int offset, int len) throws IOException {
/* 332 */     _verifyValueWrite("write raw value");
/* 333 */     writeRaw(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeRawValue(char[] text, int offset, int len) throws IOException {
/* 337 */     _verifyValueWrite("write raw value");
/* 338 */     writeRaw(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeRawValue(SerializableString text) throws IOException {
/* 342 */     _verifyValueWrite("write raw value");
/* 343 */     writeRaw(text);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
/* 349 */     _reportUnsupportedOperation();
/* 350 */     return 0;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeObject(Object value) throws IOException {
/* 379 */     if (value == null) {
/*     */       
/* 381 */       writeNull();
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 388 */       if (this._objectCodec != null) {
/* 389 */         this._objectCodec.writeValue(this, value);
/*     */         return;
/*     */       } 
/* 392 */       _writeSimpleObject(value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTree(TreeNode rootNode) throws IOException {
/* 399 */     if (rootNode == null) {
/* 400 */       writeNull();
/*     */     } else {
/* 402 */       if (this._objectCodec == null) {
/* 403 */         throw new IllegalStateException("No ObjectCodec defined");
/*     */       }
/* 405 */       this._objectCodec.writeValue(this, rootNode);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void flush() throws IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 416 */     this._closed = true; } public boolean isClosed() {
/* 417 */     return this._closed;
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
/*     */   protected abstract void _releaseBuffers();
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
/*     */   protected abstract void _verifyValueWrite(String paramString) throws IOException;
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
/*     */   protected PrettyPrinter _constructDefaultPrettyPrinter() {
/* 453 */     return (PrettyPrinter)new DefaultPrettyPrinter();
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
/*     */   protected String _asString(BigDecimal value) throws IOException {
/* 469 */     if (JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._features)) {
/*     */       
/* 471 */       int scale = value.scale();
/* 472 */       if (scale < -9999 || scale > 9999)
/* 473 */         _reportError(String.format("Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]", new Object[] {
/*     */                 
/* 475 */                 Integer.valueOf(scale), Integer.valueOf(9999), Integer.valueOf(9999)
/*     */               })); 
/* 477 */       return value.toPlainString();
/*     */     } 
/* 479 */     return value.toString();
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
/*     */   protected final int _decodeSurrogate(int surr1, int surr2) throws IOException {
/* 492 */     if (surr2 < 56320 || surr2 > 57343) {
/* 493 */       String msg = String.format("Incomplete surrogate pair: first char 0x%04X, second 0x%04X", new Object[] {
/* 494 */             Integer.valueOf(surr1), Integer.valueOf(surr2) });
/* 495 */       _reportError(msg);
/*     */     } 
/* 497 */     int c = 65536 + (surr1 - 55296 << 10) + surr2 - 56320;
/* 498 */     return c;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/base/GeneratorBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */