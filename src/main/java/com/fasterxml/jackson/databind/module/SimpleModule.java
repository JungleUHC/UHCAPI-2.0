/*     */ package com.fasterxml.jackson.databind.module;
/*     */ 
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.Module;
/*     */ import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*     */ import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
/*     */ import com.fasterxml.jackson.databind.deser.Deserializers;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiators;
/*     */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*     */ import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
/*     */ import com.fasterxml.jackson.databind.ser.Serializers;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ public class SimpleModule
/*     */   extends Module
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  49 */   private static final AtomicInteger MODULE_ID_SEQ = new AtomicInteger(1);
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _name;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Version _version;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _hasExplicitName;
/*     */ 
/*     */   
/*  64 */   protected SimpleSerializers _serializers = null;
/*  65 */   protected SimpleDeserializers _deserializers = null;
/*     */   
/*  67 */   protected SimpleSerializers _keySerializers = null;
/*  68 */   protected SimpleKeyDeserializers _keyDeserializers = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   protected SimpleAbstractTypeResolver _abstractTypes = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   protected SimpleValueInstantiators _valueInstantiators = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  87 */   protected BeanDeserializerModifier _deserializerModifier = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   protected BeanSerializerModifier _serializerModifier = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  98 */   protected HashMap<Class<?>, Class<?>> _mixins = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   protected LinkedHashSet<NamedType> _subtypes = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 108 */   protected PropertyNamingStrategy _namingStrategy = null;
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
/*     */   public SimpleModule() {
/* 126 */     this
/*     */       
/* 128 */       ._name = (getClass() == SimpleModule.class) ? ("SimpleModule-" + MODULE_ID_SEQ.getAndIncrement()) : getClass().getName();
/* 129 */     this._version = Version.unknownVersion();
/*     */     
/* 131 */     this._hasExplicitName = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleModule(String name) {
/* 139 */     this(name, Version.unknownVersion());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleModule(Version version) {
/* 147 */     this(version.getArtifactId(), version);
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
/*     */   public SimpleModule(String name, Version version) {
/* 160 */     this._name = name;
/* 161 */     this._version = version;
/*     */     
/* 163 */     this._hasExplicitName = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleModule(String name, Version version, Map<Class<?>, JsonDeserializer<?>> deserializers) {
/* 171 */     this(name, version, deserializers, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleModule(String name, Version version, List<JsonSerializer<?>> serializers) {
/* 179 */     this(name, version, null, serializers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleModule(String name, Version version, Map<Class<?>, JsonDeserializer<?>> deserializers, List<JsonSerializer<?>> serializers) {
/* 189 */     this._name = name;
/*     */     
/* 191 */     this._hasExplicitName = true;
/* 192 */     this._version = version;
/* 193 */     if (deserializers != null) {
/* 194 */       this._deserializers = new SimpleDeserializers(deserializers);
/*     */     }
/* 196 */     if (serializers != null) {
/* 197 */       this._serializers = new SimpleSerializers(serializers);
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
/*     */   public Object getTypeId() {
/* 211 */     if (this._hasExplicitName) {
/* 212 */       return this._name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 219 */     if (getClass() == SimpleModule.class) {
/* 220 */       return this._name;
/*     */     }
/*     */ 
/*     */     
/* 224 */     return super.getTypeId();
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
/*     */   public void setSerializers(SimpleSerializers s) {
/* 237 */     this._serializers = s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDeserializers(SimpleDeserializers d) {
/* 244 */     this._deserializers = d;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKeySerializers(SimpleSerializers ks) {
/* 251 */     this._keySerializers = ks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKeyDeserializers(SimpleKeyDeserializers kd) {
/* 258 */     this._keyDeserializers = kd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAbstractTypes(SimpleAbstractTypeResolver atr) {
/* 265 */     this._abstractTypes = atr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValueInstantiators(SimpleValueInstantiators svi) {
/* 272 */     this._valueInstantiators = svi;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleModule setDeserializerModifier(BeanDeserializerModifier mod) {
/* 279 */     this._deserializerModifier = mod;
/* 280 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleModule setSerializerModifier(BeanSerializerModifier mod) {
/* 287 */     this._serializerModifier = mod;
/* 288 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SimpleModule setNamingStrategy(PropertyNamingStrategy naming) {
/* 295 */     this._namingStrategy = naming;
/* 296 */     return this;
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
/*     */   
/*     */   public SimpleModule addSerializer(JsonSerializer<?> ser) {
/* 315 */     _checkNotNull(ser, "serializer");
/* 316 */     if (this._serializers == null) {
/* 317 */       this._serializers = new SimpleSerializers();
/*     */     }
/* 319 */     this._serializers.addSerializer(ser);
/* 320 */     return this;
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
/*     */   public <T> SimpleModule addSerializer(Class<? extends T> type, JsonSerializer<T> ser) {
/* 332 */     _checkNotNull(type, "type to register serializer for");
/* 333 */     _checkNotNull(ser, "serializer");
/* 334 */     if (this._serializers == null) {
/* 335 */       this._serializers = new SimpleSerializers();
/*     */     }
/* 337 */     this._serializers.addSerializer(type, ser);
/* 338 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> SimpleModule addKeySerializer(Class<? extends T> type, JsonSerializer<T> ser) {
/* 343 */     _checkNotNull(type, "type to register key serializer for");
/* 344 */     _checkNotNull(ser, "key serializer");
/* 345 */     if (this._keySerializers == null) {
/* 346 */       this._keySerializers = new SimpleSerializers();
/*     */     }
/* 348 */     this._keySerializers.addSerializer(type, ser);
/* 349 */     return this;
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
/*     */   public <T> SimpleModule addDeserializer(Class<T> type, JsonDeserializer<? extends T> deser) {
/* 367 */     _checkNotNull(type, "type to register deserializer for");
/* 368 */     _checkNotNull(deser, "deserializer");
/* 369 */     if (this._deserializers == null) {
/* 370 */       this._deserializers = new SimpleDeserializers();
/*     */     }
/* 372 */     this._deserializers.addDeserializer(type, deser);
/* 373 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SimpleModule addKeyDeserializer(Class<?> type, KeyDeserializer deser) {
/* 378 */     _checkNotNull(type, "type to register key deserializer for");
/* 379 */     _checkNotNull(deser, "key deserializer");
/* 380 */     if (this._keyDeserializers == null) {
/* 381 */       this._keyDeserializers = new SimpleKeyDeserializers();
/*     */     }
/* 383 */     this._keyDeserializers.addDeserializer(type, deser);
/* 384 */     return this;
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
/*     */   public <T> SimpleModule addAbstractTypeMapping(Class<T> superType, Class<? extends T> subType) {
/* 401 */     _checkNotNull(superType, "abstract type to map");
/* 402 */     _checkNotNull(subType, "concrete type to map to");
/* 403 */     if (this._abstractTypes == null) {
/* 404 */       this._abstractTypes = new SimpleAbstractTypeResolver();
/*     */     }
/*     */     
/* 407 */     this._abstractTypes = this._abstractTypes.addMapping(superType, subType);
/* 408 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleModule registerSubtypes(Class<?>... subtypes) {
/* 418 */     if (this._subtypes == null) {
/* 419 */       this._subtypes = new LinkedHashSet<>();
/*     */     }
/* 421 */     for (Class<?> subtype : subtypes) {
/* 422 */       _checkNotNull(subtype, "subtype to register");
/* 423 */       this._subtypes.add(new NamedType(subtype));
/*     */     } 
/* 425 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleModule registerSubtypes(NamedType... subtypes) {
/* 435 */     if (this._subtypes == null) {
/* 436 */       this._subtypes = new LinkedHashSet<>();
/*     */     }
/* 438 */     for (NamedType subtype : subtypes) {
/* 439 */       _checkNotNull(subtype, "subtype to register");
/* 440 */       this._subtypes.add(subtype);
/*     */     } 
/* 442 */     return this;
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
/*     */   public SimpleModule registerSubtypes(Collection<Class<?>> subtypes) {
/* 454 */     if (this._subtypes == null) {
/* 455 */       this._subtypes = new LinkedHashSet<>();
/*     */     }
/* 457 */     for (Class<?> subtype : subtypes) {
/* 458 */       _checkNotNull(subtype, "subtype to register");
/* 459 */       this._subtypes.add(new NamedType(subtype));
/*     */     } 
/* 461 */     return this;
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
/*     */   public SimpleModule addValueInstantiator(Class<?> beanType, ValueInstantiator inst) {
/* 479 */     _checkNotNull(beanType, "class to register value instantiator for");
/* 480 */     _checkNotNull(inst, "value instantiator");
/* 481 */     if (this._valueInstantiators == null) {
/* 482 */       this._valueInstantiators = new SimpleValueInstantiators();
/*     */     }
/* 484 */     this._valueInstantiators = this._valueInstantiators.addValueInstantiator(beanType, inst);
/* 485 */     return this;
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
/*     */   public SimpleModule setMixInAnnotation(Class<?> targetType, Class<?> mixinClass) {
/* 498 */     _checkNotNull(targetType, "target type");
/* 499 */     _checkNotNull(mixinClass, "mixin class");
/* 500 */     if (this._mixins == null) {
/* 501 */       this._mixins = new HashMap<>();
/*     */     }
/* 503 */     this._mixins.put(targetType, mixinClass);
/* 504 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getModuleName() {
/* 515 */     return this._name;
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
/*     */   public void setupModule(Module.SetupContext context) {
/* 528 */     if (this._serializers != null) {
/* 529 */       context.addSerializers((Serializers)this._serializers);
/*     */     }
/* 531 */     if (this._deserializers != null) {
/* 532 */       context.addDeserializers((Deserializers)this._deserializers);
/*     */     }
/* 534 */     if (this._keySerializers != null) {
/* 535 */       context.addKeySerializers((Serializers)this._keySerializers);
/*     */     }
/* 537 */     if (this._keyDeserializers != null) {
/* 538 */       context.addKeyDeserializers(this._keyDeserializers);
/*     */     }
/* 540 */     if (this._abstractTypes != null) {
/* 541 */       context.addAbstractTypeResolver(this._abstractTypes);
/*     */     }
/* 543 */     if (this._valueInstantiators != null) {
/* 544 */       context.addValueInstantiators((ValueInstantiators)this._valueInstantiators);
/*     */     }
/* 546 */     if (this._deserializerModifier != null) {
/* 547 */       context.addBeanDeserializerModifier(this._deserializerModifier);
/*     */     }
/* 549 */     if (this._serializerModifier != null) {
/* 550 */       context.addBeanSerializerModifier(this._serializerModifier);
/*     */     }
/* 552 */     if (this._subtypes != null && this._subtypes.size() > 0) {
/* 553 */       context.registerSubtypes((NamedType[])this._subtypes.toArray((Object[])new NamedType[this._subtypes.size()]));
/*     */     }
/* 555 */     if (this._namingStrategy != null) {
/* 556 */       context.setNamingStrategy(this._namingStrategy);
/*     */     }
/* 558 */     if (this._mixins != null) {
/* 559 */       for (Map.Entry<Class<?>, Class<?>> entry : this._mixins.entrySet()) {
/* 560 */         context.setMixInAnnotations(entry.getKey(), entry.getValue());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Version version() {
/* 566 */     return this._version;
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
/*     */   protected void _checkNotNull(Object thingy, String type) {
/* 579 */     if (thingy == null)
/* 580 */       throw new IllegalArgumentException(String.format("Cannot pass `null` as %s", new Object[] { type })); 
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/module/SimpleModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */