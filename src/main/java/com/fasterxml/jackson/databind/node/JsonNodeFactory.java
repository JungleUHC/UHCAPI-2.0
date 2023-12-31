/*     */ package com.fasterxml.jackson.databind.node;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.util.RawValue;
/*     */ import java.io.Serializable;
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
/*     */ public class JsonNodeFactory
/*     */   implements Serializable, JsonNodeCreator
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final boolean _cfgBigDecimalExact;
/*  25 */   private static final JsonNodeFactory decimalsNormalized = new JsonNodeFactory(false);
/*     */   
/*  27 */   private static final JsonNodeFactory decimalsAsIs = new JsonNodeFactory(true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  35 */   public static final JsonNodeFactory instance = decimalsNormalized;
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
/*     */   
/*     */   public JsonNodeFactory(boolean bigDecimalExact) {
/*  64 */     this._cfgBigDecimalExact = bigDecimalExact;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonNodeFactory() {
/*  75 */     this(false);
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
/*     */   public static JsonNodeFactory withExactBigDecimals(boolean bigDecimalExact) {
/*  87 */     return bigDecimalExact ? decimalsAsIs : decimalsNormalized;
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
/*     */   public BooleanNode booleanNode(boolean v) {
/* 102 */     return v ? BooleanNode.getTrue() : BooleanNode.getFalse();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NullNode nullNode() {
/* 110 */     return NullNode.getInstance();
/*     */   }
/*     */   public JsonNode missingNode() {
/* 113 */     return MissingNode.getInstance();
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
/*     */   public NumericNode numberNode(byte v) {
/* 127 */     return IntNode.valueOf(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueNode numberNode(Byte value) {
/* 137 */     return (value == null) ? nullNode() : IntNode.valueOf(value.intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NumericNode numberNode(short v) {
/* 145 */     return ShortNode.valueOf(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueNode numberNode(Short value) {
/* 155 */     return (value == null) ? nullNode() : ShortNode.valueOf(value.shortValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NumericNode numberNode(int v) {
/* 163 */     return IntNode.valueOf(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueNode numberNode(Integer value) {
/* 173 */     return (value == null) ? nullNode() : IntNode.valueOf(value.intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NumericNode numberNode(long v) {
/* 182 */     return LongNode.valueOf(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueNode numberNode(Long v) {
/* 192 */     if (v == null) {
/* 193 */       return nullNode();
/*     */     }
/* 195 */     return LongNode.valueOf(v.longValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueNode numberNode(BigInteger v) {
/* 204 */     if (v == null) {
/* 205 */       return nullNode();
/*     */     }
/* 207 */     return BigIntegerNode.valueOf(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NumericNode numberNode(float v) {
/* 215 */     return FloatNode.valueOf(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueNode numberNode(Float value) {
/* 225 */     return (value == null) ? nullNode() : FloatNode.valueOf(value.floatValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NumericNode numberNode(double v) {
/* 233 */     return DoubleNode.valueOf(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueNode numberNode(Double value) {
/* 243 */     return (value == null) ? nullNode() : DoubleNode.valueOf(value.doubleValue());
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
/*     */   public ValueNode numberNode(BigDecimal v) {
/* 259 */     if (v == null) {
/* 260 */       return nullNode();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 267 */     if (this._cfgBigDecimalExact) {
/* 268 */       return DecimalNode.valueOf(v);
/*     */     }
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
/* 281 */     if (v.signum() == 0) {
/* 282 */       return DecimalNode.ZERO;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 288 */       v = v.stripTrailingZeros();
/* 289 */     } catch (ArithmeticException arithmeticException) {}
/*     */ 
/*     */ 
/*     */     
/* 293 */     return DecimalNode.valueOf(v);
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
/*     */   public TextNode textNode(String text) {
/* 307 */     return TextNode.valueOf(text);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BinaryNode binaryNode(byte[] data) {
/* 315 */     return BinaryNode.valueOf(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BinaryNode binaryNode(byte[] data, int offset, int length) {
/* 324 */     return BinaryNode.valueOf(data, offset, length);
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
/*     */   public ArrayNode arrayNode() {
/* 337 */     return new ArrayNode(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayNode arrayNode(int capacity) {
/* 345 */     return new ArrayNode(this, capacity);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectNode objectNode() {
/* 351 */     return new ObjectNode(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueNode pojoNode(Object pojo) {
/* 360 */     return new POJONode(pojo);
/*     */   }
/*     */   
/*     */   public ValueNode rawValueNode(RawValue value) {
/* 364 */     return new POJONode(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _inIntRange(long l) {
/* 375 */     int i = (int)l;
/* 376 */     long l2 = i;
/* 377 */     return (l2 == l);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/JsonNodeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */