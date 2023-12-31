/*     */ package com.fasterxml.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonToken;
/*     */ import com.fasterxml.jackson.databind.DeserializationContext;
/*     */ import com.fasterxml.jackson.databind.DeserializationFeature;
/*     */ import com.fasterxml.jackson.databind.JavaType;
/*     */ import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.fasterxml.jackson.databind.util.TokenBuffer;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
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
/*     */ public class ExternalTypeHandler
/*     */ {
/*     */   private final JavaType _beanType;
/*     */   private final ExtTypedProperty[] _properties;
/*     */   private final Map<String, Object> _nameToPropertyIndex;
/*     */   private final String[] _typeIds;
/*     */   private final TokenBuffer[] _tokens;
/*     */   
/*     */   protected ExternalTypeHandler(JavaType beanType, ExtTypedProperty[] properties, Map<String, Object> nameToPropertyIndex, String[] typeIds, TokenBuffer[] tokens) {
/*  41 */     this._beanType = beanType;
/*  42 */     this._properties = properties;
/*  43 */     this._nameToPropertyIndex = nameToPropertyIndex;
/*  44 */     this._typeIds = typeIds;
/*  45 */     this._tokens = tokens;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ExternalTypeHandler(ExternalTypeHandler h) {
/*  50 */     this._beanType = h._beanType;
/*  51 */     this._properties = h._properties;
/*  52 */     this._nameToPropertyIndex = h._nameToPropertyIndex;
/*  53 */     int len = this._properties.length;
/*  54 */     this._typeIds = new String[len];
/*  55 */     this._tokens = new TokenBuffer[len];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder builder(JavaType beanType) {
/*  62 */     return new Builder(beanType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExternalTypeHandler start() {
/*  70 */     return new ExternalTypeHandler(this);
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
/*     */   public boolean handleTypePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean) throws IOException {
/*  85 */     Object ob = this._nameToPropertyIndex.get(propName);
/*  86 */     if (ob == null) {
/*  87 */       return false;
/*     */     }
/*  89 */     String typeId = p.getText();
/*     */     
/*  91 */     if (ob instanceof List) {
/*  92 */       boolean result = false;
/*  93 */       for (Integer index : ob) {
/*  94 */         if (_handleTypePropertyValue(p, ctxt, propName, bean, typeId, index
/*  95 */             .intValue())) {
/*  96 */           result = true;
/*     */         }
/*     */       } 
/*  99 */       return result;
/*     */     } 
/* 101 */     return _handleTypePropertyValue(p, ctxt, propName, bean, typeId, ((Integer)ob)
/* 102 */         .intValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean _handleTypePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean, String typeId, int index) throws IOException {
/* 109 */     ExtTypedProperty prop = this._properties[index];
/* 110 */     if (!prop.hasTypePropertyName(propName)) {
/* 111 */       return false;
/*     */     }
/*     */     
/* 114 */     boolean canDeserialize = (bean != null && this._tokens[index] != null);
/*     */     
/* 116 */     if (canDeserialize) {
/* 117 */       _deserializeAndSet(p, ctxt, bean, index, typeId);
/*     */       
/* 119 */       this._tokens[index] = null;
/*     */     } else {
/* 121 */       this._typeIds[index] = typeId;
/*     */     } 
/* 123 */     return true;
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
/*     */   public boolean handlePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean) throws IOException {
/*     */     boolean canDeserialize;
/* 138 */     Object ob = this._nameToPropertyIndex.get(propName);
/* 139 */     if (ob == null) {
/* 140 */       return false;
/*     */     }
/*     */     
/* 143 */     if (ob instanceof List) {
/* 144 */       Iterator<Integer> it = ((List<Integer>)ob).iterator();
/* 145 */       Integer integer = it.next();
/*     */       
/* 147 */       ExtTypedProperty extTypedProperty = this._properties[integer.intValue()];
/*     */ 
/*     */       
/* 150 */       if (extTypedProperty.hasTypePropertyName(propName)) {
/* 151 */         String typeId = p.getText();
/* 152 */         p.skipChildren();
/* 153 */         this._typeIds[integer.intValue()] = typeId;
/* 154 */         while (it.hasNext()) {
/* 155 */           this._typeIds[((Integer)it.next()).intValue()] = typeId;
/*     */         }
/*     */       } else {
/* 158 */         TokenBuffer tokens = ctxt.bufferAsCopyOfValue(p);
/* 159 */         this._tokens[integer.intValue()] = tokens;
/* 160 */         while (it.hasNext()) {
/* 161 */           this._tokens[((Integer)it.next()).intValue()] = tokens;
/*     */         }
/*     */       } 
/* 164 */       return true;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 169 */     int index = ((Integer)ob).intValue();
/* 170 */     ExtTypedProperty prop = this._properties[index];
/*     */     
/* 172 */     if (prop.hasTypePropertyName(propName)) {
/*     */ 
/*     */       
/* 175 */       this._typeIds[index] = p.getValueAsString();
/* 176 */       p.skipChildren();
/* 177 */       canDeserialize = (bean != null && this._tokens[index] != null);
/*     */     } else {
/*     */       
/* 180 */       TokenBuffer tokens = ctxt.bufferAsCopyOfValue(p);
/* 181 */       this._tokens[index] = tokens;
/* 182 */       canDeserialize = (bean != null && this._typeIds[index] != null);
/*     */     } 
/*     */ 
/*     */     
/* 186 */     if (canDeserialize) {
/* 187 */       String typeId = this._typeIds[index];
/*     */       
/* 189 */       this._typeIds[index] = null;
/* 190 */       _deserializeAndSet(p, ctxt, bean, index, typeId);
/* 191 */       this._tokens[index] = null;
/*     */     } 
/* 193 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object complete(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/* 204 */     for (int i = 0, len = this._properties.length; i < len; i++) {
/* 205 */       String typeId = this._typeIds[i];
/* 206 */       ExtTypedProperty extProp = this._properties[i];
/* 207 */       if (typeId == null) {
/* 208 */         TokenBuffer tokens = this._tokens[i];
/*     */ 
/*     */         
/* 211 */         if (tokens == null) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */         
/* 216 */         JsonToken t = tokens.firstToken();
/* 217 */         if (t.isScalarValue()) {
/* 218 */           JsonParser buffered = tokens.asParser(p);
/* 219 */           buffered.nextToken();
/* 220 */           SettableBeanProperty prop = extProp.getProperty();
/* 221 */           Object result = TypeDeserializer.deserializeIfNatural(buffered, ctxt, prop.getType());
/* 222 */           if (result != null) {
/* 223 */             prop.set(bean, result);
/*     */             
/*     */             continue;
/*     */           } 
/*     */         } 
/* 228 */         if (!extProp.hasDefaultType()) {
/* 229 */           ctxt.reportPropertyInputMismatch(this._beanType, extProp.getProperty().getName(), "Missing external type id property '%s' (and no 'defaultImpl' specified)", new Object[] { extProp
/*     */                 
/* 231 */                 .getTypePropertyName() });
/*     */         } else {
/* 233 */           typeId = extProp.getDefaultTypeId();
/* 234 */           if (typeId == null) {
/* 235 */             ctxt.reportPropertyInputMismatch(this._beanType, extProp.getProperty().getName(), "Invalid default type id for property '%s': `null` returned by TypeIdResolver", new Object[] { extProp
/*     */                   
/* 237 */                   .getTypePropertyName() });
/*     */           }
/*     */         } 
/* 240 */       } else if (this._tokens[i] == null) {
/* 241 */         SettableBeanProperty prop = extProp.getProperty();
/*     */         
/* 243 */         if (prop.isRequired() || ctxt
/* 244 */           .isEnabled(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)) {
/* 245 */           ctxt.reportPropertyInputMismatch(bean.getClass(), prop.getName(), "Missing property '%s' for external type id '%s'", new Object[] { prop
/*     */                 
/* 247 */                 .getName(), extProp.getTypePropertyName() });
/*     */         }
/* 249 */         return bean;
/*     */       } 
/* 251 */       _deserializeAndSet(p, ctxt, bean, i, typeId); continue;
/*     */     } 
/* 253 */     return bean;
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
/*     */   public Object complete(JsonParser p, DeserializationContext ctxt, PropertyValueBuffer buffer, PropertyBasedCreator creator) throws IOException {
/* 265 */     int len = this._properties.length;
/* 266 */     Object[] values = new Object[len];
/* 267 */     for (int i = 0; i < len; i++) {
/* 268 */       String typeId = this._typeIds[i];
/* 269 */       ExtTypedProperty extProp = this._properties[i];
/* 270 */       if (typeId == null) {
/*     */         
/* 272 */         TokenBuffer tb = this._tokens[i];
/* 273 */         if (tb == null || tb
/*     */ 
/*     */           
/* 276 */           .firstToken() == JsonToken.VALUE_NULL) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 282 */         if (!extProp.hasDefaultType()) {
/* 283 */           ctxt.reportPropertyInputMismatch(this._beanType, extProp.getProperty().getName(), "Missing external type id property '%s'", new Object[] { extProp
/*     */                 
/* 285 */                 .getTypePropertyName() });
/*     */         } else {
/* 287 */           typeId = extProp.getDefaultTypeId();
/*     */         } 
/* 289 */       } else if (this._tokens[i] == null) {
/* 290 */         SettableBeanProperty settableBeanProperty = extProp.getProperty();
/* 291 */         if (settableBeanProperty.isRequired() || ctxt
/* 292 */           .isEnabled(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)) {
/* 293 */           ctxt.reportPropertyInputMismatch(this._beanType, settableBeanProperty.getName(), "Missing property '%s' for external type id '%s'", new Object[] { settableBeanProperty
/*     */                 
/* 295 */                 .getName(), this._properties[i].getTypePropertyName() });
/*     */         }
/*     */       } 
/* 298 */       if (this._tokens[i] != null) {
/* 299 */         values[i] = _deserialize(p, ctxt, i, typeId);
/*     */       }
/*     */       
/* 302 */       SettableBeanProperty prop = extProp.getProperty();
/*     */       
/* 304 */       if (prop.getCreatorIndex() >= 0) {
/* 305 */         buffer.assignParameter(prop, values[i]);
/*     */ 
/*     */         
/* 308 */         SettableBeanProperty typeProp = extProp.getTypeProperty();
/*     */         
/* 310 */         if (typeProp != null && typeProp.getCreatorIndex() >= 0) {
/*     */           Object v;
/*     */ 
/*     */           
/* 314 */           if (typeProp.getType().hasRawClass(String.class)) {
/* 315 */             v = typeId;
/*     */           } else {
/* 317 */             TokenBuffer tb = ctxt.bufferForInputBuffering(p);
/* 318 */             tb.writeString(typeId);
/* 319 */             v = typeProp.getValueDeserializer().deserialize(tb.asParserOnFirstToken(), ctxt);
/* 320 */             tb.close();
/*     */           } 
/* 322 */           buffer.assignParameter(typeProp, v);
/*     */         } 
/*     */       }  continue;
/*     */     } 
/* 326 */     Object bean = creator.build(ctxt, buffer);
/*     */     
/* 328 */     for (int j = 0; j < len; j++) {
/* 329 */       SettableBeanProperty prop = this._properties[j].getProperty();
/* 330 */       if (prop.getCreatorIndex() < 0) {
/* 331 */         prop.set(bean, values[j]);
/*     */       }
/*     */     } 
/* 334 */     return bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, int index, String typeId) throws IOException {
/* 341 */     JsonParser p2 = this._tokens[index].asParser(p);
/* 342 */     JsonToken t = p2.nextToken();
/*     */     
/* 344 */     if (t == JsonToken.VALUE_NULL) {
/* 345 */       return null;
/*     */     }
/* 347 */     TokenBuffer merged = ctxt.bufferForInputBuffering(p);
/* 348 */     merged.writeStartArray();
/* 349 */     merged.writeString(typeId);
/* 350 */     merged.copyCurrentStructure(p2);
/* 351 */     merged.writeEndArray();
/*     */ 
/*     */     
/* 354 */     JsonParser mp = merged.asParser(p);
/* 355 */     mp.nextToken();
/* 356 */     return this._properties[index].getProperty().deserialize(mp, ctxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void _deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, int index, String typeId) throws IOException {
/* 364 */     if (typeId == null) {
/* 365 */       ctxt.reportInputMismatch(this._beanType, "Internal error in external Type Id handling: `null` type id passed", new Object[0]);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 370 */     JsonParser p2 = this._tokens[index].asParser(p);
/* 371 */     JsonToken t = p2.nextToken();
/*     */     
/* 373 */     if (t == JsonToken.VALUE_NULL) {
/* 374 */       this._properties[index].getProperty().set(bean, null);
/*     */       return;
/*     */     } 
/* 377 */     TokenBuffer merged = ctxt.bufferForInputBuffering(p);
/* 378 */     merged.writeStartArray();
/* 379 */     merged.writeString(typeId);
/*     */     
/* 381 */     merged.copyCurrentStructure(p2);
/* 382 */     merged.writeEndArray();
/*     */     
/* 384 */     JsonParser mp = merged.asParser(p);
/* 385 */     mp.nextToken();
/* 386 */     this._properties[index].getProperty().deserializeAndSet(mp, ctxt, bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     private final JavaType _beanType;
/*     */ 
/*     */ 
/*     */     
/* 399 */     private final List<ExternalTypeHandler.ExtTypedProperty> _properties = new ArrayList<>();
/* 400 */     private final Map<String, Object> _nameToPropertyIndex = new HashMap<>();
/*     */     
/*     */     protected Builder(JavaType t) {
/* 403 */       this._beanType = t;
/*     */     }
/*     */ 
/*     */     
/*     */     public void addExternal(SettableBeanProperty property, TypeDeserializer typeDeser) {
/* 408 */       Integer index = Integer.valueOf(this._properties.size());
/* 409 */       this._properties.add(new ExternalTypeHandler.ExtTypedProperty(property, typeDeser));
/* 410 */       _addPropertyIndex(property.getName(), index);
/* 411 */       _addPropertyIndex(typeDeser.getPropertyName(), index);
/*     */     }
/*     */     
/*     */     private void _addPropertyIndex(String name, Integer index) {
/* 415 */       Object ob = this._nameToPropertyIndex.get(name);
/* 416 */       if (ob == null) {
/* 417 */         this._nameToPropertyIndex.put(name, index);
/* 418 */       } else if (ob instanceof List) {
/*     */         
/* 420 */         List<Object> list = (List<Object>)ob;
/* 421 */         list.add(index);
/*     */       } else {
/* 423 */         List<Object> list = new LinkedList();
/* 424 */         list.add(ob);
/* 425 */         list.add(index);
/* 426 */         this._nameToPropertyIndex.put(name, list);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ExternalTypeHandler build(BeanPropertyMap otherProps) {
/* 439 */       int len = this._properties.size();
/* 440 */       ExternalTypeHandler.ExtTypedProperty[] extProps = new ExternalTypeHandler.ExtTypedProperty[len];
/* 441 */       for (int i = 0; i < len; i++) {
/* 442 */         ExternalTypeHandler.ExtTypedProperty extProp = this._properties.get(i);
/* 443 */         String typePropId = extProp.getTypePropertyName();
/* 444 */         SettableBeanProperty typeProp = otherProps.find(typePropId);
/* 445 */         if (typeProp != null) {
/* 446 */           extProp.linkTypeProperty(typeProp);
/*     */         }
/* 448 */         extProps[i] = extProp;
/*     */       } 
/* 450 */       return new ExternalTypeHandler(this._beanType, extProps, this._nameToPropertyIndex, null, null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ExtTypedProperty
/*     */   {
/*     */     private final SettableBeanProperty _property;
/*     */     
/*     */     private final TypeDeserializer _typeDeserializer;
/*     */     
/*     */     private final String _typePropertyName;
/*     */     
/*     */     private SettableBeanProperty _typeProperty;
/*     */ 
/*     */     
/*     */     public ExtTypedProperty(SettableBeanProperty property, TypeDeserializer typeDeser) {
/* 468 */       this._property = property;
/* 469 */       this._typeDeserializer = typeDeser;
/* 470 */       this._typePropertyName = typeDeser.getPropertyName();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void linkTypeProperty(SettableBeanProperty p) {
/* 477 */       this._typeProperty = p;
/*     */     }
/*     */     
/*     */     public boolean hasTypePropertyName(String n) {
/* 481 */       return n.equals(this._typePropertyName);
/*     */     }
/*     */     
/*     */     public boolean hasDefaultType() {
/* 485 */       return this._typeDeserializer.hasDefaultImpl();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getDefaultTypeId() {
/* 494 */       Class<?> defaultType = this._typeDeserializer.getDefaultImpl();
/* 495 */       if (defaultType == null) {
/* 496 */         return null;
/*     */       }
/* 498 */       return this._typeDeserializer.getTypeIdResolver().idFromValueAndType(null, defaultType);
/*     */     }
/*     */     public String getTypePropertyName() {
/* 501 */       return this._typePropertyName;
/*     */     }
/*     */     public SettableBeanProperty getProperty() {
/* 504 */       return this._property;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public SettableBeanProperty getTypeProperty() {
/* 511 */       return this._typeProperty;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/com/fasterxml/jackson/databind/deser/impl/ExternalTypeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */