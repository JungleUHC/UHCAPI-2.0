/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JacksonInject;
/*     */ import com.fasterxml.jackson.annotation.JsonCreator;
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.annotation.JsonIncludeProperties;
/*     */ import com.fasterxml.jackson.annotation.JsonProperty;
/*     */ import com.fasterxml.jackson.annotation.JsonSetter;
/*     */ import com.fasterxml.jackson.core.Version;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.KeyDeserializer;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
/*     */ import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AnnotationIntrospectorPair
/*     */   extends AnnotationIntrospector
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final AnnotationIntrospector _primary;
/*     */   protected final AnnotationIntrospector _secondary;
/*     */   
/*     */   public AnnotationIntrospectorPair(AnnotationIntrospector p, AnnotationIntrospector s) {
/*  48 */     this._primary = p;
/*  49 */     this._secondary = s;
/*     */   }
/*     */ 
/*     */   
/*     */   public Version version() {
/*  54 */     return this._primary.version();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AnnotationIntrospector create(AnnotationIntrospector primary, AnnotationIntrospector secondary) {
/*  65 */     if (primary == null) {
/*  66 */       return secondary;
/*     */     }
/*  68 */     if (secondary == null) {
/*  69 */       return primary;
/*     */     }
/*  71 */     return new AnnotationIntrospectorPair(primary, secondary);
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<AnnotationIntrospector> allIntrospectors() {
/*  76 */     return allIntrospectors(new ArrayList<>());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
/*  82 */     this._primary.allIntrospectors(result);
/*  83 */     this._secondary.allIntrospectors(result);
/*  84 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAnnotationBundle(Annotation ann) {
/*  91 */     return (this._primary.isAnnotationBundle(ann) || this._secondary.isAnnotationBundle(ann));
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
/*     */   public PropertyName findRootName(AnnotatedClass ac) {
/* 103 */     PropertyName name1 = this._primary.findRootName(ac);
/* 104 */     if (name1 == null) {
/* 105 */       return this._secondary.findRootName(ac);
/*     */     }
/* 107 */     if (name1.hasSimpleName()) {
/* 108 */       return name1;
/*     */     }
/*     */     
/* 111 */     PropertyName name2 = this._secondary.findRootName(ac);
/* 112 */     return (name2 == null) ? name1 : name2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonIgnoreProperties.Value findPropertyIgnoralByName(MapperConfig<?> config, Annotated ann) {
/* 119 */     JsonIgnoreProperties.Value v2 = this._secondary.findPropertyIgnoralByName(config, ann);
/* 120 */     JsonIgnoreProperties.Value v1 = this._primary.findPropertyIgnoralByName(config, ann);
/* 121 */     return (v2 == null) ? v1 : v2
/* 122 */       .withOverrides(v1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonIncludeProperties.Value findPropertyInclusionByName(MapperConfig<?> config, Annotated a) {
/* 128 */     JsonIncludeProperties.Value v2 = this._secondary.findPropertyInclusionByName(config, a);
/* 129 */     JsonIncludeProperties.Value v1 = this._primary.findPropertyInclusionByName(config, a);
/* 130 */     return (v2 == null) ? v1 : v2
/* 131 */       .withOverrides(v1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean isIgnorableType(AnnotatedClass ac) {
/* 137 */     Boolean result = this._primary.isIgnorableType(ac);
/* 138 */     if (result == null) {
/* 139 */       result = this._secondary.isIgnorableType(ac);
/*     */     }
/* 141 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findFilterId(Annotated ann) {
/* 147 */     Object id = this._primary.findFilterId(ann);
/* 148 */     if (id == null) {
/* 149 */       id = this._secondary.findFilterId(ann);
/*     */     }
/* 151 */     return id;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findNamingStrategy(AnnotatedClass ac) {
/* 157 */     Object str = this._primary.findNamingStrategy(ac);
/* 158 */     if (str == null) {
/* 159 */       str = this._secondary.findNamingStrategy(ac);
/*     */     }
/* 161 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public String findClassDescription(AnnotatedClass ac) {
/* 166 */     String str = this._primary.findClassDescription(ac);
/* 167 */     if (str == null || str.isEmpty()) {
/* 168 */       str = this._secondary.findClassDescription(ac);
/*     */     }
/* 170 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String[] findPropertiesToIgnore(Annotated ac, boolean forSerialization) {
/* 176 */     String[] result = this._primary.findPropertiesToIgnore(ac, forSerialization);
/* 177 */     if (result == null) {
/* 178 */       result = this._secondary.findPropertiesToIgnore(ac, forSerialization);
/*     */     }
/* 180 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
/* 187 */     Boolean result = this._primary.findIgnoreUnknownProperties(ac);
/* 188 */     if (result == null) {
/* 189 */       result = this._secondary.findIgnoreUnknownProperties(ac);
/*     */     }
/* 191 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated a) {
/* 198 */     JsonIgnoreProperties.Value v2 = this._secondary.findPropertyIgnorals(a);
/* 199 */     JsonIgnoreProperties.Value v1 = this._primary.findPropertyIgnorals(a);
/* 200 */     return (v2 == null) ? v1 : v2
/* 201 */       .withOverrides(v1);
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
/*     */   public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
/* 217 */     checker = this._secondary.findAutoDetectVisibility(ac, checker);
/* 218 */     return this._primary.findAutoDetectVisibility(ac, checker);
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
/*     */   public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
/* 231 */     TypeResolverBuilder<?> b = this._primary.findTypeResolver(config, ac, baseType);
/* 232 */     if (b == null) {
/* 233 */       b = this._secondary.findTypeResolver(config, ac, baseType);
/*     */     }
/* 235 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
/* 242 */     TypeResolverBuilder<?> b = this._primary.findPropertyTypeResolver(config, am, baseType);
/* 243 */     if (b == null) {
/* 244 */       b = this._secondary.findPropertyTypeResolver(config, am, baseType);
/*     */     }
/* 246 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
/* 253 */     TypeResolverBuilder<?> b = this._primary.findPropertyContentTypeResolver(config, am, baseType);
/* 254 */     if (b == null) {
/* 255 */       b = this._secondary.findPropertyContentTypeResolver(config, am, baseType);
/*     */     }
/* 257 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<NamedType> findSubtypes(Annotated a) {
/* 263 */     List<NamedType> types1 = this._primary.findSubtypes(a);
/* 264 */     List<NamedType> types2 = this._secondary.findSubtypes(a);
/* 265 */     if (types1 == null || types1.isEmpty()) return types2; 
/* 266 */     if (types2 == null || types2.isEmpty()) return types1; 
/* 267 */     ArrayList<NamedType> result = new ArrayList<>(types1.size() + types2.size());
/* 268 */     result.addAll(types1);
/* 269 */     result.addAll(types2);
/* 270 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String findTypeName(AnnotatedClass ac) {
/* 276 */     String name = this._primary.findTypeName(ac);
/* 277 */     if (name == null || name.isEmpty()) {
/* 278 */       name = this._secondary.findTypeName(ac);
/*     */     }
/* 280 */     return name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
/* 290 */     AnnotationIntrospector.ReferenceProperty r = this._primary.findReferenceType(member);
/* 291 */     return (r == null) ? this._secondary.findReferenceType(member) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
/* 296 */     NameTransformer r = this._primary.findUnwrappingNameTransformer(member);
/* 297 */     return (r == null) ? this._secondary.findUnwrappingNameTransformer(member) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   public JacksonInject.Value findInjectableValue(AnnotatedMember m) {
/* 302 */     JacksonInject.Value r = this._primary.findInjectableValue(m);
/* 303 */     if (r == null || r.getUseInput() == null) {
/* 304 */       JacksonInject.Value secondary = this._secondary.findInjectableValue(m);
/* 305 */       if (secondary != null) {
/* 306 */         r = (r == null) ? secondary : r.withUseInput(secondary.getUseInput());
/*     */       }
/*     */     } 
/* 309 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasIgnoreMarker(AnnotatedMember m) {
/* 314 */     return (this._primary.hasIgnoreMarker(m) || this._secondary.hasIgnoreMarker(m));
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean hasRequiredMarker(AnnotatedMember m) {
/* 319 */     Boolean r = this._primary.hasRequiredMarker(m);
/* 320 */     return (r == null) ? this._secondary.hasRequiredMarker(m) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Object findInjectableValueId(AnnotatedMember m) {
/* 326 */     Object r = this._primary.findInjectableValueId(m);
/* 327 */     return (r == null) ? this._secondary.findInjectableValueId(m) : r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findSerializer(Annotated am) {
/* 334 */     Object r = this._primary.findSerializer(am);
/* 335 */     if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
/* 336 */       return r;
/*     */     }
/* 338 */     return _explicitClassOrOb(this._secondary.findSerializer(am), JsonSerializer.None.class);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findKeySerializer(Annotated a) {
/* 344 */     Object r = this._primary.findKeySerializer(a);
/* 345 */     if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
/* 346 */       return r;
/*     */     }
/* 348 */     return _explicitClassOrOb(this._secondary.findKeySerializer(a), JsonSerializer.None.class);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findContentSerializer(Annotated a) {
/* 354 */     Object r = this._primary.findContentSerializer(a);
/* 355 */     if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
/* 356 */       return r;
/*     */     }
/* 358 */     return _explicitClassOrOb(this._secondary.findContentSerializer(a), JsonSerializer.None.class);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findNullSerializer(Annotated a) {
/* 364 */     Object r = this._primary.findNullSerializer(a);
/* 365 */     if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
/* 366 */       return r;
/*     */     }
/* 368 */     return _explicitClassOrOb(this._secondary.findNullSerializer(a), JsonSerializer.None.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonInclude.Include findSerializationInclusion(Annotated a, JsonInclude.Include defValue) {
/* 378 */     defValue = this._secondary.findSerializationInclusion(a, defValue);
/* 379 */     defValue = this._primary.findSerializationInclusion(a, defValue);
/* 380 */     return defValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonInclude.Include findSerializationInclusionForContent(Annotated a, JsonInclude.Include defValue) {
/* 388 */     defValue = this._secondary.findSerializationInclusionForContent(a, defValue);
/* 389 */     defValue = this._primary.findSerializationInclusionForContent(a, defValue);
/* 390 */     return defValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonInclude.Value findPropertyInclusion(Annotated a) {
/* 396 */     JsonInclude.Value v2 = this._secondary.findPropertyInclusion(a);
/* 397 */     JsonInclude.Value v1 = this._primary.findPropertyInclusion(a);
/*     */     
/* 399 */     if (v2 == null) {
/* 400 */       return v1;
/*     */     }
/* 402 */     return v2.withOverrides(v1);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerialize.Typing findSerializationTyping(Annotated a) {
/* 407 */     JsonSerialize.Typing r = this._primary.findSerializationTyping(a);
/* 408 */     return (r == null) ? this._secondary.findSerializationTyping(a) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object findSerializationConverter(Annotated a) {
/* 413 */     Object r = this._primary.findSerializationConverter(a);
/* 414 */     return (r == null) ? this._secondary.findSerializationConverter(a) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object findSerializationContentConverter(AnnotatedMember a) {
/* 419 */     Object r = this._primary.findSerializationContentConverter(a);
/* 420 */     return (r == null) ? this._secondary.findSerializationContentConverter(a) : r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?>[] findViews(Annotated a) {
/* 429 */     Class<?>[] result = this._primary.findViews(a);
/* 430 */     if (result == null) {
/* 431 */       result = this._secondary.findViews(a);
/*     */     }
/* 433 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean isTypeId(AnnotatedMember member) {
/* 438 */     Boolean b = this._primary.isTypeId(member);
/* 439 */     return (b == null) ? this._secondary.isTypeId(member) : b;
/*     */   }
/*     */ 
/*     */   
/*     */   public ObjectIdInfo findObjectIdInfo(Annotated ann) {
/* 444 */     ObjectIdInfo r = this._primary.findObjectIdInfo(ann);
/* 445 */     return (r == null) ? this._secondary.findObjectIdInfo(ann) : r;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
/* 451 */     objectIdInfo = this._secondary.findObjectReferenceInfo(ann, objectIdInfo);
/* 452 */     objectIdInfo = this._primary.findObjectReferenceInfo(ann, objectIdInfo);
/* 453 */     return objectIdInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonFormat.Value findFormat(Annotated ann) {
/* 458 */     JsonFormat.Value v1 = this._primary.findFormat(ann);
/* 459 */     JsonFormat.Value v2 = this._secondary.findFormat(ann);
/* 460 */     if (v2 == null) {
/* 461 */       return v1;
/*     */     }
/* 463 */     return v2.withOverrides(v1);
/*     */   }
/*     */ 
/*     */   
/*     */   public PropertyName findWrapperName(Annotated ann) {
/* 468 */     PropertyName name = this._primary.findWrapperName(ann);
/* 469 */     if (name == null) {
/* 470 */       name = this._secondary.findWrapperName(ann);
/* 471 */     } else if (name == PropertyName.USE_DEFAULT) {
/*     */       
/* 473 */       PropertyName name2 = this._secondary.findWrapperName(ann);
/* 474 */       if (name2 != null) {
/* 475 */         name = name2;
/*     */       }
/*     */     } 
/* 478 */     return name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String findPropertyDefaultValue(Annotated ann) {
/* 483 */     String str = this._primary.findPropertyDefaultValue(ann);
/* 484 */     return (str == null || str.isEmpty()) ? this._secondary.findPropertyDefaultValue(ann) : str;
/*     */   }
/*     */ 
/*     */   
/*     */   public String findPropertyDescription(Annotated ann) {
/* 489 */     String r = this._primary.findPropertyDescription(ann);
/* 490 */     return (r == null) ? this._secondary.findPropertyDescription(ann) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer findPropertyIndex(Annotated ann) {
/* 495 */     Integer r = this._primary.findPropertyIndex(ann);
/* 496 */     return (r == null) ? this._secondary.findPropertyIndex(ann) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   public String findImplicitPropertyName(AnnotatedMember ann) {
/* 501 */     String r = this._primary.findImplicitPropertyName(ann);
/* 502 */     return (r == null) ? this._secondary.findImplicitPropertyName(ann) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PropertyName> findPropertyAliases(Annotated ann) {
/* 507 */     List<PropertyName> r = this._primary.findPropertyAliases(ann);
/* 508 */     return (r == null) ? this._secondary.findPropertyAliases(ann) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonProperty.Access findPropertyAccess(Annotated ann) {
/* 513 */     JsonProperty.Access acc = this._primary.findPropertyAccess(ann);
/* 514 */     if (acc != null && acc != JsonProperty.Access.AUTO) {
/* 515 */       return acc;
/*     */     }
/* 517 */     acc = this._secondary.findPropertyAccess(ann);
/* 518 */     if (acc != null) {
/* 519 */       return acc;
/*     */     }
/* 521 */     return JsonProperty.Access.AUTO;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
/* 528 */     AnnotatedMethod res = this._primary.resolveSetterConflict(config, setter1, setter2);
/* 529 */     if (res == null) {
/* 530 */       res = this._secondary.resolveSetterConflict(config, setter1, setter2);
/*     */     }
/* 532 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertyName findRenameByField(MapperConfig<?> config, AnnotatedField f, PropertyName implName) {
/* 538 */     PropertyName n = this._secondary.findRenameByField(config, f, implName);
/* 539 */     if (n == null) {
/* 540 */       n = this._primary.findRenameByField(config, f, implName);
/*     */     }
/* 542 */     return n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
/* 551 */     JavaType t = this._secondary.refineSerializationType(config, a, baseType);
/* 552 */     return this._primary.refineSerializationType(config, a, t);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Class<?> findSerializationType(Annotated a) {
/* 558 */     Class<?> r = this._primary.findSerializationType(a);
/* 559 */     return (r == null) ? this._secondary.findSerializationType(a) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
/* 565 */     Class<?> r = this._primary.findSerializationKeyType(am, baseType);
/* 566 */     return (r == null) ? this._secondary.findSerializationKeyType(am, baseType) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
/* 572 */     Class<?> r = this._primary.findSerializationContentType(am, baseType);
/* 573 */     return (r == null) ? this._secondary.findSerializationContentType(am, baseType) : r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
/* 580 */     String[] r = this._primary.findSerializationPropertyOrder(ac);
/* 581 */     return (r == null) ? this._secondary.findSerializationPropertyOrder(ac) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean findSerializationSortAlphabetically(Annotated ann) {
/* 586 */     Boolean r = this._primary.findSerializationSortAlphabetically(ann);
/* 587 */     return (r == null) ? this._secondary.findSerializationSortAlphabetically(ann) : r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
/* 594 */     this._primary.findAndAddVirtualProperties(config, ac, properties);
/* 595 */     this._secondary.findAndAddVirtualProperties(config, ac, properties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertyName findNameForSerialization(Annotated a) {
/* 602 */     PropertyName n = this._primary.findNameForSerialization(a);
/*     */     
/* 604 */     if (n == null) {
/* 605 */       n = this._secondary.findNameForSerialization(a);
/* 606 */     } else if (n == PropertyName.USE_DEFAULT) {
/* 607 */       PropertyName n2 = this._secondary.findNameForSerialization(a);
/* 608 */       if (n2 != null) {
/* 609 */         n = n2;
/*     */       }
/*     */     } 
/* 612 */     return n;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean hasAsKey(MapperConfig<?> config, Annotated a) {
/* 617 */     Boolean b = this._primary.hasAsKey(config, a);
/* 618 */     if (b == null) {
/* 619 */       b = this._secondary.hasAsKey(config, a);
/*     */     }
/* 621 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean hasAsValue(Annotated a) {
/* 626 */     Boolean b = this._primary.hasAsValue(a);
/* 627 */     if (b == null) {
/* 628 */       b = this._secondary.hasAsValue(a);
/*     */     }
/* 630 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean hasAnyGetter(Annotated a) {
/* 635 */     Boolean b = this._primary.hasAnyGetter(a);
/* 636 */     if (b == null) {
/* 637 */       b = this._secondary.hasAnyGetter(a);
/*     */     }
/* 639 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
/* 645 */     names = this._secondary.findEnumValues(enumType, (Enum[])enumValues, names);
/* 646 */     names = this._primary.findEnumValues(enumType, (Enum[])enumValues, names);
/* 647 */     return names;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void findEnumAliases(Class<?> enumType, Enum<?>[] enumValues, String[][] aliases) {
/* 653 */     this._secondary.findEnumAliases(enumType, (Enum[])enumValues, aliases);
/* 654 */     this._primary.findEnumAliases(enumType, (Enum[])enumValues, aliases);
/*     */   }
/*     */ 
/*     */   
/*     */   public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
/* 659 */     Enum<?> en = this._primary.findDefaultEnumValue(enumCls);
/* 660 */     return (en == null) ? this._secondary.findDefaultEnumValue(enumCls) : en;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String findEnumValue(Enum<?> value) {
/* 666 */     String r = this._primary.findEnumValue(value);
/* 667 */     return (r == null) ? this._secondary.findEnumValue(value) : r;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean hasAsValueAnnotation(AnnotatedMethod am) {
/* 673 */     return (this._primary.hasAsValueAnnotation(am) || this._secondary.hasAsValueAnnotation(am));
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
/* 679 */     return (this._primary.hasAnyGetterAnnotation(am) || this._secondary.hasAnyGetterAnnotation(am));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findDeserializer(Annotated a) {
/* 686 */     Object r = this._primary.findDeserializer(a);
/* 687 */     if (_isExplicitClassOrOb(r, JsonDeserializer.None.class)) {
/* 688 */       return r;
/*     */     }
/* 690 */     return _explicitClassOrOb(this._secondary.findDeserializer(a), JsonDeserializer.None.class);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findKeyDeserializer(Annotated a) {
/* 696 */     Object r = this._primary.findKeyDeserializer(a);
/* 697 */     if (_isExplicitClassOrOb(r, KeyDeserializer.None.class)) {
/* 698 */       return r;
/*     */     }
/* 700 */     return _explicitClassOrOb(this._secondary.findKeyDeserializer(a), KeyDeserializer.None.class);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findContentDeserializer(Annotated am) {
/* 706 */     Object r = this._primary.findContentDeserializer(am);
/* 707 */     if (_isExplicitClassOrOb(r, JsonDeserializer.None.class)) {
/* 708 */       return r;
/*     */     }
/* 710 */     return _explicitClassOrOb(this._secondary.findContentDeserializer(am), JsonDeserializer.None.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findDeserializationConverter(Annotated a) {
/* 717 */     Object ob = this._primary.findDeserializationConverter(a);
/* 718 */     return (ob == null) ? this._secondary.findDeserializationConverter(a) : ob;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object findDeserializationContentConverter(AnnotatedMember a) {
/* 723 */     Object ob = this._primary.findDeserializationContentConverter(a);
/* 724 */     return (ob == null) ? this._secondary.findDeserializationContentConverter(a) : ob;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
/* 734 */     JavaType t = this._secondary.refineDeserializationType(config, a, baseType);
/* 735 */     return this._primary.refineDeserializationType(config, a, t);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Class<?> findDeserializationType(Annotated am, JavaType baseType) {
/* 741 */     Class<?> r = this._primary.findDeserializationType(am, baseType);
/* 742 */     return (r != null) ? r : this._secondary.findDeserializationType(am, baseType);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType) {
/* 748 */     Class<?> result = this._primary.findDeserializationKeyType(am, baseKeyType);
/* 749 */     return (result == null) ? this._secondary.findDeserializationKeyType(am, baseKeyType) : result;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType) {
/* 755 */     Class<?> result = this._primary.findDeserializationContentType(am, baseContentType);
/* 756 */     return (result == null) ? this._secondary.findDeserializationContentType(am, baseContentType) : result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findValueInstantiator(AnnotatedClass ac) {
/* 763 */     Object result = this._primary.findValueInstantiator(ac);
/* 764 */     return (result == null) ? this._secondary.findValueInstantiator(ac) : result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> findPOJOBuilder(AnnotatedClass ac) {
/* 769 */     Class<?> result = this._primary.findPOJOBuilder(ac);
/* 770 */     return (result == null) ? this._secondary.findPOJOBuilder(ac) : result;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
/* 775 */     JsonPOJOBuilder.Value result = this._primary.findPOJOBuilderConfig(ac);
/* 776 */     return (result == null) ? this._secondary.findPOJOBuilderConfig(ac) : result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertyName findNameForDeserialization(Annotated a) {
/* 785 */     PropertyName n = this._primary.findNameForDeserialization(a);
/* 786 */     if (n == null) {
/* 787 */       n = this._secondary.findNameForDeserialization(a);
/* 788 */     } else if (n == PropertyName.USE_DEFAULT) {
/* 789 */       PropertyName n2 = this._secondary.findNameForDeserialization(a);
/* 790 */       if (n2 != null) {
/* 791 */         n = n2;
/*     */       }
/*     */     } 
/* 794 */     return n;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean hasAnySetter(Annotated a) {
/* 799 */     Boolean b = this._primary.hasAnySetter(a);
/* 800 */     if (b == null) {
/* 801 */       b = this._secondary.hasAnySetter(a);
/*     */     }
/* 803 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSetter.Value findSetterInfo(Annotated a) {
/* 808 */     JsonSetter.Value v2 = this._secondary.findSetterInfo(a);
/* 809 */     JsonSetter.Value v1 = this._primary.findSetterInfo(a);
/* 810 */     return (v2 == null) ? v1 : v2
/* 811 */       .withOverrides(v1);
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean findMergeInfo(Annotated a) {
/* 816 */     Boolean b = this._primary.findMergeInfo(a);
/* 817 */     if (b == null) {
/* 818 */       b = this._secondary.findMergeInfo(a);
/*     */     }
/* 820 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean hasCreatorAnnotation(Annotated a) {
/* 826 */     return (this._primary.hasCreatorAnnotation(a) || this._secondary.hasCreatorAnnotation(a));
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JsonCreator.Mode findCreatorBinding(Annotated a) {
/* 832 */     JsonCreator.Mode mode = this._primary.findCreatorBinding(a);
/* 833 */     if (mode != null) {
/* 834 */       return mode;
/*     */     }
/* 836 */     return this._secondary.findCreatorBinding(a);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
/* 841 */     JsonCreator.Mode mode = this._primary.findCreatorAnnotation(config, a);
/* 842 */     return (mode == null) ? this._secondary.findCreatorAnnotation(config, a) : mode;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
/* 848 */     return (this._primary.hasAnySetterAnnotation(am) || this._secondary.hasAnySetterAnnotation(am));
/*     */   }
/*     */   
/*     */   protected boolean _isExplicitClassOrOb(Object maybeCls, Class<?> implicit) {
/* 852 */     if (maybeCls == null || maybeCls == implicit) {
/* 853 */       return false;
/*     */     }
/* 855 */     if (maybeCls instanceof Class) {
/* 856 */       return !ClassUtil.isBogusClass((Class)maybeCls);
/*     */     }
/* 858 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object _explicitClassOrOb(Object maybeCls, Class<?> implicit) {
/* 863 */     if (maybeCls == null || maybeCls == implicit) {
/* 864 */       return null;
/*     */     }
/* 866 */     if (maybeCls instanceof Class && ClassUtil.isBogusClass((Class)maybeCls)) {
/* 867 */       return null;
/*     */     }
/* 869 */     return maybeCls;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/AnnotationIntrospectorPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */