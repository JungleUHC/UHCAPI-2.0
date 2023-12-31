/*     */ package com.fasterxml.jackson.databind.introspect;
/*     */ import com.fasterxml.jackson.annotation.JsonCreator;
/*     */ import com.fasterxml.jackson.annotation.JsonFormat;
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
/*     */ import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*     */ import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*     */ import com.fasterxml.jackson.databind.util.Annotations;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.Converter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class BasicBeanDescription extends BeanDescription {
/*  31 */   private static final Class<?>[] NO_VIEWS = new Class[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final POJOPropertiesCollector _propCollector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final MapperConfig<?> _config;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AnnotationIntrospector _annotationIntrospector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AnnotatedClass _classInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Class<?>[] _defaultViews;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _defaultViewsResolved;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<BeanPropertyDefinition> _properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ObjectIdInfo _objectIdInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BasicBeanDescription(POJOPropertiesCollector coll, JavaType type, AnnotatedClass classDef) {
/*  96 */     super(type);
/*  97 */     this._propCollector = coll;
/*  98 */     this._config = coll.getConfig();
/*     */     
/* 100 */     if (this._config == null) {
/* 101 */       this._annotationIntrospector = null;
/*     */     } else {
/* 103 */       this._annotationIntrospector = this._config.getAnnotationIntrospector();
/*     */     } 
/* 105 */     this._classInfo = classDef;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BasicBeanDescription(MapperConfig<?> config, JavaType type, AnnotatedClass classDef, List<BeanPropertyDefinition> props) {
/* 115 */     super(type);
/* 116 */     this._propCollector = null;
/* 117 */     this._config = config;
/*     */     
/* 119 */     if (this._config == null) {
/* 120 */       this._annotationIntrospector = null;
/*     */     } else {
/* 122 */       this._annotationIntrospector = this._config.getAnnotationIntrospector();
/*     */     } 
/* 124 */     this._classInfo = classDef;
/* 125 */     this._properties = props;
/*     */   }
/*     */ 
/*     */   
/*     */   protected BasicBeanDescription(POJOPropertiesCollector coll) {
/* 130 */     this(coll, coll.getType(), coll.getClassDef());
/* 131 */     this._objectIdInfo = coll.getObjectIdInfo();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BasicBeanDescription forDeserialization(POJOPropertiesCollector coll) {
/* 139 */     return new BasicBeanDescription(coll);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BasicBeanDescription forSerialization(POJOPropertiesCollector coll) {
/* 147 */     return new BasicBeanDescription(coll);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BasicBeanDescription forOtherUse(MapperConfig<?> config, JavaType type, AnnotatedClass ac) {
/* 158 */     return new BasicBeanDescription(config, type, ac, 
/* 159 */         Collections.emptyList());
/*     */   }
/*     */   
/*     */   protected List<BeanPropertyDefinition> _properties() {
/* 163 */     if (this._properties == null) {
/* 164 */       this._properties = this._propCollector.getProperties();
/*     */     }
/* 166 */     return this._properties;
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
/*     */   public boolean removeProperty(String propName) {
/* 184 */     Iterator<BeanPropertyDefinition> it = _properties().iterator();
/* 185 */     while (it.hasNext()) {
/* 186 */       BeanPropertyDefinition prop = it.next();
/* 187 */       if (prop.getName().equals(propName)) {
/* 188 */         it.remove();
/* 189 */         return true;
/*     */       } 
/*     */     } 
/* 192 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addProperty(BeanPropertyDefinition def) {
/* 198 */     if (hasProperty(def.getFullName())) {
/* 199 */       return false;
/*     */     }
/* 201 */     _properties().add(def);
/* 202 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasProperty(PropertyName name) {
/* 209 */     return (findProperty(name) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanPropertyDefinition findProperty(PropertyName name) {
/* 217 */     for (BeanPropertyDefinition prop : _properties()) {
/* 218 */       if (prop.hasName(name)) {
/* 219 */         return prop;
/*     */       }
/*     */     } 
/* 222 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedClass getClassInfo() {
/* 232 */     return this._classInfo;
/*     */   }
/*     */   public ObjectIdInfo getObjectIdInfo() {
/* 235 */     return this._objectIdInfo;
/*     */   }
/*     */   
/*     */   public List<BeanPropertyDefinition> findProperties() {
/* 239 */     return _properties();
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedMember findJsonKeyAccessor() {
/* 244 */     return (this._propCollector == null) ? null : this._propCollector
/* 245 */       .getJsonKeyAccessor();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public AnnotatedMethod findJsonValueMethod() {
/* 251 */     return (this._propCollector == null) ? null : this._propCollector
/* 252 */       .getJsonValueMethod();
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedMember findJsonValueAccessor() {
/* 257 */     return (this._propCollector == null) ? null : this._propCollector
/* 258 */       .getJsonValueAccessor();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getIgnoredPropertyNames() {
/* 264 */     Set<String> ign = (this._propCollector == null) ? null : this._propCollector.getIgnoredPropertyNames();
/* 265 */     if (ign == null) {
/* 266 */       return Collections.emptySet();
/*     */     }
/* 268 */     return ign;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasKnownClassAnnotations() {
/* 273 */     return this._classInfo.hasAnnotations();
/*     */   }
/*     */ 
/*     */   
/*     */   public Annotations getClassAnnotations() {
/* 278 */     return this._classInfo.getAnnotations();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public TypeBindings bindingsForBeanType() {
/* 284 */     return this._type.getBindings();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JavaType resolveType(Type jdkType) {
/* 292 */     return this._config.getTypeFactory().resolveMemberType(jdkType, this._type.getBindings());
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedConstructor findDefaultConstructor() {
/* 297 */     return this._classInfo.getDefaultConstructor();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedMember findAnySetterAccessor() throws IllegalArgumentException {
/* 303 */     if (this._propCollector != null) {
/* 304 */       AnnotatedMethod anyMethod = this._propCollector.getAnySetterMethod();
/* 305 */       if (anyMethod != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 312 */         Class<?> type = anyMethod.getRawParameterType(0);
/* 313 */         if (type != String.class && type != Object.class) {
/* 314 */           throw new IllegalArgumentException(String.format("Invalid 'any-setter' annotation on method '%s()': first argument not of type String or Object, but %s", new Object[] { anyMethod
/*     */                   
/* 316 */                   .getName(), type.getName() }));
/*     */         }
/* 318 */         return anyMethod;
/*     */       } 
/* 320 */       AnnotatedMember anyField = this._propCollector.getAnySetterField();
/* 321 */       if (anyField != null) {
/*     */ 
/*     */         
/* 324 */         Class<?> type = anyField.getRawType();
/* 325 */         if (!Map.class.isAssignableFrom(type)) {
/* 326 */           throw new IllegalArgumentException(String.format("Invalid 'any-setter' annotation on field '%s': type is not instance of java.util.Map", new Object[] { anyField
/*     */                   
/* 328 */                   .getName() }));
/*     */         }
/* 330 */         return anyField;
/*     */       } 
/*     */     } 
/* 333 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<Object, AnnotatedMember> findInjectables() {
/* 338 */     if (this._propCollector != null) {
/* 339 */       return this._propCollector.getInjectables();
/*     */     }
/* 341 */     return Collections.emptyMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<AnnotatedConstructor> getConstructors() {
/* 346 */     return this._classInfo.getConstructors();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode>> getConstructorsWithMode() {
/* 351 */     List<AnnotatedConstructor> allCtors = this._classInfo.getConstructors();
/* 352 */     if (allCtors.isEmpty()) {
/* 353 */       return Collections.emptyList();
/*     */     }
/* 355 */     List<AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode>> result = new ArrayList<>();
/* 356 */     for (AnnotatedConstructor ctor : allCtors) {
/* 357 */       JsonCreator.Mode mode = this._annotationIntrospector.findCreatorAnnotation(this._config, ctor);
/* 358 */       if (mode == JsonCreator.Mode.DISABLED) {
/*     */         continue;
/*     */       }
/* 361 */       result.add(AnnotatedAndMetadata.of(ctor, mode));
/*     */     } 
/* 363 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object instantiateBean(boolean fixAccess) {
/* 368 */     AnnotatedConstructor ac = this._classInfo.getDefaultConstructor();
/* 369 */     if (ac == null) {
/* 370 */       return null;
/*     */     }
/* 372 */     if (fixAccess) {
/* 373 */       ac.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/*     */     try {
/* 376 */       return ac.call();
/* 377 */     } catch (Exception e) {
/* 378 */       Throwable t = e;
/* 379 */       while (t.getCause() != null) {
/* 380 */         t = t.getCause();
/*     */       }
/* 382 */       ClassUtil.throwIfError(t);
/* 383 */       ClassUtil.throwIfRTE(t);
/* 384 */       throw new IllegalArgumentException("Failed to instantiate bean of type " + this._classInfo
/* 385 */           .getAnnotated().getName() + ": (" + t.getClass().getName() + ") " + 
/* 386 */           ClassUtil.exceptionMessage(t), t);
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
/*     */   public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
/* 398 */     return this._classInfo.findMethod(name, paramTypes);
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
/*     */   public JsonFormat.Value findExpectedFormat(JsonFormat.Value defValue) {
/* 412 */     if (this._annotationIntrospector != null) {
/* 413 */       JsonFormat.Value value = this._annotationIntrospector.findFormat(this._classInfo);
/* 414 */       if (value != null) {
/* 415 */         if (defValue == null) {
/* 416 */           defValue = value;
/*     */         } else {
/* 418 */           defValue = defValue.withOverrides(value);
/*     */         } 
/*     */       }
/*     */     } 
/* 422 */     JsonFormat.Value v = this._config.getDefaultPropertyFormat(this._classInfo.getRawType());
/* 423 */     if (v != null) {
/* 424 */       if (defValue == null) {
/* 425 */         defValue = v;
/*     */       } else {
/* 427 */         defValue = defValue.withOverrides(v);
/*     */       } 
/*     */     }
/* 430 */     return defValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?>[] findDefaultViews() {
/* 436 */     if (!this._defaultViewsResolved) {
/* 437 */       this._defaultViewsResolved = true;
/*     */       
/* 439 */       Class<?>[] def = (this._annotationIntrospector == null) ? null : this._annotationIntrospector.findViews(this._classInfo);
/*     */       
/* 441 */       if (def == null && 
/* 442 */         !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
/* 443 */         def = NO_VIEWS;
/*     */       }
/*     */       
/* 446 */       this._defaultViews = def;
/*     */     } 
/* 448 */     return this._defaultViews;
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
/*     */   public Converter<Object, Object> findSerializationConverter() {
/* 460 */     if (this._annotationIntrospector == null) {
/* 461 */       return null;
/*     */     }
/* 463 */     return _createConverter(this._annotationIntrospector.findSerializationConverter(this._classInfo));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonInclude.Value findPropertyInclusion(JsonInclude.Value defValue) {
/* 474 */     if (this._annotationIntrospector != null) {
/* 475 */       JsonInclude.Value incl = this._annotationIntrospector.findPropertyInclusion(this._classInfo);
/* 476 */       if (incl != null) {
/* 477 */         return (defValue == null) ? incl : defValue.withOverrides(incl);
/*     */       }
/*     */     } 
/* 480 */     return defValue;
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
/*     */   public AnnotatedMember findAnyGetter() throws IllegalArgumentException {
/* 492 */     if (this._propCollector != null) {
/* 493 */       AnnotatedMember anyGetter = this._propCollector.getAnyGetterMethod();
/* 494 */       if (anyGetter != null) {
/*     */ 
/*     */         
/* 497 */         Class<?> type = anyGetter.getRawType();
/* 498 */         if (!Map.class.isAssignableFrom(type)) {
/* 499 */           throw new IllegalArgumentException(String.format("Invalid 'any-getter' annotation on method %s(): return type is not instance of java.util.Map", new Object[] { anyGetter
/*     */                   
/* 501 */                   .getName() }));
/*     */         }
/* 503 */         return anyGetter;
/*     */       } 
/*     */       
/* 506 */       AnnotatedMember anyField = this._propCollector.getAnyGetterField();
/* 507 */       if (anyField != null) {
/*     */ 
/*     */         
/* 510 */         Class<?> type = anyField.getRawType();
/* 511 */         if (!Map.class.isAssignableFrom(type)) {
/* 512 */           throw new IllegalArgumentException(String.format("Invalid 'any-getter' annotation on field '%s': type is not instance of java.util.Map", new Object[] { anyField
/*     */                   
/* 514 */                   .getName() }));
/*     */         }
/* 516 */         return anyField;
/*     */       } 
/*     */     } 
/* 519 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<BeanPropertyDefinition> findBackReferences() {
/* 525 */     List<BeanPropertyDefinition> result = null;
/* 526 */     HashSet<String> names = null;
/* 527 */     for (BeanPropertyDefinition property : _properties()) {
/* 528 */       AnnotationIntrospector.ReferenceProperty refDef = property.findReferenceType();
/* 529 */       if (refDef == null || !refDef.isBackReference()) {
/*     */         continue;
/*     */       }
/* 532 */       String refName = refDef.getName();
/* 533 */       if (result == null) {
/* 534 */         result = new ArrayList<>();
/* 535 */         names = new HashSet<>();
/* 536 */         names.add(refName);
/*     */       }
/* 538 */       else if (!names.add(refName)) {
/* 539 */         throw new IllegalArgumentException("Multiple back-reference properties with name " + ClassUtil.name(refName));
/*     */       } 
/*     */       
/* 542 */       result.add(property);
/*     */     } 
/* 544 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Map<String, AnnotatedMember> findBackReferenceProperties() {
/* 551 */     List<BeanPropertyDefinition> props = findBackReferences();
/* 552 */     if (props == null) {
/* 553 */       return null;
/*     */     }
/* 555 */     Map<String, AnnotatedMember> result = new HashMap<>();
/* 556 */     for (BeanPropertyDefinition prop : props) {
/* 557 */       result.put(prop.getName(), prop.getMutator());
/*     */     }
/* 559 */     return result;
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
/*     */   public List<AnnotatedMethod> getFactoryMethods() {
/* 572 */     List<AnnotatedMethod> candidates = this._classInfo.getFactoryMethods();
/* 573 */     if (candidates.isEmpty()) {
/* 574 */       return candidates;
/*     */     }
/* 576 */     List<AnnotatedMethod> result = null;
/* 577 */     for (AnnotatedMethod am : candidates) {
/* 578 */       if (isFactoryMethod(am)) {
/* 579 */         if (result == null) {
/* 580 */           result = new ArrayList<>();
/*     */         }
/* 582 */         result.add(am);
/*     */       } 
/*     */     } 
/* 585 */     if (result == null) {
/* 586 */       return Collections.emptyList();
/*     */     }
/* 588 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode>> getFactoryMethodsWithMode() {
/* 594 */     List<AnnotatedMethod> candidates = this._classInfo.getFactoryMethods();
/* 595 */     if (candidates.isEmpty()) {
/* 596 */       return Collections.emptyList();
/*     */     }
/* 598 */     List<AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode>> result = null;
/* 599 */     for (AnnotatedMethod am : candidates) {
/*     */       
/* 601 */       AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode> match = findFactoryMethodMetadata(am);
/* 602 */       if (match != null) {
/* 603 */         if (result == null) {
/* 604 */           result = new ArrayList<>();
/*     */         }
/* 606 */         result.add(match);
/*     */       } 
/*     */     } 
/* 609 */     if (result == null) {
/* 610 */       return Collections.emptyList();
/*     */     }
/* 612 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Constructor<?> findSingleArgConstructor(Class<?>... argTypes) {
/* 619 */     for (AnnotatedConstructor ac : this._classInfo.getConstructors()) {
/*     */       
/* 621 */       if (ac.getParameterCount() == 1) {
/* 622 */         Class<?> actArg = ac.getRawParameterType(0);
/* 623 */         for (Class<?> expArg : argTypes) {
/* 624 */           if (expArg == actArg) {
/* 625 */             return ac.getAnnotated();
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 630 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Method findFactoryMethod(Class<?>... expArgTypes) {
/* 638 */     for (AnnotatedMethod am : this._classInfo.getFactoryMethods()) {
/*     */       
/* 640 */       if (isFactoryMethod(am) && am.getParameterCount() == 1) {
/*     */         
/* 642 */         Class<?> actualArgType = am.getRawParameterType(0);
/* 643 */         for (Class<?> expArgType : expArgTypes) {
/*     */           
/* 645 */           if (actualArgType.isAssignableFrom(expArgType)) {
/* 646 */             return am.getAnnotated();
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 651 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isFactoryMethod(AnnotatedMethod am) {
/* 658 */     Class<?> rt = am.getRawReturnType();
/* 659 */     if (!getBeanClass().isAssignableFrom(rt)) {
/* 660 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 667 */     JsonCreator.Mode mode = this._annotationIntrospector.findCreatorAnnotation(this._config, am);
/* 668 */     if (mode != null && mode != JsonCreator.Mode.DISABLED) {
/* 669 */       return true;
/*     */     }
/* 671 */     String name = am.getName();
/*     */     
/* 673 */     if ("valueOf".equals(name) && 
/* 674 */       am.getParameterCount() == 1) {
/* 675 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 679 */     if ("fromString".equals(name) && 
/* 680 */       am.getParameterCount() == 1) {
/* 681 */       Class<?> cls = am.getRawParameterType(0);
/* 682 */       if (cls == String.class || CharSequence.class.isAssignableFrom(cls)) {
/* 683 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 687 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode> findFactoryMethodMetadata(AnnotatedMethod am) {
/* 695 */     Class<?> rt = am.getRawReturnType();
/* 696 */     if (!getBeanClass().isAssignableFrom(rt)) {
/* 697 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 703 */     JsonCreator.Mode mode = this._annotationIntrospector.findCreatorAnnotation(this._config, am);
/* 704 */     if (mode != null) {
/* 705 */       if (mode == JsonCreator.Mode.DISABLED) {
/* 706 */         return null;
/*     */       }
/* 708 */       return AnnotatedAndMetadata.of(am, mode);
/*     */     } 
/* 710 */     String name = am.getName();
/*     */     
/* 712 */     if ("valueOf".equals(name) && 
/* 713 */       am.getParameterCount() == 1) {
/* 714 */       return AnnotatedAndMetadata.of(am, mode);
/*     */     }
/*     */ 
/*     */     
/* 718 */     if ("fromString".equals(name) && 
/* 719 */       am.getParameterCount() == 1) {
/* 720 */       Class<?> cls = am.getRawParameterType(0);
/* 721 */       if (cls == String.class || CharSequence.class.isAssignableFrom(cls)) {
/* 722 */         return AnnotatedAndMetadata.of(am, mode);
/*     */       }
/*     */     } 
/*     */     
/* 726 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected PropertyName _findCreatorPropertyName(AnnotatedParameter param) {
/* 735 */     PropertyName name = this._annotationIntrospector.findNameForDeserialization(param);
/* 736 */     if (name == null || name.isEmpty()) {
/* 737 */       String str = this._annotationIntrospector.findImplicitPropertyName(param);
/* 738 */       if (str != null && !str.isEmpty()) {
/* 739 */         name = PropertyName.construct(str);
/*     */       }
/*     */     } 
/* 742 */     return name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> findPOJOBuilder() {
/* 753 */     return (this._annotationIntrospector == null) ? null : this._annotationIntrospector
/* 754 */       .findPOJOBuilder(this._classInfo);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPOJOBuilder.Value findPOJOBuilderConfig() {
/* 760 */     return (this._annotationIntrospector == null) ? null : this._annotationIntrospector
/* 761 */       .findPOJOBuilderConfig(this._classInfo);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Converter<Object, Object> findDeserializationConverter() {
/* 767 */     if (this._annotationIntrospector == null) {
/* 768 */       return null;
/*     */     }
/* 770 */     return _createConverter(this._annotationIntrospector.findDeserializationConverter(this._classInfo));
/*     */   }
/*     */ 
/*     */   
/*     */   public String findClassDescription() {
/* 775 */     return (this._annotationIntrospector == null) ? null : this._annotationIntrospector
/* 776 */       .findClassDescription(this._classInfo);
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
/*     */   @Deprecated
/*     */   public LinkedHashMap<String, AnnotatedField> _findPropertyFields(Collection<String> ignoredProperties, boolean forSerialization) {
/* 801 */     LinkedHashMap<String, AnnotatedField> results = new LinkedHashMap<>();
/* 802 */     for (BeanPropertyDefinition property : _properties()) {
/* 803 */       AnnotatedField f = property.getField();
/* 804 */       if (f != null) {
/* 805 */         String name = property.getName();
/* 806 */         if (ignoredProperties != null && 
/* 807 */           ignoredProperties.contains(name)) {
/*     */           continue;
/*     */         }
/*     */         
/* 811 */         results.put(name, f);
/*     */       } 
/*     */     } 
/* 814 */     return results;
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
/*     */   protected Converter<Object, Object> _createConverter(Object converterDef) {
/* 826 */     if (converterDef == null) {
/* 827 */       return null;
/*     */     }
/* 829 */     if (converterDef instanceof Converter) {
/* 830 */       return (Converter<Object, Object>)converterDef;
/*     */     }
/* 832 */     if (!(converterDef instanceof Class)) {
/* 833 */       throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef
/* 834 */           .getClass().getName() + "; expected type Converter or Class<Converter> instead");
/*     */     }
/* 836 */     Class<?> converterClass = (Class)converterDef;
/*     */     
/* 838 */     if (converterClass == Converter.None.class || ClassUtil.isBogusClass(converterClass)) {
/* 839 */       return null;
/*     */     }
/* 841 */     if (!Converter.class.isAssignableFrom(converterClass)) {
/* 842 */       throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass
/* 843 */           .getName() + "; expected Class<Converter>");
/*     */     }
/* 845 */     HandlerInstantiator hi = this._config.getHandlerInstantiator();
/* 846 */     Converter<?, ?> conv = (hi == null) ? null : hi.converterInstance(this._config, this._classInfo, converterClass);
/* 847 */     if (conv == null) {
/* 848 */       conv = (Converter<?, ?>)ClassUtil.createInstance(converterClass, this._config
/* 849 */           .canOverrideAccessModifiers());
/*     */     }
/* 851 */     return (Converter)conv;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/introspect/BasicBeanDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */