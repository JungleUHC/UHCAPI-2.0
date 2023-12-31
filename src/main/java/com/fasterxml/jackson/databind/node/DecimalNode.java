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
/*     */ 
/*     */ public class DecimalNode
/*     */   extends NumericNode
/*     */ {
/*  18 */   public static final DecimalNode ZERO = new DecimalNode(BigDecimal.ZERO);
/*     */   
/*  20 */   private static final BigDecimal MIN_INTEGER = BigDecimal.valueOf(-2147483648L);
/*  21 */   private static final BigDecimal MAX_INTEGER = BigDecimal.valueOf(2147483647L);
/*  22 */   private static final BigDecimal MIN_LONG = BigDecimal.valueOf(Long.MIN_VALUE);
/*  23 */   private static final BigDecimal MAX_LONG = BigDecimal.valueOf(Long.MAX_VALUE);
/*     */ 
/*     */ 
/*     */   
/*     */   protected final BigDecimal _value;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DecimalNode(BigDecimal v) {
/*  33 */     this._value = v;
/*     */   } public static DecimalNode valueOf(BigDecimal d) {
/*  35 */     return new DecimalNode(d);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken asToken() {
/*  43 */     return JsonToken.VALUE_NUMBER_FLOAT;
/*     */   }
/*     */   public JsonParser.NumberType numberType() {
/*  46 */     return JsonParser.NumberType.BIG_DECIMAL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFloatingPointNumber() {
/*  55 */     return true;
/*     */   }
/*     */   public boolean isBigDecimal() {
/*  58 */     return true;
/*     */   }
/*     */   public boolean canConvertToInt() {
/*  61 */     return (this._value.compareTo(MIN_INTEGER) >= 0 && this._value.compareTo(MAX_INTEGER) <= 0);
/*     */   }
/*     */   public boolean canConvertToLong() {
/*  64 */     return (this._value.compareTo(MIN_LONG) >= 0 && this._value.compareTo(MAX_LONG) <= 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canConvertToExactIntegral() {
/*  69 */     return (this._value.signum() == 0 || this._value
/*  70 */       .scale() <= 0 || this._value
/*  71 */       .stripTrailingZeros().scale() <= 0);
/*     */   }
/*     */   
/*     */   public Number numberValue() {
/*  75 */     return this._value;
/*     */   }
/*     */   public short shortValue() {
/*  78 */     return this._value.shortValue();
/*     */   }
/*     */   public int intValue() {
/*  81 */     return this._value.intValue();
/*     */   }
/*     */   public long longValue() {
/*  84 */     return this._value.longValue();
/*     */   }
/*     */   
/*     */   public BigInteger bigIntegerValue() {
/*  88 */     return this._value.toBigInteger();
/*     */   }
/*     */   public float floatValue() {
/*  91 */     return this._value.floatValue();
/*     */   }
/*     */   public double doubleValue() {
/*  94 */     return this._value.doubleValue();
/*     */   }
/*     */   public BigDecimal decimalValue() {
/*  97 */     return this._value;
/*     */   }
/*     */   
/*     */   public String asText() {
/* 101 */     return this._value.toString();
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
/*     */   public final void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException {
/* 118 */     jgen.writeNumber(this._value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 124 */     if (o == this) return true; 
/* 125 */     if (o == null) return false; 
/* 126 */     if (o instanceof DecimalNode) {
/* 127 */       return (((DecimalNode)o)._value.compareTo(this._value) == 0);
/*     */     }
/* 129 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 133 */     return Double.valueOf(doubleValue()).hashCode();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/DecimalNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */