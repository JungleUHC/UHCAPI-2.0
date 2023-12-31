/*     */ package com.fasterxml.jackson.databind.node;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variant;
/*     */ import com.fasterxml.jackson.core.Base64Variants;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.core.io.CharTypes;
/*     */ import com.fasterxml.jackson.core.io.NumberInput;
/*     */ import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.exc.InvalidFormatException;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TextNode
/*     */   extends ValueNode
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*  21 */   static final TextNode EMPTY_STRING_NODE = new TextNode("");
/*     */   protected final String _value;
/*     */   
/*     */   public TextNode(String v) {
/*  25 */     this._value = v;
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
/*     */   public static TextNode valueOf(String v) {
/*  38 */     if (v == null) {
/*  39 */       return null;
/*     */     }
/*  41 */     if (v.isEmpty()) {
/*  42 */       return EMPTY_STRING_NODE;
/*     */     }
/*  44 */     return new TextNode(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonNodeType getNodeType() {
/*  49 */     return JsonNodeType.STRING;
/*     */   }
/*     */   public JsonToken asToken() {
/*  52 */     return JsonToken.VALUE_STRING;
/*     */   }
/*     */   
/*     */   public String textValue() {
/*  56 */     return this._value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/*  67 */     String str = this._value.trim();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     int initBlockSize = 4 + (str.length() >> 2) * 3;
/*  73 */     ByteArrayBuilder builder = new ByteArrayBuilder(Math.max(16, 
/*  74 */           Math.min(65536, initBlockSize)));
/*     */     try {
/*  76 */       b64variant.decode(str, builder);
/*  77 */     } catch (IllegalArgumentException e) {
/*  78 */       throw InvalidFormatException.from(null, 
/*  79 */           String.format("Cannot access contents of TextNode as binary due to broken Base64 encoding: %s", new Object[] {
/*     */               
/*  81 */               e.getMessage()
/*     */             }), str, byte[].class);
/*     */     } 
/*  84 */     return builder.toByteArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] binaryValue() throws IOException {
/*  89 */     return getBinaryValue(Base64Variants.getDefaultVariant());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String asText() {
/* 100 */     return this._value;
/*     */   }
/*     */ 
/*     */   
/*     */   public String asText(String defaultValue) {
/* 105 */     return (this._value == null) ? defaultValue : this._value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean asBoolean(boolean defaultValue) {
/* 112 */     if (this._value != null) {
/* 113 */       String v = this._value.trim();
/* 114 */       if ("true".equals(v)) {
/* 115 */         return true;
/*     */       }
/* 117 */       if ("false".equals(v)) {
/* 118 */         return false;
/*     */       }
/*     */     } 
/* 121 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public int asInt(int defaultValue) {
/* 126 */     return NumberInput.parseAsInt(this._value, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public long asLong(long defaultValue) {
/* 131 */     return NumberInput.parseAsLong(this._value, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public double asDouble(double defaultValue) {
/* 136 */     return NumberInput.parseAsDouble(this._value, defaultValue);
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
/*     */   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
/* 148 */     if (this._value == null) {
/* 149 */       g.writeNull();
/*     */     } else {
/* 151 */       g.writeString(this._value);
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
/*     */   public boolean equals(Object o) {
/* 164 */     if (o == this) return true; 
/* 165 */     if (o == null) return false; 
/* 166 */     if (o instanceof TextNode) {
/* 167 */       return ((TextNode)o)._value.equals(this._value);
/*     */     }
/* 169 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 173 */     return this._value.hashCode();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected static void appendQuoted(StringBuilder sb, String content) {
/* 178 */     sb.append('"');
/* 179 */     CharTypes.appendQuoted(sb, content);
/* 180 */     sb.append('"');
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/node/TextNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */