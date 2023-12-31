/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.FormatSchema;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.StreamWriteCapability;
/*     */ import com.fasterxml.jackson.core.TreeNode;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonGeneratorDelegate
/*     */   extends JsonGenerator
/*     */ {
/*     */   protected JsonGenerator delegate;
/*     */   protected boolean delegateCopyMethods;
/*     */   
/*     */   public JsonGeneratorDelegate(JsonGenerator d) {
/*  33 */     this(d, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGeneratorDelegate(JsonGenerator d, boolean delegateCopyMethods) {
/*  43 */     this.delegate = d;
/*  44 */     this.delegateCopyMethods = delegateCopyMethods;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectCodec getCodec() {
/*  53 */     return this.delegate.getCodec();
/*     */   }
/*     */   public JsonGenerator setCodec(ObjectCodec oc) {
/*  56 */     this.delegate.setCodec(oc);
/*  57 */     return this;
/*     */   }
/*     */   
/*  60 */   public void setSchema(FormatSchema schema) { this.delegate.setSchema(schema); }
/*  61 */   public FormatSchema getSchema() { return this.delegate.getSchema(); }
/*  62 */   public Version version() { return this.delegate.version(); }
/*  63 */   public Object getOutputTarget() { return this.delegate.getOutputTarget(); } public int getOutputBuffered() {
/*  64 */     return this.delegate.getOutputBuffered();
/*     */   }
/*  66 */   public void assignCurrentValue(Object v) { this.delegate.assignCurrentValue(v); } public Object currentValue() {
/*  67 */     return this.delegate.currentValue();
/*     */   }
/*     */   
/*     */   public void setCurrentValue(Object v) {
/*  71 */     this.delegate.setCurrentValue(v);
/*     */   }
/*     */   
/*     */   public Object getCurrentValue() {
/*  75 */     return this.delegate.getCurrentValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canUseSchema(FormatSchema schema) {
/*  84 */     return this.delegate.canUseSchema(schema);
/*     */   }
/*     */   public boolean canWriteTypeId() {
/*  87 */     return this.delegate.canWriteTypeId();
/*     */   }
/*     */   public boolean canWriteObjectId() {
/*  90 */     return this.delegate.canWriteObjectId();
/*     */   }
/*     */   public boolean canWriteBinaryNatively() {
/*  93 */     return this.delegate.canWriteBinaryNatively();
/*     */   }
/*     */   public boolean canOmitFields() {
/*  96 */     return this.delegate.canOmitFields();
/*     */   }
/*     */   public boolean canWriteFormattedNumbers() {
/*  99 */     return this.delegate.canWriteFormattedNumbers();
/*     */   }
/*     */   
/*     */   public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
/* 103 */     return this.delegate.getWriteCapabilities();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator enable(JsonGenerator.Feature f) {
/* 114 */     this.delegate.enable(f);
/* 115 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f) {
/* 120 */     this.delegate.disable(f);
/* 121 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isEnabled(JsonGenerator.Feature f) {
/* 125 */     return this.delegate.isEnabled(f);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFeatureMask() {
/* 131 */     return this.delegate.getFeatureMask();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonGenerator setFeatureMask(int mask) {
/* 136 */     this.delegate.setFeatureMask(mask);
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator overrideStdFeatures(int values, int mask) {
/* 142 */     this.delegate.overrideStdFeatures(values, mask);
/* 143 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonGenerator overrideFormatFeatures(int values, int mask) {
/* 148 */     this.delegate.overrideFormatFeatures(values, mask);
/* 149 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
/* 160 */     this.delegate.setPrettyPrinter(pp);
/* 161 */     return this;
/*     */   }
/*     */   
/*     */   public PrettyPrinter getPrettyPrinter() {
/* 165 */     return this.delegate.getPrettyPrinter();
/*     */   }
/*     */   public JsonGenerator useDefaultPrettyPrinter() {
/* 168 */     this.delegate.useDefaultPrettyPrinter();
/* 169 */     return this;
/*     */   }
/*     */   public JsonGenerator setHighestNonEscapedChar(int charCode) {
/* 172 */     this.delegate.setHighestNonEscapedChar(charCode);
/* 173 */     return this;
/*     */   }
/*     */   public int getHighestEscapedChar() {
/* 176 */     return this.delegate.getHighestEscapedChar();
/*     */   }
/*     */   public CharacterEscapes getCharacterEscapes() {
/* 179 */     return this.delegate.getCharacterEscapes();
/*     */   }
/*     */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
/* 182 */     this.delegate.setCharacterEscapes(esc);
/* 183 */     return this;
/*     */   }
/*     */   public JsonGenerator setRootValueSeparator(SerializableString sep) {
/* 186 */     this.delegate.setRootValueSeparator(sep);
/* 187 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeStartArray() throws IOException {
/* 196 */     this.delegate.writeStartArray();
/*     */   }
/*     */   
/*     */   public void writeStartArray(int size) throws IOException {
/* 200 */     this.delegate.writeStartArray(size);
/*     */   }
/*     */   public void writeStartArray(Object forValue) throws IOException {
/* 203 */     this.delegate.writeStartArray(forValue);
/*     */   }
/*     */   public void writeStartArray(Object forValue, int size) throws IOException {
/* 206 */     this.delegate.writeStartArray(forValue, size);
/*     */   }
/*     */   public void writeEndArray() throws IOException {
/* 209 */     this.delegate.writeEndArray();
/*     */   }
/*     */   public void writeStartObject() throws IOException {
/* 212 */     this.delegate.writeStartObject();
/*     */   }
/*     */   public void writeStartObject(Object forValue) throws IOException {
/* 215 */     this.delegate.writeStartObject(forValue);
/*     */   }
/*     */   
/*     */   public void writeStartObject(Object forValue, int size) throws IOException {
/* 219 */     this.delegate.writeStartObject(forValue, size);
/*     */   }
/*     */   
/*     */   public void writeEndObject() throws IOException {
/* 223 */     this.delegate.writeEndObject();
/*     */   }
/*     */   
/*     */   public void writeFieldName(String name) throws IOException {
/* 227 */     this.delegate.writeFieldName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFieldName(SerializableString name) throws IOException {
/* 232 */     this.delegate.writeFieldName(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFieldId(long id) throws IOException {
/* 237 */     this.delegate.writeFieldId(id);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeArray(int[] array, int offset, int length) throws IOException {
/* 242 */     this.delegate.writeArray(array, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeArray(long[] array, int offset, int length) throws IOException {
/* 247 */     this.delegate.writeArray(array, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeArray(double[] array, int offset, int length) throws IOException {
/* 252 */     this.delegate.writeArray(array, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeArray(String[] array, int offset, int length) throws IOException {
/* 257 */     this.delegate.writeArray(array, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeString(String text) throws IOException {
/* 267 */     this.delegate.writeString(text);
/*     */   }
/*     */   
/*     */   public void writeString(Reader reader, int len) throws IOException {
/* 271 */     this.delegate.writeString(reader, len);
/*     */   }
/*     */   
/*     */   public void writeString(char[] text, int offset, int len) throws IOException {
/* 275 */     this.delegate.writeString(text, offset, len);
/*     */   }
/*     */   public void writeString(SerializableString text) throws IOException {
/* 278 */     this.delegate.writeString(text);
/*     */   }
/*     */   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/* 281 */     this.delegate.writeRawUTF8String(text, offset, length);
/*     */   }
/*     */   public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
/* 284 */     this.delegate.writeUTF8String(text, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeRaw(String text) throws IOException {
/* 293 */     this.delegate.writeRaw(text);
/*     */   }
/*     */   public void writeRaw(String text, int offset, int len) throws IOException {
/* 296 */     this.delegate.writeRaw(text, offset, len);
/*     */   }
/*     */   public void writeRaw(SerializableString raw) throws IOException {
/* 299 */     this.delegate.writeRaw(raw);
/*     */   }
/*     */   public void writeRaw(char[] text, int offset, int len) throws IOException {
/* 302 */     this.delegate.writeRaw(text, offset, len);
/*     */   }
/*     */   public void writeRaw(char c) throws IOException {
/* 305 */     this.delegate.writeRaw(c);
/*     */   }
/*     */   public void writeRawValue(String text) throws IOException {
/* 308 */     this.delegate.writeRawValue(text);
/*     */   }
/*     */   public void writeRawValue(String text, int offset, int len) throws IOException {
/* 311 */     this.delegate.writeRawValue(text, offset, len);
/*     */   }
/*     */   public void writeRawValue(char[] text, int offset, int len) throws IOException {
/* 314 */     this.delegate.writeRawValue(text, offset, len);
/*     */   }
/*     */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
/* 317 */     this.delegate.writeBinary(b64variant, data, offset, len);
/*     */   }
/*     */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
/* 320 */     return this.delegate.writeBinary(b64variant, data, dataLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeNumber(short v) throws IOException {
/* 329 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(int v) throws IOException {
/* 332 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(long v) throws IOException {
/* 335 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(BigInteger v) throws IOException {
/* 338 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(double v) throws IOException {
/* 341 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(float v) throws IOException {
/* 344 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(BigDecimal v) throws IOException {
/* 347 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   public void writeNumber(String encodedValue) throws IOException, UnsupportedOperationException {
/* 350 */     this.delegate.writeNumber(encodedValue);
/*     */   }
/*     */   public void writeNumber(char[] encodedValueBuffer, int offset, int length) throws IOException, UnsupportedOperationException {
/* 353 */     this.delegate.writeNumber(encodedValueBuffer, offset, length);
/*     */   }
/*     */   public void writeBoolean(boolean state) throws IOException {
/* 356 */     this.delegate.writeBoolean(state);
/*     */   }
/*     */   public void writeNull() throws IOException {
/* 359 */     this.delegate.writeNull();
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
/*     */   public void writeOmittedField(String fieldName) throws IOException {
/* 385 */     this.delegate.writeOmittedField(fieldName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeObjectId(Object id) throws IOException {
/* 395 */     this.delegate.writeObjectId(id);
/*     */   }
/*     */   public void writeObjectRef(Object id) throws IOException {
/* 398 */     this.delegate.writeObjectRef(id);
/*     */   }
/*     */   public void writeTypeId(Object id) throws IOException {
/* 401 */     this.delegate.writeTypeId(id);
/*     */   }
/*     */   public void writeEmbeddedObject(Object object) throws IOException {
/* 404 */     this.delegate.writeEmbeddedObject(object);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writePOJO(Object pojo) throws IOException {
/* 414 */     writeObject(pojo);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeObject(Object pojo) throws IOException {
/* 419 */     if (this.delegateCopyMethods) {
/* 420 */       this.delegate.writeObject(pojo);
/*     */       return;
/*     */     } 
/* 423 */     if (pojo == null) {
/* 424 */       writeNull();
/*     */     } else {
/* 426 */       ObjectCodec c = getCodec();
/* 427 */       if (c != null) {
/* 428 */         c.writeValue(this, pojo);
/*     */         return;
/*     */       } 
/* 431 */       _writeSimpleObject(pojo);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTree(TreeNode tree) throws IOException {
/* 437 */     if (this.delegateCopyMethods) {
/* 438 */       this.delegate.writeTree(tree);
/*     */       
/*     */       return;
/*     */     } 
/* 442 */     if (tree == null) {
/* 443 */       writeNull();
/*     */     } else {
/* 445 */       ObjectCodec c = getCodec();
/* 446 */       if (c == null) {
/* 447 */         throw new IllegalStateException("No ObjectCodec defined");
/*     */       }
/* 449 */       c.writeTree(this, tree);
/*     */     } 
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
/*     */   public void copyCurrentEvent(JsonParser p) throws IOException {
/* 469 */     if (this.delegateCopyMethods) { this.delegate.copyCurrentEvent(p); }
/* 470 */     else { super.copyCurrentEvent(p); }
/*     */   
/*     */   }
/*     */   
/*     */   public void copyCurrentStructure(JsonParser p) throws IOException {
/* 475 */     if (this.delegateCopyMethods) { this.delegate.copyCurrentStructure(p); }
/* 476 */     else { super.copyCurrentStructure(p); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonStreamContext getOutputContext() {
/* 485 */     return this.delegate.getOutputContext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 493 */     this.delegate.flush(); } public void close() throws IOException {
/* 494 */     this.delegate.close();
/*     */   } public boolean isClosed() {
/* 496 */     return this.delegate.isClosed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonGenerator getDelegate() {
/* 505 */     return this.delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonGenerator delegate() {
/* 512 */     return this.delegate;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/util/JsonGeneratorDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */