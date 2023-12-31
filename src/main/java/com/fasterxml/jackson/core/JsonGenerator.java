/*      */ package com.fasterxml.jackson.core;
/*      */ 
/*      */ import com.fasterxml.jackson.core.io.CharacterEscapes;
/*      */ import com.fasterxml.jackson.core.type.WritableTypeId;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeature;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*      */ import com.fasterxml.jackson.core.util.VersionUtil;
/*      */ import java.io.Closeable;
/*      */ import java.io.Flushable;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class JsonGenerator
/*      */   implements Closeable, Flushable, Versioned
/*      */ {
/*   41 */   protected static final JacksonFeatureSet<StreamWriteCapability> DEFAULT_WRITE_CAPABILITIES = JacksonFeatureSet.fromDefaults((JacksonFeature[])StreamWriteCapability.values());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   49 */   protected static final JacksonFeatureSet<StreamWriteCapability> DEFAULT_TEXTUAL_WRITE_CAPABILITIES = DEFAULT_WRITE_CAPABILITIES
/*   50 */     .with(StreamWriteCapability.CAN_WRITE_FORMATTED_NUMBERS);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   58 */   protected static final JacksonFeatureSet<StreamWriteCapability> DEFAULT_BINARY_WRITE_CAPABILITIES = DEFAULT_WRITE_CAPABILITIES
/*   59 */     .with(StreamWriteCapability.CAN_WRITE_BINARY_NATIVELY);
/*      */ 
/*      */   
/*      */   protected PrettyPrinter _cfgPrettyPrinter;
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract JsonGenerator setCodec(ObjectCodec paramObjectCodec);
/*      */ 
/*      */   
/*      */   public abstract ObjectCodec getCodec();
/*      */ 
/*      */   
/*      */   public abstract Version version();
/*      */ 
/*      */   
/*      */   public abstract JsonStreamContext getOutputContext();
/*      */ 
/*      */   
/*      */   public enum Feature
/*      */   {
/*   80 */     AUTO_CLOSE_TARGET(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   92 */     AUTO_CLOSE_JSON_CONTENT(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  105 */     FLUSH_PASSED_TO_STREAM(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  120 */     QUOTE_FIELD_NAMES(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  137 */     QUOTE_NON_NUMERIC_NUMBERS(true),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  159 */     ESCAPE_NON_ASCII(false),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  184 */     WRITE_NUMBERS_AS_STRINGS(false),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  201 */     WRITE_BIGDECIMAL_AS_PLAIN(false),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  220 */     STRICT_DUPLICATE_DETECTION(false),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  242 */     IGNORE_UNKNOWN(false);
/*      */ 
/*      */ 
/*      */     
/*      */     private final boolean _defaultState;
/*      */ 
/*      */ 
/*      */     
/*      */     private final int _mask;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int collectDefaults() {
/*  256 */       int flags = 0;
/*  257 */       for (Feature f : values()) {
/*  258 */         if (f.enabledByDefault()) {
/*  259 */           flags |= f.getMask();
/*      */         }
/*      */       } 
/*  262 */       return flags;
/*      */     }
/*      */     
/*      */     Feature(boolean defaultState) {
/*  266 */       this._defaultState = defaultState;
/*  267 */       this._mask = 1 << ordinal();
/*      */     }
/*      */     public boolean enabledByDefault() {
/*  270 */       return this._defaultState;
/*      */     }
/*      */     public boolean enabledIn(int flags) {
/*  273 */       return ((flags & this._mask) != 0);
/*      */     } public int getMask() {
/*  275 */       return this._mask;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getOutputTarget() {
/*  360 */     return null;
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
/*      */   public Object currentValue() {
/*  380 */     return getCurrentValue();
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
/*      */   public void assignCurrentValue(Object v) {
/*  395 */     setCurrentValue(v);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getCurrentValue() {
/*  406 */     JsonStreamContext ctxt = getOutputContext();
/*  407 */     return (ctxt == null) ? null : ctxt.getCurrentValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCurrentValue(Object v) {
/*  418 */     JsonStreamContext ctxt = getOutputContext();
/*  419 */     if (ctxt != null) {
/*  420 */       ctxt.setCurrentValue(v);
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
/*      */   public abstract JsonGenerator enable(Feature paramFeature);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract JsonGenerator disable(Feature paramFeature);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final JsonGenerator configure(Feature f, boolean state) {
/*  460 */     if (state) { enable(f); } else { disable(f); }
/*  461 */      return this;
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
/*      */   public abstract boolean isEnabled(Feature paramFeature);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(StreamWriteFeature f) {
/*  485 */     return isEnabled(f.mappedFeature());
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
/*      */   public abstract int getFeatureMask();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public abstract JsonGenerator setFeatureMask(int paramInt);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator overrideStdFeatures(int values, int mask) {
/*  531 */     int oldState = getFeatureMask();
/*  532 */     int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/*  533 */     return setFeatureMask(newState);
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
/*      */   public int getFormatFeatures() {
/*  545 */     return 0;
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
/*      */   public JsonGenerator overrideFormatFeatures(int values, int mask) {
/*  567 */     return this;
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
/*      */   public void setSchema(FormatSchema schema) {
/*  592 */     throw new UnsupportedOperationException(String.format("Generator of type %s does not support schema of type '%s'", new Object[] {
/*      */             
/*  594 */             getClass().getName(), schema.getSchemaType()
/*      */           }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FormatSchema getSchema() {
/*  603 */     return null;
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
/*      */   public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
/*  625 */     this._cfgPrettyPrinter = pp;
/*  626 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PrettyPrinter getPrettyPrinter() {
/*  636 */     return this._cfgPrettyPrinter;
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
/*      */   public abstract JsonGenerator useDefaultPrettyPrinter();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator setHighestNonEscapedChar(int charCode) {
/*  671 */     return this;
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
/*      */   public int getHighestEscapedChar() {
/*  685 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CharacterEscapes getCharacterEscapes() {
/*  693 */     return null;
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
/*      */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
/*  705 */     return this;
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
/*      */   public JsonGenerator setRootValueSeparator(SerializableString sep) {
/*  719 */     throw new UnsupportedOperationException();
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
/*      */   public int getOutputBuffered() {
/*  747 */     return -1;
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
/*      */   public boolean canUseSchema(FormatSchema schema) {
/*  764 */     return false;
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
/*      */   public boolean canWriteObjectId() {
/*  784 */     return false;
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
/*      */   public boolean canWriteTypeId() {
/*  804 */     return false;
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
/*      */   public boolean canWriteBinaryNatively() {
/*  821 */     return false;
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
/*      */   public boolean canOmitFields() {
/*  834 */     return true;
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
/*      */   public boolean canWriteFormattedNumbers() {
/*  853 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
/*  864 */     return DEFAULT_WRITE_CAPABILITIES;
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
/*      */   public abstract void writeStartArray() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public void writeStartArray(int size) throws IOException {
/*  910 */     writeStartArray();
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
/*      */   public void writeStartArray(Object forValue) throws IOException {
/*  927 */     writeStartArray();
/*  928 */     setCurrentValue(forValue);
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
/*      */   public void writeStartArray(Object forValue, int size) throws IOException {
/*  950 */     writeStartArray(size);
/*  951 */     setCurrentValue(forValue);
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
/*      */   public abstract void writeEndArray() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeStartObject() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue) throws IOException {
/* 1001 */     writeStartObject();
/* 1002 */     setCurrentValue(forValue);
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
/*      */   public void writeStartObject(Object forValue, int size) throws IOException {
/* 1032 */     writeStartObject();
/* 1033 */     setCurrentValue(forValue);
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
/*      */   public abstract void writeEndObject() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeFieldName(String paramString) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeFieldName(SerializableString paramSerializableString) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldId(long id) throws IOException {
/* 1101 */     writeFieldName(Long.toString(id));
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
/*      */   public void writeArray(int[] array, int offset, int length) throws IOException {
/* 1126 */     if (array == null) {
/* 1127 */       throw new IllegalArgumentException("null array");
/*      */     }
/* 1129 */     _verifyOffsets(array.length, offset, length);
/* 1130 */     writeStartArray(array, length);
/* 1131 */     for (int i = offset, end = offset + length; i < end; i++) {
/* 1132 */       writeNumber(array[i]);
/*      */     }
/* 1134 */     writeEndArray();
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
/*      */   public void writeArray(long[] array, int offset, int length) throws IOException {
/* 1153 */     if (array == null) {
/* 1154 */       throw new IllegalArgumentException("null array");
/*      */     }
/* 1156 */     _verifyOffsets(array.length, offset, length);
/* 1157 */     writeStartArray(array, length);
/* 1158 */     for (int i = offset, end = offset + length; i < end; i++) {
/* 1159 */       writeNumber(array[i]);
/*      */     }
/* 1161 */     writeEndArray();
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
/*      */   public void writeArray(double[] array, int offset, int length) throws IOException {
/* 1180 */     if (array == null) {
/* 1181 */       throw new IllegalArgumentException("null array");
/*      */     }
/* 1183 */     _verifyOffsets(array.length, offset, length);
/* 1184 */     writeStartArray(array, length);
/* 1185 */     for (int i = offset, end = offset + length; i < end; i++) {
/* 1186 */       writeNumber(array[i]);
/*      */     }
/* 1188 */     writeEndArray();
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
/*      */   public void writeArray(String[] array, int offset, int length) throws IOException {
/* 1207 */     if (array == null) {
/* 1208 */       throw new IllegalArgumentException("null array");
/*      */     }
/* 1210 */     _verifyOffsets(array.length, offset, length);
/* 1211 */     writeStartArray(array, length);
/* 1212 */     for (int i = offset, end = offset + length; i < end; i++) {
/* 1213 */       writeString(array[i]);
/*      */     }
/* 1215 */     writeEndArray();
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
/*      */   public abstract void writeString(String paramString) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(Reader reader, int len) throws IOException {
/* 1263 */     _reportUnsupportedOperation();
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
/*      */   public abstract void writeString(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeString(SerializableString paramSerializableString) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRawUTF8String(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeUTF8String(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRaw(String paramString) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRaw(String paramString, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRaw(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRaw(char paramChar) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRaw(SerializableString raw) throws IOException {
/* 1461 */     writeRaw(raw.getValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRawValue(String paramString) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRawValue(String paramString, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeRawValue(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawValue(SerializableString raw) throws IOException {
/* 1496 */     writeRawValue(raw.getValue());
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
/*      */   public abstract void writeBinary(Base64Variant paramBase64Variant, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBinary(byte[] data, int offset, int len) throws IOException {
/* 1542 */     writeBinary(Base64Variants.getDefaultVariant(), data, offset, len);
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
/*      */   public void writeBinary(byte[] data) throws IOException {
/* 1557 */     writeBinary(Base64Variants.getDefaultVariant(), data, 0, data.length);
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
/*      */   public int writeBinary(InputStream data, int dataLength) throws IOException {
/* 1580 */     return writeBinary(Base64Variants.getDefaultVariant(), data, dataLength);
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
/*      */   public abstract int writeBinary(Base64Variant paramBase64Variant, InputStream paramInputStream, int paramInt) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(short v) throws IOException {
/* 1628 */     writeNumber(v);
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
/*      */   public abstract void writeNumber(int paramInt) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeNumber(long paramLong) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeNumber(BigInteger paramBigInteger) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeNumber(double paramDouble) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeNumber(float paramFloat) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeNumber(BigDecimal paramBigDecimal) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeNumber(String paramString) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(char[] encodedValueBuffer, int offset, int len) throws IOException {
/* 1755 */     writeNumber(new String(encodedValueBuffer, offset, len));
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
/*      */   public abstract void writeBoolean(boolean paramBoolean) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeNull() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeEmbeddedObject(Object object) throws IOException {
/* 1808 */     if (object == null) {
/* 1809 */       writeNull();
/*      */       return;
/*      */     } 
/* 1812 */     if (object instanceof byte[]) {
/* 1813 */       writeBinary((byte[])object);
/*      */       return;
/*      */     } 
/* 1816 */     throw new JsonGenerationException("No native support for writing embedded objects of type " + object
/* 1817 */         .getClass().getName(), this);
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
/*      */   public void writeObjectId(Object id) throws IOException {
/* 1846 */     throw new JsonGenerationException("No native support for writing Object Ids", this);
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
/*      */   public void writeObjectRef(Object referenced) throws IOException {
/* 1866 */     throw new JsonGenerationException("No native support for writing Object Ids", this);
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
/*      */   public void writeTypeId(Object id) throws IOException {
/* 1888 */     throw new JsonGenerationException("No native support for writing Type Ids", this);
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
/*      */   public WritableTypeId writeTypePrefix(WritableTypeId typeIdDef) throws IOException {
/* 1917 */     Object id = typeIdDef.id;
/*      */     
/* 1919 */     JsonToken valueShape = typeIdDef.valueShape;
/* 1920 */     if (canWriteTypeId()) {
/* 1921 */       typeIdDef.wrapperWritten = false;
/*      */       
/* 1923 */       writeTypeId(id);
/*      */     }
/*      */     else {
/*      */       
/* 1927 */       String idStr = (id instanceof String) ? (String)id : String.valueOf(id);
/* 1928 */       typeIdDef.wrapperWritten = true;
/*      */       
/* 1930 */       WritableTypeId.Inclusion incl = typeIdDef.include;
/*      */       
/* 1932 */       if (valueShape != JsonToken.START_OBJECT && incl
/* 1933 */         .requiresObjectContext()) {
/* 1934 */         typeIdDef.include = incl = WritableTypeId.Inclusion.WRAPPER_ARRAY;
/*      */       }
/*      */       
/* 1937 */       switch (incl) {
/*      */         case PARENT_PROPERTY:
/*      */         case PAYLOAD_PROPERTY:
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case METADATA_PROPERTY:
/* 1949 */           writeStartObject(typeIdDef.forValue);
/* 1950 */           writeStringField(typeIdDef.asProperty, idStr);
/* 1951 */           return typeIdDef;
/*      */ 
/*      */         
/*      */         case WRAPPER_OBJECT:
/* 1955 */           writeStartObject();
/* 1956 */           writeFieldName(idStr);
/*      */           break;
/*      */         
/*      */         default:
/* 1960 */           writeStartArray();
/* 1961 */           writeString(idStr);
/*      */           break;
/*      */       } 
/*      */     } 
/* 1965 */     if (valueShape == JsonToken.START_OBJECT) {
/* 1966 */       writeStartObject(typeIdDef.forValue);
/* 1967 */     } else if (valueShape == JsonToken.START_ARRAY) {
/*      */       
/* 1969 */       writeStartArray();
/*      */     } 
/* 1971 */     return typeIdDef;
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
/*      */   public WritableTypeId writeTypeSuffix(WritableTypeId typeIdDef) throws IOException {
/* 1993 */     JsonToken valueShape = typeIdDef.valueShape;
/*      */     
/* 1995 */     if (valueShape == JsonToken.START_OBJECT) {
/* 1996 */       writeEndObject();
/* 1997 */     } else if (valueShape == JsonToken.START_ARRAY) {
/* 1998 */       writeEndArray();
/*      */     } 
/*      */     
/* 2001 */     if (typeIdDef.wrapperWritten) {
/* 2002 */       Object id; String idStr; switch (typeIdDef.include) {
/*      */         case WRAPPER_ARRAY:
/* 2004 */           writeEndArray();
/*      */ 
/*      */ 
/*      */         
/*      */         case PARENT_PROPERTY:
/* 2009 */           id = typeIdDef.id;
/* 2010 */           idStr = (id instanceof String) ? (String)id : String.valueOf(id);
/* 2011 */           writeStringField(typeIdDef.asProperty, idStr);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case PAYLOAD_PROPERTY:
/*      */         case METADATA_PROPERTY:
/* 2024 */           return typeIdDef;
/*      */       } 
/*      */       writeEndObject();
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
/*      */   
/*      */   public void writePOJO(Object pojo) throws IOException {
/* 2052 */     writeObject(pojo);
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
/*      */   public abstract void writeObject(Object paramObject) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void writeTree(TreeNode paramTreeNode) throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeBinaryField(String fieldName, byte[] data) throws IOException {
/* 2107 */     writeFieldName(fieldName);
/* 2108 */     writeBinary(data);
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
/*      */   public void writeBooleanField(String fieldName, boolean value) throws IOException {
/* 2126 */     writeFieldName(fieldName);
/* 2127 */     writeBoolean(value);
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
/*      */   public void writeNullField(String fieldName) throws IOException {
/* 2144 */     writeFieldName(fieldName);
/* 2145 */     writeNull();
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
/*      */   public void writeStringField(String fieldName, String value) throws IOException {
/* 2163 */     writeFieldName(fieldName);
/* 2164 */     writeString(value);
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
/*      */   public void writeNumberField(String fieldName, short value) throws IOException {
/* 2184 */     writeFieldName(fieldName);
/* 2185 */     writeNumber(value);
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
/*      */   public void writeNumberField(String fieldName, int value) throws IOException {
/* 2203 */     writeFieldName(fieldName);
/* 2204 */     writeNumber(value);
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
/*      */   public void writeNumberField(String fieldName, long value) throws IOException {
/* 2222 */     writeFieldName(fieldName);
/* 2223 */     writeNumber(value);
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
/*      */   public void writeNumberField(String fieldName, BigInteger value) throws IOException {
/* 2243 */     writeFieldName(fieldName);
/* 2244 */     writeNumber(value);
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
/*      */   public void writeNumberField(String fieldName, float value) throws IOException {
/* 2262 */     writeFieldName(fieldName);
/* 2263 */     writeNumber(value);
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
/*      */   public void writeNumberField(String fieldName, double value) throws IOException {
/* 2281 */     writeFieldName(fieldName);
/* 2282 */     writeNumber(value);
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
/*      */   public void writeNumberField(String fieldName, BigDecimal value) throws IOException {
/* 2301 */     writeFieldName(fieldName);
/* 2302 */     writeNumber(value);
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
/*      */   public void writeArrayFieldStart(String fieldName) throws IOException {
/* 2324 */     writeFieldName(fieldName);
/* 2325 */     writeStartArray();
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
/*      */   public void writeObjectFieldStart(String fieldName) throws IOException {
/* 2347 */     writeFieldName(fieldName);
/* 2348 */     writeStartObject();
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
/*      */   public void writePOJOField(String fieldName, Object pojo) throws IOException {
/* 2370 */     writeObjectField(fieldName, pojo);
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
/*      */   public void writeObjectField(String fieldName, Object pojo) throws IOException {
/* 2384 */     writeFieldName(fieldName);
/* 2385 */     writeObject(pojo);
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
/*      */   public void writeOmittedField(String fieldName) throws IOException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyCurrentEvent(JsonParser p) throws IOException {
/*      */     JsonParser.NumberType n;
/* 2429 */     JsonToken t = p.currentToken();
/* 2430 */     int token = (t == null) ? -1 : t.id();
/* 2431 */     switch (token) {
/*      */       case -1:
/* 2433 */         _reportError("No current event to copy");
/*      */         return;
/*      */       case 1:
/* 2436 */         writeStartObject();
/*      */         return;
/*      */       case 2:
/* 2439 */         writeEndObject();
/*      */         return;
/*      */       case 3:
/* 2442 */         writeStartArray();
/*      */         return;
/*      */       case 4:
/* 2445 */         writeEndArray();
/*      */         return;
/*      */       case 5:
/* 2448 */         writeFieldName(p.getCurrentName());
/*      */         return;
/*      */       case 6:
/* 2451 */         if (p.hasTextCharacters()) {
/* 2452 */           writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
/*      */         } else {
/* 2454 */           writeString(p.getText());
/*      */         } 
/*      */         return;
/*      */       
/*      */       case 7:
/* 2459 */         n = p.getNumberType();
/* 2460 */         if (n == JsonParser.NumberType.INT) {
/* 2461 */           writeNumber(p.getIntValue());
/* 2462 */         } else if (n == JsonParser.NumberType.BIG_INTEGER) {
/* 2463 */           writeNumber(p.getBigIntegerValue());
/*      */         } else {
/* 2465 */           writeNumber(p.getLongValue());
/*      */         } 
/*      */         return;
/*      */ 
/*      */       
/*      */       case 8:
/* 2471 */         n = p.getNumberType();
/* 2472 */         if (n == JsonParser.NumberType.BIG_DECIMAL) {
/* 2473 */           writeNumber(p.getDecimalValue());
/* 2474 */         } else if (n == JsonParser.NumberType.FLOAT) {
/* 2475 */           writeNumber(p.getFloatValue());
/*      */         } else {
/* 2477 */           writeNumber(p.getDoubleValue());
/*      */         } 
/*      */         return;
/*      */       
/*      */       case 9:
/* 2482 */         writeBoolean(true);
/*      */         return;
/*      */       case 10:
/* 2485 */         writeBoolean(false);
/*      */         return;
/*      */       case 11:
/* 2488 */         writeNull();
/*      */         return;
/*      */       case 12:
/* 2491 */         writeObject(p.getEmbeddedObject());
/*      */         return;
/*      */     } 
/* 2494 */     throw new IllegalStateException("Internal error: unknown current token, " + t);
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
/*      */ 
/*      */   
/*      */   public void copyCurrentStructure(JsonParser p) throws IOException {
/* 2535 */     JsonToken t = p.currentToken();
/*      */     
/* 2537 */     int id = (t == null) ? -1 : t.id();
/* 2538 */     if (id == 5) {
/* 2539 */       writeFieldName(p.getCurrentName());
/* 2540 */       t = p.nextToken();
/* 2541 */       id = (t == null) ? -1 : t.id();
/*      */     } 
/*      */     
/* 2544 */     switch (id) {
/*      */       case 1:
/* 2546 */         writeStartObject();
/* 2547 */         _copyCurrentContents(p);
/*      */         return;
/*      */       case 3:
/* 2550 */         writeStartArray();
/* 2551 */         _copyCurrentContents(p);
/*      */         return;
/*      */     } 
/*      */     
/* 2555 */     copyCurrentEvent(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _copyCurrentContents(JsonParser p) throws IOException {
/* 2562 */     int depth = 1;
/*      */     
/*      */     JsonToken t;
/*      */     
/* 2566 */     while ((t = p.nextToken()) != null) {
/* 2567 */       JsonParser.NumberType n; switch (t.id()) {
/*      */         case 5:
/* 2569 */           writeFieldName(p.getCurrentName());
/*      */           continue;
/*      */         
/*      */         case 3:
/* 2573 */           writeStartArray();
/* 2574 */           depth++;
/*      */           continue;
/*      */         
/*      */         case 1:
/* 2578 */           writeStartObject();
/* 2579 */           depth++;
/*      */           continue;
/*      */         
/*      */         case 4:
/* 2583 */           writeEndArray();
/* 2584 */           if (--depth == 0) {
/*      */             return;
/*      */           }
/*      */           continue;
/*      */         case 2:
/* 2589 */           writeEndObject();
/* 2590 */           if (--depth == 0) {
/*      */             return;
/*      */           }
/*      */           continue;
/*      */         
/*      */         case 6:
/* 2596 */           if (p.hasTextCharacters()) {
/* 2597 */             writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength()); continue;
/*      */           } 
/* 2599 */           writeString(p.getText());
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 7:
/* 2604 */           n = p.getNumberType();
/* 2605 */           if (n == JsonParser.NumberType.INT) {
/* 2606 */             writeNumber(p.getIntValue()); continue;
/* 2607 */           }  if (n == JsonParser.NumberType.BIG_INTEGER) {
/* 2608 */             writeNumber(p.getBigIntegerValue()); continue;
/*      */           } 
/* 2610 */           writeNumber(p.getLongValue());
/*      */           continue;
/*      */ 
/*      */ 
/*      */         
/*      */         case 8:
/* 2616 */           n = p.getNumberType();
/* 2617 */           if (n == JsonParser.NumberType.BIG_DECIMAL) {
/* 2618 */             writeNumber(p.getDecimalValue()); continue;
/* 2619 */           }  if (n == JsonParser.NumberType.FLOAT) {
/* 2620 */             writeNumber(p.getFloatValue()); continue;
/*      */           } 
/* 2622 */           writeNumber(p.getDoubleValue());
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 9:
/* 2627 */           writeBoolean(true);
/*      */           continue;
/*      */         case 10:
/* 2630 */           writeBoolean(false);
/*      */           continue;
/*      */         case 11:
/* 2633 */           writeNull();
/*      */           continue;
/*      */         case 12:
/* 2636 */           writeObject(p.getEmbeddedObject());
/*      */           continue;
/*      */       } 
/* 2639 */       throw new IllegalStateException("Internal error: unknown current token, " + t);
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
/*      */   public abstract void flush() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean isClosed();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void close() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportError(String msg) throws JsonGenerationException {
/* 2710 */     throw new JsonGenerationException(msg, this);
/*      */   }
/*      */   protected final void _throwInternal() {
/* 2713 */     VersionUtil.throwInternal();
/*      */   }
/*      */   protected void _reportUnsupportedOperation() {
/* 2716 */     throw new UnsupportedOperationException("Operation not supported by generator of type " + getClass().getName());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _verifyOffsets(int arrayLength, int offset, int length) {
/* 2722 */     if (offset < 0 || offset + length > arrayLength) {
/* 2723 */       throw new IllegalArgumentException(String.format("invalid argument(s) (offset=%d, length=%d) for input array of %d element", new Object[] {
/*      */               
/* 2725 */               Integer.valueOf(offset), Integer.valueOf(length), Integer.valueOf(arrayLength)
/*      */             }));
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
/*      */   protected void _writeSimpleObject(Object value) throws IOException {
/* 2744 */     if (value == null) {
/* 2745 */       writeNull();
/*      */       return;
/*      */     } 
/* 2748 */     if (value instanceof String) {
/* 2749 */       writeString((String)value);
/*      */       return;
/*      */     } 
/* 2752 */     if (value instanceof Number) {
/* 2753 */       Number n = (Number)value;
/* 2754 */       if (n instanceof Integer) {
/* 2755 */         writeNumber(n.intValue()); return;
/*      */       } 
/* 2757 */       if (n instanceof Long) {
/* 2758 */         writeNumber(n.longValue()); return;
/*      */       } 
/* 2760 */       if (n instanceof Double) {
/* 2761 */         writeNumber(n.doubleValue()); return;
/*      */       } 
/* 2763 */       if (n instanceof Float) {
/* 2764 */         writeNumber(n.floatValue()); return;
/*      */       } 
/* 2766 */       if (n instanceof Short) {
/* 2767 */         writeNumber(n.shortValue()); return;
/*      */       } 
/* 2769 */       if (n instanceof Byte) {
/* 2770 */         writeNumber((short)n.byteValue()); return;
/*      */       } 
/* 2772 */       if (n instanceof BigInteger) {
/* 2773 */         writeNumber((BigInteger)n); return;
/*      */       } 
/* 2775 */       if (n instanceof BigDecimal) {
/* 2776 */         writeNumber((BigDecimal)n);
/*      */         
/*      */         return;
/*      */       } 
/* 2780 */       if (n instanceof AtomicInteger) {
/* 2781 */         writeNumber(((AtomicInteger)n).get()); return;
/*      */       } 
/* 2783 */       if (n instanceof AtomicLong) {
/* 2784 */         writeNumber(((AtomicLong)n).get()); return;
/*      */       } 
/*      */     } else {
/* 2787 */       if (value instanceof byte[]) {
/* 2788 */         writeBinary((byte[])value); return;
/*      */       } 
/* 2790 */       if (value instanceof Boolean) {
/* 2791 */         writeBoolean(((Boolean)value).booleanValue()); return;
/*      */       } 
/* 2793 */       if (value instanceof AtomicBoolean) {
/* 2794 */         writeBoolean(((AtomicBoolean)value).get()); return;
/*      */       } 
/*      */     } 
/* 2797 */     throw new IllegalStateException("No ObjectCodec defined for the generator, can only serialize simple wrapper types (type passed " + value
/* 2798 */         .getClass().getName() + ")");
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/core/JsonGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */