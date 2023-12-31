/*     */ package com.fasterxml.jackson.databind;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.core.Versioned;
/*     */ import com.fasterxml.jackson.databind.cfg.PackageVersion;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
/*     */ import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
/*     */ import java.io.Closeable;
/*     */ import java.io.Flushable;
/*     */ import java.io.IOException;
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
/*     */ public class SequenceWriter
/*     */   implements Versioned, Closeable, Flushable
/*     */ {
/*     */   protected final DefaultSerializerProvider _provider;
/*     */   protected final SerializationConfig _config;
/*     */   protected final JsonGenerator _generator;
/*     */   protected final JsonSerializer<Object> _rootSerializer;
/*     */   protected final TypeSerializer _typeSerializer;
/*     */   protected final boolean _closeGenerator;
/*     */   protected final boolean _cfgFlush;
/*     */   protected final boolean _cfgCloseCloseable;
/*     */   protected PropertySerializerMap _dynamicSerializers;
/*     */   protected boolean _openArray;
/*     */   protected boolean _closed;
/*     */   
/*     */   public SequenceWriter(DefaultSerializerProvider prov, JsonGenerator gen, boolean closeGenerator, ObjectWriter.Prefetch prefetch) throws IOException {
/*  82 */     this._provider = prov;
/*  83 */     this._generator = gen;
/*  84 */     this._closeGenerator = closeGenerator;
/*  85 */     this._rootSerializer = prefetch.getValueSerializer();
/*  86 */     this._typeSerializer = prefetch.getTypeSerializer();
/*     */     
/*  88 */     this._config = prov.getConfig();
/*  89 */     this._cfgFlush = this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
/*  90 */     this._cfgCloseCloseable = this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE);
/*     */ 
/*     */     
/*  93 */     this._dynamicSerializers = PropertySerializerMap.emptyForRootValues();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SequenceWriter init(boolean wrapInArray) throws IOException {
/* 102 */     if (wrapInArray) {
/* 103 */       this._generator.writeStartArray();
/* 104 */       this._openArray = true;
/*     */     } 
/* 106 */     return this;
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
/*     */   public Version version() {
/* 121 */     return PackageVersion.VERSION;
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
/*     */   public SequenceWriter write(Object value) throws IOException {
/* 137 */     if (value == null) {
/* 138 */       this._provider.serializeValue(this._generator, null);
/* 139 */       return this;
/*     */     } 
/*     */     
/* 142 */     if (this._cfgCloseCloseable && value instanceof Closeable) {
/* 143 */       return _writeCloseableValue(value);
/*     */     }
/* 145 */     JsonSerializer<Object> ser = this._rootSerializer;
/* 146 */     if (ser == null) {
/* 147 */       Class<?> type = value.getClass();
/* 148 */       ser = this._dynamicSerializers.serializerFor(type);
/* 149 */       if (ser == null) {
/* 150 */         ser = _findAndAddDynamic(type);
/*     */       }
/*     */     } 
/* 153 */     this._provider.serializeValue(this._generator, value, null, ser);
/* 154 */     if (this._cfgFlush) {
/* 155 */       this._generator.flush();
/*     */     }
/* 157 */     return this;
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
/*     */   public SequenceWriter write(Object value, JavaType type) throws IOException {
/* 171 */     if (value == null) {
/* 172 */       this._provider.serializeValue(this._generator, null);
/* 173 */       return this;
/*     */     } 
/*     */     
/* 176 */     if (this._cfgCloseCloseable && value instanceof Closeable) {
/* 177 */       return _writeCloseableValue(value, type);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 184 */     JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type.getRawClass());
/* 185 */     if (ser == null) {
/* 186 */       ser = _findAndAddDynamic(type);
/*     */     }
/* 188 */     this._provider.serializeValue(this._generator, value, type, ser);
/* 189 */     if (this._cfgFlush) {
/* 190 */       this._generator.flush();
/*     */     }
/* 192 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SequenceWriter writeAll(Object[] value) throws IOException {
/* 197 */     for (int i = 0, len = value.length; i < len; i++) {
/* 198 */       write(value[i]);
/*     */     }
/* 200 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <C extends java.util.Collection<?>> SequenceWriter writeAll(C container) throws IOException {
/* 206 */     for (Object value : container) {
/* 207 */       write(value);
/*     */     }
/* 209 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SequenceWriter writeAll(Iterable<?> iterable) throws IOException {
/* 217 */     for (Object value : iterable) {
/* 218 */       write(value);
/*     */     }
/* 220 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 225 */     if (!this._closed) {
/* 226 */       this._generator.flush();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 233 */     if (!this._closed) {
/* 234 */       this._closed = true;
/* 235 */       if (this._openArray) {
/* 236 */         this._openArray = false;
/* 237 */         this._generator.writeEndArray();
/*     */       } 
/* 239 */       if (this._closeGenerator) {
/* 240 */         this._generator.close();
/*     */       }
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
/*     */   protected SequenceWriter _writeCloseableValue(Object value) throws IOException {
/* 253 */     Closeable toClose = (Closeable)value;
/*     */     try {
/* 255 */       JsonSerializer<Object> ser = this._rootSerializer;
/* 256 */       if (ser == null) {
/* 257 */         Class<?> type = value.getClass();
/* 258 */         ser = this._dynamicSerializers.serializerFor(type);
/* 259 */         if (ser == null) {
/* 260 */           ser = _findAndAddDynamic(type);
/*     */         }
/*     */       } 
/* 263 */       this._provider.serializeValue(this._generator, value, null, ser);
/* 264 */       if (this._cfgFlush) {
/* 265 */         this._generator.flush();
/*     */       }
/* 267 */       Closeable tmpToClose = toClose;
/* 268 */       toClose = null;
/* 269 */       tmpToClose.close();
/*     */     } finally {
/* 271 */       if (toClose != null) {
/*     */         try {
/* 273 */           toClose.close();
/* 274 */         } catch (IOException iOException) {}
/*     */       }
/*     */     } 
/* 277 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SequenceWriter _writeCloseableValue(Object value, JavaType type) throws IOException {
/* 282 */     Closeable toClose = (Closeable)value;
/*     */     
/*     */     try {
/* 285 */       JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type.getRawClass());
/* 286 */       if (ser == null) {
/* 287 */         ser = _findAndAddDynamic(type);
/*     */       }
/* 289 */       this._provider.serializeValue(this._generator, value, type, ser);
/* 290 */       if (this._cfgFlush) {
/* 291 */         this._generator.flush();
/*     */       }
/* 293 */       Closeable tmpToClose = toClose;
/* 294 */       toClose = null;
/* 295 */       tmpToClose.close();
/*     */     } finally {
/* 297 */       if (toClose != null) {
/*     */         try {
/* 299 */           toClose.close();
/* 300 */         } catch (IOException iOException) {}
/*     */       }
/*     */     } 
/* 303 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   private final JsonSerializer<Object> _findAndAddDynamic(Class<?> type) throws JsonMappingException {
/*     */     PropertySerializerMap.SerializerAndMapResult result;
/* 309 */     if (this._typeSerializer == null) {
/* 310 */       result = this._dynamicSerializers.findAndAddRootValueSerializer(type, (SerializerProvider)this._provider);
/*     */     } else {
/* 312 */       result = this._dynamicSerializers.addSerializer(type, (JsonSerializer)new TypeWrappedSerializer(this._typeSerializer, this._provider
/* 313 */             .findValueSerializer(type, null)));
/*     */     } 
/* 315 */     this._dynamicSerializers = result.map;
/* 316 */     return result.serializer;
/*     */   }
/*     */ 
/*     */   
/*     */   private final JsonSerializer<Object> _findAndAddDynamic(JavaType type) throws JsonMappingException {
/*     */     PropertySerializerMap.SerializerAndMapResult result;
/* 322 */     if (this._typeSerializer == null) {
/* 323 */       result = this._dynamicSerializers.findAndAddRootValueSerializer(type, (SerializerProvider)this._provider);
/*     */     } else {
/* 325 */       result = this._dynamicSerializers.addSerializer(type, (JsonSerializer)new TypeWrappedSerializer(this._typeSerializer, this._provider
/* 326 */             .findValueSerializer(type, null)));
/*     */     } 
/* 328 */     this._dynamicSerializers = result.map;
/* 329 */     return result.serializer;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/SequenceWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */