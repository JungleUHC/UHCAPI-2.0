/*     */ package com.fasterxml.jackson.databind.deser;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.type.ArrayType;
/*     */ import com.fasterxml.jackson.databind.type.CollectionLikeType;
/*     */ import com.fasterxml.jackson.databind.type.CollectionType;
/*     */ import com.fasterxml.jackson.databind.type.MapLikeType;
/*     */ import com.fasterxml.jackson.databind.type.MapType;
/*     */ import com.fasterxml.jackson.databind.type.ReferenceType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.Converter;
/*     */ import com.fasterxml.jackson.databind.util.LRUMap;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
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
/*     */ public final class DeserializerCache
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final LRUMap<JavaType, JsonDeserializer<Object>> _cachedDeserializers;
/*  44 */   protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers = new HashMap<>(8);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DeserializerCache() {
/*  54 */     this(2000);
/*     */   }
/*     */   
/*     */   public DeserializerCache(int maxSize) {
/*  58 */     int initial = Math.min(64, maxSize >> 2);
/*  59 */     this._cachedDeserializers = new LRUMap(initial, maxSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object writeReplace() {
/*  70 */     this._incompleteDeserializers.clear();
/*  71 */     return this;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public int cachedDeserializersCount() {
/*  93 */     return this._cachedDeserializers.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flushCachedDeserializers() {
/* 104 */     this._cachedDeserializers.clear();
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
/*     */   public JsonDeserializer<Object> findValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType propertyType) throws JsonMappingException {
/* 139 */     JsonDeserializer<Object> deser = _findCachedDeserializer(propertyType);
/* 140 */     if (deser == null) {
/*     */       
/* 142 */       deser = _createAndCacheValueDeserializer(ctxt, factory, propertyType);
/* 143 */       if (deser == null)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 148 */         deser = _handleUnknownValueDeserializer(ctxt, propertyType);
/*     */       }
/*     */     } 
/* 151 */     return deser;
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
/*     */   public KeyDeserializer findKeyDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/* 166 */     KeyDeserializer kd = factory.createKeyDeserializer(ctxt, type);
/* 167 */     if (kd == null) {
/* 168 */       return _handleUnknownKeyDeserializer(ctxt, type);
/*     */     }
/*     */     
/* 171 */     if (kd instanceof ResolvableDeserializer) {
/* 172 */       ((ResolvableDeserializer)kd).resolve(ctxt);
/*     */     }
/* 174 */     return kd;
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
/*     */   public boolean hasValueDeserializerFor(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/* 189 */     JsonDeserializer<Object> deser = _findCachedDeserializer(type);
/* 190 */     if (deser == null) {
/* 191 */       deser = _createAndCacheValueDeserializer(ctxt, factory, type);
/*     */     }
/* 193 */     return (deser != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonDeserializer<Object> _findCachedDeserializer(JavaType type) {
/* 204 */     if (type == null) {
/* 205 */       throw new IllegalArgumentException("Null JavaType passed");
/*     */     }
/* 207 */     if (_hasCustomHandlers(type)) {
/* 208 */       return null;
/*     */     }
/* 210 */     return (JsonDeserializer<Object>)this._cachedDeserializers.get(type);
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
/*     */   protected JsonDeserializer<Object> _createAndCacheValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/* 228 */     synchronized (this._incompleteDeserializers) {
/*     */       
/* 230 */       JsonDeserializer<Object> deser = _findCachedDeserializer(type);
/* 231 */       if (deser != null) {
/* 232 */         return deser;
/*     */       }
/* 234 */       int count = this._incompleteDeserializers.size();
/*     */       
/* 236 */       if (count > 0) {
/* 237 */         deser = this._incompleteDeserializers.get(type);
/* 238 */         if (deser != null) {
/* 239 */           return deser;
/*     */         }
/*     */       } 
/*     */       
/*     */       try {
/* 244 */         return _createAndCache2(ctxt, factory, type);
/*     */       } finally {
/*     */         
/* 247 */         if (count == 0 && this._incompleteDeserializers.size() > 0) {
/* 248 */           this._incompleteDeserializers.clear();
/*     */         }
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
/*     */   protected JsonDeserializer<Object> _createAndCache2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/*     */     JsonDeserializer<Object> deser;
/*     */     try {
/* 264 */       deser = _createDeserializer(ctxt, factory, type);
/* 265 */     } catch (IllegalArgumentException iae) {
/*     */ 
/*     */       
/* 268 */       ctxt.reportBadDefinition(type, ClassUtil.exceptionMessage(iae));
/* 269 */       deser = null;
/*     */     } 
/* 271 */     if (deser == null) {
/* 272 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 278 */     boolean addToCache = (!_hasCustomHandlers(type) && deser.isCachable());
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
/* 292 */     if (deser instanceof ResolvableDeserializer) {
/* 293 */       this._incompleteDeserializers.put(type, deser);
/* 294 */       ((ResolvableDeserializer)deser).resolve(ctxt);
/* 295 */       this._incompleteDeserializers.remove(type);
/*     */     } 
/* 297 */     if (addToCache) {
/* 298 */       this._cachedDeserializers.put(type, deser);
/*     */     }
/* 300 */     return deser;
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
/*     */   protected JsonDeserializer<Object> _createDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/* 319 */     DeserializationConfig config = ctxt.getConfig();
/*     */ 
/*     */     
/* 322 */     if (type.isAbstract() || type.isMapLikeType() || type.isCollectionLikeType()) {
/* 323 */       type = factory.mapAbstractType(config, type);
/*     */     }
/* 325 */     BeanDescription beanDesc = config.introspect(type);
/*     */     
/* 327 */     JsonDeserializer<Object> deser = findDeserializerFromAnnotation(ctxt, (Annotated)beanDesc
/* 328 */         .getClassInfo());
/* 329 */     if (deser != null) {
/* 330 */       return deser;
/*     */     }
/*     */ 
/*     */     
/* 334 */     JavaType newType = modifyTypeByAnnotation(ctxt, (Annotated)beanDesc.getClassInfo(), type);
/* 335 */     if (newType != type) {
/* 336 */       type = newType;
/* 337 */       beanDesc = config.introspect(newType);
/*     */     } 
/*     */ 
/*     */     
/* 341 */     Class<?> builder = beanDesc.findPOJOBuilder();
/* 342 */     if (builder != null) {
/* 343 */       return factory.createBuilderBasedDeserializer(ctxt, type, beanDesc, builder);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 348 */     Converter<Object, Object> conv = beanDesc.findDeserializationConverter();
/* 349 */     if (conv == null) {
/* 350 */       return (JsonDeserializer)_createDeserializer2(ctxt, factory, type, beanDesc);
/*     */     }
/*     */     
/* 353 */     JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/*     */     
/* 355 */     if (!delegateType.hasRawClass(type.getRawClass())) {
/* 356 */       beanDesc = config.introspect(delegateType);
/*     */     }
/* 358 */     return (JsonDeserializer<Object>)new StdDelegatingDeserializer(conv, delegateType, 
/* 359 */         _createDeserializer2(ctxt, factory, delegateType, beanDesc));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonDeserializer<?> _createDeserializer2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 366 */     DeserializationConfig config = ctxt.getConfig();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 371 */     if (type.isEnumType()) {
/* 372 */       return factory.createEnumDeserializer(ctxt, type, beanDesc);
/*     */     }
/* 374 */     if (type.isContainerType()) {
/* 375 */       if (type.isArrayType()) {
/* 376 */         return factory.createArrayDeserializer(ctxt, (ArrayType)type, beanDesc);
/*     */       }
/* 378 */       if (type.isMapLikeType()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 384 */         JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/* 385 */         if (format.getShape() != JsonFormat.Shape.OBJECT) {
/* 386 */           MapLikeType mlt = (MapLikeType)type;
/* 387 */           if (mlt instanceof MapType) {
/* 388 */             return factory.createMapDeserializer(ctxt, (MapType)mlt, beanDesc);
/*     */           }
/* 390 */           return factory.createMapLikeDeserializer(ctxt, mlt, beanDesc);
/*     */         } 
/*     */       } 
/* 393 */       if (type.isCollectionLikeType()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 399 */         JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/* 400 */         if (format.getShape() != JsonFormat.Shape.OBJECT) {
/* 401 */           CollectionLikeType clt = (CollectionLikeType)type;
/* 402 */           if (clt instanceof CollectionType) {
/* 403 */             return factory.createCollectionDeserializer(ctxt, (CollectionType)clt, beanDesc);
/*     */           }
/* 405 */           return factory.createCollectionLikeDeserializer(ctxt, clt, beanDesc);
/*     */         } 
/*     */       } 
/*     */     } 
/* 409 */     if (type.isReferenceType()) {
/* 410 */       return factory.createReferenceDeserializer(ctxt, (ReferenceType)type, beanDesc);
/*     */     }
/* 412 */     if (JsonNode.class.isAssignableFrom(type.getRawClass())) {
/* 413 */       return factory.createTreeDeserializer(config, type, beanDesc);
/*     */     }
/* 415 */     return factory.createBeanDeserializer(ctxt, type, beanDesc);
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
/*     */   protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/* 427 */     Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
/* 428 */     if (deserDef == null) {
/* 429 */       return null;
/*     */     }
/* 431 */     JsonDeserializer<Object> deser = ctxt.deserializerInstance(ann, deserDef);
/*     */     
/* 433 */     return findConvertingDeserializer(ctxt, ann, deser);
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
/*     */   protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, Annotated a, JsonDeserializer<Object> deser) throws JsonMappingException {
/* 446 */     Converter<Object, Object> conv = findConverter(ctxt, a);
/* 447 */     if (conv == null) {
/* 448 */       return deser;
/*     */     }
/* 450 */     JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/* 451 */     return (JsonDeserializer<Object>)new StdDelegatingDeserializer(conv, delegateType, deser);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Converter<Object, Object> findConverter(DeserializationContext ctxt, Annotated a) throws JsonMappingException {
/* 458 */     Object convDef = ctxt.getAnnotationIntrospector().findDeserializationConverter(a);
/* 459 */     if (convDef == null) {
/* 460 */       return null;
/*     */     }
/* 462 */     return ctxt.converterInstance(a, convDef);
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
/*     */ 
/*     */   
/*     */   private JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
/*     */     MapLikeType mapLikeType;
/* 484 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 485 */     if (intr == null) {
/* 486 */       return type;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 492 */     if (type.isMapLikeType()) {
/* 493 */       JavaType keyType = type.getKeyType();
/*     */ 
/*     */ 
/*     */       
/* 497 */       if (keyType != null && keyType.getValueHandler() == null) {
/* 498 */         Object kdDef = intr.findKeyDeserializer(a);
/* 499 */         if (kdDef != null) {
/* 500 */           KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
/* 501 */           if (kd != null) {
/* 502 */             mapLikeType = ((MapLikeType)type).withKeyValueHandler(kd);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 508 */     JavaType contentType = mapLikeType.getContentType();
/* 509 */     if (contentType != null && 
/* 510 */       contentType.getValueHandler() == null) {
/* 511 */       Object cdDef = intr.findContentDeserializer(a);
/* 512 */       if (cdDef != null) {
/* 513 */         JsonDeserializer<?> cd = null;
/* 514 */         if (cdDef instanceof JsonDeserializer) {
/* 515 */           cd = (JsonDeserializer)cdDef;
/*     */         } else {
/* 517 */           Class<?> cdClass = _verifyAsClass(cdDef, "findContentDeserializer", JsonDeserializer.None.class);
/* 518 */           if (cdClass != null) {
/* 519 */             cd = ctxt.deserializerInstance(a, cdClass);
/*     */           }
/*     */         } 
/* 522 */         if (cd != null) {
/* 523 */           javaType1 = mapLikeType.withContentValueHandler(cd);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 531 */     JavaType javaType1 = intr.refineDeserializationType((MapperConfig)ctxt.getConfig(), a, javaType1);
/*     */     
/* 533 */     return javaType1;
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
/*     */   private boolean _hasCustomHandlers(JavaType t) {
/* 549 */     if (t.isContainerType()) {
/*     */       
/* 551 */       JavaType ct = t.getContentType();
/* 552 */       if (ct != null && (
/* 553 */         ct.getValueHandler() != null || ct.getTypeHandler() != null)) {
/* 554 */         return true;
/*     */       }
/*     */ 
/*     */       
/* 558 */       if (t.isMapLikeType()) {
/* 559 */         JavaType kt = t.getKeyType();
/* 560 */         if (kt.getValueHandler() != null) {
/* 561 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 565 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private Class<?> _verifyAsClass(Object src, String methodName, Class<?> noneClass) {
/* 570 */     if (src == null) {
/* 571 */       return null;
/*     */     }
/* 573 */     if (!(src instanceof Class)) {
/* 574 */       throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
/*     */     }
/* 576 */     Class<?> cls = (Class)src;
/* 577 */     if (cls == noneClass || ClassUtil.isBogusClass(cls)) {
/* 578 */       return null;
/*     */     }
/* 580 */     return cls;
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
/*     */   protected JsonDeserializer<Object> _handleUnknownValueDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/* 593 */     Class<?> rawClass = type.getRawClass();
/* 594 */     if (!ClassUtil.isConcrete(rawClass)) {
/* 595 */       return (JsonDeserializer<Object>)ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for abstract type " + type);
/*     */     }
/* 597 */     return (JsonDeserializer<Object>)ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for type " + type);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected KeyDeserializer _handleUnknownKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/* 603 */     return (KeyDeserializer)ctxt.reportBadDefinition(type, "Cannot find a (Map) Key deserializer for type " + type);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/DeserializerCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */