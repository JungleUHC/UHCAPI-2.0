/*      */ package com.fasterxml.jackson.databind.util;
/*      */ import com.fasterxml.jackson.core.Base64Variant;
/*      */ import com.fasterxml.jackson.core.JsonGenerator;
/*      */ import com.fasterxml.jackson.core.JsonLocation;
/*      */ import com.fasterxml.jackson.core.JsonParser;
/*      */ import com.fasterxml.jackson.core.JsonStreamContext;
/*      */ import com.fasterxml.jackson.core.JsonToken;
/*      */ import com.fasterxml.jackson.core.ObjectCodec;
/*      */ import com.fasterxml.jackson.core.SerializableString;
/*      */ import com.fasterxml.jackson.core.StreamReadCapability;
/*      */ import com.fasterxml.jackson.core.StreamWriteCapability;
/*      */ import com.fasterxml.jackson.core.TreeNode;
/*      */ import com.fasterxml.jackson.core.Version;
/*      */ import com.fasterxml.jackson.core.json.JsonWriteContext;
/*      */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*      */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*      */ import com.fasterxml.jackson.databind.DeserializationContext;
/*      */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*      */ import com.fasterxml.jackson.databind.cfg.PackageVersion;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class TokenBuffer extends JsonGenerator {
/*   28 */   protected static final int DEFAULT_GENERATOR_FEATURES = JsonGenerator.Feature.collectDefaults();
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
/*      */   protected JsonStreamContext _parentContext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _generatorFeatures;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _closed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _hasNativeTypeIds;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _hasNativeObjectIds;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _mayHaveNativeIds;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _forceBigDecimal;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Segment _first;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Segment _last;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _appendAt;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object _typeId;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object _objectId;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _hasNativeId = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected JsonWriteContext _writeContext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TokenBuffer(ObjectCodec codec, boolean hasNativeIds) {
/*  147 */     this._objectCodec = codec;
/*  148 */     this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
/*  149 */     this._writeContext = JsonWriteContext.createRootContext(null);
/*      */     
/*  151 */     this._first = this._last = new Segment();
/*  152 */     this._appendAt = 0;
/*  153 */     this._hasNativeTypeIds = hasNativeIds;
/*  154 */     this._hasNativeObjectIds = hasNativeIds;
/*      */     
/*  156 */     this._mayHaveNativeIds = (this._hasNativeTypeIds || this._hasNativeObjectIds);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TokenBuffer(JsonParser p) {
/*  163 */     this(p, (DeserializationContext)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TokenBuffer(JsonParser p, DeserializationContext ctxt) {
/*  171 */     this._objectCodec = p.getCodec();
/*  172 */     this._parentContext = p.getParsingContext();
/*  173 */     this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
/*  174 */     this._writeContext = JsonWriteContext.createRootContext(null);
/*      */     
/*  176 */     this._first = this._last = new Segment();
/*  177 */     this._appendAt = 0;
/*  178 */     this._hasNativeTypeIds = p.canReadTypeId();
/*  179 */     this._hasNativeObjectIds = p.canReadObjectId();
/*  180 */     this._mayHaveNativeIds = (this._hasNativeTypeIds || this._hasNativeObjectIds);
/*  181 */     this
/*  182 */       ._forceBigDecimal = (ctxt == null) ? false : ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
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
/*      */   public static TokenBuffer asCopyOfValue(JsonParser p) throws IOException {
/*  199 */     TokenBuffer b = new TokenBuffer(p);
/*  200 */     b.copyCurrentStructure(p);
/*  201 */     return b;
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
/*      */   public TokenBuffer overrideParentContext(JsonStreamContext ctxt) {
/*  213 */     this._parentContext = ctxt;
/*  214 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TokenBuffer forceUseOfBigDecimal(boolean b) {
/*  221 */     this._forceBigDecimal = b;
/*  222 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public Version version() {
/*  227 */     return PackageVersion.VERSION;
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
/*      */   public JsonParser asParser() {
/*  241 */     return asParser(this._objectCodec);
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
/*      */   public JsonParser asParserOnFirstToken() throws IOException {
/*  255 */     JsonParser p = asParser(this._objectCodec);
/*  256 */     p.nextToken();
/*  257 */     return p;
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
/*      */   public JsonParser asParser(ObjectCodec codec) {
/*  275 */     return (JsonParser)new Parser(this._first, codec, this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonParser asParser(JsonParser src) {
/*  284 */     Parser p = new Parser(this._first, src.getCodec(), this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext);
/*  285 */     p.setLocation(src.getTokenLocation());
/*  286 */     return (JsonParser)p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonToken firstToken() {
/*  297 */     return this._first.type(0);
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
/*      */   public boolean isEmpty() {
/*  309 */     return (this._appendAt == 0 && this._first == this._last);
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
/*      */   public TokenBuffer append(TokenBuffer other) throws IOException {
/*  328 */     if (!this._hasNativeTypeIds) {
/*  329 */       this._hasNativeTypeIds = other.canWriteTypeId();
/*      */     }
/*  331 */     if (!this._hasNativeObjectIds) {
/*  332 */       this._hasNativeObjectIds = other.canWriteObjectId();
/*      */     }
/*  334 */     this._mayHaveNativeIds = (this._hasNativeTypeIds || this._hasNativeObjectIds);
/*      */     
/*  336 */     JsonParser p = other.asParser();
/*  337 */     while (p.nextToken() != null) {
/*  338 */       copyCurrentStructure(p);
/*      */     }
/*  340 */     return this;
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
/*      */   public void serialize(JsonGenerator gen) throws IOException {
/*  355 */     Segment segment = this._first;
/*  356 */     int ptr = -1;
/*      */     
/*  358 */     boolean checkIds = this._mayHaveNativeIds;
/*  359 */     boolean hasIds = (checkIds && segment.hasIds());
/*      */     while (true) {
/*      */       Object ob, n, value;
/*  362 */       if (++ptr >= 16) {
/*  363 */         ptr = 0;
/*  364 */         segment = segment.next();
/*  365 */         if (segment == null)
/*  366 */           break;  hasIds = (checkIds && segment.hasIds());
/*      */       } 
/*  368 */       JsonToken t = segment.type(ptr);
/*  369 */       if (t == null)
/*      */         break; 
/*  371 */       if (hasIds) {
/*  372 */         Object id = segment.findObjectId(ptr);
/*  373 */         if (id != null) {
/*  374 */           gen.writeObjectId(id);
/*      */         }
/*  376 */         id = segment.findTypeId(ptr);
/*  377 */         if (id != null) {
/*  378 */           gen.writeTypeId(id);
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/*  383 */       switch (t) {
/*      */         case INT:
/*  385 */           gen.writeStartObject();
/*      */           continue;
/*      */         case BIG_INTEGER:
/*  388 */           gen.writeEndObject();
/*      */           continue;
/*      */         case BIG_DECIMAL:
/*  391 */           gen.writeStartArray();
/*      */           continue;
/*      */         case FLOAT:
/*  394 */           gen.writeEndArray();
/*      */           continue;
/*      */ 
/*      */         
/*      */         case LONG:
/*  399 */           ob = segment.get(ptr);
/*  400 */           if (ob instanceof SerializableString) {
/*  401 */             gen.writeFieldName((SerializableString)ob); continue;
/*      */           } 
/*  403 */           gen.writeFieldName((String)ob);
/*      */           continue;
/*      */ 
/*      */ 
/*      */         
/*      */         case null:
/*  409 */           ob = segment.get(ptr);
/*  410 */           if (ob instanceof SerializableString) {
/*  411 */             gen.writeString((SerializableString)ob); continue;
/*      */           } 
/*  413 */           gen.writeString((String)ob);
/*      */           continue;
/*      */ 
/*      */ 
/*      */         
/*      */         case null:
/*  419 */           n = segment.get(ptr);
/*  420 */           if (n instanceof Integer) {
/*  421 */             gen.writeNumber(((Integer)n).intValue()); continue;
/*  422 */           }  if (n instanceof BigInteger) {
/*  423 */             gen.writeNumber((BigInteger)n); continue;
/*  424 */           }  if (n instanceof Long) {
/*  425 */             gen.writeNumber(((Long)n).longValue()); continue;
/*  426 */           }  if (n instanceof Short) {
/*  427 */             gen.writeNumber(((Short)n).shortValue()); continue;
/*      */           } 
/*  429 */           gen.writeNumber(((Number)n).intValue());
/*      */           continue;
/*      */ 
/*      */ 
/*      */         
/*      */         case null:
/*  435 */           n = segment.get(ptr);
/*  436 */           if (n instanceof Double) {
/*  437 */             gen.writeNumber(((Double)n).doubleValue()); continue;
/*  438 */           }  if (n instanceof BigDecimal) {
/*  439 */             gen.writeNumber((BigDecimal)n); continue;
/*  440 */           }  if (n instanceof Float) {
/*  441 */             gen.writeNumber(((Float)n).floatValue()); continue;
/*  442 */           }  if (n == null) {
/*  443 */             gen.writeNull(); continue;
/*  444 */           }  if (n instanceof String) {
/*  445 */             gen.writeNumber((String)n); continue;
/*      */           } 
/*  447 */           _reportError(String.format("Unrecognized value type for VALUE_NUMBER_FLOAT: %s, cannot serialize", new Object[] { n
/*      */                   
/*  449 */                   .getClass().getName() }));
/*      */           continue;
/*      */ 
/*      */         
/*      */         case null:
/*  454 */           gen.writeBoolean(true);
/*      */           continue;
/*      */         case null:
/*  457 */           gen.writeBoolean(false);
/*      */           continue;
/*      */         case null:
/*  460 */           gen.writeNull();
/*      */           continue;
/*      */         
/*      */         case null:
/*  464 */           value = segment.get(ptr);
/*      */ 
/*      */ 
/*      */           
/*  468 */           if (value instanceof RawValue) {
/*  469 */             ((RawValue)value).serialize(gen); continue;
/*  470 */           }  if (value instanceof com.fasterxml.jackson.databind.JsonSerializable) {
/*  471 */             gen.writeObject(value); continue;
/*      */           } 
/*  473 */           gen.writeEmbeddedObject(value);
/*      */           continue;
/*      */       } 
/*      */ 
/*      */       
/*  478 */       throw new RuntimeException("Internal error: should never end up through this code path");
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
/*      */   public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  490 */     if (!p.hasToken(JsonToken.FIELD_NAME)) {
/*  491 */       copyCurrentStructure(p);
/*  492 */       return this;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  498 */     writeStartObject(); JsonToken t;
/*      */     do {
/*  500 */       copyCurrentStructure(p);
/*  501 */     } while ((t = p.nextToken()) == JsonToken.FIELD_NAME);
/*  502 */     if (t != JsonToken.END_OBJECT) {
/*  503 */       ctxt.reportWrongTokenException(TokenBuffer.class, JsonToken.END_OBJECT, "Expected END_OBJECT after copying contents of a JsonParser into TokenBuffer, got " + t, new Object[0]);
/*      */     }
/*      */ 
/*      */     
/*  507 */     writeEndObject();
/*  508 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  516 */     int MAX_COUNT = 100;
/*      */     
/*  518 */     StringBuilder sb = new StringBuilder();
/*  519 */     sb.append("[TokenBuffer: ");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  526 */     JsonParser jp = asParser();
/*  527 */     int count = 0;
/*  528 */     boolean hasNativeIds = (this._hasNativeTypeIds || this._hasNativeObjectIds);
/*      */ 
/*      */     
/*      */     while (true) {
/*      */       try {
/*  533 */         JsonToken t = jp.nextToken();
/*  534 */         if (t == null)
/*      */           break; 
/*  536 */         if (hasNativeIds) {
/*  537 */           _appendNativeIds(sb);
/*      */         }
/*      */         
/*  540 */         if (count < 100) {
/*  541 */           if (count > 0) {
/*  542 */             sb.append(", ");
/*      */           }
/*  544 */           sb.append(t.toString());
/*  545 */           if (t == JsonToken.FIELD_NAME) {
/*  546 */             sb.append('(');
/*  547 */             sb.append(jp.currentName());
/*  548 */             sb.append(')');
/*      */           } 
/*      */         } 
/*  551 */       } catch (IOException ioe) {
/*  552 */         throw new IllegalStateException(ioe);
/*      */       } 
/*  554 */       count++;
/*      */     } 
/*      */     
/*  557 */     if (count >= 100) {
/*  558 */       sb.append(" ... (truncated ").append(count - 100).append(" entries)");
/*      */     }
/*  560 */     sb.append(']');
/*  561 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private final void _appendNativeIds(StringBuilder sb) {
/*  566 */     Object objectId = this._last.findObjectId(this._appendAt - 1);
/*  567 */     if (objectId != null) {
/*  568 */       sb.append("[objectId=").append(String.valueOf(objectId)).append(']');
/*      */     }
/*  570 */     Object typeId = this._last.findTypeId(this._appendAt - 1);
/*  571 */     if (typeId != null) {
/*  572 */       sb.append("[typeId=").append(String.valueOf(typeId)).append(']');
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
/*      */   public JsonGenerator enable(JsonGenerator.Feature f) {
/*  584 */     this._generatorFeatures |= f.getMask();
/*  585 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonGenerator disable(JsonGenerator.Feature f) {
/*  590 */     this._generatorFeatures &= f.getMask() ^ 0xFFFFFFFF;
/*  591 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnabled(JsonGenerator.Feature f) {
/*  598 */     return ((this._generatorFeatures & f.getMask()) != 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getFeatureMask() {
/*  603 */     return this._generatorFeatures;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonGenerator setFeatureMask(int mask) {
/*  610 */     this._generatorFeatures = mask;
/*  611 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonGenerator overrideStdFeatures(int values, int mask) {
/*  616 */     int oldState = getFeatureMask();
/*  617 */     this._generatorFeatures = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/*  618 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonGenerator useDefaultPrettyPrinter() {
/*  624 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonGenerator setCodec(ObjectCodec oc) {
/*  629 */     this._objectCodec = oc;
/*  630 */     return this;
/*      */   }
/*      */   
/*      */   public ObjectCodec getCodec() {
/*  634 */     return this._objectCodec;
/*      */   }
/*      */   public final JsonWriteContext getOutputContext() {
/*  637 */     return this._writeContext;
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
/*      */   public boolean canWriteBinaryNatively() {
/*  650 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
/*  659 */     return DEFAULT_WRITE_CAPABILITIES;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void flush() throws IOException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/*  673 */     this._closed = true;
/*      */   }
/*      */   
/*      */   public boolean isClosed() {
/*  677 */     return this._closed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeStartArray() throws IOException {
/*  688 */     this._writeContext.writeValue();
/*  689 */     _appendStartMarker(JsonToken.START_ARRAY);
/*  690 */     this._writeContext = this._writeContext.createChildArrayContext();
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeStartArray(Object forValue) throws IOException {
/*  695 */     this._writeContext.writeValue();
/*  696 */     _appendStartMarker(JsonToken.START_ARRAY);
/*  697 */     this._writeContext = this._writeContext.createChildArrayContext(forValue);
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeStartArray(Object forValue, int size) throws IOException {
/*  702 */     this._writeContext.writeValue();
/*  703 */     _appendStartMarker(JsonToken.START_ARRAY);
/*  704 */     this._writeContext = this._writeContext.createChildArrayContext(forValue);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeEndArray() throws IOException {
/*  710 */     _appendEndMarker(JsonToken.END_ARRAY);
/*      */     
/*  712 */     JsonWriteContext c = this._writeContext.getParent();
/*  713 */     if (c != null) {
/*  714 */       this._writeContext = c;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeStartObject() throws IOException {
/*  721 */     this._writeContext.writeValue();
/*  722 */     _appendStartMarker(JsonToken.START_OBJECT);
/*  723 */     this._writeContext = this._writeContext.createChildObjectContext();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue) throws IOException {
/*  729 */     this._writeContext.writeValue();
/*  730 */     _appendStartMarker(JsonToken.START_OBJECT);
/*  731 */     JsonWriteContext ctxt = this._writeContext.createChildObjectContext(forValue);
/*  732 */     this._writeContext = ctxt;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeStartObject(Object forValue, int size) throws IOException {
/*  738 */     this._writeContext.writeValue();
/*  739 */     _appendStartMarker(JsonToken.START_OBJECT);
/*  740 */     JsonWriteContext ctxt = this._writeContext.createChildObjectContext(forValue);
/*  741 */     this._writeContext = ctxt;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeEndObject() throws IOException {
/*  747 */     _appendEndMarker(JsonToken.END_OBJECT);
/*      */     
/*  749 */     JsonWriteContext c = this._writeContext.getParent();
/*  750 */     if (c != null) {
/*  751 */       this._writeContext = c;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeFieldName(String name) throws IOException {
/*  758 */     this._writeContext.writeFieldName(name);
/*  759 */     _appendFieldName(name);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeFieldName(SerializableString name) throws IOException {
/*  765 */     this._writeContext.writeFieldName(name.getValue());
/*  766 */     _appendFieldName(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeString(String text) throws IOException {
/*  777 */     if (text == null) {
/*  778 */       writeNull();
/*      */     } else {
/*  780 */       _appendValue(JsonToken.VALUE_STRING, text);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeString(char[] text, int offset, int len) throws IOException {
/*  786 */     writeString(new String(text, offset, len));
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeString(SerializableString text) throws IOException {
/*  791 */     if (text == null) {
/*  792 */       writeNull();
/*      */     } else {
/*  794 */       _appendValue(JsonToken.VALUE_STRING, text);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/*  802 */     _reportUnsupportedOperation();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
/*  809 */     _reportUnsupportedOperation();
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeRaw(String text) throws IOException {
/*  814 */     _reportUnsupportedOperation();
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeRaw(String text, int offset, int len) throws IOException {
/*  819 */     _reportUnsupportedOperation();
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeRaw(SerializableString text) throws IOException {
/*  824 */     _reportUnsupportedOperation();
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeRaw(char[] text, int offset, int len) throws IOException {
/*  829 */     _reportUnsupportedOperation();
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeRaw(char c) throws IOException {
/*  834 */     _reportUnsupportedOperation();
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeRawValue(String text) throws IOException {
/*  839 */     _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeRawValue(String text, int offset, int len) throws IOException {
/*  844 */     if (offset > 0 || len != text.length()) {
/*  845 */       text = text.substring(offset, offset + len);
/*      */     }
/*  847 */     _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeRawValue(char[] text, int offset, int len) throws IOException {
/*  852 */     _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new String(text, offset, len));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(short i) throws IOException {
/*  863 */     _appendValue(JsonToken.VALUE_NUMBER_INT, Short.valueOf(i));
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNumber(int i) throws IOException {
/*  868 */     _appendValue(JsonToken.VALUE_NUMBER_INT, Integer.valueOf(i));
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNumber(long l) throws IOException {
/*  873 */     _appendValue(JsonToken.VALUE_NUMBER_INT, Long.valueOf(l));
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNumber(double d) throws IOException {
/*  878 */     _appendValue(JsonToken.VALUE_NUMBER_FLOAT, Double.valueOf(d));
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNumber(float f) throws IOException {
/*  883 */     _appendValue(JsonToken.VALUE_NUMBER_FLOAT, Float.valueOf(f));
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNumber(BigDecimal dec) throws IOException {
/*  888 */     if (dec == null) {
/*  889 */       writeNull();
/*      */     } else {
/*  891 */       _appendValue(JsonToken.VALUE_NUMBER_FLOAT, dec);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNumber(BigInteger v) throws IOException {
/*  897 */     if (v == null) {
/*  898 */       writeNull();
/*      */     } else {
/*  900 */       _appendValue(JsonToken.VALUE_NUMBER_INT, v);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeNumber(String encodedValue) throws IOException {
/*  909 */     _appendValue(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeBoolean(boolean state) throws IOException {
/*  914 */     _appendValue(state ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE);
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeNull() throws IOException {
/*  919 */     _appendValue(JsonToken.VALUE_NULL);
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
/*      */   public void writeObject(Object value) throws IOException {
/*  931 */     if (value == null) {
/*  932 */       writeNull();
/*      */       return;
/*      */     } 
/*  935 */     Class<?> raw = value.getClass();
/*  936 */     if (raw == byte[].class || value instanceof RawValue) {
/*  937 */       _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
/*      */       return;
/*      */     } 
/*  940 */     if (this._objectCodec == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  945 */       _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
/*      */     } else {
/*  947 */       this._objectCodec.writeValue(this, value);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTree(TreeNode node) throws IOException {
/*  954 */     if (node == null) {
/*  955 */       writeNull();
/*      */       
/*      */       return;
/*      */     } 
/*  959 */     if (this._objectCodec == null) {
/*      */       
/*  961 */       _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, node);
/*      */     } else {
/*  963 */       this._objectCodec.writeTree(this, node);
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
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
/*  982 */     byte[] copy = new byte[len];
/*  983 */     System.arraycopy(data, offset, copy, 0, len);
/*  984 */     writeObject(copy);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) {
/*  995 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canWriteTypeId() {
/* 1006 */     return this._hasNativeTypeIds;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean canWriteObjectId() {
/* 1011 */     return this._hasNativeObjectIds;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeTypeId(Object id) {
/* 1016 */     this._typeId = id;
/* 1017 */     this._hasNativeId = true;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeObjectId(Object id) {
/* 1022 */     this._objectId = id;
/* 1023 */     this._hasNativeId = true;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeEmbeddedObject(Object object) throws IOException {
/* 1028 */     _appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, object);
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
/*      */   public void copyCurrentEvent(JsonParser p) throws IOException {
/* 1040 */     if (this._mayHaveNativeIds) {
/* 1041 */       _checkNativeIds(p);
/*      */     }
/* 1043 */     switch (p.currentToken()) {
/*      */       case INT:
/* 1045 */         writeStartObject();
/*      */         return;
/*      */       case BIG_INTEGER:
/* 1048 */         writeEndObject();
/*      */         return;
/*      */       case BIG_DECIMAL:
/* 1051 */         writeStartArray();
/*      */         return;
/*      */       case FLOAT:
/* 1054 */         writeEndArray();
/*      */         return;
/*      */       case LONG:
/* 1057 */         writeFieldName(p.currentName());
/*      */         return;
/*      */       case null:
/* 1060 */         if (p.hasTextCharacters()) {
/* 1061 */           writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
/*      */         } else {
/* 1063 */           writeString(p.getText());
/*      */         } 
/*      */         return;
/*      */       case null:
/* 1067 */         switch (p.getNumberType()) {
/*      */           case INT:
/* 1069 */             writeNumber(p.getIntValue());
/*      */             return;
/*      */           case BIG_INTEGER:
/* 1072 */             writeNumber(p.getBigIntegerValue());
/*      */             return;
/*      */         } 
/* 1075 */         writeNumber(p.getLongValue());
/*      */         return;
/*      */       
/*      */       case null:
/* 1079 */         if (this._forceBigDecimal) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1084 */           writeNumber(p.getDecimalValue());
/*      */         } else {
/* 1086 */           switch (p.getNumberType()) {
/*      */             case BIG_DECIMAL:
/* 1088 */               writeNumber(p.getDecimalValue());
/*      */               return;
/*      */             case FLOAT:
/* 1091 */               writeNumber(p.getFloatValue());
/*      */               return;
/*      */           } 
/* 1094 */           writeNumber(p.getDoubleValue());
/*      */         } 
/*      */         return;
/*      */       
/*      */       case null:
/* 1099 */         writeBoolean(true);
/*      */         return;
/*      */       case null:
/* 1102 */         writeBoolean(false);
/*      */         return;
/*      */       case null:
/* 1105 */         writeNull();
/*      */         return;
/*      */       case null:
/* 1108 */         writeObject(p.getEmbeddedObject());
/*      */         return;
/*      */     } 
/* 1111 */     throw new RuntimeException("Internal error: unexpected token: " + p.currentToken());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyCurrentStructure(JsonParser p) throws IOException {
/* 1118 */     JsonToken t = p.currentToken();
/*      */ 
/*      */     
/* 1121 */     if (t == JsonToken.FIELD_NAME) {
/* 1122 */       if (this._mayHaveNativeIds) {
/* 1123 */         _checkNativeIds(p);
/*      */       }
/* 1125 */       writeFieldName(p.currentName());
/* 1126 */       t = p.nextToken();
/*      */     }
/* 1128 */     else if (t == null) {
/* 1129 */       throw new IllegalStateException("No token available from argument `JsonParser`");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1135 */     switch (t) {
/*      */       case BIG_DECIMAL:
/* 1137 */         if (this._mayHaveNativeIds) {
/* 1138 */           _checkNativeIds(p);
/*      */         }
/* 1140 */         writeStartArray();
/* 1141 */         _copyBufferContents(p);
/*      */         return;
/*      */       case INT:
/* 1144 */         if (this._mayHaveNativeIds) {
/* 1145 */           _checkNativeIds(p);
/*      */         }
/* 1147 */         writeStartObject();
/* 1148 */         _copyBufferContents(p);
/*      */         return;
/*      */       case FLOAT:
/* 1151 */         writeEndArray();
/*      */         return;
/*      */       case BIG_INTEGER:
/* 1154 */         writeEndObject();
/*      */         return;
/*      */     } 
/* 1157 */     _copyBufferValue(p, t);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _copyBufferContents(JsonParser p) throws IOException {
/* 1163 */     int depth = 1;
/*      */     
/*      */     JsonToken t;
/* 1166 */     while ((t = p.nextToken()) != null) {
/* 1167 */       switch (t) {
/*      */         case LONG:
/* 1169 */           if (this._mayHaveNativeIds) {
/* 1170 */             _checkNativeIds(p);
/*      */           }
/* 1172 */           writeFieldName(p.currentName());
/*      */           continue;
/*      */         
/*      */         case BIG_DECIMAL:
/* 1176 */           if (this._mayHaveNativeIds) {
/* 1177 */             _checkNativeIds(p);
/*      */           }
/* 1179 */           writeStartArray();
/* 1180 */           depth++;
/*      */           continue;
/*      */         
/*      */         case INT:
/* 1184 */           if (this._mayHaveNativeIds) {
/* 1185 */             _checkNativeIds(p);
/*      */           }
/* 1187 */           writeStartObject();
/* 1188 */           depth++;
/*      */           continue;
/*      */         
/*      */         case FLOAT:
/* 1192 */           writeEndArray();
/* 1193 */           if (--depth == 0) {
/*      */             return;
/*      */           }
/*      */           continue;
/*      */         case BIG_INTEGER:
/* 1198 */           writeEndObject();
/* 1199 */           if (--depth == 0) {
/*      */             return;
/*      */           }
/*      */           continue;
/*      */       } 
/*      */       
/* 1205 */       _copyBufferValue(p, t);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _copyBufferValue(JsonParser p, JsonToken t) throws IOException {
/* 1213 */     if (this._mayHaveNativeIds) {
/* 1214 */       _checkNativeIds(p);
/*      */     }
/* 1216 */     switch (t) {
/*      */       case null:
/* 1218 */         if (p.hasTextCharacters()) {
/* 1219 */           writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
/*      */         } else {
/* 1221 */           writeString(p.getText());
/*      */         } 
/*      */         return;
/*      */       case null:
/* 1225 */         switch (p.getNumberType()) {
/*      */           case INT:
/* 1227 */             writeNumber(p.getIntValue());
/*      */             return;
/*      */           case BIG_INTEGER:
/* 1230 */             writeNumber(p.getBigIntegerValue());
/*      */             return;
/*      */         } 
/* 1233 */         writeNumber(p.getLongValue());
/*      */         return;
/*      */       
/*      */       case null:
/* 1237 */         if (this._forceBigDecimal) {
/* 1238 */           writeNumber(p.getDecimalValue());
/*      */         }
/*      */         else {
/*      */           
/* 1242 */           Number n = p.getNumberValueExact();
/* 1243 */           _appendValue(JsonToken.VALUE_NUMBER_FLOAT, n);
/*      */         } 
/*      */         return;
/*      */       case null:
/* 1247 */         writeBoolean(true);
/*      */         return;
/*      */       case null:
/* 1250 */         writeBoolean(false);
/*      */         return;
/*      */       case null:
/* 1253 */         writeNull();
/*      */         return;
/*      */       case null:
/* 1256 */         writeObject(p.getEmbeddedObject());
/*      */         return;
/*      */     } 
/* 1259 */     throw new RuntimeException("Internal error: unexpected token: " + t);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final void _checkNativeIds(JsonParser p) throws IOException {
/* 1265 */     if ((this._typeId = p.getTypeId()) != null) {
/* 1266 */       this._hasNativeId = true;
/*      */     }
/* 1268 */     if ((this._objectId = p.getObjectId()) != null) {
/* 1269 */       this._hasNativeId = true;
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
/*      */   protected final void _appendValue(JsonToken type) {
/*      */     Segment next;
/* 1322 */     this._writeContext.writeValue();
/*      */     
/* 1324 */     if (this._hasNativeId) {
/* 1325 */       next = this._last.append(this._appendAt, type, this._objectId, this._typeId);
/*      */     } else {
/* 1327 */       next = this._last.append(this._appendAt, type);
/*      */     } 
/* 1329 */     if (next == null) {
/* 1330 */       this._appendAt++;
/*      */     } else {
/* 1332 */       this._last = next;
/* 1333 */       this._appendAt = 1;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _appendValue(JsonToken type, Object value) {
/*      */     Segment next;
/* 1345 */     this._writeContext.writeValue();
/*      */     
/* 1347 */     if (this._hasNativeId) {
/* 1348 */       next = this._last.append(this._appendAt, type, value, this._objectId, this._typeId);
/*      */     } else {
/* 1350 */       next = this._last.append(this._appendAt, type, value);
/*      */     } 
/* 1352 */     if (next == null) {
/* 1353 */       this._appendAt++;
/*      */     } else {
/* 1355 */       this._last = next;
/* 1356 */       this._appendAt = 1;
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
/*      */   protected final void _appendFieldName(Object value) {
/*      */     Segment next;
/* 1370 */     if (this._hasNativeId) {
/* 1371 */       next = this._last.append(this._appendAt, JsonToken.FIELD_NAME, value, this._objectId, this._typeId);
/*      */     } else {
/* 1373 */       next = this._last.append(this._appendAt, JsonToken.FIELD_NAME, value);
/*      */     } 
/* 1375 */     if (next == null) {
/* 1376 */       this._appendAt++;
/*      */     } else {
/* 1378 */       this._last = next;
/* 1379 */       this._appendAt = 1;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void _appendStartMarker(JsonToken type) {
/*      */     Segment next;
/* 1391 */     if (this._hasNativeId) {
/* 1392 */       next = this._last.append(this._appendAt, type, this._objectId, this._typeId);
/*      */     } else {
/* 1394 */       next = this._last.append(this._appendAt, type);
/*      */     } 
/* 1396 */     if (next == null) {
/* 1397 */       this._appendAt++;
/*      */     } else {
/* 1399 */       this._last = next;
/* 1400 */       this._appendAt = 1;
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
/*      */   protected final void _appendEndMarker(JsonToken type) {
/* 1412 */     Segment next = this._last.append(this._appendAt, type);
/* 1413 */     if (next == null) {
/* 1414 */       this._appendAt++;
/*      */     } else {
/* 1416 */       this._last = next;
/* 1417 */       this._appendAt = 1;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _reportUnsupportedOperation() {
/* 1423 */     throw new UnsupportedOperationException("Called operation not supported for TokenBuffer");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final class Parser
/*      */     extends ParserMinimalBase
/*      */   {
/*      */     protected ObjectCodec _codec;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final boolean _hasNativeTypeIds;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final boolean _hasNativeObjectIds;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final boolean _hasNativeIds;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected TokenBuffer.Segment _segment;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected int _segmentPtr;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected TokenBufferReadContext _parsingContext;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean _closed;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected transient ByteArrayBuilder _byteBuilder;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1481 */     protected JsonLocation _location = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public Parser(TokenBuffer.Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds) {
/* 1493 */       this(firstSeg, codec, hasNativeTypeIds, hasNativeObjectIds, (JsonStreamContext)null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Parser(TokenBuffer.Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds, JsonStreamContext parentContext) {
/* 1500 */       super(0);
/* 1501 */       this._segment = firstSeg;
/* 1502 */       this._segmentPtr = -1;
/* 1503 */       this._codec = codec;
/* 1504 */       this._parsingContext = TokenBufferReadContext.createRootContext(parentContext);
/* 1505 */       this._hasNativeTypeIds = hasNativeTypeIds;
/* 1506 */       this._hasNativeObjectIds = hasNativeObjectIds;
/* 1507 */       this._hasNativeIds = (hasNativeTypeIds || hasNativeObjectIds);
/*      */     }
/*      */     
/*      */     public void setLocation(JsonLocation l) {
/* 1511 */       this._location = l;
/*      */     }
/*      */     
/*      */     public ObjectCodec getCodec() {
/* 1515 */       return this._codec;
/*      */     }
/*      */     public void setCodec(ObjectCodec c) {
/* 1518 */       this._codec = c;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Version version() {
/* 1528 */       return PackageVersion.VERSION;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
/* 1537 */       return DEFAULT_READ_CAPABILITIES;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public JsonToken peekNextToken() throws IOException {
/* 1549 */       if (this._closed) return null; 
/* 1550 */       TokenBuffer.Segment seg = this._segment;
/* 1551 */       int ptr = this._segmentPtr + 1;
/* 1552 */       if (ptr >= 16) {
/* 1553 */         ptr = 0;
/* 1554 */         seg = (seg == null) ? null : seg.next();
/*      */       } 
/* 1556 */       return (seg == null) ? null : seg.type(ptr);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/* 1567 */       if (!this._closed) {
/* 1568 */         this._closed = true;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public JsonToken nextToken() throws IOException {
/* 1582 */       if (this._closed || this._segment == null) return null;
/*      */ 
/*      */       
/* 1585 */       if (++this._segmentPtr >= 16) {
/* 1586 */         this._segmentPtr = 0;
/* 1587 */         this._segment = this._segment.next();
/* 1588 */         if (this._segment == null) {
/* 1589 */           return null;
/*      */         }
/*      */       } 
/* 1592 */       this._currToken = this._segment.type(this._segmentPtr);
/*      */       
/* 1594 */       if (this._currToken == JsonToken.FIELD_NAME) {
/* 1595 */         Object ob = _currentObject();
/* 1596 */         String name = (ob instanceof String) ? (String)ob : ob.toString();
/* 1597 */         this._parsingContext.setCurrentName(name);
/* 1598 */       } else if (this._currToken == JsonToken.START_OBJECT) {
/* 1599 */         this._parsingContext = this._parsingContext.createChildObjectContext();
/* 1600 */       } else if (this._currToken == JsonToken.START_ARRAY) {
/* 1601 */         this._parsingContext = this._parsingContext.createChildArrayContext();
/* 1602 */       } else if (this._currToken == JsonToken.END_OBJECT || this._currToken == JsonToken.END_ARRAY) {
/*      */ 
/*      */         
/* 1605 */         this._parsingContext = this._parsingContext.parentOrCopy();
/*      */       } else {
/* 1607 */         this._parsingContext.updateForValue();
/*      */       } 
/* 1609 */       return this._currToken;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String nextFieldName() throws IOException {
/* 1616 */       if (this._closed || this._segment == null) {
/* 1617 */         return null;
/*      */       }
/*      */       
/* 1620 */       int ptr = this._segmentPtr + 1;
/* 1621 */       if (ptr < 16 && this._segment.type(ptr) == JsonToken.FIELD_NAME) {
/* 1622 */         this._segmentPtr = ptr;
/* 1623 */         this._currToken = JsonToken.FIELD_NAME;
/* 1624 */         Object ob = this._segment.get(ptr);
/* 1625 */         String name = (ob instanceof String) ? (String)ob : ob.toString();
/* 1626 */         this._parsingContext.setCurrentName(name);
/* 1627 */         return name;
/*      */       } 
/* 1629 */       return (nextToken() == JsonToken.FIELD_NAME) ? currentName() : null;
/*      */     }
/*      */     
/*      */     public boolean isClosed() {
/* 1633 */       return this._closed;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public JsonStreamContext getParsingContext() {
/* 1642 */       return this._parsingContext;
/*      */     }
/*      */     public JsonLocation getTokenLocation() {
/* 1645 */       return getCurrentLocation();
/*      */     }
/*      */     
/*      */     public JsonLocation getCurrentLocation() {
/* 1649 */       return (this._location == null) ? JsonLocation.NA : this._location;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String currentName() {
/* 1655 */       if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/* 1656 */         JsonStreamContext parent = this._parsingContext.getParent();
/* 1657 */         return parent.getCurrentName();
/*      */       } 
/* 1659 */       return this._parsingContext.getCurrentName();
/*      */     }
/*      */     
/*      */     public String getCurrentName() {
/* 1663 */       return currentName();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void overrideCurrentName(String name) {
/* 1669 */       JsonStreamContext ctxt = this._parsingContext;
/* 1670 */       if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/* 1671 */         ctxt = ctxt.getParent();
/*      */       }
/* 1673 */       if (ctxt instanceof TokenBufferReadContext) {
/*      */         try {
/* 1675 */           ((TokenBufferReadContext)ctxt).setCurrentName(name);
/* 1676 */         } catch (IOException e) {
/* 1677 */           throw new RuntimeException(e);
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getText() {
/* 1692 */       if (this._currToken == JsonToken.VALUE_STRING || this._currToken == JsonToken.FIELD_NAME) {
/*      */         
/* 1694 */         Object ob = _currentObject();
/* 1695 */         if (ob instanceof String) {
/* 1696 */           return (String)ob;
/*      */         }
/* 1698 */         return ClassUtil.nullOrToString(ob);
/*      */       } 
/* 1700 */       if (this._currToken == null) {
/* 1701 */         return null;
/*      */       }
/* 1703 */       switch (this._currToken) {
/*      */         case null:
/*      */         case null:
/* 1706 */           return ClassUtil.nullOrToString(_currentObject());
/*      */       } 
/* 1708 */       return this._currToken.asString();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public char[] getTextCharacters() {
/* 1714 */       String str = getText();
/* 1715 */       return (str == null) ? null : str.toCharArray();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTextLength() {
/* 1720 */       String str = getText();
/* 1721 */       return (str == null) ? 0 : str.length();
/*      */     }
/*      */     
/*      */     public int getTextOffset() {
/* 1725 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasTextCharacters() {
/* 1730 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isNaN() {
/* 1742 */       if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
/* 1743 */         Object value = _currentObject();
/* 1744 */         if (value instanceof Double) {
/* 1745 */           Double v = (Double)value;
/* 1746 */           return (v.isNaN() || v.isInfinite());
/*      */         } 
/* 1748 */         if (value instanceof Float) {
/* 1749 */           Float v = (Float)value;
/* 1750 */           return (v.isNaN() || v.isInfinite());
/*      */         } 
/*      */       } 
/* 1753 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BigInteger getBigIntegerValue() throws IOException {
/* 1759 */       Number n = getNumberValue();
/* 1760 */       if (n instanceof BigInteger) {
/* 1761 */         return (BigInteger)n;
/*      */       }
/* 1763 */       if (getNumberType() == JsonParser.NumberType.BIG_DECIMAL) {
/* 1764 */         return ((BigDecimal)n).toBigInteger();
/*      */       }
/*      */       
/* 1767 */       return BigInteger.valueOf(n.longValue());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BigDecimal getDecimalValue() throws IOException {
/* 1773 */       Number n = getNumberValue();
/* 1774 */       if (n instanceof BigDecimal) {
/* 1775 */         return (BigDecimal)n;
/*      */       }
/* 1777 */       switch (getNumberType()) {
/*      */         case INT:
/*      */         case LONG:
/* 1780 */           return BigDecimal.valueOf(n.longValue());
/*      */         case BIG_INTEGER:
/* 1782 */           return new BigDecimal((BigInteger)n);
/*      */       } 
/*      */ 
/*      */       
/* 1786 */       return BigDecimal.valueOf(n.doubleValue());
/*      */     }
/*      */ 
/*      */     
/*      */     public double getDoubleValue() throws IOException {
/* 1791 */       return getNumberValue().doubleValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public float getFloatValue() throws IOException {
/* 1796 */       return getNumberValue().floatValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getIntValue() throws IOException {
/* 1803 */       Number n = (this._currToken == JsonToken.VALUE_NUMBER_INT) ? (Number)_currentObject() : getNumberValue();
/* 1804 */       if (n instanceof Integer || _smallerThanInt(n)) {
/* 1805 */         return n.intValue();
/*      */       }
/* 1807 */       return _convertNumberToInt(n);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public long getLongValue() throws IOException {
/* 1813 */       Number n = (this._currToken == JsonToken.VALUE_NUMBER_INT) ? (Number)_currentObject() : getNumberValue();
/* 1814 */       if (n instanceof Long || _smallerThanLong(n)) {
/* 1815 */         return n.longValue();
/*      */       }
/* 1817 */       return _convertNumberToLong(n);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public JsonParser.NumberType getNumberType() throws IOException {
/* 1823 */       Number n = getNumberValue();
/* 1824 */       if (n instanceof Integer) return JsonParser.NumberType.INT; 
/* 1825 */       if (n instanceof Long) return JsonParser.NumberType.LONG; 
/* 1826 */       if (n instanceof Double) return JsonParser.NumberType.DOUBLE; 
/* 1827 */       if (n instanceof BigDecimal) return JsonParser.NumberType.BIG_DECIMAL; 
/* 1828 */       if (n instanceof BigInteger) return JsonParser.NumberType.BIG_INTEGER; 
/* 1829 */       if (n instanceof Float) return JsonParser.NumberType.FLOAT; 
/* 1830 */       if (n instanceof Short) return JsonParser.NumberType.INT; 
/* 1831 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     public final Number getNumberValue() throws IOException {
/* 1836 */       _checkIsNumber();
/* 1837 */       Object value = _currentObject();
/* 1838 */       if (value instanceof Number) {
/* 1839 */         return (Number)value;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1844 */       if (value instanceof String) {
/* 1845 */         String str = (String)value;
/* 1846 */         if (str.indexOf('.') >= 0) {
/* 1847 */           return Double.valueOf(Double.parseDouble(str));
/*      */         }
/* 1849 */         return Long.valueOf(Long.parseLong(str));
/*      */       } 
/* 1851 */       if (value == null) {
/* 1852 */         return null;
/*      */       }
/* 1854 */       throw new IllegalStateException("Internal error: entry should be a Number, but is of type " + value
/* 1855 */           .getClass().getName());
/*      */     }
/*      */     
/*      */     private final boolean _smallerThanInt(Number n) {
/* 1859 */       return (n instanceof Short || n instanceof Byte);
/*      */     }
/*      */     
/*      */     private final boolean _smallerThanLong(Number n) {
/* 1863 */       return (n instanceof Integer || n instanceof Short || n instanceof Byte);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected int _convertNumberToInt(Number n) throws IOException {
/* 1870 */       if (n instanceof Long) {
/* 1871 */         long l = n.longValue();
/* 1872 */         int result = (int)l;
/* 1873 */         if (result != l) {
/* 1874 */           reportOverflowInt();
/*      */         }
/* 1876 */         return result;
/*      */       } 
/* 1878 */       if (n instanceof BigInteger) {
/* 1879 */         BigInteger big = (BigInteger)n;
/* 1880 */         if (BI_MIN_INT.compareTo(big) > 0 || BI_MAX_INT
/* 1881 */           .compareTo(big) < 0)
/* 1882 */           reportOverflowInt(); 
/*      */       } else {
/* 1884 */         if (n instanceof Double || n instanceof Float) {
/* 1885 */           double d = n.doubleValue();
/*      */           
/* 1887 */           if (d < -2.147483648E9D || d > 2.147483647E9D) {
/* 1888 */             reportOverflowInt();
/*      */           }
/* 1890 */           return (int)d;
/* 1891 */         }  if (n instanceof BigDecimal) {
/* 1892 */           BigDecimal big = (BigDecimal)n;
/* 1893 */           if (BD_MIN_INT.compareTo(big) > 0 || BD_MAX_INT
/* 1894 */             .compareTo(big) < 0) {
/* 1895 */             reportOverflowInt();
/*      */           }
/*      */         } else {
/* 1898 */           _throwInternal();
/*      */         } 
/* 1900 */       }  return n.intValue();
/*      */     }
/*      */ 
/*      */     
/*      */     protected long _convertNumberToLong(Number n) throws IOException {
/* 1905 */       if (n instanceof BigInteger) {
/* 1906 */         BigInteger big = (BigInteger)n;
/* 1907 */         if (BI_MIN_LONG.compareTo(big) > 0 || BI_MAX_LONG
/* 1908 */           .compareTo(big) < 0)
/* 1909 */           reportOverflowLong(); 
/*      */       } else {
/* 1911 */         if (n instanceof Double || n instanceof Float) {
/* 1912 */           double d = n.doubleValue();
/*      */           
/* 1914 */           if (d < -9.223372036854776E18D || d > 9.223372036854776E18D) {
/* 1915 */             reportOverflowLong();
/*      */           }
/* 1917 */           return (long)d;
/* 1918 */         }  if (n instanceof BigDecimal) {
/* 1919 */           BigDecimal big = (BigDecimal)n;
/* 1920 */           if (BD_MIN_LONG.compareTo(big) > 0 || BD_MAX_LONG
/* 1921 */             .compareTo(big) < 0) {
/* 1922 */             reportOverflowLong();
/*      */           }
/*      */         } else {
/* 1925 */           _throwInternal();
/*      */         } 
/* 1927 */       }  return n.longValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getEmbeddedObject() {
/* 1939 */       if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 1940 */         return _currentObject();
/*      */       }
/* 1942 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/* 1950 */       if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
/*      */         
/* 1952 */         Object ob = _currentObject();
/* 1953 */         if (ob instanceof byte[]) {
/* 1954 */           return (byte[])ob;
/*      */         }
/*      */       } 
/*      */       
/* 1958 */       if (this._currToken != JsonToken.VALUE_STRING) {
/* 1959 */         throw _constructError("Current token (" + this._currToken + ") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), cannot access as binary");
/*      */       }
/* 1961 */       String str = getText();
/* 1962 */       if (str == null) {
/* 1963 */         return null;
/*      */       }
/* 1965 */       ByteArrayBuilder builder = this._byteBuilder;
/* 1966 */       if (builder == null) {
/* 1967 */         this._byteBuilder = builder = new ByteArrayBuilder(100);
/*      */       } else {
/* 1969 */         this._byteBuilder.reset();
/*      */       } 
/* 1971 */       _decodeBase64(str, builder, b64variant);
/* 1972 */       return builder.toByteArray();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/* 1978 */       byte[] data = getBinaryValue(b64variant);
/* 1979 */       if (data != null) {
/* 1980 */         out.write(data, 0, data.length);
/* 1981 */         return data.length;
/*      */       } 
/* 1983 */       return 0;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean canReadObjectId() {
/* 1994 */       return this._hasNativeObjectIds;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean canReadTypeId() {
/* 1999 */       return this._hasNativeTypeIds;
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getTypeId() {
/* 2004 */       return this._segment.findTypeId(this._segmentPtr);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getObjectId() {
/* 2009 */       return this._segment.findObjectId(this._segmentPtr);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final Object _currentObject() {
/* 2019 */       return this._segment.get(this._segmentPtr);
/*      */     }
/*      */ 
/*      */     
/*      */     protected final void _checkIsNumber() throws JacksonException {
/* 2024 */       if (this._currToken == null || !this._currToken.isNumeric()) {
/* 2025 */         throw _constructError("Current token (" + this._currToken + ") not numeric, cannot use numeric value accessors");
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     protected void _handleEOF() {
/* 2031 */       _throwInternal();
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
/*      */   protected static final class Segment
/*      */   {
/*      */     public static final int TOKENS_PER_SEGMENT = 16;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2053 */     private static final JsonToken[] TOKEN_TYPES_BY_INDEX = new JsonToken[16]; protected Segment _next; static {
/* 2054 */       JsonToken[] t = JsonToken.values();
/*      */       
/* 2056 */       System.arraycopy(t, 1, TOKEN_TYPES_BY_INDEX, 1, Math.min(15, t.length - 1));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected long _tokenTypes;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2074 */     protected final Object[] _tokens = new Object[16];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected TreeMap<Integer, Object> _nativeIds;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public JsonToken type(int index) {
/* 2087 */       long l = this._tokenTypes;
/* 2088 */       if (index > 0) {
/* 2089 */         l >>= index << 2;
/*      */       }
/* 2091 */       int ix = (int)l & 0xF;
/* 2092 */       return TOKEN_TYPES_BY_INDEX[ix];
/*      */     }
/*      */ 
/*      */     
/*      */     public int rawType(int index) {
/* 2097 */       long l = this._tokenTypes;
/* 2098 */       if (index > 0) {
/* 2099 */         l >>= index << 2;
/*      */       }
/* 2101 */       int ix = (int)l & 0xF;
/* 2102 */       return ix;
/*      */     }
/*      */     
/*      */     public Object get(int index) {
/* 2106 */       return this._tokens[index];
/*      */     }
/*      */     public Segment next() {
/* 2109 */       return this._next;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean hasIds() {
/* 2116 */       return (this._nativeIds != null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Segment append(int index, JsonToken tokenType) {
/* 2123 */       if (index < 16) {
/* 2124 */         set(index, tokenType);
/* 2125 */         return null;
/*      */       } 
/* 2127 */       this._next = new Segment();
/* 2128 */       this._next.set(0, tokenType);
/* 2129 */       return this._next;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Segment append(int index, JsonToken tokenType, Object objectId, Object typeId) {
/* 2135 */       if (index < 16) {
/* 2136 */         set(index, tokenType, objectId, typeId);
/* 2137 */         return null;
/*      */       } 
/* 2139 */       this._next = new Segment();
/* 2140 */       this._next.set(0, tokenType, objectId, typeId);
/* 2141 */       return this._next;
/*      */     }
/*      */ 
/*      */     
/*      */     public Segment append(int index, JsonToken tokenType, Object value) {
/* 2146 */       if (index < 16) {
/* 2147 */         set(index, tokenType, value);
/* 2148 */         return null;
/*      */       } 
/* 2150 */       this._next = new Segment();
/* 2151 */       this._next.set(0, tokenType, value);
/* 2152 */       return this._next;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Segment append(int index, JsonToken tokenType, Object value, Object objectId, Object typeId) {
/* 2158 */       if (index < 16) {
/* 2159 */         set(index, tokenType, value, objectId, typeId);
/* 2160 */         return null;
/*      */       } 
/* 2162 */       this._next = new Segment();
/* 2163 */       this._next.set(0, tokenType, value, objectId, typeId);
/* 2164 */       return this._next;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void set(int index, JsonToken tokenType) {
/* 2218 */       long typeCode = tokenType.ordinal();
/* 2219 */       if (index > 0) {
/* 2220 */         typeCode <<= index << 2;
/*      */       }
/* 2222 */       this._tokenTypes |= typeCode;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void set(int index, JsonToken tokenType, Object objectId, Object typeId) {
/* 2228 */       long typeCode = tokenType.ordinal();
/* 2229 */       if (index > 0) {
/* 2230 */         typeCode <<= index << 2;
/*      */       }
/* 2232 */       this._tokenTypes |= typeCode;
/* 2233 */       assignNativeIds(index, objectId, typeId);
/*      */     }
/*      */ 
/*      */     
/*      */     private void set(int index, JsonToken tokenType, Object value) {
/* 2238 */       this._tokens[index] = value;
/* 2239 */       long typeCode = tokenType.ordinal();
/* 2240 */       if (index > 0) {
/* 2241 */         typeCode <<= index << 2;
/*      */       }
/* 2243 */       this._tokenTypes |= typeCode;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void set(int index, JsonToken tokenType, Object value, Object objectId, Object typeId) {
/* 2249 */       this._tokens[index] = value;
/* 2250 */       long typeCode = tokenType.ordinal();
/* 2251 */       if (index > 0) {
/* 2252 */         typeCode <<= index << 2;
/*      */       }
/* 2254 */       this._tokenTypes |= typeCode;
/* 2255 */       assignNativeIds(index, objectId, typeId);
/*      */     }
/*      */ 
/*      */     
/*      */     private final void assignNativeIds(int index, Object objectId, Object typeId) {
/* 2260 */       if (this._nativeIds == null) {
/* 2261 */         this._nativeIds = new TreeMap<>();
/*      */       }
/* 2263 */       if (objectId != null) {
/* 2264 */         this._nativeIds.put(Integer.valueOf(_objectIdIndex(index)), objectId);
/*      */       }
/* 2266 */       if (typeId != null) {
/* 2267 */         this._nativeIds.put(Integer.valueOf(_typeIdIndex(index)), typeId);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Object findObjectId(int index) {
/* 2275 */       return (this._nativeIds == null) ? null : this._nativeIds.get(Integer.valueOf(_objectIdIndex(index)));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Object findTypeId(int index) {
/* 2282 */       return (this._nativeIds == null) ? null : this._nativeIds.get(Integer.valueOf(_typeIdIndex(index)));
/*      */     }
/*      */     
/* 2285 */     private final int _typeIdIndex(int i) { return i + i; } private final int _objectIdIndex(int i) {
/* 2286 */       return i + i + 1;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/util/TokenBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */