/*     */ package com.fasterxml.jackson.databind.ser;
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ 
/*     */ public class PropertyBuilder {
/*  18 */   private static final Object NO_DEFAULT_MARKER = Boolean.FALSE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SerializationConfig _config;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final BeanDescription _beanDesc;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AnnotationIntrospector _annotationIntrospector;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _defaultBean;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonInclude.Value _defaultInclusion;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean _useRealPropertyDefaults;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertyBuilder(SerializationConfig config, BeanDescription beanDesc) {
/*  54 */     this._config = config;
/*  55 */     this._beanDesc = beanDesc;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  66 */     JsonInclude.Value inclPerType = JsonInclude.Value.merge(beanDesc
/*  67 */         .findPropertyInclusion(JsonInclude.Value.empty()), config
/*  68 */         .getDefaultPropertyInclusion(beanDesc.getBeanClass(), 
/*  69 */           JsonInclude.Value.empty()));
/*  70 */     this._defaultInclusion = JsonInclude.Value.merge(config.getDefaultPropertyInclusion(), inclPerType);
/*     */     
/*  72 */     this._useRealPropertyDefaults = (inclPerType.getValueInclusion() == JsonInclude.Include.NON_DEFAULT);
/*  73 */     this._annotationIntrospector = this._config.getAnnotationIntrospector();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Annotations getClassAnnotations() {
/*  83 */     return this._beanDesc.getClassAnnotations();
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
/*     */   protected BeanPropertyWriter buildWriter(SerializerProvider prov, BeanPropertyDefinition propDef, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, TypeSerializer contentTypeSer, AnnotatedMember am, boolean defaultUseStaticTyping) throws JsonMappingException {
/*     */     JavaType serializationType;
/*     */     Object defaultBean;
/*     */     SerializationFeature emptyJsonArrays;
/*     */     try {
/* 100 */       serializationType = findSerializationType((Annotated)am, defaultUseStaticTyping, declaredType);
/* 101 */     } catch (JsonMappingException e) {
/* 102 */       if (propDef == null) {
/* 103 */         return (BeanPropertyWriter)prov.reportBadDefinition(declaredType, ClassUtil.exceptionMessage((Throwable)e));
/*     */       }
/* 105 */       return (BeanPropertyWriter)prov.reportBadPropertyDefinition(this._beanDesc, propDef, ClassUtil.exceptionMessage((Throwable)e), new Object[0]);
/*     */     } 
/*     */ 
/*     */     
/* 109 */     if (contentTypeSer != null) {
/*     */ 
/*     */ 
/*     */       
/* 113 */       if (serializationType == null)
/*     */       {
/* 115 */         serializationType = declaredType;
/*     */       }
/* 117 */       JavaType ct = serializationType.getContentType();
/*     */       
/* 119 */       if (ct == null) {
/* 120 */         prov.reportBadPropertyDefinition(this._beanDesc, propDef, "serialization type " + serializationType + " has no content", new Object[0]);
/*     */       }
/*     */       
/* 123 */       serializationType = serializationType.withContentTypeHandler(contentTypeSer);
/* 124 */       ct = serializationType.getContentType();
/*     */     } 
/*     */     
/* 127 */     Object valueToSuppress = null;
/* 128 */     boolean suppressNulls = false;
/*     */ 
/*     */     
/* 131 */     JavaType actualType = (serializationType == null) ? declaredType : serializationType;
/*     */ 
/*     */     
/* 134 */     AnnotatedMember accessor = propDef.getAccessor();
/* 135 */     if (accessor == null)
/*     */     {
/* 137 */       return (BeanPropertyWriter)prov.reportBadPropertyDefinition(this._beanDesc, propDef, "could not determine property type", new Object[0]);
/*     */     }
/*     */     
/* 140 */     Class<?> rawPropertyType = accessor.getRawType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     JsonInclude.Value inclV = this._config.getDefaultInclusion(actualType.getRawClass(), rawPropertyType, this._defaultInclusion);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     inclV = inclV.withOverrides(propDef.findInclusion());
/*     */     
/* 152 */     JsonInclude.Include inclusion = inclV.getValueInclusion();
/* 153 */     if (inclusion == JsonInclude.Include.USE_DEFAULTS) {
/* 154 */       inclusion = JsonInclude.Include.ALWAYS;
/*     */     }
/* 156 */     switch (inclusion) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case NON_DEFAULT:
/* 169 */         if (this._useRealPropertyDefaults && (defaultBean = getDefaultBean()) != null) {
/*     */           
/* 171 */           if (prov.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
/* 172 */             am.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */           }
/*     */           try {
/* 175 */             valueToSuppress = am.getValue(defaultBean);
/* 176 */           } catch (Exception e) {
/* 177 */             _throwWrapped(e, propDef.getName(), defaultBean);
/*     */           } 
/*     */         } else {
/* 180 */           valueToSuppress = BeanUtil.getDefaultValue(actualType);
/* 181 */           suppressNulls = true;
/*     */         } 
/* 183 */         if (valueToSuppress == null) {
/* 184 */           suppressNulls = true; break;
/*     */         } 
/* 186 */         if (valueToSuppress.getClass().isArray()) {
/* 187 */           valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
/*     */         }
/*     */         break;
/*     */ 
/*     */       
/*     */       case NON_ABSENT:
/* 193 */         suppressNulls = true;
/*     */         
/* 195 */         if (actualType.isReferenceType()) {
/* 196 */           valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
/*     */         }
/*     */         break;
/*     */       
/*     */       case NON_EMPTY:
/* 201 */         suppressNulls = true;
/*     */         
/* 203 */         valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
/*     */         break;
/*     */       case CUSTOM:
/* 206 */         valueToSuppress = prov.includeFilterInstance(propDef, inclV.getValueFilter());
/* 207 */         if (valueToSuppress == null) {
/* 208 */           suppressNulls = true; break;
/*     */         } 
/* 210 */         suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress);
/*     */         break;
/*     */       
/*     */       case NON_NULL:
/* 214 */         suppressNulls = true;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       default:
/* 220 */         emptyJsonArrays = SerializationFeature.WRITE_EMPTY_JSON_ARRAYS;
/* 221 */         if (actualType.isContainerType() && !this._config.isEnabled(emptyJsonArrays)) {
/* 222 */           valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
/*     */         }
/*     */         break;
/*     */     } 
/* 226 */     Class<?>[] views = propDef.findViews();
/* 227 */     if (views == null) {
/* 228 */       views = this._beanDesc.findDefaultViews();
/*     */     }
/* 230 */     BeanPropertyWriter bpw = _constructPropertyWriter(propDef, am, this._beanDesc
/* 231 */         .getClassAnnotations(), declaredType, ser, typeSer, serializationType, suppressNulls, valueToSuppress, views);
/*     */ 
/*     */ 
/*     */     
/* 235 */     Object serDef = this._annotationIntrospector.findNullSerializer((Annotated)am);
/* 236 */     if (serDef != null) {
/* 237 */       bpw.assignNullSerializer(prov.serializerInstance((Annotated)am, serDef));
/*     */     }
/*     */     
/* 240 */     NameTransformer unwrapper = this._annotationIntrospector.findUnwrappingNameTransformer(am);
/* 241 */     if (unwrapper != null) {
/* 242 */       bpw = bpw.unwrappingWriter(unwrapper);
/*     */     }
/* 244 */     return bpw;
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
/*     */   protected BeanPropertyWriter _constructPropertyWriter(BeanPropertyDefinition propDef, AnnotatedMember member, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, boolean suppressNulls, Object suppressableValue, Class<?>[] includeInViews) throws JsonMappingException {
/* 261 */     return new BeanPropertyWriter(propDef, member, contextAnnotations, declaredType, ser, typeSer, serType, suppressNulls, suppressableValue, includeInViews);
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
/*     */   protected JavaType findSerializationType(Annotated a, boolean useStaticTyping, JavaType declaredType) throws JsonMappingException {
/* 281 */     JavaType secondary = this._annotationIntrospector.refineSerializationType((MapperConfig)this._config, a, declaredType);
/*     */ 
/*     */ 
/*     */     
/* 285 */     if (secondary != declaredType) {
/* 286 */       Class<?> serClass = secondary.getRawClass();
/*     */       
/* 288 */       Class<?> rawDeclared = declaredType.getRawClass();
/* 289 */       if (!serClass.isAssignableFrom(rawDeclared))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 298 */         if (!rawDeclared.isAssignableFrom(serClass)) {
/* 299 */           throw new IllegalArgumentException("Illegal concrete-type annotation for method '" + a.getName() + "': class " + serClass.getName() + " not a super-type of (declared) class " + rawDeclared.getName());
/*     */         }
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 306 */       useStaticTyping = true;
/* 307 */       declaredType = secondary;
/*     */     } 
/*     */     
/* 310 */     JsonSerialize.Typing typing = this._annotationIntrospector.findSerializationTyping(a);
/* 311 */     if (typing != null && typing != JsonSerialize.Typing.DEFAULT_TYPING) {
/* 312 */       useStaticTyping = (typing == JsonSerialize.Typing.STATIC);
/*     */     }
/* 314 */     if (useStaticTyping)
/*     */     {
/* 316 */       return declaredType.withStaticTyping();
/*     */     }
/*     */     
/* 319 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object getDefaultBean() {
/* 330 */     Object def = this._defaultBean;
/* 331 */     if (def == null) {
/*     */ 
/*     */ 
/*     */       
/* 335 */       def = this._beanDesc.instantiateBean(this._config.canOverrideAccessModifiers());
/* 336 */       if (def == null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 344 */         def = NO_DEFAULT_MARKER;
/*     */       }
/* 346 */       this._defaultBean = def;
/*     */     } 
/* 348 */     return (def == NO_DEFAULT_MARKER) ? null : this._defaultBean;
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
/*     */   @Deprecated
/*     */   protected Object getPropertyDefaultValue(String name, AnnotatedMember member, JavaType type) {
/* 370 */     Object defaultBean = getDefaultBean();
/* 371 */     if (defaultBean == null) {
/* 372 */       return getDefaultValue(type);
/*     */     }
/*     */     try {
/* 375 */       return member.getValue(defaultBean);
/* 376 */     } catch (Exception e) {
/* 377 */       return _throwWrapped(e, name, defaultBean);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected Object getDefaultValue(JavaType type) {
/* 386 */     return BeanUtil.getDefaultValue(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _throwWrapped(Exception e, String propName, Object defaultBean) {
/* 397 */     Throwable t = e;
/* 398 */     while (t.getCause() != null) {
/* 399 */       t = t.getCause();
/*     */     }
/* 401 */     ClassUtil.throwIfError(t);
/* 402 */     ClassUtil.throwIfRTE(t);
/* 403 */     throw new IllegalArgumentException("Failed to get property '" + propName + "' of default " + defaultBean.getClass().getName() + " instance");
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/PropertyBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */