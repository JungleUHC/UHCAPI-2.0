/*      */ package com.fasterxml.jackson.databind.introspect;
/*      */ import com.fasterxml.jackson.annotation.JacksonInject;
/*      */ import com.fasterxml.jackson.annotation.JsonAlias;
/*      */ import com.fasterxml.jackson.annotation.JsonCreator;
/*      */ import com.fasterxml.jackson.annotation.JsonFormat;
/*      */ import com.fasterxml.jackson.annotation.JsonIdentityInfo;
/*      */ import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*      */ import com.fasterxml.jackson.annotation.JsonInclude;
/*      */ import com.fasterxml.jackson.annotation.JsonIncludeProperties;
/*      */ import com.fasterxml.jackson.annotation.JsonManagedReference;
/*      */ import com.fasterxml.jackson.annotation.JsonProperty;
/*      */ import com.fasterxml.jackson.annotation.JsonPropertyOrder;
/*      */ import com.fasterxml.jackson.annotation.JsonSetter;
/*      */ import com.fasterxml.jackson.annotation.JsonSubTypes;
/*      */ import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*      */ import com.fasterxml.jackson.annotation.JsonUnwrapped;
/*      */ import com.fasterxml.jackson.annotation.JsonValue;
/*      */ import com.fasterxml.jackson.annotation.JsonView;
/*      */ import com.fasterxml.jackson.databind.JavaType;
/*      */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*      */ import com.fasterxml.jackson.databind.JsonSerializer;
/*      */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*      */ import com.fasterxml.jackson.databind.PropertyName;
/*      */ import com.fasterxml.jackson.databind.annotation.JsonAppend;
/*      */ import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
/*      */ import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/*      */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*      */ import com.fasterxml.jackson.databind.jsontype.NamedType;
/*      */ import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*      */ import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
/*      */ import com.fasterxml.jackson.databind.type.MapLikeType;
/*      */ import com.fasterxml.jackson.databind.type.TypeFactory;
/*      */ import java.lang.reflect.Field;
/*      */ import java.util.List;
/*      */ 
/*      */ public class JacksonAnnotationIntrospector extends AnnotationIntrospector implements Serializable {
/*   37 */   private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_SER = new Class[] { JsonSerialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonRawValue.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final long serialVersionUID = 1L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   50 */   private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_DESER = new Class[] { JsonDeserialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class, JsonMerge.class };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final Java7Support _java7Helper;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static {
/*   66 */     Java7Support x = null;
/*      */     try {
/*   68 */       x = Java7Support.instance();
/*   69 */     } catch (Throwable throwable) {}
/*   70 */     _java7Helper = x;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   82 */   protected transient LRUMap<Class<?>, Boolean> _annotationsInside = new LRUMap(48, 48);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _cfgConstructorPropertiesImpliesCreator = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Version version() {
/*  110 */     return PackageVersion.VERSION;
/*      */   }
/*      */   
/*      */   protected Object readResolve() {
/*  114 */     if (this._annotationsInside == null) {
/*  115 */       this._annotationsInside = new LRUMap(48, 48);
/*      */     }
/*  117 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JacksonAnnotationIntrospector setConstructorPropertiesImpliesCreator(boolean b) {
/*  138 */     this._cfgConstructorPropertiesImpliesCreator = b;
/*  139 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAnnotationBundle(Annotation ann) {
/*  158 */     Class<?> type = ann.annotationType();
/*  159 */     Boolean b = (Boolean)this._annotationsInside.get(type);
/*  160 */     if (b == null) {
/*  161 */       b = Boolean.valueOf((type.getAnnotation(JacksonAnnotationsInside.class) != null));
/*  162 */       this._annotationsInside.putIfAbsent(type, b);
/*      */     } 
/*  164 */     return b.booleanValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public String findEnumValue(Enum<?> value) {
/*      */     try {
/*  186 */       Field f = value.getDeclaringClass().getField(value.name());
/*  187 */       if (f != null) {
/*  188 */         JsonProperty prop = f.<JsonProperty>getAnnotation(JsonProperty.class);
/*  189 */         if (prop != null) {
/*  190 */           String n = prop.value();
/*  191 */           if (n != null && !n.isEmpty()) {
/*  192 */             return n;
/*      */           }
/*      */         } 
/*      */       } 
/*  196 */     } catch (SecurityException securityException) {
/*      */     
/*  198 */     } catch (NoSuchFieldException noSuchFieldException) {}
/*      */ 
/*      */     
/*  201 */     return value.name();
/*      */   }
/*      */ 
/*      */   
/*      */   public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
/*  206 */     HashMap<String, String> expl = null;
/*  207 */     for (Field f : enumType.getDeclaredFields()) {
/*  208 */       if (f.isEnumConstant()) {
/*      */ 
/*      */         
/*  211 */         JsonProperty prop = f.<JsonProperty>getAnnotation(JsonProperty.class);
/*  212 */         if (prop != null) {
/*      */ 
/*      */           
/*  215 */           String n = prop.value();
/*  216 */           if (!n.isEmpty())
/*      */           
/*      */           { 
/*  219 */             if (expl == null) {
/*  220 */               expl = new HashMap<>();
/*      */             }
/*  222 */             expl.put(f.getName(), n); } 
/*      */         } 
/*      */       } 
/*  225 */     }  if (expl != null) {
/*  226 */       for (int i = 0, end = enumValues.length; i < end; i++) {
/*  227 */         String defName = enumValues[i].name();
/*  228 */         String explValue = expl.get(defName);
/*  229 */         if (explValue != null) {
/*  230 */           names[i] = explValue;
/*      */         }
/*      */       } 
/*      */     }
/*  234 */     return names;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void findEnumAliases(Class<?> enumType, Enum<?>[] enumValues, String[][] aliasList) {
/*  242 */     for (Field f : enumType.getDeclaredFields()) {
/*  243 */       if (f.isEnumConstant()) {
/*  244 */         JsonAlias aliasAnnotation = f.<JsonAlias>getAnnotation(JsonAlias.class);
/*  245 */         if (aliasAnnotation != null) {
/*  246 */           String[] aliases = aliasAnnotation.value();
/*  247 */           if (aliases.length != 0) {
/*  248 */             String name = f.getName();
/*      */             
/*  250 */             for (int i = 0, end = enumValues.length; i < end; i++) {
/*  251 */               if (name.equals(enumValues[i].name())) {
/*  252 */                 aliasList[i] = aliases;
/*      */               }
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
/*  272 */     return ClassUtil.findFirstAnnotatedEnumValue(enumCls, JsonEnumDefaultValue.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PropertyName findRootName(AnnotatedClass ac) {
/*  284 */     JsonRootName ann = (JsonRootName)_findAnnotation(ac, JsonRootName.class);
/*  285 */     if (ann == null) {
/*  286 */       return null;
/*      */     }
/*  288 */     String ns = ann.namespace();
/*  289 */     if (ns != null && ns.isEmpty()) {
/*  290 */       ns = null;
/*      */     }
/*  292 */     return PropertyName.construct(ann.value(), ns);
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean isIgnorableType(AnnotatedClass ac) {
/*  297 */     JsonIgnoreType ignore = (JsonIgnoreType)_findAnnotation(ac, JsonIgnoreType.class);
/*  298 */     return (ignore == null) ? null : Boolean.valueOf(ignore.value());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonIgnoreProperties.Value findPropertyIgnoralByName(MapperConfig<?> config, Annotated a) {
/*  304 */     JsonIgnoreProperties v = (JsonIgnoreProperties)_findAnnotation(a, JsonIgnoreProperties.class);
/*  305 */     if (v == null) {
/*  306 */       return JsonIgnoreProperties.Value.empty();
/*      */     }
/*  308 */     return JsonIgnoreProperties.Value.from(v);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated ac) {
/*  315 */     return findPropertyIgnoralByName((MapperConfig<?>)null, ac);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonIncludeProperties.Value findPropertyInclusionByName(MapperConfig<?> config, Annotated a) {
/*  321 */     JsonIncludeProperties v = (JsonIncludeProperties)_findAnnotation(a, JsonIncludeProperties.class);
/*  322 */     if (v == null) {
/*  323 */       return JsonIncludeProperties.Value.all();
/*      */     }
/*  325 */     return JsonIncludeProperties.Value.from(v);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object findFilterId(Annotated a) {
/*  330 */     JsonFilter ann = (JsonFilter)_findAnnotation(a, JsonFilter.class);
/*  331 */     if (ann != null) {
/*  332 */       String id = ann.value();
/*      */       
/*  334 */       if (!id.isEmpty()) {
/*  335 */         return id;
/*      */       }
/*      */     } 
/*  338 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findNamingStrategy(AnnotatedClass ac) {
/*  344 */     JsonNaming ann = (JsonNaming)_findAnnotation(ac, JsonNaming.class);
/*  345 */     return (ann == null) ? null : ann.value();
/*      */   }
/*      */ 
/*      */   
/*      */   public String findClassDescription(AnnotatedClass ac) {
/*  350 */     JsonClassDescription ann = (JsonClassDescription)_findAnnotation(ac, JsonClassDescription.class);
/*  351 */     return (ann == null) ? null : ann.value();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
/*  364 */     JsonAutoDetect ann = (JsonAutoDetect)_findAnnotation(ac, JsonAutoDetect.class);
/*  365 */     return (ann == null) ? checker : (VisibilityChecker<?>)checker.with(ann);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String findImplicitPropertyName(AnnotatedMember m) {
/*  376 */     PropertyName n = _findConstructorName(m);
/*  377 */     return (n == null) ? null : n.getSimpleName();
/*      */   }
/*      */ 
/*      */   
/*      */   public List<PropertyName> findPropertyAliases(Annotated m) {
/*  382 */     JsonAlias ann = (JsonAlias)_findAnnotation(m, JsonAlias.class);
/*  383 */     if (ann == null) {
/*  384 */       return null;
/*      */     }
/*  386 */     String[] strs = ann.value();
/*  387 */     int len = strs.length;
/*  388 */     if (len == 0) {
/*  389 */       return Collections.emptyList();
/*      */     }
/*  391 */     List<PropertyName> result = new ArrayList<>(len);
/*  392 */     for (int i = 0; i < len; i++) {
/*  393 */       result.add(PropertyName.construct(strs[i]));
/*      */     }
/*  395 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasIgnoreMarker(AnnotatedMember m) {
/*  400 */     return _isIgnorable(m);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Boolean hasRequiredMarker(AnnotatedMember m) {
/*  406 */     JsonProperty ann = (JsonProperty)_findAnnotation(m, JsonProperty.class);
/*  407 */     if (ann != null) {
/*  408 */       return Boolean.valueOf(ann.required());
/*      */     }
/*  410 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonProperty.Access findPropertyAccess(Annotated m) {
/*  415 */     JsonProperty ann = (JsonProperty)_findAnnotation(m, JsonProperty.class);
/*  416 */     if (ann != null) {
/*  417 */       return ann.access();
/*      */     }
/*  419 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public String findPropertyDescription(Annotated ann) {
/*  424 */     JsonPropertyDescription desc = (JsonPropertyDescription)_findAnnotation(ann, JsonPropertyDescription.class);
/*  425 */     return (desc == null) ? null : desc.value();
/*      */   }
/*      */ 
/*      */   
/*      */   public Integer findPropertyIndex(Annotated ann) {
/*  430 */     JsonProperty prop = (JsonProperty)_findAnnotation(ann, JsonProperty.class);
/*  431 */     if (prop != null) {
/*  432 */       int ix = prop.index();
/*  433 */       if (ix != -1) {
/*  434 */         return Integer.valueOf(ix);
/*      */       }
/*      */     } 
/*  437 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public String findPropertyDefaultValue(Annotated ann) {
/*  442 */     JsonProperty prop = (JsonProperty)_findAnnotation(ann, JsonProperty.class);
/*  443 */     if (prop == null) {
/*  444 */       return null;
/*      */     }
/*  446 */     String str = prop.defaultValue();
/*      */     
/*  448 */     return str.isEmpty() ? null : str;
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonFormat.Value findFormat(Annotated ann) {
/*  453 */     JsonFormat f = (JsonFormat)_findAnnotation(ann, JsonFormat.class);
/*      */ 
/*      */     
/*  456 */     return (f == null) ? null : JsonFormat.Value.from(f);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
/*  462 */     JsonManagedReference ref1 = (JsonManagedReference)_findAnnotation(member, JsonManagedReference.class);
/*  463 */     if (ref1 != null) {
/*  464 */       return AnnotationIntrospector.ReferenceProperty.managed(ref1.value());
/*      */     }
/*  466 */     JsonBackReference ref2 = (JsonBackReference)_findAnnotation(member, JsonBackReference.class);
/*  467 */     if (ref2 != null) {
/*  468 */       return AnnotationIntrospector.ReferenceProperty.back(ref2.value());
/*      */     }
/*  470 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
/*  476 */     JsonUnwrapped ann = (JsonUnwrapped)_findAnnotation(member, JsonUnwrapped.class);
/*      */ 
/*      */     
/*  479 */     if (ann == null || !ann.enabled()) {
/*  480 */       return null;
/*      */     }
/*  482 */     String prefix = ann.prefix();
/*  483 */     String suffix = ann.suffix();
/*  484 */     return NameTransformer.simpleTransformer(prefix, suffix);
/*      */   }
/*      */ 
/*      */   
/*      */   public JacksonInject.Value findInjectableValue(AnnotatedMember m) {
/*  489 */     JacksonInject ann = (JacksonInject)_findAnnotation(m, JacksonInject.class);
/*  490 */     if (ann == null) {
/*  491 */       return null;
/*      */     }
/*      */     
/*  494 */     JacksonInject.Value v = JacksonInject.Value.from(ann);
/*  495 */     if (!v.hasId()) {
/*      */       Object id;
/*      */       
/*  498 */       if (!(m instanceof AnnotatedMethod)) {
/*  499 */         id = m.getRawType().getName();
/*      */       } else {
/*  501 */         AnnotatedMethod am = (AnnotatedMethod)m;
/*  502 */         if (am.getParameterCount() == 0) {
/*  503 */           id = m.getRawType().getName();
/*      */         } else {
/*  505 */           id = am.getRawParameterType(0).getName();
/*      */         } 
/*      */       } 
/*  508 */       v = v.withId(id);
/*      */     } 
/*  510 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Object findInjectableValueId(AnnotatedMember m) {
/*  516 */     JacksonInject.Value v = findInjectableValue(m);
/*  517 */     return (v == null) ? null : v.getId();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Class<?>[] findViews(Annotated a) {
/*  523 */     JsonView ann = (JsonView)_findAnnotation(a, JsonView.class);
/*  524 */     return (ann == null) ? null : ann.value();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
/*  545 */     Class<?> cls1 = setter1.getRawParameterType(0);
/*  546 */     Class<?> cls2 = setter2.getRawParameterType(0);
/*      */ 
/*      */ 
/*      */     
/*  550 */     if (cls1.isPrimitive()) {
/*  551 */       if (!cls2.isPrimitive()) {
/*  552 */         return setter1;
/*      */       }
/*      */       
/*  555 */       return null;
/*  556 */     }  if (cls2.isPrimitive()) {
/*  557 */       return setter2;
/*      */     }
/*      */     
/*  560 */     if (cls1 == String.class) {
/*  561 */       if (cls2 != String.class) {
/*  562 */         return setter1;
/*      */       }
/*  564 */     } else if (cls2 == String.class) {
/*  565 */       return setter2;
/*      */     } 
/*      */     
/*  568 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PropertyName findRenameByField(MapperConfig<?> config, AnnotatedField f, PropertyName implName) {
/*  575 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
/*  588 */     return _findTypeResolver(config, ac, baseType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
/*  599 */     if (baseType.isContainerType() || baseType.isReferenceType()) {
/*  600 */       return null;
/*      */     }
/*      */     
/*  603 */     return _findTypeResolver(config, am, baseType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
/*  612 */     if (containerType.getContentType() == null) {
/*  613 */       throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
/*      */     }
/*  615 */     return _findTypeResolver(config, am, containerType);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public List<NamedType> findSubtypes(Annotated a) {
/*  621 */     JsonSubTypes t = (JsonSubTypes)_findAnnotation(a, JsonSubTypes.class);
/*  622 */     if (t == null) return null; 
/*  623 */     JsonSubTypes.Type[] types = t.value();
/*  624 */     ArrayList<NamedType> result = new ArrayList<>(types.length);
/*  625 */     for (JsonSubTypes.Type type : types) {
/*  626 */       result.add(new NamedType(type.value(), type.name()));
/*      */       
/*  628 */       for (String name : type.names()) {
/*  629 */         result.add(new NamedType(type.value(), name));
/*      */       }
/*      */     } 
/*  632 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String findTypeName(AnnotatedClass ac) {
/*  638 */     JsonTypeName tn = (JsonTypeName)_findAnnotation(ac, JsonTypeName.class);
/*  639 */     return (tn == null) ? null : tn.value();
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean isTypeId(AnnotatedMember member) {
/*  644 */     return Boolean.valueOf(_hasAnnotation(member, JsonTypeId.class));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectIdInfo findObjectIdInfo(Annotated ann) {
/*  655 */     JsonIdentityInfo info = (JsonIdentityInfo)_findAnnotation(ann, JsonIdentityInfo.class);
/*  656 */     if (info == null || info.generator() == ObjectIdGenerators.None.class) {
/*  657 */       return null;
/*      */     }
/*      */     
/*  660 */     PropertyName name = PropertyName.construct(info.property());
/*  661 */     return new ObjectIdInfo(name, info.scope(), info.generator(), info.resolver());
/*      */   }
/*      */ 
/*      */   
/*      */   public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
/*  666 */     JsonIdentityReference ref = (JsonIdentityReference)_findAnnotation(ann, JsonIdentityReference.class);
/*  667 */     if (ref == null) {
/*  668 */       return objectIdInfo;
/*      */     }
/*  670 */     if (objectIdInfo == null) {
/*  671 */       objectIdInfo = ObjectIdInfo.empty();
/*      */     }
/*  673 */     return objectIdInfo.withAlwaysAsId(ref.alwaysAsId());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findSerializer(Annotated a) {
/*  685 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  686 */     if (ann != null) {
/*      */       
/*  688 */       Class<? extends JsonSerializer> serClass = ann.using();
/*  689 */       if (serClass != JsonSerializer.None.class) {
/*  690 */         return serClass;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  698 */     JsonRawValue annRaw = (JsonRawValue)_findAnnotation(a, JsonRawValue.class);
/*  699 */     if (annRaw != null && annRaw.value()) {
/*      */       
/*  701 */       Class<?> cls = a.getRawType();
/*  702 */       return new RawSerializer(cls);
/*      */     } 
/*  704 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findKeySerializer(Annotated a) {
/*  710 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  711 */     if (ann != null) {
/*      */       
/*  713 */       Class<? extends JsonSerializer> serClass = ann.keyUsing();
/*  714 */       if (serClass != JsonSerializer.None.class) {
/*  715 */         return serClass;
/*      */       }
/*      */     } 
/*  718 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findContentSerializer(Annotated a) {
/*  724 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  725 */     if (ann != null) {
/*      */       
/*  727 */       Class<? extends JsonSerializer> serClass = ann.contentUsing();
/*  728 */       if (serClass != JsonSerializer.None.class) {
/*  729 */         return serClass;
/*      */       }
/*      */     } 
/*  732 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findNullSerializer(Annotated a) {
/*  738 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  739 */     if (ann != null) {
/*      */       
/*  741 */       Class<? extends JsonSerializer> serClass = ann.nullsUsing();
/*  742 */       if (serClass != JsonSerializer.None.class) {
/*  743 */         return serClass;
/*      */       }
/*      */     } 
/*  746 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonInclude.Value findPropertyInclusion(Annotated a) {
/*  752 */     JsonInclude inc = (JsonInclude)_findAnnotation(a, JsonInclude.class);
/*  753 */     JsonInclude.Value value = (inc == null) ? JsonInclude.Value.empty() : JsonInclude.Value.from(inc);
/*      */ 
/*      */     
/*  756 */     if (value.getValueInclusion() == JsonInclude.Include.USE_DEFAULTS) {
/*  757 */       value = _refinePropertyInclusion(a, value);
/*      */     }
/*  759 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   private JsonInclude.Value _refinePropertyInclusion(Annotated a, JsonInclude.Value value) {
/*  764 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  765 */     if (ann != null) {
/*  766 */       switch (ann.include()) {
/*      */         case ALWAYS:
/*  768 */           return value.withValueInclusion(JsonInclude.Include.ALWAYS);
/*      */         case NON_NULL:
/*  770 */           return value.withValueInclusion(JsonInclude.Include.NON_NULL);
/*      */         case NON_DEFAULT:
/*  772 */           return value.withValueInclusion(JsonInclude.Include.NON_DEFAULT);
/*      */         case NON_EMPTY:
/*  774 */           return value.withValueInclusion(JsonInclude.Include.NON_EMPTY);
/*      */       } 
/*      */ 
/*      */     
/*      */     }
/*  779 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonSerialize.Typing findSerializationTyping(Annotated a) {
/*  785 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  786 */     return (ann == null) ? null : ann.typing();
/*      */   }
/*      */ 
/*      */   
/*      */   public Object findSerializationConverter(Annotated a) {
/*  791 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  792 */     return (ann == null) ? null : _classIfExplicit(ann.converter(), Converter.None.class);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object findSerializationContentConverter(AnnotatedMember a) {
/*  797 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  798 */     return (ann == null) ? null : _classIfExplicit(ann.contentConverter(), Converter.None.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
/*      */     MapLikeType mapLikeType;
/*  812 */     JavaType javaType1, type = baseType;
/*  813 */     TypeFactory tf = config.getTypeFactory();
/*      */     
/*  815 */     JsonSerialize jsonSer = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*      */ 
/*      */ 
/*      */     
/*  819 */     Class<?> serClass = (jsonSer == null) ? null : _classIfExplicit(jsonSer.as());
/*  820 */     if (serClass != null) {
/*  821 */       if (type.hasRawClass(serClass)) {
/*      */ 
/*      */         
/*  824 */         type = type.withStaticTyping();
/*      */       } else {
/*  826 */         Class<?> currRaw = type.getRawClass();
/*      */ 
/*      */         
/*      */         try {
/*  830 */           if (serClass.isAssignableFrom(currRaw)) {
/*  831 */             type = tf.constructGeneralizedType(type, serClass);
/*  832 */           } else if (currRaw.isAssignableFrom(serClass)) {
/*  833 */             type = tf.constructSpecializedType(type, serClass);
/*  834 */           } else if (_primitiveAndWrapper(currRaw, serClass)) {
/*      */             
/*  836 */             type = type.withStaticTyping();
/*      */           } else {
/*  838 */             throw _databindException(
/*  839 */                 String.format("Cannot refine serialization type %s into %s; types not related", new Object[] {
/*  840 */                     type, serClass.getName() }));
/*      */           } 
/*  842 */         } catch (IllegalArgumentException iae) {
/*  843 */           throw _databindException(iae, 
/*  844 */               String.format("Failed to widen type %s with annotation (value %s), from '%s': %s", new Object[] {
/*  845 */                   type, serClass.getName(), a.getName(), iae.getMessage()
/*      */                 }));
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  852 */     if (type.isMapLikeType()) {
/*  853 */       JavaType keyType = type.getKeyType();
/*  854 */       Class<?> keyClass = (jsonSer == null) ? null : _classIfExplicit(jsonSer.keyAs());
/*  855 */       if (keyClass != null) {
/*  856 */         if (keyType.hasRawClass(keyClass)) {
/*  857 */           keyType = keyType.withStaticTyping();
/*      */         } else {
/*  859 */           Class<?> currRaw = keyType.getRawClass();
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/*  864 */             if (keyClass.isAssignableFrom(currRaw)) {
/*  865 */               keyType = tf.constructGeneralizedType(keyType, keyClass);
/*  866 */             } else if (currRaw.isAssignableFrom(keyClass)) {
/*  867 */               keyType = tf.constructSpecializedType(keyType, keyClass);
/*  868 */             } else if (_primitiveAndWrapper(currRaw, keyClass)) {
/*      */               
/*  870 */               keyType = keyType.withStaticTyping();
/*      */             } else {
/*  872 */               throw _databindException(
/*  873 */                   String.format("Cannot refine serialization key type %s into %s; types not related", new Object[] {
/*  874 */                       keyType, keyClass.getName() }));
/*      */             } 
/*  876 */           } catch (IllegalArgumentException iae) {
/*  877 */             throw _databindException(iae, 
/*  878 */                 String.format("Failed to widen key type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[] {
/*  879 */                     type, keyClass.getName(), a.getName(), iae.getMessage() }));
/*      */           } 
/*      */         } 
/*  882 */         mapLikeType = ((MapLikeType)type).withKeyType(keyType);
/*      */       } 
/*      */     } 
/*      */     
/*  886 */     JavaType contentType = mapLikeType.getContentType();
/*  887 */     if (contentType != null) {
/*      */       
/*  889 */       Class<?> contentClass = (jsonSer == null) ? null : _classIfExplicit(jsonSer.contentAs());
/*  890 */       if (contentClass != null) {
/*  891 */         if (contentType.hasRawClass(contentClass)) {
/*  892 */           contentType = contentType.withStaticTyping();
/*      */         
/*      */         }
/*      */         else {
/*      */           
/*  897 */           Class<?> currRaw = contentType.getRawClass();
/*      */           try {
/*  899 */             if (contentClass.isAssignableFrom(currRaw)) {
/*  900 */               contentType = tf.constructGeneralizedType(contentType, contentClass);
/*  901 */             } else if (currRaw.isAssignableFrom(contentClass)) {
/*  902 */               contentType = tf.constructSpecializedType(contentType, contentClass);
/*  903 */             } else if (_primitiveAndWrapper(currRaw, contentClass)) {
/*      */               
/*  905 */               contentType = contentType.withStaticTyping();
/*      */             } else {
/*  907 */               throw _databindException(
/*  908 */                   String.format("Cannot refine serialization content type %s into %s; types not related", new Object[] {
/*  909 */                       contentType, contentClass.getName() }));
/*      */             } 
/*  911 */           } catch (IllegalArgumentException iae) {
/*  912 */             throw _databindException(iae, 
/*  913 */                 String.format("Internal error: failed to refine value type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[] {
/*  914 */                     mapLikeType, contentClass.getName(), a.getName(), iae.getMessage() }));
/*      */           } 
/*      */         } 
/*  917 */         javaType1 = mapLikeType.withContentType(contentType);
/*      */       } 
/*      */     } 
/*  920 */     return javaType1;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Class<?> findSerializationType(Annotated am) {
/*  926 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
/*  932 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
/*  938 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
/*  949 */     JsonPropertyOrder order = (JsonPropertyOrder)_findAnnotation(ac, JsonPropertyOrder.class);
/*  950 */     return (order == null) ? null : order.value();
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean findSerializationSortAlphabetically(Annotated ann) {
/*  955 */     return _findSortAlpha(ann);
/*      */   }
/*      */   
/*      */   private final Boolean _findSortAlpha(Annotated ann) {
/*  959 */     JsonPropertyOrder order = (JsonPropertyOrder)_findAnnotation(ann, JsonPropertyOrder.class);
/*      */ 
/*      */     
/*  962 */     if (order != null && order.alphabetic()) {
/*  963 */       return Boolean.TRUE;
/*      */     }
/*  965 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
/*  971 */     JsonAppend ann = (JsonAppend)_findAnnotation(ac, JsonAppend.class);
/*  972 */     if (ann == null) {
/*      */       return;
/*      */     }
/*  975 */     boolean prepend = ann.prepend();
/*  976 */     JavaType propType = null;
/*      */ 
/*      */     
/*  979 */     JsonAppend.Attr[] attrs = ann.attrs();
/*  980 */     for (int i = 0, len = attrs.length; i < len; i++) {
/*  981 */       if (propType == null) {
/*  982 */         propType = config.constructType(Object.class);
/*      */       }
/*  984 */       BeanPropertyWriter bpw = _constructVirtualProperty(attrs[i], config, ac, propType);
/*      */       
/*  986 */       if (prepend) {
/*  987 */         properties.add(i, bpw);
/*      */       } else {
/*  989 */         properties.add(bpw);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  994 */     JsonAppend.Prop[] props = ann.props();
/*  995 */     for (int j = 0, k = props.length; j < k; j++) {
/*  996 */       BeanPropertyWriter bpw = _constructVirtualProperty(props[j], config, ac);
/*      */       
/*  998 */       if (prepend) {
/*  999 */         properties.add(j, bpw);
/*      */       } else {
/* 1001 */         properties.add(bpw);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeanPropertyWriter _constructVirtualProperty(JsonAppend.Attr attr, MapperConfig<?> config, AnnotatedClass ac, JavaType type) {
/* 1009 */     PropertyMetadata metadata = attr.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
/*      */ 
/*      */     
/* 1012 */     String attrName = attr.value();
/*      */ 
/*      */     
/* 1015 */     PropertyName propName = _propertyName(attr.propName(), attr.propNamespace());
/* 1016 */     if (!propName.hasSimpleName()) {
/* 1017 */       propName = PropertyName.construct(attrName);
/*      */     }
/*      */     
/* 1020 */     AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), attrName, type);
/*      */ 
/*      */     
/* 1023 */     SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, attr
/* 1024 */         .include());
/*      */     
/* 1026 */     return (BeanPropertyWriter)AttributePropertyWriter.construct(attrName, (BeanPropertyDefinition)propDef, ac
/* 1027 */         .getAnnotations(), type);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected BeanPropertyWriter _constructVirtualProperty(JsonAppend.Prop prop, MapperConfig<?> config, AnnotatedClass ac) {
/* 1033 */     PropertyMetadata metadata = prop.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
/*      */     
/* 1035 */     PropertyName propName = _propertyName(prop.name(), prop.namespace());
/* 1036 */     JavaType type = config.constructType(prop.type());
/*      */ 
/*      */     
/* 1039 */     AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), propName.getSimpleName(), type);
/*      */     
/* 1041 */     SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, prop
/* 1042 */         .include());
/*      */     
/* 1044 */     Class<?> implClass = prop.value();
/*      */     
/* 1046 */     HandlerInstantiator hi = config.getHandlerInstantiator();
/*      */     
/* 1048 */     VirtualBeanPropertyWriter bpw = (hi == null) ? null : hi.virtualPropertyWriterInstance(config, implClass);
/* 1049 */     if (bpw == null) {
/* 1050 */       bpw = (VirtualBeanPropertyWriter)ClassUtil.createInstance(implClass, config
/* 1051 */           .canOverrideAccessModifiers());
/*      */     }
/*      */ 
/*      */     
/* 1055 */     return (BeanPropertyWriter)bpw.withConfig(config, ac, (BeanPropertyDefinition)propDef, type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PropertyName findNameForSerialization(Annotated a) {
/* 1067 */     boolean useDefault = false;
/* 1068 */     JsonGetter jg = (JsonGetter)_findAnnotation(a, JsonGetter.class);
/* 1069 */     if (jg != null) {
/* 1070 */       String s = jg.value();
/*      */       
/* 1072 */       if (!s.isEmpty()) {
/* 1073 */         return PropertyName.construct(s);
/*      */       }
/* 1075 */       useDefault = true;
/*      */     } 
/* 1077 */     JsonProperty pann = (JsonProperty)_findAnnotation(a, JsonProperty.class);
/* 1078 */     if (pann != null) {
/*      */       
/* 1080 */       String ns = pann.namespace();
/* 1081 */       if (ns != null && ns.isEmpty()) {
/* 1082 */         ns = null;
/*      */       }
/* 1084 */       return PropertyName.construct(pann.value(), ns);
/*      */     } 
/* 1086 */     if (useDefault || _hasOneOf(a, (Class[])ANNOTATIONS_TO_INFER_SER)) {
/* 1087 */       return PropertyName.USE_DEFAULT;
/*      */     }
/* 1089 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean hasAsKey(MapperConfig<?> config, Annotated a) {
/* 1094 */     JsonKey ann = (JsonKey)_findAnnotation(a, JsonKey.class);
/* 1095 */     if (ann == null) {
/* 1096 */       return null;
/*      */     }
/* 1098 */     return Boolean.valueOf(ann.value());
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean hasAsValue(Annotated a) {
/* 1103 */     JsonValue ann = (JsonValue)_findAnnotation(a, JsonValue.class);
/* 1104 */     if (ann == null) {
/* 1105 */       return null;
/*      */     }
/* 1107 */     return Boolean.valueOf(ann.value());
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean hasAnyGetter(Annotated a) {
/* 1112 */     JsonAnyGetter ann = (JsonAnyGetter)_findAnnotation(a, JsonAnyGetter.class);
/* 1113 */     if (ann == null) {
/* 1114 */       return null;
/*      */     }
/* 1116 */     return Boolean.valueOf(ann.enabled());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
/* 1123 */     return _hasAnnotation(am, JsonAnyGetter.class);
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public boolean hasAsValueAnnotation(AnnotatedMethod am) {
/* 1129 */     JsonValue ann = (JsonValue)_findAnnotation(am, JsonValue.class);
/*      */     
/* 1131 */     return (ann != null && ann.value());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findDeserializer(Annotated a) {
/* 1143 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/* 1144 */     if (ann != null) {
/*      */       
/* 1146 */       Class<? extends JsonDeserializer> deserClass = ann.using();
/* 1147 */       if (deserClass != JsonDeserializer.None.class) {
/* 1148 */         return deserClass;
/*      */       }
/*      */     } 
/* 1151 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findKeyDeserializer(Annotated a) {
/* 1157 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/* 1158 */     if (ann != null) {
/* 1159 */       Class<? extends KeyDeserializer> deserClass = ann.keyUsing();
/* 1160 */       if (deserClass != KeyDeserializer.None.class) {
/* 1161 */         return deserClass;
/*      */       }
/*      */     } 
/* 1164 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findContentDeserializer(Annotated a) {
/* 1170 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/* 1171 */     if (ann != null) {
/*      */       
/* 1173 */       Class<? extends JsonDeserializer> deserClass = ann.contentUsing();
/* 1174 */       if (deserClass != JsonDeserializer.None.class) {
/* 1175 */         return deserClass;
/*      */       }
/*      */     } 
/* 1178 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findDeserializationConverter(Annotated a) {
/* 1184 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/* 1185 */     return (ann == null) ? null : _classIfExplicit(ann.converter(), Converter.None.class);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findDeserializationContentConverter(AnnotatedMember a) {
/* 1191 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/* 1192 */     return (ann == null) ? null : _classIfExplicit(ann.contentConverter(), Converter.None.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
/*      */     MapLikeType mapLikeType;
/* 1205 */     JavaType javaType1, type = baseType;
/* 1206 */     TypeFactory tf = config.getTypeFactory();
/*      */     
/* 1208 */     JsonDeserialize jsonDeser = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/*      */ 
/*      */     
/* 1211 */     Class<?> valueClass = (jsonDeser == null) ? null : _classIfExplicit(jsonDeser.as());
/* 1212 */     if (valueClass != null && !type.hasRawClass(valueClass) && 
/* 1213 */       !_primitiveAndWrapper(type, valueClass)) {
/*      */       try {
/* 1215 */         type = tf.constructSpecializedType(type, valueClass);
/* 1216 */       } catch (IllegalArgumentException iae) {
/* 1217 */         throw _databindException(iae, 
/* 1218 */             String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s", new Object[] {
/* 1219 */                 type, valueClass.getName(), a.getName(), iae.getMessage()
/*      */               }));
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1225 */     if (type.isMapLikeType()) {
/* 1226 */       JavaType keyType = type.getKeyType();
/* 1227 */       Class<?> keyClass = (jsonDeser == null) ? null : _classIfExplicit(jsonDeser.keyAs());
/* 1228 */       if (keyClass != null && 
/* 1229 */         !_primitiveAndWrapper(keyType, keyClass))
/*      */         try {
/* 1231 */           keyType = tf.constructSpecializedType(keyType, keyClass);
/* 1232 */           mapLikeType = ((MapLikeType)type).withKeyType(keyType);
/* 1233 */         } catch (IllegalArgumentException iae) {
/* 1234 */           throw _databindException(iae, 
/* 1235 */               String.format("Failed to narrow key type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[] {
/* 1236 */                   mapLikeType, keyClass.getName(), a.getName(), iae.getMessage()
/*      */                 }));
/*      */         }  
/*      */     } 
/* 1240 */     JavaType contentType = mapLikeType.getContentType();
/* 1241 */     if (contentType != null) {
/*      */       
/* 1243 */       Class<?> contentClass = (jsonDeser == null) ? null : _classIfExplicit(jsonDeser.contentAs());
/* 1244 */       if (contentClass != null && 
/* 1245 */         !_primitiveAndWrapper(contentType, contentClass))
/*      */         try {
/* 1247 */           contentType = tf.constructSpecializedType(contentType, contentClass);
/* 1248 */           javaType1 = mapLikeType.withContentType(contentType);
/* 1249 */         } catch (IllegalArgumentException iae) {
/* 1250 */           throw _databindException(iae, 
/* 1251 */               String.format("Failed to narrow value type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[] {
/* 1252 */                   javaType1, contentClass.getName(), a.getName(), iae.getMessage()
/*      */                 }));
/*      */         }  
/*      */     } 
/* 1256 */     return javaType1;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType) {
/* 1262 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Class<?> findDeserializationType(Annotated am, JavaType baseType) {
/* 1268 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType) {
/* 1274 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object findValueInstantiator(AnnotatedClass ac) {
/* 1286 */     JsonValueInstantiator ann = (JsonValueInstantiator)_findAnnotation(ac, JsonValueInstantiator.class);
/*      */     
/* 1288 */     return (ann == null) ? null : ann.value();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Class<?> findPOJOBuilder(AnnotatedClass ac) {
/* 1294 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(ac, JsonDeserialize.class);
/* 1295 */     return (ann == null) ? null : _classIfExplicit(ann.builder());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
/* 1301 */     JsonPOJOBuilder ann = (JsonPOJOBuilder)_findAnnotation(ac, JsonPOJOBuilder.class);
/* 1302 */     return (ann == null) ? null : new JsonPOJOBuilder.Value(ann);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PropertyName findNameForDeserialization(Annotated a) {
/* 1316 */     boolean useDefault = false;
/* 1317 */     JsonSetter js = (JsonSetter)_findAnnotation(a, JsonSetter.class);
/* 1318 */     if (js != null) {
/* 1319 */       String s = js.value();
/*      */       
/* 1321 */       if (s.isEmpty()) {
/* 1322 */         useDefault = true;
/*      */       } else {
/* 1324 */         return PropertyName.construct(s);
/*      */       } 
/*      */     } 
/* 1327 */     JsonProperty pann = (JsonProperty)_findAnnotation(a, JsonProperty.class);
/* 1328 */     if (pann != null) {
/*      */       
/* 1330 */       String ns = pann.namespace();
/* 1331 */       if (ns != null && ns.isEmpty()) {
/* 1332 */         ns = null;
/*      */       }
/* 1334 */       return PropertyName.construct(pann.value(), ns);
/*      */     } 
/* 1336 */     if (useDefault || _hasOneOf(a, (Class[])ANNOTATIONS_TO_INFER_DESER)) {
/* 1337 */       return PropertyName.USE_DEFAULT;
/*      */     }
/* 1339 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean hasAnySetter(Annotated a) {
/* 1344 */     JsonAnySetter ann = (JsonAnySetter)_findAnnotation(a, JsonAnySetter.class);
/* 1345 */     return (ann == null) ? null : Boolean.valueOf(ann.enabled());
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonSetter.Value findSetterInfo(Annotated a) {
/* 1350 */     return JsonSetter.Value.from((JsonSetter)_findAnnotation(a, JsonSetter.class));
/*      */   }
/*      */ 
/*      */   
/*      */   public Boolean findMergeInfo(Annotated a) {
/* 1355 */     JsonMerge ann = (JsonMerge)_findAnnotation(a, JsonMerge.class);
/* 1356 */     return (ann == null) ? null : ann.value().asBoolean();
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
/* 1362 */     return _hasAnnotation(am, JsonAnySetter.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public boolean hasCreatorAnnotation(Annotated a) {
/* 1372 */     JsonCreator ann = (JsonCreator)_findAnnotation(a, JsonCreator.class);
/* 1373 */     if (ann != null) {
/* 1374 */       return (ann.mode() != JsonCreator.Mode.DISABLED);
/*      */     }
/*      */ 
/*      */     
/* 1378 */     if (this._cfgConstructorPropertiesImpliesCreator && 
/* 1379 */       a instanceof AnnotatedConstructor && 
/* 1380 */       _java7Helper != null) {
/* 1381 */       Boolean b = _java7Helper.hasCreatorAnnotation(a);
/* 1382 */       if (b != null) {
/* 1383 */         return b.booleanValue();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1388 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public JsonCreator.Mode findCreatorBinding(Annotated a) {
/* 1394 */     JsonCreator ann = (JsonCreator)_findAnnotation(a, JsonCreator.class);
/* 1395 */     return (ann == null) ? null : ann.mode();
/*      */   }
/*      */ 
/*      */   
/*      */   public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
/* 1400 */     JsonCreator ann = (JsonCreator)_findAnnotation(a, JsonCreator.class);
/* 1401 */     if (ann != null) {
/* 1402 */       return ann.mode();
/*      */     }
/* 1404 */     if (this._cfgConstructorPropertiesImpliesCreator && config
/* 1405 */       .isEnabled(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES))
/*      */     {
/* 1407 */       if (a instanceof AnnotatedConstructor && 
/* 1408 */         _java7Helper != null) {
/* 1409 */         Boolean b = _java7Helper.hasCreatorAnnotation(a);
/* 1410 */         if (b != null && b.booleanValue())
/*      */         {
/*      */           
/* 1413 */           return JsonCreator.Mode.PROPERTIES;
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/* 1418 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean _isIgnorable(Annotated a) {
/* 1429 */     JsonIgnore ann = (JsonIgnore)_findAnnotation(a, JsonIgnore.class);
/* 1430 */     if (ann != null) {
/* 1431 */       return ann.value();
/*      */     }
/* 1433 */     if (_java7Helper != null) {
/* 1434 */       Boolean b = _java7Helper.findTransient(a);
/* 1435 */       if (b != null) {
/* 1436 */         return b.booleanValue();
/*      */       }
/*      */     } 
/* 1439 */     return false;
/*      */   }
/*      */   
/*      */   protected Class<?> _classIfExplicit(Class<?> cls) {
/* 1443 */     if (cls == null || ClassUtil.isBogusClass(cls)) {
/* 1444 */       return null;
/*      */     }
/* 1446 */     return cls;
/*      */   }
/*      */   
/*      */   protected Class<?> _classIfExplicit(Class<?> cls, Class<?> implicit) {
/* 1450 */     cls = _classIfExplicit(cls);
/* 1451 */     return (cls == null || cls == implicit) ? null : cls;
/*      */   }
/*      */   
/*      */   protected PropertyName _propertyName(String localName, String namespace) {
/* 1455 */     if (localName.isEmpty()) {
/* 1456 */       return PropertyName.USE_DEFAULT;
/*      */     }
/* 1458 */     if (namespace == null || namespace.isEmpty()) {
/* 1459 */       return PropertyName.construct(localName);
/*      */     }
/* 1461 */     return PropertyName.construct(localName, namespace);
/*      */   }
/*      */ 
/*      */   
/*      */   protected PropertyName _findConstructorName(Annotated a) {
/* 1466 */     if (a instanceof AnnotatedParameter) {
/* 1467 */       AnnotatedParameter p = (AnnotatedParameter)a;
/* 1468 */       AnnotatedWithParams ctor = p.getOwner();
/*      */       
/* 1470 */       if (ctor != null && 
/* 1471 */         _java7Helper != null) {
/* 1472 */         PropertyName name = _java7Helper.findConstructorName(p);
/* 1473 */         if (name != null) {
/* 1474 */           return name;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1479 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType) {
/*      */     StdTypeResolverBuilder stdTypeResolverBuilder;
/* 1492 */     JsonTypeInfo info = (JsonTypeInfo)_findAnnotation(ann, JsonTypeInfo.class);
/* 1493 */     JsonTypeResolver resAnn = (JsonTypeResolver)_findAnnotation(ann, JsonTypeResolver.class);
/*      */     
/* 1495 */     if (resAnn != null) {
/* 1496 */       if (info == null) {
/* 1497 */         return null;
/*      */       }
/*      */ 
/*      */       
/* 1501 */       TypeResolverBuilder<?> b = config.typeResolverBuilderInstance(ann, resAnn.value());
/*      */     } else {
/* 1503 */       if (info == null) {
/* 1504 */         return null;
/*      */       }
/*      */       
/* 1507 */       if (info.use() == JsonTypeInfo.Id.NONE) {
/* 1508 */         return (TypeResolverBuilder<?>)_constructNoTypeResolverBuilder();
/*      */       }
/* 1510 */       stdTypeResolverBuilder = _constructStdTypeResolverBuilder();
/*      */     } 
/*      */     
/* 1513 */     JsonTypeIdResolver idResInfo = (JsonTypeIdResolver)_findAnnotation(ann, JsonTypeIdResolver.class);
/*      */     
/* 1515 */     TypeIdResolver idRes = (idResInfo == null) ? null : config.typeIdResolverInstance(ann, idResInfo.value());
/* 1516 */     if (idRes != null) {
/* 1517 */       idRes.init(baseType);
/*      */     }
/* 1519 */     TypeResolverBuilder<?> typeResolverBuilder = stdTypeResolverBuilder.init(info.use(), idRes);
/*      */ 
/*      */ 
/*      */     
/* 1523 */     JsonTypeInfo.As inclusion = info.include();
/* 1524 */     if (inclusion == JsonTypeInfo.As.EXTERNAL_PROPERTY && ann instanceof AnnotatedClass) {
/* 1525 */       inclusion = JsonTypeInfo.As.PROPERTY;
/*      */     }
/* 1527 */     typeResolverBuilder = typeResolverBuilder.inclusion(inclusion);
/* 1528 */     typeResolverBuilder = typeResolverBuilder.typeProperty(info.property());
/* 1529 */     Class<?> defaultImpl = info.defaultImpl();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1535 */     if (defaultImpl != JsonTypeInfo.None.class && !defaultImpl.isAnnotation()) {
/* 1536 */       typeResolverBuilder = typeResolverBuilder.defaultImpl(defaultImpl);
/*      */     }
/* 1538 */     typeResolverBuilder = typeResolverBuilder.typeIdVisibility(info.visible());
/* 1539 */     return typeResolverBuilder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected StdTypeResolverBuilder _constructStdTypeResolverBuilder() {
/* 1547 */     return new StdTypeResolverBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected StdTypeResolverBuilder _constructNoTypeResolverBuilder() {
/* 1555 */     return StdTypeResolverBuilder.noTypeInfoBuilder();
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean _primitiveAndWrapper(Class<?> baseType, Class<?> refinement) {
/* 1560 */     if (baseType.isPrimitive()) {
/* 1561 */       return (baseType == ClassUtil.primitiveType(refinement));
/*      */     }
/* 1563 */     if (refinement.isPrimitive()) {
/* 1564 */       return (refinement == ClassUtil.primitiveType(baseType));
/*      */     }
/* 1566 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean _primitiveAndWrapper(JavaType baseType, Class<?> refinement) {
/* 1571 */     if (baseType.isPrimitive()) {
/* 1572 */       return baseType.hasRawClass(ClassUtil.primitiveType(refinement));
/*      */     }
/* 1574 */     if (refinement.isPrimitive()) {
/* 1575 */       return (refinement == ClassUtil.primitiveType(baseType.getRawClass()));
/*      */     }
/* 1577 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private JsonMappingException _databindException(String msg) {
/* 1582 */     return new JsonMappingException(null, msg);
/*      */   }
/*      */ 
/*      */   
/*      */   private JsonMappingException _databindException(Throwable t, String msg) {
/* 1587 */     return new JsonMappingException(null, msg, t);
/*      */   }
/*      */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/JacksonAnnotationIntrospector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */