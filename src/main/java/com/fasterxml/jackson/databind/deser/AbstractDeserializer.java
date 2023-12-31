/*     */ package com.fasterxml.jackson.databind.deser;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*     */ import com.fasterxml.jackson.annotation.ObjectIdGenerators;
/*     */ import com.fasterxml.jackson.annotation.ObjectIdResolver;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*     */ import com.fasterxml.jackson.databind.BeanDescription;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.PropertyName;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*     */ import com.fasterxml.jackson.databind.introspect.Annotated;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AbstractDeserializer
/*     */   extends JsonDeserializer<Object>
/*     */   implements ContextualDeserializer, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _baseType;
/*     */   protected final ObjectIdReader _objectIdReader;
/*     */   protected final Map<String, SettableBeanProperty> _backRefProperties;
/*     */   protected transient Map<String, SettableBeanProperty> _properties;
/*     */   protected final boolean _acceptString;
/*     */   protected final boolean _acceptBoolean;
/*     */   protected final boolean _acceptInt;
/*     */   protected final boolean _acceptDouble;
/*     */   
/*     */   public AbstractDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, Map<String, SettableBeanProperty> backRefProps, Map<String, SettableBeanProperty> props) {
/*  67 */     this._baseType = beanDesc.getType();
/*  68 */     this._objectIdReader = builder.getObjectIdReader();
/*  69 */     this._backRefProperties = backRefProps;
/*  70 */     this._properties = props;
/*  71 */     Class<?> cls = this._baseType.getRawClass();
/*  72 */     this._acceptString = cls.isAssignableFrom(String.class);
/*  73 */     this._acceptBoolean = (cls == boolean.class || cls.isAssignableFrom(Boolean.class));
/*  74 */     this._acceptInt = (cls == int.class || cls.isAssignableFrom(Integer.class));
/*  75 */     this._acceptDouble = (cls == double.class || cls.isAssignableFrom(Double.class));
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public AbstractDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, Map<String, SettableBeanProperty> backRefProps) {
/*  81 */     this(builder, beanDesc, backRefProps, null);
/*     */   }
/*     */ 
/*     */   
/*     */   protected AbstractDeserializer(BeanDescription beanDesc) {
/*  86 */     this._baseType = beanDesc.getType();
/*  87 */     this._objectIdReader = null;
/*  88 */     this._backRefProperties = null;
/*  89 */     Class<?> cls = this._baseType.getRawClass();
/*  90 */     this._acceptString = cls.isAssignableFrom(String.class);
/*  91 */     this._acceptBoolean = (cls == boolean.class || cls.isAssignableFrom(Boolean.class));
/*  92 */     this._acceptInt = (cls == int.class || cls.isAssignableFrom(Integer.class));
/*  93 */     this._acceptDouble = (cls == double.class || cls.isAssignableFrom(Double.class));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractDeserializer(AbstractDeserializer base, ObjectIdReader objectIdReader, Map<String, SettableBeanProperty> props) {
/* 102 */     this._baseType = base._baseType;
/* 103 */     this._backRefProperties = base._backRefProperties;
/* 104 */     this._acceptString = base._acceptString;
/* 105 */     this._acceptBoolean = base._acceptBoolean;
/* 106 */     this._acceptInt = base._acceptInt;
/* 107 */     this._acceptDouble = base._acceptDouble;
/*     */     
/* 109 */     this._objectIdReader = objectIdReader;
/* 110 */     this._properties = props;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AbstractDeserializer constructForNonPOJO(BeanDescription beanDesc) {
/* 120 */     return new AbstractDeserializer(beanDesc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 127 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 128 */     if (property != null && intr != null) {
/* 129 */       AnnotatedMember accessor = property.getMember();
/* 130 */       if (accessor != null) {
/* 131 */         ObjectIdInfo objectIdInfo = intr.findObjectIdInfo((Annotated)accessor);
/* 132 */         if (objectIdInfo != null) {
/*     */           JavaType idType;
/*     */           ObjectIdGenerator<?> idGen;
/* 135 */           SettableBeanProperty idProp = null;
/* 136 */           ObjectIdResolver resolver = ctxt.objectIdResolverInstance((Annotated)accessor, objectIdInfo);
/*     */ 
/*     */           
/* 139 */           objectIdInfo = intr.findObjectReferenceInfo((Annotated)accessor, objectIdInfo);
/* 140 */           Class<?> implClass = objectIdInfo.getGeneratorType();
/*     */           
/* 142 */           if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
/* 143 */             PropertyName propName = objectIdInfo.getPropertyName();
/* 144 */             idProp = (this._properties == null) ? null : this._properties.get(propName.getSimpleName());
/* 145 */             if (idProp == null)
/* 146 */               ctxt.reportBadDefinition(this._baseType, String.format("Invalid Object Id definition for %s: cannot find property with name %s", new Object[] {
/*     */                       
/* 148 */                       ClassUtil.nameOf(handledType()), ClassUtil.name(propName)
/*     */                     })); 
/* 150 */             idType = idProp.getType();
/* 151 */             PropertyBasedObjectIdGenerator propertyBasedObjectIdGenerator = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
/*     */ 
/*     */           
/*     */           }
/*     */           else {
/*     */ 
/*     */ 
/*     */             
/* 159 */             resolver = ctxt.objectIdResolverInstance((Annotated)accessor, objectIdInfo);
/* 160 */             JavaType type = ctxt.constructType(implClass);
/* 161 */             idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/* 162 */             idGen = ctxt.objectIdGeneratorInstance((Annotated)accessor, objectIdInfo);
/*     */           } 
/* 164 */           JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
/* 165 */           ObjectIdReader oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
/*     */           
/* 167 */           return new AbstractDeserializer(this, oir, null);
/*     */         } 
/*     */       } 
/*     */     } 
/* 171 */     if (this._properties == null) {
/* 172 */       return this;
/*     */     }
/*     */     
/* 175 */     return new AbstractDeserializer(this, this._objectIdReader, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> handledType() {
/* 186 */     return this._baseType.getRawClass();
/*     */   }
/*     */   
/*     */   public boolean isCachable() {
/* 190 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 196 */     return LogicalType.POJO;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 206 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectIdReader getObjectIdReader() {
/* 216 */     return this._objectIdReader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SettableBeanProperty findBackReference(String logicalName) {
/* 225 */     return (this._backRefProperties == null) ? null : this._backRefProperties.get(logicalName);
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
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 241 */     if (this._objectIdReader != null) {
/* 242 */       JsonToken t = p.currentToken();
/* 243 */       if (t != null) {
/*     */         
/* 245 */         if (t.isScalarValue()) {
/* 246 */           return _deserializeFromObjectId(p, ctxt);
/*     */         }
/*     */         
/* 249 */         if (t == JsonToken.START_OBJECT) {
/* 250 */           t = p.nextToken();
/*     */         }
/* 252 */         if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader
/* 253 */           .isValidReferencePropertyName(p.currentName(), p)) {
/* 254 */           return _deserializeFromObjectId(p, ctxt);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 259 */     Object result = _deserializeIfNatural(p, ctxt);
/* 260 */     if (result != null) {
/* 261 */       return result;
/*     */     }
/* 263 */     return typeDeserializer.deserializeTypedFromObject(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 273 */     ValueInstantiator bogus = new ValueInstantiator.Base(this._baseType);
/* 274 */     return ctxt.handleMissingInstantiator(this._baseType.getRawClass(), bogus, p, "abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information", new Object[0]);
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
/*     */   protected Object _deserializeIfNatural(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 292 */     switch (p.currentTokenId()) {
/*     */       case 6:
/* 294 */         if (this._acceptString) {
/* 295 */           return p.getText();
/*     */         }
/*     */         break;
/*     */       case 7:
/* 299 */         if (this._acceptInt) {
/* 300 */           return Integer.valueOf(p.getIntValue());
/*     */         }
/*     */         break;
/*     */       case 8:
/* 304 */         if (this._acceptDouble) {
/* 305 */           return Double.valueOf(p.getDoubleValue());
/*     */         }
/*     */         break;
/*     */       case 9:
/* 309 */         if (this._acceptBoolean) {
/* 310 */           return Boolean.TRUE;
/*     */         }
/*     */         break;
/*     */       case 10:
/* 314 */         if (this._acceptBoolean) {
/* 315 */           return Boolean.FALSE;
/*     */         }
/*     */         break;
/*     */     } 
/* 319 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object _deserializeFromObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 328 */     Object id = this._objectIdReader.readObjectReference(p, ctxt);
/* 329 */     ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
/*     */     
/* 331 */     Object pojo = roid.resolve();
/* 332 */     if (pojo == null) {
/* 333 */       throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] -- unresolved forward-reference?", p
/* 334 */           .getCurrentLocation(), roid);
/*     */     }
/* 336 */     return pojo;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/AbstractDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */