/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.FormatSchema;
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.StreamReadCapability;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Writer;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonParserDelegate
/*     */   extends JsonParser
/*     */ {
/*     */   protected JsonParser delegate;
/*     */   
/*     */   public JsonParserDelegate(JsonParser d) {
/*  26 */     this.delegate = d;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCodec(ObjectCodec c) {
/*  35 */     this.delegate.setCodec(c); } public ObjectCodec getCodec() {
/*  36 */     return this.delegate.getCodec();
/*     */   }
/*     */   
/*     */   public JsonParser enable(JsonParser.Feature f) {
/*  40 */     this.delegate.enable(f);
/*  41 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonParser disable(JsonParser.Feature f) {
/*  46 */     this.delegate.disable(f);
/*  47 */     return this;
/*     */   }
/*     */   
/*  50 */   public boolean isEnabled(JsonParser.Feature f) { return this.delegate.isEnabled(f); } public int getFeatureMask() {
/*  51 */     return this.delegate.getFeatureMask();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonParser setFeatureMask(int mask) {
/*  56 */     this.delegate.setFeatureMask(mask);
/*  57 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonParser overrideStdFeatures(int values, int mask) {
/*  62 */     this.delegate.overrideStdFeatures(values, mask);
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonParser overrideFormatFeatures(int values, int mask) {
/*  68 */     this.delegate.overrideFormatFeatures(values, mask);
/*  69 */     return this;
/*     */   }
/*     */   
/*  72 */   public FormatSchema getSchema() { return this.delegate.getSchema(); }
/*  73 */   public void setSchema(FormatSchema schema) { this.delegate.setSchema(schema); }
/*  74 */   public boolean canUseSchema(FormatSchema schema) { return this.delegate.canUseSchema(schema); }
/*  75 */   public Version version() { return this.delegate.version(); } public Object getInputSource() {
/*  76 */     return this.delegate.getInputSource();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean requiresCustomCodec() {
/*  84 */     return this.delegate.requiresCustomCodec();
/*     */   } public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
/*  86 */     return this.delegate.getReadCapabilities();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  94 */     this.delegate.close(); } public boolean isClosed() {
/*  95 */     return this.delegate.isClosed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearCurrentToken() {
/* 103 */     this.delegate.clearCurrentToken();
/* 104 */   } public JsonToken getLastClearedToken() { return this.delegate.getLastClearedToken(); } public void overrideCurrentName(String name) {
/* 105 */     this.delegate.overrideCurrentName(name);
/*     */   }
/*     */   public void assignCurrentValue(Object v) {
/* 108 */     this.delegate.assignCurrentValue(v);
/*     */   }
/*     */   
/*     */   public void setCurrentValue(Object v) {
/* 112 */     this.delegate.setCurrentValue(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonStreamContext getParsingContext() {
/* 120 */     return this.delegate.getParsingContext();
/*     */   }
/* 122 */   public JsonToken currentToken() { return this.delegate.currentToken(); }
/* 123 */   public int currentTokenId() { return this.delegate.currentTokenId(); } public String currentName() throws IOException {
/* 124 */     return this.delegate.currentName();
/*     */   } public Object currentValue() {
/* 126 */     return this.delegate.currentValue();
/*     */   }
/*     */   public JsonLocation currentLocation() {
/* 129 */     return this.delegate.getCurrentLocation();
/*     */   } public JsonLocation currentTokenLocation() {
/* 131 */     return this.delegate.getTokenLocation();
/*     */   }
/*     */   
/* 134 */   public JsonToken getCurrentToken() { return this.delegate.getCurrentToken(); } @Deprecated
/*     */   public int getCurrentTokenId() {
/* 136 */     return this.delegate.getCurrentTokenId();
/*     */   } public String getCurrentName() throws IOException {
/* 138 */     return this.delegate.getCurrentName();
/*     */   } public Object getCurrentValue() {
/* 140 */     return this.delegate.getCurrentValue();
/*     */   }
/*     */   public JsonLocation getCurrentLocation() {
/* 143 */     return this.delegate.getCurrentLocation();
/*     */   } public JsonLocation getTokenLocation() {
/* 145 */     return this.delegate.getTokenLocation();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasCurrentToken() {
/* 153 */     return this.delegate.hasCurrentToken();
/* 154 */   } public boolean hasTokenId(int id) { return this.delegate.hasTokenId(id); } public boolean hasToken(JsonToken t) {
/* 155 */     return this.delegate.hasToken(t);
/*     */   }
/* 157 */   public boolean isExpectedStartArrayToken() { return this.delegate.isExpectedStartArrayToken(); }
/* 158 */   public boolean isExpectedStartObjectToken() { return this.delegate.isExpectedStartObjectToken(); } public boolean isExpectedNumberIntToken() {
/* 159 */     return this.delegate.isExpectedNumberIntToken();
/*     */   } public boolean isNaN() throws IOException {
/* 161 */     return this.delegate.isNaN();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() throws IOException {
/* 169 */     return this.delegate.getText();
/* 170 */   } public boolean hasTextCharacters() { return this.delegate.hasTextCharacters(); }
/* 171 */   public char[] getTextCharacters() throws IOException { return this.delegate.getTextCharacters(); }
/* 172 */   public int getTextLength() throws IOException { return this.delegate.getTextLength(); }
/* 173 */   public int getTextOffset() throws IOException { return this.delegate.getTextOffset(); } public int getText(Writer writer) throws IOException, UnsupportedOperationException {
/* 174 */     return this.delegate.getText(writer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigInteger getBigIntegerValue() throws IOException {
/* 183 */     return this.delegate.getBigIntegerValue();
/*     */   }
/*     */   public boolean getBooleanValue() throws IOException {
/* 186 */     return this.delegate.getBooleanValue();
/*     */   }
/*     */   public byte getByteValue() throws IOException {
/* 189 */     return this.delegate.getByteValue();
/*     */   }
/*     */   public short getShortValue() throws IOException {
/* 192 */     return this.delegate.getShortValue();
/*     */   }
/*     */   public BigDecimal getDecimalValue() throws IOException {
/* 195 */     return this.delegate.getDecimalValue();
/*     */   }
/*     */   public double getDoubleValue() throws IOException {
/* 198 */     return this.delegate.getDoubleValue();
/*     */   }
/*     */   public float getFloatValue() throws IOException {
/* 201 */     return this.delegate.getFloatValue();
/*     */   }
/*     */   public int getIntValue() throws IOException {
/* 204 */     return this.delegate.getIntValue();
/*     */   }
/*     */   public long getLongValue() throws IOException {
/* 207 */     return this.delegate.getLongValue();
/*     */   }
/*     */   public JsonParser.NumberType getNumberType() throws IOException {
/* 210 */     return this.delegate.getNumberType();
/*     */   }
/*     */   public Number getNumberValue() throws IOException {
/* 213 */     return this.delegate.getNumberValue();
/*     */   }
/*     */   public Number getNumberValueExact() throws IOException {
/* 216 */     return this.delegate.getNumberValueExact();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValueAsInt() throws IOException {
/* 224 */     return this.delegate.getValueAsInt();
/* 225 */   } public int getValueAsInt(int defaultValue) throws IOException { return this.delegate.getValueAsInt(defaultValue); }
/* 226 */   public long getValueAsLong() throws IOException { return this.delegate.getValueAsLong(); }
/* 227 */   public long getValueAsLong(long defaultValue) throws IOException { return this.delegate.getValueAsLong(defaultValue); }
/* 228 */   public double getValueAsDouble() throws IOException { return this.delegate.getValueAsDouble(); }
/* 229 */   public double getValueAsDouble(double defaultValue) throws IOException { return this.delegate.getValueAsDouble(defaultValue); }
/* 230 */   public boolean getValueAsBoolean() throws IOException { return this.delegate.getValueAsBoolean(); }
/* 231 */   public boolean getValueAsBoolean(boolean defaultValue) throws IOException { return this.delegate.getValueAsBoolean(defaultValue); }
/* 232 */   public String getValueAsString() throws IOException { return this.delegate.getValueAsString(); } public String getValueAsString(String defaultValue) throws IOException {
/* 233 */     return this.delegate.getValueAsString(defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getEmbeddedObject() throws IOException {
/* 241 */     return this.delegate.getEmbeddedObject();
/* 242 */   } public byte[] getBinaryValue(Base64Variant b64variant) throws IOException { return this.delegate.getBinaryValue(b64variant); } public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/* 243 */     return this.delegate.readBinaryValue(b64variant, out);
/*     */   } public JsonToken nextToken() throws IOException {
/* 245 */     return this.delegate.nextToken();
/*     */   } public JsonToken nextValue() throws IOException {
/* 247 */     return this.delegate.nextValue();
/*     */   } public void finishToken() throws IOException {
/* 249 */     this.delegate.finishToken();
/*     */   }
/*     */   
/*     */   public JsonParser skipChildren() throws IOException {
/* 253 */     this.delegate.skipChildren();
/*     */     
/* 255 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canReadObjectId() {
/* 264 */     return this.delegate.canReadObjectId();
/* 265 */   } public boolean canReadTypeId() { return this.delegate.canReadTypeId(); }
/* 266 */   public Object getObjectId() throws IOException { return this.delegate.getObjectId(); } public Object getTypeId() throws IOException {
/* 267 */     return this.delegate.getTypeId();
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
/*     */   public JsonParser delegate() {
/* 282 */     return this.delegate;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/JsonParserDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */