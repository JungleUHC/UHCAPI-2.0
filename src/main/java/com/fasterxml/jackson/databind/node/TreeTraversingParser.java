/*     */ package com.fasterxml.jackson.databind.node;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.JacksonException;
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.ObjectCodec;
/*     */ import com.fasterxml.jackson.core.StreamReadCapability;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.core.base.ParserMinimalBase;
/*     */ import com.fasterxml.jackson.core.util.JacksonFeatureSet;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.cfg.PackageVersion;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
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
/*     */ public class TreeTraversingParser
/*     */   extends ParserMinimalBase
/*     */ {
/*     */   protected ObjectCodec _objectCodec;
/*     */   protected NodeCursor _nodeCursor;
/*     */   protected boolean _closed;
/*     */   
/*     */   public TreeTraversingParser(JsonNode n) {
/*  53 */     this(n, (ObjectCodec)null);
/*     */   }
/*     */   
/*     */   public TreeTraversingParser(JsonNode n, ObjectCodec codec) {
/*  57 */     super(0);
/*  58 */     this._objectCodec = codec;
/*  59 */     this._nodeCursor = new NodeCursor.RootCursor(n, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCodec(ObjectCodec c) {
/*  64 */     this._objectCodec = c;
/*     */   }
/*     */ 
/*     */   
/*     */   public ObjectCodec getCodec() {
/*  69 */     return this._objectCodec;
/*     */   }
/*     */ 
/*     */   
/*     */   public Version version() {
/*  74 */     return PackageVersion.VERSION;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
/*  80 */     return DEFAULT_READ_CAPABILITIES;
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
/*     */   public void close() throws IOException {
/*  92 */     if (!this._closed) {
/*  93 */       this._closed = true;
/*  94 */       this._nodeCursor = null;
/*  95 */       this._currToken = null;
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
/*     */   public JsonToken nextToken() throws IOException {
/* 108 */     this._currToken = this._nodeCursor.nextToken();
/* 109 */     if (this._currToken == null) {
/* 110 */       this._closed = true;
/* 111 */       return null;
/*     */     } 
/* 113 */     switch (this._currToken) {
/*     */       case START_OBJECT:
/* 115 */         this._nodeCursor = this._nodeCursor.startObject();
/*     */         break;
/*     */       case START_ARRAY:
/* 118 */         this._nodeCursor = this._nodeCursor.startArray();
/*     */         break;
/*     */       case END_OBJECT:
/*     */       case END_ARRAY:
/* 122 */         this._nodeCursor = this._nodeCursor.getParent();
/*     */         break;
/*     */     } 
/* 125 */     return this._currToken;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser skipChildren() throws IOException {
/* 134 */     if (this._currToken == JsonToken.START_OBJECT) {
/* 135 */       this._nodeCursor = this._nodeCursor.getParent();
/* 136 */       this._currToken = JsonToken.END_OBJECT;
/* 137 */     } else if (this._currToken == JsonToken.START_ARRAY) {
/* 138 */       this._nodeCursor = this._nodeCursor.getParent();
/* 139 */       this._currToken = JsonToken.END_ARRAY;
/*     */     } 
/* 141 */     return (JsonParser)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 146 */     return this._closed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCurrentName() {
/* 156 */     NodeCursor crsr = this._nodeCursor;
/* 157 */     if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/* 158 */       crsr = crsr.getParent();
/*     */     }
/* 160 */     return (crsr == null) ? null : crsr.getCurrentName();
/*     */   }
/*     */   
/*     */   public void overrideCurrentName(String name) {
/* 164 */     NodeCursor crsr = this._nodeCursor;
/* 165 */     if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/* 166 */       crsr = crsr.getParent();
/*     */     }
/* 168 */     if (crsr != null) {
/* 169 */       crsr.overrideCurrentName(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonStreamContext getParsingContext() {
/* 175 */     return this._nodeCursor;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonLocation getTokenLocation() {
/* 180 */     return JsonLocation.NA;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonLocation getCurrentLocation() {
/* 185 */     return JsonLocation.NA;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/*     */     JsonNode n;
/* 197 */     if (this._currToken == null) {
/* 198 */       return null;
/*     */     }
/*     */     
/* 201 */     switch (this._currToken) {
/*     */       case FIELD_NAME:
/* 203 */         return this._nodeCursor.getCurrentName();
/*     */       case VALUE_STRING:
/* 205 */         return currentNode().textValue();
/*     */       case VALUE_NUMBER_INT:
/*     */       case VALUE_NUMBER_FLOAT:
/* 208 */         return String.valueOf(currentNode().numberValue());
/*     */       case VALUE_EMBEDDED_OBJECT:
/* 210 */         n = currentNode();
/* 211 */         if (n != null && n.isBinary())
/*     */         {
/* 213 */           return n.asText(); } 
/*     */         break;
/*     */     } 
/* 216 */     return this._currToken.asString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] getTextCharacters() throws IOException {
/* 222 */     return getText().toCharArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTextLength() throws IOException {
/* 227 */     return getText().length();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTextOffset() throws IOException {
/* 232 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasTextCharacters() {
/* 238 */     return false;
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
/*     */   public JsonParser.NumberType getNumberType() throws IOException {
/* 251 */     JsonNode n = currentNumericNode();
/* 252 */     return (n == null) ? null : n.numberType();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BigInteger getBigIntegerValue() throws IOException {
/* 258 */     return currentNumericNode().bigIntegerValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getDecimalValue() throws IOException {
/* 263 */     return currentNumericNode().decimalValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDoubleValue() throws IOException {
/* 268 */     return currentNumericNode().doubleValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloatValue() throws IOException {
/* 273 */     return (float)currentNumericNode().doubleValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntValue() throws IOException {
/* 278 */     NumericNode node = (NumericNode)currentNumericNode();
/* 279 */     if (!node.canConvertToInt()) {
/* 280 */       reportOverflowInt();
/*     */     }
/* 282 */     return node.intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLongValue() throws IOException {
/* 287 */     NumericNode node = (NumericNode)currentNumericNode();
/* 288 */     if (!node.canConvertToLong()) {
/* 289 */       reportOverflowLong();
/*     */     }
/* 291 */     return node.longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public Number getNumberValue() throws IOException {
/* 296 */     return currentNumericNode().numberValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getEmbeddedObject() {
/* 302 */     if (!this._closed) {
/* 303 */       JsonNode n = currentNode();
/* 304 */       if (n != null) {
/* 305 */         if (n.isPojo()) {
/* 306 */           return ((POJONode)n).getPojo();
/*     */         }
/* 308 */         if (n.isBinary()) {
/* 309 */           return ((BinaryNode)n).binaryValue();
/*     */         }
/*     */       } 
/*     */     } 
/* 313 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNaN() {
/* 318 */     if (!this._closed) {
/* 319 */       JsonNode n = currentNode();
/* 320 */       if (n instanceof NumericNode) {
/* 321 */         return ((NumericNode)n).isNaN();
/*     */       }
/*     */     } 
/* 324 */     return false;
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
/*     */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/* 338 */     JsonNode n = currentNode();
/* 339 */     if (n != null) {
/*     */ 
/*     */       
/* 342 */       if (n instanceof TextNode) {
/* 343 */         return ((TextNode)n).getBinaryValue(b64variant);
/*     */       }
/* 345 */       return n.binaryValue();
/*     */     } 
/*     */     
/* 348 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/* 356 */     byte[] data = getBinaryValue(b64variant);
/* 357 */     if (data != null) {
/* 358 */       out.write(data, 0, data.length);
/* 359 */       return data.length;
/*     */     } 
/* 361 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonNode currentNode() {
/* 371 */     if (this._closed || this._nodeCursor == null) {
/* 372 */       return null;
/*     */     }
/* 374 */     return this._nodeCursor.currentNode();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonNode currentNumericNode() throws JacksonException {
/* 380 */     JsonNode n = currentNode();
/* 381 */     if (n == null || !n.isNumber()) {
/* 382 */       JsonToken t = (n == null) ? null : n.asToken();
/* 383 */       throw _constructError("Current token (" + t + ") not numeric, cannot use numeric value accessors");
/*     */     } 
/* 385 */     return n;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void _handleEOF() {
/* 390 */     _throwInternal();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/TreeTraversingParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */