/*     */ package com.fasterxml.jackson.databind.deser;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*     */ import com.fasterxml.jackson.annotation.ObjectIdResolver;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.InjectableValues;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class DefaultDeserializationContext
/*     */   extends DeserializationContext
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected transient LinkedHashMap<ObjectIdGenerator.IdKey, ReadableObjectId> _objectIds;
/*     */   private List<ObjectIdResolver> _objectIdResolvers;
/*     */   
/*     */   protected DefaultDeserializationContext(DeserializerFactory df, DeserializerCache cache) {
/*  45 */     super(df, cache);
/*     */   }
/*     */ 
/*     */   
/*     */   protected DefaultDeserializationContext(DefaultDeserializationContext src, DeserializationConfig config, JsonParser p, InjectableValues values) {
/*  50 */     super(src, config, p, values);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected DefaultDeserializationContext(DefaultDeserializationContext src, DeserializationConfig config) {
/*  56 */     super(src, config);
/*     */   }
/*     */ 
/*     */   
/*     */   protected DefaultDeserializationContext(DefaultDeserializationContext src, DeserializerFactory factory) {
/*  61 */     super(src, factory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected DefaultDeserializationContext(DefaultDeserializationContext src) {
/*  68 */     super(src);
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
/*     */   public DefaultDeserializationContext copy() {
/*  80 */     throw new IllegalStateException("DefaultDeserializationContext sub-class not overriding copy()");
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
/*     */   public ReadableObjectId findObjectId(Object id, ObjectIdGenerator<?> gen, ObjectIdResolver resolverType) {
/*  94 */     if (id == null) {
/*  95 */       return null;
/*     */     }
/*     */     
/*  98 */     ObjectIdGenerator.IdKey key = gen.key(id);
/*     */     
/* 100 */     if (this._objectIds == null) {
/* 101 */       this._objectIds = new LinkedHashMap<>();
/*     */     } else {
/* 103 */       ReadableObjectId readableObjectId = this._objectIds.get(key);
/* 104 */       if (readableObjectId != null) {
/* 105 */         return readableObjectId;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 110 */     ObjectIdResolver resolver = null;
/*     */     
/* 112 */     if (this._objectIdResolvers == null) {
/* 113 */       this._objectIdResolvers = new ArrayList<>(8);
/*     */     } else {
/* 115 */       for (ObjectIdResolver res : this._objectIdResolvers) {
/* 116 */         if (res.canUseFor(resolverType)) {
/* 117 */           resolver = res;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 123 */     if (resolver == null) {
/* 124 */       resolver = resolverType.newForDeserialization(this);
/* 125 */       this._objectIdResolvers.add(resolver);
/*     */     } 
/*     */     
/* 128 */     ReadableObjectId entry = createReadableObjectId(key);
/* 129 */     entry.setResolver(resolver);
/* 130 */     this._objectIds.put(key, entry);
/* 131 */     return entry;
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
/*     */   protected ReadableObjectId createReadableObjectId(ObjectIdGenerator.IdKey key) {
/* 147 */     return new ReadableObjectId(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkUnresolvedObjectId() throws UnresolvedForwardReference {
/* 153 */     if (this._objectIds == null) {
/*     */       return;
/*     */     }
/*     */     
/* 157 */     if (!isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS)) {
/*     */       return;
/*     */     }
/* 160 */     UnresolvedForwardReference exception = null;
/* 161 */     for (Map.Entry<ObjectIdGenerator.IdKey, ReadableObjectId> entry : this._objectIds.entrySet()) {
/* 162 */       ReadableObjectId roid = entry.getValue();
/* 163 */       if (!roid.hasReferringProperties()) {
/*     */         continue;
/*     */       }
/*     */       
/* 167 */       if (tryToResolveUnresolvedObjectId(roid)) {
/*     */         continue;
/*     */       }
/* 170 */       if (exception == null) {
/* 171 */         exception = new UnresolvedForwardReference(getParser(), "Unresolved forward references for: ");
/*     */       }
/* 173 */       Object key = (roid.getKey()).key;
/* 174 */       for (Iterator<ReadableObjectId.Referring> iterator = roid.referringProperties(); iterator.hasNext(); ) {
/* 175 */         ReadableObjectId.Referring referring = iterator.next();
/* 176 */         exception.addUnresolvedId(key, referring.getBeanType(), referring.getLocation());
/*     */       } 
/*     */     } 
/* 179 */     if (exception != null) {
/* 180 */       throw exception;
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
/*     */   
/*     */   protected boolean tryToResolveUnresolvedObjectId(ReadableObjectId roid) {
/* 196 */     return roid.tryToResolveUnresolved(this);
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
/*     */   public JsonDeserializer<Object> deserializerInstance(Annotated ann, Object deserDef) throws JsonMappingException {
/*     */     JsonDeserializer<?> deser;
/* 210 */     if (deserDef == null) {
/* 211 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 215 */     if (deserDef instanceof JsonDeserializer) {
/* 216 */       deser = (JsonDeserializer)deserDef;
/*     */     }
/*     */     else {
/*     */       
/* 220 */       if (!(deserDef instanceof Class)) {
/* 221 */         throw new IllegalStateException("AnnotationIntrospector returned deserializer definition of type " + deserDef.getClass().getName() + "; expected type JsonDeserializer or Class<JsonDeserializer> instead");
/*     */       }
/* 223 */       Class<?> deserClass = (Class)deserDef;
/*     */       
/* 225 */       if (deserClass == JsonDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
/* 226 */         return null;
/*     */       }
/* 228 */       if (!JsonDeserializer.class.isAssignableFrom(deserClass)) {
/* 229 */         throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<JsonDeserializer>");
/*     */       }
/* 231 */       HandlerInstantiator hi = this._config.getHandlerInstantiator();
/* 232 */       deser = (hi == null) ? null : hi.deserializerInstance(this._config, ann, deserClass);
/* 233 */       if (deser == null) {
/* 234 */         deser = (JsonDeserializer)ClassUtil.createInstance(deserClass, this._config
/* 235 */             .canOverrideAccessModifiers());
/*     */       }
/*     */     } 
/*     */     
/* 239 */     if (deser instanceof ResolvableDeserializer) {
/* 240 */       ((ResolvableDeserializer)deser).resolve(this);
/*     */     }
/* 242 */     return (JsonDeserializer)deser;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final KeyDeserializer keyDeserializerInstance(Annotated ann, Object deserDef) throws JsonMappingException {
/*     */     KeyDeserializer deser;
/* 249 */     if (deserDef == null) {
/* 250 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 255 */     if (deserDef instanceof KeyDeserializer) {
/* 256 */       deser = (KeyDeserializer)deserDef;
/*     */     } else {
/* 258 */       if (!(deserDef instanceof Class)) {
/* 259 */         throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + deserDef
/* 260 */             .getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
/*     */       }
/*     */       
/* 263 */       Class<?> deserClass = (Class)deserDef;
/*     */       
/* 265 */       if (deserClass == KeyDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
/* 266 */         return null;
/*     */       }
/* 268 */       if (!KeyDeserializer.class.isAssignableFrom(deserClass)) {
/* 269 */         throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<KeyDeserializer>");
/*     */       }
/*     */       
/* 272 */       HandlerInstantiator hi = this._config.getHandlerInstantiator();
/* 273 */       deser = (hi == null) ? null : hi.keyDeserializerInstance(this._config, ann, deserClass);
/* 274 */       if (deser == null) {
/* 275 */         deser = (KeyDeserializer)ClassUtil.createInstance(deserClass, this._config
/* 276 */             .canOverrideAccessModifiers());
/*     */       }
/*     */     } 
/*     */     
/* 280 */     if (deser instanceof ResolvableDeserializer) {
/* 281 */       ((ResolvableDeserializer)deser).resolve(this);
/*     */     }
/* 283 */     return deser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract DefaultDeserializationContext with(DeserializerFactory paramDeserializerFactory);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract DefaultDeserializationContext createInstance(DeserializationConfig paramDeserializationConfig, JsonParser paramJsonParser, InjectableValues paramInjectableValues);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract DefaultDeserializationContext createDummyInstance(DeserializationConfig paramDeserializationConfig);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object readRootValue(JsonParser p, JavaType valueType, JsonDeserializer<Object> deser, Object valueToUpdate) throws IOException {
/* 318 */     if (this._config.useRootWrapping()) {
/* 319 */       return _unwrapAndDeserialize(p, valueType, deser, valueToUpdate);
/*     */     }
/* 321 */     if (valueToUpdate == null) {
/* 322 */       return deser.deserialize(p, this);
/*     */     }
/* 324 */     return deser.deserialize(p, this, valueToUpdate);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _unwrapAndDeserialize(JsonParser p, JavaType rootType, JsonDeserializer<Object> deser, Object valueToUpdate) throws IOException {
/*     */     Object result;
/* 332 */     PropertyName expRootName = this._config.findRootName(rootType);
/*     */     
/* 334 */     String expSimpleName = expRootName.getSimpleName();
/* 335 */     if (p.currentToken() != JsonToken.START_OBJECT)
/* 336 */       reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name %s), but %s", new Object[] {
/*     */             
/* 338 */             ClassUtil.name(expSimpleName), p.currentToken()
/*     */           }); 
/* 340 */     if (p.nextToken() != JsonToken.FIELD_NAME)
/* 341 */       reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name %s), but %s", new Object[] {
/*     */             
/* 343 */             ClassUtil.name(expSimpleName), p.currentToken()
/*     */           }); 
/* 345 */     String actualName = p.currentName();
/* 346 */     if (!expSimpleName.equals(actualName)) {
/* 347 */       reportPropertyInputMismatch(rootType, actualName, "Root name (%s) does not match expected (%s) for type %s", new Object[] {
/*     */             
/* 349 */             ClassUtil.name(actualName), ClassUtil.name(expSimpleName), ClassUtil.getTypeDescription(rootType)
/*     */           });
/*     */     }
/* 352 */     p.nextToken();
/*     */     
/* 354 */     if (valueToUpdate == null) {
/* 355 */       result = deser.deserialize(p, this);
/*     */     } else {
/* 357 */       result = deser.deserialize(p, this, valueToUpdate);
/*     */     } 
/*     */     
/* 360 */     if (p.nextToken() != JsonToken.END_OBJECT)
/* 361 */       reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name %s), but %s", new Object[] {
/*     */             
/* 363 */             ClassUtil.name(expSimpleName), p.currentToken()
/*     */           }); 
/* 365 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Impl
/*     */     extends DefaultDeserializationContext
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Impl(DeserializerFactory df) {
/* 386 */       super(df, (DeserializerCache)null);
/*     */     }
/*     */ 
/*     */     
/*     */     private Impl(Impl src, DeserializationConfig config, JsonParser p, InjectableValues values) {
/* 391 */       super(src, config, p, values);
/*     */     }
/*     */     private Impl(Impl src) {
/* 394 */       super(src);
/*     */     }
/*     */     private Impl(Impl src, DeserializerFactory factory) {
/* 397 */       super(src, factory);
/*     */     }
/*     */     
/*     */     private Impl(Impl src, DeserializationConfig config) {
/* 401 */       super(src, config);
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultDeserializationContext copy() {
/* 406 */       ClassUtil.verifyMustOverride(Impl.class, this, "copy");
/* 407 */       return new Impl(this);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public DefaultDeserializationContext createInstance(DeserializationConfig config, JsonParser p, InjectableValues values) {
/* 413 */       return new Impl(this, config, p, values);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public DefaultDeserializationContext createDummyInstance(DeserializationConfig config) {
/* 419 */       return new Impl(this, config);
/*     */     }
/*     */ 
/*     */     
/*     */     public DefaultDeserializationContext with(DeserializerFactory factory) {
/* 424 */       return new Impl(this, factory);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/DefaultDeserializationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */