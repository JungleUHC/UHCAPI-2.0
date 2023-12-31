/*     */ package com.fasterxml.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DatabindContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public abstract class TypeDeserializerBase
/*     */   extends TypeDeserializer
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final TypeIdResolver _idResolver;
/*     */   protected final JavaType _baseType;
/*     */   protected final BeanProperty _property;
/*     */   protected final JavaType _defaultImpl;
/*     */   protected final String _typePropertyName;
/*     */   protected final boolean _typeIdVisible;
/*     */   protected final Map<String, JsonDeserializer<Object>> _deserializers;
/*     */   protected JsonDeserializer<Object> _defaultImplDeserializer;
/*     */   
/*     */   protected TypeDeserializerBase(JavaType baseType, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
/*  73 */     this._baseType = baseType;
/*  74 */     this._idResolver = idRes;
/*  75 */     this._typePropertyName = ClassUtil.nonNullString(typePropertyName);
/*  76 */     this._typeIdVisible = typeIdVisible;
/*     */     
/*  78 */     this._deserializers = new ConcurrentHashMap<>(16, 0.75F, 2);
/*  79 */     this._defaultImpl = defaultImpl;
/*  80 */     this._property = null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected TypeDeserializerBase(TypeDeserializerBase src, BeanProperty property) {
/*  85 */     this._baseType = src._baseType;
/*  86 */     this._idResolver = src._idResolver;
/*  87 */     this._typePropertyName = src._typePropertyName;
/*  88 */     this._typeIdVisible = src._typeIdVisible;
/*  89 */     this._deserializers = src._deserializers;
/*  90 */     this._defaultImpl = src._defaultImpl;
/*  91 */     this._defaultImplDeserializer = src._defaultImplDeserializer;
/*  92 */     this._property = property;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract TypeDeserializer forProperty(BeanProperty paramBeanProperty);
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract JsonTypeInfo.As getTypeInclusion();
/*     */ 
/*     */ 
/*     */   
/*     */   public String baseTypeName() {
/* 107 */     return this._baseType.getRawClass().getName();
/*     */   }
/*     */   public final String getPropertyName() {
/* 110 */     return this._typePropertyName;
/*     */   }
/*     */   public TypeIdResolver getTypeIdResolver() {
/* 113 */     return this._idResolver;
/*     */   }
/*     */   
/*     */   public Class<?> getDefaultImpl() {
/* 117 */     return ClassUtil.rawClass(this._defaultImpl);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasDefaultImpl() {
/* 122 */     return (this._defaultImpl != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType baseType() {
/* 129 */     return this._baseType;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 135 */     StringBuilder sb = new StringBuilder();
/* 136 */     sb.append('[').append(getClass().getName());
/* 137 */     sb.append("; base-type:").append(this._baseType);
/* 138 */     sb.append("; id-resolver: ").append(this._idResolver);
/* 139 */     sb.append(']');
/* 140 */     return sb.toString();
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
/*     */   protected final JsonDeserializer<Object> _findDeserializer(DeserializationContext ctxt, String typeId) throws IOException {
/* 152 */     JsonDeserializer<Object> deser = this._deserializers.get(typeId);
/* 153 */     if (deser == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 159 */       JavaType type = this._idResolver.typeFromId((DatabindContext)ctxt, typeId);
/* 160 */       if (type == null) {
/*     */         
/* 162 */         deser = _findDefaultImplDeserializer(ctxt);
/* 163 */         if (deser == null)
/*     */         {
/* 165 */           JavaType actual = _handleUnknownTypeId(ctxt, typeId);
/* 166 */           if (actual == null)
/*     */           {
/* 168 */             return (JsonDeserializer<Object>)NullifyingDeserializer.instance;
/*     */           }
/*     */           
/* 171 */           deser = ctxt.findContextualValueDeserializer(actual, this._property);
/*     */ 
/*     */         
/*     */         }
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 182 */         if (this._baseType != null && this._baseType
/* 183 */           .getClass() == type.getClass())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 193 */           if (!type.hasGenericTypes()) {
/*     */             try {
/* 195 */               type = ctxt.constructSpecializedType(this._baseType, type.getRawClass());
/* 196 */             } catch (IllegalArgumentException e) {
/*     */ 
/*     */               
/* 199 */               throw ctxt.invalidTypeIdException(this._baseType, typeId, e.getMessage());
/*     */             } 
/*     */           }
/*     */         }
/* 203 */         deser = ctxt.findContextualValueDeserializer(type, this._property);
/*     */       } 
/* 205 */       this._deserializers.put(typeId, deser);
/*     */     } 
/* 207 */     return deser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonDeserializer<Object> _findDefaultImplDeserializer(DeserializationContext ctxt) throws IOException {
/* 215 */     if (this._defaultImpl == null) {
/* 216 */       if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)) {
/* 217 */         return (JsonDeserializer<Object>)NullifyingDeserializer.instance;
/*     */       }
/* 219 */       return null;
/*     */     } 
/* 221 */     Class<?> raw = this._defaultImpl.getRawClass();
/* 222 */     if (ClassUtil.isBogusClass(raw)) {
/* 223 */       return (JsonDeserializer<Object>)NullifyingDeserializer.instance;
/*     */     }
/*     */     
/* 226 */     synchronized (this._defaultImpl) {
/* 227 */       if (this._defaultImplDeserializer == null) {
/* 228 */         this._defaultImplDeserializer = ctxt.findContextualValueDeserializer(this._defaultImpl, this._property);
/*     */       }
/*     */       
/* 231 */       return this._defaultImplDeserializer;
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
/*     */   @Deprecated
/*     */   protected Object _deserializeWithNativeTypeId(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 244 */     return _deserializeWithNativeTypeId(jp, ctxt, jp.getTypeId());
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
/*     */   protected Object _deserializeWithNativeTypeId(JsonParser p, DeserializationContext ctxt, Object typeId) throws IOException {
/*     */     JsonDeserializer<Object> deser;
/* 257 */     if (typeId == null) {
/*     */ 
/*     */       
/* 260 */       deser = _findDefaultImplDeserializer(ctxt);
/* 261 */       if (deser == null) {
/* 262 */         return ctxt.reportInputMismatch(baseType(), "No (native) type id found when one was expected for polymorphic type handling", new Object[0]);
/*     */       }
/*     */     } else {
/*     */       
/* 266 */       String typeIdStr = (typeId instanceof String) ? (String)typeId : String.valueOf(typeId);
/* 267 */       deser = _findDeserializer(ctxt, typeIdStr);
/*     */     } 
/* 269 */     return deser.deserialize(p, ctxt);
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
/*     */   protected JavaType _handleUnknownTypeId(DeserializationContext ctxt, String typeId) throws IOException {
/* 288 */     String extraDesc = this._idResolver.getDescForKnownTypeIds();
/* 289 */     if (extraDesc == null) {
/* 290 */       extraDesc = "type ids are not statically known";
/*     */     } else {
/* 292 */       extraDesc = "known type ids = " + extraDesc;
/*     */     } 
/* 294 */     if (this._property != null) {
/* 295 */       extraDesc = String.format("%s (for POJO property '%s')", new Object[] { extraDesc, this._property
/* 296 */             .getName() });
/*     */     }
/* 298 */     return ctxt.handleUnknownTypeId(this._baseType, typeId, this._idResolver, extraDesc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JavaType _handleMissingTypeId(DeserializationContext ctxt, String extraDesc) throws IOException {
/* 307 */     return ctxt.handleMissingTypeId(this._baseType, this._idResolver, extraDesc);
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/impl/TypeDeserializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */