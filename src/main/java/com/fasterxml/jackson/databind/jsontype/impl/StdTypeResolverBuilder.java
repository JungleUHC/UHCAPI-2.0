/*     */ package com.fasterxml.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.annotation.NoClass;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*     */ import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StdTypeResolverBuilder
/*     */   implements TypeResolverBuilder<StdTypeResolverBuilder>
/*     */ {
/*     */   protected JsonTypeInfo.Id _idType;
/*     */   protected JsonTypeInfo.As _includeAs;
/*     */   protected String _typeProperty;
/*     */   protected boolean _typeIdVisible = false;
/*     */   protected Class<?> _defaultImpl;
/*     */   protected TypeIdResolver _customIdResolver;
/*     */   
/*     */   protected StdTypeResolverBuilder(JsonTypeInfo.Id idType, JsonTypeInfo.As idAs, String propName) {
/*  58 */     this._idType = idType;
/*  59 */     this._includeAs = idAs;
/*  60 */     this._typeProperty = propName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StdTypeResolverBuilder(StdTypeResolverBuilder base, Class<?> defaultImpl) {
/*  71 */     this._idType = base._idType;
/*  72 */     this._includeAs = base._includeAs;
/*  73 */     this._typeProperty = base._typeProperty;
/*  74 */     this._typeIdVisible = base._typeIdVisible;
/*  75 */     this._customIdResolver = base._customIdResolver;
/*     */     
/*  77 */     this._defaultImpl = defaultImpl;
/*     */   }
/*     */   
/*     */   public static StdTypeResolverBuilder noTypeInfoBuilder() {
/*  81 */     return (new StdTypeResolverBuilder()).init(JsonTypeInfo.Id.NONE, (TypeIdResolver)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdTypeResolverBuilder init(JsonTypeInfo.Id idType, TypeIdResolver idRes) {
/*  88 */     if (idType == null) {
/*  89 */       throw new IllegalArgumentException("idType cannot be null");
/*     */     }
/*  91 */     this._idType = idType;
/*  92 */     this._customIdResolver = idRes;
/*     */     
/*  94 */     this._typeProperty = idType.getDefaultPropertyName();
/*  95 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
/* 102 */     if (this._idType == JsonTypeInfo.Id.NONE) return null;
/*     */ 
/*     */     
/* 105 */     if (baseType.isPrimitive())
/*     */     {
/* 107 */       if (!allowPrimitiveTypes((MapperConfig<?>)config, baseType)) {
/* 108 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 112 */     TypeIdResolver idRes = idResolver((MapperConfig<?>)config, baseType, subTypeValidator((MapperConfig<?>)config), subtypes, true, false);
/*     */ 
/*     */     
/* 115 */     if (this._idType == JsonTypeInfo.Id.DEDUCTION)
/*     */     {
/* 117 */       return new AsExistingPropertyTypeSerializer(idRes, null, this._typeProperty);
/*     */     }
/*     */     
/* 120 */     switch (this._includeAs) {
/*     */       case DEDUCTION:
/* 122 */         return new AsArrayTypeSerializer(idRes, null);
/*     */       case CLASS:
/* 124 */         return new AsPropertyTypeSerializer(idRes, null, this._typeProperty);
/*     */       case MINIMAL_CLASS:
/* 126 */         return new AsWrapperTypeSerializer(idRes, null);
/*     */       case NAME:
/* 128 */         return new AsExternalTypeSerializer(idRes, null, this._typeProperty);
/*     */       
/*     */       case NONE:
/* 131 */         return new AsExistingPropertyTypeSerializer(idRes, null, this._typeProperty);
/*     */     } 
/* 133 */     throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
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
/*     */   public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
/* 146 */     if (this._idType == JsonTypeInfo.Id.NONE) return null;
/*     */ 
/*     */     
/* 149 */     if (baseType.isPrimitive())
/*     */     {
/* 151 */       if (!allowPrimitiveTypes((MapperConfig<?>)config, baseType)) {
/* 152 */         return null;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 158 */     PolymorphicTypeValidator subTypeValidator = verifyBaseTypeValidity((MapperConfig<?>)config, baseType);
/*     */     
/* 160 */     TypeIdResolver idRes = idResolver((MapperConfig<?>)config, baseType, subTypeValidator, subtypes, false, true);
/*     */     
/* 162 */     JavaType defaultImpl = defineDefaultImpl(config, baseType);
/*     */     
/* 164 */     if (this._idType == JsonTypeInfo.Id.DEDUCTION)
/*     */     {
/* 166 */       return new AsDeductionTypeDeserializer(baseType, idRes, defaultImpl, config, subtypes);
/*     */     }
/*     */ 
/*     */     
/* 170 */     switch (this._includeAs) {
/*     */       case DEDUCTION:
/* 172 */         return new AsArrayTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
/*     */       
/*     */       case CLASS:
/*     */       case NONE:
/* 176 */         return new AsPropertyTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl, this._includeAs);
/*     */       
/*     */       case MINIMAL_CLASS:
/* 179 */         return new AsWrapperTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
/*     */       
/*     */       case NAME:
/* 182 */         return new AsExternalTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
/*     */     } 
/*     */     
/* 185 */     throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
/*     */   }
/*     */ 
/*     */   
/*     */   protected JavaType defineDefaultImpl(DeserializationConfig config, JavaType baseType) {
/* 190 */     if (this._defaultImpl != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 197 */       if (this._defaultImpl == Void.class || this._defaultImpl == NoClass.class)
/*     */       {
/*     */         
/* 200 */         return config.getTypeFactory().constructType(this._defaultImpl);
/*     */       }
/* 202 */       if (baseType.hasRawClass(this._defaultImpl)) {
/* 203 */         return baseType;
/*     */       }
/* 205 */       if (baseType.isTypeOrSuperTypeOf(this._defaultImpl))
/*     */       {
/* 207 */         return config.getTypeFactory()
/* 208 */           .constructSpecializedType(baseType, this._defaultImpl);
/*     */       }
/* 210 */       if (baseType.hasRawClass(this._defaultImpl)) {
/* 211 */         return baseType;
/*     */       }
/*     */     } 
/*     */     
/* 215 */     if (config.isEnabled(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL) && 
/* 216 */       !baseType.isAbstract())
/*     */     {
/* 218 */       return baseType;
/*     */     }
/* 220 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdTypeResolverBuilder inclusion(JsonTypeInfo.As includeAs) {
/* 231 */     if (includeAs == null) {
/* 232 */       throw new IllegalArgumentException("includeAs cannot be null");
/*     */     }
/* 234 */     this._includeAs = includeAs;
/* 235 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
/* 245 */     if (typeIdPropName == null || typeIdPropName.isEmpty()) {
/* 246 */       typeIdPropName = this._idType.getDefaultPropertyName();
/*     */     }
/* 248 */     this._typeProperty = typeIdPropName;
/* 249 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public StdTypeResolverBuilder defaultImpl(Class<?> defaultImpl) {
/* 254 */     this._defaultImpl = defaultImpl;
/* 255 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public StdTypeResolverBuilder typeIdVisibility(boolean isVisible) {
/* 260 */     this._typeIdVisible = isVisible;
/* 261 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public StdTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
/* 266 */     if (this._defaultImpl == defaultImpl) {
/* 267 */       return this;
/*     */     }
/* 269 */     ClassUtil.verifyMustOverride(StdTypeResolverBuilder.class, this, "withDefaultImpl");
/*     */ 
/*     */     
/* 272 */     return new StdTypeResolverBuilder(this, defaultImpl);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getDefaultImpl() {
/* 281 */     return this._defaultImpl;
/*     */   }
/* 283 */   public String getTypeProperty() { return this._typeProperty; } public boolean isTypeIdVisible() {
/* 284 */     return this._typeIdVisible;
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
/*     */   protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator subtypeValidator, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
/* 302 */     if (this._customIdResolver != null) return this._customIdResolver; 
/* 303 */     if (this._idType == null) throw new IllegalStateException("Cannot build, 'init()' not yet called"); 
/* 304 */     switch (this._idType) {
/*     */       case DEDUCTION:
/*     */       case CLASS:
/* 307 */         return ClassNameIdResolver.construct(baseType, config, subtypeValidator);
/*     */       case MINIMAL_CLASS:
/* 309 */         return MinimalClassNameIdResolver.construct(baseType, config, subtypeValidator);
/*     */       case NAME:
/* 311 */         return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
/*     */       case NONE:
/* 313 */         return null;
/*     */     } 
/*     */     
/* 316 */     throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + this._idType);
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
/*     */   public PolymorphicTypeValidator subTypeValidator(MapperConfig<?> config) {
/* 335 */     return config.getPolymorphicTypeValidator();
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
/*     */   protected PolymorphicTypeValidator verifyBaseTypeValidity(MapperConfig<?> config, JavaType baseType) {
/* 348 */     PolymorphicTypeValidator ptv = subTypeValidator(config);
/* 349 */     if (this._idType == JsonTypeInfo.Id.CLASS || this._idType == JsonTypeInfo.Id.MINIMAL_CLASS) {
/* 350 */       PolymorphicTypeValidator.Validity validity = ptv.validateBaseType(config, baseType);
/*     */       
/* 352 */       if (validity == PolymorphicTypeValidator.Validity.DENIED) {
/* 353 */         return reportInvalidBaseType(config, baseType, ptv);
/*     */       }
/*     */       
/* 356 */       if (validity == PolymorphicTypeValidator.Validity.ALLOWED) {
/* 357 */         return (PolymorphicTypeValidator)LaissezFaireSubTypeValidator.instance;
/*     */       }
/*     */     } 
/*     */     
/* 361 */     return ptv;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected PolymorphicTypeValidator reportInvalidBaseType(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator ptv) {
/* 370 */     throw new IllegalArgumentException(String.format("Configured `PolymorphicTypeValidator` (of type %s) denied resolution of all subtypes of base type %s", new Object[] {
/*     */             
/* 372 */             ClassUtil.classNameOf(ptv), ClassUtil.classNameOf(baseType.getRawClass())
/*     */           }));
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
/*     */   protected boolean allowPrimitiveTypes(MapperConfig<?> config, JavaType baseType) {
/* 400 */     return false;
/*     */   }
/*     */   
/*     */   public StdTypeResolverBuilder() {}
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/jsontype/impl/StdTypeResolverBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */