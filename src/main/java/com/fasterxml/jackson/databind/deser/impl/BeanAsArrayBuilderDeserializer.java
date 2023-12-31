/*     */ package com.fasterxml.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.DeserializationConfig;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.fasterxml.jackson.databind.util.ClassUtil;
/*     */ import com.fasterxml.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
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
/*     */ public class BeanAsArrayBuilderDeserializer
/*     */   extends BeanDeserializerBase
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final BeanDeserializerBase _delegate;
/*     */   protected final SettableBeanProperty[] _orderedProperties;
/*     */   protected final AnnotatedMethod _buildMethod;
/*     */   protected final JavaType _targetType;
/*     */   
/*     */   public BeanAsArrayBuilderDeserializer(BeanDeserializerBase delegate, JavaType targetType, SettableBeanProperty[] ordered, AnnotatedMethod buildMethod) {
/*  56 */     super(delegate);
/*  57 */     this._delegate = delegate;
/*  58 */     this._targetType = targetType;
/*  59 */     this._orderedProperties = ordered;
/*  60 */     this._buildMethod = buildMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
/*  70 */     return this._delegate.unwrappingDeserializer(unwrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
/*  75 */     return new BeanAsArrayBuilderDeserializer(this._delegate.withObjectIdReader(oir), this._targetType, this._orderedProperties, this._buildMethod);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withByNameInclusion(Set<String> ignorableProps, Set<String> includableProps) {
/*  82 */     return new BeanAsArrayBuilderDeserializer(this._delegate.withByNameInclusion(ignorableProps, includableProps), this._targetType, this._orderedProperties, this._buildMethod);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withIgnoreAllUnknown(boolean ignoreUnknown) {
/*  88 */     return new BeanAsArrayBuilderDeserializer(this._delegate.withIgnoreAllUnknown(ignoreUnknown), this._targetType, this._orderedProperties, this._buildMethod);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
/*  94 */     return new BeanAsArrayBuilderDeserializer(this._delegate.withBeanProperties(props), this._targetType, this._orderedProperties, this._buildMethod);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanDeserializerBase asArrayDeserializer() {
/* 100 */     return this;
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
/*     */   public Boolean supportsUpdate(DeserializationConfig config) {
/* 112 */     return Boolean.FALSE;
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
/*     */   protected final Object finishBuild(DeserializationContext ctxt, Object builder) throws IOException {
/*     */     try {
/* 125 */       return this._buildMethod.getMember().invoke(builder, (Object[])null);
/* 126 */     } catch (Exception e) {
/* 127 */       return wrapInstantiationProblem(e, ctxt);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 136 */     if (!p.isExpectedStartArrayToken()) {
/* 137 */       return finishBuild(ctxt, _deserializeFromNonArray(p, ctxt));
/*     */     }
/* 139 */     if (!this._vanillaProcessing) {
/* 140 */       return finishBuild(ctxt, _deserializeNonVanilla(p, ctxt));
/*     */     }
/* 142 */     Object builder = this._valueInstantiator.createUsingDefault(ctxt);
/* 143 */     SettableBeanProperty[] props = this._orderedProperties;
/* 144 */     int i = 0;
/* 145 */     int propCount = props.length;
/*     */     while (true) {
/* 147 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 148 */         return finishBuild(ctxt, builder);
/*     */       }
/* 150 */       if (i == propCount) {
/*     */         break;
/*     */       }
/* 153 */       SettableBeanProperty prop = props[i];
/* 154 */       if (prop != null) {
/*     */         try {
/* 156 */           builder = prop.deserializeSetAndReturn(p, ctxt, builder);
/* 157 */         } catch (Exception e) {
/* 158 */           wrapAndThrow(e, builder, prop.getName(), ctxt);
/*     */         } 
/*     */       } else {
/* 161 */         p.skipChildren();
/*     */       } 
/* 163 */       i++;
/*     */     } 
/*     */ 
/*     */     
/* 167 */     if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/* 168 */       ctxt.reportInputMismatch(handledType(), "Unexpected JSON values; expected at most %d properties (in JSON Array)", new Object[] {
/*     */             
/* 170 */             Integer.valueOf(propCount)
/*     */           });
/*     */     }
/*     */     
/* 174 */     while (p.nextToken() != JsonToken.END_ARRAY) {
/* 175 */       p.skipChildren();
/*     */     }
/* 177 */     return finishBuild(ctxt, builder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object value) throws IOException {
/* 185 */     return this._delegate.deserialize(p, ctxt, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 192 */     return _deserializeFromNonArray(p, ctxt);
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
/*     */   protected Object _deserializeNonVanilla(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 211 */     if (this._nonStandardCreation) {
/* 212 */       return deserializeFromObjectUsingNonDefault(p, ctxt);
/*     */     }
/* 214 */     Object builder = this._valueInstantiator.createUsingDefault(ctxt);
/* 215 */     if (this._injectables != null) {
/* 216 */       injectValues(ctxt, builder);
/*     */     }
/* 218 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 219 */     SettableBeanProperty[] props = this._orderedProperties;
/* 220 */     int i = 0;
/* 221 */     int propCount = props.length;
/*     */     while (true) {
/* 223 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 224 */         return builder;
/*     */       }
/* 226 */       if (i == propCount) {
/*     */         break;
/*     */       }
/* 229 */       SettableBeanProperty prop = props[i];
/* 230 */       i++;
/* 231 */       if (prop != null && (
/* 232 */         activeView == null || prop.visibleInView(activeView))) {
/*     */         try {
/* 234 */           prop.deserializeSetAndReturn(p, ctxt, builder);
/* 235 */         } catch (Exception e) {
/* 236 */           wrapAndThrow(e, builder, prop.getName(), ctxt);
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 242 */       p.skipChildren();
/*     */     } 
/*     */     
/* 245 */     if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/* 246 */       ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.END_ARRAY, "Unexpected JSON value(s); expected at most %d properties (in JSON Array)", new Object[] {
/*     */             
/* 248 */             Integer.valueOf(propCount)
/*     */           });
/*     */     }
/*     */     
/* 252 */     while (p.nextToken() != JsonToken.END_ARRAY) {
/* 253 */       p.skipChildren();
/*     */     }
/* 255 */     return builder;
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
/*     */   protected final Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 271 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/* 272 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/*     */     
/* 274 */     SettableBeanProperty[] props = this._orderedProperties;
/* 275 */     int propCount = props.length;
/* 276 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 277 */     int i = 0;
/* 278 */     Object builder = null;
/*     */     
/* 280 */     for (; p.nextToken() != JsonToken.END_ARRAY; i++) {
/* 281 */       SettableBeanProperty prop = (i < propCount) ? props[i] : null;
/* 282 */       if (prop == null) {
/* 283 */         p.skipChildren();
/*     */       
/*     */       }
/* 286 */       else if (activeView != null && !prop.visibleInView(activeView)) {
/* 287 */         p.skipChildren();
/*     */ 
/*     */       
/*     */       }
/* 291 */       else if (builder != null) {
/*     */         try {
/* 293 */           builder = prop.deserializeSetAndReturn(p, ctxt, builder);
/* 294 */         } catch (Exception e) {
/* 295 */           wrapAndThrow(e, builder, prop.getName(), ctxt);
/*     */         } 
/*     */       } else {
/*     */         
/* 299 */         String propName = prop.getName();
/*     */         
/* 301 */         SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/*     */         
/* 303 */         if (!buffer.readIdProperty(propName) || creatorProp != null)
/*     */         {
/*     */           
/* 306 */           if (creatorProp != null) {
/*     */             
/* 308 */             if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
/*     */               try {
/* 310 */                 builder = creator.build(ctxt, buffer);
/* 311 */               } catch (Exception e) {
/* 312 */                 wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*     */               } 
/*     */ 
/*     */               
/* 316 */               if (builder.getClass() != this._beanType.getRawClass())
/*     */               {
/*     */ 
/*     */ 
/*     */                 
/* 321 */                 return ctxt.reportBadDefinition(this._beanType, String.format("Cannot support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type %s, actual type %s", new Object[] {
/*     */                         
/* 323 */                         ClassUtil.getTypeDescription(this._beanType), builder
/* 324 */                         .getClass().getName()
/*     */                       }));
/*     */               }
/*     */             } 
/*     */           } else {
/*     */             
/* 330 */             buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/*     */           }  } 
/*     */       } 
/*     */     } 
/* 334 */     if (builder == null) {
/*     */       try {
/* 336 */         builder = creator.build(ctxt, buffer);
/* 337 */       } catch (Exception e) {
/* 338 */         return wrapInstantiationProblem(e, ctxt);
/*     */       } 
/*     */     }
/* 341 */     return builder;
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
/*     */   protected Object _deserializeFromNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 354 */     String message = "Cannot deserialize a POJO (of type %s) from non-Array representation (token: %s): type/property designed to be serialized as JSON Array";
/*     */     
/* 356 */     return ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p, message, new Object[] { this._beanType.getRawClass().getName(), p.currentToken() });
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/BeanAsArrayBuilderDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */