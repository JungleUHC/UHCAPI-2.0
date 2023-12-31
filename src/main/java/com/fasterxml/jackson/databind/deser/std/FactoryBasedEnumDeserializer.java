/*     */ package com.fasterxml.jackson.databind.deser.std;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.BeanProperty;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.MapperFeature;
/*     */ import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
/*     */ import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.type.LogicalType;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
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
/*     */ class FactoryBasedEnumDeserializer
/*     */   extends StdDeserializer<Object>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _inputType;
/*     */   protected final AnnotatedMethod _factory;
/*     */   protected final JsonDeserializer<?> _deser;
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected final SettableBeanProperty[] _creatorProps;
/*     */   protected final boolean _hasArgs;
/*     */   private transient PropertyBasedCreator _propCreator;
/*     */   
/*     */   public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f, JavaType paramType, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps) {
/*  51 */     super(cls);
/*  52 */     this._factory = f;
/*  53 */     this._hasArgs = true;
/*     */     
/*  55 */     this._inputType = (paramType.hasRawClass(String.class) || paramType.hasRawClass(CharSequence.class)) ? null : paramType;
/*     */     
/*  57 */     this._deser = null;
/*  58 */     this._valueInstantiator = valueInstantiator;
/*  59 */     this._creatorProps = creatorProps;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f) {
/*  67 */     super(cls);
/*  68 */     this._factory = f;
/*  69 */     this._hasArgs = false;
/*  70 */     this._inputType = null;
/*  71 */     this._deser = null;
/*  72 */     this._valueInstantiator = null;
/*  73 */     this._creatorProps = null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected FactoryBasedEnumDeserializer(FactoryBasedEnumDeserializer base, JsonDeserializer<?> deser) {
/*  78 */     super(base._valueClass);
/*  79 */     this._inputType = base._inputType;
/*  80 */     this._factory = base._factory;
/*  81 */     this._hasArgs = base._hasArgs;
/*  82 */     this._valueInstantiator = base._valueInstantiator;
/*  83 */     this._creatorProps = base._creatorProps;
/*     */     
/*  85 */     this._deser = deser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/*  96 */     if (this._deser == null && this._inputType != null && this._creatorProps == null) {
/*  97 */       return new FactoryBasedEnumDeserializer(this, ctxt
/*  98 */           .findContextualValueDeserializer(this._inputType, property));
/*     */     }
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 105 */     return Boolean.FALSE;
/*     */   }
/*     */ 
/*     */   
/*     */   public LogicalType logicalType() {
/* 110 */     return LogicalType.Enum;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCachable() {
/* 115 */     return true;
/*     */   }
/*     */   public ValueInstantiator getValueInstantiator() {
/* 118 */     return this._valueInstantiator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*     */     Object value;
/* 127 */     if (this._deser != null) {
/* 128 */       value = this._deser.deserialize(p, ctxt);
/*     */     
/*     */     }
/* 131 */     else if (this._hasArgs) {
/*     */ 
/*     */       
/* 134 */       if (this._creatorProps != null) {
/* 135 */         if (!p.isExpectedStartObjectToken()) {
/* 136 */           JavaType targetType = getValueType(ctxt);
/* 137 */           ctxt.reportInputMismatch(targetType, "Input mismatch reading Enum %s: properties-based `@JsonCreator` (%s) expects JSON Object (JsonToken.START_OBJECT), got JsonToken.%s", new Object[] {
/*     */                 
/* 139 */                 ClassUtil.getTypeDescription(targetType), this._factory, p.currentToken() });
/*     */         } 
/* 141 */         if (this._propCreator == null) {
/* 142 */           this._propCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, this._creatorProps, ctxt
/* 143 */               .isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
/*     */         }
/* 145 */         p.nextToken();
/* 146 */         return deserializeEnumUsingPropertyBased(p, ctxt, this._propCreator);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 153 */       JsonToken t = p.currentToken();
/* 154 */       if (t != null && !t.isScalarValue()) {
/*     */         
/* 156 */         value = "";
/* 157 */         p.skipChildren();
/*     */       } else {
/* 159 */         value = p.getValueAsString();
/*     */       } 
/*     */     } else {
/* 162 */       p.skipChildren();
/*     */       try {
/* 164 */         return this._factory.call();
/* 165 */       } catch (Exception e) {
/* 166 */         Throwable t = ClassUtil.throwRootCauseIfIOE(e);
/* 167 */         return ctxt.handleInstantiationProblem(this._valueClass, null, t);
/*     */       } 
/*     */     } 
/*     */     try {
/* 171 */       return this._factory.callOnWith(this._valueClass, new Object[] { value });
/* 172 */     } catch (Exception e) {
/* 173 */       Throwable t = ClassUtil.throwRootCauseIfIOE(e);
/* 174 */       if (t instanceof IllegalArgumentException)
/*     */       {
/* 176 */         if (ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
/* 177 */           return null;
/*     */         }
/*     */       }
/*     */ 
/*     */       
/* 182 */       return ctxt.handleInstantiationProblem(this._valueClass, value, t);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 188 */     if (this._deser == null) {
/* 189 */       return deserialize(p, ctxt);
/*     */     }
/* 191 */     return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object deserializeEnumUsingPropertyBased(JsonParser p, DeserializationContext ctxt, PropertyBasedCreator creator) throws IOException {
/* 198 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
/*     */     
/* 200 */     JsonToken t = p.currentToken();
/* 201 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 202 */       String propName = p.currentName();
/* 203 */       p.nextToken();
/*     */       
/* 205 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 206 */       if (!buffer.readIdProperty(propName) || creatorProp != null)
/*     */       {
/*     */         
/* 209 */         if (creatorProp != null) {
/* 210 */           buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp));
/*     */         }
/*     */         else {
/*     */           
/* 214 */           p.skipChildren();
/*     */         }  } 
/* 216 */     }  return creator.build(ctxt, buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
/*     */     try {
/* 225 */       return prop.deserialize(p, ctxt);
/* 226 */     } catch (Exception e) {
/* 227 */       return wrapAndThrow(e, handledType(), prop.getName(), ctxt);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
/* 234 */     throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, fieldName);
/*     */   }
/*     */ 
/*     */   
/*     */   private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException {
/* 239 */     t = ClassUtil.getRootCause(t);
/*     */     
/* 241 */     ClassUtil.throwIfError(t);
/* 242 */     boolean wrap = (ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/*     */     
/* 244 */     if (t instanceof IOException) {
/* 245 */       if (!wrap || !(t instanceof com.fasterxml.jackson.core.JacksonException)) {
/* 246 */         throw (IOException)t;
/*     */       }
/* 248 */     } else if (!wrap) {
/* 249 */       ClassUtil.throwIfRTE(t);
/*     */     } 
/* 251 */     return t;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/std/FactoryBasedEnumDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */