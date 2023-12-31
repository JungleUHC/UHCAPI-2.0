/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Base64Variants;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.exc.InvalidFormatException;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UUIDDeserializer
/*     */   extends FromStringDeserializer<UUID>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  17 */   static final int[] HEX_DIGITS = new int[127];
/*     */   static {
/*  19 */     Arrays.fill(HEX_DIGITS, -1); int i;
/*  20 */     for (i = 0; i < 10; ) { HEX_DIGITS[48 + i] = i; i++; }
/*  21 */      for (i = 0; i < 6; i++) {
/*  22 */       HEX_DIGITS[97 + i] = 10 + i;
/*  23 */       HEX_DIGITS[65 + i] = 10 + i;
/*     */     } 
/*     */   }
/*     */   public UUIDDeserializer() {
/*  27 */     super(UUID.class);
/*     */   }
/*     */   
/*     */   public Object getEmptyValue(DeserializationContext ctxt) {
/*  31 */     return new UUID(0L, 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected UUID _deserialize(String id, DeserializationContext ctxt) throws IOException {
/*  39 */     if (id.length() != 36) {
/*     */ 
/*     */ 
/*     */       
/*  43 */       if (id.length() == 24) {
/*  44 */         byte[] stuff = Base64Variants.getDefaultVariant().decode(id);
/*  45 */         return _fromBytes(stuff, ctxt);
/*     */       } 
/*  47 */       return _badFormat(id, ctxt);
/*     */     } 
/*     */ 
/*     */     
/*  51 */     if (id.charAt(8) != '-' || id.charAt(13) != '-' || id
/*  52 */       .charAt(18) != '-' || id.charAt(23) != '-') {
/*  53 */       _badFormat(id, ctxt);
/*     */     }
/*  55 */     long l1 = intFromChars(id, 0, ctxt);
/*  56 */     l1 <<= 32L;
/*  57 */     long l2 = shortFromChars(id, 9, ctxt) << 16L;
/*  58 */     l2 |= shortFromChars(id, 14, ctxt);
/*  59 */     long hi = l1 + l2;
/*     */     
/*  61 */     int i1 = shortFromChars(id, 19, ctxt) << 16 | shortFromChars(id, 24, ctxt);
/*  62 */     l1 = i1;
/*  63 */     l1 <<= 32L;
/*  64 */     l2 = intFromChars(id, 28, ctxt);
/*  65 */     l2 = l2 << 32L >>> 32L;
/*  66 */     long lo = l1 | l2;
/*     */     
/*  68 */     return new UUID(hi, lo);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected UUID _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException {
/*  74 */     if (ob instanceof byte[]) {
/*  75 */       return _fromBytes((byte[])ob, ctxt);
/*     */     }
/*  77 */     return super._deserializeEmbedded(ob, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private UUID _badFormat(String uuidStr, DeserializationContext ctxt) throws IOException {
/*  83 */     return (UUID)ctxt.handleWeirdStringValue(handledType(), uuidStr, "UUID has to be represented by standard 36-char representation", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   int intFromChars(String str, int index, DeserializationContext ctxt) throws JsonMappingException {
/*  88 */     return (byteFromChars(str, index, ctxt) << 24) + (
/*  89 */       byteFromChars(str, index + 2, ctxt) << 16) + (
/*  90 */       byteFromChars(str, index + 4, ctxt) << 8) + 
/*  91 */       byteFromChars(str, index + 6, ctxt);
/*     */   }
/*     */   
/*     */   int shortFromChars(String str, int index, DeserializationContext ctxt) throws JsonMappingException {
/*  95 */     return (byteFromChars(str, index, ctxt) << 8) + byteFromChars(str, index + 2, ctxt);
/*     */   }
/*     */ 
/*     */   
/*     */   int byteFromChars(String str, int index, DeserializationContext ctxt) throws JsonMappingException {
/* 100 */     char c1 = str.charAt(index);
/* 101 */     char c2 = str.charAt(index + 1);
/*     */     
/* 103 */     if (c1 <= '' && c2 <= '') {
/* 104 */       int hex = HEX_DIGITS[c1] << 4 | HEX_DIGITS[c2];
/* 105 */       if (hex >= 0) {
/* 106 */         return hex;
/*     */       }
/*     */     } 
/* 109 */     if (c1 > '' || HEX_DIGITS[c1] < 0) {
/* 110 */       return _badChar(str, index, ctxt, c1);
/*     */     }
/* 112 */     return _badChar(str, index + 1, ctxt, c2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int _badChar(String uuidStr, int index, DeserializationContext ctxt, char c) throws JsonMappingException {
/* 118 */     throw ctxt.weirdStringException(uuidStr, handledType(), 
/* 119 */         String.format("Non-hex character '%c' (value 0x%s), not valid for UUID String", new Object[] {
/*     */             
/* 121 */             Character.valueOf(c), Integer.toHexString(c) }));
/*     */   }
/*     */   
/*     */   private UUID _fromBytes(byte[] bytes, DeserializationContext ctxt) throws JsonMappingException {
/* 125 */     if (bytes.length != 16) {
/* 126 */       throw InvalidFormatException.from(ctxt.getParser(), "Can only construct UUIDs from byte[16]; got " + bytes.length + " bytes", bytes, 
/*     */           
/* 128 */           handledType());
/*     */     }
/* 130 */     return new UUID(_long(bytes, 0), _long(bytes, 8));
/*     */   }
/*     */   
/*     */   private static long _long(byte[] b, int offset) {
/* 134 */     long l1 = _int(b, offset) << 32L;
/* 135 */     long l2 = _int(b, offset + 4);
/*     */     
/* 137 */     l2 = l2 << 32L >>> 32L;
/* 138 */     return l1 | l2;
/*     */   }
/*     */   
/*     */   private static int _int(byte[] b, int offset) {
/* 142 */     return b[offset] << 24 | (b[offset + 1] & 0xFF) << 16 | (b[offset + 2] & 0xFF) << 8 | b[offset + 3] & 0xFF;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/UUIDDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */