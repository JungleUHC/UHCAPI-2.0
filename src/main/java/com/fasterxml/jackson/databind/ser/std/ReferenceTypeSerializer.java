/*     */ package com.fasterxml.jackson.databind.ser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*     */ import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.fasterxml.jackson.databind.type.ReferenceType;
/*     */ import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*     */ import com.fasterxml.jackson.databind.util.BeanUtil;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ReferenceTypeSerializer<T>
/*     */   extends StdSerializer<T>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  35 */   public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JavaType _referredType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final BeanProperty _property;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final TypeSerializer _valueTypeSerializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonSerializer<Object> _valueSerializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final NameTransformer _unwrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient PropertySerializerMap _dynamicSerializers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Object _suppressableValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _suppressNulls;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReferenceTypeSerializer(ReferenceType fullType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> ser) {
/* 100 */     super((JavaType)fullType);
/* 101 */     this._referredType = fullType.getReferencedType();
/* 102 */     this._property = null;
/* 103 */     this._valueTypeSerializer = vts;
/* 104 */     this._valueSerializer = ser;
/* 105 */     this._unwrapper = null;
/* 106 */     this._suppressableValue = null;
/* 107 */     this._suppressNulls = false;
/* 108 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ReferenceTypeSerializer(ReferenceTypeSerializer<?> base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, Object suppressableValue, boolean suppressNulls) {
/* 117 */     super(base);
/* 118 */     this._referredType = base._referredType;
/*     */     
/* 120 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/* 121 */     this._property = property;
/* 122 */     this._valueTypeSerializer = vts;
/* 123 */     this._valueSerializer = (JsonSerializer)valueSer;
/* 124 */     this._unwrapper = unwrapper;
/* 125 */     this._suppressableValue = suppressableValue;
/* 126 */     this._suppressNulls = suppressNulls;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<T> unwrappingSerializer(NameTransformer transformer) {
/* 131 */     JsonSerializer<Object> valueSer = this._valueSerializer;
/* 132 */     if (valueSer != null) {
/*     */ 
/*     */       
/* 135 */       valueSer = valueSer.unwrappingSerializer(transformer);
/* 136 */       if (valueSer == this._valueSerializer) {
/* 137 */         return this;
/*     */       }
/*     */     } 
/*     */     
/* 141 */     NameTransformer unwrapper = (this._unwrapper == null) ? transformer : NameTransformer.chainedTransformer(transformer, this._unwrapper);
/* 142 */     if (this._valueSerializer == valueSer && this._unwrapper == unwrapper) {
/* 143 */       return this;
/*     */     }
/* 145 */     return withResolved(this._property, this._valueTypeSerializer, valueSer, unwrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract ReferenceTypeSerializer<T> withResolved(BeanProperty paramBeanProperty, TypeSerializer paramTypeSerializer, JsonSerializer<?> paramJsonSerializer, NameTransformer paramNameTransformer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ReferenceTypeSerializer<T> withContentInclusion(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean _isValuePresent(T paramT);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Object _getReferenced(T paramT);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Object _getReferencedIfPresent(T paramT);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
/*     */     ReferenceTypeSerializer<?> refSer;
/* 201 */     TypeSerializer typeSer = this._valueTypeSerializer;
/* 202 */     if (typeSer != null) {
/* 203 */       typeSer = typeSer.forProperty(property);
/*     */     }
/*     */     
/* 206 */     JsonSerializer<?> ser = findAnnotatedContentSerializer(provider, property);
/* 207 */     if (ser == null) {
/*     */       
/* 209 */       ser = this._valueSerializer;
/* 210 */       if (ser == null) {
/*     */         
/* 212 */         if (_useStatic(provider, property, this._referredType)) {
/* 213 */           ser = _findSerializer(provider, this._referredType, property);
/*     */         }
/*     */       } else {
/* 216 */         ser = provider.handlePrimaryContextualization(ser, property);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 221 */     if (this._property == property && this._valueTypeSerializer == typeSer && this._valueSerializer == ser) {
/*     */       
/* 223 */       refSer = this;
/*     */     } else {
/* 225 */       refSer = withResolved(property, typeSer, ser, this._unwrapper);
/*     */     } 
/*     */ 
/*     */     
/* 229 */     if (property != null) {
/* 230 */       JsonInclude.Value inclV = property.findPropertyInclusion((MapperConfig)provider.getConfig(), handledType());
/* 231 */       if (inclV != null) {
/* 232 */         JsonInclude.Include incl = inclV.getContentInclusion();
/*     */         
/* 234 */         if (incl != JsonInclude.Include.USE_DEFAULTS) {
/*     */           Object valueToSuppress;
/*     */           boolean suppressNulls;
/* 237 */           switch (incl) {
/*     */             case NON_DEFAULT:
/* 239 */               valueToSuppress = BeanUtil.getDefaultValue(this._referredType);
/* 240 */               suppressNulls = true;
/* 241 */               if (valueToSuppress != null && 
/* 242 */                 valueToSuppress.getClass().isArray()) {
/* 243 */                 valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
/*     */               }
/*     */               break;
/*     */             
/*     */             case NON_ABSENT:
/* 248 */               suppressNulls = true;
/* 249 */               valueToSuppress = this._referredType.isReferenceType() ? MARKER_FOR_EMPTY : null;
/*     */               break;
/*     */             case NON_EMPTY:
/* 252 */               suppressNulls = true;
/* 253 */               valueToSuppress = MARKER_FOR_EMPTY;
/*     */               break;
/*     */             case CUSTOM:
/* 256 */               valueToSuppress = provider.includeFilterInstance(null, inclV.getContentFilter());
/* 257 */               if (valueToSuppress == null) {
/* 258 */                 suppressNulls = true; break;
/*     */               } 
/* 260 */               suppressNulls = provider.includeFilterSuppressNulls(valueToSuppress);
/*     */               break;
/*     */             
/*     */             case NON_NULL:
/* 264 */               valueToSuppress = null;
/* 265 */               suppressNulls = true;
/*     */               break;
/*     */             
/*     */             default:
/* 269 */               valueToSuppress = null;
/* 270 */               suppressNulls = false;
/*     */               break;
/*     */           } 
/* 273 */           if (this._suppressableValue != valueToSuppress || this._suppressNulls != suppressNulls)
/*     */           {
/* 275 */             refSer = refSer.withContentInclusion(valueToSuppress, suppressNulls);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 280 */     return refSer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _useStatic(SerializerProvider provider, BeanProperty property, JavaType referredType) {
/* 287 */     if (referredType.isJavaLangObject()) {
/* 288 */       return false;
/*     */     }
/*     */     
/* 291 */     if (referredType.isFinal()) {
/* 292 */       return true;
/*     */     }
/*     */     
/* 295 */     if (referredType.useStaticType()) {
/* 296 */       return true;
/*     */     }
/*     */     
/* 299 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/* 300 */     if (intr != null && property != null) {
/* 301 */       AnnotatedMember annotatedMember = property.getMember();
/* 302 */       if (annotatedMember != null) {
/* 303 */         JsonSerialize.Typing t = intr.findSerializationTyping((Annotated)property.getMember());
/* 304 */         if (t == JsonSerialize.Typing.STATIC) {
/* 305 */           return true;
/*     */         }
/* 307 */         if (t == JsonSerialize.Typing.DYNAMIC) {
/* 308 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 313 */     return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
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
/*     */   public boolean isEmpty(SerializerProvider provider, T value) {
/* 326 */     if (!_isValuePresent(value)) {
/* 327 */       return true;
/*     */     }
/* 329 */     Object contents = _getReferenced(value);
/* 330 */     if (contents == null) {
/* 331 */       return this._suppressNulls;
/*     */     }
/* 333 */     if (this._suppressableValue == null) {
/* 334 */       return false;
/*     */     }
/* 336 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 337 */     if (ser == null) {
/*     */       try {
/* 339 */         ser = _findCachedSerializer(provider, contents.getClass());
/* 340 */       } catch (JsonMappingException e) {
/* 341 */         throw new RuntimeJsonMappingException(e);
/*     */       } 
/*     */     }
/* 344 */     if (this._suppressableValue == MARKER_FOR_EMPTY) {
/* 345 */       return ser.isEmpty(provider, contents);
/*     */     }
/* 347 */     return this._suppressableValue.equals(contents);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUnwrappingSerializer() {
/* 352 */     return (this._unwrapper != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType getReferredType() {
/* 359 */     return this._referredType;
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
/*     */   public void serialize(T ref, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 372 */     Object value = _getReferencedIfPresent(ref);
/* 373 */     if (value == null) {
/* 374 */       if (this._unwrapper == null) {
/* 375 */         provider.defaultSerializeNull(g);
/*     */       }
/*     */       return;
/*     */     } 
/* 379 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 380 */     if (ser == null) {
/* 381 */       ser = _findCachedSerializer(provider, value.getClass());
/*     */     }
/* 383 */     if (this._valueTypeSerializer != null) {
/* 384 */       ser.serializeWithType(value, g, provider, this._valueTypeSerializer);
/*     */     } else {
/* 386 */       ser.serialize(value, g, provider);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeWithType(T ref, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
/* 395 */     Object value = _getReferencedIfPresent(ref);
/* 396 */     if (value == null) {
/* 397 */       if (this._unwrapper == null) {
/* 398 */         provider.defaultSerializeNull(g);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 413 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 414 */     if (ser == null) {
/* 415 */       ser = _findCachedSerializer(provider, value.getClass());
/*     */     }
/* 417 */     ser.serializeWithType(value, g, provider, typeSer);
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
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 430 */     JsonSerializer<?> ser = this._valueSerializer;
/* 431 */     if (ser == null) {
/* 432 */       ser = _findSerializer(visitor.getProvider(), this._referredType, this._property);
/* 433 */       if (this._unwrapper != null) {
/* 434 */         ser = ser.unwrappingSerializer(this._unwrapper);
/*     */       }
/*     */     } 
/* 437 */     ser.acceptJsonFormatVisitor(visitor, this._referredType);
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
/*     */   private final JsonSerializer<Object> _findCachedSerializer(SerializerProvider provider, Class<?> rawType) throws JsonMappingException {
/* 453 */     JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(rawType);
/* 454 */     if (ser == null) {
/*     */ 
/*     */ 
/*     */       
/* 458 */       if (this._referredType.hasGenericTypes()) {
/*     */ 
/*     */         
/* 461 */         JavaType fullType = provider.constructSpecializedType(this._referredType, rawType);
/*     */ 
/*     */ 
/*     */         
/* 465 */         ser = provider.findPrimaryPropertySerializer(fullType, this._property);
/*     */       } else {
/* 467 */         ser = provider.findPrimaryPropertySerializer(rawType, this._property);
/*     */       } 
/* 469 */       if (this._unwrapper != null) {
/* 470 */         ser = ser.unwrappingSerializer(this._unwrapper);
/*     */       }
/* 472 */       this._dynamicSerializers = this._dynamicSerializers.newWith(rawType, ser);
/*     */     } 
/* 474 */     return ser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, JavaType type, BeanProperty prop) throws JsonMappingException {
/* 485 */     return provider.findPrimaryPropertySerializer(type, prop);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/std/ReferenceTypeSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */