/*     */ package com.fasterxml.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JsonDeserializer;
/*     */ import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
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
/*     */ public class BeanAsArrayDeserializer
/*     */   extends BeanDeserializerBase
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final BeanDeserializerBase _delegate;
/*     */   protected final SettableBeanProperty[] _orderedProperties;
/*     */   
/*     */   public BeanAsArrayDeserializer(BeanDeserializerBase delegate, SettableBeanProperty[] ordered) {
/*  48 */     super(delegate);
/*  49 */     this._delegate = delegate;
/*  50 */     this._orderedProperties = ordered;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
/*  60 */     return this._delegate.unwrappingDeserializer(unwrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
/*  65 */     return new BeanAsArrayDeserializer(this._delegate.withObjectIdReader(oir), this._orderedProperties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withByNameInclusion(Set<String> ignorableProps, Set<String> includableProps) {
/*  72 */     return new BeanAsArrayDeserializer(this._delegate.withByNameInclusion(ignorableProps, includableProps), this._orderedProperties);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withIgnoreAllUnknown(boolean ignoreUnknown) {
/*  78 */     return new BeanAsArrayDeserializer(this._delegate.withIgnoreAllUnknown(ignoreUnknown), this._orderedProperties);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
/*  84 */     return new BeanAsArrayDeserializer(this._delegate.withBeanProperties(props), this._orderedProperties);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected BeanDeserializerBase asArrayDeserializer() {
/*  90 */     return this;
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
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 104 */     if (!p.isExpectedStartArrayToken()) {
/* 105 */       return _deserializeFromNonArray(p, ctxt);
/*     */     }
/* 107 */     if (!this._vanillaProcessing) {
/* 108 */       return _deserializeNonVanilla(p, ctxt);
/*     */     }
/* 110 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/*     */     
/* 112 */     p.setCurrentValue(bean);
/*     */     
/* 114 */     SettableBeanProperty[] props = this._orderedProperties;
/* 115 */     int i = 0;
/* 116 */     int propCount = props.length;
/*     */     while (true) {
/* 118 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 119 */         return bean;
/*     */       }
/* 121 */       if (i == propCount) {
/*     */         break;
/*     */       }
/* 124 */       SettableBeanProperty prop = props[i];
/* 125 */       if (prop != null) {
/*     */         try {
/* 127 */           prop.deserializeAndSet(p, ctxt, bean);
/* 128 */         } catch (Exception e) {
/* 129 */           wrapAndThrow(e, bean, prop.getName(), ctxt);
/*     */         } 
/*     */       } else {
/* 132 */         p.skipChildren();
/*     */       } 
/* 134 */       i++;
/*     */     } 
/*     */     
/* 137 */     if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/* 138 */       ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.END_ARRAY, "Unexpected JSON values; expected at most %d properties (in JSON Array)", new Object[] {
/*     */             
/* 140 */             Integer.valueOf(propCount)
/*     */           });
/*     */     }
/*     */     
/*     */     while (true) {
/* 145 */       p.skipChildren();
/* 146 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 147 */         return bean;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/* 155 */     p.setCurrentValue(bean);
/*     */     
/* 157 */     if (!p.isExpectedStartArrayToken()) {
/* 158 */       return _deserializeFromNonArray(p, ctxt);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 164 */     if (this._injectables != null) {
/* 165 */       injectValues(ctxt, bean);
/*     */     }
/* 167 */     SettableBeanProperty[] props = this._orderedProperties;
/* 168 */     int i = 0;
/* 169 */     int propCount = props.length;
/*     */     while (true) {
/* 171 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 172 */         return bean;
/*     */       }
/* 174 */       if (i == propCount) {
/*     */         break;
/*     */       }
/* 177 */       SettableBeanProperty prop = props[i];
/* 178 */       if (prop != null) {
/*     */         try {
/* 180 */           prop.deserializeAndSet(p, ctxt, bean);
/* 181 */         } catch (Exception e) {
/* 182 */           wrapAndThrow(e, bean, prop.getName(), ctxt);
/*     */         } 
/*     */       } else {
/* 185 */         p.skipChildren();
/*     */       } 
/* 187 */       i++;
/*     */     } 
/*     */ 
/*     */     
/* 191 */     if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/* 192 */       ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.END_ARRAY, "Unexpected JSON values; expected at most %d properties (in JSON Array)", new Object[] {
/*     */             
/* 194 */             Integer.valueOf(propCount)
/*     */           });
/*     */     }
/*     */     
/*     */     while (true) {
/* 199 */       p.skipChildren();
/* 200 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 201 */         return bean;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 209 */     return _deserializeFromNonArray(p, ctxt);
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
/*     */   protected Object _deserializeNonVanilla(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 225 */     if (this._nonStandardCreation) {
/* 226 */       return deserializeFromObjectUsingNonDefault(p, ctxt);
/*     */     }
/* 228 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/*     */     
/* 230 */     p.setCurrentValue(bean);
/* 231 */     if (this._injectables != null) {
/* 232 */       injectValues(ctxt, bean);
/*     */     }
/* 234 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 235 */     SettableBeanProperty[] props = this._orderedProperties;
/* 236 */     int i = 0;
/* 237 */     int propCount = props.length;
/*     */     
/*     */     while (true) {
/* 240 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 241 */         return bean;
/*     */       }
/* 243 */       if (i == propCount) {
/*     */         break;
/*     */       }
/* 246 */       SettableBeanProperty prop = props[i];
/* 247 */       i++;
/* 248 */       if (prop != null && (
/* 249 */         activeView == null || prop.visibleInView(activeView))) {
/*     */         try {
/* 251 */           prop.deserializeAndSet(p, ctxt, bean);
/* 252 */         } catch (Exception e) {
/* 253 */           wrapAndThrow(e, bean, prop.getName(), ctxt);
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 259 */       p.skipChildren();
/*     */     } 
/*     */     
/* 262 */     if (!this._ignoreAllUnknown) {
/* 263 */       ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.END_ARRAY, "Unexpected JSON values; expected at most %d properties (in JSON Array)", new Object[] {
/*     */             
/* 265 */             Integer.valueOf(propCount)
/*     */           });
/*     */     }
/*     */     
/*     */     while (true) {
/* 270 */       p.skipChildren();
/* 271 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 272 */         return bean;
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
/*     */   protected final Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 287 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/* 288 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/*     */     
/* 290 */     SettableBeanProperty[] props = this._orderedProperties;
/* 291 */     int propCount = props.length;
/* 292 */     int i = 0;
/* 293 */     Object bean = null;
/* 294 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/*     */     
/* 296 */     for (; p.nextToken() != JsonToken.END_ARRAY; i++) {
/* 297 */       SettableBeanProperty prop = (i < propCount) ? props[i] : null;
/* 298 */       if (prop == null) {
/* 299 */         p.skipChildren();
/*     */       
/*     */       }
/* 302 */       else if (activeView != null && !prop.visibleInView(activeView)) {
/* 303 */         p.skipChildren();
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 308 */       else if (bean != null) {
/*     */         try {
/* 310 */           prop.deserializeAndSet(p, ctxt, bean);
/* 311 */         } catch (Exception e) {
/* 312 */           wrapAndThrow(e, bean, prop.getName(), ctxt);
/*     */         } 
/*     */       } else {
/*     */         
/* 316 */         String propName = prop.getName();
/*     */         
/* 318 */         SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/*     */         
/* 320 */         if (!buffer.readIdProperty(propName) || creatorProp != null)
/*     */         {
/*     */           
/* 323 */           if (creatorProp != null) {
/*     */             
/* 325 */             if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
/*     */               try {
/* 327 */                 bean = creator.build(ctxt, buffer);
/* 328 */               } catch (Exception e) {
/* 329 */                 wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*     */               } 
/*     */ 
/*     */               
/* 333 */               p.setCurrentValue(bean);
/*     */ 
/*     */               
/* 336 */               if (bean.getClass() != this._beanType.getRawClass())
/*     */               {
/*     */ 
/*     */ 
/*     */                 
/* 341 */                 ctxt.reportBadDefinition(this._beanType, String.format("Cannot support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type %s, actual type %s", new Object[] {
/*     */ 
/*     */                         
/* 344 */                         ClassUtil.getTypeDescription(this._beanType), 
/* 345 */                         ClassUtil.getClassDescription(bean)
/*     */                       }));
/*     */               }
/*     */             } 
/*     */           } else {
/*     */             
/* 351 */             buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/*     */           }  } 
/*     */       } 
/*     */     } 
/* 355 */     if (bean == null) {
/*     */       try {
/* 357 */         bean = creator.build(ctxt, buffer);
/* 358 */       } catch (Exception e) {
/* 359 */         return wrapInstantiationProblem(e, ctxt);
/*     */       } 
/*     */     }
/* 362 */     return bean;
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
/*     */   protected Object _deserializeFromNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 374 */     String message = "Cannot deserialize a POJO (of type %s) from non-Array representation (token: %s): type/property designed to be serialized as JSON Array";
/*     */     
/* 376 */     return ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p, message, new Object[] {
/* 377 */           ClassUtil.getTypeDescription(this._beanType), p.currentToken()
/*     */         });
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/BeanAsArrayDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */