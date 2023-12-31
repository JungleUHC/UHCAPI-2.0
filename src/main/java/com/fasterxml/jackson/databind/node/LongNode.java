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
/*     */ public class LongNode
/*     */   extends NumericNode
/*     */ {
/*     */   protected final long _value;
/*     */   
/*     */   public LongNode(long v) {
/*  27 */     this._value = v;
/*     */   } public static LongNode valueOf(long l) {
/*  29 */     return new LongNode(l);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken asToken() {
/*  37 */     return JsonToken.VALUE_NUMBER_INT;
/*     */   }
/*     */   public JsonParser.NumberType numberType() {
/*  40 */     return JsonParser.NumberType.LONG;
/*     */   }
/*     */   
/*     */   public boolean isIntegralNumber() {
/*  44 */     return true;
/*     */   }
/*     */   public boolean isLong() {
/*  47 */     return true;
/*     */   }
/*     */   public boolean canConvertToInt() {
/*  50 */     return (this._value >= -2147483648L && this._value <= 2147483647L);
/*     */   } public boolean canConvertToLong() {
/*  52 */     return true;
/*     */   }
/*     */   
/*     */   public Number numberValue() {
/*  56 */     return Long.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public short shortValue() {
/*  60 */     return (short)(int)this._value;
/*     */   }
/*     */   public int intValue() {
/*  63 */     return (int)this._value;
/*     */   }
/*     */   public long longValue() {
/*  66 */     return this._value;
/*     */   }
/*     */   public float floatValue() {
/*  69 */     return (float)this._value;
/*     */   }
/*     */   public double doubleValue() {
/*  72 */     return this._value;
/*     */   }
/*     */   public BigDecimal decimalValue() {
/*  75 */     return BigDecimal.valueOf(this._value);
/*     */   }
/*     */   public BigInteger bigIntegerValue() {
/*  78 */     return BigInteger.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public String asText() {
/*  82 */     return NumberOutput.toString(this._value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean asBoolean(boolean defaultValue) {
/*  87 */     return (this._value != 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
/*  94 */     g.writeNumber(this._value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 100 */     if (o == this) return true; 
/* 101 */     if (o == null) return false; 
/* 102 */     if (o instanceof LongNode) {
/* 103 */       return (((LongNode)o)._value == this._value);
/*     */     }
/* 105 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 110 */     return (int)this._value ^ (int)(this._value >> 32L);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/LongNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */