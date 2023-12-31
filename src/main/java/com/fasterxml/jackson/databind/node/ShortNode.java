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
/*     */ public class ShortNode
/*     */   extends NumericNode
/*     */ {
/*     */   protected final short _value;
/*     */   
/*     */   public ShortNode(short v) {
/*  27 */     this._value = v;
/*     */   } public static ShortNode valueOf(short l) {
/*  29 */     return new ShortNode(l);
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
/*  40 */     return JsonParser.NumberType.INT;
/*     */   }
/*     */   
/*     */   public boolean isIntegralNumber() {
/*  44 */     return true;
/*     */   }
/*     */   public boolean isShort() {
/*  47 */     return true;
/*     */   }
/*  49 */   public boolean canConvertToInt() { return true; } public boolean canConvertToLong() {
/*  50 */     return true;
/*     */   }
/*     */   
/*     */   public Number numberValue() {
/*  54 */     return Short.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public short shortValue() {
/*  58 */     return this._value;
/*     */   }
/*     */   public int intValue() {
/*  61 */     return this._value;
/*     */   }
/*     */   public long longValue() {
/*  64 */     return this._value;
/*     */   }
/*     */   public float floatValue() {
/*  67 */     return this._value;
/*     */   }
/*     */   public double doubleValue() {
/*  70 */     return this._value;
/*     */   }
/*     */   public BigDecimal decimalValue() {
/*  73 */     return BigDecimal.valueOf(this._value);
/*     */   }
/*     */   public BigInteger bigIntegerValue() {
/*  76 */     return BigInteger.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public String asText() {
/*  80 */     return NumberOutput.toString(this._value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean asBoolean(boolean defaultValue) {
/*  85 */     return (this._value != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
/*  92 */     g.writeNumber(this._value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  98 */     if (o == this) return true; 
/*  99 */     if (o == null) return false; 
/* 100 */     if (o instanceof ShortNode) {
/* 101 */       return (((ShortNode)o)._value == this._value);
/*     */     }
/* 103 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 108 */     return this._value;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/ShortNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */