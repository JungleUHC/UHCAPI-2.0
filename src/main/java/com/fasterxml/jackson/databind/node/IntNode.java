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
/*     */ public class IntNode
/*     */   extends NumericNode
/*     */ {
/*     */   static final int MIN_CANONICAL = -1;
/*     */   static final int MAX_CANONICAL = 10;
/*     */   private static final IntNode[] CANONICALS;
/*     */   protected final int _value;
/*     */   
/*     */   static {
/*  26 */     int count = 12;
/*  27 */     CANONICALS = new IntNode[count];
/*  28 */     for (int i = 0; i < count; i++) {
/*  29 */       CANONICALS[i] = new IntNode(-1 + i);
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
/*     */   public IntNode(int v) {
/*  44 */     this._value = v;
/*     */   }
/*     */   public static IntNode valueOf(int i) {
/*  47 */     if (i > 10 || i < -1) return new IntNode(i); 
/*  48 */     return CANONICALS[i - -1];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonToken asToken() {
/*  57 */     return JsonToken.VALUE_NUMBER_INT;
/*     */   }
/*     */   public JsonParser.NumberType numberType() {
/*  60 */     return JsonParser.NumberType.INT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIntegralNumber() {
/*  69 */     return true;
/*     */   }
/*     */   public boolean isInt() {
/*  72 */     return true;
/*     */   }
/*  74 */   public boolean canConvertToInt() { return true; } public boolean canConvertToLong() {
/*  75 */     return true;
/*     */   }
/*     */   
/*     */   public Number numberValue() {
/*  79 */     return Integer.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public short shortValue() {
/*  83 */     return (short)this._value;
/*     */   }
/*     */   public int intValue() {
/*  86 */     return this._value;
/*     */   }
/*     */   public long longValue() {
/*  89 */     return this._value;
/*     */   }
/*     */   public float floatValue() {
/*  92 */     return this._value;
/*     */   }
/*     */   public double doubleValue() {
/*  95 */     return this._value;
/*     */   }
/*     */   
/*     */   public BigDecimal decimalValue() {
/*  99 */     return BigDecimal.valueOf(this._value);
/*     */   }
/*     */   public BigInteger bigIntegerValue() {
/* 102 */     return BigInteger.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public String asText() {
/* 106 */     return NumberOutput.toString(this._value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean asBoolean(boolean defaultValue) {
/* 111 */     return (this._value != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
/* 118 */     g.writeNumber(this._value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 124 */     if (o == this) return true; 
/* 125 */     if (o == null) return false; 
/* 126 */     if (o instanceof IntNode) {
/* 127 */       return (((IntNode)o)._value == this._value);
/*     */     }
/* 129 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 133 */     return this._value;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/IntNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */