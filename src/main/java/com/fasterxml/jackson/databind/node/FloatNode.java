/*     */ package com.fasterxml.jackson.databind.node;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.io.NumberOutput;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import java.io.IOException;
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
/*     */ public class FloatNode
/*     */   extends NumericNode
/*     */ {
/*     */   protected final float _value;
/*     */   
/*     */   public FloatNode(float v) {
/*  29 */     this._value = v;
/*     */   } public static FloatNode valueOf(float v) {
/*  31 */     return new FloatNode(v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken asToken() {
/*  39 */     return JsonToken.VALUE_NUMBER_FLOAT;
/*     */   }
/*     */   public JsonParser.NumberType numberType() {
/*  42 */     return JsonParser.NumberType.FLOAT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFloatingPointNumber() {
/*  51 */     return true;
/*     */   }
/*     */   public boolean isFloat() {
/*  54 */     return true;
/*     */   }
/*     */   public boolean canConvertToInt() {
/*  57 */     return (this._value >= -2.14748365E9F && this._value <= 2.14748365E9F);
/*     */   }
/*     */   
/*     */   public boolean canConvertToLong() {
/*  61 */     return (this._value >= -9.223372E18F && this._value <= 9.223372E18F);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canConvertToExactIntegral() {
/*  66 */     return (!Float.isNaN(this._value) && !Float.isInfinite(this._value) && this._value == 
/*  67 */       Math.round(this._value));
/*     */   }
/*     */ 
/*     */   
/*     */   public Number numberValue() {
/*  72 */     return Float.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public short shortValue() {
/*  76 */     return (short)(int)this._value;
/*     */   }
/*     */   public int intValue() {
/*  79 */     return (int)this._value;
/*     */   }
/*     */   public long longValue() {
/*  82 */     return (long)this._value;
/*     */   }
/*     */   public float floatValue() {
/*  85 */     return this._value;
/*     */   }
/*     */   public double doubleValue() {
/*  88 */     return this._value;
/*     */   }
/*     */   public BigDecimal decimalValue() {
/*  91 */     return BigDecimal.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public BigInteger bigIntegerValue() {
/*  95 */     return decimalValue().toBigInteger();
/*     */   }
/*     */ 
/*     */   
/*     */   public String asText() {
/* 100 */     return NumberOutput.toString(this._value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNaN() {
/* 107 */     return (Float.isNaN(this._value) || Float.isInfinite(this._value));
/*     */   }
/*     */ 
/*     */   
/*     */   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
/* 112 */     g.writeNumber(this._value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 118 */     if (o == this) return true; 
/* 119 */     if (o == null) return false; 
/* 120 */     if (o instanceof FloatNode) {
/*     */ 
/*     */       
/* 123 */       float otherValue = ((FloatNode)o)._value;
/* 124 */       return (Float.compare(this._value, otherValue) == 0);
/*     */     } 
/* 126 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 131 */     return Float.floatToIntBits(this._value);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/FloatNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */