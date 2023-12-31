/*     */ package com.fasterxml.jackson.databind.ser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import java.util.Arrays;
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
/*     */ public abstract class PropertySerializerMap
/*     */ {
/*     */   protected final boolean _resetWhenFull;
/*     */   
/*     */   protected PropertySerializerMap(boolean resetWhenFull) {
/*  36 */     this._resetWhenFull = resetWhenFull;
/*     */   }
/*     */   
/*     */   protected PropertySerializerMap(PropertySerializerMap base) {
/*  40 */     this._resetWhenFull = base._resetWhenFull;
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
/*     */   public abstract JsonSerializer<Object> serializerFor(Class<?> paramClass);
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
/*     */   public final SerializerAndMapResult findAndAddPrimarySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/*  64 */     JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
/*  65 */     return new SerializerAndMapResult(serializer, newWith(type, serializer));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SerializerAndMapResult findAndAddPrimarySerializer(JavaType type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/*  72 */     JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
/*  73 */     return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
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
/*     */   public final SerializerAndMapResult findAndAddSecondarySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/*  90 */     JsonSerializer<Object> serializer = provider.findContentValueSerializer(type, property);
/*  91 */     return new SerializerAndMapResult(serializer, newWith(type, serializer));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SerializerAndMapResult findAndAddSecondarySerializer(JavaType type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/*  98 */     JsonSerializer<Object> serializer = provider.findContentValueSerializer(type, property);
/*  99 */     return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
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
/*     */   
/*     */   public final SerializerAndMapResult findAndAddRootValueSerializer(Class<?> type, SerializerProvider provider) throws JsonMappingException {
/* 117 */     JsonSerializer<Object> serializer = provider.findTypedValueSerializer(type, false, null);
/* 118 */     return new SerializerAndMapResult(serializer, newWith(type, serializer));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SerializerAndMapResult findAndAddRootValueSerializer(JavaType type, SerializerProvider provider) throws JsonMappingException {
/* 128 */     JsonSerializer<Object> serializer = provider.findTypedValueSerializer(type, false, null);
/* 129 */     return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
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
/*     */   public final SerializerAndMapResult findAndAddKeySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/* 144 */     JsonSerializer<Object> serializer = provider.findKeySerializer(type, property);
/* 145 */     return new SerializerAndMapResult(serializer, newWith(type, serializer));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SerializerAndMapResult addSerializer(Class<?> type, JsonSerializer<Object> serializer) {
/* 155 */     return new SerializerAndMapResult(serializer, newWith(type, serializer));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SerializerAndMapResult addSerializer(JavaType type, JsonSerializer<Object> serializer) {
/* 162 */     return new SerializerAndMapResult(serializer, newWith(type.getRawClass(), serializer));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract PropertySerializerMap newWith(Class<?> paramClass, JsonSerializer<Object> paramJsonSerializer);
/*     */ 
/*     */   
/*     */   public static PropertySerializerMap emptyForProperties() {
/* 171 */     return Empty.FOR_PROPERTIES;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PropertySerializerMap emptyForRootValues() {
/* 178 */     return Empty.FOR_ROOT_VALUES;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class SerializerAndMapResult
/*     */   {
/*     */     public final JsonSerializer<Object> serializer;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final PropertySerializerMap map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public SerializerAndMapResult(JsonSerializer<Object> serializer, PropertySerializerMap map) {
/* 199 */       this.serializer = serializer;
/* 200 */       this.map = map;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class TypeAndSerializer
/*     */   {
/*     */     public final Class<?> type;
/*     */     
/*     */     public final JsonSerializer<Object> serializer;
/*     */ 
/*     */     
/*     */     public TypeAndSerializer(Class<?> type, JsonSerializer<Object> serializer) {
/* 213 */       this.type = type;
/* 214 */       this.serializer = serializer;
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
/*     */   private static final class Empty
/*     */     extends PropertySerializerMap
/*     */   {
/* 231 */     public static final Empty FOR_PROPERTIES = new Empty(false);
/*     */ 
/*     */     
/* 234 */     public static final Empty FOR_ROOT_VALUES = new Empty(true);
/*     */     
/*     */     protected Empty(boolean resetWhenFull) {
/* 237 */       super(resetWhenFull);
/*     */     }
/*     */ 
/*     */     
/*     */     public JsonSerializer<Object> serializerFor(Class<?> type) {
/* 242 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
/* 247 */       return new PropertySerializerMap.Single(this, type, serializer);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class Single
/*     */     extends PropertySerializerMap
/*     */   {
/*     */     private final Class<?> _type;
/*     */ 
/*     */     
/*     */     private final JsonSerializer<Object> _serializer;
/*     */ 
/*     */     
/*     */     public Single(PropertySerializerMap base, Class<?> type, JsonSerializer<Object> serializer) {
/* 263 */       super(base);
/* 264 */       this._type = type;
/* 265 */       this._serializer = serializer;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<Object> serializerFor(Class<?> type) {
/* 271 */       if (type == this._type) {
/* 272 */         return this._serializer;
/*     */       }
/* 274 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
/* 279 */       return new PropertySerializerMap.Double(this, this._type, this._serializer, type, serializer);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class Double
/*     */     extends PropertySerializerMap
/*     */   {
/*     */     private final Class<?> _type1;
/*     */     private final Class<?> _type2;
/*     */     private final JsonSerializer<Object> _serializer1;
/*     */     private final JsonSerializer<Object> _serializer2;
/*     */     
/*     */     public Double(PropertySerializerMap base, Class<?> type1, JsonSerializer<Object> serializer1, Class<?> type2, JsonSerializer<Object> serializer2) {
/* 292 */       super(base);
/* 293 */       this._type1 = type1;
/* 294 */       this._serializer1 = serializer1;
/* 295 */       this._type2 = type2;
/* 296 */       this._serializer2 = serializer2;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<Object> serializerFor(Class<?> type) {
/* 302 */       if (type == this._type1) {
/* 303 */         return this._serializer1;
/*     */       }
/* 305 */       if (type == this._type2) {
/* 306 */         return this._serializer2;
/*     */       }
/* 308 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
/* 314 */       PropertySerializerMap.TypeAndSerializer[] ts = new PropertySerializerMap.TypeAndSerializer[3];
/* 315 */       ts[0] = new PropertySerializerMap.TypeAndSerializer(this._type1, this._serializer1);
/* 316 */       ts[1] = new PropertySerializerMap.TypeAndSerializer(this._type2, this._serializer2);
/* 317 */       ts[2] = new PropertySerializerMap.TypeAndSerializer(type, serializer);
/* 318 */       return new PropertySerializerMap.Multi(this, ts);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class Multi
/*     */     extends PropertySerializerMap
/*     */   {
/*     */     private static final int MAX_ENTRIES = 8;
/*     */ 
/*     */ 
/*     */     
/*     */     private final PropertySerializerMap.TypeAndSerializer[] _entries;
/*     */ 
/*     */ 
/*     */     
/*     */     public Multi(PropertySerializerMap base, PropertySerializerMap.TypeAndSerializer[] entries) {
/* 337 */       super(base);
/* 338 */       this._entries = entries;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonSerializer<Object> serializerFor(Class<?> type) {
/* 346 */       PropertySerializerMap.TypeAndSerializer entry = this._entries[0];
/* 347 */       if (entry.type == type) return entry.serializer; 
/* 348 */       entry = this._entries[1];
/* 349 */       if (entry.type == type) return entry.serializer; 
/* 350 */       entry = this._entries[2];
/* 351 */       if (entry.type == type) return entry.serializer;
/*     */       
/* 353 */       switch (this._entries.length) {
/*     */         case 8:
/* 355 */           entry = this._entries[7];
/* 356 */           if (entry.type == type) return entry.serializer; 
/*     */         case 7:
/* 358 */           entry = this._entries[6];
/* 359 */           if (entry.type == type) return entry.serializer; 
/*     */         case 6:
/* 361 */           entry = this._entries[5];
/* 362 */           if (entry.type == type) return entry.serializer; 
/*     */         case 5:
/* 364 */           entry = this._entries[4];
/* 365 */           if (entry.type == type) return entry.serializer; 
/*     */         case 4:
/* 367 */           entry = this._entries[3];
/* 368 */           if (entry.type == type) return entry.serializer; 
/*     */           break;
/*     */       } 
/* 371 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
/* 377 */       int len = this._entries.length;
/*     */ 
/*     */       
/* 380 */       if (len == 8) {
/* 381 */         if (this._resetWhenFull) {
/* 382 */           return new PropertySerializerMap.Single(this, type, serializer);
/*     */         }
/* 384 */         return this;
/*     */       } 
/* 386 */       PropertySerializerMap.TypeAndSerializer[] entries = Arrays.<PropertySerializerMap.TypeAndSerializer>copyOf(this._entries, len + 1);
/* 387 */       entries[len] = new PropertySerializerMap.TypeAndSerializer(type, serializer);
/* 388 */       return new Multi(this, entries);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/impl/PropertySerializerMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */