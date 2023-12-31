/*     */ package com.fasterxml.jackson.databind.node;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BigIntegerNode
/*     */   extends NumericNode
/*     */ {
/*  17 */   private static final BigInteger MIN_INTEGER = BigInteger.valueOf(-2147483648L);
/*  18 */   private static final BigInteger MAX_INTEGER = BigInteger.valueOf(2147483647L);
/*  19 */   private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
/*  20 */   private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
/*     */ 
/*     */ 
/*     */   
/*     */   protected final BigInteger _value;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigIntegerNode(BigInteger v) {
/*  30 */     this._value = v;
/*     */   } public static BigIntegerNode valueOf(BigInteger v) {
/*  32 */     return new BigIntegerNode(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken asToken() {
/*  41 */     return JsonToken.VALUE_NUMBER_INT;
/*     */   }
/*     */   public JsonParser.NumberType numberType() {
/*  44 */     return JsonParser.NumberType.BIG_INTEGER;
/*     */   }
/*     */   public boolean isIntegralNumber() {
/*  47 */     return true;
/*     */   }
/*     */   public boolean isBigInteger() {
/*  50 */     return true;
/*     */   }
/*     */   public boolean canConvertToInt() {
/*  53 */     return (this._value.compareTo(MIN_INTEGER) >= 0 && this._value.compareTo(MAX_INTEGER) <= 0);
/*     */   }
/*     */   public boolean canConvertToLong() {
/*  56 */     return (this._value.compareTo(MIN_LONG) >= 0 && this._value.compareTo(MAX_LONG) <= 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public Number numberValue() {
/*  61 */     return this._value;
/*     */   }
/*     */   
/*     */   public short shortValue() {
/*  65 */     return this._value.shortValue();
/*     */   }
/*     */   public int intValue() {
/*  68 */     return this._value.intValue();
/*     */   }
/*     */   public long longValue() {
/*  71 */     return this._value.longValue();
/*     */   }
/*     */   public BigInteger bigIntegerValue() {
/*  74 */     return this._value;
/*     */   }
/*     */   public float floatValue() {
/*  77 */     return this._value.floatValue();
/*     */   }
/*     */   public double doubleValue() {
/*  80 */     return this._value.doubleValue();
/*     */   }
/*     */   public BigDecimal decimalValue() {
/*  83 */     return new BigDecimal(this._value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String asText() {
/*  93 */     return this._value.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean asBoolean(boolean defaultValue) {
/*  98 */     return !BigInteger.ZERO.equals(this._value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
/* 105 */     g.writeNumber(this._value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 111 */     if (o == this) return true; 
/* 112 */     if (o == null) return false; 
/* 113 */     if (!(o instanceof BigIntegerNode)) {
/* 114 */       return false;
/*     */     }
/* 116 */     return ((BigIntegerNode)o)._value.equals(this._value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 121 */     return this._value.hashCode();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/BigIntegerNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */