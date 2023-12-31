/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import java.io.IOException;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UUIDSerializer
/*     */   extends StdScalarSerializer<UUID>
/*     */   implements ContextualSerializer
/*     */ {
/*  29 */   static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Boolean _asBinary;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UUIDSerializer() {
/*  40 */     this((Boolean)null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected UUIDSerializer(Boolean asBinary) {
/*  46 */     super(UUID.class);
/*  47 */     this._asBinary = asBinary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty(SerializerProvider prov, UUID value) {
/*  54 */     if (value.getLeastSignificantBits() == 0L && value
/*  55 */       .getMostSignificantBits() == 0L) {
/*  56 */       return true;
/*     */     }
/*  58 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
/*  65 */     JsonFormat.Value format = findFormatOverrides(serializers, property, 
/*  66 */         handledType());
/*  67 */     Boolean asBinary = null;
/*     */     
/*  69 */     if (format != null) {
/*  70 */       JsonFormat.Shape shape = format.getShape();
/*  71 */       if (shape == JsonFormat.Shape.BINARY) {
/*  72 */         asBinary = Boolean.valueOf(true);
/*  73 */       } else if (shape == JsonFormat.Shape.STRING) {
/*  74 */         asBinary = Boolean.valueOf(false);
/*     */       } 
/*     */     } 
/*     */     
/*  78 */     if (!Objects.equals(asBinary, this._asBinary)) {
/*  79 */       return new UUIDSerializer(asBinary);
/*     */     }
/*  81 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serialize(UUID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
/*  89 */     if (_writeAsBinary(gen)) {
/*  90 */       gen.writeBinary(_asBytes(value));
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/*  97 */     char[] ch = new char[36];
/*  98 */     long msb = value.getMostSignificantBits();
/*  99 */     _appendInt((int)(msb >> 32L), ch, 0);
/* 100 */     ch[8] = '-';
/* 101 */     int i = (int)msb;
/* 102 */     _appendShort(i >>> 16, ch, 9);
/* 103 */     ch[13] = '-';
/* 104 */     _appendShort(i, ch, 14);
/* 105 */     ch[18] = '-';
/*     */     
/* 107 */     long lsb = value.getLeastSignificantBits();
/* 108 */     _appendShort((int)(lsb >>> 48L), ch, 19);
/* 109 */     ch[23] = '-';
/* 110 */     _appendShort((int)(lsb >>> 32L), ch, 24);
/* 111 */     _appendInt((int)lsb, ch, 28);
/*     */     
/* 113 */     gen.writeString(ch, 0, 36);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _writeAsBinary(JsonGenerator g) {
/* 119 */     if (this._asBinary != null) {
/* 120 */       return this._asBinary.booleanValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 126 */     return (!(g instanceof com.fasterxml.jackson.databind.util.TokenBuffer) && g.canWriteBinaryNatively());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 135 */     visitStringFormat(visitor, typeHint, JsonValueFormat.UUID);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void _appendInt(int bits, char[] ch, int offset) {
/* 140 */     _appendShort(bits >> 16, ch, offset);
/* 141 */     _appendShort(bits, ch, offset + 4);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void _appendShort(int bits, char[] ch, int offset) {
/* 146 */     ch[offset] = HEX_CHARS[bits >> 12 & 0xF];
/* 147 */     ch[++offset] = HEX_CHARS[bits >> 8 & 0xF];
/* 148 */     ch[++offset] = HEX_CHARS[bits >> 4 & 0xF];
/* 149 */     ch[++offset] = HEX_CHARS[bits & 0xF];
/*     */   }
/*     */ 
/*     */   
/*     */   private static final byte[] _asBytes(UUID uuid) {
/* 154 */     byte[] buffer = new byte[16];
/* 155 */     long hi = uuid.getMostSignificantBits();
/* 156 */     long lo = uuid.getLeastSignificantBits();
/* 157 */     _appendInt((int)(hi >> 32L), buffer, 0);
/* 158 */     _appendInt((int)hi, buffer, 4);
/* 159 */     _appendInt((int)(lo >> 32L), buffer, 8);
/* 160 */     _appendInt((int)lo, buffer, 12);
/* 161 */     return buffer;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final void _appendInt(int value, byte[] buffer, int offset) {
/* 166 */     buffer[offset] = (byte)(value >> 24);
/* 167 */     buffer[++offset] = (byte)(value >> 16);
/* 168 */     buffer[++offset] = (byte)(value >> 8);
/* 169 */     buffer[++offset] = (byte)value;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/UUIDSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */