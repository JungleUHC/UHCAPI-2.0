/*     */ package com.fasterxml.jackson.databind.ser;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonInclude;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.io.SerializedString;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.JsonSerializer;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.PropertyMetadata;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.SerializationConfig;
/*     */ import com.fasterxml.jackson.databind.SerializationFeature;
/*     */ import com.fasterxml.jackson.databind.SerializerProvider;
/*     */ import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
/*     */ import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
/*     */ import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.fasterxml.jackson.databind.node.ObjectNode;
/*     */ import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
/*     */ import com.fasterxml.jackson.databind.util.Annotations;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class BeanPropertyWriter
/*     */   extends PropertyWriter
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  49 */   public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SerializedString _name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final PropertyName _wrapperName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JavaType _declaredType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JavaType _cfgSerializationType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JavaType _nonTrivialBaseType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final transient Annotations _contextAnnotations;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AnnotatedMember _member;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient Method _accessorMethod;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient Field _field;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonSerializer<Object> _serializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonSerializer<Object> _nullSerializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TypeSerializer _typeSerializer;
/*     */ 
/*     */ 
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
/*     */   
/*     */   protected final Object _suppressableValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Class<?>[] _includeInViews;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient HashMap<Object, Object> _internalSettings;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanPropertyWriter(BeanPropertyDefinition propDef, AnnotatedMember member, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, boolean suppressNulls, Object suppressableValue, Class<?>[] includeInViews) {
/* 217 */     super(propDef);
/* 218 */     this._member = member;
/* 219 */     this._contextAnnotations = contextAnnotations;
/*     */     
/* 221 */     this._name = new SerializedString(propDef.getName());
/* 222 */     this._wrapperName = propDef.getWrapperName();
/*     */     
/* 224 */     this._declaredType = declaredType;
/* 225 */     this._serializer = (JsonSerializer)ser;
/* 226 */     this
/* 227 */       ._dynamicSerializers = (ser == null) ? PropertySerializerMap.emptyForProperties() : null;
/* 228 */     this._typeSerializer = typeSer;
/* 229 */     this._cfgSerializationType = serType;
/*     */     
/* 231 */     if (member instanceof com.fasterxml.jackson.databind.introspect.AnnotatedField) {
/* 232 */       this._accessorMethod = null;
/* 233 */       this._field = (Field)member.getMember();
/* 234 */     } else if (member instanceof com.fasterxml.jackson.databind.introspect.AnnotatedMethod) {
/* 235 */       this._accessorMethod = (Method)member.getMember();
/* 236 */       this._field = null;
/*     */     }
/*     */     else {
/*     */       
/* 240 */       this._accessorMethod = null;
/* 241 */       this._field = null;
/*     */     } 
/* 243 */     this._suppressNulls = suppressNulls;
/* 244 */     this._suppressableValue = suppressableValue;
/*     */ 
/*     */     
/* 247 */     this._nullSerializer = null;
/* 248 */     this._includeInViews = includeInViews;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public BeanPropertyWriter(BeanPropertyDefinition propDef, AnnotatedMember member, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, boolean suppressNulls, Object suppressableValue) {
/* 258 */     this(propDef, member, contextAnnotations, declaredType, ser, typeSer, serType, suppressNulls, suppressableValue, null);
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
/*     */   protected BeanPropertyWriter() {
/* 271 */     super(PropertyMetadata.STD_REQUIRED_OR_OPTIONAL);
/* 272 */     this._member = null;
/* 273 */     this._contextAnnotations = null;
/*     */     
/* 275 */     this._name = null;
/* 276 */     this._wrapperName = null;
/* 277 */     this._includeInViews = null;
/*     */     
/* 279 */     this._declaredType = null;
/* 280 */     this._serializer = null;
/* 281 */     this._dynamicSerializers = null;
/* 282 */     this._typeSerializer = null;
/* 283 */     this._cfgSerializationType = null;
/*     */     
/* 285 */     this._accessorMethod = null;
/* 286 */     this._field = null;
/* 287 */     this._suppressNulls = false;
/* 288 */     this._suppressableValue = null;
/*     */     
/* 290 */     this._nullSerializer = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanPropertyWriter(BeanPropertyWriter base) {
/* 297 */     this(base, base._name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanPropertyWriter(BeanPropertyWriter base, PropertyName name) {
/* 304 */     super(base);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 311 */     this._name = new SerializedString(name.getSimpleName());
/* 312 */     this._wrapperName = base._wrapperName;
/*     */     
/* 314 */     this._contextAnnotations = base._contextAnnotations;
/* 315 */     this._declaredType = base._declaredType;
/*     */     
/* 317 */     this._member = base._member;
/* 318 */     this._accessorMethod = base._accessorMethod;
/* 319 */     this._field = base._field;
/*     */     
/* 321 */     this._serializer = base._serializer;
/* 322 */     this._nullSerializer = base._nullSerializer;
/*     */     
/* 324 */     if (base._internalSettings != null) {
/* 325 */       this._internalSettings = new HashMap<>(base._internalSettings);
/*     */     }
/*     */     
/* 328 */     this._cfgSerializationType = base._cfgSerializationType;
/* 329 */     this._dynamicSerializers = base._dynamicSerializers;
/* 330 */     this._suppressNulls = base._suppressNulls;
/* 331 */     this._suppressableValue = base._suppressableValue;
/* 332 */     this._includeInViews = base._includeInViews;
/* 333 */     this._typeSerializer = base._typeSerializer;
/* 334 */     this._nonTrivialBaseType = base._nonTrivialBaseType;
/*     */   }
/*     */   
/*     */   protected BeanPropertyWriter(BeanPropertyWriter base, SerializedString name) {
/* 338 */     super(base);
/* 339 */     this._name = name;
/* 340 */     this._wrapperName = base._wrapperName;
/*     */     
/* 342 */     this._member = base._member;
/* 343 */     this._contextAnnotations = base._contextAnnotations;
/* 344 */     this._declaredType = base._declaredType;
/* 345 */     this._accessorMethod = base._accessorMethod;
/* 346 */     this._field = base._field;
/* 347 */     this._serializer = base._serializer;
/* 348 */     this._nullSerializer = base._nullSerializer;
/* 349 */     if (base._internalSettings != null) {
/* 350 */       this._internalSettings = new HashMap<>(base._internalSettings);
/*     */     }
/*     */     
/* 353 */     this._cfgSerializationType = base._cfgSerializationType;
/* 354 */     this._dynamicSerializers = base._dynamicSerializers;
/* 355 */     this._suppressNulls = base._suppressNulls;
/* 356 */     this._suppressableValue = base._suppressableValue;
/* 357 */     this._includeInViews = base._includeInViews;
/* 358 */     this._typeSerializer = base._typeSerializer;
/* 359 */     this._nonTrivialBaseType = base._nonTrivialBaseType;
/*     */   }
/*     */   
/*     */   public BeanPropertyWriter rename(NameTransformer transformer) {
/* 363 */     String newName = transformer.transform(this._name.getValue());
/* 364 */     if (newName.equals(this._name.toString())) {
/* 365 */       return this;
/*     */     }
/* 367 */     return _new(PropertyName.construct(newName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanPropertyWriter _new(PropertyName newName) {
/* 376 */     return new BeanPropertyWriter(this, newName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void assignTypeSerializer(TypeSerializer typeSer) {
/* 386 */     this._typeSerializer = typeSer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void assignSerializer(JsonSerializer<Object> ser) {
/* 394 */     if (this._serializer != null && this._serializer != ser)
/* 395 */       throw new IllegalStateException(String.format("Cannot override _serializer: had a %s, trying to set to %s", new Object[] {
/*     */               
/* 397 */               ClassUtil.classNameOf(this._serializer), ClassUtil.classNameOf(ser)
/*     */             })); 
/* 399 */     this._serializer = ser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void assignNullSerializer(JsonSerializer<Object> nullSer) {
/* 407 */     if (this._nullSerializer != null && this._nullSerializer != nullSer)
/* 408 */       throw new IllegalStateException(String.format("Cannot override _nullSerializer: had a %s, trying to set to %s", new Object[] {
/*     */               
/* 410 */               ClassUtil.classNameOf(this._nullSerializer), ClassUtil.classNameOf(nullSer)
/*     */             })); 
/* 412 */     this._nullSerializer = nullSer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper) {
/* 420 */     return (BeanPropertyWriter)new UnwrappingBeanPropertyWriter(this, unwrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNonTrivialBaseType(JavaType t) {
/* 429 */     this._nonTrivialBaseType = t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fixAccess(SerializationConfig config) {
/* 440 */     this._member.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
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
/*     */   Object readResolve() {
/* 455 */     if (this._member instanceof com.fasterxml.jackson.databind.introspect.AnnotatedField) {
/* 456 */       this._accessorMethod = null;
/* 457 */       this._field = (Field)this._member.getMember();
/* 458 */     } else if (this._member instanceof com.fasterxml.jackson.databind.introspect.AnnotatedMethod) {
/* 459 */       this._accessorMethod = (Method)this._member.getMember();
/* 460 */       this._field = null;
/*     */     } 
/* 462 */     if (this._serializer == null) {
/* 463 */       this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */     }
/* 465 */     return this;
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
/*     */   public String getName() {
/* 477 */     return this._name.getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public PropertyName getFullName() {
/* 483 */     return new PropertyName(this._name.getValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaType getType() {
/* 488 */     return this._declaredType;
/*     */   }
/*     */ 
/*     */   
/*     */   public PropertyName getWrapperName() {
/* 493 */     return this._wrapperName;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> acls) {
/* 499 */     return (this._member == null) ? null : (A)this._member.getAnnotation(acls);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <A extends java.lang.annotation.Annotation> A getContextAnnotation(Class<A> acls) {
/* 505 */     return (this._contextAnnotations == null) ? null : (A)this._contextAnnotations
/* 506 */       .get(acls);
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedMember getMember() {
/* 511 */     return this._member;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void _depositSchemaProperty(ObjectNode propertiesNode, JsonNode schemaNode) {
/* 517 */     propertiesNode.set(getName(), schemaNode);
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
/*     */   public Object getInternalSetting(Object key) {
/* 533 */     return (this._internalSettings == null) ? null : this._internalSettings.get(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object setInternalSetting(Object key, Object value) {
/* 542 */     if (this._internalSettings == null) {
/* 543 */       this._internalSettings = new HashMap<>();
/*     */     }
/* 545 */     return this._internalSettings.put(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object removeInternalSetting(Object key) {
/* 554 */     Object removed = null;
/* 555 */     if (this._internalSettings != null) {
/* 556 */       removed = this._internalSettings.remove(key);
/*     */       
/* 558 */       if (this._internalSettings.size() == 0) {
/* 559 */         this._internalSettings = null;
/*     */       }
/*     */     } 
/* 562 */     return removed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SerializableString getSerializedName() {
/* 572 */     return (SerializableString)this._name;
/*     */   }
/*     */   
/*     */   public boolean hasSerializer() {
/* 576 */     return (this._serializer != null);
/*     */   }
/*     */   
/*     */   public boolean hasNullSerializer() {
/* 580 */     return (this._nullSerializer != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeSerializer getTypeSerializer() {
/* 587 */     return this._typeSerializer;
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
/*     */   public boolean isUnwrapping() {
/* 601 */     return false;
/*     */   }
/*     */   
/*     */   public boolean willSuppressNulls() {
/* 605 */     return this._suppressNulls;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean wouldConflictWithName(PropertyName name) {
/* 615 */     if (this._wrapperName != null) {
/* 616 */       return this._wrapperName.equals(name);
/*     */     }
/*     */     
/* 619 */     return (name.hasSimpleName(this._name.getValue()) && !name.hasNamespace());
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonSerializer<Object> getSerializer() {
/* 624 */     return this._serializer;
/*     */   }
/*     */   
/*     */   public JavaType getSerializationType() {
/* 628 */     return this._cfgSerializationType;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Class<?> getRawSerializationType() {
/* 633 */     return (this._cfgSerializationType == null) ? null : this._cfgSerializationType
/* 634 */       .getRawClass();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Class<?> getPropertyType() {
/* 642 */     if (this._accessorMethod != null) {
/* 643 */       return this._accessorMethod.getReturnType();
/*     */     }
/* 645 */     if (this._field != null) {
/* 646 */       return this._field.getType();
/*     */     }
/* 648 */     return null;
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
/*     */   public Type getGenericPropertyType() {
/* 660 */     if (this._accessorMethod != null) {
/* 661 */       return this._accessorMethod.getGenericReturnType();
/*     */     }
/* 663 */     if (this._field != null) {
/* 664 */       return this._field.getGenericType();
/*     */     }
/* 666 */     return null;
/*     */   }
/*     */   
/*     */   public Class<?>[] getViews() {
/* 670 */     return this._includeInViews;
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
/*     */   public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
/* 689 */     Object value = (this._accessorMethod == null) ? this._field.get(bean) : this._accessorMethod.invoke(bean, (Object[])null);
/*     */ 
/*     */     
/* 692 */     if (value == null) {
/* 693 */       if (this._nullSerializer != null) {
/* 694 */         gen.writeFieldName((SerializableString)this._name);
/* 695 */         this._nullSerializer.serialize(null, gen, prov);
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 700 */     JsonSerializer<Object> ser = this._serializer;
/* 701 */     if (ser == null) {
/* 702 */       Class<?> cls = value.getClass();
/* 703 */       PropertySerializerMap m = this._dynamicSerializers;
/* 704 */       ser = m.serializerFor(cls);
/* 705 */       if (ser == null) {
/* 706 */         ser = _findAndAddDynamic(m, cls, prov);
/*     */       }
/*     */     } 
/*     */     
/* 710 */     if (this._suppressableValue != null) {
/* 711 */       if (MARKER_FOR_EMPTY == this._suppressableValue) {
/* 712 */         if (ser.isEmpty(prov, value)) {
/*     */           return;
/*     */         }
/* 715 */       } else if (this._suppressableValue.equals(value)) {
/*     */         return;
/*     */       } 
/*     */     }
/*     */     
/* 720 */     if (value == bean)
/*     */     {
/* 722 */       if (_handleSelfReference(bean, gen, prov, ser)) {
/*     */         return;
/*     */       }
/*     */     }
/* 726 */     gen.writeFieldName((SerializableString)this._name);
/* 727 */     if (this._typeSerializer == null) {
/* 728 */       ser.serialize(value, gen, prov);
/*     */     } else {
/* 730 */       ser.serializeWithType(value, gen, prov, this._typeSerializer);
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
/*     */   public void serializeAsOmittedField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
/* 744 */     if (!gen.canOmitFields()) {
/* 745 */       gen.writeOmittedField(this._name.getValue());
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
/*     */   public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
/* 761 */     Object value = (this._accessorMethod == null) ? this._field.get(bean) : this._accessorMethod.invoke(bean, (Object[])null);
/* 762 */     if (value == null) {
/* 763 */       if (this._nullSerializer != null) {
/* 764 */         this._nullSerializer.serialize(null, gen, prov);
/*     */       } else {
/* 766 */         gen.writeNull();
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 771 */     JsonSerializer<Object> ser = this._serializer;
/* 772 */     if (ser == null) {
/* 773 */       Class<?> cls = value.getClass();
/* 774 */       PropertySerializerMap map = this._dynamicSerializers;
/* 775 */       ser = map.serializerFor(cls);
/* 776 */       if (ser == null) {
/* 777 */         ser = _findAndAddDynamic(map, cls, prov);
/*     */       }
/*     */     } 
/*     */     
/* 781 */     if (this._suppressableValue != null) {
/* 782 */       if (MARKER_FOR_EMPTY == this._suppressableValue) {
/* 783 */         if (ser.isEmpty(prov, value)) {
/*     */           
/* 785 */           serializeAsPlaceholder(bean, gen, prov);
/*     */           return;
/*     */         } 
/* 788 */       } else if (this._suppressableValue.equals(value)) {
/*     */         
/* 790 */         serializeAsPlaceholder(bean, gen, prov);
/*     */         
/*     */         return;
/*     */       } 
/*     */     }
/* 795 */     if (value == bean && 
/* 796 */       _handleSelfReference(bean, gen, prov, ser)) {
/*     */       return;
/*     */     }
/*     */     
/* 800 */     if (this._typeSerializer == null) {
/* 801 */       ser.serialize(value, gen, prov);
/*     */     } else {
/* 803 */       ser.serializeWithType(value, gen, prov, this._typeSerializer);
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
/*     */   public void serializeAsPlaceholder(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
/* 818 */     if (this._nullSerializer != null) {
/* 819 */       this._nullSerializer.serialize(null, gen, prov);
/*     */     } else {
/* 821 */       gen.writeNull();
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
/*     */   public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider) throws JsonMappingException {
/* 835 */     if (v != null) {
/* 836 */       if (isRequired()) {
/* 837 */         v.property((BeanProperty)this);
/*     */       } else {
/* 839 */         v.optionalProperty((BeanProperty)this);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void depositSchemaProperty(ObjectNode propertiesNode, SerializerProvider provider) throws JsonMappingException {
/*     */     JsonNode schemaNode;
/* 861 */     JavaType propType = getSerializationType();
/*     */ 
/*     */     
/* 864 */     Type hint = (propType == null) ? (Type)getType() : propType.getRawClass();
/*     */ 
/*     */     
/* 867 */     JsonSerializer<Object> ser = getSerializer();
/* 868 */     if (ser == null) {
/* 869 */       ser = provider.findValueSerializer(getType(), (BeanProperty)this);
/*     */     }
/* 871 */     boolean isOptional = !isRequired();
/* 872 */     if (ser instanceof SchemaAware) {
/* 873 */       schemaNode = ((SchemaAware)ser).getSchema(provider, hint, isOptional);
/*     */     }
/*     */     else {
/*     */       
/* 877 */       schemaNode = JsonSchema.getDefaultSchemaNode();
/*     */     } 
/* 879 */     _depositSchemaProperty(propertiesNode, schemaNode);
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
/*     */   protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
/*     */     PropertySerializerMap.SerializerAndMapResult result;
/* 892 */     if (this._nonTrivialBaseType != null) {
/* 893 */       JavaType t = provider.constructSpecializedType(this._nonTrivialBaseType, type);
/*     */       
/* 895 */       result = map.findAndAddPrimarySerializer(t, provider, (BeanProperty)this);
/*     */     } else {
/* 897 */       result = map.findAndAddPrimarySerializer(type, provider, (BeanProperty)this);
/*     */     } 
/*     */     
/* 900 */     if (map != result.map) {
/* 901 */       this._dynamicSerializers = result.map;
/*     */     }
/* 903 */     return result.serializer;
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
/*     */   public final Object get(Object bean) throws Exception {
/* 915 */     return (this._accessorMethod == null) ? this._field.get(bean) : this._accessorMethod
/* 916 */       .invoke(bean, (Object[])null);
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
/*     */   protected boolean _handleSelfReference(Object bean, JsonGenerator gen, SerializerProvider prov, JsonSerializer<?> ser) throws IOException {
/* 937 */     if (!ser.usesObjectId()) {
/* 938 */       if (prov.isEnabled(SerializationFeature.FAIL_ON_SELF_REFERENCES)) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 943 */         if (ser instanceof com.fasterxml.jackson.databind.ser.std.BeanSerializerBase) {
/* 944 */           prov.reportBadDefinition(getType(), "Direct self-reference leading to cycle");
/*     */         }
/* 946 */       } else if (prov.isEnabled(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL)) {
/* 947 */         if (this._nullSerializer != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 953 */           if (!gen.getOutputContext().inArray()) {
/* 954 */             gen.writeFieldName((SerializableString)this._name);
/*     */           }
/* 956 */           this._nullSerializer.serialize(null, gen, prov);
/*     */         } 
/* 958 */         return true;
/*     */       } 
/*     */     }
/* 961 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 966 */     StringBuilder sb = new StringBuilder(40);
/* 967 */     sb.append("property '").append(getName()).append("' (");
/* 968 */     if (this._accessorMethod != null) {
/* 969 */       sb.append("via method ")
/* 970 */         .append(this._accessorMethod.getDeclaringClass().getName())
/* 971 */         .append("#").append(this._accessorMethod.getName());
/* 972 */     } else if (this._field != null) {
/* 973 */       sb.append("field \"").append(this._field.getDeclaringClass().getName())
/* 974 */         .append("#").append(this._field.getName());
/*     */     } else {
/* 976 */       sb.append("virtual");
/*     */     } 
/* 978 */     if (this._serializer == null) {
/* 979 */       sb.append(", no static serializer");
/*     */     } else {
/* 981 */       sb.append(", static serializer of type " + this._serializer
/* 982 */           .getClass().getName());
/*     */     } 
/* 984 */     sb.append(')');
/* 985 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/ser/BeanPropertyWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */